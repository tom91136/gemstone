package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Col

import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.ArrayCheckedMemory
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.trace.TraceEvent

final class InterpreterSuite extends munit.FunSuite {
  private def geometry(blocks: Int, threads: Int): LaunchGeometry =
    LaunchGeometry.create(Dimensions.create(threads, 1, 1).toOption.get, Dimensions.create(blocks, 1, 1).toOption.get)
      .toOption.get

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
    val kernel = ISA.compile(vectorAdd).toOption.get
    assertEquals(kernel.name, "vector_add")
    assertEquals(
      kernel.parameters.toSeq,
      Seq(KernelParameter("input_a", 8), KernelParameter("input_b", 8), KernelParameter("output", 8))
    )

    val memory = ArrayCheckedMemory.allocate(192, provenance = "vector-add arena")
    val base   = memory.allocation.start
    val a      = base
    val b      = base.offset(64L)
    val output = base.offset(128L)
    val values = Col(1, 2, 3, 4)

    values.indices.foreach { index =>
      memory.writeInt32(a.offset(index.toLong * 4L), values(index)).toOption.get
      memory.writeInt32(b.offset(index.toLong * 4L), values(index) * 10).toOption.get
    }

    val result = Interpreter
      .execute(
        kernel,
        geometry(1, values.size),
        Map(
          "input_a" -> Argument(a.value),
          "input_b" -> Argument(b.value),
          "output"  -> Argument(output.value)
        ),
        memory
      )
      .toOption
      .get

    assertEquals(
      values.indices.map(index => memory.readInt32(output.offset(index.toLong * 4L)).toOption.get).toSeq,
      Seq(11, 22, 33, 44)
    )
    assertEquals(
      result.trace.events.collect { case event: TraceEvent.OutputWrite => event.value }.toSeq,
      Seq(11, 22, 33, 44)
    )
  }

  test("lowers shared symbols as addresses") {
    val source = """
      |.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry shared_symbols() {
      |  .shared .align 4 .b8 first[4];
      |  .shared .align 4 .b8 second[4];
      |  .reg .b32 %r<2>;
      |  mov.u32 %r0, first;
      |  mov.u32 %r1, second;
      |  ret;
      |}
      |""".stripMargin
    val kernel = ISA.compile(source).fold(error => fail(error.message), identity)
    assertEquals(kernel.sharedSymbols.keySet, Set("first", "second"))
    assertNotEquals(kernel.sharedSymbols("first"), kernel.sharedSymbols("second"))
    assert(
      kernel.instructions.exists {
        case ExecutableInstruction.Move(_, Value.Symbol("first"), _) => true
        case _                                                       => false
      }
    )
    assert(
      kernel.instructions.exists {
        case ExecutableInstruction.Move(_, Value.Symbol("second"), _) => true
        case _                                                        => false
      }
    )
  }

  test("integer conversion and generic shared access support named registers") {
    val source = """
      |.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry convert_shared(.param .u64 output) {
      |  .shared .align 4 .b8 scratch[4];
      |  .reg .b8 %byte;
      |  .reg .b32 %wide;
      |  .reg .b64 %ptr;
      |  mov.u8 %byte, 255;
      |  cvt.u32.u8 %wide, %byte;
      |  cvta.shared.u64 %ptr, scratch;
      |  st.u32 [%ptr], %wide;
      |  ld.u32 %wide, [%ptr];
      |  ld.param.u64 %ptr, [output];
      |  st.global.u32 [%ptr], %wide;
      |  ret;
      |}
      |""".stripMargin
    val kernel = ISA.compile(source).fold(error => fail(error.message), identity)
    val memory = ArrayCheckedMemory.allocate(4, provenance = "generic-shared")
    val result = Interpreter.execute(
      kernel,
      geometry(1, 1),
      Map("output" -> Argument(memory.allocation.start.value)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(memory.readInt32(memory.allocation.start).toOption.get, 255)
  }

  test("execute signed and unsigned integer division and remainder") {
    val source = """
      |.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry divide(
      |  .param .s64 dividend,
      |  .param .u64 unsigned_dividend,
      |  .param .s64 divisor,
      |  .param .u64 unsigned_divisor,
      |  .param .u64 output
      |) {
      |  .reg .b64 %rd<9>;
      |  ld.param.s64 %rd0, [dividend];
      |  ld.param.u64 %rd1, [unsigned_dividend];
      |  ld.param.s64 %rd2, [divisor];
      |  ld.param.u64 %rd3, [unsigned_divisor];
      |  ld.param.u64 %rd4, [output];
      |  div.s64 %rd5, %rd0, %rd2;
      |  st.global.s64 [%rd4], %rd5;
      |  div.u64 %rd5, %rd1, %rd3;
      |  st.global.u64 [%rd4+8], %rd5;
      |  rem.s64 %rd6, %rd0, %rd2;
      |  st.global.s64 [%rd4+16], %rd6;
      |  rem.u64 %rd7, %rd1, %rd3;
      |  st.global.u64 [%rd4+24], %rd7;
      |  ret;
      |}
      |""".stripMargin
    val kernel = ISA.compile(source).fold(error => fail(error.message), identity)
    val memory = ArrayCheckedMemory.allocate(32, provenance = "integer-division")
    val result = Interpreter.execute(
      kernel,
      geometry(1, 1),
      Map(
        "dividend"          -> Argument(-21L),
        "unsigned_dividend" -> Argument(-1L),
        "divisor"           -> Argument(3L),
        "unsigned_divisor"  -> Argument(2L),
        "output"            -> Argument(memory.allocation.start.value)
      ),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(memory.readLittleEndian(memory.allocation.start, 8).toOption.get, -7L)
    assertEquals(
      memory.readLittleEndian(memory.allocation.start.offset(8L), 8).toOption.get,
      java.lang.Long.divideUnsigned(-1L, 2L)
    )
    assertEquals(memory.readLittleEndian(memory.allocation.start.offset(16L), 8).toOption.get, 0L)
    assertEquals(
      memory.readLittleEndian(memory.allocation.start.offset(24L), 8).toOption.get,
      java.lang.Long.remainderUnsigned(-1L, 2L)
    )
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
    val kernel = ISA.compile(source).fold(error => fail(error.message), identity)
    val memory = ArrayCheckedMemory.allocate(16, provenance = "divergent-barrier")
    val result = Interpreter.execute(
      kernel,
      geometry(1, 4),
      Map("output" -> Argument(memory.allocation.start.value)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Col.tabulate(4)(index => memory.readInt32(memory.allocation.start.offset(index.toLong * 4L)).toOption.get).toSeq,
      Seq(7, 7, 9, 9)
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
    val kernel = ISA.compile(source).fold(error => fail(error.message), identity)
    val memory = ArrayCheckedMemory.allocate(16, provenance = "divergent-warp-barrier")
    val result = Interpreter.execute(
      kernel,
      geometry(1, 4),
      Map("output" -> Argument(memory.allocation.start.value)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Col.tabulate(4)(index => memory.readInt32(memory.allocation.start.offset(index.toLong * 4L)).toOption.get).toSeq,
      Seq(7, 7, 7, 7)
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
    val kernel = ISA.compile(source).fold(error => fail(error.message), identity)
    val memory = ArrayCheckedMemory.allocate(16, provenance = "synchronized-warp-shuffle")
    val result = Interpreter.execute(
      kernel,
      geometry(1, 4),
      Map("output" -> Argument(memory.allocation.start.value)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Col.tabulate(4)(index => memory.readInt32(memory.allocation.start.offset(index.toLong * 4L)).toOption.get).toSeq,
      Seq(7, 7, 7, 7)
    )
  }

  test("three-dimensional launch coordinates expose PTX thread, block, block-size, and grid-size registers") {
    val source = """
      |.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry coordinates(.param .u64 output) {
      |  .reg .b32 %r<9>;
      |  .reg .b64 %rd<3>;
      |  ld.param.u64 %rd0, [output];
      |  mov.u32 %r0, %tid.x;
      |  mov.u32 %r1, %tid.y;
      |  mov.u32 %r2, %ctaid.x;
      |  mov.u32 %r3, %ctaid.y;
      |  mov.u32 %r4, %ntid.x;
      |  mov.u32 %r5, %ntid.y;
      |  mov.u32 %r6, %nctaid.x;
      |  mul.lo.u32 %r2, %r2, %r4;
      |  add.u32 %r0, %r2, %r0;
      |  mul.lo.u32 %r3, %r3, %r5;
      |  add.u32 %r1, %r3, %r1;
      |  mul.lo.u32 %r7, %r4, %r6;
      |  mul.lo.u32 %r8, %r1, %r7;
      |  add.u32 %r8, %r8, %r0;
      |  mul.wide.u32 %rd1, %r8, 4;
      |  add.u64 %rd2, %rd0, %rd1;
      |  st.global.u32 [%rd2], %r8;
      |  ret;
      |}
      |""".stripMargin
    val kernel     = ISA.compile(source).fold(error => fail(error.message), identity)
    val groupSize  = Dimensions.create(2, 2, 1).toOption.get
    val groupCount = Dimensions.create(2, 2, 1).toOption.get
    val launch     = LaunchGeometry.create(groupSize, groupCount).toOption.get
    val memory     = ArrayCheckedMemory.allocate(16 * 4, provenance = "ptx-3d-coordinates")
    val result     = Interpreter.execute(
      kernel,
      launch,
      Map("output" -> Argument(memory.allocation.start.value)),
      memory,
      captureTrace = false
    )

    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Col.tabulate(16)(index => memory.readInt32(memory.allocation.start.offset(index.toLong * 4L)).toOption.get).toSeq,
      Col.from(0.until(16))
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
    val kernel = ISA.compile(source).fold(error => fail(error.message), identity)
    val result = Interpreter.execute(
      kernel,
      geometry(1, 1),
      Map.empty,
      ArrayCheckedMemory.allocate(4),
      ExecutionControl.create(ExecutionLimits(maxInstructions = 8L, quantum = 2))
    )
    assert(result.left.exists(_.message.contains("instruction budget of 8")))
  }

  test("execution observes cancellation before fetching an instruction") {
    val kernel       = ISA.compile(vectorAdd).fold(error => fail(error.message), identity)
    val cancellation = CancellationToken.Source()
    cancellation.cancel()
    val result = Interpreter.execute(
      kernel,
      geometry(1, 1),
      Map.empty,
      ArrayCheckedMemory.allocate(4),
      control = ExecutionControl.create(cancellation = cancellation)
    )
    assertEquals(result, Left(ExecutionError("execution was cancelled")))
  }
}
