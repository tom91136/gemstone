package net.kurobako.gemstone.isa.amdgpu

import VMDispatchMemory.*
import cats.syntax.all.*
import java.util.concurrent.locks.ReentrantLock
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

  private case class Indexed(
      index: ELF.Index,
      aliases: Map[String, String],
      metadata: Map[String, KernelMetadata],
      layouts: Map[String, KernargLayout],
      symbols: ProgramSymbols
  )

  // Only immutable load data is cached; every program forks the index for its own relocated globals and kernels.
  private val cache = LRUCache[CodeObjectKey, Either[Fault, Indexed]](capacity = 4)

  private def index(program: CodeObject): Either[Fault, Indexed] =
    cache.get(CodeObjectKey(program.image))(indexUncached(program)).map(value => value.copy(index = value.index.fork()))

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
      kernelLinkage = kernelSymbols.view.map(_.linkageName: String).toSet
      globals <- indexed.globals.filterNot(global => kernelLinkage.contains(global.name)).traverse { global =>
        GlobalSymbol.validated(
          global.name,
          Some(global.address),
          global.size.widen[Long >= 0],
          global.pointerOffsets
        )
          .leftMap(detail => Fault.InvalidRequest(detail))
      }
    } yield Indexed(indexed, aliases, metadata, layouts, ProgramSymbols(kernelSymbols, globals))
}

class LoadedProgram private (program: CodeObject, workgroups: Workgroups) extends Backend.Program {
  private val compiler = VMCompiler()
  private val compiled = java.util.concurrent.ConcurrentHashMap[String, Either[Fault, VMIR.Kernel]]()

  private lazy val initialised = LoadedProgram.index(program)

  private case class Bound(value: GlobalMapping, memory: AddressSpace) derives CanEqual
  private val bindingLock = ReentrantLock()
  private val bound       = mutable.Map.empty[NonEmpty[String], Bound]

  private def bindings: Vec[GlobalMapping] = bindingLock.withLock(Vec.from(bound.values.view.map(_.value)).distinct)

  override def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata] =
    initialised.flatMap { program =>
      program.metadata.get(program.aliases.getOrElse(name, name)).orElse(program.metadata.get(name))
        .toRight(Fault.NotFound(s"AMDGPU kernel metadata for '$name' is not present"))
    }

  override def symbols: Either[Fault, ProgramSymbols] = initialised.map(_.symbols)

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

  override def execute(
      task: KernelTask,
      memory: AddressSpace,
      control: TaskControl,
      handler: Event => Either[Fault, Unit]
  ): Either[Fault, Unit] =
    for {
      program <- initialised
      alias   <- program.aliases.get(
        task.kernelName
      ).toRight(Fault.NotFound(s"AMDGPU kernel '${task.kernelName}' is not present"))
      kernel   <- program.index.kernel(alias).leftMap(error => Fault.InvalidRequest(error.message))
      prepared <- compiled.computeIfAbsent(kernel.name, _ => compiler.compile(kernel))
      layout = program.layouts.get(alias).orElse(program.layouts.get(task.kernelName))
      _ <- VMInterpreter.execute(prepared, task, memory, control, workgroups, bindings, handler, layout)
    } yield ()
}
