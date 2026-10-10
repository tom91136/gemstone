import com.typesafe.tools.mima.plugin.MimaPlugin.autoImport.*
import sbt.*
import sbt.Keys.*
import scala.xml.Elem
import scala.xml.Node
import scala.xml.transform.RewriteRule
import scala.xml.transform.RuleTransformer

/** Settings for the modules published as the Gemstone library. */
object Publishing {

  /** The release whose binary API the next one must keep; none until the first release. */
  val previousRelease: Option[String] = None

  // Test scopes name unpublished fixture modules that consumers never resolve.
  private val testDependencies = new RewriteRule {
    override def transform(node: Node): Seq[Node] = node match {
      case dependency: Elem if dependency.label == "dependency" && (dependency \ "scope").text == "test" => Nil
      case other                                                                                         => other
    }
  }

  def library(about: String): Seq[Setting[?]] = Seq(
    description           := about,
    publish / skip        := false,
    pomPostProcess        := (pom => RuleTransformer(testDependencies).transform(pom).head),
    mimaPreviousArtifacts := previousRelease.map(release => projectID.value.withRevision(release)).toSet,
    // Scaladoc rejects the compiler-plugin options the build passes to scalac.
    Compile / doc / scalacOptions ~= (_.filterNot(option => option.startsWith("-Xplugin") || option.startsWith("-P:")))
  )
}
