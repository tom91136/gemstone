package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.constraint.*

/** Measurements of one SPIR-V execution. */
case class ExecutionMetrics(
    dispatches: Long >= 0,
    collectiveDispatches: Long >= 0,
    invocations: Long >= 0,
    instructions: Long >= 0,
    invocationValueInstances: Long >= 0,
    denseValueSlots: Long >= 0,
    estimatedDenseValueBytes: Long >= 0,
    elapsedNanos: Long >= 0
)
