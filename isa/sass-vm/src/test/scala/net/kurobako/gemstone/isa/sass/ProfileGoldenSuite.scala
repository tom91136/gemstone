package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.profile.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchObserver.*

/** Hardware counters of the probe kernels on an RTX 4060 Ti (sm_89, driver 615.78.08, CUDA 13.3, ncu 2026.2.1), one
  * block of 32 threads per launch: per kernel and argument, global load and store requests and sectors, shared load and
  * store wavefronts, shared load and store bank conflicts, then thread and warp instructions executed.
  */
class ProfileGoldenSuite extends Suite {
  private val columns =
    Vec("ldReq", "ldSec", "stReq", "stSec", "shLdWf", "shStWf", "shLdCf", "shStCf", "thrInst", "inst")

  private val table = """ld_stride        1  1 4  1 4  0  0  0  0  320  10
                        |ld_stride        2  1 8  1 4  0  0  0  0  320  10
                        |ld_stride        4  1 16 1 4  0  0  0  0  320  10
                        |ld_stride        8  1 32 1 4  0  0  0  0  320  10
                        |ld_stride        32 1 32 1 4  0  0  0  0  320  10
                        |ld_offset        1  1 5  1 4  0  0  0  0  320  10
                        |ld_offset        8  1 4  1 4  0  0  0  0  320  10
                        |ld_double        1  1 8  1 8  0  0  0  0  320  10
                        |ld_double        2  1 16 1 8  0  0  0  0  320  10
                        |ld_float4        -  1 16 1 16 0  0  0  0  288  9
                        |shared_stride    1  0 0  1 4  1  1  0  0  384  12
                        |shared_stride    2  0 0  1 4  2  2  1  1  384  12
                        |shared_stride    4  0 0  1 4  4  4  3  3  384  12
                        |shared_stride    8  0 0  1 4  8  8  7  7  384  12
                        |shared_stride    16 0 0  1 4  16 16 15 15 384  12
                        |shared_stride    32 0 0  1 4  32 32 31 31 384  12
                        |shared_stride    33 0 0  1 4  1  1  0  0  384  12
                        |shared_broadcast -  1 1  1 4  1  1  0  0  420  18
                        |shared_double    1  0 0  1 8  2  2  0  0  384  12
                        |shared_double    2  0 0  1 8  4  4  2  2  384  12
                        |divergent        1  2 5  1 4  0  0  0  0  1088 55
                        |divergent        16 2 5  1 4  0  0  0  0  1088 55""".stripMargin

  table.linesIterator.map(_.trim.split(' ').filter(_.nonEmpty)).foreach { words =>
    val kernel = words(0)
    val arg    = words(1).toIntOption
    test(s"hardware: ${words.take(if (arg.isEmpty) 1 else 2).mkString(" ")}") {
      assertEquals(counts(observe(kernel, arg)), named(Vec.from(words.drop(2).map(_.toLong))))
    }
  }

  private def pointers(kernel: String): Int = kernel match {
    case "shared_stride" | "shared_double" => 1
    case _                                 => 2
  }

  private def shared(kernel: String): Int >= 0 = kernel match {
    case "shared_stride"    => 4224
    case "shared_broadcast" => 128
    case "shared_double"    => 8192
    case _                  => 0
  }

  private def listing(kernel: String): String =
    Fixture.listing(kernel.split('_').zipWithIndex.map((part, index) =>
      if (index == 0) part else part.capitalize
    ).mkString + "Sm89")

  private def observe(name: String, arg: Option[Int]): Profile = {
    val kernel  = Harness.load(89, listing(name), name, shared(name))
    val memory  = Memory()
    val buffers = Vec.tabulate(pointers(name))(_ => memory.allocate(Vec.fill(65536)(0.toByte)))
    val values  = Vec.from(
      buffers.view.map(buffer => KernelTask.Argument.Memory(buffer, 0L)) ++
        arg.view.map(value =>
          KernelTask.Argument.Scalar(LittleEndian.toVec(value.toLong, 4).refine[NonEmpty[Vec[Byte]]].value)
        )
    )
    val task = KernelTask.validated(
      kernel.name,
      LaunchGeometry.Dimensions(32, 1, 1),
      LaunchGeometry.Dimensions(1, 1, 1),
      values
    ).value
    val sizes    = Seq.fill(pointers(name))(8) ++ arg.fold(Seq.empty[Int])(_ => Seq(4))
    val compiled = kernel.compile(sizes*).value
    val recorder = ProfileRecorder()
    memory.run(compiled, task, TaskControl(observer = Some(recorder))).left.foreach(fault => fail(fault.message))
    Profiler.run(recorder.stream, Architecture.sm89)
  }

  private def named(values: Vec[Long]): Vec[String] = columns.zip(values).map((name, value) => s"$name $value")

  private def counts(profile: Profile): Vec[String] = {
    def memory(space: Space, kind: AccessKind): Profile.Memory =
      profile.total.memory.getOrElse((space, kind), Profile.Memory.zero)
    val loads        = memory(Space.Global, AccessKind.Read)
    val stores       = memory(Space.Global, AccessKind.Write)
    val sharedLoads  = memory(Space.Shared, AccessKind.Read)
    val sharedStores = memory(Space.Shared, AccessKind.Write)
    named(Vec(
      loads.requests,
      loads.sectors,
      stores.requests,
      stores.sectors,
      sharedLoads.wavefronts,
      sharedStores.wavefronts,
      sharedLoads.conflicts,
      sharedStores.conflicts,
      profile.total.activeLanes,
      profile.total.issues
    ))
  }
}
