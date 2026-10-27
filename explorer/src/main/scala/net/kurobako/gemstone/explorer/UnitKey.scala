package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.vm.*

object UnitKey {

  /** What identifies `unit` across its changes. */
  def of(unit: MachineUnit): UnitKey =
    UnitKey(unit.workgroup.x.raw, unit.workgroup.y.raw, unit.workgroup.z.raw, unit.ordinal.raw)
}

/** What identifies a unit across its changes: its workgroup and its place in it. */
case class UnitKey(x: Int, y: Int, z: Int, ordinal: Int) derives CanEqual
