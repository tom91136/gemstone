package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Col

import net.kurobako.gemstone.binary.Bits.mask
import net.kurobako.gemstone.binary.Bits.signExtend
import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.isa.ValidationResult
import scala.collection.mutable // scalafix:ok GemstoneRuntimeCollections.mutable-collection; invocation machine state
import scala.util.control.NonFatal

/** Typed SPIR-V kernel execution over process-neutral allocation identities.
  *
  * This interpreter deliberately operates on the generated instruction enum. It does not translate pointers into host
  * addresses: external memory is always accessed through [[ExecutionMemory]], while Function and local storage use
  * invocation/work-group-owned byte buffers.
  */
@SuppressWarnings(
  Array(
    "scalafix:DisableSyntax.var",
    "scalafix:DisableSyntax.return",
    "scalafix:GemstoneRuntimeCollections.mutable-collection"
  )
)
object Interpreter {
  private val VirtualAddressSpan      = 0x0000010000000000L
  private val TracePointerConversions = sys.env.get("GEMSTONE_SPIRV_POINTER_TRACE").contains("1")

  final class PreparedProgram private[Interpreter] (private[Interpreter] val value: DispatchProgram)

  private def tracePointer(message: => String): Unit =
    if (TracePointerConversions) System.err.println(s"[gemstone:spirv:pointer] $message")

  private enum RuntimeType {
    case Void
    case Bool
    case Integer(width: Int, signed: Boolean)
    case Floating(width: Int)
    case VectorType(component: Int, count: Int)
    case Array(element: Int, count: Long)
    case Structure(members: Col[Int])
    case Pointer(storageClass: Int, pointee: Int)
    case Function(result: Int, parameters: Col[Int])
  }

  private enum Region {
    case External(id: AllocationId)
    case Internal(bytes: Array[Byte])
    case Null
  }

  private final case class Pointer(
      region: Region,
      offset: Long,
      pointee: Int,
      storageClass: Int,
      slots: Option[mutable.LongMap[(Int, Pointer)]] = None
  )

  private enum RuntimeValue {
    case Bits(typeId: Int, lanes: Array[Long])
    case Address(typeId: Int, pointer: Pointer)
  }

  /** Per-invocation writes over immutable module/dispatch values. A large oneDPL launch can contain hundreds of
    * thousands of invocations, so eagerly copying every SPIR-V constant into every lane dominates execution.
    */
  private final class InvocationValues(
      constants: Map[Int, RuntimeValue],
      globals: Map[Int, RuntimeValue],
      idBound: Long
  ) {
    private val denseLimit = 1000000L
    private val dense      =
      if (idBound > 0L && idBound <= denseLimit) Array.ofDim[RuntimeValue | Null](idBound.toInt)
      else Array.empty[RuntimeValue | Null]
    private val denseScalarTypes = Array.fill(dense.length)(-1)
    private val denseScalarBits  = Array.ofDim[Long](dense.length)
    private val baseScalarTypes  = Array.fill(dense.length)(-1)
    private val baseScalarBits   = Array.ofDim[Long](dense.length)
    private val touched          = mutable.ArrayBuffer.empty[Int]
    private val overflow         = mutable.LongMap.empty[RuntimeValue]

    constants.iterator.concat(globals.iterator).foreach {
      case (key, RuntimeValue.Bits(typeId, Array(value))) if key >= 0 && key < dense.length =>
        baseScalarTypes(key) = typeId
        baseScalarBits(key) = value
      case _ => ()
    }

    def get(key: Long): Option[RuntimeValue] = {
      val local =
        if (key >= 0L && key < dense.length.toLong) {
          val index = key.toInt
          if (denseScalarTypes(index) >= 0)
            Some(RuntimeValue.Bits(denseScalarTypes(index), Array(denseScalarBits(index))))
          else
            dense(index) match {
              case null  => None
              case value => Some(value)
            }
        } else overflow.get(key)
      local.orElse(globals.get(key.toInt)).orElse(constants.get(key.toInt))
    }

    def update(key: Long, value: RuntimeValue): Unit =
      if (key >= 0L && key < dense.length.toLong) {
        val index = key.toInt
        if (dense(index) == null && denseScalarTypes(index) < 0) touched += index
        value match {
          case RuntimeValue.Bits(typeId, Array(bits)) =>
            dense(index) = null
            denseScalarTypes(index) = typeId
            denseScalarBits(index) = bits
          case _ =>
            dense(index) = value
            denseScalarTypes(index) = -1
        }
      } else overflow.update(key, value)

    def scalarType(key: Int): Int =
      if (key >= 0 && key < dense.length) {
        val localType = denseScalarTypes(key)
        if (localType >= 0) localType
        else if (dense(key) != null) -1
        else baseScalarTypes(key)
      } else
        overflow.get(key.toLong) match {
          case Some(RuntimeValue.Bits(typeId, Array(_))) => typeId
          case _                                         => -1
        }

    def scalarBits(key: Int): Long =
      if (key >= 0 && key < dense.length) {
        if (denseScalarTypes(key) >= 0) denseScalarBits(key)
        else baseScalarBits(key)
      } else
        overflow(key.toLong) match {
          case RuntimeValue.Bits(_, Array(value)) => value
          case _                                  => throw IllegalStateException(s"SPIR-V value %$key is not scalar")
        }

    def updateScalar(key: Int, typeId: Int, bits: Long): Unit =
      if (key >= 0 && key < dense.length) {
        if (dense(key) == null && denseScalarTypes(key) < 0) touched += key
        dense(key) = null
        denseScalarTypes(key) = typeId
        denseScalarBits(key) = bits
      } else overflow.update(key.toLong, RuntimeValue.Bits(typeId, Array(bits)))

    def clear(): Unit = {
      var index = 0
      while (index < touched.length) {
        dense(touched(index)) = null
        denseScalarTypes(touched(index)) = -1
        index += 1
      }
      touched.clear()
      overflow.clear()
    }
  }

  private enum FloatingOperation(val opcode: String) {
    case Add      extends FloatingOperation("OpFAdd")
    case Subtract extends FloatingOperation("OpFSub")
    case Multiply extends FloatingOperation("OpFMul")
    case Divide   extends FloatingOperation("OpFDiv")
  }

  private enum FloatingComparison(val opcode: String) {
    case OrderedEqual         extends FloatingComparison("OpFOrdEqual")
    case OrderedLessThan      extends FloatingComparison("OpFOrdLessThan")
    case OrderedLessThanEqual extends FloatingComparison("OpFOrdLessThanEqual")
    case OrderedGreaterThan   extends FloatingComparison("OpFOrdGreaterThan")
  }

  private enum FloatingClassification(val opcode: String) {
    case Infinite extends FloatingClassification("OpIsInf")
    case NaN      extends FloatingClassification("OpIsNan")
    case Signed   extends FloatingClassification("OpSignBitSet")
  }

  private final case class FunctionInfo(
      id: Int,
      parameters: Col[(Int, Int)],
      start: Int,
      end: Int,
      labels: Map[Int, Int]
  )

  private final case class CallFrame(
      function: FunctionInfo,
      pc: Int,
      currentLabel: Int,
      predecessor: Int,
      resultType: Int,
      destination: Int
  )

  private final case class GlobalInfo(pointerType: Int, storageClass: Int, initializer: Option[Int])

  private final case class DispatchProgram(
      module: Module,
      types: Map[Int, RuntimeType],
      constants: Map[Int, RuntimeValue],
      globals: Map[Int, GlobalInfo],
      builtins: Map[Int, Int],
      arrayStrides: Map[Int, Int],
      memberOffsets: Map[(Int, Int), Int],
      extendedInstructionSets: Map[Int, String],
      entryPoints: Map[String, Int],
      subgroupSizes: Map[Int, Int],
      functions: Map[Int, FunctionInfo],
      hasCollectives: Boolean
  )

  private final class DirtyPage {
    val bytes         = Array.ofDim[Byte](4096)
    private val dirty = Array.ofDim[Long](64)

    def update(offset: Int, value: Byte): Unit = {
      bytes(offset) = value
      dirty(offset >>> 6) |= 1L << (offset & 63)
    }

    def contains(offset: Int): Boolean =
      (dirty(offset >>> 6) & (1L << (offset & 63))) != 0L
  }

  private final class MemoryState(memory: ExecutionMemory) {
    private val writes                   = mutable.HashMap.empty[AllocationId, mutable.LongMap[DirtyPage]]
    private val externalVirtualAddresses = mutable.HashMap.empty[AllocationId, Long]
    private val internalVirtualAddresses = java.util.IdentityHashMap[Array[Byte], java.lang.Long]()
    private val virtualPointers          = mutable.LongMap.empty[Pointer]
    private var nextVirtualAddress       = 0x0000100000000000L

    def pointerInteger(pointer: Pointer): Either[ExecutionFailure, Long] = {
      val base = pointer.region match {
        case Region.External(id)    => externalVirtualAddresses.getOrElseUpdate(id, allocateVirtualAddress(pointer))
        case Region.Internal(bytes) =>
          val existing = internalVirtualAddresses.get(bytes)
          if (existing == null) {
            val allocated = allocateVirtualAddress(pointer)
            internalVirtualAddresses.put(bytes, allocated)
            allocated
          } else existing.longValue()
        case Region.Null => return Right(0L)
      }
      Either.cond(
        pointer.offset >= 0L && pointer.offset < VirtualAddressSpan,
        base + pointer.offset,
        ExecutionFailure.InvalidRequest(
          s"SPIR-V pointer offset ${pointer.offset} exceeds the virtual region span"
        )
      )
    }

    def integerPointer(address: Long, pointee: Int, storageClass: Int): Either[ExecutionFailure, Pointer] =
      if (address == 0L) Right(Pointer(Region.Null, 0L, pointee, storageClass))
      else {
        val base   = address & ~(VirtualAddressSpan - 1L)
        val offset = address - base
        virtualPointers.get(base) match {
          case Some(origin) if offset >= 0L && offset < VirtualAddressSpan =>
            Either.cond(
              storageClass == 8 || storageClass == origin.storageClass,
              origin.copy(offset = offset, pointee = pointee),
              ExecutionFailure.InvalidRequest(
                s"SPIR-V virtual address 0x${java.lang.Long.toHexString(address)} belongs to storage class " +
                  s"${origin.storageClass}, not $storageClass"
              )
            )
          case _ =>
            Left(
              ExecutionFailure.InvalidRequest(
                s"SPIR-V integer 0x${java.lang.Long.toHexString(address)} is not a dispatch-local virtual address"
              )
            )
        }
      }

    private def allocateVirtualAddress(pointer: Pointer): Long = {
      val address = nextVirtualAddress
      nextVirtualAddress = Math.addExact(nextVirtualAddress, VirtualAddressSpan)
      virtualPointers.update(address, pointer.copy(offset = 0L))
      address
    }

    def read(pointer: Pointer, size: Int): Either[ExecutionFailure, Array[Byte]] = pointer.region match {
      case Region.Internal(bytes) =>
        checkedRange(bytes.length, pointer.offset, size).map(start =>
          java.util.Arrays.copyOfRange(bytes, start, start + size)
        )
      case Region.External(id) =>
        memory.read(id, pointer.offset, size).map { source =>
          val result = source.toArray
          writes.get(id).foreach { pending =>
            var index = 0
            while (index < result.length) {
              val absolute   = pointer.offset + index.toLong
              val pageOffset = (absolute & 4095L).toInt
              pending.get(absolute >>> 12).foreach { page =>
                if (page.contains(pageOffset)) result(index) = page.bytes(pageOffset)
              }
              index += 1
            }
          }
          result
        }
      case Region.Null => Left(ExecutionFailure.InvalidRequest("cannot read through a null SPIR-V pointer"))
    }

    def readScalar(pointer: Pointer, size: Int): Either[ExecutionFailure, Long] = pointer.region match {
      case Region.Internal(bytes) =>
        checkedRange(bytes.length, pointer.offset, size).map(start => LittleEndian.decode(bytes, start, size))
      case Region.External(id) =>
        memory.read(id, pointer.offset, size).map { source =>
          val pending = writes.get(id)
          LittleEndian.decode(source.length) { index =>
            val absolute   = pointer.offset + index.toLong
            val pageOffset = (absolute & 4095L).toInt
            pending match {
              case Some(pages) => pages.get(absolute >>> 12) match {
                  case Some(page) if page.contains(pageOffset) => page.bytes(pageOffset)
                  case _                                       => source(index)
                }
              case None => source(index)
            }
          }
        }
      case Region.Null => Left(ExecutionFailure.InvalidRequest("cannot read through a null SPIR-V pointer"))
    }

    def write(pointer: Pointer, bytes: Array[Byte]): Either[ExecutionFailure, Unit] = pointer.region match {
      case Region.Internal(target) =>
        checkedRange(target.length, pointer.offset, bytes.length).map { start =>
          System.arraycopy(bytes, 0, target, start, bytes.length)
        }
      case Region.External(id) =>
        if (pointer.offset < 0L || pointer.offset > Long.MaxValue - bytes.length.toLong)
          Left(ExecutionFailure.InvalidRequest(s"invalid external write offset ${pointer.offset}"))
        else {
          val pending = writes.getOrElseUpdate(id, mutable.LongMap.empty)
          var index   = 0
          while (index < bytes.length) {
            val absolute   = pointer.offset + index.toLong
            val pageOffset = (absolute & 4095L).toInt
            pending.getOrElseUpdate(absolute >>> 12, DirtyPage()).update(pageOffset, bytes(index))
            index += 1
          }
          Right(())
        }
      case Region.Null => Left(ExecutionFailure.InvalidRequest("cannot write through a null SPIR-V pointer"))
    }

    def writeScalar(pointer: Pointer, value: Long, size: Int): Either[ExecutionFailure, Unit] = pointer.region match {
      case Region.Internal(target) =>
        checkedRange(target.length, pointer.offset, size).map(start => LittleEndian.write(value, target, start, size))
      case Region.External(id) =>
        if (pointer.offset < 0L || size < 0 || pointer.offset > Long.MaxValue - size.toLong)
          Left(ExecutionFailure.InvalidRequest(s"invalid external write offset ${pointer.offset}"))
        else {
          val pending = writes.getOrElseUpdate(id, mutable.LongMap.empty)
          LittleEndian.write(value, size) { (index, byte) =>
            val absolute   = pointer.offset + index.toLong
            val pageOffset = (absolute & 4095L).toInt
            pending
              .getOrElseUpdate(absolute >>> 12, DirtyPage())
              .update(pageOffset, byte)
          }
          Right(())
        }
      case Region.Null => Left(ExecutionFailure.InvalidRequest("cannot write through a null SPIR-V pointer"))
    }

    def flush(): Either[ExecutionFailure, Unit] = {
      val allocations = writes.iterator.toArray.sortBy(_._1.toLong)
      var allocation  = 0
      while (allocation < allocations.length) {
        val (id, pending) = allocations(allocation)
        val pages         = pending.iterator.toArray.sortBy(_._1)
        var pageIndex     = 0
        while (pageIndex < pages.length) {
          val (number, page) = pages(pageIndex)
          var offset         = 0
          while (offset < page.bytes.length) {
            while (offset < page.bytes.length && !page.contains(offset)) offset += 1
            if (offset < page.bytes.length) {
              val start = offset
              while (offset < page.bytes.length && page.contains(offset)) offset += 1
              memory.write(
                id,
                (number << 12) + start.toLong,
                Col.from(java.util.Arrays.copyOfRange(page.bytes, start, offset))
              ) match {
                case Left(error) => return Left(error)
                case Right(())   => ()
              }
            }
          }
          pageIndex += 1
        }
        allocation += 1
      }
      Right(())
    }

    private def checkedRange(length: Int, offset: Long, size: Int): Either[ExecutionFailure, Int] =
      ByteRange
        .start(length, offset, size)
        .toRight(ExecutionFailure.InvalidRequest(s"internal byte range $offset + $size exceeds $length"))
  }

  def prepare(module: Module): Either[ExecutionFailure, PreparedProgram] =
    try compile(module).map(PreparedProgram(_))
    catch {
      case NonFatal(error) =>
        Left(
          ExecutionFailure.InvalidRequest(
            s"malformed SPIR-V execution state: ${Option(error.getMessage).getOrElse(error.getClass.getSimpleName)}"
          )
        )
    }

  /** Validate modules received from an external toolchain before compiling their execution state. Handwritten unit
    * fixtures may use the validation-free overload to exercise individual interpreter rules in isolation.
    */
  def prepare(module: Module, environment: Environment): Either[ExecutionFailure, PreparedProgram] =
    Validator.validate(module, environment) match {
      case ValidationResult.Valid           => prepare(module)
      case ValidationResult.Invalid(issues) =>
        Left(ExecutionFailure.InvalidRequest(issues.map(_.message).mkString("invalid SPIR-V module: ", "; ", "")))
      case ValidationResult.Unsupported(issues) =>
        Left(ExecutionFailure.Unsupported(issues.map(_.message).mkString("unsupported SPIR-V module: ", "; ", "")))
    }

  def run(program: PreparedProgram, dispatch: KernelDispatch, memory: ExecutionMemory): Either[ExecutionFailure, Unit] =
    run(program, dispatch, memory, ExecutionControl.default)

  def run(
      program: PreparedProgram,
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit] =
    try runValidated(program.value, dispatch, memory, control)
    catch {
      case NonFatal(error) =>
        Left(
          ExecutionFailure.InvalidRequest(
            s"malformed SPIR-V execution state: ${Option(error.getMessage).getOrElse(error.getClass.getSimpleName)}"
          )
        )
    }

  def run(module: Module, dispatch: KernelDispatch, memory: ExecutionMemory): Either[ExecutionFailure, Unit] =
    prepare(module).flatMap(run(_, dispatch, memory))

  private def runValidated(
      program: DispatchProgram,
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit] =
    program.entryPoints.get(dispatch.kernelName).toRight(
      ExecutionFailure.NotFound(s"SPIR-V entry point ${dispatch.kernelName} does not exist")
    ).flatMap { functionId =>
      program.functions.get(functionId).toRight(
        ExecutionFailure.InvalidRequest(s"SPIR-V entry point ${dispatch.kernelName} has no function body")
      ).flatMap { function =>
        Either.cond(
          function.parameters.size == dispatch.arguments.size,
          (),
          ExecutionFailure.InvalidRequest(
            s"SPIR-V entry point ${dispatch.kernelName} expects ${function.parameters.size} arguments, " +
              s"received ${dispatch.arguments.size}"
          )
        ).flatMap(_ => executeDispatch(program, function, dispatch, memory, control))
      }
    }

  private def compile(module: Module): Either[ExecutionFailure, DispatchProgram] = {
    val types                   = mutable.HashMap.empty[Int, RuntimeType]
    val constants               = mutable.HashMap.empty[Int, RuntimeValue]
    val globals                 = mutable.HashMap.empty[Int, GlobalInfo]
    val builtins                = mutable.HashMap.empty[Int, Int]
    val arrayStrides            = mutable.HashMap.empty[Int, Int]
    val memberOffsets           = mutable.HashMap.empty[(Int, Int), Int]
    val extendedInstructionSets = mutable.HashMap.empty[Int, String]
    val entryPoints             = mutable.HashMap.empty[String, Int]
    val subgroupSizes           = mutable.HashMap.empty[Int, Int]
    val functions               = mutable.HashMap.empty[Int, FunctionInfo]

    var index = 0
    while (index < module.instructions.size) {
      module.instructions(index) match {
        case Instruction.TypeVoid(dest)                   => types.update(id(dest), RuntimeType.Void)
        case Instruction.TypeBool(dest)                   => types.update(id(dest), RuntimeType.Bool)
        case Instruction.TypeInt(dest, width, signedness) =>
          val bits = unsigned(width).toInt
          if (!Col(8, 16, 32, 64).contains(bits))
            return Left(ExecutionFailure.Unsupported(s"SPIR-V integer width $bits is unsupported"))
          types.update(id(dest), RuntimeType.Integer(bits, unsigned(signedness) != 0L))
        case Instruction.TypeFloat(dest, width, _) =>
          val bits = unsigned(width).toInt
          if (!Col(16, 32, 64).contains(bits))
            return Left(ExecutionFailure.Unsupported(s"SPIR-V floating-point width $bits is unsupported"))
          types.update(id(dest), RuntimeType.Floating(bits))
        case Instruction.TypeVector(dest, component, count) =>
          types.update(id(dest), RuntimeType.VectorType(id(component), unsigned(count).toInt))
        case Instruction.TypeArray(dest, element, length) =>
          val count = constants.get(id(length)).flatMap(scalarBits) match {
            case Some(value) => value
            case None        =>
              return Left(
                ExecutionFailure.InvalidRequest(s"SPIR-V array length %${id(length)} is not a scalar constant")
              )
          }
          types.update(id(dest), RuntimeType.Array(id(element), count))
        case Instruction.TypeStruct(dest, members) =>
          types.update(id(dest), RuntimeType.Structure(members.map(id)))
        case Instruction.TypePointer(dest, storageClass, dataType) =>
          types.update(id(dest), RuntimeType.Pointer(unsigned(storageClass).toInt, id(dataType)))
        case Instruction.TypeFunction(dest, result, parameters) =>
          types.update(id(dest), RuntimeType.Function(id(result), parameters.map(id)))
        case Instruction.ConstantTrue(resultType, dest) =>
          constants.update(id(dest), RuntimeValue.Bits(id(resultType), Array(1L)))
        case Instruction.ConstantFalse(resultType, dest) =>
          constants.update(id(dest), RuntimeValue.Bits(id(resultType), Array(0L)))
        case Instruction.Constant(resultType, dest, value) =>
          constants.update(id(dest), RuntimeValue.Bits(id(resultType), Array(literalBits(value))))
        case Instruction.SpecConstant(resultType, dest, value) =>
          constants.update(id(dest), RuntimeValue.Bits(id(resultType), Array(literalBits(value))))
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
            GlobalInfo(id(resultType), unsigned(storageClass).toInt, initializer.map(id))
          )
        case Instruction.Decorate(target, decoration) if decoration.words.size >= 2 && unsigned(decoration) == 11L =>
          builtins.update(id(target), decoration.words(1).toUnsigned.toInt)
        case Instruction.Decorate(target, decoration) if decoration.words.size >= 2 && unsigned(decoration) == 6L =>
          arrayStrides.update(id(target), Math.toIntExact(decoration.words(1).toUnsigned))
        case Instruction.MemberDecorate(structureType, member, decoration)
            if decoration.words.size >= 2 && unsigned(decoration) == 35L =>
          memberOffsets.update(
            id(structureType) -> Math.toIntExact(unsigned(member)),
            Math.toIntExact(decoration.words(1).toUnsigned)
          )
        case Instruction.ExtInstImport(dest, name) =>
          extendedInstructionSets.update(id(dest), literalString(name))
        case Instruction.EntryPoint(_, entryPoint, name, _) =>
          entryPoints.update(literalString(name), id(entryPoint))
        case Instruction.ExecutionMode(entryPoint, mode)
            if unsigned(mode) == 35L && mode.words.size >= 2 =>
          val size = mode.words(1).toUnsigned
          if (size == 0L || size > Int.MaxValue.toLong)
            return Left(ExecutionFailure.InvalidRequest(s"SPIR-V SubgroupSize $size is invalid"))
          subgroupSizes.update(id(entryPoint), size.toInt)
        case Instruction.Function(_, dest, _, _) =>
          val functionId = id(dest)
          val parameters = mutable.ArrayBuilder.make[(Int, Int)]
          val labels     = mutable.HashMap.empty[Int, Int]
          val start      = index
          index += 1
          while (
            index < module.instructions.size && !module.instructions(index).isInstanceOf[Instruction.FunctionEnd]
          ) {
            module.instructions(index) match {
              case Instruction.FunctionParameter(resultType, parameter) =>
                parameters += ((id(parameter), id(resultType)))
              case Instruction.Label(label) => labels.update(id(label), index)
              case _                        => ()
            }
            index += 1
          }
          if (index >= module.instructions.size)
            return Left(ExecutionFailure.InvalidRequest(s"SPIR-V function %$functionId has no OpFunctionEnd"))
          functions.update(
            functionId,
            FunctionInfo(functionId, Col.from(parameters.result()), start, index, labels.toMap)
          )
        case _ => ()
      }
      index += 1
    }
    Right(
      DispatchProgram(
        module,
        types.toMap,
        constants.toMap,
        globals.toMap,
        builtins.toMap,
        arrayStrides.toMap,
        memberOffsets.toMap,
        extendedInstructionSets.toMap,
        entryPoints.toMap,
        subgroupSizes.toMap,
        functions.toMap,
        module.instructions.exists {
          case _: Instruction.GroupAny | _: Instruction.GroupUMin | _: Instruction.GroupIAdd |
              _: Instruction.GroupIMulKHR | _: Instruction.GroupFAdd | _: Instruction.GroupBroadcast |
              _: Instruction.ControlBarrier => true
          case _ => false
        }
      )
    )
  }

  private def constantComposite(
      types: collection.Map[Int, RuntimeType],
      constants: collection.Map[Int, RuntimeValue],
      resultType: Operand,
      destination: Operand,
      constituents: Col[Operand]
  ): Either[ExecutionFailure, RuntimeValue] = {
    val lanes = Array.newBuilder[Long]
    var index = 0
    while (index < constituents.size) {
      val constituent = constituents(index)
      constants.get(id(constituent)) match {
        case Some(RuntimeValue.Bits(_, values)) => lanes ++= values
        case Some(_)                            =>
          return Left(
            ExecutionFailure.InvalidRequest(
              s"SPIR-V composite constant %${id(destination)} contains pointer %${id(constituent)}"
            )
          )
        case None => return Left(undefined(constituent))
      }
      index += 1
    }
    val result = lanes.result()
    scalarLeafCount(types, id(resultType)).flatMap(expected =>
      Either.cond(
        result.length == expected,
        RuntimeValue.Bits(id(resultType), result),
        ExecutionFailure.InvalidRequest(
          s"SPIR-V composite constant %${id(destination)} has ${result.length} scalar leaves, expected $expected"
        )
      )
    )
  }

  private def zeroValue(
      types: collection.Map[Int, RuntimeType],
      resultType: Operand,
      destination: Operand,
      opcode: String
  ): Either[ExecutionFailure, RuntimeValue] = {
    val typeId = id(resultType)
    types.get(typeId) match {
      case Some(RuntimeType.Pointer(storageClass, pointee)) =>
        Right(RuntimeValue.Address(typeId, Pointer(Region.Null, 0L, pointee, storageClass)))
      case _ =>
        scalarLeafCount(types, typeId) match {
          case Right(count)                                   => Right(RuntimeValue.Bits(typeId, Array.fill(count)(0L)))
          case Left(ExecutionFailure.InvalidRequest(message)) =>
            Left(
              ExecutionFailure.InvalidRequest(s"$opcode %${id(destination)} has invalid result type: $message")
            )
          case Left(error) => Left(error)
        }
    }
  }

  private def scalarLeafCount(
      types: collection.Map[Int, RuntimeType],
      typeId: Int
  ): Either[ExecutionFailure, Int] = types.get(typeId) match {
    case Some(RuntimeType.Bool) | Some(RuntimeType.Integer(_, _)) | Some(RuntimeType.Floating(_)) => Right(1)
    case Some(RuntimeType.VectorType(component, count))                                           =>
      scalarLeafCount(types, component).flatMap(value => checkedLeafCount(typeId, value.toLong * count.toLong))
    case Some(RuntimeType.Array(element, count)) =>
      scalarLeafCount(types, element).flatMap(value => checkedLeafCount(typeId, value.toLong * count))
    case Some(RuntimeType.Structure(members)) =>
      members.foldLeft[Either[ExecutionFailure, Int]](Right(0)) { (total, member) =>
        total.flatMap(left =>
          scalarLeafCount(types, member).flatMap(right => checkedLeafCount(typeId, left.toLong + right))
        )
      }
    case value =>
      Left(ExecutionFailure.InvalidRequest(s"SPIR-V type %$typeId has no scalar data leaves: $value"))
  }

  private def checkedLeafCount(typeId: Int, count: Long): Either[ExecutionFailure, Int] =
    Either.cond(
      count >= 0L && count <= Int.MaxValue.toLong,
      count.toInt,
      ExecutionFailure.InvalidRequest(s"SPIR-V type %$typeId has an invalid scalar leaf count: $count")
    )

  private def executeDispatch(
      program: DispatchProgram,
      function: FunctionInfo,
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit] = {
    val state        = MemoryState(memory)
    val globalValues = materializeGlobals(program, storageClass => storageClass != 4 && storageClass != 6) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    val sx             = dispatch.geometry.groupSize.x
    val sy             = dispatch.geometry.groupSize.y
    val sz             = dispatch.geometry.groupSize.z
    val gx             = dispatch.geometry.groupCount.x
    val gy             = dispatch.geometry.groupCount.y
    val gz             = dispatch.geometry.groupCount.z
    val totalLocal     = Math.multiplyExact(Math.multiplyExact(sx, sy), sz)
    val subgroup       = program.subgroupSizes.getOrElse(function.id, math.max(1, math.min(8, totalLocal)))
    val reusableValues =
      if (program.hasCollectives) None
      else
        Some(
          InvocationValues(
            program.constants,
            globalValues,
            program.module.header.bound.toUnsigned
          )
        )
    var reusableExecution: InvocationExecution | Null = null
    var groupZ                                        = 0
    while (groupZ < gz) {
      var groupY = 0
      while (groupY < gy) {
        var groupX = 0
        while (groupX < gx) {
          val workgroupValues = materializeGlobals(program, _ == 4) match {
            case Left(error)  => return Left(error)
            case Right(value) => value
          }
          val shared = dispatch.arguments.zipWithIndex.collect { case (KernelArgument.Local(size), argumentIndex) =>
            argumentIndex -> Region.Internal(Array.ofDim[Byte](checkedSize(size)))
          }.toMap
          val executions = mutable.ArrayBuffer.empty[InvocationExecution]
          var localZ     = 0
          while (localZ < sz) {
            var localY = 0
            while (localY < sy) {
              var localX = 0
              while (localX < sx) {
                val linearLocal = (localZ * sy + localY) * sx + localX
                val globalX     = groupX.toLong * sx + localX
                val globalY     = groupY.toLong * sy + localY
                val globalZ     = groupZ.toLong * sz + localZ
                if (
                  globalX < dispatch.geometry.globalSize.x && globalY < dispatch.geometry.globalSize.y &&
                  globalZ < dispatch.geometry.globalSize.z
                ) {
                  val privateValues = materializeGlobals(program, _ == 6) match {
                    case Left(error)  => return Left(error)
                    case Right(value) => value
                  }
                  val invocation = Invocation(
                    groupX = groupX.toLong,
                    groupY = groupY.toLong,
                    groupZ = groupZ.toLong,
                    localX = localX.toLong,
                    localY = localY.toLong,
                    localZ = localZ.toLong,
                    globalX = globalX,
                    globalY = globalY,
                    globalZ = globalZ,
                    globalSizeX = dispatch.geometry.globalSize.x.toLong,
                    globalSizeY = dispatch.geometry.globalSize.y.toLong,
                    globalSizeZ = dispatch.geometry.globalSize.z.toLong,
                    workgroupSizeX = sx.toLong,
                    workgroupSizeY = sy.toLong,
                    workgroupSizeZ = sz.toLong,
                    subgroupSize = subgroup,
                    subgroupIndex = linearLocal / subgroup,
                    subgroupLocal = linearLocal % subgroup
                  )
                  prepareInvocation(
                    program,
                    function,
                    dispatch.arguments,
                    shared,
                    invocation,
                    globalValues ++ workgroupValues ++ privateValues,
                    reusableValues,
                    reusableExecution
                  ) match {
                    case Left(error)  => return Left(error)
                    case Right(value) =>
                      if (program.hasCollectives) executions += value
                      else {
                        reusableExecution = value
                        resumeInvocation(program, value, state, control) match {
                          case Left(error)                  => return Left(error)
                          case Right(()) if value.completed => ()
                          case Right(())                    =>
                            return Left(
                              ExecutionFailure.InvalidRequest(
                                "SPIR-V program suspended at a collective absent from its static instruction set"
                              )
                            )
                        }
                      }
                  }
                }
                localX += 1
              }
              localY += 1
            }
            localZ += 1
          }
          if (program.hasCollectives)
            runWorkgroup(program, executions, state, control) match {
              case Left(error) => return Left(error)
              case Right(())   => ()
            }
          groupX += 1
        }
        groupY += 1
      }
      groupZ += 1
    }
    state.flush()
  }

  private final case class Invocation(
      groupX: Long,
      groupY: Long,
      groupZ: Long,
      localX: Long,
      localY: Long,
      localZ: Long,
      globalX: Long,
      globalY: Long,
      globalZ: Long,
      globalSizeX: Long,
      globalSizeY: Long,
      globalSizeZ: Long,
      workgroupSizeX: Long,
      workgroupSizeY: Long,
      workgroupSizeZ: Long,
      subgroupSize: Int,
      subgroupIndex: Int,
      subgroupLocal: Int
  ) {
    val group: Array[Long]         = Array(groupX, groupY, groupZ)
    val local: Array[Long]         = Array(localX, localY, localZ)
    val global: Array[Long]        = Array(globalX, globalY, globalZ)
    val globalSize: Array[Long]    = Array(globalSizeX, globalSizeY, globalSizeZ)
    val workgroupSize: Array[Long] = Array(workgroupSizeX, workgroupSizeY, workgroupSizeZ)
    def numSubgroups: Long         =
      (workgroupSizeX * workgroupSizeY * workgroupSizeZ + subgroupSize.toLong - 1L) / subgroupSize.toLong
  }

  private enum CollectiveKind {
    case Any
    case UnsignedMin
    case IntegerAdd
    case IntegerMultiply
    case FloatingAdd
    case Broadcast
    case Barrier
  }

  private enum CollectivePayload {
    case Value(resultType: Int, destination: Int, value: RuntimeValue)
    case Barrier(memoryScope: Int, semantics: Long)
  }

  private final case class CollectiveRequest(
      kind: CollectiveKind,
      function: Int,
      pc: Int,
      scope: Int,
      payload: CollectivePayload,
      operation: Int = 0,
      selector: Col[Long] = Col.empty
  )

  private final class InvocationExecution(
      var invocation: Invocation,
      val values: InvocationValues,
      var currentFunction: FunctionInfo,
      var pc: Int,
      var currentLabel: Int,
      var predecessor: Int,
      var instructionCount: Long,
      val calls: mutable.ArrayDeque[CallFrame],
      var suspended: Option[CollectiveRequest],
      var completed: Boolean
  ) {
    def reset(nextInvocation: Invocation, function: FunctionInfo): Unit = {
      invocation = nextInvocation
      currentFunction = function
      pc = function.start + 1
      currentLabel = -1
      predecessor = -1
      instructionCount = 0L
      calls.clear()
      suspended = None
      completed = false
    }
  }

  private def prepareInvocation(
      program: DispatchProgram,
      function: FunctionInfo,
      arguments: Col[KernelArgument],
      shared: Map[Int, Region],
      invocation: Invocation,
      globalValues: Map[Int, RuntimeValue],
      reusableValues: Option[InvocationValues],
      reusableExecution: InvocationExecution | Null
  ): Either[ExecutionFailure, InvocationExecution] = {
    val values = reusableValues match {
      case Some(value) =>
        value.clear()
        value
      case None =>
        InvocationValues(program.constants, globalValues, program.module.header.bound.toUnsigned)
    }
    globalValues.foreach { case (id, value) => values.update(id, value) }
    val globalIterator = program.globals.iterator
    while (globalIterator.hasNext) {
      val (variable, global) = globalIterator.next()
      program.builtins.get(variable) match {
        case Some(builtin) =>
          val pointee = pointerPointee(program, global.pointerType)
          val value   = builtinValue(program, pointee, builtin, invocation) match {
            case Left(error)   => return Left(error)
            case Right(result) => result
          }
          val bytes = encode(program, value)
          values.update(
            variable,
            RuntimeValue.Address(
              global.pointerType,
              Pointer(Region.Internal(bytes), 0L, pointee, global.storageClass)
            )
          )
        case None => ()
      }
    }
    var parameterIndex = 0
    while (parameterIndex < function.parameters.size) {
      val (parameterId, parameterType) = function.parameters(parameterIndex)
      bindArgument(program, parameterType, arguments(parameterIndex), shared.get(parameterIndex)) match {
        case Left(error)  => return Left(error)
        case Right(value) => values.update(parameterId, value)
      }
      parameterIndex += 1
    }

    val execution = reusableExecution match {
      case null =>
        InvocationExecution(
          invocation,
          values,
          function,
          function.start + 1,
          -1,
          -1,
          0L,
          mutable.ArrayDeque.empty[CallFrame],
          None,
          false
        )
      case value =>
        value.reset(invocation, function)
        value
    }
    Right(execution)
  }

  private def resumeInvocation(
      program: DispatchProgram,
      execution: InvocationExecution,
      memory: MemoryState,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit] = {
    val values           = execution.values
    var currentFunction  = execution.currentFunction
    var pc               = execution.pc
    var currentLabel     = execution.currentLabel
    var predecessor      = execution.predecessor
    var instructionCount = execution.instructionCount
    val calls            = execution.calls
    while (pc < currentFunction.end) {
      instructionCount += 1L
      control.checkpoint() match {
        case Left(fault) => return Left(ExecutionFailure.Failed(fault.message))
        case Right(())   => ()
      }
      program.module.instructions(pc) match {
        case Instruction.FunctionParameter(_, _) => pc += 1
        case Instruction.Label(label)            =>
          currentLabel = id(label)
          pc += 1
        case Instruction.Phi(resultType, dest, incoming) =>
          phiIncoming(incoming, predecessor) match {
            case Left(error)          => return Left(error)
            case Right(Some(valueId)) =>
              if (values.scalarType(valueId) >= 0)
                values.updateScalar(id(dest), id(resultType), values.scalarBits(valueId))
              else
                values.get(valueId.toLong) match {
                  case Some(value) => values.update(id(dest), retag(value, id(resultType)))
                  case None        =>
                    return Left(
                      ExecutionFailure.InvalidRequest(
                        s"OpPhi %${id(dest)} has no incoming value for label %$predecessor"
                      )
                    )
                }
              pc += 1
            case Right(None) =>
              return Left(
                ExecutionFailure.InvalidRequest(s"OpPhi %${id(dest)} has no incoming value for label %$predecessor")
              )
          }
        case Instruction.Variable(resultType, dest, storageClass, initializer) =>
          val pointee = pointerPointee(program, id(resultType))
          val buffer  = Array.ofDim[Byte](typeSize(program, pointee))
          initializer.flatMap(value => values.get(id(value))).foreach(value =>
            System.arraycopy(
              encode(program, value),
              0,
              buffer,
              0,
              math.min(buffer.length, encode(program, value).length)
            )
          )
          values.update(
            id(dest),
            RuntimeValue.Address(
              id(resultType),
              Pointer(Region.Internal(buffer), 0L, pointee, unsigned(storageClass).toInt)
            )
          )
          pc += 1
        case Instruction.Load(resultType, dest, pointer, _) =>
          address(values, pointer) match {
            case Left(error)   => return Left(error)
            case Right(source) => program.types.get(id(resultType)) match {
                case Some(RuntimeType.Pointer(_, _)) => loadPointer(program, source, id(resultType)) match {
                    case Left(error)  => return Left(error)
                    case Right(value) =>
                      values.update(id(dest), value)
                      pc += 1
                  }
                case Some(RuntimeType.Bool) | Some(RuntimeType.Integer(_, _)) | Some(RuntimeType.Floating(_)) =>
                  memory.readScalar(source, typeSize(program, id(resultType))) match {
                    case Left(error) => return Left(error)
                    case Right(bits) =>
                      values.updateScalar(id(dest), id(resultType), bits)
                      pc += 1
                  }
                case _ => memory.read(source, typeSize(program, id(resultType))) match {
                    case Left(error)  => return Left(error)
                    case Right(bytes) =>
                      values.update(id(dest), decode(program, id(resultType), bytes))
                      pc += 1
                  }
              }
          }
        case Instruction.Store(pointer, source, _) =>
          address(values, pointer) match {
            case Right(target) if values.scalarType(id(source)) >= 0 =>
              val sourceType = values.scalarType(id(source))
              memory.writeScalar(target, values.scalarBits(id(source)), typeSize(program, sourceType)) match {
                case Left(error) => return Left(error)
                case Right(())   => pc += 1
              }
            case Right(target) => values.get(id(source)) match {
                case Some(RuntimeValue.Address(sourceType, value)) =>
                  storePointer(program, target, sourceType, value) match {
                    case Left(error) => return Left(error)
                    case Right(())   => pc += 1
                  }
                case Some(value) => memory.write(target, encode(program, value)) match {
                    case Left(error) => return Left(error)
                    case Right(())   => pc += 1
                  }
                case None => return Left(undefined(source))
              }
            case Left(error) => return Left(error)
          }
        case Instruction.Bitcast(resultType, dest, source) =>
          values.get(id(source)) match {
            case Some(RuntimeValue.Address(_, pointer)) =>
              val pointee = pointerPointee(program, id(resultType))
              values.update(id(dest), RuntimeValue.Address(id(resultType), pointer.copy(pointee = pointee)))
              pc += 1
            case Some(value) =>
              values.update(id(dest), retag(value, id(resultType)))
              pc += 1
            case None => return Left(undefined(source))
          }
        case Instruction.PtrCastToGeneric(resultType, dest, source) =>
          (program.types.get(id(resultType)), values.get(id(source))) match {
            case (Some(RuntimeType.Pointer(8, pointee)), Some(RuntimeValue.Address(sourceType, pointer))) =>
              program.types.get(sourceType) match {
                case Some(RuntimeType.Pointer(storageClass, sourcePointee))
                    if Col(4, 5, 7).contains(storageClass) && sourcePointee == pointee &&
                      pointer.pointee == pointee && (pointer.region == Region.Null || pointer.storageClass == storageClass) =>
                  values.update(id(dest), RuntimeValue.Address(id(resultType), pointer))
                  pc += 1
                case Some(RuntimeType.Pointer(storageClass, sourcePointee)) =>
                  return Left(
                    ExecutionFailure.InvalidRequest(
                      s"OpPtrCastToGeneric cannot cast storage class $storageClass pointee %$sourcePointee to %$pointee"
                    )
                  )
                case _ =>
                  return Left(ExecutionFailure.InvalidRequest("OpPtrCastToGeneric source type is not a pointer"))
              }
            case (Some(RuntimeType.Pointer(storageClass, _)), _) =>
              return Left(
                ExecutionFailure.InvalidRequest(
                  s"OpPtrCastToGeneric result pointer has storage class $storageClass instead of Generic"
                )
              )
            case (None, _) =>
              return Left(
                ExecutionFailure.InvalidRequest(s"OpPtrCastToGeneric result type %${id(resultType)} is undefined")
              )
            case (_, None) => return Left(undefined(source))
            case _         => return Left(ExecutionFailure.InvalidRequest("OpPtrCastToGeneric source is not a pointer"))
          }
        case Instruction.GenericCastToPtr(resultType, dest, source) =>
          genericCastToPointer(program, values, resultType, dest, source) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.PtrAccessChain(resultType, dest, base, element, indexes) =>
          accessChain(program, values, resultType, dest, base, Some(element), indexes) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.InBoundsPtrAccessChain(resultType, dest, base, element, indexes) =>
          accessChain(program, values, resultType, dest, base, Some(element), indexes) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AccessChain(resultType, dest, base, indexes) =>
          accessChain(program, values, resultType, dest, base, None, indexes) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.InBoundsAccessChain(resultType, dest, base, indexes) =>
          accessChain(program, values, resultType, dest, base, None, indexes) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.CompositeExtract(resultType, dest, composite, indexes) =>
          values.get(id(composite)) match {
            case Some(RuntimeValue.Bits(_, lanes)) if indexes.size == 1 =>
              val component = unsigned(indexes.head).toInt
              if (component < 0 || component >= lanes.length)
                return Left(ExecutionFailure.InvalidRequest(s"OpCompositeExtract index $component is out of range"))
              values.update(id(dest), RuntimeValue.Bits(id(resultType), Array(lanes(component))))
              pc += 1
            case Some(_) =>
              return Left(ExecutionFailure.Unsupported("composite extraction is only implemented for vectors"))
            case None => return Left(undefined(composite))
          }
        case Instruction.ExtInst(resultType, dest, set, inst, sources) =>
          extendedInstruction(program, values, resultType, dest, set, inst, sources) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.UConvert(resultType, dest, source) =>
          val sourceId = id(source)
          if (values.scalarType(sourceId) >= 0) {
            val width = scalarWidth(program, id(resultType))
            values.updateScalar(id(dest), id(resultType), values.scalarBits(sourceId) & mask(width))
            pc += 1
          } else
            values.get(sourceId) match {
              case Some(RuntimeValue.Bits(_, lanes)) =>
                val width = scalarWidth(program, id(resultType))
                values.update(id(dest), RuntimeValue.Bits(id(resultType), lanes.map(_ & mask(width))))
                pc += 1
              case Some(_) => return Left(ExecutionFailure.InvalidRequest("OpUConvert cannot convert a pointer"))
              case None    => return Left(undefined(source))
            }
        case Instruction.SConvert(resultType, dest, source) =>
          signedConvert(program, values, resultType, dest, source) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.ConvertPtrToU(resultType, dest, pointer) =>
          val resultWidth = integerShape(program, id(resultType)) match {
            case Some((width, 1)) => width
            case _                =>
              return Left(ExecutionFailure.InvalidRequest("OpConvertPtrToU requires a scalar integer result"))
          }
          address(values, pointer).flatMap(memory.pointerInteger) match {
            case Left(error)  => return Left(error)
            case Right(value) =>
              tracePointer(
                s"OpConvertPtrToU function=%${currentFunction.id} pc=$pc local=" +
                  s"${execution.invocation.local.mkString("(", ",", ")")} pointer=%${id(pointer)} result=%${id(dest)} " +
                  s"value=0x${java.lang.Long.toHexString(value)}"
              )
              values.update(id(dest), RuntimeValue.Bits(id(resultType), Array(value & mask(resultWidth))))
              pc += 1
          }
        case Instruction.ConvertUToPtr(resultType, dest, integerValue) =>
          (program.types.get(id(resultType)), values.get(id(integerValue))) match {
            case (
                  Some(RuntimeType.Pointer(storageClass, pointee)),
                  Some(RuntimeValue.Bits(integerType, Array(rawAddress)))
                ) if integerShape(program, integerType).exists(_._2 == 1) =>
              tracePointer(
                s"OpConvertUToPtr function=%${currentFunction.id} pc=$pc local=" +
                  s"${execution.invocation.local.mkString("(", ",", ")")} source=%${id(integerValue)} result=%${id(dest)} " +
                  s"value=0x${java.lang.Long.toHexString(rawAddress)}"
              )
              memory.integerPointer(rawAddress & mask(scalarWidth(program, integerType)), pointee, storageClass) match {
                case Left(error) =>
                  return Left(
                    ExecutionFailure.InvalidRequest(
                      s"${error.message} at OpConvertUToPtr function %${currentFunction.id}, pc $pc, " +
                        s"source %${id(integerValue)}, result %${id(dest)}"
                    )
                  )
                case Right(pointer) =>
                  values.update(id(dest), RuntimeValue.Address(id(resultType), pointer))
                  pc += 1
              }
            case (Some(RuntimeType.Pointer(_, _)), Some(_)) =>
              return Left(ExecutionFailure.InvalidRequest("OpConvertUToPtr requires a scalar integer operand"))
            case (_, None)    => return Left(undefined(integerValue))
            case (Some(_), _) =>
              return Left(ExecutionFailure.InvalidRequest("OpConvertUToPtr requires a pointer result type"))
            case (None, _) =>
              return Left(
                ExecutionFailure.InvalidRequest(s"OpConvertUToPtr result type %${id(resultType)} is undefined")
              )
          }
        case Instruction.ConvertSToF(resultType, dest, source) =>
          convertSignedToFloat(program, values, resultType, dest, source) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.ConvertFToS(resultType, dest, source) =>
          convertFloatToSigned(program, values, resultType, dest, source) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.ConvertFToU(resultType, dest, source) =>
          convertFloatToUnsigned(program, values, resultType, dest, source) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.CopyObject(resultType, dest, source) =>
          values.get(id(source)) match {
            case Some(value) =>
              values.update(id(dest), retag(value, id(resultType)))
              pc += 1
            case None => return Left(undefined(source))
          }
        case Instruction.LifetimeStart(pointer, size) =>
          validateLifetime(program, values, "OpLifetimeStart", pointer, size) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.LifetimeStop(pointer, size) =>
          validateLifetime(program, values, "OpLifetimeStop", pointer, size) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.IAdd(resultType, dest, left, right) =>
          binary(program, values, resultType, dest, left, right)(_ + _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.ISub(resultType, dest, left, right) =>
          binary(program, values, resultType, dest, left, right)(_ - _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.IMul(resultType, dest, left, right) =>
          binary(program, values, resultType, dest, left, right)(_ * _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.SDiv(resultType, dest, left, right) =>
          signedIntegerArithmetic(
            program,
            values,
            resultType,
            dest,
            left,
            right,
            SignedIntegerOperation.Divide
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.SRem(resultType, dest, left, right) =>
          signedIntegerArithmetic(
            program,
            values,
            resultType,
            dest,
            left,
            right,
            SignedIntegerOperation.Remainder
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicStore(pointer, scope, semantics, value) =>
          atomicStore(program, values, memory, pointer, scope, semantics, value) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicIAdd(resultType, dest, pointer, scope, semantics, value) =>
          atomicInteger(
            program,
            values,
            memory,
            resultType,
            dest,
            pointer,
            scope,
            semantics,
            value,
            AtomicIntegerOperation.Add
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicUMin(resultType, dest, pointer, scope, semantics, value) =>
          atomicInteger(
            program,
            values,
            memory,
            resultType,
            dest,
            pointer,
            scope,
            semantics,
            value,
            AtomicIntegerOperation.UnsignedMinimum
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FAdd(resultType, dest, left, right) =>
          floatingBinary(program, values, resultType, dest, left, right, FloatingOperation.Add) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FSub(resultType, dest, left, right) =>
          floatingBinary(program, values, resultType, dest, left, right, FloatingOperation.Subtract) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FMul(resultType, dest, left, right) =>
          floatingBinary(program, values, resultType, dest, left, right, FloatingOperation.Multiply) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FDiv(resultType, dest, left, right) =>
          floatingBinary(program, values, resultType, dest, left, right, FloatingOperation.Divide) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FNegate(resultType, dest, source) =>
          floatingNegate(program, values, resultType, dest, source) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.ShiftLeftLogical(resultType, dest, base, shift) =>
          integerShift(program, values, resultType, dest, base, shift, "OpShiftLeftLogical") {
            (value, amount, width) =>
              if (java.lang.Long.compareUnsigned(amount, width.toLong) >= 0) 0L
              else value << amount.toInt
          } match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.ShiftRightLogical(resultType, dest, base, shift) =>
          integerShift(program, values, resultType, dest, base, shift, "OpShiftRightLogical") {
            (value, amount, width) =>
              if (java.lang.Long.compareUnsigned(amount, width.toLong) >= 0) 0L
              else (value & mask(width)) >>> amount.toInt
          } match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.BitwiseOr(resultType, dest, left, right) =>
          integerBitwise(program, values, resultType, dest, left, right, "OpBitwiseOr")(_ | _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.BitwiseXor(resultType, dest, left, right) =>
          integerBitwise(program, values, resultType, dest, left, right, "OpBitwiseXor")(_ ^ _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.BitwiseAnd(resultType, dest, left, right) =>
          integerBitwise(program, values, resultType, dest, left, right, "OpBitwiseAnd")(_ & _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.Not(resultType, dest, source) =>
          integerNot(program, values, resultType, dest, source) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.IEqual(resultType, dest, left, right) =>
          integerEquality(program, values, resultType, dest, left, right, "OpIEqual")(_ == _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.INotEqual(resultType, dest, left, right) =>
          integerEquality(program, values, resultType, dest, left, right, "OpINotEqual")(_ != _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.LogicalEqual(resultType, dest, left, right) =>
          logicalBinary(program, values, resultType, dest, left, right, "OpLogicalEqual")(_ == _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.LogicalNotEqual(resultType, dest, left, right) =>
          logicalBinary(program, values, resultType, dest, left, right, "OpLogicalNotEqual")(_ != _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.LogicalOr(resultType, dest, left, right) =>
          logicalBinary(program, values, resultType, dest, left, right, "OpLogicalOr")(_ || _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.LogicalAnd(resultType, dest, left, right) =>
          logicalBinary(program, values, resultType, dest, left, right, "OpLogicalAnd")(_ && _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.LogicalNot(resultType, dest, source) =>
          logicalNot(program, values, resultType, dest, source) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FOrdEqual(resultType, dest, left, right) =>
          floatingCompare(program, values, resultType, dest, left, right, FloatingComparison.OrderedEqual) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FOrdLessThanEqual(resultType, dest, left, right) =>
          floatingCompare(
            program,
            values,
            resultType,
            dest,
            left,
            right,
            FloatingComparison.OrderedLessThanEqual
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FOrdLessThan(resultType, dest, left, right) =>
          floatingCompare(program, values, resultType, dest, left, right, FloatingComparison.OrderedLessThan) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FOrdGreaterThan(resultType, dest, left, right) =>
          floatingCompare(program, values, resultType, dest, left, right, FloatingComparison.OrderedGreaterThan) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.IsInf(resultType, dest, value) =>
          floatingClassify(program, values, resultType, dest, value, FloatingClassification.Infinite) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.IsNan(resultType, dest, value) =>
          floatingClassify(program, values, resultType, dest, value, FloatingClassification.NaN) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.SignBitSet(resultType, dest, value) =>
          floatingClassify(program, values, resultType, dest, value, FloatingClassification.Signed) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.ULessThan(resultType, dest, left, right) =>
          compare(values, resultType, dest, left, right)(java.lang.Long.compareUnsigned(_, _) < 0) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.UGreaterThan(resultType, dest, left, right) =>
          compare(values, resultType, dest, left, right)(java.lang.Long.compareUnsigned(_, _) > 0) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.ULessThanEqual(resultType, dest, left, right) =>
          compare(values, resultType, dest, left, right)(java.lang.Long.compareUnsigned(_, _) <= 0) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.UGreaterThanEqual(resultType, dest, left, right) =>
          compare(values, resultType, dest, left, right)(java.lang.Long.compareUnsigned(_, _) >= 0) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.SGreaterThan(resultType, dest, left, right) =>
          signedCompare(program, values, resultType, dest, left, right, "OpSGreaterThan")(_ > _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.SGreaterThanEqual(resultType, dest, left, right) =>
          signedCompare(program, values, resultType, dest, left, right, "OpSGreaterThanEqual")(_ >= _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.SLessThan(resultType, dest, left, right) =>
          signedCompare(program, values, resultType, dest, left, right, "OpSLessThan")(_ < _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.SLessThanEqual(resultType, dest, left, right) =>
          signedCompare(program, values, resultType, dest, left, right, "OpSLessThanEqual")(_ <= _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.Select(resultType, dest, condition, whenTrue, whenFalse) =>
          val trueId  = id(whenTrue)
          val falseId = id(whenFalse)
          if (values.scalarType(trueId) >= 0 && values.scalarType(falseId) >= 0)
            scalar(values, condition) match {
              case Left(error) => return Left(error)
              case Right(test) =>
                val selected = if (test != 0L) trueId else falseId
                values.updateScalar(id(dest), id(resultType), values.scalarBits(selected))
                pc += 1
            }
          else
            (scalar(values, condition), values.get(trueId), values.get(falseId)) match {
              case (Right(test), Some(yes), Some(no)) =>
                values.update(id(dest), retag(if (test != 0L) yes else no, id(resultType)))
                pc += 1
              case (Left(error), _, _) => return Left(error)
              case (_, None, _)        => return Left(undefined(whenTrue))
              case (_, _, None)        => return Left(undefined(whenFalse))
            }
        case Instruction.ControlBarrier(executionScope, memoryScope, semantics) =>
          controlBarrier(values, currentFunction.id, pc, executionScope, memoryScope, semantics) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.GroupAny(resultType, dest, executionScope, pred) =>
          collectiveAny(program, values, currentFunction.id, pc, resultType, dest, executionScope, pred) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.GroupUMin(resultType, dest, executionScope, operation, value) =>
          collectiveUnsignedMin(
            program,
            values,
            currentFunction.id,
            pc,
            resultType,
            dest,
            executionScope,
            operation,
            value
          ) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.GroupIAdd(resultType, dest, executionScope, operation, value) =>
          collectiveIntegerAdd(
            program,
            values,
            currentFunction.id,
            pc,
            resultType,
            dest,
            executionScope,
            operation,
            value
          ) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.GroupIMulKHR(resultType, dest, executionScope, operation, value) =>
          collectiveIntegerMultiply(
            program,
            values,
            currentFunction.id,
            pc,
            resultType,
            dest,
            executionScope,
            operation,
            value
          ) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.GroupFAdd(resultType, dest, executionScope, operation, value) =>
          collectiveFloatingAdd(
            program,
            values,
            currentFunction.id,
            pc,
            resultType,
            dest,
            executionScope,
            operation,
            value
          ) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.GroupBroadcast(resultType, dest, executionScope, value, localId) =>
          collectiveBroadcast(
            program,
            values,
            currentFunction.id,
            pc,
            resultType,
            dest,
            executionScope,
            value,
            localId
          ) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.FunctionCall(resultType, dest, called, arguments) =>
          val callee = program.functions.get(id(called)) match {
            case Some(value) => value
            case None        =>
              return Left(
                ExecutionFailure.InvalidRequest(s"OpFunctionCall references undefined function %${id(called)}")
              )
          }
          if (calls.size >= 128)
            return Left(ExecutionFailure.Failed("SPIR-V function call depth exceeds 128 frames"))
          if (arguments.size != callee.parameters.size)
            return Left(
              ExecutionFailure.InvalidRequest(
                s"OpFunctionCall %${id(called)} expects ${callee.parameters.size} arguments, received ${arguments.size}"
              )
            )
          var argumentIndex = 0
          while (argumentIndex < arguments.size) {
            values.get(id(arguments(argumentIndex))) match {
              case Some(value) =>
                val (parameterId, parameterType) = callee.parameters(argumentIndex)
                values.update(parameterId, retag(value, parameterType))
              case None => return Left(undefined(arguments(argumentIndex)))
            }
            argumentIndex += 1
          }
          calls.append(
            CallFrame(currentFunction, pc + 1, currentLabel, predecessor, id(resultType), id(dest))
          )
          currentFunction = callee
          pc = callee.start + 1
          currentLabel = -1
          predecessor = -1
        case Instruction.Branch(target) =>
          predecessor = currentLabel
          pc = label(currentFunction, target)
        case Instruction.BranchConditional(condition, whenTrue, whenFalse, _) =>
          scalar(values, condition) match {
            case Left(error) => return Left(error)
            case Right(test) =>
              predecessor = currentLabel
              pc = label(currentFunction, if (test != 0L) whenTrue else whenFalse)
          }
        case Instruction.ReturnValue(value) =>
          val returned = values.get(id(value)) match {
            case Some(result) => result
            case None         => return Left(undefined(value))
          }
          calls.removeLastOption() match {
            case None        => return Left(ExecutionFailure.InvalidRequest("SPIR-V entry point returned a value"))
            case Some(frame) =>
              values.update(frame.destination, retag(returned, frame.resultType))
              currentFunction = frame.function
              pc = frame.pc
              currentLabel = frame.currentLabel
              predecessor = frame.predecessor
          }
        case Instruction.Return() => calls.removeLastOption() match {
            case None =>
              execution.completed = true
              return Right(())
            case Some(frame) => program.types.get(frame.resultType) match {
                case Some(RuntimeType.Void) =>
                  currentFunction = frame.function
                  pc = frame.pc
                  currentLabel = frame.currentLabel
                  predecessor = frame.predecessor
                case _ =>
                  return Left(
                    ExecutionFailure.InvalidRequest(
                      s"SPIR-V function %${currentFunction.id} returned no value to %${frame.destination}"
                    )
                  )
              }
          }
        case Instruction.AssumeTrueKHR(_) | Instruction.LoopMerge(_, _, _) | Instruction.SelectionMerge(_, _) =>
          pc += 1
        case instruction =>
          return Left(ExecutionFailure.Unsupported(s"${instruction.opcode} execution is not implemented"))
      }
    }
    Left(
      ExecutionFailure.InvalidRequest(s"SPIR-V function %${currentFunction.id} reached OpFunctionEnd without OpReturn")
    )
  }

  private def suspend(
      execution: InvocationExecution,
      function: FunctionInfo,
      pc: Int,
      currentLabel: Int,
      predecessor: Int,
      instructionCount: Long,
      request: CollectiveRequest
  ): Unit = {
    execution.currentFunction = function
    execution.pc = pc
    execution.currentLabel = currentLabel
    execution.predecessor = predecessor
    execution.instructionCount = instructionCount
    execution.suspended = Some(request)
  }

  private def runWorkgroup(
      program: DispatchProgram,
      executions: mutable.ArrayBuffer[InvocationExecution],
      memory: MemoryState,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit] = {
    while (executions.exists(!_.completed)) {
      var index = 0
      while (index < executions.size) {
        val execution = executions(index)
        if (!execution.completed && execution.suspended.isEmpty)
          resumeInvocation(program, execution, memory, control) match {
            case Left(error) => return Left(error)
            case Right(())   => ()
          }
        index += 1
      }
      resolveCollectives(program, executions) match {
        case Left(error)                                 => return Left(error)
        case Right(0) if executions.exists(!_.completed) =>
          val waiting = executions.iterator
            .filter(!_.completed)
            .map(execution =>
              execution.suspended match {
                case Some(request) =>
                  s"local=${execution.invocation.local.mkString("(", ",", ")")}:" +
                    s"${request.kind}@%${request.function}:${request.pc}/scope=${request.scope}"
                case None => s"local=${execution.invocation.local.mkString("(", ",", ")")}:running"
              }
            )
            .take(8)
            .mkString(", ")
          return Left(
            ExecutionFailure.InvalidRequest(
              s"SPIR-V collective participation diverged within a workgroup: $waiting"
            )
          )
        case Right(_) => ()
      }
    }
    Right(())
  }

  private def resolveCollectives(
      program: DispatchProgram,
      executions: mutable.ArrayBuffer[InvocationExecution]
  ): Either[ExecutionFailure, Int] = {
    var resolved  = 0
    val subgroups = executions.iterator.map(_.invocation.subgroupIndex).toSet.toArray.sorted
    var index     = 0
    while (index < subgroups.length) {
      val subgroup     = subgroups(index)
      val participants = Col.from(executions.iterator.filter(_.invocation.subgroupIndex == subgroup))
      val requests     = participants.flatMap(_.suspended)
      if (
        requests.nonEmpty && requests.head.scope == 3 &&
        participants.forall(execution =>
          !execution.completed && execution.suspended.exists(sameCollective(_, requests.head))
        )
      )
        resolveCollective(program, participants, requests.head) match {
          case Left(error) => return Left(error)
          case Right(())   => resolved += 1
        }
      index += 1
    }
    val requests = executions.flatMap(_.suspended)
    requests.find(_.scope == 2) match {
      case Some(request)
          if executions.forall(execution =>
            !execution.completed && execution.suspended.exists(sameCollective(_, request))
          ) =>
        resolveCollective(program, Col.from(executions), request) match {
          case Left(error) => return Left(error)
          case Right(())   => resolved += 1
        }
      case _ => ()
    }
    Right(resolved)
  }

  private def sameCollective(left: CollectiveRequest, right: CollectiveRequest): Boolean =
    left.kind == right.kind && left.function == right.function && left.pc == right.pc &&
      left.scope == right.scope && left.operation == right.operation && left.selector == right.selector &&
      ((left.payload, right.payload) match {
        case (CollectivePayload.Value(leftType, _, _), CollectivePayload.Value(rightType, _, _)) =>
          leftType == rightType
        case (
              CollectivePayload.Barrier(leftMemoryScope, leftSemantics),
              CollectivePayload.Barrier(rightMemoryScope, rightSemantics)
            ) =>
          leftMemoryScope == rightMemoryScope && leftSemantics == rightSemantics
        case _ => false
      })

  private def resolveCollective(
      program: DispatchProgram,
      participants: Col[InvocationExecution],
      expected: CollectiveRequest
  ): Either[ExecutionFailure, Unit] = {
    val requests = participants.map(_.suspended.get)
    expected.payload match {
      case CollectivePayload.Barrier(_, _) =>
        participants.foreach { execution =>
          val request = execution.suspended.get
          execution.pc = request.pc + 1
          execution.suspended = None
        }
        Right(())
      case CollectivePayload.Value(resultType, _, _) =>
        val requestValues = requests.collect {
          case CollectiveRequest(_, _, _, _, CollectivePayload.Value(_, _, value), _, _) =>
            value
        }
        if (requestValues.size != requests.size)
          Left(ExecutionFailure.InvalidRequest("collective participants have incompatible payloads"))
        else {
          val results = expected.kind match {
            case CollectiveKind.Any =>
              val any = requestValues.exists {
                case RuntimeValue.Bits(_, Array(value)) => value != 0L
                case _                                  => false
              }
              Right(Col.fill(participants.size)(RuntimeValue.Bits(resultType, Array(if (any) 1L else 0L))))
            case CollectiveKind.UnsignedMin =>
              val inputs = requestValues.collect { case RuntimeValue.Bits(_, lanes) => lanes }
              if (inputs.size != requests.size || inputs.isEmpty)
                Left(ExecutionFailure.InvalidRequest("OpGroupUMin participants do not all provide integer values"))
              else {
                val width = integerShape(program, resultType).map(_._1).getOrElse(64)
                val lanes = Array.tabulate(inputs.head.length) { lane =>
                  inputs.iterator.map(_(lane) & mask(width)).reduce((left, right) =>
                    if (java.lang.Long.compareUnsigned(left, right) <= 0) left else right
                  )
                }
                Right(Col.fill(participants.size)(RuntimeValue.Bits(resultType, lanes)))
              }
            case CollectiveKind.IntegerAdd =>
              val inputs = requestValues.collect { case RuntimeValue.Bits(_, lanes) => lanes }
              if (inputs.size != requests.size || inputs.isEmpty)
                Left(ExecutionFailure.InvalidRequest("OpGroupIAdd participants do not all provide integer values"))
              else {
                val width = integerShape(program, resultType).map(_._1).getOrElse(64)
                if (expected.operation == 0) {
                  val reduced = Array.tabulate(inputs.head.length) { lane =>
                    inputs.iterator.foldLeft(0L)((sum, input) => (sum + input(lane)) & mask(width))
                  }
                  Right(Col.fill(participants.size)(RuntimeValue.Bits(resultType, reduced)))
                } else {
                  val accumulated = Array.fill(inputs.head.length)(0L)
                  val outputs     = inputs.map { input =>
                    val before = accumulated.clone()
                    var lane   = 0
                    while (lane < accumulated.length) {
                      accumulated(lane) = (accumulated(lane) + input(lane)) & mask(width)
                      lane += 1
                    }
                    RuntimeValue.Bits(
                      resultType,
                      if (expected.operation == 1) accumulated.clone() else before
                    )
                  }
                  Right(outputs)
                }
              }
            case CollectiveKind.IntegerMultiply =>
              val inputs = requestValues.collect { case RuntimeValue.Bits(_, lanes) => lanes }
              if (inputs.size != requests.size || inputs.isEmpty)
                Left(ExecutionFailure.InvalidRequest("OpGroupIMulKHR participants do not all provide integer values"))
              else {
                val width = integerShape(program, resultType).map(_._1).getOrElse(64)
                if (expected.operation == 0) {
                  val reduced = Array.tabulate(inputs.head.length) { lane =>
                    inputs.iterator.foldLeft(1L)((product, input) => (product * input(lane)) & mask(width))
                  }
                  Right(Col.fill(participants.size)(RuntimeValue.Bits(resultType, reduced)))
                } else {
                  val accumulated = Array.fill(inputs.head.length)(1L)
                  val outputs     = inputs.map { input =>
                    val before = accumulated.clone()
                    var lane   = 0
                    while (lane < accumulated.length) {
                      accumulated(lane) = (accumulated(lane) * input(lane)) & mask(width)
                      lane += 1
                    }
                    RuntimeValue.Bits(
                      resultType,
                      if (expected.operation == 1) accumulated.clone() else before
                    )
                  }
                  Right(outputs)
                }
              }
            case CollectiveKind.FloatingAdd =>
              val inputs = requestValues.collect { case RuntimeValue.Bits(_, lanes) => lanes }
              if (inputs.size != requests.size || inputs.isEmpty)
                Left(ExecutionFailure.InvalidRequest("OpGroupFAdd participants do not all provide floating values"))
              else {
                val width = floatingShape(program, resultType).map(_._1).getOrElse(64)
                if (expected.operation == 0) {
                  val reduced = Array.tabulate(inputs.head.length) { lane =>
                    inputs.iterator.foldLeft(0L)((sum, input) => floatingAddBits(width, sum, input(lane)))
                  }
                  Right(Col.fill(participants.size)(RuntimeValue.Bits(resultType, reduced)))
                } else {
                  val accumulated = Array.fill(inputs.head.length)(0L)
                  val outputs     = inputs.map { input =>
                    val before = accumulated.clone()
                    var lane   = 0
                    while (lane < accumulated.length) {
                      accumulated(lane) = floatingAddBits(width, accumulated(lane), input(lane))
                      lane += 1
                    }
                    RuntimeValue.Bits(
                      resultType,
                      if (expected.operation == 1) accumulated.clone() else before
                    )
                  }
                  Right(outputs)
                }
              }
            case CollectiveKind.Broadcast =>
              val selected = expected.selector match {
                case Col(x)       => participants.indexWhere(_.invocation.localX == x)
                case Col(x, y, z) => participants.indexWhere(execution =>
                    execution.invocation.localX == x && execution.invocation.localY == y &&
                      execution.invocation.localZ == z
                  )
                case _ => -1
              }
              if (selected < 0)
                Left(
                  ExecutionFailure.InvalidRequest(
                    s"OpGroupBroadcast local id ${expected.selector.mkString("(", ",", ")")} is outside the workgroup"
                  )
                )
              else Right(Col.fill(participants.size)(requestValues(selected)))
            case CollectiveKind.Barrier =>
              Left(ExecutionFailure.InvalidRequest("barrier rendezvous unexpectedly carried a value"))
          }
          results.map { values =>
            participants.zip(values).foreach { case (execution, value) =>
              val request = execution.suspended.get
              request.payload match {
                case CollectivePayload.Value(requestType, destination, _) =>
                  execution.values.update(destination, retag(value, requestType))
                case CollectivePayload.Barrier(_, _) => ()
              }
              execution.pc = request.pc + 1
              execution.suspended = None
            }
          }
        }
    }
  }

  private def controlBarrier(
      values: InvocationValues,
      function: Int,
      pc: Int,
      executionScope: Operand,
      memoryScope: Operand,
      semantics: Operand
  ): Either[ExecutionFailure, CollectiveRequest] =
    (scalar(values, executionScope), scalar(values, memoryScope), scalar(values, semantics)) match {
      case (Right(2L), Right(2L), Right(memorySemantics))
          if (memorySemantics & ~0xffdeL) == 0L &&
            Col(0L, 2L, 4L, 8L, 16L).contains(memorySemantics & 0x1eL) =>
        Right(
          CollectiveRequest(
            CollectiveKind.Barrier,
            function,
            pc,
            2,
            CollectivePayload.Barrier(2, memorySemantics)
          )
        )
      case (Right(scope), _, _) if scope != 2L =>
        Left(ExecutionFailure.Unsupported(s"OpControlBarrier execution scope $scope is not Workgroup"))
      case (Right(_), Right(scope), _) if scope != 2L =>
        Left(ExecutionFailure.Unsupported(s"OpControlBarrier memory scope $scope is not Workgroup"))
      case (Right(_), Right(_), Right(memorySemantics)) =>
        Left(
          ExecutionFailure.InvalidRequest(
            f"OpControlBarrier memory semantics 0x$memorySemantics%x are invalid or unsupported"
          )
        )
      case (Left(error), _, _) => Left(error)
      case (_, Left(error), _) => Left(error)
      case (_, _, Left(error)) => Left(error)
    }

  private def collectiveAny(
      program: DispatchProgram,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      pred: Operand
  ): Either[ExecutionFailure, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(pred))) match {
      case (Right(scope @ (2L | 3L)), Some(value @ RuntimeValue.Bits(predType, Array(_))))
          if booleanLanes(program, id(resultType)).contains(1) && booleanLanes(program, predType).contains(1) =>
        Right(
          CollectiveRequest(
            CollectiveKind.Any,
            function,
            pc,
            scope.toInt,
            CollectivePayload.Value(id(resultType), id(destination), value)
          )
        )
      case (Right(scope), _) if scope != 2L && scope != 3L =>
        Left(ExecutionFailure.Unsupported(s"OpGroupAny execution scope $scope is not Subgroup or Workgroup"))
      case (Right(_), Some(_)) =>
        Left(ExecutionFailure.InvalidRequest("OpGroupAny requires scalar boolean predicate and result types"))
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(pred))
    }

  private def collectiveUnsignedMin(
      program: DispatchProgram,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      operation: Operand,
      source: Operand
  ): Either[ExecutionFailure, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(source))) match {
      case (Right(2L), Some(value @ RuntimeValue.Bits(sourceType, lanes)))
          if unsigned(operation) == 0L && integerShape(program, id(resultType)) == integerShape(program, sourceType) &&
            integerShape(program, sourceType).exists(_._2 == lanes.length) =>
        Right(
          CollectiveRequest(
            CollectiveKind.UnsignedMin,
            function,
            pc,
            2,
            CollectivePayload.Value(id(resultType), id(destination), value)
          )
        )
      case (Right(_), _) if unsigned(operation) != 0L =>
        Left(ExecutionFailure.Unsupported(s"OpGroupUMin group operation ${unsigned(operation)} is not Reduce"))
      case (Right(scope), _) if scope != 2L =>
        Left(ExecutionFailure.Unsupported(s"OpGroupUMin execution scope $scope is not Workgroup"))
      case (Right(_), Some(_)) =>
        Left(ExecutionFailure.InvalidRequest("OpGroupUMin requires matching integer scalar or vector types"))
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(source))
    }

  private def collectiveIntegerAdd(
      program: DispatchProgram,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      operation: Operand,
      source: Operand
  ): Either[ExecutionFailure, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(source))) match {
      case (Right(2L), Some(value @ RuntimeValue.Bits(sourceType, lanes)))
          if Col(0L, 1L, 2L).contains(unsigned(operation)) &&
            integerShape(program, id(resultType)) == integerShape(program, sourceType) &&
            integerShape(program, sourceType).exists(_._2 == lanes.length) =>
        Right(
          CollectiveRequest(
            CollectiveKind.IntegerAdd,
            function,
            pc,
            2,
            CollectivePayload.Value(id(resultType), id(destination), value),
            unsigned(operation).toInt
          )
        )
      case (Right(_), _) if !Col(0L, 1L, 2L).contains(unsigned(operation)) =>
        Left(
          ExecutionFailure.Unsupported(
            s"OpGroupIAdd group operation ${unsigned(operation)} is not Reduce, InclusiveScan, or ExclusiveScan"
          )
        )
      case (Right(scope), _) if scope != 2L =>
        Left(ExecutionFailure.Unsupported(s"OpGroupIAdd execution scope $scope is not Workgroup"))
      case (Right(_), Some(_)) =>
        Left(ExecutionFailure.InvalidRequest("OpGroupIAdd requires matching integer scalar or vector types"))
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(source))
    }

  private def collectiveIntegerMultiply(
      program: DispatchProgram,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      operation: Operand,
      source: Operand
  ): Either[ExecutionFailure, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(source))) match {
      case (Right(2L), Some(value @ RuntimeValue.Bits(sourceType, lanes)))
          if Col(0L, 1L, 2L).contains(unsigned(operation)) &&
            integerShape(program, id(resultType)) == integerShape(program, sourceType) &&
            integerShape(program, sourceType).exists(_._2 == lanes.length) =>
        Right(
          CollectiveRequest(
            CollectiveKind.IntegerMultiply,
            function,
            pc,
            2,
            CollectivePayload.Value(id(resultType), id(destination), value),
            unsigned(operation).toInt
          )
        )
      case (Right(_), _) if !Col(0L, 1L, 2L).contains(unsigned(operation)) =>
        Left(
          ExecutionFailure.Unsupported(
            s"OpGroupIMulKHR group operation ${unsigned(operation)} is not Reduce, InclusiveScan, or ExclusiveScan"
          )
        )
      case (Right(scope), _) if scope != 2L =>
        Left(ExecutionFailure.Unsupported(s"OpGroupIMulKHR execution scope $scope is not Workgroup"))
      case (Right(_), Some(_)) =>
        Left(ExecutionFailure.InvalidRequest("OpGroupIMulKHR requires matching integer scalar or vector types"))
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(source))
    }

  private def collectiveFloatingAdd(
      program: DispatchProgram,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      operation: Operand,
      source: Operand
  ): Either[ExecutionFailure, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(source))) match {
      case (Right(2L), Some(value @ RuntimeValue.Bits(sourceType, lanes)))
          if Col(0L, 1L, 2L).contains(unsigned(operation)) &&
            floatingShape(program, id(resultType)) == floatingShape(program, sourceType) &&
            floatingShape(program, sourceType).exists(_._2 == lanes.length) =>
        Right(
          CollectiveRequest(
            CollectiveKind.FloatingAdd,
            function,
            pc,
            2,
            CollectivePayload.Value(id(resultType), id(destination), value),
            unsigned(operation).toInt
          )
        )
      case (Right(_), _) if !Col(0L, 1L, 2L).contains(unsigned(operation)) =>
        Left(
          ExecutionFailure.Unsupported(
            s"OpGroupFAdd group operation ${unsigned(operation)} is not Reduce, InclusiveScan, or ExclusiveScan"
          )
        )
      case (Right(scope), _) if scope != 2L =>
        Left(ExecutionFailure.Unsupported(s"OpGroupFAdd execution scope $scope is not Workgroup"))
      case (Right(_), Some(_)) =>
        Left(ExecutionFailure.InvalidRequest("OpGroupFAdd requires matching floating scalar or vector types"))
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(source))
    }

  private def collectiveBroadcast(
      program: DispatchProgram,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      source: Operand,
      localId: Operand
  ): Either[ExecutionFailure, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(source)), values.get(id(localId))) match {
      case (
            Right(2L),
            Some(value @ RuntimeValue.Bits(sourceType, _)),
            Some(RuntimeValue.Bits(localIdType, lanes))
          )
          if sourceType == id(resultType) && integerShape(program, localIdType).exists { case (_, count) =>
            count == lanes.length && Col(1, 3).contains(count)
          } =>
        val width = integerShape(program, localIdType).map(_._1).getOrElse(64)
        Right(
          CollectiveRequest(
            CollectiveKind.Broadcast,
            function,
            pc,
            2,
            CollectivePayload.Value(id(resultType), id(destination), value),
            selector = Col.from(lanes.map(_ & mask(width)))
          )
        )
      case (Right(scope), _, _) if scope != 2L =>
        Left(ExecutionFailure.Unsupported(s"OpGroupBroadcast execution scope $scope is not Workgroup"))
      case (Right(_), None, _) => Left(undefined(source))
      case (Right(_), _, None) => Left(undefined(localId))
      case (Right(_), _, _)    =>
        Left(
          ExecutionFailure.InvalidRequest(
            "OpGroupBroadcast requires matching result/source bit types and a scalar or three-lane integer local id"
          )
        )
      case (Left(error), _, _) => Left(error)
    }

  private def bindArgument(
      program: DispatchProgram,
      parameterType: Int,
      argument: KernelArgument,
      shared: Option[Region]
  ): Either[ExecutionFailure, RuntimeValue] = program.types.get(parameterType) match {
    case Some(RuntimeType.Pointer(storageClass, pointee)) => argument match {
        case KernelArgument.Allocation(allocation, offset) if storageClass == 7 =>
          bindAggregatePointerArgument(program, parameterType, pointee, allocation, offset)
        case KernelArgument.Allocation(allocation, offset) =>
          Right(
            RuntimeValue.Address(
              parameterType,
              Pointer(Region.External(allocation), offset, pointee, storageClass)
            )
          )
        case KernelArgument.Scalar(bytes) =>
          val size   = math.max(typeSize(program, pointee), bytes.size)
          val buffer = Array.ofDim[Byte](size)
          val _      = bytes.copyToArray(buffer)
          Right(RuntimeValue.Address(parameterType, Pointer(Region.Internal(buffer), 0L, pointee, storageClass)))
        case KernelArgument.Local(size) =>
          val region = shared.getOrElse(Region.Internal(Array.ofDim[Byte](checkedSize(size))))
          Right(RuntimeValue.Address(parameterType, Pointer(region, 0L, pointee, storageClass)))
      }
    case Some(_) => argument match {
        case KernelArgument.Scalar(bytes) => Right(decode(program, parameterType, bytes.toArray))
        case value => Left(ExecutionFailure.InvalidRequest(s"scalar SPIR-V parameter cannot bind $value"))
      }
    case None => Left(ExecutionFailure.InvalidRequest(s"SPIR-V parameter type %$parameterType is undefined"))
  }

  private def bindAggregatePointerArgument(
      program: DispatchProgram,
      parameterType: Int,
      pointee: Int,
      allocation: AllocationId,
      allocationOffset: Long
  ): Either[ExecutionFailure, RuntimeValue] = {
    val leaves = pointerLeafOffsets(program, pointee, 0L)
    leaves match {
      case Left(error)                           => Left(error)
      case Right(Col((slotOffset, pointerType))) => program.types.get(pointerType) match {
          case Some(RuntimeType.Pointer(storageClass, leafPointee)) =>
            val slots = mutable.LongMap.empty[(Int, Pointer)]
            slots.update(
              slotOffset,
              pointerType -> Pointer(Region.External(allocation), allocationOffset, leafPointee, storageClass)
            )
            Right(
              RuntimeValue.Address(
                parameterType,
                Pointer(Region.Internal(Array.ofDim[Byte](typeSize(program, pointee))), 0L, pointee, 7, Some(slots))
              )
            )
          case value =>
            Left(
              ExecutionFailure.InvalidRequest(
                s"SPIR-V aggregate pointer slot type %$pointerType is not a pointer: $value"
              )
            )
        }
      case Right(leaves) =>
        Left(
          ExecutionFailure.InvalidRequest(
            s"Function-storage aggregate argument type %$pointee has ${leaves.size} pointer slots, expected exactly one"
          )
        )
    }
  }

  private def pointerLeafOffsets(
      program: DispatchProgram,
      typeId: Int,
      baseOffset: Long
  ): Either[ExecutionFailure, Col[(Long, Int)]] = program.types.get(typeId) match {
    case Some(RuntimeType.Pointer(_, _))      => Right(Col(baseOffset -> typeId))
    case Some(RuntimeType.Structure(members)) =>
      members.zipWithIndex.foldLeft[Either[ExecutionFailure, Col[(Long, Int)]]](Right(Col.empty)) {
        case (result, (member, index)) =>
          result.flatMap(existing =>
            pointerLeafOffsets(program, member, baseOffset + structMemberOffset(program, typeId, members, index)).map(
              existing ++ _
            )
          )
      }
    case Some(RuntimeType.Array(element, count)) =>
      if (count < 0L || count > Int.MaxValue.toLong)
        Left(ExecutionFailure.InvalidRequest(s"SPIR-V aggregate pointer array type %$typeId has invalid size $count"))
      else
        (0 until count.toInt).foldLeft[Either[ExecutionFailure, Col[(Long, Int)]]](Right(Col.empty)) {
          case (result, index) =>
            result.flatMap(existing =>
              pointerLeafOffsets(program, element, baseOffset + index.toLong * typeSize(program, element)).map(
                existing ++ _
              )
            )
        }
    case Some(_) => Right(Col.empty)
    case None    => Left(ExecutionFailure.InvalidRequest(s"SPIR-V aggregate member type %$typeId is undefined"))
  }

  private def loadPointer(
      program: DispatchProgram,
      source: Pointer,
      resultType: Int
  ): Either[ExecutionFailure, RuntimeValue] = program.types.get(resultType) match {
    case Some(RuntimeType.Pointer(resultStorage, resultPointee)) => source.slots.flatMap(_.get(source.offset)) match {
        case Some((slotType, pointer)) => program.types.get(slotType) match {
            case Some(RuntimeType.Pointer(slotStorage, slotPointee))
                if slotType == resultType && slotStorage == resultStorage && slotPointee == resultPointee &&
                  pointer.pointee == resultPointee &&
                  (pointer.region == Region.Null || pointer.storageClass == resultStorage) =>
              Right(RuntimeValue.Address(resultType, pointer))
            case value =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"SPIR-V pointer slot at byte ${source.offset} has type %$slotType ($value), expected %$resultType"
                )
              )
          }
        case None =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"SPIR-V pointer load at byte ${source.offset} does not reference a typed pointer slot"
            )
          )
      }
    case value =>
      Left(ExecutionFailure.InvalidRequest(s"SPIR-V pointer load result type %$resultType is invalid: $value"))
  }

  private def storePointer(
      program: DispatchProgram,
      target: Pointer,
      sourceType: Int,
      source: Pointer
  ): Either[ExecutionFailure, Unit] =
    (program.types.get(target.pointee), program.types.get(sourceType), target.slots) match {
      case (
            Some(RuntimeType.Pointer(targetStorage, targetPointee)),
            Some(RuntimeType.Pointer(sourceStorage, sourcePointee)),
            Some(slots)
          )
          if target.pointee == sourceType && targetStorage == sourceStorage && targetPointee == sourcePointee &&
            source.pointee == sourcePointee &&
            (source.region == Region.Null || source.storageClass == sourceStorage) =>
        slots.update(target.offset, sourceType -> source)
        Right(())
      case (Some(RuntimeType.Pointer(_, _)), Some(RuntimeType.Pointer(_, _)), None) =>
        Left(
          ExecutionFailure.InvalidRequest(
            s"SPIR-V pointer store at byte ${target.offset} does not reference a typed pointer slot"
          )
        )
      case (targetType, sourceValue, _) =>
        Left(
          ExecutionFailure.InvalidRequest(
            s"SPIR-V pointer store type mismatch: target pointee %${target.pointee} is $targetType, " +
              s"source %$sourceType is $sourceValue"
          )
        )
    }

  private def materializeGlobals(
      program: DispatchProgram,
      includeStorageClass: Int => Boolean
  ): Either[ExecutionFailure, Map[Int, RuntimeValue]] = {
    val values  = Map.newBuilder[Int, RuntimeValue]
    val globals = program.globals.iterator
    while (globals.hasNext) {
      val (variable, global) = globals.next()
      if (!program.builtins.contains(variable) && includeStorageClass(global.storageClass)) {
        val pointee = pointerPointee(program, global.pointerType)
        val bytes   = global.initializer match {
          case None              => Array.ofDim[Byte](typeSize(program, pointee))
          case Some(initializer) => program.constants.get(initializer) match {
              case None =>
                return Left(
                  ExecutionFailure.InvalidRequest(
                    s"SPIR-V global variable %$variable has undefined initializer %$initializer"
                  )
                )
              case Some(value) =>
                val encoded = encode(program, value)
                if (encoded.length != typeSize(program, pointee))
                  return Left(
                    ExecutionFailure.InvalidRequest(
                      s"SPIR-V global variable %$variable initializer has ${encoded.length} bytes, " +
                        s"expected ${typeSize(program, pointee)}"
                    )
                  )
                encoded
            }
        }
        values += variable -> RuntimeValue.Address(
          global.pointerType,
          Pointer(Region.Internal(bytes), 0L, pointee, global.storageClass)
        )
      }
    }
    Right(values.result())
  }

  private def accessChain(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      base: Operand,
      element: Option[Operand],
      indexes: Col[Operand]
  ): Either[ExecutionFailure, Unit] = {
    val source = address(values, base) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    val initial = element match {
      case Some(value) =>
        scalar(values, value).map(index => (source.offset + index * typeSize(program, source.pointee), source.pointee))
      case None => Right((source.offset, source.pointee))
    }
    val (baseOffset, baseType) = initial match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    var offset   = baseOffset
    var typeId   = baseType
    var position = 0
    while (position < indexes.size) {
      val index = scalar(values, indexes(position)) match {
        case Left(error)  => return Left(error)
        case Right(value) => value
      }
      program.types.get(typeId) match {
        case Some(RuntimeType.Structure(members)) =>
          if (index < 0L || index >= members.size.toLong)
            return Left(ExecutionFailure.InvalidRequest(s"structure member index $index is out of range"))
          offset += structMemberOffset(program, typeId, members, index.toInt)
          typeId = members(index.toInt)
        case Some(RuntimeType.Array(member, _)) =>
          offset += index * program.arrayStrides.getOrElse(typeId, typeSize(program, member))
          typeId = member
        case Some(RuntimeType.VectorType(component, _)) =>
          offset += index * typeSize(program, component)
          typeId = component
        case Some(value) => return Left(ExecutionFailure.InvalidRequest(s"cannot index through SPIR-V type $value"))
        case None        => return Left(ExecutionFailure.InvalidRequest(s"SPIR-V type %$typeId is undefined"))
      }
      position += 1
    }
    val outputType = id(resultType)
    val pointee    = pointerPointee(program, outputType)
    if (source.region == Region.Null)
      Left(ExecutionFailure.InvalidRequest("cannot form an access chain from a null SPIR-V pointer"))
    else {
      values.update(
        id(destination),
        RuntimeValue.Address(outputType, Pointer(source.region, offset, pointee, source.storageClass, source.slots))
      )
      Right(())
    }
  }

  private def genericCastToPointer(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = {
    val outputType = id(resultType)
    (program.types.get(outputType), values.get(id(source))) match {
      case (
            Some(RuntimeType.Pointer(targetStorage, targetPointee)),
            Some(RuntimeValue.Address(sourceType, pointer))
          ) if Col(4, 5, 7).contains(targetStorage) =>
        program.types.get(sourceType) match {
          case Some(RuntimeType.Pointer(8, sourcePointee))
              if sourcePointee == targetPointee && pointer.pointee == targetPointee =>
            val converted = pointer.region match {
              case Region.Null                                => Pointer(Region.Null, 0L, targetPointee, targetStorage)
              case _ if pointer.storageClass == targetStorage => pointer
              case _                                          => Pointer(Region.Null, 0L, targetPointee, targetStorage)
            }
            values.update(id(destination), RuntimeValue.Address(outputType, converted))
            Right(())
          case Some(RuntimeType.Pointer(sourceStorage, sourcePointee)) =>
            Left(
              ExecutionFailure.InvalidRequest(
                s"OpGenericCastToPtr cannot cast storage class $sourceStorage pointee %$sourcePointee " +
                  s"to storage class $targetStorage pointee %$targetPointee"
              )
            )
          case _ => Left(ExecutionFailure.InvalidRequest("OpGenericCastToPtr source type is not a pointer"))
        }
      case (Some(RuntimeType.Pointer(targetStorage, _)), _) =>
        Left(
          ExecutionFailure.InvalidRequest(
            s"OpGenericCastToPtr result pointer has invalid storage class $targetStorage"
          )
        )
      case (None, _) =>
        Left(ExecutionFailure.InvalidRequest(s"OpGenericCastToPtr result type %$outputType is undefined"))
      case (_, None) => Left(undefined(source))
      case _         => Left(ExecutionFailure.InvalidRequest("OpGenericCastToPtr source is not a pointer"))
    }
  }

  private def extendedInstruction(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      set: Operand,
      instruction: Operand,
      sources: Col[Operand]
  ): Either[ExecutionFailure, Unit] = {
    val setId  = id(set)
    val opcode = unsigned(instruction).toInt
    program.extendedInstructionSets.get(setId) match {
      case None => Left(ExecutionFailure.InvalidRequest(s"OpExtInst references undefined import %$setId"))
      case Some("OpenCL.std") if opcode == 13 =>
        if (sources.size != 2)
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std copysign expects 2 operands, received ${sources.size}"))
        else
          (values.get(id(sources(0))), values.get(id(sources(1)))) match {
            case (Some(RuntimeValue.Bits(leftType, left)), Some(RuntimeValue.Bits(rightType, right))) =>
              (
                floatingShape(program, leftType),
                floatingShape(program, rightType),
                floatingShape(program, id(resultType))
              ) match {
                case (Some(leftShape @ (width, lanes)), Some(rightShape), Some(resultShape))
                    if leftShape == rightShape && leftShape == resultShape =>
                  val signMask = width match {
                    case 16 => 0x8000L
                    case 32 => 0x80000000L
                    case 64 => 0x8000000000000000L
                  }
                  values.update(
                    id(destination),
                    RuntimeValue.Bits(
                      id(resultType),
                      Array.tabulate(lanes)(index => (left(index) & ~signMask) | (right(index) & signMask))
                    )
                  )
                  Right(())
                case _ =>
                  Left(
                    ExecutionFailure.InvalidRequest(
                      "OpenCL.std copysign requires matching floating-point scalar/vector operand and result types"
                    )
                  )
              }
            case (None, _) => Left(undefined(sources(0)))
            case (_, None) => Left(undefined(sources(1)))
            case _ => Left(ExecutionFailure.InvalidRequest("OpenCL.std copysign operands are not floating-point"))
          }
      case Some("OpenCL.std") if Set(14, 23, 37, 57, 61).contains(opcode) =>
        val name = opcode match {
          case 14 => "cos"
          case 23 => "fabs"
          case 37 => "log"
          case 57 => "sin"
          case 61 => "sqrt"
        }
        if (sources.size != 1)
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std $name expects 1 operand, received ${sources.size}"))
        else
          values.get(id(sources.head)) match {
            case Some(RuntimeValue.Bits(sourceType, lanes)) =>
              (floatingShape(program, sourceType), floatingShape(program, id(resultType))) match {
                case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes)))
                    if sourceWidth == resultWidth && sourceLanes == resultLanes =>
                  val result = lanes.map { raw =>
                    def evaluate(value: Double): Double = opcode match {
                      case 14 => Math.cos(value)
                      case 23 => Math.abs(value)
                      case 37 => Math.log(value)
                      case 57 => Math.sin(value)
                      case 61 => Math.sqrt(value)
                    }
                    resultWidth match {
                      case 16 if opcode == 23 => raw & 0x7fffL
                      case 32 if opcode == 23 => raw & 0x7fffffffL
                      case 64 if opcode == 23 => raw & 0x7fffffffffffffffL
                      case 16 => IEEE754.floatToBinary16(evaluate(IEEE754.binary16ToFloat(raw).toDouble).toFloat)
                      case 32 =>
                        IEEE754.floatToBinary32(evaluate(IEEE754.binary32ToFloat(raw).toDouble).toFloat)
                      case 64 => IEEE754.doubleToBinary64(evaluate(IEEE754.binary64ToDouble(raw)))
                    }
                  }
                  values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
                  Right(())
                case _ =>
                  Left(
                    ExecutionFailure.InvalidRequest(
                      s"OpenCL.std $name requires matching floating-point scalar/vector operand and result types"
                    )
                  )
              }
            case Some(_) => Left(ExecutionFailure.InvalidRequest(s"OpenCL.std $name operand is not floating-point"))
            case None    => Left(undefined(sources.head))
          }
      case Some("OpenCL.std") if opcode == 7 || opcode == 32 =>
        val name = if (opcode == 7) "atan2" else "hypot"
        if (sources.size != 2)
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std $name expects 2 operands, received ${sources.size}"))
        else if (floatingShape(program, id(resultType)) != Some((64, 1)))
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std $name currently requires a scalar f64 result"))
        else
          (values.get(id(sources(0))), values.get(id(sources(1)))) match {
            case (Some(RuntimeValue.Bits(leftType, Array(left))), Some(RuntimeValue.Bits(rightType, Array(right))))
                if floatingShape(program, leftType) == Some((64, 1)) &&
                  floatingShape(program, rightType) == Some((64, 1)) =>
              val leftValue  = IEEE754.binary64ToDouble(left)
              val rightValue = IEEE754.binary64ToDouble(right)
              val result     = if (opcode == 7) Math.atan2(leftValue, rightValue) else Math.hypot(leftValue, rightValue)
              values.update(
                id(destination),
                RuntimeValue.Bits(id(resultType), Array(IEEE754.doubleToBinary64(result)))
              )
              Right(())
            case (None, _) => Left(undefined(sources(0)))
            case (_, None) => Left(undefined(sources(1)))
            case _         =>
              Left(ExecutionFailure.InvalidRequest(s"OpenCL.std $name currently requires two scalar f64 operands"))
          }
      case Some(name) =>
        Left(ExecutionFailure.Unsupported(s"OpExtInst $name opcode $opcode execution is not implemented"))
    }
  }

  private def binary(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand
  )(operation: (Long, Long) => Long): Either[ExecutionFailure, Unit] = {
    val leftId  = id(left)
    val rightId = id(right)
    if (values.scalarType(leftId) >= 0 && values.scalarType(rightId) >= 0) {
      val width = scalarWidth(program, id(resultType))
      values.updateScalar(
        id(destination),
        id(resultType),
        operation(values.scalarBits(leftId), values.scalarBits(rightId)) & mask(width)
      )
      Right(())
    } else
      (bits(values, left), bits(values, right)) match {
        case (Right(a), Right(b)) =>
          val width = scalarWidth(program, id(resultType))
          val size  = math.max(a.length, b.length)
          val lanes = Array.tabulate(size)(index => operation(a(index % a.length), b(index % b.length)) & mask(width))
          values.update(id(destination), RuntimeValue.Bits(id(resultType), lanes))
          Right(())
        case (Left(error), _) => Left(error)
        case (_, Left(error)) => Left(error)
      }
  }

  private def integerShift(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      base: Operand,
      shift: Operand,
      opcode: String
  )(operation: (Long, Long, Int) => Long): Either[ExecutionFailure, Unit] =
    (values.get(id(base)), values.get(id(shift))) match {
      case (
            Some(RuntimeValue.Bits(baseType, baseLanes)),
            Some(RuntimeValue.Bits(shiftType, shiftLanes))
          ) =>
        val resultShape = integerShape(program, id(resultType))
        val baseShape   = integerShape(program, baseType)
        val shiftShape  = integerShape(program, shiftType)
        (resultShape, baseShape, shiftShape) match {
          case (Some((width, count)), Some(baseDescriptor), Some((shiftWidth, shiftCount)))
              if baseDescriptor == (width, count) && shiftCount == count && baseLanes.length == count &&
                shiftLanes.length == count =>
            val lanes = Array.tabulate(count) { lane =>
              val amount = shiftLanes(lane) & mask(shiftWidth)
              operation(baseLanes(lane), amount, width) & mask(width)
            }
            values.update(id(destination), RuntimeValue.Bits(id(resultType), lanes))
            Right(())
          case _ =>
            Left(
              ExecutionFailure.InvalidRequest(
                s"$opcode requires an integer scalar/vector result and base of the same type, " +
                  "plus an integer shift operand with the same lane count"
              )
            )
        }
      case (None, _) => Left(undefined(base))
      case (_, None) => Left(undefined(shift))
      case _         =>
        Left(
          ExecutionFailure.InvalidRequest(
            s"$opcode requires integer scalar or vector operands"
          )
        )
    }

  private def compare(
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand
  )(predicate: (Long, Long) => Boolean): Either[ExecutionFailure, Unit] = {
    val leftId  = id(left)
    val rightId = id(right)
    if (values.scalarType(leftId) >= 0 && values.scalarType(rightId) >= 0) {
      values.updateScalar(
        id(destination),
        id(resultType),
        if (predicate(values.scalarBits(leftId), values.scalarBits(rightId))) 1L else 0L
      )
      Right(())
    } else
      (bits(values, left), bits(values, right)) match {
        case (Right(a), Right(b)) =>
          val size  = math.max(a.length, b.length)
          val lanes =
            Array.tabulate(size)(index => if (predicate(a(index % a.length), b(index % b.length))) 1L else 0L)
          values.update(id(destination), RuntimeValue.Bits(id(resultType), lanes))
          Right(())
        case (Left(error), _) => Left(error)
        case (_, Left(error)) => Left(error)
      }
  }

  private def signedCompare(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      opcode: String
  )(predicate: (Long, Long) => Boolean): Either[ExecutionFailure, Unit] = {
    val leftId    = id(left)
    val rightId   = id(right)
    val leftType  = values.scalarType(leftId)
    val rightType = values.scalarType(rightId)
    (
      Option.when(leftType >= 0)(leftType).flatMap(integerShape(program, _)),
      Option.when(rightType >= 0)(rightType).flatMap(integerShape(program, _)),
      booleanLanes(program, id(resultType))
    ) match {
      case (Some((width, 1)), Some((rightWidth, 1)), Some(1)) if width == rightWidth =>
        val result =
          predicate(signExtend(values.scalarBits(leftId), width), signExtend(values.scalarBits(rightId), width))
        values.updateScalar(id(destination), id(resultType), if (result) 1L else 0L)
        Right(())
      case _ =>
        (values.get(leftId), values.get(rightId)) match {
          case (
                Some(RuntimeValue.Bits(leftRuntimeType, leftLanes)),
                Some(RuntimeValue.Bits(rightRuntimeType, rightLanes))
              ) =>
            (
              integerShape(program, leftRuntimeType),
              integerShape(program, rightRuntimeType),
              booleanLanes(program, id(resultType))
            ) match {
              case (Some((width, count)), Some((rightWidth, rightCount)), Some(resultCount))
                  if width == rightWidth && count == rightCount && count == resultCount &&
                    leftLanes.length == count && rightLanes.length == count =>
                val result = Array.tabulate(count)(index =>
                  if (predicate(signExtend(leftLanes(index), width), signExtend(rightLanes(index), width))) 1L else 0L
                )
                values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
                Right(())
              case (None, _, _) =>
                Left(
                  ExecutionFailure.InvalidRequest(
                    s"$opcode left operand %$leftId is not an integer scalar or vector"
                  )
                )
              case (_, None, _) =>
                Left(
                  ExecutionFailure.InvalidRequest(
                    s"$opcode right operand %$rightId is not an integer scalar or vector"
                  )
                )
              case (_, _, None) =>
                Left(
                  ExecutionFailure.InvalidRequest(
                    s"$opcode result type %${id(resultType)} is not a boolean scalar or vector"
                  )
                )
              case (Some(leftShape), Some(rightShape), Some(resultLanes)) =>
                Left(
                  ExecutionFailure.InvalidRequest(
                    s"$opcode type shapes do not match: left $leftShape, right $rightShape, result lanes $resultLanes"
                  )
                )
            }
          case (Some(RuntimeValue.Address(_, _)), _) =>
            Left(ExecutionFailure.InvalidRequest(s"$opcode left operand %$leftId is a pointer"))
          case (_, Some(RuntimeValue.Address(_, _))) =>
            Left(ExecutionFailure.InvalidRequest(s"$opcode right operand %$rightId is a pointer"))
          case (None, _) => Left(undefined(left))
          case (_, None) => Left(undefined(right))
        }
    }
  }

  private def builtinValue(
      program: DispatchProgram,
      typeId: Int,
      builtin: Int,
      invocation: Invocation
  ): Either[ExecutionFailure, RuntimeValue] = {
    val lanes: Either[ExecutionFailure, Array[Long]] = builtin match {
      case 25    => Right(invocation.workgroupSize)
      case 26    => Right(invocation.group)
      case 27    => Right(invocation.local)
      case 28    => Right(invocation.global)
      case 31    => Right(invocation.globalSize)
      case 33    => Right(Array(0L, 0L, 0L))
      case 36    => Right(Array(invocation.subgroupSize.toLong))
      case 38    => Right(Array(invocation.numSubgroups))
      case 40    => Right(Array(invocation.subgroupIndex.toLong))
      case 41    => Right(Array(invocation.subgroupLocal.toLong))
      case value => Left(ExecutionFailure.Unsupported(s"SPIR-V BuiltIn $value is not implemented"))
    }
    lanes.flatMap(values =>
      integerShape(program, typeId) match {
        case Some((width, count)) if count == values.length =>
          Right(RuntimeValue.Bits(typeId, values.map(_ & mask(width))))
        case Some((_, count)) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"SPIR-V BuiltIn $builtin requires ${values.length} integer lanes, result type %$typeId has $count"
            )
          )
        case None =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"SPIR-V BuiltIn $builtin result type %$typeId is not an integer scalar or vector"
            )
          )
      }
    )
  }

  private def decode(program: DispatchProgram, typeId: Int, bytes: Array[Byte]): RuntimeValue = {
    val width = scalarWidth(program, typeId)
    val count = laneCount(program, typeId)
    val step  = width / 8
    val lanes = Array.tabulate(count) { lane =>
      val offset    = Math.min(lane * step, bytes.length)
      val available = Math.min(step, bytes.length - offset)
      LittleEndian.decode(bytes, offset, available) & mask(width)
    }
    RuntimeValue.Bits(typeId, lanes)
  }

  private def encode(program: DispatchProgram, value: RuntimeValue): Array[Byte] = value match {
    case RuntimeValue.Bits(typeId, lanes) =>
      val bytes = Array.ofDim[Byte](typeSize(program, typeId))
      val used  = encodeBits(program, typeId, lanes, 0, bytes, 0)
      if (used != lanes.length)
        throw IllegalArgumentException(
          s"SPIR-V value of type %$typeId has ${lanes.length} scalar leaves, expected $used"
        )
      bytes
    case RuntimeValue.Address(_, pointer) =>
      throw IllegalArgumentException(s"cannot encode runtime pointer $pointer as process-neutral bytes")
  }

  private def encodeBits(
      program: DispatchProgram,
      typeId: Int,
      lanes: Array[Long],
      laneStart: Int,
      output: Array[Byte],
      byteStart: Int
  ): Int = program.types.get(typeId) match {
    case Some(RuntimeType.Bool) | Some(RuntimeType.Integer(_, _)) | Some(RuntimeType.Floating(_)) =>
      if (laneStart >= lanes.length)
        throw IllegalArgumentException(s"SPIR-V value of type %$typeId has too few scalar leaves")
      val width = scalarWidth(program, typeId)
      LittleEndian.write(lanes(laneStart), output, byteStart, width / 8)
      laneStart + 1
    case Some(RuntimeType.VectorType(component, count)) =>
      var lane  = laneStart
      var index = 0
      while (index < count) {
        lane = encodeBits(program, component, lanes, lane, output, byteStart + index * typeSize(program, component))
        index += 1
      }
      lane
    case Some(RuntimeType.Array(element, count)) =>
      val stride = program.arrayStrides.getOrElse(typeId, typeSize(program, element))
      var lane   = laneStart
      var index  = 0L
      while (index < count) {
        lane = encodeBits(
          program,
          element,
          lanes,
          lane,
          output,
          byteStart + Math.toIntExact(index * stride.toLong)
        )
        index += 1L
      }
      lane
    case Some(RuntimeType.Structure(members)) =>
      var lane  = laneStart
      var index = 0
      while (index < members.size) {
        lane = encodeBits(
          program,
          members(index),
          lanes,
          lane,
          output,
          byteStart + structMemberOffset(program, typeId, members, index)
        )
        index += 1
      }
      lane
    case value => throw IllegalArgumentException(s"SPIR-V type %$typeId cannot be encoded as data: $value")
  }

  private def typeSize(program: DispatchProgram, typeId: Int): Int = program.types.get(typeId) match {
    case Some(RuntimeType.Bool)                     => 1
    case Some(RuntimeType.Integer(width, _))        => width / 8
    case Some(RuntimeType.Floating(width))          => width / 8
    case Some(RuntimeType.VectorType(component, n)) => Math.multiplyExact(typeSize(program, component), n)
    case Some(RuntimeType.Array(element, n))        =>
      Math.toIntExact(Math.multiplyExact(program.arrayStrides.getOrElse(typeId, typeSize(program, element)).toLong, n))
    case Some(RuntimeType.Structure(members)) => structSize(program, typeId, members)
    case Some(RuntimeType.Pointer(_, _))      => 8
    case Some(RuntimeType.Void)               => 0
    case Some(RuntimeType.Function(_, _))     => 0
    case None                                 => throw IllegalArgumentException(s"SPIR-V type %$typeId is undefined")
  }

  private def structSize(program: DispatchProgram, structureType: Int, members: Col[Int]): Int = {
    var offset = 0
    var align  = 1
    var index  = 0
    while (index < members.size) {
      val memberAlign = typeAlignment(program, members(index))
      offset = program.memberOffsets.getOrElse(structureType -> index, alignUp(offset, memberAlign))
      offset = Math.addExact(offset, typeSize(program, members(index)))
      align = math.max(align, memberAlign)
      index += 1
    }
    alignUp(offset, align)
  }

  private def structMemberOffset(
      program: DispatchProgram,
      structureType: Int,
      members: Col[Int],
      selected: Int
  ): Int =
    program.memberOffsets.get(structureType -> selected).getOrElse {
      var offset = 0
      var index  = 0
      while (index < selected) {
        offset = alignUp(offset, typeAlignment(program, members(index)))
        offset = Math.addExact(offset, typeSize(program, members(index)))
        index += 1
      }
      alignUp(offset, typeAlignment(program, members(selected)))
    }

  private def typeAlignment(program: DispatchProgram, typeId: Int): Int = program.types.get(typeId) match {
    case Some(RuntimeType.Structure(members))           => members.map(typeAlignment(program, _)).maxOption.getOrElse(1)
    case Some(RuntimeType.Array(element, _))            => typeAlignment(program, element)
    case Some(RuntimeType.VectorType(component, count)) =>
      math.min(16, typeAlignment(program, component) * (if (count == 3) 4 else count))
    case Some(RuntimeType.Pointer(_, _)) => 8
    case _                               => math.max(1, typeSize(program, typeId))
  }

  private def scalarWidth(program: DispatchProgram, typeId: Int): Int = program.types.get(typeId) match {
    case Some(RuntimeType.Bool)                     => 8
    case Some(RuntimeType.Integer(width, _))        => width
    case Some(RuntimeType.Floating(width))          => width
    case Some(RuntimeType.VectorType(component, _)) => scalarWidth(program, component)
    case value => throw IllegalArgumentException(s"SPIR-V type %$typeId is not scalar or vector: $value")
  }

  private def laneCount(program: DispatchProgram, typeId: Int): Int = program.types.get(typeId) match {
    case Some(RuntimeType.VectorType(_, count)) => count
    case _                                      => 1
  }

  private def pointerPointee(program: DispatchProgram, typeId: Int): Int = program.types.get(typeId) match {
    case Some(RuntimeType.Pointer(_, pointee)) => pointee
    case value => throw IllegalArgumentException(s"SPIR-V type %$typeId is not a pointer: $value")
  }

  private def address(values: InvocationValues, operand: Operand): Either[ExecutionFailure, Pointer] =
    values.get(id(operand)) match {
      case Some(RuntimeValue.Address(_, value)) => Right(value)
      case Some(_) => Left(ExecutionFailure.InvalidRequest(s"SPIR-V value %${id(operand)} is not a pointer"))
      case None    => Left(undefined(operand))
    }

  /** Lifetime instructions are semantic markers, but accepting a malformed marker would hide a corrupt module. Resolve
    * the object identity and validate the literal byte range without reading external allocation contents.
    */
  private def validateLifetime(
      program: DispatchProgram,
      values: InvocationValues,
      opcode: String,
      pointerOperand: Operand,
      sizeOperand: Operand
  ): Either[ExecutionFailure, Unit] =
    (pointerOperand.words, sizeOperand.words) match {
      case (Col(pointerId), Col(sizeWord)) => values.get(pointerId.bits) match {
          case Some(RuntimeValue.Address(pointerType, pointer)) => program.types.get(pointerType) match {
              case Some(RuntimeType.Pointer(_, pointee)) if pointee == pointer.pointee =>
                val size = sizeWord.toUnsigned
                pointer.region match {
                  case Region.Internal(bytes) if size != 0L =>
                    val end = pointer.offset + size
                    Either.cond(
                      pointer.offset >= 0L && end >= pointer.offset && end <= bytes.length.toLong,
                      (),
                      ExecutionFailure.InvalidRequest(
                        s"$opcode byte range ${pointer.offset} + $size exceeds internal object of ${bytes.length} bytes"
                      )
                    )
                  case _ => Right(())
                }
              case Some(_) =>
                Left(ExecutionFailure.InvalidRequest(s"$opcode target %${pointerId.bits} does not have pointer type"))
              case None =>
                Left(ExecutionFailure.InvalidRequest(s"$opcode target %${pointerId.bits} has undefined pointer type"))
            }
          case Some(_) => Left(ExecutionFailure.InvalidRequest(s"$opcode target %${pointerId.bits} is not a pointer"))
          case None    => Left(ExecutionFailure.InvalidRequest(s"$opcode target %${pointerId.bits} is undefined"))
        }
      case (pointerWords, _) if pointerWords.size != 1 =>
        Left(ExecutionFailure.InvalidRequest(s"$opcode pointer must contain one word, found ${pointerWords.size}"))
      case (_, sizeWords) =>
        Left(ExecutionFailure.InvalidRequest(s"$opcode size must contain one word, found ${sizeWords.size}"))
    }

  private def bits(values: InvocationValues, operand: Operand): Either[ExecutionFailure, Array[Long]] =
    values.get(id(operand)) match {
      case Some(RuntimeValue.Bits(_, value)) => Right(value)
      case Some(_) => Left(ExecutionFailure.InvalidRequest(s"SPIR-V value %${id(operand)} is not numeric"))
      case None    => Left(undefined(operand))
    }

  private def scalar(values: InvocationValues, operand: Operand): Either[ExecutionFailure, Long] =
    if (values.scalarType(id(operand)) >= 0) Right(values.scalarBits(id(operand)))
    else
      bits(values, operand).flatMap {
        case Array(value) => Right(value)
        case value => Left(ExecutionFailure.InvalidRequest(s"SPIR-V value %${id(operand)} has ${value.length} lanes"))
      }

  private def scalarBits(value: RuntimeValue): Option[Long] = value match {
    case RuntimeValue.Bits(_, Array(result)) => Some(result)
    case _                                   => None
  }

  private def retag(value: RuntimeValue, typeId: Int): RuntimeValue = value match {
    case RuntimeValue.Bits(_, lanes)      => RuntimeValue.Bits(typeId, lanes.clone())
    case RuntimeValue.Address(_, pointer) => RuntimeValue.Address(typeId, pointer)
  }

  private def phiIncoming(
      incoming: Col[Operand],
      predecessor: Int
  ): Either[ExecutionFailure, Option[Int]] = {
    var selected = -1
    var index    = 0
    while (index < incoming.size) {
      val pair = incoming(index)
      pair.words match {
        case Col(value, parent) => if (parent.bits == predecessor) selected = value.bits
        case words              =>
          return Left(
            ExecutionFailure.InvalidRequest(
              s"OpPhi ${pair.kind} operand must contain a value and parent label, found ${words.size} words"
            )
          )
      }
      index += 1
    }
    Right(if (selected < 0) None else Some(selected))
  }

  private def convertSignedToFloat(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (integerShape(program, sourceType), floatingShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes))) if sourceLanes == resultLanes =>
          val converted = lanes.map { raw =>
            val signed = signExtend(raw, sourceWidth)
            resultWidth match {
              case 16 => IEEE754.floatToBinary16(signed.toFloat)
              case 32 => IEEE754.floatToBinary32(signed.toFloat)
              case 64 => IEEE754.doubleToBinary64(signed.toDouble)
            }
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), converted))
          Right(())
        case (None, _) =>
          Left(
            ExecutionFailure.InvalidRequest(s"OpConvertSToF source %${id(source)} is not an integer scalar or vector")
          )
        case (_, None) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpConvertSToF result type %${id(resultType)} is not a floating-point scalar or vector"
            )
          )
        case (Some((_, sourceLanes)), Some((_, resultLanes))) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpConvertSToF cannot convert $sourceLanes source lanes to $resultLanes result lanes"
            )
          )
      }
    case Some(_) => Left(ExecutionFailure.InvalidRequest(s"OpConvertSToF source %${id(source)} is a pointer"))
    case None    => Left(undefined(source))
  }

  private def signedConvert(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (integerShape(program, sourceType), integerShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes))) if sourceLanes == resultLanes =>
          values.update(
            id(destination),
            RuntimeValue.Bits(id(resultType), lanes.map(value => signExtend(value, sourceWidth) & mask(resultWidth)))
          )
          Right(())
        case (None, _) =>
          Left(ExecutionFailure.InvalidRequest(s"OpSConvert source %${id(source)} is not an integer scalar or vector"))
        case (_, None) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpSConvert result type %${id(resultType)} is not an integer scalar or vector"
            )
          )
        case (Some((_, sourceLanes)), Some((_, resultLanes))) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpSConvert cannot convert $sourceLanes source lanes to $resultLanes result lanes"
            )
          )
      }
    case Some(_) => Left(ExecutionFailure.InvalidRequest(s"OpSConvert source %${id(source)} is a pointer"))
    case None    => Left(undefined(source))
  }

  private def floatingClassify(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand,
      classification: FloatingClassification
  ): Either[ExecutionFailure, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (floatingShape(program, sourceType), booleanLanes(program, id(resultType))) match {
        case (Some((width, sourceLanes)), Some(resultLanes)) if sourceLanes == resultLanes =>
          val result = lanes.map { raw =>
            val (signMask, exponentMask, fractionMask) = width match {
              case 16 => (0x8000L, 0x7c00L, 0x03ffL)
              case 32 => (0x80000000L, 0x7f800000L, 0x007fffffL)
              case 64 => (0x8000000000000000L, 0x7ff0000000000000L, 0x000fffffffffffffL)
            }
            val exponentSet = (raw & exponentMask) == exponentMask
            val fractionSet = (raw & fractionMask) != 0L
            val matches     = classification match {
              case FloatingClassification.Infinite => exponentSet && !fractionSet
              case FloatingClassification.NaN      => exponentSet && fractionSet
              case FloatingClassification.Signed   => (raw & signMask) != 0L
            }
            if (matches) 1L else 0L
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case (None, _) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${classification.opcode} source %${id(source)} is not a floating-point scalar or vector"
            )
          )
        case (_, None) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${classification.opcode} result type %${id(resultType)} is not a boolean scalar or vector"
            )
          )
        case (Some((_, sourceLanes)), Some(resultLanes)) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${classification.opcode} cannot classify $sourceLanes lanes into $resultLanes lanes"
            )
          )
      }
    case Some(_) => Left(ExecutionFailure.InvalidRequest(s"${classification.opcode} source is a pointer"))
    case None    => Left(undefined(source))
  }

  private def floatingNegate(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (floatingShape(program, sourceType), floatingShape(program, id(resultType))) match {
        case (Some(sourceShape @ (width, _)), Some(resultShape)) if sourceShape == resultShape =>
          val signMask = width match {
            case 16 => 0x8000L
            case 32 => 0x80000000L
            case 64 => 0x8000000000000000L
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), lanes.map(_ ^ signMask)))
          Right(())
        case _ =>
          Left(
            ExecutionFailure.InvalidRequest(
              "OpFNegate requires matching floating-point scalar/vector operand and result types"
            )
          )
      }
    case Some(_) => Left(ExecutionFailure.InvalidRequest("OpFNegate source is a pointer"))
    case None    => Left(undefined(source))
  }

  private def floatingBinary(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      operation: FloatingOperation
  ): Either[ExecutionFailure, Unit] = (values.get(id(left)), values.get(id(right))) match {
    case (Some(RuntimeValue.Bits(leftType, leftLanes)), Some(RuntimeValue.Bits(rightType, rightLanes))) =>
      (
        floatingShape(program, id(resultType)),
        floatingShape(program, leftType),
        floatingShape(program, rightType)
      ) match {
        case (Some((width, count)), Some((leftWidth, leftCount)), Some((rightWidth, rightCount)))
            if width == leftWidth && width == rightWidth && count == leftCount && count == rightCount =>
          val result = Array.tabulate(count) { index =>
            width match {
              case 16 =>
                val a = IEEE754.binary16ToFloat(leftLanes(index))
                val b = IEEE754.binary16ToFloat(rightLanes(index))
                IEEE754.floatToBinary16(
                  operation match {
                    case FloatingOperation.Add      => a + b
                    case FloatingOperation.Subtract => a - b
                    case FloatingOperation.Multiply => a * b
                    case FloatingOperation.Divide   => a / b
                  }
                )
              case 32 =>
                val a = IEEE754.binary32ToFloat(leftLanes(index))
                val b = IEEE754.binary32ToFloat(rightLanes(index))
                IEEE754.floatToBinary32(
                  operation match {
                    case FloatingOperation.Add      => a + b
                    case FloatingOperation.Subtract => a - b
                    case FloatingOperation.Multiply => a * b
                    case FloatingOperation.Divide   => a / b
                  }
                )
              case 64 =>
                val a = IEEE754.binary64ToDouble(leftLanes(index))
                val b = IEEE754.binary64ToDouble(rightLanes(index))
                IEEE754.doubleToBinary64(
                  operation match {
                    case FloatingOperation.Add      => a + b
                    case FloatingOperation.Subtract => a - b
                    case FloatingOperation.Multiply => a * b
                    case FloatingOperation.Divide   => a / b
                  }
                )
            }
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case (None, _, _) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${operation.opcode} result type %${id(resultType)} is not a floating-point scalar or vector"
            )
          )
        case (_, None, _) =>
          Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} left operand %${id(left)} is not floating-point"))
        case (_, _, None) =>
          Left(
            ExecutionFailure.InvalidRequest(s"${operation.opcode} right operand %${id(right)} is not floating-point")
          )
        case (Some(result), Some(leftShape), Some(rightShape)) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${operation.opcode} type shapes do not match: result $result, left $leftShape, right $rightShape"
            )
          )
      }
    case (Some(RuntimeValue.Address(_, _)), _) =>
      Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} left operand %${id(left)} is a pointer"))
    case (_, Some(RuntimeValue.Address(_, _))) =>
      Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} right operand %${id(right)} is a pointer"))
    case (None, _) => Left(undefined(left))
    case (_, None) => Left(undefined(right))
  }

  private def convertFloatToSigned(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (floatingShape(program, sourceType), integerShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes))) if sourceLanes == resultLanes =>
          val converted = lanes.map { raw =>
            val signed = sourceWidth match {
              case 16 => IEEE754.binary16ToFloat(raw).toLong
              case 32 => IEEE754.binary32ToFloat(raw).toLong
              case 64 => IEEE754.binary64ToDouble(raw).toLong
            }
            signed & mask(resultWidth)
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), converted))
          Right(())
        case (None, _) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpConvertFToS source %${id(source)} is not a floating-point scalar or vector"
            )
          )
        case (_, None) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpConvertFToS result type %${id(resultType)} is not an integer scalar or vector"
            )
          )
        case (Some((_, sourceLanes)), Some((_, resultLanes))) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpConvertFToS cannot convert $sourceLanes source lanes to $resultLanes result lanes"
            )
          )
      }
    case Some(_) => Left(ExecutionFailure.InvalidRequest(s"OpConvertFToS source %${id(source)} is a pointer"))
    case None    => Left(undefined(source))
  }

  private def convertFloatToUnsigned(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (floatingShape(program, sourceType), integerShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes))) if sourceLanes == resultLanes =>
          val converted = Array.ofDim[Long](lanes.length)
          var index     = 0
          while (index < lanes.length) {
            floatingToUnsigned(lanes(index), sourceWidth, resultWidth) match {
              case Left(detail) =>
                return Left(
                  ExecutionFailure.InvalidRequest(
                    s"OpConvertFToU source lane $index cannot be represented as an unsigned $resultWidth-bit integer: $detail"
                  )
                )
              case Right(value) => converted(index) = value
            }
            index += 1
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), converted))
          Right(())
        case (None, _) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpConvertFToU source %${id(source)} is not a floating-point scalar or vector"
            )
          )
        case (_, None) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpConvertFToU result type %${id(resultType)} is not an integer scalar or vector"
            )
          )
        case (Some((_, sourceLanes)), Some((_, resultLanes))) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpConvertFToU cannot convert $sourceLanes source lanes to $resultLanes result lanes"
            )
          )
      }
    case Some(_) => Left(ExecutionFailure.InvalidRequest(s"OpConvertFToU source %${id(source)} is a pointer"))
    case None    => Left(undefined(source))
  }

  /** Converts an IEEE-754 bit pattern by truncating toward zero without first narrowing through a signed Long. */
  private def floatingToUnsigned(raw: Long, sourceWidth: Int, resultWidth: Int): Either[String, Long] = {
    val (exponentBits, fractionBits, bias) = sourceWidth match {
      case 16 => (5, 10, 15)
      case 32 => (8, 23, 127)
      case 64 => (11, 52, 1023)
    }
    val bits            = raw & mask(sourceWidth)
    val fractionMask    = (1L << fractionBits) - 1L
    val exponentMask    = (1L << exponentBits) - 1L
    val fraction        = bits & fractionMask
    val encodedExponent = (bits >>> fractionBits) & exponentMask
    val negative        = ((bits >>> (sourceWidth - 1)) & 1L) != 0L
    val magnitudeIsZero = encodedExponent == 0L && fraction == 0L

    if (encodedExponent == exponentMask) Left("NaN and infinity are not integers")
    else if (negative && !magnitudeIsZero) Left("negative values are outside the unsigned range")
    else if (encodedExponent == 0L) Right(0L)
    else {
      val exponent    = encodedExponent.toInt - bias
      val significand = BigInt((1L << fractionBits) | fraction)
      val integer     =
        if (exponent >= fractionBits) significand << (exponent - fractionBits)
        else significand >> (fractionBits - exponent)
      if (integer.bitLength > resultWidth) Left("value exceeds the result width")
      else Right(integer.longValue & mask(resultWidth))
    }
  }

  private def integerEquality(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      opcode: String
  )(predicate: (Long, Long) => Boolean): Either[ExecutionFailure, Unit] = {
    val leftId    = id(left)
    val rightId   = id(right)
    val leftType  = values.scalarType(leftId)
    val rightType = values.scalarType(rightId)
    (
      Option.when(leftType >= 0)(leftType).flatMap(integerShape(program, _)),
      Option.when(rightType >= 0)(rightType).flatMap(integerShape(program, _)),
      booleanLanes(program, id(resultType))
    ) match {
      case (Some(leftShape @ (_, 1)), Some(rightShape), Some(1)) if leftShape == rightShape =>
        values.updateScalar(
          id(destination),
          id(resultType),
          if (predicate(values.scalarBits(leftId), values.scalarBits(rightId))) 1L else 0L
        )
        Right(())
      case _ => (values.get(leftId), values.get(rightId)) match {
          case (
                Some(RuntimeValue.Bits(leftRuntimeType, leftLanes)),
                Some(RuntimeValue.Bits(rightRuntimeType, rightLanes))
              ) =>
            (
              integerShape(program, leftRuntimeType),
              integerShape(program, rightRuntimeType),
              booleanLanes(program, id(resultType))
            ) match {
              case (Some(leftShape), Some(rightShape), Some(resultLanes))
                  if leftShape == rightShape && leftShape._2 == resultLanes &&
                    leftLanes.length == resultLanes && rightLanes.length == resultLanes =>
                values.update(
                  id(destination),
                  RuntimeValue.Bits(
                    id(resultType),
                    Array.tabulate(resultLanes)(index => if (predicate(leftLanes(index), rightLanes(index))) 1L else 0L)
                  )
                )
                Right(())
              case (None, _, _) =>
                Left(
                  ExecutionFailure.InvalidRequest(
                    s"$opcode left operand %$leftId is not an integer scalar or vector"
                  )
                )
              case (_, None, _) =>
                Left(
                  ExecutionFailure.InvalidRequest(
                    s"$opcode right operand %$rightId is not an integer scalar or vector"
                  )
                )
              case (_, _, None) =>
                Left(
                  ExecutionFailure.InvalidRequest(
                    s"$opcode result type %${id(resultType)} is not a boolean scalar or vector"
                  )
                )
              case (Some(leftShape), Some(rightShape), Some(resultLanes)) =>
                Left(
                  ExecutionFailure.InvalidRequest(
                    s"$opcode type shapes do not match: left $leftShape, right $rightShape, result lanes $resultLanes"
                  )
                )
            }
          case (Some(RuntimeValue.Address(_, _)), _) =>
            Left(ExecutionFailure.InvalidRequest(s"$opcode left operand %$leftId is a pointer"))
          case (_, Some(RuntimeValue.Address(_, _))) =>
            Left(ExecutionFailure.InvalidRequest(s"$opcode right operand %$rightId is a pointer"))
          case (None, _) => Left(undefined(left))
          case (_, None) => Left(undefined(right))
        }
    }
  }

  private def integerBitwise(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      opcode: String
  )(operation: (Long, Long) => Long): Either[ExecutionFailure, Unit] = {
    val resultId = id(resultType)
    val leftId   = id(left)
    val rightId  = id(right)
    integerShape(program, resultId) match {
      case None =>
        Left(ExecutionFailure.InvalidRequest(s"$opcode result type %$resultId is not an integer scalar or vector"))
      case Some((width, count)) =>
        val leftType  = values.scalarType(leftId)
        val rightType = values.scalarType(rightId)
        if (count == 1 && leftType == resultId && rightType == resultId) {
          values.updateScalar(
            id(destination),
            resultId,
            operation(values.scalarBits(leftId), values.scalarBits(rightId)) & mask(width)
          )
          Right(())
        } else
          (values.get(leftId), values.get(rightId)) match {
            case (
                  Some(RuntimeValue.Bits(leftRuntimeType, leftLanes)),
                  Some(RuntimeValue.Bits(rightRuntimeType, rightLanes))
                )
                if leftRuntimeType == resultId && rightRuntimeType == resultId &&
                  leftLanes.length == count && rightLanes.length == count =>
              values.update(
                id(destination),
                RuntimeValue.Bits(
                  resultId,
                  Array.tabulate(count)(index => operation(leftLanes(index), rightLanes(index)) & mask(width))
                )
              )
              Right(())
            case (None, _) => Left(undefined(left))
            case (_, None) => Left(undefined(right))
            case _         =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"$opcode requires matching integer scalar or vector operand and result types"
                )
              )
          }
    }
  }

  private def integerNot(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = {
    val resultId = id(resultType)
    val sourceId = id(source)
    integerShape(program, resultId) match {
      case None =>
        Left(ExecutionFailure.InvalidRequest(s"OpNot result type %$resultId is not an integer scalar or vector"))
      case Some((width, count)) if count == 1 && values.scalarType(sourceId) == resultId =>
        values.updateScalar(id(destination), resultId, ~values.scalarBits(sourceId) & mask(width))
        Right(())
      case Some((width, count)) =>
        values.get(sourceId) match {
          case Some(RuntimeValue.Bits(runtimeType, lanes)) if runtimeType == resultId && lanes.length == count =>
            values.update(id(destination), RuntimeValue.Bits(resultId, lanes.map(value => ~value & mask(width))))
            Right(())
          case Some(_) =>
            Left(
              ExecutionFailure.InvalidRequest(
                "OpNot requires matching integer scalar or vector operand and result types"
              )
            )
          case None => Left(undefined(source))
        }
    }
  }

  private def logicalBinary(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      opcode: String
  )(predicate: (Boolean, Boolean) => Boolean): Either[ExecutionFailure, Unit] = {
    val leftId    = id(left)
    val rightId   = id(right)
    val leftType  = values.scalarType(leftId)
    val rightType = values.scalarType(rightId)
    if (
      leftType >= 0 && rightType >= 0 && booleanLanes(program, leftType).contains(1) &&
      booleanLanes(program, rightType).contains(1) && booleanLanes(program, id(resultType)).contains(1)
    ) {
      val result = predicate(values.scalarBits(leftId) != 0L, values.scalarBits(rightId) != 0L)
      values.updateScalar(id(destination), id(resultType), if (result) 1L else 0L)
      Right(())
    } else
      (values.get(leftId), values.get(rightId)) match {
        case (
              Some(RuntimeValue.Bits(leftRuntimeType, leftLanes)),
              Some(RuntimeValue.Bits(rightRuntimeType, rightLanes))
            ) =>
          (
            booleanLanes(program, leftRuntimeType),
            booleanLanes(program, rightRuntimeType),
            booleanLanes(program, id(resultType))
          ) match {
            case (Some(leftCount), Some(rightCount), Some(resultCount))
                if leftCount == rightCount && leftCount == resultCount &&
                  leftLanes.length == leftCount && rightLanes.length == rightCount =>
              values.update(
                id(destination),
                RuntimeValue.Bits(
                  id(resultType),
                  Array.tabulate(resultCount)(index =>
                    if (predicate(leftLanes(index) != 0L, rightLanes(index) != 0L)) 1L else 0L
                  )
                )
              )
              Right(())
            case _ =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"$opcode requires matching boolean scalar or vector operand and result types"
                )
              )
          }
        case (Some(_), Some(_)) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"$opcode requires matching boolean scalar or vector operand and result types"
            )
          )
        case (None, _) => Left(undefined(left))
        case (_, None) => Left(undefined(right))
      }
  }

  private def logicalNot(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = {
    val sourceId   = id(source)
    val sourceType = values.scalarType(sourceId)
    if (
      sourceType >= 0 && booleanLanes(program, sourceType).contains(1) &&
      booleanLanes(program, id(resultType)).contains(1)
    ) {
      values.updateScalar(id(destination), id(resultType), if (values.scalarBits(sourceId) == 0L) 1L else 0L)
      Right(())
    } else
      values.get(sourceId) match {
        case Some(RuntimeValue.Bits(runtimeType, lanes))
            if booleanLanes(program, runtimeType).contains(lanes.length) &&
              booleanLanes(program, id(resultType)).contains(lanes.length) =>
          values.update(
            id(destination),
            RuntimeValue.Bits(id(resultType), lanes.map(value => if (value == 0L) 1L else 0L))
          )
          Right(())
        case Some(_) =>
          Left(ExecutionFailure.InvalidRequest("OpLogicalNot requires matching boolean operand and result types"))
        case None => Left(undefined(source))
      }
  }

  private enum SignedIntegerOperation(val opcode: String) {
    case Divide    extends SignedIntegerOperation("OpSDiv")
    case Remainder extends SignedIntegerOperation("OpSRem")
  }

  private enum AtomicIntegerOperation(val opcode: String) {
    case Add             extends AtomicIntegerOperation("OpAtomicIAdd")
    case UnsignedMinimum extends AtomicIntegerOperation("OpAtomicUMin")
  }

  private def atomicStore(
      program: DispatchProgram,
      values: InvocationValues,
      memory: MemoryState,
      pointer: Operand,
      scope: Operand,
      semantics: Operand,
      value: Operand
  ): Either[ExecutionFailure, Unit] = {
    val opcode    = "OpAtomicStore"
    val valueId   = id(value)
    val valueType = values.scalarType(valueId)
    val shape     = Option.when(valueType >= 0)(valueType).flatMap(integerShape(program, _)) match {
      case Some(result @ (width, 1)) if width == 32 || width == 64 => result
      case _                                                       =>
        return Left(
          ExecutionFailure.InvalidRequest(s"$opcode requires a 32- or 64-bit scalar integer value")
        )
    }
    val target = address(values, pointer) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    if (target.pointee != valueType)
      return Left(
        ExecutionFailure.InvalidRequest(
          s"$opcode pointer pointee %${target.pointee} does not match value type %$valueType"
        )
      )
    if (!Col(4, 5, 8, 10, 11, 12, 5349).contains(target.storageClass))
      return Left(
        ExecutionFailure.InvalidRequest(
          s"$opcode pointer storage class ${target.storageClass} does not support atomic access"
        )
      )
    validateAtomicControls(values, scope, semantics, opcode).flatMap { _ =>
      val width = shape._1
      memory.writeScalar(target, values.scalarBits(valueId) & mask(width), width / 8)
    }
  }

  private def validateAtomicControls(
      values: InvocationValues,
      scope: Operand,
      semantics: Operand,
      opcode: String
  ): Either[ExecutionFailure, Unit] =
    for {
      memoryScope <- scalar(values, scope)
      _           <- Either.cond(
        memoryScope >= 0L && memoryScope <= 6L,
        (),
        ExecutionFailure.InvalidRequest(s"$opcode has invalid memory scope $memoryScope")
      )
      memorySemantics <- scalar(values, semantics)
      knownSemantics = 0xffdeL
      _ <- Either.cond(
        (memorySemantics & ~knownSemantics) == 0L,
        (),
        ExecutionFailure.InvalidRequest(
          s"$opcode has unknown memory-semantics bits 0x${java.lang.Long.toHexString(memorySemantics)}"
        )
      )
    } yield ()

  private def atomicInteger(
      program: DispatchProgram,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      pointer: Operand,
      scope: Operand,
      semantics: Operand,
      value: Operand,
      operation: AtomicIntegerOperation
  ): Either[ExecutionFailure, Unit] = {
    val resultTypeId = id(resultType)
    val valueId      = id(value)
    val valueType    = values.scalarType(valueId)
    val shape        = (
      integerShape(program, resultTypeId),
      Option.when(valueType >= 0)(valueType).flatMap(integerShape(program, _))
    ) match {
      case (Some(result @ (width, 1)), Some(source)) if result == source && (width == 32 || width == 64) => result
      case _                                                                                             =>
        return Left(
          ExecutionFailure.InvalidRequest(
            s"${operation.opcode} requires matching 32- or 64-bit scalar integer result and value types"
          )
        )
    }
    val target = address(values, pointer) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    if (target.pointee != resultTypeId)
      return Left(
        ExecutionFailure.InvalidRequest(
          s"${operation.opcode} pointer pointee %${target.pointee} does not match result type %$resultTypeId"
        )
      )
    validateAtomicControls(values, scope, semantics, operation.opcode) match {
      case Left(error) => return Left(error)
      case Right(())   => ()
    }
    val width    = shape._1
    val byteSize = width / 8
    memory.readScalar(target, byteSize).flatMap { previous =>
      val operand = values.scalarBits(valueId) & mask(width)
      val updated = operation match {
        case AtomicIntegerOperation.Add             => (previous + operand) & mask(width)
        case AtomicIntegerOperation.UnsignedMinimum =>
          if (width == 64 && java.lang.Long.compareUnsigned(previous, operand) > 0) operand
          else if (width < 64 && previous > operand) operand
          else previous
      }
      memory.writeScalar(target, updated, byteSize).map { _ =>
        values.updateScalar(id(destination), resultTypeId, previous & mask(width))
      }
    }
  }

  private def signedIntegerArithmetic(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      operation: SignedIntegerOperation
  ): Either[ExecutionFailure, Unit] = {
    val leftId          = id(left)
    val rightId         = id(right)
    val scalarLeftType  = values.scalarType(leftId)
    val scalarRightType = values.scalarType(rightId)
    if (scalarLeftType >= 0 && scalarRightType >= 0)
      (
        integerShape(program, id(resultType)),
        integerShape(program, scalarLeftType),
        integerShape(program, scalarRightType)
      ) match {
        case (Some((width, 1)), Some((leftWidth, 1)), Some((rightWidth, 1)))
            if width == leftWidth && width == rightWidth =>
          val divisor = signExtend(values.scalarBits(rightId), width)
          if (divisor == 0L)
            Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} divisor lane 0 is zero"))
          else {
            val dividend = signExtend(values.scalarBits(leftId), width)
            val minimum  = if (width == 64) Long.MinValue else -(1L << (width - 1))
            if (dividend == minimum && divisor == -1L)
              Left(
                ExecutionFailure.InvalidRequest(
                  s"${operation.opcode} lane 0 has the undefined minimum-signed-value divided by -1 case"
                )
              )
            else {
              val result = operation match {
                case SignedIntegerOperation.Divide    => dividend / divisor
                case SignedIntegerOperation.Remainder => dividend % divisor
              }
              values.updateScalar(id(destination), id(resultType), result & mask(width))
              Right(())
            }
          }
        case (None, _, _) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${operation.opcode} result type %${id(resultType)} is not an integer scalar or vector"
            )
          )
        case (_, None, _) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${operation.opcode} left operand %${id(left)} is not an integer scalar or vector"
            )
          )
        case (_, _, None) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${operation.opcode} right operand %${id(right)} is not an integer scalar or vector"
            )
          )
        case (Some(resultShape), Some(leftShape), Some(rightShape)) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${operation.opcode} type shapes do not match: result $resultShape, left $leftShape, right $rightShape"
            )
          )
      }
    else
      (values.get(leftId), values.get(rightId)) match {
        case (
              Some(RuntimeValue.Bits(leftType, Array(leftValue))),
              Some(RuntimeValue.Bits(rightType, Array(rightValue)))
            ) =>
          (
            integerShape(program, id(resultType)),
            integerShape(program, leftType),
            integerShape(program, rightType)
          ) match {
            case (Some((width, 1)), Some((leftWidth, 1)), Some((rightWidth, 1)))
                if width == leftWidth && width == rightWidth =>
              val divisor = signExtend(rightValue, width)
              if (divisor == 0L)
                Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} divisor lane 0 is zero"))
              else {
                val dividend = signExtend(leftValue, width)
                val minimum  = if (width == 64) Long.MinValue else -(1L << (width - 1))
                if (dividend == minimum && divisor == -1L)
                  Left(
                    ExecutionFailure.InvalidRequest(
                      s"${operation.opcode} lane 0 has the undefined minimum-signed-value divided by -1 case"
                    )
                  )
                else {
                  val result = operation match {
                    case SignedIntegerOperation.Divide    => dividend / divisor
                    case SignedIntegerOperation.Remainder => dividend % divisor
                  }
                  values.update(id(destination), RuntimeValue.Bits(id(resultType), Array(result & mask(width))))
                  Right(())
                }
              }
            case (None, _, _) =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"${operation.opcode} result type %${id(resultType)} is not an integer scalar or vector"
                )
              )
            case (_, None, _) =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"${operation.opcode} left operand %${id(left)} is not an integer scalar or vector"
                )
              )
            case (_, _, None) =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"${operation.opcode} right operand %${id(right)} is not an integer scalar or vector"
                )
              )
            case (Some(resultShape), Some(leftShape), Some(rightShape)) =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"${operation.opcode} type shapes do not match: result $resultShape, left $leftShape, right $rightShape"
                )
              )
          }
        case (Some(RuntimeValue.Bits(leftType, leftLanes)), Some(RuntimeValue.Bits(rightType, rightLanes))) =>
          (
            integerShape(program, id(resultType)),
            integerShape(program, leftType),
            integerShape(program, rightType)
          ) match {
            case (Some(resultShape), Some(leftShape), Some(rightShape))
                if resultShape == leftShape && resultShape == rightShape =>
              val (width, count) = resultShape
              val result         = Array.ofDim[Long](count)
              var index          = 0
              while (index < count) {
                val divisor = signExtend(rightLanes(index), width)
                if (divisor == 0L)
                  return Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} divisor lane $index is zero"))
                val dividend = signExtend(leftLanes(index), width)
                val minimum  = if (width == 64) Long.MinValue else -(1L << (width - 1))
                if (dividend == minimum && divisor == -1L)
                  return Left(
                    ExecutionFailure.InvalidRequest(
                      s"${operation.opcode} lane $index has the undefined minimum-signed-value divided by -1 case"
                    )
                  )
                result(index) =
                  (operation match {
                    case SignedIntegerOperation.Divide    => dividend / divisor
                    case SignedIntegerOperation.Remainder => dividend % divisor
                  }) & mask(width)
                index += 1
              }
              values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
              Right(())
            case (None, _, _) =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"${operation.opcode} result type %${id(resultType)} is not an integer scalar or vector"
                )
              )
            case (_, None, _) =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"${operation.opcode} left operand %${id(left)} is not an integer scalar or vector"
                )
              )
            case (_, _, None) =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"${operation.opcode} right operand %${id(right)} is not an integer scalar or vector"
                )
              )
            case (Some(resultShape), Some(leftShape), Some(rightShape)) =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"${operation.opcode} type shapes do not match: result $resultShape, left $leftShape, right $rightShape"
                )
              )
          }
        case (Some(RuntimeValue.Address(_, _)), _) =>
          Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} left operand %${id(left)} is a pointer"))
        case (_, Some(RuntimeValue.Address(_, _))) =>
          Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} right operand %${id(right)} is a pointer"))
        case (None, _) => Left(undefined(left))
        case (_, None) => Left(undefined(right))
      }
  }

  private def floatingCompare(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      operation: FloatingComparison
  ): Either[ExecutionFailure, Unit] = (values.get(id(left)), values.get(id(right))) match {
    case (Some(RuntimeValue.Bits(leftType, leftLanes)), Some(RuntimeValue.Bits(rightType, rightLanes))) =>
      (
        floatingShape(program, leftType),
        floatingShape(program, rightType),
        booleanLanes(program, id(resultType))
      ) match {
        case (Some(leftShape), Some(rightShape), Some(resultLanes))
            if leftShape == rightShape && leftShape._2 == resultLanes =>
          val width  = leftShape._1
          val result = Array.tabulate(resultLanes) { index =>
            val comparison = width match {
              case 16 =>
                val a = IEEE754.binary16ToFloat(leftLanes(index))
                val b = IEEE754.binary16ToFloat(rightLanes(index))
                operation match {
                  case FloatingComparison.OrderedEqual         => a == b
                  case FloatingComparison.OrderedLessThan      => a < b
                  case FloatingComparison.OrderedLessThanEqual => a <= b
                  case FloatingComparison.OrderedGreaterThan   => a > b
                }
              case 32 =>
                val a = IEEE754.binary32ToFloat(leftLanes(index))
                val b = IEEE754.binary32ToFloat(rightLanes(index))
                operation match {
                  case FloatingComparison.OrderedEqual         => a == b
                  case FloatingComparison.OrderedLessThan      => a < b
                  case FloatingComparison.OrderedLessThanEqual => a <= b
                  case FloatingComparison.OrderedGreaterThan   => a > b
                }
              case 64 =>
                val a = IEEE754.binary64ToDouble(leftLanes(index))
                val b = IEEE754.binary64ToDouble(rightLanes(index))
                operation match {
                  case FloatingComparison.OrderedEqual         => a == b
                  case FloatingComparison.OrderedLessThan      => a < b
                  case FloatingComparison.OrderedLessThanEqual => a <= b
                  case FloatingComparison.OrderedGreaterThan   => a > b
                }
            }
            if (comparison) 1L else 0L
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case (None, _, _) =>
          Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} left operand %${id(left)} is not floating-point"))
        case (_, None, _) =>
          Left(
            ExecutionFailure.InvalidRequest(s"${operation.opcode} right operand %${id(right)} is not floating-point")
          )
        case (_, _, None) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${operation.opcode} result type %${id(resultType)} is not a boolean scalar or vector"
            )
          )
        case (Some(leftShape), Some(rightShape), Some(resultLanes)) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${operation.opcode} type shapes do not match: left $leftShape, right $rightShape, result lanes $resultLanes"
            )
          )
      }
    case (Some(RuntimeValue.Address(_, _)), _) =>
      Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} left operand %${id(left)} is a pointer"))
    case (_, Some(RuntimeValue.Address(_, _))) =>
      Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} right operand %${id(right)} is a pointer"))
    case (None, _) => Left(undefined(left))
    case (_, None) => Left(undefined(right))
  }

  private def integerShape(program: DispatchProgram, typeId: Int): Option[(Int, Int)] =
    program.types.get(typeId) match {
      case Some(RuntimeType.Integer(width, _))            => Some((width, 1))
      case Some(RuntimeType.VectorType(component, count)) => program.types.get(component) match {
          case Some(RuntimeType.Integer(width, _)) => Some((width, count))
          case _                                   => None
        }
      case _ => None
    }

  private def floatingShape(program: DispatchProgram, typeId: Int): Option[(Int, Int)] =
    program.types.get(typeId) match {
      case Some(RuntimeType.Floating(width))              => Some((width, 1))
      case Some(RuntimeType.VectorType(component, count)) => program.types.get(component) match {
          case Some(RuntimeType.Floating(width)) => Some((width, count))
          case _                                 => None
        }
      case _ => None
    }

  private[v1_6] def floatingAddBits(width: Int, left: Long, right: Long): Long = width match {
    case 16 =>
      IEEE754.floatToBinary16(IEEE754.binary16ToFloat(left) + IEEE754.binary16ToFloat(right))
    case 32 =>
      IEEE754.floatToBinary32(IEEE754.binary32ToFloat(left) + IEEE754.binary32ToFloat(right))
    case 64 =>
      IEEE754.doubleToBinary64(IEEE754.binary64ToDouble(left) + IEEE754.binary64ToDouble(right))
  }

  private def booleanLanes(program: DispatchProgram, typeId: Int): Option[Int] = program.types.get(typeId) match {
    case Some(RuntimeType.Bool) => Some(1)
    case Some(RuntimeType.VectorType(component, count)) if program.types.get(component).contains(RuntimeType.Bool) =>
      Some(count)
    case _ => None
  }

  private def label(function: FunctionInfo, operand: Operand): Int =
    function.labels.getOrElse(
      id(operand),
      throw IllegalArgumentException(s"SPIR-V label %${id(operand)} is absent in function %${function.id}")
    )

  private def id(operand: Operand): Int        = operand.word.bits
  private def unsigned(operand: Operand): Long = operand.words.head.toUnsigned

  private def literalBits(operand: Operand): Long = {
    var result = 0L
    var index  = 0
    while (index < operand.words.size && index < 2) {
      result |= operand.words(index).toUnsigned << (index * 32)
      index += 1
    }
    result
  }

  private def literalString(operand: Operand): String = {
    val bytes = operand.words.flatMap { word =>
      LittleEndian.toCol(word.bits.toLong, 4)
    }.takeWhile(_ != 0)
    String(bytes.toArray, java.nio.charset.StandardCharsets.UTF_8)
  }

  private def alignUp(value: Int, alignment: Int): Int =
    Math.multiplyExact((value + alignment - 1) / alignment, alignment)
  private def checkedSize(size: Long): Int = {
    if (size < 0L || size > Int.MaxValue.toLong) throw IllegalArgumentException(s"invalid local size $size")
    size.toInt
  }
  private def undefined(operand: Operand): ExecutionFailure.InvalidRequest =
    ExecutionFailure.InvalidRequest(s"SPIR-V value %${id(operand)} is undefined")
}
