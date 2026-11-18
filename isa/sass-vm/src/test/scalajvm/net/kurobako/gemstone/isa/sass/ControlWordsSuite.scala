package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.Workgroups
import scala.io.Source

/** Regenerates the scheduling fields of the kernels under `controlwords`, which `compile.sh` builds with the ptxas the
  * sm_89 table names, and compares them with what ptxas wrote.
  */
class ControlWordsSuite extends Suite {
  private case class Scheduled(
      source: String,
      kernel: String,
      path: ControlWords.Path,
      words: Vec[BigInt],
      schedule: Vec[Scheduling],
      labels: Vec[Int]
  ) {
    def name: String = s"$source $kernel #${path.first}"
  }

  private def resource(name: String): Array[Byte] =
    Option(getClass.getResourceAsStream(s"controlwords/$name")).fold(fail(s"missing resource $name"))(stream =>
      try stream.readAllBytes()
      finally stream.close()
    )

  private lazy val table: ControlWords.Table = ControlWords.tables.getOrElse("sm_89", fail("no sm_89 table"))

  private lazy val sources: Vec[String] =
    Vec.from(Source.fromBytes(resource("manifest.tsv"), "UTF-8").getLines().filter(_.nonEmpty).map { line =>
      val Array(source, target) = line.split('\t')
      s"$source.$target.cubin"
    })

  private lazy val corpus: Vec[Scheduled] = sources.flatMap { source =>
    val module  = Cubin.decode(Vec.unsafeWrapArray(resource(source))).fold(error => fail(s"$source: $error"), identity)
    val program = new LoadedProgram(module, Map.empty, Workgroups.Sequential, Harness.text)
    module.kernels.flatMap { kernel =>
      val (graph, uses, instructions) =
        program.staticCode(kernel.name).fold(error => fail(s"$source ${kernel.name}: ${error.message}"), identity)
      val blocks  = graph.blocks
      val targets = blocks.indices.filter(block =>
        blocks.indices.exists(from => from != block - 1 && blocks(from).successors.contains(block))
      ).map(blocks(_).first)
      ControlWords.paths(graph, uses, instructions).map { path =>
        val code = instructions.slice(path.first, path.first + path.steps.size)
        Scheduled(
          source,
          kernel.name.raw,
          path,
          code.flatMap(_.encodingWord),
          code.flatMap(_.scheduling),
          Vec.from(targets.filter(label => label > path.first && label < path.first + path.steps.size))
        )
      }
    }
  }

  private def regenerated(unit: Scheduled): Vec[Scheduling] =
    ControlWords.regenerate(table, unit.path).fold(error => fail(s"${unit.name}: $error"), identity)

  private def path(source: String, kernel: String): Scheduled =
    corpus.find(unit => unit.source == source && unit.kernel == kernel).getOrElse(fail(s"no path of $kernel"))

  private def stalled(scheduling: Scheduling, stall: Int): Scheduling =
    scheduling.copy(stall = stall.unsafe[Int in 0 ~ 15])

  test("every kernel of the corpus has a ptxas schedule on each of its paths") {
    assertEquals(sources.size, 11)
    assertEquals(corpus.map(unit => (unit.source, unit.kernel)).distinct.size, 36)
    corpus.foreach { unit =>
      assertEquals(unit.schedule.size, unit.path.steps.size, unit.name)
      assertEquals(unit.words.size, unit.path.steps.size, unit.name)
    }
  }

  test("the table is no stricter than ptxas on the kernels it was harvested from") {
    val kept = corpus.filterNot(unit => ControlWords.heldOut(unit.source))
    assert(kept.nonEmpty && kept.size < corpus.size, "the corpus has harvested and held-out programs")
    kept.foreach(unit =>
      assertEquals(
        ControlWords.violations(table, unit.path, unit.schedule, harvested = true),
        Vec.empty[(Int, String)],
        unit.name
      )
    )
  }

  test("every path regenerates to a schedule that meets the table") {
    corpus.foreach(unit =>
      assertEquals(ControlWords.violations(table, unit.path, regenerated(unit)), Vec.empty[(Int, String)], unit.name)
    )
  }

  test("the fields ptxas wrote encode back to its words, and regenerated fields decode as written") {
    corpus.foreach { unit =>
      unit.words.zip(unit.schedule).foreach((word, scheduling) =>
        assertEquals(ControlWords.encode(word, scheduling), word, unit.name)
      )
      unit.words.zip(regenerated(unit)).foreach((word, scheduling) =>
        assertEquals(
          Scheduling.decode(Generation.Modern, ControlWords.encode(word, scheduling)),
          Right(Some(scheduling)),
          unit.name
        )
      )
    }
  }

  test("the checker reports a stall cut short of a register's distance") {
    val unit     = path("intchain.sm_89.cubin", "intchain")
    val schedule = regenerated(unit)
    val waiting  = schedule.indexWhere(_.stall.raw >= 4)
    assert(waiting >= 0, "the chain waits on a register")
    val cut   = schedule.updated(waiting, stalled(schedule(waiting), 1))
    val found = ControlWords.violations(table, unit.path, cut)
    assert(found.exists((_, reason) => reason.contains("before its registers")), found.mkString(", "))
    assertEquals(ControlWords.shorter(unit.path, schedule, schedule), Vec.empty[(Int, Int)])
    assert(ControlWords.shorter(unit.path, cut, schedule).nonEmpty, "the cut dependency is shorter than the original")
  }

  test("the checker reports a missing barrier wait and a missing barrier") {
    val unit     = path("loads.sm_89.cubin", "loadchain")
    val schedule = regenerated(unit)
    val waiter   = schedule.indexWhere(_.waitMask.raw != 0)
    val setter   = schedule.indexWhere(_.writeBarrier.nonEmpty)
    assert(waiter >= 0 && setter >= 0, "a load sets a barrier that its reader waits on")
    val unwaited = schedule.updated(waiter, schedule(waiter).copy(waitMask = 0.unsafe[Int in 0 ~ 0x3f]))
    val unset    = schedule.updated(setter, schedule(setter).copy(writeBarrier = None))
    assert(ControlWords.violations(table, unit.path, unwaited).exists(_._2.contains("misses a wait")))
    assert(ControlWords.violations(table, unit.path, unset).exists(_._2.contains("sets no write barrier")))
  }

  /** Whether an instruction at or after `label` reads a register that the path last wrote before it. */
  private def crosses(unit: Scheduled, label: Int): Boolean = {
    val steps  = unit.path.steps
    val at     = label - unit.path.first
    val before = steps.take(at).flatMap(_.writes).toSet
    steps.drop(at).foldLeft((before, false)) { case ((pending, found), step) =>
      (pending -- step.writes, found || step.reads.exists(pending))
    }._2
  }

  test("a path that falls through into a branch target meets the table across the label") {
    val crossing = corpus.filter(unit => unit.labels.exists(crosses(unit, _)))
    assertEquals(
      crossing.map(unit => s"${unit.name} ${unit.labels.mkString(",")}"),
      Vec(
        "branches.sm_89.cubin switchy #57 59",
        "branches.sm_89.cubin collatz #0 11,19",
        "branches.sm_89.cubin sumloop #0 23,61,85,87,103,108,117",
        "mufu.sm_89.cubin divide #15 16",
        "mufu.sm_89.cubin divide #47 84",
        "mufu.sm_89.cubin mufuchain #17 21",
        "mufu.sm_89.cubin mufuchain #80 81",
        "mufu.sm_89.cubin mufu #10 14",
        "mufu.sm_89.cubin mufu #34 38",
        "mufu.sm_89.cubin mufu #108 109",
        "mufu.sm_89.cubin mufu #113 133"
      )
    )
    crossing.foreach(unit =>
      assertEquals(ControlWords.violations(table, unit.path, regenerated(unit)), Vec.empty[(Int, String)], unit.name)
    )
  }
}
