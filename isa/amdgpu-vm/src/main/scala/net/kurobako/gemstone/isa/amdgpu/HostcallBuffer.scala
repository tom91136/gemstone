package net.kurobako.gemstone.isa.amdgpu

/** Where a dispatch's kernel arguments hold the hostcall buffer, and the host end that answers its packets. */
private case class HostcallBuffer(argumentOffset: Int, listener: HostcallListener)
