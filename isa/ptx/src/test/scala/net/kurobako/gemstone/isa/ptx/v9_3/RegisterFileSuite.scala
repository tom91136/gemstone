package net.kurobako.gemstone.isa.ptx.v9_3

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
class RegisterFileSuite extends munit.FunSuite {
  test("compiled kernels size the dense register directory from numbered slots") {
    val namedOnly = compileKernel(""".version 8.0
.target sm_80
.address_size 64
.visible .entry named() { .reg .u32 %value; mov.u32 %value,1; ret; }""")
    val numbered = compileKernel(""".version 8.0
.target sm_80
.address_size 64
.visible .entry numbered() { .reg .u32 %r<18>; mov.u32 %r17,1; ret; }""")
    val predicateNamedScalar = compileKernel(""".version 8.0
.target sm_80
.address_size 64
.visible .entry predicate_named_scalar() {
  .reg .b64 %p63;
  mov.b64 %p63, 1;
  ret;
}""")

    assertEquals(namedOnly.registerChunkCount.raw, 1)
    assertEquals(numbered.registerChunkCount.raw, 33, numbered.program.instructions.toString)
    assertEquals(predicateNamedScalar.registerChunkCount.raw, 81)
  }

  test("predicate operands do not enlarge the general register directory") {
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
    assertEquals(kernel.registerChunkCount.raw, 33)
    val memory = ArrayCheckedMemory.allocate(4)
    assertEquals(
      execute(
        kernel,
        1,
        1,
        Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
        memory
      ),
      Right(())
    )
    assertEquals(memory.readInt32(memory.allocation.start), Right(42))
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
    assertEquals(kernel.registerChunkCount.raw, 81)
    val memory = ArrayCheckedMemory.allocate(4)
    assertEquals(
      execute(
        kernel,
        1,
        1,
        Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
        memory
      ),
      Right(())
    )
    assertEquals(memory.readInt32(memory.allocation.start), Right(42))
  }

  test("destination-only numbered registers in reachable functions size the directory") {
    val kernel = compileKernel(""".version 8.0
.target sm_80
.address_size 64
.func scalar() {
  .reg .b64 %p63;
  mov.b64 %p63, 1;
  ret;
}
.visible .entry caller() {
  call scalar, ();
  ret;
}""")

    assert(kernel.functions.keys.exists(_.raw == "scalar"))
    assertEquals(kernel.registerChunkCount.raw, 81)
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
      val memory = ArrayCheckedMemory.allocate(4)
      assertEquals(
        execute(
          kernel,
          1,
          1,
          Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
          memory
        ),
        Right(()),
        clues(predicate)
      )
      assertEquals(memory.readInt32(memory.allocation.start), Right(42), clues(predicate))
    }
  }
}
