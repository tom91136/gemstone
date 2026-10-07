package net.kurobako.gemstone.isa.spirv

import Op.*
import VMInvocation.*
import VMLowering.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.vm.*

/** Image views, samplers, texel access and sampling. Descriptor words are read once per memory state. */
private object ImageOps {
  import Operand.*

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    val program = site.program
    {
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
      case Instruction.ImageSampleImplicitLod(t, d, i, c, m) =>
        sampling { (execution, memory, _) =>
          sample(program, execution, memory, t, d, i, c, None, false, m)
        }
      case Instruction.ImageSampleExplicitLod(t, d, i, c, m) =>
        sampling { (execution, memory, _) =>
          sample(program, execution, memory, t, d, i, c, None, true, Some(m))
        }
      case Instruction.ImageSampleDrefImplicitLod(t, d, i, c, r, m) =>
        sampling { (execution, memory, _) =>
          sample(program, execution, memory, t, d, i, c, Some(r), false, m)
        }
      case Instruction.ImageSampleDrefExplicitLod(t, d, i, c, r, m) =>
        sampling { (execution, memory, _) =>
          sample(program, execution, memory, t, d, i, c, Some(r), true, Some(m))
        }
      case Instruction.ImageSampleProjImplicitLod(t, d, i, c, m) =>
        sampling { (execution, memory, _) =>
          sample(program, execution, memory, t, d, i, c, None, false, m, projective = true)
        }
      case Instruction.ImageSampleProjExplicitLod(t, d, i, c, m) =>
        sampling { (execution, memory, _) =>
          sample(program, execution, memory, t, d, i, c, None, true, Some(m), projective = true)
        }
      case Instruction.ImageSampleProjDrefImplicitLod(t, d, i, c, r, m) =>
        sampling { (execution, memory, _) =>
          sample(program, execution, memory, t, d, i, c, Some(r), false, m, projective = true)
        }
      case Instruction.ImageSampleProjDrefExplicitLod(t, d, i, c, r, m) =>
        sampling { (execution, memory, _) =>
          sample(program, execution, memory, t, d, i, c, Some(r), true, Some(m), projective = true)
        }
      case Instruction.ImageGather(t, d, i, c, component, m) =>
        sampling { (execution, memory, _) =>
          sample(program, execution, memory, t, d, i, c, None, false, m, gatherComponent = Some(component))
        }
      case Instruction.ImageDrefGather(t, d, i, c, r, m) =>
        sampling { (execution, memory, _) =>
          sample(program, execution, memory, t, d, i, c, Some(r), false, m, gatherDepth = true)
        }
      case Instruction.ImageQueryLod(t, d, i, c) =>
        sampling { (execution, memory, values) =>
          for {
            coords     <- bits(values, id(c))
            image      <- address(values, id(i))
            dimensions <- program.runtimeType(image.pointee) match {
              case Some(RuntimeType.Texture(_, n, false, _)) => Right((n, false))
              case Some(RuntimeType.CubeTexture(_, _))       => Right((3, true))
              case _ => Left(Fault.InvalidRequest("LOD query requires a sampled 1D, 2D, 3D or cube image"))
            }
            _ <- Either.cond(
              coords.length >= dimensions._1,
              (),
              Fault.InvalidRequest("LOD query coordinate shape does not match image")
            )
            _ <- if (execution.fragmentQuad && execution.gradients.isEmpty) {
              execution.quadRequest =
                Some(QuadRequest(
                  execution.currentFunction.id,
                  execution.pc,
                  id(t),
                  id(d),
                  coords,
                  Derivative.Gradients
                ))
              Right(())
            } else for {
              sampler     <- image.sampler.toRight(Fault.InvalidRequest("LOD query has no sampler"))
              level       <- imageLevel(memory, image, 0)
              levels      <- imageLevels(memory, image)
              biasBits    <- memory.descriptor(sampler, ImageDescriptor.SamplerWord.MipLodBias * 4, 4)
              minBits     <- memory.descriptor(sampler, ImageDescriptor.SamplerWord.MinLod * 4, 4)
              maxBits     <- memory.descriptor(sampler, ImageDescriptor.SamplerWord.MaxLod * 4, 4)
              mipmap      <- memory.descriptor(sampler, ImageDescriptor.SamplerWord.MipmapMode * 4, 4)
              resultShape <- program.floatingShape(id(t)).filter((w, n) => w.raw == 32 && n == 2)
                .toRight(Fault.InvalidRequest("LOD query result must be float32 vec2"))
            } yield {
              def float(bits: Long): Double = IEEE754.binary32ToFloat(Binary32.low(bits)).toDouble
              val direction                 = coords.take(dimensions._1).map(float(_))
              val projection                =
                if (dimensions._2) Some(CubeMap.project(direction(0), direction(1), direction(2))) else None
              val base = execution.gradients.fold(Double.NegativeInfinity) { case (dx, dy) =>
                imageGradientLod(level, direction, dx, dy, dimensions._1, projection, false)
              }
              val lod     = base + Math.max(-16.0, Math.min(16.0, float(biasBits)))
              val clamped =
                Math.max(0.0, Math.min(levels - 1.0, Math.max(float(minBits), Math.min(float(maxBits), lod))))
              val selected = if (mipmap == ImageDescriptor.MipmapMode.Nearest)
                Math.max(0.0, Math.ceil(clamped + 0.5) - 1.0)
              else clamped
              values.update(
                id(d),
                VMValue.Bits(id(t), Array(selected, lod).map(v => IEEE754.fromDouble(v, resultShape._1)))
              )
              execution.gradients = None
            }
          } yield ()
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
            dimensions <- program.runtimeType(image.pointee) match {
              case Some(RuntimeType.Texture(_, dimensions, false, arrayed)) => Right((dimensions, arrayed, false))
              case Some(RuntimeType.CubeTexture(_, arrayed))                => Right((2, arrayed, true))
              case _ => Left(Fault.InvalidRequest("image mip size query requires a sampled image"))
            }
            levels <- imageLevels(memory, image)
            bounded = if (program.vulkan) Math.max(0, Math.min(levels - 1, level.toInt)) else level.toInt
            data <- imageLevel(memory, image, bounded)
          } yield values.update(
            id(d),
            VMValue.Bits(
              id(t),
              (if (dimensions._1 == 1) Array(data.width)
               else if (dimensions._1 == 3) Array(data.width, data.height, data.depth)
               else Array(data.width, data.height)) ++ (if (dimensions._2)
                                                          Array(if (dimensions._3) data.layers / 6 else data.layers)
                                                        else Array.empty[Long])
            )
          )
        }
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
              case Some(RuntimeType.Texture(_, dimensions, buffer, arrayed)) =>
                Right((dimensions, buffer, false, arrayed))
              case Some(RuntimeType.MultisampledTexture(_, arrayed)) => Right((2, false, true, arrayed))
              case _ => Left(Fault.InvalidRequest("texel fetch requires a sampled image"))
            }
            operands      = mods.map(_.words.toVector.map(_.raw).toArray).getOrElse(Array(0))
            mask          = operands.headOption.getOrElse(-1)
            selectionMask = if (imageType._3) ImageOperands.Sample else ImageOperands.Lod
            hasSelection  = (mask & selectionMask) != 0
            hasOffset     = (mask & ImageOperands.ConstOffset) != 0
            _ <- Either.cond(
              (mask & ~(selectionMask | (ImageOperands.SignExtend | ImageOperands.ZeroExtend) | (if (
                                                                                                   imageType._2 || imageType._3
                                                                                                 ) 0
                                                                                                 else
                                                                                                   ImageOperands.ConstOffset))) == 0 &&
                (mask & (ImageOperands.SignExtend | ImageOperands.ZeroExtend)) != (ImageOperands.SignExtend | ImageOperands.ZeroExtend) &&
                (!imageType._3 || hasSelection) && (!imageType._2 || (mask & ~(ImageOperands.SignExtend | ImageOperands.ZeroExtend)) == 0),
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
                x < 0 || y < 0 || x >= data.width || y >= data.height || z < 0 || z >= data.depth || sample < 0 || sample >= data.samples || layer < 0 || layer >= data.layers
              )
                Right(Array.fill(4)(0.0))
              else sampleTexel(
                memory,
                data.pixels,
                data.format,
                data.width * data.samples,
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
                else IEEE754.floatToBinary32(v.toFloat).raw.toLong & 0xffffffffL
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
            readInputAttachment(program, memory, values, image, coordinate, mods)
              .map(lanes => values.update(id(dest), VMValue.Bits(id(resultType), lanes)))
          else
            for {
              _ <- Either.cond(
                mods.forall(m =>
                  m.words.length == 1 && (unsigned(m) == 0L || unsigned(m) == ImageOperands.SignExtend || unsigned(
                    m
                  ) == ImageOperands.ZeroExtend)
                ),
                (),
                Fault.Unsupported("unsupported storage image read operands")
              )
              texel <- imageTexel(program, memory, values, image, coordinate)
              lanes <- {
                val (pointer, format) = texel
                val valid             = pointer.bounds.exists { case (_, end) => pointer.address < end }
                val components        = wordComponents(format)
                if (ImageFormats.integer(format).isDefined)
                  readIntegerTexel(memory, pointer, ImageFormats.integer(format).get, valid)
                else if (ImageFormats.narrow(format).isDefined)
                  readNarrowTexel(memory, pointer, ImageFormats.narrow(format).get, valid).map(_.map(value =>
                    IEEE754.floatToBinary32(value.toFloat).raw.toLong & 0xffffffffL
                  ))
                else if (ImageFormats.normalised(format).isDefined)
                  readNormalisedTexel(memory, pointer, ImageFormats.normalised(format).get, valid).map(_.map(value =>
                    IEEE754.floatToBinary32(value.toFloat).raw.toLong & 0xffffffffL
                  ))
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
            } yield values.update(id(dest), VMValue.Bits(id(resultType), lanes))
        }
      case Instruction.ImageWrite(image, coordinate, texel, mods) =>
        imaging { (_, memory, values) =>
          for {
            _ <- Either.cond(
              mods.forall(m =>
                m.words.length == 1 && (unsigned(m) == 0L || unsigned(m) == ImageOperands.SignExtend || unsigned(
                  m
                ) == ImageOperands.ZeroExtend)
              ),
              (),
              Fault.Unsupported("unsupported storage image write operands")
            )
            target <- imageTexel(program, memory, values, image, coordinate)
            lanes  <- bits(values, id(texel))
            _      <- Either.cond(lanes.length == 4, (), Fault.InvalidRequest("image texel requires four components"))
            _      <- if (ImageFormats.integer(target._2).isDefined) {
              val layout = ImageFormats.integer(target._2).get
              if (layout.packed) memory.writeScalar(target._1, layout.encodePacked(lanes), 4)
              else (0 until layout.components).foldLeft[AddressSpace.ReadResult[Unit]](Right(())) { (result, lane) =>
                result.flatMap(_ =>
                  memory.writeScalar(
                    target._1.copy(address = target._1.address + lane * layout.componentBytes.raw),
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
            target      <- imageTexel(program, memory, values, image, coordinate)
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
              case Some(RuntimeType.Texture(_, 1, true, false))      => Right((-1, 1, true, false, false))
              case Some(RuntimeType.MultisampledTexture(_, arrayed)) => Right((-1, 2, false, arrayed, false))
              case _ => Left(Fault.InvalidRequest("image size query requires a storage image or texel buffer"))
            }
            shape <-
              imageLevel(memory, pointer, 0, if (program.vulkan) -1 else imageType._1, imageType._3)
          } yield values.update(
            id(dest),
            VMValue.Bits(
              id(resultType),
              (if (imageType._2 == 1) Array(shape.width)
               else if (imageType._2 == 3) Array(shape.width, shape.height, shape.depth)
               else Array(shape.width, shape.height)) ++ (if (imageType._4)
                                                            Array(if (imageType._5) shape.layers / 6 else shape.layers)
                                                          else Array.empty[Long])
            )
          )
        }
    }: PartialFunction[Instruction, Op]
  }

  /** Sampling ops leave the pc in place while the lane waits for its quad's implicit derivatives. */
  private def sampling(body: (InvocationState, MemoryState, InvocationValues) => AddressSpace.ReadResult[Unit]): Op =
    Dynamic((execution, memory) =>
      body(execution, memory, execution.values).map(_ => if (execution.quadRequest.isEmpty) execution.pc += 1)
    )

  private def imaging(body: (InvocationState, MemoryState, InvocationValues) => AddressSpace.ReadResult[Unit]): Op =
    step((execution, memory) => body(execution, memory, execution.values))

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

  private def sample(
      program: VMProgram,
      execution: InvocationState,
      memory: MemoryState,
      resultType: Operand,
      dest: Operand,
      source: Operand,
      coordinate: Operand,
      dref: Option[Operand],
      explicit: Boolean,
      mods: Option[Operand],
      projective: Boolean = false,
      gatherComponent: Option[Operand] = None,
      gatherDepth: Boolean = false
  ): AddressSpace.ReadResult[Unit] = {
    val values = execution.values
    for {
      image      <- address(values, id(source))
      dimensions <- program.runtimeType(image.pointee) match {
        case Some(RuntimeType.Texture(_, dimensions, false, arrayed)) => Right((dimensions, arrayed, false))
        case Some(RuntimeType.CubeTexture(_, arrayed))                => Right((3, arrayed, true))
        case _ => Left(Fault.InvalidRequest("sampling requires an image view, not a texel buffer"))
      }
      raw <- bits(values, id(coordinate))
      _   <- Either.cond(
        !projective || (!dimensions._2 && !dimensions._3 && raw.length > dimensions._1),
        (),
        Fault.InvalidRequest("projective sampling requires non-array 1D, 2D or 3D coordinates and q")
      )
      q = if (projective) IEEE754.binary32ToFloat(Binary32.low(raw(dimensions._1))) else 1.0f
      // Implicit derivatives are taken after projection. Explicit Grad values
      // already describe the projected coordinates and pass through unchanged.
      coords = if (projective) raw.take(dimensions._1).map(w =>
        IEEE754.floatToBinary32(IEEE754.binary32ToFloat(Binary32.low(w)) / q).raw.toLong & 0xffffffffL
      )
      else raw
      reference <- dref match {
        case Some(value) =>
          scalar(values, id(value)).map(w => Some((IEEE754.binary32ToFloat(Binary32.low(w)) / q).toDouble))
        case None => Right(None)
      }
      gather <- if (gatherDepth) Right(Some(0))
      else gatherComponent match {
        case None            => Right(None)
        case Some(component) => scalar(values, id(component)).flatMap(c =>
            Either.cond(c >= 0 && c <= 3, Some(c.toInt), Fault.InvalidRequest("gather component must be in [0,3]"))
          )
      }
      _ <- Either.cond(
        gather.isEmpty || dimensions._1 == 2 || dimensions._3,
        (),
        Fault.InvalidRequest("gather requires a 2D or cube image")
      )
      _ <- if (gather.isEmpty && !explicit && execution.fragmentQuad && execution.gradients.isEmpty) {
        execution.quadRequest = Some(QuadRequest(
          execution.currentFunction.id,
          execution.pc,
          id(resultType),
          id(dest),
          coords,
          Derivative.Gradients
        ))
        Right(())
      } else sampleImage(
        memory,
        values,
        image,
        coords,
        reference,
        explicit,
        mods,
        execution.gradients,
        dimensions._1,
        dimensions._2,
        dimensions._3,
        gather,
        program.vulkan
      ).map { result =>
        execution.gradients = None
        values.update(id(dest), VMValue.Bits(id(resultType), result))
      }
    } yield ()
  }

  /** 32-bit words per texel of formats stored as raw words, including the depth plane of a combined format. */
  private def wordComponents(format: Int): Int =
    if (format == ImageDescriptor.Format.D32SfloatS8Depth) 1 else ImageFormats.floatComponents(format)
  private def imageIsInteger(format: Int): Boolean =
    format == ImageDescriptor.Format.S8Uint || format == ImageDescriptor.Format.D32SfloatS8Stencil || ImageFormats.integer(
      format
    ).isDefined

  private def readIntegerTexel(
      memory: MemoryState,
      pointer: Pointer,
      layout: IntegerImageFormat.Layout,
      valid: Boolean = true
  ): AddressSpace.ReadResult[Array[Long]] =
    (0 until layout.components).foldLeft[AddressSpace.ReadResult[Array[Long]]](Right(Array(
      0L,
      0L,
      0L,
      if (valid) 1L else 0L
    ))) {
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
  ): AddressSpace.ReadResult[Array[Double]] =
    (0 until layout.components).foldLeft[AddressSpace.ReadResult[Array[Double]]](Right(Array(
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
  ): AddressSpace.ReadResult[Array[Double]] =
    (0 until layout.components).foldLeft[AddressSpace.ReadResult[Array[Double]]](Right(Array(
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

  private case class ImageLevel(
      pixels: Pointer,
      width: Long,
      height: Long,
      format: Int,
      samples: Int = 1,
      layers: Long = 1,
      depth: Long = 1
  )

  private def imageLevels(memory: MemoryState, image: Pointer): AddressSpace.ReadResult[Int] =
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
  private def swizzleImage(
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
          ImageDescriptor.Layout.LevelTable + ImageDescriptor.Layout.SwizzleBytes + levels * ImageDescriptor.Layout.LevelBytes,
          "image swizzle exceeds descriptor"
        )
        result <-
          (0 until 4).foldLeft[AddressSpace.ReadResult[Array[Double]]](Right(Array.ofDim[Double](4))) { (result, lane) =>
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
  private def imageLevel(
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
      layers          = if (volume) 1L else slices
      depth           = if (volume) Math.max(1L, slices >> level) else 1L
      samples         = if (rawSamples == 0L) 1 else rawSamples.toInt
      bytes           = width * height * ImageTexelCodec.texelBytes(format) * samples * layers * depth
      validDimensions = width > 0 && height > 0 && (
        if (texelBuffer) !volume && layers == 1 && width <= 0xffffffffL && height == 1
        else if (volume) samples == 1 && slices >= 1 && slices <= 2048 && width <= 2048 && height <= 2048
        else width <= 16384 && height <= 16384
      )
      _ <- Either.cond(
        validDimensions && (samples == 1 || samples == 4) && layers >= 1 && layers <= 2048 &&
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
      depth
    )
  private def imageTexel(
      program: VMProgram,
      memory: MemoryState,
      values: InvocationValues,
      image: Operand,
      coordinate: Operand
  ): AddressSpace.ReadResult[(Pointer, Int)] =
    for {
      source    <- address(values, id(image))
      imageType <- program.runtimeType(source.pointee) match {
        case Some(RuntimeType.StorageImage(sampledType, format, dimensions, texelBuffer, arrayed, cube)) =>
          Right((sampledType, format, dimensions, texelBuffer, arrayed || cube))
        case _ => Left(Fault.InvalidRequest("image operand does not identify a storage image"))
      }
      data   <- imageLevel(memory, source, 0, if (program.vulkan) -1 else imageType._2, imageType._4)
      coords <- bits(values, id(coordinate))
      _      <- Either.cond(
        coords.length == imageType._3 + (if (imageType._5) 1 else 0),
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
      val end        = begin + width * height * data.layers * data.depth * texelBytes
      val offset     =
        if (
          x < 0 || y < 0 || x >= width || y >= height || z < 0 || z >= data.depth || layer < 0 || layer >= data.layers
        ) end
        else begin + (((layer * data.depth + z) * height + y) * width + x) * texelBytes
      (
        data.pixels.copy(
          address = offset,
          pointee = imageType._1,
          storageClass = StorageClass.Image,
          bounds = Some(begin -> end)
        ),
        data.format
      )
    }

  // Sampler ABI: min/mag filters, U/V address modes, unnormalized, compare enable/op,
  // border colour, float bias/min/max LOD, mipmap mode, then W address mode.
  private def sampleTexel(
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
      (0 until components).foldLeft[AddressSpace.ReadResult[Array[Double]]](Right(Array(0.0, 0.0, 0.0, 1.0))) {
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
  private def sampleAddress(value: Long, size: Long, mode: Int): Option[Long] = mode match {
    case ImageDescriptor.AddressMode.Repeat         => Some(Math.floorMod(value, size))
    case ImageDescriptor.AddressMode.MirroredRepeat =>
      val wrapped = Math.floorMod(value, 2 * size); Some(if (wrapped >= size) 2 * size - 1 - wrapped else wrapped)
    case ImageDescriptor.AddressMode.ClampToEdge       => Some(Math.max(0L, Math.min(size - 1, value)))
    case ImageDescriptor.AddressMode.ClampToBorder     => Option.when(value >= 0 && value < size)(value)
    case ImageDescriptor.AddressMode.MirrorClampToEdge =>
      Some(Math.max(0L, Math.min(size - 1, if (value < 0) -value - 1 else value)))
    case _ => None
  }
  private def sampleCompare(op: Int, reference: Double, depth: Double): Double = {
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
  private val imageZeroOffset    = Array(0L, 0L, 0L)
  private val imageGatherOffsets = Array((0L, 1L), (1L, 1L), (1L, 0L), (0L, 0L))

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

  private def imageGradientLod(
      level: ImageLevel,
      direction: Array[Double],
      dx: Array[Double],
      dy: Array[Double],
      dimensions: Int,
      projection: Option[CubeMap.Projection],
      unnormalized: Boolean
  ): Double = {
    val width    = if (unnormalized) 1.0 else level.width.toDouble
    val height   = if (unnormalized) 1.0 else level.height.toDouble
    val (gx, gy) = projection match {
      case Some(p) =>
        val x = CubeMap.gradient(p.face, direction, dx); val y = CubeMap.gradient(p.face, direction, dy)
        (Array(x._1, x._2), Array(y._1, y._2))
      case None => (dx, dy)
    }
    val rho = if (dimensions == 1) Math.max(Math.abs(gx(0) * width), Math.abs(gy(0) * width))
    else if (dimensions == 3 && projection.isEmpty) {
      val depth = if (unnormalized) 1.0 else level.depth.toDouble
      Math.max(
        Math.hypot(Math.hypot(gx(0) * width, gx(1) * height), gx(2) * depth),
        Math.hypot(Math.hypot(gy(0) * width, gy(1) * height), gy(2) * depth)
      )
    } else Math.max(Math.hypot(gx(0) * width, gx(1) * height), Math.hypot(gy(0) * width, gy(1) * height))
    Math.log(rho) / Math.log(2.0)
  }

  private def sampleImage(
      memory: MemoryState,
      values: InvocationValues,
      image: Pointer,
      coords: Array[Long],
      dref: Option[Double],
      explicit: Boolean,
      mods: Option[Operand],
      gradients: Option[(Array[Double], Array[Double])],
      dimensions: Int,
      arrayed: Boolean,
      cube: Boolean,
      gather: Option[Int],
      vulkan: Boolean
  ): AddressSpace.ReadResult[Array[Long]] = {
    def float(word: Long): Double = IEEE754.binary32ToFloat(Binary32.low(word)).toDouble
    val volume                    = dimensions == 3 && !cube
    for {
      sampler <- image.sampler.toRight(Fault.InvalidRequest("sample operation has no sampler"))
      words   <-
        (0 until ImageDescriptor.SamplerWord.Count).foldLeft[AddressSpace.ReadResult[Array[Long]]](
          Right(Array.ofDim[Long](ImageDescriptor.SamplerWord.Count))
        ) { (result, index) =>
          for {
            data <- result
            word <- memory.descriptor(sampler, index * 4, 4)
          } yield { data(index) = word; data }
        }
      formatWord <- memory.descriptor(image, ImageDescriptor.Layout.Format, 4)
      format = (formatWord & ImageDescriptor.Flags.FormatMask).toInt
      levels <- imageLevels(memory, image)
      _      <- Either.cond(
        coords.length >= dimensions + (if (arrayed) 1 else 0) && coords.take(dimensions + (if (arrayed) 1
                                                                                           else 0)).forall(w =>
          java.lang.Double.isFinite(float(w))
        ),
        (),
        Fault.InvalidRequest("invalid sample coordinate dimensionality")
      )
      direction  = coords.take(dimensions).map(float(_))
      projection = if (cube) Some(CubeMap.project(direction(0), direction(1), direction(2))) else None
      _ <- Either.cond(
        !cube || words(ImageDescriptor.SamplerWord.Unnormalized) == 0,
        (),
        Fault.InvalidRequest("cube sampling requires normalised coordinates")
      )
      operands    = mods.map(_.words.toVector.map(_.raw).toArray).getOrElse(Array(0))
      mask        = operands.headOption.getOrElse(-1)
      lodMask     = mask & (ImageOperands.Bias | ImageOperands.Lod | ImageOperands.Grad)
      offsetIndex = 1 + (if (lodMask == ImageOperands.Grad) 2 else if (lodMask != 0) 1 else 0)
      _ <- Either.cond(
        if (gather.isDefined)
          mask == 0 || mask == ImageOperands.ConstOffset || mask == ImageOperands.Offset || mask == ImageOperands.ConstOffsets
        else
          (mask & ~(ImageOperands.Bias | ImageOperands.Lod | ImageOperands.Grad | ImageOperands.ConstOffset)) == 0 && (lodMask == 0 || lodMask == ImageOperands.Bias || lodMask == ImageOperands.Lod || lodMask == ImageOperands.Grad),
        (),
        Fault.Unsupported("unsupported sample/gather image operands")
      )
      _ <- Either.cond(
        operands.length == offsetIndex + (if (
                                            (mask & (ImageOperands.ConstOffset | ImageOperands.Offset | ImageOperands.ConstOffsets)) != 0
                                          ) 1
                                          else 0),
        (),
        Fault.InvalidRequest("invalid sample operand count")
      )
      offset <- if ((mask & (ImageOperands.ConstOffset | ImageOperands.Offset)) == 0) Right(imageZeroOffset)
      else if (cube) Left(Fault.InvalidRequest("cube sampling does not allow ConstOffset"))
      else imageConstOffset(values, operands(offsetIndex), dimensions)
      gatherOffsets <- if ((mask & ImageOperands.ConstOffsets) == 0) Right(imageGatherOffsets)
      else if (cube) Left(Fault.InvalidRequest("cube gather does not allow ConstOffsets"))
      else values.get(operands(offsetIndex)) match {
        case Some(VMValue.Bits(_, lanes)) if lanes.length == 8 =>
          val offsets = lanes.map(_.toInt.toLong)
          Either.cond(
            offsets.forall(v => v >= -8 && v <= 7),
            Array.tabulate(4)(i => (offsets(i * 2), offsets(i * 2 + 1))),
            Fault.InvalidRequest("gather ConstOffsets exceeds device limits")
          )
        case _ => Left(Fault.InvalidRequest("gather ConstOffsets requires four two-dimensional offsets"))
      }
      lod <- if (lodMask == ImageOperands.Bias || lodMask == ImageOperands.Lod) {
        values.get(operands(1)) match {
          case Some(VMValue.Bits(_, lanes)) if lanes.length == 1 => Right(float(lanes(0)))
          case _ => Left(Fault.InvalidRequest("sample LOD is not scalar"))
        }
      } else Right(0.0)
      resolvedGradients <- if (lodMask == ImageOperands.Grad) {
        (values.get(operands(1)), values.get(operands(2))) match {
          case (Some(VMValue.Bits(_, dx)), Some(VMValue.Bits(_, dy))) =>
            Right(Some((dx.take(dimensions).map(float(_)), dy.take(dimensions).map(float(_)))))
          case _ => Left(Fault.InvalidRequest("sample gradients are not floating values"))
        }
      } else Right(gradients.map((dx, dy) => (dx.take(dimensions), dy.take(dimensions))))
      _ <- Either.cond(
        gather.isDefined || !explicit || lodMask == ImageOperands.Lod || lodMask == ImageOperands.Grad,
        (),
        Fault.Unsupported("explicit sampling requires Lod or Grad")
      )
      _ <- Either.cond(
        gather.isDefined || explicit || gradients.nonEmpty || words(ImageDescriptor.SamplerWord.MinFilter) == words(
          ImageDescriptor.SamplerWord.MagFilter
        ) || float(words(ImageDescriptor.SamplerWord.MaxLod)) <= 0 || float(
          words(ImageDescriptor.SamplerWord.MinLod)
        ) > 0,
        (),
        Fault.Unsupported("implicit LOD with distinct min/mag filters needs fragment derivatives")
      )
      baseLod <- if (lodMask == ImageOperands.Lod) Right(lod)
      else resolvedGradients match {
        case None           => Right(0.0)
        case Some((dx, dy)) => imageLevel(memory, image, 0).flatMap { level =>
            Either.cond(
              dx.length == dimensions && dy.length == dimensions,
              (),
              Fault.InvalidRequest("sample gradient dimensionality does not match")
            ).map { _ =>
              imageGradientLod(
                level,
                direction,
                dx,
                dy,
                dimensions,
                projection,
                words(ImageDescriptor.SamplerWord.Unnormalized) != 0
              )
            }
          }
      }
      // Sampler bias applies to both explicit and implicit LOD. Shader Bias is
      // combined with it before the device's maxSamplerLodBias clamp.
      bias = Math.max(
        -16.0,
        Math.min(
          16.0,
          float(words(ImageDescriptor.SamplerWord.MipLodBias)) + (if (lodMask == ImageOperands.Bias) lod else 0.0)
        )
      )
      lambda = Math.max(
        float(words(ImageDescriptor.SamplerWord.MinLod)),
        Math.min(float(words(ImageDescriptor.SamplerWord.MaxLod)), baseLod + bias)
      )
      filter = if (gather.isDefined) ImageDescriptor.Filter.Linear
      else if (lambda > 0) words(ImageDescriptor.SamplerWord.MinFilter).toInt
      else words(ImageDescriptor.SamplerWord.MagFilter).toInt
      _ <- Either.cond(
        filter == ImageDescriptor.Filter.Nearest || filter == ImageDescriptor.Filter.Linear,
        (),
        Fault.Unsupported("sample filter is not nearest or linear")
      )
      _ <- Either.cond(
        gather.isDefined || filter == ImageDescriptor.Filter.Nearest || !imageIsInteger(format),
        (),
        Fault.InvalidRequest("integer images require nearest filtering")
      )
      reference = dref.getOrElse(0.0)
      _ <-
        Either.cond(
          vulkan || dref.isDefined == (words(ImageDescriptor.SamplerWord.CompareEnable) != 0),
          (),
          Fault.InvalidRequest("depth comparison and sampler disagree")
        )
      _ <- Either.cond(
        gather.isDefined || explicit || gradients.nonEmpty || levels == 1 || float(
          words(ImageDescriptor.SamplerWord.MinLod)
        ) == float(
          words(ImageDescriptor.SamplerWord.MaxLod)
        ) || float(words(ImageDescriptor.SamplerWord.MaxLod)) <= 0,
        (),
        Fault.Unsupported("implicit mip selection needs fragment derivatives")
      )
      result <- {
        def atLevel(level: Int): AddressSpace.ReadResult[Array[Double]] =
          imageLevel(memory, image, level).flatMap { data =>
            val layerCount = if (cube) data.layers / 6 else data.layers
            val layer      =
              if (arrayed) Math.max(0L, Math.min(layerCount - 1, Math.rint(float(coords(dimensions))).toLong)) else 0L
            def layerPixels(index: Long): Pointer =
              data.pixels.copy(address =
                data.pixels.address + index * data.width * data.height * ImageTexelCodec.texelBytes(format)
              )
            val sx =
              projection.fold(float(coords(0)))(_.s) * (if (words(ImageDescriptor.SamplerWord.Unnormalized) != 0) 1.0
                                                        else data.width.toDouble) + offset(0)
            val sy = if (dimensions == 1) 0.5
            else projection.fold(float(coords(1)))(_.t) * (if (words(ImageDescriptor.SamplerWord.Unnormalized) != 0) 1.0
                                                           else data.height.toDouble) + offset(1)
            val sz = if (volume) float(coords(2)) * (if (words(ImageDescriptor.SamplerWord.Unnormalized) != 0) 1.0
                                                     else data.depth.toDouble) + offset(2)
            else 0.5
            val components =
              if (
                format == ImageDescriptor.Format.D16Unorm || format == ImageDescriptor.Format.S8Uint || format == ImageDescriptor.Format.D32SfloatS8Stencil
              ) 1
              else ImageFormats.integer(
                format
              ).fold(ImageFormats.normalised(format).fold(
                ImageFormats.narrow(format).fold(wordComponents(format))(_.components)
              )(_.components))(_.components) match {
                case 0     => 4
                case count => count
              }
            val border = Array.tabulate(4) { lane =>
              if (lane >= components) { if (lane == 3) 1.0 else 0.0 }
              else if (
                words(
                  ImageDescriptor.SamplerWord.BorderColour
                ) >= ImageDescriptor.BorderColour.FloatOpaqueWhite || (lane == 3 && words(
                  ImageDescriptor.SamplerWord.BorderColour
                ) >= ImageDescriptor.BorderColour.FloatOpaqueBlack)
              ) 1.0
              else 0.0
            }
            def compare(data: Array[Double]): Array[Double] =
              if (dref.isDefined) Array(sampleCompare(
                words(ImageDescriptor.SamplerWord.CompareOp).toInt,
                if (format == ImageDescriptor.Format.D16Unorm) Math.max(0.0, Math.min(1.0, reference)) else reference,
                data(0)
              ))
              else data
            def texel(x: Long, y: Long, z: Long): AddressSpace.ReadResult[Array[Double]] = projection match {
              case Some(p) =>
                if (data.width != data.height || data.layers < 6 || data.layers % 6 != 0)
                  Left(Fault.InvalidRequest("cube image requires square faces in groups of six"))
                else {
                  val neighbours = CubeMap.taps(p.face, x, y, data.width, filter == ImageDescriptor.Filter.Linear)
                  // Cube corners have no unique neighbour. Selecting the incident
                  // face is permitted and preserves integer and Dref gather values.
                  val taps = if (gather.isDefined) neighbours.take(1) else neighbours
                  taps.foldLeft[AddressSpace.ReadResult[Array[Double]]](Right(
                    Array.fill(if (dref.isDefined) 1 else 4)(0.0)
                  )) {
                    (result, tap) =>
                      for {
                        sum   <- result
                        value <-
                          sampleTexel(memory, layerPixels(layer * 6 + tap.face), format, data.width, tap.x, tap.y)
                      } yield { val v = compare(value); Array.tabulate(sum.length)(i => sum(i) + v(i) / taps.size) }
                  }
                }
              case None =>
                val fetched =
                  (
                    sampleAddress(x, data.width, words(ImageDescriptor.SamplerWord.AddressU).toInt),
                    sampleAddress(y, data.height, words(ImageDescriptor.SamplerWord.AddressV).toInt),
                    if (volume) sampleAddress(z, data.depth, words(ImageDescriptor.SamplerWord.AddressW).toInt)
                    else Some(0L)
                  ) match {
                    case (Some(ix), Some(iy), Some(iz)) =>
                      sampleTexel(memory, layerPixels(layer), format, data.width, ix, iz * data.height + iy)
                    case _ => Right(border)
                  }
                fetched.map(compare(_))
            }
            if (gather.isDefined) {
              val x = Math.floor(sx - 0.5).toLong; val y = Math.floor(sy - 0.5).toLong
              gatherOffsets.foldLeft[AddressSpace.ReadResult[Array[Double]]](Right(Array.empty[Double])) {
                (result, delta) =>
                  for {
                    accumulated <- result
                    value       <- texel(x + delta._1, y + delta._2, 0)
                    mapped      <- if (dref.isEmpty) swizzleImage(memory, image, value) else Right(value)
                  } yield accumulated :+ mapped(gather.get)
              }
            } else if (filter == ImageDescriptor.Filter.Nearest)
              texel(Math.floor(sx).toLong, Math.floor(sy).toLong, Math.floor(sz).toLong)
            else {
              val x  = Math.floor(sx - 0.5).toLong; val y = Math.floor(sy - 0.5).toLong
              val fx = sx - 0.5 - x; val fy               = sy - 0.5 - y
              def plane(z: Long): AddressSpace.ReadResult[Array[Double]] = for {
                a <- texel(x, y, z)
                b <- texel(x + 1, y, z)
                c <- texel(x, y + 1, z)
                d <- texel(x + 1, y + 1, z)
              } yield Array.tabulate(a.length)(i =>
                (1 - fy) * ((1 - fx) * a(i) + fx * b(i)) + fy * ((1 - fx) * c(i) + fx * d(i))
              )
              if (!volume) plane(0)
              else {
                val z  = Math.floor(sz - 0.5).toLong
                val fz = sz - 0.5 - z
                for {
                  a <- plane(z)
                  b <- plane(z + 1)
                } yield Array.tabulate(a.length)(i => (1 - fz) * a(i) + fz * b(i))
              }
            }
          }
        val selected = Math.max(0.0, Math.min(levels - 1.0, lambda))
        if (gather.isDefined) atLevel(0)
        else if (words(ImageDescriptor.SamplerWord.MipmapMode) == ImageDescriptor.MipmapMode.Nearest)
          atLevel(Math.floor(selected + 0.5).toInt)
        else {
          val low      = Math.floor(selected).toInt
          val fraction = selected - low
          for {
            a <- atLevel(low)
            b <- atLevel(Math.min(low + 1, levels - 1))
          } yield Array.tabulate(a.length)(i => if (fraction == 0) a(i) else a(i) * (1 - fraction) + b(i) * fraction)
        }
      }
      swizzled <- if (dref.isEmpty && gather.isEmpty) swizzleImage(memory, image, result) else Right(result)
    } yield swizzled.map(value =>
      if (imageIsInteger(format) && dref.isEmpty) value.toLong & 0xffffffffL
      else IEEE754.floatToBinary32(value.toFloat).raw.toLong & 0xffffffffL
    )
  }
  private def readInputAttachment(
      program: VMProgram,
      memory: MemoryState,
      values: InvocationValues,
      image: Operand,
      coordinate: Operand,
      mods: Option[Operand]
  ): AddressSpace.ReadResult[Array[Long]] = for {
    source    <- address(values, id(image))
    imageType <- program.runtimeType(source.pointee) match {
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
    position <- program.builtins.view.collectFirst {
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
          if imageType._2 && m.words.length == 2 && (m.words.head.raw & ~(ImageOperands.SignExtend | ImageOperands.ZeroExtend)) == ImageOperands.Sample &&
            (m.words.head.raw & (ImageOperands.SignExtend | ImageOperands.ZeroExtend)) != (ImageOperands.SignExtend | ImageOperands.ZeroExtend) =>
        values.get(m.words.apply(1).raw) match {
          case Some(VMValue.Bits(_, lanes)) if lanes.length == 1 => Right(lanes(0).toInt)
          case _ => Left(Fault.InvalidRequest("input attachment sample index is not scalar"))
        }
      case None if !imageType._2 => Right(0)
      case Some(m)
          if !imageType._2 && m.words.length == 1 &&
            (m.words.head.raw == 0 || m.words.head.raw == ImageOperands.SignExtend || m.words.head.raw == ImageOperands.ZeroExtend) =>
        Right(0)
      case _ => Left(Fault.Unsupported("input attachment operands require a sample index only for multisampled images"))
    }
    x = Math.floor(IEEE754.binary32ToFloat(Binary32.low(px)).toDouble).toLong + coord(0).toInt
    y = Math.floor(IEEE754.binary32ToFloat(Binary32.low(py)).toDouble).toLong + coord(1).toInt
    texel <- if (x < 0 || y < 0 || x >= data.width || y >= data.height || sample < 0 || sample >= data.samples)
      Right(Array.fill(4)(0.0))
    else sampleTexel(memory, data.pixels, data.format, data.width * data.samples, x * data.samples + sample, y)
  } yield
    if (imageIsInteger(data.format)) texel.map(_.toLong & 0xffffffffL)
    else texel.map(x => IEEE754.floatToBinary32(x.toFloat).raw.toLong & 0xffffffffL)
}
