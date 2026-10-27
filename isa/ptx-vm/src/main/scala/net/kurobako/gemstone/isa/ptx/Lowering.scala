package net.kurobako.gemstone.isa.ptx

import cats.syntax.all.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.{Parameter as _, *}
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable

/** Errors, operand resolution and dense storage shared by the PTX instruction lowerings. */
private object Lowering {
  enum Extremum derives CanEqual { case Minimum, Maximum }

  enum Error {
    case Invalid(detail: NonEmpty[String])
    case Unsupported(instruction: NonEmpty[String], detail: String)

    def message: NonEmpty[String] = this match {
      case Invalid(detail)                  => detail
      case Unsupported(instruction, detail) => s"unsupported instruction: $instruction ($detail)"
    }
  }

  def invalid[A](detail: NonEmpty[String]): Either[Error, A] = Left(Error.Invalid(detail))

  def logicTable(table: Operand): Either[Error, Int in 0 ~ 255] =
    NumericLiteral.immediate(table).flatMap(_.toInt.refine[Int in 0 ~ 255].toOption)
      .toRight(Error.Invalid(s"lop3 requires an 8-bit immediate table, found $table"))

  def pair[A](values: Vec[A], detail: String): Either[Error, Vec[A]] =
    Either.cond(values.size == 2, values, Error.Invalid(s"$detail must contain two registers"))

  def scalarWidth(modifiers: Vec[Modifier]): Either[Error, ScalarBitWidth] =
    modifiers.reverse.view.collectFirst {
      case Modifier.B8 | Modifier.S8 | Modifier.U8                                   => 8: ScalarBitWidth
      case Modifier.B16 | Modifier.S16 | Modifier.U16 | Modifier.F16 | Modifier.Bf16 => 16: ScalarBitWidth
      case Modifier.B32 | Modifier.S32 | Modifier.U32 | Modifier.F32                 => 32: ScalarBitWidth
      case Modifier.B64 | Modifier.S64 | Modifier.U64 | Modifier.F64                 => 64: ScalarBitWidth
    }.toRight(Error.Invalid(s"expected an 8, 16, 32, or 64-bit type in ${modifiers.map(_.name.raw).mkString(".")}"))

  private val specialRegisters: Map[String, Value] = Map(
    "%tid.x"             -> Value.ThreadId(Axis.X),
    "%tid.y"             -> Value.ThreadId(Axis.Y),
    "%tid.z"             -> Value.ThreadId(Axis.Z),
    "%ctaid.x"           -> Value.BlockId(Axis.X),
    "%ctaid.y"           -> Value.BlockId(Axis.Y),
    "%ctaid.z"           -> Value.BlockId(Axis.Z),
    "%ntid.x"            -> Value.BlockDim(Axis.X),
    "%ntid.y"            -> Value.BlockDim(Axis.Y),
    "%ntid.z"            -> Value.BlockDim(Axis.Z),
    "%nctaid.x"          -> Value.GridDim(Axis.X),
    "%nctaid.y"          -> Value.GridDim(Axis.Y),
    "%nctaid.z"          -> Value.GridDim(Axis.Z),
    "%dynamic_smem_size" -> Value.DynamicSharedBytes,
    "%laneid"            -> Value.LaneId,
    "%lanemask_ge"       -> Value.LaneMaskGreaterEqual,
    "%lanemask_gt"       -> Value.LaneMaskGreater,
    "%lanemask_le"       -> Value.LaneMaskLessEqual,
    "%lanemask_lt"       -> Value.LaneMaskLess,
    "%lanemask_eq"       -> Value.LaneMaskEqual,
    "%warpid"            -> Value.WarpId,
    "%nwarpid"           -> Value.Immediate(EmulatedDevice.WarpsPerMultiprocessor.toLong),
    "%smid"              -> Value.MultiprocessorId,
    "%nsmid"             -> Value.Immediate(EmulatedDevice.Multiprocessors.toLong),
    "%clock"             -> Value.Clock(CounterPart.Low),
    "%clock_hi"          -> Value.Clock(CounterPart.High),
    "%clock64"           -> Value.Clock(CounterPart.Whole),
    "%globaltimer"       -> Value.GlobalTimer(CounterPart.Whole),
    "%globaltimer_lo"    -> Value.GlobalTimer(CounterPart.Low),
    "%globaltimer_hi"    -> Value.GlobalTimer(CounterPart.High),
    "%clusterid.x"       -> Value.ClusterId(Axis.X),
    "%clusterid.y"       -> Value.ClusterId(Axis.Y),
    "%clusterid.z"       -> Value.ClusterId(Axis.Z),
    "%nclusterid.x"      -> Value.ClusterCount(Axis.X),
    "%nclusterid.y"      -> Value.ClusterCount(Axis.Y),
    "%nclusterid.z"      -> Value.ClusterCount(Axis.Z),
    "%cluster_ctaid.x"   -> Value.ClusterCtaId(Axis.X),
    "%cluster_ctaid.y"   -> Value.ClusterCtaId(Axis.Y),
    "%cluster_ctaid.z"   -> Value.ClusterCtaId(Axis.Z),
    "%cluster_nctaid.x"  -> Value.ClusterShape(Axis.X),
    "%cluster_nctaid.y"  -> Value.ClusterShape(Axis.Y),
    "%cluster_nctaid.z"  -> Value.ClusterShape(Axis.Z),
    "%cluster_ctarank"   -> Value.ClusterCtaRank,
    "%cluster_nctarank"  -> Value.ClusterCtaCount,
    // No kernel runs as part of a device graph.
    "%current_graph_exec" -> Value.Immediate(0L)
  )

  /** Every name the PTX ISA predefines as a special register, so none reads as an undefined virtual register. */
  private val SpecialRegister =
    ("%(?:(?:n?tid|n?ctaid|n?clusterid|cluster_n?ctaid)(?:\\.[xyz])?|laneid|n?warpid|n?smid|gridid|is_explicit_cluster|" +
      "cluster_n?ctarank|lanemask_(?:eq|le|lt|ge|gt)|clock|clock_hi|clock64|pm[0-7](?:_64)?|envreg[0-9]+|" +
      "globaltimer(?:_lo|_hi)?|reserved_smem_offset_(?:begin|end|cap|[0-9]+)|total_smem_size|aggr_smem_size|" +
      "dynamic_smem_size|current_graph_exec)").r

  private val predefinedConstants: Map[String, Value] = Map("WARP_SZ" -> Value.Immediate(32))

  case class Access(address: Operand, value: Operand, modifiers: Vec[Modifier])
  case class Transfer(destination: Operand, source: Operand, modifiers: Vec[Modifier])

  /** Operand resolution and dense storage slots of one routine. Symbols resolve to addresses when lowered, so the
    * interpreter never looks a name up.
    */
  class Scope(
      functionTable: FunctionTable,
      definedFunctions: Set[NonEmpty[String]],
      globals: Map[NonEmpty[String], GuestAddress],
      kernelParameters: Map[NonEmpty[String], Long],
      declared: Set[NonEmpty[String]],
      spilled: Set[NonEmpty[String]],
      locals: Map[NonEmpty[String], Long],
      shared: Map[NonEmpty[String], Long],
      declaredRegisters: Set[NonEmpty[String]],
      val architecture: Int
  ) {
    private val registers                                     = mutable.HashMap.empty[NonEmpty[String], Register]
    private val predicates                                    = mutable.HashMap.empty[NonEmpty[String], Predicate]
    private val parameters                                    = mutable.HashMap.empty[NonEmpty[String], VMIR.Parameter]
    val directCalls: mutable.LinkedHashSet[NonEmpty[String]]  = mutable.LinkedHashSet.empty
    val addressTaken: mutable.LinkedHashSet[NonEmpty[String]] = mutable.LinkedHashSet.empty

    private def slot(names: mutable.HashMap[NonEmpty[String], ?]): Int >= 0 = names.size.unsafe[Int >= 0]

    // A routine may declare a register under a special register's name, which then shadows it.
    private def special(name: NonEmpty[String]): Either[Error, Unit] =
      if (SpecialRegister.matches(name.raw) && !declaredRegisters(name))
        invalid(s"special register $name is not supported here")
      else Right(())

    def isKernelParameter(name: NonEmpty[String]): Boolean    = kernelParameters.contains(name)
    def kernelParameter(name: NonEmpty[String]): Option[Long] = kernelParameters.get(name)

    /** The frame-local offset of a function parameter whose address is taken. */
    def spilledParameter(name: NonEmpty[String]): Option[Long] = if (spilled(name)) locals.get(name) else None

    def parameter(name: NonEmpty[String]): Either[Error, VMIR.Parameter] =
      if (!declared(name)) invalid(s"unknown parameter $name")
      else Right(parameters.getOrElseUpdate(name, VMIR.Parameter(slot(parameters))))

    def register(operand: Operand, describe: String = "register"): Either[Error, Register] = operand match {
      case Operand.Register(name, None) =>
        special(name).map(_ => registers.getOrElseUpdate(name, Register(slot(registers))))
      case Operand.Symbol(name) => Right(registers.getOrElseUpdate(name, Register(slot(registers))))
      case other                => invalid(s"expected $describe, found $other")
    }

    def predicateNames: Vec[NonEmpty[String]] = Vec.from(predicates.toVector.sortBy(_._2.raw).map(_._1))

    /** Each register slot's source name, filled by slot so lowering every routine stays cheap. */
    def registerNames: Vec[NonEmpty[String]] = {
      val names = Array.ofDim[NonEmpty[String]](registers.size)
      registers.foreachEntry((name, slot) => names(slot.raw) = name)
      Vec.unsafeWrapArray(names)
    }

    def registerCount: Int >= 0  = slot(registers)
    def predicateCount: Int >= 0 = slot(predicates)

    def predicate(operand: Operand): Either[Error, Predicate] = operand match {
      case Operand.Register(name, None) =>
        special(name).map(_ => predicates.getOrElseUpdate(name, Predicate(slot(predicates))))
      case Operand.Symbol(name) => Right(predicates.getOrElseUpdate(name, Predicate(slot(predicates))))
      case other                => invalid(s"expected predicate register, found $other")
    }

    /** A predicate source operand and whether it is negated with `!`. */
    def predicateSource(operand: Operand): Either[Error, (Predicate, Boolean)] = operand match {
      case Operand.Not(inner) => predicate(inner).map(_ -> true)
      case other              => predicate(other).map(_ -> false)
    }

    def symbol(name: NonEmpty[String]): Either[Error, Value] =
      kernelParameters.get(name).map(Value.Immediate(_))
        .orElse(locals.get(name).map(Value.Immediate(_)))
        .orElse(shared.get(name).map(address =>
          Value.Address(Value.SharedWindow, address - VMAddressSpace.SharedBase)
        ))
        .orElse(globals.get(name).map(address => Value.Immediate(address.raw)))
        .orElse(functionTable.address(name).map { address =>
          addressTaken += name
          Value.Immediate(address)
        })
        .toRight(Error.Invalid(
          if (declared(name)) s"the address of locally scoped parameter $name cannot be taken"
          else s"unknown symbol $name"
        ))

    def value(operand: Operand): Either[Error, Value] = operand match {
      case Operand.Register(name, None) =>
        specialRegisters.get(name.raw).filterNot(_ => declaredRegisters(name))
          .fold(register(operand).map(Value.Register(_)))(Right(_))
      case Operand.Symbol(name) => predefinedConstants.get(name.raw).fold(symbol(name))(Right(_))
      case literal: (Operand.Immediate | Operand.Negate) =>
        NumericLiteral.immediate(literal).map(Value.Immediate(_)).toRight(Error.Invalid(s"invalid immediate $literal"))
      case other => invalid(s"expected register or immediate, found $other")
    }

    /** The CTA mask of a `form` that multicasts when `multicast`, which it then requires. */
    def multicastMask(mask: Option[Operand], multicast: Boolean, form: String): Either[Error, Option[Value]] =
      if (multicast)
        mask.toRight(Error.Invalid(s"multicast $form requires a CTA mask")).flatMap(value(_)).map(Some(_))
      else Right(None)

    def predicateValue(operand: Operand): Either[Error, Value] = operand match {
      case Operand.Register(name, None) if name.raw == "%is_explicit_cluster" && !declaredRegisters(name) =>
        Right(Value.ExplicitCluster)
      case Operand.Register(_, None) | Operand.Symbol(_) => predicate(operand).map(Value.Predicate(_))
      case other                                         => value(other)
    }

    def offsetValue(operand: Operand): Either[Error, Value] = {
      def displaced(base: Operand, displacement: Long): Either[Error, Value] = value(base).map {
        case Value.Immediate(address)      => Value.Immediate(address + displacement)
        case Value.Address(origin, offset) => Value.Address(origin, offset + displacement)
        case other                         => Value.Address(other, displacement)
      }
      def displacement(offset: Operand): Either[Error, Long] =
        NumericLiteral.immediate(offset).toRight(Error.Invalid(s"expected integer immediate, found $offset"))
      operand match {
        case Operand.Add(base, offset)      => displacement(offset).flatMap(displaced(base, _))
        case Operand.Subtract(base, offset) => displacement(offset).flatMap(delta => displaced(base, -delta))
        case other                          => value(other)
      }
    }

    def address(operand: Operand): Either[Error, Value] = operand match {
      case Operand.Address(Vec(single)) => offsetValue(single)
      case other                        => invalid(s"expected address operand, found $other")
    }

    def registers(operand: Operand): Either[Error, Vec[Register]] = operand match {
      case Operand.VectorLiteral(items) => items.traverse(register(_))
      case other                        => register(other).map(Vec(_))
    }

    def values(operand: Operand): Either[Error, Vec[Value]] = operand match {
      case Operand.VectorLiteral(items) => items.traverse(value(_))
      case other                        => value(other).map(Vec(_))
    }

    /** Destination components where `_` discards the corresponding part. */
    def sinks(operand: Operand): Either[Error, Vec[Option[Register]]] = operand match {
      case Operand.VectorLiteral(items) => items.traverse(sink(_))
      case other                        => sink(other).map(Vec(_))
    }

    def sink(operand: Operand): Either[Error, Option[Register]] = operand match {
      case Operand.Symbol(name) if name.raw == "_" => Right(None)
      case other                                   => register(other).map(Some(_))
    }

    def callParameters(operand: Operand): Either[Error, Vec[VMIR.Parameter]] = operand match {
      case Operand.Symbol(name)         => parameter(name).map(Vec(_))
      case Operand.Parenthesized(items) => items.flatTraverse(callParameters(_))
      case Operand.VectorLiteral(items) => items.flatTraverse(callParameters(_))
      case other                        => invalid(s"expected call parameter names, found $other")
    }

    def functionAddress(name: NonEmpty[String]): Option[Long] = functionTable.address(name)

    def definedFunction(name: NonEmpty[String]): Boolean = definedFunctions(name)
  }
}
