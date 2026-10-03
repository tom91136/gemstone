package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class LegacyConditionCodeSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module = SASSFixture.module(
    35,
    Vec(SASSFixture.Kernel("arith_probe", 320, Vec((0, 8), (8, 8), (16, 8), (24, 4)), registers = 24))
  )

  private def instructions(lhs: String, rhs: String): Vec[DecodedInstruction] = {
    val body = Vec(
      ("LDC.64", "R4,c[0x0][0x140]"),
      ("MOV", "R0,c[0x0][0x148]"),
      ("MOV", "R1,c[0x0][0x150]"),
      ("IADD", s"RZ.CC,$lhs,$rhs"),
      ("P2R", "R6,RZ,0xf000"),
      // Deliberately destroy every CC bit before restoring the packed state.
      ("IADD", "RZ.CC,R0,R0"),
      ("R2P", "PR,R6,0xf000"),
      ("IADD.X", "R7,RZ,RZ"),
      ("ST.E", "[R4],R6"),
      ("ST.E", "[R4+0x4],R7"),
      ("EXIT", "")
    )
    body.zipWithIndex.map { case ((opcode, operands), index) =>
      SASSFixture.instruction(index * 16, opcode, operands.split(',').filter(_.nonEmpty).toVec, None)
    }
  }

  private def expectedFlags(a: Long, b: Long, lhsText: String, rhsText: String): Int = {
    val subtract   = lhsText.startsWith("-") || rhsText.startsWith("-")
    val (lhs, rhs) =
      if (lhsText.startsWith("-")) (b, a)
      else if (rhsText.startsWith("-")) (a, b)
      else (a, b)
    val aa           = lhs.toInt
    val bb           = rhs.toInt
    val result       = if (subtract) aa - bb else aa + bb
    val unsignedA    = lhs & 0xffffffffL
    val unsignedB    = rhs & 0xffffffffL
    val carry        = if (subtract) unsignedA >= unsignedB else unsignedA + unsignedB > 0xffffffffL
    val z            = if (result == 0) 1 else 0
    val s            = if (result < 0) 1 else 0
    val c            = if (carry) 1 else 0
    val overflowBits = if (subtract) (aa ^ bb) & (aa ^ result) else (aa ^ result) & (bb ^ result)
    val o            = if ((overflowBits & 0x80000000) != 0) 1 else 0
    (z << 12) | (s << 13) | (c << 14) | (o << 15)
  }

  test("sm_35 IADD CC bits survive P2R/R2P and restore carry for a following IADD.X") {
    val module   = fixtureModule
    val kernel   = module.kernels.find(_.name == "arith_probe").get
    val variants = Vec(("R0", "R1"), ("-R0", "R1"), ("R0", "-R1"))
    val cases    = Vec(
      0x00000000L -> 0x00000000L,
      0x00000001L -> 0x00000002L,
      0xffffffffL -> 0x00000001L,
      0x7fffffffL -> 0x00000001L,
      0x80000000L -> 0x80000000L,
      0xffffffffL -> 0x00000000L,
      0x80000000L -> 0x7fffffffL
    )
    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(8L)
      variants.foreach { case (lhs, rhs) =>
        val sass = SASSFixture(
          module,
          kernel,
          Vec(DecodedFunction("arith_probe", instructions(lhs, rhs)))
        )
        val program = service.compile(sass)
          .fold(e => fail(e.message), identity)
        cases.foreach { case (a, b) =>
          val task = SASSFixture.task(
            "arith_probe": NonEmpty[String],
            LaunchGeometry.Dimensions.One,
            LaunchGeometry.Dimensions.One,
            Vec(
              KernelTask.Argument.Memory(output, 0L),
              KernelTask.Argument.Scalar(LittleEndian.toVec(a, 8)),
              KernelTask.Argument.Scalar(LittleEndian.toVec(b, 8)),
              KernelTask.Argument.Scalar(LittleEndian.toVec(0L, 4))
            )
          )
          service.run(program, task).fold(e => fail(e.message), identity)
          val bytes  = service.read(output, 0L, 8).toOption.get
          val actual = (0 until 4).foldLeft(0)((value, byte) => value | ((bytes(byte).toInt & 255) << (byte * 8)))
          assertEquals(actual, expectedFlags(a, b, lhs, rhs), s"$lhs,$rhs a=0x${a.toHexString}, b=0x${b.toHexString}")
          val restoredCarry = (0 until 4).foldLeft(0)((value, byte) =>
            value | ((bytes(4 + byte).toInt & 255) << (byte * 8))
          )
          val (carryA, carryB) = if (lhs.startsWith("-")) (b, a) else if (rhs.startsWith("-")) (a, b) else (a, b)
          val expectedCarry    = if (lhs.startsWith("-") || rhs.startsWith("-"))
            if ((carryA & 0xffffffffL) >= (carryB & 0xffffffffL)) 1 else 0
          else if (((a & 0xffffffffL) + (b & 0xffffffffL)) >>> 32 != 0) 1 else 0
          assertEquals(restoredCarry, expectedCarry)
        }
      }
    }
  }

  test("sm_35 ISCADD.CC aliases its shifted source and reports add flags") {
    val module = fixtureModule
    val kernel = module.kernels.find(_.name == "arith_probe").get
    val inputs = Vec(
      0x20000000L -> 0x80000000L,
      0x1fffffffL -> 0x00000003L,
      0x3fffffffL -> 0x00000008L,
      0xffffffffL -> 0x00000003L
    )
    val body = Vec(
      ("LDC.64", "R4,c[0x0][0x140]"),
      ("MOV", "R0,c[0x0][0x148]"),
      ("MOV", "R1,c[0x0][0x150]"),
      ("ISCADD", "R0.CC,R0,R1,0x2"),
      ("P2R", "R6,RZ,0xf000"),
      ("R2P", "PR,R6,0xf000"),
      ("IADD.X", "R7,RZ,RZ"),
      ("ST.E", "[R4],R6"),
      ("ST.E", "[R4+0x4],R7"),
      ("EXIT", "")
    ).zipWithIndex.map { case ((op, args), index) =>
      SASSFixture.instruction(index * 16, op, args.split(',').filter(_.nonEmpty).toVec, None)
    }
    val sass = SASSFixture(module, kernel, Vec(DecodedFunction("arith_probe", body)))
    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(8L)
      val program = service.compile(sass)
        .fold(e => fail(e.message), identity)
      inputs.foreach { case (a, b) =>
        val task = SASSFixture.task(
          "arith_probe": NonEmpty[String],
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Scalar(LittleEndian.toVec(a, 8)),
            KernelTask.Argument.Scalar(LittleEndian.toVec(b, 8)),
            KernelTask.Argument.Scalar(LittleEndian.toVec(0L, 4))
          )
        )
        service.run(program, task).fold(e => fail(e.message), identity)
        val bytes   = service.read(output, 0L, 8).toOption.get
        val packed  = (0 until 4).foldLeft(0)((value, byte) => value | ((bytes(byte).toInt & 255) << (byte * 8)))
        val carry   = (0 until 4).foldLeft(0)((value, byte) => value | ((bytes(4 + byte).toInt & 255) << (byte * 8)))
        val shifted = ((a & 0xffffffffL) << 2) & 0xffffffffL
        val result  = (shifted + (b & 0xffffffffL)) & 0xffffffffL
        val signedA = shifted.toInt
        val signedB = b.toInt
        val signedResult     = result.toInt
        val expectedCarry    = if (shifted + (b & 0xffffffffL) > 0xffffffffL) 1 else 0
        val expectedOverflow = if (((~(signedA ^ signedB) & (signedA ^ signedResult)) & 0x80000000) != 0) 1 else 0
        val expected         = (if (result == 0) 0x1000 else 0) | (if (signedResult < 0) 0x2000 else 0) |
          (expectedCarry << 14) | (expectedOverflow << 15)
        assertEquals(packed, expected, s"ISCADD a=0x${a.toHexString}, b=0x${b.toHexString}")
        assertEquals(carry, expectedCarry)
      }
    }
  }

  test("sm_35 plain IMAD.CC aliases its source and preserves valid flags under predication") {
    val module = fixtureModule
    val kernel = module.kernels.find(_.name == "arith_probe").get
    val cases  = Vec(
      0x1fffffffL -> 0x00000008L,
      0x3fffffffL -> 0xffffffffL,
      0x40000000L -> 0x00000000L,
      0xffffffffL -> 0x00000001L
    )
    val variants: Vec[(Vec[(String, String, Option[String])], (String, String, Option[String]))] = Vec(
      Vec.empty[(String, String, Option[String])] -> ("IMAD", "R0.CC,R0,0x4,R1", None),
      Vec(
        ("IADD", "RZ.CC,R0,R1", None),
        ("ISETP.NE.AND", "P0,PT,RZ,RZ,PT", None)
      )                                             -> ("IMAD", "R0.CC,R0,0x4,R1", Some("@P0")),
      Vec(("ISETP.EQ.AND", "P0,PT,RZ,RZ,PT", None)) -> ("IMAD", "R0.CC,R0,0x4,R1", Some("@P0"))
    )
    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(8L)
      variants.foreach { case (setup, (opcode, operands, predicate)) =>
        val body = Vec(
          ("LDC.64", "R4,c[0x0][0x140]", None),
          ("MOV", "R0,c[0x0][0x148]", None),
          ("MOV", "R1,c[0x0][0x150]", None)
        ) ++ setup ++ Vec(
          (opcode, operands, predicate),
          ("P2R", "R6,RZ,0xf000", None),
          ("R2P", "PR,R6,0xf000", None),
          ("IADD.X", "R7,RZ,RZ", None),
          ("ST.E", "[R4],R6", None),
          ("ST.E", "[R4+0x4],R7", None),
          ("EXIT", "", None)
        )
        val instructions = body.zipWithIndex.map { case ((op, args, pred), index) =>
          SASSFixture.instruction(index * 16, op, args.split(',').filter(_.nonEmpty).toVec, pred)
        }
        val sass    = SASSFixture(module, kernel, Vec(DecodedFunction("arith_probe", instructions)))
        val program = service.compile(sass)
          .fold(e => fail(e.message), identity)
        cases.foreach { case (a, b) =>
          val task = SASSFixture.task(
            "arith_probe": NonEmpty[String],
            LaunchGeometry.Dimensions.One,
            LaunchGeometry.Dimensions.One,
            Vec(
              KernelTask.Argument.Memory(output, 0L),
              KernelTask.Argument.Scalar(LittleEndian.toVec(a, 8)),
              KernelTask.Argument.Scalar(LittleEndian.toVec(b, 8)),
              KernelTask.Argument.Scalar(LittleEndian.toVec(0L, 4))
            )
          )
          service.run(program, task).fold(e => fail(e.message), identity)
          val bytes        = service.read(output, 0L, 8).toOption.get
          val packed       = (0 until 4).foldLeft(0)((v, i) => v | ((bytes(i).toInt & 255) << (i * 8)))
          val carry        = (0 until 4).foldLeft(0)((v, i) => v | ((bytes(i + 4).toInt & 255) << (i * 8)))
          val imadExecutes = !setup.exists(_._1 == "IADD")
          val expected     = if (imadExecutes) {
            val productBits = ((a & 0xffffffffL) * 4L) & 0xffffffffL
            val product     = productBits.toInt
            val sum         = product + b.toInt
            val c           = if (productBits + (b & 0xffffffffL) > 0xffffffffL) 1 else 0
            val o           = if (((product ^ sum) & (b.toInt ^ sum) & 0x80000000) != 0) 1 else 0
            (if (sum == 0) 0x1000 else 0) | (if (sum < 0) 0x2000 else 0) | (c << 14) | (o << 15)
          } else expectedFlags(a, b, "R0", "R1")
          val expectedCarry = if (imadExecutes)
            if ((((a & 0xffffffffL) * 4L) & 0xffffffffL) + (b & 0xffffffffL) > 0xffffffffL) 1 else 0
          else if (((a & 0xffffffffL) + (b & 0xffffffffL)) > 0xffffffffL) 1 else 0
          assertEquals(packed, expected, s"IMAD a=0x${a.toHexString}, b=0x${b.toHexString}")
          assertEquals(carry, expectedCarry)
        }
      }
    }
  }
}
