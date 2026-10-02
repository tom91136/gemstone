package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*

/** Machine words printed by nvdisasm in the checked-in text oracle fixtures. */
object SASSOracleWords {
  def read(text: String): Vec[(Int, Array[Byte])] = {
    val lines = text.linesIterator.toVec
    lines.indices.flatMap { index =>
      for {
        (offset, instruction) <- instructionWord(lines(index))
        scheduling            <- Option.when(index + 1 < lines.size)(controlWord(lines(index + 1))).flatten
      } yield offset -> (bytes(instruction) ++ bytes(scheduling))
    }.toVec
  }

  private def instructionWord(line: String): Option[(Int, String)] = {
    val first = line.indexOf("/*")
    if (first < 0) None
    else {
      val firstEnd = line.indexOf("*/", first + 2)
      if (firstEnd < 0) None
      else {
        val offset = line.substring(first + 2, firstEnd).trim
        val second = line.indexOf("/*", firstEnd + 2)
        if (second < 0) None
        else {
          val secondEnd = line.indexOf("*/", second + 2)
          if (secondEnd < 0) None
          else {
            val word = line.substring(second + 2, secondEnd).trim
            for {
              address <- parseHex(offset)
              _       <- Option.when(word.startsWith("0x"))(())
            } yield address -> word
          }
        }
      }
    }
  }

  private def controlWord(line: String): Option[String] = {
    val trimmed = line.trim
    if (!trimmed.startsWith("/*") || !trimmed.endsWith("*/")) None
    else {
      val word = trimmed.substring(2, trimmed.length - 2).trim
      Option.when(word.startsWith("0x"))(word)
    }
  }

  private def parseHex(value: String): Option[Int] =
    if (value.isEmpty || !value.forall(character => Character.digit(character, 16) >= 0)) None
    else scala.util.Try(Integer.parseInt(value, 16)).toOption

  private def bytes(hex: String): Array[Byte] = {
    val word = BigInt(hex.drop(2), 16)
    Array.tabulate[Byte](8)(index => (word >> (index * 8)).toByte)
  }
}
