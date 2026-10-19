package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*

class CollectiveExecutionSuite extends munit.FunSuite {
  private val inactiveLane         = (999, 999)
  private val highByteLane         = (0xff00, 1)
  private val legacyVoteThresholds = Vec(0, 8, 32, 40, 64)

  private def execute(body: String, header: String = ".version 8.0\n.target sm_80"): Vec[(Int, Int)] = {
    val source = s"""$header
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
    val memory = DeviceMemory(512, GuestAddress(0x10000L))
    assertEquals(
      execution.execute(
        kernel,
        1,
        64,
        Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
        memory
      ),
      Right(())
    )
    Vec.tabulate(64) { lane =>
      val address = memory.start.offset(lane.toLong * 8)
      (memory.readInt32(address).toOption.get, memory.readInt32(address.offset(4)).toOption.get)
    }
  }

  test("vote and barrier reductions accept negated predicate sources") {
    val result = execute(
      """setp.lt.u32 %p, %lane, 40;
        | vote.sync.any.pred %selected, !%p, -1;
        | selp.u32 %result, 1, 0, %selected;
        | vote.sync.ballot.b32 %valid, !%p, -1;
        | bar.red.and.pred %active, 0, !%p;
        | @%active mov.u32 %valid, 7;""".stripMargin
    )
    val expected = Vec.tabulate(64)(lane => if (lane < 32) (0, 0) else (1, 0xffffff00))
    assertEquals(result, expected)
    val reduced =
      execute("setp.lt.u32 %p, %lane, 64; bar.red.or.pred %active, 0, !%p; selp.u32 %result, 1, 0, %active;")
    assertEquals(reduced.map(_._1), Vec.fill(64)(0))
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
      val activeLane = (expected, 1)
      assertEquals(result, (0 until 64).map(lane => if (lane < 16) activeLane else inactiveLane))
    }

  // PTX ISA 9.7.9.5 and 9.7.14.9: without .sync, shfl and vote act on the whole warp, as their .sync forms with a full
  // member mask do; they remain valid for targets below sm_70.
  private val legacy = ".version 6.0\n.target sm_60"

  for ((operation, clamp) <- Vec("up" -> 0, "down" -> 31, "bfly" -> 31, "idx" -> 31))
    test(s"legacy $operation shuffle matches its synchronising form over the full warp") {
      assertEquals(
        execute(s"shfl.$operation.b32 %result|%p, %value, 3, $clamp; selp.u32 %valid, 1, 0, %p;", legacy),
        execute(s"shfl.sync.$operation.b32 %result|%p, %value, 3, $clamp, -1; selp.u32 %valid, 1, 0, %p;")
      )
    }

  for {
    operation <- Vec("all", "any", "uni")
    threshold <- legacyVoteThresholds
  }
    test(s"legacy $operation vote with $threshold true lanes matches its synchronising form over the full warp") {
      def vote(spelling: String, header: String) = execute(
        s"""setp.lt.u32 %selected, %lane, $threshold;
           |$spelling %p, %selected${if (spelling.contains("sync")) ", -1" else ""};
           |selp.u32 %result, 1, 0, %p;
           |vote.ballot.b32 %valid, %selected;""".stripMargin,
        header
      )
      assertEquals(vote(s"vote.$operation.pred", legacy), vote(s"vote.sync.$operation.pred", legacy))
    }

  // PTX ISA 9.7.14.1: bar{.cta}.sync, .arrive and .red are barrier{.cta}.sync, .arrive and .red with .aligned.
  for (
    spelling <- Vec(
      "bar.red.popc.u32",
      "bar.cta.red.popc.u32",
      "barrier.red.popc.u32",
      "barrier.red.popc.aligned.u32",
      "barrier.cta.red.popc.aligned.u32"
    )
  )
    test(s"$spelling counts the participants whose predicate holds") {
      val result = execute(s"""setp.lt.u32 %p, %lane, 40;
        |$spelling %result, 0, %p;
        |$spelling %valid, 1, 64, !%p;""".stripMargin)
      assertEquals(result, Vec.fill(64)((40, 24)))
    }

  for (
    (operation, spelling) <- Vec("and", "or").flatMap(operation =>
      Vec(
        s"barrier.red.$operation.pred",
        s"barrier.red.$operation.aligned.pred",
        s"barrier.cta.red.$operation.pred",
        s"bar.cta.red.$operation.pred"
      ).map(operation -> _)
    )
  )
    test(s"$spelling reduces like bar.red.$operation.pred") {
      def reduce(spelling: String) = execute(s"""setp.lt.u32 %p, %lane, 40;
        |$spelling %active, 0, %p;
        |selp.u32 %result, 1, 0, %active;
        |$spelling %active, 1, 64, !%p;
        |selp.u32 %valid, 1, 0, %active;""".stripMargin)
      assertEquals(reduce(spelling), reduce(s"bar.red.$operation.pred"))
    }

  test("ballot uses lane bits within the participating warp and leaves inactive lanes untouched") {
    val result = execute("""setp.lt.u32 %active, %lane, 16;
      |setp.ge.u32 %selected, %lane, 8;
      |@%active vote.sync.ballot.b32 %result, %selected, 0x0000ffff;
      |@%active mov.u32 %valid, 1;""".stripMargin)
    assertEquals(result, (0 until 64).map(lane => if (lane < 16) highByteLane else inactiveLane))
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
      DeviceMemory(16)
    val result = execution.execute(
      kernel,
      1,
      4,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Vec.tabulate(4)(index => memory.readInt32(memory.start.offset(index.toLong * 4L)).toOption.get),
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
    val memory = DeviceMemory(
      8
    )
    val result = execution.execute(
      kernel,
      1,
      256,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      memory.readLittleEndian(memory.start, 8).toOption.get,
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
    val memory = DeviceMemory(
      16
    )
    val result = execution.execute(
      kernel,
      1,
      4,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Vec.tabulate(4)(index => memory.readInt32(memory.start.offset(index.toLong * 4L)).toOption.get),
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
      DeviceMemory(16)
    val result = execution.execute(
      kernel,
      1,
      4,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Vec.tabulate(4)(index => memory.readInt32(memory.start.offset(index.toLong * 4L)).toOption.get),
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
    val memory = DeviceMemory(
      32 * 4
    )
    val result = execution.execute(
      kernel,
      1,
      32,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Vec.tabulate(32)(index => memory.readInt32(memory.start.offset(index.toLong * 4L)).toOption.get),
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
      DeviceMemory(8)
    val result = execution.execute(
      kernel,
      1,
      4,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      memory.readLittleEndian(memory.start, 8).toOption,
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
    val memory = DeviceMemory(
      8
    )
    val result = execution.execute(
      kernel,
      1,
      2,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Vec.tabulate(2)(index => memory.readInt32(memory.start.offset(index.toLong * 4L)).toOption.get),
      Vec(7, 7)
    )
  }

  // 9.7.14.11: .any gives each lane the lanes holding its value, .all every participating lane where they all match
  test("match.any and match.all compare a value across the warp") {
    val any =
      execute("and.b32 %valid, %lane, 3; match.any.sync.b32 %result, %valid, -1;", ".version 8.0\n.target sm_70")
    assertEquals(any.map(_._1), Vec.tabulate(64)(lane => 0x11111111 << (lane % 4)))
    val all = execute(
      """shr.u32 %valid, %lane, 5; match.all.sync.b32 %result|%p, %valid, -1; selp.u32 %valid, 1, 0, %p;""".stripMargin,
      ".version 8.0\n.target sm_70"
    )
    assertEquals(all, Vec.fill(64)((-1, 1)))
    val some = execute(
      "setp.lt.u32 %active, %lane, 8; @%active match.all.sync.b64 %result|%p, %out, 0xff;" +
        " @%active selp.u32 %valid, 1, 0, %p;",
      ".version 8.0\n.target sm_70"
    )
    assertEquals(some, Vec.tabulate(64)(lane => if (lane < 8) (0xff, 1) else inactiveLane))
  }

  // 9.7.14.13: binary32 minimum and maximum over the warp, of magnitudes under .abs, NaN only under .NaN
  test("redux.sync reduces binary32 values to their minimum and maximum") {
    val values  = "sub.u32 %valid, %lane, 10; cvt.rn.f32.s32 %valid, %valid;"
    val minimum = execute(s"$values redux.sync.min.f32 %result, %valid, -1;", ".version 8.6\n.target sm_100a")
    assertEquals(
      minimum.map(_._1),
      Vec.tabulate(64)(lane => java.lang.Float.floatToRawIntBits(if (lane < 32) -10f else 22f))
    )
    val magnitude = execute(s"$values redux.sync.max.abs.f32 %result, %valid, -1;", ".version 8.6\n.target sm_100a")
    assertEquals(
      magnitude.map(_._1),
      Vec.tabulate(64)(lane => java.lang.Float.floatToRawIntBits(if (lane < 32) 21f else 53f))
    )
    val nan = execute(
      s"$values setp.eq.u32 %p, %lane, 5; @%p mov.b32 %valid, 0x7fc00000; redux.sync.max.NaN.f32 %result, %valid, -1;" +
        " redux.sync.max.f32 %valid, %valid, -1;",
      ".version 8.6\n.target sm_100a"
    )
    assertEquals(
      nan,
      Vec.tabulate(64)(lane =>
        if (lane < 32) (0x7fffffff, java.lang.Float.floatToRawIntBits(21f))
        else (java.lang.Float.floatToRawIntBits(53f), java.lang.Float.floatToRawIntBits(53f))
      )
    )
  }
}
