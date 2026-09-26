package net.kurobako.gemstone.server

import net.kurobako.gemstone.launcher.ComputeAPI
import net.kurobako.gemstone.launcher.RuntimeLayout

import java.nio.file.Files

/** Guards against drift between the launcher's library names and the native build outputs. */
final class RuntimeLayoutSuite extends munit.FunSuite {
  test("launcher library names match the native build outputs") {
    val layout = RuntimeLayout(NativeTestProcess.nativeDirectory)
    ComputeAPI.values.foreach { api =>
      val library = layout.library(api)
      assert(Files.isRegularFile(library), s"missing native library for $api: $library")
    }
  }
}
