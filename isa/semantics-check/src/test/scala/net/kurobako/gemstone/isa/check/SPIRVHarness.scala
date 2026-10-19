package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.vm.*

object SPIRVHarness {
  val Cells: Long        = 1L << 20
  val CellBase: Long     = 1L << 28
  val ExchangeBase: Long = 1L << 50
  val ExchangeBytes: Int = 4096

  val CellRegion: AllocationId     = AllocationId(1L)
  val ExchangeRegion: AllocationId = AllocationId(2L)
}

/** The memory a SPIR-V instance runs against, as both its definition and the VM see it: `Cells` bytes at every address,
  * address `a` holding cell `a` modulo the cell count, each starting as a hash of the seed. Pointer operands address it
  * modulo its size, so any pointer value names a cell. The `Exchange` region carries a kernel's operands and results.
  */
class SPIRVHarness(seed: Long) extends AddressSpace with SPIRVMemory {
  import SPIRVHarness.*

  private val memory   = SeededMemory(SeededMemory.byte(seed, _), mask = Cells - 1)
  private val exchange = Array.ofDim[Byte](ExchangeBytes)

  given SPIRVMemory = this

  private def value(address: Long, size: Int): Long = memory.load(address, size)

  private def place(address: Long, value: Long, size: Int): Unit = memory.store(address, value, size)

  /** The 64-bit slot `index` of the exchange region. */
  def slot(index: Int): Long = LittleEndian.decode(exchange, index * 8, 8)

  def slot(index: Int, value: Long): Unit = LittleEndian.write(value, exchange, index * 8, 8)

  /** The cells whose bytes changed, in order. */
  def changes: Vec[(Long, Byte)] = memory.changes

  private def bytes(tpe: Type): Int = (tpe.bits + 7) / 8

  @SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
  private def typed[T](bits: Long, tpe: Type): T = (tpe.unranged match {
    case Type.Bool => bits != 0L
    case _         => tpe.represent(bits)
  }).asInstanceOf[T]

  @SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
  private def raw[T](value: T): Long = value match {
    case truth: Boolean => if (truth) 1L else 0L
    case _              => value.asInstanceOf[Long]
  }

  def load[T](address: UInt[64])(using typing: Type.Reified[T]): T =
    typed(value(address.raw, bytes(typing.tpe)), typing.tpe)
  def store[T](address: UInt[64], value: T)(using typing: Type.Reified[T]): Unit =
    place(address.raw, raw(value), bytes(typing.tpe))
  def atomicLoad[T](address: UInt[64])(using typing: Type.Reified[T]): T = {
    aligned(address, typing.tpe)
    load(address)
  }
  def atomicStore[T](address: UInt[64], value: T)(using typing: Type.Reified[T]): Unit = {
    aligned(address, typing.tpe)
    store(address, value)
  }

  private def aligned(address: UInt[64], tpe: Type): Unit =
    require((address.raw & (bytes(tpe) - 1).toLong) == 0L, s"atomic at misaligned 0x${address.raw.toHexString}")

  private def outside(region: AllocationId): Fault = Fault.InvalidRequest(s"no region $region")

  def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
    if (region == CellRegion) Right(memory.read(CellBase + offset, size))
    else if (region == ExchangeRegion && offset.raw + size.raw <= ExchangeBytes)
      Right(Vec.from(exchange.slice(offset.toInt, offset.toInt + size)))
    else Left(outside(region))

  def write(region: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
    if (region == CellRegion) Right(memory.write(CellBase + offset, bytes))
    else if (region == ExchangeRegion && offset.raw + bytes.size <= ExchangeBytes)
      Right(bytes.zipWithIndex.foreach((value, index) => exchange(offset.toInt + index) = value))
    else Left(outside(region))

  def address(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
    if (region == CellRegion) Right(GuestAddress(CellBase + offset))
    else if (region == ExchangeRegion) Right(GuestAddress(ExchangeBase + offset))
    else Left(outside(region))

  def locate(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location] = {
    val at = address.raw - ExchangeBase
    if (at >= 0 && at < ExchangeBytes) Right(AddressSpace.Location.Within(ExchangeRegion, at.unsafe[Long >= 0]))
    else Right(AddressSpace.Location.Absolute(address))
  }

  override def read(address: GuestAddress, size: Int >= 0): Either[Fault, Vec[Byte]] =
    locate(address, size).flatMap {
      case AddressSpace.Location.Within(region, offset) => read(region, offset, size)
      case AddressSpace.Location.Absolute(_)            => Right(memory.read(address.raw, size))
    }
  override def readDeferred(address: GuestAddress, size: Int >= 0): AddressSpace.ReadResult[Vec[Byte]] =
    read(address, size)
  override def write(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
    locate(address, bytes.nonNegativeSize).flatMap {
      case AddressSpace.Location.Within(region, offset) => write(region, offset, bytes)
      case AddressSpace.Location.Absolute(_)            => Right(memory.write(address.raw, bytes))
    }
  override def readValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] =
    read(address, size.widen[Int >= 0]).map(found => LittleEndian.decode(found.toArray, 0, size.widen[Int in 0 ~ 8]))
  override def writeValue(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] =
    write(address, LittleEndian.toVec(value, size.widen[Int in 0 ~ 8]))
  override def atomicModifyValue(address: GuestAddress, size: Int in 1 ~ 8)(update: Long => Long)
      : Either[Fault, Long] = readValue(address, size).flatMap(previous =>
    writeValue(address, update(previous), size).map(_ => previous)
  )
}
