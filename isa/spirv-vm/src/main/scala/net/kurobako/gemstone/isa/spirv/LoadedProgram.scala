package net.kurobako.gemstone.isa.spirv

import VMProgram.*
import cats.syntax.all.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*
import scala.util.control.NonFatal

private object LoadedProgram {
  def load(
      module: Module,
      environment: Validation.Environment,
      workgroups: Workgroups = Workgroups.Sequential
  ): Either[Fault, LoadedProgram] =
    Validation.validate(module, environment) match {
      case ValidationOutcome.Accepted         => loadUnchecked(module, workgroups)
      case ValidationOutcome.Rejected(issues) =>
        Left(Fault.InvalidRequest(s"invalid SPIR-V module: ${issues.map(_.message).mkString("; ")}"))
      case ValidationOutcome.Incomplete(issues) =>
        Left(Fault.Unsupported(s"unsupported SPIR-V module: ${issues.map(_.message).mkString("; ")}"))
    }

  def loadUnchecked(module: Module, workgroups: Workgroups = Workgroups.Sequential): Either[Fault, LoadedProgram] =
    try
      VMLoader.tables(ModuleNormalisation.mergeContinuations(ModuleNormalisation.expandDecorationGroups(module)))
        .map(LoadedProgram(_, workgroups))
    catch {
      case NonFatal(error) =>
        Left(Fault.InvalidRequest(s"malformed SPIR-V execution state: ${throwables.detail(error)}"))
    }
}

/** A loaded module with its lowered op table: the program the backend runs, and the metadata of its kernels. */
private class LoadedProgram(tables: Tables, workgroups: Workgroups) extends VMProgram(tables) {
  val ops: Array[Op] = VMLowering.lower(this)

  override def execute(task: KernelTask, context: TaskContext): Either[Fault, Unit] =
    VMInterpreter.execute(this, ops, KernelLaunch(task, context, workgroups))

  override def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata] =
    if (vulkanEntries.contains(name)) VMShaderInterface.metadata(this, name)
    else
      entryPoints.get(name)
        .orElse(names.collectFirst { case (id, candidate) if candidate == name && functions.contains(id) => id })
        .toRight(Fault.NotFound(s"SPIR-V entry point $name does not exist"))
        .flatMap { functionId =>
          functions.get(functionId).toRight(Fault.InvalidRequest(s"SPIR-V entry point $name has no function body"))
            .flatMap { function =>
              parameterLayout(function.parameters).flatMap { case (parameters, size) =>
                for {
                  entryPointId <-
                    functionId.refine[Int >= 0].leftMap(_ =>
                      Fault.InvalidRequest(s"negative SPIR-V function ID $functionId")
                    )
                  metadata <- KernelMetadata.validated(
                    size,
                    parameters.nonNegativeSize,
                    parameters,
                    requiredWorkGroupSize = workGroupSize(functionId),
                    workGroupSizeHint = workGroupSizeHints.get(functionId),
                    entryPointId = Some(entryPointId)
                  ).leftMap(detail => Fault.InvalidRequest(detail))
                } yield metadata
              }
            }
        }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def parameterLayout(parameters: Vec[(Int, Int)]): Either[Fault, (Vec[KernelMetadata.Parameter], Int >= 0)] = {
    val laidOut = Vec.newBuilder[KernelMetadata.Parameter]
    var size    = 0.unsafe[Int >= 0]
    var index   = 0
    var failure = Option.empty[Fault]
    while (failure.isEmpty && index < parameters.size) {
      val (parameterId, parameterType) = parameters(index)
      parameterMetadata(parameterId, parameterType) match {
        case Right(parameter) =>
          val offset = size.alignUp(typeAlignment(parameterType).min(8))
          laidOut += parameter.copy(offset = offset)
          size = offset + parameter.width
        case Left(fault) => failure = Some(fault)
      }
      index += 1
    }
    failure.toLeft((laidOut.result(), size))
  }

  private def parameterMetadata(parameterId: Int, parameterType: Int): Either[Fault, KernelMetadata.Parameter] =
    runtimeType(parameterType) match {
      case Some(RuntimeType.Pointer(_, pointee)) if byValueParameters.contains(parameterId) =>
        scalarParameter(pointee)
      case Some(RuntimeType.Pointer(StorageClass.Workgroup, _)) =>
        Right(KernelMetadata.Parameter(8, 0, KernelMetadata.ParameterKind.Local, containsPointers = false))
      case Some(RuntimeType.Pointer(_, _)) =>
        Right(KernelMetadata.Parameter(8, 0, KernelMetadata.ParameterKind.Allocation, containsPointers = false))
      case Some(_) => scalarParameter(parameterType)
      case None    => Left(Fault.InvalidRequest(s"SPIR-V parameter type %$parameterType is undefined"))
    }

  private def scalarParameter(valueType: Int): Either[Fault, KernelMetadata.Parameter] =
    pointerLeafOffsets(valueType, 0L).flatMap { pointers =>
      val width = typeSize(valueType)
      width.refine[Int > 0].leftMap(_ => Fault.InvalidRequest(s"SPIR-V parameter width must be positive: $width"))
        .map(KernelMetadata.Parameter(_, 0, KernelMetadata.ParameterKind.Scalar, pointers.nonEmpty))
    }
}
