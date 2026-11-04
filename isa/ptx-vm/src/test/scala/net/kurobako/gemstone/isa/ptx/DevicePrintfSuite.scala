package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

class DevicePrintfSuite extends Suite {
  private def bytes(text: String): String = (text.getBytes("UTF-8").toSeq :+ 0.toByte).map(_ & 0xff).mkString(", ")

  private def run(format: String, store: String, expectedReturn: Int, threads: Int = 1): Vec[Event] = {
    val source = s""".version 8.0
      |.target sm_80
      |.address_size 64
      |.extern .func (.param .b32 vprintf_retval) vprintf(.param .b64 vprintf_format, .param .b64 vprintf_arguments);
      |.global .align 1 .b8 template[${format.length + 1}] = {${bytes(format)}};
      |.global .align 1 .b8 greeting[6] = {${bytes("hello")}};
      |.visible .entry probe() {
      | .local .align 8 .b8 depot[64];
      | .reg .u64 %depot, %arguments, %format, %word;
      | .reg .u32 %thread, %returned;
      | .reg .f64 %double;
      | .reg .pred %bad;
      | mov.u32 %thread, %tid.x;
      | mov.u64 %depot, depot;
      | cvta.local.u64 %arguments, %depot;
      | mov.u64 %word, greeting;
      | cvta.global.u64 %word, %word;
      | mov.f64 %double, 0d3FF8000000000000;
      | $store
      | mov.u64 %format, template;
      | cvta.global.u64 %format, %format;
      | {
      |  .param .b64 format;
      |  .param .b64 arguments;
      |  .param .b32 returned;
      |  st.param.b64 [format], %format;
      |  st.param.b64 [arguments], %arguments;
      |  call.uni (returned), vprintf, (format, arguments);
      |  ld.param.b32 %returned, [returned];
      | }
      | setp.ne.s32 %bad, %returned, $expectedReturn;
      | @%bad trap;
      | ret;
      |}
      |""".stripMargin
    val loaded = Parser.parse(source).left.map(_.message).flatMap(program =>
      LoadedProgram.load(program, text = execution.text).left.map(_.message)
    ).value
    val memory = RegionMemory()
    Vec[NonEmpty[String]]("template", "greeting").foreach { name =>
      val image   = loaded.global(name).value
      val region  = memory.allocate(image.initial)
      val binding = GlobalBinding.validated(name, region, 0L, image.size, 64L).value
      loaded.bindGlobal(name, binding, memory).fold(error => fail(error.message), identity)
    }
    val task = KernelTask.validated(
      "probe",
      LaunchGeometry.Dimensions(threads.unsafe[Int > 0], 1, 1),
      LaunchGeometry.Dimensions.One,
      Vec.empty
    ).value
    val events = mutable.ArrayBuffer.empty[Event]
    loaded.execute(task, TaskContext(memory, TaskControl(), event => { events += event; Right(()) }))
      .fold(error => fail(error.message), identity)
    Vec.from(events)
  }

  private def rendered(events: Vec[Event]): Vec[String] = events.map {
    case print: Event.Print => PrintRenderer.render(print, 4096).value
    case other              => fail(s"unexpected event $other")
  }

  test("vprintf decodes an argument buffer packed at natural alignment") {
    val events = run(
      "t=%d big=%lld d=%f s=%s c=%c\n",
      """st.local.u32 [depot], %thread;
        | st.local.u64 [depot+8], 5000000000;
        | st.local.f64 [depot+16], %double;
        | st.local.u64 [depot+24], %word;
        | st.local.u32 [depot+32], 65;""".stripMargin,
      5
    )
    assertEquals(rendered(events), Vec("t=0 big=5000000000 d=1.500000 s=hello c=A\n"))
  }

  test("vprintf prints from every active lane in lane order") {
    val events = run("lane %d\n", "st.local.u32 [depot], %thread;", 1, threads = 3)
    assertEquals(rendered(events), Vec("lane 0\n", "lane 1\n", "lane 2\n"))
  }

  test("vprintf with no conversions prints the format and returns 0") {
    assertEquals(rendered(run("plain 100%%\n", "", 0)), Vec("plain 100%\n"))
  }

  test("vprintf returns -1 and prints nothing for a conversion the device runtime rejects") {
    assertEquals(run("%n\n", "st.local.u32 [depot], %thread;", -1), Vec.empty[Event])
  }

  test("a null %s argument prints (null)") {
    assertEquals(rendered(run("[%s]\n", "st.local.u64 [depot], 0;", 1)), Vec("[(null)]\n"))
  }

  test("a null %p argument prints (nil) and a non-null one prints in hexadecimal") {
    val events =
      run("%p|%8p|%p\n", "st.local.u64 [depot], 0; st.local.u64 [depot+8], 0; st.local.u64 [depot+16], 4660;", 3)
    assertEquals(rendered(events), Vec("(nil)|   (nil)|0x1234\n"))
  }
}
