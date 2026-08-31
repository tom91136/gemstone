package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.isa.spirv.v1_6.Endianness
import net.kurobako.gemstone.isa.spirv.v1_6.Header
import net.kurobako.gemstone.isa.spirv.v1_6.ISA as SPIRV
import net.kurobako.gemstone.isa.spirv.v1_6.Instruction
import net.kurobako.gemstone.isa.spirv.v1_6.Module
import net.kurobako.gemstone.isa.spirv.v1_6.Operand
import net.kurobako.gemstone.isa.spirv.v1_6.Word
import net.kurobako.gemstone.memory.AllocationKind

import scala.annotation.tailrec

final class SPIRVProgramExecutorSuite extends munit.FunSuite {
  test("the external SPIR-V load boundary does not treat non-kernel entry points as libraries") {
    val voidType     = operand("IdResult", 1)
    val functionType = operand("IdResult", 2)
    val function     = operand("IdResult", 3)
    val module       = Module(
      Header(SPIRV.version, Word(0), Word(5), Word(0)),
      Col(
        Instruction.Capability(operand("Capability", 6)),
        Instruction.MemoryModel(operand("AddressingModel", 2), operand("MemoryModel", 2)),
        Instruction.TypeVoid(voidType),
        Instruction.TypeFunction(functionType, voidType, Col.empty),
        Instruction.EntryPoint(operand("ExecutionModel", 0), function, stringOperand("vertex"), Col.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
        Instruction.Label(operand("IdResult", 4)),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )

    ISATypedProgramExecutor.default.load(
      ProgramId.fromLong(54L).toOption.get,
      ProgramFormat.SPIRV,
      SPIRV.encode(module).toOption.get
    ) match {
      case Left(ExecutionFailure.InvalidRequest(detail)) => assert(detail.contains("OpEntryPoint Kernel"))
      case result => fail(s"expected kernel entry-point validation failure, found $result")
    }
  }

  test("the external SPIR-V load boundary rejects modules outside the OpenCL compute environment") {
    val voidType     = operand("IdResult", 1)
    val functionType = operand("IdResult", 2)
    val function     = operand("IdResult", 3)
    val module       = Module(
      Header(SPIRV.version, Word(0), Word(5), Word(0)),
      Col(
        Instruction.TypeVoid(voidType),
        Instruction.TypeFunction(functionType, voidType, Col.empty),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("invalid_environment"), Col.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
        Instruction.Label(operand("IdResult", 4)),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val executor = ISATypedProgramExecutor.default

    executor.load(ProgramId.fromLong(55L).toOption.get, ProgramFormat.SPIRV, SPIRV.encode(module).toOption.get) match {
      case Left(ExecutionFailure.InvalidRequest(detail)) => assert(detail.contains("OpCapability Kernel"))
      case result => fail(s"expected OpenCL environment validation failure, found $result")
    }
  }

  test("typed SPIR-V executes through the binary program service boundary") {
    val voidType    = operand("IdResult", 1)
    val u32         = operand("IdResult", 2)
    val pointerType = operand("IdResult", 3)
    val function    = operand("IdResult", 5)
    val output      = operand("IdResult", 6)
    val value       = operand("IdResult", 7)
    val module      = Module(
      Header(SPIRV.version, Word(0), Word(16), Word(0)),
      Col(
        Instruction.Capability(operand("Capability", 6)),
        Instruction.MemoryModel(operand("AddressingModel", 2), operand("MemoryModel", 2)),
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypePointer(pointerType, operand("StorageClass", 5), u32),
        Instruction.TypeFunction(operand("IdResult", 4), voidType, Col(pointerType, u32)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("store"), Col.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), operand("IdRef", 4)),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(u32, value),
        Instruction.Label(operand("IdResult", 8)),
        Instruction.Store(output, value, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val image      = SPIRV.encode(module).toOption.get
    val service    = ExecutionService(ISATypedProgramExecutor.default)
    val allocation = AllocationId.fromLong(9L).toOption.get
    assertEquals(service.registerAllocation(allocation, 4L, AllocationKind.Shared), Right(()))
    val program  = service.load(ProgramFormat.SPIRV, image).toOption.get
    val dispatch = KernelDispatch
      .create(
        program,
        "store",
        Dimensions.One,
        Dimensions.One,
        Col(
          KernelArgument.Allocation(allocation, 0L),
          KernelArgument.Scalar(Col[Byte](0x78, 0x56, 0x34, 0x12))
        )
      )
      .toOption
      .get

    val completion = service.launch(dispatch).toOption.get
    assertEquals(awaitCompletion(service, completion), Right(CompletionStatus.Succeeded))
    assertEquals(service.read(allocation, 0L, 4), Right(Col[Byte](0x78, 0x56, 0x34, 0x12)))
  }

  private def awaitCompletion(
      service: ExecutionService,
      completion: CompletionId,
      deadline: Long = System.nanoTime() + 5_000_000_000L
  ): Either[ExecutionFailure, CompletionStatus] = {
    @tailrec def loop(): Either[ExecutionFailure, CompletionStatus] = service.completion(completion) match {
      case pending @ Right(CompletionStatus.Pending) if System.nanoTime() < deadline =>
        Thread.sleep(1L)
        loop()
      case result => result
    }
    loop()
  }

  private def operand(kind: String, values: Int*): Operand = Operand(kind, Col.from(values.map(Word(_))))

  private def stringOperand(value: String): Operand = {
    val bytes = value.getBytes(java.nio.charset.StandardCharsets.UTF_8) :+ 0.toByte
    val words = bytes.grouped(4).map { group =>
      Word(LittleEndian.decode(group, 0, group.length).toInt)
    }
    Operand("LiteralString", Col.from(words))
  }
}
