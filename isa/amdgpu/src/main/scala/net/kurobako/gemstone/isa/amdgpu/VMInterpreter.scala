package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import VMArithmetic.*
import VMIR.*
import VMState.*
import cats.syntax.either.*
import cats.syntax.traverse.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

private object VMInterpreter {
  import VMDispatchMemory.*

  /** Step results other than a jump target. */
  private object Step {
    inline val Next    = -1
    inline val Exit    = -2
    inline val Arrive  = -3
    inline val Blocked = -4
  }
  private val NextStep: Either[Fault, Int] = Right(Step.Next)

  private enum WaveBoundary {
    case Barrier, End
  }

  private enum Location {
    case Global, Private, Local, Discarded
  }

  def execute(
      prepared: Kernel,
      dispatch: KernelTask,
      memory: AddressSpace,
      control: TaskControl,
      workgroups: Workgroups,
      globalBindings: Vec[GlobalMapping],
      @scala.annotation.unused handler: Event => Either[Fault, Unit]
  ): Either[Fault, Unit] =
    for {
      kernarg <- dispatch.argumentBuffer match {
        case Some(buffer) => memory.address(buffer.region, buffer.offset).flatMap { address =>
            memory.locate(address, prepared.source.kernargSize.raw.max(1).unsafe[Int >= 0])
              .map(_ => (Array.emptyByteArray, address.raw))
          }
        case None =>
          effectiveKernargSize(prepared.source, dispatch.arguments)
            .leftMap(detail => Fault.InvalidRequest(detail))
            .flatMap(size => packArguments(dispatch, size, memory, globalBindings))
            .map(bytes => (bytes, KernargAddress))
      }
      _ <- runDispatch(
        prepared,
        kernarg._1,
        kernarg._2,
        Vec.toArray(globalBindings),
        dispatch,
        memory,
        control,
        workgroups
      )
    } yield ()

  /** A descriptor without a kernarg size takes the packed size of the typed arguments. */
  def effectiveKernargSize(
      kernel: CodeObjectKernel,
      arguments: Vec[KernelTask.Argument]
  ): Either[NonEmpty[String], Int >= 0] =
    if (kernel.kernargSize.raw != 0 || arguments.isEmpty) Right(kernel.kernargSize)
    else
      arguments.traverse[[A] =>> Either[NonEmpty[String], A], Long] {
        case KernelTask.Argument.Scalar(bytes)   => Right(bytes.length.toLong)
        case KernelTask.Argument.Memory(_, _, _) => Right(8L)
        case KernelTask.Argument.Local(_)        => Left("gfx900 local kernargs are not implemented")
        case KernelTask.Argument.Descriptor      => Left("AMDGPU kernels have no descriptor arguments")
      }.flatMap(sizes =>
        sizes.sum.refine[Long in 0L ~ 2147483647L].bimap(_ => "kernel arguments are too large", _.toIntExact)
      )

  private def packArguments(
      task: KernelTask,
      kernargSize: Int >= 0,
      addresses: AddressSpace,
      globalBindings: Vec[GlobalMapping]
  ): Either[Fault, Array[Byte]] =
    KernelArguments.resolve(task, argumentAddress(_, _, addresses, globalBindings)).flatMap { arguments =>
      val packed = Array.ofDim[Byte](kernargSize.raw)
      arguments.zipWithIndex
        .foldLeft[Either[Fault, Int]](Right(0)) {
          case (Right(offset), (KernelArguments.Resolved.Bytes(argument), index)) =>
            val bytes = argument.raw
            if (offset == kernargSize.raw) Left(Fault.InvalidRequest(
              s"kernel descriptor requires $kernargSize kernarg bytes, received trailing argument $index"
            ))
            else if (offset > kernargSize.raw - bytes.length) Left(Fault.InvalidRequest(
              s"kernel descriptor requires $kernargSize kernarg bytes, received more than $kernargSize"
            ))
            else {
              val _ = bytes.copyToArray(packed, offset, bytes.length)
              Right(offset + bytes.length)
            }
          case (Right(_), (KernelArguments.Resolved.Local(_), _)) =>
            Left(Fault.Unsupported("gfx900 local kernargs are not implemented"))
          case (failure, _) => failure
        }
        .flatMap(offset =>
          Either.cond(
            offset == kernargSize.raw,
            packed,
            Fault.InvalidRequest(s"kernel descriptor requires $kernargSize kernarg bytes, received $offset")
          )
        )
    }

  private def argumentAddress(
      region: AllocationId,
      offset: Long >= 0,
      addresses: AddressSpace,
      globalBindings: Vec[GlobalMapping]
  ): Either[Fault, GuestAddress] =
    globalBindings.find(mapping =>
      mapping.binding.region == region &&
        offset >= mapping.binding.offset &&
        (offset - mapping.binding.offset).raw <= mapping.size.raw.toLong
    ) match {
      case Some(mapping) => Right(mapping.address.offset((offset - mapping.binding.offset).raw))
      case None          => addresses.address(region, offset)
    }

  // Workgroups advance through the shared bounded workgroups without materialising a result collection.
  private def runDispatch(
      program: Kernel,
      kernarg: Array[Byte],
      kernargAddress: Long,
      allocations: Array[GlobalMapping],
      dispatch: KernelTask,
      memory: AddressSpace,
      control: TaskControl,
      workgroups: Workgroups
  ): Either[Fault, Unit] = {
    val kernel = program.source
    // Kernels using enable_sgpr_dispatch_ptr read the HSA packet directly;
    // OpenMP derives its grid-stride loop increment from grid_size_x.
    val packet = Array.ofDim[Byte](64)
    LittleEndian.write(2L, packet, 0, 2)
    LittleEndian.write(dispatch.geometry.workDim.raw.toLong, packet, 2, 2)
    LittleEndian.write(dispatch.geometry.groupSize.x.raw.toLong, packet, 4, 2)
    LittleEndian.write(dispatch.geometry.groupSize.y.raw.toLong, packet, 6, 2)
    LittleEndian.write(dispatch.geometry.groupSize.z.raw.toLong, packet, 8, 2)
    LittleEndian.write(dispatch.geometry.globalExtent.x.raw, packet, 12, 4)
    LittleEndian.write(dispatch.geometry.globalExtent.y.raw, packet, 16, 4)
    LittleEndian.write(dispatch.geometry.globalExtent.z.raw, packet, 20, 4)
    LittleEndian.write(math.max(kernel.privateSegmentSize.raw, dispatch.privateSegmentBytes.raw).toLong, packet, 24, 4)
    LittleEndian.write(
      effectiveGroupSegmentSize(kernel.groupSegmentSize.raw, dispatch.groupSegmentBytes.raw).toLong,
      packet,
      28,
      4
    )
    LittleEndian.write(kernargAddress, packet, 40, 8)
    val dispatchMemory = VMDispatchMemory(kernel, kernarg, packet, allocations, memory, kernargAddress)
    Dispatch.forEachGroup(dispatch.geometry, workgroups)((_, group) =>
      runGroup(program, dispatchMemory, dispatch, group, control)
    )
  }

  // Wave rendezvous is a measured mutable state machine; null marks a wave not yet started or already finished.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  private def runGroup(
      program: Kernel,
      dispatchMemory: VMDispatchMemory,
      dispatch: KernelTask,
      group: Coordinates,
      control: TaskControl
  ): Either[Fault, Unit] = {
    val kernel     = program.source
    val localCount = dispatch.geometry.groupVolume.raw
    val lds = Array.ofDim[Byte](effectiveGroupSegmentSize(kernel.groupSegmentSize.raw, dispatch.groupSegmentBytes.raw))
    val waveSize  = kernel.waveSize.raw
    val waveCount = (localCount + waveSize - 1) / waveSize
    val waves     = Array.ofDim[Wave | Null](waveCount)
    val barriers  = WorkgroupBarriers(waveCount)
    // A wave can end before others reach a barrier in divergent control flow; it must not restart next round.
    val finished              = Array.fill(waveCount)(false)
    var complete              = false
    var failure: Fault | Null = null
    while (!complete && failure == null) {
      var waiting   = 0
      var live      = 0
      var waveIndex = 0
      while (waveIndex < waveCount && failure == null) {
        if (!finished(waveIndex)) {
          live += 1
          val wave = waves(waveIndex) match {
            case null =>
              val start = waveIndex * waveSize
              // The enclosing wave loop guarantees start < localCount; waveSize is 32 or 64.
              val lanes = math.min(waveSize, localCount - start).unsafe[Int where Between[1, 64]]
              Wave(program, dispatchMemory, dispatch.geometry, group, start, lanes, lds, barriers, control)
            case started => started
          }
          wave.run() match {
            case Left(error)                 => failure = error
            case Right(WaveBoundary.Barrier) =>
              waves(waveIndex) = wave
              waiting += 1
            case Right(WaveBoundary.End) =>
              waves(waveIndex) = null
              finished(waveIndex) = true
              barriers.drop(waveIndex)
          }
        }
        waveIndex += 1
      }
      if (failure == null)
        if (live == 0 || waiting == 0) complete = true
    }
    if (failure == null) Right(()) else Left(failure)
  }

  // Register, PC and barrier state are updated directly in the measured wave interpreter loop; null avoids allocating
  // optional-state wrappers for every instruction.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  private class Wave(
      program: Kernel,
      dispatchMemory: VMDispatchMemory,
      geometry: LaunchGeometry,
      group: Coordinates,
      localStart: Int,
      lanes: Int in 1 ~ 64,
      lds: Array[Byte],
      barriers: WorkgroupBarriers,
      control: TaskControl
  ) {
    private val kernel             = program.source
    private val operations         = program.instructions
    private val instructionIndices = program.instructionIndices
    private val instructionOffsets = program.instructionOffsets
    private val waveSize           = kernel.waveSize
    private val scalar             = Array.fill[Long](128)(0L)
    private val vector             = VectorRegisters(waveSize)
    private val accumulator        = VectorRegisters(waveSize)
    private val routed             = Array.ofDim[Long](lanes.raw)
    private val preservedHalves    = Array.ofDim[Long](lanes.raw)
    // A vector comparison defines only the wave's lane bits of VCC. On a wave32 target the compiler keeps scratch values
    // in vcc_hi (s107), so the upper half survives a comparison while s106/s107 still round-trip through the register.
    private val carry        = LaneBits(lanes, 0L)
    private val active       = LaneBits(lanes, geometry.liveLanes(group, localStart.unsafe[Int >= 0], lanes))
    private var scalarCarry  = false
    private var mode         = kernel.initialMode
    private val modeWritable = if (Mode.ieeeSupported(kernel.target.isa)) -1 else ~Mode.IEEE
    // The 4096-byte floor proves the per-lane allocation is positive.
    private val scratch          = ScratchMemory(lanes, math.max(4096, kernel.privateSegmentSize.raw).unsafe[Int > 0])
    private var conditionMask    = 0L
    private val codeBase         = kernel.textAddress
    private var instructionIndex = program.entryInstruction
    private var located          = Location.Global

    locally {
      var nextUserSgpr: Int >= 0                       = 0
      def enabled(value: Int, bit: Int): Boolean       = (value & (1 << bit)) != 0
      def reserve(count: Int > 0): ScalarRegisterIndex = {
        val start = nextUserSgpr
        require(count.raw <= 128 - start.raw, "user scalar registers exceed the register bank")
        nextUserSgpr += count
        start.raw.unsafe[ScalarRegisterIndex]
      }
      if (enabled(kernel.kernelCodeProperties, 0)) nextUserSgpr += 4
      if (enabled(kernel.kernelCodeProperties, 1)) setPair(scalar, reserve(2), DispatchAddress)
      if (enabled(kernel.kernelCodeProperties, 2)) setPair(scalar, reserve(2), QueueAddress)
      if (enabled(kernel.kernelCodeProperties, 3)) setPair(scalar, reserve(2), dispatchMemory.kernargAddress)
      if (enabled(kernel.kernelCodeProperties, 4)) setPair(scalar, reserve(2), 0L)
      if (enabled(kernel.kernelCodeProperties, 5)) setPair(scalar, reserve(2), 0L)
      if (enabled(kernel.kernelCodeProperties, 6)) scalar(reserve(1).raw) = kernel.privateSegmentSize.raw.toLong
      // System SGPRs start after USER_SGPR_COUNT, which may include uninitialised user slots.
      nextUserSgpr = nextUserSgpr.max(Bits.unsignedIntField(kernel.computePgmRsrc2.toLong, 1, 5).widen[Int >= 0])
      if (enabled(kernel.computePgmRsrc2, 7)) scalar(reserve(1).raw) = Bits.unsigned32(group.x.raw).toLong
      if (enabled(kernel.computePgmRsrc2, 8)) scalar(reserve(1).raw) = Bits.unsigned32(group.y.raw).toLong
      if (enabled(kernel.computePgmRsrc2, 9)) scalar(reserve(1).raw) = Bits.unsigned32(group.z.raw).toLong
      if (enabled(kernel.computePgmRsrc2, 10)) scalar(reserve(1).raw) = 0L
      if (kernel.target.isa == ISA.RDNA4) {
        // RDNA4 architected workgroup IDs: TTMP9=X, TTMP7={Z[15:0],Y[15:0]}.
        scalar(Source.TTMP_GFX9PLUS_MIN + 9) = Bits.unsigned32(group.x.raw).toLong
        scalar(Source.TTMP_GFX9PLUS_MIN + 7) = (group.y.raw & 0xffffL) | ((group.z.raw & 0xffffL) << 16)
      }
      val enabledWorkItemIds = (kernel.computePgmRsrc2 >>> 11) & 0x3
      var lane               = 0
      while (lane < lanes.raw) {
        val local = geometry.localCoordinates((localStart + lane).unsafe[Int >= 0])
        kernel.target.isa match {
          case ISA.CDNA2 | ISA.CDNA3 | ISA.CDNA4 | ISA.CDNA5 | ISA.RDNA3 | ISA.RDNA3_5 | ISA.RDNA4 | ISA.GFX11_7 =>
            // The HSA ABI packs each enabled work-item coordinate into ten bits of VGPR0.
            val y = if (enabledWorkItemIds >= 1) local.y.raw else 0
            val z = if (enabledWorkItemIds >= 2) local.z.raw else 0
            vector(0)(lane) = local.x.raw.toLong | (y.toLong << 10) | (z.toLong << 20)
          case _ =>
            vector(0)(lane) = Bits.unsigned32(local.x.raw).toLong
            if (enabledWorkItemIds >= 1) vector(1)(lane) = Bits.unsigned32(local.y.raw).toLong
            if (enabledWorkItemIds >= 2) vector(2)(lane) = Bits.unsigned32(local.z.raw).toLong
        }
        lane += 1
      }
    }

    // Compare and carry results write the whole mask, clearing EXEC-inactive lanes. A discarded result names the
    // null SGPR, which must not alias the live EXEC mask.
    def updateLaneMask(destination: ScalarRegisterIndex, result: Long): Unit =
      if (destination.raw != Fixed.NullSgpr) {
        if (waveSize.raw <= 32) {
          writeScalar32(destination, Bits.low32(result).toLong)
        } else {
          writeScalar64(destination, result)
        }
      }

    def saveExecSource(src: InstructionSource, width: Int in (32, 64)): Long =
      if (width.raw == 32) Bits.low32(sourceScalar64(src)).toLong else sourceScalar64(src)

    def saveExecWrite(destination: ScalarRegisterIndex, value: Long, width: Int in (32, 64)): Unit =
      if (width.raw == 32) writeScalar32(destination, value) else writeScalar64(destination, value)

    def holds(condition: ScalarBranchCondition): Boolean = condition match {
      case ScalarBranchCondition.Always      => true
      case ScalarBranchCondition.SccZero     => !scalarCarry
      case ScalarBranchCondition.SccNonZero  => scalarCarry
      case ScalarBranchCondition.VccZero     => carry.isEmpty
      case ScalarBranchCondition.VccNonZero  => carry.nonEmpty
      case ScalarBranchCondition.ExecZero    => active.isEmpty
      case ScalarBranchCondition.ExecNonZero => active.nonEmpty
    }

    // Uniform sources are read once rather than per lane.
    def move(dest: VectorRegisterIndex, src: InstructionSource): Unit = {
      val destination = vector(dest)
      src match {
        case InstructionSource.VectorRegister(index) =>
          val values         = vector(index)
          var lane: Int >= 0 = 0
          while (lane.raw < lanes.raw) {
            if (active(lane.raw.unsafe[Lane])) destination(lane.raw) = Bits.low32(values(lane.raw)).toLong
            lane += 1
          }
        case InstructionSource.DPP =>
          var lane: Int >= 0 = 0
          while (lane.raw < lanes.raw) {
            if (active(lane.raw.unsafe[Lane])) destination(lane.raw) = Bits.low32(routed(lane.raw)).toLong
            lane += 1
          }
        case uniform =>
          val value          = Bits.low32(source(uniform, 0)).toLong
          var lane: Int >= 0 = 0
          while (lane.raw < lanes.raw) {
            if (active(lane.raw.unsafe[Lane])) destination(lane.raw) = value
            lane += 1
          }
      }
    }

    def source(value: InstructionSource, lane: Int >= 0): Long = value match {
      case InstructionSource.Scalar(register) if register.raw == Fixed.VccLo  => Bits.low32(carry.register).toLong
      case InstructionSource.Scalar(register) if register.raw == Fixed.VccHi  => Bits.high32(carry.register).toLong
      case InstructionSource.Scalar(register) if register.raw == Fixed.ExecLo => Bits.low32(active.register).toLong
      case InstructionSource.Scalar(register) if register.raw == Fixed.ExecHi => Bits.high32(active.register).toLong
      case InstructionSource.Scalar(index)                                    => scalar(index.raw)
      case InstructionSource.VectorRegister(index)                            => vector(index)(lane.raw)
      case InstructionSource.DPP                                              => routed(lane.raw)
      case InstructionSource.Condition(condition)                             => if (holds(condition)) 1L else 0L
      case InstructionSource.HighHalf(half)  => Bits.low16(source(half, lane) >>> 16).toLong
      case InstructionSource.Floating(value) => IEEE754.floatToBinary32(value.toFloat).toLong
      case InstructionSource.Constant(raw)   => raw
      case InstructionSource.Literal(raw)    => raw
    }

    def sourceScalar64(value: InstructionSource): Long = value match {
      case InstructionSource.Scalar(register) if register.raw == Fixed.VccLo  => carry.register
      case InstructionSource.Scalar(register) if register.raw == Fixed.VccHi  => Bits.high32(carry.register).toLong
      case InstructionSource.Scalar(register) if register.raw == Fixed.ExecLo => active.register
      case InstructionSource.Scalar(register) if register.raw == Fixed.ExecHi => Bits.high32(active.register).toLong
      case InstructionSource.Scalar(index)                                    => pair(scalar, index)
      case InstructionSource.Condition(condition)                             => if (holds(condition)) 1L else 0L
      case InstructionSource.HighHalf(half)                            => Bits.low16(sourceScalar64(half) >>> 16).toLong
      case InstructionSource.Floating(value)                           => IEEE754.doubleToBinary64(value)
      case InstructionSource.Constant(raw)                             => raw
      case InstructionSource.Literal(raw)                              => raw
      case InstructionSource.VectorRegister(_) | InstructionSource.DPP => 0L
    }

    def source64(value: InstructionSource, lane: Int >= 0): Long = value match {
      case InstructionSource.VectorRegister(index) => pair(vector, index, lane)
      case _                                       => sourceScalar64(value)
    }

    def sourcePackedF32(value: InstructionSource, lane: Int >= 0): Long = value match {
      // Packed operands select 32-bit dwords even though register sources span a pair.
      case InstructionSource.Floating(value) => IEEE754.floatToBinary32(value.toFloat).toLong
      case _                                 => source64(value, lane)
    }

    def sourceFloating64(value: InstructionSource, lane: Int >= 0): Long = value match {
      case InstructionSource.Literal(raw) => IEEE754.doubleToBinary64(floatingLiteralToDouble(raw))
      case _                              => source64(value, lane)
    }

    def writeScalar64(dest: ScalarRegisterIndex, value: Long): Unit = dest.raw match {
      case Fixed.VccLo  => carry.register = value
      case Fixed.ExecLo => active.register = value
      case _            => setPair(scalar, dest, value)
    }

    def writeScalar32(dest: ScalarRegisterIndex, value: Long): Unit = dest.raw match {
      case Fixed.VccLo => carry.register = (carry.register & 0xffffffff00000000L) | Bits.low32(value).toLong
      case Fixed.VccHi =>
        carry.register = (carry.register & 0xffffffffL) | (Bits.low32(value).toLong << 32)
      case Fixed.ExecLo => active.register = (active.register & 0xffffffff00000000L) | Bits.low32(value).toLong
      case Fixed.ExecHi => active.register = (active.register & 0xffffffffL) | (Bits.low32(value).toLong << 32)
      case _            => scalar(dest.raw) = Bits.low32(value).toLong
    }

    /** Rewrites the active lanes' `width`-bit result in `dest`; a binary16 result keeps the register's high half. */
    def rewriteFloatResult(dest: VectorRegisterIndex, width: Int in (16, 32, 64), update: Long => Long): Unit =
      foreachActive { lane =>
        width.raw match {
          case 64 => writeVectorValue(vector, dest, lane, update(pair(vector, dest, lane)), 8)
          case 32 => vector(dest)(lane.raw) = update(vector(dest)(lane.raw))
          case _  =>
            val bits = vector(dest)(lane.raw)
            vector(dest)(lane.raw) = (bits & ~0xffffL) | Bits.low16(update(bits)).toLong
        }
      }

    def foreachLane(operation: Lane => Unit): Unit = {
      var lane: Int >= 0 = 0
      while (lane.raw < lanes.raw) {
        operation(lane.raw.unsafe[Lane])
        lane += 1
      }
    }

    def foreachActive(operation: Lane => Unit): Unit = {
      var lane: Int >= 0 = 0
      while (lane.raw < lanes.raw) {
        val current = lane.raw.unsafe[Lane]
        if (active(current)) operation(current)
        lane += 1
      }
    }

    def programCounter(index: Int): Long = codeBase + instructionOffsets(index).toLong

    def programIndex(address: Long): Option[Int] = {
      val relative = address - codeBase
      if (relative < 0L || relative > Int.MaxValue.toLong) None
      else instructionIndices.get(relative.toInt)
    }

    /** The guest address, private offset or LDS address `address` names for `lane`; `located` records which. */
    private def locate(address: MemoryAddress, lane: Int >= 0, bytes: Int): Long = address match {
      case MemoryAddress.Global(register, base, offset) =>
        located = Location.Global
        (base match {
          case Some(base) => pair(scalar, base) + Bits.low32(vector(register)(lane.raw)).toLong
          case None       => pair(vector, register, lane)
        }) + offset.toLong
      case MemoryAddress.Flat(register, offset) =>
        val value = pair(vector, register, lane) + offset.toLong
        if (VMAddressSpace.isLocal(value)) {
          located = Location.Local
          VMAddressSpace.offset(value)
        } else if (VMAddressSpace.isPrivate(value)) {
          located = Location.Private
          VMAddressSpace.offset(value)
        } else {
          located = Location.Global
          value
        }
      case MemoryAddress.Scratch(register, base, offset) =>
        located = Location.Private
        laneOffset(register, lane) + scalarOffset(base) + offset.toLong
      case MemoryAddress.Buffer(register, resource, base, offset) =>
        val start = laneOffset(register, lane) + offset.toLong
        val bound = pair(scalar, resource) & 0x0000ffffffffffffL
        if (bound == 0L) {
          // A null descriptor addresses wave-interleaved scratch; its scalar offset spans every lane.
          located = Location.Private
          start + scalarOffset(base) / waveSize.raw
        } else {
          val relative = start + scalarOffset(base)
          located =
            if (relative >= 0L && relative <= Bits.low32(scalar(resource.raw + 2)).toLong - bytes) Location.Global
            else Location.Discarded
          bound + relative
        }
      case MemoryAddress.Local(register, offset) =>
        located = Location.Local
        Bits.low32(vector(register)(lane.raw)).toLong + offset.toLong
    }

    private def laneOffset(register: Option[VectorRegisterIndex], lane: Int >= 0): Long = register match {
      case Some(register) => Bits.low32(vector(register)(lane.raw)).toLong
      case None           => 0L
    }

    private def scalarOffset(register: Option[ScalarRegisterIndex]): Long = register match {
      case Some(register) => Bits.low32(scalar(register.raw)).toLong
      case None           => 0L
    }

    private def privateFault(offset: Long, bytes: Int): Fault =
      Fault.Memory(s"private access at offset $offset ($bytes bytes) exceeds the private segment")

    // Per-lane accesses report a fault or null so local and private lanes allocate nothing.
    private var loaded = 0L

    /** Reads at most eight bytes from a located access into `loaded`. */
    private def readValue(at: Long, lane: Lane, bytes: Int in 1 ~ 8, coherent: Boolean): Fault | Null =
      located match {
        case Location.Global =>
          val value =
            if (coherent && (bytes.raw == 4 || bytes.raw == 8) && !dispatchMemory.isImmutable(at, bytes))
              dispatchMemory.atomicModifyValue(at, if (bytes.raw == 4) 4 else 8)(identity)
            else dispatchMemory.readValue(at, bytes)
          value match {
            case Left(fault)  => fault
            case Right(value) =>
              loaded = value
              null
          }
        case Location.Private =>
          val storage = scratch(lane)
          if (!ByteRange.contains(storage.length, at, bytes.raw.toLong)) privateFault(at, bytes.raw)
          else {
            loaded = LittleEndian.decode(storage, at.toInt, bytes)
            null
          }
        case Location.Local =>
          val offset = checkedLdsOffset(lds, at, bytes.raw)
          if (offset == InvalidLdsOffset) ldsFailure(at, bytes.raw)
          else {
            loaded = LittleEndian.decode(lds, offset, bytes)
            null
          }
        case Location.Discarded =>
          loaded = 0L
          null
      }

    private def writeValue(at: Long, lane: Lane, value: Long, bytes: Int in 1 ~ 8): Fault | Null =
      located match {
        case Location.Global  => dispatchMemory.writeValue(at, value, bytes).left.getOrElse(null)
        case Location.Private =>
          val storage = scratch(lane)
          if (!ByteRange.contains(storage.length, at, bytes.raw.toLong)) privateFault(at, bytes.raw)
          else {
            LittleEndian.write(value, storage, at.toInt, bytes)
            null
          }
        case Location.Local =>
          val offset = checkedLdsOffset(lds, at, bytes.raw)
          if (offset == InvalidLdsOffset) ldsFailure(at, bytes.raw)
          else {
            LittleEndian.write(value, lds, offset, bytes)
            null
          }
        case Location.Discarded => null
      }

    private def loadLane(load: Instruction.Load, registers: VectorRegisters, lane: Lane): Fault | Null = {
      val bytes = load.bytes.raw
      val at    = locate(load.address, lane, bytes)
      if (bytes > 8) located match {
        case Location.Global =>
          dispatchMemory.readBytes(at, load.bytes) match {
            case Left(fault) => fault
            case Right(data) =>
              writeVectorBytes(registers, load.dest, lane, data)
              null
          }
        case Location.Private =>
          val storage = scratch(lane)
          if (!ByteRange.contains(storage.length, at, bytes.toLong)) privateFault(at, bytes)
          else {
            writeVectorBytes(registers, load.dest, lane, storage, at.toInt, bytes)
            null
          }
        case Location.Local =>
          val offset = checkedLdsOffset(lds, at, bytes)
          if (offset == InvalidLdsOffset) ldsFailure(at, bytes)
          else {
            writeVectorBytes(registers, load.dest, lane, lds, offset, bytes)
            null
          }
        case Location.Discarded =>
          writeVectorBytes(registers, load.dest, lane, Array.ofDim[Byte](bytes))
          null
      }
      else {
        val fault = readValue(at, lane, bytes.unsafe[Int in 1 ~ 8], load.coherent)
        if (fault == null) {
          val value = if (load.signed) Bits.signExtend(loaded, (bytes * 8).unsafe[Int in 1 ~ 64]) else loaded
          load.placement match {
            case LoadPlacement.Full => writeVectorValue(registers, load.dest, lane, value, bytes)
            case placement          =>
              registers(load.dest)(lane.raw) =
                mergeD16(registers(load.dest)(lane.raw), Bits.low16(value).toLong, placement == LoadPlacement.High)
          }
        }
        fault
      }
    }

    private def storeLane(store: Instruction.Store, registers: VectorRegisters, lane: Lane): Fault | Null = {
      val at = locate(store.address, lane, store.bytes.raw)
      if (store.bytes.raw > 8) {
        val data = vectorBytes(registers, store.src, lane, store.bytes)
        located match {
          case Location.Global  => dispatchMemory.writeBytes(at, data).left.getOrElse(null)
          case Location.Private =>
            val storage = scratch(lane)
            if (!ByteRange.contains(storage.length, at, data.length.toLong)) privateFault(at, data.length)
            else {
              Array.copy(data, 0, storage, at.toInt, data.length)
              null
            }
          case Location.Local     => writeLDS(lds, at, data).left.getOrElse(null)
          case Location.Discarded => null
        }
      } else {
        val value =
          if (store.high) registers(store.src)(lane.raw) >>> 16
          else if (store.bytes.raw <= 4) registers(store.src)(lane.raw)
          else pair(registers, store.src, lane)
        writeValue(at, lane, value, store.bytes.raw.unsafe[Int in 1 ~ 8])
      }
    }

    // Waves within a workgroup run serially, so LDS and private read-modify-writes cannot interleave.
    private def atomicLane(
        address: MemoryAddress,
        dest: VectorRegisterIndex,
        bytes: Int in (4, 8),
        returnValue: Boolean,
        lane: Lane,
        update: Long => Long
    ): Fault | Null = {
      val at    = locate(address, lane, bytes.raw)
      val fault =
        if (located == Location.Global) dispatchMemory.atomicModifyValue(at, bytes)(update) match {
          case Left(fault)     => fault
          case Right(previous) =>
            loaded = previous
            null
        }
        else {
          val read = readValue(at, lane, bytes.widen, false)
          if (read == null) writeValue(at, lane, update(loaded), bytes.widen) else read
        }
      if (fault == null && returnValue) writeVectorValue(vector, dest, lane, loaded, bytes.raw)
      fault
    }

    private def foreachActiveUntilFault(operation: Lane => Fault | Null): Either[Fault, Int] = {
      var lane: Int >= 0        = 0
      var failure: Fault | Null = null
      while (lane.raw < lanes.raw && failure == null) {
        val current = lane.raw.unsafe[Lane]
        if (active(current)) failure = operation(current)
        lane += 1
      }
      if (failure == null) NextStep else Left(failure)
    }

    // Only collect addresses that the load will read from host memory; apertures, code-object images and
    // out-of-range buffer lanes never reach the host pump.
    private def prepareReads(load: Instruction.Load): Either[Fault, Unit] =
      if (load.coherent) Right(())
      else {
        val ranges         = Vec.newBuilder[(GuestAddress, Int >= 0)]
        var lane: Int >= 0 = 0
        while (lane.raw < lanes.raw) {
          if (active(lane.raw.unsafe[Lane])) {
            val at = locate(load.address, lane, load.bytes.raw)
            if (located == Location.Global && dispatchMemory.isExternal(at, load.bytes))
              ranges += ((GuestAddress(at), load.bytes))
          }
          lane += 1
        }
        val collected = ranges.result()
        if (collected.isEmpty) Right(()) else dispatchMemory.memory.prepareReads(collected)
      }

    def stepMemory(instruction: Instruction): Either[Fault, Int] = instruction match {
      case load: Instruction.Load =>
        val registers = if (load.bank == RegisterBank.Accumulator) accumulator else vector
        prepareReads(load).flatMap(_ => foreachActiveUntilFault(loadLane(load, registers, _)))
      case store: Instruction.Store =>
        val registers = if (store.bank == RegisterBank.Accumulator) accumulator else vector
        foreachActiveUntilFault(storeLane(store, registers, _))
      case Instruction.Atomic(operation, dest, address, src, bytes, returnValue) =>
        val width = bytes.toBitWidth
        foreachActiveUntilFault { lane =>
          val operand = if (bytes.raw == 8) pair(vector, src, lane) else vector(src)(lane.raw)
          atomicLane(address, dest, bytes, returnValue, lane, operation(_, operand, width))
        }
      case Instruction.CompareSwap(dest, address, data, bytes, returnValue) =>
        val comparand = data.offsetBy(bytes.widen[Int >= 0] / 4)
        foreachActiveUntilFault { lane =>
          val (compare, swap) =
            if (bytes.raw == 8) (pair(vector, comparand, lane), pair(vector, data, lane))
            else (Bits.low32(vector(comparand)(lane.raw)).toLong, Bits.low32(vector(data)(lane.raw)).toLong)
          atomicLane(address, dest, bytes, returnValue, lane, old => if (old == compare) swap else old)
        }
      case Instruction.LoadPair(dest, address, bytes, first, second) =>
        val upper = dest.offsetBy(bytes.widen[Int >= 0] / 4)
        foreachActiveUntilFault { lane =>
          val base = Bits.low32(vector(address)(lane.raw)).toLong
          val low  = checkedLdsOffset(lds, base + first, bytes.raw)
          val high = checkedLdsOffset(lds, base + second, bytes.raw)
          if (low == InvalidLdsOffset) ldsFailure(base + first, bytes.raw)
          else if (high == InvalidLdsOffset) ldsFailure(base + second, bytes.raw)
          else {
            writeVectorBytes(vector, dest, lane, lds, low, bytes.raw)
            writeVectorBytes(vector, upper, lane, lds, high, bytes.raw)
            null
          }
        }
      case Instruction.StorePair(address, srcA, srcB, bytes, first, second) =>
        foreachActiveUntilFault { lane =>
          val base = Bits.low32(vector(address)(lane.raw)).toLong
          writeLDS(lds, base + first, vectorBytes(vector, srcA, lane, bytes.widen))
            .flatMap(_ => writeLDS(lds, base + second, vectorBytes(vector, srcB, lane, bytes.widen)))
            .left.getOrElse(null)
        }
      case Instruction.CacheWritebackInvalidate =>
        dispatchMemory.memory.flushHostWrites().flatMap { _ =>
          dispatchMemory.memory.invalidateHostReads()
          NextStep
        }
      case Instruction.CacheInvalidate =>
        // A following ordinary load must not reuse bytes cached before acquire.
        dispatchMemory.memory.invalidateHostReads()
        NextStep
      case other => Left(Fault.Unsupported(s"unhandled AMDGPU instruction $other"))
    }

    // Split the instruction switch into JIT-sized methods. The complete switch is too large for C2 to compile.
    def step(index: Int): Either[Fault, Int] = operations(index) match {
      case Instruction.End        => dispatchMemory.memory.flushHostWrites().map(_ => Step.Exit)
      case Instruction.Trap(code) =>
        Left(Fault.Backend(f"AMDGPU s_trap $code%d at pc 0x${programCounter(index)}%x"))
      case Instruction.Wait                       => dispatchMemory.memory.flushHostWrites().flatMap(_ => NextStep)
      case Instruction.SSetGprIdxOn               => NextStep
      case Instruction.Barrier                    => Right(Step.Arrive)
      case Instruction.BarrierSignal(id, isFirst) =>
        barriers.signal(source(id, 0).toInt, localStart / waveSize.raw).flatMap { first =>
          if (isFirst) scalarCarry = first
          NextStep
        }
      case Instruction.BarrierWait(id) =>
        barriers.await(id, localStart / waveSize.raw).flatMap { ready =>
          if (ready) NextStep else Right(Step.Blocked)
        }
      case Instruction.Branch(condition, target) =>
        Right(if (holds(condition)) target else index + 1)
      case Instruction.BranchRelative(_, _) =>
        Left(Fault.Backend("unresolved AMDGPU branch reached execution"))
      case Instruction.SLoad(dest, count, base, offset, scalarOffset, elementBytes) =>
        val address = pair(scalar, base) + offset + scalarOffset.fold(0L)(register => scalar(register.raw))
        dispatchMemory.readBytes(address, count.widen[Int > 0] * elementBytes.widen[Int > 0]) match {
          case Left(error)  => Left(error)
          case Right(bytes) =>
            (0 until count.raw).foreach { register =>
              scalar(dest.raw + register) = LittleEndian.decode(bytes, register * elementBytes.raw, elementBytes)
            }
            NextStep
        }
      case Instruction.SMove(dest, src) =>
        writeScalar32(dest, source(src, 0))
        NextStep
      case Instruction.SMultiplyImmediate(dest, value) =>
        val (result, overflow) = signedMultiplyImmediate(scalar(dest.raw), value)
        scalar(dest.raw) = result
        scalarCarry = overflow
        NextStep
      case Instruction.SAddImmediate(dest, value) =>
        val result = scalar(dest.raw) + value.toLong
        writeScalar32(dest, result)
        scalarCarry = result > Bits.Mask32 || result < 0L
        NextStep
      case Instruction.SMove64(dest, src) =>
        writeScalar64(dest, sourceScalar64(src))
        NextStep
      case Instruction.SSetReg(_, _) =>
        NextStep
      case Instruction.SSetMode(offset, size, src) =>
        val mask = (Bits.mask(size) << offset.raw).toInt & modeWritable
        mode = (mode & ~mask) | ((source(src, 0).toInt << offset.raw) & mask)
        NextStep
      case Instruction.SGetMode(dest, offset, size) =>
        scalar(dest.raw) = (Integer.toUnsignedLong(mode) >>> offset.raw) & Bits.mask(size)
        NextStep
      case Instruction.SGetPc(dest, offset) =>
        writeScalar64(dest, programCounter(index) + 4L)
        NextStep
      case Instruction.SSetPc(src) =>
        val target = sourceScalar64(src)
        programIndex(target)
          .map(index => Right(index))
          .getOrElse(Left(Fault.Backend(
            f"AMDGPU setpc at 0x${programCounter(index)}%X targets non-instruction 0x$target%X"
          )))
      case Instruction.SSwapPc(dest, src) =>
        val target = sourceScalar64(src)
        kernel.emulatedCalls.get(target) match {
          case Some(call) =>
            foreachActive { lane =>
              val (real, imaginary) =
                if (call.double)
                  (IEEE754.binary64ToDouble(pair(vector, 0, lane)), IEEE754.binary64ToDouble(pair(vector, 2, lane)))
                else
                  (
                    IEEE754.binary32ToFloat(Bits.low32(vector(0)(lane.raw))).toDouble,
                    IEEE754.binary32ToFloat(Bits.low32(vector(1)(lane.raw))).toDouble
                  )
              val (resultReal, resultImaginary) = call.function(real, imaginary)
              if (call.double) {
                val realBits      = IEEE754.doubleToBinary64(resultReal)
                val imaginaryBits = IEEE754.doubleToBinary64(resultImaginary)
                vector(0)(lane.raw) = Bits.low32(realBits).toLong
                vector(1)(lane.raw) = Bits.high32(realBits).toLong
                vector(2)(lane.raw) = Bits.low32(imaginaryBits).toLong
                vector(3)(lane.raw) = Bits.high32(imaginaryBits).toLong
              } else {
                vector(0)(lane.raw) = IEEE754.floatToBinary32(resultReal.toFloat).toLong
                vector(1)(lane.raw) = IEEE754.floatToBinary32(resultImaginary.toFloat).toLong
              }
            }
            NextStep
          case None =>
            programIndex(target) match {
              // Calls and returns share this primitive: the saved return address in the scalar pair is the frame.
              case Some(targetIndex) =>
                writeScalar64(dest, programCounter(index) + 4L)
                Right(targetIndex)
              case None =>
                kernel.functionTargets.get(target) match {
                  case Some(name) => Left(Fault.Unsupported(s"AMDGPU call to $name is not implemented"))
                  case None => Left(Fault.Backend(f"AMDGPU call target 0x$target%X is not present in the code object"))
                }
            }
        }
      case Instruction.SSaveExec(operation, dest, src, width) =>
        val previous = if (width.raw == 32) Bits.low32(active.bits).toLong else active.bits
        val result   = operation(saveExecSource(src, width), previous)
        saveExecWrite(dest, previous, width)
        saveExecWrite(Fixed.ExecLo, result, width)
        scalarCarry = result != 0L
        NextStep
      case Instruction.SUnary(operation, dest, src) =>
        val result = operation match {
          case ScalarUnaryOperation.U32FromF32 =>
            IEEE754.toIntegerSaturated(
              IEEE754.binary32ToFloat(Bits.low32(source(src, 0))).toDouble,
              32,
              false,
              IEEE754.Rounding.TowardZero
            )
          case ScalarUnaryOperation.TruncateF32 =>
            val value = java.lang.Float.intBitsToFloat(source(src, 0).toInt)
            java.lang.Float.floatToRawIntBits((if (value >= 0) math.floor(value.toDouble)
                                               else math.ceil(value.toDouble)).toFloat).toLong
          case ScalarUnaryOperation.F32FromU32 =>
            java.lang.Float.floatToRawIntBits(Bits.low32(source(src, 0)).toLong.toFloat).toLong
          case ScalarUnaryOperation.AbsoluteI32 =>
            Bits.low32(math.abs(Bits.low32(source(src, 0)).toInt).toLong).toLong
          case ScalarUnaryOperation.SignExtendI8 =>
            Bits.low32(Bits.signExtend(source(src, 0) & 0xffL, 8)).toLong
          case ScalarUnaryOperation.SignExtendI16 =>
            Bits.low32(Bits.signExtend(source(src, 0) & 0xffffL, 16)).toLong
          case ScalarUnaryOperation.CountBits32 => Bits.bitCount(source(src, 0), 32).raw.toLong
          case ScalarUnaryOperation.CountBits64 =>
            Bits.bitCount(sourceScalar64(src), 64).raw.toLong
          case ScalarUnaryOperation.ReverseBits32   => Bits.reverseBits(source(src, 0), 32)
          case ScalarUnaryOperation.Not32           => Bits.low32(~source(src, 0)).toLong
          case ScalarUnaryOperation.Not64           => ~sourceScalar64(src)
          case ScalarUnaryOperation.FirstBitHighI32 => signedFirstBitHigh32(source(src, 0))
          case ScalarUnaryOperation.FirstBitHighI64 => signedFirstBitHigh64(sourceScalar64(src))
          case ScalarUnaryOperation.FirstBitHighU32 => firstBitHigh32(source(src, 0))
          case ScalarUnaryOperation.FirstBitHighU64 =>
            val value = sourceScalar64(src)
            if (value == 0L) 0xffffffffL else java.lang.Long.numberOfLeadingZeros(value).toLong
          case ScalarUnaryOperation.ClearBit32 =>
            Bits.low32(scalar(dest.raw) & ~(1L << (source(src, 0).toInt & 31))).toLong
          case ScalarUnaryOperation.SetBit32 =>
            Bits.low32(scalar(dest.raw) | (1L << (source(src, 0).toInt & 31))).toLong
        }
        if (operation == ScalarUnaryOperation.Not64) writeScalar64(dest, result) else writeScalar32(dest, result)
        if (operation == ScalarUnaryOperation.Not32 || operation == ScalarUnaryOperation.Not64)
          scalarCarry = result != 0L
        NextStep
      case other => stepScalarAndVectorMove(other)
    }

    def stepScalarAndVectorMove(instruction: Instruction): Either[Fault, Int] = instruction match {
      case Instruction.VOutputModified(ignored, applied, dest, scale, width, clamp) =>
        if (!Mode.outputModifierApplies(mode, width)) stepScalarAndVectorMove(ignored)
        else stepScalarAndVectorMove(applied).map { next =>
          rewriteFloatResult(dest, width, outputModified(_, scale, width, clamp))
          next
        }
      case Instruction.VHalfResult(operation, dest, high) =>
        val registers = vector(dest)
        foreachActive(lane => preservedHalves(lane.raw) = registers(lane.raw))
        stepScalarAndVectorMove(operation).map { next =>
          foreachActive { lane =>
            val result = Bits.low16(registers(lane.raw)).toLong
            val kept   = preservedHalves(lane.raw)
            registers(lane.raw) = if (high) (kept & 0xffffL) | (result << 16) else (kept & 0xffff0000L) | result
          }
          next
        }
      case Instruction.VClampResult(operation, dest, width) =>
        stepScalarAndVectorMove(operation).map { next =>
          rewriteFloatResult(dest, width, clampFloat(_, width))
          next
        }
      case other => stepScalarAlu(other)
    }

    def stepScalarAlu(instruction: Instruction): Either[Fault, Int] = instruction match {
      case Instruction.SReciprocalF32(dest, src, modifiers, clamp) =>
        val result = 1.0f / IEEE754.binary32ToFloat(Bits.low32(modifiers(source(src, 0), 0, 32)))
        writeScalar32(dest, IEEE754.floatToBinary32(if (clamp) clampF32(result) else result).toLong)
        NextStep
      case Instruction.SFmaF32(dest, srcA, srcB, srcC) =>
        def operand(src: InstructionSource): Float =
          IEEE754.binary32ToFloat(Bits.low32(source(src, 0)))
        val result = Math.fma(operand(srcA), operand(srcB), operand(srcC))
        writeScalar32(dest, IEEE754.floatToBinary32(result).toLong)
        NextStep
      case Instruction.SBinary(operation, dest, srcA, srcB) =>
        val left   = Bits.low32(source(srcA, 0)).toLong
        val right  = Bits.low32(source(srcB, 0)).toLong
        val result = operation match {
          case ScalarBinaryOperation.And32 =>
            val value = left & right
            scalarCarry = value != 0L
            value
          case ScalarBinaryOperation.Or32 =>
            val value = left | right
            scalarCarry = value != 0L
            value
          case ScalarBinaryOperation.AddF32 =>
            java.lang.Float.floatToRawIntBits(
              java.lang.Float.intBitsToFloat(left.toInt) + java.lang.Float.intBitsToFloat(right.toInt)
            ).toLong
          case ScalarBinaryOperation.SubtractF32 =>
            java.lang.Float.floatToRawIntBits(
              java.lang.Float.intBitsToFloat(left.toInt) - java.lang.Float.intBitsToFloat(right.toInt)
            ).toLong
          case ScalarBinaryOperation.MultiplyF32 =>
            java.lang.Float.floatToRawIntBits(
              java.lang.Float.intBitsToFloat(left.toInt) * java.lang.Float.intBitsToFloat(right.toInt)
            ).toLong
          case ScalarBinaryOperation.MultiplyI32     => left * right
          case ScalarBinaryOperation.MultiplyHighU32 => IntegerArithmetic.multiplyHigh(left, right, 32, false)
          case ScalarBinaryOperation.MultiplyHighI32 => IntegerArithmetic.multiplyHigh(left, right, 32, true)
          case ScalarBinaryOperation.AddI32          =>
            val sum = Bits.low32(left + right).toLong
            scalarCarry = ((~(left ^ right) & (left ^ sum)) & 0x80000000L) != 0L
            sum
          case ScalarBinaryOperation.SubtractI32 =>
            val difference = Bits.low32(left - right).toLong
            scalarCarry = (((left ^ right) & (left ^ difference)) & 0x80000000L) != 0L
            difference
          case ScalarBinaryOperation.ShiftLeft32            => left << (right.toInt & 31)
          case ScalarBinaryOperation.ShiftLeftTwoAndAddU32  => (left << 2) + right
          case ScalarBinaryOperation.ShiftRightArithmetic32 => Bits.unsigned32(left.toInt >> (right.toInt & 31)).toLong
          case ScalarBinaryOperation.BitFieldExtractU32     =>
            val shift = (right & 0x3fL).toInt
            val width = ((right >>> 16) & 0x3fL).toInt
            if (width >= 32) Bits.low32(left >>> shift).toLong
            else if (width == 0) 0L else Bits.low32((left >>> shift) & ((1L << width) - 1L)).toLong
          case ScalarBinaryOperation.BitFieldExtractI32 =>
            val shift = (right & 0x3fL).toInt
            val width = ((right >>> 16) & 0x3fL).toInt
            if (width >= 32) Bits.low32(Bits.signExtend(left >>> shift, 32)).toLong
            else if (width == 0) 0L
            else {
              val bitWidth = width.unsafe[Int in 1 ~ 64]
              Bits.low32(Bits.signExtend((left >>> shift) & ((1L << width) - 1L), bitWidth)).toLong
            }
          case ScalarBinaryOperation.ShiftRight32 => left >>> (right.toInt & 31)
          case ScalarBinaryOperation.AndNot32     =>
            val value = left & ~right
            scalarCarry = value != 0L
            value
          case ScalarBinaryOperation.OrNot32 =>
            val value = Bits.low32(left | ~right).toLong
            scalarCarry = value != 0L
            value
          case ScalarBinaryOperation.Nor32 =>
            val value = Bits.low32(~(left | right)).toLong
            scalarCarry = value != 0L
            value
          case ScalarBinaryOperation.PackLowLow16  => Bits.low16(left).toLong | (Bits.low16(right).toLong << 16)
          case ScalarBinaryOperation.PackLowHigh16 =>
            Bits.low16(left).toLong | (Bits.low16(right >>> 16).toLong << 16)
          case ScalarBinaryOperation.PackHighHigh16 =>
            (Bits.low16(left >>> 16).toLong) | (Bits.low16(right >>> 16).toLong << 16)
          case ScalarBinaryOperation.AddU32 =>
            val sum = left + right
            scalarCarry = sum > Bits.Mask32
            sum
          case ScalarBinaryOperation.AddCarryU32 =>
            val sum = left + right + (if (scalarCarry) 1L else 0L)
            scalarCarry = sum > Bits.Mask32
            sum
          case ScalarBinaryOperation.SubtractU32 =>
            scalarCarry = left >= right
            left - right
          case ScalarBinaryOperation.SubtractBorrowU32 =>
            val borrow = if (scalarCarry) 0L else 1L
            scalarCarry = left >= right + borrow
            left - right - borrow
          case ScalarBinaryOperation.Xor32 =>
            val value = left ^ right
            scalarCarry = value != 0L
            value
        }
        writeScalar32(dest, result)
        NextStep
      case Instruction.SMinMax(operation, dest, srcA, srcB) =>
        val (selected, sourceZeroWon) = scalarMinMax(operation, source(srcA, 0), source(srcB, 0))
        writeScalar32(dest, selected)
        scalarCarry = sourceZeroWon
        NextStep
      case Instruction.SBinary64(operation, dest, srcA, srcB) =>
        val left   = sourceScalar64(srcA)
        val right  = sourceScalar64(srcB)
        val result = operation match {
          case ScalarBinary64Operation.Multiply             => left * right
          case ScalarBinary64Operation.AddNoCarry           => left + right
          case ScalarBinary64Operation.SubtractNoCarry      => left - right
          case ScalarBinary64Operation.And                  => left & right
          case ScalarBinary64Operation.AndNot               => left & ~right
          case ScalarBinary64Operation.Or                   => left | right
          case ScalarBinary64Operation.OrNot                => left | ~right
          case ScalarBinary64Operation.Nor                  => ~(left | right)
          case ScalarBinary64Operation.Xor                  => left ^ right
          case ScalarBinary64Operation.ShiftLeft            => left << (right.toInt & 63)
          case ScalarBinary64Operation.ShiftRight           => left >>> (right.toInt & 63)
          case ScalarBinary64Operation.ShiftRightArithmetic => left >> (right.toInt & 63)
          case ScalarBinary64Operation.BitFieldExtractI64   =>
            val shift = (right & 0x3fL).toInt
            val width = ((right >>> 16) & 0x3fL).toInt
            if (width >= 64) Bits.signExtend(left >>> shift, 64)
            else if (width == 0) 0L
            else {
              val bitWidth = width.unsafe[Int in 1 ~ 64]
              Bits.signExtend((left >>> shift) & Bits.mask(bitWidth), bitWidth)
            }
        }
        writeScalar64(dest, result)
        operation match {
          case ScalarBinary64Operation.Multiply | ScalarBinary64Operation.AddNoCarry | ScalarBinary64Operation.SubtractNoCarry =>
            ()
          case _ => scalarCarry = result != 0L
        }
        NextStep
      case Instruction.SCSelect(dest, srcA, srcB, width) =>
        val selected = if (scalarCarry) srcA else srcB
        if (width.raw == 64) writeScalar64(dest, sourceScalar64(selected))
        else writeScalar32(dest, source(selected, 0))
        NextStep
      case Instruction.SFloatCompare(operation, srcA, srcB) =>
        scalarCarry = compare(operation, source(srcA, 0), source(srcB, 0))
        NextStep
      case Instruction.SCmp(operation, srcA, srcB) =>
        val left32  = Bits.low32(source(srcA, 0)).toLong
        val right32 = Bits.low32(source(srcB, 0)).toLong
        scalarCarry = operation match {
          case ScalarCompareOperation.EqualU32          => left32 == right32
          case ScalarCompareOperation.GreaterI32        => left32.toInt > right32.toInt
          case ScalarCompareOperation.GreaterU32        => left32 > right32
          case ScalarCompareOperation.GreaterOrEqualU32 => left32 >= right32
          case ScalarCompareOperation.LessOrEqualU32    => left32 <= right32
          case ScalarCompareOperation.EqualI32          => left32.toInt == right32.toInt
          case ScalarCompareOperation.NotEqualI32       => left32.toInt != right32.toInt
          case ScalarCompareOperation.GreaterOrEqualI32 => left32.toInt >= right32.toInt
          case ScalarCompareOperation.LessOrEqualI32    => left32.toInt <= right32.toInt
          case ScalarCompareOperation.LessU32           => left32 < right32
          case ScalarCompareOperation.NotEqualU32       => left32 != right32
          case ScalarCompareOperation.LessI32           => left32.toInt < right32.toInt
          case ScalarCompareOperation.BitSet32          => Bits.isSet(left32, right32.toInt & 31)
          case ScalarCompareOperation.BitClear32        => !Bits.isSet(left32, right32.toInt & 31)
          case ScalarCompareOperation.EqualU64          => sourceScalar64(srcA) == sourceScalar64(srcB)
          case ScalarCompareOperation.NotEqualU64       => sourceScalar64(srcA) != sourceScalar64(srcB)
        }
        NextStep
      case other => stepVectorMove(other)
    }

    def stepVectorMove(instruction: Instruction): Either[Fault, Int] = instruction match {
      case Instruction.VDual(left, right) =>
        def result(operation: DualVector, lane: Lane): Long = {
          val a                         = source(operation.left, lane)
          val b                         = source(operation.right, lane)
          def float(value: Long): Float = java.lang.Float.intBitsToFloat(value.toInt)
          def bits(value: Float): Long  = java.lang.Float.floatToRawIntBits(value).toLong & Bits.Mask32
          operation.operation match {
            case DualVectorOperation.Move               => a
            case DualVectorOperation.AddF32             => bits(float(a) + float(b))
            case DualVectorOperation.SubtractF32        => bits(float(a) - float(b))
            case DualVectorOperation.ReverseSubtractF32 => bits(float(b) - float(a))
            case DualVectorOperation.MultiplyF32        => bits(float(a) * float(b))
            case DualVectorOperation.FmaF32 => bits(Math.fma(float(a), float(b), float(source(operation.addend, lane))))
            case DualVectorOperation.MinimumF32      => IEEE754.minNum(a, b, 32)
            case DualVectorOperation.MaximumF32      => IEEE754.maxNum(a, b, 32)
            case DualVectorOperation.ConditionalMask => if (carry(lane)) b else a
            case DualVectorOperation.AddU32          => a + b
            case DualVectorOperation.ShiftLeft32     => b << (a.toInt & 31)
            case DualVectorOperation.And32           => a & b
          }
        }
        foreachActive { lane =>
          val x = result(left, lane)
          val y = result(right, lane)
          vector(left.dest)(lane.raw) = Bits.low32(x).toLong
          vector(right.dest)(lane.raw) = Bits.low32(y).toLong
        }
        NextStep
      case Instruction.VMove(dest, src) =>
        move(dest, src)
        NextStep
      case Instruction.VMove64(dest, src) =>
        foreachActive { lane =>
          val value = src match {
            case InstructionSource.VectorRegister(index) => pair(vector, index, lane)
            case other                                   => sourceScalar64(other)
          }
          writeVectorValue(vector, dest, lane, value, 8)
        }
        NextStep
      case Instruction.VBitOp3(dest, srcA, srcB, srcC, table, half) =>
        foreachActive { lane =>
          val a      = source(srcA, lane)
          val b      = source(srcB, lane)
          val c      = source(srcC, lane)
          var result = 0L
          var bit    = 0
          while (bit < 8) {
            if ((table & (1 << bit)) != 0)
              result |= (if ((bit & 4) != 0) a else ~a) &
                (if ((bit & 2) != 0) b else ~b) & (if ((bit & 1) != 0) c else ~c)
            bit += 1
          }
          vector(dest)(lane.raw) = if (half) Bits.low16(result).toLong else Bits.low32(result).toLong
        }
        NextStep
      case Instruction.VAccumulatorRead(dest, src) =>
        foreachActive(lane => vector(dest)(lane.raw) = accumulator(src)(lane.raw))
        NextStep
      case Instruction.VAccumulatorWrite(dest, src) =>
        foreachActive(lane => accumulator(dest)(lane.raw) = source(src, lane))
        NextStep
      case Instruction.VAccumulatorMove(dest, src) =>
        foreachActive(lane => accumulator(dest)(lane.raw) = accumulator(src)(lane.raw))
        NextStep
      case Instruction.VMatrixF32(dest, srcA, srcB, srcC, size, blockBroadcast, blockId, accumulatorMask, broadcast) =>
        if (waveSize.raw != 64 || lanes.raw != 64 || active.count != 64)
          Left(Fault.InvalidRequest("MFMA requires a complete active wave64"))
        else {
          val dimension = size.raw
          val registers = if (dimension == 4) 4 else dimension * dimension / 64
          val depth     = if (dimension == 4) 1 else 64 / dimension
          val results   = Array.ofDim[Long](registers, 64)
          def input(value: InstructionSource, operand: Int, lane: Int, offset: Int >= 0 = 0): Float = {
            val bits = value match {
              case InstructionSource.VectorRegister(base) =>
                (if ((accumulatorMask.raw & (1 << operand)) != 0) accumulator else vector) (base.offsetBy(offset))(lane)
              case other => source(other, lane.unsafe[Int >= 0])
            }
            IEEE754.binary32ToFloat(Bits.low32(bits))
          }
          def broadcastLane(lane: Int): Int = broadcast.raw match {
            case 0     => lane
            case 1     => lane        % 32
            case 2     => lane        % 32 + 32
            case 3     => (lane + 16) % 64
            case group => lane        % 16 + (group - 4) * 16
          }
          var register: Int >= 0 = 0
          while (register.raw < registers) {
            for (lane <- 0 until 64) {
              val row =
                if (dimension == 4) register.raw
                else register.raw % 4 + lane / dimension * 4 + register.raw / 4 * (256 / dimension)
              val column = lane % dimension
              val block  = if (dimension == 4) lane / 4 else 0
              val aBlock = (block >> blockBroadcast.raw << blockBroadcast.raw) + blockId.raw
              var sum    = input(srcC, 2, lane, register)
              for (k <- 0 until depth)
                sum = Math.fma(
                  input(srcA, 0, aBlock * dimension + row + k * dimension),
                  input(srcB, 1, broadcastLane(block * dimension + column + k * dimension)),
                  sum
                )
              results(register.raw)(lane) = IEEE754.floatToBinary32(sum).toLong
            }
            register += 1
          }
          val destination = if ((accumulatorMask.raw & 4) != 0) accumulator else vector
          register = 0
          while (register.raw < registers) {
            for (lane <- 0 until 64)
              destination(dest.offsetBy(register))(lane) = results(register.raw)(lane)
            register += 1
          }
          NextStep
        }
      case Instruction.VWaveMatrixF16(dest, srcA, srcB, srcC, halfResult, high, negateLow, negateHigh) =>
        if (lanes.raw != waveSize.raw)
          Left(Fault.InvalidRequest("WMMA requires a complete wave"))
        else {
          val groups    = waveSize.raw / 16
          val registers = 16 / groups
          val results   = Array.ofDim[Long](registers, lanes.raw)
          def input(base: VectorRegisterIndex, lane: Int, element: Int >= 0, operand: Int): Float = {
            val upper = (element.raw & 1) != 0
            val bits  = vector(base.offsetBy(element / 2))(lane) >>> (if (upper) 16 else 0)
            val value = IEEE754.binary16ToFloat(Bits.low16(bits))
            if (((if (upper) negateHigh else negateLow) & (1 << operand)) != 0) -value else value
          }
          var register: Int >= 0 = 0
          while (register.raw < registers) {
            for (lane <- 0 until lanes.raw) {
              val row       = register.raw * groups + lane / 16
              val column    = lane % 16
              val groupBase = lane / 16 * 16
              val c         = srcC match {
                case InstructionSource.VectorRegister(base) => vector(base.offsetBy(register))(lane)
                case other                                  => source(other, lane.unsafe[Int >= 0])
              }
              val raw = if (halfResult)
                IEEE754.binary16ToFloat(Bits.low16(c >>> (if (high) 16 else 0)))
              else IEEE754.binary32ToFloat(Bits.low32(c))
              val absolute          = if ((negateHigh & 4) != 0) math.abs(raw) else raw
              var sum               = if ((negateLow & 4) != 0) -absolute else absolute
              var element: Int >= 0 = 0
              while (element.raw < 16) {
                sum =
                  Math.fma(input(srcA, groupBase + row, element, 0), input(srcB, groupBase + column, element, 1), sum)
                element += 1
              }
              results(register.raw)(lane) = if (halfResult)
                mergeD16(
                  vector(dest.offsetBy(register))(lane),
                  IEEE754.floatToBinary16(sum).toLong,
                  high
                )
              else IEEE754.floatToBinary32(sum).toLong
            }
            register += 1
          }
          register = 0
          while (register.raw < registers) {
            foreachActive { lane =>
              vector(dest.offsetBy(register))(lane.raw) = results(register.raw)(lane.raw)
            }
            register += 1
          }
          NextStep
        }
      case Instruction.VPackedF32(
            operation,
            dest,
            srcA,
            srcB,
            srcC,
            lowSelection,
            highSelection,
            negateLow,
            negateHigh
          ) =>
        foreachActive { lane =>
          // Snapshot sources before writing either destination dword.
          val left                                         = sourcePackedF32(srcA, lane)
          val right                                        = sourcePackedF32(srcB, lane)
          val third                                        = sourcePackedF32(srcC, lane)
          def calculate(selection: Int, negate: Int): Long = {
            val rawA   = IEEE754.binary32ToFloat(Bits.low32(left >>> ((selection & 1) * 32)))
            val rawB   = IEEE754.binary32ToFloat(Bits.low32(right >>> (((selection >>> 1) & 1) * 32)))
            val valueA = if ((negate & 1) != 0) -rawA else rawA
            val valueB = if ((negate & 2) != 0) -rawB else rawB
            val result = operation match {
              case PackedF32Operation.Add      => valueA + valueB
              case PackedF32Operation.Multiply => valueA * valueB
              case PackedF32Operation.Fma      =>
                val rawC =
                  IEEE754.binary32ToFloat(Bits.low32(third >>> (((selection >>> 2) & 1) * 32)))
                val valueC = if ((negate & 4) != 0) -rawC else rawC
                java.lang.Math.fma(valueA, valueB, valueC)
            }
            IEEE754.floatToBinary32(result).toLong
          }
          val low  = calculate(lowSelection, negateLow)
          val high = calculate(highSelection, negateHigh)
          writeVectorValue(vector, dest, lane, Bits.join32(low, high), 8)
        }
        NextStep
      case Instruction.VPackedMove32(dest, srcA, srcB, highA, highB) =>
        foreachActive { lane =>
          val left  = sourcePackedF32(srcA, lane) >>> (if (highA) 32 else 0)
          val right = sourcePackedF32(srcB, lane) >>> (if (highB) 32 else 0)
          writeVectorValue(vector, dest, lane, Bits.join32(left, right), 8)
        }
        NextStep
      case Instruction.VMoveRelative(dest, InstructionSource.VectorRegister(src)) =>
        val indexed = src.raw + (Bits.low32(scalar(Fixed.M0)).toLong & 0xffL).toInt
        if (indexed < 0 || indexed >= 256)
          Left(Fault.Backend(s"v_movrels_b32 indexed VGPR $indexed is out of range"))
        else {
          val sourceValues = vector(indexed.unsafe[VectorRegisterIndex])
          foreachActive(lane => vector(dest)(lane.raw) = Bits.low32(sourceValues(lane.raw)).toLong)
          NextStep
        }
      case Instruction.VMoveRelative(_, source) =>
        Left(Fault.Backend(
          s"v_movrels_b32 requires a vector-register source, found $source"
        ))
      case Instruction.VMoveRelativeDestination(dest, src) =>
        val indexed = dest.raw + (Bits.low32(scalar(Fixed.M0)).toLong & 0xffL).toInt
        if (indexed < 0 || indexed >= 256)
          Left(Fault.Backend(s"v_movreld_b32 indexed VGPR $indexed is out of range"))
        else {
          val destinationValues = vector(indexed.unsafe[VectorRegisterIndex])
          foreachActive(lane => destinationValues(lane.raw) = Bits.low32(source(src, lane)).toLong)
          NextStep
        }
      case Instruction.VNot(dest, src) =>
        foreachActive(lane => vector(dest)(lane.raw) = Bits.low32(~source(src, lane)).toLong)
        NextStep
      case Instruction.VSwap(dest, InstructionSource.VectorRegister(src)) =>
        foreachActive { lane =>
          val destinationValue = vector(dest)(lane.raw)
          vector(dest)(lane.raw) = Bits.low32(vector(src)(lane.raw)).toLong
          vector(src)(lane.raw) = Bits.low32(destinationValue).toLong
        }
        NextStep
      case Instruction.VSwap(_, _) =>
        Left(Fault.Backend("v_swap_b32 requires a vector-register source"))
      case Instruction.VBitReverse(dest, src) =>
        foreachActive(lane => vector(dest)(lane.raw) = Bits.reverseBits(source(src, lane), 32))
        NextStep
      case Instruction.VBitFind(operation, dest, src) =>
        foreachActive { lane =>
          val value  = Bits.signed32(source(src, lane))
          val result = operation match {
            case VectorBitFindOperation.FirstBitLow32 =>
              if (value == 0) -1L else Integer.numberOfTrailingZeros(value).toLong
            case VectorBitFindOperation.FirstBitHighI32 => signedFirstBitHigh32(value)
            // FFBH counts leading zeros rather than returning the highest set bit index.
            case VectorBitFindOperation.FirstBitHighU32 => firstBitHigh32(value)
          }
          vector(dest)(lane.raw) = Bits.unsigned32(result.toInt).toLong
        }
        NextStep
      case other => stepVectorConvertAndRoute(other)
    }

    def stepVectorConvertAndRoute(instruction: Instruction): Either[Fault, Int] = instruction match {
      case Instruction.VConvert(operation, dest, src, modifiers, clamp) =>
        foreachActive { lane =>
          val raw             = Bits.signed32(source(src, lane))
          val float32         = IEEE754.binary32ToFloat(Bits.low32(modifiers(source(src, lane), 0, 32)))
          def float64: Double = IEEE754.binary64ToDouble(modifiers(sourceFloating64(src, lane), 0, 64))
          def writeFloat32(value: Float): Unit =
            vector(dest)(lane.raw) = IEEE754.floatToBinary32(if (clamp) clampF32(value) else value).toLong
          def writeFloat64(value: Double): Unit =
            writeVectorValue(
              vector,
              dest,
              lane,
              IEEE754.doubleToBinary64(if (clamp) math.max(0.0, math.min(1.0, value)) else value),
              8
            )
          def toInteger(value: Double, width: Int in 1 ~ 64, signed: Boolean): Long =
            IEEE754.toIntegerSaturated(value, width, signed, IEEE754.Rounding.TowardZero)
          operation match {
            case VectorConvertOperation.FrexpExponentF64 =>
              val value = float64
              vector(dest)(lane.raw) = Bits.unsigned32(
                if (value == 0.0 || value.isNaN || value.isInfinite) 0 else Math.getExponent(value) + 1
              ).toLong
            case VectorConvertOperation.FrexpExponentF32 =>
              vector(dest)(lane.raw) = Bits.unsigned32(
                if (float32 == 0.0f || float32.isNaN || float32.isInfinite) 0 else Math.getExponent(float32) + 1
              ).toLong
            case VectorConvertOperation.FrexpMantissaF32 =>
              writeFloat32(
                if (float32 == 0.0f || float32.isNaN || float32.isInfinite) float32
                else Math.scalb(float32, -Math.getExponent(float32) - 1)
              )
            case VectorConvertOperation.FrexpMantissaF64 =>
              val value = float64
              writeFloat64(
                if (value == 0.0 || value.isNaN || value.isInfinite) value
                else Math.scalb(value, -Math.getExponent(value) - 1)
              )
            case VectorConvertOperation.ReciprocalF32 => writeFloat32(1.0f / float32)
            case VectorConvertOperation.ReciprocalF64 =>
              writeFloat64(1.0 / float64)
            case VectorConvertOperation.TruncateF32 =>
              writeFloat32((if (float32 >= 0.0f) math.floor(float32.toDouble) else math.ceil(float32.toDouble)).toFloat)
            case VectorConvertOperation.FractionF32 =>
              writeFloat32(math.min(float32 - math.floor(float32.toDouble).toFloat, Math.nextDown(1.0f)))
            case VectorConvertOperation.FloorF32            => writeFloat32(math.floor(float32.toDouble).toFloat)
            case VectorConvertOperation.RoundNearestEvenF32 => writeFloat32(Math.rint(float32.toDouble).toFloat)
            case VectorConvertOperation.TruncateF64         =>
              val value = float64
              writeFloat64(if (value >= 0.0) math.floor(value) else math.ceil(value))
            case VectorConvertOperation.FloorF64 =>
              writeFloat64(math.floor(float64))
            case VectorConvertOperation.FractionF64 =>
              val value = float64
              writeFloat64(math.min(value - math.floor(value), Math.nextDown(1.0)))
            case VectorConvertOperation.RoundNearestEvenF64 =>
              writeFloat64(Math.rint(float64))
            case VectorConvertOperation.ReciprocalSquareRootF32 =>
              writeFloat32((1.0 / math.sqrt(float32.toDouble)).toFloat)
            case VectorConvertOperation.ReciprocalSquareRootF64 =>
              writeFloat64(
                1.0 / math.sqrt(float64)
              )
            case VectorConvertOperation.SquareRootF32    => writeFloat32(math.sqrt(float32.toDouble).toFloat)
            case VectorConvertOperation.ExponentBase2F32 => writeFloat32(math.pow(2.0, float32.toDouble).toFloat)
            case trig @ (VectorConvertOperation.SineF32 | VectorConvertOperation.CosineF32) =>
              writeFloat32(trigF32(trig, float32))
            case VectorConvertOperation.LogarithmBase2F32 =>
              writeFloat32((math.log(float32.toDouble) / math.log(2.0)).toFloat)
            case VectorConvertOperation.I32FromF32 => vector(dest)(lane.raw) = toInteger(float32.toDouble, 32, true)
            case VectorConvertOperation.U32FromF32 => vector(dest)(lane.raw) = toInteger(float32.toDouble, 32, false)
            case VectorConvertOperation.I32FromF64 =>
              vector(dest)(lane.raw) = toInteger(IEEE754.binary64ToDouble(source64(src, lane)), 32, true)
            case VectorConvertOperation.U32FromF64 =>
              vector(dest)(lane.raw) = toInteger(IEEE754.binary64ToDouble(source64(src, lane)), 32, false)
            case byte @ (
                  VectorConvertOperation.F32FromUByte0 | VectorConvertOperation.F32FromUByte1 |
                  VectorConvertOperation.F32FromUByte2 | VectorConvertOperation.F32FromUByte3
                ) =>
              val index = byte.ordinal - VectorConvertOperation.F32FromUByte0.ordinal
              writeFloat32(((raw >>> (index * 8)) & 0xff).toFloat)
            case VectorConvertOperation.F32FromF64 =>
              writeFloat32(float64.toFloat)
            case VectorConvertOperation.F32FromF16 =>
              writeFloat32(IEEE754.binary16ToFloat(Bits.low16(modifiers(source(src, lane), 0, 16))))
            case VectorConvertOperation.F16FromF32 =>
              vector(dest)(lane.raw) = clampF16(IEEE754.floatToBinary16(float32).toLong, clamp)
            case VectorConvertOperation.I16FromF16 =>
              val half = IEEE754.binary16ToFloat(Bits.low16(modifiers(source(src, lane), 0, 16)))
              vector(dest)(lane.raw) = toInteger(half.toDouble, 16, true)
            case VectorConvertOperation.F64FromI32 => writeFloat64(raw.toDouble)
            case VectorConvertOperation.F64FromU32 => writeFloat64(Integer.toUnsignedLong(raw).toDouble)
            case VectorConvertOperation.F64FromF32 => writeFloat64(float32.toDouble)
            case VectorConvertOperation.F32FromI32 => writeFloat32(raw.toFloat)
            case VectorConvertOperation.F32FromU32 => writeFloat32(Integer.toUnsignedLong(raw).toFloat)
          }
        }
        NextStep
      case Instruction.VDPP(operation, src, control, fetchInactive) =>
        val values                        = vector(src)
        val executing                     = active.bits
        var enabled                       = 0L
        def readable(from: Lane): Boolean = from.raw < lanes.raw && (fetchInactive || active(from))
        control match {
          case DPPControl.DPP16(control, rowMask, bankMask, boundControl) =>
            // An unreadable source reads zero under BOUND_CTRL and leaves the lane unwritten otherwise.
            foreachActive { lane =>
              if (dppLaneEnabled(lane, rowMask, bankMask)) dppSourceLane(lane, control) match {
                case Some(from) if readable(from) =>
                  routed(lane.raw) = Bits.low32(values(from.raw)).toLong
                  enabled |= 1L << lane.raw
                case _ if boundControl =>
                  routed(lane.raw) = 0L
                  enabled |= 1L << lane.raw
                case _ => ()
              }
            }
          case DPPControl.DPP8(selectors) =>
            enabled = executing
            foreachActive { lane =>
              val from = dpp8SourceLane(lane, selectors)
              routed(lane.raw) = if (readable(from)) Bits.low32(values(from.raw)).toLong else 0L
            }
        }
        active.bits = enabled
        val result = stepScalarAndVectorMove(operation)
        active.bits = executing
        result
      case Instruction.VCndMask(dest, srcA, srcB, condition, modifiers) =>
        // Wave64 conditions occupy an SGPR pair; truncating it would make lanes 32-63 always select A.
        val conditionMask = waveConditionMask(sourceScalar64(condition), lanes)
        foreachActive { lane =>
          val selected = if ((conditionMask & (1L << lane.raw)) != 0L) 1 else 0
          vector(dest)(lane.raw) =
            Bits.low32(modifiers(source(if (selected == 1) srcB else srcA, lane), selected, 32)).toLong
        }
        NextStep
      case other => stepVectorArithmetic(other)
    }

    def stepVectorArithmetic(instruction: Instruction): Either[Fault, Int] = instruction match {
      case Instruction.VReadFirstLane(dest, src) =>
        active.first match {
          case None       => Left(Fault.Backend("v_readfirstlane_b32 has an empty EXEC mask"))
          case Some(lane) =>
            scalar(dest.raw) = Bits.low32(source(src, lane)).toLong
            NextStep
        }
      case Instruction.VReadLane(dest, src, lane) =>
        val selected = (source(lane, 0).toInt & (waveSize.raw - 1)).unsafe[Lane]
        scalar(dest.raw) = Bits.low32(source(src, selected)).toLong
        NextStep
      case Instruction.VWriteLane(dest, src, lane) =>
        val selected = (source(lane, 0).toInt & (waveSize.raw - 1)).unsafe[Lane]
        vector(dest)(selected.raw) = Bits.low32(source(src, selected)).toLong
        NextStep
      case Instruction.VMaskBitCount(dest, mask, base, high) =>
        val executionMask = active.bits
        foreachActive { lane =>
          vector(dest)(lane.raw) = mbCount(source(mask, lane), executionMask, lane, high, source(base, lane))
        }
        NextStep
      case Instruction.VBitCount(dest, src, base) =>
        foreachActive { lane =>
          vector(dest)(lane.raw) =
            Bits.low32(java.lang.Integer.bitCount(Bits.low32(source(
              src,
              lane
            )).toInt).toLong + Bits.low32(source(
              base,
              lane
            )).toLong).toLong
        }
        NextStep
      case Instruction.VMadF32(operation, dest, srcA, srcB, srcC, modifiers, clamp) =>
        foreachActive { lane =>
          def operand(src: InstructionSource, index: Int): Float =
            IEEE754.binary32ToFloat(Bits.low32(modifiers(source(src, lane), index, 32)))
          val left   = operand(srcA, 0)
          val right  = operand(srcB, 1)
          val addend = operand(srcC, 2)
          val result =
            if (operation == VectorMadF32Operation.FusedMultiplyAdd) Math.fma(left, right, addend)
            else left * right + addend
          vector(dest)(lane.raw) = IEEE754.floatToBinary32(if (clamp) clampF32(result) else result).toLong
        }
        NextStep
      case Instruction.VMadF64(dest, srcA, srcB, srcC, modifiers) =>
        foreachActive { lane =>
          def operand(src: InstructionSource, index: Int): Double =
            IEEE754.binary64ToDouble(modifiers(sourceFloating64(src, lane), index, 64))
          val bits = IEEE754.doubleToBinary64(Math.fma(operand(srcA, 0), operand(srcB, 1), operand(srcC, 2)))
          vector(dest)(lane.raw) = Bits.low32(bits).toLong
          vector(dest.offsetBy(1))(lane.raw) = Bits.high32(bits).toLong
        }
        NextStep
      case Instruction.VTrigPreopF64(dest, src, segment, modifiers) =>
        foreachActive { lane =>
          val value = IEEE754.binary64ToDouble(modifiers(sourceFloating64(src, lane), 0, 64))
          writeVectorValue(
            vector,
            dest,
            lane,
            IEEE754.doubleToBinary64(trigPreopF64(value, source(segment, lane).toInt)),
            8
          )
        }
        NextStep
      case Instruction.VDivScaleF32(dest, conditionDest, srcA, srcB, srcC, modifiers) =>
        conditionMask = 0L
        def denormal(value: Float): Boolean =
          value != 0.0f && !value.isNaN && !value.isInfinite && math.abs(value) < java.lang.Float.MIN_NORMAL
        foreachActive { lane =>
          val s0     = IEEE754.binary32ToFloat(Bits.low32(modifiers(source(srcA, lane), 0, 32)))
          val s1     = IEEE754.binary32ToFloat(Bits.low32(modifiers(source(srcB, lane), 1, 32)))
          val s2     = IEEE754.binary32ToFloat(Bits.low32(modifiers(source(srcC, lane), 2, 32)))
          var result = s0
          if (s1 == 0.0f || s2 == 0.0f) {
            result = Float.NaN
          } else if (Math.getExponent(s2) - Math.getExponent(s1) >= 96) {
            conditionMask |= 1L << lane.raw
            if (s0 == s1) result = Math.scalb(s0, 64)
          } else if (denormal(s1)) {
            result = Math.scalb(s0, 64)
          } else if (denormal(1.0f / s1) && denormal(s2 / s1)) {
            conditionMask |= 1L << lane.raw
            if (s0 == s1) result = Math.scalb(s0, 64)
          } else if (denormal(1.0f / s1)) {
            result = Math.scalb(s0, -64)
          } else if (denormal(s2 / s1)) {
            conditionMask |= 1L << lane.raw
            if (s0 == s2) result = Math.scalb(s0, 64)
          } else if (Math.getExponent(s2) <= 23 - 127) {
            result = Math.scalb(s0, 64)
          }
          vector(dest)(lane.raw) = IEEE754.floatToBinary32(result).toLong
        }
        if (waveSize.raw <= 32) writeScalar32(conditionDest, conditionMask)
        else writeScalar64(conditionDest, conditionMask)
        NextStep
      case Instruction.VDivScaleF64(dest, conditionDest, srcA, srcB, srcC, modifiers) =>
        conditionMask = 0L
        def denormal(value: Double): Boolean =
          value != 0.0 && !value.isNaN && !value.isInfinite && math.abs(value) < java.lang.Double.MIN_NORMAL
        foreachActive { lane =>
          val s0     = IEEE754.binary64ToDouble(modifiers(sourceFloating64(srcA, lane), 0, 64))
          val s1     = IEEE754.binary64ToDouble(modifiers(sourceFloating64(srcB, lane), 1, 64))
          val s2     = IEEE754.binary64ToDouble(modifiers(sourceFloating64(srcC, lane), 2, 64))
          var result = s0
          if (s1 == 0.0 || s2 == 0.0) {
            result = Double.NaN
          } else if (Math.getExponent(s2) - Math.getExponent(s1) >= 768) {
            conditionMask |= 1L << lane.raw
            if (s0 == s1) result = Math.scalb(s0, 128)
          } else if (denormal(s1)) {
            result = Math.scalb(s0, 128)
          } else if (denormal(1.0 / s1) && denormal(s2 / s1)) {
            conditionMask |= 1L << lane.raw
            if (s0 == s1) result = Math.scalb(s0, 128)
          } else if (denormal(1.0 / s1)) {
            result = Math.scalb(s0, -128)
          } else if (denormal(s2 / s1)) {
            conditionMask |= 1L << lane.raw
            if (s0 == s2) result = Math.scalb(s0, 128)
          } else if (Math.getExponent(s2) <= 53 - 1023) {
            result = Math.scalb(s0, 128)
          }
          val bits = IEEE754.doubleToBinary64(result)
          vector(dest)(lane.raw) = Bits.low32(bits).toLong
          vector(dest.offsetBy(1))(lane.raw) = Bits.high32(bits).toLong
        }
        if (waveSize.raw <= 32) writeScalar32(conditionDest, conditionMask)
        else writeScalar64(conditionDest, conditionMask)
        NextStep
      case Instruction.VDivFmasF64(dest, srcA, srcB, srcC, modifiers) =>
        foreachActive { lane =>
          val left   = IEEE754.binary64ToDouble(modifiers(sourceFloating64(srcA, lane), 0, 64))
          val right  = IEEE754.binary64ToDouble(modifiers(sourceFloating64(srcB, lane), 1, 64))
          val addend = IEEE754.binary64ToDouble(modifiers(sourceFloating64(srcC, lane), 2, 64))
          val result = Math.fma(left, right, addend)
          val scaled = (carry.bits & (1L << lane.raw)) != 0L
          val bits   = IEEE754.doubleToBinary64(if (scaled) Math.scalb(result, 64) else result)
          vector(dest)(lane.raw) = Bits.low32(bits).toLong
          vector(dest.offsetBy(1))(lane.raw) = Bits.high32(bits).toLong
        }
        NextStep
      case Instruction.VDivFixupF32(dest, quotient, denominator, numerator, absolute, negate) =>
        val modifiers = SourceModifiers(absolute, negate)
        foreachActive { lane =>
          def operand(value: InstructionSource, index: Int): Float =
            IEEE754.binary32ToFloat(Bits.low32(modifiers(source(value, lane), index, 32)))
          val q      = operand(quotient, 0)
          val d      = operand(denominator, 1)
          val n      = operand(numerator, 2)
          val result = divisionFixupF32(q, d, n)
          vector(dest)(lane.raw) = IEEE754.floatToBinary32(result).toLong
        }
        NextStep
      case Instruction.VDivFixupF64(dest, quotient, denominator, numerator, absolute, negate) =>
        val modifiers = SourceModifiers(absolute, negate)
        foreachActive { lane =>
          def operand(value: InstructionSource, index: Int): Double =
            IEEE754.binary64ToDouble(modifiers(sourceFloating64(value, lane), index, 64))
          val q    = operand(quotient, 0)
          val d    = operand(denominator, 1)
          val n    = operand(numerator, 2)
          val sign =
            if ((IEEE754.doubleToBinary64(n) ^ IEEE754.doubleToBinary64(d)) < 0L)
              -1.0
            else 1.0
          val result =
            if (n.isNaN || d.isNaN || (n.isInfinite && d.isInfinite) || (n == 0.0 && d == 0.0)) Double.NaN
            else if (d == 0.0 || n.isInfinite) Math.copySign(Double.PositiveInfinity, sign)
            else if (d.isInfinite || n == 0.0) Math.copySign(0.0, sign)
            else q
          val bits = IEEE754.doubleToBinary64(result)
          vector(dest)(lane.raw) = Bits.low32(bits).toLong
          vector(dest.offsetBy(1))(lane.raw) = Bits.high32(bits).toLong
        }
        NextStep
      case Instruction.VBinary(operation, dest, srcA, srcB, clamp, modifiers) =>
        val exponent                   = operation == VectorBinaryOperation.LoadExponentF32
        val width: Int in (16, 32, 64) = operation match {
          case VectorBinaryOperation.AddF16 | VectorBinaryOperation.MultiplyF16 | VectorBinaryOperation.MinimumF16 |
              VectorBinaryOperation.MaximumF16 | VectorBinaryOperation.FusedMultiplyAccumulateF16 => 16
          case _ => 32
        }
        foreachActive { lane =>
          val left  = Bits.low32(modifiers(source(srcA, lane), 0, width)).toLong
          val right = Bits.low32(if (exponent) source(srcB, lane) else modifiers(source(srcB, lane), 1, width)).toLong
          val previous                     = Bits.low32(vector(dest)(lane.raw)).toLong
          def float32(bits: Long): Float   = IEEE754.binary32ToFloat(Bits.low32(bits))
          def result32(value: Float): Long = IEEE754.floatToBinary32(if (clamp) clampF32(value) else value).toLong
          operation match {
            case VectorBinaryOperation.AddU32 =>
              vector(dest)(lane.raw) =
                if (clamp) IntegerArithmetic.addSaturated(left, right, 32, false) else Bits.low32(left + right).toLong
            case VectorBinaryOperation.MultiplyLowU32  => vector(dest)(lane.raw) = Bits.low32(left * right).toLong
            case VectorBinaryOperation.MultiplyHighU32 =>
              vector(dest)(lane.raw) = IntegerArithmetic.multiplyHigh(left, right, 32, false)
            case VectorBinaryOperation.MultiplyHighI32 =>
              vector(dest)(lane.raw) = Bits.low32(IntegerArithmetic.multiplyHigh(left, right, 32, true)).toLong
            case VectorBinaryOperation.ShiftRightArithmeticI32 =>
              vector(dest)(lane.raw) = Bits.unsigned32(right.toInt >> (left.toInt & 31)).toLong
            case VectorBinaryOperation.ShiftRightArithmeticI16 =>
              val signed = (Bits.low16Int(right).raw << 16) >> 16
              vector(dest)(lane.raw) = Bits.low16(signed >> (left.toInt & 15)).toLong
            case VectorBinaryOperation.ShiftLeft32 =>
              vector(dest)(lane.raw) = Bits.low32(right << (left.toInt & 31)).toLong
            case VectorBinaryOperation.ShiftRight32 => vector(dest)(lane.raw) = right >>> (left.toInt & 31)
            case VectorBinaryOperation.ShiftRight16 =>
              vector(dest)(lane.raw) = Bits.low16(right).toLong >>> (left.toInt & 15)
            case VectorBinaryOperation.AddU16 =>
              vector(dest)(lane.raw) =
                if (clamp) IntegerArithmetic.addSaturated(left, right, 16, false) else Bits.low16(left + right).toLong
            case VectorBinaryOperation.And32       => vector(dest)(lane.raw) = left & right
            case VectorBinaryOperation.Or32        => vector(dest)(lane.raw) = left | right
            case VectorBinaryOperation.Xor32       => vector(dest)(lane.raw) = left ^ right
            case VectorBinaryOperation.ShiftLeft16 =>
              vector(dest)(lane.raw) = Bits.low16(Bits.low16(right).toLong << (left.toInt & 15)).toLong
            case VectorBinaryOperation.SubtractU16 =>
              vector(dest)(lane.raw) =
                if (clamp) IntegerArithmetic.subtractSaturated(left, right, 16, false)
                else Bits.low16(left - right).toLong
            case VectorBinaryOperation.SubtractU32 =>
              vector(dest)(lane.raw) =
                if (clamp) IntegerArithmetic.subtractSaturated(left, right, 32, false)
                else Bits.low32(left - right).toLong
            case VectorBinaryOperation.SubtractReverseU32 =>
              vector(dest)(lane.raw) =
                if (clamp) IntegerArithmetic.subtractSaturated(right, left, 32, false)
                else unsignedSubtractReverse32(left, right)
            case VectorBinaryOperation.SubtractReverseF32 =>
              vector(dest)(lane.raw) = result32(float32(right) - float32(left))
            case VectorBinaryOperation.MultiplyU24     => vector(dest)(lane.raw) = unsignedMultiply24(left, right)
            case VectorBinaryOperation.MultiplyHighU24 => vector(dest)(lane.raw) = unsignedMultiplyHigh24(left, right)
            case VectorBinaryOperation.MultiplyI24     => vector(dest)(lane.raw) = signedMultiply24(left, right)
            case VectorBinaryOperation.MultiplyHighI24 => vector(dest)(lane.raw) = signedMultiplyHigh24(left, right)
            case VectorBinaryOperation.MultiplyLowU16  => vector(dest)(lane.raw) = Bits.low16(left * right).toLong
            case VectorBinaryOperation.MinimumU32      => vector(dest)(lane.raw) = math.min(left, right)
            case VectorBinaryOperation.MaximumU32      => vector(dest)(lane.raw) = math.max(left, right)
            case VectorBinaryOperation.MinimumI32      =>
              vector(dest)(lane.raw) = Bits.low32(math.min(left.toInt, right.toInt).toLong).toLong
            case VectorBinaryOperation.MinimumI16 => vector(dest)(lane.raw) =
                Bits.low16(math.min(
                  Bits.signExtend(Bits.low16(left).toLong, 16),
                  Bits.signExtend(Bits.low16(right).toLong, 16)
                )).toLong
            case VectorBinaryOperation.MinimumU16 =>
              vector(dest)(lane.raw) = Bits.low16(math.min(Bits.low16(left).toLong, Bits.low16(right).toLong)).toLong
            case VectorBinaryOperation.MinimumF32 =>
              val bits = IEEE754.minNum(left, right, 32)
              vector(dest)(lane.raw) = if (clamp) result32(float32(bits)) else bits
            case VectorBinaryOperation.MinimumF16 =>
              vector(dest)(lane.raw) = clampF16(IEEE754.minNum(left, right, 16), clamp)
            case VectorBinaryOperation.MaximumI32 =>
              vector(dest)(lane.raw) = math.max(left.toInt, right.toInt).toLong & Bits.Mask32
            case VectorBinaryOperation.MaximumI16 => vector(dest)(lane.raw) =
                Bits.low16(math.max(
                  Bits.signExtend(Bits.low16(left).toLong, 16),
                  Bits.signExtend(Bits.low16(right).toLong, 16)
                )).toLong
            case VectorBinaryOperation.MaximumU16 =>
              vector(dest)(lane.raw) = Bits.low16(math.max(Bits.low16(left).toLong, Bits.low16(right).toLong)).toLong
            case VectorBinaryOperation.MaximumF32 =>
              val bits = IEEE754.maxNum(left, right, 32)
              vector(dest)(lane.raw) = if (clamp) result32(float32(bits)) else bits
            case VectorBinaryOperation.MaximumF16 =>
              vector(dest)(lane.raw) = clampF16(IEEE754.maxNum(left, right, 16), clamp)
            case VectorBinaryOperation.LoadExponentF32 =>
              vector(dest)(lane.raw) = result32(Math.scalb(float32(left), right.toInt))
            case VectorBinaryOperation.AddF32      => vector(dest)(lane.raw) = result32(float32(left) + float32(right))
            case VectorBinaryOperation.MultiplyF32 => vector(dest)(lane.raw) = result32(float32(left) * float32(right))
            case VectorBinaryOperation.MultiplyAccumulateF32 =>
              val result = IEEE754.binary32ToFloat(Bits.low32(left)) * IEEE754.binary32ToFloat(
                Bits.low32(right)
              ) +
                IEEE754.binary32ToFloat(Bits.low32(previous))
              vector(dest)(lane.raw) = IEEE754.floatToBinary32(result).toLong
            case VectorBinaryOperation.AddF16 =>
              val result =
                IEEE754.binary16ToFloat(Bits.low16(left)) + IEEE754.binary16ToFloat(
                  Bits.low16(right)
                )
              vector(dest)(lane.raw) = clampF16(IEEE754.floatToBinary16(result).toLong, clamp)
            case VectorBinaryOperation.MultiplyF16 =>
              val result =
                IEEE754.binary16ToFloat(Bits.low16(left)) * IEEE754.binary16ToFloat(
                  Bits.low16(right)
                )
              vector(dest)(lane.raw) = clampF16(IEEE754.floatToBinary16(result).toLong, clamp)
            case VectorBinaryOperation.FusedMultiplyAccumulateF16 =>
              vector(dest)(lane.raw) = clampF16(IEEE754.fusedMultiplyAdd(left, right, previous, 16), clamp)
            case VectorBinaryOperation.SubtractF32 => vector(dest)(lane.raw) = result32(float32(left) - float32(right))
            case VectorBinaryOperation.AddCarryU32 =>
              val sum = left + right
              vector(dest)(lane.raw) = Bits.low32(sum).toLong
              carry(lane) = sum > Bits.Mask32
            case VectorBinaryOperation.AddWithCarryU32 =>
              val sum = left + right + (if (carry(lane)) 1L else 0L)
              vector(dest)(lane.raw) = Bits.low32(sum).toLong
              carry(lane) = sum > Bits.Mask32
            case VectorBinaryOperation.SubtractCarryU32 =>
              val (difference, borrow) = IntegerArithmetic.subtractUnsigned(left, right, false, 32)
              vector(dest)(lane.raw) = difference
              carry(lane) = borrow
            case VectorBinaryOperation.SubtractWithBorrowU32 =>
              val (difference, borrow) =
                IntegerArithmetic.subtractUnsigned(left, right, carry(lane), 32)
              vector(dest)(lane.raw) = difference
              carry(lane) = borrow
          }
        }
        NextStep
      case Instruction.VDot2F32F16(dest, srcA, srcB, accumulate) =>
        foreachActive { lane =>
          vector(dest)(lane.raw) = dot2F32F16(
            Bits.low32(source(srcA, lane)).toLong,
            Bits.low32(source(srcB, lane)).toLong,
            vector(dest)(lane.raw),
            accumulate
          ).toLong
        }
        NextStep
      case Instruction.VDot4U32U8(dest, srcA, srcB, srcC) =>
        foreachActive { lane =>
          vector(dest)(lane.raw) = dot4U32U8(
            source(srcA, lane),
            source(srcB, lane),
            source(srcC, lane)
          )
        }
        NextStep
      case Instruction.VBinary64(operation, dest, srcA, srcB, modifiers) =>
        val destinationLow  = vector(dest)
        val destinationHigh = vector(dest.offsetBy(1))
        val sourceARegister = srcA match {
          case InstructionSource.VectorRegister(index) => Some(index)
          case _                                       => None
        }
        val sourceBRegister = srcB match {
          case InstructionSource.VectorRegister(index) => Some(index)
          case _                                       => None
        }
        val invariantA =
          if (sourceARegister.nonEmpty) 0L else sourceFloating64(srcA, 0)
        // LDEXP's exponent is a signed dword, not a floating-point register pair.
        val scalarExponent = operation == VectorBinary64Operation.LoadExponent
        val invariantB     =
          if (scalarExponent || sourceBRegister.nonEmpty) 0L else sourceFloating64(srcB, 0)
        var lane: Int >= 0 = 0
        while (lane.raw < lanes.raw) {
          if (active(lane.raw.unsafe[Lane])) {
            val bitsA = sourceARegister.fold(invariantA)(register => pair(vector, register, lane))
            val bitsB =
              if (scalarExponent) 0L else sourceBRegister.fold(invariantB)(register => pair(vector, register, lane))
            val a    = modifiers(bitsA, 0, 64)
            val b    = modifiers(bitsB, 1, 64)
            val bits = operation match {
              case VectorBinary64Operation.LoadExponent =>
                IEEE754.doubleToBinary64(Math.scalb(IEEE754.binary64ToDouble(a), source(srcB, lane).toInt))
              case VectorBinary64Operation.Add =>
                IEEE754.doubleToBinary64(IEEE754.binary64ToDouble(a) + IEEE754.binary64ToDouble(b))
              case VectorBinary64Operation.Multiply =>
                IEEE754.doubleToBinary64(IEEE754.binary64ToDouble(a) * IEEE754.binary64ToDouble(b))
              case VectorBinary64Operation.Maximum => IEEE754.maxNum(a, b, 64)
              case VectorBinary64Operation.Minimum => IEEE754.minNum(a, b, 64)
            }
            destinationLow(lane.raw) = Bits.low32(bits).toLong
            destinationHigh(lane.raw) = Bits.high32(bits).toLong
          }
          lane += 1
        }
        NextStep
      case other => stepVectorPackedAndTernary(other)
    }

    def stepVectorPackedAndTernary(instruction: Instruction): Either[Fault, Int] = instruction match {
      case Instruction.VBinarySubDword(
            operation,
            dest,
            srcA,
            srcB,
            destSelection,
            destUnused,
            srcASelection,
            srcBSelection,
            srcASignExtend,
            srcBSignExtend
          ) =>
        foreachActive { lane =>
          val left   = selectSubDword(source(srcA, lane), srcASelection, srcASignExtend)
          val right  = selectSubDword(source(srcB, lane), srcBSelection, srcBSignExtend)
          val result = operation match {
            case VectorSubDwordOperation.ConditionalMask32 =>
              val conditionMask = Bits.low32(source(InstructionSource.Scalar(Fixed.VccLo), 0)).toLong
              if ((conditionMask & (1L << lane.raw)) != 0L) right else left
            case VectorSubDwordOperation.AddU32 => Bits.low32(left + right).toLong
            case VectorSubDwordOperation.AddU16 => Bits.low16(left + right).toLong
            case VectorSubDwordOperation.AddF16 =>
              IEEE754.floatToBinary16(
                IEEE754.binary16ToFloat(Bits.low16(left)) + IEEE754.binary16ToFloat(
                  Bits.low16(right)
                )
              ).toLong
            case VectorSubDwordOperation.AddCarryU32 =>
              val sum = left + right
              carry(lane) = sum > Bits.Mask32
              Bits.low32(sum).toLong
            case VectorSubDwordOperation.AddWithCarryU32 =>
              val sum = left + right + (if (carry(lane)) 1L else 0L)
              carry(lane) = sum > Bits.Mask32
              Bits.low32(sum).toLong
            case VectorSubDwordOperation.And32       => left & right
            case VectorSubDwordOperation.ShiftLeft16 =>
              Bits.low16(Bits.low16(right).toLong << (left.toInt & 15)).toLong
            case VectorSubDwordOperation.ShiftLeft32  => Bits.low32(right << (left.toInt & 31)).toLong
            case VectorSubDwordOperation.ShiftRight32 => right >>> (left.toInt & 31)
            case VectorSubDwordOperation.ShiftRight16 => Bits.low16(right).toLong >>> (left.toInt & 15)
            case VectorSubDwordOperation.MaximumI16   => Bits.low16(math.max(left, right)).toLong
            case VectorSubDwordOperation.MaximumF16   => IEEE754.maxNum(left, right, 16)
            case VectorSubDwordOperation.MinimumI16   => Bits.low16(math.min(left, right)).toLong
            case VectorSubDwordOperation.MinimumF16   => IEEE754.minNum(left, right, 16)
            case VectorSubDwordOperation.MinimumU16   =>
              Bits.low16(math.min(Bits.low16(left).toLong, Bits.low16(right).toLong)).toLong
            case VectorSubDwordOperation.MultiplyLowU16 => Bits.low16(left * right).toLong
            case VectorSubDwordOperation.MultiplyF16    =>
              val result =
                IEEE754.binary16ToFloat(Bits.low16(left)) * IEEE754.binary16ToFloat(
                  Bits.low16(right)
                )
              IEEE754.floatToBinary16(result).toLong
            case VectorSubDwordOperation.MultiplyU24      => unsignedMultiply24(left, right)
            case VectorSubDwordOperation.MultiplyI24      => signedMultiply24(left, right)
            case VectorSubDwordOperation.Or32             => left | right
            case VectorSubDwordOperation.Xor32            => left ^ right
            case VectorSubDwordOperation.SubtractU16      => Bits.low16(left - right).toLong
            case VectorSubDwordOperation.SubtractU32      => Bits.low32(left - right).toLong
            case VectorSubDwordOperation.SubtractCarryU32 =>
              val (difference, borrow) = IntegerArithmetic.subtractUnsigned(left, right, false, 32)
              carry(lane) = borrow
              difference
            case VectorSubDwordOperation.SubtractWithBorrowU32 =>
              val (difference, borrow) =
                IntegerArithmetic.subtractUnsigned(left, right, carry(lane), 32)
              carry(lane) = borrow
              difference
            case VectorSubDwordOperation.SubtractReverseU32 =>
              val (difference, borrow) = IntegerArithmetic.subtractUnsigned(right, left, false, 32)
              carry(lane) = borrow
              difference
            case VectorSubDwordOperation.SubtractReverseWithBorrowU32 =>
              val (difference, borrow) =
                IntegerArithmetic.subtractUnsigned(right, left, carry(lane), 32)
              carry(lane) = borrow
              difference
          }
          vector(dest)(lane.raw) = placeSubDword(vector(dest)(lane.raw), result, destSelection, destUnused)
        }
        NextStep
      case Instruction.VPackedBinary(operation, dest, srcA, srcB, srcAHighForHigh, srcBHighForHigh) =>
        foreachActive { lane =>
          val left                              = Bits.low32(source(srcA, lane)).toLong
          val right                             = Bits.low32(source(srcB, lane)).toLong
          def calculate(a: Long, b: Long): Long = operation match {
            case VectorPackedBinaryOperation.AddU16 => Bits.low16(a + b).toLong
            case VectorPackedBinaryOperation.AddF16 =>
              Bits.low16(IEEE754.floatToBinary16(
                IEEE754.binary16ToFloat(Bits.low16(a)) + IEEE754.binary16ToFloat(
                  Bits.low16(b)
                )
              ).toLong).toLong
            case VectorPackedBinaryOperation.MultiplyLowU16 => Bits.low16(a * b).toLong
            case VectorPackedBinaryOperation.MultiplyF16    =>
              Bits.low16(IEEE754.floatToBinary16(
                IEEE754.binary16ToFloat(Bits.low16(a)) * IEEE754.binary16ToFloat(
                  Bits.low16(b)
                )
              ).toLong).toLong
            case VectorPackedBinaryOperation.MaximumF16 => IEEE754.maxNum(a, b, 16)
            case VectorPackedBinaryOperation.MinimumF16 => IEEE754.minNum(a, b, 16)
            case VectorPackedBinaryOperation.MaximumI16 =>
              if (
                Bits.signExtend(
                  Bits.low16(a).toLong,
                  16
                ) >= Bits.signExtend(Bits.low16(b).toLong, 16)
              )
                Bits.low16(a).toLong
              else Bits.low16(b).toLong
            case VectorPackedBinaryOperation.MaximumU16   => math.max(Bits.low16(a).toLong, Bits.low16(b).toLong)
            case VectorPackedBinaryOperation.SubtractI16  => Bits.low16(a - b).toLong
            case VectorPackedBinaryOperation.ShiftRight16 => Bits.low16(b >>> (a.toInt & 15)).toLong
          }
          val low  = calculate(Bits.low16(left).toLong, Bits.low16(right).toLong)
          val high = calculate(
            if (srcAHighForHigh) left >>> 16 else Bits.low16(left).toLong,
            if (srcBHighForHigh) right >>> 16 else Bits.low16(right).toLong
          )
          vector(dest)(lane.raw) = low | (high << 16)
        }
        NextStep
      case Instruction.VPackB32F16(dest, srcA, srcB, modifiers, opSelection, clamp) =>
        foreachActive { lane =>
          def half(src: InstructionSource, index: Int): Long = {
            val selected = source(src, lane) >>> (if ((opSelection & (1 << index)) != 0) 16 else 0)
            Bits.low16(clampF16(modifiers(selected, index, 16), clamp)).toLong
          }
          vector(dest)(lane.raw) = half(srcA, 0) | (half(srcB, 1) << 16)
        }
        NextStep
      case Instruction.VPackedFmaF16(
            dest,
            srcA,
            srcB,
            srcC,
            lowSelection,
            highSelection,
            negateLow,
            negateHigh,
            clamp
          ) =>
        foreachActive { lane =>
          vector(dest)(lane.raw) = packedFmaF16(
            Bits.low32(source(srcA, lane)).toLong,
            Bits.low32(source(srcB, lane)).toLong,
            Bits.low32(source(srcC, lane)).toLong,
            lowSelection,
            highSelection,
            negateLow,
            negateHigh,
            clamp
          )
        }
        NextStep
      case Instruction.VFmaMixF16(
            dest,
            srcA,
            srcB,
            srcC,
            opSelection,
            opSelectionHi,
            negate,
            absolute,
            clamp,
            high,
            fused
          ) =>
        foreachActive { lane =>
          vector(dest)(lane.raw) = fmaMixF16(
            vector(dest)(lane.raw),
            Bits.low32(source(srcA, lane)).toLong,
            Bits.low32(source(srcB, lane)).toLong,
            Bits.low32(source(srcC, lane)).toLong,
            opSelection,
            opSelectionHi,
            negate,
            absolute,
            clamp,
            high,
            fused
          )
        }
        NextStep
      case Instruction.VFmaMixF32(
            dest,
            srcA,
            srcB,
            srcC,
            opSelection,
            opSelectionHi,
            negate,
            absolute,
            clamp,
            fused
          ) =>
        foreachActive { lane =>
          vector(dest)(lane.raw) = fmaMixF32(
            Bits.low32(source(srcA, lane)).toLong,
            Bits.low32(source(srcB, lane)).toLong,
            Bits.low32(source(srcC, lane)).toLong,
            opSelection,
            opSelectionHi,
            negate,
            absolute,
            clamp,
            fused
          )
        }
        NextStep
      case Instruction.VAddCarry(dest, carryDest, srcA, srcB, carryIn, clamp) =>
        val inputCarry = carryIn match {
          case Some(mask) => sourceScalar64(mask)
          case None       => 0L
        }
        conditionMask = 0L
        foreachActive { lane =>
          val left            = Bits.low32(source(srcA, lane)).toLong
          val right           = Bits.low32(source(srcB, lane)).toLong
          val (sum, overflow) =
            IntegerArithmetic.addUnsigned(left, right, (inputCarry & (1L << lane.raw)) != 0L, 32)
          vector(dest)(lane.raw) = if (clamp && overflow) Bits.Mask32 else sum
          if (overflow) conditionMask |= 1L << lane.raw
        }
        updateLaneMask(carryDest, conditionMask)
        NextStep
      case Instruction.VSubCarry(dest, carryDest, srcA, srcB, carryIn, clamp) =>
        val inputCarry = carryIn match {
          case Some(mask) => sourceScalar64(mask)
          case None       => 0L
        }
        conditionMask = 0L
        foreachActive { lane =>
          val left                 = Bits.low32(source(srcA, lane)).toLong
          val right                = Bits.low32(source(srcB, lane)).toLong
          val borrowIn             = (inputCarry & (1L << lane.raw)) != 0L
          val (difference, borrow) = IntegerArithmetic.subtractUnsigned(left, right, borrowIn, 32)
          vector(dest)(lane.raw) = if (clamp && borrow) 0L else difference
          if (borrow) conditionMask |= 1L << lane.raw
        }
        val carryMask = conditionMask
        updateLaneMask(carryDest, carryMask)
        NextStep
      case Instruction.VSubReverseCarry(dest, carryDest, srcA, srcB, carryIn, clamp) =>
        val inputCarry = carryIn match {
          case Some(mask) => sourceScalar64(mask)
          case None       => 0L
        }
        conditionMask = 0L
        foreachActive { lane =>
          val left                 = Bits.low32(source(srcA, lane)).toLong
          val right                = Bits.low32(source(srcB, lane)).toLong
          val borrowIn             = (inputCarry & (1L << lane.raw)) != 0L
          val (difference, borrow) = IntegerArithmetic.subtractUnsigned(right, left, borrowIn, 32)
          vector(dest)(lane.raw) = if (clamp && borrow) 0L else difference
          if (borrow) conditionMask |= 1L << lane.raw
        }
        val carryMask = conditionMask
        updateLaneMask(carryDest, carryMask)
        NextStep
      case Instruction.VTernary(operation, dest, srcA, srcB, srcC, modifiers, clamp) =>
        val width: Int in (16, 32, 64) = if (operation == VectorTernaryOperation.FusedMultiplyAddF16) 16 else 32
        foreachActive { lane =>
          val left   = Bits.low32(modifiers(source(srcA, lane), 0, width)).toLong
          val right  = Bits.low32(modifiers(source(srcB, lane), 1, width)).toLong
          val addend = Bits.low32(modifiers(source(srcC, lane), 2, width)).toLong
          vector(dest)(lane.raw) = Bits.low32(
            operation match {
              case VectorTernaryOperation.Add3U32     => left + right + addend
              case VectorTernaryOperation.Or3B32      => left | right | addend
              case VectorTernaryOperation.Xor3B32     => left ^ right ^ addend
              case VectorTernaryOperation.Maximum3I32 =>
                math.max(math.max(left.toInt, right.toInt), addend.toInt).toLong
              case VectorTernaryOperation.Maximum3U32 => math.max(math.max(left, right), addend)
              case VectorTernaryOperation.Minimum3I32 =>
                math.min(math.min(left.toInt, right.toInt), addend.toInt).toLong
              case VectorTernaryOperation.Minimum3F32 => IEEE754.minNum(IEEE754.minNum(left, right, 32), addend, 32)
              case VectorTernaryOperation.Minimum3U32 => math.min(math.min(left, right), addend)
              case VectorTernaryOperation.Median3I32  =>
                val a = left.toInt
                val b = right.toInt
                val c = addend.toInt
                math.max(math.min(a, b), math.min(math.max(a, b), c)).toLong
              case VectorTernaryOperation.Median3U32 =>
                val a = Bits.low32(left).toLong
                val b = Bits.low32(right).toLong
                val c = Bits.low32(addend).toLong
                math.max(math.min(a, b), math.min(math.max(a, b), c))
              case VectorTernaryOperation.Median3I16 =>
                val a = Bits.signExtend(Bits.low16(left).toLong, 16)
                val b = Bits.signExtend(Bits.low16(right).toLong, 16)
                val c = Bits.signExtend(Bits.low16(addend).toLong, 16)
                Bits.low16(math.max(math.min(a, b), math.min(math.max(a, b), c))).toLong
              case VectorTernaryOperation.Median3U16 =>
                val a = Bits.low16(left).toLong
                val b = Bits.low16(right).toLong
                val c = Bits.low16(addend).toLong
                Bits.low16(math.max(math.min(a, b), math.min(math.max(a, b), c))).toLong
              case VectorTernaryOperation.Maximum3I16 => Bits.low16(math.max(
                  math.max(
                    Bits.signExtend(Bits.low16(left).toLong, 16),
                    Bits.signExtend(Bits.low16(right).toLong, 16)
                  ),
                  Bits.signExtend(Bits.low16(addend).toLong, 16)
                )).toLong
              case VectorTernaryOperation.Maximum3U16 =>
                Bits.low16(math.max(
                  math.max(Bits.low16(left).toLong, Bits.low16(right).toLong),
                  Bits.low16(addend).toLong
                )).toLong
              case VectorTernaryOperation.Minimum3I16 => Bits.low16(math.min(
                  math.min(
                    Bits.signExtend(Bits.low16(left).toLong, 16),
                    Bits.signExtend(Bits.low16(right).toLong, 16)
                  ),
                  Bits.signExtend(Bits.low16(addend).toLong, 16)
                )).toLong
              case VectorTernaryOperation.Minimum3U16 =>
                Bits.low16(math.min(
                  math.min(Bits.low16(left).toLong, Bits.low16(right).toLong),
                  Bits.low16(addend).toLong
                )).toLong
              case VectorTernaryOperation.MultiplyAddU16 | VectorTernaryOperation.MultiplyAddLegacyU16 if clamp =>
                IntegerArithmetic.multiplyAddSaturated(left, right, addend, 16, false)
              case VectorTernaryOperation.MultiplyAddU16 =>
                Bits.low16(Bits.low16(left).toLong * Bits.low16(right).toLong + Bits.low16(addend).toLong).toLong
              case VectorTernaryOperation.FusedMultiplyAddF16 => IEEE754.fusedMultiplyAdd(left, right, addend, 16)
              case VectorTernaryOperation.DivisionFusedMultiplyAddF32 =>
                val result = IEEE754.binary32ToFloat(Bits.low32(IEEE754.fusedMultiplyAdd(left, right, addend, 32)))
                val scaled = (carry.bits & (1L << lane.raw)) != 0L
                IEEE754.floatToBinary32(if (scaled) Math.scalb(result, 32) else result).toLong
              case VectorTernaryOperation.MultiplyAddLegacyU16 =>
                Bits.low16(Bits.low16(left).toLong * Bits.low16(right).toLong + Bits.low16(addend).toLong).toLong
              case VectorTernaryOperation.MultiplyAddU24 if clamp =>
                IntegerArithmetic.multiplyAddSaturated(left & 0xffffffL, right & 0xffffffL, addend, 32, false)
              case VectorTernaryOperation.MultiplyAddU24          => unsignedMultiplyAdd24(left, right, addend)
              case VectorTernaryOperation.MultiplyAddI24 if clamp =>
                IntegerArithmetic.multiplyAddSaturated(
                  Bits.signExtend(left, 24),
                  Bits.signExtend(right, 24),
                  addend,
                  32,
                  true
                )
              case VectorTernaryOperation.MultiplyAddI24 => Bits.low32(signedMultiply24(left, right) + addend).toLong
              // ALIGNBIT shifts the window SRC0:SRC1, with SRC0 as the high dword
              case VectorTernaryOperation.AlignBit32         => Bits.funnelShiftRight(right, left, addend.toInt & 31)
              case VectorTernaryOperation.BitFieldInsert32   => (left & right) | (~left & addend)
              case VectorTernaryOperation.AndOr32            => (left & right) | addend
              case VectorTernaryOperation.BitFieldExtractI32 =>
                val width = addend.toInt & 31
                if (width == 0) 0L
                else {
                  val mask = (1L << width) - 1L
                  Bits.low32(Bits.signExtend(
                    (left >>> (right.toInt & 31)) & mask,
                    width.unsafe[Int in 1 ~ 64]
                  )).toLong
                }
              case VectorTernaryOperation.BitFieldExtractU32 =>
                val width = addend.toInt & 31
                if (width == 0) 0L else Bits.low32(left >>> (right.toInt & 31)).toLong & ((1L << width) - 1L)
              case VectorTernaryOperation.XorAddU32       => Bits.low32((left ^ right) + addend).toLong
              case VectorTernaryOperation.AddShiftLeftU32 => (left + right) << (addend.toInt & 31)
              case VectorTernaryOperation.ShiftLeftAddU32 => (left << (right.toInt & 31)) + addend
              case VectorTernaryOperation.ShiftLeftOr32   => (left << (right.toInt & 31)) | addend
              case VectorTernaryOperation.PermuteBytes32  => permuteBytes(left, right, addend)
            }
          ).toLong
        }
        NextStep
      case Instruction.VMadU64U32(dest, carryDest, srcA, srcB, srcC, clamp) =>
        conditionMask = 0L
        foreachActive { lane =>
          val rawA    = source(srcA, lane)
          val rawB    = source(srcB, lane)
          val addend  = source64(srcC, lane)
          val product = Bits.low32(rawA).toLong * Bits.low32(rawB).toLong
          val sum     = product + addend
          val result  =
            if (clamp) IntegerArithmetic.multiplyAddSaturated(
              Bits.low32(rawA).toLong,
              Bits.low32(rawB).toLong,
              addend,
              64,
              false
            )
            else sum
          vector(dest)(lane.raw) = Bits.low32(result).toLong
          vector(dest.offsetBy(1))(lane.raw) = Bits.high32(result).toLong
          if (java.lang.Long.compareUnsigned(sum, product) < 0) conditionMask |= 1L << lane.raw
        }
        updateLaneMask(carryDest, conditionMask)
        NextStep
      case Instruction.VMadI64I32(dest, carryDest, srcA, srcB, srcC, clamp) =>
        conditionMask = 0L
        foreachActive { lane =>
          val rawA    = source(srcA, lane)
          val rawB    = source(srcB, lane)
          val addend  = source64(srcC, lane)
          val left    = Bits.signExtend(Bits.low32(rawA).toLong, 32)
          val right   = Bits.signExtend(Bits.low32(rawB).toLong, 32)
          val product = left * right
          val sum     = product + addend
          val result  = if (clamp) IntegerArithmetic.multiplyAddSaturated(left, right, addend, 64, true) else sum
          vector(dest)(lane.raw) = Bits.low32(result).toLong
          vector(dest.offsetBy(1))(lane.raw) = Bits.high32(result).toLong
          // VCC receives the unsigned carry out of the 64-bit addition of the two's-complement product.
          if (java.lang.Long.compareUnsigned(sum, product) < 0) conditionMask |= 1L << lane.raw
        }
        updateLaneMask(carryDest, conditionMask)
        NextStep
      case other => stepCompareAndCrossLane(other)
    }

    def stepCompareAndCrossLane(instruction: Instruction): Either[Fault, Int] =
      instruction match {
        case Instruction.VCompare(operation, dest, srcA, srcB, modifiers, selection) =>
          def operand(src: InstructionSource, lane: Int >= 0, index: Int): Long = operation.kind match {
            case VectorComparisonKind.Signed64 | VectorComparisonKind.Unsigned64 => source64(src, lane)
            case VectorComparisonKind.ClassFloat32 | VectorComparisonKind.ClassFloat64 if index == 1 =>
              source(src, lane)
            case VectorComparisonKind.Float64 | VectorComparisonKind.ClassFloat64 =>
              modifiers(sourceFloating64(src, lane), index, 64)
            case kind =>
              val value = selection match {
                case Some(sources) if index == 0 =>
                  selectSubDword(source(src, lane), sources.selectionA, sources.signExtendA)
                case Some(sources) => selectSubDword(source(src, lane), sources.selectionB, sources.signExtendB)
                case None          => source(src, lane)
              }
              kind match {
                case VectorComparisonKind.Float16                                     => modifiers(value, index, 16)
                case VectorComparisonKind.Float32 | VectorComparisonKind.ClassFloat32 => modifiers(value, index, 32)
                case _                                                                => value
              }
          }
          conditionMask = 0L
          foreachActive { lane =>
            if (compare(operation, operand(srcA, lane, 0), operand(srcB, lane, 1))) conditionMask |= 1L << lane.raw
          }
          if (operation.updateExec) active.bits = conditionMask
          updateLaneMask(dest, conditionMask)
          NextStep
        case Instruction.VShiftLeftAdd64(dest, src, amount, addend) =>
          foreachActive { lane =>
            // CDNA3 uses the low three count bits; counts 5-7 act as zero.
            val count = source(amount, lane).toInt & 7
            val shift = if (count <= 4) count else 0
            val value = (source64(src, lane) << shift) + source64(addend, lane)
            writeVectorValue(vector, dest, lane, value, 8)
          }
          NextStep
        case Instruction.VShiftLeft64(dest, amount, src) =>
          foreachActive { lane =>
            val shifted = source64(src, lane) << (source(amount, lane).toInt & 63)
            vector(dest)(lane.raw) = Bits.low32(shifted).toLong
            vector(dest.offsetBy(1))(lane.raw) = Bits.high32(shifted).toLong
          }
          NextStep
        case Instruction.VShiftRight64(dest, amount, src) =>
          foreachActive { lane =>
            val shifted = source64(src, lane) >>> (source(amount, lane).toInt & 63)
            vector(dest)(lane.raw) = Bits.low32(shifted).toLong
            vector(dest.offsetBy(1))(lane.raw) = Bits.high32(shifted).toLong
          }
          NextStep
        case Instruction.VShiftRightArithmetic64(dest, amount, src) =>
          foreachActive { lane =>
            val shifted = source64(src, lane) >> (source(amount, lane).toInt & 63)
            vector(dest)(lane.raw) = Bits.low32(shifted).toLong
            vector(dest.offsetBy(1))(lane.raw) = Bits.high32(shifted).toLong
          }
          NextStep
        case Instruction.DSPermute(dest, addressRegister, src, offset) =>
          // Scatter: EXEC gates source lanes, so a source may write an inactive destination lane; lanes receiving
          // nothing keep their value. Operands are snapshotted because the destination may alias them.
          val addresses = vector(addressRegister).clone()
          val sources   = vector(src).clone()
          val result    = vector(dest).clone()
          foreachActive { lane =>
            val destinationLane = permuteDestinationLane(addresses(lane.raw), offset.toLong, waveSize)
            if (destinationLane.raw < lanes.raw)
              result(destinationLane.raw) = Bits.low32(sources(lane.raw)).toLong
          }
          foreachLane { lane => vector(dest)(lane.raw) = result(lane.raw) }
          NextStep
        case Instruction.DSBPermute(dest, addressRegister, src, offset) =>
          val addresses = vector(addressRegister).clone()
          val sources   = vector(src).clone()
          foreachActive { lane =>
            // The byte address includes the instruction offset.
            val sourceLane = bpermuteSourceLane(addresses(lane.raw), offset.toLong)
            vector(dest)(lane.raw) = if (sourceLane.raw < lanes.raw) Bits.low32(sources(sourceLane.raw)).toLong else 0L
          }
          NextStep
        case Instruction.DSSwizzle(dest, src, control) =>
          // The offset packs AND[4:0], OR[9:5] and XOR[14:10] lane masks; the source may be the destination.
          val sources = vector(src).clone()
          val andMask = control & 0x1f
          val orMask  = (control >>> 5) & 0x1f
          val xorMask = (control >>> 10) & 0x1f
          foreachActive { lane =>
            val sourceLane = ((lane.raw & andMask) | orMask) ^ xorMask
            vector(dest)(lane.raw) =
              if (sourceLane >= 0 && sourceLane < lanes.raw) Bits.low32(sources(sourceLane)).toLong else 0L
          }
          NextStep
        case other => stepMemory(other)
      }

    def run(): Either[Fault, WaveBoundary] = {
      var boundary: WaveBoundary | Null = null
      var failure: Fault | Null         = null
      while (boundary == null && failure == null)
        control.checkpoint(math.max(1, active.count).unsafe[Int > 0]) match {
          case Left(fault) => failure = fault
          case Right(())   =>
            step(instructionIndex) match {
              case Left(Fault.Memory(message)) =>
                failure = Fault.Memory(f"AMDGPU memory access at pc 0x${programCounter(instructionIndex)}%X: $message")
              case Left(error)        => failure = error
              case Right(Step.Next)   => instructionIndex += 1
              case Right(Step.Exit)   => boundary = WaveBoundary.End
              case Right(Step.Arrive) =>
                instructionIndex += 1
                boundary = WaveBoundary.Barrier
              case Right(Step.Blocked) => boundary = WaveBoundary.Barrier
              case Right(target)       => instructionIndex = target
            }
        }
      if (failure == null) Right(boundary.nn) else Left(failure)
    }
  }

  private def pair(registers: Array[Long], first: ScalarRegisterIndex): Long =
    Bits.join32(registers(first.raw), registers(first.offsetBy(1).raw))

  private def pair(registers: VectorRegisters, first: VectorRegisterIndex, lane: Int >= 0): Long =
    Bits.join32(registers(first)(lane.raw), registers(first.offsetBy(1))(lane.raw))

  private def setPair(registers: Array[Long], first: ScalarRegisterIndex, value: Long): Unit = {
    val high = first.offsetBy(1)
    registers(first.raw) = Bits.low32(value).toLong
    registers(high.raw) = Bits.high32(value).toLong
  }

  def effectiveGroupSegmentSize(fixed: Int, dynamic: Int): Int =
    Math.addExact(fixed, dynamic)

  def ldsOffset(address: Long): Long = address & 0xffffL

  // A primitive sentinel keeps the per-lane LDS path allocation-free.
  private inline val InvalidLdsOffset = -1

  private def checkedLdsOffset(lds: Array[Byte], address: Long, size: Int): Int = {
    val offset = ldsOffset(address)
    if (ByteRange.contains(lds.length, offset, size.toLong)) offset.toInt else InvalidLdsOffset
  }

  private def ldsFailure(address: Long, size: Int): Fault = {
    val offset = ldsOffset(address)
    Fault.Memory(
      f"out-of-bounds AMDGPU LDS read at 0x$address%x (offset 0x$offset%x, $size bytes)"
    )
  }

  private def writeLDS(lds: Array[Byte], address: Long, bytes: Array[Byte]): Either[Fault, Unit] = {
    val offset = ldsOffset(address)
    Either.cond(
      ByteRange.contains(lds.length, offset, bytes.length.toLong), {
        Array.copy(bytes, 0, lds, offset.toInt, bytes.length)
        ()
      },
      Fault.Memory(
        f"out-of-bounds AMDGPU LDS write at 0x$address%x (offset 0x$offset%x, ${bytes.length} bytes)"
      )
    )
  }

  private def vectorBytes(
      registers: VectorRegisters,
      first: VectorRegisterIndex,
      lane: Int >= 0,
      size: Int >= 0
  ): Array[Byte] =
    Array.tabulate(size.raw) { byte =>
      val register = first.offsetBy((byte / 4).unsafe[Int >= 0])
      ((registers(register)(lane.raw) >>> ((byte % 4) * 8)) & 0xffL).toByte
    }

  /** Consume the owned memory snapshot directly; copying it into another array adds no isolation. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def writeVectorBytes(
      registers: VectorRegisters,
      first: VectorRegisterIndex,
      lane: Int >= 0,
      bytes: Vec[Byte]
  ): Unit = {
    var offset: Int >= 0 = 0
    while (offset.raw < bytes.size) {
      val width = math.min(4, bytes.size - offset.raw).unsafe[Int in 1 ~ 4]
      // The loop guard proves at least one byte remains; each register consumes at most four bytes.
      registers(first.offsetBy(offset / 4))(lane.raw) = LittleEndian.decode(
        bytes,
        offset.raw,
        width.widen[Int in 0 ~ 8]
      )
      offset += width.widen[Int >= 0]
    }
  }

  private def writeVectorBytes(
      registers: VectorRegisters,
      first: VectorRegisterIndex,
      lane: Int >= 0,
      bytes: Array[Byte]
  ): Unit = writeVectorBytes(registers, first, lane, bytes, 0, bytes.length)

  // Pack complete VGPR words in one pass on the memory hot path.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def writeVectorBytes(
      registers: VectorRegisters,
      first: VectorRegisterIndex,
      lane: Int >= 0,
      bytes: Array[Byte],
      sourceOffset: Int,
      size: Int
  ): Unit = {
    // A partial trailing dword has its unused high bits cleared.
    var offset: Int >= 0 = 0
    while (offset.raw <= size - 4) {
      registers(first.offsetBy(offset / 4))(lane.raw) =
        (bytes(sourceOffset + offset.raw).toLong & 0xffL) |
          ((bytes(sourceOffset + offset.raw + 1).toLong & 0xffL) << 8) |
          ((bytes(sourceOffset + offset.raw + 2).toLong & 0xffL) << 16) |
          ((bytes(sourceOffset + offset.raw + 3).toLong & 0xffL) << 24)
      offset += 4
    }
    if (offset.raw < size) {
      var value = 0L
      var shift = 0
      while (offset.raw + shift < size) {
        value |= (bytes(sourceOffset + offset.raw + shift).toLong & 0xffL) << (shift * 8)
        shift += 1
      }
      registers(first.offsetBy(offset / 4))(lane.raw) = value
    }
  }

  private def writeVectorValue(
      registers: VectorRegisters,
      first: VectorRegisterIndex,
      lane: Int >= 0,
      value: Long,
      size: Int
  ): Unit = {
    registers(first)(lane.raw) = Bits.low32(value).toLong
    if (size > 4) registers(first.offsetBy(1))(lane.raw) = Bits.high32(value).toLong
  }

}
