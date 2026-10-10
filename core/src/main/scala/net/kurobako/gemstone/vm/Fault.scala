package net.kurobako.gemstone.vm

import net.kurobako.gemstone.constraint.*

object Fault {

  /** Where in a launch a fault arose, as far as the backend knows. `instruction` is the ISA's own instruction address
    * within `function`, or within the kernel when no function is named: the byte offset for SASS and AMDGPU, the
    * instruction's ordinal within its function for PTX, and the instruction index within the module for SPIR-V. `lane`
    * is the invocation's index within its workgroup.
    */
  case class Location(
      kernel: NonEmpty[String],
      function: Option[NonEmpty[String]],
      instruction: Option[Long >= 0],
      workgroup: Option[LaunchGeometry.Coordinates],
      lane: Option[Int >= 0]
  ) {
    def describe: String = (
      Vector(s"kernel '$kernel'") ++
        function.map(name => s"function '$name'") ++
        instruction.map(at => s"instruction 0x${at.raw.toHexString}") ++
        workgroup.map(group => s"workgroup (${group.x}, ${group.y}, ${group.z})") ++
        lane.map(index => s"lane $index")
    ).mkString(", ")
  }

  object InvalidRequest {
    def apply(detail: NonEmpty[String]): Fault = new InvalidRequest(detail)(None)
  }
  case class InvalidRequest private (detail: NonEmpty[String])(val location: Option[Location])
      extends Fault(detail) {
    def at(where: Location): Fault = if (location.isEmpty) InvalidRequest(detail)(Some(where)) else this
  }

  object NotFound {
    def apply(detail: NonEmpty[String]): Fault = new NotFound(detail)(None)
  }
  case class NotFound private (detail: NonEmpty[String])(val location: Option[Location]) extends Fault(detail) {
    def at(where: Location): Fault = if (location.isEmpty) NotFound(detail)(Some(where)) else this
  }

  object Memory {
    def apply(detail: NonEmpty[String]): Fault = new Memory(detail)(None)
  }
  case class Memory private (detail: NonEmpty[String])(val location: Option[Location]) extends Fault(detail) {
    def at(where: Location): Fault = if (location.isEmpty) Memory(detail)(Some(where)) else this
  }

  object Unsupported {
    def apply(detail: NonEmpty[String]): Fault = new Unsupported(detail)(None)
  }
  case class Unsupported private (detail: NonEmpty[String])(val location: Option[Location])
      extends Fault(detail) {
    def at(where: Location): Fault = if (location.isEmpty) Unsupported(detail)(Some(where)) else this
  }

  object EventHandling {
    def apply(detail: NonEmpty[String]): Fault = new EventHandling(detail)(None)
  }
  case class EventHandling private (detail: NonEmpty[String])(val location: Option[Location])
      extends Fault(detail) {
    def at(where: Location): Fault = if (location.isEmpty) EventHandling(detail)(Some(where)) else this
  }

  object Backend {
    def apply(detail: NonEmpty[String]): Fault = new Backend(detail)(None)
  }
  case class Backend private (detail: NonEmpty[String])(val location: Option[Location]) extends Fault(detail) {
    def at(where: Location): Fault = if (location.isEmpty) Backend(detail)(Some(where)) else this
  }

  object BudgetExceeded {
    def apply(limit: Long > 0): Fault = new BudgetExceeded(limit)(None)
  }
  case class BudgetExceeded private (limit: Long > 0)(val location: Option[Location])
      extends Fault(s"instruction budget of $limit was exhausted") {
    def at(where: Location): Fault = if (location.isEmpty) BudgetExceeded(limit)(Some(where)) else this
  }

  case object Cancelled extends Fault("execution was cancelled") {
    val location: Option[Location] = None
    def at(where: Location): Fault = this
  }
}

/** Why a request or launch failed. Equality and extraction consider only the kind and detail, not the location. */
sealed abstract class Fault(val message: NonEmpty[String]) {

  /** Where in a launch the fault arose, when the backend knows. */
  def location: Option[Fault.Location]

  /** This fault located at `where`, unless it already carries a more precise location. */
  def at(where: Fault.Location): Fault

  /** The message, followed by the location when known. */
  def description: NonEmpty[String] = location.fold(message)(where => s"$message (${where.describe})")
}
