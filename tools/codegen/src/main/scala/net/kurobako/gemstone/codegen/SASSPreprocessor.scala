package net.kurobako.gemstone.codegen

import scala.annotation.tailrec

/** Resolves the small conditional-preprocessing language used by legacy SASS tables. */
object SASSPreprocessor {
  case class ParseError(message: String, offset: Int)

  private case class Branch(parentActive: Boolean, condition: Boolean, sawElse: Boolean)

  /** Selects the branches active without ARCH_SEMANTICS and blanks directives and inactive lines without shifting
    * source offsets.
    */
  def preprocess(source: String): Either[ParseError, String] = {
    val lines = source.split("\n", -1).toVector

    def blank(line: String): String = line.map {
      case '\r' => '\r'
      case _    => ' '
    }

    @tailrec
    def loop(
        index: Int,
        offset: Int,
        branches: Vector[Branch],
        active: Boolean,
        output: Vector[String]
    ): Either[ParseError, String] = lines.lift(index) match {
      case None if branches.nonEmpty => Left(ParseError("unterminated ##IF", source.length))
      case None                      => Right(output.mkString)
      case Some(line)                =>
        val hasNewline = index < lines.length - 1
        val newline    = if (hasNewline) "\n" else ""
        val trimmed    = line.trim
        val trimOffset = offset + line.indexOf(trimmed)
        val lineResult = if (!trimmed.startsWith("##")) {
          Right((if (active) line else blank(line), branches, active))
        } else trimmed match {
          case "##IF DEFINED(ARCH_SEMANTICS)" =>
            Right((blank(line), branches :+ Branch(active, false, false), false))
          case "##IF !DEFINED(ARCH_SEMANTICS)" =>
            Right((blank(line), branches :+ Branch(active, true, false), active))
          case "##ELSE" => branches.lastOption match {
              case None                           => Left(ParseError("unmatched ##ELSE", trimOffset))
              case Some(branch) if branch.sawElse => Left(ParseError("duplicate ##ELSE", trimOffset))
              case Some(branch)                   =>
                val updated = branches.updated(branches.length - 1, branch.copy(sawElse = true))
                Right((blank(line), updated, branch.parentActive && !branch.condition))
            }
          case "##ENDIF" => branches.lastOption match {
              case None         => Left(ParseError("unmatched ##ENDIF", trimOffset))
              case Some(branch) => Right((blank(line), branches.dropRight(1), branch.parentActive))
            }
          case _ => Left(ParseError(s"unsupported preprocessing directive: $trimmed", trimOffset))
        }
        lineResult match {
          case Left(error)                                 => Left(error)
          case Right((rendered, nextBranches, nextActive)) =>
            loop(
              index + 1,
              offset + line.length + (if (hasNewline) 1 else 0),
              nextBranches,
              nextActive,
              output :+ (rendered + newline)
            )
        }
    }

    loop(0, 0, Vector.empty, true, Vector.empty)
  }
}
