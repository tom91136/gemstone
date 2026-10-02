package net.kurobako.gemstone.isa.ptx.v9_3

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*

class CollectiveExecutionSuite extends munit.FunSuite {
  private def execute(body: String): Vec[(Int, Int)] = {
    val source = s""".version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry collective(.param .u64 output) {
      | .reg .b32 %lane, %value, %result, %valid;
      | .reg .b64 %out, %offset, %address;
      | .reg .pred %p, %active, %selected;
      | ld.param.u64 %out, [output];
      | mov.u32 %lane, %tid.x;
      | add.u32 %value, %lane, 100;
      | mov.u32 %result, 999;
      | mov.u32 %valid, 999;
      | $body
      | mul.wide.u32 %offset, %lane, 8;
      | add.u64 %address, %out, %offset;
      | st.global.u32 [%address], %result;
      | st.global.u32 [%address+4], %valid;
      | ret;
      |}
      |""".stripMargin
    val kernel = compileKernel(source)
    val memory = ArrayCheckedMemory.allocate(512, GuestAddress(0x10000L))
    assertEquals(
      execution.execute(
        kernel,
        1,
        64,
        Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
        memory
      ),
      Right(())
    )
    Vec.tabulate(64) { lane =>
      val address = memory.allocation.start.offset(lane.toLong * 8)
      (memory.readInt32(address).toOption.get, memory.readInt32(address.offset(4)).toOption.get)
    }
  }

  for ((operation, clamp) <- Vec("up" -> 0, "down" -> 31, "bfly" -> 31))
    test(s"$operation shuffle reports boundary validity and stays within each warp") {
      val result   = execute(s"shfl.sync.$operation.b32 %result|%p, %value, 3, $clamp, -1; selp.u32 %valid, 1, 0, %p;")
      val expected = Vec.tabulate(64) { lane =>
        val local  = lane % 32
        val source = operation match {
          case "up"   => local - 3
          case "down" => local + 3
          case _      => local ^ 3
        }
        val valid = source >= 0 && source < 32
        (100 + (if (valid) lane / 32 * 32 + source else lane), if (valid) 1 else 0)
      }
      assertEquals(result, expected)
    }

  for (
    (operation, threshold, expected) <- Vec(
      ("all", 8, 0),
      ("any", 8, 1),
      ("uni", 8, 0),
      ("all", 16, 1),
      ("any", 16, 1),
      ("uni", 16, 1),
      ("all", 0, 0),
      ("any", 0, 0),
      ("uni", 0, 1)
    )
  )
    test(s"$operation vote with $threshold true lanes reduces only participating lanes") {
      val result = execute(s"""setp.lt.u32 %active, %lane, 16;
        |setp.lt.u32 %selected, %lane, $threshold;
        |@%active vote.sync.$operation.pred %p, %selected, 0x0000ffff;
        |@%active selp.u32 %result, 1, 0, %p;
        |@%active mov.u32 %valid, 1;""".stripMargin)
      assertEquals(result, (0 until 64).map(lane => if (lane < 16) (expected, 1) else (999, 999)))
    }

  test("ballot uses lane bits within the participating warp and leaves inactive lanes untouched") {
    val result = execute("""setp.lt.u32 %active, %lane, 16;
      |setp.ge.u32 %selected, %lane, 8;
      |@%active vote.sync.ballot.b32 %result, %selected, 0x0000ffff;
      |@%active mov.u32 %valid, 1;""".stripMargin)
    assertEquals(result, (0 until 64).map(lane => if (lane < 16) (0xff00, 1) else (999, 999)))
  }

  test("predicated branches reconverge at a block barrier") {
    val source = """
      |.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry choose(.param .u64 output) {
      |  .reg .pred %p;
      |  .reg .b32 %r<3>;
      |  .reg .b64 %rd<2>;
      |  ld.param.u64 %rd0, [output];
      |  mov.u32 %r0, %tid.x;
      |  setp.lt.u32 %p, %r0, 2;
      |  @%p bra low;
      |  mov.u32 %r1, 9;
      |  bra join;
      |low:
      |  mov.u32 %r1, 7;
      |join:
      |  bar.sync 0;
      |  mul.wide.u32 %rd1, %r0, 4;
      |  add.u64 %rd1, %rd0, %rd1;
      |  st.global.u32 [%rd1], %r1;
      |  ret;
      |}
      |""".stripMargin
    val kernel = compileKernel(source)
    val memory =
      ArrayCheckedMemory.allocate(16, provenance = "divergent-barrier")
    val result = execution.execute(
      kernel,
      1,
      4,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Vec.tabulate(4)(index => memory.readInt32(memory.allocation.start.offset(index.toLong * 4L)).toOption.get),
      Vec(7, 7, 9, 9)
    )
  }

  test("workgroup barriers match divergent arrivals by hardware barrier ID") {
    val source = """
      |.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry divergent_barrier(.param .u64 output) {
      |  .shared .align 8 .b8 values[2048];
      |  .reg .pred %p;
      |  .reg .b32 %r<2>;
      |  .reg .b64 %rd<4>;
      |  .reg .f64 %fd;
      |  ld.param.u64 %rd0, [output];
      |  mov.u32 %r0, %tid.x;
      |  mul.wide.u32 %rd1, %r0, 8;
      |  mov.u64 %rd2, values;
      |  add.s64 %rd2, %rd2, %rd1;
      |  mov.f64 %fd, 0d3ff0000000000000;
      |  st.shared.f64 [%rd2], %fd;
      |  setp.lt.u32 %p, %r0, 128;
      |  @%p bra low;
      |  bar.sync 0;
      |  bra join;
      |low:
      |  bar.sync 0;
      |  ld.shared.f64 %fd, [%rd2];
      |join:
      |  setp.eq.u32 %p, %r0, 0;
      |  @%p st.global.f64 [%rd0], %fd;
      |  ret;
      |}
      |""".stripMargin
    val kernel = compileKernel(source)
    val memory = ArrayCheckedMemory.allocate(
      8,
      provenance = "divergent-workgroup-barrier-id"
    )
    val result = execution.execute(
      kernel,
      1,
      256,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      memory.readLittleEndian(memory.allocation.start, 8).toOption.get,
      0x3ff0000000000000L
    )
  }

  test("divergent warp participants publish shared stores at a warp barrier") {
    val source = """
      |.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry choose(.param .u64 output) {
      |  .reg .pred %p;
      |  .reg .b32 %r<4>;
      |  .reg .b64 %rd<3>;
      |  ld.param.u64 %rd0, [output];
      |  mov.u32 %r0, %tid.x;
      |  setp.lt.u32 %p, %r0, 2;
      |  @%p bra low;
      |  mov.u32 %r1, 9;
      |  bra join;
      |low:
      |  mov.u32 %r1, 7;
      |join:
      |  mul.wide.u32 %rd1, %r0, 4;
      |  st.shared.u32 [%rd1], %r1;
      |  bar.warp.sync -1;
      |  mov.u32 %r2, 0;
      |  ld.shared.u32 %r3, [%r2];
      |  mul.wide.u32 %rd2, %r0, 4;
      |  add.u64 %rd2, %rd0, %rd2;
      |  st.global.u32 [%rd2], %r3;
      |  ret;
      |}
      |""".stripMargin
    val kernel = compileKernel(source)
    val memory = ArrayCheckedMemory.allocate(
      16,
      provenance = "divergent-warp-barrier"
    )
    val result = execution.execute(
      kernel,
      1,
      4,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Vec.tabulate(4)(index => memory.readInt32(memory.allocation.start.offset(index.toLong * 4L)).toOption.get),
      Vec(7, 7, 7, 7)
    )
  }

  test("warp shuffles wait for masked lanes before reading registers") {
    val source = """
      |.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry choose(.param .u64 output) {
      |  .reg .pred %p;
      |  .reg .b32 %r<4>;
      |  .reg .b64 %rd<3>;
      |  ld.param.u64 %rd0, [output];
      |  mov.u32 %r0, %tid.x;
      |  setp.eq.u32 %p, %r0, 0;
      |  @%p mov.u32 %r1, 7;
      |  shfl.sync.idx.b32 %r2, %r1, 0, 31, -1;
      |  mul.wide.u32 %rd1, %r0, 4;
      |  add.u64 %rd2, %rd0, %rd1;
      |  st.global.u32 [%rd2], %r2;
      |  ret;
      |}
      |""".stripMargin
    val kernel = compileKernel(source)
    val memory =
      ArrayCheckedMemory.allocate(16, provenance = "sync-warp-shuffle")
    val result = execution.execute(
      kernel,
      1,
      4,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Vec.tabulate(4)(index => memory.readInt32(memory.allocation.start.offset(index.toLong * 4L)).toOption.get),
      Vec(7, 7, 7, 7)
    )
  }

  test("indexed warp shuffle keeps the source inside each eight-lane segment") {
    val source = """
      |.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry choose(.param .u64 output) {
      |  .reg .b32 %r<3>;
      |  .reg .b64 %rd<3>;
      |  ld.param.u64 %rd0, [output];
      |  mov.u32 %r0, %tid.x;
      |  shfl.sync.idx.b32 %r1, %r0, 0, 0x1807, -1;
      |  mul.wide.u32 %rd1, %r0, 4;
      |  add.u64 %rd2, %rd0, %rd1;
      |  st.global.u32 [%rd2], %r1;
      |  ret;
      |}
      |""".stripMargin
    val kernel = compileKernel(source)
    val memory = ArrayCheckedMemory.allocate(
      32 * 4,
      provenance = "segmented-warp-shuffle"
    )
    val result = execution.execute(
      kernel,
      1,
      32,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Vec.tabulate(32)(index => memory.readInt32(memory.allocation.start.offset(index.toLong * 4L)).toOption.get),
      (0 until 32).map(_ & ~7)
    )
  }

  test("64-bit warp shuffles preserve the complete register value") {
    val source = """
      |.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry choose(.param .u64 output) {
      |  .reg .b64 %rd<4>;
      |  ld.param.u64 %rd0, [output];
      |  mov.b64 %rd1, 0x1122334455667788;
      |  shfl.sync.idx.b64 %rd2, %rd1, 0, 31, -1;
      |  st.global.b64 [%rd0], %rd2;
      |  ret;
      |}
      |""".stripMargin
    val kernel = compileKernel(source)
    val memory =
      ArrayCheckedMemory.allocate(8, provenance = "64-bit-warp-shuffle")
    val result = execution.execute(
      kernel,
      1,
      4,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      memory.readLittleEndian(memory.allocation.start, 8).toOption,
      Some(0x1122334455667788L)
    )
  }

  test("inline PTX warp-shuffle register aliases are accepted") {
    val source = """
      |.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry choose(.param .u64 output) {
      |  .reg .pred p;
      |  .reg .b32 lo;
      |  .reg .b32 %r<2>;
      |  .reg .b64 %rd<3>;
      |  ld.param.u64 %rd0, [output];
      |  mov.u32 lo, 7;
      |  shfl.sync.up.b32 lo|p, lo, 1, 0, -1;
      |  mov.u32 %r0, %tid.x;
      |  mul.wide.u32 %rd1, %r0, 4;
      |  add.u64 %rd2, %rd0, %rd1;
      |  st.global.u32 [%rd2], lo;
      |  ret;
      |}
      |""".stripMargin
    val kernel = compileKernel(source)
    val memory = ArrayCheckedMemory.allocate(
      8,
      provenance = "inline-warp-shuffle-registers"
    )
    val result = execution.execute(
      kernel,
      1,
      2,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Vec.tabulate(2)(index => memory.readInt32(memory.allocation.start.offset(index.toLong * 4L)).toOption.get),
      Vec(7, 7)
    )
  }
}
