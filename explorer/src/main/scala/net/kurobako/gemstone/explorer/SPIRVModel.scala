package net.kurobako.gemstone.explorer

import java.util.concurrent.ConcurrentHashMap
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

  private def operandDetail(shape: OperandShape, constraint: Option[OperandConstraint]): String =
    constraint.fold(shape.kind.category.toString)(value => s"${shape.kind.category}, must be ${value.describe}")

  /** The values a Scope or MemorySemantics operand takes, listed under its constraint. */
  private def domainOf(constraint: OperandConstraint): Option[View.Domain] = constraint match {
    case OperandConstraint.ScopeId           => Some(View.Domain("Scope", valuesOf(OperandKind.Scope)))
    case OperandConstraint.MemorySemanticsId =>
      Some(View.Domain("MemorySemantics", valuesOf(OperandKind.MemorySemantics)))
    case _ => None
  }

  private def valuesOf(kind: OperandKind): Vec[(String, String)] =
    Instruction.operandKinds.get(kind).fold(Vec.empty[(String, String)])(
      _.enumerants.map(value => value.value.toString -> value.name.raw)
    )

  private def form(definition: Instruction.Definition): View.Form = {
    val number   = definition.opcodeNumber.toInt
    val names    = instance(definition).map(SPIRVInstances.operandNames(_))
    val operands = definition.shapes.zipWithIndex.map { (shape, index) =>
      val constraint = OperandConstraintCheck.of(definition.opcode.raw, index, shape.kind)
      View.OperandSlot(
        names.flatMap(_.lift(index)).getOrElse(s"word ${index + 1}"),
        quantified(shape),
        operandDetail(shape, constraint),
        constraint.flatMap(domainOf)
      )
    }
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

  override def semanticsOf(entry: View.Entry, form: View.Form): Semantics = {
    val outcomes = machined.computeIfAbsent((entry, form), _ => MachineSemantics.machine(entry.mnemonic))
    if (outcomes.nonEmpty) {
      withMachineOutcomes(entry, form, outcomes)
    } else
      definitions.get(entry.mnemonic).flatMap(definition =>
        loaded(definition).map(Semantics.LoadTime(_)).orElse(noOp(definition).map(Semantics.NoOp(_)))
      ).orElse(noOps.get(entry.mnemonic).map(Semantics.NoOp(_)))
        .getOrElse(Semantics.of(semantics(entry, form), Vec.empty))
  }

  private val machined =
    ConcurrentHashMap[(View.Entry, View.Form), Vec[(instance: String, outcome: Either[Unrendered, Definition])]]()

  override protected def clearOutcomes(): Unit = machined.clear()

  private val loadedReasons = ConcurrentHashMap[String, Option[String]]()

  /** Why the VM reads `definition` as it loads a module, whether it lowers it to an op that does nothing or the loader
    * reads it alone.
    */
  private def loaded(definition: Instruction.Definition): Option[String] =
    loadedReasons.computeIfAbsent(
      definition.opcode.raw,
      _ => loadTime(definition, family(definition).isRight).orElse(loadTimeReasons.get(definition.opcode.raw))
    )

  private def grammarClass(definition: Instruction.Definition): String =
    requirements(definition).fold("")(_.grammarClass)

  /** Why `definition` has no effect of its own: it dispatches to an extended instruction, or the grammar files it with
    * the miscellaneous instructions and gives it neither operands nor a result.
    */
  private def noOp(definition: Instruction.Definition): Option[String] =
    if (dispatching(definition.opcode.raw)) Some("an extended instruction is described under its own entry.")
    else
      Option.when(grammarClass(definition) == "Miscellaneous" && definition.shapes.isEmpty)(
        s"${definition.opcode} takes no operands and has no result."
      )

  /** Instructions with no effect of their own, and why the VM has none. */
  private val noOps: Map[String, String] = Map(
    "OpenCL.std prefetch" -> "a cache hint with no observable effect.",
    "OpLoopMerge"         -> "a structured merge; the VM reconverges lanes there, outside the machine semantics.",
    "OpSelectionMerge"    -> "a structured merge; the VM reconverges lanes there, outside the machine semantics.",
    "OpLoopControlINTEL"  -> "a loop control hint with no observable effect.",
    "OpAssumeTrueKHR"     -> "an assumption the VM does not check.",
    "OpLifetimeStart"     -> "a lifetime marker; the VM checks only that its range lies in storage.",
    "OpLifetimeStop"      -> "a lifetime marker; the VM checks only that its range lies in storage.",
    "OpCommitReadPipe" -> "packets are visible once reserved and transferred, so a commit has nothing left to publish.",
    "OpCommitWritePipe" ->
      "packets are visible once reserved and transferred, so a commit has nothing left to publish.",
    "OpGroupCommitReadPipe" ->
      "packets are visible once reserved and transferred, so a commit has nothing left to publish.",
    "OpGroupCommitWritePipe" ->
      "packets are visible once reserved and transferred, so a commit has nothing left to publish."
  )

  /** Instructions the loader resolves as the module loads, which the grammar files outside its declaration classes. */
  private val loadTimeReasons: Map[String, String] = Map(
    "OpAliasDomainDeclINTEL"             -> "an alias domain the loader reads, which only permits optimisations.",
    "OpAliasScopeDeclINTEL"              -> "an alias scope the loader reads, which only permits optimisations.",
    "OpAliasScopeListDeclINTEL"          -> "an alias scope list the loader reads, which only permits optimisations.",
    "OpConstantFunctionPointerINTEL"     -> "a function pointer constant the loader resolves to its function.",
    "OpCompositeConstructContinuedINTEL" -> "a continuation the loader merges into the construct it follows.",
    "OpSizeOf"              -> "the size of the pointee's layout, which the VM computes as the module loads.",
    "OpConstantPipeStorage" -> "a pipe storage constant the loader lays out as a pipe header.",
    "OpTypeTensorARM"       -> "a tensor type the loader reads, whose rank and element type its accesses use."
  )

  /** Grammar classes of module-level declarations, which the VM reads as it loads a module rather than runs. */
  private val declarations =
    Set("Annotation", "Constant-Creation", "Debug", "Extension", "Function", "Mode-Setting", "Type-Declaration")

  /** Extension-class instructions that execute, dispatching to the extended instruction set they name. */
  private val dispatching = Set("OpExtInst", "OpExtInstWithForwardRefsKHR")

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
      case _ if dispatching(entry.mnemonic) =>
        Unavailable.Mechanics("dispatches to the extended instruction; see the extended instruction set entries")
      case _ if entry.mnemonic.matches("OpDPd[xy].*|OpFwidth.*") =>
        Unavailable.NotModelled("derivatives need fragment quads, which compute does not have.")
      case _ =>
        definitions.get(entry.mnemonic).fold[Unavailable](
          Unavailable.NotInstantiated(s"${entry.mnemonic} is not in the grammar.")
        ) { definition =>
          family(definition).fold(
            fault =>
              unmodelled(requirements(definition).fold(Vec.empty[String])(_.capabilities)).getOrElse(
                loadTime(definition).map(Unavailable.Mechanics(_)).getOrElse(
                  if (declarations(grammarClass(definition)))
                    Unavailable.NotImplemented(s"${definition.opcode} is not read when the module loads")
                  else Unavailable.NotImplemented(fault.message.raw)
                )
              ),
            reason(instances, _)
          )
        }
    }
  }

  /** Why the VM reads `definition` as it loads a module rather than runs it: the grammar files it in a declaration
    * class, and the VM lowers it, or the loader reads core declarations, but an extension's only where it defines what
    * they declare.
    */
  private def loadTime(definition: Instruction.Definition, lowered: Boolean = false): Option[String] = {
    val declaration = grammarClass(definition)
    val core        = requirements(definition).forall(_.version != "None")
    Option.when(
      !dispatching(definition.opcode.raw) && declarations(declaration) &&
        (lowered || (if (values(declaration)) declared(definition, core) else core))
    )(s"Read as the module loads, as the grammar's $declaration class.")
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
