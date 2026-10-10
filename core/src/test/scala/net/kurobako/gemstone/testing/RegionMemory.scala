package net.kurobako.gemstone.testing

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

object RegionMemory {
  val Base: Long   = ArrayMemory.Base
  val Stride: Long = ArrayMemory.Stride
}

class RegionMemory(initial: (AllocationId, Int)*) extends ArrayMemory(initial*) {
  def allocate(bytes: Vec[Byte])(using munit.Location): AllocationId = {
    val id = allocate(bytes.size.toLong.refine[Long > 0].fold(error => munit.Assertions.fail(error.raw), identity))
    val _  = bytes.copyToArray(array(id))
    id
  }
}
