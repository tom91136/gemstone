package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMAssembler.Statement.*
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
  private def fail(detail: String): Nothing = throw IllegalArgumentException(detail)

  private def codeTarget(operand: Operand): Int =
    OperandQueries.integer(operand).filter(_.isValidInt).fold(fail(s"expected code address, found $operand"))(_.toInt)

  /** Legacy branch destinations name a bundle's control word, which executes with the instruction after it. */
  private def resolveLegacyTargets(generation: Generation, function: DecodedFunction): DecodedFunction =
    if (generation == Generation.Modern) function
    else {
      val addresses = function.instructions.map(_.address.raw).toSet
      function.copy(instructions = function.instructions.map { instruction =>
        if (!ControlFlow.directTarget(instruction.operation)) instruction
        else {
          val operands = instruction.operands.map { operand =>
            OperandQueries.integer(operand) match {
              case Some(target)
                  if target.isValidInt && target >= 0 && target % generation.bundleBytes.raw == 0 &&
                    !addresses(target.toInt) && addresses(target.toInt + 8) => Operand.Integer(target + 8)
              case _ => operand
            }
          }
          if (operands == instruction.operands) instruction else instruction.rewrite(operands = operands)
        }
      })
    }

  def compile(
      module: Cubin.Module,
      kernel: Cubin.Kernel,
      functions: Vec[DecodedFunction],
      globalSymbols: Map[NonEmpty[String], GuestAddress] = Map.empty
  ): Either[NonEmpty[String], VMIR.Kernel] =
    lowerKernel(module, kernel, functions, globalSymbols).flatMap { (statements, registerCount) =>
      VMAssembler.assemble(statements).map(_ -> registerCount)
    }.map { (program, registerCount) =>
      val constants = module.sections.find(_.name == s".nv.constant0.${kernel.name}").fold(Vec.empty[Byte])(_.bytes)
      val block     = constants.slice(kernel.parameterBase.raw, kernel.parameterBase.raw + kernel.parameterSize.raw)
      VMIR.Kernel(
        Entry(
          Routine(kernel.name, program, registerCount, VMRegisters.PredicateCount),
          kernel.parameters.map(parameter =>
            KernelParameter(s"param${parameter.ordinal}", parameter.offset, parameter.size)
          ),
          block.padTo(kernel.parameterSize.raw, 0.toByte)
        ),
        "SASS"
      )
    }

  /** The kernel's statements and the register slots its lanes need. */
  def lowerKernel(
      module: Cubin.Module,
      kernel: Cubin.Kernel,
      functions: Vec[DecodedFunction],
      boundSymbols: Map[NonEmpty[String], GuestAddress]
  ): Either[NonEmpty[String], (Vec[VMAssembler.Statement], Int >= 0)] = try {
    val generation = Generation.of(module.sm)
    if (kernel.requiredCluster != LaunchGeometry.Dimensions.One || kernel.explicitCluster)
      fail("kernel requires unsupported thread-block cluster launch geometry")
    module.sections.filter(_.isRelocation).foreach { table =>
      if (Cubin.relocationEntries(table).isLeft) fail("invalid cubin relocations")
      val target = module.sections.lift(table.info)
      if (table.size.raw != 0 && target.exists(_.name == s".nv.constant0.${kernel.name}"))
        fail("relocations in launch parameter constants are not implemented")
      if (table.size.raw != 0 && target.exists(_.name.startsWith(".nv.global")))
        fail("relocations in kernel code, constants or globals are not implemented")
    }
    val original        = functions.find(_.name == kernel.name).getOrElse(fail(s"missing SASS for ${kernel.name}"))
    val textRelocations = Cubin.TextRelocation.resolve(module, kernel, original, boundSymbols.keySet)
      .fold(error => fail(error.raw), identity)
    val function     = LegacyAddressRewrite(module.sm.raw, resolveLegacyTargets(generation, original))
    val instructions = function.instructions
    val layout       = VMRegisters.Layout.of(kernel, functions)
    val reductions   = LegacyRangeReduction.inputs(module.sm.raw, function, layout)
    def is[A <: SASSInstruction](instruction: DecodedInstruction)(using scala.reflect.TypeTest[SASSInstruction, A]) =
      OperandQueries.is[A](instruction.operation)
    def at(address: Int): Option[DecodedInstruction] = instructions.find(_.address.raw == address)
    val collectiveEnds                               = instructions.filter(i =>
      is[SASSInstruction.Warpsync](i) &&
        (i.modifiers == Vec(Modifier.COLLECTIVE) || i.modifiers == Vec(Modifier.COLLECTIVE, Modifier.ALL))
    ).map { setup =>
      val target = (setup.guard, setup.modifiers.contains(Modifier.ALL), setup.operands) match {
        case (None, true, Vec(end))     => codeTarget(end)
        case (None, false, Vec(_, end)) => codeTarget(end)
        case _ => fail("collective warp synchronisation requires an unguarded mask and end target")
      }
      if (
        target <= setup.address.raw ||
        !at(target - 16).exists(i => i.guard.isEmpty && is[SASSInstruction.Endcollective](i))
      ) fail("collective warp target must immediately follow ENDCOLLECTIVE")
      target - 16
    }.toSet
    instructions.filter(is[SASSInstruction.Endcollective]).foreach { end =>
      if (!collectiveEnds(end.address.raw)) fail("ENDCOLLECTIVE has no matching warp synchronisation")
    }
    // BSYNC falls through to the setup target; BMOV may restore the token into a different B register.
    instructions.filter(is[SASSInstruction.Bssy]).foreach { setup =>
      val target = setup.operands match {
        case Vec(token, end) if OperandQueries.isRegister(token, RegisterBank.B) => codeTarget(end)
        case _ => fail("invalid convergence barrier setup")
      }
      if (target <= setup.address.raw || !at(target - 16).exists(is[SASSInstruction.Bsync]))
        fail("convergence barrier target must be forward and immediately after its BSYNC")
    }
    val legacyControl                   = Option.when(generation != Generation.Modern)(LegacyControlFlow(instructions))
    val legacyTokens                    = legacyControl.fold(Vec.empty[LegacyControlFlow.Setup])(_.setups)
    val addresses                       = instructions.map(_.address.raw).sorted
    def next(address: Int): Option[Int] = addresses.find(_ > address)
    val returnBases                     = instructions.filter(is[SASSInstruction.Lepc]).map(_.address.raw) ++
      instructions.filter(is[SASSInstruction.Call]).map(_.address.raw + generation.instructionBytes.raw) ++
      instructions.filter(is[SASSInstruction.Cal]).map(i =>
        next(i.address.raw).getOrElse(fail("call has no return instruction"))
      ) ++ instructions.filter(is[SASSInstruction.Pret]).map(i =>
        i.operands.headOption.fold(fail("PRET has no return target"))(codeTarget(_))
      )
    val lowered = instructions.flatMap { instruction =>
      try {
        val setup      = legacyTokens.find(_.instruction.address == instruction.address)
        val syncSuffix = generation != Generation.Modern && instruction.modifiers.lastOption.contains(Modifier.S)
        val transfer   = legacyControl.flatMap(_.transfer(instruction))
        // Some legacy entry points end with RET; EXIT despite having no call sites or return tokens. This terminal
        // entry epilogue ends the launch; it must not consume a device-call return address.
        val entryReturn = generation != Generation.Modern && returnBases.isEmpty && instruction.operands.isEmpty &&
          is[SASSInstruction.Ret](instruction) &&
          next(instruction.address.raw).flatMap(at(_)).exists(i => is[SASSInstruction.Exit](i) && i.guard.isEmpty)
        val normalised = setup match {
          case Some(LegacyControlFlow.Setup(_, _, slot, target)) =>
            instruction.rewrite(SASSInstruction.BssyRegImm(
              InstructionOperand.Register.from(Operand.Register(RegisterBank.B, slot)).fold(fail(_), identity),
              InstructionOperand.Immediate.from(Operand.Integer(target.raw)).fold(fail(_), identity),
              pred = instruction.guard
            ))
          case None if syncSuffix        => instruction.rewrite(modifiers = instruction.modifiers.dropRight(1))
          case None if transfer.nonEmpty => instruction.rewrite(SASSInstruction.NopNoOperands(pred = instruction.guard))
          case None if entryReturn => instruction.rewrite(SASSInstruction.ExitNoOperands(pred = instruction.guard))
          case None                => instruction
        }
        val never  = LegacyControlFlow.never(instruction)
        val branch = transfer.toVec.flatMap { token =>
          val predication =
            instruction.guard.map(_ => ExecutionPredicate(VMRegisters.Predicate.Guard.slot, negated = false))
          val unwind = legacyControl.toVec.flatMap(_.unwound(instruction).map(enclosed =>
            Execute(Convergence(layout.barrier(enclosed.slot), ConvergenceAction.Break), predication)
          ))
          unwind :+ VMAssembler.Statement.Branch(s"L${token.target.raw.toHexString}", predication)
        }
        val statements =
          if (never) Vec(Label(s"L${instruction.address.raw.toHexString}"))
          else
            InstructionCompiler(
              module,
              kernel,
              normalised,
              returnBases,
              addresses,
              boundSymbols,
              reductions.inputs.get(instruction.address.raw),
              textRelocations.get(instruction.address),
              layout
            ).compile() ++ branch
        val arrivals = legacyTokens.filter(_.target == instruction.address).reverse.map(token =>
          Execute(Convergence(layout.barrier(token.slot), ConvergenceAction.Synchronise))
        )
        VMAssembler.Statement.Source(instruction.address.raw.toLong.unsafe[Long >= 0]) +:
          (statements.take(1) ++ arrivals ++ statements.drop(1))
      } catch {
        case NonFatal(error) => fail(
            s"${kernel.name}+0x${instruction.address.raw.toHexString} (sm_${module.sm.raw}): ${(Vec(instruction.mnemonic.raw) ++ instruction.modifiers.map(_.name)).mkString(".")} ${instruction.operands.mkString(", ")}: ${error.getMessage}"
          )
      }
    }
    val zero = Value.Immediate(0L)
    Right(
      Vec(
        Execute(SetPredicate(
          VMRegisters.Predicate.True.slot,
          Comparison.Equal,
          NumericFormat.Unsigned(32),
          zero,
          zero
        )),
        Execute(SetPredicate(
          VMRegisters.Predicate.False.slot,
          Comparison.NotEqual,
          NumericFormat.Unsigned(32),
          zero,
          zero
        ))
      ) ++ lowered -> layout.registerCount(reductions.count)
    )
  } catch { case NonFatal(error) => Left(throwables.detail(error)) }

  private class InstructionCompiler(
      val module: Cubin.Module,
      val kernel: Cubin.Kernel,
      val instruction: DecodedInstruction,
      val returnBases: Vec[Int],
      val addresses: Vec[Int],
      val boundSymbols: Map[NonEmpty[String], GuestAddress],
      val reducedRadians: Option[Value.Register],
      val textRelocation: Option[Cubin.TextRelocation],
      val registerLayout: VMRegisters.Layout
  ) extends VMCompilationContext {
    private val code         = ArrayBuffer.empty[VMAssembler.Statement]
    private val writtenFlags = scala.collection.mutable.Set.empty[VMConditionCodes.Flag]
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var temporaryCount = 0

    // The generated SCO vocabulary uses VC as an alias of GPU scope.
    val modifiers: Vec[Modifier] = instruction.modifiers.map {
      case Modifier.VC => Modifier.GPU
      case other       => other
    }

    private def nextTemporary(): Int = {
      if (temporaryCount >= VMRegisters.TemporaryCount) unsupported("instruction temporary limit exceeded")
      temporaryCount += 1
      temporaryCount - 1
    }
    def temp(): Value.Register           = Value.Register(registerLayout.temporary(nextTemporary()))
    def predicateTemp(): Value.Predicate = Value.Predicate(VMRegisters.temporaryPredicate(nextTemporary()))

    def emit(value: VMIR.Instruction): Unit = {
      value match {
        case WriteConditionFlag(flag, _) => writtenFlags += flag
        case _                           => ()
      }
      code += Execute(value)
    }
    def guarded(value: VMIR.Instruction, predicate: Value.Predicate, negated: Boolean = false): Unit =
      code += Execute(value, Some(ExecutionPredicate(predicate.slot, negated)))
    def branch(target: Long, predicate: Option[ExecutionPredicate] = None): Unit =
      code += VMAssembler.Statement.Branch(label(target), predicate)

    def dispatch(target: Value.Register, destinations: Vec[Int]): Unit = {
      val known   = destinations.toSet
      val targets = destinations.flatMap { destination =>
        val control = destination - 8
        val alias   = Option.when(
          legacy && control >= 0 && control % generation.bundleBytes.raw == 0 && !known(control)
        )(control -> destination)
        Vec(destination -> destination) ++ alias.toVec
      }
      targets.foreach { (address, destination) =>
        val matches = compare(Comparison.Equal, NumericFormat.Unsigned(32), target, Value.Immediate(address.toLong))
        branch(destination, Some(ExecutionPredicate(matches.slot, negated = false)))
        temporaryCount -= 1
      }
      emit(Trap)
    }

    def readConditionFlag(flag: VMConditionCodes.Flag): Value.Predicate = {
      val result = predicateTemp()
      emit(ReadConditionPredicate(result.slot, flag))
      result
    }

    def writeConditionFlags(): Unit = VMConditionCodes.Flag.values.foreach(flag =>
      emit(WriteConditionFlag(flag, Value.Predicate(VMRegisters.condition(flag))))
    )

    def store(target: Operand, value: Value, bits: ScalarBitWidth = 32): Unit =
      if (!OperandQueries.isZero(target)) {
        val uniform = OperandQueries.base(target) match {
          case Operand.Register(RegisterBank.UR, _) => true
          case _                                    => false
        }
        if (bits.raw == 64 && uniform) {
          val words = Vec(temp(), temp())
          emit(MoveSplit(words.map(word => Some(word.slot)), value, 32))
          words.zipWithIndex.foreach((word, index) => emit(UniformMove(destination(target, index).slot, word)))
        } else if (bits.raw == 64)
          emit(MoveSplit(Vec(destination(target), destination(target, 1)).map(word => Some(word.slot)), value, 32))
        else if (uniform) emit(UniformMove(destination(target).slot, value))
        else emit(Move(destination(target).slot, value, 32))
      }

    def source(raw: Operand, bits: ScalarBitWidth = 32): Value =
      OperandQueries.without(raw, OperandSelector.`reuse`) match {
        case value: Operand.Register =>
          if (bits.raw == 64 && !OperandQueries.isZero(value)) pair(value) else register(value)
        case Operand.ConstantBank(bank, Operand.Address(Some(base), None, displacement, scale, None, None))
            if scale.raw == 1 && OperandQueries.isZero(base) =>
          source(Operand.ConstantBank(bank, Operand.Integer(displacement)), bits)
        case Operand.ConstantBank(bank, offset: Operand.Address) =>
          VMConstantBank.dynamic(bank, offset, bits)(using this)
        case Operand.ConstantBank(bank, offset) => VMConstantBank.static(bank, number(offset), bits)(using this)
        case Operand.Unary(inner, negative, complement, false) =>
          val value  = source(inner, bits)
          val result = temp()
          if (complement) emit(IntegerUnary(IntegerUnaryOperation.BitwiseNot, result.slot, value, bits))
          else emit(Move(result.slot, value, bits))
          if (negative) emit(IntegerUnary(IntegerUnaryOperation.Negate, result.slot, result, bits))
          result
        case value @ (Operand.Integer(_) | Operand.Bits(_, _)) => literal(value)
        case other                                             => unsupported(s"unsupported source $other")
      }

    def floatSource(raw: Operand): Value  = floatingSource(raw, 32)
    def doubleSource(raw: Operand): Value = floatingSource(raw, 64)

    private def floatingSource(raw: Operand, width: Int in (32, 64)): Value =
      OperandQueries.without(raw, OperandSelector.`reuse`) match {
        case Operand.Integer(value) =>
          Value.Immediate(if (width.raw == 32) IEEE754.floatToBinary32(value.toFloat).toLong
          else IEEE754.fromDouble(value.toDouble, 64))
        case Operand.Floating(value, _, negativeZero) =>
          Value.Immediate(
            if (width.raw == 32) IEEE754.floatToBinary32(if (negativeZero) -0.0f else value.toFloat).toLong
            else IEEE754.fromDouble(if (negativeZero) -0.0 else value.toDouble, 64)
          )
        case Operand.FloatingBits(_, encodedWidth, bits) if encodedWidth.raw == width.raw =>
          Value.Immediate(bits.toLong)
        case Operand.FloatingSymbol(name) =>
          val value = name match {
            case FloatingConstant.INF                         => Double.PositiveInfinity
            case FloatingConstant.QNAN | FloatingConstant.NAN => Double.NaN
            case _                                            => unsupported(s"unsupported floating immediate $name")
          }
          Value.Immediate(IEEE754.fromDouble(value, width))
        case Operand.Unary(inner, negative, false, absolute) =>
          val value  = floatingSource(inner, width)
          val sign   = if (width.raw == 64) Long.MinValue else 0x80000000L
          val result = binary(IntegerOperation.BitwiseAnd, value, Value.Immediate(if (absolute) ~sign else -1L), width)
          if (negative)
            emit(IntegerBinary(IntegerOperation.BitwiseXor, result.slot, result, Value.Immediate(sign), width))
          result
        case other => source(other, width)
      }

    def fselSource(raw: Operand, index: Int): Value = raw match {
      case Operand.FloatingSymbol(name) if name.isNaN =>
        if (legacy) unsupported("raw FSEL NaN immediates are only verified for modern cubins")
        if (index != 2) unsupported("FSEL NaN immediate is only verified in the second source")
        val word =
          instruction.encodingWord.getOrElse(unsupported("FSEL NaN immediate requires raw instruction encoding")).toLong
        if ((word & 0xfffL) != 0x808L) unsupported("FSEL NaN source is not encoded as an immediate")
        val bits = (word >>> 32) & 0xffffffffL
        if ((bits & 0x7f800000L) != 0x7f800000L || (bits & 0x007fffffL) == 0)
          unsupported("FSEL NaN spelling does not match encoded source bits")
        Value.Immediate(bits)
      case other => floatSource(other)
    }

    def address(
        raw: Operand,
        global: Boolean,
        sharedUniform: Boolean = false,
        sharedAccess: Boolean = false,
        allowGlobalU32UniformBase: Boolean = false
    ): Value = raw match {
      case operand: Operand.Address =>
        val result      = temp()
        val mode        = operand.mode
        val uniformBase =
          if (operand.index.isEmpty) operand.base.filter(_.bank == RegisterBank.UR)
          else if (operand.base.exists(OperandQueries.isZero)) operand.index.filter(_.bank == RegisterBank.UR)
          else None
        def add(offset: Value): Unit = emit(IntegerBinary(IntegerOperation.Add, result.slot, result, offset, 64))
        if (mode.contains(AddressMode.U32) && uniformBase.nonEmpty) {
          val base = uniformBase.get
          if (!global || sm < 75 || base.index >= 255 || operand.displacement != 0 || operand.scale.raw != 1)
            unsupported("unsupported uniform-only .U32 global address")
          emit(MoveCombine(result.slot, Vec(register(base), register(base, 1)), 32))
        } else if (mode.contains(AddressMode.U32)) {
          val base =
            operand.base.getOrElse(unsupported("unsupported .U32 uniform global address: missing general base"))
          val index =
            operand.index.getOrElse(unsupported("unsupported .U32 uniform global address: missing uniform base"))
          if (
            !global || !allowGlobalU32UniformBase || sm != 75 || base.bank != RegisterBank.R ||
            index.bank != RegisterBank.UR || index.index >= 255 || operand.displacement != 0
          ) unsupported("unsupported .U32 uniform global address")
          emit(MoveCombine(result.slot, Vec(register(index), register(index, 1)), 32))
          add(widen(register(base)))
        } else {
          operand.base match {
            case Some(base) if global && !OperandQueries.isZero(base) =>
              emit(MoveCombine(result.slot, Vec(register(base), register(base, 1)), 32))
            case Some(base) =>
              emit(Convert(result.slot, register(base), NumericFormat.Unsigned(32), NumericFormat.Unsigned(64), None))
            case None if !global => emit(Move(result.slot, Value.Immediate(0L), 64))
            case _               => unsupported("absolute global address is unsupported")
          }
          if (!Set(1, 4, 8, 16)(operand.scale.raw)) unsupported("unsupported address scale")
          if (operand.scale.raw != 1)
            emit(IntegerBinary(
              IntegerOperation.ShiftLeft,
              result.slot,
              result,
              Value.Immediate(Integer.numberOfTrailingZeros(operand.scale.raw).toLong),
              64
            ))
          operand.index.foreach { index =>
            if (index.bank != RegisterBank.UR) unsupported("only uniform address offsets are supported")
            if (global) {
              if (!mode.contains(AddressMode.`64`) || index.index >= 255)
                unsupported("uniform offset requires a 64-bit global base and an in-range register pair")
              add(pair(index))
            } else {
              val verified =
                sm >= 80 ||
                  (sm == 75 && OperandQueries.is[SASSInstruction.Lds](operation) && modifiers.contains(Modifier.`U`))
              if (!sharedUniform || !verified || mode.contains(AddressMode.`64`))
                unsupported("uniform offsets on this non-global address form are not implemented")
              add(widen(register(index)))
            }
          }
          if (operand.displacement != 0) add(literal(Operand.Integer(operand.displacement)))
          if (!global && operand.index.nonEmpty)
            emit(Convert(result.slot, narrow(result), NumericFormat.Unsigned(32), NumericFormat.Unsigned(64), None))
        }
        if (sharedAccess)
          emit(IntegerBinary(
            IntegerOperation.BitwiseOr,
            result.slot,
            result,
            Value.Immediate(VMAddressSpace.SharedBase),
            64
          ))
        result
      case _ => unsupported(s"unsupported address $raw")
    }

    def updateIntegerCC(lhs: Value, rhs: Value, result: Value, isSub: Boolean, carry: Option[Value]): Unit = {
      if (isSub)
        emit(SetPredicate(
          VMRegisters.Predicate.Carry.slot,
          Comparison.GreaterThanOrEqual,
          NumericFormat.Unsigned(32),
          lhs,
          rhs
        ))
      else
        emit(SetPredicate(
          VMRegisters.Predicate.Carry.slot,
          Comparison.NotEqual,
          NumericFormat.Unsigned(32),
          carry.getOrElse(unsupported("missing integer carry")),
          Value.Immediate(0L)
        ))
      emit(SetPredicate(
        VMRegisters.Predicate.Zero.slot,
        Comparison.Equal,
        NumericFormat.Unsigned(32),
        result,
        Value.Immediate(0L)
      ))
      emit(SetPredicate(
        VMRegisters.Predicate.Sign.slot,
        Comparison.LessThan,
        NumericFormat.Signed(32),
        result,
        Value.Immediate(0L)
      ))
      def negative(value: Value) = compare(Comparison.LessThan, NumericFormat.Signed(32), value, Value.Immediate(0L))
      val signL                  = negative(lhs)
      val signR                  = negative(rhs)
      val signOut                = negative(result)
      val operandSignsDiffer     = combine(PredicateOperation.Xor, signL, signR)
      val resultSignDiffers      = combine(PredicateOperation.Xor, signL, signOut)
      val overflowSigns          = if (isSub) operandSignsDiffer else invert(operandSignsDiffer)
      copyPredicate(
        Value.Predicate(VMRegisters.Predicate.Overflow.slot),
        combine(PredicateOperation.And, overflowSigns, resultSignDiffers)
      )
      copyPredicate(Value.Predicate(VMRegisters.Predicate.Equal.slot), Value.Predicate(VMRegisters.Predicate.Zero.slot))
      emit(PredicateUnary(VMRegisters.Predicate.LessThan.slot, VMRegisters.Predicate.Carry.slot))
    }

    lazy val ccFlagsSupported: Boolean = VMConditionCodeForms.allFlags(using this)

    private def lowered: Boolean =
      VMFloatingCompiler.lower(using this) || VMIntegerCompiler.lower(using this) ||
        VMPredicateCompiler.lower(using this) || VMTensorCompiler.lower(using this) ||
        VMMemoryCompiler.lower(using this) || VMControlCompiler.lower(using this)

    def compile(): Vec[VMAssembler.Statement] = {
      if (modifiers.exists { case Modifier.Vendor(_) => true; case _ => false })
        unsupported("unsupported vendor modifier")
      if (operands.exists(OperandQueries.hasUnknownDomain(_))) unsupported("unsupported vendor operand domain")
      if (legacy && modifiers.contains(Modifier.X) && VMConditionCodeForms.carryConsumer(operation))
        seedCarry(readConditionFlag(VMConditionCodes.Flag.Carry))
      if (!lowered) unsupported("SASS instruction is not implemented")
      if (operands.headOption.exists(OperandQueries.has(_, OperandSelector.`CC`)))
        VMConditionCodeForms.finish(writtenFlags.toSet)(using this)
      val collective = OperandQueries.is[SASSInstruction.Bra](operation) && modifiers.contains(Modifier.ANY)
      val guard      = instruction.guard.map(_.predicate).filterNot(_ => collective)
      val setup      = guard.toVec.map { predicate =>
        val value = this.predicate(predicate.copy(negated = false))
        Execute(
          if (predicate.negated) PredicateUnary(VMRegisters.Predicate.Guard.slot, value.slot)
          else PredicateBinary(PredicateOperation.Or, VMRegisters.Predicate.Guard.slot, value.slot, value.slot)
        )
      }
      val block = guard.nonEmpty &&
        (OperandQueries.is[SASSInstruction.Ret](operation) ||
          OperandQueries.is[SASSInstruction.Bar](operation) || code.exists {
            case Execute(_, Some(_)) | VMAssembler.Statement.Branch(_, Some(_)) => true
            case _                                                              => false
          })
      val predication = Some(ExecutionPredicate(VMRegisters.Predicate.Guard.slot, negated = false))
      val body        =
        if (block) {
          val end: NonEmpty[String] = s"REnd${instruction.address.raw.toHexString}"
          Vec(VMAssembler.Statement.Branch(
            end,
            Some(ExecutionPredicate(VMRegisters.Predicate.Guard.slot, negated = true))
          )) ++
            code.toVec :+ Label(end)
        } else
          code.toVec.map {
            case statement: Execute if guard.nonEmpty                      => statement.copy(predication = predication)
            case statement: VMAssembler.Statement.Branch if guard.nonEmpty => statement.copy(predication = predication)
            case statement                                                 => statement
          }
      Vec(Label(label(instruction.address.raw))) ++ setup ++ body
    }
  }
}
