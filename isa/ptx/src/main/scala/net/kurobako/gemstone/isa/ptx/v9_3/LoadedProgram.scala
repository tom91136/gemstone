package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*
import cats.syntax.all.*
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import scala.annotation.tailrec

object LoadedProgram {
  case class Relocation(offset: Int >= 0, width: Int where Between[1, 8], symbol: NonEmpty[String], addend: Long)
  case class GlobalDefinition(size: Int > 0, initial: NonEmpty[Vec[Byte]], relocations: Vec[Relocation])

  def load(program: Program): Either[Fault, LoadedProgram] = Linker.link(program).flatMap(loadLinked(_))

  private def loadLinked(program: Program): Either[Fault, LoadedProgram] = {
    val entries       = program.statements.collect { case entry: Statement.Entry => entry }
    val functions     = program.statements.collect { case function: Statement.Function => function }
    val functionTable = FunctionTable(functions.map(_.name).toSet ++ entries.map(_.name).toSet)
    for {
      shared  <- moduleSharedSymbols(program.statements)
      globals <- globalDefinitions(program.statements)
      loaded  <-
        globals.values.toVec.flatMap(_.relocations).traverse_ { relocation =>
          Either.cond(
            globals.contains(relocation.symbol) || functionTable.address(relocation.symbol).nonEmpty,
            (),
            Fault.InvalidTask(s"PTX initializer references unknown symbol '${relocation.symbol}'")
          )
        }.map(_ => LoadedProgram(entries, functions, globals, shared, functionTable))
    } yield loaded
  }

  private def moduleSharedSymbols(statements: Vec[Statement]): Either[Fault, Map[NonEmpty[String], Long]] = {
    val declarations = statements.collect {
      case value: Statement.Data if value.addressSpace == DataAddressSpace.Shared => value
    }.groupMapReduce(_.name)(identity)(Linker.preferDefinition(_, _)).values.toVec.sortBy(_.name.raw)
    val (dynamic, fixed)                       = declarations.partition(_.extent == Extent.Unsized)
    def alignment(value: Statement.Data): Long =
      value.qualifiers.collectFirst { case DeclarationQualifier.Alignment(bytes) => bytes.raw.toLong }
        .getOrElse(value.elementType.widthBits.raw.toLong / 8L)
    def aligned(address: Long, alignment: Long): Long =
      address + (alignment - address % alignment) % alignment
    val layout = fixed.foldLeft[Either[Fault, (Long, Map[NonEmpty[String], Long])]](
      Right(VMAddressSpace.ModuleSharedBase -> Map.empty)
    ) { (result, declaration) =>
      result.flatMap { (next, symbols) =>
        val count = declaration.extent match {
          case Extent.Sized(elements) => elements.raw.toLong
          case _                      => 1L
        }
        val address = aligned(next, alignment(declaration))
        val end     = address + count * declaration.elementType.widthBits.raw.toLong / 8L
        Either.cond(
          end <= VMAddressSpace.SharedLimit,
          end -> symbols.updated(declaration.name, address),
          Fault.Unsupported(s"PTX shared declaration '${declaration.name}' exceeds the shared address aperture")
        )
      }
    }
    layout.flatMap { (next, symbols) =>
      val dynamicAlignment = dynamic.foldLeft(1L)((current, declaration) => math.max(current, alignment(declaration)))
      val base             = aligned(next, dynamicAlignment)
      Either.cond(
        dynamic.isEmpty || base < VMAddressSpace.SharedLimit,
        symbols ++ dynamic.map(declaration => declaration.name -> base),
        Fault.Unsupported("PTX dynamic shared storage exceeds the shared address aperture")
      )
    }
  }

  private def globalDefinitions(
      statements: Vec[Statement]
  ): Either[Fault, Map[NonEmpty[String], GlobalDefinition]] =
    statements.collect {
      case value: Statement.Data
          if value.addressSpace == DataAddressSpace.Global || value.addressSpace == DataAddressSpace.Const => value
    }.groupMapReduce(_.name)(identity)(Linker.preferDefinition(_, _)).values.toVec.traverse { declaration =>
      val Statement.Data(_, _, elementType, name, extent, initializer) = declaration
      val count                                                        = extent match {
        case Extent.Scalar          => Right[Fault, Int > 0](1)
        case Extent.Sized(elements) => Right(elements)
        case Extent.Unsized         => initializer.size.refine[Int > 0].left.map(_ =>
            Fault.Unsupported(s"PTX global '$name' has no defined extent")
          )
      }
      count.flatMap(globalDefinition(name, elementType, _, initializer)).map(name -> _)
    }.map(_.toMap)

  // Initializer decoding is a parser boundary and fails on the first malformed or unrepresentable scalar.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  private def globalDefinition(
      name: NonEmpty[String],
      elementType: DataType,
      count: Int > 0,
      initializer: Vec[Operand]
  ): Either[Fault, GlobalDefinition] = {
    val elementBytes = elementType.byteCount
    if (count.raw > Int.MaxValue / elementBytes.raw)
      return Left(Fault.InvalidTask(
        s"PTX global '$name' exceeds the supported size"
      ))
    if (elementBytes.raw > 8 && initializer.nonEmpty)
      return Left(Fault.Unsupported(
        s"PTX global '$name' has an initializer wider than 64 bits"
      ))

    if (initializer.size > count.raw)
      return Left(Fault.InvalidTask(s"too many initializers for PTX global '$name'"))
    val size  = elementBytes * count
    val bytes = Array.ofDim[Byte](size.raw)
    if (initializer.isEmpty) return Right(GlobalDefinition(size, Vec.from(bytes).unsafe, Vec.empty))
    val scalarWidth     = elementBytes.raw.unsafe[Int in 1 ~ 8]
    val relocations     = Vec.newBuilder[Relocation]
    var index: Int >= 0 = 0
    while (index.raw < initializer.size) {
      val operand = initializer(index.raw)
      val offset  = index * elementBytes
      val bits    = operand match {
        case Operand.Immediate(raw)                 => initializerBits(raw, elementType)
        case Operand.Negate(Operand.Immediate(raw)) =>
          initializerBits(s"-$raw", elementType)
        case _ => None
      }
      bits match {
        case Some(value) =>
          var byte = 0
          while (byte < elementBytes.raw) {
            bytes(offset.raw + byte) = (value >>> (byte * 8)).toByte
            byte += 1
          }
        case None =>
          initializerAddress(operand) match {
            case Some((symbol, addend)) if elementType.kind != 'f' =>
              relocations += Relocation(
                offset = offset,
                width = scalarWidth,
                symbol = symbol,
                addend = addend
              )
            case _ => return Left(Fault.InvalidTask(s"invalid initializer '$operand' for PTX global '$name'"))
          }
      }
      index += 1
    }
    Right(GlobalDefinition(size, Vec.from(bytes).unsafe, relocations.result()))
  }

  private def initializerAddress(operand: Operand): Option[(NonEmpty[String], Long)] = operand match {
    case Operand.Symbol(name)                         => Some((name, 0L))
    case Operand.GenericAddress(symbol)               => initializerAddress(symbol)
    case Operand.Add(base, Operand.Immediate(offset)) =>
      for {
        address <- initializerAddress(base)
        value   <- initializerBits(offset, DataType.U64)
      } yield (address._1, address._2 + value)
    case Operand.Subtract(base, Operand.Immediate(offset)) =>
      for {
        address <- initializerAddress(base)
        value   <- initializerBits(offset, DataType.U64)
      } yield (address._1, address._2 - value)
    case _ => None
  }

  private def initializerBits(raw: NonEmpty[String], elementType: DataType): Option[Long] = {
    val kind  = elementType.kind
    val width = elementType.widthBits.raw
    try
      if (kind == 'f' && width == 32) {
        NumericLiteral.hexadecimalPayload(raw, 'f')
          .fold(Some(IEEE754.floatToBinary32(raw.toFloat).toLong))(digits => Some(BigInt(digits, 16).toLong))
      } else if (kind == 'f' && width == 64) {
        NumericLiteral.hexadecimalPayload(raw, 'd')
          .fold(Some(IEEE754.doubleToBinary64(raw.toDouble)))(digits => Some(BigInt(digits, 16).toLong))
      } else {
        val unsigned = NumericLiteral.stripIntegerSuffix(raw)
        val negative = unsigned.startsWith("-")
        val value    = if (negative) unsigned.drop(1) else unsigned
        val parsed   = NumericLiteral.integer(value)
        Some((if (negative) -parsed else parsed).toLong)
      }
    catch {
      case _: NumberFormatException => None
    }
  }
}

class LoadedProgram private (
    entries: Vec[Statement.Entry],
    functions: Vec[Statement.Function],
    globals: Map[NonEmpty[String], LoadedProgram.GlobalDefinition],
    moduleSharedSymbols: Map[NonEmpty[String], Long],
    functionTable: FunctionTable
) {
  private val initializedFunctions = globals.values.toVec.flatMap(_.relocations.map(_.symbol))
    .filter(symbol => functionTable.address(symbol).nonEmpty).toSet

  private case class CompilationState(
      globalAddresses: Map[NonEmpty[String], GuestAddress],
      kernels: Map[NonEmpty[String], Either[Fault, Kernel]]
  )

  private val bindingLock      = ReentrantLock()
  private val compilationState = AtomicReference(CompilationState(Map.empty, Map.empty))

  val symbols: Either[Fault, ProgramSymbols] =
    globals.toVec.sortBy(_._1).traverse { case (name, definition) =>
      for {
        globalName = name
        size       = definition.size.toLong
        metadata <- GlobalSymbol.validated(globalName, None, size, Vec.empty)
          .leftMap(Fault.Backend(_))
      } yield metadata
    }.flatMap { globalMetadata =>
      val kernels = entries.map(_.name).distinct.sorted.map(name =>
        KernelSymbol(name, name)
      )
      Right(ProgramSymbols(kernels, Vec.from(globalMetadata)))
    }

  def kernel(
      name: NonEmpty[String]
  ): Either[Fault, KernelMetadata] =
    entry(name).toRight(Fault.Backend(s"PTX kernel '$name' is not present"))
      .flatMap(value => VMCompiler.parameterLayout(value).leftMap(error => Fault.Unsupported(error.message)))
      .flatMap { layout =>
        val parameters = layout.map { case (parameter, offset) =>
          KernelMetadata.Parameter(
            parameter.widthBytes,
            offset,
            if (parameter.pointer) KernelMetadata.ParameterKind.Allocation
            else KernelMetadata.ParameterKind.Scalar,
            containsPointers = false
          )
        }
        val blockSize =
          parameters.lastOption.fold(0L)(parameter => parameter.offset.raw.toLong + parameter.width.raw.toLong)
        for {
          refinedBlockSize <- Either.cond(
            blockSize <= Int.MaxValue.toLong,
            blockSize.toInt.unsafe[Int >= 0],
            Fault.InvalidTask(s"PTX argument block size exceeds the supported range: $blockSize")
          )
          metadata <- KernelMetadata.validated(
            refinedBlockSize,
            parameters.nonNegativeSize,
            parameters
          ).leftMap(Fault.Backend(_))
        } yield metadata
      }

  def global(
      name: NonEmpty[String]
  ): Either[Fault, GlobalImage] =
    globals.get(name).toRight(Fault.Backend(
      s"PTX global '$name' is not present"
    )).flatMap { definition =>
      GlobalImage.validated(
        definition.size.toLong,
        definition.initial.toVec
      ).left.map(Fault.InvalidTask(_))
    }

  def bindGlobal(
      name: NonEmpty[String],
      region: AllocationId,
      offset: Long >= 0,
      regionSize: Long > 0,
      addresses: AddressSpace
  ): Either[Fault, Unit] = bindingLock.withLock {
    for {
      definition <- globals.get(name).toRight(Fault.Backend(s"PTX global '$name' is not present"))
      _          <- Either.cond(
        offset.raw <= regionSize.raw && definition.size.raw.toLong <= regionSize.raw - offset.raw,
        (),
        Fault.InvalidTask(s"PTX global '$name' does not fit its bound memory region")
      )
      address <- addresses.address(region, offset)
      previous = compilationState.get().globalAddresses
      _ <- previous.get(name) match {
        case Some(existing) => Either.cond(
            existing == address,
            (),
            Fault.InvalidTask(s"PTX global '$name' is already bound to different storage")
          )
        case None =>
          val bound = previous.updated(name, address)
          globals.toVec.traverse_ { case (source, value) =>
            value.relocations.traverse_ { relocation =>
              val target = bound.get(relocation.symbol).map(_.raw).orElse(functionTable.address(relocation.symbol))
              (bound.get(source), target) match {
                case (Some(destination), Some(target)) if source == name || relocation.symbol == name =>
                  addresses.writeValue(
                    destination.offset(relocation.offset.raw.toLong),
                    target + relocation.addend,
                    relocation.width
                  )
                case _ => Right(())
              }
            }
          }.map { _ =>
            compilationState.updateAndGet(state => state.copy(globalAddresses = bound, kernels = Map.empty))
            ()
          }
      }
    } yield ()
  }

  def execute(
      task: KernelTask,
      memory: AddressSpace,
      control: TaskControl,
      workgroups: Workgroups = Workgroups.Sequential
  ): Either[Fault, Unit] =
    compiled(task.kernelName).flatMap(VMKernelLaunch.execute(_, task, memory, control, workgroups))

  @tailrec
  private def compiled(name: NonEmpty[String]): Either[Fault, Kernel] = {
    val state = compilationState.get()
    state.kernels.get(name) match {
      case Some(kernel) => kernel
      case None         =>
        val kernel = entry(name).toRight(
          Fault.InvalidTask(
            s"PTX kernel '$name' is not present"
          )
        ).flatMap(value =>
          VMCompiler.compile(
            value,
            functions,
            state.globalAddresses,
            moduleSharedSymbols,
            entries,
            initializedFunctions
          )
            .leftMap(error => Fault.Unsupported(error.message))
        )
        val updated = state.copy(kernels = state.kernels.updated(name, kernel))
        if (compilationState.compareAndSet(state, updated)) kernel else compiled(name)
    }
  }

  private def entry(name: NonEmpty[String]): Option[Statement.Entry] = {
    val matching = entries.filter(_.name == name)
    matching.reduceOption(Linker.preferDefinition(_, _))
  }
}
