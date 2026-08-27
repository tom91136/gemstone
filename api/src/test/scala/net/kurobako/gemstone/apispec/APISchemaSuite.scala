package net.kurobako.gemstone.apispec

class APISchemaSuite extends munit.FunSuite {
  test("opcodes and exported names are unique") {
    val functions = GemstoneAPIRegistry.apis.flatMap(_.functions)
    assertEquals(functions.map(_.opcode).distinct.size, functions.size)
    assertEquals(functions.map(_.name).distinct.size, functions.size)
  }

  test("output pointers declare an explicit extent") {
    val outputPointers = GemstoneAPIRegistry.apis
      .flatMap(_.functions)
      .flatMap(_.parameters)
      .filter(parameter => parameter.direction != Direction.In)
    assert(outputPointers.forall(_.elements > 0))
  }

}
