package net.kurobako.gemstone.isa.spirv

import LaneOps.*
import Op.*
import VMInvocation.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** Composite construction, extraction, insertion and vector permutation over flattened scalar lanes. */
private object CompositeOps {
  import Operand.*

  def lower(using site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.CompositeExtract(t, d, composite, indexes) => extract(ResultIds(t, d), id(composite), indexes)
    // a cooperative matrix is constructed from one scalar that fills it
    case Instruction.CompositeConstruct(t, d, Vec(constituent)) if site.program.cooperativeMatrices.contains(id(t)) =>
      val matrix = site.program.cooperativeMatrices(id(t))
      construct(ResultIds(t, d), Vec.fill(matrix.rows * matrix.columns)(id(constituent)))
    case Instruction.CompositeConstruct(t, d, constituents)      => construct(ResultIds(t, d), constituents.map(id(_)))
    case Instruction.CompositeConstructReplicateEXT(t, d, value) =>
      VMLoader.constituentCount(site.program.types, id(t)).fold(
        Fail(Fault.InvalidRequest(s"OpCompositeConstructReplicateEXT result type %${id(t)} is not a composite"))
      )(count => construct(ResultIds(t, d), Vec.fill(count)(id(value))))
    case Instruction.CompositeInsert(t, d, source, composite, indexes) =>
      insert(ResultIds(t, d), id(source), id(composite), indexes)
    case Instruction.VectorExtractDynamic(t, d, vector, index) => extractDynamic(ResultIds(t, d), id(vector), id(index))
    case Instruction.VectorInsertDynamic(t, d, vector, component, index) =>
      insertDynamic(ResultIds(t, d), vector = id(vector), component = id(component), index = id(index))
    case Instruction.VectorShuffle(t, d, left, right, components) =>
      shuffle(ResultIds(t, d), id(left), id(right), components)
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
          offset = Math.addExact(offset, Math.toIntExact(before * leaves.toLong))
          current = element
        }
      val step = program.runtimeType(current) match {
        case Some(RuntimeType.VectorType(component, count)) if selected < count.toLong =>
          advance(component, selected)
        case Some(RuntimeType.MatrixType(column, count)) if selected < count.toLong => advance(column, selected)
        case Some(RuntimeType.Array(element, count)) if selected < count            => advance(element, selected)
        case Some(RuntimeType.Structure(members)) if selected < members.size.toLong =>
          members.take(selected.toInt).foldLeft[Either[Fault, Unit]](Right(())) { (result, member) =>
            result.flatMap(_ =>
              program.scalarLeafCount(member).map(leaves => offset = Math.addExact(offset, leaves))
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

  private def extract(ids: ResultIds, composite: Int, indexes: Vec[Operand])(using site: Site): Op = {
    val program = site.program
    val typeId  = program.valueType(composite)
    if (typeId < 0) Fail(undefined(composite))
    else if (!program.scalarSlot(composite) && program.runtimeType(typeId).exists(isPointer))
      Fail(Fault.Unsupported("composite extraction requires scalar data"))
    else
      leafRange(program, typeId, indexes) match {
        case Left(error)            => Fail(error)
        case Right((offset, count)) => Extract(composite, offset, count, ids.dest, ids.resultType)
      }
  }

  private class Extract(composite: Int, offset: Int, count: Int, dest: Int, resultType: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      CompositeSemantics.extract(InvocationMachine(execution))(composite, offset, count, dest, resultType)
  }

  private def construct(ids: ResultIds, constituents: Vec[Int])(using site: Site): Op = {
    val program = site.program
    val kinds   = constituents.map(typed(site, _))
    kinds.collectFirst {
      case Typed.Undefined(value) => Fail(undefined(value))
      case Typed.Opaque(value, _) =>
        Fail(Fault.InvalidRequest(s"OpCompositeConstruct constituent %$value is a pointer"))
    }.getOrElse {
      val counts =
        constituents.map(constituent => program.scalarLeafCount(program.valueType(constituent)).fold(_ => -1, _.raw))
      program.scalarLeafCount(ids.resultType) match {
        case Left(error)                                                          => Fail(error)
        case Right(expected) if counts.sum != expected.raw || counts.contains(-1) =>
          Fail(Fault.InvalidRequest(
            s"OpCompositeConstruct %${ids.dest} has ${counts.sum} scalar leaves, expected $expected"
          ))
        case Right(expected) => Construct(constituents.toArray, expected, ids.dest, ids.resultType)
      }
    }
  }

  private class Construct(parts: Array[Int], leaves: Int, dest: Int, resultType: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      CompositeSemantics.construct(InvocationMachine(execution))(parts, leaves, dest, resultType)
  }

  private def insert(ids: ResultIds, source: Int, composite: Int, indexes: Vec[Operand])(using
      site: Site
  ): Op = {
    val program = site.program
    val typeId  = program.valueType(composite)
    if (program.valueType(source) < 0) Fail(undefined(source))
    else if (typeId < 0) Fail(undefined(composite))
    else
      leafRange(program, typeId, indexes) match {
        case Left(error)            => Fail(error)
        case Right((offset, count)) => Insert(source, composite, offset, count, ids.dest, ids.resultType)
      }
  }

  private class Insert(source: Int, composite: Int, offset: Int, count: Int, dest: Int, resultType: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      CompositeSemantics.insert(InvocationMachine(execution))(source, composite, offset, count, dest, resultType)
  }

  private def extractDynamic(ids: ResultIds, vector: Int, index: Int): Op =
    ExtractDynamic(vector, index, ids.dest, ids.resultType)

  private class ExtractDynamic(vector: Int, index: Int, dest: Int, resultType: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      CompositeSemantics.extractDynamic(InvocationMachine(execution))(vector, index, dest, resultType)
  }

  private def insertDynamic(ids: ResultIds, vector: Int, component: Int, index: Int): Op =
    InsertDynamic(vector, component, index, ids.dest, ids.resultType)

  private class InsertDynamic(vector: Int, component: Int, index: Int, dest: Int, resultType: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      CompositeSemantics.insertDynamic(InvocationMachine(execution))(vector, component, index, dest, resultType)
  }

  private def shuffle(ids: ResultIds, left: Int, right: Int, components: Vec[Operand])(using
      site: Site
  ): Op = {
    val program                        = site.program
    def lanes(value: Int): Option[Int] = program.scalarLeafCount(program.valueType(value)).toOption.rawElements
    (program.valueType(left), program.valueType(right), program.scalarLeafCount(ids.resultType)) match {
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
          case None => Shuffle(left, right, selection, ids.dest, ids.resultType)
        }
    }
  }

  private class Shuffle(left: Int, right: Int, selection: Array[Long], dest: Int, resultType: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      CompositeSemantics.shuffle(InvocationMachine(execution))(left, right, selection, dest, resultType)
  }
}
