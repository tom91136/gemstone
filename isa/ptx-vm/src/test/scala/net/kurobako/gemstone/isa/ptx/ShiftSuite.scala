package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.Suite

class ShiftSuite extends Suite {
  private val shiftOperations = Vec("shl", "shr.s", "shr.u")

  test("integer shifts clamp counts for 16, 32, and 64-bit operands") {
    val counts   = Vec(0L, 1L, 15L, 16L, 17L, 31L, 32L, 33L, 63L, 64L, 65L, 0xffffffffL)
    val allCases = Vec(16, 32, 64).flatMap { width =>
      val negative = if (width == 64) Long.MinValue | 1L else (1L << (width - 1)) | 1L
      val positive = if (width == 64) 0x4000000000000001L else (1L << (width - 2)) | 1L
      val raws     = Vec(negative, positive)
      for {
        count     <- counts
        raw       <- raws
        operation <- shiftOperations
      } yield {
        val signed = Bits.signExtend(raw, width.unsafe[Int in 1 ~ 64])
        (width, raw, signed, count, operation)
      }
    }
    val offsets      = allCases.scanLeft(0) { case (offset, (width, _, _, _, _)) => offset + width / 8 }
    val instructions = allCases.zip(offsets).flatMap {
      case ((width, raw, _, count, operation), offset) =>
        val opcode = operation match {
          case "shl"   => s"shl.b$width"
          case "shr.s" => s"shr.s$width"
          case "shr.u" => s"shr.u$width"
          case other   => fail(s"unexpected shift operation $other")
        }
        Vec(
          s"mov.b$width %x$width, 0x${java.lang.Long.toHexString(raw)};",
          s"mov.u32 %amt, 0x${java.lang.Long.toHexString(count)};",
          s"$opcode %d$width, %x$width, %amt;",
          s"st.global.b$width [%p+$offset], %d$width;"
        )
    }

    val source = s""".version 9.3
      |.target sm_80
      |.address_size 64
      |.visible .entry shifts(.param .u64 output) {
      |  .reg .u64 %p;
      |  .reg .u32 %amt;
      |  .reg .b16 %x16, %d16;
      |  .reg .b32 %x32, %d32;
      |  .reg .b64 %x64, %d64;
      |  ld.param.u64 %p, [output];
      |  ${instructions.mkString("\n  ")}
      |  ret;
      |}
      |""".stripMargin
    val kernel      = compileKernel(source)
    val outputBytes = offsets.last
    val memory      =
      DeviceMemory(outputBytes.unsafe[Int >= 0])
    val start  = memory.start
    val result = execute(
      kernel,
      1,
      1,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(start.raw)),
      memory
    )
    assertEquals(result, Right(()))

    val actual = allCases.zip(offsets).map { case ((width, _, _, _, _), offset) =>
      memory.readLittleEndian(start.offset(offset.toLong), (width / 8).unsafe[Int in 1 ~ 8]).toOption.get
    }
    val expected = allCases.map { case (width, raw, signed, count, operation) =>
      val mask   = if (width == 64) -1L else (1L << width) - 1L
      val result = operation match {
        case "shl" if count >= width   => 0L
        case "shl"                     => (raw << count.toInt) & mask
        case "shr.u" if count >= width => 0L
        case "shr.u"                   => (raw & mask) >>> count.toInt
        case "shr.s" if count >= width => if (signed < 0) mask else 0L
        case "shr.s"                   => (signed >> count.toInt) & mask
        case other                     => fail(s"unexpected shift operation $other")
      }
      result & mask
    }
    assertEquals(actual, expected)
  }
}
