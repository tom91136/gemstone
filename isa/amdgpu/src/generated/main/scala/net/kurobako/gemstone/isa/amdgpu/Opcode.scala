// Generated from the pinned AMDGPU instruction metadata. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.string.*

object Opcode {
  enum ComparisonKind {
    case Signed16, Signed32, Signed64, Unsigned16, Unsigned32, Unsigned64
    case Float16, Float32, Float64, ClassFloat32, ClassFloat64
  }

  enum ComparisonRelation {
    case Equal, NotEqual, OrderedNotEqual, Less, LessOrEqual, Greater, GreaterOrEqual
    case NotLess, NotLessOrEqual, NotGreater, NotGreaterOrEqual
    case Ordered, Unordered, NotLessOrGreater, Class
  }

  case class ComparisonOpcode(
      kind: ComparisonKind,
      relation: ComparisonRelation,
      updateExec: Boolean
  )

  val ComparisonOpcodes: Map[Opcode, ComparisonOpcode] = Map(
    SOPCInstructionDefs.SCmpEqF16 -> ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Equal, false),
    SOPCInstructionDefs.SCmpEqF32 -> ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Equal, false),
    SOPCInstructionDefs.SCmpEqI32 -> ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Equal, false),
    SOPCInstructionDefs.SCmpEqU32 -> ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Equal, false),
    SOPCInstructionDefs.SCmpEqU64 -> ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.Equal, false),
    SOPCInstructionDefs.SCmpGeF16 -> ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.GreaterOrEqual, false),
    SOPCInstructionDefs.SCmpGeF32 -> ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.GreaterOrEqual, false),
    SOPCInstructionDefs.SCmpGeI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.GreaterOrEqual, false),
    SOPCInstructionDefs.SCmpGeU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.GreaterOrEqual, false),
    SOPCInstructionDefs.SCmpGtF16 -> ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Greater, false),
    SOPCInstructionDefs.SCmpGtF32 -> ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Greater, false),
    SOPCInstructionDefs.SCmpGtI32 -> ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Greater, false),
    SOPCInstructionDefs.SCmpGtU32 -> ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Greater, false),
    SOPCInstructionDefs.SCmpLeF16 -> ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.LessOrEqual, false),
    SOPCInstructionDefs.SCmpLeF32 -> ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.LessOrEqual, false),
    SOPCInstructionDefs.SCmpLeI32 -> ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.LessOrEqual, false),
    SOPCInstructionDefs.SCmpLeU32 -> ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.LessOrEqual, false),
    SOPCInstructionDefs.SCmpLgF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.OrderedNotEqual, false),
    SOPCInstructionDefs.SCmpLgF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.OrderedNotEqual, false),
    SOPCInstructionDefs.SCmpLgI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.OrderedNotEqual, false),
    SOPCInstructionDefs.SCmpLgU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.OrderedNotEqual, false),
    SOPCInstructionDefs.SCmpLgU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.OrderedNotEqual, false),
    SOPCInstructionDefs.SCmpLtF16 -> ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Less, false),
    SOPCInstructionDefs.SCmpLtF32 -> ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Less, false),
    SOPCInstructionDefs.SCmpLtI32 -> ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Less, false),
    SOPCInstructionDefs.SCmpLtU32 -> ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Less, false),
    SOPCInstructionDefs.SCmpNeqF16 -> ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotEqual, false),
    SOPCInstructionDefs.SCmpNeqF32 -> ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotEqual, false),
    SOPCInstructionDefs.SCmpNgeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotGreaterOrEqual, false),
    SOPCInstructionDefs.SCmpNgeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotGreaterOrEqual, false),
    SOPCInstructionDefs.SCmpNgtF16 -> ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotGreater, false),
    SOPCInstructionDefs.SCmpNgtF32 -> ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotGreater, false),
    SOPCInstructionDefs.SCmpNleF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLessOrEqual, false),
    SOPCInstructionDefs.SCmpNleF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLessOrEqual, false),
    SOPCInstructionDefs.SCmpNlgF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLessOrGreater, false),
    SOPCInstructionDefs.SCmpNlgF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLessOrGreater, false),
    SOPCInstructionDefs.SCmpNltF16 -> ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLess, false),
    SOPCInstructionDefs.SCmpNltF32 -> ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLess, false),
    SOPCInstructionDefs.SCmpOF16 -> ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Ordered, false),
    SOPCInstructionDefs.SCmpOF32 -> ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Ordered, false),
    SOPCInstructionDefs.SCmpUF16 -> ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Unordered, false),
    SOPCInstructionDefs.SCmpUF32 -> ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Unordered, false),
    VectorCompareInstructionDefs.VCmpClassF32 ->
      ComparisonOpcode(ComparisonKind.ClassFloat32, ComparisonRelation.Class, false),
    VectorCompareInstructionDefs.VCmpClassF64 ->
      ComparisonOpcode(ComparisonKind.ClassFloat64, ComparisonRelation.Class, false),
    VectorCompareInstructionDefs.VCmpEqF16 -> ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Equal, false),
    VectorCompareInstructionDefs.VCmpEqF32 -> ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Equal, false),
    VectorCompareInstructionDefs.VCmpEqF64 -> ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Equal, false),
    VectorCompareInstructionDefs.VCmpEqI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.Equal, false),
    VectorCompareInstructionDefs.VCmpEqI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Equal, false),
    VectorCompareInstructionDefs.VCmpEqI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.Equal, false),
    VectorCompareInstructionDefs.VCmpEqU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.Equal, false),
    VectorCompareInstructionDefs.VCmpEqU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Equal, false),
    VectorCompareInstructionDefs.VCmpEqU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.Equal, false),
    VectorCompareInstructionDefs.VCmpGeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.GreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpGeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.GreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpGeF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.GreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpGeI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.GreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpGeI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.GreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpGeI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.GreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpGeU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.GreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpGeU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.GreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpGeU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.GreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpGtF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Greater, false),
    VectorCompareInstructionDefs.VCmpGtF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Greater, false),
    VectorCompareInstructionDefs.VCmpGtF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Greater, false),
    VectorCompareInstructionDefs.VCmpGtI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.Greater, false),
    VectorCompareInstructionDefs.VCmpGtI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Greater, false),
    VectorCompareInstructionDefs.VCmpGtI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.Greater, false),
    VectorCompareInstructionDefs.VCmpGtU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.Greater, false),
    VectorCompareInstructionDefs.VCmpGtU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Greater, false),
    VectorCompareInstructionDefs.VCmpGtU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.Greater, false),
    VectorCompareInstructionDefs.VCmpLeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.LessOrEqual, false),
    VectorCompareInstructionDefs.VCmpLeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.LessOrEqual, false),
    VectorCompareInstructionDefs.VCmpLeF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.LessOrEqual, false),
    VectorCompareInstructionDefs.VCmpLeI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.LessOrEqual, false),
    VectorCompareInstructionDefs.VCmpLeI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.LessOrEqual, false),
    VectorCompareInstructionDefs.VCmpLeI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.LessOrEqual, false),
    VectorCompareInstructionDefs.VCmpLeU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.LessOrEqual, false),
    VectorCompareInstructionDefs.VCmpLeU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.LessOrEqual, false),
    VectorCompareInstructionDefs.VCmpLeU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.LessOrEqual, false),
    VectorCompareInstructionDefs.VCmpLgF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.OrderedNotEqual, false),
    VectorCompareInstructionDefs.VCmpLgF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.OrderedNotEqual, false),
    VectorCompareInstructionDefs.VCmpLgF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.OrderedNotEqual, false),
    VectorCompareInstructionDefs.VCmpLtF16 -> ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Less, false),
    VectorCompareInstructionDefs.VCmpLtF32 -> ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Less, false),
    VectorCompareInstructionDefs.VCmpLtF64 -> ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Less, false),
    VectorCompareInstructionDefs.VCmpLtI16 -> ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.Less, false),
    VectorCompareInstructionDefs.VCmpLtI32 -> ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Less, false),
    VectorCompareInstructionDefs.VCmpLtI64 -> ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.Less, false),
    VectorCompareInstructionDefs.VCmpLtU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.Less, false),
    VectorCompareInstructionDefs.VCmpLtU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Less, false),
    VectorCompareInstructionDefs.VCmpLtU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.Less, false),
    VectorCompareInstructionDefs.VCmpNeI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.NotEqual, false),
    VectorCompareInstructionDefs.VCmpNeI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.NotEqual, false),
    VectorCompareInstructionDefs.VCmpNeI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.NotEqual, false),
    VectorCompareInstructionDefs.VCmpNeU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.NotEqual, false),
    VectorCompareInstructionDefs.VCmpNeU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.NotEqual, false),
    VectorCompareInstructionDefs.VCmpNeU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.NotEqual, false),
    VectorCompareInstructionDefs.VCmpNeqF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotEqual, false),
    VectorCompareInstructionDefs.VCmpNeqF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotEqual, false),
    VectorCompareInstructionDefs.VCmpNeqF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotEqual, false),
    VectorCompareInstructionDefs.VCmpNgeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotGreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpNgeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotGreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpNgeF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotGreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpNgtF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotGreater, false),
    VectorCompareInstructionDefs.VCmpNgtF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotGreater, false),
    VectorCompareInstructionDefs.VCmpNgtF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotGreater, false),
    VectorCompareInstructionDefs.VCmpNleF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLessOrEqual, false),
    VectorCompareInstructionDefs.VCmpNleF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLessOrEqual, false),
    VectorCompareInstructionDefs.VCmpNleF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotLessOrEqual, false),
    VectorCompareInstructionDefs.VCmpNlgF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLessOrGreater, false),
    VectorCompareInstructionDefs.VCmpNlgF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLessOrGreater, false),
    VectorCompareInstructionDefs.VCmpNlgF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotLessOrGreater, false),
    VectorCompareInstructionDefs.VCmpNltF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLess, false),
    VectorCompareInstructionDefs.VCmpNltF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLess, false),
    VectorCompareInstructionDefs.VCmpNltF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotLess, false),
    VectorCompareInstructionDefs.VCmpOF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Ordered, false),
    VectorCompareInstructionDefs.VCmpOF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Ordered, false),
    VectorCompareInstructionDefs.VCmpOF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Ordered, false),
    VectorCompareInstructionDefs.VCmpUF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Unordered, false),
    VectorCompareInstructionDefs.VCmpUF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Unordered, false),
    VectorCompareInstructionDefs.VCmpUF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Unordered, false),
    VectorCompareInstructionDefs.VCmpxClassF32 ->
      ComparisonOpcode(ComparisonKind.ClassFloat32, ComparisonRelation.Class, true),
    VectorCompareInstructionDefs.VCmpxClassF64 ->
      ComparisonOpcode(ComparisonKind.ClassFloat64, ComparisonRelation.Class, true),
    VectorCompareInstructionDefs.VCmpxEqF16 -> ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Equal, true),
    VectorCompareInstructionDefs.VCmpxEqF32 -> ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Equal, true),
    VectorCompareInstructionDefs.VCmpxEqF64 -> ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Equal, true),
    VectorCompareInstructionDefs.VCmpxEqI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.Equal, true),
    VectorCompareInstructionDefs.VCmpxEqI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Equal, true),
    VectorCompareInstructionDefs.VCmpxEqI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.Equal, true),
    VectorCompareInstructionDefs.VCmpxEqU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.Equal, true),
    VectorCompareInstructionDefs.VCmpxEqU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Equal, true),
    VectorCompareInstructionDefs.VCmpxEqU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.Equal, true),
    VectorCompareInstructionDefs.VCmpxGeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.GreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxGeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.GreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxGeF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.GreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxGeI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.GreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxGeI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.GreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxGeI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.GreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxGeU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.GreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxGeU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.GreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxGeU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.GreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxGtF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Greater, true),
    VectorCompareInstructionDefs.VCmpxGtF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Greater, true),
    VectorCompareInstructionDefs.VCmpxGtF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Greater, true),
    VectorCompareInstructionDefs.VCmpxGtI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.Greater, true),
    VectorCompareInstructionDefs.VCmpxGtI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Greater, true),
    VectorCompareInstructionDefs.VCmpxGtI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.Greater, true),
    VectorCompareInstructionDefs.VCmpxGtU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.Greater, true),
    VectorCompareInstructionDefs.VCmpxGtU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Greater, true),
    VectorCompareInstructionDefs.VCmpxGtU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.Greater, true),
    VectorCompareInstructionDefs.VCmpxLeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.LessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxLeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.LessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxLeF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.LessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxLeI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.LessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxLeI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.LessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxLeI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.LessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxLeU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.LessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxLeU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.LessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxLeU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.LessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxLgF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.OrderedNotEqual, true),
    VectorCompareInstructionDefs.VCmpxLgF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.OrderedNotEqual, true),
    VectorCompareInstructionDefs.VCmpxLgF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.OrderedNotEqual, true),
    VectorCompareInstructionDefs.VCmpxLtF16 -> ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Less, true),
    VectorCompareInstructionDefs.VCmpxLtF32 -> ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Less, true),
    VectorCompareInstructionDefs.VCmpxLtF64 -> ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Less, true),
    VectorCompareInstructionDefs.VCmpxLtI16 -> ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.Less, true),
    VectorCompareInstructionDefs.VCmpxLtI32 -> ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Less, true),
    VectorCompareInstructionDefs.VCmpxLtI64 -> ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.Less, true),
    VectorCompareInstructionDefs.VCmpxLtU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.Less, true),
    VectorCompareInstructionDefs.VCmpxLtU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Less, true),
    VectorCompareInstructionDefs.VCmpxLtU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.Less, true),
    VectorCompareInstructionDefs.VCmpxNeI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.NotEqual, true),
    VectorCompareInstructionDefs.VCmpxNeI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.NotEqual, true),
    VectorCompareInstructionDefs.VCmpxNeI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.NotEqual, true),
    VectorCompareInstructionDefs.VCmpxNeU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.NotEqual, true),
    VectorCompareInstructionDefs.VCmpxNeU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.NotEqual, true),
    VectorCompareInstructionDefs.VCmpxNeU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.NotEqual, true),
    VectorCompareInstructionDefs.VCmpxNeqF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotEqual, true),
    VectorCompareInstructionDefs.VCmpxNeqF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotEqual, true),
    VectorCompareInstructionDefs.VCmpxNeqF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotEqual, true),
    VectorCompareInstructionDefs.VCmpxNgeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotGreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxNgeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotGreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxNgeF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotGreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxNgtF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotGreater, true),
    VectorCompareInstructionDefs.VCmpxNgtF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotGreater, true),
    VectorCompareInstructionDefs.VCmpxNgtF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotGreater, true),
    VectorCompareInstructionDefs.VCmpxNleF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxNleF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxNleF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotLessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxNlgF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLessOrGreater, true),
    VectorCompareInstructionDefs.VCmpxNlgF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLessOrGreater, true),
    VectorCompareInstructionDefs.VCmpxNlgF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotLessOrGreater, true),
    VectorCompareInstructionDefs.VCmpxNltF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLess, true),
    VectorCompareInstructionDefs.VCmpxNltF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLess, true),
    VectorCompareInstructionDefs.VCmpxNltF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotLess, true),
    VectorCompareInstructionDefs.VCmpxOF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Ordered, true),
    VectorCompareInstructionDefs.VCmpxOF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Ordered, true),
    VectorCompareInstructionDefs.VCmpxOF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Ordered, true),
    VectorCompareInstructionDefs.VCmpxUF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Unordered, true),
    VectorCompareInstructionDefs.VCmpxUF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Unordered, true),
    VectorCompareInstructionDefs.VCmpxUF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Unordered, true)
  )

  val definitions: Vec[Opcode] = Vec(
    SOP1InstructionDefs.definitions,
    SOP2InstructionDefs.definitions,
    SOPCInstructionDefs.definitions,
    SOPKInstructionDefs.definitions,
    SOPPInstructionDefs.definitions,
    ScalarMemoryInstructionDefs.definitions,
    VOP1InstructionDefs.definitions,
    VOP2InstructionDefs.definitions,
    VOP3InstructionDefs.definitions,
    PackedVectorInstructionDefs.definitions,
    VectorCompareInstructionDefs.definitions,
    DualVectorInstructionDefs.definitions,
    InterpolationInstructionDefs.definitions,
    MatrixInstructionDefs.definitions,
    DataShareInstructionDefs.definitions,
    BufferInstructionDefs.definitions,
    FlatInstructionDefs.definitions,
    ImageInstructionDefs.definitions,
    ExportInstructionDefs.definitions,
    LDSDirectInstructionDefs.definitions
  ).flatten

  private lazy val byName = definitions.groupBy(_.opcode.raw)
  def forOpcode(name: String): Vec[Opcode] = byName.getOrElse(name.toLowerCaseRoot, Vec.empty)
}

abstract class Opcode(val opcode: NonEmpty[String], private val metadataId: Int) {
  lazy val meta: Metadata = Metadata.Catalogue.metadata(metadataId)
  override final def toString: String = opcode
}
