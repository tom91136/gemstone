// Generated from the pinned Khronos SPIR-V core grammar. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv.v1_6

@javax.annotation.processing.Generated(Array("sbt codegen"))
object MatrixMultiplyAccumulateOperands {
  final val None: Int = 0
  final val MatrixASignedComponentsINTEL: Int = 1
  final val MatrixBSignedComponentsINTEL: Int = 2
  final val MatrixCBFloat16INTEL: Int = 4
  final val MatrixResultBFloat16INTEL: Int = 8
  final val MatrixAPackedInt8INTEL: Int = 16
  final val MatrixBPackedInt8INTEL: Int = 32
  final val MatrixAPackedInt4INTEL: Int = 64
  final val MatrixBPackedInt4INTEL: Int = 128
  final val MatrixATF32INTEL: Int = 256
  final val MatrixBTF32INTEL: Int = 512
  final val MatrixAPackedFloat16INTEL: Int = 1024
  final val MatrixBPackedFloat16INTEL: Int = 2048
  final val MatrixAPackedBFloat16INTEL: Int = 4096
  final val MatrixBPackedBFloat16INTEL: Int = 8192
}
