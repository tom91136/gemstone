package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class HalfConstantSuite extends munit.FunSuite {

  for {
    binary <- Vec.nonEmpty(false, true)
    mma    <- Vec.nonEmpty(false, true)
  }
    test(s"HFMA2 mma=$mma immediate addends preserve special values (${if (binary) "binary" else "text"} frontend)") {
      val cases = Vec.nonEmpty(
        "38004000" -> 0x38004000L,
        "7c003c00" -> 0x7c003c00L,
        "fc007c00" -> 0xfc007c00L,
        "80000000" -> 0x80000000L,
        "7c01fc01" -> 0x7fff7fffL,
        "7e013c00" -> 0x7fff3c00L,
        "fe017e01" -> 0x7fff7fffL
      )
      cases.foreach { case (name, expected) =>
        val text = Vec.nonEmpty(
          "38004000" -> Hfma2Fixtures.hfma2ConstantSm8938004000,
          "7c003c00" -> Hfma2Fixtures.hfma2ConstantSm897c003c00,
          "7c01fc01" -> Hfma2Fixtures.hfma2ConstantSm897c01fc01,
          "7e013c00" -> Hfma2Fixtures.hfma2ConstantSm897e013c00,
          "80000000" -> Hfma2Fixtures.hfma2ConstantSm8980000000,
          "fc007c00" -> Hfma2Fixtures.hfma2ConstantSm89Fc007c00,
          "fe017e01" -> Hfma2Fixtures.hfma2ConstantSm89Fe017e01
        ).find(_._1 == name).map(_._2).getOrElse(fail(s"missing constant fixture $name"))
        val functions = if (binary) {
          val decoded = SASSOracleWords.read(text).foldLeft[Either[String, Vec[DecodedInstruction]]](Right(Vec.empty)) {
            case (accumulated, (address, bytes)) => accumulated.flatMap { instructions =>
                InstructionDecoder.decodeTyped(address, bytes, "SM89").map(instructions :+ _)
              }
          }
          decoded.map(instructions => Vec.nonEmpty(DecodedFunction("hfma2_mma_probe", instructions))).fold(
            fail(_),
            identity
          )
        } else ListingDecoder.parse(text, "hfma2_mma_probe", 89).fold(fail(_), identity)
        val module = SASSFixture.module(
          89,
          Vec.nonEmpty(SASSFixture.Kernel(
            "hfma2_mma_probe",
            0x160,
            Vec.nonEmpty((0, 8), (8, 8), (16, 8)),
            registers = 12
          ))
        )
        val selected = if (mma) functions
        else functions.map { function =>
          function.copy(instructions = function.instructions.map { instruction =>
            instruction.operation match {
              case _: Instruction.Hfma2 => instruction.rewrite(modifiers = Vec.empty[Instruction.Modifier])
              case _                    => instruction
            }
          })
        }
        val lowered = SASSFixture(module, module.kernels.head, selected)
        locally {
          val service = SASSFixture.Execution()
          val output  = service.allocate(4L)
          val program = service.compile(lowered)
            .fold(error => fail(error.message), identity)
          val pointer = KernelTask.Argument.Memory(output, 0L)
          val task    = SASSFixture.task(
            "hfma2_mma_probe": NonEmpty[String],
            LaunchGeometry.Dimensions.One,
            LaunchGeometry.Dimensions.One,
            Vec.nonEmpty(pointer, pointer, pointer)
          )
          service.run(program, task).fold(error => fail(error.message), identity)
          val bytes = service.read(output, 0L, 4).toOption.get
          assertEquals(LittleEndian.decode(bytes, 0, 4), expected, name)
        }
      }
    }
}
