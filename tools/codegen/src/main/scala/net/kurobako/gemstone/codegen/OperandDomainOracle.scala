package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.string.*
import scala.jdk.CollectionConverters.*
import scala.util.Using

/** Operand spellings the LLVM MC oracle accepts or rejects as unavailable, per target. */
object OperandDomainOracle {

  /** A rejected observation carries the register the diagnostic names in `message`. */
  case class Observation(target: String, assembly: String, accepted: Boolean, message: String) {
    lazy val tokens: Set[String] = Identifier.findAllIn(assembly.toLowerCaseRoot).toSet
  }

  private val Identifier  = "[a-z_][a-z0-9_]*".r
  private val Target      = raw"// TARGET: (\S+)".r
  private val Unavailable = raw"// ERROR: (\S+) register not available on this GPU".r
  private val Accepted    = "// ENCODING: .*".r
  private val Instruction = raw"[a-z][a-z0-9_]*\s.*".r

  def read(directory: Path): Vector[Observation] = {
    val files =
      Using.resource(Files.list(directory))(_.toList.asScala.toVector.filter(_.toString.endsWith(".s")).sorted)
    files.flatMap { file =>
      val lines  = Files.readAllLines(file, StandardCharsets.UTF_8).asScala.toVector
      val target = lines.collectFirst { case Target(name) => name }
        .getOrElse(throw IllegalArgumentException(s"oracle file without target: $file"))
      lines.zip(lines.drop(1)).collect {
        case (assembly @ Instruction(), Accepted()) => Observation(target, assembly.trim, accepted = true, "")
        case (assembly @ Instruction(), Unavailable(register)) =>
          Observation(target, assembly.trim, accepted = false, register.toLowerCaseRoot)
      }
    }
  }
}
