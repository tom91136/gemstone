package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.vm.*

/** The definitions of the machine instructions the suites render, over operands named after their roles. */
private[isa] object MachineDefinitions {
  val destination: Value = Value.Register(Register(1))
  val source: Value      = Value.Register(Register(2))
  val size: Value        = Value.Register(Register(3))
  val barrier: Value     = Value.Register(Register(4))
  val mask: Value        = Value.Register(Register(5))
  val left: Value        = Value.Register(Register(6))
  val right: Value       = Value.Register(Register(7))
  val row: Value         = Value.Register(Register(13))
  val first: Value       = Value.Register(Register(14))
  val second: Value      = Value.Register(Register(15))
  val done: Predicate    = Predicate(0)

  private class Operands extends SlotNaming {
    val predicates: Map[Int, String] = Map(0 -> "done")
    val registers: Map[Int, String]  =
      Map(
        1  -> "dst",
        2  -> "src",
        3  -> "size",
        4  -> "bar",
        5  -> "mask",
        6  -> "left",
        7  -> "right",
        8  -> "token",
        9  -> "d0",
        10 -> "d1",
        11 -> "d2",
        12 -> "d3",
        13 -> "row"
      ) ++ (14 to 21).map(index => index -> s"s${index - 14}") ++ (22 to 29).map(index => index -> s"f${index - 22}")
    def resolve(slot: Slot): Vec[Naming.Name] = (slot match {
      case Slot.Gpr(found)  => registers.get(found.raw).map(Naming.Name(_, Type.unsigned(64)))
      case Slot.Pred(found) => predicates.get(found.raw).map(Naming.Name(_, Type.Bool))
    }).toVec
  }

  def rendered(name: String, guarded: Boolean = false)(
      semantics: NvidiaMachineRender => Term.Steps
  ): Either[String, Definition] = {
    val names = Names(Operands())
    val guard = Option.when(guarded)(Term.Variable(names.declare("p", Type.Bool)))
    Definition.from(semantics(NvidiaMachineRender(names, guard)), name, names, Vec.empty)
  }

  def branch(guarded: Boolean = false): Either[String, Definition] =
    rendered("bra", guarded)(_.jump(Pc(7)))

  def copy(
      sharedSource: Boolean = false,
      clusterDestination: Boolean = false,
      multicast: Option[Value] = None,
      ignored: Option[(left: Value, right: Value)] = None,
      guarded: Boolean = false
  ): Either[String, Definition] = rendered("cp.async.bulk", guarded) { machine =>
    MachineSemantics.bulkCopy(Render, machine)(
      Instruction.AsyncBulkCopy(
        destination,
        source,
        size,
        barrier,
        sharedSource,
        multicast,
        ignored,
        clusterDestination
      )
    )
  }

  def moveMatrix(): Either[String, Definition] =
    rendered("movmatrix")(MachineSemantics.moveMatrix(Render, _)(Register(1), source))

  /** An `ldmatrix` of `count` matrices into the registers named `d0` on, whose row addresses are in `row`. */
  def loadMatrix(
      count: Int in (1, 2, 4),
      transpose: Boolean = false,
      shape: Matrix.TileShape = Matrix.TileShape.M8N8,
      packing: Matrix.Packing = Matrix.Packing.Bytes
  ): Either[String, Definition] = rendered("ldmatrix") { machine =>
    val tile = Matrix.Tile(shape, count, transpose, packing)
    MachineSemantics.loadMatrix(Render, machine)(
      tile,
      Vec(Register(9), Register(10), Register(11), Register(12)).take(count.raw * tile.registers.raw),
      row
    )
  }

  private val sourceRegisters: Vec[Value] = Vec(
    Value.Register(Register(14)),
    Value.Register(Register(15)),
    Value.Register(Register(16)),
    Value.Register(Register(17)),
    Value.Register(Register(18)),
    Value.Register(Register(19)),
    Value.Register(Register(20)),
    Value.Register(Register(21))
  )

  private val destinationRegisters: Vec[Register] =
    Vec(Register(22), Register(23), Register(24), Register(25), Register(26), Register(27), Register(28), Register(29))

  /** A `stmatrix` of `count` matrices from the registers named `s0` on, whose row addresses are in `row`. */
  def storeMatrix(
      count: Int in (1, 2, 4),
      transpose: Boolean = false,
      shape: Matrix.TileShape = Matrix.TileShape.M8N8,
      packing: Matrix.Packing = Matrix.Packing.Bytes,
      space: MemorySpace = MemorySpace.Shared
  ): Either[String, Definition] = rendered("stmatrix") { machine =>
    val tile = Matrix.Tile(shape, count, transpose, packing)
    MachineSemantics.storeMatrix(
      Render,
      machine
    )(tile, sourceRegisters.take(count.raw * tile.registers.raw), row, space)
  }

  /** A `wmma.load` of `fragment` into the registers named `f0` on, from the address in `row` with the stride in `size`.
    */
  def loadFragment(
      fragment: Matrix.Fragment,
      columnMajor: Boolean = false,
      space: MemorySpace = MemorySpace.Shared
  ): Either[String, Definition] = rendered("wmma_load") { machine =>
    MachineSemantics.loadFragment(
      Render,
      machine
    )(fragment, destinationRegisters.take(fragment.registers.raw), row, size, columnMajor, space)
  }

  /** A `wmma.store` of `fragment` from the registers named `s0` on, to the address in `row` with the stride in `size`.
    */
  def storeFragment(
      fragment: Matrix.Fragment,
      columnMajor: Boolean = false,
      space: MemorySpace = MemorySpace.Shared
  ): Either[String, Definition] = rendered("wmma_store") { machine =>
    MachineSemantics.storeFragment(
      Render,
      machine
    )(fragment, sourceRegisters.take(fragment.registers.raw), row, size, columnMajor, space)
  }

  /** An arrival whose token is written to a register, then a wait at the barrier, as a semantics could request them. */
  def arrival(): Either[String, Definition] = rendered("arrive") { machine =>
    machine.everyLane { lane =>
      machine.mbarrierArrive(
        lane,
        machine.read(lane, barrier),
        machine.read(lane, size),
        machine.read(lane, size),
        drop = false,
        complete = true
      ) { token =>
        machine.write(lane, Register(8), token)(machine.done)
      }
    }(machine.park(machine.uniform(barrier), machine.uniform(size), blocking = true))
  }

  def mbarrierInit(): Either[String, Definition] =
    rendered("mbarrier.init")(MachineSemantics.mbarrierInit(_)(barrier, size))

  def mbarrierInvalidate(): Either[String, Definition] =
    rendered("mbarrier.inval")(MachineSemantics.mbarrierInvalidate(_)(barrier))

  def mbarrierLoad(): Either[String, Definition] = rendered("mbarrier.load")(MachineSemantics.mbarrierLoad(_)(barrier))

  /** An arrival expecting `bytes` transaction bytes, whose token is written to `destination` if it has one. */
  def mbarrierArrive(
      destination: Option[Register] = Some(Register(8)),
      bytes: Value = Value.Immediate(0L),
      drop: Boolean = false,
      complete: Boolean = true
  ): Either[String, Definition] = rendered("mbarrier.arrive")(MachineSemantics.mbarrierArrive(_)(
    destination,
    barrier,
    size,
    bytes,
    drop,
    complete
  ))

  def mbarrierTransaction(complete: Boolean): Either[String, Definition] =
    rendered("mbarrier.transaction")(MachineSemantics.mbarrierTransaction(_)(barrier, size, complete))

  def mbarrierTest(parity: Boolean): Either[String, Definition] =
    rendered("mbarrier.test_wait")(MachineSemantics.mbarrierTest(_)(done, barrier, source, parity))

  def mbarrierPending(): Either[String, Definition] =
    rendered("mbarrier.pending_count")(machine =>
      MachineSemantics.mbarrierPending(Render, machine)(Register(1), source)
    )

  /** A call and a return under a guard. */
  def calls(): Either[String, Definition] = rendered("call", guarded = true) { machine =>
    machine.call(source, Vec(Parameter(1)), Vec(Parameter(2), Parameter(3))) ++ machine.returnControl
  }

  def bulkStore(): Either[String, Definition] = rendered("cp.async.bulk") { machine =>
    MachineSemantics.bulkStore(Render, machine)(destination, source, size, mask)
  }

  def tensorMapCopy(guarded: Boolean = false): Either[String, Definition] =
    rendered("tensormap.cp_fenceproxy", guarded) { machine =>
      MachineSemantics.tensorMapCopy(Render, machine)(destination, source)
    }

  def tensorMapReplace(field: TensorMap.Field, index: Int = 0): Either[String, Definition] =
    rendered("tensormap.replace") { machine =>
      MachineSemantics.tensorMapReplace(Render, machine)(destination, field, index, source)
    }

  /** A tensor copy through the map in `src` to the shared box in `dst`, completing on `bar`, at the coordinates in
    * `left` and `right`.
    */
  def tensorCopy(
      operation: TensorMap.Operation = TensorMap.Operation.Load,
      multicast: Boolean = false,
      pair: Boolean = false,
      gather: Boolean = false,
      im2col: Boolean = false,
      completes: Boolean = true,
      guarded: Boolean = false,
      rank: Int = 2
  ): Either[String, Definition] = rendered("cp.async.bulk.tensor", guarded) { machine =>
    MachineSemantics.asyncTensor(Render, machine)(
      Instruction.AsyncTensor(
        operation = operation,
        tensorMap = source,
        coordinates = if (gather) Vec(left, right, row, size, mask) else Vec(left, right, row, size, mask).take(rank),
        shared = destination,
        barrier = Option.when(completes)(barrier),
        multicast = Option.when(multicast)(mask),
        offsets = if (im2col) Vec(first, second).take(rank - 2) else Vec.empty,
        im2col = im2col,
        gather = gather,
        pair = pair
      )
    )
  }

  def bulkFill(): Either[String, Definition] = rendered("st.bulk") { machine =>
    MachineSemantics.bulkFill(Render, machine)(destination, size)
  }

  def bulkReduce(
      completes: Boolean = true,
      operator: TensorMap.Reduction = TensorMap.Reduction.Add,
      element: TensorMap.Element = TensorMap.Element.F32
  ): Either[String, Definition] = rendered("cp.reduce.async.bulk") { machine =>
    MachineSemantics.bulkReduce(Render, machine)(
      Instruction.AsyncBulkReduce(destination, source, size, Option.when(completes)(barrier), operator, element)
    )
  }

  def sharedStore(): Either[String, Definition] = rendered("st.async") { machine =>
    MachineSemantics.sharedStore(Render, machine)(destination, Vec(source, left), 32.unsafe[ScalarBitWidth], barrier)
  }

  def sharedReduce(): Either[String, Definition] = rendered("red.async") { machine =>
    MachineSemantics.sharedReduce(Render, machine)(
      destination,
      source,
      32.unsafe[ScalarBitWidth],
      AtomicOperation.Add,
      barrier
    )
  }

  def asyncCopy(): Either[String, Definition] = rendered("cp.async") { machine =>
    MachineSemantics.asyncCopy(Render, machine)(destination, source, 16, size)
  }

  def stackAllocate(): Either[String, Definition] =
    rendered("alloca")(MachineSemantics.stackAllocate(Render, _)(Register(1), size, 16L))

  def stackSave(): Either[String, Definition] = rendered("stacksave")(MachineSemantics.stackSave(_)(Register(1)))

  def stackRestore(guarded: Boolean = false): Either[String, Definition] =
    rendered("stackrestore", guarded)(MachineSemantics.stackRestore(_)(source))

  def exits(guarded: Boolean = false): Either[String, Definition] =
    rendered("exit", guarded)(_.exit)

  def returns(guarded: Boolean = false): Either[String, Definition] =
    rendered("ret", guarded)(_.returnControl)

  def traps(guarded: Boolean = false): Either[String, Definition] =
    rendered("brkpt", guarded)(_.trap)

  def fence(guarded: Boolean = false): Either[String, Definition] =
    rendered("membar", guarded)(_.fenceHost)

  def branchTable(): Either[String, Definition] =
    rendered("brx")(_.branchTable(source, Vec(Pc(7), Pc(9))))

  /** A call of `function`, passing and receiving the parameter slots the calls of the suites name. */
  def call(function: Value): Either[String, Definition] =
    rendered("call")(_.call(function, Vec(Parameter(1)), Vec(Parameter(2), Parameter(3))))

  def builtin(): Either[String, Definition] =
    rendered("malloc")(machine =>
      machine.callBuiltin(Builtin.Malloc, Vec(Parameter(0)), Vec(Parameter(1)))(machine.advance)
    )

  def setupConvergence(): Either[String, Definition] =
    rendered("bssy")(MachineSemantics.convergence(_)(Register(8), ConvergenceAction.Setup))

  def synchroniseConvergence(): Either[String, Definition] =
    rendered("bsync")(MachineSemantics.convergence(_)(Register(8), ConvergenceAction.Synchronise))

  def leaveConvergence(guarded: Boolean = false): Either[String, Definition] =
    rendered("bbreak", guarded)(MachineSemantics.convergence(_)(Register(8), ConvergenceAction.Break))

  def barrierSync(id: Value, count: Option[Value] = None): Either[String, Definition] =
    rendered("bar.sync")(MachineSemantics.barrierSync(Render, _)(id, count))

  def barrierArrive(id: Value, threads: Value): Either[String, Definition] =
    rendered("bar.arrive")(MachineSemantics.barrierArrive(Render, _)(id, threads))

  def barrierReduce(
      result: BarrierResult,
      id: Value,
      predicate: Predicate,
      count: Option[Value] = None,
      negated: Boolean = false
  ): Either[String, Definition] =
    rendered("bar.red")(MachineSemantics.barrierReduce(Render, _)(result, id, predicate, count, negated))

  def warpBarrier(guarded: Boolean = false): Either[String, Definition] =
    rendered("bar.warp.sync", guarded)(_.warpSync(mask))

  def clusterBarrier(arrive: Boolean): Either[String, Definition] =
    rendered("barrier.cluster")(_.clusterBarrier(arrive))

  def clusterMap(): Either[String, Definition] =
    rendered("mapa")(MachineSemantics.clusterMap(Render, _)(Register(1), source, size))

  def clusterRank(): Either[String, Definition] =
    rendered("getctarank")(MachineSemantics.clusterRank(Render, _)(Register(1), source))

  def clusterTryCancel(multicast: Boolean = false): Either[String, Definition] =
    rendered("clusterlaunchcontrol.try_cancel")(MachineSemantics.clusterTryCancel(Render, _)(
      source,
      barrier,
      multicast
    ))

  def tensorMemoryAllocate(guarded: Boolean = false): Either[String, Definition] =
    rendered("tcgen05.alloc", guarded)(MachineSemantics.tensorMemoryAllocate(Render, _)(destination, size))

  def tensorMemoryDeallocate(guarded: Boolean = false): Either[String, Definition] =
    rendered("tcgen05.dealloc", guarded)(MachineSemantics.tensorMemoryDeallocate(Render, _)(source, size))

  def tensorMemoryRelinquish(guarded: Boolean = false): Either[String, Definition] =
    rendered("tcgen05.relinquish_alloc_permit", guarded)(MachineSemantics.tensorMemoryRelinquish(_))

  /** A load of `count` registers from the tensor memory at `src`, reduced to `dst` if `reduction`. */
  def tensorMemoryLoad(
      movement: Tcgen05.Movement,
      count: Int,
      reduction: Option[Tcgen05.Reduction] = None
  ): Either[String, Definition] = rendered("tcgen05.ld")(
    MachineSemantics.tensorMemoryLoad(Render, _)(
      movement,
      Vec(Register(9), Register(10), Register(11), Register(12)).take(count),
      source,
      reduction
    )
  )

  /** A store of `count` registers to the tensor memory at `src`. */
  def tensorMemoryStore(movement: Tcgen05.Movement, count: Int, guarded: Boolean = false)
      : Either[String, Definition] = rendered("tcgen05.st", guarded)(
    MachineSemantics.tensorMemoryStore(Render, _)(
      movement,
      Vec(Register(9), Register(10), Register(11), Register(12)).take(count).map(Value.Register(_)),
      source
    )
  )

  /** A copy to the tensor memory at `dst` from the shared memory the descriptor in `src` names. */
  def tensorMemoryCopy(copy: Tcgen05.Copy, guarded: Boolean = false): Either[String, Definition] =
    rendered("tcgen05.cp", guarded)(MachineSemantics.tensorMemoryCopy(Render, _)(copy, destination, source))

  /** A tcgen05.mma of `kind` with the instruction descriptor `descriptor`, block-scaled where the kind says and sparse
    * as the descriptor says where `sparse`.
    */
  def tensorMemoryMultiply(kind: Tcgen05.Kind, descriptor: Value, sparse: Boolean = false)
      : Either[String, Definition] =
    rendered("tcgen05.mma")(
      MachineSemantics.tensorMemoryMultiply(_)(
        Tcgen05.Multiply(
          kind = kind,
          architecture = 100,
          pair = false,
          ws = false,
          vector = 0,
          shift = 0,
          shiftA = false,
          d = destination,
          a = source,
          aInTensorMemory = false,
          b = size,
          descriptor = descriptor,
          enable = barrier,
          disabled = Vec.empty,
          scales = Option.when(kind.scaled)((a = left, b = right)),
          metadata = Option.when(sparse)(mask),
          zeroColumns = None,
          sparseByDescriptor = sparse
        )
      )
    )

  def tensorMemoryShift(pair: Boolean, guarded: Boolean = false): Either[String, Definition] =
    rendered("tcgen05.shift", guarded)(MachineSemantics.tensorMemoryShift(Render, _)(destination, pair))

  def tensorMemoryCommit(multicast: Boolean, guarded: Boolean = false): Either[String, Definition] =
    rendered("tcgen05.commit", guarded)(
      MachineSemantics.tensorMemoryCommit(Render, _)(barrier, Option.when(multicast)(mask))
    )

  /** The machine definitions of a branch under the predicate `done`, as the explorer renders a predicated instruction.
    */
  def predicatedBranch(negated: Boolean): Vec[Definition] = StepRenderer.machine(
    StepRenderer.Spec("bra", Vec.empty, Vec.empty),
    Vec(PredicatedInstruction(Instruction.Branch(Pc(7)), Some(ExecutionPredicate(done, negated)))),
    Names(Operands())
  ).flatMap(_.toOption)

  /** The machine definitions of `instructions` under `done`, as the explorer renders a lowered instruction. */
  def lowered(instructions: Vec[PredicatedInstruction]): Vec[Either[Unrendered, Definition]] =
    StepRenderer.machine(StepRenderer.Spec("op", Vec.empty, Vec.empty), instructions, Names(Operands()))

  /** A synchronisation on a token a lane step moves from `src`, under `done` when `guarded`. */
  def synchroniseAfterMove(guarded: Boolean): Vec[PredicatedInstruction] = Vec(
    PredicatedInstruction(Instruction.Move(Register(8), source, 64), None),
    PredicatedInstruction(
      Instruction.Convergence(Register(8), ConvergenceAction.Synchronise),
      Option.when(guarded)(ExecutionPredicate(done, negated = false))
    )
  )

  /** A synchronisation on a token no step assigns and no operand names. */
  def synchroniseUnbound: Vec[PredicatedInstruction] =
    Vec(PredicatedInstruction(Instruction.Convergence(Register(40), ConvergenceAction.Synchronise), None))
}
