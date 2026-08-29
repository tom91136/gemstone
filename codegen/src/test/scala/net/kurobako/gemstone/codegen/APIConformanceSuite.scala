package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets
import upickle.default.read

final class APIConformanceSuite extends munit.FunSuite {
  private val manifest = {
    val stream = Option(getClass.getResourceAsStream("/official-api-headers.json"))
      .getOrElse(throw IllegalStateException("official API header manifest is absent from the test classpath"))
    try read[ValidateAPIs.Manifest](String(stream.readAllBytes(), StandardCharsets.UTF_8))
    finally stream.close()
  }

  ValidateScalaAPIs.validations(manifest).foreach { surface =>
    surface.declarations.foreach { declaration =>
      test(s"${declaration.surface}/${declaration.name}") {
        declaration.validate()
      }
    }
    test(s"${surface.name}/has no non-header declarations") {
      assertEquals(surface.extra, Vector.empty)
    }
  }
}
