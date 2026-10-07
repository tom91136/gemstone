import sbt.*
import sbt.Keys.*

object RunMain {
  def main(
      project: ProjectReference,
      mainClass: String,
      arguments: Def.Initialize[Task[Seq[String]]]
  ): Def.Initialize[Task[Unit]] = Def.task {
    Def.uncached {
      val converter = fileConverter.value
      val classpath = (project / Compile / fullClasspath).value.map(entry => converter.toPath(entry.data))
      (project / Compile / run / runner).value.run(mainClass, classpath, arguments.value, streams.value.log).get
    }
  }
}
