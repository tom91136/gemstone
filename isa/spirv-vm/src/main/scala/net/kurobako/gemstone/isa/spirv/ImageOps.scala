package net.kurobako.gemstone.isa.spirv

import Op.*
import VMInvocation.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.vm.*

/** Image views, samplers, texel access and sampling. Descriptor words are read once per memory state. */
private object ImageOps {
  import Operand.*

  /** Runs the image operation `instruction` names: sampling leaves the position in place while the invocation waits for
    * its quad's implicit derivatives.
    */
  def execute(execution: InvocationState, instruction: Instruction): AddressSpace.ReadResult[Unit] = {
    val program = execution.values.program
    val memory  = execution.memory
    def sampling(body: (InvocationState, MemoryState, InvocationValues) => AddressSpace.ReadResult[Unit])
        : AddressSpace.ReadResult[Unit] =
      body(execution, memory, execution.values).map(_ => if (execution.quadRequest.isEmpty) execution.pc += 1)
    def imaging(body: (InvocationState, MemoryState, InvocationValues) => AddressSpace.ReadResult[Unit])
        : AddressSpace.ReadResult[Unit] =
      body(execution, memory, execution.values).map(_ => execution.pc += 1)
    instruction match {
      case Instruction.SampledImage(resultType, dest, source, sampler) =>
        sampling { (_, _, values) =>
          for {
            image <- address(values, id(source))
            state <- address(values, id(sampler))
          } yield values.update(id(dest), VMValue.Address(id(resultType), image.copy(sampler = Some(state))))
        }
      case Instruction.Image(resultType, dest, source) =>
        sampling { (_, _, values) =>
          address(values, id(source)).map(image =>
            values.update(id(dest), VMValue.Address(id(resultType), image.copy(sampler = None)))
          )
        }
      case Instruction.ImageQueryLevels(t, d, i) =>
        sampling { (_, memory, values) =>
          for {
            image  <- address(values, id(i))
            levels <- imageLevels(memory, image)
          } yield values.update(id(d), VMValue.Bits(id(t), Array(levels.toLong)))
        }
      case Instruction.ImageQuerySizeLod(t, d, i, lod) =>
        sampling { (_, memory, values) =>
          for {
            level      <- scalar(values, id(lod))
            image      <- address(values, id(i))
            dimensions <-
              view(program, image, cubeDimensions = 2, "image mip size query requires a sampled image")
            levels <- imageLevels(memory, image)
            bounded = if (program.vulkan) Math.max(0, Math.min(levels - 1, level.toInt)) else level.toInt
            data <- imageLevel(memory, image, bounded)
          } yield values.update(
            id(d),
            VMValue.Bits(
              id(t),
              (if (dimensions._1 == 1) Array(data.width)
               else if (dimensions._1 == 3) Array(data.width, data.height, data.depth)
               else Array(data.width, data.height)) ++
                (if (dimensions._2)
                   Array(if (dimensions._3) data.layers / 6 else data.layers)
                 else Array.empty[Long])
            )
          )
        }
      case Instruction.ImageQueryOrder(t, d, i)   => channelQuery(execution, t, d, i, "order")(_._1)
      case Instruction.ImageQueryFormat(t, d, i)  => channelQuery(execution, t, d, i, "data type")(_._2)
      case Instruction.ImageQuerySamples(t, d, i) =>
        sampling { (_, memory, values) =>
          for {
            image <- address(values, id(i))
            data  <- imageLevel(memory, image, 0)
          } yield values.update(id(d), VMValue.Bits(id(t), Array(data.samples.toLong)))
        }
      case Instruction.ImageFetch(t, d, i, c, mods) =>
        sampling { (_, memory, values) =>
          for {
            image     <- address(values, id(i))
            imageType <- program.runtimeType(image.pointee) match {
              case Some(RuntimeType.Texture(dimensions, buffer, arrayed)) =>
                Right((dimensions, buffer, false, arrayed))
              case Some(RuntimeType.MultisampledTexture(arrayed)) => Right((2, false, true, arrayed))
              case _ => Left(Fault.InvalidRequest("texel fetch requires a sampled image"))
            }
            operands      = mods.fold(Array(0))(_.words.toVector.map(_.raw).toArray)
            mask          = operands.headOption.getOrElse(-1)
            selectionMask = if (imageType._3) ImageOperands.Sample else ImageOperands.Lod
            hasSelection  = (mask & selectionMask) != 0
            hasOffset     = (mask & ImageOperands.ConstOffset) != 0
            _ <- Either.cond(
              (mask &
                ~(selectionMask | (ImageOperands.SignExtend | ImageOperands.ZeroExtend) |
                  (if (imageType._2 || imageType._3) 0
                   else
                     ImageOperands.ConstOffset))) == 0 &&
                (mask & (ImageOperands.SignExtend | ImageOperands.ZeroExtend)) !=
                (ImageOperands.SignExtend | ImageOperands.ZeroExtend) &&
                (!imageType._3 || hasSelection) &&
                (!imageType._2 || (mask & ~(ImageOperands.SignExtend | ImageOperands.ZeroExtend)) == 0),
              (),
              Fault.Unsupported("texel fetch requires matching Lod or Sample with optional ConstOffset")
            )
            _ <- Either.cond(
              operands.length == 1 + (if (hasSelection) 1 else 0) + (if (hasOffset) 1 else 0),
              (),
              Fault.InvalidRequest("invalid texel fetch operand count")
            )
            operand <- if (!hasSelection) Right(0)
            else values.get(operands(1)) match {
              case Some(VMValue.Bits(_, lanes)) if lanes.length == 1 => Right(lanes(0).toInt)
              case _ => Left(Fault.InvalidRequest("texel fetch LOD or sample is not scalar"))
            }
            offset <- if (!hasOffset) Right(imageZeroOffset)
            else imageConstOffset(values, operands(if (hasSelection) 2 else 1), imageType._1)
            data <- imageLevel(memory, image, if (imageType._3) 0 else operand, texelBuffer = imageType._2)
            _    <- Either.cond(
              imageType._3 || data.samples == 1,
              (),
              Fault.InvalidRequest("multisample image needs a multisampled texture type")
            )
            coords <- bits(values, id(c))
            _      <- Either.cond(
              coords.length == imageType._1 + (if (imageType._4) 1 else 0),
              (),
              Fault.InvalidRequest("texel fetch coordinate dimensionality does not match")
            )
            x      = coords(0).toInt.toLong + offset(0)
            y      = if (imageType._1 == 1) 0L else coords(1).toInt.toLong + offset(1)
            z      = if (imageType._1 == 3) coords(2).toInt.toLong + offset(2) else 0L
            sample = if (imageType._3) operand else 0
            layer  = if (imageType._4) coords(imageType._1).toInt.toLong else 0L
            texel <-
              if (
                x < 0 || y < 0 || x >= data.width || y >= data.height || z < 0 || z >= data.depth || sample < 0 ||
                sample >= data.samples || layer < 0 || layer >= data.layers
              )
                Right(Array.fill(4)(0.0))
              else sampleTexel(
                memory,
                data.pixels,
                data.format,
                data.stride * data.samples,
                x * data.samples + sample,
                (layer * data.depth + z) * data.height + y
              )
            swizzled <- swizzleImage(memory, image, texel)
          } yield values.update(
            id(d),
            VMValue.Bits(
              id(t),
              swizzled.map(v =>
                if (imageIsInteger(data.format)) v.toLong & 0xffffffffL
                else IEEE754.floatToBinary32(v.toFloat).raw & 0xffffffffL
              )
            )
          )
        }
      case Instruction.ImageRead(resultType, dest, image, coordinate, mods) =>
        imaging { (_, memory, values) =>
          if (
            address(values, id(image)).toOption.exists(pointer =>
              program.runtimeType(pointer.pointee).exists {
                case RuntimeType.InputAttachment(_, _) => true
                case _                                 => false
              }
            )
          )
            readInputAttachment(memory, values, image, coordinate, mods)
              .map(lanes => values.update(id(dest), VMValue.Bits(id(resultType), lanes)))
          else
            for {
              _     <- hinted(mods, "unsupported storage image read operands")
              texel <- imageTexel(memory, values, image, coordinate)
              lanes <- {
                val (pointer, format) = texel
                val valid             = pointer.bounds.exists { case (_, end) => pointer.address < end }
                val components        = wordComponents(format)
                def binary32(texel: AddressSpace.ReadResult[Array[Double]]) =
                  texel.map(_.map(value => IEEE754.floatToBinary32(value.toFloat).raw & 0xffffffffL))
                if (ImageFormats.integer(format).isDefined)
                  readIntegerTexel(memory, pointer, ImageFormats.integer(format).get, valid)
                else if (ImageFormats.narrow(format).isDefined)
                  binary32(readNarrowTexel(memory, pointer, ImageFormats.narrow(format).get, valid))
                else if (ImageFormats.normalised(format).isDefined)
                  binary32(readNormalisedTexel(memory, pointer, ImageFormats.normalised(format).get, valid))
                else if (components != 0) {
                  val initial = Array(0L, 0L, 0L, if (valid) 0x3f800000L else 0L)
                  (0 until components).foldLeft[AddressSpace.ReadResult[Array[Long]]](Right(initial)) { (result, lane) =>
                    for {
                      data <- result
                      word <- memory.readScalar(pointer.copy(address = pointer.address + lane * 4), 4)
                    } yield { data(lane) = word; data }
                  }
                } else Left(Fault.Unsupported(s"storage image format $format has no texel decoding"))
              }
            } yield {
              // read_imageh narrows the float result to binary16; depth images read a scalar.
              val half   = program.floatingShape(id(resultType)).exists((width, _) => width == 16)
              val result = if (program.floatingShape(id(resultType)).exists(_._2 == 1)) lanes.take(1) else lanes
              values.update(
                id(dest),
                VMValue.Bits(
                  id(resultType),
                  if (!half) result
                  else result.map(lane => IEEE754.fromDouble(IEEE754.binary32ToFloat(Binary32.low(lane)).toDouble, 16))
                )
              )
            }
        }
      case Instruction.ImageWrite(image, coordinate, texel, mods) =>
        imaging { (_, memory, values) =>
          for {
            _       <- hinted(mods, "unsupported storage image write operands")
            target  <- imageTexel(memory, values, image, coordinate)
            written <- bits(values, id(texel))
            lanes = if (written.length == 1) Array(written(0), 0L, 0L, 0x3f800000L) else written
            _ <- Either.cond(lanes.length == 4, (), Fault.InvalidRequest("image texel requires four components"))
            _ <- if (ImageFormats.integer(target._2).isDefined) {
              val layout = ImageFormats.integer(target._2).get
              if (layout.packed) memory.writeScalar(target._1, layout.encodePacked(lanes), 4)
              else (0 until layout.components).foldLeft[AddressSpace.ReadResult[Unit]](Right(())) { (result, lane) =>
                result.flatMap(_ =>
                  memory.writeScalar(
                    target._1.copy(address = target._1.address + lane * layout.componentBytes),
                    layout.encode(lanes(lane)),
                    layout.componentBytes
                  )
                )
              }
            } else if (ImageFormats.narrow(target._2).isDefined) {
              val layout = ImageFormats.narrow(target._2).get
              val floats = lanes.map(v => IEEE754.binary32ToFloat(Binary32.low(v)).toDouble)
              if (layout.encoding != NarrowEncoding.Half)
                memory.writeScalar(
                  target._1,
                  layout.encodePacked(floats),
                  layout.texelBytes
                )
              else (0 until layout.components).foldLeft[AddressSpace.ReadResult[Unit]](Right(())) { (result, lane) =>
                result.flatMap(_ =>
                  memory.writeScalar(
                    target._1.copy(address = target._1.address + layout.offset(lane), pointee = target._1.pointee),
                    layout.encodeLane(floats(lane), lane),
                    2
                  )
                )
              }
            } else if (ImageFormats.normalised(target._2).isDefined) {
              val layout = ImageFormats.normalised(target._2).get
              (0 until layout.components).foldLeft[AddressSpace.ReadResult[Unit]](Right(())) { (result, lane) =>
                result.flatMap(_ =>
                  memory.writeScalar(
                    target._1.copy(address = target._1.address + layout.offset(lane)),
                    layout.encode(IEEE754.binary32ToFloat(Binary32.low(lanes(lane))).toDouble, lane),
                    layout.componentBytes.widen[Int in 1 ~ 8]
                  )
                )
              }
            } else if (wordComponents(target._2) != 0)
              (0 until wordComponents(target._2)).foldLeft[AddressSpace.ReadResult[Unit]](Right(())) {
                (result, lane) =>
                  result.flatMap(_ =>
                    memory.writeScalar(
                      target._1.copy(address = target._1.address + lane * 4),
                      lanes(lane),
                      4
                    )
                  )
              }
            else Left(Fault.Unsupported(s"storage image format ${target._2} has no texel encoding"))
          } yield ()
        }
      case Instruction.ImageTexelPointer(resultType, dest, image, coordinate, sample) =>
        imaging { (_, memory, values) =>
          for {
            sampleIndex <- scalar(values, id(sample))
            _           <- Either.cond(sampleIndex == 0, (), Fault.InvalidRequest("storage image has only sample zero"))
            target      <- imageTexel(memory, values, image, coordinate)
            _           <- Either.cond(
              target._2 == 33 || target._2 == 24,
              (),
              Fault.Unsupported("image atomics require R32ui or R32i")
            )
          } yield values.update(id(dest), VMValue.Address(id(resultType), target._1))
        }
      case Instruction.ImageQuerySize(resultType, dest, image) =>
        imaging { (_, memory, values) =>
          for {
            pointer   <- address(values, id(image))
            imageType <- program.runtimeType(pointer.pointee) match {
              case Some(RuntimeType.StorageImage(_, format, dimensions, buffer, arrayed, cube)) =>
                Right((format, dimensions, buffer, arrayed, cube))
              case Some(RuntimeType.Texture(1, true, false))      => Right((-1, 1, true, false, false))
              case Some(RuntimeType.MultisampledTexture(arrayed)) => Right((-1, 2, false, arrayed, false))
              case _ => Left(Fault.InvalidRequest("image size query requires a storage image or texel buffer"))
            }
            shape <-
              imageLevel(memory, pointer, 0, expectedFormat(program, imageType._1), imageType._3)
          } yield values.update(
            id(dest),
            VMValue.Bits(
              id(resultType),
              (if (imageType._2 == 1) Array(shape.width)
               else if (imageType._2 == 3) Array(shape.width, shape.height, shape.depth)
               else Array(shape.width, shape.height)) ++
                (if (imageType._4)
                   Array(if (imageType._5) shape.layers / 6 else shape.layers)
                 else Array.empty[Long])
            )
          )
        }
      case _ => Left(Fault.Unsupported(s"${instruction.opcode} is not an image operation"))
    }
  }

  /** The dimensions, array flag and cube flag of a sampled or storage image view; a cube has `cubeDimensions`. */
  private def view(
      program: VMProgram,
      image: Pointer,
      cubeDimensions: Int,
      failure: NonEmpty[String]
  ): Either[Fault, (Int, Boolean, Boolean)] = program.runtimeType(image.pointee) match {
    case Some(RuntimeType.Texture(dimensions, false, arrayed)) => Right((dimensions, arrayed, false))
    case Some(RuntimeType.CubeTexture(arrayed))                => Right((cubeDimensions, arrayed, true))
    case Some(RuntimeType.StorageImage(_, ImageFormat.Unknown, dimensions, false, arrayed, false)) =>
      Right((dimensions, arrayed, false))
    case _ => Left(Fault.InvalidRequest(failure))
  }

  private def hinted(mods: Option[Operand], failure: NonEmpty[String]): Either[Fault, Unit] =
    Either.cond(mods.forall(m => m.words.length == 1 && hintsOnly(unsigned(m))), (), Fault.Unsupported(failure))

  private inline def channelQuery(
      execution: InvocationState,
      resultType: Operand,
      dest: Operand,
      image: Operand,
      label: String
  )(inline select: ((Int, Int)) => Int): AddressSpace.ReadResult[Unit] = {
    val program = execution.values.program
    val values  = execution.values
    (for {
      pointer <- address(values, id(image))
      format  <- program.runtimeType(pointer.pointee) match {
        case Some(RuntimeType.StorageImage(format = declared, texelBuffer = buffer)) =>
          imageLevel(execution.memory, pointer, 0, expectedFormat(program, declared), buffer).map(_.format)
        case _ => Left(Fault.InvalidRequest(s"image channel $label query requires a storage image"))
      }
      channels <-
        ImageChannels(format).toRight(Fault.Unsupported(s"image format $format has no OpenCL channel $label"))
    } yield values.update(id(dest), VMValue.Bits(id(resultType), Array(select(channels).toLong)))).map(_ =>
      execution.pc += 1
    )
  }

  private def blackTexel(): Array[Double] = Array.tabulate(4)(lane => if (lane == 3) 1.0 else 0.0)

  private val Hints = ImageOperands.SignExtend | ImageOperands.ZeroExtend | ImageOperands.Nontemporal

  private def hintsOnly(mask: Long): Boolean = (mask & ~Hints.toLong) == 0L &&
    (mask & (ImageOperands.SignExtend | ImageOperands.ZeroExtend)) !=
    (ImageOperands.SignExtend | ImageOperands.ZeroExtend)

  /** Images without a declared format use the bound view's format; Vulkan validates the view against the type. */
  private def expectedFormat(program: VMProgram, declared: Int): Int =
    if (program.vulkan || declared == ImageFormat.Unknown) -1 else declared

  /** A combined image-sampler descriptor: the image relocation, the view size, then the sampler record. */
  def sampledImage(memory: MemoryState, source: Pointer, imageType: Int): AddressSpace.ReadResult[Pointer] =
    for {
      size  <- memory.descriptor(source, 8, 8)
      image <- memory.descriptorPointer(source, imageType, 0).flatMap(
        _.toRight(Fault.InvalidRequest("sampled-image descriptor is missing its image relocation"))
      )
      _ <- Either.cond(
        size >= 16 && size <= Int.MaxValue && image.address + size >= image.address,
        (),
        Fault.InvalidRequest("invalid sampled-image descriptor bounds")
      )
    } yield image.copy(
      bounds = Some(image.address -> (image.address + size)),
      sampler = Some(source.copy(address = source.address + 16))
    )

  /** 32-bit words per texel of formats stored as raw words, including the depth plane of a combined format. */
  private def wordComponents(format: Int): Int =
    if (format == ImageDescriptor.Format.D32SfloatS8Depth || format == ImageDescriptor.Format.D32Sfloat) 1
    else ImageFormats.floatComponents(format)

  /** The components a sampled texel of `format` holds. */
  def texelComponents(format: Int): Int =
    if (
      format == ImageDescriptor.Format.D16Unorm || format == ImageDescriptor.Format.S8Uint ||
      format == ImageDescriptor.Format.D32SfloatS8Stencil
    ) 1
    else ImageFormats.integer(
      format
    ).fold(ImageFormats.normalised(format).fold(
      ImageFormats.narrow(format).fold(wordComponents(format))(_.components)
    )(_.components))(_.components) match {
      case 0     => 4
      case count => count
    }

  /** The words of the image operands: the mask, then the ids the mask names. */
  def operandWords(mods: Option[Operand]): Array[Int] = mods.fold(Array(0))(_.words.toVector.map(_.raw).toArray)

  def imageIsInteger(format: Int): Boolean =
    format == ImageDescriptor.Format.S8Uint || format == ImageDescriptor.Format.D32SfloatS8Stencil ||
      ImageFormats.integer(
        format
      ).isDefined

  private def readIntegerTexel(
      memory: MemoryState,
      pointer: Pointer,
      layout: IntegerImageFormat.Layout,
      valid: Boolean = true
  ): AddressSpace.ReadResult[Array[Long]] = (0 until layout.components).foldLeft[AddressSpace.ReadResult[Array[Long]]](
    Right(Array(
      0L,
      0L,
      0L,
      if (valid) 1L else 0L
    ))
  ) {
    (result, lane) =>
      for {
        data <- result
        word <- memory.readScalar(
          pointer.copy(address = pointer.address + layout.offset(lane), pointee = pointer.pointee),
          layout.componentBytes
        )
      } yield { data(lane) = layout.decode(word, lane); data }
  }

  private def readNormalisedTexel(
      memory: MemoryState,
      pointer: Pointer,
      layout: NormalisedImageFormat.Layout,
      valid: Boolean = true
  ): AddressSpace.ReadResult[
    Array[Double]
  ] = (0 until layout.components).foldLeft[AddressSpace.ReadResult[Array[Double]]](Right(Array(
    0.0,
    0.0,
    0.0,
    if (valid) 1.0 else 0.0
  ))) {
    (result, lane) =>
      for {
        data <- result
        word <-
          memory.readScalar(
            pointer.copy(address = pointer.address + layout.offset(lane)),
            layout.componentBytes.widen[Int in 1 ~ 8]
          )
      } yield { data(lane) = layout.decode(word, lane); data }
  }

  private def readNarrowTexel(
      memory: MemoryState,
      pointer: Pointer,
      layout: NarrowImageFormat.Layout,
      valid: Boolean = true
  ): AddressSpace.ReadResult[
    Array[Double]
  ] = (0 until layout.components).foldLeft[AddressSpace.ReadResult[Array[Double]]](Right(Array(
    0.0,
    0.0,
    0.0,
    if (valid) 1.0 else 0.0
  ))) {
    (result, lane) =>
      for {
        data <- result
        word <- memory.readScalar(
          pointer.copy(address = pointer.address + layout.offset(lane), pointee = pointer.pointee),
          layout.elementBytes
        )
      } yield { data(lane) = layout.decode(word, lane); data }
  }

  def imageLevels(memory: MemoryState, image: Pointer): AddressSpace.ReadResult[Int] =
    for {
      format <-
        memory.descriptor(image, ImageDescriptor.Layout.Format, 4)
      _ <- Either.cond(
        (format & ImageDescriptor.Flags.LevelTable) != 0,
        (),
        Fault.InvalidRequest("image view has no level table")
      )
      levels <- memory.descriptor(image, ImageDescriptor.Layout.Levels, 4)
      _      <- Either.cond(levels >= 1 && levels <= 15, (), Fault.InvalidRequest("invalid image level count"))
    } yield levels.toInt

  // Swizzles apply after filtering and missing-component/border replacement.
  // Bit 29 adds four VkComponentSwizzle words following the level table.
  def swizzleImage(
      memory: MemoryState,
      image: Pointer,
      values: Array[Double]
  ): AddressSpace.ReadResult[Array[Double]] =
    memory.descriptor(image, ImageDescriptor.Layout.Format, 4).flatMap { format =>
      if ((format & ImageDescriptor.Flags.Swizzle) == 0) Right(values)
      else for {
        levels <- imageLevels(memory, image)
        _      <- descriptorSpan(
          image,
          ImageDescriptor.Layout.LevelTable + ImageDescriptor.Layout.SwizzleBytes +
            levels * ImageDescriptor.Layout.LevelBytes,
          "image swizzle exceeds descriptor"
        )
        result <- (0 until 4).foldLeft[AddressSpace.ReadResult[Array[Double]]](Right(Array.ofDim[Double](4))) {
          (result, lane) =>
            for {
              out       <- result
              component <- memory.descriptor(
                image,
                ImageDescriptor.Layout.LevelTable + levels * ImageDescriptor.Layout.LevelBytes + lane * 4,
                4
              )
              _ <- Either.cond(
                component <= ImageDescriptor.Swizzle.A,
                (),
                Fault.InvalidRequest("invalid image component swizzle")
              )
            } yield {
              out(lane) = if (component == ImageDescriptor.Swizzle.Identity) values(lane)
              else if (component == ImageDescriptor.Swizzle.Zero) 0.0
              else if (component == ImageDescriptor.Swizzle.One) 1.0
              else values(component.toInt - ImageDescriptor.Swizzle.R);
              out
            }
        }
      } yield result
    }

  private def descriptorSpan(image: Pointer, bytes: Long, exceeded: NonEmpty[String]): Either[Fault, Unit] =
    image.bounds match {
      case None               => Left(Fault.InvalidRequest("image descriptor binding requires an explicit range"))
      case Some((begin, end)) =>
        Either.cond(image.address >= begin && bytes <= end - image.address, (), Fault.InvalidRequest(exceeded))
    }

  // Views carry a level table and a relocated pointer to shared texels.
  // Format bit 30 marks a volume: header word 2 is base-view depth, shifted by
  // the view-relative mip. Without that bit, word 2 is the constant layer count.
  def imageLevel(
      memory: MemoryState,
      image: Pointer,
      level: Int,
      expectedFormat: Int = -1,
      texelBuffer: Boolean = false
  ): AddressSpace.ReadResult[ImageLevel] =
    for {
      rawFormat <-
        memory.descriptor(image, ImageDescriptor.Layout.Format, 4)
      format = (rawFormat & ImageDescriptor.Flags.FormatMask).toInt
      volume = (rawFormat & ImageDescriptor.Flags.Volume) != 0
      levels <- imageLevels(memory, image)
      _      <- Either.cond(
        level >= 0 && level < levels && ImageTexelCodec.texelBytes(
          format
        ) != 0 && (expectedFormat < 0 || format == expectedFormat),
        (),
        Fault.InvalidRequest(s"invalid image level $level of $levels or format $format (expected $expectedFormat)")
      )
      table = ImageDescriptor.Layout.LevelTable + level * ImageDescriptor.Layout.LevelBytes
      _ <- descriptorSpan(
        image,
        ImageDescriptor.Layout.LevelTable + levels * ImageDescriptor.Layout.LevelBytes,
        "image level table exceeds descriptor"
      )
      size <- memory.descriptor(image, ImageDescriptor.Layout.TexelBytes, 8)
      base <- memory.descriptorPointer(
        image.copy(address = image.address + ImageDescriptor.Layout.Texels),
        image.pointee,
        image.storageClass
      ).flatMap(_.toRight(Fault.InvalidRequest("image view is missing its texel relocation")))
      width      <- memory.descriptor(image, table, 4)
      height     <- memory.descriptor(image, table + ImageDescriptor.Layout.LevelHeight, 4)
      offset     <- memory.descriptor(image, table + ImageDescriptor.Layout.LevelOffset, 8)
      slices     <- memory.descriptor(image, ImageDescriptor.Layout.Slices, 4)
      rawSamples <- memory.descriptor(image, ImageDescriptor.Layout.Samples, 4)
      pitched = (rawFormat & ImageDescriptor.Flags.Pitched) != 0
      pitchAt = ImageDescriptor.Layout.LevelTable + levels * ImageDescriptor.Layout.LevelBytes +
        (if ((rawFormat & ImageDescriptor.Flags.Swizzle) != 0) ImageDescriptor.Layout.SwizzleBytes else 0)
      _ <-
        if (pitched)
          descriptorSpan(image, pitchAt + ImageDescriptor.Layout.PitchBytes, "image row pitch exceeds descriptor")
        else Right(())
      pitch <- if (pitched) memory.descriptor(image, pitchAt, 8) else Right(0L)
      texelBytes = ImageTexelCodec.texelBytes(format)
      layers     = if (volume) 1L else slices
      depth      = if (volume) Math.max(1L, slices >> level) else 1L
      samples    = if (rawSamples == 0L) 1 else rawSamples.toInt
      stride     = if (pitched) pitch / texelBytes else width
      _ <- Either.cond(
        !pitched || (levels == 1 && samples == 1 && pitch % texelBytes == 0 && stride >= width),
        (),
        Fault.InvalidRequest("image row pitch must hold a row of whole texels")
      )
      bytes           = stride * height * texelBytes * samples * layers * depth
      validDimensions = width > 0 && height > 0 && (
        if (texelBuffer) !volume && layers == 1 && width <= 0xffffffffL && height == 1
        else if (volume) samples == 1 && slices >= 1 && slices <= 2048 && width <= 2048 && height <= 2048
        else width <= 16384 && height <= 16384
      )
      _ <- Either.cond(
        validDimensions &&
          (samples == 1 || samples == 4) && layers >= 1 && layers <= 2048 &&
          size > 0 && size <= Int.MaxValue && offset >= 0 && offset <= size && bytes <= size - offset &&
          base.address + size >= base.address,
        (),
        Fault.InvalidRequest("image level exceeds texel allocation")
      )
    } yield ImageLevel(
      base.copy(
        address = base.address + offset,
        bounds = Some((base.address + offset) -> (base.address + offset + bytes))
      ),
      width,
      height,
      format,
      samples,
      layers,
      depth,
      stride
    )
  private def imageTexel(
      memory: MemoryState,
      values: InvocationValues,
      image: Operand,
      coordinate: Operand
  ): AddressSpace.ReadResult[(Pointer, Int)] =
    for {
      source    <- address(values, id(image))
      imageType <- values.program.runtimeType(source.pointee) match {
        case Some(RuntimeType.StorageImage(sampledType, format, dimensions, texelBuffer, arrayed, cube)) =>
          Right((sampledType, format, dimensions, texelBuffer, arrayed || cube))
        case _ => Left(Fault.InvalidRequest("image operand does not identify a storage image"))
      }
      data   <- imageLevel(memory, source, 0, expectedFormat(values.program, imageType._2), imageType._4)
      coords <- bits(values, id(coordinate))
      required = imageType._3 + (if (imageType._5) 1 else 0)
      // OpenCL passes four-component coordinates to 3D and 2D array images.
      _ <- Either.cond(
        coords.length == required || (!values.program.vulkan && coords.length == 4 && required >= 3),
        (),
        Fault.InvalidRequest("image coordinate component count does not match dimensionality")
      )
    } yield {
      val width      = data.width
      val height     = data.height
      val x          = coords(0).toInt.toLong
      val y          = if (imageType._3 == 1) 0L else coords(1).toInt.toLong
      val z          = if (imageType._3 == 3) coords(2).toInt.toLong else 0L
      val layer      = if (imageType._5) coords(imageType._3).toInt.toLong else 0L
      val begin      = data.pixels.address
      val texelBytes = ImageTexelCodec.texelBytes(data.format)
      val end        = begin + data.stride * height * data.layers * data.depth * texelBytes
      val offset     =
        if (
          x < 0 || y < 0 || x >= width || y >= height || z < 0 || z >= data.depth || layer < 0 || layer >= data.layers
        ) end
        else begin + (((layer * data.depth + z) * height + y) * data.stride + x) * texelBytes
      (
        data.pixels.copy(
          address = offset,
          pointee = imageType._1,
          storageClass = StorageClass.Image,
          bounds = Some(begin -> end)
        ),
        // OpenCL depth images store UNORM_INT16 depths as R16 texels
        if (!values.program.vulkan && data.format == ImageDescriptor.Format.D16Unorm) ImageFormat.R16 else data.format
      )
    }

  // Sampler ABI: min/mag filters, U/V address modes, unnormalized, compare enable/op,
  // border colour, float bias/min/max LOD, mipmap mode, then W address mode.
  def sampleTexel(
      memory: MemoryState,
      image: Pointer,
      format: Int,
      width: Long,
      x: Long,
      y: Long
  ): AddressSpace.ReadResult[Array[Double]] = {
    val pointer =
      image.copy(address =
        image.address + (y * width + x) * ImageTexelCodec.texelBytes(
          format
        ) + (if (format == ImageDescriptor.Format.D32SfloatS8Stencil) 4 else 0)
      )
    val components = wordComponents(format)
    if (ImageFormats.integer(format).isDefined) {
      val layout = ImageFormats.integer(format).get
      readIntegerTexel(memory, pointer, layout).map(_.map(v => if (layout.signed) v.toInt.toDouble else v.toDouble))
    } else if (ImageFormats.narrow(format).isDefined) {
      readNarrowTexel(memory, pointer, ImageFormats.narrow(format).get)
    } else if (ImageFormats.normalised(format).isDefined) {
      readNormalisedTexel(memory, pointer, ImageFormats.normalised(format).get)
    } else if (components != 0) {
      (0 until components).foldLeft[AddressSpace.ReadResult[Array[Double]]](Right(blackTexel())) {
        (result, lane) =>
          for {
            data <- result
            word <- memory.readScalar(pointer.copy(address = pointer.address + lane * 4), 4)
          } yield {
            data(lane) = IEEE754.binary32ToFloat(Binary32.low(word)).toDouble
            data
          }
      }
    } else format match {
      case ImageDescriptor.Format.D16Unorm =>
        memory.readScalar(pointer, 2).map(word => Array(word.toDouble / 65535.0, 0.0, 0.0, 1.0))
      case ImageDescriptor.Format.S8Uint | ImageDescriptor.Format.D32SfloatS8Stencil =>
        memory.readScalar(pointer, 1).map(word => Array(word.toDouble, 0.0, 0.0, 1.0))
      case _ => Left(Fault.Unsupported(s"image format $format has no texel decoding"))
    }
  }
  def sampleAddress(value: Long, size: Long, mode: Int): Option[Long] = mode match {
    case ImageDescriptor.AddressMode.Repeat         => Some(Math.floorMod(value, size))
    case ImageDescriptor.AddressMode.MirroredRepeat =>
      val wrapped = Math.floorMod(value, 2 * size); Some(if (wrapped >= size) 2 * size - 1 - wrapped else wrapped)
    case ImageDescriptor.AddressMode.ClampToEdge       => Some(Math.max(0L, Math.min(size - 1, value)))
    case ImageDescriptor.AddressMode.ClampToBorder     => Option.when(value >= 0 && value < size)(value)
    case ImageDescriptor.AddressMode.MirrorClampToEdge =>
      Some(Math.max(0L, Math.min(size - 1, if (value < 0) -value - 1 else value)))
    case _ => None
  }
  def sampleCompare(op: Int, reference: Double, depth: Double): Double = {
    val pass = op match {
      case ImageDescriptor.CompareOp.Never          => false
      case ImageDescriptor.CompareOp.Less           => reference < depth
      case ImageDescriptor.CompareOp.Equal          => reference == depth
      case ImageDescriptor.CompareOp.LessOrEqual    => reference <= depth
      case ImageDescriptor.CompareOp.Greater        => reference > depth
      case ImageDescriptor.CompareOp.NotEqual       => reference != depth
      case ImageDescriptor.CompareOp.GreaterOrEqual => reference >= depth
      case ImageDescriptor.CompareOp.Always         => true
      case _                                        => false
    }
    if (pass) 1.0 else 0.0
  }
  private val imageZeroOffset = Array(0L, 0L, 0L)

  // ConstOffset is measured in texels at the selected level, and never moves
  // the array layer. These bounds match VkPhysicalDeviceLimits.
  private def imageConstOffset(
      values: InvocationValues,
      operand: Int,
      dimensions: Int
  ): AddressSpace.ReadResult[Array[Long]] =
    values.get(operand) match {
      case Some(VMValue.Bits(_, lanes)) if lanes.length == dimensions =>
        val offset = lanes.map(_.toInt.toLong)
        Either.cond(
          offset.forall(v => v >= -8 && v <= 7),
          offset,
          Fault.InvalidRequest("image ConstOffset exceeds device limits")
        )
      case _ => Left(Fault.InvalidRequest("image ConstOffset dimensionality does not match"))
    }

  private def readInputAttachment(
      memory: MemoryState,
      values: InvocationValues,
      image: Operand,
      coordinate: Operand,
      mods: Option[Operand]
  ): AddressSpace.ReadResult[Array[Long]] = for {
    source    <- address(values, id(image))
    imageType <- values.program.runtimeType(source.pointee) match {
      case Some(RuntimeType.InputAttachment(component, multisampled)) => Right(component -> multisampled)
      case _ => Left(Fault.InvalidRequest("subpass read requires an input attachment"))
    }
    data <- imageLevel(memory, source, 0)
    _    <-
      Either.cond(
        imageType._2 == (data.samples > 1),
        (),
        Fault.InvalidRequest("input attachment sample count mismatch")
      )
    coord <- bits(values, id(coordinate))
    _ <- Either.cond(coord.length == 2, (), Fault.InvalidRequest("input attachment coordinates require two components"))
    position <- values.program.builtins.view.collectFirst {
      case (variable, 15) if values.get(variable).exists { case VMValue.Address(_, _) => true; case _ => false } =>
        variable
    }.flatMap(v => values.get(v)) match {
      case Some(VMValue.Address(_, pointer)) => Right(pointer)
      case _ => Left(Fault.InvalidRequest("input attachment requires a fragment position record"))
    }
    px     <- memory.readScalar(position, 4)
    py     <- memory.readScalar(position.copy(address = position.address + 4), 4)
    sample <- mods match {
      case Some(m)
          if imageType._2 && m.words.length == 2 &&
            (m.words.head.raw & ~(ImageOperands.SignExtend | ImageOperands.ZeroExtend)) == ImageOperands.Sample &&
            (m.words.head.raw & (ImageOperands.SignExtend | ImageOperands.ZeroExtend)) !=
            (ImageOperands.SignExtend | ImageOperands.ZeroExtend) =>
        values.get(m.words.apply(1).raw) match {
          case Some(VMValue.Bits(_, lanes)) if lanes.length == 1 => Right(lanes(0).toInt)
          case _ => Left(Fault.InvalidRequest("input attachment sample index is not scalar"))
        }
      case None if !imageType._2 => Right(0)
      case Some(m)
          if !imageType._2 && m.words.length == 1 &&
            (m.words.head.raw == 0 || m.words.head.raw == ImageOperands.SignExtend ||
              m.words.head.raw == ImageOperands.ZeroExtend) =>
        Right(0)
      case _ => Left(Fault.Unsupported("input attachment operands require a sample index only for multisampled images"))
    }
    x = Math.floor(IEEE754.binary32ToFloat(Binary32.low(px)).toDouble).toLong + coord(0).toInt
    y = Math.floor(IEEE754.binary32ToFloat(Binary32.low(py)).toDouble).toLong + coord(1).toInt
    texel <- if (x < 0 || y < 0 || x >= data.width || y >= data.height || sample < 0 || sample >= data.samples)
      Right(Array.fill(4)(0.0))
    else sampleTexel(memory, data.pixels, data.format, data.stride * data.samples, x * data.samples + sample, y)
  } yield
    if (imageIsInteger(data.format)) texel.map(_.toLong & 0xffffffffL)
    else texel.map(x => IEEE754.floatToBinary32(x.toFloat).raw & 0xffffffffL)
}
