package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.GZIPInputStream
import net.kurobako.gemstone.testing.*
import scala.util.Using

private object generator {
  def inDirectory[A](body: Path => A): A = files.temporary(directory => output.onFailure(body(directory)))

  private lazy val sassRoot = sys.props.get("gemstone.codegen.sassReferences").map(Path.of(_))
    .filter(root => Files.isRegularFile(root.resolve("manifest.json")))
  private lazy val sassBundle = sassRoot.map(GenerateSASSISA.read)

  private def requireSASSReferences()(using munit.Location): Unit =
    munit.Assertions.assume(sassRoot.nonEmpty, "missing recovered SASS descriptions; run sbt codegen")

  /** The bundle `sbt codegen` recovers; tests reading it are skipped until it has run. Parsing every description takes
    * tens of seconds, so suites reading it raise their timeout.
    */
  def sassReferences(using munit.Location): GenerateSASSISA.Bundle = {
    requireSASSReferences()
    sassBundle.get
  }

  def sassTable(digest: String)(using munit.Location): String = {
    requireSASSReferences()
    Using.resource(GZIPInputStream(Files.newInputStream(sassRoot.get.resolve(s"tables/$digest.txt.gz"))))(input =>
      String(input.readAllBytes(), UTF_8)
    )
  }
}
