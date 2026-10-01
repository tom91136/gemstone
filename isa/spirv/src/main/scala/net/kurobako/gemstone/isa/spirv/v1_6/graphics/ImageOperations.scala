package net.kurobako.gemstone.isa.spirv.v1_6.graphics

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.isa.spirv.v1_6.*
import net.kurobako.gemstone.isa.spirv.v1_6.ImageFormats.*

/** Transfer and attachment operations share exactly the same storage and texel conversion as draws. */
private class ImageOperations(memory: GraphicsAccess) {
  private def storage(image: GraphicsImage): ImageStorage                        = ImageStorage(image, memory)
  private def aspect(format: GraphicsFormat, layers: GraphicsLayers): (Int, Int) =
    if (layers.color || (layers.depth && layers.stencil)) 0 -> format.texelBytes.raw
    else if (layers.stencil) format.stencilOffset -> 1
    else 0                                        -> (if (format.depth == GraphicsDepthFormat.Unorm16) 2 else 4)

  def clearColor(image: GraphicsImage, area: GraphicsRect, bits: Array[Int]): Unit = {
    require(bits.length == 4, "color clear needs four components")
    val target = storage(image)
    val pixel  = Array.ofDim[Byte](image.format.texelBytes.raw)
    ImageTexelCodec.write(pixel, 0, image.format.shaderFormat.code, bits, 15)
    target.rows(area) { (offset, _, _, _, columns) =>
      val count = Math.multiplyExact(columns, image.samples.raw)
      val row   = Array.ofDim[Byte](Math.multiplyExact(count, pixel.length))
      for (i <- 0 until count) Array.copy(pixel, 0, row, i * pixel.length, pixel.length)
      memory.store(image.memory, offset, row)
    }
  }
  def clearDepthStencil(
      image: GraphicsImage,
      area: GraphicsRect,
      depth: Float,
      stencil: Int,
      clearDepth: Boolean,
      clearStencil: Boolean
  ): Unit = {
    val target = storage(image)
    target.rows(area) { (offset, _, _, _, columns) =>
      val count = Math.multiplyExact(columns, image.samples.raw)
      val row   = memory.bytes(image.memory, offset, Math.multiplyExact(count, image.format.texelBytes.raw))
      for (i <- 0 until count) {
        val start = i * image.format.texelBytes.raw
        if (clearDepth) image.format.depth match {
          case GraphicsDepthFormat.Unorm16 =>
            val value = ImageStorage.depth16(depth)
            row(start) = value.toByte; row(start + 1) = (value >>> 8).toByte
          case GraphicsDepthFormat.Float32 => ShaderRecords.put(row, start, java.lang.Float.floatToRawIntBits(depth))
          case GraphicsDepthFormat.None    => ()
        }
        if (clearStencil && image.format.stencilOffset >= 0) row(start + image.format.stencilOffset) = stencil.toByte
      }
      memory.store(image.memory, offset, row)
    }
  }
  def copy(source: GraphicsImage, destination: GraphicsImage, region: GraphicsImageCopy): Unit = {
    val src                                  = storage(source); val dst = storage(destination)
    val (sourceAspect, size)                 = aspect(source.format, region.source)
    val (destinationAspect, destinationSize) = aspect(destination.format, region.destination)
    require(size == destinationSize && source.samples == destination.samples, "incompatible image copy")
    val step   = if (source.format.compression == GraphicsCompression.None) 1 else 4
    val planes = math.max(region.extent.depth.raw, math.max(region.source.layers.raw, region.destination.layers.raw))
    for {
      plane <- 0 until planes
      y     <- 0 until region.extent.height.raw by step
    } {
      memory.checkpoint()
      for {
        x      <- 0 until region.extent.width.raw by step
        sample <- 0 until source.samples.raw
      } {
        val so = src.offset(
          region.source.level.raw,
          region.source.baseLayer.raw + (if (source.volume) 0 else plane),
          region.sourceOffset.x + x,
          region.sourceOffset.y + y,
          region.sourceOffset.z + (if (source.volume) plane else 0),
          sample
        )
        val dest = dst.offset(
          region.destination.level.raw,
          region.destination.baseLayer.raw + (if (destination.volume) 0 else plane),
          region.destinationOffset.x + (x / step) *
            (if (destination.format.compression == GraphicsCompression.None) 1 else 4),
          region.destinationOffset.y + (y / step) *
            (if (destination.format.compression == GraphicsCompression.None) 1 else 4),
          region.destinationOffset.z + (if (destination.volume) plane else 0),
          sample
        )
        memory.store(destination.memory, dest + destinationAspect, memory.bytes(source.memory, so + sourceAspect, size))
      }
    }
  }
  def copyBuffer(
      buffer: GraphicsRange,
      image: GraphicsImage,
      region: GraphicsBufferImageCopy,
      toImage: Boolean
  ): Unit = {
    val target               = storage(image)
    val (aspectOffset, size) = aspect(image.format, region.image)
    require(image.samples.raw == 1, "buffer image copies require a single sample")
    val step        = if (image.format.compression == GraphicsCompression.None) 1 else 4
    val rowLength   = if (region.rowLength.raw == 0) region.extent.width.raw else region.rowLength.raw
    val imageHeight = if (region.imageHeight.raw == 0) region.extent.height.raw else region.imageHeight.raw
    val rowPitch    = ((rowLength.toLong + step - 1) / step) * size
    val planePitch  = rowPitch * ((imageHeight.toLong + step - 1) / step)
    for {
      layer <- 0 until region.image.layers.raw
      z     <- 0 until region.extent.depth.raw
      y     <- 0 until region.extent.height.raw by step
    } {
      memory.checkpoint()
      val io = target.offset(
        region.image.level.raw,
        region.image.baseLayer.raw + layer,
        region.offset.x,
        region.offset.y + y,
        region.offset.z + z
      )
      val bo =
        region.bufferOffset.raw + (layer.toLong * region.extent.depth.raw + z) * planePitch + (y / step) * rowPitch
      val columns = (region.extent.width.raw + step - 1) / step
      if (size == image.format.texelBytes.raw && aspectOffset == 0) {
        val bytes = Math.multiplyExact(columns, size)
        if (toImage) memory.store(image.memory, io, memory.bytes(buffer, bo, bytes))
        else memory.store(buffer, bo, memory.bytes(image.memory, io, bytes))
      } else {
        val row       = memory.bytes(image.memory, io, Math.multiplyExact(columns, image.format.texelBytes.raw))
        val bufferRow = if (toImage) memory.bytes(buffer, bo, Math.multiplyExact(columns, size))
        else Array.ofDim[Byte](Math.multiplyExact(columns, size))
        for (x <- 0 until columns)
          if (toImage) Array.copy(bufferRow, x * size, row, x * image.format.texelBytes.raw + aspectOffset, size)
          else Array.copy(row, x * image.format.texelBytes.raw + aspectOffset, bufferRow, x * size, size)
        if (toImage) memory.store(image.memory, io, row) else memory.store(buffer, bo, bufferRow)
      }
    }
  }
  def resolve(source: GraphicsImage, destination: GraphicsImage, region: GraphicsImageCopy): Unit = {
    val src = storage(source); val dst = storage(destination)
    require(destination.samples.raw == 1, "resolve destination must have one sample")
    for {
      layer <- 0 until region.source.layers.raw
      z     <- 0 until region.extent.depth.raw
      y     <- 0 until region.extent.height.raw
    } {
      memory.checkpoint()
      for (x <- 0 until region.extent.width.raw) {
        val so = src.offset(
          region.source.level.raw,
          region.source.baseLayer.raw + layer,
          region.sourceOffset.x + x,
          region.sourceOffset.y + y,
          region.sourceOffset.z + z
        )
        val dest = dst.offset(
          region.destination.level.raw,
          region.destination.baseLayer.raw + layer,
          region.destinationOffset.x + x,
          region.destinationOffset.y + y,
          region.destinationOffset.z + z
        )
        val bits = if (ImageTexelCodec.integer(source.format.shaderFormat.code)) src.colorBits(so)
        else {
          val samples =
            Array.tabulate(source.samples.raw)(s => src.colorDouble(so + s.toLong * source.format.texelBytes.raw))
          Array.tabulate(4)(lane =>
            java.lang.Float.floatToRawIntBits((samples.foldLeft(0.0)((sum, value) => sum + value(lane)) /
              source.samples.raw).toFloat)
          )
        }
        dst.writeColor(dest, bits)
      }
    }
  }
  def resolveAttachment(
      source: GraphicsImage,
      destination: GraphicsImage,
      area: GraphicsRect,
      depth: GraphicsResolve,
      stencil: GraphicsResolve
  ): Unit = {
    val src                                                        = storage(source); val dst = storage(destination)
    def reduce(values: Array[Float], mode: GraphicsResolve): Float = mode match {
      case GraphicsResolve.Average => values.sum / values.length
      case GraphicsResolve.Min     => values.min
      case GraphicsResolve.Max     => values.max
      case _                       => values(0)
    }
    dst.rectangle(area) { (dest, x, y, plane, _) =>
      val so = src.offset(0, plane / src.depth(0), x, y, plane % src.depth(0))
      if (depth != GraphicsResolve.None) dst.writeDepth(
        dest,
        reduce(
          Array.tabulate(source.samples.raw)(s => src.readDepth(so + s.toLong * source.format.texelBytes.raw)),
          depth
        )
      )
      if (stencil != GraphicsResolve.None) dst.writeStencil(
        dest,
        reduce(
          Array.tabulate(source.samples.raw)(s =>
            src.readStencil(so + s.toLong * source.format.texelBytes.raw).toFloat
          ),
          stencil
        ).toInt
      )
    }
  }

  def decode(source: GraphicsImage, destination: GraphicsImage): Unit = {
    val src = storage(source); val dst = storage(destination)
    require(
      source.extent == destination.extent && source.layers == destination.layers &&
        source.levels.map(_.extent) == destination.levels.map(_.extent),
      "decoded image layout mismatch"
    )
    for {
      level <- 0 until source.levels.size
      layer <- 0 until source.layers.raw
      z     <- 0 until src.depth(level)
      by    <- 0 until src.height(level) by 4
    } {
      memory.checkpoint()
      for (bx <- 0 until src.width(level) by 4) {
        val block = CompressedTexels.decode(source.format, src.bytes(src.offset(level, layer, bx, by, z)))
        for {
          y <- 0 until math.min(4, src.height(level) - by)
          x <- 0 until math.min(4, src.width(level) - bx)
        }
          dst.writeColor(
            dst.offset(level, layer, bx + x, by + y, z),
            Array.tabulate(4)(lane => java.lang.Float.floatToRawIntBits(block((y * 4 + x) * 4 + lane)))
          )
      }
    }
  }
  def blit(source: GraphicsImage, destination: GraphicsImage, region: GraphicsBlit, linear: Boolean): Unit = {
    val src = storage(source); val dst = storage(destination)
    require(
      source.samples.raw == 1 && destination.samples.raw == 1 &&
        destination.format.compression == GraphicsCompression.None,
      "unsupported blit image layout"
    )
    val sb         = Array(region.sourceBegin.x, region.sourceBegin.y, region.sourceBegin.z)
    val se         = Array(region.sourceEnd.x, region.sourceEnd.y, region.sourceEnd.z)
    val db         = Array(region.destinationBegin.x, region.destinationBegin.y, region.destinationBegin.z)
    val de         = Array(region.destinationEnd.x, region.destinationEnd.y, region.destinationEnd.z)
    val dimensions =
      Array(src.width(region.source.level.raw), src.height(region.source.level.raw), src.depth(region.source.level.raw))
    val compressed = scala.collection.mutable.Map.empty[Long, Array[Float]]
    def sample(layer: Int, coordinates: Array[Int]): (Long, Array[Double]) = {
      val c      = coordinates.indices.map(i => math.max(0, math.min(dimensions(i) - 1, coordinates(i)))).toArray
      val offset = src.offset(region.source.level.raw, region.source.baseLayer.raw + layer, c(0), c(1), c(2))
      val color  = if (source.format.compression == GraphicsCompression.None) {
        if (
          source.format.depth != GraphicsDepthFormat.None || source.format.stencilOffset >= 0 ||
          ImageTexelCodec.integer(source.format.shaderFormat.code)
        ) Array.emptyDoubleArray
        else src.colorDouble(offset)
      } else {
        val block = compressed.getOrElseUpdate(offset, CompressedTexels.decode(source.format, src.bytes(offset)))
        Array.tabulate(4)(lane => block(((c(1) % 4) * 4 + c(0) % 4) * 4 + lane).toDouble)
      }
      offset -> color
    }
    if (db.indices.forall(i => db(i) != de(i)))
      for {
        layer <- 0 until region.destination.layers.raw
        z     <- math.min(db(2), de(2)) until math.max(db(2), de(2))
        y     <- math.min(db(1), de(1)) until math.max(db(1), de(1))
      } {
        memory.checkpoint()
        for (x <- math.min(db(0), de(0)) until math.max(db(0), de(0))) {
          val point      = Array(x, y, z)
          val coordinate =
            point.indices.map(i => sb(i) + (point(i) + .5 - db(i)) * (se(i) - sb(i)).toDouble / (de(i) - db(i))).toArray
          val offset = dst.offset(region.destination.level.raw, region.destination.baseLayer.raw + layer, x, y, z)
          if (!linear) {
            val (input, color) = sample(layer, coordinate.map(v => math.floor(v).toInt))
            if (region.source.depth) dst.writeDepth(offset, src.readDepth(input))
            if (region.source.stencil) dst.writeStencil(offset, src.readStencil(input))
            if (region.source.color) {
              if (ImageTexelCodec.integer(source.format.shaderFormat.code)) dst.writeColor(offset, src.colorBits(input))
              else dst.writeColor(offset, color.map(v => java.lang.Float.floatToRawIntBits(v.toFloat)))
            }
          } else {
            require(
              region.source.color && !ImageTexelCodec.integer(source.format.shaderFormat.code),
              "linear filtering requires a non-integer color image"
            )
            val base     = coordinate.map(v => math.floor(v - .5).toInt)
            val fraction = coordinate.indices.map(i => coordinate(i) - .5 - base(i)).toArray
            val value    = Array.ofDim[Double](4)
            for {
              dz <- 0 to 1
              dy <- 0 to 1
              dx <- 0 to 1
            } {
              val delta      = Array(dx, dy, dz)
              val weight     = delta.indices.map(i => if (delta(i) == 0) 1 - fraction(i) else fraction(i)).product
              val (_, color) = sample(layer, base.indices.map(i => base(i) + delta(i)).toArray)
              for (lane <- 0 until 4) value(lane) += weight * color(lane)
            }
            dst.writeColor(offset, value.map(v => java.lang.Float.floatToRawIntBits(v.toFloat)))
          }
        }
      }
  }
}
