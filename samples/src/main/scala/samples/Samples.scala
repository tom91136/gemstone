package samples

/** Assembles a small kernel for each ISA, runs it, disassembles it, changes its constant and adds an instruction. */
object Samples {
  val all: Vector[(String, () => Either[String, Outcome])] =
    Vector(
      "AMDGPU" -> (() => AMDGPUSample.run()),
      "PTX"    -> (() => PTXSample.run()),
      "SASS"   -> (() => SASSSample.run()),
      "SPIR-V" -> (() => SPIRVSample.run())
    )

  def main(arguments: Array[String]): Unit = all.foreach { (isa, sample) =>
    println(s"== $isa")
    sample() match {
      case Left(error)    => println(s"failed: $error")
      case Right(outcome) =>
        println(s"assembled: ${outcome.assembled.mkString(" ")}")
        println(outcome.disassembly)
        println(s"constant changed: ${outcome.edited.mkString(" ")}")
        println(s"instruction added: ${outcome.extended.mkString(" ")}")
    }
  }
}
