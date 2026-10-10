package net.kurobako.gemstone.isa.spirv

import VMProgram.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

/** Builds the module tables of a [[VMProgram]] in one pass over the instruction stream. */
private object VMLoader {
  import Operand.*

  // The tables stay locals: held as fields of a per-call object, they crash the Scala Native tests in HashMap lookups.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  def tables(module: Module): Either[Fault, Tables] = {
    val types                   = mutable.HashMap.empty[Int, RuntimeType]
    val constants               = mutable.HashMap.empty[Int, Constant]
    val globals                 = mutable.HashMap.empty[Int, Global]
    val descriptorSets          = mutable.HashMap.empty[Int, Int >= 0]
    val descriptorSlots         = mutable.HashMap.empty[Int, Int >= 0]
    val builtins                = mutable.HashMap.empty[Int, Int]
    val arrayStrides            = mutable.HashMap.empty[Int, Int > 0]
    val memberOffsets           = mutable.HashMap.empty[(Int, Int), Int >= 0]
    val matrixStrides           = mutable.HashMap.empty[(Int, Int), Int > 0]
    val rowMajorMatrices        = mutable.HashSet.empty[(Int, Int)]
    val packedStructures        = mutable.HashSet.empty[Int]
    val byValueParameters       = mutable.HashSet.empty[Int]
    val saturatedConversions    = mutable.HashSet.empty[Int]
    val roundingModes           = mutable.HashMap.empty[Int, Int]
    val extendedInstructionSets = mutable.HashMap.empty[Int, NonEmpty[String]]
    val entryPoints             = mutable.HashMap.empty[NonEmpty[String], Int]
    val entryPointInterfaces    = mutable.HashMap.empty[NonEmpty[String], Set[Int]]
    val vulkanEntries           = mutable.HashSet.empty[NonEmpty[String]]
    val graphicsEntries         = mutable.HashSet.empty[NonEmpty[String]]
    val subgroupSizes           = mutable.HashMap.empty[Int, Int > 0]
    val workGroupSizes          = mutable.HashMap.empty[Int, LaunchGeometry.Dimensions]
    val workGroupSizeHints      = mutable.HashMap.empty[Int, LaunchGeometry.Dimensions]
    val localSizeIds            = mutable.ArrayBuffer.empty[(Int, Operand)]
    val functions               = mutable.HashMap.empty[Int, Function]
    val names                   = mutable.HashMap.empty[Int, String]
    val valueTypes              = mutable.HashMap.empty[Int, Int]
    var largestId               = 0

    def define(dest: Operand): Either[Fault, Int] = {
      val value = id(dest)
      largestId = math.max(largestId, value)
      Either.cond(
        value > 0 && value <= IdLimit,
        value,
        Fault.InvalidRequest(s"SPIR-V result id ${unsigned(dest)} exceeds the supported bound $IdLimit")
      )
    }

    // Result ids follow the grammar's operand shapes; hand-built operands may carry any kind.
    def record(instruction: Instruction): Either[Fault, Unit] = {
      val shapes = instruction.definition.shapes
      if (shapes.size >= 2 && shapes(0).kind == OperandKind.IdResultType && shapes(1).kind == OperandKind.IdResult)
        define(instruction.operands(1)).map { value =>
          if (instruction.definition != Instruction.Definition.Function)
            valueTypes.update(value, id(instruction.operands(0)))
        }
      else if (shapes.nonEmpty && shapes(0).kind == OperandKind.IdResult) define(instruction.operands(0)).map(_ => ())
      else Right(())
    }

    // Types, constants and module-scope variables.
    def declare(instruction: Instruction): Either[Fault, Unit] = {
      instruction match {
        case Instruction.TypeVoid(dest)                   => types.update(id(dest), RuntimeType.Void)
        case Instruction.TypeBool(dest)                   => types.update(id(dest), RuntimeType.Bool)
        case Instruction.TypeInt(dest, width, signedness) =>
          id(width).refine[ScalarBitWidth] match {
            case Left(_)     => return Left(Fault.Unsupported(s"SPIR-V integer width ${id(width)} is unsupported"))
            case Right(bits) => types.update(id(dest), RuntimeType.Integer(bits, unsigned(signedness) != 0L))
          }
        case Instruction.TypeFloat(dest, width, _) =>
          id(width).refine[Int in (16, 32, 64)] match {
            case Left(_) => return Left(Fault.Unsupported(s"SPIR-V floating-point width ${id(width)} is unsupported"))
            case Right(bits) => types.update(id(dest), RuntimeType.Floating(bits))
          }
        case Instruction.TypeVector(dest, component, count) =>
          id(count).refine[Int > 0] match {
            case Left(_) =>
              return Left(Fault.InvalidRequest(s"SPIR-V vector component count ${unsigned(count)} is invalid"))
            case Right(lanes) => types.update(id(dest), RuntimeType.VectorType(id(component), lanes))
          }
        case Instruction.TypeMatrix(dest, column, count) =>
          val validColumn = types.get(id(column)).exists {
            case RuntimeType.VectorType(component, rows) =>
              rows.raw >= 2 && rows.raw <= 4 && types.get(component).exists {
                case RuntimeType.Floating(_) => true
                case _                       => false
              }
            case _ => false
          }
          id(count).refine[Int in 2 ~ 4] match {
            case Right(columns) if validColumn => types.update(id(dest), RuntimeType.MatrixType(id(column), columns))
            case _ => return Left(Fault.InvalidRequest("SPIR-V matrix requires two to four floating columns and rows"))
          }
        case Instruction.TypeArray(dest, element, length) =>
          constants.get(id(length)).flatMap(scalarBits(_)) match {
            case None =>
              return Left(Fault.InvalidRequest(s"SPIR-V array length %${id(length)} is not a scalar constant"))
            case Some(count) => count.refine[Long >= 0] match {
                case Right(size) => types.update(id(dest), RuntimeType.Array(id(element), size))
                case Left(_)     => return Left(Fault.InvalidRequest(s"SPIR-V array length $count is invalid"))
              }
          }
        // A zero count marks an unsized descriptor array; accesses use the external allocation's bounds.
        case Instruction.TypeRuntimeArray(dest, element) => types.update(id(dest), RuntimeType.Array(id(element), 0L))
        case Instruction.TypeStruct(dest, members) => types.update(id(dest), RuntimeType.Structure(members.map(id(_))))
        case Instruction.TypePointer(dest, storageClass, dataType) =>
          types.update(id(dest), RuntimeType.Pointer(id(storageClass), id(dataType)))
        case Instruction.TypeFunction(dest, result, parameters) =>
          types.update(id(dest), RuntimeType.Function(id(result), parameters.map(id(_))))
        case Instruction.TypeSampler(dest)                 => types.update(id(dest), RuntimeType.Sampler)
        case Instruction.TypeSampledImage(dest, imageType) =>
          // SPIR-V 1.6 forbids sampled buffer images; older GLSL toolchains still emit them for samplerBuffer
          val bufferImage = types.get(id(imageType)).exists {
            case RuntimeType.Texture(texelBuffer = true) => true
            case _                                       => false
          }
          if (bufferImage && module.header.version >= Version(1, 6))
            return Left(Fault.InvalidRequest("OpTypeSampledImage cannot wrap a Buffer image in SPIR-V 1.6"))
          types.update(id(dest), RuntimeType.SampledImage(id(imageType)))
        case instruction: Instruction.TypeImage =>
          image(types, instruction) match {
            case Left(error)  => return Left(error)
            case Right(value) => types.update(id(instruction.dest), value)
          }
        case Instruction.TypeEvent(dest)        => types.update(id(dest), RuntimeType.Event)
        case Instruction.TypeReserveId(dest)    => types.update(id(dest), RuntimeType.ReserveId)
        case Instruction.TypeDeviceEvent(dest)  => types.update(id(dest), RuntimeType.DeviceEvent)
        case Instruction.TypeQueue(dest)        => types.update(id(dest), RuntimeType.Queue)
        case Instruction.TypePipe(dest, access) => types.update(id(dest), RuntimeType.Pipe(unsigned(access).toInt))
        case Instruction.ConstantTrue(resultType, dest) =>
          constants.update(id(dest), Constant.Bits(id(resultType), Vec(1L)))
        case Instruction.SpecConstantTrue(resultType, dest) =>
          constants.update(id(dest), Constant.Bits(id(resultType), Vec(1L)))
        case Instruction.ConstantFalse(resultType, dest) =>
          constants.update(id(dest), Constant.Bits(id(resultType), Vec(0L)))
        case Instruction.SpecConstantFalse(resultType, dest) =>
          constants.update(id(dest), Constant.Bits(id(resultType), Vec(0L)))
        case ScalarConstant(resultType, dest, value) =>
          constants.update(id(dest), Constant.Bits(id(resultType), Vec(literalBits(types, id(resultType), value))))
        case CompositeConstant(resultType, dest, constituents) =>
          constantComposite(types, constants, globals, resultType, dest, constituents) match {
            case Left(error)  => return Left(error)
            case Right(value) => constants.update(id(dest), value)
          }
        case Instruction.SpecConstantOp(resultType, dest, opcode, operands)
            if addressing(unsigned(opcode).toInt) && operands.nonEmpty &&
              (globals.contains(id(operands.head)) || isAddress(constants.get(id(operands.head)))) =>
          globalAddress(constants, id(resultType), unsigned(opcode).toInt, operands.map(id(_))) match {
            case Some(value) => constants.update(id(dest), value)
            case None        => return Left(Fault.Unsupported(
                s"SPIR-V specialization constant operation ${unsigned(opcode)} on a global address has non-constant operands"
              ))
          }
        case Instruction.SpecConstantOp(resultType, dest, opcode, Vec(operand))
            if pointerCasts(unsigned(opcode).toInt) &&
              (constants.get(id(operand)) match {
                case Some(Constant.NullPointer(_, _, _)) => true
                case _                                   => false
              }) =>
          zeroValue(types, id(resultType), id(dest), "OpSpecConstantOp") match {
            case Left(error)  => return Left(error)
            case Right(value) => constants.update(id(dest), value)
          }
        case Instruction.SpecConstantOp(resultType, dest, opcode, operands) =>
          SpecializationOperation.evaluate(
            types,
            constants,
            id(resultType),
            unsigned(opcode),
            operands.map(unsigned)
          ) match {
            case Some(value) => constants.update(id(dest), value)
            case None        => return Left(Fault.Unsupported(
                s"SPIR-V specialization constant operation ${unsigned(opcode)} is unsupported or has invalid operands"
              ))
          }
        case Instruction.ConstantSampler(resultType, dest, addressingMode, normalised, filter) =>
          constants.update(
            id(dest),
            literalSampler(id(resultType), unsigned(addressingMode), unsigned(normalised), unsigned(filter))
          )
        case Instruction.ConstantNull(resultType, dest) =>
          zeroValue(types, id(resultType), id(dest), "OpConstantNull") match {
            case Left(error)  => return Left(error)
            case Right(value) => constants.update(id(dest), value)
          }
        case Instruction.Undef(resultType, dest) =>
          zeroValue(types, id(resultType), id(dest), "OpUndef") match {
            case Left(error)  => return Left(error)
            case Right(value) => constants.update(id(dest), value)
          }
        case Instruction.Variable(resultType, dest, storageClass, initialiser) =>
          globals.update(id(dest), Global(id(resultType), id(storageClass), initialiser.map(id(_))))
        case _ => ()
      }
      Right(())
    }

    // Decorations that the tables record per id or structure member.
    def decorate(instruction: Instruction): Either[Fault, Unit] = {
      instruction match {
        case Instruction.Decorate(target, decoration) if decoration.words.length >= 2 =>
          val argument = decoration.words(1)
          unsigned(decoration) match {
            case Decoration.BuiltIn => builtins.update(id(target), argument.raw)
            case Decoration.Binding =>
              argument.raw.refine[Int >= 0] match {
                case Left(_) => return Left(Fault.Unsupported(
                    s"SPIR-V descriptor binding ${argument.toUnsigned.toLong} exceeds the supported index range"
                  ))
                case Right(value) => descriptorSlots.update(id(target), value)
              }
            case Decoration.DescriptorSet =>
              argument.raw.refine[Int >= 0] match {
                case Left(_) => return Left(Fault.Unsupported(
                    s"SPIR-V descriptor set ${argument.toUnsigned.toLong} exceeds the supported index range"
                  ))
                case Right(value) => descriptorSets.update(id(target), value)
              }
            case Decoration.ArrayStride =>
              argument.raw.refine[Int > 0] match {
                case Right(stride) => arrayStrides.update(id(target), stride)
                case Left(_)       =>
                  return Left(Fault.InvalidRequest("SPIR-V array stride must be a positive supported byte size"))
              }
            case Decoration.CPacked => val _ = packedStructures.add(id(target))
            case Decoration.FuncParamAttr if argument.toUnsigned.toLong == FunctionParameterAttribute.ByVal =>
              val _ = byValueParameters.add(id(target))
            case Decoration.SaturatedConversion => val _ = saturatedConversions.add(id(target))
            case Decoration.FPRoundingMode      =>
              if (decoration.words.length != 2 || argument.toUnsigned.raw > FPRoundingMode.RTN)
                return Left(Fault.InvalidRequest("FPRoundingMode requires RTE, RTZ, RTP or RTN"))
              roundingModes.update(id(target), argument.raw)
            case _ => ()
          }
        case Instruction.Decorate(target, decoration) =>
          unsigned(decoration) match {
            case Decoration.CPacked             => val _ = packedStructures.add(id(target))
            case Decoration.SaturatedConversion => val _ = saturatedConversions.add(id(target))
            case Decoration.FPRoundingMode      =>
              return Left(Fault.InvalidRequest("FPRoundingMode requires RTE, RTZ, RTP or RTN"))
            case _ => ()
          }
        case Instruction.MemberDecorate(structureType, member, decoration) =>
          val key = id(structureType) -> id(member)
          unsigned(decoration) match {
            case Decoration.MatrixStride if decoration.words.length >= 2 =>
              decoration.words(1).raw.refine[Int > 0] match {
                case Right(stride) => matrixStrides.update(key, stride)
                case Left(_)       =>
                  return Left(Fault.InvalidRequest("SPIR-V matrix stride must be a positive supported byte size"))
              }
            case Decoration.RowMajor                               => val _ = rowMajorMatrices.add(key)
            case Decoration.Offset if decoration.words.length >= 2 =>
              decoration.words(1).raw.refine[Int >= 0] match {
                case Right(offset) => memberOffsets.update(key, offset)
                case Left(_)       => return Left(Fault.InvalidRequest(
                    s"SPIR-V member offset ${decoration.words(1).toUnsigned.toLong} exceeds the supported range"
                  ))
              }
            case _ => ()
          }
        case _ => ()
      }
      Right(())
    }

    // Extended instruction sets, entry points and their execution modes.
    def entry(instruction: Instruction): Either[Fault, Unit] = {
      instruction match {
        case Instruction.ExtInstImport(dest, name) =>
          LiteralString.decode(name).refine[NonEmpty[String]] match {
            case Left(_) => return Left(Fault.InvalidRequest("SPIR-V extended instruction-set name is empty"))
            case Right(instructionSetName) => extendedInstructionSets.update(id(dest), instructionSetName)
          }
        case Instruction.EntryPoint(executionModel, entryPoint, name, interfaces) =>
          val decodedName = LiteralString.decode(name)
          decodedName.refine[NonEmpty[String]] match {
            case Left(_) => return Left(Fault.InvalidRequest(s"invalid SPIR-V entry-point name '$decodedName'"))
            case Right(kernelName) =>
              entryPoints.update(kernelName, id(entryPoint))
              entryPointInterfaces.update(kernelName, interfaces.view.map(id).toSet)
              unsigned(executionModel) match {
                case ExecutionModel.GLCompute                        => val _ = vulkanEntries.add(kernelName)
                case ExecutionModel.Vertex | ExecutionModel.Fragment =>
                  val _ = vulkanEntries.add(kernelName)
                  val _ = graphicsEntries.add(kernelName)
                case _ => ()
              }
          }
        case Instruction.ExecutionMode(entryPoint, mode) =>
          unsigned(mode) match {
            case ExecutionMode.SubgroupSize if mode.words.length >= 2 =>
              val size = mode.words(1).toUnsigned.raw
              size.refine[Long in 1L ~ 2147483647L] match {
                case Left(_)      => return Left(Fault.InvalidRequest(s"SPIR-V SubgroupSize $size is invalid"))
                case Right(value) => subgroupSizes.update(id(entryPoint), value.toIntExact.widen[Int > 0])
              }
            case size @ (ExecutionMode.LocalSize | ExecutionMode.LocalSizeHint) if mode.words.length >= 4 =>
              val table = if (size == ExecutionMode.LocalSize) workGroupSizes else workGroupSizeHints
              localSize(mode.words(1), mode.words(2), mode.words(3)).foreach(table.update(id(entryPoint), _))
            case _ => localSizeIds += id(entryPoint) -> mode
          }
        case Instruction.ExecutionModeId(entryPoint, mode) => localSizeIds += id(entryPoint) -> mode
        case _                                             => ()
      }
      Right(())
    }

    /** Records the function starting at `start` and returns the index of its OpFunctionEnd. */
    def function(dest: Operand, start: Int): Either[Fault, Int] = {
      val functionId = id(dest)
      val parameters = mutable.ArrayBuilder.make[(Int, Int)]
      val labels     = mutable.HashMap.empty[Int, Int]
      var index      = start + 1
      while (index < module.instructions.size && module.instructions(index) != Instruction.FunctionEnd()) {
        module.instructions(index) match {
          case Instruction.FunctionParameter(resultType, parameter) =>
            parameters += ((id(parameter), id(resultType)))
            valueTypes.update(id(parameter), id(resultType))
          case Instruction.Label(label) => labels.update(id(label), index)
          case _                        => ()
        }
        record(module.instructions(index)) match {
          case Left(error) => return Left(error)
          case Right(())   => ()
        }
        index += 1
      }
      if (index >= module.instructions.size)
        return Left(Fault.InvalidRequest(s"SPIR-V function %$functionId has no OpFunctionEnd"))
      functions.update(functionId, Function(functionId, Vec.from(parameters.result()), start, index, labels.toMap))
      Right(index)
    }

    module.instructions.foreach {
      case Instruction.Name(target, name) => names.update(id(target), LiteralString.decode(name))
      // Optimised and linked modules may omit OpName for imported builtins but keep LinkageAttributes.
      case Instruction.Decorate(target, decoration)
          if decoration.words.length >= 2 && unsigned(decoration) == Decoration.LinkageAttributes =>
        names.update(id(target), LiteralString.decode(decoration.words.drop(1)))
      case _ => ()
    }

    var index = 0
    while (index < module.instructions.size) {
      val instruction = module.instructions(index)
      record(instruction) match {
        case Left(error) => return Left(error)
        case Right(())   => ()
      }
      instruction match {
        case Instruction.Function(dest = dest) =>
          function(dest, index) match {
            case Left(error) => return Left(error)
            case Right(end)  => index = end
          }
        case _ =>
          declare(instruction).flatMap(_ => decorate(instruction)).flatMap(_ => entry(instruction)) match {
            case Left(error) => return Left(error)
            case Right(())   => ()
          }
      }
      index += 1
    }
    // Constant builtins resolve after the complete stream, including forward declarations.
    // OpenCL also uses WorkgroupSize on input variables; the dispatch supplies those.
    val builtinSize = builtins.toSeq.collect {
      case (variable, BuiltIn.WorkgroupSize) if constants.contains(variable) => variable
    }.sorted.foldLeft[Either[Fault, Option[LaunchGeometry.Dimensions]]](Right(None)) { (result, variable) =>
      result.flatMap { previous =>
        constants(variable) match {
          case Constant.Bits(_, lanes) if lanes.size == 3 =>
            positiveTriple(lanes(0), lanes(1), lanes(2))
              .toRight(
                Fault.InvalidRequest(
                  s"SPIR-V WorkgroupSize %$variable has invalid dimensions: ${lanes.mkString(", ")}"
                )
              )
              .flatMap { size =>
                if (previous.exists(_ != size)) Left(Fault.InvalidRequest("SPIR-V WorkgroupSize constants disagree"))
                else Right(Some(size))
              }
          case _ => Left(Fault.InvalidRequest(s"SPIR-V WorkgroupSize %$variable must have three integer components"))
        }
      }
    } match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }

    localSizeIds.foreach { case (entryPoint, mode) =>
      localSizeId(constants, mode, entryPoint, workGroupSizes, workGroupSizeHints)
    }
    val invalidDescriptorArray = vulkanEntries.nonEmpty && descriptorSlots.keys.view.exists { variable =>
      globals.get(variable).exists { global =>
        types.get(global.pointerType) match {
          case Some(RuntimeType.Pointer(_, pointee)) => types.get(pointee) match {
              case Some(RuntimeType.Array(element, count)) =>
                count.raw <= 0 || count.raw > 4096 || types.get(element).exists {
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
    val definedNames     = names.toMap
    Right(Tables(
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
      memberMatrixLayouts = matrixStrides.map { case (key, stride) =>
        key -> MatrixLayout(stride, rowMajorMatrices.contains(key))
      }.toMap,
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
      names = definedNames,
      collectiveFunctions = collectiveFunctions(module, definedFunctions, definedNames),
      valueTypes = valueTypes.toMap,
      idBound = largestId + 1
    ))
  }

  private def image(
      types: scala.collection.Map[Int, RuntimeType],
      instruction: Instruction.TypeImage
  ): Either[Fault, RuntimeType] = {
    val sampledType  = id(instruction.sampledType)
    val dim          = unsigned(instruction.dim)
    val depth        = unsigned(instruction.depth)
    val arrayed      = unsigned(instruction.arrayed)
    val multisampled = unsigned(instruction.mS)
    val sampled      = unsigned(instruction.sampled)
    val format       = unsigned(instruction.imageFormat)
    if (sampled == 1L && multisampled == 1L)
      Either.cond(
        dim == Dim.Value2D && arrayed <= 1L,
        RuntimeType.MultisampledTexture(sampledType, arrayed == 1L),
        Fault.Unsupported("multisampled textures require 2D views")
      )
    else if (dim == Dim.SubpassData)
      Either.cond(
        arrayed == 0L && sampled == 2L && format == ImageFormat.Unknown && multisampled <= 1L,
        RuntimeType.InputAttachment(sampledType, multisampled == 1L),
        Fault.Unsupported("input attachments require non-arrayed SubpassData images")
      )
    else if (sampled == 1L && dim == Dim.Cube)
      Either.cond(
        arrayed <= 1L && multisampled == 0L,
        RuntimeType.CubeTexture(sampledType, arrayed == 1L),
        Fault.Unsupported("cube textures require single-sample views")
      )
    else if (sampled == 1L)
      Either.cond(
        Set[Long](Dim.Value1D, Dim.Value2D, Dim.Value3D, Dim.Buffer).contains(dim) && arrayed <= 1L &&
          !((dim == Dim.Buffer || dim == Dim.Value3D) && arrayed != 0L) && multisampled == 0L,
        RuntimeType.Texture(
          sampledType,
          if (dim == Dim.Value3D) 3 else if (dim == Dim.Value2D) 2 else 1,
          dim == Dim.Buffer,
          arrayed == 1L
        ),
        Fault.Unsupported("sampled images currently require single-sample 1D/2D/3D or texel-buffer views")
      )
    else if (sampled == 0L && format == ImageFormat.Unknown)
      // OpenCL images take their format from the bound image, and the kernel decides how to access them.
      Either.cond(
        Set[Long](Dim.Value1D, Dim.Value2D, Dim.Value3D, Dim.Buffer).contains(dim) && depth <= 1L && arrayed <= 1L &&
          !((dim == Dim.Buffer || dim == Dim.Value3D) && arrayed != 0L) && multisampled == 0L,
        RuntimeType.StorageImage(
          sampledType,
          ImageFormat.Unknown,
          if (dim == Dim.Value3D) 3 else if (dim == Dim.Value2D) 2 else 1,
          dim == Dim.Buffer,
          arrayed == 1L
        ),
        Fault.Unsupported("OpenCL images require single-sample 1D, 2D, 3D or buffer views")
      )
    else {
      val floating = ImageFormats.floatingStorage(format.toInt)
      Either.cond(
        Set[Long](Dim.Value1D, Dim.Value2D, Dim.Value3D, Dim.Cube, Dim.Buffer).contains(dim) && depth <= 2L &&
          arrayed <= 1L &&
          !((dim == Dim.Buffer || dim == Dim.Value3D) && arrayed != 0L) && multisampled == 0L &&
          sampled == 2L &&
          (floating || ImageFormats.integer(format.toInt).isDefined) &&
          (if (floating) types.get(sampledType).contains(RuntimeType.Floating(32))
           else types.get(sampledType).exists {
             case RuntimeType.Integer(width, _) => width.raw == 32
             case _                             => false
           }),
        RuntimeType.StorageImage(
          sampledType,
          format.toInt,
          if (dim == Dim.Value3D) 3 else if (dim == Dim.Value2D || dim == Dim.Cube) 2 else 1,
          dim == Dim.Buffer,
          arrayed == 1L,
          dim == Dim.Cube
        ),
        Fault.Unsupported(
          "SPIR-V storage images require supported 1D/2D/3D/cube or texel-buffer formats with matching scalar type and no multisampling"
        )
      )
    }
  }

  // Fixed-point reachability uses mutable work sets to avoid rebuilding the call graph on each iteration.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def collectiveFunctions(module: Module, functions: Map[Int, Function], names: Map[Int, String]): Set[Int] = {
    def body(function: Function): Iterator[Instruction] =
      Iterator.range(function.start + 1, function.end).map(module.instructions(_))
    val result = mutable.HashSet.from(functions.values.collect {
      case function if body(function).exists(collective(_, names)) => function.id
    })
    var changed = true
    while (changed) {
      changed = false
      functions.values.foreach { function =>
        if (
          !result.contains(function.id) && body(function).exists {
            case Instruction.FunctionCall(function = called) => result.contains(id(called))
            case _                                           => false
          }
        ) {
          result += function.id
          changed = true
        }
      }
    }
    result.toSet
  }

  private def collective(instruction: Instruction, names: Map[Int, String]): Boolean = instruction match {
    case value if VMCollectives.isNonUniform(value) => true
    case _: Instruction.DPdx | _: Instruction.DPdy | _: Instruction.Fwidth | _: Instruction.DPdxFine |
        _: Instruction.DPdyFine | _: Instruction.FwidthFine | _: Instruction.DPdxCoarse | _: Instruction.DPdyCoarse |
        _: Instruction.FwidthCoarse | _: Instruction.ImageSampleImplicitLod | _: Instruction.ImageSampleDrefImplicitLod |
        _: Instruction.ImageSampleProjImplicitLod | _: Instruction.ImageSampleProjDrefImplicitLod |
        _: Instruction.ImageQueryLod => true
    case _: Instruction.GroupAny | _: Instruction.GroupAll | _: Instruction.GroupUMin | _: Instruction.GroupUMax |
        _: Instruction.GroupSMin | _: Instruction.GroupSMax | _: Instruction.GroupIAdd | _: Instruction.GroupIMulKHR |
        _: Instruction.GroupBitwiseAndKHR | _: Instruction.GroupBitwiseOrKHR | _: Instruction.GroupBitwiseXorKHR |
        _: Instruction.GroupLogicalAndKHR | _: Instruction.GroupLogicalOrKHR | _: Instruction.GroupLogicalXorKHR |
        _: Instruction.GroupFAdd | _: Instruction.GroupFMin | _: Instruction.GroupFMax | _: Instruction.GroupFMulKHR |
        _: Instruction.SubgroupShuffleINTEL | _: Instruction.SubgroupShuffleXorINTEL |
        _: Instruction.SubgroupShuffleUpINTEL | _: Instruction.SubgroupShuffleDownINTEL |
        _: Instruction.GroupBroadcast | _: Instruction.ControlBarrier | _: Instruction.GroupReserveReadPipePackets |
        _: Instruction.GroupReserveWritePipePackets => true
    case Instruction.FunctionCall(function = called) => names.get(id(called)).exists(OpenCLBuiltins.collective)
    case _                                           => false
  }

  // Composite decoding walks constituents directly and exits on the first undefined one.
  private val addressing = Set(
    SPIRV.Opcode.AccessChain,
    SPIRV.Opcode.InBoundsAccessChain,
    SPIRV.Opcode.PtrAccessChain,
    SPIRV.Opcode.InBoundsPtrAccessChain,
    SPIRV.Opcode.PtrCastToGeneric,
    SPIRV.Opcode.GenericCastToPtr,
    SPIRV.Opcode.Bitcast
  )

  private def literalSampler(typeId: Int, addressing: Long, normalised: Long, filter: Long): Constant = {
    import ImageDescriptor.*
    val mode = addressing match {
      case 2L => AddressMode.ClampToBorder
      case 3L => AddressMode.Repeat
      case 4L => AddressMode.MirroredRepeat
      case _  => AddressMode.ClampToEdge
    }
    val filtering = if (filter == 1L) Filter.Linear else Filter.Nearest
    val words     = Array.fill(SamplerWord.Count)(0L)
    words(SamplerWord.MinFilter) = filtering
    words(SamplerWord.MagFilter) = filtering
    words(SamplerWord.AddressU) = mode
    words(SamplerWord.AddressV) = mode
    words(SamplerWord.AddressW) = mode
    words(SamplerWord.Unnormalized) = if (normalised == 0L) 1L else 0L
    words(SamplerWord.BorderColour) = BorderColour.FloatTransparentBlack
    words(SamplerWord.MaxLod) = java.lang.Float.floatToRawIntBits(1000f).toLong
    words(SamplerWord.MipmapMode) = MipmapMode.Nearest
    Constant.Sampler(typeId, Vec.from(words))
  }

  private val pointerCasts = Set(SPIRV.Opcode.PtrCastToGeneric, SPIRV.Opcode.GenericCastToPtr, SPIRV.Opcode.Bitcast)

  private def isAddress(constant: Option[Constant]): Boolean = constant.exists {
    case Constant.GlobalAddress(_, _, _, _) => true
    case _                                  => false
  }

  private def globalAddress(
      constants: scala.collection.Map[Int, Constant],
      resultType: Int,
      opcode: Int,
      operands: Vec[Int]
  ): Option[Constant] = {
    val indexes = operands.tail.map(operand =>
      constants.get(operand).collect { case Constant.Bits(_, lanes) if lanes.size == 1 => lanes.head }
    )
    if (indexes.exists(_.isEmpty)) None
    else {
      val values = indexes.flatten
      opcode match {
        case SPIRV.Opcode.PtrAccessChain | SPIRV.Opcode.InBoundsPtrAccessChain if values.nonEmpty =>
          Some(Constant.GlobalAddress(resultType, operands.head, Some(values.head), values.tail))
        case SPIRV.Opcode.AccessChain | SPIRV.Opcode.InBoundsAccessChain =>
          Some(Constant.GlobalAddress(resultType, operands.head, None, values))
        case _ if values.isEmpty => Some(Constant.GlobalAddress(resultType, operands.head, None, Vec.empty))
        case _                   => None
      }
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def constantComposite(
      types: scala.collection.Map[Int, RuntimeType],
      constants: scala.collection.Map[Int, Constant],
      globals: scala.collection.Map[Int, Global],
      resultType: Operand,
      destination: Operand,
      constituents: Vec[Operand]
  ): Either[Fault, Constant] = {
    val lanes    = Array.newBuilder[Long]
    val pointers = Vec.newBuilder[(Int, Int)]
    var count    = 0
    var index    = 0
    while (index < constituents.size) {
      val constituent = id(constituents(index))
      constants.get(constituent) match {
        case Some(Constant.Bits(_, values)) =>
          lanes ++= values
          count += values.size
        // Composite leaves use the same zero pointer representation as OpConstantNull aggregates.
        case Some(Constant.NullPointer(_, _, _)) =>
          lanes += 0L
          count += 1
        case Some(Constant.GlobalAddress(_, _, _, _)) =>
          lanes += 0L
          pointers += count -> constituent
          count += 1
        case Some(Constant.Composite(_, values, nested)) =>
          lanes ++= values
          pointers ++= nested.map((lane, target) => (count + lane) -> target)
          count += values.size
        case Some(Constant.Sampler(_, _)) =>
          return Left(Fault.Unsupported(s"SPIR-V composite constant %${id(destination)} contains a sampler"))
        case None if globals.contains(constituent) =>
          lanes += 0L
          pointers += count -> constituent
          count += 1
        case None => return Left(Fault.InvalidRequest(s"SPIR-V value %$constituent is undefined"))
      }
      index += 1
    }
    val result    = Vec.from(lanes.result())
    val addresses = pointers.result()
    scalarLeafCount(types, id(resultType)).flatMap(expected =>
      Either.cond(
        result.size == expected.raw,
        if (addresses.isEmpty) Constant.Bits(id(resultType), result)
        else Constant.Composite(id(resultType), result, addresses),
        Fault.InvalidRequest(
          s"SPIR-V composite constant %${id(destination)} has ${result.size} scalar leaves, expected $expected"
        )
      )
    )
  }

  private def positiveTriple(x: Long, y: Long, z: Long): Option[LaunchGeometry.Dimensions] =
    for {
      width  <- x.refine[Long in 1L ~ 2147483647L].toOption
      height <- y.refine[Long in 1L ~ 2147483647L].toOption
      depth  <- z.refine[Long in 1L ~ 2147483647L].toOption
    } yield LaunchGeometry.Dimensions(width.toIntExact.widen, height.toIntExact.widen, depth.toIntExact.widen)

  /** A literal constant; specialisation constants declare their default value. */
  private object ScalarConstant {
    def unapply(instruction: Instruction): Option[(Operand, Operand, Operand)] = instruction match {
      case Instruction.Constant(resultType, dest, value)     => Some((resultType, dest, value))
      case Instruction.SpecConstant(resultType, dest, value) => Some((resultType, dest, value))
      case _                                                 => None
    }
  }

  /** A composite constant; specialisation constants declare their default constituents. */
  private object CompositeConstant {
    def unapply(instruction: Instruction): Option[(Operand, Operand, Vec[Operand])] = instruction match {
      case Instruction.ConstantComposite(resultType, dest, constituents)     => Some((resultType, dest, constituents))
      case Instruction.SpecConstantComposite(resultType, dest, constituents) => Some((resultType, dest, constituents))
      case _                                                                 => None
    }
  }

  private def localSize(x: Word, y: Word, z: Word): Option[LaunchGeometry.Dimensions] =
    positiveTriple(x.toUnsigned.toLong, y.toUnsigned.toLong, z.toUnsigned.toLong)

  private def localSizeId(
      constants: scala.collection.Map[Int, Constant],
      mode: Operand,
      entryPoint: Int,
      sizes: mutable.Map[Int, LaunchGeometry.Dimensions],
      hints: mutable.Map[Int, LaunchGeometry.Dimensions]
  ): Unit =
    if (mode.words.length >= 4) {
      val target = unsigned(mode) match {
        case ExecutionMode.LocalSizeId     => Some(sizes)
        case ExecutionMode.LocalSizeHintId => Some(hints)
        case _                             => None
      }
      for {
        table <- target
        x     <- constants.get(mode.words(1).raw).flatMap(scalarBits(_))
        y     <- constants.get(mode.words(2).raw).flatMap(scalarBits(_))
        z     <- constants.get(mode.words(3).raw).flatMap(scalarBits(_))
        size  <- positiveTriple(x, y, z)
      } table.update(entryPoint, size)
    }

  def scalarBits(value: Constant): Option[Long] = value match {
    case Constant.Bits(_, values) if values.size == 1 => Some(values.head)
    case _                                            => None
  }

  // Narrow signed literals arrive sign-extended, but every runtime producer holds scalars masked to their width.
  private def literalBits(types: collection.Map[Int, RuntimeType], resultType: Int, operand: Operand): Long = {
    val bits = operand.words.take(2).zipWithIndex.foldLeft(0L) { case (result, (word, index)) =>
      result | (word.toUnsigned.toLong << (index * 32))
    }
    types.get(resultType) match {
      case Some(RuntimeType.Integer(width, _)) => bits & Bits.mask(width.widen)
      case Some(RuntimeType.Floating(width))   => bits & Bits.mask(width.widen)
      case _                                   => bits
    }
  }
}
