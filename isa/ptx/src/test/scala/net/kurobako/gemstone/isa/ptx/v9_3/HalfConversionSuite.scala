package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.{NonEmptyString, NonEmptyVec, NonNegativeInt, NonNegativeLong, PositiveInt, PositiveLong}

import Program.*

import VMIR.*

import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.LaunchGeometry
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions
final class HalfConversionSuite extends munit.FunSuite {
  test("cvt.rn.f16.f64 rounds directly at a half midpoint") {
    val input  = java.lang.Double.doubleToRawLongBits(1 + math.pow(2, -11) + math.pow(2, -25))
    val source = s""".version 8.0
.target sm_80
.address_size 64
.visible .entry k(.param .u64 output) {
.reg .b64 %out;
.reg .f64 %d;
.reg .b16 %h;
ld.param.u64 %out, [output];
mov.f64 %d, 0d${java.lang.Long.toHexString(input)};
cvt.rn.f16.f64 %h, %d;
st.global.b16 [%out], %h;
ret;
}
"""
    val kernel = VMCompiler.compile(source).fold(error => fail(error.message), value => value)
    val memory = ArrayCheckedMemory.allocate(NonNegativeInt(2), GuestAddress(0x10000L))
    assert(VMInterpreter.execute(
      kernel,
      LaunchGeometry.forLaunch(Dimensions.One, Dimensions.One).toOption.get,
      Map(ParameterName("output") -> Argument.fromLong(memory.allocation.start.raw)),
      memory
    ).isRight)
    assertEquals(memory.readLittleEndian(memory.allocation.start, MemoryValueByteWidth.Word), Right(0x3c01L))
  }
}
