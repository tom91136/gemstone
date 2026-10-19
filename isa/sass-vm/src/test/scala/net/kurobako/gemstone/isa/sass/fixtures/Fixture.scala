package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.testing.*
import scala.annotation.tailrec

/** `nvdisasm -c` listings of the family fixtures by name. Listings are stored normalised: without the file preamble and
  * without the unreachable self-branch and NOP padding that ends each function.
  */
object Fixture {
  private val listings = Vec(
    BarrierFixtures.listings,
    CompilerKernelFixtures.listings,
    ConstantFixtures.listings,
    ControlFlowFixtures.listings,
    ConversionFixtures.listings,
    FloatFixtures.listings,
    GenericMemoryFixtures.listings,
    HalfFixtures.listings,
    IntegerAddFixtures.listings,
    IntegerCompareFixtures.listings,
    IntegerMultiplyFixtures.listings,
    LeaFixtures.listings,
    LogicFixtures.listings,
    MatrixFixtures.listings,
    WarpgroupFixtures.listings,
    MemoryFixtures.listings,
    MemoryFormFixtures.listings,
    MinMaxFixtures.listings,
    SharedMemoryFixtures.listings,
    TensorFixtures.listings,
    AsyncFixtures.listings
  ).reduce(_ ++ _)

  def listing(name: String): String = listings.getOrElse(name, throw IllegalArgumentException(s"missing listing $name"))

  def exists(name: String): Boolean = listings.contains(name)

  def code(name: String): Vec[Byte] = Vec.unsafeWrapArray(bytes.hex(CompilerKernelFixtures.code(name)))

  def copyAddCubin: Vec[Byte] = Vec.unsafeWrapArray(bytes.hex(CompilerKernelFixtures.copyAddCubin))

  /** SAXPY machine code; sm_89 comes from the `-hex` listing of the unoptimised build. */
  def saxpy(sm: Int): Vec[Byte] =
    if (sm != 89) code(s"saxpySm$sm")
    else Listing.words(listing("saxpyO0Sm89"), "saxpy", 89).fold(
      error => throw IllegalStateException(error),
      _.flatMap(_._2)
    )

  private def label(line: String): Option[String] = line.trim match {
    case s".L_x_$index:" if index.nonEmpty && index.forall(_.isDigit) => Some(s".L_x_$index")
    case _                                                            => None
  }

  private def record(line: String): Option[String] = line.trim match {
    case s"/*$address*/$instruction" if address.length == 4 && address.forall(Character.digit(_, 16) >= 0) =>
      Some(instruction.trim)
    case _ => None
  }

  private def word(line: String): Boolean = line.trim match {
    case s"/* 0x$digits */" => digits.length == 16
    case _                  => false
  }

  def normalise(listing: String): String = {
    val lines                           = listing.linesIterator.toVec.dropWhile(!_.startsWith("//---"))
    def boundary(line: String): Boolean = line.isBlank || line.startsWith("//---") || label(line).isDefined
    def padding(line: String): Boolean  = word(line) || record(line).exists(_.startsWith("NOP"))
    @tailrec def strip(index: Int, kept: Vec[String]): Vec[String] =
      if (index >= lines.size) kept
      else {
        val selfBranch = label(lines(index)).exists(name =>
          lines.lift(index + 1).flatMap(record(_)).exists(_.startsWith(s"BRA `($name)"))
        )
        val tail = lines.drop(index + 2).takeWhile(!boundary(_))
        if (selfBranch && tail.forall(padding(_))) strip(index + 2 + tail.size, kept)
        else strip(index + 1, kept :+ lines(index))
      }
    strip(0, Vec.empty).reverse.dropWhile(_.isEmpty).reverse.map(_ + "\n").mkString
  }
}
