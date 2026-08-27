package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.abi.ABIDerivation
import net.kurobako.gemstone.abi.CFunction
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
  private val scalaSurfaces: Vector[(Vector[String], Vector[CFunction])] = Vector(
    Vector("hip")        -> ABIDerivation.cFunctions[HIP],
    Vector("hsa")        -> ABIDerivation.cFunctions[HSA],
    Vector("level-zero") -> ABIDerivation.cFunctions[LevelZero],
    Vector("opencl")     -> ABIDerivation.cFunctions[OpenCL]
  )

  def main(arguments: Array[String]): Unit = arguments.toVector match {
    case Vector(manifestPath) =>
      val manifest = read[ValidateAPIs.Manifest](Files.readString(Paths.get(manifestPath), StandardCharsets.UTF_8))
      val official = manifest.surfaces.map(surface => surface.name -> surface).toMap
      scalaSurfaces.foreach { case (names, actual) =>
        val expected = names.flatMap(name =>
          official(name).functions.map(function => CFunction(function.name, function.functionType))
        )
          .sortBy(_.name)
        require(actual == expected, mismatch(names.mkString("+"), expected, actual))
        println(s"validated ${actual.size} checked-in ${names.mkString("+")} Scala declarations")
      }
    case _ => throw new IllegalArgumentException("usage: ValidateScalaAPIs <official-api-manifest>")
  }

  private def mismatch(name: String, expected: Vector[CFunction], actual: Vector[CFunction]): String = {
    val expectedMap = expected.map(function => function.name -> function.functionType).toMap
    val actualMap   = actual.map(function => function.name -> function.functionType).toMap
    val missing     = expectedMap.keySet.diff(actualMap.keySet).toVector.sorted
    val extra       = actualMap.keySet.diff(expectedMap.keySet).toVector.sorted
    val changed     = expectedMap.keySet.intersect(actualMap.keySet).toVector.sorted.collect {
      case function if expectedMap(function) != actualMap(function) =>
        s"$function: ${actualMap(function)} != ${expectedMap(function)}"
    }
    s"$name Scala API differs from its official headers; missing=${missing.mkString(",")}; extra=${extra.mkString(",")}; changed=${changed.mkString("; ")}"
  }
}
