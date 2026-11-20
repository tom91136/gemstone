package net.kurobako.gemstone.isa.amdgpu

import assembly.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.testing.*

class ImageAtomicWidthSuite extends Suite {
  private val gfx1036 = Target("gfx1036")
  private val gfx908  = Target("gfx908")

  // bytes from llvm-mc 23.1.5 -triple=amdgcn -mcpu=<target> -show-encoding
  private val gfx1036Accepted = Vec(
    "image_atomic_add v1, v[2:3], s[8:15] dmask:1 dim:SQ_RSRC_IMG_2D"             -> "08 01 44 f0 02 01 02 00",
    "image_atomic_add v1, v[2:3], s[8:15] dmask:1 glc dim:SQ_RSRC_IMG_2D"         -> "08 21 44 f0 02 01 02 00",
    "image_atomic_add v[1:2], v[3:4], s[8:15] dmask:3 dim:SQ_RSRC_IMG_2D"         -> "08 03 44 f0 03 01 02 00",
    "image_atomic_add v[1:2], v[3:4], s[8:15] dmask:3 glc dim:SQ_RSRC_IMG_2D"     -> "08 23 44 f0 03 01 02 00",
    "image_atomic_cmpswap v[1:2], v[3:4], s[8:15] dmask:3 dim:SQ_RSRC_IMG_2D"     -> "08 03 40 f0 03 01 02 00",
    "image_atomic_cmpswap v[1:2], v[3:4], s[8:15] dmask:3 glc dim:SQ_RSRC_IMG_2D" -> "08 23 40 f0 03 01 02 00",
    "image_atomic_cmpswap v[1:4], v[5:6], s[8:15] dmask:15 dim:SQ_RSRC_IMG_2D"    -> "08 0f 40 f0 05 01 02 00"
  )

  private val gfx908Accepted = Vec(
    "image_atomic_add v1, v2, s[8:15] dmask:1 unorm"              -> "00 11 48 f0 02 01 02 00",
    "image_atomic_add v1, v2, s[8:15] dmask:1 glc unorm"          -> "00 31 48 f0 02 01 02 00",
    "image_atomic_add v[1:2], v2, s[8:15] dmask:3 unorm"          -> "00 13 48 f0 02 01 02 00",
    "image_atomic_add v[1:2], v2, s[8:15] dmask:3 glc unorm"      -> "00 33 48 f0 02 01 02 00",
    "image_atomic_cmpswap v[1:2], v2, s[8:15] dmask:3 unorm"      -> "00 13 44 f0 02 01 02 00",
    "image_atomic_cmpswap v[1:2], v2, s[8:15] dmask:3 glc unorm"  -> "00 33 44 f0 02 01 02 00",
    "image_atomic_cmpswap v[1:4], v2, s[8:15] dmask:15 unorm"     -> "00 1f 44 f0 02 01 02 00",
    "image_atomic_add v[1:2], v2, s[8:15] dmask:1 tfe unorm"      -> "00 11 49 f0 02 01 02 00",
    "image_atomic_add v[1:3], v2, s[8:15] dmask:3 tfe unorm"      -> "00 13 49 f0 02 01 02 00",
    "image_atomic_cmpswap v[1:2], v2, s[8:15] dmask:1 tfe unorm"  -> "00 11 45 f0 02 01 02 00",
    "image_atomic_cmpswap v[1:5], v2, s[8:15] dmask:15 tfe unorm" -> "00 1f 45 f0 02 01 02 00"
  )

  private val gfx1036Rejected = Vec(
    "image_atomic_add v1, v[3:4], s[8:15] dmask:3 dim:SQ_RSRC_IMG_2D",
    "image_atomic_add v[1:2], v[3:4], s[8:15] dmask:1 dim:SQ_RSRC_IMG_2D",
    "image_atomic_add v[1:4], v[3:4], s[8:15] dmask:15 dim:SQ_RSRC_IMG_2D",
    "image_atomic_cmpswap v1, v[3:4], s[8:15] dmask:1 dim:SQ_RSRC_IMG_2D",
    "image_atomic_cmpswap v1, v[3:4], s[8:15] dmask:1 glc dim:SQ_RSRC_IMG_2D",
    "image_atomic_cmpswap v[1:2], v[3:4], s[8:15] dmask:1 dim:SQ_RSRC_IMG_2D",
    "image_atomic_cmpswap v1, v[3:4], s[8:15] dmask:3 dim:SQ_RSRC_IMG_2D"
  )

  private val gfx908Rejected = Vec(
    "image_atomic_add v1, v2, s[8:15] dmask:3 unorm",
    "image_atomic_add v[1:4], v2, s[8:15] dmask:15 unorm",
    "image_atomic_add v[1:4], v2, s[8:15] dmask:3 tfe unorm",
    "image_atomic_cmpswap v1, v2, s[8:15] dmask:1 unorm",
    "image_atomic_cmpswap v[1:2], v2, s[8:15] dmask:1 unorm",
    "image_atomic_cmpswap v[1:3], v2, s[8:15] dmask:1 tfe unorm",
    "image_atomic_cmpswap v1, v2, s[8:15] dmask:3 unorm"
  )

  private def dump(bytes: Vec[Byte]): String = bytes.map(byte => f"${byte & 0xff}%02x").mkString(" ")

  private def failures(target: Target, text: String, expected: Vec[Byte]): Vec[String] =
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

  private def encodes(target: Target, text: String): Boolean =
    Binary.encodeStatement(parseStatement(text), TargetMode(target)).isRight

  test("image atomic VDATA width follows dmask and cmpswap as llvm-mc does") {
    val failing = Vec(gfx1036 -> gfx1036Accepted, gfx908 -> gfx908Accepted).flatMap { (target, forms) =>
      forms.flatMap((text, bytes) => failures(target, text, hex(bytes)))
    }
    assert(failing.isEmpty, failing.mkString("\n"))
  }

  test("image atomic VDATA sizes llvm-mc rejects do not encode") {
    val failing = Vec(gfx1036 -> gfx1036Rejected, gfx908 -> gfx908Rejected).flatMap { (target, forms) =>
      forms.filter(encodes(target, _)).map(text => s"$text: encoded, llvm-mc rejects")
    }
    assert(failing.isEmpty, failing.mkString("\n"))
  }
}
