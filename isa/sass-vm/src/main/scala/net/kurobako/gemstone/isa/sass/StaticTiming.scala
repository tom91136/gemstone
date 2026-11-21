package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.profile.Pipe
import net.kurobako.gemstone.vm.Schedule

/** The offset, pipe and planned issue cycles of each decoded instruction, as a launch profiles it: the pipe is the
  * first one among the lowered instructions [[VMAssembler.source]] attributes to it, and the schedule the one its
  * control word encodes.
  */
private object StaticTiming {

  type Row = (offset: Long, pipe: Option[Pipe], schedule: Option[Schedule])

  def of(program: ExecutableProgram, offsets: Vec[Long]): Vec[Row] = {
    val decoded   = offsets.zipWithIndex.toMap
    val pipes     = Array.fill[Option[Pipe]](offsets.size)(None)
    val schedules = Array.fill[Option[Schedule]](offsets.size)(None)
    program.instructions.indices.foreach { pc =>
      val at = Pc(pc.unsafe[Int >= 0])
      program.source(at).flatMap(address => decoded.get(address.raw)).foreach { index =>
        if (pipes(index).isEmpty) pipes(index) = Pipes.of(program.instructions(pc).instruction)
        if (schedules(index).isEmpty) schedules(index) = program.schedule(at)
      }
    }
    Vec.tabulate(offsets.size)(index => (offsets(index), pipes(index), schedules(index)))
  }
}
