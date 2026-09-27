package net.kurobako.gemstone.server

import net.kurobako.gemstone.launcher.ComputeAPI

import java.nio.file.Files

/** Guards against drift between the launcher's library names and the native build outputs. */
final class ComputeAPISuite extends munit.FunSuite {
  test("launcher library names match the native build outputs") {
    ComputeAPI.values.foreach { api =>
      val library = NativeTestProcess.nativeDirectory.resolve(api.lib)
      assert(Files.isRegularFile(library), s"missing native library for $api: $library")
    }
  }
}
