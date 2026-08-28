package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import scala.jdk.CollectionConverters.*

/** Build-time guard for the runtime collection abstraction. Generator implementation and test harness collections are
  * deliberately outside its scope.
  */
object AuditRuntimeCollections {
  private val RuntimeModules = Seq("api", "core", "isa_ptx", "isa_amdgpu", "isa_spirv", "server")

  def main(arguments: Array[String]): Unit = arguments match {
    case Array(rootValue) =>
      val root       = Path.of(rootValue).toAbsolutePath.normalize()
      val violations = RuntimeModules.flatMap { module =>
        val moduleRoot = root.resolve(module)
        if (!Files.isDirectory(moduleRoot)) Seq.empty
        else {
          val stream = Files.walk(moduleRoot)
          try
            stream.iterator().asScala
              .filter(path => Files.isRegularFile(path) && path.getFileName.toString.endsWith(".scala"))
              .filter(isRuntimeSource)
              .filterNot(path => path.endsWith(Path.of("net", "kurobako", "gemstone", "Col.scala")))
              .filter { path =>
                val source = Files.readString(path, StandardCharsets.UTF_8)
                RuntimeCollections(source) != source
              }
              .map(root.relativize)
              .toSeq
          finally stream.close()
        }
      }.sortBy(_.toString)

      if (violations.nonEmpty)
        sys.error(
          "runtime sources must construct collections through Col:\n" + violations.map(path => s"  $path").mkString(
            "\n"
          )
        )
      println(s"verified runtime collection boundary across ${RuntimeModules.size} modules")
    case _ => sys.error("usage: AuditRuntimeCollections <repository-root>")
  }

  private def isRuntimeSource(path: Path): Boolean = {
    val parts = path.iterator().asScala.map(_.toString).toSeq
    parts.sliding(2).exists(_ == Seq("src", "main")) ||
    parts.sliding(2).exists(_ == Seq("src", "main-jvm")) ||
    parts.sliding(3).exists(_ == Seq("src", "generated", "main"))
  }
}
