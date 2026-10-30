import java.nio.file.Files
import java.nio.file.Path
import sbt.internal.util.MessageOnlyException
import scala.sys.process.Process

/** Conformance builds without the rest of the repository: Gemstone may use conformance, never the reverse. */
object ConformanceStandalone {
  private val SourceExtensions = Set("c", "cc", "cpp", "cu", "cxx", "h", "hip", "hpp")
  private val GemstoneName     = raw"\bGEMSTONE_[A-Z0-9_]+".r
  private val GemstoneInclude  = raw"""#\s*include\s*[<"]gemstone/""".r
  private val GemstonePackage  = raw"\bnet\.kurobako\b".r
  private val ProjectParent    = raw"\$$\{(?:CMAKE_SOURCE_DIR|CMAKE_BINARY_DIR|PROJECT_SOURCE_DIR)\}/\.\.".r
  private val ListParent       = raw"\$$\{(?:CMAKE_CURRENT_LIST_DIR|CMAKE_CURRENT_SOURCE_DIR)\}((?:/\.\.)+)".r
  private val IncludeParent    = raw"""#\s*include\s*"((?:\.\./)+)""".r

  def check(conformance: Path): Unit = {
    val root  = conformance.toAbsolutePath.normalize
    val files = Process(Seq("git", "-C", s"$root", "ls-files", "--cached", "--others", "--exclude-standard")).!!
      .linesIterator.filterNot(name => name.endsWith(".md") || name.startsWith("patches/")).map(root.resolve(_))
      .filter(Files.isRegularFile(_)).toVector
    val findings = files.flatMap { path =>
      val depth  = root.relativize(path).getNameCount - 1
      val source = SourceExtensions(path.getFileName.toString.split('.').last)
      val text   = scala.util.Try(Files.readString(path)).getOrElse("")
      text.linesIterator.zipWithIndex.flatMap { (line, index) =>
        def escapes(parents: String): Boolean = parents.split('/').count(_ == "..") > depth
        Seq(
          GemstoneName.findFirstIn(line).map(name => s"Gemstone environment variable $name"),
          GemstoneInclude.findFirstIn(line).map(_ => "Gemstone header"),
          GemstonePackage.findFirstIn(line).map(_ => "Gemstone package"),
          ProjectParent.findFirstIn(line).map(_ => "path above a project root"),
          ListParent.findAllMatchIn(line).find(found => escapes(found.group(1))).map(_ => "path outside conformance"),
          Option.when(source)(IncludeParent.findAllMatchIn(line).find(found => escapes(found.group(1)))).flatten
            .map(_ => "include outside conformance")
        ).flatten.map(problem => s"${root.relativize(path)}:${index + 1}: $problem")
      }
    }
    if (findings.nonEmpty) throw MessageOnlyException(findings.mkString("conformance must stand alone:\n", "\n", ""))
  }
}
