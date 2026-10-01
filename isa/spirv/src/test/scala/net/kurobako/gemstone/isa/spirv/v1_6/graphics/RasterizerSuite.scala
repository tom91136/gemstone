package net.kurobako.gemstone.isa.spirv.v1_6.graphics

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.isa.spirv.v1_6.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

class RasterizerSuite extends munit.FunSuite {
  private object NoMemory extends Memory {
    override def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
      Left(Fault.Memory("unexpected rasterizer memory read"))
    override def write(region: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
      Left(Fault.Memory("unexpected rasterizer memory write"))
  }
  private val stencil = GraphicsStencil(
    fail = GraphicsStencilOp.Keep,
    pass = GraphicsStencilOp.Keep,
    depthFail = GraphicsStencilOp.Keep,
    compare = GraphicsCompare.Always,
    compareMask = 255,
    writeMask = 255,
    reference = 0
  )
  private val shader =
    GraphicsShader(
      image = Vec.empty,
      entry = "main",
      interfaces = Vec.empty,
      arguments = Vec.empty,
      relocations = Vec.empty,
      pushConstants = Vec.empty,
      earlyFragmentTests = false,
      fragmentQuads = false
    )
  private def draw(topology: GraphicsTopology, count: Int >= 0): GraphicsDraw = GraphicsDraw(
    vertex = shader,
    fragment = Some(shader),
    vertexBuffers = Vec.empty,
    attributes = Vec.empty,
    index = None,
    call = GraphicsDrawCall(count, 1, 0, 0, 0),
    indirect = None,
    raster = GraphicsRasterState(
      topology = topology,
      restart = false,
      discard = false,
      cullFront = false,
      cullBack = false,
      frontCounterClockwise = true,
      biasEnabled = false,
      biasConstant = 0,
      biasClamp = 0,
      biasSlope = 0,
      samples = 1,
      sampleMask = -1,
      sampleShading = false,
      alphaToCoverage = false
    ),
    viewport = GraphicsViewport(0, 0, 4, 4, 0, 1),
    scissor = GraphicsRect(0, 0, 4, 4),
    renderArea = GraphicsRect(0, 0, 4, 4),
    width = 4,
    height = 4,
    viewIndex = 0,
    colors = Vec.empty,
    depth = None,
    blends = Vec.empty,
    blendConstants = GraphicsColor(0, 0, 0, 0),
    depthStencil = GraphicsDepthStencil(
      depthTest = false,
      depthWrite = false,
      compare = GraphicsCompare.Always,
      boundsTest = false,
      minBounds = 0,
      maxBounds = 1,
      stencilTest = false,
      front = stencil,
      back = stencil
    )
  )
  private def vertex(x: Float, y: Float, w: Float = 1): RasterVertex =
    RasterVertex(Array(x * w, y * w, .5f * w, w), Map(0 -> Array(1f, -2f, 3f, 4f)))
  private def raster(topology: GraphicsTopology, vertices: Array[RasterVertex]): Vector[Fragment] =
    Rasterizer(draw(topology, vertices.length.unsafe[Int >= 0]), GraphicsAccess(NoMemory, TaskControl()))
      .execute(VertexStage.Output(vertices, Vector(0 -> vertices.length), vertices.length, true))

  test("graphics rejects missing arguments before execution and permits sampler descriptors without backing storage") {
    val sampler = GraphicsSampler(
      minFilter = GraphicsFilter.Nearest,
      magFilter = GraphicsFilter.Nearest,
      addressU = GraphicsAddressMode.Repeat,
      addressV = GraphicsAddressMode.Repeat,
      addressW = GraphicsAddressMode.Repeat,
      unnormalized = false,
      compareEnable = false,
      compareOp = GraphicsCompare.Never,
      borderColor = GraphicsBorderColor.FloatTransparentBlack,
      mipLodBias = 0f,
      minLod = 0f,
      maxLod = 1f,
      mipmapMode = GraphicsMipmapMode.Nearest
    )
    val missing = draw(GraphicsTopology.PointList, 1).copy(vertex = shader.copy(arguments = Vec(None)))
    def validate(value: GraphicsDraw): Either[String, Set[AllocationId]] =
      GraphicsRuntime.validate(GraphicsSubmission(Vec(GraphicsCommand.Draw(value))), _ => None)
    assert(validate(missing).isLeft)
    val supplied = missing.copy(vertex =
      missing.vertex.copy(
        descriptorBindings = Vec(DescriptorBinding(0, GraphicsDescriptor.Sampler(sampler)))
      )
    )
    assertEquals(validate(supplied), Right(Set.empty[AllocationId]))
    assert(validate(supplied.copy(vertex = supplied.vertex.copy(arguments = Vec.empty))).isLeft)
  }

  test("adjacent triangles cover a rectangle once, preserving constant varyings across unequal W") {
    val fragments = raster(
      GraphicsTopology.TriangleList,
      Array(vertex(-1, -1), vertex(1, -1, 2), vertex(-1, 1, 4), vertex(1, -1, 2), vertex(1, 1), vertex(-1, 1, 4))
    )
    assertEquals(fragments.size, 16)
    assertEquals(
      fragments.map(f => f.x -> f.y).toSet,
      (for {
        y <- 0 until 4
        x <- 0 until 4
      } yield x -> y).toSet
    )
    fragments.foreach { fragment =>
      assertEquals(fragment.varying(0).toVector, Vector(1f, -2f, 3f, 4f))
      assertEquals(fragment.z, .5f)
    }
  }
  test("width-one line coverage is independent of direction and clipped to the viewport") {
    val a       = vertex(-2, 0); val b = vertex(2, 0, 2)
    val forward = raster(GraphicsTopology.LineList, Array(a, b)).map(f => f.x -> f.y).toSet
    val reverse = raster(GraphicsTopology.LineList, Array(b, a)).map(f => f.x -> f.y).toSet
    assertEquals(forward, reverse)
    assertEquals(forward.size, 4)
    assertEquals(forward.map(_._1), Set(0, 1, 2, 3))
  }
  test("points beyond the homogeneous near and far planes produce no fragments") {
    val vertices =
      Array(RasterVertex(Array(0f, 0f, -.01f, 1f), Map.empty), RasterVertex(Array(0f, 0f, 1.01f, 1f), Map.empty))
    assertEquals(raster(GraphicsTopology.PointList, vertices), Vector.empty)
  }
}
