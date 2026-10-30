package net.kurobako.gemstone.isa.nvidia

import MachineDefinitions.*
import VMIR.*
import dotty.tools.dotc.Driver
import dotty.tools.dotc.reporting.StoreReporter
import java.nio.file.Files
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.testing.Suite

class RenderedMachineSuite extends Suite {
  private def errors(definition: Either[String, Definition]): List[String] =
    errors(definition.fold(failure => fail(failure), ScalaBackend.source(_)))

  private def errors(source: String, stopAfter: Option[String] = None): List[String] = {
    val directory = Files.createTempDirectory("gemstone-rendered-")
    try {
      val file = directory.resolve("Rendered.scala")
      Files.writeString(file, s"package net.kurobako.gemstone.isa.nvidia\n\n$source\n")
      val output   = Files.createDirectory(directory.resolve("classes"))
      val reporter = StoreReporter()
      val _        = Driver().process(
        Array(
          "-classpath",
          sys.props("gemstone.nvidia.classpath"),
          "-d",
          output.toString,
          "-color:never"
        ) ++ stopAfter.map(phase => s"-Ystop-after:$phase") :+ file.toString,
        reporter
      )
      reporter.allErrors.map(_.message)
    } finally {
      val paths = Files.walk(directory)
      try paths.sorted(java.util.Comparator.reverseOrder()).forEach(path => Files.delete(path))
      finally paths.close()
    }
  }

  test("the compiler rejects ill-typed source") {
    assertEquals(errors("def broken(): Int = \"text\"").size, 1)
  }

  test("a rendered branch compiles") {
    assertEquals(errors(branch()), Nil)
    assertEquals(errors(branch(guarded = true)), Nil)
  }

  test("a rendered bulk copy compiles with and without multicast, ignored bounds and a cluster launch") {
    assertEquals(errors(copy()), Nil)
    assertEquals(errors(copy(guarded = true)), Nil)
    assertEquals(errors(copy(sharedSource = true, multicast = Some(mask), ignored = Some((left, right)))), Nil)
    assertEquals(errors(copy(sharedSource = true, clusterDestination = true)), Nil)
  }

  test("rendered arrivals, writes, waits and calls compile") {
    assertEquals(errors(arrival()), Nil)
    assertEquals(errors(calls()), Nil)
  }

  test("rendered copies from global memory and stack operations compile") {
    assertEquals(errors(asyncCopy()), Nil)
    assertEquals(errors(stackAllocate()), Nil)
    assertEquals(errors(stackSave()), Nil)
    assertEquals(errors(stackRestore()), Nil)
    assertEquals(errors(stackRestore(guarded = true)), Nil)
  }

  test("a rendered tensor map copy compiles") {
    assertEquals(errors(tensorMapCopy()), Nil)
    assertEquals(errors(tensorMapCopy(guarded = true)), Nil)
  }

  test("rendered tensor map replacements compile with and without a value check") {
    assertEquals(errors(tensorMapReplace(TensorMap.Field.ElementType)), Nil)
    assertEquals(errors(tensorMapReplace(TensorMap.Field.Swizzle)), Nil)
    assertEquals(errors(tensorMapReplace(TensorMap.Field.BoxDimension, 3)), Nil)
  }

  test("rendered tensor copies compile for each operation, with multicast, pairs, gathers and im2col") {
    assertEquals(errors(tensorCopy()), Nil)
    assertEquals(errors(tensorCopy(guarded = true)), Nil)
    assertEquals(errors(tensorCopy(multicast = true, pair = true)), Nil)
    assertEquals(errors(tensorCopy(completes = false)), Nil)
    assertEquals(errors(tensorCopy(TensorMap.Operation.Store)), Nil)
    assertEquals(errors(tensorCopy(TensorMap.Operation.Reduce(TensorMap.Reduction.Maximum), guarded = true)), Nil)
    assertEquals(errors(tensorCopy(TensorMap.Operation.Prefetch, gather = true)), Nil)
    assertEquals(errors(tensorCopy(im2col = true, rank = 3)), Nil)
    assertEquals(errors(tensorCopy(im2col = true, rank = 4)), Nil)
    assertEquals(errors(tensorCopy(im2col = true, rank = 5, guarded = true)), Nil)
    assertEquals(errors(tensorCopy(rank = 1)), Nil)
    assertEquals(errors(tensorCopy(rank = 3)), Nil)
    assertEquals(errors(tensorCopy(rank = 5)), Nil)
    assertEquals(errors(tensorCopy(gather = true)), Nil)
    assertEquals(errors(tensorCopy(TensorMap.Operation.Store, gather = true)), Nil)
    assertEquals(errors(tensorCopy(TensorMap.Operation.Reduce(TensorMap.Reduction.Add), rank = 3)), Nil)
  }

  test("rendered tensor memory products compile for f16, fp8 and block-scaled forms and an opaque descriptor") {
    val shape = (8L << 24) | (8L << 17)
    assertEquals(errors(tensorMemoryMultiply(Tcgen05.Kind.F16, Value.Immediate(shape | (1L << 4)))), Nil)
    assertEquals(errors(tensorMemoryMultiply(Tcgen05.Kind.F8F6F4, Value.Immediate(shape | (1L << 4)))), Nil)
    assertEquals(
      errors(tensorMemoryMultiply(Tcgen05.Kind.MXF8F6F4, Value.Immediate((1L << 27) | (8L << 17) | (1L << 23)))),
      Nil
    )
  }

  Vec(Tcgen05.Kind.F16, Tcgen05.Kind.TF32, Tcgen05.Kind.I8, Tcgen05.Kind.MXF4, Tcgen05.Kind.MXF4NVF4).foreach { kind =>
    test(s"a rendered tensor memory product with a register descriptor compiles for $kind") {
      assertEquals(errors(tensorMemoryMultiply(kind, row)), Nil)
    }
  }

  // code generation is quadratic in the branches of these forms, so only their types are checked
  Vec((Tcgen05.Kind.F8F6F4, false), (Tcgen05.Kind.MXF8F6F4, false), (Tcgen05.Kind.F16, true)).foreach { (kind, sparse) =>
    test(s"a rendered tensor memory product with a register descriptor type checks for $kind, sparse $sparse") {
      val source = tensorMemoryMultiply(kind, row, sparse).fold(failure => fail(failure), ScalaBackend.source(_))
      assertEquals(errors(source, Some("erasure")), Nil)
    }
  }

  test("a rendered matrix move compiles") {
    assertEquals(errors(moveMatrix()), Nil)
  }

  test("rendered unit widths and lane activity compile") {
    val unit = rendered("unit")(machine =>
      machine.writeEach(Vec(Register(1), Register(2))) { (lane, index) =>
        if (index == 0) machine.active(lane, machine.laneIndex(lane)) else machine.unitSize
      }(machine.advance)
    )
    assertEquals(errors(unit), Nil)
  }

  test("rendered matrix loads compile with one, two and four matrices, transposed and in byte forms") {
    assertEquals(errors(loadMatrix(1)), Nil)
    assertEquals(errors(loadMatrix(2, transpose = true)), Nil)
    assertEquals(errors(loadMatrix(4)), Nil)
    assertEquals(errors(loadMatrix(2, shape = Matrix.TileShape.M16N16, packing = Matrix.Packing.Four)), Nil)
    assertEquals(
      errors(loadMatrix(4, transpose = true, shape = Matrix.TileShape.M8N16, packing = Matrix.Packing.Six)),
      Nil
    )
  }

  test("rendered matrix stores compile with one, two and four matrices, transposed and in generic space") {
    assertEquals(errors(storeMatrix(1)), Nil)
    assertEquals(errors(storeMatrix(2, transpose = true, shape = Matrix.TileShape.M16N8)), Nil)
    assertEquals(errors(storeMatrix(4)), Nil)
    assertEquals(errors(storeMatrix(1, space = MemorySpace.Generic)), Nil)
  }

  test("rendered fragment loads and stores compile for each element form") {
    val subByte = Matrix.Fragment(Matrix.Shape(8, 8, 32), Matrix.Role.A, Matrix.Element.S4, wmma = true)
    assertEquals(errors(loadFragment(subByte)), Nil)
    assertEquals(
      errors(loadFragment(Matrix.Fragment(Matrix.Shape(8, 8, 128), Matrix.Role.A, Matrix.Element.B1, wmma = true))),
      Nil
    )
    assertEquals(
      errors(loadFragment(Matrix.Fragment(Matrix.Shape(8, 8, 4), Matrix.Role.A, Matrix.Element.F64, wmma = true))),
      Nil
    )
    assertEquals(
      errors(loadFragment(
        Matrix.Fragment(Matrix.Shape(16, 16, 16), Matrix.Role.A, Matrix.Element.F16, wmma = true),
        space = MemorySpace.Generic
      )),
      Nil
    )
    assertEquals(
      errors(loadFragment(
        Matrix.Fragment(Matrix.Shape(16, 16, 8), Matrix.Role.B, Matrix.Element.TF32, wmma = true),
        columnMajor = true
      )),
      Nil
    )
    assertEquals(
      errors(storeFragment(Matrix.Fragment(Matrix.Shape(16, 16, 16), Matrix.Role.C, Matrix.Element.F32, wmma = true))),
      Nil
    )
    assertEquals(
      errors(storeFragment(
        Matrix.Fragment(Matrix.Shape(16, 16, 16), Matrix.Role.C, Matrix.Element.F16, wmma = true),
        columnMajor = true,
        space = MemorySpace.Global
      )),
      Nil
    )
    assertEquals(
      errors(storeFragment(Matrix.Fragment(Matrix.Shape(8, 8, 4), Matrix.Role.C, Matrix.Element.F64, wmma = true))),
      Nil
    )
    assertEquals(
      errors(storeFragment(Matrix.Fragment(Matrix.Shape(8, 8, 32), Matrix.Role.A, Matrix.Element.S4, wmma = true))),
      Nil
    )
  }

  test("a rendered bulk store compiles") {
    assertEquals(errors(bulkStore()), Nil)
  }

  test("a rendered bulk fill compiles") {
    assertEquals(errors(bulkFill()), Nil)
  }

  test("rendered bulk reductions compile with and without an mbarrier") {
    assertEquals(errors(bulkReduce()), Nil)
    assertEquals(errors(bulkReduce(element = TensorMap.Element.BF16)), Nil)
    assertEquals(
      errors(bulkReduce(completes = false, operator = TensorMap.Reduction.Increment, element = TensorMap.Element.U32)),
      Nil
    )
  }

  test("a rendered asynchronous shared store compiles") {
    assertEquals(errors(sharedStore()), Nil)
  }

  test("a rendered asynchronous shared reduction compiles") {
    assertEquals(errors(sharedReduce()), Nil)
  }

  test("rendered exits, returns, traps, branch tables and predicated branches compile") {
    assertEquals(errors(exits()), Nil)
    assertEquals(errors(exits(guarded = true)), Nil)
    assertEquals(errors(returns()), Nil)
    assertEquals(errors(traps()), Nil)
    assertEquals(errors(traps(guarded = true)), Nil)
    assertEquals(errors(branchTable()), Nil)
    predicatedBranch(negated = true).foreach(definition => assertEquals(errors(Right(definition)), Nil))
  }

  test("a rendered host fence compiles") {
    assertEquals(errors(fence()), Nil)
    assertEquals(errors(fence(guarded = true)), Nil)
  }

  test("rendered calls, builtins and convergence operations compile") {
    assertEquals(errors(call(Value.Immediate(0x1000L))), Nil)
    assertEquals(errors(call(source)), Nil)
    assertEquals(errors(builtin()), Nil)
    assertEquals(errors(setupConvergence()), Nil)
    assertEquals(errors(synchroniseConvergence()), Nil)
    assertEquals(errors(leaveConvergence()), Nil)
    assertEquals(errors(leaveConvergence(guarded = true)), Nil)
  }

  test("rendered workgroup, warp and cluster barriers compile") {
    assertEquals(errors(barrierSync(Value.Immediate(0L))), Nil)
    assertEquals(errors(barrierSync(barrier, Some(size))), Nil)
    assertEquals(errors(barrierArrive(Value.Immediate(1L), Value.Immediate(32L))), Nil)
    assertEquals(errors(barrierArrive(barrier, size)), Nil)
    assertEquals(
      errors(barrierReduce(
        BarrierResult.Combined(Predicate(2), PredicateOperation.And),
        Value.Immediate(0L),
        Predicate(3)
      )),
      Nil
    )
    assertEquals(
      errors(barrierReduce(BarrierResult.Count(Register(9)), barrier, done, Some(size), negated = true)),
      Nil
    )
    assertEquals(
      errors(barrierReduce(
        BarrierResult.Combined(Predicate(2), PredicateOperation.And),
        Value.Immediate(0L),
        Predicate(3),
        Some(Value.Immediate(32L))
      )),
      Nil
    )
    assertEquals(
      errors(barrierReduce(BarrierResult.Count(Register(9)), Value.Immediate(0L), done, Some(size))),
      Nil
    )
    assertEquals(errors(warpBarrier()), Nil)
    assertEquals(errors(warpBarrier(guarded = true)), Nil)
    assertEquals(errors(clusterBarrier(arrive = true)), Nil)
    assertEquals(errors(clusterBarrier(arrive = false)), Nil)
  }

  test("rendered tensor memory allocations, deallocations and permits compile") {
    assertEquals(errors(tensorMemoryAllocate()), Nil)
    assertEquals(errors(tensorMemoryAllocate(guarded = true)), Nil)
    assertEquals(errors(tensorMemoryDeallocate()), Nil)
    assertEquals(errors(tensorMemoryDeallocate(guarded = true)), Nil)
    assertEquals(errors(tensorMemoryRelinquish()), Nil)
    assertEquals(errors(tensorMemoryRelinquish(guarded = true)), Nil)
  }

  test("rendered tensor memory loads and stores compile for each shape, packed, reduced and for half the threads") {
    val flags = Vec(false, true)
    Tcgen05.DataShape.values.foreach { shape =>
      flags.foreach { packed =>
        val movement = Tcgen05.Movement(shape, registers = shape.registers, split = 16, packed = packed)
        assertEquals(errors(tensorMemoryLoad(movement, 2)), Nil, s"$shape $packed")
        assertEquals(errors(tensorMemoryStore(movement, 2)), Nil, s"$shape $packed")
      }
    }
    val plain = Tcgen05.Movement(Tcgen05.DataShape.Lanes32Bits32, registers = 1, split = 0, packed = false)
    Vec(
      Tcgen05.ReductionKind.Float(absolute = true, propagate = false),
      Tcgen05.ReductionKind.Signed,
      Tcgen05.ReductionKind.Unsigned
    ).foreach(kind =>
      assertEquals(errors(tensorMemoryLoad(plain, 3, Some(Tcgen05.Reduction(Register(14), maximum = true, kind)))), Nil)
    )
    Vec(Tcgen05.Threads.Low, Tcgen05.Threads.High).foreach { threads =>
      val half = Tcgen05.Movement(Tcgen05.DataShape.Lanes16Bits32x2, registers = 1, split = 16, packed = false, threads)
      assertEquals(errors(tensorMemoryLoad(half, 1)), Nil)
      assertEquals(errors(tensorMemoryStore(half, 1, guarded = true)), Nil)
    }
  }

  test("rendered tensor memory copies, shifts and commits compile") {
    val flags = Vec(false, true)
    Tcgen05.CopyShape.values.foreach(shape =>
      Tcgen05.Replication.values.foreach(replication =>
        flags.foreach { pair =>
          val copy = Tcgen05.Copy(shape, replication, Matrix.Packing.Four, pair)
          assertEquals(errors(tensorMemoryCopy(copy)), Nil, s"$shape $replication $pair")
        }
      )
    )
    assertEquals(
      errors(tensorMemoryCopy(
        Tcgen05.Copy(Tcgen05.CopyShape.Rows4Bits256, Tcgen05.Replication.Every, Matrix.Packing.Bytes, pair = true),
        guarded = true
      )),
      Nil
    )
    assertEquals(errors(tensorMemoryShift(pair = false)), Nil)
    assertEquals(errors(tensorMemoryShift(pair = true, guarded = true)), Nil)
    assertEquals(errors(tensorMemoryCommit(multicast = false)), Nil)
    assertEquals(errors(tensorMemoryCommit(multicast = true, guarded = true)), Nil)
  }

  test("rendered cluster addressing and cancellations compile") {
    assertEquals(errors(clusterMap()), Nil)
    assertEquals(errors(clusterRank()), Nil)
    assertEquals(errors(clusterTryCancel()), Nil)
    assertEquals(errors(clusterTryCancel(multicast = true)), Nil)
  }

  test("rendered mbarrier operations compile") {
    assertEquals(errors(mbarrierInit()), Nil)
    assertEquals(errors(mbarrierInvalidate()), Nil)
    assertEquals(errors(mbarrierLoad()), Nil)
    assertEquals(errors(mbarrierArrive()), Nil)
    assertEquals(errors(mbarrierArrive(destination = None, bytes = size, drop = true, complete = false)), Nil)
    assertEquals(errors(mbarrierTransaction(complete = false)), Nil)
    assertEquals(errors(mbarrierTransaction(complete = true)), Nil)
    assertEquals(errors(mbarrierTest(parity = false)), Nil)
    assertEquals(errors(mbarrierTest(parity = true)), Nil)
    assertEquals(errors(mbarrierPending()), Nil)
  }
}
