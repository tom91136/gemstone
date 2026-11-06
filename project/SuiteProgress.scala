import java.util.concurrent.ConcurrentHashMap
import sbt.*

class SuiteProgress(log: Logger, suites: Seq[String]) extends TestReportListener {
  private val seen = ConcurrentHashMap.newKeySet[String]()

  def startGroup(name: String): Unit                                      = ()
  def endGroup(name: String, t: Throwable): Unit                          = ()
  def endGroup(name: String, result: TestResult): Unit                    = ()
  override def contentLogger(test: TestDefinition): Option[ContentLogger] = None

  def testEvent(event: TestEvent): Unit = event.detail
    .flatMap(e => suites.filter(e.fullyQualifiedName.startsWith).maxByOption(_.length))
    .distinct
    .filter(seen.add)
    .foreach(suite => log.info(s"finished $suite"))
}
