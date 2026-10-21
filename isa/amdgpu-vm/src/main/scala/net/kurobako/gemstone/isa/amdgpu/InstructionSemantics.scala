package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.semantics.render.*

/** The staged semantics one AMDGPU instruction lowers to on a target, rendered through the dispatch execution uses as a
  * definition of what it does to one lane.
  */
object InstructionSemantics {

  /** The modifiers that, one at a time, give the valid variants of the matrix instruction `mnemonic` on `target`: the
    * broadcasts its blocks allow, B's lane permutations, f64 negations, the formats of f8f6f4, the index sets of a
    * sparse A, scale selections, and WMMA's negations, signedness, clamp and result half; `None` for others. They are
    * spelt as LLVM spells them, f64 negations as `neg` and index sets as `index_key`.
    */
  def matrixModifiers(target: Target, mnemonic: String): Option[Vec[String]] =
    MatrixShape.of(mnemonic, target.isa, TargetDefaults.waveSize(target)).toOption.map { shape =>
      def selected(name: String, bits: Int): Vec[String] =
        Vec.tabulate(bits)(bit => Vec.tabulate(3)(at => if (at == bit) 1 else 0).mkString(s"$name:[", ",", "]"))
      val sets = 32 / (2 * shape.aItems)
      val keys = if (shape.sparse) Vec.tabulate(sets - 1)(set => s"index_key:${set + 1}") else Vec.empty[String]
      shape.layout match {
        case MatrixLayout.Core if mnemonic.endsWith("_f8f6f4") =>
          Vec.tabulate(4)(format => s"cbsz:${format + 1}") ++ Vec.tabulate(4)(format => s"blgp:${format + 1}") ++
            (if (mnemonic.startsWith("v_mfma_scale_")) selected("op_sel", 2) ++ selected("op_sel_hi", 2)
             else Vec.empty[String])
        case MatrixLayout.Core if shape.sparse => Vec("cbsz:1") ++ Vec.tabulate(sets - 1)(set => s"abid:${set + 1}")
        case MatrixLayout.Core if shape.c == MatrixFormat.F64 =>
          if (target.isa == ISA.CDNA2) Vec.empty[String]
          else Vec.tabulate(7)(bits => Vec.tabulate(3)(at => ((bits + 1) >> at) & 1).mkString("neg:[", ",", "]"))
        case MatrixLayout.Core =>
          Vec.tabulate(Integer.numberOfTrailingZeros(shape.blocks))(size =>
            s"cbsz:${size + 1} abid:${(1 << (size + 1)) - 1}"
          ) ++ Vec.tabulate(7)(control => s"blgp:${control + 1}")
        case _ if shape.a.integer =>
          selected("neg_lo", 2) ++ Vec("clamp") ++
            keys
        case _ =>
          (if (shape.a.width == 8) Vec.empty[String] else selected("neg_lo", 3) ++ selected("neg_hi", 3)) ++
            (if (shape.halfRegisters) Vec("op_sel:[0,0,1]") else Vec.empty[String]) ++
            keys
      }
    }

  /** The definition named `name` of what the assembly `text` of one instruction does on `target`, or why there is none:
    * it does not parse, assemble or, given `encoding`, assemble to that catalogue encoding; execution rejects it or
    * runs it with no staged semantics; or its semantics do not form a definition. A register, register range, literal
    * or piece of implicit state whose spelling, such as `s1`, `v[2:3]`, `0x1234` or `scc`, is a key of `operands` takes
    * its value as name; others keep their spelling. Parameters follow the names in `order`, then the order they are
    * first read in.
    */
  def describe(
      target: Target,
      text: String,
      operands: Map[String, String],
      order: Vec[String] = Vec.empty,
      name: Option[String] = None,
      encoding: Option[Metadata.Encoding] = None
  ): Either[Unrendered, Definition] = for {
    program <-
      asm.Parser.parse(text).left.map(error => Unrendered.Malformed(s"$text does not parse: ${error.diagnostic.raw}"))
    code <- asm.Assembler.encode(program, TargetMode(target)).left.map(error =>
      Unrendered.Malformed(s"$text does not assemble: $error")
    )
    found = encoded(code, target)
    _ <- encoding.filter(expected => found.nonEmpty && !found.exists(same(_, expected))).toLeft(()).left.map(_ =>
      Unrendered.Malformed(
        s"$text assembles to ${found.map(value => s"${value.isa} ${value.format}").mkString(" and ")} instead."
      )
    )
    instruction <-
      VMCompiler.decodeInstruction(code, target).left.map(error => Unrendered.Rejected(s"$text: ${error.raw}"))
    naming = RenderContext.Names(operands.get, TargetDefaults.waveSize(target), Some(target.isa))
    rendered   <- LaneRenderer.describe(instruction, naming).toRight(Unrendered.Unstaged(Vec(mechanics(instruction))))
    definition <- Definition.from(
      rendered,
      name.getOrElse(text.takeWhile(!_.isWhitespace)),
      naming,
      order,
      settle = true
    ).left.map(why => Unrendered.Undefined(s"$text has no definition: $why"))
  } yield definition

  /** Whether a decoded encoding is the expected one; the decoder reports an instruction with a literal under the format
    * without it.
    */
  private def same(found: Metadata.Encoding, expected: Metadata.Encoding): Boolean =
    found.isa == expected.isa && found.opcode == expected.opcode &&
      (found.format == expected.format ||
        found.format.withLiteral(4).contains(expected.format) || found.format.withLiteral(8).contains(expected.format))

  /** The catalogue encodings of the first instruction in `code`: each half of a dual-issue pair, the one with a literal
    * operand where the pair carries a literal, or the encoding.
    */
  private def encoded(code: Vec[Byte], target: Target): Vec[Metadata.Encoding] = {
    val bytes = Vec.toArray(code)
    InstructionDecoder.dual(bytes, 0, target).flatMap(_.toOption)
      .map(dual =>
        Vec(dual.x, dual.y).flatMap(half =>
          half.map(_._2).find(_.operands.exists(_.field.contains(EncodingField.LITERAL)) == dual.literal.nonEmpty)
            .orElse(half.headOption.map(_._2))
        )
      )
      .getOrElse(InstructionDecoder.decode(bytes, 0, target).toOption.map(_.encoding).toVec)
  }

  /** What execution does with an instruction that has no per-lane definition. */
  private def mechanics(instruction: Instruction): String = instruction match {
    case _: (Instruction.Branch | Instruction.BranchRelative | Instruction.SSetPc | Instruction.SSwapPc) =>
      "a transfer of control"
    case _: (Instruction.Call | Instruction.CallRelative) => "a call, which saves the return address"
    case _: (Instruction.ForkIndirect | Instruction.ForkRelative | Instruction.Fork) =>
      "a fork of EXEC, which defers one side on the conditional branch stack"
    case Instruction.Join(_)             => "a join, which resumes a side deferred on the conditional branch stack"
    case Instruction.SetVectorSkip(_, _) =>
      "a MODE write, after which vector instructions are skipped while VSKIP is set"
    case Instruction.Halt(reason)                                 => s"a halt, which faults: $reason"
    case _: (Instruction.BarrierSignal | Instruction.BarrierWait) => "a workgroup barrier"
    case _: (Instruction.BarrierInit | Instruction.BarrierJoin)   => "a named barrier's set-up"
    case Instruction.BarrierLeave                                 => "a named barrier's set-up"
    case Instruction.Barrier                                      => "a workgroup barrier"
    case Instruction.End                                          => "the end of the wave"
    case Instruction.Trap(_)                                      => "a trap, which faults"
    case Instruction.Wait => "nothing a lane observes: a wait, hint or synchronous cache operation"
    case Instruction.CacheInvalidate | Instruction.CacheWritebackInvalidate =>
      "cache maintenance, after which loads read memory again"
    case Instruction.SSetReg | _: (Instruction.SSetMode | Instruction.SGetMode) => "a hardware register access"
    case Instruction.SGprIndexOff                                               => "a VGPR indexing mode switch"
    case Instruction.SMoveRelative(dest = _)         => "a move between SGPRs that M0 offsets"
    case Instruction.LocalCounter(decrement = false) => "an LDS append counter, which the wave updates once"
    case Instruction.LocalCounter(decrement = true)  => "an LDS consume counter, which the wave updates once"
    case Instruction.GlobalWaveSync(operation = GlobalWaveSyncOperation.Barrier) =>
      "a GWS barrier, which only a cooperative dispatch has the resources for"
    case Instruction.GlobalWaveSync(name = _) =>
      "a GWS semaphore operation, which only a cooperative dispatch has the resources for"
    case Instruction.Sleep => "a sleep, during which the other workgroups of a cooperative dispatch run"
    case Instruction.SReadClock(clock = Clock.Shader)       => "a read of the shader clock"
    case Instruction.SReadClock(clock = Clock.ShaderCycles) => "a read of the shader cycle counter"
    case Instruction.SReadClock(clock = Clock.RealTime)     => "a read of the real-time clock"
    case Instruction.SGetPc(dest = _)                       => "a read of the program counter"
    case Instruction.SReadWaveId(dest = _)                  => "a read of the wave's index among its workgroup's waves"
    case _: (Instruction.SubvectorLoopRelative | Instruction.SubvectorLoop) =>
      "a loop over the halves of a wave64's lanes, which runs its body once for each half that has active lanes"
    case Instruction.Load(address = MemoryAddress.Buffer(resource = _)) =>
      "a buffer load through a resource descriptor, zero outside its range"
    case Instruction.Store(address = MemoryAddress.Buffer(resource = _)) =>
      "a buffer store through a resource descriptor, dropped outside its range"
    case Instruction.Atomic(address = MemoryAddress.Buffer(resource = _)) |
        Instruction.CompareSwap(address = MemoryAddress.Buffer(resource = _)) =>
      "a buffer atomic through a resource descriptor, skipped outside its range"
    case Instruction.Atomic(address = MemoryAddress.Texel(_)) =>
      "an image atomic on the texel a resource descriptor addresses"
    case Instruction.CompareSwap(address = MemoryAddress.Texel(_)) =>
      "an image atomic on the texel a resource descriptor addresses"
    case Instruction.FormatLoad(_, _, _, _, _, _)     => "a typed buffer load converting texels from their data format"
    case Instruction.FormatStore(_, _, _, _, _, _)    => "a typed buffer store converting texels to their data format"
    case Instruction.LoadLocal(_, _, _, _)            => "a buffer load into LDS"
    case Instruction.StoreLocal(_, _)                 => "a buffer store from LDS"
    case Instruction.SBufferLoad(_, _, _, _, _, _, _) => "a scalar load through a buffer resource descriptor"
    case Instruction.Image(_, _, _, _, _, _) => "an image access converting texels through a resource descriptor"
    case Instruction.WithStatus(access, _)   => s"${mechanics(access)}, then a zero TFE status"
    case other                               => s"${other.productPrefix} execution"
  }
}
