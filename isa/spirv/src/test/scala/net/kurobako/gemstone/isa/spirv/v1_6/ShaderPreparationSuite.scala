package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.graphics.ShaderMetadata.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.rpc.*

class ShaderPreparationSuite extends munit.FunSuite {
  private def o(kind: NonEmpty[String], values: Int*): Operand             = operand(kind, values*)
  private def ref(value: Int): Operand                                     = o("IdRef", value)
  private def dest(value: Int): Operand                                    = o("IdResult", value)
  private def literal(value: Int): Operand                                 = o("LiteralInteger", value)
  private def decoration(target: Int, kind: Long, value: Int): Instruction =
    Instruction.Decorate(ref(target), o("Decoration", kind.toInt, value))
  private def base(
      model: Long,
      declarations: Vec[Instruction] = Vec.empty,
      interfaces: Vec[Int] = Vec.empty,
      body: Vec[Instruction] = Vec(Instruction.Return())
  ): Module = Module(
    Header(Version(1, 4), Word(0), Word(100), Word(0)),
    Vec(
      Instruction.Capability(o("Capability", Capability.Shader)),
      Instruction.MemoryModel(o("AddressingModel", AddressingModel.Logical), o("MemoryModel", MemoryModel.GLSL450)),
      Instruction.EntryPoint(o("ExecutionModel", model.toInt), ref(10), stringOperand("main"), interfaces.map(ref(_)))
    ) ++
      (if (model == ExecutionModel.GLCompute)
         Vec(Instruction.ExecutionMode(ref(10), o("ExecutionMode", ExecutionMode.LocalSize, 2, 3, 4)))
       else Vec.empty[Instruction]) ++
      Vec(Instruction.TypeVoid(dest(1)), Instruction.TypeFunction(dest(2), ref(1), Vec.empty)) ++ declarations ++
      Vec(
        Instruction.Function(o("IdResultType", 1), dest(10), o("FunctionControl", FunctionControl.None), ref(2)),
        Instruction.Label(dest(11))
      ) ++ body ++ Vec(Instruction.FunctionEnd()),
    Endianness.Little
  )
  private def prepare(
      module: Module,
      stage: ShaderStageKind,
      constants: Map[Binary32, Vec[Byte]] = Map.empty
  ): PreparedShader =
    ShaderPreparation.prepare(ISA.encode(module).toOption.get, "main", stage, constants).fold(
      error => fail(error.message),
      identity
    )

  test("select equal entry names by stage and retain only their execution modes") {
    val compute = base(ExecutionModel.GLCompute)
    val module  = compute.copy(instructions =
      compute.instructions ++ Vec(
        Instruction.EntryPoint(
          o("ExecutionModel", ExecutionModel.Vertex),
          ref(20),
          stringOperand("main"),
          Vec.empty
        ),
        Instruction.ExecutionMode(ref(20), o("ExecutionMode", ExecutionMode.EarlyFragmentTests))
      )
    )
    val selected = ISA.decode(prepare(module, ShaderStageKind.Compute).image).toOption.get
    assertEquals(selected.instructions, compute.instructions)
    assert(ShaderPreparation.prepare(
      ISA.encode(module).toOption.get,
      "missing",
      ShaderStageKind.Vertex,
      Map.empty
    ).isLeft)
    assert(ShaderPreparation.prepare(
      ISA.encode(module.copy(instructions = module.instructions ++ module.instructions.collect {
        case value: Instruction.EntryPoint
            if value.executionModel == o("ExecutionModel", ExecutionModel.Vertex) => value
      })).toOption.get,
      "main",
      ShaderStageKind.Vertex,
      Map.empty
    ).isLeft)
  }

  test("early fragment tests carry helper state even without derivative instructions") {
    val fragment = base(ExecutionModel.Fragment)
    val early    = fragment.copy(instructions =
      fragment.instructions.patch(
        3,
        Vec(Instruction.ExecutionMode(ref(10), o("ExecutionMode", ExecutionMode.EarlyFragmentTests))),
        0
      )
    )
    val prepared = prepare(early, ShaderStageKind.Fragment)
    assert(prepared.earlyFragmentTests)
    assert(!prepared.fragmentQuads)
    val helperFields = prepared.interfaces.filter(_.builtin.exists(_.raw == BuiltIn.HelperInvocation))
    assertEquals(helperFields.size, 1)
    assertEquals(helperFields.head.size.raw, 1)
    val ordinary = prepare(fragment, ShaderStageKind.Fragment)
    assert(!ordinary.earlyFragmentTests)
    assert(!ordinary.interfaces.exists(_.builtin.exists(_.raw == BuiltIn.HelperInvocation)))
  }

  test("compute preparation returns the executable and its workgroup in one result") {
    val result = prepare(base(ExecutionModel.GLCompute), ShaderStageKind.Compute)
    assertEquals(result.workgroup.map(size => (size.x.raw, size.y.raw, size.z.raw)), Some((2, 3, 4)))
    assertEquals(result.descriptors, Vec.empty[ShaderDescriptor])
    assertEquals(
      Wire.Codec[PreparedShader].encode(
        result,
        ExecutionRPC.Limits
      ).flatMap(Wire.Codec[PreparedShader].decode(_, ExecutionRPC.Limits)),
      Right(result)
    )
  }

  test("compute builtin inputs are internal to execution and do not become graphics records") {
    val module = base(
      ExecutionModel.GLCompute,
      Vec(
        decoration(6, Decoration.BuiltIn, BuiltIn.GlobalInvocationId),
        Instruction.TypeInt(dest(3), literal(32), literal(0)),
        Instruction.TypeVector(dest(4), ref(3), literal(3)),
        Instruction.TypePointer(dest(5), o("StorageClass", StorageClass.Input), ref(4)),
        Instruction.Variable(o("IdResultType", 5), dest(6), o("StorageClass", StorageClass.Input), None)
      ),
      Vec(6)
    )
    assertEquals(prepare(module, ShaderStageKind.Compute).interfaces, Vec.empty[ShaderInterface])
  }

  test("descriptor scoping and ordering match compiled execution arguments, including empty interfaces") {
    val declarations = Vec(
      decoration(6, Decoration.DescriptorSet, 1),
      decoration(6, Decoration.Binding, 5),
      decoration(7, Decoration.DescriptorSet, 0),
      decoration(7, Decoration.Binding, 3),
      Instruction.TypeInt(dest(3), literal(32), literal(0)),
      Instruction.TypePointer(dest(4), o("StorageClass", StorageClass.StorageBuffer), ref(3)),
      Instruction.Variable(
        o("IdResultType", 4),
        dest(6),
        o("StorageClass", StorageClass.StorageBuffer),
        None
      ),
      Instruction.Variable(
        o("IdResultType", 4),
        dest(7),
        o("StorageClass", StorageClass.StorageBuffer),
        None
      )
    )
    def descriptors(ids: Vec[Int]) =
      prepare(base(ExecutionModel.GLCompute, declarations, ids), ShaderStageKind.Compute).descriptors
    assertEquals(descriptors(Vec(6)).map(d => (d.set.raw, d.binding.raw)), Vec((1, 5)))
    assertEquals(descriptors(Vec(6, 7)).map(d => (d.set.raw, d.binding.raw)), Vec((0, 3), (1, 5)))
    assertEquals(descriptors(Vec.empty), Vec.empty[ShaderDescriptor])
    val legacy = base(ExecutionModel.GLCompute, declarations).copy(header =
      Header(Version(1, 3), Word(0), Word(100), Word(0))
    )
    assertEquals(prepare(legacy, ShaderStageKind.Compute).descriptors.size, 2)
  }

  test("specialized interface arrays share compiler evaluation and physical layout") {
    val declarations = Vec(
      decoration(4, Decoration.SpecId, 9),
      decoration(8, Decoration.Location, 2),
      Instruction.TypeInt(dest(3), literal(32), literal(0)),
      Instruction.SpecConstant(o("IdResultType", 3), dest(4), o("LiteralContextDependentNumber", 2)),
      Instruction.TypeFloat(dest(5), literal(32), None),
      Instruction.TypeArray(dest(6), ref(5), ref(4)),
      Instruction.TypePointer(dest(7), o("StorageClass", StorageClass.Output), ref(6)),
      Instruction.Variable(o("IdResultType", 7), dest(8), o("StorageClass", StorageClass.Output), None)
    )
    val module = base(ExecutionModel.Vertex, declarations, Vec(8))
    val bytes  = ISA.encode(module).toOption.get
    val result = prepare(module, ShaderStageKind.Vertex, Map(Binary32(9L) -> Vec[Byte](3, 0, 0, 0)))
    assertEquals(
      Wire.Codec[PreparedShader].encode(
        result,
        ExecutionRPC.Limits
      ).flatMap(Wire.Codec[PreparedShader].decode(_, ExecutionRPC.Limits)),
      Right(result)
    )
    val record = result.interfaces.head
    assertEquals(record.size.raw, 12)
    assertEquals(
      record.fields.map(field => (field.offset.raw, field.size.raw, field.location.map(_.raw))),
      Vec((0, 4, Some(2)), (4, 4, Some(3)), (8, 4, Some(4)))
    )
    assertEquals(ISA.encode(module), Right(bytes))
    assertEquals(prepare(module, ShaderStageKind.Vertex).interfaces.head.size.raw, 8)
    assertEquals(prepare(module, ShaderStageKind.Vertex, Map(Binary32(9L) -> Vec[Byte](3, 0, 0, 0))), result)
  }

  test("fragment kill coverage lowering is idempotent and leaves a standard initialized builtin") {
    val module   = base(ExecutionModel.Fragment, body = Vec(Instruction.Kill()))
    val prepared = prepare(module, ShaderStageKind.Fragment)
    val mask     = prepared.interfaces.find(_.builtin.exists(_.raw == BuiltIn.SampleMask)).get
    assertEquals(mask.direction, ShaderDirection.Output)
    assertEquals(mask.size.raw, 4)
    val lowered = ISA.decode(prepared.image).toOption.get
    assertEquals(ShaderLowering.lower(lowered, ShaderStageKind.Fragment).module, lowered)
    val compiled = VMCompiler.compileUnchecked(lowered).toOption.get
    val initial  = compiled.globals(mask.id.raw).initializer.get
    compiled.constantData(initial) match {
      case VMIR.Constant.Bits(_, lanes) => assertEquals(lanes, Vec(0xffffffffL))
      case value                        => fail(s"unexpected mask initializer $value")
    }
  }

  test("decoration groups expand before consumers and undefined groups fail") {
    val module    = base(ExecutionModel.Vertex)
    val decorated = module.copy(instructions =
      module.instructions ++ Vec(
        Instruction.DecorationGroup(dest(30)),
        decoration(30, Decoration.Location, 3),
        Instruction.GroupDecorate(ref(30), Vec(ref(6), ref(7))),
        Instruction.GroupMemberDecorate(ref(30), Vec(o("PairIdRefLiteralInteger", 8, 0)))
      )
    )
    val normalized =
      ModuleNormalization.expandDecorationGroups(ISA.decode(ISA.encode(decorated).toOption.get).toOption.get)
    assert(normalized.instructions.contains(decoration(6, Decoration.Location, 3)))
    assert(normalized.instructions.contains(decoration(7, Decoration.Location, 3)))
    assert(normalized.instructions.contains(Instruction.MemberDecorate(
      ref(8),
      literal(0),
      o("Decoration", Decoration.Location, 3)
    )))
    val bad = module.copy(instructions = module.instructions ++ Vec(Instruction.GroupDecorate(ref(31), Vec(ref(6)))))
    assert(ShaderPreparation.prepare(
      ISA.encode(bad).toOption.get,
      "main",
      ShaderStageKind.Vertex,
      Map.empty
    ).isLeft)
  }

  test("recursive and oversized interface layouts fail preparation without escaping an exception") {
    def module(count: Int, recursive: Boolean): Module = base(
      ExecutionModel.Vertex,
      Vec(
        Instruction.TypeInt(dest(3), literal(32), literal(0)),
        Instruction.Constant(o("IdResultType", 3), dest(4), o("LiteralContextDependentNumber", count)),
        if (recursive) Instruction.TypeStruct(dest(5), Vec(ref(5))) else Instruction.TypeArray(dest(5), ref(3), ref(4)),
        Instruction.TypePointer(dest(6), o("StorageClass", StorageClass.Output), ref(5)),
        Instruction.Variable(o("IdResultType", 6), dest(7), o("StorageClass", StorageClass.Output), None)
      ),
      Vec(7)
    )
    for (invalid <- List(module(1, true), module(Int.MaxValue, false))) {
      val bytes = ISA.encode(invalid).toOption.get
      assert(ShaderPreparation.prepare(bytes, "main", ShaderStageKind.Vertex, Map.empty).isLeft)
    }
  }

  test("fragment runtime builtin lowering reuses types and inserts all required records once") {
    val module = base(
      ExecutionModel.Fragment,
      Vec(
        Instruction.TypeFloat(dest(3), literal(32), None),
        Instruction.TypeVector(dest(4), ref(3), literal(4)),
        Instruction.TypePointer(dest(5), o("StorageClass", StorageClass.Input), ref(4)),
        Instruction.TypeImage(
          dest(6),
          ref(3),
          o("Dim", Dim.SubpassData),
          literal(0),
          literal(0),
          literal(0),
          literal(2),
          o("ImageFormat", ImageFormat.Unknown),
          None
        ),
        Instruction.Constant(o("IdResultType", 3), dest(7), o("LiteralContextDependentNumber", 0))
      ),
      body = Vec(Instruction.DPdx(o("IdResultType", 3), dest(8), ref(7)), Instruction.Kill())
    )
    val prepared = prepare(module, ShaderStageKind.Fragment)
    assert(prepared.fragmentQuads)
    assertEquals(
      prepared.interfaces.flatMap(_.builtin.map(_.raw)).toSet,
      Set(BuiltIn.FragCoord, BuiltIn.SampleMask, BuiltIn.HelperInvocation)
    )
    val decoded = ISA.decode(prepared.image).toOption.get
    assertEquals(decoded.instructions.count { case Instruction.TypeVector(_, _, _) => true; case _ => false }, 1)
    assertEquals(ShaderLowering.lower(decoded, ShaderStageKind.Fragment).module, decoded)
    assertEquals(
      Wire.Codec[PreparedShader].encode(
        prepared,
        ExecutionRPC.Limits
      ).flatMap(Wire.Codec[PreparedShader].decode(_, ExecutionRPC.Limits)),
      Right(prepared)
    )
  }

  test("location spans preserve wide scalar words and reject invalid component assignments") {
    assertEquals(ShaderReflection.interfaceSpans(32, 2, 2), Vec((0, 8, 2)))
    assertEquals(ShaderReflection.interfaceSpans(64, 3, 0), Vec((0, 16, 0), (16, 8, 0)))
    assertEquals(ShaderReflection.interfaceSpans(64, 1, 2), Vec((0, 8, 2)))
    for (
      (
        width,
        lanes,
        component
      ) <- List((32, 3, 2), (64, 1, 1), (64, 2, 2), (64, 3, 2), (32, 0, 0), (32, 5, 0), (16, 2, 0), (32, 1, 4))
    )
      intercept[IllegalArgumentException](ShaderReflection.interfaceSpans(width, lanes, component))
  }
}
