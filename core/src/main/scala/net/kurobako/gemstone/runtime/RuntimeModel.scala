package net.kurobako.gemstone.runtime

final case class DeviceId(value: Int)

final case class Device(id: DeviceId, name: String)

final class RuntimeState private (val devices: Vector[Device]) {
  def deviceCount: Int = devices.size
}

object RuntimeState {
  val singleEmulatedDevice: RuntimeState =
    new RuntimeState(Vector(Device(DeviceId(0), "GEMSTONE deterministic emulator")))
}
