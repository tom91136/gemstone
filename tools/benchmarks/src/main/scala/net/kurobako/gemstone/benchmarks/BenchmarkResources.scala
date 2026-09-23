package net.kurobako.gemstone.benchmarks

import java.nio.file.Files
import java.nio.file.Path

/** Generated fixtures are kept under the ignored benchmark target directory. */
object BenchmarkResources {
  def bytes(name: String): Array[Byte] = {
    val file = Path.of("tools/benchmarks/target/generated-fixtures", name)
    if (!Files.isRegularFile(file)) {
      val target = name match {
        case "opencl-cts-async-copy.spv" => "opencl"
        case "cloverleaf-flux.ptx"       => "cuda"
        case "cloverleaf-flux.hsaco"     => "hip"
        case "hsa-vector-copy.hsaco"     => "hsa"
        case _                           => "all"
      }
      throw IllegalStateException(
        s"missing benchmark fixture $file; run sbt 'benchmarks/generateBenchmarkFixtures $target'"
      )
    }
    Files.readAllBytes(file)
  }
}
