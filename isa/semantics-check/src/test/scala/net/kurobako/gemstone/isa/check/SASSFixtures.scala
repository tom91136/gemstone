package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.asm.*

object SASSFixtures {

  val target = "sm_89"
  val kernel = "intchain"

  def cubin(name: String): Vec[Byte] =
    Vec.unsafeWrapArray(
      getClass.getResourceAsStream(s"/net/kurobako/gemstone/isa/sass/controlwords/$name").readAllBytes()
    )

  private val source =
    """IADD3 R2, R2, 0x1, RZ ;
      |IADD3 R4, R2, 0x2, RZ ;
      |IMAD R6, R2, R2, RZ ;
      |ISETP.GT.U32.AND P0, PT, R6, 0x10, PT ;
      |@P0 EXIT ;
      |EXIT ;
      |""".stripMargin

  /** `intchain` with its code replaced by a block that no load or store reaches, whose second instruction writes a
    * register nothing reads.
    */
  lazy val arithmetic: Vec[Byte] = {
    val selected = Target.parse(target).fold(error => throw IllegalStateException(error), identity)
    val program  = Parser.parseProgram(source).fold(error => throw IllegalStateException(error.toString), identity)
    val code     =
      new Assembler(selected).encode(program).fold(error => throw IllegalStateException(error.toString), identity)
    Cubin.replaceKernel(cubin("intchain.sm_89.cubin"), kernel.unsafe[NonEmpty[String]], code)
      .fold(error => throw IllegalStateException(error.raw), identity)
  }

  /** The window over the first four instructions of [[arithmetic]]. */
  def first(image: Vec[Byte]): Window =
    SASSWindows.of(image, kernel, target).fold(error => throw IllegalStateException(error), identity)
      .find(window => window.first == 0 && window.last >= 3)
      .getOrElse(throw IllegalStateException("no window over the first block"))

  val launch: AMDGPUBlockCost.Launch = AMDGPUBlockCost.Launch.Stated(32.unsafe[Int > 0], 0L.unsafe[Long >= 0], None)
}
