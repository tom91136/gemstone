package net.kurobako.gemstone.isa.spirv.v1_6

import Operand.*
import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
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
      words.head.unsafe[Int >= 0]
    }
    def qualifiers(parent: ShaderQualifiers): ShaderQualifiers = {
      require(!values.contains(Decoration.Sample), "sample interpolation is unsupported")
      require(!values.contains(Decoration.Index), "dual source interfaces are unsupported")
      val component = get(Decoration.Component).map(_.raw.unsafe[Int where Between[0, 3]]).getOrElse(parent.component)
      ShaderQualifiers(
        parent.flat || values.contains(Decoration.Flat),
        parent.noPerspective || values.contains(Decoration.NoPerspective),
        parent.centroid || values.contains(Decoration.Centroid),
        component
      )
    }
  }

  def interfaces(program: VMProgramRuntime, entry: NonEmpty[String], stage: ShaderStageKind): Vec[ShaderInterface] = {
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
    val checked                                             = mutable.Set.empty[Int]
    def validateType(value: Int, ancestors: Set[Int]): Unit = {
      require(!ancestors.contains(value) && ancestors.size < 128, "recursive or excessively nested shader interface")
      if (!checked.contains(value)) {
        val children: Vec[Int] = program.types(value) match {
          case RuntimeType.Bool | _: RuntimeType.Integer | _: RuntimeType.Floating => Vec.empty
          case RuntimeType.VectorType(component, count)                            =>
            require(count >= 1 && count <= 4, "invalid interface vector size")
            Vec(component)
          case RuntimeType.MatrixType(column, count) =>
            require(count >= 1 && count <= 4, "invalid interface matrix size")
            Vec(column)
          case RuntimeType.Array(element, count) =>
            require(count > 0 && count <= MaximumRecordBytes, "invalid interface array size")
            Vec(element)
          case RuntimeType.Structure(fields) => fields
          case _                             => throw IllegalArgumentException("unsupported shader interface type")
        }
        children.foreach(child => validateType(child, ancestors + value))
        val size = program.layout.typeSize(value)
        require(size > 0 && size <= MaximumRecordBytes, "invalid shader interface record size")
        checked += value
      }
    }
    Vec.from(VMShaderInterface.stageInterfaceVariables(program, entry).toVector.flatMap { variable =>
      program.globals.get(variable).filter(global =>
        global.storageClass == StorageClass.Input || global.storageClass == StorageClass.Output
      ).map { global =>
        val pointee = program.types(global.pointerType) match {
          case RuntimeType.Pointer(_, value) => value
          case _                             => throw IllegalArgumentException("shader interface is not a pointer")
        }
        validateType(pointee, Set.empty)
        val decorations = Decorations(variables.getOrElse(variable, Map.empty))
        val qualifiers  = decorations.qualifiers(DefaultQualifiers)
        val direction   =
          if (global.storageClass == StorageClass.Input) ShaderDirection.Input else ShaderDirection.Output
        val external = stage == ShaderStageKind.Vertex || direction == ShaderDirection.Output
        val fields   = mutable.ArrayBuffer.empty[ShaderField]
        def append(
            offset: Int,
            size: Int,
            location: Option[Int >= 0],
            builtin: Option[Int >= 0],
            q: ShaderQualifiers
        ): Unit = {
          require(fields.size < MaximumFields, "too many shader interface fields")
          require(
            offset >= 0 && size > 0 && Math.addExact(offset, size) <= program.layout.typeSize(pointee),
            "interface field exceeds record"
          )
          fields += ShaderField(offset.unsafe[Int >= 0], size.unsafe[Int > 0], location, builtin, q)
        }
        def located(value: Int, offset: Int, location: Int, q: ShaderQualifiers): Int = program.types(value) match {
          case RuntimeType.Array(element, count) =>
            val stride = program.arrayStrides.getOrElse(value, program.layout.typeSize(element))
            (0 until Math.toIntExact(count)).foldLeft(location)((next, index) =>
              located(element, Math.addExact(offset, Math.multiplyExact(index, stride)), next, q)
            )
          case RuntimeType.MatrixType(column, count) =>
            (0 until count).foldLeft(location)((next, index) =>
              located(
                column,
                Math.addExact(offset, Math.multiplyExact(index, program.layout.typeSize(column))),
                next,
                q
              )
            )
          case RuntimeType.Structure(types) =>
            types.zipWithIndex.foldLeft(location) { case (next, (member, index)) =>
              located(
                member,
                Math.addExact(offset, program.layout.structMemberOffset(value, types, index)),
                next,
                memberDecorations(value, index).qualifiers(q)
              )
            }
          case scalarOrVector =>
            val (scalar, lanes) = scalarOrVector match {
              case RuntimeType.VectorType(component, count) => program.types(component) -> count
              case scalar                                   => scalar                   -> 1
            }
            val width = scalar match {
              case RuntimeType.Floating(width)                         => width.raw
              case RuntimeType.Integer(width, _) if external || q.flat => width.raw
              case _ => throw IllegalArgumentException("unsupported interpolated interface scalar")
            }
            require(width != 64 || q.flat, "64-bit interfaces require flat interpolation")
            val spans = interfaceSpans(width, lanes, q.component.raw)
            spans.zipWithIndex.foreach { case ((byteOffset, bytes, component), index) =>
              append(
                Math.addExact(offset, byteOffset),
                bytes,
                Some(Math.addExact(location, index).unsafe[Int >= 0]),
                None,
                q.copy(component = component.unsafe[Int where Between[0, 3]])
              )
            }
            Math.addExact(location, spans.size)
        }
        decorations.get(Decoration.Location) match {
          case Some(location) => val _ = located(pointee, 0, location.raw, qualifiers)
          case None           => program.types(pointee) match {
              case RuntimeType.Structure(types) => types.zipWithIndex.foreach { case (member, index) =>
                  val d      = memberDecorations(pointee, index)
                  val q      = d.qualifiers(qualifiers)
                  val offset = program.layout.structMemberOffset(pointee, types, index)
                  d.get(Decoration.Location) match {
                    case Some(location) => val _ = located(member, offset, location.raw, q)
                    case None           => append(
                        offset,
                        program.layout.storageTypeSize(
                          member,
                          program.layout.memberMatrixLayouts.get(pointee -> index)
                        ),
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
          program.layout.typeSize(pointee).unsafe[Int > 0],
          decorations.get(Decoration.BuiltIn),
          qualifiers,
          Vec.from(fields)
        )
      }
    })
  }

  /** A location holds four 32-bit components. Wide vectors may occupy two locations. */
  def interfaceSpans(width: Int, lanes: Int, component: Int): Vec[(Int, Int, Int)] = {
    require(
      (width == 32 || width == 64) && lanes >= 1 && lanes <= 4 && component >= 0 && component <= 3,
      "invalid interface span"
    )
    val words = lanes * (width / 32)
    require(width != 32 || component + words <= 4, "interface exceeds location")
    require(width != 64 || (component % 2 == 0 && (component == 0 || words <= 2)), "misaligned wide interface")
    val first = math.min(words, 4 - component)
    Vec((0, first * 4, component)) ++ (if (first == words) Vec.empty else Vec((first * 4, (words - first) * 4, 0)))
  }
}
