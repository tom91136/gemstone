package net.kurobako.gemstone.profile

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.flow.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import net.kurobako.gemstone.vm.LaunchObserver.*

/** The stream a launch records for a kernel known by what its static view says of each instruction: either one subgroup
  * issuing instructions in a stated order with every lane active, or a recorded launch of another layout of the kernel
  * issued again over these instructions.
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

  /** Where the issues of one recorded instruction go: the rows issued in its place, in order, and which of them takes
    * the accesses the recorded issue made.
    */
  case class Site(rows: Vec[Int], inherits: Option[Int])

  private val workgroup = Coordinates(1, 2, 3)

  private class Issued(
      val issued: Long,
      row: Row,
      val unit: MachineUnit,
      val subgroupSize: Int > 0,
      lanes: Vec[Int >= 0],
      val begins: Boolean
  ) extends ArchitecturalState {
    def text: String                             = row.text
    override def schedule: Option[Schedule]      = row.schedule
    override def pipe: Option[Pipe]              = row.pipe
    def eachLane(each: (Int >= 0) => Unit): Unit = lanes.foreach(each(_))
  }

  private def started(
      kernel: NonEmpty[String],
      graph: ControlFlowGraph,
      rows: Vec[Row],
      resources: KernelResources
  ): ProfileRecorder = {
    val recorder = ProfileRecorder()
    recorder.launched(resources)
    recorder.controlFlow(kernel.raw, graph, rows.map(_.address))
    recorder
  }

  private def record(recorder: ProfileRecorder, kernel: NonEmpty[String], row: Row, state: Issued)
      : Either[String, Unit] = {
    val at = Fault.Location(kernel, Some(kernel), Some(row.address.unsafe), Some(state.unit.workgroup), Some(0.unsafe))
    recorder.instruction(at, state).left.map(_.message.raw).map(_ => recorder.registers(state.issued, row.use))
  }

  /** `rows` holds one row per instruction of `graph`; `order` names the rows issued, in issue order. */
  def stream(
      kernel: NonEmpty[String],
      graph: ControlFlowGraph,
      rows: Vec[Row],
      order: Vec[Int],
      resources: KernelResources
  ): Either[String, ProfileStream] = {
    val recorder = started(kernel, graph, rows, resources)
    val size     = resources.subgroupSize
    val lanes    = Vec.tabulate(size.raw)(_.unsafe[Int >= 0])
    order.zipWithIndex.foldLeft[Either[String, Unit]](Right(())) { case (done, (index, issued)) =>
      done.flatMap { _ =>
        val row  = rows(index)
        val unit = MachineUnit(workgroup, issued.unsafe[Int >= 0], size.widen[Int >= 0], MachineUnit.State.Running)
        record(recorder, kernel, row, Issued(issued.toLong, row, unit, size, lanes, begins = true)).map(_ =>
          row.accesses.foreach(access =>
            recorder.access(access.lane, access.space, access.kind, access.address, access.bytes)
          )
        )
      }
    }.map(_ => recorder.stream)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class Replay(
      recorder: ProfileRecorder,
      kernel: NonEmpty[String],
      rows: Vec[Row],
      recorded: Vec[ProfileStream.Instruction],
      sites: Map[Long, Site]
  ) extends ProfileStream.Visitor {
    private val placed = recorded.map(instruction =>
      Option.when(instruction.kernel == kernel)(instruction.address).flatten.flatMap(address => sites.get(address.raw))
    )
    private var issued                                            = 0L
    private var inheriting                                        = false
    private var trailing: Option[(ProfileStream.Issue, Vec[Int])] = None
    var failure: Option[String]                                   = None

    private def emit(from: ProfileStream.Issue, index: Int): Unit = {
      val row   = rows(index)
      val unit  = MachineUnit(from.workgroup, from.unit, from.subgroupSize.widen[Int >= 0], MachineUnit.State.Running)
      val state = Issued(issued, row, unit, from.subgroupSize, from.lanes, from.begins)
      record(recorder, kernel, row, state).left.foreach(reason => if (failure.isEmpty) failure = Some(reason))
      issued += 1L
    }

    def flush(): Unit = {
      trailing.foreach((from, pending) => pending.foreach(emit(from, _)))
      trailing = None
    }

    override def issue(issue: ProfileStream.Issue): Unit = {
      flush()
      placed(issue.instruction) match {
        case None =>
          inheriting = false
          if (failure.isEmpty)
            failure = Some(s"the recording issues ${recorded(issue.instruction).text} outside $kernel")
        case Some(site) =>
          val head = site.inherits.fold(site.rows.size)(_ + 1)
          site.rows.take(head).foreach(emit(issue, _))
          inheriting = site.inherits.nonEmpty
          trailing = Some(issue -> site.rows.drop(head))
      }
    }

    override def access(lane: Int >= 0, space: Space, kind: AccessKind, address: Long, bytes: Int >= 0): Unit =
      if (inheriting) recorder.access(lane, space, kind, address, bytes)

    override def asynchronous(lane: Int >= 0, kind: AsyncKind, target: Long, bytes: Long): Unit =
      if (inheriting) recorder.asynchronous(lane, kind, target, bytes)

    override def transition(event: MachineEvent): Unit = recorder.transition(event.copy(issued = issued, at = None))
  }

  /** `recorded` as its launch would have recorded `kernel` holding `rows`. `sites` gives, by the address of each
    * instruction the launch issued, the rows every one of its issues becomes, for the same unit and lanes. The rows
    * after the one that takes an issue's accesses are issued before the next issue of the launch. Unit changes pass
    * through without their place in the program.
    */
  def replay(
      recorded: ProfileStream,
      kernel: NonEmpty[String],
      graph: ControlFlowGraph,
      rows: Vec[Row],
      sites: Map[Long, Site],
      resources: KernelResources
  ): Either[String, ProfileStream] = {
    val recorder = started(kernel, graph, rows, resources)
    val replay   = Replay(recorder, kernel, rows, recorded.instructions, sites)
    recorded.foreach(replay)
    replay.flush()
    replay.failure.toLeft(recorder.stream)
  }
}
