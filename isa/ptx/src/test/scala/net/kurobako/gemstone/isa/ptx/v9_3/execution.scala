package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.ptx.v9_3.Program.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

private object execution extends munit.Assertions {
  def geometry(blocks: Int > 0, threads: Int > 0): LaunchGeometry =
    LaunchGeometry.forLaunch(Dimensions(threads, 1, 1), Dimensions(blocks, 1, 1)).toOption.get

  def compileKernel(source: String)(using munit.Location): Kernel =
    VMCompiler.compile(source).fold(error => fail(error.message), identity)

  def compileKernel(entry: Statement.Entry, functions: Vec[Statement.Function])(using munit.Location): Kernel =
    VMCompiler.compile(entry, functions).fold(error => fail(error.message), identity)

  def execute(
      kernel: Kernel,
      blocks: Int > 0,
      threads: Int > 0,
      arguments: Map[NonEmpty[String], Argument],
      memory: AddressMemory,
      control: TaskControl = TaskControl(),
      workgroups: Workgroups = Workgroups.Sequential
  ): Either[VMFault, Unit] =
    VMInterpreter.execute(kernel, geometry(blocks, threads), arguments, memory, control, workgroups)
}
