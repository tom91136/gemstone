package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.collection.*

/** The register and predicate slots, and the condition flags, an instruction reads and writes, taken from the lowered
  * instruction rather than from an issue. A register a case names outside a `destination` field is read, and a partial
  * write of one is also a read: the bits it leaves are the value it had. An instruction a predicate guards reads the
  * guard and writes its destinations only where the guard holds, so a guarded write is a read as well. A reference
  * names each slot once, in the order the lowering names them.
  */
// one row is SetPredicateCombined and reads a predicate operand; the other is IntegerBinary and reads none
@SuppressWarnings(Array("bonsai:duplicate-code"))
object RegisterUses {

  /** The slots and flags of one instruction: register and predicate slots, and implicit condition flags. */
  case class Slots(
      registersRead: Vec[Register],
      registersWritten: Vec[Register],
      predicatesRead: Vec[Predicate],
      predicatesWritten: Vec[Predicate],
      flagsRead: Vec[VMConditionCodes.Flag],
      flagsWritten: Vec[VMConditionCodes.Flag]
  ) derives CanEqual {
    def merge(other: Slots): Slots = Slots(
      registersRead ++ other.registersRead,
      registersWritten ++ other.registersWritten,
      predicatesRead ++ other.predicatesRead,
      predicatesWritten ++ other.predicatesWritten,
      flagsRead ++ other.flagsRead,
      flagsWritten ++ other.flagsWritten
    )

    def distinct: Slots =
      Slots(
        registersRead = registersRead.distinct,
        registersWritten = registersWritten.distinct,
        predicatesRead = predicatesRead.distinct,
        predicatesWritten = predicatesWritten.distinct,
        flagsRead = flagsRead.distinct,
        flagsWritten = flagsWritten.distinct
      )
  }

  object Slots {
    val empty: Slots = Slots(Vec.empty, Vec.empty, Vec.empty, Vec.empty, Vec.empty, Vec.empty)
  }

  def of(instruction: Instruction): Slots = {
    val slots = Builder()
    slots.instruction(instruction)
    slots.result
  }

  /** `predicated` as [[of]] sees it: a guard is a read, and the instruction's writes hold only where it holds, so each
    * is also a read of the slot it may leave unchanged.
    */
  def of(predicated: PredicatedInstruction): Slots = predicated.predication match {
    case None =>
      of(predicated.instruction)
    case Some(predication) =>
      val slots = of(predicated.instruction)
      slots.copy(
        registersRead = slots.registersRead ++ slots.registersWritten,
        predicatesRead = slots.predicatesRead ++ slots.predicatesWritten :+ predication.predicate,
        flagsRead = slots.flagsRead ++ slots.flagsWritten
      ).distinct
  }

  private class Builder {
    private val registersRead     = Vec.newBuilder[Register]
    private val registersWritten  = Vec.newBuilder[Register]
    private val predicatesRead    = Vec.newBuilder[Predicate]
    private val predicatesWritten = Vec.newBuilder[Predicate]
    private val flagsRead         = Vec.newBuilder[VMConditionCodes.Flag]
    private val flagsWritten      = Vec.newBuilder[VMConditionCodes.Flag]

    def readRegister(register: Register): Unit       = { registersRead += register; () }
    def writeRegister(register: Register): Unit      = { registersWritten += register; () }
    def readPredicate(predicate: Predicate): Unit    = { predicatesRead += predicate; () }
    def writePredicate(predicate: Predicate): Unit   = { predicatesWritten += predicate; () }
    def readFlag(flag: VMConditionCodes.Flag): Unit  = { flagsRead += flag; () }
    def writeFlag(flag: VMConditionCodes.Flag): Unit = { flagsWritten += flag; () }

    def readValue(value: Value): Unit = value match {
      case Value.Register(slot)   => readRegister(slot)
      case Value.Predicate(slot)  => readPredicate(slot)
      case Value.Address(base, _) => readValue(base)
      case _                      => ()
    }

    def readValues(values: Iterable[Value]): Unit              = values.foreach(readValue)
    def writeRegisters(registers: Iterable[Register]): Unit    = registers.foreach(writeRegister)
    def writeOptional(register: Option[Register]): Unit        = register.foreach(writeRegister)
    def writeOptionals(registers: Vec[Option[Register]]): Unit = registers.foreach(writeOptional)

    def result: Slots = Slots(
      registersRead = registersRead.result(),
      registersWritten = registersWritten.result(),
      predicatesRead = predicatesRead.result(),
      predicatesWritten = predicatesWritten.result(),
      flagsRead = flagsRead.result(),
      flagsWritten = flagsWritten.result()
    ).distinct

    def instruction(instruction: Instruction): Unit = instruction match {
      case Instruction.Tensor(operation)  => matrix(operation)
      case Instruction.Texture(operation) => texture(operation)
      case _: (Instruction.ConvertTF32 | Instruction.LoadParameter | Instruction.StoreParameter | Instruction.Move |
            Instruction.MoveVector | Instruction.UniformMove | Instruction.MovePredicate | Instruction.MoveSplit |
            Instruction.MoveCombine | Instruction.IntegerUnary | Instruction.BitFind | Instruction.DotProduct4 |
            Instruction.DotProduct2 | Instruction.SumAbsoluteDifference | Instruction.FindNthSet |
            Instruction.PackSaturated | Instruction.ConvertToFloat8 | Instruction.ConvertNarrow |
            Instruction.ConvertWiden | Instruction.ConvertFromFloat8 | Instruction.SpaceTest |
            Instruction.PackedSaturatingAdd | Instruction.VideoSimd | Instruction.VideoScalar |
            Instruction.VideoMultiplyAdd | Instruction.ConvertStochastic |
            Instruction.SelectSign) => valueOps(instruction)
      case _: (Instruction.TestProperty | Instruction.CompareSetCombined | Instruction.SetPredicateCombined |
            Instruction.StackAllocate | Instruction.StackSave | Instruction.StackRestore | Instruction.IntegerBinary |
            Instruction.FunnelShift | Instruction.Logic3 | Instruction.IntegerSubtractAdd |
            Instruction.IntegerAddWithCarry | Instruction.IntegerSubtractWithBorrow | Instruction.IntegerAddThree |
            Instruction.WriteConditionFlag | Instruction.ReadConditionFlag | Instruction.ReadConditionPredicate |
            Instruction.InvalidateConditionFlag | Instruction.MultiplyAdd | Instruction.FloatBinary |
            Instruction.FloatBinaryPacked | Instruction.FloatUnary | Instruction.MufuUnary |
            Instruction.FloatUnaryPacked | Instruction.FusedMultiplyAdd | Instruction.FusedMultiplyAddBf16 |
            Instruction.FusedMultiplyAddPacked) | Instruction.InvalidateAddThreeCarry => arithmeticOps(instruction)
      case _: (Instruction.Shuffle | Instruction.VoteBallot | Instruction.Elect | Instruction.WarpReduceFloat |
            Instruction.WarpMatch | Instruction.VotePredicate | Instruction.BitFieldExtract |
            Instruction.BitFieldInsert | Instruction.BitMask | Instruction.BytePermute | Instruction.Select |
            Instruction.Convert | Instruction.ConvertPacked | Instruction.CompareSet | Instruction.Load |
            Instruction.LoadVector | Instruction.Store | Instruction.StoreVector | Instruction.AsyncCopy |
            Instruction.AsyncBulkStore | Instruction.AsyncBulkCopy | Instruction.AsyncBulkReduce |
            Instruction.AsyncBulkFill | Instruction.AsyncTensor | Instruction.TensorMapReplace |
            Instruction.TensorMapCopy | Instruction.AsyncSharedStore) => warpMemoryOps(instruction)
      case _: (Instruction.AsyncSharedReduce | Instruction.ClusterMap | Instruction.ClusterRankOf |
            Instruction.ClusterBarrier | Instruction.GridBarrier | Instruction.ClusterTryCancel |
            Instruction.ClusterCancelStatus |
            Instruction.ClusterCancelCta | Instruction.TensorMemoryAllocate | Instruction.TensorMemoryDeallocate |
            Instruction.TensorMemoryClaim | Instruction.TensorMemoryRelease | Instruction.TensorMemoryLoad |
            Instruction.TensorMemoryStore | Instruction.TensorMemoryCopy | Instruction.TensorMemoryShift |
            Instruction.TensorMemoryMultiply | Instruction.TensorMemoryCommit | Instruction.MBarrierLoad |
            Instruction.MBarrierInit | Instruction.MBarrierArrive | Instruction.MBarrierPending |
            Instruction.MBarrierTransaction | Instruction.MBarrierTest | Instruction.MBarrierInvalidate |
            Instruction.AsyncCommit | Instruction.AsyncWait) |
          Instruction.TensorMemoryRelinquish => clusterTensorOps(instruction)
      case _: (Instruction.SharedLockLoad | Instruction.SharedStoreUnlock | Instruction.SetPredicate |
            Instruction.PredicateCompare | Instruction.PredicateUnary | Instruction.PredicateBinary |
            Instruction.Branch | Instruction.BranchTable | Instruction.BarrierSync | Instruction.BarrierArrive |
            Instruction.BarrierReduce | Instruction.Atomic | Instruction.AtomicCompareExchange |
            Instruction.WarpReduce | Instruction.Convergence | Instruction.ReturnStackPush |
            Instruction.ReturnStackPop | Instruction.ReturnStackSave | Instruction.WarpBarrier | Instruction.Call |
            Instruction.CallBuiltin) | Instruction.NoOp | Instruction.Yield | Instruction.Spin | Instruction.Fence |
          Instruction.Trap | Instruction.Return | Instruction.Exit => controlOps(instruction)
    }

    private def valueOps(instruction: Instruction): Unit = (instruction: @unchecked) match {
      case Instruction.ConvertTF32(destination = destination, source = source) =>
        writeRegister(destination); readValue(source)
      case Instruction.LoadParameter(destinations = destinations) =>
        writeRegisters(destinations)
      case Instruction.StoreParameter(source = source) =>
        readValue(source)
      case Instruction.Move(destination, source, _) =>
        writeRegister(destination); readValue(source)
      case Instruction.MoveVector(destinations, sources, _) =>
        writeRegisters(destinations); readValues(sources)
      case Instruction.UniformMove(destination, source) =>
        writeRegister(destination); readValue(source)
      case Instruction.MovePredicate(destination, source) =>
        writePredicate(destination); readValue(source)
      case Instruction.MoveSplit(destinations, source, partWidth) =>
        readValue(source)
        destinations.foreach {
          case Some(destination) =>
            writeRegister(destination)
            if (partWidth == 16) readRegister(destination)
          case None => ()
        }
      case Instruction.MoveCombine(destination, sources, _) =>
        writeRegister(destination); readValues(sources)
      case Instruction.IntegerUnary(_, destination, source, _) =>
        writeRegister(destination); readValue(source)
      case Instruction.BitFind(destination = destination, source = source) =>
        writeRegister(destination); readValue(source)
      case Instruction.DotProduct4(destination, left, right, addend, _, _) =>
        writeRegister(destination); readValues(Vec(left, right, addend))
      case Instruction.DotProduct2(destination = destination, left = left, right = right, addend = addend) =>
        writeRegister(destination); readValues(Vec(left, right, addend))
      case Instruction.SumAbsoluteDifference(destination, left, right, addend, _, _) =>
        writeRegister(destination); readValues(Vec(left, right, addend))
      case Instruction.FindNthSet(destination, mask, base, offset) =>
        writeRegister(destination); readValues(Vec(mask, base, offset))
      case Instruction.PackSaturated(destination, high, low, rest, _, _) =>
        writeRegister(destination); readValues(Vec(high, low, rest))
      case Instruction.ConvertToFloat8(destination = destination, high = high, low = low, rest = rest) =>
        writeRegister(destination); readValues(Vec(high, low, rest))
      case Instruction.ConvertNarrow(destination = destination, high = high, low = low, scale = scale) =>
        writeRegister(destination); readValues(Vec(high, low, scale))
      case Instruction.ConvertWiden(destination = destination, source = source, scale = scale) =>
        writeRegister(destination); readValues(Vec(source, scale))
      case Instruction.ConvertFromFloat8(destination, source, _) =>
        writeRegister(destination); readValue(source)
      case Instruction.SpaceTest(destination, address, _) =>
        writePredicate(destination); readValue(address)
      case Instruction.PackedSaturatingAdd(destination, left, right, _, _) =>
        writeRegister(destination); readValues(Vec(left, right))
      case Instruction.VideoSimd(destination = destination, left = left, right = right, addend = addend) =>
        writeRegister(destination); readValues(Vec(left, right, addend))
      case Instruction.VideoScalar(destination = destination, left = left, right = right, addend = addend) =>
        writeRegister(destination); readValues(Vec(left, right, addend))
      case Instruction.VideoMultiplyAdd(destination = destination, left = left, right = right, addend = addend) =>
        writeRegister(destination); readValues(Vec(left, right, addend))
      case Instruction.ConvertStochastic(destination = destination, high = high, low = low, bits = bits, rest = rest) =>
        writeRegister(destination); readValues(Vec(high, low, bits, rest))
      case Instruction.SelectSign(
            destination = destination,
            whenNonNegative = whenNonNegative,
            whenNegative = whenNegative,
            condition = condition
          ) =>
        writeRegister(destination); readValues(Vec(whenNonNegative, whenNegative, condition))
    }

    private def arithmeticOps(instruction: Instruction): Unit = (instruction: @unchecked) match {
      case Instruction.TestProperty(destination, source, _, _) =>
        writePredicate(destination); readValue(source)
      case Instruction.CompareSetCombined(
            destination = destination,
            left = left,
            right = right,
            predicate = predicate
          ) =>
        writeRegister(destination); readValues(Vec(left, right)); readPredicate(predicate)
      case Instruction.SetPredicateCombined(
            destination = destination,
            left = left,
            right = right,
            predicate = predicate
          ) =>
        writePredicate(destination); readValues(Vec(left, right)); readPredicate(predicate)
      case Instruction.StackAllocate(destination, size, _) =>
        writeRegister(destination); readValue(size)
      case Instruction.StackSave(destination) =>
        writeRegister(destination)
      case Instruction.StackRestore(source) =>
        readValue(source)
      case Instruction.IntegerBinary(_, destination, left, right, _) =>
        writeRegister(destination); readValues(Vec(left, right))
      case Instruction.FunnelShift(destination, low, high, amount, _, _) =>
        writeRegister(destination); readValues(Vec(low, high, amount))
      case Instruction.Logic3(destination, first, second, third, _, _) =>
        writeRegister(destination); readValues(Vec(first, second, third))
      case Instruction.IntegerSubtractAdd(destination, left, right, addend, _) =>
        writeRegister(destination); readValues(Vec(left, right, addend))
      case Instruction.IntegerAddWithCarry(destination, left, right, _, carryIn, carryOut) =>
        writeRegister(destination); readValues(Vec(left, right))
        if (carryIn) readFlag(VMConditionCodes.Flag.Carry)
        if (carryOut) writeFlag(VMConditionCodes.Flag.Carry)
      case Instruction.IntegerSubtractWithBorrow(destination, left, right, _, borrowIn, borrowOut) =>
        writeRegister(destination); readValues(Vec(left, right))
        if (borrowIn) readFlag(VMConditionCodes.Flag.Carry)
        if (borrowOut) writeFlag(VMConditionCodes.Flag.Carry)
      case Instruction.IntegerAddThree(
            destination = destination,
            first = first,
            second = second,
            third = third,
            carryMode = carryMode
          ) =>
        writeRegister(destination); readValues(Vec(first, second, third))
        carryMode match {
          case AddThreeCarryMode.None    => ()
          case AddThreeCarryMode.Produce => writeFlag(VMConditionCodes.Flag.Carry)
          case AddThreeCarryMode.Consume => readFlag(VMConditionCodes.Flag.Carry)
        }
      case Instruction.WriteConditionFlag(flag, value) =>
        writeFlag(flag); readValue(value)
      case Instruction.ReadConditionFlag(destination, flag) =>
        writeRegister(destination); readFlag(flag)
      case Instruction.ReadConditionPredicate(destination, flag) =>
        writePredicate(destination); readFlag(flag)
      case Instruction.InvalidateConditionFlag(flag) =>
        writeFlag(flag)
      case Instruction.InvalidateAddThreeCarry =>
        writeFlag(VMConditionCodes.Flag.Carry)
      case Instruction.MultiplyAdd(destination, left, right, addend, _) =>
        writeRegister(destination); readValues(Vec(left, right, addend))
      case Instruction.FloatBinary(destination = destination, left = left, right = right) =>
        writeRegister(destination); readValues(Vec(left, right))
      case Instruction.FloatBinaryPacked(destination = destination, left = left, right = right) =>
        writeRegister(destination); readValues(Vec(left, right))
      case Instruction.FloatUnary(destination = destination, source = source) =>
        writeRegister(destination); readValue(source)
      case Instruction.MufuUnary(destination = destination, source = source) =>
        writeRegister(destination); readValue(source)
      case Instruction.FloatUnaryPacked(_, destination, source, _) =>
        writeRegister(destination); readValue(source)
      case Instruction.FusedMultiplyAdd(destination = destination, left = left, right = right, addend = addend) =>
        writeRegister(destination); readValues(Vec(left, right, addend))
      case Instruction.FusedMultiplyAddBf16(destination, left, right, addend) =>
        writeRegister(destination); readValues(Vec(left, right, addend))
      case Instruction.FusedMultiplyAddPacked(destination, left, right, addend, _, _) =>
        writeRegister(destination); readValues(Vec(left, right, addend))
    }

    private def warpMemoryOps(instruction: Instruction): Unit = (instruction: @unchecked) match {
      case Instruction.Shuffle(_, destination, validity, source, lane, clamp, memberMask, _) =>
        writeRegister(destination); readValues(Vec(source, lane, clamp, memberMask))
        validity.foreach(readPredicate)
      case Instruction.VoteBallot(destination, predicate, memberMask, _) =>
        writeRegister(destination); readPredicate(predicate); readValue(memberMask)
      case Instruction.Elect(destination, predicate, memberMask) =>
        writeOptional(destination); readPredicate(predicate); readValue(memberMask)
      case Instruction.WarpReduceFloat(destination = destination, source = source, memberMask = memberMask) =>
        writeRegister(destination); readValues(Vec(source, memberMask))
      case Instruction.WarpMatch(destination, predicate, source, memberMask, _, _) =>
        writeOptional(destination); predicate.foreach(writePredicate); readValues(Vec(source, memberMask))
      case Instruction.VotePredicate(destination, predicate, memberMask, _, _) =>
        writePredicate(destination); readPredicate(predicate); readValue(memberMask)
      case Instruction.BitFieldExtract(destination, source, start, widthValue, _, _) =>
        writeRegister(destination); readValues(Vec(source, start, widthValue))
      case Instruction.BitFieldInsert(destination, insertValue, baseValue, bitPosition, bitLength, _) =>
        writeRegister(destination); readValues(Vec(insertValue, baseValue, bitPosition, bitLength))
      case Instruction.BitMask(destination, start, widthValue, _) =>
        writeRegister(destination); readValues(Vec(start, widthValue))
      case Instruction.BytePermute(destination, left, right, control, _) =>
        writeRegister(destination); readValues(Vec(left, right, control))
      case Instruction.Select(destination, whenTrue, whenFalse, predicate, _) =>
        writeRegister(destination); readValues(Vec(whenTrue, whenFalse)); readPredicate(predicate)
      case Instruction.Convert(destination = destination, source = source) =>
        writeRegister(destination); readValue(source)
      case Instruction.ConvertPacked(destination = destination, high = high, low = low) =>
        writeRegister(destination); readValues(Vec(high, low))
      case Instruction.CompareSet(destination = destination, left = left, right = right) =>
        writeRegister(destination); readValues(Vec(left, right))
      case Instruction.Load(destination = destination, address = address) =>
        writeRegister(destination); readValue(address)
      case Instruction.LoadVector(destinations = destinations, address = address) =>
        writeRegisters(destinations); readValue(address)
      case Instruction.Store(address, source, _, _) =>
        readValues(Vec(address, source))
      case Instruction.StoreVector(address, sources, _, _) =>
        readValue(address); readValues(sources)
      case Instruction.AsyncCopy(destination, source, _, sourceSize) =>
        readValues(Vec(destination, source, sourceSize))
      case Instruction.AsyncBulkStore(destination, source, size, byteMask) =>
        readValues(Vec(destination, source, size, byteMask))
      case Instruction.AsyncBulkCopy(destination, source, size, barrier, _, multicast, ignored, _) =>
        readValues(Vec(destination, source, size, barrier))
        multicast.foreach(readValue)
        ignored.foreach((left, right) => readValues(Vec(left, right)))
      case Instruction.AsyncBulkReduce(destination, source, size, barrier, _, _) =>
        readValues(Vec(destination, source, size))
        barrier.foreach(readValue)
      case Instruction.AsyncBulkFill(address, size) =>
        readValues(Vec(address, size))
      case Instruction.AsyncTensor(
            tensorMap = tensorMap,
            coordinates = coordinates,
            shared = shared,
            barrier = barrier,
            multicast = multicast,
            offsets = offsets
          ) =>
        readValue(tensorMap); readValues(coordinates); readValue(shared); readValues(offsets)
        barrier.foreach(readValue)
        multicast.foreach(readValue)
      case Instruction.TensorMapReplace(address, _, _, value) =>
        readValues(Vec(address, value))
      case Instruction.TensorMapCopy(destination, source) =>
        readValues(Vec(destination, source))
      case Instruction.AsyncSharedStore(address, sources, _, barrier) =>
        readValues(Vec(address, barrier)); readValues(sources)
    }

    private def clusterTensorOps(instruction: Instruction): Unit = (instruction: @unchecked) match {
      case Instruction.AsyncSharedReduce(address, source, _, _, barrier) =>
        readValues(Vec(address, source, barrier))
      case Instruction.ClusterMap(destination, address, rank) =>
        writeRegister(destination); readValues(Vec(address, rank))
      case Instruction.ClusterRankOf(destination, address) =>
        writeRegister(destination); readValue(address)
      case Instruction.ClusterBarrier(_) =>
        ()
      case Instruction.GridBarrier(flags, handle) =>
        readValue(flags); handle.foreach(readValue)
      case Instruction.ClusterTryCancel(response, barrier, _) =>
        readValues(Vec(response, barrier))
      case Instruction.ClusterCancelStatus(destination, response) =>
        writePredicate(destination); readValue(response)
      case Instruction.ClusterCancelCta(destination, responseLow, responseHigh, _) =>
        writeRegister(destination); readValues(Vec(responseLow, responseHigh))
      case Instruction.TensorMemoryAllocate(destination, columns) =>
        readValues(Vec(destination, columns))
      case Instruction.TensorMemoryDeallocate(address, columns) =>
        readValues(Vec(address, columns))
      case Instruction.TensorMemoryRelinquish =>
        ()
      case Instruction.TensorMemoryClaim(destination, claimed, granules) =>
        writeRegister(destination); writePredicate(claimed); readValue(granules)
      case Instruction.TensorMemoryRelease(mask) =>
        readValue(mask)
      case Instruction.TensorMemoryLoad(_, destinations, address, reduction) =>
        writeRegisters(destinations); readValue(address)
        reduction.foreach(value => writeRegister(value.destination))
      case Instruction.TensorMemoryStore(_, sources, address) =>
        readValues(sources); readValue(address)
      case Instruction.TensorMemoryCopy(_, address, descriptor) =>
        readValues(Vec(address, descriptor))
      case Instruction.TensorMemoryShift(address, _) =>
        readValue(address)
      case Instruction.TensorMemoryMultiply(multiply) =>
        tcgen05(multiply)
      case Instruction.TensorMemoryCommit(barrier, mask) =>
        readValue(barrier); mask.foreach(readValue)
      case Instruction.MBarrierLoad(address) =>
        readValue(address)
      case Instruction.MBarrierInit(address, count) =>
        readValues(Vec(address, count))
      case Instruction.MBarrierArrive(destination, address, count, expectedBytes, _, _) =>
        writeOptional(destination); readValues(Vec(address, count, expectedBytes))
      case Instruction.MBarrierPending(destination, token) =>
        writeRegister(destination); readValue(token)
      case Instruction.MBarrierTransaction(address, bytes, _) =>
        readValues(Vec(address, bytes))
      case Instruction.MBarrierTest(destination, address, phase, _) =>
        writePredicate(destination); readValues(Vec(address, phase))
      case Instruction.MBarrierInvalidate(address) =>
        readValue(address)
      case Instruction.AsyncCommit(_) =>
        ()
      case Instruction.AsyncWait(_, _) =>
        ()
    }

    private def controlOps(instruction: Instruction): Unit = (instruction: @unchecked) match {
      case Instruction.SharedLockLoad(destination, result, address, _) =>
        writePredicate(destination); writeRegister(result); readValue(address)
      case Instruction.SharedStoreUnlock(destination, address, source, _) =>
        writePredicate(destination); readValues(Vec(address, source))
      case Instruction.NoOp =>
        ()
      case Instruction.Yield =>
        ()
      case Instruction.Spin =>
        ()
      case Instruction.SetPredicate(destination = destination, left = left, right = right) =>
        writePredicate(destination); readValues(Vec(left, right))
      case Instruction.PredicateCompare(destination, _, left, right) =>
        writePredicate(destination); readPredicate(left); readPredicate(right)
      case Instruction.PredicateUnary(destination, source) =>
        writePredicate(destination); readPredicate(source)
      case Instruction.PredicateBinary(_, destination, left, right) =>
        writePredicate(destination); readPredicate(left); readPredicate(right)
      case Instruction.Branch(_) =>
        ()
      case Instruction.BranchTable(index, _) =>
        readValue(index)
      case Instruction.BarrierSync(id, count) =>
        readValue(id); count.foreach(readValue)
      case Instruction.BarrierArrive(id, count) =>
        readValues(Vec(id, count))
      case Instruction.BarrierReduce(result, id, predicate, count, _) =>
        result match {
          case BarrierResult.Combined(destination, _) => writePredicate(destination)
          case BarrierResult.Count(destination)       => writeRegister(destination)
        }
        readValue(id); readPredicate(predicate)
        count.foreach(readValue)
      case Instruction.Atomic(destination = destination, address = address, source = source) =>
        writeOptional(destination); readValues(Vec(address, source))
      case Instruction.AtomicCompareExchange(destination, address, comparator, source, _, _) =>
        writeRegister(destination); readValues(Vec(address, comparator, source))
      case Instruction.WarpReduce(destination, source, memberMask, _, _) =>
        writeRegister(destination); readValues(Vec(source, memberMask))
      case Instruction.Fence =>
        ()
      case Instruction.Convergence(token, action) =>
        action match {
          case ConvergenceAction.Setup                                 => writeRegister(token)
          case ConvergenceAction.Synchronise | ConvergenceAction.Break => readRegister(token)
        }
      case Instruction.ReturnStackPush(_) =>
        ()
      case Instruction.ReturnStackPop(destination) =>
        writeRegister(destination)
      case Instruction.ReturnStackSave(source) =>
        readRegister(source)
      case Instruction.WarpBarrier(memberMask, _) =>
        readValue(memberMask)
      case Instruction.Call(function, _, _) =>
        readValue(function)
      case Instruction.CallBuiltin(_, _, _) =>
        ()
      case Instruction.Trap =>
        ()
      case Instruction.Return =>
        ()
      case Instruction.Exit =>
        ()
    }

    private def matrix(operation: Matrix.Operation): Unit = operation match {
      case Matrix.Operation.Load(destinations = destinations, address = address, stride = stride) =>
        writeRegisters(destinations); readValues(Vec(address, stride))
      case Matrix.Operation.Store(sources = sources, address = address, stride = stride) =>
        readValues(sources); readValues(Vec(address, stride))
      case Matrix.Operation.Multiply(
            destinations = destinations,
            left = left,
            right = right,
            addend = addend,
            sparsity = sparsity,
            scaling = scaling
          ) =>
        writeRegisters(destinations); readValues(left); readValues(right); readValues(addend)
        sparsity.foreach(value => readValues(Vec(value.metadata, value.selector)))
        scaling.foreach(value =>
          readValues(Vec(value.aData, value.aByte, value.aThread, value.bData, value.bByte, value.bThread))
        )
      case Matrix.Operation.LoadMatrix(_, destinations, address, _) =>
        writeRegisters(destinations); readValue(address)
      case Matrix.Operation.StoreMatrix(_, sources, address, _) =>
        readValues(sources); readValue(address)
      case Matrix.Operation.Transpose(destination, source) =>
        writeRegister(destination); readValue(source)
      case Matrix.Operation.Widen(low, high, source) =>
        writeRegister(low); writeRegister(high); readValue(source)
      case Matrix.Operation.WarpgroupMultiply(product = product, a = a, b = b, scale = scale) =>
        matrix(product); readValues(a); readValues(b); readValue(scale)
    }

    private def texture(operation: Texture.Operation): Unit = operation match {
      case Texture.Operation.Sample(handle = handle, coordinates = coordinates, destinations = destinations) =>
        readValue(handle); readValues(coordinates); writeOptionals(destinations)
      case Texture.Operation.Gather(handle, coordinates, _, _, destinations) =>
        readValue(handle); readValues(coordinates); writeOptionals(destinations)
      case Texture.Operation.Query(handle, _, _, destination) =>
        readValue(handle); writeRegister(destination)
      case Texture.Operation.Load(handle, coordinates, _, _, destinations) =>
        readValue(handle); readValues(coordinates); writeOptionals(destinations)
      case Texture.Operation.Store(handle, coordinates, _, _, sources) =>
        readValue(handle); readValues(coordinates); readValues(sources)
      case Texture.Operation.Reduce(handle = handle, coordinates = coordinates, operand = operand, fetched = fetched) =>
        readValue(handle); readValues(coordinates); readValue(operand); writeOptional(fetched)
    }

    private def tcgen05(multiply: Tcgen05.Multiply): Unit = {
      readValues(Vec(multiply.d, multiply.a, multiply.b, multiply.descriptor, multiply.enable))
      readValues(multiply.disabled)
      multiply.scales.foreach((a, b) => readValues(Vec(a, b)))
      multiply.metadata.foreach(readValue)
      multiply.zeroColumns.foreach(readValue)
    }
  }
}
