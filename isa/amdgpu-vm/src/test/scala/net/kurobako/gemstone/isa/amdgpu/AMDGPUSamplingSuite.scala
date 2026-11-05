package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.language.implicitConversions

/** Image sampling through sampler descriptors: address modes, filters, levels, offsets, depth comparison, gathers and
  * image kinds. Reference values follow the ISA documents' filtering rules, with filter weights of 1/256 steps, and the
  * descriptors Mesa builds; the sample instruction under test is Gemstone's assembly of LLVM's mnemonic.
  */
class AMDGPUSamplingSuite extends Suite {

  private val Identity = 0xfacL

  // a tagged lane, as Scala.js cannot tell a whole-number Float from an Int at runtime
  private enum Lane {
    case Bits(value: Int)
    case Real(value: Float)
  }

  private given Conversion[Int, Lane]   = Lane.Bits(_)
  private given Conversion[Float, Lane] = Lane.Real(_)

  private def float(value: Float): Int = java.lang.Float.floatToRawIntBits(value)

  private def toFloat(bits: Int): Float = java.lang.Float.intBitsToFloat(bits)

  private def run(target: Target, lanes: Int, data: Seq[Int], program: Vec[Byte])(using
      munit.Location
  )
      : Vec[Int] = {
    val memory = RegionMemory()
    val input  = AllocationId(1L)
    val output = AllocationId(2L)
    memory.allocate(input, 8192)
    memory.allocate(output, 4096)
    data.zipWithIndex.foreach((value, index) => memory.putInt(input, (index * 4).unsafe[Int >= 0], value))
    val wave32 = target.isa.isRDNA
    val source = codeKernel(
      target = target,
      groupSegmentSize = 4096,
      kernelCodeProperties = if (wave32) KernelCodeProperty.ENABLE_WAVEFRONT_SIZE32 else 0,
      code = program
    )
    val task = KernelTask.validated("kernel", Dimensions(lanes.unsafe[Int > 0], 1, 1), Dimensions.One, Vec.empty).value
    assertEquals(executeKernel(compileKernel(source), task, memory), Right(()))
    Vec.tabulate(1024)(index => memory.getInt(output, (index * 4).unsafe[Int >= 0]))
  }

  private def assemble(target: Target, text: String)(using munit.Location): Vec[Byte] = {
    val waveSize = if (target.isa.isRDNA) 32 else 64
    asm.Assembler.encode(asm.Parser.parse(text).value, TargetMode(target, waveSize.unsafe[Int in (32, 64)])).value
  }

  private def address(allocation: Long): Long = RegionMemory.Base + (allocation - 1L) * RegionMemory.Stride

  private def moves(first: Int, words: Seq[Long]): String =
    words.zipWithIndex.map((word, index) => f"s_mov_b32 s${first + index}, 0x$word%x").mkString("", "\n", "\n")

  private val output: String = {
    val out = address(2L)
    f"s_mov_b32 s14, 0x${out & 0xffffffffL}%x\ns_mov_b32 s15, 0x${out >>> 32}%x\n"
  }

  private def store(target: Target): String = target.isa match {
    case ISA.RDNA3 | ISA.RDNA3_5 | ISA.RDNA4 => "global_store_b128"
    case _                                   => "global_store_dwordx4"
  }

  private val clear = "v_mov_b32 v2, -1\nv_mov_b32 v3, -1\nv_mov_b32 v4, -1\nv_mov_b32 v5, -1\n"

  /** `setup` for each lane, `instruction`, then v[2:5] stored 16 bytes a lane at the lane's index kept in v8. */
  private def sampleProgram(target: Target, setup: String, instruction: String)(using munit.Location): Vec[Byte] =
    assemble(target, "v_mov_b32 v8, v0\n" + setup + instruction + "\n") ++ assemble(
      target,
      s"""v_lshlrev_b32 v6, 4, v8
         |${store(target)} v6, v[2:5], s[14:15]
         |s_endpgm
         |""".stripMargin
    )

  private val gfx1030 = Target("gfx1030")

  private val Float32 = 22L
  private val Unorm8  = 56L

  /** A GFX10 linear image of the input: `kind` as SQ_RSRC_IMG numbers it, `levels` mip levels. */
  private def image(
      format: Long,
      width: Long,
      height: Long,
      pitch: Long,
      kind: Long = 9,
      depth: Long = 0,
      levels: Long = 1,
      selects: Long = Identity
  ): Seq[Long] = {
    val base = address(1L) >>> 8
    Seq(
      base & 0xffffffffL,
      (base >>> 32) | (format << 20) | (((width - 1) & 3) << 30),
      ((width - 1) >>> 2) | ((height - 1) << 14),
      selects | ((levels - 1) << 16) | (kind << 28),
      if (kind == 8 || kind == 9) pitch - 1 else depth,
      0,
      0,
      0
    )
  }

  private def fixed(value: Float): Long = Math.round(value * 256f).toLong

  /** A sampler: clamp modes, XY and Z filters (0 point, 1 bilinear; Z 1 point, 2 linear), mip filter, level range. */
  private def sampler(
      clamp: Long = 2,
      filter: Long = 0,
      mip: Long = 0,
      zFilter: Long = 1,
      compare: Long = 7,
      minLod: Float = 0f,
      maxLod: Float = 0f,
      bias: Float = 0f,
      unnormalised: Boolean = true,
      border: Long = 0,
      reduction: Long = 0,
      wide: Boolean = false
  ): Seq[Long] = Seq(
    clamp | (clamp << 3) | (clamp << 6) | (compare << 12) | ((if (unnormalised) 1L else 0L) << 15) | (reduction << 29),
    if (wide) fixed(minLod) | (fixed(maxLod) << 13) else fixed(minLod) | (fixed(maxLod) << 12),
    (fixed(bias) & 0x3fffL) | (filter << 20) | (filter << 22) | (zFilter << 24) | (mip << 26),
    border << 30
  )

  private val image2x2 = Seq(10f, 20f, 30f, 40f).map(float(_))

  /** Registers v0 up from the per-lane `values`, as float bits unless `Bits`. */
  private def lanesOf(values: Seq[Seq[Lane]], mask: String): String = {
    val registers = values.map(_.size).max
    (0 until registers).map(register => s"v_mov_b32 v$register, 0\n").mkString + values.zipWithIndex.map {
      case (lane, index) =>
        lane.zipWithIndex.map { case (value, register) =>
          val bits = value match {
            case Lane.Bits(integer)   => integer
            case Lane.Real(component) => float(component)
          }
          s"""v_cmp_eq_u32 $mask, $index, v8
             |v_mov_b32 v9, $bits
             |v_cndmask_b32 v$register, v$register, v9, $mask
             |""".stripMargin
        }.mkString
    }.mkString
  }

  private val Sample2d = "image_sample_lz v[2:5], v[0:1], s[16:23], s[24:27] dmask:0xf dim:SQ_RSRC_IMG_2D"

  /** One lane per row of `coordinates`, each sampling `instruction` through `resource` and `samplerWords`. */
  private def sampled(
      resource: Seq[Long],
      samplerWords: Seq[Long],
      coordinates: Seq[Seq[Lane]],
      data: Seq[Int],
      instruction: String = Sample2d,
      target: Target = gfx1030
  )(using munit.Location): Vec[Int] = run(
    target,
    coordinates.size,
    data,
    sampleProgram(
      target,
      moves(16, resource) + moves(24, samplerWords) + output + clear +
        lanesOf(coordinates, if (target.isa.isRDNA) "vcc_lo" else "vcc"),
      instruction
    )
  )

  private def at(x: Float, y: Float): Seq[Lane] = Seq(x, y)

  private def red(out: Vec[Int], lanes: Int): Seq[Float] = (0 until lanes).map(index => toFloat(out(index * 4)))

  private def texelFloats(values: Float*): Seq[Int] = values.map(float(_))

  private val One = "image_sample_lz v2, v[0:1], s[16:23], s[24:27] dmask:0x1 dim:SQ_RSRC_IMG_2D"

  test("RDNA2 point sampling reads the texel the coordinate floors to, in texel units") {
    val out = sampled(
      image(Float32, width = 2, height = 2, pitch = 2),
      sampler(),
      Seq(at(0.5f, 0.5f), at(1.25f, 1.0f), at(0.75f, 0.25f), at(1.5f, 1.5f)),
      image2x2,
      One
    )
    assertEquals(red(out, 4), Seq(10f, 40f, 10f, 40f))
  }

  test("RDNA2 bilinear sampling blends the four texels around the coordinate by 1/256 weights") {
    val out = sampled(
      image(Float32, width = 2, height = 2, pitch = 2),
      sampler(filter = 1),
      Seq(at(1.25f, 1.0f), at(0.75f, 0.25f), at(0.5f, 0.5f), at(1.5f, 1.5f), at(0.5f + 0.0033f, 0.5f), at(1.0f, 1.0f)),
      image2x2,
      One
    )
    assertEquals(red(out, 6), Seq(27.5f, 12.5f, 10f, 40f, 10.0390625f, 25f))
  }

  test("RDNA2 normalised coordinates scale by the image size, and the instruction's UNORM forces texels") {
    val texels = sampler(unnormalised = false)
    val out    = sampled(
      image(Float32, width = 2, height = 2, pitch = 2),
      texels,
      Seq(at(0.25f, 0.25f), at(0.75f, 0.75f), at(0.75f, 0.25f)),
      image2x2,
      One
    )
    assertEquals(red(out, 3), Seq(10f, 40f, 20f))
    val forced = sampled(
      image(Float32, width = 2, height = 2, pitch = 2),
      texels,
      Seq(at(0.25f, 0.25f), at(1.75f, 1.75f)),
      image2x2,
      "image_sample_lz v2, v[0:1], s[16:23], s[24:27] dmask:0x1 dim:SQ_RSRC_IMG_2D unorm"
    )
    assertEquals(red(forced, 2), Seq(10f, 40f))
  }

  private val line = Seq(10f, 20f, 30f, 40f)

  private def lineSamples(clamp: Long, filter: Long, border: Long, coordinates: Seq[Float]): Seq[Float] =
    red(
      sampled(
        image(Float32, width = 4, height = 1, pitch = 4, kind = 8),
        sampler(clamp = clamp, filter = filter, border = border, unnormalised = false),
        coordinates.map(c => Seq[Lane](c)),
        texelFloats(line*),
        "image_sample_lz v2, v0, s[16:23], s[24:27] dmask:0x1 dim:SQ_RSRC_IMG_1D"
      ),
      coordinates.size
    )

  private val probes = Seq(-6, -5, -2, -1, 0, 1, 2, 3, 4, 5, 8, 9)

  private def centres(indices: Seq[Int]): Seq[Float] = indices.map(k => (k + 0.5f) / 4f)

  test("RDNA2 address modes wrap, mirror, repeat the last texel and read the border for texels outside") {
    def texel(k: Int): Float = line(k)
    val wrap                 = (k: Int) => texel(Math.floorMod(k, 4))
    val mirror               = (k: Int) => {
      val m = Math.floorMod(k, 8)
      texel(if (m < 4) m else 7 - m)
    }
    val clamp  = (k: Int) => texel(Math.max(0, Math.min(3, k)))
    val border = (k: Int) => if (k >= 0 && k < 4) texel(k) else 0f
    assertEquals(lineSamples(clamp = 0, filter = 0, border = 0, coordinates = centres(probes)), probes.map(wrap))
    assertEquals(lineSamples(clamp = 1, filter = 0, border = 0, coordinates = centres(probes)), probes.map(mirror))
    assertEquals(lineSamples(clamp = 2, filter = 0, border = 0, coordinates = centres(probes)), probes.map(clamp))
    assertEquals(lineSamples(clamp = 6, filter = 0, border = 0, coordinates = centres(probes)), probes.map(border))
    // mirror once reflects the coordinate about zero, then repeats the last texel or reads the border
    val reflected = (k: Int) => if (k < 0) -1 - k else k
    assertEquals(
      lineSamples(clamp = 3, filter = 0, border = 0, coordinates = centres(probes)),
      probes.map(k => clamp(reflected(k)))
    )
    assertEquals(
      lineSamples(clamp = 7, filter = 0, border = 0, coordinates = centres(probes)),
      probes.map(k => border(reflected(k)))
    )
  }

  test("RDNA2 bilinear address modes blend across the edge as the mode addresses the texels beyond it") {
    val coordinates = Seq(0f, 0.125f, 1f, 1.0625f)
    assertEquals(lineSamples(clamp = 0, filter = 1, border = 0, coordinates = coordinates), Seq(25f, 10f, 25f, 17.5f))
    assertEquals(lineSamples(clamp = 1, filter = 1, border = 0, coordinates = coordinates), Seq(10f, 10f, 40f, 40f))
    assertEquals(lineSamples(clamp = 2, filter = 1, border = 0, coordinates = coordinates), Seq(10f, 10f, 40f, 40f))
    assertEquals(lineSamples(clamp = 6, filter = 1, border = 0, coordinates = coordinates), Seq(5f, 10f, 20f, 10f))
    // clamp to half border moves a coordinate beyond the image to its edge, whose texels beyond are the border
    assertEquals(lineSamples(clamp = 4, filter = 1, border = 0, coordinates = Seq(-0.25f, 1.25f, 0f)), Seq(5f, 20f, 5f))
    // an opaque white border fills every channel with one, and opaque black only alpha
    assertEquals(lineSamples(clamp = 6, filter = 1, border = 2, coordinates = Seq(0f, 1f)), Seq(5.5f, 20.5f))
  }

  private def channels(out: Vec[Int], lanes: Int, count: Int): Seq[Seq[Float]] = (0 until lanes).map(index =>
    (0 until count).map(component => toFloat(out(index * 4 + component)))
  )

  private def assertClose(actual: Seq[Float], expected: Seq[Float], tolerance: Float = 1e-6f)(using
      munit.Location
  ): Unit = {
    assertEquals(actual.size, expected.size)
    actual.zip(expected).foreach((got, want) =>
      assert(Math.abs(got - want) <= tolerance, s"$got is not within $tolerance of $want")
    )
  }

  /** Four 4 by 4 levels, 2 by 2 and 1 by 1 of a float image with 256-byte rows: 100 + texel, 200 + texel and 300. */
  private val mipTexels: Seq[Int] = {
    val data = Array.fill(512)(0)
    for {
      y <- 0 until 4
      x <- 0 until 4
    } data(y * 64 + x) = float(100f + y * 4 + x)
    for {
      y <- 0 until 2
      x <- 0 until 2
    } data(256 + y * 64 + x) = float(200f + y * 2 + x)
    data(384) = float(300f)
    data.toSeq
  }

  private def mipSamples(samplerWords: Seq[Long], lods: Seq[Float]): Seq[Float] = red(
    sampled(
      image(Float32, width = 4, height = 4, pitch = 64, levels = 3),
      samplerWords,
      lods.map(lod => Seq[Lane](0.75f, 0.25f, lod)),
      mipTexels,
      "image_sample_l v2, v[0:2], s[16:23], s[24:27] dmask:0x1 dim:SQ_RSRC_IMG_2D"
    ),
    lods.size
  )

  test("RDNA2 explicit levels pick the nearest level, clamp to the sampler's range and blend by the fraction") {
    val lods = Seq(0f, 1f, 2f, 0.4f, 0.6f, 3f)
    assertEquals(
      mipSamples(sampler(mip = 1, maxLod = 2f, unnormalised = false), lods),
      Seq(107f, 201f, 300f, 107f, 201f, 300f)
    )
    assertEquals(
      mipSamples(sampler(mip = 1, maxLod = 1f, unnormalised = false), lods),
      Seq(107f, 201f, 201f, 107f, 201f, 201f)
    )
    assertEquals(mipSamples(sampler(mip = 0, maxLod = 2f, unnormalised = false), lods), Seq.fill(6)(107f))
    assertEquals(mipSamples(sampler(mip = 2, maxLod = 2f, unnormalised = false), Seq(0.5f, 1.25f)), Seq(154f, 225.75f))
    assertEquals(mipSamples(sampler(mip = 1, minLod = 1f, maxLod = 2f, unnormalised = false), Seq(0f)), Seq(201f))
    assertEquals(mipSamples(sampler(mip = 1, maxLod = 2f, bias = 1f, unnormalised = false), Seq(0f)), Seq(201f))
  }

  private val gradientPadding = Seq[Lane](0.75f, 0.25f)

  private def gradientSamples(gradients: Seq[Float]): Seq[Float] = red(
    sampled(
      image(Float32, width = 4, height = 4, pitch = 64, levels = 3),
      sampler(mip = 1, maxLod = 2f, unnormalised = false),
      gradients.grouped(4).map(g => g.map(v => v: Lane) ++ gradientPadding).toSeq,
      mipTexels,
      "image_sample_d v2, v[0:5], s[16:23], s[24:27] dmask:0x1 dim:SQ_RSRC_IMG_2D"
    ),
    gradients.size / 4
  )

  test("RDNA2 gradients pick the level from the larger of the two directions' length in texels") {
    assertEquals(
      gradientSamples(Seq(
        0.25f, 0f, 0f, 0.25f, 0.5f, 0f, 0f, 0.5f, 1f, 0f, 0f, 0f, 0.25f, 0f, 0f, 1f, 0f, 0.125f, 0.125f, 0f
      )),
      Seq(107f, 201f, 300f, 300f, 107f)
    )
  }

  private def quad(
      instruction: String,
      extra: Seq[Lane],
      samplerWords: Seq[Long],
      trailing: Seq[Lane] = Seq.empty
  ): Seq[Float] =
    red(
      sampled(
        image(Float32, width = 4, height = 4, pitch = 64, levels = 3),
        samplerWords,
        Seq((0f, 0f), (0.5f, 0f), (0f, 0.5f), (0.5f, 0.5f)).map((x, y) => extra ++ Seq[Lane](x, y) ++ trailing),
        mipTexels,
        instruction
      ),
      4
    )

  test("RDNA2 implicit levels difference the coordinates across the lanes of a quad, plus a bias or a clamp") {
    val mips  = sampler(mip = 1, maxLod = 2f, unnormalised = false)
    val plain = "image_sample v2, v[0:1], s[16:23], s[24:27] dmask:0x1 dim:SQ_RSRC_IMG_2D"
    assertEquals(quad(plain, Seq.empty, mips), Seq(200f, 201f, 202f, 203f))
    assertEquals(
      quad("image_sample_b v2, v[0:2], s[16:23], s[24:27] dmask:0x1 dim:SQ_RSRC_IMG_2D", Seq(0f), mips),
      Seq(200f, 201f, 202f, 203f)
    )
    assertEquals(
      quad("image_sample_b v2, v[0:2], s[16:23], s[24:27] dmask:0x1 dim:SQ_RSRC_IMG_2D", Seq(1f), mips),
      Seq.fill(4)(300f)
    )
    assertEquals(
      quad("image_sample_cl v2, v[0:2], s[16:23], s[24:27] dmask:0x1 dim:SQ_RSRC_IMG_2D", Seq.empty, mips, Seq(2f)),
      Seq.fill(4)(300f)
    )
    assertEquals(
      quad("image_sample_cl v2, v[0:2], s[16:23], s[24:27] dmask:0x1 dim:SQ_RSRC_IMG_2D", Seq.empty, mips, Seq(0f)),
      Seq(200f, 201f, 202f, 203f)
    )
  }

  test("RDNA2 implicit levels sample a wave whose last quad has inactive lanes") {
    val out = sampled(
      image(Float32, width = 2, height = 2, pitch = 2),
      sampler(),
      Seq(at(0.5f, 0.5f), at(1.5f, 1.5f), at(0.5f, 1.5f)),
      image2x2,
      "image_sample v2, v[0:1], s[16:23], s[24:27] dmask:0x1 dim:SQ_RSRC_IMG_2D"
    )
    assertEquals(red(out, 3), Seq(10f, 40f, 30f))
  }

  test("RDNA2 offsets move the texel by signed texels per axis, and the lower level clamp raises the level") {
    val out = sampled(
      image(Float32, width = 2, height = 2, pitch = 2),
      sampler(),
      Seq(0x0101, 0x3f, 0x01, 0x0100).map(offset => Seq[Lane](offset, 0.5f, 0.5f)),
      image2x2,
      "image_sample_lz_o v2, v[0:2], s[16:23], s[24:27] dmask:0x1 dim:SQ_RSRC_IMG_2D"
    )
    assertEquals(red(out, 4), Seq(40f, 10f, 20f, 30f))
  }

  test("RDNA2 depth comparison filters the pass or fail of each texel against the reference") {
    def compare(function: Long, filter: Long, lanes: Seq[Seq[Lane]]): Seq[Float] = red(
      sampled(
        image(Float32, width = 2, height = 2, pitch = 2),
        sampler(compare = function, filter = filter),
        lanes,
        image2x2,
        "image_sample_c_lz v2, v[0:2], s[16:23], s[24:27] dmask:0x1 dim:SQ_RSRC_IMG_2D"
      ),
      lanes.size
    )
    val texel = (reference: Float) => Seq[Lane](reference, 0.5f, 0.5f)
    assertEquals(compare(function = 3, filter = 0, lanes = Seq(texel(5f), texel(15f), texel(10f))), Seq(1f, 0f, 1f))
    assertEquals(compare(function = 2, filter = 0, lanes = Seq(texel(5f), texel(10f))), Seq(0f, 1f))
    assertEquals(compare(function = 1, filter = 0, lanes = Seq(texel(5f), texel(10f))), Seq(1f, 0f))
    assertEquals(compare(function = 0, filter = 0, lanes = Seq(texel(5f))), Seq(0f))
    assertEquals(compare(function = 7, filter = 0, lanes = Seq(texel(50f))), Seq(1f))
    assertEquals(compare(function = 3, filter = 1, lanes = Seq(Seq[Lane](25f, 1f, 1f))), Seq(0.5f))
    assertEquals(compare(function = 4, filter = 1, lanes = Seq(Seq[Lane](25f, 1f, 1f))), Seq(0.5f))
    assertEquals(compare(function = 3, filter = 1, lanes = Seq(Seq[Lane](25f, 0.5f, 1f))), Seq(0.5f))
  }

  test("RDNA2 gathers return the four texels around the coordinate of one channel, bottom left last") {
    val gather = "image_gather4_lz v[2:5], v[0:1], s[16:23], s[24:27] dmask:0x1 dim:SQ_RSRC_IMG_2D"
    val inside = sampled(
      image(Float32, width = 2, height = 2, pitch = 2),
      sampler(),
      Seq(at(1f, 1f), at(0.5f, 0.5f)),
      image2x2,
      gather
    )
    assertEquals(channels(inside, lanes = 2, count = 4), Seq(Seq(30f, 40f, 20f, 10f), Seq(30f, 40f, 20f, 10f)))
    val edge =
      sampled(image(Float32, width = 2, height = 2, pitch = 2), sampler(), Seq(at(0.25f, 0.25f)), image2x2, gather)
    assertEquals(channels(edge, lanes = 1, count = 4), Seq(Seq(10f, 10f, 10f, 10f)))
    val bordered = sampled(
      image(Float32, width = 2, height = 2, pitch = 2),
      sampler(clamp = 6),
      Seq(at(0.25f, 0.25f)),
      image2x2,
      gather
    )
    assertEquals(channels(bordered, lanes = 1, count = 4), Seq(Seq(0f, 10f, 0f, 0f)))
    val texels = Seq.tabulate(4)(i => (10 + i) | ((20 + i) << 8) | ((30 + i) << 16) | ((40 + i) << 24))
    val blue   = sampled(
      image(Unorm8, width = 2, height = 2, pitch = 2),
      sampler(),
      Seq(at(1f, 1f)),
      texels,
      "image_gather4_lz v[2:5], v[0:1], s[16:23], s[24:27] dmask:0x4 dim:SQ_RSRC_IMG_2D"
    )
    assertClose(channels(blue, lanes = 1, count = 4).head, Seq(32f, 33f, 31f, 30f).map(_ / 255f))
    val compared = sampled(
      image(Float32, width = 2, height = 2, pitch = 2),
      sampler(compare = 3),
      Seq(Seq[Lane](25f, 1f, 1f)),
      image2x2,
      "image_gather4_c_lz v[2:5], v[0:2], s[16:23], s[24:27] dmask:0x1 dim:SQ_RSRC_IMG_2D"
    )
    assertEquals(channels(compared, lanes = 1, count = 4), Seq(Seq(1f, 1f, 0f, 0f)))
  }

  test("RDNA2 array layers round to the nearest layer, clamp to the array, and filtering stays within a layer") {
    val layers = Seq.tabulate(384)(index => float(1000f * (index / 128) + 10f * ((index % 128) / 64) + (index % 64)))
    def layered(filter: Long, lanes: Seq[Seq[Lane]]): Seq[Float] = red(
      sampled(
        image(Float32, width = 2, height = 2, pitch = 2, kind = 13, depth = 2),
        sampler(filter = filter),
        lanes,
        layers,
        "image_sample_lz v2, v[0:2], s[16:23], s[24:27] dmask:0x1 dim:SQ_RSRC_IMG_2D_ARRAY"
      ),
      lanes.size
    )
    assertEquals(
      layered(
        0,
        Seq(
          Seq[Lane](0.5f, 0.5f, 0f),
          Seq[Lane](1.5f, 0.5f, 1f),
          Seq[Lane](0.5f, 1.5f, 2f),
          Seq[Lane](0.5f, 0.5f, 0.6f),
          Seq[Lane](0.5f, 0.5f, 7f),
          Seq[Lane](0.5f, 0.5f, -3f)
        )
      ),
      Seq(0f, 1001f, 2010f, 1000f, 2000f, 0f)
    )
    assertEquals(layered(1, Seq(Seq[Lane](1f, 1f, 1f))), Seq(1005.5f))
  }

  test("RDNA2 volumes filter the depth axis by the Z filter and the other axes by the XY filter") {
    val texels = Seq.tabulate(256)(index => float((index / 128) * 4f + ((index % 128) / 64) * 2f + (index % 64) + 1f))
    def volume(filter: Long, zFilter: Long, lanes: Seq[Seq[Lane]]): Seq[Float] = red(
      sampled(
        image(Float32, width = 2, height = 2, pitch = 2, kind = 10, depth = 1),
        sampler(filter = filter, zFilter = zFilter),
        lanes,
        texels,
        "image_sample_lz v2, v[0:2], s[16:23], s[24:27] dmask:0x1 dim:SQ_RSRC_IMG_3D"
      ),
      lanes.size
    )
    assertEquals(volume(filter = 0, zFilter = 1, lanes = Seq(Seq[Lane](0.5f, 1.5f, 1.5f))), Seq(7f))
    assertEquals(volume(filter = 1, zFilter = 2, lanes = Seq(Seq[Lane](1f, 1f, 1f))), Seq(4.5f))
    assertEquals(volume(filter = 1, zFilter = 1, lanes = Seq(Seq[Lane](1f, 1f, 1f))), Seq(6.5f))
  }

  test("RDNA2 samples convert texels through the format and destination selects, and write only the dmask channels") {
    val texels = Seq.tabulate(4)(i => (10 + i * 40) | ((20 + i) << 8) | ((30 + i) << 16) | (255 << 24))
    val out    = sampled(
      image(Unorm8, width = 2, height = 2, pitch = 2),
      sampler(filter = 1),
      Seq(at(0.5f, 0.5f), at(1f, 1f)),
      texels,
      "image_sample_lz v[2:3], v[0:1], s[16:23], s[24:27] dmask:0x5 dim:SQ_RSRC_IMG_2D"
    )
    assertClose(channels(out, lanes = 2, count = 2).head, Seq(10f / 255f, 30f / 255f))
    assertClose(channels(out, lanes = 2, count = 2)(1), Seq(70f / 255f, 31.5f / 255f))
    assertEquals(out.slice(2, 4), Vec(-1, -1))
    val selected = sampled(
      image(Float32, width = 2, height = 2, pitch = 2, selects = 4L | (0L << 3) | (1L << 6) | (4L << 9)),
      sampler(),
      Seq(at(0.5f, 0.5f)),
      image2x2
    )
    assertEquals(channels(selected, lanes = 1, count = 4), Seq(Seq(10f, 0f, 1f, 10f)))
  }

  test("RDNA3 samples read the 13-bit level range of its sampler descriptor") {
    val gfx1100 = Target("gfx1100")
    val out     = sampled(
      image(Float32, width = 4, height = 4, pitch = 64, levels = 3),
      sampler(mip = 1, maxLod = 2f, unnormalised = false, wide = true),
      Seq(Seq[Lane](0.75f, 0.25f, 1f), Seq[Lane](0.75f, 0.25f, 5f)),
      mipTexels,
      "image_sample_l v2, v[0:2], s[16:23], s[24:27] dmask:0x1 dim:SQ_RSRC_IMG_2D",
      gfx1100
    )
    assertEquals(red(out, 2), Seq(201f, 300f))
  }

  test("GCN5 samples take the image kind from the descriptor and the legacy sampler layout") {
    val gfx906 = Target("gfx906")
    val base   = address(1L) >>> 8
    val legacy = Seq(
      base & 0xffffffffL,
      (base >>> 32) | (4L << 20) | (7L << 26),
      1L | (1L << 14),
      Identity | (9L << 28),
      1L << 13,
      0,
      0,
      0
    )
    val out = sampled(
      legacy,
      sampler(filter = 1),
      Seq(at(1.25f, 1.0f), at(0.5f, 0.5f)),
      image2x2,
      "image_sample_lz v2, v[0:1], s[16:23], s[24:27] dmask:0x1",
      gfx906
    )
    assertEquals(red(out, 2), Seq(27.5f, 10f))
    val normalised = sampled(
      legacy,
      sampler(filter = 1, unnormalised = false),
      Seq(at(0.625f, 0.5f), at(0.5f, 0.5f), at(0.25f, 0.25f)),
      image2x2,
      "image_sample_lz v2, v[0:1], s[16:23], s[24:27] dmask:0x1",
      gfx906
    )
    assertEquals(red(normalised, 3), Seq(27.5f, 25f, 10f))
    val gathered = sampled(
      legacy,
      sampler(),
      Seq(at(1f, 1f)),
      image2x2,
      "image_gather4_lz v[2:5], v[0:1], s[16:23], s[24:27] dmask:0x1",
      gfx906
    )
    assertEquals(channels(gathered, lanes = 1, count = 4), Seq(Seq(30f, 40f, 20f, 10f)))
    val levelled = sampled(
      legacy,
      sampler(filter = 1, maxLod = 2f),
      Seq(Seq[Lane](1f, 1f, 3f)),
      image2x2,
      "image_sample_l v2, v[0:2], s[16:23], s[24:27] dmask:0x1",
      gfx906
    )
    assertEquals(red(levelled, 1), Seq(25f))
  }

  test("RDNA4 samples take their VSAMPLE address registers and the sampler named by SAMP") {
    val gfx1200 = Target("gfx1200")
    val base    = address(1L) >>> 8
    val words   = Seq(
      base & 0xffffffffL,
      (base >>> 32) | (22L << 17) | (1L << 30),
      0L | (1L << 14),
      Identity | (9L << 28),
      1L,
      0,
      0,
      0
    )
    val out = sampled(
      words,
      sampler(filter = 1, wide = true),
      Seq(at(1.25f, 1.0f), at(1.5f, 1.5f)),
      image2x2,
      "image_sample_lz v2, [v0, v1], s[16:23], s[24:27] dmask:0x1 dim:SQ_RSRC_IMG_2D",
      gfx1200
    )
    assertEquals(red(out, 2), Seq(27.5f, 40f))
  }

  test("sampler descriptors decode their clamp modes, filters, level range, bias and comparison per generation") {
    val words = Array(
      1L | (2L << 3) | (6L << 6) | (3L << 12) | (1L << 15) | (1L << 29),
      (1L << 4) | (3L << 12),
      0x3ffeL | (1L << 20) | (3L << 22) | (2L << 24) | (2L << 26),
      2L << 30
    )
    val older = SamplerResource.decode(ResourceGeneration.GFX10, words)
    assertEquals(
      (older.clampX, older.clampY, older.clampZ, older.depthCompare, older.forceUnnormalised, older.filterMode),
      (1, 2, 6, 3, true, 1)
    )
    assertEquals((older.minLod, older.maxLod, older.lodBias), (1.0 / 16.0, 3.0 / 256.0, -2.0 / 256.0))
    assertEquals(
      (older.magnifyFilter, older.minifyFilter, older.zFilter, older.mipFilter, older.borderColour),
      (1, 3, 2, 2, 2)
    )
    val newer =
      SamplerResource.decode(ResourceGeneration.GFX11, Array(words(0), (1L << 4) | (3L << 13), words(2), words(3)))
    assertEquals((newer.minLod, newer.maxLod), (1.0 / 16.0, 3.0 / 256.0))
  }

  test("sampler address functions map coordinates and indices as each clamp mode defines") {
    assertEquals(
      (0 to 3).map(mode => SamplerResource.coordinate(mode, value = -1.5, extent = 4.0)),
      Seq(-1.5, -1.5, -1.5, 1.5)
    )
    assertEquals(
      (4 to 7).map(mode => SamplerResource.coordinate(mode, value = -1.5, extent = 4.0)),
      Seq(0.0, 1.5, -1.5, 1.5)
    )
    assertEquals(
      (4 to 7).map(mode => SamplerResource.coordinate(mode, value = 9.0, extent = 4.0)),
      Seq(4.0, 4.0, 9.0, 9.0)
    )
    assertEquals(Seq(-5L, -1L, 0L, 3L, 4L, 7L).map(SamplerResource.texel(0, _, 4)), Seq(3L, 3L, 0L, 3L, 0L, 3L))
    assertEquals(Seq(-5L, -1L, 0L, 3L, 4L, 7L).map(SamplerResource.texel(1, _, 4)), Seq(3L, 0L, 0L, 3L, 3L, 0L))
    assertEquals(Seq(-5L, -1L, 0L, 3L, 4L, 7L).map(SamplerResource.texel(2, _, 4)), Seq(0L, 0L, 0L, 3L, 3L, 3L))
    assertEquals(Seq(-5L, -1L, 0L, 3L, 4L, 7L).map(SamplerResource.texel(6, _, 4)), Seq(-1L, -1L, 0L, 3L, -1L, -1L))
    assertEquals(
      (0 to 7).map(SamplerResource.passes(_, reference = 2.0, depth = 3.0)),
      Seq(false, true, false, true, false, true, false, true)
    )
  }
}
