package net.kurobako.gemstone.isa.amdgpu

import VMDispatchMemory.*
import cats.syntax.all.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

object LoadedProgram {
  def load(program: CodeObject, workgroups: Workgroups = Workgroups.Sequential): LoadedProgram =
    LoadedProgram(program, workgroups)

  // Compilation reads neither the relocated globals nor the launch configuration, so decoded kernels are shared by every
  // program of the image; the kernels hold sourceless code that each program rebinds to its own forked index.
  private case class Indexed(
      index: ELF.Index,
      aliases: Map[String, String],
      metadata: Map[String, KernelMetadata],
      layouts: Map[String, KernargLayout],
      kernelSymbols: Vec[KernelSymbol],
      compiler: VMCompiler,
      kernels: Memo[String, Either[Fault, VMIR.Kernel]]
  )

  // Only immutable load data is cached; every program forks the index for its own relocated globals and kernels.
  private val images = ImageCache[Fault, Indexed]()

  private def index(program: CodeObject): Either[Fault, ImageCache.Lease[Indexed]] =
    images.acquire(ImageKey(program.image))(indexUncached(program))

  private def unbound(kernel: CodeObjectKernel): CodeObjectKernel =
    kernel.copy(readOnly = Array.emptyByteArray, globalMemory = Array.empty)

  private def indexUncached(program: CodeObject): Either[Fault, Indexed] =
    for {
      indexed   <- ELF.index(program.image).leftMap(error => Fault.InvalidRequest(error.message))
      manifests <- CodeObjectMetadata.decodeNotes(indexed.notes)
        .leftMap(error => Fault.InvalidRequest(error.message))
        .map(_.getOrElse(Vec.empty[CodeObjectMetadata.KernelManifest]))
      aliases <- KernelVariants.select(
        indexed.kernelNames.toSet[String],
        indexed.target.name,
        name =>
          indexed.kernelCodeSize(name).exists { size =>
            size != 4 || indexed.kernelCode(name).exists { code =>
              !VMCompiler.decodeInstruction(code, indexed.target).contains(VMIR.Instruction.End)
            }
          }
      ).leftMap(detail => Fault.InvalidRequest(detail))
      metadata = manifests.view.flatMap(manifest =>
        Vec(
          manifest.name                      -> manifest.metadata,
          manifest.symbol                    -> manifest.metadata,
          manifest.symbol.stripSuffix(".kd") -> manifest.metadata
        )
      ).toMap
      layouts = manifests.view.flatMap { manifest =>
        val layout = KernargLayout(
          manifest.metadata.parameters.take(manifest.metadata.explicitParameterCount),
          manifest.hiddenArguments
        )
        Vec(
          (manifest.name: String)            -> layout,
          (manifest.symbol: String)          -> layout,
          manifest.symbol.stripSuffix(".kd") -> layout
        )
      }.toMap
      kernelSymbols =
        if (manifests.nonEmpty) manifests.map(manifest => KernelSymbol(manifest.name, manifest.symbol)).distinct
        else Vec.from(indexed.kernelNames.toSeq.sortBy[String](identity).map(name => KernelSymbol(name, name)))
      _ <- globalSymbols(indexed, kernelSymbols)
    } yield Indexed(
      indexed,
      aliases,
      metadata,
      layouts,
      kernelSymbols,
      VMCompiler(),
      Memo()
    )

  def globalSymbols(index: ELF.Index, kernels: Vec[KernelSymbol]): Either[Fault, Vec[GlobalSymbol]] = {
    val kernelLinkage = kernels.view.map(_.linkageName: String).toSet
    index.globals.filterNot(global => kernelLinkage.contains(global.name)).traverse { global =>
      GlobalSymbol.validated(
        global.name,
        Some(global.address),
        global.size.widen[Long >= 0],
        global.pointerOffsets
      )
        .leftMap(detail => Fault.InvalidRequest(detail))
    }
  }
}

class LoadedProgram private (program: CodeObject, workgroups: Workgroups) extends Backend.Program {
  private val compiled = java.util.concurrent.ConcurrentHashMap[String, Either[Fault, VMIR.Kernel]]()
  private val leased   = AtomicReference(Option.empty[ImageCache.Lease[LoadedProgram.Indexed]])

  private val loadBase = AtomicLong(0L)
  private val forked   = AtomicBoolean(false)

  private lazy val shared: Either[Fault, LoadedProgram.Indexed] = LoadedProgram.index(program).map { lease =>
    leased.set(Some(lease))
    lease.value
  }

  private lazy val initialised: Either[Fault, LoadedProgram.Indexed] = shared.map { indexed =>
    forked.set(true)
    indexed.copy(index = indexed.index.fork(loadBase.get()))
  }

  // A malformed image reports at its first launch, as it does without a window.
  override def bindFunctionPointerNamespace(id: ProgramId): Either[Fault, Unit] =
    shared.fold(
      _ => Right(()),
      indexed =>
        for {
          base <- CodeWindows.loadBase(id, indexed.index.extent)
          _    <- Either.cond(
            loadBase.get() == base || !forked.get(),
            (),
            Fault.InvalidRequest("function pointer namespace must be bound before the program is used")
          )
        } yield loadBase.set(base)
    )

  private val libraries = java.util.concurrent.ConcurrentHashMap[Int, Either[Fault, VMIR.Kernel]]()

  // Another program's wave can enter any function of the text; the library decodes all of it for the caller's wave size.
  private def library(caller: CodeObjectKernel): Either[Fault, PeerCode.Program] =
    initialised.flatMap { program =>
      libraries.computeIfAbsent(
        caller.waveSize.raw,
        _ =>
          if (program.index.target.isa != caller.target.isa)
            Left(Fault.Unsupported(s"AMDGPU code for ${program.index.target.name} cannot run on ${caller.target.name}"))
          else {
            val code = program.index.library(caller.waveSize)
            if (code.waveSize != caller.waveSize)
              Left(Fault.Unsupported(s"AMDGPU code for ${code.target.name} cannot run in wave${caller.waveSize.raw}"))
            else program.compiler.compileLinked(code)
          }
      ).map(PeerCode.Program(_, Vec.toArray(bindings)))
    }

  private case class Bound(value: GlobalMapping, memory: AddressSpace) derives CanEqual
  private val bindingLock = ReentrantLock()
  private val bound       = mutable.Map.empty[NonEmpty[String], Bound]

  private def bindings: Vec[GlobalMapping] = bindingLock.withLock(Vec.from(bound.values.view.map(_.value)).distinct)

  override def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata] =
    initialised.flatMap { program =>
      program.metadata.get(program.aliases.getOrElse(name, name)).orElse(program.metadata.get(name))
        .toRight(Fault.NotFound(s"AMDGPU kernel metadata for '$name' is not present"))
    }

  override def symbols: Either[Fault, ProgramSymbols] =
    initialised.flatMap(program =>
      LoadedProgram.globalSymbols(program.index, program.kernelSymbols).map(ProgramSymbols(program.kernelSymbols, _))
    )

  override def global(name: NonEmpty[String]): Either[Fault, ProgramGlobal] =
    for {
      program <- initialised
      data    <- program.index.globalData(name).leftMap(error => Fault.NotFound(error.message))
      global  <-
        ProgramGlobal.validated(data._2.nonNegativeSize.widen[Long >= 0], data._2).leftMap(Fault.InvalidRequest(_))
    } yield global

  /** Uncovered `[start, start + size)` sub-ranges, relative to `start`, of the bound mappings overlapping it. */
  // The covered-range scan maintains one cursor while appending disjoint gaps.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def gaps(start: Long, size: Int): Vec[(Int, Int)] = {
    val covered = bound.values.view.map(_.value).filter(mapping => mapping.overlaps(start, size)).map(mapping =>
      (math.max(start, mapping.address.raw) - start).toInt ->
        (math.min(start + size, mapping.address.raw + mapping.size) - start).toInt
    ).toVec.sortBy(_._1)
    val result = Vec.newBuilder[(Int, Int)]
    var cursor = 0
    covered.foreach { case (from, until) =>
      if (from > cursor) result += cursor -> from
      cursor = math.max(cursor, until)
    }
    if (cursor < size) result += cursor -> size
    result.result()
  }

  override def bindGlobal(name: NonEmpty[String], binding: GlobalBinding, memory: AddressSpace): Either[Fault, Unit] =
    bindingLock.withLock {
      for {
        program <- initialised
        data    <- program.index.globalData(name).leftMap(error => Fault.NotFound(error.message))
        (address, initial) = data
        _ <- Either.cond(
          address.raw >= binding.offset && address.raw <= Long.MaxValue - initial.size,
          (),
          Fault.InvalidRequest("global binding exceeds the virtual address range")
        )
        entry = Bound(GlobalMapping(address, initial.nonNegativeSize, binding), memory)
        first <- GlobalBinding.rebind(name, bound.get(name), entry)
        _     <- if (first) bindFirst(entry, initial) else Right(())
      } yield bound.update(name, entry)
    }

  // Aliases of a bound global must map the same storage; uncovered bytes take the code-object initialiser.
  private def bindFirst(entry: Bound, initial: Vec[Byte]): Either[Fault, Unit] = {
    val mapping = entry.value
    val delta   = mapping.address.raw - mapping.binding.offset
    val sharing = bound.values.forall { other =>
      val same = mapping.binding.region == other.value.binding.region &&
        delta == other.value.address.raw - other.value.binding.offset &&
        mapping.binding.regionSize == other.value.binding.regionSize && entry.memory == other.memory
      same ||
      (!other.value.overlaps(mapping.address.raw, mapping.size.raw) &&
        mapping.binding.region != other.value.binding.region)
    }
    val uncovered = gaps(mapping.address.raw, initial.size)
    if (!sharing) Left(Fault.InvalidRequest("global aliases have conflicting allocation mappings"))
    else if (uncovered.isEmpty) Right(())
    else
      entry.memory.read(mapping.binding.region, mapping.binding.offset, mapping.size).flatMap { current =>
        val bytes = Vec.toArray(current)
        uncovered.foreach { case (from, until) => val _ = initial.slice(from, until).copyToArray(bytes, from) }
        entry.memory.write(mapping.binding.region, mapping.binding.offset, Vec.unsafeWrapArray(bytes))
      }
  }

  override def writeGlobal(name: NonEmpty[String], offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
    bindingLock.withLock {
      for {
        program <- initialised
        symbol  <- program.index.global(name).toRight(Fault.NotFound(s"AMDGPU global $name is not present"))
        (address, size) = symbol
        _ <- Either.cond(
          offset <= size && bytes.size.toLong <= size.toLong - offset,
          (),
          Fault.InvalidRequest("global write exceeds symbol bounds")
        )
        start = address.raw + offset
        // Bound aliases share storage, so writing every overlapping one stores the same bytes.
        _ <- bound.values.view.filter(_.value.overlaps(start, bytes.size)).toVec.distinct.traverse_ { entry =>
          val from  = math.max(start, entry.value.address.raw)
          val until = math.min(start + bytes.size, entry.value.address.raw + entry.value.size)
          entry.memory.write(
            entry.value.binding.region,
            entry.value.offsetOf(from),
            bytes.slice((from - start).toInt, (until - start).toInt)
          )
        }
        _ <- gaps(start, bytes.size).traverse_ { case (from, until) =>
          program.index.writeGlobal(name, offset.raw + from, bytes.slice(from, until))
            .leftMap(error => Fault.InvalidRequest(error.message))
        }
      } yield ()
    }

  private def compile(name: NonEmpty[String]): Either[Fault, (LoadedProgram.Indexed, String, VMIR.Kernel)] =
    for {
      program  <- initialised
      alias    <- program.aliases.get(name).toRight(Fault.NotFound(s"AMDGPU kernel '$name' is not present"))
      kernel   <- program.index.kernel(alias).leftMap(error => Fault.InvalidRequest(error.message))
      prepared <- compiled.computeIfAbsent(
        kernel.name,
        _ =>
          program.kernels.get(kernel.name)(
            program.compiler.compile(kernel).map(_.withSource(LoadedProgram.unbound(kernel)))
          )
            .map(_.withSource(kernel))
      )
    } yield (program, alias, prepared)

  private def peers(context: TaskContext): PeerCode = (id, caller) =>
    context.programs.program(id) match {
      case Some(other: LoadedProgram) => other.library(caller).map(Some(_))
      case _                          => Right(None)
    }

  override def close(): Unit = leased.getAndSet(None).foreach(_.release())

  override def build(name: NonEmpty[String]): Either[Fault, Unit] = compile(name).map(_ => ())

  override def execute(task: KernelTask, context: TaskContext): Either[Fault, Unit] =
    for {
      (program, alias, prepared) <- compile(task.kernelName)
      layout = program.layouts.get(alias).orElse(program.layouts.get(task.kernelName))
      _ <- VMInterpreter.execute(prepared, KernelLaunch(task, context, workgroups), bindings, layout, peers(context))
    } yield ()
}
