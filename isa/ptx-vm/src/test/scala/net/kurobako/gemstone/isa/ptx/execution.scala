package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

private object execution extends munit.Assertions {
  def geometry(blocks: Int > 0, threads: Int > 0): LaunchGeometry =
    LaunchGeometry.forLaunch(Dimensions(threads, 1, 1), Dimensions(blocks, 1, 1)).value

  def compileKernel(source: String)(using munit.Location): Kernel =
    VMCompiler.compile(source).value

  def compileKernel(source: String, globals: Map[NonEmpty[String], GuestAddress])(using munit.Location): Kernel = {
    val program = Parser.parse(source).value
    val entry   = program.statements.collectFirst { case entry: Program.Statement.Entry => entry.name }.get
    VMCompiler.module(program).flatMap(VMCompiler.compile(_, entry, globals)).value
  }

  def compileProgram(program: Program, entry: NonEmpty[String])(using munit.Location): Kernel =
    VMCompiler.module(program).flatMap(VMCompiler.compile(_, entry, Map.empty)).value

  def execute(
      kernel: Kernel,
      blocks: Int > 0,
      threads: Int > 0,
      arguments: Map[NonEmpty[String], Argument],
      memory: AddressSpace,
      control: TaskControl = TaskControl(),
      workgroups: Workgroups = Workgroups.Sequential,
      dynamicSharedBytes: Int >= 0 = 0
  ): Either[Fault, Unit] =
    VMInterpreter.execute(kernel, geometry(blocks, threads), arguments, memory, control, workgroups, dynamicSharedBytes)
}
