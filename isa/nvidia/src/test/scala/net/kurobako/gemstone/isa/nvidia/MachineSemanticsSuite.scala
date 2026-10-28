package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.semantics.render.*

class MachineSemanticsSuite extends munit.FunSuite {
  import MachineDefinitions.*

  private def shown(definition: Either[String, Definition]): String =
    definition.fold(failure => fail(failure), ScalaBackend.show(_))

  test("an unguarded branch renders as a jump to its target") {
    assertEquals(
      shown(branch()),
      """def bra()(using machine: Machine): Unit = {
        |  machine.jump(7)
        |  ()
        |}""".stripMargin
    )
  }

  test("a guarded branch renders its jump under the guard") {
    assertEquals(
      shown(branch(guarded = true)),
      """def bra(p: Bool)(using machine: Machine): Unit = {
        |  if (p) machine.jump(7) else ()
        |  ()
        |}""".stripMargin
    )
  }

  test("a bulk copy renders its validation, copy and completion") {
    assertEquals(
      shown(copy()),
      """def `cp.async.bulk`(dst: UInt[64], src: UInt[64], size: UInt[64], bar: UInt[64])(using machine: Machine, cluster: Cluster): Unit = {
        |  machine.require(dst.to[UInt[4]] == UInt[4](0) && src.to[UInt[4]] == UInt[4](0) && size.to[UInt[4]] == UInt[4](0) && size.to[SInt[64]] >= 0 && size.to[SInt[64]] <= 0xfffff, "invalid bulk copy")
        |  machine.require(cluster.shared(dst, size), "bulk copy destination")
        |  machine.require(cluster.shared(bar, UInt[64](8)), "bulk copy mbarrier")
        |  val v0 = machine.loadBytes(Space.Generic, src, size)
        |  cluster.store(cluster.owner(dst), cluster.offset(dst), v0)
        |  cluster.completeTx(cluster.owner(dst), cluster.offset(bar), size)
        |  ()
        |}""".stripMargin
    )
  }

  test("a multicast bulk copy from shared memory with ignored bounds renders per destination CTA") {
    assertEquals(
      shown(copy(sharedSource = true, multicast = Some(mask), ignored = Some((left, right)))),
      """def `cp.async.bulk`(dst: UInt[64], src: UInt[64], size: UInt[64], left: UInt[64], right: UInt[64], bar: UInt[64], mask: UInt[64])(using machine: Machine, cluster: Cluster): Unit = {
        |  machine.require(dst.to[UInt[4]] == UInt[4](0) && src.to[UInt[4]] == UInt[4](0) && size.to[UInt[4]] == UInt[4](0) && size.to[SInt[64]] >= 0 && size.to[SInt[64]] <= 0xfffff && left.to[SInt[64]] >= 0 && left.to[SInt[64]] <= 15 && right.to[SInt[64]] >= 0 && right.to[SInt[64]] <= 15 && (left + right).to[SInt[64]] <= size.to[SInt[64]], "invalid bulk copy")
        |  machine.require(cluster.shared(dst, size), "bulk copy destination")
        |  machine.require(cluster.shared(bar, UInt[64](8)), "bulk copy mbarrier")
        |  val v0 = machine.loadBytes(Space.Shared, src + left, size - left - right)
        |  for (cta <- cluster.destinations(mask)) {
        |    cluster.store(cta, cluster.offset(dst) + left, v0)
        |    cluster.completeTx(cta, cluster.offset(bar), size)
        |  }
        |  ()
        |}""".stripMargin
    )
  }

  test("a bulk copy from shared memory into cluster memory requires a cluster launch") {
    assertEquals(
      shown(copy(sharedSource = true, clusterDestination = true)),
      """def `cp.async.bulk`(dst: UInt[64], src: UInt[64], size: UInt[64], bar: UInt[64])(using machine: Machine, launch: Launch, cluster: Cluster): Unit = {
        |  machine.require(launch.explicitCluster() != UInt[32](0), "cluster launch")
        |  machine.require(dst.to[UInt[4]] == UInt[4](0) && src.to[UInt[4]] == UInt[4](0) && size.to[UInt[4]] == UInt[4](0) && size.to[SInt[64]] >= 0 && size.to[SInt[64]] <= 0xfffff, "invalid bulk copy")
        |  machine.require(cluster.shared(dst, size), "bulk copy destination")
        |  machine.require(cluster.shared(bar, UInt[64](8)), "bulk copy mbarrier")
        |  val v0 = machine.loadBytes(Space.Shared, src, size)
        |  cluster.store(cluster.owner(dst), cluster.offset(dst), v0)
        |  cluster.completeTx(cluster.owner(dst), cluster.offset(bar), size)
        |  ()
        |}""".stripMargin
    )
  }

  test("a guarded bulk copy renders its work under the guard") {
    assertEquals(
      shown(copy(guarded = true)),
      """def `cp.async.bulk`(p: Bool, dst: UInt[64], src: UInt[64], size: UInt[64], bar: UInt[64])(using machine: Machine, cluster: Cluster): Unit = {
        |  if (p) {
        |    machine.require(dst.to[UInt[4]] == UInt[4](0) && src.to[UInt[4]] == UInt[4](0) && size.to[UInt[4]] == UInt[4](0) && size.to[SInt[64]] >= 0 && size.to[SInt[64]] <= 0xfffff, "invalid bulk copy")
        |    machine.require(cluster.shared(dst, size), "bulk copy destination")
        |    machine.require(cluster.shared(bar, UInt[64](8)), "bulk copy mbarrier")
        |    val v0 = machine.loadBytes(Space.Generic, src, size)
        |    cluster.store(cluster.owner(dst), cluster.offset(dst), v0)
        |    cluster.completeTx(cluster.owner(dst), cluster.offset(bar), size)
        |  }
        |  ()
        |}""".stripMargin
    )
  }

  test("an arrival, a register write and a wait render as statements and a result") {
    assertEquals(
      shown(arrival()),
      """def arrive(bar: UInt[64], size: UInt[64])(using machine: Machine, cluster: Cluster, barriers: Barriers): (token: UInt[64]) = {
        |  machine.require(cluster.shared(bar, UInt[64](8)), "mbarrier")
        |  val v0 = barriers.arrive(bar, size, size, false, true)
        |  machine.park(uniform(bar), uniform(size), true)
        |  (token = v0)
        |}""".stripMargin
    )
  }

  test("a call and a return render under the guard") {
    assertEquals(
      shown(calls()),
      """def call(p: Bool, src: UInt[64])(using frames: Frames): Unit = {
        |  if (p) frames.call(src, Seq(1), Seq(2, 3)) else ()
        |  if (p) frames.returnControl() else ()
        |  ()
        |}""".stripMargin
    )
  }

  test("a host fence renders as a memory fence") {
    assertEquals(
      shown(fence()),
      """def membar()(using memory: Memory): Unit = {
        |  memory.fence()
        |  ()
        |}""".stripMargin
    )
  }

  test("a guarded host fence renders its fence under the guard") {
    assertEquals(
      shown(fence(guarded = true)),
      """def membar(p: Bool)(using memory: Memory): Unit = {
        |  if (p) memory.fence() else ()
        |  ()
        |}""".stripMargin
    )
  }

  test("a matrix move reads every lane of the warp") {
    assertEquals(
      shown(moveMatrix()),
      """def movmatrix(src: UInt[64])(using machine: Machine, lanes: Lanes): (dst: UInt[64]) = {
        |  machine.require(lanes.converged(), "warp convergence")
        |  val v0 = lanes.gather(src)
        |  val v1 = (lanes.peer(v0, (lanes.index().to[UInt[2]].to[UInt[64]] << 3).to[UInt[64]] | (lanes.index() >> 3)) >> ((lanes.index() & 4) << 2)).to[UInt[16]].to[UInt[64]] | ((lanes.peer(v0, (lanes.index().to[UInt[2]].to[UInt[64]] << 3).to[UInt[64]] | (lanes.index() >> 3) | 4) >> ((lanes.index() & 4) << 2)).to[UInt[16]].to[UInt[64]] << 16)
        |  (dst = v1)
        |}""".stripMargin
    )
  }

  test("a matrix load places the rows of one matrix") {
    assertEquals(
      shown(loadMatrix(1)),
      """def ldmatrix(row: UInt[64])(using machine: Machine, lanes: Lanes): (d0: UInt[64]) = {
        |  machine.require(lanes.converged(), "warp convergence")
        |  machine.require(lanes.index() >= 8 || (row.to[UInt[4]] == UInt[4](0) && (row - 0x10000000 <= 0x2ffffff0 || row - 0x4000000000000000L <= 0xfffffff0L)), "invalid matrix row address")
        |  val v0 = lanes.gatherLoad[UInt[64]](8, Space.Shared, row, 0)
        |  val v1 = lanes.gatherLoad[UInt[64]](8, Space.Shared, row, 8)
        |  val v2 = MatrixLayout.element(MatrixLayout.column(UInt[64](0), lanes.index(), UInt[64](0)), lanes.peer(v0, MatrixLayout.row(UInt[64](0), lanes.index(), UInt[64](0))), lanes.peer(v1, MatrixLayout.row(UInt[64](0), lanes.index(), UInt[64](0)))) | (MatrixLayout.element(MatrixLayout.column(UInt[64](0), lanes.index(), UInt[64](1)), lanes.peer(v0, MatrixLayout.row(UInt[64](0), lanes.index(), UInt[64](1))), lanes.peer(v1, MatrixLayout.row(UInt[64](0), lanes.index(), UInt[64](1)))) << 16)
        |  (d0 = v2)
        |}""".stripMargin
    )
  }

  test("a transposed matrix load of two matrices places both") {
    assertEquals(
      shown(loadMatrix(2, transpose = true)),
      """def ldmatrix(row: UInt[64])(using machine: Machine, lanes: Lanes): (d0: UInt[64], d1: UInt[64]) = {
        |  machine.require(lanes.converged(), "warp convergence")
        |  machine.require(lanes.index() >= 16 || (row.to[UInt[4]] == UInt[4](0) && (row - 0x10000000 <= 0x2ffffff0 || row - 0x4000000000000000L <= 0xfffffff0L)), "invalid matrix row address")
        |  val v0 = lanes.gatherLoad[UInt[64]](16, Space.Shared, row, 0)
        |  val v1 = lanes.gatherLoad[UInt[64]](16, Space.Shared, row, 8)
        |  val v2 = MatrixLayout.element(MatrixLayout.column(UInt[64](1), lanes.index(), UInt[64](0)), lanes.peer(v0, MatrixLayout.row(UInt[64](1), lanes.index(), UInt[64](0))), lanes.peer(v1, MatrixLayout.row(UInt[64](1), lanes.index(), UInt[64](0)))) | (MatrixLayout.element(MatrixLayout.column(UInt[64](1), lanes.index(), UInt[64](1)), lanes.peer(v0, MatrixLayout.row(UInt[64](1), lanes.index(), UInt[64](1))), lanes.peer(v1, MatrixLayout.row(UInt[64](1), lanes.index(), UInt[64](1)))) << 16)
        |  val v3 = MatrixLayout.element(MatrixLayout.column(UInt[64](1), lanes.index(), UInt[64](4)), lanes.peer(v0, MatrixLayout.row(UInt[64](1), lanes.index(), UInt[64](4))), lanes.peer(v1, MatrixLayout.row(UInt[64](1), lanes.index(), UInt[64](4)))) | (MatrixLayout.element(MatrixLayout.column(UInt[64](1), lanes.index(), UInt[64](5)), lanes.peer(v0, MatrixLayout.row(UInt[64](1), lanes.index(), UInt[64](5))), lanes.peer(v1, MatrixLayout.row(UInt[64](1), lanes.index(), UInt[64](5)))) << 16)
        |  (
        |    d0 = v2,
        |    d1 = v3
        |  )
        |}""".stripMargin
    )
  }

  test("a bulk store renders its masked copy") {
    assertEquals(
      shown(bulkStore()),
      """def `cp.async.bulk`(dst: UInt[64], src: UInt[64], size: UInt[64], mask: UInt[64])(using machine: Machine, memory: Memory): Unit = {
        |  machine.require(dst.to[UInt[4]] == UInt[4](0) && src.to[UInt[4]] == UInt[4](0) && size.to[UInt[4]] == UInt[4](0), "invalid bulk store")
        |  machine.require(size.to[SInt[64]] >= 0 && size.to[SInt[64]] <= 0xfffff, "invalid bulk store size")
        |  memory.storeGlobalMasked(dst, src, size, mask)
        |  ()
        |}""".stripMargin
    )
  }

  test("a tensor map copy renders its cluster checks and copy") {
    assertEquals(
      shown(tensorMapCopy()),
      """def `tensormap.cp_fenceproxy`(dst: UInt[64], src: UInt[64])(using machine: Machine, cluster: Cluster): Unit = {
        |  machine.require(cluster.inCluster(src, UInt[64](128)), "tensor map source")
        |  val v0 = machine.loadBytes(Space.Generic, src, UInt[64](128))
        |  machine.require(cluster.inCluster(dst, UInt[64](128)), "tensor map destination")
        |  machine.storeBytes(Space.Generic, dst, v0)
        |  ()
        |}""".stripMargin
    )
  }

  test("a tensor map replacement of the element type renders its primitive check, read and write") {
    assertEquals(
      shown(tensorMapReplace(TensorMap.Field.ElementType)),
      """def `tensormap.replace`(dst: UInt[64], src: UInt[64])(using machine: Machine, memory: Memory): Unit = {
        |  machine.require(dst.to[UInt[6]] == UInt[6](0), "tensor map alignment")
        |  val v0 = machine.loadBytes(Space.Generic, dst, UInt[64](128))
        |  machine.require(TensorLayout.elementType(src) != UInt[64](0), "tensor map field")
        |  val v1 = memory.replaceTensorMap(v0, TensorMap.Field.ElementType, 0, src)
        |  machine.storeBytes(Space.Generic, dst, v1)
        |  ()
        |}""".stripMargin
    )
  }

  test("a tensor map replacement of the interleave renders its limit check") {
    assertEquals(
      shown(tensorMapReplace(TensorMap.Field.Interleave)),
      """def `tensormap.replace`(dst: UInt[64], src: UInt[64])(using machine: Machine, memory: Memory): Unit = {
        |  machine.require(dst.to[UInt[6]] == UInt[6](0), "tensor map alignment")
        |  val v0 = machine.loadBytes(Space.Generic, dst, UInt[64](128))
        |  machine.require(src.to[SInt[64]] <= 2, "tensor map field")
        |  val v1 = memory.replaceTensorMap(v0, TensorMap.Field.Interleave, 0, src)
        |  machine.storeBytes(Space.Generic, dst, v1)
        |  ()
        |}""".stripMargin
    )
  }

  test("a tensor map replacement of a box dimension renders its read and write without a value check") {
    assertEquals(
      shown(tensorMapReplace(TensorMap.Field.BoxDimension, 2)),
      """def `tensormap.replace`(dst: UInt[64], src: UInt[64])(using machine: Machine, memory: Memory): Unit = {
        |  machine.require(dst.to[UInt[6]] == UInt[6](0), "tensor map alignment")
        |  val v0 = machine.loadBytes(Space.Generic, dst, UInt[64](128))
        |  val v1 = memory.replaceTensorMap(v0, TensorMap.Field.BoxDimension, 2, src)
        |  machine.storeBytes(Space.Generic, dst, v1)
        |  ()
        |}""".stripMargin
    )
  }

  test("a tensor load renders its checks, runs, placement and completion") {
    assertEquals(
      shown(tensorCopy()),
      """def `cp.async.bulk.tensor`(left: UInt[64], right: UInt[64], src: UInt[64], dst: UInt[64], bar: UInt[64])(using machine: Machine, tensors: Tensors, cluster: Cluster): Unit = {
        |  val v0 = machine.loadBytes(Space.Generic, src, UInt[64](128))
        |  val v1 = tensors.transfer(v0, false, false, TensorMap.Operation.Load, 2, left.to[SInt[32]].to[UInt[64]], right.to[SInt[32]].to[UInt[64]])
        |  machine.require((tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 >= 1 && (tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 <= 5 && tensors.word(v0, 64, 4).to[UInt[32]] <= 3 && TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) != UInt[64](0), "tensor map")
        |  machine.require((tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 == UInt[64](2) && tensors.word(v0, 64, 4).to[UInt[32]] == UInt[32](0), "tensor copy rank")
        |  machine.require((tensors.word(v0, 8, 4) >> 11).to[UInt[2]] == UInt[2](0), "tensor map interleave")
        |  machine.require(tensors.word(v0, 64, 4).to[UInt[32]] != UInt[32](0) || (left.to[SInt[32]].to[UInt[64]] * TensorLayout.elementBytes((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]])).to[UInt[4]] == UInt[4](0), "tensor tile coordinate")
        |  machine.require(if (TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) == UInt[64](1)) 1 != 0 else (left.to[SInt[32]].to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) && ((tensors.word(v0, 32, 4).to[UInt[32]].to[UInt[64]] + 1).to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) && tensors.word(v0, 52, 4).to[UInt[3]].to[UInt[64]] + 1 == UInt[64](1) && (if (tensors.word(v0, 64, 4).to[UInt[32]] == UInt[32](0)) (((tensors.word(v0, 52, 4) >> 24).to[UInt[8]].to[UInt[64]] + 1).to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) else (tensors.word(v0, 72, 4).to[UInt[32]].to[SInt[32]].to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0)), "tensor copy units")
        |  machine.require(cluster.shared(dst, tensors.fact(v1, TensorLayout.Fact.Bytes)), "tensor copy destination")
        |  machine.require(cluster.shared(bar, UInt[64](8)), "tensor copy mbarrier")
        |  val v2 = tensors.fact(v1, TensorLayout.Fact.Values)
        |  val v3 = tensors.word(v0, 0, 8)
        |  val v4 = tensors.fact(v1, TensorLayout.Fact.Global)
        |  val v5 = tensors.fact(v1, TensorLayout.Fact.Local)
        |  val v6 = left.to[SInt[32]].to[SInt[64]] / v2.to[SInt[64]]
        |  val v7 = tensors.word(v0, 56, 4).to[UInt[8]].to[UInt[64]] + 1
        |  val v8 = (tensors.word(v0, 52, 4) >> 3).to[UInt[3]].to[UInt[64]] + 1
        |  val v9 = (v7 + v8 - 1).to[SInt[64]] / v8.to[SInt[64]]
        |  val v10 = SInt[64](1) * v9
        |  val v11 = tensors.word(v0, 36, 4).to[UInt[32]].to[UInt[64]] + 1
        |  val v12 = (tensors.word(v0, 12, 4).to[UInt[32]].to[UInt[64]] << 4) | (tensors.word(v0, 28, 4).to[UInt[4]].to[UInt[64]] << 36)
        |  val v13 = (tensors.word(v0, 52, 4) >> 24).to[UInt[8]].to[UInt[64]] + 1
        |  val v14 = v13.to[SInt[64]] / v2.to[SInt[64]]
        |  val v15 = tensors.word(v0, 52, 4).to[UInt[3]].to[UInt[64]] + 1
        |  val v16 = (v14.to[UInt[64]] + v15 - 1).to[SInt[64]] / v15.to[SInt[64]]
        |  val v17 = tensors.word(v0, 32, 4).to[UInt[32]].to[UInt[64]] + 1
        |  val v18 = v17.to[SInt[64]] / v2.to[SInt[64]]
        |  for (t11 <- tensors.indices(v10.to[UInt[64]])) {
        |    val v19 = right.to[SInt[32]].to[UInt[64]] + ((t11.to[SInt[64]] / 1) % v9).to[UInt[64]] * v8
        |    val v20 = v3 + v19 * v12
        |    val v21 = v19.to[SInt[64]] >= 0 && v19.to[SInt[64]] < v11.to[SInt[64]]
        |    for (t21 <- tensors.indices(v16.to[UInt[64]])) {
        |      val v22 = v6.to[UInt[64]] + t21 * v15
        |      val v23 = v21 && (v22.to[SInt[64]] >= 0 && v22.to[SInt[64]] < v18)
        |      val v24 = (t11 * v16.to[UInt[64]] + t21) * v5
        |      val v25 = v20 + v22 * v4
        |      tensors.loadRun(v1, v24, v25, UInt[64](1), v23, tensors.fact(v1, TensorLayout.Fact.Fill))
        |    }
        |  }
        |  val v26 = tensors.fact(v1, TensorLayout.Fact.Swizzle)
        |  val v27 = tensors.fact(v1, TensorLayout.Fact.Atomicity)
        |  val v28 = tensors.fact(v1, TensorLayout.Fact.Bytes)
        |  val v29 = SInt[64](if (v26 == UInt[64](3) && v27 == UInt[64](2)) 8 else 16)
        |  for (t32 <- tensors.indices(((v28 + v29.to[UInt[64]] - 1).to[SInt[64]] / v29).to[UInt[64]])) {
        |    val v30 = t32 * v29.to[UInt[64]]
        |    cluster.placeChunk(cluster.owner(dst), cluster.offset(dst) + TensorMapping.swizzledOffset(v26, v27, cluster.offset(dst), v30), v1, v30, min(v29, (v28 - v30).to[SInt[64]]).to[UInt[64]])
        |  }
        |  cluster.completeTx(cluster.owner(dst), cluster.offset(bar), tensors.fact(v1, TensorLayout.Fact.Bytes))
        |  ()
        |}""".stripMargin
    )
  }

  test("a multicast tensor load into a CTA pair renders per destination CTA and its peer") {
    assertEquals(
      shown(tensorCopy(multicast = true, pair = true)),
      """def `cp.async.bulk.tensor`(left: UInt[64], right: UInt[64], src: UInt[64], dst: UInt[64], bar: UInt[64], mask: UInt[64])(using machine: Machine, tensors: Tensors, cluster: Cluster): Unit = {
        |  val v0 = machine.loadBytes(Space.Generic, src, UInt[64](128))
        |  val v1 = tensors.transfer(v0, false, false, TensorMap.Operation.Load, 2, left.to[SInt[32]].to[UInt[64]], right.to[SInt[32]].to[UInt[64]])
        |  machine.require((tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 >= 1 && (tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 <= 5 && tensors.word(v0, 64, 4).to[UInt[32]] <= 3 && TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) != UInt[64](0), "tensor map")
        |  machine.require((tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 == UInt[64](2) && tensors.word(v0, 64, 4).to[UInt[32]] == UInt[32](0), "tensor copy rank")
        |  machine.require((tensors.word(v0, 8, 4) >> 11).to[UInt[2]] == UInt[2](0), "tensor map interleave")
        |  machine.require(tensors.word(v0, 64, 4).to[UInt[32]] != UInt[32](0) || (left.to[SInt[32]].to[UInt[64]] * TensorLayout.elementBytes((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]])).to[UInt[4]] == UInt[4](0), "tensor tile coordinate")
        |  machine.require(if (TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) == UInt[64](1)) 1 != 0 else (left.to[SInt[32]].to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) && ((tensors.word(v0, 32, 4).to[UInt[32]].to[UInt[64]] + 1).to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) && tensors.word(v0, 52, 4).to[UInt[3]].to[UInt[64]] + 1 == UInt[64](1) && (if (tensors.word(v0, 64, 4).to[UInt[32]] == UInt[32](0)) (((tensors.word(v0, 52, 4) >> 24).to[UInt[8]].to[UInt[64]] + 1).to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) else (tensors.word(v0, 72, 4).to[UInt[32]].to[SInt[32]].to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0)), "tensor copy units")
        |  machine.require(cluster.shared(dst, tensors.fact(v1, TensorLayout.Fact.Bytes)), "tensor copy destination")
        |  machine.require(cluster.shared(bar, UInt[64](8)), "tensor copy mbarrier")
        |  val v2 = tensors.fact(v1, TensorLayout.Fact.Values)
        |  val v3 = tensors.word(v0, 0, 8)
        |  val v4 = tensors.fact(v1, TensorLayout.Fact.Global)
        |  val v5 = tensors.fact(v1, TensorLayout.Fact.Local)
        |  val v6 = left.to[SInt[32]].to[SInt[64]] / v2.to[SInt[64]]
        |  val v7 = tensors.word(v0, 56, 4).to[UInt[8]].to[UInt[64]] + 1
        |  val v8 = (tensors.word(v0, 52, 4) >> 3).to[UInt[3]].to[UInt[64]] + 1
        |  val v9 = (v7 + v8 - 1).to[SInt[64]] / v8.to[SInt[64]]
        |  val v10 = SInt[64](1) * v9
        |  val v11 = tensors.word(v0, 36, 4).to[UInt[32]].to[UInt[64]] + 1
        |  val v12 = (tensors.word(v0, 12, 4).to[UInt[32]].to[UInt[64]] << 4) | (tensors.word(v0, 28, 4).to[UInt[4]].to[UInt[64]] << 36)
        |  val v13 = (tensors.word(v0, 52, 4) >> 24).to[UInt[8]].to[UInt[64]] + 1
        |  val v14 = v13.to[SInt[64]] / v2.to[SInt[64]]
        |  val v15 = tensors.word(v0, 52, 4).to[UInt[3]].to[UInt[64]] + 1
        |  val v16 = (v14.to[UInt[64]] + v15 - 1).to[SInt[64]] / v15.to[SInt[64]]
        |  val v17 = tensors.word(v0, 32, 4).to[UInt[32]].to[UInt[64]] + 1
        |  val v18 = v17.to[SInt[64]] / v2.to[SInt[64]]
        |  for (t11 <- tensors.indices(v10.to[UInt[64]])) {
        |    val v19 = right.to[SInt[32]].to[UInt[64]] + ((t11.to[SInt[64]] / 1) % v9).to[UInt[64]] * v8
        |    val v20 = v3 + v19 * v12
        |    val v21 = v19.to[SInt[64]] >= 0 && v19.to[SInt[64]] < v11.to[SInt[64]]
        |    for (t21 <- tensors.indices(v16.to[UInt[64]])) {
        |      val v22 = v6.to[UInt[64]] + t21 * v15
        |      val v23 = v21 && (v22.to[SInt[64]] >= 0 && v22.to[SInt[64]] < v18)
        |      val v24 = (t11 * v16.to[UInt[64]] + t21) * v5
        |      val v25 = v20 + v22 * v4
        |      tensors.loadRun(v1, v24, v25, UInt[64](1), v23, tensors.fact(v1, TensorLayout.Fact.Fill))
        |    }
        |  }
        |  for (cta <- cluster.destinations(mask)) {
        |    val v26 = tensors.fact(v1, TensorLayout.Fact.Swizzle)
        |    val v27 = tensors.fact(v1, TensorLayout.Fact.Atomicity)
        |    val v28 = tensors.fact(v1, TensorLayout.Fact.Bytes)
        |    val v29 = SInt[64](if (v26 == UInt[64](3) && v27 == UInt[64](2)) 8 else 16)
        |    for (t32 <- tensors.indices(((v28 + v29.to[UInt[64]] - 1).to[SInt[64]] / v29).to[UInt[64]])) {
        |      val v30 = t32 * v29.to[UInt[64]]
        |      cluster.placeChunk(cta, cluster.offset(dst) + TensorMapping.swizzledOffset(v26, v27, cluster.offset(dst), v30), v1, v30, min(v29, (v28 - v30).to[SInt[64]]).to[UInt[64]])
        |    }
        |    machine.require(cluster.present(cluster.peerRank(cta, cluster.owner(bar))), "cta_group::2 peer")
        |    cluster.completeTx(cluster.at(cluster.peerRank(cta, cluster.owner(bar))), cluster.offset(bar), tensors.fact(v1, TensorLayout.Fact.Bytes))
        |  }
        |  ()
        |}""".stripMargin
    )
  }

  test("a tensor store renders its box fetch and runs") {
    assertEquals(
      shown(tensorCopy(TensorMap.Operation.Store)),
      """def `cp.async.bulk.tensor`(left: UInt[64], right: UInt[64], src: UInt[64], dst: UInt[64])(using machine: Machine, tensors: Tensors, cluster: Cluster): Unit = {
        |  val v0 = machine.loadBytes(Space.Generic, src, UInt[64](128))
        |  val v1 = tensors.transfer(v0, false, false, TensorMap.Operation.Store, 2, left.to[SInt[32]].to[UInt[64]], right.to[SInt[32]].to[UInt[64]])
        |  machine.require((tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 >= 1 && (tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 <= 5 && tensors.word(v0, 64, 4).to[UInt[32]] <= 3 && TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) != UInt[64](0), "tensor map")
        |  machine.require((tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 == UInt[64](2) && tensors.word(v0, 64, 4).to[UInt[32]] == UInt[32](0), "tensor copy rank")
        |  machine.require((tensors.word(v0, 8, 4) >> 11).to[UInt[2]] == UInt[2](0), "tensor map interleave")
        |  machine.require(!((tensors.word(v0, 8, 4) >> 7).to[UInt[4]] == UInt[4](12)), "tensor store form")
        |  machine.require(tensors.word(v0, 64, 4).to[UInt[32]] != UInt[32](0) || (left.to[SInt[32]].to[UInt[64]] * TensorLayout.elementBytes((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]])).to[UInt[4]] == UInt[4](0), "tensor tile coordinate")
        |  machine.require(if (TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) == UInt[64](1)) 1 != 0 else (left.to[SInt[32]].to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) && ((tensors.word(v0, 32, 4).to[UInt[32]].to[UInt[64]] + 1).to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) && tensors.word(v0, 52, 4).to[UInt[3]].to[UInt[64]] + 1 == UInt[64](1) && (if (tensors.word(v0, 64, 4).to[UInt[32]] == UInt[32](0)) (((tensors.word(v0, 52, 4) >> 24).to[UInt[8]].to[UInt[64]] + 1).to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) else (tensors.word(v0, 72, 4).to[UInt[32]].to[SInt[32]].to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0)), "tensor copy units")
        |  machine.require(cluster.shared(dst, tensors.fact(v1, TensorLayout.Fact.Bytes)), "tensor copy source")
        |  val v2 = tensors.fact(v1, TensorLayout.Fact.Swizzle)
        |  val v3 = tensors.fact(v1, TensorLayout.Fact.Atomicity)
        |  val v4 = tensors.fact(v1, TensorLayout.Fact.Bytes)
        |  val v5 = SInt[64](if (v2 == UInt[64](3) && v3 == UInt[64](2)) 8 else 16)
        |  for (t6 <- tensors.indices(((v4 + v5.to[UInt[64]] - 1).to[SInt[64]] / v5).to[UInt[64]])) {
        |    val v6 = t6 * v5.to[UInt[64]]
        |    cluster.fetchChunk(cluster.owner(dst), cluster.offset(dst) + TensorMapping.swizzledOffset(v2, v3, cluster.offset(dst), v6), v1, v6, min(v5, (v4 - v6).to[SInt[64]]).to[UInt[64]])
        |  }
        |  val v7 = tensors.fact(v1, TensorLayout.Fact.Values)
        |  val v8 = tensors.word(v0, 0, 8)
        |  val v9 = tensors.fact(v1, TensorLayout.Fact.Global)
        |  val v10 = tensors.fact(v1, TensorLayout.Fact.Local)
        |  val v11 = left.to[SInt[32]].to[SInt[64]] / v7.to[SInt[64]]
        |  val v12 = tensors.word(v0, 56, 4).to[UInt[8]].to[UInt[64]] + 1
        |  val v13 = (tensors.word(v0, 52, 4) >> 3).to[UInt[3]].to[UInt[64]] + 1
        |  val v14 = (v12 + v13 - 1).to[SInt[64]] / v13.to[SInt[64]]
        |  val v15 = SInt[64](1) * v14
        |  val v16 = tensors.word(v0, 36, 4).to[UInt[32]].to[UInt[64]] + 1
        |  val v17 = (tensors.word(v0, 12, 4).to[UInt[32]].to[UInt[64]] << 4) | (tensors.word(v0, 28, 4).to[UInt[4]].to[UInt[64]] << 36)
        |  val v18 = (tensors.word(v0, 52, 4) >> 24).to[UInt[8]].to[UInt[64]] + 1
        |  val v19 = v18.to[SInt[64]] / v7.to[SInt[64]]
        |  val v20 = tensors.word(v0, 52, 4).to[UInt[3]].to[UInt[64]] + 1
        |  val v21 = (v19.to[UInt[64]] + v20 - 1).to[SInt[64]] / v20.to[SInt[64]]
        |  val v22 = tensors.word(v0, 32, 4).to[UInt[32]].to[UInt[64]] + 1
        |  val v23 = v22.to[SInt[64]] / v7.to[SInt[64]]
        |  for (t17 <- tensors.indices(v15.to[UInt[64]])) {
        |    val v24 = right.to[SInt[32]].to[UInt[64]] + ((t17.to[SInt[64]] / 1) % v14).to[UInt[64]] * v13
        |    val v25 = v8 + v24 * v17
        |    val v26 = v24.to[SInt[64]] >= 0 && v24.to[SInt[64]] < v16.to[SInt[64]]
        |    for (t27 <- tensors.indices(v21.to[UInt[64]])) {
        |      val v27 = v11.to[UInt[64]] + t27 * v20
        |      val v28 = v26 && (v27.to[SInt[64]] >= 0 && v27.to[SInt[64]] < v23)
        |      val v29 = (t17 * v21.to[UInt[64]] + t27) * v10
        |      val v30 = v25 + v27 * v9
        |      tensors.storeRun(v1, v29, v30, UInt[64](1), v28)
        |    }
        |  }
        |  ()
        |}""".stripMargin
    )
  }

  test("a tensor reduction renders its operator check and runs") {
    assertEquals(
      shown(tensorCopy(TensorMap.Operation.Reduce(TensorMap.Reduction.Add))),
      """def `cp.async.bulk.tensor`(left: UInt[64], right: UInt[64], src: UInt[64], dst: UInt[64])(using machine: Machine, tensors: Tensors, cluster: Cluster): Unit = {
        |  val v0 = machine.loadBytes(Space.Generic, src, UInt[64](128))
        |  val v1 = tensors.transfer(v0, false, false, TensorMap.Operation.Reduce(TensorMap.Reduction.Add), 2, left.to[SInt[32]].to[UInt[64]], right.to[SInt[32]].to[UInt[64]])
        |  machine.require((tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 >= 1 && (tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 <= 5 && tensors.word(v0, 64, 4).to[UInt[32]] <= 3 && TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) != UInt[64](0), "tensor map")
        |  machine.require((tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 == UInt[64](2) && tensors.word(v0, 64, 4).to[UInt[32]] == UInt[32](0), "tensor copy rank")
        |  machine.require((tensors.word(v0, 8, 4) >> 11).to[UInt[2]] == UInt[2](0), "tensor map interleave")
        |  machine.require(!(TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) > 1), "tensor reduction form")
        |  machine.require(tensors.word(v0, 64, 4).to[UInt[32]] != UInt[32](0) || (left.to[SInt[32]].to[UInt[64]] * TensorLayout.elementBytes((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]])).to[UInt[4]] == UInt[4](0), "tensor tile coordinate")
        |  machine.require(if (TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) == UInt[64](1)) 1 != 0 else (left.to[SInt[32]].to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) && ((tensors.word(v0, 32, 4).to[UInt[32]].to[UInt[64]] + 1).to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) && tensors.word(v0, 52, 4).to[UInt[3]].to[UInt[64]] + 1 == UInt[64](1) && (if (tensors.word(v0, 64, 4).to[UInt[32]] == UInt[32](0)) (((tensors.word(v0, 52, 4) >> 24).to[UInt[8]].to[UInt[64]] + 1).to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) else (tensors.word(v0, 72, 4).to[UInt[32]].to[SInt[32]].to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0)), "tensor copy units")
        |  machine.require(cluster.shared(dst, tensors.fact(v1, TensorLayout.Fact.Bytes)), "tensor copy source")
        |  val v2 = tensors.fact(v1, TensorLayout.Fact.Swizzle)
        |  val v3 = tensors.fact(v1, TensorLayout.Fact.Atomicity)
        |  val v4 = tensors.fact(v1, TensorLayout.Fact.Bytes)
        |  val v5 = SInt[64](if (v2 == UInt[64](3) && v3 == UInt[64](2)) 8 else 16)
        |  for (t6 <- tensors.indices(((v4 + v5.to[UInt[64]] - 1).to[SInt[64]] / v5).to[UInt[64]])) {
        |    val v6 = t6 * v5.to[UInt[64]]
        |    cluster.fetchChunk(cluster.owner(dst), cluster.offset(dst) + TensorMapping.swizzledOffset(v2, v3, cluster.offset(dst), v6), v1, v6, min(v5, (v4 - v6).to[SInt[64]]).to[UInt[64]])
        |  }
        |  machine.require(tensors.fact(v1, TensorLayout.Fact.Unreducible) == UInt[64](0), "tensor reduction operator")
        |  val v7 = tensors.fact(v1, TensorLayout.Fact.Values)
        |  val v8 = tensors.word(v0, 0, 8)
        |  val v9 = tensors.fact(v1, TensorLayout.Fact.Global)
        |  val v10 = tensors.fact(v1, TensorLayout.Fact.Local)
        |  val v11 = left.to[SInt[32]].to[SInt[64]] / v7.to[SInt[64]]
        |  val v12 = tensors.word(v0, 56, 4).to[UInt[8]].to[UInt[64]] + 1
        |  val v13 = (tensors.word(v0, 52, 4) >> 3).to[UInt[3]].to[UInt[64]] + 1
        |  val v14 = (v12 + v13 - 1).to[SInt[64]] / v13.to[SInt[64]]
        |  val v15 = SInt[64](1) * v14
        |  val v16 = tensors.word(v0, 36, 4).to[UInt[32]].to[UInt[64]] + 1
        |  val v17 = (tensors.word(v0, 12, 4).to[UInt[32]].to[UInt[64]] << 4) | (tensors.word(v0, 28, 4).to[UInt[4]].to[UInt[64]] << 36)
        |  val v18 = (tensors.word(v0, 52, 4) >> 24).to[UInt[8]].to[UInt[64]] + 1
        |  val v19 = v18.to[SInt[64]] / v7.to[SInt[64]]
        |  val v20 = tensors.word(v0, 52, 4).to[UInt[3]].to[UInt[64]] + 1
        |  val v21 = (v19.to[UInt[64]] + v20 - 1).to[SInt[64]] / v20.to[SInt[64]]
        |  val v22 = tensors.word(v0, 32, 4).to[UInt[32]].to[UInt[64]] + 1
        |  val v23 = v22.to[SInt[64]] / v7.to[SInt[64]]
        |  for (t17 <- tensors.indices(v15.to[UInt[64]])) {
        |    val v24 = right.to[SInt[32]].to[UInt[64]] + ((t17.to[SInt[64]] / 1) % v14).to[UInt[64]] * v13
        |    val v25 = v8 + v24 * v17
        |    val v26 = v24.to[SInt[64]] >= 0 && v24.to[SInt[64]] < v16.to[SInt[64]]
        |    for (t27 <- tensors.indices(v21.to[UInt[64]])) {
        |      val v27 = v11.to[UInt[64]] + t27 * v20
        |      val v28 = v26 && (v27.to[SInt[64]] >= 0 && v27.to[SInt[64]] < v23)
        |      val v29 = (t17 * v21.to[UInt[64]] + t27) * v10
        |      val v30 = v25 + v27 * v9
        |      tensors.reduceRun(v1, v29, v30, UInt[64](1), v28, TensorMap.Reduction.Add)
        |    }
        |  }
        |  ()
        |}""".stripMargin
    )
  }

  test("a 3D tensor load renders the row and element of each of its dimensions") {
    assertEquals(
      shown(tensorCopy(rank = 3)),
      """def `cp.async.bulk.tensor`(left: UInt[64], right: UInt[64], row: UInt[64], src: UInt[64], dst: UInt[64], bar: UInt[64])(using machine: Machine, tensors: Tensors, cluster: Cluster): Unit = {
        |  val v0 = machine.loadBytes(Space.Generic, src, UInt[64](128))
        |  val v1 = tensors.transfer(v0, false, false, TensorMap.Operation.Load, 3, left.to[SInt[32]].to[UInt[64]], right.to[SInt[32]].to[UInt[64]], row.to[SInt[32]].to[UInt[64]])
        |  machine.require((tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 >= 1 && (tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 <= 5 && tensors.word(v0, 64, 4).to[UInt[32]] <= 3 && TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) != UInt[64](0), "tensor map")
        |  machine.require((tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 == UInt[64](3) && tensors.word(v0, 64, 4).to[UInt[32]] == UInt[32](0), "tensor copy rank")
        |  machine.require((tensors.word(v0, 8, 4) >> 11).to[UInt[2]] == UInt[2](0), "tensor map interleave")
        |  machine.require(tensors.word(v0, 64, 4).to[UInt[32]] != UInt[32](0) || (left.to[SInt[32]].to[UInt[64]] * TensorLayout.elementBytes((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]])).to[UInt[4]] == UInt[4](0), "tensor tile coordinate")
        |  machine.require(if (TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) == UInt[64](1)) 1 != 0 else (left.to[SInt[32]].to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) && ((tensors.word(v0, 32, 4).to[UInt[32]].to[UInt[64]] + 1).to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) && tensors.word(v0, 52, 4).to[UInt[3]].to[UInt[64]] + 1 == UInt[64](1) && (if (tensors.word(v0, 64, 4).to[UInt[32]] == UInt[32](0)) (((tensors.word(v0, 52, 4) >> 24).to[UInt[8]].to[UInt[64]] + 1).to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) else (tensors.word(v0, 72, 4).to[UInt[32]].to[SInt[32]].to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0)), "tensor copy units")
        |  machine.require(cluster.shared(dst, tensors.fact(v1, TensorLayout.Fact.Bytes)), "tensor copy destination")
        |  machine.require(cluster.shared(bar, UInt[64](8)), "tensor copy mbarrier")
        |  val v2 = tensors.fact(v1, TensorLayout.Fact.Values)
        |  val v3 = tensors.word(v0, 0, 8)
        |  val v4 = tensors.fact(v1, TensorLayout.Fact.Global)
        |  val v5 = tensors.fact(v1, TensorLayout.Fact.Local)
        |  val v6 = left.to[SInt[32]].to[SInt[64]] / v2.to[SInt[64]]
        |  val v7 = tensors.word(v0, 56, 4).to[UInt[8]].to[UInt[64]] + 1
        |  val v8 = (tensors.word(v0, 52, 4) >> 3).to[UInt[3]].to[UInt[64]] + 1
        |  val v9 = (v7 + v8 - 1).to[SInt[64]] / v8.to[SInt[64]]
        |  val v10 = (tensors.word(v0, 56, 4) >> 8).to[UInt[8]].to[UInt[64]] + 1
        |  val v11 = (tensors.word(v0, 52, 4) >> 6).to[UInt[3]].to[UInt[64]] + 1
        |  val v12 = (v10 + v11 - 1).to[SInt[64]] / v11.to[SInt[64]]
        |  val v13 = SInt[64](1) * v9 * v12
        |  val v14 = tensors.word(v0, 36, 4).to[UInt[32]].to[UInt[64]] + 1
        |  val v15 = (tensors.word(v0, 12, 4).to[UInt[32]].to[UInt[64]] << 4) | (tensors.word(v0, 28, 4).to[UInt[4]].to[UInt[64]] << 36)
        |  val v16 = tensors.word(v0, 40, 4).to[UInt[32]].to[UInt[64]] + 1
        |  val v17 = (tensors.word(v0, 16, 4).to[UInt[32]].to[UInt[64]] << 4) | ((tensors.word(v0, 28, 4) >> 4).to[UInt[4]].to[UInt[64]] << 36)
        |  val v18 = (tensors.word(v0, 52, 4) >> 24).to[UInt[8]].to[UInt[64]] + 1
        |  val v19 = v18.to[SInt[64]] / v2.to[SInt[64]]
        |  val v20 = tensors.word(v0, 52, 4).to[UInt[3]].to[UInt[64]] + 1
        |  val v21 = (v19.to[UInt[64]] + v20 - 1).to[SInt[64]] / v20.to[SInt[64]]
        |  val v22 = tensors.word(v0, 32, 4).to[UInt[32]].to[UInt[64]] + 1
        |  val v23 = v22.to[SInt[64]] / v2.to[SInt[64]]
        |  for (t14 <- tensors.indices(v13.to[UInt[64]])) {
        |    val v24 = right.to[SInt[32]].to[UInt[64]] + ((t14.to[SInt[64]] / 1) % v9).to[UInt[64]] * v8
        |    val v25 = row.to[SInt[32]].to[UInt[64]] + ((t14.to[SInt[64]] / (SInt[64](1) * v9)) % v12).to[UInt[64]] * v11
        |    val v26 = v3 + v24 * v15 + v25 * v17
        |    val v27 = v24.to[SInt[64]] >= 0 && v24.to[SInt[64]] < v14.to[SInt[64]] && (v25.to[SInt[64]] >= 0 && v25.to[SInt[64]] < v16.to[SInt[64]])
        |    for (t27 <- tensors.indices(v21.to[UInt[64]])) {
        |      val v28 = v6.to[UInt[64]] + t27 * v20
        |      val v29 = v27 && (v28.to[SInt[64]] >= 0 && v28.to[SInt[64]] < v23)
        |      val v30 = (t14 * v21.to[UInt[64]] + t27) * v5
        |      val v31 = v26 + v28 * v4
        |      tensors.loadRun(v1, v30, v31, UInt[64](1), v29, tensors.fact(v1, TensorLayout.Fact.Fill))
        |    }
        |  }
        |  val v32 = tensors.fact(v1, TensorLayout.Fact.Swizzle)
        |  val v33 = tensors.fact(v1, TensorLayout.Fact.Atomicity)
        |  val v34 = tensors.fact(v1, TensorLayout.Fact.Bytes)
        |  val v35 = SInt[64](if (v32 == UInt[64](3) && v33 == UInt[64](2)) 8 else 16)
        |  for (t38 <- tensors.indices(((v34 + v35.to[UInt[64]] - 1).to[SInt[64]] / v35).to[UInt[64]])) {
        |    val v36 = t38 * v35.to[UInt[64]]
        |    cluster.placeChunk(cluster.owner(dst), cluster.offset(dst) + TensorMapping.swizzledOffset(v32, v33, cluster.offset(dst), v36), v1, v36, min(v35, (v34 - v36).to[SInt[64]]).to[UInt[64]])
        |  }
        |  cluster.completeTx(cluster.owner(dst), cluster.offset(bar), tensors.fact(v1, TensorLayout.Fact.Bytes))
        |  ()
        |}""".stripMargin
    )
  }

  test("a gather load renders the row each coordinate selects") {
    assertEquals(
      shown(tensorCopy(gather = true)),
      """def `cp.async.bulk.tensor`(left: UInt[64], right: UInt[64], row: UInt[64], size: UInt[64], mask: UInt[64], src: UInt[64], dst: UInt[64], bar: UInt[64])(using machine: Machine, tensors: Tensors, cluster: Cluster): Unit = {
        |  val v0 = machine.loadBytes(Space.Generic, src, UInt[64](128))
        |  val v1 = tensors.transfer(v0, false, true, TensorMap.Operation.Load, 5, left.to[SInt[32]].to[UInt[64]], right.to[SInt[32]].to[UInt[64]], row.to[SInt[32]].to[UInt[64]], size.to[SInt[32]].to[UInt[64]], mask.to[SInt[32]].to[UInt[64]])
        |  machine.require((tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 >= 1 && (tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 <= 5 && tensors.word(v0, 64, 4).to[UInt[32]] <= 3 && TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) != UInt[64](0), "tensor map")
        |  machine.require((tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 == UInt[64](2) && tensors.word(v0, 64, 4).to[UInt[32]] == UInt[32](0), "tensor copy rank")
        |  machine.require((tensors.word(v0, 8, 4) >> 11).to[UInt[2]] == UInt[2](0), "tensor map interleave")
        |  machine.require(if (TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) == UInt[64](1)) 1 != 0 else (left.to[SInt[32]].to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) && ((tensors.word(v0, 32, 4).to[UInt[32]].to[UInt[64]] + 1).to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) && tensors.word(v0, 52, 4).to[UInt[3]].to[UInt[64]] + 1 == UInt[64](1) && (if (tensors.word(v0, 64, 4).to[UInt[32]] == UInt[32](0)) (((tensors.word(v0, 52, 4) >> 24).to[UInt[8]].to[UInt[64]] + 1).to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) else (tensors.word(v0, 72, 4).to[UInt[32]].to[SInt[32]].to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0)), "tensor copy units")
        |  machine.require(cluster.shared(dst, tensors.fact(v1, TensorLayout.Fact.Bytes)), "tensor copy destination")
        |  machine.require(cluster.shared(bar, UInt[64](8)), "tensor copy mbarrier")
        |  val v2 = tensors.fact(v1, TensorLayout.Fact.Values)
        |  val v3 = tensors.word(v0, 0, 8)
        |  val v4 = tensors.fact(v1, TensorLayout.Fact.Global)
        |  val v5 = tensors.fact(v1, TensorLayout.Fact.Local)
        |  val v6 = left.to[SInt[32]].to[SInt[64]] / v2.to[SInt[64]]
        |  val v7 = tensors.word(v0, 36, 4).to[UInt[32]].to[UInt[64]] + 1
        |  val v8 = (tensors.word(v0, 12, 4).to[UInt[32]].to[UInt[64]] << 4) | (tensors.word(v0, 28, 4).to[UInt[4]].to[UInt[64]] << 36)
        |  val v9 = (tensors.word(v0, 52, 4) >> 24).to[UInt[8]].to[UInt[64]] + 1
        |  val v10 = v9.to[SInt[64]] / v2.to[SInt[64]]
        |  val v11 = tensors.word(v0, 52, 4).to[UInt[3]].to[UInt[64]] + 1
        |  val v12 = (v10.to[UInt[64]] + v11 - 1).to[SInt[64]] / v11.to[SInt[64]]
        |  val v13 = tensors.word(v0, 32, 4).to[UInt[32]].to[UInt[64]] + 1
        |  val v14 = v13.to[SInt[64]] / v2.to[SInt[64]]
        |  for (t7 <- tensors.indices(UInt[64](4))) {
        |    val v15 = if (t7 == UInt[64](0)) right.to[SInt[32]] else if (t7 == UInt[64](1)) row.to[SInt[32]] else if (t7 == UInt[64](2)) size.to[SInt[32]] else mask.to[SInt[32]]
        |    val v16 = v3 + v15.to[UInt[64]] * v8
        |    val v17 = v15 >= 0 && v15.to[SInt[64]] < v7.to[SInt[64]]
        |    for (t17 <- tensors.indices(v12.to[UInt[64]])) {
        |      val v18 = v6.to[UInt[64]] + t17 * v11
        |      val v19 = v17 && (v18.to[SInt[64]] >= 0 && v18.to[SInt[64]] < v14)
        |      val v20 = (t7 * v12.to[UInt[64]] + t17) * v5
        |      val v21 = v16 + v18 * v4
        |      tensors.loadRun(v1, v20, v21, UInt[64](1), v19, tensors.fact(v1, TensorLayout.Fact.Fill))
        |    }
        |  }
        |  val v22 = tensors.fact(v1, TensorLayout.Fact.Swizzle)
        |  val v23 = tensors.fact(v1, TensorLayout.Fact.Atomicity)
        |  val v24 = tensors.fact(v1, TensorLayout.Fact.Bytes)
        |  val v25 = SInt[64](if (v22 == UInt[64](3) && v23 == UInt[64](2)) 8 else 16)
        |  for (t28 <- tensors.indices(((v24 + v25.to[UInt[64]] - 1).to[SInt[64]] / v25).to[UInt[64]])) {
        |    val v26 = t28 * v25.to[UInt[64]]
        |    cluster.placeChunk(cluster.owner(dst), cluster.offset(dst) + TensorMapping.swizzledOffset(v22, v23, cluster.offset(dst), v26), v1, v26, min(v25, (v24 - v26).to[SInt[64]]).to[UInt[64]])
        |  }
        |  cluster.completeTx(cluster.owner(dst), cluster.offset(bar), tensors.fact(v1, TensorLayout.Fact.Bytes))
        |  ()
        |}""".stripMargin
    )
  }

  test("an im2col load renders the walk of its bounding box and the elements of each pixel") {
    assertEquals(
      shown(tensorCopy(im2col = true, rank = 4)),
      """def `cp.async.bulk.tensor`(left: UInt[64], right: UInt[64], row: UInt[64], size: UInt[64], s0: UInt[64], s1: UInt[64], src: UInt[64], dst: UInt[64], bar: UInt[64])(using machine: Machine, tensors: Tensors, cluster: Cluster): Unit = {
        |  val v0 = machine.loadBytes(Space.Generic, src, UInt[64](128))
        |  val v1 = tensors.transfer(v0, true, false, TensorMap.Operation.Load, 4, left.to[SInt[32]].to[UInt[64]], right.to[SInt[32]].to[UInt[64]], row.to[SInt[32]].to[UInt[64]], size.to[SInt[32]].to[UInt[64]], s0.to[UInt[16]].to[UInt[64]], s1.to[UInt[16]].to[UInt[64]])
        |  machine.require((tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 >= 1 && (tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 <= 5 && tensors.word(v0, 64, 4).to[UInt[32]] <= 3 && TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) != UInt[64](0), "tensor map")
        |  machine.require((tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 == UInt[64](4) && tensors.word(v0, 64, 4).to[UInt[32]] != UInt[32](0), "tensor copy rank")
        |  machine.require((tensors.word(v0, 8, 4) >> 11).to[UInt[2]] == UInt[2](0), "tensor map interleave")
        |  machine.require(tensors.word(v0, 64, 4).to[UInt[32]] != UInt[32](0) || (left.to[SInt[32]].to[UInt[64]] * TensorLayout.elementBytes((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]])).to[UInt[4]] == UInt[4](0), "tensor tile coordinate")
        |  machine.require(if (TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) == UInt[64](1)) 1 != 0 else (left.to[SInt[32]].to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) && ((tensors.word(v0, 32, 4).to[UInt[32]].to[UInt[64]] + 1).to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) && tensors.word(v0, 52, 4).to[UInt[3]].to[UInt[64]] + 1 == UInt[64](1) && (if (tensors.word(v0, 64, 4).to[UInt[32]] == UInt[32](0)) (((tensors.word(v0, 52, 4) >> 24).to[UInt[8]].to[UInt[64]] + 1).to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) else (tensors.word(v0, 72, 4).to[UInt[32]].to[SInt[32]].to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0)), "tensor copy units")
        |  machine.require(cluster.shared(dst, tensors.fact(v1, TensorLayout.Fact.Bytes)), "tensor copy destination")
        |  machine.require(cluster.shared(bar, UInt[64](8)), "tensor copy mbarrier")
        |  val v2 = tensors.fact(v1, TensorLayout.Fact.Values)
        |  val v3 = tensors.word(v0, 0, 8)
        |  val v4 = tensors.fact(v1, TensorLayout.Fact.Global)
        |  val v5 = tensors.fact(v1, TensorLayout.Fact.Local)
        |  val v6 = left.to[SInt[32]].to[SInt[64]] / v2.to[SInt[64]]
        |  val v7 = tensors.fact(v1, TensorLayout.Fact.Mode)
        |  val v8 = v7.to[SInt[64]] >= 2
        |  val v9 = v7 == UInt[64](3)
        |  val v10 = tensors.word(v0, 76, 4).to[UInt[32]].to[SInt[32]]
        |  val v11 = if (v9) SInt[32](128) else v10
        |  val v12 = if (v9) (if (v8) s0.to[UInt[16]].to[UInt[64]] else UInt[16](0).to[UInt[64]]) * 4 else if (v8) s0.to[UInt[16]].to[UInt[64]] else UInt[16](0).to[UInt[64]]
        |  val v13 = if (v8) s1.to[UInt[16]] else UInt[16](0)
        |  val v14 = tensors.word(v0, 72, 4).to[UInt[32]].to[SInt[32]]
        |  val v15 = v14.to[SInt[64]] / v2.to[SInt[64]]
        |  val v16 = (tensors.word(v0, 52, 4) >> 3).to[UInt[3]].to[UInt[64]] + 1
        |  val v17 = tensors.word(v0, 80, 4).to[UInt[32]].to[SInt[32]]
        |  val v18 = tensors.word(v0, 36, 4).to[UInt[32]].to[UInt[64]] + 1
        |  val v19 = tensors.word(v0, 92, 4).to[UInt[32]].to[SInt[32]]
        |  val v20 = (tensors.word(v0, 12, 4).to[UInt[32]].to[UInt[64]] << 4) | (tensors.word(v0, 28, 4).to[UInt[4]].to[UInt[64]] << 36)
        |  val v21 = (tensors.word(v0, 52, 4) >> 6).to[UInt[3]].to[UInt[64]] + 1
        |  val v22 = tensors.word(v0, 84, 4).to[UInt[32]].to[SInt[32]]
        |  val v23 = tensors.word(v0, 40, 4).to[UInt[32]].to[UInt[64]] + 1
        |  val v24 = tensors.word(v0, 96, 4).to[UInt[32]].to[SInt[32]]
        |  val v25 = (tensors.word(v0, 16, 4).to[UInt[32]].to[UInt[64]] << 4) | ((tensors.word(v0, 28, 4) >> 4).to[UInt[4]].to[UInt[64]] << 36)
        |  val v26 = (tensors.word(v0, 20, 4).to[UInt[32]].to[UInt[64]] << 4) | ((tensors.word(v0, 28, 4) >> 8).to[UInt[4]].to[UInt[64]] << 36)
        |  val v27 = tensors.word(v0, 44, 4).to[UInt[32]].to[UInt[64]] + 1
        |  val v28 = tensors.word(v0, 32, 4).to[UInt[32]].to[UInt[64]] + 1
        |  val v29 = v28.to[SInt[64]] / v2.to[SInt[64]]
        |  for (t16 <- tensors.indices(v11.to[UInt[64]] + v12)) {
        |    val v30 = if (v11.to[SInt[64]] <= t16.to[SInt[64]] && v9) (SInt[64](32) * ((t16 - v11.to[UInt[64]]).to[SInt[64]] % 4 + 1) + (t16 - v11.to[UInt[64]]).to[SInt[64]] / 4).to[UInt[64]] else t16
        |    val v31 = right.to[SInt[32]].to[SInt[64]] + v13.to[UInt[64]].to[SInt[64]]
        |    val v32 = v17.to[SInt[64]] + v13.to[UInt[64]].to[SInt[64]]
        |    val v33 = v18 - 1 + (v19.to[SInt[64]] + v13.to[UInt[64]].to[SInt[64]]).to[UInt[64]]
        |    val v34 = max(SInt[64](1), (v33 - v31.to[UInt[64]]).to[SInt[64]] / v16.to[SInt[64]] + 1)
        |    val v35 = max(SInt[64](1), (v33 - v32.to[UInt[64]]).to[SInt[64]] / v16.to[SInt[64]] + 1)
        |    val v36 = if (v30.to[SInt[64]] < v34) v31.to[UInt[64]] + v30 * v16 else v32.to[UInt[64]] + ((v30 - v34.to[UInt[64]]).to[SInt[64]] % v35).to[UInt[64]] * v16
        |    val v37 = if (v30.to[SInt[64]] < v34) SInt[64](0) else SInt[64](1) + (v30 - v34.to[UInt[64]]).to[SInt[64]] / v35
        |    val v38 = if (v8) UInt[16](0) else s0.to[UInt[16]]
        |    val v39 = v36 + v38.to[UInt[64]]
        |    val v40 = v23 - 1 + v24.to[UInt[64]]
        |    val v41 = max(SInt[64](1), (v40 - row.to[SInt[32]].to[UInt[64]]).to[SInt[64]] / v21.to[SInt[64]] + 1)
        |    val v42 = max(SInt[64](1), (v40 - v22.to[UInt[64]]).to[SInt[64]] / v21.to[SInt[64]] + 1)
        |    val v43 = if (v8) SInt[64](0) else v37
        |    val v44 = if (v43 < v41) row.to[SInt[32]].to[UInt[64]] + v43.to[UInt[64]] * v21 else v22.to[UInt[64]] + ((v43 - v41) % v42).to[UInt[64]] * v21
        |    val v45 = if (v43 < v41) SInt[64](0) else SInt[64](1) + (v43 - v41) / v42
        |    val v46 = if (v8) UInt[16](0) else s1.to[UInt[16]]
        |    val v47 = v44 + v46.to[UInt[64]]
        |    val v48 = if (v8) v37 else v45
        |    val v49 = size.to[SInt[32]].to[SInt[64]] + v48
        |    val v50 = v3 + v39 * v20 + v47 * v25 + v49.to[UInt[64]] * v26
        |    val v51 = v39.to[SInt[64]] >= 0 && v39.to[SInt[64]] < v18.to[SInt[64]] && (v47.to[SInt[64]] >= 0 && v47.to[SInt[64]] < v23.to[SInt[64]]) && (v49 >= 0 && v49 < v27.to[SInt[64]])
        |    for (t51 <- tensors.indices(v15.to[UInt[64]])) {
        |      val v52 = v6.to[UInt[64]] + t51 * 1
        |      val v53 = v51 && (v52.to[SInt[64]] >= 0 && v52.to[SInt[64]] < v29)
        |      val v54 = (t16 * v15.to[UInt[64]] + t51) * v5
        |      val v55 = v50 + v52 * v4
        |      tensors.loadRun(v1, v54, v55, UInt[64](1), v53, tensors.fact(v1, TensorLayout.Fact.Fill))
        |    }
        |  }
        |  val v56 = tensors.fact(v1, TensorLayout.Fact.Swizzle)
        |  val v57 = tensors.fact(v1, TensorLayout.Fact.Atomicity)
        |  val v58 = tensors.fact(v1, TensorLayout.Fact.Bytes)
        |  val v59 = SInt[64](if (v56 == UInt[64](3) && v57 == UInt[64](2)) 8 else 16)
        |  for (t62 <- tensors.indices(((v58 + v59.to[UInt[64]] - 1).to[SInt[64]] / v59).to[UInt[64]])) {
        |    val v60 = t62 * v59.to[UInt[64]]
        |    cluster.placeChunk(cluster.owner(dst), cluster.offset(dst) + TensorMapping.swizzledOffset(v56, v57, cluster.offset(dst), v60), v1, v60, min(v59, (v58 - v60).to[SInt[64]]).to[UInt[64]])
        |  }
        |  cluster.completeTx(cluster.owner(dst), cluster.offset(bar), tensors.fact(v1, TensorLayout.Fact.Bytes))
        |  ()
        |}""".stripMargin
    )
  }

  test("a tensor prefetch renders its checks alone") {
    assertEquals(
      shown(tensorCopy(TensorMap.Operation.Prefetch, gather = true)),
      """def `cp.async.bulk.tensor`(left: UInt[64], right: UInt[64], row: UInt[64], size: UInt[64], mask: UInt[64], src: UInt[64])(using machine: Machine, tensors: Tensors): Unit = {
        |  val v0 = machine.loadBytes(Space.Generic, src, UInt[64](128))
        |  val _ = tensors.transfer(v0, false, true, TensorMap.Operation.Prefetch, 5, left.to[SInt[32]].to[UInt[64]], right.to[SInt[32]].to[UInt[64]], row.to[SInt[32]].to[UInt[64]], size.to[SInt[32]].to[UInt[64]], mask.to[SInt[32]].to[UInt[64]])
        |  machine.require((tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 >= 1 && (tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 <= 5 && tensors.word(v0, 64, 4).to[UInt[32]] <= 3 && TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) != UInt[64](0), "tensor map")
        |  machine.require((tensors.word(v0, 8, 4) >> 4).to[UInt[3]].to[UInt[64]] + 1 == UInt[64](2) && tensors.word(v0, 64, 4).to[UInt[32]] == UInt[32](0), "tensor copy rank")
        |  machine.require(if (TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) == UInt[64](1)) 1 != 0 else (left.to[SInt[32]].to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) && ((tensors.word(v0, 32, 4).to[UInt[32]].to[UInt[64]] + 1).to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) && tensors.word(v0, 52, 4).to[UInt[3]].to[UInt[64]] + 1 == UInt[64](1) && (if (tensors.word(v0, 64, 4).to[UInt[32]] == UInt[32](0)) (((tensors.word(v0, 52, 4) >> 24).to[UInt[8]].to[UInt[64]] + 1).to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0) else (tensors.word(v0, 72, 4).to[UInt[32]].to[SInt[32]].to[UInt[64]] & TensorLayout.elementValues((tensors.word(v0, 8, 4) >> 7).to[UInt[4]].to[UInt[64]], (tensors.word(v0, 8, 4) >> 16).to[UInt[1]].to[UInt[64]]) - 1) == UInt[64](0)), "tensor copy units")
        |  ()
        |}""".stripMargin
    )
  }

  test("a bulk fill renders its validation and clear") {
    assertEquals(
      shown(bulkFill()),
      """def `st.bulk`(size: UInt[64], dst: UInt[64])(using machine: Machine, cluster: Cluster): Unit = {
        |  machine.require(cluster.shared(dst, size), "bulk fill")
        |  machine.require(size.to[SInt[64]] >= 0 && size.to[UInt[3]] == UInt[3](0), "invalid bulk fill size")
        |  cluster.clear(cluster.owner(dst), cluster.offset(dst), size)
        |  ()
        |}""".stripMargin
    )
  }

  test("a bulk reduction with an mbarrier renders its combine and completion") {
    assertEquals(
      shown(bulkReduce()),
      """def `cp.reduce.async.bulk`(dst: UInt[64], src: UInt[64], size: UInt[64], bar: UInt[64])(using machine: Machine, cluster: Cluster): Unit = {
        |  machine.require(dst.to[UInt[4]] == UInt[4](0) && src.to[UInt[4]] == UInt[4](0) && size.to[UInt[4]] == UInt[4](0) && size.to[SInt[64]] >= 0 && size.to[SInt[64]] <= 0xfffff, "invalid bulk reduction")
        |  val v0 = machine.loadBytes(Space.Shared, src, size)
        |  machine.require(cluster.shared(dst, size), "bulk reduction destination")
        |  machine.require(cluster.shared(bar, UInt[64](8)), "bulk reduction mbarrier")
        |  cluster.reduceBytes(cluster.owner(dst), cluster.offset(dst), v0, TensorMap.Reduction.Add, TensorMap.Element.F32)
        |  cluster.completeTx(cluster.owner(dst), cluster.offset(bar), size)
        |  ()
        |}""".stripMargin
    )
  }

  test("a bulk reduction without an mbarrier renders its global reduction") {
    assertEquals(
      shown(bulkReduce(completes = false, operator = TensorMap.Reduction.Increment, element = TensorMap.Element.U32)),
      """def `cp.reduce.async.bulk`(dst: UInt[64], src: UInt[64], size: UInt[64])(using machine: Machine, memory: Memory): Unit = {
        |  machine.require(dst.to[UInt[4]] == UInt[4](0) && src.to[UInt[4]] == UInt[4](0) && size.to[UInt[4]] == UInt[4](0) && size.to[SInt[64]] >= 0 && size.to[SInt[64]] <= 0xfffff, "invalid bulk reduction")
        |  val v0 = machine.loadBytes(Space.Shared, src, size)
        |  memory.reduceGlobal(dst, v0, TensorMap.Reduction.Increment, TensorMap.Element.U32)
        |  ()
        |}""".stripMargin
    )
  }

  test("an asynchronous shared store renders its stores and completion") {
    assertEquals(
      shown(sharedStore()),
      """def `st.async`(dst: UInt[64], bar: UInt[64], src: UInt[64], left: UInt[64])(using machine: Machine, cluster: Cluster): Unit = {
        |  machine.require(cluster.shared(dst, UInt[64](8)), "asynchronous store")
        |  machine.require(cluster.shared(bar, UInt[64](8)), "asynchronous store mbarrier")
        |  machine.require(cluster.same(cluster.owner(dst), cluster.owner(bar)), "asynchronous store cta")
        |  cluster.storeWord[UInt[32]](cluster.owner(dst), cluster.offset(dst), src.to[UInt[32]])
        |  cluster.storeWord[UInt[32]](cluster.owner(dst), cluster.offset(dst) + 4, left.to[UInt[32]])
        |  cluster.completeTx(cluster.owner(dst), cluster.offset(bar), UInt[64](8))
        |  ()
        |}""".stripMargin
    )
  }

  test("an asynchronous shared reduction renders its reduction and completion") {
    assertEquals(
      shown(sharedReduce()),
      """def `red.async`(dst: UInt[64], bar: UInt[64], src: UInt[64])(using machine: Machine, cluster: Cluster): Unit = {
        |  machine.require(cluster.shared(dst, UInt[64](4)), "asynchronous reduction")
        |  machine.require(cluster.shared(bar, UInt[64](8)), "asynchronous reduction mbarrier")
        |  cluster.reduceWord[UInt[32]](cluster.owner(dst), cluster.offset(dst), src.to[UInt[32]], AtomicOperation.Add)
        |  cluster.completeTx(cluster.owner(dst), cluster.offset(bar), UInt[64](4))
        |  ()
        |}""".stripMargin
    )
  }

  test("an mbarrier initialisation renders its window check and init") {
    assertEquals(
      shown(mbarrierInit()),
      """def `mbarrier.init`(bar: UInt[64], size: UInt[64])(using machine: Machine, cluster: Cluster, barriers: Barriers): Unit = {
        |  machine.require(cluster.shared(bar, UInt[64](8)), "mbarrier")
        |  barriers.init(bar, size)
        |  ()
        |}""".stripMargin
    )
  }

  test("an mbarrier invalidation renders its window check and invalidate") {
    assertEquals(
      shown(mbarrierInvalidate()),
      """def `mbarrier.inval`(bar: UInt[64])(using machine: Machine, cluster: Cluster, barriers: Barriers): Unit = {
        |  machine.require(cluster.shared(bar, UInt[64](8)), "mbarrier")
        |  barriers.invalidate(bar)
        |  ()
        |}""".stripMargin
    )
  }

  test("an mbarrier load renders its window check and load") {
    assertEquals(
      shown(mbarrierLoad()),
      """def `mbarrier.load`(bar: UInt[64])(using machine: Machine, cluster: Cluster, barriers: Barriers): Unit = {
        |  machine.require(cluster.shared(bar, UInt[64](8)), "mbarrier")
        |  barriers.load(bar)
        |  ()
        |}""".stripMargin
    )
  }

  test("an arrival with a token renders its arrive and the token's write") {
    assertEquals(
      shown(mbarrierArrive()),
      """def `mbarrier.arrive`(bar: UInt[64], size: UInt[64])(using machine: Machine, cluster: Cluster, barriers: Barriers): (token: UInt[64]) = {
        |  machine.require(cluster.shared(bar, UInt[64](8)), "mbarrier")
        |  val v0 = barriers.arrive(bar, size, UInt[64](0), false, true)
        |  (token = v0)
        |}""".stripMargin
    )
  }

  test("an arrival without a token renders its arrive alone") {
    assertEquals(
      shown(mbarrierArrive(destination = None)),
      """def `mbarrier.arrive`(bar: UInt[64], size: UInt[64])(using machine: Machine, cluster: Cluster, barriers: Barriers): Unit = {
        |  machine.require(cluster.shared(bar, UInt[64](8)), "mbarrier")
        |  val _ = barriers.arrive(bar, size, UInt[64](0), false, true)
        |  ()
        |}""".stripMargin
    )
  }

  test("an arrival expecting bytes renders the bytes it passes") {
    assertEquals(
      shown(mbarrierArrive(destination = None, bytes = size)),
      """def `mbarrier.arrive`(bar: UInt[64], size: UInt[64])(using machine: Machine, cluster: Cluster, barriers: Barriers): Unit = {
        |  machine.require(cluster.shared(bar, UInt[64](8)), "mbarrier")
        |  val _ = barriers.arrive(bar, size, size, false, true)
        |  ()
        |}""".stripMargin
    )
  }

  test("an arrival that drops and leaves its phase incomplete renders the flags it passes") {
    assertEquals(
      shown(mbarrierArrive(destination = None, drop = true, complete = false)),
      """def `mbarrier.arrive`(bar: UInt[64], size: UInt[64])(using machine: Machine, cluster: Cluster, barriers: Barriers): Unit = {
        |  machine.require(cluster.shared(bar, UInt[64](8)), "mbarrier")
        |  val _ = barriers.arrive(bar, size, UInt[64](0), true, false)
        |  ()
        |}""".stripMargin
    )
  }

  test("an expected transaction count renders as an expect") {
    assertEquals(
      shown(mbarrierTransaction(complete = false)),
      """def `mbarrier.transaction`(bar: UInt[64], size: UInt[64])(using machine: Machine, cluster: Cluster, barriers: Barriers): Unit = {
        |  machine.require(cluster.shared(bar, UInt[64](8)), "mbarrier")
        |  barriers.transaction(bar, size, false)
        |  ()
        |}""".stripMargin
    )
  }

  test("a completed transaction count renders as a complete") {
    assertEquals(
      shown(mbarrierTransaction(complete = true)),
      """def `mbarrier.transaction`(bar: UInt[64], size: UInt[64])(using machine: Machine, cluster: Cluster, barriers: Barriers): Unit = {
        |  machine.require(cluster.shared(bar, UInt[64](8)), "mbarrier")
        |  barriers.transaction(bar, size, true)
        |  ()
        |}""".stripMargin
    )
  }

  test("a wait by phase renders its test and predicate write") {
    assertEquals(
      shown(mbarrierTest(parity = false)),
      """def `mbarrier.test_wait`(bar: UInt[64], src: UInt[64])(using machine: Machine, cluster: Cluster, barriers: Barriers): (done: Bool) = {
        |  machine.require(cluster.shared(bar, UInt[64](8)), "mbarrier")
        |  val v0 = barriers.test(bar, src, false)
        |  (done = v0)
        |}""".stripMargin
    )
  }

  test("a wait by parity renders its test and predicate write") {
    assertEquals(
      shown(mbarrierTest(parity = true)),
      """def `mbarrier.test_wait`(bar: UInt[64], src: UInt[64])(using machine: Machine, cluster: Cluster, barriers: Barriers): (done: Bool) = {
        |  machine.require(cluster.shared(bar, UInt[64](8)), "mbarrier")
        |  val v0 = barriers.test(bar, src, true)
        |  (done = v0)
        |}""".stripMargin
    )
  }

  test("a pending count renders as the arrivals a token leaves") {
    assertEquals(
      shown(mbarrierPending()),
      """def `mbarrier.pending_count`(src: UInt[64]): (dst: UInt[64]) =
        |  (dst = UInt[64](0x100000) - (src >> 43).to[UInt[20]].to[UInt[64]])""".stripMargin
    )
  }

  test("an exit renders as a request to end the lanes") {
    assertEquals(
      shown(exits()),
      """def exit()(using machine: Machine): Unit = {
        |  machine.exit()
        |  ()
        |}""".stripMargin
    )
  }

  test("a guarded exit renders its request under the guard") {
    assertEquals(
      shown(exits(guarded = true)),
      """def exit(p: Bool)(using machine: Machine): Unit = {
        |  if (p) machine.exit() else ()
        |  ()
        |}""".stripMargin
    )
  }

  test("a return renders as a request to return the lanes") {
    assertEquals(
      shown(returns()),
      """def ret()(using frames: Frames): Unit = {
        |  frames.returnControl()
        |  ()
        |}""".stripMargin
    )
  }

  test("a trap renders as a check that never holds") {
    assertEquals(
      shown(traps()),
      """def brkpt()(using machine: Machine): Unit = {
        |  machine.require(false, "trap")
        |  ()
        |}""".stripMargin
    )
  }

  test("a guarded trap renders its check under the guard") {
    assertEquals(
      shown(traps(guarded = true)),
      """def brkpt(p: Bool)(using machine: Machine): Unit = {
        |  if (p) machine.require(false, "trap") else ()
        |  ()
        |}""".stripMargin
    )
  }

  test("a branch table renders its index and targets as a request to the frames") {
    assertEquals(
      shown(branchTable()),
      """def brx(src: UInt[64])(using frames: Frames): Unit = {
        |  frames.branchTable(src, Seq(7, 9))
        |  ()
        |}""".stripMargin
    )
  }

  test("a call of a direct function renders its address") {
    assertEquals(
      shown(call(VMIR.Value.Immediate(0x1000L))),
      """def call()(using frames: Frames): Unit = {
        |  frames.call(UInt[64](0x1000), Seq(1), Seq(2, 3))
        |  ()
        |}""".stripMargin
    )
  }

  test("a call of an indirect function renders the register that names it") {
    assertEquals(
      shown(call(source)),
      """def call(src: UInt[64])(using frames: Frames): Unit = {
        |  frames.call(src, Seq(1), Seq(2, 3))
        |  ()
        |}""".stripMargin
    )
  }

  test("a device builtin renders as a request to the frames") {
    assertEquals(
      shown(builtin()),
      """def malloc()(using frames: Frames): Unit = {
        |  frames.builtin(VMIR.Builtin.Malloc, Seq(0), Seq(1))
        |  ()
        |}""".stripMargin
    )
  }

  test("a convergence setup renders the barrier of the lane's warp into its token") {
    assertEquals(
      shown(setupConvergence()),
      """def bssy()(using barriers: Barriers): (token: UInt[64]) =
        |  (token = barriers.setup())""".stripMargin
    )
  }

  test("a convergence synchronisation renders a wait at the token's barrier") {
    assertEquals(
      shown(synchroniseConvergence()),
      """def bsync(token: UInt[64])(using barriers: Barriers): Unit = {
        |  barriers.synchronise(token)
        |  ()
        |}""".stripMargin
    )
  }

  test("a convergence break renders the lanes leaving the token's barrier under the guard") {
    assertEquals(
      shown(leaveConvergence(guarded = true)),
      """def bbreak(p: Bool, token: UInt[64])(using barriers: Barriers): Unit = {
        |  if (p) {
        |    barriers.leave(token)
        |  }
        |  ()
        |}""".stripMargin
    )
  }

  test("a predicated branch renders its jump under the predicate it executes for") {
    assertEquals(
      predicatedBranch(negated = false).toList.map(ScalaBackend.show(_)),
      List("""def bra(done: Bool)(using machine: Machine): Unit = {
        |  if (done) machine.jump(7) else ()
        |  ()
        |}""".stripMargin)
    )
  }

  test("a branch predicated on a negated predicate renders the negation of the predicate") {
    assertEquals(
      predicatedBranch(negated = true).toList.map(ScalaBackend.show(_)),
      List("""def bra(done: Bool)(using machine: Machine): Unit = {
        |  if (!done) machine.jump(7) else ()
        |  ()
        |}""".stripMargin)
    )
  }

  test("the lane steps before a machine instruction bind what it reads, and its predicate guards it") {
    assertEquals(
      lowered(synchroniseAfterMove(guarded = true)).toList.map(_.map(ScalaBackend.show(_))),
      List(Right("""def op(src: UInt[64], done: Bool)(using barriers: Barriers): (token: UInt[64]) = {
        |  if (done) barriers.synchronise(src) else ()
        |  (token = src)
        |}""".stripMargin))
    )
  }

  test("a machine instruction that reads what no step binds is reported with its reason") {
    assertEquals(
      lowered(synchroniseUnbound).toList.map(_.left.map(_.message)),
      List(Left("op reads t0, which is neither declared nor assigned"))
    )
  }

  test("a barrier sync of immediate operands parks the lanes on that barrier") {
    assertEquals(
      shown(barrierSync(Value.Immediate(0L))),
      """def `bar.sync`()(using machine: Machine): Unit = {
        |  machine.park(UInt[64](0), UInt[64](0), true)
        |  ()
        |}""".stripMargin
    )
  }

  test("a barrier sync of register operands requests each lane's own barrier and count") {
    assertEquals(
      shown(barrierSync(barrier, Some(size))),
      """def `bar.sync`(bar: UInt[64], size: UInt[64])(using barriers: Barriers): Unit = {
        |  barriers.parkEach(bar, size, true, None)
        |  ()
        |}""".stripMargin
    )
  }

  test("a barrier arrive of immediate operands parks the lanes without waiting") {
    assertEquals(
      shown(barrierArrive(Value.Immediate(1L), Value.Immediate(32L))),
      """def `bar.arrive`()(using machine: Machine): Unit = {
        |  machine.park(UInt[64](1), UInt[64](32), false)
        |  ()
        |}""".stripMargin
    )
  }

  test("a barrier reduction of immediate operands parks the lanes and reduces their predicates") {
    assertEquals(
      shown(barrierReduce(
        BarrierResult.Combined(Predicate(2), PredicateOperation.And),
        Value.Immediate(0L),
        Predicate(3)
      )),
      """def `bar.red`()(using barriers: Barriers): Unit = {
        |  barriers.parkReduce(UInt[64](0), UInt[64](0), (result = VMIR.BarrierResult.Combined(VMIR.Predicate(2), VMIR.PredicateOperation.And), predicate = VMIR.Predicate(3), negated = false))
        |  ()
        |}""".stripMargin
    )
  }

  test("a barrier reduction of register operands requests each lane's reduction with its count") {
    assertEquals(
      shown(barrierReduce(BarrierResult.Count(Register(9)), barrier, done, Some(size), negated = true)),
      """def `bar.red`(bar: UInt[64], size: UInt[64])(using barriers: Barriers): Unit = {
        |  barriers.parkEach(bar, size, true, Some((result = VMIR.BarrierResult.Count(VMIR.Register(9)), predicate = VMIR.Predicate(0), negated = true)))
        |  ()
        |}""".stripMargin
    )
  }

  test("a warp barrier requests its member mask under the guard") {
    assertEquals(
      shown(warpBarrier(guarded = true)),
      """def `bar.warp.sync`(p: Bool, mask: UInt[64])(using barriers: Barriers): Unit = {
        |  if (p) barriers.warpSync(mask) else ()
        |  ()
        |}""".stripMargin
    )
  }

  test("a cluster barrier requests the arrival or the wait") {
    assertEquals(
      shown(clusterBarrier(arrive = true)),
      """def `barrier.cluster`()(using cluster: Cluster): Unit = {
        |  cluster.barrier(true)
        |  ()
        |}""".stripMargin
    )
  }

  test("a barrier arrive of register operands requests each lane's own barrier and count without waiting") {
    assertEquals(
      shown(barrierArrive(barrier, size)),
      """def `bar.arrive`(bar: UInt[64], size: UInt[64])(using barriers: Barriers): Unit = {
        |  barriers.parkEach(bar, size, false, None)
        |  ()
        |}""".stripMargin
    )
  }

  test("a barrier reduction of immediate operands with a count parks the lanes on that count") {
    assertEquals(
      shown(barrierReduce(
        BarrierResult.Combined(Predicate(2), PredicateOperation.And),
        Value.Immediate(0L),
        Predicate(3),
        Some(Value.Immediate(32L))
      )),
      """def `bar.red`()(using barriers: Barriers): Unit = {
        |  barriers.parkReduce(UInt[64](0), UInt[64](32), (result = VMIR.BarrierResult.Combined(VMIR.Predicate(2), VMIR.PredicateOperation.And), predicate = VMIR.Predicate(3), negated = false))
        |  ()
        |}""".stripMargin
    )
  }

  test("a barrier reduction of an immediate barrier with register counts requests each lane's reduction") {
    assertEquals(
      shown(barrierReduce(BarrierResult.Count(Register(9)), Value.Immediate(0L), done, Some(size))),
      """def `bar.red`(size: UInt[64])(using barriers: Barriers): Unit = {
        |  barriers.parkEach(UInt[64](0), size, true, Some((result = VMIR.BarrierResult.Count(VMIR.Register(9)), predicate = VMIR.Predicate(0), negated = false)))
        |  ()
        |}""".stripMargin
    )
  }

  test("a cluster map renders its shared check, rank check and cluster address") {
    assertEquals(
      shown(clusterMap()),
      """def mapa(src: UInt[64], size: UInt[64])(using machine: Machine, cluster: Cluster): (dst: UInt[64]) = {
        |  machine.require(cluster.shared(src, UInt[64](1)), "mapa of a non-shared address")
        |  machine.require(size.to[SInt[64]] >= 0 && size.to[SInt[64]] < cluster.clusterSize().to[SInt[64]], "mapa rank")
        |  val v0 = cluster.clusterAddress(src, size)
        |  (dst = v0)
        |}""".stripMargin
    )
  }

  test("a cluster rank renders the rank of the address's CTA, or the issuing CTA's rank for its own window") {
    assertEquals(
      shown(clusterRank()),
      """def getctarank(src: UInt[64])(using machine: Machine, cluster: Cluster): (dst: UInt[64]) = {
        |  machine.require(cluster.shared(src, UInt[64](1)), "getctarank of a non-shared address")
        |  val v0 = if (cluster.rankOf(src).to[SInt[64]] < 0) cluster.ctaRank() else cluster.rankOf(src)
        |  (dst = v0)
        |}""".stripMargin
    )
  }

  test("a copy from global memory renders its size check, load and zero-filled shared stores") {
    assertEquals(
      shown(asyncCopy()),
      """def `cp.async`(dst: UInt[64], src: UInt[64], size: UInt[64])(using machine: Machine, cluster: Cluster): Unit = {
        |  machine.require(size.to[SInt[64]] >= 0 && size.to[SInt[64]] <= 16, "cp.async source size")
        |  val v0 = machine.loadBytes(Space.Global, src, size)
        |  cluster.storeSharedBytes(dst, v0, UInt[64](16))
        |  ()
        |}""".stripMargin
    )
  }

  test("an allocation renders its stack pointer as the destination's value") {
    assertEquals(
      shown(stackAllocate()),
      """def alloca(size: UInt[64])(using frames: Frames): (dst: UInt[64]) = {
        |  val v0 = frames.allocate(size, UInt[64](16))
        |  (dst = v0)
        |}""".stripMargin
    )
  }

  test("a stack save renders the stack pointer as the destination's value") {
    assertEquals(
      shown(stackSave()),
      """def stacksave()(using frames: Frames): (dst: UInt[64]) =
        |  (dst = frames.stackPointer())""".stripMargin
    )
  }

  test("a stack restore renders its request to the frames") {
    assertEquals(
      shown(stackRestore()),
      """def stackrestore(src: UInt[64])(using frames: Frames): Unit = {
        |  frames.restoreStack(src)
        |  ()
        |}""".stripMargin
    )
  }

  test("a guarded stack restore renders its request under the guard") {
    assertEquals(
      shown(stackRestore(guarded = true)),
      """def stackrestore(p: Bool, src: UInt[64])(using frames: Frames): Unit = {
        |  if (p) {
        |    frames.restoreStack(src)
        |  }
        |  ()
        |}""".stripMargin
    )
  }

  test("a cluster cancellation renders its alignment check, completion and response writes") {
    assertEquals(
      shown(clusterTryCancel()),
      """def `clusterlaunchcontrol.try_cancel`(src: UInt[64], bar: UInt[64])(using machine: Machine, cluster: Cluster, barriers: Barriers): Unit = {
        |  machine.require(src.to[UInt[4]] == UInt[4](0) && bar.to[UInt[3]] == UInt[3](0), "cluster cancellation alignment")
        |  machine.require(cluster.shared(bar, UInt[64](8)), "mbarrier")
        |  barriers.transaction(bar, UInt[64](16), true)
        |  cluster.storeShared[UInt[64]](src, UInt[64](0))
        |  cluster.storeShared[UInt[64]](src + 8, UInt[64](0))
        |  ()
        |}""".stripMargin
    )
  }

  test("a multicast cluster cancellation renders its completion and response writes for every CTA") {
    assertEquals(
      shown(clusterTryCancel(multicast = true)),
      """def `clusterlaunchcontrol.try_cancel`(src: UInt[64], bar: UInt[64])(using machine: Machine, cluster: Cluster, barriers: Barriers): Unit = {
        |  machine.require(src.to[UInt[4]] == UInt[4](0) && bar.to[UInt[3]] == UInt[3](0), "cluster cancellation alignment")
        |  for (rank <- cluster.ranks()) {
        |    machine.require(cluster.shared(cluster.clusterAddress(bar, rank), UInt[64](8)), "mbarrier")
        |    barriers.transaction(cluster.clusterAddress(bar, rank), UInt[64](16), true)
        |    cluster.storeShared[UInt[64]](cluster.clusterAddress(src, rank), UInt[64](0))
        |    cluster.storeShared[UInt[64]](cluster.clusterAddress(src, rank) + 8, UInt[64](0))
        |  }
        |  ()
        |}""".stripMargin
    )
  }

  test("a matrix store renders its row address checks, element placement and shared stores") {
    assertEquals(
      shown(storeMatrix(1)),
      """def stmatrix(row: UInt[64], s0: UInt[64])(using machine: Machine, lanes: Lanes, cluster: Cluster): Unit = {
        |  machine.require(lanes.converged(), "warp convergence")
        |  machine.require(lanes.index() >= 8 || row.to[UInt[4]] == UInt[4](0), "invalid matrix row address")
        |  val v0 = lanes.gather(row)
        |  cluster.storeShared[UInt[16]](lanes.peer(v0, MatrixLayout.row(UInt[64](0), lanes.index(), UInt[64](0))) + MatrixLayout.column(UInt[64](0), lanes.index(), UInt[64](0)) * 2, s0.to[UInt[16]])
        |  cluster.storeShared[UInt[16]](lanes.peer(v0, MatrixLayout.row(UInt[64](0), lanes.index(), UInt[64](1))) + MatrixLayout.column(UInt[64](0), lanes.index(), UInt[64](1)) * 2, (s0 >> 16).to[UInt[16]])
        |  ()
        |}""".stripMargin
    )
  }

  test("a sub-byte fragment load renders its byte reads and bit fields") {
    assertEquals(
      shown(loadFragment(Matrix.Fragment(Matrix.Shape(8, 8, 32), Matrix.Role.A, Matrix.Element.S4, wmma = true))),
      """def wmma_load(row: UInt[64], size: UInt[64])(using machine: Machine, lanes: Lanes, units: Units, cluster: Cluster): (f0: UInt[64]) = {
        |  machine.require(lanes.converged(), "warp convergence")
        |  val v0 = lanes.gather(row)
        |  val v1 = lanes.gather(size)
        |  machine.require(units.agreed(v0), "uniform wmma address")
        |  machine.require(units.agreed(v1), "uniform wmma stride")
        |  machine.require(lanes.peer(v0, lanes.index()).to[UInt[5]] == UInt[5](0) && ((if ((lanes.peer(v1, lanes.index()) * 4).to[SInt[64]] < 0) UInt[64](0) - lanes.peer(v1, lanes.index()) * 4 else lanes.peer(v1, lanes.index()) * 4) >> 3).to[UInt[4]] == UInt[4](0), "wmma alignment")
        |  machine.require(lanes.peer(v1, lanes.index()).to[SInt[64]] >= 32, "wmma stride")
        |  machine.require((lanes.peer(v0, lanes.index()) - 0x10000000 <= 0x2fffffff || lanes.peer(v0, lanes.index()) - 0x4000000000000000L <= 0xffffffffL) && cluster.rankOf(lanes.peer(v0, lanes.index())).to[SInt[64]] < cluster.clusterSize().to[SInt[64]] && (lanes.peer(v0, lanes.index()) + (((UInt[64](7) * lanes.peer(v1, lanes.index()) + 32) * 4 + 7 >> 3) - 1) - 0x10000000 <= 0x2fffffff || lanes.peer(v0, lanes.index()) + (((UInt[64](7) * lanes.peer(v1, lanes.index()) + 32) * 4 + 7 >> 3) - 1) - 0x4000000000000000L <= 0xffffffffL) && cluster.rankOf(lanes.peer(v0, lanes.index()) + (((UInt[64](7) * lanes.peer(v1, lanes.index()) + 32) * 4 + 7 >> 3) - 1)).to[SInt[64]] < cluster.clusterSize().to[SInt[64]], "wmma window")
        |  val v2 = machine.load[UInt[8]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x800000020L), lanes.index(), UInt[64](0)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x800000020L), lanes.index(), UInt[64](0))) * 4 >> 3))
        |  val v3 = machine.load[UInt[8]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x800000020L), lanes.index(), UInt[64](1)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x800000020L), lanes.index(), UInt[64](1))) * 4 >> 3))
        |  val v4 = machine.load[UInt[8]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x800000020L), lanes.index(), UInt[64](2)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x800000020L), lanes.index(), UInt[64](2))) * 4 >> 3))
        |  val v5 = machine.load[UInt[8]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x800000020L), lanes.index(), UInt[64](3)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x800000020L), lanes.index(), UInt[64](3))) * 4 >> 3))
        |  val v6 = machine.load[UInt[8]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x800000020L), lanes.index(), UInt[64](4)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x800000020L), lanes.index(), UInt[64](4))) * 4 >> 3))
        |  val v7 = machine.load[UInt[8]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x800000020L), lanes.index(), UInt[64](5)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x800000020L), lanes.index(), UInt[64](5))) * 4 >> 3))
        |  val v8 = machine.load[UInt[8]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x800000020L), lanes.index(), UInt[64](6)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x800000020L), lanes.index(), UInt[64](6))) * 4 >> 3))
        |  val v9 = machine.load[UInt[8]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x800000020L), lanes.index(), UInt[64](7)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x800000020L), lanes.index(), UInt[64](7))) * 4 >> 3))
        |  val v10 = (v2 >> ((MatrixLayout.fragmentRow(UInt[64](0x800000020L), lanes.index(), UInt[64](0)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x800000020L), lanes.index(), UInt[64](0))) * 4).to[UInt[3]]).to[UInt[4]].to[UInt[64]] | ((v3 >> ((MatrixLayout.fragmentRow(UInt[64](0x800000020L), lanes.index(), UInt[64](1)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x800000020L), lanes.index(), UInt[64](1))) * 4).to[UInt[3]]).to[UInt[4]].to[UInt[64]] << 4) | ((v4 >> ((MatrixLayout.fragmentRow(UInt[64](0x800000020L), lanes.index(), UInt[64](2)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x800000020L), lanes.index(), UInt[64](2))) * 4).to[UInt[3]]).to[UInt[4]].to[UInt[64]] << 8) | ((v5 >> ((MatrixLayout.fragmentRow(UInt[64](0x800000020L), lanes.index(), UInt[64](3)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x800000020L), lanes.index(), UInt[64](3))) * 4).to[UInt[3]]).to[UInt[4]].to[UInt[64]] << 12) | ((v6 >> ((MatrixLayout.fragmentRow(UInt[64](0x800000020L), lanes.index(), UInt[64](4)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x800000020L), lanes.index(), UInt[64](4))) * 4).to[UInt[3]]).to[UInt[4]].to[UInt[64]] << 16) | ((v7 >> ((MatrixLayout.fragmentRow(UInt[64](0x800000020L), lanes.index(), UInt[64](5)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x800000020L), lanes.index(), UInt[64](5))) * 4).to[UInt[3]]).to[UInt[4]].to[UInt[64]] << 20) | ((v8 >> ((MatrixLayout.fragmentRow(UInt[64](0x800000020L), lanes.index(), UInt[64](6)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x800000020L), lanes.index(), UInt[64](6))) * 4).to[UInt[3]]).to[UInt[4]].to[UInt[64]] << 24) | ((v9 >> ((MatrixLayout.fragmentRow(UInt[64](0x800000020L), lanes.index(), UInt[64](7)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x800000020L), lanes.index(), UInt[64](7))) * 4).to[UInt[3]]).to[UInt[4]].to[UInt[64]] << 28)
        |  (f0 = v10)
        |}""".stripMargin
    )
  }

  test("a column-major fragment load renders its elements at the leading stride") {
    assertEquals(
      shown(loadFragment(
        Matrix.Fragment(Matrix.Shape(16, 16, 8), Matrix.Role.B, Matrix.Element.TF32, wmma = true),
        columnMajor = true
      )),
      """def wmma_load(row: UInt[64], size: UInt[64])(using machine: Machine, lanes: Lanes, units: Units, cluster: Cluster): (f0: UInt[64], f1: UInt[64], f2: UInt[64], f3: UInt[64]) = {
        |  machine.require(lanes.converged(), "warp convergence")
        |  val v0 = lanes.gather(row)
        |  val v1 = lanes.gather(size)
        |  machine.require(units.agreed(v0), "uniform wmma address")
        |  machine.require(units.agreed(v1), "uniform wmma stride")
        |  machine.require(lanes.peer(v0, lanes.index()).to[UInt[5]] == UInt[5](0) && ((if ((lanes.peer(v1, lanes.index()) * 32).to[SInt[64]] < 0) UInt[64](0) - lanes.peer(v1, lanes.index()) * 32 else lanes.peer(v1, lanes.index()) * 32) >> 3).to[UInt[4]] == UInt[4](0), "wmma alignment")
        |  machine.require(lanes.peer(v1, lanes.index()).to[SInt[64]] >= 8, "wmma stride")
        |  machine.require((lanes.peer(v0, lanes.index()) - 0x10000000 <= 0x2fffffff || lanes.peer(v0, lanes.index()) - 0x4000000000000000L <= 0xffffffffL) && cluster.rankOf(lanes.peer(v0, lanes.index())).to[SInt[64]] < cluster.clusterSize().to[SInt[64]] && (lanes.peer(v0, lanes.index()) + (((UInt[64](15) * lanes.peer(v1, lanes.index()) + 8) * 32 + 7 >> 3) - 1) - 0x10000000 <= 0x2fffffff || lanes.peer(v0, lanes.index()) + (((UInt[64](15) * lanes.peer(v1, lanes.index()) + 8) * 32 + 7 >> 3) - 1) - 0x4000000000000000L <= 0xffffffffL) && cluster.rankOf(lanes.peer(v0, lanes.index()) + (((UInt[64](15) * lanes.peer(v1, lanes.index()) + 8) * 32 + 7 >> 3) - 1)).to[SInt[64]] < cluster.clusterSize().to[SInt[64]], "wmma window")
        |  val v2 = machine.load[UInt[32]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentColumn(UInt[64](0x800000010L), lanes.index(), UInt[64](0)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentRow(UInt[64](0x800000010L), lanes.index(), UInt[64](0))) * 32 >> 3))
        |  val v3 = machine.load[UInt[32]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentColumn(UInt[64](0x800000010L), lanes.index(), UInt[64](1)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentRow(UInt[64](0x800000010L), lanes.index(), UInt[64](1))) * 32 >> 3))
        |  val v4 = machine.load[UInt[32]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentColumn(UInt[64](0x800000010L), lanes.index(), UInt[64](2)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentRow(UInt[64](0x800000010L), lanes.index(), UInt[64](2))) * 32 >> 3))
        |  val v5 = machine.load[UInt[32]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentColumn(UInt[64](0x800000010L), lanes.index(), UInt[64](3)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentRow(UInt[64](0x800000010L), lanes.index(), UInt[64](3))) * 32 >> 3))
        |  (
        |    f0 = v2.to[UInt[64]],
        |    f1 = v3.to[UInt[64]],
        |    f2 = v4.to[UInt[64]],
        |    f3 = v5.to[UInt[64]]
        |  )
        |}""".stripMargin
    )
  }

  test("an accumulator load renders one 32-bit element per register") {
    assertEquals(
      shown(loadFragment(Matrix.Fragment(Matrix.Shape(16, 16, 16), Matrix.Role.C, Matrix.Element.F32, wmma = true))),
      """def wmma_load(row: UInt[64], size: UInt[64])(using machine: Machine, lanes: Lanes, units: Units, cluster: Cluster): (f0: UInt[64], f1: UInt[64], f2: UInt[64], f3: UInt[64], f4: UInt[64], f5: UInt[64], f6: UInt[64], f7: UInt[64]) = {
        |  machine.require(lanes.converged(), "warp convergence")
        |  val v0 = lanes.gather(row)
        |  val v1 = lanes.gather(size)
        |  machine.require(units.agreed(v0), "uniform wmma address")
        |  machine.require(units.agreed(v1), "uniform wmma stride")
        |  machine.require(lanes.peer(v0, lanes.index()).to[UInt[5]] == UInt[5](0) && ((if ((lanes.peer(v1, lanes.index()) * 32).to[SInt[64]] < 0) UInt[64](0) - lanes.peer(v1, lanes.index()) * 32 else lanes.peer(v1, lanes.index()) * 32) >> 3).to[UInt[4]] == UInt[4](0), "wmma alignment")
        |  machine.require(lanes.peer(v1, lanes.index()).to[SInt[64]] >= 16, "wmma stride")
        |  machine.require((lanes.peer(v0, lanes.index()) - 0x10000000 <= 0x2fffffff || lanes.peer(v0, lanes.index()) - 0x4000000000000000L <= 0xffffffffL) && cluster.rankOf(lanes.peer(v0, lanes.index())).to[SInt[64]] < cluster.clusterSize().to[SInt[64]] && (lanes.peer(v0, lanes.index()) + (((UInt[64](15) * lanes.peer(v1, lanes.index()) + 16) * 32 + 7 >> 3) - 1) - 0x10000000 <= 0x2fffffff || lanes.peer(v0, lanes.index()) + (((UInt[64](15) * lanes.peer(v1, lanes.index()) + 16) * 32 + 7 >> 3) - 1) - 0x4000000000000000L <= 0xffffffffL) && cluster.rankOf(lanes.peer(v0, lanes.index()) + (((UInt[64](15) * lanes.peer(v1, lanes.index()) + 16) * 32 + 7 >> 3) - 1)).to[SInt[64]] < cluster.clusterSize().to[SInt[64]], "wmma window")
        |  val v2 = machine.load[UInt[32]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x1000000010L), lanes.index(), UInt[64](0)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x1000000010L), lanes.index(), UInt[64](0))) * 32 >> 3))
        |  val v3 = machine.load[UInt[32]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x1000000010L), lanes.index(), UInt[64](1)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x1000000010L), lanes.index(), UInt[64](1))) * 32 >> 3))
        |  val v4 = machine.load[UInt[32]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x1000000010L), lanes.index(), UInt[64](2)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x1000000010L), lanes.index(), UInt[64](2))) * 32 >> 3))
        |  val v5 = machine.load[UInt[32]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x1000000010L), lanes.index(), UInt[64](3)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x1000000010L), lanes.index(), UInt[64](3))) * 32 >> 3))
        |  val v6 = machine.load[UInt[32]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x1000000010L), lanes.index(), UInt[64](4)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x1000000010L), lanes.index(), UInt[64](4))) * 32 >> 3))
        |  val v7 = machine.load[UInt[32]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x1000000010L), lanes.index(), UInt[64](5)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x1000000010L), lanes.index(), UInt[64](5))) * 32 >> 3))
        |  val v8 = machine.load[UInt[32]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x1000000010L), lanes.index(), UInt[64](6)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x1000000010L), lanes.index(), UInt[64](6))) * 32 >> 3))
        |  val v9 = machine.load[UInt[32]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x1000000010L), lanes.index(), UInt[64](7)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x1000000010L), lanes.index(), UInt[64](7))) * 32 >> 3))
        |  (
        |    f0 = v2.to[UInt[64]],
        |    f1 = v3.to[UInt[64]],
        |    f2 = v4.to[UInt[64]],
        |    f3 = v5.to[UInt[64]],
        |    f4 = v6.to[UInt[64]],
        |    f5 = v7.to[UInt[64]],
        |    f6 = v8.to[UInt[64]],
        |    f7 = v9.to[UInt[64]]
        |  )
        |}""".stripMargin
    )
  }

  test("an accumulator store renders one 32-bit element per register") {
    assertEquals(
      shown(storeFragment(Matrix.Fragment(Matrix.Shape(16, 16, 16), Matrix.Role.C, Matrix.Element.F32, wmma = true))),
      """def wmma_store(row: UInt[64], size: UInt[64], s0: UInt[64], s1: UInt[64], s2: UInt[64], s3: UInt[64], s4: UInt[64], s5: UInt[64], s6: UInt[64], s7: UInt[64])(using machine: Machine, lanes: Lanes, units: Units, cluster: Cluster): Unit = {
        |  machine.require(lanes.converged(), "warp convergence")
        |  val v0 = lanes.gather(row)
        |  val v1 = lanes.gather(size)
        |  machine.require(units.agreed(v0), "uniform wmma address")
        |  machine.require(units.agreed(v1), "uniform wmma stride")
        |  machine.require(lanes.peer(v0, lanes.index()).to[UInt[5]] == UInt[5](0) && ((if ((lanes.peer(v1, lanes.index()) * 32).to[SInt[64]] < 0) UInt[64](0) - lanes.peer(v1, lanes.index()) * 32 else lanes.peer(v1, lanes.index()) * 32) >> 3).to[UInt[4]] == UInt[4](0), "wmma alignment")
        |  machine.require(lanes.peer(v1, lanes.index()).to[SInt[64]] >= 16, "wmma stride")
        |  machine.require((lanes.peer(v0, lanes.index()) - 0x10000000 <= 0x2fffffff || lanes.peer(v0, lanes.index()) - 0x4000000000000000L <= 0xffffffffL) && cluster.rankOf(lanes.peer(v0, lanes.index())).to[SInt[64]] < cluster.clusterSize().to[SInt[64]] && (lanes.peer(v0, lanes.index()) + (((UInt[64](15) * lanes.peer(v1, lanes.index()) + 16) * 32 + 7 >> 3) - 1) - 0x10000000 <= 0x2fffffff || lanes.peer(v0, lanes.index()) + (((UInt[64](15) * lanes.peer(v1, lanes.index()) + 16) * 32 + 7 >> 3) - 1) - 0x4000000000000000L <= 0xffffffffL) && cluster.rankOf(lanes.peer(v0, lanes.index()) + (((UInt[64](15) * lanes.peer(v1, lanes.index()) + 16) * 32 + 7 >> 3) - 1)).to[SInt[64]] < cluster.clusterSize().to[SInt[64]], "wmma window")
        |  machine.store[UInt[32]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x1000000010L), lanes.index(), UInt[64](0)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x1000000010L), lanes.index(), UInt[64](0))) * 32 >> 3), s0.to[UInt[32]])
        |  machine.store[UInt[32]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x1000000010L), lanes.index(), UInt[64](1)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x1000000010L), lanes.index(), UInt[64](1))) * 32 >> 3), s1.to[UInt[32]])
        |  machine.store[UInt[32]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x1000000010L), lanes.index(), UInt[64](2)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x1000000010L), lanes.index(), UInt[64](2))) * 32 >> 3), s2.to[UInt[32]])
        |  machine.store[UInt[32]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x1000000010L), lanes.index(), UInt[64](3)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x1000000010L), lanes.index(), UInt[64](3))) * 32 >> 3), s3.to[UInt[32]])
        |  machine.store[UInt[32]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x1000000010L), lanes.index(), UInt[64](4)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x1000000010L), lanes.index(), UInt[64](4))) * 32 >> 3), s4.to[UInt[32]])
        |  machine.store[UInt[32]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x1000000010L), lanes.index(), UInt[64](5)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x1000000010L), lanes.index(), UInt[64](5))) * 32 >> 3), s5.to[UInt[32]])
        |  machine.store[UInt[32]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x1000000010L), lanes.index(), UInt[64](6)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x1000000010L), lanes.index(), UInt[64](6))) * 32 >> 3), s6.to[UInt[32]])
        |  machine.store[UInt[32]](Space.Shared, lanes.peer(v0, lanes.index()) + ((MatrixLayout.fragmentRow(UInt[64](0x1000000010L), lanes.index(), UInt[64](7)) * lanes.peer(v1, lanes.index()) + MatrixLayout.fragmentColumn(UInt[64](0x1000000010L), lanes.index(), UInt[64](7))) * 32 >> 3), s7.to[UInt[32]])
        |  ()
        |}""".stripMargin
    )
  }

  test("a guarded tensor memory allocation renders its checks and stores the first column from the first lane") {
    assertEquals(
      shown(tensorMemoryAllocate(guarded = true)),
      """def `tcgen05.alloc`(p: Bool, dst: UInt[64], size: UInt[64])(using machine: Machine, lanes: Lanes, cluster: Cluster): Unit = {
        |  machine.require(lanes.converged(), "warp convergence")
        |  if (p) {
        |    if (lanes.index() == UInt[64](0)) {
        |      val v0 = cluster.at(cluster.ctaRank())
        |      machine.require(cluster.tensorMemoryPermitted(v0), "tcgen05.alloc permit")
        |      machine.require(TensorMemoryLayout.granted(size) != UInt[64](0), "tcgen05.alloc columns")
        |      val v1 = cluster.allocateTensorMemory(v0, TensorMemoryLayout.granted(size))
        |      machine.require(v1 < 512, "tcgen05.alloc space")
        |      cluster.storeShared[UInt[32]](dst, v1.to[UInt[32]])
        |    }
        |  }
        |  ()
        |}""".stripMargin
    )
  }

  test("a tensor memory deallocation renders its range check and frees the granules") {
    assertEquals(
      shown(tensorMemoryDeallocate()),
      """def `tcgen05.dealloc`(src: UInt[64], size: UInt[64])(using machine: Machine, lanes: Lanes, cluster: Cluster): Unit = {
        |  machine.require(lanes.converged(), "warp convergence")
        |  if (lanes.index() == UInt[64](0)) {
        |    machine.require(TensorMemoryLayout.freed(src, size) != UInt[64](0), "tcgen05.dealloc range")
        |    val v0 = cluster.at(cluster.ctaRank())
        |    val v1 = cluster.freeTensorMemory(v0, TensorMemoryLayout.freed(src, size))
        |    machine.require(v1, "tcgen05.dealloc allocated")
        |  }
        |  ()
        |}""".stripMargin
    )
  }

  test("a guarded relinquish of the allocation permit renders its request under the guard") {
    assertEquals(
      shown(tensorMemoryRelinquish(guarded = true)),
      """def `tcgen05.relinquish_alloc_permit`(p: Bool)(using cluster: Cluster): Unit = {
        |  val v0 = cluster.at(cluster.ctaRank())
        |  if (p) cluster.relinquishTensorMemory(v0) else ()
        |  ()
        |}""".stripMargin
    )
  }

  test("a tensor memory load renders the cell checks and a register per cell") {
    assertEquals(
      shown(tensorMemoryLoad(
        Tcgen05.Movement(Tcgen05.DataShape.Lanes32Bits32, registers = 2, split = 0, packed = false),
        2
      )),
      """def `tcgen05.ld`(src: UInt[64])(using machine: Machine, lanes: Lanes, cluster: Cluster, units: Units): (d0: UInt[64], d1: UInt[64]) = {
        |  machine.require(lanes.converged(), "warp convergence")
        |  val v0 = lanes.gather(src)
        |  val v1 = cluster.at(cluster.ctaRank())
        |  machine.require((TensorMemoryLayout.cellLane(UInt[64](0), lanes.peer(v0, UInt[64](0)), lanes.index()) >> 5) == units.unitIndex().to[UInt[2]].to[UInt[64]], "tensor memory lane")
        |  machine.require(TensorMemoryLayout.cellColumn(UInt[64](0), lanes.peer(v0, UInt[64](0)), lanes.index()) < 512, "tensor memory column")
        |  val v2 = cluster.tensorMemoryCell(v1, TensorMemoryLayout.cellLane(UInt[64](0), lanes.peer(v0, UInt[64](0)), lanes.index()), TensorMemoryLayout.cellColumn(UInt[64](0), lanes.peer(v0, UInt[64](0)), lanes.index()))
        |  machine.require((TensorMemoryLayout.cellLane(UInt[64](256), lanes.peer(v0, UInt[64](0)), lanes.index()) >> 5) == units.unitIndex().to[UInt[2]].to[UInt[64]], "tensor memory lane")
        |  machine.require(TensorMemoryLayout.cellColumn(UInt[64](256), lanes.peer(v0, UInt[64](0)), lanes.index()) < 512, "tensor memory column")
        |  val v3 = cluster.tensorMemoryCell(v1, TensorMemoryLayout.cellLane(UInt[64](256), lanes.peer(v0, UInt[64](0)), lanes.index()), TensorMemoryLayout.cellColumn(UInt[64](256), lanes.peer(v0, UInt[64](0)), lanes.index()))
        |  (
        |    d0 = v2,
        |    d1 = v3
        |  )
        |}""".stripMargin
    )
  }

  test("a tensor memory load with a reduction renders the combination of its registers") {
    assertEquals(
      shown(tensorMemoryLoad(
        Tcgen05.Movement(Tcgen05.DataShape.Lanes32Bits32, registers = 2, split = 0, packed = false),
        2,
        Some(Tcgen05.Reduction(
          Register(14),
          maximum = true,
          Tcgen05.ReductionKind.Float(absolute = false, propagate = true)
        ))
      )),
      """def `tcgen05.ld`(src: UInt[64])(using machine: Machine, lanes: Lanes, cluster: Cluster, units: Units): (d0: UInt[64], d1: UInt[64], s0: UInt[64]) = {
        |  machine.require(lanes.converged(), "warp convergence")
        |  val v0 = lanes.gather(src)
        |  val v1 = cluster.at(cluster.ctaRank())
        |  machine.require((TensorMemoryLayout.cellLane(UInt[64](0), lanes.peer(v0, UInt[64](0)), lanes.index()) >> 5) == units.unitIndex().to[UInt[2]].to[UInt[64]], "tensor memory lane")
        |  machine.require(TensorMemoryLayout.cellColumn(UInt[64](0), lanes.peer(v0, UInt[64](0)), lanes.index()) < 512, "tensor memory column")
        |  val v2 = cluster.tensorMemoryCell(v1, TensorMemoryLayout.cellLane(UInt[64](0), lanes.peer(v0, UInt[64](0)), lanes.index()), TensorMemoryLayout.cellColumn(UInt[64](0), lanes.peer(v0, UInt[64](0)), lanes.index()))
        |  machine.require((TensorMemoryLayout.cellLane(UInt[64](256), lanes.peer(v0, UInt[64](0)), lanes.index()) >> 5) == units.unitIndex().to[UInt[2]].to[UInt[64]], "tensor memory lane")
        |  machine.require(TensorMemoryLayout.cellColumn(UInt[64](256), lanes.peer(v0, UInt[64](0)), lanes.index()) < 512, "tensor memory column")
        |  val v3 = cluster.tensorMemoryCell(v1, TensorMemoryLayout.cellLane(UInt[64](256), lanes.peer(v0, UInt[64](0)), lanes.index()), TensorMemoryLayout.cellColumn(UInt[64](256), lanes.peer(v0, UInt[64](0)), lanes.index()))
        |  val v4 = TensorMemoryLayout.combined(UInt[64](22), cluster.tensorMemoryCell(v1, TensorMemoryLayout.cellLane(UInt[64](0), lanes.peer(v0, UInt[64](0)), lanes.index()), TensorMemoryLayout.cellColumn(UInt[64](0), lanes.peer(v0, UInt[64](0)), lanes.index())), cluster.tensorMemoryCell(v1, TensorMemoryLayout.cellLane(UInt[64](256), lanes.peer(v0, UInt[64](0)), lanes.index()), TensorMemoryLayout.cellColumn(UInt[64](256), lanes.peer(v0, UInt[64](0)), lanes.index())))
        |  (
        |    d0 = v2,
        |    d1 = v3,
        |    s0 = v4
        |  )
        |}""".stripMargin
    )
  }

  test("a packed tensor memory load joins two 16-bit columns into each register") {
    assertEquals(
      shown(tensorMemoryLoad(
        Tcgen05.Movement(Tcgen05.DataShape.Lanes16Bits64, registers = 2, split = 0, packed = true),
        2,
        Some(Tcgen05.Reduction(Register(14), maximum = false, Tcgen05.ReductionKind.Signed))
      )),
      """def `tcgen05.ld`(src: UInt[64])(using machine: Machine, lanes: Lanes, cluster: Cluster, units: Units): (d0: UInt[64], d1: UInt[64], s0: UInt[64]) = {
        |  machine.require(lanes.converged(), "warp convergence")
        |  val v0 = lanes.gather(src)
        |  val v1 = cluster.at(cluster.ctaRank())
        |  machine.require((TensorMemoryLayout.cellLane(UInt[64](9), lanes.peer(v0, UInt[64](0)), lanes.index()) >> 5) == units.unitIndex().to[UInt[2]].to[UInt[64]], "tensor memory lane")
        |  machine.require(TensorMemoryLayout.cellColumn(UInt[64](9), lanes.peer(v0, UInt[64](0)), lanes.index()) < 512, "tensor memory column")
        |  machine.require(TensorMemoryLayout.cellColumn(UInt[64](25), lanes.peer(v0, UInt[64](0)), lanes.index()) < 512, "tensor memory column")
        |  val v2 = cluster.tensorMemoryCell(v1, TensorMemoryLayout.cellLane(UInt[64](9), lanes.peer(v0, UInt[64](0)), lanes.index()), TensorMemoryLayout.cellColumn(UInt[64](9), lanes.peer(v0, UInt[64](0)), lanes.index())).to[UInt[16]].to[UInt[64]] | (cluster.tensorMemoryCell(v1, TensorMemoryLayout.cellLane(UInt[64](9), lanes.peer(v0, UInt[64](0)), lanes.index()), TensorMemoryLayout.cellColumn(UInt[64](25), lanes.peer(v0, UInt[64](0)), lanes.index())).to[UInt[16]].to[UInt[64]] << 16)
        |  machine.require((TensorMemoryLayout.cellLane(UInt[64](265), lanes.peer(v0, UInt[64](0)), lanes.index()) >> 5) == units.unitIndex().to[UInt[2]].to[UInt[64]], "tensor memory lane")
        |  machine.require(TensorMemoryLayout.cellColumn(UInt[64](265), lanes.peer(v0, UInt[64](0)), lanes.index()) < 512, "tensor memory column")
        |  machine.require(TensorMemoryLayout.cellColumn(UInt[64](281), lanes.peer(v0, UInt[64](0)), lanes.index()) < 512, "tensor memory column")
        |  val v3 = cluster.tensorMemoryCell(v1, TensorMemoryLayout.cellLane(UInt[64](265), lanes.peer(v0, UInt[64](0)), lanes.index()), TensorMemoryLayout.cellColumn(UInt[64](265), lanes.peer(v0, UInt[64](0)), lanes.index())).to[UInt[16]].to[UInt[64]] | (cluster.tensorMemoryCell(v1, TensorMemoryLayout.cellLane(UInt[64](265), lanes.peer(v0, UInt[64](0)), lanes.index()), TensorMemoryLayout.cellColumn(UInt[64](281), lanes.peer(v0, UInt[64](0)), lanes.index())).to[UInt[16]].to[UInt[64]] << 16)
        |  val v4 = TensorMemoryLayout.combined(UInt[64](1), (cluster.tensorMemoryCell(v1, TensorMemoryLayout.cellLane(UInt[64](9), lanes.peer(v0, UInt[64](0)), lanes.index()), TensorMemoryLayout.cellColumn(UInt[64](9), lanes.peer(v0, UInt[64](0)), lanes.index())).to[UInt[16]].to[UInt[64]] | (cluster.tensorMemoryCell(v1, TensorMemoryLayout.cellLane(UInt[64](9), lanes.peer(v0, UInt[64](0)), lanes.index()), TensorMemoryLayout.cellColumn(UInt[64](25), lanes.peer(v0, UInt[64](0)), lanes.index())).to[UInt[16]].to[UInt[64]] << 16)).to[UInt[64]], (cluster.tensorMemoryCell(v1, TensorMemoryLayout.cellLane(UInt[64](265), lanes.peer(v0, UInt[64](0)), lanes.index()), TensorMemoryLayout.cellColumn(UInt[64](265), lanes.peer(v0, UInt[64](0)), lanes.index())).to[UInt[16]].to[UInt[64]] | (cluster.tensorMemoryCell(v1, TensorMemoryLayout.cellLane(UInt[64](265), lanes.peer(v0, UInt[64](0)), lanes.index()), TensorMemoryLayout.cellColumn(UInt[64](281), lanes.peer(v0, UInt[64](0)), lanes.index())).to[UInt[16]].to[UInt[64]] << 16)).to[UInt[64]])
        |  (
        |    d0 = v2,
        |    d1 = v3,
        |    s0 = v4
        |  )
        |}""".stripMargin
    )
  }

  test("a tensor memory load of half the threads renders its checks and writes under the threads it involves") {
    assertEquals(
      shown(tensorMemoryLoad(
        Tcgen05.Movement(
          Tcgen05.DataShape.Lanes16Bits32x2,
          registers = 1,
          split = 16,
          packed = false,
          threads = Tcgen05.Threads.Low
        ),
        1
      )),
      """def `tcgen05.ld`(src: UInt[64], d0: UInt[64])(using machine: Machine, lanes: Lanes, cluster: Cluster, units: Units): (d0: UInt[64]) = {
        |  machine.require(lanes.converged(), "warp convergence")
        |  val v0 = lanes.gather(src)
        |  val v1 = cluster.at(cluster.ctaRank())
        |  if (TensorMemoryLayout.included(UInt[64](0x1000000024L), lanes.index()) != UInt[64](0)) {
        |    machine.require((TensorMemoryLayout.cellLane(UInt[64](0x1000000024L), lanes.peer(v0, UInt[64](0)), lanes.index()) >> 5) == units.unitIndex().to[UInt[2]].to[UInt[64]], "tensor memory lane")
        |    machine.require(TensorMemoryLayout.cellColumn(UInt[64](0x1000000024L), lanes.peer(v0, UInt[64](0)), lanes.index()) < 512, "tensor memory column")
        |  }
        |  val v2 = lanes.index()
        |  val v3 = lanes.peer(v0, UInt[64](0))
        |  (d0 = if (TensorMemoryLayout.included(UInt[64](0x1000000024L), v2) != UInt[64](0)) cluster.tensorMemoryCell(v1, TensorMemoryLayout.cellLane(UInt[64](0x1000000024L), v3, v2), TensorMemoryLayout.cellColumn(UInt[64](0x1000000024L), v3, v2)) else d0)
        |}""".stripMargin
    )
  }

  test("a tensor memory store renders the cell checks and a store per register") {
    assertEquals(
      shown(tensorMemoryStore(
        Tcgen05.Movement(Tcgen05.DataShape.Lanes32Bits32, registers = 2, split = 0, packed = false),
        2
      )),
      """def `tcgen05.st`(src: UInt[64], d0: UInt[64], d1: UInt[64])(using machine: Machine, lanes: Lanes, cluster: Cluster, units: Units): Unit = {
        |  machine.require(lanes.converged(), "warp convergence")
        |  val v0 = lanes.gather(src)
        |  val v1 = cluster.at(cluster.ctaRank())
        |  machine.require((TensorMemoryLayout.cellLane(UInt[64](0), lanes.peer(v0, UInt[64](0)), lanes.index()) >> 5) == units.unitIndex().to[UInt[2]].to[UInt[64]], "tensor memory lane")
        |  machine.require(TensorMemoryLayout.cellColumn(UInt[64](0), lanes.peer(v0, UInt[64](0)), lanes.index()) < 512, "tensor memory column")
        |  cluster.storeTensorMemory(v1, TensorMemoryLayout.cellLane(UInt[64](0), lanes.peer(v0, UInt[64](0)), lanes.index()), TensorMemoryLayout.cellColumn(UInt[64](0), lanes.peer(v0, UInt[64](0)), lanes.index()), d0)
        |  machine.require((TensorMemoryLayout.cellLane(UInt[64](256), lanes.peer(v0, UInt[64](0)), lanes.index()) >> 5) == units.unitIndex().to[UInt[2]].to[UInt[64]], "tensor memory lane")
        |  machine.require(TensorMemoryLayout.cellColumn(UInt[64](256), lanes.peer(v0, UInt[64](0)), lanes.index()) < 512, "tensor memory column")
        |  cluster.storeTensorMemory(v1, TensorMemoryLayout.cellLane(UInt[64](256), lanes.peer(v0, UInt[64](0)), lanes.index()), TensorMemoryLayout.cellColumn(UInt[64](256), lanes.peer(v0, UInt[64](0)), lanes.index()), d1)
        |  ()
        |}""".stripMargin
    )
  }

  test("a guarded packed tensor memory store splits each register into two 16-bit columns") {
    assertEquals(
      shown(tensorMemoryStore(
        Tcgen05.Movement(Tcgen05.DataShape.Lanes16Bits64, registers = 2, split = 0, packed = true),
        2,
        guarded = true
      )),
      """def `tcgen05.st`(p: Bool, src: UInt[64], d0: UInt[64], d1: UInt[64])(using machine: Machine, lanes: Lanes, cluster: Cluster, units: Units): Unit = {
        |  machine.require(lanes.converged(), "warp convergence")
        |  val v0 = lanes.gather(src)
        |  if (p) {
        |    val v1 = cluster.at(cluster.ctaRank())
        |    machine.require((TensorMemoryLayout.cellLane(UInt[64](9), lanes.peer(v0, UInt[64](0)), lanes.index()) >> 5) == units.unitIndex().to[UInt[2]].to[UInt[64]], "tensor memory lane")
        |    machine.require(TensorMemoryLayout.cellColumn(UInt[64](9), lanes.peer(v0, UInt[64](0)), lanes.index()) < 512, "tensor memory column")
        |    machine.require(TensorMemoryLayout.cellColumn(UInt[64](25), lanes.peer(v0, UInt[64](0)), lanes.index()) < 512, "tensor memory column")
        |    cluster.storeTensorMemory(v1, TensorMemoryLayout.cellLane(UInt[64](9), lanes.peer(v0, UInt[64](0)), lanes.index()), TensorMemoryLayout.cellColumn(UInt[64](9), lanes.peer(v0, UInt[64](0)), lanes.index()), d0.to[UInt[16]].to[UInt[64]])
        |    cluster.storeTensorMemory(v1, TensorMemoryLayout.cellLane(UInt[64](9), lanes.peer(v0, UInt[64](0)), lanes.index()), TensorMemoryLayout.cellColumn(UInt[64](25), lanes.peer(v0, UInt[64](0)), lanes.index()), (d0 >> 16).to[UInt[16]].to[UInt[64]])
        |    machine.require((TensorMemoryLayout.cellLane(UInt[64](265), lanes.peer(v0, UInt[64](0)), lanes.index()) >> 5) == units.unitIndex().to[UInt[2]].to[UInt[64]], "tensor memory lane")
        |    machine.require(TensorMemoryLayout.cellColumn(UInt[64](265), lanes.peer(v0, UInt[64](0)), lanes.index()) < 512, "tensor memory column")
        |    machine.require(TensorMemoryLayout.cellColumn(UInt[64](281), lanes.peer(v0, UInt[64](0)), lanes.index()) < 512, "tensor memory column")
        |    cluster.storeTensorMemory(v1, TensorMemoryLayout.cellLane(UInt[64](265), lanes.peer(v0, UInt[64](0)), lanes.index()), TensorMemoryLayout.cellColumn(UInt[64](265), lanes.peer(v0, UInt[64](0)), lanes.index()), d1.to[UInt[16]].to[UInt[64]])
        |    cluster.storeTensorMemory(v1, TensorMemoryLayout.cellLane(UInt[64](265), lanes.peer(v0, UInt[64](0)), lanes.index()), TensorMemoryLayout.cellColumn(UInt[64](281), lanes.peer(v0, UInt[64](0)), lanes.index()), (d1 >> 16).to[UInt[16]].to[UInt[64]])
        |  }
        |  ()
        |}""".stripMargin
    )
  }

  test("a tensor memory copy renders its descriptor and range checks and a loop over its chunks") {
    assertEquals(
      shown(tensorMemoryCopy(Tcgen05.Copy(
        Tcgen05.CopyShape.Rows128Bits256,
        Tcgen05.Replication.Once,
        Matrix.Packing.Bytes,
        pair = false
      ))),
      """def `tcgen05.cp`(dst: UInt[64], src: UInt[64])(using machine: Machine, tensors: Tensors, cluster: Cluster): Unit = {
        |  machine.require(TensorMemoryLayout.decodable(src) != UInt[64](0), "tcgen05.cp descriptor")
        |  machine.require((TensorMemoryLayout.addressLane(dst) + 127).to[SInt[64]] < 128 && (TensorMemoryLayout.addressColumn(dst) + 8).to[SInt[64]] <= 512, "tcgen05.cp range")
        |  for (run <- tensors.indices(UInt[64](256))) {
        |    val v0 = cluster.at(cluster.ctaRank())
        |    val v1 = cluster.loadChunk(v0, TensorMemoryLayout.major(src, run / 2, run % 2), VMIR.Matrix.Packing.Bytes)
        |    cluster.storeTensorWords(v0, TensorMemoryLayout.addressLane(dst) + TensorMemoryLayout.replicated(UInt[64](0), run / 2, UInt[64](0)), TensorMemoryLayout.addressColumn(dst) + (run % 2) * 4, v1)
        |  }
        |  ()
        |}""".stripMargin
    )
  }

  test("a guarded tensor memory copy to a CTA pair checks both CTAs and replicates each row") {
    assertEquals(
      shown(tensorMemoryCopy(
        Tcgen05.Copy(
          Tcgen05.CopyShape.Rows64Bits128,
          Tcgen05.Replication.PairsOfAlternate,
          Matrix.Packing.Six,
          pair = true
        ),
        guarded = true
      )),
      """def `tcgen05.cp`(p: Bool, dst: UInt[64], src: UInt[64])(using machine: Machine, cluster: Cluster, tensors: Tensors): Unit = {
        |  if (p) {
        |    machine.require(TensorMemoryLayout.decodable(src) != UInt[64](0), "tcgen05.cp descriptor")
        |    machine.require(cluster.present(cluster.ctaRank() - cluster.ctaRank().to[UInt[1]].to[UInt[64]] + 0), "cta pair")
        |    machine.require(cluster.present(cluster.ctaRank() - cluster.ctaRank().to[UInt[1]].to[UInt[64]] + 1), "cta pair")
        |    machine.require((TensorMemoryLayout.addressLane(dst) + 127).to[SInt[64]] < 128 && (TensorMemoryLayout.addressColumn(dst) + 4).to[SInt[64]] <= 512, "tcgen05.cp range")
        |    for (run <- tensors.indices(UInt[64](128))) {
        |      val v0 = cluster.at(cluster.ctaRank() - cluster.ctaRank().to[UInt[1]].to[UInt[64]] + run / 64)
        |      val v1 = cluster.loadChunk(v0, TensorMemoryLayout.major(src, run % 64, UInt[64](0)), VMIR.Matrix.Packing.Six)
        |      cluster.storeTensorWords(v0, TensorMemoryLayout.addressLane(dst) + TensorMemoryLayout.replicated(UInt[64](1), run % 64, UInt[64](0)), TensorMemoryLayout.addressColumn(dst) + 0, v1)
        |      cluster.storeTensorWords(v0, TensorMemoryLayout.addressLane(dst) + TensorMemoryLayout.replicated(UInt[64](1), run % 64, UInt[64](1)), TensorMemoryLayout.addressColumn(dst) + 0, v1)
        |    }
        |  }
        |  ()
        |}""".stripMargin
    )
  }

  test("a tensor memory shift renders its address check and one shift") {
    assertEquals(
      shown(tensorMemoryShift(pair = false)),
      """def `tcgen05.shift`(dst: UInt[64])(using machine: Machine, cluster: Cluster): Unit = {
        |  machine.require(TensorMemoryLayout.addressLane(dst).to[UInt[5]] == UInt[5](0) && (TensorMemoryLayout.addressColumn(dst) + 8).to[SInt[64]] <= 512, "tcgen05.shift address")
        |  val v0 = cluster.at(cluster.ctaRank())
        |  cluster.shiftTensorMemory(v0, TensorMemoryLayout.addressLane(dst), TensorMemoryLayout.addressColumn(dst), 32, 8)
        |  ()
        |}""".stripMargin
    )
  }

  test("a tensor memory shift for a CTA pair checks both CTAs and shifts each") {
    assertEquals(
      shown(tensorMemoryShift(pair = true)),
      """def `tcgen05.shift`(dst: UInt[64])(using machine: Machine, cluster: Cluster): Unit = {
        |  machine.require(TensorMemoryLayout.addressLane(dst).to[UInt[5]] == UInt[5](0) && (TensorMemoryLayout.addressColumn(dst) + 8).to[SInt[64]] <= 512, "tcgen05.shift address")
        |  machine.require(cluster.present(cluster.ctaRank() - cluster.ctaRank().to[UInt[1]].to[UInt[64]] + 0), "cta pair")
        |  machine.require(cluster.present(cluster.ctaRank() - cluster.ctaRank().to[UInt[1]].to[UInt[64]] + 1), "cta pair")
        |  val v0 = cluster.at(cluster.ctaRank() - cluster.ctaRank().to[UInt[1]].to[UInt[64]] + 0)
        |  cluster.shiftTensorMemory(v0, TensorMemoryLayout.addressLane(dst), TensorMemoryLayout.addressColumn(dst), 32, 8)
        |  val v1 = cluster.at(cluster.ctaRank() - cluster.ctaRank().to[UInt[1]].to[UInt[64]] + 1)
        |  cluster.shiftTensorMemory(v1, TensorMemoryLayout.addressLane(dst), TensorMemoryLayout.addressColumn(dst), 32, 8)
        |  ()
        |}""".stripMargin
    )
  }

  test("a tensor memory commit renders one arrival on its mbarrier") {
    assertEquals(
      shown(tensorMemoryCommit(multicast = false)),
      """def `tcgen05.commit`(bar: UInt[64])(using machine: Machine, cluster: Cluster, barriers: Barriers): Unit = {
        |  machine.require(cluster.shared(bar, UInt[64](8)), "mbarrier")
        |  val _ = barriers.arrive(bar, UInt[64](1), UInt[64](0), false, true)
        |  ()
        |}""".stripMargin
    )
  }

  test("a guarded multicast tensor memory commit arrives in each CTA its mask names") {
    assertEquals(
      shown(tensorMemoryCommit(multicast = true, guarded = true)),
      """def `tcgen05.commit`(p: Bool, bar: UInt[64], mask: UInt[64])(using tensors: Tensors, machine: Machine, cluster: Cluster, barriers: Barriers): Unit = {
        |  if (p) {
        |    for (run <- tensors.indices(UInt[64](16))) {
        |      if ((mask >> run).to[UInt[1]] != UInt[1](0)) {
        |        machine.require(cluster.shared(cluster.clusterAddress(bar, run), UInt[64](8)), "mbarrier")
        |        val v0 = barriers.arrive(cluster.clusterAddress(bar, run), UInt[64](1), UInt[64](0), false, true)
        |      }
        |    }
        |  }
        |  ()
        |}""".stripMargin
    )
  }

  private def outline(definition: Either[String, Definition], head: Int, tail: Int): String = {
    val lines = shown(definition).linesIterator.toList
    (lines.take(head) ::: lines.takeRight(tail)).mkString("\n")
  }

  private val Product128x64 = (8L << 24) | (8L << 17)

  test("an f16 tensor memory product renders the dot product over K from its f16 operands") {
    assertEquals(
      outline(tensorMemoryMultiply(Tcgen05.Kind.F16, Value.Immediate(Product128x64 | (1L << 4))), head = 11, tail = 5),
      """def `tcgen05.mma`(dst: UInt[64], src: UInt[64], size: UInt[64], bar: UInt[64])(using tensors: Tensors): Unit = {
        |  val v0 = tensors.product(VMIR.Tcgen05.Kind.F16, false, false, false, 0, UInt[64](0x8100010), dst, src, size, bar)
        |  for (run <- tensors.indices(tensors.size(v0))) {
        |    val v1 = if (bar != UInt[64](0)) tensors.c[Float32](v0, run).bits else UInt[32](0)
        |    val v2 = TensorArithmetic.widen(v1.asFloat)
        |    val v3 = TensorArithmetic.widen(v1.asFloat)
        |    val v4 = TensorArithmetic.widen(tensors.a[Float16](v0, run, 0))
        |    val v5 = TensorArithmetic.widen(tensors.b[Float16](v0, run, 0))
        |    val v6 = TensorArithmetic.product(v4, v5)
        |    val v7 = TensorArithmetic.special(v2, v6)
        |    val v8 = TensorArithmetic.exponent(v4, minimum = -14) + TensorArithmetic.exponent(v5, minimum = -14)
        |    val v86 = TensorArithmetic.round(v85, v84, v3, extra = 2, fraction = 23, nearest = false)
        |    tensors.store(v0, run, TensorArithmetic.finish(v86, v82).bits.to[UInt[64]])
        |  }
        |  ()
        |}""".stripMargin
    )
  }

  test("an fp8 tensor memory product renders its element conversions at the types of A and B") {
    assertEquals(
      outline(
        tensorMemoryMultiply(Tcgen05.Kind.F8F6F4, Value.Immediate(Product128x64 | (1L << 4))),
        head = 11,
        tail = 5
      ),
      """def `tcgen05.mma`(dst: UInt[64], src: UInt[64], size: UInt[64], bar: UInt[64])(using tensors: Tensors): Unit = {
        |  val v0 = tensors.product(VMIR.Tcgen05.Kind.F8F6F4, false, false, false, 0, UInt[64](0x8100010), dst, src, size, bar)
        |  for (run <- tensors.indices(tensors.size(v0))) {
        |    val v1 = if (bar != UInt[64](0)) tensors.c[Float32](v0, run).bits else UInt[32](0)
        |    val v2 = TensorArithmetic.widen(v1.asFloat)
        |    val v3 = TensorArithmetic.widen(FloatingPoint[32](0))
        |    val v4 = TensorArithmetic.widen(tensors.a[Float8E4M3](v0, run, 0))
        |    val v5 = TensorArithmetic.widen(tensors.b[Float8E4M3](v0, run, 0))
        |    val v6 = TensorArithmetic.product(v4, v5)
        |    val v7 = TensorArithmetic.special(v2, v6)
        |    val v8 = TensorArithmetic.exponent(v4, minimum = -6) + TensorArithmetic.exponent(v5, minimum = -6)
        |    val v170 = TensorArithmetic.round(v169, v168, v87, extra = 2, fraction = 23, nearest = false)
        |    tensors.store(v0, run, TensorArithmetic.finish(FloatArithmetic.add(v170, v1.asFloat), v166).bits.to[UInt[64]])
        |  }
        |  ()
        |}""".stripMargin
    )
  }

  test("a block-scaled tensor memory product renders the scale factors it reads for each block") {
    assertEquals(
      outline(
        tensorMemoryMultiply(Tcgen05.Kind.MXF8F6F4, Value.Immediate((1L << 27) | (8L << 17) | (1L << 23))),
        head = 12,
        tail = 5
      ),
      """def `tcgen05.mma`(dst: UInt[64], src: UInt[64], size: UInt[64], bar: UInt[64], left: UInt[64], right: UInt[64])(using tensors: Tensors): Unit = {
        |  val v0 = tensors.product(VMIR.Tcgen05.Kind.MXF8F6F4, false, false, false, 0, UInt[64](0x8900000), dst, src, size, bar, left, right)
        |  for (run <- tensors.indices(tensors.size(v0))) {
        |    val v1 = if (bar != UInt[64](0)) tensors.c[Float32](v0, run).bits else UInt[32](0)
        |    val v2 = TensorArithmetic.widen(v1.asFloat)
        |    val v3 = TensorArithmetic.widen(FloatingPoint[32](0))
        |    val v4 = TensorArithmetic.widen(tensors.a[Float8E4M3](v0, run, 0))
        |    val v5 = TensorArithmetic.widen(tensors.b[Float8E4M3](v0, run, 0))
        |    val v6 = TensorArithmetic.product(TensorArithmetic.scale(tensors.scaleA[ScaleE8M0](v0, run, 0)), TensorArithmetic.scale(tensors.scaleB[ScaleE8M0](v0, run, 0)))
        |    val v7 = TensorArithmetic.product(TensorArithmetic.product(v4, v5), v6)
        |    val v8 = TensorArithmetic.special(v2, v7)
        |    val v9 = TensorArithmetic.scaled(TensorArithmetic.exponent(v4, minimum = -6) + TensorArithmetic.exponent(v5, minimum = -6), v6)
        |    val v202 = TensorArithmetic.round(v201, v200, v103, extra = 2, fraction = 23, nearest = false)
        |    tensors.store(v0, run, TensorArithmetic.finish(FloatArithmetic.add(v202, v1.asFloat), v198).bits.to[UInt[64]])
        |  }
        |  ()
        |}""".stripMargin
    )
  }

  test("an f16 tensor memory product with a register descriptor picks its formats by the descriptor") {
    val product = tensorMemoryMultiply(Tcgen05.Kind.F16, row)
    assertEquals(
      outline(product, head = 14, tail = 5),
      """def `tcgen05.mma`(row: UInt[64], dst: UInt[64], src: UInt[64], size: UInt[64], bar: UInt[64])(using tensors: Tensors): Unit = {
        |  val v0 = tensors.product(VMIR.Tcgen05.Kind.F16, false, false, false, 0, row, dst, src, size, bar)
        |  for (run <- tensors.indices(tensors.size(v0))) {
        |    val v1 = (row >> 7).to[UInt[3]]
        |    val v2 = (row >> 10).to[UInt[3]]
        |    val v3 = (row >> 4).to[UInt[2]]
        |    val v4 = (row >> 13).to[UInt[1]]
        |    val v5 = (row >> 14).to[UInt[1]]
        |    if (v3 == UInt[2](0)) {
        |      val v6 = if (bar != UInt[64](0)) tensors.c[Float16](v0, run).bits else UInt[16](0)
        |      val v7 = TensorArithmetic.widen(v6.asFloat)
        |      val v8 = TensorArithmetic.widen(v6.asFloat)
        |      val v9 = TensorArithmetic.widen(if (v4 != UInt[1](0)) ProductArithmetic.negate(tensors.a[Float16](v0, run, 0)).bits.asFloat else tensors.a[Float16](v0, run, 0).bits.asFloat)
        |      val v10 = TensorArithmetic.widen(if (v5 != UInt[1](0)) ProductArithmetic.negate(tensors.b[Float16](v0, run, 0)).bits.asFloat else tensors.b[Float16](v0, run, 0).bits.asFloat)
        |      tensors.store(v0, run, TensorArithmetic.finish(v223, v219).bits.to[UInt[64]])
        |    }
        |  }
        |  ()
        |}""".stripMargin
    )
    assertEquals(
      shown(product).linesIterator.filter(_.startsWith("    if (")).map(_.trim).toList,
      List(
        "if (v3 == UInt[2](0)) {",
        "if ((v1 == UInt[3](0) || v1 == UInt[3](1)) && v3 == UInt[2](1)) {",
        "if (v1 == UInt[3](2)) {"
      )
    )
  }

  test("an fp8 tensor memory product with a register descriptor converts each operand by its format field") {
    val product = tensorMemoryMultiply(Tcgen05.Kind.F8F6F4, row)
    assertEquals(
      outline(product, head = 14, tail = 5),
      """def `tcgen05.mma`(row: UInt[64], dst: UInt[64], src: UInt[64], size: UInt[64], bar: UInt[64])(using tensors: Tensors): Unit = {
        |  val v0 = tensors.product(VMIR.Tcgen05.Kind.F8F6F4, false, false, false, 0, row, dst, src, size, bar)
        |  for (run <- tensors.indices(tensors.size(v0))) {
        |    val v1 = (row >> 7).to[UInt[3]]
        |    val v2 = (row >> 10).to[UInt[3]]
        |    val v3 = (row >> 4).to[UInt[2]]
        |    val v4 = (row >> 13).to[UInt[1]]
        |    val v5 = (row >> 14).to[UInt[1]]
        |    if (v3 == UInt[2](0)) {
        |      val v6 = if (bar != UInt[64](0)) tensors.c[Float16](v0, run).bits else UInt[16](0)
        |      val v7 = TensorArithmetic.widen(v6.asFloat)
        |      val v8 = TensorArithmetic.widen(FloatingPoint[16](0))
        |      val v9 = if (v1 == UInt[3](0)) TensorArithmetic.widen(Float8E4M3(if (v4 != UInt[1](0)) ProductArithmetic.negate(tensors.a[Float8E4M3](v0, run, 0)).bits else tensors.a[Float8E4M3](v0, run, 0).bits)).bits else if (v1 == UInt[3](1)) TensorArithmetic.widen(Float8E5M2(if (v4 != UInt[1](0)) ProductArithmetic.negate(tensors.a[Float8E5M2](v0, run, 0)).bits else tensors.a[Float8E5M2](v0, run, 0).bits)).bits else if (v1 == UInt[3](3)) TensorArithmetic.widen(Float6E2M3(if (v4 != UInt[1](0)) ProductArithmetic.negate(tensors.a[Float6E2M3](v0, run, 0)).bits else tensors.a[Float6E2M3](v0, run, 0).bits)).bits else if (v1 == UInt[3](4)) TensorArithmetic.widen(Float6E3M2(if (v4 != UInt[1](0)) ProductArithmetic.negate(tensors.a[Float6E3M2](v0, run, 0)).bits else tensors.a[Float6E3M2](v0, run, 0).bits)).bits else TensorArithmetic.widen(Float4E2M1(if (v4 != UInt[1](0)) ProductArithmetic.negate(tensors.a[Float4E2M1](v0, run, 0)).bits else tensors.a[Float4E2M1](v0, run, 0).bits)).bits
        |      val v10 = if (v2 == UInt[3](0)) TensorArithmetic.widen(Float8E4M3(if (v5 != UInt[1](0)) ProductArithmetic.negate(tensors.b[Float8E4M3](v0, run, 0)).bits else tensors.b[Float8E4M3](v0, run, 0).bits)).bits else if (v2 == UInt[3](1)) TensorArithmetic.widen(Float8E5M2(if (v5 != UInt[1](0)) ProductArithmetic.negate(tensors.b[Float8E5M2](v0, run, 0)).bits else tensors.b[Float8E5M2](v0, run, 0).bits)).bits else if (v2 == UInt[3](3)) TensorArithmetic.widen(Float6E2M3(if (v5 != UInt[1](0)) ProductArithmetic.negate(tensors.b[Float6E2M3](v0, run, 0)).bits else tensors.b[Float6E2M3](v0, run, 0).bits)).bits else if (v2 == UInt[3](4)) TensorArithmetic.widen(Float6E3M2(if (v5 != UInt[1](0)) ProductArithmetic.negate(tensors.b[Float6E3M2](v0, run, 0)).bits else tensors.b[Float6E3M2](v0, run, 0).bits)).bits else TensorArithmetic.widen(Float4E2M1(if (v5 != UInt[1](0)) ProductArithmetic.negate(tensors.b[Float4E2M1](v0, run, 0)).bits else tensors.b[Float4E2M1](v0, run, 0).bits)).bits
        |      tensors.store(v0, run, TensorArithmetic.finish(FloatArithmetic.add(v345, v176.asFloat), v341).bits.to[UInt[64]])
        |    }
        |  }
        |  ()
        |}""".stripMargin
    )
    assertEquals(
      shown(product).linesIterator.filter(_.startsWith("    if (")).map(_.trim).toList,
      List(
        "if (v3 == UInt[2](0)) {",
        "if (v3 == UInt[2](1)) {"
      )
    )
  }

  test("a block-scaled tensor memory product with a register descriptor picks its scale format by the descriptor") {
    val product = tensorMemoryMultiply(Tcgen05.Kind.MXF4NVF4, row)
    assertEquals(
      outline(product, head = 13, tail = 5),
      """def `tcgen05.mma`(row: UInt[64], dst: UInt[64], src: UInt[64], size: UInt[64], bar: UInt[64], left: UInt[64], right: UInt[64])(using tensors: Tensors): Unit = {
        |  val v0 = tensors.product(VMIR.Tcgen05.Kind.MXF4NVF4, false, false, false, 0, row, dst, src, size, bar, left, right)
        |  for (run <- tensors.indices(tensors.size(v0))) {
        |    val v1 = (row >> 23).to[UInt[2]]
        |    val v2 = (row >> 13).to[UInt[1]]
        |    val v3 = (row >> 14).to[UInt[1]]
        |    val v4 = if (bar != UInt[64](0)) tensors.c[Float32](v0, run).bits else UInt[32](0)
        |    val v5 = TensorArithmetic.widen(v4.asFloat)
        |    val v6 = TensorArithmetic.widen(FloatingPoint[32](0))
        |    val v7 = TensorArithmetic.widen(Float4E2M1(if (v2 != UInt[1](0)) ProductArithmetic.negate(tensors.a[Float4E2M1](v0, run, 0)).bits else tensors.a[Float4E2M1](v0, run, 0).bits))
        |    val v8 = TensorArithmetic.widen(Float4E2M1(if (v3 != UInt[1](0)) ProductArithmetic.negate(tensors.b[Float4E2M1](v0, run, 0)).bits else tensors.b[Float4E2M1](v0, run, 0).bits))
        |    val v9 = if (v1 == UInt[2](0)) TensorArithmetic.product(TensorArithmetic.scale(tensors.scaleA[Float8E4M3](v0, run, 0)), TensorArithmetic.scale(tensors.scaleB[Float8E4M3](v0, run, 0))).bits else TensorArithmetic.product(TensorArithmetic.scale(tensors.scaleA[ScaleE8M0](v0, run, 0)), TensorArithmetic.scale(tensors.scaleB[ScaleE8M0](v0, run, 0))).bits
        |    val v10 = TensorArithmetic.product(TensorArithmetic.product(v7, v8), v9.asFloat)
        |    val v405 = TensorArithmetic.round(v404, v403, v306, extra = 2, fraction = 23, nearest = false)
        |    tensors.store(v0, run, TensorArithmetic.finish(FloatArithmetic.add(v405, v4.asFloat), v401).bits.to[UInt[64]])
        |  }
        |  ()
        |}""".stripMargin
    )
  }
}
