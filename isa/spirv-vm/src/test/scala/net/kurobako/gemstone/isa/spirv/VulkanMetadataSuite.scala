package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class VulkanMetadataSuite extends munit.FunSuite {
  private val DefaultSize = Seq(4L, 3L, 2L)
  private val SizeIds     = Seq("%x", "%y", "%z")

  private val functionTypes = """%void = OpTypeVoid
    |%fn = OpTypeFunction %void""".stripMargin

  private def function(id: String): String =
    s"""$id = OpFunction %void None %fn
       |${id}_entry = OpLabel
       |OpReturn
       |OpFunctionEnd""".stripMargin

  private val sizeConstants = """%uint = OpTypeInt 32 0
    |%x = OpSpecConstant %uint 4
    |%y = OpSpecConstant %uint 3
    |%z = OpSpecConstant %uint 2""".stripMargin

  private def metadata(module: Module, name: NonEmpty[String]): KernelMetadata = load(module).value.kernel(name).value

  for (
    (name, environment, modes, declarations, required, hint) <- Seq(
      ("LocalSize", vulkan, "OpExecutionMode %main LocalSize 8 2 1", "", Dimensions(8, 2, 1), None),
      (
        "LocalSizeId constants",
        vulkan,
        "OpExecutionModeId %main LocalSizeId %x %y %z",
        sizeConstants,
        Dimensions(4, 3, 2),
        None
      ),
      (
        "the WorkgroupSize builtin over a conflicting LocalSize",
        vulkan,
        "OpExecutionMode %main LocalSize 1 1 1\nOpDecorate %size BuiltIn WorkgroupSize",
        s"$sizeConstants\n%uint3 = OpTypeVector %uint 3\n%size = OpSpecConstantComposite %uint3 %x %y %z",
        Dimensions(4, 3, 2),
        None
      ),
      (
        "OpenCL LocalSize and LocalSizeHint",
        openCL,
        "OpExecutionMode %main LocalSize 8 2 1\nOpExecutionMode %main LocalSizeHint 4 3 2",
        "",
        Dimensions(8, 2, 1),
        Some(Dimensions(4, 3, 2))
      )
    )
  )
    test(s"work-group sizes and the entry point come from $name") {
      val model  = if (environment == openCL) "Kernel" else "GLCompute"
      val module = assemble(s"""
        $environment
        OpEntryPoint $model %main "main"
        $modes
        $functionTypes
        $declarations
        ${function("%main")}
      """)
      val entry = module.instructions.collectFirst { case Instruction.EntryPoint(entryPoint = id) => id.words.head.raw }
      val result = metadata(module, "main")
      assertEquals(result.requiredWorkGroupSize, Some(required))
      assertEquals(result.workGroupSizeHint, hint)
      assertEquals(result.entryPointId.map(_.raw), entry)
    }

  test("invalid WorkgroupSize constants fail during loading before metadata is requested") {
    for {
      invalid <- Seq(0L, 0x80000000L, 0x100000001L)
      axis    <- 0 until 3
    } {
      val dimensions = DefaultSize.updated(axis, invalid)
      val module     = spv"""
        $vulkan
        OpEntryPoint GLCompute %main "main"
        OpDecorate %size BuiltIn WorkgroupSize
        $functionTypes
        %ulong = OpTypeInt 64 0
        %ulong3 = OpTypeVector %ulong 3
        ${SizeIds.zip(dimensions).map((id, size) => s"$id = OpSpecConstant %ulong $size").mkString("\n")}
        %size = OpSpecConstantComposite %ulong3 %x %y %z
        ${function("%main")}
      """
      load(module) match {
        case Left(Fault.InvalidRequest(message)) => assert(message.contains("WorkgroupSize"))
        case other => fail(s"expected invalid dimensions for axis $axis and value $invalid, got $other")
      }
    }
  }

  private def storageBuffer(id: String, set: Long, binding: Long): (String, String) = (
    s"""OpDecorate $id DescriptorSet $set
       |OpDecorate $id Binding $binding""".stripMargin,
    s"$id = OpVariable %block_ptr StorageBuffer"
  )

  private val blockTypes = """OpDecorate %block Block
    |OpMemberDecorate %block 0 Offset 0""".stripMargin

  private val blockDeclarations = """%uint = OpTypeInt 32 0
    |%block = OpTypeStruct %uint
    |%block_ptr = OpTypePointer StorageBuffer %block""".stripMargin

  test("descriptor indices beyond the supported unsigned range return a loading error") {
    for ((set, binding) <- Seq((0L, 0xffffffffL), (0xffffffffL, 0L))) {
      val (decorations, variable) = storageBuffer("%buffer", set, binding)
      val module                  = spv"""
        $vulkan
        OpEntryPoint GLCompute %main "main" %buffer
        OpExecutionMode %main LocalSize 1 1 1
        $decorations
        $blockTypes
        $functionTypes
        $blockDeclarations
        $variable
        ${function("%main")}
      """
      load(module) match {
        case Left(Fault.Unsupported(message)) => assert(message.contains("4294967295"))
        case other                            => fail(s"expected unsupported descriptor index, got $other")
      }
    }
  }

  test("descriptor bindings are scoped to the selected entry point") {
    val (leftDecorations, left)   = storageBuffer("%left", 0L, 3L)
    val (rightDecorations, right) = storageBuffer("%right", 0L, 5L)
    val module                    = spv"""
      $vulkan
      OpEntryPoint GLCompute %first "first" %left
      OpEntryPoint GLCompute %second "second" %right
      OpExecutionMode %first LocalSize 1 1 1
      OpExecutionMode %second LocalSize 1 1 1
      $leftDecorations
      $rightDecorations
      $blockTypes
      $functionTypes
      $blockDeclarations
      $left
      $right
      ${function("%first")}
      ${function("%second")}
    """
    assertEquals(metadata(module, "first").bindingIndices, Vec[Int >= 0](3))
    assertEquals(metadata(module, "second").bindingIndices, Vec[Int >= 0](5))
  }

  test("decoration groups apply descriptor and member decorations") {
    val module = spv"""
      $vulkan
      OpEntryPoint GLCompute %main "main" %buffer
      OpExecutionMode %main LocalSize 1 1 1
      OpDecorate %bindings DescriptorSet 3
      OpDecorate %bindings Binding 7
      %bindings = OpDecorationGroup
      OpDecorate %offsets Offset 16
      %offsets = OpDecorationGroup
      OpGroupDecorate %bindings %buffer
      OpGroupMemberDecorate %offsets %pair 1
      OpDecorate %pair Block
      OpMemberDecorate %pair 0 Offset 0
      $functionTypes
      %uint = OpTypeInt 32 0
      %pair = OpTypeStruct %uint %uint
      %pair_ptr = OpTypePointer StorageBuffer %pair
      %buffer = OpVariable %pair_ptr StorageBuffer
      %first = OpConstant %uint 19
      %second = OpConstant %uint 23
      %value = OpConstantComposite %pair %first %second
      %main = OpFunction %void None %fn
      %entry = OpLabel
      OpStore %buffer %value
      OpReturn
      OpFunctionEnd
    """
    assertEquals(metadata(module, "main").bindingIndices, Vec[Int >= 0](7))
    val memory = RegionMemory()
    val region = memory.allocate(20L)
    assertEquals(run(module, "main", memory, Vec(buffer(region))), Right(()))
    assertEquals(words(memory, region, offset = 0, count = 5), Vec(19L, 0L, 0L, 0L, 23L))
  }
}
