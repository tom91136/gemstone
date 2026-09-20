package net.kurobako.gemstone.codegen

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

private[codegen] final class ScalaPackageVersionConstraint
private[codegen] object ScalaPackageVersionConstraint {
  given RuntimeConstraint[String, ScalaPackageVersionConstraint] = RuntimeConstraint(
    value =>
      value.refineOption[MinLength[4]].exists {
        case s"v$numbers" => numbers.split('_').forall(part => part.nonEmpty && part.forall(_.isDigit))
        case _            => false
      },
    "Should be a version package such as v1_6"
  )
}

private[codegen] type ScalaPackageVersion = ScalaPackageVersion.T
private[codegen] object ScalaPackageVersion extends RefinedType[String, ScalaPackageVersionConstraint]

private[gemstone] object Identifiers {
  def isC(value: String): Boolean =
    value.headOption.exists(character => character.isLetter || character == '_') &&
      value.tail.forall(character => character.isLetterOrDigit || character == '_')

  def startsWithOneOf(value: String, prefixes: String*): Boolean =
    prefixes.exists(value.startsWith(_)) && value.forall(character => character.isLetterOrDigit || character == '_')

  def isLowerSnake(value: String, prefix: String): Boolean =
    value.startsWith(prefix) && value.length > prefix.length &&
      value.drop(prefix.length).forall(character => character.isLower || character.isDigit || character == '_')

  def isUpperCamelAfter(value: String, prefix: String): Boolean =
    value.startsWith(prefix) && value.drop(prefix.length).headOption.exists(_.isUpper) &&
      value.drop(prefix.length).forall(character => character.isLetterOrDigit || character == '_')

  def sanitized(value: String): String = {
    val result = StringBuilder()
    value.foreach { character =>
      if (asciiLetterOrDigit(character)) {
        val _ = result.append(character)
      } else if (result.lastOption.forall(_ != '_')) {
        val _ = result.append('_')
      }
    }
    result.result().stripSuffix("_")
  }

  private def asciiLetterOrDigit(character: Char): Boolean =
    character >= 'A' && character <= 'Z' || character >= 'a' && character <= 'z' ||
      character >= '0' && character <= '9'
}
