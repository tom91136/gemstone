package net.kurobako.gemstone.isa.spirv

import ExtendedInstruction.GlslStd450 as Glsl
import ExtendedInstruction.OpenClStd as OpenCL
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.profile.*
import net.kurobako.gemstone.testing.*

class InstructionPipesSuite extends Suite {
  private val floatType   = 1
  private val integerType = 2
  private val value       = 3
  private val dest        = 4
  private val setId       = 5

  private val arithmetic = Instruction.definitions.filter { definition =>
    val grammar = InstructionRequirements.all(definition.opcodeNumber.toInt).grammarClass
    // ExtInst is classified by its extended set and opcode, covered by ProfileSuite
    (grammar == "Arithmetic" || grammar == "Bit") && definition.opcodeNumber.toInt != SPIRV.Opcode.ExtInst
  }

  private val glslInverse = Vec(
    Glsl.Asin,
    Glsl.Acos,
    Glsl.Atan,
    Glsl.Atan2,
    Glsl.Sinh,
    Glsl.Cosh,
    Glsl.Tanh,
    Glsl.Asinh,
    Glsl.Acosh,
    Glsl.Atanh
  )

  private val openClInverse = Vec(
    OpenCL.Acos,
    OpenCL.Acosh,
    OpenCL.Acospi,
    OpenCL.Asin,
    OpenCL.Asinh,
    OpenCL.Asinpi,
    OpenCL.Atan,
    OpenCL.Atan2,
    OpenCL.Atanh,
    OpenCL.Atanpi,
    OpenCL.Atan2pi,
    OpenCL.Sinh,
    OpenCL.Cosh,
    OpenCL.Tanh
  )

  private val openClPiScaled = Vec(
    OpenCL.Sinpi,
    OpenCL.Cospi,
    OpenCL.Tanpi,
    OpenCL.Cbrt,
    OpenCL.Hypot
  )

  private def decode(opcode: String, words: Int*): Instruction = {
    val definition = Instruction.definitions.find(_.opcode.raw == opcode).getOrElse(fail(s"no definition for $opcode"))
    definition.decoder(RawInstruction(definition.opcodeNumber, Vec.from(words.map(Word(_))))).fold(
      error => fail(error.message),
      identity
    )
  }

  private def extInst(opcode: Int, resultType: Int = floatType): Instruction =
    decode("OpExtInst", resultType, dest, setId, opcode, value)

  private def floats(bits: Int): Int => Option[Int] = id => Option.when(id == floatType)(bits)

  private def pipe(
      instruction: Instruction,
      width: Int => Option[Int] = floats(32),
      integer: Int => Boolean = _ => false,
      set: ExtendedSet = ExtendedSet.GLSL
  ): Option[Pipe] = InstructionPipes.of(instruction, _ => floatType, width, integer, _ => Some(set))

  private def unmatched(opcodes: Vec[Int], set: ExtendedSet, bits: Int, expected: Pipe): Vec[Int] =
    opcodes.filterNot(opcode => pipe(extInst(opcode), floats(bits), set = set).contains(expected))

  test("every arithmetic opcode in the generated set has a pipe") {
    val unclassified = arithmetic.flatMap { definition =>
      val words = definition.shapes.filter(_.quantifier == OperandQuantifier.One).zipWithIndex.map((shape, index) =>
        Word(if (shape.kind.category == OperandKind.Category.Id) index + 1 else 0)
      )
      val instruction =
        definition.decoder(RawInstruction(definition.opcodeNumber, words)).fold(error => fail(error.message), identity)
      if (InstructionPipes.of(instruction, _ => floatType, _ => Some(32), _ => false, _ => None).isEmpty)
        Some(definition.opcode.raw)
      else None
    }
    assertEquals(unclassified, Vec.empty[String])
  }

  test("float to integer conversions take the fma pipe at 16 and 32 bits") {
    Vec(16, 32).foreach { bits =>
      assertEquals(pipe(decode("OpConvertFToS", integerType, dest, value), floats(bits)), Some(Pipe.Fma))
      assertEquals(pipe(decode("OpConvertFToU", integerType, dest, value), floats(bits)), Some(Pipe.Fma))
    }
  }

  test("float to integer conversions from 64 bits take the fp64 pipe") {
    assertEquals(pipe(decode("OpConvertFToS", integerType, dest, value), floats(64)), Some(Pipe.Fp64))
    assertEquals(pipe(decode("OpConvertFToU", integerType, dest, value), floats(64)), Some(Pipe.Fp64))
  }

  test("glsl inverse and hyperbolic functions take the mufu pipe, fp64 at 64 bits") {
    assertEquals(unmatched(glslInverse, ExtendedSet.GLSL, 16, Pipe.Mufu), Vec.empty[Int])
    assertEquals(unmatched(glslInverse, ExtendedSet.GLSL, 32, Pipe.Mufu), Vec.empty[Int])
    assertEquals(unmatched(glslInverse, ExtendedSet.GLSL, 64, Pipe.Fp64), Vec.empty[Int])
  }

  test("opencl inverse and hyperbolic functions take the mufu pipe, fp64 at 64 bits") {
    assertEquals(unmatched(openClInverse, ExtendedSet.OpenCL, 16, Pipe.Mufu), Vec.empty[Int])
    assertEquals(unmatched(openClInverse, ExtendedSet.OpenCL, 32, Pipe.Mufu), Vec.empty[Int])
    assertEquals(unmatched(openClInverse, ExtendedSet.OpenCL, 64, Pipe.Fp64), Vec.empty[Int])
  }

  test("opencl pi-scaled trigonometry, cube root and hypot take the mufu pipe, fp64 at 64 bits") {
    assertEquals(unmatched(openClPiScaled, ExtendedSet.OpenCL, 16, Pipe.Mufu), Vec.empty[Int])
    assertEquals(unmatched(openClPiScaled, ExtendedSet.OpenCL, 32, Pipe.Mufu), Vec.empty[Int])
    assertEquals(unmatched(openClPiScaled, ExtendedSet.OpenCL, 64, Pipe.Fp64), Vec.empty[Int])
  }

  test("non-transcendental float functions take the fma pipe, fp64 at 64 bits") {
    assertEquals(pipe(extInst(Glsl.FAbs)), Some(Pipe.Fma))
    assertEquals(pipe(extInst(OpenCL.Fabs), set = ExtendedSet.OpenCL), Some(Pipe.Fma))
    assertEquals(pipe(extInst(Glsl.FAbs), floats(64)), Some(Pipe.Fp64))
  }

  test("non-transcendental integer functions take the alu pipe") {
    assertEquals(pipe(extInst(Glsl.SAbs, integerType), integer = _ == integerType), Some(Pipe.Alu))
    assertEquals(pipe(extInst(Glsl.FindILsb, integerType), integer = _ == integerType), Some(Pipe.Alu))
  }

  test("every function in the extended catalogue has a pipe") {
    val unclassified = Vec.from(ExtendedInstruction.names).flatMap { (name, functions) =>
      ExtendedSet.of(name).fold(Vec.empty[String]) { set =>
        Vec.from(functions).collect {
          case (function, opcode) if pipe(extInst(opcode), set = set).isEmpty => s"$name $function"
        }
      }
    }
    assertEquals(unclassified, Vec.empty[String])
  }
}
