package net.kurobako.gemstone.isa

import net.kurobako.gemstone.constraint.*

/** A major/minor ISA version, ordered lexicographically. */
case class Version(major: Int > 0, minor: Int >= 0) extends Ordered[Version] {
  override def compare(that: Version): Int =
    Ordering[(Int > 0, Int >= 0)].compare((major, minor), (that.major, that.minor))
  override def toString: String = s"$major.$minor"
}
