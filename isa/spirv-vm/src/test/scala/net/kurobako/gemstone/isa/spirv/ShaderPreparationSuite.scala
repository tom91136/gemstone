package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.graphics.ShaderMetadata.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class ShaderPreparationSuite extends munit.FunSuite {
  private def shader(
      model: String,
      modes: String = "",
      annotations: String = "",
      declarations: String = "",
      interfaces: String = "",
      body: String = "OpReturn"
  ): String = s"""
    $vulkan
    OpEntryPoint $model %main "main" $interfaces
    ${if (model == "GLCompute") "OpExecutionMode %main LocalSize 2 3 4" else ""}
    ${if (model == "Fragment") "OpExecutionMode %main OriginUpperLeft" else ""}
    $modes
    $annotations
    %void  = OpTypeVoid
    %fn    = OpTypeFunction %void
    $declarations
    %main  = OpFunction %void None %fn
    %entry = OpLabel
    $body
    OpFunctionEnd
  """

  private def image(source: String): Vec[Byte] = ISA.encode(assemble(source)).value

  private def prepare(
      source: String,
      stage: ShaderStageKind,
      constants: Vec[SpecializationConstant] = Vec.empty
  ): PreparedShader = ShaderPreparation.prepare(image(source), "main", stage, constants).value

  private def rejected(source: String, stage: ShaderStageKind, entry: NonEmpty[String] = "main"): Boolean =
    ShaderPreparation.prepare(image(source), entry, stage, Vec.empty).isLeft

  private def assertWire(prepared: PreparedShader): Unit = assertEquals(
    Wire.Codec[PreparedShader].encode(prepared, ExecutionRPC.Limits)
      .flatMap(Wire.Codec[PreparedShader].decode(_, ExecutionRPC.Limits)),
    Right(prepared)
  )

  test("select equal entry names by stage and retain only their execution modes") {
    val compute = shader("GLCompute")
    val vertex  = s"""$compute
      OpEntryPoint Vertex %vertex "main"
      OpExecutionMode %vertex EarlyFragmentTests
    """
    val selected = ISA.decode(prepare(vertex, ShaderStageKind.Compute).image).value
    assertEquals(selected.instructions, assemble(compute).instructions)
    assert(rejected(vertex, ShaderStageKind.Vertex, "missing"))
    assert(rejected(s"$vertex\nOpEntryPoint Vertex %vertex \"main\"", ShaderStageKind.Vertex))
  }

  test("early fragment tests carry helper state even without derivative instructions") {
    val early    = shader("Fragment", modes = "OpExecutionMode %main EarlyFragmentTests")
    val prepared = prepare(early, ShaderStageKind.Fragment)
    assert(prepared.earlyFragmentTests)
    assert(!prepared.fragmentQuads)
    val helperFields = prepared.interfaces.filter(_.builtin.exists(_.raw == BuiltIn.HelperInvocation))
    assertEquals(helperFields.size, 1)
    assertEquals(helperFields.head.size.raw, 1)
    val ordinary = prepare(shader("Fragment"), ShaderStageKind.Fragment)
    assert(!ordinary.earlyFragmentTests)
    assert(!ordinary.interfaces.exists(_.builtin.exists(_.raw == BuiltIn.HelperInvocation)))
  }

  test("compute preparation returns the executable and its workgroup in one result") {
    val result = prepare(shader("GLCompute"), ShaderStageKind.Compute)
    assertEquals(result.workgroup.map(size => (size.x.raw, size.y.raw, size.z.raw)), Some((2, 3, 4)))
    assertEquals(result.descriptors, Vec.empty[ShaderDescriptor])
    assertWire(result)
  }

  test("compute builtin inputs are internal to execution and do not become graphics records") {
    val module = shader(
      "GLCompute",
      annotations = "OpDecorate %id BuiltIn GlobalInvocationId",
      declarations = """
        %uint   = OpTypeInt 32 0
        %uvec3  = OpTypeVector %uint 3
        %id_ptr = OpTypePointer Input %uvec3
        %id     = OpVariable %id_ptr Input
      """,
      interfaces = "%id"
    )
    assertEquals(prepare(module, ShaderStageKind.Compute).interfaces, Vec.empty[ShaderInterface])
  }

  test("descriptor scoping and ordering match compiled execution arguments, including empty interfaces") {
    def module(interfaces: String) = shader(
      "GLCompute",
      annotations = """
        OpDecorate %first DescriptorSet 1
        OpDecorate %first Binding 5
        OpDecorate %second DescriptorSet 0
        OpDecorate %second Binding 3
      """,
      declarations = """
        %uint     = OpTypeInt 32 0
        %uint_ptr = OpTypePointer StorageBuffer %uint
        %first    = OpVariable %uint_ptr StorageBuffer
        %second   = OpVariable %uint_ptr StorageBuffer
      """,
      interfaces = interfaces
    )
    def descriptors(source: String) = prepare(source, ShaderStageKind.Compute).descriptors
    assertEquals(descriptors(module("%first")).map(d => (d.set.raw, d.binding.raw)), Vec((1, 5)))
    assertEquals(descriptors(module("%first %second")).map(d => (d.set.raw, d.binding.raw)), Vec((0, 3), (1, 5)))
    assertEquals(descriptors(module("")), Vec.empty[ShaderDescriptor])
    assertEquals(descriptors(s"; Version: 1.3\n${module("")}").size, 2)
  }

  test("specialized interface arrays share compiler evaluation and physical layout") {
    val module = shader(
      "Vertex",
      annotations = """
        OpDecorate %count SpecId 9
        OpDecorate %out Location 2
      """,
      declarations = """
        %uint    = OpTypeInt 32 0
        %count   = OpSpecConstant %uint 2
        %float   = OpTypeFloat 32
        %array   = OpTypeArray %float %count
        %out_ptr = OpTypePointer Output %array
        %out     = OpVariable %out_ptr Output
      """,
      interfaces = "%out"
    )
    val three  = Vec(SpecializationConstant(Binary32(9L), Vec[Byte](3, 0, 0, 0)))
    val result = prepare(module, ShaderStageKind.Vertex, three)
    assertWire(result)
    val record = result.interfaces.head
    assertEquals(record.size.raw, 12)
    assertEquals(
      record.fields.map(field => (field.offset.raw, field.size.raw, field.location.map(_.raw))),
      Vec((0, 4, Some(2)), (4, 4, Some(3)), (8, 4, Some(4)))
    )
    assertEquals(prepare(module, ShaderStageKind.Vertex).interfaces.head.size.raw, 8)
    assertEquals(prepare(module, ShaderStageKind.Vertex, three), result)
  }

  test("fragment kill coverage lowering is idempotent and leaves a standard initialised builtin") {
    val prepared = prepare(shader("Fragment", body = "OpKill"), ShaderStageKind.Fragment)
    val mask     = prepared.interfaces.find(_.builtin.exists(_.raw == BuiltIn.SampleMask)).get
    assertEquals(mask.direction, ShaderDirection.Output)
    assertEquals(mask.size.raw, 4)
    val lowered = ISA.decode(prepared.image).value
    assertEquals(ShaderLowering.lower(lowered, ShaderStageKind.Fragment).module, lowered)
    assert(load(lowered).isRight)
    val initialiser = lowered.instructions.collectFirst {
      case Instruction.Variable(_, variable, _, Some(value)) if Operand.id(variable) == mask.id.raw => Operand.id(value)
    }
    val lanes = lowered.instructions.collectFirst {
      case Instruction.ConstantComposite(_, composite, values) if initialiser.contains(Operand.id(composite)) =>
        values.map(Operand.id)
    }
    val bits = lowered.instructions.collect {
      case Instruction.Constant(_, constant, value) if lanes.exists(_.contains(Operand.id(constant))) =>
        value.words.head.toUnsigned.toLong
    }
    assertEquals(bits, Vec(0xffffffffL))
  }

  test("decoration groups expand before consumers and undefined groups fail") {
    val vertex    = shader("Vertex")
    val decorated = assemble(s"""$vertex
      %group = OpDecorationGroup
      OpDecorate %group Location 3
      OpGroupDecorate %group %6 %7
      OpGroupMemberDecorate %group %8 0
    """)
    val normalised = ModuleNormalisation.expandDecorationGroups(decorated)
    val expanded   = assemble("""
      OpDecorate %6 Location 3
      OpDecorate %7 Location 3
      OpMemberDecorate %8 0 Location 3
    """)
    val present = normalised.instructions.toSet
    expanded.instructions.foreach(instruction => assert(present(instruction)))
    assert(rejected(s"$vertex\nOpGroupDecorate %missing %main", ShaderStageKind.Vertex))
  }

  test("recursive and oversized interface layouts fail preparation without escaping an exception") {
    def module(count: Long, record: String) = shader(
      "Vertex",
      declarations = s"""
        %uint    = OpTypeInt 32 0
        %count   = OpConstant %uint $count
        %record  = $record
        %out_ptr = OpTypePointer Output %record
        %out     = OpVariable %out_ptr Output
      """,
      interfaces = "%out"
    )
    assert(rejected(module(1, "OpTypeStruct %record"), ShaderStageKind.Vertex))
    assert(rejected(module(Int.MaxValue, "OpTypeArray %uint %count"), ShaderStageKind.Vertex))
  }

  test("fragment runtime builtin lowering reuses types and inserts all required records once") {
    val module = shader(
      "Fragment",
      declarations = """
        %float    = OpTypeFloat 32
        %vec4     = OpTypeVector %float 4
        %vec4_ptr = OpTypePointer Input %vec4
        %subpass  = OpTypeImage %float SubpassData 0 0 0 2 Unknown
        %zero     = OpConstant %float 0
      """,
      body = """
        %dx = OpDPdx %float %zero
        OpKill
      """
    )
    val prepared = prepare(module, ShaderStageKind.Fragment)
    assert(prepared.fragmentQuads)
    assertEquals(
      prepared.interfaces.flatMap(_.builtin.map(_.raw)).toSet,
      Set(BuiltIn.FragCoord, BuiltIn.SampleMask, BuiltIn.HelperInvocation)
    )
    val decoded = ISA.decode(prepared.image).value
    assertEquals(decoded.instructions.count { case Instruction.TypeVector(_, _, _) => true; case _ => false }, 1)
    assertEquals(ShaderLowering.lower(decoded, ShaderStageKind.Fragment).module, decoded)
    assertWire(prepared)
  }

  test("an interface Component decoration beyond the last component fails preparation") {
    val module = "OpCapability Float64\n" + shader(
      "Vertex",
      annotations = """
        OpDecorate %out Location 0
        OpDecorate %out Component 4
      """,
      declarations = """
        %double  = OpTypeFloat 64
        %out_ptr = OpTypePointer Output %double
        %out     = OpVariable %out_ptr Output
      """,
      interfaces = "%out"
    )
    assertEquals(
      ShaderPreparation.prepare(image(module), "main", ShaderStageKind.Vertex, Vec.empty).leftValue,
      Fault.InvalidRequest("shader preparation: interface component 4 is out of range")
    )
  }

  test("location spans preserve wide scalar words and reject invalid component assignments") {
    assertEquals(
      ShaderReflection.interfaceSpans(32, 2, 2),
      Vec[(Int >= 0, Int > 0, Int in 0 ~ 3)]((0, 8, 2))
    )
    assertEquals(
      ShaderReflection.interfaceSpans(64, 3, 0),
      Vec[(Int >= 0, Int > 0, Int in 0 ~ 3)]((0, 16, 0), (16, 8, 0))
    )
    assertEquals(
      ShaderReflection.interfaceSpans(64, 1, 2),
      Vec[(Int >= 0, Int > 0, Int in 0 ~ 3)]((0, 8, 2))
    )
    for (
      (
        width,
        lanes,
        component
      ) <- List((32, 3, 2), (64, 1, 1), (64, 2, 2), (64, 3, 2), (32, 0, 0), (32, 5, 0), (16, 2, 0), (32, 1, 4))
    )
      intercept[IllegalArgumentException](ShaderReflection.interfaceSpans(
        width.unsafe[Int in 1 ~ 64],
        lanes.unsafe[Int > 0],
        component.unsafe[Int in 0 ~ 3]
      ))
  }
}
