package net.kurobako.gemstone.codegen

import io.github.iltotore.iron.*

type ScalaPackageVersion = ScalaPackageVersion.T
object ScalaPackageVersion extends RefinedType[String, ScalaPackageVersionConstraint]
