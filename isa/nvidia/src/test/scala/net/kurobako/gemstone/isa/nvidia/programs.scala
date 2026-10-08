package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** Hand-assembled kernels over dense register and predicate slots. */
private object programs {
  def r(index: Int): Register       = Register(index.unsafe[Int >= 0])
  def p(index: Int): Predicate      = Predicate(index.unsafe[Int >= 0])
  def register(index: Int): Value   = Value.Register(r(index))
  def predicate(index: Int): Value  = Value.Predicate(p(index))
  def immediate(value: Long): Value = Value.Immediate(value)

  val emptyRoutine: Routine = Routine("test", ExecutableProgram(Vec.empty), 0, 0)

  def frame(laneCount: Int in 1 ~ 1024): VMLanes = VMFramePool(laneCount).acquire(emptyRoutine)

  def kernel(instructions: Vec[Instruction], parameters: Vec[KernelParameter] = Vec.empty): Kernel =
    Kernel(
      Entry(Routine("test", ExecutableProgram(instructions.map(PredicatedInstruction(_, None))), 0, 0), parameters),
      "PTX"
    )

  def execute(
      instructions: Vec[Instruction],
      memory: AddressSpace,
      threads: Int > 0 = 1,
      parameters: Vec[KernelParameter] = Vec.empty,
      arguments: Map[NonEmpty[String], Argument] = Map.empty
  ): Either[Fault, Unit] =
    LaunchGeometry.forLaunch(Dimensions(threads, 1, 1), Dimensions.One).left.map(Fault.InvalidRequest(_)).flatMap(
      VMInterpreter.execute(kernel(instructions, parameters), _, arguments, memory)
    )
}
