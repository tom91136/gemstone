package net.kurobako.gemstone.isa.spirv

import Op.*
import VMInvocation.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** The reads of image and sampler descriptors, texels and quad gradients that sampling requests of an invocation. */
private object ImageMachine {
  private def double(value: Double): Long = java.lang.Double.doubleToRawLongBits(value)

  private def sampler(execution: InvocationState, image: Int, missing: NonEmpty[String]): Flow[Pointer] =
    address(execution.values, image).flatMap(_.sampler.toRight(Fault.InvalidRequest(missing)))

  def integral(execution: InvocationState, operand: Int): Long =
    if (execution.values.program.integerShape(execution.values.program.valueType(operand)).isEmpty) 0L else 1L

  def count(execution: InvocationState, operand: Int): Long = execution.values.get(operand) match {
    case Some(VMValue.Bits(_, lanes)) => lanes.length.toLong
    case _                            => -1L
  }

  def numeric(execution: InvocationState, operand: Int): Flow[Unit] = bits(execution.values, operand).map(_ => ())

  /** The dimensions, then the array and cube flags, of the view `image` names, packed a byte each. */
  def view(execution: InvocationState, image: Int, query: Boolean): Flow[Long] = {
    val program = execution.values.program
    address(execution.values, image).flatMap { pointer =>
      val found = program.runtimeType(pointer.pointee) match {
        case Some(RuntimeType.Texture(n, false, arrayed)) => Right((n, if (query) false else arrayed, false))
        case Some(RuntimeType.CubeTexture(arrayed))       => Right((3, if (query) false else arrayed, true))
        case Some(RuntimeType.StorageImage(_, ImageFormat.Unknown, n, false, arrayed, false)) if !query =>
          Right((n, arrayed, false))
        case _ if query => Left(Fault.InvalidRequest("LOD query requires a sampled 1D, 2D, 3D or cube image"))
        case _          => Left(Fault.InvalidRequest("sampling requires an image view, not a texel buffer"))
      }
      found.map((n, arrayed, cube) => n.toLong | (if (arrayed) 1L << 8 else 0L) | (if (cube) 1L << 16 else 0L))
    }
  }

  def samplerPresent(execution: InvocationState, image: Int, missing: NonEmpty[String]): Flow[Unit] =
    sampler(execution, image, missing).map(_ => ())

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def samplerWords(execution: InvocationState, image: Int, into: Int): Flow[Unit] =
    sampler(execution, image, "sample operation has no sampler").flatMap { record =>
      var index  = 0
      var result = Done
      while (index < ImageDescriptor.SamplerWord.Count && result.isRight) {
        execution.memory.descriptor(record, index * 4, 4) match {
          case Left(fault) => result = Left(fault)
          case Right(word) => execution.partials(into + index) = word
        }
        index += 1
      }
      result
    }

  def samplerWord(execution: InvocationState, image: Int, word: Int): Flow[Long] =
    sampler(execution, image, "LOD query has no sampler").flatMap(execution.memory.descriptor(_, word * 4, 4))

  def formatWord(execution: InvocationState, image: Int): Flow[Long] =
    address(execution.values, image).flatMap(execution.memory.descriptor(_, ImageDescriptor.Layout.Format, 4))

  def levelCount(execution: InvocationState, image: Int): Flow[Long] =
    address(execution.values, image).flatMap(ImageOps.imageLevels(execution.memory, _)).map(_.toLong)

  /** Binds level `level` of the view `image` names, whose width, height, depth, layers, samples, row stride and format
    * go to the partial results from `into`.
    */
  def level(execution: InvocationState, image: Int, level: Long, into: Int): Flow[Unit] =
    address(execution.values, image).flatMap(ImageOps.imageLevel(execution.memory, _, level.toInt)).map { data =>
      val out = execution.partials
      out(into) = data.width
      out(into + 1) = data.height
      out(into + 2) = data.depth
      out(into + 3) = data.layers
      out(into + 4) = data.samples.toLong
      out(into + 5) = data.stride
      out(into + 6) = data.format.toLong
      execution.texels = Some(data)
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  /** The texel at column `x` of row `row` of `layer` in the bound level, decoded to four doubles from `into`. */
  def texel(execution: InvocationState, layer: Long, x: Long, row: Long, into: Int): Flow[Unit] = {
    val data    = execution.texels.get
    val pointer = data.pixels.copy(address =
      data.pixels.address + layer * data.stride * data.height * ImageTexelCodec.texelBytes(data.format)
    )
    ImageOps.sampleTexel(execution.memory, pointer, data.format, data.stride, x, row).map { found =>
      var lane = 0
      while (lane < 4) {
        execution.partials(into + lane) = double(found(lane))
        lane += 1
      }
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  /** The texel in the four doubles from `from` has the swizzle of the view `image` names applied. */
  def swizzle(execution: InvocationState, image: Int, from: Long): Flow[Unit] =
    address(execution.values, image).flatMap { pointer =>
      val texel = Array.tabulate(4)(lane => java.lang.Double.longBitsToDouble(execution.partials(from.toInt + lane)))
      ImageOps.swizzleImage(execution.memory, pointer, texel).map { swizzled =>
        var lane = 0
        while (lane < 4) {
          execution.partials(from.toInt + lane) = double(swizzled(lane))
          lane += 1
        }
      }
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  /** Whether the quad's gradients are known, which go as doubles to `into` (a flag, then across, then down), or the
    * invocation waits for them: the `count` words from `from` are its coordinates. Reading them consumes them.
    */
  def gradients(execution: InvocationState, from: Int, count: Long, resultType: Int, dest: Int, into: Int): Flow[Long] =
    if (execution.fragmentQuad && execution.gradients.isEmpty) {
      execution.quadRequest = Some(QuadRequest(
        execution.currentFunction.id,
        execution.pc,
        resultType,
        dest,
        IArray.unsafeFromArray(Array.tabulate(count.toInt)(lane => execution.partials(from + lane))),
        Derivative.Gradients
      ))
      Right(-1L)
    } else {
      val found = execution.gradients
      execution.partials(into) = if (found.isDefined) 1L else 0L
      found.foreach { (dx, dy) =>
        var axis = 0
        while (axis < 3) {
          execution.partials(into + 1 + axis) = if (axis < dx.length) double(dx(axis)) else 0L
          execution.partials(into + 4 + axis) = if (axis < dy.length) double(dy(axis)) else 0L
          axis += 1
        }
      }
      execution.gradients = None
      Right(execution.partials(into))
    }

  def lodResult(execution: InvocationState, typeId: Int): Flow[Unit] =
    execution.values.program.floatingShape(typeId).filter((w, n) => w == 32 && n == 2)
      .toRight(Fault.InvalidRequest("LOD query result must be float32 vec2")).map(_ => ())
}
