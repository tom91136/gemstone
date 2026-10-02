package net.kurobako.gemstone.codegen

import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.GZIPInputStream
import scala.util.Using

class SASSPreprocessorSuite extends munit.FunSuite {
  test("selects either architecture semantics branch and preserves line offsets") {
    val source = "before\r\n##IF DEFINED(ARCH_SEMANTICS)\r\nmodern\r\n##ELSE\r\nlegacy\r\n##ENDIF\r\nafter\r\n"
    val modern = SASSPreprocessor.preprocess(source, architectureSemantics = true).toOption.get
    val legacy = SASSPreprocessor.preprocess(source, architectureSemantics = false).toOption.get

    assertEquals(modern.length, source.length)
    assertEquals(legacy.length, source.length)
    assertEquals(modern.count(_ == '\n'), source.count(_ == '\n'))
    assertEquals(legacy.count(_ == '\n'), source.count(_ == '\n'))
    assert(modern.contains("modern\r\n"))
    assert(!modern.contains("legacy"))
    assert(legacy.contains("legacy\r\n"))
    assert(!legacy.contains("modern"))
    assert(modern.endsWith("after\r\n"))
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
    val legacy = SASSPreprocessor.preprocess(source, architectureSemantics = false).toOption.get
    val modern = SASSPreprocessor.preprocess(source, architectureSemantics = true).toOption.get
    assert(legacy.contains("legacy\n"))
    assert(legacy.contains("nested legacy\n"))
    assert(!legacy.contains("unreachable"))
    assert(modern.contains("modern\n"))
    assert(!modern.contains("legacy"))
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
      assert(SASSPreprocessor.preprocess(source, architectureSemantics = false).isLeft, source)
    )
  }

  test("descriptions without directives remain byte-for-byte unchanged") {
    val source = "ARCHITECTURE \"Kepler\"\r\nCLASS \"NOP\"\nFORMAT Opcode;\n"
    assertEquals(SASSPreprocessor.preprocess(source, architectureSemantics = false), Right(source))
    assertEquals(SASSPreprocessor.preprocess(source, architectureSemantics = true), Right(source))
  }

  test("large source is processed without consuming the call stack") {
    val source = "CLASS \"instruction\"\n" + ("FORMAT Register:R;\n" * 100000)
    val result = SASSPreprocessor.preprocess(source, architectureSemantics = false).toOption.get
    assertEquals(result, source)
  }

  test("all recovered legacy descriptions preprocess and compile") {
    val root     = Path.of("target/sass-codegen-references")
    val manifest = ujson.read(Files.readString(root.resolve("manifest.json")))
    val digests  = manifest("targets").arr.toVector
      .filter(_("tool").str == "legacy")
      .map(_("table").str)
      .distinct

    assert(digests.nonEmpty)
    digests.foreach { digest =>
      val source = Using.resource(GZIPInputStream(Files.newInputStream(root.resolve(s"tables/$digest.txt.gz"))))(
        stream => String(stream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8)
      )
      val active = SASSPreprocessor.preprocess(source, architectureSemantics = false)
        .fold(error => fail(s"$digest preprocessing failed: ${error.message} at ${error.offset}"), identity)
      assert(!active.contains("##"), digest)

      val description = SASSDescription.parse(active)
        .fold(error => fail(s"$digest description parse failed: ${error.message} at ${error.offset}"), identity)
      assert(!description.classes.exists(_.conditional), digest)
      val definitions =
        SASSConditions.definitions(description.preamble).fold(error => fail(s"$digest: $error"), identity)
      assert(definitions.parameters.contains("MAX_REG_COUNT"))
      val compiled = SASSEncoding.compile(description)
      assertEquals(compiled.forms.size, description.classes.size, digest)
    }
  }
}
