package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*

/** Lossless structural view of a CUDA code section. Legacy bundle controls remain attached to their bundle, while the
  * exposed words contain only instruction bytes.
  */
object SASSCodeWords {
  type Address     = Int >= 0
  type ControlBits = Int where Between[0, 0x1fffff]

  case class CodeWord(address: Address, bytes: Vec[Byte])

  case class ControlWord(value: ControlBits)

  enum BundleControl {
    case Kepler(bytes: Vec[Byte])
    case Maxwell(slots: Vec[ControlWord], reserved: Int in (0, 1))
    case Modern
  }

  case class CodeBundle(address: Address, control: BundleControl, words: Vec[CodeWord])

  case class CodeSection(sm: Int >= 35, bundles: Vec[CodeBundle])

  /** Splits code bytes without losing bundle controls or their reserved bits. */
  def split(code: Array[Byte], sm: Int): Either[String, CodeSection] =
    if (sm >= 35 && sm < 50) splitKepler(code, sm)
    else if (sm >= 50 && sm < 70) splitMaxwell(code, sm)
    else if (sm >= 70 && sm <= 121) splitModern(code, sm)
    else Left(s"unsupported SASS architecture sm_$sm")

  /** Reassembles a section after validating its layout, addresses, counts, widths, and controls. */
  def join(section: CodeSection): Either[String, Array[Byte]] = {
    val sm     = section.sm.raw
    val layout =
      if (sm >= 35 && sm < 50) Some((64, 8, 7))
      else if (sm >= 50 && sm < 70) Some((32, 8, 3))
      else if (sm >= 70 && sm <= 121) Some((16, 0, 1))
      else None
    layout.toRight(s"unsupported SASS architecture sm_$sm").flatMap { (bundleBytes, controlBytes, wordCount) =>
      val totalBytes = section.bundles.size.toLong * bundleBytes
      if (totalBytes > Int.MaxValue) Left("SASS code section is too large to encode")
      else {
        val output = Array.ofDim[Byte](totalBytes.toInt)
        section.bundles.zipWithIndex.foldLeft[Either[String, Unit]](Right(())) { case (result, (bundle, index)) =>
          result.flatMap { _ =>
            val base = index.toLong * bundleBytes
            validateBundle(bundle, base, bundleBytes, controlBytes, wordCount, sm).map { _ =>
              encodeBundle(output, bundle, base.toInt)
            }
          }
        }.map(_ => output)
      }
    }
  }

  /** Compatibility view used by the existing disassembler: `(section byte address, encoded word)` pairs. Maxwell
    * instructions retain the historical 11-byte form, with their 21-bit control word appended little-endian.
    */
  def words(code: Array[Byte], sm: Int): Either[String, Vec[(Int, Array[Byte])]] =
    split(code, sm).map(words(_))

  /** Presents each instruction with the control bits required by the encoding tables. */
  def words(section: CodeSection): Vec[(Int, Array[Byte])] =
    section.bundles.flatMap { bundle =>
      bundle.control match {
        case BundleControl.Kepler(_) | BundleControl.Modern =>
          bundle.words.map(word => (word.address.raw, word.bytes.toArray))
        case BundleControl.Maxwell(controls, _) =>
          bundle.words.zip(controls).map { (word, control) =>
            val suffix = Array.tabulate[Byte](3) { index =>
              (control.value.raw >>> (index * 8)).toByte
            }
            (word.address.raw, word.bytes.toArray ++ suffix)
          }
      }
    }

  private def splitKepler(code: Array[Byte], sm: Int): Either[String, CodeSection] =
    if (code.length % 64 != 0) Left("SM35-SM49 code must contain complete 64-byte bundles")
    else Right(CodeSection(
      sm.unsafe[Int >= 35],
      Vec.tabulate(code.length / 64) { index =>
        val base = index * 64
        CodeBundle(
          base.unsafe[Address],
          BundleControl.Kepler(code.slice(base, base + 8).toVec),
          Vec.tabulate(7) { slot =>
            val address = base + 8 + slot * 8
            CodeWord(address.unsafe[Address], code.slice(address, address + 8).toVec)
          }
        )
      }
    ))

  private def splitMaxwell(code: Array[Byte], sm: Int): Either[String, CodeSection] =
    if (code.length % 32 != 0) Left("SM50-SM69 code must contain complete 32-byte bundles")
    else Right(CodeSection(
      sm.unsafe[Int >= 35],
      Vec.tabulate(code.length / 32) { index =>
        val base    = index * 32
        val control = readLong(code, base)
        val slots   = Vec.tabulate(3) { slot =>
          ControlWord(((control >>> (slot * 21)) & 0x1fffffL).toInt.unsafe[ControlBits])
        }
        CodeBundle(
          base.unsafe[Address],
          BundleControl.Maxwell(slots, ((control >>> 63) & 1L).toInt.unsafe[Int in (0, 1)]),
          Vec.tabulate(3) { slot =>
            val address = base + 8 + slot * 8
            CodeWord(address.unsafe[Address], code.slice(address, address + 8).toVec)
          }
        )
      }
    ))

  private def splitModern(code: Array[Byte], sm: Int): Either[String, CodeSection] =
    if (code.length % 16 != 0) Left("SM70+ code must contain complete 128-bit instructions")
    else Right(CodeSection(
      sm.unsafe[Int >= 35],
      Vec.tabulate(code.length / 16) { index =>
        val address = index * 16
        CodeBundle(
          address.unsafe[Address],
          BundleControl.Modern,
          Vec(CodeWord(address.unsafe[Address], code.slice(address, address + 16).toVec))
        )
      }
    ))

  private def validateBundle(
      bundle: CodeBundle,
      base: Long,
      bundleBytes: Int,
      controlBytes: Int,
      wordCount: Int,
      sm: Int
  ): Either[String, Unit] = {
    def at(expected: Long): Either[String, Unit] =
      if (expected > Int.MaxValue || bundle.address.raw.toLong != expected)
        Left(s"invalid SASS bundle address ${bundle.address.raw}; expected $expected")
      else Right(())

    val controlValidation: Either[String, Unit] = (sm < 50, controlBytes, bundle.control) match {
      case (true, 8, BundleControl.Kepler(bytes)) if bytes.size == 8      => Right(())
      case (false, 8, BundleControl.Maxwell(slots, _)) if slots.size == 3 => Right(())
      case (false, 0, BundleControl.Modern)                               => Right(())
      case _ => Left(s"invalid SASS control data for sm_$sm")
    }
    for {
      _ <- at(base)
      _ <- controlValidation
      _ <- Either.cond(bundle.words.size == wordCount, (), s"SASS bundle requires $wordCount words")
      _ <- bundle.words.zipWithIndex.foldLeft[Either[String, Unit]](Right(())) { case (result, (word, index)) =>
        result.flatMap { _ =>
          val expectedAddress = if (controlBytes == 0) base + index * 16L else base + 8 + index * 8L
          if (expectedAddress > Int.MaxValue || word.address.raw.toLong != expectedAddress)
            Left(s"invalid SASS word address ${word.address.raw}; expected $expectedAddress")
          else {
            val expectedWidth = if (controlBytes == 0) 16 else 8
            Either.cond(word.bytes.size == expectedWidth, (), s"SASS word must contain $expectedWidth bytes")
          }
        }
      }
      _ <- Either.cond(base + bundleBytes <= Int.MaxValue.toLong + 1L, (), "SASS code address overflow")
    } yield ()
  }

  private def encodeBundle(output: Array[Byte], bundle: CodeBundle, base: Int): Unit = {
    bundle.control match {
      case BundleControl.Kepler(bytes)            => copy(bytes, output, base)
      case BundleControl.Maxwell(slots, reserved) =>
        val control = slots.zipWithIndex.foldLeft(reserved.raw.toLong << 63) { (value, entry) =>
          val (slot, index) = entry
          value | (slot.value.raw.toLong << (index * 21))
        }
        writeLong(output, base, control)
      case BundleControl.Modern => ()
    }
    bundle.words.foreach(word => copy(word.bytes, output, word.address.raw))
  }

  private def copy(source: Vec[Byte], destination: Array[Byte], offset: Int): Unit =
    source.zipWithIndex.foreach { (value, index) => destination(offset + index) = value }

  private def readLong(bytes: Array[Byte], offset: Int): Long = (0 until 8).foldLeft(0L) { (value, index) =>
    value | ((bytes(offset + index).toLong & 255L) << (index * 8))
  }

  private def writeLong(bytes: Array[Byte], offset: Int, value: Long): Unit = (0 until 8).foreach(index =>
    bytes(offset + index) = (value >>> (index * 8)).toByte
  )
}
