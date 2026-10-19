package net.kurobako.gemstone.isa.spirv.graphics

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.graphics.ShaderMetadata.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

private object VertexStage {
  import ShaderRecords.*
  def decode(format: GraphicsVertexFormat, source: Array[Byte]): Array[Int] = {
    val integer = format.encoding == GraphicsVertexEncoding.Uint || format.encoding == GraphicsVertexEncoding.Sint
    val result  = Array(0, 0, 0, if (integer) 1 else bits(1f))
    format.widths.zipWithIndex.foreach { (widthValue, lane) =>
      val offset  = format.widths.take(lane).foldLeft(0)((sum, width) => sum + width)
      val bounded = widthValue.refine[Int in 1 ~ 32].fold(
        _ => throw IllegalArgumentException("invalid vertex format"),
        identity
      )
      val width = bounded
      require(lane < 4, "invalid vertex format")
      val raw = if (width.raw % 8 == 0)
        (0 until width.raw / 8).foldLeft(0)((value, byte) => value | ((source(offset / 8 + byte) & 255) << (8 * byte)))
      else (word(source, 0) >>> offset) & ((1 << width) - 1)
      val signed  = raw << (32 - width) >> (32 - width)
      val decoded = format.encoding match {
        case GraphicsVertexEncoding.Uint  => raw
        case GraphicsVertexEncoding.Sint  => signed
        case GraphicsVertexEncoding.Unorm => bits(Normalised.decodeUnsigned(raw.toLong, bounded).toFloat)
        case GraphicsVertexEncoding.Snorm =>
          val signedWidth =
            width.refine[Int in 2 ~ 32].fold(
              _ => throw IllegalArgumentException("invalid snorm vertex width"),
              identity
            )
          bits(Normalised.decodeSigned(raw.toLong, signedWidth).toFloat)
        case GraphicsVertexEncoding.Uscaled => bits((raw.toLong & 0xffffffffL).toFloat)
        case GraphicsVertexEncoding.Sscaled => bits(signed.toFloat)
        case GraphicsVertexEncoding.Float   =>
          if (width == 32) raw else bits(IEEE754.binary16ToFloat(Binary16.low(raw.toLong)))
        case GraphicsVertexEncoding.Ufloat =>
          val fractionBits = width.raw - 5
          val fraction     = raw & ((1 << fractionBits) - 1)
          val exponent     = (raw >>> fractionBits) & 31
          if (exponent == 0) bits(Math.scalb(fraction.toFloat, -14 - fractionBits))
          else if (exponent == 31) 0x7f800000 | (fraction << (23 - fractionBits))
          else ((exponent + 112) << 23) | (fraction << (23 - fractionBits))
      }
      result(if (format.bgra && lane < 3) 2 - lane else lane) = decoded
    }
    result
  }
  case class RasterVertex(position: IArray[Float], varying: Map[Int, Array[Float]])
  case class Output(
      vertices: IArray[RasterVertex],
      segments: Vector[(Int, Int)],
      count: Int,
      hasPosition: Boolean
  )
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def execute(
      draw: GraphicsDraw,
      memory: GraphicsAccess,
      handler: Event => Either[Fault, Unit],
      program: VMProgram
  ): Output = {
    val indices  = mutable.ArrayBuffer.empty[Int]
    val segments = mutable.ArrayBuffer.empty[(Int, Int)]
    var begin    = 0
    for (vertex <- 0 until draw.call.vertices) {
      val raw = draw.index.fold(draw.call.firstVertex + vertex)(index =>
        memory.word(
          index.memory,
          ((draw.call.firstVertex.toLong & 0xffffffffL) + vertex) * index.width,
          index.width
        ).toInt
      )
      val restart = draw.raster.restart && draw.index.exists(index => raw == (if (index.width == 2) 65535 else -1))
      if (restart) { segments += begin -> indices.size; begin = indices.size }
      else indices += (raw + (if (draw.index.nonEmpty) draw.call.vertexOffset else 0))
    }
    segments += begin -> indices.size
    val count   = indices.size.unsafe[Int >= 0] * draw.call.instances
    val records = allocate(draw.vertex, count)
    fields(draw.vertex, ShaderDirection.Input).foreach { (variable, field) =>
      for (invocation <- 0 until count) {
        val instance = draw.call.firstInstance + invocation / indices.size
        val vertex   = indices(invocation % indices.size)
        val offset   = invocation * draw.vertex.interfaces(variable).size + field.offset
        field.builtin match {
          case Some(builtin) =>
            require(field.size == 4, "unsupported vertex builtin width")
            val value = builtin.raw match {
              case BuiltIn.VertexIndex   => vertex
              case BuiltIn.InstanceIndex => instance
              case BuiltIn.DeviceIndex   => 0
              case BuiltIn.ViewIndex     => draw.viewIndex.raw
              case _                     => throw IllegalArgumentException("unsupported vertex builtin")
            }
            put(records(variable), offset, value)
          case None => field.location.foreach { location =>
              val attribute = draw.attributes.find(
                _.location == location
              ).getOrElse(throw IllegalArgumentException("missing vertex attribute"))
              val binding = draw.vertexBuffers.find(
                _.binding == attribute.binding
              ).getOrElse(throw IllegalArgumentException("missing vertex binding"))
              val index   = if (binding.instanceRate) instance else vertex
              val address = (index.toLong & 0xffffffffL) * binding.stride + attribute.offset
              val data    =
                if (address <= binding.memory.size && attribute.format.bytes <= binding.memory.size.raw - address)
                  memory.bytes(binding.memory, address, attribute.format.bytes)
                else Array.ofDim[Byte](attribute.format.bytes)
              val value = decode(attribute.format, data)
              require(
                field.size.raw % 4 == 0 && field.qualifiers.component + field.size.raw / 4 <= 4,
                "invalid vertex field"
              )
              for (lane <- 0 until field.size.raw / 4)
                put(records(variable), offset + lane * 4, value(field.qualifiers.component + lane))
            }
        }
      }
    }
    val executor = ShaderExecutor(draw.vertex, memory, handler, program)
    executor.execute(count, records)
    val outputs     = fields(draw.vertex, ShaderDirection.Output)
    val hasPosition = outputs.exists(_._2.builtin.exists(_.raw == BuiltIn.Position))
    val vertices    = Array.tabulate(count) { invocation =>
      val position = Array.ofDim[Float](4)
      val varying  = mutable.Map.empty[Int, Array[Float]]
      outputs.foreach { (variable, field) =>
        val offset = invocation * draw.vertex.interfaces(variable).size + field.offset
        if (field.builtin.exists(_.raw == BuiltIn.Position)) {
          require(field.size == 16, "invalid position output")
          for (lane <- 0 until 4) position(lane) = float(word(records(variable), offset + lane * 4))
        }
        field.location.foreach { location =>
          val value = varying.getOrElseUpdate(location, Array.ofDim[Float](4))
          require(field.qualifiers.component + field.size.raw / 4 <= 4, "invalid varying output")
          for (lane <- 0 until field.size.raw / 4)
            value(field.qualifiers.component + lane) = float(word(records(variable), offset + lane * 4))
        }
      }
      RasterVertex(IArray.unsafeFromArray(position), varying.toMap)
    }
    Output(IArray.unsafeFromArray(vertices), segments.toVector, indices.size, hasPosition)
  }
}
