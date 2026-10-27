package net.kurobako.gemstone.codegen

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import scala.jdk.CollectionConverters.*

/** The tables and listings of the pinned PTX document that type operands. */
object PTXReferenceTables {

  /** A table whose rows and columns are labelled by type names or classes, without leading dots. */
  case class Matrix(columns: Vector[String], rows: Vector[(label: String, cells: Vector[String])]) {
    def apply(row: String, column: String): Option[String] =
      for {
        cells <- rows.collectFirst { case (`row`, cells) => cells }
        index = columns.indexOf(column)
        cell <- cells.lift(index).filter(_ => index >= 0)
      } yield cell
  }

  case class Packed(name: String, elements: Int, element: String, declared: Vector[String])
  case class SpecialRegister(name: String, tpe: String, vector: Boolean, section: String)

  case class Tables(
      fundamental: Vector[(basic: String, types: Vector[String])],
      packed: Vector[Packed],
      agreement: Matrix,
      source: Matrix,
      destination: Matrix,
      identifiers: Vector[String],
      special: Vector[SpecialRegister]
  )

  private val SpecialDeclaration = raw"\.sreg\s+(\.v4\s+)?\.([a-z0-9]+)\s+([^;/]+)".r
  private val Counted            = "(.*)<([0-9]+)>(.*)".r
  private val Or                 = " or ".r

  def read(document: Document): Tables = Tables(
    fundamental = fundamental(table(document, "Fundamental Type Specifiers")),
    packed = packed(table(document, "Operand types for packed floating point")),
    agreement = matrix(table(document, "Type Checking Rules")),
    source = matrix(table(document, "Relaxed Type-checking Rules for Source")),
    destination = matrix(table(document, "Relaxed Type-checking Rules for Destination")),
    identifiers = identifiers(table(document, "Predefined Identifiers")),
    special = specialRegisters(document)
  )

  /** The text of the section with `id`, whitespace normalised; subsections are included. */
  def sectionText(document: Document, id: String): Option[String] =
    Option(document.getElementById(id)).map(element => Text.normaliseWhitespace(element.wholeText()))

  private def table(document: Document, caption: String): Vector[Vector[String]] =
    document.select("table").asScala.toVector.find { element =>
      Option(element.selectFirst("caption .caption-text")).exists(_.text().contains(caption))
    }.map(grid(_)).getOrElse(Vector.empty)

  /** Cells of `table` with row and column spans repeated into every position they cover. */
  private def grid(table: Element): Vector[Vector[String]] = {
    val rows  = table.select("tr").asScala.toVector
    val cells = rows.zipWithIndex.foldLeft(Map.empty[(Int, Int), String]) { case (placed, (row, y)) =>
      row.select("th,td").asScala.foldLeft((placed, 0)) { case ((occupied, from), cell) =>
        val x       = Iterator.from(from).find(column => !occupied.contains((y, column))).getOrElse(from)
        val width   = cell.attr("colspan").toIntOption.getOrElse(1)
        val height  = cell.attr("rowspan").toIntOption.getOrElse(1)
        val text    = Text.normaliseWhitespace(cell.text())
        val covered = for {
          dy <- 0 until height
          dx <- 0 until width
        } yield (y + dy, x + dx) -> text
        (occupied ++ covered, x + width)
      }._1
    }
    val columns = cells.keys.map(_._2).maxOption.fold(0)(_ + 1)
    Vector.tabulate(rows.size)(y => Vector.tabulate(columns)(x => cells.getOrElse((y, x), "")))
  }

  private def bare(value: String): String = value.trim.stripPrefix(".")

  private def fundamental(grid: Vector[Vector[String]]): Vector[(basic: String, types: Vector[String])] =
    grid.drop(1).filter(_.size >= 2).map(row => (basic = row(0), types = row(1).split(',').toVector.map(bare(_))))

  private def packed(grid: Vector[Vector[String]]): Vector[Packed] =
    grid.drop(1).filter(_.size >= 4).map { row =>
      val elements = row(1) match {
        case "Two"  => 2
        case "Four" => 4
        case other  => throw IllegalStateException(s"unknown packed element count $other")
      }
      Packed(bare(row(0)), elements, bare(row(2)), Or.split(row(3)).toVector.map(bare(_)))
    }

  private def matrix(grid: Vector[Vector[String]]): Matrix = {
    val header = grid.indexWhere(_.lift(1).exists(_.nonEmpty)) - 1
    if (header < 0) Matrix(Vector.empty, Vector.empty)
    else
      Matrix(
        grid(header).drop(2).map(bare(_)),
        grid.drop(header + 1).filter(row => row.size > 2 && row(0) != "Notes").map(row =>
          (label = bare(row(1)), cells = row.drop(2))
        )
      )
  }

  private def identifiers(grid: Vector[Vector[String]]): Vector[String] = grid.flatten.filter(_.nonEmpty)

  private def specialRegisters(document: Document): Vector[SpecialRegister] =
    document.select("section[id^=special-registers-]").asScala.toVector.flatMap { section =>
      val listing = Option(section.selectFirst("pre")).map(_.wholeText()).getOrElse("")
      SpecialDeclaration.findAllMatchIn(listing).toVector.flatMap { found =>
        val names = found.group(3).split(',').toVector.flatMap(name =>
          Option(name.trim).filter(_.nonEmpty).toVector.flatMap(expand(_))
        )
        names.map(SpecialRegister(_, found.group(2), Option(found.group(1)).nonEmpty, section.id()))
      }
    }

  private def expand(name: String): Vector[String] = name match {
    case Counted(prefix, count, suffix) => Vector.tabulate(count.toInt)(index => s"$prefix$index$suffix")
    case other                          => Vector(other)
  }
}
