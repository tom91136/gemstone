package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.isa.catalogue.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import upickle.default.*

/** Reads the `OperandTypes` section of an AMD ISA XML document into domains. */
object OperandDomainXML {
  import GenerateAMDGPUISA.*

  case class Value(name: String, code: Int)
  case class Field(name: String, ranges: Vector[BitRange], values: Vector[Value])

  case class TypeDefinition(
      name: String,
      description: String,
      subtypes: Vector[String],
      values: Vector[Value],
      fields: Vector[Field]
  )

  def parse(document: Document): Vector[TypeDefinition] = {
    val nodes = document.getElementsByTagName("OperandType")
    Vector.tabulate(nodes.getLength)(nodes.item(_)).collect { case element: Element => element }.flatMap { node =>
      optionalDirect(node, "OperandTypeName").map { name =>
        val fields = microcodeFields(node).map { field =>
          Field(
            requiredText(field, "FieldName"),
            bitRanges(field).map(range => read[BitRange](range)),
            values(optionalDirect(field, "FieldPredefinedValues"))
          )
        }
        TypeDefinition(
          name.getTextContent.trim.stripPrefix("OPR_"),
          optionalText(node, "Description").getOrElse(""),
          optionalDirect(node, "Subtypes").fold(Vector.empty[Element])(directChildren(_, "Subtype"))
            .map(_.getTextContent.trim.stripPrefix("OPR_")),
          values(optionalDirect(node, "OperandPredefinedValues")),
          fields
        )
      }
    }
  }

  private val BareFields = Set("VALUE", "TARGET")

  private def values(container: Option[Element]): Vector[Value] =
    container.fold(Vector.empty[Element])(directChildren(_, "PredefinedValue")).map { value =>
      Value(requiredText(value, "Name"), requiredText(value, "Value").toInt)
    }

  /** The domain of one operand type: its named values and, for a partitioned format, its fields. */
  def domain(definition: TypeDefinition): OperandDomain = {
    val named = definition.values.map(value => value.name -> value.code)
    val parts = OperandDomain.compress(named) ++ OperandDomain.markers(named)
    OperandDomain.union(parts ++ formatDomain(definition.fields))
  }

  private def formatDomain(fields: Vector[Field]): Vector[OperandDomain] = fields match {
    case Vector()                                                                                    => Vector.empty
    case Vector(field) if BareFields(field.name) && field.values.isEmpty && field.ranges.sizeIs == 1 =>
      Vector(fieldDomain(field))
    case _ => Vector(OperandDomain.Partitioned(fields.map(field =>
        OperandDomain.SubField(field.name, field.ranges, fieldDomain(field))
      )))
  }

  private def fieldDomain(field: Field): OperandDomain = {
    val named = field.values.map(value => value.name -> value.code)
    if (named.isEmpty) OperandDomain.Immediate(field.ranges.foldLeft(0)(_ + _.bits), signed = false)
    else OperandDomain.union(OperandDomain.compress(named))
  }
}
