package net.kurobako.gemstone.isa.spirv.v1_6

import VMCollectives.*
import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable
import scala.util.control.NonFatal

private object VMCompiler {
  import Operand.*

  // OpSpecConstantOp can embed any pure opcode; the evaluator supports these two.
  private val CompositeExtractOpcode = Instruction.Definition.CompositeExtract.opcodeNumber.toInt.toLong
  private val UDivOpcode             = Instruction.Definition.UDiv.opcodeNumber.toInt.toLong

  private def isFunctionEnd(instruction: Instruction): Boolean = instruction match {
    case Instruction.FunctionEnd() => true
    case _                         => false
  }

  def compileUnchecked(module: Module): Either[Fault, Program] =
    try compileModule(module)
    catch {
      case NonFatal(error) =>
        Left(
          Fault.InvalidTask(
            s"malformed SPIR-V execution state: ${Option(error.getMessage).getOrElse(error.getClass.getSimpleName)}"
          )
        )
    }

  def compile(
      module: Module,
      environment: Validation.Environment
  ): Either[Fault, Program] =
    Validation.validate(module, environment) match {
      case ValidationOutcome.Accepted         => compileUnchecked(module)
      case ValidationOutcome.Rejected(issues) =>
        Left(Fault.InvalidTask(s"invalid SPIR-V module: ${issues.map(_.message).mkString("; ")}"))
      case ValidationOutcome.Incomplete(issues) =>
        Left(Fault.Unsupported(s"unsupported SPIR-V module: ${issues.map(_.message).mkString("; ")}"))
    }

  // Module compilation fills indexed tables in one pass and stops at the first malformed instruction.
  @SuppressWarnings(
    Array(
      "scalafix:DisableSyntax.var",
      "scalafix:DisableSyntax.return"
    )
  )
  private def compileModule(module: Module): Either[Fault, Program] = {
    val types                   = mutable.HashMap.empty[Int, RuntimeType]
    val constants               = mutable.HashMap.empty[Int, Constant]
    val globals                 = mutable.HashMap.empty[Int, Global]
    val descriptorSets          = mutable.HashMap.empty[Int, Int >= 0]
    val descriptorSlots         = mutable.HashMap.empty[Int, Int >= 0]
    val builtins                = mutable.HashMap.empty[Int, Int]
    val arrayStrides            = mutable.HashMap.empty[Int, Int]
    val memberOffsets           = mutable.HashMap.empty[(Int, Int), Int]
    val packedStructures        = mutable.HashSet.empty[Int]
    val byValueParameters       = mutable.HashSet.empty[Int]
    val saturatedConversions    = mutable.HashSet.empty[Int]
    val roundingModes           = mutable.HashMap.empty[Int, Int]
    val extendedInstructionSets = mutable.HashMap.empty[Int, NonEmpty[String]]
    val entryPoints             = mutable.HashMap.empty[NonEmpty[String], Int]
    val entryPointInterfaces    = mutable.HashMap.empty[NonEmpty[String], Set[Int]]
    val vulkanEntries           = mutable.HashSet.empty[NonEmpty[String]]
    val subgroupSizes           = mutable.HashMap.empty[Int, Int]
    val workGroupSizes          = mutable.HashMap.empty[Int, LaunchGeometry.Dimensions]
    val workGroupSizeHints      = mutable.HashMap.empty[Int, LaunchGeometry.Dimensions]
    val functions               = mutable.HashMap.empty[Int, Function]
    val functionNames           = mutable.HashMap.empty[Int, String]

    module.instructions.foreach {
      case Instruction.Name(target, name) =>
        functionNames.update(id(target), literalString(name))
      case Instruction.Decorate(target, decoration)
          if decoration.words.length >= 2 && unsigned(decoration) == 41L =>
        // Optimized/linker-produced modules may omit OpName for imported
        // builtins but retain their LinkageAttributes decoration.
        functionNames.update(
          id(target),
          literalString(decoration.copy(words =
            decoration.words.drop(1).unsafe
          ))
        )
      case _ => ()
    }

    var index = 0
    while (index < module.instructions.size) {
      module.instructions(index) match {
        case Instruction.TypeVoid(dest)                   => types.update(id(dest), RuntimeType.Void)
        case Instruction.TypeBool(dest)                   => types.update(id(dest), RuntimeType.Bool)
        case Instruction.TypeInt(dest, width, signedness) =>
          val bits = id(width)
          if (bits < 8 || bits > 64 || bits % 8 != 0)
            return Left(Fault.Unsupported(s"SPIR-V integer width $bits is unsupported"))
          // The preceding guard proves the SPIR-V scalar bit-width range at the decode boundary.
          types.update(
            id(dest),
            RuntimeType.Integer(bits.unsafe[Int where Between[1, 64]], unsigned(signedness) != 0L)
          )
        case Instruction.TypeFloat(dest, width, _) =>
          val bits = id(width)
          if (!Vec(16, 32, 64).contains(bits))
            return Left(
              Fault.Unsupported(s"SPIR-V floating-point width $bits is unsupported")
            )
          // The preceding closed-domain check proves the supported interchange width.
          types.update(id(dest), RuntimeType.Floating(bits.unsafe[OneOf[(16, 32, 64)]]))
        case Instruction.TypeVector(dest, component, count) =>
          val componentCount = id(count)
          if (componentCount <= 0)
            return Left(Fault.InvalidTask(s"SPIR-V vector component count ${unsigned(count)} is invalid"))
          types.update(id(dest), RuntimeType.VectorType(id(component), componentCount))
        case Instruction.TypeArray(dest, element, length) =>
          val count = constants.get(id(length)).flatMap(scalarBits(_)) match {
            case Some(value) => value
            case None        =>
              return Left(
                Fault.InvalidTask(
                  s"SPIR-V array length %${id(length)} is not a scalar constant"
                )
              )
          }
          types.update(id(dest), RuntimeType.Array(id(element), count))
        case Instruction.TypeRuntimeArray(dest, element) =>
          // A zero count marks an unsized descriptor array; accesses use the external allocation's bounds.
          types.update(id(dest), RuntimeType.Array(id(element), 0L))
        case Instruction.TypeStruct(dest, members) =>
          types.update(id(dest), RuntimeType.Structure(members.map(id(_))))
        case Instruction.TypePointer(dest, storageClass, dataType) =>
          types.update(id(dest), RuntimeType.Pointer(id(storageClass), id(dataType)))
        case Instruction.TypeFunction(dest, result, parameters) =>
          types.update(id(dest), RuntimeType.Function(id(result), parameters.map(id(_))))
        case Instruction.TypeEvent(dest)                => types.update(id(dest), RuntimeType.Event)
        case Instruction.ConstantTrue(resultType, dest) =>
          constants.update(id(dest), Constant.Bits(id(resultType), Vec(1L)))
        case Instruction.SpecConstantTrue(resultType, dest) =>
          constants.update(id(dest), Constant.Bits(id(resultType), Vec(1L)))
        case Instruction.ConstantFalse(resultType, dest) =>
          constants.update(id(dest), Constant.Bits(id(resultType), Vec(0L)))
        case Instruction.SpecConstantFalse(resultType, dest) =>
          constants.update(id(dest), Constant.Bits(id(resultType), Vec(0L)))
        case Instruction.Constant(resultType, dest, value) =>
          constants.update(id(dest), Constant.Bits(id(resultType), Vec(literalBits(value))))
        case Instruction.SpecConstant(resultType, dest, value) =>
          constants.update(id(dest), Constant.Bits(id(resultType), Vec(literalBits(value))))
        case Instruction.ConstantComposite(resultType, dest, constituents) =>
          constantComposite(types, constants, resultType, dest, constituents) match {
            case Left(error)  => return Left(error)
            case Right(value) => constants.update(id(dest), value)
          }
        case Instruction.SpecConstantComposite(resultType, dest, constituents) =>
          constantComposite(types, constants, resultType, dest, constituents) match {
            case Left(error)  => return Left(error)
            case Right(value) => constants.update(id(dest), value)
          }
        case Instruction.SpecConstantOp(resultType, dest, instructionOpcode, srcs) =>
          val operands = srcs.map(unsigned(_))
          val opcode   = unsigned(instructionOpcode)
          val result   =
            if (operands.size != 2) None
            else if (opcode == CompositeExtractOpcode)
              constants.get(Math.toIntExact(operands(0))).flatMap {
                case Constant.Bits(_, lanes) if operands(1) < lanes.size => Some(lanes(operands(1).toInt))
                case _                                                   => None
              }
            else if (opcode == UDivOpcode)
              for {
                left  <- constants.get(Math.toIntExact(operands(0))).flatMap(scalarBits(_))
                right <- constants.get(Math.toIntExact(operands(1))).flatMap(scalarBits(_))
                if right != 0L
              } yield java.lang.Long.divideUnsigned(left, right)
            else None
          result match {
            case Some(value) => constants.update(id(dest), Constant.Bits(id(resultType), Vec(value)))
            case None        =>
              return Left(Fault.Unsupported(s"SPIR-V specialization constant operation $opcode is unsupported"))
          }
        case Instruction.ConstantNull(resultType, dest) =>
          zeroValue(types, resultType, dest, "OpConstantNull") match {
            case Left(error)  => return Left(error)
            case Right(value) => constants.update(id(dest), value)
          }
        case Instruction.Undef(resultType, dest) =>
          zeroValue(types, resultType, dest, "OpUndef") match {
            case Left(error)  => return Left(error)
            case Right(value) => constants.update(id(dest), value)
          }
        case Instruction.Variable(resultType, dest, storageClass, initializer) =>
          globals.update(
            id(dest),
            Global(id(resultType), id(storageClass), initializer.map(id(_)))
          )
        case Instruction.Decorate(target, decoration) if decoration.words.length >= 2 && unsigned(decoration) == 11L =>
          builtins.update(id(target), decoration.words(1).raw)
        case Instruction.Decorate(target, decoration)
            if decoration.words.length >= 2 && unsigned(decoration) == Decoration.Binding =>
          decoration.words(1).raw.refine[Int >= 0] match {
            case Left(_) => return Left(Fault.Unsupported(
                s"SPIR-V descriptor binding ${decoration.words(1).toUnsigned.toLong} exceeds the supported index range"
              ))
            case Right(value) => descriptorSlots.update(id(target), value)
          }
        case Instruction.Decorate(target, decoration)
            if decoration.words.length >= 2 && unsigned(decoration) == Decoration.DescriptorSet =>
          decoration.words(1).raw.refine[Int >= 0] match {
            case Left(_) => return Left(Fault.Unsupported(
                s"SPIR-V descriptor set ${decoration.words(1).toUnsigned.toLong} exceeds the supported index range"
              ))
            case Right(value) => descriptorSets.update(id(target), value)
          }
        case Instruction.Decorate(target, decoration) if decoration.words.length >= 2 && unsigned(decoration) == 6L =>
          arrayStrides.update(id(target), Math.toIntExact(decoration.words(1).toUnsigned.toLong))
        case Instruction.Decorate(target, decoration) if unsigned(decoration) == 10L =>
          val _ = packedStructures.add(id(target))
        case Instruction.Decorate(target, decoration)
            if decoration.words.length >= 2 && unsigned(decoration) == 38L &&
              decoration.words(1).toUnsigned.toLong == 2L =>
          val _ = byValueParameters.add(id(target)) // FuncParamAttr ByVal
        case Instruction.Decorate(target, decoration) if unsigned(decoration) == 28L =>
          val _ = saturatedConversions.add(id(target))
        case Instruction.Decorate(target, decoration) if decoration.words.length >= 2 && unsigned(decoration) == 39L =>
          roundingModes.update(id(target), decoration.words(1).raw)
        case Instruction.MemberDecorate(structureType, member, decoration)
            if decoration.words.length >= 2 && unsigned(decoration) == 35L =>
          memberOffsets.update(
            id(structureType) -> Math.toIntExact(unsigned(member)),
            Math.toIntExact(decoration.words(1).toUnsigned.toLong)
          )
        case Instruction.ExtInstImport(dest, name) =>
          val decodedName = literalString(name)
          decodedName.refine[NonEmpty[String]] match {
            case Left(_) =>
              return Left(Fault.InvalidTask("SPIR-V extended instruction-set name is empty"))
            case Right(instructionSetName) => extendedInstructionSets.update(id(dest), instructionSetName)
          }
        case Instruction.EntryPoint(executionModel, entryPoint, name, interfaces) =>
          val decodedName = literalString(name)
          decodedName.refine[NonEmpty[String]] match {
            case Left(_) =>
              return Left(
                Fault.InvalidTask(s"invalid SPIR-V entry-point name '$decodedName'")
              )
            case Right(kernelName) =>
              entryPoints.update(kernelName, id(entryPoint))
              entryPointInterfaces.update(kernelName, interfaces.view.map(id).toSet)
              if (unsigned(executionModel) == 5L) {
                val _ = vulkanEntries.add(kernelName)
              }
          }
        case Instruction.ExecutionMode(entryPoint, mode) =>
          unsigned(mode) match {
            case 35L if mode.words.length >= 2 =>
              val size = mode.words(1).toUnsigned.toLong
              if (size == 0L || size > Int.MaxValue.toLong)
                return Left(Fault.InvalidTask(s"SPIR-V SubgroupSize $size is invalid"))
              subgroupSizes.update(id(entryPoint), size.toInt)
            case 17L if mode.words.length >= 4 =>
              localSize(mode.words(1), mode.words(2), mode.words(3)).foreach(size =>
                workGroupSizes.update(id(entryPoint), size)
              )
            case 18L if mode.words.length >= 4 =>
              localSize(mode.words(1), mode.words(2), mode.words(3)).foreach(size =>
                workGroupSizeHints.update(id(entryPoint), size)
              )
            case 38L if mode.words.length >= 4 =>
              localSizeId(constants, mode.words(1), mode.words(2), mode.words(3))
                .foreach(size => workGroupSizes.update(id(entryPoint), size))
            case 39L if mode.words.length >= 4 =>
              localSizeId(constants, mode.words(1), mode.words(2), mode.words(3))
                .foreach(size => workGroupSizeHints.update(id(entryPoint), size))
            case _ => ()
          }
        case Instruction.ExecutionModeId(entryPoint, mode) =>
          unsigned(mode) match {
            case 38L if mode.words.length >= 4 =>
              localSizeId(constants, mode.words(1), mode.words(2), mode.words(3))
                .foreach(size => workGroupSizes.update(id(entryPoint), size))
            case 39L if mode.words.length >= 4 =>
              localSizeId(constants, mode.words(1), mode.words(2), mode.words(3))
                .foreach(size => workGroupSizeHints.update(id(entryPoint), size))
            case _ => ()
          }
        case Instruction.Function(_, dest, _, _) =>
          val functionId = id(dest)
          val parameters = mutable.ArrayBuilder.make[(Int, Int)]
          val labels     = mutable.HashMap.empty[Int, Int]
          val start      = index
          index += 1
          while (index < module.instructions.size && !isFunctionEnd(module.instructions(index))) {
            module.instructions(index) match {
              case Instruction.FunctionParameter(resultType, parameter) =>
                parameters += ((id(parameter), id(resultType)))
              case Instruction.Label(label) => labels.update(id(label), index)
              case _                        => ()
            }
            index += 1
          }
          if (index >= module.instructions.size)
            return Left(
              Fault.InvalidTask(s"SPIR-V function %$functionId has no OpFunctionEnd")
            )
          functions.update(
            functionId,
            Function(functionId, Vec.from(parameters.result()), start, index, labels.toMap)
          )
        case _ => ()
      }
      index += 1
    }
    // Resolve constant builtins after the complete stream, including forward declarations.
    // OpenCL also uses WorkgroupSize on input variables; those are supplied by the dispatch.
    val builtinSize = builtins.toSeq.collect {
      case (variable, BuiltIn.WorkgroupSize) if constants.contains(variable) => variable
    }.sorted.foldLeft[Either[Fault, Option[LaunchGeometry.Dimensions]]](Right(None)) { (result, variable) =>
      result.flatMap { previous =>
        constants(variable) match {
          case Constant.Bits(_, lanes) if lanes.size == 3 =>
            positiveTriple(lanes(0), lanes(1), lanes(2))
              .toRight(
                Fault.InvalidTask(s"SPIR-V WorkgroupSize %$variable has invalid dimensions: ${lanes.mkString(", ")}")
              )
              .flatMap { size =>
                if (previous.exists(_ != size)) Left(Fault.InvalidTask("SPIR-V WorkgroupSize constants disagree"))
                else Right(Some(size))
              }
          case _ => Left(Fault.InvalidTask(s"SPIR-V WorkgroupSize %$variable must have three integer components"))
        }
      }
    } match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    val definedFunctions = functions.toMap
    val definedNames     = functionNames.toMap
    Right(
      Program(
        module = module,
        types = types.toMap,
        constantData = constants.toMap,
        globals = globals.toMap,
        descriptorBindings = descriptorSlots.view.flatMap { case (variable, slot) =>
          descriptorSets.get(variable).map(set => variable -> (set -> slot))
        }.toMap,
        builtins = builtins.toMap,
        arrayStrides = arrayStrides.toMap,
        memberOffsets = memberOffsets.toMap,
        packedStructures = packedStructures.toSet,
        byValueParameters = byValueParameters.toSet,
        saturatedConversions = saturatedConversions.toSet,
        roundingModes = roundingModes.toMap,
        extendedInstructionSets = extendedInstructionSets.toMap,
        entryPoints = entryPoints.toMap,
        entryPointInterfaces = entryPointInterfaces.toMap,
        vulkanEntries = vulkanEntries.toSet,
        subgroupSizes = subgroupSizes.toMap,
        workGroupSizes = workGroupSizes.toMap,
        builtinWorkGroupSize = builtinSize,
        workGroupSizeHints = workGroupSizeHints.toMap,
        functions = definedFunctions,
        functionNames = definedNames,
        collectiveFunctions = reachableFunctions(
          module,
          definedFunctions,
          instruction => isDirectCollective(instruction, definedNames)
        )
      )
    )
  }

  // Fixed-point reachability uses mutable work sets to avoid rebuilding the graph on each iteration.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def reachableFunctions(
      module: Module,
      functions: Map[Int, Function],
      matches: Instruction => Boolean
  ): Set[Int] = {
    val result = mutable.HashSet.empty[Int]
    functions.values.view.foreach { function =>
      var pc     = function.start + 1
      var direct = false
      while (pc < function.end && !direct) {
        direct = matches(module.instructions(pc))
        pc += 1
      }
      if (direct) result += function.id
    }

    var changed = true
    while (changed) {
      changed = false
      functions.values.view.foreach { function =>
        if (!result.contains(function.id)) {
          var pc      = function.start + 1
          var reaches = false
          while (pc < function.end && !reaches) {
            module.instructions(pc) match {
              case Instruction.FunctionCall(_, _, called, _) => reaches = result.contains(id(called))
              case _                                         => ()
            }
            pc += 1
          }
          if (reaches) {
            result += function.id
            changed = true
          }
        }
      }
    }
    result.toSet
  }

  private def isDirectCollective(
      instruction: Instruction,
      functionNames: Map[Int, String]
  ): Boolean =
    instruction match {
      case _: Instruction.GroupAny | _: Instruction.GroupAll | _: Instruction.GroupUMin | _: Instruction.GroupUMax |
          _: Instruction.GroupSMin | _: Instruction.GroupSMax | _: Instruction.GroupIAdd |
          _: Instruction.GroupIMulKHR | _: Instruction.GroupBitwiseAndKHR | _: Instruction.GroupBitwiseOrKHR | _: Instruction.GroupBitwiseXorKHR | _: Instruction.GroupLogicalAndKHR | _: Instruction.GroupLogicalOrKHR | _: Instruction.GroupLogicalXorKHR | _: Instruction.GroupFAdd | _: Instruction.GroupFMin |
          _: Instruction.GroupFMax | _: Instruction.GroupFMulKHR |
          _: Instruction.SubgroupShuffleINTEL | _: Instruction.SubgroupShuffleXorINTEL |
          _: Instruction.SubgroupShuffleUpINTEL |
          _: Instruction.SubgroupShuffleDownINTEL | _: Instruction.GroupBroadcast |
          _: Instruction.GroupNonUniformBallot |
          _: Instruction.ControlBarrier => true
      case Instruction.FunctionCall(_, _, called, _) =>
        functionNames.get(id(called)).exists(name => isOpenCLCollective(name))
      case _ => false
    }

  private def isOpenCLCollective(name: String): Boolean = name match {
    case "_Z7barrierj" | "_Z29work_group_scan_exclusive_addi" | "_Z29work_group_scan_exclusive_addl" |
        "_Z28sub_group_scan_exclusive_addi" => true
    case value => openCLBroadcastScope(value).isDefined || value.startsWith("_Z26intel_sub_group_shuffle_up")
  }

  // Composite decoding walks operands directly and exits on the first invalid constituent.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def constantComposite(
      types: scala.collection.Map[Int, RuntimeType],
      constants: scala.collection.Map[Int, Constant],
      resultType: Operand,
      destination: Operand,
      constituents: Vec[Operand]
  ): Either[Fault, Constant] = {
    val lanes = Array.newBuilder[Long]
    var index = 0
    while (index < constituents.size) {
      val constituent = constituents(index)
      constants.get(id(constituent)) match {
        case Some(Constant.Bits(_, values)) => lanes ++= values
        // Composite leaves use the same zero pointer representation as OpConstantNull aggregates.
        case Some(Constant.NullPointer(_, _, _)) => lanes += 0L
        case None                                => return Left(undefined(constituent))
      }
      index += 1
    }
    val result = lanes.result()
    scalarLeafCount(types, id(resultType)).flatMap(expected =>
      Either.cond(
        result.length == expected,
        Constant.Bits(id(resultType), Vec.from(result)),
        Fault.InvalidTask(
          s"SPIR-V composite constant %${id(destination)} has ${result.length} scalar leaves, expected $expected"
        )
      )
    )
  }

  private def positiveTriple(x: Long, y: Long, z: Long): Option[LaunchGeometry.Dimensions] =
    if (x <= 0L || y <= 0L || z <= 0L || x > Int.MaxValue.toLong || y > Int.MaxValue.toLong || z > Int.MaxValue.toLong)
      None
    else Some(LaunchGeometry.Dimensions(x.toInt.unsafe, y.toInt.unsafe, z.toInt.unsafe))

  private def localSize(x: Word, y: Word, z: Word): Option[LaunchGeometry.Dimensions] =
    positiveTriple(x.toUnsigned.toLong, y.toUnsigned.toLong, z.toUnsigned.toLong)

  private def localSizeId(
      constants: scala.collection.Map[Int, Constant],
      x: Word,
      y: Word,
      z: Word
  ): Option[LaunchGeometry.Dimensions] =
    for {
      xValue <- constants.get(x.raw).flatMap(scalarBits(_))
      yValue <- constants.get(y.raw).flatMap(scalarBits(_))
      zValue <- constants.get(z.raw).flatMap(scalarBits(_))
      size   <- positiveTriple(xValue, yValue, zValue)
    } yield size

  private def scalarBits(value: Constant): Option[Long] = value match {
    case Constant.Bits(_, values) if values.size == 1 => Some(values.head)
    case _                                            => None
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def literalBits(operand: Operand): Long = {
    var result = 0L
    var index  = 0
    while (index < operand.words.length && index < 2) {
      result |= operand.words(index).toUnsigned.toLong << (index * 32)
      index += 1
    }
    result
  }

  private def literalString(operand: Operand): String = {
    val bytes = operand.words.flatMap { word =>
      LittleEndian.toVec(word.raw.toLong, 4)
    }.takeWhile(_ != 0)
    String(bytes.toArray, java.nio.charset.StandardCharsets.UTF_8)
  }

  private def undefined(operand: Operand): Fault.InvalidTask =
    Fault.InvalidTask(s"SPIR-V value %${id(operand)} is undefined")
}
