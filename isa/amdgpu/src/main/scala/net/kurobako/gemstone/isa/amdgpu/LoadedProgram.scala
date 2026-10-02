package net.kurobako.gemstone.isa.amdgpu

import ELF.*
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
  def load(program: CodeObject): Either[Fault, LoadedProgram] =
    ELF.validateHeader(program.image).leftMap(error => Fault.InvalidTask(error.message))
      .map(_ => LoadedProgram(() => VMLoader.index(program)))

}

class LoadedProgram private (initialize: () => Either[Fault, VMLoader.IndexedProgram]) {
  private val compiled = java.util.concurrent.ConcurrentHashMap[
    String,
    Either[Fault, VMIR.Kernel]
  ]()

  private lazy val initialized = initialize()

  private def resolveKernel(name: String): Either[Fault, CodeObjectKernel] =
    initialized.flatMap { program =>
      program.aliases.get(name)
        .toRight(Fault.InvalidTask(s"AMDGPU kernel '$name' is not present"))
        .flatMap(alias =>
          program.index.kernel(alias)
            .leftMap(error => Fault.InvalidTask(error.message))
        )
    }

  def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata] =
    initialized.flatMap { program =>
      val resolved = program.aliases.getOrElse(name, name)
      program.metadata.get(resolved).orElse(program.metadata.get(name))
        .toRight(Fault.InvalidTask(
          s"AMDGPU kernel metadata for '$name' is not present"
        ))
    }

  def symbols: Either[Fault, ProgramSymbols] = initialized.map(_.symbols)

  def global(name: NonEmpty[String]): Either[Fault, GlobalImage] =
    initialized.flatMap { program =>
      program.index.globalData(name)
        .leftMap(error => Fault.Memory(error.message))
        .flatMap { case (_, initial) =>
          GlobalImage.validated(initial.nonNegativeSize.toLong, initial).left.map(Fault.InvalidTask(_))
        }
    }

  private case class Bound(value: GlobalBinding, memory: Memory)
  private val bindingLock = ReentrantLock()
  private val bound       = mutable.Map.empty[NonEmpty[String], Bound]

  private def bindings: Vec[GlobalBinding] = bindingLock.withLock {
    Vec.from(bound.values.view.map(_.value)).distinct
  }

  // The covered-range scan maintains one cursor while appending disjoint gaps.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def bindGlobal(
      name: NonEmpty[String],
      allocation: AllocationId,
      offset: Long >= 0,
      allocationSize: Long > 0,
      memory: Memory
  ): Either[Fault, Unit] = bindingLock.withLock {
    initialized match {
      case Left(error)    => Left(error)
      case Right(program) =>
        program.index.globalData(name)
          .leftMap(error => Fault.Memory(error.message)) match {
          case Left(error)                    => Left(error)
          case Right((guestAddress, initial)) =>
            val address = guestAddress.raw
            if (
              offset.raw > allocationSize.raw ||
              initial.size.toLong > (allocationSize - offset).raw ||
              address < offset.raw || address > Long.MaxValue - initial.size.toLong
            )
              Left(Fault.InvalidTask("global binding exceeds allocation or virtual address range"))
            else {
              val value = GlobalBinding(
                address,
                initial.nonNegativeSize,
                allocation,
                offset,
                allocationSize
              )
              val old = bound.get(name)
              if (old.exists(entry => entry.value != value || !(entry.memory eq memory)))
                Left(Fault.InvalidTask(
                  s"global $name is already bound to different storage"
                ))
              else if (old.nonEmpty) Right(())
              else if (
                bound.values.view.exists { entry =>
                  val other    = entry.value
                  val overlaps = address < other.address + other.size.raw &&
                    other.address < address + initial.size
                  val sameMapping = allocation == other.allocation &&
                    address - offset.raw == other.address - other.offset.raw &&
                    allocationSize == other.allocationSize && (entry.memory eq memory)
                  (overlaps && !sameMapping) || (allocation == other.allocation && !sameMapping)
                }
              ) Left(Fault.InvalidTask("global aliases have conflicting allocation mappings"))
              else {
                // Adding an overlapping symbol alias must not overwrite already-live bound bytes with old initializers.
                val covered = bound.values.view.map(_.value).filter(other =>
                  address < other.address + other.size.raw && other.address < address + initial.size
                ).map(other =>
                  (math.max(address, other.address) - address).toInt ->
                    (math.min(address + initial.size, other.address + other.size.raw) - address).toInt
                ).toVec.sortBy(_._1)
                val gaps       = Vec.newBuilder[(Int, Int)]
                var coveredEnd = 0
                covered.foreach { case (start, end) =>
                  if (start > coveredEnd) gaps += coveredEnd -> start
                  coveredEnd = math.max(coveredEnd, end)
                }
                if (coveredEnd < initial.size) gaps += coveredEnd -> initial.size
                val uncovered         = gaps.result()
                val initializedMemory = if (uncovered.isEmpty) Right(())
                else memory.read(allocation, offset, initial.nonNegativeSize) match {
                  case Left(error)    => Left(error)
                  case Right(current) =>
                    val bytes = Vec.toArray(current)
                    uncovered.foreach { case (start, end) =>
                      val _ = initial.slice(start, end).copyToArray(bytes, start, end - start)
                    }
                    memory.write(allocation, offset, Vec.unsafeWrapArray(bytes))
                }
                initializedMemory match {
                  case Left(error) => Left(error)
                  case Right(_)    =>
                    bound.update(name, Bound(value, memory))
                    Right(())
                }
              }
            }
        }
    }
  }

  def writeGlobal(
      name: NonEmpty[String],
      offset: Long >= 0,
      bytes: Vec[Byte]
  ): Either[Fault, Unit] =
    bindingLock.withLock {
      initialized match {
        case Left(error)    => Left(error)
        case Right(program) =>
          program.index.global(name)
            .toRight(Fault.Memory(s"AMDGPU global $name is not present")) match {
            case Left(error)                 => Left(error)
            case Right((guestAddress, size)) =>
              val address = guestAddress.raw
              if (offset.raw > size.raw || bytes.size.toLong > size.raw.toLong - offset.raw)
                Left(Fault.InvalidTask("global write exceeds symbol bounds"))
              else {
                val start   = address + offset.raw
                val covered = bound.values.view.find(entry =>
                  start >= entry.value.address &&
                    start - entry.value.address <= entry.value.size.raw.toLong - bytes.size
                )
                covered match {
                  case Some(entry) =>
                    entry.memory.write(
                      entry.value.allocation,
                      (entry.value.offset.raw + start - entry.value.address).unsafe[Long >= 0],
                      bytes
                    )
                  case None
                      if bound.values.view.forall(entry =>
                        start >= entry.value.address + entry.value.size.raw ||
                          start + bytes.size <= entry.value.address
                      ) =>
                    program.index.writeGlobal(name, offset.raw, bytes).leftMap(error =>
                      Fault.InvalidTask(error.message)
                    )
                  case None =>
                    bytes.zipWithIndex.foldLeft[Either[Fault, Unit]](Right(())) {
                      case (result, (byte, index)) =>
                        result match {
                          case error @ Left(_) => error
                          case Right(_)        =>
                            bound.values.view.find(entry =>
                              start + index >= entry.value.address &&
                                start + index < entry.value.address + entry.value.size.raw
                            ) match {
                              case Some(entry) =>
                                entry.memory.write(
                                  entry.value.allocation,
                                  (
                                    entry.value.offset.raw + start + index - entry.value.address
                                  ).unsafe[Long >= 0],
                                  Vec(byte)
                                )
                              case None =>
                                program.index.writeGlobal(name, offset.raw + index, Vec(byte))
                                  .leftMap(error => Fault.InvalidTask(error.message))
                            }
                        }
                    }
                }
              }
          }
      }
    }

  def execute(
      task: KernelTask,
      memory: AddressSpace,
      control: TaskControl,
      workgroups: Workgroups = Workgroups.Sequential
  ): Either[Fault, Unit] = resolveKernel(task.kernelName) match {
    case Left(error)   => Left(error)
    case Right(kernel) =>
      compiled.computeIfAbsent(kernel.name, _ => VMCompiler.compile(kernel)) match {
        case Left(error)    => Left(error)
        case Right(program) =>
          VMInterpreter.execute(program, task, memory, control, workgroups, bindings)
      }
  }

}
