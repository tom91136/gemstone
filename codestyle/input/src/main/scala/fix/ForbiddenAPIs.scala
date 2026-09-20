/*
rule = GemstoneForbiddenAPIs
 */
package fix

import java.nio.file.{Files, Path}

object ForbiddenAPIs {
  private object System {
    def nanoTime(): Long = 0L
  }

  val systemTime: Long  = java.lang.System.nanoTime()              // assert: GemstoneForbiddenAPIs.forbidden-api
  val runtimeCores: Int = Runtime.getRuntime.availableProcessors() // assert: GemstoneForbiddenAPIs.forbidden-api
  val deque: java.util.ArrayDeque[Int] = ??? // assert: GemstoneForbiddenAPIs.forbidden-api

  def permissions(path: Path): Unit =
    Files.setPosixFilePermissions(path, java.util.Set.of()) // assert: GemstoneForbiddenAPIs.forbidden-api

  val scalaRuntimeCores: Int = sys.runtime.availableProcessors()
  val unrelatedSystem: Long  = System.nanoTime()

  @SuppressWarnings(Array("scalafix:GemstoneForbiddenAPIs"))
  def platformBoundary: Long = java.lang.System.nanoTime()
}
