package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

/** Executes lowered NVIDIA instructions on the VM for one lane, with each operand and piece of state set first and read
  * back after.
  */
object NvidiaReference {
  private val Lane: Int >= 0 = 0.unsafe[Int >= 0]

  private val CarryOperands = Vec(Vec(0L, 0L, 0L), Vec(0xffffffffL, 1L, 0L), Vec(0xffffffffL, 0xffffffffL, 2L))

  /** Where a definition's operand or state lives: a register, the low then high register of a pair, a predicate, the
    * carry flag, IADD3's carry count or a condition flag.
    */
  enum Slot derives CanEqual {
    case Registers(slots: Vec[VMIR.Register])
    case Predicate(slot: VMIR.Predicate)
    case Carry
    case Carries
    case Flag(flag: VMConditionCodes.Flag)
  }

  /** The state `name` stands for when the lowering does not name it. */
  def state(name: String): Option[Slot] =
    if (name == "CC.CF") Some(Slot.Carry)
    else if (name == "iadd3.carry") Some(Slot.Carries)
    else VMConditionCodes.Flag.values.find(flag => s"cc.${flag.toString.toLowerCaseRoot}" == name).map(Slot.Flag(_))

  private def immediate(value: Long): VMIR.Value = VMIR.Value.Immediate(value)

  /** `steps` after `prologue` on one lane, with `inputs` set before and the values of `outputs` after, or why they do
    * not execute.
    */
  def run(
      prologue: Vec[VMIR.Instruction],
      steps: Vec[VMIR.Instruction],
      registerCount: Int >= 0,
      predicateCount: Int >= 0,
      inputs: Vec[(Slot, Type, Long)],
      outputs: Vec[(Slot, Type)],
      memory: AddressSpace
  ): Either[String, Vec[Long]] = {
    val scratch                    = mutable.ArrayBuffer.empty[VMIR.Register]
    def temporary(): VMIR.Register = {
      scratch += VMIR.Register((registerCount.raw + scratch.size).unsafe[Int >= 0])
      scratch.last
    }
    val set = inputs.flatMap { (slot, tpe, value) =>
      val bits = Bits.truncate(value, tpe.bits)
      slot match {
        case Slot.Registers(Vec(single)) => Vec(VMIR.Instruction.Move(single, immediate(bits), 64))
        case Slot.Registers(registers)   =>
          registers.zipWithIndex.map((register, index) =>
            VMIR.Instruction.Move(register, immediate(Bits.truncate(bits >>> (32 * index), 32)), 64)
          )
        case Slot.Predicate(predicate) => Vec(VMIR.Instruction.MovePredicate(predicate, immediate(bits)))
        case Slot.Carry                =>
          Vec(VMIR.Instruction.IntegerAddWithCarry(
            temporary(),
            immediate(0xffffffffL),
            immediate(bits),
            32,
            false,
            true
          ))
        case Slot.Carries =>
          val operands = CarryOperands(bits.toInt)
          Vec(VMIR.Instruction.IntegerAddThree(
            temporary(),
            immediate(operands(0)),
            immediate(operands(1)),
            immediate(operands(2)),
            32,
            VMIR.AddThreeCarryMode.Produce,
            false,
            false,
            false
          ))
        case Slot.Flag(flag) => Vec(VMIR.Instruction.WriteConditionFlag(flag, immediate(bits)))
      }
    }
    val read = outputs.map { (slot, _) =>
      slot match {
        case Slot.Carry =>
          val target = temporary()
          (
            Slot.Registers(Vec(target)),
            Vec(VMIR.Instruction.IntegerAddWithCarry(target, immediate(0L), immediate(0L), 32, true, false))
          )
        case Slot.Carries =>
          val target = temporary()
          (
            Slot.Registers(Vec(target)),
            Vec(VMIR.Instruction.IntegerAddThree(
              target,
              immediate(0L),
              immediate(0L),
              immediate(0L),
              32,
              VMIR.AddThreeCarryMode.Consume,
              false,
              false,
              false
            ))
          )
        case Slot.Flag(flag) =>
          val target = temporary()
          (Slot.Registers(Vec(target)), Vec(VMIR.Instruction.ReadConditionFlag(target, flag)))
        case other => (other, Vec.empty[VMIR.Instruction])
      }
    }
    val instructions = (prologue ++ set ++ steps ++ read.flatMap(_._2) :+ VMIR.Instruction.Return)
      .map(VMIR.PredicatedInstruction(_, None))
    NvidiaHarness.run(instructions, (registerCount.raw + scratch.size).unsafe[Int >= 0], predicateCount, 1, memory) {
      view =>
        read.zip(outputs).map { case ((slot, _), (_, tpe)) =>
          slot match {
            case Slot.Registers(registers) =>
              tpe.represent(
                registers.zipWithIndex.foldLeft(0L) { case (value, (register, index)) =>
                  val raw = view.register(Lane, register.unwrap)
                  if (registers.size == 1) raw else value | (Bits.truncate(raw, 32) << (32 * index))
                }
              )
            case Slot.Predicate(predicate) => if (view.predicate(Lane, predicate.unwrap).getOrElse(false)) 1L else 0L
            case _                         => 0L
          }
        }
    }
  }

  /** Each parameter and result at the slot `slot` names it, or the first name it does not know. */
  def slots(names: Vec[String], slot: String => Option[Slot]): Either[String, Vec[Slot]] = {
    val found = names.map(name => slot(name).orElse(state(name)).toRight(s"no slot for $name"))
    found.collectFirst { case Left(error) => error }.toLeft(found.collect { case Right(resolved) => resolved })
  }
}
