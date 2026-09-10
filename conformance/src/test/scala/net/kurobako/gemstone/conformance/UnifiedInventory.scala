package net.kurobako.gemstone.conformance

import scala.concurrent.duration.*

/** CTest owns the matrix. Preserve its argv, environment, and deadline when relocating the executable. */
private[conformance] object UnifiedInventory {
  final case class Entry(
      name: String,
      command: Vector[String],
      environment: Map[String, String],
      timeout: FiniteDuration
  )

  def read(json: String): Vector[Entry] = {
    val entries = ujson.read(json)("tests").arr.toVector.flatMap { test =>
      val properties = test.obj.get("properties").toVector.flatMap(_.arr).map { property =>
        property("name").str -> property("value")
      }.toMap
      if properties.get("DISABLED").exists(_.bool) then None
      else {
        val command = test.obj.get("command").getOrElse(throw IllegalArgumentException(
          s"CTest did not resolve the executable for ${test("name").str}; rebuild the artifact"
        )).arr.toVector.map(_.str)
        require(command.nonEmpty, "CTest command is empty")
        val environment = properties.get("ENVIRONMENT").toVector.flatMap(_.arr).map { value =>
          value.str.split("=", 2) match {
            case Array(name, setting) if name.nonEmpty => name -> setting
            case _ => throw IllegalArgumentException(s"invalid CTest environment: ${value.str}")
          }
        }.toMap
        val seconds = properties.get("TIMEOUT").fold(120.0)(_.num)
        require(java.lang.Double.isFinite(seconds) && seconds > 0, s"invalid CTest timeout: $seconds")
        Some(Entry(test("name").str, command, environment, seconds.seconds))
      }
    }
    require(entries.nonEmpty, "CTest inventory has no enabled cases")
    require(entries.map(_.name).distinct.size == entries.size, "CTest inventory contains duplicate names")
    entries
  }

  /** Compare the portable workload matrix before backend-specific capability skips are evaluated. */
  def assertAligned(inventories: Vector[(String, Vector[Entry])]): Unit = {
    val comparable = inventories.filter { case (backend, _) => Set("host", "cuda", "hip", "sycl")(backend) }
      .map { case (backend, entries) =>
        val common = entries.flatMap { entry =>
          val name     = entry.name.stripPrefix(backend + "/")
          val portable =
            Vector("algorithms/", "blas/", "edge-cases/algorithms/", "edge-cases/blas/", "async/", "stress/")
              .exists(name.startsWith)
          // These are explicit vendor API extensions, not counterparts of the portable catalog.
          val specific = name.startsWith("algorithms/roc-thrust-semantic/") || name.startsWith("async/hip/")
          val parameters = entry.command.tail.filterNot(_.startsWith("--filter="))
          Option.when(portable && !specific)(name -> (parameters, entry.timeout))
        }.toMap
        require(common.nonEmpty, s"$backend has no portable unified cases")
        backend -> common
      }
    comparable.headOption.foreach { case (reference, expected) =>
      comparable.tail.foreach { case (backend, actual) =>
        val missing = expected.keySet -- actual.keySet
        val extra   = actual.keySet -- expected.keySet
        require(
          missing.isEmpty && extra.isEmpty,
          s"unified matrix differs: $backend versus $reference; missing=${missing.toVector.sorted.take(8)}; extra=${extra.toVector.sorted.take(8)}"
        )
        val changed = expected.keysIterator.filter(name => expected(name) != actual(name)).toVector.sorted
        require(changed.isEmpty, s"unified parameters/deadlines differ: $backend versus $reference: ${changed.take(8)}")
      }
    }
  }

}
