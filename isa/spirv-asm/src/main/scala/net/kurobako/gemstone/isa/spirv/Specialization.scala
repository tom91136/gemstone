package net.kurobako.gemstone.isa.spirv

import Operand.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*

/** Enumerates and substitutes scalar specialization constants, located through their `SpecId` decorations. */
object Specialization extends Backend.Specialization {

  private case class Scalar(index: Int, byteSize: Int > 0, boolean: Boolean)
  private case class Constants(specIds: Map[Binary32, Vector[Int]], scalars: Map[Int, Scalar])

  override def describe(image: Vec[Byte]): Either[Fault, Vec[SpecializationInfo]] =
    decode(image).map { module =>
      val found = constants(module)
      Vec.from(
        found.specIds.keys.toVector.sortBy(_.toLong).flatMap(specId =>
          found.specIds(specId).headOption.flatMap(found.scalars.get).map(scalar =>
            SpecializationInfo(specId, scalar.byteSize)
          )
        )
      )
    }

  override def specialize(image: Vec[Byte], values: Vec[SpecializationConstant]): Either[Fault, Vec[Byte]] =
    decode(image).flatMap(specialize(_, values)).flatMap(module =>
      ISA.encode(module).left.map(error => Fault.InvalidRequest(s"specialization: ${error.message}"))
    )

  def specialize(module: Module, values: Vec[SpecializationConstant]): Either[Fault, Module] = {
    val found        = constants(module)
    val instructions = module.instructions.toArray
    values.sortBy(_.specId.toLong).foldLeft[Either[Fault, Unit]](Right(())) { (accumulated, value) =>
      // values that do not name a constant in this module have no effect
      found.specIds.getOrElse(value.specId, Vector.empty).foldLeft(accumulated) { (previous, id) =>
        previous.flatMap { _ =>
          found.scalars.get(id) match {
            case None => Left(failure(s"specialization constant ${value.specId} is not a scalar constant"))
            case Some(scalar)
                if value.value.size != scalar.byteSize.raw && !(scalar.boolean && value.value.size == 4) =>
              Left(failure(s"specialization constant ${value.specId} expects ${scalar.byteSize} bytes"))
            case Some(scalar) =>
              instructions(scalar.index) = instructions(scalar.index) match {
                case Instruction.SpecConstantTrue(t, d) if value.value.forall(_ == 0) =>
                  Instruction.SpecConstantFalse(t, d)
                case Instruction.SpecConstantFalse(t, d) if value.value.exists(_ != 0) =>
                  Instruction.SpecConstantTrue(t, d)
                case Instruction.SpecConstant(t, d, literal) =>
                  val words = Vec.tabulate(literal.words.length)(word =>
                    Word((0 until 4).foldLeft(0) { (bits, byte) =>
                      bits | ((value.value.lift(word * 4 + byte).getOrElse(0: Byte) & 0xff) << (byte * 8))
                    })
                  )
                  Instruction.SpecConstant(t, d, literal.copy(words = words.unsafe))
                case unchanged => unchanged
              }
              Right(())
          }
        }
      }
    }.map(_ => module.copy(instructions = Vec.from(instructions)))
  }

  private def failure(detail: String): Fault = Fault.InvalidRequest(s"specialization: $detail")

  private def decode(image: Vec[Byte]): Either[Fault, Module] =
    ISA.decode(image).left.map(error => failure(error.message))

  private def constants(module: Module): Constants = {
    val specIds = module.instructions.collect {
      case Instruction.Decorate(target, decoration)
          if unsigned(decoration) == Decoration.SpecId && decoration.words.length >= 2 =>
        Bits.unsigned32(decoration.words(1).raw) -> id(target)
    }.groupMap(_._1)(_._2).view.mapValues(_.toVector).toMap
    val sizes = module.instructions.collect {
      case Instruction.TypeBool(dest)            => id(dest) -> Some(1: Int > 0)
      case Instruction.TypeInt(dest, width, _)   => id(dest) -> bytes(width)
      case Instruction.TypeFloat(dest, width, _) => id(dest) -> bytes(width)
    }.collect { case (typeId, Some(size)) => typeId -> size }.toMap
    val booleans = module.instructions.collect { case Instruction.TypeBool(dest) => id(dest) }.toSet
    val scalars  = module.instructions.zipWithIndex.collect {
      case (Instruction.SpecConstantTrue(t, d), index) if booleans.contains(id(t)) =>
        id(d) -> Scalar(index, 1, boolean = true)
      case (Instruction.SpecConstantFalse(t, d), index) if booleans.contains(id(t)) =>
        id(d) -> Scalar(index, 1, boolean = true)
      case (Instruction.SpecConstant(t, d, literal), index)
          if !booleans.contains(id(t)) && sizes.get(id(t)).exists(size => literal.words.length == (size.raw + 3) / 4) =>
        id(d) -> Scalar(index, sizes(id(t)), boolean = false)
    }.toMap
    Constants(specIds, scalars)
  }

  private def bytes(width: Operand): Option[Int > 0] = {
    val bits = width.words.head.raw
    Option.when(bits > 0 && bits <= 64 && bits % 8 == 0)((bits / 8).unsafe[Int > 0])
  }
}
