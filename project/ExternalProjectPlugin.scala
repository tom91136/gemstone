import java.nio.file.Path
import sbt.*
import sbt.Keys.*

object ExternalProjectPlugin extends AutoPlugin {
  object autoImport {
    val build  = inputKey[Path]("Build the external project.")
    val format = taskKey[Unit]("Format the external project's sources.")
    val lint   = taskKey[Unit]("Check the external project's sources.")
  }

  override def requires: Plugins                         = plugins.CorePlugin
  override def trigger: PluginTrigger                    = noTrigger
  override def projectConfigurations: Seq[Configuration] = Seq(Compile, Test)

  override def projectSettings: Seq[Def.Setting[?]] = Seq(
    Compile / managedSources := Nil,
    Test / managedSources    := Nil
  )
}
