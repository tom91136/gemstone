package net.kurobako.gemstone.server.backend

import cats.syntax.all.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.sass.{Cubin as ELF, *}
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

object Cubin {
  val encoding: String = "cuda-cubin"
}

class Cubin(workgroups: Workgroups = Workgroups.Sequential) extends RuntimeBackend {
  override val encoding: String = Cubin.encoding

  override def load(image: Vec[Byte]): Either[ExecutionFailure, RuntimeBackend.Program] =
    ELF.decode(Vec.toArray(image))
      .left.map(ExecutionFailure.InvalidRequest(_))
      .map(module => Program(module, Map.empty))

  /** Uses supplied disassembly while exercising the same direct SASS compilation and execution path. */
  def loadDecoded(
      module: ELF.Module,
      functions: Vec[DecodedFunction]
  ): Either[ExecutionFailure, RuntimeBackend.Program] =
    Right(Program(module, module.kernels.map(_.name -> functions).toMap))

  private case class Binding(region: AllocationId, offset: Long >= 0, size: Long > 0, memory: Memory)

  private class Program(
      module: ELF.Module,
      decoded: Map[String, Vec[DecodedFunction]]
  ) extends RuntimeBackend.Program {
    private val closed   = AtomicBoolean(false)
    private val compiled = ConcurrentHashMap[String, LoadedProgram.Compiled]()
    private val bindings = ConcurrentHashMap[String, Binding]()

    private def openProgram[A](body: => Either[ExecutionFailure, A]): Either[ExecutionFailure, A] =
      if (closed.get()) Left(ExecutionFailure.Failed("cubin program is closed")) else body

    private def constantName(section: Int): String = s"__sass_const$section"

    private def symbol(name: String, size: Long): Either[String, GlobalSymbol] =
      GlobalSymbol.validated(
        name.unsafe[NonEmpty[String]],
        None,
        size.unsafe[Long >= 0],
        Vec.empty
      ).left.map(_.raw)

    private def globalSize(name: String): Option[Long] =
      if (name.startsWith("__sass_const")) {
        name.stripPrefix("__sass_const").toIntOption.flatMap(index =>
          module.sections.find(section =>
            section.index == index && section.name.matches("\\.nv\\.constant[1-9][0-9]*(?:\\..*)?")
          ).map(_.size.toLong)
        )
      } else module.globals.find(_.name == name).map(_.bytes.size.toLong)

    override def symbols: Either[ExecutionFailure, ProgramSymbols] = openProgram {
      val constants = module.sections.filter(section =>
        section.size > 0 && section.name.matches("\\.nv\\.constant[1-9][0-9]*(?:\\..*)?")
      ).map(section => symbol(constantName(section.index), section.size.toLong))
      val globalSymbols = module.globals.filter(_.constantSection.isEmpty).map(global =>
        symbol(global.name, global.bytes.size.toLong)
      ) ++ constants ++ module.globals.filter(_.constantSection.nonEmpty).map(global =>
        symbol(global.name, global.bytes.size.toLong)
      )
      globalSymbols.sequence
        .left.map(error => ExecutionFailure.InvalidRequest(error.toString))
        .map(values =>
          ProgramSymbols(
            Vec.from(module.kernels.map(kernel =>
              KernelSymbol(kernel.name.unsafe[NonEmpty[String]], kernel.name.unsafe[NonEmpty[String]])
            )),
            Vec.from(values)
          )
        )
    }

    override def kernel(name: NonEmpty[String]): Either[ExecutionFailure, KernelMetadata] = openProgram {
      module.kernels.find(_.name == name.raw).toRight(
        ExecutionFailure.NotFound(s"cubin kernel '${name.raw}' is not present")
      ).flatMap { kernel =>
        KernelMetadata.validated(
          kernel.parameterSize.unsafe[Int >= 0],
          kernel.parameters.size.unsafe[Int >= 0],
          Vec.from(kernel.parameters.map(parameter =>
            KernelMetadata.Parameter(
              parameter.size.unsafe[Int > 0],
              parameter.offset.unsafe[Int >= 0],
              KernelMetadata.ParameterKind.Scalar,
              containsPointers = true
            )
          )),
          registerCount = Some((module.sections(kernel.section).info >>> 24).unsafe[Int >= 0])
        ).left.map(error => ExecutionFailure.InvalidRequest(error.toString))
      }
    }

    override def global(name: NonEmpty[String]): Either[ExecutionFailure, GlobalImage] = openProgram {
      val sectionImage = name.raw.stripPrefix("__sass_const").toIntOption
        .filter(_ => name.raw.startsWith("__sass_const"))
        .flatMap(index =>
          module.sections.find(section =>
            section.index == index && section.name.matches("\\.nv\\.constant[1-9][0-9]*(?:\\..*)?")
          )
        )
      sectionImage match {
        case Some(section) =>
          GlobalImage.validated(section.size.toLong.unsafe[Long >= 0], section.bytes)
            .left.map(ExecutionFailure.InvalidRequest(_))
        case None => module.globals.find(_.name == name.raw) match {
            case Some(global) if global.constantSection.nonEmpty =>
              GlobalImage.validated(
                global.bytes.size.toLong.unsafe[Long >= 0],
                Vec.empty,
                Some(GlobalImage.Alias(
                  constantName(global.constantSection.get).unsafe[NonEmpty[String]],
                  global.offset.toLong.unsafe[Long >= 0]
                ))
              ).left.map(ExecutionFailure.InvalidRequest(_))
            case Some(global) => GlobalImage.validated(global.bytes.size.toLong.unsafe[Long >= 0], global.bytes)
                .left.map(ExecutionFailure.InvalidRequest(_))
            case None => Left(ExecutionFailure.NotFound(s"cubin global '${name.raw}' is not present"))
          }
      }
    }

    override def bindGlobal(
        name: NonEmpty[String],
        region: AllocationId,
        offset: Long >= 0,
        regionSize: Long > 0,
        memory: Memory
    ): Either[ExecutionFailure, Unit] = openProgram {
      globalSize(name.raw).toRight(ExecutionFailure.NotFound(s"cubin global '${name.raw}' is not present"))
        .flatMap { size =>
          Either.cond(
            offset.raw <= regionSize.raw && size <= regionSize.raw - offset.raw,
            (),
            ExecutionFailure.InvalidRequest(s"cubin global '${name.raw}' does not fit its bound memory region")
          )
        }.flatMap { _ =>
          memory match {
            case addresses: AddressSpace => addresses.address(region, offset)
                .left.map(ExecutionFailure.fromFault(_))
                .map { _ =>
                  val _ = bindings.put(name.raw, Binding(region, offset, regionSize, memory))
                  compiled.clear()
                }
            case _ => Left(ExecutionFailure.Unsupported("cubin global binding requires address-space capable memory"))
          }
        }
    }

    override def writeGlobal(
        name: NonEmpty[String],
        offset: Long >= 0,
        bytes: Vec[Byte]
    ): Either[ExecutionFailure, Unit] = openProgram {
      Option(bindings.get(name.raw)).toRight(ExecutionFailure.NotFound(s"cubin global '${name.raw}' is not bound"))
        .flatMap { binding =>
          val size = globalSize(name.raw).getOrElse(0L)
          Either.cond(
            offset.raw <= size && bytes.size.toLong <= size - offset.raw,
            (),
            ExecutionFailure.InvalidRequest(s"write exceeds cubin global '${name.raw}'")
          ).flatMap { _ =>
            binding.memory.write(
              binding.region,
              (binding.offset.raw + offset.raw).unsafe[Long >= 0],
              bytes
            ).left.map(ExecutionFailure.fromFault(_))
          }
        }
    }

    private def compile(name: NonEmpty[String]): Either[ExecutionFailure, LoadedProgram.Compiled] =
      Option(compiled.get(name.raw)).fold {
        module.kernels.find(_.name == name.raw).toRight(
          ExecutionFailure.NotFound(s"missing cubin kernel ${name.raw}")
        ).flatMap { kernel =>
          val decodedFunctions = decoded.get(name.raw).fold {
            val code = module.sections(kernel.section).bytes
            if (code.isEmpty) Left(ExecutionFailure.Unsupported("empty SASS code section"))
            else CodeSection.decode(Vec.toArray(code), module.sm)
              .left.map(error => ExecutionFailure.Unsupported(s"${kernel.name} sm_${module.sm} $error"))
              .map(section => Vec(DecodedFunction(kernel.name, section.instructions)))
          }(Right(_))
          decodedFunctions.flatMap { functions =>
            val globalSymbols = bindings.keySet().toArray.toVec.collect { case globalName: String =>
              val binding = bindings.get(globalName)
              binding.memory match {
                case addresses: AddressSpace => addresses.address(binding.region, binding.offset)
                    .toOption.map(address => globalName.unsafe[NonEmpty[String]] -> address)
                case _ => None
              }
            }.flatten.toMap
            LoadedProgram.compile(module, kernel, functions, globalSymbols)
              .left.map(error => ExecutionFailure.Unsupported(error.message))
          }
        }.map(kernel => Option(compiled.putIfAbsent(name.raw, kernel)).getOrElse(kernel))
      }(Right(_))

    override def execute(
        task: KernelTask,
        memory: Memory,
        control: TaskControl,
        handler: Event => Either[Fault, Unit]
    ): Either[ExecutionFailure, Unit] = openProgram {
      memory match {
        case addresses: AddressSpace =>
          compile(task.kernelName).flatMap(kernel =>
            LoadedProgram.execute(kernel, task, addresses, control, workgroups)
              .left.map(ExecutionFailure.fromFault(_))
          )
        case _ => Left(ExecutionFailure.Unsupported("cubin execution requires address-space capable memory"))
      }
    }

    override def close(): Unit = if (closed.compareAndSet(false, true)) {
      compiled.clear()
      bindings.clear()
      super.close()
    }
  }
}
