// Generated from the pinned Khronos SPIR-V core grammar. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv.v1_6

@javax.annotation.processing.Generated(Array("sbt codegen"))
object LoopControl {
  final val None: Int = 0
  final val Unroll: Int = 1
  final val DontUnroll: Int = 2
  final val DependencyInfinite: Int = 4
  final val DependencyLength: Int = 8
  final val MinIterations: Int = 16
  final val MaxIterations: Int = 32
  final val IterationMultiple: Int = 64
  final val PeelCount: Int = 128
  final val PartialCount: Int = 256
  final val InitiationIntervalALTERA: Int = 65536
  final val MaxConcurrencyALTERA: Int = 131072
  final val DependencyArrayALTERA: Int = 262144
  final val PipelineEnableALTERA: Int = 524288
  final val LoopCoalesceALTERA: Int = 1048576
  final val MaxInterleavingALTERA: Int = 2097152
  final val SpeculatedIterationsALTERA: Int = 4194304
  final val NoFusionALTERA: Int = 8388608
  final val LoopCountALTERA: Int = 16777216
  final val MaxReinvocationDelayALTERA: Int = 33554432
  final val MultipleWaitQueuesQCOM: Int = 268435456
}
