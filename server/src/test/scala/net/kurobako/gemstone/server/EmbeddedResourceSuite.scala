package net.kurobako.gemstone.server

final class EmbeddedResourceSuite extends munit.FunSuite {
  test("native server includes AMDGPU instruction metadata") {
    val path   = "/net/kurobako/gemstone/isa/amdgpu/metadata.json"
    val stream = Option(getClass.getResourceAsStream(path)).getOrElse(fail(s"missing AMDGPU metadata resource $path"))
    stream.close()
  }
}
