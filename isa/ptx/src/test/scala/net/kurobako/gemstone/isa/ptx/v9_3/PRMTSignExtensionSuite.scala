package net.kurobako.gemstone.isa.ptx.v9_3

import execution.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*

class PRMTSignExtensionSuite extends munit.FunSuite {
  test("PRMT sign-extension selectors replicate the selected byte sign bit") {
    val source = """.version 9.3
      |.target sm_80
      |.address_size 64
      |.visible .entry permute(.param .u64 output) {
      |  .reg .u64 %p, %address, %offset;
      |  .reg .u32 %lane, %value, %result;
      |  ld.param.u64 %p, [output];
      |  mov.u32 %lane, %tid.x;
      |  mul.wide.u32 %offset, %lane, 4;
      |  add.u64 %address, %p, %offset;
      |  mov.b32 %value, %lane;
      |  prmt.b32 %result, %value, 0, 0x8880;
      |  st.global.b32 [%address], %result;
      |  ret;
      |}
      |""".stripMargin
    val kernel = compileKernel(source)
    val memory = ArrayCheckedMemory.allocate((256 * 4).unsafe[Int >= 0])
    val start  = memory.allocation.start
    assertEquals(
      execute(
        kernel,
        1,
        256.unsafe[Int > 0],
        Map(("output": NonEmpty[String]) -> Argument.fromLong(start.raw)),
        memory
      ),
      Right(())
    )

    val actual = (0 until 256).map { value =>
      memory.readLittleEndian(start.offset(value.toLong * 4), 4.unsafe[Int where Between[1, 8]]).toOption.get
    }
    val expected = (0 until 256).map(value => if ((value & 0x80) != 0) 0xffffff00L | value else value.toLong)
    assertEquals(actual, expected)
  }
}
