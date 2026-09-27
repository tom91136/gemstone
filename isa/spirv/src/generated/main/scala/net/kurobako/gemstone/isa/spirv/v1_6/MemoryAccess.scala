// Generated from the pinned Khronos SPIR-V core grammar. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv.v1_6

@javax.annotation.processing.Generated(Array("sbt codegen"))
object MemoryAccess {
  final val None: Int = 0
  final val Volatile: Int = 1
  final val Aligned: Int = 2
  final val Nontemporal: Int = 4
  final val MakePointerAvailable: Int = 8
  final val MakePointerVisible: Int = 16
  final val NonPrivatePointer: Int = 32
  final val AliasScopeINTELMask: Int = 65536
  final val NoAliasINTELMask: Int = 131072
}
