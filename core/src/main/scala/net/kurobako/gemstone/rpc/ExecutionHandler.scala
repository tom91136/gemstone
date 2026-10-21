package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.graphics.ShaderMetadata.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.vm.*
import scala.util.control.NonFatal

/** Maps decoded RPC operations onto an execution session and encodes their replies. */
object ExecutionHandler {
  def apply(operation: Operation[?], session: ExecutionSession): Either[ExecutionFailure, Array[Byte]] =
    try dispatch(operation, session)
    catch { case NonFatal(error) => Left(ExecutionFailure.Failed(throwables.detail(error))) }

  private def dispatch(
      operation: Operation[?],
      session: ExecutionSession
  ): Either[ExecutionFailure, Array[Byte]] = operation match {
    case Operation.EnableHostMemory => encode(Operation.EnableHostMemory, session.enableHostMemory())
    case request @ Operation.PollHostMemory(completion)           => encode(request, session.pollHostMemory(completion))
    case request @ Operation.SupplyHostMemory(completion, ranges) =>
      encode(request, session.supplyHostMemory(completion, ranges))
    case Operation.Ping                               => encode(Operation.Ping, Right(()))
    case request @ Operation.RegisterAllocation(size) =>
      encode(request, session.registerAllocation(size.toIntExact.widen[Int > 0]))
    case request @ Operation.RegisterAllocationAddress(allocation, nativeBase, hostBacked) =>
      encode(request, session.registerAllocationAddress(allocation, nativeBase, hostBacked))
    case request @ Operation.SupplyHostSync(completion, ticket, observed) =>
      encode(request, session.supplyHostSync(completion, ticket, observed))
    case request @ Operation.ReleaseAllocation(allocation) =>
      encode(request, session.releaseAllocation(allocation))
    case request @ Operation.WriteAllocation(allocation, offset, bytes) =>
      encode(request, session.write(allocation, offset, bytes))
    case request @ Operation.ReadAllocation(allocation, offset, size) =>
      encode(request, session.read(allocation, offset, size))
    case request @ Operation.LoadProgram(encoding, image) =>
      encode(request, session.load(encoding, image))
    case request @ Operation.DescribeSpecialization(image) =>
      encode(request, session.describeSpecialization(image))
    case request @ Operation.SpecializeImage(image, constants) =>
      encode(request, session.specializeImage(image, constants))
    case request @ Operation.PrepareShader(image, entry, stage, constants) =>
      encode(request, session.prepareShader(ShaderStage(image, entry, stage, constants)))
    case request @ Operation.SubmitGraphics(submission)         => encode(request, session.submitGraphics(submission))
    case request @ Operation.ReadGraphicsStatistics(completion) =>
      encode(request, session.readGraphicsStatistics(completion))
    case request @ Operation.UnloadProgram(program) =>
      encode(request, session.unload(program))
    case request @ Operation.DescribeProgram(program) =>
      encode(request, session.symbols(program))
    case request @ Operation.DescribeKernel(program, name) =>
      encode(request, session.kernel(program, name))
    case request @ Operation.DescribeImage(encoding, image, name) =>
      encode(request, session.describeImage(encoding, image, name))
    case request @ Operation.DescribeGlobal(program, name) =>
      encode(request, session.global(program, name))
    case request @ Operation.BindProgramGlobal(program, name, allocation, offset) =>
      encode(request, session.bindGlobal(program, name, allocation, offset))
    case request @ Operation.WriteProgramGlobal(program, name, offset, bytes) =>
      encode(request, session.writeGlobal(program, name, offset, bytes))
    case request @ Operation.BuildKernel(program, kernelName) =>
      encode(request, session.build(program, kernelName))
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
          descriptorBindings,
          argumentBuffer,
          clusterSize,
          cooperative
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
        discoverArgumentRelocations = discoverArgumentRelocations,
        argumentBuffer = argumentBuffer,
        cluster = clusterSize,
        cooperative = cooperative
      ).left.map(ExecutionFailure.InvalidRequest(_))
      val result = created match {
        case Left(error) => Left(error)
        case Right(task) => session.launch(program, task, descriptorBindings)
      }
      encode(request, result)
    case request @ Operation.QueryCompletion(completion) =>
      encode(request, session.completion(completion))
    case request @ Operation.CompletionDirtyRanges(completion, offset) =>
      encode(
        request,
        session.completionDirtyRanges(id = completion, offset = offset, limit = ExecutionRPC.MaximumElements)
      )
    case request @ Operation.CompletionPointerRelocations(completion, offset) =>
      encode(
        request,
        session.completionPointerRelocations(id = completion, offset = offset, limit = ExecutionRPC.MaximumElements)
      )
    case request @ Operation.CompletionPrints(completion) =>
      encode(request, session.completionPrints(completion))
    case request @ Operation.ReleaseCompletion(completion) =>
      encode(request, session.releaseCompletion(completion))
  }

  private def encode[A](
      operation: Operation[A],
      result: Either[ExecutionFailure, A]
  ): Either[ExecutionFailure, Array[Byte]] =
    result.flatMap(value =>
      operation.replyCodec.encode(value, ExecutionRPC.Limits)
        .left.map(message => ExecutionFailure.Failed(s"${operation.productPrefix} reply: $message"))
    )

}
