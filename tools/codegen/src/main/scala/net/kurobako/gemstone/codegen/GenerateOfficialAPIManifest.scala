package net.kurobako.gemstone.codegen

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.pickler.given
import scala.collection.mutable
import scala.sys.process.Process
import scala.sys.process.ProcessLogger
import upickle.default.ReadWriter
import upickle.default.read
import upickle.default.write

/** Probes the pinned vendor headers of every [[NativeAPIs.Surface]] with Clang. */
object GenerateOfficialAPIManifest {
  object Function {
    def merge(functions: Vector[Function]): Vector[Function] = {
      val conflicts = functions.groupBy(_.name).collect {
        case (name, declarations)
            if declarations.map(f => (f.returnType, f.functionType, f.parameters)).distinct.size > 1 =>
          name.raw
      }
      if (conflicts.nonEmpty)
        throw IllegalArgumentException(
          s"conflicting function declarations: ${conflicts.toVector.sorted.mkString(", ")}"
        )
      functions.distinctBy(_.name)
    }
  }
  case class Function(
      name: NonEmpty[String],
      returnType: NonEmpty[String],
      functionType: NonEmpty[String],
      parameters: Vector[NonEmpty[String]]
  ) derives ReadWriter
  case class DispatchField(name: NonEmpty[String], cType: NonEmpty[String]) derives ReadWriter
  case class DispatchTable(name: NonEmpty[String], fields: Vector[DispatchField]) derives ReadWriter

  /** The ascending driver versions introducing each revision of `name`. */
  case class EntryPoint(name: NonEmpty[String], versions: Vector[Int]) derives ReadWriter
  case class Surface(
      name: NonEmpty[String],
      functions: Vector[Function],
      dispatchTable: Option[DispatchTable],
      entryPoints: Vector[EntryPoint] = Vector.empty
  ) derives ReadWriter
  case class Manifest(format: Int, surfaces: Vector[Surface]) derives ReadWriter

  private case class Request(clang: String, references: Path, output: Path)
  private case class TypeInfo(qualType: String) derives ReadWriter
  private case class Node(
      kind: Option[String] = None,
      name: Option[String] = None,
      `type`: Option[TypeInfo] = None,
      isImplicit: Option[Boolean] = None,
      storageClass: Option[String] = None,
      inner: Option[Vector[Node]] = None
  ) derives ReadWriter {
    def children: Vector[Node]    = inner.getOrElse(Vector.empty)
    def descendants: Vector[Node] = this +: children.flatMap(_.descendants)
    def explicit: Boolean         = !isImplicit.getOrElse(false)
  }

  private val command = Command("GenerateOfficialAPIManifest", "Generate the official API manifest")((
    Opts.option[String]("clang", help = "Clang executable"),
    CodegenCommand.path("references"),
    CodegenCommand.path("manifest")
  ).mapN(Request(_, _, _)))

  def main(arguments: Array[String]): Unit = CodegenCommand.run(command, arguments) { request =>
    val surfaces = NativeAPIs.platforms.flatMap(_.surfaces).map(probe(_, request.references, request.clang))
    GeneratedFiles.write(request.output, write(Manifest(format = 6, surfaces), indent = 2) + "\n")
    surfaces.foreach(surface => Console.out.println(s"probed ${surface.functions.size} ${surface.name} declarations"))
  }

  private def probe(surface: NativeAPIs.Surface, references: Path, clang: String): Surface = {
    val command = Vector(clang, "-x", "c", "-Xclang", "-ast-dump=json", "-fsyntax-only", "-Wno-visibility") ++
      Vector(s"-I${surface.include(references)}") ++
      surface.defines.map(define => s"-D$define") ++
      surface.headers.flatMap(header => Vector("-include", header)) ++ Vector("-")
    val diagnostics = mutable.StringBuilder()
    val output      = try
      Process(command).#<(ByteArrayInputStream(Array.emptyByteArray)).!!(
        ProcessLogger { line =>
          val _ = diagnostics.append(line).append('\n')
        }
      )
    catch {
      case error: RuntimeException =>
        throw IllegalStateException(s"${surface.name}: clang failed:\n${diagnostics.result()}", error)
    }
    val nodes     = read[Node](output).descendants
    val functions = nodes.collect {
      case node @ Node(Some("FunctionDecl"), Some(name), Some(TypeInfo(functionType)), _, storageClass, _)
          if node.explicit && !storageClass.contains("static") && surface.accepts(name) =>
        val boundary = functionType.indexOf('(')
        if (boundary <= 0) throw IllegalArgumentException(s"cannot split C function type: $functionType")
        Function(
          nonEmpty(name, "function"),
          nonEmpty(functionType.substring(0, boundary).trim, "function return type"),
          nonEmpty(functionType, "function type"),
          node.children.collect { case Node(Some("ParmVarDecl"), _, Some(TypeInfo(cType)), _, _, _) =>
            nonEmpty(cType, "parameter type")
          }
        )
    }
    val conflicts = functions.groupBy(_.name).collect {
      case (name, entries) if entries.map(_.functionType).distinct.size > 1 =>
        s"$name: ${entries.map(_.functionType).distinct.mkString(" versus ")}"
    }.toVector
    if (conflicts.nonEmpty)
      throw IllegalStateException(s"${surface.name}: conflicting declarations: ${conflicts.mkString("; ")}")
    val dispatchTable = surface.dispatchTable.map { tableName =>
      nodes.filter(node =>
        node.kind.contains("RecordDecl") && node.name.contains(tableName) && node.explicit && node.children.nonEmpty
      ) match {
        case Vector(record) =>
          val fields = record.children.collect {
            case field @ Node(Some("FieldDecl"), Some(name), Some(TypeInfo(cType)), _, _, _) if field.explicit =>
              DispatchField(nonEmpty(name, "dispatch field"), nonEmpty(cType, "dispatch field type"))
          }
          DispatchTable(nonEmpty(tableName, "dispatch table"), fields.distinctBy(_.name))
        case records =>
          throw IllegalStateException(
            s"${surface.name}: expected one complete $tableName record, found ${records.size}"
          )
      }
    }
    Surface(
      nonEmpty(surface.name, "surface"),
      functions.distinctBy(_.name).sortBy(_.name.raw),
      dispatchTable,
      surface.entryPointHeader.fold(Vector.empty)(header =>
        entryPoints(surface, Files.readString(surface.include(references).resolve(header)))
      )
    )
  }

  private val PfnName = "\\bPFN_([A-Za-z0-9_]+?)_v([0-9]+)\\b".r

  // Read as text: the oldest revisions are only declared under the vendor's internal-build guard. Per-thread-stream
  // typedefs end in a suffix and name a stream variant of a revision rather than a new one.
  private def entryPoints(surface: NativeAPIs.Surface, header: String): Vector[EntryPoint] =
    PfnName.findAllMatchIn(header).map(found => found.group(1) -> found.group(2).toInt)
      .filter((name, _) => surface.accepts(name)).toVector.groupMap(_._1)(_._2).toVector.sortBy(_._1)
      .map((name, versions) => EntryPoint(nonEmpty(name, "entry point"), versions.distinct.sorted))

  private def nonEmpty(value: String, description: String): NonEmpty[String] =
    value.refine[NonEmpty[String]].fold(
      message => throw IllegalArgumentException(s"invalid $description '$value': $message"),
      identity
    )
}
