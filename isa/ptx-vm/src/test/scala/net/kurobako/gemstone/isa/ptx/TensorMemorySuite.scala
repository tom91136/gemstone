package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

// The kernels and expectations are TensorMemoryCases', shared with the SASS suite that runs ptxas's output for them.
class TensorMemorySuite extends munit.FunSuite {
  private def run(kernel: Kernel, blocks: Int, threads: Int, memory: DeviceMemory, arguments: (String, Long)*)
      : Either[Fault, Unit] =
    execute(
      kernel,
      blocks.unsafe[Int > 0],
      threads.unsafe[Int > 0],
      Map(("out": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)) ++
        arguments.map((name, value) => name.unsafe[NonEmpty[String]] -> Argument.fromLong(value)),
      memory
    )

  test("tcgen05.st and tcgen05.ld place each shape's registers as the data movement figures show") {
    val memory = DeviceMemory(128 * 40)
    assertEquals(run(compileKernel(TensorMemoryCases.movementPtx), 1, 128, memory), Right(()))
    assertEquals(Vec.tabulate(128)(thread => memory.words(thread * 40L, 10)), TensorMemoryCases.movementExpected)
  }

  private def runProduct(product: TensorMemoryCases.Product): Vec[Long] = {
    val setup  = product.setup
    val count  = 128 * setup.columns
    val memory = DeviceMemory((count * 4 * 2 + 32768).unsafe[Int >= 0])
    assertEquals(memory.writeBytes(memory.start.offset(count * 4L), Vec.from(setup.image)), Right(()))
    setup.cells.zipWithIndex.foreach { (value, index) =>
      assertEquals(memory.writeInt32(memory.start.offset(count * 4L + 32768L + index * 4L), value.toInt), Right(()))
    }
    assertEquals(
      run(
        compileKernel(product.ptx),
        1,
        128,
        memory,
        "image"   -> (memory.start.raw + count * 4L),
        "cells"   -> (memory.start.raw + count * 4L + 32768L),
        "adesc"   -> product.adesc,
        "bdesc"   -> product.bdesc,
        "idesc"   -> product.idesc,
        "bytes"   -> 32768L,
        "columns" -> setup.columns.toLong,
        "enable"  -> (if (product.enable) 1L else 0L),
        "extra"   -> product.extra
      ),
      Right(())
    )
    memory.words(0L, count)
  }

  TensorMemoryCases.products.foreach { product =>
    test(s"tcgen05 ${product.name} with enable-input-d ${product.enable}: ${product.lines.mkString(" ")}") {
      assertEquals(runProduct(product), product.expected)
    }
  }

  test("tcgen05.alloc, tcgen05.dealloc and tcgen05.relinquish_alloc_permit fault on misuse") {
    def attempt(lines: String*): Either[Fault, Unit] = {
      val kernel = compileKernel(s""".version 8.7
        |.target sm_100a
        |.address_size 64
        |.visible .entry allocate(.param .u64 out) {
        | .shared .align 16 .b32 slot[4];
        | .reg .b32 %a, %b;
        |${lines.mkString("\n")}
        | ret;
        |}""".stripMargin)
      run(kernel, blocks = 1, threads = 32, DeviceMemory(16))
    }
    val allocate = "tcgen05.alloc.cta_group::1.sync.aligned.shared::cta.b32"
    val release  = "tcgen05.dealloc.cta_group::1.sync.aligned.b32"
    assertEquals(
      attempt(
        s"$allocate [slot], 256;",
        s"$allocate [slot+4], 128;",
        s"$allocate [slot+8], 128;",
        " ld.shared.b32 %a, [slot+4];",
        " ld.shared.b32 %b, [slot+8];",
        s"$release %a, 128;",
        s"$release %b, 128;",
        " ld.shared.b32 %a, [slot];",
        s"$release %a, 256;"
      ),
      Right(())
    )
    assert(attempt(s"$allocate [slot], 512;", s"$allocate [slot+4], 32;").isLeft)
    assert(attempt(s"$allocate [slot], 96;").isLeft)
    assert(attempt(" mov.b32 %a, 32;", s"$release %a, 32;").isLeft)
    assert(attempt("tcgen05.relinquish_alloc_permit.cta_group::1.sync.aligned;", s"$allocate [slot], 32;").isLeft)
  }

  test("tcgen05 .sync.aligned forms wait for every lane of a warp to reconverge after a divergent branch") {
    val kernel = compileKernel(""".version 8.7
      |.target sm_100a
      |.address_size 64
      |.visible .entry diverged(.param .u64 out) {
      | .shared .align 16 .b32 slot;
      | .reg .u64 %out, %offset;
      | .reg .b32 %t, %v, %taddr;
      | .reg .pred %others;
      | ld.param.u64 %out, [out];
      | mov.u32 %t, %tid.x;
      | mov.u32 %v, %t;
      | setp.ne.u32 %others, %t, 0;
      | @%others bra JOIN;
      | add.u32 %v, %v, 100;
      |JOIN:
      | tcgen05.alloc.cta_group::1.sync.aligned.shared::cta.b32 [slot], 32;
      | ld.shared.b32 %taddr, [slot];
      | tcgen05.st.sync.aligned.32x32b.x1.b32 [%taddr], {%v};
      | tcgen05.ld.sync.aligned.32x32b.x1.b32 {%v}, [%taddr];
      | tcgen05.wait::ld.sync.aligned;
      | mul.wide.u32 %offset, %t, 4;
      | add.u64 %out, %out, %offset;
      | st.global.u32 [%out], %v;
      | tcgen05.relinquish_alloc_permit.cta_group::1.sync.aligned;
      | tcgen05.dealloc.cta_group::1.sync.aligned.b32 %taddr, 32;
      | ret;
      |}""".stripMargin)
    val memory = DeviceMemory(32 * 4)
    assertEquals(run(kernel, blocks = 1, threads = 32, memory), Right(()))
    assertEquals(memory.words(0L, 32), Vec.tabulate(32)(thread => thread + (if (thread == 0) 100L else 0L)))
  }

  test("tcgen05.ld.red reduces each thread's loaded values by minimum or maximum, of magnitudes, with or without NaN") {
    val memory = DeviceMemory(32 * 32)
    TensorMemoryCases.reduceInputs.zipWithIndex.foreach((row, thread) =>
      row.zipWithIndex.foreach((value, column) =>
        assertEquals(memory.writeInt32(memory.start.offset(thread * 32L + column * 4L), value.toInt), Right(()))
      )
    )
    assertEquals(run(compileKernel(TensorMemoryCases.reducePtx), 1, 32, memory), Right(()))
    assertEquals(
      Vec.tabulate(32)(thread => memory.words(thread * 32L + 16, 4)),
      TensorMemoryCases.reduceExpected
    )
  }

  test(
    "cta_group::2 multiplies M = 256 across a CTA pair: each CTA holds half of A, half of B's columns and half of D"
  ) {
    val pair   = TensorMemoryCases.pair
    val memory = DeviceMemory(3 * 65536)
    assertEquals(memory.writeBytes(memory.start.offset(65536), pair.images), Right(()))
    pair.cells.zipWithIndex.foreach((value, index) =>
      assertEquals(memory.writeInt32(memory.start.offset(131072L + index * 4L), value.toInt), Right(()))
    )
    assertEquals(
      run(
        compileKernel(TensorMemoryCases.pairPtx),
        2,
        128,
        memory,
        "image" -> (memory.start.raw + 65536),
        "cells" -> (memory.start.raw + 131072),
        "adesc" -> Descriptors.tensorMemory(start = 0, leading = 16, stride = 1024, mode = 2),
        "bdesc" -> Descriptors.tensorMemory(start = 0, leading = 16, stride = 1024, mode = 2),
        "idesc" -> pair.idesc
      ),
      Right(())
    )
    assertEquals(memory.words(0L, 256 * 64), pair.expected)
  }
}
