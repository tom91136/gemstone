package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.abi.ABIDerivation
import net.kurobako.gemstone.abi.CFunction
import net.kurobako.gemstone.cuda.CUDA
import net.kurobako.gemstone.hip.HIP
import net.kurobako.gemstone.hsa.HSA
import net.kurobako.gemstone.levelzero.LevelZero
import net.kurobako.gemstone.opencl.OpenCL

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import upickle.default.read

/** Compares the checked-in Scala declarations with the Clang-normalized official header manifest. */
object ValidateScalaAPIs {
  final case class Declaration(surface: String, name: String, expected: String, actual: Vector[String]) {
    def validate(): Unit = require(
      actual == Vector(expected),
      if (actual.isEmpty) s"$surface Scala API is missing $name: $expected"
      else s"$surface Scala API declaration differs for $name: ${actual.mkString(" or ")} != $expected"
    )
  }

  final case class SurfaceValidation(name: String, declarations: Vector[Declaration], extra: Vector[CFunction]) {
    def validate(): Unit = {
      declarations.foreach(_.validate())
      require(extra.isEmpty, s"$name Scala API has extra declarations: ${extra.map(_.name).mkString(",")}")
    }
  }

  private val legacyCUDA      = Vector(CFunction("cuCtxCreate_v2", "CUresult (CUcontext *, unsigned int, CUdevice)"))
  private val cudaCompilerABI = Vector(
    CFunction("__cudaGetKernel", "int32_t (void **, const void *)"),
    CFunction("__cudaLaunchKernel", "int32_t (void *, GemstoneDim3, GemstoneDim3, void **, size_t, void *)"),
    CFunction("__cudaPopCallConfiguration", "int32_t (GemstoneDim3 *, GemstoneDim3 *, size_t *, void *)"),
    CFunction("__cudaPushCallConfiguration", "unsigned int (GemstoneDim3, GemstoneDim3, size_t, void *)"),
    CFunction("__cudaRegisterFatBinary", "void **(void *)"),
    CFunction("__cudaRegisterFatBinaryEnd", "void (void **)"),
    CFunction(
      "__cudaRegisterFunction",
      "void (void **, const char *, char *, const char *, int32_t, void *, void *, void *, void *, int32_t *)"
    ),
    CFunction("__cudaRegisterVar", "void (void **, char *, char *, const char *, int32_t, size_t, int32_t, int32_t)"),
    CFunction("__cudaUnregisterFatBinary", "void (void **)")
  )

  private val scalaSurfaces: Vector[(Vector[String], Vector[CFunction], Vector[CFunction])] = Vector(
    (Vector("cuda-driver", "cuda-runtime"), ABIDerivation.cFunctions[CUDA].toVector, legacyCUDA ++ cudaCompilerABI),
    (Vector("hip"), ABIDerivation.cFunctions[HIP].toVector, Vector.empty),
    (Vector("hsa"), ABIDerivation.cFunctions[HSA].toVector, Vector.empty),
    (Vector("level-zero"), ABIDerivation.cFunctions[LevelZero].toVector, Vector.empty),
    (Vector("opencl"), ABIDerivation.cFunctions[OpenCL].toVector, Vector.empty)
  )

  def main(arguments: Array[String]): Unit = arguments.toVector match {
    case Vector(manifestPath) =>
      val manifest = read[ValidateAPIs.Manifest](Files.readString(Paths.get(manifestPath), StandardCharsets.UTF_8))
      validations(manifest).foreach { validation =>
        validation.validate()
        println(s"validated ${validation.declarations.size} checked-in ${validation.name} Scala declarations")
      }
    case _ => throw IllegalArgumentException("usage: ValidateScalaAPIs <official-api-manifest>")
  }

  def validations(manifest: ValidateAPIs.Manifest): Vector[SurfaceValidation] = {
    val official = manifest.surfaces.map(surface => surface.name -> surface).toMap
    scalaSurfaces.map { case (names, actual, additions) =>
      val surfaceName = names.mkString("+")
      val expected    =
        (names.flatMap(name =>
          official.getOrElse(name, throw IllegalArgumentException(s"official manifest is missing $name")).functions
            .map(function => CFunction(function.name, canonical(function.functionType)))
        ) ++ additions).sortBy(_.name)
      val actualByName  = actual.groupMap(_.name)(_.functionType)
      val expectedNames = expected.iterator.map(_.name).toSet
      val declarations  = expected.map(function =>
        Declaration(
          surfaceName,
          function.name,
          function.functionType,
          actualByName.getOrElse(function.name, Vector.empty)
        )
      )
      val extra = actual.filterNot(function => expectedNames(function.name)).sortBy(_.name)
      SurfaceValidation(surfaceName, declarations, extra)
    }
  }

  private def canonical(functionType: String): String =
    if (functionType.endsWith("()")) functionType.dropRight(2) + "(void)" else functionType

}
