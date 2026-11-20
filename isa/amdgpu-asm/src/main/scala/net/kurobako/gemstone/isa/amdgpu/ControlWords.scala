package net.kurobako.gemstone.isa.amdgpu

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.amdgpu.asm.Instruction
import net.kurobako.gemstone.isa.amdgpu.asm.InstructionCodec
import net.kurobako.gemstone.isa.amdgpu.asm.Operand
import net.kurobako.gemstone.isa.amdgpu.asm.Program
import net.kurobako.gemstone.isa.amdgpu.asm.RegisterFile
import scala.annotation.tailrec

/** The control words LLVM derives from an instruction sequence: `s_delay_alu` from `AMDGPUInsertDelayAlu` and the
  * hazard `s_nop` from `GCNHazardRecognizer`, both of LLVM 23.1.0. The block is straight-line: it takes no branch and
  * nothing continues into it, so the delay state starts empty and every hazard scan stays inside the block.
  *
  * On GFX11 and later the delay state follows `AMDGPUInsertDelayAlu`: one entry per register unit, holding the cycles
  * its writer still needs and how many instructions of each class have issued since. Hazard `s_nop` covers the rules of
  * `GCNHazardRecognizer` that a straight-line block reaches on its target: the `s_setreg`/`s_getreg` wait states, from
  * GFX8 the wait states of a VMEM instruction reading an SGPR a VALU wrote, a VALU overwriting the data registers of a
  * wide store, and on GFX10 the wait states an FP atomic owes before `s_denorm_mode`. The first three apply to GFX9 and
  * earlier and to CDNA1 to CDNA4 only: LLVM skips them on targets with the no-data-dependency hazard model (GFX10 and
  * later, CDNA5).
  *
  * Write latencies, the delay type (VALU, TRANS, SALU), the wait classes and the tied destination of each instruction
  * come from the generated scheduling model, which records them from LLVM's instruction records. An instruction the
  * model has no record for, and a dual-issue pair (whose components the model records only as the single-issue forms),
  * stop the regeneration rather than borrow a latency.
  *
  * Not covered: the DPP, SDWA and VOPD forms, whose operands differ from the plain form; and the other
  * `GCNHazardRecognizer` checks, such as DivFMas, RWLane, MAI, inline assembly, RFE, the M0 read of movrel and
  * interpolation, the gfx101x bugs, and the `fixHazards` pass.
  */
object ControlWords {
  private enum Kind derives CanEqual { case Other, Valu, Trans, Salu }

  private[amdgpu] enum Hazard derives CanEqual { case Setreg, VmemSgpr, WideStore, FPAtomicToDenormMode }

  private case class Def(unit: String, index: Int)

  private case class Step(
      mnemonic: String,
      statement: Program.Statement,
      kind: Kind,
      reads: Vector[String],
      allReads: Vector[String],
      defs: Vector[Def],
      wideStore: Option[Set[String]],
      latency: Int,
      cycles: Int,
      waitsForValu: Boolean,
      waitsForSgprWrite: Boolean,
      isValu: Boolean,
      isVmem: Boolean,
      fpAtomic: Boolean,
      hwreg: Option[Int]
  )

  private case class Delay(
      valuCycles: Int = 0,
      valuNum: Int = 5,
      transCycles: Int = 0,
      transNum: Int = 4,
      transNumValu: Int = 5,
      saluCycles: Int = 0
  ) {
    def merge(other: Delay): Delay = Delay(
      valuCycles = math.max(valuCycles, other.valuCycles),
      valuNum = math.min(valuNum, other.valuNum),
      transCycles = math.max(transCycles, other.transCycles),
      transNum = math.min(transNum, other.transNum),
      transNumValu = math.min(transNumValu, other.transNumValu),
      saluCycles = math.max(saluCycles, other.saluCycles)
    )

    /** This delay after an instruction of `kind` issues for `cycles`, or `None` once nothing is worth waiting for. */
    def advanced(kind: Kind, cycles: Int): Option[Delay] = {
      val valu      = valuNum + (if (kind == Kind.Valu) 1 else 0)
      val valuKept  = valu < 5 && valuCycles > cycles
      val trans     = transNum + (if (kind == Kind.Trans) 1 else 0)
      val transValu = transNumValu + (if (kind == Kind.Valu) 1 else 0)
      val transKept = trans < 4 && transCycles > cycles
      val saluKept  = saluCycles > cycles
      Option.when(valuKept || transKept || saluKept)(
        Delay(
          valuCycles = if (valuKept) valuCycles - cycles else 0,
          valuNum = if (valuKept) valu else 5,
          transCycles = if (transKept) transCycles - cycles else 0,
          transNum = if (transKept) trans else 4,
          transNumValu = if (transKept) transValu else 5,
          saluCycles = if (saluKept) saluCycles - cycles else 0
        )
      )
    }
  }

  private case class State(
      out: Vector[Program.Statement],
      hazard: Vector[Step],
      delays: Map[String, Delay],
      lastDelayAlu: Option[Int],
      lastSgprFromValu: Option[String],
      fired: Set[Hazard]
  )

  private val Empty = State(Vector.empty, Vector.empty, Map.empty, None, None, Set.empty)

  private val Nop      = "s_nop"
  private val DelayAlu = "s_delay_alu"
  private val Hwreg    = raw"(?:hwreg\()?(\d+)\D".r
  private val DepCtr   = raw"depctr_va_vdst\((\d+)\)".r

  private val Waitcnts = Set(
    "s_waitcnt",
    "s_waitcnt_vscnt",
    "s_waitcnt_vmcnt",
    "s_waitcnt_expcnt",
    "s_waitcnt_lgkmcnt",
    "s_wait_loadcnt",
    "s_wait_loadcnt_dscnt",
    "s_wait_storecnt",
    "s_wait_storecnt_dscnt",
    "s_wait_samplecnt",
    "s_wait_bvhcnt",
    "s_wait_expcnt",
    "s_wait_dscnt",
    "s_wait_kmcnt",
    "s_wait_xcnt",
    "s_wait_idle"
  )

  /** `block` with its `s_delay_alu` and hazard `s_nop` instructions removed, regenerated for `target`. */
  def regenerate(block: Vec[Program.Statement], target: Target): Either[String, Vec[Program.Statement]] =
    simulate(block, target).map(state => Vec.from(state.out))

  private[amdgpu] def hazards(block: Vec[Program.Statement], target: Target): Either[String, Set[Hazard]] =
    simulate(block, target).map(_.fired)

  private def simulate(block: Vec[Program.Statement], target: Target): Either[String, State] =
    for {
      steps <- block.toVector.filterNot(controlWord).traverse(describe(_, target))
      state <- steps.foldLeft[Either[String, State]](Right(Empty)) { case (accumulated, step) =>
        accumulated.flatMap(advance(_, step, target))
      }
    } yield state

  private def controlWord(statement: Program.Statement): Boolean = statement match {
    case Program.Statement.Instruction(value) => value.opcode.raw == Nop || value.opcode.raw == DelayAlu
    case _                                    => false
  }

  private def delayInstruction(immediate: Int): Either[String, Instruction] =
    Instruction
      .decode(DelayAlu, Vec(Operand.IntegerLiteral(BigInt(immediate), 10)))
      .left
      .map(error => s"$DelayAlu $immediate: $error")

  private def nopInstruction(count: Int): Either[String, Instruction] =
    Instruction
      .decode(Nop, Vec(Operand.IntegerLiteral(BigInt(count), 10)))
      .left
      .map(error => s"$Nop $count: $error")

  private def units(operand: Operand): Vector[String] = operand match {
    case Operand.Register(RegisterFile.Scalar, first, last)         => (first to last).map(index => s"s$index").toVector
    case Operand.Register(RegisterFile.VectorRegister, first, last) =>
      (first to last).map(index => s"v$index").toVector
    case Operand.Register(RegisterFile.Accumulator, first, last) =>
      (first to last).map(index => s"a$index").toVector
    case Operand.Register(RegisterFile.TrapTemporary, first, last) =>
      (first to last).map(index => s"ttmp$index").toVector
    case Operand.SpecialRegister("vcc")  => Vector("vcc_lo", "vcc_hi")
    case Operand.SpecialRegister("exec") => Vector("exec_lo", "exec_hi")
    case Operand.SpecialRegister("null") => Vector.empty
    case Operand.SpecialRegister(name)   => Vector(name)
    case Operand.Half(value, _)          => units(value)
    case Operand.Negated(value)          => units(value)
    case Operand.Absolute(value)         => units(value)
    case Operand.SignExtended(value)     => units(value)
    case Operand.RegisterList(values)    => values.toVector.flatMap(units)
    case _                               => Vector.empty
  }

  private def special(kind: OperandKind): Vector[String] = kind match {
    case OperandKind.EXEC                                                    => Vector("exec_lo", "exec_hi")
    case OperandKind.SDST_EXEC                                               => Vector("exec_lo", "exec_hi")
    case OperandKind.VCC                                                     => Vector("vcc_lo", "vcc_hi")
    case OperandKind.SSRC_SPECIAL_SCC                                        => Vector("scc")
    case OperandKind.SREG_M0 | OperandKind.SDST_M0 | OperandKind.SREG_M0_INL => Vector("m0")
    case OperandKind.FLAT_SCRATCH => Vector("flat_scratch_lo", "flat_scratch_hi")
    case _                        => Vector.empty
  }

  private def scalar(unit: String): Boolean =
    unit.length > 1 && unit.startsWith("s") && unit.drop(1).forall(_.isDigit)

  private def vectorRegister(unit: String): Boolean =
    unit.length > 1 && (unit.startsWith("v") || unit.startsWith("a")) && unit.drop(1).forall(_.isDigit)

  private def hwreg(instruction: Instruction): Option[Int] =
    if (instruction.opcode.raw.startsWith("s_setreg") || instruction.opcode.raw.startsWith("s_getreg"))
      instruction.operands.toVector.collectFirst {
        case Operand.Expression(text)         => Hwreg.findFirstMatchIn(text).map(_.group(1).toInt)
        case Operand.IntegerLiteral(value, _) => Some(value.toInt & 63)
      }.flatten
    else None

  private def waitsForValuOperand(instruction: Instruction): Boolean = {
    val opcode = instruction.opcode.raw
    opcode == "s_sendmsg_rtn_b32" || opcode == "s_sendmsg_rtn_b64" ||
    (opcode == "s_waitcnt_depctr" && instruction.operands.toVector.exists {
      case Operand.Expression(text)         => DepCtr.findFirstMatchIn(text).exists(_.group(1) == "0")
      case Operand.IntegerLiteral(value, _) => ((value.toInt >> 12) & 15) == 0
      case _                                => false
    })
  }

  private def kindOf(traits: Set[String]): Kind =
    if (traits("trans")) Kind.Trans
    else if (traits("valu")) Kind.Valu
    else if (traits("salu")) Kind.Salu
    else Kind.Other

  private def describe(statement: Program.Statement, target: Target): Either[String, Step] = statement match {
    case Program.Statement.InstructionPair(_, _) =>
      Left("a dual-issue pair has no scheduling record in the generated model")
    case Program.Statement.Instruction(instruction) =>
      val printed = instruction.operands.size
      val shapes  = instruction.definition.encodings(target).flatMap { encoding =>
        val schema = InstructionCodec.operandSchema(instruction, target, encoding).getOrElse(
          encoding.operands.filterNot(info => info.implicitOperand || info.kind == OperandKind.EXEC)
        )
        val unspelled = encoding.operands.filter(info => info.implicitOperand || info.kind == OperandKind.EXEC)
        Option.when(schema.size == printed)((encoding, schema, unspelled))
      }
      val fitted = shapes.filter((_, schema, _) =>
        schema.zip(instruction.operands).forall((info, operand) => fits(info.kind, operand))
      )
      for {
        shape <- Option.when(fitted.nonEmpty)(fitted).getOrElse(shapes).headOption
          .toRight(s"${instruction.opcode.raw} has no encoding for ${target.name}")
        timing <- Scheduling.timing(target, instruction.opcode.raw, instruction.mods.exists(_.name.raw == "lds"))
          .toRight(s"${instruction.opcode.raw} has no scheduling record for ${target.name}")
      } yield {
        val (_, schema, unspelled) = shape
        val traits                 = timing.traits
        val kind                   = kindOf(traits)
        val operandReads           = schema.zip(instruction.operands).collect {
          case (info, operand) if !info.output => (info, units(operand))
        }.toVector
        val reads = operandReads.collect {
          case (info, values) if info.kind != OperandKind.VCC && info.kind != OperandKind.EXEC => values
        }.flatten
        // the catalogue omits the EXEC read that LLVM declares on every VMEM form
        val vmemExec   = if (traits("vmem")) special(OperandKind.EXEC) else Vector.empty
        val stateReads = operandReads.collect {
          case (info, values) if info.kind == OperandKind.VCC || info.kind == OperandKind.EXEC => values
        }.flatten ++ unspelled.filterNot(_.output).flatMap(info => special(info.kind)) ++ vmemExec
        val explicitDefs = schema.zip(instruction.operands).zipWithIndex.collect {
          case ((info, operand), index) if info.output => (index, units(operand))
        }.toVector
        val tiedReads =
          if (traits("tied") && instruction.opcode.raw != "v_writelane_b32")
            explicitDefs.headOption.toVector.flatMap(_._2)
          else Vector.empty
        val implicitDefs = unspelled.filter(_.output).flatMap(info => special(info.kind))
        val defs         =
          (explicitDefs.flatMap((index, values) => values.map(Def(_, index))) ++
            implicitDefs.zipWithIndex.map((unit, index) => Def(unit, explicitDefs.size + index))).distinctBy(_.unit)
        Step(
          mnemonic = instruction.opcode.raw,
          statement = statement,
          kind = kind,
          reads = (reads ++ tiedReads).distinct,
          allReads = (reads ++ tiedReads ++ stateReads).distinct,
          defs = defs,
          wideStore = store(instruction, schema, instruction.definition.encodings(target).map(_.format), traits),
          latency = timing.latency,
          cycles = 1,
          waitsForValu = traits("waits-valu") || waitsForValuOperand(instruction),
          waitsForSgprWrite = traits("smrd") ||
            (kind == Kind.Salu &&
              (operandReads.exists(_._2.nonEmpty) || stateReads.nonEmpty || implicitDefs.nonEmpty)),
          isValu = kind == Kind.Valu || kind == Kind.Trans,
          isVmem = traits("vmem"),
          fpAtomic = traits("fp-atomic"),
          hwreg = hwreg(instruction)
        )
      }
    case other => Left(s"$other is not an instruction")
  }

  /** The data registers of a store wider than 64 bits that a following VALU may overwrite, as
    * `GCNHazardRecognizer::createsVALUHazard` finds them: every flat store, and a buffer store without an SGPR offset.
    */
  private def store(
      instruction: Instruction,
      schema: Seq[OperandInfo],
      formats: Seq[EncodingFormat],
      traits: Set[String]
  ): Option[Set[String]] =
    if (!traits("store") || !traits("vmem")) None
    else {
      val operands = schema.zip(instruction.operands)
      val data     = operands.collectFirst {
        case (info, operand) if !info.output && info.field.exists(_.isDataOperand) => units(operand)
      }
      val flat    = formats.exists(_.isFlat)
      val buffer  = formats.exists(_.isBuffer)
      val soffset =
        operands.collectFirst { case (info, operand) if info.field.contains(EncodingField.SOFFSET) => operand }
      val offset = soffset.forall {
        case Operand.Register(RegisterFile.Scalar | RegisterFile.TrapTemporary, _, _) => true
        case _                                                                        => false
      }
      data.map(_.toSet).filter(values => values.nonEmpty && (flat || (buffer && offset)))
    }

  /** Whether a decoded operand can fill a schema slot of `kind`: a vector slot takes a vector register, a scalar slot a
    * scalar, trap temporary or named state register, and a generic slot anything. The `VCC` and `EXEC` kinds are named
    * state registers.
    */
  private def fits(kind: OperandKind, operand: Operand): Boolean = {
    def register: Boolean = operand match {
      case Operand.Register(RegisterFile.VectorRegister | RegisterFile.Accumulator, _, _) => true
      case Operand.Half(value, _)                                                         => fits(kind, value)
      case Operand.Negated(value)                                                         => fits(kind, value)
      case Operand.Absolute(value)                                                        => fits(kind, value)
      case Operand.SignExtended(value)                                                    => fits(kind, value)
      case _                                                                              => false
    }
    def scalar: Boolean = operand match {
      case Operand.Register(RegisterFile.Scalar | RegisterFile.TrapTemporary, _, _) => true
      case Operand.SpecialRegister(name)                                            =>
        name == "vcc" || name == "vcc_lo" || name == "vcc_hi" || name == "exec" || name == "exec_lo" ||
        name == "exec_hi" || name == "scc" || name == "m0" || name == "flat_scratch"
      case _ => false
    }
    kind match {
      case OperandKind.VGPR | OperandKind.VGPR_OR_ACCVGPR | OperandKind.VGPR_OR_LDS | OperandKind.SRC_VGPR |
          OperandKind.ACCVGPR | OperandKind.SRC_ACCVGPR =>
        register
      case OperandKind.VCC =>
        operand == Operand.SpecialRegister("vcc") || operand == Operand.SpecialRegister("vcc_lo") ||
        operand == Operand.SpecialRegister("vcc_hi")
      case OperandKind.EXEC | OperandKind.SDST_EXEC =>
        operand == Operand.SpecialRegister("exec") || operand == Operand.SpecialRegister("exec_lo") ||
        operand == Operand.SpecialRegister("exec_hi")
      case OperandKind.SDST_M0 | OperandKind.SREG_M0 | OperandKind.SREG_M0_INL =>
        operand == Operand.SpecialRegister("m0")
      case OperandKind.SDST_NULL =>
        operand == Operand.SpecialRegister("null") || operand == Operand.Identifier("null")
      case OperandKind.SGPR | OperandKind.SREG | OperandKind.SDST | OperandKind.SSRC | OperandKind.SREG_NONULL |
          OperandKind.SREG_NOVCC =>
        scalar
      case _ => true
    }
  }

  private def latencyOf(step: Step, index: Int): Int =
    if (index == 0) step.latency else 1

  private def started(kind: Kind, cycles: Int): Delay = kind match {
    case Kind.Valu  => Delay(valuCycles = cycles, valuNum = 0)
    case Kind.Trans => Delay(transCycles = cycles, transNum = 0, transNumValu = 0)
    case Kind.Salu  => Delay(saluCycles = math.min(cycles, 4))
    case Kind.Other => Delay()
  }

  @tailrec
  private def waitStatesSince(
      emitted: Seq[Step],
      isHazard: Step => Boolean,
      limit: Int,
      expired: Step => Boolean = _ => false,
      waiting: Int = 0
  ): Int =
    emitted.lastOption match {
      case None                                 => Int.MaxValue
      case Some(previous) if isHazard(previous) => waiting
      case Some(previous)                       =>
        val next = waiting + previous.cycles
        if (next >= limit || expired(previous)) Int.MaxValue
        else waitStatesSince(emitted.dropRight(1), isHazard, limit, expired, next)
    }

  private def clearsFpAtomic(step: Step): Boolean = step.isValu || Waitcnts(step.mnemonic)

  private def nopsBefore(state: State, step: Step, target: Target): Map[Hazard, Int] = {
    val byDenormMode =
      if (target.isa.isGFX10 && step.mnemonic == "s_denorm_mode") {
        val owed   = 3
        val atomic = (previous: Step) => previous.isVmem && previous.fpAtomic
        math.max(0, owed - waitStatesSince(state.hazard, atomic, owed, clearsFpAtomic))
      } else 0
    if (target.isa.hasNoDataDepHazard) Map(Hazard.FPAtomicToDenormMode -> byDenormMode)
    else {
      val setreg = if (target.isa.isPreGFX9) 1 else 2
      val byReg  = step.hwreg match {
        case Some(hwreg) if step.mnemonic.startsWith("s_setreg") =>
          val same = (previous: Step) => previous.mnemonic.startsWith("s_setreg") && previous.hwreg.contains(hwreg)
          math.max(0, setreg - waitStatesSince(state.hazard, same, setreg))
        case Some(hwreg) if step.mnemonic.startsWith("s_getreg") =>
          val same = (previous: Step) => previous.mnemonic.startsWith("s_getreg") && previous.hwreg.contains(hwreg)
          math.max(0, setreg - waitStatesSince(state.hazard, same, setreg))
        case _ => 0
      }
      val byStore = if (step.isValu) {
        state.hazard.lastOption.flatMap(_.wideStore).filter(values =>
          step.defs.exists(def_ => values.contains(def_.unit))
        ).fold(0)(_ => 1)
      } else 0
      val byVmem = if (target.isa != ISA.GFX6 && target.isa != ISA.GFX7 && step.isVmem) {
        step.allReads.filterNot(vectorRegister).map { unit =>
          val isValu = (previous: Step) => previous.kind == Kind.Valu && previous.defs.exists(_.unit == unit)
          5 - waitStatesSince(state.hazard, isValu, 5)
        }.maxOption.getOrElse(0)
      } else 0
      Map(
        Hazard.Setreg               -> byReg,
        Hazard.VmemSgpr             -> byVmem,
        Hazard.WideStore            -> byStore,
        Hazard.FPAtomicToDenormMode -> byDenormMode
      )
    }
  }

  private def advanceState(state: State, step: Step, cycles: Int): Map[String, Delay] =
    state.delays.flatMap((unit, delay) => delay.advanced(step.kind, cycles).map(unit -> _))

  private def delayImm(delay: Delay): Int = {
    val trans = if (delay.transNum < 4) 4 + delay.transNum else 0
    val valu  =
      if (delay.valuNum < 5 && delay.valuNum <= delay.transNumValu) {
        if (trans != 0) delay.valuNum << 7 else delay.valuNum
      } else 0
    val salu =
      if (delay.saluCycles > 0) {
        if ((trans | valu) & 0x780) != 0 then 0
        else if (trans | valu) != 0 then (delay.saluCycles + 8) << 7
        else delay.saluCycles + 8
      } else 0
    trans | valu | salu
  }

  /** Emits `s_nop` wait states before `step`, as the hazard recognizer does, and lets each one advance the delay state.
    */
  private def nops(state: State, step: Step, target: Target): Either[String, State] = {
    val rules   = nopsBefore(state, step, target)
    val needed  = rules.values.foldLeft(0)(math.max)
    val maximum = 1 << target.isa.snopBits
    val counts  = Iterator.unfold(needed)(remaining =>
      Option.when(remaining > 0) {
        val count = math.min(remaining, maximum)
        (count, remaining - count)
      }
    ).toVector
    val firing = rules.collect { case (hazard, count) if count > 0 => hazard }
    counts.foldLeft[Either[String, State]](Right(state.copy(fired = state.fired ++ firing))) {
      case (accumulated, count) =>
        accumulated.flatMap { current =>
          for {
            instruction <- nopInstruction(count - 1)
            nop         <- describe(Program.Statement.Instruction(instruction), target)
            emitted = nop.copy(cycles = count)
          } yield current.copy(
            out = current.out :+ emitted.statement,
            hazard = current.hazard :+ emitted,
            delays = advanceState(current, emitted, count)
          )
        }
    }
  }

  /** Emits `s_delay_alu` before `step` and updates the delay state, following `AMDGPUInsertDelayAlu` step for step. */
  private def delays(state: State, step: Step, target: Target): Either[String, State] =
    if (!target.isa.isGFX11OrLater) Right(state)
    else {
      val folded = step.reads.foldLeft((Delay(), state.delays)) { case ((found, pending), unit) =>
        pending.get(unit) match {
          case Some(known) => (found.merge(known), pending - unit)
          case None        => (found, pending)
        }
      }
      val (delay, pending) = folded
      val sgpr             = if (step.kind == Kind.Valu) {
        step.defs.find(definition => scalar(definition.unit)).map(_.unit).orElse(state.lastSgprFromValu)
      } else state.lastSgprFromValu
      val imm = delayImm(delay)
      if (imm == 0) Right(state.copy(delays = pending, lastSgprFromValu = sgpr))
      else if ((imm & 0x780) == 0)
        state.lastDelayAlu.fold(create(state, imm, pending, sgpr))(index =>
          extend(state, index, imm).flatMap {
            case Some(extended) => Right(extended.copy(delays = pending, lastSgprFromValu = sgpr))
            case None           => create(state, imm, pending, sgpr)
          }
        )
      else create(state, imm, pending, sgpr)
    }

  /** `state` with `imm` folded into the `s_delay_alu` at `index`, if the skipped instructions fit in six. */
  private def extend(state: State, index: Int, imm: Int): Either[String, Option[State]] = {
    val between = state.out.drop(index + 1)
    val msb     = between.exists {
      case Program.Statement.Instruction(value) => value.opcode.raw == "s_set_vgpr_msb"
      case _                                    => false
    }
    val skip = if (msb) 6 else between.length
    if (skip >= 6) Right(None)
    else
      state.out.lift(index) match {
        case Some(Program.Statement.Instruction(previous)) =>
          previous.operands.headOption.collect { case Operand.IntegerLiteral(value, _) => value.toInt } match {
            case Some(last) =>
              val combined = last | (imm << 7) | (skip << 4)
              delayInstruction(combined).map(delay =>
                Some(state.copy(
                  out = state.out.updated(index, Program.Statement.Instruction(delay)),
                  lastDelayAlu = None
                ))
              )
            case None => Right(None)
          }
        case _ => Right(None)
      }
  }

  private def create(state: State, imm: Int, pending: Map[String, Delay], sgpr: Option[String])
      : Either[String, State] =
    delayInstruction(imm).map { delay =>
      state.copy(
        out = state.out :+ Program.Statement.Instruction(delay),
        delays = pending,
        lastDelayAlu = Option.when((imm & 0x780) == 0)(state.out.size),
        lastSgprFromValu = sgpr
      )
    }

  private def advance(state: State, step: Step, target: Target): Either[String, State] = for {
    waited <- nops(state, step, target)
    cleared = if (!step.waitsForSgprWrite) waited
    else
      waited.lastSgprFromValu.flatMap(waited.delays.get).fold(waited) { known =>
        waited.copy(
          delays = waited.delays.filter((_, delay) => !(delay.valuNum >= known.valuNum && delay.valuCycles > 0)),
          lastSgprFromValu = None
        )
      }
    before = if (step.waitsForValu) cleared.copy(delays = Map.empty) else cleared
    emitted <- if (step.kind == Kind.Other) Right(before) else delays(before, step, target)
    stored = emitted.copy(
      out = emitted.out :+ step.statement,
      delays = if (step.kind == Kind.Other) emitted.delays
      else
        step.defs.foldLeft(emitted.delays)((known, definition) =>
          known.updated(definition.unit, started(step.kind, latencyOf(step, definition.index)))
        )
    )
  } yield stored.copy(hazard = stored.hazard :+ step, delays = advanceState(stored, step, step.cycles))
}
