package net.kurobako.gemstone.vm

import net.kurobako.gemstone.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.util.Random as ScalaRandom

object ConcurrencySchedule {

  case object Fixed extends ConcurrencySchedule {
    override def choose(candidates: Int): Int = 0
  }

  class Random(val seed: Long) extends ConcurrencySchedule {
    private val rng = ScalaRandom(mixSeed(seed, Coordinates.origin))

    override def choose(candidates: Int): Int =
      if (candidates <= 1) 0 else rng.nextInt(candidates)

    override def forWorkgroup(group: Coordinates): ConcurrencySchedule =
      Random(mixSeed(seed, group))
  }

  def mixSeed(base: Long, group: Coordinates): Long = {
    val z0 = (base + 0x9e3779b97f4a7c15L) ^
      (group.x.raw.toLong * 0x9e3779b97f4a7c15L + group.y.raw.toLong * 0xbf58476d1ce4e5b9L +
        group.z.raw.toLong * 0x94d049bb133111ebL)
    val z1 = (z0 ^ (z0 >>> 30)) * 0xbf58476d1ce4e5b9L
    val z2 = (z1 ^ (z1 >>> 27)) * 0x94d049bb133111ebL
    z2 ^ (z2 >>> 31)
  }

  def parse(value: String): Either[String, ConcurrencySchedule] = {
    val trimmed = value.trim
    if (trimmed.isEmpty || trimmed.equalsIgnoreCase("fixed")) Right(Fixed)
    else if (trimmed.toLowerCaseRoot.startsWith("random:")) {
      val seedStr = trimmed.substring("random:".length).trim
      seedStr.toLongOption match {
        case Some(seed) => Right(Random(seed))
        case None       => Left(s"invalid random seed in schedule '$value': expected a 64-bit integer")
      }
    } else if (trimmed.toLowerCaseRoot.startsWith("pct:")) {
      Left(s"PCT schedule is not yet implemented: '$value'")
    } else {
      Left(s"unknown concurrency schedule '$value': expected 'fixed' or 'random:<seed>'")
    }
  }

  def fromEnvironment(values: Map[String, String]): Either[String, ConcurrencySchedule] =
    configuration.lookup(values, environment.Schedule) match {
      case Some(value) => parse(value)
      case None        => Right(Fixed)
    }
}

sealed trait ConcurrencySchedule derives CanEqual {
  def choose(candidates: Int): Int
  def forWorkgroup(group: Coordinates): ConcurrencySchedule = this
}
