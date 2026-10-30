package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.Suite

class TensorMemoryFieldsSuite extends Suite {
  private val sparseModes = Seq(false, true)

  test("every kind takes at most five descriptor fields, sparse or not") {
    Tcgen05.Kind.values.foreach { kind =>
      sparseModes.foreach { sparse =>
        val multiply = Tcgen05.Multiply(
          kind = kind,
          architecture = 100,
          pair = false,
          ws = false,
          vector = 0,
          shift = 0,
          shiftA = false,
          d = Value.Register(Register(1)),
          a = Value.Register(Register(2)),
          aInTensorMemory = false,
          b = Value.Register(Register(3)),
          descriptor = Value.Register(Register(4)),
          enable = Value.Register(Register(5)),
          disabled = Vec.empty,
          scales = None,
          metadata = None,
          zeroColumns = None,
          sparseByDescriptor = sparse
        )
        assert(TensorMemory.Fields.formats(multiply).size <= 5, clue((kind, sparse)))
      }
    }
  }
}
