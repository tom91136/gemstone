package net.kurobako.gemstone.isa.spirv.v1_6

import VMCollectives.Request as CollectiveRequest
import VMIR.{Constant, Function}
import VMMemory.{Pointer, Region}

import net.kurobako.gemstone.Vec

import scala.collection.mutable

private[v1_6] object VMInvocation {
  enum RuntimeValue {
    case Bits(typeId: Int, lanes: Array[Long])
    case Address(typeId: Int, pointer: Pointer)
  }

  def runtimeValue(constant: Constant): RuntimeValue = constant match {
    case Constant.Bits(typeId, lanes)                        => RuntimeValue.Bits(typeId, Vec.toArray(lanes))
    case Constant.NullPointer(typeId, pointee, storageClass) =>
      RuntimeValue.Address(typeId, Pointer(Region.Null, 0L, pointee, storageClass))
  }
  final class BaseScalarValues(
      constants: Map[Int, RuntimeValue],
      globals: Map[Int, RuntimeValue],
      idBound: Long
  ) {
    private val denseLimit = 1000000L
    private val types      =
      if (idBound > 0L && idBound <= denseLimit) Array.fill(idBound.toInt)(-1)
      else Array.empty[Int]
    private val bits = Array.ofDim[Long](types.length)

    (constants.view ++ globals.view).foreach {
      case (key, RuntimeValue.Bits(typeId, Array(value))) if key >= 0 && key < types.length =>
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
  final class InvocationValues(
      constants: Map[Int, RuntimeValue],
      globals: Map[Int, RuntimeValue],
      idBound: Long,
      baseScalars: BaseScalarValues,
      metrics: Performance.Metrics
  ) {
    private val denseLimit = 1000000L
    private val dense      =
      if (idBound > 0L && idBound <= denseLimit) Array.ofDim[RuntimeValue | Null](idBound.toInt)
      else Array.empty[RuntimeValue | Null]
    private val denseScalarTypes = Array.fill(dense.length)(-1)
    private val denseScalarBits  = Array.ofDim[Long](dense.length)
    private val touched          = Array.ofDim[Int](dense.length)
    private var touchedSize      = 0
    private val overflow         = mutable.LongMap.empty[RuntimeValue]

    metrics.recordInvocationValues(dense.length, 16)

    def get(key: Long): Option[RuntimeValue] = {
      val local =
        if (key >= 0L && key < dense.length.toLong) {
          val index = key.toInt
          if (denseScalarTypes(index) >= 0)
            Some(RuntimeValue.Bits(denseScalarTypes(index), Array(denseScalarBits(index))))
          else
            dense(index) match {
              case null  => None
              case value => Some(value)
            }
        } else overflow.get(key)
      local.orElse(globals.get(key.toInt)).orElse(constants.get(key.toInt))
    }

    def update(key: Long, value: RuntimeValue): Unit =
      if (key >= 0L && key < dense.length.toLong) {
        val index = key.toInt
        if (dense(index) == null && denseScalarTypes(index) < 0) {
          touched(touchedSize) = index
          touchedSize += 1
        }
        value match {
          case RuntimeValue.Bits(typeId, Array(bits)) =>
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
          case Some(RuntimeValue.Bits(typeId, Array(_))) => typeId
          case Some(_)                                   => -1
          case None                                      => fallbackScalarType(key)
        }

    def scalarBits(key: Int): Long =
      if (key >= 0 && key < dense.length) {
        if (denseScalarTypes(key) >= 0) denseScalarBits(key)
        else if (baseScalars.scalarType(key) >= 0) baseScalars.scalarBits(key)
        else fallbackScalarBits(key)
      } else
        overflow.get(key.toLong) match {
          case Some(RuntimeValue.Bits(_, Array(value))) => value
          case Some(_) => throw IllegalStateException(s"SPIR-V value %$key is not scalar")
          case None    => fallbackScalarBits(key)
        }

    private def fallbackScalarType(key: Int): Int =
      globals.get(key).orElse(constants.get(key)) match {
        case Some(RuntimeValue.Bits(typeId, Array(_))) => typeId
        case _                                         => -1
      }

    private def fallbackScalarBits(key: Int): Long =
      globals.get(key).orElse(constants.get(key)) match {
        case Some(RuntimeValue.Bits(_, Array(value))) => value
        case _ => throw IllegalStateException(s"SPIR-V value %$key is not scalar")
      }

    def updateScalar(key: Int, typeId: Int, bits: Long): Unit =
      if (key >= 0 && key < dense.length) {
        if (dense(key) == null && denseScalarTypes(key) < 0) {
          touched(touchedSize) = key
          touchedSize += 1
        }
        dense(key) = null
        denseScalarTypes(key) = typeId
        denseScalarBits(key) = bits
      } else overflow.update(key.toLong, RuntimeValue.Bits(typeId, Array(bits)))

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

  final case class CallFrame(
      function: Function,
      pc: Int,
      currentLabel: Int,
      predecessor: Int,
      resultType: Int,
      destination: Int
  )
  final case class Invocation(
      groupX: Long,
      groupY: Long,
      groupZ: Long,
      localX: Long,
      localY: Long,
      localZ: Long,
      globalX: Long,
      globalY: Long,
      globalZ: Long,
      globalSizeX: Long,
      globalSizeY: Long,
      globalSizeZ: Long,
      globalOffsetX: Long,
      globalOffsetY: Long,
      globalOffsetZ: Long,
      workDim: Int,
      numWorkgroupsX: Long,
      numWorkgroupsY: Long,
      numWorkgroupsZ: Long,
      workgroupSizeX: Long,
      workgroupSizeY: Long,
      workgroupSizeZ: Long,
      enqueuedWorkgroupSizeX: Long,
      enqueuedWorkgroupSizeY: Long,
      enqueuedWorkgroupSizeZ: Long,
      subgroupSize: Int,
      maxSubgroupSize: Int,
      numSubgroups: Long,
      subgroupIndex: Int,
      subgroupLocal: Int
  ) {
    val group: Array[Long]                 = Array(groupX, groupY, groupZ)
    val local: Array[Long]                 = Array(localX, localY, localZ)
    val global: Array[Long]                = Array(globalX, globalY, globalZ)
    val globalSize: Array[Long]            = Array(globalSizeX, globalSizeY, globalSizeZ)
    val globalOffset: Array[Long]          = Array(globalOffsetX, globalOffsetY, globalOffsetZ)
    val workgroupSize: Array[Long]         = Array(workgroupSizeX, workgroupSizeY, workgroupSizeZ)
    val enqueuedWorkgroupSize: Array[Long] =
      Array(enqueuedWorkgroupSizeX, enqueuedWorkgroupSizeY, enqueuedWorkgroupSizeZ)
    val numWorkgroups: Array[Long] = Array(numWorkgroupsX, numWorkgroupsY, numWorkgroupsZ)
  }
  // PC, call stack and collective suspension are the mutable state of one invocation machine.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  final class InvocationExecution(
      var invocation: Invocation,
      val values: InvocationValues,
      var currentFunction: Function,
      var pc: Int,
      var currentLabel: Int,
      var predecessor: Int,
      var instructionCount: Long,
      val calls: mutable.ArrayDeque[CallFrame],
      var suspended: Option[CollectiveRequest],
      var completed: Boolean
  ) {
    def reset(nextInvocation: Invocation, function: Function): Unit = {
      invocation = nextInvocation
      currentFunction = function
      pc = function.start + 1
      currentLabel = -1
      predecessor = -1
      instructionCount = 0L
      calls.clear()
      suspended = None
      completed = false
    }
  }
}
