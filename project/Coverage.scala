import Paths.*
import java.io.File
import java.io.Reader
import java.io.StringReader
import java.nio.file.Files
import java.time.Instant
import org.jacoco.core.analysis.Analyzer
import org.jacoco.core.analysis.CoverageBuilder
import org.jacoco.core.tools.ExecFileLoader
import org.jacoco.report.FileMultiReportOutput
import org.jacoco.report.ISourceFileLocator
import org.jacoco.report.MultiReportVisitor
import org.jacoco.report.html.HTMLFormatter
import org.jacoco.report.xml.XMLFormatter
import sbt.*
import sbt.Keys.*
import scala.jdk.CollectionConverters.*
import scala.util.Using

/** Runs ordinary JVM tests with temporary agent settings and publishes only complete reports. */
object Coverage {
  case class Entry(project: String, measure: Boolean = true, tests: Boolean = true, generated: Seq[File] = Nil)
  case class Module(name: String, classes: File, sources: Seq[File], generated: Seq[File])

  private val agentOptions = settingKey[String]("Agent options for this coverage invocation.")
  private val module       = taskKey[Module]("Classes and sources for this coverage invocation.")

  def commands(full: String, focused: String, title: String, entries: Seq[Entry], includes: Seq[String], output: File)
      : Seq[Command] = {
    require(
      entries.nonEmpty && entries.map(_.project).distinct.size == entries.size,
      "duplicate or empty coverage inventory"
    )
    Seq(
      Command.command(full)(state => run(state, entries, None, title, includes, output / "full")),
      Command.args(focused, "<project> [test patterns]") { (state, arguments) =>
        require(arguments.nonEmpty, s"usage: $focused <project> [test patterns]")
        val selected = entries.find(entry => entry.project == arguments.head && entry.tests)
          .getOrElse(sys.error(s"unknown coverage test project: ${arguments.head}"))
        run(
          state,
          entries.filter(entry => entry.measure || entry == selected).map(entry =>
            entry.copy(tests = entry == selected)
          ),
          Some(arguments.tail.mkString(" ")),
          title,
          includes,
          output / "focused" / selected.project
        )
      }
    )
  }

  private def run(
      original: State,
      entries: Seq[Entry],
      filter: Option[String],
      title: String,
      includes: Seq[String],
      destination: File
  ): State = destination.toPath.parent.get.withTempDirectory(".coverage-") { directory =>
    val staging  = directory.toFile
    val agent    = new File(classOf[org.jacoco.agent.rt.RT].getProtectionDomain.getCodeSource.getLocation.toURI)
    val build    = Project.extract(original).currentRef.build
    val runners  = entries.filter(_.tests)
    val subjects = entries.filter(_.measure)
    val settings = entries.flatMap { entry =>
      val ref = ProjectRef(build, entry.project)
      Seq(ref / module := Def.uncached(Module(
        entry.project,
        (ref / Compile / classDirectory).value,
        (ref / Compile / sources).value,
        entry.generated ++ (ref / Compile / managedSourceDirectories).value
      ))) ++
        Option.when(entry.tests)(Seq(
          ref / Test / fork         := true,
          ref / Test / agentOptions :=
            s"-javaagent:$agent=destfile=${staging / s"${entry.project}.exec"},append=true,includes=${includes.mkString(":")}",
          ref / Test / javaOptions += (ref / Test / agentOptions).value
        )).toSeq.flatten
    }
    // Keep session customizations, and return the original state on completion. Failures likewise
    // leave the caller's state untouched: instrumentation never becomes a session setting.
    var state = Project.extract(original).appendWithSession(settings, original)
    subjects.foreach { entry =>
      state = Project.extract(state).runTask(ProjectRef(build, entry.project) / Compile / compile, state)._1
    }
    runners.foreach { entry =>
      val ref            = ProjectRef(build, entry.project)
      val (next, result) = filter match {
        case Some(patterns) if patterns.nonEmpty =>
          Project.extract(state).runInputTask(ref / Test / testOnly, s" $patterns", state)
        case _ => Project.extract(state).runTask(ref / Test / testFull, state)
      }
      require(result == sbt.protocol.testing.TestResult.Passed, s"coverage tests failed: ${entry.project}")
      state = next
    }
    val modules = subjects.map { entry =>
      val (next, result) = Project.extract(state).runTask(ProjectRef(build, entry.project) / module, state)
      state = next
      result
    }
    writeReport(title, modules, runners.map(entry => staging / s"${entry.project}.exec"), staging)
    IO.write(
      staging / "RUN.txt",
      s"Completed: ${Instant.now()}\nTests: ${runners.map(_.project).mkString(", ")}\nSelection: ${filter.filter(_.nonEmpty).getOrElse("all tests in selected projects")}\n"
    )
    IO.delete(destination)
    IO.move(staging, destination)
    original.log.info(s"Coverage: ${destination / "index.html"}")
    original
  }

  def writeReport(title: String, modules: Seq[Module], data: Seq[File], destination: File): Unit = {
    require(
      data.forall(_.isFile),
      s"missing execution data from coverage test runners: ${data.filterNot(_.isFile).mkString(", ")}"
    )
    require(modules.forall(_.classes.isDirectory), "missing compiled classes")
    val execution = new ExecFileLoader()
    data.foreach(execution.load(_))
    IO.createDirectory(destination)
    Using.resource(Files.newOutputStream((destination / "jacoco.xml").toPath)) { xml =>
      val visitor = new MultiReportVisitor(java.util.List.of(
        new HTMLFormatter().createVisitor(new FileMultiReportOutput(destination)),
        new XMLFormatter().createVisitor(xml)
      ))
      visitor.visitInfo(execution.getSessionInfoStore.getInfos, execution.getExecutionDataStore.getContents)
      val root = visitor.visitGroup(title)
      modules.foreach { module =>
        // Match debug source names against source paths, independently of Scala package syntax.
        // Nonstandard directory layouts can use a unique filename; ambiguous mappings fail visibly.
        def source(packageName: String, fileName: String): Option[File] = {
          val named      = module.sources.filter(_.getName == fileName)
          val suffix     = s"$packageName/$fileName"
          val matching   = named.filter(_.toPath.endsWith(java.nio.file.Path.of(suffix.stripPrefix("/"))))
          val candidates = if (matching.nonEmpty) matching else named
          require(candidates.size <= 1, s"ambiguous coverage source: ${module.name}: $suffix")
          candidates.headOption
        }
        val locator = new ISourceFileLocator {
          override def getTabWidth: Int                                             = 2
          override def getSourceFile(packageName: String, fileName: String): Reader =
            source(packageName, fileName).map(file => new StringReader(IO.read(file))).orNull
        }
        val classes = new CoverageBuilder()
        new Analyzer(execution.getExecutionDataStore, classes).analyzeAll(module.classes)
        require(classes.getNoMatchClasses.isEmpty, s"class files changed after coverage ran: ${module.name}")
        val group       = root.visitGroup(module.name)
        val categorized = classes.getClasses.asScala.toSeq.groupBy { entry =>
          source(entry.getPackageName, entry.getSourceFileName) match {
            case None                                                                               => "unmapped"
            case Some(file) if module.generated.exists(root => file.toPath.startsWith(root.toPath)) => "generated"
            case _                                                                                  => "authored"
          }
        }
        categorized.toSeq.sortBy(_._1).foreach { (name, entries) =>
          val bundle = new CoverageBuilder()
          entries.foreach(bundle.visitCoverage(_))
          group.visitBundle(bundle.getBundle(name), locator)
        }
      }
      visitor.visitEnd()
    }
  }
}
