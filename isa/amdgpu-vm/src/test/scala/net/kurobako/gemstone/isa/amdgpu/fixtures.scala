package net.kurobako.gemstone.isa.amdgpu

import CodeKernels.*
import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

private object fixtures extends munit.Assertions {

  /** Little-endian machine words, as LLVM prints instruction dwords. */
  def words(values: Int*): Vec[Byte] = Vec.unsafeWrapArray(bytes.words(values*))

  def validate(code: Vec[Byte], target: Target): Either[NonEmpty[String], Unit] =
    VMCompiler.decodeProgram(code, TargetMode(target, TargetDefaults.waveSize(target))).map(_ => ())

  def decoded(target: Target, code: Vec[Byte])(using munit.Location): VMIR.Instruction =
    VMCompiler.decodeInstruction(code, target).fold(error => fail(error.raw), identity)

  def decoded(target: Target, code: Int*)(using munit.Location): VMIR.Instruction =
    decoded(target, Vec.from(code.map(_.toByte)))

  def singleTask(arguments: KernelTask.Argument*)(using munit.Location): KernelTask =
    KernelTask.validated("kernel", Dimensions.One, Dimensions.One, Vec.from(arguments)).value

  def runIR(
      operations: Vec[VMIR.Instruction],
      memory: AddressSpace,
      source: CodeObjectKernel = codeKernel(),
      task: KernelTask = singleTask()
  ): Either[Fault, Unit] = executeKernel(irKernel(operations, source), task, memory)

  def compileKernel(kernel: CodeObjectKernel): VMIR.Kernel =
    VMCompiler().compile(kernel).fold(error => fail(error.message), identity)

  def executeKernel(
      kernel: VMIR.Kernel,
      task: KernelTask,
      memory: AddressSpace,
      control: TaskControl = TaskControl(),
      workgroups: Workgroups = Workgroups.Sequential,
      schedule: ConcurrencySchedule = ConcurrencySchedule.Fixed
  ): Either[Fault, Unit] =
    VMInterpreter.execute(
      kernel,
      KernelLaunch(task, TaskContext(memory, control, _ => Right(()), schedule = schedule), workgroups),
      Vec.empty
    )

  def minimalCodeObject(globalPointer: Boolean = false): Array[Byte] =
    elfImage.amdgpu(bytes.words(0xbf810000), kernargSize = 24, kernelCodeProperties = 0x0a, globalPointer)
}
