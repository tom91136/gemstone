package net.kurobako.gemstone.isa.check

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.sass.ControlWords
import net.kurobako.gemstone.isa.sass.Cubin
import net.kurobako.gemstone.isa.sass.LoadedProgram
import net.kurobako.gemstone.isa.sass.Scheduling
import net.kurobako.gemstone.vm.Workgroups
import scala.collection.immutable.TreeMap
import scala.collection.mutable
import scala.jdk.CollectionConverters.*
import scala.util.Try
import upickle.default.*

/** Harvests the SASS control-word table of one target from code ptxas compiled, and measures regeneration against the
  * programs held out of the harvest. An input is a cubin, an executable whose fatbin holds uncompressed cubins, or a
  * directory of either; compressed fatbin entries and kernels the VM cannot lower are left out and counted. One program
  * in `ControlWords.HeldOut` is held out, chosen by its file name.
  */
object ControlWordHarvestRunner {

  private val usage =
    "usage: ControlWordHarvestRunner <sm> <toolkit> <control-words.json> <cubin, executable or directory>..."

  private val Fatbin     = Array[Byte](0x50, 0xed.toByte, 0x55, 0xba.toByte, 0x01, 0x00, 0x10, 0x00)
  private val Elf        = Array[Byte](0x7f, 'E', 'L', 'F')
  private val CudaObject = 190
  private val Reason     = 48
  private val Reasons    = 6

  private case class Scheduled(source: String, kernel: String, path: ControlWords.Path, schedule: Vec[Scheduling])

  private def cubin(bytes: Array[Byte], at: Int): Boolean =
    bytes.length >= at + 20 && bytes.slice(at, at + 4).sameElements(Elf) &&
      ((bytes(at + 18) & 0xff) | ((bytes(at + 19) & 0xff) << 8)) == CudaObject

  /** The cubins of a file, with `None` for each fatbin entry of the target that is compressed. */
  private def images(bytes: Array[Byte], sm: Int): Vec[Option[Array[Byte]]] =
    if (cubin(bytes, 0)) Vec(Some(bytes))
    else {
      val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
      Vec.from(Iterator.iterate(bytes.indexOfSlice(Fatbin))(at => bytes.indexOfSlice(Fatbin, at + 16))
        .takeWhile(_ >= 0).flatMap { start =>
          val end = start + 16 + buffer.getLong(start + 8)
          Iterator.iterate(start + 16L)(entry =>
            entry + buffer.getInt(entry.toInt + 4) + buffer.getLong(entry.toInt + 8)
          )
            .takeWhile(entry => entry + 16 <= end && entry + 16 <= bytes.length)
            .map(_.toInt)
            .filter(entry => buffer.getShort(entry) == 2 && buffer.getInt(entry + 28) == sm)
            .map { entry =>
              val payload = entry + buffer.getInt(entry + 4)
              Option.when(cubin(bytes, payload))(bytes.slice(payload, payload + buffer.getLong(entry + 8).toInt))
            }
        })
    }

  private def files(input: Path): Vec[Path] =
    if (Files.isDirectory(input)) {
      val listing = Files.list(input)
      try Vec.from(listing.iterator.asScala.filter(Files.isRegularFile(_)).toVector.sortBy(_.getFileName.toString))
      finally listing.close()
    } else Vec(input)

  private def scheduled(
      source: String,
      image: Array[Byte],
      sm: Int,
      intern: String => String,
      skipped: mutable.Map[String, Int]
  ): Vec[Scheduled] = {
    val decoded = Cubin.decode(image.toVec).toOption
    if (decoded.isEmpty) skipped("cubins not decoded") += 1
    decoded.filter(_.sm.raw == sm).fold(Vec.empty[Scheduled]) { module =>
      val program = new LoadedProgram(module, Map.empty, Workgroups.Sequential, _ => None)
      module.kernels.flatMap { kernel =>
        val outcome = Try(program.staticCode(kernel.name)).toEither.left.map(_.getClass.getSimpleName)
          .flatMap(_.left.map(_.message.replace(kernel.name.raw, "<kernel>").takeWhile(_ != '\'').take(Reason)))
        val lowered = outcome.toOption
        skipped(if (lowered.isEmpty) "kernels not lowered" else "kernels") += 1
        outcome.left.foreach(reason => skipped(s"not lowered: $reason") += 1)
        lowered.fold(Vec.empty[Scheduled]) {
          (graph, uses, instructions) =>
            val keptPaths = Vec.newBuilder[Scheduled]
            ControlWords.paths(graph, uses, instructions).foreach { path =>
              val schedule = instructions.slice(path.first, path.first + path.steps.size).flatMap(_.scheduling)
              val steps    = path.steps.map(step =>
                step.copy(
                  form = intern(step.form),
                  reads = step.reads.map(intern),
                  kinds = step.kinds.map(intern),
                  writes = step.writes.map(intern)
                )
              )
              if (schedule.size != steps.size) skipped("paths with no schedule") += 1
              else keptPaths += Scheduled(source, kernel.name.raw, ControlWords.Path(path.first, steps), schedule)
            }
            keptPaths.result()
        }
      }
    }
  }

  private class Measure {
    val fields: mutable.Map[String, Int]      = mutable.Map.empty.withDefaultValue(0)
    val classes: mutable.Map[String, Int]     = mutable.Map.empty.withDefaultValue(0)
    val examples: mutable.Map[String, String] = mutable.Map.empty

    def add(table: ControlWords.Table, unit: Scheduled): Unit = {
      val steps = unit.path.steps
      fields("paths") += 1
      fields("instructions") += steps.size
      ControlWords.regenerate(table, unit.path) match {
        case Left(_)         => fields("paths not regenerated") += 1
        case Right(schedule) =>
          val pairs                                                                = schedule.zip(unit.schedule)
          def count(name: String)(same: (Scheduling, Scheduling) => Boolean): Unit =
            fields(name) += pairs.count(same(_, _))
          count("stall")(_.stall == _.stall)
          count("yield")(_.yieldHint == _.yieldHint)
          count("write barrier")(_.writeBarrier == _.writeBarrier)
          count("write barrier set")(_.writeBarrier.isEmpty == _.writeBarrier.isEmpty)
          count("read barrier")(_.readBarrier == _.readBarrier)
          count("read barrier set")(_.readBarrier.isEmpty == _.readBarrier.isEmpty)
          count("wait mask")(_.waitMask == _.waitMask)
          count("reuse")(_.reuse == _.reuse)
          count("stall not below ptxas")(_.stall.raw >= _.stall.raw)
          if (pairs.forall(_ == _)) fields("paths exact") += 1
          if (pairs.forall((ours, theirs) => ours.copy(reuse = theirs.reuse) == theirs))
            fields("paths exact but reuse") += 1
          if (pairs.forall(_.stall.raw >= _.stall.raw)) fields("paths with no stall below ptxas") += 1
          ControlWords.violations(table, unit.path, schedule).headOption.foreach { (index, reason) =>
            fields("paths violating the table") += 1
            if (fields("paths violating the table") <= 3)
              Console.out.println(s"VIOLATION ${unit.source} #$index $reason")
          }
          if (ControlWords.shorter(unit.path, schedule, unit.schedule).isEmpty)
            fields("paths with no shorter dependency") += 1
          pairs.indices.filter(index => pairs(index)._1.stall != pairs(index)._2.stall).foreach { index =>
            val next = if (index + 1 < steps.size) steps(index + 1).form else "leave"
            val sign = if (pairs(index)._1.stall.raw < pairs(index)._2.stall.raw) "below" else "above"
            val name = s"${steps(index).form} -> $next $sign"
            classes(name) += 1
            if (!examples.contains(name))
              examples(name) = s"${unit.source} ${unit.kernel} #${unit.path.first + index} ours " +
                s"${pairs(index)._1.stall} ptxas ${pairs(index)._2.stall}"
          }
      }
      if (ControlWords.violations(table, unit.path, unit.schedule).isEmpty) fields("ptxas paths within the table") += 1
    }

    def report(name: String, top: Int): Unit = {
      Console.out.println(s"== $name")
      fields.toSeq.sortBy(_._1).foreach((field, count) => Console.out.println(s"$field: $count"))
      Console.out.println(s"stall mismatch classes: ${classes.size}")
      Vector("below", "above").foreach(sign =>
        classes.toSeq.filter(_._1.endsWith(sign)).sortBy(-_._2).take(top).foreach((mismatch, count) =>
          Console.out.println(s"$count $mismatch (${examples(mismatch)})")
        )
      )
    }
  }

  private def sorted(table: ControlWords.Table): ControlWords.Table = table.copy(
    latency = TreeMap.from(table.latency.map((producer, row) => producer -> TreeMap.from(row))),
    settle = TreeMap.from(table.settle),
    late = scala.collection.immutable.TreeSet.from(table.late),
    spacing = TreeMap.from(table.spacing.map((form, row) => form -> TreeMap.from(row))),
    idle = TreeMap.from(table.idle.map((form, row) => form -> TreeMap.from(row)))
  )

  def main(arguments: Array[String]): Unit = {
    if (arguments.length < 4) throw IllegalArgumentException(usage)
    val sm          = arguments(0).toInt
    val toolkit     = arguments(1)
    val output      = Path.of(arguments(2))
    val interned    = mutable.Map.empty[String, String]
    val skipped     = mutable.Map.empty[String, Int].withDefaultValue(0)
    val unitBuilder = Vector.newBuilder[Scheduled]
    arguments.drop(3).toVector.flatMap(input => files(Path.of(input))).foreach { file =>
      val name = file.getFileName.toString
      images(Files.readAllBytes(file), sm).zipWithIndex.foreach {
        case (Some(image), index) =>
          unitBuilder ++= scheduled(s"$name#$index", image, sm, text => interned.getOrElseUpdate(text, text), skipped)
        case (None, _) =>
          skipped("compressed fatbin entries") += 1
      }
    }
    val units        = unitBuilder.result()
    val (held, kept) = units.partition(unit => ControlWords.heldOut(unit.source.takeWhile(_ != '#')))
    val harvest      = ControlWords.Harvest()
    kept.foreach(unit => harvest.classes(unit.path, unit.schedule))
    kept.foreach(unit => harvest.distances(unit.path, unit.schedule))
    val registers = harvest.registers
    kept.foreach(unit => harvest.idles(registers, unit.path, unit.schedule))
    val table  = sorted(harvest.table(s"sm_$sm", toolkit, kept.map(_.source).distinct.size))
    val others =
      if (Files.exists(output))
        read[Vector[ControlWords.Table]](Files.readString(output)).filter(_.target != table.target)
      else Vector.empty[ControlWords.Table]
    val _ = Files.writeString(
      output,
      write((others :+ table).sortBy(_.target), indent = 1) + "\n",
      StandardCharsets.UTF_8
    )
    val spread = harvest.idleCounts.values.count(_.count(_ > 0) > 1)
    Console.out.println(
      s"sources ${units.map(_.source).distinct.size} kept ${table.sources} paths ${units.size} " +
        s"instructions kept ${table.instructions} latency rows ${table.latency.size} " +
        s"pairs ${table.latency.values.map(_.size).sum} settle ${table.settle.size} late ${table.late.size} spacing ${table.spacing.values.map(_.size).sum} " +
        s"idle pairs ${harvest.idleCounts.size} with more than one stall $spread"
    )
    Console.out.println(
      s"coverage: kernels ${skipped("kernels")} not lowered ${skipped("kernels not lowered")} " +
        s"compressed fatbin entries ${skipped("compressed fatbin entries")} " +
        s"cubins not decoded ${skipped("cubins not decoded")} paths with no schedule ${skipped("paths with no schedule")}"
    )
    skipped.toSeq.filter(_._1.startsWith("not lowered: ")).sortBy(-_._2).take(Reasons)
      .foreach((reason, count) => Console.out.println(s"$count $reason"))
    Vector("held out" -> held, "harvested" -> kept).foreach { (name, group) =>
      val measure = Measure()
      group.foreach(measure.add(table, _))
      measure.report(name, 40)
    }
  }
}
