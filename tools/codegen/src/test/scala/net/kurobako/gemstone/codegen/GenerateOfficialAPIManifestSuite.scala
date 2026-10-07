package net.kurobako.gemstone.codegen

import generator.*
import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.testing.files.*

class GenerateOfficialAPIManifestSuite extends munit.FunSuite {
  private val cuda = """
typedef int (*Notify)(const char *, int);
extern int cuFixture(const char *name, Notify callback, int);
extern int cuFixture(const char *renamed, Notify notify, int count);
static int cuInternal(void) { return 0; }
extern int unrelated(void);
"""

  private def references(root: Path): Path = {
    val headers = Map(
      "nvidia/cuda/include/cuda.h"         -> cuda,
      "nvidia/cuda/include/cudaTypedefs.h" -> """
typedef int (*PFN_cuFixture_v3020)(const char *, Notify, int);
#if defined(__CUDA_API_VERSION_INTERNAL)
typedef int (*PFN_cuFixture_v2000)(const char *, Notify, int);
#endif
typedef int (*PFN_cuFixture_v7000_ptds)(const char *, Notify, int);
typedef int (*PFN_cuOther_v11040)(void);
typedef int (*cuUnversioned)(void);
""",
      "nvidia/cuda/include/cuda_runtime_api.h"        -> "extern void cudaFixture(void);",
      "amd/hip/include/hip/hip_runtime_api.h"         -> "extern void hipFixture(void);",
      "amd/hsa/runtime/hsa-runtime/inc/hsa.h"         -> "extern void hsa_fixture(void);",
      "amd/hsa/runtime/hsa-runtime/inc/hsa_ext_amd.h" -> "",
      "amd/hsa/libhsakmt/include/hsakmt/hsakmt.h"     -> "extern void hsaKmtFixture(void);",
      "oneapi/level-zero/include/ze_ddi.h"            -> "extern int zeGetFixtureProcAddrTable(void);",
      "oneapi/level-zero/include/zes_ddi.h"           -> "",
      "oneapi/level-zero/include/zet_ddi.h"           -> "",
      "khronos/opencl/CL/cl_icd.h"                    -> """
typedef int clFixture_t(void);
struct _cl_icd_dispatch;
struct _cl_icd_dispatch { clFixture_t *clFixture; void *reserved; };
extern int clFixture(void);
""",
      "khronos/opencl/CL/cl_ext.h"             -> "",
      "khronos/opencl/CL/cl_gl.h"              -> "",
      "khronos/vulkan/include/vulkan/vulkan.h" -> "extern void vkFixture(void);"
    )
    headers.foreach { (name, content) =>
      val _ = write(root, name, content)
    }
    root
  }

  private def generate(input: Path, output: Path): Unit =
    GenerateOfficialAPIManifest.main(Array(
      "--clang",
      sys.env.getOrElse("CLANG", "clang"),
      input.toString,
      output.toString
    ))

  test(
    "Clang declarations preserve prototypes and merge redeclarations while filtering internal symbols"
  ) {
    inDirectory { root =>
      val input  = references(root.resolve("headers"))
      val output = root.resolve("manifest.json")
      generate(input, output)
      val surfaces = ujson.read(Files.readString(output))("surfaces").arr.map(s => s("name").str -> s).toMap
      assertEquals(surfaces.keySet, NativeAPIs.platforms.flatMap(_.surfaces).map(_.name).toSet)
      val driver = surfaces("cuda-driver")
      assertEquals(
        driver("functions"),
        ujson.Arr(ujson.Obj(
          "name"         -> "cuFixture",
          "returnType"   -> "int",
          "functionType" -> "int (const char *, Notify, int)",
          "parameters"   -> ujson.Arr("const char *", "Notify", "int")
        ))
      )
      val before = Files.readString(output)
      generate(input, output)
      assertEquals(Files.readString(output), before)
    }
  }

  test("entry point typedefs list each function's revision versions without per-thread variants") {
    inDirectory { root =>
      val input  = references(root.resolve("headers"))
      val output = root.resolve("manifest.json")
      generate(input, output)
      val surfaces = ujson.read(Files.readString(output))("surfaces").arr.map(s => s("name").str -> s).toMap
      assertEquals(
        surfaces("cuda-driver")("entryPoints"),
        ujson.Arr(
          ujson.Obj("name" -> "cuFixture", "versions" -> ujson.Arr(2000, 3020)),
          ujson.Obj("name" -> "cuOther", "versions"   -> ujson.Arr(11040))
        )
      )
      assert(!surfaces("cuda-runtime").obj.contains("entryPoints"))
    }
  }

  test("dispatch records preserve typedef pointers, direct callbacks and reserved slots") {
    inDirectory { root =>
      val input  = references(root.resolve("headers"))
      val output = root.resolve("manifest.json")
      generate(input, output)
      val tables = ujson.read(Files.readString(output))("surfaces").arr.map(_("dispatchTable")).filterNot(_.isNull)
        .map(t => t("name").str -> t("fields").arr.map(f => f("name").str -> f("cType").str).toVector).toMap
      assertEquals(tables, Map("_cl_icd_dispatch" -> Vector("clFixture" -> "clFixture_t *", "reserved" -> "void *")))
    }
  }

  test("a header error in the last surface preserves the previous manifest") {
    inDirectory { root =>
      val input  = references(root.resolve("headers"))
      val output = write(root, "manifest.json", "previous manifest\n")
      val _      = write(input, "khronos/vulkan/include/vulkan/vulkan.h", "this is not valid C;")
      val error  = intercept[RuntimeException](generate(input, output))
      assert(error.getMessage.contains("clang failed"), error.getMessage)
      assert(error.getMessage.contains("vulkan.h"), error.getMessage)
      assertEquals(Files.readString(output), "previous manifest\n")
    }
  }
}
