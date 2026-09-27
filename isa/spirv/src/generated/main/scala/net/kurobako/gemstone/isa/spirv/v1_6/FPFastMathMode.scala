// Generated from the pinned Khronos SPIR-V core grammar. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv.v1_6

@javax.annotation.processing.Generated(Array("sbt codegen"))
object FPFastMathMode {
  final val None: Int = 0
  final val NotNaN: Int = 1
  final val NotInf: Int = 2
  final val NSZ: Int = 4
  final val AllowRecip: Int = 8
  final val Fast: Int = 16
  final val AllowContract: Int = 65536
  final val AllowReassoc: Int = 131072
  final val AllowTransform: Int = 262144
}
