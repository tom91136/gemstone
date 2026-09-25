import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.Locale
import scala.util.control.NonFatal
import scala.util.matching.Regex

/** Line-oriented text rules and the shared scan loop behind the codestyle checks. */
object Codestyle {
  case class Violation(path: Path, line: Int, message: String) {
    def render: String = s"$path:$line: $message"
  }

  type Rule = (Path, Vector[String]) => Seq[Violation]

  def scan(files: Seq[Path], rules: Seq[Rule], root: Path): Seq[Violation] =
    files.flatMap { file =>
      val display = if (file.startsWith(root)) root.relativize(file) else file
      lines(file).toSeq.flatMap(content => rules.flatMap(rule => rule(display, content)))
    }

  def forbidden(pattern: Regex, describe: String => String): Rule =
    (path, lines) =>
      lines.iterator.zipWithIndex.flatMap { case (line, index) =>
        pattern.findFirstIn(line).iterator.map(value => Violation(path, index + 1, describe(value)))
      }.toSeq

  def prohibitedTerm(pattern: Regex, message: String): Rule =
    (path, lines) =>
      lines.iterator.zipWithIndex.collect {
        case (line, index) if pattern.findFirstIn(line).nonEmpty => Violation(path, index + 1, message)
      }.toSeq

  val testCaseSpacing: Rule =
    (path, lines) =>
      lines.iterator.zipWithIndex.collect {
        case (line, index)
            if index > 0 && testCaseMacro.findPrefixOf(line).nonEmpty && lines(index - 1).trim.nonEmpty =>
          Violation(path, index + 1, "Catch test cases must be preceded by a blank line")
      }.toSeq

  val includeBlockSpacing: Rule =
    (path, lines) =>
      lines.iterator.zipWithIndex.collect {
        case (line, index)
            if includeDirective.findPrefixOf(line).nonEmpty && includeBlockNeedsSeparator(lines, index) =>
          Violation(path, index + 2, "include blocks must be followed by a blank line")
      }.toSeq

  def wordVariants(stem: String): Regex = {
    val lower = raw"\b${stem}s?\b"
    val camel = s"${stem.capitalize}s?(?=\\z|[^a-z])"
    val upper = s"${stem.toUpperCase(Locale.ROOT)}s?(?=\\z|[^A-Z])"
    s"(?:$lower|$camel|$upper)".r
  }

  private val testCaseMacro    = raw"^[A-Z_]*TEST_CASE[A-Z_]*\s*\(".r
  private val includeDirective = raw"^\s*#\s*include\b".r

  private def includeBlockNeedsSeparator(lines: Vector[String], includeIndex: Int): Boolean = {
    var index = includeIndex + 1
    while (index < lines.size && lines(index).trim.nonEmpty) {
      if (includeDirective.findPrefixOf(lines(index)).nonEmpty) return false
      if (lines(index).trim.matches("#\\s*(else|elif)\\b.*")) return false
      if (!lines(index).trim.startsWith("#")) return true
      index += 1
    }
    false
  }

  private def lines(file: Path): Option[Vector[String]] =
    try Some(new String(Files.readAllBytes(file), StandardCharsets.UTF_8).linesIterator.toVector)
    catch { case NonFatal(_) => None }
}
