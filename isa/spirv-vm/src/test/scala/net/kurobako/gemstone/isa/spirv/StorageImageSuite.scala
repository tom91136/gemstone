package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.bits.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
private object StorageImageSuite {
  val Behaviour = "writes and atomics alias reads, fetches and size queries"

  def enumerant(kind: OperandKind, value: Int): String =
    Instruction.operandKinds(kind).enumerants.find(_.value == value).fold(value.toString)(_.name.raw)

  def stage(model: Int): String = enumerant(OperandKind.ExecutionModel, model)

  enum Encoding derives CanEqual { case Float, Unorm, Snorm, Ufloat, Sint, Uint }

  val Inputs: Seq[Float] = Seq(-1f, 0.25f, 1f, 2f)
  val Lanes: Seq[String] = Seq("%red", "%green", "%blue", "%alpha")

  case class Format(code: Int, encoding: Encoding, widths: Int*) {
    def name: String     = enumerant(OperandKind.ImageFormat, code)
    def signed: Boolean  = encoding == Encoding.Sint
    def integer: Boolean = signed || encoding == Encoding.Uint
    def texelBytes: Int  = widths.sum / 8
    def input: Vec[Long] = encoding match {
      case Encoding.Sint => Vec(-123456L, -1L, Int.MaxValue.toLong, Int.MinValue.toLong)
      case Encoding.Uint => Vec(7L, 42L, 0xffffffffL, 65536L)
      case _             => Vec.from(Inputs.map(single))
    }
    def literal(value: Long): String = if (signed) value.toString else s"0x${(value & 0xffffffffL).toHexString}"
    def literals: Seq[String]        = if (integer) input.map(value => literal(value)) else Inputs.map(_.toString)
    def expected(atomic: Boolean): Vec[Long] = Vec.tabulate(4) { lane =>
      val one = lane == 3
      if (lane >= widths.size) {
        if (integer) { if (one) 1L else 0L }
        else single(if (one) 1f else 0f)
      } else {
        val bits                                           = widths(lane)
        val value                                          = Inputs(lane)
        def normalised(minimum: Float, maximum: Int): Long =
          single(Math.round(value.max(minimum).min(1f) * maximum) / maximum.toFloat)
        def saturated(low: Long, high: Long): Long =
          (input(lane).max(low).min(high) + (if (atomic) 1L else 0L)) & 0xffffffffL
        encoding match {
          case Encoding.Float  => single(value)
          case Encoding.Ufloat => single(value.max(0f))
          case Encoding.Unorm  => normalised(0f, (1 << bits) - 1)
          case Encoding.Snorm  => normalised(-1f, (1 << (bits - 1)) - 1)
          case Encoding.Sint   => saturated(-(1L << (bits - 1)), (1L << (bits - 1)) - 1)
          case Encoding.Uint   => saturated(0L, (1L << bits) - 1)
        }
      }
    }
  }

  val Formats: Seq[Format] = Seq(
    Format(ImageFormat.Rgba32f, Encoding.Float, 32, 32, 32, 32),
    Format(ImageFormat.Rg32f, Encoding.Float, 32, 32),
    Format(ImageFormat.R32f, Encoding.Float, 32),
    Format(ImageFormat.Rgba16f, Encoding.Float, 16, 16, 16, 16),
    Format(ImageFormat.Rg16f, Encoding.Float, 16, 16),
    Format(ImageFormat.R16f, Encoding.Float, 16),
    Format(ImageFormat.R11fG11fB10f, Encoding.Ufloat, 11, 11, 10),
    Format(ImageFormat.Rgba8, Encoding.Unorm, 8, 8, 8, 8),
    Format(ImageFormat.Rg8, Encoding.Unorm, 8, 8),
    Format(ImageFormat.R8, Encoding.Unorm, 8),
    Format(ImageFormat.Rgb10A2, Encoding.Unorm, 10, 10, 10, 2),
    Format(ImageFormat.Rgba8Snorm, Encoding.Snorm, 8, 8, 8, 8),
    Format(ImageFormat.Rg8Snorm, Encoding.Snorm, 8, 8),
    Format(ImageFormat.R8Snorm, Encoding.Snorm, 8),
    Format(ImageFormat.Rgba16, Encoding.Unorm, 16, 16, 16, 16),
    Format(ImageFormat.Rg16, Encoding.Unorm, 16, 16),
    Format(ImageFormat.R16, Encoding.Unorm, 16),
    Format(ImageFormat.Rgba16Snorm, Encoding.Snorm, 16, 16, 16, 16),
    Format(ImageFormat.Rg16Snorm, Encoding.Snorm, 16, 16),
    Format(ImageFormat.R16Snorm, Encoding.Snorm, 16),
    Format(ImageFormat.Rgba32i, Encoding.Sint, 32, 32, 32, 32),
    Format(ImageFormat.Rgba16i, Encoding.Sint, 16, 16, 16, 16),
    Format(ImageFormat.Rgba8i, Encoding.Sint, 8, 8, 8, 8),
    Format(ImageFormat.Rg32i, Encoding.Sint, 32, 32),
    Format(ImageFormat.Rg16i, Encoding.Sint, 16, 16),
    Format(ImageFormat.Rg8i, Encoding.Sint, 8, 8),
    Format(ImageFormat.R32i, Encoding.Sint, 32),
    Format(ImageFormat.R16i, Encoding.Sint, 16),
    Format(ImageFormat.R8i, Encoding.Sint, 8),
    Format(ImageFormat.Rgba32ui, Encoding.Uint, 32, 32, 32, 32),
    Format(ImageFormat.Rgba16ui, Encoding.Uint, 16, 16, 16, 16),
    Format(ImageFormat.Rgba8ui, Encoding.Uint, 8, 8, 8, 8),
    Format(ImageFormat.Rg32ui, Encoding.Uint, 32, 32),
    Format(ImageFormat.Rg16ui, Encoding.Uint, 16, 16),
    Format(ImageFormat.Rg8ui, Encoding.Uint, 8, 8),
    Format(ImageFormat.R32ui, Encoding.Uint, 32),
    Format(ImageFormat.R16ui, Encoding.Uint, 16),
    Format(ImageFormat.R8ui, Encoding.Uint, 8),
    Format(ImageFormat.Rgb10a2ui, Encoding.Uint, 10, 10, 10, 2)
  )

  // one format per family and lane count, every integer width and signedness, and both atomic formats
  val Executed: Seq[Format] = Seq(
    ImageFormat.Rgba32f,
    ImageFormat.Rg32f,
    ImageFormat.R32f,
    ImageFormat.Rgba16f,
    ImageFormat.R16f,
    ImageFormat.R11fG11fB10f,
    ImageFormat.Rgba8,
    ImageFormat.Rg8,
    ImageFormat.Rgb10A2,
    ImageFormat.Rgba8Snorm,
    ImageFormat.R8Snorm,
    ImageFormat.Rgba16,
    ImageFormat.Rg16Snorm,
    ImageFormat.Rgba32i,
    ImageFormat.Rg16i,
    ImageFormat.R8i,
    ImageFormat.R32i,
    ImageFormat.Rgba8ui,
    ImageFormat.R16ui,
    ImageFormat.Rg32ui,
    ImageFormat.R32ui,
    ImageFormat.Rgb10a2ui
  ).map(format(_))

  def format(code: Int): Format = Formats.find(_.code == code).get

  def encode(format: Format, lanes: Vec[Long]): Array[Byte] = {
    val bytes = Array.ofDim[Byte](format.texelBytes)
    ImageTexelCodec.write(bytes, 0, format.code, lanes.map(_.toInt).toArray, 15)
    bytes
  }

  def decode(bytes: Array[Byte], offset: Int, format: Format): Vec[Long] = Vec.tabulate(4)(lane =>
    if (format.integer) ImageTexelCodec.readInteger(bytes, offset, format.code, lane).toLong & 0xffffffffL
    else single(ImageTexelCodec.read(bytes, offset, format.code, lane).toFloat)
  )

  enum Position(label: String) derives CanEqual {
    case First    extends Position("first")
    case Last     extends Position("last")
    case Negative extends Position("negative")
    case PastEnd  extends Position("past-end")
    def apply(extent: Int): Int = this match {
      case First    => 0
      case Last     => extent - 1
      case Negative => -1
      case PastEnd  => extent
    }
    override def toString: String = label
  }

  val Positions: Seq[Position] = Position.values.toSeq

  val Models: Seq[Int] = Seq(ExecutionModel.Vertex, ExecutionModel.Fragment, ExecutionModel.GLCompute)

  /** `slices` is the layer count, or the depth of a volume; cube arrays hold two cubes. */
  case class Shape(dim: Int, arrayed: Boolean = false, width: Int = 3, wrapped: Boolean = false) {
    def volume: Boolean  = dim == Dim.Value3D
    def layered: Boolean = arrayed || volume
    def height: Int      = if (dim == Dim.Value1D || dim == Dim.Buffer) 1 else if (dim == Dim.Cube) width else 2
    def slices: Int      = if (dim == Dim.Cube) 12 else if (layered) 3 else 1
    // cube images cannot be fetched, so the sampled view of a cube array is a 2D array over the same texels
    def fetchDim: Int                         = if (dim == Dim.Cube) Dim.Value2D else dim
    def sampledExtent: Vec[Long]              = extent(slices)
    def storageExtent: Vec[Long]              = extent(if (dim == Dim.Cube) slices / 6 else slices)
    private def extent(depth: Int): Vec[Long] =
      Vec.from((Seq(width) ++ Option.when(height > 1)(height) ++ Option.when(layered)(depth)).map(_.toLong))
    def label: String =
      (Seq(enumerant(OperandKind.Dim, dim)) ++ Option.when(arrayed)("arrayed") ++
        Option.when(width > 16384)(s"width $width") ++ Option.when(wrapped)("sampled wrapper")).mkString(" ")
  }

  val LayeredShapes: Seq[Shape] = Seq(
    Shape(Dim.Value1D, arrayed = true),
    Shape(Dim.Value2D, arrayed = true),
    Shape(Dim.Value3D),
    Shape(Dim.Cube, arrayed = true)
  )

  val FlatShapes: Seq[Shape] = Seq(
    Shape(Dim.Value1D),
    Shape(Dim.Value2D),
    Shape(Dim.Buffer),
    Shape(Dim.Buffer, wrapped = true),
    Shape(Dim.Buffer, width = 20000)
  )

  // pairwise rarely puts both coordinates in bounds; these pin in-bounds layers, volumes and atomics per shape
  val LayeredInBounds: Seq[(Int, Shape, Format, Position, Position)] = Seq(
    (ExecutionModel.GLCompute, LayeredShapes(0), format(ImageFormat.R32ui), Position.Last, Position.Last),
    (ExecutionModel.Fragment, LayeredShapes(0), format(ImageFormat.Rgba8), Position.First, Position.Last),
    (ExecutionModel.Vertex, LayeredShapes(2), format(ImageFormat.Rgba8Snorm), Position.Last, Position.Last),
    (ExecutionModel.GLCompute, LayeredShapes(2), format(ImageFormat.R32i), Position.First, Position.Last),
    (ExecutionModel.Fragment, LayeredShapes(2), format(ImageFormat.Rg16i), Position.Last, Position.First),
    (ExecutionModel.Vertex, LayeredShapes(3), format(ImageFormat.R32ui), Position.Last, Position.Last),
    (ExecutionModel.GLCompute, LayeredShapes(3), format(ImageFormat.Rgb10A2), Position.First, Position.Last),
    (ExecutionModel.Fragment, LayeredShapes(3), format(ImageFormat.Rgba32i), Position.Last, Position.Last)
  )

  val FlatInBounds: Seq[(Int, Shape, Format, Position)] = Seq(
    (ExecutionModel.GLCompute, Shape(Dim.Buffer), format(ImageFormat.R32ui), Position.Last),
    (ExecutionModel.Vertex, Shape(Dim.Buffer, wrapped = true), format(ImageFormat.R32i), Position.Last),
    (ExecutionModel.Fragment, Shape(Dim.Buffer, wrapped = true), format(ImageFormat.Rgba8ui), Position.First),
    (ExecutionModel.GLCompute, Shape(Dim.Value2D), format(ImageFormat.Rgba32f), Position.Last),
    (ExecutionModel.GLCompute, Shape(Dim.Value2D), format(ImageFormat.Rgba16), Position.Last),
    (ExecutionModel.Fragment, Shape(Dim.Value1D), format(ImageFormat.Rg16Snorm), Position.First),
    (ExecutionModel.GLCompute, Shape(Dim.Buffer, width = 20000), format(ImageFormat.Rg32ui), Position.Last),
    (ExecutionModel.Vertex, Shape(Dim.Buffer, width = 20000, wrapped = true), format(ImageFormat.R16ui), Position.Last)
  )
}

class StorageImageSuite extends munit.FunSuite {
  import StorageImageSuite.*

  for (format <- Formats) test(s"${format.name} texel codec saturates, rounds and widens lanes") {
    assertEquals(ImageTexelCodec.texelBytes(format.code), format.texelBytes)
    assertEquals(decode(encode(format, format.input), 0, format), format.expected(atomic = false))
  }

  test("texel codec stores 8- and 16-bit channels in memory order") {
    for (
      (code, packed) <- Seq(
        ImageFormat.Rgba8       -> 0xffff4000L,
        ImageFormat.Rgba8Snorm  -> 0x7f7f2081L,
        ImageFormat.Rgba8ui     -> 0xffff2a07L,
        ImageFormat.Rgba8i      -> 0x807fff80L,
        ImageFormat.Rgba16      -> 0xffffffff40000000L,
        ImageFormat.Rgba16Snorm -> 0x7fff7fff20008001L
      )
    ) {
      val format = Formats.find(_.code == code).get
      assertEquals(
        LittleEndian.decode(encode(format, format.input), 0, format.texelBytes.unsafe[Int in 0 ~ 8]),
        packed,
        format.name
      )
    }
  }

  test("SPIR-V 1.6 rejects a sampled image over a Buffer image") {
    val module = spv"""
      OpCapability Shader
      OpCapability SampledBuffer
      OpMemoryModel Logical GLSL450
      %float = OpTypeFloat 32
      %image = OpTypeImage %float Buffer 0 0 0 1 Unknown
      %sampled = OpTypeSampledImage %image
    """
    assertEquals(
      LoadedProgram.loadUnchecked(module).map(_ => ()),
      Left(Fault.InvalidRequest("OpTypeSampledImage cannot wrap a Buffer image in SPIR-V 1.6"))
    )
  }

  private val layered = pairwise(Models, LayeredShapes, Executed, Positions, Positions)
  for ((model, shape, format, x, layer) <- layered ++ LayeredInBounds.filterNot(layered.toSet))
    test(s"${stage(model)} ${shape.label} ${format.name}, $x texel, $layer layer: $Behaviour") {
      check(model, shape, format, x, layer)
    }

  private val flat = pairwise(Models, FlatShapes, Executed, Positions)
  for ((model, shape, format, x) <- flat ++ FlatInBounds.filterNot(flat.toSet))
    test(s"${stage(model)} ${shape.label} ${format.name}, $x texel: $Behaviour") {
      check(model, shape, format, x, Position.First)
    }

  private def check(model: Int, shape: Shape, format: Format, x: Position, layer: Position): Unit = {
    val atomic      = format.code == ImageFormat.R32i || format.code == ImageFormat.R32ui
    val texelBuffer = shape.dim == Dim.Buffer
    val component   = if (format.integer) "%int" else "%float"
    val arrayed     = if (shape.arrayed) 1 else 0
    val coordinate  = Seq("%x" -> x(shape.width)) ++ Option.when(shape.height > 1)("%y" -> (shape.height - 1)) ++
      Option.when(shape.layered)("%layer" -> layer(shape.slices))
    val (coordinates, where) = if (coordinate.size == 1) ("%int", "%x") else ("%coordinates", "%where")
    val ids                  = coordinate.map(_._1)
    val constants            = coordinate.map((id, value) => s"$id = OpConstant %int ${format.literal(value)}") ++
      Option.when(ids.size > 1)(s"%where = OpConstantComposite %coordinates ${ids.mkString(" ")}") ++
      Lanes.zip(format.literals).map((id, value) => s"$id = OpConstant $component $value")
    val sampled =
      if (shape.wrapped)
        Seq("%combined_view = OpLoad %combined %sampled", "%sampled_view = OpImage %sampled_image %combined_view")
      else Seq("%sampled_view = OpLoad %sampled_image %sampled")
    val mode = model match {
      case ExecutionModel.Fragment  => "OpExecutionMode %main OriginUpperLeft"
      case ExecutionModel.GLCompute => "OpExecutionMode %main LocalSize 1 1 1"
      case _                        => ""
    }
    val module = spv"""
      ${if (shape.wrapped) "; Version: 1.5" else ""}
      $vulkan
      OpCapability Image1D
      OpCapability Sampled1D
      OpCapability ImageBuffer
      OpCapability SampledBuffer
      OpCapability ImageCubeArray
      OpCapability SampledCubeArray
      OpCapability StorageImageExtendedFormats
      OpEntryPoint ${stage(model)} %main "main" %storage %sampled %out
      $mode
      OpDecorate %storage DescriptorSet 0
      OpDecorate %storage Binding 0
      OpDecorate %sampled DescriptorSet 0
      OpDecorate %sampled Binding 1
      OpDecorate %out DescriptorSet 0
      OpDecorate %out Binding 2
      OpDecorate %results Block
      OpMemberDecorate %results 0 Offset 0
      OpMemberDecorate %results 1 Offset 16
      OpMemberDecorate %results 2 Offset 32
      OpMemberDecorate %results 3 Offset 48
      %void = OpTypeVoid
      %int = OpTypeInt 32 ${if (format.signed) 1 else 0}
      %float = OpTypeFloat 32
      %vec4 = OpTypeVector $component 4
      ${if (coordinate.size == 1) "" else s"%coordinates = OpTypeVector %int ${coordinate.size}"}
      %storage_image = OpTypeImage $component ${enumerant(OperandKind.Dim, shape.dim)} 0 $arrayed 0 2 ${format.name}
      %sampled_image = OpTypeImage $component ${enumerant(OperandKind.Dim, shape.fetchDim)} 0 $arrayed 0 1 Unknown
      ${if (shape.wrapped) "%combined = OpTypeSampledImage %sampled_image" else ""}
      %storage_ptr = OpTypePointer UniformConstant %storage_image
      %sampled_ptr = OpTypePointer UniformConstant ${if (shape.wrapped) "%combined" else "%sampled_image"}
      %results = OpTypeStruct %vec4 %vec4 $coordinates $coordinates
      %results_ptr = OpTypePointer StorageBuffer %results
      %vec4_ptr = OpTypePointer StorageBuffer %vec4
      %size_ptr = OpTypePointer StorageBuffer $coordinates
      %texel_ptr = OpTypePointer Image %int
      %fn = OpTypeFunction %void
      %storage = OpVariable %storage_ptr UniformConstant
      %sampled = OpVariable %sampled_ptr UniformConstant
      %out = OpVariable %results_ptr StorageBuffer
      %zero = OpConstant %int 0
      %one = OpConstant %int 1
      %two = OpConstant %int 2
      %three = OpConstant %int 3
      ${constants.mkString("\n")}
      %texel = OpConstantComposite %vec4 ${Lanes.mkString(" ")}
      %main = OpFunction %void None %fn
      %entry = OpLabel
      %storage_view = OpLoad %storage_image %storage
      ${sampled.mkString("\n")}
      OpImageWrite %storage_view $where %texel
      ${if (atomic) s"%pointer = OpImageTexelPointer %texel_ptr %storage $where %zero" else ""}
      ${if (atomic) "%previous = OpAtomicIAdd %int %pointer %one %zero %one" else ""}
      %read = OpImageRead %vec4 %storage_view $where
      %fetched = OpImageFetch %vec4 %sampled_view $where ${if (texelBuffer) "" else "Lod %zero"}
      %storage_size = OpImageQuerySize $coordinates %storage_view
      %sampled_size = ${
        if (texelBuffer) s"OpImageQuerySize $coordinates %sampled_view"
        else s"OpImageQuerySizeLod $coordinates %sampled_view %zero"
      }
      %read_out = OpAccessChain %vec4_ptr %out %zero
      %fetched_out = OpAccessChain %vec4_ptr %out %one
      %storage_size_out = OpAccessChain %size_ptr %out %two
      %sampled_size_out = OpAccessChain %size_ptr %out %three
      OpStore %read_out %read
      OpStore %fetched_out %fetched
      OpStore %storage_size_out %storage_size
      OpStore %sampled_size_out %sampled_size
      OpReturn
      OpFunctionEnd
    """
    val payload    = shape.width * shape.height * shape.slices * format.texelBytes
    val level      = ImageLevel(shape.width, shape.height)
    val view       = ImageView(format.code, Vec(level), payload.toLong, shape.slices, shape.volume)
    val memory     = RegionMemory()
    val size       = view.size.toLong.refine[Long > 0].value
    val descriptor = memory.allocate(size)
    val texels     = memory.allocate((payload + 8).toLong.refine[Long > 0].value)
    val out        = memory.allocate(64L)
    writeWords(memory, texels, 0, 0xfeedL)
    writeWords(memory, texels, 4 + payload, 0xbeefL)
    val relocation = writeImage(memory, descriptor, 0, view, texels, 4L)
    val bound      = buffer(descriptor, range = Some(size))
    val arguments  = Vec(bound, bound, buffer(out, range = Some(64L)))
    assertEquals(run(module, "main", memory, arguments, relocations = Vec(relocation)), Right(()))

    val selected = coordinate.map(_._2)
    val valid    = selected.head >= 0 && selected.head < shape.width &&
      (!shape.layered || (selected.last >= 0 && selected.last < shape.slices))
    val expected = if (valid) format.expected(atomic) else Vec.fill(4)(0L)
    assertEquals(words(memory, out, offset = 0, count = 4), expected, "read")
    assertEquals(words(memory, out, offset = 16, count = 4), expected, "fetched")
    assertEquals(words(memory, out, 32, coordinate.size), shape.storageExtent, "storage size")
    assertEquals(words(memory, out, 48, coordinate.size), shape.sampledExtent, "sampled size")
    assertEquals(word(memory, texels, 0), 0xfeedL)
    assertEquals(word(memory, texels, 4 + payload), 0xbeefL)
    val stored = memory.bytes(texels).slice(4, 4 + payload)
    if (!valid) assert(stored.forall(_ == 0))
    else {
      val texel = ((if (shape.layered) selected.last else 0) * shape.height + shape.height - 1) * shape.width +
        selected.head
      val offset = texel * format.texelBytes
      assertEquals(stored.slice(offset, offset + format.texelBytes), Vec.from(encode(format, expected)))
      assert((stored.take(offset) ++ stored.drop(offset + format.texelBytes)).forall(_ == 0))
    }
  }
}
