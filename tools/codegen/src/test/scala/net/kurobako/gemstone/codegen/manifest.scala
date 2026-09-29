package net.kurobako.gemstone.codegen

import generator.*
import java.nio.file.Files
import java.nio.file.Path

private object manifest {
  def function(name: String, result: String, signature: String, parameters: (String, String)*): ujson.Obj =
    ujson.Obj(
      "name"         -> name,
      "returnType"   -> result,
      "functionType" -> signature,
      "parameters"   -> ujson.Arr.from(parameters.map { (name, cType) =>
        ujson.Obj("name" -> (if (name.isEmpty) ujson.Null else ujson.Str(name)), "cType" -> cType)
      })
    )

  def manifest(): ujson.Obj = ujson.Obj(
    "format"   -> 4,
    "surfaces" -> ujson.Arr.from(
      Seq(
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
      ).map { name =>
        ujson.Obj(
          "name"      -> name,
          "functions" -> ujson.Arr(function(if (name == "opencl") "clFixture" else "fixture", "int", "int (void)")),
          "callbackTypedefs" -> ujson.Arr(),
          "enumConstants"    -> ujson.Arr(),
          "dispatchTables"   ->
            (name match {
              case "opencl" => ujson.Arr(ujson.Obj(
                  "name"   -> "cl_icd_dispatch",
                  "fields" -> ujson.Arr(
                    ujson.Obj("name" -> "clFixture", "cType" -> "int (*)(void)"),
                    ujson.Obj("name" -> "clAbsent", "cType"  -> "void (*)(void)")
                  )
                ))
              case "level-zero-ddi" => ujson.Arr(ujson.Obj(
                  "name"   -> "_ze_fixture_dditable_t",
                  "fields" -> ujson.Arr(
                    ujson.Obj("name" -> "pfnFirst", "cType"  -> "void (*)(void)"),
                    ujson.Obj("name" -> "pfnSecond", "cType" -> "void (*)(void)")
                  )
                ))
              case _ => ujson.Arr()
            })
        )
      }
    )
  )

  def surface(manifest: ujson.Obj, name: String): ujson.Value =
    manifest("surfaces").arr.find(_("name").str == name).get

  def fixture[A](manifest: ujson.Obj)(body: (Path, Path) => A): A =
    inDirectory { directory =>
      val input = directory.resolve("manifest.json")
      Files.writeString(input, manifest.toString)
      body(input, directory.resolve("output"))
    }
}
