package net.kurobako.gemstone.isa.ptx

import Program.*
import cats.syntax.all.*
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
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

  def load(
      program: Program,
      workgroups: Workgroups = Workgroups.Sequential,
      release: () => Unit = () => ()
  ): Either[Fault, LoadedProgram] = Linker.link(program).flatMap(loadLinked(_, workgroups, release))

  private def loadLinked(
      program: Program,
      workgroups: Workgroups,
      release: () => Unit
  ): Either[Fault, LoadedProgram] =
    for {
      module  <- VMCompiler.module(program).leftMap(error => Fault.Unsupported(error.message))
      globals <- globalDefinitions(program.statements)
      _       <- globals.values.toVec.flatMap(_.relocations).traverse_ { relocation =>
        Either.cond(
          globals.contains(relocation.symbol) || module.functionTable.address(relocation.symbol).nonEmpty,
          (),
          Fault.InvalidRequest(s"PTX initialiser references unknown symbol '${relocation.symbol}'")
        )
      }
    } yield LoadedProgram(module, globals, workgroups, release)

  private def globalDefinitions(
      statements: Vec[Statement]
  ): Either[Fault, Map[NonEmpty[String], GlobalDefinition]] =
    statements.collect {
      case value: Statement.Data
          if value.addressSpace == DataAddressSpace.Global || value.addressSpace == DataAddressSpace.Const => value
    }.groupMapReduce(_.name)(identity)(Linker.preferDefinition(_, _)).values.toVec.traverse { declaration =>
      val Statement.Data(_, _, elementType, name, extent, initialiser) = declaration
      val count                                                        = extent match {
        case Extent.Scalar          => Right[Fault, Int > 0](1)
        case Extent.Sized(elements) => Right(elements)
        case Extent.Unsized         => initialiser.size.refine[Int > 0].left.map(_ =>
            Fault.Unsupported(s"PTX global '$name' has no defined extent")
          )
      }
      count.flatMap(globalDefinition(name, elementType, _, initialiser)).map(name -> _)
    }.map(_.toMap)

  // Initialiser decoding is a parser boundary and fails on the first malformed or unrepresentable scalar.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  private def globalDefinition(
      name: NonEmpty[String],
      elementType: DataType,
      count: Int > 0,
      initialiser: Vec[Operand]
  ): Either[Fault, GlobalDefinition] = {
    val elementBytes = elementType.byteCount
    if (count > Int.MaxValue / elementBytes)
      return Left(Fault.InvalidRequest(
        s"PTX global '$name' exceeds the supported size"
      ))
    if (elementBytes > 8 && initialiser.nonEmpty)
      return Left(Fault.Unsupported(
        s"PTX global '$name' has an initialiser wider than 64 bits"
      ))

    if (initialiser.size > count)
      return Left(Fault.InvalidRequest(s"too many initialisers for PTX global '$name'"))
    val size  = elementBytes * count
    val bytes = Array.ofDim[Byte](size)
    if (initialiser.isEmpty) return Right(GlobalDefinition(size, Vec.from(bytes).unsafe, Vec.empty))
    val scalarWidth     = elementBytes.unsafe[Int in 1 ~ 8]
    val relocations     = Vec.newBuilder[Relocation]
    var index: Int >= 0 = 0
    while (index < initialiser.size) {
      val operand = initialiser(index)
      val offset  = index * elementBytes
      val bits    = operand match {
        case Operand.Immediate(raw)                 => initialiserBits(raw, elementType)
        case Operand.Negate(Operand.Immediate(raw)) =>
          initialiserBits(s"-$raw", elementType)
        case _ => None
      }
      bits match {
        case Some(value) =>
          var byte = 0
          while (byte < elementBytes) {
            bytes(offset.raw + byte) = (value >>> (byte * 8)).toByte
            byte += 1
          }
        case None =>
          initialiserAddress(operand) match {
            case Some((symbol, addend)) if elementType.kind != 'f' =>
              relocations += Relocation(
                offset = offset,
                width = scalarWidth,
                symbol = symbol,
                addend = addend
              )
            case _ => return Left(Fault.InvalidRequest(s"invalid initialiser '$operand' for PTX global '$name'"))
          }
      }
      index += 1
    }
    Right(GlobalDefinition(size, Vec.from(bytes).unsafe, relocations.result()))
  }

  private def initialiserAddress(operand: Operand): Option[(NonEmpty[String], Long)] = operand match {
    case Operand.Symbol(name)                         => Some((name, 0L))
    case Operand.GenericAddress(symbol)               => initialiserAddress(symbol)
    case Operand.Add(base, Operand.Immediate(offset)) =>
      for {
        address <- initialiserAddress(base)
        value   <- initialiserBits(offset, DataType.U64)
      } yield (address._1, address._2 + value)
    case Operand.Subtract(base, Operand.Immediate(offset)) =>
      for {
        address <- initialiserAddress(base)
        value   <- initialiserBits(offset, DataType.U64)
      } yield (address._1, address._2 - value)
    case _ => None
  }

  private def initialiserBits(raw: NonEmpty[String], elementType: DataType): Option[Long] =
    NumericLiteral.initialiser(raw, elementType.kind, elementType.widthBits)
}

class LoadedProgram private (
    module: VMCompiler.Module,
    globals: Map[NonEmpty[String], LoadedProgram.GlobalDefinition],
    workgroups: Workgroups,
    release: () => Unit
) extends Backend.Program {
  private case class CompilationState(
      globalAddresses: Map[NonEmpty[String], GuestAddress],
      kernels: Map[NonEmpty[String], Either[Fault, Kernel]]
  )

  private val bindingLock      = ReentrantLock()
  private val compilationState = AtomicReference(CompilationState(Map.empty, Map.empty))

  override val symbols: Either[Fault, ProgramSymbols] =
    globals.toVec.sortBy(_._1).traverse((name, definition) =>
      GlobalSymbol.validated(name, None, definition.size.widen[Long >= 0], Vec.empty).leftMap(Fault.InvalidRequest(_))
    ).map(globalMetadata =>
      ProgramSymbols(module.entries.keys.toVec.sorted.map(name => KernelSymbol(name, name)), globalMetadata)
    )

  override def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata] =
    module.entries.get(name).toRight(Fault.NotFound(s"PTX kernel '$name' is not present"))
      .flatMap(entry =>
        VMCompiler.parameterLayout(entry.parameters).leftMap(error => Fault.InvalidProgram(error.message))
      )
      .flatMap { layout =>
        val parameters = layout.map(parameter =>
          KernelMetadata.Parameter(
            parameter.widthBytes,
            parameter.offset,
            if (parameter.pointer) KernelMetadata.ParameterKind.Allocation else KernelMetadata.ParameterKind.Scalar,
            containsPointers = false
          )
        )
        val blockSize = layout.lastOption.fold(0L)(parameter => parameter.offset.toLong + parameter.widthBytes)
        for {
          size <- Either.cond(
            blockSize <= Int.MaxValue.toLong,
            blockSize.toInt.unsafe[Int >= 0],
            Fault.InvalidRequest(s"PTX argument block size exceeds the supported range: $blockSize")
          )
          metadata <-
            KernelMetadata.validated(size, parameters.nonNegativeSize, parameters).leftMap(Fault.InvalidRequest(_))
        } yield metadata
      }

  override def global(
      name: NonEmpty[String]
  ): Either[Fault, ProgramGlobal] =
    globals.get(name).toRight(Fault.NotFound(
      s"PTX global '$name' is not present"
    )).flatMap { definition =>
      ProgramGlobal.validated(
        definition.size.widen[Long >= 0],
        definition.initial.toVec
      ).left.map(Fault.InvalidRequest(_))
    }

  override def bindGlobal(
      name: NonEmpty[String],
      binding: GlobalBinding,
      addresses: AddressSpace
  ): Either[Fault, Unit] =
    bindingLock.withLock {
      for {
        _       <- globals.get(name).toRight(Fault.NotFound(s"PTX global '$name' is not present"))
        address <- addresses.address(binding.region, binding.offset)
        previous = compilationState.get().globalAddresses
        first <- GlobalBinding.rebind(name, previous.get(name), address)
        _     <- if (!first) Right(())
        else {
          val bound = previous.updated(name, address)
          globals.toVec.traverse_ { case (source, value) =>
            value.relocations.traverse_ { relocation =>
              val target =
                bound.get(relocation.symbol).map(_.raw).orElse(module.functionTable.address(relocation.symbol))
              (bound.get(source), target) match {
                case (Some(destination), Some(targetAddress)) if source == name || relocation.symbol == name =>
                  addresses.writeValue(
                    destination.offset(relocation.offset.toLong),
                    targetAddress + relocation.addend,
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

  override def execute(
      task: KernelTask,
      memory: AddressSpace,
      control: TaskControl,
      handler: Event => Either[Fault, Unit]
  ): Either[Fault, Unit] =
    compiled(task.kernelName).flatMap(VMKernelLaunch.execute(_, task, memory, control, workgroups))

  override def close(): Unit = release()

  @tailrec
  private def compiled(name: NonEmpty[String]): Either[Fault, Kernel] = {
    val state = compilationState.get()
    state.kernels.get(name) match {
      case Some(kernel) => kernel
      case None         =>
        val kernel = module.entries.get(name).toRight(Fault.NotFound(s"PTX kernel '$name' is not present")).flatMap(_ =>
          VMCompiler.compile(module, name, state.globalAddresses).leftMap(error => Fault.InvalidProgram(error.message))
        )
        val updated = state.copy(kernels = state.kernels.updated(name, kernel))
        if (compilationState.compareAndSet(state, updated)) kernel else compiled(name)
    }
  }
}
