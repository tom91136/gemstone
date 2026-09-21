package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*

import VMIR.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import net.kurobako.gemstone.memory.ArrayCheckedMemory
import net.kurobako.gemstone.vm.LaunchGeometry
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions

final class WarpReductionSuite extends munit.FunSuite {
  for {
    (operation, signed, negative) <-
      Seq(("min", true, false), ("min", false, false), ("max", true, true), ("max", false, false))
    mask <- Seq(0xffffffffL, 0x55555555L, 1L)
  }
    test(s"redux $operation signed=$signed negative=$negative mask=$mask") {
      val suffix = if (signed) "s32" else "u32"
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
      val kernel = VMCompiler.compile(source).fold(e => fail(e.toString), value => value)
      val memory = ArrayCheckedMemory.allocate(128)
      val values =
        Vector.tabulate(32)(lane =>
          if (negative) -100 + lane else if (signed) lane + 17 else if (lane % 2 == 0) 0x80000010 + lane else lane + 17
        )
      values.zipWithIndex.foreach { case (value, lane) =>
        assertEquals(memory.writeInt32(memory.allocation.start.offset(lane * 4L), value), Right(()))
      }
      val participating = values.zipWithIndex.collect {
        case (value, lane) if (mask & (1L << lane)) != 0 => if (signed) value.toLong else value.toLong & 0xffffffffL
      }
      val expected = (if (operation == "min") participating.min else participating.max).toInt
      val geometry = LaunchGeometry.forLaunch(Dimensions(32, 1, 1), Dimensions.One).toOption.get
      assert(VMInterpreter.execute(
        kernel,
        geometry,
        Map(ParameterName("data") -> Argument(memory.allocation.start.value)),
        memory
      ).isRight)
      values.zipWithIndex.foreach { case (value, lane) =>
        assertEquals(
          memory.readInt32(memory.allocation.start.offset(lane * 4L)),
          Right(if ((mask & (1L << lane)) != 0) expected else value)
        )
      }
    }
  test("redux rejects an executing lane absent from its member mask") {
    val kernel = VMCompiler.compile(""".version 8.0
.target sm_80
.address_size 64
.visible .entry empty() { .reg .u32 %value; redux.sync.min.u32 %value,1,0; ret; }""").toOption.get
    val geometry = LaunchGeometry.forLaunch(Dimensions.One, Dimensions.One).toOption.get
    assert(VMInterpreter.execute(
      kernel,
      geometry,
      Map.empty,
      ArrayCheckedMemory.allocate(4)
    ).left.exists(_.toString.contains("member mask")))
  }

}
