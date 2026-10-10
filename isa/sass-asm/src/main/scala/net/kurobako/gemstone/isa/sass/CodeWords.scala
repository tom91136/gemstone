package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*

/** Lossless structural view of a CUDA code section. Legacy bundle controls remain attached to their bundle, while the
  * exposed words contain only instruction bytes.
  */
object CodeWords {
  type Address     = Int >= 0
  type ControlBits = Int in 0 ~ 0x1fffff

  case class CodeWord(address: Address, bytes: Vec[Byte])

  enum BundleControl {
    case Kepler(bytes: Vec[Byte])
    case Maxwell(slots: Vec[ControlBits], reserved: Int in (0, 1))
    case Modern
  }

  case class CodeBundle(address: Address, control: BundleControl, words: Vec[CodeWord])

  case class Layout(generation: Generation, bundles: Vec[CodeBundle])

  /** Splits code bytes without losing bundle controls or their reserved bits. */
  def split(code: Array[Byte], generation: Generation): Either[String, Layout] = {
    val bundleBytes = generation.bundleBytes.raw
    if (code.length % bundleBytes != 0) Left(s"$generation code must contain complete $bundleBytes-byte bundles")
    else Right(Layout(
      generation,
      Vec.indices((code.length / bundleBytes).unsafe[Int >= 0]).map { index =>
        val base    = (index.raw * bundleBytes).unsafe[Address]
        val control = generation match {
          case Generation.Kepler  => BundleControl.Kepler(code.slice(base.raw, base.raw + 8).toVec)
          case Generation.Maxwell =>
            val word = LittleEndian.decodeInt64(code, base.raw)
            BundleControl.Maxwell(
              Vec.range[0, 3].map(slot => ((word >>> (slot.raw * 21)) & 0x1fffffL).toInt.unsafe[ControlBits]),
              ((word >>> 63) & 1L).toInt.unsafe[Int in (0, 1)]
            )
          case Generation.Modern => BundleControl.Modern
        }
        CodeBundle(
          base,
          control,
          Vec.indices(generation.slots.unsafe[Int >= 0]).map { slot =>
            val address = base.raw + generation.controlBytes + slot.raw * generation.instructionBytes.raw
            CodeWord(address.unsafe[Address], code.slice(address, address + generation.instructionBytes.raw).toVec)
          }
        )
      }
    ))
  }

  /** Reassembles a layout after validating its addresses, counts, widths, and controls. */
  def join(layout: Layout): Either[String, Array[Byte]] = {
    val generation  = layout.generation
    val bundleBytes = generation.bundleBytes.raw
    val totalBytes  = layout.bundles.size.toLong * bundleBytes
    if (totalBytes > Int.MaxValue) Left("SASS code section is too large to encode")
    else {
      val output = Array.ofDim[Byte](totalBytes.toInt)
      layout.bundles.zipWithIndex.foldLeft[Either[String, Unit]](Right(())) { case (result, (bundle, index)) =>
        result.flatMap(_ => validate(generation, bundle, index * bundleBytes).map(_ => encode(output, bundle)))
      }.map(_ => output)
    }
  }

  /** Presents each instruction with the control bits required by the encoding tables. */
  def words(layout: Layout): Vec[(Address, Array[Byte])] =
    layout.bundles.flatMap { bundle =>
      bundle.control match {
        case BundleControl.Kepler(_) | BundleControl.Modern =>
          bundle.words.map(word => (word.address, word.bytes.toArray))
        case BundleControl.Maxwell(controls, _) =>
          bundle.words.zip(controls).map((word, control) => (word.address, word.bytes.toArray ++ controlBytes(control)))
      }
    }

  /** The 21-bit Maxwell control appended little-endian to its 8-byte instruction word. */
  def controlBytes(control: ControlBits): Array[Byte] =
    Array.tabulate[Byte](3)(index => (control.raw >>> (index * 8)).toByte)

  private def validate(generation: Generation, bundle: CodeBundle, base: Int): Either[String, Unit] = {
    val validControl = (generation, bundle.control) match {
      case (Generation.Kepler, BundleControl.Kepler(bytes))      => bytes.size == 8
      case (Generation.Maxwell, BundleControl.Maxwell(slots, _)) => slots.size == 3
      case (Generation.Modern, BundleControl.Modern)             => true
      case _                                                     => false
    }
    for {
      _ <- Either.cond(bundle.address.raw == base, (), s"invalid SASS bundle address ${bundle.address}; expected $base")
      _ <- Either.cond(validControl, (), s"invalid SASS control data for $generation")
      _ <- Either.cond(bundle.words.size == generation.slots, (), s"SASS bundle requires ${generation.slots} words")
      _ <- bundle.words.zipWithIndex.foldLeft[Either[String, Unit]](Right(())) { case (result, (word, index)) =>
        val expected = base + generation.controlBytes + index * generation.instructionBytes.raw
        result.flatMap(_ =>
          if (word.address.raw != expected) Left(s"invalid SASS word address ${word.address}; expected $expected")
          else
            Either.cond(
              word.bytes.size == generation.instructionBytes.raw,
              (),
              s"SASS word must contain ${generation.instructionBytes} bytes"
            )
        )
      }
    } yield ()
  }

  private def encode(output: Array[Byte], bundle: CodeBundle): Unit = {
    bundle.control match {
      case BundleControl.Kepler(bytes)            => copy(bytes, output, bundle.address.raw)
      case BundleControl.Maxwell(slots, reserved) =>
        val control = slots.zipWithIndex.foldLeft(reserved.raw.toLong << 63) { case (value, (slot, index)) =>
          value | (slot.raw.toLong << (index * 21))
        }
        LittleEndian.write(control, output, bundle.address.raw, 8)
      case BundleControl.Modern => ()
    }
    bundle.words.foreach(word => copy(word.bytes, output, word.address.raw))
  }

  private def copy(source: Vec[Byte], destination: Array[Byte], offset: Int): Unit =
    source.zipWithIndex.foreach((value, index) => destination(offset + index) = value)
}
