/*
rule = ForbiddenAPIs
 */
package fix

import java.nio.file.{Files, Path}
import java.util.Locale

object ForbiddenAPIs {
  private object System {
    def nanoTime(): Long = 0L
  }

  val systemTime: Long  = java.lang.System.nanoTime()              // assert: ForbiddenAPIs.forbidden-api
  val runtimeCores: Int = Runtime.getRuntime.availableProcessors() // assert: ForbiddenAPIs.forbidden-api
  val deque: java.util.ArrayDeque[Int] = ??? // assert: ForbiddenAPIs.forbidden-api

  def permissions(path: Path): Unit =
    Files.setPosixFilePermissions(path, java.util.Set.of()) // assert: ForbiddenAPIs.forbidden-api

  val lower          = "ABC".toLowerCase                        // assert: ForbiddenAPIs.forbidden-api
  val upper          = "abc".toUpperCase()                      // assert: ForbiddenAPIs.forbidden-api
  val lowerLocale    = "ABC".toLowerCase(Locale.ROOT)           // assert: ForbiddenAPIs.forbidden-api
  val upperQualified = "abc".toUpperCase(java.util.Locale.ROOT) // assert: ForbiddenAPIs.forbidden-api
  val chained        = "ABC".toLowerCase.trim                   // assert: ForbiddenAPIs.forbidden-api
  val lowerReference: Locale => String = "ABC".toLowerCase // assert: ForbiddenAPIs.forbidden-api

  val characterLower = 'A'.toLower
  val characterUpper = Character.toUpperCase('a')
  object Unrelated {
    def toLowerCase(): String = "lower"
  }
  val unrelatedLower = Unrelated.toLowerCase()

  val scalaRuntimeCores: Int = sys.runtime.availableProcessors()
  val unrelatedSystem: Long  = System.nanoTime()

  @SuppressWarnings(Array("scalafix:ForbiddenAPIs"))
  def platformBoundary: Long = java.lang.System.nanoTime()
}
