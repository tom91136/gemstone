package net.kurobako.gemstone.isa.spirv.graphics

import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.isa.spirv.*

/** Fixed-function tests, independent of shader invocation and primitive traversal. */
private object FragmentOperations {
  def compare(operation: GraphicsCompare, source: Float, destination: Float): Boolean = operation match {
    case GraphicsCompare.Never          => false
    case GraphicsCompare.Less           => source < destination
    case GraphicsCompare.Equal          => source == destination
    case GraphicsCompare.LessOrEqual    => source <= destination
    case GraphicsCompare.Greater        => source > destination
    case GraphicsCompare.NotEqual       => source != destination
    case GraphicsCompare.GreaterOrEqual => source >= destination
    case GraphicsCompare.Always         => true
  }
  def stencil(previous: Int, state: GraphicsStencil, operation: GraphicsStencilOp): Int = {
    val value = operation match {
      case GraphicsStencilOp.Keep              => previous
      case GraphicsStencilOp.Zero              => 0
      case GraphicsStencilOp.Replace           => state.reference
      case GraphicsStencilOp.IncrementAndClamp => math.min(255, previous + 1)
      case GraphicsStencilOp.DecrementAndClamp => math.max(0, previous - 1)
      case GraphicsStencilOp.Invert            => ~previous
      case GraphicsStencilOp.IncrementAndWrap  => previous + 1
      case GraphicsStencilOp.DecrementAndWrap  => previous - 1
    }
    ((previous & ~state.writeMask) | (value & state.writeMask)) & 255
  }
  def test(target: ImageStorage, offset: Long, depth: Float, front: Boolean, state: GraphicsDepthStencil): Boolean = {
    val hasDepth = target.format.depth != GraphicsDepthFormat.None
    val stored   = if (hasDepth) target.readDepth(offset) else 0f
    if (hasDepth && state.boundsTest && !(stored >= state.minBounds && stored <= state.maxBounds)) false
    else {
      val face        = if (front) state.front else state.back
      val hasStencil  = state.stencilTest && target.format.stencilOffset >= 0
      val previous    = if (hasStencil) target.readStencil(offset) else 0
      val stencilPass = !hasStencil || compare(
        face.compare,
        (face.reference & face.compareMask & 255).toFloat,
        (previous & face.compareMask & 255).toFloat
      )
      val converted = if (target.format.depth == GraphicsDepthFormat.Unorm16)
        ImageStorage.depth16(depth).toFloat / 65535f
      else math.max(0f, math.min(1f, depth))
      val depthPass = !hasDepth || !state.depthTest || compare(state.compare, converted, stored)
      if (hasStencil) target.writeStencil(
        offset,
        stencil(previous, face, if (!stencilPass) face.fail else if (!depthPass) face.depthFail else face.pass)
      )
      if (stencilPass && depthPass && hasDepth && state.depthTest && state.depthWrite)
        target.writeDepth(offset, converted)
      stencilPass && depthPass
    }
  }
}
