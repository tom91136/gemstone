package net.kurobako.gemstone.codegen

class RuntimeCollectionsSuite extends munit.FunSuite {
  test("rewrites collection identifiers but preserves specification text") {
    val source =
      """|package example
         |
         |final case class Value(items: Vector[String], bytes: IArray[Byte], names: List[String], ordered: Seq[String])
         |val copied = items.toSeq
         |val description = "Vector and IArray are literal specification words"
         |// Vector in a comment
         |""".stripMargin

    val rewritten = RuntimeCollections(source)
    assert(rewritten.contains("import net.kurobako.gemstone.Col"))
    assert(rewritten.contains("import net.kurobako.gemstone.Col.*"))
    assert(
      rewritten.contains(
        "Value(items: Col[String], bytes: Col[Byte], names: Col[String], ordered: Col[String])"
      )
    )
    assert(rewritten.contains("items.toCol"))
    assert(rewritten.contains("\"Vector and IArray are literal specification words\""))
    assert(rewritten.contains("// Vector in a comment"))
  }
}
