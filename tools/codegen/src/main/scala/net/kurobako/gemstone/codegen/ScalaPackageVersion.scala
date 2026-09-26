package net.kurobako.gemstone.codegen

opaque type ScalaPackageVersion = String

object ScalaPackageVersion {
  def either(value: String): Either[String, ScalaPackageVersion] =
    if (value.length >= 4 && value.startsWith("v")) {
      val numbers = value.drop(1)
      if (numbers.split('_').forall(part => part.nonEmpty && part.forall(_.isDigit))) Right(value)
      else Left("Should be a version package such as v1_6")
    } else Left("Should be a version package such as v1_6")

  def unsafe(value: String): ScalaPackageVersion =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  extension (value: ScalaPackageVersion) {
    def raw: String = value
  }
}
