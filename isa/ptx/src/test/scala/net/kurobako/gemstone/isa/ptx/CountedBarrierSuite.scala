package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*

class CountedBarrierSuite extends munit.FunSuite {
  for ((count, peerMask, barrierId) <- Vec((64, 32, "mov.u32 %id, 0;"), (32, 1, "shr.u32 %id, %lane, 5;")))
    test(s"register barriers synchronise $count threads independently and reset for reuse") {
      val kernel = compileKernel(s""".version 8.0
        |.target sm_80
        |.address_size 64
        |.visible .entry counted(.param .u64 output) {
        | .shared .align 4 .b8 values[256];
        | .reg .u32 %lane, %peer, %id, %count, %value, %result;
        | .reg .u64 %out, %offset, %own, %other;
        | .reg .pred %outside;
        | ld.param.u64 %out, [output];
        | mov.u32 %lane, %tid.x;
        | mov.u32 %result, 999;
        | setp.ge.u32 %outside, %lane, 64;
        | @%outside bra join;
        | $barrierId
        | mov.u32 %count, $count;
        | mul.wide.u32 %offset, %lane, 4;
        | add.u64 %own, values, %offset;
        | xor.b32 %peer, %lane, $peerMask;
        | mul.wide.u32 %offset, %peer, 4;
        | add.u64 %other, values, %offset;
        | st.shared.u32 [%own], %lane;
        | bar.sync %id, %count;
        | ld.shared.u32 %result, [%other];
        | bar.sync %id, %count;
        | add.u32 %value, %result, 1000;
        | st.shared.u32 [%own], %value;
        | bar.sync %id, %count;
        | ld.shared.u32 %result, [%other];
        |join:
        | bar.sync 2;
        | mul.wide.u32 %offset, %lane, 4;
        | add.u64 %out, %out, %offset;
        | st.global.u32 [%out], %result;
        | ret;
        |}""".stripMargin)
      val memory = DeviceMemory(96 * 4)
      assertEquals(
        execute(
          kernel,
          1,
          96,
          Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
          memory
        ),
        Right(())
      )
      assertEquals(
        Vec.tabulate(96)(lane => memory.readInt32(memory.start.offset(lane.toLong * 4)).toOption.get),
        Vec.tabulate(96)(lane => if (lane < 64) lane + 1000 else 999)
      )
    }

  test("a pending barrier rejects inconsistent participant counts") {
    val kernel = compileKernel(""".version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry mismatch() {
      | .reg .u32 %lane, %count;
      | .reg .pred %first;
      | mov.u32 %lane, %tid.x;
      | setp.lt.u32 %first, %lane, 32;
      | selp.u32 %count, 64, 32, %first;
      | bar.sync 0, %count;
      | ret;
      |}""".stripMargin)
    val result = execute(kernel, 1, 64, Map.empty, DeviceMemory(4))
    assert(result.left.toOption.exists(_.message.contains("thread count changed from 64 to 32")), result.toString)
  }

  for ((id, count) <- Vec((0, 1), (0, 33), (0, 1056), (16, 32)))
    test(s"invalid barrier id $id or count $count is rejected") {
      val kernel = compileKernel(s""".version 8.0
        |.target sm_80
        |.address_size 64
        |.visible .entry invalid() {
        | bar.sync $id, $count;
        | ret;
        |}""".stripMargin)
      assert(execute(kernel, 1, 32, Map.empty, DeviceMemory(4)).isLeft)
    }
  test("nonblocking arrivals publish data, synchronise a subset and reuse the barrier") {
    val kernel = compileKernel(""".version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry producer_consumer(.param .u64 output) {
      | .shared .align 4 .u32 values[32];
      | .reg .u32 %lane, %iteration, %value, %result, %flag, %peer;
      | .reg .u64 %out, %offset, %slot;
      | .reg .pred %producer, %outside, %waiting, %again;
      | ld.param.u64 %out, [output];
      | mov.u32 %lane, %tid.x;
      | mov.u32 %iteration, 1;
      | mov.u32 %result, 999;
      | setp.lt.u32 %producer, %lane, 32;
      | setp.ge.u32 %outside, %lane, 64;
      |loop:
      | @%outside bra join;
      | @%producer bra produce;
      |poll:
      | ld.volatile.global.u32 %flag, [%out];
      | setp.lt.u32 %waiting, %flag, %iteration;
      | @%waiting bra poll;
      | bar.sync 0, 64;
      | sub.u32 %peer, %lane, 32;
      | mul.wide.u32 %offset, %peer, 4;
      | add.u64 %slot, values, %offset;
      | ld.shared.u32 %result, [%slot];
      | bra join;
      |produce:
      | mul.lo.u32 %value, %iteration, 100;
      | add.u32 %value, %value, %lane;
      | mul.wide.u32 %offset, %lane, 4;
      | add.u64 %slot, values, %offset;
      | st.shared.u32 [%slot], %value;
      | bar.arrive 0, 64;
      | st.volatile.global.u32 [%out], %iteration;
      |join:
      | bar.sync 1;
      | add.u32 %iteration, %iteration, 1;
      | setp.le.u32 %again, %iteration, 2;
      | @%again bra loop;
      | mul.wide.u32 %offset, %lane, 4;
      | add.u64 %slot, %out, %offset;
      | st.global.u32 [%slot+4], %result;
      | ret;
      |}""".stripMargin)
    val memory = DeviceMemory(97 * 4)
    assertEquals(
      execute(
        kernel,
        1,
        96,
        Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
        memory
      ),
      Right(())
    )
    assertEquals(
      Vec.tabulate(96)(lane => memory.readInt32(memory.start.offset((lane.toLong + 1) * 4)).toOption.get),
      Vec.tabulate(96)(lane => if (lane >= 32 && lane < 64) lane - 32 + 200 else 999)
    )
  }

  test("a nonblocking lane cannot contribute twice to one barrier generation") {
    val kernel = compileKernel(""".version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry duplicate() {
      | bar.arrive 0, 64;
      | bar.arrive 0, 64;
      | ret;
      |}""".stripMargin)
    val result = execute(kernel, 1, 32, Map.empty, DeviceMemory(4))
    assert(result.left.toOption.exists(_.message.contains("repeated arrival")), result.toString)
  }

  for ((operation, compare, expected) <- Vec(("or", "eq", 1), ("and", "ne", 0)))
    test(s"counted $operation reduction uses each participant's own predicate and destination") {
      val kernel = compileKernel(s""".version 8.0
        |.target sm_80
        |.address_size 64
        |.visible .entry reduction(.param .u64 output) {
        | .reg .u32 %lane, %count, %result;
        | .reg .u64 %out, %offset;
        | .reg .pred %outside, %first, %a, %b, %ra, %rb;
        | ld.param.u64 %out, [output];
        | mov.u32 %lane, %tid.x;
        | mov.u32 %count, 64;
        | mov.u32 %result, 999;
        | setp.ge.u32 %outside, %lane, 64;
        | @%outside bra join;
        | setp.lt.u32 %first, %lane, 32;
        | @%first bra first;
        | setp.$compare.u32 %b, %lane, 33;
        | bar.red.$operation.pred %rb, 0, %count, %b;
        | selp.u32 %result, 1, 0, %rb;
        | bra join;
        |first:
        | setp.$compare.u32 %a, %lane, 33;
        | bar.red.$operation.pred %ra, 0, %count, %a;
        | selp.u32 %result, 1, 0, %ra;
        |join:
        | bar.sync 1;
        | mul.wide.u32 %offset, %lane, 4;
        | add.u64 %out, %out, %offset;
        | st.global.u32 [%out], %result;
        | ret;
        |}""".stripMargin)
      val memory = DeviceMemory(96 * 4)
      assertEquals(
        execute(
          kernel,
          1,
          96,
          Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
          memory
        ),
        Right(())
      )
      assertEquals(
        Vec.tabulate(96)(lane => memory.readInt32(memory.start.offset(lane.toLong * 4)).toOption.get),
        Vec.tabulate(96)(lane => if (lane < 64) expected else 999)
      )
    }

}
