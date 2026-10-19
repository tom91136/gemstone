import java.nio.file.Files
import java.nio.file.Path
import sbt.complete.DefaultParsers.*
import sbt.complete.Parser
import scala.jdk.CollectionConverters.*
import scala.util.Using

/** Completing input task arguments: `--name value` options checked against their choices, flags, and free tokens. */
object ArgumentParsers {
  sealed trait Item
  final case class Valued(name: String, value: String) extends Item
  final case class Flag(name: String)                  extends Item
  final case class Free(value: String)                 extends Item

  final case class Arguments(items: Vector[Item]) {
    def option(name: String): Option[String] = items.collect { case Valued(`name`, value) => value }.lastOption
    def flag(name: String): Boolean          = items.contains(Flag(name))
    def free: Vector[String]                 = items.collect { case Free(value) => value }
    def tokens(excluding: Set[String]): Vector[String] = items.flatMap {
      case Valued(name, value) if !excluding(name) => Vector(name, value)
      case Flag(name) if !excluding(name)          => Vector(name)
      case Free(value)                             => Vector(value)
      case _                                       => Vector.empty
    }
  }

  /** `options` maps each option name to a label and its accepted values; `free` only seeds completion. */
  def arguments(
      options: Map[String, (String, Seq[String])],
      flags: Seq[String],
      free: Seq[String]
  ): Parser[Arguments] = {
    val names  = options.keySet ++ flags
    val valued = options.toSeq.sortBy(_._1).map { case (name, (label, values)) =>
      (literal(name) ~> Space ~> token(NotSpace.examples(values*), label).filter(
        values.contains,
        value => s"unknown $label '$value'; expected one of ${values.mkString(", ")}"
      )).map(value => Valued(name, value): Item)
    }
    val flagged = flags.map(name => literal(name).map(_ => Flag(name): Item))
    val other   = NotSpace.filter(!names(_), name => s"$name needs a value").map(value => Free(value): Item)
    val choices = (names.toSeq.sorted ++ free).distinct
    (token(Space) ~> token((valued ++ flagged).foldRight(other)(_ | _)).examples(choices*)).*.map(items =>
      Arguments(items.toVector)
    )
  }

  /** Every prefix of each `/`-separated path, so completion offers `a`, `a/b` and `a/b/c`. */
  def prefixes(paths: Seq[String]): Seq[String] =
    paths.flatMap(path => path.split('/').inits.toSeq.init.map(_.mkString("/"))).distinct.sorted

  /** Names of the files in `directory` ending in `suffix`, without it. */
  def fileNames(directory: Path, suffix: String): Seq[String] =
    if (!Files.isDirectory(directory)) Seq.empty
    else
      Using.resource(Files.list(directory))(
        _.iterator.asScala.map(
          _.getFileName.toString
        ).filter(_.endsWith(suffix)).map(_.stripSuffix(suffix)).toVector.sorted
      )
}
