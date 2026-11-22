package net.kurobako.gemstone.isa.check

import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.isa.KernelMetadata.ParameterKind
import net.kurobako.gemstone.profile.ProfileStream
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.ArrayMemory
import net.kurobako.gemstone.vm.KernelTask

class LaunchRecordingSuite extends Suite {

  private val scale = "scale"
  private val flux  = "clover_flux"

  private def parsed(text: String): Vec[LaunchArgument] = LaunchArgument.parse(text).fold(fail(_), identity)

  private def bound(
      language: Language,
      image: Vec[Byte],
      kernel: String,
      memory: ArrayMemory,
      text: String
  ): Either[String, Vec[KernelTask.Argument]] = LaunchRecording.bind(language, image, kernel, memory, parsed(text))

  private def contents(memory: ArrayMemory, argument: KernelTask.Argument): Vec[Byte] = argument match {
    case KernelTask.Argument.Memory(region, _, _) => memory.bytes(region)
    case other                                    => fail(s"$other is not a buffer")
  }

  private def word(bytes: Vec[Byte]): Long = bytes.zipWithIndex.map((byte, at) => (byte & 0xffL) << (8 * at)).sum

  private def ints(bytes: Vec[Byte]): Vec[Int] = bytes.grouped(4).toVec.map(group => word(group).toInt)

  private def scalar(argument: KernelTask.Argument): Vec[Byte] = argument match {
    case KernelTask.Argument.Scalar(bytes) => bytes
    case other                             => fail(s"$other is not a scalar")
  }

  private def kinds(language: Language, image: Vec[Byte], kernel: String): Vec[ParameterKind] = {
    val loaded = ProgramRun.load(ProgramListing.Image(language, image, None)).fold(fail(_), identity)
    try {
      val metadata =
        loaded.program.kernel(kernel.unsafe[NonEmpty[String]]).fold(fault => fail(fault.toString), identity)
      metadata.parameters.take(metadata.explicitParameterCount).map(_.kind)
    } finally loaded.close()
  }

  private def buffers(arguments: Vec[KernelTask.Argument]): Boolean = arguments.forall {
    case _: KernelTask.Argument.Memory => true
    case _                             => false
  }

  private def issues(stream: ProfileStream): Int = {
    val count = AtomicInteger()
    stream.foreach(new ProfileStream.Visitor {
      override def issue(issue: ProfileStream.Issue): Unit = { val _ = count.incrementAndGet() }
    })
    count.get
  }

  test("a cubin that marks its pointers has them as allocations and an eight-byte scalar as a scalar") {
    assertEquals(
      kinds(Language.SASS, SASSFixtures.typed, scale),
      Vec(ParameterKind.Allocation, ParameterKind.Allocation, ParameterKind.Allocation, ParameterKind.Scalar)
    )
  }

  test("a SASS double is passed by value and a pointer gets a buffer") {
    val memory = ArrayMemory()
    val result = bound(Language.SASS, SASSFixtures.typed, scale, memory, "buffer:64,buffer:64,buffer:16,f64:0.5")
      .fold(fail(_), identity)
    assertEquals(result.size, 4)
    assert(buffers(result.take(3)), result.toString)
    assertEquals(word(scalar(result(3))), java.lang.Double.doubleToLongBits(0.5))
  }

  test("a SASS pointer is not given a scalar") {
    val error = bound(Language.SASS, SASSFixtures.typed, scale, ArrayMemory(), "i64:4096,buffer:64,buffer:16,f64:0.5")
      .left.getOrElse(fail("a scalar bound to a pointer"))
    assert(error.contains("argument 0 of scale") && error.contains("pointer"), error)
  }

  test("a scalar of another width is refused") {
    val error = bound(Language.SASS, SASSFixtures.typed, scale, ArrayMemory(), "buffer:64,buffer:64,buffer:16,i32:1")
      .left.getOrElse(fail("an i32 bound to an eight-byte scalar"))
    assert(error.contains("argument 3 of scale") && error.contains("i32"), error)
  }

  test("an AMDGPU double is passed by value and a pointer gets a buffer") {
    val memory = ArrayMemory()
    val result = bound(
      Language.AMDGPU,
      WindowFixtures.flux,
      flux,
      memory,
      "buffer:1024,buffer:1024,buffer:1024,buffer:1024,buffer:1024,buffer:1024,buffer:1024,buffer:1024,i32:256,i32:16,f64:0.25"
    ).fold(fail(_), identity)
    assertEquals(result.size, 11)
    assert(buffers(result.take(8)), result.toString)
    assertEquals(word(scalar(result(8))), 256L)
    assertEquals(word(scalar(result(10))), java.lang.Double.doubleToLongBits(0.25))
    assertEquals(scalar(result(10)).size, 8)
  }

  test("an AMDGPU double is not given a buffer and a pointer is not given a scalar") {
    val buffers = Vec.fill(8)("buffer:1024").mkString(",")
    val dt      = bound(Language.AMDGPU, WindowFixtures.flux, flux, ArrayMemory(), s"$buffers,i32:256,i32:16,buffer:8")
      .left.getOrElse(fail("a buffer bound to a double"))
    assert(dt.contains("argument 10 of clover_flux") && dt.contains("scalar"), dt)
    val first = bound(
      Language.AMDGPU,
      WindowFixtures.flux,
      flux,
      ArrayMemory(),
      s"i64:1,${Vec.fill(7)("buffer:1024").mkString(",")},i32:256,i32:16,f64:1.0"
    ).left.getOrElse(fail("a scalar bound to a pointer"))
    assert(first.contains("argument 0 of clover_flux") && first.contains("pointer"), first)
  }

  test("a buffer holds its stated constant, ramp, or zeroes") {
    val memory = ArrayMemory()
    val result = bound(
      Language.SASS,
      SASSFixtures.typed,
      scale,
      memory,
      "buffer:16:fill=f32:1.0,buffer:16:ramp=i32,buffer:16:ramp=i32:5:2,f64:0.5"
    ).fold(fail(_), identity)
    assertEquals(ints(contents(memory, result(0))), Vec.fill(4)(java.lang.Float.floatToRawIntBits(1.0f)))
    assertEquals(ints(contents(memory, result(1))), Vec(0, 1, 2, 3))
    assertEquals(ints(contents(memory, result(2))), Vec(5, 7, 9, 11))
    val zeroed = bound(Language.SASS, SASSFixtures.typed, scale, memory, "buffer:16,buffer:16,buffer:16,f64:0.5")
      .fold(fail(_), identity)
    assertEquals(ints(contents(memory, zeroed(0))), Vec(0, 0, 0, 0))
  }

  test("a stated fill reaches the kernel: a loop bound read from memory makes the kernel run its loop") {
    val record = (count: String) =>
      LaunchRecording.record(
        Language.SASS,
        SASSFixtures.typed,
        scale,
        32,
        1,
        parsed(s"buffer:4096,buffer:4096:fill=f32:1.0,buffer:16$count,f64:0.5")
      ).fold(fail(_), identity)
    val empty    = issues(record(""))
    val repeated = issues(record(":fill=i32:3"))
    assert(repeated > empty, s"$repeated issues with a count of three against $empty with zero")
  }

  test("arguments parse as a type and a value or as a sized buffer") {
    assertEquals(
      parsed("i32:64,f64:0.5,buffer:4096:fill=f32:1.0"),
      Vec(
        LaunchArgument.Scalar(LaunchArgument.Element.I32, LaunchArgument.Number.Integer(64)),
        LaunchArgument.Scalar(LaunchArgument.Element.F64, LaunchArgument.Number.Real(0.5)),
        LaunchArgument.Buffer(
          4096L.unsafe[Long > 0],
          LaunchArgument.Fill.Constant(LaunchArgument.Element.F32, LaunchArgument.Number.Real(1.0))
        )
      )
    )
    Vec("4096", "x32:1", "i32:abc", "buffer:0", "buffer:8:fill=i32", "buffer:8:ramp=f16").foreach(text =>
      assert(LaunchArgument.parse(text).isLeft, text)
    )
  }
}
