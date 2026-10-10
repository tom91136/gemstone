package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*

/** Where a kernel's explicit arguments sit in its kernarg segment and which hidden arguments follow them, as the code
  * object's metadata declares. Hidden arguments the dispatch describes are filled in as the HSA runtime would; the rest
  * stay zero.
  */
private case class KernargLayout(
    parameters: Vec[KernelMetadata.Parameter],
    hidden: Vec[CodeObjectMetadata.HiddenArgument]
) {
  def hiddenValue(kind: String, geometry: LaunchGeometry): Long = kind match {
    case "hidden_block_count_x"   => geometry.groupCount.x.raw.toLong
    case "hidden_block_count_y"   => geometry.groupCount.y.raw.toLong
    case "hidden_block_count_z"   => geometry.groupCount.z.raw.toLong
    case "hidden_group_size_x"    => geometry.groupSize.x.raw.toLong
    case "hidden_group_size_y"    => geometry.groupSize.y.raw.toLong
    case "hidden_group_size_z"    => geometry.groupSize.z.raw.toLong
    case "hidden_remainder_x"     => geometry.globalExtent.x.raw % geometry.groupSize.x.raw
    case "hidden_remainder_y"     => geometry.globalExtent.y.raw % geometry.groupSize.y.raw
    case "hidden_remainder_z"     => geometry.globalExtent.z.raw % geometry.groupSize.z.raw
    case "hidden_global_offset_x" => geometry.globalOffset.x.raw.toLong
    case "hidden_global_offset_y" => geometry.globalOffset.y.raw.toLong
    case "hidden_global_offset_z" => geometry.globalOffset.z.raw.toLong
    case "hidden_grid_dims"       => geometry.workDim.raw.toLong
    case _                        => 0L
  }

  /** `explicit` placed at their declared offsets, followed by the hidden arguments, in `size` bytes. */
  def pack(explicit: Vec[Vec[Byte]], geometry: LaunchGeometry, size: Int): Either[Fault, Array[Byte]] = {
    val packed                                 = Array.ofDim[Byte](size)
    def fits(offset: Int, width: Int): Boolean = offset >= 0 && width >= 0 && offset <= size - width
    for {
      _ <- Either.cond(
        explicit.size == parameters.size,
        (),
        Fault.InvalidRequest(s"kernel declares ${parameters.size} arguments, received ${explicit.size}")
      )
      _ <- explicit.zip(parameters).zipWithIndex.foldLeft[Either[Fault, Unit]](Right(())) {
        case (result, ((bytes, parameter), index)) => result.flatMap { _ =>
            Either.cond(
              bytes.size <= parameter.width.raw && fits(parameter.offset.raw, parameter.width.raw),
              (),
              Fault.InvalidRequest(
                s"argument $index takes ${bytes.size} bytes, but its parameter holds ${parameter.width} at ${parameter.offset}"
              )
            ).map(_ => bytes.copyToArray(packed, parameter.offset.raw)).map(_ => ())
          }
      }
      _ <- hidden.foldLeft[Either[Fault, Unit]](Right(())) { (result, argument) =>
        result.flatMap { _ =>
          Either.cond(
            fits(argument.offset.raw, argument.width.raw),
            LittleEndian.write(
              hiddenValue(argument.valueKind, geometry),
              packed,
              argument.offset.raw,
              math.min(argument.width.raw, 8).unsafe[Int in 0 ~ 8]
            ),
            Fault.InvalidRequest(s"hidden argument ${argument.valueKind} lies outside the $size-byte kernarg segment")
          )
        }
      }
    } yield packed
  }
}
