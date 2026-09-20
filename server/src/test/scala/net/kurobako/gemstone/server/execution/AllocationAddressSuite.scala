package net.kurobako.gemstone.server.execution
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.{LittleEndian, ScalarByteWidth}
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.{AllocationKind, GuestAddress}
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import java.util.concurrent.atomic.AtomicReference
import scala.util.Using

final class AllocationAddressSuite extends munit.FunSuite {
  private val env    = AllocationId(1)
  private val nested = AllocationId(2)
  private final class Executor extends ProgramExecutor {
    val observed                                                     = AtomicReference(Vec.empty[AllocationAddress])
    def load(id: ProgramId, format: ProgramFormat, image: Vec[Byte]) = Right(())
    def unload(id: ProgramId)                                        = Right(())
    def launch(dispatch: KernelDispatch, memory: ExecutionMemory, control: ExecutionControl) = {
      observed.set(memory.allocationAddresses)
      memory.write(nested, 0, LittleEndian.toVec(42L, ScalarByteWidth(4)))
    }
  }
  private def register(s: ExecutionService, size: Long :| Positive = 32L): AllocationId =
    s.registerAllocation(size, AllocationKind.Shared).fold(error => fail(error.message), value => value)

  test("queued dispatch snapshots aliases and retains implicit targets with their dirty tracking") {
    val executor  = Executor()
    val scheduled = AtomicReference(Option.empty[Runnable])
    Using.resource(ExecutionService(executor, runnable => scheduled.set(Some(runnable)))) { s =>
      assertEquals(register(s), env)
      assertEquals(register(s), nested)
      assertEquals(s.registerAllocationAddress(env, GuestAddress(0x3d9d8000L)), Right(()))
      assertEquals(s.registerAllocationAddress(nested, GuestAddress(0x3da78000L)), Right(()))
      val bytes = LittleEndian.toVec(0x3da78000L, ScalarByteWidth(8)) ++
        LittleEndian.toVec(0x3da78000L, ScalarByteWidth(8))
      assertEquals(s.write(env, 0, bytes), Right(()))
      val p        = s.load(ProgramFormat.HSACodeObjectBinary, Vec.empty).toOption.get
      val dispatch = KernelDispatch.validated(
        p,
        KernelName("kernel"),
        Dimensions.One,
        Dimensions.One,
        Vec(KernelArgument.Allocation(env, 0))
      ).toOption.get
      val c = s.launch(dispatch).toOption.get
      assert(s.releaseAllocation(nested).isLeft, "implicit alias target must survive queued execution")
      val late = register(s)
      assertEquals(s.registerAllocationAddress(late, GuestAddress(0x40000000L)), Right(()))
      assertEquals(s.releaseAllocation(late), Right(()), "later aliases are absent from the queued snapshot")
      scheduled.get().get.run()
      assertEquals(s.completion(c), Right(CompletionStatus.Succeeded))
      assertEquals(executor.observed.get().map(_.allocation).toSet, Set(env, nested))
      assertEquals(
        s.read(env, 0, bytes.size.refineUnsafe[Positive0]),
        Right(bytes),
        "address-like scalar and nested pointer bytes are unchanged"
      )
      assertEquals(s.read(nested, 0, 4), Right(LittleEndian.toVec(42L, ScalarByteWidth(4))))
      assert(s.completionDirtyRanges(c).toOption.get.exists(_.allocation == nested))
      assert(s.releaseAllocation(nested).isLeft, "completed result still owns implicit dirty memory")
      assertEquals(s.releaseCompletion(c), Right(()))
      assertEquals(s.releaseAllocation(nested), Right(()))
      val replacement = register(s)
      assertEquals(
        s.registerAllocationAddress(replacement, GuestAddress(0x3da78000L)),
        Right(()),
        "free removed address ownership"
      )
    }
  }

  test("alias registration rejects remaps, overlap, overflow and invalid ownership while clients stay isolated") {
    Using.Manager { use =>
      val a = use(ExecutionService(Executor(), _.run()))
      val b = use(ExecutionService(Executor(), _.run()))
      assertEquals(register(a), env)
      assertEquals(register(a), nested)
      assertEquals(register(b), env)
      val absent = AllocationId(99)
      assert(a.registerAllocationAddress(absent, GuestAddress(0x8000L)).isLeft)
      assert(a.registerAllocationAddress(env, GuestAddress(0L)).isLeft)
      assert(a.registerAllocationAddress(env, GuestAddress(-1L)).isLeft)
      assert(a.registerAllocationAddress(env, GuestAddress(Long.MaxValue - 16L)).isLeft)
      assertEquals(a.registerAllocationAddress(env, GuestAddress(0x8000L)), Right(()))
      assertEquals(a.registerAllocationAddress(env, GuestAddress(0x8000L)), Right(()))
      assert(a.registerAllocationAddress(env, GuestAddress(0x9000L)).isLeft)
      assert(a.registerAllocationAddress(nested, GuestAddress(0x8010L)).isLeft)
      assertEquals(
        a.registerAllocationAddress(nested, GuestAddress(0x8020L)),
        Right(()),
        "adjacent ranges are legal"
      )
      assertEquals(b.registerAllocationAddress(env, GuestAddress(0x8000L)), Right(()))
      a.close()
      assert(a.registerAllocationAddress(env, GuestAddress(0x8000L)).isLeft)
      assertEquals(b.write(env, 0, Vec[Byte](1)), Right(()))
    }.get
  }
}
