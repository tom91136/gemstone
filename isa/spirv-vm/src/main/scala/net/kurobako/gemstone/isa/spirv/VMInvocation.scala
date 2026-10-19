package net.kurobako.gemstone.isa.spirv

import VMCollectives.Request as CollectiveRequest
import VMMemory.*
import VMProgram.*
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
      val calls: mutable.ArrayDeque[CallFrame]
  ) derives CanEqual {
    private enum Status derives CanEqual {
      case Running
      case WaitingMemory
      case WaitingCollective(request: CollectiveRequest)
      case Completed
    }
    val storage: Arena                       = Arena()
    private var status: Status               = Status.Running
    def completed: Boolean                   = status == Status.Completed
    def memoryPending: Boolean               = status == Status.WaitingMemory
    def suspended: Option[CollectiveRequest] = status match {
      case Status.WaitingCollective(request) => Some(request)
      case _                                 => None
    }
    def complete(): Unit                          = status = Status.Completed
    def awaitMemory(): Unit                       = status = Status.WaitingMemory
    def memoryReady(): Unit                       = if (memoryPending) status = Status.Running
    def suspend(request: CollectiveRequest): Unit = status = Status.WaitingCollective(request)
    def resume(): Unit                            = status = Status.Running

    /** The invocation stopped at a point another lane or the scheduler must resolve. */
    def yielded: Boolean = status != Status.Running || quadRequest.nonEmpty || reconvergenceJoin.nonEmpty

    val reconvergenceRegions: mutable.ArrayBuffer[ReconvergenceRegion] = mutable.ArrayBuffer.empty
    var reconvergenceJoin: Option[ReconvergenceJoin]                   = None
    var reconvergedReturn                                              = false
    var quadRequest: Option[QuadRequest]                               = None
    var gradients: Option[(Array[Double], Array[Double])]              = None
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

    def reset(nextInvocation: Invocation, function: Function): Unit = {
      invocation = nextInvocation
      currentFunction = function
      pc = function.start + 1
      currentLabel = -1
      predecessor = -1
      calls.clear()
      status = Status.Running
      quadRequest = None
      reconvergenceRegions.clear()
      reconvergenceJoin = None
      reconvergedReturn = false
      gradients = None
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
