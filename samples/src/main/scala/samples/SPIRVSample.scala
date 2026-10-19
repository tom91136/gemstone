package samples

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.vm.*

/** SPIR-V assembles into a binary module whose instructions are plain values: edit them, then allocate any new result
  * ids above the header's bound.
  */
object SPIRVSample {
  val source: String =
    """OpCapability Addresses
      |OpCapability Kernel
      |OpCapability Int64
      |OpMemoryModel Physical64 OpenCL
      |OpEntryPoint Kernel %main "add" %gid
      |OpDecorate %gid BuiltIn GlobalInvocationId
      |%void = OpTypeVoid
      |%uint = OpTypeInt 32 0
      |%ulong = OpTypeInt 64 0
      |%v3ulong = OpTypeVector %ulong 3
      |%uint_ptr = OpTypePointer CrossWorkgroup %uint
      |%gid_ptr = OpTypePointer Input %v3ulong
      |%gid = OpVariable %gid_ptr Input
      |%fn = OpTypeFunction %void %uint_ptr %uint_ptr
      |%three = OpConstant %uint 3
      |%main = OpFunction %void None %fn
      |%input = OpFunctionParameter %uint_ptr
      |%output = OpFunctionParameter %uint_ptr
      |%entry = OpLabel
      |%ids = OpLoad %v3ulong %gid
      |%index = OpCompositeExtract %ulong %ids 0
      |%source = OpPtrAccessChain %uint_ptr %input %index
      |%target = OpPtrAccessChain %uint_ptr %output %index
      |%value = OpLoad %uint %source
      |%sum = OpIAdd %uint %value %three
      |OpStore %target %sum
      |OpReturn
      |OpFunctionEnd
      |""".stripMargin

  private def words(values: Int*): NonEmpty[Vec[Word]] = Vec.from(values.map(Word(_))).unsafe[NonEmpty[Vec[Word]]]

  private def run(module: Module): Either[String, Vec[Long]] =
    ISA.encode(module).left.map(_.message.raw).flatMap(image =>
      Kernel.run(
        SPIRVBackend(Validation.Environment.OpenCLCompute),
        image,
        "add",
        (input, output) => Vec(KernelTask.Argument.Memory(input, 0L), KernelTask.Argument.Memory(output, 0L))
      )
    )

  /** Every constant whose value is 3, now 5. */
  private def replaceConstant(module: Module): Module =
    module.copy(instructions = module.instructions.map {
      case constant @ Instruction.Constant(_, _, value) if value.words == words(3) =>
        constant.copy(value = value.copy(words = words(5)))
      case other => other
    })

  /** Adds `amount` to the kernel's sum before it is stored, through a new constant and a new `OpIAdd`. */
  private def addToSum(module: Module, amount: Int): Either[String, Module] =
    module.instructions.collectFirst { case sum: Instruction.IAdd => sum }.toRight("the kernel has no add").map { sum =>
      val fresh    = module.freshIds(2)
      val constant = fresh.ids(0)
      val total    = fresh.ids(1)
      val value    = module.instructions.collectFirst { case Instruction.Constant(_, _, value) => value.kind }.get
      val declared =
        Instruction.Constant(sum.resultType, Operand(sum.dest.kind, words(constant)), Operand(value, words(amount)))
      val increment = Instruction.IAdd(
        sum.resultType,
        Operand(sum.dest.kind, words(total)),
        Operand(sum.srcA.kind, sum.dest.words),
        Operand(sum.srcA.kind, words(constant))
      )
      val constants    = module.instructions.lastIndexWhere { case _: Instruction.Constant => true; case _ => false }
      val summed       = Vec(sum, increment)
      val instructions = module.instructions.flatMap {
        case `sum`                                                                     => summed
        case store @ Instruction.Store(_, stored, _) if stored.words == sum.dest.words =>
          Vec(store.copy(src = stored.copy(words = words(total))))
        case other => Vec(other)
      }.patch(constants + 1, Vec(declared), 0)
      fresh.module.copy(instructions = instructions)
    }

  def run(): Either[String, Outcome] = for {
    module      <- asm.Parser.parse(source).left.map(_.message.raw)
    image       <- ISA.encode(module).left.map(_.message.raw)
    original    <- run(module)
    decoded     <- ISA.decode(image).left.map(_.message.raw)
    edited      <- run(replaceConstant(decoded))
    incremented <- addToSum(replaceConstant(decoded), 1)
    extended    <- run(incremented)
  } yield Outcome(original, asm.Printer.render(decoded), edited, extended)
}
