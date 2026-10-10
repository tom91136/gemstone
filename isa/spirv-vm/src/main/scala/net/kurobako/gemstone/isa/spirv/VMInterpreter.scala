package net.kurobako.gemstone.isa.spirv

import VMInvocation.*
import VMMemory.*
import VMProgram.*
import VMShaderInterface.*
import cats.syntax.all.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.control.NonFatal

/** Typed SPIR-V kernel execution. Guest memory is accessed through the address space; unbound program globals,
  * Workgroup and private storage use dispatch-, group- and invocation-owned apertures.
  */
private object VMInterpreter {
  import Operand.*

  private inline val LocalArgumentAlignment = 128

  def execute(
      program: VMProgram,
      dispatch: KernelTask,
      memory: AddressSpace,
      control: TaskControl,
      workgroups: Workgroups,
      handler: Event => Either[Fault, Unit]
  ): Either[Fault, Unit] =
    try
      if (dispatch.argumentBuffer.nonEmpty)
        Left(Fault.Unsupported("SPIR-V does not support allocation-backed argument buffers"))
      else
        program.entryPoints.get(dispatch.kernelName)
          .toRight(Fault.NotFound(s"SPIR-V entry point ${dispatch.kernelName} does not exist"))
          .flatMap(functionId =>
            program.functions.get(functionId)
              .toRight(Fault.InvalidRequest(s"SPIR-V entry point ${dispatch.kernelName} has no function body"))
          )
          .flatMap(executeDispatch(program, _, dispatch, memory, control, workgroups, handler))
    catch {
      case NonFatal(error) =>
        Left(Fault.InvalidRequest(s"malformed SPIR-V execution state: ${throwables.detail(error)}"))
    }

  /** Per-dispatch reuse of invocation machines: one for independent lanes, one per lane for collective groups. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  private class Reuse(lanes: Int) {
    var execution: InvocationState | Null          = null
    val lanesStates: Array[InvocationState | Null] = Array.ofDim(lanes)
  }

  /** Places the non-builtin globals of the selected storage classes: bound globals at their allocation, the rest in
    * `layout`.
    */
  private def placeGlobals(
      program: VMProgram,
      memory: AddressSpace,
      layout: Layout,
      include: Int => Boolean
  ): Either[Fault, Map[Int, VMValue]] =
    program.globalImages(include).flatMap { images =>
      images.foldLeft[Either[Fault, Map[Int, VMValue]]](Right(Map.empty)) { (result, image) =>
        result.flatMap { values =>
          val global  = image.global
          val pointee = program.pointerPointee(global.pointerType)
          val pointer = program.globalBinding(image.variable) match {
            case Some(binding) =>
              memory.address(binding.region, binding.offset).map(address =>
                Pointer(address.raw, pointee, global.storageClass)
              )
            case None => layout.append(image.bytes, program.typeAlignment(pointee).raw).map { address =>
                val bounded = program.vulkan && global.storageClass == StorageClass.Private
                Pointer(
                  address,
                  pointee,
                  global.storageClass,
                  Option.when(bounded)(address -> (address + image.bytes.length))
                )
              }
          }
          pointer.map(value => values.updated(image.variable, VMValue.Address(global.pointerType, value)))
        }
      }.map { values =>
        // Bound globals received their pointer words when bound.
        for {
          image <- images if program.globalBinding(image.variable).isEmpty
          fixup <- image.fixups
          case VMValue.Address(_, holder) <- values.get(image.variable)
          case VMValue.Address(_, target) <- values.get(fixup.target)
        } layout.patch(holder.address + fixup.offset, LittleEndian.toArray(target.address + fixup.addend, 8))
        values
      }
    }

  private def placeSamplers(program: VMProgram, layout: Layout): Either[Fault, Map[Int, VMValue]] =
    program.samplerConstants.traverse { (id, typeId, words) =>
      val bytes = Array.ofDim[Byte](words.size * 4)
      words.zipWithIndex.foreach((word, index) => LittleEndian.write(word, bytes, index * 4, 4))
      layout.append(Vec.unsafeWrapArray(bytes), 4).map(address =>
        id -> VMValue.Address(
          typeId,
          Pointer(address, typeId, StorageClass.UniformConstant, Some(address -> (address + bytes.length)))
        )
      )
    }.map(_.toMap)

  /** A builtin variable written into private storage at the start of each invocation. */
  private case class PrivateBuiltin(offset: Long >= 0, typeId: Int, builtin: Int)

  private def executeDispatch(
      program: VMProgram,
      function: Function,
      dispatch: KernelTask,
      memory: AddressSpace,
      control: TaskControl,
      workgroups: Workgroups,
      handler: Event => Either[Fault, Unit]
  ): Either[Fault, Unit] = {
    val shader       = program.vulkanEntries.contains(dispatch.kernelName)
    val arguments    = if (shader) argumentVariables(program, dispatch.kernelName) else Vec.empty[Int]
    val expected     = if (shader) arguments.size else function.parameters.size
    val globalLayout = Layout(GlobalAperture)
    for {
      _ <- Either.cond(
        expected == dispatch.arguments.size,
        (),
        Fault.InvalidRequest(
          s"SPIR-V entry point ${dispatch.kernelName} expects $expected arguments, received ${dispatch.arguments.size}"
        )
      )
      placed <- placeGlobals(
        program,
        memory,
        globalLayout,
        storageClass => storageClass != StorageClass.Workgroup && storageClass != StorageClass.Private
      )
      addressConstants <- program.addressConstants(placed)
      samplers         <- placeSamplers(program, globalLayout)
      initialGlobals = placed ++ addressConstants ++ samplers
      globalValues <-
        if (!shader) Right(initialGlobals)
        else descriptorValues(program, memory, arguments.zip(dispatch.arguments), initialGlobals)
      storage  = Storage(memory, dispatch.pointerRelocations, globalLayout.result(), handler)
      launcher = Launcher(program, memory, control, workgroups, globalValues, storage)
      _ <- launcher.launch(function, dispatch, shader) { (workgroupLayout, privateLayout) =>
        if (shader) Right(Map.empty[Int, VMValue])
        else bindParameters(program, function, dispatch, memory, workgroupLayout, privateLayout)
      }
      _ <- if (shader) Right(()) else launcher.drain()
    } yield ()
  }

  /** Launches kernels that share a dispatch's placed globals and storage: the dispatch itself, then the kernels its
    * work-items enqueue.
    */
  private class Launcher(
      program: VMProgram,
      memory: AddressSpace,
      control: TaskControl,
      workgroups: Workgroups,
      globalValues: Map[Int, VMValue],
      storage: Storage
  ) {
    private lazy val entries = program.entryPoints.map(_.swap)

    def launch(function: Function, dispatch: KernelTask, shader: Boolean)(
        parameters: (Layout, Layout) => Either[Fault, Map[Int, VMValue]]
    ): Either[Fault, Unit] = {
      val workgroupLayout = Layout(WorkgroupAperture)
      val privateLayout   = Layout(PrivateAperture)
      for {
        privateGlobals   <- placeGlobals(program, memory, privateLayout, _ == StorageClass.Private)
        workgroupGlobals <- placeGlobals(program, memory, workgroupLayout, _ == StorageClass.Workgroup)
        privateBuiltins  <- privateBuiltins(program, dispatch.kernelName, shader, globalValues, privateLayout)
        bound            <- parameters(workgroupLayout, privateLayout)
        template = program.registers(
          globalValues ++ privateGlobals ++ workgroupGlobals ++ privateBuiltins.map(_._1) ++ bound
        )
        _ <- run(
          program,
          function,
          dispatch,
          memory,
          control,
          workgroups,
          globalValues,
          template,
          privateBuiltins.map(_._2),
          privateLayout.result(),
          workgroupLayout.result(),
          storage
        )
      } yield ()
    }

    /** Runs enqueued commands as their wait lists complete, until none remain. */
    @tailrec final def drain(): Either[Fault, Unit] = {
      val queue = storage.deviceQueue
      val state = MemoryState(storage)
      queue.next(state.write) match {
        case Left(error)                  => Left(error)
        case Right(None) if queue.isEmpty => Right(())
        case Right(None)                  =>
          Left(Fault.InvalidRequest("enqueued commands wait on events that never complete"))
        case Right(Some(command)) =>
          queue.begin(command)
          val ran = command match {
            case kernel: DeviceQueue.Command.Kernel => enqueued(kernel)
            case DeviceQueue.Command.Marker(_, _)   => Right(())
          }
          ran.flatMap(_ => queue.finish(command, state.write)) match {
            case Left(error) => Left(error)
            case Right(())   => drain()
          }
      }
    }

    private def enqueued(kernel: DeviceQueue.Command.Kernel): Either[Fault, Unit] =
      for {
        function <- program.functions.get(kernel.function)
          .toRight(Fault.InvalidRequest(s"enqueued block %${kernel.function} has no function body"))
        name <- entries.get(kernel.function)
          .toRight(Fault.InvalidRequest(s"enqueued block %${kernel.function} is not a kernel"))
        _ <- Either.cond(
          function.parameters.size == kernel.locals.size + 1,
          (),
          Fault.InvalidRequest(s"enqueued block $name expects ${function.parameters.size - 1} local sizes")
        )
        task <- enqueuedTask(name, kernel.range)
        _    <- launch(function, task, shader = false) { (workgroupLayout, privateLayout) =>
          for {
            literal <- privateLayout.append(Vec.unsafeWrapArray(kernel.literal), math.max(1, kernel.literalAlignment))
            locals  <- kernel.locals.traverse(size => workgroupLayout.reserve(size, LocalArgumentAlignment))
          } yield (literal +: locals).zip(function.parameters.toVector).map { case (address, (parameter, typeId)) =>
            val storageClass = program.runtimeType(typeId) match {
              case Some(RuntimeType.Pointer(storageClass, _)) => storageClass
              case _                                          => StorageClass.Generic
            }
            parameter -> VMValue.Address(typeId, Pointer(address, program.pointerPointee(typeId), storageClass))
          }.toMap
        }
      } yield ()

    private def enqueuedTask(name: NonEmpty[String], range: DeviceQueue.NDRange): Either[Fault, KernelTask] = {
      val dimensions                                     = range.dimensions
      val group                                          = DeviceEnqueueOps.groupShape(range)
      def extent(values: Vector[Long], index: Int): Long = if (index < dimensions) values(index) else 1L
      def positive(value: Long): Either[Fault, Int > 0]  =
        value.toInt.refine[Int > 0].leftMap(_ => Fault.InvalidRequest(s"enqueued ND-range extent $value is invalid"))
      def coordinate(value: Long): Either[Fault, Int >= 0] =
        value.toInt.refine[Int >= 0].leftMap(_ => Fault.InvalidRequest(s"enqueued ND-range offset $value is invalid"))
      for {
        size  <- (0 until 3).toVector.traverse(i => positive(extent(group, i)))
        count <- (0 until 3).toVector.traverse(i =>
          positive((extent(range.global, i) + extent(group, i) - 1L) / extent(group, i))
        )
        global <- (0 until 3).toVector.traverse(i =>
          extent(range.global, i).refine[Long > 0].leftMap(_ => Fault.InvalidRequest("enqueued ND-range is empty"))
        )
        offset  <- (0 until 3).toVector.traverse(i => coordinate(if (i < dimensions) range.offset(i) else 0L))
        workDim <-
          dimensions.refine[Int in 1 ~ 3].leftMap(_ => Fault.InvalidRequest("enqueued ND-range has no dimensions"))
        task <- KernelTask.validated(
          name,
          Dimensions(size(0), size(1), size(2)),
          Dimensions(count(0), count(1), count(2)),
          Vec.empty,
          globalSize = Some(GlobalDimensions(global(0), global(1), global(2))),
          workDim = workDim,
          globalOffset = Coordinates(offset(0), offset(1), offset(2))
        ).leftMap(detail => Fault.InvalidRequest(detail))
      } yield task
    }
  }

  /** Vulkan descriptor variables address their bound ranges; arrayed bindings collect one element per argument. */
  private def descriptorValues(
      program: VMProgram,
      memory: AddressSpace,
      bindings: Vec[(Int, KernelTask.Argument)],
      initial: Map[Int, VMValue]
  ): Either[Fault, Map[Int, VMValue]] =
    bindings.foldLeft[Either[Fault, Map[Int, VMValue]]](Right(initial)) {
      case (result, (variable, KernelTask.Argument.Memory(allocation, offset, range))) =>
        for {
          values <- result
          global <-
            program.globals.get(variable).toRight(Fault.InvalidRequest(s"SPIR-V descriptor %$variable has no variable"))
          address <- memory.address(allocation, offset).map(_.raw)
        } yield {
          val pointee = program.pointerPointee(global.pointerType)
          val leaf = Pointer(address, pointee, global.storageClass, range.map(size => address -> (address + size.raw)))
          val pointer = program.runtimeType(pointee) match {
            case Some(RuntimeType.Array(member, _)) if program.descriptorBindings.contains(variable) =>
              val previous = values.get(variable).collect {
                case VMValue.Address(_, pointer) => pointer.descriptors.getOrElse(Vector.empty)
              }.getOrElse(Vector.empty)
              leaf.copy(descriptors = Some(previous :+ leaf.copy(pointee = member)))
            case _ => leaf
          }
          values.updated(variable, VMValue.Address(global.pointerType, pointer))
        }
      case (_, (variable, _)) => Left(Fault.InvalidRequest(s"SPIR-V descriptor %$variable requires a memory argument"))
    }

  /** Builtins visible to the entry that no stage record backs; each invocation writes them into private storage. */
  private def privateBuiltins(
      program: VMProgram,
      entry: NonEmpty[String],
      shader: Boolean,
      globalValues: Map[Int, VMValue],
      layout: Layout
  ): Either[Fault, Vector[((Int, VMValue), PrivateBuiltin)]] = {
    val interfaces = program.entryPointInterfaces.getOrElse(entry, Set.empty)
    program.globals.toVector.filter { case (variable, global) =>
      program.builtins.contains(variable) && !globalValues.contains(variable) &&
      (!shader || (global.storageClass != StorageClass.Input && global.storageClass != StorageClass.Output) ||
        interfaces.contains(variable))
    }.traverse { case (variable, global) =>
      val pointee = program.pointerPointee(global.pointerType)
      layout.reserve(program.typeSize(pointee).raw.toLong, program.typeAlignment(pointee).raw).map(address =>
        (
          variable -> VMValue.Address(global.pointerType, Pointer(address, pointee, global.storageClass)),
          PrivateBuiltin(offsetOf(address), pointee, program.builtins(variable))
        )
      )
    }
  }

  private def bindParameters(
      program: VMProgram,
      function: Function,
      dispatch: KernelTask,
      memory: AddressSpace,
      workgroupLayout: Layout,
      privateLayout: Layout
  ): Either[Fault, Map[Int, VMValue]] =
    KernelArguments.resolve(dispatch, memory).flatMap(resolved =>
      function.parameters.zipWithIndex.toVector.traverse { case ((parameterId, parameterType), index) =>
        val relocatedAtZero = dispatch.pointerRelocations.exists {
          case KernelTask.PointerRelocation(KernelTask.RelocationSource.Argument(source), offset, _, _) =>
            source.raw == index && offset.raw == 0L
          case _ => false
        }
        val bound = resolved(index) match {
          case KernelArguments.Resolved.Local(size) =>
            workgroupLayout.reserve(size.raw, LocalArgumentAlignment).map(address =>
              Array.emptyByteArray -> Some(address)
            )
          case KernelArguments.Resolved.Bytes(bytes) => Right(Vec.toArray(bytes.raw) -> None)
        }
        bound.flatMap { case (bytes, local) =>
          MemoryOps.bindArgument(
            program,
            parameterType,
            program.byValueParameters.contains(parameterId),
            dispatch.arguments(index),
            bytes,
            relocatedAtZero,
            local,
            privateLayout
          ).map(parameterId -> _)
        }
      }.map(_.toMap)
    )

  // Work-item scheduling reuses invocation machines and mutable loop state to keep the dispatch hot path lean.
  @SuppressWarnings(Array(
    "scalafix:DisableSyntax.null",
    "scalafix:DisableSyntax.var",
    "scalafix:DisableSyntax.return"
  ))
  private def run(
      program: VMProgram,
      function: Function,
      dispatch: KernelTask,
      memory: AddressSpace,
      control: TaskControl,
      workgroups: Workgroups,
      globalValues: Map[Int, VMValue],
      template: InvocationValues,
      builtins: Vector[PrivateBuiltin],
      privateImage: Array[Byte],
      workgroupImage: Array[Byte],
      storage: Storage
  ): Either[Fault, Unit] = {
    val geometry          = dispatch.geometry
    val stageInterfaces   = stageInterfaceVariables(program, dispatch.kernelName).toSet
    val stageInitialisers = stageInterfaces.toVector.flatMap { variable =>
      program.globals.get(variable).filter(_.storageClass == StorageClass.Output).flatMap(_.initialiser).map {
        initialiser => variable -> program.encode(program.constants(initialiser))
      }
    }
    val fragment = program.module.instructions.exists {
      case Instruction.EntryPoint(model, entry, _, _) =>
        unsigned(model) == ExecutionModel.Fragment && id(entry) == function.id
      case _ => false
    }
    val helperVariable =
      stageInterfaces.find(variable => program.builtins.get(variable).contains(BuiltIn.HelperInvocation))
    val maskVariable = stageInterfaces.find(variable =>
      program.builtins.get(variable).contains(BuiltIn.SampleMask) &&
        program.globals.get(variable).exists(_.storageClass == StorageClass.Output)
    )
    val groupSize  = geometry.groupSize
    val totalLocal = geometry.groupVolume.raw
    val subgroup   = program.subgroupSizes.get(function.id).fold(
      if (program.vulkan) 32 else math.max(1, math.min(32, totalLocal))
    )(_.raw)
    val enqueuedSubgroups    = (totalLocal.toLong + subgroup.toLong - 1L) / subgroup.toLong
    val hasStaticCollectives = program.collectiveFunctions.contains(function.id)
    val hasCollectives       = hasStaticCollectives || memory.defersHostReads
    val parallel             = workgroups.shouldParallel(geometry.groupTotal, geometry.groupVolume.toLong)
    val state                = MemoryState(storage)
    val sequential           = Reuse(totalLocal)
    val ops                  = instrumented(program, dispatch.kernelName, control.observer)

    def runGroup(group: Coordinates, groupState: MemoryState, reuse: Reuse): Either[Fault, Unit] = {
      val activeGroupSize = geometry.activeGroupSize(group) match {
        case Left(message) => return Left(Fault.InvalidRequest(message))
        case Right(value)  => value
      }
      val activeLocalCount = activeGroupSize.volumeInt match {
        case Left(message) => return Left(Fault.InvalidRequest(message))
        case Right(value)  => value.raw
      }
      groupState.workgroup = if (workgroupImage.isEmpty) workgroupImage else workgroupImage.clone()
      val executions   = mutable.ArrayBuffer.empty[InvocationState]
      val numSubgroups = (activeLocalCount.toLong + subgroup.toLong - 1L) / subgroup.toLong
      var linearLocal  = 0
      while (linearLocal < activeLocalCount) {
        // The loop lower bound proves the linear local index is non-negative.
        val local  = activeGroupSize.coordinates(linearLocal.toLong.unsafe[Long >= 0])
        val global = geometry.globalCoordinates(group, local)
        if (geometry.containsGlobal(global)) {
          val subgroupIndex = linearLocal / subgroup
          val invocation    = Invocation(
            group,
            local,
            global,
            geometry,
            activeGroupSize,
            if (program.vulkan) subgroup else math.min(subgroup, activeLocalCount - subgroupIndex * subgroup),
            subgroup,
            numSubgroups,
            enqueuedSubgroups,
            subgroupIndex,
            linearLocal % subgroup
          )
          // Stage arguments are arrays of invocation records: each invocation addresses its own slice.
          val stageValues = stageInterfaces.view.flatMap { variable =>
            globalValues.get(variable).collect { case VMValue.Address(pointerType, pointer) =>
              variable -> VMValue.Address(
                pointerType,
                pointer.copy(address = pointer.address + global.x.raw * program.typeSize(pointer.pointee).raw)
              )
            }
          }.toMap
          // Output initialisers apply to every record, including those never explicitly stored.
          var initialiser = 0
          while (initialiser < stageInitialisers.size) {
            val (variable, bytes) = stageInitialisers(initialiser)
            initialiser += 1
            stageValues.get(variable) match {
              case Some(VMValue.Address(_, pointer)) => bytes.flatMap(groupState.write(pointer, _)) match {
                  case Left(error) => return Left(error)
                  case Right(())   => ()
                }
              case _ => return Left(Fault.InvalidRequest(s"SPIR-V output %$variable has no invocation record"))
            }
          }
          val reused    = if (hasStaticCollectives) reuse.lanesStates(linearLocal) else reuse.execution
          val execution = prepareInvocation(
            program,
            function,
            builtins,
            invocation,
            stageValues,
            reused,
            template,
            privateImage
          ) match {
            case Left(error)  => return Left(error)
            case Right(value) => value
          }
          execution.fragmentInvocation = fragment
          execution.sampleMask = maskVariable.flatMap(stageValues.get).collect { case VMValue.Address(_, pointer) =>
            pointer
          }
          execution.fragmentQuad = fragment && groupSize.x.raw == 4 && groupSize.y.raw == 1 && groupSize.z.raw == 1
          execution.subgroupReconvergence = program.usesNonUniformCollectives && !fragment
          execution.helperInvocation = helperVariable.flatMap(stageValues.get) match {
            case Some(VMValue.Address(_, pointer)) => memory.awaitRead(groupState.readScalar(pointer, 1)) match {
                case Left(error)  => return Left(error)
                case Right(value) => value != 0
              }
            case _ => false
          }
          if (hasStaticCollectives) {
            reuse.lanesStates(linearLocal) = execution
            executions += execution
          } else
            // Deferred host reads retain a frame only when the lane actually suspends.
            resumeInvocation(program, ops, execution, groupState, control) match {
              case Left(error)                      => return Left(located(error, dispatch.kernelName, execution))
              case Right(()) if execution.completed => reuse.execution = execution
              case Right(()) if (hasCollectives && execution.memoryPending) || execution.preempted =>
                executions += execution
                reuse.execution = null
              case Right(()) =>
                return Left(
                  Fault.InvalidRequest(
                    "SPIR-V program suspended at a collective absent from its static instruction set"
                  )
                )
            }
        }
        linearLocal += 1
      }
      if (hasCollectives || executions.nonEmpty)
        runWorkgroup(program, dispatch.kernelName, ops, executions, groupState, control)
      else Right(())
    }

    Dispatch.forEachGroup(geometry, workgroups) { (_, group) =>
      if (parallel) runGroup(group, MemoryState(storage), Reuse(totalLocal))
      else runGroup(group, state, sequential)
    }
  }

  // Invocation setup fills the machine frame sequentially and fails fast on invalid builtins.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  private def prepareInvocation(
      program: VMProgram,
      function: Function,
      builtins: Vector[PrivateBuiltin],
      invocation: Invocation,
      bindings: Map[Int, VMValue],
      reused: InvocationState | Null,
      template: InvocationValues,
      privateImage: Array[Byte]
  ): Either[Fault, InvocationState] = {
    val execution =
      if (reused == null)
        InvocationState(
          invocation,
          template.copy(),
          function,
          function.start + 1,
          -1,
          -1,
          mutable.ArrayDeque.empty[CallFrame]
        )
      else {
        reused.reset(invocation, function)
        reused
      }
    bindings.foreach { case (variable, value) => execution.values.update(variable, value) }
    execution.storage.reset(privateImage)
    var builtin = 0
    while (builtin < builtins.size) {
      val PrivateBuiltin(offset, typeId, kind) = builtins(builtin)
      builtin += 1
      builtinValue(program, typeId, kind, invocation) match {
        case Left(error)                   => return Left(error)
        case Right(VMValue.Bits(_, lanes)) => program.writeLeaves(typeId, lanes, execution.storage.bytes, offset)
        case Right(VMValue.Address(_, _))  => return Left(Fault.InvalidRequest("SPIR-V builtin is not numeric"))
      }
    }
    Right(execution)
  }

  private val InstructionCompleted: Either[Fault, Unit] = Right(())

  /** Instructions run between budget charges; the shared allowance is charged after they execute. */
  private val ChargeInterval = 1024
  private val SliceCharges   = 64

  // The instruction loop exits as soon as the invocation completes, suspends, or faults. Executed instructions are
  // charged in batches so lanes do not contend on the shared allowance; a charge precedes every exit, so an exhausted
  // budget still wins over the outcome of the instructions it no longer covered.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def resumeInvocation(
      program: VMProgram,
      ops: Array[Op],
      execution: InvocationState,
      memory: MemoryState,
      control: TaskControl
  ): Either[Fault, Unit] = {
    memory.helperInvocation = execution.helperInvocation
    memory.invocation = execution.storage
    execution.preempted = false
    var executed                      = 0
    var slices                        = 0
    def charge(): Either[Fault, Unit] =
      if (executed == 0) InstructionCompleted
      else {
        val charged = control.checkpoint(executed.toLong.unsafe[Long > 0])
        executed = 0
        charged
      }
    while (execution.pc < execution.currentFunction.end) {
      val result = ops(execution.pc).run(execution, memory)
      executed += 1
      result match {
        case Left(AddressSpace.Pending) =>
          execution.awaitMemory()
          return charge()
        case Left(error: Fault) => return charge().flatMap(_ => Left(error))
        case Right(_)           => ()
      }
      if (execution.yielded) return charge()
      if (executed == ChargeInterval) charge() match {
        case Left(fault) => return Left(fault)
        case Right(())   =>
          slices += 1
          // Lanes that run on, such as spin-waits, yield so other subgroups make independent forward progress.
          if (slices == SliceCharges) {
            execution.preempted = true
            return Right(())
          }
      }
    }
    charge().flatMap(_ =>
      Left(Fault.InvalidRequest(
        s"SPIR-V function %${execution.currentFunction.id} " +
          s"(${program.names.getOrElse(execution.currentFunction.id, "unnamed")}) reached OpFunctionEnd without OpReturn"
      ))
    )
  }

  private def location(kernel: NonEmpty[String], execution: InvocationState): Fault.Location = {
    val invocation = execution.invocation
    val lane       = (invocation.local(2) * invocation.workgroupSize(1) + invocation.local(1)) *
      invocation.workgroupSize(0) + invocation.local(0)
    Fault.Location(
      kernel,
      None,
      Some(execution.pc.toLong.unsafe[Long >= 0]),
      Some(invocation.groupId),
      Some(lane.toInt.unsafe[Int >= 0])
    )
  }

  private def located(fault: Fault, kernel: NonEmpty[String], execution: InvocationState): Fault =
    fault.at(location(kernel, execution))

  /** The program's ops, each preceded by a call to `observer` when one is attached. */
  private def instrumented(program: VMProgram, kernel: NonEmpty[String], observer: Option[LaunchObserver]): Array[Op] =
    observer.fold(program.ops)(observer => program.ops.map(Observed(_, kernel, observer)))

  private class Observed(op: Op, kernel: NonEmpty[String], observer: LaunchObserver) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      observer.instruction(location(kernel, execution), View(execution)).flatMap(_ => op.run(execution, memory))
  }

  private class View(execution: InvocationState) extends InvocationView {
    def function: Int = execution.currentFunction.id

    def value(id: Int): Option[InvocationView.Value] = execution.values.get(id).map {
      case VMValue.Bits(_, lanes)      => InvocationView.Value.Bits(Vec.from(lanes))
      case VMValue.Address(_, pointer) => InvocationView.Value.Address(pointer.address)
    }
  }

  // Workgroup progress mutates invocation machines in place and stops at the first fault or deadlock.
  @SuppressWarnings(
    Array(
      "scalafix:DisableSyntax.var",
      "scalafix:DisableSyntax.return"
    )
  )
  private def runWorkgroup(
      program: VMProgram,
      kernel: NonEmpty[String],
      ops: Array[Op],
      executions: mutable.ArrayBuffer[InvocationState],
      memory: MemoryState,
      control: TaskControl
  ): Either[Fault, Unit] = {
    while (executions.exists(!_.completed)) {
      var index = 0
      while (index < executions.size) {
        val execution = executions(index)
        if (
          !execution.completed && execution.suspended.isEmpty && execution.quadRequest.isEmpty &&
          execution.reconvergenceJoin.isEmpty
        )
          resumeInvocation(program, ops, execution, memory, control) match {
            case Left(error) => return Left(located(error, kernel, execution))
            case Right(())   => ()
          }
        index += 1
      }
      val hadPendingMemory = executions.exists(_.memoryPending)
      if (hadPendingMemory) {
        memory.flushHostReads() match {
          case Left(error) => return Left(error)
          case Right(())   => executions.foreach(_.memoryReady())
        }
      }
      // A deferred read is a transport suspension, not subgroup divergence.
      // Resume those lanes before choosing the active set at a collective.
      if (!hadPendingMemory) CollectiveOps.resolve(program, executions, memory) match {
        case Left(error) => return Left(error)
        case Right(0) if !hadPendingMemory && executions.exists(!_.completed) && !executions.exists(_.preempted) =>
          val waiting = executions.view
            .filter(!_.completed)
            .map(execution =>
              execution.suspended match {
                case Some(request) =>
                  s"local=${execution.invocation.vector(execution.invocation.local(_)).mkString("(", ",", ")")}:" +
                    s"${request.kind}@%${request.function}:${request.pc}/scope=${request.scope}"
                case None =>
                  s"local=${execution.invocation.vector(execution.invocation.local(_)).mkString("(", ",", ")")}:running"
              }
            )
            .take(8)
            .mkString(", ")
          return Left(
            Fault.InvalidRequest(
              s"SPIR-V collective participation diverged within a workgroup: $waiting"
            )
          )
        case Right(_) => ()
      }
    }
    Right(())
  }

  private def builtinValue(
      program: VMProgram,
      typeId: Int,
      builtin: Int,
      invocation: Invocation
  ): Either[Fault, VMValue] = {
    val lanes: Either[Fault, Array[Long]] = builtin match {
      case BuiltIn.NumWorkgroups                 => Right(invocation.vector(invocation.numWorkgroups(_)))
      case BuiltIn.WorkgroupSize                 => Right(invocation.vector(invocation.workgroupSize(_)))
      case BuiltIn.WorkgroupId if program.vulkan =>
        Right(Array(
          invocation.group(0) + invocation.globalOffset(0) / invocation.enqueuedWorkgroupSize(0),
          invocation.group(1) + invocation.globalOffset(1) / invocation.enqueuedWorkgroupSize(1),
          invocation.group(2) + invocation.globalOffset(2) / invocation.enqueuedWorkgroupSize(2)
        ))
      case BuiltIn.WorkgroupId          => Right(invocation.vector(invocation.group(_)))
      case BuiltIn.LocalInvocationId    => Right(invocation.vector(invocation.local(_)))
      case BuiltIn.GlobalInvocationId   => Right(invocation.vector(invocation.global(_)))
      case BuiltIn.LocalInvocationIndex => Right(Array(
          (invocation.local(2) * invocation.workgroupSize(1) + invocation.local(1)) * invocation.workgroupSize(0) +
            invocation.local(0)
        ))
      case BuiltIn.WorkDim               => Right(Array(invocation.workDim.toLong))
      case BuiltIn.GlobalSize            => Right(invocation.vector(invocation.globalSize(_)))
      case BuiltIn.EnqueuedWorkgroupSize => Right(invocation.vector(invocation.enqueuedWorkgroupSize(_)))
      case BuiltIn.GlobalOffset          => Right(invocation.vector(invocation.globalOffset(_)))
      case BuiltIn.GlobalLinearId        => Right(
          Array(
            (invocation.global(0) - invocation.globalOffset(0)) + invocation.globalSize(0) *
              ((invocation.global(1) - invocation.globalOffset(1)) +
                invocation.globalSize(1) * (invocation.global(2) - invocation.globalOffset(2)))
          )
        )
      case BuiltIn.WorkgroupLinearId => Right(
          Array(
            invocation.group(0) + invocation.numWorkgroups(0) *
              (invocation.group(1) + invocation.numWorkgroups(1) * invocation.group(2))
          )
        )
      case maskKind @ (BuiltIn.SubgroupEqMask | BuiltIn.SubgroupGeMask | BuiltIn.SubgroupGtMask | BuiltIn.SubgroupLeMask | BuiltIn.SubgroupLtMask) =>
        Right(Array.tabulate(4) { word =>
          (0 until 32).foldLeft(0L) { (bits, bit) =>
            val lane     = word * 32 + bit
            val selected = maskKind match {
              case BuiltIn.SubgroupEqMask => lane == invocation.subgroupLocal
              case BuiltIn.SubgroupGeMask => lane >= invocation.subgroupLocal
              case BuiltIn.SubgroupGtMask => lane > invocation.subgroupLocal
              case BuiltIn.SubgroupLeMask => lane <= invocation.subgroupLocal
              case _                      => lane < invocation.subgroupLocal
            }
            if (lane < invocation.maxSubgroupSize && selected) bits | (1L << bit) else bits
          }
        })
      // The Vulkan runtime exposes one physical device in each device group.
      case BuiltIn.DeviceIndex if program.vulkan => Right(Array(0L))
      case BuiltIn.SubgroupSize                  => Right(Array(invocation.subgroupSize.toLong))
      case BuiltIn.SubgroupMaxSize               => Right(Array(invocation.maxSubgroupSize.toLong))
      case BuiltIn.NumSubgroups                  => Right(Array(invocation.numSubgroups))
      case BuiltIn.NumEnqueuedSubgroups          => Right(Array(invocation.enqueuedSubgroups))
      case BuiltIn.SubgroupId                    => Right(Array(invocation.subgroupIndex.toLong))
      case BuiltIn.SubgroupLocalInvocationId     => Right(Array(invocation.subgroupLocal.toLong))
      case value                                 => Left(Fault.Unsupported(s"SPIR-V BuiltIn $value is not implemented"))
    }
    lanes.flatMap(values =>
      program.integerShape(typeId) match {
        case Some((width, count)) if count == values.length =>
          Right(VMValue.Bits(typeId, values.map(_ & mask(width))))
        case Some((_, count)) =>
          Left(
            Fault.InvalidRequest(
              s"SPIR-V BuiltIn $builtin requires ${values.length} integer lanes, result type %$typeId has $count"
            )
          )
        case None =>
          Left(
            Fault.InvalidRequest(
              s"SPIR-V BuiltIn $builtin result type %$typeId is not an integer scalar or vector"
            )
          )
      }
    )
  }

}
