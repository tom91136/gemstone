package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import net.kurobako.gemstone.semantics.*

/** The one dispatch from lowered AMDGPU instructions to their staged semantics: execution and description pass their
  * sinks, so operand wiring is defined once. Instructions without staged semantics take `otherwise`. Each family
  * matches its own instructions and passes the rest on.
  */
private object AMDGPUStaged {

  inline def staged[V, R](inline algebra: Algebra[V], instruction: Instruction)(inline sinks: AMDGPUSinks[V, R])(
      otherwise: => R
  ): R = VectorStaged.staged(algebra, instruction)(sinks)(
    ScalarStaged.staged(algebra, instruction)(sinks)(
      VectorFloatStaged.staged(algebra, instruction)(sinks)(
        MoveStaged.staged(algebra, instruction)(sinks)(
          ConversionStaged.staged(algebra, instruction)(sinks)(
            AtomicStaged.staged(algebra, instruction)(sinks)(otherwise)
          )
        )
      )
    )
  )
}
