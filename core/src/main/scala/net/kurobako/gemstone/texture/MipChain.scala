package net.kurobako.gemstone.texture

/** The levels of a mipmapped texture: `table` is the device address of one [[TextureLayout.Level]] record per level.
  * Levels below `minLod` or above `maxLod` are never selected, `bias` shifts the level of detail before that, and
  * `filter` says whether neighbouring levels blend.
  */
case class MipChain(table: Long, levels: Int, filter: FilterMode, minLod: Float, maxLod: Float, bias: Float)
    derives CanEqual
