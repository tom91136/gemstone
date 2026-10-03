package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class LegacyIntegerLoweringSuite extends munit.FunSuite {
  private def saxpyModule: Cubin.Module = SASSFixture.module(
    86,
    Vec(SASSFixture.Kernel("saxpy", 352, Vec((0, 8), (8, 8), (16, 4), (20, 4)), registers = 10))
  )
  private def icmpModule: Cubin.Module = SASSFixture.module(
    35,
    Vec(SASSFixture.Kernel("probe", 320, Vec((0, 8), (8, 4), (12, 4), (16, 4)), registers = 7))
  )

  private def execute(body: Vec[(String, String)], expected: Vec[Int]): Unit = {
    val module       = saxpyModule
    val instructions = body.zipWithIndex.map { case ((op, operands), index) =>
      SASSFixture.instruction(
        index * 16,
        op,
        if (operands.isEmpty) Vec.empty else operands.split(',').toVec.map(_.trim),
        None
      )
    }
    val sass = SASSFixture(module, module.kernels.head, Vec(DecodedFunction("saxpy", instructions)))
    locally {
      val service = SASSFixture.Execution()
      val output  =
        service.allocate((expected.size * 4L).unsafe[Long > 0])
      val program = service.compile(sass)
        .fold(e => fail(e.message), identity)
      val task = SASSFixture.task(
        "saxpy": NonEmpty[String],
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(output, 0L),
          KernelTask.Argument.Memory(output, 0L),
          KernelTask.Argument.Scalar(Vec.from(Array.fill[Byte](4)(0))),
          KernelTask.Argument.Scalar(Vec.from(Array(1.toByte, 0, 0, 0)))
        )
      )
      service.run(program, task).fold(e => fail(e.message), identity)
      val bytes  = service.read(output, 0L, (expected.size * 4).unsafe[Int >= 0]).toOption.get
      val actual = (0 until expected.size).map { i =>
        val at = i * 4
        (bytes(at) & 255) | ((bytes(at + 1) & 255) << 8) | ((bytes(at + 2) & 255) << 16) | (bytes(at + 3) << 24)
      }.toVec
      assertEquals(actual, expected)
    }
  }

  test("ICMP compares its third source with zero and selects the first or second source") {
    execute(
      Vec(
        ("LDC.64", "R4,c[0x0][0x160]"),
        ("MOV", "R0,11"),
        ("MOV", "R1,22"),
        ("MOV", "R2,-1"),
        ("ICMP.NE", "R3,R0,R1,R2"),
        ("STG.E", "[R4.64],R3"),
        ("MOV", "R2,0"),
        ("ICMP.NE", "R3,R0,R1,R2"),
        ("STG.E", "[R4.64+0x4],R3"),
        ("MOV", "R2,1"),
        ("ICMP.NE", "R3,R0,R1,R2"),
        ("STG.E", "[R4.64+0x8],R3"),
        ("EXIT", "")
      ),
      Vec(11, 22, 11)
    )
  }

  test("compiler-produced sm_35 ICMP sequence matches signed select behavior") {
    val module   = icmpModule
    val function = ListingDecoder.parse(
      LegacyIcmpFixtures.legacyIcmpSm35,
      "probe",
      35
    ).fold(fail(_), _.head)
    val sass   = SASSFixture(module, module.kernels.head, Vec(function))
    val scalar = (value: Int) => LittleEndian.toVec(value.toLong, 4)
    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(12L)
      val program = service.compile(sass)
        .fold(e => fail(e.message), identity)
      Vec((-1, Vec(11, 11, 22)), (0, Vec(22, 22, 22)), (1, Vec(11, 22, 11))).foreach {
        case (c, expected) =>
          val task = SASSFixture.task(
            "probe": NonEmpty[String],
            LaunchGeometry.Dimensions.One,
            LaunchGeometry.Dimensions.One,
            Vec(
              KernelTask.Argument.Memory(output, 0L),
              KernelTask.Argument.Scalar(scalar(11)),
              KernelTask.Argument.Scalar(scalar(22)),
              KernelTask.Argument.Scalar(scalar(c))
            )
          )
          service.run(program, task).fold(e => fail(e.message), identity)
          val bytes  = service.read(output, 0L, 12).toOption.get
          val actual = (0 until 3).map { i =>
            val at = i * 4
            (bytes(at) & 255) | ((bytes(at + 1) & 255) << 8) | ((bytes(at + 2) & 255) << 16) | (bytes(at + 3) << 24)
          }.toVec
          assertEquals(actual, expected)
      }
    }
  }

  test("ISET combines its compare with the incoming predicate and supports unsigned comparison") {
    execute(
      Vec(
        ("LDC.64", "R4,c[0x0][0x160]"),
        ("MOV", "R0,-1"),
        ("MOV", "R1,1"),
        ("ISETP.EQ.AND", "P0,PT,RZ,RZ,PT"),
        ("ISET.GT.U32.AND", "R2,R0,R1,P0"),
        ("STG.E", "[R4.64],R2"),
        ("ISETP.EQ.AND", "P0,PT,RZ,1,PT"),
        ("ISET.GT.U32.AND", "R2,R0,R1,P0"),
        ("STG.E", "[R4.64+0x4],R2"),
        ("ISETP.EQ.AND", "P0,PT,RZ,RZ,PT"),
        ("ISET.GT.U32.AND", "R2,R0,R1,P0"),
        ("STG.E", "[R4.64+0x8],R2"),
        ("EXIT", "")
      ),
      Vec(-1, 0, -1)
    )
  }

  test("ISET.CC supplies the zero flag consumed by BRA CC.EQU and CC.NEU") {
    execute(
      Vec(
        ("LDC.64", "R4,c[0x0][0x160]"),
        ("MOV", "R0,5"),
        ("MOV", "R1,5"),
        ("ISET.EQ.AND", "RZ.CC,R0,R1,PT"),
        ("BRA", "CC.NEU,0x70"),
        ("MOV", "R2,0"),
        ("BRA", "0x80"),
        ("MOV", "R2,1"),
        ("STG.E", "[R4.64],R2"),
        ("MOV", "R0,5"),
        ("MOV", "R1,6"),
        ("ISET.EQ.AND", "RZ.CC,R0,R1,PT"),
        ("BRA", "CC.EQU,0xf0"),
        ("MOV", "R2,0"),
        ("BRA", "0x100"),
        ("MOV", "R2,1"),
        ("STG.E", "[R4.64+0x4],R2"),
        ("EXIT", "")
      ),
      Vec(1, 1)
    )
  }
}
