import Paths.*
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import sbt.*
import sbt.Keys.*

object ResourceSync {
  val settings: Seq[Def.Setting[?]] = Seq(Compile, Test).flatMap(config =>
    inConfig(config)(Seq(
      copyResources := Def.uncached {
        val _        = resourcesVF.value
        val mappings = Defaults.copyResourcesTask.value
        mappings.foreach { (source, target) =>
          val from = source.toPath
          val to   = target.toPath
          if (from != to && (!to.isFile || Files.mismatch(from, to) != -1L)) {
            to.parent.foreach(_.createDirectories())
            val _ = Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING)
          }
        }
        mappings
      },
      products := products.dependsOn(copyResources).value
    ))
  )
}
