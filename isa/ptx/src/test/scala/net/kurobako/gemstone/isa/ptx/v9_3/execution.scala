package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.vm.*

private object execution {
  def geometry(blocks: Int, threads: Int): LaunchGeometry =
    LaunchGeometry.forLaunch(dimensions(threads), dimensions(blocks))
      .toOption.get

}
