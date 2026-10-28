package net.kurobako.gemstone.codegen

import java.nio.file.Path
import scala.collection.immutable.SortedMap

/** Renders one Scala enum per AMDGPU operand domain, spanning every ISA family that lists the domain. */
object AMDGPUDomainTypes {
  import AMDGPUOperandDomains.*
  import OperandDomain.*

  private val Package = "net.kurobako.gemstone.isa.amdgpu.domain"

  private val Files = Vector(
    "sgpr"    -> "Scalar",
    "ttmp"    -> "TrapTemporary",
    "vgpr"    -> "VectorRegister",
    "accvgpr" -> "Accumulator"
  )

  /** The cases a domain's enum needs: the union of what its ISA families list, with the widest bounds. */
  case class Shape(
      registers: SortedMap[String, Int],
      named: Vector[String],
      numbered: SortedMap[String, Int],
      integers: Option[(Int, Int)],
      floating: Boolean,
      literals: Set[Int],
      immediate: Boolean
  ) {
    def isEmpty: Boolean =
      registers.isEmpty && named.isEmpty && numbered.isEmpty && integers.isEmpty && !floating && literals.isEmpty &&
        !immediate
  }

  def typeName(id: String): String = caseName(id.replace("@", "_at_"))

  /** A case named like its enum would shadow the enum inside its own body, so it is called `Value` instead. */
  private def label(owner: String, name: String): String = {
    val base = caseName(name)
    if (base == owner) "Value" else base
  }

  def caseName(name: String): String = name.split("_").filter(_.nonEmpty).map(_.capitalize).mkString

  /** Domains made of registers, names and constants have a type; partitioned and untyped ones do not. */
  def shapeOf(domains: Vector[OperandDomain]): Option[Shape] = {
    val parts = domains.flatMap(flatten(_))
    Option.when(parts.forall(typed(_)) && parts.nonEmpty) {
      val registers = parts.collect { case Registers(file = file, last = last) => file -> last }
        .groupMapReduce(_._1)(_._2)(_ max _)
      val numbered = parts.collect { case NamedRange(prefix, _, last, _) => prefix -> last }
        .groupMapReduce(_._1)(_._2)(_ max _)
      val integers = parts.collect { case IntegerRange(first, last, _) => Vector(first, last) }.flatten
      Shape(
        SortedMap.from(registers),
        parts.collect { case Named(name, _) => name }.distinct,
        SortedMap.from(numbered),
        Option.when(integers.nonEmpty)(integers.min -> integers.max),
        parts.exists { case FloatSet(_) => true; case _ => false },
        parts.collect { case Literal(bytes, _) => bytes }.toSet,
        parts.exists { case Immediate(_, _, _, _) => true; case _ => false }
      )
    }.filterNot(_.isEmpty)
  }

  private def typed(domain: OperandDomain): Boolean = domain match {
    case Registers(_, _, _, _, _, _) | Named(_, _) | NamedRange(_, _, _, _) | IntegerRange(_, _, _) | FloatSet(_) |
        Literal(_, _) | Immediate(_, _, _, _) => true
    case _ => false
  }

  private def registerCases(owner: String, shape: Shape): Vector[(String, String, String)] =
    Files.collect {
      case (file, register) if shape.registers.contains(file) =>
        val last = shape.registers(file)
        val name = label(owner, file)
        (
          s"  case $name(index: Int in 0 ~ $last)",
          s"    case $owner.$name(index) => Slot.Register(RegisterFile.$register, index.raw)",
          s"    case Slot.Register(RegisterFile.$register, index) if index >= 0 && index <= $last =>\n" +
            s"      Some($owner.$name(index.unsafe[Int in 0 ~ $last]))"
        )
    }

  private def namedCases(owner: String, shape: Shape): Vector[(String, String, String)] = shape.named.map { name =>
    val label = this.label(owner, name)
    (
      s"  case $label",
      s"""    case $owner.$label => Slot.Named("$name")""",
      s"""    case Slot.Named("$name") => Some($owner.$label)"""
    )
  }

  private def numberedCases(owner: String, shape: Shape): Vector[(String, String, String)] =
    shape.numbered.toVector.map {
      case (prefix, last) =>
        val label = this.label(owner, prefix)
        (
          s"  case $label(index: Int in 0 ~ $last)",
          s"""    case $owner.$label(index) => Slot.Numbered("$prefix", index.raw)""",
          s"""    case Slot.Numbered("$prefix", index) if index >= 0 && index <= $last =>\n""" +
            s"      Some($owner.$label(index.unsafe[Int in 0 ~ $last]))"
        )
    }

  private def constantCases(owner: String, shape: Shape): Vector[(String, String, String)] =
    shape.integers.toVector.map { case (low, high) =>
      (
        s"  case Integer(value: Int in $low ~ $high)",
        s"    case $owner.Integer(value) => Slot.Integer(value.raw)",
        s"    case Slot.Integer(value) if value >= $low && value <= $high =>\n" +
          s"      Some($owner.Integer(value.unsafe[Int in $low ~ $high]))"
      )
    } ++ Option.when(shape.floating)((
      "  case Floating(value: Double)",
      s"    case $owner.Floating(value) => Slot.Floating(value)",
      s"    case Slot.Floating(value) => Some($owner.Floating(value))"
    )) ++ shape.literals.toVector.sorted.map { bytes =>
      val label = if (bytes == 4) "Literal" else s"Literal${bytes * 8}"
      (
        s"  case $label",
        s"    case $owner.$label => Slot.Literal($bytes)",
        s"    case Slot.Literal($bytes) => Some($owner.$label)"
      )
    } ++ Option.when(shape.immediate)((
      "  case Immediate(value: BigInt)",
      s"    case $owner.Immediate(value) => Slot.Immediate(value)",
      s"    case Slot.Immediate(value) => Some($owner.Immediate(value))"
    ))

  def render(id: String, shape: Shape): String = {
    val name  = typeName(id)
    val cases = registerCases(name, shape) ++ namedCases(name, shape) ++ numberedCases(name, shape) ++
      constantCases(name, shape)
    Vector(
      GeneratedHeader.scala("from the pinned AMD ISA XML, LLVM tables and MC oracle"),
      s"package $Package",
      "",
      Option.when(shape.registers.nonEmpty || shape.numbered.nonEmpty || shape.integers.nonEmpty)(
        "import net.kurobako.gemstone.constraint.*"
      ).getOrElse(""),
      Option.when(shape.registers.nonEmpty)("import net.kurobako.gemstone.isa.amdgpu.asm.*").getOrElse(""),
      "",
      GeneratedHeader.annotation().trim,
      s"object $name extends Domain[$name] {",
      s"""  val id: String = "$id"""",
      "",
      s"  def of(slot: Slot): Option[$name] = slot match {",
      cases.map(_._3).mkString("\n"),
      "    case _ => None",
      "  }",
      "}",
      "",
      GeneratedHeader.annotation().trim,
      s"enum $name extends DomainValue derives CanEqual {",
      cases.map(_._1).mkString("\n"),
      "  def slot: Slot = this match {",
      cases.map(_._2).mkString("\n"),
      "  }",
      "}",
      ""
    ).mkString("\n")
  }

  def renderIndex(names: Vector[String]): String =
    Vector(
      GeneratedHeader.scala("from the pinned AMD ISA XML, LLVM tables and MC oracle"),
      s"package $Package",
      "",
      GeneratedHeader.annotation().trim,
      "object OperandDomains {",
      "  val all: Vector[Domain[? <: DomainValue]] = Vector(",
      names.map(name => s"    $name").mkString(",\n"),
      "  )",
      "}",
      ""
    ).mkString("\n")

  /** The source file of every typed domain, keyed by its path under `root`, and the ids left untyped. `domains` pairs
    * each domain id with its domain on one ISA family.
    */
  def sources(
      domains: Vector[(String, OperandDomain)],
      root: Path
  ): (files: Vector[(Path, String)], untyped: Vector[String]) = {
    val shapes   = SortedMap.from(domains.groupMap(_._1)(_._2)).toVector.map((id, group) => id -> shapeOf(group))
    val typedIds = shapes.collect { case (id, Some(shape)) => id -> shape }
    val files    = typedIds.map((id, shape) => root.resolve(s"${typeName(id)}.scala") -> render(id, shape)) :+
      (root.resolve("OperandDomains.scala") -> renderIndex(typedIds.map(entry => typeName(entry._1))))
    (files, shapes.collect { case (id, None) => id })
  }
}
