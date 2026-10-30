package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.testing.Suite

/** The SPIR-V describe pipeline loads each instance in the VM, which must run as it does on the JVM. */
class SPIRVPipelineSuite extends Suite {
  private def entry(mnemonic: String): View.Entry =
    SPIRVModel.all.find(_.mnemonic == mnemonic).getOrElse(fail(s"missing $mnemonic"))

  test("instances describe once the VM loads them, and reasons come from its lowering") {
    for (mnemonic <- Seq("OpSatConvertSToU", "OpenCL.std normalize", "OpenCL.std vstore_half")) {
      val found = entry(mnemonic)
      assert(SPIRVModel.describe(found, found.forms.head).nonEmpty, mnemonic)
    }
    val store = entry("OpStore")
    assertEquals(
      SPIRVModel.unavailable(store, store.forms.head),
      Unavailable.Mechanics(
        "Lowers to the VM's variables, loads, stores and pointer operations, which have no staged semantics."
      )
    )
    val trace = entry("OpTraceRayKHR")
    assertEquals(
      SPIRVModel.unavailable(trace, trace.forms.head),
      Unavailable.NotModelled("Requires RayTracingKHR: ray tracing.")
    )
  }
}
