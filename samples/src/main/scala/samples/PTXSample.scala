package samples

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.ptx.*
import net.kurobako.gemstone.isa.ptx.Program.*
import net.kurobako.gemstone.vm.*

/** PTX is text: parse it into the instruction model, edit the model, and print it back for the backend to load. */
object PTXSample {
  val source: String =
    """.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry add(.param .u64 input, .param .u64 output) {
      |  .reg .b32 %r<3>;
      |  .reg .b64 %rd<5>;
      |  ld.param.u64 %rd0, [input];
      |  ld.param.u64 %rd1, [output];
      |  mov.u32 %r0, %tid.x;
      |  mul.wide.u32 %rd2, %r0, 4;
      |  add.u64 %rd3, %rd0, %rd2;
      |  add.u64 %rd4, %rd1, %rd2;
      |  ld.global.u32 %r1, [%rd3];
      |  add.u32 %r2, %r1, 3;
      |  st.global.u32 [%rd4], %r2;
      |  ret;
      |}
      |""".stripMargin

  private def run(program: Program): Either[String, Vec[Long]] =
    Kernel.run(
      PTXBackend(),
      Vec.from(asm.Printer.render(program).getBytes(StandardCharsets.UTF_8)),
      "add",
      (input, output) => Vec(KernelTask.Argument.Memory(input, 0L), KernelTask.Argument.Memory(output, 0L))
    )

  /** Every `add` of the immediate `from`, now adding `to`. */
  private def replaceConstant(program: Program, from: String, to: NonEmpty[String]): Program =
    program.mapInstructions {
      case add @ Instruction.Add(_, _, Operand.Immediate(raw), _, _) if raw.raw == from =>
        add.copy(srcB = Operand.Immediate(to))
      case other => other
    }

  /** `extra` placed after each `add` of an immediate. */
  private def insertAfterAdd(program: Program, extra: Instruction): Program = program.mapBodies(_.flatMap {
    case statement @ Statement.Instruction(Instruction.Add(_, _, Operand.Immediate(_), _, _)) =>
      Vec(statement, Statement.Instruction(extra))
    case statement => Vec(statement)
  })

  def run(): Either[String, Outcome] = for {
    program  <- Parser.parse(source).left.map(_.message.raw)
    original <- run(program)
    edited   <- run(replaceConstant(program, "3", "5"))
    add      <- program.statements.collectFirst { case Statement.Entry(body = Some(body)) =>
      body.collectFirst { case Statement.Instruction(add @ Instruction.Add(_, _, Operand.Immediate(_), _, _)) => add }
    }.flatten.toRight("the kernel has no add")
    extended <- run(insertAfterAdd(
      replaceConstant(program, "3", "5"),
      add.copy(srcA = add.dest, srcB = Operand.Immediate("1"))
    ))
  } yield Outcome(original, asm.Printer.render(program), edited, extended)
}
