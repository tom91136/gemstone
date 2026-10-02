package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

object SassRuntimeCheck {
  def run(): Unit = {
    // SM89 EXIT.
    val bytes = "4d790000000000000000800300ea0f00".grouped(2)
      .map(value => Integer.parseInt(value, 16).toByte).toVec
    val decoded = CodeSection.decode(bytes.toArray, 89).fold(error => throw IllegalStateException(error), identity)
    require(
      decoded.instructions.size == 1 &&
        (decoded.instructions.head.operation match {
          case _: Instruction.Exit => true
          case _                   => false
        }),
      "runtime SASS fixture did not decode to EXIT"
    )
    val entry   = Cubin.Kernel("runtime_exit", 0, 0, 0, Vec.empty)
    val section = Cubin.Section(0, ".text.runtime_exit", 1, 0L, bytes.size, 0, 0, bytes)
    val module  = Cubin.Module(89, Vec(section), Vec(entry), Vec.empty)
    val kernel  = VMCompiler.compile(module, entry, Vec(DecodedFunction(entry.name, decoded.instructions)))
      .fold(error => throw IllegalStateException(error), identity)
    val geometry = LaunchGeometry.forLaunch(LaunchGeometry.Dimensions.One, LaunchGeometry.Dimensions.One)
      .fold(error => throw IllegalStateException(error.toString), identity)
    VMInterpreter.execute(kernel, geometry, Map.empty, ArrayCheckedMemory.allocate(0))
      .fold(error => throw IllegalStateException(error.message.toString), identity)
  }
}
