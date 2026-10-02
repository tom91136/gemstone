package net.kurobako.gemstone.codegen

import java.io.IOException
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import java.util.zip.GZIPOutputStream
import net.kurobako.gemstone.collection.*
import scala.util.Using

class ExtractSASSSuite extends munit.FunSuite {
  private val description =
    """ARCHITECTURE "Fixture"
      |PROCESSOR_ID "fixture";
      |CLASS "A"
      |FORMAT Opcode;
      |END;
      |CLASS "A"
      |FORMAT Opcode;
      |END;
      |ALTERNATE CLASS "B"
      |##IF DEFINED(ARCH_SEMANTICS)
      |FORMAT Opcode;
      |##ELSE
      |FORMAT Register:Rd;
      |##ENDIF
      |END;
      |""".stripMargin
  private val bytes  = description.getBytes(UTF_8)
  private val digest = sha256(bytes)
  private val pins   = Map("legacy" -> Map("SM35" -> digest), "modern" -> Map("SM89" -> digest))

  private def sha256(bytes: Array[Byte]): String =
    MessageDigest.getInstance("SHA-256").digest(bytes).map(value => f"${value & 255}%02x").mkString

  private def copyJson(value: ujson.Value): ujson.Value = ujson.read(ujson.write(value))

  private def gzip(path: Path, bytes: Array[Byte]): Unit =
    Using.resource(GZIPOutputStream(Files.newOutputStream(path)))(_.write(bytes))

  private case class Bundle(root: Path, table: Path, tools: Vec[ExtractSASS.Tool], manifest: ujson.Obj) {
    def writeManifest(): Unit = {
      val _ = Files.writeString(root.resolve("manifest.json"), ujson.write(manifest))
    }
    def verify(): Unit = {
      writeManifest()
      ExtractSASS.verify(root, tools, pins)
    }
  }

  private def withBundle(body: Bundle => Unit): Unit = generator.inDirectory { root =>
    val tools = Vec("legacy", "modern").map { name =>
      val binary      = root.resolve(s"$name-tool")
      val toolBytes   = s"$name fixture tool\n".getBytes(UTF_8)
      val toolDigest  = sha256(toolBytes)
      val _           = Files.write(binary, toolBytes)
      val packageInfo = ujson.Obj(
        "filename"        -> s"$name.deb",
        "url"             -> s"https://example.invalid/$name.deb",
        "sha256"          -> sha256(s"$name package".getBytes(UTF_8)),
        "nvdisasm_sha256" -> toolDigest
      )
      ExtractSASS.Tool(name, binary, toolDigest, packageInfo)
    }
    val _     = Files.createDirectory(root.resolve("tables"))
    val table = root.resolve(s"tables/$digest.txt.gz")
    gzip(table, bytes)
    val manifest = ujson.Obj(
      "format"   -> 1,
      "platform" -> "ubuntu2204/x86_64",
      "packages" -> ujson.Obj.from(tools.map(tool => tool.name -> copyJson(tool.packageInfo))),
      "tools"    -> ujson.Obj.from(tools.map(tool => tool.name -> ujson.Obj("sha256" -> tool.digest))),
      "targets"  -> ujson.Arr(
        ujson.Obj("name" -> "SM35", "tool" -> "legacy", "table" -> digest),
        ujson.Obj("name" -> "SM89", "tool" -> "modern", "table" -> digest)
      ),
      "tables" -> ujson.Arr(ujson.Obj(
        "sha256"                 -> digest,
        "path"                   -> s"tables/$digest.txt.gz",
        "classes"                -> 2,
        "unique_classes"         -> 1,
        "alternate_classes"      -> 1,
        "conditional_directives" -> ujson.Obj("IF" -> 1, "ELSE" -> 1, "ENDIF" -> 1)
      )),
      "unique_descriptions" -> 1
    )
    body(Bundle(root, table, tools, manifest))
  }

  test("reference pins contain all 36 targets and 16 unique descriptions") {
    assertEquals(ExtractSASS.expected.keySet, Set("legacy", "modern"))
    assertEquals(ExtractSASS.expected.values.map(_.size).sum, 36)
    assertEquals(ExtractSASS.expected.values.flatMap(_.values).toSet.size, 16)
  }

  test("accepts shared descriptions with verified class and conditional counts") {
    val metadata = SASSCapture.inspect(bytes)
    assertEquals(metadata("class_count").num, 2.0)
    assertEquals(metadata("unique_class_count").num, 1.0)
    assertEquals(metadata("alternate_class_count").num, 1.0)
    assertEquals(metadata("conditional_directives"), ujson.Obj("IF" -> 1, "ELSE" -> 1, "ENDIF" -> 1))
    withBundle(_.verify())
  }

  test("rejects changed content even when description structure and counts remain valid") {
    withBundle { bundle =>
      gzip(bundle.table, description.replace("Fixture", "Changed").getBytes(UTF_8))
      val _ = intercept[IllegalArgumentException](bundle.verify())
    }
  }

  test("rejects every mismatched class count and conditional count") {
    Vec("classes", "unique_classes", "alternate_classes").foreach { key =>
      withBundle { bundle =>
        bundle.manifest("tables")(0)(key) = 99
        val _ = intercept[IllegalArgumentException](bundle.verify())
      }
    }
    Vec("IF", "ELSE", "ENDIF").foreach { key =>
      withBundle { bundle =>
        bundle.manifest("tables")(0)("conditional_directives")(key) = 99
        val _ = intercept[IllegalArgumentException](bundle.verify())
      }
    }
  }

  test("rejects duplicate targets and duplicate tables") {
    Vec("targets", "tables").foreach { key =>
      withBundle { bundle =>
        val _ = bundle.manifest(key).arr += copyJson(bundle.manifest(key)(0))
        val _ = intercept[IllegalArgumentException](bundle.verify())
      }
    }
  }

  test("rejects unknown or missing targets and incorrect target mappings") {
    Vec[(String, ujson.Obj => Unit)](
      "unknown target"     -> (manifest => manifest("targets")(0)("name") = "SM999"),
      "missing target"     -> (manifest => { val _ = manifest("targets").arr.remove(0) }),
      "wrong tool"         -> (manifest => manifest("targets")(0)("tool") = "modern"),
      "unknown tool"       -> (manifest => manifest("targets")(0)("tool") = "unknown"),
      "wrong table"        -> (manifest => manifest("targets")(0)("table") = "0" * 64),
      "missing table"      -> (manifest => manifest("tables") = ujson.Arr()),
      "wrong unique count" -> (manifest => manifest("unique_descriptions") = 2)
    ).foreach { (name, mutate) =>
      withBundle { bundle =>
        mutate(bundle.manifest)
        val error = intercept[IllegalArgumentException](bundle.verify())
        assert(error.getMessage.nonEmpty, name)
      }
    }
  }

  test("rejects altered package provenance, platform, manifest version, and tool hashes") {
    Vec[ujson.Obj => Unit](
      manifest => manifest("packages")("legacy")("sha256") = "0" * 64,
      manifest => manifest("platform") = "other/platform",
      manifest => manifest("format") = 2,
      manifest => manifest("tools")("modern")("sha256") = "0" * 64
    ).foreach { mutate =>
      withBundle { bundle =>
        mutate(bundle.manifest)
        val _ = intercept[IllegalArgumentException](bundle.verify())
      }
    }
  }

  test("rejects parent-relative and absolute table paths") {
    withBundle { bundle =>
      Vec("../outside.txt.gz", bundle.table.toAbsolutePath.toString).foreach { path =>
        bundle.manifest("tables")(0)("path") = path
        val _ = intercept[IllegalArgumentException](bundle.verify())
      }
    }
  }

  test("rejects duplicate and missing tools even when package provenance matches") {
    withBundle { bundle =>
      Vec(bundle.tools :+ bundle.tools.head, bundle.tools.take(1)).foreach { tools =>
        bundle.manifest("packages") = ujson.Obj.from(tools.map(tool => tool.name -> copyJson(tool.packageInfo)))
        bundle.writeManifest()
        val _ = intercept[IllegalArgumentException](ExtractSASS.verify(bundle.root, tools, pins))
      }
    }
  }

  test("extraction rejects an escaping tables directory before executing tools or writing files") {
    generator.inDirectory { outside =>
      withBundle { bundle =>
        Files.delete(bundle.table)
        Files.delete(bundle.root.resolve("tables"))
        val _     = Files.createSymbolicLink(bundle.root.resolve("tables"), outside)
        val error = intercept[IllegalArgumentException](ExtractSASS.extract(bundle.root, bundle.tools))
        assert(error.getMessage.contains("escapes"), error.getMessage)
        Using.resource(Files.list(outside))(paths => assertEquals(paths.count(), 0L))
      }
    }
  }

  test("rejects a table symlink escaping the bundle") {
    generator.inDirectory { outside =>
      val externalTable = outside.resolve("external.txt.gz")
      gzip(externalTable, bytes)
      withBundle { bundle =>
        val _ = Files.createSymbolicLink(bundle.root.resolve("escape.txt.gz"), externalTable)
        bundle.manifest("tables")(0)("path") = "escape.txt.gz"
        val _ = intercept[IllegalArgumentException](bundle.verify())
      }
    }
  }

  test("reports missing and malformed gzip tables as I/O failures") {
    withBundle { bundle =>
      Files.delete(bundle.table)
      val _ = intercept[IOException](bundle.verify())
      val _ = Files.write(bundle.table, "not gzip".getBytes(UTF_8))
      val _ = intercept[IOException](bundle.verify())
      gzip(bundle.table, bytes)
      val compressed = Files.readAllBytes(bundle.table)
      val _          = Files.write(bundle.table, compressed.dropRight(4))
      val _          = intercept[IOException](bundle.verify())
    }
  }
}
