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
enum { CU_FIRST = -7, CU_NEXT, CU_SHIFT = 1 << 9, CU_AFTER };
"""

  private def references(root: Path): Path = {
    val headers = Map(
      "nvidia/cuda/include/cuda.h"                    -> cuda,
      "nvidia/cuda/include/cuda_runtime_api.h"        -> "extern void cudaFixture(void);",
      "nvidia/cuda/include/cublas_api.h"              -> "enum { CUBLAS_FIXTURE = 3 };",
      "amd/hip/include/hip/hip_runtime_api.h"         -> "extern void hipFixture(void);",
      "amd/hsa/runtime/hsa-runtime/inc/hsa.h"         -> "extern void hsa_fixture(void);",
      "amd/hsa/runtime/hsa-runtime/inc/hsa_ext_amd.h" -> "",
      "amd/hsa/libhsakmt/include/hsakmt/hsakmt.h"     -> "extern void hsaKmtFixture(void);",
      "oneapi/level-zero/include/ze_api.h"            -> "extern void zeFixture(void);",
      "oneapi/level-zero/include/zes_api.h"           -> "",
      "oneapi/level-zero/include/zet_api.h"           -> "",
      "oneapi/level-zero/include/ze_ddi.h"            -> """
typedef int (*ze_pfnFixture_t)(int);
typedef struct _ze_fixture_dditable_t {
  ze_pfnFixture_t pfnFirst;
  void (*pfnSecond)(int);
} ze_fixture_dditable_t;
struct _ze_dditable_t { ze_fixture_dditable_t core; struct Nested { int (*extra)(void); } nested; };
extern int zeGetFixtureProcAddrTable(void);
""",
      "oneapi/level-zero/include/zes_ddi.h" -> "",
      "oneapi/level-zero/include/zet_ddi.h" -> "",
      "khronos/opencl/CL/cl_icd.h"          -> """
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
    "Clang declarations preserve prototypes, unnamed arguments and enum expressions while filtering internal symbols"
  ) {
    inDirectory { root =>
      val input  = references(root.resolve("headers"))
      val output = root.resolve("manifest.json")
      generate(input, output)
      val surfaces = ujson.read(Files.readString(output))("surfaces").arr.map(s => s("name").str -> s).toMap
      assertEquals(
        surfaces.keySet,
        Set(
          "cuda-driver",
          "cuda-runtime",
          "cuda-cublas",
          "hip",
          "hsa",
          "hsakmt",
          "level-zero",
          "level-zero-ddi",
          "opencl",
          "vulkan"
        )
      )
      val driver = surfaces("cuda-driver")
      assertEquals(
        driver("functions"),
        ujson.Arr(ujson.Obj(
          "name"         -> "cuFixture",
          "returnType"   -> "int",
          "functionType" -> "int (const char *, Notify, int)",
          "parameters"   -> ujson.Arr(
            ujson.Obj("name" -> "name", "cType"     -> "const char *"),
            ujson.Obj("name" -> "callback", "cType" -> "Notify"),
            ujson.Obj("name" -> ujson.Null, "cType" -> "int")
          )
        ))
      )
      assertEquals(
        driver("callbackTypedefs"),
        ujson.Arr(ujson.Obj("name" -> "Notify", "cType" -> "int (*)(const char *, int)"))
      )
      assertEquals(
        driver("enumConstants").arr.map(c => c("name").str -> c("value").str).toMap,
        Map("CU_FIRST" -> "-7", "CU_NEXT" -> "-6", "CU_SHIFT" -> "512", "CU_AFTER" -> "513")
      )
      val before = Files.readString(output)
      generate(input, output)
      assertEquals(Files.readString(output), before)
    }
  }

  test("dispatch records preserve typedef pointers, direct callbacks, reserved slots and nested tables") {
    inDirectory { root =>
      val input  = references(root.resolve("headers"))
      val output = root.resolve("manifest.json")
      generate(input, output)
      val tables = ujson.read(Files.readString(output))("surfaces").arr.flatMap(_("dispatchTables").arr)
        .map(t => t("name").str -> t("fields").arr.map(f => f("name").str -> f("cType").str).toVector).toMap
      assertEquals(
        tables,
        Map(
          "_cl_icd_dispatch"       -> Vector("clFixture" -> "clFixture_t *", "reserved" -> "void *"),
          "_ze_fixture_dditable_t" -> Vector("pfnFirst" -> "ze_pfnFixture_t", "pfnSecond" -> "void (*)(int)"),
          "_ze_dditable_t"         -> Vector("core" -> "ze_fixture_dditable_t", "nested" -> "struct Nested")
        )
      )
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
