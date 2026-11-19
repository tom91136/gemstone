package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.Workgroups
import scala.io.Source

/** Writes regenerated scheduling fields into the cubins under `controlwords`. */
class RegeneratedCubinSuite extends Suite {
  private def resource(name: String): Vec[Byte] =
    Option(getClass.getResourceAsStream(s"controlwords/$name")).fold(fail(s"missing resource $name"))(stream =>
      try Vec.unsafeWrapArray(stream.readAllBytes())
      finally stream.close()
    )

  private lazy val table: ControlWords.Table = ControlWords.tables.getOrElse("sm_89", fail("no sm_89 table"))

  private lazy val images: Vec[(String, Vec[Byte])] =
    Vec.from(Source.fromBytes(resource("manifest.tsv").toArray, "UTF-8").getLines().filter(_.nonEmpty).map { line =>
      val Array(source, target) = line.split('\t')
      val name                  = s"$source.$target.cubin"
      name -> resource(name)
    })

  private def regenerated(source: String, image: Vec[Byte]): Vec[Byte] =
    RegeneratedCubin(image, table).fold(error => fail(s"$source: $error"), identity)

  private case class Scheduled(kernel: String, path: ControlWords.Path, code: Vec[DecodedInstruction])

  private def scheduled(source: String, image: Vec[Byte]): Vec[Scheduled] = {
    val module  = Cubin.decode(image).fold(error => fail(s"$source: $error"), identity)
    val program = new LoadedProgram(module, Map.empty, Workgroups.Sequential, _ => None)
    module.kernels.flatMap { kernel =>
      val (graph, uses, instructions) =
        program.staticCode(kernel.name).fold(error => fail(s"$source ${kernel.name}: ${error.message}"), identity)
      ControlWords.paths(graph, uses, instructions).map(path =>
        Scheduled(kernel.name.raw, path, instructions.slice(path.first, path.first + path.steps.size))
      )
    }
  }

  test("writing back the fields ptxas wrote reproduces every image byte for byte") {
    assertEquals(images.size, 11)
    images.foreach((source, image) =>
      assertEquals(
        RegeneratedCubin.rewritten(image)((_, code) => Right(code.flatMap(_.scheduling))),
        Right(image),
        source
      )
    )
  }

  test("every path of a regenerated image decodes to its regenerated schedule, which meets the table") {
    images.foreach { (source, image) =>
      val before = scheduled(source, image)
      val after  = scheduled(source, regenerated(source, image))
      assertEquals(after.map(unit => (unit.kernel, unit.path)), before.map(unit => (unit.kernel, unit.path)), source)
      before.zip(after).foreach { (original, emitted) =>
        val name     = s"$source ${original.kernel} #${original.path.first}"
        val schedule = emitted.code.flatMap(_.scheduling)
        assertEquals(emitted.code.map(_.operation), original.code.map(_.operation), name)
        assertEquals(Right(schedule), ControlWords.regenerate(table, original.path), name)
        assertEquals(ControlWords.violations(table, emitted.path, schedule), Vec.empty[(Int, String)], name)
      }
    }
  }
}
