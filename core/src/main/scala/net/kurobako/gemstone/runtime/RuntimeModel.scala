package net.kurobako.gemstone.runtime

final case class DeviceId(value: Int)
final case class ContextId(value: Long)
final case class StreamId(value: Long)
final case class EventId(value: Long)
final case class ModuleId(value: Long)
final case class KernelId(value: Long)
final case class LaunchId(value: Long)

final case class Device(id: DeviceId, name: String)
final case class Context(id: ContextId, device: DeviceId)
final case class Stream(id: StreamId, context: ContextId)
final case class Event(id: EventId, context: ContextId)
final case class Module(id: ModuleId, profile: SupportProfile)
final case class Kernel(id: KernelId, module: ModuleId, name: String)
final case class Launch(id: LaunchId, kernel: KernelId, blocks: Int, threadsPerBlock: Int)

final case class SupportProfile(vendor: String, toolchain: String, target: String, schemaVersion: Int)

final class RuntimeState private (val devices: Vector[Device]) {
  def deviceCount: Int = devices.size
}

object RuntimeState {
  val singleEmulatedDevice: RuntimeState =
    new RuntimeState(Vector(Device(DeviceId(0), "GEMSTONE deterministic emulator")))
}
