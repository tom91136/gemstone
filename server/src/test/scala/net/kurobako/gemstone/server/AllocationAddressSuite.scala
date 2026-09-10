package net.kurobako.gemstone.server
import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.AllocationKind
import java.util.concurrent.atomic.AtomicReference

final class AllocationAddressSuite extends munit.FunSuite {
  private val env    = AllocationId.fromLong(1).toOption.get
  private val nested = AllocationId.fromLong(118).toOption.get
  private val late   = AllocationId.fromLong(119).toOption.get
  private final class Executor extends ProgramExecutor {
    val observed                                                     = AtomicReference(Col.empty[AllocationAddress])
    def load(id: ProgramId, format: ProgramFormat, image: Col[Byte]) = Right(())
    def unload(id: ProgramId)                                        = Right(())
    def launch(dispatch: KernelDispatch, memory: ExecutionMemory, control: ExecutionControl) = {
      observed.set(memory.allocationAddresses)
      memory.write(nested, 0, LittleEndian.toCol(42L, 4))
    }
  }
  private def register(s: ExecutionService, id: AllocationId, size: Long = 32L): Unit =
    assertEquals(s.registerAllocation(id, size, AllocationKind.Shared), Right(()))

  test("queued dispatch snapshots aliases and retains implicit targets with their dirty tracking") {
    val executor  = Executor()
    val scheduled = AtomicReference(Option.empty[Runnable])
    val s         = ExecutionService(executor, r => scheduled.set(Some(r)))
    try {
      register(s, env); register(s, nested)
      assertEquals(s.registerAllocationAddress(env, 0x3d9d8000L), Right(()))
      assertEquals(s.registerAllocationAddress(nested, 0x3da78000L), Right(()))
      val bytes = LittleEndian.toCol(0x3da78000L, 8) ++ LittleEndian.toCol(0x3da78000L, 8)
      assertEquals(s.write(env, 0, bytes), Right(()))
      val p        = s.load(ProgramFormat.AMDGPUCodeObject, Col.empty).toOption.get
      val dispatch = KernelDispatch.create(
        p,
        "kernel",
        Dimensions.One,
        Dimensions.One,
        Col(KernelArgument.Allocation(env, 0))
      ).toOption.get
      val c = s.launch(dispatch).toOption.get
      assert(s.releaseAllocation(nested).isLeft, "implicit alias target must survive queued execution")
      register(s, late)
      assertEquals(s.registerAllocationAddress(late, 0x40000000L), Right(()))
      assertEquals(s.releaseAllocation(late), Right(()), "later aliases are absent from the queued snapshot")
      scheduled.get().get.run()
      assertEquals(s.completion(c), Right(CompletionStatus.Succeeded))
      assertEquals(executor.observed.get().map(_.allocation).toSet, Set(env, nested))
      assertEquals(
        s.read(env, 0, bytes.size),
        Right(bytes),
        "address-like scalar and nested pointer bytes are unchanged"
      )
      assertEquals(s.read(nested, 0, 4), Right(LittleEndian.toCol(42L, 4)))
      assert(s.completionDirtyRanges(c).toOption.get.exists(_.allocation == nested))
      assert(s.releaseAllocation(nested).isLeft, "completed result still owns implicit dirty memory")
      assertEquals(s.releaseCompletion(c), Right(()))
      assertEquals(s.releaseAllocation(nested), Right(()))
      register(s, late)
      assertEquals(s.registerAllocationAddress(late, 0x3da78000L), Right(()), "free removed address ownership")
    } finally s.close()
  }

  test("alias registration rejects remaps, overlap, overflow and invalid ownership while clients stay isolated") {
    val a = ExecutionService(Executor(), _.run()); val b = ExecutionService(Executor(), _.run())
    try {
      register(a, env); register(a, nested); register(b, env)
      assert(a.registerAllocationAddress(late, 0x8000L).isLeft)
      assert(a.registerAllocationAddress(env, 0L).isLeft)
      assert(a.registerAllocationAddress(env, -1L).isLeft)
      assert(a.registerAllocationAddress(env, Long.MaxValue - 16L).isLeft)
      assertEquals(a.registerAllocationAddress(env, 0x8000L), Right(()))
      assertEquals(a.registerAllocationAddress(env, 0x8000L), Right(()))
      assert(a.registerAllocationAddress(env, 0x9000L).isLeft)
      assert(a.registerAllocationAddress(nested, 0x8010L).isLeft)
      assertEquals(a.registerAllocationAddress(nested, 0x8020L), Right(()), "adjacent ranges are legal")
      assertEquals(b.registerAllocationAddress(env, 0x8000L), Right(()))
      register(a, late, 0L)
      assert(a.registerAllocationAddress(late, 0xa000L).isLeft)
      assertEquals(a.releaseAllocation(late), Right(()), "failed alias adds no retention")
      a.close()
      assert(a.registerAllocationAddress(env, 0x8000L).isLeft)
      assertEquals(b.write(env, 0, Col[Byte](1)), Right(()))
    } finally { a.close(); b.close() }
  }
}
