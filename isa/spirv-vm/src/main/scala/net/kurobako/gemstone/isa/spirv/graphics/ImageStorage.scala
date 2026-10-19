package net.kurobako.gemstone.isa.spirv.graphics

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.isa.spirv.ImageFormats.*

/** Image access with explicit pitches and view bounds enforced by GraphicsAccess. */
private object ImageStorage {
  def depth16(value: Float): Int = math.round(math.max(0f, math.min(1f, value)) * 65535f)
}
private class ImageStorage(val image: GraphicsImage, memory: GraphicsAccess) {
  val format: GraphicsFormat                                                        = image.format
  def width(level: Int): Int                                                        = image.levels(level).extent.width
  def height(level: Int): Int                                                       = image.levels(level).extent.height
  def depth(level: Int): Int                                                        = image.levels(level).extent.depth
  def offset(level: Int, layer: Int, x: Int, y: Int, z: Int, sample: Int = 0): Long = {
    require(
      level >= 0 && level < image.levels.size && layer >= 0 && layer < image.layers,
      "image subresource is outside its view"
    )
    require(
      x >= 0 && x < width(level) && y >= 0 && y < height(level) && z >= 0 && z < depth(level),
      "image coordinate is outside its view"
    )
    require(sample >= 0 && sample < image.samples, "image sample is outside its view")
    val compressed = format.compression != GraphicsCompression.None
    val column     = if (compressed) x / 4 else x
    val row        = if (compressed) y / 4 else y
    val layout     = image.levels(level)
    layout.offset.raw + layer * layout.layerPitch.raw + z * layout.slicePitch.raw + row * layout.rowPitch.raw +
      (column.toLong * image.samples.raw + sample) * format.texelBytes.raw
  }
  def bytes(offset: Long): Array[Byte]              = memory.bytes(image.memory, offset, format.texelBytes)
  def store(offset: Long, bytes: Array[Byte]): Unit = memory.store(image.memory, offset, bytes)
  def colour(offset: Long): Array[Float]            = colourDouble(offset).map(_.toFloat)
  def colourDouble(offset: Long): Array[Double]     = {
    val data = bytes(offset)
    Array.tabulate(4)(lane => ImageTexelCodec.read(data, 0, format.shaderFormat.code, lane))
  }
  def colourBits(offset: Long): Array[Int] = {
    val data = bytes(offset)
    Array.tabulate(4)(lane =>
      if (ImageTexelCodec.integer(format.shaderFormat.code))
        ImageTexelCodec.readInteger(data, 0, format.shaderFormat.code, lane)
      else java.lang.Float.floatToRawIntBits(ImageTexelCodec.read(data, 0, format.shaderFormat.code, lane).toFloat)
    )
  }
  def writeColour(offset: Long, bits: Array[Int], mask: Int = 15): Unit = {
    val data = if (mask == 15) Array.ofDim[Byte](format.texelBytes) else bytes(offset)
    ImageTexelCodec.write(data, 0, format.shaderFormat.code, bits, mask)
    store(offset, data)
  }
  def readDepth(offset: Long): Float = format.depth match {
    case GraphicsDepthFormat.Unorm16 => memory.word(image.memory, offset, 2).toFloat / 65535f
    case GraphicsDepthFormat.Float32 => java.lang.Float.intBitsToFloat(memory.word(image.memory, offset, 4).toInt)
    case GraphicsDepthFormat.None    => 0f
  }
  def writeDepth(offset: Long, value: Float): Unit = format.depth match {
    case GraphicsDepthFormat.Unorm16 =>
      memory.putWord(image.memory, offset, 2, ImageStorage.depth16(value).toLong)
    case GraphicsDepthFormat.Float32 =>
      memory.putWord(image.memory, offset, 4, java.lang.Float.floatToRawIntBits(value).toLong)
    case GraphicsDepthFormat.None => ()
  }
  def readStencil(offset: Long): Int =
    if (format.stencilOffset < 0) 0 else memory.word(image.memory, offset + format.stencilOffset, 1).toInt
  def writeStencil(offset: Long, value: Int): Unit =
    if (format.stencilOffset >= 0) memory.putWord(image.memory, offset + format.stencilOffset, 1, value.toLong)
  inline def rows(area: GraphicsRect)(inline f: (Long, Int, Int, Int, Int) => Unit): Unit = {
    val firstX = math.max(0, area.x)
    val endX   = math.min(width(0).toLong, area.x.toLong + area.width).toInt
    val endY   = math.min(height(0).toLong, area.y.toLong + area.height).toInt
    if (endX > firstX) for {
      layer <- 0 until image.layers
      z     <- 0 until depth(0)
      y     <- math.max(0, area.y) until endY
    } {
      memory.checkpoint()
      f(offset(0, layer, firstX, y, z), firstX, y, layer * depth(0) + z, endX - firstX)
    }
  }
  inline def rectangle(area: GraphicsRect)(inline f: (Long, Int, Int, Int, Int) => Unit): Unit =
    rows(area) { (_, firstX, y, plane, columns) =>
      for {
        x      <- 0 until columns
        sample <- 0 until image.samples
      } f(offset(0, plane / depth(0), firstX + x, y, plane % depth(0), sample), firstX + x, y, plane, sample)
    }
}
