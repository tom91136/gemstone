package net.kurobako.gemstone.codegen

import generator.*
import java.nio.file.Files
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.testing.files.*

class GenerateEnvironmentSuite extends Suite {
  private val name        = "GEMSTONE_" + "TIMEOUT"
  private val owned       = "CONFORMANCE_" + "TIMEOUT"
  private val arguments   = Vector(name, "7")
  private def entries     = GenerateEnvironment.parse("GEMSTONE", arguments)
  private def conformance = GenerateEnvironment.parse("CONFORMANCE", Vector(owned, ""))

  test("table rejects duplicate names, symbol collisions, malformed names and invalid defaults") {
    for (
      invalid <- List(
        Vector.empty,
        arguments ++ arguments,
        Vector("GEMSTONE_" + "FOO1", "", "GEMSTONE_" + "FOO_1", ""),
        arguments.updated(0, "UPSTREAM_TIMEOUT"),
        arguments.updated(0, owned),
        arguments.updated(0, "GEMSTONE_" + "bad-name"),
        arguments.updated(0, "GEMSTONE_" + "FOO__BAR"),
        arguments.updated(1, "-1"),
        arguments.updated(1, "text"),
        arguments.updated(1, "9223372036854775808"),
        arguments.dropRight(1)
      )
    ) intercept[IllegalArgumentException](GenerateEnvironment.parse("GEMSTONE", invalid))
    val _ = intercept[IllegalArgumentException](GenerateEnvironment.parse("CONFORMANCE", arguments))
    assertEquals(entries.head.symbol, "Timeout")
    assertEquals(conformance.head.symbol, "Timeout")
    assertEquals(entries.head.default, Some(7L))
    assertEquals(GenerateEnvironment.parse("GEMSTONE", Vector(name, "")).head.default, None)
  }

  test("generation is stable and checks every output for drift without rewriting") {
    inDirectory { root =>
      def generate(check: Boolean): Unit =
        GenerateEnvironment.generate(root = root, gemstone = entries, conformance = conformance, check = check)
      generate(check = false)
      generate(check = true)
      val scala  = root.resolve("core/src/generated/main/scala/net/kurobako/gemstone/environment.scala")
      val native = root.resolve("native/generated/environment_keys.hpp")
      val tool   = root.resolve(
        "tools/conformance/src/generated/main/scala/net/kurobako/gemstone/tools/conformance/conformanceEnvironment.scala"
      )
      val testing =
        root.resolve("test-support/src/generated/main/scala/net/kurobako/gemstone/testing/environmentKeys.scala")
      assert(Files.readString(scala).contains("TimeoutDefault: Long > 0 = 7L"))
      assert(Files.readString(testing).contains("TimeoutDefault: Long = 7L"))
      assert(!Files.readString(testing).contains("constraint"))
      assert(Files.readString(tool).contains(s"""val Timeout: String = "$owned""""))
      assert(!Files.readString(tool).contains("constraint"))
      assert(!Files.exists(root.resolve("conformance")))
      assert(!Files.exists(root.resolve("config")))
      for (path <- List(scala, native, tool, testing)) {
        val original = Files.readString(path)
        val _        = Files.writeString(path, original + "stale\n")
        val _        = intercept[IllegalArgumentException](generate(check = true))
        assertEquals(Files.readString(path), original + "stale\n")
        val _ = Files.writeString(path, original)
      }
      Files.delete(native)
      val _ = intercept[IllegalArgumentException](generate(check = true))
      generate(check = false)
      generate(check = true)
    }
  }

  test("zero defaults retain nonnegative evidence without claiming positivity") {
    inDirectory { root =>
      GenerateEnvironment.generate(
        root,
        GenerateEnvironment.parse("GEMSTONE", Vector(name, "0")),
        conformance,
        check = false
      )
      val scala =
        Files.readString(root.resolve("core/src/generated/main/scala/net/kurobako/gemstone/environment.scala"))
      assert(scala.contains("TimeoutDefault: Long >= 0 = 0L"))
    }
  }

  test("conformance declares its own names and Gemstone reads them") {
    inDirectory { root =>
      val header = write(
        root,
        GenerateEnvironment.ConformanceKeys,
        s"""namespace conformance::environment {\ninline constexpr char Timeout[] = "$owned";\n}\n"""
      )
      assertEquals(GenerateEnvironment.conformance(root), conformance)
      val _ = Files.writeString(header, s"""inline constexpr char Deadline[] = "$owned";\n""")
      val _ = intercept[IllegalArgumentException](GenerateEnvironment.conformance(root))
      val _ = Files.writeString(header, s"""inline constexpr char Timeout[] = "$name";\n""")
      val _ = intercept[IllegalArgumentException](GenerateEnvironment.conformance(root))
    }
  }

  test("name audit checks every owned name and keeps conformance to its own") {
    inDirectory { root =>
      val unknown = "GEMSTONE_" + "UNKNOWN"
      val stray   = "CONFORMANCE_" + "UNREGISTERED"
      def check() = GenerateEnvironment.checkNames(root, entries, conformance)
      val _       = write(root, "target/cache.cpp", s"""getenv("$unknown");""")
      val _       = write(root, "native/generated/fixture.hpp", s""""$unknown"""")
      val _       = write(root, "conformance/independent.cpp", s"""getenv("$unknown"); getenv("$owned");""")
      val _       = write(root, "external.cpp", s"""getenv("$owned"); getenv("PATH");""")
      check()
      for (
        (file, content) <- List(
          "fixture.cpp"             -> s"""getenv("$unknown");""",
          "fixture.cmake"           -> s"set(value $$ENV{$unknown})",
          "fixture.toml"            -> s"environment = { $unknown = \"1\" }",
          "external.scala"          -> s"""sys.env.get("$stray")""",
          "conformance/fixture.cpp" -> s"""getenv("$stray");"""
        )
      ) {
        val path  = write(root, file, content)
        val error = intercept[IllegalArgumentException](check())
        assert(error.getMessage.contains(s"$file:1"), error.getMessage)
        Files.delete(path)
      }
      val _ = write(root, "fixture.cpp", s"""getenv("$name");""")
      check()
    }
  }
}
