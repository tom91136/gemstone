package net.kurobako.gemstone.isa.amdgpu

private object DeviceHeapCall {
  def matching(name: String): Option[DeviceHeapCall] = DeviceHeapCall.values.find(_.symbol == name)
}

/** The device-library entry points of device-side `malloc` and `free`, which the VM serves from the launch's heap
  * region instead of running the library's slab allocator. Both take a 64-bit operand in v0-v1 per active lane;
  * `Allocate` returns the block's address there.
  */
private enum DeviceHeapCall(val symbol: String) derives CanEqual {
  case Allocate extends DeviceHeapCall("__ockl_dm_alloc")
  case Free     extends DeviceHeapCall("__ockl_dm_dealloc")
}
