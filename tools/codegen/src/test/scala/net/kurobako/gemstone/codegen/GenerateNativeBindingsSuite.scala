package net.kurobako.gemstone.codegen

import NativeAPIs.*
import java.nio.file.Files
import java.nio.file.Path
import manifest.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.testing.files.*

class GenerateNativeBindingsSuite extends Suite {
  private val complete = NativeAPIs.platforms.map(_.copy(coverage = Coverage.Complete))

  private def generate(input: Path, output: Path, platforms: Vector[Platform] = complete): Unit =
    GenerateNativeBindings.generate(output, input, platforms)

  private def partial(coverage: Coverage.Partial): Vector[Platform] =
    complete.map(platform => if (platform.name == "cuda") platform.copy(coverage = coverage) else platform)

  test("native signatures and dispatch holes follow a small independent manifest") {
    val input = manifest()
    surface(input, "cuda-driver")("functions") = ujson.Arr(
      function("zeta", "int", "int (const char *, void (*)(int, int))", "const char *", "void (*)(int, int)"),
      function("alpha", "void", "void (void)")
    )
    fixture(input) { (manifest, output) =>
      generate(manifest, output)
      val files  = snapshot(output)
      val header = files("cuda/gemstone_cuda_api.h")
      assert(header.contains("extern int zeta(const char *, void (*)(int, int)) GEMSTONE_PUBLIC_API;"), header)
      assert(header.indexOf("alpha(") < header.indexOf("zeta("))
      assertEquals(
        files.keySet.filter(_.startsWith("cuda/")),
        Set("cuda/gemstone_cuda_api.h", "cuda/gemstone_cuda_impl.cpp")
      )
      assert(files(
        "cuda/gemstone_cuda_impl.cpp"
      ).contains("reinterpret_cast<void (*)()>(static_cast<int (*)(const char *, void (*)(int, int))>(&zeta)),"))
      val dispatch = files("opencl/gemstone_opencl_dispatch.hpp")
      assert(dispatch.contains("dispatch.clFixture = &clFixture;"))
      assert(dispatch.contains("dispatch.clAbsent = nullptr;"))
      generate(manifest, output)
      assertEquals(snapshot(output), files)
    }
  }

  test("entry point revisions become a name-sorted history padded to the longest revision list") {
    val input = manifest()
    surface(input, "cuda-driver")("entryPoints") = ujson.Arr(
      ujson.Obj("name" -> "cuZeta", "versions"  -> ujson.Arr(2000, 3020, 11040)),
      ujson.Obj("name" -> "cuAlpha", "versions" -> ujson.Arr(8000))
    )
    fixture(input) { (manifest, output) =>
      generate(manifest, output)
      val history = snapshot(output)("cuda/gemstone_cuda_entry_points.hpp")
      for (
        expected <- Vector(
          "namespace gemstone::cuda {",
          "std::array<int, 3> revisions;",
          "EntryPointHistory{\"cuAlpha\", {8000}},\n    EntryPointHistory{\"cuZeta\", {2000, 3020, 11040}},"
        )
      ) assert(history.contains(expected), history)
      assert(!snapshot(output).keySet.exists(_.startsWith("hip/gemstone_hip_entry_points")))
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

  test("merged surfaces declare identical functions once and reject conflicting ones") {
    val input = manifest()
    surface(input, "cuda-runtime")("functions") = ujson.Arr(function("fixture", "int", "int (void)"))
    fixture(input) { (manifest, output) =>
      generate(manifest, output)
      assertEquals("extern int fixture\\(".r.findAllIn(snapshot(output)("cuda/gemstone_cuda_api.h")).size, 1)
    }
    surface(input, "cuda-runtime")("functions") = ujson.Arr(function("fixture", "void", "void (void)"))
    fixture(input) { (manifest, output) =>
      val error = intercept[IllegalArgumentException](generate(manifest, output))
      assert(error.getMessage.contains("fixture"), error.getMessage)
      assert(!Files.exists(output))
    }
  }

  test("late failures and a missing dispatch table preserve existing output") {
    val input = manifest()
    fixture(input) { (manifest, output) =>
      generate(manifest, output)
      val before = snapshot(output)
      surface(input, "vulkan")("functions") =
        ujson.Arr(function("fixture", "int", "int (void)"), function("fixture", "void", "void (void)"))
      Files.writeString(manifest, input.toString)
      val _ = intercept[IllegalArgumentException](generate(manifest, output))
      assertEquals(snapshot(output), before)
      surface(input, "vulkan")("functions") = ujson.Arr(function("fixture", "int", "int (void)"))
      surface(input, "opencl")("dispatchTable") = ujson.Null
      Files.writeString(manifest, input.toString)
      val error = intercept[IllegalArgumentException](generate(manifest, output))
      assert(error.getMessage.contains("_cl_icd_dispatch"), error.getMessage)
      assertEquals(snapshot(output), before)
    }
  }

  test("unimplemented functions alias a status stub or receive a default body") {
    val input = manifest()
    surface(input, "cuda-driver")("functions") = ujson.Arr(
      function("cuDone", "CUresult", "CUresult (int)", "int"),
      function("cuMissing", "CUresult", "CUresult (int)", "int"),
      function("cuName", "const char *", "const char *(int)", "int"),
      function("cuCreate", "CUhandle", "CUhandle (int, CUresult *)", "int", "CUresult *"),
      function("cuNotify", "void", "void (void *)", "void *")
    )
    surface(input, "cuda-runtime")("functions") =
      ujson.Arr(function("cudaMissing", "cudaError_t", "cudaError_t (void)"))
    val statuses = Vector(Status("CUresult", "DRIVER", "driverStub"), Status("cudaError_t", "RUNTIME", "runtimeStub"))
    fixture(input) { (manifest, output) =>
      generate(
        manifest,
        output,
        partial(Coverage.Partial(statuses, Set("cuDone"), Vector("cuOld" -> "decltype(cuDone)")))
      )
      val source = snapshot(output)("cuda/gemstone_cuda_unsupported.cpp")
      for (
        expected <- Vector(
          "GEMSTONE_INTERNAL_API CUresult driverStub() { return DRIVER; }",
          "GEMSTONE_INTERNAL_API cudaError_t runtimeStub() { return RUNTIME; }",
          "GEMSTONE_UNSUPPORTED(cuMissing, driverStub)",
          "GEMSTONE_UNSUPPORTED(cudaMissing, runtimeStub)",
          "GEMSTONE_UNSUPPORTED_AS(decltype(cuDone), cuOld, driverStub)",
          "const char * cuName(int) { return \"unsupported\"; }",
          "CUhandle cuCreate(int, CUresult *error) { if (error) *error = DRIVER; return {}; }",
          "void cuNotify(void *) { }"
        )
      ) assert(source.contains(expected), source)
      assert(!source.contains("cuDone,"), source)
      generate(manifest, output, partial(Coverage.Partial(statuses, Set("cuDone"), erased = true)))
      assert(snapshot(output)("cuda/gemstone_cuda_unsupported.cpp").contains(
        "GEMSTONE_UNSUPPORTED_AS(CUresult(), cuMissing, driverStub)"
      ))
      val error = intercept[IllegalArgumentException](
        generate(manifest, output, partial(Coverage.Partial(statuses, Set("cuDone", "cuRemoved"))))
      )
      assert(error.getMessage.contains("cuRemoved"), error.getMessage)
    }
  }
}
