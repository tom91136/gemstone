package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.string.*
import scala.collection.immutable.SortedMap
import scala.collection.mutable

/** Maps each operand field of every encoding to the domain of its operand type. */
object AMDGPUOperandFields {
  import AMDGPUDomainRules.*
  import AMDGPUOperandDomains.*
  import GenerateAMDGPUISA.*

  case class Operand(field: String, kind: String, implicitOperand: Boolean)
  case class Encoding(isa: String, format: String, opcode: Int, operands: Vector[Operand])
  case class LayoutField(name: String, width: Int, structural: Boolean)
  case class Layout(isa: String, format: String, fields: Vector[LayoutField])

  /** The fields of one format and the domain id of each; `ignored` and `unmodelled` stand for the two untyped cases. */
  case class Signature(format: String, fields: SortedMap[String, String]) derives CanEqual

  case class Mapping(
      signatures: Vector[Signature],
      encodings: SortedMap[String, Vector[(Int, Int)]],
      narrowed: SortedMap[String, OperandDomain],
      findings: Vector[Finding]
  )

  val Ignored: String    = "ignored"
  val Unmodelled: String = "unmodelled"

  private val OpcodeFields   = Set("OP", "OPX", "OPY")
  private val Accumulator    = "acc[0-9]+".r
  private val VectorRegister = "v[0-9]+".r

  def readEncodings(metadata: ujson.Value): Vector[Encoding] = {
    val strings = metadata("strings").arr.map(_.str)
    val schemas = metadata("schemas").arr.map(_.arr.map { operand =>
      val values = operand.arr
      Operand(
        field = if (values(0).num < 0) "" else strings(values(0).num.toInt),
        kind = strings(values(1).num.toInt),
        implicitOperand = values(2).bool
      )
    }.toVector)
    metadata("encodings").arr.map { encoding =>
      Encoding(
        strings(encoding("isa").num.toInt),
        strings(encoding("format").num.toInt),
        encoding("opcode").num.toInt,
        schemas(encoding("schema").num.toInt)
      )
    }.toVector
  }

  def readLayouts(layouts: ujson.Value): Vector[Layout] = layouts.arr.map { layout =>
    val mask = BigInt(layout("mask").str, 16)
    Layout(
      layout("isa").str,
      layout("format").str,
      layout("fields").arr.map { field =>
        val ranges     = field("ranges").arr
        val covered    = bitMask(ranges)
        val structural = OpcodeFields(field("name").str) || (covered & ~mask) == 0
        val width      = ranges.map(range => range("bits").num.toInt + range("padding").num.toInt).sum
        LayoutField(field("name").str, width, structural)
      }.toVector
    )
  }.toVector

  private def remapped(isa: String, encoding: Encoding): Encoding =
    encoding.copy(operands = encoding.operands.map { operand =>
      val aliased = KindAliases.find(alias => alias.isas(isa) && alias.from == operand.kind)
        .fold(operand)(alias => operand.copy(kind = alias.to))
      FieldRemaps.find(remap =>
        remap.isas(isa) && remap.format == encoding.format && remap.field == aliased.field && remap.kind == aliased.kind
      ).fold(aliased)(remap => aliased.copy(field = remap.toField, kind = remap.toKind))
    })

  def map(isa: String, all: Vector[Encoding], layouts: Vector[Layout], entries: Vector[Entry]): Mapping = {
    val encodings = all.map(remapped(isa, _))
    val domains   = entries.map(entry => entry.name -> entry.domain).toMap
    val formats   = layouts.filter(_.isa == isa).map(layout => layout.format -> layout).toMap
    val operated  = encodings.groupBy(_.format).view.mapValues(
      _.flatMap(_.operands.collect { case o if !o.implicitOperand && o.field.nonEmpty => o.field }).toSet
    ).toMap
    val findings = Vector.newBuilder[Finding]
    val narrowed = mutable.SortedMap.empty[String, OperandDomain]
    def typed(format: String, layout: Option[Layout], field: String, operand: Operand): String =
      domains.get(operand.kind) match {
        case None =>
          findings += Finding(
            isa,
            "operand-kind",
            operand.kind,
            "operand kind has no XML operand type",
            Vector(operand.kind),
            None
          )
          Unmodelled
        case Some(domain) =>
          layout.flatMap(_.fields.find(_.name == field)).fold(operand.kind.toLowerCaseRoot) { width =>
            fit(isa, format, layout.get, width, operand.kind, domain) match {
              case (id, found, extra) =>
                found.foreach(findings += _)
                extra.foreach(narrowed += _)
                id
            }
          }
      }
    def signature(encoding: Encoding): Signature = {
      val layout   = formats.get(encoding.format)
      val operands = encoding.operands.filter(operand => !operand.implicitOperand && operand.field.nonEmpty)
      val known    = layout.fold(Set.empty[String])(_.fields.map(_.name).toSet)
      layout.foreach { _ =>
        operands.filterNot(operand => known(operand.field)).foreach { operand =>
          val subject = s"${encoding.format}.${operand.field}"
          findings += Finding(
            isa,
            "operand-field",
            subject,
            s"operand ${operand.kind} names no field of the format",
            Vector(operand.kind),
            None
          )
        }
      }
      val fields = layout.fold(operands.map(operand => operand.field -> Some(operand))) { value =>
        value.fields.filterNot(_.structural).map(field => field.name -> operands.find(_.field == field.name))
      }
      Signature(
        encoding.format,
        SortedMap.from(fields.map { (name, operand) =>
          name -> operand.fold(if (operated(encoding.format)(name)) Ignored else Unmodelled)(typed(
            encoding.format,
            layout,
            name,
            _
          ))
        })
      )
    }
    val signed     = encodings.map(encoding => encoding -> signature(encoding))
    val signatures = signed.map(_._2).distinct
    val ids        = signatures.zipWithIndex.toMap
    val grouped    = signed.groupMap(_._1.format)(entry => entry._1.opcode -> ids(entry._2))
    val missing    = encodings.map(_.format).distinct.filterNot(formats.contains).sorted.map { format =>
      Finding(
        isa,
        "format-layout",
        format,
        "no layout for the format; only its operand fields are mapped",
        Vector.empty,
        Some("layouts.json omits formats without a single fixed identifier")
      )
    }
    Mapping(
      signatures,
      SortedMap.from(grouped.view.mapValues(_.distinct.sorted).toMap),
      SortedMap.from(narrowed),
      findings.result().distinct ++ missing
    )
  }

  /** The domain id for a field: the type itself, or a narrowed immediate when the field is shorter than the type. */
  private def fit(
      isa: String,
      format: String,
      layout: Layout,
      field: LayoutField,
      kind: String,
      domain: OperandDomain
  ): (String, Option[Finding], Option[(String, OperandDomain)]) = {
    val subject                                             = s"$format.${field.name}"
    def finding(detail: String, resolution: Option[String]) =
      Some(Finding(isa, "field-fit", subject, detail, Vector(kind), resolution))
    flatten(domain) match {
      case Vector(OperandDomain.Immediate(bits, signed, _, _)) if bits > field.width =>
        val id = s"${kind.toLowerCaseRoot}@${field.width}"
        (
          id,
          finding(s"$kind has $bits bits, field has ${field.width}", Some("layout wins: the field holds the low bits")),
          Some(id -> OperandDomain.Immediate(field.width, signed = signed))
        )
      case parts =>
        val limit  = 1L << field.width.min(40)
        val excess = parts.flatMap(_.codes).filter(_._2 >= limit).map(_._1)
        val flag   =
          SdwaFlags.get(field.name).filter(name => format.contains("_SDWA") && layout.fields.exists(_.name == name))
        val resolution =
          if (excess.forall(Accumulator.matches))
            Some("accumulator registers are numbered above the vector file; the extra bit travels outside the field")
          else if (flag.nonEmpty && excess.forall(VectorRegister.matches))
            Some(s"the ${flag.get} flag selects the vector file; the field holds the register index")
          else None
        val top  = parts.flatMap(_.codes).map(_._2).maxOption.getOrElse(0)
        val note = if (excess.isEmpty) None
        else finding(s"$kind has codes up to $top, field has ${field.width} bits", resolution)
        (kind.toLowerCaseRoot, note, None)
    }
  }
}
