package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** The vertical slice of the real-VM sandbox: assemble a short program, patch an ISA template, run it, and read the PC,
  * register and memory evidence back from the VM's own recording.
  */
class SandboxSuite extends munit.FunSuite {

  private def run(program: SandboxProgram)(using munit.Location): SandboxRun =
    SandboxRunner.run(program).fold(failure => fail(s"${failure.programPage}: ${failure.detail}"), identity)

  private def register(run: SandboxRun, name: String)(using munit.Location): Long =
    run.registers.find(_.name == name).fold(fail(s"no register $name in ${run.registers.map(_.name)}"))(_.bits)

  test("SASS register-only: MOV, IADD3 and EXIT leave R1 and R2, and the PC walks instruction addresses") {
    val result = run(SandboxProgram(
      Language.SASS,
      "MOV R1, 0x2a; IADD3 R2, R1, 0x1, RZ; EXIT;",
      target = Some("sm_50")
    ))
    assertEquals(result.failure, None)
    assert(result.complete)
    assertEquals(result.pcs.distinct, Vec(8L, 16L, 24L))
    assertEquals(register(result, "R1"), 42L)
    assertEquals(register(result, "R2"), 43L)
    assert(result.lines.exists(line => line.address == 8L && line.mnemonic == "MOV"))
  }

  test("SASS branch: BRA skips an instruction and lands on the label") {
    val result = run(SandboxProgram(
      Language.SASS,
      "MOV R1, 0x2a; BRA SKIP; MOV R2, 0x1; SKIP: IADD3 R3, R1, 0x1, RZ; EXIT;",
      target = Some("sm_89")
    ))
    assertEquals(result.failure, None)
    assert(result.complete)
    assertEquals(result.pcs.distinct, Vec(0L, 16L, 48L, 64L))
    assertEquals(register(result, "R1"), 42L)
    assertEquals(register(result, "R2"), 0L)
    assertEquals(register(result, "R3"), 43L)
  }

  test("SASS memory: the sm_89 copy_add template's first parameter stores through STG") {
    val result = run(SandboxProgram(
      Language.SASS,
      "MOV R2, c[0][0x160]; MOV R3, c[0][0x164]; MOV R4, 0x2a; STG.E [R2.64], R4; EXIT;",
      target = Some("sm_89"),
      allocations = Vec(Vec.empty[Byte])
    ))
    assertEquals(result.failure, None)
    assert(result.complete)
    assertEquals(result.pcs.distinct, Vec(0L, 16L, 32L, 48L, 64L))
    assertEquals(register(result, "R4"), 42L)
    assertEquals(result.allocations.size, 2)
    assertEquals(result.bytes(0).take(4), Vec(42.toByte, 0.toByte, 0.toByte, 0.toByte))
  }

  test("AMDGPU register-only: s_mov, s_add and s_endpgm leave s10 and s11") {
    val result = run(SandboxProgram(
      Language.AMDGPU,
      "s_mov_b32 s10, 0x2a\ns_add_u32 s11, s10, 1\ns_endpgm",
      target = Some("gfx900")
    ))
    assertEquals(result.failure, None)
    assert(result.complete)
    assertEquals(result.pcs, Vec(0L, 4L, 8L))
    assertEquals(register(result, "s10"), 42L)
    assertEquals(register(result, "s11"), 43L)
  }

  test("AMDGPU branch: s_branch skips the instruction before the label") {
    val result = run(SandboxProgram(
      Language.AMDGPU,
      "s_mov_b32 s10, 5\ns_branch SKIP\ns_mov_b32 s11, 99\nSKIP:\ns_add_u32 s12, s10, 7\ns_endpgm",
      target = Some("gfx900")
    ))
    assertEquals(result.failure, None)
    assert(result.complete)
    assertEquals(result.pcs, Vec(0L, 4L, 16L, 20L))
    assertEquals(register(result, "s10"), 5L)
    assertEquals(register(result, "s11"), 0L)
    assertEquals(register(result, "s12"), 12L)
  }

  test("AMDGPU memory: a global_store through the kernarg pointer writes the first allocation") {
    val result = run(SandboxProgram(
      Language.AMDGPU,
      """s_load_dwordx2 s[6:7], s[4:5], 0x0
        |s_mov_b32 s8, 0x2a
        |v_mov_b32 v0, s8
        |v_mov_b32 v1, 0
        |global_store_dword v1, v0, s[6:7]
        |s_endpgm""".stripMargin,
      target = Some("gfx900"),
      allocations = Vec(Vec.empty[Byte])
    ))
    assertEquals(result.failure, None)
    assert(result.complete)
    assertEquals(result.pcs, Vec(0L, 8L, 12L, 16L, 20L, 28L))
    assertEquals(register(result, "v0"), 42L)
    assertEquals(result.allocations.size, 3)
    assertEquals(result.bytes(0).take(4), Vec(42.toByte, 0.toByte, 0.toByte, 0.toByte))
  }

  test("PTX register-only: mov and add leave %r0 and %r1") {
    val result = run(SandboxProgram(Language.PTX, "  mov.u32 %r0, 42;\n  add.u32 %r1, %r0, 1;"))
    assertEquals(result.failure, None)
    assert(result.complete)
    assertEquals(result.pcs, Vec(0L, 1L, 2L))
    assertEquals(register(result, "%r0"), 42L)
    assertEquals(register(result, "%r1"), 43L)
  }

  test("PTX branch: bra skips the instruction before its label") {
    val result = run(SandboxProgram(
      Language.PTX,
      "  mov.u32 %r0, 5;\n  bra SKIP;\n  mov.u32 %r1, 99;\nSKIP:\n  add.u32 %r2, %r0, 7;"
    ))
    assertEquals(result.failure, None)
    assert(result.complete)
    assertEquals(result.pcs, Vec(0L, 1L, 3L, 4L))
    assertEquals(register(result, "%r1"), 0L)
    assertEquals(register(result, "%r2"), 12L)
  }

  test("PTX memory: an out parameter store writes the first allocation") {
    val result = run(SandboxProgram(
      Language.PTX,
      "  ld.param.u64 %rd0, [out];\n  mov.u32 %r0, 42;\n  st.global.u32 [%rd0], %r0;",
      parameters = Vec(SandboxProgram.Parameter("out", SandboxProgram.Parameter.Kind.Pointer)),
      allocations = Vec(Vec.empty[Byte])
    ))
    assertEquals(result.failure, None)
    assert(result.complete)
    assertEquals(result.pcs, Vec(0L, 1L, 2L, 3L))
    assertEquals(register(result, "%r0"), 42L)
    assertEquals(result.allocations.size, 1)
    assertEquals(result.bytes(0).take(4), Vec(42.toByte, 0.toByte, 0.toByte, 0.toByte))
  }

  test("PTX parameters: initial allocation bytes and scalar arguments reach the kernel and come back") {
    val result = run(SandboxProgram(
      Language.PTX,
      """  ld.param.u64 %rd0, [out];
        |  ld.param.u32 %r0, [value];
        |  ld.global.u32 %r1, [%rd0];
        |  add.u32 %r2, %r0, %r1;
        |  st.global.u32 [%rd0], %r2;""".stripMargin,
      parameters = Vec(
        SandboxProgram.Parameter("out", SandboxProgram.Parameter.Kind.Pointer),
        SandboxProgram.Parameter("value", SandboxProgram.Parameter.Kind.Scalar(32))
      ),
      allocations = Vec(Vec[Byte](41, 0, 0, 0)),
      scalars = Vec(Vec[Byte](1, 0, 0, 0))
    ))
    assertEquals(result.failure, None)
    assert(result.complete)
    assertEquals(register(result, "%r0"), 1L)
    assertEquals(register(result, "%r1"), 41L)
    assertEquals(register(result, "%r2"), 42L)
    assertEquals(result.bytes(0).take(4), Vec(42.toByte, 0.toByte, 0.toByte, 0.toByte))
  }

  test("SPIR-V branch/phi/return: labels, predecessors, call depth and final memory") {
    val result = run(SandboxProgram(
      Language.SPIRV,
      SandboxSPIRVModules.branch,
      kernel = Some("branch"),
      allocations = Vec(Vec.empty[Byte])
    ))
    assertEquals(result.failure, None)
    assert(result.complete)
    val facade = result.spirv.getOrElse(fail("no SPIR-V facade evidence"))
    assert(facade.steps.map(_.label).distinct.size >= 5, s"labels: ${facade.steps.map(_.label)}")
    assert(facade.steps.exists(_.predecessor >= 0), "no edge recorded a predecessor")
    assertEquals(facade.steps.map(_.callDepth).max, 1)
    assert(facade.steps.exists(_.callDepth == 1), "the helper never ran")
    assertEquals(result.spirvSteps.map(_.label), facade.steps.map(_.label))
    assertEquals(result.spirvSteps.map(_.predecessor), facade.steps.map(_.predecessor))
    assertEquals(result.pcs, facade.pcs.map(_.toLong))
    assert(facade.values.exists(_._2 == InvocationView.Value.Bits(Vec(20L))), s"values: ${facade.values}")
    assert(result.registers.nonEmpty)
    assert(result.lines.exists(_.address == 0L))
    assertEquals(LittleEndian.decode(result.bytes(0), 0, 4), 20L)

    val evidence = SandboxEvidence.report(result)
    assert(evidence.executed.nonEmpty)
    assert(evidence.registers.nonEmpty)
    assert(evidence.memory.nonEmpty)
  }

  test("SPIR-V OpControlBarrier: four invocations block, resume and retire") {
    val result = run(SandboxProgram(
      Language.SPIRV,
      SandboxSPIRVModules.barrier,
      kernel = Some("barrier"),
      allocations = Vec(Vec.empty[Byte]),
      group = Dimensions(4, 1, 1)
    ))
    assertEquals(result.failure, None)
    assert(result.complete)
    val facade = result.spirv.getOrElse(fail("no SPIR-V facade evidence"))
    assertEquals(facade.blocked.size, 4)
    assertEquals(facade.resumed.size, 4)
    assert(facade.transitions.exists(_.kind == MachineEvent.Kind.Retired))
    assertEquals(facade.steps.map(_.ordinal).distinct.size, 4)
    assertEquals(result.trace.changes, facade.transitions)
    assertEquals(LittleEndian.decode(result.bytes(0), 0, 4), 1L)
  }

  test("SPIR-V assembly and load failures map to typed sandbox failures") {
    val assembly =
      SandboxRunner.run(SandboxProgram(Language.SPIRV, "OpDefinitelyNotAnInstruction")).swap.toOption.get
    assembly match {
      case SandboxFailure.Assembly(Language.SPIRV, detail) => assert(detail.nonEmpty)
      case other                                           => fail(s"expected a SPIR-V assembly failure, found $other")
    }
    assertEquals(assembly.programPage, "Program page (SPIR-V)")

    val load = SandboxRunner.run(SandboxProgram(
      Language.SPIRV,
      """OpCapability Shader
        |OpMemoryModel Logical GLSL450
        |OpEntryPoint Kernel %main "wrong-model"
        |%void = OpTypeVoid
        |%fn = OpTypeFunction %void
        |%main = OpFunction %void None %fn
        |%entry = OpLabel
        |OpReturn
        |OpFunctionEnd
        |""".stripMargin
    )).swap.toOption.get
    load match {
      case SandboxFailure.Load(Language.SPIRV, detail) => assert(detail.nonEmpty)
      case other                                       => fail(s"expected a SPIR-V load failure, found $other")
    }
  }

  test("assembly failures are typed and point at the Program page instead of emulating") {
    val failure =
      SandboxRunner.run(SandboxProgram(Language.SASS, "NOT_AN_INSTRUCTION R1", Some("sm_50"))).swap.toOption.get
    failure match {
      case SandboxFailure.Assembly(Language.SASS, detail) => assert(detail.nonEmpty)
      case other                                          => fail(s"expected a SASS assembly failure, found $other")
    }
    assertEquals(failure.programPage, "Program page (SASS)")
  }

  test("SPIR-V modules without an entry point are a typed setup failure") {
    val failure = SandboxRunner.run(SandboxProgram(Language.SPIRV, "OpNop")).swap.toOption.get
    failure match {
      case SandboxFailure.Setup(Language.SPIRV, detail) => assert(detail.nonEmpty)
      case other                                        => fail(s"expected SPIR-V setup failure, found $other")
    }
    assertEquals(failure.programPage, "Program page (SPIR-V)")
  }
}
