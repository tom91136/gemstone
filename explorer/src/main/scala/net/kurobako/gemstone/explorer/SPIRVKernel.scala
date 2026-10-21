package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.isa.spirv.asm.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable
import scala.util.control.NonFatal

object SPIRVKernel {

  /** A result of `result`, or none, from value operands of `operands`, with literal operand words, the rounding and
    * saturation decorating the result, whether a shader rather than a kernel runs it, and what the literals mean.
    */
  case class Signature(
      result: Option[Shape],
      operands: Vec[Shape],
      literals: Vec[Int] = Vec.empty,
      rounding: Option[IEEE754.Rounding] = None,
      saturated: Boolean = false,
      shader: Boolean = false,
      tag: String = ""
  ) {
    def rounded(mode: IEEE754.Rounding): Signature = copy(rounding = Some(mode))
  }

  /** An instruction at one signature, its ids named and shaped for rendering. */
  case class Instance(
      text: String,
      instruction: Instruction,
      extendedSet: Option[String],
      ids: IdRenderer.Instance,
      signature: Signature
  ) {

    /** The staged definition, or why it does not render. */
    lazy val rendered: Either[Unrendered, Definition] = InstructionSemantics.describe(instruction, ids, extendedSet)

    /** The fault the VM raises for the kernel that runs the instance as it loads it, or for the instance's own op. */
    lazy val rejection: Option[Fault] =
      try {
        val kernel = SPIRVKernel(this)
        Parser.parse(kernel.text) match {
          case Left(error)   => Some(Fault.InvalidRequest(error.diagnostic))
          case Right(module) =>
            val result = module.instructions.collectFirst {
              case Instruction.Name(target, name) if LiteralString.decode(name) == "result" => Operand.id(target)
            }
            val at = module.instructions.indexWhere(found =>
              found.definition.shapes.lift(1).exists(_.kind == OperandKind.IdResult) &&
                found.operands.lift(1).exists(operand => result.contains(Operand.id(operand)))
            )
            SPIRVLowering.failures(
              module,
              if (kernel.shader) Validation.Environment.VulkanCompute else Validation.Environment.OpenCLCompute
            ).fold(Some(_), _.lift(at).flatten)
        }
      } catch { case NonFatal(error) => Some(Fault.InvalidRequest(s"$text has no kernel: $error")) }

    /** The definition, where it renders and the VM loads the kernel that runs the instance. */
    def described: Option[Definition] = rendered.toOption.filter(_ => rejection.isEmpty)
  }

  def rounding(mode: IEEE754.Rounding): String = mode match {
    case IEEE754.Rounding.NearestEven => "RTE"
    case IEEE754.Rounding.TowardZero  => "RTZ"
    case IEEE754.Rounding.Up          => "RTP"
    case IEEE754.Rounding.Down        => "RTN"
    case IEEE754.Rounding.NearestAway => throw IllegalArgumentException("SPIR-V has no ties-away rounding mode")
  }

  /** An operand's kernel parameter: the components of a value as 64-bit slots, or a pointer into memory. */
  // the semantics-check reference binds its inputs to the parameters by name
  @SuppressWarnings(Array("baumkuchen:test-only-member"))
  enum Parameter {
    case Slot(name: String)
    case Pointer(name: String)
  }
}

/** The module that runs `instance` as the only instruction of a one-invocation kernel, or of a shader where its
  * semantics are a shader's: its text, its parameters in order and its result slots by name. Operands load from 64-bit
  * slots, kernel parameters or the shader's block, and results store to them; a pointer operand is a parameter, or is
  * converted from its slot where only its address matters. The instance's result is named `result`.
  */
class SPIRVKernel(instance: SPIRVKernel.Instance) {
  import SPIRVKernel.*

  val shader: Boolean     = instance.signature.shader
  private val ids         = instance.ids
  private val name        = InstructionSemantics.name(instance.instruction, instance.extendedSet)
  private val physical32  = name.startsWith("vload") || name.startsWith("vstore")
  private val addressOnly = Set("OpPtrEqual", "OpPtrNotEqual", "OpPtrDiff")(name)

  private val types     = mutable.LinkedHashMap.empty[String, String]
  private val constants = mutable.LinkedHashMap.empty[String, String]
  // types declared by constants, after them
  private val sized  = mutable.LinkedHashMap.empty[String, String]
  private val before = Vec.newBuilder[String]
  private val after  = Vec.newBuilder[String]

  private def tpe(value: Type): String = {
    val (key, declaration) = value.unranged match {
      case Type.Integer(width, _) => (s"%i$width", s"OpTypeInt $width 0")
      case Type.Float(width)      => (s"%f$width", s"OpTypeFloat $width")
      case Type.Encoded(format)   => (s"%${Type.name(format)}", s"OpTypeFloat 8 ${Type.name(format)}EXT")
      case _                      => ("%bool", "OpTypeBool")
    }
    types(key) = declaration
    key
  }

  private def tpe(shape: Shape): String = shape match {
    case Shape.Scalar(value)            => tpe(value)
    case Shape.Vector(component, lanes) =>
      val lane = tpe(component)
      val key  = s"%v$lanes${lane.drop(1)}"
      types(key) = s"OpTypeVector $lane $lanes"
      key
    case Shape.Matrix(component, rows, columns) =>
      val column = tpe(Shape.Vector(component, rows))
      val key    = s"%m$columns${column.drop(1)}"
      types(key) = s"OpTypeMatrix $column $columns"
      key
    case Shape.Structure(members) =>
      val names = members.map(tpe(_))
      val key   = s"%s_${names.map(_.drop(1)).mkString("_")}"
      types(key) = s"OpTypeStruct ${names.mkString(" ")}"
      key
    case Shape.Pointer(pointee) =>
      val target = tpe(pointee)
      val key    = s"%p${target.drop(1)}"
      types(key) = s"OpTypePointer CrossWorkgroup $target"
      key
    // a use below zero is SPV_NV_cooperative_matrix's type, which has none
    case Shape.Cooperative(component, rows, columns, use) =>
      val lane       = tpe(component)
      val dimensions = Vec(3L, rows.toLong, columns.toLong).map(constant(_, 32))
      if (use < 0) {
        val key = s"%nv${rows}x$columns${lane.drop(1)}"
        sized(key) = s"OpTypeCooperativeMatrixNV $lane ${dimensions.mkString(" ")}"
        key
      } else {
        val key = s"%k${rows}x${columns}u$use${lane.drop(1)}"
        sized(key) = s"OpTypeCooperativeMatrixKHR $lane ${dimensions.mkString(" ")} ${constant(use.toLong, 32)}"
        key
      }
  }

  private def constant(value: Long, width: Int): String = {
    val key = s"%c${width}_$value"
    constants(key) = s"OpConstant ${tpe(Type.unsigned(width.unsafe))} $value"
    key
  }

  private val index = if (physical32) 32 else 64

  /** Each scalar of `shape` in component order, as the indices that extract it. */
  private def paths(shape: Shape): Vec[(tpe: Type, path: Vec[Int])] = shape match {
    case Shape.Scalar(value)                    => Vec((value, Vec.empty))
    case Shape.Vector(component, lanes)         => Vec.tabulate(lanes)(lane => (component, Vec(lane)))
    case Shape.Matrix(component, rows, columns) =>
      Vec.tabulate(rows * columns)(lane => (component, Vec(lane / rows, lane % rows)))
    case Shape.Structure(members) =>
      members.zipWithIndex.flatMap((member, at) => paths(member).map((value, path) => (value, at +: path)))
    case Shape.Pointer(_)                               => Vec((Shape.Address, Vec.empty))
    case Shape.Cooperative(component, rows, columns, _) =>
      Vec.tabulate(rows * columns)(lane => (component, Vec(lane)))
  }

  private val fresh = Iterator.from(0).map(index => s"%t$index")

  private def fromBits(source: String, value: Type): String = {
    val target = fresh.next()
    value.unranged match {
      case Type.Integer(width, _) if width == 64 => before += s"$target = OpCopyObject %i64 $source"
      case Type.Integer(_, _)                    => before += s"$target = OpUConvert ${tpe(value)} $source"
      case Type.Float(width) if width == 64      => before += s"$target = OpBitcast ${tpe(value)} $source"
      case Type.Float(width)                     =>
        val bits = fresh.next()
        before += s"$bits = OpUConvert ${tpe(Type.unsigned(width))} $source"
        before += s"$target = OpBitcast ${tpe(value)} $bits"
      case Type.Encoded(format) =>
        val bits = fresh.next()
        before += s"$bits = OpUConvert ${tpe(Type.unsigned(Type.width(format.width)))} $source"
        before += s"$target = OpBitcast ${tpe(value)} $bits"
      case _ => before += s"$target = OpINotEqual ${tpe(Type.Bool)} $source ${constant(0L, 64)}"
    }
    target
  }

  private def toBits(source: String, value: Type): String = {
    val target = fresh.next()
    value.unranged match {
      case Type.Integer(width, _) if width == 64 => after += s"$target = OpCopyObject %i64 $source"
      case Type.Integer(_, _)                    => after += s"$target = OpUConvert ${tpe(Type.unsigned(64))} $source"
      case Type.Float(width) if width == 64      => after += s"$target = OpBitcast ${tpe(Type.unsigned(64))} $source"
      case Type.Float(width)                     =>
        val bits = fresh.next()
        after += s"$bits = OpBitcast ${tpe(Type.unsigned(width))} $source"
        after += s"$target = OpUConvert ${tpe(Type.unsigned(64))} $bits"
      case Type.Encoded(format) =>
        val bits = fresh.next()
        after += s"$bits = OpBitcast ${tpe(Type.unsigned(Type.width(format.width)))} $source"
        after += s"$target = OpUConvert ${tpe(Type.unsigned(64))} $bits"
      case _ =>
        after += s"$target = OpSelect ${tpe(Type.unsigned(64))} $source ${constant(1L, 64)} ${constant(0L, 64)}"
    }
    target
  }

  private val operands = SPIRVStaged.operandIds(
    instance.instruction,
    if (instance.extendedSet.nonEmpty) 4 else 2
  ).operands.take(instance.signature.operands.size)

  private val kinds = instance.instruction.operands.map(operand => Operand.id(operand) -> operand.kind).toMap

  private def scope(id: Int): Boolean =
    kinds.get(id).exists(kind => kind == OperandKind.IdScope || kind == OperandKind.IdMemorySemantics)

  private val parameterList = Vec.newBuilder[(Parameter, String)]
  private val slotCount     = operands.filterNot(scope(_)).flatMap(id =>
    ids.shapes.get(id).fold(Vec.empty[Int]) {
      case Shape.Pointer(_) if !addressOnly => Vec.empty[Int]
      case shape                            => paths(shape).map(_ => 0)
    }
  ).size
  private val slots = Iterator.from(0)

  /** The slot value `index` as a 64-bit integer: a kernel parameter, or a load from the shader's block. */
  private def slot(name: String): String =
    if (shader) {
      val pointer = fresh.next()
      val loaded  = fresh.next()
      before += s"$pointer = OpAccessChain %pslot %data ${constant(slots.next().toLong, 32)}"
      before += s"$loaded = OpLoad %i64 $pointer"
      parameterList += ((Parameter.Slot(name), ""))
      loaded
    } else {
      val parameter = fresh.next()
      parameterList += ((Parameter.Slot(name), s"$parameter = OpFunctionParameter %i64"))
      parameter
    }

  /** The value of operand `id`, built from its components. */
  private def operand(id: Int): String = {
    val shape = ids.shapes(id)
    val names = shape.components(ids.names(id))
    shape match {
      case Shape.Pointer(_) if addressOnly =>
        val pointer = fresh.next()
        before += s"$pointer = OpConvertUToPtr ${tpe(shape)} ${slot(names.head.name)}"
        pointer
      case Shape.Pointer(_) =>
        val parameter = fresh.next()
        parameterList += ((Parameter.Pointer(names.head.name), s"$parameter = OpFunctionParameter ${tpe(shape)}"))
        parameter
      case Shape.Scalar(value)        => fromBits(slot(names.head.name), value)
      case Shape.Vector(component, _) =>
        val lanes = names.map(lane => fromBits(slot(lane.name), component))
        val built = fresh.next()
        before += s"$built = OpCompositeConstruct ${tpe(shape)} ${lanes.mkString(" ")}"
        built
      case Shape.Matrix(component, rows, columns) =>
        val lanes      = names.map(lane => fromBits(slot(lane.name), component))
        val columnType = tpe(Shape.Vector(component, rows))
        val vectors    = Vec.newBuilder[String]
        lanes.grouped(rows).foreach { column =>
          val built = fresh.next()
          before += s"$built = OpCompositeConstruct $columnType ${column.mkString(" ")}"
          vectors += built
        }
        val built = fresh.next()
        before += s"$built = OpCompositeConstruct ${tpe(shape)} ${vectors.result().mkString(" ")}"
        built
      case Shape.Cooperative(component = component) =>
        val lanes  = names.map(lane => fromBits(slot(lane.name), component))
        val filled = fresh.next()
        before += s"$filled = OpCompositeConstruct ${tpe(shape)} ${lanes.head}"
        lanes.zipWithIndex.drop(1).foldLeft(filled) { case (previous, (lane, at)) =>
          val inserted = fresh.next()
          before += s"$inserted = OpCompositeInsert ${tpe(shape)} $lane $previous $at"
          inserted
        }
      case Shape.Structure(_) => throw IllegalArgumentException(s"structure operand %$id")
    }
  }

  private val values = operands.map { id =>
    if (scope(id)) id -> constant(if (kinds(id) == OperandKind.IdScope) 1L else 0L, 32)
    else id           -> operand(id)
  }.toMap

  private val resultShape = ids.shapes.get(2)

  private val instruction: String = {
    val operandsOf = instance.instruction.operands
    val ext        = instance.extendedSet.nonEmpty
    val rendered   = operandsOf.zipWithIndex.map { (operand, at) =>
      val word = Operand.id(operand)
      operand.kind match {
        case OperandKind.IdResultType => resultShape.fold("%void")(tpe(_))
        case OperandKind.IdResult     => "%result"
        case _ if ext && at == 2      => "%ext"
        case _ if ext && at == 3      => word.toString
        case kind
            if kind.category == OperandKind.Category.Id && values.contains(word) &&
              (!ext || at - 4 < operands.size) =>
          values(word)
        case _ if ext => word.toString
        case _        => f"!0x$word%08x"
      }
    }
    s"%result = ${instance.instruction.definition.opcode.raw} ${rendered.filterNot(_ == "%result").mkString(" ")}"
  }

  val results: Vec[String] = resultShape.fold(Vec.empty[String])(_.components(ids.names(2)).map(_.name))

  resultShape.foreach { shape =>
    paths(shape).zipWithIndex.foreach { case ((value, path), at) =>
      val extracted =
        if (path.isEmpty) "%result"
        else {
          val lane = fresh.next()
          after += s"$lane = OpCompositeExtract ${tpe(value)} %result ${path.mkString(" ")}"
          lane
        }
      val bits    = toBits(extracted, value)
      val pointer = fresh.next()
      if (shader) after += s"$pointer = OpAccessChain %pslot %data ${constant((slotCount + at).toLong, 32)}"
      else after += s"$pointer = OpInBoundsPtrAccessChain %pout %out ${constant(at.toLong, index)}"
      after += s"OpStore $pointer $bits"
    }
  }

  // the semantics-check reference binds its inputs to the parameters in order
  @SuppressWarnings(Array("baumkuchen:test-only-member"))
  val parameters: Vec[Parameter] = parameterList.result().map(_._1)

  private val decorations = Vec.from(instance.signature.rounding.map { mode =>
    s"OpDecorate %result FPRoundingMode ${rounding(mode)}"
  }) ++ Vec.from(Option.when(instance.signature.saturated)("OpDecorate %result SaturatedConversion"))

  private val capabilities =
    if (shader) Vec("Shader", "Int8", "Int16", "Int64", "Float16", "Float64")
    else
      Vec(
        "Addresses",
        "Kernel",
        "Int8",
        "Int16",
        "Int64",
        "Float16",
        "Float64",
        "Float8EXT",
        "Int64Atomics",
        "Vector16",
        "DotProduct",
        "DotProductInputAll",
        "DotProductInput4x8Bit",
        "DotProductInput4x8BitPacked",
        "AtomicFloat16AddEXT",
        "AtomicFloat32AddEXT",
        "AtomicFloat64AddEXT",
        "AtomicFloat16MinMaxEXT",
        "AtomicFloat32MinMaxEXT",
        "AtomicFloat64MinMaxEXT",
        "IntegerFunctions2INTEL",
        "TernaryBitwiseFunctionINTEL",
        "BFloat16ConversionINTEL",
        "TensorFloat32RoundingINTEL",
        "DotProductFloat16AccFloat32VALVE",
        "DotProductFloat16AccFloat16VALVE",
        "DotProductFloat8AccFloat32VALVE",
        "PoisonFreezeKHR",
        "BitcastExtractEXT",
        "CooperativeMatrixKHR",
        "CooperativeMatrixNV"
      )

  val text: String = {
    val parameterLines = parameterList.result().map(_._2).filter(_.nonEmpty)
    val slotTypes      = Vec.fill(slotCount + results.size)("%i64")
    types("%i64") = "OpTypeInt 64 0"
    if (shader) {
      types("%block") = s"OpTypeStruct ${slotTypes.mkString(" ")}"
      types("%pblock") = "OpTypePointer StorageBuffer %block"
      types("%pslot") = "OpTypePointer StorageBuffer %i64"
    } else types("%pout") = "OpTypePointer CrossWorkgroup %i64"
    types("%void") = "OpTypeVoid"
    val signature = parameterLines.map(_.split(' ').last)
    types("%fn") = s"OpTypeFunction %void${if (shader) "" else " %pout"}${signature.map(" " + _).mkString}"
    (capabilities.map(capability => s"OpCapability $capability") ++
      instance.extendedSet.map(set => s"%ext = OpExtInstImport \"$set\"") ++
      Vec(
        if (shader) "OpMemoryModel Logical GLSL450"
        else s"OpMemoryModel ${if (physical32) "Physical32" else "Physical64"} OpenCL",
        if (shader) "OpEntryPoint GLCompute %main \"main\" %data" else "OpEntryPoint Kernel %main \"main\""
      ) ++
      (if (shader)
         Vec("OpExecutionMode %main LocalSize 1 1 1", "OpDecorate %block Block") ++
           slotTypes.indices.map(at => s"OpMemberDecorate %block $at Offset ${at * 8}") ++
           Vec("OpDecorate %data DescriptorSet 0", "OpDecorate %data Binding 0")
       else Vec.empty[String]) ++ Vec("OpName %result \"result\"") ++ decorations ++
      types.toVec.map((name, declaration) => s"$name = $declaration") ++
      constants.toVec.map((name, declaration) => s"$name = $declaration") ++
      sized.toVec.map((name, declaration) => s"$name = $declaration") ++
      Vec.from(Option.when(shader)("%data = OpVariable %pblock StorageBuffer")) ++
      Vec("%main = OpFunction %void None %fn") ++
      Vec.from(Option.when(!shader)("%out = OpFunctionParameter %pout")) ++
      parameterLines ++ Vec("%entry = OpLabel") ++ before.result() ++ Vec(instruction) ++ after.result() ++
      Vec("OpReturn", "OpFunctionEnd")).mkString("\n")
  }
}
