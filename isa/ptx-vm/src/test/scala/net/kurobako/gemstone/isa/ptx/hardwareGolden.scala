package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*

/** Hardware probe results, `inputs -> outputs` in hex words as the probe kit prints them, replayed with one thread per
  * row like the kit's `op_kernel`, so warp-wide instructions see the same lanes.
  */
private object hardwareGolden extends munit.Assertions {
  case class Row(inputs: Vec[Long], outputs: Vec[Long], text: String)

  private def words(text: String): Vec[Long] =
    Vec.from(text.trim.split(' ').filter(_.nonEmpty).map(java.lang.Long.parseLong(_, 16)))

  def rows(table: String): Vec[Row] = Vec.from(table.stripMargin.linesIterator.map(_.trim).filter(_.nonEmpty).map {
    line =>
      val arrow = line.indexOf("->")
      Row(words(line.take(arrow)), words(line.drop(arrow + 2)), line)
  })

  private def compare(table: Vec[Row], produced: Vec[Vec[Long]])(using munit.Location): Unit = {
    val wrong = table.zip(produced).filter((row, words) => row.outputs != words)
    if (wrong.nonEmpty)
      fail(
        s"${wrong.size} of ${table.size} rows differ from the hardware:\n" + wrong.take(8).map((row, words) =>
          s"  ${row.text}\n  gemstone ${words.map(word => f"$word%08x").mkString(" ")}"
        ).mkString("\n")
      )
  }

  private def launch(kernel: Kernel, threads: Int, arguments: Map[NonEmpty[String], Argument], memory: DeviceMemory)(
      using munit.Location
  ): Unit = execute(kernel, 1, threads.unsafe[Int > 0], arguments, memory).left.foreach(fault =>
    fail(s"rejected or faulted on the hardware's instructions: ${fault.message}")
  )

  /** Runs `body` on every row's thread with its inputs in `%a0` onwards, and compares `%r0` onwards. */
  def check(target: String, body: String, table: Vec[Row], registers: String = "")(using munit.Location): Unit = {
    val inputs  = table.head.inputs.size
    val outputs = table.head.outputs.size
    val loads   = Vec.tabulate(inputs)(index => s"ld.global.u32 %a$index, [%in+${index * 4}];").mkString(" ")
    val clears  = Vec.tabulate(outputs)(index => s"mov.b32 %r$index, 0;").mkString(" ")
    val stores  = Vec.tabulate(outputs)(index => s"st.global.u32 [%out+${index * 4}], %r$index;").mkString(" ")
    val source  = s""".version 8.7
      |.target $target
      |.address_size 64
      |.visible .entry golden(.param .u64 input, .param .u64 output) {
      |  .reg .b32 %a<${inputs.max(1)}>, %r<$outputs>, %tid;
      |  .reg .b64 %in, %out, %offset;
      |  $registers
      |  ld.param.u64 %in, [input];
      |  ld.param.u64 %out, [output];
      |  mov.u32 %tid, %tid.x;
      |  mul.wide.u32 %offset, %tid, ${inputs * 4};
      |  add.u64 %in, %in, %offset;
      |  mul.wide.u32 %offset, %tid, ${outputs * 4};
      |  add.u64 %out, %out, %offset;
      |  $loads
      |  $clears
      |  $body
      |  $stores
      |  ret;
      |}""".stripMargin
    val kernel = compileKernel(source)
    val output = (table.size * inputs * 4 + 15) & ~15
    val memory = DeviceMemory((output + table.size * outputs * 4).unsafe[Int >= 0])
    table.flatMap(_.inputs).zipWithIndex.foreach((value, index) =>
      assertEquals(memory.writeLittleEndian(memory.start.offset(index * 4L), value, 4), Right(()))
    )
    launch(
      kernel,
      table.size,
      Map(
        ("input": NonEmpty[String])  -> Argument.fromLong(memory.start.raw),
        ("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw + output)
      ),
      memory
    )
    compare(table, Vec.tabulate(table.size)(row => memory.words(output + row * outputs * 4L, outputs)))
  }

  /** Runs a probe op's own PTX on every row's thread: `body` reads `%hwi0` onwards and sets `%hwo0` onwards, and
    * `registers` declares the op entry's registers.
    */
  def checkCore(target: String, registers: String, body: String, table: Vec[Row])(using munit.Location): Unit = {
    val inputs  = table.head.inputs.size
    val outputs = table.head.outputs.size
    val loads   = Vec.tabulate(inputs)(index => s"ld.global.u32 %hwi$index, [%hwin+${index * 4}];").mkString(" ")
    val clears  = Vec.tabulate(outputs)(index => s"mov.b32 %hwo$index, 0;").mkString(" ")
    val stores  = Vec.tabulate(outputs)(index => s"st.global.u32 [%hwout+${index * 4}], %hwo$index;").mkString(" ")
    val source  = s""".version 8.7
      |.target $target
      |.address_size 64
      |.visible .entry golden(.param .u64 input, .param .u64 output) {
      |  .reg .b32 %hwi<$inputs>, %hwo<$outputs>, %hwtid;
      |  .reg .b64 %hwin, %hwout, %hwoffset;
      |  $registers
      |  ld.param.u64 %hwin, [input];
      |  ld.param.u64 %hwout, [output];
      |  mov.u32 %hwtid, %tid.x;
      |  mul.wide.u32 %hwoffset, %hwtid, ${inputs * 4};
      |  add.u64 %hwin, %hwin, %hwoffset;
      |  mul.wide.u32 %hwoffset, %hwtid, ${outputs * 4};
      |  add.u64 %hwout, %hwout, %hwoffset;
      |  $loads
      |  $clears
      |  $body
      |  $stores
      |  ret;
      |}""".stripMargin
    val kernel =
      VMCompiler.compile(source).fold(error => fail(s"rejected the hardware's PTX: ${error.message}"), identity)
    val output = (table.size * inputs * 4 + 15) & ~15
    val memory = DeviceMemory((output + table.size * outputs * 4).unsafe[Int >= 0])
    table.flatMap(_.inputs).zipWithIndex.foreach((value, index) =>
      assertEquals(memory.writeLittleEndian(memory.start.offset(index * 4L), value, 4), Right(()))
    )
    launch(
      kernel,
      table.size,
      Map(
        ("input": NonEmpty[String])  -> Argument.fromLong(memory.start.raw),
        ("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw + output)
      ),
      memory
    )
    compare(table, Vec.tabulate(table.size)(row => memory.words(output + row * outputs * 4L, outputs)))
  }
}
