package net.kurobako.gemstone.isa.spirv

import VMCollectives.Request as CollectiveRequest
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable

private object VMInvocation {

  /** One invocation's register file. Scalar numeric values live unboxed; every other value is a [[VMValue]]. A file
    * starts as a copy of its dispatch template, so constants and dispatch globals are never copied per invocation.
    */
  object InvocationValues {

    /** A register file holding every constant of `program`; dispatches copy it and add their globals. */
    def registers(program: VMProgram, globals: Map[Int, VMValue]): InvocationValues = {
      val values =
        InvocationValues(program, Array.ofDim[Long](program.idBound), Array.ofDim[VMValue | Null](program.idBound))
      program.constants.foreach { case (id, value) => values.update(id, value) }
      program.functionPointerConstants.foreach((id, typeId, function) =>
        values.update(id, VMValue.Bits(typeId, Array(program.functionPointer(function))))
      )
      globals.foreach { case (id, value) => values.update(id, value) }
      values
    }
  }

  class InvocationValues(val program: VMProgram, val scalars: Array[Long], val objects: Array[VMValue | Null]) {
    def copy(): InvocationValues = InvocationValues(program, scalars.clone(), objects.clone())

    private def defined(key: Int): Boolean = key >= 0 && key < scalars.length

    def get(key: Int): Option[VMValue] =
      if (!defined(key)) None
      else if (program.scalarSlot(key)) Some(VMValue.Bits(program.valueType(key), Array(scalars(key))))
      else Option(objects(key))

    def update(key: Int, value: VMValue): Unit =
      if (program.scalarSlot(key)) value match {
        case VMValue.Bits(_, lanes) if lanes.length == 1 => scalars(key) = lanes(0)
        case other => throw IllegalStateException(s"SPIR-V scalar value %$key cannot hold $other")
      }
      else objects(key) = value

    def updateScalar(key: Int, typeId: Int, bits: Long): Unit =
      if (program.scalarSlot(key)) scalars(key) = bits else objects(key) = VMValue.Bits(typeId, Array(bits))

    /** The scalar lanes of a numeric value, or null when the value is absent or a pointer. */
    @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
    def lanes(key: Int): Array[Long] | Null =
      if (program.scalarSlot(key)) Array(scalars(key))
      else objects(key) match {
        case VMValue.Bits(_, lanes) => lanes
        case _                      => null
      }

    private def leafCount(key: Int): Int =
      if (program.scalarSlot(key)) 1
      else objects(key) match {
        case VMValue.Bits(_, lanes) => lanes.length
        case _                      => 0
      }

    /** The first of `keys` that holds no numeric value, or -1. */
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def absent(keys: Array[Int]): Int = {
      var at    = 0
      var found = -1
      while (at < keys.length && found < 0) {
        if (leafCount(keys(at)) == 0) found = keys(at)
        at += 1
      }
      found
    }

    /** Leaf `index` of the numeric value `key` holds. */
    def leaf(key: Int, index: Int): Long =
      if (program.scalarSlot(key)) scalars(key)
      else objects(key) match {
        case VMValue.Bits(_, lanes) => lanes(index)
        case _                      => 0L
      }

    /** Leaf `index` of the leaves, in order, of the first `count` of `keys`. */
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def leafAt(keys: Array[Int], count: Int, index: Int): Long = {
      var remaining = index
      var at        = 0
      var found     = 0L
      while (at < count) {
        val size = leafCount(keys(at))
        if (remaining < size) {
          found = leaf(keys(at), remaining)
          at = count
        } else {
          remaining -= size
          at += 1
        }
      }
      found
    }

    def write(key: Int, typeId: Int, lanes: Array[Long]): Unit =
      if (program.scalarSlot(key) && lanes.length == 1) scalars(key) = lanes(0)
      else update(key, VMValue.Bits(typeId, lanes))
  }

  case class CallFrame(
      function: Function,
      pc: Int,
      currentLabel: Int,
      predecessor: Int,
      resultType: Int,
      destination: Int,
      stack: Int
  )

  /** An op that calls a function several times, in progress: the call it issued last, and the values the calls before
    * it returned. The op runs again as each call returns, at the same position and call depth.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  class CallLoop(val pc: Int, val depth: Int, val results: Array[Long]) {
    var call = 0
  }

  /** The launch coordinates and subgroup placement of one invocation. */
  case class Invocation(
      groupId: Coordinates,
      localId: Coordinates,
      globalId: LongCoordinates,
      geometry: LaunchGeometry,
      activeGroupSize: Dimensions,
      subgroupSize: Int,
      maxSubgroupSize: Int,
      numSubgroups: Long,
      enqueuedSubgroups: Long,
      subgroupIndex: Int,
      subgroupLocal: Int
  ) {
    def group(axis: Int): Long  = axisOf(axis).select(groupId).toLong
    def local(axis: Int): Long  = axisOf(axis).select(localId).toLong
    def global(axis: Int): Long = axis match {
      case 0 => globalId.x
      case 1 => globalId.y
      case _ => globalId.z
    }
    def globalSize(axis: Int): Long              = axisOf(axis).select(geometry.globalExtent)
    def globalOffset(axis: Int): Long            = axisOf(axis).select(geometry.globalOffset).toLong
    def numWorkgroups(axis: Int): Long           = axisOf(axis).select(geometry.groupCount).toLong
    def workgroupSize(axis: Int): Long           = axisOf(axis).select(activeGroupSize).toLong
    def enqueuedWorkgroupSize(axis: Int): Long   = axisOf(axis).select(geometry.groupSize).toLong
    def workDim: Int                             = geometry.workDim
    def vector(select: Int => Long): Array[Long] = Array(select(0), select(1), select(2))

    /** The invocation's index within its workgroup, with x varying fastest. It names the invocation within its
      * workgroup, and is the lane a fault at the invocation is reported against.
      */
    def linearLocal: Long = (local(2) * workgroupSize(1) + local(1)) * workgroupSize(0) + local(0)
  }

  private def axisOf(index: Int): Axis = index match {
    case 0 => Axis.X
    case 1 => Axis.Y
    case _ => Axis.Z
  }

  // One request per dynamic derivative site. Lanes are ordered (0,0), (1,0), (0,1), (1,1).
  case class QuadRequest(
      function: Int,
      pc: Int,
      resultType: Int,
      destination: Int,
      operand: IArray[Long],
      derivative: Derivative
  )

  /** Coarse derivatives take both differences from the quad's first lane; gradients feed implicit-LOD sampling. */
  enum Derivative(val coarse: Boolean) derives CanEqual {
    case Dx          extends Derivative(false)
    case Dy          extends Derivative(false)
    case Width       extends Derivative(false)
    case CoarseDx    extends Derivative(true)
    case CoarseDy    extends Derivative(true)
    case CoarseWidth extends Derivative(true)
    case Gradients   extends Derivative(false)
  }

  /** Structured regions whose merge blocks reconverge quad and subgroup lanes. */
  case class ReconvergenceRegion(function: Int, header: Int, merge: Int, loop: Boolean)
  enum ReconvergenceJoin {
    case Merge(label: Int)
    case FunctionReturn(function: Int)
  }

  // PC, call stack and collective suspension are the mutable state of one invocation machine.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  class InvocationState(
      var invocation: Invocation,
      val values: InvocationValues,
      var currentFunction: Function,
      var pc: Int,
      var currentLabel: Int,
      var predecessor: Int,
      val calls: mutable.ArrayDeque[CallFrame],
      val timeline: VMTimeline,
      var memory: MemoryState
  ) derives CanEqual {
    private enum Status derives CanEqual {
      case Running
      case WaitingMemory
      case WaitingCollective(request: CollectiveRequest)
      case Completed
    }
    val storage: Arena                       = Arena()
    var partials: Array[Long]                = Array.emptyLongArray
    var loops: List[CallLoop]                = Nil
    private var status: Status               = Status.Running
    def completed: Boolean                   = status == Status.Completed
    def memoryPending: Boolean               = status == Status.WaitingMemory
    def suspended: Option[CollectiveRequest] = status match {
      case Status.WaitingCollective(request) => Some(request)
      case _                                 => None
    }

    /** The invocation's index within its workgroup, which names the unit it runs as. */
    def ordinal: Int >= 0 = invocation.linearLocal.toInt.unsafe[Int >= 0]

    /** The unit this invocation runs as, in the state given. */
    def unit(state: MachineUnit.State): MachineUnit =
      timeline.unit(invocation.groupId, ordinal, state)

    /** A change in what this invocation is doing, which the timeline draws between the issues it surrounds. */
    private def transition(kind: MachineEvent.Kind): Unit =
      timeline.transition(
        invocation.groupId,
        ordinal,
        kind,
        Some(timeline.location(pc, invocation.groupId, ordinal))
      )

    /** The invocation finished: it issues nothing more. */
    def complete(): Unit = {
      status = Status.Completed
      transition(MachineEvent.Kind.Retired)
    }

    /** The invocation waits for memory it cannot read yet. A retried read finds it waiting already, and does not park
      * it again.
      */
    def awaitMemory(): Unit =
      if (!memoryPending) {
        status = Status.WaitingMemory
        transition(MachineEvent.Kind.Blocked(MachineUnit.Reason.Memory))
      }

    /** The memory the invocation waited for has arrived, so it runs on. */
    def memoryReady(): Unit =
      if (memoryPending) {
        status = Status.Running
        transition(MachineEvent.Kind.Resumed)
      }

    /** The invocation waits at a collective rendezvous until the lanes that meet there arrive. */
    def suspend(request: CollectiveRequest): Unit = {
      val parking = suspended.isEmpty
      status = Status.WaitingCollective(request)
      if (parking) transition(MachineEvent.Kind.Blocked(MachineUnit.Reason.Collective(VMTimeline.name(request))))
    }

    /** The collective the invocation waited at resolved, so it runs on. */
    def resume(): Unit = {
      status = Status.Running
      transition(MachineEvent.Kind.Resumed)
    }

    /** The invocation stops waiting without the machine releasing it: the launch faults instead. */
    def abandon(): Unit = status = Status.Running

    /** The request the invocation waits with changes, without the invocation stopping to wait. */
    def remark(request: CollectiveRequest): Unit = status = Status.WaitingCollective(request)

    /** The invocation stopped at a point another lane or the scheduler must resolve. */
    def yielded: Boolean = status != Status.Running || quadRequest.nonEmpty || reconvergenceJoin.nonEmpty

    val reconvergenceRegions: mutable.ArrayBuffer[ReconvergenceRegion] = mutable.ArrayBuffer.empty
    var reconvergenceJoin: Option[ReconvergenceJoin]                   = None
    var reconvergedReturn                                              = false
    var quadRequest: Option[QuadRequest]                               = None
    var gradients: Option[(Array[Double], Array[Double])]              = None
    var texels: Option[ImageOps.ImageLevel]                            = None
    var fragmentQuad                                                   = false
    var subgroupReconvergence                                          = false
    var helperInvocation                                               = false
    var fragmentInvocation                                             = false
    // The lane used its time slice and stopped where it can resume; it is runnable, not waiting.
    var preempted                   = false
    var fragmentKilled              = false
    var sampleMask: Option[Pointer] = None

    /** Instructions the invocation has issued, its subgroup clock. */
    var issued = 0L

    // An OpPhi group stages its incoming values here so the group's copies take effect together.
    private var stagedBits   = Array.ofDim[Long](4)
    private var stagedValues = Array.ofDim[VMValue | Null](4)

    def phiBits(count: Int): Array[Long] = {
      if (stagedBits.length < count) stagedBits = Array.ofDim[Long](count)
      stagedBits
    }

    def phiValues(count: Int): Array[VMValue | Null] = {
      if (stagedValues.length < count) stagedValues = Array.ofDim[VMValue | Null](count)
      stagedValues
    }

    def reset(nextInvocation: Invocation, function: Function, nextMemory: MemoryState): Unit = {
      invocation = nextInvocation
      memory = nextMemory
      currentFunction = function
      pc = function.start + 1
      currentLabel = -1
      predecessor = -1
      calls.clear()
      loops = Nil
      status = Status.Running
      quadRequest = None
      reconvergenceRegions.clear()
      reconvergenceJoin = None
      reconvergedReturn = false
      gradients = None
      texels = None
      fragmentQuad = false
      subgroupReconvergence = false
      helperInvocation = false
      fragmentInvocation = false
      fragmentKilled = false
      sampleMask = None
      issued = 0L
    }
  }
}
