package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*

/** Hexadecimal byte text: pairs in memory order, or `0x`-prefixed little-endian words such as cuobjdump prints. */
object Hex {
  def parse(text: String): Either[String, Vec[Byte]] = {
    val bytes   = Vec.newBuilder[Byte]
    val invalid = text.split("[\\s,;]+|/\\*|\\*/").view.filter(_.nonEmpty).map(token => decode(token).map(bytes ++= _))
      .collectFirst { case Left(error) => error }
    invalid.toLeft(bytes.result())
  }

  private def decode(token: String): Either[String, Vec[Byte]] = {
    val word   = token.startsWith("0x") || token.startsWith("0X")
    val digits = if (word) token.drop(2) else token
    if (digits.isEmpty || !digits.forall(c => Character.digit(c, 16) >= 0)) Left(s"invalid hexadecimal token $token")
    else {
      val padded = if (digits.length % 2 == 0) digits else s"0$digits"
      val parsed = padded.grouped(2).toVec.map(pair => java.lang.Integer.parseInt(pair, 16).toByte)
      Right(if (word) parsed.reverse else parsed)
    }
  }

  def render(bytes: Vec[Byte]): String =
    bytes.grouped(16).map(_.map(value => f"${value & 0xff}%02x").mkString(" ")).mkString("\n")
}
