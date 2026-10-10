package samples

class SamplesSuite extends munit.FunSuite {
  for ((isa, sample) <- Samples.all)
    test(s"$isa: the assembled kernel adds 3, the edited one 5, and the extended one 6") {
      sample() match {
        case Left(error)    => fail(error)
        case Right(outcome) =>
          assertEquals(outcome.assembled, Kernel.inputs.map(_ + 3))
          assertEquals(outcome.edited, Kernel.inputs.map(_ + 5))
          assertEquals(outcome.extended, Kernel.inputs.map(_ + 6))
          assert(outcome.disassembly.nonEmpty)
      }
    }
}
