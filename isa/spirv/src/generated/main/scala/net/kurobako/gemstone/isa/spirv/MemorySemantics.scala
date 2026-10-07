// Generated from the pinned Khronos SPIR-V core grammar. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv

@javax.annotation.processing.Generated(Array("sbt codegen"))
object MemorySemantics {
  final val Relaxed: Int = 0
  final val Acquire: Int = 2
  final val Release: Int = 4
  final val AcquireRelease: Int = 8
  final val SequentiallyConsistent: Int = 16
  final val UniformMemory: Int = 64
  final val SubgroupMemory: Int = 128
  final val WorkgroupMemory: Int = 256
  final val CrossWorkgroupMemory: Int = 512
  final val AtomicCounterMemory: Int = 1024
  final val ImageMemory: Int = 2048
  final val OutputMemory: Int = 4096
  final val MakeAvailable: Int = 8192
  final val MakeVisible: Int = 16384
  final val Volatile: Int = 32768
}
