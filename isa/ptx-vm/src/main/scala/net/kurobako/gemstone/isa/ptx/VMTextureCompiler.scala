package net.kurobako.gemstone.isa.ptx

import Lowering.*
import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Texture.*
import net.kurobako.gemstone.isa.nvidia.VMIR.{Instruction as _, *}
import net.kurobako.gemstone.texture.*
import net.kurobako.gemstone.vm.*

/** Lowers the texture and surface instructions to the NVIDIA VM's texture family. */
private object VMTextureCompiler {
  private type Lowered = Either[Error, VMIR.Instruction]

  private def texture(operation: Operation): Lowered = Right(VMIR.Instruction.Texture(operation))

  /** The handle and coordinates of a `[handle, {coordinates}]` operand, of which `axes` coordinates count. */
  private def target(operand: Operand, axes: Int)(using scope: Scope): Either[Error, (Value, Vec[Value])] =
    operand match {
      case Operand.Address(Vec(handle))              => scope.value(handle).map((_, Vec.empty[Value]))
      case Operand.Address(items) if items.size >= 2 =>
        val coordinates = items.last match {
          case Operand.VectorLiteral(components) => components
          case single                            => Vec(single)
        }
        for {
          handle <- scope.value(items.head)
          values <- coordinates.traverse(scope.value(_))
          _      <- Either.cond(values.size >= axes, (), Error.Invalid(s"expected $axes coordinates, found $values"))
        } yield (handle, values.take(axes))
      case other => invalid(s"expected a texture or surface operand, found $other")
    }

  private def axes(modifiers: Vec[Modifier]): Either[Error, Int] =
    modifiers.collectFirst {
      case Modifier.N1d => 1
      case Modifier.N2d => 2
      case Modifier.N3d => 3
    }.toRight(Error.Invalid("expected .1d, .2d or .3d"))

  private def element(modifier: Modifier): Option[Element] = modifier match {
    case Modifier.F32 => Some(Element.F32)
    case Modifier.S32 => Some(Element.S32)
    case Modifier.U32 => Some(Element.U32)
    case _            => None
  }

  private def boundary(modifiers: Vec[Modifier]): Either[Error, BoundaryMode] =
    modifiers.collectFirst {
      case Modifier.Trap  => BoundaryMode.Trap
      case Modifier.Clamp => BoundaryMode.Clamp
      case Modifier.Zero  => BoundaryMode.Zero
    }.toRight(Error.Invalid("expected .trap, .clamp or .zero"))

  private def bytes(modifiers: Vec[Modifier]): Either[Error, Int] =
    scalarWidth(modifiers).map(_.raw / 8)

  private def surfaceBytes(modifiers: Vec[Modifier]): Either[Error, Int in (1, 2, 4, 8)] =
    bytes(
      modifiers
    ).flatMap(_.refine[Int in (1, 2, 4, 8)].toOption.toRight(Error.Invalid("unsupported surface element width")))

  private def reduceBytes(modifiers: Vec[Modifier]): Either[Error, Int in (4, 8)] =
    bytes(modifiers).flatMap(_.refine[Int in (4, 8)].toOption.toRight(Error.Invalid("sured takes .b32 or .b64")))

  private def unsupported(instruction: Instruction, detail: String): Lowered =
    Left(Error.Unsupported(instruction.qualifiedOpcode, detail))

  private val properties: Map[Modifier, Property] = Map(
    Modifier.Width            -> Property.Width,
    Modifier.Height           -> Property.Height,
    Modifier.Depth            -> Property.Depth,
    Modifier.ChannelDataType  -> Property.ChannelDataType,
    Modifier.ChannelOrder     -> Property.ChannelOrder,
    Modifier.NormalizedCoords -> Property.NormalisedCoordinates
  )

  /** The lowering of `instruction`, or None when another family owns it. */
  def lower(instruction: Instruction, modifiers: Vec[Modifier])(using scope: Scope): Option[Lowered] =
    instruction.form match {
      case Form.Tex(dest, textureAndCoordinates, None, None, None, None) =>
        Some(for {
          dimensions <- axes(modifiers)
          types      = modifiers.toVector.flatMap(element(_))
          components = if (modifiers.contains(Modifier.V2)) 2 else 4
          _            <- Either.cond(types.size == 2, (), Error.Invalid("tex takes two types"))
          destinations <- scope.sinks(dest)
          _            <- Either.cond(
            destinations.size == components,
            (),
            Error.Invalid(s"tex takes $components destinations")
          )
          located <- target(textureAndCoordinates, dimensions)
          kind = if (modifiers.last == Modifier.F32) Coordinates.Float else Coordinates.Integer
          lowered <- texture(Operation.Sample(located._1, located._2, kind, types.head, destinations))
        } yield lowered)
      case Form.Tex(_, _, _, _, _, _) => Some(unsupported(instruction, "level, gradient, offset and compare forms"))
      case Form.Tld4(dest, textureAndCoordinates, None, None) =>
        Some(for {
          component <- modifiers.collectFirst {
            case Modifier.R => 0
            case Modifier.G => 1
            case Modifier.B => 2
            case Modifier.A => 3
          }.flatMap(_.refine[Int in 0 ~ 3].toOption).toRight(Error.Invalid("tld4 takes .r, .g, .b or .a"))
          _ <- Either.cond(modifiers.contains(Modifier.N2d), (), Error.Invalid("tld4 takes .2d"))
          types = modifiers.toVector.flatMap(element(_))
          destinations <- scope.sinks(dest)
          located      <- target(textureAndCoordinates, 2)
          lowered      <- texture(Operation.Gather(located._1, located._2, component, types.head, destinations))
        } yield lowered)
      case Form.Tld4(_, _, _, _)        => Some(unsupported(instruction, "offset and compare forms"))
      case Form.Txq(dest, handle, None) =>
        Some(for {
          property <- modifiers.collectFirst(Function.unlift(properties.get))
            .toRight(Error.Unsupported(instruction.qualifiedOpcode, "query"))
          target      <- handleOf(handle)
          destination <- scope.register(dest)
          lowered     <- texture(Operation.Query(target._1, surface = false, property, destination))
        } yield lowered)
      case Form.Suld(dest, address) =>
        Some(for {
          dimensions   <- axes(modifiers)
          width        <- surfaceBytes(modifiers)
          mode         <- boundary(modifiers)
          destinations <- scope.sinks(dest)
          located      <- target(address, dimensions)
          lowered      <- texture(Operation.Load(located._1, located._2, width, mode, destinations))
        } yield lowered)
      case Form.Sust(address, sources) if !modifiers.contains(Modifier.P) =>
        Some(for {
          dimensions <- axes(modifiers)
          width      <- surfaceBytes(modifiers)
          mode       <- boundary(modifiers)
          values     <- scope.values(sources)
          located    <- target(address, dimensions)
          lowered    <- texture(Operation.Store(located._1, located._2, width, mode, values))
        } yield lowered)
      case Form.Sust(_, _) => Some(unsupported(instruction, "formatted stores"))
      case Form.Sured(address, source) if !modifiers.contains(Modifier.P) =>
        Some(for {
          dimensions <- axes(modifiers)
          width      <- reduceBytes(modifiers)
          mode       <- boundary(modifiers)
          operation  <- modifiers.collectFirst {
            case Modifier.Add => AtomicOperation.Add
            case Modifier.Min => AtomicOperation.MinimumUnsigned
            case Modifier.Max => AtomicOperation.MaximumUnsigned
            case Modifier.And => AtomicOperation.And
            case Modifier.Or  => AtomicOperation.Or
          }.toRight(Error.Invalid("sured takes .add, .min, .max, .and or .or"))
          operand <- scope.value(source)
          located <- target(address, dimensions)
          lowered <- texture(Operation.Reduce(located._1, located._2, width, operation, mode, operand, None))
        } yield lowered)
      case Form.Sured(_, _) => Some(unsupported(instruction, "formatted reductions"))
      case _                => None
    }

  private def handleOf(operand: Operand)(using scope: Scope): Either[Error, (Value, Vec[Value])] =
    operand match {
      case Operand.Address(Vec(handle)) => scope.value(handle).map((_, Vec.empty[Value]))
      case other                        => invalid(s"expected a texture operand, found $other")
    }
}
