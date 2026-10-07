package net.kurobako.gemstone.codegen

import scala.concurrent.duration.*

class SASSPreprocessorSuite extends munit.FunSuite {
  override val munitTimeout: Duration = 120.seconds

  test("selects the branches active without architecture semantics and preserves line offsets") {
    val source = "before\r\n##IF DEFINED(ARCH_SEMANTICS)\r\nmodern\r\n##ELSE\r\nlegacy\r\n##ENDIF\r\nafter\r\n"
    val legacy = SASSPreprocessor.preprocess(source).toOption.get
    assertEquals(legacy.length, source.length)
    assertEquals(legacy.count(_ == '\n'), source.count(_ == '\n'))
    assert(legacy.contains("legacy\r\n"))
    assert(!legacy.contains("modern"))
    assert(legacy.endsWith("after\r\n"))
  }

  test("evaluates negated guards and nested inactive branches") {
    val source = """##IF !DEFINED(ARCH_SEMANTICS)
                     |legacy
                     |##IF DEFINED(ARCH_SEMANTICS)
                     |unreachable
                     |##ELSE
                     |nested legacy
                     |##ENDIF
                     |##ELSE
                     |modern
                     |##ENDIF
                     |""".stripMargin
    val legacy = SASSPreprocessor.preprocess(source).toOption.get
    assert(legacy.contains("legacy\n"))
    assert(legacy.contains("nested legacy\n"))
    assert(!legacy.contains("unreachable"))
    assert(!legacy.contains("modern"))
  }

  test("rejects malformed, unsupported, and unbalanced directives") {
    val malformed = Vector(
      "##ELSE\n",
      "##ENDIF\n",
      "##IF DEFINED(UNKNOWN)\nx\n##ENDIF\n",
      "##IF DEFINED(ARCH_SEMANTICS)\n##ELSE\n##ELSE\n##ENDIF\n",
      "##IF DEFINED(ARCH_SEMANTICS)\n",
      "##IF DEFINED(ARCH_SEMANTICS)\n##ENDIF trailing\n"
    )
    malformed.foreach(source =>
      assert(SASSPreprocessor.preprocess(source).isLeft, source)
    )
  }

  test("descriptions without directives remain byte-for-byte unchanged") {
    val source = "ARCHITECTURE \"Kepler\"\r\nCLASS \"NOP\"\nFORMAT Opcode;\n"
    assertEquals(SASSPreprocessor.preprocess(source), Right(source))
  }

  test("large source is processed without consuming the call stack") {
    val source = "CLASS \"instruction\"\n" + ("FORMAT Register:R;\n" * 100000)
    val result = SASSPreprocessor.preprocess(source).toOption.get
    assertEquals(result, source)
  }

  test("all recovered legacy descriptions preprocess and compile") {
    val references = generator.sassReferences
    ExtractSASS.expected("legacy").values.toVector.distinct.sorted.foreach { digest =>
      val raw    = generator.sassTable(digest)
      val active = SASSPreprocessor.preprocess(raw).fold(e => fail(s"$digest: ${e.message} at ${e.offset}"), identity)
      assert(!active.linesIterator.exists(_.trim.startsWith("##")), digest)
      val description = references.tables.find(_.digest == digest).getOrElse(fail(s"missing $digest")).description
      assert(!description.classes.exists(_.conditional), digest)
      val definitions = SASSConditions.definitions(description.preamble).fold(e => fail(s"$digest: $e"), identity)
      assert(definitions.parameters.contains("MAX_REG_COUNT"), digest)
      assertEquals(SASSEncoding.compile(description).forms.size, description.classes.size, digest)
    }
  }
}
