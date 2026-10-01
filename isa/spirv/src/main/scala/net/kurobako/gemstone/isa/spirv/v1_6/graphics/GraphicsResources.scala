package net.kurobako.gemstone.isa.spirv.v1_6.graphics

import net.kurobako.gemstone.graphics.GraphicsProtocol.*

/** One traversal for storage validation and read preparation; layout checks remain with the validator. */
private object GraphicsResources {
  def foreach(command: GraphicsCommand)(
      view: GraphicsRange => Unit,
      image: GraphicsImage => Unit,
      shader: GraphicsShader => Unit
  ): Unit = {
    def visitImage(value: GraphicsImage): Unit   = { view(value.memory); image(value) }
    def visitShader(value: GraphicsShader): Unit = {
      value.arguments.foreach(_.foreach(view))
      value.descriptorBindings.foreach(binding => binding.descriptor.image.foreach(visitImage(_)))
      shader(value)
    }
    command match {
      case GraphicsCommand.Draw(draw) =>
        visitShader(draw.vertex)
        draw.fragment.foreach(visitShader(_))
        draw.vertexBuffers.foreach(value => view(value.memory))
        draw.index.foreach(value => view(value.memory))
        draw.indirect.foreach(value => view(value.memory))
        draw.colors.foreach(_.foreach(visitImage(_)))
        draw.depth.foreach(visitImage(_))
      case GraphicsCommand.ClearColor(target, _, _)                 => visitImage(target)
      case GraphicsCommand.ClearDepthStencil(target, _, _, _, _, _) => visitImage(target)
      case GraphicsCommand.CopyBufferImage(buffer, target, _, _)    => view(buffer); visitImage(target)
      case GraphicsCommand.CopyImage(source, destination, _)        => visitImage(source); visitImage(destination)
      case GraphicsCommand.Blit(source, destination, _, _)          => visitImage(source); visitImage(destination)
      case GraphicsCommand.Resolve(source, destination, _)          => visitImage(source); visitImage(destination)
      case GraphicsCommand.ResolveAttachment(source, destination, _, _, _) =>
        visitImage(source); visitImage(destination)
    }
  }
}
