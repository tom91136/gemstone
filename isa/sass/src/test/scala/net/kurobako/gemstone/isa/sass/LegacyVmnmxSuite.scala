package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*
import scala.util.Random

class LegacyVmnmxSuite extends munit.FunSuite {
  private def fixtureModule(sm: Int): Cubin.Module =
    SASSFixture.module(
      sm,
      Vec(
        SASSFixture.Kernel("nested_max", 320, Vec(0 -> 8, 8 -> 8, 16 -> 8, 24 -> 8), registers = 11),
        SASSFixture.Kernel("nested_min", 320, Vec(0 -> 8, 8 -> 8, 16 -> 8, 24 -> 8), registers = 11)
      )
    )

  private def words(values: Vec[Int]): Vec[Byte] =
    Vec.from(values.flatMap(value => LittleEndian.toVec(value.toLong, 4)))

  private val boundaryValues = Vec(
    Int.MinValue,
    Int.MinValue + 1,
    -16777217,
    -65537,
    -65536,
    -1,
    0,
    1,
    65535,
    65536,
    16777217,
    Int.MaxValue - 1,
    Int.MaxValue
  )

  private val cases = {
    val boundaries = for a <- boundaryValues; b <- boundaryValues; c <- boundaryValues yield (a, b, c)
    val random     = Random(0x51a7L)
    val sampled    = Vec.fill(10000)(
      (random.nextInt(), random.nextInt(), random.nextInt())
    )
    val unpadded = boundaries ++ sampled
    unpadded ++ Vec.fill((128 - (unpadded.size % 128)) % 128)((0, 0, 0))
  }

  test("CUDA 11.8 sm_50/sm_60 signed nested VMNMX kernels match scalar min/max") {
    Vec(50, 60).foreach { sm =>
      val module = fixtureModule(sm)
      assertEquals(module.sm, sm)
      val functions = Vec(
        ("nested_min", Vec(Instruction.Modifier("MIN")), (a: Int, b: Int, c: Int) => math.min(math.min(a, b), c)),
        (
          "nested_max",
          Vec(Instruction.Modifier("MX"), Instruction.Modifier("MAX")),
          (a: Int, b: Int, c: Int) => math.max(math.max(a, b), c)
        )
      )
      locally {
        val service    = SASSFixture.Execution()
        val count      = cases.size
        val allocation = count.toLong * 4L
        val output     = service.allocate(allocation.unsafe[Long > 0])
        val inputs     = Vec(
          service.allocate(allocation.unsafe[Long > 0]),
          service.allocate(allocation.unsafe[Long > 0]),
          service.allocate(allocation.unsafe[Long > 0])
        )
        val values = Vec(cases.map(_._1), cases.map(_._2), cases.map(_._3))
        inputs.zip(values).foreach { case (memory, data) =>
          assert(service.write(memory, 0L, words(data)).isRight)
        }

        functions.foreach { case (kernelName, opcode, expected) =>
          val listing = (sm, kernelName) match {
            case (50, "nested_min") => VideoMinMaxFixtures.videoMinmaxSm5060NestedMinSm50
            case (60, "nested_min") => VideoMinMaxFixtures.videoMinmaxSm5060NestedMinSm60
            case (50, "nested_max") => VideoMinMaxFixtures.videoMinmaxSm5060NestedMaxSm50
            case (60, "nested_max") => VideoMinMaxFixtures.videoMinmaxSm5060NestedMaxSm60
            case other              => fail(s"unknown VMNMX fixture $other")
          }
          val function = ListingDecoder.parse(listing, kernelName, sm).fold(fail(_), _.head)
          val vmnmx    = function.instructions.filter(_.mnemonic.raw == "VMNMX")
          assertEquals(vmnmx.map(_.modifiers), Vec(opcode), s"sm_$sm $kernelName")
          assertEquals(vmnmx.head.operands.size, 4, s"sm_$sm $kernelName")
          val sass = SASSFixture(
            module,
            module.kernels.find(_.name == kernelName).get,
            Vec(function)
          )
          val program = service.compile(sass)
            .fold(error => fail(error.message), identity)
          val task = SASSFixture.task(
            kernelName.unsafe[NonEmpty[String]],
            LaunchGeometry.Dimensions(128, 1, 1),
            LaunchGeometry.Dimensions(((count + 127) / 128).unsafe[Int > 0], 1, 1),
            Vec(
              KernelTask.Argument.Memory(inputs(0), 0L),
              KernelTask.Argument.Memory(inputs(1), 0L),
              KernelTask.Argument.Memory(inputs(2), 0L),
              KernelTask.Argument.Memory(output, 0L)
            )
          )
          service.run(program, task).fold(error => fail(error.message), identity)
          val bytes  = service.read(output, 0L, (count * 4).unsafe[Int >= 0]).toOption.get
          val actual = cases.indices.map { index =>
            (0 until 4).foldLeft(0)((value, byte) =>
              value | ((bytes(index * 4 + byte).toInt & 255) << (byte * 8))
            )
          }.toVec
          val expectedValues = cases.map { case (a, b, c) => expected(a, b, c) }
          assertEquals(actual, expectedValues, s"sm_$sm $kernelName")

          if (kernelName == "nested_min") {
            val instructionIndex = function.instructions.indexWhere(_.mnemonic.raw == "VMNMX")
            val instruction      = function.instructions(instructionIndex)
            val sentinel         = 0x13579bdf
            val initialize       = SASSFixture.instruction(
              instruction.address.raw - 1,
              "MOV32I",
              Vec(
                OperandPrinter.print(instruction.operands.head).fold(fail(_), identity),
                f"0x$sentinel%08x"
              ),
              None
            )
            val guarded = function.copy(instructions =
              function.instructions.patch(
                instructionIndex,
                Vec(
                  initialize,
                  instruction.rewrite(
                    guard = Parser.parse("@!PT NOP;").toOption.get.guard
                  )
                ),
                1
              )
            )
            val guardedSass = SASSFixture(
              module,
              module.kernels.find(_.name == kernelName).get,
              Vec(guarded)
            )
            val guardedProgram = service.compile(guardedSass).fold(error => fail(error.message), identity)
            service.run(guardedProgram, task).fold(error => fail(error.message), identity)

            val guardedBytes = service.read(output, 0L, (count * 4).unsafe[Int >= 0]).toOption.get
            assertEquals(guardedBytes, words(Vec.fill(count)(sentinel)), s"sm_$sm false VMNMX guard")
          }
        }
      }
    }
  }
}
