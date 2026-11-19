package net.kurobako.gemstone.isa.amdgpu

import assembly.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.testing.*

class NoReturnAtomicSuite extends Suite {
  private val target = Target("gfx1036")

  private def register(first: Int, bits: Int): String =
    if bits == 32 then s"v$first" else s"v[$first:${first + bits / 32 - 1}]"

  private def noReturnText(opcode: Opcode, encoding: Metadata.Encoding, saddr: Option[String]): String = {
    val operands = encoding.operands.filterNot(info => info.implicitOperand || info.output).map { info =>
      info.field match {
        // with an SGPR base the offset is a single 32-bit VGPR
        case Some(EncodingField.ADDR)  => register(2, if saddr.isDefined then 32 else info.bits)
        case Some(EncodingField.DATA)  => register(4, info.bits)
        case Some(EncodingField.SADDR) => saddr.getOrElse("off")
        case other                     => fail(s"${opcode.mnemonic}: unexpected operand $other")
      }
    }
    s"${opcode.mnemonic} ${operands.mkString(", ")}"
  }

  // llvm-mc 23.1.5 -triple=amdgcn -mcpu=gfx1036 -show-encoding, one line per no-return form
  private val llvm = Vec(
    "flat_atomic_add v[2:3], v4"                    -> "00 00 c8 dc 02 04 7d 00",
    "flat_atomic_add_x2 v[2:3], v[4:5]"             -> "00 00 48 dd 02 04 7d 00",
    "flat_atomic_and v[2:3], v4"                    -> "00 00 e4 dc 02 04 7d 00",
    "flat_atomic_and_x2 v[2:3], v[4:5]"             -> "00 00 64 dd 02 04 7d 00",
    "flat_atomic_cmpswap v[2:3], v[4:5]"            -> "00 00 c4 dc 02 04 7d 00",
    "flat_atomic_cmpswap_x2 v[2:3], v[4:7]"         -> "00 00 44 dd 02 04 7d 00",
    "flat_atomic_dec v[2:3], v4"                    -> "00 00 f4 dc 02 04 7d 00",
    "flat_atomic_dec_x2 v[2:3], v[4:5]"             -> "00 00 74 dd 02 04 7d 00",
    "flat_atomic_fcmpswap v[2:3], v[4:5]"           -> "00 00 f8 dc 02 04 7d 00",
    "flat_atomic_fcmpswap_x2 v[2:3], v[4:7]"        -> "00 00 78 dd 02 04 7d 00",
    "flat_atomic_fmax v[2:3], v4"                   -> "00 00 00 dd 02 04 7d 00",
    "flat_atomic_fmax_x2 v[2:3], v[4:5]"            -> "00 00 80 dd 02 04 7d 00",
    "flat_atomic_fmin v[2:3], v4"                   -> "00 00 fc dc 02 04 7d 00",
    "flat_atomic_fmin_x2 v[2:3], v[4:5]"            -> "00 00 7c dd 02 04 7d 00",
    "flat_atomic_inc v[2:3], v4"                    -> "00 00 f0 dc 02 04 7d 00",
    "flat_atomic_inc_x2 v[2:3], v[4:5]"             -> "00 00 70 dd 02 04 7d 00",
    "flat_atomic_or v[2:3], v4"                     -> "00 00 e8 dc 02 04 7d 00",
    "flat_atomic_or_x2 v[2:3], v[4:5]"              -> "00 00 68 dd 02 04 7d 00",
    "flat_atomic_smax v[2:3], v4"                   -> "00 00 dc dc 02 04 7d 00",
    "flat_atomic_smax_x2 v[2:3], v[4:5]"            -> "00 00 5c dd 02 04 7d 00",
    "flat_atomic_smin v[2:3], v4"                   -> "00 00 d4 dc 02 04 7d 00",
    "flat_atomic_smin_x2 v[2:3], v[4:5]"            -> "00 00 54 dd 02 04 7d 00",
    "flat_atomic_sub v[2:3], v4"                    -> "00 00 cc dc 02 04 7d 00",
    "flat_atomic_sub_x2 v[2:3], v[4:5]"             -> "00 00 4c dd 02 04 7d 00",
    "flat_atomic_swap v[2:3], v4"                   -> "00 00 c0 dc 02 04 7d 00",
    "flat_atomic_swap_x2 v[2:3], v[4:5]"            -> "00 00 40 dd 02 04 7d 00",
    "flat_atomic_umax v[2:3], v4"                   -> "00 00 e0 dc 02 04 7d 00",
    "flat_atomic_umax_x2 v[2:3], v[4:5]"            -> "00 00 60 dd 02 04 7d 00",
    "flat_atomic_umin v[2:3], v4"                   -> "00 00 d8 dc 02 04 7d 00",
    "flat_atomic_umin_x2 v[2:3], v[4:5]"            -> "00 00 58 dd 02 04 7d 00",
    "flat_atomic_xor v[2:3], v4"                    -> "00 00 ec dc 02 04 7d 00",
    "flat_atomic_xor_x2 v[2:3], v[4:5]"             -> "00 00 6c dd 02 04 7d 00",
    "global_atomic_add v[2:3], v4, off"             -> "00 80 c8 dc 02 04 7d 00",
    "global_atomic_add_x2 v[2:3], v[4:5], off"      -> "00 80 48 dd 02 04 7d 00",
    "global_atomic_and v[2:3], v4, off"             -> "00 80 e4 dc 02 04 7d 00",
    "global_atomic_and_x2 v[2:3], v[4:5], off"      -> "00 80 64 dd 02 04 7d 00",
    "global_atomic_cmpswap v[2:3], v[4:5], off"     -> "00 80 c4 dc 02 04 7d 00",
    "global_atomic_cmpswap_x2 v[2:3], v[4:7], off"  -> "00 80 44 dd 02 04 7d 00",
    "global_atomic_csub v[2:3], v4, off"            -> "00 80 d0 dc 02 04 7d 00",
    "global_atomic_dec v[2:3], v4, off"             -> "00 80 f4 dc 02 04 7d 00",
    "global_atomic_dec_x2 v[2:3], v[4:5], off"      -> "00 80 74 dd 02 04 7d 00",
    "global_atomic_fcmpswap v[2:3], v[4:5], off"    -> "00 80 f8 dc 02 04 7d 00",
    "global_atomic_fcmpswap_x2 v[2:3], v[4:7], off" -> "00 80 78 dd 02 04 7d 00",
    "global_atomic_fmax v[2:3], v4, off"            -> "00 80 00 dd 02 04 7d 00",
    "global_atomic_fmax_x2 v[2:3], v[4:5], off"     -> "00 80 80 dd 02 04 7d 00",
    "global_atomic_fmin v[2:3], v4, off"            -> "00 80 fc dc 02 04 7d 00",
    "global_atomic_fmin_x2 v[2:3], v[4:5], off"     -> "00 80 7c dd 02 04 7d 00",
    "global_atomic_inc v[2:3], v4, off"             -> "00 80 f0 dc 02 04 7d 00",
    "global_atomic_inc_x2 v[2:3], v[4:5], off"      -> "00 80 70 dd 02 04 7d 00",
    "global_atomic_or v[2:3], v4, off"              -> "00 80 e8 dc 02 04 7d 00",
    "global_atomic_or_x2 v[2:3], v[4:5], off"       -> "00 80 68 dd 02 04 7d 00",
    "global_atomic_smax v[2:3], v4, off"            -> "00 80 dc dc 02 04 7d 00",
    "global_atomic_smax_x2 v[2:3], v[4:5], off"     -> "00 80 5c dd 02 04 7d 00",
    "global_atomic_smin v[2:3], v4, off"            -> "00 80 d4 dc 02 04 7d 00",
    "global_atomic_smin_x2 v[2:3], v[4:5], off"     -> "00 80 54 dd 02 04 7d 00",
    "global_atomic_sub v[2:3], v4, off"             -> "00 80 cc dc 02 04 7d 00",
    "global_atomic_sub_x2 v[2:3], v[4:5], off"      -> "00 80 4c dd 02 04 7d 00",
    "global_atomic_swap v[2:3], v4, off"            -> "00 80 c0 dc 02 04 7d 00",
    "global_atomic_swap_x2 v[2:3], v[4:5], off"     -> "00 80 40 dd 02 04 7d 00",
    "global_atomic_umax v[2:3], v4, off"            -> "00 80 e0 dc 02 04 7d 00",
    "global_atomic_umax_x2 v[2:3], v[4:5], off"     -> "00 80 60 dd 02 04 7d 00",
    "global_atomic_umin v[2:3], v4, off"            -> "00 80 d8 dc 02 04 7d 00",
    "global_atomic_umin_x2 v[2:3], v[4:5], off"     -> "00 80 58 dd 02 04 7d 00",
    "global_atomic_xor v[2:3], v4, off"             -> "00 80 ec dc 02 04 7d 00",
    "global_atomic_xor_x2 v[2:3], v[4:5], off"      -> "00 80 6c dd 02 04 7d 00",
    "global_atomic_add v2, v4, s[2:3]"              -> "00 80 c8 dc 02 04 02 00",
    "global_atomic_add_x2 v2, v[4:5], s[2:3]"       -> "00 80 48 dd 02 04 02 00",
    "global_atomic_and v2, v4, s[2:3]"              -> "00 80 e4 dc 02 04 02 00",
    "global_atomic_and_x2 v2, v[4:5], s[2:3]"       -> "00 80 64 dd 02 04 02 00",
    "global_atomic_cmpswap v2, v[4:5], s[2:3]"      -> "00 80 c4 dc 02 04 02 00",
    "global_atomic_cmpswap_x2 v2, v[4:7], s[2:3]"   -> "00 80 44 dd 02 04 02 00",
    "global_atomic_csub v2, v4, s[2:3]"             -> "00 80 d0 dc 02 04 02 00",
    "global_atomic_dec v2, v4, s[2:3]"              -> "00 80 f4 dc 02 04 02 00",
    "global_atomic_dec_x2 v2, v[4:5], s[2:3]"       -> "00 80 74 dd 02 04 02 00",
    "global_atomic_fcmpswap v2, v[4:5], s[2:3]"     -> "00 80 f8 dc 02 04 02 00",
    "global_atomic_fcmpswap_x2 v2, v[4:7], s[2:3]"  -> "00 80 78 dd 02 04 02 00",
    "global_atomic_fmax v2, v4, s[2:3]"             -> "00 80 00 dd 02 04 02 00",
    "global_atomic_fmax_x2 v2, v[4:5], s[2:3]"      -> "00 80 80 dd 02 04 02 00",
    "global_atomic_fmin v2, v4, s[2:3]"             -> "00 80 fc dc 02 04 02 00",
    "global_atomic_fmin_x2 v2, v[4:5], s[2:3]"      -> "00 80 7c dd 02 04 02 00",
    "global_atomic_inc v2, v4, s[2:3]"              -> "00 80 f0 dc 02 04 02 00",
    "global_atomic_inc_x2 v2, v[4:5], s[2:3]"       -> "00 80 70 dd 02 04 02 00",
    "global_atomic_or v2, v4, s[2:3]"               -> "00 80 e8 dc 02 04 02 00",
    "global_atomic_or_x2 v2, v[4:5], s[2:3]"        -> "00 80 68 dd 02 04 02 00",
    "global_atomic_smax v2, v4, s[2:3]"             -> "00 80 dc dc 02 04 02 00",
    "global_atomic_smax_x2 v2, v[4:5], s[2:3]"      -> "00 80 5c dd 02 04 02 00",
    "global_atomic_smin v2, v4, s[2:3]"             -> "00 80 d4 dc 02 04 02 00",
    "global_atomic_smin_x2 v2, v[4:5], s[2:3]"      -> "00 80 54 dd 02 04 02 00",
    "global_atomic_sub v2, v4, s[2:3]"              -> "00 80 cc dc 02 04 02 00",
    "global_atomic_sub_x2 v2, v[4:5], s[2:3]"       -> "00 80 4c dd 02 04 02 00",
    "global_atomic_swap v2, v4, s[2:3]"             -> "00 80 c0 dc 02 04 02 00",
    "global_atomic_swap_x2 v2, v[4:5], s[2:3]"      -> "00 80 40 dd 02 04 02 00",
    "global_atomic_umax v2, v4, s[2:3]"             -> "00 80 e0 dc 02 04 02 00",
    "global_atomic_umax_x2 v2, v[4:5], s[2:3]"      -> "00 80 60 dd 02 04 02 00",
    "global_atomic_umin v2, v4, s[2:3]"             -> "00 80 d8 dc 02 04 02 00",
    "global_atomic_umin_x2 v2, v[4:5], s[2:3]"      -> "00 80 58 dd 02 04 02 00",
    "global_atomic_xor v2, v4, s[2:3]"              -> "00 80 ec dc 02 04 02 00",
    "global_atomic_xor_x2 v2, v[4:5], s[2:3]"       -> "00 80 6c dd 02 04 02 00"
  )

  private def dump(bytes: Vec[Byte]): String = bytes.map(byte => f"${byte & 0xff}%02x").mkString(" ")

  private def failures(text: String, expected: Vec[Byte]): Vec[String] =
    Binary.encodeStatement(parseStatement(text), TargetMode(target)) match {
      case Left(error)                         => Vec(s"$text: encode failed: $error")
      case Right(actual) if actual != expected => Vec(s"$text: encoded ${dump(actual)}, llvm-mc ${dump(expected)}")
      case Right(_)                            =>
        Binary.decodeStatementAt(expected, 0, TargetMode(target)) match {
          case Left(error)    => Vec(s"$text: decode failed: $error")
          case Right(decoded) =>
            val printed = Printer.render(Program(Vec(decoded.statement)))
            if printed == text then Vec.empty else Vec(s"$text: printed as $printed")
        }
    }

  test("gfx1036 no-return flat and global atomics encode, decode and print as LLVM does") {
    val derived = InstructionDefs.definitions.filter(_.mnemonic.raw.contains("atomic")).flatMap { opcode =>
      opcode.encodings(target).filter(_.format.isFlat).flatMap { encoding =>
        val saddrs: Vec[Option[String]] =
          if encoding.operands.exists(_.field.contains(EncodingField.SADDR)) then Vec(None, Some("s[2:3]"))
          else Vec(None)
        saddrs.map(noReturnText(opcode, encoding, _))
      }
    }
    assertEquals(derived.toSet, llvm.map(_._1).toSet)
    assertEquals(derived.size, llvm.size)
    val failing = llvm.flatMap((text, bytes) => failures(text, hex(bytes)))
    assert(failing.isEmpty, failing.mkString("\n"))
  }
}
