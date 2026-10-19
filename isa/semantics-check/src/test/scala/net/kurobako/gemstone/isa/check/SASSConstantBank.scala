package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

/** The constant bank SASS instances read, bound at its base, holding the bytes of a harness's run; it cannot be
  * written. Instances lower with their constant operands at default offsets, and `moved` takes each one a run moves to
  * the offset it reads.
  */
class SASSConstantBank(harness: NvidiaHarness, moved: Vec[(Long, Long)]) extends AddressSpace {
  private val Region = AllocationId(1L.unsafe[Long > 0])

  def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
    Right(Vec.tabulate(size.raw)(index => NvidiaHarness.constant(harness.seed, offset.raw + index)))

  def write(region: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
    Left(Fault.Memory("the constant bank is read-only"))

  def address(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
    Right(GuestAddress(InstructionSemantics.ConstantBase + offset))

  def locate(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location] = {
    val lowered = address.raw - InstructionSemantics.ConstantBase
    // a wide operand reads its words from the operand's offset up
    val offset = moved.collectFirst { case (from, to) if lowered >= from && lowered < from + 16 => to + lowered - from }
      .getOrElse(lowered)
    if (offset >= 0L && offset + size <= InstructionSemantics.ConstantBytes)
      Right(AddressSpace.Location.Within(Region, offset.unsafe[Long >= 0]))
    else Left(Fault.Memory(s"${address.raw.toHexString} is outside the constant bank"))
  }
}
