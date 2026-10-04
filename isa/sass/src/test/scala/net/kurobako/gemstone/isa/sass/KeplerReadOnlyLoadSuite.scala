package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*

class KeplerReadOnlyLoadSuite extends SyntheticSASSExecutionSuiteBase {
  for (size <- 0 to 6) test(s"Kepler texture-pipe global load preserves size code $size") {
    val first = size match {
      case 0 => 0xef
      case 1 => -17
      case 2 => 0xcdef
      case 3 => 0xffffcdef
      case _ => 0x89abcdef
    }
    val count    = if (size == 5) 2 else if (size == 6) 4 else 1
    val input    = Vec.nonEmpty(0x89abcdef, 0x12345678, 0xfedcba98, 0x76543210)
    val expected = Vec.nonEmpty(first).concatNonEmpty(input.slice(1, count)).concatNonEmpty(Vec.fill(4 - count)(99))
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("MOV", "R4,0x89abcdef", None),
        ("MOV", "R5,0x12345678", None),
        ("MOV", "R6,0xfedcba98", None),
        ("MOV", "R7,0x76543210", None),
        ("STG.E.128", "[R10],R4", None),
        ("MOV", "R16,99", None),
        ("MOV", "R17,99", None),
        ("MOV", "R18,99", None),
        ("MOV", "R19,99", None),
        ("LDG.E", "R16,[R10]", None),
        ("STG.E.128", "[R10],R16", None),
        ("EXIT", "", None)
      ),
      expected,
      sm = 35,
      rewrite = instruction =>
        instruction.operation match {
          case _: Instruction.Ldg =>
            // nvdisasm's LDG.E.P is a TEX encoding with the size in tid and component mask in wmsk.
            val base = 0x600210867f9c287dL & ~(0x1fffL << 47) & ~(15L << 34) & ~(255L << 2)
            val word = base | (size.toLong << 47) | (((1L << count) - 1) << 34) | (16L << 2)
            InstructionDecoder.decodeTyped(instruction.address, LittleEndian.toVec(word, 8).toArray, "SM35")
              .fold(fail(_), identity)
          case _ => instruction
        }
    )
  }
}
