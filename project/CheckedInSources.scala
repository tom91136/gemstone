import Paths.*
import java.nio.file.Path
import sbt.*
import sbt.Keys.*

object CheckedInSources {
  val checkedInGeneratedSourceDirectory   = settingKey[Path]("Checked-in generated source root.")
  val checkedInGeneratedResourceDirectory = settingKey[Path]("Checked-in generated resource root.")

  val settings: Seq[Def.Setting[?]] = Seq(Compile -> "main", Test -> "test").flatMap { (config, name) =>
    inConfig(config)(Seq(
      checkedInGeneratedSourceDirectory :=
        projectMatrixBaseDirectory.value.toPath.absolute / s"src/generated/$name/scala",
      checkedInGeneratedResourceDirectory :=
        projectMatrixBaseDirectory.value.toPath.absolute / s"src/generated/$name/resources",
      unmanagedSourceDirectories += checkedInGeneratedSourceDirectory.value.toFile,
      unmanagedResourceDirectories += checkedInGeneratedResourceDirectory.value.toFile
    ))
  }
}
