package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.Bits.byteCount
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.AddressMemory
import net.kurobako.gemstone.memory.GuestAddress

import scala.collection.mutable // scalafix:ok GemstoneRuntimeCollections.mutable-collection; architectural state

private enum StateWrite extends Write {
  case Register(lane: Int, name: String, value: Long)
  case Carry(lane: Int, value: Boolean)
  case FunctionRegister(lane: Int, name: String, value: String)
  case Predicate(lane: Int, name: String, value: Boolean)
  case Parameter(lane: Int, name: String, value: Long)
  case ParameterBytes(lane: Int, name: String, offset: Int, width: Int, value: Long)
  case FunctionParameter(lane: Int, name: String, value: String)
  case ParameterBuffer(address: Long, width: Int, value: Long)
  case Shared(lane: Int, address: Long, width: Int, value: Long)
  case Local(lane: Int, address: Long, width: Int, value: Long)
  case Memory(lane: Int, address: GuestAddress, width: Int, value: Long)
  case MemoryVector(lane: Int, address: GuestAddress, width: Int, values: Col[Long])
  case AtomicSharedAdd(lane: Int, destination: String, address: Long, width: Int, value: Long)
  case AtomicSharedMin(lane: Int, destination: String, address: Long, width: Int, value: Long)
  case AtomicMemoryAdd(lane: Int, destination: String, address: GuestAddress, width: Int, value: Long)
  case AtomicMemoryMin(lane: Int, destination: String, address: GuestAddress, width: Int, value: Long)
  case AtomicMemoryAddFloat(lane: Int, destination: String, address: GuestAddress, width: Int, value: Long)
}

/** Dense storage for the register names emitted by nvcc. PTX names are stable for a kernel, so parsing the small
  * numeric register suffix on every access is cheaper than allocating and hashing a String in a mutable map.
  */
@SuppressWarnings(
  Array(
    "scalafix:DisableSyntax.var",
    "scalafix:GemstoneRuntimeCollections.mutable-collection",
    "scalafix:GemstoneNoExplicitNew.explicit-new"
  )
)
private final class RegisterFile {
  private val values = new Array[Array[Long]](96)
  private val named  = mutable.Map.empty[String, Long]

  def get(name: String): Long = slot(name) match {
    case Some(index) => getSlot(index)
    case None        => named.getOrElse(name, 0L)
  }

  def getSlot(index: Int): Long = {
    val chunk = values(index >>> 6)
    if (chunk == null) 0L else chunk(index & 63)
  }

  def apply(name: String): Long = get(name)

  def update(name: String, value: Long): Unit = slot(name) match {
    case Some(index) => updateSlot(index, value)
    case None        => named(name) = value
  }

  def updateSlot(index: Int, value: Long): Unit = {
    val chunkIndex = index >>> 6
    var chunk      = values(chunkIndex)
    if (chunk == null) {
      chunk = Array.ofDim[Long](64)
      values(chunkIndex) = chunk
    }
    chunk(index & 63) = value
  }

  private def slot(name: String): Option[Int] = {
    val value = RegisterFile.index(name)
    if (value < 0) None else Some(value)
  }
}

@SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:GemstoneNoExplicitNew.explicit-new"))
private object RegisterFile {
  private val indexCache = new java.util.concurrent.ConcurrentHashMap[String, Integer]()

  def index(name: String): Int = {
    val cached = indexCache.get(name)
    if (cached != null) return cached.intValue
    val canonical        = if (name.startsWith("%")) name else s"%$name"
    val (prefix, offset) =
      if (canonical.startsWith("%rd")) 3 -> 0
      else if (canonical.startsWith("%rs")) 3 -> 1024
      else if (canonical.startsWith("%r")) 2 -> 2048
      else if (canonical.startsWith("%fd")) 3 -> 3072
      else if (canonical.startsWith("%f")) 2 -> 4096
      else if (canonical.startsWith("%p")) 2 -> 5120
      else -1                                -> 0
    val index =
      if (prefix < 0 || canonical.length == prefix || !canonical.substring(prefix).forall(_.isDigit)) -1
      else canonical.substring(prefix).toIntOption.filter(value => value >= 0 && value < 1024).fold(-1)(_ + offset)
    val _ = indexCache.putIfAbsent(name, Integer.valueOf(index))
    index
  }

  def name(index: Int): String =
    if (index >= 0 && index < 1024) s"%rd$index"
    else if (index < 2048) s"%rs${index - 1024}"
    else if (index < 3072) s"%r${index - 2048}"
    else if (index < 4096) s"%fd${index - 3072}"
    else if (index < 5120) s"%f${index - 4096}"
    else if (index < 6144) s"%p${index - 5120}"
    else s"%r$index"
}

/** Predicate bits retain a separate defined mask so a false predicate is distinct from an undefined one. Allocate dense
  * storage only when a numbered predicate is first written: many kernels never use predicates.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:GemstoneRuntimeCollections.mutable-collection"))
private final class PredicateFile {
  private var bits: Array[Long] = null
  private val named             = mutable.Map.empty[String, Boolean]

  def get(name: String): Option[Boolean] = predicateSlot(name) match {
    case Some(index) => getSlot(index)
    case None        => named.get(name)
  }

  def getSlot(index: Int): Option[Boolean] = {
    val word = index >>> 6
    val bit  = 1L << (index & 63)
    if (bits == null || (bits(word) & bit) == 0L) None
    else Some((bits(word + 16) & bit) != 0L)
  }

  def apply(name: String): Boolean = get(name).getOrElse(false)

  def update(name: String, value: Boolean): Unit = predicateSlot(name) match {
    case Some(index) => updateSlot(index, value)
    case None        => named(name) = value
  }

  def updateSlot(index: Int, value: Boolean): Unit = {
    if (bits == null) bits = Array.ofDim[Long](32)
    val word = index >>> 6
    val bit  = 1L << (index & 63)
    bits(word) |= bit
    if (value) bits(word + 16) |= bit
    else bits(word + 16) &= ~bit
  }

  private def predicateSlot(name: String): Option[Int] = {
    val index = PredicateFile.index(name)
    if (index < 0) None else Some(index)
  }
}

private object PredicateFile {
  def index(name: String): Int =
    if (name.startsWith("%p") && name.length > 2 && name.substring(2).forall(_.isDigit))
      name.substring(2).toIntOption.filter(value => value >= 0 && value < 1024).getOrElse(-1)
    else -1
}

@SuppressWarnings(
  Array(
    "scalafix:DisableSyntax.var",
    "scalafix:GemstoneRuntimeCollections.mutable-collection",
    "scalafix:GemstoneNoExplicitNew.explicit-new"
  )
)
private final class BlockState(val laneCount: Int, initialLive: LaneMask) {
  val registers: Array[RegisterFile]                        = Array.fill(laneCount)(new RegisterFile)
  val functionRegisters: Array[mutable.Map[String, String]] =
    Array.fill(laneCount)(mutable.Map.empty[String, String])
  val predicates: Array[PredicateFile]                       = Array.fill(laneCount)(new PredicateFile)
  val parameters: Array[mutable.Map[String, Long]]           = Array.fill(laneCount)(mutable.Map.empty[String, Long])
  val functionParameters: Array[mutable.Map[String, String]] =
    Array.fill(laneCount)(mutable.Map.empty[String, String])
  val parameterBytes: Array[mutable.Map[String, mutable.Map[Int, Byte]]] =
    Array.fill(laneCount)(mutable.Map.empty[String, mutable.Map[Int, Byte]])
  val carry: Array[Boolean]                            = Array.fill(laneCount)(false)
  val parameterBuffers: mutable.Map[Long, Array[Byte]] = mutable.Map.empty
  val parameterAddresses: mutable.Map[String, Long]    = mutable.Map.empty
  private var nextParameterBuffer                      = 0x700000000L
  private val shared: mutable.Map[Long, Byte]          = mutable.Map.empty
  private val local: Array[mutable.Map[Long, Byte]]    = Array.fill(laneCount)(mutable.Map.empty[Long, Byte])
  var live: LaneMask                                   = initialLive

  def readShared(address: Long, width: Int): Long =
    LittleEndian.decode(byteCount(width))(index => shared.getOrElse(address + index.toLong, 0.toByte))

  def writeShared(address: Long, width: Int, value: Long): Unit =
    LittleEndian.write(value, byteCount(width)) { (index, byte) => shared(address + index.toLong) = byte }

  def readLocal(lane: Int, address: Long, width: Int): Long =
    LittleEndian.decode(byteCount(width))(index => local(lane).getOrElse(address + index.toLong, 0.toByte))

  def writeLocal(lane: Int, address: Long, width: Int, value: Long): Unit =
    LittleEndian.write(value, byteCount(width)) { (index, byte) => local(lane)(address + index.toLong) = byte }

  def writeParameterBytes(lane: Int, name: String, offset: Int, width: Int, value: Long): Unit = {
    val bytes = parameterBytes(lane).getOrElseUpdate(name, mutable.Map.empty)
    LittleEndian.write(value, byteCount(width)) { (index, byte) => bytes(offset + index) = byte }
  }

  def readParameterBytes(lane: Int, name: String, offset: Int, size: Int): Long =
    LittleEndian.decode(size) { index =>
      parameterBytes(lane).get(name).flatMap(_.get(offset + index)).getOrElse(0.toByte)
    }

  def allocateParameterBuffer(size: Int): Long = {
    val base = nextParameterBuffer
    parameterBuffers(base) = Array.ofDim[Byte](Math.max(size, 1))
    nextParameterBuffer = Math.addExact(base, Math.max(size, 1).toLong + 0x100L)
    base
  }

  def bindParameterBuffer(name: String, bytes: Array[Byte]): Unit = {
    val base = allocateParameterBuffer(bytes.length)
    Array.copy(bytes, 0, parameterBuffers(base), 0, bytes.length)
    parameterAddresses(name) = base
  }

  def importParameterBuffers(buffers: Map[Long, Array[Byte]]): Unit =
    buffers.foreach { case (address, bytes) =>
      parameterBuffers(address) = bytes
      nextParameterBuffer = Math.max(
        nextParameterBuffer,
        Math.addExact(address, Math.max(bytes.length, 1).toLong + 0x100L)
      )
    }

  def parameterBufferRange(address: Long, size: Int): Option[(Array[Byte], Int)] =
    parameterBuffers.iterator.collectFirst {
      case (base, bytes)
          if address >= base && address - base >= 0L && address - base <= bytes.length.toLong - size.toLong =>
        bytes -> (address - base).toInt
    }

  def readParameterBuffer(address: Long, size: Int): Option[Long] =
    parameterBufferRange(address, size).map { case (bytes, offset) => LittleEndian.decode(bytes, offset, size) }

  def readParameterBufferBytes(address: Long, size: Int): Option[Array[Byte]] =
    parameterBufferRange(address, size).map { case (bytes, offset) =>
      java.util.Arrays.copyOfRange(bytes, offset, offset + size)
    }
}

private final class ExecutionContext(
    val kernel: Kernel,
    val block: Int,
    val group: Coordinates,
    val geometry: LaunchGeometry,
    val state: BlockState,
    val pc: Pc,
    val active: LaneMask,
    val arguments: Map[String, Argument],
    val memory: AddressMemory,
    val control: ExecutionControl
) {
  def read(lane: Int, value: Value): Either[ExecutionFault, Long] = value match {
    // A PTX register read after a divergent path is unspecified.  Hardware
    // nevertheless carries a value for it, and CUDA reductions commonly use
    // such lanes only as inactive shuffle participants.  Use a deterministic
    // zero for that unspecified value so the active lane's identity is stable.
    case Value.Register(name)       => Right(state.registers(lane).get(name))
    case Value.RegisterSlot(index)  => Right(state.registers(lane).getSlot(index))
    case Value.Predicate(name)      => Right(if (state.predicates(lane).get(name).getOrElse(false)) 1L else 0L)
    case Value.PredicateSlot(index) => Right(if (state.predicates(lane).getSlot(index).getOrElse(false)) 1L else 0L)
    case Value.Function(name)       => Left(ExecutionFault.InvalidState(s"function symbol $name is not numeric"))
    case Value.Symbol(name)         =>
      state.parameterAddresses.get(
        name
      ).orElse(kernel.sharedSymbols.get(name)).orElse(kernel.globalSymbols.get(name)).toRight(
        ExecutionFault.InvalidState(s"unknown shared symbol $name")
      )
    case Value.Immediate(immediate)  => Right(immediate)
    case Value.Address(base, offset) => read(lane, base).map(_ + offset)
    case Value.LaneId                => Right((lane & 31).toLong)
    case Value.ActiveMask            => Right(active.warpBits(lane))
    case Value.LaneMaskGreaterEqual  => Right((0xffffffffL << (lane & 31)) & 0xffffffffL)
    case Value.LaneMaskGreater       => Right((0xffffffffL << ((lane & 31) + 1)) & 0xffffffffL)
    case Value.LaneMaskLessEqual     => Right((1L << ((lane & 31) + 1)) - 1L)
    case Value.LaneMaskLess          => Right((1L << (lane & 31)) - 1L)
    case Value.LaneMaskEqual         => Right(1L << (lane & 31))
    case Value.ThreadId(axis)        => Right(axis.select(geometry.localCoordinates(lane)).toLong)
    case Value.BlockId(axis)         => Right(axis.select(group).toLong)
    case Value.BlockDim(axis)        => Right(axis.select(geometry.groupSize).toLong)
    case Value.GridDim(axis)         => Right(axis.select(geometry.groupCount).toLong)
  }

  def functionName(lane: Int, value: Value): Either[ExecutionFault, String] = value match {
    case Value.Function(name) => Right(name)
    case Value.Register(name) => state.functionRegisters(lane).get(name).toRight(
        ExecutionFault.UndefinedRegister(name, pc, lane)
      )
    case Value.RegisterSlot(index) =>
      val name = RegisterFile.name(index)
      state.functionRegisters(lane).get(name).toRight(
        ExecutionFault.UndefinedRegister(name, pc, lane)
      )
    case other => Left(ExecutionFault.InvalidState(s"expected a function value, found $other"))
  }
}
