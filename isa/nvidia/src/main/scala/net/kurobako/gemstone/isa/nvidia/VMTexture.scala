package net.kurobako.gemstone.isa.nvidia

import VMIR.Texture.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.texture.*
import net.kurobako.gemstone.vm.*

/** Texture and surface access over the device's memory, which the core sampler works on through [[TexelMemory]]. */
private object VMTexture {

  /** The device memory of one lane, remembering the fault of the access that failed. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class DeviceMemory(context: VMContext, lane: WorkgroupLane) extends TexelMemory {
    var failure: Option[Fault] = None

    def read(address: Long, size: Int): Option[Array[Byte]] =
      context.device.memory.read(GuestAddress(address), size.unsafe[Int >= 0]) match {
        case Right(bytes) => Some(Vec.toArray(bytes))
        case Left(fault)  =>
          failure = Some(VMWriteBuffer.located(fault, context.pc, lane))
          None
      }

    def write(address: Long, bytes: Array[Byte]): Boolean =
      context.device.memory.write(GuestAddress(address), Vec.unsafeWrapArray(bytes)) match {
        case Right(_)    => true
        case Left(fault) =>
          failure = Some(VMWriteBuffer.located(fault, context.pc, lane))
          false
      }
  }

  /** Reads of the address an access resolves to, without touching memory. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class Locator extends TexelMemory {
    var address: Option[Long] = None

    def read(address: Long, size: Int): Option[Array[Byte]] = {
      this.address = Some(address)
      Some(Array.ofDim[Byte](size))
    }

    def write(address: Long, bytes: Array[Byte]): Boolean = false
  }

  private def memoryFault(detail: NonEmpty[String]): Fault = Fault.Memory(detail)

  private def fault(memory: DeviceMemory, failure: TexelFault): Fault = failure match {
    case TexelFault.Unmapped(address) =>
      memory.failure.getOrElse(memoryFault(s"texture access to unmapped address 0x${address.toHexString}"))
    case TexelFault.OutOfBounds(x, y, z) => memoryFault(s"surface access out of bounds at ($x, $y, $z)")
  }

  private def decodeKepler(record: Array[Byte]): Option[TextureDescriptor] = {
    val buffer                     = ByteBuffer.wrap(record).order(ByteOrder.LITTLE_ENDIAN)
    def unsigned(field: Int): Long = buffer.getInt(field) & 0xffffffffL
    val format                     = unsigned(TextureLayout.Kepler.Format)
    val limit                      = unsigned(TextureLayout.Kepler.LimitX) + 1
    for {
      kind <- ChannelKind.fromCode(((format >>> 16) & 0xff).toInt)
      texel = TexelFormat(kind, (format & 0xff).toInt, ((format >>> 8) & 0xff).toInt)
      if texel.isValid && limit % texel.texelBytes == 0 && limit <= TextureLayout.Kepler.MaxBytes
      pitch  = unsigned(TextureLayout.Kepler.Pitch)
      height = buffer.getInt(TextureLayout.Kepler.Height)
      depth  = buffer.getInt(TextureLayout.Kepler.LimitZ) + 1
    } yield TextureDescriptor(
      kind = ObjectKind.Surface,
      resource = ResourceKind.Pitch2D,
      dimensions = if (depth > 1) 3 else if (height > 1) 2 else 1,
      base = unsigned(TextureLayout.Kepler.Base) * TextureLayout.Kepler.Alignment,
      pitch = pitch,
      slicePitch = pitch * height,
      width = (limit / texel.texelBytes).toInt,
      height = height,
      depth = depth,
      format = texel,
      address = Vector.fill(3)(AddressMode.Clamp),
      filter = FilterMode.Point,
      readMode = ReadMode.ElementType,
      srgb = false,
      normalisedCoordinates = false,
      border = Vector.fill(4)(0f)
    )
  }

  private def keplerSurface(memory: DeviceMemory, handle: Long): Either[TextureTable.Failure, TextureDescriptor] = {
    val offset = handle - TextureLayout.KeplerTable.BaseAddress
    val slot   = offset / TextureLayout.Kepler.Bytes
    if (offset % TextureLayout.Kepler.Bytes != 0 || slot >= TextureLayout.Table.Entries)
      Left(TextureTable.Failure.InvalidHandle(handle))
    else
      memory
        .read(handle, TextureLayout.Kepler.Bytes)
        .toRight(TextureTable.Failure.Unreadable(handle))
        .flatMap(record => decodeKepler(record).toRight(TextureTable.Failure.NotLive(handle)))
  }

  private def resolved(memory: DeviceMemory, handle: Long, kind: ObjectKind): Either[Fault, TextureDescriptor] = {
    val result =
      if (kind == ObjectKind.Surface && handle >= TextureLayout.KeplerTable.BaseAddress) keplerSurface(memory, handle)
      else TextureTable.resolve(handle, kind, memory)
    result.left.map {
      case TextureTable.Failure.InvalidHandle(found) => memoryFault(s"invalid texture handle 0x${found.toHexString}")
      case TextureTable.Failure.NotLive(found)      => memoryFault(s"texture handle 0x${found.toHexString} is not live")
      case TextureTable.Failure.Unreadable(address) =>
        memory.failure.getOrElse(memoryFault(s"texture table unreadable at 0x${address.toHexString}"))
    }
  }

  private def words(texel: Texel, element: Element): Array[Byte] = {
    val float = element match {
      case Element.F32    => true
      case Element.Native => !texel.integral
      case _              => false
    }
    val bytes = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN)
    texel.values.foreach { value =>
      bytes.putInt(if (float) java.lang.Float.floatToRawIntBits(value.toFloat) else value.toLong.toInt)
    }
    bytes.array()
  }

  private def word(value: Int): Array[Byte] = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
    .putInt(value).array()

  private def channelDataType(d: TextureDescriptor): Int = {
    val width = d.format.bits.headOption.getOrElse(32)
    d.format.kind match {
      case ChannelKind.Float                         => if (width == 16) 0xe else 0xf
      case ChannelKind.UnsignedNormalised            => if (width == 8) 0x3 else 0x4
      case ChannelKind.SignedNormalised              => if (width == 8) 0x1 else 0x2
      case ChannelKind.Signed | ChannelKind.Unsigned =>
        val normalised = d.format.decodeMode(d.readMode) == ReadMode.NormalisedFloat
        val signed     = d.format.kind == ChannelKind.Signed
        (normalised, signed, width) match {
          case (true, true, 8)    => 0x1
          case (true, true, _)    => 0x2
          case (true, false, 8)   => 0x3
          case (true, false, _)   => 0x4
          case (false, true, 8)   => 0x8
          case (false, true, 16)  => 0x9
          case (false, true, _)   => 0xa
          case (false, false, 8)  => 0xb
          case (false, false, 16) => 0xc
          case _                  => 0xd
        }
    }
  }

  private def channelOrder(d: TextureDescriptor): Int = d.format.channels match {
    case 1 => 0x1
    case 2 => 0x3
    case _ => 0x6
  }

  private def property(d: TextureDescriptor, which: Property): Int = which match {
    case Property.Width                 => d.width
    case Property.Height                => d.height
    case Property.Depth                 => d.depth
    case Property.ChannelDataType       => channelDataType(d)
    case Property.ChannelOrder          => channelOrder(d)
    case Property.NormalisedCoordinates => if (d.normalisedCoordinates) 1 else 0
  }

  private def floats(coordinates: Vec[Long]): Vector[Double] =
    coordinates.toVector.map(bits => java.lang.Float.intBitsToFloat(bits.toInt).toDouble)

  private def detailed(values: Vector[Double], detail: Detail): (Vector[Double], LevelOfDetail) = detail match {
    case Detail.Base     => (values, LevelOfDetail.Explicit(0.0))
    case Detail.Level    => (values.init, LevelOfDetail.Explicit(values.last))
    case Detail.Gradient =>
      val axes = values.size / 3
      (values.take(axes), LevelOfDetail.Gradient(values.slice(axes, 2 * axes), values.drop(2 * axes)))
  }

  private def integers(coordinates: Vec[Long]): Vector[Long] = coordinates.toVector.map(_.toInt.toLong)

  private def position(d: TextureDescriptor, coordinates: Vec[Long]): SurfacePosition = {
    val values = integers(coordinates)
    SurfacePosition(d, values(0), values.lift(1).getOrElse(0L), values.lift(2).getOrElse(0L))
  }

  /** The bytes a texel or surface read of `operation` yields, packed as its destination registers take them. */
  def read(
      context: VMContext,
      lane: WorkgroupLane,
      operation: Operation,
      handle: Long,
      coordinates: Vec[Long]
  )
      : Either[Fault, Array[Byte]] = {
    val memory                                                                          = DeviceMemory(context, lane)
    def texel(access: TextureDescriptor => Either[TexelFault, Texel], element: Element) =
      resolved(memory, handle, ObjectKind.Texture).flatMap(access(_).left.map(fault(memory, _)).map(words(_, element)))
    operation match {
      case Operation.Sample(kind = Coordinates.Float, element = element, detail = detail) =>
        val (position, lod) = detailed(floats(coordinates), detail)
        texel(TextureSampler.sample(_, position, memory, lod), element)
      case Operation.Sample(kind = Coordinates.Integer, element = element, detail = detail) =>
        val (position, level) = integers(coordinates) match {
          case values if detail == Detail.Level => (values.init, values.last.toInt)
          case values                           => (values, 0)
        }
        texel(TextureSampler.fetch(_, position, memory, level), element)
      case Operation.Gather(component = component, element = element) =>
        texel(TextureSampler.gather(_, floats(coordinates), component.raw, memory), element)
      case Operation.Query(_, surface, which, _) =>
        resolved(memory, handle, if (surface) ObjectKind.Surface else ObjectKind.Texture)
          .map(d => word(property(d, which)))
      case Operation.Load(_, _, bytes, boundary, destinations) =>
        resolved(memory, handle, ObjectKind.Surface).flatMap { d =>
          SurfaceAccess.load(
            position(d, coordinates),
            bytes.raw * destinations.size,
            boundary,
            memory
          ).left.map(fault(memory, _))
        }
      case _ => Left(memoryFault("not a texture read"))
    }
  }

  /** A surface store of `sources`, each the low bytes of an element, as `store` describes. */
  def store(
      context: VMContext,
      lane: WorkgroupLane,
      store: Operation.Store,
      handle: Long,
      coordinates: Vec[Long],
      sources: Vec[Long]
  )
      : Either[Fault, Unit] = {
    val memory = DeviceMemory(context, lane)
    val bytes  = store.bytes.raw
    val packed = Array.ofDim[Byte](bytes * sources.size)
    sources.indices.foreach(index =>
      (0 until bytes).foreach(byte => packed(index * bytes + byte) = (sources(index) >>> (8 * byte)).toByte)
    )
    resolved(memory, handle, ObjectKind.Surface).flatMap { d =>
      SurfaceAccess.store(position(d, coordinates), packed, store.boundary, memory).left.map(fault(memory, _))
    }
  }

  /** Stages the atomic update `reduce` makes to the element at `coordinates`, unless the boundary mode drops it. */
  def reduce(
      context: VMContext,
      lane: WorkgroupLane,
      reduce: Operation.Reduce,
      handle: Long,
      coordinates: Vec[Long],
      operand: Long
  )
      : Either[Fault, Unit] = {
    val memory  = DeviceMemory(context, lane)
    val locator = Locator()
    val bytes   = reduce.bytes.raw
    resolved(memory, handle, ObjectKind.Surface).flatMap { d =>
      SurfaceAccess.load(position(d, coordinates), bytes, reduce.boundary, locator).left.map(fault(memory, _)).flatMap {
        _ =>
          locator.address.foreach { address =>
            val width: ScalarBitWidth = if (bytes == 4) 32 else 64
            val update: AtomicUpdate  = previous => reduce.operation(previous, operand, width)
            context.effects.stage(VMWrite.Atomic(lane, reduce.fetched, address, width, AtomicTarget.Device, update))
          }
          Right(())
      }
    }
  }
}
