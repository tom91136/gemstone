package net.kurobako.gemstone.isa.sass

import cats.syntax.all.*
import java.util.concurrent.ConcurrentHashMap
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import scala.jdk.CollectionConverters.*

object LoadedProgram {

  /** State shared by every load of a cubin, so only work independent of bound globals is memoised here. */
  class Image(val module: Cubin.Module) {
    val banks: Vec[(NonEmpty[String], Cubin.Section)] =
      Cubin.constantSections(module).map(section => Cubin.constantSymbol(section.index) -> section)
    val bank: Map[NonEmpty[String], Cubin.Section]                = banks.toMap
    val relocations: Either[Fault, Vec[Cubin.ConstantRelocation]] =
      Cubin.constantRelocations(module).left.map(Fault.InvalidProgram(_))
    val globalRelocations: Either[Fault, Vec[Cubin.GlobalRelocation]] =
      Cubin.globalRelocations(module).left.map(Fault.InvalidProgram(_))
    val functions: Memo[NonEmpty[String], Either[Fault, Vec[DecodedFunction]]] = Memo()
    val kernels: Memo[NonEmpty[String], Either[Fault, VMCompiler.Compiled]]    = Memo()
  }
}

/** A loaded cubin. Kernels compile from their code sections, or from `decoded` functions when supplied. */
class LoadedProgram(
    image: LoadedProgram.Image,
    decoded: Map[NonEmpty[String], Vec[DecodedFunction]],
    workgroups: Workgroups,
    text: SASSInstruction => Option[String],
    release: () => Unit
) extends Backend.Program {

  def this(
      module: Cubin.Module,
      decoded: Map[NonEmpty[String], Vec[DecodedFunction]],
      workgroups: Workgroups,
      text: SASSInstruction => Option[String]
  ) = this(LoadedProgram.Image(module), decoded, workgroups, text, () => ())

  private val module           = image.module
  private val banks            = image.banks
  private val bank             = image.bank
  private val relocations      = image.relocations
  private val compiled         = ConcurrentHashMap[NonEmpty[String], VMCompiler.Compiled]()
  private val bindings         = ConcurrentHashMap[NonEmpty[String], BoundGlobal]()
  private val relocated        = ConcurrentHashMap[Cubin.ConstantRelocation, (BoundGlobal, BoundGlobal)]()
  private val relocatedGlobals = ConcurrentHashMap[Cubin.GlobalRelocation, (BoundGlobal, BoundGlobal)]()

  private def pointTo(
      destination: BoundGlobal,
      offset: Int >= 0,
      source: BoundGlobal,
      addend: Long
  ): Either[Fault, Unit] =
    source.memory.address(source.binding.region, source.binding.offset).flatMap { address =>
      destination.memory.write(
        destination.binding.region,
        destination.binding.offset + offset.widen[Long >= 0],
        LittleEndian.toVec(address.offset(addend).raw, 8)
      )
    }

  private def relocateBoundGlobals(): Either[Fault, Unit] = image.globalRelocations.flatMap(_.traverse_ { relocation =>
    (Option(bindings.get(relocation.destination)), Option(bindings.get(relocation.target))) match {
      case (Some(destination), Some(source))
          if !Option(relocatedGlobals.get(relocation)).contains(destination -> source) =>
        pointTo(destination, relocation.offset, source, relocation.addend).map { _ =>
          val _ = relocatedGlobals.put(relocation, destination -> source)
        }
      case _ => Right(())
    }
  })

  private def relocateBoundConstants(): Either[Fault, Unit] = relocations.flatMap(_.traverse_ { relocation =>
    (Option(bindings.get(Cubin.constantSymbol(relocation.section))), Option(bindings.get(relocation.target))) match {
      case (Some(destination), Some(source)) if !Option(relocated.get(relocation)).contains(destination -> source) =>
        pointTo(destination, relocation.offset, source, relocation.addend).map { _ =>
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
      banks.collect { case (name, section) if section.size > 0 => name -> section.size.widen[Long >= 0] } ++
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
          relocations.fold(_ => Right(()), _ => relocateBoundConstants())
            .flatMap(_ => image.globalRelocations.fold(_ => Right(()), _ => relocateBoundGlobals())).left.map { error =>
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

  private def decodeFunctions(kernel: Cubin.Kernel): Either[Fault, Vec[DecodedFunction]] =
    Cubin.link(module, kernel).left.map(Fault.InvalidProgram(_)).flatMap { linked =>
      val code = linked.text
      if (code.isEmpty) Left(Fault.InvalidProgram("empty SASS code section"))
      else Target.of(module.sm).flatMap(CodeSection.decode(Vec.toArray(code), _))
        .left.map(error => Fault.InvalidProgram(s"${kernel.name} sm_${module.sm} $error"))
        .map(section => Vec(DecodedFunction(kernel.name, section.instructions)))
    }

  private def lower(
      kernel: Cubin.Kernel,
      functions: Vec[DecodedFunction],
      globalSymbols: Map[NonEmpty[String], GuestAddress]
  ): Either[Fault, VMCompiler.Compiled] =
    for {
      resolved <- Cubin.relocateConstants(module, globalSymbols).left.map(Fault.InvalidProgram(_))
      lowered  <- VMCompiler.compile(resolved, kernel, functions, globalSymbols, text).left.map(Fault.InvalidProgram(_))
    } yield lowered

  private def lowerBound(kernel: Cubin.Kernel, functions: Vec[DecodedFunction]): Either[Fault, VMCompiler.Compiled] = {
    val globalSymbols = bindings.asScala.toVec.flatMap((global, bound) =>
      bound.memory.address(bound.binding.region, bound.binding.offset).toOption.map(global -> _)
    ).toMap
    relocateBoundConstants().flatMap(_ => relocateBoundGlobals()).flatMap(_ => lower(kernel, functions, globalSymbols))
  }

  private def compile(name: NonEmpty[String]): Either[Fault, VMCompiler.Compiled] =
    Option(compiled.get(name)).fold {
      module.kernels.find(_.name == name).toRight(Fault.NotFound(s"cubin kernel '$name' is not present")).flatMap {
        kernel =>
          // Without bound globals the output depends on the module alone, so every load of the image shares it.
          decoded.get(name) match {
            case Some(functions)          => lowerBound(kernel, functions)
            case None if bindings.isEmpty =>
              image.kernels.get(name)(
                decodeFunctions(kernel).flatMap(functions =>
                  relocations.flatMap(_ => lower(kernel, functions, Map.empty))
                )
              )
            case None => image.functions.get(name)(decodeFunctions(kernel)).flatMap(lowerBound(kernel, _))
          }
      }.map(kernel => Option(compiled.putIfAbsent(name, kernel)).getOrElse(kernel))
    }(Right(_))

  override def close(): Unit = release()

  override def build(name: NonEmpty[String]): Either[Fault, Unit] = compile(name).map(_ => ())

  override def execute(task: KernelTask, context: TaskContext): Either[Fault, Unit] =
    compile(task.kernelName).flatMap { lowered =>
      val observer: LaunchObserver | Null = context.control.observer.orNull
      observer.observed(watcher =>
        lowered.controlFlow.foreach(graph => watcher.controlFlow(task.kernelName.raw, graph(), lowered.offsets))
      )
      VMKernelLaunch.execute(lowered.kernel, KernelLaunch(task, context, workgroups))
    }
}
