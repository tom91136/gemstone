package net.kurobako.gemstone.codegen

import io.github.iltotore.iron.*

private[codegen] type ScalaPackageVersion = ScalaPackageVersion.T
private[codegen] object ScalaPackageVersion extends RefinedType[String, ScalaPackageVersionConstraint]
