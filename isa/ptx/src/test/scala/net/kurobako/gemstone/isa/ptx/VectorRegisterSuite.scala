package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.ptx.asm.*
import net.kurobako.gemstone.memory.*

class VectorRegisterSuite extends munit.FunSuite {
  for {
    count  <- Vec(2, 4)
    width  <- Vec(16, 32, 64)
    prefix <- Vec("", "%")
    if count * width <= 128
  } test(s"named v$count u$width vectors ($prefix) move simultaneously and retain component aliases") {
    val parts    = Vec("x", "y", "z", "w").take(count)
    val colours  = Vec("r", "g", "b", "a").take(count)
    val initial  = Vec.tabulate(count)(_ + 1)
    val reversed = parts.reverse.map(part => s"$prefix" + s"v.$part").mkString("{", ",", "}")
    val forward  = colours.map(part => s"$prefix" + s"v.$part").mkString("{", ",", "}")
    val bytes    = count * width / 8
    val source   = s""".version 8.0
      .target sm_80
      .address_size 64
      .visible .entry main(.param .u64 output) {
        .reg .u64 %output;
        .reg .v$count .u$width ${prefix}v, ${prefix}copy;
        ld.param.u64 %output, [output];
        mov.v$count.u$width ${prefix}v, ${initial.mkString("{", ",", "}")};
        mov.u$width ${prefix}v.x, 7;
        mov.v$count.u$width ${prefix}copy, ${prefix}v;
        mov.v$count.u$width $reversed, $forward;
        st.global.v$count.u$width [%output], ${prefix}v;
        st.global.v$count.u$width [%output+$bytes], ${prefix}copy;
        ld.global.v$count.u$width ${prefix}copy, [%output];
        st.global.v$count.u$width [%output+${2 * bytes}], ${prefix}copy;
        ret;
      }"""
    val parsed = Parser.parse(source).fold(error => fail(error.message), identity)
    assertEquals(Parser.parse(Printer.render(parsed)), Right(parsed))
    val kernel = compileKernel(source)
    val memory = DeviceMemory((bytes * 3).unsafe[Int >= 0])
    assertEquals(
      execute(
        kernel,
        1,
        1,
        Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
        memory
      ),
      Right(())
    )
    val changed  = initial.updated(0, 7)
    val expected = (changed.reverse ++ changed ++ changed.reverse).flatMap(value =>
      LittleEndian.toVec(value.toLong, (width / 8).unsafe[Int where Between[0, 8]])
    )
    assertEquals(memory.readBytes(memory.start, expected.nonNegativeSize), Right(expected))
  }
}
