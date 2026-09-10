package net.kurobako.gemstone.conformance

import java.nio.file.Files

final class TestProfileSuite extends munit.FunSuite {
  test("unknown CI hosts have no GPU oracle") {
    val profile =
      TestProfile.load(ConformanceSupport.root.resolve("conformance/profiles"), Map.empty, "ci-unknown-123", "linux")
    assertEquals(profile.file.getFileName.toString, "default.env")
    assert(profile.oracles.isEmpty)
  }
  test("framework16 has AMD targets and no NVIDIA oracle") {
    val profile = TestProfile.load(
      ConformanceSupport.root.resolve("conformance/profiles"),
      Map.empty,
      "sora-framework16.local",
      "linux"
    )
    assertEquals(profile.oracle("hip").map(_.target), Some("gfx1150"))
    assertEquals(profile.oracle("hip").map(_.resource), profile.oracle("hsa").map(_.resource))
    assertEquals(profile.oracle("cuda"), None)
  }
  test("explicit OS profile precedes hostname and append assignments retain all targets") {
    val directory = Files.createTempDirectory("gemstone-profile-")
    try {
      Files.writeString(directory.resolve("default.env"), "GEMSTONE_TEST_ORACLES=\n")
      Files.writeString(
        directory.resolve("chosen.linux.env"),
        "export GEMSTONE_TEST_ORACLES=\nGEMSTONE_TEST_ORACLES+=:hip@gfx900!amd-0\nGEMSTONE_TEST_ORACLES+=:hsa@gfx900!amd-0\n"
      )
      val profile = TestProfile.load(directory, Map("GEMSTONE_TEST_PROFILE" -> "chosen"), "unmatched", "linux")
      assertEquals(profile.oracles.map(_.provider), Vector("hip", "hsa"))
      assertEquals(profile.file.getFileName.toString, "chosen.linux.env")
    } finally {
      Files.deleteIfExists(directory.resolve("default.env"))
      Files.deleteIfExists(directory.resolve("chosen.linux.env"))
      val _ = Files.deleteIfExists(directory)
    }
  }
}
