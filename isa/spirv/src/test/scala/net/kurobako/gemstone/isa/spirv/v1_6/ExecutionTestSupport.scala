package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Vec

import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.binary.ScalarByteWidth
import net.kurobako.gemstone.concurrent.withLock
import net.kurobako.gemstone.execution.ExecutionFailure
import net.kurobako.gemstone.execution.ExecutionControl
import net.kurobako.gemstone.execution.ExecutionLimits
import net.kurobako.gemstone.execution.ExecutionMemory
import net.kurobako.gemstone.execution.AllocationId
import net.kurobako.gemstone.execution.Dimensions
import net.kurobako.gemstone.execution.KernelArgument
import net.kurobako.gemstone.execution.KernelDispatch
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import net.kurobako.gemstone.execution.PointerRelocation
import net.kurobako.gemstone.execution.ProgramId
import net.kurobako.gemstone.memory.MemoryValueByteWidth

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock

private trait ExecutionTestSupport { self: munit.FunSuite =>
  protected final def testDimensions(
      x: Int :| Positive,
      y: Int :| Positive = 1,
      z: Int :| Positive = 1
  ): Dimensions = Dimensions(x, y, z)

  protected final class ArrayMemory(
      expectedId: AllocationId,
      size: Int,
      relocation: PointerRelocation => Unit = _ => (),
      written: (Long, Int) => Unit = (_, _) => ()
  ) extends ExecutionMemory {
    private val content    = AtomicReference(Vec.fill(size)(0.toByte))
    private val atomicLock = ReentrantLock()

    def bytes: Vec[Byte] = content.get()

    override def read(
        id: AllocationId,
        offset: Long :| Positive0,
        size: Int :| Positive0
    ): Either[ExecutionFailure, Vec[Byte]] =
      checked(id, offset, size).map(start => content.get().slice(start, start + size))

    override def write(
        id: AllocationId,
        offset: Long :| Positive0,
        bytes: Vec[Byte]
    ): Either[ExecutionFailure, Unit] =
      // Collection sizes are non-negative by construction.
      checked(id, offset, bytes.size.refineUnsafe[Positive0]) match {
        case Left(error)  => Left(error)
        case Right(start) =>
          written(offset, bytes.size)
          content.updateAndGet(current =>
            Vec.from(current.take(start).iterator ++ bytes.iterator ++ current.drop(start + bytes.size))
          )
          Right(())
      }

    override def recordPointerRelocation(value: PointerRelocation): Unit = relocation(value)

    override def atomicModifyValue(
        id: AllocationId,
        offset: Long :| Positive0,
        size: MemoryValueByteWidth
    )(update: Long => Long): Either[ExecutionFailure, Long] = atomicLock.withLock {
      checked(id, offset, size.nonNegative) match {
        case Left(error)  => Left(error)
        case Right(start) =>
          val scalarWidth = size.scalarByteWidth
          val current     = content.get()
          val previous    = LittleEndian.decode(current, start, scalarWidth)
          val replacement = LittleEndian.toVec(update(previous), scalarWidth)
          written(offset, replacement.size)
          content.set(
            Vec.from(current.take(start).iterator ++ replacement.iterator ++ current.drop(start + replacement.size))
          )
          Right(previous)
      }
    }

    private def checked(
        id: AllocationId,
        offset: Long :| Positive0,
        length: Int :| Positive0
    ): Either[ExecutionFailure, Int :| Positive0] =
      Either.cond(
        id == expectedId && ByteRange.contains(size, offset, length.toLong),
        offset.toInt.refineUnsafe[Positive0],
        ExecutionFailure.InvalidRequest(s"invalid test memory access at $offset + $length")
      )
  }

  protected def operand(kind: String, values: Int*): Operand = Operand(
    SPIRVOperandKind.either(kind).fold(message => fail(message), valid => valid),
    Vec.from(values.map(Word(_))).refineEither[MinLength[1]]
      .fold(message => throw IllegalArgumentException(message), value => value)
  )

  protected def phiPair(value: Operand, parent: Operand): Operand = {
    val valueWord  = value.singleWord.fold(message => fail(message), word => word)
    val parentWord = parent.singleWord.fold(message => fail(message), word => word)
    Operand(SPIRVOperandKind("PairIdRefIdRef"), Vec.nonEmpty(valueWord, parentWord))
  }

  protected def stringOperand(value: String): Operand = {
    val checked = Either.cond(!value.contains('\u0000'), value, "SPIR-V literal string contains NUL")
      .fold(message => fail(message), valid => valid)
    val bytes = checked.getBytes(java.nio.charset.StandardCharsets.UTF_8) :+ 0.toByte
    val words = bytes.grouped(4).map { group =>
      val width = ScalarByteWidth.either(group.length).fold(message => fail(message), value => value)
      Word(LittleEndian.decode(group, 0, width).toInt)
    }
    Operand(SPIRVOperandKind("LiteralString"), Vec.from(words).refineUnsafe[MinLength[1]])
  }
}
