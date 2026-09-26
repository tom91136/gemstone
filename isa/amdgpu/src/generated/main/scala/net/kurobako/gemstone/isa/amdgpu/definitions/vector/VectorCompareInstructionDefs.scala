// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec
import Instruction.*
import Program.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private[amdgpu] object VectorCompareInstructionDefs {
  case object VCmpClassF16 extends Definition(
        Opcode("v_cmp_class_f16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpClassF16(operands(0), operands(1), operands(2), mods))),
        1412
      )
  case object VCmpClassF32 extends Definition(
        Opcode("v_cmp_class_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpClassF32(operands(0), operands(1), operands(2), mods))),
        1413
      )
  case object VCmpClassF64 extends Definition(
        Opcode("v_cmp_class_f64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpClassF64(operands(0), operands(1), operands(2), mods))),
        1414
      )
  case object VCmpEqF16 extends Definition(
        Opcode("v_cmp_eq_f16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpEqF16(operands(0), operands(1), operands(2), mods))),
        1415
      )
  case object VCmpEqF32 extends Definition(
        Opcode("v_cmp_eq_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpEqF32(operands(0), operands(1), operands(2), mods))),
        1416
      )
  case object VCmpEqF64 extends Definition(
        Opcode("v_cmp_eq_f64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpEqF64(operands(0), operands(1), operands(2), mods))),
        1417
      )
  case object VCmpEqI16 extends Definition(
        Opcode("v_cmp_eq_i16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpEqI16(operands(0), operands(1), operands(2), mods))),
        1418
      )
  case object VCmpEqI32 extends Definition(
        Opcode("v_cmp_eq_i32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpEqI32(operands(0), operands(1), operands(2), mods))),
        1419
      )
  case object VCmpEqI64 extends Definition(
        Opcode("v_cmp_eq_i64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpEqI64(operands(0), operands(1), operands(2), mods))),
        1420
      )
  case object VCmpEqU16 extends Definition(
        Opcode("v_cmp_eq_u16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpEqU16(operands(0), operands(1), operands(2), mods))),
        1421
      )
  case object VCmpEqU32 extends Definition(
        Opcode("v_cmp_eq_u32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpEqU32(operands(0), operands(1), operands(2), mods))),
        1422
      )
  case object VCmpEqU64 extends Definition(
        Opcode("v_cmp_eq_u64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpEqU64(operands(0), operands(1), operands(2), mods))),
        1423
      )
  case object VCmpFF16 extends Definition(
        Opcode("v_cmp_f_f16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpFF16(operands(0), operands(1), operands(2), mods))),
        1424
      )
  case object VCmpFF32 extends Definition(
        Opcode("v_cmp_f_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpFF32(operands(0), operands(1), operands(2), mods))),
        1425
      )
  case object VCmpFF64 extends Definition(
        Opcode("v_cmp_f_f64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpFF64(operands(0), operands(1), operands(2), mods))),
        1426
      )
  case object VCmpFI16 extends Definition(
        Opcode("v_cmp_f_i16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpFI16(operands(0), operands(1), operands(2), mods))),
        1427
      )
  case object VCmpFI32 extends Definition(
        Opcode("v_cmp_f_i32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpFI32(operands(0), operands(1), operands(2), mods))),
        1428
      )
  case object VCmpFI64 extends Definition(
        Opcode("v_cmp_f_i64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpFI64(operands(0), operands(1), operands(2), mods))),
        1429
      )
  case object VCmpFU16 extends Definition(
        Opcode("v_cmp_f_u16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpFU16(operands(0), operands(1), operands(2), mods))),
        1430
      )
  case object VCmpFU32 extends Definition(
        Opcode("v_cmp_f_u32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpFU32(operands(0), operands(1), operands(2), mods))),
        1431
      )
  case object VCmpFU64 extends Definition(
        Opcode("v_cmp_f_u64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpFU64(operands(0), operands(1), operands(2), mods))),
        1432
      )
  case object VCmpGeF16 extends Definition(
        Opcode("v_cmp_ge_f16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpGeF16(operands(0), operands(1), operands(2), mods))),
        1433
      )
  case object VCmpGeF32 extends Definition(
        Opcode("v_cmp_ge_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpGeF32(operands(0), operands(1), operands(2), mods))),
        1434
      )
  case object VCmpGeF64 extends Definition(
        Opcode("v_cmp_ge_f64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpGeF64(operands(0), operands(1), operands(2), mods))),
        1435
      )
  case object VCmpGeI16 extends Definition(
        Opcode("v_cmp_ge_i16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpGeI16(operands(0), operands(1), operands(2), mods))),
        1436
      )
  case object VCmpGeI32 extends Definition(
        Opcode("v_cmp_ge_i32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpGeI32(operands(0), operands(1), operands(2), mods))),
        1437
      )
  case object VCmpGeI64 extends Definition(
        Opcode("v_cmp_ge_i64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpGeI64(operands(0), operands(1), operands(2), mods))),
        1438
      )
  case object VCmpGeU16 extends Definition(
        Opcode("v_cmp_ge_u16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpGeU16(operands(0), operands(1), operands(2), mods))),
        1439
      )
  case object VCmpGeU32 extends Definition(
        Opcode("v_cmp_ge_u32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpGeU32(operands(0), operands(1), operands(2), mods))),
        1440
      )
  case object VCmpGeU64 extends Definition(
        Opcode("v_cmp_ge_u64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpGeU64(operands(0), operands(1), operands(2), mods))),
        1441
      )
  case object VCmpGtF16 extends Definition(
        Opcode("v_cmp_gt_f16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpGtF16(operands(0), operands(1), operands(2), mods))),
        1442
      )
  case object VCmpGtF32 extends Definition(
        Opcode("v_cmp_gt_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpGtF32(operands(0), operands(1), operands(2), mods))),
        1443
      )
  case object VCmpGtF64 extends Definition(
        Opcode("v_cmp_gt_f64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpGtF64(operands(0), operands(1), operands(2), mods))),
        1444
      )
  case object VCmpGtI16 extends Definition(
        Opcode("v_cmp_gt_i16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpGtI16(operands(0), operands(1), operands(2), mods))),
        1445
      )
  case object VCmpGtI32 extends Definition(
        Opcode("v_cmp_gt_i32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpGtI32(operands(0), operands(1), operands(2), mods))),
        1446
      )
  case object VCmpGtI64 extends Definition(
        Opcode("v_cmp_gt_i64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpGtI64(operands(0), operands(1), operands(2), mods))),
        1447
      )
  case object VCmpGtU16 extends Definition(
        Opcode("v_cmp_gt_u16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpGtU16(operands(0), operands(1), operands(2), mods))),
        1448
      )
  case object VCmpGtU32 extends Definition(
        Opcode("v_cmp_gt_u32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpGtU32(operands(0), operands(1), operands(2), mods))),
        1449
      )
  case object VCmpGtU64 extends Definition(
        Opcode("v_cmp_gt_u64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpGtU64(operands(0), operands(1), operands(2), mods))),
        1450
      )
  case object VCmpLeF16 extends Definition(
        Opcode("v_cmp_le_f16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLeF16(operands(0), operands(1), operands(2), mods))),
        1451
      )
  case object VCmpLeF32 extends Definition(
        Opcode("v_cmp_le_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLeF32(operands(0), operands(1), operands(2), mods))),
        1452
      )
  case object VCmpLeF64 extends Definition(
        Opcode("v_cmp_le_f64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLeF64(operands(0), operands(1), operands(2), mods))),
        1453
      )
  case object VCmpLeI16 extends Definition(
        Opcode("v_cmp_le_i16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLeI16(operands(0), operands(1), operands(2), mods))),
        1454
      )
  case object VCmpLeI32 extends Definition(
        Opcode("v_cmp_le_i32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLeI32(operands(0), operands(1), operands(2), mods))),
        1455
      )
  case object VCmpLeI64 extends Definition(
        Opcode("v_cmp_le_i64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLeI64(operands(0), operands(1), operands(2), mods))),
        1456
      )
  case object VCmpLeU16 extends Definition(
        Opcode("v_cmp_le_u16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLeU16(operands(0), operands(1), operands(2), mods))),
        1457
      )
  case object VCmpLeU32 extends Definition(
        Opcode("v_cmp_le_u32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLeU32(operands(0), operands(1), operands(2), mods))),
        1458
      )
  case object VCmpLeU64 extends Definition(
        Opcode("v_cmp_le_u64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLeU64(operands(0), operands(1), operands(2), mods))),
        1459
      )
  case object VCmpLgF16 extends Definition(
        Opcode("v_cmp_lg_f16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLgF16(operands(0), operands(1), operands(2), mods))),
        1460
      )
  case object VCmpLgF32 extends Definition(
        Opcode("v_cmp_lg_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLgF32(operands(0), operands(1), operands(2), mods))),
        1461
      )
  case object VCmpLgF64 extends Definition(
        Opcode("v_cmp_lg_f64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLgF64(operands(0), operands(1), operands(2), mods))),
        1462
      )
  case object VCmpLgI16 extends Definition(
        Opcode("v_cmp_lg_i16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLgI16(operands(0), operands(1), operands(2), mods))),
        1463
      )
  case object VCmpLgI32 extends Definition(
        Opcode("v_cmp_lg_i32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLgI32(operands(0), operands(1), operands(2), mods))),
        1464
      )
  case object VCmpLgI64 extends Definition(
        Opcode("v_cmp_lg_i64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLgI64(operands(0), operands(1), operands(2), mods))),
        1465
      )
  case object VCmpLgU16 extends Definition(
        Opcode("v_cmp_lg_u16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLgU16(operands(0), operands(1), operands(2), mods))),
        1466
      )
  case object VCmpLgU32 extends Definition(
        Opcode("v_cmp_lg_u32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLgU32(operands(0), operands(1), operands(2), mods))),
        1467
      )
  case object VCmpLgU64 extends Definition(
        Opcode("v_cmp_lg_u64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLgU64(operands(0), operands(1), operands(2), mods))),
        1468
      )
  case object VCmpLtF16 extends Definition(
        Opcode("v_cmp_lt_f16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLtF16(operands(0), operands(1), operands(2), mods))),
        1469
      )
  case object VCmpLtF32 extends Definition(
        Opcode("v_cmp_lt_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLtF32(operands(0), operands(1), operands(2), mods))),
        1470
      )
  case object VCmpLtF64 extends Definition(
        Opcode("v_cmp_lt_f64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLtF64(operands(0), operands(1), operands(2), mods))),
        1471
      )
  case object VCmpLtI16 extends Definition(
        Opcode("v_cmp_lt_i16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLtI16(operands(0), operands(1), operands(2), mods))),
        1472
      )
  case object VCmpLtI32 extends Definition(
        Opcode("v_cmp_lt_i32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLtI32(operands(0), operands(1), operands(2), mods))),
        1473
      )
  case object VCmpLtI64 extends Definition(
        Opcode("v_cmp_lt_i64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLtI64(operands(0), operands(1), operands(2), mods))),
        1474
      )
  case object VCmpLtU16 extends Definition(
        Opcode("v_cmp_lt_u16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLtU16(operands(0), operands(1), operands(2), mods))),
        1475
      )
  case object VCmpLtU32 extends Definition(
        Opcode("v_cmp_lt_u32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLtU32(operands(0), operands(1), operands(2), mods))),
        1476
      )
  case object VCmpLtU64 extends Definition(
        Opcode("v_cmp_lt_u64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpLtU64(operands(0), operands(1), operands(2), mods))),
        1477
      )
  case object VCmpNeI16 extends Definition(
        Opcode("v_cmp_ne_i16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNeI16(operands(0), operands(1), operands(2), mods))),
        1478
      )
  case object VCmpNeI32 extends Definition(
        Opcode("v_cmp_ne_i32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNeI32(operands(0), operands(1), operands(2), mods))),
        1479
      )
  case object VCmpNeI64 extends Definition(
        Opcode("v_cmp_ne_i64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNeI64(operands(0), operands(1), operands(2), mods))),
        1480
      )
  case object VCmpNeU16 extends Definition(
        Opcode("v_cmp_ne_u16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNeU16(operands(0), operands(1), operands(2), mods))),
        1481
      )
  case object VCmpNeU32 extends Definition(
        Opcode("v_cmp_ne_u32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNeU32(operands(0), operands(1), operands(2), mods))),
        1482
      )
  case object VCmpNeU64 extends Definition(
        Opcode("v_cmp_ne_u64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNeU64(operands(0), operands(1), operands(2), mods))),
        1483
      )
  case object VCmpNeqF16 extends Definition(
        Opcode("v_cmp_neq_f16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNeqF16(operands(0), operands(1), operands(2), mods))),
        1484
      )
  case object VCmpNeqF32 extends Definition(
        Opcode("v_cmp_neq_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNeqF32(operands(0), operands(1), operands(2), mods))),
        1485
      )
  case object VCmpNeqF64 extends Definition(
        Opcode("v_cmp_neq_f64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNeqF64(operands(0), operands(1), operands(2), mods))),
        1486
      )
  case object VCmpNgeF16 extends Definition(
        Opcode("v_cmp_nge_f16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNgeF16(operands(0), operands(1), operands(2), mods))),
        1487
      )
  case object VCmpNgeF32 extends Definition(
        Opcode("v_cmp_nge_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNgeF32(operands(0), operands(1), operands(2), mods))),
        1488
      )
  case object VCmpNgeF64 extends Definition(
        Opcode("v_cmp_nge_f64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNgeF64(operands(0), operands(1), operands(2), mods))),
        1489
      )
  case object VCmpNgtF16 extends Definition(
        Opcode("v_cmp_ngt_f16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNgtF16(operands(0), operands(1), operands(2), mods))),
        1490
      )
  case object VCmpNgtF32 extends Definition(
        Opcode("v_cmp_ngt_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNgtF32(operands(0), operands(1), operands(2), mods))),
        1491
      )
  case object VCmpNgtF64 extends Definition(
        Opcode("v_cmp_ngt_f64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNgtF64(operands(0), operands(1), operands(2), mods))),
        1492
      )
  case object VCmpNleF16 extends Definition(
        Opcode("v_cmp_nle_f16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNleF16(operands(0), operands(1), operands(2), mods))),
        1493
      )
  case object VCmpNleF32 extends Definition(
        Opcode("v_cmp_nle_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNleF32(operands(0), operands(1), operands(2), mods))),
        1494
      )
  case object VCmpNleF64 extends Definition(
        Opcode("v_cmp_nle_f64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNleF64(operands(0), operands(1), operands(2), mods))),
        1495
      )
  case object VCmpNlgF16 extends Definition(
        Opcode("v_cmp_nlg_f16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNlgF16(operands(0), operands(1), operands(2), mods))),
        1496
      )
  case object VCmpNlgF32 extends Definition(
        Opcode("v_cmp_nlg_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNlgF32(operands(0), operands(1), operands(2), mods))),
        1497
      )
  case object VCmpNlgF64 extends Definition(
        Opcode("v_cmp_nlg_f64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNlgF64(operands(0), operands(1), operands(2), mods))),
        1498
      )
  case object VCmpNltF16 extends Definition(
        Opcode("v_cmp_nlt_f16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNltF16(operands(0), operands(1), operands(2), mods))),
        1499
      )
  case object VCmpNltF32 extends Definition(
        Opcode("v_cmp_nlt_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNltF32(operands(0), operands(1), operands(2), mods))),
        1500
      )
  case object VCmpNltF64 extends Definition(
        Opcode("v_cmp_nlt_f64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpNltF64(operands(0), operands(1), operands(2), mods))),
        1501
      )
  case object VCmpOF16 extends Definition(
        Opcode("v_cmp_o_f16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpOF16(operands(0), operands(1), operands(2), mods))),
        1502
      )
  case object VCmpOF32 extends Definition(
        Opcode("v_cmp_o_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpOF32(operands(0), operands(1), operands(2), mods))),
        1503
      )
  case object VCmpOF64 extends Definition(
        Opcode("v_cmp_o_f64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpOF64(operands(0), operands(1), operands(2), mods))),
        1504
      )
  case object VCmpTF16 extends Definition(
        Opcode("v_cmp_t_f16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpTF16(operands(0), operands(1), operands(2), mods))),
        1505
      )
  case object VCmpTF32 extends Definition(
        Opcode("v_cmp_t_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpTF32(operands(0), operands(1), operands(2), mods))),
        1506
      )
  case object VCmpTF64 extends Definition(
        Opcode("v_cmp_t_f64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpTF64(operands(0), operands(1), operands(2), mods))),
        1507
      )
  case object VCmpTI16 extends Definition(
        Opcode("v_cmp_t_i16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpTI16(operands(0), operands(1), operands(2), mods))),
        1508
      )
  case object VCmpTI32 extends Definition(
        Opcode("v_cmp_t_i32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpTI32(operands(0), operands(1), operands(2), mods))),
        1509
      )
  case object VCmpTI64 extends Definition(
        Opcode("v_cmp_t_i64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpTI64(operands(0), operands(1), operands(2), mods))),
        1510
      )
  case object VCmpTU16 extends Definition(
        Opcode("v_cmp_t_u16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpTU16(operands(0), operands(1), operands(2), mods))),
        1511
      )
  case object VCmpTU32 extends Definition(
        Opcode("v_cmp_t_u32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpTU32(operands(0), operands(1), operands(2), mods))),
        1512
      )
  case object VCmpTU64 extends Definition(
        Opcode("v_cmp_t_u64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpTU64(operands(0), operands(1), operands(2), mods))),
        1513
      )
  case object VCmpTruF16 extends Definition(
        Opcode("v_cmp_tru_f16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpTruF16(operands(0), operands(1), operands(2), mods))),
        1514
      )
  case object VCmpTruF32 extends Definition(
        Opcode("v_cmp_tru_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpTruF32(operands(0), operands(1), operands(2), mods))),
        1515
      )
  case object VCmpTruF64 extends Definition(
        Opcode("v_cmp_tru_f64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpTruF64(operands(0), operands(1), operands(2), mods))),
        1516
      )
  case object VCmpTruI16 extends Definition(
        Opcode("v_cmp_tru_i16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpTruI16(operands(0), operands(1), operands(2), mods))),
        1517
      )
  case object VCmpTruI32 extends Definition(
        Opcode("v_cmp_tru_i32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpTruI32(operands(0), operands(1), operands(2), mods))),
        1518
      )
  case object VCmpTruI64 extends Definition(
        Opcode("v_cmp_tru_i64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpTruI64(operands(0), operands(1), operands(2), mods))),
        1519
      )
  case object VCmpTruU16 extends Definition(
        Opcode("v_cmp_tru_u16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpTruU16(operands(0), operands(1), operands(2), mods))),
        1520
      )
  case object VCmpTruU32 extends Definition(
        Opcode("v_cmp_tru_u32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpTruU32(operands(0), operands(1), operands(2), mods))),
        1521
      )
  case object VCmpTruU64 extends Definition(
        Opcode("v_cmp_tru_u64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpTruU64(operands(0), operands(1), operands(2), mods))),
        1522
      )
  case object VCmpUF16 extends Definition(
        Opcode("v_cmp_u_f16"),
        decoder(form(3)((operands, mods) => Instruction.VCmpUF16(operands(0), operands(1), operands(2), mods))),
        1523
      )
  case object VCmpUF32 extends Definition(
        Opcode("v_cmp_u_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCmpUF32(operands(0), operands(1), operands(2), mods))),
        1524
      )
  case object VCmpUF64 extends Definition(
        Opcode("v_cmp_u_f64"),
        decoder(form(3)((operands, mods) => Instruction.VCmpUF64(operands(0), operands(1), operands(2), mods))),
        1525
      )
  case object VCmpxClassF16 extends Definition(
        Opcode("v_cmpx_class_f16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxClassF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxClassF16_3(operands(0), operands(1), operands(2), mods))
        ),
        1526
      )
  case object VCmpxClassF32 extends Definition(
        Opcode("v_cmpx_class_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxClassF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxClassF32_3(operands(0), operands(1), operands(2), mods))
        ),
        1527
      )
  case object VCmpxClassF64 extends Definition(
        Opcode("v_cmpx_class_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxClassF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxClassF64_3(operands(0), operands(1), operands(2), mods))
        ),
        1528
      )
  case object VCmpxEqF16 extends Definition(
        Opcode("v_cmpx_eq_f16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxEqF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxEqF16_3(operands(0), operands(1), operands(2), mods))
        ),
        1529
      )
  case object VCmpxEqF32 extends Definition(
        Opcode("v_cmpx_eq_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxEqF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxEqF32_3(operands(0), operands(1), operands(2), mods))
        ),
        1530
      )
  case object VCmpxEqF64 extends Definition(
        Opcode("v_cmpx_eq_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxEqF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxEqF64_3(operands(0), operands(1), operands(2), mods))
        ),
        1531
      )
  case object VCmpxEqI16 extends Definition(
        Opcode("v_cmpx_eq_i16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxEqI16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxEqI16_3(operands(0), operands(1), operands(2), mods))
        ),
        1532
      )
  case object VCmpxEqI32 extends Definition(
        Opcode("v_cmpx_eq_i32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxEqI32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxEqI32_3(operands(0), operands(1), operands(2), mods))
        ),
        1533
      )
  case object VCmpxEqI64 extends Definition(
        Opcode("v_cmpx_eq_i64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxEqI64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxEqI64_3(operands(0), operands(1), operands(2), mods))
        ),
        1534
      )
  case object VCmpxEqU16 extends Definition(
        Opcode("v_cmpx_eq_u16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxEqU16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxEqU16_3(operands(0), operands(1), operands(2), mods))
        ),
        1535
      )
  case object VCmpxEqU32 extends Definition(
        Opcode("v_cmpx_eq_u32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxEqU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxEqU32_3(operands(0), operands(1), operands(2), mods))
        ),
        1536
      )
  case object VCmpxEqU64 extends Definition(
        Opcode("v_cmpx_eq_u64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxEqU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxEqU64_3(operands(0), operands(1), operands(2), mods))
        ),
        1537
      )
  case object VCmpxFF16 extends Definition(
        Opcode("v_cmpx_f_f16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxFF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxFF16_3(operands(0), operands(1), operands(2), mods))
        ),
        1538
      )
  case object VCmpxFF32 extends Definition(
        Opcode("v_cmpx_f_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxFF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxFF32_3(operands(0), operands(1), operands(2), mods))
        ),
        1539
      )
  case object VCmpxFF64 extends Definition(
        Opcode("v_cmpx_f_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxFF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxFF64_3(operands(0), operands(1), operands(2), mods))
        ),
        1540
      )
  case object VCmpxFI16 extends Definition(
        Opcode("v_cmpx_f_i16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxFI16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxFI16_3(operands(0), operands(1), operands(2), mods))
        ),
        1541
      )
  case object VCmpxFI32 extends Definition(
        Opcode("v_cmpx_f_i32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxFI32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxFI32_3(operands(0), operands(1), operands(2), mods))
        ),
        1542
      )
  case object VCmpxFI64 extends Definition(
        Opcode("v_cmpx_f_i64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxFI64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxFI64_3(operands(0), operands(1), operands(2), mods))
        ),
        1543
      )
  case object VCmpxFU16 extends Definition(
        Opcode("v_cmpx_f_u16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxFU16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxFU16_3(operands(0), operands(1), operands(2), mods))
        ),
        1544
      )
  case object VCmpxFU32 extends Definition(
        Opcode("v_cmpx_f_u32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxFU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxFU32_3(operands(0), operands(1), operands(2), mods))
        ),
        1545
      )
  case object VCmpxFU64 extends Definition(
        Opcode("v_cmpx_f_u64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxFU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxFU64_3(operands(0), operands(1), operands(2), mods))
        ),
        1546
      )
  case object VCmpxGeF16 extends Definition(
        Opcode("v_cmpx_ge_f16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGeF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGeF16_3(operands(0), operands(1), operands(2), mods))
        ),
        1547
      )
  case object VCmpxGeF32 extends Definition(
        Opcode("v_cmpx_ge_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGeF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGeF32_3(operands(0), operands(1), operands(2), mods))
        ),
        1548
      )
  case object VCmpxGeF64 extends Definition(
        Opcode("v_cmpx_ge_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGeF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGeF64_3(operands(0), operands(1), operands(2), mods))
        ),
        1549
      )
  case object VCmpxGeI16 extends Definition(
        Opcode("v_cmpx_ge_i16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGeI16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGeI16_3(operands(0), operands(1), operands(2), mods))
        ),
        1550
      )
  case object VCmpxGeI32 extends Definition(
        Opcode("v_cmpx_ge_i32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGeI32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGeI32_3(operands(0), operands(1), operands(2), mods))
        ),
        1551
      )
  case object VCmpxGeI64 extends Definition(
        Opcode("v_cmpx_ge_i64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGeI64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGeI64_3(operands(0), operands(1), operands(2), mods))
        ),
        1552
      )
  case object VCmpxGeU16 extends Definition(
        Opcode("v_cmpx_ge_u16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGeU16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGeU16_3(operands(0), operands(1), operands(2), mods))
        ),
        1553
      )
  case object VCmpxGeU32 extends Definition(
        Opcode("v_cmpx_ge_u32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGeU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGeU32_3(operands(0), operands(1), operands(2), mods))
        ),
        1554
      )
  case object VCmpxGeU64 extends Definition(
        Opcode("v_cmpx_ge_u64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGeU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGeU64_3(operands(0), operands(1), operands(2), mods))
        ),
        1555
      )
  case object VCmpxGtF16 extends Definition(
        Opcode("v_cmpx_gt_f16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGtF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGtF16_3(operands(0), operands(1), operands(2), mods))
        ),
        1556
      )
  case object VCmpxGtF32 extends Definition(
        Opcode("v_cmpx_gt_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGtF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGtF32_3(operands(0), operands(1), operands(2), mods))
        ),
        1557
      )
  case object VCmpxGtF64 extends Definition(
        Opcode("v_cmpx_gt_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGtF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGtF64_3(operands(0), operands(1), operands(2), mods))
        ),
        1558
      )
  case object VCmpxGtI16 extends Definition(
        Opcode("v_cmpx_gt_i16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGtI16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGtI16_3(operands(0), operands(1), operands(2), mods))
        ),
        1559
      )
  case object VCmpxGtI32 extends Definition(
        Opcode("v_cmpx_gt_i32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGtI32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGtI32_3(operands(0), operands(1), operands(2), mods))
        ),
        1560
      )
  case object VCmpxGtI64 extends Definition(
        Opcode("v_cmpx_gt_i64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGtI64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGtI64_3(operands(0), operands(1), operands(2), mods))
        ),
        1561
      )
  case object VCmpxGtU16 extends Definition(
        Opcode("v_cmpx_gt_u16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGtU16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGtU16_3(operands(0), operands(1), operands(2), mods))
        ),
        1562
      )
  case object VCmpxGtU32 extends Definition(
        Opcode("v_cmpx_gt_u32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGtU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGtU32_3(operands(0), operands(1), operands(2), mods))
        ),
        1563
      )
  case object VCmpxGtU64 extends Definition(
        Opcode("v_cmpx_gt_u64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxGtU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxGtU64_3(operands(0), operands(1), operands(2), mods))
        ),
        1564
      )
  case object VCmpxLeF16 extends Definition(
        Opcode("v_cmpx_le_f16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLeF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLeF16_3(operands(0), operands(1), operands(2), mods))
        ),
        1565
      )
  case object VCmpxLeF32 extends Definition(
        Opcode("v_cmpx_le_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLeF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLeF32_3(operands(0), operands(1), operands(2), mods))
        ),
        1566
      )
  case object VCmpxLeF64 extends Definition(
        Opcode("v_cmpx_le_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLeF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLeF64_3(operands(0), operands(1), operands(2), mods))
        ),
        1567
      )
  case object VCmpxLeI16 extends Definition(
        Opcode("v_cmpx_le_i16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLeI16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLeI16_3(operands(0), operands(1), operands(2), mods))
        ),
        1568
      )
  case object VCmpxLeI32 extends Definition(
        Opcode("v_cmpx_le_i32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLeI32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLeI32_3(operands(0), operands(1), operands(2), mods))
        ),
        1569
      )
  case object VCmpxLeI64 extends Definition(
        Opcode("v_cmpx_le_i64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLeI64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLeI64_3(operands(0), operands(1), operands(2), mods))
        ),
        1570
      )
  case object VCmpxLeU16 extends Definition(
        Opcode("v_cmpx_le_u16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLeU16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLeU16_3(operands(0), operands(1), operands(2), mods))
        ),
        1571
      )
  case object VCmpxLeU32 extends Definition(
        Opcode("v_cmpx_le_u32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLeU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLeU32_3(operands(0), operands(1), operands(2), mods))
        ),
        1572
      )
  case object VCmpxLeU64 extends Definition(
        Opcode("v_cmpx_le_u64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLeU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLeU64_3(operands(0), operands(1), operands(2), mods))
        ),
        1573
      )
  case object VCmpxLgF16 extends Definition(
        Opcode("v_cmpx_lg_f16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLgF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLgF16_3(operands(0), operands(1), operands(2), mods))
        ),
        1574
      )
  case object VCmpxLgF32 extends Definition(
        Opcode("v_cmpx_lg_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLgF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLgF32_3(operands(0), operands(1), operands(2), mods))
        ),
        1575
      )
  case object VCmpxLgF64 extends Definition(
        Opcode("v_cmpx_lg_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLgF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLgF64_3(operands(0), operands(1), operands(2), mods))
        ),
        1576
      )
  case object VCmpxLgI16 extends Definition(
        Opcode("v_cmpx_lg_i16"),
        decoder(form(2)((operands, mods) => Instruction.VCmpxLgI16(operands(0), operands(1), mods))),
        1577
      )
  case object VCmpxLgI32 extends Definition(
        Opcode("v_cmpx_lg_i32"),
        decoder(form(2)((operands, mods) => Instruction.VCmpxLgI32(operands(0), operands(1), mods))),
        1578
      )
  case object VCmpxLgI64 extends Definition(
        Opcode("v_cmpx_lg_i64"),
        decoder(form(2)((operands, mods) => Instruction.VCmpxLgI64(operands(0), operands(1), mods))),
        1579
      )
  case object VCmpxLgU16 extends Definition(
        Opcode("v_cmpx_lg_u16"),
        decoder(form(2)((operands, mods) => Instruction.VCmpxLgU16(operands(0), operands(1), mods))),
        1580
      )
  case object VCmpxLgU32 extends Definition(
        Opcode("v_cmpx_lg_u32"),
        decoder(form(2)((operands, mods) => Instruction.VCmpxLgU32(operands(0), operands(1), mods))),
        1581
      )
  case object VCmpxLgU64 extends Definition(
        Opcode("v_cmpx_lg_u64"),
        decoder(form(2)((operands, mods) => Instruction.VCmpxLgU64(operands(0), operands(1), mods))),
        1582
      )
  case object VCmpxLtF16 extends Definition(
        Opcode("v_cmpx_lt_f16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLtF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLtF16_3(operands(0), operands(1), operands(2), mods))
        ),
        1583
      )
  case object VCmpxLtF32 extends Definition(
        Opcode("v_cmpx_lt_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLtF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLtF32_3(operands(0), operands(1), operands(2), mods))
        ),
        1584
      )
  case object VCmpxLtF64 extends Definition(
        Opcode("v_cmpx_lt_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLtF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLtF64_3(operands(0), operands(1), operands(2), mods))
        ),
        1585
      )
  case object VCmpxLtI16 extends Definition(
        Opcode("v_cmpx_lt_i16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLtI16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLtI16_3(operands(0), operands(1), operands(2), mods))
        ),
        1586
      )
  case object VCmpxLtI32 extends Definition(
        Opcode("v_cmpx_lt_i32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLtI32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLtI32_3(operands(0), operands(1), operands(2), mods))
        ),
        1587
      )
  case object VCmpxLtI64 extends Definition(
        Opcode("v_cmpx_lt_i64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLtI64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLtI64_3(operands(0), operands(1), operands(2), mods))
        ),
        1588
      )
  case object VCmpxLtU16 extends Definition(
        Opcode("v_cmpx_lt_u16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLtU16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLtU16_3(operands(0), operands(1), operands(2), mods))
        ),
        1589
      )
  case object VCmpxLtU32 extends Definition(
        Opcode("v_cmpx_lt_u32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLtU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLtU32_3(operands(0), operands(1), operands(2), mods))
        ),
        1590
      )
  case object VCmpxLtU64 extends Definition(
        Opcode("v_cmpx_lt_u64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxLtU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxLtU64_3(operands(0), operands(1), operands(2), mods))
        ),
        1591
      )
  case object VCmpxNeI16 extends Definition(
        Opcode("v_cmpx_ne_i16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNeI16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNeI16_3(operands(0), operands(1), operands(2), mods))
        ),
        1592
      )
  case object VCmpxNeI32 extends Definition(
        Opcode("v_cmpx_ne_i32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNeI32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNeI32_3(operands(0), operands(1), operands(2), mods))
        ),
        1593
      )
  case object VCmpxNeI64 extends Definition(
        Opcode("v_cmpx_ne_i64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNeI64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNeI64_3(operands(0), operands(1), operands(2), mods))
        ),
        1594
      )
  case object VCmpxNeU16 extends Definition(
        Opcode("v_cmpx_ne_u16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNeU16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNeU16_3(operands(0), operands(1), operands(2), mods))
        ),
        1595
      )
  case object VCmpxNeU32 extends Definition(
        Opcode("v_cmpx_ne_u32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNeU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNeU32_3(operands(0), operands(1), operands(2), mods))
        ),
        1596
      )
  case object VCmpxNeU64 extends Definition(
        Opcode("v_cmpx_ne_u64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNeU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNeU64_3(operands(0), operands(1), operands(2), mods))
        ),
        1597
      )
  case object VCmpxNeqF16 extends Definition(
        Opcode("v_cmpx_neq_f16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNeqF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNeqF16_3(operands(0), operands(1), operands(2), mods))
        ),
        1598
      )
  case object VCmpxNeqF32 extends Definition(
        Opcode("v_cmpx_neq_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNeqF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNeqF32_3(operands(0), operands(1), operands(2), mods))
        ),
        1599
      )
  case object VCmpxNeqF64 extends Definition(
        Opcode("v_cmpx_neq_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNeqF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNeqF64_3(operands(0), operands(1), operands(2), mods))
        ),
        1600
      )
  case object VCmpxNgeF16 extends Definition(
        Opcode("v_cmpx_nge_f16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNgeF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNgeF16_3(operands(0), operands(1), operands(2), mods))
        ),
        1601
      )
  case object VCmpxNgeF32 extends Definition(
        Opcode("v_cmpx_nge_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNgeF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNgeF32_3(operands(0), operands(1), operands(2), mods))
        ),
        1602
      )
  case object VCmpxNgeF64 extends Definition(
        Opcode("v_cmpx_nge_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNgeF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNgeF64_3(operands(0), operands(1), operands(2), mods))
        ),
        1603
      )
  case object VCmpxNgtF16 extends Definition(
        Opcode("v_cmpx_ngt_f16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNgtF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNgtF16_3(operands(0), operands(1), operands(2), mods))
        ),
        1604
      )
  case object VCmpxNgtF32 extends Definition(
        Opcode("v_cmpx_ngt_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNgtF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNgtF32_3(operands(0), operands(1), operands(2), mods))
        ),
        1605
      )
  case object VCmpxNgtF64 extends Definition(
        Opcode("v_cmpx_ngt_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNgtF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNgtF64_3(operands(0), operands(1), operands(2), mods))
        ),
        1606
      )
  case object VCmpxNleF16 extends Definition(
        Opcode("v_cmpx_nle_f16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNleF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNleF16_3(operands(0), operands(1), operands(2), mods))
        ),
        1607
      )
  case object VCmpxNleF32 extends Definition(
        Opcode("v_cmpx_nle_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNleF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNleF32_3(operands(0), operands(1), operands(2), mods))
        ),
        1608
      )
  case object VCmpxNleF64 extends Definition(
        Opcode("v_cmpx_nle_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNleF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNleF64_3(operands(0), operands(1), operands(2), mods))
        ),
        1609
      )
  case object VCmpxNlgF16 extends Definition(
        Opcode("v_cmpx_nlg_f16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNlgF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNlgF16_3(operands(0), operands(1), operands(2), mods))
        ),
        1610
      )
  case object VCmpxNlgF32 extends Definition(
        Opcode("v_cmpx_nlg_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNlgF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNlgF32_3(operands(0), operands(1), operands(2), mods))
        ),
        1611
      )
  case object VCmpxNlgF64 extends Definition(
        Opcode("v_cmpx_nlg_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNlgF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNlgF64_3(operands(0), operands(1), operands(2), mods))
        ),
        1612
      )
  case object VCmpxNltF16 extends Definition(
        Opcode("v_cmpx_nlt_f16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNltF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNltF16_3(operands(0), operands(1), operands(2), mods))
        ),
        1613
      )
  case object VCmpxNltF32 extends Definition(
        Opcode("v_cmpx_nlt_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNltF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNltF32_3(operands(0), operands(1), operands(2), mods))
        ),
        1614
      )
  case object VCmpxNltF64 extends Definition(
        Opcode("v_cmpx_nlt_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxNltF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxNltF64_3(operands(0), operands(1), operands(2), mods))
        ),
        1615
      )
  case object VCmpxOF16 extends Definition(
        Opcode("v_cmpx_o_f16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxOF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxOF16_3(operands(0), operands(1), operands(2), mods))
        ),
        1616
      )
  case object VCmpxOF32 extends Definition(
        Opcode("v_cmpx_o_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxOF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxOF32_3(operands(0), operands(1), operands(2), mods))
        ),
        1617
      )
  case object VCmpxOF64 extends Definition(
        Opcode("v_cmpx_o_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxOF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxOF64_3(operands(0), operands(1), operands(2), mods))
        ),
        1618
      )
  case object VCmpxTF16 extends Definition(
        Opcode("v_cmpx_t_f16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTF16_3(operands(0), operands(1), operands(2), mods))
        ),
        1619
      )
  case object VCmpxTF32 extends Definition(
        Opcode("v_cmpx_t_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTF32_3(operands(0), operands(1), operands(2), mods))
        ),
        1620
      )
  case object VCmpxTF64 extends Definition(
        Opcode("v_cmpx_t_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTF64_3(operands(0), operands(1), operands(2), mods))
        ),
        1621
      )
  case object VCmpxTI16 extends Definition(
        Opcode("v_cmpx_t_i16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTI16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTI16_3(operands(0), operands(1), operands(2), mods))
        ),
        1622
      )
  case object VCmpxTI32 extends Definition(
        Opcode("v_cmpx_t_i32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTI32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTI32_3(operands(0), operands(1), operands(2), mods))
        ),
        1623
      )
  case object VCmpxTI64 extends Definition(
        Opcode("v_cmpx_t_i64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTI64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTI64_3(operands(0), operands(1), operands(2), mods))
        ),
        1624
      )
  case object VCmpxTU16 extends Definition(
        Opcode("v_cmpx_t_u16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTU16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTU16_3(operands(0), operands(1), operands(2), mods))
        ),
        1625
      )
  case object VCmpxTU32 extends Definition(
        Opcode("v_cmpx_t_u32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTU32_3(operands(0), operands(1), operands(2), mods))
        ),
        1626
      )
  case object VCmpxTU64 extends Definition(
        Opcode("v_cmpx_t_u64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTU64_3(operands(0), operands(1), operands(2), mods))
        ),
        1627
      )
  case object VCmpxTruF16 extends Definition(
        Opcode("v_cmpx_tru_f16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTruF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTruF16_3(operands(0), operands(1), operands(2), mods))
        ),
        1628
      )
  case object VCmpxTruF32 extends Definition(
        Opcode("v_cmpx_tru_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTruF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTruF32_3(operands(0), operands(1), operands(2), mods))
        ),
        1629
      )
  case object VCmpxTruF64 extends Definition(
        Opcode("v_cmpx_tru_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxTruF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxTruF64_3(operands(0), operands(1), operands(2), mods))
        ),
        1630
      )
  case object VCmpxTruI16 extends Definition(
        Opcode("v_cmpx_tru_i16"),
        decoder(form(2)((operands, mods) => Instruction.VCmpxTruI16(operands(0), operands(1), mods))),
        1631
      )
  case object VCmpxTruI32 extends Definition(
        Opcode("v_cmpx_tru_i32"),
        decoder(form(2)((operands, mods) => Instruction.VCmpxTruI32(operands(0), operands(1), mods))),
        1632
      )
  case object VCmpxTruI64 extends Definition(
        Opcode("v_cmpx_tru_i64"),
        decoder(form(2)((operands, mods) => Instruction.VCmpxTruI64(operands(0), operands(1), mods))),
        1633
      )
  case object VCmpxTruU16 extends Definition(
        Opcode("v_cmpx_tru_u16"),
        decoder(form(2)((operands, mods) => Instruction.VCmpxTruU16(operands(0), operands(1), mods))),
        1634
      )
  case object VCmpxTruU32 extends Definition(
        Opcode("v_cmpx_tru_u32"),
        decoder(form(2)((operands, mods) => Instruction.VCmpxTruU32(operands(0), operands(1), mods))),
        1635
      )
  case object VCmpxTruU64 extends Definition(
        Opcode("v_cmpx_tru_u64"),
        decoder(form(2)((operands, mods) => Instruction.VCmpxTruU64(operands(0), operands(1), mods))),
        1636
      )
  case object VCmpxUF16 extends Definition(
        Opcode("v_cmpx_u_f16"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxUF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxUF16_3(operands(0), operands(1), operands(2), mods))
        ),
        1637
      )
  case object VCmpxUF32 extends Definition(
        Opcode("v_cmpx_u_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxUF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxUF32_3(operands(0), operands(1), operands(2), mods))
        ),
        1638
      )
  case object VCmpxUF64 extends Definition(
        Opcode("v_cmpx_u_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.VCmpxUF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VCmpxUF64_3(operands(0), operands(1), operands(2), mods))
        ),
        1639
      )

  val definitions: Vec[Definition] = Vec(
    VCmpClassF16,
    VCmpClassF32,
    VCmpClassF64,
    VCmpEqF16,
    VCmpEqF32,
    VCmpEqF64,
    VCmpEqI16,
    VCmpEqI32,
    VCmpEqI64,
    VCmpEqU16,
    VCmpEqU32,
    VCmpEqU64,
    VCmpFF16,
    VCmpFF32,
    VCmpFF64,
    VCmpFI16,
    VCmpFI32,
    VCmpFI64,
    VCmpFU16,
    VCmpFU32,
    VCmpFU64,
    VCmpGeF16,
    VCmpGeF32,
    VCmpGeF64,
    VCmpGeI16,
    VCmpGeI32,
    VCmpGeI64,
    VCmpGeU16,
    VCmpGeU32,
    VCmpGeU64,
    VCmpGtF16,
    VCmpGtF32,
    VCmpGtF64,
    VCmpGtI16,
    VCmpGtI32,
    VCmpGtI64,
    VCmpGtU16,
    VCmpGtU32,
    VCmpGtU64,
    VCmpLeF16,
    VCmpLeF32,
    VCmpLeF64,
    VCmpLeI16,
    VCmpLeI32,
    VCmpLeI64,
    VCmpLeU16,
    VCmpLeU32,
    VCmpLeU64,
    VCmpLgF16,
    VCmpLgF32,
    VCmpLgF64,
    VCmpLgI16,
    VCmpLgI32,
    VCmpLgI64,
    VCmpLgU16,
    VCmpLgU32,
    VCmpLgU64,
    VCmpLtF16,
    VCmpLtF32,
    VCmpLtF64,
    VCmpLtI16,
    VCmpLtI32,
    VCmpLtI64,
    VCmpLtU16,
    VCmpLtU32,
    VCmpLtU64,
    VCmpNeI16,
    VCmpNeI32,
    VCmpNeI64,
    VCmpNeU16,
    VCmpNeU32,
    VCmpNeU64,
    VCmpNeqF16,
    VCmpNeqF32,
    VCmpNeqF64,
    VCmpNgeF16,
    VCmpNgeF32,
    VCmpNgeF64,
    VCmpNgtF16,
    VCmpNgtF32,
    VCmpNgtF64,
    VCmpNleF16,
    VCmpNleF32,
    VCmpNleF64,
    VCmpNlgF16,
    VCmpNlgF32,
    VCmpNlgF64,
    VCmpNltF16,
    VCmpNltF32,
    VCmpNltF64,
    VCmpOF16,
    VCmpOF32,
    VCmpOF64,
    VCmpTF16,
    VCmpTF32,
    VCmpTF64,
    VCmpTI16,
    VCmpTI32,
    VCmpTI64,
    VCmpTU16,
    VCmpTU32,
    VCmpTU64,
    VCmpTruF16,
    VCmpTruF32,
    VCmpTruF64,
    VCmpTruI16,
    VCmpTruI32,
    VCmpTruI64,
    VCmpTruU16,
    VCmpTruU32,
    VCmpTruU64,
    VCmpUF16,
    VCmpUF32,
    VCmpUF64,
    VCmpxClassF16,
    VCmpxClassF32,
    VCmpxClassF64,
    VCmpxEqF16,
    VCmpxEqF32,
    VCmpxEqF64,
    VCmpxEqI16,
    VCmpxEqI32,
    VCmpxEqI64,
    VCmpxEqU16,
    VCmpxEqU32,
    VCmpxEqU64,
    VCmpxFF16,
    VCmpxFF32,
    VCmpxFF64,
    VCmpxFI16,
    VCmpxFI32,
    VCmpxFI64,
    VCmpxFU16,
    VCmpxFU32,
    VCmpxFU64,
    VCmpxGeF16,
    VCmpxGeF32,
    VCmpxGeF64,
    VCmpxGeI16,
    VCmpxGeI32,
    VCmpxGeI64,
    VCmpxGeU16,
    VCmpxGeU32,
    VCmpxGeU64,
    VCmpxGtF16,
    VCmpxGtF32,
    VCmpxGtF64,
    VCmpxGtI16,
    VCmpxGtI32,
    VCmpxGtI64,
    VCmpxGtU16,
    VCmpxGtU32,
    VCmpxGtU64,
    VCmpxLeF16,
    VCmpxLeF32,
    VCmpxLeF64,
    VCmpxLeI16,
    VCmpxLeI32,
    VCmpxLeI64,
    VCmpxLeU16,
    VCmpxLeU32,
    VCmpxLeU64,
    VCmpxLgF16,
    VCmpxLgF32,
    VCmpxLgF64,
    VCmpxLgI16,
    VCmpxLgI32,
    VCmpxLgI64,
    VCmpxLgU16,
    VCmpxLgU32,
    VCmpxLgU64,
    VCmpxLtF16,
    VCmpxLtF32,
    VCmpxLtF64,
    VCmpxLtI16,
    VCmpxLtI32,
    VCmpxLtI64,
    VCmpxLtU16,
    VCmpxLtU32,
    VCmpxLtU64,
    VCmpxNeI16,
    VCmpxNeI32,
    VCmpxNeI64,
    VCmpxNeU16,
    VCmpxNeU32,
    VCmpxNeU64,
    VCmpxNeqF16,
    VCmpxNeqF32,
    VCmpxNeqF64,
    VCmpxNgeF16,
    VCmpxNgeF32,
    VCmpxNgeF64,
    VCmpxNgtF16,
    VCmpxNgtF32,
    VCmpxNgtF64,
    VCmpxNleF16,
    VCmpxNleF32,
    VCmpxNleF64,
    VCmpxNlgF16,
    VCmpxNlgF32,
    VCmpxNlgF64,
    VCmpxNltF16,
    VCmpxNltF32,
    VCmpxNltF64,
    VCmpxOF16,
    VCmpxOF32,
    VCmpxOF64,
    VCmpxTF16,
    VCmpxTF32,
    VCmpxTF64,
    VCmpxTI16,
    VCmpxTI32,
    VCmpxTI64,
    VCmpxTU16,
    VCmpxTU32,
    VCmpxTU64,
    VCmpxTruF16,
    VCmpxTruF32,
    VCmpxTruF64,
    VCmpxTruI16,
    VCmpxTruI32,
    VCmpxTruI64,
    VCmpxTruU16,
    VCmpxTruU32,
    VCmpxTruU64,
    VCmpxUF16,
    VCmpxUF32,
    VCmpxUF64
  )
}
