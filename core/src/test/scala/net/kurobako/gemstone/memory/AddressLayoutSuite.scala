package net.kurobako.gemstone.memory

import net.kurobako.gemstone.testing.*

class AddressLayoutSuite extends Suite {
  private def below(a: Long, b: Long): Boolean = java.lang.Long.compareUnsigned(a, b) < 0

  test("every window is out of the host's reach and no two overlap") {
    for ((name, base, limit) <- AddressLayout.Windows) {
      assert(below(base, limit), name)
      assert(HostAddresses.guestOnly(base) && HostAddresses.guestOnly(limit - 1L), name)
    }
    val sorted = AddressLayout.Windows.sortWith((a, b) => below(a._2, b._2))
    for (List(first, second) <- sorted.sliding(2))
      assert(!below(second._2, first._3), s"${first._1} overlaps ${second._1}")
  }

  test("the reserved range fits the 48-bit AMDGPU descriptor base and holds every window the AMDGPU route addresses") {
    assertEquals(AddressLayout.ReservedLimit, 1L << 48)
    for (name <- List("Allocation", "Program", "AmdgpuDispatch"))
      for ((_, base, limit) <- AddressLayout.Windows.filter(_._1 == name))
        assert(base >= AddressLayout.ReservedBase && limit <= AddressLayout.ReservedLimit, name)
  }
}
