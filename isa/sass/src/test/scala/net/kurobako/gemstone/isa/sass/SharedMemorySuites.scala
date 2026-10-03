package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class SharedMemorySuites extends munit.FunSuite {
  {
    def fixtureModule: Cubin.Module = SASSFixture.module(
      86,
      Vec(SASSFixture.Kernel("probe", 352, Vec((0, 8), (8, 4), (12, 4)), registers = 10, shared = 512))
    )

    def scalar(value: Int): KernelTask.Argument.Scalar =
      KernelTask.Argument.Scalar(LittleEndian.toVec(value.toLong, 4))

    test("sm_86 LDS adds one uniform word and wraps the scaled shared address at 32 bits") {
      val module = fixtureModule
      val sass   = SharedMemoryFixtures.sharedUniformAddressSm86Patched
      assert(sass.contains("LDS R5, [R7.X4+UR6+0x80]"))
      val function = ListingDecoder.parse(sass, "probe", 86).fold(fail(_), _.head)
      val lowered  = SASSFixture(module, module.kernels.head, Vec(function))
      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate(128L)
        val program = service.compile(lowered)
          .fold(e => fail(e.message), identity)
        def task(addressLow: Int, addressHigh: Int) = SASSFixture.task(
          "probe": NonEmpty[String],
          LaunchGeometry.Dimensions(32, 1, 1),
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            scalar(addressLow),
            scalar(addressHigh)
          )
        )

        def resultFor(low: Int, high: Int): Vec[Long] = {
          service.run(program, task(low, high)).fold(e => fail(e.message), identity)

          val bytes = service.read(output, 0L, 128).toOption.get
          Vec.tabulate(32)(i => LittleEndian.decode(bytes, i * 4, 4))
        }
        assertEquals(resultFor(0, 0), Vec.tabulate(32)(i => 1100L + i))
        assertEquals(resultFor(0, 0x12345678), Vec.tabulate(32)(i => 1100L + i))
        assertEquals(resultFor(-128, 0), Vec.tabulate(32)(i => 100L + i))
        assertEquals(resultFor(-128, -1), Vec.tabulate(32)(i => 100L + i))
      }
    }
  }

  {
    def fixtureModule: Cubin.Module = SASSFixture.module(
      75,
      Vec(SASSFixture.Kernel("probe", 352, Vec((0, 8), (8, 4), (12, 4)), registers = 10, shared = 512))
    )

    def scalar(value: Int): KernelTask.Argument.Scalar =
      KernelTask.Argument.Scalar(LittleEndian.toVec(value.toLong, 4))

    def verify(sass: String, instruction: String, expected: Int => Long): Unit = {
      val module = fixtureModule
      assert(sass.contains(instruction))
      val function = ListingDecoder.parse(sass, "probe", 75).fold(fail(_), _.head)
      val lowered  = SASSFixture(module, module.kernels.head, Vec(function))

      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate(128L)
        val program = service.compile(lowered)
          .fold(e => fail(e.message), identity)
        def resultFor(low: Int, high: Int): Vec[Long] = {
          val task = SASSFixture.task(
            "probe": NonEmpty[String],
            LaunchGeometry.Dimensions(32, 1, 1),
            LaunchGeometry.Dimensions.One,
            Vec(
              KernelTask.Argument.Memory(output, 0L),
              scalar(low),
              scalar(high)
            )
          )
          service.run(program, task).fold(e => fail(e.message), identity)

          val bytes = service.read(output, 0L, 128).toOption.get
          Vec.tabulate(32)(i => LittleEndian.decode(bytes, i * 4, 4).toInt.toLong)
        }

        assertEquals(resultFor(0, 0), Vec.tabulate(32)(i => expected(200 + i)))
        assertEquals(resultFor(0, 0x12345678), Vec.tabulate(32)(i => expected(200 + i)))
        assertEquals(resultFor(-128, 0), Vec.tabulate(32)(i => expected(100 + i)))
        assertEquals(resultFor(-128, -1), Vec.tabulate(32)(i => expected(100 + i)))
      }
    }

    def signedByte(value: Int): Long =
      (if (value < 128) value else value - 256).toLong

    test("sm_75 LDS.U.S8 adds the uniform address and wraps shared offsets at 32 bits") {
      verify(
        SharedMemoryFixtures.sharedUniformAddressSm75Patched,
        "LDS.U.S8 R5, [R7.X4+UR6+0x80]",
        signedByte(_)
      )
    }

    test("sm_75 LDS.U.U8 adds the uniform address and wraps shared offsets at 32 bits") {
      verify(
        SharedMemoryFixtures.sharedUniformAddressSm75PatchedU8,
        "LDS.U.U8 R5, [R7.X4+UR6+0x80]",
        _.toLong
      )
    }
  }

  {
    def fixtureModule: Cubin.Module = SASSFixture.module(
      86,
      Vec(SASSFixture.Kernel("probe", 352, Vec((0, 8), (8, 8), (16, 4), (20, 4)), registers = 16, shared = 256))
    )

    def scalar(value: Int): KernelTask.Argument.Scalar =
      KernelTask.Argument.Scalar(LittleEndian.toVec(value.toLong, 4))

    test("sm_86 LDGSTS wraps a scalar uniform offset in its shared destination") {
      val module = fixtureModule
      val sass   = SharedMemoryFixtures.sharedUniformCopySm86Patched
      assert(sass.contains("LDGSTS.E [R7+UR6+0x80], [R2.64]"))
      val function = ListingDecoder.parse(sass, "probe", 86).fold(fail(_), _.head)
      val lowered  = SASSFixture(module, module.kernels.head, Vec(function))
      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate(256L)
        val input   = service.allocate(128L)
        val values  = Vec.from((2100 until 2132).flatMap(i => LittleEndian.toVec(i.toLong, 4)))
        assert(service.write(input, 0L, values).isRight)
        val program = service.compile(lowered)
          .fold(e => fail(e.message), identity)
        def task(addressLow: Int, addressHigh: Int) = SASSFixture.task(
          "probe": NonEmpty[String],
          LaunchGeometry.Dimensions(32, 1, 1),
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Memory(input, 0L),
            scalar(addressLow),
            scalar(addressHigh)
          )
        )

        def resultFor(low: Int, high: Int): Vec[Long] = {
          service.run(program, task(low, high)).fold(e => fail(e.message), identity)

          val bytes = service.read(output, 0L, 256).toOption.get
          Vec.tabulate(64)(i => LittleEndian.decode(bytes, i * 4, 4))
        }
        assertEquals(resultFor(0, 0), Vec.tabulate(32)(i => 100L + i) ++ Vec.tabulate(32)(i => 2100L + i))
        assertEquals(resultFor(0, 0x12345678), Vec.tabulate(32)(i => 100L + i) ++ Vec.tabulate(32)(i => 2100L + i))
        assertEquals(resultFor(-128, 0), Vec.tabulate(32)(i => 2100L + i) ++ Vec.tabulate(32)(i => 1100L + i))
        assertEquals(resultFor(-128, -1), Vec.tabulate(32)(i => 2100L + i) ++ Vec.tabulate(32)(i => 1100L + i))
      }
    }
  }

  {
    def fixtureModule: Cubin.Module = SASSFixture.module(
      86,
      Vec(SASSFixture.Kernel("probe", 352, Vec((0, 8), (8, 4), (12, 4)), registers = 14, shared = 512))
    )

    def scalar(value: Int): KernelTask.Argument.Scalar =
      KernelTask.Argument.Scalar(LittleEndian.toVec(value.toLong, 4))

    test("sm_86 STS.U8 wraps a scalar uniform offset and ignores the adjacent uniform register") {
      val module = fixtureModule
      val sass   = SharedMemoryFixtures.sharedUniformStoreSm86Patched
      assert(sass.contains("STS.U8 [R9+UR6+0x80], R4"))
      val function = ListingDecoder.parse(sass, "probe", 86).fold(fail(_), _.head)
      val lowered  = SASSFixture(module, module.kernels.head, Vec(function))
      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate(256L)
        val program = service.compile(lowered)
          .fold(e => fail(e.message), identity)
        def task(addressLow: Int, addressHigh: Int) = SASSFixture.task(
          "probe": NonEmpty[String],
          LaunchGeometry.Dimensions(32, 1, 1),
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            scalar(addressLow),
            scalar(addressHigh)
          )
        )

        def resultFor(low: Int, high: Int): Vec[Long] = {
          service.run(program, task(low, high)).fold(e => fail(e.message), identity)

          val bytes = service.read(output, 0L, 256).toOption.get
          Vec.tabulate(64)(i => LittleEndian.decode(bytes, i * 4, 4))
        }
        assertEquals(resultFor(0, 0), Vec.tabulate(32)(i => 100L + i) ++ Vec.tabulate(32)(i => 132L + i))
        assertEquals(resultFor(0, 0x12345678), Vec.tabulate(32)(i => 100L + i) ++ Vec.tabulate(32)(i => 132L + i))
        assertEquals(resultFor(-128, 0), Vec.tabulate(32)(i => 132L + i) ++ Vec.tabulate(32)(i => 100L + i))
        assertEquals(resultFor(-128, -1), Vec.tabulate(32)(i => 132L + i) ++ Vec.tabulate(32)(i => 100L + i))
      }
    }
  }

  {
    def fixtureModule: Cubin.Module = SASSFixture.module(
      89,
      Vec(SASSFixture.Kernel("probe", 352, Vec((0, 8), (8, 4), (12, 4)), registers = 23, shared = 1024))
    )

    def scalar(value: Int): KernelTask.Argument.Scalar =
      KernelTask.Argument.Scalar(LittleEndian.toVec(value.toLong, 4))

    test("sm_89 STS.128 wraps a scalar uniform offset and ignores the adjacent uniform register") {
      val module = fixtureModule
      val sass   = SharedMemoryFixtures.sharedUniformVectorStoreSm89Patched
      assert(sass.contains("STS.128 [R0.X16+UR6+0x200], R8"))
      val function = ListingDecoder.parse(sass, "probe", 89).fold(fail(_), _.head)
      val lowered  = SASSFixture(module, module.kernels.head, Vec(function))
      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate(1024L)
        val program = service.compile(lowered)
          .fold(e => fail(e.message), identity)
        def task(addressLow: Int, addressHigh: Int) = SASSFixture.task(
          "probe": NonEmpty[String],
          LaunchGeometry.Dimensions(32, 1, 1),
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            scalar(addressLow),
            scalar(addressHigh)
          )
        )

        def resultFor(low: Int, high: Int): Vec[Long] = {
          service.run(program, task(low, high)).fold(e => fail(e.message), identity)

          val bytes = service.read(output, 0L, 1024).toOption.get
          Vec.tabulate(256)(i => LittleEndian.decode(bytes, i * 4, 4))
        }
        def expected(updatedBlock: Int) = (0 until 2).toVec.flatMap { block =>
          (0 until 32).toVec.flatMap { lane =>
            Vec.tabulate(4)(word => lane.toLong + 100L * (word + 1) + (if (block == updatedBlock) 1000L else 0L))
          }
        }
        assertEquals(resultFor(0, 0), expected(1))
        assertEquals(resultFor(0, 0x12345678), expected(1))
        assertEquals(resultFor(-512, 0), expected(0))
        assertEquals(resultFor(-512, -1), expected(0))
      }
    }
  }

  {
    def fixtureModule: Cubin.Module = SASSFixture.module(
      89,
      Vec(SASSFixture.Kernel("probe", 352, Vec((0, 8), (8, 4), (12, 4)), registers = 14, shared = 512))
    )

    def scalar(value: Int): KernelTask.Argument.Scalar =
      KernelTask.Argument.Scalar(LittleEndian.toVec(value.toLong, 4))

    test("sm_89 STS wraps a scalar uniform offset and ignores the adjacent uniform register") {
      val module = fixtureModule
      val sass   = SharedMemoryFixtures.sharedUniformWordStoreSm89Patched
      assert(sass.contains("STS [R9.X4+UR6+0x80], R4"))
      val function = ListingDecoder.parse(sass, "probe", 89).fold(fail(_), _.head)
      val lowered  = SASSFixture(module, module.kernels.head, Vec(function))
      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate(256L)
        val program = service.compile(lowered)
          .fold(e => fail(e.message), identity)
        def task(addressLow: Int, addressHigh: Int) = SASSFixture.task(
          "probe": NonEmpty[String],
          LaunchGeometry.Dimensions(32, 1, 1),
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            scalar(addressLow),
            scalar(addressHigh)
          )
        )

        def resultFor(low: Int, high: Int): Vec[Long] = {
          service.run(program, task(low, high)).fold(e => fail(e.message), identity)

          val bytes = service.read(output, 0L, 256).toOption.get
          Vec.tabulate(64)(i => LittleEndian.decode(bytes, i * 4, 4))
        }
        assertEquals(resultFor(0, 0), Vec.tabulate(32)(i => 100L + i) ++ Vec.tabulate(32)(i => 132L + i))
        assertEquals(resultFor(0, 0x12345678), Vec.tabulate(32)(i => 100L + i) ++ Vec.tabulate(32)(i => 132L + i))
        assertEquals(resultFor(-128, 0), Vec.tabulate(32)(i => 132L + i) ++ Vec.tabulate(32)(i => 100L + i))
        assertEquals(resultFor(-128, -1), Vec.tabulate(32)(i => 132L + i) ++ Vec.tabulate(32)(i => 100L + i))
      }
    }
  }

  {
    def fixtureModule: Cubin.Module = SASSFixture.module(
      86,
      Vec(SASSFixture.Kernel("probe", 352, Vec((0, 8), (8, 8), (16, 4), (20, 4)), registers = 12, shared = 128))
    )

    def scalar(value: Int): KernelTask.Argument.Scalar =
      KernelTask.Argument.Scalar(LittleEndian.toVec(value.toLong, 4))

    test("compiler-produced sm_86 LDGSTS adds the 64-bit UR pair to its global base") {
      val module = fixtureModule
      val sass   = GenericMemoryFixtures.uniformAddressSm86Patched
      assert(sass.contains("LDGSTS.E [R7], [R2.64+UR6]"))
      assert(sass.contains("ULDC.64 UR6, c[0x0][0x170]"))
      val function = ListingDecoder.parse(sass, "probe", 86).fold(fail(_), _.head)
      val lowered  = SASSFixture(module, module.kernels.head, Vec(function))
      assert(lowered.instructions.exists {
        case Instruction.Load(_, _, width, _, MemoryAccess.Global(false)) if width.raw == 32 => true
        case _                                                                               => false
      })
      assert(lowered.instructions.exists {
        case Instruction.Store(_, _, width, MemoryAccess.Shared) if width.raw == 32 => true
        case _                                                                      => false
      })

      locally {
        val service    = SASSFixture.Execution()
        val input      = service.allocate(144L)
        val output     = service.allocate(128L)
        val inputBytes = Vec.from((0 until 36).flatMap(i =>
          LittleEndian.toVec(0x12340000L + i, 4)
        ))
        assert(service.write(input, 0L, inputBytes).isRight)
        val program = service.compile(lowered)
          .fold(e => fail(e.message), identity)
        def task(inputOffset: Long, addressLow: Int, addressHigh: Int) = SASSFixture.task(
          "probe": NonEmpty[String],
          LaunchGeometry.Dimensions(32, 1, 1),
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Memory(input, inputOffset.unsafe[Long >= 0]),
            scalar(addressLow),
            scalar(addressHigh)
          )
        )

        def resultFor(inputOffset: Long, low: Int, high: Int): Vec[Long] = {
          service.run(program, task(inputOffset, low, high)).fold(e => fail(e.message), identity)

          val bytes = service.read(output, 0L, 128).toOption.get
          Vec.tabulate(32)(i => LittleEndian.decode(bytes, i * 4, 4))
        }
        // The nonzero low half exercises the address offset while keeping the high half zero.
        assertEquals(resultFor(0L, 8, 0), Vec.tabulate(32)(i => 0x12340002L + i))
        // Negative two's-complement UR6:UR7 is tested with a +4-byte base adjustment so
        // the wrapped address still names the start of the allocation in the VM.
        assertEquals(resultFor(4L, -4, -1), Vec.tabulate(32)(i => 0x12340000L + i))
      }
    }
  }
}
