package net.kurobako.gemstone.isa.spirv

import dotty.tools.dotc.Driver
import dotty.tools.dotc.reporting.StoreReporter
import java.nio.file.Files
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.testing.Suite
import scala.concurrent.duration.*

class RenderedMachineSuite extends Suite {
  // each test runs the Scala compiler over every rendered form of a family
  override def baseTimeout: FiniteDuration = 3.minutes

  private def errors(source: String): List[String] = {
    val directory = Files.createTempDirectory("gemstone-rendered-")
    try {
      val file = directory.resolve("Rendered.scala")
      Files.writeString(file, s"package net.kurobako.gemstone.isa.spirv\n\n$source\n")
      val output   = Files.createDirectory(directory.resolve("classes"))
      val reporter = StoreReporter()
      val _        = Driver().process(
        Array(
          "-classpath",
          sys.props("gemstone.spirv.classpath"),
          "-d",
          output.toString,
          "-color:never",
          file.toString
        ),
        reporter
      )
      reporter.allErrors.map(_.message)
    } finally {
      val paths = Files.walk(directory)
      try paths.sorted(java.util.Comparator.reverseOrder()).forEach(path => Files.delete(path))
      finally paths.close()
    }
  }

  test("the compiler rejects ill-typed source") {
    assertEquals(errors("def broken(): Int = \"text\"").size, 1)
  }

  test("rendered memory instructions compile") {
    Vec("OpLoad", "OpStore", "OpCopyMemorySized").foreach { opcode =>
      MachineSemantics.machine(opcode).foreach { (_, outcome) =>
        val definition = outcome.fold(failure => fail(s"$opcode: $failure"), identity)
        assertEquals(errors(ScalaBackend.source(definition)), Nil, opcode)
      }
    }
  }

  test("rendered copies and atomic accesses compile") {
    Vec("OpCopyMemory", "OpAtomicLoad", "OpAtomicStore", "OpAtomicFlagTestAndSet", "OpAtomicFlagClear").foreach {
      opcode =>
        MachineSemantics.machine(opcode).foreach { (_, outcome) =>
          val definition = outcome.fold(failure => fail(s"$opcode: $failure"), identity)
          assertEquals(errors(ScalaBackend.source(definition)), Nil, opcode)
        }
    }
  }

  test("rendered group copies and event waits compile") {
    Vec("OpGroupAsyncCopy", "OpUntypedGroupAsyncCopyKHR", "OpGroupWaitEvents").foreach { opcode =>
      MachineSemantics.machine(opcode).foreach { (_, outcome) =>
        val definition = outcome.fold(failure => fail(s"$opcode: $failure"), identity)
        assertEquals(errors(ScalaBackend.source(definition)), Nil, opcode)
      }
    }
  }

  test("rendered device enqueue instructions compile") {
    Vec(
      "OpGetDefaultQueue",
      "OpBuildNDRange",
      "OpEnqueueKernel",
      "OpEnqueueMarker",
      "OpRetainEvent",
      "OpReleaseEvent",
      "OpCreateUserEvent",
      "OpIsValidEvent",
      "OpSetUserEventStatus",
      "OpCaptureEventProfilingInfo",
      "OpGetKernelWorkGroupSize",
      "OpGetKernelPreferredWorkGroupSizeMultiple",
      "OpGetKernelMaxNumSubgroups",
      "OpGetKernelLocalSizeForSubgroupCount",
      "OpGetKernelNDrangeSubGroupCount",
      "OpGetKernelNDrangeMaxSubGroupSize"
    ).foreach { opcode =>
      MachineSemantics.machine(opcode).foreach { (_, outcome) =>
        val definition = outcome.fold(failure => fail(s"$opcode: $failure"), identity)
        assertEquals(errors(ScalaBackend.source(definition)), Nil, opcode)
      }
    }
  }

  test("rendered tensor layout, view and transfer instructions compile") {
    Vec(
      "OpCreateTensorLayoutNV",
      "OpTensorLayoutSetDimensionNV",
      "OpTensorLayoutSetStrideNV",
      "OpTensorLayoutSetBlockSizeNV",
      "OpTensorLayoutSliceNV",
      "OpTensorLayoutSetClampValueNV",
      "OpCreateTensorViewNV",
      "OpTensorViewSetDimensionNV",
      "OpTensorViewSetStrideNV",
      "OpTensorViewSetClipNV",
      "OpCooperativeMatrixLoadTensorNV",
      "OpCooperativeMatrixStoreTensorNV"
    ).foreach { opcode =>
      MachineSemantics.machine(opcode).foreach { (_, outcome) =>
        val definition = outcome.fold(failure => fail(s"$opcode: $failure"), identity)
        assertEquals(errors(ScalaBackend.source(definition)), Nil, opcode)
      }
    }
  }

  test("rendered tensor element and size instructions compile") {
    Vec("OpTensorQuerySizeARM", "OpTensorReadARM", "OpTensorWriteARM").foreach { opcode =>
      MachineSemantics.machine(opcode).foreach { (_, outcome) =>
        val definition = outcome.fold(failure => fail(s"$opcode: $failure"), identity)
        assertEquals(errors(ScalaBackend.source(definition)), Nil, opcode)
      }
    }
  }

  test("rendered untyped pointer instructions compile") {
    Vec(
      "OpUntypedVariableKHR",
      "OpUntypedPrefetchKHR",
      "OpUntypedVariableLengthArrayINTEL",
      "OpUntypedAccessChainKHR",
      "OpUntypedInBoundsAccessChainKHR",
      "OpUntypedPtrAccessChainKHR",
      "OpUntypedInBoundsPtrAccessChainKHR",
      "OpUntypedArrayLengthKHR"
    ).foreach { opcode =>
      MachineSemantics.machine(opcode).foreach { (_, outcome) =>
        val definition = outcome.fold(failure => fail(s"$opcode: $failure"), identity)
        assertEquals(errors(ScalaBackend.source(definition)), Nil, opcode)
      }
    }
  }

  test("rendered image samples, gathers and level of detail queries compile") {
    Vec(
      "OpImageSampleImplicitLod",
      "OpImageSampleExplicitLod",
      "OpImageSampleDrefImplicitLod",
      "OpImageSampleDrefExplicitLod",
      "OpImageSampleProjImplicitLod",
      "OpImageSampleProjExplicitLod",
      "OpImageSampleProjDrefImplicitLod",
      "OpImageSampleProjDrefExplicitLod",
      "OpImageGather",
      "OpImageDrefGather",
      "OpImageQueryLod"
    ).foreach { opcode =>
      MachineSemantics.machine(opcode).foreach { (_, outcome) =>
        val definition = outcome.fold(failure => fail(s"$opcode: $failure"), identity)
        assertEquals(errors(ScalaBackend.source(definition)), Nil, opcode)
      }
    }
  }

  test("rendered variables compile") {
    Vec(
      "OpVariable",
      "OpVariableLengthArrayINTEL",
      "OpSaveMemoryINTEL",
      "OpRestoreMemoryINTEL",
      "OpGenericPtrMemSemantics"
    ).foreach {
      opcode =>
        MachineSemantics.machine(opcode).foreach { (_, outcome) =>
          val definition = outcome.fold(failure => fail(s"$opcode: $failure"), identity)
          assertEquals(errors(ScalaBackend.source(definition)), Nil, opcode)
        }
    }
  }

  test("rendered values and clock reads compile") {
    Vec("OpExpectKHR", "OpUndef", "OpPoisonKHR", "OpReadClockKHR").foreach { opcode =>
      MachineSemantics.machine(opcode).foreach { (_, outcome) =>
        val definition = outcome.fold(failure => fail(s"$opcode: $failure"), identity)
        assertEquals(errors(ScalaBackend.source(definition)), Nil, opcode)
      }
    }
  }

  test("rendered composite and pointer instructions compile") {
    Vec(
      "OpCompositeExtract",
      "OpCompositeConstruct",
      "OpCompositeConstructReplicateEXT",
      "OpCompositeInsert",
      "OpVectorShuffle",
      "OpVectorExtractDynamic",
      "OpVectorInsertDynamic",
      "OpCopyObject",
      "OpCopyLogical",
      "OpConditionalCopyObjectINTEL",
      "OpAccessChain",
      "OpInBoundsAccessChain",
      "OpPtrAccessChain",
      "OpInBoundsPtrAccessChain",
      "OpConvertPtrToU",
      "OpConvertUToPtr",
      "OpPtrCastToGeneric",
      "OpGenericCastToPtr",
      "OpGenericCastToPtrExplicit",
      "OpArrayLength"
    ).foreach { opcode =>
      MachineSemantics.machine(opcode).foreach { (_, outcome) =>
        val definition = outcome.fold(failure => fail(s"$opcode: $failure"), identity)
        assertEquals(errors(ScalaBackend.source(definition)), Nil, opcode)
      }
    }
  }

  test("rendered barrier instructions compile") {
    Vec(
      "OpControlBarrier",
      "OpControlBarrierArriveEXT",
      "OpControlBarrierWaitEXT",
      "OpMemoryBarrier",
      "OpMemoryNamedBarrier",
      "OpNamedBarrierInitialize"
    ).foreach { opcode =>
      MachineSemantics.machine(opcode).foreach { (_, outcome) =>
        val definition = outcome.fold(failure => fail(s"$opcode: $failure"), identity)
        assertEquals(errors(ScalaBackend.source(definition)), Nil, opcode)
      }
    }
  }

  test("rendered image instructions compile") {
    Vec(
      "OpSampledImage",
      "OpImage",
      "OpImageSampleImplicitLod",
      "OpImageSampleExplicitLod",
      "OpImageSampleDrefImplicitLod",
      "OpImageSampleDrefExplicitLod",
      "OpImageSampleProjImplicitLod",
      "OpImageSampleProjExplicitLod",
      "OpImageSampleProjDrefImplicitLod",
      "OpImageSampleProjDrefExplicitLod",
      "OpImageGather",
      "OpImageDrefGather",
      "OpImageQueryLod",
      "OpImageQueryLevels",
      "OpImageQuerySizeLod",
      "OpImageQuerySize",
      "OpImageQuerySamples",
      "OpImageQueryOrder",
      "OpImageQueryFormat",
      "OpImageFetch",
      "OpImageRead",
      "OpImageWrite",
      "OpImageTexelPointer"
    ).foreach { opcode =>
      MachineSemantics.machine(opcode).foreach { (_, outcome) =>
        val definition = outcome.fold(failure => fail(s"$opcode: $failure"), identity)
        assertEquals(errors(ScalaBackend.source(definition)), Nil, opcode)
      }
    }
  }

  test("rendered cooperative matrix and vector instructions compile") {
    Vec(
      "OpCooperativeMatrixLengthKHR",
      "OpCooperativeMatrixLengthNV",
      "OpCooperativeMatrixLoadKHR",
      "OpCooperativeMatrixLoadNV",
      "OpCooperativeMatrixStoreKHR",
      "OpCooperativeMatrixStoreNV",
      "OpCooperativeMatrixConvertNV",
      "OpCooperativeMatrixTransposeNV",
      "OpCooperativeMatrixPerElementOpNV",
      "OpCooperativeMatrixReduceNV",
      "OpCompositeConstructCoopMatQCOM",
      "OpCompositeExtractCoopMatQCOM",
      "OpCooperativeVectorLoadNV",
      "OpCooperativeVectorStoreNV",
      "OpCooperativeVectorMatrixMulNV",
      "OpCooperativeVectorMatrixMulAddNV",
      "OpCooperativeVectorOuterProductAccumulateNV",
      "OpCooperativeVectorReduceSumAccumulateNV"
    ).foreach { opcode =>
      MachineSemantics.machine(opcode).foreach { (_, outcome) =>
        val definition = outcome.fold(failure => fail(s"$opcode: $failure"), identity)
        assertEquals(errors(ScalaBackend.source(definition)), Nil, opcode)
      }
    }
  }

  test("rendered pipe instructions compile") {
    Vec(
      "OpWritePipe",
      "OpReadPipe",
      "OpReservedWritePipe",
      "OpReservedReadPipe",
      "OpReserveWritePipePackets",
      "OpReserveReadPipePackets",
      "OpGroupReserveWritePipePackets",
      "OpGroupReserveReadPipePackets",
      "OpIsValidReserveId",
      "OpGetNumPipePackets",
      "OpGetMaxPipePackets",
      "OpCreatePipeFromPipeStorage"
    ).foreach { opcode =>
      MachineSemantics.machine(opcode).foreach { (_, outcome) =>
        val definition = outcome.fold(failure => fail(s"$opcode: $failure"), identity)
        assertEquals(errors(ScalaBackend.source(definition)), Nil, opcode)
      }
    }
  }

  test("rendered group instructions compile") {
    Vec(
      "OpGroupAll",
      "OpGroupAny",
      "OpGroupBroadcast",
      "OpGroupIAdd",
      "OpGroupFAdd",
      "OpGroupLogicalAndKHR",
      "OpGroupIAddNonUniformAMD",
      "OpGroupNonUniformElect",
      "OpGroupNonUniformAll",
      "OpGroupNonUniformAny",
      "OpGroupNonUniformAllEqual",
      "OpGroupNonUniformBroadcast",
      "OpGroupNonUniformBroadcastFirst",
      "OpGroupNonUniformBallot",
      "OpGroupNonUniformInverseBallot",
      "OpGroupNonUniformBallotBitExtract",
      "OpGroupNonUniformBallotBitCount",
      "OpGroupNonUniformBallotFindLSB",
      "OpGroupNonUniformBallotFindMSB",
      "OpGroupNonUniformShuffle",
      "OpGroupNonUniformShuffleXor",
      "OpGroupNonUniformShuffleUp",
      "OpGroupNonUniformShuffleDown",
      "OpGroupNonUniformRotateKHR",
      "OpGroupNonUniformQuadBroadcast",
      "OpGroupNonUniformQuadSwap",
      "OpGroupNonUniformPartitionEXT",
      "OpGroupNonUniformIAdd",
      "OpGroupNonUniformFMax",
      "OpGroupNonUniformLogicalXor",
      "OpSubgroupAllKHR",
      "OpSubgroupAnyKHR",
      "OpSubgroupAllEqualKHR",
      "OpSubgroupBallotKHR",
      "OpSubgroupFirstInvocationKHR",
      "OpSubgroupReadInvocationKHR",
      "OpSubgroupShuffleINTEL",
      "OpSubgroupShuffleXorINTEL",
      "OpSubgroupShuffleUpINTEL",
      "OpSubgroupShuffleDownINTEL"
    ).foreach { opcode =>
      MachineSemantics.machine(opcode).foreach { (_, outcome) =>
        val definition = outcome.fold(failure => fail(s"$opcode: $failure"), identity)
        assertEquals(errors(ScalaBackend.source(definition)), Nil, opcode)
      }
    }
  }

  test("rendered matrix instructions compile") {
    Vec("OpTranspose").foreach { opcode =>
      MachineSemantics.machine(opcode).foreach { (_, outcome) =>
        val definition = outcome.fold(failure => fail(s"$opcode: $failure"), identity)
        assertEquals(errors(ScalaBackend.source(definition)), Nil, opcode)
      }
    }
  }

  test("rendered control instructions compile") {
    Vec(
      "OpLabel",
      "OpBranch",
      "OpBranchConditional",
      "OpSwitch",
      "OpPhi",
      "OpFunctionCall",
      "OpFunctionPointerCallINTEL",
      "OpReturn",
      "OpReturnValue",
      "OpKill",
      "OpTerminateInvocation",
      "OpUnreachable",
      "OpAbortKHR"
    ).foreach { opcode =>
      MachineSemantics.machine(opcode).foreach { (_, outcome) =>
        val definition = outcome.fold(failure => fail(s"$opcode: $failure"), identity)
        assertEquals(errors(ScalaBackend.source(definition)), Nil, opcode)
      }
    }
  }

  test("rendered extended instructions compile") {
    Vec(
      "GLSL.std.450 Determinant",
      "GLSL.std.450 MatrixInverse",
      "OpenCL.std printf",
      "OpenCL.std shuffle",
      "OpenCL.std shuffle2",
      "OpenCL.std vloadn",
      "OpenCL.std vstoren"
    ).foreach { opcode =>
      MachineSemantics.machine(opcode).foreach { (_, outcome) =>
        val definition = outcome.fold(failure => fail(s"$opcode: $failure"), identity)
        assertEquals(errors(ScalaBackend.source(definition)), Nil, opcode)
      }
    }
  }

  test("rendered Intel subgroup block instructions compile") {
    Vec(
      "OpSubgroupBlockReadINTEL",
      "OpSubgroupBlockWriteINTEL",
      "OpSubgroupBlockPrefetchINTEL",
      "OpSubgroup2DBlockPrefetchINTEL",
      "OpSubgroupImageBlockReadINTEL",
      "OpSubgroupImageBlockWriteINTEL",
      "OpSubgroupImageMediaBlockReadINTEL",
      "OpSubgroupImageMediaBlockWriteINTEL",
      "OpSubgroup2DBlockLoadINTEL",
      "OpSubgroup2DBlockLoadTransformINTEL",
      "OpSubgroup2DBlockLoadTransposeINTEL",
      "OpSubgroup2DBlockStoreINTEL",
      "OpSubgroupMatrixMultiplyAccumulateINTEL"
    ).foreach { opcode =>
      MachineSemantics.machine(opcode).foreach { (_, outcome) =>
        val definition = outcome.fold(failure => fail(s"$opcode: $failure"), identity)
        assertEquals(errors(ScalaBackend.source(definition)), Nil, opcode)
      }
    }
  }

  test("rendered vendor memory instructions compile") {
    Vec(
      "OpConvertHandleToImageINTEL",
      "OpConvertHandleToSamplerINTEL",
      "OpRawAccessChainNV",
      "OpPredicatedLoadINTEL",
      "OpPredicatedStoreINTEL",
      "OpMaskedGatherINTEL",
      "OpMaskedScatterINTEL",
      "OpConvertHandleToSampledImageINTEL",
      "OpBitCastArrayQCOM",
      "OpExtractSubArrayQCOM"
    ).foreach { opcode =>
      MachineSemantics.machine(opcode).foreach { (_, outcome) =>
        val definition = outcome.fold(failure => fail(s"$opcode: $failure"), identity)
        assertEquals(errors(ScalaBackend.source(definition)), Nil, opcode)
      }
    }
  }
}
