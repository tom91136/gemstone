package net.kurobako.gemstone.isa.sass

import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.ptx.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.Suite
import scala.jdk.CollectionConverters.*
import scala.util.Using

/** Validates the PTX corpus: checked-in `.ptx` files and every PTX source the SASS fixtures compile. */
class CorpusValidationSuite extends Suite {
  private val files: Vec[(String, String)] = {
    val root = Iterator.iterate(Option(Path.of("").toAbsolutePath))(_.flatMap(path => Option(path.getParent)))
      .takeWhile(_.nonEmpty).flatten.find(path => Files.isRegularFile(path.resolve("build.sbt")))
      .getOrElse(fail("repository root not found"))
    Vec.from(Vec("isa", "conformance").map(root.resolve(_)).filter(Files.isDirectory(_)).flatMap(directory =>
      Using.resource(Files.walk(directory))(_.toList.asScala.filter(path =>
        path.toString.endsWith(".ptx") && !root.relativize(path).asScala.exists(_.toString == "target")
      ).toVector)
    ).sorted.map(path => path.toString -> Files.readString(path)))
  }

  private val corpus: Vec[(String, String)] = files ++ Recipes().ptxInputs

  test("every PTX module parses and satisfies its declared version, target and syntax forms") {
    assert(corpus.size >= 70, corpus.size)
    val failures = corpus.flatMap { (name, text) =>
      Parser.parse(text) match {
        case Left(error)    => Vec(s"$name: $error")
        case Right(program) => Validation.validate(program, forms = true) match {
            case ValidationOutcome.Accepted           => Vec.empty[String]
            case ValidationOutcome.Rejected(issues)   => issues.map(issue => s"$name: ${issue.message}")
            case ValidationOutcome.Incomplete(issues) => issues.map(issue => s"$name: ${issue.message}")
          }
      }
    }
    assertEquals(failures, Vec.empty[String])
  }
}
