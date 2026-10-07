package net.kurobako.gemstone.benchmarks

import java.nio.file.Files
import java.nio.file.Path

/** Fixtures from `benchmarks/generateBenchmarkFixtures`, relative to the repository root that `run` forks into. */
object BenchmarkResources {
  def bytes(name: String, target: String): Array[Byte] = {
    val file = Path.of("tools/benchmarks/target/generated-fixtures", name).toAbsolutePath
    if (!Files.isRegularFile(file))
      throw IllegalStateException(
        s"missing benchmark fixture $file; run sbt 'benchmarks/generateBenchmarkFixtures $target'"
      )
    Files.readAllBytes(file)
  }
}
