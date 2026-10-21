package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.semantics.render.*
import scala.annotation.tailrec
import scala.collection.mutable

/** The staged semantics one SASS instruction lowers to on a target, followed by an `EXIT`, as a definition over the
  * registers it uses: 32-bit registers, truth-valued predicates and, where the lowering joins a register pair into one
  * value, the pair as a 64-bit value named after its first register. `PT` reads as true and writes to it are discarded.
  */
object InstructionSemantics {
  private val Kernel: NonEmpty[String] = "semantics"

  /** The constant bank an instance's constant operands name; it is bound to memory at [[ConstantBase]], so each of its
    * reads stays a load of the bank.
    */
  val ConstantBank: Int = 3

  private[isa] val ConstantBase: Long = 0x7c0000000000L

  /** The bytes of the constant bank. */
  private[isa] val ConstantBytes: Int >= 0 = 0x10000

  /** The steps an instruction lowers to after the kernel's `prologue`, and the register and predicate slots each name
    * stands for: one slot, or the low then the high register of a pair. `rendered` is the steps with each such pair
    * joined into one value, and `naming` names and types what they use.
    */
  // the semantics-check reference runs the lowering the rendered semantics describe
  @SuppressWarnings(Array("baumkuchen:test-only-member"))
  private[isa] case class SASSLowering(
      prologue: Vec[VMIR.Instruction],
      steps: Vec[VMIR.Instruction],
      registerCount: Int >= 0,
      predicateCount: Int >= 0,
      registers: Map[String, Vec[VMIR.Register]],
      predicates: Map[String, VMIR.Predicate],
      rendered: Vec[VMIR.Instruction],
      naming: SlotNaming
  )

  /** Names registers by their spelling, or the operand `operands` binds it to, and constant operands by the field
    * `constants` binds their offset to; `pairs` maps the slot each pair renders as to its name.
    */
  private class Spelled(
      layout: VMRegisters.Layout,
      operands: Map[String, String],
      constants: Map[Long, (name: String, tpe: Type)],
      pairs: Map[VMIR.Register, String]
  ) extends SlotNaming {

    def name(slot: VMIR.Register): Option[String] =
      layout.spelling(slot).map(spelling => operands.getOrElse(spelling, spelling))

    def predicate(slot: VMIR.Predicate): Option[String] =
      VMRegisters.spelling(slot).map(spelling => operands.getOrElse(spelling, spelling))

    def resolve(slot: Slot): Vec[Naming.Name] = (slot match {
      case Slot.Gpr(register) =>
        pairs.get(register).map(Naming.Name.spelt(_, Type.unsigned(64)))
          .orElse(name(register).map(Naming.Name.spelt(_, Type.unsigned(32))))
      case Slot.Pred(predicate) => this.predicate(predicate).map(Naming.Name.spelt(_, Type.Bool))
    }).toVec

    override def constant(slot: VMIR.Predicate): Option[Boolean] =
      if (slot == VMRegisters.Predicate.True.slot) Some(true)
      else if (slot == VMRegisters.Predicate.False.slot) Some(false)
      else None

    override def constantBank(address: Long)
        : Option[(bank: Long, field: Option[(name: String, tpe: Type)], offset: Long)] =
      Option.when(address >= ConstantBase && address < ConstantBase + ConstantBytes) {
        val offset = address - ConstantBase
        // a wide operand reads its words from the operand's offset up
        constants.find((start, _) => offset >= start && offset < start + 16) match {
          case Some((start, field)) => (ConstantBank.toLong, Some(field), offset - start)
          case None                 => (ConstantBank.toLong, None, offset)
        }
      }
  }

  private def parameterBase(sm: Int >= 35): Int >= 0 =
    (if (sm < 70) 0x140 else if (sm < 90) 0x160 else if (sm < 100) 0x210 else 0x380).unsafe[Int >= 0]

  /** The lowered instructions attributed to the SASS instruction at `at`, or `None` when any is predicated, and whether
    * it branches.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def attributed(statements: Vec[VMAssembler.Statement], at: Long)
      : (steps: Option[Vec[VMIR.Instruction]], branches: Boolean) = {
    val found    = mutable.ArrayBuffer.empty[VMIR.Instruction]
    var current  = Option.empty[Long]
    var complete = true
    var branches = false
    statements.foreach {
      case VMAssembler.Statement.Source(address)                                    => current = Some(address)
      case VMAssembler.Statement.Execute(instruction, None) if current.contains(at) =>
        if (complete) found.append(instruction)
      case VMAssembler.Statement.Execute(_, Some(_)) if current.contains(at) => complete = false
      case VMAssembler.Statement.Branch(_, _) if current.contains(at)        => branches = true
      case _                                                                 => ()
    }
    (Option.when(complete)(found.toVec), branches)
  }

  /** Whether steps that accompany branches only select the target or leave control-stack tokens: an indirect branch's
    * comparisons ending in a trap for unknown targets, or a legacy transfer's unwinding. Predicated steps that
    * accompany branches are the alternatives of a dispatch.
    */
  private def transfers(steps: Vec[VMIR.Instruction]): Boolean =
    steps.lastOption.contains(VMIR.Instruction.Trap) || steps.forall {
      case VMIR.Instruction.NoOp | VMIR.Instruction.Convergence(_, _) => true
      case _                                                          => false
    }

  /** Where the instruction sits in the function it lowers in, and the instructions around it that make it lower as it
    * does in a program: the BSYNC a convergence barrier's target follows, the end of a collective, the setup a legacy
    * control-stack transfer pops and the call a return returns to. Targets the explorer spells as `0x20` name the
    * function's final EXIT.
    */
  private def surrounding(sm: Int >= 35, instruction: Instruction): Either[String, (Vec[Instruction], Int)] = {
    def decode(mnemonic: String, modifiers: Vec[Instruction.Modifier], operands: Operand*) =
      InstructionDefinitions.decode(mnemonic, modifiers, Vec.from(operands), None)
    val end = Operand.Integer(0x20)
    for {
      exit   <- decode("EXIT", Vec.empty)
      nop    <- decode("NOP", Vec.empty)
      placed <- instruction.form match {
        case setup: Form.Bssy =>
          setup.operands.headOption.toRight("BSSY has no barrier").flatMap(barrier =>
            decode("BSYNC", Vec.empty, barrier).map(sync => (Vec(instruction, sync, exit), 0))
          )
        case _: Form.Warpsync if instruction.modifiers.contains(Instruction.Modifier.COLLECTIVE) =>
          decode("ENDCOLLECTIVE", Vec.empty).map(close => (Vec(instruction, close, exit), 0))
        case _: Form.Endcollective =>
          decode("WARPSYNC", Vec(Instruction.Modifier.COLLECTIVE, Instruction.Modifier.ALL), end).map(open =>
            (Vec(open, instruction, exit), 1)
          )
        case _: Form.Ret if instruction.operands.nonEmpty =>
          decode("CALL", Vec(Instruction.Modifier.REL, Instruction.Modifier.NOINC), Operand.Integer(0x10))
            .map(call => (Vec(call, instruction, exit), 1))
        case _ =>
          LegacyControlFlow.transferKind(DecodedInstruction(0, instruction, None, None)).filter(_ =>
            Generation.of(sm) != Generation.Modern
          ) match {
            case Some(kind) =>
              val mnemonic = kind match {
                case LegacyControlFlow.Kind.Break           => "PBK"
                case LegacyControlFlow.Kind.Continue        => "PCNT"
                case LegacyControlFlow.Kind.Synchronisation => "SSY"
              }
              decode(mnemonic, Vec.empty, end).map(setup => (Vec(setup, instruction, exit), 1))
            case None if ControlFlow.directTarget(instruction) => Right((Vec(instruction, nop, exit), 0))
            case None                                          => Right((Vec(instruction, exit), 0))
          }
      }
    } yield placed
  }

  /** The steps `instruction` lowers to on `sm` and the slots of the registers and predicates it names, spelt as the
    * keys of `operands`, or why it does not lower.
    */
  private[isa] def lowered(
      sm: Int >= 35,
      instruction: Instruction,
      operands: Map[String, String],
      constants: Map[Long, (name: String, tpe: Type)]
  ): Either[Unrendered, SASSLowering] =
    for {
      placed <- surrounding(sm, instruction).left.map(Unrendered.Rejected(_))
      (instructions, position) = placed
      function                 = DecodedFunction(
        Kernel.raw,
        instructions.zipWithIndex.map((operation, index) =>
          DecodedInstruction((index * 16).unsafe[Int >= 0], operation, None, None)
        )
      )
      base   = parameterBase(sm)
      module = Cubin.Module(
        sm,
        Vec(
          Cubin.Section(0, s".text.$Kernel", 1, 0L, 0, 0, 0, Vec.empty),
          Cubin.Section(1, s".nv.constant0.$Kernel", 1, 2L, base, 0, 0, Vec.fill(base)(0.toByte)),
          Cubin.Section(2, s".nv.constant$ConstantBank.$Kernel", 1, 2L, ConstantBytes, 0, 0, Vec.empty)
        ),
        Vec(Cubin.Kernel(Kernel, 0, base, 0, Vec.empty)),
        Vec.empty
      )
      (statements, registerCount) <- VMCompiler.lowerKernel(
        module,
        module.kernels.head,
        Vec(function),
        Map(Cubin.constantSymbol(2) -> GuestAddress(ConstantBase))
      ).left.map {
        case VMCompiler.Failure.NoProduct(reason)   => Unrendered.Malformed(reason.raw)
        case VMCompiler.Failure.Unsupported(reason) => Unrendered.Rejected(reason.raw)
      }
      found = attributed(statements, position * 16L)
      steps <- found.steps match {
        case steps if found.branches && steps.forall(transfers(_)) => Left(Unrendered.Unstaged(Vec("Branch")))
        case Some(steps) if steps.nonEmpty                         => Right(steps)
        case Some(_)                                               => Left(Unrendered.Unstaged(Vec("NoOp")))
        case None => Left(Unrendered.Rejected(s"$instruction lowers to predicated steps"))
      }
      layout = VMRegisters.Layout.of(module.kernels.head, Vec(function))
    } yield {
      val general = (0 until layout.generalCount + layout.uniformCount).map(index =>
        VMIR.Register(index.unsafe[Int >= 0])
      )
      val predicates      = (0 until VMRegisters.PredicateCount).map(index => VMIR.Predicate(index.unsafe[Int >= 0]))
      val spelt           = Spelled(layout, operands, constants, Map.empty)
      val (pairs, naming) = joined(steps, Spelled(layout, operands, constants, _), candidates(steps, spelt))
      SASSLowering(
        statements.takeWhile {
          case VMAssembler.Statement.Source(_) => false
          case _                               => true
        }.collect { case VMAssembler.Statement.Execute(initial, None) => initial },
        steps,
        registerCount,
        VMRegisters.PredicateCount,
        general.flatMap(slot => spelt.name(slot).map(_ -> Vec(slot))).toMap ++
          pairs.map((low, name) => name -> Vec(low, VMIR.Register((low.raw + 1).unsafe[Int >= 0]))),
        predicates.flatMap(slot => spelt.predicate(slot).map(_ -> slot)).toMap,
        joins(steps, pairs.keySet),
        naming
      )
    }

  /** The pairs that render as one value, by their low register, and the naming that renders them so: each candidate
    * whose registers the steps also read or write one at a time stays two registers.
    */
  @tailrec
  private def joined(
      steps: Vec[VMIR.Instruction],
      spelling: Map[VMIR.Register, String] => Spelled,
      pairs: Map[VMIR.Register, String]
  ): (Map[VMIR.Register, String], Spelled) = {
    val naming  = spelling(pairs.map((low, name) => paired(low) -> name))
    val names   = Names(naming)
    val _       = StepRenderer.describe(joins(steps, pairs.keySet), names)
    val touched = names.touched
    val split   = pairs.filter((low, _) =>
      touched(Slot.Gpr(low)) || touched(Slot.Gpr(VMIR.Register((low.raw + 1).unsafe[Int >= 0])))
    )
    if (split.isEmpty) (pairs, naming) else joined(steps, spelling, pairs -- split.keySet)
  }

  /** The slot a pair whose low register is `low` renders as. */
  private def paired(low: VMIR.Register): VMIR.Register = VMIR.Register((low.raw + (1 << 20)).unsafe[Int >= 0])

  /** The register pairs the steps join into one value or split one value into, by their low register. */
  private def candidates(steps: Vec[VMIR.Instruction], naming: Spelled): Map[VMIR.Register, String] = {
    def pair(low: VMIR.Register, high: VMIR.Register): Option[(VMIR.Register, String)] =
      Option.when(high.raw == low.raw + 1 && naming.name(high).nonEmpty)(naming.name(low)).flatten.map(low -> _)
    steps.flatMap {
      case VMIR.Instruction.MoveCombine(_, Vec(VMIR.Value.Register(low), VMIR.Value.Register(high)), part)
          if part == 32 =>
        pair(low, high)
      case VMIR.Instruction.MoveSplit(Vec(Some(low), Some(high)), _, part) if part == 32 => pair(low, high)
      case _                                                                             => None
    }.toMap
  }

  /** The steps with each join of a pair in `pairs` into one value, and each split of one value into such a pair, as a
    * move of the pair.
    */
  private def joins(steps: Vec[VMIR.Instruction], pairs: Set[VMIR.Register]): Vec[VMIR.Instruction] = steps.map {
    case VMIR.Instruction.MoveCombine(destination, Vec(VMIR.Value.Register(low), VMIR.Value.Register(_)), part)
        if part == 32 && pairs(low) => VMIR.Instruction.Move(destination, VMIR.Value.Register(paired(low)), 64)
    case VMIR.Instruction.MoveSplit(Vec(Some(low), Some(_)), source, part) if part == 32 && pairs(low) =>
      VMIR.Instruction.Move(paired(low), source, 64)
    case other => other
  }

  /** The definition named `mnemonic` that `instruction` lowers to on `sm`, or why there is none. A register or
    * predicate the instruction spells as a key of `operands`, such as `R1` or `P0`, takes its value as its name; other
    * physical registers keep their spelling, and the lowering's own are temporaries. A constant operand at an offset
    * that is a key of `constants` reads the bank at the field its value names, of the type it gives. Operands are
    * declared in `order` and must satisfy `requirements` for the instruction to encode.
    */
  def describe(
      mnemonic: String,
      sm: Int >= 35,
      instruction: Instruction,
      operands: Map[String, String],
      constants: Map[Long, (name: String, tpe: Type)],
      order: Vec[String],
      requirements: Vec[Term]
  ): Either[Unrendered, Definition] =
    lowered(sm, instruction, operands, constants).flatMap { described =>
      StepRenderer.definition(mnemonic, described.rendered, Names(described.naming), order, requirements)
    }
}
