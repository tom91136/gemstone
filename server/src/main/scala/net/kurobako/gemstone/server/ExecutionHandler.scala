package net.kurobako.gemstone.server

import cats.syntax.all.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.vm.*
import scala.util.control.NonFatal

object ExecutionHandler {
  def apply(operation: Operation[?], service: ExecutionSession): Either[ExecutionFailure, Array[Byte]] =
    try dispatch(operation, service)
    catch { case NonFatal(error) => Left(ExecutionFailure.Failed(throwables.detail(error))) }

  private def dispatch(
      operation: Operation[?],
      service: ExecutionSession
  ): Either[ExecutionFailure, Array[Byte]] = operation match {
    case Operation.EnableHostMemory => encode(Operation.EnableHostMemory, service.enableHostMemory())
    case request @ Operation.PollHostMemory(completion)           => encode(request, service.pollHostMemory(completion))
    case request @ Operation.SupplyHostMemory(completion, ranges) =>
      encode(request, service.supplyHostMemory(completion, ranges))
    case Operation.Ping                                     => encode(Operation.Ping, Right(()))
    case request @ Operation.RegisterAllocation(kind, size) =>
      encode(request, service.registerAllocation(size, kind))
    case request @ Operation.RegisterAllocationAddress(allocation, nativeBase, hostBacked) =>
      encode(request, service.registerAllocationAddress(allocation, nativeBase, hostBacked))
    case request @ Operation.SupplyHostSync(completion, ticket, observed) =>
      encode(request, service.supplyHostSync(completion, ticket, observed))
    case request @ Operation.ReleaseAllocation(allocation) =>
      encode(request, service.releaseAllocation(allocation))
    case request @ Operation.WriteAllocation(allocation, offset, bytes) =>
      encode(request, service.write(allocation, offset, bytes))
    case request @ Operation.ReadAllocation(allocation, offset, size) =>
      encode(request, service.read(allocation, offset, size))
    case request @ Operation.LoadProgram(encoding, image) =>
      encode(request, service.load(encoding, image))
    case request @ Operation.DescribeSpecialization(image) =>
      encode(request, service.describeSpecialization(image))
    case request @ Operation.SpecializeImage(image, constants) =>
      encode(request, service.specializeImage(image, constants))
    case request @ Operation.PrepareShader(image, entry, stage, constants) =>
      encode(request, service.prepareShader(image, entry, stage, constants))
    case request @ Operation.SubmitGraphics(submission)         => encode(request, service.submitGraphics(submission))
    case request @ Operation.ReadGraphicsStatistics(completion) =>
      encode(request, service.readGraphicsStatistics(completion))
    case request @ Operation.UnloadProgram(program) =>
      encode(request, service.unload(program))
    case request @ Operation.DescribeProgram(program) =>
      encode(request, service.symbols(program))
    case request @ Operation.DescribeKernel(program, name) =>
      encode(request, service.kernel(program, name))
    case request @ Operation.DescribeImage(encoding, image, name) =>
      encode(request, service.describeImage(encoding, image, name))
    case request @ Operation.Blas(blas) =>
      encode(request, service.blas(blas))
    case request @ Operation.DescribeGlobal(program, name) =>
      encode(request, service.global(program, name))
    case request @ Operation.BindProgramGlobal(program, name, allocation, offset) =>
      encode(request, service.bindGlobal(program, name, allocation, offset))
    case request @ Operation.WriteProgramGlobal(program, name, offset, bytes) =>
      encode(request, service.writeGlobal(program, name, offset, bytes))
    case request @ Operation.Launch(
          program,
          kernelName,
          groupSize,
          groupCount,
          globalSize,
          workDim,
          globalOffset,
          privateSegmentBytes,
          groupSegmentBytes,
          arguments,
          pointerRelocations,
          discoverArgumentRelocations,
          descriptorBindings
        ) =>
      val created = KernelTask.validated(
        kernelName = kernelName,
        groupSize = groupSize,
        groupCount = groupCount,
        arguments = arguments,
        globalSize = Some(globalSize),
        privateSegmentBytes = privateSegmentBytes,
        groupSegmentBytes = groupSegmentBytes,
        pointerRelocations = pointerRelocations,
        workDim = workDim,
        globalOffset = globalOffset,
        discoverArgumentRelocations = discoverArgumentRelocations
      ).leftMap(message => ExecutionFailure.InvalidRequest(message))
      val result = created match {
        case Left(error) => Left(error)
        case Right(task) => service.launch(program, task, descriptorBindings)
      }
      encode(request, result)
    case request @ Operation.QueryCompletion(completion) =>
      encode(request, service.completion(completion))
    case request @ Operation.AwaitCompletion(completion) =>
      encode(request, service.awaitCompletion(completion))
    case request @ Operation.CompletionDirtyRanges(completion, offset) =>
      encode(
        request,
        service.completionDirtyRanges(id = completion, offset = offset, limit = ExecutionRPC.MaximumElements)
      )
    case request @ Operation.CompletionPointerRelocations(completion, offset) =>
      encode(
        request,
        service.completionPointerRelocations(id = completion, offset = offset, limit = ExecutionRPC.MaximumElements)
      )
    case request @ Operation.CompletionPrints(completion) =>
      encode(request, service.completionPrints(completion))
    case request @ Operation.ReleaseCompletion(completion) =>
      encode(request, service.releaseCompletion(completion))
  }

  private def encode[A](
      operation: Operation[A],
      result: Either[ExecutionFailure, A]
  ): Either[ExecutionFailure, Array[Byte]] =
    result.flatMap(value =>
      operation.replyCodec.encode(value, ExecutionRPC.Limits)
        .leftMap(message => ExecutionFailure.Failed(s"${operation.productPrefix} reply: $message"))
    )

}
