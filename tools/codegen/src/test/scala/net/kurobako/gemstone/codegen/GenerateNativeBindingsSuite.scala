package net.kurobako.gemstone.codegen

import java.nio.file.Files
import java.nio.file.Path
import manifest.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.files.*
import scala.concurrent.duration.*

class GenerateNativeBindingsSuite extends munit.FunSuite {
  private def generate(input: Path, output: Path): Unit =
    GenerateNativeBindings.main(Array(output.toString, input.toString))

  test("native signatures, constants and dispatch holes follow a small independent manifest") {
    val input = manifest()
    surface(input, "cuda-driver")("functions") = ujson.Arr(
      function(
        "zeta",
        "int",
        "int (const char *, void (*)(int, int))",
        "text"     -> "const char *",
        "callback" -> "void (*)(int, int)"
      ),
      function("alpha", "void", "void (void)")
    )
    surface(input, "cuda-driver")("enumConstants") = ujson.Arr(ujson.Obj("name" -> "NEGATIVE", "value" -> "-7"))
    fixture(input) { (manifest, output) =>
      generate(manifest, output)
      val files  = snapshot(output)
      val header = files("cuda/gemstone_cuda_api.h")
      assert(header.contains("extern int zeta(const char *, void (*)(int, int)) GEMSTONE_PUBLIC_API;"), header)
      assert(header.indexOf("alpha(") < header.indexOf("zeta("))
      assert(files("cuda/gemstone_cuda_constants.hpp").contains("inline constexpr auto NEGATIVE = -7;"))
      for (
        (name, macroName) <- Vector(
          "LaunchParamEnd"           -> "CU_LAUNCH_PARAM_END",
          "LaunchParamBufferPointer" -> "CU_LAUNCH_PARAM_BUFFER_POINTER",
          "LaunchParamBufferSize"    -> "CU_LAUNCH_PARAM_BUFFER_SIZE"
        )
      ) {
        assert(files("cuda/gemstone_cuda_constants.hpp").contains(s"extern const void *const $name;"))
        assert(
          files("cuda/gemstone_cuda_impl.cpp").contains(s"const void *const gemstone::cuda::api::$name = $macroName;")
        )
      }
      assert(files(
        "cuda/gemstone_cuda_impl.cpp"
      ).contains("using signature_zeta = int (*)(const char *, void (*)(int, int));"))
      val dispatch = files("opencl/gemstone_opencl_dispatch.hpp")
      assert(dispatch.contains("dispatch.clFixture = &clFixture;"))
      assert(dispatch.contains("dispatch.clAbsent = nullptr;"))
      generate(manifest, output)
      assertEquals(snapshot(output), files)
    }
  }

  test("binding cleanup removes stale owned files while preserving other generators") {
    fixture(manifest()) { (manifest, output) =>
      val stale       = write(output, "cuda/obsolete.hpp", "old binding")
      val rpc         = write(output, "rpc/client.hpp", "RPC generator output")
      val environment = write(output, "environment_keys.hpp", "environment generator output")
      generate(manifest, output)
      assert(!Files.exists(stale))
      assertEquals(Files.readString(rpc), "RPC generator output")
      assertEquals(Files.readString(environment), "environment generator output")
    }
  }

  test("conflicting declarations cannot be silently discarded when surfaces are merged") {
    val input = manifest()
    surface(input, "cuda-runtime")("functions") = ujson.Arr(function("fixture", "void", "void (void)"))
    fixture(input) { (manifest, output) =>
      val error = intercept[IllegalArgumentException](generate(manifest, output))
      assert(error.getMessage.contains("fixture"), error.getMessage)
      assert(!Files.exists(output))
    }
  }

  test("conflicting constants and malformed dispatch tables preserve existing output") {
    val input = manifest()
    fixture(input) { (manifest, output) =>
      generate(manifest, output)
      val before = snapshot(output)
      surface(input, "cuda-driver")("enumConstants") = ujson.Arr(ujson.Obj("name" -> "VALUE", "value" -> "1"))
      surface(input, "cuda-runtime")("enumConstants") = ujson.Arr(ujson.Obj("name" -> "VALUE", "value" -> "2"))
      Files.writeString(manifest, input.toString)
      val _ = intercept[IllegalStateException](generate(manifest, output))
      assertEquals(snapshot(output), before)
      surface(input, "cuda-runtime")("enumConstants") = ujson.Arr()
      surface(input, "opencl")("dispatchTables") = ujson.Arr()
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
      val declarations = snapshot(output)("cuda/gemstone_cuda_api.h")
      assertEquals("extern int fixture\\(".r.findAllIn(declarations).size, 1)
    }
  }

  test("generated DDI clearing resets callbacks in both leaf and aggregate tables") {
    val input = manifest()
    val _     = surface(input, "level-zero-ddi")("dispatchTables").arr.addOne(ujson.Obj(
      "name"   -> "_ze_dditable_t",
      "fields" -> ujson.Arr(ujson.Obj("name" -> "core", "cType" -> "_ze_fixture_dditable_t"))
    ))
    fixture(input) { (manifest, output) =>
      generate(manifest, output)
      val root = manifest.getParent
      val _    = write(
        root,
        "ze_ddi.h",
        """
struct _ze_fixture_dditable_t { void (*pfnFirst)(); void (*pfnSecond)(); };
struct _ze_dditable_t { _ze_fixture_dditable_t core; };
"""
      )
      val _      = write(root, "zes_ddi.h", "")
      val _      = write(root, "zet_ddi.h", "")
      val source = write(
        root,
        "clear.cpp",
        """
#include "gemstone_levelzero_ddi.hpp"
void callback() {}
int main() {
  _ze_fixture_dditable_t leaf{callback, callback};
  gemstone_levelzero_ddi::clear(&leaf);
  if (leaf.pfnFirst || leaf.pfnSecond) return 1;
  _ze_dditable_t aggregate{{callback, callback}};
  gemstone_levelzero_ddi::clear(&aggregate);
  return aggregate.core.pfnFirst || aggregate.core.pfnSecond ? 2 : 0;
}
"""
      )
      val executable = root.resolve("clear")
      val compile    = process.run(
        Seq(
          sys.env.getOrElse("CXX", "clang++"),
          "-std=c++17",
          s"-I$root",
          s"-I${output.resolve("levelzero")}",
          source.toString,
          "-o",
          executable.toString
        ),
        root,
        Map.empty,
        Set.empty,
        30.seconds
      )
      assertEquals(compile.exit, 0, compile.diagnostic)
      val run = process.run(Seq(executable.toString), root, Map.empty, Set.empty, 5.seconds)
      assertEquals(run.exit, 0, run.diagnostic)
    }
  }
}
