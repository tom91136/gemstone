package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.fixtures.HardwareGoldenFixtures.*

class HardwareGoldenSuite extends munit.FunSuite {
  import hardwareGolden.*

  private val Cuda   = Vec(35, 50, 61, 70, 89, 90)
  private val Legacy = Vec(35, 50, 61)

  private def mask(sm: Int, register: String, value: Int): String =
    if (sm < 70) f"LOP32I.AND $register, $register, 0x$value%x"
    else f"LOP3.LUT $register, $register, 0x$value%x, RZ, 0xc0, !PT"

  // Titan X (Pascal) sm_61, driver 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89,
  // driver 615, CUDA 13.3: half_const IsInf
  private val isInfSm70Rows = rows(
    """
      |0000 -> 0000
      |3c8f -> 0000
      |7db5 -> 0000
      |bedb -> 0000
      |1bfc -> 0000
      |5d22 -> 0000
      |9e48 -> 0000
      |df6e -> 0000
      |"""
  )

  // Titan X (Pascal) sm_61, driver 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89,
  // driver 615, CUDA 13.3: half_const IsInf
  // GH200 sm_90, driver 580.178.04, CUDA 12.6, 2026-10-19: the same rows
  private val isInfSm89Rows = rows(
    """
      |0000 -> 0000
      |3c8f -> 0000
      |7db5 -> 0000
      |bedb -> 0000
      |1bfc -> 0000
      |5d22 -> 0000
      |9e48 -> 0000
      |df6e -> 0000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3; GH200 sm_90,
  // driver 580.178.04, CUDA 12.6, 2026-10-19: int_selectors PrmtRc8
  private val prmtRc8 = rows(
    """
      |00000000 00000000 00000000 -> 00000000
      |80000000 00010000 ffffffff -> 80808080
      |7f7f7f7f 00ff00ff 00000001 -> 7f7f7f7f
      |adfb0000 0000e0e2 00008c04 -> 00000000
      |00007fff 00000080 9abcdef0 -> ffffffff
      |f0f0f0f0 fffffffe 0000ffff -> f0f0f0f0
      |cb8d0000 0000d0c7 0000b518 -> 00000000
      |912c0000 0000d881 00009619 -> 00000000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3:
  // int_selectors PrmtF4e
  private val prmtF4e = rows(
    """
      |00000000 00000000 00000000 -> 00000000
      |80000000 00010000 ffffffff -> 01000080
      |7f7f7f7f 00ff00ff 00000001 -> ff7f7f7f
      |adfb0000 0000e0e2 00008c04 -> adfb0000
      |00007fff 00000080 9abcdef0 -> 00007fff
      |f0f0f0f0 fffffffe 0000ffff -> fffffef0
      |cb8d0000 0000d0c7 0000b518 -> cb8d0000
      |912c0000 0000d881 00009619 -> 81912c00
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3:
  // int_selectors PrmtB4e
  private val prmtB4e = rows(
    """
      |00000000 00000000 00000000 -> 00000000
      |80000000 00010000 ffffffff -> 00000080
      |7f7f7f7f 00ff00ff 00000001 -> ff007f7f
      |adfb0000 0000e0e2 00008c04 -> e0000000
      |00007fff 00000080 9abcdef0 -> 000000ff
      |f0f0f0f0 fffffffe 0000ffff -> f0f0f0f0
      |cb8d0000 0000d0c7 0000b518 -> d0000000
      |912c0000 0000d881 00009619 -> 00000000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3:
  // int_selectors PrmtEcl
  private val prmtEcl = rows(
    """
      |00000000 00000000 00000000 -> 00000000
      |80000000 00010000 ffffffff -> 80808080
      |7f7f7f7f 00ff00ff 00000001 -> 7f7f7f7f
      |adfb0000 0000e0e2 00008c04 -> adfb0000
      |00007fff 00000080 9abcdef0 -> 00007fff
      |f0f0f0f0 fffffffe 0000ffff -> f0f0f0f0
      |cb8d0000 0000d0c7 0000b518 -> cb8d0000
      |912c0000 0000d881 00009619 -> 912c0000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3:
  // int_selectors PrmtEcr
  private val prmtEcr = rows(
    """
      |00000000 00000000 00000000 -> 00000000
      |80000000 00010000 ffffffff -> 80000000
      |7f7f7f7f 00ff00ff 00000001 -> 7f7f7f7f
      |adfb0000 0000e0e2 00008c04 -> 00000000
      |00007fff 00000080 9abcdef0 -> ffffffff
      |f0f0f0f0 fffffffe 0000ffff -> f0f0f0f0
      |cb8d0000 0000d0c7 0000b518 -> 00000000
      |912c0000 0000d881 00009619 -> 00000000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3:
  // int_selectors PrmtRc16
  private val prmtRc16 = rows(
    """
      |00000000 00000000 00000000 -> 00000000
      |80000000 00010000 ffffffff -> 80008000
      |7f7f7f7f 00ff00ff 00000001 -> 7f7f7f7f
      |adfb0000 0000e0e2 00008c04 -> 00000000
      |00007fff 00000080 9abcdef0 -> 7fff7fff
      |f0f0f0f0 fffffffe 0000ffff -> f0f0f0f0
      |cb8d0000 0000d0c7 0000b518 -> 00000000
      |912c0000 0000d881 00009619 -> 912c912c
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3:
  // int_selectors Prmt
  private val prmt = rows(
    """
      |00000000 00000000 00000000 -> 00000000
      |00007fff 00000000 f0f0f0f0 -> 00ff00ff
      |80000000 00000000 00007fff -> 00000000
      |f0f0f0f0 00000000 7f7f7f7f -> 00000000
      |7f7f7f7f 00000000 80000000 -> 7f7f7f7f
      |7b2f0000 00005e7d 0000021c -> 002f0000
      |43340000 0000e48a 00008703 -> 00000043
      |cd560000 00001d14 00009d19 -> 00000000
      |"""
  )

  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: carry_chain
  // AddCarryIn
  private val addCarryInSm70 = rows(
    """
      |b73dd78c 12345678 7fffffff 00000000 -> c9722e04 7fffffff 00000000
      |00000002 ffffffff 905a35d5 5098c485 -> 00000001 e0f2fa5b 00000000
      |f387c252 00000000 ffffffff 00000000 -> f387c252 ffffffff 00000000
      |4eb6e3e6 deadbeef 0000ffff 311ed653 -> 2d64a2d5 311fd653 00000000
      |a5f5840a 00000001 12345678 7fffffff -> a5f5840b 92345677 00000000
      |28d9abda e6d7819b 00000001 deadbeef -> 0fb12d75 deadbef1 00000000
      |0617a263 00000002 00000001 6b748074 -> 0617a265 6b748075 00000000
      |12345678 0cbe028f 4d122f26 fabad344 -> 1ef25907 47cd026a 00000001
      |a4b043b4 fffffffe 7fffffff 79b53f02 -> a4b043b2 f9b53f02 00000000
      |7fffffff fffffffe deadbeef 00000001 -> 7ffffffd deadbef1 00000000
      |7336f871 ffffffff 9f134ed3 80000000 -> 7336f870 1f134ed4 00000001
      |5dcd4b82 7fffffff fffffffe ffff0000 -> ddcd4b81 fffefffe 00000001
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: carry_chain
  // AddCarryIn
  // GH200 sm_90, driver 580.178.04, CUDA 12.6, 2026-10-19: the same rows
  private val addCarryInSm89 = rows(
    """
      |b73dd78c 12345678 7fffffff 00000000 -> c9722e04 7fffffff 00000000
      |00000002 ffffffff 905a35d5 5098c485 -> 00000001 e0f2fa5b 00000000
      |f387c252 00000000 ffffffff 00000000 -> f387c252 ffffffff 00000000
      |4eb6e3e6 deadbeef 0000ffff 311ed653 -> 2d64a2d5 311fd653 00000000
      |a5f5840a 00000001 12345678 7fffffff -> a5f5840b 92345677 00000000
      |28d9abda e6d7819b 00000001 deadbeef -> 0fb12d75 deadbef1 00000000
      |0617a263 00000002 00000001 6b748074 -> 0617a265 6b748075 00000000
      |12345678 0cbe028f 4d122f26 fabad344 -> 1ef25907 47cd026a 00000001
      |a4b043b4 fffffffe 7fffffff 79b53f02 -> a4b043b2 f9b53f02 00000000
      |7fffffff fffffffe deadbeef 00000001 -> 7ffffffd deadbef1 00000000
      |7336f871 ffffffff 9f134ed3 80000000 -> 7336f870 1f134ed4 00000001
      |5dcd4b82 7fffffff fffffffe ffff0000 -> ddcd4b81 fffefffe 00000001
      |"""
  )
  // Titan X (Pascal) sm_61, driver 580, CUDA 12.8: half_f32 hadd_rzop_w
  private val haddRzOperandSm61 = rows(
    """
      |0000 0000 -> 00000000
      |3bff ff800000 -> ff800000
      |7fff bf800000 -> 7fffffff
      |7800 387fc000 -> 47000000
      |a846 6a715e74 -> 477fe000
      |52da 3e5a14ee -> 425c2000
      |83ff 477fe000 -> 477fe000
      |fbff 41200000 -> c77fe000
      |3e00 3fb504f3 -> 403a8000
      |f346 aa57748d -> c668c000
      |75b6 c0edee9a -> 46b6c000
      |066d 44676255 -> 44676000
      |"""
  )
  // Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: half_f32 hadd_rzop_w
  private val haddRzOperandSm70 = rows(
    """
      |0000 0000 -> 00000000
      |3bff ff800000 -> ff800000
      |7fff bf800000 -> 7fffffff
      |7800 387fc000 -> 47000000
      |5e74 8eeaa846 -> 43ce8000
      |14ee 954d52da -> 3a9dc000
      |83ff 477fe000 -> 477fe000
      |fbff 41200000 -> c77fe000
      |3e00 3fb504f3 -> 403a8000
      |748d 38f3f346 -> 4691a000
      |ee9a 9d8275b6 -> c5d34000
      |6255 b156066d -> 444aa000
      |"""
  )
  // Titan X (Pascal) sm_61, driver 580, CUDA 12.8: half_f32 hadd_w
  private val haddWideSm61 = rows(
    """
      |0001 bc00 -> bf800000
      |6400 83ff -> 44800000
      |5a26 4bb7 -> 43542000
      |9878 81df -> bb10e000
      |2cb1 eed3 -> c5da6000
      |f607 d5f7 -> c6c1a000
      |0000 0000 -> 00000000
      |3c00 bc00 -> 00000000
      |fc00 fbff -> ff800000
      |4900 7e00 -> 7fffffff
      |3c86 34a4 -> 3fb5e000
      |f58f 75d8 -> 44920000
      |"""
  )
  // Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: half_f32 hadd_w
  private val haddWideSm70 = rows(
    """
      |0001 bc00 -> bf800000
      |6400 83ff -> 44800000
      |4bb7 5a26 -> 43542000
      |81df 9878 -> bb10e000
      |eed3 2cb1 -> c5da6000
      |d5f7 f607 -> c6c1a000
      |0000 0000 -> 00000000
      |3c00 bc00 -> 00000000
      |fc00 fbff -> ff800000
      |4900 7e00 -> 7fffffff
      |34a4 3c86 -> 3fb5e000
      |75d8 f58f -> 44920000
      |"""
  )
  // Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: half_f32 hadd_w
  private val haddWideSm89 = rows(
    """
      |0000 0000 -> 00000000
      |bc00 bc00 -> c0000000
      |7c00 7c00 -> 7f800000
      |4248 4248 -> 40c90000
      |f5ee e415 -> c6c5e000
      |6a17 87ff -> 4542e000
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8: simd_intrinsics vsubss2
  private val vsubss2 = rows(
    """
      |00000000 00008000 -> 00007fff
      |00000100 00008000 -> 00007fff
      |01000000 00008000 -> 01007fff
      |55555555 00008000 -> 55557fff
      |12345678 00008000 -> 12347fff
      |81818181 00008000 -> 81810181
      |00000000 00000000 -> 00000000
      |00008000 01010101 -> feff8000
      |ffffffff aaaaaaaa -> 55555555
      |deadbeef 0000ffff -> deadbef0
      |77d468c0 922e3853 -> 7fff306d
      |4448c03f 2cd5db13 -> 1773e52c
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu
  // RcpApprox
  private val rcpApproxLegacy = rows(
    """
      |461c4000 -> 38d1b718
      |e44cc7b0 -> 9aa003fe
      |3fa03778 -> 3f4c85e6
      |df2cf73c -> 9fbd72a9
      |4096cbe5 -> 3e594cad
      |c2260000 -> bcc565c9
      |00800000 -> 7e800000
      |3fc90fdb -> 3f22f983
      |477fefff -> 37800801
      |bf800000 -> bf800000
      |c2a00000 -> bc4ccccd
      |70bba4e9 -> 0e2ea0de
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu
  // Lg2Approx
  private val lg2ApproxLegacy = rows(
    """
      |00800000 -> c2fbffff
      |0d30a236 -> c2c9121d
      |3f2c59e3 -> bf121f7f
      |5e98f5a3 -> 4279072c
      |41afede1 -> 408eaee8
      |429c0000 -> 40c92203
      |80800000 -> 7fffffff
      |41200000 -> 40549a78
      |47800000 -> 41800000
      |bf800000 -> 7fffffff
      |42b00000 -> 40ceb3aa
      |4f000001 -> 41f80000
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu
  // RsqrtApprox
  private val rsqrtApproxLegacy = rows(
    """
      |00800001 -> 5efffffe
      |2418b29f -> 4d25bc1b
      |47927d6a -> 3b6f4c94
      |42108364 -> 3e2a5d04
      |42280000 -> 3e1e01b2
      |42d60000 -> 3dc5fcd5
      |00800000 -> 5f000000
      |40c90fdb -> 3ecc4229
      |477ff000 -> 3b800400
      |c30f8000 -> 7fffffff
      |c2c40000 -> 7fffffff
      |40b89cb9 -> 3ed52a19
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8: mufu SinApprox
  private val sinApproxLegacy = rows(
    """
      |00800000 -> 00000000
      |6ffb2b0e -> 00000000
      |c1cccb09 -> bee63b26
      |707444e9 -> 00000000
      |42490fdb -> b516cd80
      |41600000 -> 3f7d986f
      |00000000 -> 00000000
      |807fffff -> 80000000
      |7fc00001 -> 7fffffff
      |7fa00000 -> 7fffffff
      |bfaadc0e -> bf78e816
      |c24f5859 -> bf800000
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu
  // Ex2Approx
  private val ex2ApproxLegacy = rows(
    """
      |bf000000 -> 3f3504f2
      |40c37428 -> 4289f16e
      |c0a50109 -> 3ce5b3df
      |c1c90fdc -> 32e97f10
      |42687a55 -> 5c8b0ced
      |c27a0000 -> 203504f2
      |00000000 -> 3f800000
      |41200000 -> 44800000
      |7fa00000 -> 7fffffff
      |44d646f6 -> 7f800000
      |43090000 -> 7f800000
      |845d9fc6 -> 3f800000
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu
  // RcpApprox
  private val rcpApproxSm70 = rows(
    """
      |461c4000 -> 38d1b718
      |e44cc7b0 -> 9aa003fe
      |3fa03778 -> 3f4c85e6
      |df2cf73c -> 9fbd72a9
      |4096cbe5 -> 3e594cad
      |c2260000 -> bcc565c9
      |00800000 -> 7e800000
      |3fc90fdb -> 3f22f983
      |477fefff -> 37800801
      |bf800000 -> bf800000
      |c2a00000 -> bc4ccccd
      |70bba4e9 -> 0e2ea0de
      |"""
  )
  // Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu SinApprox
  private val sinApproxSm70 = rows(
    """
      |3f800000 -> 3f576aa3
      |845d9fc6 -> 80000000
      |40978e4f -> bf7fed8b
      |3a79429b -> 3a7939cd
      |4229a560 -> bf7fffff
      |40600000 -> beb399ce
      |00000000 -> 00000000
      |3f7fffff -> 3f576aa3
      |ff800000 -> 7fffffff
      |477ff000 -> bf5f8327
      |bdf8e0d1 -> bdf8441d
      |444b937d -> bf1736f3
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu
  // RcpApprox
  private val rcpApproxSm89 = rows(
    """
      |461c4000 -> 38d1b718
      |e44cc7b0 -> 9aa003fe
      |3fa03778 -> 3f4c85e6
      |df2cf73c -> 9fbd72a9
      |4096cbe5 -> 3e594cad
      |c2260000 -> bcc565c9
      |00800000 -> 7e800000
      |3fc90fdb -> 3f22f983
      |477fefff -> 37800801
      |bf800000 -> bf800000
      |c2a00000 -> bc4ccccd
      |70bba4e9 -> 0e2ea0de
      |"""
  )
  // Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu SinApprox
  private val sinApproxSm89 = rows(
    """
      |3f800000 -> 3f576aa3
      |845d9fc6 -> 80000000
      |40978e4f -> bf7fed8b
      |3a79429b -> 3a7939cd
      |4229a560 -> bf7fffff
      |40600000 -> beb399ce
      |00000000 -> 00000000
      |3f7fffff -> 3f576aa3
      |ff800000 -> 7fffffff
      |477ff000 -> bf5f8327
      |bdf8e0d1 -> bdf8441d
      |444b937d -> bf1736f3
      |"""
  )

  // Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu DivRnFtz
  private val divRnFtzSm70Rows = rows(
    """
      |00000000 80000000 -> 7fffffff
      |c29f0000 c20a0000 -> 40137a6f
      |c1cccb09 42870000 -> bec22c99
      |f03aa117 6f001855 -> c0ba7da4
      |4f000001 00216a85 -> 7f800000
      |3fc00000 406fce11 -> 3eccf772
      |b37dab0a 6083057f -> 9277d17d
      |b01d4a62 c2b00000 -> 2ce4c949
      |296a0dc3 c1b80000 -> a722d1ec
      |c2750b52 b99766e7 -> 484f2b0a
      |c2b10000 99b4c684 -> 687aa74c
      |42c50000 4ceaa61a -> 3556ece9
      |"""
  )
  // Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu DivRnFtz
  private val divRnFtzSm89Rows = rows(
    """
      |00000000 80000000 -> 7fffffff
      |c29f0000 c20a0000 -> 40137a6f
      |c1cccb09 42870000 -> bec22c99
      |f03aa117 6f001855 -> c0ba7da4
      |4f000001 00216a85 -> 7f800000
      |3fc00000 406fce11 -> 3eccf772
      |b37dab0a 6083057f -> 9277d17d
      |b01d4a62 c2b00000 -> 2ce4c949
      |296a0dc3 c1b80000 -> a722d1ec
      |c2750b52 b99766e7 -> 484f2b0a
      |c2b10000 99b4c684 -> 687aa74c
      |42c50000 4ceaa61a -> 3556ece9
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8: mufu DivRnFtz
  private val divRnFtzSm35Rows = rows(
    """
      |00000000 80000000 -> 7fffffff
      |c20a0000 c29f0000 -> 3ede304d
      |c1cccb09 42870000 -> bec22c99
      |f03aa117 6f001855 -> c0ba7da4
      |00216a85 4f000001 -> 00000000
      |3fc00000 406fce11 -> 3eccf772
      |b37dab0a 6083057f -> 9277d17d
      |b01d4a62 c2b00000 -> 2ce4c949
      |c1b80000 296a0dc3 -> d7c940bf
      |c2750b52 b99766e7 -> 484f2b0a
      |c2b10000 99b4c684 -> 687aa74c
      |4ceaa61a 42c50000 -> 49987652
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8: mufu Atan2f
  private val atan2fSm50Rows = rows(
    """
      |00000000 80000000 -> 40490fdb
      |c20a0000 c29f0000 -> c02edba3
      |c1cccb09 42870000 -> beb99815
      |f03aa117 6f001855 -> bfb34f6a
      |00216a85 4f000001 -> 00000000
      |3fc00000 406fce11 -> 3ec2f67e
      |b37dab0a 6083057f -> 9277d17d
      |b01d4a62 c2b00000 -> c0490fdb
      |c1b80000 296a0dc3 -> bfc90fdb
      |c2750b52 b99766e7 -> bfc91003
      |c2b10000 99b4c684 -> bfc90fdb
      |4ceaa61a 42c50000 -> 3fc90fd4
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8: mufu Atan2f
  private val atan2fSm61Rows = rows(
    """
      |00000000 80000000 -> 40490fdb
      |c20a0000 c29f0000 -> c02edba3
      |c1cccb09 42870000 -> beb99815
      |f03aa117 6f001855 -> bfb34f6a
      |00216a85 4f000001 -> 00000000
      |3fc00000 406fce11 -> 3ec2f67e
      |b37dab0a 6083057f -> 9277d17d
      |b01d4a62 c2b00000 -> c0490fdb
      |c1b80000 296a0dc3 -> bfc90fdb
      |c2750b52 b99766e7 -> bfc91003
      |c2b10000 99b4c684 -> bfc90fdb
      |4ceaa61a 42c50000 -> 3fc90fd4
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu Coshf
  private val coshfSm35Rows = rows(
    """
      |00000000 -> 3f800000
      |f23d21f6 -> 7f800000
      |427831ff -> 6bb73b82
      |f03aa117 -> 7f800000
      |4203f269 -> 56c0a4be
      |40000000 -> 4070c7d1
      |46353067 -> 7f800000
      |b01d4a62 -> 3f800000
      |469c9ef5 -> 7f800000
      |c2750b53 -> 6b26b0b3
      |c2b10000 -> 7eccdcc4
      |42b90000 -> 7f800000
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu Coshf
  private val coshfSm50Rows = rows(
    """
      |00000000 -> 3f800000
      |f23d21f6 -> 7f800000
      |427831ff -> 6bb73b82
      |f03aa117 -> 7f800000
      |4203f269 -> 56c0a4be
      |40000000 -> 4070c7d1
      |46353067 -> 7f800000
      |b01d4a62 -> 3f800000
      |469c9ef5 -> 7f800000
      |c2750b53 -> 6b26b0b3
      |c2b10000 -> 7eccdcc4
      |42b90000 -> 7f800000
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: mufu Coshf
  private val coshfSm61Rows = rows(
    """
      |00000000 -> 3f800000
      |f23d21f6 -> 7f800000
      |427831ff -> 6bb73b82
      |f03aa117 -> 7f800000
      |4203f269 -> 56c0a4be
      |40000000 -> 4070c7d1
      |46353067 -> 7f800000
      |b01d4a62 -> 3f800000
      |469c9ef5 -> 7f800000
      |c2750b53 -> 6b26b0b3
      |c2b10000 -> 7eccdcc4
      |42b90000 -> 7f800000
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8: video_ptx vadd2_uuu_h10_h11
  private val vadd2Sm35Rows = rows(
    """
      |00000000 00000000 00000000 -> 00000000
      |80000000 80ff7f01 ffffffff -> 00008000
      |00ff00ff 00000000 feedc0de -> 01fe01fe
      |feedc0de 80ff7f01 feedc0de -> fddabfcb
      |57005648 c9d314d5 db426572 -> ae00ad48
      |e42259b3 d8a971d0 6b23684e -> c8443dd5
      |ffffffff 80000000 feedc0de -> fffefffe
      |7fffffff 12345678 feedc0de -> fffe7ffe
      |12345678 80000000 00000000 -> 246868ac
      |30450f23 74213028 7d8fda3b -> 608a3f68
      |bd172f8d 18bbffd3 e18c0d45 -> 7a2eeca4
      |49026aec 4b7334ef 1206c10b -> 9204b3ee
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8: wide_mul Mul64HiS
  private val mul64HiSSm35Rows = rows(
    """
      |00000000 00010000 00010000 81818181 -> 81810001 ffff8181
      |00000000 80000000 80000000 00007fff -> 40000000 ffffc000
      |01000000 80000000 00000000 01000000 -> 00010000 ff800000
      |00000000 00000000 00000000 00000000 -> 00000000 00000000
      |00007fff 80000001 00000001 00010000 -> 0000ffff ffff8000
      |80000000 81818181 00000002 0f0f0f0f -> 5a43ad15 f88f25bc
      |f0f0f0f0 00008000 00000001 12345678 -> 3c5e3343 0000091a
      |7f7f7f7f f0f0f0f0 00000000 7f7f7f7f -> e59e570f f880078e
      |e8cb925e ce0bde0a 87df6088 809f715a -> 070c5970 18daf43b
      |718c7d04 132ffd49 65079d1c 95288759 -> e2d75020 f7fdfac6
      |47ee9d2e e0dca183 0fca701a 45fba4ee -> 20c5675e f77cdbcb
      |04756713 2b59d20d 57e24dfb 55064204 -> 23399236 0e65e207
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3:
  // local_generic spaces
  private val spacesSm35Rows = rows(
    """
      |00 00 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |00 25 -> 00000001 00000004 00000002 00000002 00000002 00000004 00000001 00000001
      |00 51 -> 00000001 00000004 00000002 00000002 00000002 00000004 00000001 00000001
      |01 12 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |01 38 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |00 12 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |00 38 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |01 00 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |01 25 -> 00000001 00000004 00000002 00000002 00000002 00000004 00000001 00000001
      |01 51 -> 00000001 00000004 00000002 00000002 00000002 00000004 00000001 00000001
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3:
  // local_generic cvta
  private val cvtaSm35Rows = rows(
    """
      |00 00 -> 00000001 00000000 00000000 abc00000 00000001 00000001 00000000 00000000
      |00 25 -> 00000001 00000034 000009d1 abc00019 00000001 00000001 00000000 00000000
      |00 51 -> 00000001 0000003c 000013fb abc00033 00000001 00000001 00000000 00000000
      |01 12 -> 00000001 00000030 000004bc abc0000c 00000001 00000001 00000000 00000000
      |01 38 -> 00000001 00000038 00000ee6 abc00026 00000001 00000001 00000000 00000000
      |00 12 -> 00000001 00000030 000004bc abc0000c 00000001 00000001 00000000 00000000
      |00 38 -> 00000001 00000038 00000ee6 abc00026 00000001 00000001 00000000 00000000
      |01 00 -> 00000001 00000000 00000000 abc00000 00000001 00000001 00000000 00000000
      |01 25 -> 00000001 00000034 000009d1 abc00019 00000001 00000001 00000000 00000000
      |01 51 -> 00000001 0000003c 000013fb abc00033 00000001 00000001 00000000 00000000
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3:
  // local_generic spaces
  private val spacesSm50Rows = rows(
    """
      |00 00 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |00 25 -> 00000001 00000004 00000002 00000002 00000002 00000004 00000001 00000001
      |00 51 -> 00000001 00000004 00000002 00000002 00000002 00000004 00000001 00000001
      |01 12 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |01 38 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |00 12 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |00 38 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |01 00 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |01 25 -> 00000001 00000004 00000002 00000002 00000002 00000004 00000001 00000001
      |01 51 -> 00000001 00000004 00000002 00000002 00000002 00000004 00000001 00000001
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3:
  // local_generic cvta
  private val cvtaSm50Rows = rows(
    """
      |00 00 -> 00000001 00000000 00000000 abc00000 00000001 00000001 00000000 00000000
      |00 25 -> 00000001 00000034 000009d1 abc00019 00000001 00000001 00000000 00000000
      |00 51 -> 00000001 0000003c 000013fb abc00033 00000001 00000001 00000000 00000000
      |01 12 -> 00000001 00000030 000004bc abc0000c 00000001 00000001 00000000 00000000
      |01 38 -> 00000001 00000038 00000ee6 abc00026 00000001 00000001 00000000 00000000
      |00 12 -> 00000001 00000030 000004bc abc0000c 00000001 00000001 00000000 00000000
      |00 38 -> 00000001 00000038 00000ee6 abc00026 00000001 00000001 00000000 00000000
      |01 00 -> 00000001 00000000 00000000 abc00000 00000001 00000001 00000000 00000000
      |01 25 -> 00000001 00000034 000009d1 abc00019 00000001 00000001 00000000 00000000
      |01 51 -> 00000001 0000003c 000013fb abc00033 00000001 00000001 00000000 00000000
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3:
  // local_generic spaces
  private val spacesSm61Rows = rows(
    """
      |00 00 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |00 25 -> 00000001 00000004 00000002 00000002 00000002 00000004 00000001 00000001
      |00 51 -> 00000001 00000004 00000002 00000002 00000002 00000004 00000001 00000001
      |01 12 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |01 38 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |00 12 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |00 38 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |01 00 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |01 25 -> 00000001 00000004 00000002 00000002 00000002 00000004 00000001 00000001
      |01 51 -> 00000001 00000004 00000002 00000002 00000002 00000004 00000001 00000001
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3:
  // local_generic cvta
  private val cvtaSm61Rows = rows(
    """
      |00 00 -> 00000001 00000000 00000000 abc00000 00000001 00000001 00000000 00000000
      |00 25 -> 00000001 00000034 000009d1 abc00019 00000001 00000001 00000000 00000000
      |00 51 -> 00000001 0000003c 000013fb abc00033 00000001 00000001 00000000 00000000
      |01 12 -> 00000001 00000030 000004bc abc0000c 00000001 00000001 00000000 00000000
      |01 38 -> 00000001 00000038 00000ee6 abc00026 00000001 00000001 00000000 00000000
      |00 12 -> 00000001 00000030 000004bc abc0000c 00000001 00000001 00000000 00000000
      |00 38 -> 00000001 00000038 00000ee6 abc00026 00000001 00000001 00000000 00000000
      |01 00 -> 00000001 00000000 00000000 abc00000 00000001 00000001 00000000 00000000
      |01 25 -> 00000001 00000034 000009d1 abc00019 00000001 00000001 00000000 00000000
      |01 51 -> 00000001 0000003c 000013fb abc00033 00000001 00000001 00000000 00000000
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3:
  // local_generic spaces
  private val spacesSm70Rows = rows(
    """
      |00 00 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |00 25 -> 00000001 00000004 00000002 00000002 00000002 00000004 00000001 00000001
      |00 51 -> 00000001 00000004 00000002 00000002 00000002 00000004 00000001 00000001
      |01 12 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |01 38 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |00 12 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |00 38 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |01 00 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |01 25 -> 00000001 00000004 00000002 00000002 00000002 00000004 00000001 00000001
      |01 51 -> 00000001 00000004 00000002 00000002 00000002 00000004 00000001 00000001
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3:
  // local_generic cvta
  private val cvtaSm70Rows = rows(
    """
      |00 00 -> 00000001 00000000 00000000 abc00000 00000001 00000001 00000000 00000000
      |00 25 -> 00000001 00000034 000009d1 abc00019 00000001 00000001 00000000 00000000
      |00 51 -> 00000001 0000003c 000013fb abc00033 00000001 00000001 00000000 00000000
      |01 12 -> 00000001 00000030 000004bc abc0000c 00000001 00000001 00000000 00000000
      |01 38 -> 00000001 00000038 00000ee6 abc00026 00000001 00000001 00000000 00000000
      |00 12 -> 00000001 00000030 000004bc abc0000c 00000001 00000001 00000000 00000000
      |00 38 -> 00000001 00000038 00000ee6 abc00026 00000001 00000001 00000000 00000000
      |01 00 -> 00000001 00000000 00000000 abc00000 00000001 00000001 00000000 00000000
      |01 25 -> 00000001 00000034 000009d1 abc00019 00000001 00000001 00000000 00000000
      |01 51 -> 00000001 0000003c 000013fb abc00033 00000001 00000001 00000000 00000000
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3:
  // local_generic spaces
  private val spacesSm89Rows = rows(
    """
      |00 00 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |00 25 -> 00000001 00000004 00000002 00000002 00000002 00000004 00000001 00000001
      |00 51 -> 00000001 00000004 00000002 00000002 00000002 00000004 00000001 00000001
      |01 12 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |01 38 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |00 12 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |00 38 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |01 00 -> 00000001 00000004 00000002 00000002 00000002 00000002 00000001 00000001
      |01 25 -> 00000001 00000004 00000002 00000002 00000002 00000004 00000001 00000001
      |01 51 -> 00000001 00000004 00000002 00000002 00000002 00000004 00000001 00000001
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3:
  // local_generic cvta
  private val cvtaSm89Rows = rows(
    """
      |00 00 -> 00000001 00000000 00000000 abc00000 00000001 00000001 00000000 00000000
      |00 25 -> 00000001 00000034 000009d1 abc00019 00000001 00000001 00000000 00000000
      |00 51 -> 00000001 0000003c 000013fb abc00033 00000001 00000001 00000000 00000000
      |01 12 -> 00000001 00000030 000004bc abc0000c 00000001 00000001 00000000 00000000
      |01 38 -> 00000001 00000038 00000ee6 abc00026 00000001 00000001 00000000 00000000
      |00 12 -> 00000001 00000030 000004bc abc0000c 00000001 00000001 00000000 00000000
      |00 38 -> 00000001 00000038 00000ee6 abc00026 00000001 00000001 00000000 00000000
      |01 00 -> 00000001 00000000 00000000 abc00000 00000001 00000001 00000000 00000000
      |01 25 -> 00000001 00000034 000009d1 abc00019 00000001 00000001 00000000 00000000
      |01 51 -> 00000001 0000003c 000013fb abc00033 00000001 00000001 00000000 00000000
      |"""
  )
  // Titan X (Pascal) sm_61, driver 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89,
  // driver 615, CUDA 13.3: half_const MulC03
  private val mulC03Sm61Rows = rows(
    """
      |0000 -> 0000
      |2609 -> 1f3e
      |500e -> 48de
      |7a13 -> 734a
      |a7f8 -> a0c8
      |d1fd -> cb30
      |7e00 -> 7fff
      |fe00 -> 7fff
      |7c01 -> 7fff
      |7fff -> 7fff
      |fd76 -> 7fff
      |7df3 -> 7fff
      |"""
  )
  // Titan X (Pascal) sm_61, driver 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89,
  // driver 615, CUDA 13.3: half_const Cvt16
  private val cvt16Sm70Rows = rows(
    """
      |0000 -> 0000
      |26e2 -> 0000
      |525b -> 5240
      |7db5 -> 0000
      |a92e -> 0000
      |d488 -> d480
      |1135 -> 0000
      |3c8f -> 3c00
      |6808 -> 6808
      |9362 -> 0000
      |bedb -> bc00
      |ea35 -> ea35
      |"""
  )
  // Titan X (Pascal) sm_61, driver 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89,
  // driver 615, CUDA 13.3: half_const Cvt16
  private val cvt16Sm89Rows = rows(
    """
      |0000 -> 0000
      |26e2 -> 0000
      |525b -> 5240
      |7db5 -> 0000
      |a92e -> 0000
      |d488 -> d480
      |1135 -> 0000
      |3c8f -> 3c00
      |6808 -> 6808
      |9362 -> 0000
      |bedb -> bc00
      |ea35 -> ea35
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: carry_chain
  // I128Cmp
  private val i128CmpSm35Rows = rows(
    """
      |5d61dab5 80000000 ffffffff 80000001 00000001 36484818 0e0a3766 80000000 -> 00000000 00000000
      |00000000 fffffffe 80000001 4247f76a a5dd6ce9 fffffffe fffffffe 80000001 -> 00000001 00000000
      |b6c133b9 00000002 0000ffff 00000001 23b3b1a6 00000000 ffff0000 ddd02299 -> 00000001 00000000
      |a2657dcd c54d2726 682ec389 7fffffff ffff0000 7e561b7c b11263b8 fffffffe -> 00000001 00000000
      |12345678 52e38fb0 09ae6911 00000000 80000000 12345678 7f292a7a fffffffe -> 00000001 00000000
      |80000000 00000000 12345678 ffff0000 00000002 00000001 b43d8816 50b2dbf3 -> 00000004 00000000
      |7fffffff ffffffff e0bc2414 3cb94aa8 80000000 12345678 80000001 95486db2 -> 00000001 00000000
      |ffff0000 12345678 00000002 5d1231a9 ba2b7802 80000001 b5efb2f1 fffffffe -> 00000001 00000000
      |00000000 80000000 c3b5e5ca ffffffff 12345678 80000001 ffffffff 893215e6 -> 00000000 00000000
      |32404f9f 00000002 ffff0000 80000000 959e2ec0 61eb5c56 ffff0000 80000001 -> 00000005 00000000
      |ffff0000 00000002 4b7899d2 ffffffff 8c2f9f21 5b8b4bb6 80000000 08bb1c79 -> 00000004 00000000
      |80000001 12345678 00000002 00000002 4a27acc4 00000001 80000000 00000000 -> 00000000 00000000
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: carry_chain
  // I128Cmp
  private val i128CmpSm50Rows = rows(
    """
      |5d61dab5 80000000 ffffffff 80000001 00000001 36484818 0e0a3766 80000000 -> 00000000 00000000
      |00000000 fffffffe 80000001 4247f76a a5dd6ce9 fffffffe fffffffe 80000001 -> 00000001 00000000
      |b6c133b9 00000002 0000ffff 00000001 23b3b1a6 00000000 ffff0000 ddd02299 -> 00000001 00000000
      |a2657dcd c54d2726 682ec389 7fffffff ffff0000 7e561b7c b11263b8 fffffffe -> 00000001 00000000
      |12345678 52e38fb0 09ae6911 00000000 80000000 12345678 7f292a7a fffffffe -> 00000001 00000000
      |80000000 00000000 12345678 ffff0000 00000002 00000001 b43d8816 50b2dbf3 -> 00000004 00000000
      |7fffffff ffffffff e0bc2414 3cb94aa8 80000000 12345678 80000001 95486db2 -> 00000001 00000000
      |ffff0000 12345678 00000002 5d1231a9 ba2b7802 80000001 b5efb2f1 fffffffe -> 00000001 00000000
      |00000000 80000000 c3b5e5ca ffffffff 12345678 80000001 ffffffff 893215e6 -> 00000000 00000000
      |32404f9f 00000002 ffff0000 80000000 959e2ec0 61eb5c56 ffff0000 80000001 -> 00000005 00000000
      |ffff0000 00000002 4b7899d2 ffffffff 8c2f9f21 5b8b4bb6 80000000 08bb1c79 -> 00000004 00000000
      |80000001 12345678 00000002 00000002 4a27acc4 00000001 80000000 00000000 -> 00000000 00000000
      |"""
  )
  // GT 710 sm_35, driver 470, CUDA 11.8; Quadro K620 sm_50, driver 580, CUDA 12.8; Titan X (Pascal) sm_61, driver
  // 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89, driver 615, CUDA 13.3: carry_chain
  // I128Cmp
  private val i128CmpSm61Rows = rows(
    """
      |5d61dab5 80000000 ffffffff 80000001 00000001 36484818 0e0a3766 80000000 -> 00000000 00000000
      |00000000 fffffffe 80000001 4247f76a a5dd6ce9 fffffffe fffffffe 80000001 -> 00000001 00000000
      |b6c133b9 00000002 0000ffff 00000001 23b3b1a6 00000000 ffff0000 ddd02299 -> 00000001 00000000
      |a2657dcd c54d2726 682ec389 7fffffff ffff0000 7e561b7c b11263b8 fffffffe -> 00000001 00000000
      |12345678 52e38fb0 09ae6911 00000000 80000000 12345678 7f292a7a fffffffe -> 00000001 00000000
      |80000000 00000000 12345678 ffff0000 00000002 00000001 b43d8816 50b2dbf3 -> 00000004 00000000
      |7fffffff ffffffff e0bc2414 3cb94aa8 80000000 12345678 80000001 95486db2 -> 00000001 00000000
      |ffff0000 12345678 00000002 5d1231a9 ba2b7802 80000001 b5efb2f1 fffffffe -> 00000001 00000000
      |00000000 80000000 c3b5e5ca ffffffff 12345678 80000001 ffffffff 893215e6 -> 00000000 00000000
      |32404f9f 00000002 ffff0000 80000000 959e2ec0 61eb5c56 ffff0000 80000001 -> 00000005 00000000
      |ffff0000 00000002 4b7899d2 ffffffff 8c2f9f21 5b8b4bb6 80000000 08bb1c79 -> 00000004 00000000
      |80000001 12345678 00000002 00000002 4a27acc4 00000001 80000000 00000000 -> 00000000 00000000
      |"""
  )
  // Titan X (Pascal) sm_61, driver 580, CUDA 12.8; Tesla V100 sm_70, driver 550, CUDA 12.4; RTX 4060 Ti sm_89,
  // driver 615, CUDA 13.3: half_const Sin
  private val sinHalfSm61Rows = rows(
    """
      |0000 -> 0000
      |0001 -> 0001
      |bc00 -> babb
      |7bff -> 3bce
      |ffff -> 7fff
      |0100 -> 0100
      |0022 -> 0022
      |5ab6 -> 3b35
      |6fe7 -> b969
      |be7e -> bbfd
      |a646 -> a646
      |f8dc -> 3a2a
      |"""
  )
  // Tesla V100 sm_70, driver 550, CUDA 12.4: hmma884 RowColD32C16, the first warp
  private val hmmaRowColD32C16Sm70Rows = rows(
    """
      |00000000 c3124b98 c25d37db ac91cdc4 2d15b472 26dbc807 b80e2e2b c1eed0af 4ddc2c6b 00000000 00000000 00000000 00000000 -> c3b33a03 c2a76200 c26d2886 c1760cf4 c3260391 c2fa3425 c1c9df69 c39f8df6
      |00000001 cceb4a28 ba423c43 4c19bedf ad4f2e85 2af6c4eb b852b1d9 512d4a8d b0e1c571 00000000 00000000 00000000 00000000 -> c38fea2e c3abada6 415ab837 401c3555 c2ae8af1 c275901a c180e918 420ccd6e
      |00000002 b71840e9 cba74591 c3c4c349 334c3fa5 c5bb2bca 3a7e2e74 d6b72f93 33b5d2e3 00000000 00000000 00000000 00000000 -> c22524ff c09e1fbc 3ce35800 4323ee8a c2c7eca7 42aa47bb c0030fc0 414be628
      |00000003 2eecb66e 35a1b38d b0a3bd75 b8c94f10 27ed4451 aa8ebd25 4f7dcce8 36e8b6a3 00000000 00000000 00000000 00000000 -> 4204d505 4180a23b bf1ceecc c0cca3d8 c40f9dcb 4388d12c 3fb78cb6 bfbfe244
      |00000004 c53fca43 c3b7c6af c8ce3874 b31c3ab3 d0d7c6a4 b42e55da a8445411 2784ce62 00000000 00000000 00000000 00000000 -> 42003d96 c1907800 43161ae3 3ff0cac8 c32e5760 43fcad2e 43578794 c40191db
      |00000005 35773dda 4bbbb082 b1d4369d c614b2df 54ddb119 cd965344 2ebbab7c 2fcb345c 00000000 00000000 00000000 00000000 -> c0c5c284 c17969c0 c17f4a76 406f4fa1 436f9701 c402e462 c04083f8 41897bbd
      |00000006 c5cc4c04 3bbc3047 38952f2e 42f8ba19 2dd85559 b89d3388 b0e7a703 399a4ae5 00000000 00000000 00000000 00000000 -> 42a1bce7 43a9e679 3ffdf08a c3e97bd1 4341f7ca 40b56bd8 c3e9ddc5 4285f4c8
      |00000007 3e96b40b b48baeec 3d38cf2a 3d813bf9 40ca285f ce9ea635 ac4a2d69 3ccfb376 00000000 00000000 00000000 00000000 -> 42e0ed78 c22abe0b bc86e100 c18c9fdb c30937b6 c1ddb5b3 420fe03e c1b91ea8
      |00000008 ced2bcce 3842c654 baacbdd9 be93c624 298b410d cdda5627 25afc266 3bfeae8b 00000000 00000000 00000000 00000000 -> 428208ed 40eddfd4 c2d23770 418c13bc 424b4bb7 c1b069e5 41664aa2 c01db7f5
      |00000009 3f8b4043 b8304a52 38b1354a c6d1c455 b4353df4 3aa7a90a a8e244a7 b78b550d 00000000 00000000 00000000 00000000 -> c29febcf c24693ca c2fd12af c0381074 42943802 41c2059c c1be147e c15ec17f
      |0000000a 2d73b0cf b3d04890 3a7c421a b20dad53 52ecd234 c93024d4 2a13482d b789ca67 00000000 00000000 00000000 00000000 -> 42922c10 c25f9aaa bf8ab0ad c0ee108c c2e6b833 440c269f 43397879 c0a6c349
      |0000000b 2d22bcca bdcc420f 3cccbe27 37df34c6 39bdd700 c5cbbb21 2d1b478b 53c8a490 00000000 00000000 00000000 00000000 -> 40e1317c 405511e0 c08e9d28 c06a86a2 43b17fdb c22e0c7a 4281e175 426b207d
      |0000000c 4ebd4e19 ca4c4a79 ae2dbeca 43983b48 d57e3ffd b47fcfbd ab26b56d ab65c42f 00000000 00000000 00000000 00000000 -> c29c1257 43d9b8c8 c202b5ae 43029afa 44355e99 c4a6a109 c4172429 441e881b
      |0000000d be28cd3a c0153d3d 42764e3b 44eec83b 2ea32601 bd64b741 3907c33e 2408c47b 00000000 00000000 00000000 00000000 -> 41e8c86a c408c21a c2bd39b7 c3051b4f c4060b14 442181b0 c39e44fe 419263b4
      |0000000e cddbc0ee 2c15cfe7 3e0dbdb3 b58c3da5 abeac94c c9832476 4cb6ce9b 39213c52 00000000 00000000 00000000 00000000 -> c0132c60 c2e0d06b c2990e4f c208ae86 c1bc7c64 c40d6620 4202a31c 41b5e840
      |0000000f c429bcae ce40c35a 3c3a3a45 4a4cad87 3e863d12 3001a9aa a4892f6a b5e0a610 00000000 00000000 00000000 00000000 -> 41ec7db3 c234b348 bf985f70 c39fdb5e c04220fe 43eb7be8 40bba9f7 c213c264
      |00000010 4ee4c2e8 c8912c4a b2e4c83b 32e0b3fa b400c5a9 a8c32a01 30a8d2e6 383e54df 00000000 00000000 00000000 00000000 -> 428e88c5 43e51b36 41aebaca 423e7a2c c20789fb c292d3ba 41009ed8 c2c97c30
      |00000011 b33dbc93 cc293da6 4128c42b 4c604a11 ab9f57a8 5050d0fb b493d664 3ed9b15a 00000000 00000000 00000000 00000000 -> 43132cf8 be8e9c48 c35abadf 423e3b5f c2c0e7df c3871726 c321b0c0 434d2e62
      |00000012 4198bba1 379e2ee0 4f25bad1 c45833a3 315a2922 4c2f4360 d79937f4 3a73b4b7 00000000 00000000 00000000 00000000 -> c2c0b2d6 4100935c c058a028 41a30a3c 4463100f c3b808e3 429ccf59 c215a7ef
      |00000013 4384489e 35194cb5 caaa3b48 b29b32e6 ac17b49d c73132ae a819d4e2 b031b622 00000000 00000000 00000000 00000000 -> c20f9547 42abe07e c13f69fe 43ffaf5d 4285ecd6 40ed9bc1 42cc7f8d c2174559
      |00000014 c1d947e4 2f8144ad 414f4ac3 4b153319 a8cb2b73 d4f3cfc8 54ab3007 2fe9c6d6 00000000 00000000 00000000 00000000 -> 4211be87 40028d0a c184a3cb c090b8f3 42cb8aeb c32f1187 c20235ad 41901164
      |00000015 440fcf87 adc3c171 365bcf8f cfc2bd77 cf714d78 d30dc89c a935a7ba 4c324ff3 00000000 00000000 00000000 00000000 -> c210936d c227462e c0285420 4283f9a3 c3c728be 44659ae5 c1f14171 42bc06a2
      |00000016 3f15ac8d 3075392a 4c52cdd5 c70e3d82 c25fa8ad c98cbe89 4d17d0cd 337c5484 00000000 00000000 00000000 00000000 -> c20c477e c39825fc bf226804 c0bd28a4 c36bd35d 42bd63f8 42d0d2bd c185cd9d
      |00000017 31b732a0 c075cda8 cb27bc47 bd0e4a8f 51f14c5b ad5e38e0 3065424e 47b0385a 00000000 00000000 00000000 00000000 -> c10830a0 444a3807 41236ff9 c1fb2e64 44484dfc c2329c82 c1850d6a c390863f
      |00000018 cba1bf19 c579c65b c3094f32 2cd13caf d06b3668 478226f8 b1c13924 d19f33dd 00000000 00000000 00000000 00000000 -> 427ef623 419faa18 bfed3c61 c0952cd5 c09386ac c1c3894c 41148fb3 c1b67582
      |00000019 3efac9e3 acfe2e33 350e49d0 af31adbf 4947ced5 a50741a2 d543cbdb b9eed24d 00000000 00000000 00000000 00000000 -> c14099d2 40f9869a 41f1da77 432f745c c3b44be0 c35c80ad c0dc4388 bdec9910
      |0000001a b97137e2 ae6d361b baaf371d 33484d70 c2ea37a5 a926ace2 cf13c7ba 3512a76a 00000000 00000000 00000000 00000000 -> c18184a8 c14a615a 3f5cd871 bfc64d19 c2fe8b9f 43847d45 41108fc6 415f793f
      |0000001b b363b3e1 cecd40e1 cd1baea8 3993b4fe 3a7eaa82 bd4f25dc a49dbdac 30e4bb68 00000000 00000000 00000000 00000000 -> c1fd7376 41a15e50 4080d07c c15e13fd c25c08cf c20ce133 42381a90 c16d2b92
      |0000001c b60fb5b1 c7de319e 346b4e65 48874c07 51514ab5 344c46d2 52c6ae71 36dc3ddc 00000000 00000000 00000000 00000000 -> c17a8185 c0fa6690 41df9e78 c36f4b43 c29b6bee 42175bf0 c303199c 441f38c5
      |0000001d accb39e2 c3bd36b9 cca7cf88 4443c2a3 523bb8a5 b4dd4036 4ccec997 b2f7b699 00000000 00000000 00000000 00000000 -> c1810987 423532b3 41206945 430527eb c1a540eb c19b6358 430c5347 c3a36bf6
      |0000001e cdc7c6cd af13b93d b4d2adab ac5dba27 b0b34b99 aee22a67 342c5399 38223a8a 00000000 00000000 00000000 00000000 -> 411babc2 c2c70178 c1d0d394 c1f9874a 4000c118 c11f11bf 410e4963 43147a46
      |0000001f 46d1440b 411739d6 33e4cdae 40b53b28 44014738 b58c3382 d56a3391 ae923272 00000000 00000000 00000000 00000000 -> 40383cd6 c24247c1 409dd6ba 42280ba2 bf03a665 c1cd6185 c03c7668 c2a74312
      |"""
  )

  for {
    (mode, table) <- Vec(
      "RC8"  -> prmtRc8,
      "F4E"  -> prmtF4e,
      "B4E"  -> prmtB4e,
      "ECL"  -> prmtEcl,
      "ECR"  -> prmtEcr,
      "RC16" -> prmtRc16
    )
    sm <- Cuda
  }
    test(s"sm_$sm hardware: PRMT.$mode with a two-bit register selector") {
      check(sm, Vec("R0", "R1", "R2"), s"${mask(sm, "R2", 3)}; PRMT.$mode R4, R0, R2, R1", Vec("R4"), table)
    }

  for (sm <- Cuda) test(s"sm_$sm hardware: PRMT with a 16-bit register selector") {
    check(sm, Vec("R0", "R1", "R2:u16"), "PRMT R4, R0, R2, R1", Vec("R4"), prmt)
  }

  for ((sm, table) <- Vec(70 -> addCarryInSm70, 89 -> addCarryInSm89, 90 -> addCarryInSm89))
    test(s"sm_$sm hardware: IADD3.X with one live carry-out predicate chains a 64-bit add") {
      check(
        sm,
        Vec("R0", "R5", "R6", "R7"),
        "IADD3 R9, P0, R0, R5, RZ; IADD3.X R7, P0, R6, R7, RZ, P0, !PT; SEL R11, RZ, 0x1, !P0",
        Vec("R9", "R7", "R11"),
        table
      )
    }

  test("sm_61 hardware: HADD2.F32 with an F32 source operand narrows it toward zero") {
    check(61, Vec("R6:u16", "R0"), "HADD2.F32 R6, R6.H0_H0, R0.F32", Vec("R6"), haddRzOperandSm61)
  }

  test("sm_70 hardware: HADD2.F32 with an F32 source operand narrows it toward zero") {
    check(70, Vec("R7:u16", "R0"), "HADD2.F32 R7, R7.H0_H0, R0.F32", Vec("R7"), haddRzOperandSm70)
  }

  test("sm_61 hardware: HADD2.F32 rounds the half sum before widening it") {
    check(61, Vec("R0:u16", "R6:u16"), "HADD2.F32 R6, R0.H0_H0, R6.H0_H0", Vec("R6"), haddWideSm61)
  }

  test("sm_70 hardware: HADD2.F32 rounds the half sum before widening it") {
    check(70, Vec("R0:u16", "R7:u16"), "HADD2.F32 R7, R0.H0_H0, R7.H0_H0", Vec("R7"), haddWideSm70)
  }

  test("sm_89 hardware: HADD2 then HADD2.F32 of -RZ widens the half sum") {
    check(
      89,
      Vec("R0:u16", "R5:u16"),
      "HADD2 R0, R0.H0_H0, R5.H0_H0; HADD2.F32 R7, -RZ, R0.H0_H0",
      Vec("R7"),
      haddWideSm89
    )
  }

  for (sm <- Vec(50, 61)) test(s"sm_$sm hardware: IADD.SAT of a negated register saturates the true difference") {
    check(
      sm,
      Vec("R0", "R5"),
      "SHF.L R7, RZ, 0x10, R0; LOP32I.AND R0, R0, 0xffff0000; SHF.L R8, RZ, 0x10, R5; " +
        "LOP32I.AND R6, R5, 0xffff0000; IADD.SAT R7, R7, -R8; IADD.SAT R0, R0, -R6; PRMT R7, R7, 0x7632, R0",
      Vec("R7"),
      vsubss2
    )
  }

  test("sm_35 hardware: VADD2.SAT of a negated register saturates each half") {
    check(35, Vec("R6", "R7"), "VADD2.SAT R7, R6, -R7, RZ", Vec("R7"), vsubss2)
  }

  for {
    (name, body, table) <- Vec(
      ("MUFU.RCP", "MUFU.RCP R4, R0", rcpApproxLegacy),
      ("MUFU.RSQ", "MUFU.RSQ R4, R0", rsqrtApproxLegacy),
      ("MUFU.LG2", "MUFU.LG2 R4, R0", lg2ApproxLegacy),
      ("RRO.EX2 and MUFU.EX2", "RRO.EX2 R4, R0; MUFU.EX2 R4, R4", ex2ApproxLegacy),
      ("RRO.SINCOS and MUFU.SIN", "RRO.SINCOS R4, R0; MUFU.SIN R4, R4", sinApproxLegacy)
    )
    sm <- Legacy
  }
    test(s"sm_$sm hardware: $name approximation bits") {
      check(sm, Vec("R0"), body, Vec("R4"), table)
    }

  for (
    (sm, rcp, sin) <- Vec(
      (70, rcpApproxSm70, sinApproxSm70),
      (89, rcpApproxSm89, sinApproxSm89),
      (90, rcpApproxSm89, sinApproxSm89)
    )
  ) {
    test(s"sm_$sm hardware: MUFU.RCP approximation bits") {
      check(sm, Vec("R0"), "MUFU.RCP R4, R0", Vec("R4"), rcp)
    }
    test(s"sm_$sm hardware: FMUL.RZ and MUFU.SIN approximation bits") {
      check(sm, Vec("R0"), "FMUL.RZ R4, R0, 0.15915493667125701904; MUFU.SIN R4, R4", Vec("R4"), sin)
    }
  }

  test("sm_61 hardware: HMUL2 with an f32 constant-bank operand narrows the constant") {
    checkKernel(61, mulC03Sm61, mulC03Sm61Rows, mulC03Sm61Banks)
  }

  for ((sm, table) <- Vec(70 -> cvt16Sm70Rows, 89 -> cvt16Sm89Rows))
    test(s"sm_$sm hardware: F2I.S16.F16.TRUNC and I2F.F16.S16 round a half through a short") {
      check(sm, Vec("R2:u16"), "F2I.S16.F16.TRUNC.NTZ R0, R2; I2F.F16.S16 R7, R0", Vec("R7"), table)
    }

  for ((sm, table) <- Vec(70 -> isInfSm70Rows, 89 -> isInfSm89Rows, 90 -> isInfSm89Rows))
    test(s"sm_$sm hardware: I2F.F16.RZ of the -1, 0 or 1 isinf result") {
      check(
        sm,
        Vec("R2:u16"),
        "PRMT R0, R2, 0x9910, RZ; ISETP.NE.AND P1, PT, R0, 0x7c00, PT; ISETP.NE.AND P0, PT, R0, -0x400, PT; " +
          "SEL R0, RZ, 0x1, P1; SEL R0, R0, 0xffffffff, P0; I2F.F16.RZ R7, R0",
        Vec("R7"),
        table
      )
    }

  for ((sm, code, table) <- Vec((70, divRnFtzSm70, divRnFtzSm70Rows), (89, divRnFtzSm89, divRnFtzSm89Rows)))
    test(s"sm_$sm hardware: div.rn.ftz.f32 with its predicated BREAK") {
      checkKernel(sm, code, table)
    }

  test("sm_35 hardware: div.rn.ftz.f32 with FSET.NEU.FTZ setting the condition code") {
    checkKernel(35, divRnFtzSm35, divRnFtzSm35Rows, divRnFtzSm35Banks)
  }

  for (
    (sm, code, table, banks) <- Vec(
      (50, atan2fSm50, atan2fSm50Rows, atan2fSm50Banks),
      (61, atan2fSm61, atan2fSm61Rows, atan2fSm61Banks)
    )
  )
    test(s"sm_$sm hardware: atan2f with FSET.EQ setting the condition code") {
      checkKernel(sm, code, table, banks)
    }

  for (
    (sm, code, table, banks) <- Vec(
      (35, coshfSm35, coshfSm35Rows, coshfSm35Banks),
      (50, coshfSm50, coshfSm50Rows, coshfSm50Banks),
      (61, coshfSm61, coshfSm61Rows, coshfSm61Banks)
    )
  )
    test(s"sm_$sm hardware: coshf with RRO outside a straight-line MUFU lifetime") {
      checkKernel(sm, code, table, banks)
    }

  for (
    (sm, code, table) <- Vec(
      (35, i128CmpSm35, i128CmpSm35Rows),
      (50, i128CmpSm50, i128CmpSm50Rows),
      (61, i128CmpSm61, i128CmpSm61Rows)
    )
  )
    test(s"sm_$sm hardware: 128-bit compare through ISET.X and LOP.X condition codes") {
      checkKernel(sm, code, table)
    }

  test("sm_35 hardware: VADD2 with half selectors on both sources") {
    checkKernel(35, vadd2Sm35, vadd2Sm35Rows)
  }

  test("sm_35 hardware: IMAD.U32.U32.HI.CC carry chains of a signed 64-bit high multiply") {
    checkKernel(35, mul64HiSSm35, mul64HiSSm35Rows)
  }

  test("sm_61 hardware: half sin through the F2F and MUFU sequence") {
    checkKernel(61, sinHalfSm61, sinHalfSm61Rows, sinHalfSm61Banks)
  }

  test("sm_70 hardware: HMMA.884.F32.F16 with a 16-bit C and a 32-bit D") {
    checkKernel(70, hmmaRowColD32C16Sm70, hmmaRowColD32C16Sm70Rows)
  }

  for (
    (sm, spaces, spacesTable, cvta, cvtaTable) <- Vec(
      (35, spacesSm35, spacesSm35Rows, cvtaSm35, cvtaSm35Rows),
      (50, spacesSm50, spacesSm50Rows, cvtaSm50, cvtaSm50Rows),
      (61, spacesSm61, spacesSm61Rows, cvtaSm61, cvtaSm61Rows),
      (70, spacesSm70, spacesSm70Rows, cvtaSm70, cvtaSm70Rows),
      (89, spacesSm89, spacesSm89Rows, cvtaSm89, cvtaSm89Rows)
    )
  ) {
    test(s"sm_$sm hardware: isspacep classifies local, global, shared and null generic pointers") {
      checkGrid(sm, spaces, 2, spacesTable)
    }
    test(s"sm_$sm hardware: cvta between local and generic addresses") {
      checkGrid(sm, cvta, 2, cvtaTable)
    }
  }
}
