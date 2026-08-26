package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.execution.CancellationToken
import net.kurobako.gemstone.execution.ExecutionLimits
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
    val error = PtxParser.parse(vectorAdd.replace("add.u32", "xor.b32")).left.toOption.get
    assert(error.line > 0)
    assert(error.message.contains("unsupported instruction"))
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
    val kernel = PtxParser.parse(source).fold(error => fail(error.message), identity)
    val memory = ArrayCheckedMemory.allocate(16, provenance = "divergent-barrier")
    val result = PtxInterpreter.execute(
      kernel,
      LaunchShape.create(1, 4).toOption.get,
      Map("output" -> memory.allocation.start),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Vector.tabulate(4)(index => memory.readInt32(memory.allocation.start.offset(index.toLong * 4L)).toOption.get),
      Vector(7, 7, 9, 9)
    )
  }

  test("an infinite branch is stopped by its instruction budget") {
    val source = """
      |.version 8.0
      |.target sm_80
      |.visible .entry spin() {
      |again:
      |  bra again;
      |}
      |""".stripMargin
    val kernel = PtxParser.parse(source).fold(error => fail(error.message), identity)
    val result = PtxInterpreter.execute(
      kernel,
      LaunchShape.create(1, 1).toOption.get,
      Map.empty,
      ArrayCheckedMemory.allocate(4),
      ExecutionLimits(maxInstructions = 8L, quantum = 2)
    )
    assert(result.left.exists(_.message.contains("instruction budget of 8")))
  }

  test("execution observes cancellation before fetching an instruction") {
    val kernel       = PtxParser.parse(vectorAdd).fold(error => fail(error.message), identity)
    val cancellation = new CancellationToken.Source
    cancellation.cancel()
    val result = PtxInterpreter.execute(
      kernel,
      LaunchShape.create(1, 1).toOption.get,
      Map.empty,
      ArrayCheckedMemory.allocate(4),
      cancellation = cancellation
    )
    assertEquals(result, Left(PtxExecutionError("execution was cancelled")))
  }
}
