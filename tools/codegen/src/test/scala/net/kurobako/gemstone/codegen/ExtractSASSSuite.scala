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
  private val description = "ARCHITECTURE \"Fixture\"\nCLASS \"A\"\nFORMAT Opcode;\nEND;\n"
  private val bytes       = description.getBytes(UTF_8)
  private val digest      = sha256(bytes)
  private val pins        = Map("legacy" -> Map("SM35" -> digest), "modern" -> Map("SM89" -> digest))

  private def sha256(bytes: Array[Byte]): String =
    MessageDigest.getInstance("SHA-256").digest(bytes).map(value => f"${value & 255}%02x").mkString

  private def gzip(path: Path, bytes: Array[Byte]): Unit =
    Using.resource(GZIPOutputStream(Files.newOutputStream(path)))(_.write(bytes))

  private case class Bundle(root: Path, table: Path, tools: Vec[ExtractSASS.Tool], manifest: ujson.Obj) {
    def verify(): Unit = {
      val _ = Files.writeString(root.resolve("manifest.json"), ujson.write(manifest))
      ExtractSASS.verify(root, tools, pins)
    }
  }

  private def withBundle(body: Bundle => Unit): Unit = generator.inDirectory { root =>
    val tools = Vec("legacy", "modern").map { name =>
      val binary     = root.resolve(s"$name-tool")
      val toolBytes  = s"$name fixture tool\n".getBytes(UTF_8)
      val _          = Files.write(binary, toolBytes)
      val toolDigest = sha256(toolBytes)
      ExtractSASS.Tool(
        name,
        binary,
        toolDigest,
        ujson.Obj("filename" -> s"$name.deb", "sha256" -> sha256(name.getBytes(UTF_8)), "nvdisasm_sha256" -> toolDigest)
      )
    }
    val _     = Files.createDirectory(root.resolve("tables"))
    val table = root.resolve(s"tables/$digest.txt.gz")
    gzip(table, bytes)
    val manifest = ujson.Obj(
      "platform" -> "ubuntu2204/x86_64",
      "packages" -> ujson.Obj.from(tools.map(tool => tool.name -> ujson.read(ujson.write(tool.packageInfo)))),
      "tools"    -> ujson.Obj.from(tools.map(tool => tool.name -> ujson.Obj("sha256" -> tool.digest))),
      "targets"  -> ujson.Arr(
        ujson.Obj("name" -> "SM35", "tool" -> "legacy", "table" -> digest),
        ujson.Obj("name" -> "SM89", "tool" -> "modern", "table" -> digest)
      )
    )
    body(Bundle(root, table, tools, manifest))
  }

  test("reference pins contain all 36 targets and 16 unique descriptions") {
    assertEquals(ExtractSASS.expected.keySet, Set("legacy", "modern"))
    assertEquals(ExtractSASS.expected.values.map(_.size).sum, 36)
    assertEquals(ExtractSASS.expected.values.flatMap(_.values).toSet.size, 16)
  }

  test("accepts tables matching their pinned digests") {
    withBundle(_.verify())
  }

  test("rejects changed table content") {
    withBundle { bundle =>
      gzip(bundle.table, description.replace("Fixture", "Changed").getBytes(UTF_8))
      val _ = intercept[IllegalArgumentException](bundle.verify())
    }
  }

  test("rejects unknown, missing, duplicate and remapped targets") {
    Vec[ujson.Obj => Unit](
      manifest => manifest("targets")(0)("name") = "SM999",
      manifest => { val _ = manifest("targets").arr.remove(0) },
      manifest => { val _ = manifest("targets").arr += ujson.read(ujson.write(manifest("targets")(0))) },
      manifest => manifest("targets")(1) = ujson.read(ujson.write(manifest("targets")(0))),
      manifest => manifest("targets")(0)("tool") = "modern",
      manifest => manifest("targets")(0)("table") = "0" * 64
    ).foreach { mutate =>
      withBundle { bundle =>
        mutate(bundle.manifest)
        val _ = intercept[IllegalArgumentException](bundle.verify())
      }
    }
  }

  test("rejects altered platform, package provenance and tool digests") {
    Vec[ujson.Obj => Unit](
      manifest => manifest("platform") = "other/platform",
      manifest => manifest("packages")("legacy")("sha256") = "0" * 64,
      manifest => manifest("tools")("modern")("sha256") = "0" * 64
    ).foreach { mutate =>
      withBundle { bundle =>
        mutate(bundle.manifest)
        val _ = intercept[IllegalArgumentException](bundle.verify())
      }
    }
  }

  test("rejects tables beyond the decompressed size limit") {
    withBundle { bundle =>
      val chunk = Array.ofDim[Byte](1 << 20)
      Using.resource(GZIPOutputStream(Files.newOutputStream(bundle.table)))(output =>
        (0 to 64).foreach(_ => output.write(chunk))
      )
      val error = intercept[IllegalArgumentException](bundle.verify())
      assert(error.getMessage.contains("exceeds"), error.getMessage)
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
