package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable

class ObserverSuite extends Suite {
  private val module = spv"""
    $openCL
    OpEntryPoint Kernel %main "store" %lid
    OpDecorate %lid BuiltIn LocalInvocationIndex
    %void = OpTypeVoid
    %uint = OpTypeInt 32 0
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %lid_ptr = OpTypePointer Input %uint
    %lid = OpVariable %lid_ptr Input
    %fn = OpTypeFunction %void %uint_ptr
    %main = OpFunction %void None %fn
    %out = OpFunctionParameter %uint_ptr
    %entry = OpLabel
    %lane = OpLoad %uint %lid
    %slot = OpPtrAccessChain %uint_ptr %out %lane
    OpStore %slot %lane
    OpReturn
    OpFunctionEnd
  """

  private val store = module.instructions.indexWhere { case Instruction.Store(_, _, _) => true; case _ => false }
  private val lane  = module.instructions.collectFirst { case Instruction.Load(dest = dest) =>
    dest.words.head.raw
  }.get

  private class Recording extends LaunchObserver {
    val stores: mutable.ArrayBuffer[(Option[Int], Option[InvocationView.Value])] = mutable.ArrayBuffer.empty
    val seen: mutable.ArrayBuffer[Long]                                          = mutable.ArrayBuffer.empty
    override def instruction(at: Fault.Location, state: ArchitecturalState): Either[Fault, Unit] = {
      seen ++= at.instruction.map(_.raw)
      state match {
        case view: InvocationView if at.instruction.exists(_.raw == store.toLong) =>
          stores += at.lane.map(_.raw) -> view.value(lane)
        case _: InvocationView => ()
        case other             => fail(s"unexpected state $other")
      }
      Right(())
    }
  }

  test("an observer sees each invocation's instructions with its values, in sequential order") {
    val memory   = RegionMemory()
    val out      = memory.allocate(8L)
    val observer = Recording()
    val result   = load(module).flatMap(program =>
      execute(
        program,
        task("store", Vec(buffer(out)), Dimensions(2, 1, 1)),
        memory,
        TaskControl(observer = Some(observer))
      )
    )
    assertEquals(result, Right(()))
    assertEquals(
      observer.stores.toVector,
      Vector(
        Some(0) -> Some(InvocationView.Value.Bits(Vec(0L))),
        Some(1) -> Some(InvocationView.Value.Bits(Vec(1L)))
      )
    )
    assertEquals(observer.seen.count(_ == store.toLong), 2)
    assertEquals(word(memory, out, 4), 1L)
  }

  test("an observer stops a launch before the instruction executes") {
    val memory               = RegionMemory()
    val out                  = memory.allocate(8L)
    val stop: LaunchObserver = (at, _) =>
      if (at.instruction.exists(_.raw == store.toLong)) Left(Fault.Cancelled) else Right(())
    val result = load(module).flatMap(program =>
      execute(program, task("store", Vec(buffer(out)), Dimensions(2, 1, 1)), memory, TaskControl(observer = Some(stop)))
    )
    assertEquals(result, Left(Fault.Cancelled))
    assertEquals(word(memory, out, 0), 0L)
  }
}
