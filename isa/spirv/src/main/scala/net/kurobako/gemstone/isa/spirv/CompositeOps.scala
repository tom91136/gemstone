package net.kurobako.gemstone.isa.spirv

import LaneOps.*
import Op.*
import VMLowering.*
import VMProgram.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** Composite construction, extraction, insertion and vector permutation over flattened scalar lanes. */
private object CompositeOps {
  import Operand.*

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.CompositeExtract(t, d, composite, indexes) => extract(site, id(t), id(d), id(composite), indexes)
    case Instruction.CompositeConstruct(t, d, constituents) => construct(site, id(t), id(d), constituents.map(id(_)))
    case Instruction.CompositeInsert(t, d, source, composite, indexes) =>
      insert(site, id(t), id(d), id(source), id(composite), indexes)
    case Instruction.VectorExtractDynamic(t, d, vector, index) => extractDynamic(id(t), id(d), id(vector), id(index))
    case Instruction.VectorInsertDynamic(t, d, vector, component, index) =>
      insertDynamic(id(t), id(d), id(vector), id(component), id(index))
    case Instruction.VectorShuffle(t, d, left, right, components) =>
      shuffle(site, id(t), id(d), id(left), id(right), components)
  }

  /** The first scalar lane and lane count that literal `indexes` select within `typeId`. */
  // Composite layout walks nested members with a cursor and fails on invalid type structure.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def leafRange(program: VMProgram, typeId: Int, indexes: Vec[Operand]): Either[Fault, (Int, Int >= 0)] = {
    var current = typeId
    var offset  = 0
    var index   = 0
    while (index < indexes.size) {
      val selected                                                 = unsigned(indexes(index))
      def advance(element: Int, before: Long): Either[Fault, Unit] =
        program.scalarLeafCount(element).map { leaves =>
          offset = Math.addExact(offset, Math.toIntExact(before * leaves.raw.toLong))
          current = element
        }
      val step = program.runtimeType(current) match {
        case Some(RuntimeType.VectorType(component, count)) if selected < count.raw.toLong =>
          advance(component, selected)
        case Some(RuntimeType.MatrixType(column, count)) if selected < count.raw.toLong => advance(column, selected)
        case Some(RuntimeType.Array(element, count)) if selected < count.raw            => advance(element, selected)
        case Some(RuntimeType.Structure(members)) if selected < members.size.toLong     =>
          members.take(selected.toInt).foldLeft[Either[Fault, Unit]](Right(())) { (result, member) =>
            result.flatMap(_ =>
              program.scalarLeafCount(member).map(leaves => offset = Math.addExact(offset, leaves.raw))
            )
          }.map(_ => current = members(selected.toInt))
        case Some(value) =>
          Left(Fault.InvalidRequest(s"OpComposite index $selected is invalid for type %$current ($value)"))
        case None => Left(Fault.InvalidRequest(s"OpComposite type %$current is undefined"))
      }
      step match {
        case Left(error) => return Left(error)
        case Right(())   => ()
      }
      index += 1
    }
    program.scalarLeafCount(current).map(offset -> _)
  }

  private def extract(site: Site, resultType: Int, dest: Int, composite: Int, indexes: Vec[Operand]): Op = {
    val program = site.program
    val typeId  = program.valueType(composite)
    if (typeId < 0) Fail(undefined(composite))
    else if (
      !program.scalarSlot(composite) && program.runtimeType(typeId).exists {
        case _: RuntimeType.Pointer => true
        case _                      => false
      }
    ) Fail(Fault.Unsupported("composite extraction requires scalar data"))
    else
      leafRange(program, typeId, indexes) match {
        case Left(error)            => Fail(error)
        case Right((offset, count)) =>
          step { (execution, _) =>
            val values = execution.values
            Option(values.lanes(composite)) match {
              case None                                                             => Left(undefined(composite))
              case Some(lanes) if offset >= 0 && offset + count.raw <= lanes.length =>
                Right(
                  if (count.raw == 1) values.updateScalar(dest, resultType, lanes(offset))
                  else values.update(dest, VMValue.Bits(resultType, lanes.slice(offset, offset + count.raw)))
                )
              case Some(lanes) =>
                Left(
                  Fault.InvalidRequest(s"OpCompositeExtract range $offset+$count exceeds ${lanes.length} scalar leaves")
                )
            }
          }
      }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  private def construct(site: Site, resultType: Int, dest: Int, constituents: Vec[Int]): Op = {
    val program = site.program
    val kinds   = constituents.map(typed(site, _))
    kinds.collectFirst {
      case Typed.Undefined(value) => Fail(undefined(value))
      case Typed.Opaque(value, _) =>
        Fail(Fault.InvalidRequest(s"OpCompositeConstruct constituent %$value is a pointer"))
    }.getOrElse {
      val counts =
        constituents.map(constituent => program.scalarLeafCount(program.valueType(constituent)).fold(_ => -1, _.raw))
      program.scalarLeafCount(resultType) match {
        case Left(error)                                                          => Fail(error)
        case Right(expected) if counts.sum != expected.raw || counts.contains(-1) =>
          Fail(Fault.InvalidRequest(s"OpCompositeConstruct %$dest has ${counts.sum} scalar leaves, expected $expected"))
        case Right(expected) =>
          val parts = constituents.toArray
          step { (execution, _) =>
            val values = execution.values
            val result = Array.ofDim[Long](expected.raw)
            var cursor = 0
            var index  = 0
            var absent = -1
            while (index < parts.length && absent < 0) {
              val part = parts(index)
              if (program.scalarSlot(part)) {
                result(cursor) = values.scalars(part)
                cursor += 1
              } else values.lanes(part) match {
                case null  => absent = part
                case lanes =>
                  Array.copy(lanes, 0, result, cursor, lanes.length)
                  cursor += lanes.length
              }
              index += 1
            }
            if (absent >= 0) Left(undefined(absent)) else Right(values.update(dest, VMValue.Bits(resultType, result)))
          }
      }
    }
  }

  private def insert(site: Site, resultType: Int, dest: Int, source: Int, composite: Int, indexes: Vec[Operand]): Op = {
    val program = site.program
    val typeId  = program.valueType(composite)
    if (program.valueType(source) < 0) Fail(undefined(source))
    else if (typeId < 0) Fail(undefined(composite))
    else
      leafRange(program, typeId, indexes) match {
        case Left(error)            => Fail(error)
        case Right((offset, count)) =>
          step { (execution, _) =>
            val values = execution.values
            (Option(values.lanes(source)), Option(values.lanes(composite))) match {
              case (Some(inserted), Some(lanes))
                  if inserted.length == count.raw && offset + count.raw <= lanes.length =>
                val result = lanes.clone()
                Array.copy(inserted, 0, result, offset, count.raw)
                Right(values.update(dest, VMValue.Bits(resultType, result)))
              case (Some(inserted), Some(_)) =>
                Left(Fault.InvalidRequest(
                  s"OpCompositeInsert source has ${inserted.length} leaves, expected $count at offset $offset"
                ))
              case _ => Left(Fault.Unsupported("composite insertion requires scalar data"))
            }
          }
      }
  }

  private def extractDynamic(resultType: Int, dest: Int, vector: Int, index: Int): Op = step { (execution, _) =>
    val values = execution.values
    (values.get(vector), values.get(index)) match {
      case (Some(VMValue.Bits(_, lanes)), Some(VMValue.Bits(_, Array(component)))) =>
        val selected = component.toInt
        if (selected < 0 || selected >= lanes.length)
          Left(Fault.InvalidRequest(s"OpVectorExtractDynamic index $selected is out of range"))
        else Right(values.updateScalar(dest, resultType, lanes(selected)))
      case (None, _) => Left(undefined(vector))
      case (_, None) => Left(undefined(index))
      case _         => Left(Fault.Unsupported("OpVectorExtractDynamic requires a scalar index"))
    }
  }

  private def insertDynamic(resultType: Int, dest: Int, vector: Int, component: Int, index: Int): Op =
    step { (execution, _) =>
      val values = execution.values
      (values.get(vector), values.get(component), scalar(values, index)) match {
        case (Some(VMValue.Bits(_, lanes)), Some(VMValue.Bits(_, Array(value))), Right(selected)) =>
          val position = selected.toInt
          if (position < 0 || position >= lanes.length)
            Left(Fault.InvalidRequest(s"OpVectorInsertDynamic index $position is out of range"))
          else {
            val result = lanes.clone()
            result(position) = value
            Right(values.update(dest, VMValue.Bits(resultType, result)))
          }
        case (None, _, _)        => Left(undefined(vector))
        case (_, None, _)        => Left(undefined(component))
        case (_, _, Left(error)) => Left(error)
        case _                   => Left(Fault.InvalidRequest("OpVectorInsertDynamic requires scalar component"))
      }
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def shuffle(site: Site, resultType: Int, dest: Int, left: Int, right: Int, components: Vec[Operand]): Op = {
    val program                        = site.program
    def lanes(value: Int): Option[Int] = program.scalarLeafCount(program.valueType(value)).toOption.map(_.raw)
    (program.valueType(left), program.valueType(right), program.scalarLeafCount(resultType)) match {
      case (-1, _, _)                                                 => Fail(undefined(left))
      case (_, -1, _)                                                 => Fail(undefined(right))
      case (_, _, Left(error))                                        => Fail(error)
      case (_, _, Right(expected)) if expected.raw != components.size =>
        Fail(Fault.InvalidRequest(
          s"OpVectorShuffle result has ${components.size} components, expected $expected scalar leaves"
        ))
      case _ =>
        val combined  = lanes(left).getOrElse(0) + lanes(right).getOrElse(0)
        val selection = components.map(unsigned).toArray
        selection.find(selected => selected != 0xffffffffL && selected >= combined.toLong) match {
          case Some(selected) =>
            Fail(Fault.InvalidRequest(s"OpVectorShuffle component $selected is outside $combined lanes"))
          case None =>
            step { (execution, _) =>
              val values = execution.values
              (Option(values.lanes(left)), Option(values.lanes(right))) match {
                case (Some(first), Some(second)) =>
                  val result = Array.ofDim[Long](selection.length)
                  var index  = 0
                  while (index < selection.length) {
                    val selected = selection(index)
                    result(index) =
                      if (selected == 0xffffffffL) 0L
                      else if (selected < first.length) first(selected.toInt)
                      else second(selected.toInt - first.length)
                    index += 1
                  }
                  Right(values.update(dest, VMValue.Bits(resultType, result)))
                case _ => Left(Fault.InvalidRequest("OpVectorShuffle operands must be numeric vectors"))
              }
            }
        }
    }
  }
}
