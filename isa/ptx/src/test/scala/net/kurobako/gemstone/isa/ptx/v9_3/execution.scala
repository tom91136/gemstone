package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

private object execution {
  def geometry(blocks: Int > 0, threads: Int > 0): LaunchGeometry =
    LaunchGeometry.forLaunch(dimensions(threads), dimensions(blocks))
      .toOption.get

}
