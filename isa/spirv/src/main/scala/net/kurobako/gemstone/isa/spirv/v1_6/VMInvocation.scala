package net.kurobako.gemstone.isa.spirv.v1_6

import VMCollectives.Request as CollectiveRequest
import VMIR.*
import VMMemory.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

private object VMInvocation {
  class BaseScalarValues(
      constants: Map[Int, VMValue],
      globals: Map[Int, VMValue],
      idBound: Long
  ) {
    private val denseLimit = 1000000L
    private val types      =
      if (idBound > 0L && idBound <= denseLimit) Array.fill(idBound.toInt)(-1)
      else Array.empty[Int]
    private val bits = Array.ofDim[Long](types.length)

    (constants.view ++ globals.view).foreach {
      case (key, VMValue.Bits(typeId, Array(value))) if key >= 0 && key < types.length =>
        types(key) = typeId
        bits(key) = value
      case _ => ()
    }

    def scalarType(key: Int): Int = if (key >= 0 && key < types.length) types(key) else -1

    def scalarBits(key: Int): Long = bits(key)
  }

  /** Per-invocation writes over immutable module/dispatch values. A large oneDPL launch can contain hundreds of
    * thousands of invocations, so eagerly copying every SPIR-V constant into every lane dominates execution.
    */
  // This final holder is the mutable register file for one invocation.
  // Dense nullable slots avoid allocating an Option for every interpreted SSA read and write.
  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  class InvocationValues(
      constants: Map[Int, VMValue],
      globals: Map[Int, VMValue],
      idBound: Long,
      baseScalars: BaseScalarValues,
      metrics: VMMetrics,
      trackTouched: Boolean = true
  ) {
    private val denseLimit = 1000000L
    private val dense      =
      if (idBound > 0L && idBound <= denseLimit) Array.ofDim[VMValue | Null](idBound.toInt)
      else Array.empty[VMValue | Null]
    private val denseScalarTypes = Array.fill(dense.length)(-1)
    private val denseScalarBits  = Array.ofDim[Long](dense.length)
    private val touched          = if (trackTouched) Array.ofDim[Int](dense.length) else Array.empty[Int]
    private var touchedSize      = 0
    private val overflow         = mutable.LongMap.empty[VMValue]

    metrics.recordInvocationValues(dense.length, 16)

    def get(key: Long): Option[VMValue] = {
      val local =
        if (key >= 0L && key < dense.length.toLong) {
          val index = key.toInt
          if (denseScalarTypes(index) >= 0)
            Some(VMValue.Bits(denseScalarTypes(index), Array(denseScalarBits(index))))
          else
            dense(index) match {
              case null  => None
              case value => Some(value)
            }
        } else overflow.get(key)
      local.orElse(globals.get(key.toInt)).orElse(constants.get(key.toInt))
    }

    def update(key: Long, value: VMValue): Unit =
      if (key >= 0L && key < dense.length.toLong) {
        val index = key.toInt
        if (trackTouched && dense(index) == null && denseScalarTypes(index) < 0) {
          touched(touchedSize) = index
          touchedSize += 1
        }
        value match {
          case VMValue.Bits(typeId, Array(bits)) =>
            dense(index) = null
            denseScalarTypes(index) = typeId
            denseScalarBits(index) = bits
          case _ =>
            dense(index) = value
            denseScalarTypes(index) = -1
        }
      } else overflow.update(key, value)

    def scalarType(key: Int): Int =
      if (key >= 0 && key < dense.length) {
        val localType = denseScalarTypes(key)
        if (localType >= 0) localType
        else if (dense(key) != null) -1
        else {
          val baseType = baseScalars.scalarType(key)
          if (baseType >= 0) baseType else fallbackScalarType(key)
        }
      } else
        overflow.get(key.toLong) match {
          case Some(VMValue.Bits(typeId, Array(_))) => typeId
          case Some(_)                              => -1
          case None                                 => fallbackScalarType(key)
        }

    def scalarBits(key: Int): Long =
      if (key >= 0 && key < dense.length) {
        if (denseScalarTypes(key) >= 0) denseScalarBits(key)
        else if (baseScalars.scalarType(key) >= 0) baseScalars.scalarBits(key)
        else fallbackScalarBits(key)
      } else
        overflow.get(key.toLong) match {
          case Some(VMValue.Bits(_, Array(value))) => value
          case Some(_)                             => throw IllegalStateException(s"SPIR-V value %$key is not scalar")
          case None                                => fallbackScalarBits(key)
        }

    def readScalar(key: Int): Option[Long] =
      if (key >= 0 && key < dense.length) {
        if (denseScalarTypes(key) >= 0) Some(denseScalarBits(key))
        else if (dense(key) != null) None
        else if (baseScalars.scalarType(key) >= 0) Some(baseScalars.scalarBits(key))
        else readFallbackScalar(key)
      } else
        overflow.get(key.toLong) match {
          case Some(VMValue.Bits(_, Array(value))) => Some(value)
          case Some(_)                             => None
          case None                                => readFallbackScalar(key)
        }

    private def readFallbackScalar(key: Int): Option[Long] =
      globals.get(key).orElse(constants.get(key)) match {
        case Some(VMValue.Bits(_, Array(value))) => Some(value)
        case _                                   => None
      }

    private def fallbackScalarType(key: Int): Int =
      globals.get(key).orElse(constants.get(key)) match {
        case Some(VMValue.Bits(typeId, Array(_))) => typeId
        case _                                    => -1
      }

    private def fallbackScalarBits(key: Int): Long =
      globals.get(key).orElse(constants.get(key)) match {
        case Some(VMValue.Bits(_, Array(value))) => value
        case _                                   => throw IllegalStateException(s"SPIR-V value %$key is not scalar")
      }

    def updateScalar(key: Int, typeId: Int, bits: Long): Unit =
      if (key >= 0 && key < dense.length) {
        if (trackTouched && dense(key) == null && denseScalarTypes(key) < 0) {
          touched(touchedSize) = key
          touchedSize += 1
        }
        dense(key) = null
        denseScalarTypes(key) = typeId
        denseScalarBits(key) = bits
      } else overflow.update(key.toLong, VMValue.Bits(typeId, Array(bits)))

    def clear(): Unit = {
      var index = 0
      while (index < touchedSize) {
        dense(touched(index)) = null
        denseScalarTypes(touched(index)) = -1
        index += 1
      }
      touchedSize = 0
      overflow.clear()
    }
  }

  case class CallFrame(
      function: Function,
      pc: Int,
      currentLabel: Int,
      predecessor: Int,
      resultType: Int,
      destination: Int
  )
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  class InvocationBuiltins(
      initialGroup: Coordinates,
      initialLocal: Coordinates,
      initialGlobal: LongCoordinates,
      initialGeometry: LaunchGeometry,
      initialActiveGroupSize: Dimensions,
      initialEnqueuedGroupSize: Dimensions,
      initialSubgroupSize: Int,
      initialMaxSubgroupSize: Int,
      initialNumSubgroups: Long,
      initialEnqueuedSubgroups: Long,
      initialSubgroupIndex: Int,
      initialSubgroupLocal: Int
  ) {
    private val groupValues                      = Array.ofDim[Long](3)
    def group(axis: Int): Long                   = groupValues(axis)
    def groupVector: Array[Long]                 = groupValues.clone()
    def groupX: Long                             = groupValues(0)
    def groupY: Long                             = groupValues(1)
    def groupZ: Long                             = groupValues(2)
    private val localValues                      = Array.ofDim[Long](3)
    def local(axis: Int): Long                   = localValues(axis)
    def localVector: Array[Long]                 = localValues.clone()
    def localX: Long                             = localValues(0)
    def localY: Long                             = localValues(1)
    def localZ: Long                             = localValues(2)
    private val globalValues                     = Array.ofDim[Long](3)
    def global(axis: Int): Long                  = globalValues(axis)
    def globalVector: Array[Long]                = globalValues.clone()
    def globalX: Long                            = globalValues(0)
    def globalY: Long                            = globalValues(1)
    def globalZ: Long                            = globalValues(2)
    private val globalSizeValues                 = Array.ofDim[Long](3)
    def globalSize(axis: Int): Long              = globalSizeValues(axis)
    def globalSizeVector: Array[Long]            = globalSizeValues.clone()
    def globalSizeX: Long                        = globalSizeValues(0)
    def globalSizeY: Long                        = globalSizeValues(1)
    def globalSizeZ: Long                        = globalSizeValues(2)
    private val globalOffsetValues               = Array.ofDim[Long](3)
    def globalOffset(axis: Int): Long            = globalOffsetValues(axis)
    def globalOffsetVector: Array[Long]          = globalOffsetValues.clone()
    def globalOffsetX: Long                      = globalOffsetValues(0)
    def globalOffsetY: Long                      = globalOffsetValues(1)
    def globalOffsetZ: Long                      = globalOffsetValues(2)
    private val numWorkgroupsValues              = Array.ofDim[Long](3)
    def numWorkgroups(axis: Int): Long           = numWorkgroupsValues(axis)
    def numWorkgroupsVector: Array[Long]         = numWorkgroupsValues.clone()
    def numWorkgroupsX: Long                     = numWorkgroupsValues(0)
    def numWorkgroupsY: Long                     = numWorkgroupsValues(1)
    def numWorkgroupsZ: Long                     = numWorkgroupsValues(2)
    private val workgroupSizeValues              = Array.ofDim[Long](3)
    def workgroupSize(axis: Int): Long           = workgroupSizeValues(axis)
    def workgroupSizeVector: Array[Long]         = workgroupSizeValues.clone()
    def workgroupSizeX: Long                     = workgroupSizeValues(0)
    def workgroupSizeY: Long                     = workgroupSizeValues(1)
    def workgroupSizeZ: Long                     = workgroupSizeValues(2)
    private val enqueuedWorkgroupSizeValues      = Array.ofDim[Long](3)
    def enqueuedWorkgroupSize(axis: Int): Long   = enqueuedWorkgroupSizeValues(axis)
    def enqueuedWorkgroupSizeVector: Array[Long] = enqueuedWorkgroupSizeValues.clone()
    def enqueuedWorkgroupSizeX: Long             = enqueuedWorkgroupSizeValues(0)
    def enqueuedWorkgroupSizeY: Long             = enqueuedWorkgroupSizeValues(1)
    def enqueuedWorkgroupSizeZ: Long             = enqueuedWorkgroupSizeValues(2)
    private var currentWorkDim: Int              = 0
    def workDim: Int                             = currentWorkDim
    private var currentSubgroupSize: Int         = initialSubgroupSize
    def subgroupSize: Int                        = currentSubgroupSize
    private var currentMaxSubgroupSize: Int      = initialMaxSubgroupSize
    def maxSubgroupSize: Int                     = currentMaxSubgroupSize
    private var currentNumSubgroups: Long        = initialNumSubgroups
    def numSubgroups: Long                       = currentNumSubgroups
    private var currentEnqueuedSubgroups: Long   = initialEnqueuedSubgroups
    def enqueuedSubgroups: Long                  = currentEnqueuedSubgroups
    private var currentSubgroupIndex: Int        = initialSubgroupIndex
    def subgroupIndex: Int                       = currentSubgroupIndex
    private var currentSubgroupLocal: Int        = initialSubgroupLocal
    def subgroupLocal: Int                       = currentSubgroupLocal
    reset(
      initialGroup,
      initialLocal,
      initialGlobal,
      initialGeometry,
      initialActiveGroupSize,
      initialEnqueuedGroupSize,
      initialSubgroupSize,
      initialMaxSubgroupSize,
      initialNumSubgroups,
      initialEnqueuedSubgroups,
      initialSubgroupIndex,
      initialSubgroupLocal
    )

    def reset(
        nextGroup: Coordinates,
        nextLocal: Coordinates,
        nextGlobal: LongCoordinates,
        geometry: LaunchGeometry,
        activeGroupSize: Dimensions,
        enqueuedGroupSize: Dimensions,
        nextSubgroupSize: Int,
        nextMaxSubgroupSize: Int,
        nextNumSubgroups: Long,
        nextEnqueuedSubgroups: Long,
        nextSubgroupIndex: Int,
        nextSubgroupLocal: Int
    ): Unit = {
      groupValues(0) = nextGroup.x.raw.toLong
      groupValues(1) = nextGroup.y.raw.toLong
      groupValues(2) = nextGroup.z.raw.toLong
      localValues(0) = nextLocal.x.raw.toLong
      localValues(1) = nextLocal.y.raw.toLong
      localValues(2) = nextLocal.z.raw.toLong
      globalValues(0) = nextGlobal.x.raw
      globalValues(1) = nextGlobal.y.raw
      globalValues(2) = nextGlobal.z.raw
      globalSizeValues(0) = geometry.globalExtent.x.raw
      globalSizeValues(1) = geometry.globalExtent.y.raw
      globalSizeValues(2) = geometry.globalExtent.z.raw
      globalOffsetValues(0) = geometry.globalOffset.x.raw.toLong
      globalOffsetValues(1) = geometry.globalOffset.y.raw.toLong
      globalOffsetValues(2) = geometry.globalOffset.z.raw.toLong
      currentWorkDim = geometry.workDim.raw
      numWorkgroupsValues(0) = geometry.groupCount.x.raw.toLong
      numWorkgroupsValues(1) = geometry.groupCount.y.raw.toLong
      numWorkgroupsValues(2) = geometry.groupCount.z.raw.toLong
      workgroupSizeValues(0) = activeGroupSize.x.raw.toLong
      workgroupSizeValues(1) = activeGroupSize.y.raw.toLong
      workgroupSizeValues(2) = activeGroupSize.z.raw.toLong
      enqueuedWorkgroupSizeValues(0) = enqueuedGroupSize.x.raw.toLong
      enqueuedWorkgroupSizeValues(1) = enqueuedGroupSize.y.raw.toLong
      enqueuedWorkgroupSizeValues(2) = enqueuedGroupSize.z.raw.toLong
      currentSubgroupSize = nextSubgroupSize
      currentMaxSubgroupSize = nextMaxSubgroupSize
      currentNumSubgroups = nextNumSubgroups
      currentEnqueuedSubgroups = nextEnqueuedSubgroups
      currentSubgroupIndex = nextSubgroupIndex
      currentSubgroupLocal = nextSubgroupLocal
    }

  }
  // One request per dynamic derivative site. Lanes are ordered (0,0), (1,0), (0,1), (1,1).
  case class QuadRequest(
      function: Int,
      pc: Int,
      resultType: Int,
      destination: Int,
      operand: Array[Long],
      operation: Int
  )

  case class FragmentRegion(function: Int, header: Int, merge: Int, loop: Boolean)
  enum FragmentJoin {
    case Merge(label: Int)
    case FunctionReturn(function: Int)
  }

  // PC, call stack and collective suspension are the mutable state of one invocation machine.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  class InvocationState(
      var invocation: InvocationBuiltins,
      val values: InvocationValues,
      var currentFunction: Function,
      var pc: Int,
      var currentLabel: Int,
      var predecessor: Int,
      var instructionCount: Long,
      val calls: mutable.ArrayDeque[CallFrame]
  ) {
    private enum Status {
      case Running
      case WaitingMemory
      case WaitingCollective(request: CollectiveRequest)
      case Completed
    }
    private var status: Status               = Status.Running
    def completed: Boolean                   = status == Status.Completed
    def memoryPending: Boolean               = status == Status.WaitingMemory
    def suspended: Option[CollectiveRequest] = status match {
      case Status.WaitingCollective(request) => Some(request)
      case _                                 => None
    }
    def complete(): Unit                                  = status = Status.Completed
    def awaitMemory(): Unit                               = status = Status.WaitingMemory
    def memoryReady(): Unit                               = if (memoryPending) status = Status.Running
    def suspend(request: CollectiveRequest): Unit         = status = Status.WaitingCollective(request)
    def resume(): Unit                                    = status = Status.Running
    lazy val fragmentRegions: ArrayBuffer[FragmentRegion] = mutable.ArrayBuffer.empty[FragmentRegion]
    var fragmentJoin: Option[FragmentJoin]                = None
    var fragmentReturnReady                               = false
    var quadRequest: Option[QuadRequest]                  = None
    var gradients: Option[(Array[Double], Array[Double])] = None
    var fragmentQuad                                      = false
    var subgroupReconvergence                             = false
    var helperInvocation                                  = false
    var fragmentInvocation                                = false
    var fragmentKilled                                    = false
    var sampleMask: Option[Pointer]                       = None

    def reset(nextInvocation: InvocationBuiltins, function: Function): Unit = {
      invocation = nextInvocation
      currentFunction = function
      pc = function.start + 1
      currentLabel = -1
      predecessor = -1
      instructionCount = 0L
      calls.clear()
      status = Status.Running
      quadRequest = None
      if (fragmentQuad || subgroupReconvergence) fragmentRegions.clear()
      fragmentJoin = None
      fragmentReturnReady = false
      gradients = None
      fragmentQuad = false
      subgroupReconvergence = false
      helperInvocation = false
      fragmentInvocation = false
      fragmentKilled = false
      sampleMask = None
    }
  }
}
