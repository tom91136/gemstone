package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.vm.*

/** Launch checks against the session allocations, which only the session can resolve. */
private object TaskValidation {
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def validate(
      task: KernelTask,
      allocations: AllocationAddressIndex,
      descriptorBindings: Vec[DescriptorBinding]
  ): Either[ExecutionFailure, Unit] = {
    val word                                  = abi.MachineWordSize.toLong
    def registered(id: AllocationId): Boolean = allocations.entry(id) != null
    def size(id: AllocationId): Long          = allocations.entry(id).size.toLong
    def lastScalarWord(index: Int >= 0): Long = task.arguments(index.raw) match {
      case KernelTask.Argument.Scalar(bytes) => bytes.length.toLong - word
      case _                                 => -1L
    }
    task.arguments.collectFirst {
      case KernelTask.Argument.Memory(region, _, _) if !registered(region) =>
        ExecutionFailure.NotFound(s"kernel argument refers to unregistered allocation $region")
      case KernelTask.Argument.Memory(region, offset, _) if offset > size(region) =>
        ExecutionFailure.InvalidRequest(s"kernel argument offset $offset is outside allocation $region")
      case KernelTask.Argument.Memory(region, offset, Some(range)) if range > size(region) - offset =>
        ExecutionFailure.InvalidRequest("kernel argument range exceeds its allocation")
    }.orElse {
      val bound = descriptorBindings.map(_.argument.raw).toSet
      task.arguments.view.zipWithIndex.collectFirst {
        case (KernelTask.Argument.Descriptor, index) if !bound(index) =>
          ExecutionFailure.InvalidRequest(s"kernel argument $index has no descriptor binding")
      }
    }.orElse(task.argumentBuffer.collect {
      case buffer if !registered(buffer.region) =>
        ExecutionFailure.NotFound(s"argument buffer refers to unregistered allocation ${buffer.region}")
      case buffer if buffer.offset >= size(buffer.region) =>
        ExecutionFailure.InvalidRequest("argument buffer offset is outside its allocation")
    }).orElse(task.pointerRelocations.collectFirst {
      case KernelTask.PointerRelocation(KernelTask.RelocationSource.Memory(source), _, _, _)
          if !registered(source) =>
        ExecutionFailure.NotFound(s"pointer relocation refers to unregistered source allocation $source")
      case KernelTask.PointerRelocation(target = target) if !registered(target) =>
        ExecutionFailure.NotFound(s"pointer relocation refers to unregistered target allocation $target")
      case KernelTask.PointerRelocation(KernelTask.RelocationSource.Memory(source), sourceOffset, _, _)
          if sourceOffset > size(source) - word =>
        ExecutionFailure.InvalidRequest(s"pointer relocation source offset $sourceOffset exceeds allocation $source")
      case KernelTask.PointerRelocation(_, _, target, targetOffset) if targetOffset > size(target) =>
        ExecutionFailure.InvalidRequest(s"pointer relocation target offset $targetOffset exceeds allocation $target")
      case KernelTask.PointerRelocation(KernelTask.RelocationSource.Argument(index), sourceOffset, _, _)
          if sourceOffset > lastScalarWord(index) =>
        ExecutionFailure.InvalidRequest(s"pointer relocation source offset $sourceOffset exceeds argument $index")
    }).toLeft(())
  }

  /** Pointer-bearing scalar arguments whose values fall within a client alias, including one past its end. */
  def discover(
      task: KernelTask,
      metadata: KernelMetadata,
      addresses: AllocationAddressIndex
  ): Vec[KernelTask.PointerRelocation] =
    Vec.from(task.arguments.zip(Vec.indices(task.arguments.nonNegativeSize)).flatMap {
      case (KernelTask.Argument.Scalar(bytes), index)
          if metadata.parameters.lift(index).exists(_.containsPointers) =>
        val array = bytes.toArray
        (0 to bytes.size - 8).by(4).flatMap { start =>
          val offset = start.unsafe[Int >= 0]
          addresses.findAlias(GuestAddress(LittleEndian.decode(array, start, 8)), 0).map((allocation, target) =>
            KernelTask.PointerRelocation(KernelTask.RelocationSource.Argument(index), offset, allocation, target)
          )
        }
      case _ => Vector.empty
    })

  def referenced(task: KernelTask): Set[AllocationId] =
    task.arguments.collect { case KernelTask.Argument.Memory(region, _, _) => region }.toSet ++
      task.argumentBuffer.map(_.region) ++
      task.pointerRelocations.flatMap { relocation =>
        relocation.source match {
          case KernelTask.RelocationSource.Memory(region) => Vector(relocation.target, region)
          case KernelTask.RelocationSource.Argument(_)    => Vector(relocation.target)
        }
      }
}
