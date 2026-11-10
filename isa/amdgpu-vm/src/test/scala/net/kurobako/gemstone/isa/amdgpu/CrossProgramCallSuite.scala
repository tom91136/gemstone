package net.kurobako.gemstone.isa.amdgpu

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.profile.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

class CrossProgramCallSuite extends Suite {
  private val TextOffset = 0x1000L
  private val BssOffset  = 0x3000L

  private def window(id: Long): Long = CodeWindows.Base + id * CodeWindows.Stride

  private def programId(id: Long): ProgramId = ProgramId(id.unsafe[Long > 0])

  private def program(code: Int*): LoadedProgram =
    LoadedProgram.load(CodeObject.decode(Vec.unsafeWrapArray(
      elfImage.amdgpu(bytes.words(code*), kernargSize = 0, kernelCodeProperties = 0x0a)
    )).value)

  // s_mov_b32 s4, lo; s_mov_b32 s5, hi; s_swappc_b64 s[2:3], s[4:5]; s_trap 2; s_endpgm
  private def caller(target: Long): LoadedProgram = program(
    0xbe8400ff,
    target.toInt,
    0xbe8500ff,
    (target >>> 32).toInt,
    0xbe821e04,
    0xbf920002,
    0xbf810000
  )

  private val returns = 0xbe841e02 // s_swappc_b64 s[4:5], s[2:3]

  private def session(programs: LoadedProgram*): TaskContext = session(TaskControl(), programs*)

  private def session(control: TaskControl, programs: LoadedProgram*): TaskContext = {
    programs.zipWithIndex.foreach { case (loaded, index) =>
      assertEquals(loaded.bindFunctionPointerNamespace(programId(index + 1L)), Right(()))
    }
    val directory: ProgramDirectory = id => programs.lift((id.raw - 1L).toInt)
    TaskContext(RegionMemory(), control, _ => Right(()), directory)
  }

  private def run(first: LoadedProgram, context: TaskContext): Either[String, Unit] =
    first.execute(singleTask(), context).left.map(_.message)

  private def trapAt(code: Int, address: Long): Either[String, Unit] =
    Left(s"AMDGPU s_trap $code at pc 0x${address.toHexString}")

  private class Lost extends LaunchObserver {
    val kernels: mutable.ArrayBuffer[String] = mutable.ArrayBuffer.empty[String]
    def instruction(at: Fault.Location, state: ArchitecturalState): Either[Fault, Unit] = Right(())
    override def controlFlowLost(kernel: String): Unit                                  = kernels += kernel
  }

  test("a loaded program records one control flow graph for each launch") {
    val recorder = ProfileRecorder()
    val loaded   = program(0xbf810000)
    assertEquals(run(loaded, session(TaskControl(observer = Some(recorder)), loaded)), Right(()))
    assertEquals(recorder.stream.graphs.map(_.kernel).toVector, Vector("kernel"))
  }

  test("a call into another program drops the launch's control flow and reports the loss once") {
    val recorder = ProfileRecorder()
    val lost     = Lost()
    val first    = caller(window(2) + TextOffset)
    val control  = TaskControl(observer = Some(LaunchObserver.both(recorder, lost)))
    assertEquals(run(first, session(control, first, program(0xbf920001))), trapAt(1, window(2) + TextOffset))
    assert(recorder.stream.graphs.isEmpty)
    assertEquals(lost.kernels.toVector, Vector("kernel"))
  }

  test("returning from another program to the caller reports no further loss") {
    val lost    = Lost()
    val first   = caller(window(2) + TextOffset)
    val context = session(TaskControl(observer = Some(lost)), first, program(returns))
    assertEquals(run(first, context), trapAt(2, window(1) + TextOffset + 20))
    assertEquals(lost.kernels.toVector, Vector("kernel"))
  }

  test("a call through a function pointer enters the code of another program and runs it there") {
    val first   = caller(window(2) + TextOffset)
    val context = session(first, program(0xbf920001))
    assertEquals(run(first, context), trapAt(1, window(2) + TextOffset))
  }

  test("a callee returns to the instruction after the call in the caller's program") {
    val first   = caller(window(2) + TextOffset)
    val context = session(first, program(returns))
    assertEquals(run(first, context), trapAt(2, window(1) + TextOffset + 20))
  }

  test("a callee reads its own program's globals") {
    val global = window(2) + BssOffset
    // load device_global into s0 and return only when it holds 5
    val callee = program(
      0xbe8600ff,
      global.toInt,
      0xbe8700ff,
      (global >>> 32).toInt,
      0xc0020003,
      0x00000000,
      0xbf8cc07f,
      0xbf068500,
      0xbf850001,
      0xbf920003,
      returns
    )
    val first   = caller(window(2) + TextOffset)
    val context = session(first, callee)
    assertEquals(run(first, context), trapAt(3, window(2) + TextOffset + 36))
    assertEquals(callee.writeGlobal("device_global", 0L, Vec[Byte](5, 0, 0, 0)), Right(()))
    assertEquals(run(first, context), trapAt(2, window(1) + TextOffset + 20))
  }

  test("a call into a window that holds no program faults, naming the target") {
    val target  = window(5) + TextOffset
    val first   = caller(target)
    val context = session(first, program(returns))
    assertEquals(run(first, context), Left(f"AMDGPU call target 0x$target%X is not present in the code object"))
  }

  test("a call to an address outside the callee's text faults") {
    val target  = window(2) + TextOffset + 0x100
    val first   = caller(target)
    val context = session(first, program(returns))
    assertEquals(run(first, context), Left(f"AMDGPU call target 0x$target%X is not present in the code object"))
  }

  test("loaded programs report globals inside their own windows") {
    val first     = program(0xbf810000)
    val second    = program(0xbf810000)
    val _         = session(first, second)
    val addresses = Vec(first, second).map(
      _.symbols.value.globals.collectFirst {
        case global if global.name == "device_global" => global.virtualAddress.get.raw
      }.get
    )
    assertEquals(addresses, Vec(window(1) + BssOffset, window(2) + BssOffset))
  }

  test("a program binds a window only before it is used and only once the image fits") {
    val loaded = program(0xbf810000)
    assertEquals(loaded.bindFunctionPointerNamespace(programId(1L)), Right(()))
    assertEquals(loaded.bindFunctionPointerNamespace(programId(1L)), Right(()))
    val _ = loaded.symbols.value
    assert(loaded.bindFunctionPointerNamespace(programId(2L)).isLeft)
    assert(program(0xbf810000).bindFunctionPointerNamespace(programId(1L + CodeWindows.Limit)).isLeft)
  }
}
