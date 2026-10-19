package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.isa.spirv.asm.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.vm.*

/** SPIR-V core grammar instructions and the GLSL.std.450 and OpenCL.std instructions, the sets the VM executes; SPIR-V
  * has no registers, so builtins and storage classes stand in for them.
  */
object SPIRVModel extends Model {
  val targets: Vec[String]          = Vec.empty
  val anyTarget: Boolean            = true
  def defaultTarget: Option[String] = None

  private def quantified(shape: OperandShape): String = shape.quantifier match {
    case OperandQuantifier.One      => shape.kind.toString
    case OperandQuantifier.Optional => s"${shape.kind}?"
    case OperandQuantifier.Repeated => s"${shape.kind}*"
  }

  private def enumerants(kind: OperandKind): Vec[String] =
    Instruction.operandKinds.get(kind).fold(Vec.empty[String])(_.enumerants.map(_.name.raw))

  private val wordLayout = View.Encoding(
    32,
    Vec(
      View.Field("opcode", Vec.tabulate(16)(identity), "low half of the first word: opcode number"),
      View.Field("word count", Vec.tabulate(16)(16 + _), "high half of the first word: instruction length in words")
    ),
    Map.empty
  )

  /** An instance of `definition` with ids from 1 in its required id operands, in grammar order, a zero word in its
    * other required operands and none of its optional or repeated ones, or `None` when that does not decode.
    */
  def instance(definition: Instruction.Definition): Option[Instruction] = {
    val words = definition.shapes.filter(_.quantifier == OperandQuantifier.One).zipWithIndex.map((shape, index) =>
      Word(if (shape.kind.category == OperandKind.Category.Id) index + 1 else 0)
    )
    definition.decoder(RawInstruction(definition.opcodeNumber, words)).toOption
  }

  private def form(definition: Instruction.Definition): View.Form = {
    val number   = definition.opcodeNumber.toInt
    val names    = instance(definition).map(SPIRVInstances.operandNames(_))
    val operands = definition.shapes.zipWithIndex.map((shape, index) =>
      View.OperandSlot(
        names.flatMap(_.lift(index)).getOrElse(s"word ${index + 1}"),
        quantified(shape),
        shape.kind.category.toString
      )
    )
    View.Form(
      definition.opcode.raw,
      Vec[View.Token](View.Token.Text(definition.opcode.raw)) ++ definition.shapes.flatMap(shape =>
        Vec(View.Token.Text(" "), View.Token.Slot(quantified(shape), shape.kind.category.toString))
      ),
      operands,
      definition.shapes.map(_.kind).distinct.flatMap(kind =>
        Option.when(enumerants(kind).nonEmpty)(View.Choice(kind.toString, enumerants(kind)))
      ),
      Vec("Opcode" -> s"$number (0x${number.toHexString})", "Grammar" -> s"SPIR-V ${ISA.version}"),
      encoding = Some(wordLayout.copy(fixed = View.deposit(Vec.tabulate(16)(identity), BigInt(number))))
    )
  }

  /** The form of an extended instruction, an OpExtInst importing it from `set`, under its grammar's operand names. */
  private def extendedForm(set: String, name: String, operands: Vec[String]): View.Form = {
    val number = ExtendedInstruction.names(set)(name)
    View.Form(
      name,
      Vec[View.Token](View.Token.Text(s"OpExtInst $set $name")) ++ operands.flatMap(operand =>
        Vec(View.Token.Text(" "), View.Token.Slot(operand, OperandKind.Category.Id.toString))
      ),
      Vec(
        View.OperandSlot("resultType", OperandKind.IdResultType.toString, OperandKind.Category.Id.toString),
        View.OperandSlot("dest", OperandKind.IdResult.toString, OperandKind.Category.Id.toString)
      ) ++ operands.map(View.OperandSlot(_, OperandKind.IdRef.toString, OperandKind.Category.Id.toString)),
      properties = Vec("Instruction" -> s"$number (0x${number.toHexString})", "Set" -> set)
    )
  }

  private val extendedSets = Vec("GLSL.std.450", "OpenCL.std")

  lazy val all: Vec[View.Entry] =
    (Instruction.definitions.map(definition =>
      View.Entry(Language.SPIRV, definition.opcode.raw, Vec(definition.opcode.raw), () => Vec(form(definition)))
    ) ++ extendedSets.flatMap(set =>
      Vec.from(ExtendedInstruction.grammar.getOrElse(set, Map.empty)).map { case (name, (_, operands)) =>
        View.Entry(
          Language.SPIRV,
          SPIRVInstances.mnemonic(set, name),
          Vec(name),
          () => Vec(extendedForm(set, name, operands))
        )
      }
    )).sortBy(_.mnemonic)

  def entries(target: Option[String]): Vec[View.Entry] = all

  private val RegisterKinds = Vec(
    OperandKind.BuiltIn -> "Built-in variables decorated with BuiltIn, such as invocation and workgroup identifiers.",
    OperandKind.StorageClass -> "Storage classes that pointers and variables live in."
  )

  def registers(target: Option[String]): Vec[View.RegisterFile] = RegisterKinds.map { (kind, description) =>
    View.RegisterFile(
      kind.toString,
      description,
      Instruction.operandKinds.get(kind).fold(Vec.empty[View.Register])(
        _.enumerants.map(value => View.Register(value.name.raw, Some(BigInt(value.value))))
      )
    )
  }

  val binary: Boolean = true

  def assemble(source: String, target: Option[String]): Either[String, String] = for {
    module <- Parser.parse(source).left.map(_.diagnostic.raw)
    bytes  <- ISA.encode(module).left.map(_.message.raw)
  } yield Hex.render(bytes)

  def disassemble(bytes: Vec[Byte], target: Option[String]): Either[String, String] =
    ISA.decode(bytes).left.map(_.message.raw).map(Printer.render(_))

  val assemblyExample: String =
    """OpCapability Shader
      |OpMemoryModel Logical GLSL450
      |OpEntryPoint GLCompute %main "main"
      |OpExecutionMode %main LocalSize 1 1 1
      |%void = OpTypeVoid
      |%function = OpTypeFunction %void
      |%main = OpFunction %void None %function
      |%entry = OpLabel
      |OpReturn
      |OpFunctionEnd
      |""".stripMargin
  val binaryExample: String =
    "03 02 23 07 00 00 01 00 00 00 00 00 01 00 00 00 00 00 00 00 11 00 02 00 01 00 00 00\n"

  /** Every instance of the entry's instruction at the types it supports whose definition renders and whose kernel the
    * VM loads.
    */
  protected def render(entry: View.Entry, form: View.Form): Either[Unavailable, Vec[Described]] = {
    val described = SPIRVInstances.instances(entry.mnemonic).flatMap(instance =>
      instance.described.map(Described(instance.text, _))
    )
    Either.cond(described.nonEmpty, described, pipeline(entry))
  }

  private def definitions: Map[String, Instruction.Definition] = SPIRVInstances.definitions

  private def requirements(definition: Instruction.Definition): Option[InstructionRequirements] =
    InstructionRequirements.all.get(definition.opcodeNumber.toInt)

  /** The VM family that lowers instances of `definition`, those without optional or repeated operands first. */
  private def family(definition: Instruction.Definition): Either[Fault, String] = {
    val probes = instance(definition).toVec ++ SPIRVInstances.instances(definition.opcode.raw).map(_.instruction)
    probes.map(SPIRVLowering.family(_)).find(_.isRight)
      .getOrElse(Left(Fault.Unsupported(s"${definition.opcode} execution is not implemented")))
  }

  /** Capability families with no meaning in a compute emulator, by the names the grammar gives their capabilities. */
  private val outsideCompute: Vec[(family: String, member: String => Boolean)] = Vec(
    "ray tracing" ->
      (name =>
        Vec("RayTracing", "RayQuery", "RayTraversal", "RayCull", "ShaderInvocationReorder", "DisplacementMicromap")
          .exists(name.startsWith(_))
      ),
    "graphics stages" ->
      (name =>
        Vec(
          "Geometry",
          "Tessellation",
          "MeshShading",
          "InterpolationFunction",
          "SampleRateShading",
          "FragmentShader",
          "FragmentBarycentric",
          "FragmentDensity",
          "FragmentFully",
          "FragmentShadingRate",
          "FragmentMask",
          "ShadingRate",
          "DemoteToHelperInvocation",
          "TileImage",
          "QuadControl",
          "StencilExport",
          "MultiView",
          "ShaderViewport",
          "ShaderLayer"
        ).exists(name.startsWith(_))
      ),
    "graphics image and texture extensions" ->
      (name =>
        Vec(
          "Sparse",
          "MinLod",
          "BindlessTexture",
          "DescriptorHeap",
          "TextureBlockMatch",
          "TextureBoxFilter",
          "TextureSampleWeighted",
          "ImageGather",
          "ImageFootprint"
        ).exists(name.startsWith(_))
      ),
    "video motion estimation" -> (name => name.startsWith("SubgroupAvcMotionEstimation")),
    "FPGA"                    -> (name => name.endsWith("ALTERA") || name.contains("FPGA")),
    "work graph node dispatch, which needs a work graph pipeline" -> (name => name.startsWith("ShaderEnqueue")),
    "data graph pipelines, which run no shader invocations"       -> (name => name == "GraphARM"),
    "inline assembly in the target's native ISA"                  -> (name => name == "AsmINTEL"),
    "ESIMD vector compute, whose kernels import GenX intrinsics rather than SPIR-V instructions" ->
      (name => name == "VectorComputeINTEL")
  )

  /** Extended instructions their sets list but define no semantics for. */
  private val withdrawn: Map[String, String] =
    Map("GLSL.std.450 IMix" -> "Removed from GLSL.std.450 before its first release, with no defined semantics.")

  /** Where every capability that enables a form belongs to a family outside compute. */
  private def unmodelled(capabilities: Vec[String]): Option[Unavailable] =
    Option.when(capabilities.nonEmpty && capabilities.forall(name => outsideCompute.exists(_.member(name)))) {
      val families = capabilities.flatMap(name => outsideCompute.find(_.member(name)).map(_.family)).distinct
      Unavailable.NotModelled(s"Requires ${capabilities.mkString(" or ")}: ${families.mkString(", ")}.")
    }

  /** Grammar classes of module-level declarations, which the VM reads as it loads a module rather than runs. */
  private val declarations =
    Set("Annotation", "Constant-Creation", "Debug", "Extension", "Function", "Mode-Setting", "Type-Declaration")

  /** Why the instances of an entry the VM lowers have no definition: the VM rejects them as unimplemented, no staged
    * family takes them, or none renders and loads.
    */
  private def reason(instances: Vec[SPIRVKernel.Instance], family: String): Unavailable =
    instances.flatMap(_.rejection).collectFirst { case fault @ Fault.Unsupported(_) => fault }.fold(
      if (instances.forall(_.rendered == Left(Unrendered.Unstaged(Vec.empty))))
        Unavailable.Mechanics(s"Lowers to the VM's $family, which have no staged semantics.")
      else
        Unavailable.NotInstantiated(
          instances.flatMap(_.rendered.left.toOption).collectFirst { case Unrendered.Undefined(message) => message }
            .orElse(instances.flatMap(_.rejection).headOption.map(_.message.raw))
            .getOrElse("No instance at the types tried renders.")
        )
    )(fault => Unavailable.NotImplemented(fault.message.raw))

  /** From the pipeline: the grammar's requirements, the VM's lowering of the instruction, then the staged semantics and
    * the VM's loading of its instances.
    */
  private def pipeline(entry: View.Entry): Unavailable = {
    val instances = SPIRVInstances.instances(entry.mnemonic)
    entry.mnemonic.split(' ') match {
      case Array(_, _) if withdrawn.contains(entry.mnemonic) =>
        Unavailable.NotModelled(withdrawn(entry.mnemonic))
      case Array(set, name) =>
        unmodelled(ExtendedInstruction.grammar.get(set).flatMap(_.get(name)).fold(Vec.empty[String])(_.capabilities))
          .getOrElse(reason(instances, "extended instructions"))
      case _ =>
        definitions.get(entry.mnemonic).fold[Unavailable](
          Unavailable.NotInstantiated(s"${entry.mnemonic} is not in the grammar.")
        ) { definition =>
          val required     = requirements(definition)
          val grammarClass = required.fold("")(_.grammarClass)
          // the loader reads core declarations, but an extension's only where it defines what they declare
          val core = required.forall(_.version != "None")
          family(definition).fold(
            fault =>
              unmodelled(required.fold(Vec.empty[String])(_.capabilities)).getOrElse(
                if (declarations(grammarClass) && (if (values(grammarClass)) declared(definition, core) else core))
                  Unavailable.Mechanics(s"Read as the module loads, as the grammar's $grammarClass class.")
                else if (declarations(grammarClass))
                  Unavailable.NotImplemented(s"${definition.opcode} is not read when the module loads")
                else Unavailable.NotImplemented(fault.message.raw)
              ),
            reason(instances, _)
          )
        }
    }
  }

  /** Declaration classes whose instructions define a type or a constant, which the loader may not know. */
  private def values(grammarClass: String): Boolean =
    grammarClass == "Type-Declaration" || grammarClass == "Constant-Creation"

  /** Whether the loader defines, or rejects, a probe of the declaration `definition`; one without a result is read
    * where it is `core`.
    */
  private def declared(definition: Instruction.Definition, core: Boolean): Boolean =
    if (definition.shapes.exists(_.kind == OperandKind.IdResult)) instance(definition).exists(SPIRVLowering.declares(_))
    else core
}
