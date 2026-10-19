package net.kurobako.gemstone.isa.check

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import scala.util.Using

/** Writes one ISA's described definitions, with the cases checking them against execution, as Scala sources under the
  * directory given.
  */
object Generate {
  private val generators: Map[String, Generator] =
    Map(
      "PTX"            -> PTXGenerator,
      "SASS"           -> SASSGenerator.earlier,
      "SASSHopper"     -> SASSGenerator.hopper,
      "SASSBlackwell"  -> SASSGenerator.blackwell,
      "AMDGPU"         -> AMDGPUGenerator.rest,
      "AMDGPUMatrix"   -> AMDGPUGenerator.matrix,
      "AMDGPUResource" -> AMDGPUGenerator.resource,
      "SPIRV"          -> SPIRVGenerator
    )

  def main(arguments: Array[String]): Unit = {
    val output = Paths.get(arguments(0))
    Files.createDirectories(output)
    val generator = generators.getOrElse(arguments(1), throw IllegalArgumentException(s"no ISA ${arguments(1)}"))
    val written   = generator.sources.map { (name, source) =>
      write(output.resolve(s"$name.scala"), source)
      s"$name.scala"
    }.toSet
    // A run that writes fewer chunks than the last must not leave the last run's tail to be compiled.
    Using.resource(Files.list(output))(_.toArray(Array.ofDim[Path](_)))
      .filter(path => !written(path.getFileName.toString)).foreach(Files.delete(_))
  }

  private def write(target: Path, source: String): Unit = {
    val bytes = source.getBytes(StandardCharsets.UTF_8)
    if (!Files.isRegularFile(target) || !java.util.Arrays.equals(Files.readAllBytes(target), bytes)) {
      val _ = Files.write(target, bytes)
    }
  }
}
