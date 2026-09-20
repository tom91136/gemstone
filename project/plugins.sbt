// Meta-build bootstrap only. sbt loads this file before build.sbt, so plugin and
// build-helper dependencies cannot use the product-version catalog there.
addSbtPlugin("com.timushev.sbt" % "sbt-updates"      % "0.7.0")
addSbtPlugin("ch.epfl.scala"    % "sbt-scalafix"     % "0.14.9")
addSbtPlugin("org.scalameta"    % "sbt-scalafmt"     % "2.6.2")
addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.5.12")
addDependencyTreePlugin

libraryDependencies ++= Seq(
  "com.google.guava"               % "guava"            % "33.7.1-jre",
  "com.softwaremill.sttp.client4" %% "core"             % "4.0.26",
  "org.apache.commons"             % "commons-compress" % "1.28.0",
  "org.tukaani"                    % "xz"               % "1.12"
)
