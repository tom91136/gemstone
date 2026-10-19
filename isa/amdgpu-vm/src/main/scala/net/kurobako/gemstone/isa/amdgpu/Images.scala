package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.semantics.prelude.*

/** The linear images a lane of a rendered AMDGPU definition addresses through eight-dword resource descriptors, `t0` to
  * `t7`, as `isa` lays them out. A descriptor execution cannot read faults.
  */
private[isa] trait Images {

  /** The address of the texel an image address names, as [[ImageResource.locate]] finds it from the coordinates `c0` to
    * `c4`, or -1 outside the image.
    */
  def texel(
      isa: ISA,
      t0: UInt[32],
      t1: UInt[32],
      t2: UInt[32],
      t3: UInt[32],
      t4: UInt[32],
      t5: UInt[32],
      t6: UInt[32],
      t7: UInt[32],
      c0: UInt[32],
      c1: UInt[32],
      c2: UInt[32],
      c3: UInt[32],
      c4: UInt[32],
      dimension: Option[ImageDimension],
      mip: Bool
  ): UInt[64]

  /** What GET_RESINFO returns for `component` of `level`: the width, height, depth or layers, or the level count. */
  def information(
      isa: ISA,
      t0: UInt[32],
      t1: UInt[32],
      t2: UInt[32],
      t3: UInt[32],
      t4: UInt[32],
      t5: UInt[32],
      t6: UInt[32],
      t7: UInt[32],
      level: UInt[32],
      component: Int
  ): UInt[32]
}
