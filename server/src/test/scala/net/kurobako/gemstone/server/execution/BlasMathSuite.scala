package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.{FloatingPointWidth, IEEE754, LittleEndian, ScalarByteWidth}
import net.kurobako.gemstone.rpc.AllocationId
import net.kurobako.gemstone.rpc.ExecutionProtocol.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final class BlasMathSuite extends munit.FunSuite {
  private val x = AllocationId(1)
  private val y = AllocationId(2)
  private val a = AllocationId(3)
  private val b = AllocationId(4)
  private val c = AllocationId(5)

  private def f32(values: Float*): Array[Byte] = {
    val bytes = Array.ofDim[Byte](values.size * 4)
    values.zipWithIndex.foreach { case (value, index) =>
      LittleEndian.write(
        java.lang.Float.floatToRawIntBits(value).toLong & 0xffffffffL,
        bytes,
        index * 4,
        ScalarByteWidth.Dword
      )
    }
    bytes
  }

  private def read(bytes: Array[Byte]): Vec[Float] =
    Vec.tabulate(bytes.length / 4)(index =>
      java.lang.Float.intBitsToFloat(LittleEndian.decode(bytes, index * 4, ScalarByteWidth.Dword).toInt)
    )

  private def scalar(value: Float): Vec[Byte] = Vec.from(f32(value))

  private def memory(entries: (AllocationId, Array[Byte])*): AllocationId => Option[Array[Byte]] =
    entries.toMap.get(_)

  private def vector(allocation: AllocationId, increment: Int = 1): Option[BlasVector] =
    Some(BlasVector(allocation, 0L.refineUnsafe[Positive0], increment))

  private def request(operation: BlasOperation, element: BlasElement = BlasElement.F32, count: Int = 0) =
    BlasRequest(
      operation = operation,
      element = element,
      count = count,
      rows = 0,
      columns = 0,
      inner = 0,
      transposeA = false,
      transposeB = false,
      alpha = scalar(0),
      beta = scalar(0),
      x = None,
      y = None,
      a = None,
      b = None,
      c = None,
      strideA = 0L,
      strideB = 0L,
      strideC = 0L,
      batch = 0
    )

  test("axpy, scal and copy follow strided vectors") {
    val target = f32(10, 20, 30)
    val axpy   = request(BlasOperation.Axpy, count = 3).copy(x = vector(x), y = vector(y), alpha = scalar(2))
    assertEquals(BlasMath.execute(axpy, memory(x -> f32(1, 2, 3), y -> target)), Right(BlasResult(Vec.empty)))
    assertEquals(read(target).toList, List(12.0f, 24.0f, 36.0f))

    val scal = f32(1, 2, 3)
    assertEquals(
      BlasMath.execute(
        request(BlasOperation.Scal, count = 3).copy(x = vector(x), alpha = scalar(3)),
        memory(x -> scal)
      ),
      Right(BlasResult(Vec.empty))
    )
    assertEquals(read(scal).toList, List(3.0f, 6.0f, 9.0f))

    val destination = f32(0, 0, 0)
    assertEquals(
      BlasMath.execute(
        request(BlasOperation.Copy, count = 3).copy(x = vector(x), y = vector(y)),
        memory(x -> f32(4, 5, 6), y -> destination)
      ),
      Right(BlasResult(Vec.empty))
    )
    assertEquals(read(destination).toList, List(4.0f, 5.0f, 6.0f))
  }

  test("reductions return scalars") {
    def scalarOf(result: Either[String, BlasResult]): Float =
      java.lang.Float.intBitsToFloat(LittleEndian.decode(
        result.toOption.get.scalar.toArray,
        0,
        ScalarByteWidth.Dword
      ).toInt)

    assertEquals(
      scalarOf(BlasMath.execute(
        request(BlasOperation.Dot, count = 2).copy(x = vector(x), y = vector(y)),
        memory(x -> f32(1, 2), y -> f32(3, 4))
      )),
      11.0f
    )
    assertEquals(
      scalarOf(BlasMath.execute(request(BlasOperation.Nrm2, count = 2).copy(x = vector(x)), memory(x -> f32(3, 4)))),
      5.0f
    )
    assertEquals(
      scalarOf(BlasMath.execute(
        request(BlasOperation.Asum, count = 3).copy(x = vector(x)),
        memory(x -> f32(-1, 2, -3))
      )),
      6.0f
    )
  }

  test("gemv applies alpha and beta") {
    val output = f32(0, 0)
    val gemv   = request(BlasOperation.Gemv).copy(
      rows = 2,
      columns = 2,
      a = Some(BlasMatrix(a, 0L.refineUnsafe[Positive0], 2)),
      x = vector(x),
      y = vector(y),
      alpha = scalar(1),
      beta = scalar(0)
    )
    assertEquals(
      BlasMath.execute(gemv, memory(a -> f32(1, 2, 3, 4), x -> f32(1, 1), y -> output)),
      Right(BlasResult(Vec.empty))
    )
    assertEquals(read(output).toList, List(4.0f, 6.0f))
  }

  test("gemm and strided batched gemm write the product") {
    def gemm(operation: BlasOperation, batch: Int, strideC: Long) =
      request(operation).copy(
        rows = 2,
        columns = 2,
        inner = 2,
        a = Some(BlasMatrix(a, 0L.refineUnsafe[Positive0], 2)),
        b = Some(BlasMatrix(b, 0L.refineUnsafe[Positive0], 2)),
        c = Some(BlasMatrix(c, 0L.refineUnsafe[Positive0], 2)),
        alpha = scalar(1),
        beta = scalar(0),
        batch = batch,
        strideA = 4L,
        strideB = 4L,
        strideC = strideC
      )

    val single = f32(0, 0, 0, 0)
    assertEquals(
      BlasMath.execute(
        gemm(BlasOperation.Gemm, 0, 0L),
        memory(a -> f32(1, 2, 3, 4), b -> f32(5, 6, 7, 8), c -> single)
      ),
      Right(BlasResult(Vec.empty))
    )
    assertEquals(read(single).toList, List(23.0f, 34.0f, 31.0f, 46.0f))

    val batched = f32(0, 0, 0, 0, 0, 0, 0, 0)
    val both    = memory(a -> f32(1, 2, 3, 4, 1, 0, 0, 1), b -> f32(5, 6, 7, 8, 1, 0, 0, 1), c -> batched)
    assertEquals(BlasMath.execute(gemm(BlasOperation.GemmStridedBatched, 2, 4L), both), Right(BlasResult(Vec.empty)))
    assertEquals(read(batched).toList, List(23.0f, 34.0f, 31.0f, 46.0f, 1.0f, 0.0f, 0.0f, 1.0f))
  }

  test("half precision gemm matches the single precision reference") {
    val half  = Array[Byte](0, 0, 0, 0, 0, 0, 0, 0)
    val hgemm = request(BlasOperation.Gemm, element = BlasElement.F16).copy(
      rows = 2,
      columns = 2,
      inner = 2,
      a = Some(BlasMatrix(a, 0L.refineUnsafe[Positive0], 2)),
      b = Some(BlasMatrix(b, 0L.refineUnsafe[Positive0], 2)),
      c = Some(BlasMatrix(c, 0L.refineUnsafe[Positive0], 2)),
      alpha = Vec[Byte](0x00, 0x3c),
      beta = Vec[Byte](0x00, 0x00)
    )
    def halfOf(value: Float): Array[Byte] = {
      val bits = IEEE754.fromDouble(value.toDouble, FloatingPointWidth(16)).toInt & 0xffff
      Array[Byte](bits.toByte, (bits >> 8).toByte)
    }
    val operands = memory(
      a -> (halfOf(1) ++ halfOf(2) ++ halfOf(3) ++ halfOf(4)),
      b -> (halfOf(5) ++ halfOf(6) ++ halfOf(7) ++ halfOf(8)),
      c -> half
    )
    assertEquals(BlasMath.execute(hgemm, operands), Right(BlasResult(Vec.empty)))
    assertEquals(half.toList, (halfOf(23) ++ halfOf(34) ++ halfOf(31) ++ halfOf(46)).toList)
  }
}
