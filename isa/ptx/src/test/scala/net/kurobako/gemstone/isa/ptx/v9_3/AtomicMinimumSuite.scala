package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*

import VMIR.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.LaunchGeometry
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions

final class AtomicMinimumSuite extends munit.FunSuite {
  for {
    space  <- Seq("global", "shared")
    width  <- Seq(32, 64)
    groups <- Seq(1, 5)
  }
    test(s"unsigned atomic min $space/$width over $groups groups preserves high-bit ordering") {
      val memory    = ArrayCheckedMemory.allocate((8 * (groups + 1)).refineUnsafe[Positive0], GuestAddress(0x100000))
      val max       = if (width == 64) -1L else 0xffffffffL
      val byteWidth = if (width == 64) MemoryValueByteWidth(8) else MemoryValueByteWidth(4)
      memory.writeLittleEndian(memory.allocation.start, max, byteWidth).toOption.get
      val init   = if (space == "shared") s"st.shared.u$width [scratch], $max;" else ""
      val target = if (space == "shared") "scratch" else "%out"
      val body   = s""".version 8.0
.target sm_80
.address_size 64
.visible .entry kernel(.param .u64 output) {
.reg .b64 %out, %address, %offset;
.reg .b32 %group;
.reg .b$width %old, %result;
.shared .align 8 .b8 scratch[8];
ld.param.u64 %out, [output];
$init
atom.$space.min.u$width %old, [$target], 1;
ld.$space.u$width %result, [$target];
mov.u32 %group, %ctaid.x;
mul.wide.u32 %offset, %group, 8;
add.u64 %address, %out, %offset;
add.u64 %address, %address, 8;
st.global.u$width [%address], %result;
ret;
}
"""
      val kernel   = VMCompiler.compile(body).fold(error => fail(error.message), value => value)
      val geometry = LaunchGeometry.forLaunch(Dimensions.One, TestDimensions(groups)).toOption.get
      val result   = VMInterpreter.execute(
        kernel,
        geometry,
        Map(ParameterName("output") -> Argument(memory.allocation.start.value)),
        memory
      )
      assert(result.isRight, result.toString)
      for (group <- 0 until groups)
        assertEquals(memory.readLittleEndian(memory.allocation.start.offset(8 + group * 8), byteWidth), Right(1L))
    }
}
