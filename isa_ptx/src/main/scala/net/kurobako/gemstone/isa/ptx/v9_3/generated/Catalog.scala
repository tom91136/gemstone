package net.kurobako.gemstone.isa.ptx.v9_3.generated

import net.kurobako.gemstone.isa.ptx.v9_3.InstructionCatalog
import net.kurobako.gemstone.isa.ptx.v9_3.InstructionInfo
import net.kurobako.gemstone.isa.ptx.v9_3.InstructionForm
import net.kurobako.gemstone.isa.ptx.v9_3.OperandConstraint
import net.kurobako.gemstone.isa.ptx.v9_3.PtxVersion
import net.kurobako.gemstone.isa.ptx.v9_3.SmTarget

// Generated from the pinned NVIDIA PTX ISA 9.3 specification. Do not edit.
object Catalog extends InstructionCatalog {
  override val version: String = "9.3"
  private def chunk0: Vector[InstructionInfo] = Vector(
    InstructionInfo(
      "integer-arithmetic-instructions-add",
      "9.7.1.1. Integer Arithmetic Instructions: add",
      "add",
      "Integer Arithmetic Instructions",
      Vector("add"),
      Vector("add.type1        d, a, b;\nadd{.sat}.type2  d, a, b;\n\n.type1 = { .u16, .u64,\n           .s16, .s64 };\n\n.type2 = { .u32, .u16x2, .u8x4,\n           .s32, .s16x2, .s8x4 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("add", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("s16", "s64", "u16", "u64")), Set(), false), InstructionForm("add", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("s16x2", "s32", "s8x4", "u16x2", "u32", "u8x4")), Set("sat"), false))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-sub",
      "9.7.1.2. Integer Arithmetic Instructions: sub",
      "sub",
      "Integer Arithmetic Instructions",
      Vector("sub"),
      Vector("sub.type1        d, a, b;\nsub{.sat}.type2  d, a, b;\n\n.type1 = { .u16, .u32, .u64,\n           .s16, .s64 };\n\n.type2 = { .s32, .u8x4, .s8x4 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("sub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("s16", "s64", "u16", "u32", "u64")), Set(), false), InstructionForm("sub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("s32", "s8x4", "u8x4")), Set("sat"), false))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-mul",
      "9.7.1.3. Integer Arithmetic Instructions: mul",
      "mul",
      "Integer Arithmetic Instructions",
      Vector("mul"),
      Vector("mul.mode.type  d, a, b;\n\n.mode = { .hi, .lo, .wide };\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("mul", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("hi", "lo", "wide"), Set("s16", "s32", "s64", "u16", "u32", "u64")), Set(), true))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-mad",
      "9.7.1.4. Integer Arithmetic Instructions: mad",
      "mad",
      "Integer Arithmetic Instructions",
      Vector("mad"),
      Vector("mad.mode.type  d, a, b, c;\nmad.hi.sat.s32 d, a, b, c;\n\n.mode = { .hi, .lo, .wide };\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("mad", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("hi", "lo", "wide"), Set("s16", "s32", "s64", "u16", "u32", "u64")), Set(), true), InstructionForm("mad", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("hi"), Set("sat"), Set("s32")), Set(), true))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-clmad",
      "9.7.1.5. Integer Arithmetic Instructions: clmad",
      "clmad",
      "Integer Arithmetic Instructions",
      Vector("clmad"),
      Vector("clmad.mode.u64 d, a, b, c;\n\n.mode = { .hi, .lo };"),
      PtxVersion(9, 3),
      SmTarget(80),
      Vector(InstructionForm("clmad", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("hi", "lo"), Set("u64")), Set(), true))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-mul24",
      "9.7.1.6. Integer Arithmetic Instructions: mul24",
      "mul24",
      "Integer Arithmetic Instructions",
      Vector("mul24"),
      Vector("mul24.mode.type  d, a, b;\n\n.mode = { .hi, .lo };\n.type = { .u32, .s32 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("mul24", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("hi", "lo"), Set("s32", "u32")), Set(), true))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-mad24",
      "9.7.1.7. Integer Arithmetic Instructions: mad24",
      "mad24",
      "Integer Arithmetic Instructions",
      Vector("mad24"),
      Vector("mad24.mode.type  d, a, b, c;\nmad24.hi.sat.s32 d, a, b, c;\n\n.mode = { .hi, .lo };\n.type = { .u32, .s32 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("mad24", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("hi", "lo"), Set("s32", "u32")), Set(), true), InstructionForm("mad24", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("hi"), Set("sat"), Set("s32")), Set(), true))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-sad",
      "9.7.1.8. Integer Arithmetic Instructions: sad",
      "sad",
      "Integer Arithmetic Instructions",
      Vector("sad"),
      Vector("sad.type  d, a, b, c;\n\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("sad", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("s16", "s32", "s64", "u16", "u32", "u64")), Set(), true))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-div",
      "9.7.1.9. Integer Arithmetic Instructions: div",
      "div",
      "Integer Arithmetic Instructions",
      Vector("div"),
      Vector("div.type  d, a, b;\n\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("div", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("s16", "s32", "s64", "u16", "u32", "u64")), Set(), true))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-rem",
      "9.7.1.10. Integer Arithmetic Instructions: rem",
      "rem",
      "Integer Arithmetic Instructions",
      Vector("rem"),
      Vector("rem.type  d, a, b;\n\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("rem", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("s16", "s32", "s64", "u16", "u32", "u64")), Set(), true))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-abs",
      "9.7.1.11. Integer Arithmetic Instructions: abs",
      "abs",
      "Integer Arithmetic Instructions",
      Vector("abs"),
      Vector("abs.type  d, a;\n\n.type = { .s16, .s32, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("abs", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("s16", "s32", "s64")), Set(), true))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-neg",
      "9.7.1.12. Integer Arithmetic Instructions: neg",
      "neg",
      "Integer Arithmetic Instructions",
      Vector("neg"),
      Vector("neg.type  d, a;\n\n.type = { .s8x4, .s16, .s32, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("neg", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("s16", "s32", "s64", "s8x4")), Set(), false))
    )
  )

  private def chunk1: Vector[InstructionInfo] = Vector(
    InstructionInfo(
      "integer-arithmetic-instructions-min",
      "9.7.1.13. Integer Arithmetic Instructions: min",
      "min",
      "Integer Arithmetic Instructions",
      Vector("min"),
      Vector("min.type1         d, a, b;\nmin{.relu}.type2  d, a, b;\n\n.type1 = { .u16, .u32, .u64,\n           .u16x2, .u8x4, .s16, .s64 };\n.type2 = { .s16x2, .s32, .s8x4 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("min", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("s16", "s64", "u16", "u16x2", "u32", "u64", "u8x4")), Set(), false), InstructionForm("min", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("s16x2", "s32", "s8x4")), Set("relu"), false))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-max",
      "9.7.1.14. Integer Arithmetic Instructions: max",
      "max",
      "Integer Arithmetic Instructions",
      Vector("max"),
      Vector("max.type1         d, a, b;\nmax{.relu}.type2  d, a, b;\n\n.type1 = { .u16, .u32, .u64,\n           .u16x2, .u8x4, .s16, .s64 };\n.type2 = { .s16x2, .s32, .s8x4 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("max", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("s16", "s64", "u16", "u16x2", "u32", "u64", "u8x4")), Set(), false), InstructionForm("max", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("s16x2", "s32", "s8x4")), Set("relu"), false))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-popc",
      "9.7.1.15. Integer Arithmetic Instructions: popc",
      "popc",
      "Integer Arithmetic Instructions",
      Vector("popc"),
      Vector("popc.type  d, a;\n\n.type = { .b32, .b64 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("popc", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("b32", "b64")), Set(), true))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-clz",
      "9.7.1.16. Integer Arithmetic Instructions: clz",
      "clz",
      "Integer Arithmetic Instructions",
      Vector("clz"),
      Vector("clz.type  d, a;\n\n.type = { .b32, .b64 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("clz", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("b32", "b64")), Set(), true))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-bfind",
      "9.7.1.17. Integer Arithmetic Instructions: bfind",
      "bfind",
      "Integer Arithmetic Instructions",
      Vector("bfind"),
      Vector("bfind.type           d, a;\nbfind.shiftamt.type  d, a;\n\n.type = { .u32, .u64,\n          .s32, .s64 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("bfind", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("s32", "s64", "u32", "u64")), Set(), true), InstructionForm("bfind", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("shiftamt"), Set("s32", "s64", "u32", "u64")), Set(), true))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-fns",
      "9.7.1.18. Integer Arithmetic Instructions: fns",
      "fns",
      "Integer Arithmetic Instructions",
      Vector("fns"),
      Vector("fns.b32 d, mask, base, offset;"),
      PtxVersion(6, 0),
      SmTarget(30),
      Vector(InstructionForm("fns", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("b32")), Set(), true))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-brev",
      "9.7.1.19. Integer Arithmetic Instructions: brev",
      "brev",
      "Integer Arithmetic Instructions",
      Vector("brev"),
      Vector("brev.type  d, a;\n\n.type = { .b32, .b64 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("brev", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("b32", "b64")), Set(), true))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-bfe",
      "9.7.1.20. Integer Arithmetic Instructions: bfe",
      "bfe",
      "Integer Arithmetic Instructions",
      Vector("bfe"),
      Vector("bfe.type  d, a, b, c;\n\n.type = { .u32, .u64,\n          .s32, .s64 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("bfe", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("s32", "s64", "u32", "u64")), Set(), true))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-bfi",
      "9.7.1.21. Integer Arithmetic Instructions: bfi",
      "bfi",
      "Integer Arithmetic Instructions",
      Vector("bfi"),
      Vector("bfi.type  f, a, b, c, d;\n\n.type = { .b32, .b64 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("bfi", Vector(OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Register), Vector(Set("b32", "b64")), Set(), true))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-szext",
      "9.7.1.22. Integer Arithmetic Instructions: szext",
      "szext",
      "Integer Arithmetic Instructions",
      Vector("szext"),
      Vector("szext.mode.type  d, a, b;\n\n.mode = { .clamp, .wrap };\n.type = { .u32, .s32 };"),
      PtxVersion(7, 6),
      SmTarget(70),
      Vector(InstructionForm("szext", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("clamp", "wrap"), Set("s32", "u32")), Set(), true))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-bmsk",
      "9.7.1.23. Integer Arithmetic Instructions: bmsk",
      "bmsk",
      "Integer Arithmetic Instructions",
      Vector("bmsk"),
      Vector("bmsk.mode.b32  d, a, b;\n\n.mode = { .clamp, .wrap };"),
      PtxVersion(7, 6),
      SmTarget(70),
      Vector(InstructionForm("bmsk", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("clamp", "wrap"), Set("b32")), Set(), true))
    ),
    InstructionInfo(
      "integer-arithmetic-instructions-dp4a",
      "9.7.1.24. Integer Arithmetic Instructions: dp4a",
      "dp4a",
      "Integer Arithmetic Instructions",
      Vector("dp4a"),
      Vector("dp4a.atype.btype  d, a, b, c;\n\n.atype = .btype = { .u32, .s32 };"),
      PtxVersion(5, 0),
      SmTarget(61),
      Vector(InstructionForm("dp4a", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("atype"), Set("s32", "u32")), Set(), true))
    )
  )

  private def chunk2: Vector[InstructionInfo] = Vector(
    InstructionInfo(
      "integer-arithmetic-instructions-dp2a",
      "9.7.1.25. Integer Arithmetic Instructions: dp2a",
      "dp2a",
      "Integer Arithmetic Instructions",
      Vector("dp2a"),
      Vector("dp2a.mode.atype.btype  d, a, b, c;\n\n.atype = .btype = { .u32, .s32 };\n.mode = { .lo, .hi };"),
      PtxVersion(5, 0),
      SmTarget(61),
      Vector(InstructionForm("dp2a", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("hi", "lo"), Set("atype"), Set("s32", "u32")), Set(), true))
    ),
    InstructionInfo(
      "extended-precision-arithmetic-instructions-add-cc",
      "9.7.2.1. Extended-Precision Arithmetic Instructions: add.cc",
      "add.cc",
      "Extended-Precision Arithmetic Instructions",
      Vector("add", "addc"),
      Vector("add.cc.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("add", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("cc"), Set("s32", "s64", "u32", "u64")), Set(), true))
    ),
    InstructionInfo(
      "extended-precision-arithmetic-instructions-addc",
      "9.7.2.2. Extended-Precision Arithmetic Instructions: addc",
      "addc",
      "Extended-Precision Arithmetic Instructions",
      Vector("addc", "add"),
      Vector("addc{.cc}.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("addc", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("s32", "s64", "u32", "u64")), Set("cc"), true))
    ),
    InstructionInfo(
      "extended-precision-arithmetic-instructions-sub-cc",
      "9.7.2.3. Extended-Precision Arithmetic Instructions: sub.cc",
      "sub.cc",
      "Extended-Precision Arithmetic Instructions",
      Vector("sub", "subc"),
      Vector("sub.cc.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("sub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("cc"), Set("s32", "s64", "u32", "u64")), Set(), true))
    ),
    InstructionInfo(
      "extended-precision-arithmetic-instructions-subc",
      "9.7.2.4. Extended-Precision Arithmetic Instructions: subc",
      "subc",
      "Extended-Precision Arithmetic Instructions",
      Vector("subc", "sub"),
      Vector("subc{.cc}.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("subc", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("s32", "s64", "u32", "u64")), Set("cc"), true))
    ),
    InstructionInfo(
      "extended-precision-arithmetic-instructions-mad-cc",
      "9.7.2.5. Extended-Precision Arithmetic Instructions: mad.cc",
      "mad.cc",
      "Extended-Precision Arithmetic Instructions",
      Vector("mad"),
      Vector("mad{.hi,.lo}.cc.type  d, a, b, c;\n\n.type = { .u32, .s32, .u64, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("mad", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("cc"), Set("s32", "s64", "u32", "u64")), Set("hi", "lo"), true))
    ),
    InstructionInfo(
      "extended-precision-arithmetic-instructions-madc",
      "9.7.2.6. Extended-Precision Arithmetic Instructions: madc",
      "madc",
      "Extended-Precision Arithmetic Instructions",
      Vector("madc", "mul"),
      Vector("madc{.hi,.lo}{.cc}.type  d, a, b, c;\n\n.type = { .u32, .s32, .u64, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("madc", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("s32", "s64", "u32", "u64")), Set("cc", "hi", "lo"), true))
    ),
    InstructionInfo(
      "floating-point-instructions-testp",
      "9.7.3.1. Floating Point Instructions: testp",
      "testp",
      "Floating Point Instructions",
      Vector("testp"),
      Vector("testp.op.type  p, a;  // result is .pred\n\n.op   = { .finite, .infinite,\n          .number, .notanumber,\n          .normal, .subnormal };\n.type = { .f32, .f64 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("testp", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("finite", "infinite", "normal", "notanumber", "number", "subnormal"), Set("f32", "f64")), Set(), true))
    ),
    InstructionInfo(
      "floating-point-instructions-copysign",
      "9.7.3.2. Floating Point Instructions: copysign",
      "copysign",
      "Floating Point Instructions",
      Vector("copysign"),
      Vector("copysign.type  d, a, b;\n\n.type = { .f32, .f64 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("copysign", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f32", "f64")), Set(), true))
    ),
    InstructionInfo(
      "floating-point-instructions-add",
      "9.7.3.3. Floating Point Instructions: add",
      "add",
      "Floating Point Instructions",
      Vector("add"),
      Vector("add{.rnd}{.ftz}{.sat}.f32  d, a, b;\nadd{.rnd}{.ftz}.f32x2      d, a, b;\nadd{.rnd}.f64              d, a, b;\n\n.rnd = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("add", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f32")), Set("ftz", "rm", "rn", "rp", "rz", "sat"), false), InstructionForm("add", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f32x2")), Set("ftz", "rm", "rn", "rp", "rz"), false), InstructionForm("add", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f64")), Set("rm", "rn", "rp", "rz"), false))
    ),
    InstructionInfo(
      "floating-point-instructions-sub",
      "9.7.3.4. Floating Point Instructions: sub",
      "sub",
      "Floating Point Instructions",
      Vector("sub"),
      Vector("sub{.rnd}{.ftz}{.sat}.f32  d, a, b;\nsub{.rnd}{.ftz}.f32x2      d, a, b;\nsub{.rnd}.f64              d, a, b;\n\n.rnd = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("sub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f32")), Set("ftz", "rm", "rn", "rp", "rz", "sat"), false), InstructionForm("sub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f32x2")), Set("ftz", "rm", "rn", "rp", "rz"), false), InstructionForm("sub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f64")), Set("rm", "rn", "rp", "rz"), false))
    ),
    InstructionInfo(
      "floating-point-instructions-mul",
      "9.7.3.5. Floating Point Instructions: mul",
      "mul",
      "Floating Point Instructions",
      Vector("mul"),
      Vector("mul{.rnd}{.ftz}{.sat}.f32  d, a, b;\nmul{.rnd}{.ftz}.f32x2      d, a, b;\nmul{.rnd}.f64              d, a, b;\n\n.rnd = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("mul", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f32")), Set("ftz", "rm", "rn", "rp", "rz", "sat"), false), InstructionForm("mul", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f32x2")), Set("ftz", "rm", "rn", "rp", "rz"), false), InstructionForm("mul", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f64")), Set("rm", "rn", "rp", "rz"), false))
    )
  )

  private def chunk3: Vector[InstructionInfo] = Vector(
    InstructionInfo(
      "floating-point-instructions-fma",
      "9.7.3.6. Floating Point Instructions: fma",
      "fma",
      "Floating Point Instructions",
      Vector("fma"),
      Vector("fma.rnd{.ftz}{.sat}.f32  d, a, b, c;\nfma.rnd{.ftz}.f32x2      d, a, b, c;\nfma.rnd.f64              d, a, b, c;\n\n.rnd = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("rm", "rn", "rp", "rz"), Set("f32")), Set("ftz", "sat"), false), InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("rm", "rn", "rp", "rz"), Set("f32x2")), Set("ftz"), false), InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("rm", "rn", "rp", "rz"), Set("f64")), Set(), false))
    ),
    InstructionInfo(
      "floating-point-instructions-mad",
      "9.7.3.7. Floating Point Instructions: mad",
      "mad",
      "Floating Point Instructions",
      Vector("mad"),
      Vector("mad{.ftz}{.sat}.f32      d, a, b, c;    // .target sm_1x\nmad.rnd{.ftz}{.sat}.f32  d, a, b, c;    // .target sm_20\nmad.rnd.f64              d, a, b, c;    // .target sm_13 and higher\n\n.rnd = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("mad", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f32")), Set("ftz", "sat"), false))
    ),
    InstructionInfo(
      "floating-point-instructions-div",
      "9.7.3.8. Floating Point Instructions: div",
      "div",
      "Floating Point Instructions",
      Vector("div"),
      Vector("div.approx{.ftz}.f32  d, a, b;  // fast, approximate divide\ndiv.full{.ftz}.f32    d, a, b;  // full-range approximate divide\ndiv.rnd{.ftz}.f32     d, a, b;  // IEEE 754 compliant rounding\ndiv.rnd.f64           d, a, b;  // IEEE 754 compliant rounding\n\n.rnd = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("div", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("approx"), Set("f32")), Set("ftz"), false))
    ),
    InstructionInfo(
      "floating-point-instructions-abs",
      "9.7.3.9. Floating Point Instructions: abs",
      "abs",
      "Floating Point Instructions",
      Vector("abs"),
      Vector("abs{.ftz}.f32  d, a;\nabs.f64        d, a;"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("abs", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("f32")), Set("ftz"), true), InstructionForm("abs", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("f64")), Set(), true))
    ),
    InstructionInfo(
      "floating-point-instructions-neg",
      "9.7.3.10. Floating Point Instructions: neg",
      "neg",
      "Floating Point Instructions",
      Vector("neg"),
      Vector("neg{.ftz}.f32  d, a;\nneg.f64        d, a;"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("neg", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("f32")), Set("ftz"), true), InstructionForm("neg", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("f64")), Set(), true))
    ),
    InstructionInfo(
      "floating-point-instructions-min",
      "9.7.3.11. Floating Point Instructions: min",
      "min",
      "Floating Point Instructions",
      Vector("min"),
      Vector("min{.ftz}{.NaN}{.xorsign.abs}.f32  d, a, b;\nmin{.ftz}{.NaN}{.abs}.f32          d, a, b, c;\nmin.f64                            d, a, b;"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("min", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f32")), Set("NaN", "abs", "ftz", "xorsign"), false), InstructionForm("min", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f32")), Set("NaN", "abs", "ftz"), false), InstructionForm("min", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f64")), Set(), false))
    ),
    InstructionInfo(
      "floating-point-instructions-max",
      "9.7.3.12. Floating Point Instructions: max",
      "max",
      "Floating Point Instructions",
      Vector("max"),
      Vector("max{.ftz}{.NaN}{.xorsign.abs}.f32  d, a, b;\nmax{.ftz}{.NaN}{.abs}.f32          d, a, b, c;\nmax.f64                            d, a, b;"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("max", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f32")), Set("NaN", "abs", "ftz", "xorsign"), false), InstructionForm("max", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f32")), Set("NaN", "abs", "ftz"), false), InstructionForm("max", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f64")), Set(), false))
    ),
    InstructionInfo(
      "floating-point-instructions-rcp",
      "9.7.3.13. Floating Point Instructions: rcp",
      "rcp",
      "Floating Point Instructions",
      Vector("rcp"),
      Vector("rcp.approx{.ftz}.f32  d, a;  // fast, approximate reciprocal\nrcp.rnd{.ftz}.f32     d, a;  // IEEE 754 compliant rounding\nrcp.rnd.f64           d, a;  // IEEE 754 compliant rounding\n\n.rnd = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("rcp", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("approx"), Set("f32")), Set("ftz"), false))
    ),
    InstructionInfo(
      "floating-point-instructions-rcp-approx-ftz-f64",
      "9.7.3.14. Floating Point Instructions: rcp.approx.ftz.f64",
      "rcp.approx.ftz.f64",
      "Floating Point Instructions",
      Vector("rcp"),
      Vector("rcp.approx.ftz.f64  d, a;"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("rcp", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("approx"), Set("ftz"), Set("f64")), Set(), true))
    ),
    InstructionInfo(
      "floating-point-instructions-sqrt",
      "9.7.3.15. Floating Point Instructions: sqrt",
      "sqrt",
      "Floating Point Instructions",
      Vector("sqrt"),
      Vector("sqrt.approx{.ftz}.f32  d, a; // fast, approximate square root\nsqrt.rnd{.ftz}.f32     d, a; // IEEE 754 compliant rounding\nsqrt.rnd.f64           d, a; // IEEE 754 compliant rounding\n\n.rnd = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("sqrt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("approx"), Set("f32")), Set("ftz"), false))
    ),
    InstructionInfo(
      "floating-point-instructions-rsqrt",
      "9.7.3.16. Floating Point Instructions: rsqrt",
      "rsqrt",
      "Floating Point Instructions",
      Vector("rsqrt"),
      Vector("rsqrt.approx{.ftz}.f32  d, a;\nrsqrt.approx.f64        d, a;"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("rsqrt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("approx"), Set("f32")), Set("ftz"), true), InstructionForm("rsqrt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("approx"), Set("f64")), Set(), true))
    ),
    InstructionInfo(
      "floating-point-instructions-rsqrt-approx-ftz-f64",
      "9.7.3.17. Floating Point Instructions: rsqrt.approx.ftz.f64",
      "rsqrt.approx.ftz.f64",
      "Floating Point Instructions",
      Vector("rsqrt"),
      Vector("rsqrt.approx.ftz.f64 d, a;"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("rsqrt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("approx"), Set("ftz"), Set("f64")), Set(), true))
    )
  )

  private def chunk4: Vector[InstructionInfo] = Vector(
    InstructionInfo(
      "floating-point-instructions-sin",
      "9.7.3.18. Floating Point Instructions: sin",
      "sin",
      "Floating Point Instructions",
      Vector("sin"),
      Vector("sin.approx{.ftz}.f32  d, a;"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("sin", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("approx"), Set("f32")), Set("ftz"), true))
    ),
    InstructionInfo(
      "floating-point-instructions-cos",
      "9.7.3.19. Floating Point Instructions: cos",
      "cos",
      "Floating Point Instructions",
      Vector("cos"),
      Vector("cos.approx{.ftz}.f32  d, a;"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("cos", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("approx"), Set("f32")), Set("ftz"), true))
    ),
    InstructionInfo(
      "floating-point-instructions-lg2",
      "9.7.3.20. Floating Point Instructions: lg2",
      "lg2",
      "Floating Point Instructions",
      Vector("lg2"),
      Vector("lg2.approx{.ftz}.f32  d, a;"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("lg2", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("approx"), Set("f32")), Set("ftz"), true))
    ),
    InstructionInfo(
      "floating-point-instructions-ex2",
      "9.7.3.21. Floating Point Instructions: ex2",
      "ex2",
      "Floating Point Instructions",
      Vector("ex2"),
      Vector("ex2.approx{.ftz}.f32  d, a;"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("ex2", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("approx"), Set("f32")), Set("ftz"), true))
    ),
    InstructionInfo(
      "floating-point-instructions-tanh",
      "9.7.3.22. Floating Point Instructions: tanh",
      "tanh",
      "Floating Point Instructions",
      Vector("tanh"),
      Vector("tanh.approx.f32 d, a;"),
      PtxVersion(7, 0),
      SmTarget(75),
      Vector(InstructionForm("tanh", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("approx"), Set("f32")), Set(), true))
    ),
    InstructionInfo(
      "half-precision-floating-point-instructions-add",
      "9.7.4.1. Half Precision Floating Point Instructions: add",
      "add",
      "Half Precision Floating Point Instructions",
      Vector("add", "cvt", "mov"),
      Vector("add{.rnd}{.ftz}{.sat}.f16   d, a, b;\nadd{.rnd}{.ftz}{.sat}.f16x2 d, a, b;\n\nadd{.rnd}.bf16   d, a, b;\nadd{.rnd}.bf16x2 d, a, b;\n\n.rnd = { .rn };"),
      PtxVersion(4, 2),
      SmTarget(53),
      Vector(InstructionForm("add", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f16")), Set("ftz", "rn", "sat"), false), InstructionForm("add", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f16x2")), Set("ftz", "rn", "sat"), false), InstructionForm("add", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("bf16")), Set("rn"), false), InstructionForm("add", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("bf16x2")), Set("rn"), false))
    ),
    InstructionInfo(
      "half-precision-floating-point-instructions-sub",
      "9.7.4.2. Half Precision Floating Point Instructions: sub",
      "sub",
      "Half Precision Floating Point Instructions",
      Vector("sub", "cvt", "mov"),
      Vector("sub{.rnd}{.ftz}{.sat}.f16   d, a, b;\nsub{.rnd}{.ftz}{.sat}.f16x2 d, a, b;\n\nsub{.rnd}.bf16   d, a, b;\nsub{.rnd}.bf16x2 d, a, b;\n\n.rnd = { .rn };"),
      PtxVersion(4, 2),
      SmTarget(53),
      Vector(InstructionForm("sub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f16")), Set("ftz", "rn", "sat"), false), InstructionForm("sub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f16x2")), Set("ftz", "rn", "sat"), false), InstructionForm("sub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("bf16")), Set("rn"), false), InstructionForm("sub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("bf16x2")), Set("rn"), false))
    ),
    InstructionInfo(
      "half-precision-floating-point-instructions-mul",
      "9.7.4.3. Half Precision Floating Point Instructions: mul",
      "mul",
      "Half Precision Floating Point Instructions",
      Vector("mul", "cvt", "mov"),
      Vector("mul{.rnd}{.ftz}{.sat}.f16   d, a, b;\nmul{.rnd}{.ftz}{.sat}.f16x2 d, a, b;\n\nmul{.rnd}.bf16   d, a, b;\nmul{.rnd}.bf16x2 d, a, b;\n\n.rnd = { .rn };"),
      PtxVersion(4, 2),
      SmTarget(53),
      Vector(InstructionForm("mul", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f16")), Set("ftz", "rn", "sat"), false), InstructionForm("mul", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f16x2")), Set("ftz", "rn", "sat"), false), InstructionForm("mul", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("bf16")), Set("rn"), false), InstructionForm("mul", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("bf16x2")), Set("rn"), false))
    ),
    InstructionInfo(
      "half-precision-floating-point-instructions-fma",
      "9.7.4.4. Half Precision Floating Point Instructions: fma",
      "fma",
      "Half Precision Floating Point Instructions",
      Vector("fma", "cvt", "mov"),
      Vector("fma.rnd{.ftz}{.sat}.f16     d, a, b, c;\nfma.rnd{.ftz}{.sat}.f16x2   d, a, b, c;\nfma.rnd{.ftz}.relu.f16      d, a, b, c;\nfma.rnd{.ftz}.relu.f16x2    d, a, b, c;\nfma.rnd{.relu}.bf16         d, a, b, c;\nfma.rnd{.relu}.bf16x2       d, a, b, c;\nfma.rnd.oob.{relu}.type     d, a, b, c;\n\n.rnd = { .rn };"),
      PtxVersion(4, 2),
      SmTarget(53),
      Vector(InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("rn"), Set("f16")), Set("ftz", "sat"), false), InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("rn"), Set("f16x2")), Set("ftz", "sat"), false), InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("rn"), Set("relu"), Set("f16")), Set("ftz"), false), InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("rn"), Set("relu"), Set("f16x2")), Set("ftz"), false), InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("rn"), Set("bf16")), Set("relu"), false), InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("rn"), Set("bf16x2")), Set("relu"), false), InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("rn"), Set("oob"), Set("type")), Set(), false))
    ),
    InstructionInfo(
      "half-precision-floating-point-instructions-neg",
      "9.7.4.5. Half Precision Floating Point Instructions: neg",
      "neg",
      "Half Precision Floating Point Instructions",
      Vector("neg"),
      Vector("neg{.ftz}.f16    d, a;\nneg{.ftz}.f16x2  d, a;\nneg.bf16         d, a;\nneg.bf16x2       d, a;"),
      PtxVersion(6, 0),
      SmTarget(53),
      Vector(InstructionForm("neg", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("f16")), Set("ftz"), false), InstructionForm("neg", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("f16x2")), Set("ftz"), false), InstructionForm("neg", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("bf16")), Set(), false), InstructionForm("neg", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("bf16x2")), Set(), false))
    ),
    InstructionInfo(
      "half-precision-floating-point-instructions-abs",
      "9.7.4.6. Half Precision Floating Point Instructions: abs",
      "abs",
      "Half Precision Floating Point Instructions",
      Vector("abs"),
      Vector("abs{.ftz}.f16    d, a;\nabs{.ftz}.f16x2  d, a;\nabs.bf16         d, a;\nabs.bf16x2       d, a;"),
      PtxVersion(6, 5),
      SmTarget(53),
      Vector(InstructionForm("abs", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("f16")), Set("ftz"), false), InstructionForm("abs", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("f16x2")), Set("ftz"), false), InstructionForm("abs", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("bf16")), Set(), false), InstructionForm("abs", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("bf16x2")), Set(), false))
    ),
    InstructionInfo(
      "half-precision-floating-point-instructions-min",
      "9.7.4.7. Half Precision Floating Point Instructions: min",
      "min",
      "Half Precision Floating Point Instructions",
      Vector("min"),
      Vector("min{.ftz}{.NaN}{.xorsign.abs}.f16      d, a, b;\nmin{.ftz}{.NaN}{.xorsign.abs}.f16x2    d, a, b;\nmin{.NaN}{.xorsign.abs}.bf16           d, a, b;\nmin{.NaN}{.xorsign.abs}.bf16x2         d, a, b;"),
      PtxVersion(7, 0),
      SmTarget(80),
      Vector(InstructionForm("min", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f16")), Set("NaN", "abs", "ftz", "xorsign"), false), InstructionForm("min", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f16x2")), Set("NaN", "abs", "ftz", "xorsign"), false), InstructionForm("min", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("bf16")), Set("NaN", "abs", "xorsign"), false), InstructionForm("min", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("bf16x2")), Set("NaN", "abs", "xorsign"), false))
    )
  )

  private def chunk5: Vector[InstructionInfo] = Vector(
    InstructionInfo(
      "half-precision-floating-point-instructions-max",
      "9.7.4.8. Half Precision Floating Point Instructions: max",
      "max",
      "Half Precision Floating Point Instructions",
      Vector("max"),
      Vector("max{.ftz}{.NaN}{.xorsign.abs}.f16      d, a, b;\nmax{.ftz}{.NaN}{.xorsign.abs}.f16x2    d, a, b;\nmax{.NaN}{.xorsign.abs}.bf16           d, a, b;\nmax{.NaN}{.xorsign.abs}.bf16x2         d, a, b;"),
      PtxVersion(7, 0),
      SmTarget(80),
      Vector(InstructionForm("max", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f16")), Set("NaN", "abs", "ftz", "xorsign"), false), InstructionForm("max", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f16x2")), Set("NaN", "abs", "ftz", "xorsign"), false), InstructionForm("max", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("bf16")), Set("NaN", "abs", "xorsign"), false), InstructionForm("max", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("bf16x2")), Set("NaN", "abs", "xorsign"), false))
    ),
    InstructionInfo(
      "half-precision-floating-point-instructions-tanh",
      "9.7.4.9. Half Precision Floating Point Instructions: tanh",
      "tanh",
      "Half Precision Floating Point Instructions",
      Vector("tanh"),
      Vector("tanh.approx.type d, a;\n\n.type = {.f16, .f16x2, .bf16, .bf16x2}"),
      PtxVersion(7, 0),
      SmTarget(75),
      Vector(InstructionForm("tanh", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("approx"), Set("type")), Set(), false))
    ),
    InstructionInfo(
      "half-precision-floating-point-instructions-ex2",
      "9.7.4.10. Half Precision Floating Point Instructions: ex2",
      "ex2",
      "Half Precision Floating Point Instructions",
      Vector("ex2"),
      Vector("ex2.approx.atype     d, a;\nex2.approx.ftz.btype d, a;\n\n.atype = { .f16,  .f16x2}\n.btype = { .bf16, .bf16x2}"),
      PtxVersion(7, 0),
      SmTarget(75),
      Vector(InstructionForm("ex2", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("approx"), Set("atype")), Set(), false), InstructionForm("ex2", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("approx"), Set("ftz"), Set("btype")), Set(), false))
    ),
    InstructionInfo(
      "mixed-precision-floating-point-instructions-add",
      "9.7.5.1. Mixed Precision Floating Point Instructions: add",
      "add",
      "Mixed Precision Floating Point Instructions",
      Vector("add"),
      Vector("add{.rnd}{.sat}.f32.atype  d, a, c;\n\n.atype = { .f16, .bf16};\n.rnd   = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(100),
      Vector(InstructionForm("add", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f32"), Set("bf16", "f16")), Set("rm", "rn", "rp", "rz", "sat"), true))
    ),
    InstructionInfo(
      "mixed-precision-floating-point-instructions-sub",
      "9.7.5.2. Mixed Precision Floating Point Instructions: sub",
      "sub",
      "Mixed Precision Floating Point Instructions",
      Vector("sub"),
      Vector("sub{.rnd}{.sat}.f32.atype  d, a, c;\n\n.atype = { .f16, .bf16};\n.rnd   = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(100),
      Vector(InstructionForm("sub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("f32"), Set("bf16", "f16")), Set("rm", "rn", "rp", "rz", "sat"), true))
    ),
    InstructionInfo(
      "mixed-precision-floating-point-instructions-fma",
      "9.7.5.3. Mixed Precision Floating Point Instructions: fma",
      "fma",
      "Mixed Precision Floating Point Instructions",
      Vector("fma"),
      Vector("fma.rnd{.sat}.f32.abtype  d, a, b, c;\n\n.abtype = { .f16, .bf16};\n.rnd    = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(100),
      Vector(InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("rm", "rn", "rp", "rz"), Set("f32"), Set("bf16", "f16")), Set("sat"), true))
    ),
    InstructionInfo(
      "comparison-and-selection-instructions-set",
      "9.7.6.1. Comparison and Selection Instructions: set",
      "set",
      "Comparison and Selection Instructions",
      Vector("set"),
      Vector("set.CmpOp{.ftz}.dtype.stype         d, a, b;\nset.CmpOp.BoolOp{.ftz}.dtype.stype  d, a, b, {!}c;\n\n.CmpOp  = { eq, ne, lt, le, gt, ge, lo, ls, hi, hs,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };\n.dtype  = { .u32, .s32, .f32 };\n.stype  = { .b16, .b32, .b64,\n            .u16, .u32, .u64,\n            .s16, .s32, .s64,\n                  .f32, .f64 };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("eq", "equ", "ge", "geu", "gt", "gtu", "hi", "hs", "le", "leu", "lo", "ls", "lt", "ltu", "nan", "ne", "neu", "num"), Set("f32", "s32", "u32"), Set("b16", "b32", "b64", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64")), Set("ftz"), true), InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Vector), Vector(Set("eq", "equ", "ge", "geu", "gt", "gtu", "hi", "hs", "le", "leu", "lo", "ls", "lt", "ltu", "nan", "ne", "neu", "num"), Set("and", "or", "xor"), Set("f32", "s32", "u32"), Set("b16", "b32", "b64", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64")), Set("ftz"), true))
    ),
    InstructionInfo(
      "comparison-and-selection-instructions-setp",
      "9.7.6.2. Comparison and Selection Instructions: setp",
      "setp",
      "Comparison and Selection Instructions",
      Vector("setp"),
      Vector("setp.CmpOp{.ftz}.type         p[|q], a, b;\nsetp.CmpOp.BoolOp{.ftz}.type  p[|q], a, b, {!}c;\n\n.CmpOp  = { eq, ne, lt, le, gt, ge, lo, ls, hi, hs,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };\n.type   = { .b16, .b32, .b64,\n            .u16, .u32, .u64,\n            .s16, .s32, .s64,\n                  .f32, .f64 };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("setp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("eq", "equ", "ge", "geu", "gt", "gtu", "hi", "hs", "le", "leu", "lo", "ls", "lt", "ltu", "nan", "ne", "neu", "num"), Set("b16", "b32", "b64", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64")), Set("ftz"), true), InstructionForm("setp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Vector), Vector(Set("eq", "equ", "ge", "geu", "gt", "gtu", "hi", "hs", "le", "leu", "lo", "ls", "lt", "ltu", "nan", "ne", "neu", "num"), Set("and", "or", "xor"), Set("b16", "b32", "b64", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64")), Set("ftz"), true))
    ),
    InstructionInfo(
      "comparison-and-selection-instructions-selp",
      "9.7.6.3. Comparison and Selection Instructions: selp",
      "selp",
      "Comparison and Selection Instructions",
      Vector("selp"),
      Vector("selp.type d, a, b, c;\n\n.type = { .b16, .b32, .b64,\n          .u16, .u32, .u64,\n          .s16, .s32, .s64,\n                .f32, .f64 };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("selp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("b16", "b32", "b64", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64")), Set(), true))
    ),
    InstructionInfo(
      "comparison-and-selection-instructions-slct",
      "9.7.6.4. Comparison and Selection Instructions: slct",
      "slct",
      "Comparison and Selection Instructions",
      Vector("slct"),
      Vector("slct.dtype.s32        d, a, b, c;\nslct{.ftz}.dtype.f32  d, a, b, c;\n\n.dtype = { .b16, .b32, .b64,\n           .u16, .u32, .u64,\n           .s16, .s32, .s64,\n                 .f32, .f64 };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("slct", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("b16", "b32", "b64", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64"), Set("s32")), Set(), true), InstructionForm("slct", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("b16", "b32", "b64", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64"), Set("f32")), Set("ftz"), true))
    ),
    InstructionInfo(
      "half-precision-comparison-instructions-set",
      "9.7.7.1. Half Precision Comparison Instructions: set",
      "set",
      "Half Precision Comparison Instructions",
      Vector("set"),
      Vector("set.CmpOp{.ftz}.f16.stype            d, a, b;\nset.CmpOp.BoolOp{.ftz}.f16.stype     d, a, b, {!}c;\n\nset.CmpOp.bf16.stype                 d, a, b;\nset.CmpOp.BoolOp.bf16.stype          d, a, b, {!}c;\n\nset.CmpOp{.ftz}.dtype.f16            d, a, b;\nset.CmpOp.BoolOp{.ftz}.dtype.f16     d, a, b, {!}c;\n.dtype  = { .u16, .s16, .u32, .s32}\n\nset.CmpOp.dtype.bf16                 d, a, b;\nset.CmpOp.BoolOp.dtype.bf16          d, a, b, {!}c;\n.dtype  = { .u16, .s16, .u32, .s32}\n\nset.CmpOp{.ftz}.dtype.f16x2          d, a, b;\nset.CmpOp.BoolOp{.ftz}.dtype.f16x2   d, a, b, {!}c;\n.dtype  = { .f16x2, .u32, .s32}\n\nset.CmpOp.dtype.bf16x2               d, a, b;\nset.CmpOp.BoolOp.dtype.bf16x2        d, a, b, {!}c;\n.dtype  = { .bf16x2, .u32, .s32}\n\n.CmpOp  = { eq, ne, lt, le, gt, ge,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };\n.stype  = { .b16, .b32, .b64,\n            .u16, .u32, .u64,\n            .s16, .s32, .s64,\n            .f16, .f32, .f64};"),
      PtxVersion(4, 2),
      SmTarget(53),
      Vector(InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), Set("f16"), Set("b16", "b32", "b64", "f16", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64")), Set("ftz"), false), InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Vector), Vector(Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), Set("and", "or", "xor"), Set("f16"), Set("b16", "b32", "b64", "f16", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64")), Set("ftz"), false), InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), Set("bf16"), Set("b16", "b32", "b64", "f16", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64")), Set(), false), InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Vector), Vector(Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), Set("and", "or", "xor"), Set("bf16"), Set("b16", "b32", "b64", "f16", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64")), Set(), false), InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), Set("dtype"), Set("f16")), Set("ftz"), false), InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Vector), Vector(Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), Set("and", "or", "xor"), Set("dtype"), Set("f16")), Set("ftz"), false))
    ),
    InstructionInfo(
      "half-precision-comparison-instructions-setp",
      "9.7.7.2. Half Precision Comparison Instructions: setp",
      "setp",
      "Half Precision Comparison Instructions",
      Vector("setp"),
      Vector("setp.CmpOp{.ftz}.f16           p, a, b;\nsetp.CmpOp.BoolOp{.ftz}.f16    p, a, b, {!}c;\n\nsetp.CmpOp{.ftz}.f16x2         p|q, a, b;\nsetp.CmpOp.BoolOp{.ftz}.f16x2  p|q, a, b, {!}c;\n\nsetp.CmpOp.bf16                p, a, b;\nsetp.CmpOp.BoolOp.bf16         p, a, b, {!}c;\n\nsetp.CmpOp.bf16x2              p|q, a, b;\nsetp.CmpOp.BoolOp.bf16x2       p|q, a, b, {!}c;\n\n.CmpOp  = { eq, ne, lt, le, gt, ge,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };"),
      PtxVersion(4, 2),
      SmTarget(53),
      Vector(InstructionForm("setp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), Set("f16")), Set("ftz"), false), InstructionForm("setp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Vector), Vector(Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), Set("and", "or", "xor"), Set("f16")), Set("ftz"), false), InstructionForm("setp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), Set("f16x2")), Set("ftz"), false), InstructionForm("setp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Vector), Vector(Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), Set("and", "or", "xor"), Set("f16x2")), Set("ftz"), false), InstructionForm("setp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), Set("bf16")), Set(), false), InstructionForm("setp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Vector), Vector(Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), Set("and", "or", "xor"), Set("bf16")), Set(), false), InstructionForm("setp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), Set("bf16x2")), Set(), false), InstructionForm("setp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Vector), Vector(Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), Set("and", "or", "xor"), Set("bf16x2")), Set(), false))
    )
  )

  private def chunk6: Vector[InstructionInfo] = Vector(
    InstructionInfo(
      "logic-and-shift-instructions-and",
      "9.7.8.1. Logic and Shift Instructions: and",
      "and",
      "Logic and Shift Instructions",
      Vector("and"),
      Vector("and.type d, a, b;\n\n.type = { .pred, .b16, .b32, .b64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("and", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("b16", "b32", "b64", "pred")), Set(), true))
    ),
    InstructionInfo(
      "logic-and-shift-instructions-or",
      "9.7.8.2. Logic and Shift Instructions: or",
      "or",
      "Logic and Shift Instructions",
      Vector("or"),
      Vector("or.type d, a, b;\n\n.type = { .pred, .b16, .b32, .b64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("or", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("b16", "b32", "b64", "pred")), Set(), true))
    ),
    InstructionInfo(
      "logic-and-shift-instructions-xor",
      "9.7.8.3. Logic and Shift Instructions: xor",
      "xor",
      "Logic and Shift Instructions",
      Vector("xor"),
      Vector("xor.type d, a, b;\n\n.type = { .pred, .b16, .b32, .b64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("xor", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("b16", "b32", "b64", "pred")), Set(), true))
    ),
    InstructionInfo(
      "logic-and-shift-instructions-not",
      "9.7.8.4. Logic and Shift Instructions: not",
      "not",
      "Logic and Shift Instructions",
      Vector("not"),
      Vector("not.type d, a;\n\n.type = { .pred, .b16, .b32, .b64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("not", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("b16", "b32", "b64", "pred")), Set(), true))
    ),
    InstructionInfo(
      "logic-and-shift-instructions-cnot",
      "9.7.8.5. Logic and Shift Instructions: cnot",
      "cnot",
      "Logic and Shift Instructions",
      Vector("cnot"),
      Vector("cnot.type d, a;\n\n.type = { .b16, .b32, .b64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("cnot", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("b16", "b32", "b64")), Set(), true))
    ),
    InstructionInfo(
      "logic-and-shift-instructions-lop3",
      "9.7.8.6. Logic and Shift Instructions: lop3",
      "lop3",
      "Logic and Shift Instructions",
      Vector("lop3"),
      Vector("lop3.b32 d, a, b, c, immLut;\nlop3.BoolOp.b32 d|p, a, b, c, immLut, q;\n\n.BoolOp   = { .or , .and };"),
      PtxVersion(4, 3),
      SmTarget(50),
      Vector(InstructionForm("lop3", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("b32")), Set(), false), InstructionForm("lop3", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Register), Vector(Set("and", "or"), Set("b32")), Set(), false))
    ),
    InstructionInfo(
      "logic-and-shift-instructions-shf",
      "9.7.8.7. Logic and Shift Instructions: shf",
      "shf",
      "Logic and Shift Instructions",
      Vector("shf"),
      Vector("shf.l.mode.b32  d, a, b, c;  // left shift\nshf.r.mode.b32  d, a, b, c;  // right shift\n\n.mode = { .clamp, .wrap };"),
      PtxVersion(3, 1),
      SmTarget(32),
      Vector(InstructionForm("shf", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("l"), Set("clamp", "wrap"), Set("b32")), Set(), true))
    ),
    InstructionInfo(
      "logic-and-shift-instructions-shl",
      "9.7.8.8. Logic and Shift Instructions: shl",
      "shl",
      "Logic and Shift Instructions",
      Vector("shl"),
      Vector("shl.type d, a, b;\n\n.type = { .b16, .b32, .b64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("shl", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("b16", "b32", "b64")), Set(), true))
    ),
    InstructionInfo(
      "logic-and-shift-instructions-shr",
      "9.7.8.9. Logic and Shift Instructions: shr",
      "shr",
      "Logic and Shift Instructions",
      Vector("shr"),
      Vector("shr.type d, a, b;\n\n.type = { .b16, .b32, .b64,\n          .u16, .u32, .u64,\n          .s16, .s32, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("shr", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("b16", "b32", "b64", "s16", "s32", "s64", "u16", "u32", "u64")), Set(), true))
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-mov",
      "9.7.9.3. Data Movement and Conversion Instructions: mov",
      "mov",
      "Data Movement and Conversion Instructions",
      Vector("mov"),
      Vector("mov.type  d, a;\nmov.type  d, sreg;\nmov.type  d, avar;       // get address of variable\nmov.type  d, avar+imm;   // get address of variable with offset\nmov.u32   d, fname;      // get address of device function\nmov.u64   d, fname;      // get address of device function\nmov.u32   d, kernel;     // get address of entry function\nmov.u64   d, kernel;     // get address of entry function\n\n.type = { .pred,\n          .b16, .b32, .b64,\n          .u16, .u32, .u64,\n          .s16, .s32, .s64,\n                .f32, .f64 };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("mov", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("b16", "b32", "b64", "f32", "f64", "pred", "s16", "s32", "s64", "u16", "u32", "u64")), Set(), false))
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-mov-2",
      "9.7.9.4. Data Movement and Conversion Instructions: mov",
      "mov",
      "Data Movement and Conversion Instructions",
      Vector("mov"),
      Vector("mov.type  d, a;\n\n.type = { .b16, .b32, .b64, .b128 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("mov", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("b128", "b16", "b32", "b64")), Set(), false))
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-shfl",
      "9.7.9.5. Data Movement and Conversion Instructions: shfl (deprecated)",
      "shfl (deprecated)",
      "Data Movement and Conversion Instructions",
      Vector("shfl", "add"),
      Vector("shfl.mode.b32  d[|p], a, b, c;\n\n.mode = { .up, .down, .bfly, .idx };"),
      PtxVersion(3, 0),
      SmTarget(30),
      Vector(InstructionForm("shfl", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("bfly", "down", "idx", "up"), Set("b32")), Set(), false))
    )
  )

  private def chunk7: Vector[InstructionInfo] = Vector(
    InstructionInfo(
      "data-movement-and-conversion-instructions-shfl-sync",
      "9.7.9.6. Data Movement and Conversion Instructions: shfl.sync",
      "shfl.sync",
      "Data Movement and Conversion Instructions",
      Vector("shfl"),
      Vector("shfl.sync.mode.b32  d[|p], a, b, c, membermask;\n\n.mode = { .up, .down, .bfly, .idx };"),
      PtxVersion(6, 0),
      SmTarget(30),
      Vector(InstructionForm("shfl", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("sync"), Set("bfly", "down", "idx", "up"), Set("b32")), Set(), true))
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-prmt",
      "9.7.9.7. Data Movement and Conversion Instructions: prmt",
      "prmt",
      "Data Movement and Conversion Instructions",
      Vector("prmt"),
      Vector("prmt.b32{.mode}  d, a, b, c;\n\n.mode = { .f4e, .b4e, .rc8, .ecl, .ecr, .rc16 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("prmt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("b32")), Set("b4e", "ecl", "ecr", "f4e", "rc16", "rc8"), true))
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-ld",
      "9.7.9.8. Data Movement and Conversion Instructions: ld",
      "ld",
      "Data Movement and Conversion Instructions",
      Vector("ld"),
      Vector("ld{.weak}{.ss}{.cop}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{.unified}{, cache_policy};\n\nld{.weak}{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{.unified}{, cache_policy};\n\nld.volatile{.ss}{.level::prefetch_size}{.vec}.type  d, [a];\n\nld.relaxed.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{, cache_policy};\n\nld.acquire.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{, cache_policy};\n\nld.mmio.sem.sys{.global}.type  d, [a];\n\n.ss =                       { .const, .global, .local, .param{::entry, ::func}, .shared{::cta, ::cluster} };\n.cop =                      { .ca, .cg, .cs, .lu, .cv };\n.sem =                      { .acquire, .relaxed };\n.level1::eviction_priority = { .L1::evict_normal, .L1::evict_unchanged,\n                               .L1::evict_first, .L1::evict_last, .L1::no_allocate };\n.level2::eviction_priority = {.L2::evict_normal, .L2::evict_first, .L2::evict_last};\n.level::cache_hint =        { .L2::cache_hint };\n.level::prefetch_size =     { .L2::64B, .L2::128B, .L2::256B }\n.scope =                    { .cta, .cluster, .gpu, .sys };\n.vec =                      { .v2, .v4, .v8 };\n.type =                     { .b8, .b16, .b32, .b64, .b128,\n                              .u8, .u16, .u32, .u64,\n                              .s8, .s16, .s32, .s64,\n                              .f32, .f64 };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("ld", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), Set("ca", "cg", "cs", "cv", "level::cache_hint", "level::prefetch_size", "lu", "ss", "v2", "v4", "v8", "weak"), false), InstructionForm("ld", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), Set("level1::eviction_priority", "level2::eviction_priority", "level::cache_hint", "level::prefetch_size", "ss", "v2", "v4", "v8", "weak"), false), InstructionForm("ld", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(Set("volatile"), Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), Set("level::prefetch_size", "ss", "v2", "v4", "v8"), false), InstructionForm("ld", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(Set("relaxed"), Set("cluster", "cta", "gpu", "sys"), Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), Set("level1::eviction_priority", "level2::eviction_priority", "level::cache_hint", "level::prefetch_size", "ss", "v2", "v4", "v8"), false), InstructionForm("ld", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(Set("acquire"), Set("cluster", "cta", "gpu", "sys"), Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), Set("level1::eviction_priority", "level2::eviction_priority", "level::cache_hint", "level::prefetch_size", "ss", "v2", "v4", "v8"), false), InstructionForm("ld", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(Set("mmio"), Set("acquire", "relaxed"), Set("sys"), Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), Set("global"), false))
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-ld-global-nc",
      "9.7.9.9. Data Movement and Conversion Instructions: ld.global.nc",
      "ld.global.nc",
      "Data Movement and Conversion Instructions",
      Vector("ld", "createpolicy"),
      Vector("ld.global{.cop}.nc{.level::cache_hint}{.level::prefetch_size}.type                 d, [a]{, cache_policy};\nld.global{.cop}.nc{.level::cache_hint}{.level::prefetch_size}.vec.type             d, [a]{, cache_policy};\n\nld.global.nc{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}.type      d, [a]{, cache_policy};\nld.global.nc{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}.vec.type  d, [a]{, cache_policy};\n\n.cop  =                     { .ca, .cg, .cs };     // cache operation\n.level1::eviction_priority = { .L1::evict_normal, .L1::evict_unchanged,\n                               .L1::evict_first, .L1::evict_last, .L1::no_allocate};\n.level2::eviction_priority = {.L2::evict_normal, .L2::evict_first, .L2::evict_last};\n.level::cache_hint =        { .L2::cache_hint };\n.level::prefetch_size =     { .L2::64B, .L2::128B, .L2::256B }\n.vec  =                     { .v2, .v4, .v8 };\n.type =                     { .b8, .b16, .b32, .b64, .b128,\n                              .u8, .u16, .u32, .u64,\n                              .s8, .s16, .s32, .s64,\n                              .f32, .f64 };"),
      PtxVersion(3, 1),
      SmTarget(32),
      Vector(InstructionForm("ld", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(Set("global"), Set("nc"), Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), Set("ca", "cg", "cs", "level::cache_hint", "level::prefetch_size"), false), InstructionForm("ld", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(Set("global"), Set("nc"), Set("v2", "v4", "v8"), Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), Set("ca", "cg", "cs", "level::cache_hint", "level::prefetch_size"), false), InstructionForm("ld", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(Set("global"), Set("nc"), Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), Set("level1::eviction_priority", "level2::eviction_priority", "level::cache_hint", "level::prefetch_size"), false), InstructionForm("ld", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(Set("global"), Set("nc"), Set("v2", "v4", "v8"), Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), Set("level1::eviction_priority", "level2::eviction_priority", "level::cache_hint", "level::prefetch_size"), false))
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-ldu",
      "9.7.9.10. Data Movement and Conversion Instructions: ldu",
      "ldu",
      "Data Movement and Conversion Instructions",
      Vector("ldu"),
      Vector("ldu{.ss}.type      d, [a];       // load from address\nldu{.ss}.vec.type  d, [a];       // vec load from address\n\n.ss   = { .global };             // state space\n.vec  = { .v2, .v4 };\n.type = { .b8, .b16, .b32, .b64, .b128,\n          .u8, .u16, .u32, .u64,\n          .s8, .s16, .s32, .s64,\n                     .f32, .f64 };"),
      PtxVersion(2, 0),
      SmTarget(13),
      Vector(InstructionForm("ldu", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), Set("global"), false))
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-st",
      "9.7.9.11. Data Movement and Conversion Instructions: st",
      "st",
      "Data Movement and Conversion Instructions",
      Vector("st", "createpolicy"),
      Vector("st{.weak}{.ss}{.cop}{.level::cache_hint}{.vec}.type   [a], b{, cache_policy};\nst{.weak}{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.vec}.type\n                                                      [a], b{, cache_policy};\nst.volatile{.ss}{.vec}.type                           [a], b;\nst.relaxed.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.vec}.type\n                                                      [a], b{, cache_policy};\nst.release.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.vec}.type\n                                                      [a], b{, cache_policy};\nst.mmio.sem.sys{.global}.type         [a], b;\n\n.ss =                       { .global, .local, .param{::func}, .shared{::cta, ::cluster} };\n.sem =                      { .release, .relaxed };\n.level1::eviction_priority = { .L1::evict_normal, .L1::evict_unchanged,\n                               .L1::evict_first, .L1::evict_last, .L1::no_allocate };\n.level2::eviction_priority = { .L2::evict_normal, .L2::evict_first, .L2::evict_last };\n.level::cache_hint =        { .L2::cache_hint };\n.cop =                      { .wb, .cg, .cs, .wt };\n.sem =                      { .relaxed, .release };\n.scope =                    { .cta, .cluster, .gpu, .sys };\n.vec =                      { .v2, .v4, .v8 };\n.type =                     { .b8, .b16, .b32, .b64, .b128,\n                              .u8, .u16, .u32, .u64,\n                              .s8, .s16, .s32, .s64,\n                              .f32, .f64 };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("st", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), Set("cg", "cs", "level::cache_hint", "ss", "v2", "v4", "v8", "wb", "weak", "wt"), false), InstructionForm("st", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), Set("level1::eviction_priority", "level2::eviction_priority", "level::cache_hint", "ss", "v2", "v4", "v8", "weak"), false), InstructionForm("st", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("volatile"), Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), Set("ss", "v2", "v4", "v8"), false), InstructionForm("st", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("relaxed"), Set("cluster", "cta", "gpu", "sys"), Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), Set("level1::eviction_priority", "level2::eviction_priority", "level::cache_hint", "ss", "v2", "v4", "v8"), false), InstructionForm("st", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("release"), Set("cluster", "cta", "gpu", "sys"), Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), Set("level1::eviction_priority", "level2::eviction_priority", "level::cache_hint", "ss", "v2", "v4", "v8"), false), InstructionForm("st", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("mmio"), Set("relaxed", "release"), Set("sys"), Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), Set("global"), false))
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-st-async",
      "9.7.9.12. Data Movement and Conversion Instructions: st.async",
      "st.async",
      "Data Movement and Conversion Instructions",
      Vector("st"),
      Vector("st.async{.weak}{.ss}.completion_mechanism{.vec}.type [a], b, [mbar];\n\n.ss   =                 { .shared::cluster };\n.type =                 { .b32, .b64, .b128,\n                          .u32, .u64,\n                          .s32, .s64,\n                          .f32, .f64 };\n.vec  =                 { .v2, .v4 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\nst.async{.mmio}.sem.scope{.ss}.type [a], b;\n\n.sem =                  { .release };\n.scope =                { .gpu, .sys };\n.ss =                   { .global };\n.type =                 { .b8, .b16, .b32, .b64,\n                          .u8, .u16, .u32, .u64,\n                          .s8, .s16, .s32, .s64,\n                                     .f32, .f64 };"),
      PtxVersion(8, 1),
      SmTarget(90),
      Vector(InstructionForm("st", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(Set("async"), Set("mbarrier::complete_tx::bytes"), Set("b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), Set("global", "v2", "v4", "weak"), false))
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-multimem-st-async",
      "9.7.9.13. Data Movement and Conversion Instructions: multimem.st.async",
      "multimem.st.async",
      "Data Movement and Conversion Instructions",
      Vector("multimem"),
      Vector("multimem.st.async.sem.scope{.ss}.type [a], b;\n\n.sem   = { .release };\n.scope = { .gpu, .sys };\n.ss    = { .global };\n.type  = { .b8, .b16, .b32, .b64,\n            .u8, .u16, .u32, .u64,\n            .s8, .s16, .s32, .s64,\n            .f32, .f64 };"),
      PtxVersion(9, 3),
      SmTarget(100),
      Vector(InstructionForm("multimem", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("st"), Set("async"), Set("release"), Set("gpu", "sys"), Set("b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), Set("global"), true))
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-st-bulk",
      "9.7.9.14. Data Movement and Conversion Instructions: st.bulk",
      "st.bulk",
      "Data Movement and Conversion Instructions",
      Vector("st"),
      Vector("st.bulk{.weak}{.shared::cta}  [a], size, initval; // initval must be zero"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("st", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("bulk")), Set("shared::cta", "weak"), true))
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-multimem",
      "9.7.9.15. Data Movement and Conversion Instructions: multimem.ld_reduce, multimem.st, multimem.red",
      "multimem.ld_reduce, multimem.st, multimem.red",
      "Data Movement and Conversion Instructions",
      Vector("multimem"),
      Vector("// Integer type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op.type      d, [a];\nmultimem.ld_reduce.weak{.ss}.op.type                 d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}.type                [a], b;\nmultimem.st.weak{.ss}.type                           [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.op.type           [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add, .and, .or, .xor }\n.type =     { .b32, .b64,  .u32, .u64, .s32, .s64 }\n\n// Floating point type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op{.acc_prec}{.vec}.type    d, [a];\nmultimem.ld_reduce.weak{.ss}.op{.acc_prec}{.vec}.type               d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}{.vec}.type                         [a], b;\nmultimem.st.weak{.ss}{.vec}.type                                    [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.redop{.vec}.redtype              [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add }\n.redop  =   { .add }\n.acc_prec = { .acc::f32, .acc::f16 }\n.vec =      { .v2, .v4, .v8 }\n.type=      { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64, .e5m2, .e5m2x2, .e5m2x4, .e4m3, .e4m3x2, .e4m3x4 }\n.redtype =  { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64 }"),
      PtxVersion(8, 1),
      SmTarget(90),
      Vector(InstructionForm("multimem", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(Set("ld_reduce"), Set("weak"), Set("op"), Set("type")), Set("ss"), false), InstructionForm("multimem", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("st"), Set("type")), Set("scope", "ss", "stsem"), false), InstructionForm("multimem", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("st"), Set("weak"), Set("type")), Set("ss"), false), InstructionForm("multimem", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("red"), Set("op"), Set("type")), Set("redsem", "scope", "ss"), false))
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-prefetch-prefetchu",
      "9.7.9.16. Data Movement and Conversion Instructions: prefetch, prefetchu",
      "prefetch, prefetchu",
      "Data Movement and Conversion Instructions",
      Vector("prefetch", "prefetchu"),
      Vector("prefetch{.space}.level                    [a];   // prefetch to data cache\nprefetch.global.level::eviction_priority  [a];   // prefetch to data cache\n\nprefetchu.L1  [a];             // prefetch to uniform cache\n\nprefetch{.tensormap_space}.tensormap [a];  // prefetch the tensormap\n\n.space =                    { .global, .local };\n.level =                    { .L1, .L2 };\n.level::eviction_priority = { .L2::evict_last, .L2::evict_normal };\n.tensormap_space =          { .const, .param };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("prefetch", Vector(OperandConstraint.Address), Vector(Set("L1", "L2")), Set("global", "local"), false))
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-applypriority",
      "9.7.9.17. Data Movement and Conversion Instructions: applypriority",
      "applypriority",
      "Data Movement and Conversion Instructions",
      Vector("applypriority"),
      Vector("applypriority{.global}.level::eviction_priority  [a], size;\n\n.level::eviction_priority = { .L2::evict_normal };"),
      PtxVersion(7, 4),
      SmTarget(80),
      Vector(InstructionForm("applypriority", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("level::eviction_priority")), Set("global"), true))
    )
  )

  private def chunk8: Vector[InstructionInfo] = Vector(
    InstructionInfo(
      "data-movement-and-conversion-instructions-discard",
      "9.7.9.18. Data Movement and Conversion Instructions: discard",
      "discard",
      "Data Movement and Conversion Instructions",
      Vector("discard", "ld"),
      Vector("discard{.global}.level  [a], size;\n\n.level = { .L2 };"),
      PtxVersion(7, 4),
      SmTarget(80),
      Vector(InstructionForm("discard", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("L2")), Set("global"), true))
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-createpolicy",
      "9.7.9.19. Data Movement and Conversion Instructions: createpolicy",
      "createpolicy",
      "Data Movement and Conversion Instructions",
      Vector("createpolicy", "cache_policy"),
      Vector("// Range-based policy\ncreatepolicy.range{.global}.level::primary_priority{.level::secondary_priority}.b64\n                                   cache_policy, [a], primary-size, total-size;\n\n// Fraction-based policy\ncreatepolicy.fractional.level::primary_priority{.level::secondary_priority}.b64\n                                   cache_policy{, fraction};\n\n// Converting the access property from CUDA APIs\ncreatepolicy.cvt.L2.b64            cache_policy, access-property;\n\n.level::primary_priority =   { .L2::evict_last, .L2::evict_normal,\n                               .L2::evict_first, .L2::evict_unchanged };\n.level::secondary_priority = { .L2::evict_first, .L2::evict_unchanged };"),
      PtxVersion(7, 4),
      SmTarget(80),
      Vector()
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-isspacep",
      "9.7.9.20. Data Movement and Conversion Instructions: isspacep",
      "isspacep",
      "Data Movement and Conversion Instructions",
      Vector("isspacep"),
      Vector("isspacep.space  p, a;    // result is .pred\n\n.space = { const, .global, .local, .shared{::cta, ::cluster}, .param{::entry} };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("isspacep", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("space")), Set(), false))
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-cvta",
      "9.7.9.21. Data Movement and Conversion Instructions: cvta",
      "cvta",
      "Data Movement and Conversion Instructions",
      Vector("cvta"),
      Vector("// convert const, global, local, or shared address to generic address\ncvta.space.size  p, a;        // source address in register a\ncvta.space.size  p, var;      // get generic address of var\ncvta.space.size  p, var+imm;  // generic address of var+offset\n\n// convert generic address to const, global, local, or shared address\ncvta.to.space.size  p, a;\n\n.space = { .const, .global, .local, .shared{::cta, ::cluster}, .param{::entry} };\n.size  = { .u32, .u64 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector()
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-cvt",
      "9.7.9.22. Data Movement and Conversion Instructions: cvt",
      "cvt",
      "Data Movement and Conversion Instructions",
      Vector("cvt"),
      Vector("cvt{.irnd}{.ftz}{.sat}.dtype.atype         d, a;  // integer rounding\ncvt{.frnd}{.ftz}{.sat}.dtype.atype         d, a;  // fp rounding\n\ncvt.frnd2{.relu}{.satfinite}.f16.f32       d, a;\ncvt.frnd2{.relu}{.satfinite}.f16x2.f32     d, a, b;\ncvt.rs{.relu}{.satfinite}.f16x2.f32        d, a, b, rbits;\n\ncvt.frnd2{.relu}{.satfinite}.bf16.f32      d, a;\ncvt.frnd2{.relu}{.satfinite}.bf16x2.f32    d, a, b;\ncvt.rs{.relu}{.satfinite}.bf16x2.f32       d, a, b, rbits;\n\ncvt.rna{.satfinite}.tf32.f32               d, a;\ncvt.frnd2{.satfinite}{.relu}.tf32.f32      d, a;\n\ncvt.rn.satfinite{.relu}.f8x2type.f32       d, a, b;\ncvt.rn.satfinite{.relu}.f8x2type.fp16x2    d, a;\ncvt.rn{.relu}.f16x2.f8x2type               d, a;\ncvt.rn{.relu}{.satfinite}{.scaled::n2::ue8m0}.bf16x2.f8x2type d, a{, scale-factor};\ncvt.rs{.relu}.satfinite.f8x4type.f32       d, {a, b, e, f}, rbits;\n\ncvt.rn.satfinite{.relu}.f4x2type.f32        d, a, b;\ncvt.rn.satfinite{.relu}.f4x2type.fp16x2type d, a;\ncvt.rn{.relu}.f16x2.f4x2type                d, a;\ncvt.rn{.relu}{.satfinite}{.scaled::n2::ue8m0}.bf16x2.f4x2type d, a{, scale-factor};\ncvt.rs{.relu}.satfinite.f4x4type.f32        d, {a, b, e, f}, rbits;\n\ncvt.rn.satfinite{.relu}.f6x2type.f32        d, a, b;\ncvt.rn.satfinite{.relu}.f6x2type.fp16x2type d, a;\ncvt.rn{.relu}.f16x2.f6x2type                d, a;\ncvt.rn{.relu}{.satfinite}{.scaled::n2::ue8m0}.bf16x2.f6x2type d, a{, scale-factor};\ncvt.rs{.relu}.satfinite.f6x4type.f32        d, {a, b, e, f}, rbits;\n\ncvt.frnd3{.satfinite}.ue8m0x2.f32          d, a, b;\ncvt.frnd3{.satfinite}.ue8m0x2.bf16x2       d, a;\ncvt.rn.bf16x2.ue8m0x2                      d, a;\n\ncvt.rn.satfinite{.relu}{.scaled::n2::ue8m0}.s2f6x2.f32      d, a, b{, scale-factor};\ncvt.rn.satfinite{.relu}{.scaled::n2::ue8m0}.s2f6x2.bf16x2   d, a{, scale-factor};\ncvt.rn{.satfinite}{.relu}{.scaled::n2::ue8m0}.bf16x2.s2f6x2 d, a{, scale-factor};\n\n.irnd   = { .rni, .rzi, .rmi, .rpi };\n.frnd   = { .rn,  .rz,  .rm,  .rp  };\n.frnd2  = { .rn,  .rz };\n.frnd3  = { .rz,  .rp };\n.dtype = .atype = { .u8,   .u16, .u32, .u64,\n                    .s8,   .s16, .s32, .s64,\n                    .bf16, .f16, .f32, .f64 };\n.f8x2type = { .e4m3x2, .e5m2x2 };\n.f4x2type = { .e2m1x2 };\n.f6x2type = { .e2m3x2, .e3m2x2 };\n.f4x4type = { .e2m1x4 };\n.f8x4type = { .e4m3x4, .e5m2x4 };\n.f6x4type = { .e2m3x4, .e3m2x4 };\n.fp16x2type = { .f16x2, .bf16x2 };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("dtype"), Set("bf16", "f16", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), Set("ftz", "rmi", "rni", "rpi", "rzi", "sat"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("rn", "rz"), Set("f16x2"), Set("f32")), Set("relu", "satfinite"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("rs"), Set("f16x2"), Set("f32")), Set("relu", "satfinite"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("rn", "rz"), Set("bf16"), Set("f32")), Set("relu", "satfinite"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("rn", "rz"), Set("bf16x2"), Set("f32")), Set("relu", "satfinite"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("rs"), Set("bf16x2"), Set("f32")), Set("relu", "satfinite"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("rna"), Set("tf32"), Set("f32")), Set("satfinite"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("rn", "rz"), Set("tf32"), Set("f32")), Set("relu", "satfinite"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("rn"), Set("satfinite"), Set("e4m3x2", "e5m2x2"), Set("f32")), Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("rn"), Set("satfinite"), Set("e4m3x2", "e5m2x2"), Set("fp16x2")), Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("rn"), Set("f16x2"), Set("e4m3x2", "e5m2x2")), Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("rn"), Set("bf16x2"), Set("e4m3x2", "e5m2x2")), Set("relu", "satfinite", "scaled::n2::ue8m0"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Vector, OperandConstraint.Value), Vector(Set("rs"), Set("satfinite"), Set("e4m3x4", "e5m2x4"), Set("f32")), Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("rn"), Set("satfinite"), Set("e2m1x2"), Set("f32")), Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("rn"), Set("satfinite"), Set("e2m1x2"), Set("bf16x2", "f16x2")), Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("rn"), Set("f16x2"), Set("e2m1x2")), Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("rn"), Set("bf16x2"), Set("e2m1x2")), Set("relu", "satfinite", "scaled::n2::ue8m0"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Vector, OperandConstraint.Value), Vector(Set("rs"), Set("satfinite"), Set("e2m1x4"), Set("f32")), Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("rn"), Set("satfinite"), Set("e2m3x2", "e3m2x2"), Set("f32")), Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("rn"), Set("satfinite"), Set("e2m3x2", "e3m2x2"), Set("bf16x2", "f16x2")), Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("rn"), Set("f16x2"), Set("e2m3x2", "e3m2x2")), Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("rn"), Set("bf16x2"), Set("e2m3x2", "e3m2x2")), Set("relu", "satfinite", "scaled::n2::ue8m0"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Vector, OperandConstraint.Value), Vector(Set("rs"), Set("satfinite"), Set("e2m3x4", "e3m2x4"), Set("f32")), Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("rp", "rz"), Set("ue8m0x2"), Set("f32")), Set("satfinite"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("rp", "rz"), Set("ue8m0x2"), Set("bf16x2")), Set("satfinite"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("rn"), Set("bf16x2"), Set("ue8m0x2")), Set(), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("rn"), Set("satfinite"), Set("s2f6x2"), Set("f32")), Set("relu", "scaled::n2::ue8m0"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("rn"), Set("satfinite"), Set("s2f6x2"), Set("bf16x2")), Set("relu", "scaled::n2::ue8m0"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("rn"), Set("bf16x2"), Set("s2f6x2")), Set("relu", "satfinite", "scaled::n2::ue8m0"), false))
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-cvt-pack",
      "9.7.9.23. Data Movement and Conversion Instructions: cvt.pack",
      "cvt.pack",
      "Data Movement and Conversion Instructions",
      Vector("cvt"),
      Vector("cvt.pack.sat.convertType.abType  d, a, b;\n    .convertType  = { .u16, .s16 }\n    .abType       = { .s32 }\n\ncvt.pack.sat.convertType.abType.cType  d, a, b, c;\n    .convertType  = { .u2, .s2, .u4, .s4, .u8, .s8 }\n    .abType       = { .s32 }\n    .cType        = { .b32 }"),
      PtxVersion(6, 5),
      SmTarget(72),
      Vector(InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("pack"), Set("sat"), Set("convertType"), Set("abType")), Set(), false))
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-mapa",
      "9.7.9.24. Data Movement and Conversion Instructions: mapa",
      "mapa",
      "Data Movement and Conversion Instructions",
      Vector("mapa"),
      Vector("mapa{.space}.type          d, a, b;\n\n// Maps shared memory address in register a into CTA b.\nmapa.shared::cluster.type  d, a, b;\n\n// Maps shared memory variable into CTA b.\nmapa.shared::cluster.type  d, sh, b;\n\n// Maps shared memory variable into CTA b.\nmapa.shared::cluster.type  d, sh + imm, b;\n\n// Maps generic address in register a into CTA b.\nmapa.type                  d, a, b;\n\n.space = { .shared::cluster }\n.type  = { .u32, .u64 }"),
      PtxVersion(7, 8),
      SmTarget(90),
      Vector(InstructionForm("mapa", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("type")), Set("space"), true))
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-getctarank",
      "9.7.9.25. Data Movement and Conversion Instructions: getctarank",
      "getctarank",
      "Data Movement and Conversion Instructions",
      Vector("getctarank"),
      Vector("getctarank{.space}.type d, a;\n\n// Get cta rank from source shared memory address in register a.\ngetctarank.shared::cluster.type d, a;\n\n// Get cta rank from shared memory variable.\ngetctarank.shared::cluster.type d, var;\n\n// Get cta rank from shared memory variable+offset.\ngetctarank.shared::cluster.type d, var + imm;\n\n// Get cta rank from generic address of shared memory variable in register a.\ngetctarank.type d, a;\n\n.space = { .shared::cluster }\n.type  = { .u32, .u64 }"),
      PtxVersion(7, 8),
      SmTarget(90),
      Vector(InstructionForm("getctarank", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("type")), Set("space"), true))
    ),
    InstructionInfo(
      "data-movement-and-conversion-instructions-tensormap-replace",
      "9.7.9.27. Data Movement and Conversion Instructions: tensormap.replace",
      "tensormap.replace",
      "Data Movement and Conversion Instructions",
      Vector("tensormap"),
      Vector("tensormap.replace.mode.field1{.ss}.b1024.type  [addr], new_val;\ntensormap.replace.mode.field2{.ss}.b1024.type  [addr], ord, new_val;\ntensormap.replace.mode.field3{.ss}.b1024.type  [addr], new_val;\n\n.mode    = { .tile }\n.field1  = { .global_address, .rank }\n.field2  = { .box_dim, .global_dim, .global_stride, .element_stride  }\n.field3  = { .elemtype,  .interleave_layout, .swizzle_mode, .swizzle_atomicity, .fill_mode }\n.ss      = { .global, .shared::cta }\n.type    = { .b32, .b64 }"),
      PtxVersion(8, 3),
      SmTarget(100),
      Vector(InstructionForm("tensormap", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("replace"), Set("mode"), Set("field1"), Set("b1024"), Set("type")), Set("ss"), false), InstructionForm("tensormap", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("replace"), Set("mode"), Set("field2"), Set("b1024"), Set("type")), Set("ss"), false), InstructionForm("tensormap", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("replace"), Set("mode"), Set("field3"), Set("b1024"), Set("type")), Set("ss"), false))
    ),
    InstructionInfo(
      "fabric-instructions-try-get",
      "9.7.10.5.1. Fabric Instructions: fabric.try_get",
      "fabric.try_get",
      "Fabric Instructions",
      Vector("fabric"),
      Vector("fabric.try_get.async.dst.completion_mechanism.sem.scope.b128 [dst], [srcLeId, srcDataOff], size, [bar];\n\n.dst = { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes.mbarrier::report::fabric }\n.sem = { .relaxed }\n.scope = { .sys }"),
      PtxVersion(9, 3),
      SmTarget(100),
      Vector(InstructionForm("fabric", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(Set("try_get"), Set("async"), Set("dst"), Set("completion_mechanism"), Set("sem"), Set("scope"), Set("b128")), Set(), true))
    ),
    InstructionInfo(
      "fabric-instructions-try-put",
      "9.7.10.5.2. Fabric Instructions: fabric.try_put",
      "fabric.try_put",
      "Fabric Instructions",
      Vector("fabric"),
      Vector("fabric.try_put.async{.multimem}.src.completion_mechanism0.sem.scope.b128 [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_put.async{.multimem}.src.completion_mechanism0.cp_mask.sem.scope.b128 [dstLeId, dstDataOff], [src], size, [bar], bytemask;\n\nfabric.try_put.async{.multimem}.src.completion_mechanism1.sem.scope.b128 [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\n.src = { .shared::cta }\n.completion_mechanism0 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric }\n.completion_mechanism1 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric.counted::bytes }\n.sem = { .relaxed }\n.scope = { .sys }"),
      PtxVersion(9, 3),
      SmTarget(100),
      Vector(InstructionForm("fabric", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(Set("try_put"), Set("async"), Set("src"), Set("completion_mechanism0"), Set("sem"), Set("scope"), Set("b128")), Set("multimem"), true), InstructionForm("fabric", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("try_put"), Set("async"), Set("src"), Set("completion_mechanism0"), Set("cp_mask"), Set("sem"), Set("scope"), Set("b128")), Set("multimem"), true), InstructionForm("fabric", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(Set("try_put"), Set("async"), Set("src"), Set("completion_mechanism1"), Set("sem"), Set("scope"), Set("b128")), Set("multimem"), true))
    ),
    InstructionInfo(
      "fabric-instructions-try-red",
      "9.7.10.5.3. Fabric Instructions: fabric.try_red",
      "fabric.try_red",
      "Fabric Instructions",
      Vector("fabric"),
      Vector("fabric.try_red.async{.multimem}.src.completion_mechanism0.sem.scope.redOpBit.typeBit [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism0.sem.scope.redOpMinMax.typeMinMax [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism0.sem.scope.redOpArith.typeArith [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism1.sem.scope.redOpBit.typeBit [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism1.sem.scope.redOpMinMax.typeMinMax [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism1.sem.scope.redOpAdd.typeAdd [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\n.src = { .shared::cta }\n.completion_mechanism0 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric }\n.completion_mechanism1 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric.counted::bytes }\n.sem = { .relaxed }\n.scope = { .sys }\n.redOpBit = { .and, .xor, .or }\n.typeBit = { .b32, .b64 }\n.redOpMinMax = { .min, .max }\n.typeMinMax = { .u32, .s32, .u64, .s64, .f16, .bf16 }\n.redOpArith = { .add }\n.typeArith = { .u32, .u64, .f16, .bf16, .f32, .f64 }\n.redOpAdd = { .add }\n.typeAdd = { .u32, .u64, .f16, .bf16, .f32, .f64 }"),
      PtxVersion(9, 3),
      SmTarget(100),
      Vector(InstructionForm("fabric", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(Set("try_red"), Set("async"), Set("src"), Set("completion_mechanism0"), Set("sem"), Set("scope"), Set("redOpBit"), Set("typeBit")), Set("multimem"), true), InstructionForm("fabric", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(Set("try_red"), Set("async"), Set("src"), Set("completion_mechanism0"), Set("sem"), Set("scope"), Set("redOpMinMax"), Set("typeMinMax")), Set("multimem"), true), InstructionForm("fabric", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(Set("try_red"), Set("async"), Set("src"), Set("completion_mechanism0"), Set("sem"), Set("scope"), Set("redOpArith"), Set("typeArith")), Set("multimem"), true), InstructionForm("fabric", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(Set("try_red"), Set("async"), Set("src"), Set("completion_mechanism1"), Set("sem"), Set("scope"), Set("redOpBit"), Set("typeBit")), Set("multimem"), true), InstructionForm("fabric", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(Set("try_red"), Set("async"), Set("src"), Set("completion_mechanism1"), Set("sem"), Set("scope"), Set("redOpMinMax"), Set("typeMinMax")), Set("multimem"), true), InstructionForm("fabric", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(Set("try_red"), Set("async"), Set("src"), Set("completion_mechanism1"), Set("sem"), Set("scope"), Set("redOpAdd"), Set("typeAdd")), Set("multimem"), true))
    )
  )

  private def chunk9: Vector[InstructionInfo] = Vector(
    InstructionInfo(
      "fabric-instructions-try-pullred",
      "9.7.10.5.4. Fabric Instructions: fabric.try_pullred",
      "fabric.try_pullred",
      "Fabric Instructions",
      Vector("fabric"),
      Vector("fabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpBit.typeBit.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpMinMax.typeMinMax.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpAdd.typeAdd.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpAddAcc16.typeAddAcc16.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpAddAcc32.typeAddAcc32.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\n.dst = { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes.mbarrier::report::fabric }\n.sem = { .relaxed }\n.scope = { .sys }\n.redOpBit = { .and, .xor, .or }\n.typeBit = { .b32, .b64 }\n.redOpMinMax = { .min, .max }\n.typeMinMax = { .u32, .s32, .u64, .s64, .bf16, .f16, .e4m3, .e5m2 }\n.redOpAdd = { .add }\n.typeAdd = { .u32, .u64, .f16, .bf16, .f32 }\n.redOpAddAcc16 = { .add.acc::f16 }\n.typeAddAcc16 = { .e4m3, .e5m2 }\n.redOpAddAcc32 = { .add.acc::f32 }\n.typeAddAcc32 = { .f16, .bf16 }"),
      PtxVersion(9, 3),
      SmTarget(100),
      Vector(InstructionForm("fabric", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("try_pullred"), Set("async"), Set("multimem"), Set("dst"), Set("completion_mechanism"), Set("sem"), Set("scope"), Set("redOpBit"), Set("typeBit"), Set("sync")), Set(), false), InstructionForm("fabric", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("try_pullred"), Set("async"), Set("multimem"), Set("dst"), Set("completion_mechanism"), Set("sem"), Set("scope"), Set("redOpMinMax"), Set("typeMinMax"), Set("sync")), Set(), false), InstructionForm("fabric", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("try_pullred"), Set("async"), Set("multimem"), Set("dst"), Set("completion_mechanism"), Set("sem"), Set("scope"), Set("redOpAdd"), Set("typeAdd"), Set("sync")), Set(), false), InstructionForm("fabric", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("try_pullred"), Set("async"), Set("multimem"), Set("dst"), Set("completion_mechanism"), Set("sem"), Set("scope"), Set("redOpAddAcc16"), Set("typeAddAcc16"), Set("sync")), Set(), false), InstructionForm("fabric", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("try_pullred"), Set("async"), Set("multimem"), Set("dst"), Set("completion_mechanism"), Set("sem"), Set("scope"), Set("redOpAddAcc32"), Set("typeAddAcc32"), Set("sync")), Set(), false))
    ),
    InstructionInfo(
      "fabric-instructions-submit",
      "9.7.10.5.5. Fabric Instructions: fabric.submit",
      "fabric.submit",
      "Fabric Instructions",
      Vector("fabric"),
      Vector("fabric.submit{.submitop};\n\n.submitop = { .op_restrict::fetching }"),
      PtxVersion(9, 3),
      SmTarget(100),
      Vector()
    ),
    InstructionInfo(
      "fabric-instructions-wait",
      "9.7.10.5.6. Fabric Instructions: fabric.wait",
      "fabric.wait",
      "Fabric Instructions",
      Vector("fabric"),
      Vector("fabric.wait.sync_restrict::reads;"),
      PtxVersion(9, 3),
      SmTarget(100),
      Vector()
    ),
    InstructionInfo(
      "texture-instructions-tex",
      "9.7.11.3. Texture Instructions: tex",
      "tex",
      "Texture Instructions",
      Vector("tex"),
      Vector("tex.geom.v4.dtype.ctype  d, [a, c] {, e} {, f};\ntex.geom.v4.dtype.ctype  d[|p], [a, b, c] {, e} {, f};  // explicit sampler\n\ntex.geom.v2.f16x2.ctype  d[|p], [a, c] {, e} {, f};\ntex.geom.v2.f16x2.ctype  d[|p], [a, b, c] {, e} {, f};  // explicit sampler\n\n// mipmaps\ntex.base.geom.v4.dtype.ctype   d[|p], [a, {b,} c] {, e} {, f};\ntex.level.geom.v4.dtype.ctype  d[|p], [a, {b,} c], lod {, e} {, f};\ntex.grad.geom.v4.dtype.ctype   d[|p], [a, {b,} c], dPdx, dPdy {, e} {, f};\n\ntex.base.geom.v2.f16x2.ctype   d[|p], [a, {b,} c] {, e} {, f};\ntex.level.geom.v2.f16x2.ctype  d[|p], [a, {b,} c], lod {, e} {, f};\ntex.grad.geom.v2.f16x2.ctype   d[|p], [a, {b,} c], dPdx, dPdy {, e} {, f};\n\n.geom  = { .1d, .2d, .3d, .a1d, .a2d, .cube, .acube, .2dms, .a2dms };\n.dtype = { .u32, .s32, .f16,  .f32 };\n.ctype = {       .s32, .f32 };          // .cube, .acube require .f32\n                                        // .2dms, .a2dms require .s32"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("tex", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(Set("a1d", "a2d", "a2dms", "acube", "cube"), Set("v4"), Set("f16", "f32", "s32", "u32"), Set("f32", "s32")), Set(), false), InstructionForm("tex", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(Set("a1d", "a2d", "a2dms", "acube", "cube"), Set("v2"), Set("f16x2"), Set("f32", "s32")), Set(), false), InstructionForm("tex", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("level"), Set("a1d", "a2d", "a2dms", "acube", "cube"), Set("v4"), Set("f16", "f32", "s32", "u32"), Set("f32", "s32")), Set(), false), InstructionForm("tex", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("grad"), Set("a1d", "a2d", "a2dms", "acube", "cube"), Set("v4"), Set("f16", "f32", "s32", "u32"), Set("f32", "s32")), Set(), false), InstructionForm("tex", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(Set("base"), Set("a1d", "a2d", "a2dms", "acube", "cube"), Set("v2"), Set("f16x2"), Set("f32", "s32")), Set(), false), InstructionForm("tex", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("level"), Set("a1d", "a2d", "a2dms", "acube", "cube"), Set("v2"), Set("f16x2"), Set("f32", "s32")), Set(), false), InstructionForm("tex", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("grad"), Set("a1d", "a2d", "a2dms", "acube", "cube"), Set("v2"), Set("f16x2"), Set("f32", "s32")), Set(), false))
    ),
    InstructionInfo(
      "texture-instructions-tld4",
      "9.7.11.4. Texture Instructions: tld4",
      "tld4",
      "Texture Instructions",
      Vector("tld4"),
      Vector("tld4.comp.2d.v4.dtype.f32    d[|p], [a, c] {, e} {, f};\ntld4.comp.geom.v4.dtype.f32  d[|p], [a, b, c] {, e} {, f};  // explicit sampler\n\n.comp  = { .r, .g, .b, .a };\n.geom  = { .2d, .a2d, .cube, .acube };\n.dtype = { .u32, .s32, .f32 };"),
      PtxVersion(2, 2),
      SmTarget(20),
      Vector(InstructionForm("tld4", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(Set("a", "b", "g", "r"), Set("v4"), Set("f32", "s32", "u32"), Set("f32")), Set(), false), InstructionForm("tld4", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(Set("a", "b", "g", "r"), Set("a2d", "acube", "cube"), Set("v4"), Set("f32", "s32", "u32"), Set("f32")), Set(), false))
    ),
    InstructionInfo(
      "texture-instructions-txq",
      "9.7.11.5. Texture Instructions: txq",
      "txq",
      "Texture Instructions",
      Vector("txq"),
      Vector("txq.tquery.b32         d, [a];       // texture attributes\ntxq.level.tlquery.b32  d, [a], lod;  // texture attributes\ntxq.squery.b32         d, [a];       // sampler attributes\n\n.tquery  = { .width, .height, .depth,\n             .channel_data_type, .channel_order,\n             .normalized_coords, .array_size,\n             .num_mipmap_levels, .num_samples};\n\n.tlquery = { .width, .height, .depth };\n\n.squery  = { .force_unnormalized_coords, .filter_mode,\n             .addr_mode_0, addr_mode_1, addr_mode_2 };"),
      PtxVersion(1, 5),
      SmTarget(10),
      Vector(InstructionForm("txq", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(Set("array_size", "channel_data_type", "channel_order", "depth", "height", "normalized_coords", "num_mipmap_levels", "num_samples", "width"), Set("b32")), Set(), false))
    ),
    InstructionInfo(
      "texture-instructions-istypep",
      "9.7.11.6. Texture Instructions: istypep",
      "istypep",
      "Texture Instructions",
      Vector("istypep"),
      Vector("istypep.type   p, a;  // result is .pred\n\n.type = { .texref, .samplerref, .surfref };"),
      PtxVersion(4, 0),
      SmTarget(30),
      Vector(InstructionForm("istypep", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("samplerref", "surfref", "texref")), Set(), true))
    ),
    InstructionInfo(
      "surface-instructions-suld",
      "9.7.12.1. Surface Instructions: suld",
      "suld",
      "Surface Instructions",
      Vector("suld"),
      Vector("suld.b.geom{.cop}.vec.dtype.clamp  d, [a, b];  // unformatted\n\n.geom  = { .1d, .2d, .3d, .a1d, .a2d };\n.cop   = { .ca, .cg, .cs, .cv };               // cache operation\n.vec   = { none, .v2, .v4 };\n.dtype = { .b8 , .b16, .b32, .b64 };\n.clamp = { .trap, .clamp, .zero };"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("suld", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(Set("b"), Set("a1d", "a2d"), Set("none", "v2", "v4"), Set("b16", "b32", "b64", "b8"), Set("clamp", "trap", "zero")), Set("ca", "cg", "cs", "cv"), true))
    ),
    InstructionInfo(
      "surface-instructions-sust",
      "9.7.12.2. Surface Instructions: sust",
      "sust",
      "Surface Instructions",
      Vector("sust"),
      Vector("sust.b.{1d,2d,3d}{.cop}.vec.ctype.clamp  [a, b], c;  // unformatted\nsust.p.{1d,2d,3d}.vec.b32.clamp          [a, b], c;  // formatted\n\nsust.b.{a1d,a2d}{.cop}.vec.ctype.clamp   [a, b], c;  // unformatted\n\n.cop   = { .wb, .cg, .cs, .wt };                     // cache operation\n.vec   = { none, .v2, .v4 };\n.ctype = { .b8 , .b16, .b32, .b64 };\n.clamp = { .trap, .clamp, .zero };"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("sust", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("b"), Set("none", "v2", "v4"), Set("b16", "b32", "b64", "b8"), Set("clamp", "trap", "zero")), Set("cg", "cs", "wb", "wt"), false))
    ),
    InstructionInfo(
      "surface-instructions-sured",
      "9.7.12.3. Surface Instructions: sured",
      "sured",
      "Surface Instructions",
      Vector("sured"),
      Vector("sured.b.op.geom.ctype.clamp  [a,b],c; // byte addressing\nsured.p.op.geom.ctype.clamp  [a,b],c; // sample addressing\n\n.op    = { .add, .min, .max, .and, .or };\n.geom  = { .1d, .2d, .3d };\n.ctype = { .u32, .u64, .s32, .b32, .s64 };  // for sured.b\n.ctype = { .b32, .b64 };                    // for sured.p\n.clamp = { .trap, .clamp, .zero };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("sured", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("b"), Set("add", "and", "max", "min", "or"), Set("b32", "b64"), Set("clamp", "trap", "zero")), Set(), false))
    ),
    InstructionInfo(
      "surface-instructions-suq",
      "9.7.12.4. Surface Instructions: suq",
      "suq",
      "Surface Instructions",
      Vector("suq"),
      Vector("suq.query.b32   d, [a];\n\n.query = { .width, .height, .depth,\n           .channel_data_type, .channel_order,\n           .array_size, .memory_layout };"),
      PtxVersion(1, 5),
      SmTarget(10),
      Vector(InstructionForm("suq", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(Set("array_size", "channel_data_type", "channel_order", "depth", "height", "memory_layout", "width"), Set("b32")), Set(), false))
    ),
    InstructionInfo(
      "control-flow-instructions-curly-braces",
      "9.7.13.1. Control Flow Instructions: {}",
      "{}",
      "Control Flow Instructions",
      Vector(),
      Vector("{ instructionList }"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector()
    )
  )

  private def chunk10: Vector[InstructionInfo] = Vector(
    InstructionInfo(
      "control-flow-instructions-at",
      "9.7.13.2. Control Flow Instructions: @",
      "@",
      "Control Flow Instructions",
      Vector("setp"),
      Vector("@{!}p    instruction;"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector()
    ),
    InstructionInfo(
      "control-flow-instructions-bra",
      "9.7.13.3. Control Flow Instructions: bra",
      "bra",
      "Control Flow Instructions",
      Vector("bra"),
      Vector("@p   bra{.uni}  tgt;           // tgt is a label\n     bra{.uni}  tgt;           // unconditional branch"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector()
    ),
    InstructionInfo(
      "control-flow-instructions-brx-idx",
      "9.7.13.4. Control Flow Instructions: brx.idx",
      "brx.idx",
      "Control Flow Instructions",
      Vector("brx"),
      Vector("@p    brx.idx{.uni} index, tlist;\n      brx.idx{.uni} index, tlist;"),
      PtxVersion(6, 0),
      SmTarget(30),
      Vector(InstructionForm("brx", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(Set("idx")), Set("uni"), true))
    ),
    InstructionInfo(
      "control-flow-instructions-call",
      "9.7.13.5. Control Flow Instructions: call",
      "call",
      "Control Flow Instructions",
      Vector("call", "ld", "mov"),
      Vector("// direct call to named function, func is a symbol\ncall{.uni} (ret-param), func, (param-list);\ncall{.uni} func, (param-list);\ncall{.uni} func;\n\n// indirect call via pointer, with full list of call targets\ncall{.uni} (ret-param), fptr, (param-list), flist;\ncall{.uni} fptr, (param-list), flist;\ncall{.uni} fptr, flist;\n\n// indirect call via pointer, with no knowledge of call targets\ncall{.uni} (ret-param), fptr, (param-list), fproto;\ncall{.uni} fptr, (param-list), fproto;\ncall{.uni} fptr, fproto;"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("call", Vector(OperandConstraint.Symbol, OperandConstraint.Unknown("unparsed")), Vector(), Set("uni"), false), InstructionForm("call", Vector(OperandConstraint.Symbol), Vector(), Set("uni"), true), InstructionForm("call", Vector(OperandConstraint.Value, OperandConstraint.Unknown("unparsed"), OperandConstraint.Value), Vector(), Set("uni"), false), InstructionForm("call", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(), Set("uni"), true))
    ),
    InstructionInfo(
      "control-flow-instructions-ret",
      "9.7.13.6. Control Flow Instructions: ret",
      "ret",
      "Control Flow Instructions",
      Vector("ret"),
      Vector("ret{.uni};"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector()
    ),
    InstructionInfo(
      "control-flow-instructions-exit",
      "9.7.13.7. Control Flow Instructions: exit",
      "exit",
      "Control Flow Instructions",
      Vector("exit"),
      Vector("exit;"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector()
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-bar",
      "9.7.14.1. Parallel Synchronization and Communication Instructions: bar, barrier",
      "bar, barrier",
      "Parallel Synchronization and Communication Instructions",
      Vector("barrier", "bar", "st", "ld"),
      Vector("barrier{.cta}.sync{.aligned}      a{, b};\nbarrier{.cta}.arrive{.aligned}    a, b;\n\nbarrier{.cta}.red.popc{.aligned}.u32  d, a{, b}, {!}c;\nbarrier{.cta}.red.op{.aligned}.pred   p, a{, b}, {!}c;\n\nbar{.cta}.sync      a{, b};\nbar{.cta}.arrive    a, b;\n\nbar{.cta}.red.popc.u32  d, a{, b}, {!}c;\nbar{.cta}.red.op.pred   p, a{, b}, {!}c;\n\n.op = { .and, .or };"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("barrier", Vector(OperandConstraint.Value), Vector(Set("sync")), Set("aligned", "cta"), false), InstructionForm("barrier", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(Set("arrive")), Set("aligned", "cta"), false), InstructionForm("barrier", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Vector), Vector(Set("red"), Set("popc"), Set("u32")), Set("aligned", "cta"), false), InstructionForm("barrier", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Vector), Vector(Set("red"), Set("and", "or"), Set("pred")), Set("aligned", "cta"), false), InstructionForm("bar", Vector(OperandConstraint.Value), Vector(Set("sync")), Set("cta"), false), InstructionForm("bar", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(Set("arrive")), Set("cta"), false), InstructionForm("bar", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Vector), Vector(Set("red"), Set("popc"), Set("u32")), Set("cta"), false), InstructionForm("bar", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Vector), Vector(Set("red"), Set("and", "or"), Set("pred")), Set("cta"), false))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-bar-warp-sync",
      "9.7.14.2. Parallel Synchronization and Communication Instructions: bar.warp.sync",
      "bar.warp.sync",
      "Parallel Synchronization and Communication Instructions",
      Vector("bar", "st"),
      Vector("bar.warp.sync      membermask;"),
      PtxVersion(6, 0),
      SmTarget(30),
      Vector(InstructionForm("bar", Vector(OperandConstraint.Value), Vector(Set("warp"), Set("sync")), Set(), true))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-barrier-cluster",
      "9.7.14.3. Parallel Synchronization and Communication Instructions: barrier.cluster",
      "barrier.cluster",
      "Parallel Synchronization and Communication Instructions",
      Vector("barrier", "ld", "st", "fence"),
      Vector("barrier.cluster.arrive{.sem}{.aligned};\nbarrier.cluster.wait{.acquire}{.aligned};\n\n.sem = {.release, .relaxed}"),
      PtxVersion(7, 8),
      SmTarget(90),
      Vector()
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-membar",
      "9.7.14.4. Parallel Synchronization and Communication Instructions: membar / fence",
      "membar / fence",
      "Parallel Synchronization and Communication Instructions",
      Vector("fence", "membar", "tensormap", "cvta", "cp", "barrier", "mbarrier"),
      Vector("// Thread fence:\nfence{.sem}.scope;\n\n// Thread fence:\nfence.acquire.sync_restrict::shared::cluster.cluster;\nfence.release.sync_restrict::shared::cta.cluster;\n\n// Operation fence:\nfence.op_restrict.release.cluster;\n\n// Proxy fence (bi-directional):\nfence.proxy.proxykind;\n\n// Proxy fence (uni-directional):\nfence.proxy.to_proxykind::from_proxykind.release.scope;\nfence.proxy.to_proxykind::from_proxykind.acquire.scope  [addr], size;\nfence.proxy.async::generic.acquire.sync_restrict::shared::cluster.cluster;\nfence.proxy.async::generic.release.sync_restrict::shared::cta.cluster;\nfence.proxy.to_proxykind::from_proxykind_fabric.alias.sem_fabric.sys;\n\n// Old style membar:\nmembar.level;\nmembar.proxy.proxykind;\n\n.sem       = { .sc, .acq_rel, .acquire, .release };\n.scope     = { .cta, .cluster, .gpu, .sys };\n.level     = { .cta, .gl, .sys };\n.proxykind = { .alias, .async, .async.global, .async.shared::{cta, cluster} };\n.op_restrict = { .mbarrier_init };\n.to_proxykind::from_proxykind = {.tensormap::generic};\n.to_proxykind::from_proxykind_fabric = {.generic::fabric, .fabric::generic, .fabric::fabric };\n.sem_fabric = { .acquire, .release };"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("fence", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("proxy"), Set("to_proxykind::from_proxykind"), Set("acquire"), Set("cluster", "cta", "gpu", "sys")), Set(), false))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-atom",
      "9.7.14.5. Parallel Synchronization and Communication Instructions: atom",
      "atom",
      "Parallel Synchronization and Communication Instructions",
      Vector("atom", "createpolicy"),
      Vector("atom{.sem}{.scope}{.space}.op{.level::cache_hint}.type d, [a], b{, cache_policy};\n\natom{.sem}{.scope}{.space}.cas.type1 d, [a], b, c;\n\natom{.sem}{.scope}{.space}.exch{.level::cache_hint}.type2 d, [a], b {, cache_policy};\n\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16     d, [a], b{, cache_policy};\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16x2   d, [a], b{, cache_policy};\n\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16    d, [a], b{, cache_policy};\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16x2  d, [a], b{, cache_policy};\n\n.space =              { .global, .shared{::cta, ::cluster} };\n.sem =                { .relaxed, .acquire, .release, .acq_rel };\n.scope =              { .cta, .cluster, .gpu, .sys };\n\n.op =                 { .and, .or, .xor,\n                        .add, .inc, .dec,\n                        .min, .max };\n.level::cache_hint =  { .L2::cache_hint };\n.type =               { .b32, .b64, .u32, .u64, .s32, .s64, .f32, .f64 };\n.type1 =              { .b16, .b32, .b64, .b128 };\n.type2 =              { .b32, .b64, .b128 };", "atom{.sem}{.scope}{.global}.add{.level::cache_hint}.vec_32_bit.f32                  d, [a], b{, cache_policy};\natom{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}.vec_16_bit.half_word_type  d, [a], b{, cache_policy};\natom{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}.vec_32_bit.packed_type     d, [a], b{, cache_policy};\n\n.sem =               { .relaxed, .acquire, .release, .acq_rel };\n.scope =             { .cta, .cluster, .gpu, .sys };\n.op =                { .add, .min, .max };\n.half_word_type =    { .f16, .bf16 };\n.packed_type =       { .f16x2, .bf16x2 };\n.vec_16_bit =        { .v2, .v4, .v8 }\n.vec_32_bit =        { .v2, .v4 };\n.level::cache_hint = { .L2::cache_hint }"),
      PtxVersion(1, 0),
      SmTarget(11),
      Vector(InstructionForm("atom", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("add", "and", "dec", "inc", "max", "min", "or", "xor"), Set("b32", "b64", "f32", "f64", "s32", "s64", "u32", "u64")), Set("acq_rel", "acquire", "cluster", "cta", "gpu", "level::cache_hint", "relaxed", "release", "space", "sys"), false), InstructionForm("atom", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("cas"), Set("b128", "b16", "b32", "b64")), Set("acq_rel", "acquire", "cluster", "cta", "gpu", "relaxed", "release", "space", "sys"), false), InstructionForm("atom", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("exch"), Set("b128", "b32", "b64")), Set("acq_rel", "acquire", "cluster", "cta", "gpu", "level::cache_hint", "relaxed", "release", "space", "sys"), false), InstructionForm("atom", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("add"), Set("noftz"), Set("f16")), Set("acq_rel", "acquire", "cluster", "cta", "gpu", "level::cache_hint", "relaxed", "release", "space", "sys"), false), InstructionForm("atom", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("add"), Set("noftz"), Set("f16x2")), Set("acq_rel", "acquire", "cluster", "cta", "gpu", "level::cache_hint", "relaxed", "release", "space", "sys"), false), InstructionForm("atom", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("add"), Set("noftz"), Set("bf16")), Set("acq_rel", "acquire", "cluster", "cta", "gpu", "level::cache_hint", "relaxed", "release", "space", "sys"), false), InstructionForm("atom", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("add"), Set("noftz"), Set("bf16x2")), Set("acq_rel", "acquire", "cluster", "cta", "gpu", "level::cache_hint", "relaxed", "release", "space", "sys"), false), InstructionForm("atom", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("add"), Set("v2", "v4"), Set("f32")), Set("acq_rel", "acquire", "cluster", "cta", "global", "gpu", "level::cache_hint", "relaxed", "release", "sys"), false), InstructionForm("atom", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("add", "max", "min"), Set("noftz"), Set("vec_16_bit"), Set("bf16", "f16")), Set("acq_rel", "acquire", "cluster", "cta", "global", "gpu", "level::cache_hint", "relaxed", "release", "sys"), false), InstructionForm("atom", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("add", "max", "min"), Set("noftz"), Set("v2", "v4"), Set("bf16x2", "f16x2")), Set("acq_rel", "acquire", "cluster", "cta", "global", "gpu", "level::cache_hint", "relaxed", "release", "sys"), false))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-red",
      "9.7.14.6. Parallel Synchronization and Communication Instructions: red",
      "red",
      "Parallel Synchronization and Communication Instructions",
      Vector("red", "createpolicy"),
      Vector("red{.sem}{.scope}{.space}.op{.level::cache_hint}.type          [a], b{, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16    [a], b{, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16x2  [a], b{, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16\n                                                      [a], b {, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16x2\n                                                      [a], b {, cache_policy};\n\n.space =              { .global, .shared{::cta, ::cluster} };\n.sem =                { .relaxed, .release };\n.scope =              { .cta, .cluster, .gpu, .sys };\n\n.op =                 { .and, .or, .xor,\n                        .add, .inc, .dec,\n                        .min, .max };\n.level::cache_hint =  { .L2::cache_hint };\n.type =               { .b32, .b64, .u32, .u64, .s32, .s64, .f32, .f64 };", "red{.sem}{.scope}{.global}.add{.level::cache_hint}.vec_32_bit.f32 [a], b{, cache_policy};\nred{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}. vec_16_bit.half_word_type [a], b{, cache_policy};\nred{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}.vec_32_bit.packed_type [a], b {, cache_policy};\n\n.sem =                { .relaxed, .release };\n.scope =              { .cta, .cluster, .gpu, .sys };\n.op =                 { .add, .min, .max };\n.half_word_type =     { .f16, .bf16 };\n.packed_type =        { .f16x2,.bf16x2 };\n.vec_16_bit =         { .v2, .v4, .v8 }\n.vec_32_bit =         { .v2, .v4 };\n.level::cache_hint =  { .L2::cache_hint }"),
      PtxVersion(1, 2),
      SmTarget(11),
      Vector(InstructionForm("red", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("add", "and", "dec", "inc", "max", "min", "or", "xor"), Set("b32", "b64", "f32", "f64", "s32", "s64", "u32", "u64")), Set("cluster", "cta", "gpu", "level::cache_hint", "relaxed", "release", "space", "sys"), false), InstructionForm("red", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("add"), Set("noftz"), Set("f16")), Set("cluster", "cta", "gpu", "level::cache_hint", "relaxed", "release", "space", "sys"), false), InstructionForm("red", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("add"), Set("noftz"), Set("f16x2")), Set("cluster", "cta", "gpu", "level::cache_hint", "relaxed", "release", "space", "sys"), false), InstructionForm("red", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("add"), Set("noftz"), Set("bf16")), Set("cluster", "cta", "gpu", "level::cache_hint", "relaxed", "release", "space", "sys"), false), InstructionForm("red", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("add"), Set("noftz"), Set("bf16x2")), Set("cluster", "cta", "gpu", "level::cache_hint", "relaxed", "release", "space", "sys"), false), InstructionForm("red", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("add"), Set("v2", "v4"), Set("f32")), Set("cluster", "cta", "global", "gpu", "level::cache_hint", "relaxed", "release", "sys"), false), InstructionForm("red", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(Set("add", "max", "min"), Set("noftz")), Set("cluster", "cta", "global", "gpu", "level::cache_hint", "relaxed", "release", "sys"), false), InstructionForm("red", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("add", "max", "min"), Set("noftz"), Set("v2", "v4"), Set("bf16x2", "f16x2")), Set("cluster", "cta", "global", "gpu", "level::cache_hint", "relaxed", "release", "sys"), false))
    )
  )

  private def chunk11: Vector[InstructionInfo] = Vector(
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-red-async",
      "9.7.14.7. Parallel Synchronization and Communication Instructions: red.async",
      "red.async",
      "Parallel Synchronization and Communication Instructions",
      Vector("red"),
      Vector("// Increment and Decrement reductions\nred.async.sem.scope{.ss}.completion_mechanism.op.type [a], b, [mbar];\n\n.sem  =                 { .relaxed };\n.scope =                { .cluster };\n.ss   =                 { .shared::cluster };\n.op   =                 { .inc, .dec };\n.type =                 { .u32 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\n\n// MIN and MAX reductions\nred.async.sem.scope{.ss}.completion_mechanism.op.type [a], b, [mbar];\n\n.sem  = { .relaxed };\n.scope = { .cluster };\n.ss   = { .shared::cluster };\n.op   = { .min, .max };\n.type = { .u32, .s32 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\n// Bitwise AND, OR and XOR reductions\nred.async.sem.scope{.ss}.completion_mechanism.op.type [a], b, [mbar];\n\n.sem  = { .relaxed };\n.scope = { .cluster };\n.ss   = { .shared::cluster };\n.op   = { .and, .or, .xor };\n.type = { .b32 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\n// ADD reductions\nred.async.sem.scope{.ss}.completion_mechanism.add.type [a], b, [mbar];\n\n.sem  = { .relaxed };\n.scope = { .cluster };\n.ss   = { .shared::cluster };\n.type = { .u32, .s32, .u64 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\nred.async{.mmio}.sem.scope{.ss}.add.type [a], b;\n\n.sem  = { .release };\n.scope = { .gpu, .sys };\n.ss   = { .global };\n.type = { .u32, .s32, .u64, .s64 };"),
      PtxVersion(8, 1),
      SmTarget(90),
      Vector()
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-multimem-red-async",
      "9.7.14.8. Parallel Synchronization and Communication Instructions: multimem.red.async",
      "multimem.red.async",
      "Parallel Synchronization and Communication Instructions",
      Vector("multimem"),
      Vector("multimem.red.async.sem.scope{.ss}.op.type [a], b;\n\n.sem   = { .release };\n.scope = { .gpu, .sys };\n.ss    = { .global };\n.op    = { .add };\n.type  = { .u32, .s32, .u64 };"),
      PtxVersion(9, 3),
      SmTarget(100),
      Vector(InstructionForm("multimem", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("red"), Set("async"), Set("release"), Set("gpu", "sys"), Set("add"), Set("s32", "u32", "u64")), Set("global"), true))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-vote",
      "9.7.14.9. Parallel Synchronization and Communication Instructions: vote (deprecated)",
      "vote (deprecated)",
      "Parallel Synchronization and Communication Instructions",
      Vector("vote"),
      Vector("vote.mode.pred  d, {!}a;\nvote.ballot.b32 d, {!}a;  // 'ballot' form, returns bitmask\n\n.mode = { .all, .any, .uni };"),
      PtxVersion(1, 2),
      SmTarget(12),
      Vector(InstructionForm("vote", Vector(OperandConstraint.Register, OperandConstraint.Vector), Vector(Set("all", "any", "uni"), Set("pred")), Set(), false), InstructionForm("vote", Vector(OperandConstraint.Register, OperandConstraint.Vector), Vector(Set("ballot"), Set("b32")), Set(), false))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-vote-sync",
      "9.7.14.10. Parallel Synchronization and Communication Instructions: vote.sync",
      "vote.sync",
      "Parallel Synchronization and Communication Instructions",
      Vector("vote"),
      Vector("vote.sync.mode.pred  d, {!}a, membermask;\nvote.sync.ballot.b32 d, {!}a, membermask;  // 'ballot' form, returns bitmask\n\n.mode = { .all, .any, .uni };"),
      PtxVersion(6, 0),
      SmTarget(30),
      Vector(InstructionForm("vote", Vector(OperandConstraint.Register, OperandConstraint.Vector, OperandConstraint.Value), Vector(Set("sync"), Set("all", "any", "uni"), Set("pred")), Set(), true), InstructionForm("vote", Vector(OperandConstraint.Register, OperandConstraint.Vector, OperandConstraint.Value), Vector(Set("sync"), Set("ballot"), Set("b32")), Set(), true))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-match-sync",
      "9.7.14.11. Parallel Synchronization and Communication Instructions: match.sync",
      "match.sync",
      "Parallel Synchronization and Communication Instructions",
      Vector("match"),
      Vector("match.any.sync.type  d, a, membermask;\nmatch.all.sync.type  d[|p], a, membermask;\n\n.type = { .b32, .b64 };"),
      PtxVersion(6, 0),
      SmTarget(70),
      Vector(InstructionForm("match", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("any"), Set("sync"), Set("b32", "b64")), Set(), true), InstructionForm("match", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("all"), Set("sync"), Set("b32", "b64")), Set(), true))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-activemask",
      "9.7.14.12. Parallel Synchronization and Communication Instructions: activemask",
      "activemask",
      "Parallel Synchronization and Communication Instructions",
      Vector("activemask"),
      Vector("activemask.b32 d;"),
      PtxVersion(6, 2),
      SmTarget(30),
      Vector(InstructionForm("activemask", Vector(OperandConstraint.Register), Vector(Set("b32")), Set(), true))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-redux-sync",
      "9.7.14.13. Parallel Synchronization and Communication Instructions: redux.sync",
      "redux.sync",
      "Parallel Synchronization and Communication Instructions",
      Vector("redux"),
      Vector("redux.sync.op.type dst, src, membermask;\n.op   = {.add, .min, .max}\n.type = {.u32, .s32}\n\nredux.sync.op.b32 dst, src, membermask;\n.op   = {.and, .or, .xor}\n\nredux.sync.op{.abs.}{.NaN}.f32 dst, src, membermask;\n.op   = { .min, .max }"),
      PtxVersion(7, 0),
      SmTarget(80),
      Vector(InstructionForm("redux", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("sync"), Set("op"), Set("type")), Set(), false))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-griddepcontrol",
      "9.7.14.14. Parallel Synchronization and Communication Instructions: griddepcontrol",
      "griddepcontrol",
      "Parallel Synchronization and Communication Instructions",
      Vector("griddepcontrol"),
      Vector("griddepcontrol.action;\n\n.action   = { .launch_dependents, .wait }"),
      PtxVersion(7, 8),
      SmTarget(90),
      Vector()
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-elect-sync",
      "9.7.14.15. Parallel Synchronization and Communication Instructions: elect.sync",
      "elect.sync",
      "Parallel Synchronization and Communication Instructions",
      Vector("elect"),
      Vector("elect.sync d|p, membermask;"),
      PtxVersion(8, 0),
      SmTarget(90),
      Vector(InstructionForm("elect", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(Set("sync")), Set(), true))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-mbarrier-init",
      "9.7.14.16.12. Parallel Synchronization and Communication Instructions: mbarrier.init",
      "mbarrier.init",
      "Parallel Synchronization and Communication Instructions",
      Vector("mbarrier", "cvta", "bar"),
      Vector("mbarrier.init{.layout}{.shared{::cta}}.b64 [addr], count;\n\n.layout = { .layout::v0, .layout::v1 }"),
      PtxVersion(7, 0),
      SmTarget(80),
      Vector(InstructionForm("mbarrier", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("init"), Set("b64")), Set("layout", "shared"), false))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-mbarrier-inval",
      "9.7.14.16.13. Parallel Synchronization and Communication Instructions: mbarrier.inval",
      "mbarrier.inval",
      "Parallel Synchronization and Communication Instructions",
      Vector("mbarrier", "bar"),
      Vector("mbarrier.inval{.shared{::cta}}.b64 [addr];"),
      PtxVersion(7, 0),
      SmTarget(80),
      Vector(InstructionForm("mbarrier", Vector(OperandConstraint.Address), Vector(Set("inval"), Set("b64")), Set("shared"), false))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-mbarrier-expect-tx",
      "9.7.14.16.14. Parallel Synchronization and Communication Instructions: mbarrier.expect_tx",
      "mbarrier.expect_tx",
      "Parallel Synchronization and Communication Instructions",
      Vector("mbarrier"),
      Vector("mbarrier.expect_tx{.sem.scope}{.space}.b64 [addr], txCount;\n\n.sem   = { .relaxed }\n.scope = { .cta, .cluster }\n.space = { .shared{::cta}, .shared::cluster }"),
      PtxVersion(8, 0),
      SmTarget(90),
      Vector(InstructionForm("mbarrier", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("expect_tx"), Set("b64")), Set("scope", "sem", "space"), true))
    )
  )

  private def chunk12: Vector[InstructionInfo] = Vector(
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-mbarrier-complete-tx",
      "9.7.14.16.15. Parallel Synchronization and Communication Instructions: mbarrier.complete_tx",
      "mbarrier.complete_tx",
      "Parallel Synchronization and Communication Instructions",
      Vector("mbarrier"),
      Vector("mbarrier.complete_tx{.sem.scope}{.space}.b64 [addr], txCount;\n\n.sem   = { .relaxed }\n.scope = { .cta, .cluster }\n.space = { .shared{::cta}, .shared::cluster }"),
      PtxVersion(8, 0),
      SmTarget(90),
      Vector(InstructionForm("mbarrier", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("complete_tx"), Set("b64")), Set("scope", "sem", "space"), true))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-mbarrier-arrive",
      "9.7.14.16.16. Parallel Synchronization and Communication Instructions: mbarrier.arrive",
      "mbarrier.arrive",
      "Parallel Synchronization and Communication Instructions",
      Vector("mbarrier", "cvta", "mov", "mapa"),
      Vector("mbarrier.arrive{.sem.scope}{.shared{::cta}}.b64           state, [addr]{, count};\nmbarrier.arrive{.sem.scope}{.shared::cluster}.b64         _, [addr] {,count}\nmbarrier.arrive.expect_tx{.sem.scope}{.shared{::cta}}.b64 state, [addr], txCount;\nmbarrier.arrive.expect_tx{.sem.scope}{.shared::cluster}.b64   _, [addr], txCount;\nmbarrier.arrive.noComplete{.release.cta}{.shared{::cta}}.b64  state, [addr], count;\n\n.sem   = { .release, .relaxed }\n.scope = { .cta, .cluster }"),
      PtxVersion(7, 0),
      SmTarget(80),
      Vector(InstructionForm("mbarrier", Vector(OperandConstraint.Value, OperandConstraint.Address), Vector(Set("arrive"), Set("b64")), Set("scope", "sem", "shared"), false), InstructionForm("mbarrier", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("arrive"), Set("b64")), Set("scope", "sem", "shared::cluster"), false), InstructionForm("mbarrier", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("arrive"), Set("expect_tx"), Set("b64")), Set("scope", "sem", "shared::cluster"), false), InstructionForm("mbarrier", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("arrive"), Set("noComplete"), Set("b64")), Set("cta", "release", "shared"), false))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-mbarrier-arrive-drop",
      "9.7.14.16.17. Parallel Synchronization and Communication Instructions: mbarrier.arrive_drop",
      "mbarrier.arrive_drop",
      "Parallel Synchronization and Communication Instructions",
      Vector("mbarrier", "exit"),
      Vector("mbarrier.arrive_drop{.sem.scope}{.shared{::cta}}.b64              state, [addr] {, count};\nmbarrier.arrive_drop{.sem.scope}{.shared::cluster}.b64            _,     [addr] {, count};\nmbarrier.arrive_drop.expect_tx{.sem.scope}{.shared{::cta}}.b64    state, [addr], tx_count;\nmbarrier.arrive_drop.expect_tx{.sem.scope}{.shared::cluster}.b64  _,     [addr], tx_count;\nmbarrier.arrive_drop.noComplete{.release.cta}{.shared{::cta}}.b64 state, [addr], count;\n\n.sem   = { .release, .relaxed }\n.scope = { .cta, .cluster }"),
      PtxVersion(7, 0),
      SmTarget(80),
      Vector(InstructionForm("mbarrier", Vector(OperandConstraint.Value, OperandConstraint.Address), Vector(Set("arrive_drop"), Set("b64")), Set("scope", "sem", "shared"), false), InstructionForm("mbarrier", Vector(OperandConstraint.Value, OperandConstraint.Address), Vector(Set("arrive_drop"), Set("b64")), Set("scope", "sem", "shared::cluster"), false), InstructionForm("mbarrier", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("arrive_drop"), Set("expect_tx"), Set("b64")), Set("scope", "sem", "shared"), false), InstructionForm("mbarrier", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("arrive_drop"), Set("expect_tx"), Set("b64")), Set("scope", "sem", "shared::cluster"), false), InstructionForm("mbarrier", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("arrive_drop"), Set("noComplete"), Set("b64")), Set("cta", "release", "shared"), false))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-cp-async-mbarrier-arrive",
      "9.7.14.16.18. Parallel Synchronization and Communication Instructions: cp.async.mbarrier.arrive",
      "cp.async.mbarrier.arrive",
      "Parallel Synchronization and Communication Instructions",
      Vector("cp", "mbarrier", "bra", "mov", "mul", "add"),
      Vector("cp.async.mbarrier.arrive{.noinc}{.shared{::cta}}.b64 [addr];"),
      PtxVersion(7, 0),
      SmTarget(80),
      Vector(InstructionForm("cp", Vector(OperandConstraint.Address), Vector(Set("async"), Set("mbarrier"), Set("arrive"), Set("b64")), Set("noinc", "shared"), false))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-mbarrier-test-wait-try-wait",
      "9.7.14.16.19. Parallel Synchronization and Communication Instructions: mbarrier.test_wait / mbarrier.try_wait",
      "mbarrier.test_wait / mbarrier.try_wait",
      "Parallel Synchronization and Communication Instructions",
      Vector("mbarrier", "nanosleep", "bra", "mov", "add", "setp", "cp", "cvta", "mapa", "barrier", "exit"),
      Vector("// without parity\nmbarrier.test_wait{.phase_type::primary}{.sem.scope}{.ss}.b64      waitComplete, [addr], state;\n\nmbarrier.test_wait.phase_type::primary{.sem}{.scope}{.ss}.b64      waitComplete|reportPredicate\n                                                                   {, reportValue}, [addr], state;\n\n// with parity\nmbarrier.test_wait.parity{.phase_type}{.sem.scope}{.ss}.b64        waitComplete, [addr], phaseParity;\n\nmbarrier.test_wait.parity.phase_type::primary{.sem}{.scope}{.ss}.b64  waitComplete|reportPredicate\n                                                                      {, reportValue}, [addr], phaseParity;\n\n// without parity\nmbarrier.try_wait{.phase_type::primary}{.sem.scope}{.ss}.b64      waitComplete, [addr], state {, timeHint};\n\nmbarrier.try_wait.phase_type::primary{.sem}{.scope}{.ss}.b64      waitComplete|reportPredicate {, reportValue},\n                                                                  [addr], state {, timeHint};\n\n// with parity\nmbarrier.try_wait.parity{.phase_type}{.sem.scope}{.ss}.b64            waitComplete, [addr], phaseParity {, timeHint};\n\nmbarrier.try_wait.parity.phase_type::primary{.sem}{.scope}{.ss}.b64   waitComplete|reportPredicate {, reportValue},\n                                                                      [addr], phaseParity {, timeHint};\n\n\n.ss   = { .shared{::cta} }\n.sem   = { .acquire, .relaxed }\n.scope = { .cta, .cluster }\n.phase_type = { .phase_type::primary, .phase_type::conditional }"),
      PtxVersion(1, 0),
      SmTarget(80),
      Vector(InstructionForm("mbarrier", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("test_wait"), Set("phase_type::primary"), Set("b64")), Set("scope", "sem", "ss"), false), InstructionForm("mbarrier", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("test_wait"), Set("parity"), Set("phase_type::primary"), Set("b64")), Set("scope", "sem", "ss"), false), InstructionForm("mbarrier", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("try_wait"), Set("phase_type::primary"), Set("b64")), Set("scope", "sem", "ss"), false), InstructionForm("mbarrier", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("try_wait"), Set("parity"), Set("phase_type::primary"), Set("b64")), Set("scope", "sem", "ss"), false))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-mbarrier-pending-count",
      "9.7.14.16.20. Parallel Synchronization and Communication Instructions: mbarrier.pending_count",
      "mbarrier.pending_count",
      "Parallel Synchronization and Communication Instructions",
      Vector("mbarrier"),
      Vector("mbarrier.pending_count{.layout}.b64 count, state;\n\n.layout = { .layout::v0 }"),
      PtxVersion(7, 0),
      SmTarget(80),
      Vector(InstructionForm("mbarrier", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(Set("pending_count"), Set("b64")), Set("layout"), false))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-mbarrier-check-layout",
      "9.7.14.16.21. Parallel Synchronization and Communication Instructions: mbarrier.check_layout",
      "mbarrier.check_layout",
      "Parallel Synchronization and Communication Instructions",
      Vector("mbarrier", "bra"),
      Vector("mbarrier.check_layout.layout{.ss}.b64 p, [addr];\n\n.layout = { .layout::v0, .layout::v1 }\n.ss = { .shared::cta }"),
      PtxVersion(9, 3),
      SmTarget(90),
      Vector(InstructionForm("mbarrier", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(Set("check_layout"), Set("layout"), Set("b64")), Set("ss"), true))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-tensormap-cp-fenceproxy",
      "9.7.14.17. Parallel Synchronization and Communication Instructions: tensormap.cp_fenceproxy",
      "tensormap.cp_fenceproxy",
      "Parallel Synchronization and Communication Instructions",
      Vector("tensormap", "cp", "mbarrier", "bra", "fence"),
      Vector("tensormap.cp_fenceproxy.cp_qualifiers.fence_qualifiers.sync.aligned  [dst], [src], size;\n\n.cp_qualifiers    = { .global.shared::cta }\n.fence_qualifiers = { .to_proxy::from_proxy.release.scope }\n.to_proxy::from_proxy  = { .tensormap::generic }\n.scope            = { .cta, .cluster, .gpu , .sys }"),
      PtxVersion(8, 3),
      SmTarget(90),
      Vector(InstructionForm("tensormap", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("cp_fenceproxy"), Set("cp_qualifiers"), Set("fence_qualifiers"), Set("sync"), Set("aligned")), Set(), true))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-clusterlaunchcontrol-try-cancel",
      "9.7.14.18. Parallel Synchronization and Communication Instructions: clusterlaunchcontrol.try_cancel",
      "clusterlaunchcontrol.try_cancel",
      "Parallel Synchronization and Communication Instructions",
      Vector("clusterlaunchcontrol", "mov", "barrier", "setp", "bra", "mbarrier", "ld", "ret", "fence"),
      Vector("clusterlaunchcontrol.try_cancel.async{.space}.completion_mechanism{.multicast::cluster::all}.b128 [addr], [mbar];\n\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n.space = { .shared::cta };"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("clusterlaunchcontrol", Vector(OperandConstraint.Address, OperandConstraint.Address), Vector(Set("try_cancel"), Set("async"), Set("mbarrier::complete_tx::bytes"), Set("b128")), Set("multicast::cluster::all", "shared::cta"), false))
    ),
    InstructionInfo(
      "parallel-synchronization-and-communication-instructions-clusterlaunchcontrol-query-cancel",
      "9.7.14.19. Parallel Synchronization and Communication Instructions: clusterlaunchcontrol.query_cancel",
      "clusterlaunchcontrol.query_cancel",
      "Parallel Synchronization and Communication Instructions",
      Vector("clusterlaunchcontrol"),
      Vector("clusterlaunchcontrol.query_cancel.is_canceled.pred.b128 pred, try_cancel_response;\n\nclusterlaunchcontrol.query_cancel.get_first_ctaid.v4.b32.b128 {xdim, ydim, zdim, _},  try_cancel_response;\n\nclusterlaunchcontrol.query_cancel.get_first_ctaid{::dimension}.b32.b128 reg, try_cancel_response;\n\n::dimension = { ::x, ::y, ::z };"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("clusterlaunchcontrol", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(Set("query_cancel"), Set("is_canceled"), Set("pred"), Set("b128")), Set(), true), InstructionForm("clusterlaunchcontrol", Vector(OperandConstraint.Vector, OperandConstraint.Value), Vector(Set("query_cancel"), Set("get_first_ctaid"), Set("v4"), Set("b32"), Set("b128")), Set(), true), InstructionForm("clusterlaunchcontrol", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(Set("query_cancel"), Set("get_first_ctaid"), Set("b32"), Set("b128")), Set(), false))
    ),
    InstructionInfo(
      "tcgen05-instructions-tcgen05-alloc-dealloc-relinquish-alloc-permit",
      "9.7.17.7.1. Tensorcore 5th Generation Instructions: tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit",
      "tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit",
      "Tensorcore 5th Generation Instructions",
      Vector("tcgen05", "ld"),
      Vector("tcgen05.alloc.cta_group.sync.aligned{.shared::cta}.b32  [dst], nCols;\n\ntcgen05.dealloc.cta_group.sync.aligned.b32              taddr, nCols;\n\ntcgen05.relinquish_alloc_permit.cta_group.sync.aligned;\n\n.cta_group = { .cta_group::1, .cta_group::2 }"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("tcgen05", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("alloc"), Set("cta_group"), Set("sync"), Set("aligned"), Set("b32")), Set("shared::cta"), false), InstructionForm("tcgen05", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(Set("dealloc"), Set("cta_group"), Set("sync"), Set("aligned"), Set("b32")), Set(), false))
    ),
    InstructionInfo(
      "tcgen05-instructions-tcgen05-ld",
      "9.7.17.8.3. Tensorcore 5th Generation Instructions: tcgen05.ld",
      "tcgen05.ld",
      "Tensorcore 5th Generation Instructions",
      Vector("tcgen05"),
      Vector("// Base load instruction:\n\ntcgen05.ld.sync.aligned.shape1.num{.pack}.b32    r, [taddr];\n\ntcgen05.ld.sync.aligned.shape2.num{.pack}.b32    r, [taddr], immHalfSplitoff;\n\n.shape1 = { .16x64b, .16x128b, .16x256b, .32x32b }\n.shape2 = { .16x32bx2 }\n.num    = { .x1, .x2, .x4, .x8, .x16, .x32, .x64, .x128 }\n.pack   = { .pack::16b }\n\n// Floating point type load along with reduction :\n\ntcgen05.ld.red.sync.aligned.shape3.num.redOp{.abs}{.NaN}.f32 r, redval, [taddr];\n\ntcgen05.ld.red.sync.aligned.shape4.num.redOp{.abs}{.NaN}.f32 r, redval, [taddr], immHalfSplitoff;\n\n// Integer type load along with reduction :\n\ntcgen05.ld.red.sync.aligned.shape3.num.redOp.type r, redval, [taddr];\n\ntcgen05.ld.red.sync.aligned.shape4.num.redOp.type r, redval, [taddr], immHalfSplitoff;\n\n.shape3 = { .32x32b   }\n.shape4 = { .16x32bx2 }\n.redOp  = { .min, .max }\n.type   = { .u32, .s32 }"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("tcgen05", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(Set("ld"), Set("sync"), Set("aligned"), Set("shape2"), Set("num"), Set("b32")), Set("pack"), false))
    )
  )

  private def chunk13: Vector[InstructionInfo] = Vector(
    InstructionInfo(
      "tcgen05-instructions-tcgen05-st",
      "9.7.17.8.4. Tensorcore 5th Generation Instructions: tcgen05.st",
      "tcgen05.st",
      "Tensorcore 5th Generation Instructions",
      Vector("tcgen05"),
      Vector("tcgen05.st.sync.aligned.shape1.num{.unpack}.b32    [taddr], r;\n\ntcgen05.st.sync.aligned.shape2.num{.unpack}.b32    [taddr], immHalfSplitoff, r;\n\n.shape1 = { .16x64b, .16x128b, .16x256b, .32x32b }\n.shape2 = { .16x32bx2 }\n.num    = { .x1, .x2, .x4, .x8, .x16, .x32, .x64, .x128 }\n.unpack = { .unpack::16b }"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("tcgen05", Vector(OperandConstraint.Address, OperandConstraint.Register), Vector(Set("st"), Set("sync"), Set("aligned"), Set("shape1"), Set("num"), Set("b32")), Set("unpack"), false), InstructionForm("tcgen05", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Register), Vector(Set("st"), Set("sync"), Set("aligned"), Set("shape2"), Set("num"), Set("b32")), Set("unpack"), false))
    ),
    InstructionInfo(
      "tcgen05-instructions-tcgen05-wait",
      "9.7.17.8.5. Tensorcore 5th Generation Instructions: tcgen05.wait",
      "tcgen05.wait",
      "Tensorcore 5th Generation Instructions",
      Vector("tcgen05"),
      Vector("tcgen05.wait_operation.sync.aligned;\n\n.wait_operation = { .wait::ld, .wait::st }"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector()
    ),
    InstructionInfo(
      "tcgen05-instructions-tcgen05-cp",
      "9.7.17.9.2. Tensorcore 5th Generation Instructions: tcgen05.cp",
      "tcgen05.cp",
      "Tensorcore 5th Generation Instructions",
      Vector("tcgen05"),
      Vector("tcgen05.cp.cta_group.shape{.multicast}{.dst_fmt.src_fmt} [taddr], s-desc;\n\n.cta_group = { .cta_group::1, .cta_group::2 }\n.src_fmt   = { .b6x16_p32 , .b4x16_p64 }\n.dst_fmt   = { .b8x16 }\n.shape     = { .128x256b, .4x256b, .128x128b, .64x128b**, .32x128b*** }\n.multicast = { .warpx2::02_13** , .warpx2::01_23**, .warpx4*** }"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("tcgen05", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(Set("cp"), Set("cta_group"), Set("shape")), Set("dst_fmt", "multicast", "src_fmt"), false))
    ),
    InstructionInfo(
      "tcgen05-instructions-tcgen05-shift",
      "9.7.17.9.3. Tensorcore 5th Generation Instructions: tcgen05.shift",
      "tcgen05.shift",
      "Tensorcore 5th Generation Instructions",
      Vector("tcgen05"),
      Vector("tcgen05.shift.cta_group.down  [taddr];\n\n.cta_group = { .cta_group::1, .cta_group::2 }"),
      PtxVersion(8, 6),
      SmTarget(10),
      Vector(InstructionForm("tcgen05", Vector(OperandConstraint.Address), Vector(Set("shift"), Set("cta_group"), Set("down")), Set(), true))
    ),
    InstructionInfo(
      "tcgen05-special-sync-operations-fence",
      "9.7.17.11.1. TensorCore 5th Generation Instructions: tcgen05.fence",
      "tcgen05.fence",
      "TensorCore 5th Generation Instructions",
      Vector("tcgen05", "st", "ld", "setp", "bra"),
      Vector("tcgen05.fence::before_thread_sync ;\ntcgen05.fence::after_thread_sync  ;"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector()
    ),
    InstructionInfo(
      "tcgen-async-sync-operations-commit",
      "9.7.17.12.1. TensorCore 5th Generation Instructions: tcgen05.commit",
      "tcgen05.commit",
      "TensorCore 5th Generation Instructions",
      Vector("tcgen05", "mbarrier", "bra"),
      Vector("tcgen05.commit.cta_group.completion_mechanism{.shared::cluster}{.multicast}.b64\n                                                            [mbar] {, ctaMask};\n\n.completion_mechanism = { .mbarrier::arrive::one }\n.cta_group            = { .cta_group::1, .cta_group::2 }\n.multicast            = { .multicast::cluster }"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("tcgen05", Vector(OperandConstraint.Address), Vector(Set("commit"), Set("cta_group"), Set("completion_mechanism"), Set("b64")), Set("multicast", "shared::cluster"), false))
    ),
    InstructionInfo(
      "stack-manipulation-instructions-stacksave",
      "9.7.18.1. Stack Manipulation Instructions: stacksave",
      "stacksave",
      "Stack Manipulation Instructions",
      Vector("stacksave"),
      Vector("stacksave.type  d;\n\n.type = { .u32, .u64 };"),
      PtxVersion(7, 3),
      SmTarget(52),
      Vector(InstructionForm("stacksave", Vector(OperandConstraint.Register), Vector(Set("u32", "u64")), Set(), true))
    ),
    InstructionInfo(
      "stack-manipulation-instructions-stackrestore",
      "9.7.18.2. Stack Manipulation Instructions: stackrestore",
      "stackrestore",
      "Stack Manipulation Instructions",
      Vector("stackrestore", "stacksave"),
      Vector("stackrestore.type  a;\n\n.type = { .u32, .u64 };"),
      PtxVersion(7, 3),
      SmTarget(52),
      Vector(InstructionForm("stackrestore", Vector(OperandConstraint.Value), Vector(Set("u32", "u64")), Set(), true))
    ),
    InstructionInfo(
      "stack-manipulation-instructions-alloca",
      "9.7.18.3. Stack Manipulation Instructions: alloca",
      "alloca",
      "Stack Manipulation Instructions",
      Vector("alloca", "stacksave"),
      Vector("alloca.type  ptr, size{, immAlign};\n\n.type = { .u32, .u64 };"),
      PtxVersion(7, 3),
      SmTarget(52),
      Vector(InstructionForm("alloca", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(Set("u32", "u64")), Set(), true))
    ),
    InstructionInfo(
      "scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax",
      "9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax",
      "vadd, vsub, vabsdiff, vmin, vmax",
      "Scalar Video Instructions",
      Vector("vop", "vadd", "vsub", "vabsdiff", "vmin"),
      Vector("// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("vop", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("dtype"), Set("atype"), Set("s32", "u32"), Set("add", "max", "min")), Set("sat"), true), InstructionForm("vop", Vector(OperandConstraint.Unknown("unparsed")), Vector(), Set(), false))
    ),
    InstructionInfo(
      "scalar-video-instructions-vshl-vshr",
      "9.7.19.1.2. Scalar Video Instructions: vshl, vshr",
      "vshl, vshr",
      "Scalar Video Instructions",
      Vector("vop", "vshl", "vshr"),
      Vector("// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.u32{.sat}.mode       d, a{.asel}, b{.bsel};\nvop.dtype.atype.u32{.sat}.mode.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.u32{.sat}.mode  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vshl, vshr };\n.dtype = .atype = { .u32, .s32 };\n.mode  = { .clamp, .wrap };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("vop", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("dtype"), Set("s32", "u32"), Set("u32"), Set("clamp", "wrap"), Set("add", "max", "min")), Set("sat"), true), InstructionForm("vop", Vector(OperandConstraint.Unknown("unparsed")), Vector(), Set(), false))
    ),
    InstructionInfo(
      "scalar-video-instructions-vmad",
      "9.7.19.1.3. Scalar Video Instructions: vmad",
      "vmad",
      "Scalar Video Instructions",
      Vector("vmad"),
      Vector("// 32-bit scalar operation\nvmad.dtype.atype.btype{.sat}{.scale}     d, {-}a{.asel}, {-}b{.bsel},\n                                         {-}c;\nvmad.dtype.atype.btype.po{.sat}{.scale}  d, a{.asel}, b{.bsel}, c;\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.scale = { .shr7, .shr15 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("vmad", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("dtype"), Set("atype"), Set("s32", "u32"), Set("po")), Set("sat", "shr15", "shr7"), true))
    )
  )

  private def chunk14: Vector[InstructionInfo] = Vector(
    InstructionInfo(
      "scalar-video-instructions-vset",
      "9.7.19.1.4. Scalar Video Instructions: vset",
      "vset",
      "Scalar Video Instructions",
      Vector("vset"),
      Vector("// 32-bit scalar operation, with optional secondary operation\nvset.atype.btype.cmp       d, a{.asel}, b{.bsel};\nvset.atype.btype.cmp.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvset.atype.btype.cmp  d.dsel, a{.asel}, b{.bsel}, c;\n\n.atype = .btype = { .u32, .s32 };\n.cmp   = { .eq, .ne, .lt, .le, .gt, .ge };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("vset", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(Set("atype"), Set("s32", "u32"), Set("eq", "ge", "gt", "le", "lt", "ne"), Set("add", "max", "min")), Set(), true))
    ),
    InstructionInfo(
      "simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2",
      "9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
      "vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
      "SIMD Video Instructions",
      Vector("vop2", "vadd2", "vsub2", "vmin2"),
      Vector("// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"),
      PtxVersion(3, 0),
      SmTarget(30),
      Vector(InstructionForm("vop2", Vector(OperandConstraint.Unknown("unparsed")), Vector(), Set(), false))
    ),
    InstructionInfo(
      "simd-video-instructions-vset2",
      "9.7.19.2.2. SIMD Video Instructions: vset2",
      "vset2",
      "SIMD Video Instructions",
      Vector("vset2"),
      Vector("// SIMD instruction with secondary SIMD merge operation\nvset2.atype.btype.cmp  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvset2.atype.btype.cmp.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n.atype = .btype = { .u32, .s32 };\n.cmp   = { .eq, .ne, .lt, .le, .gt, .ge };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"),
      PtxVersion(3, 0),
      SmTarget(30),
      Vector()
    ),
    InstructionInfo(
      "simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4",
      "9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
      "vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
      "SIMD Video Instructions",
      Vector("vop4", "defaults", "vadd4", "vsub4", "vmin4"),
      Vector("// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"),
      PtxVersion(3, 0),
      SmTarget(30),
      Vector(InstructionForm("vop4", Vector(OperandConstraint.Unknown("unparsed")), Vector(), Set(), false))
    ),
    InstructionInfo(
      "simd-video-instructions-vset4",
      "9.7.19.2.4. SIMD Video Instructions: vset4",
      "vset4",
      "SIMD Video Instructions",
      Vector("vset4", "defaults"),
      Vector("// SIMD instruction with secondary SIMD merge operation\nvset4.atype.btype.cmp  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvset4.atype.btype.cmp.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n.atype = .btype = { .u32, .s32 };\n.cmp   = { .eq, .ne, .lt, .le, .gt, .ge };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"),
      PtxVersion(3, 0),
      SmTarget(30),
      Vector()
    ),
    InstructionInfo(
      "miscellaneous-instructions-brkpt",
      "9.7.20.1. Miscellaneous Instructions: brkpt",
      "brkpt",
      "Miscellaneous Instructions",
      Vector("brkpt"),
      Vector("brkpt;"),
      PtxVersion(1, 0),
      SmTarget(11),
      Vector()
    ),
    InstructionInfo(
      "miscellaneous-instructions-nanosleep",
      "9.7.20.2. Miscellaneous Instructions: nanosleep",
      "nanosleep",
      "Miscellaneous Instructions",
      Vector("nanosleep"),
      Vector("nanosleep.u32 t;"),
      PtxVersion(1, 0),
      SmTarget(70),
      Vector(InstructionForm("nanosleep", Vector(OperandConstraint.Value), Vector(Set("u32")), Set(), true))
    ),
    InstructionInfo(
      "miscellaneous-instructions-pmevent",
      "9.7.20.3. Miscellaneous Instructions: pmevent",
      "pmevent",
      "Miscellaneous Instructions",
      Vector("pmevent"),
      Vector("pmevent       a;    // trigger a single performance monitor event\npmevent.mask  a;    // trigger one or more performance monitor events"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("pmevent", Vector(OperandConstraint.Value), Vector(), Set(), true))
    ),
    InstructionInfo(
      "miscellaneous-instructions-trap",
      "9.7.20.4. Miscellaneous Instructions: trap",
      "trap",
      "Miscellaneous Instructions",
      Vector("trap"),
      Vector("trap;"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector()
    ),
    InstructionInfo(
      "miscellaneous-instructions-setmaxnreg",
      "9.7.20.5. Miscellaneous Instructions: setmaxnreg",
      "setmaxnreg",
      "Miscellaneous Instructions",
      Vector("setmaxnreg"),
      Vector("setmaxnreg.action.sync.aligned.u32 imm-reg-count;\n\n.action = { .inc, .dec };"),
      PtxVersion(8, 0),
      SmTarget(100),
      Vector(InstructionForm("setmaxnreg", Vector(OperandConstraint.Value), Vector(Set("dec", "inc"), Set("sync"), Set("aligned"), Set("u32")), Set(), false))
    )
  )

  override lazy val instructions: Vector[InstructionInfo] = Vector(chunk0, chunk1, chunk2, chunk3, chunk4, chunk5, chunk6, chunk7, chunk8, chunk9, chunk10, chunk11, chunk12, chunk13, chunk14).flatten
}
