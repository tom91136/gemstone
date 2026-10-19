addSbtPlugin("ch.epfl.scala"      % "sbt-scalafix"     % "0.14.9")
addSbtPlugin("com.typesafe"       % "sbt-mima-plugin"  % "1.2.1")
addSbtPlugin("org.scalameta"      % "sbt-scalafmt"     % "2.6.2")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"      % "1.22.0")
addSbtPlugin("org.scala-native"   % "sbt-scala-native" % "0.5.12")
addSbtPlugin("io.stryker-mutator" % "sbt-stryker4s"    % "1.1.1")

// sbt-baumkuchen is unpublished, so a build without its local snapshot, such as CI, runs without its checks
if ((file(sys.props("user.home")) / ".ivy2/local/net.kurobako/sbt-baumkuchen_sbt2_3").isDirectory)
  Seq(addSbtPlugin("net.kurobako" % "sbt-baumkuchen" % "0.1.0-SNAPSHOT"))
else Nil

val jacocoVersion = "0.8.15"

libraryDependencies ++= Seq(
  "org.jacoco"  % "org.jacoco.report" % jacocoVersion,
  ("org.jacoco" % "org.jacoco.agent"  % jacocoVersion).classifier("runtime")
)
