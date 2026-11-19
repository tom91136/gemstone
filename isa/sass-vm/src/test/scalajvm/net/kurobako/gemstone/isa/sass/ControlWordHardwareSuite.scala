package net.kurobako.gemstone.isa.sass

import java.security.MessageDigest
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import scala.io.Source
import scala.util.Using

/** What an RTX 4060 Ti (sm_89, driver 620.70.06) computed from the kernels under `controlwords` and from the same
  * cubins with regenerated scheduling fields. `hardware.tsv` holds, per launch, the arguments (`b:` a buffer, `s:` a
  * scalar, as little-endian hex) and the buffers the card returned, which were the same from both cubins;
  * `regenerated.sha256` names the regenerated images that ran. `exact-cuts.tsv` holds, for each distance of the table
  * that a one-cycle cut made the card compute wrongly, one such cut as the instruction, its cut stall, the consumer,
  * the producer and use the distance belongs to, the distance, and how many of 20 input sets the card got wrong.
  */
class ControlWordHardwareSuite extends Suite {
  private def resource(name: String): Vec[Byte] =
    Option(getClass.getResourceAsStream(s"controlwords/$name")).fold(fail(s"missing resource $name"))(stream =>
      try Vec.unsafeWrapArray(stream.readAllBytes())
      finally stream.close()
    )

  private def lines(name: String): Vec[Vec[String]] =
    Vec.from(Source.fromBytes(resource(name).toArray, "UTF-8").getLines().filter(_.nonEmpty).map(line =>
      Vec.from(line.split('\t'))
    ))

  private lazy val table: ControlWords.Table = ControlWords.tables.getOrElse("sm_89", fail("no sm_89 table"))

  private def hex(bytes: Vec[Byte]): String = bytes.map(byte => f"${byte & 0xff}%02x").mkString

  private def unhex(text: String): Vec[Byte] =
    Vec.from(text.grouped(2).map(Integer.parseInt(_, 16).toByte))

  private def regenerated(source: String): Vec[Byte] =
    RegeneratedCubin(resource(source), table).fold(error => fail(s"$source: $error"), identity)

  private case class Launch(source: String, kernel: String, block: Int, grid: Int, arguments: Vec[String], card: String)

  private lazy val launches: Vec[Launch] = lines("hardware.tsv").map {
    case Vec(source, kernel, block, grid, arguments, card) =>
      Launch(source, kernel, block.toInt, grid.toInt, Vec.from(arguments.split(' ')), card)
    case other => fail(s"malformed launch $other")
  }

  private def run(image: Vec[Byte], launch: Launch): String =
    Using.resource(ExecutionSession(List(CubinBackend(text = Harness.text)))) { service =>
      val buffers = launch.arguments.map(argument =>
        Option.when(argument.startsWith("b:")) {
          val bytes  = unhex(argument.drop(2))
          val region = service.registerAllocation(bytes.size.unsafe[Int > 0]).value
          assert(service.write(region, 0L, bytes).isRight)
          region -> bytes.size
        }
      )
      val arguments = launch.arguments.zip(buffers).map {
        case (_, Some((region, _))) => KernelTask.Argument.Memory(region, 0L)
        case (argument, None)       =>
          KernelTask.Argument.Scalar(unhex(argument.drop(2)).refine[NonEmpty[Vec[Byte]]].value)
      }
      val program = service.load(ProgramEncoding.CUDACubin, image).value
      service.symbols(program).value.globals.foreach { symbol =>
        val global  = service.global(program, symbol.name).value
        val storage = service.registerAllocation(global.size.raw.toInt.max(1).unsafe[Int > 0]).value
        assert(service.write(storage, 0L, global.initial).isRight)
        assertEquals(service.bindGlobal(program, symbol.name, storage, 0L), Right(()))
      }
      val task = KernelTask.validated(
        launch.kernel.unsafe[NonEmpty[String]],
        LaunchGeometry.Dimensions(launch.block.unsafe[Int > 0], 1, 1),
        LaunchGeometry.Dimensions(launch.grid.unsafe[Int > 0], 1, 1),
        arguments
      ).value
      assertEquals(service.completion(service.launch(program, task).value), Right(CompletionStatus.Succeeded))
      buffers.flatten.map((region, size) =>
        hex(service.read(AllocationRange(region, 0L, size.unsafe[Int >= 0])).value)
      ).mkString("|")
    }

  test("the regenerated images are the ones that ran on the card") {
    val recorded = lines("regenerated.sha256").map(line =>
      line.head.split("  ").toSeq match {
        case Seq(digest, source) => source -> digest
        case other               => fail(s"malformed digest $other")
      }
    )
    assertEquals(recorded.map(_._1).distinct.size, 11)
    recorded.foreach((source, digest) =>
      assertEquals(
        hex(Vec.unsafeWrapArray(MessageDigest.getInstance("SHA-256").digest(regenerated(source).toArray))),
        digest,
        source
      )
    )
  }

  test("the original and the regenerated cubin compute in the SASS VM what both computed on the card") {
    assertEquals(launches.map(_.kernel).distinct.size, 36)
    val images = launches.map(_.source).distinct.map(source => source -> regenerated(source)).toMap
    launches.foreach { launch =>
      val name = s"${launch.kernel} ${launch.arguments.mkString(" ")}"
      assertEquals(run(resource(launch.source), launch), launch.card, s"original $name")
      assertEquals(run(images(launch.source), launch), launch.card, s"regenerated $name")
    }
  }

  /** Stalls cut by hand in ptxas's schedule, as instruction and stall, with how many of 500 input sets of 10 launches
    * each the card returned wrong from one thread and from one warp; stall 0 drains.
    */
  private val cuts = Vec(
    ("intchain.sm_89.cubin", "intchain", 5, 4, 499, 499),
    ("intchain.sm_89.cubin", "intchain", 5, 3, 499, 499),
    ("intchain.sm_89.cubin", "intchain", 5, 2, 500, 499),
    ("intchain.sm_89.cubin", "intchain", 5, 1, 499, 499),
    ("intchain.sm_89.cubin", "intchain", 5, 0, 0, 0),
    ("floatchain.sm_89.cubin", "floatchain", 5, 3, 500, 500),
    ("floatchain.sm_89.cubin", "floatchain", 5, 2, 500, 500),
    ("floatchain.sm_89.cubin", "floatchain", 5, 1, 500, 500),
    ("floatchain.sm_89.cubin", "floatchain", 5, 0, 0, 0)
  )

  test("the checker reports exactly the stall cuts the card computed wrongly") {
    cuts.foreach { (source, kernel, at, stall, thread, warp) =>
      val module                      = Cubin.decode(resource(source)).value
      val program                     = new LoadedProgram(module, Map.empty, Workgroups.Sequential, _ => None)
      val (graph, uses, instructions) = program.staticCode(kernel.unsafe[NonEmpty[String]]).value
      val path                        = ControlWords.paths(graph, uses, instructions)
        .find(path => path.first <= at && at < path.first + path.steps.size).getOrElse(fail(s"$kernel has no #$at"))
      val schedule = instructions.slice(path.first, path.first + path.steps.size).flatMap(_.scheduling)
      val cut   = schedule.updated(at - path.first, schedule(at - path.first).copy(stall = stall.unsafe[Int in 0 ~ 15]))
      val found = ControlWords.violations(table, path, cut, harvested = true)
      assertEquals(
        found.exists((index, reason) => index == at + 1 && reason.contains("before its registers")),
        thread > 0 || warp > 0,
        s"$kernel #$at stall $stall: ${found.mkString(", ")}"
      )
    }
  }

  test("the checker flags the cut that leaves the card a cycle short at each distance it needed") {
    val rows = lines("exact-cuts.tsv")
    assert(rows.nonEmpty)
    rows.foreach {
      case Vec(source, kernel, at, stall, consumer, producer, use, distance, _) =>
        val module                      = Cubin.decode(resource(source)).value
        val program                     = new LoadedProgram(module, Map.empty, Workgroups.Sequential, _ => None)
        val (graph, uses, instructions) = program.staticCode(kernel.unsafe[NonEmpty[String]]).value
        val path                        = ControlWords.paths(graph, uses, instructions)
          .find(path => path.first <= at.toInt && at.toInt < path.first + path.steps.size)
          .getOrElse(fail(s"$kernel has no #$at"))
        val schedule = ControlWords.regenerate(table, path).value
        val index    = at.toInt - path.first
        assertEquals(table.distance(producer, use), distance.toInt, s"$kernel $producer to $use")
        assertEquals(schedule(index).stall.raw, stall.toInt + 1, s"$kernel #$at")
        val cut   = schedule.updated(index, schedule(index).copy(stall = stall.toInt.unsafe[Int in 0 ~ 15]))
        val found = ControlWords.violations(table, path, cut, harvested = true)
        assert(
          found.exists((flagged, reason) => flagged == consumer.toInt && reason.contains("before its registers")),
          s"$kernel #$at stall $stall: ${found.mkString(", ")}"
        )
      case other => fail(s"malformed cut $other")
    }
  }
}
