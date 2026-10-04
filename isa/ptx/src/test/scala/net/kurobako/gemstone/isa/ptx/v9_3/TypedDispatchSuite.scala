package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction as VMI

class TypedDispatchSuite extends munit.FunSuite {
  private def compileInstructions(body: String) = VMCompiler.compile(s""".version 8.0
.target sm_80
.address_size 64
.visible .entry typed_dispatch(.param .u32 input, .param .u32 output) {
.reg .u64 %rd0;
.reg .u32 %r0, %r1;
.reg .pred %p0, %p1;
$body
ret;
}""").map(_.program.instructions.map(_.instruction))

  test("integer immediate boundaries retain PTX sign, radix, suffix, and unsigned range handling") {
    def immediate(raw: NonEmpty[String]): Either[VMCompiler.Error, Value] =
      VMCompiler.value(Operand.Immediate(raw))

    assertEquals(immediate("-9223372036854775808"), Right(Value.Immediate(Long.MinValue)))
    assertEquals(immediate("0xffffffffffffffffULL"), Right(Value.Immediate(-1L)))
    assertEquals(immediate("0b101u"), Right(Value.Immediate(5L)))
    assertEquals(immediate("0f3f800000"), Right(Value.Immediate(0x3f800000L)))
    assertEquals(immediate("--1"), Right(Value.Immediate(-1L)))
    assert(immediate("0x10000000000000000").isLeft)
  }

  test("duplicate and conflicting mov type tokens remain unsupported") {
    Vec(
      "mov.u32.u32 %r0, 1;",
      "mov.f64.u32 %rd0, %rd0;",
      "mov.u32.f64 %rd0, %rd0;",
      "atom.global.add.add.u32 %r0, [%rd0], 1;",
      "cvta.global.global.u64 %rd0, %rd0;",
      "cvta.global.to.u64 %rd0, %rd0;",
      "cvta.global.local.u64 %rd0, %rd0;",
      "vote.sync.all.any.pred %p0, %p1, 0xffffffff;"
    )
      .foreach { statement =>
        assert(compileInstructions(statement).isLeft, clues(statement))
      }
  }

  test("an exact atomic modifier sequence still lowers") {
    val instructions = compileInstructions("atom.global.add.u32 %r0, [%rd0], 1;").toOption.get
    assert(instructions.exists {
      case _: VMI.AtomicAdd => true
      case _                => false
    })
  }

  test("atomic ordering and scope qualifiers retain every supported global operation") {
    for {
      qualifiers <- Vec("sys.global", "acq_rel.gpu.global")
      operation  <- Vec("add.u32", "min.u32", "max.u32", "and.b32", "exch.b32", "add.f32", "cas.b32")
    } {
      val operands = if (operation == "cas.b32") "%r0, [%rd0], 0, 1" else "%r0, [%rd0], 1"
      val source   = s"atom.$qualifiers.$operation $operands;"
      assert(compileInstructions(source).isRight, clues(source))
    }
  }

  test("supported cvta orders still lower only in their exact forms") {
    val instructions = compileInstructions("""
cvta.global.u64 %rd0, %rd0;
cvta.to.global.u64 %rd0, %rd0;
""").toOption.get
    assertEquals(instructions.size, 3)
    assert(instructions.take(2).forall {
      case VMI.Move(_, _, width) => width.raw == 64
      case _                     => false
    })
  }

  test("modifier order keeps conversion source and destination types distinct") {
    val instructions = compileInstructions("""
cvt.u32.f64 %r0, %rd0;
cvt.f64.u32 %rd0, %r0;
cvt.u32.u32 %r0, %r0;
""").toOption.get
    val conversions = instructions.collect {
      case VMI.Convert(_, _, source, destination, _) => source -> destination
    }
    assertEquals(
      conversions,
      Vec(
        ConversionType.F64 -> ConversionType.U32,
        ConversionType.U32 -> ConversionType.F64,
        ConversionType.U32 -> ConversionType.U32
      )
    )
  }

  test("comparison lowering reuses typed conversion formats including BF16 and raw bits") {
    val types = Vec(
      ConversionType.S8,
      ConversionType.U64,
      ConversionType.B16,
      ConversionType.F16,
      ConversionType.F32,
      ConversionType.F64,
      ConversionType.BF16
    )
    types.foreach { numericType =>
      val instructions = compileInstructions(s"setp.eq.${numericType.mnemonic} %p0, %r0, %r0;").toOption.get
      val format       = instructions.collectFirst { case VMI.SetPredicate(_, _, format, _, _, _) => format }.get
      assert(format eq numericType.format)
    }
    assert(compileInstructions("setp.eq.pred %p0, %p0, %p1;").toOption.get.exists {
      case _: VMI.PredicateCompare => true
      case _                       => false
    })
  }

  test("generic, global, shared, local and parameter memory use their VMIR families") {
    val instructions = compileInstructions("""
ld.u32 %r0, [%rd0];
st.u32 [%rd0], %r0;
ld.global.u32 %r0, [%rd0];
st.global.u32 [%rd0], %r0;
ld.shared.u32 %r0, [%rd0];
st.shared.u32 [%rd0], %r0;
ld.local.u32 %r0, [%rd0];
st.local.u32 [%rd0], %r0;
ld.param.u32 %r0, [input];
st.param.u32 [output], %r0;
""").toOption.get

    def family(instruction: VMIR.Instruction): String = instruction match {
      case VMI.Load(_, _, _, _, MemoryAccess.Generic(false)) => "load-generic"
      case VMI.Store(_, _, _, MemoryAccess.Generic(false))   => "store-generic"
      case VMI.Load(_, _, _, _, MemoryAccess.Global(false))  => "load-global"
      case VMI.Store(_, _, _, MemoryAccess.Global(false))    => "store-global"
      case VMI.Load(_, _, _, _, MemoryAccess.Shared)         => "load-shared"
      case VMI.Store(_, _, _, MemoryAccess.Shared)           => "store-shared"
      case VMI.Load(_, _, _, _, MemoryAccess.Local)          => "load-local"
      case VMI.Store(_, _, _, MemoryAccess.Local)            => "store-local"
      case _: VMI.LoadParameter                              => "load-parameter"
      case _: VMI.StoreParameter                             => "store-parameter"
      case other                                             => s"other:${other.opcode}"
    }

    assertEquals(
      instructions.map(family(_)),
      Vec(
        "load-generic",
        "store-generic",
        "load-global",
        "store-global",
        "load-shared",
        "store-shared",
        "load-local",
        "store-local",
        "load-parameter",
        "store-parameter",
        "other:ret"
      )
    )
  }

  test("scalar and vector memory keep space and system visibility in the shared VMIR") {
    val instructions = compileInstructions("""
ld.u32 %r0, [%rd0];
st.u32 [%rd0], %r0;
ld.global.u32 %r0, [%rd0];
st.global.u32 [%rd0], %r0;
ld.shared.u32 %r0, [%rd0];
st.shared.u32 [%rd0], %r0;
ld.local.u32 %r0, [%rd0];
st.local.u32 [%rd0], %r0;
ld.relaxed.sys.global.u32 %r0, [%rd0];
st.relaxed.sys.global.u32 [%rd0], %r0;
ld.relaxed.sys.u32 %r0, [%rd0];
st.relaxed.sys.u32 [%rd0], %r0;
ld.v2.u32 {%r0, %r1}, [%rd0];
st.v2.u32 [%rd0], {%r0, %r1};
ld.global.v2.u32 {%r0, %r1}, [%rd0];
st.global.v2.u32 [%rd0], {%r0, %r1};
ld.shared.v2.u32 {%r0, %r1}, [%rd0];
st.shared.v2.u32 [%rd0], {%r0, %r1};
ld.local.v2.u32 {%r0, %r1}, [%rd0];
st.local.v2.u32 [%rd0], {%r0, %r1};
ld.relaxed.sys.global.v2.u32 {%r0, %r1}, [%rd0];
st.relaxed.sys.global.v2.u32 [%rd0], {%r0, %r1};
ld.relaxed.sys.v2.u32 {%r0, %r1}, [%rd0];
st.relaxed.sys.v2.u32 [%rd0], {%r0, %r1};
""").toOption.get

    val accesses = instructions.collect {
      case VMI.Load(_, _, _, _, access)       => ("load-scalar", access)
      case VMI.LoadVector(_, _, _, _, access) => ("load-vector", access)
      case VMI.Store(_, _, _, access)         => ("store-scalar", access)
      case VMI.StoreVector(_, _, _, access)   => ("store-vector", access)
    }
    val spaces = Vec(
      MemoryAccess.Generic(false),
      MemoryAccess.Global(false),
      MemoryAccess.Shared,
      MemoryAccess.Local,
      MemoryAccess.Global(true),
      MemoryAccess.Generic(true)
    )
    val expected = spaces.flatMap(access =>
      Vec(
        ("load-scalar", access),
        ("store-scalar", access)
      )
    ) ++ spaces.flatMap(access =>
      Vec(
        ("load-vector", access),
        ("store-vector", access)
      )
    )
    assertEquals(accesses, expected)
  }
}
