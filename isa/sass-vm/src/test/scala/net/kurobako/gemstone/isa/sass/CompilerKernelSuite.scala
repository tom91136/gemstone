package net.kurobako.gemstone.isa.sass

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.Targets.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.either.*

class CompilerKernelSuite extends munit.FunSuite {
  private val Flags = Vec(false, true)

  private def floats(values: Vec[Float]): Vec[Long] =
    values.map(v => java.lang.Float.floatToRawIntBits(v).toLong & 0xffffffffL)

  for {
    sm        <- Vec(35, 50, 60, 70, 75, 80, 86, 87, 89, 90, 100, 103, 120)
    generated <- Flags
  }
    test(
      s"compiler-produced sm_$sm SAXPY executes predicated tails and aliased register pairs (generated=$generated)"
    ) {
      val name   = s"saxpySm${if (sm == 89) 86 else if (sm > 100) 100 else sm}"
      val listed = load(sm, Fixture.listing(name), "saxpy")
      val kernel =
        if (!generated) listed
        else {
          val words = codeWords(Fixture.code(name).toArray, sm).value
          listed.copy(functions =
            Vec(DecodedFunction(
              "saxpy",
              words.traverse((address, bytes) =>
                InstructionDecoder.decode(address, bytes, target(sm))
              ).value
            ))
          )
        }
      val count = 97
      val run   = kernel.launch(block = 32, grid = 4)(
        Arg.Buffer(Harness.words(floats(Vec.tabulate(count)(i => (i % 7).toFloat)))),
        Arg.Buffer(Harness.words(floats(Vec.tabulate(count)(i => (i - 40).toFloat)))),
        Arg.f32(2f),
        Arg.u32(count)
      )
      assertEquals(run.words(0), floats(Vec.tabulate(count)(i => (2 * (i - 40) + i % 7).toFloat)))
    }

  private val sgemm = Fixture.listing("scalarSgemmSm89")

  for (inner <- Vec(0, 1, 3, 4, 5, 9))
    test(s"generated SM89 scalar SGEMM executes K=$inner without NVIDIA decoding") {
      val instructions = Listing.words(sgemm, "scalar_sgemm", 89).value.map((address, bytes) =>
        InstructionDecoder.decode(address, bytes, target(89)).value
      )
      val kernel = load(89, sgemm, "scalar_sgemm").copy(functions = Vec(DecodedFunction("scalar_sgemm", instructions)))
      val a      = Vec.tabulate(8 * inner)(index => 0.5f * (index / inner + 1) + 0.125f * (index % inner + 1))
      val b      = Vec.tabulate(inner * 4)(index => 0.25f * (index / 4 + 1) * (index % 4 + 1))
      // The oracle accumulates in the kernel's order, so it folds over boxed floats.
      @SuppressWarnings(Array("baumkuchen:loop-boxing"))
      val expected = Vec.tabulate(32)(index =>
        (0 until inner).foldLeft(0f)((sum, k) => sum + a(index / 4 * inner + k) * b(k * 4 + index % 4))
      )
      val run = kernel.launch(32)(
        Arg.Buffer(words(floats(Vec.fill(32)(-999f)))),
        Arg.Buffer(words(floats(a))),
        Arg.Buffer(words(floats(b))),
        Arg.u32(inner)
      )
      assertEquals(run.words(0), floats(expected))
      assertEquals(run.words(1).take(a.size), floats(a))
      assertEquals(run.words(2).take(b.size), floats(b))
    }
}
