package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.isa.ptx.v9_3.Argument
import net.kurobako.gemstone.isa.ptx.v9_3.Interpreter
import net.kurobako.gemstone.isa.ptx.v9_3.Kernel
import net.kurobako.gemstone.isa.ptx.v9_3.Lowering
import net.kurobako.gemstone.isa.ptx.v9_3.Parser
import net.kurobako.gemstone.isa.ptx.v9_3.Statement
import net.kurobako.gemstone.memory.AddressBinding
import net.kurobako.gemstone.memory.DynamicAddressMemory
import net.kurobako.gemstone.memory.ExecutionAddressSpace
import net.kurobako.gemstone.memory.GuestAddress

import cats.syntax.all.*

import java.nio.charset.StandardCharsets

object PTXProgramLoader extends ProgramLoader {
  private val AllocationBase = 0x0000100000000000L
  private val AllocationSpan = 0x0000010000000000L

  override val format: ProgramFormat = ProgramFormat.PTX

  override def load(image: Col[Byte]): Either[ExecutionFailure, LoadedProgram] = {
    val source = String(Col.toArray(image), StandardCharsets.UTF_8)
    Parser.parse(source)
      .leftMap(error => ExecutionFailure.InvalidRequest(error.toString))
      .flatMap { program =>
        val entries   = program.statements.collect { case entry: Statement.Entry => entry }
        val functions = program.statements.collect { case function: Statement.Function => function }
        entries
          .foldLeft[Either[ExecutionFailure, Map[String, Kernel]]](Right(Map.empty))((result, entry) =>
            result.flatMap(kernels =>
              Lowering.lower(entry, functions, Map.empty, Map.empty, entries)
                .leftMap(error => ExecutionFailure.Unsupported(error.message))
                .map(kernel => kernels.updated(entry.name, kernel))
            )
          )
          .flatMap(kernels =>
            Either.cond(
              kernels.nonEmpty,
              PTXLoadedProgram(kernels),
              ExecutionFailure.InvalidRequest("PTX module contains no entry points")
            )
          )
      }
  }

  private final class PTXLoadedProgram(kernels: Map[String, Kernel]) extends LoadedProgram {
    override def launch(
        dispatch: KernelDispatch,
        memory: ExecutionMemory,
        control: ExecutionControl
    ): Either[ExecutionFailure, Unit] =
      kernels.get(dispatch.kernelName).toRight(
        ExecutionFailure.NotFound(s"PTX kernel '${dispatch.kernelName}' is not present")
      ).flatMap(execute(_, dispatch, memory, control))
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
      prepared <- prepareArguments(kernel, dispatch.arguments, memory)
      (arguments, addressSpace) = prepared
      _ <- Interpreter.execute(
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
      arguments: Col[KernelArgument],
      memory: ExecutionMemory
  ): Either[ExecutionFailure, (Map[String, Argument], ExecutionAddressSpace)] = {
    val allocationBases = arguments.collect { case KernelArgument.Allocation(id, _) => id }.distinct.zipWithIndex.map {
      case (id, index) => id ->
          GuestAddress(Math.addExact(AllocationBase, Math.multiplyExact(index.toLong, AllocationSpan)))
    }.toMap
    val bindings = Col.from(allocationBases).sortBy(_._2.value).map { case (id, base) =>
      AddressBinding(base, id, 0L, AllocationSpan)
    }
    val decoded = kernel.parameters.zip(arguments).foldLeft[
      Either[ExecutionFailure, Map[String, Argument]]
    ](Right(Map.empty)) { case (result, (parameter, argument)) =>
      result.flatMap { values =>
        argument match {
          case KernelArgument.Scalar(bytes) =>
            Either.cond(
              bytes.size == parameter.widthBytes,
              values.updated(parameter.name, Argument(bytes)),
              ExecutionFailure.InvalidRequest(
                s"PTX argument ${parameter.name} expects ${parameter.widthBytes} bytes, found ${bytes.size}"
              )
            )
          case KernelArgument.Allocation(id, offset) =>
            Either.cond(
              parameter.widthBytes == 8 && offset >= 0L && offset < AllocationSpan,
              values.updated(parameter.name, Argument(LittleEndian.toCol(allocationBases(id).value + offset, 8))),
              ExecutionFailure.InvalidRequest(
                s"allocation argument ${parameter.name} requires an eight-byte PTX parameter and representable offset"
              )
            )
          case KernelArgument.Local(size) =>
            Left(ExecutionFailure.Unsupported(s"PTX local argument ${parameter.name} ($size bytes) is unsupported"))
        }
      }
    }
    decoded.flatMap { values =>
      ExecutionAddressSpace.create(memory, bindings)
        .leftMap(ExecutionFailure.InvalidRequest.apply)
        .map(space => values -> space)
    }
  }
}
