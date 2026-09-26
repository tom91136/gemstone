package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*

import VMIR.*

import net.kurobako.gemstone.NonEmptyVec
import net.kurobako.gemstone.NonNegativeInt
import net.kurobako.gemstone.NonNegativeLong
import net.kurobako.gemstone.PositiveInt
import net.kurobako.gemstone.PositiveLong
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Vec.*
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.binary.ScalarByteWidth
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.memory.DynamicAddressMemory
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.vm.{AddressSpace, Fault, KernelTask, Memory, Result, Scheduler, TaskControl}

import cats.syntax.all.*

import java.util.concurrent.atomic.AtomicReference
import scala.annotation.tailrec

private[v9_3] final class VMLoadedProgram private (
    entries: Vec[Statement.Entry],
    functions: Vec[Statement.Function],
    globals: Map[SymbolName, VMLoadedProgram.GlobalDefinition],
    moduleSharedSymbols: Map[SymbolName, Long]
) {
  private final case class CompilationState(
      globalAddresses: Map[SymbolName, GuestAddress],
      kernels: Map[KernelName, Either[Fault, Kernel]]
  )

  private val compilationState = AtomicReference(CompilationState(Map.empty, Map.empty))

  private[v9_3] val symbols: Either[Fault, net.kurobako.gemstone.isa.ProgramSymbols] =
    globals.toSeq.sortBy(_._1.raw).traverse { case (name, definition) =>
      for {
        globalName = net.kurobako.gemstone.isa.GlobalName(name.toNonEmptyString)
        size       = definition.size.toLong.toNonNegative
        metadata <- net.kurobako.gemstone.isa.GlobalSymbol.validated(globalName, None, size, Vec.empty)
          .leftMap(Fault.Backend(_))
      } yield metadata
    }.flatMap { globalMetadata =>
      entries.map(_.name).distinct.sortBy(_.raw).traverse { name =>
        for {
          linkageName <- net.kurobako.gemstone.isa.LinkageName.either(name.raw).leftMap(_ =>
            Fault.Backend(
              s"invalid PTX linkage name '$name'"
            )
          )
        } yield net.kurobako.gemstone.isa.KernelSymbol(
          net.kurobako.gemstone.isa.KernelName(name.toNonEmptyString),
          linkageName
        )
      }.map(kernels => net.kurobako.gemstone.isa.ProgramSymbols(kernels, Vec.from(globalMetadata)))
    }

  private[v9_3] def kernelMetadata(
      name: net.kurobako.gemstone.isa.KernelName
  ): Either[Fault, net.kurobako.gemstone.isa.KernelMetadata] =
    entry(name).toRight(Fault.Backend(s"PTX kernel '${name.raw}' is not present"))
      .flatMap(value => VMCompiler.parameterLayout(value).leftMap(error => Fault.Unsupported(error.message)))
      .flatMap { layout =>
        val parameters = layout.map { case (parameter, offset) =>
          net.kurobako.gemstone.isa.KernelMetadata.Parameter(
            parameter.widthBytes,
            offset,
            if (parameter.pointer) net.kurobako.gemstone.isa.KernelMetadata.ParameterKind.Memory
            else net.kurobako.gemstone.isa.KernelMetadata.ParameterKind.Scalar,
            containsPointers = false
          )
        }
        val blockSize =
          parameters.lastOption.fold(0L)(parameter => (parameter.offset + parameter.width).raw.toLong)
        for {
          refinedBlockSize <- Either.cond(
            blockSize <= Int.MaxValue.toLong,
            NonNegativeInt.unsafe(blockSize.toInt),
            Fault.InvalidTask(s"PTX argument block size exceeds the supported range: $blockSize")
          )
          metadata <- net.kurobako.gemstone.isa.KernelMetadata.validated(
            refinedBlockSize,
            parameters.nonNegativeSize,
            parameters
          ).leftMap(Fault.Backend(_))
        } yield metadata
      }

  private[v9_3] def globalImage(
      name: net.kurobako.gemstone.isa.GlobalName
  ): Either[net.kurobako.gemstone.vm.Fault, (NonNegativeLong, Vec[Byte])] =
    globals.get(SymbolName(name.toNonEmptyString)).toRight(net.kurobako.gemstone.vm.Fault.Backend(
      s"PTX global '${name.raw}' is not present"
    )).map(definition => definition.size.toLong.toNonNegative -> Vec.from(definition.initial.toVector))

  private[v9_3] def bindGlobal(
      name: net.kurobako.gemstone.isa.GlobalName,
      region: net.kurobako.gemstone.vm.MemoryRegion,
      offset: NonNegativeLong,
      regionSize: PositiveLong,
      addresses: net.kurobako.gemstone.vm.AddressSpace
  ): Either[net.kurobako.gemstone.vm.Fault, Unit] = for {
    definition <- globals.get(SymbolName(name.toNonEmptyString)).toRight(net.kurobako.gemstone.vm.Fault.Backend(
      s"PTX global '${name.raw}' is not present"
    ))
    _ <- Either.cond(
      definition.size.raw.toLong <= regionSize.raw - offset.raw,
      (),
      net.kurobako.gemstone.vm.Fault.InvalidTask(
        s"PTX global '${name.raw}' does not fit its bound memory region"
      )
    )
    address <- addresses.address(region, offset)
  } yield {
    compilationState.updateAndGet(state =>
      CompilationState(
        state.globalAddresses.updated(SymbolName(name.toNonEmptyString), address),
        Map.empty
      )
    )
    ()
  }

  private[v9_3] def execute(
      task: KernelTask,
      memory: Memory,
      control: TaskControl,
      scheduler: Scheduler
  ): Either[Fault, Result] =
    memory match {
      case addresses: AddressSpace =>
        compiled(task.kernelName)
          .flatMap(VMLoadedProgram.executeVM(_, task, memory, addresses, control, scheduler))
      case _ => Left(Fault.Unsupported(
          "PTX execution requires an address-space capable memory"
        ))
    }

  @tailrec
  private def compiled(name: KernelName): Either[Fault, Kernel] = {
    val state = compilationState.get()
    state.kernels.get(name) match {
      case Some(kernel) => kernel
      case None         =>
        val kernel = entry(name).toRight(
          Fault.InvalidTask(
            s"PTX kernel '${name.raw}' is not present"
          )
        ).flatMap(value =>
          VMCompiler.compile(value, functions, state.globalAddresses, moduleSharedSymbols, entries)
            .leftMap(error => Fault.Unsupported(error.message))
        )
        val updated = state.copy(kernels = state.kernels.updated(name, kernel))
        if (compilationState.compareAndSet(state, updated)) kernel else compiled(name)
    }
  }

  private def entry(name: KernelName): Option[Statement.Entry] = {
    val matching = entries.filter(_.name == name)
    matching.find(_.body.nonEmpty).orElse(matching.headOption)
  }
}

private[v9_3] object VMLoadedProgram {
  private[v9_3] final case class GlobalDefinition(size: PositiveInt, initial: NonEmptyVec[Byte])

  def load(program: Program): Either[Fault, VMLoadedProgram] = {
    val entries   = program.statements.collect { case entry: Statement.Entry => entry }
    val functions = program.statements.collect { case function: Statement.Function => function }
    val shared    = moduleSharedSymbols(program.statements)
    globalDefinitions(program.statements).map(globals =>
      VMLoadedProgram(entries, functions, globals, shared)
    )
  }

  private def executeVM(
      kernel: Kernel,
      task: KernelTask,
      memory: Memory,
      addresses: AddressSpace,
      control: TaskControl,
      scheduler: Scheduler
  ): Either[Fault, Result] =
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
        net.kurobako.gemstone.memory.DynamicAddressMemory(VMMemory(memory, addresses)),
        control,
        scheduler,
        Map.empty,
        None
      ).left.map(_.toFault)
    } yield Result(control.usedInstructions)

  private def prepareVMArguments(
      kernel: Kernel,
      arguments: Vec[KernelTask.Argument],
      relocations: Vec[KernelTask.PointerRelocation],
      addresses: AddressSpace
  ): Either[Fault, Map[ParameterName, Argument]] =
    kernel.parameters.zip(arguments).zipWithIndex.foldLeft[
      Either[Fault, Map[ParameterName, Argument]]
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
            } yield values.updated(parameter.name, Argument(NonEmptyVec.unsafeFromVec(relocated)))
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
                LittleEndian.write(address.raw, relocated, sourceOffset.raw.toInt, ScalarByteWidth.Qword)
              }
            case _ => Right(())
          }
        }
    }.map(_ => Vec.from(relocated))
  }

  private def moduleSharedSymbols(statements: Vec[Statement]): Map[SymbolName, Long] =
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
  ): Either[Fault, Map[SymbolName, GlobalDefinition]] =
    statements.collect {
      case Statement.Data(addressSpace, _, elementType, name, declaredCount, initializer)
          if addressSpace == DataAddressSpace.Global || addressSpace == DataAddressSpace.Const =>
        val count: PositiveInt = declaredCount.getOrElse(PositiveInt.One)
        (name, elementType, count, initializer)
    }.traverse { case (name, elementType, count, initializer) =>
      globalDefinition(name, elementType, count, initializer).map(name -> _)
    }.map(_.toMap)

  // Initializer decoding is a parser boundary and fails on the first malformed or unrepresentable scalar.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  private def globalDefinition(
      name: SymbolName,
      elementType: DataType,
      count: PositiveInt,
      initializer: Vec[Operand]
  ): Either[Fault, GlobalDefinition] = {
    val elementBytes = PositiveInt.unsafe(elementType.widthBits.raw / 8).raw
    if (count.raw > Int.MaxValue / elementBytes)
      return Left(Fault.InvalidTask(
        s"PTX global '$name' exceeds the supported size"
      ))
    if (elementBytes > 8 && initializer.nonEmpty)
      return Left(Fault.Unsupported(
        s"PTX global '$name' has an initializer wider than 64 bits"
      ))

    val size       = elementBytes * count.raw
    val bytes      = Array.ofDim[Byte](size)
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
          while (byte < elementBytes) {
            bytes(index * elementBytes + byte) = (bits >>> (byte * 8)).toByte
            byte += 1
          }
      }
      index += 1
    }
    Right(GlobalDefinition(PositiveInt.unsafe(size), NonEmptyVec.unsafeFromVec(Vec.from(bytes))))
  }

  private def initializerBits(raw: String, kind: Char, width: Int): Option[Long] =
    try
      if (kind == 'f' && width == 32) {
        if (raw.startsWith("0f") || raw.startsWith("0F")) Some(BigInt(raw.drop(2), 16).longValue)
        else Some(IEEE754.floatToBinary32(raw.toFloat).raw)
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
