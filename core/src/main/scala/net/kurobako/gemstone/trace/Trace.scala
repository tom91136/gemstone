package net.kurobako.gemstone.trace

import net.kurobako.gemstone.Col

import cats.data.Chain

enum TraceEvent(val step: Long, val eventType: String, fields: Col[(String, String)]) {
  case Instruction(
      override val step: Long,
      kernel: String,
      block: Int,
      lane: Int,
      pc: Int,
      opcode: String
  ) extends TraceEvent(
        step,
        "instruction",
        Col(
          "kernel" -> kernel,
          "block"  -> block.toString,
          "lane"   -> lane.toString,
          "pc"     -> pc.toString,
          "opcode" -> opcode
        )
      )

  case OutputWrite(
      override val step: Long,
      block: Int,
      lane: Int,
      address: Long,
      value: Int
  ) extends TraceEvent(
        step,
        "output-write",
        Col(
          "block"   -> block.toString,
          "lane"    -> lane.toString,
          "address" -> java.lang.Long.toUnsignedString(address),
          "value"   -> value.toString
        )
      )

  case SchedulerDecision(override val step: Long, block: Int, lane: Int)
      extends TraceEvent(
        step,
        "scheduler-decision",
        Col(
          "block" -> block.toString,
          "lane"  -> lane.toString
        )
      )

  final def toJSON: String = {
    val payload = Col("event" -> eventType, "step" -> step.toString) ++ fields
    payload.map { case (key, value) => s"\"${TraceEvent.escape(key)}\":\"${TraceEvent.escape(value)}\"" }.mkString(
      "{",
      ",",
      "}"
    )
  }
}

object TraceEvent {
  private def escape(value: String): String = value.flatMap {
    case '"'  => "\\\""
    case '\\' => "\\\\"
    case '\n' => "\\n"
    case '\r' => "\\r"
    case '\t' => "\\t"
    case char => char.toString
  }
}

final case class Trace private (private val eventChain: Chain[TraceEvent]) {
  def events: Col[TraceEvent]          = Col.from(eventChain.iterator)
  def append(event: TraceEvent): Trace = Trace(eventChain.append(event))
  def jsonLines: String                = eventChain.iterator.map(_.toJSON).mkString("\n")
}

object Trace {
  val empty: Trace = Trace(Chain.empty)
}
