package net.kurobako.gemstone.isa.amdgpu.asm

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.EncodingConstants.*

private object ImageOperands {
  def supports(layout: InstructionLayout): Boolean       = layout.format.isImage
  private def modern(layout: InstructionLayout): Boolean =
    Set(EncodingFormat.VIMAGE, EncodingFormat.VSAMPLE)(layout.format)

  def modifier(
      value: Modifier,
      layout: InstructionLayout,
      definition: Opcode
  ): Option[Map[EncodingField, BigInt]] =
    value.value.flatMap { text =>
      val field = value.name.raw match {
        case "dim"   => Some(EncodingField.DIM)
        case "th"    => Some(EncodingField.TH)
        case "scope" => Some(EncodingField.SCOPE)
        case _       => None
      }
      field.filter(layout.byName.contains(_)).flatMap { name =>
        val number = name match {
          case EncodingField.DIM   => ImageDimension.values.find(_.assemblyName == text.raw).map(_.encoding)
          case EncodingField.SCOPE =>
            Option.when(text.raw.startsWith("SCOPE_"))(text.raw.stripPrefix("SCOPE_")).flatMap(MemoryScopes.get)
          case _ =>
            val prefix =
              if (
                ImageMetadata.forOpcode(definition).exists(_.atomic) ||
                definition.meta.encodings.exists(_.groups.contains(InstructionGroup.ATOMIC))
              ) "TH_ATOMIC_"
              else if (
                ImageMetadata.forOpcode(definition).exists(_.store) ||
                definition.meta.encodings.exists(value =>
                  value.format == layout.format &&
                    value.operands.exists(operand => operand.output && operand.kind == OperandKind.GPUMEM)
                )
              ) "TH_STORE_"
              else "TH_LOAD_"
            Option.when(text.raw.startsWith(prefix))(text.raw.stripPrefix(prefix)).flatMap(value =>
              (if (prefix == "TH_ATOMIC_") AtomicHints else TemporalHints).get(value)
            )
        }
        number.orElse(text.raw.toIntOption).filter(value => layout.byName(name).put(BigInt(value)).nonEmpty).map(
          value => Map(name -> BigInt(value))
        )
      }
    }

  def modifiers(word: InstructionWord, layout: InstructionLayout): Vec[Modifier] = {
    val dimension = Option.when(supports(layout) && layout.byName.contains(EncodingField.DIM))(
      Modifier(
        "dim",
        Some(ImageDimension.fromOrdinal(layout.get(
          word,
          EncodingField.DIM
        ).toInt).assemblyName.unsafe[NonEmpty[String]])
      )
    )
    val cache = Vec(EncodingField.TH, EncodingField.SCOPE).filter(layout.byName.contains(_)).flatMap { field =>
      val value = layout.get(word, field)
      Option.when(value != 0)(Modifier(
        if (field == EncodingField.TH) "th" else "scope",
        Some(value.toString.unsafe[NonEmpty[String]])
      ))
    }
    dimension.toVec ++ cache
  }

  private def enabled(fields: Map[EncodingField, BigInt], name: EncodingField): Boolean =
    fields.getOrElse(name, BigInt(0)) != 0

  private def addressCount(opcode: Opcode, layout: InstructionLayout, fields: Map[EncodingField, BigInt]): Int =
    ImageMetadata.forOpcode(opcode).get.addressWords(
      dimension = ImageDimension.fromOrdinal(fields.getOrElse(EncodingField.DIM, BigInt(0)).toInt),
      a16 = enabled(fields, EncodingField.A16),
      packedGradients = layout.isa == ISA.RDNA1
    )

  private def dataCount(opcode: Opcode, fields: Map[EncodingField, BigInt]): Int =
    ImageMetadata.forOpcode(opcode).get.dataWords(
      mask = fields.getOrElse(EncodingField.DMASK, BigInt(0)).toInt,
      d16 = enabled(fields, EncodingField.D16),
      tfe = enabled(fields, EncodingField.TFE)
    )

  private def vector(operand: Operand): Option[(Int, Int)] = operand match {
    case Operand.Register(RegisterFile.VectorRegister, first, last) if first >= 0 && last >= first && last < 256 =>
      Some(first -> (last - first + 1))
    case _ => None
  }
  private def scalar(operand: Operand, count: Int, layout: InstructionLayout): Option[Int] = operand match {
    case Operand.Register(RegisterFile.Scalar, first, last)
        if first >= 0 && last < 128 && first % 4 == 0 && last - first + 1 == count => Some(first)
    case Operand.Register(RegisterFile.TrapTemporary, first, last) =>
      OperandEncoding.Catalogue.forKind(layout.isa, OperandKind.SRC).flatMap { encoding =>
        for {
          start <- encoding.selectors.get(s"ttmp$first")
          end   <- encoding.selectors.get(s"ttmp$last")
          value <- scalar(Operand.Register(RegisterFile.Scalar, start, end), count, layout)
        } yield value
      }
    case _ => None
  }
  private def addresses(layout: InstructionLayout): Vector[EncodingField] =
    if (modern(layout)) (0 until (if (layout.format == EncodingFormat.VSAMPLE) 4 else 5)).map(index =>
      EncodingField.ImageAddresses(index)
    ).toVector
    else Vector(EncodingField.VADDR) ++
      layout.fields.map(_.name).filter(name => name.isImageAddress && name != EncodingField.VADDR).sortBy(_.ordinal)

  def encode(
      instruction: Instruction,
      layout: InstructionLayout,
      fields: Map[EncodingField, BigInt]
  ): Option[Map[EncodingField, BigInt]] = {
    val opcode = instruction.definition
    if (!ImageMetadata.forOpcode(opcode).nonEmpty) None
    else {
      val count        = addressCount(opcode, layout, fields)
      val names        = addresses(layout)
      val hasDimension = layout.byName.contains(EncodingField.DIM) || ImageMetadata.forOpcode(opcode).get.bvh
      val nsa          = layout.format.isNonSequentialImage
      instruction.operands match {
        case data +: address +: resource +: remaining => for {
            destination <- (data match {
              case Operand.Register(RegisterFile.Accumulator, first, last)
                  if layout.byName.contains(EncodingField.ACC) =>
                vector(Operand.Register(RegisterFile.VectorRegister, first, last))
              case _ => vector(data)
            }).filter(_._2 == dataCount(opcode, fields)).map(_._1)
            descriptor <-
              scalar(
                resource,
                if (enabled(fields, EncodingField.R128) || ImageMetadata.forOpcode(opcode).get.bvh) 4 else 8,
                layout
              )
            sampler <- remaining match {
              case Vec()      => Some(Map.empty[EncodingField, BigInt])
              case Vec(value) =>
                scalar(value, 4, layout).map(value =>
                  Map((if (modern(layout)) EncodingField.SAMP else EncodingField.SSAMP) -> BigInt(value))
                )
              case _ => None
            }
            source <- address match {
              case Operand.RegisterList(values) if nsa || modern(layout) =>
                values.traverse(vector(_)).filter { registers =>
                  registers.nonEmpty && registers.size <= names.size &&
                  registers.zipWithIndex.forall { case ((_, width), index) =>
                    width == (if (index == registers.size - 1) count - index else 1)
                  } &&
                  registers.size == count.min(names.size) &&
                  (modern(layout) || (registers.size - 2) / 4 + 1 == (layout.bits - 64) / 32)
                }.map(_.map(_._1))
              case value if !nsa =>
                vector(value).filter { case (_, width) =>
                  if (hasDimension) width == count ||
                  (count == 3 && width == 4) ||
                  (count > 4 && count <= 8 && width == 8)
                  else width <= 16
                }.map { case (first, _) =>
                  if (modern(layout)) Vec.from(0 until count.min(names.size)).map(_ + first) else Vec(first)
                }
              case _ => None
            }
            nsaFields = Option.when(nsa)(EncodingField.NSA -> BigInt((layout.bits - 64) / 32)).toMap
          } yield Map(
            EncodingField.VDATA                                               -> BigInt(destination),
            (if (modern(layout)) EncodingField.RSRC else EncodingField.SRSRC) -> BigInt(descriptor)
          ) ++
            (if (ImageMetadata.forOpcode(opcode).get.bvh)
               Map(EncodingField.DMASK -> BigInt(15), EncodingField.UNORM -> BigInt(1), EncodingField.R128 -> BigInt(1))
             else Map.empty[EncodingField, BigInt]) ++
            (data match {
              case Operand.Register(RegisterFile.Accumulator, _, _) => Map(EncodingField.ACC -> BigInt(1))
              case _                                                => Map.empty[EncodingField, BigInt]
            }) ++ sampler ++ source.zip(names.toVec).map { case (value, name) => name -> BigInt(value) }.toMap ++
            nsaFields
        case _ => None
      }
    }
  }

  def decode(definition: Opcode, word: InstructionWord, layout: InstructionLayout): Option[Vec[Operand]] =
    if (!ImageMetadata.forOpcode(definition).nonEmpty) None
    else {
      val fields = layout.fields.map(field => field.name -> BigInt(field.get(word))).toMap
      val count  = addressCount(definition, layout, fields)
      def register(file: RegisterFile, first: Int, size: Int): Operand = Operand.Register(file, first, first + size - 1)
      val data                                                         =
        register(
          if (layout.get(word, EncodingField.ACC) != 0) RegisterFile.Accumulator else RegisterFile.VectorRegister,
          layout.get(word, EncodingField.VDATA).toInt,
          dataCount(definition, fields)
        )
      val nsa = layout.get(word, EncodingField.NSA).toInt
      if (!modern(layout) && nsa != (layout.bits - 64) / 32) None
      else {
        val names  = addresses(layout)
        val source = if (nsa != 0 || modern(layout)) {
          val regs = names.take(count.min(names.size)).zipWithIndex.map { case (name, index) =>
            register(
              RegisterFile.VectorRegister,
              layout.get(word, name).toInt,
              if (index == names.size - 1) count - index else 1
            )
          }.toVec
          if (regs.size == 1) regs.head else Operand.RegisterList(regs)
        } else register(
          RegisterFile.VectorRegister,
          layout.get(word, EncodingField.VADDR).toInt,
          if (layout.byName.contains(EncodingField.DIM) || ImageMetadata.forOpcode(definition).get.bvh) count else 1
        )
        val resource = register(
          RegisterFile.Scalar,
          layout.get(word, if (modern(layout)) EncodingField.RSRC else EncodingField.SRSRC).toInt,
          if (enabled(fields, EncodingField.R128) || ImageMetadata.forOpcode(definition).get.bvh) 4 else 8
        )
        val sampler = Option.when(definition.meta.encodings.exists(value =>
          value.format == layout.format &&
            value.operands.exists(info =>
              info.field.contains(EncodingField.SSAMP) || info.field.contains(EncodingField.SAMP)
            )
        ))(
          register(
            RegisterFile.Scalar,
            layout.get(word, if (modern(layout)) EncodingField.SAMP else EncodingField.SSAMP).toInt,
            4
          )
        )
        Some(Vec(data, source, resource) ++ sampler.toVec)
      }
    }
}
