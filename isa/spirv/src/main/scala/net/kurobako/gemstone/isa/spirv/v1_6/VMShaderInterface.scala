package net.kurobako.gemstone.isa.spirv.v1_6

import VMIR.*
import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*

/** The shared argument ABI of Vulkan shader preparation and execution. */
private object VMShaderInterface {
  private type Program = VMProgramRuntime

  def stageInterfaceVariables(program: Program, name: NonEmpty[String]): Vec[Int] =
    if (!program.graphicsEntries.contains(name)) Vec.empty
    else Vec.from(program.ir.entryPointInterfaces.getOrElse(name, Set.empty).toSeq.filter { variable =>
      program.globals.get(variable).exists(global =>
        global.storageClass == StorageClass.Input || global.storageClass == StorageClass.Output
      )
    }.sorted)

  def argumentVariables(program: Program, name: NonEmpty[String]): Vec[Int] = {
    val declared = program.ir.entryPointInterfaces.getOrElse(name, Set.empty)
    val scoped   = program.descriptorBindings.filter { case (variable, _) => declared.contains(variable) }
    // Before SPIR-V 1.4, descriptor globals were not required in OpEntryPoint's
    // interface. Retain compatibility with those modules when none are listed.
    val bindings =
      if (program.module.header.version.minor.raw >= 4 || scoped.nonEmpty) scoped else program.descriptorBindings
    Vec.from(bindings.toSeq.sortBy { case (variable, (set, binding)) =>
      (set, binding, variable)
    }.flatMap { case (variable, _) =>
      val count =
        program.globals.get(variable).flatMap(g =>
          program.layout.runtimeType(
            g.pointerType
          ).collect { case RuntimeType.Pointer(_, pointee) => pointee }.flatMap(program.layout.runtimeType(_))
        ) match {
          case Some(RuntimeType.Array(_, count)) => Math.toIntExact(count)
          case _                                 => 1
        }
      Seq.fill(count)(variable)
    }) ++ Vec.from(program.globals.toSeq.collect {
      case (variable, global)
          if global.storageClass == StorageClass.PushConstant &&
            (program.ir.module.header.version.minor.raw < 4 || declared.contains(variable)) => variable
    }.sorted) ++ stageInterfaceVariables(program, name)
  }

  def metadata(program: Program, name: NonEmpty[String]): Either[Fault, KernelMetadata] =
    program.entryPoints.get(name).flatMap(program.functions.get) match {
      case None    => Left(Fault.InvalidTask(s"SPIR-V entry point $name has no function body"))
      case Some(_) =>
        val functionId  = program.entryPoints(name)
        val descriptors = argumentVariables(program, name)
        val parameters  = descriptors.indices.map(index =>
          KernelMetadata.Parameter(
            8,
            (index * 8).unsafe[Int >= 0],
            KernelMetadata.ParameterKind.Allocation,
            containsPointers = false
          )
        )
        KernelMetadata.validated(
          (descriptors.size * 8).unsafe[Int >= 0],
          descriptors.size.unsafe[Int >= 0],
          Vec.from(parameters),
          requiredWorkGroupSize = program.workGroupSize(functionId),
          workGroupSizeHint = program.workGroupSizeHint(functionId),
          entryPointId = Some(functionId.unsafe[Int >= 0]),
          bindingIndices =
            Vec.from(descriptors.flatMap(variable =>
              program.descriptorBindings.get(variable).map(binding => binding._2)
            ))
        ).leftMap(detail => Fault.InvalidTask(detail))
    }

}
