import sbt.*
import sbt.Keys.*
import sbt.protocol.testing.TestResult

/** Runs every test of each project in turn, then fails naming each project that failed. */
object SemanticsChecks {
  def command(name: String, projects: Seq[String]): Command = Command.command(name) { original =>
    val (state, failed) = projects.foldLeft((original, Vector.empty[String])) { case ((state, failed), project) =>
      try {
        val (next, result) = Project.extract(state).runInputTask(LocalProject(project) / Test / testOnly, " *", state)
        (next, if (result == TestResult.Passed) failed else failed :+ project)
      } catch { case _: Incomplete => (state, failed :+ project) }
    }
    if (failed.isEmpty) state
    else {
      state.log.error(s"$name failed in ${failed.mkString(", ")}")
      state.fail
    }
  }
}
