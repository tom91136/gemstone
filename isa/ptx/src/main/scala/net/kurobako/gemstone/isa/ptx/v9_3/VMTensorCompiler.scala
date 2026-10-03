package net.kurobako.gemstone.isa.ptx.v9_3

import VMCompiler.*
import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.{Instruction as _, *}
import net.kurobako.gemstone.isa.nvidia.VMTensor.*

private object VMTensorCompiler {
  private enum MultiplyFamily {
    case Mma
    case Wmma
  }

  private enum Layout {
    case Row
    case Column
  }

  private enum MemoryOperation {
    case Load
    case Store
    case LoadMatrix
    case StoreMatrix
  }

  private case class MultiplyForm(
      family: MultiplyFamily,
      aLayout: Layout,
      bLayout: Layout,
      shape: Shape,
      qualifiers: Vec[Modifier]
  )

  private case class WmmaMemoryForm(
      operation: MemoryOperation,
      role: Role,
      layout: Layout,
      shape: Shape,
      space: Space,
      element: Element,
      dRole: Boolean
  )

  private case class MatrixMemoryForm(
      operation: MemoryOperation,
      count: Int in (1, 2, 4),
      transpose: Boolean,
      space: Space
  )

  private def layout(value: Modifier): Option[Layout] = value match {
    case Modifier.Row => Some(Layout.Row)
    case Modifier.Col => Some(Layout.Column)
    case _            => None
  }

  private def role(value: Modifier): Option[Role] = value match {
    case Modifier.A              => Some(Role.A)
    case Modifier.B              => Some(Role.B)
    case Modifier.C | Modifier.D => Some(Role.C)
    case _                       => None
  }

  private def element(value: Modifier): Option[Element] = value match {
    case Modifier.F16  => Some(Element.F16)
    case Modifier.Bf16 => Some(Element.BF16)
    case Modifier.Tf32 => Some(Element.TF32)
    case Modifier.F32  => Some(Element.F32)
    case Modifier.F64  => Some(Element.F64)
    case Modifier.S8   => Some(Element.S8)
    case Modifier.U8   => Some(Element.U8)
    case Modifier.S32  => Some(Element.S32)
    case _             => None
  }

  private def shape(value: Modifier): Option[Shape] = value match {
    case Modifier.M8n8k4    => Some(Shape(8, 8, 4))
    case Modifier.M16n8k4   => Some(Shape(16, 8, 4))
    case Modifier.M16n8k8   => Some(Shape(16, 8, 8))
    case Modifier.M16n8k16  => Some(Shape(16, 8, 16))
    case Modifier.M16n8k32  => Some(Shape(16, 8, 32))
    case Modifier.M16n8k64  => Some(Shape(16, 8, 64))
    case Modifier.M8n8k32   => Some(Shape(8, 8, 32))
    case Modifier.M8n8k128  => Some(Shape(8, 8, 128))
    case Modifier.M16n8k128 => Some(Shape(16, 8, 128))
    case Modifier.M16n8k256 => Some(Shape(16, 8, 256))
    case Modifier.M16n16k16 => Some(Shape(16, 16, 16))
    case Modifier.M8n32k16  => Some(Shape(8, 32, 16))
    case Modifier.M32n8k16  => Some(Shape(32, 8, 16))
    case Modifier.M16n16k8  => Some(Shape(16, 16, 8))
    case _                  => None
  }

  private def wmmaMemoryShape(value: Modifier): Option[Shape] = value match {
    case Modifier.M16n16k16 | Modifier.M8n32k16 | Modifier.M32n8k16 |
        Modifier.M16n16k8 | Modifier.M8n8k4 => shape(value)
    case _ => None
  }

  private def memorySpace(value: Option[Modifier]): Option[Space] = value match {
    case None                                             => Some(Space.Generic)
    case Some(Modifier.Global)                            => Some(Space.Global)
    case Some(Modifier.Shared) | Some(Modifier.SharedCta) => Some(Space.Shared)
    case _                                                => None
  }

  private def tensorModifiers(instruction: Instruction): Vec[Modifier] = instruction match {
    case _: Instruction.WmmaMma | _: Instruction.WmmaLoad | _: Instruction.WmmaStore =>
      instruction.fixedModifiers.drop(1) ++ instruction.mods
    case _ => instruction.fixedModifiers ++ instruction.mods
  }

  private def multiplyForm(operation: Instruction, modifiers: Vec[Modifier]): Option[MultiplyForm] = {
    val parsed = operation match {
      case _: Instruction.Mma => modifiers match {
          case Vec(Modifier.Sync, Modifier.Aligned, x, y, z, qualifiers*) =>
            val ordinary = for {
              aLayout <- layout(x)
              bLayout <- layout(y)
              matrix  <- shape(z)
            } yield MultiplyForm(MultiplyFamily.Mma, aLayout, bLayout, matrix, qualifiers.toVec)
            ordinary.orElse(for {
              matrix  <- shape(x)
              aLayout <- layout(y)
              bLayout <- layout(z)
            } yield MultiplyForm(MultiplyFamily.Mma, aLayout, bLayout, matrix, qualifiers.toVec))
          case _ => None
        }
      case _: Instruction.WmmaMma => modifiers match {
          case Vec(Modifier.Sync, Modifier.Aligned, a, b, s, qualifiers*) =>
            for {
              aLayout <- layout(a)
              bLayout <- layout(b)
              matrix  <- shape(s)
            } yield MultiplyForm(MultiplyFamily.Wmma, aLayout, bLayout, matrix, qualifiers.toVec)
          case _ => None
        }
      case _ => None
    }
    parsed
  }

  private def wmmaMemoryForm(operation: Instruction, modifiers: Vec[Modifier]): Option[WmmaMemoryForm] = {
    val memoryOperation = operation match {
      case _: Instruction.WmmaLoad  => Some(MemoryOperation.Load)
      case _: Instruction.WmmaStore => Some(MemoryOperation.Store)
      case _                        => None
    }
    memoryOperation.flatMap { access =>
      val parsed = modifiers match {
        case Vec(r, Modifier.Sync, Modifier.Aligned, l, s, e) =>
          for {
            matrixRole    <- role(r)
            matrixLayout  <- layout(l)
            matrixShape   <- wmmaMemoryShape(s)
            matrixElement <- element(e)
          } yield WmmaMemoryForm(
            access,
            matrixRole,
            matrixLayout,
            matrixShape,
            Space.Generic,
            matrixElement,
            r == Modifier.D
          )
        case Vec(r, Modifier.Sync, Modifier.Aligned, l, s, spaceToken, e) =>
          for {
            matrixRole    <- role(r)
            matrixLayout  <- layout(l)
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

  private def matrixMemoryForm(operation: Instruction, modifiers: Vec[Modifier]): Option[MatrixMemoryForm] = {
    val access = operation match {
      case _: Instruction.Ldmatrix => Some(MemoryOperation.LoadMatrix)
      case _: Instruction.Stmatrix => Some(MemoryOperation.StoreMatrix)
      case _                       => None
    }
    access.flatMap { matrixOperation =>
      modifiers match {
        case Vec(Modifier.Sync, Modifier.Aligned, Modifier.M8n8, count, tail*) =>
          val registers: Option[Int in (1, 2, 4)] = count match {
            case Modifier.X1 => Some(1: Int in (1, 2, 4))
            case Modifier.X2 => Some(2: Int in (1, 2, 4))
            case Modifier.X4 => Some(4: Int in (1, 2, 4))
            case _           => None
          }
          val suffix = tail.toVec match {
            case Vec(Modifier.B16)                 => Some((false, Space.Generic))
            case Vec(Modifier.Trans, Modifier.B16) => Some((true, Space.Generic))
            case Vec(Modifier.Shared, Modifier.B16) | Vec(Modifier.SharedCta, Modifier.B16) =>
              Some((false, Space.Shared))
            case Vec(Modifier.Trans, Modifier.Shared, Modifier.B16) |
                Vec(Modifier.Trans, Modifier.SharedCta, Modifier.B16) => Some((true, Space.Shared))
            case _ => None
          }
          for {
            number             <- registers
            (transpose, space) <- suffix
          } yield MatrixMemoryForm(matrixOperation, number, transpose, space)
        case _ => None
      }
    }
  }

  private def check(condition: Boolean, message: NonEmpty[String]): Either[Error, Unit] =
    Either.cond(condition, (), Error.Invalid(message))
  private def count(actual: Int, expected: Int >= 0): Either[Error, Unit] =
    check(actual == expected.raw, s"tensor fragment requires $expected registers, found $actual")
  private def countMatrix(actual: Int, expected: Int in (1, 2, 4)): Either[Error, Unit] =
    check(actual == expected.raw, s"matrix operation requires ${expected.raw} registers, found $actual")

  private def fragmentRegisters(operand: Operand) =
    check(Operand.isVectorLiteral(operand), "tensor fragments require brace-enclosed register vectors")
      .flatMap(_ => registerNames(operand))

  private def fragmentValues(operand: Operand) = fragmentRegisters(operand).map(_.map(Value.Register(_)))

  private def memoryFragment(s: Shape, role: Role, e: Element): Either[Error, Fragment] = {
    val valid =
      if (s == Shape(8, 8, 4)) e == Element.F64
      else if (s == Shape(16, 16, 8)) if (role == Role.C) e == Element.F32 else e == Element.TF32
      else if (role == Role.C) Set(Element.F16, Element.F32, Element.S32)(e)
      else Set(Element.F16, Element.BF16, Element.S8, Element.U8)(e)
    check(valid, s"unsupported WMMA fragment $s/$role/$e").map(_ => Fragment(s, role, e, true))
  }

  private def lowerWmmaMemory(
      form: WmmaMemoryForm,
      operands: Vec[Operand]
  ): Either[Error, Operation] =
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
      column = form.layout == Layout.Column
      address <- addressValue(operands(if (form.operation == MemoryOperation.Load) 1 else 0))
      stride  <- operands.lift(2).traverse(value(_)).map(_.getOrElse(
        Value.Immediate((if (column) fragment.rows.raw else fragment.columns.raw).toLong)
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

  private def lowerMultiply(
      form: MultiplyForm,
      operands: Vec[Operand],
      qualifiedOpcode: NonEmpty[String]
  ): Either[Error, Operation] = {
    val wmma      = form.family == MultiplyFamily.Wmma
    val tokens    = form.qualifiers
    val saturated = tokens.contains(Modifier.Satfinite)
    val types     = tokens.filterNot(token => token == Modifier.Satfinite || token == Modifier.Rn)
    val expanded  = if (wmma && types.size == 2)
      Vec(types.head, Modifier.F16, Modifier.F16, types.last)
    else types
    val supported = expanded.traverse(element(_))
    for {
      _ <- check(operands.size == 4 && supported.exists(_.size == 4), s"unsupported tensor types: $qualifiedOpcode")
      elements = supported.get
      d        = Fragment(form.shape, Role.C, elements(0), wmma)
      a        = Fragment(form.shape, Role.A, elements(1), wmma, form.aLayout == Layout.Column)
      b        = Fragment(form.shape, Role.B, elements(2), wmma, form.bLayout == Layout.Column)
      c        = Fragment(form.shape, Role.C, elements(3), wmma)
      integer  = Set(Element.S8, Element.U8)(a.element) && Set(Element.S8, Element.U8)(b.element)
      _ <- check(!wmma || !integer || a.element == b.element, "WMMA integer input types must match")
      _ <- check(
        if (integer) c.element == Element.S32 && d.element == Element.S32
        else if (a.element == Element.F16) b.element == Element.F16 && Set(Element.F16, Element.F32)(c.element) &&
        Set(Element.F16, Element.F32)(d.element)
        else if (a.element == Element.BF16 || a.element == Element.TF32) b.element == a.element &&
        c.element == Element.F32 && d.element == Element.F32
        else a.element == Element.F64 && b.element == Element.F64 && c.element == Element.F64 &&
        d.element == Element.F64,
        s"unsupported tensor type combination: $qualifiedOpcode"
      )
      _ <- check(wmma || c.element == d.element, "MMA accumulator input and output types must match")
      expectedQualifiers =
        if (saturated) {
          if (wmma) types :+ Modifier.Satfinite else Modifier.Satfinite +: types
        } else if (tokens.contains(Modifier.Rn) && wmma) Modifier.Rn +: types
        else types
      _ <- check(tokens == expectedQualifiers, "invalid tensor qualifier order or repetition")
      _ <- check(!saturated || integer, "satfinite is supported only for integer tensor operations")
      _ <- check(!tokens.contains(Modifier.Rn) || a.element == Element.F64, "tensor rounding qualifier requires f64")
      _ <- check(
        if (wmma) {
          if (a.element == Element.F64) form.shape == Shape(8, 8, 4)
          else if (a.element == Element.TF32) form.shape == Shape(16, 16, 8)
          else Set(Shape(16, 16, 16), Shape(8, 32, 16), Shape(32, 8, 16))(form.shape)
        } else if (form.shape == Shape(8, 8, 4) && a.element == Element.F16 && b.element == Element.F16) true
        else form.aLayout == Layout.Row && form.bLayout == Layout.Column && (
          if (a.element == Element.F64) form.shape == Shape(8, 8, 4) ||
          (form.shape.m.raw == 16 && form.shape.n.raw == 8 && Set(4, 8, 16)(form.shape.k.raw))
          else if (a.element == Element.TF32) form.shape.m.raw == 16 && form.shape.n.raw == 8 &&
          Set(4, 8)(form.shape.k.raw)
          else if (integer) form.shape.m.raw == 16 && form.shape.n.raw == 8 && Set(16, 32)(form.shape.k.raw)
          else form.shape.m.raw == 16 && form.shape.n.raw == 8 && Set(8, 16)(form.shape.k.raw)
        ),
        s"unsupported tensor shape/layout: $qualifiedOpcode"
      )
      destinations <- fragmentRegisters(operands(0))
      left         <- fragmentValues(operands(1))
      right        <- fragmentValues(operands(2))
      addend       <- fragmentValues(operands(3))
      _            <- count(destinations.size, d.registers)
      _            <- count(left.size, a.registers)
      _            <- count(right.size, b.registers)
      _            <- count(addend.size, c.registers)
    } yield Operation.Multiply(a, b, c, d, destinations, left, right, addend, saturated)
  }

  private def lowerMatrixMemory(
      form: MatrixMemoryForm,
      operands: Vec[Operand]
  ): Either[Error, Operation] = check(operands.size == 2, "invalid matrix memory operand count").flatMap { _ =>
    form.operation match {
      case MemoryOperation.LoadMatrix => for {
          destinations <- fragmentRegisters(operands(0))
          _            <- countMatrix(destinations.size, form.count)
          address      <- addressValue(operands(1))
        } yield Operation.LoadMatrix(destinations, address, form.transpose, form.space)
      case MemoryOperation.StoreMatrix => for {
          address <- addressValue(operands(0))
          sources <- fragmentValues(operands(1))
          _       <- countMatrix(sources.size, form.count)
        } yield Operation.StoreMatrix(sources, address, form.transpose, form.space)
      case _ => Left(Error.Invalid("invalid matrix memory operation"))
    }
  }

  def lower(instruction: Instruction): Either[Error, VMIR.Instruction] = {
    val operands                         = instruction.operands
    val qualifiedOpcode                  = instruction.qualifiedOpcode
    val result: Either[Error, Operation] = instruction match {
      case _: Instruction.WmmaLoad | _: Instruction.WmmaStore =>
        wmmaMemoryForm(instruction, tensorModifiers(instruction))
          .toRight(Error.Unsupported(qualifiedOpcode, "unsupported WMMA memory qualifiers"))
          .flatMap(form => lowerWmmaMemory(form, operands))
      case _: Instruction.Mma | _: Instruction.WmmaMma =>
        multiplyForm(instruction, tensorModifiers(instruction))
          .toRight(Error.Unsupported(qualifiedOpcode, "unsupported tensor instruction qualifiers"))
          .flatMap(form => lowerMultiply(form, operands, qualifiedOpcode))
      case _: Instruction.Ldmatrix | _: Instruction.Stmatrix =>
        matrixMemoryForm(instruction, tensorModifiers(instruction))
          .toRight(Error.Unsupported(qualifiedOpcode, "unsupported matrix memory qualifiers"))
          .flatMap(form => lowerMatrixMemory(form, operands))
      case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "unsupported tensor instruction qualifiers"))
    }
    result.map(VMIR.Instruction.Tensor(_, qualifiedOpcode))
  }
}
