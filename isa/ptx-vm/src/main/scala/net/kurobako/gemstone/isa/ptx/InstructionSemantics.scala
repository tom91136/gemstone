package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.semantics.render.*

/** The staged semantics one PTX instruction lowers to, as a definition over the registers the instruction names, each
  * of the type it is declared with; registers the lowering introduces are temporaries.
  */
object InstructionSemantics {
  private val Header =
    """.version 8.5
      |.target sm_90
      |.address_size 64
      |""".stripMargin

  private val Kernel = s"$Header.visible .entry semantics() {\n"

  private def parameters(prefix: String, count: Int): String =
    Vec.tabulate(count)(index => s".param .b32 ${prefix}_$index").mkString("(", ", ", ")")

  /** The type a register declared as `.declared` holds: an integer or float of its width, or a truth value for `.pred`.
    */
  def tpe(declared: String): Option[Type] = declared match {
    case "pred"    => Some(Type.Bool)
    case "f32"     => Some(Type.Float(32))
    case "f64"     => Some(Type.Float(64))
    case s"s$bits" => width(bits).map(Type.signed)
    case s"u$bits" => width(bits).map(Type.unsigned)
    case s"b$bits" => width(bits).map(Type.unsigned)
    case _         => None
  }

  private def width(bits: String): Option[Int in 1 ~ 64] =
    bits.toIntOption.filter(Set(8, 16, 32, 64)).flatMap(_.refine[Int in 1 ~ 64].toOption)

  /** The instructions an instruction lowers to, with each of `registers` declared as its PTX type, placed after it
    * where it is a `label`, or declared as a `.branchtargets` list of one label after it where it is `branchtargets`,
    * as a `.param` where it is `param`, and as a function or call prototype of `r` results and `p` parameters where it
    * is `function(r,p)` or `prototype(r,p)`.
    */
  private[isa] case class PTXLowering(
      steps: Vec[VMIR.Instruction],
      routine: Routine,
      registers: Vec[(name: String, declared: String)]
  ) {
    private val types = registers.flatMap((name, declared) => tpe(declared).map(name -> _)).toMap

    /** The name and type of each register slot the instruction names. */
    val naming: SlotNaming = Declared(routine, types)

    def names: Names = Names(naming)
  }

  private class Declared(routine: Routine, types: Map[String, Type]) extends SlotNaming {
    def resolve(slot: Slot): Vec[Naming.Name] = (slot match {
      case Slot.Gpr(register) =>
        for {
          name <- routine.registerNames.lift(register.raw).map(_.raw)
          tpe  <- types.get(name)
        } yield Naming.Name(name, tpe)
      case Slot.Pred(predicate) =>
        routine.predicateNames.lift(predicate.raw).map(name => Naming.Name(name.raw, Type.Bool))
    }).toVec
  }

  /** `instruction` lowered with each register declared as its PTX type, or why the VM rejects it. */
  private[isa] def lowering(instruction: String, registers: Vec[(name: String, declared: String)])
      : Either[Unrendered, PTXLowering] = {
    val unique                      = registers.distinctBy(_.name)
    def signature(declared: String) = declared match {
      case s"$_($results,$arguments)" =>
        for {
          returned <- results.toIntOption
          taken    <- arguments.toIntOption
        } yield (returned, taken)
      case _ => None
    }
    val functions = unique.collect { case (name, declared @ s"function($_)") => name -> signature(declared) }.collect {
      case (name, Some((results, arguments))) =>
        s".func ${parameters(s"${name}_result", results)} $name ${parameters(s"${name}_argument", arguments)} {\n" +
          "  ret;\n}\n"
    }.mkString
    val prototypes = unique.collect { case (name, declared @ s"prototype($_)") =>
      name -> signature(declared)
    }.collect {
      case (name, Some((results, arguments))) =>
        s"  $name: .callprototype ${parameters("_", results)} _ ${parameters("_", arguments)};\n"
    }.mkString
    val special       = Set("branchtargets", "param")
    val (lists, rest) = unique.filterNot(entry => entry.declared.endsWith(")")).partition(_.declared == "branchtargets")
    val (labels, declared) = rest.partition(_.declared == "label")
    val declarations       = declared.filterNot(entry => special(entry.declared))
      .map((name, declared) => s"  .reg .$declared $name;\n").mkString +
      declared.filter(_.declared == "param").map(param => s"  .param .b32 ${param.name};\n").mkString +
      lists.map(list => s"  ${list.name}: .branchtargets ${list.name}_0;\n").mkString + prototypes
    val targets = (labels.map(_.name) ++ lists.map(list => s"${list.name}_0")).map(label => s"$label:\n").mkString
    VMCompiler.compile(
      s"$Header$functions.visible .entry semantics() {\n$declarations  $instruction\n$targets  ret;\n}\n"
    ).left.map {
      case VMCompiler.SourceError.Compilation(error @ Lowering.Error.Unsupported(_, _)) =>
        Unrendered.Rejected(error.message.raw)
      case other => Unrendered.Malformed(other.message.raw)
    }.flatMap {
      kernel =>
        val routine = kernel.entry.routine
        val program = routine.program
        val steps   = program.instructions.indices.toVec.collect {
          case pc if program.source(Pc(pc.unsafe[Int >= 0])).exists(_ == 0L) =>
            program.instructions(pc).instruction
        }
        Either.cond(steps.nonEmpty, PTXLowering(steps, routine, registers), Unrendered.Unstaged(Vec.empty))
    }
  }

  /** The registers each operand of a tensor instruction holds as a fragment, or `None` for an operand that is not one;
    * `None` for other instructions.
    */
  def fragments(instruction: String): Option[Vec[Option[Int]]] =
    Parser.parse(s"$Kernel  $instruction\n}\n").toOption.flatMap(_.statements.collectFirst {
      case Program.Statement.Entry(body = Some(body)) =>
        body.collectFirst { case Program.Statement.Instruction(value) => value }
    }.flatten).flatMap(found => VMTensorCompiler.fragments(found).orElse(VMWarpgroupCompiler.fragments(found)))

  /** The definition named `mnemonic` that `instruction` lowers to, with each of `registers` declared as its PTX type
    * and a parameter or result in that order, or why there is none.
    */
  def describe(mnemonic: String, instruction: String, registers: Vec[(name: String, declared: String)])
      : Either[Unrendered, Definition] =
    lowering(instruction, registers).flatMap(lowered =>
      StepRenderer.definition(mnemonic, lowered.steps, lowered.names, registers.map(_.name), Vec.empty)
    )
}
