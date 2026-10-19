package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR as NvidiaIR
import net.kurobako.gemstone.memory.*

class WarpReductionSuite extends munit.FunSuite {
  private val reductionMasks = Vec(0xffffffffL, 0x55555555L, 1L)
  private val flags          = Vec(false, true)

  for (threads <- Vec[Int > 0](1, 32))
    test(s"unsupported warp reduction operations fail for $threads participating lanes") {
      val instructions = Vec(
        NvidiaIR.Instruction.WarpReduce(
          Register(0),
          Value.Immediate(7),
          Value.Immediate(if (threads == 1) 1 else 0xffffffffL),
          IntegerOperation.Subtract,
          32
        ),
        NvidiaIR.Instruction.Return
      )
      val kernel = Kernel(
        Entry(
          Routine("invalid_reduction", ExecutableProgram(instructions.map(PredicatedInstruction(_, None))), 0, 0),
          Vec.empty
        ),
        "PTX"
      )
      val result = execute(kernel, 1, threads, Map.empty, DeviceMemory(4))
      assert(result.left.exists(_.message.contains("unsupported warp reduction operation")))
    }

  for {
    (operation, signed, negative) <-
      Vec(
        ("min", true, false),
        ("min", false, false),
        ("max", true, true),
        ("max", false, false),
        ("and", false, false),
        ("or", false, false),
        ("xor", false, false)
      )
    mask <- reductionMasks
  }
    test(s"redux $operation signed=$signed negative=$negative mask=$mask") {
      val suffix = if (Set("and", "or", "xor")(operation)) "b32" else if (signed) "s32" else "u32"
      val source = s""".version 8.0
.target sm_80
.address_size 64
.visible .entry reduce(.param .u64 data) {
.reg .u64 %base,%address,%offset;
.reg .u32 %lane,%value,%result,%member;
.reg .pred %skip;
ld.param.u64 %base,[data];
mov.u32 %lane,%tid.x;
mul.wide.u32 %offset,%lane,4;
add.u64 %address,%base,%offset;
ld.global.u32 %value,[%address];
shr.u32 %member,$mask,%lane;
and.b32 %member,%member,1;
setp.eq.u32 %skip,%member,0;
@%skip bra DONE;
redux.sync.$operation.$suffix %result,%value,$mask;
st.global.u32 [%address],%result;
DONE:
ret;
}"""
      val kernel = compileKernel(source)
      val memory = DeviceMemory(128)
      val values =
        Vec.tabulate(32)(lane =>
          if (negative) -100 + lane else if (signed) lane + 17 else if (lane % 2 == 0) 0x80000010 + lane else lane + 17
        )
      values.zipWithIndex.foreach { case (value, lane) =>
        assertEquals(memory.writeInt32(memory.start.offset(lane * 4L), value), Right(()))
      }
      val participating = values.zipWithIndex.collect {
        case (value, lane) if (mask & (1L << lane)) != 0 => if (signed) value.toLong else value.toLong & 0xffffffffL
      }
      val expected = (operation match {
        case "min" => participating.min
        case "max" => participating.max
        case "and" => participating.reduce(_ & _)
        case "or"  => participating.reduce(_ | _)
        case "xor" => participating.reduce(_ ^ _)
      }).toInt
      assert(execute(
        kernel,
        1,
        32,
        Map(("data": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
        memory
      ).isRight)
      values.zipWithIndex.foreach { case (value, lane) =>
        assertEquals(
          memory.readInt32(memory.start.offset(lane * 4L)),
          Right(if ((mask & (1L << lane)) != 0) expected else value)
        )
      }
    }
  for {
    mask    <- Vec(0xffffffffL, 0xaaaaaaa0L)
    discard <- flags
  }
    test(s"elect selects one participating lane per warp, including a partial warp: mask=$mask discard=$discard") {
      val destination = if (discard) "_" else "%leader"
      val result      = if (discard) "mov.u32 %result,0;" else "mov.u32 %result,%leader;"
      val kernel      = compileKernel(s""".version 8.0
.target sm_90
.address_size 64
.visible .entry elect(.param .u64 data) {
.reg .u64 %base,%address,%offset;
.reg .u32 %thread,%lane,%member,%leader,%result,%flag;
.reg .pred %skip,%elected;
ld.param.u64 %base,[data];
mov.u32 %thread,%tid.x;
mov.u32 %lane,%laneid;
shr.u32 %member,$mask,%lane;
and.b32 %member,%member,1;
setp.eq.u32 %skip,%member,0;
@%skip bra DONE;
setp.lt.u32 %skip,%lane,16;
@%skip bra ELECT;
add.u32 %member,%member,1;
ELECT:
elect.sync $destination|%elected,$mask;
$result
selp.u32 %flag,256,0,%elected;
add.u32 %result,%result,%flag;
mul.wide.u32 %offset,%thread,4;
add.u64 %address,%base,%offset;
st.global.u32 [%address],%result;
DONE:
ret;
}""")
      val memory = DeviceMemory(160)
      assertEquals(
        execute(
          kernel,
          1,
          40,
          Map(("data": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
          memory
        ),
        Right(())
      )
      val leader = java.lang.Long.numberOfTrailingZeros(mask)
      (0 until 40).foreach { thread =>
        val lane     = thread % 32
        val expected = if ((mask & (1L << lane)) == 0L) 0
        else (if (discard) 0 else leader) + (if (lane == leader) 256 else 0)
        assertEquals(memory.readInt32(memory.start.offset(thread * 4L)), Right(expected))
      }
    }

  test("elect rejects an executing lane absent from its member mask") {
    val kernel = compileKernel(""".version 8.0
.target sm_90
.address_size 64
.visible .entry empty() { .reg .pred %elected; elect.sync _|%elected,0; ret; }""")
    assert(execute(kernel, 1, 1, Map.empty, DeviceMemory(4)).left.exists(
      _.toString.contains("member mask")
    ))
  }

  test("redux rejects an executing lane absent from its member mask") {
    val kernel = compileKernel(""".version 8.0
.target sm_80
.address_size 64
.visible .entry empty() { .reg .u32 %value; redux.sync.min.u32 %value,1,0; ret; }""")
    assert(execute(
      kernel,
      1,
      1,
      Map.empty,
      DeviceMemory(4)
    ).left.exists(_.toString.contains("member mask")))
  }

}
