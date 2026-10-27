package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import VMIR.Matrix.*
import VMIR.Tcgen05.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

/** The tensor memory of the fifth-generation tensor cores (tcgen05): per CTA, 128 lanes of 512 32-bit columns,
  * allocated in granules of 32 columns. An address holds its lane in the high half and its column in the low half.
  */
private[isa] object TensorMemory {
  val Lanes: Int    = 128
  val Columns: Int  = 512
  val Granule: Int  = 32
  val Granules: Int = Columns / Granule

  def lane(address: Long): Int   = ((address >>> 16) & 0xffffL).toInt
  def column(address: Long): Int = (address & 0xffffL).toInt

  /** One CTA's tensor memory and its allocation state: a bit per granule, set while allocated. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  class State {
    private var cells: Array[Int] | Null = null
    private var allocated: Long          = 0L
    private var relinquished: Boolean    = false

    /** The allocated granules, a bit each. */
    def taken: Long = allocated

    def permitted: Boolean = !relinquished

    def relinquish(): Unit = relinquished = true

    /** Frees the granules of `mask` if all are allocated, as tcgen05.dealloc does. */
    def free(mask: Long): Boolean = {
      val whole = (allocated & mask) == mask
      if (whole) allocated &= ~mask
      whole
    }

    /** Keeps only the allocated granules of `mask`, as UTCATOMSWS.AND does. */
    def retain(mask: Long): Unit = allocated &= mask

    private def storage: Array[Int] = cells match {
      case null =>
        val created = Array.ofDim[Int](Lanes * Columns)
        cells = created
        created
      case found: Array[Int] => found
    }

    def read(lane: Int, column: Int): Long = storage(lane * Columns + column).toLong & 0xffffffffL

    def write(lane: Int, column: Int, value: Long): Unit = storage(lane * Columns + column) = value.toInt

    /** Allocates `count` granules at the lowest free run aligned to `count`, returning its first granule. */
    def allocate(count: Int): Option[Int] = (0 until Granules by count).find { start =>
      val mask = ((1L << count) - 1L) << start
      (allocated & mask) == 0L
    }.map { start =>
      allocated |= ((1L << count) - 1L) << start
      start
    }

    def reset(): Unit = {
      cells = null
      allocated = 0L
      relinquished = false
    }
  }

  /** Moves the first `rows - 1` of `rows` rows of `columns` columns down by one row, as tcgen05.shift and `.ashift` do.
    */
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def shiftRows(state: State, firstLane: Int, firstColumn: Int, rows: Int, columns: Int): Unit =
    (rows - 2 to 0 by -1).foreach { row =>
      (0 until columns).foreach(offset =>
        state.write(firstLane + row + 1, firstColumn + offset, state.read(firstLane + row, firstColumn + offset))
      )
    }

  /** The granules `columns` columns from `column` on cover, when they are whole granules. */
  def granules(column: Int, columns: Long): Option[Long] =
    Option.when(
      column % Granule == 0 && columns >= Granule && columns % Granule == 0 && column + columns <= Columns
    )(((1L << (columns / Granule).toInt) - 1L) << (column / Granule))

  object SharedDescriptor {
    def decode(bits: Long): Either[NonEmpty[String], SharedDescriptor] = {
      val start   = MatrixDescriptor.start(bits)
      val leading = MatrixDescriptor.leading(bits)
      val stride  = MatrixDescriptor.stride(bits)
      val mode    = (bits >>> 61) & 7L
      if (MatrixDescriptor.base(bits) != 0)
        Left("shared memory descriptors with a matrix base offset are not supported")
      else if (Bits.isSet(bits, 52)) Left("absolute leading dimension addresses are not supported")
      else mode match {
        case 0L => Right(SharedDescriptor(start, leading, stride, 0, atoms = false))
        case 1L => Right(SharedDescriptor(start, leading, stride, 128, atoms = true))
        case 2L => Right(SharedDescriptor(start, leading, stride, 128, atoms = false))
        case 4L => Right(SharedDescriptor(start, leading, stride, 64, atoms = false))
        case 6L => Right(SharedDescriptor(start, leading, stride, 32, atoms = false))
        case _  => Left(s"invalid shared memory descriptor swizzling mode $mode")
      }
    }
  }

  /** A shared memory matrix descriptor: start, leading and stride byte offsets within the CTA's shared memory, and the
    * swizzle's row width in bytes (0 without swizzling), with 32-byte atoms when `atoms`.
    */
  case class SharedDescriptor(
      start: Long,
      leading: Long,
      stride: Long,
      swizzle: Int in (0, 32, 64, 128),
      atoms: Boolean
  ) {
    private def swizzled(offset: Long): Long =
      if (atoms) offset ^ (((offset >>> 7) & 3L) << 5) else MatrixDescriptor.swizzled(offset, swizzle)

    private def width: Int = if (atoms) 128 else swizzle

    /** The shared offset of the 16-byte chunk holding K-chunk `chunk` of row `row` of a K-major matrix. */
    def major(row: Int, chunk: Int): Long =
      if (width == 0) start + (row % 8) * 16L + (row / 8) * stride + chunk * leading
      else swizzled(start + (row % 8).toLong * width + (row / 8) * stride + chunk * 16L)

    /** The shared offset of the 16-byte chunk holding MN-chunk `chunk` of K index `k` of an MN-major matrix. */
    def minor(chunk: Int, k: Int): Long =
      if (width == 0) start + chunk * stride + (k % 8) * 16L + (k / 8) * leading
      else {
        val across = width / 16
        swizzled(
          start +
            (chunk % across) * 16L +
            (chunk / across) * leading +
            (k % 8).toLong * width +
            (k / 8) * stride
        )
      }
  }

  /** Where a datapath organisation puts row `row` and column `column` of an M x N matrix in the tensor memory of the
    * CTA pair, from the address's lane and column: the CTA (0 for the even one, 1 for the odd one), the lane and the
    * column.
    */
  class Placement(multiply: Multiply, product: Product) {
    private val n = product.n
    // the datapath organisation, in the order the cases below test them
    private val layout =
      if (multiply.pair && product.m == 256) 0
      else if (multiply.pair && product.sparse) 1
      else if (multiply.pair) 2
      else if (product.m == 128) 3
      else if (product.m == 64 && multiply.ws) 4
      else if (product.m == 64) 5
      else 6

    def cta(row: Int): Int = layout match {
      case 0 => row / 128
      case 1 => row / 64
      case 2 => row / 64
      case _ => 0
    }

    def lane(row: Int, column: Int): Int = layout match {
      case 0 => row                                                  % 128
      case 1 => 32 * (row % 64 / 16) + row                           % 16
      case 2 => 32 * (row % 64 / 32) + 64 * (column / (n / 2)) + row % 32
      case 3 => row
      case 4 => 32 * (row / 32) + 64 * (column / (n / 2)) + row      % 32
      case 5 => 32 * (row / 16) + row                                % 16
      case _ => 32 * (column / (n / 4)) + row
    }

    def column(column: Int): Int = layout match {
      case 2 | 4 => column % (n / 2)
      case 6     => column % (n / 4)
      case _     => column
    }
  }

  def placement(multiply: Multiply, product: Product): Placement = Placement(multiply, product)

  /** A tcgen05.mma's product as its instruction descriptor and qualifiers give it, with the product `multiply` takes:
    * the shape, element types, accumulation, sparsity and scaling.
    */
  case class Product(
      multiply: Operation.Multiply,
      negateA: Boolean,
      negateB: Boolean,
      transposeA: Boolean,
      transposeB: Boolean,
      selector: Int,
      scaleIds: (a: Int, b: Int),
      shift: Int,
      sparse: Boolean
  ) {
    def m: Int = multiply.d.rows
    def n: Int = multiply.d.columns
    def k: Int = multiply.b.rows

    /** [[TensorSemantics.blocked]]'s constants, for a product that accumulates in blocks. */
    val blocked: Option[TensorSemantics.BlockedProduct] = multiply.accumulation match {
      case accumulation: Accumulation.Blocked => Some(TensorSemantics.BlockedProduct(multiply, accumulation))
      case _                                  => None
    }
  }

  /** A bit field of an instruction descriptor. */
  case class Field(offset: Int, width: Int) {
    def of(bits: Long): Long = (bits >>> offset) & ((1L << width) - 1L)
    def codes: Int           = 1 << width
  }

  /** The fields of an instruction descriptor that pick the element formats, scaling and sparsity of a product. */
  object Fields {
    val Sparse: Field      = Field(offset = 2, width = 1)
    val Saturate: Field    = Field(offset = 3, width = 1)
    val Accumulator: Field = Field(offset = 4, width = 2)
    val A: Field           = Field(offset = 7, width = 3)
    val NegateA: Field     = Field(offset = 13, width = 1)
    val NegateB: Field     = Field(offset = 14, width = 1)

    def b(kind: Kind): Field     = Field(offset = 10, width = if (kind == Kind.MXF4 || kind == Kind.MXF4NVF4) 2 else 3)
    def scale(kind: Kind): Field = Field(offset = 23, width = if (kind == Kind.MXF4NVF4) 2 else 1)

    /** The fields `kind` takes that change the product's element math, apart from negation. */
    def formats(multiply: Multiply): Vec[Field] =
      Vec(A, b(multiply.kind)) ++
        (if (multiply.kind.scaled) Vec(scale(multiply.kind)) else Vec(Accumulator)) ++
        (if (multiply.kind == Kind.I8) Vec(Saturate) else Vec.empty[Field]) ++
        (if (multiply.sparseByDescriptor) Vec(Sparse) else Vec.empty[Field])
  }

  /** A product a descriptor can describe for a form, with the codes of [[Fields.formats]] that give it. */
  case class Variant(product: Product, codes: Vec[Long])

  /** Every product the descriptors of `multiply`'s kind can describe apart from their shape and negation, which the
    * element math does not read, each with the format codes that select it.
    */
  def variants(multiply: Multiply): Vec[Variant] = {
    val fields = Fields.formats(multiply)
    val shape  = if (multiply.kind.scaled) 1L << 27 else 8L << 24
    val base   = shape | (8L << 17) | (if (!multiply.sparseByDescriptor && multiply.metadata.nonEmpty) 4L else 0L)
    Vec.from(0 until fields.map(_.codes).product).flatMap { combination =>
      val codes = fields.scanLeft(combination)((rest, field) => rest / field.codes).zip(fields).map((rest, field) =>
        (rest % field.codes).toLong
      )
      val bits = base + codes.zip(fields).map((code, field) => code << field.offset).sum
      Vec.from(product(multiply, bits).toOption.map(Variant(_, codes)))
    }
  }

  /** The accumulator element a descriptor's D type names, if the kind takes it. */
  private def accumulator(kind: Kind, code: Long): Option[Element] = (kind, code) match {
    case (Kind.F16 | Kind.F8F6F4, 0L) => Some(Element.F16)
    case (Kind.I8, 2L)                => Some(Element.S32)
    case (Kind.I8, _)                 => None
    case (_, 1L)                      => Some(Element.F32)
    case _                            => None
  }

  private def input(kind: Kind, code: Long): Option[Element] = (kind, code) match {
    // one SASS instruction serves both kinds, the A and B types telling them apart
    case (Kind.F16 | Kind.TF32, 0L)        => Some(Element.F16)
    case (Kind.F16 | Kind.TF32, 1L)        => Some(Element.BF16)
    case (Kind.F16 | Kind.TF32, 2L)        => Some(Element.TF32)
    case (Kind.I8, 0L)                     => Some(Element.U8)
    case (Kind.I8, 1L)                     => Some(Element.S8)
    case (Kind.F8F6F4 | Kind.MXF8F6F4, 0L) => Some(Element.E4M3)
    case (Kind.F8F6F4 | Kind.MXF8F6F4, 1L) => Some(Element.E5M2)
    case (Kind.F8F6F4 | Kind.MXF8F6F4, 3L) => Some(Element.E2M3)
    case (Kind.F8F6F4 | Kind.MXF8F6F4, 4L) => Some(Element.E3M2)
    case (Kind.F8F6F4 | Kind.MXF8F6F4, 5L) => Some(Element.E2M1Byte)
    case (Kind.MXF4 | Kind.MXF4NVF4, 1L)   => Some(Element.E2M1)
    case _                                 => None
  }

  /** The product the instruction descriptor value `descriptor` describes for `multiply`: its kind on its architecture,
    * with its CTA pair, `.ws` mode and sparsity, and with the block scale vector size its qualifiers give (0 for the
    * kind's default).
    */
  def product(multiply: Multiply, descriptor: Long): Either[NonEmpty[String], Product] = {
    val kind                                                     = multiply.kind
    val pair                                                     = multiply.pair
    val ws                                                       = multiply.ws
    val sparse                                                   = multiply.sparse(descriptor)
    val vector                                                   = multiply.vector
    def field(offset: Int in 0 ~ 63, width: Int in 1 ~ 63): Long = (descriptor >>> offset) & ((1L << width) - 1L)
    val scaled                                                   = kind.scaled
    val m      = if (scaled) field(offset = 27, width = 2).toInt << 7 else field(offset = 24, width = 5).toInt << 4
    val n      = field(offset = 17, width = 6).toInt << 3
    val a      = input(kind, Fields.A.of(descriptor))
    val k      = (if (a.contains(Element.TF32)) 8 else kind.depth) * (if (sparse) 2 else 1)
    val b      = input(kind, Fields.b(kind).of(descriptor))
    val d      = if (scaled) Some(Element.F32) else accumulator(kind, Fields.Accumulator.of(descriptor))
    val validM =
      if (pair) Set(128, 256)(m) && !ws
      else if (ws) Set(32, 64, 128)(m)
      else if (scaled) m == 128
      else Set(64, 128)(m)
    val validN =
      if (ws) Set(64, 128, 256)(n) && (!sparse || n <= 128)
      else n >=
        (if (pair) 16 else 8) && n <= 256 &&
        n %
        (if (pair) 16 else 8) == 0 &&
        (kind != Kind.I8 || n <= 32 || n % 16 == 0) &&
        (kind != Kind.I8 || !pair || n   % 32 == 0)
    val scaleCode   = Fields.scale(kind).of(descriptor)
    val scaleFormat =
      if (!scaled) None
      else if (kind == Kind.MXF4NVF4 && scaleCode == 0L) Some(ScaleFormat.UE4M3)
      else if ((scaleCode & 1L) == 1L) Some(ScaleFormat.UE8M0)
      else None
    val vectorSize = if (vector != 0) vector else if (kind == Kind.MXF8F6F4) 1 else 2
    (a, b, d) match {
      case _ if (Fields.Sparse.of(descriptor) == 1L) != sparse =>
        Left("the instruction descriptor's sparsity does not match the instruction")
      case _ if !validM || !validN            => Left(s"invalid tcgen05.mma shape ${m}x${n}x$k")
      case _ if scaled && scaleFormat.isEmpty => Left("invalid block scale factor type")
      case _
          if scaled &&
            (field(offset = 31, width = 1) != 0L || (kind != Kind.MXF8F6F4 && field(offset = 3, width = 1) != 0L)) =>
        Left("only the base K of a block-scaled tcgen05.mma is supported")
      case _ if !scaled && field(offset = 29, width = 1) != 0L => Left("only the base K of a tcgen05.mma is supported")
      case (Some(left), Some(right), Some(result))
          if (kind != Kind.I8 || (field(offset = 13, width = 2) == 0L)) &&
            (kind == Kind.I8 || field(offset = 3, width = 1) == 0L) &&
            (kind != Kind.F16 || result == Element.F32 || (left == Element.F16 && right == Element.F16)) =>
        val shape        = Shape(m.unsafe[Int > 0], n.unsafe[Int > 0], k.unsafe[Int > 0])
        val accumulation = Matrix.accumulation(
          architecture = multiply.architecture,
          shape = shape,
          input = left,
          result = result,
          rounding = ConversionRounding.RN
        )
        val zero = Value.Immediate(0L)
        Right(Product(
          Operation.Multiply(
            a = Fragment(shape, Role.A, left, wmma = false),
            b = Fragment(shape, Role.B, right, wmma = false),
            c = Fragment(shape, Role.C, result, wmma = false),
            d = Fragment(shape, Role.C, result, wmma = false),
            destinations = Vec.empty,
            left = Vec.empty,
            right = Vec.empty,
            addend = Vec.empty,
            accumulation = accumulation,
            saturate = kind == Kind.I8 && Fields.Saturate.of(descriptor) == 1L,
            sparsity = Option.when(sparse)(Sparsity(zero, zero)),
            scaling = scaleFormat.map(format =>
              Scaling(format, vectorSize.unsafe[Int in (1, 2, 4)], zero, zero, zero, zero, zero, zero)
            )
          ),
          negateA = Fields.NegateA.of(descriptor) == 1L,
          negateB = Fields.NegateB.of(descriptor) == 1L,
          transposeA = Bits.isSet(descriptor, 15),
          transposeB = Bits.isSet(descriptor, 16),
          selector = field(0, 2).toInt,
          scaleIds = (a = field(29, 2).toInt, b = field(4, 2).toInt),
          shift = multiply.shift,
          sparse = sparse
        ))
      case _ => Left(s"invalid element types for tcgen05.mma $kind: 0x${descriptor.toHexString}")
    }
  }

  /** The bits an element occupies in shared and tensor memory: the narrow floats of `kind::f8f6f4` take a byte. */
  def container(element: Element): Int = element match {
    case Element.E2M1 => 4
    case Element.TF32 => 32
    case other        => math.max(other.bits, 8)
  }
}
