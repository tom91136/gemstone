package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.collection.*
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

  /** The `bytes`-wide words entry `name` stores from `%out` on, after one thread runs `body` over `%in` addressing
    * `inputs`.
    */
  def runWords(
      name: String,
      target: String,
      registers: String,
      body: String,
      inputs: Vec[Long],
      outputs: Int,
      bytes: Int in (4, 8)
  )(using munit.Location): Vec[Long] = {
    val source = s""".version 8.7
      |.target $target
      |.address_size 64
      |.visible .entry $name(.param .u64 input, .param .u64 output) {
      |  .reg .u64 %in, %out;
      |  $registers
      |  ld.param.u64 %in, [input];
      |  ld.param.u64 %out, [output];
      |  $body
      |  ret;
      |}""".stripMargin
    val kernel = compileKernel(source)
    val width  = bytes.raw
    val size   = bytes.widen[Int in 1 ~ 8]
    // outputs start 16-byte aligned so a kernel may store them as wider words or vectors
    val output = (inputs.size * width + 15) & ~15
    val memory = DeviceMemory((output + outputs * width).unsafe[Int >= 0])
    inputs.zipWithIndex.foreach((value, index) =>
      assertEquals(memory.writeLittleEndian(memory.start.offset(index.toLong * width), value, size), Right(()))
    )
    val result = execute(
      kernel,
      1,
      1,
      Map(
        ("input": NonEmpty[String])  -> Argument.fromLong(memory.start.raw),
        ("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw + output)
      ),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    Vec.tabulate(outputs)(index =>
      memory.readLittleEndian(memory.start.offset(output + index.toLong * width), size).toOption.get
    )
  }

  def execute(
      kernel: Kernel,
      blocks: Int > 0,
      threads: Int > 0,
      arguments: Map[NonEmpty[String], Argument],
      memory: AddressSpace,
      control: TaskControl = TaskControl(),
      workgroups: Workgroups = Workgroups.Sequential,
      dynamicSharedBytes: Int >= 0 = 0,
      cluster: Option[Dimensions] = None
  ): Either[Fault, Unit] =
    VMInterpreter.execute(
      kernel,
      geometry(blocks, threads),
      arguments,
      memory,
      control,
      workgroups,
      dynamicSharedBytes,
      cluster
    )
}
