package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class AsyncCopySuite extends munit.FunSuite {
  test("cluster requirements record the shape the kernel's launches must use") {
    def source(directive: String): String =
      s".version 8.0\n.target sm_90\n.address_size 64\n.visible .entry kernel() $directive { ret; }"
    assertEquals(
      VMCompiler.compile(source(".reqnctapercluster 1, 1, 1")).map(_.cluster),
      Right(Some(Dimensions(1, 1, 1)))
    )
    assertEquals(
      VMCompiler.compile(source(".reqnctapercluster 2, 1, 2")).map(_.cluster),
      Right(Some(Dimensions(2, 1, 2)))
    )
    assertEquals(VMCompiler.compile(source(".explicitcluster")).map(_.cluster), Right(None))
    assert(VMCompiler.compile(source(".reqnctapercluster 17, 1, 1")).isLeft)
  }

  for (sourceSize <- Vec(0, 1, 4, 15, 16))
    test(s"async copy with cache hint copies $sourceSize bytes and zero-fills the rest") {
      val source = if (sourceSize == 0) "mov.u64 %source,0;" else "add.u64 %source,%target,16;"
      val kernel = compileKernel(s""".version 8.0
.target sm_80
.address_size 64
.visible .entry copy(.param .u64 data) {
.shared .align 16 .b8 buffer[16];
.reg .u64 %source,%target;
.reg .u32 %count,%a,%b,%c,%d;
ld.param.u64 %target,[data];
$source
mov.u32 %count,$sourceSize;
st.shared.v4.u32 [buffer],{255,255,255,255};
cp.async.ca.shared.global.L2::128B [buffer],[%source],16,%count;
cp.async.commit_group;
cp.async.wait_all;
ld.shared.v4.u32 {%a,%b,%c,%d},[buffer];
st.global.v4.u32 [%target],{%a,%b,%c,%d};
ret;
}""")
      val memory = DeviceMemory(32)
      val bytes  = Vec.tabulate(32)(index => (index + 1).toByte)
      assertEquals(memory.writeBytes(memory.start, bytes), Right(()))
      assertEquals(
        execute(
          kernel,
          1,
          1,
          Map(("data": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
          memory
        ),
        Right(())
      )
      assertEquals(
        memory.readBytes(memory.start, 16),
        Right(bytes.slice(16, 16 + sourceSize) ++ Vec.fill(16 - sourceSize)(0.toByte))
      )
    }

  test("async copy from a shared window address faults") {
    val kernel = compileKernel(""".version 8.0
.target sm_80
.address_size 64
.visible .entry copy(.param .u64 data) {
.shared .align 16 .b8 buffer[16];
.reg .u64 %source;
mov.u64 %source,buffer;
cvta.shared.u64 %source,%source;
cp.async.ca.shared.global [buffer],[%source],16;
cp.async.commit_group;
cp.async.wait_all;
ret;
}""")
    val memory    = DeviceMemory(32)
    val arguments = Map(("data": NonEmpty[String]) -> Argument.fromLong(memory.start.raw))
    execute(kernel, 1, 1, arguments, memory) match {
      case Left(Fault.Memory(_)) => ()
      case other                 => fail(s"expected a memory fault, found $other")
    }
  }

  for (copyFirst <- Vec(false, true))
    test(s"bulk copy completion and arrivals jointly release two barrier phases: copy first=$copyFirst") {
      val arrive     = "mbarrier.arrive.expect_tx.release.cta.shared::cta.b64 %token,[bar],64;"
      val copy       = "cp.async.bulk.shared::cta.global.mbarrier::complete_tx::bytes [buffer],[%source],64,[bar];"
      val operations = if (copyFirst) s"$copy\n$arrive" else s"$arrive\n$copy"
      val kernel     = compileKernel(s""".version 8.0
.target sm_90
.address_size 64
.func initialise(.param .u64 address) {
.reg .u64 %address;
ld.param.u64 %address,[address];
mbarrier.init.b64 [%address],16;
ret;
}
.visible .entry copy(.param .u64 data) {
.shared .align 8 .b64 bar;
.shared .align 16 .b8 buffer[64];
.reg .u64 %source,%target,%offset,%token;
.reg .u32 %tid,%value,%phase,%shared;
.reg .pred %leader,%done;
ld.param.u64 %source,[data];
mov.u32 %tid,%tid.x;
setp.eq.u32 %leader,%tid,0;
@!%leader bra INITIALISED;
{
.param .b64 address;
st.param.b64 [address],bar;
call initialise,(address);
}
INITIALISED:
bar.sync 0;
@%leader cp.async.bulk.shared::cta.global.mbarrier::complete_tx::bytes [buffer],[0],0,[bar];
mov.u32 %phase,0;
LOOP:
@!%leader bra ARRIVE_OTHER;
$operations
bra WAIT;
ARRIVE_OTHER:
mbarrier.arrive.shared.b64 %token,[bar];
WAIT:
mbarrier.try_wait.parity.shared::cta.b64 %done,[bar],%phase;
@!%done bra WAIT;
mbarrier.test_wait.shared.b64 %done,[bar],%token;
@!%done trap;
mul.wide.u32 %offset,%tid,4;
mov.u32 %shared,buffer;
mad.lo.u32 %shared,%tid,4,%shared;
ld.shared.u32 %value,[%shared];
add.u64 %target,%source,64;
add.u64 %target,%target,%offset;
st.global.u32 [%target],%value;
bar.sync 0;
add.u64 %source,%source,128;
add.u32 %phase,%phase,1;
setp.lt.u32 %done,%phase,2;
@%done bra LOOP;
@%leader mbarrier.inval.shared.b64 [bar];
ret;
}""")
      val memory = DeviceMemory(256)
      for (index <- 0 until 16) {
        assertEquals(memory.writeInt32(memory.start.offset(index * 4L), index * 7 + 1), Right(()))
        assertEquals(memory.writeInt32(memory.start.offset(128 + index * 4L), index * 11 + 2), Right(()))
      }
      assertEquals(
        execute(
          kernel,
          2,
          16,
          Map(("data": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
          memory
        ),
        Right(())
      )
      for (index <- 0 until 16) {
        assertEquals(memory.readInt32(memory.start.offset(64 + index * 4L)), Right(index * 7 + 1))
        assertEquals(memory.readInt32(memory.start.offset(192 + index * 4L)), Right(index * 11 + 2))
      }
    }
}
