package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.memory.ArrayCheckedMemory
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.scheduler.LaunchShape
import net.kurobako.gemstone.trace.TraceEvent

class PtxInterpreterSuite extends munit.FunSuite {
  private val vectorAdd = """
    |.version 8.0
    |.target sm_80
    |.address_size 64
    |
    |.visible .entry vector_add(
    |  .param .u64 input_a,
    |  .param .u64 input_b,
    |  .param .u64 output
    |) {
    |  .reg .b32 %r<4>;
    |  .reg .b64 %rd<4>;
    |  ld.param.u64 %rd1, [input_a];
    |  ld.param.u64 %rd2, [input_b];
    |  ld.param.u64 %rd3, [output];
    |  mov.u32 %r0, %tid.x;
    |  mul.wide.u32 %rd0, %r0, 4;
    |  add.s64 %rd1, %rd1, %rd0;
    |  add.s64 %rd2, %rd2, %rd0;
    |  add.s64 %rd3, %rd3, %rd0;
    |  ld.global.u32 %r1, [%rd1];
    |  ld.global.u32 %r2, [%rd2];
    |  add.u32 %r3, %r1, %r2;
    |  st.global.u32 [%rd3], %r3;
    |  ret;
    |}
    |""".stripMargin

  test("parse and deterministically execute integer vector addition") {
    val kernel = PtxParser.parse(vectorAdd).toOption.get
    assertEquals(kernel.name, "vector_add")
    assertEquals(kernel.parameters, Vector("input_a", "input_b", "output"))

    val memory = ArrayCheckedMemory.allocate(192, provenance = "vector-add arena")
    val base   = memory.allocation.start
    val a      = base
    val b      = base.offset(64L)
    val output = base.offset(128L)
    val values = Vector(1, 2, 3, 4)

    values.indices.foreach { index =>
      memory.writeInt32(a.offset(index.toLong * 4L), values(index)).toOption.get
      memory.writeInt32(b.offset(index.toLong * 4L), values(index) * 10).toOption.get
    }

    val result = PtxInterpreter
      .execute(
        kernel,
        LaunchShape.create(1, values.size).toOption.get,
        Map("input_a" -> a, "input_b" -> b, "output" -> output),
        memory
      )
      .toOption
      .get

    assertEquals(
      values.indices.map(index => memory.readInt32(output.offset(index.toLong * 4L)).toOption.get).toVector,
      Vector(11, 22, 33, 44)
    )
    assertEquals(
      result.trace.events.collect { case event: TraceEvent.OutputWrite => event.value },
      Vector(11, 22, 33, 44)
    )
  }

  test("reject unsupported instructions with a source line") {
    val error = PtxParser.parse(vectorAdd.replace("add.u32", "sub.u32")).left.toOption.get
    assert(error.line > 0)
    assert(error.message.contains("unsupported instruction"))
  }
}
