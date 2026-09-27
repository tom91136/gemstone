package net.kurobako.gemstone.isa

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

/** A major/minor ISA version, ordered lexicographically. */
case class Version(major: Positive[Int], minor: NonNegative[Int]) extends Ordered[Version] {
  override def compare(that: Version): Int =
    Ordering[(Positive[Int], NonNegative[Int])].compare((major, minor), (that.major, that.minor))
  override def toString: String = s"${major.raw}.${minor.raw}"
}
