package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*

import VirtualMachine.*

import net.kurobako.gemstone.{ByteKey, LRUCache, Vec}
import net.kurobako.gemstone.Vec.*
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.binary.ScalarByteWidth
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.AddressBinding
import net.kurobako.gemstone.memory.DynamicAddressMemory
import net.kurobako.gemstone.memory.ExecutionAddressSpace
import net.kurobako.gemstone.memory.GuestAddress

import cats.syntax.all.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicReference
import scala.annotation.tailrec

object ModuleDecoder extends ProgramDecoder {
  private inline val AllocationBase   = 0x0000100000000000L
  private inline val AllocationSpan   = 0x0000010000000000L
  private inline val ModuleSharedBase = 0x0000000020000000L

  private final case class GlobalDefinition(size: Int :| Positive, initial: Vec[Byte] :| MinLength[1])
  private final case class PreparedProgram(
      entries: Vec[Statement.Entry],
      functions: Vec[Statement.Function],
      globals: Map[SymbolName, GlobalDefinition],
      shared: Map[SymbolName, Long]
  )
  private val prepared = LRUCache.softValues[ByteKey, Either[ExecutionFailure, PreparedProgram]](4)

  override val format: ProgramFormat = ProgramFormat.PTXText

  override def decode(image: Vec[Byte]): Either[ExecutionFailure, ProgramInstance] =
    prepared.get(ByteKey(image)) {
      val source = String(Vec.toArray(image), StandardCharsets.UTF_8)
      Parser.parse(source)
        .leftMap(_.toString)
        .leftFlatMap(detail =>
          detail.refineEither[MinLength[1]] match {
            case Left(_)        => Left(ExecutionFailure.Failed("PTX parser returned an empty diagnostic"))
            case Right(refined) => Left(ExecutionFailure.InvalidRequest(refined))
          }
        )
        .flatMap { program =>
          val entries   = program.statements.collect { case entry: Statement.Entry => entry }
          val functions = program.statements.collect { case function: Statement.Function => function }
          val shared    = moduleSharedSymbols(program.statements)
          globalDefinitions(program.statements).map(globals => PreparedProgram(entries, functions, globals, shared))
        }
    }.map(value => ModuleInstance(value.entries, value.functions, value.globals, value.shared))

  private final class ModuleInstance(
      entries: Vec[Statement.Entry],
      functions: Vec[Statement.Function],
      globals: Map[SymbolName, GlobalDefinition],
      moduleSharedSymbols: Map[SymbolName, Long]
  ) extends net.kurobako.gemstone.execution.ProgramInstance {
    private final case class LoweringState(
        globalAddresses: Map[SymbolName, GuestAddress],
        kernels: Map[KernelName, Either[ExecutionFailure, Kernel]]
    )

    private val loweringState = AtomicReference(LoweringState(Map.empty, Map.empty))

    override val catalog: Either[ExecutionFailure, ProgramCatalog] =
      globals.toSeq.sortBy(_._1.value).traverse { case (name, definition) =>
        for {
          globalName = GlobalName(name.value)
          size       = definition.size.toLong.refineUnsafe[Positive0]
          metadata <- ProgramGlobalMetadata.validated(globalName, None, size, Vec.empty)
            .leftMap(detail => ExecutionFailure.InvalidRequest(detail))
        } yield metadata
      }.flatMap { globalMetadata =>
        entries.map(_.name).distinct.sortBy(_.value).traverse { name =>
          for {
            linkageName <- LinkageName.either(name.value).leftMap(_ =>
              ExecutionFailure.InvalidRequest(
                s"invalid PTX linkage name '$name'"
              )
            )
          } yield ProgramKernel(name, linkageName)
        }.map(kernels => ProgramCatalog(kernels, Vec.from(globalMetadata)))
      }

    override def kernel(name: KernelName): Either[ExecutionFailure, KernelMetadata] =
      entry(name).toRight(ExecutionFailure.NotFound(
        s"PTX kernel '${name.value}' is not present"
      )).flatMap(value =>
        Lowering.parameterLayout(value)
          .leftMap(error => ExecutionFailure.Unsupported(error.message))
      )
        .flatMap { layout =>
          val parameters = layout.map { case (parameter, offset) =>
            KernelParameterMetadata(
              parameter.widthBytes,
              offset,
              KernelParameterKind.Scalar,
              containsPointers = false
            )
          }
          val blockSize = parameters.lastOption.fold(0L)(parameter => parameter.offset.toLong + parameter.width.toLong)
          for {
            refinedBlockSize <- Either.cond(
              blockSize <= Int.MaxValue.toLong,
              blockSize.toInt.refineUnsafe[Positive0],
              ExecutionFailure.InvalidRequest(
                s"PTX argument block size exceeds the supported range: $blockSize"
              )
            )
            metadata <- KernelMetadata.validated(
              refinedBlockSize,
              parameters.nonNegativeSize,
              parameters
            ).leftMap(detail => ExecutionFailure.InvalidRequest(detail))
          } yield metadata
        }

    override def global(name: GlobalName): Either[ExecutionFailure, ProgramGlobal] =
      globals.get(SymbolName(name.value)).toRight(ExecutionFailure.NotFound(
        s"PTX global '${name.value}' is not present"
      ))
        .flatMap(value =>
          ProgramGlobal.validated(value.size.toLong.refineUnsafe[Positive0], value.initial)
            .leftMap(detail => ExecutionFailure.InvalidRequest(detail))
        )

    override def bindGlobal(
        name: GlobalName,
        allocation: AllocationId,
        offset: Long :| Positive0,
        allocationSize: Long :| Positive,
        memory: ExecutionMemory
    ): Either[ExecutionFailure, Unit] = for {
      definition <- globals.get(SymbolName(name.value)).toRight(ExecutionFailure.NotFound(
        s"PTX global '${name.value}' is not present"
      ))
      _ <- Either.cond(
        definition.size.toLong <= allocationSize - offset,
        (),
        ExecutionFailure.InvalidRequest(
          s"PTX global '${name.value}' does not fit its bound allocation"
        )
      )
      address <- memory.allocationAddresses.find(_.allocation == allocation).flatMap(_.address)
        .toRight(ExecutionFailure.InvalidRequest("bound PTX global requires a registered client address"))
    } yield {
      loweringState.updateAndGet(state =>
        LoweringState(
          state.globalAddresses.updated(SymbolName(name.value), address.offset(offset)),
          Map.empty
        )
      )
      ()
    }

    override def launch(
        dispatch: KernelDispatch,
        memory: ExecutionMemory,
        control: ExecutionControl
    ): Either[ExecutionFailure, Unit] =
      lowered(dispatch.kernelName).flatMap(execute(_, dispatch, memory, control))

    @tailrec
    private def lowered(name: KernelName): Either[ExecutionFailure, Kernel] = {
      val state = loweringState.get()
      state.kernels.get(name) match {
        case Some(kernel) => kernel
        case None         =>
          val kernel = entry(name).toRight(
            ExecutionFailure.NotFound(
              s"PTX kernel '${name.value}' is not present"
            )
          ).flatMap(value =>
            Lowering.lower(value, functions, state.globalAddresses, moduleSharedSymbols, entries)
              .leftMap(error => ExecutionFailure.Unsupported(error.message))
          )
          val updated = state.copy(kernels = state.kernels.updated(name, kernel))
          if (loweringState.compareAndSet(state, updated)) kernel else lowered(name)
      }
    }

    private def entry(name: KernelName): Option[Statement.Entry] = {
      val matching = entries.filter(_.name == name)
      matching.find(_.body.nonEmpty).orElse(matching.headOption)
    }
  }

  private def execute(
      kernel: Kernel,
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit] =
    for {
      _ <- Either.cond(
        dispatch.arguments.size == kernel.parameters.size,
        (),
        ExecutionFailure.InvalidRequest(
          s"PTX kernel ${kernel.name} expects ${kernel.parameters.size} arguments, found ${dispatch.arguments.size}"
        )
      )
      prepared <- prepareArguments(kernel, dispatch.arguments, dispatch.pointerRelocations, memory)
      (arguments, addressSpace) = prepared
      _ <- VirtualMachine.execute(
        kernel,
        dispatch.geometry,
        arguments,
        DynamicAddressMemory(addressSpace),
        control,
        captureTrace = false
      )
        .leftMap(error => ExecutionFailure.Failed(error.message))
    } yield ()

  private def prepareArguments(
      kernel: Kernel,
      arguments: Vec[KernelArgument],
      pointerRelocations: Vec[PointerRelocation],
      memory: ExecutionMemory
  ): Either[ExecutionFailure, (Map[ParameterName, Argument], ExecutionAddressSpace)] = {
    val registeredAllocations = memory.allocationAddresses.flatMap(address =>
      address.address.map(base => address.allocation -> (base -> address.size))
    ).toMap
    val allocationIds = (
      arguments.collect { case KernelArgument.Allocation(id, _) => id } ++ pointerRelocations.map(_.target)
    ).distinct
    val allocationBases = allocationIds.zipWithIndex.map { case (id, index) =>
      id -> registeredAllocations.get(id).fold(
        GuestAddress(Math.addExact(AllocationBase, Math.multiplyExact(index.toLong, AllocationSpan)))
      )(_._1)
    }.toMap
    val argumentBindings = Vec.from(allocationBases).sortBy(_._2.value).map { case (id, base) =>
      val span = registeredAllocations.get(id).map(_._2).getOrElse(AllocationSpan.refineUnsafe[Positive])
      AddressBinding(base, id, 0L, span)
    }
    val argumentIds    = allocationBases.keySet
    val globalBindings = memory.allocationAddresses.collect {
      case AllocationAddress(id, size, Some(address)) if !argumentIds(id) =>
        AddressBinding(address, id, 0L, size)
    }
    val bindings = argumentBindings ++ globalBindings
    val decoded  = kernel.parameters.zip(arguments).zipWithIndex.foldLeft[
      Either[ExecutionFailure, Map[ParameterName, Argument]]
    ](Right(Map.empty)) { case (result, ((parameter, argument), index)) =>
      result.flatMap { values =>
        argument match {
          case KernelArgument.Scalar(bytes) =>
            for {
              _ <- Either.cond(
                bytes.size == parameter.widthBytes,
                (),
                ExecutionFailure.InvalidRequest(
                  s"PTX argument ${parameter.name} expects ${parameter.widthBytes} bytes, found ${bytes.size}"
                )
              )
              relocated <- relocateArgument(index, bytes, pointerRelocations, allocationBases)
            } yield values.updated(parameter.name, Argument(relocated.refineUnsafe[MinLength[1]]))
          case KernelArgument.Allocation(id, offset) =>
            Either.cond(
              parameter.widthBytes == 8 && offset >= 0L && offset < AllocationSpan,
              values.updated(
                parameter.name,
                Argument(
                  LittleEndian
                    .toVec(allocationBases(id).value + offset, net.kurobako.gemstone.binary.ScalarByteWidth(8))
                    .refineUnsafe[MinLength[1]]
                )
              ),
              ExecutionFailure.InvalidRequest(
                s"allocation argument ${parameter.name} requires an eight-byte PTX parameter and representable offset"
              )
            )
          case KernelArgument.Local(size) =>
            Left(ExecutionFailure.Unsupported(
              s"PTX local argument ${parameter.name} ($size bytes) is unsupported"
            ))
        }
      }
    }
    decoded.map(values => values -> ExecutionAddressSpace(memory, bindings))
  }

  private def relocateArgument(
      index: Int,
      bytes: Vec[Byte],
      relocations: Vec[PointerRelocation],
      allocationBases: Map[AllocationId, GuestAddress]
  ): Either[ExecutionFailure, Vec[Byte]] =
    try {
      val relocated = Vec.toArray(bytes)
      relocations.foreach {
        case PointerRelocation(RelocationSource.Argument(source), sourceOffset, target, targetOffset)
            if source == index =>
          LittleEndian.write(
            Math.addExact(allocationBases(target).value, targetOffset),
            relocated,
            sourceOffset.toInt,
            ScalarByteWidth(8)
          )
        case _ => ()
      }
      Right(Vec.from(relocated))
    } catch {
      case _: ArithmeticException => Left(ExecutionFailure.InvalidRequest("PTX pointer relocation address overflows"))
    }

  private def moduleSharedSymbols(statements: Vec[Statement]): Map[SymbolName, Long] =
    statements.collect {
      case Statement.Data(DataAddressSpace.Shared, _, _, name, _, _) => name
    }.distinct.zipWithIndex.map { case (name, index) =>
      name -> Math.addExact(ModuleSharedBase, Math.multiplyExact(index.toLong, AllocationSpan))
    }.toMap

  private def globalDefinitions(
      statements: Vec[Statement]
  ): Either[ExecutionFailure, Map[SymbolName, GlobalDefinition]] =
    statements.collect {
      case Statement.Data(addressSpace, _, elementType, name, declaredCount, initializer)
          if addressSpace == DataAddressSpace.Global || addressSpace == DataAddressSpace.Const =>
        val count: Int :| Positive = declaredCount.getOrElse(1)
        (name, elementType, count, initializer)
    }.traverse { case (name, elementType, count, initializer) =>
      globalDefinition(name, elementType, count, initializer).map(name -> _)
    }.map(_.toMap)

  // Initializer decoding is a parser boundary and fails on the first malformed or unrepresentable scalar.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  private def globalDefinition(
      name: SymbolName,
      elementType: DataType,
      count: Int :| Positive,
      initializer: Vec[Operand]
  ): Either[ExecutionFailure, GlobalDefinition] = {
    val elementBytes = (elementType.widthBits / 8).refineUnsafe[Positive]
    if (count > Int.MaxValue / elementBytes)
      return Left(ExecutionFailure.InvalidRequest(
        s"PTX global '$name' exceeds the supported size"
      ))
    if (elementBytes > 8 && initializer.nonEmpty)
      return Left(ExecutionFailure.Unsupported(
        s"PTX global '$name' has an initializer wider than 64 bits"
      ))

    val size       = (elementBytes * count).refineUnsafe[Positive]
    val bytes      = Array.ofDim[Byte](size)
    val immediates = initializer.iterator.collect { case Operand.Immediate(raw) => raw.value }.take(count)
    var index      = 0
    while (immediates.hasNext) {
      val raw = immediates.next()
      initializerBits(raw, elementType.kind, elementType.widthBits) match {
        case None =>
          return Left(ExecutionFailure.InvalidRequest(
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
    Right(GlobalDefinition(size, Vec.from(bytes).refineUnsafe[MinLength[1]]))
  }

  private def initializerBits(raw: String, kind: Char, width: Int): Option[Long] =
    try
      if (kind == 'f' && width == 32) {
        if (raw.startsWith("0f") || raw.startsWith("0F")) Some(BigInt(raw.drop(2), 16).longValue)
        else Some(IEEE754.floatToBinary32(raw.toFloat).value)
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
