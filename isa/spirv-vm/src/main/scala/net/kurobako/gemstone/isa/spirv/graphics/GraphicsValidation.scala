package net.kurobako.gemstone.isa.spirv.graphics

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.KernelTask.*
import scala.collection.mutable

/** Validate storage and invocation layouts before reserving a completion or touching host memory. */
private object GraphicsValidation {
  def submission(value: GraphicsSubmission, size: AllocationId => Option[Int > 0]): Either[String, Set[AllocationId]] =
    checked(size)(_.submission(value))
  def descriptorBindings(
      values: Vec[DescriptorBinding],
      count: Int >= 0,
      size: AllocationId => Option[Int > 0]
  ): Either[String, Set[AllocationId]] =
    checked(size)(_.descriptorBindings(values, count))
  private def checked(size: AllocationId => Option[Int > 0])(validate: GraphicsValidation => Unit)
      : Either[String, Set[AllocationId]] = {
    val validator = GraphicsValidation(size)
    try {
      validate(validator)
      Right(validator.referenced.toSet)
    } catch {
      case error: IllegalArgumentException => Left(error.getMessage)
      case _: ArithmeticException          => Left("graphics storage layout overflows")
    }
  }
}
private class GraphicsValidation(allocationSize: AllocationId => Option[Int > 0]) {
  private val referenced                                              = mutable.Set.empty[AllocationId]
  private def range(id: AllocationId, offset: Long, size: Long): Unit = {
    val length = allocationSize(id).map(_.raw.toLong)
      .getOrElse(throw IllegalArgumentException(s"graphics allocation ${id.raw} is not registered"))
    require(
      offset >= 0 && size >= 0 && offset <= length && size <= length - offset,
      "graphics view exceeds its allocation"
    )
    referenced += id
  }
  private def view(value: GraphicsRange): Unit  = range(value.allocation, value.offset, value.size)
  private def image(value: GraphicsImage): Unit = {
    require(value.levels.size <= 32, "image has too many mip levels")
    require(!value.volume || value.layers == 1, "volume image cannot have array layers")
    val block = if (value.format.compression == GraphicsCompression.None) 1 else 4
    val _     = value.levels.foldLeft(0L) { (previousEnd, level) =>
      val columns  = (level.extent.width.toLong + block - 1) / block
      val rows     = (level.extent.height.toLong + block - 1) / block
      val rowBytes =
        Math.multiplyExact(Math.multiplyExact(columns, value.samples.toLong), value.format.texelBytes.toLong)
      require(level.rowPitch >= rowBytes, "image row pitch is too small")
      require(level.slicePitch >= Math.multiplyExact(level.rowPitch, rows), "image slice pitch is too small")
      require(
        level.layerPitch >= Math.multiplyExact(level.slicePitch, level.extent.depth.toLong),
        "image layer pitch is too small"
      )
      require(level.offset >= previousEnd, "image mip levels overlap")
      val end = Math.addExact(level.offset, Math.multiplyExact(level.layerPitch, value.layers.toLong))
      require(end <= value.memory.size, "image storage exceeds its view")
      end
    }

  }
  private def checkBindings(values: Vec[DescriptorBinding], count: Int): Unit = {
    require(values.map(_.argument).distinct.size == values.size, "duplicate shader descriptor argument")
    values.foreach { binding =>
      require(binding.argument < count, "shader descriptor argument is out of range")
      ShaderDescriptors.validate(binding)
    }
  }
  private def shader(value: GraphicsShader): Unit = {
    checkBindings(value.descriptorBindings, value.arguments.size)
    val bound = value.descriptorBindings.map(_.argument.raw).toSet
    require(
      value.arguments.zipWithIndex.forall((argument, index) => argument.nonEmpty || bound.contains(index)),
      "shader argument has no storage or descriptor"
    )
    require(
      value.pushConstants.isEmpty || value.pushConstants.size == PushConstantBytes,
      s"push constant snapshot must contain $PushConstantBytes bytes"
    )
    value.interfaces.foreach { record =>
      record.fields.foreach { field =>
        require(
          field.offset <= record.size && field.size <= record.size - field.offset,
          "shader field exceeds invocation record"
        )
        if (field.location.nonEmpty) require(
          field.size % 4 == 0 && field.size / 4 <= 4 - field.qualifiers.component,
          "shader location exceeds four components"
        )
      }
    }
    value.relocations.foreach { relocation =>
      relocation.source match {
        case RelocationSource.Memory(id)  => range(id, relocation.sourceOffset, 8)
        case RelocationSource.Argument(_) =>
          throw IllegalArgumentException("graphics descriptor relocations must name allocations")
      }
      range(relocation.target, relocation.targetOffset, 0)
    }
  }

  def descriptorBindings(values: Vec[DescriptorBinding], count: Int): Unit = {
    checkBindings(values, count)
    values.foreach(_.descriptor.image.foreach { value => view(value.memory); image(value) })
  }
  def submission(submission: GraphicsSubmission): Unit = {
    submission.commands.foreach(command => GraphicsResources.foreach(command)(view(_), image(_), shader(_)))
    submission.commands.foreach {
      case GraphicsCommand.Draw(draw) =>
        draw.indirect.foreach { indirect =>
          val size = if (draw.index.nonEmpty) 20 else 16
          require(indirect.count <= 1 || indirect.stride >= size, "indirect command stride is too small")
          if (indirect.count > 0) require(
            (indirect.count.toLong - 1) * indirect.stride + size <= indirect.memory.size,
            "indirect commands exceed their view"
          )
        }
        require(draw.blends.size >= draw.colours.size, "missing attachment blend state")
        (draw.colours.toVector.flatten ++ draw.depth.toVector).foreach { attachment =>
          require(
            attachment.extent.width >= draw.width && attachment.extent.height >= draw.height &&
              attachment.samples == draw.raster.samples,
            "attachment dimensions or samples disagree with framebuffer"
          )
        }
      case GraphicsCommand.ClearColour(_, _, bits) =>
        require(bits.size == 4, "colour clear needs four components")
      case _ => ()
    }
  }
}
