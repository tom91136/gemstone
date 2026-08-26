package net.kurobako.gemstone.runtime

class RuntimeModelSuite extends munit.FunSuite {
  test("canonical runtime exposes one deterministic emulated device") {
    val runtime = RuntimeState.singleEmulatedDevice
    assertEquals(runtime.deviceCount, 1)
    assertEquals(runtime.devices.head.id, DeviceId(0))
  }
}
