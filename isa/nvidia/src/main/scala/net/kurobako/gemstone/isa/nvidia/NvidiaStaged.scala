package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.semantics.*

/** The NVIDIA instructions with staged semantics, matched once in [[staged]] for both executing and rendering them. */
private[isa] object NvidiaStaged {

  /** The semantics of `instruction` in `algebra`, consumed by `sinks`; `otherwise` when it has none. Each family is a
    * method of its own once expanded, which keeps every expansion within the JIT's method size limits.
    */
  inline def staged[V, R](inline algebra: Algebra[V], instruction: Instruction)(inline sinks: NvidiaSinks[V, R])(
      otherwise: => R
  ): R = {
    def tensor(instruction: Instruction): R = NvidiaMemoryStaged.tensor(algebra, instruction)(sinks)(otherwise)
    def memory(instruction: Instruction): R = NvidiaMemoryStaged.memory(algebra, instruction)(sinks)(tensor(_))
    def warp(instruction: Instruction): R   = NvidiaWarpStaged.warp(algebra, instruction)(sinks)(otherwise, memory(_))
    def moves(instruction: Instruction): R  = NvidiaMoveStaged.moves(algebra, instruction)(sinks)(warp(_))
    def predicates(instruction: Instruction): R = NvidiaMoveStaged.predicates(algebra, instruction)(sinks)(moves(_))
    def floating(instruction: Instruction): R   = NvidiaFloatStaged.floating(algebra, instruction)(sinks)(predicates(_))
    def permute(instruction: Instruction): R    = NvidiaLaneStaged.permute(algebra, instruction)(sinks)(floating(_))
    def video(instruction: Instruction): R      = NvidiaLaneStaged.video(algebra, instruction)(sinks)(permute(_))
    def lanes(instruction: Instruction): R      = NvidiaLaneStaged.lanes(algebra, instruction)(sinks)(video(_))
    def bitwise(instruction: Instruction): R    = NvidiaIntegerStaged.bitwise(algebra, instruction)(sinks)(lanes(_))
    def carries(instruction: Instruction): R    = NvidiaIntegerStaged.carries(algebra, instruction)(sinks)(bitwise(_))
    def integer(instruction: Instruction): R    = NvidiaIntegerStaged.integer(algebra, instruction)(sinks)(carries(_))

    integer(instruction)
  }
}
