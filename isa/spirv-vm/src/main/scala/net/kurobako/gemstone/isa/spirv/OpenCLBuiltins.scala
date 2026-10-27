package net.kurobako.gemstone.isa.spirv

import Op.*
import VMCollectives.Kind as CollectiveKind
import VMCollectives.Payload as CollectivePayload
import VMCollectives.Request as CollectiveRequest
import VMInvocation.*
import VMProgram.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*

/** OpenCL C builtins that compilers leave as calls to mangled, bodiless functions. They resolve when the call site
  * lowers.
  */
private object OpenCLBuiltins {
  def broadcastScope(name: String): Option[Int] =
    if (name.startsWith("_Z19sub_group_broadcast")) Some(Scope.Subgroup)
    else if (name.startsWith("_Z20work_group_broadcast")) Some(Scope.Workgroup)
    else None

  def collective(name: String): Boolean = name match {
    case "_Z7barrierj" | "_Z29work_group_scan_exclusive_addi" | "_Z29work_group_scan_exclusive_addl" |
        "_Z28sub_group_scan_exclusive_addi" => true
    case value => broadcastScope(value).isDefined || value.startsWith("_Z26intel_sub_group_shuffle_up")
  }

  /** Builtins indexed by a work dimension, and the label their faults use. */
  private val dimensioned: Map[String, (String, (Invocation, Int) => Long)] = Map(
    "_Z12get_local_idj"            -> ("get_local_id", _.local(_)),
    "_Z12get_group_idj"            -> ("_Z12get_group_idj", _.group(_)),
    "_Z14get_local_sizej"          -> ("_Z14get_local_sizej", _.workgroupSize(_)),
    "_Z23get_enqueued_local_sizej" -> ("_Z23get_enqueued_local_sizej", _.enqueuedWorkgroupSize(_)),
    "_Z13get_global_idj"           -> ("get_global_id", _.global(_)),
    "_Z15get_global_sizej"         -> ("_Z15get_global_sizej", _.globalSize(_)),
    "_Z17get_global_offsetj"       -> ("_Z17get_global_offsetj", _.globalOffset(_)),
    "_Z14get_num_groupsj"          -> ("_Z14get_num_groupsj", _.numWorkgroups(_))
  )

  private val subgroup: Map[String, Invocation => Long] = Map(
    "_Z18get_sub_group_sizev"     -> (_.subgroupSize.toLong),
    "_Z22get_sub_group_local_idv" -> (_.subgroupLocal.toLong),
    "_Z16get_sub_group_idv"       -> (_.subgroupIndex.toLong),
    "_Z18get_num_sub_groupsv"     -> (_.numSubgroups)
  )

  def lower(site: Site, name: String, ids: ResultIds, arguments: Vec[Int]): Option[Op] = {
    val program                              = site.program
    val resultType                           = ids.resultType
    val dest                                 = ids.dest
    def integerScalar: Option[Int in 1 ~ 64] = program.integerShape(resultType).collect { case (width, 1) => width }
    inline def scalarResult(label: String)(inline value: Invocation => Long): Op = integerScalar match {
      case None        => Fail(Fault.InvalidRequest(s"OpenCL $label result type %$resultType is not an integer scalar"))
      case Some(width) =>
        step((execution, _) =>
          Right(execution.values.updateScalar(dest, resultType, value(execution.invocation) & mask(width)))
        )
    }
    def pointerArgument(label: String, bytes: Int in 1 ~ 8)(convert: Long => Long): Op =
      if (arguments.size != 1)
        Fail(Fault.InvalidRequest(s"$label expects one pointer argument, received ${arguments.size}"))
      else
        step { (execution, memory) =>
          execution.values.get(arguments.head) match {
            case Some(VMValue.Address(_, pointer)) =>
              memory.readScalar(pointer, bytes).map(raw =>
                execution.values.updateScalar(dest, resultType, convert(raw))
              )
            case Some(_) => Left(Fault.InvalidRequest(s"$label argument is not a pointer"))
            case None    => Left(undefined(arguments.head))
          }
        }
    def scan(scope: Int, label: String): Op = Dynamic { (execution, _) =>
      execution.values.get(arguments.head) match {
        case Some(value @ VMValue.Bits(sourceType, lanes))
            if lanes.length == 1 && program.integerShape(sourceType).isDefined &&
              program.integerShape(sourceType) == program.integerShape(resultType) =>
          suspend(
            execution,
            CollectiveRequest(
              CollectiveKind.IntegerAdd,
              _,
              _,
              scope,
              CollectivePayload.Value(resultType, dest, value),
              2
            )
          )
        case Some(_) => Left(Fault.InvalidRequest(s"OpenCL $label requires matching integer scalar types"))
        case None    => Left(undefined(arguments.head))
      }
    }
    dimensioned.get(name).map { case (label, select) =>
      integerScalar match {
        case None => Fail(Fault.InvalidRequest(s"OpenCL $label result type %$resultType is not an integer scalar"))
        case Some(width) =>
          step { (execution, _) =>
            scalar(execution.values, arguments.head).flatMap { dimension =>
              if (dimension < 0L || dimension >= 3L)
                Left(Fault.InvalidRequest(s"OpenCL $label dimension $dimension is invalid"))
              else
                Right(execution.values.updateScalar(
                  dest,
                  resultType,
                  select(execution.invocation, dimension.toInt) & mask(width)
                ))
            }
          }
      }
    }.orElse(subgroup.get(name).map(scalarResult(name))).orElse(name match {
      case "_Z3minmm" => Some(step { (execution, _) =>
          for {
            left  <- scalar(execution.values, arguments(0))
            right <- scalar(execution.values, arguments(1))
            width <-
              integerScalar.toRight(Fault.InvalidRequest("OpenCL min requires integer scalar arguments and result"))
          } yield execution.values.updateScalar(
            dest,
            resultType,
            Execute.minimum(left, right, signed = false) & mask(width)
          )
        })
      case "_Z3ctzi" =>
        val sourceWidth = program.integerShape(program.valueType(arguments.head)).collect { case (width, 1) => width }
        Some(step { (execution, _) =>
          scalar(execution.values, arguments.head).flatMap { value =>
            (sourceWidth.filter(_ => program.scalarSlot(arguments.head)), integerScalar) match {
              case (Some(source), Some(width)) =>
                Right(execution.values.updateScalar(
                  dest,
                  resultType,
                  Execute.trailingZeros(value, source) & mask(width)
                ))
              case _ => Left(Fault.InvalidRequest("OpenCL ctz requires integer scalar arguments and result"))
            }
          }
        })
      case shuffle if shuffle.startsWith("_Z26intel_sub_group_shuffle_up") =>
        Some(
          if (arguments.size != 3)
            Fail(Fault.InvalidRequest(s"OpenCL $shuffle expects current, next, and delta arguments"))
          else CollectiveOps.intelShift(
            ids,
            arguments(0),
            arguments(1),
            arguments(2),
            Collective.ShuffleDirection.Up,
            "OpenCL subgroup shuffle-up"
          )
        )
      case "__devicelib_ConvertFToBF16INTEL" =>
        Some(pointerArgument(name, 4)(raw => IEEE754.binary32ToBfloat16(Binary32.low(raw)).toLong))
      case "__devicelib_ConvertBF16ToFINTEL" => Some(pointerArgument(name, 2)(raw => (raw & 0xffffL) << 16))
      case "__to_global"                     => Some(step { (execution, _) =>
          (execution.values.get(arguments.head), program.runtimeType(resultType)) match {
            case (Some(VMValue.Address(_, pointer)), Some(RuntimeType.Pointer(storageClass, pointee))) =>
              Right(execution.values.update(
                dest,
                VMValue.Address(resultType, pointer.copy(pointee = pointee, storageClass = storageClass))
              ))
            case (Some(VMValue.Address(_, _)), _) =>
              Left(Fault.InvalidRequest("OpenCL __to_global result is not a pointer"))
            case (Some(_), _) => Left(Fault.InvalidRequest("OpenCL __to_global source is not a pointer"))
            case (None, _)    => Left(undefined(arguments.head))
          }
        })
      case "_Z5isnanf" => Some(step { (execution, _) =>
          execution.values.get(arguments.head) match {
            case Some(VMValue.Bits(sourceType, Array(raw))) =>
              (program.floatingShape(sourceType), program.integerShape(resultType)) match {
                case (Some((width, 1)), Some((resultWidth, 1))) =>
                  val nan = IEEE754.isNaN(raw & mask(width), width)
                  Right(execution.values.updateScalar(dest, resultType, (if (nan) 1L else 0L) & mask(resultWidth)))
                case (None, _) => Left(Fault.InvalidRequest("OpenCL isnan source is not a floating-point scalar"))
                case (_, None) => Left(Fault.InvalidRequest("OpenCL isnan result is not an integer scalar"))
                case _         => Left(Fault.InvalidRequest("OpenCL isnan requires scalar source and result types"))
              }
            case Some(_) => Left(Fault.InvalidRequest("OpenCL isnan source is not numeric"))
            case None    => Left(undefined(arguments.head))
          }
        })
      case "_Z7barrierj" => Some(Dynamic { (execution, _) =>
          scalar(execution.values, arguments.head).flatMap { flags =>
            suspend(
              execution,
              CollectiveRequest(
                CollectiveKind.Barrier,
                _,
                _,
                Scope.Workgroup,
                CollectivePayload.Barrier(Scope.Workgroup, flags)
              )
            )
          }
        })
      case "_Z29work_group_scan_exclusive_addi" => Some(scan(Scope.Workgroup, "work_group_scan_exclusive_add"))
      case "_Z29work_group_scan_exclusive_addl" => Some(scan(Scope.Workgroup, "scan"))
      case "_Z28sub_group_scan_exclusive_addi"  => Some(scan(Scope.Subgroup, "scan"))
      case broadcast if broadcastScope(broadcast).isDefined =>
        val scope = broadcastScope(broadcast).getOrElse(Scope.Workgroup)
        Some(Dynamic { (execution, _) =>
          val values = execution.values
          (values.get(arguments(0)), scalar(values, arguments(1)), program.integerShape(resultType)) match {
            case (Some(value @ VMValue.Bits(sourceType, lanes)), Right(localId), Some((width, count)))
                if lanes.length == count && program.integerShape(sourceType).contains((width, count)) =>
              suspend(
                execution,
                CollectiveRequest(
                  CollectiveKind.Broadcast,
                  _,
                  _,
                  scope,
                  CollectivePayload.Value(resultType, dest, value),
                  selector = Vec(localId & mask(width))
                )
              )
            case (None, _, _)        => Left(undefined(arguments(0)))
            case (_, Left(error), _) => Left(error)
            case _                   =>
              Left(Fault.InvalidRequest("OpenCL broadcast requires matching integer scalar arguments and result"))
          }
        })
      case _ => None
    })
  }
}
