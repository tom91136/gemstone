package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class GenericAddressOffsetSuite extends munit.FunSuite {
  for (sm <- Vec.nonEmpty(50, 60)) test(s"sm$sm generic negative offset preserves constant high addend and carry") {
    val module = SASSFixture.module(
      sm,
      Vec.nonEmpty(SASSFixture.Kernel("offset", 0x140, Vec.nonEmpty((0, 8)), registers = 12)),
      constants = Vec.nonEmpty(".nv.constant2.offset" -> LittleEndian.toVec(-1L, 4))
    )
    val body = Vec.nonEmpty(
      "LDC.64"       -> "R2,c[0x0][0x140]",
      "MOV"          -> "R4,12345",
      "STG.E"        -> "[R2],R4",
      "IADD"         -> "R6.CC,R2,256",
      "IADD.X"       -> "R7,R3,RZ",
      "LEA"          -> "R8.CC,R6,-256",
      "LEA.HI.X"     -> "P0,R9,R6,c[0x2][0x0],R7",
      "LD.E"         -> "R10,[R8],P0",
      "ISETP.EQ.AND" -> "P0,PT,RZ,RZ,PT",
      "STG.E"        -> "[R2],R10",
      "EXIT"         -> ""
    ).zipWithIndex.map { case ((opcode, args), index) =>
      SASSFixture.instruction(index * 16, opcode, args.split(',').filter(_.nonEmpty).toVec)
    }
    val service = SASSFixture.Execution()
    val output  = service.allocate(4L)
    val program =
      service.compile(SASSFixture(module, module.kernels.head, Vec.nonEmpty(DecodedFunction("offset", body))))
        .fold(error => fail(error.message), identity)
    val task = SASSFixture.task(
      "offset": NonEmpty[String],
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec.nonEmpty(KernelTask.Argument.Memory(output, 0L))
    )
    service.run(program, task).fold(error => fail(error.message), identity)
    assertEquals(service.readWords(output, 1), Vec.nonEmpty(12345L))
  }
}
