package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*

import VMIR.*
import VMExecution.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import net.kurobako.gemstone.Vec

import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.memory.ArrayCheckedMemory
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.memory.MemoryValueByteWidth
import net.kurobako.gemstone.vm.{Fault, LaunchGeometry, Scheduler, TaskControl, TaskLimits}
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions

final class InterpreterSuite extends munit.FunSuite {
  test("atomics in device callees remain correct across parallel blocks") {
    val parallelCalls = java.util.concurrent.atomic.AtomicInteger(0)
    final class RecordingScheduler extends Scheduler {
      override val parallelism: Int :| Positive                                                    = 4
      override def shouldParallel(tasks: Long :| Positive0, workItems: Long :| Positive0): Boolean = tasks > 1L
      override def parallelFor(
          tasks: Long :| Positive0
      )(task: Long :| Positive0 => Either[Fault, Unit]) = {
        parallelCalls.incrementAndGet()
        Scheduler.Sequential.parallelFor(tasks)(task)
      }
    }
    val source    = """.version 8.0
      .target sm_80
      .address_size 64
      .func increment(.param .u64 pointer) {
        .reg .u64 %p;
        .reg .u32 %old;
        ld.param.u64 %p, [pointer];
        atom.global.add.u32 %old, [%p], 1;
        ret;
      }
      .visible .entry count(.param .u64 output) {
        .reg .u64 %p;
        ld.param.u64 %p, [output];
        { .param .u64 argument;
          st.param.u64 [argument], %p;
          call increment, (argument);
        }
        ret;
      }"""
    val kernel    = VMCompiler.compile(source).fold(error => fail(error.toString), value => value)
    val memory    = ArrayCheckedMemory.allocate(4)
    val scheduler = RecordingScheduler()
    val control   = TaskControl.forScheduler(scheduler = scheduler)
    val result    = VMInterpreter.execute(
      kernel,
      geometry(4, 1),
      Map(ParameterName("output") -> Argument(memory.allocation.start.value)),
      memory,
      control,
      scheduler
    )
    assert(result.isRight, result.toString)
    assertEquals(memory.readInt32(memory.allocation.start), Right(4))
    assertEquals(parallelCalls.get(), 1)
  }

  private def geometry(blocks: Int, threads: Int): LaunchGeometry =
    LaunchGeometry.forLaunch(TestDimensions(threads), TestDimensions(blocks))
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
    val kernel = VMCompiler.compile(vectorAdd).toOption.get
    assertEquals(kernel.name, KernelName("vector_add"))
    assertEquals(
      kernel.parameters.toSeq,
      Seq(
        KernelParameter(ParameterName("input_a"), 8),
        KernelParameter(ParameterName("input_b"), 8),
        KernelParameter(ParameterName("output"), 8)
      )
    )

    val memory = ArrayCheckedMemory.allocate(192, provenance = "vector-add arena")
    val base   = memory.allocation.start
    val a      = base
    val b      = base.offset(64L)
    val output = base.offset(128L)
    val values = Vec(1, 2, 3, 4)

    values.indices.foreach { index =>
      memory.writeInt32(a.offset(index.toLong * 4L), values(index)).toOption.get
      memory.writeInt32(b.offset(index.toLong * 4L), values(index) * 10).toOption.get
    }

    val result = VMInterpreter
      .execute(
        kernel,
        geometry(1, values.size),
        Map(
          ParameterName("input_a") -> Argument(a.value),
          ParameterName("input_b") -> Argument(b.value),
          ParameterName("output")  -> Argument(output.value)
        ),
        memory
      )
      .toOption
      .get

    assertEquals(
      values.indices.map(index => memory.readInt32(output.offset(index.toLong * 4L)).toOption.get).toSeq,
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
    val kernel = VMCompiler.compile(source).fold(error => fail(error.message), value => value)
    assertEquals(kernel.sharedSymbols.keySet, Set(SymbolName("first"), SymbolName("second")))
    assertNotEquals(kernel.sharedSymbols(SymbolName("first")), kernel.sharedSymbols(SymbolName("second")))
    assert(
      kernel.instructions.exists {
        case VMIR.Instruction.Move(_, Value.Symbol(name), _) if name == SymbolName("first") => true
        case _                                                                              => false
      }
    )
    assert(
      kernel.instructions.exists {
        case VMIR.Instruction.Move(_, Value.Symbol(name), _) if name == SymbolName("second") => true
        case _                                                                               => false
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
    val memory = ArrayCheckedMemory.allocate(4, provenance = "generic-shared")
    val result = VMInterpreter.execute(
      kernel,
      geometry(1, 1),
      Map(ParameterName("output") -> Argument(memory.allocation.start.value)),
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
    val kernel = VMCompiler.compile(source).fold(error => fail(error.message), value => value)
    val memory = ArrayCheckedMemory.allocate(32, provenance = "integer-division")
    val result = VMInterpreter.execute(
      kernel,
      geometry(1, 1),
      Map(
        ParameterName("dividend")          -> Argument(-21L),
        ParameterName("unsigned_dividend") -> Argument(-1L),
        ParameterName("divisor")           -> Argument(3L),
        ParameterName("unsigned_divisor")  -> Argument(2L),
        ParameterName("output")            -> Argument(memory.allocation.start.value)
      ),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(memory.readLittleEndian(memory.allocation.start, MemoryValueByteWidth(8)).toOption.get, -7L)
    assertEquals(
      memory.readLittleEndian(memory.allocation.start.offset(8L), MemoryValueByteWidth(8)).toOption.get,
      java.lang.Long.divideUnsigned(-1L, 2L)
    )
    assertEquals(memory.readLittleEndian(memory.allocation.start.offset(16L), MemoryValueByteWidth(8)).toOption.get, 0L)
    assertEquals(
      memory.readLittleEndian(memory.allocation.start.offset(24L), MemoryValueByteWidth(8)).toOption.get,
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
    val kernel = VMCompiler.compile(source).fold(error => fail(error.message), value => value)
    val memory = ArrayCheckedMemory.allocate(16, provenance = "divergent-barrier")
    val result = VMInterpreter.execute(
      kernel,
      geometry(1, 4),
      Map(ParameterName("output") -> Argument(memory.allocation.start.value)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Vec.tabulate(4)(index => memory.readInt32(memory.allocation.start.offset(index.toLong * 4L)).toOption.get).toSeq,
      Seq(7, 7, 9, 9)
    )
  }

  test("workgroup barriers match divergent arrivals by hardware barrier ID") {
    val source = """
      |.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry divergent_barrier(.param .u64 output) {
      |  .shared .align 8 .b8 values[2048];
      |  .reg .pred %p;
      |  .reg .b32 %r<2>;
      |  .reg .b64 %rd<4>;
      |  .reg .f64 %fd;
      |  ld.param.u64 %rd0, [output];
      |  mov.u32 %r0, %tid.x;
      |  mul.wide.u32 %rd1, %r0, 8;
      |  mov.u64 %rd2, values;
      |  add.s64 %rd2, %rd2, %rd1;
      |  mov.f64 %fd, 0d3ff0000000000000;
      |  st.shared.f64 [%rd2], %fd;
      |  setp.lt.u32 %p, %r0, 128;
      |  @%p bra low;
      |  bar.sync 0;
      |  bra join;
      |low:
      |  bar.sync 0;
      |  ld.shared.f64 %fd, [%rd2];
      |join:
      |  setp.eq.u32 %p, %r0, 0;
      |  @%p st.global.f64 [%rd0], %fd;
      |  ret;
      |}
      |""".stripMargin
    val kernel = VMCompiler.compile(source).fold(error => fail(error.message), value => value)
    val memory = ArrayCheckedMemory.allocate(8, provenance = "divergent-workgroup-barrier-id")
    val result = VMInterpreter.execute(
      kernel,
      geometry(1, 256),
      Map(ParameterName("output") -> Argument(memory.allocation.start.value)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      memory.readLittleEndian(memory.allocation.start, MemoryValueByteWidth(8)).toOption.get,
      0x3ff0000000000000L
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
    val kernel = VMCompiler.compile(source).fold(error => fail(error.message), value => value)
    val memory = ArrayCheckedMemory.allocate(16, provenance = "divergent-warp-barrier")
    val result = VMInterpreter.execute(
      kernel,
      geometry(1, 4),
      Map(ParameterName("output") -> Argument(memory.allocation.start.value)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Vec.tabulate(4)(index => memory.readInt32(memory.allocation.start.offset(index.toLong * 4L)).toOption.get).toSeq,
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
    val kernel = VMCompiler.compile(source).fold(error => fail(error.message), value => value)
    val memory = ArrayCheckedMemory.allocate(16, provenance = "sync-warp-shuffle")
    val result = VMInterpreter.execute(
      kernel,
      geometry(1, 4),
      Map(ParameterName("output") -> Argument(memory.allocation.start.value)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Vec.tabulate(4)(index => memory.readInt32(memory.allocation.start.offset(index.toLong * 4L)).toOption.get).toSeq,
      Seq(7, 7, 7, 7)
    )
  }

  test("64-bit warp shuffles preserve the complete register value") {
    val source = """
      |.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry choose(.param .u64 output) {
      |  .reg .b64 %rd<4>;
      |  ld.param.u64 %rd0, [output];
      |  mov.b64 %rd1, 0x1122334455667788;
      |  shfl.sync.idx.b64 %rd2, %rd1, 0, 31, -1;
      |  st.global.b64 [%rd0], %rd2;
      |  ret;
      |}
      |""".stripMargin
    val kernel = VMCompiler.compile(source).fold(error => fail(error.message), value => value)
    val memory = ArrayCheckedMemory.allocate(8, provenance = "64-bit-warp-shuffle")
    val result = VMInterpreter.execute(
      kernel,
      geometry(1, 4),
      Map(ParameterName("output") -> Argument(memory.allocation.start.value)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      memory.readLittleEndian(memory.allocation.start, MemoryValueByteWidth(8)).toOption,
      Some(0x1122334455667788L)
    )
  }

  test("inline PTX warp-shuffle register aliases are accepted") {
    val source = """
      |.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry choose(.param .u64 output) {
      |  .reg .pred p;
      |  .reg .b32 lo;
      |  .reg .b32 %r<2>;
      |  .reg .b64 %rd<3>;
      |  ld.param.u64 %rd0, [output];
      |  mov.u32 lo, 7;
      |  shfl.sync.up.b32 lo|p, lo, 1, 0, -1;
      |  mov.u32 %r0, %tid.x;
      |  mul.wide.u32 %rd1, %r0, 4;
      |  add.u64 %rd2, %rd0, %rd1;
      |  st.global.u32 [%rd2], lo;
      |  ret;
      |}
      |""".stripMargin
    val kernel = VMCompiler.compile(source).fold(error => fail(error.message), value => value)
    val memory = ArrayCheckedMemory.allocate(8, provenance = "inline-warp-shuffle-registers")
    val result = VMInterpreter.execute(
      kernel,
      geometry(1, 2),
      Map(ParameterName("output") -> Argument(memory.allocation.start.value)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Vec.tabulate(2)(index => memory.readInt32(memory.allocation.start.offset(index.toLong * 4L)).toOption.get).toSeq,
      Seq(7, 7)
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
    val memory     = ArrayCheckedMemory.allocate(16 * 4, provenance = "ptx-3d-coordinates")
    val result     = VMInterpreter.execute(
      kernel,
      launch,
      Map(ParameterName("output") -> Argument(memory.allocation.start.value)),
      memory
    )

    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Vec.tabulate(16)(index => memory.readInt32(memory.allocation.start.offset(index.toLong * 4L)).toOption.get).toSeq,
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
      geometry(1, 1),
      Map.empty,
      ArrayCheckedMemory.allocate(4),
      TaskControl.forScheduler(TaskLimits(maxInstructions = 8L, quantum = 2))
    )
    assert(result.left.exists(_.message.contains("instruction budget of 8")))
  }

  test("execution observes cancellation before fetching an instruction") {
    val kernel       = VMCompiler.compile(vectorAdd).fold(error => fail(error.message), value => value)
    val cancellation = TaskControl.Cancellation.Source()
    cancellation.cancel()
    val result = VMInterpreter.execute(
      kernel,
      geometry(1, 1),
      Map.empty,
      ArrayCheckedMemory.allocate(4),
      control = TaskControl.forScheduler(cancellation = cancellation)
    )
    assertEquals(result, Left(ExecutionFault.Cancelled))
  }
}
