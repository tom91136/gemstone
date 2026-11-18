package net.kurobako.gemstone.isa.check

import java.util.concurrent.ConcurrentHashMap
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.flow.Liveness
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.semantics.render.*
import scala.reflect.ClassTag
import scala.util.Try

/** An AMDGPU instruction as the [[Form]] an equivalence check takes, or why it takes none. The definition is the one
  * the instruction's own text renders to, each register, span, piece of implicit state and literal under its spelling:
  * a register or span binds the 32-bit units under it and a lane mask its halves, as the live sets name them. The
  * generator must check its mnemonic on the target or on the checked target of the same ISA
  * ([[AMDGPUGenerator.checked]]), and the cases behind it are those of that mnemonic and encoding there, of which the
  * generator keeps one per definition up to names and numbers.
  */
object AMDGPUForms {
  private val Width    = "_(e32|e64)$".r
  private val Variant  = "_(sdwa|dpp8?)$".r
  private val Variants = Set("sdwa", "dpp", "dpp8")
  private val Register = raw"([sva])(\d+)".r
  private val Span     = raw"([sva])\[(\d+):(\d+)\]".r

  private val Placeholder = 0x1234

  /** The mnemonic of `assembly` or of a case's instance, with its sdwa or dpp encoding and without its width. */
  def encoded(assembly: String): String = Width.replaceFirstIn(assembly.trim.takeWhile(!_.isWhitespace), "")

  /** The mnemonic and encoding class the cases of `instruction` go by. */
  def encoded(instruction: Instruction): String =
    encoded(instruction.opcode.raw) +
      instruction.mods.collectFirst { case mod if Variants(mod.name.raw) => s"_${mod.name.raw}" }.getOrElse("")

  private val lanes = ConcurrentHashMap[(String, String), Vec[Case]]()

  /** The targets whose checked cases stand for `target`: itself, and the checked target of its ISA. */
  private def standing(target: Target): Vec[String] = (target.raw +: AMDGPUGenerator.checked(target.raw).toVec).distinct

  /** The cases the generator checks of an encoded mnemonic for `target`: one per definition of its instances on the
    * checked target of the same ISA, up to names and numbers.
    */
  def checked(target: Target, name: String): Vec[Case] = lanes.computeIfAbsent(
    (target.raw, name),
    _ => {
      val on = standing(target).toSet
      AMDGPUGenerator.cases(Set(Variant.replaceFirstIn(name, "")), on).filter(kase => encoded(kase.instance) == name)
    }
  )

  private val described = ConcurrentHashMap[String, Set[String]]()

  /** Whether the generator checks the instances of an encoded mnemonic for `target`. */
  private def covered(target: Target, name: String): Boolean =
    described.computeIfAbsent(target.raw, _ => standing(target).flatMap(AMDGPUGenerator.mnemonics(_)).toSet)(
      Variant.replaceFirstIn(name, "")
    )

  /** Whether the generator checks the mnemonic of `instruction` on `target`, as every form requires: a test that costs
    * no rendering.
    */
  def covered(target: Target, instruction: Instruction): Boolean = covered(target, encoded(instruction))

  private val forms = ConcurrentHashMap[(String, String), Either[String, Form]]()

  def of(target: Target, text: String): Either[String, Form] =
    Parser.parse(text).left.map(_.diagnostic.raw).flatMap(_.statements match {
      case Vec(Program.Statement.Instruction(instruction)) => of(target, instruction)
      case _                                               => Left(s"not a single instruction: $text")
    })

  def of(target: Target, instruction: Instruction): Either[String, Form] =
    forms.computeIfAbsent((target.raw, Printer.render(instruction)), _ => form(target, instruction))

  private def numeric(text: String): Option[BigInt] =
    Try(BigInt(text.stripPrefix("0x"), if (text.startsWith("0x")) 16 else 10)).toOption

  private def named(value: BigInt): Vec[(String, Long)] =
    Vec(s"0x${value.toString(16)}" -> value.toLong, value.toString -> value.toLong).distinct

  private def embedded(text: String): Option[BigInt] =
    Option.when(text.startsWith("lit(") && text.endsWith(")"))(text.slice("lit(".length, text.length - 1))
      .flatMap(numeric)

  private def literals(instruction: Instruction): Map[String, Long] =
    instruction.operands.flatMap {
      case Operand.IntegerLiteral(value, _) => named(value)
      case Operand.Expression(text)         => embedded(text).toVec.flatMap(named)
      case _                                => Vec.empty[(String, Long)]
    }.toMap

  /** The units a register, span or piece of implicit state named `name` and of `tpe` is held in. */
  private def units(name: String, tpe: Type, waveSize: Int): Option[Vec[String]] = name match {
    case Register(_, _) | Span(_, _, _) => Some(Liveness.units(name, (tpe.bits + Form.UnitBits - 1) / Form.UnitBits))
    case state if ImplicitState.names.contains(state) => Some(ImplicitState.units(state, waveSize))
    case _                                            => None
  }

  private def each[A, B: ClassTag](values: Vec[A])(f: A => Either[String, B]): Either[String, Vec[B]] =
    values.foldLeft[Either[String, Vec[B]]](Right(Vec.empty[B]))((found, value) =>
      found.flatMap(done => f(value).map(done :+ _))
    )

  private def form(target: Target, instruction: Instruction): Either[String, Form] = {
    val own      = Printer.render(instruction)
    val waveSize = TargetDefaults.waveSize(target)
    val placed   = instruction.operands.zipWithIndex.collectFirst {
      case (Operand.Expression(text), index) if embedded(text).nonEmpty => index -> embedded(text).get
    }
    // an inline literal expression is spelt as the literal the assembler places after the instruction
    val spelled = placed.fold[Either[String, Instruction]](Right(instruction))((index, _) =>
      Instruction.decode(
        instruction.definition,
        instruction.operands.updated(index, Operand.IntegerLiteral(BigInt(Placeholder), 16)),
        instruction.mods
      ).left.map(reason => s"$own: $reason")
    )
    val values = literals(instruction) ++ placed.map((_, value) => s"0x${Placeholder.toHexString}" -> value.toLong)
    for {
      spelling <- spelled
      text = Printer.render(spelling)
      described <- InstructionSemantics.describe(target, text, Map.empty, Vec.empty).left
        .map(reason => s"$own has no rendering: ${reason.message}")
      definition = GlobalAccess.plain(described)
      kase       = Case(text, Some(target.raw), definition, Lookup.Spellings(Map.empty))
      _          <- Rejection.of(kase).toLeft(())
      parameters <- each(definition.parameters) { (name, tpe) =>
        units(name, tpe, waveSize).map(Form.read(_)).orElse(values.get(name).map(Form.Operand.Immediate(_)))
          .toRight(s"$own binds $name, neither a location nor a literal").map(name -> _)
      }
      results <- each(definition.results) { (name, tpe, _) =>
        units(name, tpe, waveSize).toRight(s"$own writes $name, which is not a location").map(name -> _)
      }
      name = encoded(instruction)
      _ <- Either.cond(covered(target, name), (), s"$own has no checked case of $name on ${target.raw}")
    } yield Form(kase, parameters.toMap, results.toMap, Form.Lane(checked(target, name)))
  }
}
