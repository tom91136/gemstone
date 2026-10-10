package net.kurobako.gemstone.isa.spirv.graphics

import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.isa.spirv.ImageFormats.*

/** Fixed-function tests and blending, independent of shader invocation and primitive traversal. */
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
  def blend(
      sourceValue: Array[Float],
      destinationValue: Array[Float],
      constantsValue: GraphicsColour,
      state: GraphicsBlend,
      format: GraphicsFormat
  ): Array[Float] = {
    val normalised                 = !ImageTexelCodec.floating(format.shaderFormat.code)
    val minimum                    = if (ImageTexelCodec.signed(format.shaderFormat.code)) -1f else 0f
    def clamp(value: Float): Float = if (normalised) math.max(minimum, math.min(1f, value)) else value
    val source                     = sourceValue.map(clamp(_)); val destination = destinationValue.map(clamp(_))
    val constants = Array(constantsValue.r, constantsValue.g, constantsValue.b, constantsValue.a).map(clamp(_))
    def factor(mode: GraphicsBlendFactor, lane: Int): Float = clamp(mode match {
      case GraphicsBlendFactor.Zero                  => 0f
      case GraphicsBlendFactor.One                   => 1f
      case GraphicsBlendFactor.SrcColor              => source(lane)
      case GraphicsBlendFactor.OneMinusSrcColor      => 1f - source(lane)
      case GraphicsBlendFactor.DstColor              => destination(lane)
      case GraphicsBlendFactor.OneMinusDstColor      => 1f - destination(lane)
      case GraphicsBlendFactor.SrcAlpha              => source(3)
      case GraphicsBlendFactor.OneMinusSrcAlpha      => 1f - source(3)
      case GraphicsBlendFactor.DstAlpha              => destination(3)
      case GraphicsBlendFactor.OneMinusDstAlpha      => 1f - destination(3)
      case GraphicsBlendFactor.ConstantColor         => constants(lane)
      case GraphicsBlendFactor.OneMinusConstantColor => 1f - constants(lane)
      case GraphicsBlendFactor.ConstantAlpha         => constants(3)
      case GraphicsBlendFactor.OneMinusConstantAlpha => 1f - constants(3)
      case GraphicsBlendFactor.SrcAlphaSaturate      => if (lane == 3) 1f else math.min(source(3), 1f - destination(3))
    })
    Array.tabulate(4) { lane =>
      val src = source(lane) * factor(if (lane == 3) state.sourceAlpha else state.sourceColour, lane)
      val dst = destination(lane) * factor(if (lane == 3) state.destinationAlpha else state.destinationColour, lane)
      val operation: GraphicsBlendOp = if (lane == 3) state.alphaOp else state.colourOp
      operation match {
        case GraphicsBlendOp.Add             => src + dst
        case GraphicsBlendOp.Subtract        => src - dst
        case GraphicsBlendOp.ReverseSubtract => dst - src
        case GraphicsBlendOp.Min             => math.min(source(lane), destination(lane))
        case GraphicsBlendOp.Max             => math.max(source(lane), destination(lane))
      }
    }
  }
}
