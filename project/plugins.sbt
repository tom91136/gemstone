addSbtPlugin("com.timushev.sbt" % "sbt-updates"      % "0.7.0")
addSbtPlugin("ch.epfl.scala"    % "sbt-scalafix"     % "0.14.7")
addSbtPlugin("org.scalameta"    % "sbt-scalafmt"     % "2.6.2")
addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.5.12")
addDependencyTreePlugin

libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-compress" % "1.28.0",
  "org.tukaani"        % "xz"               % "1.12"
)
