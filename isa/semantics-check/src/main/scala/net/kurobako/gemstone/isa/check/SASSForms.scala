package net.kurobako.gemstone.isa.check

import java.util.concurrent.ConcurrentHashMap
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.Liveness
import net.kurobako.gemstone.isa.nvidia.Machine
import net.kurobako.gemstone.isa.sass.Instruction
import net.kurobako.gemstone.isa.sass.InstructionDefinitions
import net.kurobako.gemstone.isa.sass.InstructionSemantics
import net.kurobako.gemstone.isa.sass.Operand
import net.kurobako.gemstone.isa.sass.asm.Parser
import net.kurobako.gemstone.isa.sass.asm.Printer
import net.kurobako.gemstone.isa.sass.asm.Program
import net.kurobako.gemstone.semantics.render.*
import scala.reflect.ClassTag

/** A SASS instruction as the [[Form]] an equivalence check takes, or why it takes none. The definition is the one the
  * instruction's own operands lower to, each register and predicate under its spelling, a 64-bit pair binding both its
  * registers. The cases behind it are those of its mnemonic whose instance has operands of the same kinds.
  *
  * A constant-bank operand reads its own bank at its own offset. The lane checks such forms in
  * [[InstructionSemantics.ConstantBank]], the bank the rendering binds, so the instruction renders and executes with
  * its operand moved there, and its definition names the bank it reads: the number of a bank selects what is read and
  * nothing else.
  */
object SASSForms {
  private val Unit      = raw"U?R\d+".r
  private val Predicate = raw"U?P\d+".r

  /** `instruction` with every constant-bank operand in the bank the lane checks, and the bank they named, or why they
    * name more than one.
    */
  def banked(instruction: Instruction): Either[String, (instruction: Instruction, bank: Option[BigInt])] = {
    val bound                                = BigInt(InstructionSemantics.ConstantBank)
    def banks(operand: Operand): Vec[BigInt] = operand match {
      case Operand.ConstantBank(bank, _) => Vec(bank)
      case Operand.Group(values)         => values.flatMap(banks(_))
      case Operand.Unary(value = value)  => banks(value)
      case Operand.Qualified(value, _)   => banks(value)
      case _                             => Vec.empty
    }
    def moved(operand: Operand): Operand = operand match {
      case Operand.ConstantBank(_, offset)      => Operand.ConstantBank(bound, offset)
      case Operand.Group(values)                => Operand.Group(values.map(moved(_)))
      case unary @ Operand.Unary(value = value) => unary.copy(value = moved(value))
      case Operand.Qualified(value, selectors)  => Operand.Qualified(moved(value), selectors)
      case other                                => other
    }
    instruction.operands.flatMap(banks(_)).distinct match {
      case Vec()                      => Right((instruction, None))
      case Vec(bank) if bank == bound => Right((instruction, Some(bank)))
      case Vec(bank)                  =>
        InstructionDefinitions.decode(
          instruction.mnemonic.raw,
          instruction.modifiers,
          instruction.operands.map(moved(_)),
          instruction.guard
        ).map((_, Some(bank)))
      case several => Left(s"reads the constant banks ${several.mkString(", ")}")
    }
  }

  /** `term` with each constant read in `bank`. */
  private def reading(bank: BigInt)(term: Term): Term = term match {
    case context @ Term.Context(contextType = owner, method = "load", arguments = arguments)
        if owner == Machine.constantContext._1 =>
      context.copy(arguments = Term.Literal(bank.toLong) +: arguments.drop(1).map(reading(bank)(_)))
    case other => other.mapChildren(reading(bank)(_))
  }

  private def reading(definition: Definition, bank: BigInt): Definition = definition.copy(
    requirements = definition.requirements.map(reading(bank)(_)),
    body = definition.body.map(statement => statement.copy(term = reading(bank)(statement.term))),
    results = definition.results.map((name, tpe, value) => (name, tpe, reading(bank)(value)))
  )

  /** The kinds of an instruction's operands, which the forms of a mnemonic differ in. */
  def kinds(instruction: Instruction): Vec[String] = {
    def kind(operand: Operand): String = operand match {
      case Operand.Register(bank, _)               => bank.toString
      case Operand.Integer(_) | Operand.Bits(_, _) => "I"
      case Operand.Symbol(value)                   => value
      case Operand.Group(values)                   => values.map(kind(_)).mkString("(", ",", ")")
      case Operand.Predicate(_, uniform, _)        => if (uniform) "UP" else "P"
      case Operand.SpecialRegister(_)              => "SR"
      case _: Operand.FloatingBits | _: Operand.Floating | _: Operand.FloatingSymbol => "F"
      case Operand.ConstantBank(_, _: Operand.Address)                               => "c[R]"
      case Operand.ConstantBank(_, _)                                                => "c"
      case Operand.Address(base, index, _, _, _, descriptor)                         =>
        Vec(base, index, descriptor).map(_.fold("")(kind(_))).mkString("[", ",", "]")
      case Operand.Unary(value = value) => kind(value)
      case Operand.Qualified(value, _)  => kind(value)
    }
    instruction.operands.map(kind(_))
  }

  private def parsed(text: String): Option[Instruction] = Parser.parseProgram(s"$text;").toOption.flatMap(
    _.statements.collectFirst { case Program.Statement.Operation(operation, _, _) => operation }
  )

  private val lanes = ConcurrentHashMap[(String, String), Vec[(kinds: Vec[String], kase: Case)]]()

  /** The checked cases of `mnemonic` on `target`, each with the kinds of its instance's operands. */
  private def lane(target: String, mnemonic: String): Vec[(kinds: Vec[String], kase: Case)] = lanes.computeIfAbsent(
    (target, mnemonic),
    _ =>
      SASSGenerator.describe(target, _ == mnemonic)
        .flatMap(kase => parsed(kase.instance).filter(_.mnemonic.raw == mnemonic).map(found => (kinds(found), kase)))
  )

  /** The checked cases of the form of `instruction` on `target`. */
  def checked(target: String, instruction: Instruction): Vec[Case] = {
    val own = kinds(instruction)
    lane(target, instruction.mnemonic.raw).collect { case (kinds, kase) if kinds == own => kase }
  }

  /** The units a register or predicate named `name` and of `tpe` is held in. */
  private def units(name: String, tpe: Type): Option[Vec[String]] = name match {
    case Unit()      => Some(Liveness.units(name, (tpe.bits + Form.UnitBits - 1) / Form.UnitBits))
    case Predicate() => Some(Vec(name))
    case _           => None
  }

  private def each[A, B: ClassTag](values: Vec[A])(f: A => Either[String, B]): Either[String, Vec[B]] =
    values.foldLeft[Either[String, Vec[B]]](Right(Vec.empty[B]))((found, value) =>
      found.flatMap(done => f(value).map(done :+ _))
    )

  private val forms = ConcurrentHashMap[(String, String), Either[String, Form]]()

  def of(target: String, instruction: Instruction): Either[String, Form] =
    Printer.print(instruction).map(_.stripSuffix(";")).flatMap(text =>
      forms.computeIfAbsent((target, text), _ => form(target, text, instruction))
    )

  private def form(target: String, text: String, instruction: Instruction): Either[String, Form] = for {
    _ <- Either.cond(
      !kinds(instruction).exists(_.contains("c[R]")),
      (),
      s"$text reads a constant bank at an offset a register holds"
    )
    moved      <- banked(instruction).left.map(reason => s"$text $reason")
    definition <- SASSModel.definition(target, moved.instruction).left
      .map(reason => s"$text has no rendering: ${reason.message}")
    own  = GlobalAccess.plain(moved.bank.fold(definition)(reading(definition, _)))
    kase = Case(
      text,
      Some(target),
      own,
      Lookup.Sass(Map.empty, Vec.empty, SASSModel.Written(0, "", instruction.mnemonic.raw, Vec.empty))
    )
    _          <- Rejection.of(kase).toLeft(())
    parameters <- each(own.parameters) { (name, tpe) =>
      units(name, tpe).map(Form.read(_)).toRight(s"$text binds $name, which is not a register").map(name -> _)
    }
    results <- each(own.results) { (name, tpe, _) =>
      units(name, tpe).toRight(s"$text writes $name, which is not a register").map(name -> _)
    }
    cases = checked(target, instruction)
    _ <- Either.cond(
      cases.nonEmpty,
      (),
      s"$text has no checked case of ${instruction.mnemonic.raw} with operands ${kinds(instruction).mkString(", ")}"
    )
  } yield Form(kase, parameters.toMap, results.toMap, Form.Lane(cases))
}
