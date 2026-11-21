package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.flow.Liveness
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.profile.StaticProfile
import net.kurobako.gemstone.vm.RegisterUse

/** Where the issues of a recorded launch go once a window of the launched kernel is rewritten. */
private[check] object RecordedIssues {

  /** The instructions of a kernel in layout order: their text, byte offsets, register use, and whether each reaches
    * memory.
    */
  case class Code(
      text: Vec[String],
      offsets: Vec[Int],
      uses: Vec[RegisterUse],
      reaches: Int => Either[String, Boolean]
  )

  private enum Step {
    case Deleted(index: Int)
    case Inserted(index: Int)
    case Kept(index: Int)
  }

  private val memoryUnit = "memory"

  private def units(use: RegisterUse, names: Vec[String]): Set[String] =
    names.flatMap(name => Liveness.units(name, use.unitsOf(name))).toSet

  private def memory(code: Code, index: Int): Either[String, Unit] =
    code.reaches(index).flatMap(found => Either.cond(!found, (), s"no recorded memory traffic for ${code.text(index)}"))

  /** What the rewrite may have changed by the end of `step`, given what it may have changed before. A register, or
    * memory, counts as changed once a deleted, changed or inserted instruction writes it, or a kept one that reads a
    * changed value; it stays changed, since the register use does not say whether a later write replaces it whole. A
    * kept instruction that reaches memory is held to the registers its address reads, not the value it stores. `mask`
    * names the units whose write changes which lanes issue.
    */
  private def changed(
      window: Window,
      original: Code,
      laid: Code,
      mask: Set[String],
      before: Set[String],
      step: Step
  ): Either[String, Set[String]] = step match {
    case Step.Deleted(index) =>
      val use = original.uses(index)
      original.reaches(index).map(found =>
        before ++ units(use, use.writes) ++ Option.when(found)(memoryUnit)
      )
    case Step.Inserted(index) =>
      val text    = laid.text(index)
      val written = units(laid.uses(index), laid.uses(index).writes)
      for {
        _ <- memory(laid, index)
        _ <- Either.cond(
          !written.exists(mask),
          (),
          s"$text writes the lane mask, so the recorded lanes do not hold after it"
        )
      } yield before ++ written
    case Step.Kept(index) =>
      val text = laid.text(index)
      val use  = laid.uses(index)
      laid.reaches(index).flatMap { found =>
        val deciding = if (found && use.address.nonEmpty) use.address else use.reads
        val read     = (units(use, deciding) ++ Option.when(found)(memoryUnit)).intersect(before)
        if (read.isEmpty) Right(before)
        else if (found)
          Left(
            s"$text reads ${Vec.from(read).sorted.mkString(", ")}, which the rewrite changes in the window " +
              s"${window.first}..${window.last}, so its recorded memory traffic does not hold"
          )
        else Right(before ++ units(use, use.writes))
      }
  }

  /** Where each issue of the recorded launch goes once `count` instructions stand in the window's place in `laid`. An
    * instruction outside the window keeps its issues and accesses. A window is straight-line with no write of the lane
    * mask, so every pass issues each of its instructions once for the same lanes: an instruction the replacement keeps
    * takes its issues and accesses with it, a changed or inserted one that does not reach memory issues once a pass,
    * and a deleted one issues nothing. A changed or inserted instruction that reaches memory or writes the lane mask,
    * and a kept one that reaches memory after reading what the rewrite changed, have no recorded traffic that holds.
    */
  def of(
      window: Window,
      count: Int,
      original: Code,
      laid: Code,
      mask: Set[String]
  ): Either[String, Map[Long, StaticProfile.Site]] = {
    val length = window.last - window.first + 1
    val kept   = BlockRegion.common(
      original.text.slice(window.first, window.last + 1),
      laid.text.slice(window.first, window.first + count)
    )
    val bounds = Vec.from(((-1, -1) +: kept :+ (length, count)).sliding(2))
    val steps  = bounds.flatMap { pair =>
      val (fromLeft, fromRight) = pair(0)
      val (toLeft, toRight)     = pair(1)
      Vec.from(fromLeft + 1 until toLeft).map(index => Step.Deleted(window.first + index)) ++
        Vec.from(fromRight + 1 until toRight).map(index => Step.Inserted(window.first + index)) ++
        Option.when(toLeft < length)(Step.Kept(window.first + toRight))
    }
    val outside = Vec.from(original.offsets.indices).filterNot(index => window.first <= index && index <= window.last)
      .map(index =>
        original.offsets(index).toLong ->
          StaticProfile.Site(Vec(if (index > window.last) index + count - length else index), Some(0))
      )
    val placed = kept.zipWithIndex.map { case ((left, right), at) =>
      val from  = if (at == 0) 0 else kept(at - 1)._2 + 1
      val until = if (at == kept.size - 1) count else right + 1
      left -> StaticProfile.Site(Vec.from(from until until).map(window.first + _), Some(right - from))
    }.toMap
    val unkept = StaticProfile.Site(Vec.from(window.first until window.first + count), None)
    val inside = Vec.from(0 until length).map(index =>
      original.offsets(window.first + index).toLong -> placed.getOrElse(
        index,
        if (kept.isEmpty && index == 0) unkept else StaticProfile.Site(Vec.empty, None)
      )
    )
    steps.foldLeft[Either[String, Set[String]]](Right(Set.empty))((found, step) =>
      found.flatMap(changed(window, original, laid, mask, _, step))
    ).map(_ => (outside ++ inside).toMap)
  }
}
