package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.profile.*
import net.kurobako.gemstone.string.*

/** The profile shown over the listing: a heat per line and the measure that shades it. */
object Heat {

  /** A number of a [[LineHeat]] that can shade the listing. */
  enum Metric(val title: String, val unit: String) derives CanEqual {
    case Issues        extends Metric("issues", "subgroup issues")
    case IdleLanes     extends Metric("idle lanes", "lanes")
    case ExcessSectors extends Metric("excess sectors", "sectors")
    case BankConflicts extends Metric("bank conflicts", "wavefronts")
    case CyclesLost    extends Metric("estimated cycles lost", "cycles")

    def value(heat: LineHeat): Double = this match {
      case Issues        => heat.issues.toDouble
      case IdleLanes     => heat.idleLanes.toDouble
      case ExcessSectors => heat.excessSectors.toDouble
      case BankConflicts => heat.bankConflicts.toDouble
      case CyclesLost    => heat.cyclesLost
    }
  }

  /** How many shades the ramp has above the unshaded zero. */
  val Levels: Int = 5

  /** The shade of `value` against `maximum`, from 0 for none to [[Levels]]: any positive value shades, and the highest
    * shade starts at four fifths of the maximum.
    */
  def level(value: Double, maximum: Double): Int =
    if (value <= 0.0 || maximum <= 0.0) 0
    else math.max(1, math.min(Levels, math.ceil(value / maximum * Levels.toDouble).toInt))

  /** The largest value each shade stands for, lowest shade first. */
  def bounds(maximum: Double): Vec[Double] = Vec.tabulate(Levels)(index => maximum * (index + 1).toDouble / Levels)

  /** The heat of each line of `listing` under `profile`, in the order of the lines; a line no profiled instruction maps
    * to has none.
    */
  def of(listing: Listing, profile: Profile): Vec[Option[LineHeat]] = {
    val cycles = ProfileReport.findings(profile).groupMapReduce(_.instruction)(_.cycles)(_ + _)
    val byLine = profile.instructions.foldLeft(Map.empty[ProgramLine, LineHeat]) {
      case (found, (instruction, counters)) =>
        lineOf(listing, instruction) match {
          case Some(line) if counters.issues > 0L =>
            val heat = LineHeat(counters, cycles.getOrElse(instruction, 0.0))
            found.updated(line, found.get(line).fold(heat)(_.merge(heat)))
          case _ => found
        }
    }
    listing.lines.map(byLine.get)
  }

  /** The listing line of a profiled instruction, located as faults are. */
  def lineOf(listing: Listing, instruction: ProfileStream.Instruction): Option[ProgramLine] =
    instruction.address.flatMap(address => listing.find(instruction.function.map(_.raw), address.raw))

  /** The largest value of `metric` over `heats`. */
  def maximum(heats: Vec[Option[LineHeat]], metric: Metric): Double =
    heats.flatten.map(heat => metric.value(heat)).maxOption.getOrElse(0.0)

  /** `value` as the listing and legend print it: whole numbers plainly, fractions to one place. */
  def format(value: Double): String =
    if (value == math.rint(value)) f"$value%.0f" else f"$value%.1f"

  /** The numbers behind a line's shade, for its tooltip. */
  def hover(heat: LineHeat): String = {
    val lines = Vec(
      s"${heat.issues} issues, lane efficiency ${format(heat.laneEfficiency * 100.0)}% (${heat.idleLanes} idle lanes)",
      s"${heat.divergent} divergent issues"
    ) ++ Option.when(heat.global.requests > 0L)(
      s"global: ${heat.global.requests} requests, ${heat.global.sectors} sectors, ${heat.excessSectors} beyond the ideal ${heat.global.idealSectors}"
    ) ++ Option.when(heat.shared.requests > 0L)(
      s"shared: ${heat.shared.requests} requests, ${heat.shared.wavefronts} wavefronts, ${heat.bankConflicts} from bank conflicts"
    ) ++ Vec(s"estimated cycles lost ${format(heat.cyclesLost)}")
    lines.mkString("\n")
  }

  /** The occupancy the launch reported its resources for, as a sentence. */
  def occupancy(profile: Profile): Option[String] = profile.occupancy.map { occupancy =>
    val limiters = occupancy.limiters.toVector.sortBy(_.ordinal).map(_.toString.toLowerCaseRoot).mkString(", ")
    s"occupancy ${format(occupancy.fraction * 100.0)}%: ${occupancy.workgroups} workgroups, " +
      s"${occupancy.subgroups} of ${occupancy.maxSubgroups} subgroups, limited by $limiters"
  }

  /** The `count` findings that cost the most cycles. */
  def topFindings(profile: Profile, count: Int): Vec[ProfileReport.Finding] =
    ProfileReport.findings(profile).take(count)
}
