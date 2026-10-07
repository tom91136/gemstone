addSbtPlugin("ch.epfl.scala"      % "sbt-scalafix"     % "0.14.9")
addSbtPlugin("org.scalameta"      % "sbt-scalafmt"     % "2.6.2")
addSbtPlugin("org.scala-native"   % "sbt-scala-native" % "0.5.12")
addSbtPlugin("io.stryker-mutator" % "sbt-stryker4s"    % "1.1.1")

val jacocoVersion = "0.8.14"

libraryDependencies ++= Seq(
  "org.jacoco"  % "org.jacoco.report" % jacocoVersion,
  ("org.jacoco" % "org.jacoco.agent"  % jacocoVersion).classifier("runtime")
)
