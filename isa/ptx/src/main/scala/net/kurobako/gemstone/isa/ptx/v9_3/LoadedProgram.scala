package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*
import VMIR.*
import cats.syntax.all.*
import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import scala.annotation.tailrec

object LoadedProgram {
  case class GlobalDefinition(size: Int > 0, initial: NonEmpty[Vec[Byte]])

  def load(program: Program): Either[Fault, LoadedProgram] = {
    val entries   = program.statements.collect { case entry: Statement.Entry => entry }
    val functions = program.statements.collect { case function: Statement.Function => function }
    val shared    = moduleSharedSymbols(program.statements)
    globalDefinitions(program.statements).map(globals =>
      LoadedProgram(entries, functions, globals, shared)
    )
  }

  private def executeVM(
      kernel: Kernel,
      task: KernelTask,
      addresses: AddressSpace,
      control: TaskControl,
      workgroups: Workgroups
  ): Either[Fault, Unit] =
    for {
      _ <- Either.cond(
        task.arguments.size == kernel.parameters.size,
        (),
        Fault.InvalidTask(
          s"PTX kernel ${kernel.name} expects ${kernel.parameters.size} arguments, found ${task.arguments.size}"
        )
      )
      arguments <- prepareVMArguments(kernel, task.arguments, task.pointerRelocations, addresses)
      _         <- VMInterpreter.execute(
        kernel,
        task.geometry,
        arguments,
        DynamicAddressMemory(VMMemory(addresses)),
        control,
        workgroups,
        Map.empty,
        None
      ).left.map(_.toFault)
    } yield ()

  private def prepareVMArguments(
      kernel: Kernel,
      arguments: Vec[KernelTask.Argument],
      relocations: Vec[KernelTask.PointerRelocation],
      addresses: AddressSpace
  ): Either[Fault, Map[NonEmpty[String], Argument]] =
    kernel.parameters.zip(arguments).zipWithIndex.foldLeft[
      Either[Fault, Map[NonEmpty[String], Argument]]
    ](Right(Map.empty)) { case (result, ((parameter, argument), index)) =>
      result.flatMap { values =>
        argument match {
          case KernelTask.Argument.Scalar(bytes) =>
            for {
              _ <- Either.cond(
                bytes.size == parameter.widthBytes.raw,
                (),
                Fault.InvalidTask(
                  s"PTX argument ${parameter.name} expects ${parameter.widthBytes} bytes, found ${bytes.size}"
                )
              )
              relocated <- relocateVMArgument(index, bytes, relocations, addresses)
            } yield values.updated(parameter.name, Argument(relocated.unsafe))
          case KernelTask.Argument.Memory(region, offset) =>
            for {
              _ <- Either.cond(
                parameter.widthBytes.raw == 8,
                (),
                Fault.InvalidTask(
                  s"memory argument ${parameter.name} requires an eight-byte PTX parameter"
                )
              )
              address <- addresses.address(region, offset)
            } yield values.updated(
              parameter.name,
              Argument.fromLong(address.raw)
            )
          case KernelTask.Argument.Local(size) =>
            Left(Fault.Unsupported(
              s"PTX local argument ${parameter.name} ($size bytes) is unsupported"
            ))
        }
      }
    }

  private def relocateVMArgument(
      index: Int,
      bytes: Vec[Byte],
      relocations: Vec[KernelTask.PointerRelocation],
      addresses: AddressSpace
  ): Either[Fault, Vec[Byte]] = {
    val relocated = Vec.toArray(bytes)
    relocations.foldLeft[Either[Fault, Unit]](Right(())) {
      case (result, relocation) => result.flatMap { _ =>
          relocation match {
            case KernelTask.PointerRelocation(
                  KernelTask.RelocationSource.Argument(source),
                  sourceOffset,
                  target,
                  targetOffset
                ) if source.raw == index =>
              addresses.address(target, targetOffset).map { address =>
                LittleEndian.write(address.raw, relocated, sourceOffset.raw.toInt, 8)
              }
            case _ => Right(())
          }
        }
    }.map(_ => Vec.from(relocated))
  }

  private def moduleSharedSymbols(statements: Vec[Statement]): Map[NonEmpty[String], Long] =
    statements.collect {
      case Statement.Data(DataAddressSpace.Shared, _, _, name, _, _) => name
    }.distinct.zipWithIndex.map { case (name, index) =>
      name -> Math.addExact(
        PTXAddressSpace.ModuleSharedBase,
        Math.multiplyExact(index.toLong, PTXAddressSpace.ModuleSharedStride)
      )
    }.toMap

  private def globalDefinitions(
      statements: Vec[Statement]
  ): Either[Fault, Map[NonEmpty[String], GlobalDefinition]] =
    statements.collect {
      case Statement.Data(addressSpace, _, elementType, name, declaredCount, initializer)
          if addressSpace == DataAddressSpace.Global || addressSpace == DataAddressSpace.Const =>
        val count: Int > 0 = declaredCount.getOrElse(1)
        (name, elementType, count, initializer)
    }.traverse { case (name, elementType, count, initializer) =>
      globalDefinition(name, elementType, count, initializer).map(name -> _)
    }.map(_.toMap)

  // Initializer decoding is a parser boundary and fails on the first malformed or unrepresentable scalar.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  private def globalDefinition(
      name: NonEmpty[String],
      elementType: DataType,
      count: Int > 0,
      initializer: Vec[Operand]
  ): Either[Fault, GlobalDefinition] = {
    val elementBytes = (elementType.widthBits.raw / 8).unsafe[Int > 0]
    if (count.raw > Int.MaxValue / elementBytes.raw)
      return Left(Fault.InvalidTask(
        s"PTX global '$name' exceeds the supported size"
      ))
    if (elementBytes.raw > 8 && initializer.nonEmpty)
      return Left(Fault.Unsupported(
        s"PTX global '$name' has an initializer wider than 64 bits"
      ))

    val size       = elementBytes * count
    val bytes      = Array.ofDim[Byte](size.raw)
    val immediates = initializer.view.collect { case Operand.Immediate(raw) => raw.raw }.take(count.raw).toVector
    var index      = 0
    while (index < immediates.size) {
      val raw = immediates(index)
      initializerBits(raw, elementType.kind, elementType.widthBits.raw) match {
        case None =>
          return Left(Fault.InvalidTask(
            s"invalid initializer '$raw' for PTX global '$name'"
          ))
        case Some(bits) =>
          var byte = 0
          while (byte < elementBytes.raw) {
            bytes(index * elementBytes.raw + byte) = (bits >>> (byte * 8)).toByte
            byte += 1
          }
      }
      index += 1
    }
    Right(GlobalDefinition(size, Vec.from(bytes).unsafe))
  }

  private def initializerBits(raw: String, kind: Char, width: Int): Option[Long] =
    try
      if (kind == 'f' && width == 32) {
        if (raw.startsWith("0f") || raw.startsWith("0F")) Some(BigInt(raw.drop(2), 16).longValue)
        else Some(IEEE754.floatToBinary32(raw.toFloat).toLong)
      } else if (kind == 'f' && width == 64) {
        if (raw.startsWith("0d") || raw.startsWith("0D")) Some(BigInt(raw.drop(2), 16).longValue)
        else Some(IEEE754.doubleToBinary64(raw.toDouble))
      } else {
        val unsigned = raw.reverse.dropWhile(character => "uUlL".contains(character)).reverse
        val negative = unsigned.startsWith("-")
        val value    = if (negative) unsigned.drop(1) else unsigned
        val parsed   =
          if (value.startsWith("0x") || value.startsWith("0X")) BigInt(value.drop(2), 16)
          else if (value.startsWith("0b") || value.startsWith("0B")) BigInt(value.drop(2), 2)
          else BigInt(value)
        Some((if (negative) -parsed else parsed).longValue)
      }
    catch {
      case _: NumberFormatException => None
    }
}

class LoadedProgram private (
    entries: Vec[Statement.Entry],
    functions: Vec[Statement.Function],
    globals: Map[NonEmpty[String], LoadedProgram.GlobalDefinition],
    moduleSharedSymbols: Map[NonEmpty[String], Long]
) {
  private case class CompilationState(
      globalAddresses: Map[NonEmpty[String], GuestAddress],
      kernels: Map[NonEmpty[String], Either[Fault, Kernel]]
  )

  private val compilationState = AtomicReference(CompilationState(Map.empty, Map.empty))

  val symbols: Either[Fault, ProgramSymbols] =
    globals.toSeq.sortBy(_._1).traverse { case (name, definition) =>
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
    entry(name).toRight(Fault.Backend(s"PTX kernel '${name}' is not present"))
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
      s"PTX global '${name}' is not present"
    )).flatMap { definition =>
      GlobalImage.validated(
        definition.size.toLong,
        Vec.from(definition.initial.toVector)
      ).left.map(Fault.InvalidTask(_))
    }

  def bindGlobal(
      name: NonEmpty[String],
      region: AllocationId,
      offset: Long >= 0,
      regionSize: Long > 0,
      addresses: AddressSpace
  ): Either[Fault, Unit] = for {
    definition <- globals.get(name).toRight(Fault.Backend(
      s"PTX global '${name}' is not present"
    ))
    _ <- Either.cond(
      definition.size.raw.toLong <= regionSize.raw - offset.raw,
      (),
      Fault.InvalidTask(
        s"PTX global '${name}' does not fit its bound memory region"
      )
    )
    address <- addresses.address(region, offset)
  } yield {
    compilationState.updateAndGet(state =>
      CompilationState(
        state.globalAddresses.updated(name, address),
        Map.empty
      )
    )
    ()
  }

  def execute(
      task: KernelTask,
      memory: AddressSpace,
      control: TaskControl,
      workgroups: Workgroups = Workgroups.Sequential
  ): Either[Fault, Unit] =
    compiled(task.kernelName).flatMap(LoadedProgram.executeVM(_, task, memory, control, workgroups))

  @tailrec
  private def compiled(name: NonEmpty[String]): Either[Fault, Kernel] = {
    val state = compilationState.get()
    state.kernels.get(name) match {
      case Some(kernel) => kernel
      case None         =>
        val kernel = entry(name).toRight(
          Fault.InvalidTask(
            s"PTX kernel '${name}' is not present"
          )
        ).flatMap(value =>
          VMCompiler.compile(value, functions, state.globalAddresses, moduleSharedSymbols, entries)
            .leftMap(error => Fault.Unsupported(error.message))
        )
        val updated = state.copy(kernels = state.kernels.updated(name, kernel))
        if (compilationState.compareAndSet(state, updated)) kernel else compiled(name)
    }
  }

  private def entry(name: NonEmpty[String]): Option[Statement.Entry] = {
    val matching = entries.filter(_.name == name)
    matching.find(_.body.nonEmpty).orElse(matching.headOption)
  }
}
