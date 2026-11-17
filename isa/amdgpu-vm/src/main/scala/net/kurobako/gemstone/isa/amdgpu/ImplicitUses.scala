package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.vm.RegisterUse

/** The implicit state the instruction at an offset of a code stream reads and writes: the implicit operands its
  * catalogue encoding lists with their direction, EXEC for every encoding that runs per lane, and, for an SDWA
  * encoding, the state it writes, as it keeps the bits of the lanes it leaves out.
  */
private object ImplicitUses {
  private def scalar(format: EncodingFormat): Boolean =
    format.toString.startsWith("SOP") || format.toString.startsWith("SMEM")

  private def state(info: OperandInfo): Option[String] =
    if (info.implicitOperand || info.kind == OperandKind.VCC || info.kind == OperandKind.EXEC)
      ImplicitState.of(info.kind)
    else None

  private def encodings(stream: CodeStream, offset: Int): Vec[Metadata.Encoding] =
    InstructionDecoder.dual(stream, offset).flatMap(_.toOption).fold(
      InstructionDecoder.decode(stream, offset).toOption.map(_.encoding).toVec
    )(dual => Vec(dual.x, dual.y).flatMap(_.headOption.map(_._2)))

  def of(stream: CodeStream, offset: Int, waveSize: Int): RegisterUse = {
    val found  = encodings(stream, offset)
    val states = found.flatMap(encoding =>
      encoding.operands.flatMap(info =>
        state(info).toVec.flatMap { name =>
          if (info.output && encoding.format.isSdwa) Vec(name -> true, name -> false) else Vec(name -> info.output)
        }
      )
    )
    val exec = if (found.exists(encoding => !scalar(encoding.format))) Vec(RenderContext.Exec) else Vec.empty[String]
    def units(names: Vec[String]): Vec[String] = names.distinct.flatMap(ImplicitState.units(_, waveSize))
    RegisterUse(
      units(states.collect { case (name, false) => name } ++ exec),
      units(states.collect { case (name, true) => name })
    )
  }
}
