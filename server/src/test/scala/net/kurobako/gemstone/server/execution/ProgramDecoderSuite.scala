package net.kurobako.gemstone.server.execution

final class ProgramDecoderSuite extends munit.FunSuite {
  test("backend lookup is open and reports unknown encodings") {
    val unknown = ProgramEncoding.from("not-registered").toOption.get
    assertEquals(
      BackendCatalog.from().backend(unknown),
      Left(RuntimeError.UnknownEncoding(unknown))
    )
  }
}
