package net.kurobako.gemstone.trace

sealed trait TraceEvent extends Product with Serializable {
  def step: Long
  def eventType: String
  protected def fields: Vector[(String, String)]

  final def toJSON: String = {
    val payload = Vector("event" -> eventType, "step" -> step.toString) ++ fields
    payload.map { case (key, value) => s"\"${TraceEvent.escape(key)}\":\"${TraceEvent.escape(value)}\"" }.mkString(
      "{",
      ",",
      "}"
    )
  }
}

object TraceEvent {
  final case class Instruction(
      step: Long,
      kernel: String,
      block: Int,
      lane: Int,
      pc: Int,
      opcode: String
  ) extends TraceEvent {
    override val eventType: String                          = "instruction"
    override protected val fields: Vector[(String, String)] = Vector(
      "kernel" -> kernel,
      "block"  -> block.toString,
      "lane"   -> lane.toString,
      "pc"     -> pc.toString,
      "opcode" -> opcode
    )
  }

  final case class OutputWrite(
      step: Long,
      block: Int,
      lane: Int,
      address: Long,
      value: Int
  ) extends TraceEvent {
    override val eventType: String                          = "output-write"
    override protected val fields: Vector[(String, String)] = Vector(
      "block"   -> block.toString,
      "lane"    -> lane.toString,
      "address" -> java.lang.Long.toUnsignedString(address),
      "value"   -> value.toString
    )
  }

  final case class SchedulerDecision(step: Long, block: Int, lane: Int) extends TraceEvent {
    override val eventType: String                          = "scheduler-decision"
    override protected val fields: Vector[(String, String)] = Vector(
      "block" -> block.toString,
      "lane"  -> lane.toString
    )
  }

  private def escape(value: String): String = value.flatMap {
    case '"'  => "\\\""
    case '\\' => "\\\\"
    case '\n' => "\\n"
    case '\r' => "\\r"
    case '\t' => "\\t"
    case char => char.toString
  }
}

final case class Trace(events: Vector[TraceEvent]) {
  def append(event: TraceEvent): Trace = copy(events = events :+ event)
  def jsonLines: String                = events.map(_.toJSON).mkString("\n")
}

object Trace {
  val empty: Trace = Trace(Vector.empty)
}
