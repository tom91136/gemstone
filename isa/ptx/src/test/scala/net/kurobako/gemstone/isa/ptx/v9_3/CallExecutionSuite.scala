package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*
import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

class CallExecutionSuite extends munit.FunSuite {
  for (width <- Vec(32, 64))
    test(s"predicated $width-bit calls permit uninitialized local return loads before lane broadcast") {
      val source = s""".version 6.0
        .target sm_80
        .address_size 64
        .func (.param .b$width %value_out) result {
          st.param.b$width [%value_out], 42;
          ret;
        }
        .func worker(.param .u64 output) {
          .reg .pred %leader;
          .reg .u32 %lane, %low, %high;
          .reg .b$width %result;
          .reg .u64 %out, %offset;
          mov.u32 %lane, %laneid;
          setp.eq.u32 %leader, %lane, 0;
          {
            .param .b$width %value_in;
            st.param.b$width [%value_in], 99;
          }
          {
            .param .b$width %value_in;
            @%leader call (%value_in), result;
            ld.param.b$width %result, [%value_in];
          }
          ${if (width == 64) "mov.b64 {%low, %high}, %result;" else "mov.b32 %low, %result;"}
          shfl.sync.idx.b32 %low, %low, 0, 31, 0xffffffff;
          ld.param.u64 %out, [output];
          mul.wide.u32 %offset, %lane, 4;
          add.u64 %out, %out, %offset;
          st.global.u32 [%out], %low;
          ret;
        }
        .visible .entry main(.param .u64 output) {
          .reg .u64 %out;
          .param .u64 argument;
          ld.param.u64 %out, [output];
          st.param.u64 [argument], %out;
          call worker, (argument);
          ret;
        }"""
      val memory = ArrayCheckedMemory.allocate(128)
      assertEquals(
        execute(
          compileKernel(source),
          1,
          32,
          Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
          memory
        ),
        Right(())
      )
      for (lane <- 0 until 32)
        assertEquals(memory.readInt32(memory.allocation.start.offset((lane * 4).toLong)), Right(42))
    }

  test("missing kernel arguments are not initialized as local parameters") {
    val kernel = compileKernel(""".version 6.0
      .target sm_80
      .address_size 64
      .visible .entry main(.param .u32 required) {
        .reg .u32 %value;
        ld.param.u32 %value, [required];
        ret;
      }""")
    val result = execute(kernel, 1, 1, Map.empty, ArrayCheckedMemory.allocate(4))
    assert(result.left.exists(_.message.raw.contains("missing kernel argument required")))
  }

  test("GCC scoped return parameters from argument-free calls retain results") {
    val kernel = compileKernel(""".version 6.0
      .target sm_80
      .address_size 64
      .visible .func (.param .u32 %value_out) first {
        .reg .u32 %value;
        .reg .pred %p;
        mov.u32 %value, %tid.x;
        setp.eq.u32 %p, %value, 0;
        @%p bra zero;
        mov.u32 %value, 42;
        bra done;
      zero:
        mov.u32 %value, 21;
      done:
        st.param.u32 [%value_out], %value;
        ret;
      }
      .visible .func (.param .u32 %value_out) second {
        st.param.u32 [%value_out], 7;
        ret;
      }
      .visible .entry main(.param .u64 output) {
        .reg .u64 %out, %offset;
        .reg .u32 %a, %b, %lane;
        { .reg .u32 %temp; mov.u32 %temp, 0; }
        {
          .param .u32 %value_in;
          call (%value_in), first;
          ld.param.u32 %a, [%value_in];
        }
        {
          .param .u32 %value_in;
          call (%value_in), second;
          ld.param.u32 %b, [%value_in];
        }
        add.u32 %a, %a, %b;
        ld.param.u64 %out, [output];
        mov.u32 %lane, %tid.x;
        mul.wide.u32 %offset, %lane, 4;
        add.u64 %out, %out, %offset;
        st.global.u32 [%out], %a;
        ret;
      }""")
    val memory = ArrayCheckedMemory.allocate(128)
    assertEquals(
      execute(
        kernel,
        1,
        32,
        Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
        memory
      ),
      Right(())
    )
    assertEquals(memory.readInt32(memory.allocation.start), Right(28))
    assertEquals(memory.readInt32(memory.allocation.start.offset(4)), Right(49))
  }

  test("address-taken device functions are retained without a direct call") {
    val kernel = compileKernel(""".version 8.0
      .target sm_80
      .address_size 64
      .func address_taken() { ret; }
      .visible .entry main() {
        .reg .b64 %function;
        mov.b64 %function, address_taken;
        ret;
      }""")

    assert(kernel.functions.keys.exists(_.raw == "address_taken"))
  }

  for (width <- Vec(32, 64))
    test(s"$width-bit function pointers survive parameters, local spills and return values") {
      val source = s""".version 8.0
        .target sm_80
        .address_size 64
        .func store(.param .u64 pointer) {
          .reg .u64 %output;
          ld.param.u64 %output, [pointer];
          st.global.u32 [%output], 42;
          ret;
        }
        .func (.param .b$width result) spill(.param .b$width function) {
          .reg .b$width %pointer, %restored;
          ld.param.b$width %pointer, [function];
          st.local.b$width [0], %pointer;
          mov.b$width %pointer, 0;
          ld.local.b$width %restored, [0];
          st.param.b$width [result], %restored;
          ret;
        }
        .visible .entry main(.param .u64 output) {
          .reg .b$width %pointer, %restored;
          .reg .u64 %output;
          .param .b$width function, returned;
          .param .u64 argument;
          mov.b$width %pointer, store;
          st.param.b$width [function], %pointer;
          call (returned), spill, (function);
          ld.param.b$width %restored, [returned];
          ld.param.u64 %output, [output];
          st.param.u64 [argument], %output;
          call %restored, (argument);
          ret;
        }"""
      val kernel = compileKernel(source)
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

  test("overwriting a function pointer invalidates its old target") {
    val kernel = compileKernel(""".version 8.0
      .target sm_80
      .address_size 64
      .func target() { ret; }
      .visible .entry main() {
        .reg .b64 %pointer;
        mov.b64 %pointer, target;
        mov.b64 %pointer, 0;
        call %pointer, ();
        ret;
      }""")
    val result = execute(kernel, 1, 1, Map.empty, ArrayCheckedMemory.allocate(4))
    assert(result.left.exists(_.toString.contains("invalid function address 0x0")), result.toString)
  }

  test("device launches preserve function addresses and use their own dynamic shared size") {
    val memory = ArrayCheckedMemory.allocate(8)
    val source = s""".version 8.0
      .target sm_80
      .address_size 64
      .extern .func cudaLaunchDevice();
      .func store() {
        .reg .u64 %output;
        .reg .u32 %size;
        mov.u64 %output, ${memory.allocation.start.raw};
        st.global.u32 [%output], 42;
        mov.u32 %size, %dynamic_smem_size;
        st.global.u32 [%output+4], %size;
        ret;
      }
      .visible .entry parent() {
        .reg .b64 %function;
        .param .b64 target, buffer, stream;
        .param .b32 shared;
        .param .align 4 .b8 grid[12], block[12];
        mov.b64 %function, child;
        st.local.b64 [0], %function;
        mov.b64 %function, 0;
        ld.local.b64 %function, [0];
        st.param.b64 [target], %function;
        st.param.b64 [buffer], 0;
        st.param.b64 [stream], 0;
        st.param.b32 [shared], 64;
        st.param.b32 [grid], 1;
        st.param.b32 [grid+4], 1;
        st.param.b32 [grid+8], 1;
        st.param.b32 [block], 1;
        st.param.b32 [block+4], 1;
        st.param.b32 [block+8], 1;
        call cudaLaunchDevice, (target, buffer, grid, block, shared, stream);
        ret;
      }
      .visible .entry child() {
        .reg .b64 %function;
        mov.b64 %function, store;
        call %function, ();
        ret;
      }"""
    val program   = Parser.parse(source).toOption.get
    val entries   = program.statements.collect { case entry: Statement.Entry => entry }
    val functions = program.statements.collect { case function: Statement.Function => function }
    val kernel    = VMCompiler.compile(
      entry = entries.head,
      functions = functions,
      globalSymbols = Map.empty,
      additionalSharedSymbols = Map.empty,
      additionalEntries = entries
    ).toOption.get.copy(dynamicSharedBytes = 192)
    assertEquals(execute(kernel, 1, 1, Map.empty, memory), Right(()))
    assertEquals(memory.readInt32(memory.allocation.start), Right(42))
    assertEquals(memory.readInt32(memory.allocation.start.offset(4)), Right(64))
  }

  test("GCC percent-prefixed parameter ranges retain block scope and scalar returns") {
    val source = """.version 6.0
      .target sm_80
      .address_size 64
      .func (.param .u32 %value_out) sum(.param .u32 %in_ar0, .param .u32 %in_ar1) {
        .reg .u32 %r<3>;
        ld.param.u32 %r0, [%in_ar0];
        ld.param.u32 %r1, [%in_ar1];
        add.u32 %r2, %r0, %r1;
        st.param.u32 [%value_out], %r2;
        ret;
      }
      .visible .entry main(.param .u64 %arg) {
        .reg .u64 %output;
        .reg .u32 %result, %inner;
        ld.param.u64 %output, [%arg];
        {
          .param .u32 %P<2>, %R;
          st.param.u32 [%P0], 21;
          st.param.u32 [%P1], 40;
          {
            .param .u32 %P<2>, %R;
            st.param.u32 [%P0], 3;
            st.param.u32 [%P1], 4;
            call.uni (%R), sum, (%P0, %P1);
            ld.param.u32 %inner, [%R];
          }
          call.uni (%R), sum, (%P0, %P1);
          ld.param.u32 %result, [%R];
        }
        add.u32 %result, %result, %inner;
        st.global.u32 [%output], %result;
        ret;
      }"""
    val kernel = compileKernel(source)
    val memory = ArrayCheckedMemory.allocate(4)
    assertEquals(
      execute(
        kernel,
        1,
        1,
        Map(("%arg": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
        memory
      ),
      Right(())
    )
    assertEquals(memory.readInt32(memory.allocation.start), Right(68))
  }

  test("reachable direct calls are lowered before address-taken functions") {
    val source = """.version 8.0
      .target sm_80
      .address_size 64
      .func called() {
        not_a_call_instruction.u32 %r0;
        ret;
      }
      .func address_taken() {
        not_an_address_instruction.u32 %r1;
        ret;
      }
      .visible .entry main() {
        .reg .b64 %function;
        mov.b64 %function, address_taken;
        call called, ();
        ret;
      }"""

    assert(VMCompiler.compile(source).left.exists(_.message.contains("not_a_call_instruction")))
  }

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
    val kernel = compileKernel(source)
    val memory = ArrayCheckedMemory.allocate(16)
    val result = execute(
      kernel,
      1,
      4,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
      memory
    )
    assertEquals(result, Right(()))
    assertEquals(
      Vec.tabulate(4)(index => memory.readInt32(memory.allocation.start.offset(index.toLong * 4L)).toOption.get),
      Vec(0, 1, 2, 3)
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
    val kernel = compileKernel(source)
    val memory = ArrayCheckedMemory.allocate(8)
    val result = execute(
      kernel,
      1,
      2,
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
    val kernel = compileKernel(source)
    val memory = ArrayCheckedMemory.allocate(4)
    val result = execute(
      kernel,
      1,
      1,
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
    val kernel     = compileKernel(source)
    val memory     = ArrayCheckedMemory.allocate(4)
    val workgroups = RecordingWorkgroups()
    val control    = TaskControl()
    val result     = execute(
      kernel,
      4,
      1,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
      memory,
      control,
      workgroups
    )
    assert(result.isRight, result.toString)
    assertEquals(memory.readInt32(memory.allocation.start), Right(4))
    assertEquals(parallelCalls.get(), 1)
  }

  test("array return parameters retain partial writes independently across calls") {
    val kernel = compileKernel(""".version 8.0
      .target sm_80
      .address_size 64
      .func (.param .align 4 .b8 result[12]) fill(.param .u32 value) {
        .reg .u32 %r0;
        ld.param.u32 %r0, [value];
        st.param.u32 [result+8], %r0;
        add.u32 %r0, %r0, 1;
        st.param.u32 [result+4], %r0;
        ret;
      }
      .visible .entry arrays(.param .u64 output) {
        .reg .u64 %rd0;
        .reg .u32 %r<4>;
        .param .u32 argument;
        .param .align 4 .b8 first[12];
        .param .align 4 .b8 second[12];
        ld.param.u64 %rd0, [output];
        st.param.u32 [argument], 17;
        call (first), fill, (argument);
        st.param.u32 [argument], 23;
        call (second), fill, (argument);
        ld.param.u32 %r0, [first];
        ld.param.u32 %r1, [first+4];
        ld.param.u32 %r2, [first+8];
        ld.param.u32 %r3, [second+8];
        st.global.v4.u32 [%rd0], {%r0, %r1, %r2, %r3};
        ret;
      }""")
    val memory = ArrayCheckedMemory.allocate(16)
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
    assertEquals(
      Vec.tabulate(4)(index => memory.readInt32(memory.allocation.start.offset(index.toLong * 4)).toOption.get),
      Vec(0, 18, 17, 23)
    )
  }

}
