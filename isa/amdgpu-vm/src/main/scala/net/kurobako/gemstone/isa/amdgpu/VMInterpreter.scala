package net.kurobako.gemstone.isa.amdgpu

import AmdGpuMachine.*
import EncodingConstants.*
import IntegerSemantics.*
import VMArithmetic.*
import VMIR.*
import VMState.*
import cats.syntax.either.*
import cats.syntax.traverse.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.immutable.ArraySeq
import scala.runtime.LongRef

private object VMInterpreter {
  import VMDispatchMemory.*

  /** Step results other than a jump target. */
  private object Step {
    inline val Next    = -1
    inline val Exit    = -2
    inline val Arrive  = -3
    inline val Blocked = -4
    inline val Yield   = -5
  }
  private val NextStep: Either[Fault, Int]    = Right(Step.Next)
  private val ExitStep: Either[Fault, Int]    = Right(Step.Exit)
  private val Completed: Either[Fault, Unit]  = Right(())
  private val BlockedStep: Either[Fault, Int] = Right(Step.Blocked)
  private val YieldStep: Either[Fault, Int]   = Right(Step.Yield)

  // the conditional branch stack pointer, CSP, in MODE's bits 31 to 29
  private inline val StackPointerShift = 29

  // a resident wave yields after this many backward branches, so a spin on another workgroup lets that one run
  private inline val BackwardBranchesPerSlice = 64

  /** Where a wave stops: arrived at a workgroup barrier, ended, or suspended mid-flow while it waits or yields. */
  private enum WaveBoundary derives CanEqual {
    case Barrier, End, Suspended
  }

  private enum Residency derives CanEqual {
    case Complete, Progressed, Stalled
  }

  private enum Location derives CanEqual {
    case Global, Private, Local, Discarded
  }

  def execute(
      prepared: Kernel,
      launch: KernelLaunch,
      globalBindings: Vec[GlobalMapping],
      layout: Option[KernargLayout] = None
  ): Either[Fault, Unit] = {
    val KernelLaunch(dispatch, context, _) = launch
    for {
      kernarg <- dispatch.argumentBuffer match {
        case Some(buffer) => context.memory.address(buffer.region, buffer.offset).flatMap { address =>
            context.memory.locate(address, prepared.source.kernargSize.raw.max(1).unsafe[Int >= 0])
              .map(_ => (Array.emptyByteArray, address.raw))
          }
        case None if layout.exists(_.parameters.size == dispatch.arguments.size) && dispatch.arguments.nonEmpty =>
          KernelArguments.resolve(dispatch, argumentAddress(_, _, context.memory, globalBindings)).flatMap(arguments =>
            arguments.traverse[[A] =>> Either[Fault, A], Vec[Byte]] {
              case KernelArguments.Resolved.Bytes(bytes) => Right(bytes.raw)
              case KernelArguments.Resolved.Local(_)     =>
                Left(Fault.Unsupported("gfx900 local kernargs are not implemented"))
            }
          ).flatMap(layout.get.pack(_, dispatch.geometry, prepared.source.kernargSize))
            .map(bytes => (bytes, KernargAddress))
        case None =>
          effectiveKernargSize(prepared.source, dispatch.arguments)
            .leftMap(detail => Fault.InvalidRequest(detail))
            .flatMap(size => packArguments(dispatch, size, context.memory, globalBindings))
            .map(bytes => (bytes, KernargAddress))
      }
      _ <- runDispatch(
        prepared,
        kernarg._1,
        kernarg._2,
        Vec.toArray(globalBindings),
        launch
      )
    } yield ()
  }

  /** A descriptor without a kernarg size takes the packed size of the typed arguments. */
  def effectiveKernargSize(
      kernel: CodeObjectKernel,
      arguments: Vec[KernelTask.Argument]
  ): Either[NonEmpty[String], Int >= 0] =
    if (kernel.kernargSize != 0 || arguments.isEmpty) Right(kernel.kernargSize)
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
      val packed = Array.ofDim[Byte](kernargSize)
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
        offset - mapping.binding.offset <= mapping.size.toLong
    ) match {
      case Some(mapping) => Right(mapping.address.offset(offset - mapping.binding.offset))
      case None          => addresses.address(region, offset)
    }

  // Workgroups advance through the shared bounded workgroups without materialising a result collection.
  private def runDispatch(
      program: Kernel,
      kernarg: Array[Byte],
      kernargAddress: Long,
      allocations: Array[GlobalMapping],
      launch: KernelLaunch
  ): Either[Fault, Unit] = {
    val KernelLaunch(dispatch, context, workgroups) = launch
    val memory                                      = context.memory
    val control                                     = context.control
    val kernel                                      = program.source
    // Kernels using enable_sgpr_dispatch_ptr read the HSA packet directly;
    // OpenMP derives its grid-stride loop increment from grid_size_x.
    val packet = Array.ofDim[Byte](64)
    LittleEndian.write(2L, packet, 0, 2)
    LittleEndian.write(dispatch.geometry.workDim.toLong, packet, 2, 2)
    LittleEndian.write(dispatch.geometry.groupSize.x.toLong, packet, 4, 2)
    LittleEndian.write(dispatch.geometry.groupSize.y.toLong, packet, 6, 2)
    LittleEndian.write(dispatch.geometry.groupSize.z.toLong, packet, 8, 2)
    LittleEndian.write(dispatch.geometry.globalExtent.x, packet, 12, 4)
    LittleEndian.write(dispatch.geometry.globalExtent.y, packet, 16, 4)
    LittleEndian.write(dispatch.geometry.globalExtent.z, packet, 20, 4)
    LittleEndian.write(math.max(kernel.privateSegmentSize, dispatch.privateSegmentBytes).toLong, packet, 24, 4)
    LittleEndian.write(
      effectiveGroupSegmentSize(kernel.groupSegmentSize, dispatch.groupSegmentBytes).toLong,
      packet,
      28,
      4
    )
    LittleEndian.write(kernargAddress, packet, 40, 8)
    val dispatchMemory = VMDispatchMemory(kernel, kernarg, packet, allocations, memory, kernargAddress)
    // One shared step result per branch target, so taken and fall-through branches allocate nothing.
    val jumps: Array[Either[Fault, Int]] = Array.tabulate(program.instructions.size + 1)(Right(_))
    if (dispatch.cooperative) runResident(program, jumps, dispatchMemory, dispatch, control)
    else
      Dispatch.forEachGroup(dispatch.geometry, workgroups)((_, group) =>
        runGroup(program, jumps, dispatchMemory, dispatch, group, control)
      )
  }

  /** Every workgroup of a cooperative dispatch is resident at once: one thread interleaves them, a sweep advancing each
    * wave until it reaches a barrier, waits, sleeps, spins or ends. A sweep in which no wave advances is a deadlock.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  private def runResident(
      program: Kernel,
      jumps: Array[Either[Fault, Int]],
      dispatchMemory: VMDispatchMemory,
      dispatch: KernelTask,
      control: TaskControl
  ): Either[Fault, Unit] =
    dispatch.geometry.groupTotal.refine[Long in 1L ~ 2147483647L] match {
      case Left(_) =>
        Left(Fault.InvalidRequest(s"${dispatch.geometry.groupTotal} workgroups cannot be resident at once"))
      case Right(count) =>
        val total = count.toInt
        val gws   = GlobalWaveSyncResources()
        // the HIP runtime sets resource 0 to a barrier of one wave per workgroup before each cooperative launch
        gws.initialise(0, total - 1L)
        val groups = Array.tabulate[ResidentGroup | Null](total)(index =>
          ResidentGroup(
            program,
            jumps,
            dispatchMemory,
            dispatch,
            dispatch.geometry.groupCoordinates(index.toLong.unsafe[Long >= 0]),
            control,
            gws
          )
        )
        var remaining             = total
        var failure: Fault | Null = null
        while (remaining > 0 && failure == null) {
          var progressed = false
          var index      = 0
          while (index < total && failure == null) {
            val group = groups(index)
            if (group != null) group.advance() match {
              case Left(error)               => failure = error
              case Right(Residency.Complete) =>
                groups(index) = null
                remaining -= 1
                progressed = true
              case Right(Residency.Progressed) => progressed = true
              case Right(Residency.Stalled)    => ()
            }
            index += 1
          }
          if (failure == null && remaining > 0 && !progressed)
            failure = Fault.Hang("every remaining wave waits on a GWS resource or barrier that no wave can release")
        }
        if (failure == null) Right(()) else Left(failure)
    }

  /** A resident workgroup's waves; a wave at a barrier waits there until every live wave has arrived or ended. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  private class ResidentGroup(
      program: Kernel,
      jumps: Array[Either[Fault, Int]],
      dispatchMemory: VMDispatchMemory,
      dispatch: KernelTask,
      group: Coordinates,
      control: TaskControl,
      gws: GlobalWaveSyncResources
  ) {
    private val kernel     = program.source
    private val localCount = dispatch.geometry.groupVolume.raw
    private val lds        =
      Array.ofDim[Byte](effectiveGroupSegmentSize(kernel.groupSegmentSize, dispatch.groupSegmentBytes))
    private val waveSize  = kernel.waveSize
    private val waveCount = (localCount + waveSize - 1) / waveSize
    private val barriers  = WorkgroupBarriers(waveCount, program.isa == ISA.CDNA5)
    private val waves     = Array.tabulate(waveCount) { waveIndex =>
      val start = waveIndex * waveSize
      val lanes = math.min(waveSize, localCount - start).unsafe[Int where Between[1, 64]]
      WaveRun(
        program,
        jumps,
        dispatchMemory,
        dispatch.geometry,
        group,
        start,
        lanes,
        lds,
        barriers,
        control,
        gws
      )
    }
    private val finished = Array.fill(waveCount)(false)
    private val arrived  = Array.fill(waveCount)(false)

    def advance(): Either[Fault, Residency] = {
      var failure: Fault | Null = null
      var progressed            = false
      var waveIndex             = 0
      while (waveIndex < waveCount && failure == null) {
        if (!finished(waveIndex) && !arrived(waveIndex)) {
          val wave = waves(waveIndex)
          wave.run() match {
            case Left(error)                 => failure = error
            case Right(WaveBoundary.Barrier) =>
              arrived(waveIndex) = true
              progressed = true
            case Right(WaveBoundary.End) =>
              finished(waveIndex) = true
              barriers.drop(waveIndex)
              progressed = true
            case Right(WaveBoundary.Suspended) => if (!wave.stalled) progressed = true
          }
        }
        waveIndex += 1
      }
      if (failure != null) Left(failure)
      else {
        val live    = finished.count(!_)
        val waiting = arrived.count(identity)
        if (live == 0) Right(Residency.Complete)
        else {
          if (waiting == live) {
            java.util.Arrays.fill(arrived, false)
            progressed = true
          }
          Right(if (progressed) Residency.Progressed else Residency.Stalled)
        }
      }
    }
  }

  // Wave rendezvous is a measured mutable state machine; null marks a wave not yet started or already finished.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  private def runGroup(
      program: Kernel,
      jumps: Array[Either[Fault, Int]],
      dispatchMemory: VMDispatchMemory,
      dispatch: KernelTask,
      group: Coordinates,
      control: TaskControl
  ): Either[Fault, Unit] = {
    val kernel     = program.source
    val localCount = dispatch.geometry.groupVolume.raw
    val lds        = Array.ofDim[Byte](effectiveGroupSegmentSize(kernel.groupSegmentSize, dispatch.groupSegmentBytes))
    val waveSize   = kernel.waveSize
    val waveCount  = (localCount + waveSize - 1) / waveSize
    val waves      = Array.ofDim[WaveRun | Null](waveCount)
    val barriers   = WorkgroupBarriers(waveCount, program.isa == ISA.CDNA5)
    // A wave can end before others reach a barrier in divergent control flow; it must not restart next round.
    val finished              = Array.ofDim[Boolean](waveCount)
    var complete              = false
    var failure: Fault | Null = null
    while (!complete && failure == null) {
      var waiting   = 0
      var blocked   = 0
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
              WaveRun(
                program,
                jumps,
                dispatchMemory,
                dispatch.geometry,
                group,
                start,
                lanes,
                lds,
                barriers,
                control,
                null
              )
            case started => started
          }
          wave.run() match {
            case Left(error)                 => failure = error
            case Right(WaveBoundary.Barrier) =>
              waves(waveIndex) = wave
              waiting += 1
            case Right(WaveBoundary.Suspended) =>
              waves(waveIndex) = wave
              waiting += 1
              if (wave.stalled) blocked += 1
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
        else if (blocked == live)
          failure = Fault.Hang("every remaining wave waits on a GWS resource or barrier that no wave can release")
    }
    if (failure == null) Right(()) else Left(failure)
  }

  // Register, PC and barrier state are updated directly in the measured wave interpreter loop; null avoids allocating
  // optional-state wrappers for every instruction.
  @SuppressWarnings(Array(
    "scalafix:DisableSyntax.var",
    "scalafix:DisableSyntax.null"
  ))
  private class WaveRun(
      program: Kernel,
      jumps: Array[Either[Fault, Int]],
      dispatchMemory: VMDispatchMemory,
      geometry: LaunchGeometry,
      group: Coordinates,
      localStart: Int,
      lanes: Int in 1 ~ 64,
      lds: Array[Byte],
      barriers: WorkgroupBarriers,
      control: TaskControl,
      // the GWS resources of a cooperative dispatch, whose waves also yield on sleeps and spins; null otherwise
      gws: GlobalWaveSyncResources | Null
  ) extends AMDGPUSinks[Long, Either[Fault, Int]] with PendingMachineOps {
    private val kernel             = program.source
    private val operations         = program.instructions
    private val skippedOperations  = program.skipped
    private val instructionIndices = program.instructionIndices
    private val instructionOffsets = program.instructionOffsets
    private val waveSize           = kernel.waveSize
    // Loads share their staged host reads only within this wave; workgroups run concurrently.
    private val reads = AddressSpace.ReadRangeBuffer(waveSize.widen[Int > 0])
    // the wave's ordinal within its workgroup, which the timeline names the unit by
    private val waveIndex: Int >= 0 = (localStart / waveSize).unsafe[Int >= 0]
    private val scalar              = Array.ofDim[Long](128)
    private val vector              = VectorRegisters(waveSize)
    private val accumulator         = VectorRegisters(waveSize)
    private val routed              = Array.ofDim[Long](lanes)
    private val routedHigh          = Array.ofDim[Long](lanes)
    private val preservedHalves     = Array.ofDim[Long](lanes)
    // A vector comparison defines only the wave's lane bits of VCC. On a wave32 target the compiler keeps scratch values
    // in vcc_hi (s107), so the upper half survives a comparison while s106/s107 still round-trip through the register.
    private val carry        = LaneBits(lanes, 0L)
    private val active       = LaneBits(lanes, geometry.liveLanes(group, localStart.unsafe[Int >= 0], lanes))
    private var scalarCarry  = false
    private var mode         = kernel.initialMode
    private val modeWritable = ModeRegister.writable(program.isa)
    private val m0           = program.m0
    private val discarded    = program.discarded
    // The 4096-byte floor proves the per-lane allocation is positive.
    private val scratch          = ScratchMemory(lanes, math.max(4096, kernel.privateSegmentSize).unsafe[Int > 0])
    private var conditionMask    = 0L
    private val codeBase         = kernel.textAddress
    private var instructionIndex = program.entryInstruction
    private var located          = Location.Global
    private val generation       = ResourceGeneration.of(program.isa)
    // the leading bytes of the last located access in range, short of all of them only through a buffer
    private var inRange          = 0
    private val resident         = gws != null
    private var gwsWait          = GlobalWaveSyncResources.Idle
    private var backwardBranches = 0
    // whether the last run only found its wait still unreleased
    private var stalledRun = false
    def stalled: Boolean   = stalledRun

    locally {
      var nextUserSgpr: Int >= 0                       = 0
      def requested(property: Int): Boolean            = (kernel.kernelCodeProperties & property) != 0
      def enabled(value: Int, bit: Int): Boolean       = (value & (1 << bit)) != 0
      def reserve(count: Int > 0): ScalarRegisterIndex = {
        val start = nextUserSgpr
        Predef.require(count <= 128 - start, "user scalar registers exceed the register bank")
        nextUserSgpr += count
        start.unsafe[ScalarRegisterIndex]
      }
      if (requested(KernelCodeProperty.ENABLE_SGPR_PRIVATE_SEGMENT_BUFFER)) nextUserSgpr += 4
      if (requested(KernelCodeProperty.ENABLE_SGPR_DISPATCH_PTR)) setPair(scalar, reserve(2), DispatchAddress)
      if (requested(KernelCodeProperty.ENABLE_SGPR_QUEUE_PTR)) setPair(scalar, reserve(2), QueueAddress)
      if (requested(KernelCodeProperty.ENABLE_SGPR_KERNARG_SEGMENT_PTR))
        setPair(scalar, reserve(2), dispatchMemory.kernargAddress)
      if (requested(KernelCodeProperty.ENABLE_SGPR_DISPATCH_ID)) setPair(scalar, reserve(2), 0L)
      if (requested(KernelCodeProperty.ENABLE_SGPR_FLAT_SCRATCH_INIT)) setPair(scalar, reserve(2), 0L)
      if (requested(KernelCodeProperty.ENABLE_SGPR_PRIVATE_SEGMENT_SIZE))
        scalar(reserve(1)) = kernel.privateSegmentSize.toLong
      // System SGPRs start after USER_SGPR_COUNT, which may include uninitialised user slots.
      nextUserSgpr = nextUserSgpr.max(Bits.unsignedIntField(kernel.computePgmRsrc2.toLong, 1, 5).widen[Int >= 0])
      if (enabled(kernel.computePgmRsrc2, 7)) scalar(reserve(1)) = Bits.unsigned32(group.x).toLong
      if (enabled(kernel.computePgmRsrc2, 8)) scalar(reserve(1)) = Bits.unsigned32(group.y).toLong
      if (enabled(kernel.computePgmRsrc2, 9)) scalar(reserve(1)) = Bits.unsigned32(group.z).toLong
      if (enabled(kernel.computePgmRsrc2, 10)) scalar(reserve(1)) = 0L
      if (program.isa == ISA.RDNA4) {
        // RDNA4 architected workgroup IDs: TTMP9=X, TTMP7={Z[15:0],Y[15:0]}.
        scalar(Source.TTMP_GFX9PLUS_MIN + 9) = Bits.unsigned32(group.x).toLong
        scalar(Source.TTMP_GFX9PLUS_MIN + 7) = (group.y & 0xffffL) | ((group.z & 0xffffL) << 16)
      }
      val enabledWorkItemIds = (kernel.computePgmRsrc2 >>> 11) & 0x3
      val packedIds          = program.isa match {
        case ISA.CDNA2 | ISA.CDNA3 | ISA.CDNA4 | ISA.CDNA5 | ISA.RDNA3 | ISA.RDNA3_5 | ISA.RDNA4 | ISA.GFX11_7 => true
        case _                                                                                                 => false
      }
      var lane = 0
      while (lane < lanes) {
        val local = geometry.localCoordinates((localStart + lane).unsafe[Int >= 0])
        if (packedIds) {
          // The HSA ABI packs each enabled work-item coordinate into ten bits of VGPR0.
          val y = if (enabledWorkItemIds >= 1) local.y.raw else 0
          val z = if (enabledWorkItemIds >= 2) local.z.raw else 0
          vector(0)(lane) = local.x.toLong | (y.toLong << 10) | (z.toLong << 20)
        } else {
          vector(0)(lane) = Bits.unsigned32(local.x).toLong
          if (enabledWorkItemIds >= 1) vector(1)(lane) = Bits.unsigned32(local.y).toLong
          if (enabledWorkItemIds >= 2) vector(2)(lane) = Bits.unsigned32(local.z).toLong
        }
        lane += 1
      }
    }

    // Compare and carry results write the whole mask, clearing EXEC-inactive lanes. A discarded result names the
    // null SGPR.
    def updateLaneMask(destination: ScalarRegisterIndex, result: Long): Unit =
      if (waveSize <= 32) writeScalar32(destination, Bits.low32(result).toLong)
      else writeScalar64(destination, result)

    def saveExecSource(src: InstructionSource, width: Int in (32, 64)): Long =
      if (width == 32) Bits.low32(sourceScalar64(src)).toLong else sourceScalar64(src)

    def saveExecWrite(destination: ScalarRegisterIndex, value: Long, width: Int in (32, 64)): Unit =
      if (width == 32) writeScalar32(destination, value) else writeScalar64(destination, value)

    def holds(condition: ScalarBranchCondition): Boolean = condition match {
      case ScalarBranchCondition.Always      => true
      case ScalarBranchCondition.SccZero     => !scalarCarry
      case ScalarBranchCondition.SccNonZero  => scalarCarry
      case ScalarBranchCondition.VccZero     => carry.isEmpty
      case ScalarBranchCondition.VccNonZero  => carry.nonEmpty
      case ScalarBranchCondition.ExecZero    => active.isEmpty
      case ScalarBranchCondition.ExecNonZero => active.nonEmpty
    }

    def source(value: InstructionSource, lane: Int >= 0): Long = value match {
      case InstructionSource.Scalar(register) if register.raw == Fixed.VccLo  => Bits.low32(carry.register).toLong
      case InstructionSource.Scalar(register) if register.raw == Fixed.VccHi  => Bits.high32(carry.register).toLong
      case InstructionSource.Scalar(register) if register.raw == Fixed.ExecLo => Bits.low32(active.register).toLong
      case InstructionSource.Scalar(register) if register.raw == Fixed.ExecHi => Bits.high32(active.register).toLong
      case InstructionSource.Scalar(index)                                    => scalar(index)
      case InstructionSource.VectorRegister(index)                            => vector(index)(lane)
      case InstructionSource.DPP                                              => routed(lane)
      case InstructionSource.Condition(condition)                             => if (holds(condition)) 1L else 0L
      case InstructionSource.HighHalf(half)                        => Bits.low16(source(half, lane) >>> 16).toLong
      case InstructionSource.Selected(part, selection, signExtend) =>
        Bits.low32(Execute.selectedPart(source(part, lane), selection, signExtend)).toLong
      case InstructionSource.Floating(constant) => IEEE754.floatToBinary32(constant.toFloat).toLong
      case InstructionSource.Constant(raw)      => raw
      case InstructionSource.Literal(raw)       => raw
    }

    def sourceScalar64(value: InstructionSource): Long = value match {
      case InstructionSource.Scalar(register) if register.raw == Fixed.VccLo  => carry.register
      case InstructionSource.Scalar(register) if register.raw == Fixed.VccHi  => Bits.high32(carry.register).toLong
      case InstructionSource.Scalar(register) if register.raw == Fixed.ExecLo => active.register
      case InstructionSource.Scalar(register) if register.raw == Fixed.ExecHi => Bits.high32(active.register).toLong
      case InstructionSource.Scalar(index)                                    => pair(scalar, index)
      case InstructionSource.Condition(condition)                             => if (holds(condition)) 1L else 0L
      case InstructionSource.HighHalf(half)                        => Bits.low16(sourceScalar64(half) >>> 16).toLong
      case InstructionSource.Selected(part, selection, signExtend) =>
        Bits.low32(Execute.selectedPart(sourceScalar64(part), selection, signExtend)).toLong
      case InstructionSource.Floating(constant)                        => IEEE754.doubleToBinary64(constant)
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
      case InstructionSource.Floating(constant) => IEEE754.floatToBinary32(constant.toFloat).toLong
      case _                                    => source64(value, lane)
    }

    def sourceFloating64(value: InstructionSource, lane: Int >= 0): Long = value match {
      case InstructionSource.Literal(raw) => IEEE754.doubleToBinary64(floatingLiteralToDouble(raw))
      case _                              => source64(value, lane)
    }

    def writeScalar64(dest: ScalarRegisterIndex, value: Long): Unit = dest.raw match {
      case Fixed.VccLo  => carry.register = value
      case Fixed.ExecLo => active.register = value
      case index        => if (index != discarded) setPair(scalar, dest, value)
    }

    def writeScalar32(dest: ScalarRegisterIndex, value: Long): Unit = dest.raw match {
      case Fixed.VccLo => carry.register = (carry.register & 0xffffffff00000000L) | Bits.low32(value).toLong
      case Fixed.VccHi =>
        carry.register = (carry.register & 0xffffffffL) | (Bits.low32(value).toLong << 32)
      case Fixed.ExecLo => active.register = (active.register & 0xffffffff00000000L) | Bits.low32(value).toLong
      case Fixed.ExecHi => active.register = (active.register & 0xffffffffL) | (Bits.low32(value).toLong << 32)
      case index        => if (index != discarded) scalar(index) = Bits.low32(value).toLong
    }

    /** A source resolved once per instruction: a register array read by lane, a uniform value, or a source that still
      * depends on the lane. Bound as a register pair, it reads the register and its successor, and `read` says how any
      * other source reads 64 bits.
      */
    private class Operand {
      private var registers: Array[Long] | Null     = null
      private var high: Array[Long] | Null          = null
      private var derived: InstructionSource | Null = null
      private var extracted: Array[Long] | Null     = null
      private var uniform                           = 0L

      def bind(value: InstructionSource): Unit = value match {
        case InstructionSource.VectorRegister(index) =>
          registers = vector(index)
          derived = null
        case InstructionSource.DPP =>
          registers = routed
          derived = null
        case InstructionSource.Selected(InstructionSource.VectorRegister(index), part, extend) =>
          registers = extractPart(vector(index), part, extend)
          derived = null
        case InstructionSource.HighHalf(_) | InstructionSource.Selected(source = _) =>
          registers = null
          derived = value
        case _ =>
          registers = null
          derived = null
          uniform = source(value, 0)
      }

      private def extractPart(values: Array[Long], part: SubDwordSelection, extend: Boolean): Array[Long] = {
        val parts = extracted match {
          case null =>
            val created = Array.ofDim[Long](lanes)
            extracted = created
            created
          case existing => existing
        }
        var lane = 0
        while (lane < parts.length) {
          parts(lane) = Bits.low32(Execute.selectedPart(values(lane), part, extend)).toLong
          lane += 1
        }
        parts
      }

      def apply(lane: Lane): Long = {
        val values = registers
        if (values != null) values(lane)
        else {
          val dependent = derived
          if (dependent == null) uniform else source(dependent, lane)
        }
      }

      def bindPair(value: InstructionSource, read: PairRead): Unit = value match {
        case InstructionSource.VectorRegister(index) =>
          registers = vector(index)
          high = vector(index.offsetBy(1))
        case InstructionSource.DPP =>
          registers = routed
          high = routedHigh
        case _ =>
          registers = null
          uniform = read match {
            case PairRead.Integer  => sourceScalar64(value)
            case PairRead.Floating => sourceFloating64(value, 0)
            case PairRead.Packed   => sourcePackedF32(value, 0)
          }
      }

      def pair(lane: Lane): Long = {
        val values = registers
        if (values != null) Bits.join32(values(lane), high.nn(lane)) else uniform
      }
    }

    private val first  = Operand()
    private val second = Operand()
    private val third  = Operand()

    // Two lane-sized halves for instructions that read every source before writing; few kernels need them.
    private var scratchLanes: Array[Long] | Null = null

    private def laneScratch(): Array[Long] = {
      val existing = scratchLanes
      if (existing != null) existing
      else {
        val created = Array.ofDim[Long](2 * lanes)
        scratchLanes = created
        created
      }
    }

    // Lane loops are inlined so per-lane bodies need neither a closure nor a megamorphic call.
    inline def foreachLane(inline operation: Lane => Unit): Unit = {
      var lane = 0
      while (lane < lanes) {
        operation(lane.unsafe[Lane])
        lane += 1
      }
    }

    /** Visits the lanes EXEC enables when the loop starts, in lane order. */
    inline def foreachActive(inline operation: Lane => Unit): Unit = {
      var remaining = active.bits
      while (remaining != 0L) {
        val lane = java.lang.Long.numberOfTrailingZeros(remaining)
        remaining &= remaining - 1L
        operation(lane.unsafe[Lane])
      }
    }

    def programCounter(index: Int): Long = codeBase + instructionOffsets(index).toLong

    def programIndex(address: Long): Option[Int] = {
      val relative = address - codeBase
      if (relative < 0L || relative > Int.MaxValue.toLong) None
      else instructionIndices.get(relative.toInt)
    }

    private def baseAddress(
        register: VectorRegisterIndex,
        base: Option[ScalarRegisterIndex],
        offset: Int,
        lane: Int >= 0
    ): Long = base match {
      case Some(scalarBase) =>
        Execute.offsetAddress(pair(scalar, scalarBase), vector(register)(lane), offset.toLong)
      case None => Execute.displaced(pair(vector, register, lane), offset.toLong)
    }

    /** The guest address, private offset or LDS address `address` names for `lane`; `located` records which, and
      * `inRange` how many of the `bytes`, checked in `component`-byte parts, a buffer holds.
      */
    private def locate(address: MemoryAddress, lane: Int >= 0, bytes: Int, component: Int = 4): Long = {
      inRange = bytes
      address match {
        case MemoryAddress.Global(register, base, offset) =>
          located = Location.Global
          baseAddress(register, base, offset, lane)
        case MemoryAddress.Flat(register, offset, base) =>
          val value = baseAddress(register, base, offset, lane)
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
          Execute.dwordAddress(laneOffset(register, lane), scalarOffset(base), offset.toLong)
        case buffer @ MemoryAddress.Buffer(resource = _) => locateBuffer(buffer, lane, bytes, component)
        case MemoryAddress.Local(register, offset)       =>
          located = Location.Local
          Execute.dwordAddress(vector(register)(lane), 0L, offset.toLong)
        case MemoryAddress.Texel(image) =>
          val at = texelAddress(image, lane)
          located = if (at < 0L) Location.Discarded else Location.Global
          at
        case MemoryAddress.LaneLocal(offset) =>
          located = Location.Local
          Execute.laneLocalAddress(Bits.low32(scalar(m0)).toLong, offset.toLong, lane.toLong)
        case MemoryAddress.LaneGlobal(base, offset) =>
          located = Location.Global
          Execute.laneGlobalAddress(base.fold(0L)(pair(scalar, _)), offset.toLong, lane.toLong)
      }
    }

    // The V# last decoded; a buffer access decodes it again only when its words change.
    private var descriptor0           = -1L
    private var descriptor1           = -1L
    private var descriptor2           = -1L
    private var descriptor3           = -1L
    private var descriptorBase        = 0L
    private var descriptorStride      = 0L
    private var descriptorElement     = 0
    private var descriptorIndexStride = 0
    private var descriptorThreaded    = false

    private def decodeDescriptor(word0: Long, word1: Long, word2: Long, word3: Long): Unit = {
      descriptor0 = word0
      descriptor1 = word1
      descriptor2 = word2
      descriptor3 = word3
      descriptorBase = toRaw[UInt[64]](
        BufferResource.base(fromRaw[UInt[32]](word0), fromRaw[UInt[32]](word1))
      )
      descriptorStride = BufferResource.stride(generation, word1, word3)
      descriptorElement = BufferResource.element(generation, word1, word3)
      descriptorIndexStride = BufferResource.indexStride(word3)
      descriptorThreaded = BufferResource.addThread(word3)
    }

    // Kept out of locate so the global and flat paths stay small enough to inline.
    private def locateBuffer(address: MemoryAddress.Buffer, lane: Int >= 0, bytes: Int, component: Int): Long = {
      val first = address.resource
      val word0 = Bits.low32(scalar(first)).toLong
      val word1 = Bits.low32(scalar(first + 1)).toLong
      val word2 = Bits.low32(scalar(first + 2)).toLong
      val word3 = Bits.low32(scalar(first + 3)).toLong
      if (word0 != descriptor0 || word1 != descriptor1 || word2 != descriptor2 || word3 != descriptor3)
        decodeDescriptor(word0, word1, word2, word3)
      val start = Execute.displaced(laneOffset(address.vector, lane), address.offset.toLong)
      val added = Bits.low32(scalarOffset(address.scalar) + address.scalarConstant).toLong
      if (address.wide) {
        located = Location.Global
        val pair = address.vector.fold(0L)(index =>
          laneOffset(Some(index), lane) | (laneOffset(Some(index.offsetBy(1.unsafe[Int >= 0])), lane) << 32)
        )
        toRaw[UInt[64]](BufferResource.wideAddress(
          fromRaw[UInt[32]](word0),
          fromRaw[UInt[32]](word1),
          fromRaw[UInt[64]](pair),
          fromRaw[UInt[32]](added)
        )) + address.offset.toLong
      } else if (descriptorBase == 0L) {
        // A null descriptor addresses wave-interleaved scratch; its scalar offset spans every lane.
        located = Location.Private
        Execute.interleavedAddress(start, added, waveSize.toLong)
      } else {
        val within  = Bits.low32(start).toLong
        val indexed = address.index.isDefined
        val element = toRaw[UInt[64]](BufferResource.index(
          generation,
          fromRaw[UInt[32]](word3),
          indexed,
          fromRaw[UInt[32]](laneOffset(address.index, lane)),
          fromRaw[UInt[32]](lane.toLong)
        ))
        inRange = BufferResource.inRange(
          generation,
          word2 = word2,
          word3 = word3,
          stride = descriptorStride,
          element = descriptorElement,
          indexed = indexed,
          index = element,
          offset = within,
          scalarOffset = added,
          bytes = bytes,
          component = Math.min(component, bytes)
        )
        located = if (inRange == 0) Location.Discarded else Location.Global
        descriptorBase + added +
          BufferResource.placed(
            descriptorStride,
            descriptorElement,
            indexed || descriptorThreaded,
            descriptorIndexStride,
            element,
            within
          )
      }
    }

    /** The texel an image atomic updates, or -1 outside the image or for a descriptor execution cannot read. */
    private def texelAddress(image: ImageAddress, lane: Int >= 0): Long =
      decodedImage(image.resource.raw) match {
        case Left(_)         => -1L
        case Right(resource) => imageTexel(resource, image, lane)
      }

    // The T# last decoded; an image access decodes it again only when its words change.
    private val imageWords                                  = Array.ofDim[Long](8)
    private var imageDecoded: Either[String, ImageResource] = Left("")

    java.util.Arrays.fill(imageWords, -1L)

    private def decodedImage(first: Int): Either[String, ImageResource] = {
      var changed = false
      var index   = 0
      while (index < 8) {
        val word = Bits.low32(scalar(first + index)).toLong
        if (word != imageWords(index)) {
          imageWords(index) = word
          changed = true
        }
        index += 1
      }
      if (changed) imageDecoded = ImageResource.decode(program.isa, imageWords)
      imageDecoded
    }

    private def imageTexel(resource: ImageResource, image: ImageAddress, lane: Int >= 0): Long = ImageResource.locate(
      resource,
      image.dimension,
      image.mip,
      c0 = imageCoordinate(image, lane, 0),
      c1 = imageCoordinate(image, lane, 1),
      c2 = imageCoordinate(image, lane, 2),
      c3 = imageCoordinate(image, lane, 3),
      c4 = imageCoordinate(image, lane, 4)
    )

    /** Coordinate `index` of an image address, zero past its words. */
    private def imageCoordinate(image: ImageAddress, lane: Int >= 0, index: Int): Long =
      if (index >= image.words.size * (if (image.a16) 2 else 1)) 0L
      else if (image.a16) Bits.low16(vector(image.words(index / 2))(lane) >>> (16 * (index % 2))).toLong
      else Bits.low32(vector(image.words(index))(lane)).toLong

    /** Scalar register `index` as a dword. */
    private def dword(index: Int): UInt[32] = fromRaw[UInt[32]](Bits.low32(scalar(index)).toLong)

    private def laneOffset(register: Option[VectorRegisterIndex], lane: Int >= 0): Long = register match {
      case Some(index) => Bits.low32(vector(index)(lane)).toLong
      case None        => 0L
    }

    private def scalarOffset(register: Option[ScalarRegisterIndex]): Long = register match {
      case Some(index) => Bits.low32(scalar(index)).toLong
      case None        => 0L
    }

    private def privateFault(offset: Long, bytes: Int): Fault =
      Fault.Memory(s"private access at offset $offset ($bytes bytes) exceeds the private segment")

    // Per-lane accesses report a fault or null so local and private lanes allocate nothing.
    private val loaded = LongRef(0L)

    /** Reads at most eight bytes from a located access into `loaded`. */
    private def readValue(at: Long, lane: Lane, bytes: Int in 1 ~ 8, coherent: Boolean): Fault | Null =
      located match {
        case Location.Global =>
          if (coherent && (bytes == 4 || bytes == 8) && dispatchMemory.isMutable(at, bytes))
            AddressSpace.store(loaded, dispatchMemory.atomicModifyValue(at, if (bytes == 4) 4 else 8)(identity))
          else dispatchMemory.readValueInto(at, bytes, loaded)
        case Location.Private =>
          val storage = scratch(lane)
          if (!ByteRange.contains(storage.length, at, bytes.toLong)) privateFault(at, bytes)
          else {
            loaded.elem = LittleEndian.decode(storage, at.toInt, bytes)
            null
          }
        case Location.Local =>
          val offset = checkedLdsOffset(lds, at, bytes)
          loaded.elem =
            if (offset == InvalidLdsOffset) LittleEndian.decode(ldsBytes(lds, at, bytes), 0, bytes)
            else LittleEndian.decode(lds, offset, bytes)
          null
        case Location.Discarded =>
          loaded.elem = 0L
          null
      }

    private def writeValue(at: Long, lane: Lane, value: Long, bytes: Int in 1 ~ 8): Fault | Null =
      located match {
        case Location.Global  => dispatchMemory.writeValue(at, value, bytes).left.getOrElse(null)
        case Location.Private =>
          val storage = scratch(lane)
          if (!ByteRange.contains(storage.length, at, bytes.toLong)) privateFault(at, bytes)
          else {
            LittleEndian.write(value, storage, at.toInt, bytes)
            null
          }
        case Location.Local =>
          val offset = checkedLdsOffset(lds, at, bytes)
          if (offset == InvalidLdsOffset) writeLDS(lds, at, LittleEndian.toArray(value, bytes.widen))
          else LittleEndian.write(value, lds, offset, bytes)
          null
        case Location.Discarded => null
      }

    private def loadLane(load: Instruction.Load, registers: VectorRegisters, lane: Lane): Fault | Null =
      loadFrom(load, registers, lane, locate(load.address, lane, load.bytes))

    private def loadPlacedLane(load: Instruction.Load, registers: VectorRegisters, lane: Lane): Fault | Null = {
      inRange = placedRanges(lane)
      located = Location.fromOrdinal(placedLocations(lane))
      loadFrom(load, registers, lane, placedAddresses(lane))
    }

    private def loadFrom(load: Instruction.Load, registers: VectorRegisters, lane: Lane, at: Long): Fault | Null = {
      val bytes = load.bytes
      if (located == Location.Global && inRange < bytes)
        dispatchMemory.readBytes(at, inRange.unsafe[Int >= 0]) match {
          case Left(fault) => fault
          case Right(data) =>
            val padded = Array.ofDim[Byte](bytes)
            data.indices.foreach(index => padded(index) = data(index))
            writeVectorBytes(registers, load.dest, lane, padded)
            null
        }
      else if (bytes > 8) located match {
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
          if (offset == InvalidLdsOffset) writeVectorBytes(registers, load.dest, lane, ldsBytes(lds, at, bytes))
          else writeVectorBytes(registers, load.dest, lane, lds, offset, bytes)
          null
        case Location.Discarded =>
          writeVectorBytes(registers, load.dest, lane, Array.ofDim[Byte](bytes))
          null
      }
      else {
        val fault = readValue(at, lane, bytes.unsafe[Int in 1 ~ 8], load.coherent)
        if (fault == null) {
          val value = Execute.loaded(loaded.elem, (bytes * 8).unsafe[Int in 1 ~ 64], load.signed)
          load.placement match {
            case LoadPlacement.Full => writeVectorValue(registers, load.dest, lane, value, bytes)
            case half @ (LoadPlacement.Low | LoadPlacement.High) =>
              registers(load.dest)(lane) =
                Execute.mergeHalf(
                  registers(load.dest)(lane),
                  value,
                  high = half == LoadPlacement.High
                )
            case LoadPlacement.LowZeroingHigh =>
              registers(load.dest)(lane) = Execute.zeroedHalf(value, high = false)
            case LoadPlacement.HighZeroingLow =>
              registers(load.dest)(lane) = Execute.zeroedHalf(value, high = true)
          }
        }
        fault
      }
    }

    /** Each lane of the groups the active lanes belong to reads its bytes; each active lane then takes the elements its
      * layout routes to it.
      */
    private def transposedLoad(load: Instruction.LoadTranspose): Either[Fault, Int] =
      if (load.layout.wave32 && waveSize != 32)
        Left(Fault.Unsupported(s"AMDGPU ${load.layout} transposing load in a wave64 is not implemented"))
      else transposedLanes(load)

    private def transposedLanes(load: Instruction.LoadTranspose): Either[Fault, Int] = {
      val layout  = load.layout
      val bytes   = layout.bytes
      val count   = lanes
      val scratch = laneScratch()
      var read    = 0L
      var fault   = Option.empty[Fault]
      foreachActive { lane =>
        val first = lane & ~layout.groupMask
        var index = first
        while (index <= (first | layout.groupMask) && index < count && fault.isEmpty) {
          if ((read & (1L << index)) == 0L) {
            val at = locate(load.address, index.unsafe[Int >= 0], bytes)
            located match {
              case Location.Local =>
                val offset = checkedLdsOffset(lds, at, bytes)
                if (offset == InvalidLdsOffset) keepTransposed(scratch, index, ldsBytes(lds, at, bytes), 0, bytes)
                else keepTransposed(scratch, index, lds, offset, bytes)
                read |= 1L << index
              case Location.Global =>
                dispatchMemory.readBytes(at, bytes.unsafe[Int >= 0]) match {
                  case Left(failure) => fault = Some(failure)
                  case Right(data)   =>
                    keepTransposed(scratch, index, Vec.toArray(data), 0, bytes)
                    read |= 1L << index
                }
              case other => fault = Some(Fault.Unsupported(s"AMDGPU transposing load from $other"))
            }
          }
          index += 1
        }
      }
      fault.fold {
        val dwords = (layout.bits * layout.elements + 31) / 32
        val bits   = layout.bits
        val mask   = (1L << bits) - 1L
        foreachActive { lane =>
          var packedLow  = 0L
          var packedHigh = 0L
          var element    = 0
          while (element < layout.elements) {
            val origin = layout.origin(lane, element)
            if ((read & (1L << origin.lane)) != 0L) {
              val bit   = origin.element * bits
              val start = scratch(origin.lane)
              val end   = scratch(count + origin.lane)
              val value =
                (if (bit >= 64) end >>> (bit - 64)
                 else if (bit + bits > 64) (start >>> bit) | (end << (64 - bit))
                 else start >>> bit) & mask
              val at = element * bits
              packedLow |= placedLow(value, at)
              packedHigh |= placedHigh(value, at, bits)
            }
            element += 1
          }
          var dword = 0
          while (dword < dwords) {
            vector(load.dest.offsetBy(dword.unsafe[Int >= 0]))(lane) =
              (if (dword < 2) packedLow >>> (32 * dword) else packedHigh >>> (32 * (dword - 2))) & 0xffffffffL
            dword += 1
          }
        }
        NextStep
      }(Left(_))
    }

    /** The little-endian `bytes` of `data` from `offset`, at most 16, as the low and high halves for lane `lane`. */
    private def keepTransposed(scratch: Array[Long], lane: Int, data: Array[Byte], offset: Int, bytes: Int): Unit = {
      scratch(lane) = LittleEndian.decode(data, offset, Math.min(bytes, 8).unsafe[Int in 0 ~ 8])
      scratch(lanes + lane) =
        if (bytes > 8) LittleEndian.decode(data, offset + 8, (bytes - 8).unsafe[Int in 0 ~ 8]) else 0L
    }

    private def storeLane(store: Instruction.Store, registers: VectorRegisters, lane: Lane): Fault | Null = {
      val at = locate(store.address, lane, store.bytes)
      if (located == Location.Global && inRange < store.bytes)
        dispatchMemory.writeBytes(at, vectorBytes(registers, store.src, lane, inRange.unsafe[Int >= 0]))
          .left.getOrElse(null)
      else if (store.bytes > 8) {
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
          case Location.Local =>
            writeLDS(lds, at, data)
            null
          case Location.Discarded => null
        }
      } else {
        val value =
          if (store.high) Execute.high16(registers(store.src)(lane))
          else if (store.bytes <= 4) registers(store.src)(lane)
          else pair(registers, store.src, lane)
        writeValue(at, lane, value, store.bytes.unsafe[Int in 1 ~ 8])
      }
    }

    /** Waves within a workgroup run serially, so LDS and private read-modify-writes cannot interleave. The runtime's
      * unaligned SH_MEM_CONFIG mode tolerates misaligned loads and stores on every generation, but not atomics, which
      * fault in every address space as a memory violation.
      */
    def atomicLane(
        address: MemoryAddress,
        dest: VectorRegisterIndex,
        bytes: Int in (4, 8),
        returnValue: Boolean,
        lane: Lane,
        update: Long => Long,
        file: VectorRegisters
    ): Fault | Null = {
      val at    = locate(address, lane, bytes, bytes)
      val fault =
        if ((at & (bytes - 1).toLong) != 0L && located != Location.Discarded && !texel(address))
          misalignedAtomic(lane, at, bytes)
        else if (located == Location.Global) dispatchMemory.atomicModifyValue(at, bytes)(update) match {
          case Left(fault)     => fault
          case Right(previous) =>
            loaded.elem = previous
            null
        }
        else {
          val read = readValue(at, lane, bytes.widen, coherent = false)
          if (read == null) writeValue(at, lane, update(loaded.elem), bytes.widen) else read
        }
      if (fault == null && returnValue) writeVectorValue(file, dest, lane, loaded.elem, bytes)
      fault
    }

    // an image atomic names a texel, which the descriptor's format and 256-byte aligned base place
    private def texel(address: MemoryAddress): Boolean = address match {
      case MemoryAddress.Texel(_) => true
      case _                      => false
    }

    private def misalignedAtomic(lane: Lane, at: Long, bytes: Int): Fault = {
      val space = located match {
        case Location.Local   => "LDS offset"
        case Location.Private => "private offset"
        case _                => "address"
      }
      Fault.Misaligned(f"lane $lane: $bytes-byte atomic at $space 0x$at%x")
    }

    inline def foreachActiveUntilFault(inline operation: Lane => Fault | Null): Either[Fault, Int] = {
      var remaining             = active.bits
      var failure: Fault | Null = null
      while (remaining != 0L && failure == null) {
        val lane = java.lang.Long.numberOfTrailingZeros(remaining)
        remaining &= remaining - 1L
        failure = operation(lane.unsafe[Lane])
      }
      if (failure == null) NextStep else Left(failure)
    }

    // The places prepareReads located, which the lanes of a non-coherent load then read from.
    private val placedAddresses = Array.ofDim[Long](lanes)
    private val placedRanges    = Array.ofDim[Int](lanes)
    private val placedLocations = Array.ofDim[Int](lanes)

    // Only collect addresses that the load will read from host memory; apertures, code-object images and
    // out-of-range buffer lanes never reach the host pump.
    private def prepareReads(load: Instruction.Load): Either[Fault, Unit] = {
      reads.clear()
      foreachActive { lane =>
        val at = locate(load.address, lane, load.bytes)
        placedAddresses(lane) = at
        placedRanges(lane) = inRange
        placedLocations(lane) = located.ordinal
        if (located == Location.Global && dispatchMemory.isExternal(at, inRange.unsafe[Int >= 0]))
          reads.add(GuestAddress(at), inRange.unsafe[Int >= 0])
      }
      if (reads.isEmpty) Right(()) else dispatchMemory.memory.prepareReads(reads)
    }

    def stepMemory(instruction: Instruction): Either[Fault, Int] = instruction match {
      case load: Instruction.Load =>
        val registers = if (load.bank == RegisterBank.Accumulator) accumulator else vector
        if (load.coherent) foreachActiveUntilFault(loadLane(load, registers, _))
        else
          prepareReads(load) match {
            case Left(fault) => Left(fault)
            case _           => foreachActiveUntilFault(loadPlacedLane(load, registers, _))
          }
      case store: Instruction.Store =>
        val registers = if (store.bank == RegisterBank.Accumulator) accumulator else vector
        foreachActiveUntilFault(storeLane(store, registers, _))
      case Instruction.LoadPair(dest, address, bytes, firstOffset, secondOffset, bank) =>
        val upper = dest.offsetBy(bytes.widen[Int >= 0] / 4)
        val file  = registerFile(bank)
        foreachActiveUntilFault { lane =>
          val firstAddress  = Execute.dwordAddress(vector(address)(lane), 0L, firstOffset.toLong)
          val secondAddress = Execute.dwordAddress(vector(address)(lane), 0L, secondOffset.toLong)
          val low           = checkedLdsOffset(lds, firstAddress, bytes)
          val high          = checkedLdsOffset(lds, secondAddress, bytes)
          writeLdsBytes(file, dest, lane, lds, low, firstAddress, bytes)
          writeLdsBytes(file, upper, lane, lds, high, secondAddress, bytes)
          null
        }
      case Instruction.StorePair(address, srcA, srcB, bytes, firstOffset, secondOffset, bank) =>
        val file = registerFile(bank)
        foreachActiveUntilFault { lane =>
          val firstAddress  = Execute.dwordAddress(vector(address)(lane), 0L, firstOffset.toLong)
          val secondAddress = Execute.dwordAddress(vector(address)(lane), 0L, secondOffset.toLong)
          writeLDS(lds, firstAddress, vectorBytes(file, srcA, lane, bytes.widen))
          writeLDS(lds, secondAddress, vectorBytes(file, srcB, lane, bytes.widen))
          null
        }
      case Instruction.CacheWritebackInvalidate => ScalarControl.writebackInvalidate(this)
      case other => Left(Fault.IllegalInstruction(s"unhandled AMDGPU instruction $other"))
    }

    /** Buffer formats, buffer loads into LDS, scalar buffer loads, images and TFE status. */
    def stepResource(instruction: Instruction): Either[Fault, Int] = instruction match {
      case load: Instruction.FormatLoad =>
        val word3  = scalar(bufferResource(load.address) + 3)
        val format = load.format.getOrElse(BufferResource.format(generation, word3))
        foreachActiveUntilFault(formatLoadLane(load, word3, format, _))
      case store: Instruction.FormatStore =>
        val word3  = scalar(bufferResource(store.address) + 3)
        val format = store.format.getOrElse(BufferResource.format(generation, word3))
        foreachActiveUntilFault(formatStoreLane(store, word3, format, _))
      case local: Instruction.LoadLocal  => foreachActiveUntilFault(loadLocalLane(local, _))
      case local: Instruction.StoreLocal => foreachActiveUntilFault(storeLocalLane(local, _))
      case load: Instruction.SBufferLoad => scalarBufferLoad(load)
      case image: Instruction.Image      =>
        decodedImage(image.address.resource.raw) match {
          case Left(message)   => Left(Fault.Unsupported(s"AMDGPU image access: $message"))
          case Right(resource) => foreachActiveUntilFault(imageLane(image, resource, _))
        }
      case Instruction.WithStatus(access, status) =>
        val result = access match {
          case load: Instruction.Load => stepMemory(load)
          case other                  => stepResource(other)
        }
        result.flatMap { _ =>
          foreachActive(lane => vector(status)(lane) = 0L)
          NextStep
        }
      case other => Left(Fault.IllegalInstruction(s"unhandled AMDGPU instruction $other"))
    }

    // the texel the last readTexel read, little-endian, zero outside the resource
    private var texelLow  = 0L
    private var texelHigh = 0L

    /** Reads the `bytes` texel at a located `at`; outside a buffer or image it reads as absent. */
    private def readTexel(at: Long, lane: Lane, bytes: Int): Fault | Null = {
      texelLow = 0L
      texelHigh = 0L
      if (located == Location.Discarded) null
      else {
        val low = readValue(at, lane, Math.min(bytes, 8).unsafe[Int in 1 ~ 8], coherent = false)
        if (low != null) low
        else {
          texelLow = loaded.elem
          if (bytes <= 8) null
          else {
            val high = readValue(at + 8, lane, (bytes - 8).unsafe[Int in 1 ~ 8], coherent = false)
            if (high == null) texelHigh = loaded.elem
            high
          }
        }
      }
    }

    private def writeTexel(at: Long, lane: Lane, bytes: Int, low: Long, high: Long): Fault | Null =
      if (located == Location.Discarded) null
      else {
        val first = writeValue(at, lane, low, Math.min(bytes, 8).unsafe[Int in 1 ~ 8])
        if (first != null || bytes <= 8) first
        else writeValue(at + 8, lane, high, (bytes - 8).unsafe[Int in 1 ~ 8])
      }

    /** Component `select` of the texel last read, after its destination select and conversion. */
    private def texelComponent(format: TexelFormat, select: Int, half: Boolean): Long =
      TexelFormat.component(format, select, texelLow, texelHigh, half)

    private def writeComponent(
        file: VectorRegisters,
        first: VectorRegisterIndex,
        lane: Lane,
        index: Int,
        value: Long,
        packing: Packing
    ): Unit = packing match {
      case Packing.Dword | Packing.Unpacked => file(first.offsetBy(index.unsafe[Int >= 0]))(lane) = value
      case Packing.Packed                   =>
        val register = file(first.offsetBy((index / 2).unsafe[Int >= 0]))
        register(lane) = Execute.mergeHalf(register(lane), value, index % 2 == 1)
      case Packing.High =>
        file(first)(lane) = Execute.mergeHalf(file(first)(lane), value, high = true)
    }

    private def readComponent(
        file: VectorRegisters,
        first: VectorRegisterIndex,
        lane: Lane,
        index: Int,
        packing: Packing
    )
        : Long =
      packing match {
        case Packing.Dword    => Bits.low32(file(first.offsetBy(index.unsafe[Int >= 0]))(lane)).toLong
        case Packing.Unpacked => Bits.low16(file(first.offsetBy(index.unsafe[Int >= 0]))(lane)).toLong
        case Packing.Packed   =>
          Bits.low16(file(first.offsetBy((index / 2).unsafe[Int >= 0]))(lane) >>> (16 * (index % 2))).toLong
        case Packing.High => Bits.low16(file(first)(lane) >>> 16).toLong
      }

    /** The texel bits a store writes for the 32-bit `components`, each placed by the inverse of the selects. */
    private def packTexel(format: TexelFormat, selects: Long, components: Array[Long], converted: Boolean): Unit = {
      texelLow = TexelFormat.packed(format, selects, components, converted, 0)
      texelHigh = if (format.layout.bytes > 8) TexelFormat.packed(format, selects, components, converted, 1) else 0L
    }

    private def bufferResource(address: MemoryAddress): ScalarRegisterIndex = address match {
      case MemoryAddress.Buffer(resource = resource) => resource
      case _                                         => 0
    }

    private val texelComponents = Array.ofDim[Long](4)

    private def formatLoadLane(load: Instruction.FormatLoad, word3: Long, format: TexelFormat, lane: Lane)
        : Fault | Null = {
      val bytes = Math.max(format.layout.bytes, 1)
      val at    = locate(load.address, lane, bytes, bytes)
      val fault = readTexel(at, lane, bytes)
      if (fault == null) {
        val half  = load.packing != Packing.Dword
        var index = 0
        while (index < load.components) {
          writeComponent(
            registerFile(load.bank),
            load.dest,
            lane,
            index,
            texelComponent(format, TexelFormat.select(word3, index), half),
            load.packing
          )
          index += 1
        }
      }
      fault
    }

    private def formatStoreLane(store: Instruction.FormatStore, word3: Long, format: TexelFormat, lane: Lane)
        : Fault | Null = {
      val bytes = Math.max(format.layout.bytes, 1)
      val at    = locate(store.address, lane, bytes, bytes)
      if (!format.valid) null
      else {
        val components = texelComponents
        java.util.Arrays.fill(components, 0L)
        var index = 0
        while (index < store.components) {
          val value = readComponent(registerFile(store.bank), store.src, lane, index, store.packing)
          components(index) = if (store.packing == Packing.Dword) value else TexelFormat.widen(format.number, value)
          index += 1
        }
        packTexel(format, word3, components, converted = true)
        writeTexel(at, lane, bytes, texelLow, texelHigh)
      }
    }

    private def loadLocalLane(load: Instruction.LoadLocal, lane: Lane): Fault | Null = {
      val bytes = load.bytes
      val at    = locate(load.address, lane, bytes)
      val fault = readTexel(at, lane, bytes)
      if (fault != null) fault
      else {
        val value =
          Execute.loaded(texelLow, Math.min(bytes * 8, 64).unsafe[Int in 1 ~ 64], load.signed)
        val target = Bits.low32(scalar(m0)).toLong + load.offset + lane.toLong * Math.max(4, bytes)
        val offset = checkedLdsOffset(lds, target, bytes)
        if (offset == InvalidLdsOffset) {
          val data = Array.ofDim[Byte](bytes)
          (0 until bytes).foreach(index =>
            data(index) = ((if (index < 8) value >>> (index * 8) else texelHigh >>> ((index - 8) * 8)) & 0xffL).toByte
          )
          writeLDS(lds, target, data)
        } else {
          LittleEndian.write(value, lds, offset, Math.min(bytes, 8).unsafe[Int in 0 ~ 8])
          if (bytes > 8) LittleEndian.write(texelHigh, lds, offset + 8, (bytes.raw - 8).unsafe[Int in 0 ~ 8])
        }
        null
      }
    }

    private def storeLocalLane(store: Instruction.StoreLocal, lane: Lane): Fault | Null = {
      val source = Bits.low32(scalar(m0)).toLong + store.offset + lane.toLong * 4L
      val offset = checkedLdsOffset(lds, source, 4)
      val value  =
        if (offset == InvalidLdsOffset) LittleEndian.decode(ldsBytes(lds, source, 4), 0, 4)
        else LittleEndian.decode(lds, offset, 4)
      writeValue(locate(store.address, lane, 4), lane, value, 4)
    }

    private def scalarBufferLoad(load: Instruction.SBufferLoad): Either[Fault, Int] = {
      val first  = load.resource
      val base   = toRaw[UInt[64]](BufferResource.base(dword(first), dword(first + 1)))
      val offset = Bits.low32(load.offset + load.scalarOffset.fold(0L)(register => scalar(register))).toLong
      val size   = load.elementBytes
      val start  = if (size == 4) Execute.scalarDword(offset) else offset
      val kept   = toRaw[UInt[32]](
        BufferResource.scalarElements(
          generation = generation,
          word1 = dword(first + 1),
          word2 = dword(first + 2),
          word3 = dword(first + 3),
          start = fromRaw[UInt[32]](start),
          size = size,
          count = load.count
        )
      )
      (if (kept == 0L) Right(Vec.empty[Byte])
       else dispatchMemory.readBytes(base + start, (kept * size).toInt.unsafe[Int >= 0])).map { bytes =>
        (0 until load.count).foreach { register =>
          val value =
            if (register < kept) LittleEndian.decode(bytes, register * size, size.widen[Int in 0 ~ 8]) else 0L
          if (load.dest + register != discarded)
            scalar(load.dest + register) =
              if (load.signed) Bits.low32(Bits.signExtend(value, (size * 8).unsafe[Int in 1 ~ 64])).toLong else value
        }
        Step.Next
      }
    }

    private def imageLane(image: Instruction.Image, resource: ImageResource, lane: Lane): Fault | Null = {
      val address = image.address
      image.operation match {
        case ImageOperation.Dimensions =>
          // the level is read before the results, which may overwrite its register
          val level     = imageCoordinate(address, lane, 0).toInt
          var written   = 0
          var component = 0
          while (component < 4) {
            if ((image.mask & (1 << component)) != 0) {
              writeComponent(
                registerFile(image.bank),
                image.data,
                lane,
                written,
                resource.information(level, component),
                image.packing
              )
              written += 1
            }
            component += 1
          }
          null
        case operation =>
          val at    = imageTexel(resource, address, lane)
          val bytes = resource.texelBytes
          located = if (at < 0L) Location.Discarded else Location.Global
          operation match {
            case ImageOperation.Load(converted, signed) =>
              val fault = readTexel(at, lane, bytes)
              if (fault == null) {
                val half      = image.packing != Packing.Dword
                var written   = 0
                var component = 0
                while (component < 4) {
                  if ((image.mask & (1 << component)) != 0) {
                    val value =
                      if (converted)
                        texelComponent(resource.format, TexelFormat.select(resource.selects, component), half)
                      else ImageResource.channel(resource.format, component, texelLow, texelHigh, signed, half)
                    writeComponent(registerFile(image.bank), image.data, lane, written, value, image.packing)
                    written += 1
                  }
                  component += 1
                }
              }
              fault
            case _ =>
              val converted = operation match {
                case ImageOperation.Store(converted = stored) => stored
                case _                                        => false
              }
              val components = texelComponents
              java.util.Arrays.fill(components, 0L)
              var read      = 0
              var component = 0
              while (component < 4) {
                if ((image.mask & (1 << component)) != 0) {
                  val value = readComponent(registerFile(image.bank), image.data, lane, read, image.packing)
                  components(component) =
                    if (image.packing == Packing.Dword || !converted) value
                    else TexelFormat.widen(resource.format.number, value)
                  read += 1
                }
                component += 1
              }
              packTexel(
                resource.format,
                if (converted) resource.selects else ImageResource.IdentitySelects,
                components,
                converted
              )
              writeTexel(at, lane, bytes, texelLow, texelHigh)
          }
      }
    }

    inline def pc: Long = programCounter(instructionIndex)

    inline def advance: Flow[Int] = NextStep
    inline def done: Flow[Unit]   = Completed

    inline def uniform(operand: InstructionSource): Long = source(operand, 0)

    inline def uniformPair(operand: InstructionSource): Long = sourceScalar64(operand)

    inline def condition(condition: ScalarBranchCondition): Long = if (holds(condition)) 1L else 0L

    inline def jump(target: Int): Flow[Int] = jumps(target)

    inline def jumpTo(target: Long): Flow[Int] = programIndex(target) match {
      case Some(index) => jumps(index)
      case None        =>
        Left(Fault.Backend(f"AMDGPU setpc at 0x$pc%X targets non-instruction 0x$target%X"))
    }

    inline def branch(condition: Long, taken: Int): Flow[Int] = if (condition != 0L) jumps(taken) else NextStep

    inline def branch(condition: Long, taken: Int, otherwise: Int): Flow[Int] =
      jumps(if (condition != 0L) taken else otherwise)

    inline def exit: Flow[Int] = dispatchMemory.memory.flushHostWrites() match {
      case Left(fault) => Left(fault)
      case _           => ExitStep
    }

    inline def read(lane: Lane, operand: InstructionSource): Long = source(operand, lane)

    inline def compute(primitive: Primitive[Primitive.Unary], first: Long): Long = primitive.function(first)

    inline def compute(primitive: Primitive[Primitive.Binary], first: Long, second: Long): Long =
      primitive.function(first, second)

    inline def compute(primitive: Primitive[Primitive.Ternary], first: Long, second: Long, third: Long): Long =
      primitive.function(first, second, third)

    inline def require[A](condition: Long, label: String)(inline fault: => Fault)(inline next: => Flow[A]): Flow[A] =
      if (condition != 0L) next else Left(fault)

    inline def writePair(target: ScalarRegisterIndex, value: Long)(inline next: => Flow[Int]): Flow[Int] = {
      writeScalar64(target, value)
      next
    }

    inline def callTo(link: ScalarRegisterIndex, returnAddress: Long, target: Long): Flow[Int] =
      called(link, returnAddress, target)

    inline def fork(mask: Long, taken: Int): Flow[Int] = forked(mask, taken)

    inline def forkTo(mask: Long, taken: Long): Flow[Int] = programIndex(taken) match {
      case Some(index) => forked(mask, index)
      case None        =>
        Left(Fault.Backend(f"AMDGPU fork at 0x$pc%X targets non-instruction 0x$taken%X"))
    }

    inline def join(saved: Long): Flow[Int] = joined(saved)

    inline def trap(code: Int): Flow[Int] = Left(Fault.Trap(f"AMDGPU s_trap $code%d at pc 0x$pc%x"))

    inline def halt(reason: String): Flow[Int] = Left(Fault.Backend(f"AMDGPU $reason at pc 0x$pc%x"))

    inline def fenceHost: Flow[Int] = dispatchMemory.memory.flushHostWrites().flatMap(_ => NextStep)

    inline def barrier: Flow[Int] = arrive

    private def arrive: Flow[Int] = Right(Step.Arrive)

    inline def initBarrier(id: Long, count: Long): Flow[Int] =
      barriers.initialise(id.toInt, count.toInt).flatMap(_ => NextStep)

    inline def joinBarrier(id: Long): Flow[Int] = barriers.join(id.toInt, localStart / waveSize).flatMap(_ => NextStep)

    inline def leaveBarrier(inline next: Long => Flow[Int]): Flow[Int] =
      next(if (barriers.leave(localStart / waveSize)) 1L else 0L)

    inline def signalBarrier(id: Long, count: Long)(inline next: Long => Flow[Int]): Flow[Int] =
      barriers.signal(id.toInt, localStart / waveSize, count.toInt).flatMap(first => next(if (first) 1L else 0L))

    inline def awaitBarrier(id: Long): Flow[Int] = barriers.await(id.toInt, localStart / waveSize).flatMap { ready =>
      // A named barrier releases its waves when its phase moves past the one they last saw.
      if (ready) NextStep else blocked(MachineUnit.Reason.Barrier(barrierName(id.toInt)))
    }

    inline def writeCarry(value: Long)(inline next: => Flow[Int]): Flow[Int] = {
      scalarCarry = value != 0L
      next
    }

    inline def writeMode(value: Long)(inline next: => Flow[Int]): Flow[Int] = {
      mode = value.toInt
      next
    }

    inline def writeWord(target: ScalarRegisterIndex, value: Long)(inline next: => Flow[Int]): Flow[Int] = {
      writeScalar32(target, value)
      next
    }

    inline def writeSlot(target: ScalarRegisterIndex, value: Long)(inline next: => Flow[Int]): Flow[Int] = {
      if (target.raw != discarded) scalar(target) = value
      next
    }

    inline def relativeOffset: Long = Bits.low32(scalar(m0)).toLong

    inline def scalarAt(index: Long): Long =
      source(InstructionSource.Scalar(index.toInt.unsafe[ScalarRegisterIndex]), 0)

    inline def writeScalarAt(index: Long, value: Long)(inline next: => Flow[Int]): Flow[Int] = {
      writeScalar32(index.toInt.unsafe[ScalarRegisterIndex], value)
      next
    }

    inline def requireScalars(destination: Long, origin: Long, words: Int)(inline next: => Flow[Int]): Flow[Int] =
      if (destination < 0L || destination + words > 128L || origin + words > 128L)
        Left(Fault.Backend(s"relative SGPR move from s$origin to s$destination is out of range"))
      else next

    inline def bind(value: Long)(inline next: Long => Flow[Int]): Flow[Int] = next(value)

    // the host's monotonic clock, at 1 GHz for the shader clock and 100 MHz for real time
    inline def clock(source: Clock): Long = source match {
      case Clock.RealTime => platform.monotonicNanos / 10L
      case _              => platform.monotonicNanos
    }

    inline def waveId: Long = (localStart / waveSize).toLong

    // A following ordinary load must not reuse bytes cached before acquire.
    inline def invalidateHostReads: Flow[Int] = {
      dispatchMemory.memory.invalidateHostReads()
      NextStep
    }

    inline def writebackInvalidateHost: Flow[Int] = fenceHost.flatMap(_ => invalidateHostReads)

    inline def localCounter(
        offset: Int,
        decrement: Boolean,
        dest: VectorRegisterIndex,
        bank: RegisterBank
    ): Flow[Int] = {
      val at    = offset.toLong
      val old   = LittleEndian.decode(ldsBytes(lds, at, 4), 0, 4)
      val count = java.lang.Long.bitCount(active.bits).toLong
      writeLDS(lds, at, LittleEndian.toArray(Bits.low32(if (decrement) old - count else old + count).toLong, 4))
      val registers = this.registers(bank, dest)
      foreachActive(lane => registers(lane) = old)
      NextStep
    }

    inline def globalWaveSync(
        name: String,
        operation: GlobalWaveSyncOperation,
        offset: Int,
        data: VectorRegisterIndex,
        bank: RegisterBank
    ): Flow[Int] = waveSync(name, operation, offset, data, bank)

    private def waveSync(
        name: String,
        operation: GlobalWaveSyncOperation,
        offset: Int,
        data: VectorRegisterIndex,
        bank: RegisterBank
    ): Flow[Int] = gws match {
      case null      => Left(Fault.Unsupported(s"$name needs GWS resources, which only a cooperative dispatch has"))
      case resources =>
        if (active.isEmpty) NextStep
        else {
          val resource = ((Bits.low32(scalar(m0)).toInt >>> 16) + offset) & (GlobalWaveSyncResources.Count - 1)
          val lane     = java.lang.Long.numberOfTrailingZeros(active.bits)
          val value    = Bits.low32(registers(bank, data)(lane)).toLong
          gwsWait = resources.step(operation, resource, value, gwsWait)
          // A GWS resource is a launch-wide barrier or semaphore, which the shared vocabulary has no reason for; the
          // wave reports the wait for the resource as a memory wait, the nearest the vocabulary offers.
          if (gwsWait == GlobalWaveSyncResources.Idle) NextStep else blocked(MachineUnit.Reason.Memory)
        }
    }

    // calls and returns share this primitive: the saved return address in the scalar pair is the frame
    private def called(link: ScalarRegisterIndex, returnAddress: Long, target: Long): Flow[Int] =
      kernel.emulatedCalls.get(target) match {
        case Some(call) =>
          foreachActive { lane =>
            def component(doubleRegister: VectorRegisterIndex, singleRegister: VectorRegisterIndex): Double =
              if (call.double) IEEE754.binary64ToDouble(pair(vector, doubleRegister, lane))
              else IEEE754.binary32ToFloat(Bits.low32(vector(singleRegister)(lane))).toDouble
            val (resultReal, resultImaginary) = call.function(component(0, 0), component(2, 1))
            if (call.double) {
              val realBits      = IEEE754.doubleToBinary64(resultReal)
              val imaginaryBits = IEEE754.doubleToBinary64(resultImaginary)
              vector(0)(lane) = Bits.low32(realBits).toLong
              vector(1)(lane) = Bits.high32(realBits).toLong
              vector(2)(lane) = Bits.low32(imaginaryBits).toLong
              vector(3)(lane) = Bits.high32(imaginaryBits).toLong
            } else {
              vector(0)(lane) = IEEE754.floatToBinary32(resultReal.toFloat).toLong
              vector(1)(lane) = IEEE754.floatToBinary32(resultImaginary.toFloat).toLong
            }
          }
          NextStep
        case None =>
          programIndex(target) match {
            case Some(targetIndex) =>
              writeScalar64(link, returnAddress)
              Right(targetIndex)
            case None =>
              kernel.functionTargets.get(target) match {
                case Some(name) => Left(Fault.Unsupported(s"AMDGPU call to $name is not implemented"))
                case None => Left(Fault.Backend(f"AMDGPU call target 0x$target%X is not present in the code object"))
              }
          }
      }

    // Split the instruction switch into JIT-sized methods. The complete switch is too large for C2 to compile.
    def step(index: Int): Either[Fault, Int] = operations(index) match {
      case Instruction.End        => ScalarControl.end(this)
      case Instruction.Trap(code) => ScalarControl.trap(this)(code)
      case Instruction.Wait       => ScalarControl.fence(this)
      case Instruction.Sleep      =>
        dispatchMemory.memory.flushHostWrites().flatMap(_ => if (resident) YieldStep else NextStep)
      case Instruction.SGprIndexOff                       => NextStep
      case Instruction.Barrier                            => ScalarControl.barrier(this)
      case Instruction.BarrierSignal(id, isFirst, fromM0) => ScalarControl.signal(Execute, this)(id, isFirst, fromM0)
      case Instruction.BarrierInit(id, fromM0, count)     => ScalarControl.initBarrier(Execute, this)(id, fromM0, count)
      case Instruction.BarrierJoin(id, fromM0)            => ScalarControl.joinBarrier(Execute, this)(id, fromM0)
      case Instruction.BarrierLeave                       => ScalarControl.leaveBarrier(this)
      case Instruction.BarrierWait(id)                    => ScalarControl.awaitBarrier(Execute, this)(id)
      case Instruction.Branch(condition, target)          => ScalarControl.branch(this)(condition, target)
      case Instruction.BranchRelative(_, _)               =>
        Left(Fault.Backend("unresolved AMDGPU branch reached execution"))
      case Instruction.Call(dest, target) => ScalarControl.call(Execute, this)(dest, target)
      case Instruction.CallRelative(_, _) => Left(Fault.Backend("unresolved AMDGPU call reached execution"))
      case Instruction.SLoad(dest, count, base, offset, scalarOffset, elementBytes, signed) =>
        val unaligned = Execute.scalarAddress(
          pair(scalar, base),
          offset,
          scalarOffset match {
            case Some(register) => scalar(register)
            case None           => 0L
          }
        )
        val address = if (elementBytes == 4) Execute.scalarDword(unaligned) else unaligned
        dispatchMemory.readBytes(address, count.widen[Int > 0] * elementBytes.widen[Int > 0]) match {
          case Left(error)  => Left(error)
          case Right(bytes) =>
            (0 until count).foreach { register =>
              val value = LittleEndian.decode(bytes, register * elementBytes, elementBytes)
              if (dest + register != discarded)
                scalar(dest + register) =
                  if (signed) Bits.low32(Bits.signExtend(value, (elementBytes * 8).unsafe[Int in 1 ~ 64])).toLong
                  else value
            }
            NextStep
        }
      case state @ (Instruction.SSetReg | _: Instruction.SSetMode | _: Instruction.SGetMode | _: Instruction.SGetPc |
          _: Instruction.SReadWaveId | _: Instruction.SReadClock | _: Instruction.Halt | Instruction.CacheInvalidate) =>
        stepState(state)
      case Instruction.SSetPc(src)               => ScalarControl.setPc(this)(src)
      case Instruction.SSwapPc(dest, src)        => ScalarControl.swapPc(Execute, this)(dest, src)
      case load: Instruction.Load                => stepMemory(load)
      case store: Instruction.Store              => stepMemory(store)
      case atomic: Instruction.Atomic            => updatedAtomically(atomic)
      case update: Instruction.LocalSource2      => updatedFromSource2(update)
      case exchange: Instruction.CompareSwap     => AtomicStaged.compareSwap(Execute, exchange)(this)
      case extended: Instruction.AtomicExtended  => AtomicStaged.extendedAtomic(Execute, extended)(this)
      case counter: Instruction.LocalCounter     => counted(counter)
      case moved: Instruction.SMoveRelative      => movedRelative(moved)
      case sync: Instruction.GlobalWaveSync      => ScalarControl.globalWaveSync(this)(sync)
      case pair: Instruction.LoadPair            => stepMemory(pair)
      case pair: Instruction.StorePair           => stepMemory(pair)
      case transposed: Instruction.LoadTranspose => transposedLoad(transposed)
      case Instruction.CacheWritebackInvalidate  => stepMemory(Instruction.CacheWritebackInvalidate)
      case resource @ (_: Instruction.FormatLoad | _: Instruction.FormatStore | _: Instruction.LoadLocal |
          _: Instruction.StoreLocal | _: Instruction.SBufferLoad | _: Instruction.Image | _: Instruction.WithStatus) =>
        stepResource(resource)
      case other => stepStaged(other)
    }

    private def stepState(instruction: Instruction): Either[Fault, Int] = instruction match {
      case Instruction.SSetReg                     => NextStep
      case Instruction.SSetMode(offset, size, src) =>
        ScalarControl.setMode(Execute, this)(offset, size, src, modeWritable)
      case Instruction.SGetMode(dest, offset, size) => ScalarControl.getMode(Execute, this)(dest, offset, size)
      case Instruction.SGetPc(dest, _)              => ScalarControl.getPc(Execute, this)(dest)
      case Instruction.SReadWaveId(dest)            => ScalarControl.readWaveId(this)(dest)
      case Instruction.SReadClock(dest, clock)      => ScalarControl.readClock(Execute, this)(dest, clock)
      case Instruction.Halt(reason)                 => ScalarControl.halt(this)(reason)
      case Instruction.CacheInvalidate              => ScalarControl.invalidate(this)
      case other                                    =>
        Left(Fault.IllegalInstruction(s"unhandled AMDGPU instruction $other"))
    }

    private def skipped(index: Int): Boolean = skippedOperations.nonEmpty && skippedOperations(index)

    /** Steps the instruction at `index`, or skips it as a vector instruction while VSKIP is set. */
    def stepOrSkip(index: Int): Either[Fault, Int] =
      if ((mode & (1 << ScalarControl.VectorSkipShift)) != 0 && skipped(index)) NextStep else step(index)

    /** The scalar stores and the branch stack, which execution dispatches apart from the frequent instructions. */

    private def stepScalar(instruction: Instruction): Either[Fault, Int] = instruction match {
      case store: Instruction.SStore              => scalarStore(store)
      case Instruction.Fork(mask, target)         => ScalarControl.fork(this)(mask, target)
      case Instruction.ForkIndirect(mask, target) => ScalarControl.forkIndirect(this)(mask, target)
      case Instruction.Join(saved)                => ScalarControl.join(this)(saved)
      case loop: Instruction.SubvectorLoop        => subvectorLoop(loop)
      case branch @ (_: Instruction.ForkRelative | _: Instruction.SetVectorSkip | _: Instruction.SubvectorLoopRelative) =>
        branchStack(branch)
      case other => Left(Fault.IllegalInstruction(s"unhandled AMDGPU instruction $other"))
    }

    /** The conditional branch stack, whose pointer is MODE's CSP field, each entry the address and EXEC of a deferred
      * side in four SGPRs from four times its index, forks to the instruction `target` the lanes of EXEC `mask` has
      * set.
      */
    private def forked(mask: Long, target: Int): Either[Fault, Int] = {
      val pointer                                   = (mode >>> StackPointerShift) & 7
      val exec                                      = active.register
      val taken                                     = mask & exec
      val other                                     = ~mask & exec
      def push(address: Long, deferred: Long): Unit = {
        writeScalar64((pointer * 4).unsafe[ScalarRegisterIndex], address)
        writeScalar64((pointer * 4 + 2).unsafe[ScalarRegisterIndex], deferred)
        mode = (mode & ~(7 << StackPointerShift)) | (((pointer + 1) & 7) << StackPointerShift)
      }
      if (taken == exec) jumps(target)
      else if (other == exec) NextStep
      else if (java.lang.Long.bitCount(other) < java.lang.Long.bitCount(taken)) {
        active.register = other
        push(programCounter(target), taken)
        NextStep
      } else {
        active.register = taken
        push(pc + 4L, other)
        jumps(target)
      }
    }

    /** Pops the deferred side of a fork unless the pointer is already at the depth `saved` names. */
    private def joined(saved: Long): Either[Fault, Int] = {
      val pointer = (mode >>> StackPointerShift) & 7
      if (Bits.low32(saved).toLong == pointer.toLong) NextStep
      else {
        val popped  = (pointer - 1) & 7
        val address = pair(scalar, (popped * 4).unsafe[ScalarRegisterIndex])
        mode = (mode & ~(7 << StackPointerShift)) | (popped << StackPointerShift)
        active.register = pair(scalar, (popped * 4 + 2).unsafe[ScalarRegisterIndex])
        programIndex(address).fold(
          Left(Fault.Backend(f"AMDGPU join at 0x$pc%X returns to non-instruction 0x$address%X"))
        )(Right(_))
      }
    }

    /** S_SETVSKIP, and the relative forms of the branch stack, which the compiler resolves before execution. */
    private def branchStack(operation: Instruction): Either[Fault, Int] =
      operation match {
        case Instruction.SetVectorSkip(src, bit) => ScalarControl.setVectorSkip(Execute, this)(src, bit)
        case other                               => Left(Fault.Backend(s"unresolved AMDGPU $other reached execution"))
      }

    private def movedRelative(move: Instruction.SMoveRelative): Either[Fault, Int] =
      ScalarControl.moveRelative(Execute, this)(move.dest, move.src, move.words, move.relativeSource, move.relativeDest)

    private def subvectorLoop(loop: Instruction.SubvectorLoop): Either[Fault, Int] =
      ScalarControl.subvectorLoop(Execute, this)(loop.begin, loop.state, loop.target, waveSize == 64)

    // A wave's lanes take part in one LDS update.
    // Kept out of step, which inlining every update's lanes would push past HotSpot's huge-method limit.
    private def updatedFromSource2(update: Instruction.LocalSource2): Either[Fault, Int] =
      AtomicStaged.localSource2(Execute, update)(this)

    // kept out of step for the same reason: every atomic operation inlines its own lane loop
    private def updatedAtomically(atomic: Instruction.Atomic): Either[Fault, Int] =
      AtomicStaged.atomic(Execute, atomic)(this)

    private def counted(counter: Instruction.LocalCounter): Either[Fault, Int] =
      ScalarControl.localCounter(this)(counter)

    /** Executes an instruction with staged semantics. */
    def stepStaged(instruction: Instruction): Either[Fault, Int] =
      AMDGPUStaged.staged(Execute, instruction)(this)(stepScalar(instruction))

    private inline def scalarCarryBit: Long = if (scalarCarry) 1L else 0L

    private inline def carryBit(lane: Lane): Long = (carry.bits >>> lane) & 1L

    private def registers(bank: RegisterBank, index: VectorRegisterIndex): Array[Long] = bank match {
      case RegisterBank.Vector      => vector(index)
      case RegisterBank.Accumulator => accumulator(index)
    }

    private def bindDwords(srcA: InstructionSource, srcB: InstructionSource, srcC: InstructionSource): Unit = {
      first.bind(srcA)
      second.bind(srcB)
      third.bind(srcC)
    }

    private def bindPairs(srcA: InstructionSource, srcB: InstructionSource, srcC: InstructionSource): Unit = {
      first.bindPair(srcA, PairRead.Floating)
      second.bindPair(srcB, PairRead.Floating)
      third.bindPair(srcC, PairRead.Floating)
    }

    private def writeConditionMask(destination: ScalarRegisterIndex): Either[Fault, Int] = {
      if (waveSize <= 32) writeScalar32(destination, conditionMask)
      else writeScalar64(destination, conditionMask)
      NextStep
    }

    private def finishCompare(destination: ScalarRegisterIndex, updateExec: Boolean): Either[Fault, Int] = {
      if (updateExec) active.bits = conditionMask
      updateLaneMask(destination, conditionMask)
      NextStep
    }

    private def updatesExec(instruction: Instruction): Boolean = instruction match {
      case Instruction.VCompare(operation = comparison) => comparison.updateExec
      case _                                            => false
    }

    /** Whether a routed source lane exists and EXEC, or fetching inactive lanes, lets it be read. */
    private def readable(from: Long, fetchInactive: Boolean): Boolean =
      from >= 0L && from < lanes.toLong && (fetchInactive || (active.bits & (1L << from)) != 0L)

    // The sinks of the staged semantics. Each lane loop is a local method of its own, so a dispatch over many kernels
    // stays small and the loops take their operands as parameters.

    inline def modeRegister: Long = Integer.toUnsignedLong(mode)

    inline def section(inline body: Either[Fault, Int]): Either[Fault, Int] = {
      def run(): Either[Fault, Int] = body
      run()
    }

    inline def sequence(inline before: Either[Fault, Int], inline after: Either[Fault, Int]): Either[Fault, Int] =
      before match {
        case Right(_) => after
        case failure  => failure
      }

    inline def scalarDword(dest: ScalarRegisterIndex, a: InstructionSource, b: InstructionSource)(
        inline result: (Long, Long, Long) => Long
    ): Either[Fault, Int] = {
      def write(dest: ScalarRegisterIndex, a: InstructionSource, b: InstructionSource): Either[Fault, Int] = {
        writeScalar32(dest, result(Bits.low32(source(a, 0)).toLong, Bits.low32(source(b, 0)).toLong, scalarCarryBit))
        NextStep
      }
      write(dest, a, b)
    }

    inline def scalarDwordFlagged(dest: ScalarRegisterIndex, a: InstructionSource, b: InstructionSource)(
        inline result: (Long, Long, Long) => Long,
        inline flag: (Long, Long, Long, Long) => Long
    ): Either[Fault, Int] = {
      def write(dest: ScalarRegisterIndex, a: InstructionSource, b: InstructionSource): Either[Fault, Int] = {
        val left  = Bits.low32(source(a, 0)).toLong
        val right = Bits.low32(source(b, 0)).toLong
        val carry = scalarCarryBit
        val value = result(left, right, carry)
        scalarCarry = flag(left, right, carry, value) != 0L
        writeScalar32(dest, value)
        NextStep
      }
      write(dest, a, b)
    }

    inline def scalarPair(dest: ScalarRegisterIndex, a: InstructionSource, b: InstructionSource)(
        inline result: (Long, Long, Long) => Long
    ): Either[Fault, Int] = {
      def write(dest: ScalarRegisterIndex, a: InstructionSource, b: InstructionSource): Either[Fault, Int] = {
        writeScalar64(dest, result(sourceScalar64(a), sourceScalar64(b), scalarCarryBit))
        NextStep
      }
      write(dest, a, b)
    }

    inline def scalarPairFlagged(dest: ScalarRegisterIndex, a: InstructionSource, b: InstructionSource)(
        inline result: (Long, Long, Long) => Long,
        inline flag: (Long, Long, Long, Long) => Long
    ): Either[Fault, Int] = {
      def write(dest: ScalarRegisterIndex, a: InstructionSource, b: InstructionSource): Either[Fault, Int] = {
        val left  = sourceScalar64(a)
        val right = sourceScalar64(b)
        val carry = scalarCarryBit
        val value = result(left, right, carry)
        scalarCarry = flag(left, right, carry, value) != 0L
        writeScalar64(dest, value)
        NextStep
      }
      write(dest, a, b)
    }

    inline def scalarPairDwordFlagged(dest: ScalarRegisterIndex, a: InstructionSource, b: InstructionSource)(
        inline result: (Long, Long, Long) => Long,
        inline flag: (Long, Long, Long, Long) => Long
    ): Either[Fault, Int] = {
      def write(dest: ScalarRegisterIndex, a: InstructionSource, b: InstructionSource): Either[Fault, Int] = {
        val left  = sourceScalar64(a)
        val right = Bits.low32(source(b, 0)).toLong
        val carry = scalarCarryBit
        val value = result(left, right, carry)
        scalarCarry = flag(left, right, carry, value) != 0L
        writeScalar64(dest, value)
        NextStep
      }
      write(dest, a, b)
    }

    private def scalarOperand(src: InstructionSource, pair: Boolean): Long =
      if (pair) sourceScalar64(src) else Bits.low32(source(src, 0)).toLong

    private def writeScalar(dest: ScalarRegisterIndex, value: Long, pair: Boolean): Unit =
      if (pair) writeScalar64(dest, value) else writeScalar32(dest, value)

    inline def scalarUnary(dest: ScalarRegisterIndex, a: InstructionSource, readsPair: Boolean, writesPair: Boolean)(
        inline result: (Long, Long) => Long
    ): Either[Fault, Int] = {
      def write(dest: ScalarRegisterIndex, a: InstructionSource, readsPair: Boolean, writesPair: Boolean)
          : Either[Fault, Int] = {
        writeScalar(dest, result(scalarOperand(a, readsPair), scalar(dest)), writesPair)
        NextStep
      }
      write(dest, a, readsPair, writesPair)
    }

    inline def scalarUnaryFlagged(
        dest: ScalarRegisterIndex,
        a: InstructionSource,
        readsPair: Boolean,
        writesPair: Boolean
    )(inline result: (Long, Long) => Long, inline flag: (Long, Long, Long) => Long): Either[Fault, Int] = {
      def write(dest: ScalarRegisterIndex, a: InstructionSource, readsPair: Boolean, writesPair: Boolean)
          : Either[Fault, Int] = {
        val value    = scalarOperand(a, readsPair)
        val previous = scalar(dest)
        val next     = result(value, previous)
        writeScalar(dest, next, writesPair)
        scalarCarry = flag(value, previous, next) != 0L
        NextStep
      }
      write(dest, a, readsPair, writesPair)
    }

    inline def scalarImmediate(dest: ScalarRegisterIndex, immediate: Long)(
        inline result: (Long, Long) => Long,
        inline flag: (Long, Long, Long) => Long
    ): Either[Fault, Int] = {
      def write(dest: ScalarRegisterIndex, immediate: Long): Either[Fault, Int] = {
        val previous = scalar(dest)
        val value    = result(previous, immediate)
        writeScalar32(dest, value)
        scalarCarry = flag(previous, immediate, value) != 0L
        NextStep
      }
      write(dest, immediate)
    }

    inline def saveExec(dest: ScalarRegisterIndex, a: InstructionSource, width: Int in (32, 64))(
        inline result: (Long, Long) => Long,
        inline flag: (Long, Long, Long) => Long
    ): Either[Fault, Int] = {
      def write(dest: ScalarRegisterIndex, a: InstructionSource, width: Int in (32, 64)): Either[Fault, Int] = {
        val previous = if (width == 32) Bits.low32(active.bits).toLong else active.bits
        val value    = saveExecSource(a, width)
        val next     = result(value, previous)
        saveExecWrite(dest, previous, width)
        saveExecWrite(Fixed.ExecLo, next, width)
        scalarCarry = flag(value, previous, next) != 0L
        NextStep
      }
      write(dest, a, width)
    }

    inline def writeExec(dest: ScalarRegisterIndex, a: InstructionSource, width: Int in (32, 64))(
        inline result: (Long, Long) => Long,
        inline flag: (Long, Long, Long) => Long
    ): Either[Fault, Int] = {
      def write(dest: ScalarRegisterIndex, a: InstructionSource, width: Int in (32, 64)): Either[Fault, Int] = {
        val previous = if (width == 32) Bits.low32(active.bits).toLong else active.bits
        val value    = saveExecSource(a, width)
        val next     = if (width == 32) Bits.low32(result(value, previous)).toLong else result(value, previous)
        saveExecWrite(Fixed.ExecLo, next, width)
        saveExecWrite(dest, next, width)
        scalarCarry = flag(value, previous, next) != 0L
        NextStep
      }
      write(dest, a, width)
    }

    inline def scalarCompare(a: InstructionSource, b: InstructionSource, pair: Boolean)(
        inline kernel: (Long, Long) => Long
    ): Either[Fault, Int] = {
      def compare(a: InstructionSource, b: InstructionSource, pair: Boolean): Either[Fault, Int] = {
        scalarCarry = kernel(scalarOperand(a, pair), scalarOperand(b, pair)) != 0L
        NextStep
      }
      compare(a, b, pair)
    }

    inline def scalarMove(dest: ScalarRegisterIndex, a: InstructionSource, pair: Boolean)(
        inline kernel: Long => Long
    ): Either[Fault, Int] = {
      def write(dest: ScalarRegisterIndex, a: InstructionSource, pair: Boolean): Either[Fault, Int] = {
        writeScalar(dest, kernel(if (pair) sourceScalar64(a) else source(a, 0)), pair)
        NextStep
      }
      write(dest, a, pair)
    }

    inline def scalarModified(dest: ScalarRegisterIndex, a: InstructionSource, modifiers: SourceModifiers)(
        inline kernel: Long => Long
    ): Either[Fault, Int] = {
      def write(dest: ScalarRegisterIndex, a: InstructionSource, modifiers: SourceModifiers): Either[Fault, Int] = {
        writeScalar32(dest, kernel(Bits.low32(modifiers(source(a, 0), 0, 32)).toLong))
        NextStep
      }
      write(dest, a, modifiers)
    }

    inline def scalarTernary(
        dest: ScalarRegisterIndex,
        a: InstructionSource,
        b: InstructionSource,
        c: InstructionSource
    )(inline kernel: (Long, Long, Long) => Long): Either[Fault, Int] = {
      def write(dest: ScalarRegisterIndex, a: InstructionSource, b: InstructionSource, c: InstructionSource)
          : Either[Fault, Int] = {
        writeScalar32(dest, kernel(source(a, 0), source(b, 0), source(c, 0)))
        NextStep
      }
      write(dest, a, b, c)
    }

    inline def readFirstLane(dest: ScalarRegisterIndex, a: InstructionSource)(
        inline kernel: Long => Long
    ): Either[Fault, Int] = {
      def write(dest: ScalarRegisterIndex, a: InstructionSource): Either[Fault, Int] = active.first match {
        case None       => Left(Fault.Backend("v_readfirstlane_b32 has an empty EXEC mask"))
        case Some(lane) =>
          writeScalar32(dest, kernel(source(a, lane)))
          NextStep
      }
      write(dest, a)
    }

    inline def readLane(dest: ScalarRegisterIndex, a: InstructionSource, lane: InstructionSource)(
        inline index: (Long, Long) => Long
    )(inline kernel: Long => Long): Either[Fault, Int] = {
      def write(dest: ScalarRegisterIndex, a: InstructionSource, lane: InstructionSource): Either[Fault, Int] = {
        writeScalar32(dest, kernel(source(a, index(source(lane, 0), waveSize.toLong).toInt.unsafe[Lane])))
        NextStep
      }
      write(dest, a, lane)
    }

    inline def writeLane(dest: VectorRegisterIndex, a: InstructionSource, lane: InstructionSource)(
        inline index: (Long, Long) => Long
    )(inline kernel: Long => Long): Either[Fault, Int] = {
      def write(dest: VectorRegisterIndex, a: InstructionSource, lane: InstructionSource): Either[Fault, Int] = {
        val selected = index(source(lane, 0), waveSize.toLong).toInt.unsafe[Lane]
        vector(dest)(selected) = kernel(source(a, selected))
        NextStep
      }
      write(dest, a, lane)
    }

    // A uniform source is read and computed once; a high half depends on the lane.
    inline def vectorUnary(bank: RegisterBank, dest: VectorRegisterIndex, a: InstructionSource)(
        inline kernel: Long => Long
    ): Either[Fault, Int] = {
      def lanes(target: Array[Long], a: InstructionSource): Either[Fault, Int] = {
        a match {
          case InstructionSource.VectorRegister(index) =>
            val values = vector(index)
            foreachActive(lane => target(lane) = kernel(values(lane)))
          case InstructionSource.DPP => foreachActive(lane => target(lane) = kernel(routed(lane)))
          case InstructionSource.Selected(InstructionSource.VectorRegister(index), part, extend) =>
            val values = vector(index)
            foreachActive { lane =>
              target(lane) =
                kernel(Bits.low32(Execute.selectedPart(values(lane), part, extend)).toLong)
            }
          case InstructionSource.HighHalf(_) | InstructionSource.Selected(source = _) =>
            foreachActive(lane => target(lane) = kernel(source(a, lane)))
          case uniform =>
            val value = kernel(source(uniform, 0))
            foreachActive(lane => target(lane) = value)
        }
        NextStep
      }
      lanes(registers(bank, dest), a)
    }

    inline def accumulatorMove(
        destinationBank: RegisterBank,
        dest: VectorRegisterIndex,
        src: VectorRegisterIndex
    )(inline kernel: Long => Long): Either[Fault, Int] = {
      def lanes(target: Array[Long], values: Array[Long]): Either[Fault, Int] = {
        foreachActive(lane => target(lane) = kernel(values(lane)))
        NextStep
      }
      lanes(registers(destinationBank, dest), registers(RegisterBank.Accumulator, src))
    }

    private val relativeLanes = Array.ofDim[Long](lanes)

    /** The VGPR `index` past `field` of M0, or 256 where that is beyond the last register. */
    private def relativeIndex(index: Int, field: RelativeField): Int = {
      val offset = (Bits.low32(scalar(m0)).toLong >>> field.shift) & field.mask
      if (offset < 256L - index) index + offset.toInt else 256
    }

    /** The VGPR `index` past `field` of M0, or VGPR 0 where that is beyond the last register. */
    private def relativeRead(index: Int, field: RelativeField): Array[Long] = {
      val at = relativeIndex(index, field)
      vector((if (at < 256) at else 0).unsafe[VectorRegisterIndex])
    }

    /** The VGPR `index` past `field` of M0, or null where that is beyond the last register. */
    private def relativeWritable(index: Int, field: RelativeField): Array[Long] | Null = {
      val at = relativeIndex(index, field)
      if (at < 256) vector(at.unsafe[VectorRegisterIndex]) else null
    }

    /** What a relative move reads: the VGPR M0 offsets, selected as SDWA says, or null for any other source. */
    private def relativeSource(a: InstructionSource, field: RelativeField): Array[Long] | Null = a match {
      case InstructionSource.VectorRegister(index) => relativeRead(index, field)
      case InstructionSource.Selected(InstructionSource.VectorRegister(index), part, extend) =>
        val values = relativeRead(index, field)
        var lane   = 0
        while (lane < relativeLanes.length) {
          relativeLanes(lane) = Bits.low32(Execute.selectedPart(values(lane), part, extend)).toLong
          lane += 1
        }
        relativeLanes
      case _ => null
    }

    inline def moveRelative(dest: VectorRegisterIndex, a: InstructionSource, field: RelativeField)(
        inline kernel: Long => Long
    ): Either[Fault, Int] = {
      def lanes(dest: VectorRegisterIndex, a: InstructionSource, field: RelativeField): Either[Fault, Int] =
        relativeSource(a, field) match {
          case null   => Left(Fault.Backend(s"v_movrels_b32 requires a vector-register source, found $a"))
          case values =>
            val target = vector(dest)
            foreachActive(lane => target(lane) = kernel(values(lane)))
            NextStep
        }
      lanes(dest, a, field)
    }

    inline def moveRelativeDestination(
        dest: VectorRegisterIndex,
        a: InstructionSource,
        sourceField: Option[RelativeField],
        field: RelativeField
    )(inline kernel: Long => Long): Either[Fault, Int] = {
      def lanes(
          dest: VectorRegisterIndex,
          a: InstructionSource,
          sourceField: Option[RelativeField],
          field: RelativeField
      ): Either[Fault, Int] = {
        val target = relativeWritable(dest, field)
        sourceField match {
          case Some(relativeField) =>
            relativeSource(a, relativeField) match {
              case null   => Left(Fault.Backend(s"v_movrelsd_b32 requires a vector-register source, found $a"))
              case values =>
                if (target != null) foreachActive(lane => target(lane) = kernel(values(lane)))
                NextStep
            }
          case None =>
            first.bind(a)
            if (target != null) foreachActive(lane => target(lane) = kernel(first(lane)))
            NextStep
        }
      }
      lanes(dest, a, sourceField, field)
    }

    inline def exchange(target: Array[Long], other: Array[Long], lane: Lane)(inline kernel: Long => Long): Unit = {
      val previous = target(lane)
      target(lane) = kernel(other(lane))
      other(lane) = kernel(previous)
    }

    inline def swapRelative(dest: VectorRegisterIndex, src: VectorRegisterIndex)(
        inline kernel: Long => Long
    ): Either[Fault, Int] = {
      // gfx1036 writes neither register where either offset is beyond the last
      def lanes(dest: VectorRegisterIndex, src: VectorRegisterIndex): Either[Fault, Int] = {
        val (target, other) =
          (relativeWritable(dest, RelativeField.Destination), relativeWritable(src, RelativeField.Source))
        if (target != null && other != null) foreachActive(lane => exchange(target, other, lane)(kernel))
        NextStep
      }
      lanes(dest, src)
    }

    inline def swap(
        dest: VectorRegisterIndex,
        a: InstructionSource
    )(inline kernel: Long => Long): Either[Fault, Int] = {
      def lanes(target: Array[Long], a: InstructionSource): Either[Fault, Int] = a match {
        case InstructionSource.VectorRegister(src) =>
          val other = vector(src)
          foreachActive(lane => exchange(target, other, lane)(kernel))
          NextStep
        case _ => Left(Fault.Backend("v_swap_b32 requires a vector-register source"))
      }
      lanes(vector(dest), a)
    }

    inline def swapHalves(instruction: Instruction.VSwapHalves)(
        inline dest: (Long, Long) => Long,
        inline src: (Long, Long) => Long
    ): Either[Fault, Int] = {
      def lanes(targets: Array[Long], sources: Array[Long]): Either[Fault, Int] = {
        foreachActive { lane =>
          val previous = targets(lane)
          targets(lane) = Bits.low32(dest(previous, sources(lane))).toLong
          sources(lane) = Bits.low32(src(sources(lane), previous)).toLong
        }
        NextStep
      }
      lanes(vector(instruction.dest), vector(instruction.src))
    }

    inline def modifiedUnary(
        dest: VectorRegisterIndex,
        a: InstructionSource,
        modifiers: SourceModifiers,
        width: Int in (16, 32, 64)
    )(inline kernel: Long => Long): Either[Fault, Int] = {
      def lanes(target: Array[Long], a: InstructionSource, modifiers: SourceModifiers, width: Int in (16, 32, 64))
          : Either[Fault, Int] = {
        first.bind(a)
        foreachActive(lane => target(lane) = kernel(Bits.low32(modifiers(first(lane), 0, width)).toLong))
        NextStep
      }
      lanes(vector(dest), a, modifiers, width)
    }

    inline def wideningUnary(dest: VectorRegisterIndex, a: InstructionSource, modifiers: SourceModifiers)(
        inline kernel: Long => Long
    ): Either[Fault, Int] = {
      def lanes(dest: VectorRegisterIndex, a: InstructionSource, modifiers: SourceModifiers): Either[Fault, Int] = {
        first.bind(a)
        val low  = vector(dest)
        val high = vector(dest.offsetBy(1))
        foreachActive { lane =>
          val result = kernel(Bits.low32(modifiers(first(lane), 0, 32)).toLong)
          low(lane) = Bits.low32(result).toLong
          high(lane) = Bits.high32(result).toLong
        }
        NextStep
      }
      lanes(dest, a, modifiers)
    }

    inline def narrowingUnary(dest: VectorRegisterIndex, a: InstructionSource, modifiers: SourceModifiers)(
        inline kernel: Long => Long
    ): Either[Fault, Int] = {
      def lanes(target: Array[Long], a: InstructionSource, modifiers: SourceModifiers): Either[Fault, Int] = {
        first.bindPair(a, PairRead.Floating)
        foreachActive(lane => target(lane) = kernel(modifiers(first.pair(lane), 0, 64)))
        NextStep
      }
      lanes(vector(dest), a, modifiers)
    }

    inline def pairUnary(dest: VectorRegisterIndex, a: InstructionSource, read: PairRead, modifiers: SourceModifiers)(
        inline kernel: Long => Long
    ): Either[Fault, Int] = {
      def lanes(dest: VectorRegisterIndex, a: InstructionSource, read: PairRead, modifiers: SourceModifiers)
          : Either[Fault, Int] = {
        first.bindPair(a, read)
        val low  = vector(dest)
        val high = vector(dest.offsetBy(1))
        foreachActive { lane =>
          val result = kernel(modifiers(first.pair(lane), 0, 64))
          low(lane) = Bits.low32(result).toLong
          high(lane) = Bits.high32(result).toLong
        }
        NextStep
      }
      lanes(dest, a, read, modifiers)
    }

    inline def vectorBinary(dest: VectorRegisterIndex, a: InstructionSource, b: InstructionSource)(
        inline kernel: (Long, Long) => Long
    ): Either[Fault, Int] = {
      def lanes(target: Array[Long], a: InstructionSource, b: InstructionSource): Either[Fault, Int] = {
        first.bind(a)
        second.bind(b)
        foreachActive { lane =>
          target(lane) = kernel(Bits.low32(first(lane)).toLong, Bits.low32(second(lane)).toLong)
        }
        NextStep
      }
      lanes(vector(dest), a, b)
    }

    // f16 operations take their modifiers on the binary16 sign bit.
    inline def vectorFloat(
        dest: VectorRegisterIndex,
        a: InstructionSource,
        b: InstructionSource,
        modifiers: SourceModifiers,
        width: Int in (16, 32, 64)
    )(inline kernel: (Long, Long, Long) => Long): Either[Fault, Int] = {
      def lanes(
          target: Array[Long],
          a: InstructionSource,
          b: InstructionSource,
          modifiers: SourceModifiers,
          width: Int in (16, 32, 64)
      ): Either[Fault, Int] = {
        first.bind(a)
        second.bind(b)
        foreachActive { lane =>
          target(lane) = kernel(
            Bits.low32(modifiers(first(lane), 0, width)).toLong,
            Bits.low32(modifiers(second(lane), 1, width)).toLong,
            Bits.low32(target(lane)).toLong
          )
        }
        NextStep
      }
      lanes(vector(dest), a, b, modifiers = modifiers, width = width)
    }

    inline def vectorTernary(
        dest: VectorRegisterIndex,
        a: InstructionSource,
        b: InstructionSource,
        c: InstructionSource,
        modifiers: SourceModifiers,
        width: Int in (16, 32, 64)
    )(inline kernel: (Long, Long, Long) => Long): Either[Fault, Int] = {
      def lanes(
          target: Array[Long],
          a: InstructionSource,
          b: InstructionSource,
          c: InstructionSource,
          modifiers: SourceModifiers,
          width: Int in (16, 32, 64)
      ): Either[Fault, Int] = {
        bindDwords(a, b, c)
        foreachActive { lane =>
          target(lane) = Bits.low32(kernel(
            Bits.low32(modifiers(first(lane), 0, width)).toLong,
            Bits.low32(modifiers(second(lane), 1, width)).toLong,
            Bits.low32(modifiers(third(lane), 2, width)).toLong
          )).toLong
        }
        NextStep
      }
      lanes(vector(dest), a, b, c, modifiers = modifiers, width = width)
    }

    inline def vectorCarry(dest: VectorRegisterIndex, a: InstructionSource, b: InstructionSource)(
        inline result: (Long, Long, Long) => Long,
        inline flag: (Long, Long, Long, Long) => Long
    ): Either[Fault, Int] = {
      def lanes(target: Array[Long], a: InstructionSource, b: InstructionSource): Either[Fault, Int] = {
        first.bind(a)
        second.bind(b)
        foreachActive { lane =>
          val x     = Bits.low32(first(lane)).toLong
          val y     = Bits.low32(second(lane)).toLong
          val c     = carryBit(lane)
          val value = result(x, y, c)
          target(lane) = value
          carry(lane) = flag(x, y, c, value) != 0L
        }
        NextStep
      }
      lanes(vector(dest), a, b)
    }

    inline def maskCarry(
        dest: VectorRegisterIndex,
        carryDest: ScalarRegisterIndex,
        a: InstructionSource,
        b: InstructionSource,
        carryIn: Option[InstructionSource]
    )(inline result: (Long, Long, Long) => Long, inline flag: (Long, Long, Long, Long) => Long)
        : Either[Fault, Int] = {
      def lanes(
          target: Array[Long],
          carryDest: ScalarRegisterIndex,
          a: InstructionSource,
          b: InstructionSource,
          carryIn: Option[InstructionSource]
      ): Either[Fault, Int] = {
        first.bind(a)
        second.bind(b)
        val carries = carryIn match {
          case Some(mask) => sourceScalar64(mask)
          case None       => 0L
        }
        conditionMask = 0L
        foreachActive { lane =>
          val x     = Bits.low32(first(lane)).toLong
          val y     = Bits.low32(second(lane)).toLong
          val c     = (carries >>> lane) & 1L
          val value = result(x, y, c)
          target(lane) = value
          if (flag(x, y, c, value) != 0L) conditionMask |= 1L << lane
        }
        updateLaneMask(carryDest, conditionMask)
        NextStep
      }
      lanes(vector(dest), carryDest, a, b, carryIn = carryIn)
    }

    inline def subDword(instruction: Instruction.VBinarySubDword)(
        inline kernel: (Long, Long) => Long
    ): Either[Fault, Int] = {
      def lanes(instruction: Instruction.VBinarySubDword): Either[Fault, Int] = {
        first.bind(instruction.srcA)
        second.bind(instruction.srcB)
        val target = vector(instruction.dest)
        foreachActive { lane =>
          target(lane) = Execute.placedPart(
            target(lane),
            kernel(
              Execute.selectedPart(
                first(lane),
                instruction.srcASelection,
                instruction.srcASignExtend
              ),
              Execute.selectedPart(
                second(lane),
                instruction.srcBSelection,
                instruction.srcBSignExtend
              )
            ),
            instruction.destSelection,
            instruction.destUnused
          )
        }
        NextStep
      }
      lanes(instruction)
    }

    inline def subDwordFlagged(instruction: Instruction.VBinarySubDword)(
        inline result: (Long, Long, Long) => Long,
        inline flag: (Long, Long, Long, Long) => Long
    ): Either[Fault, Int] = {
      def lanes(instruction: Instruction.VBinarySubDword): Either[Fault, Int] = {
        first.bind(instruction.srcA)
        second.bind(instruction.srcB)
        val target = vector(instruction.dest)
        foreachActive { lane =>
          val x = Bits.low32(Execute.selectedPart(
            first(lane),
            instruction.srcASelection,
            instruction.srcASignExtend
          )).toLong
          val y = Bits.low32(Execute.selectedPart(
            second(lane),
            instruction.srcBSelection,
            instruction.srcBSignExtend
          )).toLong
          val c     = carryBit(lane)
          val value = result(x, y, c)
          carry(lane) = flag(x, y, c, value) != 0L
          target(lane) = Execute.placedPart(
            target(lane),
            value,
            instruction.destSelection,
            instruction.destUnused
          )
        }
        NextStep
      }
      lanes(instruction)
    }

    inline def subDwordConditional(instruction: Instruction.VBinarySubDword)(
        inline result: (Long, Long, Long) => Long
    ): Either[Fault, Int] = {
      def lanes(instruction: Instruction.VBinarySubDword): Either[Fault, Int] = {
        first.bind(instruction.srcA)
        second.bind(instruction.srcB)
        val target = vector(instruction.dest)
        foreachActive { lane =>
          val value = result(
            Bits.low32(Execute.selectedPart(
              first(lane),
              instruction.srcASelection,
              instruction.srcASignExtend
            )).toLong,
            Bits.low32(Execute.selectedPart(
              second(lane),
              instruction.srcBSelection,
              instruction.srcBSignExtend
            )).toLong,
            carryBit(lane)
          )
          target(lane) = Execute.placedPart(
            target(lane),
            value,
            instruction.destSelection,
            instruction.destUnused
          )
        }
        NextStep
      }
      lanes(instruction)
    }

    inline def packedBinary(instruction: Instruction.VPackedBinary)(
        inline kernel: (Long, Long) => Long
    ): Either[Fault, Int] = {
      def lanes(instruction: Instruction.VPackedBinary): Either[Fault, Int] = {
        first.bind(instruction.srcA)
        second.bind(instruction.srcB)
        val target                                                  = vector(instruction.dest)
        val srcAHighForHigh                                         = instruction.srcAHighForHigh
        val srcBHighForHigh                                         = instruction.srcBHighForHigh
        val srcAHighForLow                                          = instruction.srcAHighForLow
        val srcBHighForLow                                          = instruction.srcBHighForLow
        val negateLow                                               = instruction.negateLow
        val negateHigh                                              = instruction.negateHigh
        def half(value: Long, high: Boolean, negate: Boolean): Long = {
          val part = if (high) value >>> 16 else Bits.low16(value).toLong
          if (negate) part ^ 0x8000L else part
        }
        foreachActive { lane =>
          val left  = Bits.low32(first(lane)).toLong
          val right = Bits.low32(second(lane)).toLong
          val low   = kernel(
            half(left, srcAHighForLow, (negateLow & 1) != 0),
            half(right, srcBHighForLow, (negateLow & 2) != 0)
          )
          val high = kernel(
            half(left, srcAHighForHigh, (negateHigh & 1) != 0),
            half(right, srcBHighForHigh, (negateHigh & 2) != 0)
          )
          target(lane) = low | (high << 16)
        }
        NextStep
      }
      lanes(instruction)
    }

    inline def select(
        dest: VectorRegisterIndex,
        a: InstructionSource,
        b: InstructionSource,
        condition: InstructionSource,
        modifiers: SourceModifiers
    )(inline kernel: (Long, Long, Long) => Long): Either[Fault, Int] = {
      def lanes(
          target: Array[Long],
          a: InstructionSource,
          b: InstructionSource,
          condition: InstructionSource,
          modifiers: SourceModifiers
      ): Either[Fault, Int] = {
        first.bind(a)
        second.bind(b)
        // Wave64 conditions occupy an SGPR pair; truncating it would make lanes 32-63 always select A.
        val conditions = waveConditionMask(sourceScalar64(condition), WaveRun.this.lanes)
        foreachActive { lane =>
          target(lane) =
            kernel(modifiers(first(lane), 0, 32), modifiers(second(lane), 1, 32), (conditions >>> lane) & 1L)
        }
        NextStep
      }
      lanes(vector(dest), a, b, condition = condition, modifiers = modifiers)
    }

    inline def maskCount(dest: VectorRegisterIndex, mask: InstructionSource, base: InstructionSource)(
        inline kernel: (Long, Long, Long) => Long
    ): Either[Fault, Int] = {
      def lanes(target: Array[Long], mask: InstructionSource, base: InstructionSource): Either[Fault, Int] = {
        first.bind(mask)
        second.bind(base)
        foreachActive(lane => target(lane) = kernel(first(lane), lane.toLong, second(lane)))
        NextStep
      }
      lanes(vector(dest), mask, base)
    }

    inline def dualFirst(half: DualVector)(inline kernel: (Long, Long, Long, Long) => Long): Either[Fault, Int] = {
      def lanes(half: DualVector): Either[Fault, Int] = {
        bindDual(half)
        val results = laneScratch()
        val mask    = dualCondition(half)
        if (half.operation.wide)
          foreachActive { lane =>
            results(lane) = kernel(first.pair(lane), second.pair(lane), third.pair(lane), (mask >>> lane) & 1L)
          }
        else
          foreachActive { lane =>
            results(lane) = kernel(first(lane), second(lane), third(lane), (mask >>> lane) & 1L)
          }
        NextStep
      }
      lanes(half)
    }

    // Both halves read their sources before either writes its destination.
    inline def dualSecond(half: DualVector, other: DualVector)(
        inline kernel: (Long, Long, Long, Long) => Long
    ): Either[Fault, Int] = {
      def lanes(half: DualVector, other: DualVector): Either[Fault, Int] = {
        bindDual(half)
        val results = laneScratch()
        val offset  = WaveRun.this.lanes
        val mask    = dualCondition(half)
        if (half.operation.wide)
          foreachActive { lane =>
            results(offset + lane) =
              kernel(first.pair(lane), second.pair(lane), third.pair(lane), (mask >>> lane) & 1L)
          }
        else
          foreachActive { lane =>
            results(offset + lane) = kernel(first(lane), second(lane), third(lane), (mask >>> lane) & 1L)
          }
        foreachActive { lane =>
          writeDual(other, lane, results(lane))
          writeDual(half, lane, results(offset + lane))
        }
        NextStep
      }
      lanes(half, other)
    }

    private def bindDual(half: DualVector): Unit =
      if (half.operation.wide) bindPairs(half.left, half.right, half.addend)
      else bindDwords(half.left, half.right, half.addend)

    /** The lane mask a select reads: the SGPR a VOPD3 half names, or VCC. */
    private def dualCondition(half: DualVector): Long = half.condition.fold(carry.bits)(source(_, 0))

    private def writeDual(half: DualVector, lane: Lane, value: Long): Unit =
      if (half.operation.wide) {
        vector(half.dest)(lane) = Bits.low32(value).toLong
        vector(half.dest.offsetBy(1))(lane) = Bits.high32(value).toLong
      } else vector(half.dest)(lane) = value

    inline def block(dest: VectorRegisterIndex, width: Int, sources: Vec[(InstructionSource, Int)])(
        inline kernel: (IndexedSeq[Long], IndexedSeq[Long]) => IndexedSeq[Long]
    ): Either[Fault, Int] = {
      def lanes(dest: VectorRegisterIndex, width: Int, sources: Vec[(InstructionSource, Int)]): Either[Fault, Int] = {
        val rows    = Array.ofDim[Array[Long] | Null](sources.map(_._2).sum)
        val others  = Array.ofDim[InstructionSource | Null](rows.length)
        val targets = Array.tabulate(width)(offset => vector(dest.offsetBy(offset.unsafe[Int >= 0])))
        var filled  = 0
        sources.foreach {
          case (InstructionSource.VectorRegister(start), count) =>
            (0 until count).foreach { offset =>
              rows(filled) = vector(start.offsetBy(offset.unsafe[Int >= 0]))
              filled += 1
            }
          case (other, _) =>
            others(filled) = other
            filled += 1
        }
        val inputCount = filled
        val inputs     = Array.ofDim[Long](inputCount)
        val old        = Array.ofDim[Long](width)
        val inputSeq   = ArraySeq.unsafeWrapArray(inputs)
        val oldSeq     = ArraySeq.unsafeWrapArray(old)
        foreachActive { lane =>
          var input = 0
          while (input < inputCount) {
            val row = rows(input)
            inputs(input) =
              if (row != null) Bits.low32(row(lane)).toLong else Bits.low32(source(others(input).nn, lane)).toLong
            input += 1
          }
          var offset = 0
          while (offset < width) {
            old(offset) = targets(offset)(lane)
            offset += 1
          }
          val results = kernel(inputSeq, oldSeq)
          offset = 0
          while (offset < results.size) {
            targets(offset)(lane) = Bits.low32(results(offset)).toLong
            offset += 1
          }
        }
        NextStep
      }
      lanes(dest, width, sources)
    }

    inline def accumulating(
        dest: VectorRegisterIndex,
        a: InstructionSource,
        b: InstructionSource,
        c: InstructionSource
    )(inline kernel: (Long, Long, Long, Long) => Long): Either[Fault, Int] = {
      def lanes(target: Array[Long], a: InstructionSource, b: InstructionSource, c: InstructionSource)
          : Either[Fault, Int] = {
        bindDwords(a, b, c)
        foreachActive { lane =>
          target(lane) = kernel(
            Bits.low32(first(lane)).toLong,
            Bits.low32(second(lane)).toLong,
            Bits.low32(third(lane)).toLong,
            target(lane)
          )
        }
        NextStep
      }
      lanes(vector(dest), a, b, c)
    }

    inline def packedSingle(instruction: Instruction.VPackedF32)(
        inline kernel: (Long, Long, Long) => Long
    ): Either[Fault, Int] = {
      def lanes(instruction: Instruction.VPackedF32): Either[Fault, Int] = {
        first.bindPair(instruction.srcA, PairRead.Packed)
        second.bindPair(instruction.srcB, PairRead.Packed)
        third.bindPair(instruction.srcC, PairRead.Packed)
        val low           = vector(instruction.dest)
        val high          = vector(instruction.dest.offsetBy(1))
        val lowSelection  = instruction.lowSelection
        val highSelection = instruction.highSelection
        val negateLow     = instruction.negateLow
        val negateHigh    = instruction.negateHigh
        foreachActive { lane =>
          val a                                           = first.pair(lane)
          val b                                           = second.pair(lane)
          val c                                           = third.pair(lane)
          def selected(selection: Int, negate: Int): Long =
            OperandSelection.packedTernary(Execute, a, b, c, selection, negate, width = 32)(kernel)
          low(lane) = Bits.low32(selected(lowSelection, negateLow)).toLong
          high(lane) = Bits.low32(selected(highSelection, negateHigh)).toLong
        }
        NextStep
      }
      lanes(instruction)
    }

    inline def packedHalf(instruction: Instruction.VPackedTernary)(
        inline kernel: (Long, Long, Long) => Long
    ): Either[Fault, Int] = {
      def lanes(instruction: Instruction.VPackedTernary): Either[Fault, Int] = {
        bindDwords(instruction.srcA, instruction.srcB, instruction.srcC)
        val target        = vector(instruction.dest)
        val lowSelection  = instruction.lowSelection
        val highSelection = instruction.highSelection
        val negateLow     = instruction.negateLow
        val negateHigh    = instruction.negateHigh
        foreachActive { lane =>
          val a                                           = Bits.low32(first(lane)).toLong
          val b                                           = Bits.low32(second(lane)).toLong
          val c                                           = Bits.low32(third(lane)).toLong
          def selected(selection: Int, negate: Int): Long =
            OperandSelection.packedTernary(Execute, a, b, c, selection, negate, width = 16)(kernel)
          target(lane) = selected(lowSelection, negateLow) | (selected(highSelection, negateHigh) << 16)
        }
        NextStep
      }
      lanes(instruction)
    }

    inline def halves(instruction: Instruction.VPackB32F16)(inline kernel: (Long, Long) => Long): Either[Fault, Int] = {
      def lanes(instruction: Instruction.VPackB32F16): Either[Fault, Int] = {
        first.bind(instruction.srcA)
        second.bind(instruction.srcB)
        val target    = vector(instruction.dest)
        val modifiers = instruction.modifiers
        val selection = instruction.opSelection
        foreachActive { lane =>
          target(lane) = kernel(
            modifiers(first(lane) >>> (if ((selection & 1) != 0) 16 else 0), 0, 16),
            modifiers(second(lane) >>> (if ((selection & 2) != 0) 16 else 0), 1, 16)
          )
        }
        NextStep
      }
      lanes(instruction)
    }

    inline def pairBinary(
        dest: VectorRegisterIndex,
        a: InstructionSource,
        b: InstructionSource,
        read: PairRead,
        modifiers: SourceModifiers
    )(inline kernel: (Long, Long) => Long): Either[Fault, Int] = {
      def lanes(
          dest: VectorRegisterIndex,
          a: InstructionSource,
          b: InstructionSource,
          read: PairRead,
          modifiers: SourceModifiers
      ): Either[Fault, Int] = {
        first.bindPair(a, read)
        second.bindPair(b, read)
        val low  = vector(dest)
        val high = vector(dest.offsetBy(1))
        foreachActive { lane =>
          val result = kernel(modifiers(first.pair(lane), 0, 64), modifiers(second.pair(lane), 1, 64))
          low(lane) = Bits.low32(result).toLong
          high(lane) = Bits.high32(result).toLong
        }
        NextStep
      }
      lanes(dest, a, b, read = read, modifiers = modifiers)
    }

    inline def pairDword(
        dest: VectorRegisterIndex,
        a: InstructionSource,
        b: InstructionSource,
        read: PairRead,
        modifiers: SourceModifiers
    )(inline kernel: (Long, Long) => Long): Either[Fault, Int] = {
      def lanes(
          dest: VectorRegisterIndex,
          a: InstructionSource,
          b: InstructionSource,
          read: PairRead,
          modifiers: SourceModifiers
      ): Either[Fault, Int] = {
        first.bindPair(a, read)
        second.bind(b)
        val low  = vector(dest)
        val high = vector(dest.offsetBy(1))
        foreachActive { lane =>
          val result = kernel(modifiers(first.pair(lane), 0, 64), second(lane))
          low(lane) = Bits.low32(result).toLong
          high(lane) = Bits.high32(result).toLong
        }
        NextStep
      }
      lanes(dest, a, b, read = read, modifiers = modifiers)
    }

    inline def pairTernary(
        dest: VectorRegisterIndex,
        a: InstructionSource,
        b: InstructionSource,
        c: InstructionSource,
        modifiers: SourceModifiers
    )(inline kernel: (Long, Long, Long) => Long): Either[Fault, Int] = {
      def lanes(
          dest: VectorRegisterIndex,
          a: InstructionSource,
          b: InstructionSource,
          c: InstructionSource,
          modifiers: SourceModifiers
      ): Either[Fault, Int] = {
        bindPairs(a, b, c)
        val low  = vector(dest)
        val high = vector(dest.offsetBy(1))
        foreachActive { lane =>
          val result = kernel(
            modifiers(first.pair(lane), 0, 64),
            modifiers(second.pair(lane), 1, 64),
            modifiers(third.pair(lane), 2, 64)
          )
          low(lane) = Bits.low32(result).toLong
          high(lane) = Bits.high32(result).toLong
        }
        NextStep
      }
      lanes(dest, a, b, c, modifiers = modifiers)
    }

    inline def pairShiftAdd(
        dest: VectorRegisterIndex,
        a: InstructionSource,
        b: InstructionSource,
        c: InstructionSource
    )(inline kernel: (Long, Long, Long) => Long): Either[Fault, Int] = {
      def lanes(dest: VectorRegisterIndex, a: InstructionSource, b: InstructionSource, c: InstructionSource)
          : Either[Fault, Int] = {
        first.bindPair(a, PairRead.Integer)
        second.bind(b)
        third.bindPair(c, PairRead.Integer)
        val low  = vector(dest)
        val high = vector(dest.offsetBy(1))
        foreachActive { lane =>
          val result = kernel(first.pair(lane), second(lane), third.pair(lane))
          low(lane) = Bits.low32(result).toLong
          high(lane) = Bits.high32(result).toLong
        }
        NextStep
      }
      lanes(dest, a, b, c)
    }

    inline def windowed(dest: VectorRegisterIndex, a: InstructionSource, b: InstructionSource, c: InstructionSource)(
        inline kernel: (Long, Long, Long) => Long
    ): Either[Fault, Int] = {
      def lanes(target: Array[Long], a: InstructionSource, b: InstructionSource, c: InstructionSource)
          : Either[Fault, Int] = {
        first.bindPair(a, PairRead.Integer)
        second.bind(b)
        third.bind(c)
        foreachActive { lane =>
          target(lane) = Bits.low32(kernel(first.pair(lane), Bits.low32(second(lane)).toLong, third(lane))).toLong
        }
        NextStep
      }
      lanes(vector(dest), a, b, c)
    }

    // The source is read before any lane writes, as the destination may be the source.
    inline def permuteLanes(instruction: Instruction.VPermuteLanes)(inline origin: (Long, Long, Long) => Long)(
        inline kernel: Long => Long
    ): Either[Fault, Int] = {
      def lanes(instruction: Instruction.VPermuteLanes): Either[Fault, Int] = instruction.src match {
        case InstructionSource.VectorRegister(src) =>
          val count     = WaveRun.this.lanes
          val registers = vector(instruction.dest)
          val scratch   = laneScratch()
          Array.copy(vector(src), 0, scratch, 0, count)
          // a VGPR selector, which the variable forms read per lane, may be the destination
          first.bind(instruction.low)
          second.bind(instruction.high)
          foreachActive { lane =>
            scratch(count + lane) =
              origin(lane.toLong, Bits.low32(first(lane)).toLong, Bits.low32(second(lane)).toLong)
          }
          foreachActive { lane =>
            val from = scratch(count + lane)
            if (readable(from, instruction.fetchInactive)) registers(lane) = kernel(scratch(from.toInt))
            else if (instruction.boundControl) registers(lane) = 0L
          }
          NextStep
        case other => Left(Fault.Backend(s"v_permlane requires a vector-register source, found $other"))
      }
      lanes(instruction)
    }

    inline def swapLanes(instruction: Instruction.VPermlaneSwap)(inline kernel: Long => Long): Either[Fault, Int] = {
      def lanes(instruction: Instruction.VPermlaneSwap): Either[Fault, Int] = {
        val count   = WaveRun.this.lanes.raw
        val targets = vector(instruction.dest)
        val sources = vector(instruction.src)
        val scratch = laneScratch()
        Array.copy(targets, 0, scratch, 0, count)
        Array.copy(sources, 0, scratch, count, count)
        foreachActive { lane =>
          val from      = (lane ^ instruction.rows).toLong
          val low       = (lane & instruction.rows) == 0
          val registers = if (low) sources else targets
          if (readable(from, instruction.fetchInactive))
            registers(lane) = kernel(scratch((if (low) 0 else count) + from.toInt))
          else if (instruction.boundControl) registers(lane) = 0L
        }
        NextStep
      }
      lanes(instruction)
    }

    inline def multiplyAdd64(
        dest: VectorRegisterIndex,
        carryDest: ScalarRegisterIndex,
        a: InstructionSource,
        b: InstructionSource,
        c: InstructionSource
    )(inline result: (Long, Long, Long, Long) => Long, inline flag: (Long, Long, Long, Long, Long) => Long)
        : Either[Fault, Int] = {
      def lanes(
          dest: VectorRegisterIndex,
          carryDest: ScalarRegisterIndex,
          a: InstructionSource,
          b: InstructionSource,
          c: InstructionSource
      ): Either[Fault, Int] = {
        first.bind(a)
        second.bind(b)
        third.bindPair(c, PairRead.Integer)
        conditionMask = 0L
        val low  = vector(dest)
        val high = vector(dest.offsetBy(1))
        foreachActive { lane =>
          val x     = first(lane)
          val y     = second(lane)
          val z     = third.pair(lane)
          val value = result(x, y, z, 0L)
          low(lane) = Bits.low32(value).toLong
          high(lane) = Bits.high32(value).toLong
          if (flag(x, y, z, 0L, value) != 0L) conditionMask |= 1L << lane
        }
        updateLaneMask(carryDest, conditionMask)
        NextStep
      }
      lanes(dest, carryDest, a, b, c = c)
    }

    inline def scaled(
        dest: VectorRegisterIndex,
        a: InstructionSource,
        b: InstructionSource,
        c: InstructionSource,
        modifiers: SourceModifiers
    )(inline result: (Long, Long, Long, Long) => Long): Either[Fault, Int] = {
      def lanes(
          target: Array[Long],
          a: InstructionSource,
          b: InstructionSource,
          c: InstructionSource,
          modifiers: SourceModifiers
      ): Either[Fault, Int] = {
        bindDwords(a, b, c)
        foreachActive { lane =>
          target(lane) = Bits.low32(result(
            Bits.low32(modifiers(first(lane), 0, 32)).toLong,
            Bits.low32(modifiers(second(lane), 1, 32)).toLong,
            Bits.low32(modifiers(third(lane), 2, 32)).toLong,
            carryBit(lane)
          )).toLong
        }
        NextStep
      }
      lanes(vector(dest), a, b, c, modifiers = modifiers)
    }

    inline def scaledFlagged(
        dest: VectorRegisterIndex,
        conditionDest: ScalarRegisterIndex,
        a: InstructionSource,
        b: InstructionSource,
        c: InstructionSource,
        modifiers: SourceModifiers
    )(inline result: (Long, Long, Long, Long) => Long, inline flag: (Long, Long, Long, Long, Long) => Long)
        : Either[Fault, Int] = {
      def lanes(
          target: Array[Long],
          conditionDest: ScalarRegisterIndex,
          a: InstructionSource,
          b: InstructionSource,
          c: InstructionSource,
          modifiers: SourceModifiers
      ): Either[Fault, Int] = {
        bindDwords(a, b, c)
        conditionMask = 0L
        foreachActive { lane =>
          val x     = Bits.low32(modifiers(first(lane), 0, 32)).toLong
          val y     = Bits.low32(modifiers(second(lane), 1, 32)).toLong
          val z     = Bits.low32(modifiers(third(lane), 2, 32)).toLong
          val s     = carryBit(lane)
          val value = result(x, y, z, s)
          target(lane) = Bits.low32(value).toLong
          if (flag(x, y, z, s, value) != 0L) conditionMask |= 1L << lane
        }
        writeConditionMask(conditionDest)
      }
      lanes(vector(dest), conditionDest, a, b, c = c, modifiers = modifiers)
    }

    inline def pairScaled(
        dest: VectorRegisterIndex,
        a: InstructionSource,
        b: InstructionSource,
        c: InstructionSource,
        modifiers: SourceModifiers
    )(inline result: (Long, Long, Long, Long) => Long): Either[Fault, Int] = {
      def lanes(
          dest: VectorRegisterIndex,
          a: InstructionSource,
          b: InstructionSource,
          c: InstructionSource,
          modifiers: SourceModifiers
      ): Either[Fault, Int] = {
        bindPairs(a, b, c)
        val low  = vector(dest)
        val high = vector(dest.offsetBy(1))
        foreachActive { lane =>
          val value = result(
            modifiers(first.pair(lane), 0, 64),
            modifiers(second.pair(lane), 1, 64),
            modifiers(third.pair(lane), 2, 64),
            carryBit(lane)
          )
          low(lane) = Bits.low32(value).toLong
          high(lane) = Bits.high32(value).toLong
        }
        NextStep
      }
      lanes(dest, a, b, c, modifiers = modifiers)
    }

    inline def pairScaledFlagged(
        dest: VectorRegisterIndex,
        conditionDest: ScalarRegisterIndex,
        a: InstructionSource,
        b: InstructionSource,
        c: InstructionSource,
        modifiers: SourceModifiers
    )(inline result: (Long, Long, Long, Long) => Long, inline flag: (Long, Long, Long, Long, Long) => Long)
        : Either[Fault, Int] = {
      def lanes(
          dest: VectorRegisterIndex,
          conditionDest: ScalarRegisterIndex,
          a: InstructionSource,
          b: InstructionSource,
          c: InstructionSource,
          modifiers: SourceModifiers
      ): Either[Fault, Int] = {
        bindPairs(a, b, c)
        conditionMask = 0L
        val low  = vector(dest)
        val high = vector(dest.offsetBy(1))
        foreachActive { lane =>
          val x     = modifiers(first.pair(lane), 0, 64)
          val y     = modifiers(second.pair(lane), 1, 64)
          val z     = modifiers(third.pair(lane), 2, 64)
          val s     = carryBit(lane)
          val value = result(x, y, z, s)
          low(lane) = Bits.low32(value).toLong
          high(lane) = Bits.high32(value).toLong
          if (flag(x, y, z, s, value) != 0L) conditionMask |= 1L << lane
        }
        writeConditionMask(conditionDest)
      }
      lanes(dest, conditionDest, a, b, c = c, modifiers = modifiers)
    }

    private inline def compareDwords(selection: Option[SubDwordSources], inline holds: (Long, Long) => Boolean): Unit =
      selection match {
        case Some(sources) =>
          foreachActive { lane =>
            if (
              holds(
                Execute.selectedPart(first(lane), sources.selectionA, sources.signExtendA),
                Execute.selectedPart(second(lane), sources.selectionB, sources.signExtendB)
              )
            ) conditionMask |= 1L << lane
          }
        case None =>
          foreachActive(lane => if (holds(first(lane), second(lane))) conditionMask |= 1L << lane)
      }

    inline def compare(
        dest: ScalarRegisterIndex,
        a: InstructionSource,
        b: InstructionSource,
        selection: Option[SubDwordSources],
        updateExec: Boolean
    )(inline kernel: (Long, Long) => Long): Either[Fault, Int] = {
      def lanes(
          dest: ScalarRegisterIndex,
          a: InstructionSource,
          b: InstructionSource,
          selection: Option[SubDwordSources],
          updateExec: Boolean
      ): Either[Fault, Int] = {
        first.bind(a)
        second.bind(b)
        conditionMask = 0L
        compareDwords(selection, (left, right) => kernel(left, right) != 0L)
        finishCompare(dest, updateExec)
      }
      lanes(dest, a, b, selection = selection, updateExec = updateExec)
    }

    inline def compareModified(
        dest: ScalarRegisterIndex,
        a: InstructionSource,
        b: InstructionSource,
        modifiers: SourceModifiers,
        selection: Option[SubDwordSources],
        updateExec: Boolean,
        width: Int in (16, 32, 64)
    )(inline kernel: (Long, Long) => Long): Either[Fault, Int] = {
      def lanes(
          dest: ScalarRegisterIndex,
          a: InstructionSource,
          b: InstructionSource,
          modifiers: SourceModifiers,
          selection: Option[SubDwordSources],
          updateExec: Boolean,
          width: Int in (16, 32, 64)
      ): Either[Fault, Int] = {
        first.bind(a)
        second.bind(b)
        conditionMask = 0L
        compareDwords(
          selection,
          (left, right) => kernel(modifiers(left, 0, width), modifiers(right, 1, width)) != 0L
        )
        finishCompare(dest, updateExec)
      }
      lanes(dest, a, b, modifiers = modifiers, selection = selection, updateExec = updateExec, width = width)
    }

    inline def compareClass(
        dest: ScalarRegisterIndex,
        a: InstructionSource,
        b: InstructionSource,
        modifiers: SourceModifiers,
        selection: Option[SubDwordSources],
        updateExec: Boolean
    )(inline kernel: (Long, Long) => Long): Either[Fault, Int] = {
      def lanes(
          dest: ScalarRegisterIndex,
          a: InstructionSource,
          b: InstructionSource,
          modifiers: SourceModifiers,
          selection: Option[SubDwordSources],
          updateExec: Boolean
      ): Either[Fault, Int] = {
        first.bind(a)
        second.bind(b)
        conditionMask = 0L
        selection match {
          case Some(sources) =>
            foreachActive { lane =>
              val value = modifiers(
                Execute.selectedPart(first(lane), sources.selectionA, sources.signExtendA),
                0,
                32
              )
              if (kernel(value, second(lane)) != 0L) conditionMask |= 1L << lane
            }
          case None =>
            foreachActive { lane =>
              if (kernel(modifiers(first(lane), 0, 32), second(lane)) != 0L) conditionMask |= 1L << lane
            }
        }
        finishCompare(dest, updateExec)
      }
      lanes(dest, a, b, modifiers = modifiers, selection = selection, updateExec = updateExec)
    }

    inline def comparePair(
        dest: ScalarRegisterIndex,
        a: InstructionSource,
        b: InstructionSource,
        read: PairRead,
        modifiers: SourceModifiers,
        updateExec: Boolean
    )(inline kernel: (Long, Long) => Long): Either[Fault, Int] = {
      def lanes(
          dest: ScalarRegisterIndex,
          a: InstructionSource,
          b: InstructionSource,
          read: PairRead,
          modifiers: SourceModifiers,
          updateExec: Boolean
      ): Either[Fault, Int] = {
        first.bindPair(a, read)
        second.bindPair(b, read)
        conditionMask = 0L
        foreachActive { lane =>
          if (kernel(modifiers(first.pair(lane), 0, 64), modifiers(second.pair(lane), 1, 64)) != 0L)
            conditionMask |= 1L << lane
        }
        finishCompare(dest, updateExec)
      }
      lanes(dest, a, b, read = read, modifiers = modifiers, updateExec = updateExec)
    }

    inline def comparePairClass(
        dest: ScalarRegisterIndex,
        a: InstructionSource,
        b: InstructionSource,
        modifiers: SourceModifiers,
        updateExec: Boolean
    )(inline kernel: (Long, Long) => Long): Either[Fault, Int] = {
      def lanes(
          dest: ScalarRegisterIndex,
          a: InstructionSource,
          b: InstructionSource,
          modifiers: SourceModifiers,
          updateExec: Boolean
      ): Either[Fault, Int] = {
        first.bindPair(a, PairRead.Floating)
        second.bind(b)
        conditionMask = 0L
        foreachActive { lane =>
          if (kernel(modifiers(first.pair(lane), 0, 64), second(lane)) != 0L) conditionMask |= 1L << lane
        }
        finishCompare(dest, updateExec)
      }
      lanes(dest, a, b, modifiers = modifiers, updateExec = updateExec)
    }

    /** Rewrites the active lanes' `width`-bit result in `dest`; a binary16 result keeps the register's high half. */
    private inline def rewriteResult(dest: VectorRegisterIndex, width: Int in (16, 32, 64))(
        inline kernel: Long => Long
    ): Unit = width.raw match {
      case 64 =>
        val low  = vector(dest)
        val high = vector(dest.offsetBy(1))
        foreachActive { lane =>
          val result = kernel(Bits.join32(low(lane), high(lane)))
          low(lane) = Bits.low32(result).toLong
          high(lane) = Bits.high32(result).toLong
        }
      case 32 =>
        val registers = vector(dest)
        foreachActive(lane => registers(lane) = kernel(registers(lane)))
      case _ =>
        val registers = vector(dest)
        foreachActive { lane =>
          val bits = registers(lane)
          registers(lane) = (bits & ~0xffffL) | Bits.low16(kernel(bits)).toLong
        }
    }

    inline def outputModified(instruction: Instruction.VOutputModified)(
        inline kernel: Long => Long
    ): Either[Fault, Int] = {
      def lanes(instruction: Instruction.VOutputModified): Either[Fault, Int] =
        if (ModeRegister.outputModifierDisabled(mode, instruction.width)) stepStaged(instruction.ignored)
        else {
          val next = stepStaged(instruction.applied)
          if (next.isRight) rewriteResult(instruction.dest, instruction.width)(kernel)
          next
        }
      lanes(instruction)
    }

    inline def clampResult(instruction: Instruction.VClampResult)(inline kernel: Long => Long): Either[Fault, Int] = {
      def lanes(instruction: Instruction.VClampResult): Either[Fault, Int] = {
        val next = stepStaged(instruction.operation)
        if (next.isRight) rewriteResult(instruction.dest, instruction.width)(kernel)
        next
      }
      lanes(instruction)
    }

    inline def halfResult(instruction: Instruction.VHalfResult)(
        inline kernel: (Long, Long) => Long
    ): Either[Fault, Int] = {
      def lanes(instruction: Instruction.VHalfResult): Either[Fault, Int] = {
        val registers = vector(instruction.dest)
        foreachActive(lane => preservedHalves(lane) = registers(lane))
        val next = stepStaged(instruction.operation)
        if (next.isRight)
          foreachActive(lane => registers(lane) = kernel(preservedHalves(lane), registers(lane)))
        next
      }
      lanes(instruction)
    }

    inline def placedResult(instruction: Instruction.VSubDwordResult)(
        inline kernel: (Long, Long) => Long
    ): Either[Fault, Int] = {
      def lanes(instruction: Instruction.VSubDwordResult): Either[Fault, Int] = {
        val registers = instruction.relative match {
          case Some(field) => relativeWritable(instruction.dest, field)
          case None        => vector(instruction.dest)
        }
        if (registers != null) foreachActive(lane => preservedHalves(lane) = registers(lane))
        val next = stepStaged(instruction.operation)
        if (next.isRight && registers != null)
          foreachActive(lane => registers(lane) = kernel(preservedHalves(lane), registers(lane)))
        next
      }
      lanes(instruction)
    }

    // An unreadable source reads zero under BOUND_CTRL and leaves the lane disabled otherwise. Disabled lanes take no
    // part, as inactive lanes do, so a compare-and-update leaves EXEC holding only enabled lanes.
    inline def dpp16(instruction: Instruction.VDPP, rowMask: Int, bankMask: Int, boundControl: Boolean)(
        inline origin: Long => Long
    )(inline kernel: Long => Long): Either[Fault, Int] = {
      def lanes(instruction: Instruction.VDPP, rowMask: Int, bankMask: Int, boundControl: Boolean)
          : Either[Fault, Int] = {
        val values        = routedRegister(instruction)
        val wide          = instruction.wide
        val highs         = if (wide) vector(instruction.src.offsetBy(1)) else values
        val executing     = active.bits
        val fetchInactive = instruction.fetchInactive
        var enabled       = 0L
        foreachActive { lane =>
          if (dppLaneEnabled(lane, rowMask, bankMask)) {
            val from = origin(lane.toLong)
            if (readable(from, fetchInactive)) {
              val low = kernel(values(from.toInt))
              routed(lane) = low
              routedHigh(lane) = if (wide) kernel(highs(from.toInt)) else low
              enabled |= 1L << lane
            } else if (boundControl) {
              routed(lane) = 0L
              routedHigh(lane) = 0L
              enabled |= 1L << lane
            }
          }
        }
        active.bits = enabled
        val next = stepStaged(instruction.operation)
        if (!updatesExec(instruction.operation)) active.bits = executing
        next
      }
      lanes(instruction, rowMask, bankMask, boundControl = boundControl)
    }

    inline def dpp8(instruction: Instruction.VDPP)(inline origin: Long => Long)(
        inline kernel: Long => Long
    ): Either[Fault, Int] = {
      def lanes(instruction: Instruction.VDPP): Either[Fault, Int] = {
        val values        = routedRegister(instruction)
        val fetchInactive = instruction.fetchInactive
        foreachActive { lane =>
          val from = origin(lane.toLong)
          routed(lane) = if (readable(from, fetchInactive)) kernel(values(from.toInt)) else 0L
        }
        stepStaged(instruction.operation)
      }
      lanes(instruction)
    }

    // Scatter: EXEC gates source lanes, so a source may write an inactive destination lane; lanes receiving nothing
    // keep their value. Operands are snapshotted because the destination may alias them.
    inline def permute(
        dest: VectorRegisterIndex,
        address: VectorRegisterIndex,
        src: VectorRegisterIndex,
        offset: Int,
        bank: RegisterBank
    )(inline destination: (Long, Long, Long) => Long)(inline kernel: Long => Long): Either[Fault, Int] = {
      def lanes(
          dest: VectorRegisterIndex,
          address: VectorRegisterIndex,
          src: VectorRegisterIndex,
          file: VectorRegisters
      ): Either[Fault, Int] = {
        val addresses = vector(address).clone()
        val sources   = file(src).clone()
        val result    = file(dest).clone()
        foreachActive { lane =>
          val target = destination(addresses(lane), lane.toLong, waveSize.toLong)
          if (target < WaveRun.this.lanes.toLong) result(target.toInt) = kernel(sources(lane))
        }
        val registers = file(dest)
        foreachLane(lane => registers(lane) = result(lane))
        NextStep
      }
      lanes(dest, address, src, registerFile(bank))
    }

    private def routedRegister(instruction: Instruction.VDPP): Array[Long] = instruction.relative match {
      case Some(field) => relativeRead(instruction.src, field)
      case None        => registerFile(instruction.bank)(instruction.src)
    }

    private def registerFile(bank: RegisterBank): VectorRegisters =
      if (bank == RegisterBank.Accumulator) accumulator else vector

    inline def backwardPermute(
        dest: VectorRegisterIndex,
        address: VectorRegisterIndex,
        src: VectorRegisterIndex,
        offset: Int,
        bank: RegisterBank,
        fetchInactive: Boolean
    )(inline origin: (Long, Long) => Long)(inline kernel: Long => Long): Either[Fault, Int] = {
      def lanes(
          dest: VectorRegisterIndex,
          address: VectorRegisterIndex,
          src: VectorRegisterIndex,
          file: VectorRegisters
      ): Either[Fault, Int] = {
        val addresses = vector(address).clone()
        val sources   = file(src).clone()
        val registers = file(dest)
        val enabled   = if (fetchInactive) -1L else active.bits
        // a source lane EXEC disables reads as 0
        foreachActive { lane =>
          val from = origin(addresses(lane), lane.toLong)
          registers(lane) =
            if (from < WaveRun.this.lanes.toLong && ((enabled >>> from) & 1L) != 0L) kernel(sources(from.toInt))
            else 0L
        }
        NextStep
      }
      lanes(dest, address, src, registerFile(bank))
    }

    // The source may be the destination; it is a VGPR whichever bank the destination is in.
    inline def swizzle(
        dest: VectorRegisterIndex,
        src: VectorRegisterIndex,
        bank: RegisterBank
    )(
        inline origin: Long => Long
    )(
        inline kernel: Long => Long
    ): Either[Fault, Int] = {
      def lanes(dest: VectorRegisterIndex, src: VectorRegisterIndex, file: VectorRegisters): Either[Fault, Int] = {
        val sources = vector(src).clone()

        val registers = file(dest)
        foreachActive { lane =>
          val from = origin(lane.toLong)
          registers(lane) =
            if (from >= 0L && from < WaveRun.this.lanes.toLong) kernel(sources(from.toInt)) else 0L
        }
        NextStep
      }
      lanes(dest, src, registerFile(bank))
    }

    /** The `count` registers of a matrix operand from its first in the vector or accumulator bank, or null for a
      * constant.
      */
    private def matrixRegisters(value: InstructionSource, banked: Boolean, count: Int): Array[Array[Long]] | Null =
      value match {
        case InstructionSource.VectorRegister(base) =>
          val bank = if (banked) accumulator else vector
          Array.tabulate(count)(offset => bank(base.offsetBy(offset.unsafe[Int >= 0])))
        case _ => null
      }

    /** `width` bits from bit `bit` of the registers `lane` holds, or `constant`, every element of a constant operand.
      */
    private def matrixBits(registers: Array[Array[Long]] | Null, constant: Long, lane: Int, bit: Int, width: Int)
        : Long = {
      val value = registers match {
        case null                     => constant
        case held: Array[Array[Long]] =>
          val low = Bits.low32(held(bit >>> 5)(lane)).toLong >>> (bit & 31)
          if ((bit & 31) + width > 32) low | (Bits.low32(held((bit >>> 5) + 1)(lane)).toLong << (32 - (bit & 31)))
          else low
      }
      if (width == 64) value else value & ((1L << width) - 1L)
    }

    /** An element of A (`operand` 0) or B (1) with the negation its register half takes. */
    private def matrixInput(controls: MatrixControls, bits: Long, bit: Int, width: Int, operand: Int): Long = {
      val negate = if (width == 16 && (bit & 16) != 0) controls.negateHigh else controls.negateLow
      if ((negate & (1 << operand)) != 0) bits ^ (1L << (width - 1)) else bits
    }

    /** An element of C under its absolute value and negation. */
    private def matrixAddend(controls: MatrixControls, bits: Long, width: Int): Long = {
      val sign      = 1L << (width - 1)
      val magnitude = if ((controls.negateHigh & 4) != 0) bits & ~sign else bits
      if ((controls.negateLow & 4) != 0) magnitude ^ sign else magnitude
    }

    /** The E8M0 scale byte `selection` of `value` as `lane` holds it; a float constant gives its exponent. */
    private def matrixScale(value: InstructionSource, selection: Int, lane: Int): Long = value match {
      case InstructionSource.Floating(constant) => Minifloats.exponent(Float32(constant.toFloat)).toLong
      case other                                => (source(other, lane.unsafe[Int >= 0]) >>> (8 * selection)) & 0xffL
    }

    /** The elements of A and B each lane holds, one per item, after the instruction's negations, and the E8M0 scales of
      * A's rows and B's columns when it has them. A scale is read when first asked for, as the products that use it are
      * the only ones to name its lane.
      */
    private class MatrixElements(instruction: Instruction.VMatrix) {
      val shape           = instruction.shape
      val controls        = instruction.controls
      val banked: Boolean = (controls.banks & 4) != 0
      val a: Array[Long]  =
        elements(instruction.srcA, (controls.banks & 1) != 0, shape.aRegisters, shape.aItems, shape.a.width, 0)
      val b: Array[Long] =
        elements(instruction.srcB, (controls.banks & 2) != 0, shape.bRegisters, shape.bItems, shape.b.width, 1)
      val addends: Array[Array[Long]] | Null = matrixRegisters(
        if (shape.sparse) InstructionSource.VectorRegister(instruction.dest) else instruction.srcC,
        banked,
        shape.outputRegisters
      )
      val constant: Long                     = source(instruction.srcC, 0.unsafe[Int >= 0])
      val indices: Array[Array[Long]] | Null =
        if (shape.sparse) matrixRegisters(instruction.srcC, banked = false, 1) else null
      private val scalesA = Array.fill(if (controls.scales.isEmpty) 0 else shape.m * ((shape.k + 31) / 32))(-1L)
      private val scalesB = Array.fill(if (controls.scales.isEmpty) 0 else shape.n * ((shape.k + 31) / 32))(-1L)

      private def elements(
          value: InstructionSource,
          banked: Boolean,
          registers: Int,
          items: Int,
          width: Int,
          operand: Int
      ): Array[Long] = {
        val held      = matrixRegisters(value, banked, registers)
        val constant  = source(value, 0.unsafe[Int >= 0])
        val count     = WaveRun.this.lanes
        val extracted = Array.ofDim[Long](count * items)
        var lane      = 0
        while (lane < count) {
          var item = 0
          while (item < items) {
            val bit = item * width
            extracted(lane * items + item) =
              matrixInput(controls, matrixBits(held, constant, lane, bit, width), bit, width, operand)
            item += 1
          }
          lane += 1
        }
        extracted
      }

      def scaleA(row: Int, chunk: Int): Long = {
        val index = row + shape.m * chunk
        if (scalesA(index) < 0L) scalesA(index) = controls.scales.fold(0L)(s => matrixScale(s.a, s.selectionA, index))
        scalesA(index)
      }

      def scaleB(column: Int, chunk: Int): Long = {
        val index = column + shape.n * chunk
        if (scalesB(index) < 0L) scalesB(index) = controls.scales.fold(0L)(s => matrixScale(s.b, s.selectionB, index))
        scalesB(index)
      }

      /** C's element for item `item` of `lane`, under its absolute value and negation. */
      def addend(lane: Int, item: Int): Long =
        matrixAddend(
          controls,
          matrixBits(
            addends,
            constant,
            lane,
            shape.outputBit(item) + (if (controls.high) 16 else 0),
            shape.c.width
          ),
          shape.c.width
        )

      /** The sparse index, two bits, that picks the element of each group of four that item `item` of A holds. */
      def index(lane: Int, item: Int): Int =
        matrixBits(indices, 0L, lane, 2 * shape.aItems * controls.indexSet + 2 * item, 2).toInt
    }

    /** D, written from the elements `results` holds, one array per item. */
    private def writeMatrix(instruction: Instruction.VMatrix, results: Array[Array[Long]]): Unit = {
      val shape       = instruction.shape
      val controls    = instruction.controls
      val cWidth      = shape.c.width
      val destination = if ((controls.banks & 4) != 0) accumulator else vector
      var register    = 0
      while (register < shape.outputRegisters) {
        val target = destination(instruction.dest.offsetBy(register.unsafe[Int >= 0]))
        foreachActive { lane =>
          target(lane) =
            if (shape.halfRegisters)
              Execute.mergeHalf(target(lane), results(register)(lane), controls.high)
            else if (cWidth == 64) Bits.low32(results(register / 2)(lane) >>> (32 * (register % 2))).toLong
            else if (cWidth == 16)
              Bits.low16(results(2 * register)(lane)).toLong |
                (Bits.low16(results(2 * register + 1)(lane)).toLong << 16)
            else Bits.low32(results(register)(lane)).toLong
        }
        register += 1
      }
    }

    inline def multiplyMatrices(instruction: Instruction.VMatrix)(
        inline addend: Long => Long,
        inline inputA: Long => Long,
        inline inputB: Long => Long,
        inline step: (Long, Long, Long, Long, Long) => Long,
        inline result: Long => Long
    ): Either[Fault, Int] = {
      def lanes(instruction: Instruction.VMatrix): Either[Fault, Int] = {
        val shape = instruction.shape
        val count = WaveRun.this.lanes.raw
        val core  = shape.layout == MatrixLayout.Core
        if (count != waveSize.raw || shape.waveSize != count || (core && active.count != count))
          Left(Fault.InvalidRequest(if (core) "MFMA requires a complete active wave64"
          else "WMMA requires a complete wave"))
        else {
          val operands = MatrixElements(instruction)
          val controls = operands.controls
          // each element is converted once, not once per product it joins
          val left  = operands.a
          val right = operands.b
          var index = 0
          while (index < left.length) {
            left(index) = inputA(left(index))
            index += 1
          }
          index = 0
          while (index < right.length) {
            right(index) = inputB(right(index))
            index += 1
          }
          val (m, n, storedK, depth) = (shape.m, shape.n, shape.storedK, shape.k)
          val (aItems, bItems)       = (shape.aItems, shape.bItems)
          val items                  = shape.outputItems
          val (aLanes, aHeld)        = (shape.aLanes, shape.aHeld)
          val (bLanes, bHeld)        = (shape.bLanes, shape.bHeld)
          val outputs                = shape.outputElements
          val (sparse, replicated)   = (shape.sparse, shape.replicated)
          val scaled                 = controls.scales.nonEmpty
          val (broadcast, blockId)   = (controls.blockBroadcast, controls.blockId)
          val results                = Array.ofDim[Long](items, count)
          var lane                   = 0
          while (lane < count) {
            val group = if (replicated) lane / 16 * 16 else 0
            var item  = 0
            while (item < items) {
              val flat   = outputs(lane * items + item)
              val block  = flat / (m * n)
              val row    = flat / n % m
              val column = flat     % n
              val aBase  = (block * m + row) * storedK
              val aFrom  = (((block >> broadcast << broadcast) | blockId) * m + row) * storedK
              val bBase  = (block * n + column) * depth
              var sum    = addend(operands.addend(lane, item))
              var k      = 0
              while (k < storedK) {
                val aLane = aLanes(aFrom + k) + group
                val aItem = aHeld(aBase + k)
                val dense = if (sparse) 4 * (k / 2) + operands.index(aLane, aItem) else k
                val bLane = MatrixShape.laneGroup(bLanes(bBase + dense) + group, controls.laneGroups)
                sum = step(
                  sum,
                  left(aLane * aItems + aItem),
                  right(bLane * bItems + bHeld(bBase + dense)),
                  if (scaled) operands.scaleA(row, dense / 32) else 0L,
                  if (scaled) operands.scaleB(column, dense / 32) else 0L
                )
                k += 1
              }
              results(item)(lane) = result(sum)
              item += 1
            }
            lane += 1
          }
          writeMatrix(instruction, results)
          NextStep
        }
      }
      lanes(instruction)
    }

    inline def atomic(instruction: Instruction.Atomic): AtomicLanes = AtomicLanes(instruction)

    class AtomicLanes(instruction: Instruction.Atomic) extends Sink.Binary[Long, Either[Fault, Int]] {
      inline def apply(inline kernel: (Long, Long) => Long): Either[Fault, Int] = {
        def lanes(instruction: Instruction.Atomic): Either[Fault, Int] = {
          val file = registerFile(instruction.bank)
          foreachActiveUntilFault { lane =>
            val operand =
              if (instruction.bytes == 8) pair(file, instruction.src, lane) else file(instruction.src)(lane)
            atomicLane(
              instruction.address,
              instruction.dest,
              instruction.bytes,
              instruction.returnValue,
              lane,
              kernel(_, operand),
              file
            )
          }
        }
        lanes(instruction)
      }
    }

    inline def localSource2(instruction: Instruction.LocalSource2): Source2Lanes = Source2Lanes(instruction)

    class Source2Lanes(instruction: Instruction.LocalSource2) extends Sink.Binary[Long, Either[Fault, Int]] {
      inline def apply(inline kernel: (Long, Long) => Long): Either[Fault, Int] = {
        def lanes(instruction: Instruction.LocalSource2): Either[Fault, Int] = {
          val at = MemoryAddress.Local(instruction.address, 0)
          foreachActiveUntilFault { lane =>
            val address = Bits.low32(vector(instruction.address)(lane)).toLong
            located = Location.Local
            val read = readValue(
              Execute.source2Address(address, instruction.displacement),
              lane,
              instruction.bytes.widen[Int in 1 ~ 8],
              coherent = false
            )
            if (read != null) read
            else {
              val other = loaded.elem
              atomicLane(
                at,
                instruction.address,
                instruction.bytes,
                returnValue = false,
                lane,
                kernel(_, other),
                vector
              )
            }
          }
        }
        lanes(instruction)
      }
    }

    inline def compareSwap(instruction: Instruction.CompareSwap)(
        inline kernel: (Long, Long, Long) => Long
    ): Either[Fault, Int] = {
      def lanes(instruction: Instruction.CompareSwap): Either[Fault, Int] = {
        val bytes     = instruction.bytes
        val data      = instruction.data
        val comparand = instruction.comparand
        val file      = registerFile(instruction.bank)
        foreachActiveUntilFault { lane =>
          val compare =
            if (bytes == 8) pair(file, comparand, lane) else Bits.low32(file(comparand)(lane)).toLong
          val swap = if (bytes == 8) pair(file, data, lane) else Bits.low32(file(data)(lane)).toLong
          atomicLane(
            instruction.address,
            instruction.dest,
            bytes,
            instruction.returnValue,
            lane,
            kernel(_, compare, swap),
            file
          )
        }
      }
      lanes(instruction)
    }

    inline def scalarAtomic(instruction: Instruction.SAtomic): ScalarAtomicUpdate = ScalarAtomicUpdate(instruction)

    class ScalarAtomicUpdate(instruction: Instruction.SAtomic) extends Sink.Binary[Long, Either[Fault, Int]] {
      inline def apply(inline kernel: (Long, Long) => Long): Either[Fault, Int] = {
        def update(instruction: Instruction.SAtomic): Either[Fault, Int] = {
          val operand = scalarData(instruction.data, 0, instruction.bytes)
          scalarExchange(instruction.address, instruction.data, instruction.bytes, instruction.returnValue)(
            kernel(_, operand)
          )
        }
        update(instruction)
      }
    }

    inline def scalarCompareSwap(instruction: Instruction.SCompareSwap)(
        inline kernel: (Long, Long, Long) => Long
    ): Either[Fault, Int] = {
      def update(instruction: Instruction.SCompareSwap): Either[Fault, Int] = {
        val bytes     = instruction.bytes
        val swap      = scalarData(instruction.data, 0, bytes)
        val comparand = scalarData(instruction.data, bytes.raw / 4, bytes)
        scalarExchange(instruction.address, instruction.data, bytes, instruction.returnValue)(
          kernel(_, comparand, swap)
        )
      }
      update(instruction)
    }

    /** The dword or pair of the SGPRs from `first` plus `words`. */
    private def scalarData(first: ScalarRegisterIndex, words: Int, bytes: Int in (4, 8)): Long = {
      val register = InstructionSource.Scalar(first.offsetBy(words.unsafe[Int >= 0]))
      if (bytes == 8) sourceScalar64(register) else Bits.low32(source(register, 0)).toLong
    }

    // the elements of the last located scalar access in range
    private var scalarKept = 0

    /** The global address of a scalar access of `count` elements of `size` bytes, keeping in [[scalarKept]] how many
      * are in range: all of them, or for a buffer, as many as end within it.
      */
    private def scalarLocated(address: ScalarAddress, size: Int, count: Int): Long = {
      val added = address.scalarOffset.fold(0L)(register => scalar(register))
      if (!address.buffer) {
        scalarKept = count
        Execute.scalarDword(Execute.scalarAddress(pair(scalar, address.base), address.offset, added))
      } else {
        val first = address.base
        val start = Execute.scalarDword(Bits.low32(address.offset + added).toLong)
        scalarKept = toRaw[UInt[32]](
          BufferResource.scalarElements(
            generation,
            dword(first + 1),
            dword(first + 2),
            dword(first + 3),
            fromRaw[UInt[32]](start),
            size,
            count
          )
        ).toInt
        toRaw[UInt[64]](BufferResource.base(dword(first), dword(first + 1))) + start
      }
    }

    // a buffer atomic out of range leaves memory alone and returns zero
    private def scalarExchange(
        address: ScalarAddress,
        data: ScalarRegisterIndex,
        bytes: Int in (4, 8),
        returnValue: Boolean
    )(update: Long => Long): Either[Fault, Int] = {
      val at       = scalarLocated(address, bytes, 1)
      val previous = if (scalarKept == 0) Right(0L) else dispatchMemory.atomicModifyValue(at, bytes)(update)
      previous.map { old =>
        if (returnValue)
          if (bytes == 8) writeScalar64(data, old) else writeScalar32(data, old)
        Step.Next
      }
    }

    private def scalarStore(store: Instruction.SStore): Either[Fault, Int] = {
      val at = scalarLocated(store.address, 4, store.count)
      (0 until scalarKept).foldLeft(NextStep)((written, index) =>
        written.flatMap(_ =>
          dispatchMemory.writeValue(at + index * 4L, scalarData(store.src, index, 4), 4).map(_ => Step.Next)
        )
      )
    }

    private val observer: LaunchObserver | Null = control.observer.orNull
    // the ordinal of the issue the observer is being shown, taken once per instruction
    private var currentIssue: Long = 0L
    // why the wave is parked, when it is; a step that blocks it names the reason it waits for
    private var blockReason: MachineUnit.Reason | Null = null

    private object View extends WaveView {
      def lanes: Int in 1 ~ 64                                     = WaveRun.this.lanes
      def exec: Long                                               = active.bits
      def scalar(index: Int in 0 ~ 127): Long                      = WaveRun.this.scalar(index)
      def vector(index: Int in 0 ~ 255, lane: Int in 0 ~ 63): Long = WaveRun.this.vector(index)(lane)
      def unit: MachineUnit                                        = machineUnit(MachineUnit.State.Running)
      def issued: Long                                             = currentIssue
    }

    private def observe(issue: Long): Fault | Null = {
      currentIssue = issue
      observer.nn.instruction(location, View) match {
        case Left(fault) => fault
        case Right(())   => null
      }
    }

    /** The unit the wave runs as, which the timeline follows as it parks and finishes. */
    private def machineUnit(state: MachineUnit.State): MachineUnit =
      MachineUnit(group, waveIndex, lanes.widen[Int >= 0], state)

    /** A change in what a unit is doing, which the timeline records between the issues it surrounds. */
    private def transition(kind: MachineEvent.Kind, at: => Fault.Location): Unit =
      if (observer != null) {
        observer.nn.transition(MachineEvent(machineUnit(kind.state), kind, control.issued(), Some(at)))
      }

    /** The wave parks, waiting for `reason`; the machine releases it, and the wave runs again. */
    private def park(reason: MachineUnit.Reason): Unit = {
      blockReason = reason
      transition(MachineEvent.Kind.Blocked(reason), location)
    }

    /** The machine released the wave, which runs again. */
    private def resume(): Unit = {
      transition(MachineEvent.Kind.Resumed, location)
      blockReason = null
    }

    /** The name of the barrier `id` names: the two implicit workgroup barriers, or a named one. */
    private def barrierName(id: Int): String =
      if (id == -1 || id == -2) s"workgroup barrier ${-id - 1}" else s"barrier $id"

    /** A step that parks the wave on `reason`, which the machine releases. */
    private def blocked(reason: MachineUnit.Reason): Either[Fault, Int] = {
      blockReason = reason
      BlockedStep
    }

    // A wave executes its lanes together, so only a lone active lane identifies the faulting one.
    private def location: Fault.Location = Fault.Location(
      kernel.name,
      None,
      Some(instructionOffsets(instructionIndex).toLong.unsafe[Long >= 0]),
      Some(group),
      Option.when(java.lang.Long.bitCount(active.bits) == 1)(
        (localStart + java.lang.Long.numberOfTrailingZeros(active.bits)).unsafe[Int >= 0]
      )
    )

    def run(): Either[Fault, WaveBoundary] = {
      // A wave the machine released runs again; the timeline records the change before the wave's next issue.
      if (blockReason != null) resume()
      var boundary: WaveBoundary | Null = null
      var failure: Fault | Null         = null
      var steps                         = 0
      while (boundary == null && failure == null)
        control.checkpoint(math.max(1, active.count).unsafe[Int > 0]) match {
          case Left(fault) => failure = fault.at(location)
          case _           =>
            steps += 1
            // The launch's issue ordinal is its timeline's time axis; only an observed launch needs one.
            val issue   = if (observer == null) 0L else control.nextIssue()
            val stopped = if (observer == null) null else observe(issue)
            if (stopped != null) failure = stopped.at(location)
            else stepOrSkip(instructionIndex) match {
              case Left(Fault.Memory(message)) =>
                failure = Fault.Memory(f"AMDGPU memory access at pc 0x${programCounter(instructionIndex)}%X: $message")
                  .at(location)
              case Left(error)  => failure = error.at(location)
              case Right(value) =>
                // Matching the unboxed Int avoids boxed equality per instruction.
                val next: Int = value
                next match {
                  case Step.Next => instructionIndex += 1
                  case Step.Exit =>
                    transition(MachineEvent.Kind.Retired, location)
                    boundary = WaveBoundary.End
                  case Step.Arrive =>
                    park(MachineUnit.Reason.Barrier("workgroup barrier"))
                    instructionIndex += 1
                    boundary = WaveBoundary.Barrier
                  case Step.Blocked =>
                    park(blockReason.nn)
                    stalledRun = steps == 1
                    boundary = WaveBoundary.Suspended
                  case Step.Yield =>
                    park(MachineUnit.Reason.Members)
                    instructionIndex += 1
                    boundary = WaveBoundary.Suspended
                  case target =>
                    if (resident && target <= instructionIndex) {
                      backwardBranches += 1
                      if (backwardBranches == BackwardBranchesPerSlice) {
                        backwardBranches = 0
                        boundary = WaveBoundary.Suspended
                      }
                    }
                    instructionIndex = target
                }
            }
        }
      if (failure == null) Right(boundary.nn) else Left(failure)
    }
  }

  /** Row and bank masks gate destination writes; a bank is four consecutive lanes of a row. */
  def dppLaneEnabled(lane: Lane, rowMask: Int, bankMask: Int): Boolean =
    (rowMask & (1 << (lane / 16))) != 0 && (bankMask & (1 << ((lane >>> 2) & 3))) != 0

  private def pair(registers: Array[Long], first: ScalarRegisterIndex): Long =
    Bits.join32(registers(first), registers(first.offsetBy(1)))

  private def pair(registers: VectorRegisters, first: VectorRegisterIndex, lane: Int >= 0): Long =
    Bits.join32(registers(first)(lane), registers(first.offsetBy(1))(lane))

  private def setPair(registers: Array[Long], first: ScalarRegisterIndex, value: Long): Unit = {
    val high = first.offsetBy(1)
    registers(first) = Bits.low32(value).toLong
    registers(high) = Bits.high32(value).toLong
  }

  def effectiveGroupSegmentSize(fixed: Int, dynamic: Int): Int =
    Math.addExact(fixed, dynamic)

  def ldsOffset(address: Long): Long = Execute.ldsOffset(address)

  // A primitive sentinel keeps the per-lane LDS path allocation-free.
  private inline val InvalidLdsOffset = -1

  private def checkedLdsOffset(lds: Array[Byte], address: Long, size: Int): Int = {
    val offset = ldsOffset(address)
    if (ByteRange.contains(lds.length, offset, size.toLong)) offset.toInt else InvalidLdsOffset
  }

  /** The `size` bytes the LDS holds from `address`, which wraps at 32 bits; those past the workgroup's allocation read
    * as zero.
    */
  private def ldsBytes(lds: Array[Byte], address: Long, size: Int): Array[Byte] =
    Array.tabulate(size) { index =>
      val at = ldsOffset(address + index)
      if (at < lds.length) lds(at.toInt) else 0.toByte
    }

  /** Writes `bytes` to the LDS from `address`, which wraps at 32 bits, dropping those past the workgroup's allocation.
    */
  private def writeLDS(lds: Array[Byte], address: Long, bytes: Array[Byte]): Unit = {
    val offset = ldsOffset(address)
    if (ByteRange.contains(lds.length, offset, bytes.length.toLong))
      Array.copy(bytes, 0, lds, offset.toInt, bytes.length)
    else
      bytes.indices.foreach { index =>
        val at = ldsOffset(address + index)
        if (at < lds.length) lds(at.toInt) = bytes(index)
      }
  }

  private def vectorBytes(
      registers: VectorRegisters,
      first: VectorRegisterIndex,
      lane: Int >= 0,
      size: Int >= 0
  ): Array[Byte] =
    Array.tabulate(size) { byte =>
      val register = first.offsetBy((byte / 4).unsafe[Int >= 0])
      ((registers(register)(lane) >>> ((byte % 4) * 8)) & 0xffL).toByte
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
    while (offset < bytes.size) {
      val width = math.min(4, bytes.size - offset).unsafe[Int in 1 ~ 4]
      // The loop guard proves at least one byte remains; each register consumes at most four bytes.
      registers(first.offsetBy(offset / 4))(lane) = LittleEndian.decode(
        bytes,
        offset,
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

  /** Writes the `size` bytes of the LDS at `offset`, or those `address` wraps to where `offset` is invalid. */
  private def writeLdsBytes(
      registers: VectorRegisters,
      first: VectorRegisterIndex,
      lane: Int >= 0,
      lds: Array[Byte],
      offset: Int,
      address: Long,
      size: Int
  ): Unit =
    if (offset == InvalidLdsOffset) writeVectorBytes(registers, first, lane, ldsBytes(lds, address, size))
    else writeVectorBytes(registers, first, lane, lds, offset, size)

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
    while (offset <= size - 4) {
      registers(first.offsetBy(offset / 4))(lane) = (bytes(sourceOffset + offset).toLong & 0xffL) |
        ((bytes(sourceOffset + offset + 1).toLong & 0xffL) << 8) |
        ((bytes(sourceOffset + offset + 2).toLong & 0xffL) << 16) |
        ((bytes(sourceOffset + offset + 3).toLong & 0xffL) << 24)
      offset += 4
    }
    if (offset < size) {
      var value = 0L
      var shift = 0
      while (offset.raw + shift < size) {
        value |= (bytes(sourceOffset + offset + shift).toLong & 0xffL) << (shift * 8)
        shift += 1
      }
      registers(first.offsetBy(offset / 4))(lane) = value
    }
  }

  private def writeVectorValue(
      registers: VectorRegisters,
      first: VectorRegisterIndex,
      lane: Int >= 0,
      value: Long,
      size: Int
  ): Unit = {
    registers(first)(lane) = Bits.low32(value).toLong
    if (size > 4) registers(first.offsetBy(1))(lane) = Bits.high32(value).toLong
  }

}
