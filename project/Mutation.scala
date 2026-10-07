import Paths.*
import sbt.*
import sbt.Keys.*
import stryker4s.sbt.Stryker4sPlugin.autoImport.*

object Mutation {
  val mutationCheck = taskKey[Unit]("Validate mutation source patterns and test filters.")

  val settings: Seq[Def.Setting[?]] = Seq(
    mutationCheck := Def.uncached {
      val root            = strykerBaseDir.value.toPath.absolute
      val compiledSources = (Compile / sources).value.map(file =>
        root.relativize(file.toPath.absolute)
      )
      val suites   = (Test / definedTests).value.map(_.name)
      val patterns = strykerMutate.value.filterNot(_.startsWith("!"))
      require(patterns.nonEmpty, "mutation campaign must explicitly select source files")
      patterns.foreach { pattern =>
        val matcher = root.getFileSystem.getPathMatcher(s"glob:$pattern")
        require(
          compiledSources.exists(matcher.matches),
          s"mutation source pattern matches no compiled sources: $pattern"
        )
      }
      strykerTestFilter.value.foreach { pattern =>
        require(suites.exists(GlobFilter(pattern).accept), s"mutation test filter matches no suites: $pattern")
      }
    },
    stryker := stryker.dependsOn(mutationCheck).evaluated
  )
}
