package net.kurobako.gemstone.codegen

import java.nio.file.Files
import java.nio.file.Path
import manifest.*
import net.kurobako.gemstone.testing.files.*

class GenerateAPISurfacesSuite extends munit.FunSuite {
  private def generate(input: Path, output: Path): Unit =
    GenerateAPISurfaces.main(Array(input.toString, output.toString, "1", "2", "3", "4", "5", "6"))
  private def cuda(output: Path): String =
    Files.readString(output.resolve("net/kurobako/gemstone/cuda/CUDA.scala")).replaceAll("\\s+", " ")

  test("pointer qualifiers belong to the correct level and unnamed parameters cannot shadow named ones") {
    val input = manifest()
    surface(input, "cuda-driver")("functions") = ujson.Arr(function(
      "match",
      "void",
      "void (const char *, const int **, int *const *)",
      ""     -> "const char *",
      "arg0" -> "const int **",
      "type" -> "int *const *"
    ))
    fixture(input) { (manifest, output) =>
      generate(manifest, output)
      assert(
        cuda(output).contains(
          "def `match`( arg0: CString, arg0_2: Pointer[Pointer[Int, Read, Opaque], ReadWrite, Opaque], `type`: Pointer[Pointer[Int, ReadWrite, Opaque], Read, Opaque] ): Unit = unsupported"
        ),
        cuda(output)
      )
    }
  }

  test("callbacks retain nested arguments and nominal typedef identity") {
    val input = manifest()
    surface(input, "cuda-driver")("callbackTypedefs") =
      ujson.Arr(ujson.Obj("name" -> "Notify", "cType" -> "int (*)(const char *, void (*)(int, int))"))
    surface(input, "cuda-driver")("functions") = ujson.Arr(function(
      "install",
      "void",
      "void (Notify, void (*)(void), int[4])",
      "notify" -> "Notify",
      "done"   -> "void (*)(void)",
      "values" -> "int[4]"
    ))
    fixture(input) { (manifest, output) =>
      generate(manifest, output)
      val actual = cuda(output)
      assert(
        actual.contains("type Notify = CallbackTypedef[\"Notify\", (CString, Callback[(Int, Int), Unit]), Int]"),
        actual
      )
      assert(
        actual.contains(
          "def install(notify: Notify, done: Callback[EmptyTuple, Unit], values: CArray[Int, 4]): Unit = unsupported"
        ),
        actual
      )
    }
  }

  test("different C types cannot silently share a sanitized Scala alias") {
    val input = manifest()
    surface(input, "cuda-driver")("functions") = ujson.Arr(function(
      "consume",
      "void",
      "void (struct foo *, struct_foo *)",
      "tagged"    -> "struct foo *",
      "typedefed" -> "struct_foo *"
    ))
    fixture(input) { (manifest, output) =>
      val error = intercept[IllegalArgumentException](generate(manifest, output))
      assert(error.getMessage.contains("struct_foo"), error.getMessage)
      assert(!Files.exists(output))
    }
  }

  test("a late invalid surface leaves every existing generated file intact") {
    val input = manifest()
    fixture(input) { (manifest, output) =>
      generate(manifest, output)
      val before = snapshot(output)
      surface(input, "cuda-driver")("functions") = ujson.Arr(function("changed", "int", "int (void)"))
      surface(input, "vulkan")("functions") = ujson.Arr()
      Files.writeString(manifest, input.toString)
      val _ = intercept[IllegalArgumentException](generate(manifest, output))
      assertEquals(snapshot(output), before)
    }
  }

  test("matching declarations merge even when parameter names differ") {
    val input = manifest()
    surface(input, "cuda-driver")("functions") = ujson.Arr(function("fixture", "int", "int (int)", "first" -> "int"))
    surface(input, "cuda-runtime")("functions") = ujson.Arr(function("fixture", "int", "int (int)", "second" -> "int"))
    surface(input, "cuda-cublas")("functions") = ujson.Arr()
    fixture(input) { (manifest, output) =>
      generate(manifest, output)
      val declarations = cuda(output)
      assertEquals("def fixture\\(".r.findAllIn(declarations).size, 1)
      assert(declarations.contains("def fixture(first: Int): Int"), declarations)
    }
  }
}
