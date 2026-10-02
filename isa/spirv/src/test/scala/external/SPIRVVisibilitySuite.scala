package external

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.spirv.v1_6.*

class SPIRVVisibilitySuite extends munit.FunSuite {
  test("operand construction remains public") {
    val word = Word(1)
    assertEquals(Operand(OperandKind.IdRef, Vec.nonEmpty(word)).singleWord, Right(word))
  }
}
