package net.kurobako.gemstone.codegen

import generator.*
import java.nio.file.Files
import java.nio.file.Path

private object manifest {
  def function(name: String, result: String, signature: String, parameters: String*): ujson.Obj =
    ujson.Obj(
      "name"         -> name,
      "returnType"   -> result,
      "functionType" -> signature,
      "parameters"   -> ujson.Arr.from(parameters.map(ujson.Str(_)))
    )

  def manifest(): ujson.Obj = ujson.Obj(
    "format"   -> 6,
    "surfaces" -> ujson.Arr.from(NativeAPIs.platforms.flatMap(_.surfaces).map { surface =>
      ujson.Obj(
        "name"      -> surface.name,
        "functions" ->
          ujson.Arr(function(if (surface.name == "opencl") "clFixture" else "fixture", "int", "int (void)")),
        "dispatchTable" -> surface.dispatchTable.fold(ujson.Null)(name =>
          ujson.Obj(
            "name"   -> name,
            "fields" -> ujson.Arr(
              ujson.Obj("name" -> "clFixture", "cType" -> "int (*)(void)"),
              ujson.Obj("name" -> "clAbsent", "cType"  -> "void (*)(void)")
            )
          )
        )
      )
    })
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
