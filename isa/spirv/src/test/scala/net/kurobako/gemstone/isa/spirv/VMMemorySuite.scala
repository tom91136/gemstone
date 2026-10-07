package net.kurobako.gemstone.isa.spirv

import fixtures.*
import java.util.concurrent.Callable
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class VMMemorySuite extends munit.FunSuite {
  private def storage(memory: AddressSpace, globals: Array[Byte] = Array.emptyByteArray): VMMemory.Storage =
    VMMemory.Storage(memory, Vec.empty, globals, _ => Right(()))

  test("concurrent atomics serialise updates to dispatch-owned global storage") {
    val workers    = 4
    val increments = 256
    val shared     = storage(RegionMemory(), Array.ofDim[Byte](4))
    val pointer    =
      VMMemory.Pointer(VMMemory.apertureAddress(VMMemory.GlobalAperture, 0), 0, StorageClass.CrossWorkgroup)
    val start = CyclicBarrier(workers)
    scala.util.Using.resource(threads.Pool(workers)) { pool =>
      val tasks = Vec.tabulate(workers) { _ =>
        val task: Callable[Vec[Long]] = () => {
          val memory = VMMemory.MemoryState(shared)
          val _      = start.await(5, TimeUnit.SECONDS)
          Vec.tabulate(increments)(_ => memory.atomicModifyScalar(pointer, 4)(_ + 1L).value)
        }
        pool.executor.submit(task)
      }
      val previous = tasks.flatMap(_.get(10, TimeUnit.SECONDS))
      assertEquals(previous.sorted, Vec.tabulate(workers * increments)(_.toLong))
      assertEquals(LittleEndian.decodeInt32(shared.globals, 0), workers * increments)
    }
  }

  test("guest stores write through and loads observe other writers") {
    val backing          = RegionMemory()
    val region           = backing.allocate(8200L)
    val memory           = VMMemory.MemoryState(storage(backing))
    val base             = backing.address(region, 0L).value.raw
    def at(offset: Long) = VMMemory.Pointer(base + offset, 0, StorageClass.CrossWorkgroup)
    assertEquals(memory.writeScalar(at(4094L), 0x12345678L, 4), Right(()))
    assertEquals(backing.bytes(region).slice(4094, 4098), LittleEndian.toVec(0x12345678L, 4))
    assertEquals(backing.write(region, 0L, Vec.fill(8200)(9.toByte)), Right(()))
    assertEquals(backing.awaitRead(memory.readScalar(at(4094L), 4)), Right(0x09090909L))
    assertEquals(memory.atomicReadScalar(at(8L), 4), Right(0x09090909L))
  }

  test("kernel stores reach the address space as one write each") {
    val offsets    = Vec[Long >= 0](0L, 8L, 62L, 66L, 4094L, 8191L)
    val parameters = offsets.indices.map(index => s"%out$index")
    val module     = spv"""
      $openCL
      OpEntryPoint Kernel %main "sparse"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %fn = OpTypeFunction %void ${parameters.map(_ => "%uint_ptr").mkString(" ")}
      %value = OpConstant %uint 0x12345678
      %main = OpFunction %void None %fn
      ${parameters.map(parameter => s"$parameter = OpFunctionParameter %uint_ptr").mkString("\n")}
      %entry = OpLabel
      ${parameters.map(parameter => s"OpStore $parameter %value").mkString("\n")}
      %third = OpLoad %uint %out2
      OpStore %out0 %third
      %fifth = OpLoad %uint %out4
      OpStore %out1 %fifth
      OpReturn
      OpFunctionEnd
    """
    val writes = AtomicReference(Vec.empty[(Long, Int)])
    class RecordingMemory extends RegionMemory() {
      override def write(id: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
        super.write(id, offset, bytes).map { _ =>
          val _ = writes.updateAndGet(_ :+ (offset.raw -> bytes.size))
        }
    }
    val memory = RecordingMemory()
    val region = memory.allocate(8200L)
    assertEquals(run(module, "sparse", memory, offsets.map(buffer(region, _))), Right(()))
    assertEquals(writes.get(), offsets.map(_.raw -> 4) ++ Vec(0L -> 4, 8L -> 4))
    val expected = Array.ofDim[Byte](8200)
    offsets.foreach(offset => LittleEndian.write(0x12345678L, expected, offset.raw.toInt, 4))
    assertEquals(memory.bytes(region), Vec.from(expected))
  }
}
