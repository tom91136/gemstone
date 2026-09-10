package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.cuda.*
import net.kurobako.gemstone.execution.ExecutionControl
import net.kurobako.gemstone.execution.ExecutionLimits
import net.kurobako.gemstone.memory.*

import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

final class CUDAParallelismSuite extends munit.FunSuite {
  private def value[A](result: CUDAResult[A]): A = result match {
    case CUDAResult.Success(result) => result
    case CUDAResult.Failure(error)  => fail(error.message)
  }

  test("CUDA launches use the supplied workers and reset their budget between launches") {
    val pool    = BoundedExecutionPool(4, minGroups = 2L)
    val reached = CountDownLatch(4)
    val threads = ConcurrentHashMap.newKeySet[String]()
    val groups  = 4097
    val storage = ArrayCheckedMemory.allocate(4 * groups)
    final class TrackingMemory extends CheckedMemory {
      private def recordWorker(): Unit =
        if (threads.add(Thread.currentThread().getName)) {
          reached.countDown()
          assert(reached.await(10L, TimeUnit.SECONDS), "CUDA work did not reach all four workers")
        }
      override def allocation: Allocation                       = storage.allocation
      override def free(): Unit                                 = storage.free()
      override def readInt8(address: GuestAddress)              = storage.readInt8(address)
      override def readInt32(address: GuestAddress)             = storage.readInt32(address)
      override def writeInt8(address: GuestAddress, byte: Byte) = {
        recordWorker()
        storage.writeInt8(address, byte)
      }
      override def writeInt32(address: GuestAddress, number: Int) = {
        recordWorker()
        storage.writeInt32(address, number)
      }
    }
    val memory  = TrackingMemory()
    val backend = PTXCUDAKernelExecution(() =>
      ExecutionControl.create(ExecutionLimits(maxInstructions = 30000L, quantum = 1), parallelism = pool)
    )
    val source = """.version 8.0
      .target sm_80
      .address_size 64
      .visible .entry _ZN3cub_counting_iterator_for_each(.param .u64 output) {
        .reg .u64 %base, %offset, %address;
        .reg .u32 %index;
        ld.param.u64 %base, [output];
        mov.u32 %index, %ctaid.x;
        mul.wide.u32 %offset, %index, 4;
        add.u64 %address, %base, %offset;
        st.global.u32 [%address], %index;
        ret;
      }"""
    try {
      val module   = value(backend.cuModuleLoadData(ModuleImage(Col.from(source.getBytes(StandardCharsets.UTF_8)))))
      val function = value(backend.cuModuleGetFunction(module, FunctionName("_ZN3cub_counting_iterator_for_each")))
      val launch   = KernelLaunch(
        UInt32(groups),
        UInt32(1),
        UInt32(1),
        UInt32(1),
        UInt32(1),
        UInt32(1),
        UInt32(0),
        Col(KernelArgument(net.kurobako.gemstone.binary.LittleEndian.toCol(storage.allocation.start.value, 8)))
      )
      (0 until 2).foreach { _ =>
        assertEquals(backend.cuLaunchKernel(function.handle, launch, memory), CUDAResult.Success(()))
        (0 until groups).foreach(index =>
          assertEquals(storage.readInt32(storage.allocation.start.offset(index * 4L)), Right(index))
        )
      }
      assertEquals(threads.size(), 4)
    } finally pool.close()
  }
}
