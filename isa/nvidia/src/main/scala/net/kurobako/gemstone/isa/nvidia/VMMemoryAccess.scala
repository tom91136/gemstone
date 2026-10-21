package net.kurobako.gemstone.isa.nvidia

import VMExecution.*
import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

/** One lane's memory accesses, routed to the storage their address space selects. */
private object VMMemoryAccess {

  /** Routes one access to the parameter buffers, the workgroup's shared or thread-local storage, or device memory.
    * Generic addresses select a window by aperture; explicit global accesses still see parameter buffers. Explicit
    * local addresses are relative to the executing frame, generic ones to the thread's whole local window.
    */
  // A null owner names a rank outside the cluster, which no shared window holds.
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  inline def route[A](context: VMContext, access: MemoryAccess, address: Long, size: Int >= 0)(
      inline buffer: (Array[Byte], Int) => A,
      shared: => A,
      inline local: Long => A,
      global: => A
  ): A = access match {
    case MemoryAccess.Shared => if (context.workgroup.sharedOwner(address) == null) global else shared
    case MemoryAccess.Local  => local(context.frame.localBase + address)
    case _                   =>
      context.workgroup.withBuffer(address, size)(buffer)(
        if (access.generic && VMAddressSpace.isShared(address, size) && context.workgroup.sharedOwner(address) != null)
          shared
        else if (access.generic && VMAddressSpace.isLocal(address)) local(address)
        else global
      )
  }

  /** Loads one value into `context.scalar` without boxing it for the parameter, shared and local routes. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def loadValue(
      context: VMContext,
      lane: WorkgroupLane,
      access: MemoryAccess,
      address: Long,
      width: ScalarBitWidth
  ): Either[Fault, Unit] = {
    val size = width.toScalarByteWidth
    route(context, access, address, size.widen)(
      (bytes, offset) => {
        context.scalar.elem = LittleEndian.decode(bytes, offset, size.widen[Int in 0 ~ 8])
        Completed
      }, {
        context.scalar.elem = context.workgroup.readShared(address, width)
        Completed
      },
      local => {
        context.scalar.elem = context.workgroup.readLocal(lane, local, width)
        Completed
      },
      (if (access.systemScoped) context.device.memory.readSystemValueInto(GuestAddress(address), size, context.scalar)
       else context.device.memory.readValueInto(GuestAddress(address), size, context.scalar)) match {
        case null         => Completed
        case fault: Fault => Left(VMWriteBuffer.located(fault, context.pc, lane))
      }
    )
  }

  def stageStore(
      context: VMContext,
      effects: VMWriteBuffer,
      lane: WorkgroupLane,
      access: MemoryAccess,
      address: Long,
      width: ScalarBitWidth,
      value: Long
  ): Unit = route(context, access, address, width.toScalarByteWidth.widen)(
    (_, _) => effects.stage(VMWrite.ParameterBuffer(address, width, value)),
    effects.stageShared(address, width, value),
    local => effects.stageLocal(lane, local, width, value),
    if (access.systemScoped) effects.stage(VMWrite.Memory(lane, GuestAddress(address), width, value, system = true))
    else effects.stageMemory(lane, GuestAddress(address), width, value)
  )

  /** Stores issued by the emulator on a lane's behalf, such as device-runtime out-parameters. */
  def storeGeneric(context: VMContext, lane: WorkgroupLane, address: Long, width: ScalarBitWidth, value: Long)
      : Either[Fault, Unit] =
    route(context, MemoryAccess.Generic(false), address, width.toScalarByteWidth.widen)(
      (bytes, offset) => Right(LittleEndian.write(value, bytes, offset, width.toScalarByteWidth)),
      Right(context.workgroup.writeShared(address, width, value)),
      local => Right(context.workgroup.writeLocal(lane, local, width, value)),
      context.device.memory.writeValue(GuestAddress(address), value, width.toScalarByteWidth)
    )

  /** Faults the first active lane whose access of `size` bytes is [[misaligned]], then publishes the device ranges the
    * lanes will read.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  inline def prepareReads(context: VMContext, access: MemoryAccess, size: Int >= 0)(
      inline address: WorkgroupLane => Long
  ): Either[Fault, Unit] = {
    var fault: Fault | Null = null
    access match {
      case MemoryAccess.Shared | MemoryAccess.Local =>
        context.active.foreachLane(lane =>
          if (fault == null) fault = misaligned(context, access, lane, address(lane), size)
        )
        fault match {
          case null         => Completed
          case found: Fault => Left(found)
        }
      case _ =>
        val ranges = context.reads
        ranges.clear()
        context.active.foreachLane { lane =>
          val raw = address(lane)
          if (fault == null) fault = misaligned(context, access, lane, raw, size)
          route(context, access, raw, size)(
            (_, _) => (),
            (),
            _ => (),
            ranges.add(GuestAddress(raw), size)
          )
        }
        fault match {
          case found: Fault => Left(found)
          case null         =>
            (if (access.systemScoped) context.device.memory.prepareSystemReads(ranges)
             else context.device.memory.prepareReads(ranges)) match {
              case Left(unmapped) =>
                // A batch fault names no lane, so attribute it to the first lane whose device range is unmapped.
                val faulting = context.active.filterLanes { lane =>
                  val raw = address(lane)
                  route(context, access, raw, size)(
                    (_, _) => false,
                    false,
                    _ => false,
                    context.device.memory.locate(GuestAddress(raw), size).isLeft
                  )
                }.headOption
                Left(VMWriteBuffer.located(
                  unmapped,
                  context.pc,
                  faulting.orElse(context.active.headOption).getOrElse(0)
                ))
              case prepared => prepared
            }
        }
    }
  }

  /** CUB's scan descriptors rely on coherent aligned status/value transfers, including 16-byte double descriptors. This
    * target behaviour is stronger than PTX's minimum vector ordering; it is not a general 128-bit atomic guarantee.
    */
  def coherentVector(context: VMContext, access: MemoryAccess, address: Long, size: Int >= 0): Boolean =
    (access == MemoryAccess.Global(false) || access == MemoryAccess.Generic(false)) &&
      size > 0 && size <= 16 &&
      (size & (size.raw - 1)) == 0 &&
      (address & (size.raw - 1).toLong) == 0L &&
      !VMAddressSpace.isShared(address) && !VMAddressSpace.isLocal(address) &&
      context.workgroup.withBuffer(address, size)((_, _) => false)(true)

  /** The misaligned-address fault of an access of `size` bytes at `address`, or null where it is aligned to the largest
    * power of two dividing `size`. NVIDIA GPUs fault such an access in every state space; parameter buffers are exempt,
    * as ptxas resolves their offsets statically.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  inline def misaligned(context: VMContext, access: MemoryAccess, lane: WorkgroupLane, address: Long, size: Int)
      : Fault | Null =
    if ((address & ((size & -size) - 1).toLong) == 0L || parameterBuffer(context, access, address, size)) null
    else Fault.Misaligned(s"pc ${context.pc}, lane $lane: $size-byte access at 0x${address.toHexString}")

  /** [[misaligned]] for an atomic, which no state space exempts. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  inline def misalignedAtomic(context: VMContext, lane: WorkgroupLane, address: Long, width: ScalarBitWidth)
      : Fault | Null =
    if ((address & (width.toScalarByteWidth.raw - 1).toLong) == 0L) null
    else
      Fault.Misaligned(
        s"pc ${context.pc}, lane $lane: ${width.toScalarByteWidth}-byte atomic at 0x${address.toHexString}"
      )

  private def parameterBuffer(context: VMContext, access: MemoryAccess, address: Long, size: Int): Boolean =
    access != MemoryAccess.Shared && access != MemoryAccess.Local &&
      context.workgroup.withBuffer(address, size.unsafe[Int >= 0])((_, _) => true)(false)

  def atomicTargetsShared(space: MemorySpace, address: Long, width: ScalarBitWidth): Boolean =
    space == MemorySpace.Shared ||
      (space == MemorySpace.Generic && VMAddressSpace.isShared(address, width.toScalarByteWidth))
}
