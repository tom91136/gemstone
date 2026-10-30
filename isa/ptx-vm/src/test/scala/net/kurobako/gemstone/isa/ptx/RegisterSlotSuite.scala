package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.Suite
class RegisterSlotSuite extends Suite {
  test("predicates and general registers occupy separate banks") {
    val kernel = compileKernel(""".version 8.0
.target sm_80
.address_size 64
.visible .entry predicates(.param .u64 output) {
  .reg .b64 %address;
  .reg .b32 %r0;
  .reg .pred %p1023, %copy;
  ld.param.u64 %address, [output];
  mov.u32 %r0, 41;
  setp.eq.u32 %p1023, %r0, 41;
  mov.pred %copy, %p1023;
  @%copy add.u32 %r0, %r0, 1;
  st.global.u32 [%address], %r0;
  ret;
}""")
    val memory = DeviceMemory(4)
    assertEquals(
      execute(
        kernel,
        1,
        1,
        Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
        memory
      ),
      Right(())
    )
    assertEquals(memory.readInt32(memory.start), Right(42))
  }

  test("a numbered p register can hold a non-predicate value") {
    val kernel = compileKernel(""".version 8.0
.target sm_80
.address_size 64
.visible .entry pointer(.param .u64 output) {
  .reg .b64 %p63;
  ld.param.u64 %p63, [output];
  st.global.u32 [%p63], 42;
  ret;
}""")
    val memory = DeviceMemory(4)
    assertEquals(
      execute(
        kernel,
        1,
        1,
        Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
        memory
      ),
      Right(())
    )
    assertEquals(memory.readInt32(memory.start), Right(42))
  }

  test("mov.pred reads the predicate bank regardless of the register name") {
    Vec("%p0", "%r0", "%chosen").foreach { predicate =>
      val kernel = compileKernel(s""".version 8.0
.target sm_80
.address_size 64
.visible .entry predicate_copy(.param .u64 output) {
  .reg .b64 %address;
  .reg .b32 %result;
  .reg .pred $predicate, %copy;
  ld.param.u64 %address, [output];
  mov.pred $predicate, 1;
  mov.pred %copy, $predicate;
  selp.u32 %result, 42, 0, %copy;
  st.global.u32 [%address], %result;
  ret;
}""")
      val memory = DeviceMemory(4)
      assertEquals(
        execute(
          kernel,
          1,
          1,
          Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
          memory
        ),
        Right(()),
        clues(predicate)
      )
      assertEquals(memory.readInt32(memory.start), Right(42), clues(predicate))
    }
  }

  for ((name, statement) <- Vec("a vote" -> "vote.sync.ballot.b32 %r, %p63, 1;", "a guard" -> "@%p63 mov.u32 %r, 1;"))
    test(s"undefined predicate used by $name reports its register name") {
      val kernel = compileKernel(s""".version 8.0
        .target sm_80
        .address_size 64
        .visible .entry undefined_predicate() {
          .reg .pred %p<64>;
          .reg .b32 %r;
          $statement
          ret;
        }""")
      val result = execute(kernel, 1, 1, Map.empty, DeviceMemory(0))
      assert(
        result.left.toOption.exists(_.message.contains("lane 0: undefined register %p63 at pc 0")),
        result.toString
      )
    }
}
