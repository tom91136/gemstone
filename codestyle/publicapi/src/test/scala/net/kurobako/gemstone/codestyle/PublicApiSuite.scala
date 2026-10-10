package net.kurobako.gemstone.codestyle

import dotty.tools.dotc.Driver
import dotty.tools.dotc.reporting.StoreReporter
import java.nio.file.Files
import munit.FunSuite
import scala.jdk.CollectionConverters.*

class PublicApiSuite extends FunSuite {
  private def compile(options: List[String], sources: (String, String)*): List[String] = {
    val directory = Files.createTempDirectory("gemstone-publicapi-")
    try {
      val output = Files.createDirectory(directory.resolve("classes"))
      val files  = sources.map { case (name, source) =>
        Files.writeString(directory.resolve(name), source).toString
      }
      val reporter  = new StoreReporter()
      val arguments = Array(
        "-classpath",
        System.getProperty("gemstone.publicapi.classpath"),
        "-d",
        output.toString,
        s"-Xplugin:${System.getProperty("gemstone.publicapi.jar")}",
        "-Xplugin-require:publicapi",
        "-color:never"
      ) ++ options.map(option => s"-P:publicapi:$option") ++ files
      new Driver().process(arguments, reporter).allErrors.map(_.message).toList
    } finally {
      val paths = Files.walk(directory)
      try paths.iterator().asScala.toList.reverse.foreach(Files.delete)
      finally paths.close()
    }
  }

  private val library = "Library.scala" -> """
    package lib
    private object Clock { def now: Long = 1L }
    private[lib] class Tuned { def value: Int = 2 }
    private def helper: Long = 5L
    object Exported {
      def now: Long = 3L
      private[lib] def internal: Long = 4L
      inline def inlined: Long = Clock.now
    }
  """

  private def scoped(source: String): List[String] =
    compile(List("lib.app"), library, "App.scala" -> s"package lib\npackage app\n$source")

  test("references to private declarations outside the scope are rejected") {
    val errors = scoped("object App { val now = Clock.now }")
    assertEquals(errors.size, 1, errors.mkString("\n"))
    assert(errors.head.contains("lib.Clock"), errors.head)
  }

  test("qualified private members and types are rejected") {
    assertEquals(scoped("object App { val now = Exported.internal }").size, 1)
    assertEquals(scoped("object App { def make(tuned: Tuned): Int = tuned.value }").size, 1)
    assertEquals(scoped("object App { val now = helper }").size, 1)
  }

  test("public declarations, inline expansions, and the scope's own private declarations are accepted") {
    assertEquals(
      scoped(
        "object App { val now = Exported.now + Exported.inlined + Own.value }\nprivate object Own { val value = 5L }"
      ),
      Nil
    )
  }

  test("compiler-synthesised references to other libraries' internals are accepted") {
    assertEquals(scoped("case class Point(x: Int)\nobject App { val names = \"a,b\".split(\",\").toVector }"), Nil)
  }

  test("without a scope the plugin checks nothing") {
    assertEquals(
      compile(Nil, library, "App.scala" -> "package lib\npackage app\nobject App { val now = Clock.now }"),
      Nil
    )
  }

  test("more than one scope is rejected") {
    val errors = compile(List("a", "b"), "A.scala" -> "class A")
    assert(errors.exists(_.contains("unknown publicapi option")), errors.mkString("\n"))
  }
}
