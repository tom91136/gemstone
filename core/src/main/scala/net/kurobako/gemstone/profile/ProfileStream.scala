package net.kurobako.gemstone.profile

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.flow.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchObserver.*

object ProfileStream {

  /** A static instruction of the launch, named as the backend located it, with the registers it reads and writes, the
    * static schedule its compiler encoded where the ISA encodes one, and the compute pipe it issues to where the ISA
    * says.
    */
  case class Instruction(
      kernel: NonEmpty[String],
      function: Option[NonEmpty[String]],
      address: Option[Long >= 0],
      text: String,
      use: RegisterUse,
      schedule: Option[Schedule],
      pipe: Option[Pipe]
  ) derives CanEqual

  /** The control flow of a launched kernel, with the address of each of its instructions. */
  case class Graph(kernel: String, graph: ControlFlowGraph, offsets: Vec[Long])

  /** One issue: `unit` of `workgroup` ran `instruction` for `lanes`, indices within the workgroup, and `begins` as
    * [[ArchitecturalState.begins]].
    */
  case class Issue(
      issued: Long,
      workgroup: LaunchGeometry.Coordinates,
      unit: Int >= 0,
      instruction: Int >= 0,
      subgroupSize: Int > 0,
      lanes: Vec[Int >= 0],
      begins: Boolean
  ) derives CanEqual

  /** The facts of a launch in the order its units issued them, as a [[ProfileRecorder]] saw them. */
  trait Visitor {
    def issue(issue: Issue): Unit                                                                    = ()
    def access(lane: Int >= 0, space: Space, kind: AccessKind, address: Long, bytes: Int >= 0): Unit = ()
    def asynchronous(lane: Int >= 0, kind: AsyncKind, target: Long, bytes: Long): Unit               = ()
    def transition(event: MachineEvent): Unit                                                        = ()
  }

  private val Magic   = 0x47454d50524f4631L
  private val Version = 5

  private val spaces = Space.values
  private val kinds  = AccessKind.values
  private val asyncs = AsyncKind.values
  private val pipes  = Pipe.values

  /** Reads a stream [[ProfileStream.write]] wrote. */
  def read(in: DataInputStream): Either[String, ProfileStream] =
    try
      if (in.readLong() != Magic) Left("not a profile stream")
      else if (in.readInt() != Version) Left("unsupported profile stream version")
      else {
        val resources    = Option.when(in.readBoolean())(readResources(in))
        val instructions = Vec.fill(in.readInt())(readInstruction(in))
        val events       = Vec.fill(in.readInt())(readEvent(in, instructions))
        val records      = Array.fill(in.readInt())(in.readLong())
        val graphs       = Vec.fill(in.readInt())(readGraph(in))
        Right(ProfileStream(resources, instructions, events, records, graphs))
      }
    catch { case e: IOException => Left(s"truncated or unreadable profile stream: ${e.getMessage}") }

  private def readResources(in: DataInputStream): KernelResources = {
    val lanes     = in.readInt().refine[Int > 0].getOrElse(throw IOException("empty workgroup"))
    val subgroup  = in.readInt().refine[Int > 0].getOrElse(throw IOException("empty subgroup"))
    val registers = in.readInt()
    val shared    = in.readLong().refine[Long >= 0].getOrElse(throw IOException("negative shared size"))
    KernelResources(lanes, subgroup, Option.when(registers >= 0)(registers.unsafe[Int >= 0]), shared)
  }

  private def readNames(in: DataInputStream): Vec[String] = Vec.fill(in.readInt())(in.readUTF())

  private def readName(in: DataInputStream): NonEmpty[String] =
    in.readUTF().refine[NonEmpty[String]].getOrElse(throw IOException("empty name"))

  private def readInstruction(in: DataInputStream): Instruction = {
    val kernel   = readName(in)
    val function = Option.when(in.readBoolean())(readName(in))
    val address  = in.readLong()
    val text     = in.readUTF()
    val reads    = readNames(in)
    val writes   = readNames(in)
    val units    = Vec.fill(in.readInt())(in.readUTF() -> in.readInt())
    val schedule = Option.when(in.readBoolean())(readSchedule(in))
    val pipe     = Option.when(in.readBoolean())(readPipe(in))
    Instruction(
      kernel,
      function,
      Option.when(address >= 0L)(address.unsafe[Long >= 0]),
      text,
      RegisterUse(reads, writes, units.toMap),
      schedule,
      pipe
    )
  }

  private def readSchedule(in: DataInputStream): Schedule = in.readByte() match {
    case 0 =>
      val stall = in.readInt().refine[Int in 0 ~ 15].getOrElse(throw IOException("stall out of range"))
      Schedule.Encoded(stall, in.readBoolean())
    case 1 => Schedule.Estimated(in.readInt().refine[Int >= 0].getOrElse(throw IOException("cycles out of range")))
    case _ => throw IOException("unknown schedule kind")
  }

  private def readPipe(in: DataInputStream): Pipe = {
    val ordinal = in.readUnsignedByte()
    if (ordinal >= pipes.length) throw IOException("unknown pipe")
    pipes(ordinal)
  }

  private def readEvent(in: DataInputStream, instructions: Vec[Instruction]): MachineEvent = {
    val workgroup = LaunchGeometry.Coordinates(
      readCount(in),
      readCount(in),
      readCount(in)
    )
    val ordinal = readCount(in)
    val lanes   = readCount(in)
    val kind    = in.readByte() match {
      case 0 => MachineEvent.Kind.Issue
      case 1 => MachineEvent.Kind.Blocked(readReason(in))
      case 2 => MachineEvent.Kind.Resumed
      case 3 => MachineEvent.Kind.Retired
      case n => throw IOException(s"unknown event kind $n")
    }
    val issued = in.readLong()
    val at     = in.readInt()
    MachineEvent(
      MachineUnit(workgroup, ordinal, lanes, kind.state),
      kind,
      issued,
      Option.when(at >= 0)(instructions(at)).map(locate(_))
    )
  }

  private def readGraph(in: DataInputStream): Graph = {
    val kernel  = in.readUTF()
    val entry   = in.readInt()
    val blocks  = Vec.fill(in.readInt())(readBlock(in))
    val offsets = Vec.fill(in.readInt())(in.readLong())
    Graph(kernel, ControlFlowGraph.fromBlocks(blocks, entry), offsets)
  }

  private def readBlock(in: DataInputStream): Block = {
    val first      = in.readInt()
    val last       = in.readInt()
    val open       = in.readBoolean()
    val successors = Vec.fill(in.readInt())(in.readInt())
    Block(first, last, successors, open)
  }

  private def writeGraph(out: DataOutputStream, value: Graph): Unit = {
    out.writeUTF(value.kernel)
    out.writeInt(value.graph.entry)
    out.writeInt(value.graph.blocks.size)
    value.graph.blocks.foreach { block =>
      out.writeInt(block.first)
      out.writeInt(block.last)
      out.writeBoolean(block.open)
      out.writeInt(block.successors.size)
      block.successors.foreach(out.writeInt(_))
    }
    out.writeInt(value.offsets.size)
    value.offsets.foreach(out.writeLong(_))
  }

  private def readCount(in: DataInputStream): Int >= 0 =
    in.readInt().refine[Int >= 0].getOrElse(throw IOException("negative count"))

  private def readReason(in: DataInputStream): MachineUnit.Reason = in.readByte() match {
    case 0 => MachineUnit.Reason.Barrier(in.readUTF())
    case 1 => MachineUnit.Reason.Members
    case 2 => MachineUnit.Reason.Memory
    case 3 => MachineUnit.Reason.Cluster(in.readUTF())
    case 4 => MachineUnit.Reason.Collective(in.readUTF())
    case n => throw IOException(s"unknown block reason $n")
  }

  private def writeReason(out: DataOutputStream, reason: MachineUnit.Reason): Unit = reason match {
    case MachineUnit.Reason.Barrier(id)    => out.writeByte(0); out.writeUTF(id)
    case MachineUnit.Reason.Members        => out.writeByte(1)
    case MachineUnit.Reason.Memory         => out.writeByte(2)
    case MachineUnit.Reason.Cluster(id)    => out.writeByte(3); out.writeUTF(id)
    case MachineUnit.Reason.Collective(id) => out.writeByte(4); out.writeUTF(id)
  }

  private def locate(instruction: Instruction): Fault.Location =
    Fault.Location(instruction.kernel, instruction.function, instruction.address, None, None)

  def apply(
      resources: Option[KernelResources],
      instructions: Vec[Instruction],
      events: Vec[MachineEvent],
      records: Array[Long],
      graphs: Vec[Graph]
  ): ProfileStream = new ProfileStream(resources, instructions, events, records, graphs)
}

/** A recorded launch: the static instructions it issued, the changes its units went through, located at those
  * instructions, and its facts encoded as packed records. [[foreach]] replays it to a visitor, so models run offline
  * from the machine that recorded it.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
class ProfileStream private (
    val resources: Option[KernelResources],
    val instructions: Vec[ProfileStream.Instruction],
    private val events: Vec[MachineEvent],
    private val records: Array[Long],
    val graphs: Vec[ProfileStream.Graph]
) {
  import ProfileRecords.*
  import ProfileStream.*

  /** Replays the stream to `visitor` in recorded order. */
  def foreach(visitor: Visitor): Unit = {
    var at = 0
    while (at < records.length) {
      val header = records(at)
      visit(visitor, header, at)
      at += recordLength(header)
    }
  }

  private def visit(visitor: Visitor, header: Long, at: Int): Unit = (header & 0xfL) match {
    case IssueTag =>
      val words = ((header >>> 4) & 0xffL).toInt
      val first = ((header >>> 12) & 0xffffL).toInt
      val xy    = records(at + 2)
      val zUnit = records(at + 3)
      val lanes = Vec.newBuilder[Int >= 0]
      var word  = 0
      while (word < words) {
        var bits = records(at + 5 + word)
        while (bits != 0L) {
          val bit = java.lang.Long.numberOfTrailingZeros(bits)
          lanes += ((first + word) * 64 + bit).unsafe[Int >= 0]
          bits &= bits - 1L
        }
        word += 1
      }
      visitor.issue(
        Issue(
          records(at + 1),
          LaunchGeometry.Coordinates(
            (xy & 0xffffffffL).toInt.unsafe,
            (xy >>> 32).toInt.unsafe,
            (zUnit & 0xffffffffL).toInt.unsafe
          ),
          (zUnit >>> 32).toInt.unsafe,
          (header >>> 32).toInt.unsafe,
          records(at + 4).toInt.unsafe,
          lanes.result(),
          (header & BeginsBit) != 0L
        )
      )
    case AccessTag =>
      visitor.access(
        ((header >>> 9) & 0x7fffffL).toInt.unsafe,
        spaces(((header >>> 4) & 0x7L).toInt),
        kinds(((header >>> 7) & 0x3L).toInt),
        records(at + 1),
        (header >>> 32).toInt.unsafe
      )
    case AsyncTag =>
      visitor.asynchronous(
        ((header >>> 9) & 0x7fffffL).toInt.unsafe,
        asyncs(((header >>> 4) & 0x1fL).toInt),
        records(at + 1),
        records(at + 2)
      )
    case _ => visitor.transition(events((header >>> 4).toInt))
  }

  /** Writes the stream in a versioned binary form [[ProfileStream.read]] reads back. */
  def write(out: DataOutputStream): Unit = {
    out.writeLong(Magic)
    out.writeInt(Version)
    out.writeBoolean(resources.nonEmpty)
    resources.foreach { value =>
      out.writeInt(value.workgroupLanes)
      out.writeInt(value.subgroupSize)
      out.writeInt(value.registersPerLane.fold(-1)(_.raw))
      out.writeLong(value.sharedBytes)
    }
    out.writeInt(instructions.size)
    instructions.foreach { instruction =>
      out.writeUTF(instruction.kernel.raw)
      out.writeBoolean(instruction.function.nonEmpty)
      instruction.function.foreach(name => out.writeUTF(name.raw))
      out.writeLong(instruction.address.fold(-1L)(_.raw))
      out.writeUTF(instruction.text)
      Vec(instruction.use.reads, instruction.use.writes).foreach { names =>
        out.writeInt(names.size)
        names.foreach(out.writeUTF(_))
      }
      out.writeInt(instruction.use.units.size)
      instruction.use.units.foreach { (name, units) =>
        out.writeUTF(name)
        out.writeInt(units)
      }
      out.writeBoolean(instruction.schedule.nonEmpty)
      instruction.schedule.foreach {
        case Schedule.Encoded(stall, writeBarrier) =>
          out.writeByte(0)
          out.writeInt(stall.raw)
          out.writeBoolean(writeBarrier)
        case Schedule.Estimated(cycles) =>
          out.writeByte(1)
          out.writeInt(cycles.raw)
      }
      out.writeBoolean(instruction.pipe.nonEmpty)
      instruction.pipe.foreach(pipe => out.writeByte(pipe.ordinal))
    }
    val indices = instructions.view.map(locate(_)).zipWithIndex.toMap
    out.writeInt(events.size)
    events.foreach { event =>
      out.writeInt(event.unit.workgroup.x)
      out.writeInt(event.unit.workgroup.y)
      out.writeInt(event.unit.workgroup.z)
      out.writeInt(event.unit.ordinal)
      out.writeInt(event.unit.lanes)
      event.kind match {
        case MachineEvent.Kind.Issue           => out.writeByte(0)
        case MachineEvent.Kind.Blocked(reason) => out.writeByte(1); writeReason(out, reason)
        case MachineEvent.Kind.Resumed         => out.writeByte(2)
        case MachineEvent.Kind.Retired         => out.writeByte(3)
      }
      out.writeLong(event.issued)
      out.writeInt(event.at.fold(-1)(indices))
    }
    out.writeInt(records.length)
    records.foreach(out.writeLong(_))
    out.writeInt(graphs.size)
    graphs.foreach(writeGraph(out, _))
    out.flush()
  }
}
