package net.kurobako.gemstone.launcher

import java.nio.file.Path

final class ProcessExitSuite extends munit.FunSuite {
  private val layout = RuntimeLayout(Path.of("/unused"), RuntimePlatform.Linux)
  private val apis   = Set(ComputeAPI.HSA)

  test("child exit status is preserved") {
    assertEquals(GemstoneProcess.run(apis, layout, Map.empty)(_ => 7), Right(7))
  }

  test("child receives the selected provider environment") {
    val result = GemstoneProcess.run(apis, layout, Map.empty) { environment =>
      environment.set("LD_PRELOAD")
    }
    assertEquals(result, Right("/unused/libhsa-runtime64.so.1"))
  }
}
