package net.kurobako.gemstone.profile

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.flow.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import net.kurobako.gemstone.vm.LaunchObserver.*

/** The stream a launch records when one subgroup issues instructions of a kernel in a stated order with every lane
  * active, built from what is known of each instruction without running it.
  */
object StaticProfile {

  case class Access(lane: Int >= 0, space: Space, kind: AccessKind, address: Long, bytes: Int >= 0)

  case class Row(
      address: Long,
      text: String,
      pipe: Option[Pipe],
      schedule: Option[Schedule],
      use: RegisterUse,
      accesses: Vec[Access] = Vec.empty
  )

  private val workgroup = Coordinates(1, 2, 3)

  private class Issued(val issued: Long, row: Row, lanes: Int > 0) extends ArchitecturalState {
    def text: String                             = row.text
    override def schedule: Option[Schedule]      = row.schedule
    override def pipe: Option[Pipe]              = row.pipe
    def begins: Boolean                          = true
    def subgroupSize: Int > 0                    = lanes
    def eachLane(each: (Int >= 0) => Unit): Unit = (0 until lanes.raw).foreach(lane => each(lane.unsafe))
    def unit: MachineUnit                        =
      MachineUnit(workgroup, issued.toInt.unsafe[Int >= 0], lanes.widen[Int >= 0], MachineUnit.State.Running)
  }

  /** `rows` holds one row per instruction of `graph`; `order` names the rows issued, in issue order. */
  def stream(
      kernel: NonEmpty[String],
      graph: ControlFlowGraph,
      rows: Vec[Row],
      order: Vec[Int],
      resources: KernelResources
  ): Either[String, ProfileStream] = {
    val recorder = ProfileRecorder()
    recorder.launched(resources)
    recorder.controlFlow(kernel.raw, graph, rows.map(_.address))
    order.zipWithIndex.foldLeft[Either[String, Unit]](Right(())) { case (done, (index, issued)) =>
      done.flatMap { _ =>
        val row = rows(index)
        val at  = Fault.Location(kernel, Some(kernel), Some(row.address.unsafe), Some(workgroup), Some(0.unsafe))
        recorder.instruction(at, Issued(issued.toLong, row, resources.subgroupSize)).left.map(_.message.raw).map { _ =>
          row.accesses.foreach(access =>
            recorder.access(access.lane, access.space, access.kind, access.address, access.bytes)
          )
          recorder.registers(issued.toLong, row.use)
        }
      }
    }.map(_ => recorder.stream)
  }
}
