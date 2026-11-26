package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
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

  /** `search/typed.c` as `search/compile.sh` builds it: `scale(float *out, const float *in, const int *count, double
    * factor)`, whose cubin marks its pointers.
    */
  lazy val typed: Vec[Byte] = {
    val stream = getClass.getResourceAsStream("search/typed.sm_89.cubin")
    try Vec.from(stream.readAllBytes())
    finally stream.close()
  }

  private val source =
    """IADD3 R2, R2, 0x1, RZ ;
      |IADD3 R4, R2, 0x2, RZ ;
      |IMAD R5, R2, R2, RZ ;
      |ISETP.GT.U32.AND P0, PT, R5, 0x10, PT ;
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

  val loops = "branches.sm_89.cubin"
  val sum   = "sumloop"

  /** `image` with the window's instructions replaced, laid out again, and every control word regenerated, as a launch
    * of the rewritten kernel would run it.
    */
  def rewritten(image: Vec[Byte], function: String, window: Window, replacement: Vec[DecodedInstruction]): Vec[Byte] = {
    val selected = Target.parse(target).fold(error => throw IllegalStateException(error), identity)
    val code     = SASSModel.staticCode(image, function).fold(error => throw IllegalStateException(error), _._3)
    def statement(instruction: DecodedInstruction) =
      Program.Statement.Operation(instruction.operation, instruction.encodingWord.map(_ => instruction))
    val program = Program(
      Some(selected),
      (code.take(window.first) ++ replacement ++ code.drop(window.last + 1)).map(statement)
    )
    val assembler = new Assembler(selected)
    val bytes     = assembler.relaid(program).flatMap(assembler.encode(_))
      .fold(error => throw IllegalStateException(error.toString), identity)
    val replaced = Cubin.replaceKernel(image, function.unsafe[NonEmpty[String]], bytes)
      .fold(error => throw IllegalStateException(error.raw), identity)
    RegeneratedCubin(replaced, ControlWords.tables("sm_89"))
      .fold(error => throw IllegalStateException(error.raw), identity)
  }
}
