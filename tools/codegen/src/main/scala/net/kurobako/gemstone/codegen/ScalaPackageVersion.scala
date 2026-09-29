package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.constraint.*

type ScalaPackageVersion = ScalaPackageVersion.Type
object ScalaPackageVersion extends Newtype[String] {
  override protected def validate(value: String): Unit =
    require(
      value.length >= 4 && value.startsWith("v") &&
        value.drop(1).split('_').forall(part => part.nonEmpty && part.forall(_.isDigit)),
      "Should be a version package such as v1_6"
    )
}
