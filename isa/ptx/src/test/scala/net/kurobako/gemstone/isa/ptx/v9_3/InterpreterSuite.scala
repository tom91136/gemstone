package net.kurobako.gemstone.isa.ptx.v9_3

import VMExecution.*
import VMIR.*
import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class InterpreterSuite extends munit.FunSuite {
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
    val kernel = VMCompiler.compile(vectorAdd).toOption.get
    assertEquals(kernel.name, "vector_add")
    assertEquals(
      kernel.parameters,
      Seq(
        KernelParameter("input_a", 8),
        KernelParameter("input_b", 8),
        KernelParameter("output", 8)
      )
    )

    val memory =
      ArrayCheckedMemory.allocate(192, provenance = "vector-add arena")
    val base   = memory.allocation.start
    val a      = base
    val b      = base.offset(64L)
    val output = base.offset(128L)
    val values = Vec(1, 2, 3, 4)

    values.indices.foreach { index =>
      memory.writeInt32(a.offset(index.toLong * 4L), values(index)).toOption.get
      memory.writeInt32(b.offset(index.toLong * 4L), values(index) * 10).toOption.get
    }

    VMInterpreter
      .execute(
        kernel,
        geometry(blocks = 1, threads = values.size),
        Map(
          ("input_a": NonEmpty[String]) -> Argument.fromLong(a.raw),
          ("input_b": NonEmpty[String]) -> Argument.fromLong(b.raw),
          ("output": NonEmpty[String])  -> Argument.fromLong(output.raw)
        ),
        memory
      )
      .toOption
      .get

    assertEquals(
      values.indices.map(index => memory.readInt32(output.offset(index.toLong * 4L)).toOption.get),
      Seq(11, 22, 33, 44)
    )
  }

  test("scoped bare registers carry values between instructions in the same block") {
    // CUB's adjacent-difference kernel declares a block-scoped byte register and
    // reads it back through a conversion. Treating that bare name as a symbol
    // silently zeroed the staged value.
    val source = """
      |.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry scoped_register(.param .u64 output) {
      |  .reg .b32 %r<4>;
      |  .reg .b64 %rd<4>;
      |  ld.param.u64 %rd1, [output];
      |  mov.u32 %r1, 200;
      |  st.global.u8 [%rd1+4], %r1;
      |  { .reg .u8 datum; ld.ca.u8 datum, [%rd1+4]; cvt.u32.u8 %r2, datum;}
      |  st.global.u32 [%rd1], %r2;
      |  ret;
      |}
      |""".stripMargin
    val kernel = VMCompiler.compile(source).fold(error => fail(error.message), value => value)
    val memory = ArrayCheckedMemory.allocate(16)
    val base   = memory.allocation.start
    val result = VMInterpreter.execute(
      kernel,
      geometry(blocks = 1, threads = 1),
      Map(("output": NonEmpty[String]) -> Argument.fromLong(base.raw)),
      memory
    )
    assert(result.isRight, result.toString)
    assertEquals(memory.readInt32(base), Right(200))
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
    val kernel = VMCompiler.compile(source).fold(error => fail(error.message), value => value)
    assertEquals(kernel.sharedSymbols.keySet, Set[NonEmpty[String]]("first", "second"))
    assertNotEquals(
      kernel.sharedSymbols("first"),
      kernel.sharedSymbols("second")
    )
    assert(
      kernel.instructions.exists {
        case VMIR.Instruction.Move(_, Value.Symbol(name), _) if name == "first" => true
        case _                                                                  => false
      }
    )
    assert(
      kernel.instructions.exists {
        case VMIR.Instruction.Move(_, Value.Symbol(name), _) if name == "second" => true
        case _                                                                   => false
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
    val kernel = VMCompiler.compile(source).fold(error => fail(error.message), value => value)
    val memory =
      ArrayCheckedMemory.allocate(4, provenance = "generic-shared")
    val result = VMInterpreter.execute(
      kernel,
      geometry(blocks = 1, threads = 1),
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(memory.readInt32(memory.allocation.start).toOption.get, 255)
  }

  test("vector local stores accept immediate elements") {
    val source = """
      |.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry choose(.param .u64 output) {
      |  .reg .b32 %r<2>;
      |  .reg .b64 %rd<2>;
      |  ld.param.u64 %rd0, [output];
      |  st.local.v2.b32 [0], {0f00000000, 0f3f800000};
      |  ld.local.v2.b32 {%r0, %r1}, [0];
      |  st.global.u32 [%rd0], %r0;
      |  add.u64 %rd1, %rd0, 4;
      |  st.global.u32 [%rd1], %r1;
      |  ret;
      |}
      |""".stripMargin
    val kernel = VMCompiler.compile(source).fold(error => fail(error.message), identity)
    val memory = ArrayCheckedMemory.allocate(
      8,
      provenance = "immediate-local-vector-store"
    )
    val result = VMInterpreter.execute(
      kernel,
      geometry(blocks = 1, threads = 1),
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Vec.tabulate(2)(index => memory.readInt32(memory.allocation.start.offset(index.toLong * 4L)).toOption.get),
      Seq(0, 0x3f800000)
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
    val kernel     = VMCompiler.compile(source).fold(error => fail(error.message), value => value)
    val groupSize  = Dimensions(2, 2, 1)
    val groupCount = Dimensions(2, 2, 1)
    val launch     = LaunchGeometry.forLaunch(groupSize, groupCount).toOption.get
    val memory     = ArrayCheckedMemory.allocate(
      16 * 4,
      provenance = "ptx-3d-coordinates"
    )
    val result = VMInterpreter.execute(
      kernel,
      launch,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
      memory
    )

    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Vec.tabulate(16)(index => memory.readInt32(memory.allocation.start.offset(index.toLong * 4L)).toOption.get),
      Vec.from(0.until(16))
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
    val kernel = VMCompiler.compile(source).fold(error => fail(error.message), value => value)
    val result = VMInterpreter.execute(
      kernel,
      geometry(blocks = 1, threads = 1),
      Map.empty,
      ArrayCheckedMemory.allocate(4),
      TaskControl(TaskControl.Limits(maxInstructions = 8L))
    )
    assert(result.left.exists(_.message.contains("instruction budget of 8")))
  }

  test("execution observes cancellation before fetching an instruction") {
    val kernel       = VMCompiler.compile(vectorAdd).fold(error => fail(error.message), value => value)
    val cancellation = TaskControl()
    cancellation.cancel()
    val result = VMInterpreter.execute(
      kernel,
      geometry(blocks = 1, threads = 1),
      Map.empty,
      ArrayCheckedMemory.allocate(4),
      control = cancellation
    )
    assertEquals(result, Left(ExecutionFault.Cancelled))
  }
}
