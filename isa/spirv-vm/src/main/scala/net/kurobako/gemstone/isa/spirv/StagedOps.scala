package net.kurobako.gemstone.isa.spirv

import Op.*
import VMInvocation.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** Op builders for [[OpBuilder]]: each expansion defines its own op class with the kernel inlined, so running it calls
  * no lane kernel. The scalar form applies when every operand and the result live in the scalar bank. Operand lane
  * counts may differ only where the instruction broadcasts; the shorter operand repeats.
  */
@scala.annotation.publicInBinary
private[isa] object StagedOps {

  class Unary(site: Site, resultType: Int, dest: Int, a: Int) {
    inline def apply(inline kernel: Long => Long): Op = {
      val destination = dest
      val typeId      = resultType
      val source      = a
      if (site.program.scalarSlot(destination) && site.program.scalarSlot(source)) {
        class ScalarOp extends Op {
          def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
            val registers = execution.values.scalars
            registers(destination) = kernel(registers(source))
            execution.pc += 1
            Done
          }
        }
        ScalarOp()
      } else {
        class LanesOp extends Op {
          @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
          def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
            val values = execution.values
            val left   = values.lanes(source)
            if (left == null) Left(undefined(source))
            else {
              val result = Array.ofDim[Long](left.length)
              var lane   = 0
              while (lane < result.length) {
                result(lane) = kernel(left(lane))
                lane += 1
              }
              values.write(destination, typeId, result)
              execution.pc += 1
              Done
            }
          }
        }
        LanesOp()
      }
    }
  }

  /** A unary op whose `count` result lanes repeat a shorter operand. */
  class Broadcast(resultType: Int, dest: Int, a: Int, count: Int) {
    inline def apply(inline kernel: Long => Long): Op = {
      val destination = dest
      val typeId      = resultType
      val source      = a
      val lanes       = count
      class LanesOp extends Op {
        @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
        def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
          val values = execution.values
          val left   = values.lanes(source)
          if (left == null) Left(undefined(source))
          else {
            val result = Array.ofDim[Long](lanes)
            var lane   = 0
            while (lane < result.length) {
              result(lane) = kernel(left(lane % left.length))
              lane += 1
            }
            values.write(destination, typeId, result)
            execution.pc += 1
            Done
          }
        }
      }
      LanesOp()
    }
  }

  /** `count` result lanes from the first lane of one operand, the kernel's second operand being the lane index. */
  class Indexed(resultType: Int, dest: Int, a: Int, count: Int) {
    inline def apply(inline kernel: (Long, Long) => Long): Op = {
      val destination = dest
      val typeId      = resultType
      val source      = a
      val lanes       = count
      class LanesOp extends Op {
        @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
        def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
          val values = execution.values
          val x      = values.lanes(source)
          if (x == null) Left(undefined(source))
          else {
            val result = Array.ofDim[Long](lanes)
            var lane   = 0
            while (lane < result.length) {
              result(lane) = kernel(x(0), lane.toLong)
              lane += 1
            }
            values.write(destination, typeId, result)
            execution.pc += 1
            Done
          }
        }
      }
      LanesOp()
    }
  }

  class Binary(site: Site, resultType: Int, dest: Int, a: Int, b: Int) {
    inline def apply(inline kernel: (Long, Long) => Long): Op = {
      val destination = dest
      val typeId      = resultType
      val first       = a
      val second      = b
      if (site.program.scalarSlot(destination) && site.program.scalarSlot(first) && site.program.scalarSlot(second)) {
        class ScalarOp extends Op {
          def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
            val registers = execution.values.scalars
            registers(destination) = kernel(registers(first), registers(second))
            execution.pc += 1
            Done
          }
        }
        ScalarOp()
      } else {
        class LanesOp extends Op {
          @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
          def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
            val values = execution.values
            val left   = values.lanes(first)
            val right  = values.lanes(second)
            if (left == null) Left(undefined(first))
            else if (right == null) Left(undefined(second))
            else {
              val result = Array.ofDim[Long](math.max(left.length, right.length))
              var lane   = 0
              if (left.length == right.length)
                while (lane < result.length) {
                  result(lane) = kernel(left(lane), right(lane))
                  lane += 1
                }
              else
                while (lane < result.length) {
                  result(lane) = kernel(left(lane % left.length), right(lane % right.length))
                  lane += 1
                }
              values.write(destination, typeId, result)
              execution.pc += 1
              Done
            }
          }
        }
        LanesOp()
      }
    }
  }

  class Ternary(site: Site, resultType: Int, dest: Int, a: Int, b: Int, c: Int) {
    inline def apply(inline kernel: (Long, Long, Long) => Long): Op = {
      val destination = dest
      val typeId      = resultType
      val first       = a
      val second      = b
      val third       = c
      val program     = site.program
      if (
        program.scalarSlot(destination) && program.scalarSlot(first) && program.scalarSlot(second) &&
        program.scalarSlot(third)
      ) {
        class ScalarOp extends Op {
          def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
            val registers = execution.values.scalars
            registers(destination) = kernel(registers(first), registers(second), registers(third))
            execution.pc += 1
            Done
          }
        }
        ScalarOp()
      } else {
        class LanesOp extends Op {
          @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
          def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
            val values = execution.values
            val x      = values.lanes(first)
            val y      = values.lanes(second)
            val z      = values.lanes(third)
            if (x == null) Left(undefined(first))
            else if (y == null) Left(undefined(second))
            else if (z == null) Left(undefined(third))
            else {
              val result = Array.ofDim[Long](math.max(x.length, math.max(y.length, z.length)))
              var lane   = 0
              if (x.length == result.length && y.length == result.length && z.length == result.length)
                while (lane < result.length) {
                  result(lane) = kernel(x(lane), y(lane), z(lane))
                  lane += 1
                }
              else
                while (lane < result.length) {
                  result(lane) = kernel(x(lane % x.length), y(lane % y.length), z(lane % z.length))
                  lane += 1
                }
              values.write(destination, typeId, result)
              execution.pc += 1
              Done
            }
          }
        }
        LanesOp()
      }
    }
  }

  class Quaternary(site: Site, resultType: Int, dest: Int, a: Int, b: Int, c: Int, d: Int) {
    inline def apply(inline kernel: (Long, Long, Long, Long) => Long): Op = {
      val destination = dest
      val typeId      = resultType
      val first       = a
      val second      = b
      val third       = c
      val fourth      = d
      val program     = site.program
      if (
        program.scalarSlot(destination) && program.scalarSlot(first) && program.scalarSlot(second) &&
        program.scalarSlot(third) && program.scalarSlot(fourth)
      ) {
        class ScalarOp extends Op {
          def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
            val registers = execution.values.scalars
            registers(destination) = kernel(registers(first), registers(second), registers(third), registers(fourth))
            execution.pc += 1
            Done
          }
        }
        ScalarOp()
      } else {
        class LanesOp extends Op {
          @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
          def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
            val values = execution.values
            val w      = values.lanes(first)
            val x      = values.lanes(second)
            val y      = values.lanes(third)
            val z      = values.lanes(fourth)
            if (w == null) Left(undefined(first))
            else if (x == null) Left(undefined(second))
            else if (y == null) Left(undefined(third))
            else if (z == null) Left(undefined(fourth))
            else {
              val result = Array.ofDim[Long](math.max(math.max(w.length, x.length), math.max(y.length, z.length)))
              var lane   = 0
              while (lane < result.length) {
                result(lane) = kernel(w(lane % w.length), x(lane % x.length), y(lane % y.length), z(lane % z.length))
                lane += 1
              }
              values.write(destination, typeId, result)
              execution.pc += 1
              Done
            }
          }
        }
        LanesOp()
      }
    }
  }

  /** Two results per lane of a `count`-lane operand, stored as one structure of the first results then the second. */
  class UnaryStructure(resultType: Int, dest: Int, a: Int, count: Int) {
    inline def apply(inline first: Long => Long, inline second: Long => Long): Op = {
      val destination = dest
      val typeId      = resultType
      val source      = a
      val lanes       = count
      class PairOp extends Op {
        @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
        def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
          val values = execution.values
          val x      = values.lanes(source)
          if (x == null) Left(undefined(source))
          else {
            val result = Array.ofDim[Long](lanes * 2)
            var lane   = 0
            while (lane < lanes) {
              result(lane) = first(x(lane))
              result(lanes + lane) = second(x(lane))
              lane += 1
            }
            values.update(destination, VMValue.Bits(typeId, result))
            execution.pc += 1
            Done
          }
        }
      }
      PairOp()
    }
  }

  /** As [[UnaryStructure]] over two `count`-lane operands; other lane counts raise `mismatch`. */
  class BinaryStructure(resultType: Int, dest: Int, a: Int, b: Int, count: Int, mismatch: Fault) {
    inline def apply(inline first: (Long, Long) => Long, inline second: (Long, Long) => Long): Op = {
      val destination = dest
      val typeId      = resultType
      val left        = a
      val right       = b
      val lanes       = count
      val fault       = mismatch
      class PairOp extends Op {
        @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
        def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
          val values = execution.values
          val x      = values.lanes(left)
          val y      = values.lanes(right)
          if (x == null || y == null || x.length != lanes || y.length != lanes) Left(fault)
          else {
            val result = Array.ofDim[Long](lanes * 2)
            var lane   = 0
            while (lane < lanes) {
              result(lane) = first(x(lane), y(lane))
              result(lanes + lane) = second(x(lane), y(lane))
              lane += 1
            }
            values.update(destination, VMValue.Bits(typeId, result))
            execution.pc += 1
            Done
          }
        }
      }
      PairOp()
    }
  }

  /** A pointer operand whose pointee, of the static type `pointee` or one of its shape, receives the second results of
    * a pair encoded as its leaves.
    */
  class PointerStore(program: VMProgram, opcode: String, pointer: Int, pointee: Int) {
    private val shape         = program.numericShape(pointee)
    private val untypedTarget = untypedPointer(program, pointer)

    // an untyped pointer stores what its value points at, which an untyped access chain gives it
    def locate(values: InvocationValues, lanes: Int): Either[Fault, Pointer] = address(values, pointer).flatMap {
      location =>
        val untyped = untypedTarget && !program.untyped(location.pointee)
        Either.cond(
          if (untyped) program.numericShape(location.pointee).exists(_._2 == lanes)
          else (location.pointee == pointee || program.numericShape(location.pointee) == shape) &&
          shape.exists(_._2 == lanes),
          location,
          Fault.InvalidRequest(s"$opcode pointer %$pointer does not point to a value of the operand's shape")
        )
    }

    def store(memory: MemoryState, pointer: Pointer, lanes: Array[Long]): Either[Fault, Unit] =
      program.encode(VMValue.Bits(pointer.pointee, lanes)).flatMap(memory.write(pointer, _))

    /** Stores `stored` through `pointer`, then writes `result` to `destination` and moves on. */
    def complete(
        execution: InvocationState,
        memory: MemoryState,
        pointer: Pointer,
        stored: Array[Long],
        typeId: Int,
        destination: Int,
        result: Array[Long]
    ): Either[Fault, Unit] = store(memory, pointer, stored).map { _ =>
      execution.values.write(destination, typeId, result)
      execution.pc += 1
    }
  }

  /** Faults before `field` runs when `inRange` of the scalar offset and count is 0. */
  class FieldRange(program: VMProgram, offset: Int, count: Int, field: Op) {
    inline def apply(inline inRange: (Long, Long) => Long): Op = {
      val at      = offset
      val bits    = count
      val inside  = field
      val scalars = program.scalarSlot(offset) && program.scalarSlot(count)
      class RangeOp extends Op {
        def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
          if (scalars) {
            val registers = execution.values.scalars
            if (inRange(registers(at), registers(bits)) == 0L)
              Left(Fault.InvalidRequest("bit-field range exceeds component width"))
            else inside.run(execution, memory)
          } else
            scalar(execution.values, at) match {
              case Left(error)  => Left(error)
              case Right(start) =>
                scalar(execution.values, bits) match {
                  case Left(error)   => Left(error)
                  case Right(length) =>
                    if (inRange(start, length) == 0L)
                      Left(Fault.InvalidRequest("bit-field range exceeds component width"))
                    else inside.run(execution, memory)
                }
            }
      }
      RangeOp()
    }
  }

  /** A float-to-unsigned conversion that faults on the first lane of operand `a` that `representable` maps to 0. */
  class UnsignedUnary(
      site: Site,
      resultType: Int,
      dest: Int,
      a: Int,
      width: Int in (16, 32, 64),
      resultWidth: Int in 1 ~ 64,
      mode: IEEE754.Rounding
  ) {
    def failure(lane: Int, raw: Long): Fault = Fault.InvalidRequest(
      s"OpConvertFToU source lane $lane cannot be represented as an unsigned $resultWidth-bit integer: " +
        ConversionSemantics.unrepresentable(
          ConversionSemantics.integral(IEEE754.toDouble(raw, width), mode),
          resultWidth
        )
    )

    inline def apply(inline representable: Long => Long, inline kernel: Long => Long): Op = {
      val destination = dest
      val typeId      = resultType
      val source      = a
      val fault       = this
      if (site.program.scalarSlot(destination) && site.program.scalarSlot(source)) {
        class ScalarOp extends Op {
          def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
            val registers = execution.values.scalars
            val raw       = registers(source)
            if (representable(raw) == 0L) Left(fault.failure(0, raw))
            else {
              registers(destination) = kernel(raw)
              execution.pc += 1
              Done
            }
          }
        }
        ScalarOp()
      } else {
        class LanesOp extends Op {
          @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
          def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
            val values = execution.values
            val left   = values.lanes(source)
            if (left == null) Left(undefined(source))
            else {
              val result = Array.ofDim[Long](left.length)
              var lane   = 0
              while (lane < result.length && representable(left(lane)) != 0L) {
                result(lane) = kernel(left(lane))
                lane += 1
              }
              if (lane < result.length) Left(fault.failure(lane, left(lane)))
              else {
                values.write(destination, typeId, result)
                execution.pc += 1
                Done
              }
            }
          }
        }
        LanesOp()
      }
    }
  }

  /** Two results per lane: the first are the result, and `target` stores the second through a pointer. */
  class UnaryStore(resultType: Int, dest: Int, a: Int, target: PointerStore) {
    inline def apply(inline first: Long => Long, inline second: Long => Long): Op = {
      val destination = dest
      val typeId      = resultType
      val source      = a
      val output      = target
      class StoreOp extends Op {
        @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
        def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
          val values = execution.values
          val x      = values.lanes(source)
          if (x == null) Left(undefined(source))
          else output.locate(values, x.length) match {
            case Left(error)    => Left(error)
            case Right(pointer) =>
              val result = Array.ofDim[Long](x.length)
              val stored = Array.ofDim[Long](x.length)
              var lane   = 0
              while (lane < x.length) {
                result(lane) = first(x(lane))
                stored(lane) = second(x(lane))
                lane += 1
              }
              output.complete(
                execution,
                memory,
                pointer,
                stored = stored,
                typeId = typeId,
                destination = destination,
                result = result
              )
          }
        }
      }
      StoreOp()
    }
  }

  class BinaryStore(resultType: Int, dest: Int, a: Int, b: Int, target: PointerStore) {
    inline def apply(inline first: (Long, Long) => Long, inline second: (Long, Long) => Long): Op = {
      val destination = dest
      val typeId      = resultType
      val left        = a
      val right       = b
      val output      = target
      class StoreOp extends Op {
        @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
        def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
          val values = execution.values
          val x      = values.lanes(left)
          val y      = values.lanes(right)
          if (x == null) Left(undefined(left))
          else if (y == null) Left(undefined(right))
          else output.locate(values, x.length) match {
            case Left(error)    => Left(error)
            case Right(pointer) =>
              val result = Array.ofDim[Long](x.length)
              val stored = Array.ofDim[Long](x.length)
              var lane   = 0
              while (lane < x.length) {
                result(lane) = first(x(lane), y(lane))
                stored(lane) = second(x(lane), y(lane))
                lane += 1
              }
              output.complete(
                execution,
                memory,
                pointer,
                stored = stored,
                typeId = typeId,
                destination = destination,
                result = result
              )
          }
        }
      }
      StoreOp()
    }
  }

  /** A reduction over the lanes of one operand into a scalar. */
  class Fold(resultType: Int, dest: Int, a: Int) {
    inline def apply(inline initial: Long, inline step: (Long, Long) => Long, inline finish: Long => Long): Op = {
      val destination = dest
      val typeId      = resultType
      val source      = a
      class FoldOp extends Op {
        @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
        def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
          val values = execution.values
          val x      = values.lanes(source)
          if (x == null) Left(undefined(source))
          else {
            var accumulator = initial
            var lane        = 0
            while (lane < x.length) {
              accumulator = step(accumulator, x(lane))
              lane += 1
            }
            values.updateScalar(destination, typeId, finish(accumulator))
            execution.pc += 1
            Done
          }
        }
      }
      FoldOp()
    }
  }

  /** A reduction over the paired lanes of two operands into a scalar, for as many lanes as the first has. */
  class BinaryFold(resultType: Int, dest: Int, a: Int, b: Int) {
    inline def apply(inline initial: Long, inline step: (Long, Long, Long) => Long, inline finish: Long => Long): Op = {
      val destination = dest
      val typeId      = resultType
      val left        = a
      val right       = b
      class FoldOp extends Op {
        @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
        def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
          val values = execution.values
          val x      = values.lanes(left)
          val y      = values.lanes(right)
          if (x == null) Left(undefined(left))
          else if (y == null) Left(undefined(right))
          else {
            var accumulator = initial
            var lane        = 0
            while (lane < x.length) {
              accumulator = step(accumulator, x(lane), y(lane))
              lane += 1
            }
            values.updateScalar(destination, typeId, finish(accumulator))
            execution.pc += 1
            Done
          }
        }
      }
      FoldOp()
    }
  }

  /** As [[BinaryFold]], finished with a third, scalar operand. */
  class AccumulatedFold(resultType: Int, dest: Int, a: Int, b: Int, c: Int) {
    inline def apply(
        inline initial: Long,
        inline step: (Long, Long, Long) => Long,
        inline finish: (Long, Long) => Long
    ): Op = {
      val destination = dest
      val typeId      = resultType
      val left        = a
      val right       = b
      val third       = c
      class FoldOp extends Op {
        @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
        def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
          val values                                                   = execution.values
          val x                                                        = values.lanes(left)
          val y                                                        = values.lanes(right)
          def accumulated(addend: Long): AddressSpace.ReadResult[Unit] = {
            var accumulator = initial
            var lane        = 0
            while (lane < x.length) {
              accumulator = step(accumulator, x(lane), y(lane))
              lane += 1
            }
            values.updateScalar(destination, typeId, finish(accumulator, addend))
            execution.pc += 1
            Done
          }
          if (x == null) Left(undefined(left))
          else if (y == null) Left(undefined(right))
          else if (values.program.scalarSlot(third)) accumulated(values.scalars(third))
          else {
            val z = values.lanes(third)
            if (z == null) Left(undefined(third)) else accumulated(z(0))
          }
        }
      }
      FoldOp()
    }
  }

  /** Where each lane of a matrix product reads its operands: lane `row + rows * column` folds `inner` steps, step `k`
    * reading left lane `row * leftRow + k * leftInner` and right lane `k * rightInner + column * rightColumn`.
    */
  case class Product(
      rows: Int,
      columns: Int,
      inner: Int,
      leftRow: Int,
      leftInner: Int,
      rightInner: Int,
      rightColumn: Int,
      leftLanes: Int,
      rightLanes: Int
  )

  /** A matrix product whose lanes fold the step from zero; operands of other lane counts raise `mismatch`. */
  class MatrixProduct(resultType: Int, dest: Int, a: Int, b: Int, shape: Product, mismatch: Fault) {
    inline def apply(inline step: (Long, Long, Long) => Long): Op = {
      val destination = dest
      val typeId      = resultType
      val left        = a
      val right       = b
      val layout      = shape
      val fault       = mismatch
      class ProductOp extends Op {
        @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
        def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
          val values = execution.values
          val x      = values.lanes(left)
          val y      = values.lanes(right)
          if (x == null) Left(undefined(left))
          else if (y == null) Left(undefined(right))
          else if (x.length != layout.leftLanes || y.length != layout.rightLanes) Left(fault)
          else {
            val result = Array.ofDim[Long](layout.rows * layout.columns)
            var lane   = 0
            while (lane < result.length) {
              val row         = lane % layout.rows
              val column      = lane / layout.rows
              var accumulator = 0L
              var k           = 0
              while (k < layout.inner) {
                accumulator = step(
                  accumulator,
                  x(row * layout.leftRow + k * layout.leftInner),
                  y(k * layout.rightInner + column * layout.rightColumn)
                )
                k += 1
              }
              result(lane) = accumulator
              lane += 1
            }
            values.update(destination, VMValue.Bits(typeId, result))
            execution.pc += 1
            Done
          }
        }
      }
      ProductOp()
    }
  }

  /** A cooperative matrix multiply-accumulate over row-major lanes of `rows` by `inner` and `inner` by `columns`
    * operands; operands of other lane counts fault.
    */
  class CooperativeProduct(resultType: Int, dest: Int, a: Int, b: Int, c: Int, rows: Int, columns: Int, inner: Int) {
    inline def apply(
        inline initial: Long,
        inline step: (Long, Long, Long) => Long,
        inline finish: (Long, Long) => Long
    ): Op = {
      val destination = dest
      val typeId      = resultType
      val left        = a
      val right       = b
      val addend      = c
      val height      = rows
      val width       = columns
      val depth       = inner
      class ProductOp extends Op {
        @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
        def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
          val values = execution.values
          val x      = values.lanes(left)
          val y      = values.lanes(right)
          val z      = values.lanes(addend)
          if (x == null) Left(undefined(left))
          else if (y == null) Left(undefined(right))
          else if (z == null) Left(undefined(addend))
          else if (x.length != height * depth || y.length != depth * width || z.length != height * width)
            Left(Fault.InvalidRequest("cooperative matrix multiply-accumulate operands do not agree in shape"))
          else {
            val result = Array.ofDim[Long](height * width)
            var lane   = 0
            while (lane < result.length) {
              val row         = lane / width
              val column      = lane % width
              var accumulator = initial
              var k           = 0
              while (k < depth) {
                accumulator = step(accumulator, x(row * depth + k), y(k * width + column))
                k += 1
              }
              result(lane) = finish(accumulator, z(lane))
              lane += 1
            }
            values.update(destination, VMValue.Bits(typeId, result))
            execution.pc += 1
            Done
          }
        }
      }
      ProductOp()
    }
  }

  /** The outer product of a `rows`-lane and a `columns`-lane operand, column by column. */
  class OuterProduct(resultType: Int, dest: Int, a: Int, b: Int, rows: Int, columns: Int, mismatch: Fault) {
    inline def apply(inline kernel: (Long, Long) => Long): Op = {
      val destination = dest
      val typeId      = resultType
      val left        = a
      val right       = b
      val height      = rows
      val width       = columns
      val fault       = mismatch
      class ProductOp extends Op {
        @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
        def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
          val values = execution.values
          val x      = values.lanes(left)
          val y      = values.lanes(right)
          if (x == null) Left(undefined(left))
          else if (y == null) Left(undefined(right))
          else if (x.length != height || y.length != width) Left(fault)
          else {
            val result = Array.ofDim[Long](height * width)
            var lane   = 0
            while (lane < result.length) {
              result(lane) = kernel(x(lane % height), y(lane / height))
              lane += 1
            }
            values.update(destination, VMValue.Bits(typeId, result))
            execution.pc += 1
            Done
          }
        }
      }
      ProductOp()
    }
  }

  /** A cross product: result lane `i` of three from lanes `i + 1` and `i + 2` of the operands, modulo three, in `count`
    * lanes; a fourth lane is 0.
    */
  class Cross(resultType: Int, dest: Int, a: Int, b: Int, count: Int) {
    inline def apply(inline kernel: (Long, Long, Long, Long) => Long): Op = {
      val destination = dest
      val typeId      = resultType
      val left        = a
      val right       = b
      val lanes       = count
      class CrossOp extends Op {
        @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
        def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
          val values = execution.values
          val x      = values.lanes(left)
          val y      = values.lanes(right)
          if (x == null) Left(undefined(left))
          else if (y == null) Left(undefined(right))
          else {
            val result = Array.ofDim[Long](lanes)
            var lane   = 0
            while (lane < 3) {
              val next  = (lane + 1) % 3
              val after = (lane + 2) % 3
              result(lane) = kernel(x(next), y(after), x(after), y(next))
              lane += 1
            }
            values.update(destination, VMValue.Bits(typeId, result))
            execution.pc += 1
            Done
          }
        }
      }
      CrossOp()
    }
  }

  /** A fold over the lanes of `dotted` and `b`, then each result lane from the fold, the lanes of `a` and `b` and the
    * first lane of `c`, or 0 when `c` is negative.
    */
  class Projection(resultType: Int, dest: Int, a: Int, b: Int, c: Int, dotted: Int) {
    inline def apply(
        inline initial: Long,
        inline step: (Long, Long, Long) => Long,
        inline element: (Long, Long, Long, Long) => Long
    ): Op = {
      val destination = dest
      val typeId      = resultType
      val first       = a
      val second      = b
      val third       = c
      val folded      = dotted
      class ProjectionOp extends Op {
        @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
        def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
          val values = execution.values
          val x      = values.lanes(first)
          val y      = values.lanes(second)
          val z      = if (third < 0) null else values.lanes(third)
          val d      = values.lanes(folded)
          if (x == null) Left(undefined(first))
          else if (y == null) Left(undefined(second))
          else if (third >= 0 && z == null) Left(undefined(third))
          else if (d == null) Left(undefined(folded))
          else {
            var sum  = initial
            var lane = 0
            while (lane < d.length) {
              sum = step(sum, d(lane), y(lane))
              lane += 1
            }
            val scalar = if (z == null) 0L else z(0)
            val result = Array.ofDim[Long](x.length)
            lane = 0
            while (lane < result.length) {
              result(lane) = element(sum, x(lane), y(lane), scalar)
              lane += 1
            }
            values.update(destination, VMValue.Bits(typeId, result))
            execution.pc += 1
            Done
          }
        }
      }
      ProjectionOp()
    }
  }

  /** A normalisation: two folds over the lanes of `a`, then each result lane from its lane and both folds. */
  class Normalised(resultType: Int, dest: Int, a: Int) {
    inline def apply(
        inline scale: (Long, Long) => Long,
        inline norm: (Long, Long, Long) => Long,
        inline element: (Long, Long, Long) => Long
    ): Op = {
      val destination = dest
      val typeId      = resultType
      val source      = a
      class NormalisedOp extends Op {
        @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
        def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
          val values = execution.values
          val x      = values.lanes(source)
          if (x == null) Left(undefined(source))
          else {
            var largest = 0L
            var lane    = 0
            while (lane < x.length) {
              largest = scale(largest, x(lane))
              lane += 1
            }
            var length = 0L
            lane = 0
            while (lane < x.length) {
              length = norm(length, x(lane), largest)
              lane += 1
            }
            val result = Array.ofDim[Long](x.length)
            lane = 0
            while (lane < result.length) {
              result(lane) = element(x(lane), largest, length)
              lane += 1
            }
            values.update(destination, VMValue.Bits(typeId, result))
            execution.pc += 1
            Done
          }
        }
      }
      NormalisedOp()
    }
  }

  // Uniform includes legacy BufferBlock storage buffers, alongside StorageBuffer.
  val atomicStorage: Set[Int] = Set(
    StorageClass.Uniform,
    StorageClass.Workgroup,
    StorageClass.CrossWorkgroup,
    StorageClass.Generic,
    StorageClass.AtomicCounter,
    StorageClass.Image,
    StorageClass.StorageBuffer,
    StorageClass.PhysicalStorageBuffer
  )

  /** Whether `pointer` has an untyped pointer type, whose accesses take their type from the instruction. */
  def untypedPointer(program: VMProgram, pointer: Int): Boolean =
    program.runtimeType(program.valueType(pointer)).exists {
      case RuntimeType.Pointer(_, pointee) => program.untyped(pointee)
      case _                               => false
    }

  def target(values: InvocationValues, pointer: Int, typeId: Int, opcode: String): Either[Fault, Pointer] =
    for {
      location <- address(values, pointer)
      _        <- Either.cond(
        location.pointee == typeId || untypedPointer(values.program, pointer),
        (),
        Fault.InvalidRequest(s"$opcode pointer pointee %${location.pointee} does not match type %$typeId")
      )
      _ <- Either.cond(
        atomicStorage.contains(location.storageClass),
        (),
        Fault.InvalidRequest(s"$opcode pointer storage class ${location.storageClass} does not support atomic access")
      )
    } yield location

  /** Where a read-modify-write lands: a pointer to the result type in an atomic storage class, or in any storage class
    * for integer operations, checked with its memory scope and semantics on every access.
    */
  class Target(
      val opcode: String,
      val resultType: Int,
      pointer: Int,
      val scope: Int,
      semantics: Int,
      val bytes: Int in 1 ~ 8,
      val bits: Long,
      anyStorage: Boolean
  ) {
    def locate(values: InvocationValues): Either[Fault, Pointer] = {
      val located =
        if (!anyStorage) target(values, pointer, resultType, opcode)
        else address(values, pointer).flatMap(location =>
          Either.cond(
            location.pointee == resultType || untypedPointer(values.program, pointer),
            location,
            Fault.InvalidRequest(
              s"$opcode pointer pointee %${location.pointee} does not match result type %$resultType"
            )
          )
        )
      located.flatMap(location => memoryControls(values, scope, semantics, opcode).map(_ => location))
    }
  }

  /** A read-modify-write storing the kernel of the previous value and a scalar operand. */
  class Modify(access: Target, dest: Int, value: Int) {
    inline def apply(inline kernel: (Long, Long) => Long): Op = {
      val target      = access
      val destination = dest
      val source      = value
      class ModifyOp extends Op {
        def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
          val values  = execution.values
          val operand = values.scalars(source)
          target.locate(values) match {
            case Left(error)     => Left(error)
            case Right(location) =>
              memory.atomicModifyScalar(location, target.bytes)(previous => kernel(previous, operand)) match {
                case Left(error)     => Left(error)
                case Right(previous) =>
                  values.updateScalar(destination, target.resultType, previous & target.bits)
                  execution.pc += 1
                  Done
              }
          }
        }
      }
      ModifyOp()
    }
  }

  /** A read-modify-write storing the kernel of the previous value alone. */
  class Update(access: Target, dest: Int) {
    inline def apply(inline kernel: Long => Long): Op = {
      val target      = access
      val destination = dest
      class UpdateOp extends Op {
        def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
          val values = execution.values
          target.locate(values) match {
            case Left(error)     => Left(error)
            case Right(location) =>
              memory.atomicModifyScalar(location, target.bytes)(kernel(_)) match {
                case Left(error)     => Left(error)
                case Right(previous) =>
                  values.updateScalar(destination, target.resultType, previous & target.bits)
                  execution.pc += 1
                  Done
              }
          }
        }
      }
      UpdateOp()
    }
  }

  /** A compare-exchange over the previous value, the value and the comparator; the unequal memory semantics are checked
    * alongside the target's.
    */
  class Exchange(access: Target, unequal: Int, dest: Int, value: Int, comparator: Int) {
    inline def apply(inline kernel: (Long, Long, Long) => Long): Op = {
      val target      = access
      val destination = dest
      val desired     = value
      val expected    = comparator
      val semantics   = unequal
      class ExchangeOp extends Op {
        def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
          val values  = execution.values
          val update  = values.scalars(desired)
          val against = values.scalars(expected)
          target.locate(values) match {
            case Left(error)     => Left(error)
            case Right(location) =>
              memoryControls(values, target.scope, semantics, target.opcode) match {
                case Left(error) => Left(error)
                case Right(_)    =>
                  memory.atomicModifyScalar(location, target.bytes)(previous =>
                    kernel(previous, update, against)
                  ) match {
                    case Left(error)     => Left(error)
                    case Right(previous) =>
                      values.updateScalar(destination, target.resultType, previous & target.bits)
                      execution.pc += 1
                      Done
                  }
              }
          }
        }
      }
      ExchangeOp()
    }
  }

  /** The addresses of two pointers of one type outside PhysicalStorageBuffer, or the fault their values raise. */
  def addresses(program: VMProgram, opcode: String, values: InvocationValues, left: Int, right: Int)
      : Either[Fault, (Pointer, Pointer)] = (values.get(left), values.get(right)) match {
    case (Some(VMValue.Address(leftType, a)), Some(VMValue.Address(rightType, b)))
        if leftType == rightType && program.runtimeType(leftType).exists {
          case RuntimeType.Pointer(storageClass, _) => storageClass != StorageClass.PhysicalStorageBuffer
          case _                                    => false
        } =>
      Right((a, b))
    case (None, _) => Left(undefined(left))
    case (_, None) => Left(undefined(right))
    case _         => Left(Fault.InvalidRequest(s"$opcode requires matching non-PhysicalStorageBuffer pointer types"))
  }

  /** OpPtrEqual or OpPtrNotEqual over the addresses of its operands. */
  class PointerEquality(program: VMProgram, opcode: String, ids: OperandIds) {
    inline def apply(inline kernel: (Long, Long) => Long): Op = {
      val name        = opcode
      val typeId      = ids.resultType
      val destination = ids.result
      val first       = ids(0)
      val second      = ids(1)
      val boolean     = program.runtimeType(typeId).contains(RuntimeType.Bool)
      step { (execution, _) =>
        val values = execution.values
        addresses(program, name, values, first, second).flatMap { (a, b) =>
          if (boolean) Right(values.updateScalar(destination, typeId, kernel(a.address, b.address)))
          else Left(Fault.InvalidRequest("pointer equality requires a scalar Boolean result"))
        }
      }
    }
  }

  /** OpPtrDiff over the addresses of its operands and the stride of their element type. */
  class PointerDifference(program: VMProgram, ids: OperandIds) {
    inline def apply(inline kernel: (Long, Long, Long) => Long): Op = {
      val typeId      = ids.resultType
      val destination = ids.result
      val first       = ids(0)
      val second      = ids(1)
      step { (execution, _) =>
        val values = execution.values
        addresses(program, "OpPtrDiff", values, first, second).flatMap { (a, b) =>
          val element = program.storageTypeSize(a.pointee, a.matrixLayout).raw
          val stride  = program.arrayStrides.get(program.valueType(first)).fold(element)(_.raw)
          if (stride == 0) Left(Fault.InvalidRequest("OpPtrDiff requires a nonempty element type"))
          else Right(values.updateScalar(destination, typeId, kernel(a.address, b.address, stride.toLong)))
        }
      }
    }
  }

  /** A load of `count` halves, `stride` halves apart per offset unit, each converted by the kernel. */
  class HalfLoad(resultType: Int, dest: Int, offset: Int, pointer: Int, count: Int, stride: Int) {
    inline def apply(inline convert: Long => Long): Op = {
      val destination = dest
      val typeId      = resultType
      val index       = offset
      val source      = pointer
      val lanes       = count
      val spacing     = stride
      class LoadOp extends Op {
        @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
        def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
          val values = execution.values
          scalar(values, index) match {
            case Left(error) => Left(error)
            case Right(at)   =>
              address(values, source) match {
                case Left(error)                                                                         => Left(error)
                case Right(location) if displacementOverflows(location.address, at, spacing.toLong * 2L) =>
                  Left(displacementFault(location.address, at, spacing.toLong * 2L))
                case Right(location) =>
                  val base                                              = location.address + at * spacing.toLong * 2L
                  val result                                            = Array.ofDim[Long](lanes)
                  var failure: Fault | AddressSpace.Pending.type | Null = null
                  var lane                                              = 0
                  while (lane < lanes && (failure eq null)) {
                    memory.readScalar(location.copy(address = base + lane.toLong * 2L), 2) match {
                      case Left(error) => failure = error
                      case Right(half) => result(lane) = convert(half)
                    }
                    lane += 1
                  }
                  if (failure ne null) Left(failure)
                  else {
                    values.write(destination, typeId, result)
                    execution.pc += 1
                    Done
                  }
              }
          }
        }
      }
      LoadOp()
    }
  }

  /** A store of `count` lanes, each converted by the kernel to a half, `stride` halves apart per offset unit. */
  class HalfStore(data: Int, offset: Int, pointer: Int, count: Int, stride: Int) {
    inline def apply(inline convert: Long => Long): Op = {
      val source  = data
      val index   = offset
      val target  = pointer
      val lanes   = count
      val spacing = stride
      class StoreOp extends Op {
        @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
        def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
          val values = execution.values
          val x      = values.lanes(source)
          if (x == null) Left(undefined(source))
          else
            scalar(values, index) match {
              case Left(error) => Left(error)
              case Right(at)   =>
                address(values, target) match {
                  case Left(error) => Left(error)
                  case Right(location) if displacementOverflows(location.address, at, spacing.toLong * 2L) =>
                    Left(displacementFault(location.address, at, spacing.toLong * 2L))
                  case Right(location) =>
                    val base                  = location.address + at * spacing.toLong * 2L
                    var failure: Fault | Null = null
                    var lane                  = 0
                    while (lane < lanes && failure == null) {
                      memory.writeScalar(location.copy(address = base + lane.toLong * 2L), convert(x(lane)), 2) match {
                        case Left(error) => failure = error
                        case Right(_)    => ()
                      }
                      lane += 1
                    }
                    if (failure != null) Left(failure)
                    else {
                      execution.pc += 1
                      Done
                    }
                }
            }
        }
      }
      StoreOp()
    }
  }
}
