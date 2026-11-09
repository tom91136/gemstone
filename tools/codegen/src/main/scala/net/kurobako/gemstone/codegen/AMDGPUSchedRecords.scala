package net.kurobako.gemstone.codegen

import java.nio.file.Path
import scala.annotation.tailrec
import scala.collection.mutable
import scala.io.Codec
import scala.io.Source
import scala.util.Try
import scala.util.Using

/** The scheduling-relevant slice of `llvm-tblgen --print-records`, read line by line. */
object AMDGPUSchedRecords {
  case class Dump(instanceOf: Map[String, Vector[String]], records: Map[String, ujson.Obj]) {
    def classMembers(name: String): Vector[String] = instanceOf.getOrElse(name, Vector.empty)
  }

  private val Kept = Set(
    "Instruction",
    "InstRW",
    "Predicate",
    "Processor",
    "ProcResource",
    "SchedWrite",
    "SubtargetFeature",
    "WriteRes"
  )

  private val Fields = Set(
    "AsmString",
    "AssemblerCondDag",
    "AssemblerMatcherPredicate",
    "Features",
    "Implies",
    "Instrs",
    "Latency",
    "Name",
    "NumUnits",
    "OperandReadWrites",
    "Predicates",
    "ProcResources",
    "ReleaseAtCycles",
    "SchedModel",
    "SchedRW",
    "Size",
    "Unsupported",
    "WriteType",
    "isCodeGenOnly",
    "isPseudo"
  )

  private val Defs         = "------------- Defs -----------------"
  private val Superclasses = "!superclasses"

  def read(path: Path): Dump = Using.resource(Source.fromFile(path.toFile)(using Codec.UTF8)) { source =>
    val lines   = source.getLines().dropWhile(_ != Defs).drop(1)
    val records = mutable.HashMap.empty[String, ujson.Obj]
    val members = mutable.HashMap.empty[String, mutable.ArrayBuffer[String]]
    while (lines.hasNext) {
      val line = lines.next()
      if (line.startsWith("def ")) {
        val (name, supers) = header(line)
        val body           = Body()
        scan(lines, body)
        val kept = supers.filter(Kept)
        if (kept.nonEmpty) {
          val fields = body.statements.flatMap(field(name, _))
          records(name) = ujson.Obj.from(fields.prepended(Superclasses -> ujson.Arr.from(supers.map(ujson.Str(_)))))
          kept.foreach(group => members.getOrElseUpdate(group, mutable.ArrayBuffer.empty) += name)
        }
      }
    }
    Dump(members.view.mapValues(_.sorted.toVector).toMap, records.toMap)
  }

  private def header(line: String): (String, Vector[String]) = {
    val (head, comment) = line.span(_ != '\t')
    (head.stripPrefix("def ").stripSuffix(" {"), comment.stripPrefix("\t// ").split(' ').filter(_.nonEmpty).toVector)
  }

  @tailrec
  private def scan(lines: Iterator[String], body: Body): Unit =
    if (!body.feed(lines.next())) scan(lines, body)

  private def field(record: String, statement: String): Option[(String, ujson.Value)] = {
    val equals = statement.indexOf('=')
    require(equals >= 0, s"TableGen record $record statement has no value: ${statement.trim}")
    val name = statement.take(equals).trim.split("\\s+").last
    Option.when(Fields(name))(name -> parse(record, name, statement.drop(equals + 1)))
  }

  private def parse(record: String, field: String, text: String): ujson.Value =
    Try(Parser(text).parse()).fold(
      cause => throw IllegalArgumentException(s"TableGen record $record field $field: ${cause.getMessage}", cause),
      identity
    )

  /** Splits a record body into statements, tracking quotes, `[{ }]` code blocks and nesting. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class Body {
    val statements: mutable.ArrayBuffer[String] = mutable.ArrayBuffer.empty[String]
    private val text                            = mutable.StringBuilder()
    private var depth                           = 0
    private var quoted                          = false
    private var escaped                         = false
    private var code                            = false

    private def ends(char: Char): Boolean = text.nonEmpty && text.charAt(text.length - 1) == char

    def feed(line: String): Boolean = {
      var index  = 0
      var closed = false
      while (!closed && index < line.length) {
        val char = line.charAt(index)
        index += 1
        if (quoted) {
          if (escaped) escaped = false
          else if (char == '\\') escaped = true
          else if (char == '"') quoted = false
          text.append(char)
        } else if (code) {
          if (char == ']' && ends('}')) code = false
          text.append(char)
        } else
          char match {
            case '"' =>
              quoted = true
              text.append(char)
            case '{' if ends('[') =>
              depth -= 1
              code = true
              text.append(char)
            case '(' | '[' | '{' =>
              depth += 1
              text.append(char)
            case '}' if depth == 0 => closed = true
            case ')' | ']' | '}'   =>
              depth -= 1
              text.append(char)
            case ';' if depth == 0 =>
              statements.addOne(text.toString)
              text.clear()
            case _ =>
              text.append(char)
          }
      }
      if (!closed) text.append('\n')
      closed
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class Parser(text: String) {
    private var at = 0

    def parse(): ujson.Value = {
      val value = read()
      space()
      require(at == text.length, s"unparsed TableGen value tail: ${text.drop(at)}")
      value
    }

    private def read(): ujson.Value = {
      space()
      text.charAt(at) match {
        case '?' =>
          at += 1
          ujson.Null
        case '"'                                  => string()
        case '['                                  => list()
        case '('                                  => dag()
        case char if char == '-' || char.isDigit  => number()
        case char if char.isLetter || char == '_' => ujson.Obj("def" -> ujson.Str(identifier()))
        case char => throw IllegalArgumentException(s"unsupported TableGen value at $at: $char")
      }
    }

    private def space(): Unit = while (at < text.length && text.charAt(at).isWhitespace) at += 1

    private def identifier(): String = {
      val start = at
      while (at < text.length && (text.charAt(at).isLetterOrDigit || text.charAt(at) == '_')) at += 1
      text.substring(start, at)
    }

    private def string(): ujson.Value = {
      val end     = text.indexOf('"', at + 1)
      val content = text.substring(at + 1, end)
      require(!content.contains('\\'), s"escaped TableGen string is not supported: $content")
      at = end + 1
      ujson.Str(content)
    }

    private def number(): ujson.Value = {
      val start = at
      at += 1
      while (at < text.length && text.charAt(at).isDigit) at += 1
      ujson.Num(text.substring(start, at).toDouble)
    }

    private def list(): ujson.Value = {
      at += 1
      val items = mutable.ArrayBuffer.empty[ujson.Value]
      space()
      while (text.charAt(at) != ']') {
        items.append(read())
        space()
        if (text.charAt(at) == ',') at += 1
      }
      at += 1
      ujson.Arr(items)
    }

    private def dag(): ujson.Value = {
      at += 1
      space()
      val operator = identifier()
      val args     = mutable.ArrayBuffer.empty[ujson.Value]
      space()
      while (text.charAt(at) != ')') {
        if (text.charAt(at) == ',') at += 1
        else args.append(argument())
        space()
      }
      at += 1
      ujson.Obj(
        "kind"     -> ujson.Str("dag"),
        "operator" -> ujson.Obj("def" -> ujson.Str(operator)),
        "args"     -> ujson.Arr(args)
      )
    }

    private def argument(): ujson.Value = {
      val value = read()
      val name  =
        if (text.charAt(at) == ':') { at += 2; ujson.Str(identifier()) }
        else ujson.Null
      ujson.Arr(value, name)
    }
  }
}
