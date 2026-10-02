package net.kurobako.gemstone.isa.spirv.v1_6

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.v1_6 as spv
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

/** Test-only cooperative interleaving: at most P workgroups run, one external memory operation at a time. */
private object interleaving {
  val groups: Vec[Int > 0]                        = Vec(1, 2, 3, 5, 9)
  def o(kind: OperandKind, values: Int*): Operand = Operand(
    kind,
    Vec.from(values.map(Word(_))).unsafe[NonEmpty[Vec[Word]]]
  )
  def id(value: Int): Operand              = o(OperandKind.IdRef, value)
  private def name(value: String): Operand = {
    val checked = Either.cond(!value.contains('\u0000'), value, "SPIR-V literal string contains NUL")
      .fold(message => throw IllegalArgumentException(message), valid => valid)
    val bytes = checked.getBytes(java.nio.charset.StandardCharsets.UTF_8) :+ 0.toByte
    Operand(
      OperandKind.LiteralString,
      Vec.from(bytes.grouped(4).map { group =>
        val width = group.length.refine[Int where Between[0, 8]]
          .fold(message => throw IllegalArgumentException(message), value => value)
        Word(LittleEndian.decode(group, 0, width).toInt)
      }).unsafe
    )
  }

  def geometry(barrier: Boolean, producer: Int): Module = {
    import Instruction.*
    val head = Vec[Instruction](
      Capability(o(OperandKind.Capability, spv.Capability.Kernel)),
      MemoryModel(
        o(OperandKind.AddressingModel, spv.AddressingModel.Physical64),
        o(OperandKind.MemoryModel, spv.MemoryModel.OpenCL)
      ),
      TypeVoid(id(1)),
      TypeInt(id(2), o(OperandKind.LiteralInteger, 32), o(OperandKind.LiteralInteger, 0)),
      TypeInt(id(3), o(OperandKind.LiteralInteger, 64), o(OperandKind.LiteralInteger, 0)),
      TypeBool(id(4)),
      TypeVector(id(5), id(3), o(OperandKind.LiteralInteger, 3)),
      TypePointer(id(6), o(OperandKind.StorageClass, spv.StorageClass.Input), id(5)),
      TypePointer(id(7), o(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), id(2)),
      TypePointer(id(8), o(OperandKind.StorageClass, spv.StorageClass.Workgroup), id(2)),
      TypeFunction(id(9), id(1), Vec(id(7), id(2), id(8))),
      Constant(id(2), id(10), o(OperandKind.LiteralContextDependentNumber, producer)),
      Constant(id(2), id(11), o(OperandKind.LiteralContextDependentNumber, 1000)),
      Constant(id(2), id(12), o(OperandKind.LiteralContextDependentNumber, 10)),
      Constant(id(2), id(13), o(OperandKind.LiteralContextDependentNumber, spv.Scope.Workgroup)),
      Constant(
        id(2),
        id(14),
        o(
          OperandKind.LiteralContextDependentNumber,
          spv.MemorySemantics.WorkgroupMemory | spv.MemorySemantics.AcquireRelease
        )
      ),
      Variable(id(6), id(15), o(OperandKind.StorageClass, spv.StorageClass.Input), None),
      Decorate(id(15), o(OperandKind.Decoration, spv.Decoration.BuiltIn, spv.BuiltIn.GlobalInvocationId)),
      Variable(id(6), id(16), o(OperandKind.StorageClass, spv.StorageClass.Input), None),
      Decorate(id(16), o(OperandKind.Decoration, spv.Decoration.BuiltIn, spv.BuiltIn.LocalInvocationId)),
      Variable(id(6), id(17), o(OperandKind.StorageClass, spv.StorageClass.Input), None),
      Decorate(id(17), o(OperandKind.Decoration, spv.Decoration.BuiltIn, spv.BuiltIn.WorkgroupId)),
      EntryPoint(
        o(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
        id(20),
        name("groups"),
        Vec(id(15), id(16), id(17))
      ),
      Function(id(1), id(20), o(OperandKind.FunctionControl, spv.FunctionControl.None), id(9)),
      FunctionParameter(id(7), id(21)),
      FunctionParameter(id(2), id(22)),
      FunctionParameter(id(8), id(23)),
      Label(id(24)),
      Load(id(5), id(25), id(15), None),
      Load(id(5), id(26), id(16), None),
      Load(id(5), id(27), id(17), None),
      CompositeExtract(id(3), id(28), id(25), Vec(o(OperandKind.LiteralInteger, 0))),
      CompositeExtract(id(3), id(29), id(26), Vec(o(OperandKind.LiteralInteger, 0))),
      CompositeExtract(id(3), id(30), id(27), Vec(o(OperandKind.LiteralInteger, 0))),
      UConvert(id(2), id(31), id(28)),
      UConvert(id(2), id(32), id(29)),
      UConvert(id(2), id(33), id(30)),
      IAdd(id(2), id(34), id(33), id(22))
    )
    val exchange = if (barrier) Vec[Instruction](
      IEqual(id(4), id(35), id(32), id(10)),
      SelectionMerge(id(37), o(OperandKind.SelectionControl, spv.SelectionControl.None)),
      BranchConditional(id(35), id(36), id(37), Vec.empty),
      Label(id(36)),
      Store(id(23), id(34), None),
      Branch(id(37)),
      Label(id(37)),
      ControlBarrier(id(13), id(13), id(14)),
      Load(id(2), id(38), id(23), None)
    )
    else Vec[Instruction](CopyObject(id(2), id(38), id(34)))
    Module(
      Header(ISA.version, Word(0), Word(48), Word(0)),
      head ++ exchange ++ Vec[Instruction](
        IMul(id(2), id(39), id(38), id(11)),
        IMul(id(2), id(40), id(32), id(12)),
        IAdd(id(2), id(41), id(39), id(40)),
        IAdd(id(2), id(42), id(41), id(31)),
        PtrAccessChain(id(7), id(43), id(21), id(28), Vec.empty),
        Store(id(43), id(42), None),
        Return(),
        FunctionEnd()
      ),
      Endianness.Little
    )
  }

  def publication: Module = {
    import Instruction.*
    Module(
      Header(ISA.version, Word(0), Word(48), Word(0)),
      Vec[Instruction](
        Capability(o(OperandKind.Capability, spv.Capability.Kernel)),
        MemoryModel(
          o(OperandKind.AddressingModel, spv.AddressingModel.Physical64),
          o(OperandKind.MemoryModel, spv.MemoryModel.OpenCL)
        ),
        TypeVoid(id(1)),
        TypeInt(id(2), o(OperandKind.LiteralInteger, 32), o(OperandKind.LiteralInteger, 0)),
        TypeInt(id(3), o(OperandKind.LiteralInteger, 64), o(OperandKind.LiteralInteger, 0)),
        TypeBool(id(4)),
        TypeVector(id(5), id(3), o(OperandKind.LiteralInteger, 3)),
        TypePointer(id(6), o(OperandKind.StorageClass, spv.StorageClass.Input), id(5)),
        TypePointer(id(7), o(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), id(2)),
        TypeFunction(id(9), id(1), Vec(id(7), id(7), id(7), id(7), id(2))),
        Constant(id(2), id(10), o(OperandKind.LiteralContextDependentNumber, 0)),
        Constant(id(2), id(11), o(OperandKind.LiteralContextDependentNumber, 1)),
        Constant(id(2), id(12), o(OperandKind.LiteralContextDependentNumber, 2)),
        Constant(id(2), id(13), o(OperandKind.LiteralContextDependentNumber, 1)),
        Constant(id(2), id(14), o(OperandKind.LiteralContextDependentNumber, 516)),
        Constant(id(2), id(16), o(OperandKind.LiteralContextDependentNumber, 514)),
        Constant(id(2), id(17), o(OperandKind.LiteralContextDependentNumber, 520)),
        Variable(id(6), id(15), o(OperandKind.StorageClass, spv.StorageClass.Input), None),
        Decorate(id(15), o(OperandKind.Decoration, spv.Decoration.BuiltIn, spv.BuiltIn.WorkgroupId)),
        EntryPoint(o(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel), id(20), name("publish"), Vec(id(15))),
        Function(id(1), id(20), o(OperandKind.FunctionControl, spv.FunctionControl.None), id(9)),
        FunctionParameter(id(7), id(21)),
        FunctionParameter(id(7), id(22)),
        FunctionParameter(id(7), id(23)),
        FunctionParameter(id(7), id(24)),
        FunctionParameter(id(2), id(25)),
        Label(id(26)),
        Load(id(5), id(27), id(15), None),
        CompositeExtract(id(3), id(28), id(27), Vec(o(OperandKind.LiteralInteger, 0))),
        UConvert(id(2), id(29), id(28)),
        IMul(id(2), id(30), id(29), id(12)),
        PtrAccessChain(id(7), id(31), id(21), id(30), Vec.empty),
        IAdd(id(2), id(32), id(30), id(11)),
        PtrAccessChain(id(7), id(33), id(21), id(32), Vec.empty),
        AtomicIAdd(id(2), id(34), id(22), id(13), id(17), id(11)),
        Store(id(31), id(34), None),
        IEqual(id(4), id(35), id(34), id(10)),
        SelectionMerge(id(45), o(OperandKind.SelectionControl, spv.SelectionControl.None)),
        BranchConditional(id(35), id(36), id(38), Vec.empty),
        Label(id(36)),
        Store(id(23), id(25), None),
        AtomicStore(id(24), id(13), id(14), id(11)),
        Store(id(33), id(25), None),
        Branch(id(45)),
        Label(id(38)),
        AtomicLoad(id(2), id(39), id(24), id(13), id(16)),
        INotEqual(id(4), id(40), id(39), id(10)),
        SelectionMerge(id(44), o(OperandKind.SelectionControl, spv.SelectionControl.None)),
        BranchConditional(id(40), id(41), id(43), Vec.empty),
        Label(id(41)),
        Load(id(2), id(42), id(23), None),
        Store(id(33), id(42), None),
        Branch(id(44)),
        Label(id(43)),
        Store(id(33), id(10), None),
        Branch(id(44)),
        Label(id(44)),
        Branch(id(45)),
        Label(id(45)),
        Return(),
        FunctionEnd()
      ),
      Endianness.Little
    )
  }

  // Mutable turns and membership model deterministic interleaving in this final workgroups fixture.
  // Mutable workgroups state and fail-fast return are deliberate in this deterministic concurrency fixture.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  class Interleaving(val parallelism: Int > 0 = 4)
      extends Workgroups {
    val calls: AtomicInteger    = java.util.concurrent.atomic.AtomicInteger(0)
    private val lock            = ReentrantLock()
    private val changed         = lock.newCondition()
    private val slot            = ThreadLocal.withInitial[Int](() => -1)
    private var live            = Array.empty[Boolean]
    private var entered         = Array.empty[Boolean]
    private var turn            = 0
    private var deadline        = 0L
    private def advance(): Unit = {
      if (live.exists(value => value)) {
        turn = (turn + 1) % live.length
        while (!live(turn)) turn = (turn + 1) % live.length
      }
      changed.signalAll()
    }
    private def checkpoint(): Unit = lock.withLock {
      val current = slot.get()
      if (current >= 0) {
        if (entered(current)) advance() else entered(current) = true
        while (turn != current && platform.monotonicNanos < deadline) {
          val remaining = deadline - platform.monotonicNanos
          if (remaining > 0L) {
            val _ = changed.awaitNanos(math.min(10_000_000L, remaining))
          }
        }
        assert(turn == current, "cross-workgroup interleaving exceeded its deadline")
      }
      ()
    }
    class InterleavedMemory(delegate: Memory) extends Memory {
      override def read(id: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] = {
        checkpoint()
        delegate.read(id, offset, size)
      }
      override def write(id: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
        // Final flush holds the interpreter's shared flush lock. Never wait for another group here.
        delegate.write(id, offset, bytes)
      override def atomicModifyValue(
          id: AllocationId,
          offset: Long >= 0,
          size: Int where Between[1, 8]
      )(update: Long => Long): Either[Fault, Long] = {
        checkpoint()
        delegate.atomicModifyValue(id, offset, size)(update)
      }
    }

    override def shouldParallel(tasks: Long >= 0, workItems: Long >= 0): Boolean = tasks.raw > 1L
    // Stop the deterministic schedule at the first worker failure.
    override def parallelFor(tasks: Long >= 0)(task: Long >= 0 => Either[Fault, Unit])
        : Either[Fault, Unit] = {
      calls.incrementAndGet()
      var first = 0L
      while (first < tasks.raw) {
        val count = math.min(parallelism.raw.toLong, tasks.raw - first).toInt
        live = Array.fill(count)(true); entered = Array.fill(count)(false); turn = 0
        deadline = platform.monotonicNanos + 10_000_000_000L
        val failure = AtomicReference(Option.empty[Fault])
        val base    = first
        val threads = Array.tabulate(count) { index =>
          Thread(
            () => {
              slot.set(index)
              // The batching loop keeps base + index in [0, tasks).
              try task((base + index).unsafe[Long >= 0]).left.foreach(x => failure.compareAndSet(None, Some(x)))
              catch {
                case error: Throwable =>
                  val detail: NonEmpty[String] =
                    s"cross-workgroup worker failed: ${Option(error.getMessage).getOrElse(error.getClass.getName)}"

                  failure.set(Some(Fault.Backend(detail)))
              } finally lock.withLock { live(index) = false; if (turn == index) advance(); slot.remove() }
            },
            s"spirv-test-group-${base + index}"
          )
        }
        threads.foreach(_.setDaemon(true))
        threads.foreach(_.start()); threads.foreach(_.join(12000L))
        val timedOut = threads.exists(_.isAlive)
        if (timedOut) {
          threads.foreach(_.interrupt())
          lock.withLock(changed.signalAll())
          threads.foreach(_.join(1000L))
        }
        assert(!timedOut && threads.forall(!_.isAlive), "cross-workgroup worker did not stop")
        failure.get() match { case Some(error) => return Left(error); case None => () }
        first += count
      }
      Right(())
    }
  }
}
