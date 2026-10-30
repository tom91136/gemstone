package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.Suite

class HardwareGoldenSuite extends Suite {
  import hardwareGolden.*

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8: video_ptx vadd2_uuu_h10_h11
  private val vadd2H10H11Sm61 = rows(
    """
      |00000000 00000000 00000000 -> 00000000
      |7fffffff 12345678 feedc0de -> fffe7ffe
      |feedc0de 80ff7f01 feedc0de -> fddabfcb
      |bd172f8d 18bbffd3 e18c0d45 -> 7a2eeca4
      |ffffffff feedc0de ffffffff -> fffefffe
      |00ff00ff 00ff00ff 00000000 -> 01fe01fe
      |a6d89824 f88e15c2 5353727e -> 4db03efc
      |97a830e0 7e07c71a 560fcfc9 -> 2f50c888
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8: video_ptx vadd4_uuu_b3210_b0123
  private val vadd4B3210B0123Sm61 = rows(
    """
      |00000000 00000000 00000000 -> 00000000
      |7fffffff 12345678 feedc0de -> 7efefe7e
      |feedc0de 80ff7f01 feedc0de -> dcadaddc
      |bd172f8d 18bbffd3 e18c0d45 -> 4a46464a
      |ffffffff feedc0de ffffffff -> fefefefe
      |00ff00ff 00ff00ff 00000000 -> ffffffff
      |a6d89824 f88e15c2 5353727e -> ca7070ca
      |97a830e0 7e07c71a 560fcfc9 -> 77d8d877
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8: video_ptx vset2_uu_lt_add_h10_h11
  private val vset2LtAddSm61 = rows(
    """
      |00000000 00000000 00000000 -> 00000000
      |7fffffff 12345678 feedc0de -> feedc0de
      |feedc0de 80ff7f01 feedc0de -> feedc0df
      |bd172f8d 18bbffd3 e18c0d45 -> e18c0d46
      |ffffffff feedc0de ffffffff -> ffffffff
      |00ff00ff 00ff00ff 00000000 -> 00000000
      |a6d89824 f88e15c2 5353727e -> 5353727f
      |97a830e0 7e07c71a 560fcfc9 -> 560fcfca
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8: video_ptx vmad_uuu_shr7_b0_b1
  private val vmadShr7Sm61 = rows(
    """
      |00000000 00000000 00000000 -> 00000000
      |ffffffff ffffffff 00000000 -> 000001fc
      |80000000 00000000 00000000 -> 00000000
      |7fffffff ffffffff 00000000 -> 000001fc
      |00ff00ff ffffffff 00000000 -> 000001fc
      |12345678 00000000 00000000 -> 00000000
      |7fffffff 00000000 feedc0de -> 01fddb81
      |7fffffff feedc0de ffffffff -> 0200017e
      |76e10f8f dc5eaf01 17db4d54 -> 002fb75e
      |7fffffff 12345678 feedc0de -> 01fddc2d
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu RcpRz
  private val rcpRzSm61 = rows(
    """
      |00000000 -> 7f800000
      |b01d4a62 -> ced053ec
      |f03aa117 -> 8eaf93fa
      |c2b10000 -> bc392143
      |05dd9e05 -> 7913dbc7
      |c45b0341 -> ba959dec
      |c16231d6 -> bd90ddca
      |423c0000 -> 3cae4c41
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu RcpRm
  private val rcpRmSm61 = rows(
    """
      |00000000 -> 7f800000
      |b01d4a62 -> ced053ed
      |f03aa117 -> 8eaf93fb
      |c2b10000 -> bc392144
      |05dd9e05 -> 7913dbc7
      |c45b0341 -> ba959ded
      |c16231d6 -> bd90ddcb
      |423c0000 -> 3cae4c41
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu RcpRp
  private val rcpRpSm61 = rows(
    """
      |00000000 -> 7f800000
      |b01d4a62 -> ced053ec
      |f03aa117 -> 8eaf93fa
      |c2b10000 -> bc392143
      |05dd9e05 -> 7913dbc8
      |c45b0341 -> ba959dec
      |c16231d6 -> bd90ddca
      |423c0000 -> 3cae4c42
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu SqrtRz
  private val sqrtRzSm61 = rows(
    """
      |00000000 -> 00000000
      |b01d4a62 -> 7fffffff
      |f03aa117 -> 7fffffff
      |c2b10000 -> 7fffffff
      |05dd9e05 -> 22a86cd2
      |c45b0341 -> 7fffffff
      |c16231d6 -> 7fffffff
      |423c0000 -> 40db6185
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu DivRz
  private val divRzSm61 = rows(
    """
      |00000000 80000000 -> 7fffffff
      |b01d4a62 c2b00000 -> 2ce4c948
      |f03aa117 6f001855 -> c0ba7da3
      |c2b10000 99b4c684 -> 687aa74b
      |c9095e7b 461de97b -> c25eb257
      |42ced646 bf77a24c -> c2d5d323
      |c16231d5 60277325 -> a0ace7b2
      |423a0000 43f331f0 -> 3dc3cb1f
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu
  // RcpApprox
  private val rcpApproxSm61 = rows(
    """
      |461c4000 -> 38d1b718
      |bcd513e8 -> c219c8c3
      |44321758 -> 3ab7fee1
      |1d9bcb1e -> 6152546d
      |c2a50000 -> bc46980d
      |00000000 -> 7f800000
      |4b800000 -> 33800000
      |7e800000 -> 00800000
      |c2aa0000 -> bc40c0c1
      |71157e49 -> 0ddb319b
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8: mufu SinApprox
  private val sinApproxSm61 = rows(
    """
      |00000001 -> 00000000
      |ebefe0cb -> 80000000
      |c7005097 -> bdc3c16f
      |9d4c716d -> 80000000
      |c1c80000 -> 3e078779
      |00000000 -> 00000000
      |7fc00000 -> 7fffffff
      |ffffffff -> 7fffffff
      |bfaadc0e -> bf78e816
      |40fb53d1 -> 3f800000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu
  // Lg2Approx
  private val lg2ApproxSm61 = rows(
    """
      |00800000 -> c2fbffff
      |6ffb2b0e -> 42c1f1ec
      |42292ad7 -> 40acdfb3
      |3a58f3ce -> c123d1fb
      |42580000 -> 40b82809
      |00000000 -> ff800000
      |4b000000 -> 41b80000
      |3fb504f3 -> 3effffff
      |42a70000 -> 40cc474e
      |4b000001 -> 41b80000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu
  // Ex2Approx
  private val ex2ApproxSm61 = rows(
    """
      |bf000000 -> 3f3504f2
      |3f78f767 -> 3ffb2bba
      |c25be958 -> 2401fa67
      |41d5a0d9 -> 4cd07283
      |c2990000 -> 193504f2
      |00000000 -> 3f800000
      |4effffff -> 7f800000
      |2f800000 -> 3f800000
      |b7a6fb6f -> 3f7fff19
      |c2640000 -> 23000000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu
  // RsqrtApprox
  private val rsqrtApproxSm61 = rows(
    """
      |00000001 -> 64b504f2
      |03f3fc1e -> 5d396c47
      |710bf0da -> 26ad1fcb
      |41a40000 -> 3e6229ec
      |42b10000 -> 3dd9b32f
      |00000000 -> 7f800000
      |4effffff -> 37b504f4
      |7e800000 -> 20000000
      |c3140000 -> 7fffffff
      |c2cd0000 -> 7fffffff
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: carry_chain
  // MadCcLoHi
  private val madCcLoHiSm61 = rows(
    """
      |55cb6861 fffffffe 91c90540 -> e632347e 55cb6860 00000000
      |f36fb28a 12345678 00000001 -> 9a5e0cb1 114f9f77 00000000
      |7fffffff ffff0000 00000001 -> 00010001 7fff7fff 00000000
      |80000000 12345678 12345678 -> 12345678 091a2b3c 00000000
      |ffffffff c81dea0f 80000001 -> b7e215f2 c81dea0e 00000000
      |ffffffff 00000002 80000000 -> 7ffffffe 00000002 00000000
      |00000002 ffff0000 ffff0000 -> fffd0000 00000002 00000000
      |7fffffff 80000000 ffffffff -> 7fffffff 40000000 00000000
      |d11d676c deadbeef bb369f41 -> 54685515 b5e56c86 00000000
      |0000ffff deadbeef 914f6f91 -> 7190b0a2 0000dead 00000000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: carry_chain
  // MadCcSigned
  private val madCcSignedSm61 = rows(
    """
      |1c21ca58 deadbeef 7c7ad606 -> e1ac0e2e fc569af1 00000000
      |5f3c3d25 7fffffff 8da39bff -> ae675eda 2f9e1e92 00000000
      |00000002 eef4bb9b ffffffff -> dde97735 00000000 00000001
      |12345678 cfef285f 80000001 -> 30f6d689 fc94fd2f 00000000
      |ffff0000 deadbeef ffffffff -> 4110ffff 00002153 00000000
      |00000000 7fffffff 12345678 -> 12345678 00000000 00000000
      |0000ffff 0000ffff 00000002 -> fffe0003 00000000 00000000
      |00000000 37aa7eb8 deadbeef -> deadbeef 00000000 00000000
      |3e9837b3 00000001 e81939d7 -> 26b1718a 00000001 00000000
      |598b7e2b 00000002 bb113ae1 -> 6e283737 00000001 00000000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: carry_chain
  // AddCarryIn
  private val addCarryInSm61 = rows(
    """
      |deadbeef 0000ffff 00000001 80000001 -> deaebeee 80000002 00000000
      |12345678 12345678 deadbeef 12345678 -> 2468acf0 f0e21567 00000000
      |deadbeef fffffffe 00000002 ffffffff -> deadbeed 00000002 00000001
      |00000001 00000001 deadbeef 0000ffff -> 00000002 deaebeee 00000000
      |361e46d7 b202c716 0000ffff d4f7bfb4 -> e8210ded d4f8bfb3 00000000
      |00000001 f528acaf 03c9ea3a 00000001 -> f528acb0 03c9ea3b 00000000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8: int_selectors Prmt
  private val prmtSm61 = rows(
    """
      |00000000 00000000 00000000 -> 00000000
      |00008000 deadbeef 80000001 -> 00000080
      |ffffffff 55555555 0000ffff -> 00000000
      |deadbeef 00008000 fffffffe -> 00000000
      |ccad9df7 f585d451 10c1d870 -> fffff5f7
      |b7ee0000 0000421d 0000b219 -> ffee0000
      |"""
  )

  // Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: video_ptx vadd2_uuu_h10_h11
  private val vadd2H10H11Sm70 = rows(
    """
      |00000000 00000000 00000000 -> 00000000
      |7fffffff 12345678 feedc0de -> fffe7ffe
      |feedc0de 80ff7f01 feedc0de -> fddabfcb
      |e18c0d45 18bbffd3 bd172f8d -> c318eed1
      |ffffffff feedc0de ffffffff -> fffefffe
      |00ff00ff 00ff00ff 00000000 -> 01fe01fe
      |5353727e f88e15c2 a6d89824 -> a6a6c5d1
      |560fcfc9 7e07c71a 97a830e0 -> ac1e25d8
      |"""
  )

  // Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: video_ptx
  // vadd4_uuu_b3210_b0123
  private val vadd4B3210B0123Sm70 = rows(
    """
      |00000000 00000000 00000000 -> 00000000
      |7fffffff 12345678 feedc0de -> 7efefe7e
      |feedc0de 80ff7f01 feedc0de -> dcadaddc
      |e18c0d45 18bbffd3 bd172f8d -> 26999926
      |ffffffff feedc0de ffffffff -> fefefefe
      |00ff00ff 00ff00ff 00000000 -> ffffffff
      |5353727e f88e15c2 a6d89824 -> d1c5c5d1
      |560fcfc9 7e07c71a 97a830e0 -> 1fdede1f
      |"""
  )

  // Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: video_ptx
  // vset2_uu_lt_add_h10_h11
  private val vset2LtAddSm70 = rows(
    """
      |00000000 00000000 00000000 -> 00000000
      |7fffffff 12345678 feedc0de -> feedc0de
      |feedc0de 80ff7f01 feedc0de -> feedc0df
      |e18c0d45 18bbffd3 bd172f8d -> bd172f8e
      |ffffffff feedc0de ffffffff -> ffffffff
      |00ff00ff 00ff00ff 00000000 -> 00000000
      |5353727e f88e15c2 a6d89824 -> a6d89824
      |560fcfc9 7e07c71a 97a830e0 -> 97a830e0
      |"""
  )

  // Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: video_ptx
  // vmad_uuu_shr7_b0_b1
  private val vmadShr7Sm70 = rows(
    """
      |00000000 ffffffff feedc0de -> fffddb81
      |7fffffff 00000000 feedc0de -> fffddb81
      |12345678 ffffffff feedc0de -> fffddc70
      |28f7216f 114658a8 9ff9fe5e -> ff3ff449
      |da659054 b95a14e4 d35d0ff2 -> ffa6ba2d
      |00000000 00000000 00000000 -> 00000000
      |80000000 00000000 00000000 -> 00000000
      |00ff00ff ffffffff 00000000 -> 000001fc
      |80ff7f01 00ff00ff 12345678 -> 002468ac
      |12345678 feedc0de 12345678 -> 00246960
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu RcpRz
  private val rcpRzSm70 = rows(
    """
      |00000000 -> 7f800000
      |b01d4a62 -> ced053ec
      |f03aa117 -> 8eaf93fa
      |c2b10000 -> bc392143
      |05dd9e05 -> 7913dbc7
      |c45b0341 -> ba959dec
      |c16231d6 -> bd90ddca
      |423c0000 -> 3cae4c41
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu RcpRm
  private val rcpRmSm70 = rows(
    """
      |00000000 -> 7f800000
      |b01d4a62 -> ced053ed
      |f03aa117 -> 8eaf93fb
      |c2b10000 -> bc392144
      |05dd9e05 -> 7913dbc7
      |c45b0341 -> ba959ded
      |c16231d6 -> bd90ddcb
      |423c0000 -> 3cae4c41
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu RcpRp
  private val rcpRpSm70 = rows(
    """
      |00000000 -> 7f800000
      |b01d4a62 -> ced053ec
      |f03aa117 -> 8eaf93fa
      |c2b10000 -> bc392143
      |05dd9e05 -> 7913dbc8
      |c45b0341 -> ba959dec
      |c16231d6 -> bd90ddca
      |423c0000 -> 3cae4c42
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu SqrtRz
  private val sqrtRzSm70 = rows(
    """
      |00000000 -> 00000000
      |b01d4a62 -> 7fffffff
      |f03aa117 -> 7fffffff
      |c2b10000 -> 7fffffff
      |05dd9e05 -> 22a86cd2
      |c45b0341 -> 7fffffff
      |c16231d6 -> 7fffffff
      |423c0000 -> 40db6185
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu DivRz
  private val divRzSm70 = rows(
    """
      |00000000 80000000 -> 7fffffff
      |b01d4a62 c2b00000 -> 2ce4c948
      |f03aa117 6f001855 -> c0ba7da3
      |c2b10000 99b4c684 -> 687aa74b
      |c9095e7b 461de97b -> c25eb257
      |42ced646 bf77a24c -> c2d5d323
      |c16231d5 60277325 -> a0ace7b2
      |423a0000 43f331f0 -> 3dc3cb1f
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu
  // RcpApprox
  private val rcpApproxSm70 = rows(
    """
      |461c4000 -> 38d1b718
      |bcd513e8 -> c219c8c3
      |44321758 -> 3ab7fee1
      |1d9bcb1e -> 6152546d
      |c2a50000 -> bc46980d
      |00000000 -> 7f800000
      |4b800000 -> 33800000
      |7e800000 -> 00800000
      |c2aa0000 -> bc40c0c1
      |71157e49 -> 0ddb319b
      |"""
  )

  // Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu SinApprox
  private val sinApproxSm70 = rows(
    """
      |00000001 -> 00000000
      |ce1ce6c9 -> 80000000
      |47c114c8 -> bf5f8327
      |c26231d6 -> 36c2c970
      |c1d40000 -> bf7ab725
      |00000000 -> 00000000
      |7fc00000 -> 7fffffff
      |ffffffff -> 7fffffff
      |bfaadc0e -> bf78e816
      |41eec2d5 -> bf800000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu
  // Lg2Approx
  private val lg2ApproxSm70 = rows(
    """
      |00800000 -> c2fbffff
      |6ffb2b0e -> 42c1f1ec
      |42292ad7 -> 40acdfb3
      |3a58f3ce -> c123d1fb
      |42580000 -> 40b82809
      |00000000 -> ff800000
      |4b000000 -> 41b80000
      |3fb504f3 -> 3effffff
      |42a70000 -> 40cc474e
      |4b000001 -> 41b80000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu
  // Ex2Approx
  private val ex2ApproxSm70 = rows(
    """
      |bf000000 -> 3f3504f2
      |3f78f767 -> 3ffb2bba
      |c25be958 -> 2401fa67
      |41d5a0d9 -> 4cd07283
      |c2990000 -> 193504f2
      |00000000 -> 3f800000
      |4effffff -> 7f800000
      |2f800000 -> 3f800000
      |b7a6fb6f -> 3f7fff19
      |c2640000 -> 23000000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu
  // RsqrtApprox
  private val rsqrtApproxSm70 = rows(
    """
      |00000001 -> 64b504f2
      |03f3fc1e -> 5d396c47
      |710bf0da -> 26ad1fcb
      |41a40000 -> 3e6229ec
      |42b10000 -> 3dd9b32f
      |00000000 -> 7f800000
      |4effffff -> 37b504f4
      |7e800000 -> 20000000
      |c3140000 -> 7fffffff
      |c2cd0000 -> 7fffffff
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: carry_chain
  // MadCcLoHi
  private val madCcLoHiSm70 = rows(
    """
      |55cb6861 fffffffe 91c90540 -> e632347e 55cb6860 00000000
      |f36fb28a 12345678 00000001 -> 9a5e0cb1 114f9f77 00000000
      |7fffffff ffff0000 00000001 -> 00010001 7fff7fff 00000000
      |80000000 12345678 12345678 -> 12345678 091a2b3c 00000000
      |ffffffff c81dea0f 80000001 -> b7e215f2 c81dea0e 00000000
      |ffffffff 00000002 80000000 -> 7ffffffe 00000002 00000000
      |00000002 ffff0000 ffff0000 -> fffd0000 00000002 00000000
      |7fffffff 80000000 ffffffff -> 7fffffff 40000000 00000000
      |d11d676c deadbeef bb369f41 -> 54685515 b5e56c86 00000000
      |0000ffff deadbeef 914f6f91 -> 7190b0a2 0000dead 00000000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: carry_chain
  // MadCcSigned
  private val madCcSignedSm70 = rows(
    """
      |1c21ca58 deadbeef 7c7ad606 -> e1ac0e2e fc569af1 00000000
      |5f3c3d25 7fffffff 8da39bff -> ae675eda 2f9e1e92 00000000
      |00000002 eef4bb9b ffffffff -> dde97735 00000000 00000001
      |12345678 cfef285f 80000001 -> 30f6d689 fc94fd2f 00000000
      |ffff0000 deadbeef ffffffff -> 4110ffff 00002153 00000000
      |00000000 7fffffff 12345678 -> 12345678 00000000 00000000
      |0000ffff 0000ffff 00000002 -> fffe0003 00000000 00000000
      |00000000 37aa7eb8 deadbeef -> deadbeef 00000000 00000000
      |3e9837b3 00000001 e81939d7 -> 26b1718a 00000001 00000000
      |598b7e2b 00000002 bb113ae1 -> 6e283737 00000001 00000000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: carry_chain
  // AddCarryIn
  private val addCarryInSm70 = rows(
    """
      |deadbeef 0000ffff 00000001 80000001 -> deaebeee 80000002 00000000
      |12345678 12345678 deadbeef 12345678 -> 2468acf0 f0e21567 00000000
      |deadbeef fffffffe 00000002 ffffffff -> deadbeed 00000002 00000001
      |00000001 00000001 deadbeef 0000ffff -> 00000002 deaebeee 00000000
      |361e46d7 b202c716 0000ffff d4f7bfb4 -> e8210ded d4f8bfb3 00000000
      |00000001 f528acaf 03c9ea3a 00000001 -> f528acb0 03c9ea3b 00000000
      |"""
  )

  // Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: int_selectors Prmt
  private val prmtSm70 = rows(
    """
      |00000000 00000000 00000000 -> 00000000
      |00008000 deadbeef 80000001 -> 00000080
      |ffffffff 55555555 0000ffff -> 00000000
      |deadbeef 00008000 fffffffe -> 00000000
      |10c1d870 f585d451 ccad9df7 -> fffffff5
      |b7ee0000 0000421d 0000b219 -> ffee0000
      |"""
  )

  // Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: video_ptx vadd2_uuu_h10_h11
  private val vadd2H10H11Sm89 = rows(
    """
      |00000000 00000000 00000000 -> 00000000
      |7fffffff 12345678 feedc0de -> fffe7ffe
      |feedc0de 80ff7f01 feedc0de -> fddabfcb
      |e18c0d45 18bbffd3 bd172f8d -> c318eed1
      |ffffffff feedc0de ffffffff -> fffefffe
      |00ff00ff 00ff00ff 00000000 -> 01fe01fe
      |5353727e f88e15c2 a6d89824 -> a6a6c5d1
      |560fcfc9 7e07c71a 97a830e0 -> ac1e25d8
      |"""
  )

  // Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: video_ptx
  // vadd4_uuu_b3210_b0123
  private val vadd4B3210B0123Sm89 = rows(
    """
      |00000000 00000000 00000000 -> 00000000
      |7fffffff 12345678 feedc0de -> 7efefe7e
      |feedc0de 80ff7f01 feedc0de -> dcadaddc
      |e18c0d45 18bbffd3 bd172f8d -> 26999926
      |ffffffff feedc0de ffffffff -> fefefefe
      |00ff00ff 00ff00ff 00000000 -> ffffffff
      |5353727e f88e15c2 a6d89824 -> d1c5c5d1
      |560fcfc9 7e07c71a 97a830e0 -> 1fdede1f
      |"""
  )

  // Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: video_ptx
  // vset2_uu_lt_add_h10_h11
  private val vset2LtAddSm89 = rows(
    """
      |00000000 00000000 00000000 -> 00000000
      |7fffffff 12345678 feedc0de -> feedc0de
      |feedc0de 80ff7f01 feedc0de -> feedc0df
      |e18c0d45 18bbffd3 bd172f8d -> bd172f8e
      |ffffffff feedc0de ffffffff -> ffffffff
      |00ff00ff 00ff00ff 00000000 -> 00000000
      |5353727e f88e15c2 a6d89824 -> a6d89824
      |560fcfc9 7e07c71a 97a830e0 -> 97a830e0
      |"""
  )

  // Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: video_ptx
  // vmad_uuu_shr7_b0_b1
  private val vmadShr7Sm89 = rows(
    """
      |00000000 ffffffff feedc0de -> fffddb81
      |7fffffff 00000000 feedc0de -> fffddb81
      |12345678 ffffffff feedc0de -> fffddc70
      |28f7216f 114658a8 9ff9fe5e -> ff3ff449
      |da659054 b95a14e4 d35d0ff2 -> ffa6ba2d
      |00000000 00000000 00000000 -> 00000000
      |80000000 00000000 00000000 -> 00000000
      |00ff00ff ffffffff 00000000 -> 000001fc
      |80ff7f01 00ff00ff 12345678 -> 002468ac
      |12345678 feedc0de 12345678 -> 00246960
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu RcpRz
  private val rcpRzSm89 = rows(
    """
      |00000000 -> 7f800000
      |b01d4a62 -> ced053ec
      |f03aa117 -> 8eaf93fa
      |c2b10000 -> bc392143
      |05dd9e05 -> 7913dbc7
      |c45b0341 -> ba959dec
      |c16231d6 -> bd90ddca
      |423c0000 -> 3cae4c41
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu RcpRm
  private val rcpRmSm89 = rows(
    """
      |00000000 -> 7f800000
      |b01d4a62 -> ced053ed
      |f03aa117 -> 8eaf93fb
      |c2b10000 -> bc392144
      |05dd9e05 -> 7913dbc7
      |c45b0341 -> ba959ded
      |c16231d6 -> bd90ddcb
      |423c0000 -> 3cae4c41
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu RcpRp
  private val rcpRpSm89 = rows(
    """
      |00000000 -> 7f800000
      |b01d4a62 -> ced053ec
      |f03aa117 -> 8eaf93fa
      |c2b10000 -> bc392143
      |05dd9e05 -> 7913dbc8
      |c45b0341 -> ba959dec
      |c16231d6 -> bd90ddca
      |423c0000 -> 3cae4c42
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu SqrtRz
  private val sqrtRzSm89 = rows(
    """
      |00000000 -> 00000000
      |b01d4a62 -> 7fffffff
      |f03aa117 -> 7fffffff
      |c2b10000 -> 7fffffff
      |05dd9e05 -> 22a86cd2
      |c45b0341 -> 7fffffff
      |c16231d6 -> 7fffffff
      |423c0000 -> 40db6185
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu DivRz
  private val divRzSm89 = rows(
    """
      |00000000 80000000 -> 7fffffff
      |b01d4a62 c2b00000 -> 2ce4c948
      |f03aa117 6f001855 -> c0ba7da3
      |c2b10000 99b4c684 -> 687aa74b
      |c9095e7b 461de97b -> c25eb257
      |42ced646 bf77a24c -> c2d5d323
      |c16231d5 60277325 -> a0ace7b2
      |423a0000 43f331f0 -> 3dc3cb1f
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu
  // RcpApprox
  private val rcpApproxSm89 = rows(
    """
      |461c4000 -> 38d1b718
      |bcd513e8 -> c219c8c3
      |44321758 -> 3ab7fee1
      |1d9bcb1e -> 6152546d
      |c2a50000 -> bc46980d
      |00000000 -> 7f800000
      |4b800000 -> 33800000
      |7e800000 -> 00800000
      |c2aa0000 -> bc40c0c1
      |71157e49 -> 0ddb319b
      |"""
  )

  // Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu SinApprox
  private val sinApproxSm89 = rows(
    """
      |00000001 -> 00000000
      |ce1ce6c9 -> 80000000
      |47c114c8 -> bf5f8327
      |c26231d6 -> 36c2c970
      |c1d40000 -> bf7ab725
      |00000000 -> 00000000
      |7fc00000 -> 7fffffff
      |ffffffff -> 7fffffff
      |bfaadc0e -> bf78e816
      |41eec2d5 -> bf800000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu
  // Lg2Approx
  private val lg2ApproxSm89 = rows(
    """
      |00800000 -> c2fbffff
      |6ffb2b0e -> 42c1f1ec
      |42292ad7 -> 40acdfb3
      |3a58f3ce -> c123d1fb
      |42580000 -> 40b82809
      |00000000 -> ff800000
      |4b000000 -> 41b80000
      |3fb504f3 -> 3effffff
      |42a70000 -> 40cc474e
      |4b000001 -> 41b80000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu
  // Ex2Approx
  private val ex2ApproxSm89 = rows(
    """
      |bf000000 -> 3f3504f2
      |3f78f767 -> 3ffb2bba
      |c25be958 -> 2401fa67
      |41d5a0d9 -> 4cd07283
      |c2990000 -> 193504f2
      |00000000 -> 3f800000
      |4effffff -> 7f800000
      |2f800000 -> 3f800000
      |b7a6fb6f -> 3f7fff19
      |c2640000 -> 23000000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu
  // RsqrtApprox
  private val rsqrtApproxSm89 = rows(
    """
      |00000001 -> 64b504f2
      |03f3fc1e -> 5d396c47
      |710bf0da -> 26ad1fcb
      |41a40000 -> 3e6229ec
      |42b10000 -> 3dd9b32f
      |00000000 -> 7f800000
      |4effffff -> 37b504f4
      |7e800000 -> 20000000
      |c3140000 -> 7fffffff
      |c2cd0000 -> 7fffffff
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: carry_chain
  // MadCcLoHi
  private val madCcLoHiSm89 = rows(
    """
      |55cb6861 fffffffe 91c90540 -> e632347e 55cb6860 00000000
      |f36fb28a 12345678 00000001 -> 9a5e0cb1 114f9f77 00000000
      |7fffffff ffff0000 00000001 -> 00010001 7fff7fff 00000000
      |80000000 12345678 12345678 -> 12345678 091a2b3c 00000000
      |ffffffff c81dea0f 80000001 -> b7e215f2 c81dea0e 00000000
      |ffffffff 00000002 80000000 -> 7ffffffe 00000002 00000000
      |00000002 ffff0000 ffff0000 -> fffd0000 00000002 00000000
      |7fffffff 80000000 ffffffff -> 7fffffff 40000000 00000000
      |d11d676c deadbeef bb369f41 -> 54685515 b5e56c86 00000000
      |0000ffff deadbeef 914f6f91 -> 7190b0a2 0000dead 00000000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: carry_chain
  // MadCcSigned
  private val madCcSignedSm89 = rows(
    """
      |1c21ca58 deadbeef 7c7ad606 -> e1ac0e2e fc569af1 00000000
      |5f3c3d25 7fffffff 8da39bff -> ae675eda 2f9e1e92 00000000
      |00000002 eef4bb9b ffffffff -> dde97735 00000000 00000001
      |12345678 cfef285f 80000001 -> 30f6d689 fc94fd2f 00000000
      |ffff0000 deadbeef ffffffff -> 4110ffff 00002153 00000000
      |00000000 7fffffff 12345678 -> 12345678 00000000 00000000
      |0000ffff 0000ffff 00000002 -> fffe0003 00000000 00000000
      |00000000 37aa7eb8 deadbeef -> deadbeef 00000000 00000000
      |3e9837b3 00000001 e81939d7 -> 26b1718a 00000001 00000000
      |598b7e2b 00000002 bb113ae1 -> 6e283737 00000001 00000000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: carry_chain
  // AddCarryIn
  private val addCarryInSm89 = rows(
    """
      |deadbeef 0000ffff 00000001 80000001 -> deaebeee 80000002 00000000
      |12345678 12345678 deadbeef 12345678 -> 2468acf0 f0e21567 00000000
      |deadbeef fffffffe 00000002 ffffffff -> deadbeed 00000002 00000001
      |00000001 00000001 deadbeef 0000ffff -> 00000002 deaebeee 00000000
      |361e46d7 b202c716 0000ffff d4f7bfb4 -> e8210ded d4f8bfb3 00000000
      |00000001 f528acaf 03c9ea3a 00000001 -> f528acb0 03c9ea3b 00000000
      |"""
  )

  // Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: int_selectors Prmt
  private val prmtSm89 = rows(
    """
      |00000000 00000000 00000000 -> 00000000
      |00008000 deadbeef 80000001 -> 00000080
      |ffffffff 55555555 0000ffff -> 00000000
      |deadbeef 00008000 fffffffe -> 00000000
      |10c1d870 f585d451 ccad9df7 -> fffffff5
      |b7ee0000 0000421d 0000b219 -> ffee0000
      |"""
  )

  // Titan X (Pascal) sm_61, driver 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89,
  // driver 615, CUDA 13.3: half_const Abs2
  private val abs2Sm61 = rows(
    """
      |0000 7e00 -> 7fff0000
      |7e00 4000 -> 40007fff
      |7c01 3bff -> 3bff7fff
      |2432 fd61 -> 7fff2432
      |fd87 eeb4 -> 6eb47fff
      |0000 0000 -> 00000000
      |3bff 3bff -> 3bff3bff
      |5640 4900 -> 49005640
      |9d98 4f2b -> 4f2b1d98
      |efc1 8e4a -> 0e4a6fc1
      |"""
  )

  // Titan X (Pascal) sm_61, driver 580, CUDA 12.8: half_const Neg2
  private val neg2Sm61 = rows(
    """
      |0000 7e00 -> 7fff8000
      |7e00 0400 -> 84007fff
      |7c01 7c00 -> fc007fff
      |0100 7c01 -> 7fff8100
      |db6a ffe9 -> 7fff5b6a
      |0000 0000 -> 80008000
      |3bff 0000 -> 8000bbff
      |4900 0000 -> 8000c900
      |8999 c460 -> 44600999
      |90c8 f57b -> 757b10c8
      |"""
  )

  // Titan X (Pascal) sm_61, driver 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89,
  // driver 615, CUDA 13.3: half_const Abs2
  private val abs2Sm70 = rows(
    """
      |0000 7e00 -> 7fff0000
      |7e00 4000 -> 40007fff
      |7c01 3bff -> 3bff7fff
      |2432 fd61 -> 7fff2432
      |fd87 eeb4 -> 6eb47fff
      |0000 0000 -> 00000000
      |3bff 3bff -> 3bff3bff
      |5640 4900 -> 49005640
      |9d98 4f2b -> 4f2b1d98
      |efc1 8e4a -> 0e4a6fc1
      |"""
  )

  // Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: half_const Neg2
  private val neg2Sm70 = rows(
    """
      |0000 7e00 -> 7fff8000
      |7e00 0400 -> 84007fff
      |7c01 7c00 -> fc007fff
      |0100 7c01 -> 7fff8100
      |db6a ffe9 -> 7fff5b6a
      |0000 0000 -> 80008000
      |3bff 0000 -> 8000bbff
      |4900 0000 -> 8000c900
      |c460 8999 -> 09994460
      |90c8 f57b -> 757b10c8
      |"""
  )

  // Titan X (Pascal) sm_61, driver 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89,
  // driver 615, CUDA 13.3: half_const Abs2
  private val abs2Sm89 = rows(
    """
      |0000 7e00 -> 7fff0000
      |7e00 4000 -> 40007fff
      |7c01 3bff -> 3bff7fff
      |2432 fd61 -> 7fff2432
      |fd87 eeb4 -> 6eb47fff
      |0000 0000 -> 00000000
      |3bff 3bff -> 3bff3bff
      |5640 4900 -> 49005640
      |9d98 4f2b -> 4f2b1d98
      |efc1 8e4a -> 0e4a6fc1
      |"""
  )

  // Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: half_const Neg2
  private val neg2Sm89 = rows(
    """
      |0000 7e00 -> 7fff8000
      |7e00 0400 -> 84007fff
      |7c01 7c00 -> fc007fff
      |0100 7c01 -> 7fff8100
      |db6a ffe9 -> 7fff5b6a
      |0000 0000 -> 80008000
      |3bff 0000 -> 8000bbff
      |4900 0000 -> 8000c900
      |c460 8999 -> 09994460
      |90c8 f57b -> 757b10c8
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50 and Titan X (Pascal) sm_61, driver 580, CUDA 12.8; Tesla
  // V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: local_generic spaces, identical
  private val spaces = rows(
    """
      |00 00 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |00 01 -> 00000001 00000004 00000002 00000002 00000002 00000004 00000001 00000001
      |"""
  )

  // the x86-hosted sm_35 and sm_50 runs drew the sm_61 inputs and printed the same lines; the GH200 sm_90 run (driver
  // 580.178.04, CUDA 12.6, 2026-10-19) printed the sm_89 lines
  private val Cards = Vec(
    "sm_35" -> 61,
    "sm_50" -> 61,
    "sm_61" -> 61,
    "sm_70" -> 70,
    "sm_89" -> 89,
    "sm_90" -> 89
  )

  private def byCard[A](sm61: A, sm70: A, sm89: A): Vec[(String, A)] =
    Cards.map((target, card) => target -> (if (card == 61) sm61 else if (card == 70) sm70 else sm89))

  private val f32 = ".reg .f32 %f<3>;"

  private def unary(instruction: String): String = s"mov.b32 %f0, %a0; $instruction %f1, %f0; mov.b32 %r0, %f1;"

  private def binary(instruction: String): String =
    s"mov.b32 %f0, %a0; mov.b32 %f1, %a1; $instruction %f2, %f0, %f1; mov.b32 %r0, %f2;"

  for {
    (form, body, tables) <- Vec(
      (
        "vadd2 with h10 and h11 selectors",
        "vadd2.u32.u32.u32 %r0, %a0.h10, %a1.h11, %a2;",
        byCard(vadd2H10H11Sm61, vadd2H10H11Sm70, vadd2H10H11Sm89)
      ),
      (
        "vadd4 with b3210 and b0123 selectors",
        "vadd4.u32.u32.u32 %r0, %a0.b3210, %a1.b0123, %a2;",
        byCard(vadd4B3210B0123Sm61, vadd4B3210B0123Sm70, vadd4B3210B0123Sm89)
      ),
      (
        "vset2.lt.add with h10 and h11 selectors",
        "vset2.u32.u32.lt.add %r0, %a0.h10, %a1.h11, %a2;",
        byCard(vset2LtAddSm61, vset2LtAddSm70, vset2LtAddSm89)
      ),
      (
        "vmad.shr7 of the b0 and b1 bytes",
        "vmad.u32.u32.u32.shr7 %r0, %a0.b0, %a1.b1, %a2;",
        byCard(vmadShr7Sm61, vmadShr7Sm70, vmadShr7Sm89)
      ),
      (
        "mad.lo.cc and madc.hi.cc carry",
        "{ .reg .u32 t0, t1, t2; mad.lo.cc.u32 t0, %a0, %a1, %a2; madc.hi.cc.u32 t1, %a0, %a1, 0; " +
          "addc.u32 t2, 0, 0; mov.u32 %r0, t0; mov.u32 %r1, t1; mov.u32 %r2, t2; }",
        byCard(madCcLoHiSm61, madCcLoHiSm70, madCcLoHiSm89)
      ),
      (
        "signed mad.lo.cc and madc.hi.cc carry",
        "{ .reg .u32 t0, t1, t2; mad.lo.cc.s32 t0, %a0, %a1, %a2; madc.hi.cc.s32 t1, %a0, %a1, 0; " +
          "addc.u32 t2, 0, 0; mov.u32 %r0, t0; mov.u32 %r1, t1; mov.u32 %r2, t2; }",
        byCard(madCcSignedSm61, madCcSignedSm70, madCcSignedSm89)
      ),
      (
        "add.cc and addc.cc carry",
        "{ .reg .u32 t0, t1, t2; add.cc.u32 t0, %a0, %a1; addc.cc.u32 t1, %a2, %a3; addc.u32 t2, 0, 0; " +
          "mov.u32 %r0, t0; mov.u32 %r1, t1; mov.u32 %r2, t2; }",
        byCard(addCarryInSm61, addCarryInSm70, addCarryInSm89)
      ),
      (
        "prmt with a 16-bit selector",
        "and.b32 %a2, %a2, 0xffff; prmt.b32 %r0, %a0, %a1, %a2;",
        byCard(prmtSm61, prmtSm70, prmtSm89)
      )
    )
    (target, table) <- tables
  }
    test(s"$target hardware: $form") {
      check(target, body, table)
    }

  for {
    (form, body, tables) <- Vec(
      ("rcp.rz.f32", unary("rcp.rz.f32"), byCard(rcpRzSm61, rcpRzSm70, rcpRzSm89)),
      ("rcp.rm.f32", unary("rcp.rm.f32"), byCard(rcpRmSm61, rcpRmSm70, rcpRmSm89)),
      ("rcp.rp.f32", unary("rcp.rp.f32"), byCard(rcpRpSm61, rcpRpSm70, rcpRpSm89)),
      ("sqrt.rz.f32", unary("sqrt.rz.f32"), byCard(sqrtRzSm61, sqrtRzSm70, sqrtRzSm89)),
      ("div.rz.f32", binary("div.rz.f32"), byCard(divRzSm61, divRzSm70, divRzSm89)),
      ("rcp.approx.f32", unary("rcp.approx.f32"), byCard(rcpApproxSm61, rcpApproxSm70, rcpApproxSm89)),
      ("sin.approx.f32", unary("sin.approx.f32"), byCard(sinApproxSm61, sinApproxSm70, sinApproxSm89)),
      ("lg2.approx.f32", unary("lg2.approx.f32"), byCard(lg2ApproxSm61, lg2ApproxSm70, lg2ApproxSm89)),
      ("ex2.approx.f32", unary("ex2.approx.f32"), byCard(ex2ApproxSm61, ex2ApproxSm70, ex2ApproxSm89)),
      ("rsqrt.approx.f32", unary("rsqrt.approx.f32"), byCard(rsqrtApproxSm61, rsqrtApproxSm70, rsqrtApproxSm89))
    )
    (target, table) <- tables
  }
    test(s"$target hardware: $form") {
      check(target, body, table, f32)
    }

  private val halves = ".reg .b16 %h<2>;"

  private def packed(instruction: String): String =
    s"cvt.u16.u32 %h0, %a0; cvt.u16.u32 %h1, %a1; mov.b32 %r0, {%h0, %h1}; $instruction %r0, %r0;"

  for {
    (instruction, tables) <- Vec(
      "abs.f16x2" -> Vec("sm_61" -> abs2Sm61, "sm_70" -> abs2Sm70, "sm_89" -> abs2Sm89, "sm_90" -> abs2Sm89),
      "neg.f16x2" -> Vec("sm_61" -> neg2Sm61, "sm_70" -> neg2Sm70, "sm_89" -> neg2Sm89, "sm_90" -> neg2Sm89)
    )
    (target, table) <- tables
  }
    test(s"$target hardware: $instruction canonicalises NaN lanes") {
      check(target, packed(instruction), table, halves)
    }

  private def bits(pointer: String, result: String): String =
    Vec("local" -> 1, "global" -> 2, "shared" -> 4, "const" -> 8).map((space, bit) =>
      s"isspacep.$space %p, $pointer; selp.u32 %x, $bit, 0, %p; or.b32 $result, $result, %x;"
    ).mkString(" ")

  for ((target, _) <- Cards) test(s"$target hardware: isspacep of local, shared, global and null generic pointers") {
    check(
      target,
      "mov.u64 %l, loc; cvta.local.u64 %l, %l; mov.u64 %s, sh; cvta.shared.u64 %s, %s; mov.u64 %z, 0; " +
        "and.b32 %x, %tid, 1; setp.ne.u32 %p, %x, 0; selp.b64 %sel, %s, %out, %p; add.u64 %l2, %l, 8; " +
        "add.u64 %l1, %l, 4; " + Vec("%l", "%s", "%out", "%z", "%out", "%sel", "%l2", "%l1").zipWithIndex.map(
          (pointer, index) => bits(pointer, s"%r$index")
        ).mkString(" "),
      spaces,
      ".reg .pred %p; .reg .b32 %x; .reg .b64 %l, %s, %z, %sel, %l1, %l2; .local .align 4 .b8 loc[16]; " +
        ".shared .align 4 .b8 sh[32];"
    )
  }

  // RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mad.hi.cc.u32 then addc, run on its own
  private val madHiCc = rows(
    """
      |00000000 00000000 00000000 -> 00000000 00000000
      |00000001 00000000 ffffffff -> ffffffff 00000000
      |00000002 00000000 00000002 -> 00000002 00000000
      |00000002 80000001 ffffffff -> 00000000 00000001
      |7fffffff 00000000 deadbeef -> deadbeef 00000000
      |7fffffff 0000ffff ffffffff -> 00007ffe 00000001
      |80000000 00000000 80000000 -> 80000000 00000000
      |80000000 80000001 deadbeef -> 1eadbeef 00000001
      |80000001 00000000 ffff0000 -> ffff0000 00000000
      |fffffffe 00000000 fffffffe -> fffffffe 00000000
      |fffffffe 80000001 ffff0000 -> 7ffeffff 00000001
      |ffffffff 00000000 00000001 -> 00000001 00000000
      |ffffffff 80000001 fffffffe -> 7ffffffe 00000001
      |12345678 00000000 12345678 -> 12345678 00000000
      |"""
  )

  // RTX 4060 Ti sm_89, driver 615, CUDA 13.3: a generic pointer to local offset 0, tested for null and read
  private val localBase = rows(
    """
      |00000000 00000000 -> 00000001 00000000
      |80000001 80000001 -> 00000001 80000001
      |0000ffff 00000001 -> 00000001 0000ffff
      |00000001 80000001 -> 00000001 00000001
      |"""
  )

  test("sm_89 hardware: mad.hi.cc carries out of the high half plus the addend") {
    check(
      "sm_89",
      "{ .reg .u32 t0, t1; mad.hi.cc.u32 t0, %a0, %a1, %a2; addc.u32 t1, 0, 0; mov.u32 %r0, t0; mov.u32 %r1, t1; }",
      madHiCc
    )
  }

  test("sm_89 hardware: a generic pointer to local offset 0 is not null") {
    check(
      "sm_89",
      "st.local.u32 [frame], %a0; st.local.u32 [frame+4], %a1; cvta.local.u64 %g, frame; setp.ne.u64 %q, %g, 0; " +
        "selp.u32 %r0, 1, 0, %q; ld.u32 %r1, [%g];",
      localBase,
      ".local .align 4 .b8 frame[8]; .reg .u64 %g; .reg .pred %q;"
    )
  }
}
