// Generated from the pinned Khronos SPIR-V core grammar. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv

import javax.annotation.processing.Generated

@Generated(Array("sbt codegen"))
object ImageOperands {
  final val None: Int = 0
  final val Bias: Int = 1
  final val Lod: Int = 2
  final val Grad: Int = 4
  final val ConstOffset: Int = 8
  final val Offset: Int = 16
  final val ConstOffsets: Int = 32
  final val Sample: Int = 64
  final val MinLod: Int = 128
  final val MakeTexelAvailable: Int = 256
  final val MakeTexelVisible: Int = 512
  final val NonPrivateTexel: Int = 1024
  final val VolatileTexel: Int = 2048
  final val SignExtend: Int = 4096
  final val ZeroExtend: Int = 8192
  final val Nontemporal: Int = 16384
  final val Offsets: Int = 65536
}
