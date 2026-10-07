package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*

class AtomicExecutionSuite extends munit.FunSuite {
  for {
    width  <- Vec(32, 64)
    target <- Vec("generic", "global", "acquire.sys.global", "shared", "generic-shared")
  } test(s"$target $width-bit CAS returns old bits on success and failure") {
    val size      = width / 8
    val shared    = target == "shared" || target == "generic-shared"
    val qualifier = if (target.startsWith("generic")) "" else s"$target."
    val setup     = if (shared) s"mov.u64 %p, scratch; st.shared.b$width [%p], -1;"
    else s"mov.u64 %p, %q; st.global.b$width [%p], -1;"
    val load   = if (shared) "shared" else "global"
    val source = s""".version 8.0
      .target sm_80
      .address_size 64
      .visible .entry compare(.param .u64 output) {
        .shared .align 8 .b8 scratch[8];
        .reg .u64 %p, %q;
        .reg .b$width %old, %value;
        ld.param.u64 %q, [output];
        $setup
        atom.${qualifier}cas.b$width %old, [%p], -1, 7;
        st.global.b$width [%q+$size], %old;
        atom.${qualifier}cas.b$width %old, [%p], -1, 19;
        st.global.b$width [%q+${2 * size}], %old;
        ld.$load.b$width %value, [%p];
        st.global.b$width [%q], %value;
        ret;
      }"""
    val kernel = compileKernel(source)
    val memory = DeviceMemory((3 * size).unsafe[Int >= 0])
    val start  = memory.start
    assertEquals(
      execute(
        kernel,
        1,
        1,
        Map(("output": NonEmpty[String]) -> Argument.fromLong(start.raw)),
        memory
      ),
      Right(())
    )
    val byteWidth = size.unsafe[Int in 1 ~ 8]
    assertEquals(memory.readLittleEndian(start, byteWidth).toOption.get, 7L)
    assertEquals(
      memory.readLittleEndian(start.offset(size.toLong), byteWidth).toOption.get,
      if (width == 32) 0xffffffffL else -1L
    )
    assertEquals(memory.readLittleEndian(start.offset(2L * size), byteWidth).toOption.get, 7L)
  }

  for (width <- Vec(32, 64)) test(s"$width-bit CAS has exactly one winner across lanes") {
    val size   = width / 8
    val source = s""".version 8.0
      .target sm_80
      .address_size 64
      .visible .entry compare(.param .u64 output) {
        .reg .u64 %p, %q, %offset;
        .reg .u32 %lane;
        .reg .b$width %old;
        ld.param.u64 %p, [output];
        mov.u32 %lane, %tid.x;
        add.u32 %lane, %lane, 1;
        mul.wide.u32 %offset, %lane, $size;
        add.u64 %q, %p, %offset;
        atom.global.cas.b$width %old, [%p], 0, 1;
        st.global.b$width [%q], %old;
        ret;
      }"""
    val kernel = compileKernel(source)
    val memory = DeviceMemory((33 * size).unsafe[Int >= 0])
    val start  = memory.start
    assertEquals(
      execute(
        kernel,
        1,
        32,
        Map(("output": NonEmpty[String]) -> Argument.fromLong(start.raw)),
        memory
      ),
      Right(())
    )
    val byteWidth = size.unsafe[Int in 1 ~ 8]
    val observed  = (1 to 32).map(i => memory.readLittleEndian(start.offset(i.toLong * size), byteWidth).toOption.get)
    assertEquals(observed.count(_ == 0L), 1)
    assertEquals(observed.count(_ == 1L), 31)
    assertEquals(memory.readLittleEndian(start, byteWidth).toOption.get, 1L)
  }

  test("generic atom.exch.b32 returns the old value and stores the new value") {
    val source = """.version 8.0
      .target sm_80
      .address_size 64
      .visible .entry exchange(.param .u64 output) {
        .reg .u64 %p, %old_address;
        .reg .b32 %old;
        ld.param.u64 %p, [output];
        atom.exch.b32 %old, [%p], 9;
        add.u64 %old_address, %p, 4;
        st.global.u32 [%old_address], %old;
        ret;
      }"""
    val kernel = compileKernel(source)
    val memory = DeviceMemory(8)
    val start  = memory.start
    assertEquals(memory.writeInt32(start, 3), Right(()))
    assertEquals(
      execute(
        kernel,
        1,
        1,
        Map(("output": NonEmpty[String]) -> Argument.fromLong(start.raw)),
        memory
      ),
      Right(())
    )
    assertEquals(memory.readInt32(start).toOption.get, 9)
    assertEquals(memory.readInt32(start.offset(4)).toOption.get, 3)
  }

  test("generic atom.add.u32 returns the old value and adds") {
    val source = """.version 8.0
      .target sm_80
      .address_size 64
      .visible .entry add(.param .u64 output) {
        .reg .u64 %p, %old_address;
        .reg .u32 %old;
        ld.param.u64 %p, [output];
        atom.add.u32 %old, [%p], 2;
        add.u64 %old_address, %p, 4;
        st.global.u32 [%old_address], %old;
        ret;
      }"""
    val kernel = compileKernel(source)
    val memory = DeviceMemory(8)
    val start  = memory.start
    assertEquals(memory.writeInt32(start, 3), Right(()))
    assertEquals(
      execute(
        kernel,
        1,
        1,
        Map(("output": NonEmpty[String]) -> Argument.fromLong(start.raw)),
        memory
      ),
      Right(())
    )
    assertEquals(memory.readInt32(start).toOption.get, 5)
    assertEquals(memory.readInt32(start.offset(4)).toOption.get, 3)
  }

  test("generic atom.min.u32 returns the old value and stores the minimum") {
    val source = """.version 8.0
      .target sm_80
      .address_size 64
      .visible .entry minimum(.param .u64 output) {
        .reg .u64 %p, %old_address;
        .reg .u32 %old;
        ld.param.u64 %p, [output];
        atom.min.u32 %old, [%p], 3;
        add.u64 %old_address, %p, 4;
        st.global.u32 [%old_address], %old;
        ret;
      }"""
    val kernel = compileKernel(source)
    val memory = DeviceMemory(8)
    val start  = memory.start
    assertEquals(memory.writeInt32(start, 7), Right(()))
    assertEquals(
      execute(
        kernel,
        1,
        1,
        Map(("output": NonEmpty[String]) -> Argument.fromLong(start.raw)),
        memory
      ),
      Right(())
    )
    assertEquals(memory.readInt32(start).toOption.get, 3)
    assertEquals(memory.readInt32(start.offset(4)).toOption.get, 7)
  }

  test("generic maximum and bitwise atomics return each previous value") {
    val source = """.version 8.0
      .target sm_80
      .address_size 64
      .visible .entry atomic_bits(.param .u64 output) {
        .reg .u64 %p, %old_address;
        .reg .u32 %old;
        ld.param.u64 %p, [output];
        atom.max.u32 %old, [%p], 19;
        add.u64 %old_address, %p, 4;
        st.global.u32 [%old_address], %old;
        atom.and.b32 %old, [%p], 31;
        add.u64 %old_address, %p, 8;
        st.global.u32 [%old_address], %old;
        atom.or.b32 %old, [%p], 32;
        add.u64 %old_address, %p, 12;
        st.global.u32 [%old_address], %old;
        atom.xor.b32 %old, [%p], 3;
        add.u64 %old_address, %p, 16;
        st.global.u32 [%old_address], %old;
        ret;
      }"""
    val kernel = compileKernel(source)
    val memory = DeviceMemory(20)
    val start  = memory.start
    assertEquals(memory.writeInt32(start, 7), Right(()))
    assertEquals(
      execute(
        kernel,
        1,
        1,
        Map(("output": NonEmpty[String]) -> Argument.fromLong(start.raw)),
        memory
      ),
      Right(())
    )
    assertEquals(
      (0 to 4).map(index => memory.readInt32(start.offset(index.toLong * 4L)).toOption.get),
      Vec(48, 7, 19, 19, 51)
    )
  }

  for {
    space  <- Vec("global", "shared")
    width  <- Vec(32, 64)
    groups <- Vec[Int > 0](1, 5)
  }
    test(s"unsigned atomic min $space/$width over $groups groups preserves high-bit ordering") {
      val memory                  = DeviceMemory((groups * 2 + 1) * 8, GuestAddress(0x100000L))
      val max                     = if (width == 64) -1L else 0xffffffffL
      val byteWidth: Int in 1 ~ 8 = if (width == 64) 8 else 4
      memory.writeLittleEndian(memory.start, max, byteWidth).toOption.get
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
mul.wide.u32 %offset, %group, 16;
add.u64 %address, %out, %offset;
add.u64 %address, %address, 8;
st.global.u$width [%address], %result;
add.u64 %address, %address, 8;
st.global.u$width [%address], %old;
ret;
}
"""
      val kernel = compileKernel(body)
      val result = execute(
        kernel,
        groups,
        1,
        Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
        memory
      )
      assert(result.isRight, result.toString)
      for (group <- 0 until groups.raw)
        assertEquals(memory.readLittleEndian(memory.start.offset(8 + group * 16), byteWidth), Right(1L))
      val previous = Vec.tabulate(groups.raw) { group =>
        memory.readLittleEndian(memory.start.offset(16 + group * 16), byteWidth).toOption.get
      }
      val expected = if (space == "shared") Vec.fill(groups.raw)(max)
      else max +: Vec.fill(groups.raw - 1)(1L)
      assertEquals(previous.sorted, expected.sorted)
    }

}
