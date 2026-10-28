// Generated from the pinned AMDGPU instruction metadata. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import javax.annotation.processing.Generated

import Opcode.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.string.*

@Generated(Array("sbt codegen"))
object InstructionDefs {
  val ComparisonOpcodes: Map[Opcode, ComparisonOpcode] = Map(
    SOPCInstructionDefs.SCmpEqF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Equal, updateExec = false),
    SOPCInstructionDefs.SCmpEqF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Equal, updateExec = false),
    SOPCInstructionDefs.SCmpEqI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Equal, updateExec = false),
    SOPCInstructionDefs.SCmpEqU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Equal, updateExec = false),
    SOPCInstructionDefs.SCmpEqU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.Equal, updateExec = false),
    SOPCInstructionDefs.SCmpGeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.GreaterOrEqual, updateExec = false),
    SOPCInstructionDefs.SCmpGeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.GreaterOrEqual, updateExec = false),
    SOPCInstructionDefs.SCmpGeI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.GreaterOrEqual, updateExec = false),
    SOPCInstructionDefs.SCmpGeU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.GreaterOrEqual, updateExec = false),
    SOPCInstructionDefs.SCmpGtF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Greater, updateExec = false),
    SOPCInstructionDefs.SCmpGtF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Greater, updateExec = false),
    SOPCInstructionDefs.SCmpGtI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Greater, updateExec = false),
    SOPCInstructionDefs.SCmpGtU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Greater, updateExec = false),
    SOPCInstructionDefs.SCmpLeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.LessOrEqual, updateExec = false),
    SOPCInstructionDefs.SCmpLeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.LessOrEqual, updateExec = false),
    SOPCInstructionDefs.SCmpLeI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.LessOrEqual, updateExec = false),
    SOPCInstructionDefs.SCmpLeU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.LessOrEqual, updateExec = false),
    SOPCInstructionDefs.SCmpLgF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.OrderedNotEqual, updateExec = false),
    SOPCInstructionDefs.SCmpLgF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.OrderedNotEqual, updateExec = false),
    SOPCInstructionDefs.SCmpLgI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.OrderedNotEqual, updateExec = false),
    SOPCInstructionDefs.SCmpLgU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.OrderedNotEqual, updateExec = false),
    SOPCInstructionDefs.SCmpLgU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.OrderedNotEqual, updateExec = false),
    SOPCInstructionDefs.SCmpLtF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Less, updateExec = false),
    SOPCInstructionDefs.SCmpLtF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Less, updateExec = false),
    SOPCInstructionDefs.SCmpLtI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Less, updateExec = false),
    SOPCInstructionDefs.SCmpLtU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Less, updateExec = false),
    SOPCInstructionDefs.SCmpNeqF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotEqual, updateExec = false),
    SOPCInstructionDefs.SCmpNeqF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotEqual, updateExec = false),
    SOPCInstructionDefs.SCmpNgeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotGreaterOrEqual, updateExec = false),
    SOPCInstructionDefs.SCmpNgeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotGreaterOrEqual, updateExec = false),
    SOPCInstructionDefs.SCmpNgtF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotGreater, updateExec = false),
    SOPCInstructionDefs.SCmpNgtF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotGreater, updateExec = false),
    SOPCInstructionDefs.SCmpNleF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLessOrEqual, updateExec = false),
    SOPCInstructionDefs.SCmpNleF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLessOrEqual, updateExec = false),
    SOPCInstructionDefs.SCmpNlgF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLessOrGreater, updateExec = false),
    SOPCInstructionDefs.SCmpNlgF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLessOrGreater, updateExec = false),
    SOPCInstructionDefs.SCmpNltF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLess, updateExec = false),
    SOPCInstructionDefs.SCmpNltF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLess, updateExec = false),
    SOPCInstructionDefs.SCmpOF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Ordered, updateExec = false),
    SOPCInstructionDefs.SCmpOF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Ordered, updateExec = false),
    SOPCInstructionDefs.SCmpUF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Unordered, updateExec = false),
    SOPCInstructionDefs.SCmpUF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Unordered, updateExec = false),
    VectorCompareInstructionDefs.VCmpClassF32 ->
      ComparisonOpcode(ComparisonKind.ClassFloat32, ComparisonRelation.Class, updateExec = false),
    VectorCompareInstructionDefs.VCmpClassF64 ->
      ComparisonOpcode(ComparisonKind.ClassFloat64, ComparisonRelation.Class, updateExec = false),
    VectorCompareInstructionDefs.VCmpEqF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Equal, updateExec = false),
    VectorCompareInstructionDefs.VCmpEqF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Equal, updateExec = false),
    VectorCompareInstructionDefs.VCmpEqF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Equal, updateExec = false),
    VectorCompareInstructionDefs.VCmpEqI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.Equal, updateExec = false),
    VectorCompareInstructionDefs.VCmpEqI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Equal, updateExec = false),
    VectorCompareInstructionDefs.VCmpEqI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.Equal, updateExec = false),
    VectorCompareInstructionDefs.VCmpEqU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.Equal, updateExec = false),
    VectorCompareInstructionDefs.VCmpEqU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Equal, updateExec = false),
    VectorCompareInstructionDefs.VCmpEqU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.Equal, updateExec = false),
    VectorCompareInstructionDefs.VCmpGeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.GreaterOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpGeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.GreaterOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpGeF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.GreaterOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpGeI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.GreaterOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpGeI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.GreaterOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpGeI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.GreaterOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpGeU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.GreaterOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpGeU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.GreaterOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpGeU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.GreaterOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpGtF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Greater, updateExec = false),
    VectorCompareInstructionDefs.VCmpGtF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Greater, updateExec = false),
    VectorCompareInstructionDefs.VCmpGtF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Greater, updateExec = false),
    VectorCompareInstructionDefs.VCmpGtI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.Greater, updateExec = false),
    VectorCompareInstructionDefs.VCmpGtI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Greater, updateExec = false),
    VectorCompareInstructionDefs.VCmpGtI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.Greater, updateExec = false),
    VectorCompareInstructionDefs.VCmpGtU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.Greater, updateExec = false),
    VectorCompareInstructionDefs.VCmpGtU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Greater, updateExec = false),
    VectorCompareInstructionDefs.VCmpGtU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.Greater, updateExec = false),
    VectorCompareInstructionDefs.VCmpLeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.LessOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpLeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.LessOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpLeF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.LessOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpLeI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.LessOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpLeI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.LessOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpLeI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.LessOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpLeU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.LessOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpLeU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.LessOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpLeU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.LessOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpLgF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.OrderedNotEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpLgF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.OrderedNotEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpLgF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.OrderedNotEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpLtF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Less, updateExec = false),
    VectorCompareInstructionDefs.VCmpLtF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Less, updateExec = false),
    VectorCompareInstructionDefs.VCmpLtF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Less, updateExec = false),
    VectorCompareInstructionDefs.VCmpLtI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.Less, updateExec = false),
    VectorCompareInstructionDefs.VCmpLtI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Less, updateExec = false),
    VectorCompareInstructionDefs.VCmpLtI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.Less, updateExec = false),
    VectorCompareInstructionDefs.VCmpLtU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.Less, updateExec = false),
    VectorCompareInstructionDefs.VCmpLtU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Less, updateExec = false),
    VectorCompareInstructionDefs.VCmpLtU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.Less, updateExec = false),
    VectorCompareInstructionDefs.VCmpNeI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.NotEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpNeI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.NotEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpNeI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.NotEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpNeU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.NotEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpNeU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.NotEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpNeU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.NotEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpNeqF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpNeqF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpNeqF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpNgeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotGreaterOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpNgeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotGreaterOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpNgeF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotGreaterOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpNgtF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotGreater, updateExec = false),
    VectorCompareInstructionDefs.VCmpNgtF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotGreater, updateExec = false),
    VectorCompareInstructionDefs.VCmpNgtF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotGreater, updateExec = false),
    VectorCompareInstructionDefs.VCmpNleF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLessOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpNleF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLessOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpNleF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotLessOrEqual, updateExec = false),
    VectorCompareInstructionDefs.VCmpNlgF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLessOrGreater, updateExec = false),
    VectorCompareInstructionDefs.VCmpNlgF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLessOrGreater, updateExec = false),
    VectorCompareInstructionDefs.VCmpNlgF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotLessOrGreater, updateExec = false),
    VectorCompareInstructionDefs.VCmpNltF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLess, updateExec = false),
    VectorCompareInstructionDefs.VCmpNltF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLess, updateExec = false),
    VectorCompareInstructionDefs.VCmpNltF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotLess, updateExec = false),
    VectorCompareInstructionDefs.VCmpOF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Ordered, updateExec = false),
    VectorCompareInstructionDefs.VCmpOF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Ordered, updateExec = false),
    VectorCompareInstructionDefs.VCmpOF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Ordered, updateExec = false),
    VectorCompareInstructionDefs.VCmpUF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Unordered, updateExec = false),
    VectorCompareInstructionDefs.VCmpUF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Unordered, updateExec = false),
    VectorCompareInstructionDefs.VCmpUF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Unordered, updateExec = false),
    VectorCompareInstructionDefs.VCmpxClassF32 ->
      ComparisonOpcode(ComparisonKind.ClassFloat32, ComparisonRelation.Class, updateExec = true),
    VectorCompareInstructionDefs.VCmpxClassF64 ->
      ComparisonOpcode(ComparisonKind.ClassFloat64, ComparisonRelation.Class, updateExec = true),
    VectorCompareInstructionDefs.VCmpxEqF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Equal, updateExec = true),
    VectorCompareInstructionDefs.VCmpxEqF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Equal, updateExec = true),
    VectorCompareInstructionDefs.VCmpxEqF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Equal, updateExec = true),
    VectorCompareInstructionDefs.VCmpxEqI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.Equal, updateExec = true),
    VectorCompareInstructionDefs.VCmpxEqI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Equal, updateExec = true),
    VectorCompareInstructionDefs.VCmpxEqI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.Equal, updateExec = true),
    VectorCompareInstructionDefs.VCmpxEqU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.Equal, updateExec = true),
    VectorCompareInstructionDefs.VCmpxEqU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Equal, updateExec = true),
    VectorCompareInstructionDefs.VCmpxEqU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.Equal, updateExec = true),
    VectorCompareInstructionDefs.VCmpxGeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.GreaterOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxGeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.GreaterOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxGeF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.GreaterOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxGeI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.GreaterOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxGeI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.GreaterOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxGeI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.GreaterOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxGeU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.GreaterOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxGeU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.GreaterOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxGeU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.GreaterOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxGtF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Greater, updateExec = true),
    VectorCompareInstructionDefs.VCmpxGtF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Greater, updateExec = true),
    VectorCompareInstructionDefs.VCmpxGtF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Greater, updateExec = true),
    VectorCompareInstructionDefs.VCmpxGtI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.Greater, updateExec = true),
    VectorCompareInstructionDefs.VCmpxGtI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Greater, updateExec = true),
    VectorCompareInstructionDefs.VCmpxGtI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.Greater, updateExec = true),
    VectorCompareInstructionDefs.VCmpxGtU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.Greater, updateExec = true),
    VectorCompareInstructionDefs.VCmpxGtU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Greater, updateExec = true),
    VectorCompareInstructionDefs.VCmpxGtU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.Greater, updateExec = true),
    VectorCompareInstructionDefs.VCmpxLeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.LessOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxLeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.LessOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxLeF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.LessOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxLeI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.LessOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxLeI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.LessOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxLeI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.LessOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxLeU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.LessOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxLeU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.LessOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxLeU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.LessOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxLgF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.OrderedNotEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxLgF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.OrderedNotEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxLgF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.OrderedNotEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxLtF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Less, updateExec = true),
    VectorCompareInstructionDefs.VCmpxLtF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Less, updateExec = true),
    VectorCompareInstructionDefs.VCmpxLtF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Less, updateExec = true),
    VectorCompareInstructionDefs.VCmpxLtI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.Less, updateExec = true),
    VectorCompareInstructionDefs.VCmpxLtI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Less, updateExec = true),
    VectorCompareInstructionDefs.VCmpxLtI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.Less, updateExec = true),
    VectorCompareInstructionDefs.VCmpxLtU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.Less, updateExec = true),
    VectorCompareInstructionDefs.VCmpxLtU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Less, updateExec = true),
    VectorCompareInstructionDefs.VCmpxLtU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.Less, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNeI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.NotEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNeI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.NotEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNeI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.NotEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNeU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.NotEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNeU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.NotEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNeU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.NotEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNeqF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNeqF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNeqF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNgeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotGreaterOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNgeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotGreaterOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNgeF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotGreaterOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNgtF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotGreater, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNgtF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotGreater, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNgtF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotGreater, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNleF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLessOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNleF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLessOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNleF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotLessOrEqual, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNlgF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLessOrGreater, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNlgF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLessOrGreater, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNlgF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotLessOrGreater, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNltF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLess, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNltF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLess, updateExec = true),
    VectorCompareInstructionDefs.VCmpxNltF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotLess, updateExec = true),
    VectorCompareInstructionDefs.VCmpxOF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Ordered, updateExec = true),
    VectorCompareInstructionDefs.VCmpxOF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Ordered, updateExec = true),
    VectorCompareInstructionDefs.VCmpxOF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Ordered, updateExec = true),
    VectorCompareInstructionDefs.VCmpxUF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Unordered, updateExec = true),
    VectorCompareInstructionDefs.VCmpxUF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Unordered, updateExec = true),
    VectorCompareInstructionDefs.VCmpxUF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Unordered, updateExec = true)
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

  private lazy val byName = definitions.groupBy(_.mnemonic.raw)
  def forOpcode(name: String): Vec[Opcode] = byName.getOrElse(name.toLowerCaseRoot, Vec.empty)
}
