package net.kurobako.gemstone.graphics

import ShaderMetadata.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionRPC.given
import net.kurobako.gemstone.rpc.Wire.*
import net.kurobako.gemstone.vm.KernelTask.*

/** Immutable execution snapshots. All storage is named by registered allocations and bounded views. Vulkan handles,
  * pointers, dynamic-state selection and render-pass object lifetime stay in the facade.
  */
object GraphicsProtocol {
  export GraphicsFormats.*
  final val PushConstantBytes: Int = 128

  enum GraphicsTopology derives Codec { case PointList, LineList, LineStrip, TriangleList, TriangleStrip, TriangleFan }
  enum GraphicsCompare derives Codec { case Never, Less, Equal, LessOrEqual, Greater, NotEqual, GreaterOrEqual, Always }
  enum GraphicsStencilOp derives Codec {
    case Keep, Zero, Replace, IncrementAndClamp, DecrementAndClamp, Invert, IncrementAndWrap, DecrementAndWrap
  }
  enum GraphicsBlendOp derives Codec     { case Add, Subtract, ReverseSubtract, Min, Max }
  enum GraphicsBlendFactor derives Codec {
    case Zero, One, SrcColor, OneMinusSrcColor, DstColor, OneMinusDstColor, SrcAlpha, OneMinusSrcAlpha,
      DstAlpha, OneMinusDstAlpha, ConstantColor, OneMinusConstantColor, ConstantAlpha, OneMinusConstantAlpha,
      SrcAlphaSaturate
  }
  enum GraphicsFilter derives Codec       { case Nearest, Linear                                                       }
  enum GraphicsAddressMode derives Codec  { case Repeat, MirroredRepeat, ClampToEdge, ClampToBorder, MirrorClampToEdge }
  enum GraphicsMipmapMode derives Codec   { case Nearest, Linear                                                       }
  enum GraphicsBorderColour derives Codec {
    case FloatTransparentBlack, IntTransparentBlack, FloatOpaqueBlack, IntOpaqueBlack, FloatOpaqueWhite, IntOpaqueWhite
  }
  enum GraphicsSwizzle derives Codec { case Identity, Zero, One, R, G, B, A }
  case class GraphicsComponents(r: GraphicsSwizzle, g: GraphicsSwizzle, b: GraphicsSwizzle, a: GraphicsSwizzle)
      derives Codec {
    def values: Vec[GraphicsSwizzle] = Vec(r, g, b, a)
  }
  enum GraphicsResolve derives Codec        { case None, SampleZero, Average, Min, Max                       }
  enum GraphicsVertexEncoding derives Codec { case Unorm, Snorm, Uint, Sint, Uscaled, Sscaled, Float, Ufloat }

  case class GraphicsRange(allocation: AllocationId, offset: Long >= 0, size: Long > 0) derives Codec
  case class GraphicsExtent(width: Int > 0, height: Int > 0, depth: Int > 0) derives Codec
  case class GraphicsOffset(x: Int, y: Int, z: Int) derives Codec
  case class GraphicsRect(x: Int, y: Int, width: Int >= 0, height: Int >= 0) derives Codec
  case class GraphicsViewport(x: Float, y: Float, width: Float, height: Float, minDepth: Float, maxDepth: Float)
      derives Codec
  case class GraphicsColour(r: Float, g: Float, b: Float, a: Float) derives Codec
  case class GraphicsImageLevel(
      extent: GraphicsExtent,
      offset: Long >= 0,
      rowPitch: Long > 0,
      slicePitch: Long > 0,
      layerPitch: Long > 0
  ) derives Codec
  case class GraphicsImage(
      memory: GraphicsRange,
      levels: NonEmpty[Vec[GraphicsImageLevel]],
      layers: Int > 0,
      samples: Int in (1, 4),
      format: GraphicsFormat,
      volume: Boolean
  ) derives Codec {
    def extent: GraphicsExtent = levels.head.extent
  }
  case class GraphicsLayers(
      level: Int >= 0,
      baseLayer: Int >= 0,
      layers: Int > 0,
      colour: Boolean,
      depth: Boolean,
      stencil: Boolean
  ) derives Codec
  case class GraphicsImageCopy(
      source: GraphicsLayers,
      sourceOffset: GraphicsOffset,
      destination: GraphicsLayers,
      destinationOffset: GraphicsOffset,
      extent: GraphicsExtent
  ) derives Codec
  case class GraphicsBufferImageCopy(
      bufferOffset: Long >= 0,
      rowLength: Int >= 0,
      imageHeight: Int >= 0,
      image: GraphicsLayers,
      offset: GraphicsOffset,
      extent: GraphicsExtent
  ) derives Codec
  case class GraphicsBlit(
      source: GraphicsLayers,
      sourceBegin: GraphicsOffset,
      sourceEnd: GraphicsOffset,
      destination: GraphicsLayers,
      destinationBegin: GraphicsOffset,
      destinationEnd: GraphicsOffset
  ) derives Codec

  case class GraphicsVertexFormat(
      encoding: GraphicsVertexEncoding,
      widths: Vec[Int > 0],
      bytes: Int > 0,
      bgra: Boolean
  ) derives Codec
  case class GraphicsVertexBuffer(
      binding: Int >= 0,
      memory: GraphicsRange,
      stride: Int >= 0,
      instanceRate: Boolean
  ) derives Codec
  case class GraphicsVertexAttribute(
      location: Int >= 0,
      binding: Int >= 0,
      offset: Int >= 0,
      format: GraphicsVertexFormat
  ) derives Codec
  case class GraphicsIndexBuffer(memory: GraphicsRange, width: Int in (2, 4)) derives Codec

  case class GraphicsSampler(
      minFilter: GraphicsFilter,
      magFilter: GraphicsFilter,
      addressU: GraphicsAddressMode,
      addressV: GraphicsAddressMode,
      addressW: GraphicsAddressMode,
      unnormalized: Boolean,
      compareEnable: Boolean,
      compareOp: GraphicsCompare,
      borderColour: GraphicsBorderColour,
      mipLodBias: Float,
      minLod: Float,
      maxLod: Float,
      mipmapMode: GraphicsMipmapMode
  ) derives Codec

  enum GraphicsDescriptor derives Codec {
    case Image(source: GraphicsImage, components: GraphicsComponents)
    case Sampler(value: GraphicsSampler)
    case Combined(source: GraphicsImage, components: GraphicsComponents, sampler: GraphicsSampler)

    def image: Option[GraphicsImage] = this match {
      case Image(source, _)       => Some(source)
      case Combined(source, _, _) => Some(source)
      case Sampler(_)             => None
    }
  }

  /** Replace one shader argument with an execution-local descriptor. */
  case class DescriptorBinding(argument: Int >= 0, descriptor: GraphicsDescriptor) derives Codec

  /** Missing argument ranges must be supplied by descriptorBindings before shader execution. */
  case class GraphicsShader(
      image: Vec[Byte],
      entry: NonEmpty[String],
      interfaces: Vec[ShaderInterface],
      arguments: Vec[Option[GraphicsRange]],
      relocations: Vec[PointerRelocation],
      pushConstants: Vec[Byte],
      earlyFragmentTests: Boolean,
      fragmentQuads: Boolean,
      descriptorBindings: Vec[DescriptorBinding] = Vec.empty
  ) derives Codec
  case class GraphicsStencil(
      fail: GraphicsStencilOp,
      pass: GraphicsStencilOp,
      depthFail: GraphicsStencilOp,
      compare: GraphicsCompare,
      compareMask: Int,
      writeMask: Int,
      reference: Int
  ) derives Codec
  case class GraphicsBlend(
      enabled: Boolean,
      sourceColour: GraphicsBlendFactor,
      destinationColour: GraphicsBlendFactor,
      colourOp: GraphicsBlendOp,
      sourceAlpha: GraphicsBlendFactor,
      destinationAlpha: GraphicsBlendFactor,
      alphaOp: GraphicsBlendOp,
      writeMask: Int
  ) derives Codec
  case class GraphicsDepthStencil(
      depthTest: Boolean,
      depthWrite: Boolean,
      compare: GraphicsCompare,
      boundsTest: Boolean,
      minBounds: Float,
      maxBounds: Float,
      stencilTest: Boolean,
      front: GraphicsStencil,
      back: GraphicsStencil
  ) derives Codec
  case class GraphicsRasterState(
      topology: GraphicsTopology,
      restart: Boolean,
      discard: Boolean,
      cullFront: Boolean,
      cullBack: Boolean,
      frontCounterClockwise: Boolean,
      biasEnabled: Boolean,
      biasConstant: Float,
      biasClamp: Float,
      biasSlope: Float,
      samples: Int in (1, 4),
      sampleMask: Int,
      sampleShading: Boolean,
      alphaToCoverage: Boolean
  ) derives Codec
  // First vertex/instance values carry unsigned 32-bit API values as raw bits.
  case class GraphicsDrawCall(
      vertices: Int >= 0,
      instances: Int >= 0,
      firstVertex: Int,
      firstInstance: Int,
      vertexOffset: Int
  ) derives Codec
  case class GraphicsIndirect(memory: GraphicsRange, count: Int >= 0, stride: Int >= 0) derives Codec
  case class GraphicsDraw(
      vertex: GraphicsShader,
      fragment: Option[GraphicsShader],
      vertexBuffers: Vec[GraphicsVertexBuffer],
      attributes: Vec[GraphicsVertexAttribute],
      index: Option[GraphicsIndexBuffer],
      call: GraphicsDrawCall,
      indirect: Option[GraphicsIndirect],
      raster: GraphicsRasterState,
      viewport: GraphicsViewport,
      scissor: GraphicsRect,
      renderArea: GraphicsRect,
      width: Int > 0,
      height: Int > 0,
      viewIndex: Int >= 0,
      colours: Vec[Option[GraphicsImage]],
      depth: Option[GraphicsImage],
      blends: Vec[GraphicsBlend],
      blendConstants: GraphicsColour,
      depthStencil: GraphicsDepthStencil
  ) derives Codec

  enum GraphicsCommand derives Codec {
    case Draw(value: GraphicsDraw)
    case ClearColour(image: GraphicsImage, area: GraphicsRect, bits: Vec[Int])
    case ClearDepthStencil(
        image: GraphicsImage,
        area: GraphicsRect,
        depth: Float,
        stencil: Int,
        clearDepth: Boolean,
        clearStencil: Boolean
    )
    case CopyImage(source: GraphicsImage, destination: GraphicsImage, regions: Vec[GraphicsImageCopy])
    case CopyBufferImage(
        buffer: GraphicsRange,
        image: GraphicsImage,
        regions: Vec[GraphicsBufferImageCopy],
        toImage: Boolean
    )
    case Blit(source: GraphicsImage, destination: GraphicsImage, regions: Vec[GraphicsBlit], linear: Boolean)
    case Resolve(source: GraphicsImage, destination: GraphicsImage, regions: Vec[GraphicsImageCopy])
    case ResolveAttachment(
        source: GraphicsImage,
        destination: GraphicsImage,
        area: GraphicsRect,
        depth: GraphicsResolve,
        stencil: GraphicsResolve
    )
  }
  case class GraphicsSubmission(commands: Vec[GraphicsCommand]) derives Codec
  case class GraphicsStatistics(occlusionSamples: Long >= 0) derives Codec
}
