package net.kurobako.gemstone.isa.sass

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class DevicePrintfSuite extends Suite {

  // ptxas calls the driver's vprintf as it does malloc: JCAL or CALL.ABS.NOINC relocated to an undefined function, with
  // the format in R4:R5, the argument buffer in R6:R7 and the result in R4
  private def print(sm: Int, kind: Long, format: String, arguments: Vec[Byte]): (Long, Vec[Event]) = {
    val width   = if (sm < 70) 8 else 16
    val call    = if (sm < 70) "JCAL 0x0" else "CALL.ABS.NOINC 0x0"
    val bank    = if (sm < 70) 0x140 else 0x160
    val listing = Listing.program(
      s"LDC.64 R4, c[0x0][0x${bank.toHexString}]; LDC.64 R6, c[0x0][0x${(bank +
          8).toHexString}]; $call; LDC.64 R2, c[0x0][0x${(bank + 16).toHexString}]; ST.E [R2], R4; EXIT",
      "printing"
    ).value.instructions.zipWithIndex.map((instruction, index) => instruction.copy(address = (index * width).unsafe))
    val base    = Harness.module(sm, Vec(Entry("printing", bank.unsafe[Int >= 0], Vec(8, 8, 8))))
    val size    = listing.size * width
    val text    = base.sections.head.copy(size = size.unsafe[Int >= 0], bytes = Vec.fill(size)(0.toByte))
    val names   = Vec.from("\u0000vprintf\u0000".getBytes(StandardCharsets.UTF_8))
    val strings = Cubin.Section(base.sections.nonNegativeSize, ".strtab", 3, 0, names.nonNegativeSize, 0, 0, names)
    val symbol  = ByteBuffer.allocate(48).order(ByteOrder.LITTLE_ENDIAN)
    symbol.putInt(24, 1).put(28, 0x12.toByte)
    val symbols  = Cubin.Section(strings.index + 1, ".symtab", 2, 0, 48, strings.index, 0, Vec.from(symbol.array()))
    val entries  = LittleEndian.toVec(2L * width, 8) ++ LittleEndian.toVec((1L << 32) | kind, 8)
    val table    = Cubin.Section(symbols.index + 1, ".rel.text.printing", 9, 0, 16, symbols.index, text.index, entries)
    val module   = base.copy(sections = base.sections.updated(0, text) ++ Vec(strings, symbols, table))
    val compiled = VMCompiler.compile(module, module.kernels.head, Vec(DecodedFunction("printing", listing)))
      .map(_.kernel)
      .fold(error => fail(error.raw), identity)
    val memory   = Memory()
    val template = memory.allocate(Vec.from((format + "\u0000").getBytes(StandardCharsets.UTF_8)))
    val buffer   = memory.allocate(if (arguments.isEmpty) Vec(0.toByte) else arguments)
    val output   = memory.allocate(8L)
    val task     = KernelTask.validated(
      "printing",
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(template, 0L),
        KernelTask.Argument.Memory(buffer, 0L),
        KernelTask.Argument.Memory(output, 0L)
      )
    ).value
    memory.run(compiled, task).fold(error => fail(error.message), identity)
    (memory.readWords(output, 1).head, memory.events)
  }

  private def packed(entries: (Int, Long, Int)*): Vec[Byte] = {
    val bytes = Array.ofDim[Byte](entries.map((offset, _, size) => offset + size).maxOption.getOrElse(0))
    entries.foreach((offset, value, size) =>
      LittleEndian.toVec(value, size.unsafe[Int in 0 ~ 8]).zipWithIndex.foreach((byte, index) =>
        bytes(offset + index) = byte
      )
    )
    Vec.from(bytes)
  }

  for ((sm, kind) <- Vec(35 -> 0x0cL, 50 -> 0x2aL, 70 -> 0x3aL, 90 -> 0x4bL, 120 -> 0x4bL)) {
    test(s"sm_$sm calls relocated to the driver's vprintf print the decoded argument buffer") {
      val arguments = packed((0, -7L, 4), (8, 5000000000L, 8), (16, java.lang.Double.doubleToRawLongBits(1.5), 8))
      val (returned, events) = print(sm, kind, "a=%d b=%lld c=%f\n", arguments)
      assertEquals(returned, 3L)
      assertEquals(
        events,
        Vec(Event.Print(
          "a=%d b=%lld c=%f\n",
          Vec(
            Event.PrintArgument.Integer(Vec(-7L & 0xffffffffL), 32),
            Event.PrintArgument.Integer(Vec(5000000000L), 64),
            Event.PrintArgument.Floating(Vec(java.lang.Double.doubleToRawLongBits(1.5)), 64)
          )
        ))
      )
    }

    test(s"sm_$sm vprintf of a rejected format returns -1 and prints nothing") {
      val (returned, events) = print(sm, kind, "%n", packed((0, 0L, 4)))
      assertEquals(returned & 0xffffffffL, 0xffffffffL)
      assertEquals(events, Vec.empty[Event])
    }
  }

  test("an absolute register call to the driver's vprintf prints from each active lane") {
    val memory = program(
      89,
      """LDC.64 R4, c[0x0][0x160]
        |LDC.64 R6, c[0x0][0x168]
        |MOV R2, 0x10
        |MOV R3, 0xffffff00
        |CALL.ABS.NOINC R2
        |EXIT""".stripMargin
    ).launch(3)(
      Arg.Buffer(Vec.from("n=%d\u0000".getBytes(StandardCharsets.UTF_8))),
      Arg.Buffer(packed((0, 9L, 4)))
    ).memory
    val argument = Event.PrintArgument.Integer(Vec(9L), 32)
    assertEquals(Vec.from(memory.events), Vec.fill(3)(Event.Print("n=%d", Vec(argument))))
  }
}
