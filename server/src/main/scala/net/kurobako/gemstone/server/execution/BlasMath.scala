package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.{Binary16, IEEE754, LittleEndian, ScalarByteWidth}
import net.kurobako.gemstone.rpc.AllocationId
import net.kurobako.gemstone.rpc.ExecutionProtocol.*

/** Executes the cuBLAS compatibility surface over server-side allocation storage. Element arithmetic follows the vendor
  * library: `f32`/`f16` accumulate in single precision, `f64` in double precision.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
private[execution] object BlasMath {
  private trait Ops[A] {
    def width: Int
    def read(bytes: Array[Byte], offset: Int): A
    def write(bytes: Array[Byte], offset: Int, value: A): Unit
    def toDouble(value: A): Double
    def fromDouble(value: Double): A
  }

  private object F32 extends Ops[Float] {
    val width: Int                                   = 4
    def read(bytes: Array[Byte], offset: Int): Float =
      java.lang.Float.intBitsToFloat(LittleEndian.decode(bytes, offset, ScalarByteWidth.Dword).toInt)
    def write(bytes: Array[Byte], offset: Int, value: Float): Unit =
      LittleEndian.write(
        java.lang.Float.floatToRawIntBits(value).toLong & 0xffffffffL,
        bytes,
        offset,
        ScalarByteWidth.Dword
      )
    def toDouble(value: Float): Double   = value.toDouble
    def fromDouble(value: Double): Float = value.toFloat
  }

  private object F64 extends Ops[Double] {
    val width: Int                                    = 8
    def read(bytes: Array[Byte], offset: Int): Double =
      java.lang.Double.longBitsToDouble(LittleEndian.decode(bytes, offset, ScalarByteWidth.Qword))
    def write(bytes: Array[Byte], offset: Int, value: Double): Unit =
      LittleEndian.write(java.lang.Double.doubleToRawLongBits(value), bytes, offset, ScalarByteWidth.Qword)
    def toDouble(value: Double): Double   = value
    def fromDouble(value: Double): Double = value
  }

  private object F16 extends Ops[Float] {
    val width: Int                                   = 2
    def read(bytes: Array[Byte], offset: Int): Float =
      halfToFloat(LittleEndian.decode(bytes, offset, ScalarByteWidth.Word).toInt)
    def write(bytes: Array[Byte], offset: Int, value: Float): Unit =
      LittleEndian.write(floatToHalf(value).toLong & 0xffffL, bytes, offset, ScalarByteWidth.Word)
    def toDouble(value: Float): Double   = value.toDouble
    def fromDouble(value: Double): Float = value.toFloat
  }

  def execute(
      request: BlasRequest,
      resolve: AllocationId => Option[Array[Byte]]
  ): Either[String, BlasResult] =
    request.element match {
      case BlasElement.F32 => run(request, resolve, F32)(using Numeric[Float])
      case BlasElement.F64 => run(request, resolve, F64)(using Numeric[Double])
      case BlasElement.F16 => run(request, resolve, F16)(using Numeric[Float])
    }

  private def run[A](request: BlasRequest, resolve: AllocationId => Option[Array[Byte]], ops: Ops[A])(using
      N: Numeric[A]
  ): Either[String, BlasResult] =
    try
      request.operation match {
        case BlasOperation.Axpy               => axpy(request, resolve, ops)
        case BlasOperation.Scal               => scale(request, resolve, ops)
        case BlasOperation.Copy               => copy(request, resolve, ops)
        case BlasOperation.Dot                => dot(request, resolve, ops)
        case BlasOperation.Nrm2               => norm(request, resolve, ops)
        case BlasOperation.Asum               => absoluteSum(request, resolve, ops)
        case BlasOperation.Gemv               => gemv(request, resolve, ops)
        case BlasOperation.Gemm               => gemm(request, resolve, ops, 0L, 0L, 0L)
        case BlasOperation.GemmStridedBatched =>
          (0 until request.batch).foldLeft[Either[String, BlasResult]](Right(BlasResult(Vec.empty))) {
            (accumulated, batch) =>
              accumulated.flatMap(_ =>
                gemm(
                  request,
                  resolve,
                  ops,
                  batch.toLong * request.strideA,
                  batch.toLong * request.strideB,
                  batch.toLong * request.strideC
                )
              )
          }
      }
    catch { case error: IndexOutOfBoundsException => Left(s"BLAS operand is out of bounds: ${error.getMessage}") }

  private def buffer(
      resolve: AllocationId => Option[Array[Byte]],
      allocation: AllocationId
  ): Either[String, Array[Byte]] =
    resolve(allocation).toRight(s"BLAS allocation ${allocation.value} is absent")

  private def indexOf(index: Int, count: Int, increment: Int): Int =
    if (increment < 0) (count - 1 - index) * -increment else index * increment

  private def elementOffset(base: Long, index: Int, ops: Ops[?]): Int = (base + index.toLong * ops.width).toInt

  private def scalar[A](bytes: Vec[Byte], ops: Ops[A]): A = ops.read(bytes.toArray, 0)

  private def scalarBytes[A](value: A, ops: Ops[A]): Vec[Byte] = {
    val bytes = Array.ofDim[Byte](ops.width)
    ops.write(bytes, 0, value)
    Vec.from(bytes)
  }

  private def axpy[A](request: BlasRequest, resolve: AllocationId => Option[Array[Byte]], ops: Ops[A])(using
      N: Numeric[A]
  ): Either[String, BlasResult] =
    for {
      x  <- request.x.toRight("BLAS request has no x operand")
      y  <- request.y.toRight("BLAS request has no y operand")
      xs <- buffer(resolve, x.allocation)
      ys <- buffer(resolve, y.allocation)
      alpha = scalar(request.alpha, ops)
      _     = if (request.count > 0) {
        var index = 0
        while (index < request.count) {
          val xi = elementOffset(x.offset, indexOf(index, request.count, x.increment), ops)
          val yi = elementOffset(y.offset, indexOf(index, request.count, y.increment), ops)
          ops.write(ys, yi, N.plus(ops.read(ys, yi), N.times(alpha, ops.read(xs, xi))))
          index += 1
        }
      }
    } yield BlasResult(Vec.empty)

  private def scale[A](request: BlasRequest, resolve: AllocationId => Option[Array[Byte]], ops: Ops[A])(using
      N: Numeric[A]
  ): Either[String, BlasResult] =
    for {
      x  <- request.x.toRight("BLAS request has no x operand")
      xs <- buffer(resolve, x.allocation)
      alpha = scalar(request.alpha, ops)
      _     = if (request.count > 0) {
        var index = 0
        while (index < request.count) {
          val xi = elementOffset(x.offset, indexOf(index, request.count, x.increment), ops)
          ops.write(xs, xi, N.times(alpha, ops.read(xs, xi)))
          index += 1
        }
      }
    } yield BlasResult(Vec.empty)

  private def copy[A](
      request: BlasRequest,
      resolve: AllocationId => Option[Array[Byte]],
      ops: Ops[A]
  ): Either[String, BlasResult] =
    for {
      x  <- request.x.toRight("BLAS request has no x operand")
      y  <- request.y.toRight("BLAS request has no y operand")
      xs <- buffer(resolve, x.allocation)
      ys <- buffer(resolve, y.allocation)
      _ = if (request.count > 0) {
        var index = 0
        while (index < request.count) {
          val xi = elementOffset(x.offset, indexOf(index, request.count, x.increment), ops)
          val yi = elementOffset(y.offset, indexOf(index, request.count, y.increment), ops)
          ops.write(ys, yi, ops.read(xs, xi))
          index += 1
        }
      }
    } yield BlasResult(Vec.empty)

  private def dot[A](request: BlasRequest, resolve: AllocationId => Option[Array[Byte]], ops: Ops[A])(using
      N: Numeric[A]
  ): Either[String, BlasResult] =
    for {
      x  <- request.x.toRight("BLAS request has no x operand")
      y  <- request.y.toRight("BLAS request has no y operand")
      xs <- buffer(resolve, x.allocation)
      ys <- buffer(resolve, y.allocation)
      value = if (request.count <= 0) N.zero
      else {
        var sum   = N.zero
        var index = 0
        while (index < request.count) {
          val xi = elementOffset(x.offset, indexOf(index, request.count, x.increment), ops)
          val yi = elementOffset(y.offset, indexOf(index, request.count, y.increment), ops)
          sum = N.plus(sum, N.times(ops.read(xs, xi), ops.read(ys, yi)))
          index += 1
        }
        sum
      }
    } yield BlasResult(scalarBytes(value, ops))

  private def norm[A](
      request: BlasRequest,
      resolve: AllocationId => Option[Array[Byte]],
      ops: Ops[A]
  ): Either[String, BlasResult] =
    for {
      x  <- request.x.toRight("BLAS request has no x operand")
      xs <- buffer(resolve, x.allocation)
      value = if (request.count <= 0) 0.0
      else {
        var sum   = 0.0
        var index = 0
        while (index < request.count) {
          val at = ops.toDouble(ops.read(xs, elementOffset(x.offset, indexOf(index, request.count, x.increment), ops)))
          sum += at * at
          index += 1
        }
        math.sqrt(sum)
      }
    } yield BlasResult(scalarBytes(ops.fromDouble(value), ops))

  private def absoluteSum[A](request: BlasRequest, resolve: AllocationId => Option[Array[Byte]], ops: Ops[A])(using
      N: Numeric[A]
  ): Either[String, BlasResult] =
    for {
      x  <- request.x.toRight("BLAS request has no x operand")
      xs <- buffer(resolve, x.allocation)
      value = if (request.count <= 0) N.zero
      else {
        var sum   = N.zero
        var index = 0
        while (index < request.count) {
          val at = ops.toDouble(ops.read(xs, elementOffset(x.offset, indexOf(index, request.count, x.increment), ops)))
          sum = N.plus(sum, ops.fromDouble(math.abs(at)))
          index += 1
        }
        sum
      }
    } yield BlasResult(scalarBytes(value, ops))

  private def gemv[A](request: BlasRequest, resolve: AllocationId => Option[Array[Byte]], ops: Ops[A])(using
      N: Numeric[A]
  ): Either[String, BlasResult] =
    for {
      a  <- request.a.toRight("BLAS request has no a operand")
      x  <- request.x.toRight("BLAS request has no x operand")
      y  <- request.y.toRight("BLAS request has no y operand")
      as <- buffer(resolve, a.allocation)
      xs <- buffer(resolve, x.allocation)
      ys <- buffer(resolve, y.allocation)
      alpha  = scalar(request.alpha, ops)
      beta   = scalar(request.beta, ops)
      xCount = if (request.transposeA) request.rows else request.columns
      yCount = if (request.transposeA) request.columns else request.rows
      _      = if (xCount > 0 && yCount > 0) {
        var row = 0
        while (row < yCount) {
          var sum = N.zero
          var col = 0
          while (col < xCount) {
            val matrixIndex = if (request.transposeA) col + row * a.leading else row + col * a.leading
            val matrix      = ops.read(as, elementOffset(a.offset, matrixIndex, ops))
            val vector      = ops.read(xs, elementOffset(x.offset, indexOf(col, xCount, x.increment), ops))
            sum = N.plus(sum, N.times(matrix, vector))
            col += 1
          }
          val output = elementOffset(y.offset, indexOf(row, yCount, y.increment), ops)
          ops.write(
            ys,
            output,
            N.plus(N.times(alpha, sum), if (beta == N.zero) N.zero else N.times(beta, ops.read(ys, output)))
          )
          row += 1
        }
      }
    } yield BlasResult(Vec.empty)

  private def gemm[A](
      request: BlasRequest,
      resolve: AllocationId => Option[Array[Byte]],
      ops: Ops[A],
      aShift: Long,
      bShift: Long,
      cShift: Long
  )(using N: Numeric[A]): Either[String, BlasResult] =
    for {
      a  <- request.a.toRight("BLAS request has no a operand")
      b  <- request.b.toRight("BLAS request has no b operand")
      c  <- request.c.toRight("BLAS request has no c operand")
      as <- buffer(resolve, a.allocation)
      bs <- buffer(resolve, b.allocation)
      cs <- buffer(resolve, c.allocation)
      alpha = scalar(request.alpha, ops)
      beta  = scalar(request.beta, ops)
      aBase = a.offset + aShift * ops.width
      bBase = b.offset + bShift * ops.width
      cBase = c.offset + cShift * ops.width
      _     = if (request.rows > 0 && request.columns > 0 && request.inner > 0) {
        var column = 0
        while (column < request.columns) {
          var row = 0
          while (row < request.rows) {
            var sum   = N.zero
            var inner = 0
            while (inner < request.inner) {
              val aIndex = if (request.transposeA) inner + row * a.leading else row + inner * a.leading
              val bIndex = if (request.transposeB) column + inner * b.leading else inner + column * b.leading
              sum = N.plus(
                sum,
                N.times(
                  ops.read(as, elementOffset(aBase, aIndex, ops)),
                  ops.read(bs, elementOffset(bBase, bIndex, ops))
                )
              )
              inner += 1
            }
            val output = elementOffset(cBase, row + column * c.leading, ops)
            ops.write(
              cs,
              output,
              N.plus(N.times(alpha, sum), if (beta == N.zero) N.zero else N.times(beta, ops.read(cs, output)))
            )
            row += 1
          }
          column += 1
        }
      }
    } yield BlasResult(Vec.empty)

  private def halfToFloat(bits: Int): Float = IEEE754.binary16ToFloat(Binary16.low(bits.toLong))

  private def floatToHalf(value: Float): Int = IEEE754.floatToBinary16(value).value.toInt
}
