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
import scala.annotation.tailrec
import scala.collection.mutable
import scala.reflect.TypeTest
import scala.util.control.NonFatal

/** Compile SASS register operations to the shared NVIDIA execution IR, preserving physical register aliases. */
private object VMCompiler {
  private val CollectiveModifiers           = Vec(Modifier.COLLECTIVE)
  private val CollectiveAllModifiers        = Vec(Modifier.COLLECTIVE, Modifier.ALL)
  private def fail(detail: String): Nothing = throw IllegalArgumentException(detail)

  /** Why a kernel does not lower. */
  enum Failure(val message: NonEmpty[String]) {

    /** It uses a SASS feature the VM does not implement. */
    case Unsupported(reason: NonEmpty[String]) extends Failure(reason)

    /** A matrix product that no PTX instruction expresses. */
    case NoProduct(reason: NonEmpty[String]) extends Failure(reason)
  }

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
    lowerKernel(module, kernel, functions, globalSymbols).left.map(_.message).flatMap { (statements, registerCount) =>
      VMAssembler.assemble(statements).map(_ -> registerCount)
    }.map { (program, registerCount) =>
      val constants = module.sections.find(_.name == s".nv.constant0.${kernel.name}").fold(Vec.empty[Byte])(_.bytes)
      val block     = constants.slice(kernel.parameterBase, kernel.parameterBase.raw + kernel.parameterSize)
      // The slot names the explorer shows: the general and uniform banks this kernel addresses, in slot order.
      val layout = VMRegisters.Layout.of(kernel, functions)
      val names  = Vec.tabulate(layout.generalCount.raw + layout.uniformCount.raw) { slot =>
        layout.spelling(VMIR.Register(slot.unsafe[Int >= 0])).get.unsafe[NonEmpty[String]]
      }
      VMIR.Kernel(
        Entry(
          Routine(kernel.name, program, registerCount, VMRegisters.PredicateCount, registerNames = names),
          kernel.parameters.map(parameter =>
            KernelParameter(s"param${parameter.ordinal}", parameter.offset, parameter.size)
          ),
          block.padTo(kernel.parameterSize, 0.toByte)
        ),
        "SASS",
        cluster = Option.when(kernel.requiredCluster != LaunchGeometry.Dimensions.One)(kernel.requiredCluster),
        // ptxas's CTA-pair tensor memory allocation hands its address over through an mbarrier in the tcgen05
        // partition of reserved shared memory, at offset 0x48
        pairAllocationBarrier = Option.when(functions.exists(_.instructions.exists(instruction =>
          OperandQueries.is[Form.Utcatomsws](instruction.operation) &&
            instruction.modifiers.exists(_.name == "2CTA")
        )))(0x48L)
      )
    }

  /** The kernel's statements and the register slots its lanes need. */
  def lowerKernel(
      module: Cubin.Module,
      kernel: Cubin.Kernel,
      functions: Vec[DecodedFunction],
      boundSymbols: Map[NonEmpty[String], GuestAddress]
  ): Either[Failure, (Vec[VMAssembler.Statement], Int >= 0)] = try {
    val generation = Generation.of(module.sm)
    module.sections.filter(_.isRelocation).foreach { table =>
      if (Cubin.relocationEntries(table).isLeft) fail("invalid cubin relocations")
      val target = module.sections.lift(table.info)
      if (table.size != 0 && target.exists(_.name == s".nv.constant0.${kernel.name}"))
        fail("relocations in launch parameter constants are not implemented")
      if (table.size != 0 && target.exists(_.name.startsWith(".nv.global")))
        fail("relocations in kernel code, constants or globals are not implemented")
    }
    val image       = functions.find(_.name == kernel.name).getOrElse(fail(s"missing SASS for ${kernel.name}"))
    val relocations = Cubin.TextRelocation.resolve(module, kernel, image, boundSymbols.keySet)
      .fold(error => fail(error.raw), identity)
    val (linkedCalls, textRelocations) =
      relocations.partition((_, relocation) => relocation.call && relocation.symbol == kernel.name)
    val original = image.copy(instructions = image.instructions.map { instruction =>
      linkedCalls.get(instruction.address).fold(instruction)(call =>
        instruction.rewrite(operands = instruction.operands.init :+ Operand.Integer(call.addend))
      )
    })
    val function     = LegacyAddressRewrite(module.sm, resolveLegacyTargets(generation, original))
    val instructions = function.instructions
    val layout       = VMRegisters.Layout.of(kernel, functions)
    val reductions   = LegacyRangeReduction.inputs(module.sm, function, layout)
    def is[A <: Form](instruction: DecodedInstruction)(using TypeTest[Form, A]) =
      OperandQueries.is[A](instruction.operation)
    def at(address: Int): Option[DecodedInstruction] = instructions.find(_.address.raw == address)
    val collectiveEnds                               = instructions.filter(i =>
      is[Form.Warpsync](i) &&
        (i.modifiers == CollectiveModifiers || i.modifiers == CollectiveAllModifiers)
    ).map { setup =>
      val target = (setup.guard, setup.modifiers.contains(Modifier.ALL), setup.operands) match {
        case (None, true, Vec(end))     => codeTarget(end)
        case (None, false, Vec(_, end)) => codeTarget(end)
        case _ => fail("collective warp synchronisation requires an unguarded mask and end target")
      }
      if (
        target <= setup.address ||
        !at(target - 16).exists(i => i.guard.isEmpty && is[Form.Endcollective](i))
      ) fail("collective warp target must immediately follow ENDCOLLECTIVE")
      target - 16
    }.toSet
    instructions.filter(is[Form.Endcollective]).foreach { end =>
      if (!collectiveEnds(end.address)) fail("ENDCOLLECTIVE has no matching warp synchronisation")
    }
    // BSYNC falls through to the setup target; BMOV may restore the token into a different B register.
    instructions.filter(is[Form.Bssy]).foreach { setup =>
      val target = setup.operands match {
        case Vec(token, end) if VMRegisters.isBarrier(token) => codeTarget(end)
        case _                                               => fail("invalid convergence barrier setup")
      }
      if (target <= setup.address || !at(target - 16).exists(is[Form.Bsync]))
        fail("convergence barrier target must be forward and immediately after its BSYNC")
    }
    val legacyControl                   = Option.when(generation != Generation.Modern)(LegacyControlFlow(instructions))
    val legacyTokens                    = legacyControl.fold(Vec.empty[LegacyControlFlow.Setup])(_.setups)
    val addresses                       = instructions.map(_.address.raw).sorted
    def next(address: Int): Option[Int] = addresses.find(_ > address)
    val returnBases                     = instructions.filter(is[Form.Lepc]).map(_.address.raw) ++
      instructions.filter(is[Form.Call]).map(_.address.raw + generation.instructionBytes.raw) ++
      instructions.filter(i => is[Form.Cal](i) || is[Form.Jcal](i)).map(i =>
        next(i.address).getOrElse(fail("call has no return instruction"))
      ) ++ instructions.filter(is[Form.Pret]).map(i =>
        i.operands.headOption.fold(fail("PRET has no return target"))(codeTarget(_))
      )
    // A recursive CAL that saves tokens returns to its own resume point, at an address no instruction has, which
    // restores them before continuing after the call.
    val saved   = legacyControl.fold(Map.empty[Int, Vec[LegacyControlFlow.Setup]])(_.saved)
    val resumes = saved.keys.toVec.sorted.map(_ + 4)
    val targets = (addresses ++ resumes).sorted
    val returns = returnBases ++ resumes
    def lowerInstruction(instruction: DecodedInstruction): Either[Failure, Vec[VMAssembler.Statement]] =
      try {
        val setup      = legacyTokens.find(_.instruction.address == instruction.address)
        val syncSuffix = generation != Generation.Modern && instruction.modifiers.lastOption.contains(Modifier.S)
        val transfer   = legacyControl.flatMap(_.transfer(instruction))
        // Some legacy entry points end with RET; EXIT despite having no call sites or return tokens. This terminal
        // entry epilogue ends the launch; it must not consume a device-call return address.
        val entryReturn = generation != Generation.Modern && returnBases.isEmpty && instruction.operands.isEmpty &&
          is[Form.Ret](instruction) &&
          next(instruction.address).flatMap(at(_)).exists(i => is[Form.Exit](i) && i.guard.isEmpty)
        val normalised = setup match {
          case Some(LegacyControlFlow.Setup(_, _, slot, target)) =>
            instruction.rewrite(SASSInstruction(
              Form.BssyRegImm(
                InstructionOperand.Register.from(Operand.Register(RegisterBank.B, slot)).fold(fail(_), identity),
                InstructionOperand.Immediate.from(Operand.Integer(target.raw)).fold(fail(_), identity)
              ),
              guard = instruction.guard
            ))
          case None if syncSuffix        => instruction.rewrite(modifiers = instruction.modifiers.dropRight(1))
          case None if transfer.nonEmpty =>
            instruction.rewrite(SASSInstruction(Form.NopNoOperands(), guard = instruction.guard))
          case None if entryReturn =>
            instruction.rewrite(SASSInstruction(Form.ExitNoOperands(), guard = instruction.guard))
          case None => instruction
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
              returns,
              targets,
              boundSymbols,
              reductions.inputs.get(instruction.address),
              textRelocations.get(instruction.address),
              layout
            ).compile() ++ branch
        val arrivals = legacyTokens.filter(_.target == instruction.address).reverse.map(token =>
          Execute(Convergence(layout.barrier(token.slot), ConvergenceAction.Synchronise))
        )
        val (saves, restores) = saved.get(instruction.address).fold((
          Vec.empty[VMAssembler.Statement],
          Vec.empty[VMAssembler.Statement]
        )) {
          tokens =>
            val resume = instruction.address.raw + 4
            (
              tokens.map(token => Execute(ReturnStackSave(layout.barrier(token.slot)))),
              Label(s"L${resume.toHexString}") +: tokens.reverse.map(token =>
                Execute(ReturnStackPop(layout.barrier(token.slot)))
              ) :+ VMAssembler.Statement.Branch(
                s"L${next(instruction.address).getOrElse(fail("call has no return instruction")).toHexString}"
              )
            )
        }
        Right(
          VMAssembler.Statement.Source(instruction.address.toLong.unsafe[Long >= 0]) +:
            (statements.take(1) ++ arrivals ++ saves ++ statements.drop(1) ++ restores)
        )
      } catch {
        case NonFatal(error) =>
          val located =
            s"${kernel.name}+0x${instruction.address.raw.toHexString} (sm_${module.sm}): ${(Vec(instruction.mnemonic.raw) ++ instruction.modifiers.map(_.name)).mkString(".")} ${instruction.operands.mkString(", ")}: ${error.getMessage}"
              .unsafe[NonEmpty[String]]
          Left(error match {
            case _: VMTensorCompiler.NoProduct => Failure.NoProduct(located)
            case _                             => Failure.Unsupported(located)
          })
      }
    val lowered = Vec.newBuilder[VMAssembler.Statement]
    @tailrec
    def lowerFrom(index: Int): Option[Failure] =
      if (index == instructions.length) None
      else
        lowerInstruction(instructions(index)) match {
          case Left(failure)     => Some(failure)
          case Right(statements) =>
            val _ = lowered ++= statements
            lowerFrom(index + 1)
        }
    val zero = Value.Immediate(0L)
    lowerFrom(0).toLeft(
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
      ) ++ lowered.result() -> layout.registerCount(reductions.count)
    )
  } catch {
    case error: VMTensorCompiler.NoProduct => Left(Failure.NoProduct(throwables.detail(error)))
    case NonFatal(error)                   => Left(Failure.Unsupported(throwables.detail(error)))
  }

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
    private val code         = mutable.ArrayBuffer.empty[VMAssembler.Statement]
    private val writtenFlags = mutable.Set.empty[VMConditionCodes.Flag]
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
          legacy && control >= 0 && control % generation.bundleBytes == 0 && !known(control)
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
        if (bits == 64 && uniform) {
          val words = Vec(temp(), temp())
          emit(MoveSplit(words.map(word => Some(word.slot)), value, 32))
          words.zipWithIndex.foreach((word, index) => emit(UniformMove(destination(target, index).slot, word)))
        } else if (bits == 64)
          emit(MoveSplit(Vec(destination(target), destination(target, 1)).map(word => Some(word.slot)), value, 32))
        else if (uniform) emit(UniformMove(destination(target).slot, value))
        else emit(Move(destination(target).slot, value, 32))
      }

    def source(raw: Operand, bits: ScalarBitWidth = 32): Value =
      OperandQueries.without(raw, OperandSelector.`reuse`) match {
        case value: Operand.Register =>
          if (bits == 64 && !OperandQueries.isZero(value)) pair(value) else register(value)
        case Operand.ConstantBank(bank, Operand.Address(Some(base), None, displacement, scale, None, None))
            if scale == 1 && OperandQueries.isZero(base) =>
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
          Value.Immediate(if (width == 32) IEEE754.floatToBinary32(value.toFloat).toLong
          else IEEE754.fromDouble(value.toDouble, 64))
        case Operand.Floating(value, _, negativeZero) =>
          Value.Immediate(
            if (width == 32) IEEE754.floatToBinary32(if (negativeZero) -0.0f else value.toFloat).toLong
            else IEEE754.fromDouble(if (negativeZero) -0.0 else value.toDouble, 64)
          )
        case Operand.FloatingBits(_, encodedWidth, bits) if encodedWidth == width =>
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
          val sign   = if (width == 64) Long.MinValue else 0x80000000L
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
        localAccess: Boolean = false
    ): Value = raw match {
      case operand: Operand.Address =>
        val result      = temp()
        val mode        = operand.mode
        val uniformBase =
          if (operand.index.isEmpty) operand.base.filter(_.bank == RegisterBank.UR)
          else if (operand.base.exists(OperandQueries.isZero)) operand.index.filter(_.bank == RegisterBank.UR)
          else None
        def add(offset: Value): Unit = emit(IntegerBinary(IntegerOperation.Add, result.slot, result, offset, 64))
        // Global and generic addresses are 64-bit under .E or a .64 base, else 32-bit; uniform-only bases are pairs.
        val wide = global &&
          (modifiers.contains(Modifier.E) || mode.contains(AddressMode.`64`) ||
            (operand.index.isEmpty && uniformBase.nonEmpty))
        if (mode.contains(AddressMode.U32) && uniformBase.nonEmpty) {
          val base = uniformBase.get
          if (!global || sm < 75 || base.index >= 255 || operand.displacement != 0 || operand.scale != 1)
            unsupported("unsupported uniform-only .U32 global address")
          emit(MoveCombine(result.slot, Vec(register(base), register(base, 1)), 32))
        } else {
          operand.base match {
            case Some(base) if wide && !OperandQueries.isZero(base) && !mode.contains(AddressMode.U32) =>
              emit(MoveCombine(result.slot, Vec(register(base), register(base, 1)), 32))
            case Some(base) =>
              emit(Convert(result.slot, register(base), NumericFormat.Unsigned(32), NumericFormat.Unsigned(64), None))
            case None => emit(Move(result.slot, Value.Immediate(0L), 64))
          }
          if (!Set(1, 4, 8, 16)(operand.scale.raw)) unsupported("unsupported address scale")
          if (operand.scale != 1)
            emit(IntegerBinary(
              IntegerOperation.ShiftLeft,
              result.slot,
              result,
              Value.Immediate(Integer.numberOfTrailingZeros(operand.scale).toLong),
              64
            ))
          // A uniform offset is a register pair under a 64-bit address and one register otherwise.
          operand.index.foreach { index =>
            if (index.bank != RegisterBank.UR) unsupported("only uniform address offsets are supported")
            if (sm < 75 || (!global && mode.contains(AddressMode.`64`)))
              unsupported("uniform offsets on this address form are not implemented")
            if (wide) {
              if (index.index >= 255) unsupported("uniform offset requires an in-range register pair")
              add(pair(index))
            } else add(widen(register(index)))
          }
          if (operand.displacement != 0) add(literal(Operand.Integer(operand.displacement)))
          if ((!global && operand.index.nonEmpty) || (global && !wide))
            emit(Convert(result.slot, narrow(result), NumericFormat.Unsigned(32), NumericFormat.Unsigned(64), None))
        }
        // A CTA's own shared accesses ignore the cluster rank in bits 24..31.
        if (sharedAccess)
          emit(Logic3(
            result.slot,
            result,
            Value.Immediate(VMAddressSpace.SharedOffsetMask),
            Value.Immediate(VMAddressSpace.SharedBase),
            0xea,
            64
          ))
        // local offsets name words of the thread's local window, as generic local addresses do; a legacy window wraps
        // them to its 16 MiB, as cvta.to.local does
        if (localAccess) {
          val window = Value.Immediate(VMConstantBank.localWindow(sm))
          if (legacy) emit(Logic3(result.slot, result, Value.Immediate(0xffffffL), window, 0xea, 64))
          else add(window)
        }
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
      VMVideoCompiler.lower(using this) || VMFloatingCompiler.lower(using this) ||
        VMIntegerCompiler.lower(using this) ||
        VMLaneCompiler.lower(using this) ||
        VMPredicateCompiler.lower(using this) || VMTensorCompiler.lower(using this) ||
        VMWarpgroupCompiler.lower(using this) ||
        VMAsyncCompiler.lower(using this) || VMTensorMemoryCompiler.lower(using this) ||
        VMMemoryCompiler.lower(using this) || VMControlCompiler.lower(using this)

    def compile(): Vec[VMAssembler.Statement] = {
      // the tcgen05 MMAs mark their instruction descriptor operand IDESC, which the pinned domains do not name
      if (modifiers.exists { case Modifier.Vendor(name) => name.raw != "IDESC"; case _ => false })
        unsupported("unsupported vendor modifier")
      // I2IP spells the fixed shift of its third source for nibbles and crumbs as `.24` and `.28`
      val packed = OperandQueries.is[Form.I2ip](operation) ||
        OperandQueries.is[Form.Ui2ip](operation)
      // the packed video instructions name the elements of a register pair with a vendor selector
      val elements = VMVideoCompiler.acceptsElementSelectors(instruction)
      if (
        !packed && operands.exists(operand =>
          OperandQueries.hasUnknownDomain(operand) &&
            !(elements && VMVideoCompiler.isElementSource(operand))
        )
      )
        unsupported("unsupported vendor operand domain")
      // nothing defines the handle a cx[URa] bank reads, and no compute toolchain emits one
      if (operands.exists(OperandQueries.indexedBank(_)))
        unsupported("constant banks selected by a uniform register are not modelled")
      if (legacy && modifiers.contains(Modifier.X) && VMConditionCodeForms.carryConsumer(operation))
        seedCarry(readConditionFlag(VMConditionCodes.Flag.Carry))
      if (!lowered) unsupported("SASS instruction is not implemented")
      if (operands.headOption.exists(OperandQueries.has(_, OperandSelector.`CC`)))
        VMConditionCodeForms.finish(writtenFlags.toSet)(using this)
      val collective = OperandQueries.is[Form.Bra](operation) && modifiers.contains(Modifier.ANY)
      val guard      = instruction.guard.map(_.predicate).filterNot(_ => collective)
      val setup      = guard.toVec.map { predicate =>
        val value = this.predicate(predicate.copy(negated = false))
        Execute(
          if (predicate.negated) PredicateUnary(VMRegisters.Predicate.Guard.slot, value.slot)
          else PredicateBinary(PredicateOperation.Or, VMRegisters.Predicate.Guard.slot, value.slot, value.slot)
        )
      }
      val block = guard.nonEmpty &&
        (OperandQueries.is[Form.Ret](operation) ||
          OperandQueries.is[Form.Bar](operation) || code.exists {
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
