import Paths.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import sbt.*
import sbt.Keys.*
import scala.jdk.CollectionConverters.*

/** Scala.js has no class path, so each packaged resource becomes a module that the core resource loader finds by
  * reflection: `<package>.Resource_<sanitised file name>`.
  */
object EmbeddedResources {

  // Bounded so every literal stays within the class file constant limit even when each character needs three bytes.
  private val ChunkCharacters = 16384

  private def literal(value: String): String = value.flatMap {
    case '"'                      => "\\\""
    case '\\'                     => "\\\\"
    case c if c >= ' ' && c < 127 => c.toString
    case c                        => f"\\u${c.toInt}%04x"
  }

  private def module(packageName: String, name: String, text: String): String = {
    val appends = text.grouped(ChunkCharacters).map(chunk => s"    .append(\"${literal(chunk)}\")").mkString("\n")
    s"""package $packageName
       |
       |private object $name extends net.kurobako.gemstone.EmbeddedResource {
       |  // Read once by reflection and parsed by the owner; a val would pin the joined text for the module's lifetime.
       |  def text: String = new java.lang.StringBuilder(${text.length})
       |$appends
       |    .toString
       |}
       |""".stripMargin
  }

  private def identifier(value: String): Boolean =
    value.nonEmpty && value.head.isLetter && value.forall(c => c.isLetterOrDigit || c == '_')

  val settings: Seq[Def.Setting[?]] = Seq(
    Compile / sourceGenerators += Def.task {
      Def.uncached {
        val output = (Compile / sourceManaged).value.toPath / "embedded-resources"
        val files  = (Compile / unmanagedResourceDirectories).value.map(_.toPath).filter(Files.isDirectory(_)).flatMap {
          root =>
            (PathFinder(root.toFile) ** "*").get().map(_.toPath).filter(_.isFile).flatMap { file =>
              val parts = root.relativize(file.getParent).asScala.toSeq.map(_.toString)
              Option.when(parts.nonEmpty && parts.forall(identifier)) {
                val name   = "Resource_" + file.getFileName.toString.map(c => if (c.isLetterOrDigit) c else '_')
                val source =
                  module(parts.mkString("."), name, new String(Files.readAllBytes(file), StandardCharsets.UTF_8))
                val target = output.resolve(parts.mkString("/")).resolve(s"$name.scala")
                if (!target.isFile || new String(Files.readAllBytes(target), StandardCharsets.UTF_8) != source) {
                  target.parent.foreach(_.createDirectories())
                  Files.write(target, source.getBytes(StandardCharsets.UTF_8))
                }
                target.toFile
              }
            }
        }
        files
      }
    }.taskValue
  )
}
