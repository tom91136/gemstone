package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.{LittleEndian, ScalarByteWidth}

/** Rewrites SPIR-V specialization constants from a raw instruction stream.
  *
  * The module is scanned once for `OpDecorate SpecId`, `OpType*`, and `OpSpecConstant*` instructions. Each declared
  * constant is located by spec id, so a caller can either enumerate the constants or substitute their values before the
  * module reaches a backend.
  */
object Specialization {
  final case class Constant(specId: Int, byteSize: Int)

  private val DecorateOpcode           = Instruction.Definition.Decorate.opcodeNumber.value
  private val SpecIdDecoration         = 1
  private val TypeBoolOpcode           = Instruction.Definition.TypeBool.opcodeNumber.value
  private val TypeIntOpcode            = Instruction.Definition.TypeInt.opcodeNumber.value
  private val TypeFloatOpcode          = Instruction.Definition.TypeFloat.opcodeNumber.value
  private val SpecConstantTrueOpcode   = Instruction.Definition.SpecConstantTrue.opcodeNumber.value
  private val SpecConstantFalseOpcode  = Instruction.Definition.SpecConstantFalse.opcodeNumber.value
  private val SpecConstantNumberOpcode = Instruction.Definition.SpecConstant.opcodeNumber.value

  private final case class TypeInfo(byteSize: Int, boolean: Boolean)
  private final case class Declaration(offset: Int, words: Int, typeId: Int, opcode: Int)
  private final case class Located(offset: Int, byteSize: Int, boolean: Boolean)
  private final case class ModuleIndex(
      specIds: Map[Int, Int],
      types: Map[Int, TypeInfo],
      declarations: Map[Int, Declaration]
  )

  def describe(image: Vec[Byte]): Either[String, Vec[Constant]] =
    index(image).map { module =>
      Vec.from(
        module.specIds.keys.toVector.sorted.flatMap(specId =>
          locate(module, specId).map(constant => Constant(specId, constant.byteSize))
        )
      )
    }

  def specialize(image: Vec[Byte], values: Map[Int, Vec[Byte]]): Either[String, Vec[Byte]] =
    index(image).flatMap { module =>
      val bytes = Array.ofDim[Byte](image.size)
      val _     = image.copyToArray(bytes)
      values.toVector.sortBy(_._1).foldLeft[Either[String, Unit]](Right(())) { case (accumulated, (specId, value)) =>
        accumulated.flatMap { _ =>
          // Values that do not name a constant in this module have no effect.
          if (!module.specIds.contains(specId)) Right(())
          else
            locate(module, specId) match {
              case None => Left(s"specialization constant $specId is not a scalar constant")
              case Some(constant) if value.size != constant.byteSize =>
                Left(s"specialization constant $specId expects ${constant.byteSize} bytes")
              case Some(constant) =>
                val header = LittleEndian.decodeInt32(bytes, constant.offset)
                if (constant.boolean)
                  LittleEndian.write(
                    ((header & 0xffff0000) |
                      (if (value(0) == 0) SpecConstantFalseOpcode else SpecConstantTrueOpcode)).toLong,
                    bytes,
                    constant.offset,
                    ScalarByteWidth.Dword
                  )
                else {
                  java.util.Arrays.fill(bytes, constant.offset + 12, constant.offset + (header >>> 16) * 4, 0.toByte)
                  val _ = value.copyToArray(bytes, constant.offset + 12)
                }
                Right(())
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
      var specIds      = Map.empty[Int, Int]
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
          if (opcode == DecorateOpcode && count >= 4 && LittleEndian.decodeInt32(bytes, offset + 8) == SpecIdDecoration)
            specIds =
              specIds.updated(LittleEndian.decodeInt32(bytes, offset + 12), LittleEndian.decodeInt32(bytes, offset + 4))
          if (opcode == TypeBoolOpcode && count >= 2)
            types = types.updated(LittleEndian.decodeInt32(bytes, offset + 4), TypeInfo(1, boolean = true))
          if ((opcode == TypeIntOpcode || opcode == TypeFloatOpcode) && count >= 3) {
            val width = LittleEndian.decodeInt32(bytes, offset + 8)
            if (width != 0 && width <= 64 && width % 8 == 0)
              types = types.updated(LittleEndian.decodeInt32(bytes, offset + 4), TypeInfo(width / 8, boolean = false))
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

  private def locate(module: ModuleIndex, specId: Int): Option[Located] =
    module.specIds.get(specId).flatMap { id =>
      module.declarations.get(id).flatMap { declaration =>
        module.types.get(declaration.typeId).flatMap { typeInfo =>
          val specConstant =
            declaration.opcode == SpecConstantTrueOpcode || declaration.opcode == SpecConstantFalseOpcode
          val expectedWords = 3 + (if (typeInfo.boolean) 0 else (typeInfo.byteSize + 3) / 4)
          Option.when(typeInfo.boolean == specConstant && declaration.words == expectedWords)(
            Located(declaration.offset, typeInfo.byteSize, typeInfo.boolean)
          )
        }
      }
    }
}
