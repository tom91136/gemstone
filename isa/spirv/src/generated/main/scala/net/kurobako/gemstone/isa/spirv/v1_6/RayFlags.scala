// Generated from the pinned Khronos SPIR-V core grammar. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv.v1_6

@javax.annotation.processing.Generated(Array("sbt codegen"))
object RayFlags {
  final val NoneKHR: Int = 0
  final val OpaqueKHR: Int = 1
  final val NoOpaqueKHR: Int = 2
  final val TerminateOnFirstHitKHR: Int = 4
  final val SkipClosestHitShaderKHR: Int = 8
  final val CullBackFacingTrianglesKHR: Int = 16
  final val CullFrontFacingTrianglesKHR: Int = 32
  final val CullOpaqueKHR: Int = 64
  final val CullNoOpaqueKHR: Int = 128
  final val SkipTrianglesKHR: Int = 256
  final val SkipAABBsKHR: Int = 512
  final val ForceOpacityMicromap2StateKHR: Int = 1024
}
