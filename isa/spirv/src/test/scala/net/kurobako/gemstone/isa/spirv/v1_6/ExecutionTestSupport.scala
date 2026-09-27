package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Constraint.IntBetween

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

import net.kurobako.gemstone.binary.Widths.*

import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.{Coordinates, Dimensions, GlobalDimensions}
import net.kurobako.gemstone.{NonEmptyVec, Vec}

import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.concurrent.withLock
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock

private trait ExecutionTestSupport { self: munit.FunSuite =>
  protected final def loadUnchecked(module: Module): Either[Fault, VMLoadedProgram] =
    VMCompiler.compileUnchecked(module).map(VMLoadedProgram(_))

  protected final def executeUnchecked(
      module: Module,
      task: KernelTask,
      memory: Memory
  ): Either[Fault, Unit] =
    loadUnchecked(module).flatMap(executeLoaded(_, task, memory))

  protected final def executeLoaded(
      program: VMLoadedProgram,
      task: KernelTask,
      memory: Memory,
      control: TaskControl = TaskControl.default,
      scheduler: Scheduler = Scheduler.Sequential,
      observer: Observer[Event] = Observer.discard
  ): Either[Fault, Unit] =
    VMInterpreter.execute(program, task, memory, control, scheduler, observer)

  protected final def testDimensions(
      x: Positive[Int],
      y: Positive[Int] = 1,
      z: Positive[Int] = 1
  ): Dimensions = Dimensions(x, y, z)

  protected final class ArrayMemory(
      expectedId: MemoryRegion,
      size: Int,
      written: (Long, Int) => Unit = (_, _) => ()
  ) extends Memory {
    private val content    = AtomicReference(Vec.fill(size)(0.toByte))
    private val atomicLock = ReentrantLock()

    def bytes: Vec[Byte] = content.get()

    override def read(
        id: MemoryRegion,
        offset: NonNegative[Long],
        size: NonNegative[Int]
    ): Either[Fault, Vec[Byte]] =
      checked(id, offset, size).map(start => content.get().slice(start, start + size.raw))

    override def write(
        id: MemoryRegion,
        offset: NonNegative[Long],
        bytes: Vec[Byte]
    ): Either[Fault, Unit] =
      // Collection sizes are non-negative by construction.
      checked(id, offset, NonNegative.unsafe(bytes.size)) match {
        case Left(error)  => Left(error)
        case Right(start) =>
          written(offset.raw, bytes.size)
          content.updateAndGet(current =>
            Vec.from(current.take(start).view ++ bytes.view ++ current.drop(start + bytes.size))
          )
          Right(())
      }

    override def atomicModifyValue(
        id: MemoryRegion,
        offset: NonNegative[Long],
        size: IntBetween[1, 8]
    )(update: Long => Long): Either[Fault, Long] = atomicLock.withLock {
      checked(id, offset, size.nonNegative) match {
        case Left(error)  => Left(error)
        case Right(start) =>
          val scalarWidth = size.scalarByteWidth
          val current     = content.get()
          val previous    = LittleEndian.decode(current, start, scalarWidth)
          val replacement = LittleEndian.toVec(update(previous), scalarWidth)
          written(offset.raw, replacement.size)
          content.set(
            Vec.from(current.take(start).view ++ replacement.view ++ current.drop(start + replacement.size))
          )
          Right(previous)
      }
    }

    private def checked(
        id: MemoryRegion,
        offset: NonNegative[Long],
        length: NonNegative[Int]
    ): Either[Fault, Int] =
      Either.cond(
        id == expectedId && ByteRange.contains(size, offset.raw, length.raw.toLong),
        offset.raw.toInt,
        Fault.InvalidTask(s"invalid test memory access at $offset + $length")
      )
  }

  protected def operand(kind: String, values: Int*): Operand = Operand(
    NonEmptyString.either(kind).fold(message => fail(message), valid => valid),
    NonEmptyVec.fromVec(Vec.from(values.map(Word(_))))
      .fold(message => throw IllegalArgumentException(message), value => value)
  )

  protected def phiPair(value: Operand, parent: Operand): Operand = {
    val valueWord  = value.singleWord.fold(message => fail(message), word => word)
    val parentWord = parent.singleWord.fold(message => fail(message), word => word)
    Operand(NonEmptyString("PairIdRefIdRef"), Vec.nonEmpty(valueWord, parentWord))
  }

  protected def stringOperand(value: String): Operand = {
    val checked = Either.cond(!value.contains('\u0000'), value, "SPIR-V literal string contains NUL")
      .fold(message => fail(message), valid => valid)
    val bytes = checked.getBytes(java.nio.charset.StandardCharsets.UTF_8) :+ 0.toByte
    val words = bytes.grouped(4).map { group =>
      val width = IntBetween.either[0, 8](group.length).fold(message => fail(message), value => value)
      Word(LittleEndian.decode(group, 0, width).toInt)
    }
    Operand(NonEmptyString("LiteralString"), NonEmptyVec.unsafeFromVec(Vec.from(words)))
  }
}
