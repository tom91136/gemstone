// Generated PTX instruction model from the pinned NVIDIA ISA 9.3 specification. Do not edit by hand.
package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.Col.*

enum InstructionDecodeError {
  case UnknownOpcode(opcode: String)
  case InvalidOperandCount(opcode: String, expected: Col[Int], actual: Int)
  case AmbiguousInstruction(opcode: String, definitions: Col[String])
}

type Decoder = (
    operands: Col[Operand],
    mods: Col[Modifier],
    pred: Option[Predication]
) => Either[InstructionDecodeError, Instruction]

enum InstructionDef(val opcode: String, val decoder: Decoder, metadata: () => Metadata) {
  case Abs extends InstructionDef(
        "abs",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Abs(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("abs", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-abs",
              "9.7.1.11. Integer Arithmetic Instructions: abs",
              "abs",
              "Integer Arithmetic Instructions",
              Col("abs.type  d, a;\n\n.type = { .s16, .s32, .s64 };"),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm(
                "abs",
                Col(OperandConstraint.Register, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("s16", "s32", "s64")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            ),
            Metadata.Entry(
              "floating-point-instructions-abs",
              "9.7.3.9. Floating Point Instructions: abs",
              "abs",
              "Floating Point Instructions",
              Col("abs{.ftz}.f32  d, a;\nabs.f64        d, a;"),
              PTXVersion(1, 0),
              SMTarget(13),
              Col(
                InstructionForm(
                  "abs",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f32")),
                  _root_.scala.collection.immutable.Set("ftz"),
                  true
                ),
                InstructionForm(
                  "abs",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f64")),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            ),
            Metadata.Entry(
              "half-precision-floating-point-instructions-abs",
              "9.7.4.6. Half Precision Floating Point Instructions: abs",
              "abs",
              "Half Precision Floating Point Instructions",
              Col("abs{.ftz}.f16    d, a;\nabs{.ftz}.f16x2  d, a;\nabs.bf16         d, a;\nabs.bf16x2       d, a;"),
              PTXVersion(6, 5),
              SMTarget(53),
              Col(
                InstructionForm(
                  "abs",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f16")),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "abs",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f16x2")),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "abs",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("bf16")),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "abs",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("bf16x2")),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case Activemask extends InstructionDef(
        "activemask",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 1) Right(Instruction.Activemask(operands(0), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("activemask", Col(1), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-activemask",
              "9.7.14.12. Parallel Synchronization and Communication Instructions: activemask",
              "activemask",
              "Parallel Synchronization and Communication Instructions",
              Col("activemask.b32 d;"),
              PTXVersion(6, 2),
              SMTarget(30),
              Col(InstructionForm(
                "activemask",
                Col(OperandConstraint.Register),
                Col(_root_.scala.collection.immutable.Set("b32")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Add extends InstructionDef(
        "add",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.Add(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("add", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-add",
              "9.7.1.1. Integer Arithmetic Instructions: add",
              "add",
              "Integer Arithmetic Instructions",
              Col(
                "add.type1        d, a, b;\nadd{.sat}.type2  d, a, b;\n\n.type1 = { .u16, .u64,\n           .s16, .s64 };\n\n.type2 = { .u32, .u16x2, .u8x4,\n           .s32, .s16x2, .s8x4 };"
              ),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(
                InstructionForm(
                  "add",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("s16", "s64", "u16", "u64")),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "add",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("s16x2", "s32", "s8x4", "u16x2", "u32", "u8x4")),
                  _root_.scala.collection.immutable.Set("sat"),
                  false
                )
              )
            ),
            Metadata.Entry(
              "floating-point-instructions-add",
              "9.7.3.3. Floating Point Instructions: add",
              "add",
              "Floating Point Instructions",
              Col(
                "add{.rnd}{.ftz}{.sat}.f32  d, a, b;\nadd{.rnd}{.ftz}.f32x2      d, a, b;\nadd{.rnd}.f64              d, a, b;\n\n.rnd = { .rn, .rz, .rm, .rp };"
              ),
              PTXVersion(1, 0),
              SMTarget(13),
              Col(
                InstructionForm(
                  "add",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f32")),
                  _root_.scala.collection.immutable.Set("ftz", "rm", "rn", "rp", "rz", "sat"),
                  false
                ),
                InstructionForm(
                  "add",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f32x2")),
                  _root_.scala.collection.immutable.Set("ftz", "rm", "rn", "rp", "rz"),
                  false
                ),
                InstructionForm(
                  "add",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f64")),
                  _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"),
                  false
                )
              )
            ),
            Metadata.Entry(
              "half-precision-floating-point-instructions-add",
              "9.7.4.1. Half Precision Floating Point Instructions: add",
              "add",
              "Half Precision Floating Point Instructions",
              Col(
                "add{.rnd}{.ftz}{.sat}.f16   d, a, b;\nadd{.rnd}{.ftz}{.sat}.f16x2 d, a, b;\n\nadd{.rnd}.bf16   d, a, b;\nadd{.rnd}.bf16x2 d, a, b;\n\n.rnd = { .rn };"
              ),
              PTXVersion(4, 2),
              SMTarget(53),
              Col(
                InstructionForm(
                  "add",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f16")),
                  _root_.scala.collection.immutable.Set("ftz", "rn", "sat"),
                  false
                ),
                InstructionForm(
                  "add",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f16x2")),
                  _root_.scala.collection.immutable.Set("ftz", "rn", "sat"),
                  false
                ),
                InstructionForm(
                  "add",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("bf16")),
                  _root_.scala.collection.immutable.Set("rn"),
                  false
                ),
                InstructionForm(
                  "add",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("bf16x2")),
                  _root_.scala.collection.immutable.Set("rn"),
                  false
                )
              )
            ),
            Metadata.Entry(
              "mixed-precision-floating-point-instructions-add",
              "9.7.5.1. Mixed Precision Floating Point Instructions: add",
              "add",
              "Mixed Precision Floating Point Instructions",
              Col("add{.rnd}{.sat}.f32.atype  d, a, c;\n\n.atype = { .f16, .bf16};\n.rnd   = { .rn, .rz, .rm, .rp };"),
              PTXVersion(1, 0),
              SMTarget(100),
              Col(InstructionForm(
                "add",
                Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("f32"), _root_.scala.collection.immutable.Set("bf16", "f16")),
                _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz", "sat"),
                true
              ))
            )
          ))
      )
  case AddCc extends InstructionDef(
        "add.cc",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.AddCc(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("add.cc", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "extended-precision-arithmetic-instructions-add-cc",
              "9.7.2.1. Extended-Precision Arithmetic Instructions: add.cc",
              "add.cc",
              "Extended-Precision Arithmetic Instructions",
              Col("add.cc.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"),
              PTXVersion(1, 0),
              SMTarget(20),
              Col(InstructionForm(
                "add.cc",
                Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Addc extends InstructionDef(
        "addc",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.Addc(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("addc", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "extended-precision-arithmetic-instructions-addc",
              "9.7.2.2. Extended-Precision Arithmetic Instructions: addc",
              "addc",
              "Extended-Precision Arithmetic Instructions",
              Col("addc{.cc}.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"),
              PTXVersion(1, 0),
              SMTarget(20),
              Col(InstructionForm(
                "addc",
                Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")),
                _root_.scala.collection.immutable.Set("cc"),
                true
              ))
            )
          ))
      )
  case Alloca extends InstructionDef(
        "alloca",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2 || operands.size == 3)
            Right(Instruction.Alloca(operands(0), operands(1), operands.lift(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("alloca", Col(2, 3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "stack-manipulation-instructions-alloca",
              "9.7.18.3. Stack Manipulation Instructions: alloca",
              "alloca",
              "Stack Manipulation Instructions",
              Col("alloca.type  ptr, size{, immAlign};\n\n.type = { .u32, .u64 };"),
              PTXVersion(7, 3),
              SMTarget(52),
              Col(InstructionForm(
                "alloca",
                Col(OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("u32", "u64")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case And extends InstructionDef(
        "and",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.And(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("and", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "logic-and-shift-instructions-and",
              "9.7.8.1. Logic and Shift Instructions: and",
              "and",
              "Logic and Shift Instructions",
              Col("and.type d, a, b;\n\n.type = { .pred, .b16, .b32, .b64 };"),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm(
                "and",
                Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("b16", "b32", "b64", "pred")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case ApplyPriority extends InstructionDef(
        "applypriority",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.ApplyPriority(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("applypriority", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-applypriority",
              "9.7.9.17. Data Movement and Conversion Instructions: applypriority",
              "applypriority",
              "Data Movement and Conversion Instructions",
              Col(
                "applypriority{.global}.level::eviction_priority  [a], size;\n\n.level::eviction_priority = { .L2::evict_normal };"
              ),
              PTXVersion(7, 4),
              SMTarget(80),
              Col(InstructionForm(
                "applypriority",
                Col(OperandConstraint.Address, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("L2::evict_normal")),
                _root_.scala.collection.immutable.Set("global"),
                true
              ))
            )
          ))
      )
  case Atom extends InstructionDef(
        "atom",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3 || operands.size == 4)
            Right(Instruction.Atom(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("atom", Col(3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-atom",
              "9.7.14.5. Parallel Synchronization and Communication Instructions: atom",
              "atom",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "atom{.sem}{.scope}{.space}.op{.level::cache_hint}.type d, [a], b{, cache_policy};\n\natom{.sem}{.scope}{.space}.cas.type1 d, [a], b, c;\n\natom{.sem}{.scope}{.space}.exch{.level::cache_hint}.type2 d, [a], b {, cache_policy};\n\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16     d, [a], b{, cache_policy};\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16x2   d, [a], b{, cache_policy};\n\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16    d, [a], b{, cache_policy};\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16x2  d, [a], b{, cache_policy};\n\n.space =              { .global, .shared{::cta, ::cluster} };\n.sem =                { .relaxed, .acquire, .release, .acq_rel };\n.scope =              { .cta, .cluster, .gpu, .sys };\n\n.op =                 { .and, .or, .xor,\n                        .add, .inc, .dec,\n                        .min, .max };\n.level::cache_hint =  { .L2::cache_hint };\n.type =               { .b32, .b64, .u32, .u64, .s32, .s64, .f32, .f64 };\n.type1 =              { .b16, .b32, .b64, .b128 };\n.type2 =              { .b32, .b64, .b128 };",
                "atom{.sem}{.scope}{.global}.add{.level::cache_hint}.vec_32_bit.f32                  d, [a], b{, cache_policy};\natom{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}.vec_16_bit.half_word_type  d, [a], b{, cache_policy};\natom{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}.vec_32_bit.packed_type     d, [a], b{, cache_policy};\n\n.sem =               { .relaxed, .acquire, .release, .acq_rel };\n.scope =             { .cta, .cluster, .gpu, .sys };\n.op =                { .add, .min, .max };\n.half_word_type =    { .f16, .bf16 };\n.packed_type =       { .f16x2, .bf16x2 };\n.vec_16_bit =        { .v2, .v4, .v8 }\n.vec_32_bit =        { .v2, .v4 };\n.level::cache_hint = { .L2::cache_hint }"
              ),
              PTXVersion(1, 0),
              SMTarget(11),
              Col(
                InstructionForm(
                  "atom",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("add", "and", "dec", "inc", "max", "min", "or", "xor"),
                    _root_.scala.collection.immutable.Set("b32", "b64", "f32", "f64", "s32", "s64", "u32", "u64")
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L2::cache_hint",
                    "acq_rel",
                    "acquire",
                    "cluster",
                    "cta",
                    "global",
                    "gpu",
                    "relaxed",
                    "release",
                    "sys"
                  ),
                  false
                ),
                InstructionForm(
                  "atom",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("cas"),
                    _root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64")
                  ),
                  _root_.scala.collection.immutable.Set(
                    "acq_rel",
                    "acquire",
                    "cluster",
                    "cta",
                    "global",
                    "gpu",
                    "relaxed",
                    "release",
                    "sys"
                  ),
                  false
                ),
                InstructionForm(
                  "atom",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("exch"),
                    _root_.scala.collection.immutable.Set("b128", "b32", "b64")
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L2::cache_hint",
                    "acq_rel",
                    "acquire",
                    "cluster",
                    "cta",
                    "global",
                    "gpu",
                    "relaxed",
                    "release",
                    "sys"
                  ),
                  false
                ),
                InstructionForm(
                  "atom",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("add"),
                    _root_.scala.collection.immutable.Set("noftz"),
                    _root_.scala.collection.immutable.Set("f16")
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L2::cache_hint",
                    "acq_rel",
                    "acquire",
                    "cluster",
                    "cta",
                    "global",
                    "gpu",
                    "relaxed",
                    "release",
                    "sys"
                  ),
                  false
                ),
                InstructionForm(
                  "atom",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("add"),
                    _root_.scala.collection.immutable.Set("noftz"),
                    _root_.scala.collection.immutable.Set("f16x2")
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L2::cache_hint",
                    "acq_rel",
                    "acquire",
                    "cluster",
                    "cta",
                    "global",
                    "gpu",
                    "relaxed",
                    "release",
                    "sys"
                  ),
                  false
                ),
                InstructionForm(
                  "atom",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("add"),
                    _root_.scala.collection.immutable.Set("noftz"),
                    _root_.scala.collection.immutable.Set("bf16")
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L2::cache_hint",
                    "acq_rel",
                    "acquire",
                    "cluster",
                    "cta",
                    "global",
                    "gpu",
                    "relaxed",
                    "release",
                    "sys"
                  ),
                  false
                ),
                InstructionForm(
                  "atom",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("add"),
                    _root_.scala.collection.immutable.Set("noftz"),
                    _root_.scala.collection.immutable.Set("bf16x2")
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L2::cache_hint",
                    "acq_rel",
                    "acquire",
                    "cluster",
                    "cta",
                    "global",
                    "gpu",
                    "relaxed",
                    "release",
                    "sys"
                  ),
                  false
                ),
                InstructionForm(
                  "atom",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("add"),
                    _root_.scala.collection.immutable.Set("v2", "v4"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L2::cache_hint",
                    "acq_rel",
                    "acquire",
                    "cluster",
                    "cta",
                    "global",
                    "gpu",
                    "relaxed",
                    "release",
                    "sys"
                  ),
                  false
                ),
                InstructionForm(
                  "atom",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("add", "max", "min"),
                    _root_.scala.collection.immutable.Set("noftz"),
                    _root_.scala.collection.immutable.Set("v2", "v4", "v8"),
                    _root_.scala.collection.immutable.Set("bf16", "f16")
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L2::cache_hint",
                    "acq_rel",
                    "acquire",
                    "cluster",
                    "cta",
                    "global",
                    "gpu",
                    "relaxed",
                    "release",
                    "sys"
                  ),
                  false
                ),
                InstructionForm(
                  "atom",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("add", "max", "min"),
                    _root_.scala.collection.immutable.Set("noftz"),
                    _root_.scala.collection.immutable.Set("v2", "v4"),
                    _root_.scala.collection.immutable.Set("bf16x2", "f16x2")
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L2::cache_hint",
                    "acq_rel",
                    "acquire",
                    "cluster",
                    "cta",
                    "global",
                    "gpu",
                    "relaxed",
                    "release",
                    "sys"
                  ),
                  false
                )
              )
            )
          ))
      )
  case Bar extends InstructionDef(
        "bar",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 1 || operands.size == 2 || operands.size == 3 || operands.size == 4)
            Right(Instruction.Bar(operands(0), operands.lift(1), operands.lift(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("bar", Col(1, 2, 3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-bar",
              "9.7.14.1. Parallel Synchronization and Communication Instructions: bar, barrier",
              "bar, barrier",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "barrier{.cta}.sync{.aligned}      a{, b};\nbarrier{.cta}.arrive{.aligned}    a, b;\n\nbarrier{.cta}.red.popc{.aligned}.u32  d, a{, b}, {!}c;\nbarrier{.cta}.red.op{.aligned}.pred   p, a{, b}, {!}c;\n\nbar{.cta}.sync      a{, b};\nbar{.cta}.arrive    a, b;\n\nbar{.cta}.red.popc.u32  d, a{, b}, {!}c;\nbar{.cta}.red.op.pred   p, a{, b}, {!}c;\n\n.op = { .and, .or };"
              ),
              PTXVersion(1, 0),
              SMTarget(20),
              Col(
                InstructionForm(
                  "bar",
                  Col(OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("sync")),
                  _root_.scala.collection.immutable.Set("cta"),
                  false
                ),
                InstructionForm(
                  "bar",
                  Col(OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("arrive")),
                  _root_.scala.collection.immutable.Set("cta"),
                  false
                ),
                InstructionForm(
                  "bar",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("red"),
                    _root_.scala.collection.immutable.Set("popc"),
                    _root_.scala.collection.immutable.Set("u32")
                  ),
                  _root_.scala.collection.immutable.Set("cta"),
                  false
                ),
                InstructionForm(
                  "bar",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("red"),
                    _root_.scala.collection.immutable.Set("and", "or"),
                    _root_.scala.collection.immutable.Set("pred")
                  ),
                  _root_.scala.collection.immutable.Set("cta"),
                  false
                )
              )
            )
          ))
      )
  case BarWarpSync extends InstructionDef(
        "bar.warp.sync",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 1) Right(Instruction.BarWarpSync(operands(0), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("bar.warp.sync", Col(1), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-bar-warp-sync",
              "9.7.14.2. Parallel Synchronization and Communication Instructions: bar.warp.sync",
              "bar.warp.sync",
              "Parallel Synchronization and Communication Instructions",
              Col("bar.warp.sync      membermask;"),
              PTXVersion(6, 0),
              SMTarget(30),
              Col(InstructionForm(
                "bar.warp.sync",
                Col(OperandConstraint.Value),
                Col(),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Barrier extends InstructionDef(
        "barrier",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 1 || operands.size == 2 || operands.size == 3 || operands.size == 4)
            Right(Instruction.Barrier(operands(0), operands.lift(1), operands.lift(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("barrier", Col(1, 2, 3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-bar",
              "9.7.14.1. Parallel Synchronization and Communication Instructions: bar, barrier",
              "bar, barrier",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "barrier{.cta}.sync{.aligned}      a{, b};\nbarrier{.cta}.arrive{.aligned}    a, b;\n\nbarrier{.cta}.red.popc{.aligned}.u32  d, a{, b}, {!}c;\nbarrier{.cta}.red.op{.aligned}.pred   p, a{, b}, {!}c;\n\nbar{.cta}.sync      a{, b};\nbar{.cta}.arrive    a, b;\n\nbar{.cta}.red.popc.u32  d, a{, b}, {!}c;\nbar{.cta}.red.op.pred   p, a{, b}, {!}c;\n\n.op = { .and, .or };"
              ),
              PTXVersion(1, 0),
              SMTarget(20),
              Col(
                InstructionForm(
                  "barrier",
                  Col(OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("sync")),
                  _root_.scala.collection.immutable.Set("aligned", "cta"),
                  false
                ),
                InstructionForm(
                  "barrier",
                  Col(OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("arrive")),
                  _root_.scala.collection.immutable.Set("aligned", "cta"),
                  false
                ),
                InstructionForm(
                  "barrier",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("red"),
                    _root_.scala.collection.immutable.Set("popc"),
                    _root_.scala.collection.immutable.Set("u32")
                  ),
                  _root_.scala.collection.immutable.Set("aligned", "cta"),
                  false
                ),
                InstructionForm(
                  "barrier",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("red"),
                    _root_.scala.collection.immutable.Set("and", "or"),
                    _root_.scala.collection.immutable.Set("pred")
                  ),
                  _root_.scala.collection.immutable.Set("aligned", "cta"),
                  false
                )
              )
            )
          ))
      )
  case BarrierCluster extends InstructionDef(
        "barrier.cluster",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 0) Right(Instruction.BarrierCluster(mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("barrier.cluster", Col(0), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-barrier-cluster",
              "9.7.14.3. Parallel Synchronization and Communication Instructions: barrier.cluster",
              "barrier.cluster",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "barrier.cluster.arrive{.sem}{.aligned};\nbarrier.cluster.wait{.acquire}{.aligned};\n\n.sem = {.release, .relaxed}"
              ),
              PTXVersion(7, 8),
              SMTarget(90),
              Col(
                InstructionForm(
                  "barrier.cluster",
                  Col(),
                  Col(_root_.scala.collection.immutable.Set("arrive")),
                  _root_.scala.collection.immutable.Set("aligned", "relaxed", "release"),
                  true
                ),
                InstructionForm(
                  "barrier.cluster",
                  Col(),
                  Col(_root_.scala.collection.immutable.Set("wait")),
                  _root_.scala.collection.immutable.Set("acquire", "aligned"),
                  true
                )
              )
            )
          ))
      )
  case Bfe extends InstructionDef(
        "bfe",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4) Right(Instruction.Bfe(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("bfe", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-bfe",
              "9.7.1.20. Integer Arithmetic Instructions: bfe",
              "bfe",
              "Integer Arithmetic Instructions",
              Col("bfe.type  d, a, b, c;\n\n.type = { .u32, .u64,\n          .s32, .s64 };"),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(InstructionForm(
                "bfe",
                Col(
                  OperandConstraint.Register,
                  OperandConstraint.Value,
                  OperandConstraint.Value,
                  OperandConstraint.Value
                ),
                Col(_root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Bfi extends InstructionDef(
        "bfi",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 5)
            Right(Instruction.Bfi(operands(0), operands(1), operands(2), operands(3), operands(4), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("bfi", Col(5), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-bfi",
              "9.7.1.21. Integer Arithmetic Instructions: bfi",
              "bfi",
              "Integer Arithmetic Instructions",
              Col("bfi.type  f, a, b, c, d;\n\n.type = { .b32, .b64 };"),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(InstructionForm(
                "bfi",
                Col(
                  OperandConstraint.Value,
                  OperandConstraint.Value,
                  OperandConstraint.Value,
                  OperandConstraint.Value,
                  OperandConstraint.Register
                ),
                Col(_root_.scala.collection.immutable.Set("b32", "b64")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Bfind extends InstructionDef(
        "bfind",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Bfind(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("bfind", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-bfind",
              "9.7.1.17. Integer Arithmetic Instructions: bfind",
              "bfind",
              "Integer Arithmetic Instructions",
              Col(
                "bfind.type           d, a;\nbfind.shiftamt.type  d, a;\n\n.type = { .u32, .u64,\n          .s32, .s64 };"
              ),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(
                InstructionForm(
                  "bfind",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")),
                  _root_.scala.collection.immutable.Set(),
                  true
                ),
                InstructionForm(
                  "bfind",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("shiftamt"),
                    _root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case Bmsk extends InstructionDef(
        "bmsk",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.Bmsk(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("bmsk", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-bmsk",
              "9.7.1.23. Integer Arithmetic Instructions: bmsk",
              "bmsk",
              "Integer Arithmetic Instructions",
              Col("bmsk.mode.b32  d, a, b;\n\n.mode = { .clamp, .wrap };"),
              PTXVersion(7, 6),
              SMTarget(70),
              Col(InstructionForm(
                "bmsk",
                Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                Col(
                  _root_.scala.collection.immutable.Set("clamp", "wrap"),
                  _root_.scala.collection.immutable.Set("b32")
                ),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Bra extends InstructionDef(
        "bra",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 1) Right(Instruction.Bra(operands(0), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("bra", Col(1), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "control-flow-instructions-bra",
              "9.7.13.3. Control Flow Instructions: bra",
              "bra",
              "Control Flow Instructions",
              Col(
                "@p   bra{.uni}  tgt;           // tgt is a label\n     bra{.uni}  tgt;           // unconditional branch"
              ),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm(
                "bra",
                Col(OperandConstraint.Value),
                Col(),
                _root_.scala.collection.immutable.Set("uni"),
                true
              ))
            )
          ))
      )
  case Brev extends InstructionDef(
        "brev",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Brev(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("brev", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-brev",
              "9.7.1.19. Integer Arithmetic Instructions: brev",
              "brev",
              "Integer Arithmetic Instructions",
              Col("brev.type  d, a;\n\n.type = { .b32, .b64 };"),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(InstructionForm(
                "brev",
                Col(OperandConstraint.Register, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("b32", "b64")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Brkpt extends InstructionDef(
        "brkpt",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 0) Right(Instruction.Brkpt(mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("brkpt", Col(0), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "miscellaneous-instructions-brkpt",
              "9.7.20.1. Miscellaneous Instructions: brkpt",
              "brkpt",
              "Miscellaneous Instructions",
              Col("brkpt;"),
              PTXVersion(1, 0),
              SMTarget(11),
              Col(InstructionForm("brkpt", Col(), Col(), _root_.scala.collection.immutable.Set(), true))
            )
          ))
      )
  case BrxIdx extends InstructionDef(
        "brx.idx",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.BrxIdx(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("brx.idx", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "control-flow-instructions-brx-idx",
              "9.7.13.4. Control Flow Instructions: brx.idx",
              "brx.idx",
              "Control Flow Instructions",
              Col("@p    brx.idx{.uni} index, tlist;\n      brx.idx{.uni} index, tlist;"),
              PTXVersion(6, 0),
              SMTarget(30),
              Col(InstructionForm(
                "brx.idx",
                Col(OperandConstraint.Value, OperandConstraint.Value),
                Col(),
                _root_.scala.collection.immutable.Set("uni"),
                true
              ))
            )
          ))
      )
  case Call extends InstructionDef(
        "call",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 1 || operands.size == 2 || operands.size == 3 || operands.size == 4)
            Right(Instruction.Call(operands(0), operands.lift(1), operands.lift(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("call", Col(1, 2, 3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "control-flow-instructions-call",
              "9.7.13.5. Control Flow Instructions: call",
              "call",
              "Control Flow Instructions",
              Col(
                "// direct call to named function, func is a symbol\ncall{.uni} (ret-param), func, (param-list);\ncall{.uni} func, (param-list);\ncall{.uni} func;\n\n// indirect call via pointer, with full list of call targets\ncall{.uni} (ret-param), fptr, (param-list), flist;\ncall{.uni} fptr, (param-list), flist;\ncall{.uni} fptr, flist;\n\n// indirect call via pointer, with no knowledge of call targets\ncall{.uni} (ret-param), fptr, (param-list), fproto;\ncall{.uni} fptr, (param-list), fproto;\ncall{.uni} fptr, fproto;"
              ),
              PTXVersion(1, 0),
              SMTarget(20),
              Col(
                InstructionForm(
                  "call",
                  Col(
                    OperandConstraint.Unknown("unparsed"),
                    OperandConstraint.Symbol,
                    OperandConstraint.Unknown("unparsed")
                  ),
                  Col(),
                  _root_.scala.collection.immutable.Set("uni"),
                  false
                ),
                InstructionForm(
                  "call",
                  Col(OperandConstraint.Symbol, OperandConstraint.Unknown("unparsed")),
                  Col(),
                  _root_.scala.collection.immutable.Set("uni"),
                  false
                ),
                InstructionForm(
                  "call",
                  Col(OperandConstraint.Symbol),
                  Col(),
                  _root_.scala.collection.immutable.Set("uni"),
                  true
                ),
                InstructionForm(
                  "call",
                  Col(
                    OperandConstraint.Unknown("unparsed"),
                    OperandConstraint.Value,
                    OperandConstraint.Unknown("unparsed"),
                    OperandConstraint.Value
                  ),
                  Col(),
                  _root_.scala.collection.immutable.Set("uni"),
                  false
                ),
                InstructionForm(
                  "call",
                  Col(OperandConstraint.Value, OperandConstraint.Unknown("unparsed"), OperandConstraint.Value),
                  Col(),
                  _root_.scala.collection.immutable.Set("uni"),
                  false
                ),
                InstructionForm(
                  "call",
                  Col(OperandConstraint.Value, OperandConstraint.Value),
                  Col(),
                  _root_.scala.collection.immutable.Set("uni"),
                  true
                ),
                InstructionForm(
                  "call",
                  Col(
                    OperandConstraint.Unknown("unparsed"),
                    OperandConstraint.Value,
                    OperandConstraint.Unknown("unparsed"),
                    OperandConstraint.Value
                  ),
                  Col(),
                  _root_.scala.collection.immutable.Set("uni"),
                  false
                ),
                InstructionForm(
                  "call",
                  Col(OperandConstraint.Value, OperandConstraint.Unknown("unparsed"), OperandConstraint.Value),
                  Col(),
                  _root_.scala.collection.immutable.Set("uni"),
                  false
                ),
                InstructionForm(
                  "call",
                  Col(OperandConstraint.Value, OperandConstraint.Value),
                  Col(),
                  _root_.scala.collection.immutable.Set("uni"),
                  true
                )
              )
            )
          ))
      )
  case Clmad extends InstructionDef(
        "clmad",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Clmad(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("clmad", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-clmad",
              "9.7.1.5. Integer Arithmetic Instructions: clmad",
              "clmad",
              "Integer Arithmetic Instructions",
              Col("clmad.mode.u64 d, a, b, c;\n\n.mode = { .hi, .lo };"),
              PTXVersion(9, 3),
              SMTarget(80),
              Col(InstructionForm(
                "clmad",
                Col(
                  OperandConstraint.Register,
                  OperandConstraint.Value,
                  OperandConstraint.Value,
                  OperandConstraint.Value
                ),
                Col(_root_.scala.collection.immutable.Set("hi", "lo"), _root_.scala.collection.immutable.Set("u64")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case ClusterLaunchControlQueryCancel extends InstructionDef(
        "clusterlaunchcontrol.query_cancel",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2)
            Right(Instruction.ClusterLaunchControlQueryCancel(operands(0), operands(1), mods, pred))
          else
            Left(InstructionDecodeError.InvalidOperandCount("clusterlaunchcontrol.query_cancel", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-clusterlaunchcontrol-query-cancel",
              "9.7.14.19. Parallel Synchronization and Communication Instructions: clusterlaunchcontrol.query_cancel",
              "clusterlaunchcontrol.query_cancel",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "clusterlaunchcontrol.query_cancel.is_canceled.pred.b128 pred, try_cancel_response;\n\nclusterlaunchcontrol.query_cancel.get_first_ctaid.v4.b32.b128 {xdim, ydim, zdim, _},  try_cancel_response;\n\nclusterlaunchcontrol.query_cancel.get_first_ctaid{::dimension}.b32.b128 reg, try_cancel_response;\n\n::dimension = { ::x, ::y, ::z };"
              ),
              PTXVersion(8, 6),
              SMTarget(100),
              Col(
                InstructionForm(
                  "clusterlaunchcontrol.query_cancel",
                  Col(OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("is_canceled"),
                    _root_.scala.collection.immutable.Set("pred"),
                    _root_.scala.collection.immutable.Set("b128")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ),
                InstructionForm(
                  "clusterlaunchcontrol.query_cancel",
                  Col(OperandConstraint.VectorOperand, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("get_first_ctaid"),
                    _root_.scala.collection.immutable.Set("v4"),
                    _root_.scala.collection.immutable.Set("b32"),
                    _root_.scala.collection.immutable.Set("b128")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ),
                InstructionForm(
                  "clusterlaunchcontrol.query_cancel",
                  Col(OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("get_first_ctaid"),
                    _root_.scala.collection.immutable.Set("b32"),
                    _root_.scala.collection.immutable.Set("b128")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case ClusterLaunchControlTryCancel extends InstructionDef(
        "clusterlaunchcontrol.try_cancel",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.ClusterLaunchControlTryCancel(operands(0), operands(1), mods, pred))
          else
            Left(InstructionDecodeError.InvalidOperandCount("clusterlaunchcontrol.try_cancel", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-clusterlaunchcontrol-try-cancel",
              "9.7.14.18. Parallel Synchronization and Communication Instructions: clusterlaunchcontrol.try_cancel",
              "clusterlaunchcontrol.try_cancel",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "clusterlaunchcontrol.try_cancel.async{.space}.completion_mechanism{.multicast::cluster::all}.b128 [addr], [mbar];\n\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n.space = { .shared::cta };"
              ),
              PTXVersion(8, 6),
              SMTarget(100),
              Col(InstructionForm(
                "clusterlaunchcontrol.try_cancel",
                Col(OperandConstraint.Address, OperandConstraint.Address),
                Col(
                  _root_.scala.collection.immutable.Set("async"),
                  _root_.scala.collection.immutable.Set("mbarrier::complete_tx::bytes"),
                  _root_.scala.collection.immutable.Set("b128")
                ),
                _root_.scala.collection.immutable.Set("multicast::cluster::all", "shared::cta"),
                false
              ))
            )
          ))
      )
  case Clz extends InstructionDef(
        "clz",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Clz(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("clz", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-clz",
              "9.7.1.16. Integer Arithmetic Instructions: clz",
              "clz",
              "Integer Arithmetic Instructions",
              Col("clz.type  d, a;\n\n.type = { .b32, .b64 };"),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(InstructionForm(
                "clz",
                Col(OperandConstraint.Register, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("b32", "b64")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Cnot extends InstructionDef(
        "cnot",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Cnot(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("cnot", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "logic-and-shift-instructions-cnot",
              "9.7.8.5. Logic and Shift Instructions: cnot",
              "cnot",
              "Logic and Shift Instructions",
              Col("cnot.type d, a;\n\n.type = { .b16, .b32, .b64 };"),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm(
                "cnot",
                Col(OperandConstraint.Register, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("b16", "b32", "b64")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case CopySign extends InstructionDef(
        "copysign",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.CopySign(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("copysign", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "floating-point-instructions-copysign",
              "9.7.3.2. Floating Point Instructions: copysign",
              "copysign",
              "Floating Point Instructions",
              Col("copysign.type  d, a, b;\n\n.type = { .f32, .f64 };"),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(InstructionForm(
                "copysign",
                Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("f32", "f64")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Cos extends InstructionDef(
        "cos",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Cos(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("cos", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "floating-point-instructions-cos",
              "9.7.3.19. Floating Point Instructions: cos",
              "cos",
              "Floating Point Instructions",
              Col("cos.approx{.ftz}.f32  d, a;"),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm(
                "cos",
                Col(OperandConstraint.Register, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("f32")),
                _root_.scala.collection.immutable.Set("ftz"),
                true
              ))
            )
          ))
      )
  case CpAsync extends InstructionDef(
        "cp.async",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3 || operands.size == 4 || operands.size == 5) Right(Instruction.CpAsync(
            operands(0),
            operands(1),
            operands(2),
            operands.lift(3),
            operands.lift(4),
            mods,
            pred
          ))
          else Left(InstructionDecodeError.InvalidOperandCount("cp.async", Col(3, 4, 5), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-cp-async",
              "9.7.9.26.3.1. Data Movement and Conversion Instructions: cp.async",
              "cp.async",
              "Data Movement and Conversion Instructions",
              Col(
                "cp.async.ca.shared{::cta}.global{.level::cache_hint}{.level::prefetch_size}\n                         [dst], [src], cp-size{, src-size}{, cache_policy} ;\ncp.async.cg.shared{::cta}.global{.level::cache_hint}{.level::prefetch_size}\n                         [dst], [src], 16{, src-size}{, cache_policy} ;\ncp.async.ca.shared{::cta}.global{.level::cache_hint}{.level::prefetch_size}\n                         [dst], [src], cp-size{, ignore-src}{, cache_policy} ;\ncp.async.cg.shared{::cta}.global{.level::cache_hint}{.level::prefetch_size}\n                         [dst], [src], 16{, ignore-src}{, cache_policy} ;\n\n.level::cache_hint =     { .L2::cache_hint }\n.level::prefetch_size =  { .L2::64B, .L2::128B, .L2::256B }\ncp-size =                { 4, 8, 16 }"
              ),
              PTXVersion(7, 0),
              SMTarget(30),
              Col(
                InstructionForm(
                  "cp.async",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("ca"),
                    _root_.scala.collection.immutable.Set("shared"),
                    _root_.scala.collection.immutable.Set("global")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint"),
                  false
                ),
                InstructionForm(
                  "cp.async",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("cg"),
                    _root_.scala.collection.immutable.Set("shared"),
                    _root_.scala.collection.immutable.Set("global")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint"),
                  false
                ),
                InstructionForm(
                  "cp.async",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("ca"),
                    _root_.scala.collection.immutable.Set("shared"),
                    _root_.scala.collection.immutable.Set("global")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint"),
                  false
                ),
                InstructionForm(
                  "cp.async",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("cg"),
                    _root_.scala.collection.immutable.Set("shared"),
                    _root_.scala.collection.immutable.Set("global")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint"),
                  false
                )
              )
            )
          ))
      )
  case CpAsyncBulkMBarrier extends InstructionDef(
        "cp.async.bulk",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          val modifierNames = mods.map(_.name)
          if (
            !modifierNames.contains("bulk_group") && (if (modifierNames.contains("ignore_oob"))
                                                        operands.size == 6 || operands.size == 7
                                                      else if (modifierNames.contains("multicast::cluster"))
                                                        operands.size >= 4 && operands.size <= 6
                                                      else operands.size == 4 || operands.size == 5)
          ) Right(if (modifierNames.contains("ignore_oob")) Instruction.CpAsyncBulkMBarrier(
            operands(0),
            operands(1),
            operands(2),
            operands(5),
            Some(operands(3)),
            Some(operands(4)),
            None,
            operands.lift(6),
            mods,
            pred
          )
          else if (modifierNames.contains("multicast::cluster")) Instruction.CpAsyncBulkMBarrier(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            None,
            None,
            operands.lift(4),
            operands.lift(5),
            mods,
            pred
          )
          else Instruction.CpAsyncBulkMBarrier(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            None,
            None,
            None,
            operands.lift(4),
            mods,
            pred
          ))
          else if (!modifierNames.contains("bulk_group"))
            Left(InstructionDecodeError.InvalidOperandCount("cp.async.bulk", Col(4, 5, 6, 7), operands.size))
          else Left(InstructionDecodeError.UnknownOpcode((("cp.async.bulk") +: modifierNames).mkString(".")))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-cp-async-bulk",
              "9.7.9.26.4.1. Data Movement and Conversion Instructions: cp.async.bulk",
              "cp.async.bulk",
              "Data Movement and Conversion Instructions",
              Col(
                "// global -> shared::cta\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.level::cache_hint}{.ignore_oob}\n                      [dstMem], [srcMem], size{, ignoreBytesLeft, ignoreBytesRight}, [mbar] {, cache_policy};\n\n.sem =                  { .weak }\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.level::cache_hint}{.ignore_oob}.type\n                      [dstMem], [srcMem], size{, ignoreBytesLeft, ignoreBytesRight}, [mbar] {, cache_policy};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.type =                 { .b128 }\n\n\n// global -> shared::cluster\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.multicast}{.level::cache_hint}\n                      [dstMem], [srcMem], size, [mbar] {, ctaMask} {, cache_policy};\n\n.sem =                  { .weak }\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.multicast}{.level::cache_hint}.type\n                      [dstMem], [srcMem], size, [mbar] {, ctaMask} {, cache_policy};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster }\n.type =                 { .b128 }\n\n\n// shared::cta -> shared::cluster\ncp.async.bulk{.sem}.dst.src.completion_mechanism [dstMem], [srcMem], size, [mbar];\n\n.sem =                  { .weak }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism.type [dstMem], [srcMem], size, [mbar];\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.type =                 { .b128 }\n\n\n// shared::cta -> global\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.level::cache_hint}{.cp_mask}\n                      [dstMem], [srcMem], size {, cache_policy} {, byteMask};\n\n.sem =                  { .weak }\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint =    { .L2::cache_hint }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.level::cache_hint}{.cp_mask}.type\n                      [dstMem], [srcMem], size {, cache_policy} {, byteMask};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint =    { .L2::cache_hint }\n.type =                 { .b128 }"
              ),
              PTXVersion(8, 0),
              SMTarget(90),
              Col(
                InstructionForm(
                  "cp.async.bulk",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint", "ignore_oob", "relaxed"),
                  false
                ),
                InstructionForm(
                  "cp.async.bulk",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"),
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group"),
                    _root_.scala.collection.immutable.Set("b128")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint", "ignore_oob"),
                  false
                ),
                InstructionForm(
                  "cp.async.bulk",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint", "multicast::cluster", "relaxed"),
                  false
                ),
                InstructionForm(
                  "cp.async.bulk",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"),
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group"),
                    _root_.scala.collection.immutable.Set("b128")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint", "multicast::cluster"),
                  false
                ),
                InstructionForm(
                  "cp.async.bulk",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group")
                  ),
                  _root_.scala.collection.immutable.Set("relaxed"),
                  false
                ),
                InstructionForm(
                  "cp.async.bulk",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"),
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group"),
                    _root_.scala.collection.immutable.Set("b128")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "cp.async.bulk",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint", "cp_mask", "relaxed"),
                  false
                ),
                InstructionForm(
                  "cp.async.bulk",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"),
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group"),
                    _root_.scala.collection.immutable.Set("b128")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint", "cp_mask"),
                  false
                )
              )
            )
          ))
      )
  case CpAsyncBulkGroup extends InstructionDef(
        "cp.async.bulk",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          val modifierNames = mods.map(_.name)
          if (modifierNames.contains("bulk_group") && (operands.size == 3 || operands.size == 4 || operands.size == 5))
            Right(if (operands.size == 3)
              Instruction.CpAsyncBulkGroup(operands(0), operands(1), operands(2), None, None, mods, pred)
            else if (operands.size == 4 && modifierNames.contains("cp_mask"))
              Instruction.CpAsyncBulkGroup(operands(0), operands(1), operands(2), None, Some(operands(3)), mods, pred)
            else Instruction.CpAsyncBulkGroup(
              operands(0),
              operands(1),
              operands(2),
              Some(operands(3)),
              operands.lift(4),
              mods,
              pred
            ))
          else if (modifierNames.contains("bulk_group"))
            Left(InstructionDecodeError.InvalidOperandCount("cp.async.bulk", Col(3, 4, 5), operands.size))
          else Left(InstructionDecodeError.UnknownOpcode((("cp.async.bulk") +: modifierNames).mkString(".")))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-cp-async-bulk",
              "9.7.9.26.4.1. Data Movement and Conversion Instructions: cp.async.bulk",
              "cp.async.bulk",
              "Data Movement and Conversion Instructions",
              Col(
                "// global -> shared::cta\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.level::cache_hint}{.ignore_oob}\n                      [dstMem], [srcMem], size{, ignoreBytesLeft, ignoreBytesRight}, [mbar] {, cache_policy};\n\n.sem =                  { .weak }\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.level::cache_hint}{.ignore_oob}.type\n                      [dstMem], [srcMem], size{, ignoreBytesLeft, ignoreBytesRight}, [mbar] {, cache_policy};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.type =                 { .b128 }\n\n\n// global -> shared::cluster\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.multicast}{.level::cache_hint}\n                      [dstMem], [srcMem], size, [mbar] {, ctaMask} {, cache_policy};\n\n.sem =                  { .weak }\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.multicast}{.level::cache_hint}.type\n                      [dstMem], [srcMem], size, [mbar] {, ctaMask} {, cache_policy};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster }\n.type =                 { .b128 }\n\n\n// shared::cta -> shared::cluster\ncp.async.bulk{.sem}.dst.src.completion_mechanism [dstMem], [srcMem], size, [mbar];\n\n.sem =                  { .weak }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism.type [dstMem], [srcMem], size, [mbar];\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.type =                 { .b128 }\n\n\n// shared::cta -> global\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.level::cache_hint}{.cp_mask}\n                      [dstMem], [srcMem], size {, cache_policy} {, byteMask};\n\n.sem =                  { .weak }\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint =    { .L2::cache_hint }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.level::cache_hint}{.cp_mask}.type\n                      [dstMem], [srcMem], size {, cache_policy} {, byteMask};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint =    { .L2::cache_hint }\n.type =                 { .b128 }"
              ),
              PTXVersion(8, 0),
              SMTarget(90),
              Col(
                InstructionForm(
                  "cp.async.bulk",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint", "ignore_oob", "relaxed"),
                  false
                ),
                InstructionForm(
                  "cp.async.bulk",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"),
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group"),
                    _root_.scala.collection.immutable.Set("b128")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint", "ignore_oob"),
                  false
                ),
                InstructionForm(
                  "cp.async.bulk",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint", "multicast::cluster", "relaxed"),
                  false
                ),
                InstructionForm(
                  "cp.async.bulk",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"),
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group"),
                    _root_.scala.collection.immutable.Set("b128")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint", "multicast::cluster"),
                  false
                ),
                InstructionForm(
                  "cp.async.bulk",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group")
                  ),
                  _root_.scala.collection.immutable.Set("relaxed"),
                  false
                ),
                InstructionForm(
                  "cp.async.bulk",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"),
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group"),
                    _root_.scala.collection.immutable.Set("b128")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "cp.async.bulk",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint", "cp_mask", "relaxed"),
                  false
                ),
                InstructionForm(
                  "cp.async.bulk",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"),
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group"),
                    _root_.scala.collection.immutable.Set("b128")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint", "cp_mask"),
                  false
                )
              )
            )
          ))
      )
  case CpAsyncBulkCommitGroup extends InstructionDef(
        "cp.async.bulk.commit_group",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 0) Right(Instruction.CpAsyncBulkCommitGroup(mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("cp.async.bulk.commit_group", Col(0), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-cp-async-bulk-commit-group",
              "9.7.9.26.6.1. Data Movement and Conversion Instructions: cp.async.bulk.commit_group",
              "cp.async.bulk.commit_group",
              "Data Movement and Conversion Instructions",
              Col("cp.async.bulk.commit_group;"),
              PTXVersion(8, 0),
              SMTarget(90),
              Col(InstructionForm(
                "cp.async.bulk.commit_group",
                Col(),
                Col(),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case CpAsyncBulkPrefetch extends InstructionDef(
        "cp.async.bulk.prefetch",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2 || operands.size == 3)
            Right(Instruction.CpAsyncBulkPrefetch(operands(0), operands(1), operands.lift(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("cp.async.bulk.prefetch", Col(2, 3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-cp-async-bulk-prefetch",
              "9.7.9.26.4.3. Data Movement and Conversion Instructions: cp.async.bulk.prefetch",
              "cp.async.bulk.prefetch",
              "Data Movement and Conversion Instructions",
              Col(
                "cp.async.bulk.prefetch.L2.src{.level::cache_hint}   [srcMem], size {, cache_policy};\n\n.src =                { .global }\n.level::cache_hint =  { .L2::cache_hint }"
              ),
              PTXVersion(8, 0),
              SMTarget(90),
              Col(InstructionForm(
                "cp.async.bulk.prefetch",
                Col(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("L2"), _root_.scala.collection.immutable.Set("global")),
                _root_.scala.collection.immutable.Set("L2::cache_hint"),
                true
              ))
            )
          ))
      )
  case CpAsyncBulkPrefetchTensor extends InstructionDef(
        "cp.async.bulk.prefetch.tensor",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 1 || operands.size == 2 || operands.size == 3)
            Right(Instruction.CpAsyncBulkPrefetchTensor(operands(0), operands.lift(1), operands.lift(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount(
            "cp.async.bulk.prefetch.tensor",
            Col(1, 2, 3),
            operands.size
          ))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-cp-async-bulk-prefetch-tensor",
              "9.7.9.26.5.4. Data Movement and Conversion Instructions: cp.async.bulk.prefetch.tensor",
              "cp.async.bulk.prefetch.tensor",
              "Data Movement and Conversion Instructions",
              Col(
                "// global -> L2:\ncp.async.bulk.prefetch.tensor.dim.L2.src{.load_mode}{.level::cache_hint} [tensorMap, tensorCoords]\n                                                             {, im2colInfo } {, cache_policy}\n\n.src =                { .global }\n.dim =                { .1d, .2d, .3d, .4d, .5d }\n.load_mode =          { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =  { .L2::cache_hint }"
              ),
              PTXVersion(8, 0),
              SMTarget(90),
              Col(InstructionForm(
                "cp.async.bulk.prefetch.tensor",
                Col(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("L2"), _root_.scala.collection.immutable.Set("global")),
                _root_.scala.collection.immutable.Set("L2::cache_hint", "im2col", "im2col::w", "tile", "tile::gather4"),
                false
              ))
            )
          ))
      )
  case CpAsyncBulkTensorMBarrier extends InstructionDef(
        "cp.async.bulk.tensor",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          val modifierNames = mods.map(_.name)
          if (
            !modifierNames.contains(
              "bulk_group"
            ) && (if (modifierNames.exists(_.startsWith("im2col")) && modifierNames.contains("multicast::cluster"))
                    operands.size >= 5 && operands.size <= 6
                  else if (modifierNames.exists(_.startsWith("im2col")) || modifierNames.contains("multicast::cluster"))
                    operands.size >= 4 && operands.size <= 5
                  else operands.size == 3 || operands.size == 4)
          ) Right(if (modifierNames.exists(_.startsWith("im2col")) && modifierNames.contains("multicast::cluster"))
            Instruction.CpAsyncBulkTensorMBarrier(
              operands(0),
              operands(1),
              operands(2),
              Some(operands(3)),
              operands.lift(4),
              operands.lift(5),
              mods,
              pred
            )
          else if (modifierNames.exists(_.startsWith("im2col"))) Instruction.CpAsyncBulkTensorMBarrier(
            operands(0),
            operands(1),
            operands(2),
            Some(operands(3)),
            None,
            operands.lift(4),
            mods,
            pred
          )
          else if (modifierNames.contains("multicast::cluster")) Instruction.CpAsyncBulkTensorMBarrier(
            operands(0),
            operands(1),
            operands(2),
            None,
            operands.lift(3),
            operands.lift(4),
            mods,
            pred
          )
          else Instruction.CpAsyncBulkTensorMBarrier(
            operands(0),
            operands(1),
            operands(2),
            None,
            None,
            operands.lift(3),
            mods,
            pred
          ))
          else if (!modifierNames.contains("bulk_group"))
            Left(InstructionDecodeError.InvalidOperandCount("cp.async.bulk.tensor", Col(3, 4, 5, 6), operands.size))
          else Left(InstructionDecodeError.UnknownOpcode((("cp.async.bulk.tensor") +: modifierNames).mkString(".")))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-cp-async-bulk-tensor",
              "9.7.9.26.5.2. Data Movement and Conversion Instructions: cp.async.bulk.tensor",
              "cp.async.bulk.tensor",
              "Data Movement and Conversion Instructions",
              Col(
                "// global -> shared::cta\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.cta_group}{.level::cache_hint}\n                                   [dstMem], [tensorMap, tensorCoords], [mbar]{, im2colInfo} {, cache_policy}\n\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.cta_group =            { .cta_group::1, .cta_group::2 }\n.load_mode =            { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =    { .L2::cache_hint }\n\n\n// global -> shared::cluster\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.multicast}{.cta_group}{.level::cache_hint}\n                                   [dstMem], [tensorMap, tensorCoords], [mbar]{, im2colInfo}\n                                   {, ctaMask} {, cache_policy}\n\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.cta_group =            { .cta_group::1, .cta_group::2 }\n.load_mode =            { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster  }\n\n\n// shared::cta -> global\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.level::cache_hint}\n                                   [tensorMap, tensorCoords], [srcMem] {, cache_policy}\n\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .bulk_group }\n.load_mode =            { .tile, .tile::scatter4, .im2col_no_offs }\n.level::cache_hint =    { .L2::cache_hint }"
              ),
              PTXVersion(8, 0),
              SMTarget(90),
              Col(
                InstructionForm(
                  "cp.async.bulk.tensor",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint", "im2col_no_offs", "tile", "tile::scatter4"),
                  false
                ),
                InstructionForm(
                  "cp.async.bulk.tensor",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group")
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L2::cache_hint",
                    "im2col_no_offs",
                    "multicast::cluster",
                    "tile",
                    "tile::scatter4"
                  ),
                  false
                ),
                InstructionForm(
                  "cp.async.bulk.tensor",
                  Col(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint", "im2col_no_offs", "tile", "tile::scatter4"),
                  false
                )
              )
            )
          ))
      )
  case CpAsyncBulkTensorGroup extends InstructionDef(
        "cp.async.bulk.tensor",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          val modifierNames = mods.map(_.name)
          if (modifierNames.contains("bulk_group") && (operands.size == 2 || operands.size == 3))
            Right(Instruction.CpAsyncBulkTensorGroup(operands(0), operands(1), operands.lift(2), mods, pred))
          else if (modifierNames.contains("bulk_group"))
            Left(InstructionDecodeError.InvalidOperandCount("cp.async.bulk.tensor", Col(2, 3), operands.size))
          else Left(InstructionDecodeError.UnknownOpcode((("cp.async.bulk.tensor") +: modifierNames).mkString(".")))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-cp-async-bulk-tensor",
              "9.7.9.26.5.2. Data Movement and Conversion Instructions: cp.async.bulk.tensor",
              "cp.async.bulk.tensor",
              "Data Movement and Conversion Instructions",
              Col(
                "// global -> shared::cta\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.cta_group}{.level::cache_hint}\n                                   [dstMem], [tensorMap, tensorCoords], [mbar]{, im2colInfo} {, cache_policy}\n\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.cta_group =            { .cta_group::1, .cta_group::2 }\n.load_mode =            { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =    { .L2::cache_hint }\n\n\n// global -> shared::cluster\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.multicast}{.cta_group}{.level::cache_hint}\n                                   [dstMem], [tensorMap, tensorCoords], [mbar]{, im2colInfo}\n                                   {, ctaMask} {, cache_policy}\n\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.cta_group =            { .cta_group::1, .cta_group::2 }\n.load_mode =            { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster  }\n\n\n// shared::cta -> global\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.level::cache_hint}\n                                   [tensorMap, tensorCoords], [srcMem] {, cache_policy}\n\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .bulk_group }\n.load_mode =            { .tile, .tile::scatter4, .im2col_no_offs }\n.level::cache_hint =    { .L2::cache_hint }"
              ),
              PTXVersion(8, 0),
              SMTarget(90),
              Col(
                InstructionForm(
                  "cp.async.bulk.tensor",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint", "im2col_no_offs", "tile", "tile::scatter4"),
                  false
                ),
                InstructionForm(
                  "cp.async.bulk.tensor",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group")
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L2::cache_hint",
                    "im2col_no_offs",
                    "multicast::cluster",
                    "tile",
                    "tile::scatter4"
                  ),
                  false
                ),
                InstructionForm(
                  "cp.async.bulk.tensor",
                  Col(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint", "im2col_no_offs", "tile", "tile::scatter4"),
                  false
                )
              )
            )
          ))
      )
  case CpAsyncBulkWaitGroup extends InstructionDef(
        "cp.async.bulk.wait_group",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 1) Right(Instruction.CpAsyncBulkWaitGroup(operands(0), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("cp.async.bulk.wait_group", Col(1), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-cp-async-bulk-wait-group",
              "9.7.9.26.6.2. Data Movement and Conversion Instructions: cp.async.bulk.wait_group",
              "cp.async.bulk.wait_group",
              "Data Movement and Conversion Instructions",
              Col("cp.async.bulk.wait_group{.read} N;"),
              PTXVersion(8, 0),
              SMTarget(90),
              Col(InstructionForm(
                "cp.async.bulk.wait_group",
                Col(OperandConstraint.Value),
                Col(),
                _root_.scala.collection.immutable.Set("read"),
                true
              ))
            )
          ))
      )
  case CpAsyncCommitGroup extends InstructionDef(
        "cp.async.commit_group",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 0) Right(Instruction.CpAsyncCommitGroup(mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("cp.async.commit_group", Col(0), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-cp-async-commit-group",
              "9.7.9.26.3.2. Data Movement and Conversion Instructions: cp.async.commit_group",
              "cp.async.commit_group",
              "Data Movement and Conversion Instructions",
              Col("cp.async.commit_group ;"),
              PTXVersion(7, 0),
              SMTarget(80),
              Col(InstructionForm("cp.async.commit_group", Col(), Col(), _root_.scala.collection.immutable.Set(), true))
            )
          ))
      )
  case CpAsyncMbarrierArrive extends InstructionDef(
        "cp.async.mbarrier.arrive",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 1) Right(Instruction.CpAsyncMbarrierArrive(operands(0), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("cp.async.mbarrier.arrive", Col(1), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-cp-async-mbarrier-arrive",
              "9.7.14.16.18. Parallel Synchronization and Communication Instructions: cp.async.mbarrier.arrive",
              "cp.async.mbarrier.arrive",
              "Parallel Synchronization and Communication Instructions",
              Col("cp.async.mbarrier.arrive{.noinc}{.shared{::cta}}.b64 [addr];"),
              PTXVersion(7, 0),
              SMTarget(80),
              Col(InstructionForm(
                "cp.async.mbarrier.arrive",
                Col(OperandConstraint.Address),
                Col(_root_.scala.collection.immutable.Set("b64")),
                _root_.scala.collection.immutable.Set("noinc", "shared"),
                false
              ))
            )
          ))
      )
  case CpAsyncWaitAll extends InstructionDef(
        "cp.async.wait_all",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 0) Right(Instruction.CpAsyncWaitAll(mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("cp.async.wait_all", Col(0), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-cp-async-wait-group",
              "9.7.9.26.3.3. Data Movement and Conversion Instructions: cp.async.wait_group / cp.async.wait_all",
              "cp.async.wait_group / cp.async.wait_all",
              "Data Movement and Conversion Instructions",
              Col("cp.async.wait_group N;\ncp.async.wait_all ;"),
              PTXVersion(7, 0),
              SMTarget(80),
              Col(InstructionForm("cp.async.wait_all", Col(), Col(), _root_.scala.collection.immutable.Set(), true))
            )
          ))
      )
  case CpAsyncWaitGroup extends InstructionDef(
        "cp.async.wait_group",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 1) Right(Instruction.CpAsyncWaitGroup(operands(0), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("cp.async.wait_group", Col(1), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-cp-async-wait-group",
              "9.7.9.26.3.3. Data Movement and Conversion Instructions: cp.async.wait_group / cp.async.wait_all",
              "cp.async.wait_group / cp.async.wait_all",
              "Data Movement and Conversion Instructions",
              Col("cp.async.wait_group N;\ncp.async.wait_all ;"),
              PTXVersion(7, 0),
              SMTarget(80),
              Col(InstructionForm(
                "cp.async.wait_group",
                Col(OperandConstraint.Value),
                Col(),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case CpReduceAsyncBulk extends InstructionDef(
        "cp.reduce.async.bulk",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3 || operands.size == 4)
            Right(Instruction.CpReduceAsyncBulk(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("cp.reduce.async.bulk", Col(3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-cp-reduce-async-bulk",
              "9.7.9.26.4.2. Data Movement and Conversion Instructions: cp.reduce.async.bulk",
              "cp.reduce.async.bulk",
              "Data Movement and Conversion Instructions",
              Col(
                "cp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism.redOp.type\n              [dstMem], [srcMem], size, [mbar]\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.redOp=                 { .and, .or, .xor,\n                          .add, .inc, .dec,\n                          .min, .max }\n.type =                 { .b32, .u32, .s32, .b64, .u64 }\n\n\ncp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism{.level::cache_hint}.redOp.type\n               [dstMem], [srcMem], size{, cache_policy}\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .global      }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint    = { .L2::cache_hint }\n.redOp=                 { .and, .or, .xor,\n                          .add, .inc, .dec,\n                          .min, .max }\n.type =                 { .f16, .bf16, .b32, .u32, .s32, .b64, .u64, .s64, .f32, .f64 }\n\n\ncp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism{.level::cache_hint}.add.noftz.type\n               [dstMem], [srcMem], size{, cache_policy}\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst  =                 { .global }\n.src  =                 { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.type =                 { .f16, .bf16 }"
              ),
              PTXVersion(8, 0),
              SMTarget(90),
              Col(
                InstructionForm(
                  "cp.reduce.async.bulk",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group"),
                    _root_.scala.collection.immutable.Set("add", "and", "dec", "inc", "max", "min", "or", "xor"),
                    _root_.scala.collection.immutable.Set("bf16", "f16")
                  ),
                  _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "relaxed", "sys"),
                  true
                ),
                InstructionForm(
                  "cp.reduce.async.bulk",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group"),
                    _root_.scala.collection.immutable.Set("add", "and", "dec", "inc", "max", "min", "or", "xor"),
                    _root_.scala.collection.immutable.Set("bf16", "f16")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint", "cluster", "cta", "gpu", "relaxed", "sys"),
                  true
                ),
                InstructionForm(
                  "cp.reduce.async.bulk",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group"),
                    _root_.scala.collection.immutable.Set("add"),
                    _root_.scala.collection.immutable.Set("noftz"),
                    _root_.scala.collection.immutable.Set("bf16", "f16")
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint", "cluster", "cta", "gpu", "relaxed", "sys"),
                  true
                )
              )
            )
          ))
      )
  case CpReduceAsyncBulkTensor extends InstructionDef(
        "cp.reduce.async.bulk.tensor",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2 || operands.size == 3)
            Right(Instruction.CpReduceAsyncBulkTensor(operands(0), operands(1), operands.lift(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("cp.reduce.async.bulk.tensor", Col(2, 3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-cp-reduce-async-bulk-tensor",
              "9.7.9.26.5.3. Data Movement and Conversion Instructions: cp.reduce.async.bulk.tensor",
              "cp.reduce.async.bulk.tensor",
              "Data Movement and Conversion Instructions",
              Col(
                "// shared::cta -> global:\ncp.reduce.async.bulk.tensor.dim.dst.src.redOp{.load_mode}.completion_mechanism{.level::cache_hint}\n                                          [tensorMap, tensorCoords], [srcMem] {,cache_policy}\n\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .bulk_group }\n.load_mode =            { .tile, .im2col_no_offs }\n.redOp =                { .add, .min, .max, .inc, .dec, .and, .or, .xor}"
              ),
              PTXVersion(8, 0),
              SMTarget(90),
              Col(InstructionForm(
                "cp.reduce.async.bulk.tensor",
                Col(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                Col(
                  _root_.scala.collection.immutable.Set("global"),
                  _root_.scala.collection.immutable.Set("shared::cta"),
                  _root_.scala.collection.immutable.Set("add", "and", "dec", "inc", "max", "min", "or", "xor"),
                  _root_.scala.collection.immutable.Set("bulk_group")
                ),
                _root_.scala.collection.immutable.Set("im2col_no_offs", "level::cache_hint", "tile"),
                true
              ))
            )
          ))
      )
  case CreatePolicy extends InstructionDef(
        "createpolicy",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 1 || operands.size == 2 || operands.size == 4) Right(Instruction.CreatePolicy(
            operands(0),
            operands.lift(1),
            operands.lift(2),
            operands.lift(3),
            mods,
            pred
          ))
          else Left(InstructionDecodeError.InvalidOperandCount("createpolicy", Col(1, 2, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-createpolicy",
              "9.7.9.19. Data Movement and Conversion Instructions: createpolicy",
              "createpolicy",
              "Data Movement and Conversion Instructions",
              Col(
                "// Range-based policy\ncreatepolicy.range{.global}.level::primary_priority{.level::secondary_priority}.b64\n                                   cache_policy, [a], primary-size, total-size;\n\n// Fraction-based policy\ncreatepolicy.fractional.level::primary_priority{.level::secondary_priority}.b64\n                                   cache_policy{, fraction};\n\n// Converting the access property from CUDA APIs\ncreatepolicy.cvt.L2.b64            cache_policy, access-property;\n\n.level::primary_priority =   { .L2::evict_last, .L2::evict_normal,\n                               .L2::evict_first, .L2::evict_unchanged };\n.level::secondary_priority = { .L2::evict_first, .L2::evict_unchanged };"
              ),
              PTXVersion(7, 4),
              SMTarget(80),
              Col(
                InstructionForm(
                  "createpolicy",
                  Col(
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("range"),
                    _root_.scala.collection.immutable.Set(
                      "L2::evict_first",
                      "L2::evict_last",
                      "L2::evict_normal",
                      "L2::evict_unchanged"
                    ),
                    _root_.scala.collection.immutable.Set("b64")
                  ),
                  _root_.scala.collection.immutable.Set("L2::evict_first", "L2::evict_unchanged", "global"),
                  true
                ),
                InstructionForm(
                  "createpolicy",
                  Col(OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("fractional"),
                    _root_.scala.collection.immutable.Set(
                      "L2::evict_first",
                      "L2::evict_last",
                      "L2::evict_normal",
                      "L2::evict_unchanged"
                    ),
                    _root_.scala.collection.immutable.Set("b64")
                  ),
                  _root_.scala.collection.immutable.Set("L2::evict_first", "L2::evict_unchanged"),
                  true
                ),
                InstructionForm(
                  "createpolicy",
                  Col(OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("cvt"),
                    _root_.scala.collection.immutable.Set("L2"),
                    _root_.scala.collection.immutable.Set("b64")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case Cvt extends InstructionDef(
        "cvt",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2 || operands.size == 3 || operands.size == 4)
            Right(Instruction.Cvt(operands(0), operands(1), operands.lift(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("cvt", Col(2, 3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-cvt",
              "9.7.9.22. Data Movement and Conversion Instructions: cvt",
              "cvt",
              "Data Movement and Conversion Instructions",
              Col(
                "cvt{.irnd}{.ftz}{.sat}.dtype.atype         d, a;  // integer rounding\ncvt{.frnd}{.ftz}{.sat}.dtype.atype         d, a;  // fp rounding\n\ncvt.frnd2{.relu}{.satfinite}.f16.f32       d, a;\ncvt.frnd2{.relu}{.satfinite}.f16x2.f32     d, a, b;\ncvt.rs{.relu}{.satfinite}.f16x2.f32        d, a, b, rbits;\n\ncvt.frnd2{.relu}{.satfinite}.bf16.f32      d, a;\ncvt.frnd2{.relu}{.satfinite}.bf16x2.f32    d, a, b;\ncvt.rs{.relu}{.satfinite}.bf16x2.f32       d, a, b, rbits;\n\ncvt.rna{.satfinite}.tf32.f32               d, a;\ncvt.frnd2{.satfinite}{.relu}.tf32.f32      d, a;\n\ncvt.rn.satfinite{.relu}.f8x2type.f32       d, a, b;\ncvt.rn.satfinite{.relu}.f8x2type.fp16x2    d, a;\ncvt.rn{.relu}.f16x2.f8x2type               d, a;\ncvt.rn{.relu}{.satfinite}{.scaled::n2::ue8m0}.bf16x2.f8x2type d, a{, scale-factor};\ncvt.rs{.relu}.satfinite.f8x4type.f32       d, {a, b, e, f}, rbits;\n\ncvt.rn.satfinite{.relu}.f4x2type.f32        d, a, b;\ncvt.rn.satfinite{.relu}.f4x2type.fp16x2type d, a;\ncvt.rn{.relu}.f16x2.f4x2type                d, a;\ncvt.rn{.relu}{.satfinite}{.scaled::n2::ue8m0}.bf16x2.f4x2type d, a{, scale-factor};\ncvt.rs{.relu}.satfinite.f4x4type.f32        d, {a, b, e, f}, rbits;\n\ncvt.rn.satfinite{.relu}.f6x2type.f32        d, a, b;\ncvt.rn.satfinite{.relu}.f6x2type.fp16x2type d, a;\ncvt.rn{.relu}.f16x2.f6x2type                d, a;\ncvt.rn{.relu}{.satfinite}{.scaled::n2::ue8m0}.bf16x2.f6x2type d, a{, scale-factor};\ncvt.rs{.relu}.satfinite.f6x4type.f32        d, {a, b, e, f}, rbits;\n\ncvt.frnd3{.satfinite}.ue8m0x2.f32          d, a, b;\ncvt.frnd3{.satfinite}.ue8m0x2.bf16x2       d, a;\ncvt.rn.bf16x2.ue8m0x2                      d, a;\n\ncvt.rn.satfinite{.relu}{.scaled::n2::ue8m0}.s2f6x2.f32      d, a, b{, scale-factor};\ncvt.rn.satfinite{.relu}{.scaled::n2::ue8m0}.s2f6x2.bf16x2   d, a{, scale-factor};\ncvt.rn{.satfinite}{.relu}{.scaled::n2::ue8m0}.bf16x2.s2f6x2 d, a{, scale-factor};\n\n.irnd   = { .rni, .rzi, .rmi, .rpi };\n.frnd   = { .rn,  .rz,  .rm,  .rp  };\n.frnd2  = { .rn,  .rz };\n.frnd3  = { .rz,  .rp };\n.dtype = .atype = { .u8,   .u16, .u32, .u64,\n                    .s8,   .s16, .s32, .s64,\n                    .bf16, .f16, .f32, .f64 };\n.f8x2type = { .e4m3x2, .e5m2x2 };\n.f4x2type = { .e2m1x2 };\n.f6x2type = { .e2m3x2, .e3m2x2 };\n.f4x4type = { .e2m1x4 };\n.f8x4type = { .e4m3x4, .e5m2x4 };\n.f6x4type = { .e2m3x4, .e3m2x4 };\n.fp16x2type = { .f16x2, .bf16x2 };"
              ),
              PTXVersion(1, 0),
              SMTarget(13),
              Col(
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set(
                      "bf16",
                      "f16",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "s8",
                      "u16",
                      "u32",
                      "u64",
                      "u8"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("ftz", "rmi", "rni", "rpi", "rzi", "sat"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set(
                      "bf16",
                      "f16",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "s8",
                      "u16",
                      "u32",
                      "u64",
                      "u8"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("ftz", "rm", "rn", "rp", "rz", "sat"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rn", "rz"),
                    _root_.scala.collection.immutable.Set("f16"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("relu", "satfinite"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rn", "rz"),
                    _root_.scala.collection.immutable.Set("f16x2"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("relu", "satfinite"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("rs"),
                    _root_.scala.collection.immutable.Set("f16x2"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("relu", "satfinite"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rn", "rz"),
                    _root_.scala.collection.immutable.Set("bf16"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("relu", "satfinite"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rn", "rz"),
                    _root_.scala.collection.immutable.Set("bf16x2"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("relu", "satfinite"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("rs"),
                    _root_.scala.collection.immutable.Set("bf16x2"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("relu", "satfinite"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rna"),
                    _root_.scala.collection.immutable.Set("tf32"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("satfinite"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rn", "rz"),
                    _root_.scala.collection.immutable.Set("tf32"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("relu", "satfinite"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rn"),
                    _root_.scala.collection.immutable.Set("satfinite"),
                    _root_.scala.collection.immutable.Set("e4m3x2", "e5m2x2"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("relu"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rn"),
                    _root_.scala.collection.immutable.Set("satfinite"),
                    _root_.scala.collection.immutable.Set("e4m3x2", "e5m2x2"),
                    _root_.scala.collection.immutable.Set("fp16x2")
                  ),
                  _root_.scala.collection.immutable.Set("relu"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rn"),
                    _root_.scala.collection.immutable.Set("f16x2"),
                    _root_.scala.collection.immutable.Set("e4m3x2", "e5m2x2")
                  ),
                  _root_.scala.collection.immutable.Set("relu"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rn"),
                    _root_.scala.collection.immutable.Set("bf16x2"),
                    _root_.scala.collection.immutable.Set("e4m3x2", "e5m2x2")
                  ),
                  _root_.scala.collection.immutable.Set("relu", "satfinite", "scaled::n2::ue8m0"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.VectorOperand, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rs"),
                    _root_.scala.collection.immutable.Set("satfinite"),
                    _root_.scala.collection.immutable.Set("e4m3x4", "e5m2x4"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("relu"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rn"),
                    _root_.scala.collection.immutable.Set("satfinite"),
                    _root_.scala.collection.immutable.Set("e2m1x2"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("relu"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rn"),
                    _root_.scala.collection.immutable.Set("satfinite"),
                    _root_.scala.collection.immutable.Set("e2m1x2"),
                    _root_.scala.collection.immutable.Set("bf16x2", "f16x2")
                  ),
                  _root_.scala.collection.immutable.Set("relu"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rn"),
                    _root_.scala.collection.immutable.Set("f16x2"),
                    _root_.scala.collection.immutable.Set("e2m1x2")
                  ),
                  _root_.scala.collection.immutable.Set("relu"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rn"),
                    _root_.scala.collection.immutable.Set("bf16x2"),
                    _root_.scala.collection.immutable.Set("e2m1x2")
                  ),
                  _root_.scala.collection.immutable.Set("relu", "satfinite", "scaled::n2::ue8m0"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.VectorOperand, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rs"),
                    _root_.scala.collection.immutable.Set("satfinite"),
                    _root_.scala.collection.immutable.Set("e2m1x4"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("relu"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rn"),
                    _root_.scala.collection.immutable.Set("satfinite"),
                    _root_.scala.collection.immutable.Set("e2m3x2", "e3m2x2"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("relu"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rn"),
                    _root_.scala.collection.immutable.Set("satfinite"),
                    _root_.scala.collection.immutable.Set("e2m3x2", "e3m2x2"),
                    _root_.scala.collection.immutable.Set("bf16x2", "f16x2")
                  ),
                  _root_.scala.collection.immutable.Set("relu"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rn"),
                    _root_.scala.collection.immutable.Set("f16x2"),
                    _root_.scala.collection.immutable.Set("e2m3x2", "e3m2x2")
                  ),
                  _root_.scala.collection.immutable.Set("relu"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rn"),
                    _root_.scala.collection.immutable.Set("bf16x2"),
                    _root_.scala.collection.immutable.Set("e2m3x2", "e3m2x2")
                  ),
                  _root_.scala.collection.immutable.Set("relu", "satfinite", "scaled::n2::ue8m0"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.VectorOperand, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rs"),
                    _root_.scala.collection.immutable.Set("satfinite"),
                    _root_.scala.collection.immutable.Set("e2m3x4", "e3m2x4"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("relu"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rp", "rz"),
                    _root_.scala.collection.immutable.Set("ue8m0x2"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("satfinite"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rp", "rz"),
                    _root_.scala.collection.immutable.Set("ue8m0x2"),
                    _root_.scala.collection.immutable.Set("bf16x2")
                  ),
                  _root_.scala.collection.immutable.Set("satfinite"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rn"),
                    _root_.scala.collection.immutable.Set("bf16x2"),
                    _root_.scala.collection.immutable.Set("ue8m0x2")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("rn"),
                    _root_.scala.collection.immutable.Set("satfinite"),
                    _root_.scala.collection.immutable.Set("s2f6x2"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("relu", "scaled::n2::ue8m0"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rn"),
                    _root_.scala.collection.immutable.Set("satfinite"),
                    _root_.scala.collection.immutable.Set("s2f6x2"),
                    _root_.scala.collection.immutable.Set("bf16x2")
                  ),
                  _root_.scala.collection.immutable.Set("relu", "scaled::n2::ue8m0"),
                  false
                ),
                InstructionForm(
                  "cvt",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rn"),
                    _root_.scala.collection.immutable.Set("bf16x2"),
                    _root_.scala.collection.immutable.Set("s2f6x2")
                  ),
                  _root_.scala.collection.immutable.Set("relu", "satfinite", "scaled::n2::ue8m0"),
                  false
                )
              )
            )
          ))
      )
  case CvtPack extends InstructionDef(
        "cvt.pack",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3 || operands.size == 4)
            Right(Instruction.CvtPack(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("cvt.pack", Col(3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-cvt-pack",
              "9.7.9.23. Data Movement and Conversion Instructions: cvt.pack",
              "cvt.pack",
              "Data Movement and Conversion Instructions",
              Col(
                "cvt.pack.sat.convertType.abType  d, a, b;\n    .convertType  = { .u16, .s16 }\n    .abType       = { .s32 }\n\ncvt.pack.sat.convertType.abType.cType  d, a, b, c;\n    .convertType  = { .u2, .s2, .u4, .s4, .u8, .s8 }\n    .abType       = { .s32 }\n    .cType        = { .b32 }"
              ),
              PTXVersion(6, 5),
              SMTarget(72),
              Col(
                InstructionForm(
                  "cvt.pack",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("sat"),
                    _root_.scala.collection.immutable.Set("s2", "s4", "s8", "u2", "u4", "u8"),
                    _root_.scala.collection.immutable.Set("s32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "cvt.pack",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("sat"),
                    _root_.scala.collection.immutable.Set("s2", "s4", "s8", "u2", "u4", "u8"),
                    _root_.scala.collection.immutable.Set("s32"),
                    _root_.scala.collection.immutable.Set("b32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case Cvta extends InstructionDef(
        "cvta",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Cvta(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("cvta", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-cvta",
              "9.7.9.21. Data Movement and Conversion Instructions: cvta",
              "cvta",
              "Data Movement and Conversion Instructions",
              Col(
                "// convert const, global, local, or shared address to generic address\ncvta.space.size  p, a;        // source address in register a\ncvta.space.size  p, var;      // get generic address of var\ncvta.space.size  p, var+imm;  // generic address of var+offset\n\n// convert generic address to const, global, local, or shared address\ncvta.to.space.size  p, a;\n\n.space = { .const, .global, .local, .shared{::cta, ::cluster}, .param{::entry} };\n.size  = { .u32, .u64 };"
              ),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(
                InstructionForm(
                  "cvta",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("const", "global", "local"),
                    _root_.scala.collection.immutable.Set("u32", "u64")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "cvta",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("const", "global", "local"),
                    _root_.scala.collection.immutable.Set("u32", "u64")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "cvta",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("const", "global", "local"),
                    _root_.scala.collection.immutable.Set("u32", "u64")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "cvta",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("to"),
                    _root_.scala.collection.immutable.Set("const", "global", "local"),
                    _root_.scala.collection.immutable.Set("u32", "u64")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case Discard extends InstructionDef(
        "discard",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Discard(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("discard", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-discard",
              "9.7.9.18. Data Movement and Conversion Instructions: discard",
              "discard",
              "Data Movement and Conversion Instructions",
              Col("discard{.global}.level  [a], size;\n\n.level = { .L2 };"),
              PTXVersion(7, 4),
              SMTarget(80),
              Col(InstructionForm(
                "discard",
                Col(OperandConstraint.Address, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("L2")),
                _root_.scala.collection.immutable.Set("global"),
                true
              ))
            )
          ))
      )
  case Div extends InstructionDef(
        "div",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.Div(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("div", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-div",
              "9.7.1.9. Integer Arithmetic Instructions: div",
              "div",
              "Integer Arithmetic Instructions",
              Col("div.type  d, a, b;\n\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm(
                "div",
                Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("s16", "s32", "s64", "u16", "u32", "u64")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            ),
            Metadata.Entry(
              "floating-point-instructions-div",
              "9.7.3.8. Floating Point Instructions: div",
              "div",
              "Floating Point Instructions",
              Col(
                "div.approx{.ftz}.f32  d, a, b;  // fast, approximate divide\ndiv.full{.ftz}.f32    d, a, b;  // full-range approximate divide\ndiv.rnd{.ftz}.f32     d, a, b;  // IEEE 754 compliant rounding\ndiv.rnd.f64           d, a, b;  // IEEE 754 compliant rounding\n\n.rnd = { .rn, .rz, .rm, .rp };"
              ),
              PTXVersion(1, 0),
              SMTarget(13),
              Col(
                InstructionForm(
                  "div",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("f32")),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "div",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("full"), _root_.scala.collection.immutable.Set("f32")),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "div",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "div",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"),
                    _root_.scala.collection.immutable.Set("f64")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case Dp2a extends InstructionDef(
        "dp2a",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Dp2a(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("dp2a", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-dp2a",
              "9.7.1.25. Integer Arithmetic Instructions: dp2a",
              "dp2a",
              "Integer Arithmetic Instructions",
              Col("dp2a.mode.atype.btype  d, a, b, c;\n\n.atype = .btype = { .u32, .s32 };\n.mode = { .lo, .hi };"),
              PTXVersion(5, 0),
              SMTarget(61),
              Col(InstructionForm(
                "dp2a",
                Col(
                  OperandConstraint.Register,
                  OperandConstraint.Value,
                  OperandConstraint.Value,
                  OperandConstraint.Value
                ),
                Col(
                  _root_.scala.collection.immutable.Set("hi", "lo"),
                  _root_.scala.collection.immutable.Set("atype"),
                  _root_.scala.collection.immutable.Set("s32", "u32")
                ),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Dp4a extends InstructionDef(
        "dp4a",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Dp4a(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("dp4a", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-dp4a",
              "9.7.1.24. Integer Arithmetic Instructions: dp4a",
              "dp4a",
              "Integer Arithmetic Instructions",
              Col("dp4a.atype.btype  d, a, b, c;\n\n.atype = .btype = { .u32, .s32 };"),
              PTXVersion(5, 0),
              SMTarget(61),
              Col(InstructionForm(
                "dp4a",
                Col(
                  OperandConstraint.Register,
                  OperandConstraint.Value,
                  OperandConstraint.Value,
                  OperandConstraint.Value
                ),
                Col(
                  _root_.scala.collection.immutable.Set("atype"),
                  _root_.scala.collection.immutable.Set("s32", "u32")
                ),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case ElectSync extends InstructionDef(
        "elect.sync",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.ElectSync(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("elect.sync", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-elect-sync",
              "9.7.14.15. Parallel Synchronization and Communication Instructions: elect.sync",
              "elect.sync",
              "Parallel Synchronization and Communication Instructions",
              Col("elect.sync d|p, membermask;"),
              PTXVersion(8, 0),
              SMTarget(90),
              Col(InstructionForm(
                "elect.sync",
                Col(OperandConstraint.Register, OperandConstraint.Value),
                Col(),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Ex2 extends InstructionDef(
        "ex2",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Ex2(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("ex2", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "floating-point-instructions-ex2",
              "9.7.3.21. Floating Point Instructions: ex2",
              "ex2",
              "Floating Point Instructions",
              Col("ex2.approx{.ftz}.f32  d, a;"),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm(
                "ex2",
                Col(OperandConstraint.Register, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("f32")),
                _root_.scala.collection.immutable.Set("ftz"),
                true
              ))
            ),
            Metadata.Entry(
              "half-precision-floating-point-instructions-ex2",
              "9.7.4.10. Half Precision Floating Point Instructions: ex2",
              "ex2",
              "Half Precision Floating Point Instructions",
              Col(
                "ex2.approx.atype     d, a;\nex2.approx.ftz.btype d, a;\n\n.atype = { .f16,  .f16x2}\n.btype = { .bf16, .bf16x2}"
              ),
              PTXVersion(7, 0),
              SMTarget(75),
              Col(
                InstructionForm(
                  "ex2",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("approx"),
                    _root_.scala.collection.immutable.Set("f16", "f16x2")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "ex2",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("approx"),
                    _root_.scala.collection.immutable.Set("ftz"),
                    _root_.scala.collection.immutable.Set("bf16", "bf16x2")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case Exit extends InstructionDef(
        "exit",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 0) Right(Instruction.Exit(mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("exit", Col(0), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "control-flow-instructions-exit",
              "9.7.13.7. Control Flow Instructions: exit",
              "exit",
              "Control Flow Instructions",
              Col("exit;"),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm("exit", Col(), Col(), _root_.scala.collection.immutable.Set(), true))
            )
          ))
      )
  case FabricSubmit extends InstructionDef(
        "fabric.submit",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 0) Right(Instruction.FabricSubmit(mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("fabric.submit", Col(0), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "fabric-instructions-submit",
              "9.7.10.5.5. Fabric Instructions: fabric.submit",
              "fabric.submit",
              "Fabric Instructions",
              Col("fabric.submit{.submitop};\n\n.submitop = { .op_restrict::fetching }"),
              PTXVersion(9, 3),
              SMTarget(100),
              Col(InstructionForm(
                "fabric.submit",
                Col(),
                Col(),
                _root_.scala.collection.immutable.Set("op_restrict::fetching"),
                true
              ))
            )
          ))
      )
  case FabricTryGet extends InstructionDef(
        "fabric.try_get",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.FabricTryGet(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("fabric.try_get", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "fabric-instructions-try-get",
              "9.7.10.5.1. Fabric Instructions: fabric.try_get",
              "fabric.try_get",
              "Fabric Instructions",
              Col(
                "fabric.try_get.async.dst.completion_mechanism.sem.scope.b128 [dst], [srcLeId, srcDataOff], size, [bar];\n\n.dst = { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes.mbarrier::report::fabric }\n.sem = { .relaxed }\n.scope = { .sys }"
              ),
              PTXVersion(9, 3),
              SMTarget(100),
              Col(InstructionForm(
                "fabric.try_get",
                Col(
                  OperandConstraint.Address,
                  OperandConstraint.Address,
                  OperandConstraint.Value,
                  OperandConstraint.Address
                ),
                Col(
                  _root_.scala.collection.immutable.Set("async"),
                  _root_.scala.collection.immutable.Set("shared::cta"),
                  _root_.scala.collection.immutable.Set("relaxed"),
                  _root_.scala.collection.immutable.Set("sys"),
                  _root_.scala.collection.immutable.Set("b128")
                ),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case FabricTryPullred extends InstructionDef(
        "fabric.try_pullred",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 5) Right(Instruction.FabricTryPullred(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            operands(4),
            mods,
            pred
          ))
          else Left(InstructionDecodeError.InvalidOperandCount("fabric.try_pullred", Col(5), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "fabric-instructions-try-pullred",
              "9.7.10.5.4. Fabric Instructions: fabric.try_pullred",
              "fabric.try_pullred",
              "Fabric Instructions",
              Col(
                "fabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpBit.typeBit.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpMinMax.typeMinMax.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpAdd.typeAdd.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpAddAcc16.typeAddAcc16.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpAddAcc32.typeAddAcc32.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\n.dst = { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes.mbarrier::report::fabric }\n.sem = { .relaxed }\n.scope = { .sys }\n.redOpBit = { .and, .xor, .or }\n.typeBit = { .b32, .b64 }\n.redOpMinMax = { .min, .max }\n.typeMinMax = { .u32, .s32, .u64, .s64, .bf16, .f16, .e4m3, .e5m2 }\n.redOpAdd = { .add }\n.typeAdd = { .u32, .u64, .f16, .bf16, .f32 }\n.redOpAddAcc16 = { .add.acc::f16 }\n.typeAddAcc16 = { .e4m3, .e5m2 }\n.redOpAddAcc32 = { .add.acc::f32 }\n.typeAddAcc32 = { .f16, .bf16 }"
              ),
              PTXVersion(9, 3),
              SMTarget(100),
              Col(
                InstructionForm(
                  "fabric.try_pullred",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("async"),
                    _root_.scala.collection.immutable.Set("multimem"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("sys"),
                    _root_.scala.collection.immutable.Set("and", "or", "xor"),
                    _root_.scala.collection.immutable.Set("b32", "b64"),
                    _root_.scala.collection.immutable.Set("sync")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "fabric.try_pullred",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("async"),
                    _root_.scala.collection.immutable.Set("multimem"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("sys"),
                    _root_.scala.collection.immutable.Set("max", "min"),
                    _root_.scala.collection.immutable.Set("bf16", "e4m3", "e5m2", "f16", "s32", "s64", "u32", "u64"),
                    _root_.scala.collection.immutable.Set("sync")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "fabric.try_pullred",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("async"),
                    _root_.scala.collection.immutable.Set("multimem"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("sys"),
                    _root_.scala.collection.immutable.Set("add"),
                    _root_.scala.collection.immutable.Set("bf16", "f16", "f32", "u32", "u64"),
                    _root_.scala.collection.immutable.Set("sync")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "fabric.try_pullred",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("async"),
                    _root_.scala.collection.immutable.Set("multimem"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("sys"),
                    _root_.scala.collection.immutable.Set("e4m3", "e5m2"),
                    _root_.scala.collection.immutable.Set("sync")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "fabric.try_pullred",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("async"),
                    _root_.scala.collection.immutable.Set("multimem"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("sys"),
                    _root_.scala.collection.immutable.Set("bf16", "f16"),
                    _root_.scala.collection.immutable.Set("sync")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case FabricTryPut extends InstructionDef(
        "fabric.try_put",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4 || operands.size == 5) Right(Instruction.FabricTryPut(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            operands.lift(4),
            mods,
            pred
          ))
          else Left(InstructionDecodeError.InvalidOperandCount("fabric.try_put", Col(4, 5), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "fabric-instructions-try-put",
              "9.7.10.5.2. Fabric Instructions: fabric.try_put",
              "fabric.try_put",
              "Fabric Instructions",
              Col(
                "fabric.try_put.async{.multimem}.src.completion_mechanism0.sem.scope.b128 [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_put.async{.multimem}.src.completion_mechanism0.cp_mask.sem.scope.b128 [dstLeId, dstDataOff], [src], size, [bar], bytemask;\n\nfabric.try_put.async{.multimem}.src.completion_mechanism1.sem.scope.b128 [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\n.src = { .shared::cta }\n.completion_mechanism0 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric }\n.completion_mechanism1 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric.counted::bytes }\n.sem = { .relaxed }\n.scope = { .sys }"
              ),
              PTXVersion(9, 3),
              SMTarget(100),
              Col(
                InstructionForm(
                  "fabric.try_put",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("async"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("sys"),
                    _root_.scala.collection.immutable.Set("b128")
                  ),
                  _root_.scala.collection.immutable.Set("multimem"),
                  true
                ),
                InstructionForm(
                  "fabric.try_put",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("async"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("cp_mask"),
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("sys"),
                    _root_.scala.collection.immutable.Set("b128")
                  ),
                  _root_.scala.collection.immutable.Set("multimem"),
                  true
                ),
                InstructionForm(
                  "fabric.try_put",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("async"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("sys"),
                    _root_.scala.collection.immutable.Set("b128")
                  ),
                  _root_.scala.collection.immutable.Set("multimem"),
                  true
                )
              )
            )
          ))
      )
  case FabricTryRed extends InstructionDef(
        "fabric.try_red",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.FabricTryRed(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("fabric.try_red", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "fabric-instructions-try-red",
              "9.7.10.5.3. Fabric Instructions: fabric.try_red",
              "fabric.try_red",
              "Fabric Instructions",
              Col(
                "fabric.try_red.async{.multimem}.src.completion_mechanism0.sem.scope.redOpBit.typeBit [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism0.sem.scope.redOpMinMax.typeMinMax [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism0.sem.scope.redOpArith.typeArith [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism1.sem.scope.redOpBit.typeBit [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism1.sem.scope.redOpMinMax.typeMinMax [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism1.sem.scope.redOpAdd.typeAdd [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\n.src = { .shared::cta }\n.completion_mechanism0 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric }\n.completion_mechanism1 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric.counted::bytes }\n.sem = { .relaxed }\n.scope = { .sys }\n.redOpBit = { .and, .xor, .or }\n.typeBit = { .b32, .b64 }\n.redOpMinMax = { .min, .max }\n.typeMinMax = { .u32, .s32, .u64, .s64, .f16, .bf16 }\n.redOpArith = { .add }\n.typeArith = { .u32, .u64, .f16, .bf16, .f32, .f64 }\n.redOpAdd = { .add }\n.typeAdd = { .u32, .u64, .f16, .bf16, .f32, .f64 }"
              ),
              PTXVersion(9, 3),
              SMTarget(100),
              Col(
                InstructionForm(
                  "fabric.try_red",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("async"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("sys"),
                    _root_.scala.collection.immutable.Set("and", "or", "xor"),
                    _root_.scala.collection.immutable.Set("b32", "b64")
                  ),
                  _root_.scala.collection.immutable.Set("multimem"),
                  true
                ),
                InstructionForm(
                  "fabric.try_red",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("async"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("sys"),
                    _root_.scala.collection.immutable.Set("max", "min"),
                    _root_.scala.collection.immutable.Set("bf16", "f16", "s32", "s64", "u32", "u64")
                  ),
                  _root_.scala.collection.immutable.Set("multimem"),
                  true
                ),
                InstructionForm(
                  "fabric.try_red",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("async"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("sys"),
                    _root_.scala.collection.immutable.Set("add"),
                    _root_.scala.collection.immutable.Set("bf16", "f16", "f32", "f64", "u32", "u64")
                  ),
                  _root_.scala.collection.immutable.Set("multimem"),
                  true
                ),
                InstructionForm(
                  "fabric.try_red",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("async"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("sys"),
                    _root_.scala.collection.immutable.Set("and", "or", "xor"),
                    _root_.scala.collection.immutable.Set("b32", "b64")
                  ),
                  _root_.scala.collection.immutable.Set("multimem"),
                  true
                ),
                InstructionForm(
                  "fabric.try_red",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("async"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("sys"),
                    _root_.scala.collection.immutable.Set("max", "min"),
                    _root_.scala.collection.immutable.Set("bf16", "f16", "s32", "s64", "u32", "u64")
                  ),
                  _root_.scala.collection.immutable.Set("multimem"),
                  true
                ),
                InstructionForm(
                  "fabric.try_red",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("async"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("sys"),
                    _root_.scala.collection.immutable.Set("add"),
                    _root_.scala.collection.immutable.Set("bf16", "f16", "f32", "f64", "u32", "u64")
                  ),
                  _root_.scala.collection.immutable.Set("multimem"),
                  true
                )
              )
            )
          ))
      )
  case FabricWait extends InstructionDef(
        "fabric.wait",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 0) Right(Instruction.FabricWait(mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("fabric.wait", Col(0), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "fabric-instructions-wait",
              "9.7.10.5.6. Fabric Instructions: fabric.wait",
              "fabric.wait",
              "Fabric Instructions",
              Col("fabric.wait.sync_restrict::reads;"),
              PTXVersion(9, 3),
              SMTarget(100),
              Col(InstructionForm(
                "fabric.wait",
                Col(),
                Col(_root_.scala.collection.immutable.Set("sync_restrict::reads")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Fence extends InstructionDef(
        "fence",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 0 || operands.size == 2)
            Right(Instruction.Fence(operands.lift(0), operands.lift(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("fence", Col(0, 2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-membar",
              "9.7.14.4. Parallel Synchronization and Communication Instructions: membar / fence",
              "membar / fence",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "// Thread fence:\nfence{.sem}.scope;\n\n// Thread fence:\nfence.acquire.sync_restrict::shared::cluster.cluster;\nfence.release.sync_restrict::shared::cta.cluster;\n\n// Operation fence:\nfence.op_restrict.release.cluster;\n\n// Proxy fence (bi-directional):\nfence.proxy.proxykind;\n\n// Proxy fence (uni-directional):\nfence.proxy.to_proxykind::from_proxykind.release.scope;\nfence.proxy.to_proxykind::from_proxykind.acquire.scope  [addr], size;\nfence.proxy.async::generic.acquire.sync_restrict::shared::cluster.cluster;\nfence.proxy.async::generic.release.sync_restrict::shared::cta.cluster;\nfence.proxy.to_proxykind::from_proxykind_fabric.alias.sem_fabric.sys;\n\n// Old style membar:\nmembar.level;\nmembar.proxy.proxykind;\n\n.sem       = { .sc, .acq_rel, .acquire, .release };\n.scope     = { .cta, .cluster, .gpu, .sys };\n.level     = { .cta, .gl, .sys };\n.proxykind = { .alias, .async, .async.global, .async.shared::{cta, cluster} };\n.op_restrict = { .mbarrier_init };\n.to_proxykind::from_proxykind = {.tensormap::generic};\n.to_proxykind::from_proxykind_fabric = {.generic::fabric, .fabric::generic, .fabric::fabric };\n.sem_fabric = { .acquire, .release };"
              ),
              PTXVersion(1, 0),
              SMTarget(20),
              Col(
                InstructionForm(
                  "fence",
                  Col(),
                  Col(_root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys")),
                  _root_.scala.collection.immutable.Set("acq_rel", "acquire", "release", "sc"),
                  false
                ),
                InstructionForm(
                  "fence",
                  Col(),
                  Col(
                    _root_.scala.collection.immutable.Set("acquire"),
                    _root_.scala.collection.immutable.Set("sync_restrict::shared::cluster"),
                    _root_.scala.collection.immutable.Set("cluster")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "fence",
                  Col(),
                  Col(
                    _root_.scala.collection.immutable.Set("release"),
                    _root_.scala.collection.immutable.Set("sync_restrict::shared::cta"),
                    _root_.scala.collection.immutable.Set("cluster")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "fence",
                  Col(),
                  Col(
                    _root_.scala.collection.immutable.Set("mbarrier_init"),
                    _root_.scala.collection.immutable.Set("release"),
                    _root_.scala.collection.immutable.Set("cluster")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "fence",
                  Col(),
                  Col(
                    _root_.scala.collection.immutable.Set("proxy"),
                    _root_.scala.collection.immutable.Set("alias", "async", "cluster")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "fence",
                  Col(),
                  Col(
                    _root_.scala.collection.immutable.Set("proxy"),
                    _root_.scala.collection.immutable.Set("tensormap::generic"),
                    _root_.scala.collection.immutable.Set("release"),
                    _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "fence",
                  Col(OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("proxy"),
                    _root_.scala.collection.immutable.Set("tensormap::generic"),
                    _root_.scala.collection.immutable.Set("acquire"),
                    _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "fence",
                  Col(),
                  Col(
                    _root_.scala.collection.immutable.Set("proxy"),
                    _root_.scala.collection.immutable.Set("async::generic"),
                    _root_.scala.collection.immutable.Set("acquire"),
                    _root_.scala.collection.immutable.Set("sync_restrict::shared::cluster"),
                    _root_.scala.collection.immutable.Set("cluster")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "fence",
                  Col(),
                  Col(
                    _root_.scala.collection.immutable.Set("proxy"),
                    _root_.scala.collection.immutable.Set("async::generic"),
                    _root_.scala.collection.immutable.Set("release"),
                    _root_.scala.collection.immutable.Set("sync_restrict::shared::cta"),
                    _root_.scala.collection.immutable.Set("cluster")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "fence",
                  Col(),
                  Col(
                    _root_.scala.collection.immutable.Set("proxy"),
                    _root_.scala.collection.immutable.Set("fabric::fabric", "fabric::generic", "generic::fabric"),
                    _root_.scala.collection.immutable.Set("alias"),
                    _root_.scala.collection.immutable.Set("acquire", "release"),
                    _root_.scala.collection.immutable.Set("sys")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case Fma extends InstructionDef(
        "fma",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4) Right(Instruction.Fma(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("fma", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "floating-point-instructions-fma",
              "9.7.3.6. Floating Point Instructions: fma",
              "fma",
              "Floating Point Instructions",
              Col(
                "fma.rnd{.ftz}{.sat}.f32  d, a, b, c;\nfma.rnd{.ftz}.f32x2      d, a, b, c;\nfma.rnd.f64              d, a, b, c;\n\n.rnd = { .rn, .rz, .rm, .rp };"
              ),
              PTXVersion(1, 0),
              SMTarget(13),
              Col(
                InstructionForm(
                  "fma",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("ftz", "sat"),
                  false
                ),
                InstructionForm(
                  "fma",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"),
                    _root_.scala.collection.immutable.Set("f32x2")
                  ),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "fma",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"),
                    _root_.scala.collection.immutable.Set("f64")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            ),
            Metadata.Entry(
              "half-precision-floating-point-instructions-fma",
              "9.7.4.4. Half Precision Floating Point Instructions: fma",
              "fma",
              "Half Precision Floating Point Instructions",
              Col(
                "fma.rnd{.ftz}{.sat}.f16     d, a, b, c;\nfma.rnd{.ftz}{.sat}.f16x2   d, a, b, c;\nfma.rnd{.ftz}.relu.f16      d, a, b, c;\nfma.rnd{.ftz}.relu.f16x2    d, a, b, c;\nfma.rnd{.relu}.bf16         d, a, b, c;\nfma.rnd{.relu}.bf16x2       d, a, b, c;\nfma.rnd.oob.{relu}.type     d, a, b, c;\n\n.rnd = { .rn };"
              ),
              PTXVersion(4, 2),
              SMTarget(53),
              Col(
                InstructionForm(
                  "fma",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("f16")),
                  _root_.scala.collection.immutable.Set("ftz", "sat"),
                  false
                ),
                InstructionForm(
                  "fma",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("f16x2")),
                  _root_.scala.collection.immutable.Set("ftz", "sat"),
                  false
                ),
                InstructionForm(
                  "fma",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("rn"),
                    _root_.scala.collection.immutable.Set("relu"),
                    _root_.scala.collection.immutable.Set("f16")
                  ),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "fma",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("rn"),
                    _root_.scala.collection.immutable.Set("relu"),
                    _root_.scala.collection.immutable.Set("f16x2")
                  ),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "fma",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("bf16")),
                  _root_.scala.collection.immutable.Set("relu"),
                  false
                ),
                InstructionForm(
                  "fma",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("bf16x2")),
                  _root_.scala.collection.immutable.Set("relu"),
                  false
                ),
                InstructionForm(
                  "fma",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("rn"),
                    _root_.scala.collection.immutable.Set("oob"),
                    _root_.scala.collection.immutable.Set("type")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            ),
            Metadata.Entry(
              "mixed-precision-floating-point-instructions-fma",
              "9.7.5.3. Mixed Precision Floating Point Instructions: fma",
              "fma",
              "Mixed Precision Floating Point Instructions",
              Col(
                "fma.rnd{.sat}.f32.abtype  d, a, b, c;\n\n.abtype = { .f16, .bf16};\n.rnd    = { .rn, .rz, .rm, .rp };"
              ),
              PTXVersion(1, 0),
              SMTarget(100),
              Col(InstructionForm(
                "fma",
                Col(
                  OperandConstraint.Register,
                  OperandConstraint.Value,
                  OperandConstraint.Value,
                  OperandConstraint.Value
                ),
                Col(
                  _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"),
                  _root_.scala.collection.immutable.Set("f32"),
                  _root_.scala.collection.immutable.Set("bf16", "f16")
                ),
                _root_.scala.collection.immutable.Set("sat"),
                true
              ))
            )
          ))
      )
  case Fns extends InstructionDef(
        "fns",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4) Right(Instruction.Fns(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("fns", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-fns",
              "9.7.1.18. Integer Arithmetic Instructions: fns",
              "fns",
              "Integer Arithmetic Instructions",
              Col("fns.b32 d, mask, base, offset;"),
              PTXVersion(6, 0),
              SMTarget(30),
              Col(InstructionForm(
                "fns",
                Col(
                  OperandConstraint.Register,
                  OperandConstraint.Value,
                  OperandConstraint.Value,
                  OperandConstraint.Value
                ),
                Col(_root_.scala.collection.immutable.Set("b32")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case GetCtaRank extends InstructionDef(
        "getctarank",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.GetCtaRank(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("getctarank", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-getctarank",
              "9.7.9.25. Data Movement and Conversion Instructions: getctarank",
              "getctarank",
              "Data Movement and Conversion Instructions",
              Col(
                "getctarank{.space}.type d, a;\n\n// Get cta rank from source shared memory address in register a.\ngetctarank.shared::cluster.type d, a;\n\n// Get cta rank from shared memory variable.\ngetctarank.shared::cluster.type d, var;\n\n// Get cta rank from shared memory variable+offset.\ngetctarank.shared::cluster.type d, var + imm;\n\n// Get cta rank from generic address of shared memory variable in register a.\ngetctarank.type d, a;\n\n.space = { .shared::cluster }\n.type  = { .u32, .u64 }"
              ),
              PTXVersion(7, 8),
              SMTarget(90),
              Col(
                InstructionForm(
                  "getctarank",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("u32", "u64")),
                  _root_.scala.collection.immutable.Set("shared::cluster"),
                  true
                ),
                InstructionForm(
                  "getctarank",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("shared::cluster"),
                    _root_.scala.collection.immutable.Set("u32", "u64")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ),
                InstructionForm(
                  "getctarank",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("shared::cluster"),
                    _root_.scala.collection.immutable.Set("u32", "u64")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ),
                InstructionForm(
                  "getctarank",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("shared::cluster"),
                    _root_.scala.collection.immutable.Set("u32", "u64")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ),
                InstructionForm(
                  "getctarank",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("u32", "u64")),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case GridDependencyControl extends InstructionDef(
        "griddepcontrol",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 0) Right(Instruction.GridDependencyControl(mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("griddepcontrol", Col(0), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-griddepcontrol",
              "9.7.14.14. Parallel Synchronization and Communication Instructions: griddepcontrol",
              "griddepcontrol",
              "Parallel Synchronization and Communication Instructions",
              Col("griddepcontrol.action;\n\n.action   = { .launch_dependents, .wait }"),
              PTXVersion(7, 8),
              SMTarget(90),
              Col(InstructionForm(
                "griddepcontrol",
                Col(),
                Col(_root_.scala.collection.immutable.Set("launch_dependents", "wait")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case IsSpacePredicate extends InstructionDef(
        "isspacep",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.IsSpacePredicate(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("isspacep", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-isspacep",
              "9.7.9.20. Data Movement and Conversion Instructions: isspacep",
              "isspacep",
              "Data Movement and Conversion Instructions",
              Col(
                "isspacep.space  p, a;    // result is .pred\n\n.space = { const, .global, .local, .shared{::cta, ::cluster}, .param{::entry} };"
              ),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(InstructionForm(
                "isspacep",
                Col(OperandConstraint.Register, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("const", "global", "local")),
                _root_.scala.collection.immutable.Set(),
                false
              ))
            )
          ))
      )
  case IsTypePredicate extends InstructionDef(
        "istypep",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.IsTypePredicate(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("istypep", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "texture-instructions-istypep",
              "9.7.11.6. Texture Instructions: istypep",
              "istypep",
              "Texture Instructions",
              Col("istypep.type   p, a;  // result is .pred\n\n.type = { .texref, .samplerref, .surfref };"),
              PTXVersion(4, 0),
              SMTarget(30),
              Col(InstructionForm(
                "istypep",
                Col(OperandConstraint.Register, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("samplerref", "surfref", "texref")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Ld extends InstructionDef(
        "ld",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2 || operands.size == 3)
            Right(Instruction.Ld(operands(0), operands(1), operands.lift(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("ld", Col(2, 3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-ld",
              "9.7.9.8. Data Movement and Conversion Instructions: ld",
              "ld",
              "Data Movement and Conversion Instructions",
              Col(
                "ld{.weak}{.ss}{.cop}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{.unified}{, cache_policy};\n\nld{.weak}{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{.unified}{, cache_policy};\n\nld.volatile{.ss}{.level::prefetch_size}{.vec}.type  d, [a];\n\nld.relaxed.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{, cache_policy};\n\nld.acquire.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{, cache_policy};\n\nld.mmio.sem.sys{.global}.type  d, [a];\n\n.ss =                       { .const, .global, .local, .param{::entry, ::func}, .shared{::cta, ::cluster} };\n.cop =                      { .ca, .cg, .cs, .lu, .cv };\n.sem =                      { .acquire, .relaxed };\n.level1::eviction_priority = { .L1::evict_normal, .L1::evict_unchanged,\n                               .L1::evict_first, .L1::evict_last, .L1::no_allocate };\n.level2::eviction_priority = {.L2::evict_normal, .L2::evict_first, .L2::evict_last};\n.level::cache_hint =        { .L2::cache_hint };\n.level::prefetch_size =     { .L2::64B, .L2::128B, .L2::256B }\n.scope =                    { .cta, .cluster, .gpu, .sys };\n.vec =                      { .v2, .v4, .v8 };\n.type =                     { .b8, .b16, .b32, .b64, .b128,\n                              .u8, .u16, .u32, .u64,\n                              .s8, .s16, .s32, .s64,\n                              .f32, .f64 };"
              ),
              PTXVersion(1, 0),
              SMTarget(13),
              Col(
                InstructionForm(
                  "ld",
                  Col(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set(
                    "b128",
                    "b16",
                    "b32",
                    "b64",
                    "b8",
                    "f32",
                    "f64",
                    "s16",
                    "s32",
                    "s64",
                    "s8",
                    "u16",
                    "u32",
                    "u64",
                    "u8"
                  )),
                  _root_.scala.collection.immutable.Set(
                    "L2::cache_hint",
                    "ca",
                    "cg",
                    "const",
                    "cs",
                    "cv",
                    "global",
                    "local",
                    "lu",
                    "v2",
                    "v4",
                    "v8",
                    "weak"
                  ),
                  false
                ),
                InstructionForm(
                  "ld",
                  Col(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set(
                    "b128",
                    "b16",
                    "b32",
                    "b64",
                    "b8",
                    "f32",
                    "f64",
                    "s16",
                    "s32",
                    "s64",
                    "s8",
                    "u16",
                    "u32",
                    "u64",
                    "u8"
                  )),
                  _root_.scala.collection.immutable.Set(
                    "L1::evict_first",
                    "L1::evict_last",
                    "L1::evict_normal",
                    "L1::evict_unchanged",
                    "L1::no_allocate",
                    "L2::cache_hint",
                    "L2::evict_first",
                    "L2::evict_last",
                    "L2::evict_normal",
                    "const",
                    "global",
                    "local",
                    "v2",
                    "v4",
                    "v8",
                    "weak"
                  ),
                  false
                ),
                InstructionForm(
                  "ld",
                  Col(OperandConstraint.Register, OperandConstraint.Address),
                  Col(
                    _root_.scala.collection.immutable.Set("volatile"),
                    _root_.scala.collection.immutable.Set(
                      "b128",
                      "b16",
                      "b32",
                      "b64",
                      "b8",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "s8",
                      "u16",
                      "u32",
                      "u64",
                      "u8"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("const", "global", "local", "v2", "v4", "v8"),
                  false
                ),
                InstructionForm(
                  "ld",
                  Col(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"),
                    _root_.scala.collection.immutable.Set(
                      "b128",
                      "b16",
                      "b32",
                      "b64",
                      "b8",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "s8",
                      "u16",
                      "u32",
                      "u64",
                      "u8"
                    )
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L1::evict_first",
                    "L1::evict_last",
                    "L1::evict_normal",
                    "L1::evict_unchanged",
                    "L1::no_allocate",
                    "L2::cache_hint",
                    "L2::evict_first",
                    "L2::evict_last",
                    "L2::evict_normal",
                    "const",
                    "global",
                    "local",
                    "v2",
                    "v4",
                    "v8"
                  ),
                  false
                ),
                InstructionForm(
                  "ld",
                  Col(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("acquire"),
                    _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"),
                    _root_.scala.collection.immutable.Set(
                      "b128",
                      "b16",
                      "b32",
                      "b64",
                      "b8",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "s8",
                      "u16",
                      "u32",
                      "u64",
                      "u8"
                    )
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L1::evict_first",
                    "L1::evict_last",
                    "L1::evict_normal",
                    "L1::evict_unchanged",
                    "L1::no_allocate",
                    "L2::cache_hint",
                    "L2::evict_first",
                    "L2::evict_last",
                    "L2::evict_normal",
                    "const",
                    "global",
                    "local",
                    "v2",
                    "v4",
                    "v8"
                  ),
                  false
                ),
                InstructionForm(
                  "ld",
                  Col(OperandConstraint.Register, OperandConstraint.Address),
                  Col(
                    _root_.scala.collection.immutable.Set("mmio"),
                    _root_.scala.collection.immutable.Set("acquire", "relaxed"),
                    _root_.scala.collection.immutable.Set("sys"),
                    _root_.scala.collection.immutable.Set(
                      "b128",
                      "b16",
                      "b32",
                      "b64",
                      "b8",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "s8",
                      "u16",
                      "u32",
                      "u64",
                      "u8"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("global"),
                  false
                )
              )
            )
          ))
      )
  case LdGlobalNc extends InstructionDef(
        "ld.global.nc",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2 || operands.size == 3)
            Right(Instruction.LdGlobalNc(operands(0), operands(1), operands.lift(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("ld.global.nc", Col(2, 3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-ld-global-nc",
              "9.7.9.9. Data Movement and Conversion Instructions: ld.global.nc",
              "ld.global.nc",
              "Data Movement and Conversion Instructions",
              Col(
                "ld.global{.cop}.nc{.level::cache_hint}{.level::prefetch_size}.type                 d, [a]{, cache_policy};\nld.global{.cop}.nc{.level::cache_hint}{.level::prefetch_size}.vec.type             d, [a]{, cache_policy};\n\nld.global.nc{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}.type      d, [a]{, cache_policy};\nld.global.nc{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}.vec.type  d, [a]{, cache_policy};\n\n.cop  =                     { .ca, .cg, .cs };     // cache operation\n.level1::eviction_priority = { .L1::evict_normal, .L1::evict_unchanged,\n                               .L1::evict_first, .L1::evict_last, .L1::no_allocate};\n.level2::eviction_priority = {.L2::evict_normal, .L2::evict_first, .L2::evict_last};\n.level::cache_hint =        { .L2::cache_hint };\n.level::prefetch_size =     { .L2::64B, .L2::128B, .L2::256B }\n.vec  =                     { .v2, .v4, .v8 };\n.type =                     { .b8, .b16, .b32, .b64, .b128,\n                              .u8, .u16, .u32, .u64,\n                              .s8, .s16, .s32, .s64,\n                              .f32, .f64 };"
              ),
              PTXVersion(3, 1),
              SMTarget(32),
              Col(
                InstructionForm(
                  "ld.global.nc",
                  Col(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set(
                    "b128",
                    "b16",
                    "b32",
                    "b64",
                    "b8",
                    "f32",
                    "f64",
                    "s16",
                    "s32",
                    "s64",
                    "s8",
                    "u16",
                    "u32",
                    "u64",
                    "u8"
                  )),
                  _root_.scala.collection.immutable.Set("L2::cache_hint", "ca", "cg", "cs"),
                  false
                ),
                InstructionForm(
                  "ld.global.nc",
                  Col(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("v2", "v4", "v8"),
                    _root_.scala.collection.immutable.Set(
                      "b128",
                      "b16",
                      "b32",
                      "b64",
                      "b8",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "s8",
                      "u16",
                      "u32",
                      "u64",
                      "u8"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("L2::cache_hint", "ca", "cg", "cs"),
                  false
                ),
                InstructionForm(
                  "ld.global.nc",
                  Col(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set(
                    "b128",
                    "b16",
                    "b32",
                    "b64",
                    "b8",
                    "f32",
                    "f64",
                    "s16",
                    "s32",
                    "s64",
                    "s8",
                    "u16",
                    "u32",
                    "u64",
                    "u8"
                  )),
                  _root_.scala.collection.immutable.Set(
                    "L1::evict_first",
                    "L1::evict_last",
                    "L1::evict_normal",
                    "L1::evict_unchanged",
                    "L1::no_allocate",
                    "L2::cache_hint",
                    "L2::evict_first",
                    "L2::evict_last",
                    "L2::evict_normal"
                  ),
                  false
                ),
                InstructionForm(
                  "ld.global.nc",
                  Col(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("v2", "v4", "v8"),
                    _root_.scala.collection.immutable.Set(
                      "b128",
                      "b16",
                      "b32",
                      "b64",
                      "b8",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "s8",
                      "u16",
                      "u32",
                      "u64",
                      "u8"
                    )
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L1::evict_first",
                    "L1::evict_last",
                    "L1::evict_normal",
                    "L1::evict_unchanged",
                    "L1::no_allocate",
                    "L2::cache_hint",
                    "L2::evict_first",
                    "L2::evict_last",
                    "L2::evict_normal"
                  ),
                  false
                )
              )
            )
          ))
      )
  case Ldu extends InstructionDef(
        "ldu",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Ldu(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("ldu", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-ldu",
              "9.7.9.10. Data Movement and Conversion Instructions: ldu",
              "ldu",
              "Data Movement and Conversion Instructions",
              Col(
                "ldu{.ss}.type      d, [a];       // load from address\nldu{.ss}.vec.type  d, [a];       // vec load from address\n\n.ss   = { .global };             // state space\n.vec  = { .v2, .v4 };\n.type = { .b8, .b16, .b32, .b64, .b128,\n          .u8, .u16, .u32, .u64,\n          .s8, .s16, .s32, .s64,\n                     .f32, .f64 };"
              ),
              PTXVersion(2, 0),
              SMTarget(13),
              Col(
                InstructionForm(
                  "ldu",
                  Col(OperandConstraint.Register, OperandConstraint.Address),
                  Col(_root_.scala.collection.immutable.Set(
                    "b128",
                    "b16",
                    "b32",
                    "b64",
                    "b8",
                    "f32",
                    "f64",
                    "s16",
                    "s32",
                    "s64",
                    "s8",
                    "u16",
                    "u32",
                    "u64",
                    "u8"
                  )),
                  _root_.scala.collection.immutable.Set("global"),
                  false
                ),
                InstructionForm(
                  "ldu",
                  Col(OperandConstraint.Register, OperandConstraint.Address),
                  Col(
                    _root_.scala.collection.immutable.Set("v2", "v4"),
                    _root_.scala.collection.immutable.Set(
                      "b128",
                      "b16",
                      "b32",
                      "b64",
                      "b8",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "s8",
                      "u16",
                      "u32",
                      "u64",
                      "u8"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("global"),
                  false
                )
              )
            )
          ))
      )
  case Lg2 extends InstructionDef(
        "lg2",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Lg2(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("lg2", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "floating-point-instructions-lg2",
              "9.7.3.20. Floating Point Instructions: lg2",
              "lg2",
              "Floating Point Instructions",
              Col("lg2.approx{.ftz}.f32  d, a;"),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm(
                "lg2",
                Col(OperandConstraint.Register, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("f32")),
                _root_.scala.collection.immutable.Set("ftz"),
                true
              ))
            )
          ))
      )
  case Lop3 extends InstructionDef(
        "lop3",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 5 || operands.size == 6) Right(Instruction.Lop3(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            operands(4),
            operands.lift(5),
            mods,
            pred
          ))
          else Left(InstructionDecodeError.InvalidOperandCount("lop3", Col(5, 6), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "logic-and-shift-instructions-lop3",
              "9.7.8.6. Logic and Shift Instructions: lop3",
              "lop3",
              "Logic and Shift Instructions",
              Col(
                "lop3.b32 d, a, b, c, immLut;\nlop3.BoolOp.b32 d|p, a, b, c, immLut, q;\n\n.BoolOp   = { .or , .and };"
              ),
              PTXVersion(4, 3),
              SMTarget(50),
              Col(
                InstructionForm(
                  "lop3",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(_root_.scala.collection.immutable.Set("b32")),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "lop3",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Register
                  ),
                  Col(_root_.scala.collection.immutable.Set("and", "or"), _root_.scala.collection.immutable.Set("b32")),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case Mad extends InstructionDef(
        "mad",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4) Right(Instruction.Mad(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("mad", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-mad",
              "9.7.1.4. Integer Arithmetic Instructions: mad",
              "mad",
              "Integer Arithmetic Instructions",
              Col(
                "mad.mode.type  d, a, b, c;\nmad.hi.sat.s32 d, a, b, c;\n\n.mode = { .hi, .lo, .wide };\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"
              ),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(
                InstructionForm(
                  "mad",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("hi", "lo", "wide"),
                    _root_.scala.collection.immutable.Set("s16", "s32", "s64", "u16", "u32", "u64")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ),
                InstructionForm(
                  "mad",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("hi"),
                    _root_.scala.collection.immutable.Set("sat"),
                    _root_.scala.collection.immutable.Set("s32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            ),
            Metadata.Entry(
              "floating-point-instructions-mad",
              "9.7.3.7. Floating Point Instructions: mad",
              "mad",
              "Floating Point Instructions",
              Col(
                "mad{.ftz}{.sat}.f32      d, a, b, c;    // .target sm_1x\nmad.rnd{.ftz}{.sat}.f32  d, a, b, c;    // .target sm_20\nmad.rnd.f64              d, a, b, c;    // .target sm_13 and higher\n\n.rnd = { .rn, .rz, .rm, .rp };"
              ),
              PTXVersion(1, 0),
              SMTarget(13),
              Col(
                InstructionForm(
                  "mad",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(_root_.scala.collection.immutable.Set("f32")),
                  _root_.scala.collection.immutable.Set("ftz", "sat"),
                  false
                ),
                InstructionForm(
                  "mad",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("ftz", "sat"),
                  false
                ),
                InstructionForm(
                  "mad",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"),
                    _root_.scala.collection.immutable.Set("f64")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case MadCc extends InstructionDef(
        "mad.cc",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.MadCc(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("mad.cc", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "extended-precision-arithmetic-instructions-mad-cc",
              "9.7.2.5. Extended-Precision Arithmetic Instructions: mad.cc",
              "mad.cc",
              "Extended-Precision Arithmetic Instructions",
              Col("mad{.hi,.lo}.cc.type  d, a, b, c;\n\n.type = { .u32, .s32, .u64, .s64 };"),
              PTXVersion(1, 0),
              SMTarget(20),
              Col(InstructionForm(
                "mad.cc",
                Col(
                  OperandConstraint.Register,
                  OperandConstraint.Value,
                  OperandConstraint.Value,
                  OperandConstraint.Value
                ),
                Col(_root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")),
                _root_.scala.collection.immutable.Set("hi", "lo"),
                true
              ))
            )
          ))
      )
  case Mad24 extends InstructionDef(
        "mad24",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Mad24(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("mad24", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-mad24",
              "9.7.1.7. Integer Arithmetic Instructions: mad24",
              "mad24",
              "Integer Arithmetic Instructions",
              Col(
                "mad24.mode.type  d, a, b, c;\nmad24.hi.sat.s32 d, a, b, c;\n\n.mode = { .hi, .lo };\n.type = { .u32, .s32 };"
              ),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(
                InstructionForm(
                  "mad24",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("hi", "lo"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ),
                InstructionForm(
                  "mad24",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("hi"),
                    _root_.scala.collection.immutable.Set("sat"),
                    _root_.scala.collection.immutable.Set("s32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case Madc extends InstructionDef(
        "madc",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Madc(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("madc", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "extended-precision-arithmetic-instructions-madc",
              "9.7.2.6. Extended-Precision Arithmetic Instructions: madc",
              "madc",
              "Extended-Precision Arithmetic Instructions",
              Col("madc{.hi,.lo}{.cc}.type  d, a, b, c;\n\n.type = { .u32, .s32, .u64, .s64 };"),
              PTXVersion(1, 0),
              SMTarget(20),
              Col(InstructionForm(
                "madc",
                Col(
                  OperandConstraint.Register,
                  OperandConstraint.Value,
                  OperandConstraint.Value,
                  OperandConstraint.Value
                ),
                Col(_root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")),
                _root_.scala.collection.immutable.Set("cc", "hi", "lo"),
                true
              ))
            )
          ))
      )
  case Mapa extends InstructionDef(
        "mapa",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.Mapa(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("mapa", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-mapa",
              "9.7.9.24. Data Movement and Conversion Instructions: mapa",
              "mapa",
              "Data Movement and Conversion Instructions",
              Col(
                "mapa{.space}.type          d, a, b;\n\n// Maps shared memory address in register a into CTA b.\nmapa.shared::cluster.type  d, a, b;\n\n// Maps shared memory variable into CTA b.\nmapa.shared::cluster.type  d, sh, b;\n\n// Maps shared memory variable into CTA b.\nmapa.shared::cluster.type  d, sh + imm, b;\n\n// Maps generic address in register a into CTA b.\nmapa.type                  d, a, b;\n\n.space = { .shared::cluster }\n.type  = { .u32, .u64 }"
              ),
              PTXVersion(7, 8),
              SMTarget(90),
              Col(
                InstructionForm(
                  "mapa",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("u32", "u64")),
                  _root_.scala.collection.immutable.Set("shared::cluster"),
                  true
                ),
                InstructionForm(
                  "mapa",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("shared::cluster"),
                    _root_.scala.collection.immutable.Set("u32", "u64")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ),
                InstructionForm(
                  "mapa",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("shared::cluster"),
                    _root_.scala.collection.immutable.Set("u32", "u64")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ),
                InstructionForm(
                  "mapa",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("shared::cluster"),
                    _root_.scala.collection.immutable.Set("u32", "u64")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ),
                InstructionForm(
                  "mapa",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("u32", "u64")),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case MatchAllSync extends InstructionDef(
        "match.all.sync",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.MatchAllSync(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("match.all.sync", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-match-sync",
              "9.7.14.11. Parallel Synchronization and Communication Instructions: match.sync",
              "match.sync",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "match.any.sync.type  d, a, membermask;\nmatch.all.sync.type  d[|p], a, membermask;\n\n.type = { .b32, .b64 };"
              ),
              PTXVersion(6, 0),
              SMTarget(70),
              Col(InstructionForm(
                "match.all.sync",
                Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("b32", "b64")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case MatchAnySync extends InstructionDef(
        "match.any.sync",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.MatchAnySync(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("match.any.sync", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-match-sync",
              "9.7.14.11. Parallel Synchronization and Communication Instructions: match.sync",
              "match.sync",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "match.any.sync.type  d, a, membermask;\nmatch.all.sync.type  d[|p], a, membermask;\n\n.type = { .b32, .b64 };"
              ),
              PTXVersion(6, 0),
              SMTarget(70),
              Col(InstructionForm(
                "match.any.sync",
                Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("b32", "b64")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Max extends InstructionDef(
        "max",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3 || operands.size == 4)
            Right(Instruction.Max(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("max", Col(3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-max",
              "9.7.1.14. Integer Arithmetic Instructions: max",
              "max",
              "Integer Arithmetic Instructions",
              Col(
                "max.type1         d, a, b;\nmax{.relu}.type2  d, a, b;\n\n.type1 = { .u16, .u32, .u64,\n           .u16x2, .u8x4, .s16, .s64 };\n.type2 = { .s16x2, .s32, .s8x4 };"
              ),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(
                InstructionForm(
                  "max",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("s16", "s64", "u16", "u16x2", "u32", "u64", "u8x4")),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "max",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("s16x2", "s32", "s8x4")),
                  _root_.scala.collection.immutable.Set("relu"),
                  false
                )
              )
            ),
            Metadata.Entry(
              "floating-point-instructions-max",
              "9.7.3.12. Floating Point Instructions: max",
              "max",
              "Floating Point Instructions",
              Col(
                "max{.ftz}{.NaN}{.xorsign.abs}.f32  d, a, b;\nmax{.ftz}{.NaN}{.abs}.f32          d, a, b, c;\nmax.f64                            d, a, b;"
              ),
              PTXVersion(1, 0),
              SMTarget(13),
              Col(
                InstructionForm(
                  "max",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f32")),
                  _root_.scala.collection.immutable.Set("NaN", "abs", "ftz", "xorsign"),
                  false
                ),
                InstructionForm(
                  "max",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(_root_.scala.collection.immutable.Set("f32")),
                  _root_.scala.collection.immutable.Set("NaN", "abs", "ftz"),
                  false
                ),
                InstructionForm(
                  "max",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f64")),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            ),
            Metadata.Entry(
              "half-precision-floating-point-instructions-max",
              "9.7.4.8. Half Precision Floating Point Instructions: max",
              "max",
              "Half Precision Floating Point Instructions",
              Col(
                "max{.ftz}{.NaN}{.xorsign.abs}.f16      d, a, b;\nmax{.ftz}{.NaN}{.xorsign.abs}.f16x2    d, a, b;\nmax{.NaN}{.xorsign.abs}.bf16           d, a, b;\nmax{.NaN}{.xorsign.abs}.bf16x2         d, a, b;"
              ),
              PTXVersion(7, 0),
              SMTarget(80),
              Col(
                InstructionForm(
                  "max",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f16")),
                  _root_.scala.collection.immutable.Set("NaN", "abs", "ftz", "xorsign"),
                  false
                ),
                InstructionForm(
                  "max",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f16x2")),
                  _root_.scala.collection.immutable.Set("NaN", "abs", "ftz", "xorsign"),
                  false
                ),
                InstructionForm(
                  "max",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("bf16")),
                  _root_.scala.collection.immutable.Set("NaN", "abs", "xorsign"),
                  false
                ),
                InstructionForm(
                  "max",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("bf16x2")),
                  _root_.scala.collection.immutable.Set("NaN", "abs", "xorsign"),
                  false
                )
              )
            )
          ))
      )
  case MbarrierArrive extends InstructionDef(
        "mbarrier.arrive",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2 || operands.size == 3)
            Right(Instruction.MbarrierArrive(operands(0), operands(1), operands.lift(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("mbarrier.arrive", Col(2, 3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-mbarrier-arrive",
              "9.7.14.16.16. Parallel Synchronization and Communication Instructions: mbarrier.arrive",
              "mbarrier.arrive",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "mbarrier.arrive{.sem.scope}{.shared{::cta}}.b64           state, [addr]{, count};\nmbarrier.arrive{.sem.scope}{.shared::cluster}.b64         _, [addr] {,count}\nmbarrier.arrive.expect_tx{.sem.scope}{.shared{::cta}}.b64 state, [addr], txCount;\nmbarrier.arrive.expect_tx{.sem.scope}{.shared::cluster}.b64   _, [addr], txCount;\nmbarrier.arrive.noComplete{.release.cta}{.shared{::cta}}.b64  state, [addr], count;\n\n.sem   = { .release, .relaxed }\n.scope = { .cta, .cluster }"
              ),
              PTXVersion(7, 0),
              SMTarget(80),
              Col(
                InstructionForm(
                  "mbarrier.arrive",
                  Col(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("b64")),
                  _root_.scala.collection.immutable.Set("cluster", "cta", "relaxed", "release", "shared"),
                  false
                ),
                InstructionForm(
                  "mbarrier.arrive",
                  Col(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("b64")),
                  _root_.scala.collection.immutable.Set("cluster", "cta", "relaxed", "release", "shared::cluster"),
                  false
                ),
                InstructionForm(
                  "mbarrier.arrive",
                  Col(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("expect_tx"), _root_.scala.collection.immutable.Set("b64")),
                  _root_.scala.collection.immutable.Set("cluster", "cta", "relaxed", "release", "shared"),
                  false
                ),
                InstructionForm(
                  "mbarrier.arrive",
                  Col(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("expect_tx"), _root_.scala.collection.immutable.Set("b64")),
                  _root_.scala.collection.immutable.Set("cluster", "cta", "relaxed", "release", "shared::cluster"),
                  false
                ),
                InstructionForm(
                  "mbarrier.arrive",
                  Col(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("noComplete"),
                    _root_.scala.collection.immutable.Set("b64")
                  ),
                  _root_.scala.collection.immutable.Set("cta", "release", "shared"),
                  false
                )
              )
            )
          ))
      )
  case MbarrierArriveDrop extends InstructionDef(
        "mbarrier.arrive_drop",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2 || operands.size == 3)
            Right(Instruction.MbarrierArriveDrop(operands(0), operands(1), operands.lift(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("mbarrier.arrive_drop", Col(2, 3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-mbarrier-arrive-drop",
              "9.7.14.16.17. Parallel Synchronization and Communication Instructions: mbarrier.arrive_drop",
              "mbarrier.arrive_drop",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "mbarrier.arrive_drop{.sem.scope}{.shared{::cta}}.b64              state, [addr] {, count};\nmbarrier.arrive_drop{.sem.scope}{.shared::cluster}.b64            _,     [addr] {, count};\nmbarrier.arrive_drop.expect_tx{.sem.scope}{.shared{::cta}}.b64    state, [addr], tx_count;\nmbarrier.arrive_drop.expect_tx{.sem.scope}{.shared::cluster}.b64  _,     [addr], tx_count;\nmbarrier.arrive_drop.noComplete{.release.cta}{.shared{::cta}}.b64 state, [addr], count;\n\n.sem   = { .release, .relaxed }\n.scope = { .cta, .cluster }"
              ),
              PTXVersion(7, 0),
              SMTarget(80),
              Col(
                InstructionForm(
                  "mbarrier.arrive_drop",
                  Col(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("b64")),
                  _root_.scala.collection.immutable.Set("cluster", "cta", "relaxed", "release", "shared"),
                  false
                ),
                InstructionForm(
                  "mbarrier.arrive_drop",
                  Col(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("b64")),
                  _root_.scala.collection.immutable.Set("cluster", "cta", "relaxed", "release", "shared::cluster"),
                  false
                ),
                InstructionForm(
                  "mbarrier.arrive_drop",
                  Col(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("expect_tx"), _root_.scala.collection.immutable.Set("b64")),
                  _root_.scala.collection.immutable.Set("cluster", "cta", "relaxed", "release", "shared"),
                  false
                ),
                InstructionForm(
                  "mbarrier.arrive_drop",
                  Col(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("expect_tx"), _root_.scala.collection.immutable.Set("b64")),
                  _root_.scala.collection.immutable.Set("cluster", "cta", "relaxed", "release", "shared::cluster"),
                  false
                ),
                InstructionForm(
                  "mbarrier.arrive_drop",
                  Col(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("noComplete"),
                    _root_.scala.collection.immutable.Set("b64")
                  ),
                  _root_.scala.collection.immutable.Set("cta", "release", "shared"),
                  false
                )
              )
            )
          ))
      )
  case MbarrierCheckLayout extends InstructionDef(
        "mbarrier.check_layout",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.MbarrierCheckLayout(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("mbarrier.check_layout", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-mbarrier-check-layout",
              "9.7.14.16.21. Parallel Synchronization and Communication Instructions: mbarrier.check_layout",
              "mbarrier.check_layout",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "mbarrier.check_layout.layout{.ss}.b64 p, [addr];\n\n.layout = { .layout::v0, .layout::v1 }\n.ss = { .shared::cta }"
              ),
              PTXVersion(9, 3),
              SMTarget(90),
              Col(InstructionForm(
                "mbarrier.check_layout",
                Col(OperandConstraint.Register, OperandConstraint.Address),
                Col(
                  _root_.scala.collection.immutable.Set("layout::v0", "layout::v1"),
                  _root_.scala.collection.immutable.Set("b64")
                ),
                _root_.scala.collection.immutable.Set("shared::cta"),
                true
              ))
            )
          ))
      )
  case MbarrierCompleteTx extends InstructionDef(
        "mbarrier.complete_tx",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.MbarrierCompleteTx(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("mbarrier.complete_tx", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-mbarrier-complete-tx",
              "9.7.14.16.15. Parallel Synchronization and Communication Instructions: mbarrier.complete_tx",
              "mbarrier.complete_tx",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "mbarrier.complete_tx{.sem.scope}{.space}.b64 [addr], txCount;\n\n.sem   = { .relaxed }\n.scope = { .cta, .cluster }\n.space = { .shared{::cta}, .shared::cluster }"
              ),
              PTXVersion(8, 0),
              SMTarget(90),
              Col(InstructionForm(
                "mbarrier.complete_tx",
                Col(OperandConstraint.Address, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("b64")),
                _root_.scala.collection.immutable.Set("cluster", "cta", "relaxed"),
                false
              ))
            )
          ))
      )
  case MbarrierExpectTx extends InstructionDef(
        "mbarrier.expect_tx",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.MbarrierExpectTx(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("mbarrier.expect_tx", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-mbarrier-expect-tx",
              "9.7.14.16.14. Parallel Synchronization and Communication Instructions: mbarrier.expect_tx",
              "mbarrier.expect_tx",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "mbarrier.expect_tx{.sem.scope}{.space}.b64 [addr], txCount;\n\n.sem   = { .relaxed }\n.scope = { .cta, .cluster }\n.space = { .shared{::cta}, .shared::cluster }"
              ),
              PTXVersion(8, 0),
              SMTarget(90),
              Col(InstructionForm(
                "mbarrier.expect_tx",
                Col(OperandConstraint.Address, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("b64")),
                _root_.scala.collection.immutable.Set("cluster", "cta", "relaxed"),
                false
              ))
            )
          ))
      )
  case MbarrierInit extends InstructionDef(
        "mbarrier.init",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.MbarrierInit(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("mbarrier.init", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-mbarrier-init",
              "9.7.14.16.12. Parallel Synchronization and Communication Instructions: mbarrier.init",
              "mbarrier.init",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "mbarrier.init{.layout}{.shared{::cta}}.b64 [addr], count;\n\n.layout = { .layout::v0, .layout::v1 }"
              ),
              PTXVersion(7, 0),
              SMTarget(80),
              Col(InstructionForm(
                "mbarrier.init",
                Col(OperandConstraint.Address, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("b64")),
                _root_.scala.collection.immutable.Set("layout::v0", "layout::v1", "shared"),
                false
              ))
            )
          ))
      )
  case MbarrierInval extends InstructionDef(
        "mbarrier.inval",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 1) Right(Instruction.MbarrierInval(operands(0), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("mbarrier.inval", Col(1), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-mbarrier-inval",
              "9.7.14.16.13. Parallel Synchronization and Communication Instructions: mbarrier.inval",
              "mbarrier.inval",
              "Parallel Synchronization and Communication Instructions",
              Col("mbarrier.inval{.shared{::cta}}.b64 [addr];"),
              PTXVersion(7, 0),
              SMTarget(80),
              Col(InstructionForm(
                "mbarrier.inval",
                Col(OperandConstraint.Address),
                Col(_root_.scala.collection.immutable.Set("b64")),
                _root_.scala.collection.immutable.Set("shared"),
                false
              ))
            )
          ))
      )
  case MbarrierPendingCount extends InstructionDef(
        "mbarrier.pending_count",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.MbarrierPendingCount(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("mbarrier.pending_count", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-mbarrier-pending-count",
              "9.7.14.16.20. Parallel Synchronization and Communication Instructions: mbarrier.pending_count",
              "mbarrier.pending_count",
              "Parallel Synchronization and Communication Instructions",
              Col("mbarrier.pending_count{.layout}.b64 count, state;\n\n.layout = { .layout::v0 }"),
              PTXVersion(7, 0),
              SMTarget(80),
              Col(InstructionForm(
                "mbarrier.pending_count",
                Col(OperandConstraint.Value, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("b64")),
                _root_.scala.collection.immutable.Set("layout::v0"),
                false
              ))
            )
          ))
      )
  case MbarrierTestWait extends InstructionDef(
        "mbarrier.test_wait",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3 || operands.size == 4) Right(if (operands.size == 3)
            Instruction.MbarrierTestWait(operands(0), None, operands(1), operands(2), mods, pred)
          else Instruction.MbarrierTestWait(operands(0), Some(operands(1)), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("mbarrier.test_wait", Col(3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-mbarrier-test-wait-try-wait",
              "9.7.14.16.19. Parallel Synchronization and Communication Instructions: mbarrier.test_wait / mbarrier.try_wait",
              "mbarrier.test_wait / mbarrier.try_wait",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "// without parity\nmbarrier.test_wait{.phase_type::primary}{.sem.scope}{.ss}.b64      waitComplete, [addr], state;\n\nmbarrier.test_wait.phase_type::primary{.sem}{.scope}{.ss}.b64      waitComplete|reportPredicate\n                                                                   {, reportValue}, [addr], state;\n\n// with parity\nmbarrier.test_wait.parity{.phase_type}{.sem.scope}{.ss}.b64        waitComplete, [addr], phaseParity;\n\nmbarrier.test_wait.parity.phase_type::primary{.sem}{.scope}{.ss}.b64  waitComplete|reportPredicate\n                                                                      {, reportValue}, [addr], phaseParity;\n\n// without parity\nmbarrier.try_wait{.phase_type::primary}{.sem.scope}{.ss}.b64      waitComplete, [addr], state {, timeHint};\n\nmbarrier.try_wait.phase_type::primary{.sem}{.scope}{.ss}.b64      waitComplete|reportPredicate {, reportValue},\n                                                                  [addr], state {, timeHint};\n\n// with parity\nmbarrier.try_wait.parity{.phase_type}{.sem.scope}{.ss}.b64            waitComplete, [addr], phaseParity {, timeHint};\n\nmbarrier.try_wait.parity.phase_type::primary{.sem}{.scope}{.ss}.b64   waitComplete|reportPredicate {, reportValue},\n                                                                      [addr], phaseParity {, timeHint};\n\n\n.ss   = { .shared{::cta} }\n.sem   = { .acquire, .relaxed }\n.scope = { .cta, .cluster }\n.phase_type = { .phase_type::primary, .phase_type::conditional }"
              ),
              PTXVersion(1, 0),
              SMTarget(80),
              Col(
                InstructionForm(
                  "mbarrier.test_wait",
                  Col(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("b64")),
                  _root_.scala.collection.immutable.Set("acquire", "cluster", "cta", "phase_type::primary", "relaxed"),
                  false
                ),
                InstructionForm(
                  "mbarrier.test_wait",
                  Col(
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("phase_type::primary"),
                    _root_.scala.collection.immutable.Set("b64")
                  ),
                  _root_.scala.collection.immutable.Set("acquire", "cluster", "cta", "relaxed"),
                  false
                ),
                InstructionForm(
                  "mbarrier.test_wait",
                  Col(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("parity"), _root_.scala.collection.immutable.Set("b64")),
                  _root_.scala.collection.immutable.Set(
                    "acquire",
                    "cluster",
                    "cta",
                    "phase_type::conditional",
                    "phase_type::primary",
                    "relaxed"
                  ),
                  false
                ),
                InstructionForm(
                  "mbarrier.test_wait",
                  Col(
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("parity"),
                    _root_.scala.collection.immutable.Set("phase_type::primary"),
                    _root_.scala.collection.immutable.Set("b64")
                  ),
                  _root_.scala.collection.immutable.Set("acquire", "cluster", "cta", "relaxed"),
                  false
                )
              )
            )
          ))
      )
  case MbarrierTryWait extends InstructionDef(
        "mbarrier.try_wait",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3 || operands.size == 4 || operands.size == 5) Right(if (operands.size == 3)
            Instruction.MbarrierTryWait(operands(0), None, operands(1), operands(2), None, mods, pred)
          else if (operands.size == 4 && !operands(0).isInstanceOf[Operand.WithPredicate])
            Instruction.MbarrierTryWait(operands(0), None, operands(1), operands(2), Some(operands(3)), mods, pred)
          else Instruction.MbarrierTryWait(
            operands(0),
            Some(operands(1)),
            operands(2),
            operands(3),
            operands.lift(4),
            mods,
            pred
          ))
          else Left(InstructionDecodeError.InvalidOperandCount("mbarrier.try_wait", Col(3, 4, 5), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-mbarrier-test-wait-try-wait",
              "9.7.14.16.19. Parallel Synchronization and Communication Instructions: mbarrier.test_wait / mbarrier.try_wait",
              "mbarrier.test_wait / mbarrier.try_wait",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "// without parity\nmbarrier.test_wait{.phase_type::primary}{.sem.scope}{.ss}.b64      waitComplete, [addr], state;\n\nmbarrier.test_wait.phase_type::primary{.sem}{.scope}{.ss}.b64      waitComplete|reportPredicate\n                                                                   {, reportValue}, [addr], state;\n\n// with parity\nmbarrier.test_wait.parity{.phase_type}{.sem.scope}{.ss}.b64        waitComplete, [addr], phaseParity;\n\nmbarrier.test_wait.parity.phase_type::primary{.sem}{.scope}{.ss}.b64  waitComplete|reportPredicate\n                                                                      {, reportValue}, [addr], phaseParity;\n\n// without parity\nmbarrier.try_wait{.phase_type::primary}{.sem.scope}{.ss}.b64      waitComplete, [addr], state {, timeHint};\n\nmbarrier.try_wait.phase_type::primary{.sem}{.scope}{.ss}.b64      waitComplete|reportPredicate {, reportValue},\n                                                                  [addr], state {, timeHint};\n\n// with parity\nmbarrier.try_wait.parity{.phase_type}{.sem.scope}{.ss}.b64            waitComplete, [addr], phaseParity {, timeHint};\n\nmbarrier.try_wait.parity.phase_type::primary{.sem}{.scope}{.ss}.b64   waitComplete|reportPredicate {, reportValue},\n                                                                      [addr], phaseParity {, timeHint};\n\n\n.ss   = { .shared{::cta} }\n.sem   = { .acquire, .relaxed }\n.scope = { .cta, .cluster }\n.phase_type = { .phase_type::primary, .phase_type::conditional }"
              ),
              PTXVersion(1, 0),
              SMTarget(80),
              Col(
                InstructionForm(
                  "mbarrier.try_wait",
                  Col(
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(_root_.scala.collection.immutable.Set("b64")),
                  _root_.scala.collection.immutable.Set("acquire", "cluster", "cta", "phase_type::primary", "relaxed"),
                  false
                ),
                InstructionForm(
                  "mbarrier.try_wait",
                  Col(
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("phase_type::primary"),
                    _root_.scala.collection.immutable.Set("b64")
                  ),
                  _root_.scala.collection.immutable.Set("acquire", "cluster", "cta", "relaxed"),
                  false
                ),
                InstructionForm(
                  "mbarrier.try_wait",
                  Col(
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(_root_.scala.collection.immutable.Set("parity"), _root_.scala.collection.immutable.Set("b64")),
                  _root_.scala.collection.immutable.Set(
                    "acquire",
                    "cluster",
                    "cta",
                    "phase_type::conditional",
                    "phase_type::primary",
                    "relaxed"
                  ),
                  false
                ),
                InstructionForm(
                  "mbarrier.try_wait",
                  Col(
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("parity"),
                    _root_.scala.collection.immutable.Set("phase_type::primary"),
                    _root_.scala.collection.immutable.Set("b64")
                  ),
                  _root_.scala.collection.immutable.Set("acquire", "cluster", "cta", "relaxed"),
                  false
                )
              )
            )
          ))
      )
  case Membar extends InstructionDef(
        "membar",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 0) Right(Instruction.Membar(mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("membar", Col(0), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-membar",
              "9.7.14.4. Parallel Synchronization and Communication Instructions: membar / fence",
              "membar / fence",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "// Thread fence:\nfence{.sem}.scope;\n\n// Thread fence:\nfence.acquire.sync_restrict::shared::cluster.cluster;\nfence.release.sync_restrict::shared::cta.cluster;\n\n// Operation fence:\nfence.op_restrict.release.cluster;\n\n// Proxy fence (bi-directional):\nfence.proxy.proxykind;\n\n// Proxy fence (uni-directional):\nfence.proxy.to_proxykind::from_proxykind.release.scope;\nfence.proxy.to_proxykind::from_proxykind.acquire.scope  [addr], size;\nfence.proxy.async::generic.acquire.sync_restrict::shared::cluster.cluster;\nfence.proxy.async::generic.release.sync_restrict::shared::cta.cluster;\nfence.proxy.to_proxykind::from_proxykind_fabric.alias.sem_fabric.sys;\n\n// Old style membar:\nmembar.level;\nmembar.proxy.proxykind;\n\n.sem       = { .sc, .acq_rel, .acquire, .release };\n.scope     = { .cta, .cluster, .gpu, .sys };\n.level     = { .cta, .gl, .sys };\n.proxykind = { .alias, .async, .async.global, .async.shared::{cta, cluster} };\n.op_restrict = { .mbarrier_init };\n.to_proxykind::from_proxykind = {.tensormap::generic};\n.to_proxykind::from_proxykind_fabric = {.generic::fabric, .fabric::generic, .fabric::fabric };\n.sem_fabric = { .acquire, .release };"
              ),
              PTXVersion(1, 0),
              SMTarget(20),
              Col(
                InstructionForm(
                  "membar",
                  Col(),
                  Col(_root_.scala.collection.immutable.Set("cta", "gl", "sys")),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "membar",
                  Col(),
                  Col(
                    _root_.scala.collection.immutable.Set("proxy"),
                    _root_.scala.collection.immutable.Set("alias", "async", "cluster")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case Min extends InstructionDef(
        "min",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3 || operands.size == 4)
            Right(Instruction.Min(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("min", Col(3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-min",
              "9.7.1.13. Integer Arithmetic Instructions: min",
              "min",
              "Integer Arithmetic Instructions",
              Col(
                "min.type1         d, a, b;\nmin{.relu}.type2  d, a, b;\n\n.type1 = { .u16, .u32, .u64,\n           .u16x2, .u8x4, .s16, .s64 };\n.type2 = { .s16x2, .s32, .s8x4 };"
              ),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(
                InstructionForm(
                  "min",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("s16", "s64", "u16", "u16x2", "u32", "u64", "u8x4")),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "min",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("s16x2", "s32", "s8x4")),
                  _root_.scala.collection.immutable.Set("relu"),
                  false
                )
              )
            ),
            Metadata.Entry(
              "floating-point-instructions-min",
              "9.7.3.11. Floating Point Instructions: min",
              "min",
              "Floating Point Instructions",
              Col(
                "min{.ftz}{.NaN}{.xorsign.abs}.f32  d, a, b;\nmin{.ftz}{.NaN}{.abs}.f32          d, a, b, c;\nmin.f64                            d, a, b;"
              ),
              PTXVersion(1, 0),
              SMTarget(13),
              Col(
                InstructionForm(
                  "min",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f32")),
                  _root_.scala.collection.immutable.Set("NaN", "abs", "ftz", "xorsign"),
                  false
                ),
                InstructionForm(
                  "min",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(_root_.scala.collection.immutable.Set("f32")),
                  _root_.scala.collection.immutable.Set("NaN", "abs", "ftz"),
                  false
                ),
                InstructionForm(
                  "min",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f64")),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            ),
            Metadata.Entry(
              "half-precision-floating-point-instructions-min",
              "9.7.4.7. Half Precision Floating Point Instructions: min",
              "min",
              "Half Precision Floating Point Instructions",
              Col(
                "min{.ftz}{.NaN}{.xorsign.abs}.f16      d, a, b;\nmin{.ftz}{.NaN}{.xorsign.abs}.f16x2    d, a, b;\nmin{.NaN}{.xorsign.abs}.bf16           d, a, b;\nmin{.NaN}{.xorsign.abs}.bf16x2         d, a, b;"
              ),
              PTXVersion(7, 0),
              SMTarget(80),
              Col(
                InstructionForm(
                  "min",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f16")),
                  _root_.scala.collection.immutable.Set("NaN", "abs", "ftz", "xorsign"),
                  false
                ),
                InstructionForm(
                  "min",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f16x2")),
                  _root_.scala.collection.immutable.Set("NaN", "abs", "ftz", "xorsign"),
                  false
                ),
                InstructionForm(
                  "min",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("bf16")),
                  _root_.scala.collection.immutable.Set("NaN", "abs", "xorsign"),
                  false
                ),
                InstructionForm(
                  "min",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("bf16x2")),
                  _root_.scala.collection.immutable.Set("NaN", "abs", "xorsign"),
                  false
                )
              )
            )
          ))
      )
  case Mov extends InstructionDef(
        "mov",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Mov(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("mov", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-mov",
              "9.7.9.3. Data Movement and Conversion Instructions: mov",
              "mov",
              "Data Movement and Conversion Instructions",
              Col(
                "mov.type  d, a;\nmov.type  d, sreg;\nmov.type  d, avar;       // get address of variable\nmov.type  d, avar+imm;   // get address of variable with offset\nmov.u32   d, fname;      // get address of device function\nmov.u64   d, fname;      // get address of device function\nmov.u32   d, kernel;     // get address of entry function\nmov.u64   d, kernel;     // get address of entry function\n\n.type = { .pred,\n          .b16, .b32, .b64,\n          .u16, .u32, .u64,\n          .s16, .s32, .s64,\n                .f32, .f64 };"
              ),
              PTXVersion(1, 0),
              SMTarget(13),
              Col(
                InstructionForm(
                  "mov",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set(
                    "b16",
                    "b32",
                    "b64",
                    "f32",
                    "f64",
                    "pred",
                    "s16",
                    "s32",
                    "s64",
                    "u16",
                    "u32",
                    "u64"
                  )),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "mov",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set(
                    "b16",
                    "b32",
                    "b64",
                    "f32",
                    "f64",
                    "pred",
                    "s16",
                    "s32",
                    "s64",
                    "u16",
                    "u32",
                    "u64"
                  )),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "mov",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set(
                    "b16",
                    "b32",
                    "b64",
                    "f32",
                    "f64",
                    "pred",
                    "s16",
                    "s32",
                    "s64",
                    "u16",
                    "u32",
                    "u64"
                  )),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "mov",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set(
                    "b16",
                    "b32",
                    "b64",
                    "f32",
                    "f64",
                    "pred",
                    "s16",
                    "s32",
                    "s64",
                    "u16",
                    "u32",
                    "u64"
                  )),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "mov",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("u32")),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "mov",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("u64")),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "mov",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("u32")),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "mov",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("u64")),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            ),
            Metadata.Entry(
              "data-movement-and-conversion-instructions-mov-2",
              "9.7.9.4. Data Movement and Conversion Instructions: mov",
              "mov",
              "Data Movement and Conversion Instructions",
              Col("mov.type  d, a;\n\n.type = { .b16, .b32, .b64, .b128 };"),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm(
                "mov",
                Col(OperandConstraint.Register, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64")),
                _root_.scala.collection.immutable.Set(),
                false
              ))
            )
          ))
      )
  case Mul extends InstructionDef(
        "mul",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.Mul(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("mul", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-mul",
              "9.7.1.3. Integer Arithmetic Instructions: mul",
              "mul",
              "Integer Arithmetic Instructions",
              Col(
                "mul.mode.type  d, a, b;\n\n.mode = { .hi, .lo, .wide };\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"
              ),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm(
                "mul",
                Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                Col(
                  _root_.scala.collection.immutable.Set("hi", "lo", "wide"),
                  _root_.scala.collection.immutable.Set("s16", "s32", "s64", "u16", "u32", "u64")
                ),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            ),
            Metadata.Entry(
              "floating-point-instructions-mul",
              "9.7.3.5. Floating Point Instructions: mul",
              "mul",
              "Floating Point Instructions",
              Col(
                "mul{.rnd}{.ftz}{.sat}.f32  d, a, b;\nmul{.rnd}{.ftz}.f32x2      d, a, b;\nmul{.rnd}.f64              d, a, b;\n\n.rnd = { .rn, .rz, .rm, .rp };"
              ),
              PTXVersion(1, 0),
              SMTarget(13),
              Col(
                InstructionForm(
                  "mul",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f32")),
                  _root_.scala.collection.immutable.Set("ftz", "rm", "rn", "rp", "rz", "sat"),
                  false
                ),
                InstructionForm(
                  "mul",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f32x2")),
                  _root_.scala.collection.immutable.Set("ftz", "rm", "rn", "rp", "rz"),
                  false
                ),
                InstructionForm(
                  "mul",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f64")),
                  _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"),
                  false
                )
              )
            ),
            Metadata.Entry(
              "half-precision-floating-point-instructions-mul",
              "9.7.4.3. Half Precision Floating Point Instructions: mul",
              "mul",
              "Half Precision Floating Point Instructions",
              Col(
                "mul{.rnd}{.ftz}{.sat}.f16   d, a, b;\nmul{.rnd}{.ftz}{.sat}.f16x2 d, a, b;\n\nmul{.rnd}.bf16   d, a, b;\nmul{.rnd}.bf16x2 d, a, b;\n\n.rnd = { .rn };"
              ),
              PTXVersion(4, 2),
              SMTarget(53),
              Col(
                InstructionForm(
                  "mul",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f16")),
                  _root_.scala.collection.immutable.Set("ftz", "rn", "sat"),
                  false
                ),
                InstructionForm(
                  "mul",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f16x2")),
                  _root_.scala.collection.immutable.Set("ftz", "rn", "sat"),
                  false
                ),
                InstructionForm(
                  "mul",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("bf16")),
                  _root_.scala.collection.immutable.Set("rn"),
                  false
                ),
                InstructionForm(
                  "mul",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("bf16x2")),
                  _root_.scala.collection.immutable.Set("rn"),
                  false
                )
              )
            )
          ))
      )
  case Mul24 extends InstructionDef(
        "mul24",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.Mul24(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("mul24", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-mul24",
              "9.7.1.6. Integer Arithmetic Instructions: mul24",
              "mul24",
              "Integer Arithmetic Instructions",
              Col("mul24.mode.type  d, a, b;\n\n.mode = { .hi, .lo };\n.type = { .u32, .s32 };"),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm(
                "mul24",
                Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                Col(
                  _root_.scala.collection.immutable.Set("hi", "lo"),
                  _root_.scala.collection.immutable.Set("s32", "u32")
                ),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case MultimemCpAsyncBulk extends InstructionDef(
        "multimem.cp.async.bulk",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3 || operands.size == 4)
            Right(Instruction.MultimemCpAsyncBulk(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("multimem.cp.async.bulk", Col(3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-multimem-cp-async-bulk",
              "9.7.9.26.4.4. Data Movement and Conversion Instructions: multimem.cp.async.bulk",
              "multimem.cp.async.bulk",
              "Data Movement and Conversion Instructions",
              Col(
                "multimem.cp.async.bulk{.sem}.dst.src.completion_mechanism{.cp_mask}\n    [dstMem], [srcMem], size{, byteMask};\n\n    .sem                  = { .weak }\n    .dst                  = { .global }\n    .src                  = { .shared::cta }\n    .completion_mechanism = { .bulk_group }\n\nmultimem.cp.async.bulk.sem.scope.dst.src.completion_mechanism{.cp_mask}.type\n    [dstMem], [srcMem], size{, byteMask};\n\n    .sem                  = { .relaxed }\n    .scope                = { .cta, .cluster, .gpu, .sys }\n    .dst                  = { .global }\n    .src                  = { .shared::cta }\n    .completion_mechanism = { .bulk_group }\n    .type                 = { .b128 }"
              ),
              PTXVersion(9, 1),
              SMTarget(90),
              Col(
                InstructionForm(
                  "multimem.cp.async.bulk",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group")
                  ),
                  _root_.scala.collection.immutable.Set("cp_mask", "relaxed"),
                  false
                ),
                InstructionForm(
                  "multimem.cp.async.bulk",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"),
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group"),
                    _root_.scala.collection.immutable.Set("b128")
                  ),
                  _root_.scala.collection.immutable.Set("cp_mask"),
                  false
                )
              )
            )
          ))
      )
  case MultimemCpReduceAsyncBulk extends InstructionDef(
        "multimem.cp.reduce.async.bulk",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3)
            Right(Instruction.MultimemCpReduceAsyncBulk(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("multimem.cp.reduce.async.bulk", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-multimem-cp-reduce-async-bulk",
              "9.7.9.26.4.5. Data Movement and Conversion Instructions: multimem.cp.reduce.async.bulk",
              "multimem.cp.reduce.async.bulk",
              "Data Movement and Conversion Instructions",
              Col(
                "multimem.cp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism.redOp.type  [dstMem], [srcMem], size;\n\n    .sem                  =  { .relaxed }\n    .scope                = { .cta, .cluster, .gpu, .sys }\n    .dst                  = { .global }\n    .src                  = { .shared::cta }\n    .completion_mechanism = { .bulk_group }\n    .redOp                = { .and, .or, .xor,\n                              .add, .inc, .dec,\n                              .min, .max }\n    .type                 = { .f16, .bf16,\n                              .b32, .u32, .s32,\n                              .b64, .u64, .s64,\n                              .f32, .f64 }\n\nmultimem.cp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism.add.noftz.type  [dstMem], [srcMem], size;\n\n    .sem                  = { .relaxed }\n    .scope                = { .cta, .cluster, .gpu, .sys }\n    .dst                  = { .global }\n    .src                  = { .shared::cta }\n    .completion_mechanism = { .bulk_group }\n    .type                 = { .f16, .bf16 }"
              ),
              PTXVersion(9, 1),
              SMTarget(90),
              Col(
                InstructionForm(
                  "multimem.cp.reduce.async.bulk",
                  Col(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group"),
                    _root_.scala.collection.immutable.Set("add", "and", "dec", "inc", "max", "min", "or", "xor"),
                    _root_.scala.collection.immutable.Set("bf16", "f16")
                  ),
                  _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "relaxed", "sys"),
                  true
                ),
                InstructionForm(
                  "multimem.cp.reduce.async.bulk",
                  Col(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("shared::cta"),
                    _root_.scala.collection.immutable.Set("bulk_group"),
                    _root_.scala.collection.immutable.Set("add"),
                    _root_.scala.collection.immutable.Set("noftz"),
                    _root_.scala.collection.immutable.Set("bf16", "f16")
                  ),
                  _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "relaxed", "sys"),
                  true
                )
              )
            )
          ))
      )
  case MultimemLdReduce extends InstructionDef(
        "multimem.ld_reduce",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.MultimemLdReduce(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("multimem.ld_reduce", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-multimem",
              "9.7.9.15. Data Movement and Conversion Instructions: multimem.ld_reduce, multimem.st, multimem.red",
              "multimem.ld_reduce, multimem.st, multimem.red",
              "Data Movement and Conversion Instructions",
              Col(
                "// Integer type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op.type      d, [a];\nmultimem.ld_reduce.weak{.ss}.op.type                 d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}.type                [a], b;\nmultimem.st.weak{.ss}.type                           [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.op.type           [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add, .and, .or, .xor }\n.type =     { .b32, .b64,  .u32, .u64, .s32, .s64 }\n\n// Floating point type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op{.acc_prec}{.vec}.type    d, [a];\nmultimem.ld_reduce.weak{.ss}.op{.acc_prec}{.vec}.type               d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}{.vec}.type                         [a], b;\nmultimem.st.weak{.ss}{.vec}.type                                    [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.redop{.vec}.redtype              [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add }\n.redop  =   { .add }\n.acc_prec = { .acc::f32, .acc::f16 }\n.vec =      { .v2, .v4, .v8 }\n.type=      { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64, .e5m2, .e5m2x2, .e5m2x4, .e4m3, .e4m3x2, .e4m3x4 }\n.redtype =  { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64 }"
              ),
              PTXVersion(8, 1),
              SMTarget(90),
              Col(
                InstructionForm(
                  "multimem.ld_reduce",
                  Col(OperandConstraint.Register, OperandConstraint.Address),
                  Col(
                    _root_.scala.collection.immutable.Set("add", "max", "min"),
                    _root_.scala.collection.immutable.Set(
                      "bf16",
                      "bf16x2",
                      "e4m3",
                      "e4m3x2",
                      "e4m3x4",
                      "e5m2",
                      "e5m2x2",
                      "e5m2x4",
                      "f16",
                      "f16x2",
                      "f32",
                      "f64"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("acquire", "cluster", "cta", "global", "gpu", "relaxed", "sys"),
                  false
                ),
                InstructionForm(
                  "multimem.ld_reduce",
                  Col(OperandConstraint.Register, OperandConstraint.Address),
                  Col(
                    _root_.scala.collection.immutable.Set("weak"),
                    _root_.scala.collection.immutable.Set("add", "max", "min"),
                    _root_.scala.collection.immutable.Set(
                      "bf16",
                      "bf16x2",
                      "e4m3",
                      "e4m3x2",
                      "e4m3x4",
                      "e5m2",
                      "e5m2x2",
                      "e5m2x4",
                      "f16",
                      "f16x2",
                      "f32",
                      "f64"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("global"),
                  false
                ),
                InstructionForm(
                  "multimem.ld_reduce",
                  Col(OperandConstraint.Register, OperandConstraint.Address),
                  Col(
                    _root_.scala.collection.immutable.Set("add", "max", "min"),
                    _root_.scala.collection.immutable.Set(
                      "bf16",
                      "bf16x2",
                      "e4m3",
                      "e4m3x2",
                      "e4m3x4",
                      "e5m2",
                      "e5m2x2",
                      "e5m2x4",
                      "f16",
                      "f16x2",
                      "f32",
                      "f64"
                    )
                  ),
                  _root_.scala.collection.immutable.Set(
                    "acc::f16",
                    "acc::f32",
                    "acquire",
                    "cluster",
                    "cta",
                    "global",
                    "gpu",
                    "relaxed",
                    "sys",
                    "v2",
                    "v4",
                    "v8"
                  ),
                  false
                ),
                InstructionForm(
                  "multimem.ld_reduce",
                  Col(OperandConstraint.Register, OperandConstraint.Address),
                  Col(
                    _root_.scala.collection.immutable.Set("weak"),
                    _root_.scala.collection.immutable.Set("add", "max", "min"),
                    _root_.scala.collection.immutable.Set(
                      "bf16",
                      "bf16x2",
                      "e4m3",
                      "e4m3x2",
                      "e4m3x4",
                      "e5m2",
                      "e5m2x2",
                      "e5m2x4",
                      "f16",
                      "f16x2",
                      "f32",
                      "f64"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("acc::f16", "acc::f32", "global", "v2", "v4", "v8"),
                  false
                )
              )
            )
          ))
      )
  case MultimemRed extends InstructionDef(
        "multimem.red",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.MultimemRed(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("multimem.red", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-multimem",
              "9.7.9.15. Data Movement and Conversion Instructions: multimem.ld_reduce, multimem.st, multimem.red",
              "multimem.ld_reduce, multimem.st, multimem.red",
              "Data Movement and Conversion Instructions",
              Col(
                "// Integer type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op.type      d, [a];\nmultimem.ld_reduce.weak{.ss}.op.type                 d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}.type                [a], b;\nmultimem.st.weak{.ss}.type                           [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.op.type           [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add, .and, .or, .xor }\n.type =     { .b32, .b64,  .u32, .u64, .s32, .s64 }\n\n// Floating point type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op{.acc_prec}{.vec}.type    d, [a];\nmultimem.ld_reduce.weak{.ss}.op{.acc_prec}{.vec}.type               d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}{.vec}.type                         [a], b;\nmultimem.st.weak{.ss}{.vec}.type                                    [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.redop{.vec}.redtype              [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add }\n.redop  =   { .add }\n.acc_prec = { .acc::f32, .acc::f16 }\n.vec =      { .v2, .v4, .v8 }\n.type=      { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64, .e5m2, .e5m2x2, .e5m2x4, .e4m3, .e4m3x2, .e4m3x4 }\n.redtype =  { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64 }"
              ),
              PTXVersion(8, 1),
              SMTarget(90),
              Col(
                InstructionForm(
                  "multimem.red",
                  Col(OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("add", "max", "min"),
                    _root_.scala.collection.immutable.Set(
                      "bf16",
                      "bf16x2",
                      "e4m3",
                      "e4m3x2",
                      "e4m3x4",
                      "e5m2",
                      "e5m2x2",
                      "e5m2x4",
                      "f16",
                      "f16x2",
                      "f32",
                      "f64"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("cluster", "cta", "global", "gpu", "relaxed", "release", "sys"),
                  false
                ),
                InstructionForm(
                  "multimem.red",
                  Col(OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("add"),
                    _root_.scala.collection.immutable.Set("bf16", "bf16x2", "f16", "f16x2", "f32", "f64")
                  ),
                  _root_.scala.collection.immutable.Set(
                    "cluster",
                    "cta",
                    "global",
                    "gpu",
                    "relaxed",
                    "release",
                    "sys",
                    "v2",
                    "v4",
                    "v8"
                  ),
                  false
                )
              )
            )
          ))
      )
  case MultimemRedAsync extends InstructionDef(
        "multimem.red.async",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.MultimemRedAsync(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("multimem.red.async", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-multimem-red-async",
              "9.7.14.8. Parallel Synchronization and Communication Instructions: multimem.red.async",
              "multimem.red.async",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "multimem.red.async.sem.scope{.ss}.op.type [a], b;\n\n.sem   = { .release };\n.scope = { .gpu, .sys };\n.ss    = { .global };\n.op    = { .add };\n.type  = { .u32, .s32, .u64 };"
              ),
              PTXVersion(9, 3),
              SMTarget(100),
              Col(InstructionForm(
                "multimem.red.async",
                Col(OperandConstraint.Address, OperandConstraint.Value),
                Col(
                  _root_.scala.collection.immutable.Set("release"),
                  _root_.scala.collection.immutable.Set("gpu", "sys"),
                  _root_.scala.collection.immutable.Set("add"),
                  _root_.scala.collection.immutable.Set("s32", "u32", "u64")
                ),
                _root_.scala.collection.immutable.Set("global"),
                true
              ))
            )
          ))
      )
  case MultimemSt extends InstructionDef(
        "multimem.st",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.MultimemSt(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("multimem.st", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-multimem",
              "9.7.9.15. Data Movement and Conversion Instructions: multimem.ld_reduce, multimem.st, multimem.red",
              "multimem.ld_reduce, multimem.st, multimem.red",
              "Data Movement and Conversion Instructions",
              Col(
                "// Integer type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op.type      d, [a];\nmultimem.ld_reduce.weak{.ss}.op.type                 d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}.type                [a], b;\nmultimem.st.weak{.ss}.type                           [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.op.type           [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add, .and, .or, .xor }\n.type =     { .b32, .b64,  .u32, .u64, .s32, .s64 }\n\n// Floating point type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op{.acc_prec}{.vec}.type    d, [a];\nmultimem.ld_reduce.weak{.ss}.op{.acc_prec}{.vec}.type               d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}{.vec}.type                         [a], b;\nmultimem.st.weak{.ss}{.vec}.type                                    [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.redop{.vec}.redtype              [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add }\n.redop  =   { .add }\n.acc_prec = { .acc::f32, .acc::f16 }\n.vec =      { .v2, .v4, .v8 }\n.type=      { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64, .e5m2, .e5m2x2, .e5m2x4, .e4m3, .e4m3x2, .e4m3x4 }\n.redtype =  { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64 }"
              ),
              PTXVersion(8, 1),
              SMTarget(90),
              Col(
                InstructionForm(
                  "multimem.st",
                  Col(OperandConstraint.Address, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set(
                    "bf16",
                    "bf16x2",
                    "e4m3",
                    "e4m3x2",
                    "e4m3x4",
                    "e5m2",
                    "e5m2x2",
                    "e5m2x4",
                    "f16",
                    "f16x2",
                    "f32",
                    "f64"
                  )),
                  _root_.scala.collection.immutable.Set("cluster", "cta", "global", "gpu", "relaxed", "release", "sys"),
                  false
                ),
                InstructionForm(
                  "multimem.st",
                  Col(OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("weak"),
                    _root_.scala.collection.immutable.Set(
                      "bf16",
                      "bf16x2",
                      "e4m3",
                      "e4m3x2",
                      "e4m3x4",
                      "e5m2",
                      "e5m2x2",
                      "e5m2x4",
                      "f16",
                      "f16x2",
                      "f32",
                      "f64"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("global"),
                  false
                ),
                InstructionForm(
                  "multimem.st",
                  Col(OperandConstraint.Address, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set(
                    "bf16",
                    "bf16x2",
                    "e4m3",
                    "e4m3x2",
                    "e4m3x4",
                    "e5m2",
                    "e5m2x2",
                    "e5m2x4",
                    "f16",
                    "f16x2",
                    "f32",
                    "f64"
                  )),
                  _root_.scala.collection.immutable.Set(
                    "cluster",
                    "cta",
                    "global",
                    "gpu",
                    "relaxed",
                    "release",
                    "sys",
                    "v2",
                    "v4",
                    "v8"
                  ),
                  false
                ),
                InstructionForm(
                  "multimem.st",
                  Col(OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("weak"),
                    _root_.scala.collection.immutable.Set(
                      "bf16",
                      "bf16x2",
                      "e4m3",
                      "e4m3x2",
                      "e4m3x4",
                      "e5m2",
                      "e5m2x2",
                      "e5m2x4",
                      "f16",
                      "f16x2",
                      "f32",
                      "f64"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("global", "v2", "v4", "v8"),
                  false
                )
              )
            )
          ))
      )
  case MultimemStAsync extends InstructionDef(
        "multimem.st.async",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.MultimemStAsync(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("multimem.st.async", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-multimem-st-async",
              "9.7.9.13. Data Movement and Conversion Instructions: multimem.st.async",
              "multimem.st.async",
              "Data Movement and Conversion Instructions",
              Col(
                "multimem.st.async.sem.scope{.ss}.type [a], b;\n\n.sem   = { .release };\n.scope = { .gpu, .sys };\n.ss    = { .global };\n.type  = { .b8, .b16, .b32, .b64,\n            .u8, .u16, .u32, .u64,\n            .s8, .s16, .s32, .s64,\n            .f32, .f64 };"
              ),
              PTXVersion(9, 3),
              SMTarget(100),
              Col(InstructionForm(
                "multimem.st.async",
                Col(OperandConstraint.Address, OperandConstraint.Value),
                Col(
                  _root_.scala.collection.immutable.Set("release"),
                  _root_.scala.collection.immutable.Set("gpu", "sys"),
                  _root_.scala.collection.immutable.Set(
                    "b16",
                    "b32",
                    "b64",
                    "b8",
                    "f32",
                    "f64",
                    "s16",
                    "s32",
                    "s64",
                    "s8",
                    "u16",
                    "u32",
                    "u64",
                    "u8"
                  )
                ),
                _root_.scala.collection.immutable.Set("global"),
                true
              ))
            )
          ))
      )
  case NanoSleep extends InstructionDef(
        "nanosleep",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 1) Right(Instruction.NanoSleep(operands(0), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("nanosleep", Col(1), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "miscellaneous-instructions-nanosleep",
              "9.7.20.2. Miscellaneous Instructions: nanosleep",
              "nanosleep",
              "Miscellaneous Instructions",
              Col("nanosleep.u32 t;"),
              PTXVersion(1, 0),
              SMTarget(70),
              Col(InstructionForm(
                "nanosleep",
                Col(OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("u32")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Neg extends InstructionDef(
        "neg",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Neg(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("neg", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-neg",
              "9.7.1.12. Integer Arithmetic Instructions: neg",
              "neg",
              "Integer Arithmetic Instructions",
              Col("neg.type  d, a;\n\n.type = { .s8x4, .s16, .s32, .s64 };"),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm(
                "neg",
                Col(OperandConstraint.Register, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("s16", "s32", "s64", "s8x4")),
                _root_.scala.collection.immutable.Set(),
                false
              ))
            ),
            Metadata.Entry(
              "floating-point-instructions-neg",
              "9.7.3.10. Floating Point Instructions: neg",
              "neg",
              "Floating Point Instructions",
              Col("neg{.ftz}.f32  d, a;\nneg.f64        d, a;"),
              PTXVersion(1, 0),
              SMTarget(13),
              Col(
                InstructionForm(
                  "neg",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f32")),
                  _root_.scala.collection.immutable.Set("ftz"),
                  true
                ),
                InstructionForm(
                  "neg",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f64")),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            ),
            Metadata.Entry(
              "half-precision-floating-point-instructions-neg",
              "9.7.4.5. Half Precision Floating Point Instructions: neg",
              "neg",
              "Half Precision Floating Point Instructions",
              Col("neg{.ftz}.f16    d, a;\nneg{.ftz}.f16x2  d, a;\nneg.bf16         d, a;\nneg.bf16x2       d, a;"),
              PTXVersion(6, 0),
              SMTarget(53),
              Col(
                InstructionForm(
                  "neg",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f16")),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "neg",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f16x2")),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "neg",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("bf16")),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "neg",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("bf16x2")),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case Not extends InstructionDef(
        "not",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Not(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("not", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "logic-and-shift-instructions-not",
              "9.7.8.4. Logic and Shift Instructions: not",
              "not",
              "Logic and Shift Instructions",
              Col("not.type d, a;\n\n.type = { .pred, .b16, .b32, .b64 };"),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm(
                "not",
                Col(OperandConstraint.Register, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("b16", "b32", "b64", "pred")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Or extends InstructionDef(
        "or",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.Or(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("or", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "logic-and-shift-instructions-or",
              "9.7.8.2. Logic and Shift Instructions: or",
              "or",
              "Logic and Shift Instructions",
              Col("or.type d, a, b;\n\n.type = { .pred, .b16, .b32, .b64 };"),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm(
                "or",
                Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("b16", "b32", "b64", "pred")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case PmEvent extends InstructionDef(
        "pmevent",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 1) Right(Instruction.PmEvent(operands(0), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("pmevent", Col(1), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "miscellaneous-instructions-pmevent",
              "9.7.20.3. Miscellaneous Instructions: pmevent",
              "pmevent",
              "Miscellaneous Instructions",
              Col(
                "pmevent       a;    // trigger a single performance monitor event\npmevent.mask  a;    // trigger one or more performance monitor events"
              ),
              PTXVersion(1, 0),
              SMTarget(20),
              Col(
                InstructionForm(
                  "pmevent",
                  Col(OperandConstraint.Value),
                  Col(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ),
                InstructionForm(
                  "pmevent",
                  Col(OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("mask")),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case Popc extends InstructionDef(
        "popc",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Popc(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("popc", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-popc",
              "9.7.1.15. Integer Arithmetic Instructions: popc",
              "popc",
              "Integer Arithmetic Instructions",
              Col("popc.type  d, a;\n\n.type = { .b32, .b64 };"),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(InstructionForm(
                "popc",
                Col(OperandConstraint.Register, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("b32", "b64")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Prefetch extends InstructionDef(
        "prefetch",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 1) Right(Instruction.Prefetch(operands(0), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("prefetch", Col(1), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-prefetch-prefetchu",
              "9.7.9.16. Data Movement and Conversion Instructions: prefetch, prefetchu",
              "prefetch, prefetchu",
              "Data Movement and Conversion Instructions",
              Col(
                "prefetch{.space}.level                    [a];   // prefetch to data cache\nprefetch.global.level::eviction_priority  [a];   // prefetch to data cache\n\nprefetchu.L1  [a];             // prefetch to uniform cache\n\nprefetch{.tensormap_space}.tensormap [a];  // prefetch the tensormap\n\n.space =                    { .global, .local };\n.level =                    { .L1, .L2 };\n.level::eviction_priority = { .L2::evict_last, .L2::evict_normal };\n.tensormap_space =          { .const, .param };"
              ),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(
                InstructionForm(
                  "prefetch",
                  Col(OperandConstraint.Address),
                  Col(_root_.scala.collection.immutable.Set("L1", "L2")),
                  _root_.scala.collection.immutable.Set("global", "local"),
                  false
                ),
                InstructionForm(
                  "prefetch",
                  Col(OperandConstraint.Address),
                  Col(
                    _root_.scala.collection.immutable.Set("global"),
                    _root_.scala.collection.immutable.Set("L2::evict_last", "L2::evict_normal")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "prefetch",
                  Col(OperandConstraint.Address),
                  Col(_root_.scala.collection.immutable.Set("tensormap")),
                  _root_.scala.collection.immutable.Set("const", "param"),
                  false
                )
              )
            )
          ))
      )
  case Prefetchu extends InstructionDef(
        "prefetchu",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 1) Right(Instruction.Prefetchu(operands(0), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("prefetchu", Col(1), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-prefetch-prefetchu",
              "9.7.9.16. Data Movement and Conversion Instructions: prefetch, prefetchu",
              "prefetch, prefetchu",
              "Data Movement and Conversion Instructions",
              Col(
                "prefetch{.space}.level                    [a];   // prefetch to data cache\nprefetch.global.level::eviction_priority  [a];   // prefetch to data cache\n\nprefetchu.L1  [a];             // prefetch to uniform cache\n\nprefetch{.tensormap_space}.tensormap [a];  // prefetch the tensormap\n\n.space =                    { .global, .local };\n.level =                    { .L1, .L2 };\n.level::eviction_priority = { .L2::evict_last, .L2::evict_normal };\n.tensormap_space =          { .const, .param };"
              ),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(InstructionForm(
                "prefetchu",
                Col(OperandConstraint.Address),
                Col(_root_.scala.collection.immutable.Set("L1")),
                _root_.scala.collection.immutable.Set(),
                false
              ))
            )
          ))
      )
  case Prmt extends InstructionDef(
        "prmt",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Prmt(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("prmt", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-prmt",
              "9.7.9.7. Data Movement and Conversion Instructions: prmt",
              "prmt",
              "Data Movement and Conversion Instructions",
              Col("prmt.b32{.mode}  d, a, b, c;\n\n.mode = { .f4e, .b4e, .rc8, .ecl, .ecr, .rc16 };"),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(InstructionForm(
                "prmt",
                Col(
                  OperandConstraint.Register,
                  OperandConstraint.Value,
                  OperandConstraint.Value,
                  OperandConstraint.Value
                ),
                Col(_root_.scala.collection.immutable.Set("b32")),
                _root_.scala.collection.immutable.Set("b4e", "ecl", "ecr", "f4e", "rc16", "rc8"),
                true
              ))
            )
          ))
      )
  case Rcp extends InstructionDef(
        "rcp",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Rcp(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("rcp", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "floating-point-instructions-rcp",
              "9.7.3.13. Floating Point Instructions: rcp",
              "rcp",
              "Floating Point Instructions",
              Col(
                "rcp.approx{.ftz}.f32  d, a;  // fast, approximate reciprocal\nrcp.rnd{.ftz}.f32     d, a;  // IEEE 754 compliant rounding\nrcp.rnd.f64           d, a;  // IEEE 754 compliant rounding\n\n.rnd = { .rn, .rz, .rm, .rp };"
              ),
              PTXVersion(1, 0),
              SMTarget(13),
              Col(
                InstructionForm(
                  "rcp",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("f32")),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "rcp",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "rcp",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"),
                    _root_.scala.collection.immutable.Set("f64")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case RcpApproxFtzF64 extends InstructionDef(
        "rcp.approx.ftz.f64",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.RcpApproxFtzF64(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("rcp.approx.ftz.f64", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "floating-point-instructions-rcp-approx-ftz-f64",
              "9.7.3.14. Floating Point Instructions: rcp.approx.ftz.f64",
              "rcp.approx.ftz.f64",
              "Floating Point Instructions",
              Col("rcp.approx.ftz.f64  d, a;"),
              PTXVersion(1, 0),
              SMTarget(20),
              Col(InstructionForm(
                "rcp.approx.ftz.f64",
                Col(OperandConstraint.Register, OperandConstraint.Value),
                Col(),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Red extends InstructionDef(
        "red",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2 || operands.size == 3)
            Right(Instruction.Red(operands(0), operands(1), operands.lift(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("red", Col(2, 3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-red",
              "9.7.14.6. Parallel Synchronization and Communication Instructions: red",
              "red",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "red{.sem}{.scope}{.space}.op{.level::cache_hint}.type          [a], b{, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16    [a], b{, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16x2  [a], b{, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16\n                                                      [a], b {, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16x2\n                                                      [a], b {, cache_policy};\n\n.space =              { .global, .shared{::cta, ::cluster} };\n.sem =                { .relaxed, .release };\n.scope =              { .cta, .cluster, .gpu, .sys };\n\n.op =                 { .and, .or, .xor,\n                        .add, .inc, .dec,\n                        .min, .max };\n.level::cache_hint =  { .L2::cache_hint };\n.type =               { .b32, .b64, .u32, .u64, .s32, .s64, .f32, .f64 };",
                "red{.sem}{.scope}{.global}.add{.level::cache_hint}.vec_32_bit.f32 [a], b{, cache_policy};\nred{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}. vec_16_bit.half_word_type [a], b{, cache_policy};\nred{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}.vec_32_bit.packed_type [a], b {, cache_policy};\n\n.sem =                { .relaxed, .release };\n.scope =              { .cta, .cluster, .gpu, .sys };\n.op =                 { .add, .min, .max };\n.half_word_type =     { .f16, .bf16 };\n.packed_type =        { .f16x2,.bf16x2 };\n.vec_16_bit =         { .v2, .v4, .v8 }\n.vec_32_bit =         { .v2, .v4 };\n.level::cache_hint =  { .L2::cache_hint }"
              ),
              PTXVersion(1, 2),
              SMTarget(11),
              Col(
                InstructionForm(
                  "red",
                  Col(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("add", "and", "dec", "inc", "max", "min", "or", "xor"),
                    _root_.scala.collection.immutable.Set("b32", "b64", "f32", "f64", "s32", "s64", "u32", "u64")
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L2::cache_hint",
                    "cluster",
                    "cta",
                    "global",
                    "gpu",
                    "relaxed",
                    "release",
                    "sys"
                  ),
                  false
                ),
                InstructionForm(
                  "red",
                  Col(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("add"),
                    _root_.scala.collection.immutable.Set("noftz"),
                    _root_.scala.collection.immutable.Set("f16")
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L2::cache_hint",
                    "cluster",
                    "cta",
                    "global",
                    "gpu",
                    "relaxed",
                    "release",
                    "sys"
                  ),
                  false
                ),
                InstructionForm(
                  "red",
                  Col(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("add"),
                    _root_.scala.collection.immutable.Set("noftz"),
                    _root_.scala.collection.immutable.Set("f16x2")
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L2::cache_hint",
                    "cluster",
                    "cta",
                    "global",
                    "gpu",
                    "relaxed",
                    "release",
                    "sys"
                  ),
                  false
                ),
                InstructionForm(
                  "red",
                  Col(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("add"),
                    _root_.scala.collection.immutable.Set("noftz"),
                    _root_.scala.collection.immutable.Set("bf16")
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L2::cache_hint",
                    "cluster",
                    "cta",
                    "global",
                    "gpu",
                    "relaxed",
                    "release",
                    "sys"
                  ),
                  false
                ),
                InstructionForm(
                  "red",
                  Col(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("add"),
                    _root_.scala.collection.immutable.Set("noftz"),
                    _root_.scala.collection.immutable.Set("bf16x2")
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L2::cache_hint",
                    "cluster",
                    "cta",
                    "global",
                    "gpu",
                    "relaxed",
                    "release",
                    "sys"
                  ),
                  false
                ),
                InstructionForm(
                  "red",
                  Col(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("add"),
                    _root_.scala.collection.immutable.Set("v2", "v4"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L2::cache_hint",
                    "cluster",
                    "cta",
                    "global",
                    "gpu",
                    "relaxed",
                    "release",
                    "sys"
                  ),
                  false
                ),
                InstructionForm(
                  "red",
                  Col(OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("add", "max", "min"),
                    _root_.scala.collection.immutable.Set("noftz")
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L2::cache_hint",
                    "cluster",
                    "cta",
                    "global",
                    "gpu",
                    "relaxed",
                    "release",
                    "sys"
                  ),
                  false
                ),
                InstructionForm(
                  "red",
                  Col(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("add", "max", "min"),
                    _root_.scala.collection.immutable.Set("noftz"),
                    _root_.scala.collection.immutable.Set("v2", "v4"),
                    _root_.scala.collection.immutable.Set("bf16x2", "f16x2")
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L2::cache_hint",
                    "cluster",
                    "cta",
                    "global",
                    "gpu",
                    "relaxed",
                    "release",
                    "sys"
                  ),
                  false
                )
              )
            )
          ))
      )
  case RedAsync extends InstructionDef(
        "red.async",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2 || operands.size == 3)
            Right(Instruction.RedAsync(operands(0), operands(1), operands.lift(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("red.async", Col(2, 3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-red-async",
              "9.7.14.7. Parallel Synchronization and Communication Instructions: red.async",
              "red.async",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "// Increment and Decrement reductions\nred.async.sem.scope{.ss}.completion_mechanism.op.type [a], b, [mbar];\n\n.sem  =                 { .relaxed };\n.scope =                { .cluster };\n.ss   =                 { .shared::cluster };\n.op   =                 { .inc, .dec };\n.type =                 { .u32 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\n\n// MIN and MAX reductions\nred.async.sem.scope{.ss}.completion_mechanism.op.type [a], b, [mbar];\n\n.sem  = { .relaxed };\n.scope = { .cluster };\n.ss   = { .shared::cluster };\n.op   = { .min, .max };\n.type = { .u32, .s32 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\n// Bitwise AND, OR and XOR reductions\nred.async.sem.scope{.ss}.completion_mechanism.op.type [a], b, [mbar];\n\n.sem  = { .relaxed };\n.scope = { .cluster };\n.ss   = { .shared::cluster };\n.op   = { .and, .or, .xor };\n.type = { .b32 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\n// ADD reductions\nred.async.sem.scope{.ss}.completion_mechanism.add.type [a], b, [mbar];\n\n.sem  = { .relaxed };\n.scope = { .cluster };\n.ss   = { .shared::cluster };\n.type = { .u32, .s32, .u64 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\nred.async{.mmio}.sem.scope{.ss}.add.type [a], b;\n\n.sem  = { .release };\n.scope = { .gpu, .sys };\n.ss   = { .global };\n.type = { .u32, .s32, .u64, .s64 };"
              ),
              PTXVersion(8, 1),
              SMTarget(90),
              Col(
                InstructionForm(
                  "red.async",
                  Col(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
                  Col(
                    _root_.scala.collection.immutable.Set("release"),
                    _root_.scala.collection.immutable.Set("gpu", "sys"),
                    _root_.scala.collection.immutable.Set("mbarrier::complete_tx::bytes"),
                    _root_.scala.collection.immutable.Set("and", "or", "xor"),
                    _root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")
                  ),
                  _root_.scala.collection.immutable.Set("global"),
                  false
                ),
                InstructionForm(
                  "red.async",
                  Col(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
                  Col(
                    _root_.scala.collection.immutable.Set("release"),
                    _root_.scala.collection.immutable.Set("gpu", "sys"),
                    _root_.scala.collection.immutable.Set("mbarrier::complete_tx::bytes"),
                    _root_.scala.collection.immutable.Set("add"),
                    _root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")
                  ),
                  _root_.scala.collection.immutable.Set("global"),
                  false
                ),
                InstructionForm(
                  "red.async",
                  Col(OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("release"),
                    _root_.scala.collection.immutable.Set("gpu", "sys"),
                    _root_.scala.collection.immutable.Set("add"),
                    _root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")
                  ),
                  _root_.scala.collection.immutable.Set("global", "mmio"),
                  false
                )
              )
            )
          ))
      )
  case ReduxSync extends InstructionDef(
        "redux.sync",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.ReduxSync(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("redux.sync", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-redux-sync",
              "9.7.14.13. Parallel Synchronization and Communication Instructions: redux.sync",
              "redux.sync",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "redux.sync.op.type dst, src, membermask;\n.op   = {.add, .min, .max}\n.type = {.u32, .s32}\n\nredux.sync.op.b32 dst, src, membermask;\n.op   = {.and, .or, .xor}\n\nredux.sync.op{.abs.}{.NaN}.f32 dst, src, membermask;\n.op   = { .min, .max }"
              ),
              PTXVersion(7, 0),
              SMTarget(80),
              Col(
                InstructionForm(
                  "redux.sync",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("max", "min"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "redux.sync",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("max", "min"),
                    _root_.scala.collection.immutable.Set("b32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "redux.sync",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("max", "min"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("NaN", "abs"),
                  false
                )
              )
            )
          ))
      )
  case Rem extends InstructionDef(
        "rem",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.Rem(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("rem", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-rem",
              "9.7.1.10. Integer Arithmetic Instructions: rem",
              "rem",
              "Integer Arithmetic Instructions",
              Col("rem.type  d, a, b;\n\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm(
                "rem",
                Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("s16", "s32", "s64", "u16", "u32", "u64")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Ret extends InstructionDef(
        "ret",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 0) Right(Instruction.Ret(mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("ret", Col(0), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "control-flow-instructions-ret",
              "9.7.13.6. Control Flow Instructions: ret",
              "ret",
              "Control Flow Instructions",
              Col("ret{.uni};"),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm("ret", Col(), Col(), _root_.scala.collection.immutable.Set("uni"), true))
            )
          ))
      )
  case Rsqrt extends InstructionDef(
        "rsqrt",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Rsqrt(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("rsqrt", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "floating-point-instructions-rsqrt",
              "9.7.3.16. Floating Point Instructions: rsqrt",
              "rsqrt",
              "Floating Point Instructions",
              Col("rsqrt.approx{.ftz}.f32  d, a;\nrsqrt.approx.f64        d, a;"),
              PTXVersion(1, 0),
              SMTarget(13),
              Col(
                InstructionForm(
                  "rsqrt",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("f32")),
                  _root_.scala.collection.immutable.Set("ftz"),
                  true
                ),
                InstructionForm(
                  "rsqrt",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("f64")),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case RsqrtApproxFtzF64 extends InstructionDef(
        "rsqrt.approx.ftz.f64",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.RsqrtApproxFtzF64(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("rsqrt.approx.ftz.f64", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "floating-point-instructions-rsqrt-approx-ftz-f64",
              "9.7.3.17. Floating Point Instructions: rsqrt.approx.ftz.f64",
              "rsqrt.approx.ftz.f64",
              "Floating Point Instructions",
              Col("rsqrt.approx.ftz.f64 d, a;"),
              PTXVersion(1, 0),
              SMTarget(20),
              Col(InstructionForm(
                "rsqrt.approx.ftz.f64",
                Col(OperandConstraint.Register, OperandConstraint.Value),
                Col(),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Sad extends InstructionDef(
        "sad",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4) Right(Instruction.Sad(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("sad", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-sad",
              "9.7.1.8. Integer Arithmetic Instructions: sad",
              "sad",
              "Integer Arithmetic Instructions",
              Col("sad.type  d, a, b, c;\n\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm(
                "sad",
                Col(
                  OperandConstraint.Register,
                  OperandConstraint.Value,
                  OperandConstraint.Value,
                  OperandConstraint.Value
                ),
                Col(_root_.scala.collection.immutable.Set("s16", "s32", "s64", "u16", "u32", "u64")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Selp extends InstructionDef(
        "selp",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Selp(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("selp", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "comparison-and-selection-instructions-selp",
              "9.7.6.3. Comparison and Selection Instructions: selp",
              "selp",
              "Comparison and Selection Instructions",
              Col(
                "selp.type d, a, b, c;\n\n.type = { .b16, .b32, .b64,\n          .u16, .u32, .u64,\n          .s16, .s32, .s64,\n                .f32, .f64 };"
              ),
              PTXVersion(1, 0),
              SMTarget(13),
              Col(InstructionForm(
                "selp",
                Col(
                  OperandConstraint.Register,
                  OperandConstraint.Value,
                  OperandConstraint.Value,
                  OperandConstraint.Value
                ),
                Col(_root_.scala.collection.immutable.Set(
                  "b16",
                  "b32",
                  "b64",
                  "f32",
                  "f64",
                  "s16",
                  "s32",
                  "s64",
                  "u16",
                  "u32",
                  "u64"
                )),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Set extends InstructionDef(
        "set",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3 || operands.size == 4)
            Right(Instruction.Set(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("set", Col(3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "comparison-and-selection-instructions-set",
              "9.7.6.1. Comparison and Selection Instructions: set",
              "set",
              "Comparison and Selection Instructions",
              Col(
                "set.CmpOp{.ftz}.dtype.stype         d, a, b;\nset.CmpOp.BoolOp{.ftz}.dtype.stype  d, a, b, {!}c;\n\n.CmpOp  = { eq, ne, lt, le, gt, ge, lo, ls, hi, hs,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };\n.dtype  = { .u32, .s32, .f32 };\n.stype  = { .b16, .b32, .b64,\n            .u16, .u32, .u64,\n            .s16, .s32, .s64,\n                  .f32, .f64 };"
              ),
              PTXVersion(1, 0),
              SMTarget(13),
              Col(
                InstructionForm(
                  "set",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "hi",
                      "hs",
                      "le",
                      "leu",
                      "lo",
                      "ls",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("f32", "s32", "u32"),
                    _root_.scala.collection.immutable.Set(
                      "b16",
                      "b32",
                      "b64",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "u16",
                      "u32",
                      "u64"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("ftz"),
                  true
                ),
                InstructionForm(
                  "set",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "hi",
                      "hs",
                      "le",
                      "leu",
                      "lo",
                      "ls",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("and", "or", "xor"),
                    _root_.scala.collection.immutable.Set("f32", "s32", "u32"),
                    _root_.scala.collection.immutable.Set(
                      "b16",
                      "b32",
                      "b64",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "u16",
                      "u32",
                      "u64"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("ftz"),
                  true
                )
              )
            ),
            Metadata.Entry(
              "half-precision-comparison-instructions-set",
              "9.7.7.1. Half Precision Comparison Instructions: set",
              "set",
              "Half Precision Comparison Instructions",
              Col(
                "set.CmpOp{.ftz}.f16.stype            d, a, b;\nset.CmpOp.BoolOp{.ftz}.f16.stype     d, a, b, {!}c;\n\nset.CmpOp.bf16.stype                 d, a, b;\nset.CmpOp.BoolOp.bf16.stype          d, a, b, {!}c;\n\nset.CmpOp{.ftz}.dtype.f16            d, a, b;\nset.CmpOp.BoolOp{.ftz}.dtype.f16     d, a, b, {!}c;\n.dtype  = { .u16, .s16, .u32, .s32}\n\nset.CmpOp.dtype.bf16                 d, a, b;\nset.CmpOp.BoolOp.dtype.bf16          d, a, b, {!}c;\n.dtype  = { .u16, .s16, .u32, .s32}\n\nset.CmpOp{.ftz}.dtype.f16x2          d, a, b;\nset.CmpOp.BoolOp{.ftz}.dtype.f16x2   d, a, b, {!}c;\n.dtype  = { .f16x2, .u32, .s32}\n\nset.CmpOp.dtype.bf16x2               d, a, b;\nset.CmpOp.BoolOp.dtype.bf16x2        d, a, b, {!}c;\n.dtype  = { .bf16x2, .u32, .s32}\n\n.CmpOp  = { eq, ne, lt, le, gt, ge,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };\n.stype  = { .b16, .b32, .b64,\n            .u16, .u32, .u64,\n            .s16, .s32, .s64,\n            .f16, .f32, .f64};"
              ),
              PTXVersion(4, 2),
              SMTarget(53),
              Col(
                InstructionForm(
                  "set",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "le",
                      "leu",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("f16"),
                    _root_.scala.collection.immutable.Set(
                      "b16",
                      "b32",
                      "b64",
                      "f16",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "u16",
                      "u32",
                      "u64"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "set",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "le",
                      "leu",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("and", "or", "xor"),
                    _root_.scala.collection.immutable.Set("f16"),
                    _root_.scala.collection.immutable.Set(
                      "b16",
                      "b32",
                      "b64",
                      "f16",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "u16",
                      "u32",
                      "u64"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "set",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "le",
                      "leu",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("bf16"),
                    _root_.scala.collection.immutable.Set(
                      "b16",
                      "b32",
                      "b64",
                      "f16",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "u16",
                      "u32",
                      "u64"
                    )
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "set",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "le",
                      "leu",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("and", "or", "xor"),
                    _root_.scala.collection.immutable.Set("bf16"),
                    _root_.scala.collection.immutable.Set(
                      "b16",
                      "b32",
                      "b64",
                      "f16",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "u16",
                      "u32",
                      "u64"
                    )
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "set",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "le",
                      "leu",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("bf16x2", "s32", "u32"),
                    _root_.scala.collection.immutable.Set("f16")
                  ),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "set",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "le",
                      "leu",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("and", "or", "xor"),
                    _root_.scala.collection.immutable.Set("bf16x2", "s32", "u32"),
                    _root_.scala.collection.immutable.Set("f16")
                  ),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "set",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "le",
                      "leu",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("bf16x2", "s32", "u32"),
                    _root_.scala.collection.immutable.Set("bf16")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "set",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "le",
                      "leu",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("and", "or", "xor"),
                    _root_.scala.collection.immutable.Set("bf16x2", "s32", "u32"),
                    _root_.scala.collection.immutable.Set("bf16")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "set",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "le",
                      "leu",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("bf16x2", "s32", "u32"),
                    _root_.scala.collection.immutable.Set("f16x2")
                  ),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "set",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "le",
                      "leu",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("and", "or", "xor"),
                    _root_.scala.collection.immutable.Set("bf16x2", "s32", "u32"),
                    _root_.scala.collection.immutable.Set("f16x2")
                  ),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "set",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "le",
                      "leu",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("bf16x2", "s32", "u32"),
                    _root_.scala.collection.immutable.Set("bf16x2")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "set",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "le",
                      "leu",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("and", "or", "xor"),
                    _root_.scala.collection.immutable.Set("bf16x2", "s32", "u32"),
                    _root_.scala.collection.immutable.Set("bf16x2")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case SetMaxRegisters extends InstructionDef(
        "setmaxnreg",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 1) Right(Instruction.SetMaxRegisters(operands(0), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("setmaxnreg", Col(1), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "miscellaneous-instructions-setmaxnreg",
              "9.7.20.5. Miscellaneous Instructions: setmaxnreg",
              "setmaxnreg",
              "Miscellaneous Instructions",
              Col("setmaxnreg.action.sync.aligned.u32 imm-reg-count;\n\n.action = { .inc, .dec };"),
              PTXVersion(8, 0),
              SMTarget(100),
              Col(InstructionForm(
                "setmaxnreg",
                Col(OperandConstraint.Value),
                Col(
                  _root_.scala.collection.immutable.Set("dec", "inc"),
                  _root_.scala.collection.immutable.Set("sync"),
                  _root_.scala.collection.immutable.Set("aligned"),
                  _root_.scala.collection.immutable.Set("u32")
                ),
                _root_.scala.collection.immutable.Set(),
                false
              ))
            )
          ))
      )
  case Setp extends InstructionDef(
        "setp",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3 || operands.size == 4)
            Right(Instruction.Setp(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("setp", Col(3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "comparison-and-selection-instructions-setp",
              "9.7.6.2. Comparison and Selection Instructions: setp",
              "setp",
              "Comparison and Selection Instructions",
              Col(
                "setp.CmpOp{.ftz}.type         p[|q], a, b;\nsetp.CmpOp.BoolOp{.ftz}.type  p[|q], a, b, {!}c;\n\n.CmpOp  = { eq, ne, lt, le, gt, ge, lo, ls, hi, hs,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };\n.type   = { .b16, .b32, .b64,\n            .u16, .u32, .u64,\n            .s16, .s32, .s64,\n                  .f32, .f64 };"
              ),
              PTXVersion(1, 0),
              SMTarget(13),
              Col(
                InstructionForm(
                  "setp",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "hi",
                      "hs",
                      "le",
                      "leu",
                      "lo",
                      "ls",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set(
                      "b16",
                      "b32",
                      "b64",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "u16",
                      "u32",
                      "u64"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("ftz"),
                  true
                ),
                InstructionForm(
                  "setp",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "hi",
                      "hs",
                      "le",
                      "leu",
                      "lo",
                      "ls",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("and", "or", "xor"),
                    _root_.scala.collection.immutable.Set(
                      "b16",
                      "b32",
                      "b64",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "u16",
                      "u32",
                      "u64"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("ftz"),
                  true
                )
              )
            ),
            Metadata.Entry(
              "half-precision-comparison-instructions-setp",
              "9.7.7.2. Half Precision Comparison Instructions: setp",
              "setp",
              "Half Precision Comparison Instructions",
              Col(
                "setp.CmpOp{.ftz}.f16           p, a, b;\nsetp.CmpOp.BoolOp{.ftz}.f16    p, a, b, {!}c;\n\nsetp.CmpOp{.ftz}.f16x2         p|q, a, b;\nsetp.CmpOp.BoolOp{.ftz}.f16x2  p|q, a, b, {!}c;\n\nsetp.CmpOp.bf16                p, a, b;\nsetp.CmpOp.BoolOp.bf16         p, a, b, {!}c;\n\nsetp.CmpOp.bf16x2              p|q, a, b;\nsetp.CmpOp.BoolOp.bf16x2       p|q, a, b, {!}c;\n\n.CmpOp  = { eq, ne, lt, le, gt, ge,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };"
              ),
              PTXVersion(4, 2),
              SMTarget(53),
              Col(
                InstructionForm(
                  "setp",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "le",
                      "leu",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("f16")
                  ),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "setp",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "le",
                      "leu",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("and", "or", "xor"),
                    _root_.scala.collection.immutable.Set("f16")
                  ),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "setp",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "le",
                      "leu",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("f16x2")
                  ),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "setp",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "le",
                      "leu",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("and", "or", "xor"),
                    _root_.scala.collection.immutable.Set("f16x2")
                  ),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "setp",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "le",
                      "leu",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("bf16")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "setp",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "le",
                      "leu",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("and", "or", "xor"),
                    _root_.scala.collection.immutable.Set("bf16")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "setp",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "le",
                      "leu",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("bf16x2")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "setp",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "eq",
                      "equ",
                      "ge",
                      "geu",
                      "gt",
                      "gtu",
                      "le",
                      "leu",
                      "lt",
                      "ltu",
                      "nan",
                      "ne",
                      "neu",
                      "num"
                    ),
                    _root_.scala.collection.immutable.Set("and", "or", "xor"),
                    _root_.scala.collection.immutable.Set("bf16x2")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case Shf extends InstructionDef(
        "shf",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4) Right(Instruction.Shf(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("shf", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "logic-and-shift-instructions-shf",
              "9.7.8.7. Logic and Shift Instructions: shf",
              "shf",
              "Logic and Shift Instructions",
              Col(
                "shf.l.mode.b32  d, a, b, c;  // left shift\nshf.r.mode.b32  d, a, b, c;  // right shift\n\n.mode = { .clamp, .wrap };"
              ),
              PTXVersion(3, 1),
              SMTarget(32),
              Col(
                InstructionForm(
                  "shf",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("l"),
                    _root_.scala.collection.immutable.Set("clamp", "wrap"),
                    _root_.scala.collection.immutable.Set("b32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ),
                InstructionForm(
                  "shf",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("r"),
                    _root_.scala.collection.immutable.Set("clamp", "wrap"),
                    _root_.scala.collection.immutable.Set("b32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case Shfl extends InstructionDef(
        "shfl",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Shfl(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("shfl", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-shfl",
              "9.7.9.5. Data Movement and Conversion Instructions: shfl (deprecated)",
              "shfl (deprecated)",
              "Data Movement and Conversion Instructions",
              Col("shfl.mode.b32  d[|p], a, b, c;\n\n.mode = { .up, .down, .bfly, .idx };"),
              PTXVersion(3, 0),
              SMTarget(30),
              Col(InstructionForm(
                "shfl",
                Col(
                  OperandConstraint.Register,
                  OperandConstraint.Value,
                  OperandConstraint.Value,
                  OperandConstraint.Value
                ),
                Col(
                  _root_.scala.collection.immutable.Set("bfly", "down", "idx", "up"),
                  _root_.scala.collection.immutable.Set("b32")
                ),
                _root_.scala.collection.immutable.Set(),
                false
              ))
            )
          ))
      )
  case ShflSync extends InstructionDef(
        "shfl.sync",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 5)
            Right(Instruction.ShflSync(operands(0), operands(1), operands(2), operands(3), operands(4), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("shfl.sync", Col(5), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-shfl-sync",
              "9.7.9.6. Data Movement and Conversion Instructions: shfl.sync",
              "shfl.sync",
              "Data Movement and Conversion Instructions",
              Col("shfl.sync.mode.b32  d[|p], a, b, c, membermask;\n\n.mode = { .up, .down, .bfly, .idx };"),
              PTXVersion(6, 0),
              SMTarget(30),
              Col(InstructionForm(
                "shfl.sync",
                Col(
                  OperandConstraint.Register,
                  OperandConstraint.Value,
                  OperandConstraint.Value,
                  OperandConstraint.Value,
                  OperandConstraint.Value
                ),
                Col(
                  _root_.scala.collection.immutable.Set("bfly", "down", "idx", "up"),
                  _root_.scala.collection.immutable.Set("b32")
                ),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Shl extends InstructionDef(
        "shl",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.Shl(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("shl", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "logic-and-shift-instructions-shl",
              "9.7.8.8. Logic and Shift Instructions: shl",
              "shl",
              "Logic and Shift Instructions",
              Col("shl.type d, a, b;\n\n.type = { .b16, .b32, .b64 };"),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm(
                "shl",
                Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("b16", "b32", "b64")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Shr extends InstructionDef(
        "shr",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.Shr(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("shr", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "logic-and-shift-instructions-shr",
              "9.7.8.9. Logic and Shift Instructions: shr",
              "shr",
              "Logic and Shift Instructions",
              Col(
                "shr.type d, a, b;\n\n.type = { .b16, .b32, .b64,\n          .u16, .u32, .u64,\n          .s16, .s32, .s64 };"
              ),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm(
                "shr",
                Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set(
                  "b16",
                  "b32",
                  "b64",
                  "s16",
                  "s32",
                  "s64",
                  "u16",
                  "u32",
                  "u64"
                )),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Sin extends InstructionDef(
        "sin",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Sin(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("sin", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "floating-point-instructions-sin",
              "9.7.3.18. Floating Point Instructions: sin",
              "sin",
              "Floating Point Instructions",
              Col("sin.approx{.ftz}.f32  d, a;"),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm(
                "sin",
                Col(OperandConstraint.Register, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("f32")),
                _root_.scala.collection.immutable.Set("ftz"),
                true
              ))
            )
          ))
      )
  case Slct extends InstructionDef(
        "slct",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Slct(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("slct", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "comparison-and-selection-instructions-slct",
              "9.7.6.4. Comparison and Selection Instructions: slct",
              "slct",
              "Comparison and Selection Instructions",
              Col(
                "slct.dtype.s32        d, a, b, c;\nslct{.ftz}.dtype.f32  d, a, b, c;\n\n.dtype = { .b16, .b32, .b64,\n           .u16, .u32, .u64,\n           .s16, .s32, .s64,\n                 .f32, .f64 };"
              ),
              PTXVersion(1, 0),
              SMTarget(13),
              Col(
                InstructionForm(
                  "slct",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "b16",
                      "b32",
                      "b64",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "u16",
                      "u32",
                      "u64"
                    ),
                    _root_.scala.collection.immutable.Set("s32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ),
                InstructionForm(
                  "slct",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "b16",
                      "b32",
                      "b64",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "u16",
                      "u32",
                      "u64"
                    ),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("ftz"),
                  true
                )
              )
            )
          ))
      )
  case Sqrt extends InstructionDef(
        "sqrt",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Sqrt(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("sqrt", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "floating-point-instructions-sqrt",
              "9.7.3.15. Floating Point Instructions: sqrt",
              "sqrt",
              "Floating Point Instructions",
              Col(
                "sqrt.approx{.ftz}.f32  d, a; // fast, approximate square root\nsqrt.rnd{.ftz}.f32     d, a; // IEEE 754 compliant rounding\nsqrt.rnd.f64           d, a; // IEEE 754 compliant rounding\n\n.rnd = { .rn, .rz, .rm, .rp };"
              ),
              PTXVersion(1, 0),
              SMTarget(13),
              Col(
                InstructionForm(
                  "sqrt",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("f32")),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "sqrt",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("ftz"),
                  false
                ),
                InstructionForm(
                  "sqrt",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"),
                    _root_.scala.collection.immutable.Set("f64")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case St extends InstructionDef(
        "st",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2 || operands.size == 3)
            Right(Instruction.St(operands(0), operands(1), operands.lift(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("st", Col(2, 3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-st",
              "9.7.9.11. Data Movement and Conversion Instructions: st",
              "st",
              "Data Movement and Conversion Instructions",
              Col(
                "st{.weak}{.ss}{.cop}{.level::cache_hint}{.vec}.type   [a], b{, cache_policy};\nst{.weak}{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.vec}.type\n                                                      [a], b{, cache_policy};\nst.volatile{.ss}{.vec}.type                           [a], b;\nst.relaxed.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.vec}.type\n                                                      [a], b{, cache_policy};\nst.release.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.vec}.type\n                                                      [a], b{, cache_policy};\nst.mmio.sem.sys{.global}.type         [a], b;\n\n.ss =                       { .global, .local, .param{::func}, .shared{::cta, ::cluster} };\n.sem =                      { .release, .relaxed };\n.level1::eviction_priority = { .L1::evict_normal, .L1::evict_unchanged,\n                               .L1::evict_first, .L1::evict_last, .L1::no_allocate };\n.level2::eviction_priority = { .L2::evict_normal, .L2::evict_first, .L2::evict_last };\n.level::cache_hint =        { .L2::cache_hint };\n.cop =                      { .wb, .cg, .cs, .wt };\n.sem =                      { .relaxed, .release };\n.scope =                    { .cta, .cluster, .gpu, .sys };\n.vec =                      { .v2, .v4, .v8 };\n.type =                     { .b8, .b16, .b32, .b64, .b128,\n                              .u8, .u16, .u32, .u64,\n                              .s8, .s16, .s32, .s64,\n                              .f32, .f64 };"
              ),
              PTXVersion(1, 0),
              SMTarget(13),
              Col(
                InstructionForm(
                  "st",
                  Col(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set(
                    "b128",
                    "b16",
                    "b32",
                    "b64",
                    "b8",
                    "f32",
                    "f64",
                    "s16",
                    "s32",
                    "s64",
                    "s8",
                    "u16",
                    "u32",
                    "u64",
                    "u8"
                  )),
                  _root_.scala.collection.immutable.Set(
                    "L2::cache_hint",
                    "cg",
                    "cs",
                    "global",
                    "local",
                    "v2",
                    "v4",
                    "v8",
                    "wb",
                    "weak",
                    "wt"
                  ),
                  false
                ),
                InstructionForm(
                  "st",
                  Col(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set(
                    "b128",
                    "b16",
                    "b32",
                    "b64",
                    "b8",
                    "f32",
                    "f64",
                    "s16",
                    "s32",
                    "s64",
                    "s8",
                    "u16",
                    "u32",
                    "u64",
                    "u8"
                  )),
                  _root_.scala.collection.immutable.Set(
                    "L1::evict_first",
                    "L1::evict_last",
                    "L1::evict_normal",
                    "L1::evict_unchanged",
                    "L1::no_allocate",
                    "L2::cache_hint",
                    "L2::evict_first",
                    "L2::evict_last",
                    "L2::evict_normal",
                    "global",
                    "local",
                    "v2",
                    "v4",
                    "v8",
                    "weak"
                  ),
                  false
                ),
                InstructionForm(
                  "st",
                  Col(OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("volatile"),
                    _root_.scala.collection.immutable.Set(
                      "b128",
                      "b16",
                      "b32",
                      "b64",
                      "b8",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "s8",
                      "u16",
                      "u32",
                      "u64",
                      "u8"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("global", "local", "v2", "v4", "v8"),
                  false
                ),
                InstructionForm(
                  "st",
                  Col(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("relaxed"),
                    _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"),
                    _root_.scala.collection.immutable.Set(
                      "b128",
                      "b16",
                      "b32",
                      "b64",
                      "b8",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "s8",
                      "u16",
                      "u32",
                      "u64",
                      "u8"
                    )
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L1::evict_first",
                    "L1::evict_last",
                    "L1::evict_normal",
                    "L1::evict_unchanged",
                    "L1::no_allocate",
                    "L2::cache_hint",
                    "L2::evict_first",
                    "L2::evict_last",
                    "L2::evict_normal",
                    "global",
                    "local",
                    "v2",
                    "v4",
                    "v8"
                  ),
                  false
                ),
                InstructionForm(
                  "st",
                  Col(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("release"),
                    _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"),
                    _root_.scala.collection.immutable.Set(
                      "b128",
                      "b16",
                      "b32",
                      "b64",
                      "b8",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "s8",
                      "u16",
                      "u32",
                      "u64",
                      "u8"
                    )
                  ),
                  _root_.scala.collection.immutable.Set(
                    "L1::evict_first",
                    "L1::evict_last",
                    "L1::evict_normal",
                    "L1::evict_unchanged",
                    "L1::no_allocate",
                    "L2::cache_hint",
                    "L2::evict_first",
                    "L2::evict_last",
                    "L2::evict_normal",
                    "global",
                    "local",
                    "v2",
                    "v4",
                    "v8"
                  ),
                  false
                ),
                InstructionForm(
                  "st",
                  Col(OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("mmio"),
                    _root_.scala.collection.immutable.Set("relaxed", "release"),
                    _root_.scala.collection.immutable.Set("sys"),
                    _root_.scala.collection.immutable.Set(
                      "b128",
                      "b16",
                      "b32",
                      "b64",
                      "b8",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "s8",
                      "u16",
                      "u32",
                      "u64",
                      "u8"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("global"),
                  false
                )
              )
            )
          ))
      )
  case StAsync extends InstructionDef(
        "st.async",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2 || operands.size == 3)
            Right(Instruction.StAsync(operands(0), operands(1), operands.lift(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("st.async", Col(2, 3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-st-async",
              "9.7.9.12. Data Movement and Conversion Instructions: st.async",
              "st.async",
              "Data Movement and Conversion Instructions",
              Col(
                "st.async{.weak}{.ss}.completion_mechanism{.vec}.type [a], b, [mbar];\n\n.ss   =                 { .shared::cluster };\n.type =                 { .b32, .b64, .b128,\n                          .u32, .u64,\n                          .s32, .s64,\n                          .f32, .f64 };\n.vec  =                 { .v2, .v4 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\nst.async{.mmio}.sem.scope{.ss}.type [a], b;\n\n.sem =                  { .release };\n.scope =                { .gpu, .sys };\n.ss =                   { .global };\n.type =                 { .b8, .b16, .b32, .b64,\n                          .u8, .u16, .u32, .u64,\n                          .s8, .s16, .s32, .s64,\n                                     .f32, .f64 };"
              ),
              PTXVersion(8, 1),
              SMTarget(90),
              Col(
                InstructionForm(
                  "st.async",
                  Col(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
                  Col(
                    _root_.scala.collection.immutable.Set("mbarrier::complete_tx::bytes"),
                    _root_.scala.collection.immutable.Set(
                      "b16",
                      "b32",
                      "b64",
                      "b8",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "s8",
                      "u16",
                      "u32",
                      "u64",
                      "u8"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("global", "v2", "v4", "weak"),
                  false
                ),
                InstructionForm(
                  "st.async",
                  Col(OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("release"),
                    _root_.scala.collection.immutable.Set("gpu", "sys"),
                    _root_.scala.collection.immutable.Set(
                      "b16",
                      "b32",
                      "b64",
                      "b8",
                      "f32",
                      "f64",
                      "s16",
                      "s32",
                      "s64",
                      "s8",
                      "u16",
                      "u32",
                      "u64",
                      "u8"
                    )
                  ),
                  _root_.scala.collection.immutable.Set("global", "mmio"),
                  false
                )
              )
            )
          ))
      )
  case StBulk extends InstructionDef(
        "st.bulk",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.StBulk(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("st.bulk", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-st-bulk",
              "9.7.9.14. Data Movement and Conversion Instructions: st.bulk",
              "st.bulk",
              "Data Movement and Conversion Instructions",
              Col("st.bulk{.weak}{.shared::cta}  [a], size, initval; // initval must be zero"),
              PTXVersion(8, 6),
              SMTarget(100),
              Col(InstructionForm(
                "st.bulk",
                Col(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                Col(),
                _root_.scala.collection.immutable.Set("shared::cta", "weak"),
                true
              ))
            )
          ))
      )
  case StackRestore extends InstructionDef(
        "stackrestore",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 1) Right(Instruction.StackRestore(operands(0), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("stackrestore", Col(1), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "stack-manipulation-instructions-stackrestore",
              "9.7.18.2. Stack Manipulation Instructions: stackrestore",
              "stackrestore",
              "Stack Manipulation Instructions",
              Col("stackrestore.type  a;\n\n.type = { .u32, .u64 };"),
              PTXVersion(7, 3),
              SMTarget(52),
              Col(InstructionForm(
                "stackrestore",
                Col(OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("u32", "u64")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case StackSave extends InstructionDef(
        "stacksave",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 1) Right(Instruction.StackSave(operands(0), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("stacksave", Col(1), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "stack-manipulation-instructions-stacksave",
              "9.7.18.1. Stack Manipulation Instructions: stacksave",
              "stacksave",
              "Stack Manipulation Instructions",
              Col("stacksave.type  d;\n\n.type = { .u32, .u64 };"),
              PTXVersion(7, 3),
              SMTarget(52),
              Col(InstructionForm(
                "stacksave",
                Col(OperandConstraint.Register),
                Col(_root_.scala.collection.immutable.Set("u32", "u64")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Sub extends InstructionDef(
        "sub",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.Sub(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("sub", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-sub",
              "9.7.1.2. Integer Arithmetic Instructions: sub",
              "sub",
              "Integer Arithmetic Instructions",
              Col(
                "sub.type1        d, a, b;\nsub{.sat}.type2  d, a, b;\n\n.type1 = { .u16, .u32, .u64,\n           .s16, .s64 };\n\n.type2 = { .s32, .u8x4, .s8x4 };"
              ),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(
                InstructionForm(
                  "sub",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("s16", "s64", "u16", "u32", "u64")),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "sub",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("s32", "s8x4", "u8x4")),
                  _root_.scala.collection.immutable.Set("sat"),
                  false
                )
              )
            ),
            Metadata.Entry(
              "floating-point-instructions-sub",
              "9.7.3.4. Floating Point Instructions: sub",
              "sub",
              "Floating Point Instructions",
              Col(
                "sub{.rnd}{.ftz}{.sat}.f32  d, a, b;\nsub{.rnd}{.ftz}.f32x2      d, a, b;\nsub{.rnd}.f64              d, a, b;\n\n.rnd = { .rn, .rz, .rm, .rp };"
              ),
              PTXVersion(1, 0),
              SMTarget(13),
              Col(
                InstructionForm(
                  "sub",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f32")),
                  _root_.scala.collection.immutable.Set("ftz", "rm", "rn", "rp", "rz", "sat"),
                  false
                ),
                InstructionForm(
                  "sub",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f32x2")),
                  _root_.scala.collection.immutable.Set("ftz", "rm", "rn", "rp", "rz"),
                  false
                ),
                InstructionForm(
                  "sub",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f64")),
                  _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"),
                  false
                )
              )
            ),
            Metadata.Entry(
              "half-precision-floating-point-instructions-sub",
              "9.7.4.2. Half Precision Floating Point Instructions: sub",
              "sub",
              "Half Precision Floating Point Instructions",
              Col(
                "sub{.rnd}{.ftz}{.sat}.f16   d, a, b;\nsub{.rnd}{.ftz}{.sat}.f16x2 d, a, b;\n\nsub{.rnd}.bf16   d, a, b;\nsub{.rnd}.bf16x2 d, a, b;\n\n.rnd = { .rn };"
              ),
              PTXVersion(4, 2),
              SMTarget(53),
              Col(
                InstructionForm(
                  "sub",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f16")),
                  _root_.scala.collection.immutable.Set("ftz", "rn", "sat"),
                  false
                ),
                InstructionForm(
                  "sub",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("f16x2")),
                  _root_.scala.collection.immutable.Set("ftz", "rn", "sat"),
                  false
                ),
                InstructionForm(
                  "sub",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("bf16")),
                  _root_.scala.collection.immutable.Set("rn"),
                  false
                ),
                InstructionForm(
                  "sub",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("bf16x2")),
                  _root_.scala.collection.immutable.Set("rn"),
                  false
                )
              )
            ),
            Metadata.Entry(
              "mixed-precision-floating-point-instructions-sub",
              "9.7.5.2. Mixed Precision Floating Point Instructions: sub",
              "sub",
              "Mixed Precision Floating Point Instructions",
              Col("sub{.rnd}{.sat}.f32.atype  d, a, c;\n\n.atype = { .f16, .bf16};\n.rnd   = { .rn, .rz, .rm, .rp };"),
              PTXVersion(1, 0),
              SMTarget(100),
              Col(InstructionForm(
                "sub",
                Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("f32"), _root_.scala.collection.immutable.Set("bf16", "f16")),
                _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz", "sat"),
                true
              ))
            )
          ))
      )
  case SubCc extends InstructionDef(
        "sub.cc",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.SubCc(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("sub.cc", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "extended-precision-arithmetic-instructions-sub-cc",
              "9.7.2.3. Extended-Precision Arithmetic Instructions: sub.cc",
              "sub.cc",
              "Extended-Precision Arithmetic Instructions",
              Col("sub.cc.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"),
              PTXVersion(1, 0),
              SMTarget(20),
              Col(InstructionForm(
                "sub.cc",
                Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Subc extends InstructionDef(
        "subc",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.Subc(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("subc", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "extended-precision-arithmetic-instructions-subc",
              "9.7.2.4. Extended-Precision Arithmetic Instructions: subc",
              "subc",
              "Extended-Precision Arithmetic Instructions",
              Col("subc{.cc}.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"),
              PTXVersion(1, 0),
              SMTarget(20),
              Col(InstructionForm(
                "subc",
                Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")),
                _root_.scala.collection.immutable.Set("cc"),
                true
              ))
            )
          ))
      )
  case Suld extends InstructionDef(
        "suld",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Suld(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("suld", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "surface-instructions-suld",
              "9.7.12.1. Surface Instructions: suld",
              "suld",
              "Surface Instructions",
              Col(
                "suld.b.geom{.cop}.vec.dtype.clamp  d, [a, b];  // unformatted\n\n.geom  = { .1d, .2d, .3d, .a1d, .a2d };\n.cop   = { .ca, .cg, .cs, .cv };               // cache operation\n.vec   = { none, .v2, .v4 };\n.dtype = { .b8 , .b16, .b32, .b64 };\n.clamp = { .trap, .clamp, .zero };"
              ),
              PTXVersion(1, 0),
              SMTarget(20),
              Col(InstructionForm(
                "suld",
                Col(OperandConstraint.Register, OperandConstraint.Address),
                Col(
                  _root_.scala.collection.immutable.Set("b"),
                  _root_.scala.collection.immutable.Set("a1d", "a2d"),
                  _root_.scala.collection.immutable.Set("none", "v2", "v4"),
                  _root_.scala.collection.immutable.Set("b16", "b32", "b64", "b8"),
                  _root_.scala.collection.immutable.Set("clamp", "trap", "zero")
                ),
                _root_.scala.collection.immutable.Set("ca", "cg", "cs", "cv"),
                true
              ))
            )
          ))
      )
  case Suq extends InstructionDef(
        "suq",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Suq(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("suq", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "surface-instructions-suq",
              "9.7.12.4. Surface Instructions: suq",
              "suq",
              "Surface Instructions",
              Col(
                "suq.query.b32   d, [a];\n\n.query = { .width, .height, .depth,\n           .channel_data_type, .channel_order,\n           .array_size, .memory_layout };"
              ),
              PTXVersion(1, 5),
              SMTarget(10),
              Col(InstructionForm(
                "suq",
                Col(OperandConstraint.Register, OperandConstraint.Address),
                Col(
                  _root_.scala.collection.immutable.Set(
                    "array_size",
                    "channel_data_type",
                    "channel_order",
                    "depth",
                    "height",
                    "memory_layout",
                    "width"
                  ),
                  _root_.scala.collection.immutable.Set("b32")
                ),
                _root_.scala.collection.immutable.Set(),
                false
              ))
            )
          ))
      )
  case Sured extends InstructionDef(
        "sured",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Sured(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("sured", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "surface-instructions-sured",
              "9.7.12.3. Surface Instructions: sured",
              "sured",
              "Surface Instructions",
              Col(
                "sured.b.op.geom.ctype.clamp  [a,b],c; // byte addressing\nsured.p.op.geom.ctype.clamp  [a,b],c; // sample addressing\n\n.op    = { .add, .min, .max, .and, .or };\n.geom  = { .1d, .2d, .3d };\n.ctype = { .u32, .u64, .s32, .b32, .s64 };  // for sured.b\n.ctype = { .b32, .b64 };                    // for sured.p\n.clamp = { .trap, .clamp, .zero };"
              ),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(
                InstructionForm(
                  "sured",
                  Col(OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("b"),
                    _root_.scala.collection.immutable.Set("add", "and", "max", "min", "or"),
                    _root_.scala.collection.immutable.Set("b32", "b64"),
                    _root_.scala.collection.immutable.Set("clamp", "trap", "zero")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "sured",
                  Col(OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("p"),
                    _root_.scala.collection.immutable.Set("add", "and", "max", "min", "or"),
                    _root_.scala.collection.immutable.Set("b32", "b64"),
                    _root_.scala.collection.immutable.Set("clamp", "trap", "zero")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case Sust extends InstructionDef(
        "sust",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Sust(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("sust", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "surface-instructions-sust",
              "9.7.12.2. Surface Instructions: sust",
              "sust",
              "Surface Instructions",
              Col(
                "sust.b.{1d,2d,3d}{.cop}.vec.ctype.clamp  [a, b], c;  // unformatted\nsust.p.{1d,2d,3d}.vec.b32.clamp          [a, b], c;  // formatted\n\nsust.b.{a1d,a2d}{.cop}.vec.ctype.clamp   [a, b], c;  // unformatted\n\n.cop   = { .wb, .cg, .cs, .wt };                     // cache operation\n.vec   = { none, .v2, .v4 };\n.ctype = { .b8 , .b16, .b32, .b64 };\n.clamp = { .trap, .clamp, .zero };"
              ),
              PTXVersion(1, 0),
              SMTarget(20),
              Col(
                InstructionForm(
                  "sust",
                  Col(OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("b"),
                    _root_.scala.collection.immutable.Set("none", "v2", "v4"),
                    _root_.scala.collection.immutable.Set("b16", "b32", "b64", "b8"),
                    _root_.scala.collection.immutable.Set("clamp", "trap", "zero")
                  ),
                  _root_.scala.collection.immutable.Set("cg", "cs", "wb", "wt"),
                  false
                ),
                InstructionForm(
                  "sust",
                  Col(OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("p"),
                    _root_.scala.collection.immutable.Set("none", "v2", "v4"),
                    _root_.scala.collection.immutable.Set("b32"),
                    _root_.scala.collection.immutable.Set("clamp", "trap", "zero")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case Szext extends InstructionDef(
        "szext",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.Szext(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("szext", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "integer-arithmetic-instructions-szext",
              "9.7.1.22. Integer Arithmetic Instructions: szext",
              "szext",
              "Integer Arithmetic Instructions",
              Col("szext.mode.type  d, a, b;\n\n.mode = { .clamp, .wrap };\n.type = { .u32, .s32 };"),
              PTXVersion(7, 6),
              SMTarget(70),
              Col(InstructionForm(
                "szext",
                Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                Col(
                  _root_.scala.collection.immutable.Set("clamp", "wrap"),
                  _root_.scala.collection.immutable.Set("s32", "u32")
                ),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Tanh extends InstructionDef(
        "tanh",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Tanh(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("tanh", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "floating-point-instructions-tanh",
              "9.7.3.22. Floating Point Instructions: tanh",
              "tanh",
              "Floating Point Instructions",
              Col("tanh.approx.f32 d, a;"),
              PTXVersion(7, 0),
              SMTarget(75),
              Col(InstructionForm(
                "tanh",
                Col(OperandConstraint.Register, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("f32")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            ),
            Metadata.Entry(
              "half-precision-floating-point-instructions-tanh",
              "9.7.4.9. Half Precision Floating Point Instructions: tanh",
              "tanh",
              "Half Precision Floating Point Instructions",
              Col("tanh.approx.type d, a;\n\n.type = {.f16, .f16x2, .bf16, .bf16x2}"),
              PTXVersion(7, 0),
              SMTarget(75),
              Col(InstructionForm(
                "tanh",
                Col(OperandConstraint.Register, OperandConstraint.Value),
                Col(
                  _root_.scala.collection.immutable.Set("approx"),
                  _root_.scala.collection.immutable.Set("bf16", "bf16x2", "f16", "f16x2")
                ),
                _root_.scala.collection.immutable.Set(),
                false
              ))
            )
          ))
      )
  case Tcgen05Alloc extends InstructionDef(
        "tcgen05.alloc",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Tcgen05Alloc(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("tcgen05.alloc", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "tcgen05-instructions-tcgen05-alloc-dealloc-relinquish-alloc-permit",
              "9.7.17.7.1. Tensorcore 5th Generation Instructions: tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit",
              "tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit",
              "Tensorcore 5th Generation Instructions",
              Col(
                "tcgen05.alloc.cta_group.sync.aligned{.shared::cta}.b32  [dst], nCols;\n\ntcgen05.dealloc.cta_group.sync.aligned.b32              taddr, nCols;\n\ntcgen05.relinquish_alloc_permit.cta_group.sync.aligned;\n\n.cta_group = { .cta_group::1, .cta_group::2 }"
              ),
              PTXVersion(8, 6),
              SMTarget(100),
              Col(InstructionForm(
                "tcgen05.alloc",
                Col(OperandConstraint.Address, OperandConstraint.Value),
                Col(
                  _root_.scala.collection.immutable.Set("sync"),
                  _root_.scala.collection.immutable.Set("aligned"),
                  _root_.scala.collection.immutable.Set("b32")
                ),
                _root_.scala.collection.immutable.Set("shared::cta"),
                false
              ))
            )
          ))
      )
  case Tcgen05Commit extends InstructionDef(
        "tcgen05.commit",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 1 || operands.size == 2)
            Right(Instruction.Tcgen05Commit(operands(0), operands.lift(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("tcgen05.commit", Col(1, 2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "tcgen-async-sync-operations-commit",
              "9.7.17.12.1. TensorCore 5th Generation Instructions: tcgen05.commit",
              "tcgen05.commit",
              "TensorCore 5th Generation Instructions",
              Col(
                "tcgen05.commit.cta_group.completion_mechanism{.shared::cluster}{.multicast}.b64\n                                                            [mbar] {, ctaMask};\n\n.completion_mechanism = { .mbarrier::arrive::one }\n.cta_group            = { .cta_group::1, .cta_group::2 }\n.multicast            = { .multicast::cluster }"
              ),
              PTXVersion(8, 6),
              SMTarget(100),
              Col(InstructionForm(
                "tcgen05.commit",
                Col(OperandConstraint.Address, OperandConstraint.Value),
                Col(
                  _root_.scala.collection.immutable.Set("mbarrier::arrive::one"),
                  _root_.scala.collection.immutable.Set("b64")
                ),
                _root_.scala.collection.immutable.Set("multicast::cluster", "shared::cluster"),
                false
              ))
            )
          ))
      )
  case Tcgen05Cp extends InstructionDef(
        "tcgen05.cp",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Tcgen05Cp(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("tcgen05.cp", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "tcgen05-instructions-tcgen05-cp",
              "9.7.17.9.2. Tensorcore 5th Generation Instructions: tcgen05.cp",
              "tcgen05.cp",
              "Tensorcore 5th Generation Instructions",
              Col(
                "tcgen05.cp.cta_group.shape{.multicast}{.dst_fmt.src_fmt} [taddr], s-desc;\n\n.cta_group = { .cta_group::1, .cta_group::2 }\n.src_fmt   = { .b6x16_p32 , .b4x16_p64 }\n.dst_fmt   = { .b8x16 }\n.shape     = { .128x256b, .4x256b, .128x128b, .64x128b**, .32x128b*** }\n.multicast = { .warpx2::02_13** , .warpx2::01_23**, .warpx4*** }"
              ),
              PTXVersion(8, 6),
              SMTarget(100),
              Col(InstructionForm(
                "tcgen05.cp",
                Col(OperandConstraint.Address, OperandConstraint.Value),
                Col(),
                _root_.scala.collection.immutable.Set("b4x16_p64", "b6x16_p32", "b8x16"),
                false
              ))
            )
          ))
      )
  case Tcgen05Dealloc extends InstructionDef(
        "tcgen05.dealloc",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Tcgen05Dealloc(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("tcgen05.dealloc", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "tcgen05-instructions-tcgen05-alloc-dealloc-relinquish-alloc-permit",
              "9.7.17.7.1. Tensorcore 5th Generation Instructions: tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit",
              "tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit",
              "Tensorcore 5th Generation Instructions",
              Col(
                "tcgen05.alloc.cta_group.sync.aligned{.shared::cta}.b32  [dst], nCols;\n\ntcgen05.dealloc.cta_group.sync.aligned.b32              taddr, nCols;\n\ntcgen05.relinquish_alloc_permit.cta_group.sync.aligned;\n\n.cta_group = { .cta_group::1, .cta_group::2 }"
              ),
              PTXVersion(8, 6),
              SMTarget(100),
              Col(InstructionForm(
                "tcgen05.dealloc",
                Col(OperandConstraint.Value, OperandConstraint.Value),
                Col(
                  _root_.scala.collection.immutable.Set("sync"),
                  _root_.scala.collection.immutable.Set("aligned"),
                  _root_.scala.collection.immutable.Set("b32")
                ),
                _root_.scala.collection.immutable.Set(),
                false
              ))
            )
          ))
      )
  case Tcgen05FenceAfterThreadSync extends InstructionDef(
        "tcgen05.fence::after_thread_sync",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 0) Right(Instruction.Tcgen05FenceAfterThreadSync(mods, pred))
          else
            Left(InstructionDecodeError.InvalidOperandCount("tcgen05.fence::after_thread_sync", Col(0), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "tcgen05-special-sync-operations-fence",
              "9.7.17.11.1. TensorCore 5th Generation Instructions: tcgen05.fence",
              "tcgen05.fence",
              "TensorCore 5th Generation Instructions",
              Col("tcgen05.fence::before_thread_sync ;\ntcgen05.fence::after_thread_sync  ;"),
              PTXVersion(8, 6),
              SMTarget(100),
              Col(InstructionForm(
                "tcgen05.fence::after_thread_sync",
                Col(),
                Col(),
                _root_.scala.collection.immutable.Set(),
                false
              ))
            )
          ))
      )
  case Tcgen05FenceBeforeThreadSync extends InstructionDef(
        "tcgen05.fence::before_thread_sync",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 0) Right(Instruction.Tcgen05FenceBeforeThreadSync(mods, pred))
          else
            Left(InstructionDecodeError.InvalidOperandCount("tcgen05.fence::before_thread_sync", Col(0), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "tcgen05-special-sync-operations-fence",
              "9.7.17.11.1. TensorCore 5th Generation Instructions: tcgen05.fence",
              "tcgen05.fence",
              "TensorCore 5th Generation Instructions",
              Col("tcgen05.fence::before_thread_sync ;\ntcgen05.fence::after_thread_sync  ;"),
              PTXVersion(8, 6),
              SMTarget(100),
              Col(InstructionForm(
                "tcgen05.fence::before_thread_sync",
                Col(),
                Col(),
                _root_.scala.collection.immutable.Set(),
                false
              ))
            )
          ))
      )
  case Tcgen05Ld extends InstructionDef(
        "tcgen05.ld",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2 || operands.size == 3 || operands.size == 4)
            Right(Instruction.Tcgen05Ld(operands(0), operands(1), operands.lift(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("tcgen05.ld", Col(2, 3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "tcgen05-instructions-tcgen05-ld",
              "9.7.17.8.3. Tensorcore 5th Generation Instructions: tcgen05.ld",
              "tcgen05.ld",
              "Tensorcore 5th Generation Instructions",
              Col(
                "// Base load instruction:\n\ntcgen05.ld.sync.aligned.shape1.num{.pack}.b32    r, [taddr];\n\ntcgen05.ld.sync.aligned.shape2.num{.pack}.b32    r, [taddr], immHalfSplitoff;\n\n.shape1 = { .16x64b, .16x128b, .16x256b, .32x32b }\n.shape2 = { .16x32bx2 }\n.num    = { .x1, .x2, .x4, .x8, .x16, .x32, .x64, .x128 }\n.pack   = { .pack::16b }\n\n// Floating point type load along with reduction :\n\ntcgen05.ld.red.sync.aligned.shape3.num.redOp{.abs}{.NaN}.f32 r, redval, [taddr];\n\ntcgen05.ld.red.sync.aligned.shape4.num.redOp{.abs}{.NaN}.f32 r, redval, [taddr], immHalfSplitoff;\n\n// Integer type load along with reduction :\n\ntcgen05.ld.red.sync.aligned.shape3.num.redOp.type r, redval, [taddr];\n\ntcgen05.ld.red.sync.aligned.shape4.num.redOp.type r, redval, [taddr], immHalfSplitoff;\n\n.shape3 = { .32x32b   }\n.shape4 = { .16x32bx2 }\n.redOp  = { .min, .max }\n.type   = { .u32, .s32 }"
              ),
              PTXVersion(8, 6),
              SMTarget(100),
              Col(
                InstructionForm(
                  "tcgen05.ld",
                  Col(OperandConstraint.Register, OperandConstraint.Address),
                  Col(
                    _root_.scala.collection.immutable.Set("sync"),
                    _root_.scala.collection.immutable.Set("aligned"),
                    _root_.scala.collection.immutable.Set("x1", "x128", "x16", "x2", "x32", "x4", "x64", "x8"),
                    _root_.scala.collection.immutable.Set("b32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tcgen05.ld",
                  Col(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("sync"),
                    _root_.scala.collection.immutable.Set("aligned"),
                    _root_.scala.collection.immutable.Set("x1", "x128", "x16", "x2", "x32", "x4", "x64", "x8"),
                    _root_.scala.collection.immutable.Set("b32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tcgen05.ld",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Address),
                  Col(
                    _root_.scala.collection.immutable.Set("red"),
                    _root_.scala.collection.immutable.Set("sync"),
                    _root_.scala.collection.immutable.Set("aligned"),
                    _root_.scala.collection.immutable.Set("x1", "x128", "x16", "x2", "x32", "x4", "x64", "x8"),
                    _root_.scala.collection.immutable.Set("max", "min"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("NaN", "abs"),
                  false
                ),
                InstructionForm(
                  "tcgen05.ld",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("red"),
                    _root_.scala.collection.immutable.Set("sync"),
                    _root_.scala.collection.immutable.Set("aligned"),
                    _root_.scala.collection.immutable.Set("x1", "x128", "x16", "x2", "x32", "x4", "x64", "x8"),
                    _root_.scala.collection.immutable.Set("max", "min"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set("NaN", "abs"),
                  false
                ),
                InstructionForm(
                  "tcgen05.ld",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Address),
                  Col(
                    _root_.scala.collection.immutable.Set("red"),
                    _root_.scala.collection.immutable.Set("sync"),
                    _root_.scala.collection.immutable.Set("aligned"),
                    _root_.scala.collection.immutable.Set("x1", "x128", "x16", "x2", "x32", "x4", "x64", "x8"),
                    _root_.scala.collection.immutable.Set("max", "min"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tcgen05.ld",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("red"),
                    _root_.scala.collection.immutable.Set("sync"),
                    _root_.scala.collection.immutable.Set("aligned"),
                    _root_.scala.collection.immutable.Set("x1", "x128", "x16", "x2", "x32", "x4", "x64", "x8"),
                    _root_.scala.collection.immutable.Set("max", "min"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case Tcgen05Mma extends InstructionDef(
        "tcgen05.mma",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 5 || operands.size == 6 || operands.size == 7) Right(if (operands.size == 5)
            Instruction.Tcgen05Mma(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              None,
              None,
              None,
              operands(4),
              None,
              mods,
              pred
            )
          else if (operands(4).isInstanceOf[Operand.Address]) Instruction.Tcgen05Mma(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            Some(operands(4)),
            Some(operands(5)),
            None,
            operands(6),
            None,
            mods,
            pred
          )
          else Instruction.Tcgen05Mma(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            None,
            None,
            Some(operands(4)),
            operands(5),
            operands.lift(6),
            mods,
            pred
          ))
          else Left(InstructionDecodeError.InvalidOperandCount("tcgen05.mma", Col(5, 6, 7), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "tcgen05-mma-instructions-mma",
              "9.7.17.10.9.1. TensorCore 5th Generation Instructions: tcgen05.mma",
              "tcgen05.mma",
              "TensorCore 5th Generation Instructions",
              Col(
                "// 1. Floating-point type without block scaling:\n\ntcgen05.mma.cta_group.kind   [d-tmem],  a-desc,  b-desc, idesc,\n                             { disable-output-lane }, enable-input-d {, scale-input-d};\n\ntcgen05.mma.cta_group.kind   [d-tmem], [a-tmem], b-desc, idesc,\n                             { disable-output-lane }, enable-input-d {, scale-input-d};\n\n.kind      = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n.cta_group = { .cta_group::1, .cta_group::2 }\n\n----------------------------------------------------------------------------------\n\n// 2. Floating-point type with block scaling:\n\ntcgen05.mma.cta_group.kind.block_scale{.scale_vectorsize}\n                                        [d-tmem],  a-desc,  b-desc, idesc,\n                                        [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\ntcgen05.mma.cta_group.kind.block_scale{.scale_vectorsize}\n                                        [d-tmem], [a-tmem], b-desc, idesc,\n                                        [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\n.kind = { .kind::mxf8f6f4, .kind::mxf4, .kind::mxf4nvf4 }\n.cta_group      = { .cta_group::1,   .cta_group::2 }\n.scale_vectorsize = { .scale_vec::1X, .scale_vec::2X, .scale_vec::4X, .block16, .block32 }\n\n----------------------------------------------------------------------------------\n\n// 3. Convolution MMA for floating-point type without block scaling:\n\ntcgen05.mma.cta_group.kind.collector_usage [d-tmem],  a-desc,  b-desc, idesc,\n                                           { disable-output-lane }, enable-input-d {, scale-input-d};\n\ntcgen05.mma.cta_group.kind{.ashift}.collector_usage [d-tmem], [a-tmem], b-desc, idesc,\n                                                    { disable-output-lane }, enable-input-d {, scale-input-d};\n\ntcgen05.mma.cta_group.kind.ashift{.collector_usage} [d-tmem], [a-tmem], b-desc, idesc,\n                                                    { disable-output-lane }, enable-input-d {, scale-input-d};\n\n.kind      = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n.cta_group = { .cta_group::1,   .cta_group::2 }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }\n\n----------------------------------------------------------------------------------\n\n// 4. Activation Stationary MMA for floating-point type with block scaling:\n\ntcgen05.mma.cta_group.kind.block_scale{.scale_vectorsize}.collector_usage\n                                            [d-tmem],  a-desc,  b-desc, idesc,\n                                            [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\ntcgen05.mma.cta_group.kind.block_scale{.scale_vectorsize}.collector_usage\n                                            [d-tmem], [a-tmem], b-desc, idesc,\n                                            [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\n.cta_group       = { .cta_group::1,   .cta_group::2 }\n.scale_vectorsize  = { .scale_vec::1X, .scale_vec::2X, .scale_vec::4X, .block16, .block32 }\n.kind            = { .kind::mxf8f6f4, .kind::mxf4, .kind::mxf4nvf4 }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }\n\n----------------------------------------------------------------------------------\n\n// 5. Integer type:\n\ntcgen05.mma.cta_group.kind::i8  [d-tmem],  a-desc,  b-desc, idesc,\n                                { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.cta_group.kind::i8  [d-tmem], [a-tmem], b-desc, idesc,\n                                { disable-output-lane }, enable-input-d;\n\n.cta_group = { .cta_group::1,   .cta_group::2  }\n\n----------------------------------------------------------------------------------\n\n// 6. Convolution MMA for integer type:\n\ntcgen05.mma.cta_group.kind::i8.collector_usage          [d-tmem],  a-desc,  b-desc, idesc,\n                                                        { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.cta_group.kind::i8.ashift{.collector_usage} [d-tmem], [a-tmem], b-desc, idesc,\n                                                        { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.cta_group.kind::i8{.ashift}.collector_usage [d-tmem], [a-tmem], b-desc, idesc,\n                                                        { disable-output-lane }, enable-input-d;\n\n.cta_group       = { .cta_group::1,   .cta_group::2  }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }"
              ),
              PTXVersion(8, 6),
              SMTarget(100),
              Col(
                InstructionForm(
                  "tcgen05.mma",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.VectorOperand,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4")),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.VectorOperand,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4")),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"),
                    _root_.scala.collection.immutable.Set("block_scale")
                  ),
                  _root_.scala.collection.immutable.Set("block16", "block32"),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"),
                    _root_.scala.collection.immutable.Set("block_scale")
                  ),
                  _root_.scala.collection.immutable.Set("block16", "block32"),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.VectorOperand,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"),
                    _root_.scala.collection.immutable.Set("collector::buffer::op")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.VectorOperand,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"),
                    _root_.scala.collection.immutable.Set("collector::buffer::op")
                  ),
                  _root_.scala.collection.immutable.Set("ashift"),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.VectorOperand,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"),
                    _root_.scala.collection.immutable.Set("ashift")
                  ),
                  _root_.scala.collection.immutable.Set("collector::buffer::op"),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"),
                    _root_.scala.collection.immutable.Set("block_scale"),
                    _root_.scala.collection.immutable.Set("collector::buffer::op")
                  ),
                  _root_.scala.collection.immutable.Set("block16", "block32"),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"),
                    _root_.scala.collection.immutable.Set("block_scale"),
                    _root_.scala.collection.immutable.Set("collector::buffer::op")
                  ),
                  _root_.scala.collection.immutable.Set("block16", "block32"),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.VectorOperand,
                    OperandConstraint.Value
                  ),
                  Col(_root_.scala.collection.immutable.Set("kind::i8")),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.VectorOperand,
                    OperandConstraint.Value
                  ),
                  Col(_root_.scala.collection.immutable.Set("kind::i8")),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.VectorOperand,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("kind::i8"),
                    _root_.scala.collection.immutable.Set("collector::buffer::op")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.VectorOperand,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("kind::i8"),
                    _root_.scala.collection.immutable.Set("ashift")
                  ),
                  _root_.scala.collection.immutable.Set("collector::buffer::op"),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.VectorOperand,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("kind::i8"),
                    _root_.scala.collection.immutable.Set("collector::buffer::op")
                  ),
                  _root_.scala.collection.immutable.Set("ashift"),
                  false
                )
              )
            )
          ))
      )
  case Tcgen05MmaSp extends InstructionDef(
        "tcgen05.mma.sp",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 6 || operands.size == 7 || operands.size == 8) Right(if (operands.size == 6)
            Instruction.Tcgen05MmaSp(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              operands(4),
              None,
              None,
              None,
              operands(5),
              None,
              mods,
              pred
            )
          else if (operands(5).isInstanceOf[Operand.Address]) Instruction.Tcgen05MmaSp(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            operands(4),
            Some(operands(5)),
            Some(operands(6)),
            None,
            operands(7),
            None,
            mods,
            pred
          )
          else Instruction.Tcgen05MmaSp(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            operands(4),
            None,
            None,
            Some(operands(5)),
            operands(6),
            operands.lift(7),
            mods,
            pred
          ))
          else Left(InstructionDecodeError.InvalidOperandCount("tcgen05.mma.sp", Col(6, 7, 8), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "tcgen05-mma-instructions-mma-sp",
              "9.7.17.10.9.2. TensorCore 5th Generation Instructions: tcgen05.mma.sp",
              "tcgen05.mma.sp",
              "TensorCore 5th Generation Instructions",
              Col(
                "// 1. Floating-point type without block scaling:\n\ntcgen05.mma.sp.cta_group.kind  [d-tmem],  a-desc,  b-desc, [sp-meta-tmem] ,  idesc,\n                               { disable-output-lane }, enable-input-d{, scale-input-d};\n\ntcgen05.mma.sp.cta_group.kind  [d-tmem], [a-tmem], b-desc, [sp-meta-tmem] , idesc,\n                               { disable-output-lane }, enable-input-d{, scale-input-d};\n\n.kind       = { .kind::f16, , .kind::tf32, .kind::f8f6f4 }\n.cta_group  = { .cta_group::1,  .cta_group::2 }\n\n----------------------------------------------------------------------------------\n\n// 2. Floating-point type with block scaling:\n\ntcgen05.mma.sp.cta_group.kind.block_scale{.scale_vectorsize}\n                                         [d-tmem],  a-desc,  b-desc , [sp-meta-tmem] , idesc,\n                                         [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind.block_scale{.scale_vectorsize}\n                                         [d-tmem], [a-tmem], b-desc , [sp-meta-tmem] , idesc,\n                                         [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\n.scale_vectorsize = { .scale_vec::1X, .scale_vec::2X, .scale_vec::4X, .block16, .block32 }\n.cta_group      = { .cta_group::1,  .cta_group::2 }\n.kind = { .kind::mxf8f6f4, .kind::mxf4, .kind::mxf4nvf4 }\n\n----------------------------------------------------------------------------------\n\n// 3. Convolution MMA with floating-point type without block scaling:\n\ntcgen05.mma.sp.cta_group.kind.collector_usage           [d-tmem],  a-desc,  b-desc,\n                                                        [sp-meta-tmem] ,  idesc,\n                                                        { disable-output-lane }, enable-input-d\n                                                        {, scale-input-d};\n\ntcgen05.mma.sp.cta_group.kind.ashift{.collector_usage}  [d-tmem], [a-tmem], b-desc,\n                                                        [sp-meta-tmem] , idesc,\n                                                        { disable-output-lane }, enable-input-d\n                                                        {, scale-input-d};\n\ntcgen05.mma.sp.cta_group.kind{.ashift}.collector_usage  [d-tmem], [a-tmem], b-desc,\n                                                        [sp-meta-tmem] , idesc,\n                                                        { disable-output-lane }, enable-input-d\n                                                        {, scale-input-d};\n\n.kind            = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }\n\n----------------------------------------------------------------------------------\n\n// 4. Activation Stationary MMA with floating-point type with block scaling:\n\ntcgen05.mma.sp.cta_group.kind.block_scale{.scale_vectorsize}.collector_usage\n                                         [d-tmem],  a-desc,  b-desc , [sp-meta-tmem] , idesc,\n                                         [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind.block_scale{.scale_vectorsize}.collector_usage\n                                         [d-tmem], [a-tmem], b-desc , [sp-meta-tmem] , idesc,\n                                         [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\n.kind = { .kind::mxf8f6f4, .kind::mxf4, .kind::mxf4nvf4 }\n.scale_vectorsize = { .scale_vec::1X, .scale_vec::2X, .scale_vec::4X, .block16, .block32 }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }\n\n----------------------------------------------------------------------------------\n\n// 5. Integer type:\n\ntcgen05.mma.sp.cta_group.kind::i8 [d-tmem],  a-desc,  b-desc, [sp-meta-tmem] , idesc,\n                                  { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind::i8 [d-tmem], [a-tmem], b-desc, [sp-meta-tmem] , idesc,\n                                  { disable-output-lane }, enable-input-d;\n\n.cta_group      = { .cta_group::1,  .cta_group::2 }\n\n----------------------------------------------------------------------------------\n\n// 6. Convolution MMA with Integer type:\n\ntcgen05.mma.sp.cta_group.kind::i8.collector_usage          [d-tmem],  a-desc,  b-desc,\n                                                           [sp-meta-tmem] , idesc,\n                                                           { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind::i8.ashift{.collector_usage} [d-tmem], [a-tmem], b-desc,\n                                                           [sp-meta-tmem], idesc ,\n                                                           { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind::i8{.ashift}.collector_usage [d-tmem], [a-tmem], b-desc,\n                                                           [sp-meta-tmem], idesc ,\n                                                           { disable-output-lane }, enable-input-d;\n\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }"
              ),
              PTXVersion(8, 6),
              SMTarget(100),
              Col(
                InstructionForm(
                  "tcgen05.mma.sp",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.VectorOperand,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4")),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma.sp",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.VectorOperand,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4")),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma.sp",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"),
                    _root_.scala.collection.immutable.Set("block_scale")
                  ),
                  _root_.scala.collection.immutable.Set("block16", "block32"),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma.sp",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"),
                    _root_.scala.collection.immutable.Set("block_scale")
                  ),
                  _root_.scala.collection.immutable.Set("block16", "block32"),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma.sp",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.VectorOperand,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"),
                    _root_.scala.collection.immutable.Set("collector::buffer::op")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma.sp",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.VectorOperand,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"),
                    _root_.scala.collection.immutable.Set("ashift")
                  ),
                  _root_.scala.collection.immutable.Set("collector::buffer::op"),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma.sp",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.VectorOperand,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"),
                    _root_.scala.collection.immutable.Set("collector::buffer::op")
                  ),
                  _root_.scala.collection.immutable.Set("ashift"),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma.sp",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"),
                    _root_.scala.collection.immutable.Set("block_scale"),
                    _root_.scala.collection.immutable.Set("collector::buffer::op")
                  ),
                  _root_.scala.collection.immutable.Set("block16", "block32"),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma.sp",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"),
                    _root_.scala.collection.immutable.Set("block_scale"),
                    _root_.scala.collection.immutable.Set("collector::buffer::op")
                  ),
                  _root_.scala.collection.immutable.Set("block16", "block32"),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma.sp",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.VectorOperand,
                    OperandConstraint.Value
                  ),
                  Col(_root_.scala.collection.immutable.Set("kind::i8")),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma.sp",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.VectorOperand,
                    OperandConstraint.Value
                  ),
                  Col(_root_.scala.collection.immutable.Set("kind::i8")),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma.sp",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.VectorOperand,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("kind::i8"),
                    _root_.scala.collection.immutable.Set("collector::buffer::op")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma.sp",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.VectorOperand,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("kind::i8"),
                    _root_.scala.collection.immutable.Set("ashift")
                  ),
                  _root_.scala.collection.immutable.Set("collector::buffer::op"),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma.sp",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.VectorOperand,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("kind::i8"),
                    _root_.scala.collection.immutable.Set("collector::buffer::op")
                  ),
                  _root_.scala.collection.immutable.Set("ashift"),
                  false
                )
              )
            )
          ))
      )
  case Tcgen05MmaWs extends InstructionDef(
        "tcgen05.mma.ws",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 5 || operands.size == 6) Right(Instruction.Tcgen05MmaWs(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            operands(4),
            operands.lift(5),
            mods,
            pred
          ))
          else Left(InstructionDecodeError.InvalidOperandCount("tcgen05.mma.ws", Col(5, 6), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "tcgen05-mma-instructions-mma-ws",
              "9.7.17.10.9.3. TensorCore 5th Generation Instructions: tcgen05.mma.ws",
              "tcgen05.mma.ws",
              "TensorCore 5th Generation Instructions",
              Col(
                "// 1. Floating-point type without block scaling:\n\ntcgen05.mma.ws.cta_group::1.kind{.collector_usage}    [d-tmem],  a-desc,  b-desc,  idesc,\n                                                      enable-input-d {, zero-column-mask-desc };\n\ntcgen05.mma.ws.cta_group::1.kind{.collector_usage}    [d-tmem], [a-tmem], b-desc, idesc,\n                                                      enable-input-d {, zero-column-mask-desc };\n\n.kind = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n\n----------------------------------------------------------------------------------\n\n// 2. Integer type:\n\ntcgen05.mma.ws.cta_group::1.kind::i8{.collector_usage} [d-tmem],  a-desc,  b-desc, idesc,\n                                                       enable-input-d {, zero-column-mask-desc};\n\ntcgen05.mma.ws.cta_group::1.kind::i8{.collector_usage} [d-tmem], [a-tmem], b-desc, idesc,\n                                                       enable-input-d {, zero-column-mask-desc};\n\n.collector_usage = { .collector::buffer::op }\n::buffer = { ::b0, ::b1, ::b2, ::b3 }\n::op   = { ::fill, ::use, ::lastuse, ::discard}"
              ),
              PTXVersion(8, 6),
              SMTarget(100),
              Col(
                InstructionForm(
                  "tcgen05.mma.ws",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("cta_group"),
                    _root_.scala.collection.immutable.Set("kind::f16", "kind::f8f6f4", "kind::tf32")
                  ),
                  _root_.scala.collection.immutable.Set("collector::buffer::op"),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma.ws",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("cta_group"),
                    _root_.scala.collection.immutable.Set("kind::f16", "kind::f8f6f4", "kind::tf32")
                  ),
                  _root_.scala.collection.immutable.Set("collector::buffer::op"),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma.ws",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("cta_group"),
                    _root_.scala.collection.immutable.Set("kind::i8")
                  ),
                  _root_.scala.collection.immutable.Set("collector::buffer::op"),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma.ws",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("cta_group"),
                    _root_.scala.collection.immutable.Set("kind::i8")
                  ),
                  _root_.scala.collection.immutable.Set("collector::buffer::op"),
                  false
                )
              )
            )
          ))
      )
  case Tcgen05MmaWsSp extends InstructionDef(
        "tcgen05.mma.ws.sp",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 6 || operands.size == 7) Right(Instruction.Tcgen05MmaWsSp(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            operands(4),
            operands(5),
            operands.lift(6),
            mods,
            pred
          ))
          else Left(InstructionDecodeError.InvalidOperandCount("tcgen05.mma.ws.sp", Col(6, 7), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "tcgen05-mma-instructions-mma-ws-sp",
              "9.7.17.10.9.4. TensorCore 5th Generation Instructions: tcgen05.mma.ws.sp",
              "tcgen05.mma.ws.sp",
              "TensorCore 5th Generation Instructions",
              Col(
                "// 1. Floating-point type without block scaling:\n\ntcgen05.mma.ws.sp.cta_group::1.kind{.collector_usage} [d-tmem],  a-desc,  b-desc,\n                                                      [sp-meta-tmem] ,  idesc,\n                                                      enable-input-d {, zero-column-mask-desc};\n\ntcgen05.mma.ws.sp.cta_group::1.kind{.collector_usage} [d-tmem], [a-tmem], b-desc,\n                                                      [sp-meta-tmem] , idesc,\n                                                      enable-input-d {, zero-column-mask-desc};\n\n.kind = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n\n----------------------------------------------------------------------------------\n\n// 2. Integer type:\n\ntcgen05.mma.ws.sp.cta_group::1.kind::i8{.collector_usage} [d-tmem], a-desc, b-desc,\n                                                          [sp-meta-tmem] , idesc,\n                                                          enable-input-d {, zero-column-mask-desc};\n\ntcgen05.mma.ws.sp.cta_group::1.kind::i8{.collector_usage} [d-tmem], [a-tmem], b-desc,\n                                                          [sp-meta-tmem] , idesc,\n                                                          enable-input-d {, zero-column-mask-desc};\n\n.collector_usage = { .collector::buffer::op }\n::buffer = { ::b0, ::b1, ::b2, ::b3 }\n::op   = { ::fill, ::use, ::lastuse, ::discard }"
              ),
              PTXVersion(8, 6),
              SMTarget(100),
              Col(
                InstructionForm(
                  "tcgen05.mma.ws.sp",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("cta_group"),
                    _root_.scala.collection.immutable.Set("kind::f16", "kind::f8f6f4", "kind::tf32")
                  ),
                  _root_.scala.collection.immutable.Set("collector::buffer::op"),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma.ws.sp",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("cta_group"),
                    _root_.scala.collection.immutable.Set("kind::f16", "kind::f8f6f4", "kind::tf32")
                  ),
                  _root_.scala.collection.immutable.Set("collector::buffer::op"),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma.ws.sp",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("cta_group"),
                    _root_.scala.collection.immutable.Set("kind::i8")
                  ),
                  _root_.scala.collection.immutable.Set("collector::buffer::op"),
                  false
                ),
                InstructionForm(
                  "tcgen05.mma.ws.sp",
                  Col(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("cta_group"),
                    _root_.scala.collection.immutable.Set("kind::i8")
                  ),
                  _root_.scala.collection.immutable.Set("collector::buffer::op"),
                  false
                )
              )
            )
          ))
      )
  case Tcgen05RelinquishAllocPermit extends InstructionDef(
        "tcgen05.relinquish_alloc_permit",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 0) Right(Instruction.Tcgen05RelinquishAllocPermit(mods, pred))
          else
            Left(InstructionDecodeError.InvalidOperandCount("tcgen05.relinquish_alloc_permit", Col(0), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "tcgen05-instructions-tcgen05-alloc-dealloc-relinquish-alloc-permit",
              "9.7.17.7.1. Tensorcore 5th Generation Instructions: tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit",
              "tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit",
              "Tensorcore 5th Generation Instructions",
              Col(
                "tcgen05.alloc.cta_group.sync.aligned{.shared::cta}.b32  [dst], nCols;\n\ntcgen05.dealloc.cta_group.sync.aligned.b32              taddr, nCols;\n\ntcgen05.relinquish_alloc_permit.cta_group.sync.aligned;\n\n.cta_group = { .cta_group::1, .cta_group::2 }"
              ),
              PTXVersion(8, 6),
              SMTarget(100),
              Col(InstructionForm(
                "tcgen05.relinquish_alloc_permit",
                Col(),
                Col(_root_.scala.collection.immutable.Set("sync"), _root_.scala.collection.immutable.Set("aligned")),
                _root_.scala.collection.immutable.Set(),
                false
              ))
            )
          ))
      )
  case Tcgen05Shift extends InstructionDef(
        "tcgen05.shift",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 1) Right(Instruction.Tcgen05Shift(operands(0), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("tcgen05.shift", Col(1), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "tcgen05-instructions-tcgen05-shift",
              "9.7.17.9.3. Tensorcore 5th Generation Instructions: tcgen05.shift",
              "tcgen05.shift",
              "Tensorcore 5th Generation Instructions",
              Col("tcgen05.shift.cta_group.down  [taddr];\n\n.cta_group = { .cta_group::1, .cta_group::2 }"),
              PTXVersion(8, 6),
              SMTarget(10),
              Col(InstructionForm(
                "tcgen05.shift",
                Col(OperandConstraint.Address),
                Col(_root_.scala.collection.immutable.Set("down")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Tcgen05St extends InstructionDef(
        "tcgen05.st",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2 || operands.size == 3)
            Right(Instruction.Tcgen05St(operands(0), operands(1), operands.lift(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("tcgen05.st", Col(2, 3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "tcgen05-instructions-tcgen05-st",
              "9.7.17.8.4. Tensorcore 5th Generation Instructions: tcgen05.st",
              "tcgen05.st",
              "Tensorcore 5th Generation Instructions",
              Col(
                "tcgen05.st.sync.aligned.shape1.num{.unpack}.b32    [taddr], r;\n\ntcgen05.st.sync.aligned.shape2.num{.unpack}.b32    [taddr], immHalfSplitoff, r;\n\n.shape1 = { .16x64b, .16x128b, .16x256b, .32x32b }\n.shape2 = { .16x32bx2 }\n.num    = { .x1, .x2, .x4, .x8, .x16, .x32, .x64, .x128 }\n.unpack = { .unpack::16b }"
              ),
              PTXVersion(8, 6),
              SMTarget(100),
              Col(
                InstructionForm(
                  "tcgen05.st",
                  Col(OperandConstraint.Address, OperandConstraint.Register),
                  Col(
                    _root_.scala.collection.immutable.Set("sync"),
                    _root_.scala.collection.immutable.Set("aligned"),
                    _root_.scala.collection.immutable.Set("x1", "x128", "x16", "x2", "x32", "x4", "x64", "x8"),
                    _root_.scala.collection.immutable.Set("b32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tcgen05.st",
                  Col(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Register),
                  Col(
                    _root_.scala.collection.immutable.Set("sync"),
                    _root_.scala.collection.immutable.Set("aligned"),
                    _root_.scala.collection.immutable.Set("x1", "x128", "x16", "x2", "x32", "x4", "x64", "x8"),
                    _root_.scala.collection.immutable.Set("b32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case Tcgen05WaitLd extends InstructionDef(
        "tcgen05.wait::ld",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 0) Right(Instruction.Tcgen05WaitLd(mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("tcgen05.wait::ld", Col(0), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "tcgen05-instructions-tcgen05-wait",
              "9.7.17.8.5. Tensorcore 5th Generation Instructions: tcgen05.wait",
              "tcgen05.wait",
              "Tensorcore 5th Generation Instructions",
              Col("tcgen05.wait_operation.sync.aligned;\n\n.wait_operation = { .wait::ld, .wait::st }"),
              PTXVersion(8, 6),
              SMTarget(100),
              Col(InstructionForm(
                "tcgen05.wait::ld",
                Col(),
                Col(_root_.scala.collection.immutable.Set("sync"), _root_.scala.collection.immutable.Set("aligned")),
                _root_.scala.collection.immutable.Set(),
                false
              ))
            )
          ))
      )
  case Tcgen05WaitSt extends InstructionDef(
        "tcgen05.wait::st",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 0) Right(Instruction.Tcgen05WaitSt(mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("tcgen05.wait::st", Col(0), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "tcgen05-instructions-tcgen05-wait",
              "9.7.17.8.5. Tensorcore 5th Generation Instructions: tcgen05.wait",
              "tcgen05.wait",
              "Tensorcore 5th Generation Instructions",
              Col("tcgen05.wait_operation.sync.aligned;\n\n.wait_operation = { .wait::ld, .wait::st }"),
              PTXVersion(8, 6),
              SMTarget(100),
              Col(InstructionForm(
                "tcgen05.wait::st",
                Col(),
                Col(_root_.scala.collection.immutable.Set("sync"), _root_.scala.collection.immutable.Set("aligned")),
                _root_.scala.collection.immutable.Set(),
                false
              ))
            )
          ))
      )
  case TensorMapCopyFenceProxy extends InstructionDef(
        "tensormap.cp_fenceproxy",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3)
            Right(Instruction.TensorMapCopyFenceProxy(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("tensormap.cp_fenceproxy", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-tensormap-cp-fenceproxy",
              "9.7.14.17. Parallel Synchronization and Communication Instructions: tensormap.cp_fenceproxy",
              "tensormap.cp_fenceproxy",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "tensormap.cp_fenceproxy.cp_qualifiers.fence_qualifiers.sync.aligned  [dst], [src], size;\n\n.cp_qualifiers    = { .global.shared::cta }\n.fence_qualifiers = { .to_proxy::from_proxy.release.scope }\n.to_proxy::from_proxy  = { .tensormap::generic }\n.scope            = { .cta, .cluster, .gpu , .sys }"
              ),
              PTXVersion(8, 3),
              SMTarget(90),
              Col(InstructionForm(
                "tensormap.cp_fenceproxy",
                Col(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("sync"), _root_.scala.collection.immutable.Set("aligned")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case TensorMapReplace extends InstructionDef(
        "tensormap.replace",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2 || operands.size == 3)
            Right(Instruction.TensorMapReplace(operands(0), operands(1), operands.lift(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("tensormap.replace", Col(2, 3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "data-movement-and-conversion-instructions-tensormap-replace",
              "9.7.9.27. Data Movement and Conversion Instructions: tensormap.replace",
              "tensormap.replace",
              "Data Movement and Conversion Instructions",
              Col(
                "tensormap.replace.mode.field1{.ss}.b1024.type  [addr], new_val;\ntensormap.replace.mode.field2{.ss}.b1024.type  [addr], ord, new_val;\ntensormap.replace.mode.field3{.ss}.b1024.type  [addr], new_val;\n\n.mode    = { .tile }\n.field1  = { .global_address, .rank }\n.field2  = { .box_dim, .global_dim, .global_stride, .element_stride  }\n.field3  = { .elemtype,  .interleave_layout, .swizzle_mode, .swizzle_atomicity, .fill_mode }\n.ss      = { .global, .shared::cta }\n.type    = { .b32, .b64 }"
              ),
              PTXVersion(8, 3),
              SMTarget(100),
              Col(
                InstructionForm(
                  "tensormap.replace",
                  Col(OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("tile"),
                    _root_.scala.collection.immutable.Set("global_address", "rank"),
                    _root_.scala.collection.immutable.Set("b1024"),
                    _root_.scala.collection.immutable.Set("b32", "b64")
                  ),
                  _root_.scala.collection.immutable.Set("global", "shared::cta"),
                  false
                ),
                InstructionForm(
                  "tensormap.replace",
                  Col(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("tile"),
                    _root_.scala.collection.immutable.Set("box_dim", "element_stride", "global_dim", "global_stride"),
                    _root_.scala.collection.immutable.Set("b1024"),
                    _root_.scala.collection.immutable.Set("b32", "b64")
                  ),
                  _root_.scala.collection.immutable.Set("global", "shared::cta"),
                  false
                ),
                InstructionForm(
                  "tensormap.replace",
                  Col(OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("tile"),
                    _root_.scala.collection.immutable.Set(
                      "elemtype",
                      "fill_mode",
                      "interleave_layout",
                      "swizzle_atomicity",
                      "swizzle_mode"
                    ),
                    _root_.scala.collection.immutable.Set("b1024"),
                    _root_.scala.collection.immutable.Set("b32", "b64")
                  ),
                  _root_.scala.collection.immutable.Set("global", "shared::cta"),
                  false
                )
              )
            )
          ))
      )
  case TestPredicate extends InstructionDef(
        "testp",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.TestPredicate(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("testp", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "floating-point-instructions-testp",
              "9.7.3.1. Floating Point Instructions: testp",
              "testp",
              "Floating Point Instructions",
              Col(
                "testp.op.type  p, a;  // result is .pred\n\n.op   = { .finite, .infinite,\n          .number, .notanumber,\n          .normal, .subnormal };\n.type = { .f32, .f64 };"
              ),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(InstructionForm(
                "testp",
                Col(OperandConstraint.Register, OperandConstraint.Value),
                Col(
                  _root_.scala.collection.immutable.Set(
                    "finite",
                    "infinite",
                    "normal",
                    "notanumber",
                    "number",
                    "subnormal"
                  ),
                  _root_.scala.collection.immutable.Set("f32", "f64")
                ),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )
  case Tex extends InstructionDef(
        "tex",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (
            operands.size == 2 || operands.size == 3 || operands.size == 4 || operands.size == 5 || operands.size == 6
          ) Right(Instruction.Tex(
            operands(0),
            operands(1),
            operands.lift(2),
            operands.lift(3),
            operands.lift(4),
            operands.lift(5),
            mods,
            pred
          ))
          else Left(InstructionDecodeError.InvalidOperandCount("tex", Col(2, 3, 4, 5, 6), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "texture-instructions-tex",
              "9.7.11.3. Texture Instructions: tex",
              "tex",
              "Texture Instructions",
              Col(
                "tex.geom.v4.dtype.ctype  d, [a, c] {, e} {, f};\ntex.geom.v4.dtype.ctype  d[|p], [a, b, c] {, e} {, f};  // explicit sampler\n\ntex.geom.v2.f16x2.ctype  d[|p], [a, c] {, e} {, f};\ntex.geom.v2.f16x2.ctype  d[|p], [a, b, c] {, e} {, f};  // explicit sampler\n\n// mipmaps\ntex.base.geom.v4.dtype.ctype   d[|p], [a, {b,} c] {, e} {, f};\ntex.level.geom.v4.dtype.ctype  d[|p], [a, {b,} c], lod {, e} {, f};\ntex.grad.geom.v4.dtype.ctype   d[|p], [a, {b,} c], dPdx, dPdy {, e} {, f};\n\ntex.base.geom.v2.f16x2.ctype   d[|p], [a, {b,} c] {, e} {, f};\ntex.level.geom.v2.f16x2.ctype  d[|p], [a, {b,} c], lod {, e} {, f};\ntex.grad.geom.v2.f16x2.ctype   d[|p], [a, {b,} c], dPdx, dPdy {, e} {, f};\n\n.geom  = { .1d, .2d, .3d, .a1d, .a2d, .cube, .acube, .2dms, .a2dms };\n.dtype = { .u32, .s32, .f16,  .f32 };\n.ctype = {       .s32, .f32 };          // .cube, .acube require .f32\n                                        // .2dms, .a2dms require .s32"
              ),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(
                InstructionForm(
                  "tex",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("a1d", "a2d", "a2dms", "acube", "cube"),
                    _root_.scala.collection.immutable.Set("v4"),
                    _root_.scala.collection.immutable.Set("f16", "f32", "s32", "u32"),
                    _root_.scala.collection.immutable.Set("f32", "s32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tex",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("a1d", "a2d", "a2dms", "acube", "cube"),
                    _root_.scala.collection.immutable.Set("v4"),
                    _root_.scala.collection.immutable.Set("f16", "f32", "s32", "u32"),
                    _root_.scala.collection.immutable.Set("f32", "s32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tex",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("a1d", "a2d", "a2dms", "acube", "cube"),
                    _root_.scala.collection.immutable.Set("v2"),
                    _root_.scala.collection.immutable.Set("f16x2"),
                    _root_.scala.collection.immutable.Set("f32", "s32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tex",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("a1d", "a2d", "a2dms", "acube", "cube"),
                    _root_.scala.collection.immutable.Set("v2"),
                    _root_.scala.collection.immutable.Set("f16x2"),
                    _root_.scala.collection.immutable.Set("f32", "s32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tex",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("base"),
                    _root_.scala.collection.immutable.Set("a1d", "a2d", "a2dms", "acube", "cube"),
                    _root_.scala.collection.immutable.Set("v4"),
                    _root_.scala.collection.immutable.Set("f16", "f32", "s32", "u32"),
                    _root_.scala.collection.immutable.Set("f32", "s32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tex",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("level"),
                    _root_.scala.collection.immutable.Set("a1d", "a2d", "a2dms", "acube", "cube"),
                    _root_.scala.collection.immutable.Set("v4"),
                    _root_.scala.collection.immutable.Set("f16", "f32", "s32", "u32"),
                    _root_.scala.collection.immutable.Set("f32", "s32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tex",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("grad"),
                    _root_.scala.collection.immutable.Set("a1d", "a2d", "a2dms", "acube", "cube"),
                    _root_.scala.collection.immutable.Set("v4"),
                    _root_.scala.collection.immutable.Set("f16", "f32", "s32", "u32"),
                    _root_.scala.collection.immutable.Set("f32", "s32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tex",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("base"),
                    _root_.scala.collection.immutable.Set("a1d", "a2d", "a2dms", "acube", "cube"),
                    _root_.scala.collection.immutable.Set("v2"),
                    _root_.scala.collection.immutable.Set("f16x2"),
                    _root_.scala.collection.immutable.Set("f32", "s32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tex",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("level"),
                    _root_.scala.collection.immutable.Set("a1d", "a2d", "a2dms", "acube", "cube"),
                    _root_.scala.collection.immutable.Set("v2"),
                    _root_.scala.collection.immutable.Set("f16x2"),
                    _root_.scala.collection.immutable.Set("f32", "s32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tex",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("grad"),
                    _root_.scala.collection.immutable.Set("a1d", "a2d", "a2dms", "acube", "cube"),
                    _root_.scala.collection.immutable.Set("v2"),
                    _root_.scala.collection.immutable.Set("f16x2"),
                    _root_.scala.collection.immutable.Set("f32", "s32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case Tld4 extends InstructionDef(
        "tld4",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2 || operands.size == 3 || operands.size == 4)
            Right(Instruction.Tld4(operands(0), operands(1), operands.lift(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("tld4", Col(2, 3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "texture-instructions-tld4",
              "9.7.11.4. Texture Instructions: tld4",
              "tld4",
              "Texture Instructions",
              Col(
                "tld4.comp.2d.v4.dtype.f32    d[|p], [a, c] {, e} {, f};\ntld4.comp.geom.v4.dtype.f32  d[|p], [a, b, c] {, e} {, f};  // explicit sampler\n\n.comp  = { .r, .g, .b, .a };\n.geom  = { .2d, .a2d, .cube, .acube };\n.dtype = { .u32, .s32, .f32 };"
              ),
              PTXVersion(2, 2),
              SMTarget(20),
              Col(
                InstructionForm(
                  "tld4",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("a", "b", "g", "r"),
                    _root_.scala.collection.immutable.Set("v4"),
                    _root_.scala.collection.immutable.Set("f32", "s32", "u32"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "tld4",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("a", "b", "g", "r"),
                    _root_.scala.collection.immutable.Set("a2d", "acube", "cube"),
                    _root_.scala.collection.immutable.Set("v4"),
                    _root_.scala.collection.immutable.Set("f32", "s32", "u32"),
                    _root_.scala.collection.immutable.Set("f32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case Trap extends InstructionDef(
        "trap",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 0) Right(Instruction.Trap(mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("trap", Col(0), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "miscellaneous-instructions-trap",
              "9.7.20.4. Miscellaneous Instructions: trap",
              "trap",
              "Miscellaneous Instructions",
              Col("trap;"),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm("trap", Col(), Col(), _root_.scala.collection.immutable.Set(), true))
            )
          ))
      )
  case Txq extends InstructionDef(
        "txq",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2 || operands.size == 3)
            Right(Instruction.Txq(operands(0), operands(1), operands.lift(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("txq", Col(2, 3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "texture-instructions-txq",
              "9.7.11.5. Texture Instructions: txq",
              "txq",
              "Texture Instructions",
              Col(
                "txq.tquery.b32         d, [a];       // texture attributes\ntxq.level.tlquery.b32  d, [a], lod;  // texture attributes\ntxq.squery.b32         d, [a];       // sampler attributes\n\n.tquery  = { .width, .height, .depth,\n             .channel_data_type, .channel_order,\n             .normalized_coords, .array_size,\n             .num_mipmap_levels, .num_samples};\n\n.tlquery = { .width, .height, .depth };\n\n.squery  = { .force_unnormalized_coords, .filter_mode,\n             .addr_mode_0, addr_mode_1, addr_mode_2 };"
              ),
              PTXVersion(1, 5),
              SMTarget(10),
              Col(
                InstructionForm(
                  "txq",
                  Col(OperandConstraint.Register, OperandConstraint.Address),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "array_size",
                      "channel_data_type",
                      "channel_order",
                      "depth",
                      "height",
                      "normalized_coords",
                      "num_mipmap_levels",
                      "num_samples",
                      "width"
                    ),
                    _root_.scala.collection.immutable.Set("b32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "txq",
                  Col(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("level"),
                    _root_.scala.collection.immutable.Set("depth", "height", "width"),
                    _root_.scala.collection.immutable.Set("b32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "txq",
                  Col(OperandConstraint.Register, OperandConstraint.Address),
                  Col(
                    _root_.scala.collection.immutable.Set(
                      "addr_mode_0",
                      "addr_mode_1",
                      "addr_mode_2",
                      "filter_mode",
                      "force_unnormalized_coords"
                    ),
                    _root_.scala.collection.immutable.Set("b32")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case Vabsdiff extends InstructionDef(
        "vabsdiff",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3 || operands.size == 4)
            Right(Instruction.Vabsdiff(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vabsdiff", Col(3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax",
              "9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax",
              "vadd, vsub, vabsdiff, vmin, vmax",
              "Scalar Video Instructions",
              Col(
                "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
              ),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(
                InstructionForm(
                  "vabsdiff",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vabsdiff",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("add", "max", "min")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vabsdiff",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                )
              )
            )
          ))
      )
  case Vabsdiff2 extends InstructionDef(
        "vabsdiff2",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Vabsdiff2(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vabsdiff2", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2",
              "9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
              "vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
              "SIMD Video Instructions",
              Col(
                "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
              ),
              PTXVersion(3, 0),
              SMTarget(30),
              Col(
                InstructionForm(
                  "vabsdiff2",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vabsdiff2",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("add")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case Vabsdiff4 extends InstructionDef(
        "vabsdiff4",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Vabsdiff4(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vabsdiff4", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4",
              "9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
              "vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
              "SIMD Video Instructions",
              Col(
                "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
              ),
              PTXVersion(3, 0),
              SMTarget(30),
              Col(
                InstructionForm(
                  "vabsdiff4",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vabsdiff4",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("add")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case Vadd extends InstructionDef(
        "vadd",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3 || operands.size == 4)
            Right(Instruction.Vadd(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vadd", Col(3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax",
              "9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax",
              "vadd, vsub, vabsdiff, vmin, vmax",
              "Scalar Video Instructions",
              Col(
                "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
              ),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(
                InstructionForm(
                  "vadd",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vadd",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("add", "max", "min")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vadd",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                )
              )
            )
          ))
      )
  case Vadd2 extends InstructionDef(
        "vadd2",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Vadd2(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vadd2", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2",
              "9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
              "vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
              "SIMD Video Instructions",
              Col(
                "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
              ),
              PTXVersion(3, 0),
              SMTarget(30),
              Col(
                InstructionForm(
                  "vadd2",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vadd2",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("add")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case Vadd4 extends InstructionDef(
        "vadd4",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Vadd4(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vadd4", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4",
              "9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
              "vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
              "SIMD Video Instructions",
              Col(
                "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
              ),
              PTXVersion(3, 0),
              SMTarget(30),
              Col(
                InstructionForm(
                  "vadd4",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vadd4",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("add")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case Vavrg2 extends InstructionDef(
        "vavrg2",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Vavrg2(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vavrg2", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2",
              "9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
              "vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
              "SIMD Video Instructions",
              Col(
                "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
              ),
              PTXVersion(3, 0),
              SMTarget(30),
              Col(
                InstructionForm(
                  "vavrg2",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vavrg2",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("add")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case Vavrg4 extends InstructionDef(
        "vavrg4",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Vavrg4(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vavrg4", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4",
              "9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
              "vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
              "SIMD Video Instructions",
              Col(
                "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
              ),
              PTXVersion(3, 0),
              SMTarget(30),
              Col(
                InstructionForm(
                  "vavrg4",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vavrg4",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("add")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case Vmad extends InstructionDef(
        "vmad",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Vmad(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vmad", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "scalar-video-instructions-vmad",
              "9.7.19.1.3. Scalar Video Instructions: vmad",
              "vmad",
              "Scalar Video Instructions",
              Col(
                "// 32-bit scalar operation\nvmad.dtype.atype.btype{.sat}{.scale}     d, {-}a{.asel}, {-}b{.bsel},\n                                         {-}c;\nvmad.dtype.atype.btype.po{.sat}{.scale}  d, a{.asel}, b{.bsel}, c;\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.scale = { .shr7, .shr15 };"
              ),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(
                InstructionForm(
                  "vmad",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat", "shr15", "shr7"),
                  true
                ),
                InstructionForm(
                  "vmad",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("po")
                  ),
                  _root_.scala.collection.immutable.Set("sat", "shr15", "shr7"),
                  true
                )
              )
            )
          ))
      )
  case Vmax extends InstructionDef(
        "vmax",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3 || operands.size == 4)
            Right(Instruction.Vmax(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vmax", Col(3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax",
              "9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax",
              "vadd, vsub, vabsdiff, vmin, vmax",
              "Scalar Video Instructions",
              Col(
                "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
              ),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(
                InstructionForm(
                  "vmax",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vmax",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("add", "max", "min")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vmax",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                )
              )
            )
          ))
      )
  case Vmax2 extends InstructionDef(
        "vmax2",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Vmax2(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vmax2", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2",
              "9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
              "vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
              "SIMD Video Instructions",
              Col(
                "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
              ),
              PTXVersion(3, 0),
              SMTarget(30),
              Col(
                InstructionForm(
                  "vmax2",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vmax2",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("add")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case Vmax4 extends InstructionDef(
        "vmax4",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Vmax4(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vmax4", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4",
              "9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
              "vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
              "SIMD Video Instructions",
              Col(
                "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
              ),
              PTXVersion(3, 0),
              SMTarget(30),
              Col(
                InstructionForm(
                  "vmax4",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vmax4",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("add")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case Vmin extends InstructionDef(
        "vmin",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3 || operands.size == 4)
            Right(Instruction.Vmin(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vmin", Col(3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax",
              "9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax",
              "vadd, vsub, vabsdiff, vmin, vmax",
              "Scalar Video Instructions",
              Col(
                "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
              ),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(
                InstructionForm(
                  "vmin",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vmin",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("add", "max", "min")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vmin",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                )
              )
            )
          ))
      )
  case Vmin2 extends InstructionDef(
        "vmin2",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Vmin2(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vmin2", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2",
              "9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
              "vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
              "SIMD Video Instructions",
              Col(
                "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
              ),
              PTXVersion(3, 0),
              SMTarget(30),
              Col(
                InstructionForm(
                  "vmin2",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vmin2",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("add")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case Vmin4 extends InstructionDef(
        "vmin4",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Vmin4(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vmin4", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4",
              "9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
              "vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
              "SIMD Video Instructions",
              Col(
                "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
              ),
              PTXVersion(3, 0),
              SMTarget(30),
              Col(
                InstructionForm(
                  "vmin4",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vmin4",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("add")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case Vote extends InstructionDef(
        "vote",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 2) Right(Instruction.Vote(operands(0), operands(1), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vote", Col(2), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-vote",
              "9.7.14.9. Parallel Synchronization and Communication Instructions: vote (deprecated)",
              "vote (deprecated)",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "vote.mode.pred  d, {!}a;\nvote.ballot.b32 d, {!}a;  // 'ballot' form, returns bitmask\n\n.mode = { .all, .any, .uni };"
              ),
              PTXVersion(1, 2),
              SMTarget(12),
              Col(
                InstructionForm(
                  "vote",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("all", "any", "uni"),
                    _root_.scala.collection.immutable.Set("pred")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ),
                InstructionForm(
                  "vote",
                  Col(OperandConstraint.Register, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("ballot"), _root_.scala.collection.immutable.Set("b32")),
                  _root_.scala.collection.immutable.Set(),
                  false
                )
              )
            )
          ))
      )
  case VoteSync extends InstructionDef(
        "vote.sync",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.VoteSync(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vote.sync", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "parallel-synchronization-and-communication-instructions-vote-sync",
              "9.7.14.10. Parallel Synchronization and Communication Instructions: vote.sync",
              "vote.sync",
              "Parallel Synchronization and Communication Instructions",
              Col(
                "vote.sync.mode.pred  d, {!}a, membermask;\nvote.sync.ballot.b32 d, {!}a, membermask;  // 'ballot' form, returns bitmask\n\n.mode = { .all, .any, .uni };"
              ),
              PTXVersion(6, 0),
              SMTarget(30),
              Col(
                InstructionForm(
                  "vote.sync",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("all", "any", "uni"),
                    _root_.scala.collection.immutable.Set("pred")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ),
                InstructionForm(
                  "vote.sync",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(_root_.scala.collection.immutable.Set("ballot"), _root_.scala.collection.immutable.Set("b32")),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case Vset extends InstructionDef(
        "vset",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3 || operands.size == 4)
            Right(Instruction.Vset(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vset", Col(3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "scalar-video-instructions-vset",
              "9.7.19.1.4. Scalar Video Instructions: vset",
              "vset",
              "Scalar Video Instructions",
              Col(
                "// 32-bit scalar operation, with optional secondary operation\nvset.atype.btype.cmp       d, a{.asel}, b{.bsel};\nvset.atype.btype.cmp.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvset.atype.btype.cmp  d.dsel, a{.asel}, b{.bsel}, c;\n\n.atype = .btype = { .u32, .s32 };\n.cmp   = { .eq, .ne, .lt, .le, .gt, .ge };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
              ),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(
                InstructionForm(
                  "vset",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("eq", "ge", "gt", "le", "lt", "ne")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ),
                InstructionForm(
                  "vset",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("eq", "ge", "gt", "le", "lt", "ne"),
                    _root_.scala.collection.immutable.Set("add", "max", "min")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ),
                InstructionForm(
                  "vset",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("eq", "ge", "gt", "le", "lt", "ne")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case Vset2 extends InstructionDef(
        "vset2",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Vset2(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vset2", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "simd-video-instructions-vset2",
              "9.7.19.2.2. SIMD Video Instructions: vset2",
              "vset2",
              "SIMD Video Instructions",
              Col(
                "// SIMD instruction with secondary SIMD merge operation\nvset2.atype.btype.cmp  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvset2.atype.btype.cmp.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n.atype = .btype = { .u32, .s32 };\n.cmp   = { .eq, .ne, .lt, .le, .gt, .ge };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
              ),
              PTXVersion(3, 0),
              SMTarget(30),
              Col(
                InstructionForm(
                  "vset2",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("eq", "ge", "gt", "le", "lt", "ne")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ),
                InstructionForm(
                  "vset2",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("eq", "ge", "gt", "le", "lt", "ne"),
                    _root_.scala.collection.immutable.Set("add")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case Vset4 extends InstructionDef(
        "vset4",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Vset4(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vset4", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "simd-video-instructions-vset4",
              "9.7.19.2.4. SIMD Video Instructions: vset4",
              "vset4",
              "SIMD Video Instructions",
              Col(
                "// SIMD instruction with secondary SIMD merge operation\nvset4.atype.btype.cmp  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvset4.atype.btype.cmp.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n.atype = .btype = { .u32, .s32 };\n.cmp   = { .eq, .ne, .lt, .le, .gt, .ge };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
              ),
              PTXVersion(3, 0),
              SMTarget(30),
              Col(
                InstructionForm(
                  "vset4",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("eq", "ge", "gt", "le", "lt", "ne")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ),
                InstructionForm(
                  "vset4",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("eq", "ge", "gt", "le", "lt", "ne"),
                    _root_.scala.collection.immutable.Set("add")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case Vshl extends InstructionDef(
        "vshl",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3 || operands.size == 4)
            Right(Instruction.Vshl(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vshl", Col(3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "scalar-video-instructions-vshl-vshr",
              "9.7.19.1.2. Scalar Video Instructions: vshl, vshr",
              "vshl, vshr",
              "Scalar Video Instructions",
              Col(
                "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.u32{.sat}.mode       d, a{.asel}, b{.bsel};\nvop.dtype.atype.u32{.sat}.mode.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.u32{.sat}.mode  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vshl, vshr };\n.dtype = .atype = { .u32, .s32 };\n.mode  = { .clamp, .wrap };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
              ),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(
                InstructionForm(
                  "vshl",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("u32"),
                    _root_.scala.collection.immutable.Set("clamp", "wrap")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vshl",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("u32"),
                    _root_.scala.collection.immutable.Set("clamp", "wrap"),
                    _root_.scala.collection.immutable.Set("add", "max", "min")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vshl",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("u32"),
                    _root_.scala.collection.immutable.Set("clamp", "wrap")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                )
              )
            )
          ))
      )
  case Vshr extends InstructionDef(
        "vshr",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3 || operands.size == 4)
            Right(Instruction.Vshr(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vshr", Col(3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "scalar-video-instructions-vshl-vshr",
              "9.7.19.1.2. Scalar Video Instructions: vshl, vshr",
              "vshl, vshr",
              "Scalar Video Instructions",
              Col(
                "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.u32{.sat}.mode       d, a{.asel}, b{.bsel};\nvop.dtype.atype.u32{.sat}.mode.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.u32{.sat}.mode  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vshl, vshr };\n.dtype = .atype = { .u32, .s32 };\n.mode  = { .clamp, .wrap };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
              ),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(
                InstructionForm(
                  "vshr",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("u32"),
                    _root_.scala.collection.immutable.Set("clamp", "wrap")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vshr",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("u32"),
                    _root_.scala.collection.immutable.Set("clamp", "wrap"),
                    _root_.scala.collection.immutable.Set("add", "max", "min")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vshr",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("u32"),
                    _root_.scala.collection.immutable.Set("clamp", "wrap")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                )
              )
            )
          ))
      )
  case Vsub extends InstructionDef(
        "vsub",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3 || operands.size == 4)
            Right(Instruction.Vsub(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vsub", Col(3, 4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax",
              "9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax",
              "vadd, vsub, vabsdiff, vmin, vmax",
              "Scalar Video Instructions",
              Col(
                "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
              ),
              PTXVersion(2, 0),
              SMTarget(20),
              Col(
                InstructionForm(
                  "vsub",
                  Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vsub",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("add", "max", "min")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vsub",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                )
              )
            )
          ))
      )
  case Vsub2 extends InstructionDef(
        "vsub2",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Vsub2(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vsub2", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2",
              "9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
              "vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
              "SIMD Video Instructions",
              Col(
                "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
              ),
              PTXVersion(3, 0),
              SMTarget(30),
              Col(
                InstructionForm(
                  "vsub2",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vsub2",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("add")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case Vsub4 extends InstructionDef(
        "vsub4",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 4)
            Right(Instruction.Vsub4(operands(0), operands(1), operands(2), operands(3), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("vsub4", Col(4), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4",
              "9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
              "vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
              "SIMD Video Instructions",
              Col(
                "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
              ),
              PTXVersion(3, 0),
              SMTarget(30),
              Col(
                InstructionForm(
                  "vsub4",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32")
                  ),
                  _root_.scala.collection.immutable.Set("sat"),
                  true
                ),
                InstructionForm(
                  "vsub4",
                  Col(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Col(
                    _root_.scala.collection.immutable.Set("dtype"),
                    _root_.scala.collection.immutable.Set("atype"),
                    _root_.scala.collection.immutable.Set("s32", "u32"),
                    _root_.scala.collection.immutable.Set("add")
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                )
              )
            )
          ))
      )
  case Xor extends InstructionDef(
        "xor",
        (
            operands: Col[Operand],
            mods: Col[Modifier],
            pred: Option[Predication]
        ) => {
          if (operands.size == 3) Right(Instruction.Xor(operands(0), operands(1), operands(2), mods, pred))
          else Left(InstructionDecodeError.InvalidOperandCount("xor", Col(3), operands.size))
        },
        () =>
          Metadata(Col(
            Metadata.Entry(
              "logic-and-shift-instructions-xor",
              "9.7.8.3. Logic and Shift Instructions: xor",
              "xor",
              "Logic and Shift Instructions",
              Col("xor.type d, a, b;\n\n.type = { .pred, .b16, .b32, .b64 };"),
              PTXVersion(1, 0),
              SMTarget(10),
              Col(InstructionForm(
                "xor",
                Col(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                Col(_root_.scala.collection.immutable.Set("b16", "b32", "b64", "pred")),
                _root_.scala.collection.immutable.Set(),
                true
              ))
            )
          ))
      )

  final lazy val meta: Metadata = metadata()

  private[v9_3] final val fixedMods: Col[String] = opcode.split('.').toCol.drop(1)
}

enum Instruction(val definition: InstructionDef, operandValues: Operand*) {
  case Abs(dest: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Abs, dest, srcA)
  case Activemask(dest: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Activemask, dest)
  case Add(dest: Operand, srcA: Operand, srcB: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Add, dest, srcA, srcB)
  case AddCc(dest: Operand, srcA: Operand, srcB: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.AddCc, dest, srcA, srcB)
  case Addc(dest: Operand, srcA: Operand, srcB: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Addc, dest, srcA, srcB)
  case Alloca(
      pointer: Operand,
      size: Operand,
      alignment: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Alloca, (Col(pointer, size) ++ alignment.toCol)*)
  case And(dest: Operand, srcA: Operand, srcB: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.And, dest, srcA, srcB)
  case ApplyPriority(address: Operand, size: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.ApplyPriority, address, size)
  case Atom(
      dest: Operand,
      address: Operand,
      value: Operand,
      compareOrCachePolicy: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Atom, (Col(dest, address, value) ++ compareOrCachePolicy.toCol)*)
  case Bar(
      barrier: Operand,
      threadCount: Option[Operand],
      predicate: Option[Operand],
      value: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Bar, (Col(barrier) ++ threadCount.toCol ++ predicate.toCol ++ value.toCol)*)
  case BarWarpSync(barrier: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.BarWarpSync, barrier)
  case Barrier(
      barrier: Operand,
      threadCount: Option[Operand],
      predicate: Option[Operand],
      value: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Barrier, (Col(barrier) ++ threadCount.toCol ++ predicate.toCol ++ value.toCol)*)
  case BarrierCluster(mods: Col[Modifier], pred: Option[Predication]) extends Instruction(InstructionDef.BarrierCluster)
  case Bfe(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Bfe, dest, srcA, srcB, srcC)
  case Bfi(
      dest: Operand,
      insertValue: Operand,
      baseValue: Operand,
      bitPosition: Operand,
      bitLength: Operand,
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Bfi, dest, insertValue, baseValue, bitPosition, bitLength)
  case Bfind(dest: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Bfind, dest, srcA)
  case Bmsk(dest: Operand, srcA: Operand, srcB: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Bmsk, dest, srcA, srcB)
  case Bra(target: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Bra, target)
  case Brev(dest: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Brev, dest, srcA)
  case Brkpt(mods: Col[Modifier], pred: Option[Predication]) extends Instruction(InstructionDef.Brkpt)
  case BrxIdx(index: Operand, tlist: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.BrxIdx, index, tlist)
  case Call(
      function: Operand,
      returnParams: Option[Operand],
      params: Option[Operand],
      targets: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Call, (Col(function) ++ returnParams.toCol ++ params.toCol ++ targets.toCol)*)
  case Clmad(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Clmad, dest, srcA, srcB, srcC)
  case ClusterLaunchControlQueryCancel(
      predicate: Operand,
      tryCancelResponse: Operand,
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.ClusterLaunchControlQueryCancel, predicate, tryCancelResponse)
  case ClusterLaunchControlTryCancel(
      address: Operand,
      barrierAddr: Operand,
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.ClusterLaunchControlTryCancel, address, barrierAddr)
  case Clz(dest: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Clz, dest, srcA)
  case Cnot(dest: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Cnot, dest, srcA)
  case CopySign(dest: Operand, srcA: Operand, srcB: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.CopySign, dest, srcA, srcB)
  case Cos(dest: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Cos, dest, srcA)
  case CpAsync(
      destAddr: Operand,
      srcAddr: Operand,
      copySize: Operand,
      srcSizeOrIgnore: Option[Operand],
      cachePolicy: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        InstructionDef.CpAsync,
        (Col(destAddr, srcAddr, copySize) ++ srcSizeOrIgnore.toCol ++ cachePolicy.toCol)*
      )
  case CpAsyncBulkMBarrier(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      barrierAddr: Operand,
      ignoredBytesLeft: Option[Operand],
      ignoredBytesRight: Option[Operand],
      ctaMask: Option[Operand],
      cachePolicy: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        InstructionDef.CpAsyncBulkMBarrier,
        (Col(destAddr, srcAddr, size) ++ ignoredBytesLeft.toCol ++ ignoredBytesRight.toCol ++ Col(
          barrierAddr
        ) ++ ctaMask.toCol ++ cachePolicy.toCol)*
      )
  case CpAsyncBulkGroup(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      cachePolicy: Option[Operand],
      byteMask: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        InstructionDef.CpAsyncBulkGroup,
        (Col(destAddr, srcAddr, size) ++ cachePolicy.toCol ++ byteMask.toCol)*
      )
  case CpAsyncBulkCommitGroup(mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.CpAsyncBulkCommitGroup)
  case CpAsyncBulkPrefetch(
      srcAddr: Operand,
      size: Operand,
      cachePolicy: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.CpAsyncBulkPrefetch, (Col(srcAddr, size) ++ cachePolicy.toCol)*)
  case CpAsyncBulkPrefetchTensor(
      tensorMapAndCoordinates: Operand,
      im2colInfo: Option[Operand],
      cachePolicy: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        InstructionDef.CpAsyncBulkPrefetchTensor,
        (Col(tensorMapAndCoordinates) ++ im2colInfo.toCol ++ cachePolicy.toCol)*
      )
  case CpAsyncBulkTensorMBarrier(
      destAddr: Operand,
      tensorMapAndCoordinates: Operand,
      barrierAddr: Operand,
      im2colInfo: Option[Operand],
      ctaMask: Option[Operand],
      cachePolicy: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        InstructionDef.CpAsyncBulkTensorMBarrier,
        (Col(destAddr, tensorMapAndCoordinates, barrierAddr) ++ im2colInfo.toCol ++ ctaMask.toCol ++ cachePolicy.toCol)*
      )
  case CpAsyncBulkTensorGroup(
      tensorMapAndCoordinates: Operand,
      srcAddr: Operand,
      cachePolicy: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        InstructionDef.CpAsyncBulkTensorGroup,
        (Col(tensorMapAndCoordinates, srcAddr) ++ cachePolicy.toCol)*
      )
  case CpAsyncBulkWaitGroup(pendingGroupCount: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.CpAsyncBulkWaitGroup, pendingGroupCount)
  case CpAsyncCommitGroup(mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.CpAsyncCommitGroup)
  case CpAsyncMbarrierArrive(address: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.CpAsyncMbarrierArrive, address)
  case CpAsyncWaitAll(mods: Col[Modifier], pred: Option[Predication]) extends Instruction(InstructionDef.CpAsyncWaitAll)
  case CpAsyncWaitGroup(pendingGroupCount: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.CpAsyncWaitGroup, pendingGroupCount)
  case CpReduceAsyncBulk(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      cachePolicy: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.CpReduceAsyncBulk, (Col(destAddr, srcAddr, size) ++ cachePolicy.toCol)*)
  case CpReduceAsyncBulkTensor(
      address: Operand,
      srcAddr: Operand,
      cachePolicy: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.CpReduceAsyncBulkTensor, (Col(address, srcAddr) ++ cachePolicy.toCol)*)
  case CreatePolicy(
      cachePolicy: Operand,
      accessProperty: Option[Operand],
      primarySize: Option[Operand],
      totalSize: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        InstructionDef.CreatePolicy,
        (Col(cachePolicy) ++ accessProperty.toCol ++ primarySize.toCol ++ totalSize.toCol)*
      )
  case Cvt(
      dest: Operand,
      src: Operand,
      secondarySrc: Option[Operand],
      roundingBits: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Cvt, (Col(dest, src) ++ secondarySrc.toCol ++ roundingBits.toCol)*)
  case CvtPack(
      dest: Operand,
      src: Operand,
      secondarySrc: Operand,
      roundingBits: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.CvtPack, (Col(dest, src, secondarySrc) ++ roundingBits.toCol)*)
  case Cvta(dest: Operand, src: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Cvta, dest, src)
  case Discard(address: Operand, size: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Discard, address, size)
  case Div(dest: Operand, srcA: Operand, srcB: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Div, dest, srcA, srcB)
  case Dp2a(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Dp2a, dest, srcA, srcB, srcC)
  case Dp4a(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Dp4a, dest, srcA, srcB, srcC)
  case ElectSync(dest: Operand, memberMask: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.ElectSync, dest, memberMask)
  case Ex2(dest: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Ex2, dest, srcA)
  case Exit(mods: Col[Modifier], pred: Option[Predication]) extends Instruction(InstructionDef.Exit)
  case FabricSubmit(mods: Col[Modifier], pred: Option[Predication]) extends Instruction(InstructionDef.FabricSubmit)
  case FabricTryGet(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      barrierAddr: Operand,
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.FabricTryGet, destAddr, srcAddr, size, barrierAddr)
  case FabricTryPullred(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      barrierAddr: Operand,
      memberMask: Operand,
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.FabricTryPullred, destAddr, srcAddr, size, barrierAddr, memberMask)
  case FabricTryPut(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      barrierAddr: Operand,
      bytemask: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.FabricTryPut, (Col(destAddr, srcAddr, size, barrierAddr) ++ bytemask.toCol)*)
  case FabricTryRed(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      barrierAddr: Operand,
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.FabricTryRed, destAddr, srcAddr, size, barrierAddr)
  case FabricWait(mods: Col[Modifier], pred: Option[Predication]) extends Instruction(InstructionDef.FabricWait)
  case Fence(address: Option[Operand], size: Option[Operand], mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Fence, (address.toCol ++ size.toCol)*)
  case Fma(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Fma, dest, srcA, srcB, srcC)
  case Fns(dest: Operand, mask: Operand, base: Operand, offset: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Fns, dest, mask, base, offset)
  case GetCtaRank(dest: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.GetCtaRank, dest, srcA)
  case GridDependencyControl(mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.GridDependencyControl)
  case IsSpacePredicate(predicate: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.IsSpacePredicate, predicate, srcA)
  case IsTypePredicate(predicate: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.IsTypePredicate, predicate, srcA)
  case Ld(dest: Operand, address: Operand, cachePolicy: Option[Operand], mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Ld, (Col(dest, address) ++ cachePolicy.toCol)*)
  case LdGlobalNc(
      dest: Operand,
      address: Operand,
      cachePolicy: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.LdGlobalNc, (Col(dest, address) ++ cachePolicy.toCol)*)
  case Ldu(dest: Operand, address: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Ldu, dest, address)
  case Lg2(dest: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Lg2, dest, srcA)
  case Lop3(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      immLut: Operand,
      predicateQ: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Lop3, (Col(dest, srcA, srcB, srcC, immLut) ++ predicateQ.toCol)*)
  case Mad(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Mad, dest, srcA, srcB, srcC)
  case MadCc(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.MadCc, dest, srcA, srcB, srcC)
  case Mad24(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Mad24, dest, srcA, srcB, srcC)
  case Madc(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Madc, dest, srcA, srcB, srcC)
  case Mapa(dest: Operand, srcA: Operand, srcB: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Mapa, dest, srcA, srcB)
  case MatchAllSync(dest: Operand, srcA: Operand, memberMask: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.MatchAllSync, dest, srcA, memberMask)
  case MatchAnySync(dest: Operand, srcA: Operand, memberMask: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.MatchAnySync, dest, srcA, memberMask)
  case Max(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Max, (Col(dest, srcA, srcB) ++ srcC.toCol)*)
  case MbarrierArrive(
      state: Operand,
      barrierAddr: Operand,
      countOrTransactionCount: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.MbarrierArrive, (Col(state, barrierAddr) ++ countOrTransactionCount.toCol)*)
  case MbarrierArriveDrop(
      state: Operand,
      barrierAddr: Operand,
      countOrTransactionCount: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.MbarrierArriveDrop, (Col(state, barrierAddr) ++ countOrTransactionCount.toCol)*)
  case MbarrierCheckLayout(predicate: Operand, barrierAddr: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.MbarrierCheckLayout, predicate, barrierAddr)
  case MbarrierCompleteTx(
      barrierAddr: Operand,
      transactionCount: Operand,
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.MbarrierCompleteTx, barrierAddr, transactionCount)
  case MbarrierExpectTx(barrierAddr: Operand, transactionCount: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.MbarrierExpectTx, barrierAddr, transactionCount)
  case MbarrierInit(barrierAddr: Operand, count: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.MbarrierInit, barrierAddr, count)
  case MbarrierInval(barrierAddr: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.MbarrierInval, barrierAddr)
  case MbarrierPendingCount(dest: Operand, state: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.MbarrierPendingCount, dest, state)
  case MbarrierTestWait(
      completion: Operand,
      reportValue: Option[Operand],
      barrierAddr: Operand,
      stateOrPhase: Operand,
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        InstructionDef.MbarrierTestWait,
        (Col(completion) ++ reportValue.toCol ++ Col(barrierAddr, stateOrPhase))*
      )
  case MbarrierTryWait(
      completion: Operand,
      reportValue: Option[Operand],
      barrierAddr: Operand,
      stateOrPhase: Operand,
      timeHint: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        InstructionDef.MbarrierTryWait,
        (Col(completion) ++ reportValue.toCol ++ Col(barrierAddr, stateOrPhase) ++ timeHint.toCol)*
      )
  case Membar(mods: Col[Modifier], pred: Option[Predication]) extends Instruction(InstructionDef.Membar)
  case Min(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Min, (Col(dest, srcA, srcB) ++ srcC.toCol)*)
  case Mov(dest: Operand, src: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Mov, dest, src)
  case Mul(dest: Operand, srcA: Operand, srcB: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Mul, dest, srcA, srcB)
  case Mul24(dest: Operand, srcA: Operand, srcB: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Mul24, dest, srcA, srcB)
  case MultimemCpAsyncBulk(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      byteMask: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.MultimemCpAsyncBulk, (Col(destAddr, srcAddr, size) ++ byteMask.toCol)*)
  case MultimemCpReduceAsyncBulk(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.MultimemCpReduceAsyncBulk, destAddr, srcAddr, size)
  case MultimemLdReduce(dest: Operand, address: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.MultimemLdReduce, dest, address)
  case MultimemRed(address: Operand, value: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.MultimemRed, address, value)
  case MultimemRedAsync(address: Operand, value: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.MultimemRedAsync, address, value)
  case MultimemSt(address: Operand, value: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.MultimemSt, address, value)
  case MultimemStAsync(address: Operand, value: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.MultimemStAsync, address, value)
  case NanoSleep(nanoseconds: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.NanoSleep, nanoseconds)
  case Neg(dest: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Neg, dest, srcA)
  case Not(dest: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Not, dest, srcA)
  case Or(dest: Operand, srcA: Operand, srcB: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Or, dest, srcA, srcB)
  case PmEvent(event: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.PmEvent, event)
  case Popc(dest: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Popc, dest, srcA)
  case Prefetch(address: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Prefetch, address)
  case Prefetchu(address: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Prefetchu, address)
  case Prmt(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Prmt, dest, srcA, srcB, srcC)
  case Rcp(dest: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Rcp, dest, srcA)
  case RcpApproxFtzF64(dest: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.RcpApproxFtzF64, dest, srcA)
  case Red(
      address: Operand,
      value: Operand,
      cachePolicy: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Red, (Col(address, value) ++ cachePolicy.toCol)*)
  case RedAsync(
      address: Operand,
      value: Operand,
      cachePolicy: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.RedAsync, (Col(address, value) ++ cachePolicy.toCol)*)
  case ReduxSync(dest: Operand, src: Operand, memberMask: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.ReduxSync, dest, src, memberMask)
  case Rem(dest: Operand, srcA: Operand, srcB: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Rem, dest, srcA, srcB)
  case Ret(mods: Col[Modifier], pred: Option[Predication]) extends Instruction(InstructionDef.Ret)
  case Rsqrt(dest: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Rsqrt, dest, srcA)
  case RsqrtApproxFtzF64(dest: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.RsqrtApproxFtzF64, dest, srcA)
  case Sad(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Sad, dest, srcA, srcB, srcC)
  case Selp(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Selp, dest, srcA, srcB, srcC)
  case Set(
      dest: Operand,
      left: Operand,
      right: Operand,
      booleanSrc: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Set, (Col(dest, left, right) ++ booleanSrc.toCol)*)
  case SetMaxRegisters(immRegCount: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.SetMaxRegisters, immRegCount)
  case Setp(
      dest: Operand,
      left: Operand,
      right: Operand,
      booleanSrc: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Setp, (Col(dest, left, right) ++ booleanSrc.toCol)*)
  case Shf(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Shf, dest, srcA, srcB, srcC)
  case Shfl(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Shfl, dest, srcA, srcB, srcC)
  case ShflSync(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      memberMask: Operand,
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.ShflSync, dest, srcA, srcB, srcC, memberMask)
  case Shl(dest: Operand, srcA: Operand, srcB: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Shl, dest, srcA, srcB)
  case Shr(dest: Operand, srcA: Operand, srcB: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Shr, dest, srcA, srcB)
  case Sin(dest: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Sin, dest, srcA)
  case Slct(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Slct, dest, srcA, srcB, srcC)
  case Sqrt(dest: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Sqrt, dest, srcA)
  case St(
      address: Operand,
      value: Operand,
      cachePolicy: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.St, (Col(address, value) ++ cachePolicy.toCol)*)
  case StAsync(
      address: Operand,
      value: Operand,
      cachePolicy: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.StAsync, (Col(address, value) ++ cachePolicy.toCol)*)
  case StBulk(address: Operand, value: Operand, cachePolicy: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.StBulk, address, value, cachePolicy)
  case StackRestore(srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.StackRestore, srcA)
  case StackSave(dest: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.StackSave, dest)
  case Sub(dest: Operand, srcA: Operand, srcB: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Sub, dest, srcA, srcB)
  case SubCc(dest: Operand, srcA: Operand, srcB: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.SubCc, dest, srcA, srcB)
  case Subc(dest: Operand, srcA: Operand, srcB: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Subc, dest, srcA, srcB)
  case Suld(dest: Operand, address: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Suld, dest, address)
  case Suq(dest: Operand, address: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Suq, dest, address)
  case Sured(address: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Sured, address, srcC)
  case Sust(address: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Sust, address, srcC)
  case Szext(dest: Operand, srcA: Operand, srcB: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Szext, dest, srcA, srcB)
  case Tanh(dest: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Tanh, dest, srcA)
  case Tcgen05Alloc(destAddr: Operand, columnCount: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Tcgen05Alloc, destAddr, columnCount)
  case Tcgen05Commit(barrierAddr: Operand, ctaMask: Option[Operand], mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Tcgen05Commit, (Col(barrierAddr) ++ ctaMask.toCol)*)
  case Tcgen05Cp(destAddr: Operand, srcDesc: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Tcgen05Cp, destAddr, srcDesc)
  case Tcgen05Dealloc(tensorMemoryAddr: Operand, columnCount: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Tcgen05Dealloc, tensorMemoryAddr, columnCount)
  case Tcgen05FenceAfterThreadSync(mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Tcgen05FenceAfterThreadSync)
  case Tcgen05FenceBeforeThreadSync(mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Tcgen05FenceBeforeThreadSync)
  case Tcgen05Ld(
      destRegs: Operand,
      reductionValue: Operand,
      address: Option[Operand],
      halfSplitOffset: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        InstructionDef.Tcgen05Ld,
        (Col(destRegs, reductionValue) ++ address.toCol ++ halfSplitOffset.toCol)*
      )
  case Tcgen05Mma(
      destAddr: Operand,
      srcA: Operand,
      srcBDesc: Operand,
      instDesc: Operand,
      scaleAAddr: Option[Operand],
      scaleBAddr: Option[Operand],
      disabledOutputLanes: Option[Operand],
      enableInputD: Operand,
      scaleInputD: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        InstructionDef.Tcgen05Mma,
        (Col(
          destAddr,
          srcA,
          srcBDesc,
          instDesc
        ) ++ scaleAAddr.toCol ++ scaleBAddr.toCol ++ disabledOutputLanes.toCol ++ Col(
          enableInputD
        ) ++ scaleInputD.toCol)*
      )
  case Tcgen05MmaSp(
      destAddr: Operand,
      srcA: Operand,
      srcBDesc: Operand,
      sparseMetadataAddr: Operand,
      instDesc: Operand,
      scaleAAddr: Option[Operand],
      scaleBAddr: Option[Operand],
      disabledOutputLanes: Option[Operand],
      enableInputD: Operand,
      scaleInputD: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        InstructionDef.Tcgen05MmaSp,
        (Col(
          destAddr,
          srcA,
          srcBDesc,
          sparseMetadataAddr,
          instDesc
        ) ++ scaleAAddr.toCol ++ scaleBAddr.toCol ++ disabledOutputLanes.toCol ++ Col(
          enableInputD
        ) ++ scaleInputD.toCol)*
      )
  case Tcgen05MmaWs(
      destAddr: Operand,
      srcA: Operand,
      srcBDesc: Operand,
      instDesc: Operand,
      enableInputD: Operand,
      zeroColumnMaskDesc: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        InstructionDef.Tcgen05MmaWs,
        (Col(destAddr, srcA, srcBDesc, instDesc, enableInputD) ++ zeroColumnMaskDesc.toCol)*
      )
  case Tcgen05MmaWsSp(
      destAddr: Operand,
      srcA: Operand,
      srcBDesc: Operand,
      sparseMetadataAddr: Operand,
      instDesc: Operand,
      enableInputD: Operand,
      zeroColumnMaskDesc: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        InstructionDef.Tcgen05MmaWsSp,
        (Col(destAddr, srcA, srcBDesc, sparseMetadataAddr, instDesc, enableInputD) ++ zeroColumnMaskDesc.toCol)*
      )
  case Tcgen05RelinquishAllocPermit(mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Tcgen05RelinquishAllocPermit)
  case Tcgen05Shift(tensorMemoryAddr: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Tcgen05Shift, tensorMemoryAddr)
  case Tcgen05St(
      address: Operand,
      halfSplitOffset: Operand,
      srcRegs: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Tcgen05St, (Col(address, halfSplitOffset) ++ srcRegs.toCol)*)
  case Tcgen05WaitLd(mods: Col[Modifier], pred: Option[Predication]) extends Instruction(InstructionDef.Tcgen05WaitLd)
  case Tcgen05WaitSt(mods: Col[Modifier], pred: Option[Predication]) extends Instruction(InstructionDef.Tcgen05WaitSt)
  case TensorMapCopyFenceProxy(
      dest: Operand,
      srcOrOrdinal: Operand,
      valueOrSize: Operand,
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.TensorMapCopyFenceProxy, dest, srcOrOrdinal, valueOrSize)
  case TensorMapReplace(
      dest: Operand,
      srcOrOrdinal: Operand,
      valueOrSize: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.TensorMapReplace, (Col(dest, srcOrOrdinal) ++ valueOrSize.toCol)*)
  case TestPredicate(predicate: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.TestPredicate, predicate, srcA)
  case Tex(
      dest: Operand,
      textureAndCoordinates: Operand,
      lodOffsetOrDerivativeX: Option[Operand],
      depthCompareOrDerivativeY: Option[Operand],
      offset: Option[Operand],
      depthCompare: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        InstructionDef.Tex,
        (Col(
          dest,
          textureAndCoordinates
        ) ++ lodOffsetOrDerivativeX.toCol ++ depthCompareOrDerivativeY.toCol ++ offset.toCol ++ depthCompare.toCol)*
      )
  case Tld4(
      dest: Operand,
      textureAndCoordinates: Operand,
      offset: Option[Operand],
      depthCompare: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Tld4, (Col(dest, textureAndCoordinates) ++ offset.toCol ++ depthCompare.toCol)*)
  case Trap(mods: Col[Modifier], pred: Option[Predication]) extends Instruction(InstructionDef.Trap)
  case Txq(dest: Operand, texture: Operand, lod: Option[Operand], mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Txq, (Col(dest, texture) ++ lod.toCol)*)
  case Vabsdiff(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Vabsdiff, (Col(dest, srcA, srcB) ++ srcC.toCol)*)
  case Vabsdiff2(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Vabsdiff2, dest, srcA, srcB, srcC)
  case Vabsdiff4(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Vabsdiff4, dest, srcA, srcB, srcC)
  case Vadd(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Vadd, (Col(dest, srcA, srcB) ++ srcC.toCol)*)
  case Vadd2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Vadd2, dest, srcA, srcB, srcC)
  case Vadd4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Vadd4, dest, srcA, srcB, srcC)
  case Vavrg2(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Vavrg2, dest, srcA, srcB, srcC)
  case Vavrg4(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Vavrg4, dest, srcA, srcB, srcC)
  case Vmad(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Vmad, dest, srcA, srcB, srcC)
  case Vmax(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Vmax, (Col(dest, srcA, srcB) ++ srcC.toCol)*)
  case Vmax2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Vmax2, dest, srcA, srcB, srcC)
  case Vmax4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Vmax4, dest, srcA, srcB, srcC)
  case Vmin(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Vmin, (Col(dest, srcA, srcB) ++ srcC.toCol)*)
  case Vmin2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Vmin2, dest, srcA, srcB, srcC)
  case Vmin4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Vmin4, dest, srcA, srcB, srcC)
  case Vote(dest: Operand, srcA: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Vote, dest, srcA)
  case VoteSync(dest: Operand, srcA: Operand, memberMask: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.VoteSync, dest, srcA, memberMask)
  case Vset(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Vset, (Col(dest, srcA, srcB) ++ srcC.toCol)*)
  case Vset2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Vset2, dest, srcA, srcB, srcC)
  case Vset4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Vset4, dest, srcA, srcB, srcC)
  case Vshl(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Vshl, (Col(dest, srcA, srcB) ++ srcC.toCol)*)
  case Vshr(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Vshr, (Col(dest, srcA, srcB) ++ srcC.toCol)*)
  case Vsub(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Col[Modifier],
      pred: Option[Predication]
  ) extends Instruction(InstructionDef.Vsub, (Col(dest, srcA, srcB) ++ srcC.toCol)*)
  case Vsub2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Vsub2, dest, srcA, srcB, srcC)
  case Vsub4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Vsub4, dest, srcA, srcB, srcC)
  case Xor(dest: Operand, srcA: Operand, srcB: Operand, mods: Col[Modifier], pred: Option[Predication])
      extends Instruction(InstructionDef.Xor, dest, srcA, srcB)

  final val operands: Col[Operand] = operandValues.toCol

  final def opcode: String = definition.opcode

  final def decoder: Decoder = definition.decoder

  final def meta: Metadata = definition.meta

  def mods: Col[Modifier]

  def pred: Option[Predication]

  final def qualifiedOpcode: String = (opcode +: mods.map(_.name)).mkString(".")

}

object Instruction {
  private lazy val DefinitionsByMnemonic: Map[String, Col[InstructionDef]] =
    Instructions.groupMap(_.opcode.takeWhile(_ != '.'))(identity)

  def decode(
      opcode: String,
      operands: Col[Operand],
      mods: Col[Modifier] = Col.empty,
      pred: Option[Predication] = None
  ): Either[InstructionDecodeError, Instruction] = {
    val opcodeParts = normalizeOpcode(opcode).split('.').toCol.filter(_.nonEmpty)
    val name = opcodeParts.headOption.getOrElse("")
    val allModifiers = opcodeParts.drop(1).map(Modifier.apply) ++ mods
    val qualified = (name +: allModifiers.map(_.name)).mkString(".")
    val modifierNames = allModifiers.map(_.name)
    val candidates = DefinitionsByMnemonic.getOrElse(name, Col.empty[InstructionDef])
      .filter(definition => modifierNames.startsWith(definition.fixedMods))
    val specificity = candidates.map(_.fixedMods.size).maxOption.getOrElse(0)
    val attempts = candidates.filter(_.fixedMods.size == specificity).map { definition =>
      definition -> definition.decoder(operands, allModifiers.drop(definition.fixedMods.size), pred)
    }
    val successes = attempts.collect { case (definition, Right(instruction)) => definition -> instruction }
    successes match {
      case Col((_, instruction)) => Right(instruction)
      case values if values.nonEmpty =>
        Left(InstructionDecodeError.AmbiguousInstruction(qualified, values.map(_._1.toString)))
      case _ =>
        val expected = attempts.collect {
          case (_, Left(InstructionDecodeError.InvalidOperandCount(_, counts, _))) => counts
        }.flatten.distinct.sorted
        if (expected.nonEmpty)
          Left(InstructionDecodeError.InvalidOperandCount(qualified, expected, operands.size))
        else Left(InstructionDecodeError.UnknownOpcode(qualified))
    }
  }
}

val Version: PTXVersion = PTXVersion(9, 3)

val Instructions: Col[InstructionDef] = InstructionDef.values.toCol
