package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

/** Rewrites SPIR-V specialization constants from a raw instruction stream.
  *
  * The module is scanned once for `OpDecorate SpecId`, `OpType*`, and `OpSpecConstant*` instructions. Each declared
  * constant is located by spec id, so a caller can either enumerate the constants or substitute their values before the
  * module reaches a backend.
  */
object Specialization {
  case class Constant(specId: Binary32, byteSize: Int > 0)

  private val DecorateOpcode           = Instruction.Definition.Decorate.opcodeNumber.raw
  private val SpecIdDecoration         = Decoration.SpecId
  private val TypeBoolOpcode           = Instruction.Definition.TypeBool.opcodeNumber.raw
  private val TypeIntOpcode            = Instruction.Definition.TypeInt.opcodeNumber.raw
  private val TypeFloatOpcode          = Instruction.Definition.TypeFloat.opcodeNumber.raw
  private val SpecConstantTrueOpcode   = Instruction.Definition.SpecConstantTrue.opcodeNumber.raw
  private val SpecConstantFalseOpcode  = Instruction.Definition.SpecConstantFalse.opcodeNumber.raw
  private val SpecConstantNumberOpcode = Instruction.Definition.SpecConstant.opcodeNumber.raw

  private case class TypeInfo(byteSize: Int > 0, boolean: Boolean)
  private case class Declaration(offset: Int, words: Int, typeId: Int, opcode: Int)
  private case class Located(offset: Int, byteSize: Int > 0, boolean: Boolean)
  private case class ModuleIndex(
      specIds: Map[Binary32, Vector[Int]],
      types: Map[Int, TypeInfo],
      declarations: Map[Int, Declaration]
  )

  def describe(image: Vec[Byte]): Either[String, Vec[Constant]] =
    index(image).map { module =>
      Vec.from(
        module.specIds.keys.toVector.sortBy(_.toLong).flatMap(specId =>
          module.specIds(specId).headOption.flatMap(id => locate(module, id)).map(constant =>
            Constant(specId, constant.byteSize)
          )
        )
      )
    }

  def specialize(image: Vec[Byte], values: Map[Binary32, Vec[Byte]]): Either[String, Vec[Byte]] =
    index(image).flatMap { module =>
      val bytes = Array.ofDim[Byte](image.size)
      val _     = image.copyToArray(bytes)
      values.toVector.sortBy(_._1.toLong).foldLeft[Either[String, Unit]](Right(())) {
        case (accumulated, (specId, value)) =>
          accumulated.flatMap { _ =>
            // Values that do not name a constant in this module have no effect.
            if (!module.specIds.contains(specId)) Right(())
            else module.specIds(specId).foldLeft[Either[String, Unit]](Right(())) { (previous, id) =>
              previous.flatMap { _ =>
                locate(module, id) match {
                  case None => Left(s"specialization constant $specId is not a scalar constant")
                  case Some(constant)
                      if value.size != constant.byteSize.raw && !(constant.boolean && value.size == 4) =>
                    Left(s"specialization constant $specId expects ${constant.byteSize} bytes")
                  case Some(constant) =>
                    val header = LittleEndian.decodeInt32(bytes, constant.offset)
                    if (constant.boolean)
                      LittleEndian.write(
                        ((header & 0xffff0000) |
                          (if (value.forall(_ == 0)) SpecConstantFalseOpcode else SpecConstantTrueOpcode)).toLong,
                        bytes,
                        constant.offset,
                        4
                      )
                    else {
                      java.util.Arrays.fill(
                        bytes,
                        constant.offset + 12,
                        constant.offset + (header >>> 16) * 4,
                        0.toByte
                      )
                      val _ = value.copyToArray(bytes, constant.offset + 12)
                    }
                    Right(())
                }
              }
            }
          }
      }.map(_ => Vec.from(bytes))
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def index(image: Vec[Byte]): Either[String, ModuleIndex] = {
    val bytes = image.toArray
    if (bytes.length < 20 || bytes.length % 4 != 0 || LittleEndian.decodeInt32(bytes, 0) != Binary.Magic)
      Left("invalid SPIR-V module")
    else {
      var specIds      = Map.empty[Binary32, Vector[Int]]
      var types        = Map.empty[Int, TypeInfo]
      var declarations = Map.empty[Int, Declaration]
      var offset       = 20
      var failure      = Option.empty[String]
      while (offset < bytes.length && failure.isEmpty) {
        val header = LittleEndian.decodeInt32(bytes, offset)
        val count  = header >>> 16
        val opcode = header & 0xffff
        if (count == 0 || count > (bytes.length - offset) / 4)
          failure = Some("malformed SPIR-V instruction stream")
        else {
          if (
            opcode == DecorateOpcode && count >= 4 && LittleEndian.decodeInt32(bytes, offset + 8) == SpecIdDecoration
          ) {
            val specId = Bits.unsigned32(LittleEndian.decodeInt32(bytes, offset + 12))
            val id     = LittleEndian.decodeInt32(bytes, offset + 4)
            specIds = specIds.updated(specId, specIds.getOrElse(specId, Vector.empty) :+ id)
          }
          if (opcode == TypeBoolOpcode && count >= 2)
            types = types.updated(LittleEndian.decodeInt32(bytes, offset + 4), TypeInfo(1, boolean = true))
          if ((opcode == TypeIntOpcode || opcode == TypeFloatOpcode) && count >= 3) {
            val width = LittleEndian.decodeInt32(bytes, offset + 8)
            if (width > 0 && width <= 64 && width % 8 == 0)
              types = types.updated(
                LittleEndian.decodeInt32(bytes, offset + 4),
                TypeInfo((width / 8).unsafe[Int > 0], boolean = false)
              )
          }
          if (
            (opcode == SpecConstantTrueOpcode || opcode == SpecConstantFalseOpcode ||
              opcode == SpecConstantNumberOpcode)
            && count >= 3
          )
            declarations = declarations.updated(
              LittleEndian.decodeInt32(bytes, offset + 8),
              Declaration(offset, count, LittleEndian.decodeInt32(bytes, offset + 4), opcode)
            )
          offset += count * 4
        }
      }
      failure.toLeft(ModuleIndex(specIds, types, declarations))
    }
  }

  private def locate(module: ModuleIndex, id: Int): Option[Located] =
    module.declarations.get(id).flatMap { declaration =>
      module.types.get(declaration.typeId).flatMap { typeInfo =>
        val specConstant =
          declaration.opcode == SpecConstantTrueOpcode || declaration.opcode == SpecConstantFalseOpcode
        val expectedWords = 3 + (if (typeInfo.boolean) 0 else (typeInfo.byteSize.raw + 3) / 4)
        Option.when(typeInfo.boolean == specConstant && declaration.words == expectedWords)(
          Located(declaration.offset, typeInfo.byteSize, typeInfo.boolean)
        )
      }
    }
}
