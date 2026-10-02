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
  private val floatingStorageFormats = Set[Long](
    ImageFormat.Rgba32f,
    ImageFormat.Rgba16f,
    ImageFormat.R32f,
    ImageFormat.Rgba8,
    ImageFormat.Rgba8Snorm,
    ImageFormat.Rg32f,
    ImageFormat.Rg16f,
    ImageFormat.R11fG11fB10f,
    ImageFormat.R16f,
    ImageFormat.Rgb10A2,
    ImageFormat.Rg8,
    ImageFormat.R8,
    ImageFormat.Rg8Snorm,
    ImageFormat.R8Snorm
  )

  // OpSpecConstantOp can embed any pure opcode; the evaluator supports these two.

  private def isFunctionEnd(instruction: Instruction): Boolean = instruction match {
    case Instruction.FunctionEnd() => true
    case _                         => false
  }

  def compileUnchecked(module: Module): Either[Fault, Program] =
    try compileModule(ModuleNormalization.expandDecorationGroups(module))
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
    val graphicsEntries         = mutable.HashSet.empty[NonEmpty[String]]
    val subgroupSizes           = mutable.HashMap.empty[Int, Int]
    val workGroupSizes          = mutable.HashMap.empty[Int, LaunchGeometry.Dimensions]
    val workGroupSizeHints      = mutable.HashMap.empty[Int, LaunchGeometry.Dimensions]
    val functions               = mutable.HashMap.empty[Int, Function]
    val functionNames           = mutable.HashMap.empty[Int, String]

    module.instructions.foreach {
      case Instruction.Name(target, name) =>
        functionNames.update(id(target), literalString(name))
      case Instruction.Decorate(target, decoration)
          if decoration.words.length >= 2 && unsigned(decoration) == Decoration.LinkageAttributes =>
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
        case Instruction.TypeMatrix(dest, column, count) =>
          val columns     = id(count)
          val validColumn = types.get(id(column)).exists {
            case RuntimeType.VectorType(component, rows) =>
              rows >= 2 && rows <= 4 && types.get(component).exists {
                case RuntimeType.Floating(_) => true
                case _                       => false
              }
            case _ => false
          }
          if (!validColumn || columns < 2 || columns > 4)
            return Left(Fault.InvalidTask("SPIR-V matrix requires two to four floating columns and rows"))
          types.update(id(dest), RuntimeType.MatrixType(id(column), columns))
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
        case Instruction.TypeSampler(dest)                 => types.update(id(dest), RuntimeType.Sampler)
        case Instruction.TypeSampledImage(dest, imageType) =>
          types.update(id(dest), RuntimeType.SampledImage(id(imageType)))
        case Instruction.TypeImage(dest, sampledType, dim, depth, arrayed, multisampled, sampled, _, _)
            if unsigned(sampled) == 1L && unsigned(multisampled) == 1L =>
          if (unsigned(dim) != Dim.Value2D || unsigned(arrayed) > 1L)
            return Left(Fault.Unsupported("multisampled textures require 2D views"))
          types.update(id(dest), RuntimeType.MultisampledTexture(id(sampledType), unsigned(arrayed) == 1L))
        case Instruction.TypeImage(dest, sampledType, dim, depth, arrayed, multisampled, sampled, format, _)
            if unsigned(dim) == Dim.SubpassData =>
          if (
            unsigned(arrayed) != 0L || unsigned(sampled) != 2L || unsigned(format) != ImageFormat.Unknown ||
            unsigned(multisampled) > 1L
          )
            return Left(Fault.Unsupported("input attachments require non-arrayed SubpassData images"))
          types.update(id(dest), RuntimeType.InputAttachment(id(sampledType), unsigned(multisampled) == 1L))
        case Instruction.TypeImage(dest, sampledType, dim, depth, arrayed, multisampled, sampled, _, _)
            if unsigned(sampled) == 1L && unsigned(dim) == Dim.Cube =>
          if (unsigned(arrayed) > 1L || unsigned(multisampled) != 0L)
            return Left(Fault.Unsupported("cube textures require single-sample views"))
          types.update(id(dest), RuntimeType.CubeTexture(id(sampledType), unsigned(arrayed) == 1L))
        case Instruction.TypeImage(dest, sampledType, dim, depth, arrayed, multisampled, sampled, _, _)
            if unsigned(sampled) == 1L =>
          if (
            !Set[Long](Dim.Value1D, Dim.Value2D, Dim.Value3D, Dim.Buffer).contains(unsigned(dim)) ||
            unsigned(arrayed) > 1L ||
            ((unsigned(dim) == Dim.Buffer || unsigned(dim) == Dim.Value3D) && unsigned(arrayed) != 0L) ||
            unsigned(multisampled) != 0L
          )
            return Left(
              Fault.Unsupported("sampled images currently require single-sample 1D/2D/3D or texel-buffer views")
            )
          types.update(
            id(dest),
            RuntimeType.Texture(
              id(sampledType),
              if (unsigned(dim) == Dim.Value3D) 3 else if (unsigned(dim) == Dim.Value2D) 2 else 1,
              unsigned(dim) == Dim.Buffer,
              unsigned(arrayed) == 1L
            )
          )
        case Instruction.TypeImage(dest, sampledType, dim, depth, arrayed, multisampled, sampled, format, _) =>
          if (
            !Set[Long](
              Dim.Value1D,
              Dim.Value2D,
              Dim.Value3D,
              Dim.Cube,
              Dim.Buffer
            ).contains(unsigned(dim)) || unsigned(depth) > 2L || unsigned(arrayed) > 1L ||
            ((unsigned(dim) == Dim.Buffer || unsigned(dim) == Dim.Value3D) && unsigned(arrayed) != 0L) ||
            unsigned(multisampled) != 0L || unsigned(sampled) != 2L ||
            !(floatingStorageFormats.contains(unsigned(format)) ||
              ImageFormats.integer(unsigned(format).toInt).isDefined) ||
            !(if (floatingStorageFormats.contains(unsigned(format)))
                types.get(id(sampledType)).contains(RuntimeType.Floating(32))
              else types.get(id(sampledType)).exists {
                case RuntimeType.Integer(width, _) => width.raw == 32
                case _                             => false
              })
          )
            return Left(
              Fault.Unsupported(
                "SPIR-V storage images require supported 1D/2D/3D/cube or texel-buffer formats with matching scalar type and no multisampling"
              )
            )
          types.update(
            id(dest),
            RuntimeType.StorageImage(
              id(sampledType),
              unsigned(format).toInt,
              if (unsigned(dim) == Dim.Value3D) 3
              else if (unsigned(dim) == Dim.Value2D || unsigned(dim) == Dim.Cube) 2 else 1,
              unsigned(dim) == Dim.Buffer,
              unsigned(arrayed) == 1L,
              unsigned(dim) == Dim.Cube
            )
          )
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
          val result   = specialization(types, constants, id(resultType), unsigned(instructionOpcode), operands)
          result match {
            case Some(value) => constants.update(id(dest), value)
            case None        => return Left(Fault.Unsupported(
                s"SPIR-V specialization constant operation ${unsigned(instructionOpcode)} is unsupported or has invalid operands"
              ))
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
        case Instruction.Decorate(target, decoration) if unsigned(decoration) == Decoration.SaturatedConversion =>
          val _ = saturatedConversions.add(id(target))
        case Instruction.Decorate(target, decoration) if unsigned(decoration) == Decoration.FPRoundingMode =>
          if (decoration.words.length != 2 || decoration.words.apply(1).toUnsigned.raw > FPRoundingMode.RTN)
            return Left(Fault.InvalidTask("FPRoundingMode requires RTE, RTZ, RTP or RTN"))
          roundingModes.update(id(target), decoration.words.apply(1).raw)
        case Instruction.MemberDecorate(structureType, member, decoration)
            if decoration.words.length >= 2 && unsigned(decoration) == Decoration.Offset =>
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
              if (
                Set[Long](
                  ExecutionModel.Vertex,
                  ExecutionModel.Fragment,
                  ExecutionModel.GLCompute
                ).contains(unsigned(executionModel))
              ) {
                val _ = vulkanEntries.add(kernelName)
                if (unsigned(executionModel) != ExecutionModel.GLCompute) {
                  val _ = graphicsEntries.add(kernelName)
                }
              }
          }
        case Instruction.ExecutionMode(entryPoint, mode) =>
          unsigned(mode) match {
            case ExecutionMode.SubgroupSize if mode.words.length >= 2 =>
              val size = mode.words(1).toUnsigned.raw
              if (size == 0L || size > Int.MaxValue.toLong)
                return Left(Fault.InvalidTask(s"SPIR-V SubgroupSize $size is invalid"))
              subgroupSizes.update(id(entryPoint), size.toInt)
            case ExecutionMode.LocalSize if mode.words.length >= 4 =>
              localSize(mode.words(1), mode.words(2), mode.words(3)).foreach(size =>
                workGroupSizes.update(id(entryPoint), size)
              )
            case ExecutionMode.LocalSizeHint if mode.words.length >= 4 =>
              localSize(mode.words(1), mode.words(2), mode.words(3)).foreach(size =>
                workGroupSizeHints.update(id(entryPoint), size)
              )
            case ExecutionMode.LocalSizeId if mode.words.length >= 4 =>
              localSizeId(constants, mode.words(1), mode.words(2), mode.words(3))
                .foreach(size => workGroupSizes.update(id(entryPoint), size))
            case ExecutionMode.LocalSizeHintId if mode.words.length >= 4 =>
              localSizeId(constants, mode.words(1), mode.words(2), mode.words(3))
                .foreach(size => workGroupSizeHints.update(id(entryPoint), size))
            case _ => ()
          }
        case Instruction.ExecutionModeId(entryPoint, mode) =>
          unsigned(mode) match {
            case ExecutionMode.LocalSizeId if mode.words.length >= 4 =>
              localSizeId(constants, mode.words(1), mode.words(2), mode.words(3))
                .foreach(size => workGroupSizes.update(id(entryPoint), size))
            case ExecutionMode.LocalSizeHintId if mode.words.length >= 4 =>
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

    val invalidDescriptorArray = vulkanEntries.nonEmpty && descriptorSlots.keys.view.exists { variable =>
      globals.get(variable).exists { global =>
        types.get(global.pointerType) match {
          case Some(RuntimeType.Pointer(_, pointee)) => types.get(pointee) match {
              case Some(RuntimeType.Array(element, count)) => count <= 0 || count > 4096 || types.get(element).exists {
                  case RuntimeType.Array(_, _) => true
                  case _                       => false
                }
              case _ => false
            }
          case _ => false
        }
      }
    }
    if (invalidDescriptorArray)
      return Left(Fault.Unsupported("descriptor arrays require one fixed dimension of at most 4096 elements"))
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
        graphicsEntries = graphicsEntries.toSet,
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
      case value if VMCollectives.isNonUniform(value) => true
      case _: Instruction.DPdx | _: Instruction.DPdy | _: Instruction.Fwidth |
          _: Instruction.DPdxFine | _: Instruction.DPdyFine | _: Instruction.FwidthFine |
          _: Instruction.DPdxCoarse | _: Instruction.DPdyCoarse | _: Instruction.FwidthCoarse |
          _: Instruction.ImageSampleImplicitLod | _: Instruction.ImageSampleDrefImplicitLod |
          _: Instruction.ImageSampleProjImplicitLod | _: Instruction.ImageSampleProjDrefImplicitLod | _: Instruction.ImageQueryLod =>
        true
      case _: Instruction.GroupAny | _: Instruction.GroupAll | _: Instruction.GroupUMin | _: Instruction.GroupUMax |
          _: Instruction.GroupSMin | _: Instruction.GroupSMax | _: Instruction.GroupIAdd |
          _: Instruction.GroupIMulKHR | _: Instruction.GroupBitwiseAndKHR | _: Instruction.GroupBitwiseOrKHR | _: Instruction.GroupBitwiseXorKHR | _: Instruction.GroupLogicalAndKHR | _: Instruction.GroupLogicalOrKHR | _: Instruction.GroupLogicalXorKHR | _: Instruction.GroupFAdd | _: Instruction.GroupFMin |
          _: Instruction.GroupFMax | _: Instruction.GroupFMulKHR |
          _: Instruction.SubgroupShuffleINTEL | _: Instruction.SubgroupShuffleXorINTEL |
          _: Instruction.SubgroupShuffleUpINTEL |
          _: Instruction.SubgroupShuffleDownINTEL | _: Instruction.GroupBroadcast |
          _: Instruction.GroupNonUniformBallot |
          _: Instruction.GroupNonUniformFAdd | _: Instruction.GroupNonUniformFMul |
          _: Instruction.GroupNonUniformFMin | _: Instruction.GroupNonUniformFMax |
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
  private def specialization(
      types: mutable.Map[Int, RuntimeType],
      constants: mutable.Map[Int, Constant],
      resultType: Int,
      opcode: Long,
      operands: Vec[Long]
  ): Option[Constant] = {
    def source(index: Int): Option[(Int, Vec[Long])] = operands.lift(index).flatMap { value =>
      constants.get(Math.toIntExact(value)).collect { case Constant.Bits(t, lanes) => (t, lanes) }
    }
    def shape(t: Int): Option[(RuntimeType, Int)] = types.get(t).flatMap {
      case RuntimeType.VectorType(component, count) => types.get(component).map((_, count))
      case scalar @ (RuntimeType.Bool | RuntimeType.Integer(_, _) | RuntimeType.Floating(_)) => Some((scalar, 1))
      case _                                                                                 => None
    }
    def integerWidth(t: Int): Option[Int] = shape(t).collect { case (RuntimeType.Integer(width, _), _) => width.raw }
    def mask(width: Int): Long            = if (width == 64) -1L else (1L << width) - 1L
    def signed(value: Long, width: Int): Long      = (value << (64 - width)) >> (64 - width)
    def result(lanes: Vec[Long]): Option[Constant] =
      scalarLeafCount(types, resultType).toOption.filter(_ == lanes.size).map(_ => Constant.Bits(resultType, lanes))
    def component(t: Int, indices: Vec[Long]): Option[(Int, Int)] =
      if (indices.isEmpty) Some((t, 0))
      else {
        val index = indices.head
        val child = types.get(t) match {
          case Some(RuntimeType.Structure(members)) if index >= 0 && index < members.size =>
            val preceding = members.take(index.toInt).map(m => scalarLeafCount(types, m).toOption)
            if (preceding.forall(_.isDefined)) Some((members(index.toInt), preceding.flatten.sum)) else None
          case Some(RuntimeType.VectorType(element, count)) if index >= 0 && index < count =>
            scalarLeafCount(types, element).toOption.map(n => (element, Math.toIntExact(index * n)))
          case Some(RuntimeType.MatrixType(element, count)) if index >= 0 && index < count =>
            scalarLeafCount(types, element).toOption.map(n => (element, Math.toIntExact(index * n)))
          case Some(RuntimeType.Array(element, count)) if index >= 0 && index < count =>
            scalarLeafCount(types, element).toOption.map(n => (element, Math.toIntExact(index * n)))
          case _ => None
        }
        child.flatMap { case (element, offset) =>
          component(element, indices.drop(1)).map { case (leaf, inner) => (leaf, offset + inner) }
        }
      }
    opcode match {
      case SPIRV.Opcode.CompositeExtract if operands.size >= 2 =>
        for {
          composite <- source(0)
          selected  <- component(composite._1, operands.drop(1))
          if selected._1 == resultType
          count <- scalarLeafCount(types, resultType).toOption
          value <- result(composite._2.slice(selected._2, selected._2 + count))
        } yield value
      case SPIRV.Opcode.CompositeInsert if operands.size >= 3 =>
        for {
          inserted  <- source(0)
          composite <- source(1)
          if composite._1 == resultType
          selected <- component(resultType, operands.drop(2))
          if selected._1 == inserted._1
          value <-
            result(composite._2.take(selected._2) ++ inserted._2 ++ composite._2.drop(selected._2 + inserted._2.size))
        } yield value
      case SPIRV.Opcode.VectorShuffle if operands.size >= 3 =>
        for {
          a        <- source(0)
          b        <- source(1)
          aShape   <- shape(a._1)
          bShape   <- shape(b._1)
          outShape <- shape(resultType)
          if aShape._1 == bShape._1 && aShape._1 == outShape._1
          joined  = a._2 ++ b._2
          indices = operands.drop(2)
          if indices.forall(i => i == 0xffffffffL || (i >= 0 && i < joined.size))
          value <- result(indices.map(i => if (i == 0xffffffffL) 0L else joined(i.toInt)))
        } yield value
      case SPIRV.Opcode.Select if operands.size == 3 =>
        for {
          condition <- source(0)
          if shape(condition._1).exists(_._1 == RuntimeType.Bool)
          a <- source(1)
          b <- source(2)
          if a._1 == resultType && b._1 == resultType && a._2.size == b._2.size
          if condition._2.size == 1 || condition._2.size == a._2.size
          value <- result(Vec.tabulate(a._2.size)(i =>
            if (condition._2(if (condition._2.size == 1) 0 else i) != 0) a._2(i) else b._2(i)
          ))
        } yield value
      case op if op >= SPIRV.Opcode.LogicalEqual && op <= SPIRV.Opcode.SLessThanEqual && op != SPIRV.Opcode.Select =>
        for {
          a <- source(0)
          b <- if (op == SPIRV.Opcode.LogicalNot && operands.size == 1) Some(a)
          else if (operands.size == 2) source(1) else None
          outShape <- shape(resultType)
          aShape   <- shape(a._1)
          bShape   <- shape(b._1)
          if outShape._1 == RuntimeType.Bool && a._2.size == outShape._2 && b._2.size == a._2.size
          if (op <= SPIRV.Opcode.LogicalNot && aShape._1 == RuntimeType.Bool && bShape._1 == RuntimeType.Bool) ||
            (op >= SPIRV.Opcode.IEqual && integerWidth(a._1).isDefined && integerWidth(a._1) == integerWidth(b._1))
          width = integerWidth(a._1).getOrElse(1)
          value <- result(Vec.tabulate(a._2.size) { i =>
            val x          = a._2(i) & mask(width)
            val y          = b._2(i) & mask(width)
            val comparison = op match {
              case SPIRV.Opcode.LogicalEqual | SPIRV.Opcode.IEqual       => x == y
              case SPIRV.Opcode.LogicalNotEqual | SPIRV.Opcode.INotEqual => x != y
              case SPIRV.Opcode.LogicalOr                                => x != 0 || y != 0
              case SPIRV.Opcode.LogicalAnd                               => x != 0 && y != 0
              case SPIRV.Opcode.LogicalNot                               => x == 0
              case SPIRV.Opcode.UGreaterThan                             => java.lang.Long.compareUnsigned(x, y) > 0
              case SPIRV.Opcode.SGreaterThan                             => signed(x, width) > signed(y, width)
              case SPIRV.Opcode.UGreaterThanEqual                        => java.lang.Long.compareUnsigned(x, y) >= 0
              case SPIRV.Opcode.SGreaterThanEqual                        => signed(x, width) >= signed(y, width)
              case SPIRV.Opcode.ULessThan                                => java.lang.Long.compareUnsigned(x, y) < 0
              case SPIRV.Opcode.SLessThan                                => signed(x, width) < signed(y, width)
              case SPIRV.Opcode.ULessThanEqual                           => java.lang.Long.compareUnsigned(x, y) <= 0
              case SPIRV.Opcode.SLessThanEqual                           => signed(x, width) <= signed(y, width)
            }
            if (comparison) 1L else 0L
          })
        } yield value
      case SPIRV.Opcode.UConvert | SPIRV.Opcode.SConvert if operands.size == 1 =>
        for {
          a     <- source(0)
          from  <- integerWidth(a._1)
          to    <- integerWidth(resultType)
          value <- result(a._2.map(bits =>
            (if (opcode == SPIRV.Opcode.SConvert) signed(bits, from) else bits & mask(from)) & mask(to)
          ))
        } yield value
      case SPIRV.Opcode.FConvert if operands.size == 1 =>
        for {
          a     <- source(0)
          from  <- shape(a._1).collect { case (RuntimeType.Floating(width), _) => width.raw }
          to    <- shape(resultType).collect { case (RuntimeType.Floating(width), _) => width.raw }
          value <- result(a._2.map { bits =>
            val number = from match {
              case 16 => IEEE754.binary16ToFloat(Binary16.low(bits)).toDouble
              case 32 => IEEE754.binary32ToFloat(Binary32.low(bits)).toDouble
              case 64 => IEEE754.binary64ToDouble(bits)
            }
            to match {
              case 16 => IEEE754.doubleToBinary16(number).raw.toLong
              case 32 => IEEE754.floatToBinary32(number.toFloat).raw.toLong
              case 64 => IEEE754.doubleToBinary64(number)
            }
          })
        } yield value
      case SPIRV.Opcode.QuantizeToF16 if operands.size == 1 =>
        for {
          a <- source(0)
          if a._1 == resultType && shape(resultType).exists {
            case (RuntimeType.Floating(width), _) => width.raw == 32; case _ => false
          }
          value <- result(a._2.map(quantizeToF16))
        } yield value
      case _ => integerSpecialization(types, constants, resultType, opcode, operands)
    }
  }

  private def integerSpecialization(
      types: mutable.Map[Int, RuntimeType],
      constants: mutable.Map[Int, Constant],
      resultType: Int,
      opcode: Long,
      operands: Vec[Long]
  ): Option[Constant] = {
    def shape(id: Int): Option[(Int, Int)] = types.get(id) match {
      case Some(RuntimeType.Integer(width, _))          => Some((width.raw, 1))
      case Some(RuntimeType.VectorType(element, count)) => types.get(element) match {
          case Some(RuntimeType.Integer(width, _)) => Some((width.raw, count))
          case _                                   => None
        }
      case _ => None
    }
    val unary     = opcode == SPIRV.Opcode.SNegate || opcode == SPIRV.Opcode.Not
    val supported = Set[Long](
      SPIRV.Opcode.SNegate,
      SPIRV.Opcode.IAdd,
      SPIRV.Opcode.ISub,
      SPIRV.Opcode.IMul,
      SPIRV.Opcode.UDiv,
      SPIRV.Opcode.SDiv,
      SPIRV.Opcode.UMod,
      SPIRV.Opcode.SRem,
      SPIRV.Opcode.SMod,
      SPIRV.Opcode.ShiftRightLogical,
      SPIRV.Opcode.ShiftRightArithmetic,
      SPIRV.Opcode.ShiftLeftLogical,
      SPIRV.Opcode.BitwiseOr,
      SPIRV.Opcode.BitwiseXor,
      SPIRV.Opcode.BitwiseAnd,
      SPIRV.Opcode.Not
    )
    if (!supported.contains(opcode) || operands.size != (if (unary) 1 else 2)) None
    else shape(resultType).flatMap { case (width, count) =>
      def source(index: Int, shifts: Boolean): Option[Vec[Long]] =
        constants.get(Math.toIntExact(operands(index))).flatMap {
          case Constant.Bits(typeId, lanes) if lanes.size == count && shape(typeId).exists { case (bits, size) =>
                size == count && (shifts || bits == width)
              } => Some(lanes)
          case _ => None
        }
      val mask                      = if (width == 64) -1L else (1L << width) - 1L
      def signed(value: Long): Long = if (width == 64) value else (value << (64 - width)) >> (64 - width)
      for {
        left  <- source(0, false)
        right <- if (unary) Some(Vec.fill(count)(0L))
        else source(1, opcode >= SPIRV.Opcode.ShiftRightLogical && opcode <= SPIRV.Opcode.ShiftLeftLogical)
        results = Vector.tabulate(count) { lane =>
          val a                   = left(lane) & mask
          val b                   = right(lane) & mask
          val value: Option[Long] = opcode match {
            case SPIRV.Opcode.SNegate        => Some(-a)
            case SPIRV.Opcode.IAdd           => Some(a + b)
            case SPIRV.Opcode.ISub           => Some(a - b)
            case SPIRV.Opcode.IMul           => Some(a * b)
            case SPIRV.Opcode.UDiv if b != 0 => Some(java.lang.Long.divideUnsigned(a, b))
            case SPIRV.Opcode.SDiv if b != 0 => Some(signed(a) / signed(b))
            case SPIRV.Opcode.UMod if b != 0 => Some(java.lang.Long.remainderUnsigned(a, b))
            case SPIRV.Opcode.SRem if b != 0 => Some(signed(a) % signed(b))
            case SPIRV.Opcode.SMod if b != 0 =>
              val remainder = signed(a) % signed(b)
              Some(if (remainder != 0 && (remainder < 0) != (signed(b) < 0)) remainder + signed(b) else remainder)
            case SPIRV.Opcode.ShiftRightLogical if right(lane) >= 0 && right(lane) < width =>
              Some(a >>> right(lane).toInt)
            case SPIRV.Opcode.ShiftRightArithmetic if right(lane) >= 0 && right(lane) < width =>
              Some(signed(a) >> right(lane).toInt)
            case SPIRV.Opcode.ShiftLeftLogical if right(lane) >= 0 && right(lane) < width =>
              Some(a << right(lane).toInt)
            case SPIRV.Opcode.BitwiseOr  => Some(a | b)
            case SPIRV.Opcode.BitwiseXor => Some(a ^ b)
            case SPIRV.Opcode.BitwiseAnd => Some(a & b)
            case SPIRV.Opcode.Not        => Some(~a)
            case _                       => None
          }
          value.map(_ & mask)
        }
        if results.forall(_.isDefined)
      } yield Constant.Bits(resultType, Vec.from(results.flatten))
    }
  }

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
