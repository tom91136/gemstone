// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private object VectorCompareInstructionDefs {
  case object VCmpClassF16 extends Opcode("v_cmp_class_f16", 1418)

  case object VCmpClassF32 extends Opcode("v_cmp_class_f32", 1419)

  case object VCmpClassF64 extends Opcode("v_cmp_class_f64", 1420)

  case object VCmpEqF16 extends Opcode("v_cmp_eq_f16", 1421)

  case object VCmpEqF32 extends Opcode("v_cmp_eq_f32", 1422)

  case object VCmpEqF64 extends Opcode("v_cmp_eq_f64", 1423)

  case object VCmpEqI16 extends Opcode("v_cmp_eq_i16", 1424)

  case object VCmpEqI32 extends Opcode("v_cmp_eq_i32", 1425)

  case object VCmpEqI64 extends Opcode("v_cmp_eq_i64", 1426)

  case object VCmpEqU16 extends Opcode("v_cmp_eq_u16", 1427)

  case object VCmpEqU32 extends Opcode("v_cmp_eq_u32", 1428)

  case object VCmpEqU64 extends Opcode("v_cmp_eq_u64", 1429)

  case object VCmpFF16 extends Opcode("v_cmp_f_f16", 1430)

  case object VCmpFF32 extends Opcode("v_cmp_f_f32", 1431)

  case object VCmpFF64 extends Opcode("v_cmp_f_f64", 1432)

  case object VCmpFI16 extends Opcode("v_cmp_f_i16", 1433)

  case object VCmpFI32 extends Opcode("v_cmp_f_i32", 1434)

  case object VCmpFI64 extends Opcode("v_cmp_f_i64", 1435)

  case object VCmpFU16 extends Opcode("v_cmp_f_u16", 1436)

  case object VCmpFU32 extends Opcode("v_cmp_f_u32", 1437)

  case object VCmpFU64 extends Opcode("v_cmp_f_u64", 1438)

  case object VCmpGeF16 extends Opcode("v_cmp_ge_f16", 1439)

  case object VCmpGeF32 extends Opcode("v_cmp_ge_f32", 1440)

  case object VCmpGeF64 extends Opcode("v_cmp_ge_f64", 1441)

  case object VCmpGeI16 extends Opcode("v_cmp_ge_i16", 1442)

  case object VCmpGeI32 extends Opcode("v_cmp_ge_i32", 1443)

  case object VCmpGeI64 extends Opcode("v_cmp_ge_i64", 1444)

  case object VCmpGeU16 extends Opcode("v_cmp_ge_u16", 1445)

  case object VCmpGeU32 extends Opcode("v_cmp_ge_u32", 1446)

  case object VCmpGeU64 extends Opcode("v_cmp_ge_u64", 1447)

  case object VCmpGtF16 extends Opcode("v_cmp_gt_f16", 1448)

  case object VCmpGtF32 extends Opcode("v_cmp_gt_f32", 1449)

  case object VCmpGtF64 extends Opcode("v_cmp_gt_f64", 1450)

  case object VCmpGtI16 extends Opcode("v_cmp_gt_i16", 1451)

  case object VCmpGtI32 extends Opcode("v_cmp_gt_i32", 1452)

  case object VCmpGtI64 extends Opcode("v_cmp_gt_i64", 1453)

  case object VCmpGtU16 extends Opcode("v_cmp_gt_u16", 1454)

  case object VCmpGtU32 extends Opcode("v_cmp_gt_u32", 1455)

  case object VCmpGtU64 extends Opcode("v_cmp_gt_u64", 1456)

  case object VCmpLeF16 extends Opcode("v_cmp_le_f16", 1457)

  case object VCmpLeF32 extends Opcode("v_cmp_le_f32", 1458)

  case object VCmpLeF64 extends Opcode("v_cmp_le_f64", 1459)

  case object VCmpLeI16 extends Opcode("v_cmp_le_i16", 1460)

  case object VCmpLeI32 extends Opcode("v_cmp_le_i32", 1461)

  case object VCmpLeI64 extends Opcode("v_cmp_le_i64", 1462)

  case object VCmpLeU16 extends Opcode("v_cmp_le_u16", 1463)

  case object VCmpLeU32 extends Opcode("v_cmp_le_u32", 1464)

  case object VCmpLeU64 extends Opcode("v_cmp_le_u64", 1465)

  case object VCmpLgF16 extends Opcode("v_cmp_lg_f16", 1466)

  case object VCmpLgF32 extends Opcode("v_cmp_lg_f32", 1467)

  case object VCmpLgF64 extends Opcode("v_cmp_lg_f64", 1468)

  case object VCmpLtF16 extends Opcode("v_cmp_lt_f16", 1469)

  case object VCmpLtF32 extends Opcode("v_cmp_lt_f32", 1470)

  case object VCmpLtF64 extends Opcode("v_cmp_lt_f64", 1471)

  case object VCmpLtI16 extends Opcode("v_cmp_lt_i16", 1472)

  case object VCmpLtI32 extends Opcode("v_cmp_lt_i32", 1473)

  case object VCmpLtI64 extends Opcode("v_cmp_lt_i64", 1474)

  case object VCmpLtU16 extends Opcode("v_cmp_lt_u16", 1475)

  case object VCmpLtU32 extends Opcode("v_cmp_lt_u32", 1476)

  case object VCmpLtU64 extends Opcode("v_cmp_lt_u64", 1477)

  case object VCmpNeI16 extends Opcode("v_cmp_ne_i16", 1478)

  case object VCmpNeI32 extends Opcode("v_cmp_ne_i32", 1479)

  case object VCmpNeI64 extends Opcode("v_cmp_ne_i64", 1480)

  case object VCmpNeU16 extends Opcode("v_cmp_ne_u16", 1481)

  case object VCmpNeU32 extends Opcode("v_cmp_ne_u32", 1482)

  case object VCmpNeU64 extends Opcode("v_cmp_ne_u64", 1483)

  case object VCmpNeqF16 extends Opcode("v_cmp_neq_f16", 1484)

  case object VCmpNeqF32 extends Opcode("v_cmp_neq_f32", 1485)

  case object VCmpNeqF64 extends Opcode("v_cmp_neq_f64", 1486)

  case object VCmpNgeF16 extends Opcode("v_cmp_nge_f16", 1487)

  case object VCmpNgeF32 extends Opcode("v_cmp_nge_f32", 1488)

  case object VCmpNgeF64 extends Opcode("v_cmp_nge_f64", 1489)

  case object VCmpNgtF16 extends Opcode("v_cmp_ngt_f16", 1490)

  case object VCmpNgtF32 extends Opcode("v_cmp_ngt_f32", 1491)

  case object VCmpNgtF64 extends Opcode("v_cmp_ngt_f64", 1492)

  case object VCmpNleF16 extends Opcode("v_cmp_nle_f16", 1493)

  case object VCmpNleF32 extends Opcode("v_cmp_nle_f32", 1494)

  case object VCmpNleF64 extends Opcode("v_cmp_nle_f64", 1495)

  case object VCmpNlgF16 extends Opcode("v_cmp_nlg_f16", 1496)

  case object VCmpNlgF32 extends Opcode("v_cmp_nlg_f32", 1497)

  case object VCmpNlgF64 extends Opcode("v_cmp_nlg_f64", 1498)

  case object VCmpNltF16 extends Opcode("v_cmp_nlt_f16", 1499)

  case object VCmpNltF32 extends Opcode("v_cmp_nlt_f32", 1500)

  case object VCmpNltF64 extends Opcode("v_cmp_nlt_f64", 1501)

  case object VCmpOF16 extends Opcode("v_cmp_o_f16", 1502)

  case object VCmpOF32 extends Opcode("v_cmp_o_f32", 1503)

  case object VCmpOF64 extends Opcode("v_cmp_o_f64", 1504)

  case object VCmpTF16 extends Opcode("v_cmp_t_f16", 1505)

  case object VCmpTF32 extends Opcode("v_cmp_t_f32", 1506)

  case object VCmpTF64 extends Opcode("v_cmp_t_f64", 1507)

  case object VCmpTI16 extends Opcode("v_cmp_t_i16", 1508)

  case object VCmpTI32 extends Opcode("v_cmp_t_i32", 1509)

  case object VCmpTI64 extends Opcode("v_cmp_t_i64", 1510)

  case object VCmpTU16 extends Opcode("v_cmp_t_u16", 1511)

  case object VCmpTU32 extends Opcode("v_cmp_t_u32", 1512)

  case object VCmpTU64 extends Opcode("v_cmp_t_u64", 1513)

  case object VCmpTruF16 extends Opcode("v_cmp_tru_f16", 1514)

  case object VCmpTruF32 extends Opcode("v_cmp_tru_f32", 1515)

  case object VCmpTruF64 extends Opcode("v_cmp_tru_f64", 1516)

  case object VCmpUF16 extends Opcode("v_cmp_u_f16", 1517)

  case object VCmpUF32 extends Opcode("v_cmp_u_f32", 1518)

  case object VCmpUF64 extends Opcode("v_cmp_u_f64", 1519)

  case object VCmpsEqF32 extends Opcode("v_cmps_eq_f32", 1520)

  case object VCmpsEqF64 extends Opcode("v_cmps_eq_f64", 1521)

  case object VCmpsFF32 extends Opcode("v_cmps_f_f32", 1522)

  case object VCmpsFF64 extends Opcode("v_cmps_f_f64", 1523)

  case object VCmpsGeF32 extends Opcode("v_cmps_ge_f32", 1524)

  case object VCmpsGeF64 extends Opcode("v_cmps_ge_f64", 1525)

  case object VCmpsGtF32 extends Opcode("v_cmps_gt_f32", 1526)

  case object VCmpsGtF64 extends Opcode("v_cmps_gt_f64", 1527)

  case object VCmpsLeF32 extends Opcode("v_cmps_le_f32", 1528)

  case object VCmpsLeF64 extends Opcode("v_cmps_le_f64", 1529)

  case object VCmpsLgF32 extends Opcode("v_cmps_lg_f32", 1530)

  case object VCmpsLgF64 extends Opcode("v_cmps_lg_f64", 1531)

  case object VCmpsLtF32 extends Opcode("v_cmps_lt_f32", 1532)

  case object VCmpsLtF64 extends Opcode("v_cmps_lt_f64", 1533)

  case object VCmpsNeqF32 extends Opcode("v_cmps_neq_f32", 1534)

  case object VCmpsNeqF64 extends Opcode("v_cmps_neq_f64", 1535)

  case object VCmpsNgeF32 extends Opcode("v_cmps_nge_f32", 1536)

  case object VCmpsNgeF64 extends Opcode("v_cmps_nge_f64", 1537)

  case object VCmpsNgtF32 extends Opcode("v_cmps_ngt_f32", 1538)

  case object VCmpsNgtF64 extends Opcode("v_cmps_ngt_f64", 1539)

  case object VCmpsNleF32 extends Opcode("v_cmps_nle_f32", 1540)

  case object VCmpsNleF64 extends Opcode("v_cmps_nle_f64", 1541)

  case object VCmpsNlgF32 extends Opcode("v_cmps_nlg_f32", 1542)

  case object VCmpsNlgF64 extends Opcode("v_cmps_nlg_f64", 1543)

  case object VCmpsNltF32 extends Opcode("v_cmps_nlt_f32", 1544)

  case object VCmpsNltF64 extends Opcode("v_cmps_nlt_f64", 1545)

  case object VCmpsOF32 extends Opcode("v_cmps_o_f32", 1546)

  case object VCmpsOF64 extends Opcode("v_cmps_o_f64", 1547)

  case object VCmpsTruF32 extends Opcode("v_cmps_tru_f32", 1548)

  case object VCmpsTruF64 extends Opcode("v_cmps_tru_f64", 1549)

  case object VCmpsUF32 extends Opcode("v_cmps_u_f32", 1550)

  case object VCmpsUF64 extends Opcode("v_cmps_u_f64", 1551)

  case object VCmpsxEqF32 extends Opcode("v_cmpsx_eq_f32", 1552)

  case object VCmpsxEqF64 extends Opcode("v_cmpsx_eq_f64", 1553)

  case object VCmpsxFF32 extends Opcode("v_cmpsx_f_f32", 1554)

  case object VCmpsxFF64 extends Opcode("v_cmpsx_f_f64", 1555)

  case object VCmpsxGeF32 extends Opcode("v_cmpsx_ge_f32", 1556)

  case object VCmpsxGeF64 extends Opcode("v_cmpsx_ge_f64", 1557)

  case object VCmpsxGtF32 extends Opcode("v_cmpsx_gt_f32", 1558)

  case object VCmpsxGtF64 extends Opcode("v_cmpsx_gt_f64", 1559)

  case object VCmpsxLeF32 extends Opcode("v_cmpsx_le_f32", 1560)

  case object VCmpsxLeF64 extends Opcode("v_cmpsx_le_f64", 1561)

  case object VCmpsxLgF32 extends Opcode("v_cmpsx_lg_f32", 1562)

  case object VCmpsxLgF64 extends Opcode("v_cmpsx_lg_f64", 1563)

  case object VCmpsxLtF32 extends Opcode("v_cmpsx_lt_f32", 1564)

  case object VCmpsxLtF64 extends Opcode("v_cmpsx_lt_f64", 1565)

  case object VCmpsxNeqF32 extends Opcode("v_cmpsx_neq_f32", 1566)

  case object VCmpsxNeqF64 extends Opcode("v_cmpsx_neq_f64", 1567)

  case object VCmpsxNgeF32 extends Opcode("v_cmpsx_nge_f32", 1568)

  case object VCmpsxNgeF64 extends Opcode("v_cmpsx_nge_f64", 1569)

  case object VCmpsxNgtF32 extends Opcode("v_cmpsx_ngt_f32", 1570)

  case object VCmpsxNgtF64 extends Opcode("v_cmpsx_ngt_f64", 1571)

  case object VCmpsxNleF32 extends Opcode("v_cmpsx_nle_f32", 1572)

  case object VCmpsxNleF64 extends Opcode("v_cmpsx_nle_f64", 1573)

  case object VCmpsxNlgF32 extends Opcode("v_cmpsx_nlg_f32", 1574)

  case object VCmpsxNlgF64 extends Opcode("v_cmpsx_nlg_f64", 1575)

  case object VCmpsxNltF32 extends Opcode("v_cmpsx_nlt_f32", 1576)

  case object VCmpsxNltF64 extends Opcode("v_cmpsx_nlt_f64", 1577)

  case object VCmpsxOF32 extends Opcode("v_cmpsx_o_f32", 1578)

  case object VCmpsxOF64 extends Opcode("v_cmpsx_o_f64", 1579)

  case object VCmpsxTruF32 extends Opcode("v_cmpsx_tru_f32", 1580)

  case object VCmpsxTruF64 extends Opcode("v_cmpsx_tru_f64", 1581)

  case object VCmpsxUF32 extends Opcode("v_cmpsx_u_f32", 1582)

  case object VCmpsxUF64 extends Opcode("v_cmpsx_u_f64", 1583)

  case object VCmpxClassF16 extends Opcode("v_cmpx_class_f16", 1584)

  case object VCmpxClassF32 extends Opcode("v_cmpx_class_f32", 1585)

  case object VCmpxClassF64 extends Opcode("v_cmpx_class_f64", 1586)

  case object VCmpxEqF16 extends Opcode("v_cmpx_eq_f16", 1587)

  case object VCmpxEqF32 extends Opcode("v_cmpx_eq_f32", 1588)

  case object VCmpxEqF64 extends Opcode("v_cmpx_eq_f64", 1589)

  case object VCmpxEqI16 extends Opcode("v_cmpx_eq_i16", 1590)

  case object VCmpxEqI32 extends Opcode("v_cmpx_eq_i32", 1591)

  case object VCmpxEqI64 extends Opcode("v_cmpx_eq_i64", 1592)

  case object VCmpxEqU16 extends Opcode("v_cmpx_eq_u16", 1593)

  case object VCmpxEqU32 extends Opcode("v_cmpx_eq_u32", 1594)

  case object VCmpxEqU64 extends Opcode("v_cmpx_eq_u64", 1595)

  case object VCmpxFF16 extends Opcode("v_cmpx_f_f16", 1596)

  case object VCmpxFF32 extends Opcode("v_cmpx_f_f32", 1597)

  case object VCmpxFF64 extends Opcode("v_cmpx_f_f64", 1598)

  case object VCmpxFI16 extends Opcode("v_cmpx_f_i16", 1599)

  case object VCmpxFI32 extends Opcode("v_cmpx_f_i32", 1600)

  case object VCmpxFI64 extends Opcode("v_cmpx_f_i64", 1601)

  case object VCmpxFU16 extends Opcode("v_cmpx_f_u16", 1602)

  case object VCmpxFU32 extends Opcode("v_cmpx_f_u32", 1603)

  case object VCmpxFU64 extends Opcode("v_cmpx_f_u64", 1604)

  case object VCmpxGeF16 extends Opcode("v_cmpx_ge_f16", 1605)

  case object VCmpxGeF32 extends Opcode("v_cmpx_ge_f32", 1606)

  case object VCmpxGeF64 extends Opcode("v_cmpx_ge_f64", 1607)

  case object VCmpxGeI16 extends Opcode("v_cmpx_ge_i16", 1608)

  case object VCmpxGeI32 extends Opcode("v_cmpx_ge_i32", 1609)

  case object VCmpxGeI64 extends Opcode("v_cmpx_ge_i64", 1610)

  case object VCmpxGeU16 extends Opcode("v_cmpx_ge_u16", 1611)

  case object VCmpxGeU32 extends Opcode("v_cmpx_ge_u32", 1612)

  case object VCmpxGeU64 extends Opcode("v_cmpx_ge_u64", 1613)

  case object VCmpxGtF16 extends Opcode("v_cmpx_gt_f16", 1614)

  case object VCmpxGtF32 extends Opcode("v_cmpx_gt_f32", 1615)

  case object VCmpxGtF64 extends Opcode("v_cmpx_gt_f64", 1616)

  case object VCmpxGtI16 extends Opcode("v_cmpx_gt_i16", 1617)

  case object VCmpxGtI32 extends Opcode("v_cmpx_gt_i32", 1618)

  case object VCmpxGtI64 extends Opcode("v_cmpx_gt_i64", 1619)

  case object VCmpxGtU16 extends Opcode("v_cmpx_gt_u16", 1620)

  case object VCmpxGtU32 extends Opcode("v_cmpx_gt_u32", 1621)

  case object VCmpxGtU64 extends Opcode("v_cmpx_gt_u64", 1622)

  case object VCmpxLeF16 extends Opcode("v_cmpx_le_f16", 1623)

  case object VCmpxLeF32 extends Opcode("v_cmpx_le_f32", 1624)

  case object VCmpxLeF64 extends Opcode("v_cmpx_le_f64", 1625)

  case object VCmpxLeI16 extends Opcode("v_cmpx_le_i16", 1626)

  case object VCmpxLeI32 extends Opcode("v_cmpx_le_i32", 1627)

  case object VCmpxLeI64 extends Opcode("v_cmpx_le_i64", 1628)

  case object VCmpxLeU16 extends Opcode("v_cmpx_le_u16", 1629)

  case object VCmpxLeU32 extends Opcode("v_cmpx_le_u32", 1630)

  case object VCmpxLeU64 extends Opcode("v_cmpx_le_u64", 1631)

  case object VCmpxLgF16 extends Opcode("v_cmpx_lg_f16", 1632)

  case object VCmpxLgF32 extends Opcode("v_cmpx_lg_f32", 1633)

  case object VCmpxLgF64 extends Opcode("v_cmpx_lg_f64", 1634)

  case object VCmpxLtF16 extends Opcode("v_cmpx_lt_f16", 1635)

  case object VCmpxLtF32 extends Opcode("v_cmpx_lt_f32", 1636)

  case object VCmpxLtF64 extends Opcode("v_cmpx_lt_f64", 1637)

  case object VCmpxLtI16 extends Opcode("v_cmpx_lt_i16", 1638)

  case object VCmpxLtI32 extends Opcode("v_cmpx_lt_i32", 1639)

  case object VCmpxLtI64 extends Opcode("v_cmpx_lt_i64", 1640)

  case object VCmpxLtU16 extends Opcode("v_cmpx_lt_u16", 1641)

  case object VCmpxLtU32 extends Opcode("v_cmpx_lt_u32", 1642)

  case object VCmpxLtU64 extends Opcode("v_cmpx_lt_u64", 1643)

  case object VCmpxNeI16 extends Opcode("v_cmpx_ne_i16", 1644)

  case object VCmpxNeI32 extends Opcode("v_cmpx_ne_i32", 1645)

  case object VCmpxNeI64 extends Opcode("v_cmpx_ne_i64", 1646)

  case object VCmpxNeU16 extends Opcode("v_cmpx_ne_u16", 1647)

  case object VCmpxNeU32 extends Opcode("v_cmpx_ne_u32", 1648)

  case object VCmpxNeU64 extends Opcode("v_cmpx_ne_u64", 1649)

  case object VCmpxNeqF16 extends Opcode("v_cmpx_neq_f16", 1650)

  case object VCmpxNeqF32 extends Opcode("v_cmpx_neq_f32", 1651)

  case object VCmpxNeqF64 extends Opcode("v_cmpx_neq_f64", 1652)

  case object VCmpxNgeF16 extends Opcode("v_cmpx_nge_f16", 1653)

  case object VCmpxNgeF32 extends Opcode("v_cmpx_nge_f32", 1654)

  case object VCmpxNgeF64 extends Opcode("v_cmpx_nge_f64", 1655)

  case object VCmpxNgtF16 extends Opcode("v_cmpx_ngt_f16", 1656)

  case object VCmpxNgtF32 extends Opcode("v_cmpx_ngt_f32", 1657)

  case object VCmpxNgtF64 extends Opcode("v_cmpx_ngt_f64", 1658)

  case object VCmpxNleF16 extends Opcode("v_cmpx_nle_f16", 1659)

  case object VCmpxNleF32 extends Opcode("v_cmpx_nle_f32", 1660)

  case object VCmpxNleF64 extends Opcode("v_cmpx_nle_f64", 1661)

  case object VCmpxNlgF16 extends Opcode("v_cmpx_nlg_f16", 1662)

  case object VCmpxNlgF32 extends Opcode("v_cmpx_nlg_f32", 1663)

  case object VCmpxNlgF64 extends Opcode("v_cmpx_nlg_f64", 1664)

  case object VCmpxNltF16 extends Opcode("v_cmpx_nlt_f16", 1665)

  case object VCmpxNltF32 extends Opcode("v_cmpx_nlt_f32", 1666)

  case object VCmpxNltF64 extends Opcode("v_cmpx_nlt_f64", 1667)

  case object VCmpxOF16 extends Opcode("v_cmpx_o_f16", 1668)

  case object VCmpxOF32 extends Opcode("v_cmpx_o_f32", 1669)

  case object VCmpxOF64 extends Opcode("v_cmpx_o_f64", 1670)

  case object VCmpxTF16 extends Opcode("v_cmpx_t_f16", 1671)

  case object VCmpxTF32 extends Opcode("v_cmpx_t_f32", 1672)

  case object VCmpxTF64 extends Opcode("v_cmpx_t_f64", 1673)

  case object VCmpxTI16 extends Opcode("v_cmpx_t_i16", 1674)

  case object VCmpxTI32 extends Opcode("v_cmpx_t_i32", 1675)

  case object VCmpxTI64 extends Opcode("v_cmpx_t_i64", 1676)

  case object VCmpxTU16 extends Opcode("v_cmpx_t_u16", 1677)

  case object VCmpxTU32 extends Opcode("v_cmpx_t_u32", 1678)

  case object VCmpxTU64 extends Opcode("v_cmpx_t_u64", 1679)

  case object VCmpxTruF16 extends Opcode("v_cmpx_tru_f16", 1680)

  case object VCmpxTruF32 extends Opcode("v_cmpx_tru_f32", 1681)

  case object VCmpxTruF64 extends Opcode("v_cmpx_tru_f64", 1682)

  case object VCmpxUF16 extends Opcode("v_cmpx_u_f16", 1683)

  case object VCmpxUF32 extends Opcode("v_cmpx_u_f32", 1684)

  case object VCmpxUF64 extends Opcode("v_cmpx_u_f64", 1685)

  val definitions: Vec[Opcode] = Vec(
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
    VCmpUF16,
    VCmpUF32,
    VCmpUF64,
    VCmpsEqF32,
    VCmpsEqF64,
    VCmpsFF32,
    VCmpsFF64,
    VCmpsGeF32,
    VCmpsGeF64,
    VCmpsGtF32,
    VCmpsGtF64,
    VCmpsLeF32,
    VCmpsLeF64,
    VCmpsLgF32,
    VCmpsLgF64,
    VCmpsLtF32,
    VCmpsLtF64,
    VCmpsNeqF32,
    VCmpsNeqF64,
    VCmpsNgeF32,
    VCmpsNgeF64,
    VCmpsNgtF32,
    VCmpsNgtF64,
    VCmpsNleF32,
    VCmpsNleF64,
    VCmpsNlgF32,
    VCmpsNlgF64,
    VCmpsNltF32,
    VCmpsNltF64,
    VCmpsOF32,
    VCmpsOF64,
    VCmpsTruF32,
    VCmpsTruF64,
    VCmpsUF32,
    VCmpsUF64,
    VCmpsxEqF32,
    VCmpsxEqF64,
    VCmpsxFF32,
    VCmpsxFF64,
    VCmpsxGeF32,
    VCmpsxGeF64,
    VCmpsxGtF32,
    VCmpsxGtF64,
    VCmpsxLeF32,
    VCmpsxLeF64,
    VCmpsxLgF32,
    VCmpsxLgF64,
    VCmpsxLtF32,
    VCmpsxLtF64,
    VCmpsxNeqF32,
    VCmpsxNeqF64,
    VCmpsxNgeF32,
    VCmpsxNgeF64,
    VCmpsxNgtF32,
    VCmpsxNgtF64,
    VCmpsxNleF32,
    VCmpsxNleF64,
    VCmpsxNlgF32,
    VCmpsxNlgF64,
    VCmpsxNltF32,
    VCmpsxNltF64,
    VCmpsxOF32,
    VCmpsxOF64,
    VCmpsxTruF32,
    VCmpsxTruF64,
    VCmpsxUF32,
    VCmpsxUF64,
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
    VCmpxUF16,
    VCmpxUF32,
    VCmpxUF64
  )
}
