package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class HalfPackedArithmeticSuite extends munit.FunSuite {
  {
    val mmaModule = SASSFixture.module(
      89,
      Vec(
        SASSFixture.Kernel("hfma2_add_reference", 352, Vec(0 -> 8, 8 -> 8, 16 -> 8), registers = 12),
        SASSFixture.Kernel("hfma2_mma_probe", 352, Vec(0 -> 8, 8 -> 8, 16 -> 8), registers = 12)
      )
    )
    val selectionModule = SASSFixture.module(
      89,
      Vec(
        SASSFixture.Kernel("hadd2_selected_high", 0x160, Vec(0 -> 8, 8 -> 8, 16 -> 8), registers = 12),
        SASSFixture.Kernel("hadd2_selected_low", 0x160, Vec(0 -> 8, 8 -> 8, 16 -> 8), registers = 12)
      )
    )
    val fixtureModules = Map(
      "hfma2_add_reference" -> mmaModule,
      "hfma2_mma_probe"     -> mmaModule,
      "hfma2_selected"      -> SASSFixture.module(
        89,
        Vec(SASSFixture.Kernel("hfma2_selected", 352, Vec(0 -> 8, 8 -> 8, 16 -> 8, 24 -> 8), registers = 14))
      ),
      "hfma2_vector" -> SASSFixture.module(
        89,
        Vec(SASSFixture.Kernel("hfma2_vector", 352, Vec(0 -> 8, 8 -> 8, 16 -> 8, 24 -> 8), registers = 14))
      ),
      "hsetp2_probe" -> SASSFixture.module(
        89,
        Vec(SASSFixture.Kernel("hsetp2_probe", 352, Vec(0 -> 8, 8 -> 8, 16 -> 8), registers = 14))
      ),
      "hsetp2_ne_probe" -> SASSFixture.module(
        89,
        Vec(SASSFixture.Kernel("hsetp2_ne_probe", 352, Vec(0 -> 8, 8 -> 8, 16 -> 8), registers = 12))
      ),
      "hadd2_selected_high" -> selectionModule,
      "hadd2_selected_low"  -> selectionModule
    )
    def pair(value: Int): Long = value.toLong | (value.toLong << 16)

    def run(
        sassText: String,
        kernelName: String,
        cases: Vec[(Long, Long, Long, Long)],
        transform: DecodedFunction => DecodedFunction = identity
    ): Unit = {
      val module   = fixtureModules(kernelName)
      val function = ListingDecoder.parse(sassText, kernelName, 89).fold(fail(_), _.head)
      val sass     = SASSFixture(module, module.kernels.find(_.name == kernelName).get, Vec(transform(function)))
      val inputs   = Vec(
        cases.map(_._1),
        cases.map(_._2),
        cases.map(_._3)
      ).map(values => Vec.from(values).flatMap(LittleEndian.toVec(_, 4)))
      locally {
        val service     = SASSFixture.Execution()
        val output      = service.allocate((cases.size * 4L).unsafe[Long > 0])
        val allocations = inputs.map(bytes =>
          service.allocate(bytes.size.toLong.unsafe[Long > 0])
        )
        allocations.zip(inputs).foreach { case (allocation, bytes) =>
          assert(service.write(allocation, 0L, bytes).isRight)
        }
        val program = service.compile(sass)
          .fold(error => fail(error.message), identity)
        val task = SASSFixture.task(
          kernelName.unsafe[NonEmpty[String]],
          LaunchGeometry.Dimensions(cases.size.unsafe[Int > 0], 1, 1),
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Memory(allocations(0), 0L),
            KernelTask.Argument.Memory(allocations(1), 0L),
            KernelTask.Argument.Memory(allocations(2), 0L)
          )
        )
        service.run(program, task).fold(error => fail(error.message), identity)
        val actual = service.readWords(output, cases.size.unsafe[Int >= 0])
        assertEquals(actual, cases.map(_._4))
      }
    }

    def runThreeInput(
        sassFixture: String,
        kernelName: String,
        instructionText: String,
        cases: Vec[(Long, Long, Long)],
        transform: DecodedFunction => DecodedFunction = identity
    ): Unit = {
      val module   = fixtureModules(kernelName)
      val sassText = sassFixture
      val function = transform(ListingDecoder.parse(sassText, kernelName, 89).fold(fail(_), _.head))
      assert(function.instructions.exists(instruction =>
        Printer.print(instruction).toOption.exists(_.stripSuffix(";") == instructionText)
      ))
      val sass = SASSFixture(
        module,
        module.kernels.find(_.name == kernelName).get,
        Vec(function)
      )
      val inputs = Vec(cases.map(_._1), cases.map(_._2)).map(values =>
        Vec.from(values).flatMap(LittleEndian.toVec(_, 4))
      )
      locally {
        val service     = SASSFixture.Execution()
        val output      = service.allocate((cases.size * 4L).unsafe[Long > 0])
        val allocations = inputs.map(bytes =>
          service.allocate(bytes.size.toLong.unsafe[Long > 0])
        )
        allocations.zip(inputs).foreach { case (allocation, bytes) =>
          assert(service.write(allocation, 0L, bytes).isRight)
        }
        val program = service.compile(sass)
          .fold(error => fail(error.message), identity)
        val task = SASSFixture.task(
          kernelName.unsafe[NonEmpty[String]],
          LaunchGeometry.Dimensions(cases.size.unsafe[Int > 0], 1, 1),
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Memory(allocations(0), 0L),
            KernelTask.Argument.Memory(allocations(1), 0L)
          )
        )
        service.run(program, task).fold(error => fail(error.message), identity)
        val actual = service.readWords(output, cases.size.unsafe[Int >= 0])
        assertEquals(actual, cases.map(_._3))
      }
    }

    val exactRoundingCases = Vec(
      (0x5f90, 0xc7c0, 0x037c, 0xeb53),
      (0x630a, 0x82b3, 0x45c0, 0x45b7),
      (0xc65e, 0xc957, 0x712f, 0x7137),
      (0xb86d, 0xcc76, 0x8fc2, 0x48ef),
      (0x0001, 0x3800, 0x0000, 0x0000),
      (0x8000, 0x3c00, 0x8000, 0x8000)
    )

    test("compiler-produced sm_89 HFMA2 is fused and canonicalizes NaNs") {
      val sassText = Hfma2Fixtures.hfma2Sm89
      assert(sassText.contains("HFMA2 R11, R2, R5, R6"))
      val cases = exactRoundingCases.map { case (a, b, c, result) =>
        (pair(a), pair(b), pair(c), pair(result))
      } ++ Vec(
        (pair(0x7e01), pair(0x3c00), pair(0x0000), pair(0x7fff)),
        (pair(0x7c00), pair(0x0000), pair(0x3c00), pair(0x7fff))
      )
      run(Hfma2Fixtures.hfma2Sm89, "hfma2_vector", cases)
    }

    test("compiler-produced HFMA2 H0_H0 selectors duplicate the selected lane") {
      val sassText = Hfma2Fixtures.hfma2SelectedSm89
      assert(sassText.contains("HFMA2 R11, R2.H0_H0, R5.H0_H0, R6"))
      run(
        Hfma2Fixtures.hfma2SelectedSm89,
        "hfma2_selected",
        Vec(
          (0x3c003c00L, 0x40003c00L, 0xfc004900L, 0xfc004a00L),
          (0x04000001L, 0x38003c00L, 0x00000000L, 0x00000000L),
          (0x00007e01L, 0x3c000000L, 0x00000000L, 0x7fff7fffL)
        )
      )
    }

    test("HFMA2 H1_H1 sources can alias the destination while reading a separate addend") {
      val function = ListingDecoder.parse(
        Hfma2Fixtures.hfma2Sm89,
        "hfma2_vector",
        89
      ).fold(fail(_), _.head)
      val aliased = function.copy(instructions = function.instructions.map { instruction =>
        if (instruction.mnemonic.raw == "HFMA2")
          instruction.rewrite(operands = SASSFixture.operands("R2, R2.H1_H1, R5, R6"))
        else if (instruction.mnemonic.raw == "STG")
          instruction.rewrite(operands = SASSFixture.operands("[R8.64], R2"))
        else instruction
      })
      run(
        Hfma2Fixtures.hfma2Sm89,
        "hfma2_vector",
        Vec(
          (0x3c004000L, 0x42004200L, 0x3c003c00L, 0x44004400L),
          (0x35550000L, 0x35553555L, 0x00003555L, 0x2f1c371cL)
        ),
        _ => aliased
      )
    }

    test("HFMA2.MMA accumulator alias matches the fused packed operation for special values") {
      runThreeInput(
        Hfma2Fixtures.hfma2MmaAliasSm89,
        "hfma2_mma_probe",
        "HFMA2.MMA R5, R2, 1, 1, R5",
        NumericCases.packedAdd
      )
    }

    test("four-operand zero-multiplier HFMA2.MMA matches compiler-produced HFMA2") {
      runThreeInput(
        Hfma2Fixtures.hfma2MmaZeroAliasSm89,
        "hfma2_mma_probe",
        "HFMA2.MMA R5, R2, RZ, R5",
        Vec(
          (0x3c003c00L, 0xc0004000L, 0xc0004000L),
          (0x7e013c00L, 0x3c007e01L, 0x7fff7fffL),
          (0x7c003c00L, 0x3c007c00L, 0x7fff7c00L),
          (0x00018000L, 0x80000001L, 0x00000001L),
          (0x00008000L, 0x80000000L, 0x00000000L),
          (0x80008000L, 0x80008000L, 0x80008000L),
          (0x7e017e01L, 0x7e017e01L, 0x7fff7fffL),
          (0x3c003c00L, 0x00010001L, 0x00010001L)
        )
      )
    }

    test("compiler-produced selected-lane HSETP2.GEU matches half unordered comparisons") {
      runThreeInput(
        Hsetp2Fixtures.hsetp2Sm89,
        "hsetp2_probe",
        "HSETP2.GEU.AND P0, PT, R2.H0_H0, R5.H0_H0, PT",
        Vec(
          (0x38003c00L, 0x3c003800L, 0x78007001L),
          (0x3c003800L, 0x38003c00L, 0x70007800L),
          (0x00007e01L, 0x00003c00L, 0x00000001L),
          (0x00003c00L, 0x7e010000L, 0xfc020001L),
          (0x00000001L, 0x3c000000L, 0x78000001L),
          (0x3c008000L, 0x3c000000L, 0x78007801L),
          (0x3c000000L, 0x00010001L, 0x00027800L),
          (0x00007c00L, 0x38007e01L, 0x70000001L),
          (0x00003c00L, 0x00007e01L, 0x00000001L)
        )
      )
    }

    test("raw-register HSETP2.GEU uses the low comparison result") {
      runThreeInput(
        Hsetp2Fixtures.hsetp2GeuRawSm89,
        "hsetp2_probe",
        "HSETP2.GEU.AND P0, PT, R2, R5, PT",
        Vec(
          (0x38003c00L, 0x3c003800L, 0x78007001L),
          (0x3c003800L, 0x38003c00L, 0x70007800L),
          (0x00007e01L, 0x00003c00L, 0x00000001L),
          (0x00003c00L, 0x7e010000L, 0xfc020001L),
          (0x00000001L, 0x3c000000L, 0x78000001L),
          (0x3c008000L, 0x3c000000L, 0x78007801L),
          (0x3c000000L, 0x00010001L, 0x00027800L),
          (0x00007c00L, 0x38007e01L, 0x70000001L),
          (0x00003c00L, 0x00007e01L, 0x00000001L)
        )
      )
    }

    test("raw-register HSETP2.NE compares the low half with ordered NaN semantics") {
      runThreeInput(
        Hsetp2Fixtures.hsetp2NeRawSm89,
        "hsetp2_ne_probe",
        "HSETP2.NE.AND P0, PT, R2, RZ, PT",
        Vec(
          (0x3c000000L, 0L, 0x7800L),
          (0x3c008000L, 0L, 0x7800L),
          (0x3c000001L, 0L, 0x7801L),
          (0x7e013c00L, 0L, 0xfc03L),
          (0x3c007e01L, 0L, 0x7800L),
          (0x7c000000L, 0L, 0xf800L),
          (0x00007c00L, 0L, 1L),
          (0x0000fc00L, 0L, 1L),
          (0x7e010000L, 0L, 0xfc02L)
        )
      )
    }

    test("HSETP2 rejects unverified modifiers, destinations, and mixed selectors") {
      val module   = fixtureModules("hsetp2_ne_probe")
      val function = ListingDecoder.parse(
        Hsetp2Fixtures.hsetp2NeRawSm89,
        "hsetp2_ne_probe",
        89
      ).fold(fail(_), _.head)
      val unsupportedForms = Vec(
        "HSETP2.NE.OR"  -> Vec("P0", "PT", "R2", "RZ", "PT"),
        "HSETP2.LT.AND" -> Vec("P0", "PT", "R2", "RZ", "PT"),
        "HSETP2.NE.AND" -> Vec("P0", "P1", "R2", "RZ", "PT"),
        "HSETP2.NE.AND" -> Vec("P0", "PT", "R2.H0_H1", "RZ", "PT")
      )
      unsupportedForms.foreach { case (opcode, operands) =>
        val replacement  = Parser.parse(s"$opcode ${operands.mkString(", ")};").fold(fail(_), identity)
        val sassFunction = function.copy(instructions = function.instructions.map { instruction =>
          if (instruction.mnemonic.raw == "HSETP2") instruction.rewrite(replacement)
          else instruction
        })
        assert(
          SASSFixture.lower(module, module.kernels.find(_.name == "hsetp2_ne_probe").get, Vec(sassFunction)).isLeft
        )
      }
    }

    test("HFMA2.MMA unit multipliers support a separate accumulator") {
      runThreeInput(
        Hfma2Fixtures.hfma2MmaNonaliasSm89,
        "hfma2_mma_probe",
        "HFMA2.MMA R9, R2, 1, 1, R5",
        NumericCases.packedAdd
      )
    }

    test("compiler-produced packed HADD2 matches half addition and canonicalizes NaNs") {
      runThreeInput(
        Hfma2Fixtures.hfma2MmaAddReferenceSm89,
        "hfma2_add_reference",
        "HADD2 R9, R2, R5",
        NumericCases.packedAdd
      )
    }

    test("HADD2 duplicates selected H0 and H1 lanes across both packed results") {
      val cases = Vec(
        (0x42003c00L, 0x45004400L, 0x45004500L, 0x48004800L),
        (0x40003c00L, 0x42004000L, 0x42004200L, 0x45004500L)
      )
      runThreeInput(
        Hadd2Fixtures.hadd2SelectedLowSm89,
        "hadd2_selected_low",
        "HADD2 R9, R2.H0_H0, R5.H0_H0",
        cases.map { case (left, right, lowExpected, _) => (left, right, lowExpected) }
      )
      runThreeInput(
        Hadd2Fixtures.hadd2SelectedHighSm89,
        "hadd2_selected_high",
        "HADD2 R9, R2.H0_H0, R5.H0_H0",
        cases.map { case (left, right, _, highExpected) => (left, right, highExpected) }
      )
    }

    test("HADD2 H1_H1 selectors duplicate the selected high lane") {
      runThreeInput(
        Hfma2Fixtures.hfma2MmaAddReferenceSm89,
        "hfma2_add_reference",
        "HADD2 R9, R2.H1_H1, R5.H1_H1",
        Vec(
          (0x42003c00L, 0x45004400L, 0x48004800L),
          (0x40003c00L, 0x42004000L, 0x45004500L)
        ),
        function =>
          function.copy(instructions = function.instructions.map { instruction =>
            if (instruction.mnemonic.raw == "HADD2")
              instruction.rewrite(operands = SASSFixture.operands("R9, R2.H1_H1, R5.H1_H1"))
            else instruction
          })
      )
    }

    test("HFMA2.MMA rejects unverified multiplier and accumulator forms") {
      val module   = fixtureModules("hfma2_vector")
      val function = ListingDecoder.parse(
        Hfma2Fixtures.hfma2Sm89,
        "hfma2_vector",
        89
      ).fold(fail(_), _.head)
      val unsupportedForms = Vec(
        Vec("R6", "R2", "R3", "R6"),
        Vec("R6", "R2.H0_H0", "RZ", "R6"),
        Vec("R6", "R2", "RZ", "R7"),
        Vec("R6", "R2", "2", "1", "R6"),
        Vec("R6", "R2.H0_H0", "1", "1", "R6"),
        Vec("R6", "R2", "1", "1", "R7.H0_H0")
      )
      unsupportedForms.foreach { operands =>
        val replacement  = Parser.parse(s"HFMA2.MMA ${operands.mkString(", ")};").fold(fail(_), identity)
        val sassFunction = function.copy(instructions = function.instructions.map { instruction =>
          if (instruction.mnemonic.raw == "HFMA2") instruction.rewrite(replacement)
          else instruction
        })
        assert(
          SASSFixture.lower(module, module.kernels.find(_.name == "hfma2_vector").get, Vec(sassFunction)).isLeft
        )
      }
    }
  }

}
