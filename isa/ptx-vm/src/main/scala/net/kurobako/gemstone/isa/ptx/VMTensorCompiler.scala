package net.kurobako.gemstone.isa.ptx

import Lowering.*
import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Matrix.*
import net.kurobako.gemstone.isa.nvidia.VMIR.{Instruction as _, *}

private object VMTensorCompiler {
  private enum MemoryOperation derives CanEqual {
    case Load
    case Store
  }

  /** The qualifiers of a matrix product: `mma`, `mma.sp` or `wmma.mma`. */
  private case class Product(
      wmma: Boolean,
      sparse: Boolean,
      shape: Shape,
      aColumnMajor: Boolean,
      bColumnMajor: Boolean,
      d: Element,
      a: Element,
      b: Element,
      c: Element,
      saturate: Boolean,
      rounding: ConversionRounding,
      bits: Option[BitOperation],
      scale: Option[(format: ScaleFormat, vector: Int in (1, 2, 4))]
  ) {
    def fragments: Vec[Fragment] = Vec(
      Fragment(shape, Role.C, d, wmma),
      Fragment(if (sparse) shape.copy(k = (shape.k / 2).unsafe[Int > 0]) else shape, Role.A, a, wmma, aColumnMajor),
      Fragment(shape, Role.B, b, wmma, bColumnMajor),
      Fragment(shape, Role.C, c, wmma)
    )
  }

  private case class WmmaMemoryForm(
      operation: MemoryOperation,
      role: Role,
      layout: Boolean,
      shape: Shape,
      space: MemorySpace,
      element: Element,
      dRole: Boolean
  )

  private def columnMajor(value: Modifier): Option[Boolean] = value match {
    case Modifier.Row => Some(false)
    case Modifier.Col => Some(true)
    case _            => None
  }

  private def role(value: Modifier): Option[Role] = value match {
    case Modifier.A              => Some(Role.A)
    case Modifier.B              => Some(Role.B)
    case Modifier.C | Modifier.D => Some(Role.C)
    case _                       => None
  }

  /** The element a type qualifier names; 4-bit E2M1 elements are packed only for the `mxf4` kinds. */
  private def element(value: Modifier, packedE2M1: Boolean = false): Option[Element] = value match {
    case Modifier.F16  => Some(Element.F16)
    case Modifier.Bf16 => Some(Element.BF16)
    case Modifier.Tf32 => Some(Element.TF32)
    case Modifier.F32  => Some(Element.F32)
    case Modifier.F64  => Some(Element.F64)
    case Modifier.S8   => Some(Element.S8)
    case Modifier.U8   => Some(Element.U8)
    case Modifier.S4   => Some(Element.S4)
    case Modifier.U4   => Some(Element.U4)
    case Modifier.B1   => Some(Element.B1)
    case Modifier.S32  => Some(Element.S32)
    case Modifier.E4m3 => Some(Element.E4M3)
    case Modifier.E5m2 => Some(Element.E5M2)
    case Modifier.E3m2 => Some(Element.E3M2)
    case Modifier.E2m3 => Some(Element.E2M3)
    case Modifier.E2m1 => Some(if (packedE2M1) Element.E2M1 else Element.E2M1Byte)
    case _             => None
  }

  private def shape(value: Modifier): Option[Shape] = value match {
    case Modifier.M8n8k4    => Some(Shape(8, 8, 4))
    case Modifier.M8n8k16   => Some(Shape(8, 8, 16))
    case Modifier.M8n8k32   => Some(Shape(8, 8, 32))
    case Modifier.M8n8k128  => Some(Shape(8, 8, 128))
    case Modifier.M16n8k4   => Some(Shape(16, 8, 4))
    case Modifier.M16n8k8   => Some(Shape(16, 8, 8))
    case Modifier.M16n8k16  => Some(Shape(16, 8, 16))
    case Modifier.M16n8k32  => Some(Shape(16, 8, 32))
    case Modifier.M16n8k64  => Some(Shape(16, 8, 64))
    case Modifier.M16n8k128 => Some(Shape(16, 8, 128))
    case Modifier.M16n8k256 => Some(Shape(16, 8, 256))
    case Modifier.M16n16k16 => Some(Shape(16, 16, 16))
    case Modifier.M8n32k16  => Some(Shape(8, 32, 16))
    case Modifier.M32n8k16  => Some(Shape(32, 8, 16))
    case Modifier.M16n16k8  => Some(Shape(16, 16, 8))
    case _                  => None
  }

  private def rounding(value: Modifier): Option[ConversionRounding] = value match {
    case Modifier.Rn => Some(ConversionRounding.RN)
    case Modifier.Rz => Some(ConversionRounding.RZ)
    case Modifier.Rm => Some(ConversionRounding.RM)
    case Modifier.Rp => Some(ConversionRounding.RP)
    case _           => None
  }

  private def memorySpace(value: Option[Modifier]): Option[MemorySpace] = value match {
    case None                                             => Some(MemorySpace.Generic)
    case Some(Modifier.Global)                            => Some(MemorySpace.Global)
    case Some(Modifier.Shared) | Some(Modifier.SharedCta) => Some(MemorySpace.Shared)
    case _                                                => None
  }

  private def tensorModifiers(instruction: Instruction): Vec[Modifier] = instruction.form match {
    case _: Form.WmmaMma | _: Form.WmmaLoad | _: Form.WmmaStore | _: Form.MmaSp |
        _: Form.MmaSpOrderedMetadata =>
      instruction.fixedModifiers.drop(1) ++ instruction.mods
    case _ => instruction.fixedModifiers ++ instruction.mods
  }

  /** The qualifiers of a matrix product, in the order PTX writes them: `.sync.aligned`, the shape and layouts (a
    * single-bit `wmma` names its operation first), `.kind` and block scaling, `.satfinite`, the element types D, A, B,
    * C (D and C alone for half-precision `wmma`), the operation of a single-bit `mma`, the scale type and a rounding.
    */
  private def product(instruction: Instruction, modifiers: Vec[Modifier]): Option[Product] = {
    val (wmma, sparse) = instruction.form match {
      case _: Form.WmmaMma                              => (true, false)
      case _: Form.MmaSp | _: Form.MmaSpOrderedMetadata => (false, true)
      case _                                            => (false, false)
    }
    val bits = modifiers.collectFirst {
      case Modifier.And => BitOperation.And
      case Modifier.Xor => BitOperation.Xor
    }
    val rest = modifiers.filterNot(token => token == Modifier.And || token == Modifier.Xor || token == Modifier.Popc)
    val kind = rest.collectFirst {
      case token @ (Modifier.KindF8f6f4 | Modifier.KindMxf8f6f4 | Modifier.KindMxf4 | Modifier.KindMxf4nvf4) => token
    }
    val packed     = kind.exists(token => token == Modifier.KindMxf4 || token == Modifier.KindMxf4nvf4)
    val blockScale = rest.contains(Modifier.BlockScale)
    val vector     = rest.collectFirst {
      case Modifier.ScaleVec1X => 1: Int in (1, 2, 4)
      case Modifier.ScaleVec2X => 2: Int in (1, 2, 4)
      case Modifier.ScaleVec4X => 4: Int in (1, 2, 4)
    }.orElse(kind.collect {
      case Modifier.KindMxf8f6f4 => 1: Int in (1, 2, 4)
      case Modifier.KindMxf4     => 2: Int in (1, 2, 4)
    })
    val format = rest.collectFirst {
      case Modifier.Ue8m0 => ScaleFormat.UE8M0
      case Modifier.Ue4m3 => ScaleFormat.UE4M3
    }
    val layouts = rest.flatMap(columnMajor(_))
    val types   = rest.flatMap(element(_, packed))
    val shapes  = rest.flatMap(shape(_))
    val known   = rest.forall(token =>
      columnMajor(token).nonEmpty || element(token).nonEmpty || shape(token).nonEmpty || rounding(token).nonEmpty ||
        PartialFunction.cond(token) {
          case Modifier.Sync | Modifier.Aligned | Modifier.Satfinite | Modifier.BlockScale | Modifier.ScaleVec1X |
              Modifier.ScaleVec2X | Modifier.ScaleVec4X | Modifier.Ue8m0 | Modifier.Ue4m3 => true
        } || kind.contains(token)
    )
    val elements = if (wmma && types.size == 2) Some(Vec(types(0), Element.F16, Element.F16, types(1)))
    else
      Option.when(types.size == 4)(types)
    def tokens(select: Modifier => Boolean): Vec[Modifier] = modifiers.filter(select)
    val shaped                                             = tokens(shape(_).nonEmpty)
    val laid                                               = tokens(columnMajor(_).nonEmpty)
    val typed                                              = tokens(element(_).nonEmpty)
    val bitwise  = tokens(token => token == Modifier.And || token == Modifier.Xor || token == Modifier.Popc)
    val rounded  = tokens(rounding(_).nonEmpty)
    val saturate = tokens(_ == Modifier.Satfinite)
    val scaled   = tokens(PartialFunction.cond(_) {
      case Modifier.KindF8f6f4 | Modifier.KindMxf8f6f4 | Modifier.KindMxf4 | Modifier.KindMxf4nvf4 |
          Modifier.BlockScale | Modifier.ScaleVec1X | Modifier.ScaleVec2X | Modifier.ScaleVec4X => true
    })
    val scaleType = tokens(token => token == Modifier.Ue8m0 || token == Modifier.Ue4m3)
    val sync      = Vec(Modifier.Sync, Modifier.Aligned)
    val ordered   =
      if (wmma)
        Vec(
          bitwise ++ sync ++ laid ++ shaped ++ rounded ++ typed ++ saturate,
          bitwise ++ sync ++ laid ++ shaped ++ typed ++ rounded
        )
      else
        Vec(shaped ++ laid, laid ++ shaped).map(prefix =>
          sync ++ prefix ++ scaled ++ saturate ++ typed ++ bitwise ++ scaleType ++ rounded
        )
    for {
      _      <- Option.when(ordered.contains(modifiers))(())
      _      <- Option.when(known && layouts.size == 2)(())
      _      <- Option.when(blockScale == format.nonEmpty && blockScale == vector.nonEmpty)(())
      _      <- Option.when(bits.isEmpty || modifiers.contains(Modifier.Popc))(())
      matrix <- shapes.headOption.filter(_ => shapes.size == 1)
      found  <- elements
    } yield Product(
      wmma = wmma,
      sparse = sparse,
      shape = matrix,
      aColumnMajor = layouts(0),
      bColumnMajor = layouts(1),
      d = found(0),
      a = found(1),
      b = found(2),
      c = found(3),
      saturate = rest.contains(Modifier.Satfinite),
      rounding = rest.flatMap(rounding(_)).headOption.getOrElse(ConversionRounding.RN),
      bits = bits,
      scale = format.zip(vector).map((format, vector) => (format = format, vector = vector))
    )
  }

  private def wmmaMemoryShape(value: Modifier): Option[Shape] = value match {
    case Modifier.M16n16k16 | Modifier.M8n32k16 | Modifier.M32n8k16 | Modifier.M16n16k8 | Modifier.M8n8k4 |
        Modifier.M8n8k32 | Modifier.M8n8k128 => shape(value)
    case _ => None
  }

  private def wmmaMemoryForm(operation: Instruction, modifiers: Vec[Modifier]): Option[WmmaMemoryForm] = {
    val memoryOperation = operation.form match {
      case _: Form.WmmaLoad  => Some(MemoryOperation.Load)
      case _: Form.WmmaStore => Some(MemoryOperation.Store)
      case _                 => None
    }
    memoryOperation.flatMap { access =>
      val parsed = modifiers match {
        case Vec(r, Modifier.Sync, Modifier.Aligned, l, s, e) =>
          for {
            matrixRole    <- role(r)
            matrixLayout  <- columnMajor(l)
            matrixShape   <- wmmaMemoryShape(s)
            matrixElement <- element(e)
          } yield WmmaMemoryForm(
            access,
            matrixRole,
            matrixLayout,
            matrixShape,
            MemorySpace.Generic,
            matrixElement,
            r == Modifier.D
          )
        case Vec(r, Modifier.Sync, Modifier.Aligned, l, s, spaceToken, e) =>
          for {
            matrixRole    <- role(r)
            matrixLayout  <- columnMajor(l)
            matrixShape   <- wmmaMemoryShape(s)
            memory        <- memorySpace(Some(spaceToken))
            matrixElement <- element(e)
          } yield WmmaMemoryForm(
            access,
            matrixRole,
            matrixLayout,
            matrixShape,
            memory,
            matrixElement,
            r == Modifier.D
          )
        case _ => None
      }
      parsed
    }
  }

  private def loads(instruction: Instruction): Boolean = instruction.form match {
    case Form.Ldmatrix(_, _) => true
    case _                   => false
  }

  /** The tile `ldmatrix` or `stmatrix` moves, with its state space. */
  private def tile(operation: Instruction, modifiers: Vec[Modifier]): Option[(tile: Tile, space: MemorySpace)] = {
    val load  = loads(operation)
    val count = modifiers.collectFirst {
      case Modifier.X1 => 1: Int in (1, 2, 4)
      case Modifier.X2 => 2: Int in (1, 2, 4)
      case Modifier.X4 => 4: Int in (1, 2, 4)
    }
    val transpose = modifiers.contains(Modifier.Trans)
    val spaces    = modifiers.filter(token => token == Modifier.Shared || token == Modifier.SharedCta)
    val format    = modifiers.filter(PartialFunction.cond(_) {
      case Modifier.B16 | Modifier.B8 | Modifier.B8x16 | Modifier.B6x16P32 | Modifier.B4x16P64 => true
    })
    val shape = (modifiers.lift(2), format) match {
      case (Some(Modifier.M8n8), Vec(Modifier.B16))                       => Some((TileShape.M8N8, Packing.Bytes))
      case (Some(Modifier.M16n16), Vec(Modifier.B8)) if load && transpose => Some((TileShape.M16N16, Packing.Bytes))
      case (Some(Modifier.M16n16), Vec(Modifier.B8x16, Modifier.B6x16P32)) if load && transpose =>
        Some((TileShape.M16N16, Packing.Six))
      case (Some(Modifier.M16n16), Vec(Modifier.B8x16, Modifier.B4x16P64)) if load && transpose =>
        Some((TileShape.M16N16, Packing.Four))
      case (Some(Modifier.M8n16), Vec(Modifier.B8x16, Modifier.B6x16P32)) if load && !transpose =>
        Some((TileShape.M8N16, Packing.Six))
      case (Some(Modifier.M8n16), Vec(Modifier.B8x16, Modifier.B4x16P64)) if load && !transpose =>
        Some((TileShape.M8N16, Packing.Four))
      case (Some(Modifier.M16n8), Vec(Modifier.B8)) if !load && transpose => Some((TileShape.M16N8, Packing.Bytes))
      case _                                                              => None
    }
    val canonical = Vec(Modifier.Sync, Modifier.Aligned) ++ modifiers.slice(2, 4) ++
      (if (transpose) Vec(Modifier.Trans) else Vec.empty[Modifier]) ++ spaces ++ format
    for {
      _                 <- Option.when(modifiers == canonical)(())
      number            <- count.filter(number => !(shape.exists(_._1 == TileShape.M16N16) && number == 4))
      (matrix, packing) <- shape
      space             <- memorySpace(spaces.headOption).filter(_ => spaces.size <= 1)
    } yield (tile = Tile(matrix, number, transpose, packing), space = space)
  }

  private def check(condition: Boolean, message: NonEmpty[String]): Either[Error, Unit] =
    Either.cond(condition, (), Error.Invalid(message))
  private def count(actual: Int, expected: Int >= 0): Either[Error, Unit] =
    check(actual == expected.raw, s"tensor fragment requires $expected registers, found $actual")

  private def fragmentRegisters(operand: Operand)(using scope: Scope): Either[Error, Vec[Register]] =
    check(Operand.isVectorLiteral(operand), "tensor fragments require brace-enclosed register vectors")
      .flatMap(_ => scope.registers(operand))

  private def fragmentValues(operand: Operand)(using Scope): Either[Error, Vec[Value]] =
    fragmentRegisters(operand).map(_.map(Value.Register(_)))

  private def memoryFragment(s: Shape, role: Role, e: Element): Either[Error, Fragment] = {
    val valid =
      if (s == Shape(8, 8, 4)) e == Element.F64
      else if (s == Shape(16, 16, 8)) if (role == Role.C) e == Element.F32 else e == Element.TF32
      else if (s == Shape(8, 8, 32))
        if (role == Role.C) e == Element.S32 else PartialFunction.cond(e) { case Element.S4 | Element.U4 => true }
      else if (s == Shape(8, 8, 128)) if (role == Role.C) e == Element.S32 else e == Element.B1
      else if (role == Role.C) PartialFunction.cond(e) { case Element.F16 | Element.F32 | Element.S32 => true }
      else PartialFunction.cond(e) { case Element.F16 | Element.BF16 | Element.S8 | Element.U8 => true }
    check(valid, s"unsupported WMMA fragment $s/$role/$e").map(_ => Fragment(s, role, e, wmma = true))
  }

  private def lowerWmmaMemory(
      form: WmmaMemoryForm,
      operands: Vec[Operand]
  )(using scope: Scope): Either[Error, Operation] =
    for {
      _ <- check(
        operands.size == 2 || operands.size == 3,
        "invalid WMMA load/store operand count"
      )
      _ <- check(
        if (form.operation == MemoryOperation.Load) !form.dRole else form.dRole,
        "invalid WMMA load/store matrix role"
      )
      fragment <- memoryFragment(form.shape, form.role, form.element)
      column = form.layout
      address <- scope.address(operands(if (form.operation == MemoryOperation.Load) 1 else 0))
      stride  <- operands.lift(2).traverse(scope.value(_)).map(_.getOrElse(
        Value.Immediate((if (column) fragment.rows else fragment.columns).toLong)
      ))
      result <- if (form.operation == MemoryOperation.Load) for {
        destinations <- fragmentRegisters(operands(0))
        _            <- count(destinations.size, fragment.registers)
      } yield Operation.Load(fragment, destinations, address, stride, column, form.space)
      else for {
        sources <- fragmentValues(operands(1))
        _       <- count(sources.size, fragment.registers)
      } yield Operation.Store(fragment, sources, address, stride, column, form.space)
    } yield result

  /** The two values of a block-scale selector, `{byte, thread}`. */
  private def selector(operand: Operand)(using scope: Scope): Either[Error, (Value, Value)] =
    scope.values(operand).flatMap {
      case Vec(byte, thread) => Right((byte, thread))
      case _                 => Left(Error.Invalid("block-scale selectors are {byte, thread} pairs"))
    }

  private def lowerMultiply(
      form: Product,
      operands: Vec[Operand],
      qualifiedOpcode: NonEmpty[String]
  )(using scope: Scope): Either[Error, Operation] = {
    val fragments = form.fragments
    val d         = fragments(0)
    val a         = fragments(1)
    val b         = fragments(2)
    val c         = fragments(3)
    val extra     = if (form.sparse) 2 else 0
    val integer   = a.element.integer
    for {
      _ <- check(
        operands.size == 4 + extra + (if (form.scale.nonEmpty) 4 else 0),
        s"invalid operand count for $qualifiedOpcode"
      )
      _ <- check(
        Matrix.supportsMultiply(
          form.shape,
          a = a.element,
          b = b.element,
          c = c.element,
          d = d.element,
          aColumnMajor = form.aColumnMajor,
          bColumnMajor = form.bColumnMajor,
          wmma = form.wmma,
          sparse = form.sparse
        ),
        s"unsupported tensor type, shape or layout: $qualifiedOpcode"
      )
      _ <- check(!form.saturate || integer, "satfinite is supported only for integer tensor operations")
      _ <- check((a.element == Element.B1) == form.bits.nonEmpty, "single-bit tensor operations need .and or .xor")
      _ <- check(
        form.scale.isEmpty || a.element.narrow,
        "block scaling is supported only for narrow floating-point operands"
      )
      destinations <- fragmentRegisters(operands(0))
      left         <- fragmentValues(operands(1))
      right        <- fragmentValues(operands(2))
      addend       <- fragmentValues(operands(3))
      _            <- count(destinations.size, d.registers)
      _            <- count(left.size, a.registers)
      _            <- count(right.size, b.registers)
      _            <- count(addend.size, c.registers)
      sparsity     <-
        if (form.sparse) (scope.value(operands(4)), scope.value(operands(5))).mapN(Sparsity(_, _)).map(Some(_))
        else Right(None)
      scaling <- form.scale.traverse { scale =>
        (scope.value(operands(4 + extra)), selector(operands(5 + extra)), scope.value(operands(6 + extra)))
          .mapN((aData, aSelector, bData) => (aData, aSelector, bData))
          .flatMap((aData, aSelector, bData) =>
            selector(operands(7 + extra)).map(bSelector =>
              Scaling(scale.format, scale.vector, aData, aSelector._1, aSelector._2, bData, bSelector._1, bSelector._2)
            )
          )
      }
    } yield Operation.Multiply(
      a,
      b,
      c,
      d,
      destinations,
      left,
      right,
      addend,
      Matrix.accumulation(
        scope.architecture,
        form.shape,
        a.element,
        if (integer || a.element == Element.F64) d.element else Matrix.accumulator(c.element, d.element),
        form.rounding
      ),
      form.saturate,
      form.bits,
      sparsity,
      scaling
    )
  }

  /** The registers each operand of a tensor instruction holds as a fragment, or `None` for an operand that is not one;
    * `None` for an instruction the VM does not run as a tensor operation.
    */
  def fragments(instruction: Instruction): Option[Vec[Option[Int]]] = {
    val modifiers = tensorModifiers(instruction)
    instruction.form match {
      case _: Form.WmmaLoad | _: Form.WmmaStore =>
        wmmaMemoryForm(instruction, modifiers).flatMap(form =>
          memoryFragment(form.shape, form.role, form.element).toOption.map { fragment =>
            val held = Some(fragment.registers.raw)
            if (form.operation == MemoryOperation.Load) Vec(held, None, None) else Vec(None, held, None)
          }
        )
      case _: Form.Mma | _: Form.WmmaMma | _: Form.MmaSp | _: Form.MmaSpOrderedMetadata =>
        product(instruction, modifiers).map { form =>
          val held     = form.fragments.map(fragment => Some(fragment.registers.raw))
          val sparsity = if (form.sparse) Vec(None, None) else Vec.empty[Option[Int]]
          val scaling  = if (form.scale.nonEmpty) Vec(None, Some(2), None, Some(2)) else Vec.empty[Option[Int]]
          held ++ sparsity ++ scaling
        }
      case Form.Ldmatrix(_, _) | Form.Stmatrix(_, _) =>
        tile(instruction, modifiers).map { (tile, _) =>
          val held = Some(tile.count * tile.registers)
          if (loads(instruction)) Vec(held, None) else Vec(None, held)
        }
      case _ => None
    }
  }

  private def lowerTile(
      operation: Instruction,
      form: (tile: Tile, space: MemorySpace),
      operands: Vec[Operand]
  )(using scope: Scope): Either[Error, Operation] = {
    val registers = form.tile.count * form.tile.registers
    check(operands.size == 2, "invalid matrix memory operand count").flatMap { _ =>
      if (loads(operation)) for {
        destinations <- fragmentRegisters(operands(0))
        _            <- check(destinations.size == registers, s"matrix load requires $registers registers")
        address      <- scope.address(operands(1))
      } yield Operation.LoadMatrix(form.tile, destinations, address, form.space)
      else for {
        address <- scope.address(operands(0))
        sources <- fragmentValues(operands(1))
        _       <- check(sources.size == registers, s"matrix store requires $registers registers")
      } yield Operation.StoreMatrix(form.tile, sources, address, form.space)
    }
  }

  def lower(instruction: Instruction)(using Scope): Either[Error, VMIR.Instruction] = {
    val operands                         = instruction.operands
    val qualifiedOpcode                  = instruction.qualifiedOpcode
    val result: Either[Error, Operation] = instruction.form match {
      case _: Form.WmmaLoad | _: Form.WmmaStore =>
        wmmaMemoryForm(instruction, tensorModifiers(instruction))
          .toRight(Error.Unsupported(qualifiedOpcode, "unsupported WMMA memory qualifiers"))
          .flatMap(form => lowerWmmaMemory(form, operands))
      case _: Form.Mma | _: Form.WmmaMma | _: Form.MmaSp | _: Form.MmaSpOrderedMetadata =>
        product(instruction, tensorModifiers(instruction))
          .toRight(Error.Unsupported(qualifiedOpcode, "unsupported tensor instruction qualifiers"))
          .flatMap(form => lowerMultiply(form, operands, qualifiedOpcode))
      case Form.Ldmatrix(_, _) | Form.Stmatrix(_, _) =>
        tile(instruction, tensorModifiers(instruction))
          .toRight(Error.Unsupported(qualifiedOpcode, "unsupported matrix memory qualifiers"))
          .flatMap(form => lowerTile(instruction, form, operands))
      case Form.Movmatrix(destination, source) =>
        for {
          _ <- check(
            instruction.mods == Vec(Modifier.Sync, Modifier.Aligned, Modifier.M8n8, Modifier.Trans, Modifier.B16),
            "unsupported matrix transpose qualifiers"
          )
          target <- summon[Scope].register(destination)
          value  <- summon[Scope].value(source)
        } yield Operation.Transpose(target, value)
      case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "unsupported tensor instruction qualifiers"))
    }
    result.map(VMIR.Instruction.Tensor(_))
  }
}
