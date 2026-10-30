package net.kurobako.gemstone.isa.amdgpu

import MachineDefinitions.*
import dotty.tools.dotc.Driver
import dotty.tools.dotc.reporting.StoreReporter
import java.nio.file.Files
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.testing.Suite
import scala.concurrent.duration.*

class RenderedMachineSuite extends Suite {
  // each test runs the Scala compiler over every rendered form of a family
  override def baseTimeout: FiniteDuration = 3.minutes

  private def errors(source: String): List[String] = {
    val directory = Files.createTempDirectory("gemstone-rendered-")
    try {
      val file = directory.resolve("Rendered.scala")
      Files.writeString(file, s"package net.kurobako.gemstone.isa.amdgpu\n\n$source\n")
      val output   = Files.createDirectory(directory.resolve("classes"))
      val reporter = StoreReporter()
      val _        = Driver().process(
        Array(
          "-classpath",
          sys.props("gemstone.amdgpu.classpath"),
          "-d",
          output.toString,
          "-color:never",
          file.toString
        ),
        reporter
      )
      reporter.allErrors.map(_.message)
    } finally {
      val paths = Files.walk(directory)
      try paths.sorted(java.util.Comparator.reverseOrder()).forEach(path => Files.delete(path))
      finally paths.close()
    }
  }

  test("the compiler rejects ill-typed source") {
    assertEquals(errors("def broken(): Int = \"text\"").size, 1)
  }

  Instances.foreach { (target, text, operands) =>
    test(s"rendered scalar control instruction $target $text compiles") {
      val definition = machine(target, text, operands).fold(failure => fail(failure), identity)
      assertEquals(errors(ScalaBackend.source(definition)), Nil, s"$target $text")
    }
  }
}
