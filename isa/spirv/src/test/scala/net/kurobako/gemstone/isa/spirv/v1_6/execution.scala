package net.kurobako.gemstone.isa.spirv.v1_6

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

private object execution extends munit.Assertions {
  def loadUnchecked(module: Module): Either[Fault, VMProgramRuntime] =
    VMCompiler.compileUnchecked(module).map(VMProgramRuntime(_))

  def executeUnchecked(
      module: Module,
      task: KernelTask,
      memory: Memory
  ): Either[Fault, Unit] =
    loadUnchecked(module).flatMap(executeLoaded(_, task, memory))

  def executeLoaded(
      program: VMProgramRuntime,
      task: KernelTask,
      memory: Memory,
      control: TaskControl = TaskControl(),
      workgroups: Workgroups = Workgroups.Sequential,
      handler: Event => Either[Fault, Unit] = _ => Right(())
  ): Either[Fault, Unit] =
    VMInterpreter.execute(program, task, memory, control, workgroups, handler)

  def testDimensions(
      x: Int > 0,
      y: Int > 0 = 1,
      z: Int > 0 = 1
  ): Dimensions = Dimensions(x, y, z)

  class ArrayMemory(
      expectedId: AllocationId,
      size: Int,
      written: (Long, Int) => Unit = (_, _) => ()
  ) extends Memory {
    private val content    = AtomicReference(Vec.fill(size)(0.toByte))
    private val atomicLock = ReentrantLock()

    def bytes: Vec[Byte] = content.get()

    override def read(
        id: AllocationId,
        offset: Long >= 0,
        size: Int >= 0
    ): Either[Fault, Vec[Byte]] =
      checked(id, offset, size).map(start => content.get().slice(start, start + size.raw))

    override def write(
        id: AllocationId,
        offset: Long >= 0,
        bytes: Vec[Byte]
    ): Either[Fault, Unit] =
      // Collection sizes are non-negative by construction.
      checked(id, offset, bytes.nonNegativeSize) match {
        case Left(error)  => Left(error)
        case Right(start) =>
          written(offset.raw, bytes.size)
          content.updateAndGet(current =>
            Vec.from(current.take(start).view ++ bytes.view ++ current.drop(start + bytes.size))
          )
          Right(())
      }

    override def atomicModifyValue(
        id: AllocationId,
        offset: Long >= 0,
        size: Int where Between[1, 8]
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
        id: AllocationId,
        offset: Long >= 0,
        length: Int >= 0
    ): Either[Fault, Int] =
      Either.cond(
        id == expectedId && ByteRange.contains(size, offset.raw, length.raw.toLong),
        offset.raw.toInt,
        Fault.InvalidTask(s"invalid test memory access at $offset + $length")
      )
  }

  def operand(kind: NonEmpty[String], values: Int*): Operand = Operand(
    kind,
    Vec.from(values.map(Word(_))).unsafe[NonEmpty[Vec[Word]]]
  )

  def phiPair(value: Operand, parent: Operand): Operand = {
    val valueWord  = value.singleWord.fold(message => fail(message), word => word)
    val parentWord = parent.singleWord.fold(message => fail(message), word => word)
    Operand("PairIdRefIdRef", Vec.nonEmpty(valueWord, parentWord))
  }

  def stringOperand(value: String): Operand = {
    val checked = Either.cond(!value.contains('\u0000'), value, "SPIR-V literal string contains NUL")
      .fold(message => fail(message), valid => valid)
    val bytes = checked.getBytes(java.nio.charset.StandardCharsets.UTF_8) :+ 0.toByte
    val words = bytes.grouped(4).map { group =>
      val width = group.length.refine[Int where Between[0, 8]].fold(
        message => fail(message),
        value => value
      )
      Word(LittleEndian.decode(group, 0, width).toInt)
    }
    Operand("LiteralString", Vec.from(words).unsafe)
  }
}
