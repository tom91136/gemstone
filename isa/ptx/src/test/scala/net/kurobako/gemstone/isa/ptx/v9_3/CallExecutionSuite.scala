package net.kurobako.gemstone.isa.ptx.v9_3

import VMIR.*
import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

class CallExecutionSuite extends munit.FunSuite {
  test("device calls preserve thread coordinates") {
    val source = """.version 8.0
      .target sm_80
      .address_size 64
      .func store_lane(.param .u64 pointer) {
        .reg .u32 %lane;
        .reg .u64 %output, %offset;
        ld.param.u64 %output, [pointer];
        mov.u32 %lane, %tid.x;
        mul.wide.u32 %offset, %lane, 4;
        add.u64 %output, %output, %offset;
        st.global.u32 [%output], %lane;
        ret;
      }
      .visible .entry write(.param .u64 output) {
        .reg .u64 %pointer;
        ld.param.u64 %pointer, [output];
        { .param .u64 argument;
          st.param.u64 [argument], %pointer;
          call store_lane, (argument);
        }
        ret;
      }"""
    val kernel = VMCompiler.compile(source).fold(error => fail(error.toString), identity)
    val memory = ArrayCheckedMemory.allocate(16)
    val result = VMInterpreter.execute(
      kernel,
      geometry(blocks = 1, threads = 4),
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
      memory
    )
    assertEquals(result, Right(()))
    assertEquals(
      Vec.tabulate(4)(index => memory.readInt32(memory.allocation.start.offset(index.toLong * 4L)).toOption.get),
      Seq(0, 1, 2, 3)
    )
  }

  test("device function barriers synchronize lanes and share memory") {
    val source = """.version 8.0
      .target sm_80
      .address_size 64
      .func exchange(.param .u64 pointer) {
        .shared .align 4 .b8 scratch[8];
        .reg .u32 %lane, %other, %value;
        .reg .u64 %output, %slot, %offset;
        ld.param.u64 %output, [pointer];
        mov.u32 %lane, %tid.x;
        mul.wide.u32 %offset, %lane, 4;
        mov.u64 %slot, scratch;
        add.u64 %slot, %slot, %offset;
        st.shared.u32 [%slot], %lane;
        bar.sync 0;
        xor.b32 %other, %lane, 1;
        mul.wide.u32 %offset, %other, 4;
        mov.u64 %slot, scratch;
        add.u64 %slot, %slot, %offset;
        ld.shared.u32 %value, [%slot];
        mul.wide.u32 %offset, %lane, 4;
        add.u64 %output, %output, %offset;
        st.global.u32 [%output], %value;
        ret;
      }
      .visible .entry exchange_kernel(.param .u64 output) {
        .reg .u64 %pointer;
        ld.param.u64 %pointer, [output];
        { .param .u64 argument;
          st.param.u64 [argument], %pointer;
          call exchange, (argument);
        }
        ret;
      }"""
    val kernel = VMCompiler.compile(source).fold(error => fail(error.toString), identity)
    val memory = ArrayCheckedMemory.allocate(8)
    val result = VMInterpreter.execute(
      kernel,
      geometry(blocks = 1, threads = 2),
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
      memory
    )
    assertEquals(result, Right(()))
    assertEquals(memory.readInt32(memory.allocation.start), Right(1))
    assertEquals(memory.readInt32(memory.allocation.start.offset(4L)), Right(0))
  }

  test("device calls accept an unused unwritten parameter") {
    val source = """.version 8.0
      .target sm_80
      .address_size 64
      .func store(.param .u64 unused, .param .u64 pointer) {
        .reg .u64 %address;
        .reg .u32 %value;
        ld.param.u64 %address, [pointer];
        mov.u32 %value, 42;
        st.global.u32 [%address], %value;
        ret;
      }
      .visible .entry write(.param .u64 output) {
        .reg .u64 %address;
        ld.param.u64 %address, [output];
        { .param .u64 missing;
          .param .u64 supplied;
          st.param.u64 [supplied], %address;
          call store, (missing, supplied);
        }
        ret;
      }"""
    val kernel = VMCompiler.compile(source).fold(error => fail(error.toString), identity)
    val memory = ArrayCheckedMemory.allocate(4)
    val result = VMInterpreter.execute(
      kernel,
      geometry(blocks = 1, threads = 1),
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
      memory
    )
    assertEquals(result, Right(()))
    assertEquals(memory.readInt32(memory.allocation.start), Right(42))
  }

  test("atomics in device callees remain correct across parallel blocks") {
    val parallelCalls = java.util.concurrent.atomic.AtomicInteger(0)
    class RecordingWorkgroups extends Workgroups {

      override def shouldParallel(tasks: Long >= 0, workItems: Long >= 0): Boolean = tasks.raw > 1L
      override def parallelFor(
          tasks: Long >= 0
      )(task: Long >= 0 => Either[Fault, Unit]): Either[Fault, Unit] = {
        parallelCalls.incrementAndGet()
        Workgroups.Sequential.parallelFor(tasks)(task)
      }
    }
    val source     = """.version 8.0
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
    val kernel     = VMCompiler.compile(source).fold(error => fail(error.toString), value => value)
    val memory     = ArrayCheckedMemory.allocate(4)
    val workgroups = RecordingWorkgroups()
    val control    = TaskControl()
    val result     = VMInterpreter.execute(
      kernel,
      geometry(blocks = 4, threads = 1),
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
      memory,
      control,
      workgroups
    )
    assert(result.isRight, result.toString)
    assertEquals(memory.readInt32(memory.allocation.start), Right(4))
    assertEquals(parallelCalls.get(), 1)
  }

}
