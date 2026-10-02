package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

object PtxRuntimeCheck {
  def run(): Unit = {
    val source = """.version 8.0
      |.target sm_89
      |.address_size 64
      |.visible .entry runtime_store(.param .u64 output) {
      |  .reg .u64 %out;
      |  .reg .u32 %value;
      |  ld.param.u64 %out, [output];
      |  mov.u32 %value, 42;
      |  st.global.u32 [%out], %value;
      |  ret;
      |}
      |""".stripMargin
    val kernel   = VMCompiler.compile(source).fold(error => throw IllegalStateException(error.message.raw), identity)
    val memory   = ArrayCheckedMemory.allocate(4)
    val geometry = LaunchGeometry.forLaunch(LaunchGeometry.Dimensions.One, LaunchGeometry.Dimensions.One)
      .fold(error => throw IllegalStateException(error.toString), identity)
    VMInterpreter.execute(
      kernel,
      geometry,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
      memory
    ).fold(error => throw IllegalStateException(error.message.toString), identity)
    val value =
      memory.readInt32(memory.allocation.start).fold(error => throw IllegalStateException(error.toString), identity)
    require(value == 42, s"runtime PTX memory store produced $value instead of 42")
  }
}
