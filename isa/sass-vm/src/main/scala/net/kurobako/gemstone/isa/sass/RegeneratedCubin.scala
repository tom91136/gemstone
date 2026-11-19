package net.kurobako.gemstone.isa.sass

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.elf
import net.kurobako.gemstone.vm.Workgroups

/** Cubins whose instructions carry scheduling fields other than the ones the compiler wrote. */
object RegeneratedCubin {

  /** `image` with the scheduling fields of every kernel, and of every function a kernel calls, regenerated from
    * `table`.
    */
  def apply(image: Vec[Byte], table: ControlWords.Table): Either[NonEmpty[String], Vec[Byte]] =
    rewritten(image)((path, _) => ControlWords.regenerate(table, path))

  /** `image` with each path of each kernel's code scheduled by `schedule`, given the path and its instructions.
    * Instructions on no path keep their fields, and a function several kernels call must be scheduled alike from each.
    */
  def rewritten(image: Vec[Byte])(
      schedule: (ControlWords.Path, Vec[DecodedInstruction]) => Either[String, Vec[Scheduling]]
  ): Either[NonEmpty[String], Vec[Byte]] = for {
    module <- Cubin.decode(image)
    parsed <- elf.Image.parse(image)
    program = new LoadedProgram(module, Map.empty, Workgroups.Sequential, _ => None)
    sections <- module.kernels.flatTraverse(kernel => code(module, program, kernel, schedule))
    texts    <- Vec.from(sections.groupMap(_._1)(_._2)).sortBy(_._1).traverse((section, texts) =>
      Either.cond(
        texts.distinct.size == 1,
        section -> texts.head,
        s"kernels schedule section ${module.sections(section).name} differently": NonEmpty[String]
      )
    )
    patched <- texts.foldLeftM(parsed)((image, entry) => image.patched(image.sections(entry._1), entry._2))
  } yield patched.bytes

  private def code(
      module: Cubin.Module,
      program: LoadedProgram,
      kernel: Cubin.Kernel,
      schedule: (ControlWords.Path, Vec[DecodedInstruction]) => Either[String, Vec[Scheduling]]
  ): Either[NonEmpty[String], Vec[(Int, Vec[Byte])]] = {
    def failed(reason: String): NonEmpty[String] = s"${kernel.name}: $reason".unsafe[NonEmpty[String]]
    for {
      linked <- Cubin.link(module, kernel)
      found  <- program.staticCode(kernel.name).left.map(fault => failed(fault.message))
      (graph, uses, instructions) = found
      words <- ControlWords.paths(graph, uses, instructions).flatTraverse { path =>
        val code = instructions.slice(path.first, path.first + path.steps.size)
        schedule(path, code).left.map(reason => failed(s"#${path.first} $reason")).flatMap(scheduled =>
          code.zip(scheduled).traverse((instruction, scheduling) =>
            instruction.encodingWord.toRight(failed(s"no encoding at ${instruction.address}"))
              .map(word => instruction.address.raw -> ControlWords.encode(word, scheduling))
          )
        )
      }
    } yield {
      val text = linked.text.toArray
      words.foreach((address, word) => (0 until 16).foreach(at => text(address + at) = (word >> (8 * at)).toByte))
      linked.sections.map(section =>
        section.index.raw ->
          Vec.from(text.slice(linked.bases(section.index.raw), linked.bases(section.index.raw) + section.bytes.size))
      )
    }
  }
}
