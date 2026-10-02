package net.kurobako.gemstone.isa.ptx.v9_3

import execution.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

class PredicateFileSuite extends munit.FunSuite {
  test("undefined predicate used by a vote still reports an execution fault") {
    val source = """.version 8.0
      .target sm_80
      .address_size 64
      .visible .entry undefined_predicate() {
        .reg .pred %p<64>;
        .reg .b32 %r;
        vote.sync.ballot.b32 %r, %p63, 1;
        ret;
      }"""
    val kernel = compileKernel(source)
    val result = execute(
      kernel,
      1,
      1,
      Map.empty,
      ArrayCheckedMemory.allocate(0)
    )
    assert(result.left.toOption.exists(_.message.contains("undefined register %p63")), result.toString)
  }

}
