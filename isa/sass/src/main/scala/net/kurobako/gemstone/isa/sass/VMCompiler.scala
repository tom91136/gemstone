package net.kurobako.gemstone.isa.sass

import ModifierSemantics.*
import VMAssembler.*
import VMAssembler.Statement.*
import VMMemoryCompiler.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable.ArrayBuffer
import scala.util.control.NonFatal

/** Compile SASS register operations to the shared NVIDIA execution IR, preserving physical register aliases. */
private object VMCompiler {
  private enum LegacyToken { case Break, Synchronization }
  private def unsupported(detail: String): Nothing = throw IllegalArgumentException(detail)
  private type IntegerWidth = Int where (Between[8, 64] & MultipleOf[8])
  private def number(value: Operand): Long = VMOperand.integer(value)
    .filter(value => value >= Long.MinValue && value <= ((BigInt(1) << 64) - 1))
    .fold(unsupported(s"expected integer operand, found $value"))(_.toLong)
  private def barrierId(value: Operand): Int >= 0 = {
    val raw = number(value)
    if (raw < 0L || raw > Int.MaxValue.toLong) unsupported(s"barrier id is outside Int range: $raw")
    raw.toInt.unsafe[Int >= 0]
  }
  private def register(value: Operand, add: Int = 0): Value =
    if (VMOperand.isZero(value)) Value.Immediate(0L) else destination(value, add)

  private def destination(value: Operand, add: Int = 0): Value.Register = VMOperand.base(value) match {
    case Operand.Register(bank @ (RegisterBank.R | RegisterBank.UR), index) =>
      val limit = if (bank == RegisterBank.UR) 63 else 255
      if (add < 0 || index.raw + add >= limit) unsupported(s"register range exceeds $bank bank: $value + $add")
      Value.Register(s"%$bank${index.raw + add}")
    case _ => unsupported(s"expected writable register, found $value")
  }
  private def predicate(value: Operand): Value.Register = value match {
    case Operand.Predicate(None, _, negated)            => Value.Register(if (negated) "%false" else "%true")
    case Operand.Predicate(Some(index), uniform, false) =>
      Value.Register(s"%${if (uniform) "UP" else "P"}$index")
    case _ => unsupported(s"expected predicate register, found $value")
  }
  private def executionPredicate(value: Operand): ExecutionPredicate = value match {
    case pred: Operand.Predicate => ExecutionPredicate(predicate(pred.copy(negated = false)).name, pred.negated)
    case _                       => unsupported(s"expected execution predicate, found $value")
  }
  private def literal(value: Operand): Value = Value.Immediate(number(value))
  private def shuffle(
      mode: Modifier,
      dst: Value.Register,
      valid: Option[Value.Register],
      src: Value,
      delta: Value,
      clamp: Value,
      mask: Value,
      width: IntegerWidth
  ): Instruction = mode match {
    case Modifier.`DOWN` => ShuffleDown(dst.name, valid.map(_.name), src, delta, clamp, mask, width)
    case Modifier.`UP`   => ShuffleUp(dst.name, valid.map(_.name), src, delta, clamp, mask, width)
    case Modifier.`IDX`  => ShuffleIndex(dst.name, valid.map(_.name), src, delta, clamp, mask, width)
    case Modifier.`BFLY` => ShuffleBfly(dst.name, valid.map(_.name), src, delta, clamp, mask, width)
    case _               => unsupported(s"unsupported SASS shuffle mode $mode")
  }
  def compile(
      module: Cubin.Module,
      kernel: Cubin.Kernel,
      functions: Vec[DecodedFunction],
      globalSymbols: Map[NonEmpty[String], GuestAddress] = Map.empty
  ): Either[NonEmpty[String], VMIR.Kernel] = lower(module, kernel, functions, globalSymbols.keySet).flatMap {
    statements =>
      VMAssembler.compile(
        kernel.name,
        kernel.parameters.map(p => KernelParameter(s"param${p.ordinal}", p.size)),
        statements,
        globalSymbols
      )
  }

  def lower(
      module: Cubin.Module,
      kernel: Cubin.Kernel,
      functions: Vec[DecodedFunction],
      boundSymbols: Set[NonEmpty[String]] = Set.empty
  ): Either[NonEmpty[String], Vec[VMAssembler.Statement]] = try {
    module.sections.filter(s => s.kind == 4 || s.kind == 9).foreach { r =>
      if (r.bytes.size != r.size.raw || r.size.raw % (if (r.kind == 4) 24 else 16) != 0)
        unsupported("invalid cubin relocations")
      if (r.size.raw != 0 && module.sections.lift(r.info).exists(_.name == s".nv.constant0.${kernel.name}"))
        unsupported("relocations in launch parameter constants are not implemented")
    }
    val originalFunction =
      functions.find(_.name == kernel.name).getOrElse(unsupported(s"missing SASS for ${kernel.name}"))
    val function = VMAddressAnalysis.rewrite(module.sm.raw, originalFunction)
    function.instructions.foreach { instruction =>
      if (
        (instruction.operation match {
          case _: SASSInstruction.P2r => true; case _ => false
        }) && instruction.operands.lastOption.flatMap(
          VMOperand.integer(_)
        ).contains(
          BigInt(61440)
        ) && module.sm.raw != 35
      )
        unsupported("P2R condition-code mask 0xf000 is supported only on sm_35")
    }
    if (
      module.sections.exists(s =>
        (s.kind == 4 || s.kind == 9) && s.size.raw != 0 &&
          module.sections.lift(s.info).exists(target =>
            target.index == kernel.section ||
              target.name.startsWith(".nv.global")
          )
      )
    )
      unsupported("relocations in kernel code, constants or globals are not implemented")
    // Convergence uses the shared engine's explicit SASS barrier tokens.
    // Require the compiler's structured form, where BSYNC falls through to
    // the BSSY target; other control-stack operations remain unsupported.
    function.instructions.filter(_.operation match { case _: SASSInstruction.Bssy => true; case _ => false }).foreach {
      setup =>
        if (setup.operands.size != 2 || !VMOperand.isRegister(setup.operands.head, RegisterBank.B))
          unsupported("invalid convergence barrier setup")
        val target = number(setup.operands(1)).toInt
        if (
          target <= setup.address.raw ||
          !function.instructions.exists(i =>
            i.address.raw == target - 16 && (i.operation match {
              case _: SASSInstruction.Bsync => true; case _ => false
            }) && i.operands == Vec(
              setup.operands.head
            )
          )
        )
          unsupported("convergence barrier target must be forward and immediately after its BSYNC")
    }
    // Volta and Turing compiler output discard a cleared barrier before its next setup.
    // Only accept that inactive form; save/restore and clearing a live token need
    // a fuller barrier-register model than the structured convergence lowering.
    function.instructions.filter(i =>
      (i.operation match { case _: SASSInstruction.Bmov => true; case _ => false }) && i.modifiers == Vec(
        Modifier.`32`,
        Modifier.`CLEAR`
      ) && i.operands.headOption.contains(VMOperand.Zero)
    )
      .foreach { clear =>
        if (
          !Set(70, 75)(module.sm.raw) || clear.guard.nonEmpty || clear.operands.size != 2 ||
          !VMOperand.isRegister(clear.operands(1), RegisterBank.B)
        )
          unsupported("unsupported discarded barrier clear")
        if (
          function.instructions.exists(setup =>
            (setup.operation match { case _: SASSInstruction.Bssy => true; case _ => false }) &&
              setup.operands.head == clear.operands(1) && setup.address.raw < clear.address.raw &&
              clear.address.raw < number(setup.operands(1))
          )
        )
          unsupported("discarded barrier clear inside an active convergence region is not implemented")
      }
    // Kepler/Maxwell/Pascal convergence tokens have lexical targets rather than B registers.
    // Validate this structured subset and rendezvous at the target after each .S/SYNC transfer.
    val legacyTokens = ArrayBuffer.empty[(DecodedInstruction, Int, Int)]
    if (module.sm.raw < 70) function.instructions.filter(i =>
      i.operation match { case _: SASSInstruction.Ssy | _: SASSInstruction.Pbk => true; case _ => false }
    ).foreach { i =>
      if (i.operands.size != 1 || i.guard.nonEmpty)
        unsupported("unsupported legacy control-stack setup")
      val target = number(i.operands.head).toInt
      if (target <= i.address.raw || !function.instructions.exists(_.address.raw == target))
        unsupported("legacy convergence target must be a forward instruction")
      val occupied = legacyTokens.filter(_._3 > i.address.raw).map(_._2).toSet
      val slot = (0 until 16).find(!occupied(_)).getOrElse(unsupported("legacy convergence nesting exceeds 16 tokens"))
      legacyTokens += ((i, slot, target))
    }
    val addresses       = function.instructions.map(_.address.raw).toSet
    val sortedAddresses = addresses.toVec.sorted
    val returnBases     = function.instructions.filter(_.operation match {
      case _: SASSInstruction.Lepc => true; case _ => false
    }).map(_.address.raw) ++
      function.instructions.filter(
        _.operation match { case _: SASSInstruction.Call => true; case _ => false }
      ).map(_.address.raw + (if (module.sm.raw < 70) 8
                             else 16)) ++
      function.instructions.filter(_.operation match { case _: SASSInstruction.Cal => true; case _ => false }).map(i =>
        addresses.filter(_ > i.address.raw).minOption.getOrElse(unsupported("call has no return instruction"))
      ) ++ function.instructions.filter(_.operation match {
        case _: SASSInstruction.Pret => true; case _ => false
      }).map(i =>
        number(i.operands.head).toInt
      )
    val instructions = function.instructions.flatMap { instruction =>
      try {
        val setup      = legacyTokens.find(_._1.address.raw == instruction.address.raw)
        val syncSuffix = module.sm.raw < 70 && instruction.modifiers.lastOption.contains(Modifier.S)
        val syncKind   = if (instruction.operation match { case _: SASSInstruction.Brk => true; case _ => false })
          Some(LegacyToken.Break)
        else if ((instruction.operation match { case _: SASSInstruction.Sync => true; case _ => false }) || syncSuffix)
          Some(LegacyToken.Synchronization)
        else None
        val transferToken = syncKind.map { kind =>
          legacyTokens.filter((i, _, target) =>
            (kind match {
              case LegacyToken.Break => i.operation match { case _: SASSInstruction.Pbk => true; case _ => false };
              case LegacyToken.Synchronization =>
                i.operation match { case _: SASSInstruction.Ssy => true; case _ => false }
            }) && i.address.raw < instruction.address.raw && instruction.address.raw < target
          )
            .lastOption.getOrElse(unsupported("legacy control-stack transfer has no enclosing setup"))
        }
        val transfer = transferToken.map(_._3)
        // Some legacy entry points end with RET; EXIT despite having no call
        // sites or return tokens. This terminal entry epilogue ends the launch;
        // it must not consume a device-call return address.
        val entryReturn =
          module.sm.raw < 70 && returnBases.isEmpty && (instruction.operation match {
            case _: SASSInstruction.Ret => true; case _ => false
          }) &&
            instruction.operands.isEmpty && function.instructions.find(_.address.raw > instruction.address.raw).exists(
              next =>
                (next.operation match { case _: SASSInstruction.Exit => true; case _ => false }) && next.guard.isEmpty
            )
        val normalized = setup match {
          case Some((_, slot, target)) =>
            instruction.rewrite(SASSInstruction.BssyRegImm(
              InstructionOperand.Register.from(Operand.Register(
                RegisterBank.B,
                slot.unsafe[Int in 0 ~ 255]
              )).fold(unsupported(_), identity),
              InstructionOperand.Immediate.from(Operand.Integer(target)).fold(unsupported(_), identity),
              pred = instruction.guard
            ))
          case None if syncSuffix        => instruction.rewrite(modifiers = instruction.modifiers.dropRight(1))
          case None if transfer.nonEmpty =>
            instruction.rewrite(SASSInstruction.NopNoOperands(pred = instruction.guard))
          case None if entryReturn => instruction.rewrite(SASSInstruction.ExitNoOperands(pred = instruction.guard))
          case None                => instruction
        }
        val branch =
          transfer.filter(_ =>
            !instruction.guard.exists(g => g.predicate.index.isEmpty && g.predicate.negated)
          ).toVec.flatMap { target =>
            val predication = instruction.guard.map(_ => ExecutionPredicate("%guard", false))
            val unwind = if (instruction.operation match { case _: SASSInstruction.Brk => true; case _ => false }) {
              val setupAddress = transferToken.get._1.address.raw
              legacyTokens.filter((setup, _, end) =>
                setup.address.raw > setupAddress && setup.address.raw < instruction.address.raw && end > instruction.address.raw
              ).reverse.map((_, slot, _) =>
                Execute(Convergence(slot, ConvergenceAction.Break), predication)
              ).toVec
            } else Vec.empty[Statement]
            unwind :+ VMAssembler.Statement.Branch(s"L${target.toHexString}", predication)
          }
        val lowered = lowerInstruction(
          module,
          kernel,
          normalized,
          returnBases,
          sortedAddresses,
          boundSymbols
        ) ++ branch
        val arrivals = legacyTokens.filter(_._3 == instruction.address.raw).reverse.map((_, slot, _) =>
          Execute(Convergence(slot, ConvergenceAction.Synchronize))
        ).toVec
        lowered.take(1) ++ arrivals ++ lowered.drop(1)
      } catch {
        case NonFatal(error) => unsupported(
            s"${kernel.name}+0x${instruction.address.raw.toHexString} (sm_${module.sm.raw}): ${instruction.mnemonic.raw} ${instruction.operands.mkString(", ")}: ${error.getMessage}"
          )
      }
    }
    Right(Vec(
      Execute(SetPredicate(
        "%true",
        Comparison.Equal,
        ConversionType.U32.format,
        Value.Immediate(0L),
        Value.Immediate(0L)
      )),
      Execute(SetPredicate(
        "%false",
        Comparison.NotEqual,
        ConversionType.U32.format,
        Value.Immediate(0L),
        Value.Immediate(0L)
      ))
    ) ++ instructions)
  } catch { case NonFatal(error) => Left(throwables.detail(error)) }

  private def lowerInstruction(
      module: Cubin.Module,
      kernel: Cubin.Kernel,
      instruction: DecodedInstruction,
      returnBases: Vec[Int],
      addresses: Vec[Int],
      boundSymbols: Set[NonEmpty[String]]
  ): Vec[VMAssembler.Statement] =
    if (instruction.guard.exists(g => g.predicate.index.isEmpty && g.predicate.negated))
      Vec(Label(s"L${instruction.address.raw.toHexString}"))
    else InstructionCompiler(module, kernel, instruction, returnBases, addresses, boundSymbols).compile()

  private class InstructionCompiler(
      module: Cubin.Module,
      kernel: Cubin.Kernel,
      val instruction: DecodedInstruction,
      returnBases: Vec[Int],
      addresses: Vec[Int],
      boundSymbols: Set[NonEmpty[String]]
  ) extends VMCompilationContext {
    def sm: Int              = module.sm.raw
    private val writtenFlags = scala.collection.mutable.Set.empty[VMConditionCodes.Flag]
    def readConditionFlag(flag: VMConditionCodes.Flag): Value.Register = {
      val result = temp("q")
      emit(ReadConditionFlag(result.name, flag, true))
      result
    }
    private def writeConditionFlags(): Unit =
      Vec("c", "z", "s", "o", "eq", "lt").zip(Vec(
        VMConditionCodes.Flag.Carry,
        VMConditionCodes.Flag.Zero,
        VMConditionCodes.Flag.Sign,
        VMConditionCodes.Flag.Overflow,
        VMConditionCodes.Flag.Equal,
        VMConditionCodes.Flag.LessThan
      )).foreach((name, flag) => emit(WriteConditionFlag(flag, Value.Predicate(s"%cc_$name"))))
    def setZeroFlag(value: Value): Unit = {
      val zero = temp("q")
      emit(SetPredicate(zero.name, Comparison.Equal, ConversionType.U32.format, value, Value.Immediate(0L)))
      emit(WriteConditionFlag(VMConditionCodes.Flag.Zero, Value.Predicate(zero.name)))
    }
    private def seedCarry(): Unit = {
      val bit = temp()
      emit(Select(
        bit.name,
        Value.Immediate(1L),
        Value.Immediate(0L),
        readConditionFlag(VMConditionCodes.Flag.Carry).name,
        32
      ))
      val discarded = temp()
      emit(IntegerAddWithCarry(discarded.name, Value.Immediate(0xffffffffL), bit, 32, false, true))
    }
    val code: ArrayBuffer[Statement] = ArrayBuffer.empty[VMAssembler.Statement]
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var temporaryCount                     = 0
    def temp(prefix: String = "t"): Value.Register = {
      if (temporaryCount >= 64) unsupported("instruction temporary limit exceeded")
      val result: Value.Register = Value.Register(s"%$prefix$temporaryCount")
      temporaryCount += 1
      result
    }
    def emit(value: Instruction): Unit = {
      value match {
        case WriteConditionFlag(flag, _) => writtenFlags += flag
        case _                           => ()
      }
      code += Execute(value)
    }
    def guarded(value: Instruction, pred: Value.Register, negated: Boolean = false): Unit =
      code += Execute(value, Some(ExecutionPredicate(pred.name, negated)))
    def branch(target: NonEmpty[String], pred: Option[ExecutionPredicate] = None): Unit =
      code += VMAssembler.Statement.Branch(target, pred)
    private def dispatch(target: Value.Register, destinations: Vec[Int]): Unit = {
      destinations.foreach { destination =>
        val matches = temp("q")
        emit(SetPredicate(
          matches.name,
          Comparison.Equal,
          ConversionType.U32.format,
          target,
          Value.Immediate(destination.toLong)
        ))
        branch(s"L${destination.toHexString}", Some(ExecutionPredicate(matches.name, false)))
        temporaryCount -= 1
      }
      emit(Trap)
    }
    def physicalRegister(operand: Operand): Value.Register = destination(operand)
    def pair(text: Operand): Value.Register                = {
      val result = temp("wide")
      emit(MoveCombine(result.name, Vec(register(text), register(text, 1)), 32))
      result
    }
    def store(text: Operand, value: Value, bits: IntegerWidth = 32): Unit =
      if (!VMOperand.isZero(text)) {
        if (bits.raw == 64)
          emit(MoveSplit(Vec(destination(text), destination(text, 1)).map(_.name), value, 32))
        else emit(Move(destination(text).name, value, 32))
      }
    def source(raw: Operand, bits: IntegerWidth = 32): Value = VMOperand.without(raw, OperandSelector.`reuse`) match {
      case value: Operand.Register => if (bits.raw == 64 && !VMOperand.isZero(value)) pair(value) else register(value)
      case Operand.ConstantBank(bank, Operand.Address(Some(base), None, displacement, scale, None, None))
          if scale.raw == 1 && VMOperand.isZero(base) =>
        source(Operand.ConstantBank(bank, Operand.Integer(displacement)), bits)
      case Operand.ConstantBank(bankValue, offset: Operand.Address) =>
        if (!bankValue.isValidInt || bankValue < 0) unsupported("invalid constant bank")
        val bank    = bankValue.toInt
        val section =
          module.sections.find(s => s.name == s".nv.constant$bank.${kernel.name}" || s.name == s".nv.constant$bank")
            .getOrElse(unsupported("unknown dynamic constant bank"))
        if (
          bank == 0 || module.sections.exists(s =>
            (s.kind == 4 || s.kind == 9) && s.info == section.index.raw && s.size.raw != 0
          )
        )
          unsupported("dynamic access to relocated or parameter constants")
        if (offset.index.nonEmpty || offset.mode.nonEmpty || offset.scale.raw != 1 || offset.descriptor.nonEmpty)
          unsupported("unsupported dynamic constant offset")
        val index      = offset.base.getOrElse(unsupported("missing dynamic constant index"))
        val wideOffset = temp("wide")
        emit(Convert(wideOffset.name, source(index), ConversionType.U32, ConversionType.U64, None))
        if (offset.displacement != 0)
          emit(IntegerBinary(
            IntegerOperation.Add,
            wideOffset.name,
            wideOffset,
            literal(Operand.Integer(offset.displacement)),
            64
          ))
        val base = temp("wide")
        emit(Move(base.name, Value.Symbol(s"__sass_const${section.index}"), 64))
        emit(IntegerBinary(IntegerOperation.Add, base.name, base, wideOffset, 64))
        val result = temp(if (bits.raw == 64) "wide" else "t")
        emit(load(Vec(result), base, bits, false, MemoryAccess.Global(false)))
        result
      case Operand.ConstantBank(bankValue, offsetValue) =>
        if (!bankValue.isValidInt) unsupported("constant bank out of range")
        if (!bankValue.isValidInt || bankValue < 0) unsupported("invalid constant bank")
        val bank       = bankValue.toInt
        val wideOffset = number(offsetValue)
        if (wideOffset < 0 || wideOffset > Int.MaxValue) unsupported("constant offset out of range")
        val offset = wideOffset.toInt
        val result = temp(if (bits.raw == 64) "wide" else "t")
        if (
          bank == 0 && offset >= kernel.parameterBase.raw &&
          offset + bits.byteCount.raw <= kernel.parameterBase.raw + kernel.parameterSize.raw
        ) {
          val relative = offset - kernel.parameterBase.raw
          kernel.parameters.find(p =>
            relative >= p.offset.raw && relative + bits.byteCount.raw <= p.offset.raw + p.size.raw
          ) match {
            case Some(p) => emit(LoadParameter(
                Vec(result.name),
                s"param${p.ordinal}",
                (relative - p.offset.raw).refine[Int >= 0].fold(unsupported(_), identity),
                bits,
                false
              ))
            case None =>
              emit(Move(result.name, Value.Immediate(0L), if (bits.raw == 64) 64 else 32))
              (0 until bits.byteCount.raw).foreach { i =>
                kernel.parameters.find(p =>
                  relative + i >= p.offset.raw && relative + i < p.offset.raw + p.size.raw
                ).foreach {
                  p =>
                    val byte = temp();
                    emit(LoadParameter(
                      Vec(byte.name),
                      s"param${p.ordinal}",
                      (relative + i - p.offset.raw).refine[Int >= 0].fold(unsupported(_), identity),
                      8,
                      false
                    ))
                    val chunk = if (bits.raw == 64) {
                      val t = temp("wide");
                      emit(Convert(t.name, byte, ConversionType.U32, ConversionType.U64, None)); t
                    } else byte
                    emit(IntegerBinary(
                      IntegerOperation.ShiftLeft,
                      chunk.name,
                      chunk,
                      Value.Immediate((i * 8).toLong),
                      if (bits.raw == 64) 64 else 32
                    ))
                    emit(IntegerBinary(
                      IntegerOperation.BitwiseOr,
                      result.name,
                      result,
                      chunk,
                      if (bits.raw == 64) 64 else 32
                    ))
                }
              }
          }
        } else if (
          bank == 0 && bits.raw == 32 && {
            val base =
              if (module.sm.raw < 50) 0x28 else if (module.sm.raw < 70) 8 else if (module.sm.raw >= 100) 0x360 else 0
            offset >= base && offset < base + 24 && (offset - base) % 4 == 0
          }
        ) {
          val base =
            if (module.sm.raw < 50) 0x28 else if (module.sm.raw < 70) 8 else if (module.sm.raw >= 100) 0x360 else 0
          val special =
            Vec(
              Value.BlockDim(LaunchGeometry.Axis.X),
              Value.BlockDim(LaunchGeometry.Axis.Y),
              Value.BlockDim(LaunchGeometry.Axis.Z),
              Value.GridDim(LaunchGeometry.Axis.X),
              Value.GridDim(LaunchGeometry.Axis.Y),
              Value.GridDim(LaunchGeometry.Axis.Z)
            )((offset - base) / 4)
          emit(Move(result.name, special, 32))
        } else if (bank == 0 && module.sm.raw < 50 && bits.raw == 32 && offset == 0x20) {
          // Legacy cvta.shared.to.generic uses this c0 field as the shared-space tag.
          emit(Move(result.name, Value.Immediate(VMAddressSpace.SharedBase), 32))
        } else if (bank == 0 && Set(50, 60)(module.sm.raw) && bits.raw == 32 && offset == 0) {
          // CUDA 11.8 cvta.shared uses c0[0] for the low shared-aperture word.
          emit(Move(result.name, Value.Immediate(VMAddressSpace.SharedBase), 32))
        } else if (
          bank == 0 && Set(50, 60)(module.sm.raw) && bits.raw == 32 &&
          (offset == 4 || (module.sm.raw == 60 && Set(0x100, 0x104)(offset)))
        ) {
          // Local addresses occupy the VM's low aperture. Pascal additionally
          // reads the high aperture words for shared and local cvta.
          emit(Move(result.name, Value.Immediate(0L), 32))
        } else if (bank == 0 && module.sm.raw >= 70 && module.sm.raw < 90 && offset == 0x18 && Set(32, 64)(bits.raw)) {
          // Modern cvta.shared.to.generic adds the shared aperture base from c0[0x18:0x20].
          emit(Move(result.name, Value.Immediate(VMAddressSpace.SharedBase), bits))
        } else if (bank == 0 && module.sm.raw >= 70 && module.sm.raw < 90 && offset == 0x1c && bits.raw == 32) {
          emit(Move(result.name, Value.Immediate(0L), 32))
        } else if (
          bank == 0 && Set(
            if (module.sm.raw < 50) 0x44
            else if (module.sm.raw < 70) 0x20 else if (module.sm.raw >= 100) 0x37c else 0x28,
            if (module.sm.raw >= 100) 0x358 else if (module.sm.raw >= 90) 0x208 else 0x118
          )(offset)
        ) {
          // Stack origin and opaque global-memory descriptor. Global accesses use checked VM addresses.
          emit(Move(result.name, Value.Immediate(0L), if (bits.raw == 64) 64 else 32))
        } else {
          val section = module.sections.find(s =>
            s.name == s".nv.constant$bank.${kernel.name}" || s.name == s".nv.constant$bank"
          ).getOrElse(unsupported(s"unknown constant bank $bank offset $offset"))
          if (offset < 0 || offset + bits.byteCount.raw > section.size.raw) unsupported("constant load outside bank")
          module.sections.filter(s => (s.kind == 4 || s.kind == 9) && s.info == section.index.raw).foreach { reloc =>
            val entries = java.nio.ByteBuffer.wrap(reloc.bytes.toArray).order(java.nio.ByteOrder.LITTLE_ENDIAN)
            val width   = if (reloc.kind == 4) 24 else 16
            if (reloc.size.raw % width != 0) unsupported("invalid constant relocation table")
            (0 until reloc.size.raw by width).foreach { index =>
              val address = entries.getLong(index)
              if (address < offset + bits.byteCount.raw && address + 8 > offset)
                unsupported("relocated constant address is not implemented")
            }
          }
          val symbol: NonEmpty[String] = s"__sass_const${section.index}"
          if (boundSymbols(symbol))
            emit(load(
              Vec(result),
              Value.Address(Value.Symbol(symbol), offset.toLong),
              bits,
              false,
              MemoryAccess.Global(false)
            ))
          else {
            // Without a runtime binding, the cubin initializer is the bank's only storage.
            val value = section.bytes.slice(offset, offset + bits.byteCount.raw).zipWithIndex.foldLeft(0L) {
              case (v, (b, i)) =>
                v | ((b.toLong & 255) << (8 * i))
            }
            emit(Move(result.name, Value.Immediate(value), if (bits.raw == 64) 64 else 32))
          }
        }
        result
      case Operand.Unary(inner, negative, complement, false) =>
        val value  = source(inner, bits)
        val result = temp(if (bits.raw == 64) "wide" else "t")
        if (complement) emit(IntegerUnary(IntegerUnaryOperation.BitwiseNot, result.name, value, bits))
        else emit(Move(result.name, value, bits))
        if (negative) emit(IntegerUnary(IntegerUnaryOperation.Negate, result.name, result, bits))
        result
      case value @ (Operand.Integer(_) | Operand.Bits(_, _)) => literal(value)
      case other                                             => unsupported(s"unsupported source $other")
    }
    def doubleSource(raw: Operand): Value = floatingSource(raw, 64)

    private def floatingSource(raw: Operand, width: Int in (32, 64)): Value =
      VMOperand.without(raw, OperandSelector.`reuse`) match {
        case Operand.Integer(value) =>
          if (width.raw == 32) Value.Immediate(java.lang.Float.floatToRawIntBits(value.toFloat).toLong & 0xffffffffL)
          else floatingImmediate(value.toDouble, width)
        case Operand.Floating(value, _, negativeZero) =>
          if (width.raw == 32)
            Value.Immediate(java.lang.Float.floatToRawIntBits(if (negativeZero) -0.0f
            else value.toFloat).toLong & 0xffffffffL)
          else floatingImmediate(if (negativeZero) -0.0 else value.toDouble, width)
        case Operand.FloatingBits(_, encodedWidth, bits) if encodedWidth.raw == width.raw =>
          Value.Immediate(bits.toLong)
        case Operand.FloatingSymbol(name) =>
          val value = name match {
            case FloatingConstant.`INF` | FloatingConstant.`+INF`                            => Double.PositiveInfinity
            case FloatingConstant.`-INF`                                                     => Double.NegativeInfinity
            case FloatingConstant.`QNAN` | FloatingConstant.`+QNAN` | FloatingConstant.`NAN` => Double.NaN
            case FloatingConstant.`-QNAN` | FloatingConstant.`-NAN`                          =>
              java.lang.Double.longBitsToDouble(0xfff8000000000000L)
            case _ => unsupported(s"unsupported floating immediate $name")
          }
          floatingImmediate(value, width)
        case Operand.Unary(inner, negative, false, absolute) =>
          val value  = floatingSource(inner, width)
          val result = temp(if (width.raw == 64) "wide" else "t")
          val sign   = if (width.raw == 64) Long.MinValue else 0x80000000L
          val mask   = if (absolute) ~sign else -1L
          emit(IntegerBinary(IntegerOperation.BitwiseAnd, result.name, value, Value.Immediate(mask), width))
          if (negative) emit(IntegerBinary(
            IntegerOperation.BitwiseXor,
            result.name,
            result,
            Value.Immediate(sign),
            width
          ))
          result
        case other => source(other, width)
      }

    private def floatingImmediate(value: Double, width: Int in (32, 64)): Value =
      if (width.raw == 64) Value.Immediate(java.lang.Double.doubleToRawLongBits(value))
      else Value.Immediate(java.lang.Float.floatToRawIntBits(value.toFloat).toLong & 0xffffffffL)

    def address(
        raw: Operand,
        global: Boolean,
        sharedUniform: Boolean = false,
        sharedAccess: Boolean = false,
        allowGlobalU32UniformBase: Boolean = false
    ): Value = raw match {
      case operand: Operand.Address =>
        val result = temp("wide")
        val mode   = operand.mode
        if (mode.contains(AddressMode.U32)) {
          val base =
            operand.base.getOrElse(unsupported("unsupported .U32 uniform global address: missing general base"))
          val index =
            operand.index.getOrElse(unsupported("unsupported .U32 uniform global address: missing uniform base"))
          if (
            !global || !allowGlobalU32UniformBase || module.sm.raw != 75 || base.bank != RegisterBank.R ||
            index.bank != RegisterBank.UR || index.index >= 63 || operand.displacement != 0
          )
            unsupported("unsupported .U32 uniform global address")
          emit(MoveCombine(result.name, Vec(register(index), register(index, 1)), 32))
          val offset = temp("wide")
          emit(Convert(offset.name, register(base), ConversionType.U32, ConversionType.U64, None))
          emit(IntegerBinary(IntegerOperation.Add, result.name, result, offset, 64))
        } else {
          operand.base match {
            case Some(base) if global && !VMOperand.isZero(base) =>
              emit(MoveCombine(result.name, Vec(register(base), register(base, 1)), 32))
            case Some(base) =>
              emit(Convert(result.name, register(base), ConversionType.U32, ConversionType.U64, None))
            case None if !global => emit(Move(result.name, Value.Immediate(0L), 64))
            case _               => unsupported("absolute global address is unsupported")
          }
          if (!Set(1, 4, 8, 16)(operand.scale.raw)) unsupported("unsupported address scale")
          if (operand.scale.raw != 1)
            emit(IntegerBinary(
              IntegerOperation.ShiftLeft,
              result.name,
              result,
              Value.Immediate(Integer.numberOfTrailingZeros(operand.scale.raw).toLong),
              64
            ))
          operand.index.foreach { index =>
            if (index.bank != RegisterBank.UR) unsupported("only uniform address offsets are supported")
            val offset = temp("wide")
            if (global) {
              if (!mode.contains(AddressMode.`64`) || index.index >= 63)
                unsupported("uniform offset requires a 64-bit global base and an in-range register pair")
              emit(MoveCombine(offset.name, Vec(register(index), register(index, 1)), 32))
            } else {
              val supported = module.sm.raw >= 80 || (module.sm.raw == 75 &&
                (instruction.operation match { case _: SASSInstruction.Lds => true; case _ => false }) &&
                Set(Vec(Modifier.`U`, Modifier.`S8`), Vec(Modifier.`U`, Modifier.`U8`))(instruction.modifiers))
              if (!sharedUniform || !supported || mode.contains(AddressMode.`64`))
                unsupported("uniform offsets on this non-global address form are not implemented")
              emit(Convert(offset.name, register(index), ConversionType.U32, ConversionType.U64, None))
            }
            emit(IntegerBinary(IntegerOperation.Add, result.name, result, offset, 64))
          }
          if (operand.displacement != 0)
            emit(IntegerBinary(
              IntegerOperation.Add,
              result.name,
              result,
              literal(Operand.Integer(operand.displacement)),
              64
            ))
          if (!global && operand.index.nonEmpty) {
            val narrow = temp()
            emit(Convert(narrow.name, result, ConversionType.U64, ConversionType.U32, None))
            emit(Convert(result.name, narrow, ConversionType.U32, ConversionType.U64, None))
          }
        }
        if (sharedAccess)
          emit(IntegerBinary(
            IntegerOperation.BitwiseOr,
            result.name,
            result,
            Value.Immediate(VMAddressSpace.SharedBase),
            64
          ))
        result
      case _ => unsupported(s"unsupported address $raw")
    }
    def floatSource(raw: Operand): Value                   = floatingSource(raw, 32)
    def fselSource(raw: Operand, operandIndex: Int): Value = raw match {
      case Operand.FloatingSymbol(name) if name.isNaN =>
        if (module.sm.raw < 70) unsupported("raw FSEL NaN immediates are only verified for modern cubins")
        if (operandIndex != 2) unsupported("FSEL NaN immediate is only verified in the second source")
        val word = instruction.encodingWord.getOrElse(
          unsupported("FSEL NaN immediate requires raw instruction encoding")
        ).toLong
        if ((word & 0xfffL) != 0x808L) unsupported("FSEL NaN source is not encoded as an immediate")
        val bits = (word >>> 32) & 0xffffffffL
        if ((bits & 0x7f800000L) != 0x7f800000L || (bits & 0x007fffffL) == 0)
          unsupported("FSEL NaN spelling does not match encoded source bits")
        Value.Immediate(bits)
      case other => floatSource(other)
    }
    def flushFloat(value: Value): Value = {
      val magnitude = temp(); val sign = temp(); val tiny = temp("q"); val result = temp()
      emit(IntegerBinary(
        IntegerOperation.BitwiseAnd,
        magnitude.name,
        value,
        Value.Immediate(2147483647L),
        32
      ))
      emit(IntegerBinary(IntegerOperation.BitwiseAnd, sign.name, value, Value.Immediate(2147483648L), 32))
      emit(SetPredicate(
        tiny.name,
        Comparison.LessThan,
        ConversionType.U32.format,
        magnitude,
        Value.Immediate(8388608L)
      ))
      emit(Select(result.name, sign, value, tiny.name, 32))
      result
    }
    def predicateSource(value: Operand): Value.Register = value match {
      case pred: Operand.Predicate if pred.negated =>
        val result = temp("q")
        emit(PredicateUnary(result.name, predicate(pred.copy(negated = false)).name))
        result
      case other => predicate(other)
    }
    def updateIntegerCC(lhs: Value, rhs: Value, result: Value, isSub: Boolean, carry: Option[Value]): Unit = {
      if (isSub)
        emit(SetPredicate("%cc_c", Comparison.GreaterThanOrEqual, ConversionType.U32.format, lhs, rhs))
      else emit(SetPredicate(
        "%cc_c",
        Comparison.NotEqual,
        ConversionType.U32.format,
        carry.getOrElse(unsupported("missing integer carry")),
        Value.Immediate(0L)
      ))
      emit(SetPredicate("%cc_z", Comparison.Equal, ConversionType.U32.format, result, Value.Immediate(0L)))
      emit(SetPredicate(
        "%cc_s",
        Comparison.LessThan,
        ConversionType.S32.format,
        result,
        Value.Immediate(0L)
      ))
      val signL = temp("q"); val signR = temp("q"); val signOut = temp("q")
      emit(SetPredicate(
        signL.name,
        Comparison.LessThan,
        ConversionType.S32.format,
        lhs,
        Value.Immediate(0L)
      ))
      emit(SetPredicate(
        signR.name,
        Comparison.LessThan,
        ConversionType.S32.format,
        rhs,
        Value.Immediate(0L)
      ))
      emit(SetPredicate(
        signOut.name,
        Comparison.LessThan,
        ConversionType.S32.format,
        result,
        Value.Immediate(0L)
      ))
      val operandSignsDiffer = temp("q"); val resultSignDiffers = temp("q")
      emit(PredicateBinary(PredicateOperation.Xor, operandSignsDiffer.name, signL.name, signR.name))
      emit(PredicateBinary(PredicateOperation.Xor, resultSignDiffers.name, signL.name, signOut.name))
      val overflowSignCondition =
        if (isSub) operandSignsDiffer
        else { val same = temp("q"); emit(PredicateUnary(same.name, operandSignsDiffer.name)); same }
      val overflow = temp("q")
      emit(PredicateBinary(
        PredicateOperation.And,
        overflow.name,
        overflowSignCondition.name,
        resultSignDiffers.name
      ))
      emit(PredicateBinary(PredicateOperation.Or, "%cc_o", overflow.name, "%false"))
      emit(PredicateBinary(PredicateOperation.Or, "%cc_eq", "%cc_z", "%false"))
      emit(PredicateUnary("%cc_lt", "%cc_c"))
    }
    private def scoreboardGroup(args: Vec[Operand]): Boolean = args match {
      case Vec(Operand.Group(values)) =>
        values.nonEmpty && values.forall(v => VMOperand.integer(v).exists(n => n >= 0 && n <= 5))
      case _ => false
    }
    private def supportsZeroFlagFSetCC(
        sm: Int,
        operation: SASSInstruction,
        modifiers: Vec[Modifier],
        args: Vec[Operand]
    ): Boolean = {
      def input(value: Operand): Boolean = value match {
        case Operand.Unary(inner, false, false, true) => VMOperand.isRegister(inner)
        case Operand.ConstantBank(bank, offset)       => bank == 2 && VMOperand.integer(offset).exists(_ >= 0)
        case other                                    => VMOperand.isRegister(other)
      }
      sm == 35 && ((operation match { case _: SASSInstruction.Fset => true; case _ => false }) && modifiers == Vec(
        Modifier.`GTU`,
        Modifier.`AND`
      )) && args.size == 4 &&
      args.head == Operand.Qualified(VMOperand.Zero, Vec(OperandSelector.CC)) && input(args(1)) && input(args(2)) &&
      VMOperand.isPredicate(args(3), allowNegated = true)
    }
    def compile(): Vec[VMAssembler.Statement] = {
      val args      = instruction.operands
      val modifiers = instruction.modifiers
      val op        = instruction.mnemonic.raw
      if (modifiers.exists { case Modifier.Vendor(_) => true; case _ => false })
        unsupported("unsupported vendor modifier")
      if (args.exists(VMOperand.hasUnknownDomain(_))) unsupported("unsupported vendor operand domain")
      def negatedSource(raw: Operand): Boolean = VMOperand.negative(raw)
      // ENVY's GK110 encoding model gives plain IMAD U32 x U24 + U32 operand
      // types. Restrict its flags to the observed 24-bit immediate form.
      val ccFlagsSupported = args.headOption.exists(VMOperand.has(_, OperandSelector.`CC`)) && (
        ((instruction.operation match { case _: SASSInstruction.Iadd => true; case _ => false }) && !modifiers.contains(
          Modifier.`X`
        ) && args.size == 3 &&
          !(negatedSource(args(1)) && negatedSource(args(2)))) ||
          ((instruction.operation match {
            case _: SASSInstruction.Iscadd => true; case _ => false
          }) && (modifiers.isEmpty) && args.size == 4 &&
            !negatedSource(args(1)) && !negatedSource(args(2)) && VMOperand.integer(args(3)).nonEmpty &&
            number(args(3)) >= 0 && number(args(3)) < 32) ||
          ((instruction.operation match {
            case _: SASSInstruction.Imad => true; case _ => false
          }) && (modifiers.isEmpty) && args.size == 4 &&
            !args.tail.exists(negatedSource(_)) && VMOperand.integer(args(2)).nonEmpty &&
            number(args(2)) <= 0xffffff) ||
          VMAddressCompiler.updatesCarry(module.sm.raw, instruction.operation, modifiers, args)
      )
      val ccZeroFlagsSupported =
        module.sm.raw == 35 && args.headOption.contains(Operand.Qualified(VMOperand.Zero, Vec(OperandSelector.CC))) && (
          ((instruction.operation match { case _: SASSInstruction.I2i => true; case _ => false }) && (modifiers == Vec(
            Modifier.`U32`,
            Modifier.`U32`
          )) && args.size == 2) ||
            ((instruction.operation match {
              case _: SASSInstruction.Lop32i => true; case _ => false
            }) && (modifiers == Vec(
              Modifier.`AND`,
              Modifier.`X`
            )) && args.size == 3 &&
              VMOperand.integer(args(2)).contains(BigInt(65535))) ||
            supportsZeroFlagFSetCC(module.sm.raw, instruction.operation, modifiers, args)
        )
      def requireModifiers(allowed: Set[Modifier]): Unit =
        if (!modifiers.forall(allowed)) unsupported("unsupported modifiers")
      if (
        modifiers.contains(
          Modifier.`X`
        ) && (instruction.operation match {
          case _: SASSInstruction.Iadd | _: SASSInstruction.Iadd32i | _: SASSInstruction.Isub |
              _: SASSInstruction.Iscadd | _: SASSInstruction.Imad => true
          case _ => false
        }) && module.sm.raw < 70
      )
        seedCarry()
      instruction.operation match {
        case _
            if VMFloatingCompiler.compile(this) || VMIntegerCompiler.compile(this) || VMTensorCompiler.compile(
              this
            ) || VMAddressCompiler.compile(this) => ()
        case _: SASSInstruction.Nanosleep if modifiers == Vec(Modifier.`WARP`) =>
          def validSleepDuration(value: Operand): Boolean = value match {
            case Operand.ConstantBank(bank, offset) => bank == 0 && VMOperand.integer(offset).exists(_ >= 0)
            case _ => VMOperand.isRegister(value, allowZero = true) || VMOperand.integer(value).exists(v =>
                v >= 0 && v <= 0xffffffffL
              )
          }
          if (module.sm.raw != 75 || args.size != 1 || !validSleepDuration(args.head) || instruction.guard.nonEmpty)
            unsupported("unsupported NANOSLEEP.WARP form")
        // The PTX sleep is an approximate scheduling hint with a legal zero-delay
        // outcome for every u32 duration. Omitting this observed sm_75 form preserves semantics.
        case _: SASSInstruction.Nop | _: SASSInstruction.Yield | _: SASSInstruction.Nanosleep =>
          requireModifiers(Set.empty)
        case _: SASSInstruction.Bmov =>
          requireModifiers(Set(Modifier.`32`, Modifier.`CLEAR`))
          def barrier(value: Operand): Value.Register =
            if (VMOperand.isRegister(value, RegisterBank.B)) Value.Register(s"%B${VMOperand.registerIndex(value)}")
            else destination(value)
          if (args.head == VMOperand.Zero && modifiers == Vec(Modifier.`32`, Modifier.`CLEAR`)) {
            // lower() proved that no matching structured token is live here.
            emit(Move(barrier(args(1)).name, Value.Immediate(0L), 32))
          } else {
            if (!VMOperand.isRegister(args.head, RegisterBank.B)) unsupported("unsupported barrier move destination")
            emit(Move(
              barrier(args.head).name,
              if (VMOperand.isRegister(args(1), RegisterBank.B)) barrier(args(1)) else source(args(1)),
              32
            ))
            if (modifiers.contains(Modifier.`CLEAR`))
              emit(Move(barrier(args(1)).name, Value.Immediate(0L), 32))
          }
        case _: SASSInstruction.Bssy =>
          requireModifiers(Set.empty)
          if (!VMOperand.isRegister(args.head, RegisterBank.B)) unsupported("invalid convergence barrier register")
          emit(Convergence(VMOperand.registerIndex(args.head).raw, ConvergenceAction.Setup))
        case _: SASSInstruction.Bsync =>
          requireModifiers(Set.empty)
          if (!VMOperand.isRegister(args.head, RegisterBank.B)) unsupported("invalid convergence barrier register")
          emit(Convergence(VMOperand.registerIndex(args.head).raw, ConvergenceAction.Synchronize))
        case _: SASSInstruction.Break =>
          requireModifiers(Set.empty)
          if (!VMOperand.isRegister(args.head, RegisterBank.B)) unsupported("invalid convergence barrier")
          emit(Convergence(VMOperand.registerIndex(args.head).raw, ConvergenceAction.Break))
        case _: SASSInstruction.Warpsync =>
          requireModifiers(Set.empty); emit(WarpBarrier(source(args.head)))
        case _: SASSInstruction.Bar if modifiers.contains(Modifier.`RED`) =>
          requireModifiers(Set(Modifier.`RED`, Modifier.`OR`, Modifier.`AND`, Modifier.`DEFER_BLOCKING`))
          val operation = if (modifiers.contains(Modifier.OR)) PredicateOperation.Or else PredicateOperation.And
          emit(BarrierReduce(
            "%bar_result",
            barrierId(args.head),
            predicateSource(args(1)).name,
            operation
          ))
        case _: SASSInstruction.B2r if modifiers == Vec(Modifier.`RESULT`) =>
          if (args.head != VMOperand.Zero) {
            val t = temp();
            emit(Select(t.name, Value.Immediate(1L), Value.Immediate(0L), "%bar_result", 32));
            store(args.head, t)
          }
          if (args.size == 2 && args(1) != VMOperand.True)
            emit(PredicateBinary(PredicateOperation.Or, predicate(args(1)).name, "%bar_result", "%false"))
        case _: SASSInstruction.Bar =>
          if (modifiers != Vec(Modifier.`SYNC`, Modifier.`DEFER_BLOCKING`) && modifiers != Vec(Modifier.`SYNC`))
            unsupported("unsupported block barrier")
          emit(BarrierSync(barrierId(args.head)))
        case _: SASSInstruction.Cal | _: SASSInstruction.Pret if module.sm.raw < 70 =>
          requireModifiers(Set.empty)
          if (args.size != 1) unsupported("unsupported legacy call operands")
          val target = number(args.head).toInt
          if (!addresses.contains(target)) unsupported("external device call or return address")
          val resume = if (instruction.operation match { case _: SASSInstruction.Cal => true; case _ => false })
            addresses.find(_ > instruction.address.raw).getOrElse(unsupported("call has no return instruction"))
          else target
          emit(SASSReturnPush(resume))
          if (instruction.operation match { case _: SASSInstruction.Cal => true; case _ => false })
            branch(s"L${target.toHexString}")
        case _: SASSInstruction.Call =>
          requireModifiers(Set(Modifier.`REL`, Modifier.`NOINC`))
          if (args.size == 1) {
            val target = number(args.head).toInt
            if (!addresses.contains(target)) unsupported("external device call")
            branch(s"L${target.toHexString}")
          } else if (args.size == 2) {
            val target = temp();
            emit(IntegerBinary(IntegerOperation.Add, target.name, source(args.head), source(args(1)), 32))
            dispatch(target, addresses)
          } else unsupported("unsupported device call operands")
        case _: SASSInstruction.Brx =>
          requireModifiers(Set.empty)
          if (args.size != 2) unsupported("unsupported indirect branch operands")
          val target = temp()
          emit(IntegerBinary(
            IntegerOperation.Add,
            target.name,
            source(args.head),
            Value.Immediate((instruction.address.raw + (if (module.sm.raw < 70) 8 else 16) + number(args(1))).toLong),
            32
          ))
          dispatch(target, addresses)
        case _: SASSInstruction.Lepc =>
          requireModifiers(Set.empty); store(args.head, Value.Immediate(instruction.address.raw.toLong))
        case _: SASSInstruction.Ret if module.sm.raw < 70 && args.isEmpty =>
          requireModifiers(Set.empty)
          val target = temp()
          emit(SASSReturnPop(target.name))
          dispatch(target, returnBases.distinct.filter(addresses.contains))
        case _: SASSInstruction.Ret =>
          requireModifiers(Set(Modifier.`ABS`, Modifier.`REL`, Modifier.`NODEC`))
          if (args.size != 2 || returnBases.isEmpty) unsupported("unsupported return")
          val target = temp();
          emit(IntegerBinary(IntegerOperation.Add, target.name, source(args.head), source(args(1)), 32))
          val targets =
            if (modifiers.contains(Modifier.`REL`)) returnBases else returnBases.map(_ + number(args(1)).toInt)
          dispatch(target, targets.distinct.filter(addresses.contains))
        case _: SASSInstruction.Cs2r =>
          requireModifiers(Set(Modifier.`32`))
          if (args(1) != Operand.SpecialRegister(SpecialRegister.SRZ)) unsupported("unsupported special register")
          store(args.head, Value.Immediate(0L), if (modifiers.contains(Modifier.`32`)) 32 else 64)
        case _: SASSInstruction.Membar if module.sm.raw < 70 && (modifiers == Vec(Modifier.`GL`)) =>
          emit(MemoryBarrier)
        case _: SASSInstruction.Membar =>
          requireModifiers(Set(Modifier.`CTA`, Modifier.`GPU`, Modifier.`SYS`, Modifier.`SC`, Modifier.`ALL`));
          emit(MemoryBarrier)
        case _: SASSInstruction.Bmsk =>
          requireModifiers(Set(Modifier.`W`))
          emit(BitMask(
            destination(args.head).name,
            source(args(1)),
            source(args(2)),
            if (modifiers.contains(Modifier.`W`)) BitMaskMode.Wrap else BitMaskMode.Clamp
          ))
        case _: SASSInstruction.Plop3 =>
          requireModifiers(Set(Modifier.`LUT`))
          if (args.size != 7 || args(1) != VMOperand.True) unsupported("PLOP3 with two predicate destinations")
          val inputs = args.slice(2, 5).map { p =>
            val saved = temp("q");
            emit(PredicateBinary(PredicateOperation.Or, saved.name, predicateSource(p).name, "%false"));
            saved
          }
          val inverses = inputs.map { p =>
            val inverse = temp("q"); emit(PredicateUnary(inverse.name, p.name)); inverse
          }
          val lut = number(args(5)).toInt
          if (lut < 0 || lut > 255) unsupported("invalid predicate LUT")
          val result = temp("q");
          emit(PredicateBinary(PredicateOperation.Or, result.name, "%false", "%false"))
          (0 until 8).filter(bit => (lut & (1 << bit)) != 0).foreach { bit =>
            val term   = temp("q")
            val values = (0 until 3).map(i => if ((bit & (1 << (2 - i))) != 0) inputs(i) else inverses(i))
            emit(PredicateBinary(PredicateOperation.And, term.name, values(0).name, values(1).name));
            emit(PredicateBinary(PredicateOperation.And, term.name, term.name, values(2).name))
            emit(PredicateBinary(PredicateOperation.Or, result.name, result.name, term.name))
          }
          if (args.head != VMOperand.True)
            emit(PredicateBinary(PredicateOperation.Or, predicate(args.head).name, result.name, "%false"))
        case _: SASSInstruction.P2r =>
          requireModifiers(Set(Modifier.`B1`, Modifier.`B2`, Modifier.`B3`))
          val shift = modifiers.headOption.fold(0) {
            case Modifier.B1 => 8
            case Modifier.B2 => 16
            case Modifier.B3 => 24
            case _           => unsupported("unsupported predicate byte selector")
          }
          val mask   = number(args(2)).toInt
          val ccMask = mask == 0xf000
          if (mask < 0 || ((mask & ~0x7f) != 0 && !ccMask)) unsupported("invalid predicate read mask")
          if (ccMask && (module.sm.raw != 35 || shift != 0 || modifiers.nonEmpty))
            unsupported("P2R condition-code mask 0xf000 is supported only as unmodified sm_35 P2R")

          val result = temp();
          emit(IntegerBinary(
            IntegerOperation.BitwiseAnd,
            result.name,
            source(args(1)),
            Value.Immediate((~(mask << shift)).toLong & 0xffffffffL),
            32
          ))
          if (ccMask)
            Vec(
              VMConditionCodes.Flag.Zero,
              VMConditionCodes.Flag.Sign,
              VMConditionCodes.Flag.Carry,
              VMConditionCodes.Flag.Overflow
            ).map(readConditionFlag(_)).zipWithIndex.foreach { case (flag, i) =>
              val bit = temp();
              emit(Select(
                bit.name,
                Value.Immediate((1L << (12 + i)).toLong),
                Value.Immediate(0L),
                flag.name,
                32
              ))
              emit(IntegerBinary(IntegerOperation.BitwiseOr, result.name, result, bit, 32))
            }
          else (0 until 7).filter(i => (mask & (1 << i)) != 0).foreach { i =>
            val bit = temp();
            emit(Select(
              bit.name,
              Value.Immediate((1L << (i + shift)).toLong),
              Value.Immediate(0L),
              s"%P$i",
              32
            ))
            emit(IntegerBinary(IntegerOperation.BitwiseOr, result.name, result, bit, 32))
          }
          store(args.head, result)
        case _: SASSInstruction.Sel | _: SASSInstruction.Fsel | _: SASSInstruction.Usel =>
          requireModifiers(Set.empty)
          val a =
            if (instruction.operation match { case _: SASSInstruction.Fsel => true; case _ => false })
              fselSource(args(1), 1)
            else source(args(1))
          val b =
            if (instruction.operation match { case _: SASSInstruction.Fsel => true; case _ => false })
              fselSource(args(2), 2)
            else source(args(2))
          emit(Select(destination(args.head).name, a, b, predicateSource(args(3)).name, 32))
        case _: SASSInstruction.Fmnmx =>
          requireModifiers(Set.empty)
          if (module.sm.raw < 70 || args.size != 4)
            unsupported("FMNMX is supported only for modern four-operand forms")
          val operation = args(3) match {
            case Operand.Predicate(None, false, false) => FloatOperation.Minimum
            case Operand.Predicate(None, false, true)  => FloatOperation.Maximum
            case _                                     => unsupported("FMNMX selector must be PT or !PT")
          }
          val left    = floatSource(args(1)); val right = floatSource(args(2))
          val leftNan = temp("q"); val rightNan         = temp("q"); val bothNan = temp("q")
          emit(SetPredicate(leftNan.name, Comparison.Unordered, ConversionType.F32.format, left, left))
          emit(SetPredicate(rightNan.name, Comparison.Unordered, ConversionType.F32.format, right, right))
          emit(PredicateBinary(PredicateOperation.And, bothNan.name, leftNan.name, rightNan.name))
          val result = temp()
          emit(FloatBinary(operation, result.name, left, right, 32))
          emit(Select(result.name, left, result, rightNan.name, 32))
          emit(Select(result.name, right, result, leftNan.name, 32))
          emit(Select(result.name, Value.Immediate(2147483647L), result, bothNan.name, 32))
          store(args.head, result)
        case _: SASSInstruction.Vmnmx =>
          requireModifiers(Set(
            Modifier.`MX`,
            Modifier.`MIN`,
            Modifier.`MAX`,
            Modifier.`ACC`,
            Modifier.`UD`,
            Modifier.`U32`
          ))
          val legacyNested = Set(50, 60)(module.sm.raw) &&
            (modifiers == Vec(Modifier.`MIN`) || modifiers == Vec(Modifier.`MX`, Modifier.`MAX`)) &&
            args.size == 4 && VMOperand.isRegister(args.head) &&
            args.tail.forall(VMOperand.isRegister(_, allowZero = true))
          if ((module.sm.raw != 35 && !legacyNested) || args.size != 4) unsupported("unsupported video min/max form")
          val unsigned = modifiers.contains(Modifier.`UD`)
          if (modifiers.count(_ == Modifier.U32) != (if (unsigned) 2 else 0))
            unsupported("mixed-width or mixed-sign video min/max")
          val secondary = modifiers.filter(Set(Modifier.`MIN`, Modifier.`MAX`, Modifier.`ACC`))
          if (secondary.size != 1) unsupported("unsupported video min/max secondary operation")
          val signed   = !unsigned
          val selected = temp(); val result = temp()
          emit(IntegerBinary(
            if (modifiers.contains(Modifier.`MX`)) IntegerOperation.Maximum(signed)
            else IntegerOperation.Minimum(signed),
            selected.name,
            source(args(1)),
            source(args(2)),
            32
          ))
          emit(IntegerBinary(
            integerOperation(secondary.head, signed),
            result.name,
            selected,
            source(args(3)),
            32
          ))
          store(args.head, result)
        case _: SASSInstruction.Imnmx =>
          requireModifiers(Set(Modifier.`U32`))
          val signed = !modifiers.contains(Modifier.U32)
          val a      = source(args(1)); val b = source(args(2)); val lo = temp(); val hi = temp()
          emit(IntegerBinary(IntegerOperation.Minimum(signed), lo.name, a, b, 32));
          emit(IntegerBinary(IntegerOperation.Maximum(signed), hi.name, a, b, 32))
          emit(Select(destination(args.head).name, lo, hi, predicateSource(args(3)).name, 32))
        case _: SASSInstruction.Iabs =>
          requireModifiers(Set.empty);
          emit(IntegerUnary(
            IntegerUnaryOperation.Absolute,
            destination(args.head).name,
            source(args(1)),
            32
          ))
        case _: SASSInstruction.F2f
            if Set(70, 75, 80, 86, 89)(module.sm.raw) && (modifiers == Vec(Modifier.`F32`, Modifier.`F64`)) =>
          if (args.size != 2) unsupported("invalid F2F.F32.F64 operands")
          if (!VMOperand.isRegister(args.head)) unsupported("invalid F2F.F32.F64 destination")
          val value = temp()
          emit(Convert(
            value.name,
            doubleSource(args(1)),
            ConversionType.F64,
            ConversionType.F32,
            Some(ConversionRounding.RN)
          ))
          store(args.head, value)
        case _: SASSInstruction.F2f
            if Set(70, 75, 80, 86, 89)(module.sm.raw) && (modifiers == Vec(Modifier.`F64`, Modifier.`F32`)) =>
          if (args.size != 2) unsupported("invalid F2F.F64.F32 operands")
          if (!VMOperand.isRegister(args.head)) unsupported("invalid F2F.F64.F32 destination")
          val value = temp("wide")
          emit(Convert(value.name, floatSource(args(1)), ConversionType.F32, ConversionType.F64, None))
          store(args.head, value, 64)
        case _: SASSInstruction.Frnd if Set(70, 75, 80, 86, 89)(module.sm.raw) && (modifiers.isEmpty) =>
          if (args.size != 2) unsupported("invalid FRND operands")
          val input = floatSource(args(1)); val rounded = temp(); val result = temp(); val nan = temp("q")
          emit(Convert(
            rounded.name,
            input,
            ConversionType.F32,
            ConversionType.F32,
            Some(ConversionRounding.RNI)
          ))
          emit(SetPredicate(nan.name, Comparison.Unordered, ConversionType.F32.format, input, input))
          emit(Select(result.name, Value.Immediate(2147483647L), rounded, nan.name, 32))
          store(args.head, result)
        case _: SASSInstruction.I2f if modifiers == Vec(Modifier.`F16`) =>
          if (args.size != 2) unsupported("invalid I2F.F16 operands")
          val result = temp()
          emit(Convert(
            result.name,
            source(args(1)),
            ConversionType.S32,
            ConversionType.F16,
            Some(ConversionRounding.RN)
          ))
          store(args.head, result)
        case _: SASSInstruction.I2f | _: SASSInstruction.I2fp =>
          requireModifiers(Set(
            Modifier.`RP`,
            Modifier.`RN`,
            Modifier.`RM`,
            Modifier.`RZ`,
            Modifier.`F32`,
            Modifier.`F64`,
            Modifier.`S32`,
            Modifier.`U32`,
            Modifier.`U64`,
            Modifier.`S64`
          ))
          if (args.size != 2) unsupported("invalid scalar integer-to-float operands")
          if (
            (instruction.operation match {
              case _: SASSInstruction.I2fp => true; case _ => false
            }) && (!modifiers.contains(
              Modifier.`F32`
            ) || modifiers.exists(Set(Modifier.`F64`, Modifier.`U64`, Modifier.`S64`)))
          )
            unsupported("only scalar 32-bit I2FP conversion is implemented")
          val rounding =
            modifiers.find(Set(Modifier.`RP`, Modifier.`RN`, Modifier.`RM`, Modifier.`RZ`)).getOrElse(Modifier.RN)
          val kind = modifiers.find(Set(Modifier.`U32`, Modifier.`U64`, Modifier.`S64`)).getOrElse(Modifier.S32)
          val bits: IntegerWidth = if (modifiers.contains(Modifier.`F64`)) 64 else 32
          val result             = temp(if (bits.raw == 64) "wide" else "t")
          emit(Convert(
            result.name,
            source(args(1), if (Set(Modifier.U64, Modifier.S64)(kind)) 64 else 32),
            integerConversion(kind),
            if (bits.raw == 64) ConversionType.F64 else ConversionType.F32,
            Some(conversionRounding(rounding))
          ))
          store(args.head, result, bits)
        case _: SASSInstruction.F2i =>
          requireModifiers(Set(
            Modifier.`FTZ`,
            Modifier.`F32`,
            Modifier.`F64`,
            Modifier.`S32`,
            Modifier.`U32`,
            Modifier.`S64`,
            Modifier.`U64`,
            Modifier.`TRUNC`,
            Modifier.`FLOOR`,
            Modifier.`CEIL`,
            Modifier.`ROUND`,
            Modifier.`NTZ`
          ))
          val kind =
            modifiers.find(Set(Modifier.`S32`, Modifier.`U32`, Modifier.`S64`, Modifier.`U64`)).getOrElse(Modifier.S32)
          val rounding = if (modifiers.contains(Modifier.`TRUNC`)) ConversionRounding.RZI
          else if (modifiers.contains(Modifier.`FLOOR`)) ConversionRounding.RMI
          else if (modifiers.contains(Modifier.`CEIL`)) ConversionRounding.RPI else ConversionRounding.RNI
          val floatBits = if (modifiers.contains(Modifier.`F64`)) 64 else 32
          if (floatBits == 64 && modifiers.contains(Modifier.`FTZ`)) unsupported("F2I.F64.FTZ")
          val input = if (floatBits == 64) doubleSource(args(1))
          else if (modifiers.contains(Modifier.`FTZ`)) flushFloat(floatSource(args(1))) else floatSource(args(1))
          val bits: IntegerWidth = if (Set(Modifier.U64, Modifier.S64)(kind)) 64 else 32
          val result             = temp(if (bits.raw == 64) "wide" else "t")
          emit(Convert(
            result.name,
            input,
            if (floatBits == 64) ConversionType.F64 else ConversionType.F32,
            integerConversion(kind),
            Some(rounding)
          ));
          store(args.head, result, bits)
        case _: SASSInstruction.Popc | _: SASSInstruction.Upopc =>
          requireModifiers(Set.empty);
          emit(IntegerUnary(
            IntegerUnaryOperation.PopulationCount,
            destination(args.head).name,
            source(args(1)),
            32
          ))
        case _: SASSInstruction.Flo | _: SASSInstruction.Uflo =>
          requireModifiers(Set(Modifier.`U32`, Modifier.`SH`))
          emit(BitFind(
            destination(args.head).name,
            source(args(1)),
            32,
            !modifiers.contains(Modifier.`U32`),
            modifiers.contains(Modifier.`SH`)
          ))
        case _: SASSInstruction.Redux =>
          requireModifiers(Set(
            Modifier.`MIN`,
            Modifier.`MAX`,
            Modifier.`SUM`,
            Modifier.`AND`,
            Modifier.`OR`,
            Modifier.`XOR`,
            Modifier.`U32`,
            Modifier.`S32`
          ))
          val mask   = temp(); emit(Move(mask.name, Value.ActiveMask, 32))
          val signed =
            !Set(
              Modifier.AND,
              Modifier.OR,
              Modifier.XOR,
              Modifier.SUM
            )(modifiers(0)) && !modifiers.contains(Modifier.U32)
          emit(WarpReduce(
            destination(args.head).name,
            source(args(1)),
            mask,
            integerOperation(modifiers(0), signed),
            32
          ))
        case _: SASSInstruction.Vote | _: SASSInstruction.Voteu =>
          requireModifiers(Set(Modifier.`ANY`, Modifier.`ALL`, Modifier.`EQ`))
          val mask  = temp(); emit(Move(mask.name, Value.ActiveMask, 32))
          val input = predicateSource(args.last)
          if (args.size == 3 && !VMOperand.isZero(args.head))
            emit(VoteBallot(destination(args.head).name, input.name, mask))
          val output = if (args.size == 2) args.head else args(1)
          if (!VMOperand.isAlwaysPredicate(output)) emit(VotePredicate(
            predicate(output).name,
            input.name,
            mask,
            modifiers(0) match {
              case Modifier.`EQ`  => VoteOperation.Uniform
              case Modifier.`ALL` => VoteOperation.All
              case Modifier.`ANY` => VoteOperation.Any
              case other          => unsupported(s"unsupported SASS vote operation ${other.name}")
            }
          ))
        case _: SASSInstruction.Brev | _: SASSInstruction.Ubrev =>
          requireModifiers(Set.empty); val result = temp();
          emit(IntegerUnary(IntegerUnaryOperation.BitReverse, result.name, source(args(1)), 32));
          store(args.head, result)
        case _: SASSInstruction.Fchk =>
          requireModifiers(Set.empty)
          if (!Set(70, 75, 80, 86, 89)(module.sm.raw))
            unsupported("FCHK range check is not supported for this architecture")
          if (args.size != 3) unsupported("invalid FCHK operands")
          if (
            Set(70, 75)(module.sm.raw) && (instruction.guard.nonEmpty || !VMOperand.isPredicate(args.head) ||
              !args.tail.forall(VMOperand.isRegister(_)))
          ) unsupported("unsupported sm_70/sm_75 FCHK operand or predicate form")
          // Division range check measured on sm_89. The sm_70/sm_75 forms use the same
          // operation encoding; see the fchk fixture README for cross-generation limits.
          // The predicate requests the slow path. Signs and significands do not participate.
          val numerator = temp(); val denominator = temp(); val difference = temp()
          emit(BitFieldExtract(
            numerator.name,
            floatSource(args(1)),
            Value.Immediate(23L),
            Value.Immediate(8L),
            32,
            false
          ))
          emit(BitFieldExtract(
            denominator.name,
            floatSource(args(2)),
            Value.Immediate(23L),
            Value.Immediate(8L),
            32,
            false
          ))
          emit(IntegerBinary(IntegerOperation.Subtract, difference.name, numerator, denominator, 32))
          val checks = Vec(
            (Comparison.LessThan, numerator, 25L),
            (Comparison.GreaterThan, numerator, 254L),
            (Comparison.LessThan, denominator, 2L),
            (Comparison.GreaterThan, denominator, 251L),
            (Comparison.LessThan, difference, -124L),
            (Comparison.GreaterThan, difference, 126L)
          ).map { case (comparison, value, boundary) =>
            val p = temp("q")
            emit(SetPredicate(
              p.name,
              comparison,
              ConversionType.S32.format,
              value,
              Value.Immediate(boundary)
            ))
            p
          }
          val result = checks.reduceLeft { (a, b) =>
            emit(PredicateBinary(PredicateOperation.Or, a.name, a.name, b.name)); a
          }
          if (args.head != VMOperand.True)
            emit(PredicateBinary(PredicateOperation.Or, predicate(args.head).name, result.name, "%false"))
        case _: SASSInstruction.Mufu if modifiers.exists(Set(Modifier.`RCP64H`, Modifier.`RSQ64H`)) =>
          requireModifiers(Set(Modifier.`RCP64H`, Modifier.`RSQ64H`))
          def flushHigh(value: Value): Value = {
            val exponent = temp(); val sign = temp(); val result = temp(); val subnormal = temp("q")
            emit(IntegerBinary(
              IntegerOperation.BitwiseAnd,
              exponent.name,
              value,
              Value.Immediate(2146435072L),
              32
            ));
            emit(IntegerBinary(
              IntegerOperation.BitwiseAnd,
              sign.name,
              value,
              Value.Immediate(2147483648L),
              32
            ))
            emit(SetPredicate(
              subnormal.name,
              Comparison.Equal,
              ConversionType.U32.format,
              exponent,
              Value.Immediate(0L)
            ));
            emit(Select(result.name, sign, value, subnormal.name, 32))
            result
          }
          val input      = temp("wide"); val result = temp("wide"); val high = temp()
          val raw        = VMOperand.without(args(1), OperandSelector.`reuse`)
          val highSource =
            if (VMOperand.numericFloating(raw))
              doubleSource(raw) match {
                case Value.Immediate(bits) => Value.Immediate(bits >>> 32)
                case value                 =>
                  val shifted = temp("wide")
                  val high    = temp()
                  emit(IntegerBinary(
                    IntegerOperation.ShiftRightLogical,
                    shifted.name,
                    value,
                    Value.Immediate(32L),
                    64
                  ))
                  emit(Convert(high.name, shifted, ConversionType.U64, ConversionType.U32, None))
                  high
              }
            else floatSource(raw)
          emit(MoveCombine(input.name, Vec(Value.Immediate(0L), flushHigh(highSource)), 32))
          emit(FloatUnary(
            if (modifiers.contains(Modifier.`RCP64H`)) FloatUnaryOperation.Reciprocal
            else FloatUnaryOperation.ReciprocalSquareRoot,
            result.name,
            input,
            64
          ))
          emit(IntegerBinary(
            IntegerOperation.ShiftRightLogical,
            result.name,
            result,
            Value.Immediate(32L),
            64
          ));
          emit(Convert(high.name, result, ConversionType.U64, ConversionType.U32, None))
          store(args.head, flushHigh(high))
        case _: SASSInstruction.Mufu =>
          requireModifiers(Set(
            Modifier.`RCP`,
            Modifier.`RSQ`,
            Modifier.`SQRT`,
            Modifier.`EX2`,
            Modifier.`LG2`,
            Modifier.`SIN`,
            Modifier.`COS`,
            Modifier.`TANH`
          ))
          val operation = floatUnaryOperation(modifiers(0))
          val input     = flushFloat(floatSource(args(1)))
          val argument  = if (Set(Modifier.`SIN`, Modifier.`COS`)(modifiers(0))) {
            if (module.sm.raw < 70) unsupported("legacy MUFU trigonometric range-reduction encoding")
            // Volta and later MUFU trigonometric inputs are cycles, whereas PTX uses radians.
            // Reduce before scaling so large exactly integral binary32 inputs remain periodic.
            val integral = temp(); val fraction = temp(); val radians = temp(); val zero = temp("q")
            emit(Convert(
              integral.name,
              input,
              ConversionType.F32,
              ConversionType.F32,
              Some(ConversionRounding.RNI)
            ))
            emit(FloatBinary(FloatOperation.Subtract, fraction.name, input, integral, 32))
            emit(FloatBinary(
              FloatOperation.Multiply,
              radians.name,
              fraction,
              Value.Immediate(1086918619L),
              32
            ))
            emit(SetPredicate(
              zero.name,
              Comparison.Equal,
              ConversionType.F32.format,
              input,
              Value.Immediate(0L)
            ))
            emit(Select(radians.name, input, radians, zero.name, 32))
            radians
          } else input
          val result = temp(); emit(FloatUnary(operation, result.name, argument, 32))
          store(args.head, flushFloat(result))
        case _: SASSInstruction.R2p =>
          requireModifiers(Set.empty)
          if (args.head != Operand.Symbol("PR")) unsupported("unsupported predicate register bank")
          val value = temp()
          val byte  = VMOperand.selectors(args(1)).filterNot(_ == OperandSelector.reuse) match {
            case Vec()                   => 0
            case Vec(OperandSelector.B1) => 1
            case Vec(OperandSelector.B2) => 2
            case Vec(OperandSelector.B3) => 3
            case _                       => unsupported("unsupported predicate source byte selector")
          }
          if (byte != 0) emit(IntegerBinary(
            IntegerOperation.ShiftRightLogical,
            value.name,
            source(VMOperand.base(args(1))),
            Value.Immediate(byte * 8L),
            32
          ))
          else emit(Move(value.name, source(args(1)), 32))
          val mask   = number(args(2)).toInt
          val ccMask = mask == 0xf000
          if (mask < 0 || ((mask & ~0x7f) != 0 && !ccMask)) unsupported("invalid predicate write mask")
          if (ccMask && (module.sm.raw != 35 || byte != 0))
            unsupported("R2P condition-code mask 0xf000 is supported only for unmodified sm_35 register sources")
          if (ccMask) {
            Vec[NonEmpty[String]]("%cc_z", "%cc_s", "%cc_c", "%cc_o").zipWithIndex.foreach { case (flag, i) =>
              val bit = temp();
              emit(IntegerBinary(
                IntegerOperation.BitwiseAnd,
                bit.name,
                value,
                Value.Immediate((1 << (12 + i)).toLong),
                32
              ))
              emit(SetPredicate(
                flag,
                Comparison.NotEqual,
                ConversionType.U32.format,
                bit,
                Value.Immediate(0L)
              ))
            }
            emit(PredicateBinary(PredicateOperation.Or, "%cc_eq", "%cc_z", "%false"))
            emit(PredicateUnary("%cc_lt", "%cc_c"))
            // The integer CC register also feeds later IADD.X carry-in.  PTX
            // has one carry bit, so rebuild it from the saved C bit.
            val carryIn = temp();
            emit(Select(carryIn.name, Value.Immediate(1L), Value.Immediate(0L), "%cc_c", 32))
            val carryState = temp();
            emit(IntegerAddWithCarry(
              carryState.name,
              Value.Immediate(4294967295L),
              carryIn,
              32,
              false,
              true
            ))
            writeConditionFlags()
          } else (0 until 7).filter(i => (mask & (1 << i)) != 0).foreach { i =>
            val bit = temp();
            emit(IntegerBinary(
              IntegerOperation.BitwiseAnd,
              bit.name,
              value,
              Value.Immediate((1 << i).toLong),
              32
            ))
            emit(SetPredicate(
              s"%P$i",
              Comparison.NotEqual,
              ConversionType.U32.format,
              bit,
              Value.Immediate(0L)
            ))
          }
        case _: SASSInstruction.I2i
            if args.headOption.contains(Operand.Qualified(VMOperand.Zero, Vec(OperandSelector.CC))) =>
          if (
            module.sm.raw != 35 || !((instruction.operation match {
              case _: SASSInstruction.I2i => true; case _ => false
            }) && modifiers == Vec(
              Modifier.`U32`,
              Modifier.`U32`
            )) || args.size != 2
          )
            unsupported("unsupported legacy I2I condition-code conversion")
          emit(SetPredicate(
            "%cc_z",
            Comparison.Equal,
            ConversionType.U32.format,
            source(args(1)),
            Value.Immediate(0L)
          ))
        case _: SASSInstruction.Csetp if modifiers == Vec(Modifier.`NE`, Modifier.`AND`) =>
          if (
            module.sm.raw != 35 || args != Vec(
              Operand.Predicate(Some(0), false, false),
              VMOperand.True,
              Operand.Symbol("CC"),
              VMOperand.True
            )
          )
            unsupported("unsupported legacy condition-code predicate form")

          val notZero = temp("q");
          emit(PredicateUnary(notZero.name, readConditionFlag(VMConditionCodes.Flag.Zero).name))
          emit(PredicateBinary(PredicateOperation.And, "%P0", notZero.name, "%true"))
        case _: SASSInstruction.Exit => requireModifiers(Set.empty); emit(Return)
        case _: SASSInstruction.Bra  =>
          requireModifiers(Set(Modifier.`U`, Modifier.`DIV`, Modifier.`CONV`))
          if (args.size == 2 && Set(OperandSelector.EQU, OperandSelector.NEU).exists(VMOperand.has(args.head, _))) {

            val condition = ExecutionPredicate(
              readConditionFlag(VMConditionCodes.Flag.Zero).name,
              args.head != Operand.Qualified(Operand.Symbol("CC"), Vec(OperandSelector.EQU))
            )
            branch(s"L${number(args.last).toHexString}", Some(condition))
          } else if (args.size == 2 && args.head != Operand.Unary(VMOperand.UniformZero, false, true, false))
            branch(s"L${number(args.last).toHexString}", Some(executionPredicate(args.head)))
          else branch(s"L${number(args.last).toHexString}")
        case _: SASSInstruction.Mov | _: SASSInstruction.Umov | _: SASSInstruction.Mov32i | _: SASSInstruction.R2ur =>
          requireModifiers(Set.empty); store(args(0), source(args(1)))
        case _: SASSInstruction.Uldc | _: SASSInstruction.Ldc | _: SASSInstruction.Ldcu =>
          requireModifiers(Set(Modifier.`64`, Modifier.`U8`, Modifier.`U16`, Modifier.`S8`, Modifier.`S16`))
          val bits: IntegerWidth = if (modifiers.contains(Modifier.`64`)) 64
          else if (modifiers.exists(Set(Modifier.`U8`, Modifier.`S8`))) 8
          else if (modifiers.exists(Set(Modifier.`U16`, Modifier.`S16`))) 16 else 32
          val result = source(args(1), bits)
          if (modifiers.exists(Set(Modifier.`S8`, Modifier.`S16`))) {
            val destination = result match {
              case value: Value.Register => value.name
              case _                     => unsupported("signed constant load requires a register result")
            }
            emit(IntegerBinary(
              IntegerOperation.ShiftLeft,
              destination,
              result,
              Value.Immediate((32 - bits.raw).toLong),
              32
            ))
            emit(IntegerBinary(
              IntegerOperation.ShiftRightArithmetic,
              destination,
              result,
              Value.Immediate((32 - bits.raw).toLong),
              32
            ))
          }
          store(args(0), result, bits)
        case _: SASSInstruction.S2r | _: SASSInstruction.S2ur =>
          requireModifiers(Set.empty)
          val special = Map(
            SpecialRegister.SR_TID_X   -> Value.ThreadId(LaunchGeometry.Axis.X),
            SpecialRegister.SR_TID_Y   -> Value.ThreadId(LaunchGeometry.Axis.Y),
            SpecialRegister.SR_TID_Z   -> Value.ThreadId(LaunchGeometry.Axis.Z),
            SpecialRegister.SR_CTAID_X -> Value.BlockId(LaunchGeometry.Axis.X),
            SpecialRegister.SR_CTAID_Y -> Value.BlockId(LaunchGeometry.Axis.Y),
            SpecialRegister.SR_CTAID_Z -> Value.BlockId(LaunchGeometry.Axis.Z),
            SpecialRegister.SR_LANEID  -> Value.LaneId,
            SpecialRegister.SR_GEMASK  -> Value.LaneMaskGreaterEqual,
            SpecialRegister.SR_GTMASK  -> Value.LaneMaskGreater,
            SpecialRegister.SR_LEMASK  -> Value.LaneMaskLessEqual,
            SpecialRegister.SR_LTMASK  -> Value.LaneMaskLess,
            SpecialRegister.SR_EQMASK  -> Value.LaneMaskEqual
          )
          store(
            args(0),
            special.getOrElse(
              args(1) match {
                case Operand.SpecialRegister(name) => name; case _ => unsupported("expected special register")
              },
              unsupported(s"unsupported special register ${args(1)}")
            )
          )
        case _: SASSInstruction.Imad | _: SASSInstruction.Uimad =>
          requireModifiers(Set(
            Modifier.`MOV`,
            Modifier.`SHL`,
            Modifier.`IADD`,
            Modifier.`U32`,
            Modifier.`WIDE`,
            Modifier.`HI`,
            Modifier.`X`
          ))
          val high     = modifiers.contains(Modifier.`HI`) && module.sm.raw >= 70
          val wide     = modifiers.contains(Modifier.`WIDE`) || high
          val signed   = !modifiers.contains(Modifier.U32)
          val carryOut = args.size >= 5 && VMOperand.isPredicate(args(1), allowUniform = true)
          val first    = if (carryOut) 2 else 1
          val a        = source(args(first)); val b = source(args(first + 1));
          val c        = source(args(first + 2), if (wide) 64 else 32)
          val result   = temp(if (wide) "wide" else "t")
          emit(IntegerBinary(
            if (wide) {
              if (signed) IntegerOperation.MultiplyWideSigned32 else IntegerOperation.MultiplyWideUnsigned32
            } else if (modifiers.contains(Modifier.`HI`)) IntegerOperation.MultiplyHigh(signed)
            else IntegerOperation.MultiplyLow,
            result.name,
            a,
            b,
            if (wide) 64 else 32
          ))
          val product = if (carryOut || ccFlagsSupported) {
            val t = temp(if (wide) "wide" else "t"); emit(Move(t.name, result, if (wide) 64 else 32)); t
          } else result
          val negatedAddend = VMOperand.negative(args(first + 2))
          if (
            module.sm.raw < 70 && !modifiers.contains(Modifier.`X`) && VMOperand.has(
              args.head,
              OperandSelector.`CC`
            ) && negatedAddend
          ) {
            if (wide || modifiers.contains(Modifier.`HI`)) unsupported("negated extended IMAD addend")
            emit(IntegerSubWithBorrow(
              result.name,
              result,
              source(VMOperand.positive(args(first + 2))),
              32,
              false,
              true
            ))
            val borrow = temp()
            emit(IntegerSubWithBorrow(
              borrow.name,
              Value.Immediate(0L),
              Value.Immediate(0L),
              32,
              true,
              false
            ))
            emit(IntegerUnary(IntegerUnaryOperation.BitwiseNot, borrow.name, borrow, 32))
            emit(IntegerAddWithCarry(borrow.name, borrow, Value.Immediate(1L), 32, false, true))
          } else emit(IntegerAddWithCarry(
            result.name,
            result,
            c,
            if (wide) 64 else 32,
            modifiers.contains(Modifier.`X`) && module.sm.raw < 70,
            VMOperand.has(args.head, OperandSelector.`CC`)
          ))
          if (ccFlagsSupported) {
            val carry = temp();
            emit(IntegerAddWithCarry(carry.name, Value.Immediate(0L), Value.Immediate(0L), 32, true, false))
            updateIntegerCC(product, c, result, isSub = false, Some(carry))
          }
          if (carryOut && !VMOperand.isAlwaysPredicate(args(1)))
            emit(SetPredicate(
              predicate(args(1)).name,
              Comparison.LessThan,
              if (wide) ConversionType.U64.format else ConversionType.U32.format,
              result,
              product
            ))
          if (modifiers.contains(Modifier.`X`) && module.sm.raw >= 70) {
            if (args.size != first + 4) unsupported("IMAD.X requires a carry predicate")
            val carry = temp(if (wide) "wide" else "t")
            emit(Select(
              carry.name,
              Value.Immediate(1L),
              Value.Immediate(0L),
              predicateSource(args.last).name,
              if (wide) 64 else 32
            ))
            emit(IntegerBinary(IntegerOperation.Add, result.name, result, carry, if (wide) 64 else 32))
          }
          if (high) {
            // Volta and later IMAD.HI add a register pair before selecting the high word.
            val upper = temp("wide"); val narrowed = temp()
            emit(IntegerBinary(
              IntegerOperation.ShiftRightLogical,
              upper.name,
              result,
              Value.Immediate(32L),
              64
            ))
            emit(Convert(narrowed.name, upper, ConversionType.U64, ConversionType.U32, None))
            store(args(0), narrowed)
          } else store(args(0), result, if (wide) 64 else 32)
        case _: SASSInstruction.Isub =>
          requireModifiers(Set(Modifier.`X`, Modifier.`U32`))
          val a = source(args(1)); val b = source(args(2)); val result = temp()
          if (modifiers.contains(Modifier.`X`) || VMOperand.has(args.head, OperandSelector.`CC`)) {
            // SASS extended subtraction consumes the condition-code carry as
            // a no-borrow bit: a - b - (1 - C) == a + ~b + C. PTX subc uses
            // the opposite borrow convention, so express it through addc.
            val inverted = temp();
            emit(IntegerUnary(IntegerUnaryOperation.BitwiseNot, inverted.name, b, 32))
            if (!modifiers.contains(Modifier.`X`)) {
              val carrySeed = temp();
              emit(IntegerAddWithCarry(
                carrySeed.name,
                Value.Immediate(4294967295L),
                Value.Immediate(1L),
                32,
                false,
                true
              ))
            }
            emit(IntegerAddWithCarry(
              result.name,
              a,
              inverted,
              32,
              true,
              VMOperand.has(args.head, OperandSelector.`CC`)
            ))
          } else emit(IntegerBinary(IntegerOperation.Subtract, result.name, a, b, 32))
          if (VMOperand.has(args.head, OperandSelector.`CC`)) {
            emit(SetPredicate("%cc_lt", Comparison.LessThan, ConversionType.U32.format, a, b));
            emit(SetPredicate("%cc_eq", Comparison.Equal, ConversionType.U32.format, a, b))
          }
          store(args.head, result)
        case _: SASSInstruction.Imul | _: SASSInstruction.Imul32i =>
          requireModifiers(Set(Modifier.`U32`, Modifier.`HI`))
          val result = temp()
          emit(IntegerBinary(
            if (modifiers.contains(Modifier.`HI`)) IntegerOperation.MultiplyHigh(!modifiers.contains(Modifier.`U32`))
            else IntegerOperation.MultiplyLow,
            result.name,
            source(args(1)),
            source(args(2)),
            32
          ))
          store(args.head, result)
        case _: SASSInstruction.Iadd | _: SASSInstruction.Iadd32i | _: SASSInstruction.Iscadd =>
          requireModifiers(Set(Modifier.`X`, Modifier.`U32`))
          val negA = negatedSource(args(1)); val negB = negatedSource(args(2))
          if (
            (negA || negB) && (instruction.operation match { case _: SASSInstruction.Iscadd => true; case _ => false })
          )
            unsupported("negated ISCADD operands are not implemented")
          if (negA && negB) unsupported("IADD with two negated operands is not implemented")
          def addOperand(raw: Operand): Value =
            if (modifiers.contains(Modifier.`X`) && negatedSource(raw)) {
              val inverted = temp()
              emit(IntegerUnary(
                IntegerUnaryOperation.BitwiseNot,
                inverted.name,
                source(VMOperand.positive(raw)),
                32
              ))
              inverted
            } else source(raw)
          val a       = addOperand(args(1)); val b = addOperand(args(2)); val result = temp()
          val shifted = if (instruction.operation match { case _: SASSInstruction.Iscadd => true; case _ => false }) {
            val t = temp(); emit(IntegerBinary(IntegerOperation.ShiftLeft, t.name, a, source(args(3)), 32));
            t
          } else a
          val subtractInputs =
            if (negA || negB)
              Some((if (negA) b else a, source(if (negA) VMOperand.positive(args(1)) else VMOperand.positive(args(2)))))
            else None
          val rawCarry =
            if (
              !modifiers.contains(Modifier.`X`) && VMOperand.has(
                args.head,
                OperandSelector.`CC`
              ) && subtractInputs.nonEmpty
            ) {
              // A negated source denotes subtraction. SASS carries no-borrow
              // into IADD.X, whose negated source is one's-complemented.
              val (subtractLeft, subtractRight) = subtractInputs.get
              emit(IntegerSubWithBorrow(result.name, subtractLeft, subtractRight, 32, false, true))
              val borrow = temp()
              emit(IntegerSubWithBorrow(
                borrow.name,
                Value.Immediate(0L),
                Value.Immediate(0L),
                32,
                true,
                false
              ))
              emit(IntegerUnary(IntegerUnaryOperation.BitwiseNot, borrow.name, borrow, 32))
              emit(IntegerAddWithCarry(borrow.name, borrow, Value.Immediate(1L), 32, false, true))
              None
            } else {
              emit(IntegerAddWithCarry(
                result.name,
                shifted,
                b,
                32,
                modifiers.contains(Modifier.`X`),
                VMOperand.has(args.head, OperandSelector.`CC`)
              ))
              if (ccFlagsSupported) {
                val carry = temp();
                emit(IntegerAddWithCarry(
                  carry.name,
                  Value.Immediate(0L),
                  Value.Immediate(0L),
                  32,
                  true,
                  false
                ));
                Some(carry)
              } else None
            }
          if (VMOperand.has(args.head, OperandSelector.`CC`)) {
            emit(SetPredicate(
              "%cc_eq",
              Comparison.Equal,
              ConversionType.U32.format,
              result,
              Value.Immediate(0L)
            ))
            if (
              !(instruction.operation match {
                case _: SASSInstruction.Iscadd => true; case _ => false
              }) && VMOperand.negative(args(1))
            )
              emit(SetPredicate(
                "%cc_lt",
                Comparison.LessThan,
                ConversionType.U32.format,
                b,
                source(VMOperand.positive(args(1)))
              ))
            else if (VMOperand.negative(args(2)))
              emit(SetPredicate(
                "%cc_lt",
                Comparison.LessThan,
                ConversionType.U32.format,
                shifted,
                source(VMOperand.positive(args(2)))
              ))
            else {
              val carry = temp();
              emit(IntegerAddWithCarry(
                carry.name,
                Value.Immediate(0L),
                Value.Immediate(0L),
                32,
                true,
                false
              ))
              emit(SetPredicate(
                "%cc_lt",
                Comparison.Equal,
                ConversionType.U32.format,
                carry,
                Value.Immediate(0L)
              ))
            }
            if (ccFlagsSupported) {
              val isSub      = negA || negB
              val (lhs, rhs) = subtractInputs.getOrElse((shifted, b))
              updateIntegerCC(lhs, rhs, result, isSub, rawCarry)
            }
          }
          store(args.head, result)
        case _: SASSInstruction.Shl | _: SASSInstruction.Shr =>
          requireModifiers(Set(Modifier.`U32`, Modifier.`W`))
          val result = temp()
          val amount = if (modifiers.contains(Modifier.`W`)) {
            val wrapped = temp()
            emit(IntegerBinary(
              IntegerOperation.BitwiseAnd,
              wrapped.name,
              source(args(2)),
              Value.Immediate(31L),
              32
            ))
            wrapped
          } else source(args(2))
          emit(IntegerBinary(
            if (instruction.operation match { case _: SASSInstruction.Shl => true; case _ => false })
              IntegerOperation.ShiftLeft
            else if (modifiers.contains(Modifier.`U32`)) IntegerOperation.ShiftRightLogical
            else IntegerOperation.ShiftRightArithmetic,
            result.name,
            source(args(1)),
            amount,
            32
          ))
          store(args.head, result)
        case _: SASSInstruction.Iadd3 | _: SASSInstruction.Uiadd3 =>
          requireModifiers(Set(Modifier.`X`))
          if (modifiers.contains(Modifier.`X`)) {
            if (args.size != 6) unsupported("IADD3.X carry outputs")
            val a     = source(args(1)); val b = source(args(2)); val c = source(args(3))
            val first = temp(); val second     = temp(); val result     = temp()
            emit(Select(
              first.name,
              Value.Immediate(1L),
              Value.Immediate(0L),
              predicateSource(args(4)).name,
              32
            ))
            emit(Select(
              second.name,
              Value.Immediate(1L),
              Value.Immediate(0L),
              predicateSource(args(5)).name,
              32
            ))
            emit(IntegerBinary(IntegerOperation.Add, result.name, a, b, 32));
            emit(IntegerBinary(IntegerOperation.Add, result.name, result, c, 32))
            emit(IntegerBinary(IntegerOperation.Add, result.name, result, first, 32));
            emit(IntegerBinary(IntegerOperation.Add, result.name, result, second, 32))
            store(args.head, result)
          } else {
            val count = args.size - 4
            if (count < 0 || count > 2) unsupported("IADD3 carry operands")
            def extended(raw: Operand): Value = {
              val v = temp("wide")
              if (VMOperand.negative(raw)) {
                val inverse = temp();
                emit(IntegerUnary(
                  IntegerUnaryOperation.BitwiseNot,
                  inverse.name,
                  source(VMOperand.positive(raw)),
                  32
                ))
                emit(Convert(v.name, inverse, ConversionType.U32, ConversionType.U64, None));
                emit(IntegerBinary(IntegerOperation.Add, v.name, v, Value.Immediate(1L), 64))
              } else emit(Convert(v.name, source(raw), ConversionType.U32, ConversionType.U64, None))
              v
            }
            val first = 1 + count
            val a     = extended(args(first)); val b = extended(args(first + 1)); val c = extended(args(first + 2))
            val sum   = temp("wide"); val low        = temp()
            emit(IntegerBinary(IntegerOperation.Add, sum.name, a, b, 64))
            if (count >= 1 && !VMOperand.isAlwaysPredicate(args(1)))
              emit(SetPredicate(
                predicate(args(1)).name,
                Comparison.GreaterThan,
                ConversionType.U64.format,
                sum,
                Value.Immediate(4294967295L)
              ))
            emit(IntegerBinary(
              IntegerOperation.BitwiseAnd,
              sum.name,
              sum,
              Value.Immediate(4294967295L),
              64
            ));
            emit(IntegerBinary(IntegerOperation.Add, sum.name, sum, c, 64))
            if (count == 2 && !VMOperand.isAlwaysPredicate(args(2)))
              emit(SetPredicate(
                predicate(args(2)).name,
                Comparison.GreaterThan,
                ConversionType.U64.format,
                sum,
                Value.Immediate(4294967295L)
              ))
            emit(Convert(low.name, sum, ConversionType.U64, ConversionType.U32, None));
            store(args.head, low)
          }
        case _: SASSInstruction.Lop32i if modifiers == Vec(Modifier.`AND`, Modifier.`X`) =>
          if (
            module.sm.raw != 35 || args.size != 3 || args.head != Operand.Qualified(
              VMOperand.Zero,
              Vec(OperandSelector.CC)
            ) || !VMOperand.integer(args(2)).contains(BigInt(65535))
          )
            unsupported("unsupported legacy zero-flag logical extension")

          val previousZero = temp("q");
          emit(PredicateBinary(
            PredicateOperation.Or,
            previousZero.name,
            readConditionFlag(VMConditionCodes.Flag.Zero).name,
            "%false"
          ))
          val masked = temp();
          emit(IntegerBinary(
            IntegerOperation.BitwiseAnd,
            masked.name,
            source(args(1)),
            Value.Immediate(65535L),
            32
          ))
          val highZero = temp("q");
          emit(SetPredicate(
            highZero.name,
            Comparison.Equal,
            ConversionType.U32.format,
            masked,
            Value.Immediate(0L)
          ))
          emit(PredicateBinary(PredicateOperation.And, "%cc_z", previousZero.name, highZero.name))
        case _: SASSInstruction.Lop
            if Set(50, 60)(module.sm.raw) && modifiers.exists(Set(Modifier.`Z`, Modifier.`NZ`)) =>
          if (
            modifiers.size != 2 || !Set(Modifier.`AND`, Modifier.`OR`)(modifiers(0)) || !Set(
              Modifier.`Z`,
              Modifier.`NZ`
            )(modifiers(1))
          )
            unsupported("unsupported legacy logical predicate modifiers")
          if (args.size != 4 || !VMOperand.isPredicate(args(0)) || !VMOperand.isRegister(args(1), allowZero = true))
            unsupported("invalid legacy logical predicate operands")
          val result = temp()
          emit(IntegerBinary(
            integerOperation(modifiers(0)),
            result.name,
            source(args(2)),
            source(args(3)),
            32
          ))
          store(args(1), result)
          if (args.head != VMOperand.True)
            emit(SetPredicate(
              predicate(args.head).name,
              if (modifiers(1) == Modifier.`Z`) Comparison.Equal else Comparison.NotEqual,
              ConversionType.U32.format,
              result,
              Value.Immediate(0L)
            ))
        case _: SASSInstruction.Lop | _: SASSInstruction.Lop32i =>
          requireModifiers(Set(Modifier.`AND`, Modifier.`OR`, Modifier.`XOR`, Modifier.`PASS_B`))
          val result    = temp()
          val operation = modifiers.headOption.getOrElse(unsupported("missing logical operation"))
          if (operation == Modifier.PASS_B) store(args.head, source(args(2)))
          else {
            emit(IntegerBinary(
              integerOperation(operation),
              result.name,
              source(args(1)),
              source(args(2)),
              32
            ));
            store(args.head, result)
          }
        case _: SASSInstruction.Pset =>
          if (
            !Set(50, 60)(module.sm.raw) || !((instruction.operation match {
              case _: SASSInstruction.Pset => true; case _ => false
            }) && modifiers == Vec(
              Modifier.`AND`,
              Modifier.`AND`
            )) ||
            args.size != 4 || !VMOperand.isRegister(
              args.head,
              allowZero = true
            ) || VMOperand.selectors(args.head).nonEmpty ||
            !VMOperand.isPredicate(args(1), allowNegated = true) || args.drop(2) != Vec(
              VMOperand.True,
              VMOperand.True
            ) ||
            instruction.guard.nonEmpty
          )
            unsupported("unsupported legacy PSET form")
          val result = temp()
          emit(Select(
            result.name,
            Value.Immediate(4294967295L),
            Value.Immediate(0L),
            predicateSource(args(1)).name,
            32
          ))
          store(args.head, result)
        case _: SASSInstruction.Psetp =>
          requireModifiers(Set(Modifier.`AND`, Modifier.`OR`, Modifier.`XOR`))
          if (modifiers.size != 2 || args.size != 5) unsupported("unsupported predicate logic operands")
          val result = temp("q"); val complement = temp("q"); val input = temp("q")
          emit(PredicateBinary(PredicateOperation.Or, input.name, predicateSource(args(4)).name, "%false"))
          emit(PredicateBinary(
            predicateOperation(modifiers(0)),
            result.name,
            predicateSource(args(2)).name,
            predicateSource(args(3)).name
          ))
          emit(PredicateUnary(complement.name, result.name))
          if (args.head != VMOperand.True)
            emit(PredicateBinary(
              predicateOperation(modifiers(1)),
              predicate(args.head).name,
              result.name,
              input.name
            ))
          if (args(1) != VMOperand.True)
            emit(PredicateBinary(
              predicateOperation(modifiers(1)),
              predicate(args(1)).name,
              complement.name,
              input.name
            ))
        case _: SASSInstruction.Lop3 | _: SASSInstruction.Ulop3 =>
          requireModifiers(Set(Modifier.`LUT`, Modifier.`PAND`))
          val withPredicate = args.size == 7
          val operands      = if (withPredicate) args.tail
          else if (
            (instruction.operation match { case _: SASSInstruction.Lop3 => true; case _ => false }) && Set(
              50,
              60
            )(module.sm.raw) && args.size == 5 && ((instruction.operation match {
              case _: SASSInstruction.Lop3 => true; case _ => false
            }) && modifiers == Vec(Modifier.`LUT`))
          )
            args :+ VMOperand.True.copy(negated = true)
          else args
          val predicateLogic =
            (instruction.operation match {
              case _: SASSInstruction.Lop3 => true; case _ => false
            }) && withPredicate && Set(
              70, 75, 80, 86, 89
            )(module.sm.raw)
          if (operands.size != 6) unsupported("invalid LOP3 operands")
          if (modifiers.contains(Modifier.`PAND`) && !predicateLogic) unsupported("unsupported LOP3 predicate AND form")
          if (
            !VMOperand.isNeverPredicate(operands.last) &&
            !(predicateLogic && VMOperand.isPredicate(operands.last, allowNegated = true))
          ) unsupported("LOP3 predicate input")
          // Preserve a negated input before an aliased predicate destination is written.
          val predicateInput = if (withPredicate) Some(predicateSource(operands.last)) else None
          val a = source(operands(1)); val b = source(operands(2)); val c = source(operands(3)); val result = temp()
          val inputs   = Vec(a, b, c)
          val inverses = inputs.map { input =>
            val inverse = temp();
            emit(IntegerUnary(IntegerUnaryOperation.BitwiseNot, inverse.name, input, 32)); inverse
          }
          val table = number(operands(4)).toInt
          if (table < 0 || table > 255) unsupported("invalid LOP3 truth table")
          emit(Move(result.name, Value.Immediate(0L), 32))
          (0 until 8).filter(bit => (table & (1 << bit)) != 0).foreach { bit =>
            val term   = temp()
            val values = (0 until 3).map(i => if ((bit & (1 << (2 - i))) != 0) inputs(i) else inverses(i))
            emit(IntegerBinary(IntegerOperation.BitwiseAnd, term.name, values(0), values(1), 32));
            emit(IntegerBinary(IntegerOperation.BitwiseAnd, term.name, term, values(2), 32))
            emit(IntegerBinary(IntegerOperation.BitwiseOr, result.name, result, term, 32))
          }
          if (withPredicate && !VMOperand.isAlwaysPredicate(args.head)) {
            val nonzero = temp("q")
            emit(SetPredicate(
              nonzero.name,
              Comparison.NotEqual,
              ConversionType.U32.format,
              result,
              Value.Immediate(0L)
            ))
            emit(PredicateBinary(
              if (modifiers.contains(Modifier.`PAND`)) PredicateOperation.And else PredicateOperation.Or,
              predicate(args.head).name,
              nonzero.name,
              predicateInput.get.name
            ))
          }
          store(operands.head, result)
        case _: SASSInstruction.Shf | _: SASSInstruction.Ushf =>
          requireModifiers(Set(
            Modifier.`L`,
            Modifier.`R`,
            Modifier.`U32`,
            Modifier.`S32`,
            Modifier.`U64`,
            Modifier.`S64`,
            Modifier.`HI`,
            Modifier.`W`
          ))
          val wide = temp("wide"); val amount = temp(); val result = temp()
          emit(MoveCombine(wide.name, Vec(source(args(1)), source(args(3))), 32))
          val bits = if (modifiers.exists(Set(Modifier.`U64`, Modifier.`S64`))) 64 else 32
          if (modifiers.contains(Modifier.`W`))
            emit(IntegerBinary(
              IntegerOperation.BitwiseAnd,
              amount.name,
              source(args(2)),
              Value.Immediate((bits - 1).toLong),
              32
            ))
          else emit(IntegerBinary(
            IntegerOperation.Minimum(false),
            amount.name,
            source(args(2)),
            Value.Immediate(bits.toLong),
            32
          ))
          emit(IntegerBinary(
            if (modifiers.contains(Modifier.`L`)) IntegerOperation.ShiftLeft
            else if (modifiers.exists(Set(Modifier.`S32`, Modifier.`S64`))) IntegerOperation.ShiftRightArithmetic
            else IntegerOperation.ShiftRightLogical,
            wide.name,
            wide,
            amount,
            64
          ))
          if (modifiers.contains(Modifier.`HI`) || (module.sm.raw < 70 && modifiers.contains(Modifier.`L`)))
            emit(IntegerBinary(
              IntegerOperation.ShiftRightLogical,
              wide.name,
              wide,
              Value.Immediate(32L),
              64
            ))
          emit(Convert(result.name, wide, ConversionType.U64, ConversionType.U32, None));
          store(args.head, result)
        case _: SASSInstruction.Shfl =>
          requireModifiers(Set(Modifier.`UP`, Modifier.`DOWN`, Modifier.`IDX`, Modifier.`BFLY`))
          val result = temp(); val valid = temp("q"); val mask = temp()
          emit(Move(mask.name, Value.ActiveMask, 32))
          emit(shuffle(modifiers(0), result, Some(valid), source(args(2)), source(args(3)), source(args(4)), mask, 32))
          store(args(1), result)
          if (args.head != VMOperand.True)
            emit(PredicateBinary(PredicateOperation.Or, predicate(args.head).name, valid.name, "%false"))
        case _: SASSInstruction.Cctl if module.sm.raw == 35 && (modifiers == Vec(Modifier.`E`, Modifier.`IV`)) =>
          if (args.size != 1 || !VMOperand.plainAddress(args.head))
            unsupported("unsupported cache-line invalidation address")
        // ptxas emits this after global atomics to invalidate a potentially
        // stale cache line. The VM's atomic and normal accesses share one
        // coherent memory image, so there is no cached copy to invalidate.
        case _: SASSInstruction.Cctl if modifiers.contains(Modifier.`IVALL`) =>
          requireModifiers(Set(Modifier.`IVALL`)); emit(MemoryBarrier)
        case _: SASSInstruction.Cctl if modifiers.contains(Modifier.`PF1`) || modifiers.contains(Modifier.`PF2`) =>
          requireModifiers(Set(
            Modifier.`E`,
            Modifier.`PF1`,
            Modifier.`PF2`
          )) // Cache prefetch hints have no architectural result.
        case _: SASSInstruction.Lea | _: SASSInstruction.Ulea =>
          requireModifiers(Set(Modifier.`HI`, Modifier.`X`, Modifier.`SX32`))
          val high        = modifiers.contains(Modifier.`HI`)
          val carryOutput = VMOperand.isPredicate(args(1), allowUniform = true)
          val first       = if (carryOutput) 2 else 1
          val negate      = VMOperand.negative(args(first))
          val complement  = VMOperand.complemented(args(first))
          val rawA        = if (negate || complement) VMOperand.unaryValue(args(first)) else args(first)
          val a           = source(rawA); val b = source(args(first + 1))
          val result      = temp("wide")
          if (high) {
            val upper = if (modifiers.contains(Modifier.`SX32`)) {
              val sign = temp();
              emit(IntegerBinary(
                IntegerOperation.ShiftRightArithmetic,
                sign.name,
                a,
                Value.Immediate(31L),
                32
              ));
              sign
            } else source(args(first + 2))
            val shift = args(first + (if (modifiers.contains(Modifier.`SX32`)) 2 else 3))
            emit(MoveCombine(result.name, Vec(a, upper), 32))
            if (negate) emit(IntegerUnary(IntegerUnaryOperation.Negate, result.name, result, 64))
            if (complement) emit(IntegerUnary(IntegerUnaryOperation.BitwiseNot, result.name, result, 64))
            emit(IntegerBinary(IntegerOperation.ShiftLeft, result.name, result, source(shift), 64))
            emit(IntegerBinary(
              IntegerOperation.ShiftRightLogical,
              result.name,
              result,
              Value.Immediate(32L),
              64
            ))
          } else {
            if (modifiers.contains(Modifier.`X`)) unsupported("LEA.X without HI")
            val shifted = temp()
            emit(IntegerBinary(IntegerOperation.ShiftLeft, shifted.name, a, source(args(first + 2)), 32))
            if (negate || complement)
              emit(IntegerUnary(IntegerUnaryOperation.BitwiseNot, shifted.name, shifted, 32))
            emit(Convert(result.name, shifted, ConversionType.U32, ConversionType.U64, None))
            if (negate)
              emit(IntegerBinary(IntegerOperation.Add, result.name, result, Value.Immediate(1L), 64))
          }
          val addend = temp("wide");
          emit(Convert(addend.name, b, ConversionType.U32, ConversionType.U64, None))
          emit(IntegerBinary(IntegerOperation.Add, result.name, result, addend, 64))
          if (modifiers.contains(Modifier.`X`)) {
            val carry = temp("wide")
            emit(Select(
              carry.name,
              Value.Immediate(1L),
              Value.Immediate(0L),
              predicate(args.last).name,
              64
            ))
            emit(IntegerBinary(IntegerOperation.Add, result.name, result, carry, 64))
          }
          if (carryOutput && args(1) != VMOperand.True && args(1) != Operand.Predicate(None, true, false))
            emit(SetPredicate(
              predicate(args(1)).name,
              Comparison.GreaterThan,
              ConversionType.U64.format,
              result,
              Value.Immediate(4294967295L)
            ))
          val low = temp(); emit(Convert(low.name, result, ConversionType.U64, ConversionType.U32, None));
          store(args(0), low)
        case _: SASSInstruction.Fset | _: SASSInstruction.Dset =>
          if (args.size != 4) unsupported(s"$op requires destination, two sources and a predicate")
          val zeroFlagOnly =
            (instruction.operation match {
              case _: SASSInstruction.Fset => true; case _ => false
            }) && supportsZeroFlagFSetCC(
              module.sm.raw,
              instruction.operation,
              modifiers,
              args
            )
          if (VMOperand.has(args.head, OperandSelector.`CC`) && !zeroFlagOnly)
            unsupported(s"$op condition-code output is not implemented")
          if (
            (instruction.operation match {
              case _: SASSInstruction.Dset => true; case _ => false
            }) && modifiers.contains(Modifier.`FTZ`)
          )
            unsupported("DSET does not support FTZ")
          requireModifiers(Set(
            Modifier.`BF`,
            Modifier.`EQ`,
            Modifier.`NE`,
            Modifier.`LT`,
            Modifier.`LE`,
            Modifier.`GT`,
            Modifier.`GE`,
            Modifier.`EQU`,
            Modifier.`NEU`,
            Modifier.`LTU`,
            Modifier.`LEU`,
            Modifier.`GTU`,
            Modifier.`GEU`,
            Modifier.`AND`,
            Modifier.`OR`,
            Modifier.`XOR`,
            Modifier.`FTZ`
          ))
          val comparison =
            modifiers.find(Set(
              Modifier.`EQ`,
              Modifier.`NE`,
              Modifier.`LT`,
              Modifier.`LE`,
              Modifier.`GT`,
              Modifier.`GE`,
              Modifier.`EQU`,
              Modifier.`NEU`,
              Modifier.`LTU`,
              Modifier.`LEU`,
              Modifier.`GTU`,
              Modifier.`GEU`
            )).get
          val a = if (instruction.operation match { case _: SASSInstruction.Dset => true; case _ => false })
            doubleSource(args(1))
          else floatSource(args(1))
          val b = if (instruction.operation match { case _: SASSInstruction.Dset => true; case _ => false })
            doubleSource(args(2))
          else floatSource(args(2))
          val result = temp("q")
          emit(SetPredicate(
            result.name,
            comparisonCode(comparison),
            if (instruction.operation match { case _: SASSInstruction.Dset => true; case _ => false })
              ConversionType.F64.format
            else ConversionType.F32.format,
            if (modifiers.contains(Modifier.`FTZ`)) flushFloat(a) else a,
            if (modifiers.contains(Modifier.`FTZ`)) flushFloat(b) else b
          ))
          val boolean = modifiers.find(Set(Modifier.`AND`, Modifier.`OR`, Modifier.`XOR`)).get
          emit(PredicateBinary(
            predicateOperation(boolean),
            result.name,
            result.name,
            predicateSource(args(3)).name
          ))
          if (zeroFlagOnly) {
            // The observed consumer reads only the zero flag. It is clear iff
            // the predicate-masked unordered-greater result is true.
            emit(PredicateUnary("%cc_z", result.name))
          } else {
            val value = temp();
            emit(Select(
              value.name,
              if (modifiers.contains(Modifier.`BF`)) Value.Immediate(0x3f800000L) else Value.Immediate(0xffffffffL),
              Value.Immediate(0L),
              result.name,
              32
            ))
            store(args.head, value)
          }
        case _: SASSInstruction.Errbar => requireModifiers(Set.empty); emit(MemoryBarrier)
        case _: SASSInstruction.Isetp | _: SASSInstruction.Uisetp | _: SASSInstruction.Fsetp | _: SASSInstruction.Dsetp =>
          requireModifiers(Set(
            Modifier.`EQ`,
            Modifier.`NE`,
            Modifier.`LT`,
            Modifier.`LE`,
            Modifier.`GT`,
            Modifier.`GE`,
            Modifier.`EQU`,
            Modifier.`NEU`,
            Modifier.`LTU`,
            Modifier.`LEU`,
            Modifier.`GTU`,
            Modifier.`GEU`,
            Modifier.`NAN`,
            Modifier.`NUM`,
            Modifier.`U32`,
            Modifier.`AND`,
            Modifier.`OR`,
            Modifier.`XOR`,
            Modifier.`EX`,
            Modifier.`X`,
            Modifier.`FTZ`,
            Modifier.`MAX`,
            Modifier.`MIN`
          ))
          val extrema = modifiers.contains(Modifier.`MAX`) || modifiers.contains(Modifier.`MIN`)
          if (extrema) {
            if (
              !(instruction.operation match {
                case _: SASSInstruction.Dsetp => true; case _ => false
              }) || !(modifiers == Vec(
                Modifier.`MAX`,
                Modifier.`AND`
              ) || modifiers == Vec(Modifier.`MIN`, Modifier.`AND`)) ||
              args.size != 5
            )
              unsupported("unsupported DSETP MAX/MIN form")
            val a = doubleSource(args(2)); val b = doubleSource(args(3)); val input = temp("q")
            emit(PredicateBinary(
              PredicateOperation.Or,
              input.name,
              predicateSource(args(4)).name,
              "%false"
            ))
            val nanA     = temp("q"); val nanB     = temp("q"); val compare  = temp("q")
            val aOnly    = temp("q"); val bothNan  = temp("q"); val chooseA  = temp("q")
            val posZeroA = temp("q"); val negZeroA = temp("q"); val posZeroB = temp("q"); val negZeroB = temp("q")
            val zeroTie  = temp("q")
            emit(SetPredicate(nanA.name, Comparison.Unordered, ConversionType.F64.format, a, a));
            emit(SetPredicate(nanB.name, Comparison.Unordered, ConversionType.F64.format, b, b))
            emit(SetPredicate(
              compare.name,
              if (modifiers.contains(Modifier.`MAX`)) Comparison.GreaterThan else Comparison.LessThan,
              ConversionType.F64.format,
              a,
              b
            ))
            emit(SetPredicate(
              posZeroA.name,
              Comparison.Equal,
              ConversionType.U64.format,
              a,
              Value.Immediate(0L)
            ))
            emit(SetPredicate(
              negZeroA.name,
              Comparison.Equal,
              ConversionType.U64.format,
              a,
              Value.Immediate(-9223372036854775808L)
            ))
            emit(SetPredicate(
              posZeroB.name,
              Comparison.Equal,
              ConversionType.U64.format,
              b,
              Value.Immediate(0L)
            ))
            emit(SetPredicate(
              negZeroB.name,
              Comparison.Equal,
              ConversionType.U64.format,
              b,
              Value.Immediate(-9223372036854775808L)
            ))
            if (modifiers.contains(Modifier.`MAX`)) {
              emit(PredicateBinary(PredicateOperation.And, zeroTie.name, posZeroA.name, negZeroB.name))
            } else {
              emit(PredicateBinary(PredicateOperation.And, zeroTie.name, negZeroA.name, posZeroB.name))
            }
            emit(PredicateUnary(aOnly.name, nanA.name))
            emit(PredicateBinary(PredicateOperation.And, aOnly.name, aOnly.name, nanB.name))
            emit(PredicateBinary(PredicateOperation.And, bothNan.name, nanA.name, nanB.name))
            emit(PredicateBinary(PredicateOperation.Or, chooseA.name, compare.name, zeroTie.name))
            emit(PredicateBinary(PredicateOperation.Or, chooseA.name, chooseA.name, aOnly.name))
            emit(PredicateBinary(PredicateOperation.And, chooseA.name, chooseA.name, input.name))
            if (!VMOperand.isAlwaysPredicate(args.head))
              emit(PredicateBinary(
                PredicateOperation.Or,
                predicate(args.head).name,
                chooseA.name,
                "%false"
              ))
            if (!VMOperand.isAlwaysPredicate(args(1))) {
              val unordered = temp("q")
              emit(PredicateBinary(PredicateOperation.And, unordered.name, bothNan.name, input.name))
              emit(PredicateBinary(
                PredicateOperation.Or,
                predicate(args(1)).name,
                unordered.name,
                "%false"
              ))
            }
          } else {
            val extended = modifiers.contains(Modifier.`EX`)
            if (
              args.size != (if (extended) 6 else 5) || (extended && (instruction.operation match {
                case _: SASSInstruction.Fsetp => true; case _ => false
              }))
            )
              unsupported("extended predicate comparison")
            val result = temp("q")
            emit(SetPredicate(
              result.name,
              comparisonCode(modifiers(0)),
              instruction.operation match {
                case _: SASSInstruction.Dsetp => ConversionType.F64.format
                case _: SASSInstruction.Fsetp => ConversionType.F32.format
                case _                        =>
                  if (modifiers.contains(Modifier.`U32`)) ConversionType.U32.format else ConversionType.S32.format
              },
              if (instruction.operation match { case _: SASSInstruction.Fsetp => true; case _ => false }) {
                val v = floatSource(args(2)); if (modifiers.contains(Modifier.`FTZ`)) flushFloat(v) else v
              } else if (instruction.operation match { case _: SASSInstruction.Dsetp => true; case _ => false })
                doubleSource(args(2))
              else source(args(2)),
              if (instruction.operation match { case _: SASSInstruction.Fsetp => true; case _ => false }) {
                val v = floatSource(args(3)); if (modifiers.contains(Modifier.`FTZ`)) flushFloat(v) else v
              } else if (instruction.operation match { case _: SASSInstruction.Dsetp => true; case _ => false })
                doubleSource(args(3))
              else source(args(3))
            ))
            if (extended || modifiers.contains(Modifier.`X`)) {
              val low = if (extended) predicateSource(args(5))
              else {
                val q = temp("q")
                modifiers(0) match {
                  case Modifier.`LT` =>
                    emit(PredicateBinary(
                      PredicateOperation.Or,
                      q.name,
                      readConditionFlag(VMConditionCodes.Flag.LessThan).name,
                      "%false"
                    ))
                  case Modifier.`GE` =>
                    emit(PredicateUnary(q.name, readConditionFlag(VMConditionCodes.Flag.LessThan).name))
                  case Modifier.`EQ` =>
                    emit(PredicateBinary(
                      PredicateOperation.Or,
                      q.name,
                      readConditionFlag(VMConditionCodes.Flag.Equal).name,
                      "%false"
                    ))
                  case Modifier.`NE` =>
                    emit(PredicateUnary(q.name, readConditionFlag(VMConditionCodes.Flag.Equal).name))
                  case Modifier.`LE` =>
                    emit(PredicateBinary(
                      PredicateOperation.Or,
                      q.name,
                      readConditionFlag(VMConditionCodes.Flag.LessThan).name,
                      readConditionFlag(VMConditionCodes.Flag.Equal).name
                    ))
                  case Modifier.`GT` =>
                    emit(PredicateBinary(
                      PredicateOperation.Or,
                      q.name,
                      readConditionFlag(VMConditionCodes.Flag.LessThan).name,
                      readConditionFlag(VMConditionCodes.Flag.Equal).name
                    ));
                    emit(PredicateUnary(q.name, q.name))
                  case _ => unsupported("unsupported legacy comparison")
                }
                q
              }
              modifiers(0) match {
                case Modifier.`EQ` =>
                  emit(PredicateBinary(PredicateOperation.And, result.name, result.name, low.name))
                case Modifier.`NE` =>
                  emit(PredicateBinary(PredicateOperation.Or, result.name, result.name, low.name))
                case Modifier.`LT` | Modifier.`LE` | Modifier.`GT` | Modifier.`GE` =>
                  val equal = temp("q"); val different = temp("q"); val lowResult = temp("q")
                  emit(SetPredicate(
                    equal.name,
                    Comparison.Equal,
                    ConversionType.U32.format,
                    source(args(2)),
                    source(args(3))
                  ))
                  emit(PredicateUnary(different.name, equal.name))
                  emit(PredicateBinary(PredicateOperation.And, result.name, result.name, different.name))
                  emit(PredicateBinary(PredicateOperation.And, lowResult.name, low.name, equal.name))
                  emit(PredicateBinary(PredicateOperation.Or, result.name, result.name, lowResult.name))
                case _ => unsupported("extended ordered comparison")
              }
            }
            val boolean = modifiers.find(Set(
              Modifier.`AND`,
              Modifier.`OR`,
              Modifier.`XOR`
            )).getOrElse(unsupported("missing boolean operation"))
            val input = temp("q")
            emit(PredicateBinary(
              PredicateOperation.Or,
              input.name,
              predicateSource(args(4)).name,
              "%false"
            ))
            if (args(1) != VMOperand.True && args(1) != Operand.Predicate(None, true, false)) {
              val opposite = temp("q"); emit(PredicateUnary(opposite.name, result.name))
              emit(PredicateBinary(
                predicateOperation(boolean),
                predicate(args(1)).name,
                opposite.name,
                input.name
              ))
            }
            if (args(0) != VMOperand.True && args(0) != Operand.Predicate(None, true, false))
              emit(PredicateBinary(
                predicateOperation(boolean),
                predicate(args(0)).name,
                result.name,
                input.name
              ))
          }
        case _: SASSInstruction.Prmt | _: SASSInstruction.Uprmt =>
          requireModifiers(Set.empty)
          val result = temp();
          emit(BytePermute(
            result.name,
            source(args(1)),
            source(args(3)),
            source(args(2)),
            BytePermuteMode.Default
          ));
          store(args.head, result)
        case _: SASSInstruction.Ldsm =>
          if (module.sm.raw < 75 || args.size != 2 || !VMOperand.isRegister(args.head))
            unsupported("unsupported shared matrix load operands")
          val layout = modifiers.lift(1).getOrElse(unsupported("missing shared matrix layout"))
          val count  = modifiers.drop(2) match {
            case Vec()             => 1
            case Vec(Modifier.`2`) => 2
            case Vec(Modifier.`4`) => 4
            case _                 => unsupported("unsupported shared matrix load count")
          }
          if (modifiers.lift(0) != Some(Modifier.`16`) || !Set(Modifier.`M88`, Modifier.`MT88`)(layout))
            unsupported("unsupported shared matrix load shape")
          val registers = (0 until count).map(i => destination(args.head, i)).toVec
          val pointer   = address(args(1), global = false, sharedUniform = true, sharedAccess = true)
          emit(VMTensorCompiler.loadMatrix(registers, pointer, count, layout == Modifier.MT88))
        case _: SASSInstruction.Ldgsts =>
          requireModifiers(Set(
            Modifier.`E`,
            Modifier.`BYPASS`,
            Modifier.`128`,
            Modifier.`64`,
            Modifier.`LTC128B`,
            Modifier.`ZFILL`
          ))
          val bits = if (modifiers.contains(Modifier.`128`)) 128 else if (modifiers.contains(Modifier.`64`)) 64 else 32
          val registers = Vec.fill(bits / 32)(temp())

          val src = address(args(1), true);
          val dst = address(args.head, false, sharedUniform = true, sharedAccess = true)
          if (args.size == 3) {
            val enabled = predicateSource(args(2))
            guarded(load(registers, src, 32, false, MemoryAccess.Global(false)), enabled)
            registers.foreach(r => guarded(Move(r.name, Value.Immediate(0L), 32), enabled, true))
          } else emit(load(registers, src, 32, false, MemoryAccess.Global(false)))
          emit(VMMemoryCompiler.store(dst, registers, 32, MemoryAccess.Shared))
        case _: SASSInstruction.Texdepbar =>
          requireModifiers(Set.empty)
          if (module.sm.raw >= 70 || args.size != 1 || number(args.head) < 0 || number(args.head) > 63)
            unsupported("unsupported texture dependency barrier")
        // Legacy LDG reads complete synchronously, so no texture requests remain pending.
        case _: SASSInstruction.Ldgdepbar =>
          requireModifiers(Set.empty) // Copies complete synchronously in this backend.
        case _: SASSInstruction.Depbar
            if Set(
              50,
              60
            )(module.sm.raw) && ((instruction.operation match {
              case _: SASSInstruction.Depbar => true; case _ => false
            }) && modifiers.isEmpty) &&
              scoreboardGroup(args) =>
          // The legacy mask-only form waits for these scoreboards. Memory
          // instructions have already completed synchronously in this backend.
          emit(MemoryBarrier)
        case _: SASSInstruction.Depbar =>
          requireModifiers(Set(Modifier.`LE`))
          if (
            args.size < 2 || !VMOperand.isRegister(
              args.head,
              RegisterBank.SB
            ) || number(args(1)) < 0 || number(args(1)) > 63
          )
            unsupported("unsupported dependency scoreboard")
          if (args.size > 2 && !scoreboardGroup(args.drop(2)))
            unsupported("unsupported dependency wait mask")
          // Loads, copies and arithmetic finish synchronously here, including
          // every scoreboard named by the optional wait mask.
          emit(MemoryBarrier)
        case _: SASSInstruction.Dfma | _: SASSInstruction.Dadd | _: SASSInstruction.Dmul =>
          requireModifiers(Set(Modifier.`RN`, Modifier.`RM`, Modifier.`RP`, Modifier.`RZ`))
          val inputs = args.tail.map(doubleSource(_))
          val result = temp("wide")
          emit(if (instruction.operation match { case _: SASSInstruction.Dfma => true; case _ => false })
            FusedMultiplyAdd(
              result.name,
              inputs(0),
              inputs(1),
              inputs(2),
              64,
              rounding = conversionRounding(modifiers.headOption.getOrElse(Modifier.`RN`))
            )
          else FloatBinary(
            if (instruction.operation match { case _: SASSInstruction.Dadd => true; case _ => false })
              FloatOperation.Add
            else FloatOperation.Multiply,
            result.name,
            inputs(0),
            inputs(1),
            64
          ))
          store(args.head, result, 64)
        case _: SASSInstruction.Atoms | _: SASSInstruction.Atom | _: SASSInstruction.Atomg | _: SASSInstruction.Red =>
          requireModifiers(Set(
            Modifier.`E`,
            Modifier.`ADD`,
            Modifier.`MIN`,
            Modifier.`MAX`,
            Modifier.`EXCH`,
            Modifier.`CAS`,
            Modifier.`AND`,
            Modifier.`OR`,
            Modifier.`XOR`,
            Modifier.`U32`,
            Modifier.`S32`,
            Modifier.`U64`,
            Modifier.`S64`,
            Modifier.`64`,
            Modifier.`STRONG`,
            Modifier.`GPU`,
            Modifier.`SYS`,
            Modifier.`CTA`
          ))
          val operation = modifiers.find(Set(
            Modifier.`ADD`,
            Modifier.`MIN`,
            Modifier.`MAX`,
            Modifier.`EXCH`,
            Modifier.`CAS`,
            Modifier.`AND`,
            Modifier.`OR`,
            Modifier.`XOR`
          )).getOrElse(unsupported("unknown atomic operation"))
          val bits: IntegerWidth = if (modifiers.exists(Set(Modifier.`64`, Modifier.`U64`, Modifier.`S64`))) 64 else 32
          val signed = !Set(Modifier.EXCH, Modifier.CAS, Modifier.AND, Modifier.OR, Modifier.XOR)(operation) &&
            (modifiers.exists(Set(
              Modifier.S32,
              Modifier.S64
            )) || (!modifiers.exists(Set(Modifier.U32, Modifier.U64)) && Set(Modifier.MIN, Modifier.MAX)(operation)))
          // ATOMG adds a predicate destination to the ordinary global atomic operands.
          // Only the compiler's discarded predicate result is currently supported.
          if (
            (instruction.operation match {
              case _: SASSInstruction.Atomg => true; case _ => false
            }) && args.headOption != Some(VMOperand.True)
          )
            unsupported("atomic predicate result is not implemented")
          val atomicArgs = if (instruction.operation match { case _: SASSInstruction.Atomg => true; case _ => false })
            args.tail
          else args
          val first = if (instruction.operation match { case _: SASSInstruction.Red => true; case _ => false }) 0 else 1
          val pointer = address(
            atomicArgs(first),
            !(instruction.operation match { case _: SASSInstruction.Atoms => true; case _ => false }),
            sharedAccess = instruction.operation match { case _: SASSInstruction.Atoms => true; case _ => false }
          )
          val value       = source(atomicArgs(first + 1), bits); val result = temp(if (bits.raw == 64) "wide" else "t")
          val atomicSpace =
            if (instruction.operation match { case _: SASSInstruction.Atoms => true; case _ => false })
              AtomicSpace.Shared
            else AtomicSpace.Global
          val width = bits
          emit(operation match {
            case Modifier.`CAS` => AtomicCompareExchange(
                result.name,
                pointer,
                value,
                source(atomicArgs(first + 2), bits),
                width,
                atomicSpace
              )
            case Modifier.`EXCH` => AtomicExchange(result.name, pointer, value, width, atomicSpace)
            case Modifier.`ADD`  => AtomicAdd(result.name, pointer, value, width, atomicSpace)
            case _               => AtomicBinary(
                result.name,
                pointer,
                value,
                width,
                atomicSpace,
                integerOperation(operation, signed)
              )
          })
          if (!(instruction.operation match { case _: SASSInstruction.Red => true; case _ => false }))
            store(atomicArgs.head, result, bits)
        case _: SASSInstruction.Fmul
            if modifiers.exists(Set(
              Modifier.`RZ`,
              Modifier.`RP`,
              Modifier.`RM`
            )) && !modifiers.contains(Modifier.`FTZ`) =>
          requireModifiers(Set(Modifier.`RZ`, Modifier.`RP`, Modifier.`RM`))
          val a = temp("wide"); val b = temp("wide"); val product = temp("wide"); val result = temp()
          // The product of two binary32 significands is exact in binary64.
          emit(Convert(a.name, floatSource(args(1)), ConversionType.F32, ConversionType.F64, None));
          emit(Convert(b.name, floatSource(args(2)), ConversionType.F32, ConversionType.F64, None))
          emit(FloatBinary(FloatOperation.Multiply, product.name, a, b, 64))
          emit(Convert(
            result.name,
            product,
            ConversionType.F64,
            ConversionType.F32,
            Some(conversionRounding(modifiers(0)))
          ))
          store(args.head, result)
        case _: SASSInstruction.Ffma | _: SASSInstruction.Fadd | _: SASSInstruction.Fmul =>
          requireModifiers(if (instruction.operation match { case _: SASSInstruction.Ffma => true; case _ => false })
            Set(Modifier.`RN`, Modifier.`RM`, Modifier.`RP`, Modifier.`RZ`, Modifier.`FTZ`)
          else Set(Modifier.`RN`, Modifier.`FTZ`))
          val operands = args.tail.map { raw =>
            val v = floatSource(raw); if (modifiers.contains(Modifier.`FTZ`)) flushFloat(v) else v
          }
          val result = temp()
          emit(if (instruction.operation match { case _: SASSInstruction.Ffma => true; case _ => false })
            FusedMultiplyAdd(
              result.name,
              operands(0),
              operands(1),
              operands(2),
              32,
              rounding = conversionRounding(modifiers.find(Set(
                Modifier.`RN`,
                Modifier.`RM`,
                Modifier.`RP`,
                Modifier.`RZ`
              )).getOrElse(Modifier.RN))
            )
          else FloatBinary(
            if (instruction.operation match { case _: SASSInstruction.Fadd => true; case _ => false })
              FloatOperation.Add
            else FloatOperation.Multiply,
            result.name,
            operands(0),
            operands(1),
            32
          ))
          store(args.head, if (modifiers.contains(Modifier.`FTZ`)) flushFloat(result) else result)
        case _: SASSInstruction.Ldg | _: SASSInstruction.Stg | _: SASSInstruction.Lds | _: SASSInstruction.Sts | _: SASSInstruction.Ld | _: SASSInstruction.St | _: SASSInstruction.Ldl | _: SASSInstruction.Stl =>
          if (
            modifiers.contains(Modifier.`32`) && (!Set(
              50,
              60
            )(module.sm.raw) || !((instruction.operation match {
              case _: SASSInstruction.Lds => true; case _ => false
            }) && modifiers == Vec(
              Modifier.`U`,
              Modifier.`32`
            )))
          )
            unsupported("unsupported explicit 32-bit memory form")
          requireModifiers(Set(
            Modifier.`E`,
            Modifier.`32`,
            Modifier.`64`,
            Modifier.`128`,
            Modifier.`U`,
            Modifier.`U8`,
            Modifier.`S8`,
            Modifier.`U16`,
            Modifier.`S16`,
            Modifier.`STRONG`,
            Modifier.`GPU`,
            Modifier.`SYS`,
            Modifier.`CTA`,
            Modifier.`SM`,
            Modifier.`EF`,
            Modifier.`T`,
            Modifier.`P`,
            Modifier.`LU`,
            Modifier.`CI`,
            Modifier.`CG`,
            Modifier.`CS`,
            Modifier.`CV`,
            Modifier.`WT`,
            Modifier.`EL`,
            Modifier.`LTC128B`,
            Modifier.`CONSTANT`
          ))
          if (
            modifiers.contains(Modifier.`U`) && !(instruction.operation match {
              case _: SASSInstruction.Lds => true; case _ => false
            })
          )
            unsupported("uniform-address modifier is supported only for LDS")
          if (
            modifiers.contains(
              Modifier.`P`
            ) && (!(instruction.operation match {
              case _: SASSInstruction.Ldg => true; case _ => false
            }) || module.sm.raw >= 70)
          )
            unsupported("legacy texture load modifier on non-legacy LDG")
          val load = instruction.operation match {
            case _: SASSInstruction.Ld | _: SASSInstruction.Ldg | _: SASSInstruction.Ldl | _: SASSInstruction.Lds =>
              true
            case _ => false
          }
          val global = instruction.operation match {
            case _: SASSInstruction.Ldg | _: SASSInstruction.Stg => true; case _ => false
          }
          val generic =
            instruction.operation match { case _: SASSInstruction.Ld | _: SASSInstruction.St => true; case _ => false }
          if (generic && Set(50, 60)(module.sm.raw) && args.size != 2)
            unsupported("legacy generic-memory auxiliary predicate requires a verified address sequence")
          val bits = if (modifiers.contains(Modifier.`128`)) 128
          else if (modifiers.contains(Modifier.`64`)) 64
          else if (modifiers.exists(Set(Modifier.`U8`, Modifier.`S8`))) 8
          else if (modifiers.exists(Set(Modifier.`U16`, Modifier.`S16`))) 16 else 32

          val pointer = address(
            args(if (load) 1 else 0),
            global || generic,
            sharedUniform =
              (instruction.operation match {
                case _: SASSInstruction.Lds => true; case _ => false
              }) || ((instruction.operation match {
                case _: SASSInstruction.Sts => true; case _ => false
              }) && (bits == 32 || modifiers.exists(Set(
                Modifier.`U8`,
                Modifier.`128`
              )))),
            sharedAccess = instruction.operation match {
              case _: SASSInstruction.Lds | _: SASSInstruction.Sts => true; case _ => false
            },
            allowGlobalU32UniformBase =
              (instruction.operation match { case _: SASSInstruction.Ldg => true; case _ => false }) ||
                ((instruction.operation match {
                  case _: SASSInstruction.Stg => true; case _ => false
                }) && modifiers.filterNot(
                  _ == Modifier.STRONG
                ) == Vec(Modifier.E, Modifier.U8, Modifier.SYS))
          )
          val reg             = args(if (load) 0 else 1)
          val resultRegisters = if (!load) Vec.empty[Value.Register]
          else if (VMOperand.isZero(reg)) {
            Vec.fill(math.max(1, bits / 32))(temp("discard"))
          } else (0 until math.max(1, bits / 32)).map(i => destination(reg, i)).toVec
          val registers = if (load) resultRegisters
          else
            (0 until math.max(1, bits / 32)).map(i => register(reg, i)).toVec
          val system = if (!modifiers.contains(Modifier.`STRONG`)) false
          else {
            if (!global && !generic) unsupported("STRONG memory scope requires a generic or global memory instruction")
            val scope = modifiers.filter(Set(Modifier.`CTA`, Modifier.`GPU`, Modifier.`SYS`, Modifier.`SM`))
            if (scope.size != 1) unsupported("STRONG memory instruction requires exactly one scope")
            scope.head == Modifier.SYS
          }
          val space = if (generic) MemoryAccess.Generic(system)
          else if (global) MemoryAccess.Global(system)
          else if (
            instruction.operation match {
              case _: SASSInstruction.Ldl | _: SASSInstruction.Stl => true; case _ => false
            }
          ) MemoryAccess.Local
          else MemoryAccess.Shared
          val elementWidth = math.min(bits, 32).refine[IntegerWidth].fold(unsupported(_), identity)
          val operation    = if (load)
            VMMemoryCompiler.load(
              resultRegisters,
              pointer,
              elementWidth,
              bits < 32 && modifiers.exists(Set(Modifier.`S8`, Modifier.`S16`)),
              space
            )
          else VMMemoryCompiler.store(pointer, registers, elementWidth, space)
          if (load && args.size == 3) {
            val enabled = predicateSource(args(2))
            guarded(operation, enabled)
            if (!VMOperand.isZero(reg))
              resultRegisters.foreach(value =>
                guarded(Move(value.name, Value.Immediate(0L), 32), enabled, true)
              )
          } else {
            if (args.size != 2) unsupported("unsupported memory operands")
            emit(operation)
          }
        case _ => unsupported("SASS instruction is not implemented")
      }
      if (instruction.operands.headOption.exists(VMOperand.has(_, OperandSelector.`CC`))) {
        if (ccFlagsSupported) writeConditionFlags()
        else if (ccZeroFlagsSupported)
          emit(WriteConditionFlag(VMConditionCodes.Flag.Zero, Value.Predicate("%cc_z")))
        if (
          !ccFlagsSupported && (instruction.operation match {
            case _: SASSInstruction.Iadd | _: SASSInstruction.Iadd32i | _: SASSInstruction.Isub |
                _: SASSInstruction.Iscadd | _: SASSInstruction.Imad => true
            case _ => false
          })
        ) {
          val carry = temp()
          emit(IntegerAddWithCarry(carry.name, Value.Immediate(0L), Value.Immediate(0L), 32, true, false))
          emit(WriteConditionFlag(VMConditionCodes.Flag.Carry, carry))
        }
        if (
          !ccFlagsSupported && (instruction.operation match {
            case _: SASSInstruction.Iadd | _: SASSInstruction.Iadd32i | _: SASSInstruction.Isub |
                _: SASSInstruction.Iscadd => true
            case _ => false
          })
        ) {
          emit(WriteConditionFlag(VMConditionCodes.Flag.Equal, Value.Predicate("%cc_eq")))
          emit(WriteConditionFlag(VMConditionCodes.Flag.LessThan, Value.Predicate("%cc_lt")))
        }
        VMConditionCodes.Flag.values.filterNot(writtenFlags.contains(_)).foreach { flag =>
          if (
            flag != VMConditionCodes.Flag.Zero || !(instruction.operation match {
              case _: SASSInstruction.Iset => true; case _ => false
            })
          )
            emit(InvalidateConditionFlag(flag))
        }
        if (
          !((instruction.operation match { case _: SASSInstruction.Iadd3 => true; case _ => false }) && Set(
            50,
            60
          )(module.sm.raw))
        )
          emit(InvalidateAddThreeCarry)
      }
      val pred       = instruction.guard.map(_.predicate)
      val guardSetup = pred.toVec.map { p =>
        val value = predicate(p.copy(negated = false))
        Execute(if (p.negated) PredicateUnary("%guard", value.name)
        else PredicateBinary(PredicateOperation.Or, "%guard", value.name, value.name))
      }
      val body =
        if (
          pred.isDefined && ((instruction.operation match {
            case _: SASSInstruction.Ret | _: SASSInstruction.Bar => true; case _ => false
          }) || code.exists {
            case Execute(_, Some(_)) | VMAssembler.Statement.Branch(_, Some(_)) => true
            case _                                                              => false
          })
        ) {
          val end: NonEmpty[String] = s"REnd${instruction.address.raw.toHexString}"
          Vec(VMAssembler.Statement.Branch(end, Some(ExecutionPredicate("%guard", true)))) ++ code.toVec :+ Label(end)
        } else code.toVec.map {
          case statement: Execute if pred.isDefined =>
            statement.copy(predication = Some(ExecutionPredicate("%guard", false)))
          case statement: VMAssembler.Statement.Branch if pred.isDefined =>
            statement.copy(predication = Some(ExecutionPredicate("%guard", false)))
          case statement => statement
        }
      Vec(Label(s"L${instruction.address.raw.toHexString}")) ++ guardSetup ++ body
    }
  }
}
