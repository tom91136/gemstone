package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class PairedNumericSuite extends munit.FunSuite {
  private def both(ptx: String, module: Cubin.Module, listing: String)(
      check: (SASSFixture.Execution, KernelTask => Unit, Boolean) => Unit
  ): Unit = {
    val kernel    = module.kernels.head
    val functions = ListingDecoder.parse(listing, kernel.name, module.sm).fold(fail(_), identity)
    val fixture   = SASSFixture(module, kernel, functions)
    for (isPtx <- Vec(true, false)) {
      val service                 = SASSFixture.Execution()
      val run: KernelTask => Unit = if (isPtx)
        task => service.runPtx(ptx, task).fold(error => fail(error.message), identity)
      else {
        val compiled = service.compile(fixture).fold(error => fail(error.message), identity)
        task => service.run(compiled, task).fold(error => fail(error.message), identity)
      }
      check(service, run, isPtx)
    }
  }

  for (
    (sm, ptx, listing) <- Vec(
      (70, ConversionFixtures.f2fF32F64Sm70ProbePtxSource, ConversionFixtures.f2fF32F64Sm70Probe),
      (89, ConversionFixtures.f2fF32F64Sm89ProbePtxSource, ConversionFixtures.f2fF32F64Sm89Probe)
    )
  ) test(s"PTX and sm_$sm F2F fixture independently match conversion boundary values") {
    val module = SASSFixture.module(
      sm,
      Vec(SASSFixture.Kernel(
        "f2f_conversion_probe",
        352,
        Vec(0 -> 8, 8 -> 8, 16 -> 8, 24 -> 8, 32 -> 4),
        registers = 16
      ))
    )
    both(ptx, module, listing) { (service, run, isPtx) =>
      val count = NumericCases.f64ToF32.size.max(NumericCases.f32ToF64.size)
      val in64  = service.allocate((NumericCases.f64ToF32.map(_._1) ++ Vec.fill(count - NumericCases.f64ToF32.size)(0L))
        .flatMap(LittleEndian.toVec(_, 8)))
      val in32 = service.allocate((NumericCases.f32ToF64.map(_._1) ++ Vec.fill(count - NumericCases.f32ToF64.size)(0L))
        .flatMap(LittleEndian.toVec(_, 4)))
      val out32 = service.allocate(Vec.fill(count * 4)(0xa5.toByte))
      val out64 = service.allocate(Vec.fill(count * 8)(0xa5.toByte))
      run(SASSFixture.task(
        "f2f_conversion_probe",
        LaunchGeometry.Dimensions(count.unsafe[Int > 0], 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(out32, 0L),
          KernelTask.Argument.Memory(in64, 0L),
          KernelTask.Argument.Memory(out64, 0L),
          KernelTask.Argument.Memory(in32, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(count.toLong, 4))
        )
      ))
      val actual32 = service.readWords(out32, NumericCases.f64ToF32.size.unsafe[Int >= 0])
      val actual64 = service.readWords(out64, NumericCases.f32ToF64.size.unsafe[Int >= 0], 8)
      assertEquals(actual32, NumericCases.f64ToF32.map(_._2), clues(isPtx))
      assertEquals(actual64, NumericCases.f32ToF64.map(_._2), clues(isPtx))
    }
  }

  test("PTX and FMNMX fixture independently match NaN selection and signed-zero extrema") {
    val module =
      SASSFixture.module(86, Vec(SASSFixture.Kernel("probe", 352, Vec(0 -> 8, 8 -> 8, 16 -> 8), registers = 14)))
    both(FmnmxFixtures.fmnmxSm86ProbePtxSource, module, FmnmxFixtures.fmnmxSm86Probe) { (service, run, isPtx) =>
      val cases  = NumericCases.f32Extrema
      val output = service.allocate(Vec.fill(cases.size * 8)(0xa5.toByte))
      val left   = service.allocate(cases.flatMap(value => LittleEndian.toVec(value._1, 4)))
      val right  = service.allocate(cases.flatMap(value => LittleEndian.toVec(value._2, 4)))
      run(SASSFixture.task(
        "probe",
        LaunchGeometry.Dimensions(cases.size.unsafe[Int > 0], 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(output, 0L),
          KernelTask.Argument.Memory(left, 0L),
          KernelTask.Argument.Memory(right, 0L)
        )
      ))
      val actual   = service.readWords(output, (cases.size * 2).unsafe[Int >= 0])
      val expected = cases.flatMap(value => Vec(value._3, value._4))
      assertEquals(if (isPtx) actual.map(NumericCases.canonicalFloat(_)) else actual, expected, clues(isPtx))
    }
  }

  private def scalar(instruction: String, left: Long, right: Long, alias: Boolean, suffix: String = ""): Long = {
    val module      = SASSFixture.module(89, Vec(SASSFixture.Kernel("numeric", 352, Vec(0 -> 8), registers = 8)))
    val destination = if (alias) "R2" else "R4"
    val lines       = Vec(
      "MOV R0, c[0x0][0x160]",
      "MOV R1, c[0x0][0x164]",
      s"MOV R2, 0x${java.lang.Long.toHexString(left)}",
      s"MOV R3, 0x${java.lang.Long.toHexString(right)}",
      s"$instruction $destination, R2, R3$suffix",
      s"STG.E [R0.64], $destination",
      "EXIT"
    )
    val listing = ".global numeric\nnumeric:\n" + lines.zipWithIndex.map { (line, index) =>
      f"/*${index * 16}%04x*/ $line%s;"
    }.mkString("\n")
    val function = ListingDecoder.parse(listing, "numeric", 89).fold(fail(_), identity)
    val fixture  = SASSFixture(module, module.kernels.head, function)
    val service  = SASSFixture.Execution()
    val output   = service.allocate(Vec.fill(4)(0xa5.toByte))
    val compiled = service.compile(fixture).fold(error => fail(error.message), identity)
    service.run(
      compiled,
      SASSFixture.task(
        "numeric",
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(output, 0L))
      )
    ).fold(error => fail(error.message), identity)
    service.readWords(output, 1).head
  }

  for {
    (opcode, cases) <- Vec("HADD2" -> NumericCases.packedAdd, "HMUL2" -> NumericCases.packedMultiply)
    alias           <- Vec(false, true)
  }
    test(s"$opcode shares PTX half edge cases with exact SASS NaN encodings, alias=$alias") {
      cases.foreach { (left, right, expected) =>
        assertEquals(scalar(opcode, left, right, alias), expected, clues(left, right))
      }
    }
  for (unordered <- Vec(false, true))
    test(s"FSET shares PTX ordered and unordered comparison cases, unordered=$unordered") {
      NumericCases.f32Comparisons.foreach { (left, right, equal, notEqualUnordered) =>
        val opcode = if (unordered) "FSET.NEU.AND" else "FSET.EQ.AND"
        val result = scalar(opcode, left, right, false, ", PT")
        assertEquals(result, if (if (unordered) notEqualUnordered else equal) 0xffffffffL else 0L, clues(left, right))
      }
    }

}
