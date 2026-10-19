package net.kurobako.gemstone.isa.sass

import cats.syntax.all.*
import java.util.concurrent.ConcurrentHashMap
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.vm.*
import scala.jdk.CollectionConverters.*

/** A loaded cubin. Kernels compile from their code sections, or from `decoded` functions when supplied. */
class LoadedProgram(
    module: Cubin.Module,
    decoded: Map[NonEmpty[String], Vec[DecodedFunction]],
    workgroups: Workgroups
) extends Backend.Program {

  private val banks    = Cubin.constantSections(module).map(section => Cubin.constantSymbol(section.index) -> section)
  private val bank     = banks.toMap
  private val compiled = ConcurrentHashMap[NonEmpty[String], Kernel]()
  private val bindings = ConcurrentHashMap[NonEmpty[String], BoundGlobal]()
  private val relocations = Cubin.constantRelocations(module).left.map(Fault.InvalidProgram(_))
  private val relocated   = ConcurrentHashMap[Cubin.ConstantRelocation, (BoundGlobal, BoundGlobal)]()

  private def relocateBoundConstants(): Either[Fault, Unit] = relocations.flatMap(_.traverse_ { relocation =>
    (Option(bindings.get(Cubin.constantSymbol(relocation.section))), Option(bindings.get(relocation.target))) match {
      case (Some(destination), Some(source)) if Option(relocated.get(relocation)) != Some(destination -> source) =>
        source.memory.address(source.binding.region, source.binding.offset).flatMap { address =>
          destination.memory.write(
            destination.binding.region,
            destination.binding.offset + relocation.offset.widen[Long >= 0],
            LittleEndian.toVec(address.offset(relocation.addend).raw, 8)
          )
        }.map { _ =>
          val _ = relocated.put(relocation, destination -> source)
        }
      case (Some(destination), None) =>
        Cubin.DriverFunctions.get(relocation.target).fold(Right(()))(address =>
          destination.memory.write(
            destination.binding.region,
            destination.binding.offset + relocation.offset.widen[Long >= 0],
            LittleEndian.toVec(address + relocation.addend, 8)
          )
        )
      case _ => Right(())
    }
  })

  private def symbol(name: NonEmpty[String], size: Long >= 0): Either[Fault, GlobalSymbol] =
    GlobalSymbol.validated(name, None, size, Vec.empty).left.map(Fault.InvalidRequest(_))

  override def symbols: Either[Fault, ProgramSymbols] = {
    val (aliased, independent) = module.globals.partition(_.constantSection.nonEmpty)
    val globals = independent.map(global => global.name -> global.bytes.nonNegativeSize.widen[Long >= 0]) ++
      banks.filter(_._2.size > 0).map((name, section) => name -> section.size.widen[Long >= 0]) ++
      aliased.map(global => global.name -> global.bytes.nonNegativeSize.widen[Long >= 0])
    globals.traverse((name, size) => symbol(name, size)).map(values =>
      ProgramSymbols(module.kernels.map(kernel => KernelSymbol(kernel.name, kernel.name)), values)
    )
  }

  override def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata] =
    module.kernels.find(_.name == name).toRight(Fault.NotFound(s"cubin kernel '$name' is not present")).flatMap {
      kernel =>
        KernelMetadata.validated(
          kernel.parameterSize,
          kernel.parameters.nonNegativeSize,
          kernel.parameters.map(parameter =>
            KernelMetadata.Parameter(
              parameter.size,
              parameter.offset,
              KernelMetadata.ParameterKind.Scalar,
              containsPointers = true
            )
          ),
          registerCount = Some(kernel.registerCount)
        ).left.map(Fault.InvalidRequest(_))
    }

  override def global(name: NonEmpty[String]): Either[Fault, ProgramGlobal] =
    bank.get(name) match {
      case Some(section) =>
        ProgramGlobal.validated(section.size.widen[Long >= 0], section.bytes).left.map(Fault.InvalidRequest(_))
      case None =>
        module.globals.find(_.name == name).toRight(Fault.NotFound(s"cubin global '$name' is not present")).flatMap {
          global =>
            val size = global.bytes.nonNegativeSize.widen[Long >= 0]
            global.constantSection.fold(ProgramGlobal.validated(size, global.bytes))(section =>
              ProgramGlobal.validated(
                size,
                Vec.empty,
                Some(ProgramGlobalAlias(Cubin.constantSymbol(section), global.offset.widen[Long >= 0]))
              )
            ).left.map(Fault.InvalidRequest(_))
        }
    }

  override def bindGlobal(name: NonEmpty[String], binding: GlobalBinding, memory: AddressSpace): Either[Fault, Unit] =
    if (!bank.contains(name) && !module.globals.exists(_.name == name))
      Left(Fault.NotFound(s"cubin global '$name' is not present"))
    else memory.address(binding.region, binding.offset).flatMap { _ =>
      val next = BoundGlobal(binding, memory)
      GlobalBinding.rebind(name, Option(bindings.putIfAbsent(name, next)), next).flatMap { first =>
        if (!first) Right(())
        else {
          compiled.clear()
          // Unused library catalogues can contain unsupported device relocations. Compilation still
          // validates the entire module before execution; supported data relocations apply during binding.
          relocations.fold(_ => Right(()), _ => relocateBoundConstants()).left.map { error =>
            val _ = bindings.remove(name)
            error
          }
        }
      }
    }

  override def writeGlobal(name: NonEmpty[String], offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
    Option(bindings.get(name)).toRight(Fault.InvalidRequest(s"cubin global '$name' is not bound")).flatMap(
      _.write(offset, bytes)(Fault.InvalidRequest(s"write exceeds cubin global '$name'"))
    )

  private def compile(name: NonEmpty[String]): Either[Fault, Kernel] =
    Option(compiled.get(name)).fold {
      module.kernels.find(_.name == name).toRight(Fault.NotFound(s"cubin kernel '$name' is not present")).flatMap {
        kernel =>
          val functions = decoded.get(name).fold {
            val code = module.sections(kernel.section).bytes
            if (code.isEmpty) Left(Fault.InvalidProgram("empty SASS code section"))
            else Target.of(module.sm).flatMap(CodeSection.decode(Vec.toArray(code), _))
              .left.map(error => Fault.InvalidProgram(s"${kernel.name} sm_${module.sm} $error"))
              .map(section => Vec(DecodedFunction(kernel.name, section.instructions)))
          }(Right(_))
          functions.flatMap { functions =>
            val globalSymbols = bindings.asScala.toVec.flatMap((global, bound) =>
              bound.memory.address(bound.binding.region, bound.binding.offset).toOption.map(global -> _)
            ).toMap
            for {
              _        <- relocateBoundConstants()
              resolved <- Cubin.relocateConstants(module, globalSymbols).left.map(Fault.InvalidProgram(_))
              lowered  <-
                VMCompiler.compile(resolved, kernel, functions, globalSymbols).left.map(Fault.InvalidProgram(_))
            } yield lowered
          }
      }.map(kernel => Option(compiled.putIfAbsent(name, kernel)).getOrElse(kernel))
    }(Right(_))

  override def execute(
      task: KernelTask,
      memory: AddressSpace,
      control: TaskControl,
      handler: Event => Either[Fault, Unit]
  ): Either[Fault, Unit] =
    compile(task.kernelName).flatMap(VMKernelLaunch.execute(_, task, memory, control, workgroups))
}
