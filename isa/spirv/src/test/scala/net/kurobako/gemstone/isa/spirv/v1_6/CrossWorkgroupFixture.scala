package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.concurrent.withLock

import net.kurobako.gemstone.{PlatformTime, Vec}
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.binary.ScalarByteWidth
import net.kurobako.gemstone.execution.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock

/** Test-only cooperative interleaving: at most P workgroups run, one instruction at a time. */
private[v1_6] object CrossWorkgroupFixture {
  val groups: Vec[Int :| Positive]           = Vec(1, 2, 3, 5, 9)
  def o(kind: String, values: Int*): Operand = Operand(
    SPIRVOperandKind.either(kind).fold(message => throw IllegalArgumentException(message), valid => valid),
    Vec.from(values.map(Word(_))).refineEither[MinLength[1]]
      .fold(message => throw IllegalArgumentException(message), value => value)
  )
  def id(value: Int): Operand              = o("IdRef", value)
  private def name(value: String): Operand = {
    val checked = Either.cond(!value.contains('\u0000'), value, "SPIR-V literal string contains NUL")
      .fold(message => throw IllegalArgumentException(message), valid => valid)
    val bytes = checked.getBytes(java.nio.charset.StandardCharsets.UTF_8) :+ 0.toByte
    Operand(
      SPIRVOperandKind.either("LiteralString").fold(message => throw IllegalArgumentException(message), valid => valid),
      Vec.from(bytes.grouped(4).map { group =>
        val width = ScalarByteWidth.either(group.length)
          .fold(message => throw IllegalArgumentException(message), value => value)
        Word(LittleEndian.decode(group, 0, width).toInt)
      }).refineUnsafe[MinLength[1]]
    )
  }

  def geometry(barrier: Boolean, producer: Int): Module = {
    import Instruction.*
    val head = Vec[Instruction](
      Capability(o("Capability", 6)),
      MemoryModel(o("AddressingModel", 2), o("MemoryModel", 2)),
      TypeVoid(id(1)),
      TypeInt(id(2), o("LiteralInteger", 32), o("LiteralInteger", 0)),
      TypeInt(id(3), o("LiteralInteger", 64), o("LiteralInteger", 0)),
      TypeBool(id(4)),
      TypeVector(id(5), id(3), o("LiteralInteger", 3)),
      TypePointer(id(6), o("StorageClass", 1), id(5)),
      TypePointer(id(7), o("StorageClass", 5), id(2)),
      TypePointer(id(8), o("StorageClass", 4), id(2)),
      TypeFunction(id(9), id(1), Vec(id(7), id(2), id(8))),
      Constant(id(2), id(10), o("LiteralContextDependentNumber", producer)),
      Constant(id(2), id(11), o("LiteralContextDependentNumber", 1000)),
      Constant(id(2), id(12), o("LiteralContextDependentNumber", 10)),
      Constant(id(2), id(13), o("LiteralContextDependentNumber", 2)),
      Constant(id(2), id(14), o("LiteralContextDependentNumber", 264)),
      Variable(id(6), id(15), o("StorageClass", 1), None),
      Decorate(id(15), o("Decoration", 11, 28)),
      Variable(id(6), id(16), o("StorageClass", 1), None),
      Decorate(id(16), o("Decoration", 11, 27)),
      Variable(id(6), id(17), o("StorageClass", 1), None),
      Decorate(id(17), o("Decoration", 11, 26)),
      EntryPoint(o("ExecutionModel", 6), id(20), name("groups"), Vec(id(15), id(16), id(17))),
      Function(id(1), id(20), o("FunctionControl", 0), id(9)),
      FunctionParameter(id(7), id(21)),
      FunctionParameter(id(2), id(22)),
      FunctionParameter(id(8), id(23)),
      Label(id(24)),
      Load(id(5), id(25), id(15), None),
      Load(id(5), id(26), id(16), None),
      Load(id(5), id(27), id(17), None),
      CompositeExtract(id(3), id(28), id(25), Vec(o("LiteralInteger", 0))),
      CompositeExtract(id(3), id(29), id(26), Vec(o("LiteralInteger", 0))),
      CompositeExtract(id(3), id(30), id(27), Vec(o("LiteralInteger", 0))),
      UConvert(id(2), id(31), id(28)),
      UConvert(id(2), id(32), id(29)),
      UConvert(id(2), id(33), id(30)),
      IAdd(id(2), id(34), id(33), id(22))
    )
    val exchange = if (barrier) Vec[Instruction](
      IEqual(id(4), id(35), id(32), id(10)),
      SelectionMerge(id(37), o("SelectionControl", 0)),
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
        Capability(o("Capability", 6)),
        MemoryModel(o("AddressingModel", 2), o("MemoryModel", 2)),
        TypeVoid(id(1)),
        TypeInt(id(2), o("LiteralInteger", 32), o("LiteralInteger", 0)),
        TypeInt(id(3), o("LiteralInteger", 64), o("LiteralInteger", 0)),
        TypeBool(id(4)),
        TypeVector(id(5), id(3), o("LiteralInteger", 3)),
        TypePointer(id(6), o("StorageClass", 1), id(5)),
        TypePointer(id(7), o("StorageClass", 5), id(2)),
        TypeFunction(id(9), id(1), Vec(id(7), id(7), id(7), id(7), id(2))),
        Constant(id(2), id(10), o("LiteralContextDependentNumber", 0)),
        Constant(id(2), id(11), o("LiteralContextDependentNumber", 1)),
        Constant(id(2), id(12), o("LiteralContextDependentNumber", 2)),
        Constant(id(2), id(13), o("LiteralContextDependentNumber", 1)),
        Constant(id(2), id(14), o("LiteralContextDependentNumber", 516)),
        Constant(id(2), id(16), o("LiteralContextDependentNumber", 514)),
        Constant(id(2), id(17), o("LiteralContextDependentNumber", 520)),
        Variable(id(6), id(15), o("StorageClass", 1), None),
        Decorate(id(15), o("Decoration", 11, 26)),
        EntryPoint(o("ExecutionModel", 6), id(20), name("publish"), Vec(id(15))),
        Function(id(1), id(20), o("FunctionControl", 0), id(9)),
        FunctionParameter(id(7), id(21)),
        FunctionParameter(id(7), id(22)),
        FunctionParameter(id(7), id(23)),
        FunctionParameter(id(7), id(24)),
        FunctionParameter(id(2), id(25)),
        Label(id(26)),
        Load(id(5), id(27), id(15), None),
        CompositeExtract(id(3), id(28), id(27), Vec(o("LiteralInteger", 0))),
        UConvert(id(2), id(29), id(28)),
        IMul(id(2), id(30), id(29), id(12)),
        PtrAccessChain(id(7), id(31), id(21), id(30), Vec.empty),
        IAdd(id(2), id(32), id(30), id(11)),
        PtrAccessChain(id(7), id(33), id(21), id(32), Vec.empty),
        AtomicIAdd(id(2), id(34), id(22), id(13), id(17), id(11)),
        Store(id(31), id(34), None),
        IEqual(id(4), id(35), id(34), id(10)),
        SelectionMerge(id(45), o("SelectionControl", 0)),
        BranchConditional(id(35), id(36), id(38), Vec.empty),
        Label(id(36)),
        Store(id(23), id(25), None),
        AtomicStore(id(24), id(13), id(14), id(11)),
        Store(id(33), id(25), None),
        Branch(id(45)),
        Label(id(38)),
        AtomicLoad(id(2), id(39), id(24), id(13), id(16)),
        INotEqual(id(4), id(40), id(39), id(10)),
        SelectionMerge(id(44), o("SelectionControl", 0)),
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

  // Mutable turns and membership model deterministic interleaving in this final scheduler fixture.
  // Mutable scheduler state and fail-fast return are deliberate in this deterministic concurrency fixture.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  final class Interleaving(val parallelism: Int :| Positive = 4) extends ExecutionParallelism with CancellationToken {
    val calls                   = java.util.concurrent.atomic.AtomicInteger(0)
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
    override def isCancelled: Boolean = lock.withLock {
      val current = slot.get()
      if (current >= 0) {
        if (entered(current)) advance() else entered(current) = true
        while (turn != current && PlatformTime.monotonicNanos < deadline) {
          val remaining = deadline - PlatformTime.monotonicNanos
          if (remaining > 0L) {
            val _ = changed.awaitNanos(math.min(10_000_000L, remaining))
          }
        }
        assert(turn == current, "cross-workgroup interleaving exceeded its deadline")
      }
      false
    }
    override def shouldParallel(tasks: Long :| Positive0, workItems: Long :| Positive0): Boolean = tasks > 1L
    // Stop the deterministic schedule at the first worker failure.
    override def parallelFor(tasks: Long :| Positive0)(task: (Long :| Positive0) => Either[ExecutionFailure, Unit])
        : Either[ExecutionFailure, Unit] = {
      calls.incrementAndGet()
      var first = 0L
      while (first < tasks) {
        val count = math.min(parallelism.toLong, tasks - first).toInt
        live = Array.fill(count)(true); entered = Array.fill(count)(false); turn = 0
        deadline = PlatformTime.monotonicNanos + 10_000_000_000L
        val failure = AtomicReference(Option.empty[ExecutionFailure])
        val base    = first
        val threads = Array.tabulate(count) { index =>
          Thread(
            () => {
              slot.set(index)
              // The batching loop keeps base + index in [0, tasks).
              try task((base + index).refineUnsafe[Positive0]).left.foreach(x => failure.compareAndSet(None, Some(x)))
              catch {
                case error: Throwable =>
                  val detail =
                    s"cross-workgroup worker failed: ${Option(error.getMessage).getOrElse(error.getClass.getName)}"

                  failure.set(Some(ExecutionFailure.Failed(detail)))
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
