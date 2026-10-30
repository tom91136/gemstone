package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import VMIR.InstructionSource.*
import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

/** ROCm runs every generation with SH_MEM_CONFIG's unaligned alignment mode, so loads and stores at any byte address
  * access exactly those bytes while a misaligned atomic faults; SMEM drops the low two bits of a dword address. A
  * gfx1036 under the Linux 7.2 KFD behaves so: misaligned global, flat and DS atomics abort the queue with
  * HSA_STATUS_ERROR_MEMORY_APERTURE_VIOLATION, and `s_load_dword` at offsets 1 and 6 reads the dwords at 0 and 4.
  */
class AlignmentSuite extends Suite {
  private val targets = Vec(Target("gfx803"), Target("gfx90a"), Target("gfx942"), Target("gfx1100"), Target("gfx1201"))
  private val widths  = Vec[Int in (4, 8)](4, 8)

  /** The 64 ascending bytes of the input and the 64 bytes of the output after `operations`, with s[0:1] and v[0:1]
    * holding the input's address plus `input`, s[2:3] and v[2:3] the output's plus `output`, and v8 the LDS offset
    * `local`.
    */
  private def run(target: Target, operations: Vec[Instruction], input: Int = 0, output: Int = 0, local: Int = 0)
      : Either[Fault, (Vec[Byte], Vec[Byte])] = {
    val memory = RegionMemory()
    val in     = memory.allocate(Vec.tabulate(64)(_.toByte))
    val out    = memory.allocate(Vec.fill(64)(0.toByte))
    val setup  = Vec(
      Instruction.SMove64(0, Constant(memory.address(in, 0L).value.raw + input)),
      Instruction.SMove64(2, Constant(memory.address(out, 0L).value.raw + output)),
      Instruction.VMove(0, Scalar(0)),
      Instruction.VMove(1, Scalar(1)),
      Instruction.VMove(2, Scalar(2)),
      Instruction.VMove(3, Scalar(3)),
      Instruction.VMove(8, Constant(local.toLong))
    )
    runIR(setup ++ operations :+ Instruction.End, memory, codeKernel(target = target, groupSegmentSize = 64))
      .map(_ => (Vec.unsafeWrapArray(memory.array(in).clone()), Vec.unsafeWrapArray(memory.array(out).clone())))
  }

  private def misaligned(result: Either[Fault, ?]): Boolean =
    result.left.exists { case Fault.Misaligned(_) => true; case _ => false }

  for (target <- targets) {
    test(s"$target loads and stores the exact bytes at any global or flat offset") {
      val copied = run(
        target,
        Vec(
          Instruction.Load(4, MemoryAddress.Global(0, None, 0), 16, false, LoadPlacement.Full),
          Instruction.Store(4, MemoryAddress.Flat(2, 0), 16, false),
          Instruction.Load(4, MemoryAddress.Flat(0, 16), 8, false, LoadPlacement.Full),
          Instruction.Store(4, MemoryAddress.Global(2, None, 16), 8, false),
          Instruction.Load(4, MemoryAddress.Global(0, None, 32), 2, false, LoadPlacement.Full),
          Instruction.Store(4, MemoryAddress.Global(2, None, 32), 4, false)
        ),
        input = 1,
        output = 3
      ).value._2
      assertEquals(copied.slice(3, 27), Vec.tabulate(24)(index => (index + 1).toByte))
      assertEquals(copied.slice(35, 39), Vec[Byte](33, 34, 0, 0))
    }

    test(s"$target LDS loads and stores the exact bytes at any offset") {
      val copied = run(
        target,
        Vec(
          Instruction.Load(4, MemoryAddress.Global(0, None, 0), 16, false, LoadPlacement.Full),
          Instruction.Store(4, MemoryAddress.Local(8, 0), 16, false),
          Instruction.Load(12, MemoryAddress.Local(8, 0), 8, false, LoadPlacement.Full),
          Instruction.Store(12, MemoryAddress.Global(2, None, 0), 8, false)
        ),
        local = 5
      ).value._2
      assertEquals(copied.take(8), Vec.tabulate(8)(_.toByte))
    }

    for (bytes <- widths) {
      def atomics(offset: Int) = Vec(
        Instruction.Atomic(AtomicOperation.Add, 4, MemoryAddress.Global(0, None, offset), 6, bytes, true),
        Instruction.Atomic(AtomicOperation.Exchange, 4, MemoryAddress.Flat(0, offset), 6, bytes, false),
        Instruction.CompareSwap(4, MemoryAddress.Global(0, None, offset), 6, 10, bytes, true),
        Instruction.Atomic(AtomicOperation.Add, 4, MemoryAddress.Local(8, offset), 6, bytes, true)
      )
      for (atomic <- atomics(0))
        test(s"$target $bytes-byte ${atomic.productPrefix} at a multiple of its size runs") {
          assert(run(target, Vec(atomic), input = bytes).isRight)
        }
      for (atomic <- atomics(bytes / 2))
        test(s"$target $bytes-byte ${atomic.productPrefix} at an odd multiple of ${bytes / 2} bytes faults") {
          assert(misaligned(run(target, Vec(atomic))))
        }
    }

    test(s"$target scalar dword loads ignore the low two address bits, narrower ones read the exact bytes") {
      def loaded(input: Int, elementBytes: Int in (1, 2, 4)): Vec[Byte] = run(
        target,
        Vec(
          Instruction.SLoad(10, 1, 0, 0L, None, elementBytes),
          Instruction.VMove(4, Scalar(10)),
          Instruction.Store(4, MemoryAddress.Global(2, None, 0), 4, false)
        ),
        input = input
      ).value._2.take(4)
      assertEquals(loaded(1, 4), Vec[Byte](0, 1, 2, 3))
      assertEquals(loaded(6, 4), Vec[Byte](4, 5, 6, 7))
      assertEquals(loaded(3, 1), Vec[Byte](3, 0, 0, 0))
      assertEquals(loaded(3, 2), Vec[Byte](3, 4, 0, 0))
    }
  }
}
