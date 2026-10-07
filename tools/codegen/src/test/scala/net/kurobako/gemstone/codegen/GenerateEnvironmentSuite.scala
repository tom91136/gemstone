package net.kurobako.gemstone.codegen

import generator.*
import java.nio.file.Files
import net.kurobako.gemstone.testing.files.*

class GenerateEnvironmentSuite extends munit.FunSuite {
  private val name      = "GEMSTONE_" + "TIMEOUT"
  private val arguments = Vector(name, "7")
  private def entries   = GenerateEnvironment.parse(arguments)

  test("table rejects duplicate names, symbol collisions, malformed names and invalid defaults") {
    for (
      invalid <- List(
        Vector.empty,
        arguments ++ arguments,
        Vector("GEMSTONE_" + "FOO1", "", "GEMSTONE_" + "FOO_1", ""),
        arguments.updated(0, "UPSTREAM_TIMEOUT"),
        arguments.updated(0, "CONFORMANCE_TIMEOUT"),
        arguments.updated(0, "GEMSTONE_" + "bad-name"),
        arguments.updated(0, "GEMSTONE_" + "FOO__BAR"),
        arguments.updated(1, "-1"),
        arguments.updated(1, "text"),
        arguments.updated(1, "9223372036854775808"),
        arguments.dropRight(1)
      )
    ) intercept[IllegalArgumentException](GenerateEnvironment.parse(invalid))
    assertEquals(entries.head.symbol, "Timeout")
    assertEquals(entries.head.default, Some(7L))
    assertEquals(GenerateEnvironment.parse(Vector(name, "")).head.default, None)
  }

  test("generation is stable and checks every output for drift without rewriting") {
    inDirectory { root =>
      GenerateEnvironment.generate(root = root, entries = entries, check = false)
      GenerateEnvironment.generate(root = root, entries = entries, check = true)
      val scala  = root.resolve("core/src/generated/main/scala/net/kurobako/gemstone/environment.scala")
      val native = root.resolve("native/generated/environment_keys.hpp")
      assert(Files.readString(scala).contains("TimeoutDefault: Long > 0 = 7L"))
      assert(!Files.exists(root.resolve("conformance")))
      assert(!Files.exists(root.resolve("config")))
      for (path <- List(scala, native)) {
        val original = Files.readString(path)
        val _        = Files.writeString(path, original + "stale\n")
        val _        = intercept[IllegalArgumentException](GenerateEnvironment.generate(
          root = root,
          entries = entries,
          check = true
        ))
        assertEquals(Files.readString(path), original + "stale\n")
        val _ = Files.writeString(path, original)
      }
      Files.delete(native)
      val _ =
        intercept[IllegalArgumentException](GenerateEnvironment.generate(root = root, entries = entries, check = true))
      GenerateEnvironment.generate(root = root, entries = entries, check = false)
      GenerateEnvironment.generate(root = root, entries = entries, check = true)
    }
  }

  test("zero defaults retain nonnegative evidence without claiming positivity") {
    inDirectory { root =>
      GenerateEnvironment.generate(root, GenerateEnvironment.parse(Vector(name, "0")), check = false)
      val scala =
        Files.readString(root.resolve("core/src/generated/main/scala/net/kurobako/gemstone/environment.scala"))
      assert(scala.contains("TimeoutDefault: Long >= 0 = 0L"))
    }
  }

  test("name audit checks Gemstone sources and leaves conformance and external names alone") {
    inDirectory { root =>
      val unknown = "GEMSTONE_" + "UNKNOWN"
      val _       = write(root, "target/cache.cpp", s"""getenv("$unknown");""")
      val _       = write(root, "native/generated/fixture.hpp", s""""$unknown"""")
      val _       = write(root, "conformance/independent.cpp", s"""getenv("$unknown");""")
      val _       = write(root, "external.cpp", """getenv("CONFORMANCE_UNREGISTERED"); getenv("PATH");""")
      GenerateEnvironment.checkNames(root, entries)
      for (
        (file, content) <- List(
          "fixture.cpp"   -> s"""getenv("$unknown");""",
          "fixture.cmake" -> s"set(value $$ENV{$unknown})",
          "fixture.toml"  -> s"environment = { $unknown = \"1\" }"
        )
      ) {
        val path  = write(root, file, content)
        val error = intercept[IllegalArgumentException](GenerateEnvironment.checkNames(root, entries))
        assert(error.getMessage.contains(s"$file:1"), error.getMessage)
        Files.delete(path)
      }
      val _ = write(root, "fixture.cpp", s"""getenv("$name");""")
      GenerateEnvironment.checkNames(root, entries)
    }
  }
}
