package net.kurobako.gemstone.isa.spirv

import Operand.*
import VMProgram.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.graphics.ShaderMetadata.*
import scala.collection.mutable

/** Reflect invocation records from compiled types. No independent type layout or constant evaluator. */
private object ShaderReflection {
  private val MaximumRecordBytes = 1024 * 1024
  private val MaximumFields      = 16384
  private val DefaultQualifiers  = ShaderQualifiers(false, false, false, 0)

  private case class Decorations(values: Map[Long, Vec[Int]]) {
    def get(kind: Long): Option[Int >= 0] = values.get(kind).map { words =>
      require(words.size == 1, s"decoration $kind expects one operand")
      words.head.refine[Int >= 0].fold(
        _ => throw IllegalArgumentException(s"decoration $kind operand is out of range"),
        identity
      )
    }
    def qualifiers(parent: ShaderQualifiers): ShaderQualifiers = {
      require(!values.contains(Decoration.Sample), "sample interpolation is unsupported")
      require(!values.contains(Decoration.Index), "dual source interfaces are unsupported")
      val component = get(Decoration.Component).map(value =>
        value.raw.refine[Int in 0 ~ 3].fold(
          _ => throw IllegalArgumentException(s"interface component $value is out of range"),
          identity
        )
      ).getOrElse(parent.component)
      ShaderQualifiers(
        parent.flat || values.contains(Decoration.Flat),
        parent.noPerspective || values.contains(Decoration.NoPerspective),
        parent.centroid || values.contains(Decoration.Centroid),
        component
      )
    }
  }

  def interfaces(program: VMProgram, entry: NonEmpty[String], stage: ShaderStageKind): Vec[ShaderInterface] = {
    val variables = mutable.Map.empty[Int, Map[Long, Vec[Int]]]
    val members   = mutable.Map.empty[(Int, Int), Map[Long, Vec[Int]]]
    program.module.instructions.foreach {
      case Instruction.Decorate(target, decoration) =>
        variables.update(
          id(target),
          variables.getOrElse(
            id(target),
            Map.empty
          ).updated(unsigned(decoration), Vec.from(decoration.words.toVector.drop(1).map(_.raw)))
        )
      case Instruction.MemberDecorate(target, member, decoration) =>
        val key = id(target) -> id(member)
        members.update(
          key,
          members.getOrElse(
            key,
            Map.empty
          ).updated(unsigned(decoration), Vec.from(decoration.words.toVector.drop(1).map(_.raw)))
        )
      case _ => ()
    }
    def memberDecorations(structure: Int, index: Int): Decorations =
      Decorations(members.getOrElse(structure -> index, Map.empty))
    // Bound recursion and reject cycles before invoking the shared recursive layout routines.
    val checked                                                = mutable.Map.empty[Int, Int > 0]
    def validateType(value: Int, ancestors: Set[Int]): Int > 0 = {
      require(!ancestors.contains(value) && ancestors.size < 128, "recursive or excessively nested shader interface")
      if (!checked.contains(value)) {
        val children: Vec[Int] = program.types(value) match {
          case RuntimeType.Bool | _: RuntimeType.Integer | _: RuntimeType.Floating => Vec.empty
          case RuntimeType.VectorType(component, count)                            =>
            require(count.raw >= 1 && count.raw <= 4, "invalid interface vector size")
            Vec(component)
          case RuntimeType.MatrixType(column, count) =>
            require(count.raw >= 1 && count.raw <= 4, "invalid interface matrix size")
            Vec(column)
          case RuntimeType.Array(element, count) =>
            require(count.raw > 0 && count.raw <= MaximumRecordBytes, "invalid interface array size")
            Vec(element)
          case RuntimeType.Structure(fields) => fields
          case _                             => throw IllegalArgumentException("unsupported shader interface type")
        }
        children.foreach { child =>
          val _ = validateType(child, ancestors + value)
        }
        val size = program.typeSize(value).raw
        require(size > 0 && size <= MaximumRecordBytes, "invalid shader interface record size")
        checked.update(value, size.unsafe[Int > 0])
      }
      checked(value)
    }
    Vec.from(VMShaderInterface.stageInterfaceVariables(program, entry).toVector.flatMap { variable =>
      program.globals.get(variable).filter(global =>
        global.storageClass == StorageClass.Input || global.storageClass == StorageClass.Output
      ).map { global =>
        val pointee = program.types(global.pointerType) match {
          case RuntimeType.Pointer(_, value) => value
          case _                             => throw IllegalArgumentException("shader interface is not a pointer")
        }
        val recordSize  = validateType(pointee, Set.empty)
        val decorations = Decorations(variables.getOrElse(variable, Map.empty))
        val qualifiers  = decorations.qualifiers(DefaultQualifiers)
        val direction   =
          if (global.storageClass == StorageClass.Input) ShaderDirection.Input else ShaderDirection.Output
        val external = stage == ShaderStageKind.Vertex || direction == ShaderDirection.Output
        val fields   = mutable.ArrayBuffer.empty[ShaderField]
        def append(
            offset: Int >= 0,
            size: Int > 0,
            location: Option[Int >= 0],
            builtin: Option[Int >= 0],
            q: ShaderQualifiers
        ): Unit = {
          require(fields.size < MaximumFields, "too many shader interface fields")
          require(
            (offset + size).raw <= recordSize.raw,
            "interface field exceeds record"
          )
          fields += ShaderField(offset, size, location, builtin, q)
        }
        def located(value: Int, offset: Int >= 0, location: Int >= 0, q: ShaderQualifiers): Int >= 0 =
          program.types(value) match {
            case RuntimeType.Array(element, count) =>
              val stride: Int >= 0 = program.arrayStrides.get(value).fold(program.typeSize(element))(identity)
              Vec.indices(count.toIntExact).foldLeft(location)((next, index) =>
                located(element, offset + index * stride, next, q)
              )
            case RuntimeType.MatrixType(column, count) =>
              Vec.indices(count).foldLeft(location)((next, index) =>
                located(
                  column,
                  offset + index * program.typeSize(column),
                  next,
                  q
                )
              )
            case RuntimeType.Structure(types) =>
              types.zip(Vec.indices(types.nonNegativeSize)).foldLeft(location) { case (next, (member, index)) =>
                located(
                  member,
                  offset + program.structMemberOffset(value, types, index.raw),
                  next,
                  memberDecorations(value, index.raw).qualifiers(q)
                )
              }
            case scalarOrVector =>
              val (scalar, lanes) = scalarOrVector match {
                case RuntimeType.VectorType(component, count) => program.types(component) -> count
                case scalar                                   => scalar                   -> (1: Int > 0)
              }
              val width: Int in 1 ~ 64 = scalar match {
                case RuntimeType.Floating(width)                         => width
                case RuntimeType.Integer(width, _) if external || q.flat => width
                case _ => throw IllegalArgumentException("unsupported interpolated interface scalar")
              }
              require(width.raw != 64 || q.flat, "64-bit interfaces require flat interpolation")
              val spans = interfaceSpans(width, lanes, q.component)
              spans.zip(Vec.indices(spans.nonNegativeSize)).foreach { case ((byteOffset, bytes, component), index) =>
                append(
                  offset + byteOffset,
                  bytes,
                  Some(location + index),
                  None,
                  q.copy(component = component)
                )
              }
              location + spans.nonNegativeSize
          }
        decorations.get(Decoration.Location) match {
          case Some(location) => val _ = located(pointee, 0, location, qualifiers)
          case None           => program.types(pointee) match {
              case RuntimeType.Structure(types) => types.zipWithIndex.foreach { case (member, index) =>
                  val d      = memberDecorations(pointee, index)
                  val q      = d.qualifiers(qualifiers)
                  val offset = program.structMemberOffset(pointee, types, index)
                  d.get(Decoration.Location) match {
                    case Some(location) => val _ = located(member, offset, location, q)
                    case None           => append(
                        offset,
                        program.storageTypeSize(
                          member,
                          program.memberMatrixLayouts.get(pointee -> index)
                        ).raw.unsafe[Int > 0],
                        None,
                        d.get(Decoration.BuiltIn),
                        q
                      )
                  }
                }
              case _ => ()
            }
        }
        ShaderInterface(
          variable.unsafe[Int > 0],
          direction,
          recordSize,
          decorations.get(Decoration.BuiltIn),
          qualifiers,
          Vec.from(fields)
        )
      }
    })
  }

  /** A location holds four 32-bit components. Wide vectors may occupy two locations. */
  def interfaceSpans(
      width: Int in 1 ~ 64,
      lanes: Int > 0,
      component: Int in 0 ~ 3
  ): Vec[(Int >= 0, Int > 0, Int in 0 ~ 3)] = {
    require((width.raw == 32 || width.raw == 64) && lanes.raw <= 4, "invalid interface span")
    val words = lanes.raw * (width.raw / 32)
    require(width.raw != 32 || component.raw + words <= 4, "interface exceeds location")
    require(
      width.raw != 64 || (component.raw % 2 == 0 && (component.raw == 0 || words <= 2)),
      "misaligned wide interface"
    )
    val first                                           = math.min(words, 4 - component.raw).unsafe[Int > 0]
    val bytes                                           = first * 4
    val initial: Vec[(Int >= 0, Int > 0, Int in 0 ~ 3)] = Vec((0, bytes, component))
    if (first.raw == words) initial
    else initial :+ ((bytes, ((words - first.raw) * 4).unsafe[Int > 0], 0))
  }
}
