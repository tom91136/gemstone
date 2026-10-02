package net.kurobako.gemstone.isa.ptx.v9_3

import execution.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.ptx.*
import net.kurobako.gemstone.memory.*

class CompilerExecutionSuite extends munit.FunSuite {
  private def scalar32(value: Int): Argument = Argument(LittleEndian.toVec(value.toLong, 4).unsafe)

  test("compiler SAXPY PTX executes float multiply-add for each lane") {
    val kernel = compileKernel(CompilerFixtures.saxpySm35Ptx)
    val memory = ArrayCheckedMemory.allocate(32)
    val start  = memory.allocation.start
    val inputY = Vec(10f, 20f, 30f, 40f)
    val inputX = Vec(1f, 2f, 3f, 4f)
    inputY.indices.foreach { index =>
      val offset = start.offset(index.toLong * 4L)
      assertEquals(memory.writeInt32(offset, java.lang.Float.floatToRawIntBits(inputY(index))), Right(()))
      assertEquals(memory.writeInt32(offset.offset(16L), java.lang.Float.floatToRawIntBits(inputX(index))), Right(()))
    }
    val result = execute(
      kernel,
      1,
      4,
      Map(
        ("saxpy_param_0": NonEmpty[String]) -> Argument.fromLong(start.raw),
        ("saxpy_param_1": NonEmpty[String]) -> Argument.fromLong(start.raw + 16L),
        ("saxpy_param_2": NonEmpty[String]) -> scalar32(java.lang.Float.floatToRawIntBits(2f)),
        ("saxpy_param_3": NonEmpty[String]) -> scalar32(4)
      ),
      memory
    )
    assertEquals(result, Right(()))
    assertEquals(
      inputY.indices.map(index =>
        java.lang.Float.intBitsToFloat(memory.readInt32(start.offset(index.toLong * 4L)).toOption.get)
      ),
      Vec(12f, 24f, 36f, 48f)
    )
  }

  test("compiler noinline device-call PTX passes arguments and returns per-lane values") {
    val kernel = compileKernel(CompilerFixtures.deviceCallSm35Ptx)
    val memory = ArrayCheckedMemory.allocate(16)
    val start  = memory.allocation.start
    val result = execute(
      kernel,
      1,
      4,
      Map(
        ("device_call_param_0": NonEmpty[String]) -> Argument.fromLong(start.raw),
        ("device_call_param_1": NonEmpty[String]) -> scalar32(7)
      ),
      memory
    )
    assertEquals(result, Right(()))
    assertEquals(
      Vec.tabulate(4)(index => memory.readInt32(start.offset(index.toLong * 4L)).toOption.get),
      Vec(26, 29, 32, 35)
    )
  }

  test("compiler shared and local memory PTX returns each lane's incremented value") {
    val kernel = compileKernel(CompilerFixtures.sharedLocalSm89Ptx)
    val memory = ArrayCheckedMemory.allocate(256)
    val start  = memory.allocation.start
    val output = start.offset(128L)
    Vec.tabulate(32)(identity).foreach { index =>
      assertEquals(memory.writeInt32(start.offset(index.toLong * 4L), index), Right(()))
    }
    val result = execute(
      kernel,
      1,
      32,
      Map(
        ("shared_local_param_0": NonEmpty[String]) -> Argument.fromLong(output.raw),
        ("shared_local_param_1": NonEmpty[String]) -> Argument.fromLong(start.raw)
      ),
      memory
    )
    assertEquals(result, Right(()))
    assertEquals(
      Vec.tabulate(32)(index => memory.readInt32(output.offset(index.toLong * 4L)).toOption.get),
      Vec.tabulate(32)(_ + 1)
    )
  }

  test("compiler packed half PTX adds independent half lanes") {
    val kernel = compileKernel(CompilerFixtures.packedSm89Ptx)
    val memory = ArrayCheckedMemory.allocate(48)
    val start  = memory.allocation.start
    val left   = start
    val right  = start.offset(16L)
    val output = start.offset(32L)
    (0 until 4).foreach { index =>
      assertEquals(memory.writeInt32(left.offset(index.toLong * 4L), 0xc0003e00), Right(()))
      assertEquals(memory.writeInt32(right.offset(index.toLong * 4L), 0x44004080), Right(()))
    }
    val result = execute(
      kernel,
      1,
      4,
      Map(
        ("packed_add_param_0": NonEmpty[String]) -> Argument.fromLong(output.raw),
        ("packed_add_param_1": NonEmpty[String]) -> Argument.fromLong(left.raw),
        ("packed_add_param_2": NonEmpty[String]) -> Argument.fromLong(right.raw)
      ),
      memory
    )
    assertEquals(result, Right(()))
    assertEquals(
      Vec.tabulate(4)(index => memory.readInt32(output.offset(index.toLong * 4L)).toOption.get),
      Vec.fill(4)(0x40004380)
    )
  }
}
