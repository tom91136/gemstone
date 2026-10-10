// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private object FlatInstructionDefs {
  case object ClusterLoadAsyncToLdsB128 extends Opcode("cluster_load_async_to_lds_b128", 158)

  case object ClusterLoadAsyncToLdsB32 extends Opcode("cluster_load_async_to_lds_b32", 159)

  case object ClusterLoadAsyncToLdsB64 extends Opcode("cluster_load_async_to_lds_b64", 160)

  case object ClusterLoadAsyncToLdsB8 extends Opcode("cluster_load_async_to_lds_b8", 161)

  case object ClusterLoadB128 extends Opcode("cluster_load_b128", 162)

  case object ClusterLoadB32 extends Opcode("cluster_load_b32", 163)

  case object ClusterLoadB64 extends Opcode("cluster_load_b64", 164)

  case object FlatAtomicAdd extends Opcode("flat_atomic_add", 405)

  case object FlatAtomicAddF32 extends Opcode("flat_atomic_add_f32", 406)

  case object FlatAtomicAddF64 extends Opcode("flat_atomic_add_f64", 407)

  case object FlatAtomicAddU32 extends Opcode("flat_atomic_add_u32", 408)

  case object FlatAtomicAddU64 extends Opcode("flat_atomic_add_u64", 409)

  case object FlatAtomicAddX2 extends Opcode("flat_atomic_add_x2", 410)

  case object FlatAtomicAnd extends Opcode("flat_atomic_and", 411)

  case object FlatAtomicAndB32 extends Opcode("flat_atomic_and_b32", 412)

  case object FlatAtomicAndB64 extends Opcode("flat_atomic_and_b64", 413)

  case object FlatAtomicAndX2 extends Opcode("flat_atomic_and_x2", 414)

  case object FlatAtomicCmpswap extends Opcode("flat_atomic_cmpswap", 415)

  case object FlatAtomicCmpswapB32 extends Opcode("flat_atomic_cmpswap_b32", 416)

  case object FlatAtomicCmpswapB64 extends Opcode("flat_atomic_cmpswap_b64", 417)

  case object FlatAtomicCmpswapF32 extends Opcode("flat_atomic_cmpswap_f32", 418)

  case object FlatAtomicCmpswapX2 extends Opcode("flat_atomic_cmpswap_x2", 419)

  case object FlatAtomicCondSubU32 extends Opcode("flat_atomic_cond_sub_u32", 420)

  case object FlatAtomicDec extends Opcode("flat_atomic_dec", 421)

  case object FlatAtomicDecU32 extends Opcode("flat_atomic_dec_u32", 422)

  case object FlatAtomicDecU64 extends Opcode("flat_atomic_dec_u64", 423)

  case object FlatAtomicDecX2 extends Opcode("flat_atomic_dec_x2", 424)

  case object FlatAtomicFcmpswap extends Opcode("flat_atomic_fcmpswap", 425)

  case object FlatAtomicFcmpswapX2 extends Opcode("flat_atomic_fcmpswap_x2", 426)

  case object FlatAtomicFmax extends Opcode("flat_atomic_fmax", 427)

  case object FlatAtomicFmaxX2 extends Opcode("flat_atomic_fmax_x2", 428)

  case object FlatAtomicFmin extends Opcode("flat_atomic_fmin", 429)

  case object FlatAtomicFminX2 extends Opcode("flat_atomic_fmin_x2", 430)

  case object FlatAtomicInc extends Opcode("flat_atomic_inc", 431)

  case object FlatAtomicIncU32 extends Opcode("flat_atomic_inc_u32", 432)

  case object FlatAtomicIncU64 extends Opcode("flat_atomic_inc_u64", 433)

  case object FlatAtomicIncX2 extends Opcode("flat_atomic_inc_x2", 434)

  case object FlatAtomicMaxF32 extends Opcode("flat_atomic_max_f32", 435)

  case object FlatAtomicMaxF64 extends Opcode("flat_atomic_max_f64", 436)

  case object FlatAtomicMaxI32 extends Opcode("flat_atomic_max_i32", 437)

  case object FlatAtomicMaxI64 extends Opcode("flat_atomic_max_i64", 438)

  case object FlatAtomicMaxNumF32 extends Opcode("flat_atomic_max_num_f32", 439)

  case object FlatAtomicMaxNumF64 extends Opcode("flat_atomic_max_num_f64", 440)

  case object FlatAtomicMaxU32 extends Opcode("flat_atomic_max_u32", 441)

  case object FlatAtomicMaxU64 extends Opcode("flat_atomic_max_u64", 442)

  case object FlatAtomicMinF32 extends Opcode("flat_atomic_min_f32", 443)

  case object FlatAtomicMinF64 extends Opcode("flat_atomic_min_f64", 444)

  case object FlatAtomicMinI32 extends Opcode("flat_atomic_min_i32", 445)

  case object FlatAtomicMinI64 extends Opcode("flat_atomic_min_i64", 446)

  case object FlatAtomicMinNumF32 extends Opcode("flat_atomic_min_num_f32", 447)

  case object FlatAtomicMinNumF64 extends Opcode("flat_atomic_min_num_f64", 448)

  case object FlatAtomicMinU32 extends Opcode("flat_atomic_min_u32", 449)

  case object FlatAtomicMinU64 extends Opcode("flat_atomic_min_u64", 450)

  case object FlatAtomicOr extends Opcode("flat_atomic_or", 451)

  case object FlatAtomicOrB32 extends Opcode("flat_atomic_or_b32", 452)

  case object FlatAtomicOrB64 extends Opcode("flat_atomic_or_b64", 453)

  case object FlatAtomicOrX2 extends Opcode("flat_atomic_or_x2", 454)

  case object FlatAtomicPkAddBf16 extends Opcode("flat_atomic_pk_add_bf16", 455)

  case object FlatAtomicPkAddF16 extends Opcode("flat_atomic_pk_add_f16", 456)

  case object FlatAtomicSmax extends Opcode("flat_atomic_smax", 457)

  case object FlatAtomicSmaxX2 extends Opcode("flat_atomic_smax_x2", 458)

  case object FlatAtomicSmin extends Opcode("flat_atomic_smin", 459)

  case object FlatAtomicSminX2 extends Opcode("flat_atomic_smin_x2", 460)

  case object FlatAtomicSub extends Opcode("flat_atomic_sub", 461)

  case object FlatAtomicSubClampU32 extends Opcode("flat_atomic_sub_clamp_u32", 462)

  case object FlatAtomicSubU32 extends Opcode("flat_atomic_sub_u32", 463)

  case object FlatAtomicSubU64 extends Opcode("flat_atomic_sub_u64", 464)

  case object FlatAtomicSubX2 extends Opcode("flat_atomic_sub_x2", 465)

  case object FlatAtomicSwap extends Opcode("flat_atomic_swap", 466)

  case object FlatAtomicSwapB32 extends Opcode("flat_atomic_swap_b32", 467)

  case object FlatAtomicSwapB64 extends Opcode("flat_atomic_swap_b64", 468)

  case object FlatAtomicSwapX2 extends Opcode("flat_atomic_swap_x2", 469)

  case object FlatAtomicUmax extends Opcode("flat_atomic_umax", 470)

  case object FlatAtomicUmaxX2 extends Opcode("flat_atomic_umax_x2", 471)

  case object FlatAtomicUmin extends Opcode("flat_atomic_umin", 472)

  case object FlatAtomicUminX2 extends Opcode("flat_atomic_umin_x2", 473)

  case object FlatAtomicXor extends Opcode("flat_atomic_xor", 474)

  case object FlatAtomicXorB32 extends Opcode("flat_atomic_xor_b32", 475)

  case object FlatAtomicXorB64 extends Opcode("flat_atomic_xor_b64", 476)

  case object FlatAtomicXorX2 extends Opcode("flat_atomic_xor_x2", 477)

  case object FlatLoadB128 extends Opcode("flat_load_b128", 478)

  case object FlatLoadB32 extends Opcode("flat_load_b32", 479)

  case object FlatLoadB64 extends Opcode("flat_load_b64", 480)

  case object FlatLoadB96 extends Opcode("flat_load_b96", 481)

  case object FlatLoadD16B16 extends Opcode("flat_load_d16_b16", 482)

  case object FlatLoadD16HiB16 extends Opcode("flat_load_d16_hi_b16", 483)

  case object FlatLoadD16HiI8 extends Opcode("flat_load_d16_hi_i8", 484)

  case object FlatLoadD16HiU8 extends Opcode("flat_load_d16_hi_u8", 485)

  case object FlatLoadD16I8 extends Opcode("flat_load_d16_i8", 486)

  case object FlatLoadD16U8 extends Opcode("flat_load_d16_u8", 487)

  case object FlatLoadDword extends Opcode("flat_load_dword", 488)

  case object FlatLoadDwordx2 extends Opcode("flat_load_dwordx2", 489)

  case object FlatLoadDwordx3 extends Opcode("flat_load_dwordx3", 490)

  case object FlatLoadDwordx4 extends Opcode("flat_load_dwordx4", 491)

  case object FlatLoadI16 extends Opcode("flat_load_i16", 492)

  case object FlatLoadI8 extends Opcode("flat_load_i8", 493)

  case object FlatLoadMonitorB128 extends Opcode("flat_load_monitor_b128", 494)

  case object FlatLoadMonitorB32 extends Opcode("flat_load_monitor_b32", 495)

  case object FlatLoadMonitorB64 extends Opcode("flat_load_monitor_b64", 496)

  case object FlatLoadSbyte extends Opcode("flat_load_sbyte", 497)

  case object FlatLoadSbyteD16 extends Opcode("flat_load_sbyte_d16", 498)

  case object FlatLoadSbyteD16Hi extends Opcode("flat_load_sbyte_d16_hi", 499)

  case object FlatLoadShortD16 extends Opcode("flat_load_short_d16", 500)

  case object FlatLoadShortD16Hi extends Opcode("flat_load_short_d16_hi", 501)

  case object FlatLoadSshort extends Opcode("flat_load_sshort", 502)

  case object FlatLoadU16 extends Opcode("flat_load_u16", 503)

  case object FlatLoadU8 extends Opcode("flat_load_u8", 504)

  case object FlatLoadUbyte extends Opcode("flat_load_ubyte", 505)

  case object FlatLoadUbyteD16 extends Opcode("flat_load_ubyte_d16", 506)

  case object FlatLoadUbyteD16Hi extends Opcode("flat_load_ubyte_d16_hi", 507)

  case object FlatLoadUshort extends Opcode("flat_load_ushort", 508)

  case object FlatPrefetchB8 extends Opcode("flat_prefetch_b8", 509)

  case object FlatStoreB128 extends Opcode("flat_store_b128", 510)

  case object FlatStoreB16 extends Opcode("flat_store_b16", 511)

  case object FlatStoreB32 extends Opcode("flat_store_b32", 512)

  case object FlatStoreB64 extends Opcode("flat_store_b64", 513)

  case object FlatStoreB8 extends Opcode("flat_store_b8", 514)

  case object FlatStoreB96 extends Opcode("flat_store_b96", 515)

  case object FlatStoreByte extends Opcode("flat_store_byte", 516)

  case object FlatStoreByteD16Hi extends Opcode("flat_store_byte_d16_hi", 517)

  case object FlatStoreD16HiB16 extends Opcode("flat_store_d16_hi_b16", 518)

  case object FlatStoreD16HiB8 extends Opcode("flat_store_d16_hi_b8", 519)

  case object FlatStoreDword extends Opcode("flat_store_dword", 520)

  case object FlatStoreDwordx2 extends Opcode("flat_store_dwordx2", 521)

  case object FlatStoreDwordx3 extends Opcode("flat_store_dwordx3", 522)

  case object FlatStoreDwordx4 extends Opcode("flat_store_dwordx4", 523)

  case object FlatStoreShort extends Opcode("flat_store_short", 524)

  case object FlatStoreShortD16Hi extends Opcode("flat_store_short_d16_hi", 525)

  case object GlobalAtomicAdd extends Opcode("global_atomic_add", 526)

  case object GlobalAtomicAddF32 extends Opcode("global_atomic_add_f32", 527)

  case object GlobalAtomicAddF64 extends Opcode("global_atomic_add_f64", 528)

  case object GlobalAtomicAddU32 extends Opcode("global_atomic_add_u32", 529)

  case object GlobalAtomicAddU64 extends Opcode("global_atomic_add_u64", 530)

  case object GlobalAtomicAddX2 extends Opcode("global_atomic_add_x2", 531)

  case object GlobalAtomicAnd extends Opcode("global_atomic_and", 532)

  case object GlobalAtomicAndB32 extends Opcode("global_atomic_and_b32", 533)

  case object GlobalAtomicAndB64 extends Opcode("global_atomic_and_b64", 534)

  case object GlobalAtomicAndX2 extends Opcode("global_atomic_and_x2", 535)

  case object GlobalAtomicCmpswap extends Opcode("global_atomic_cmpswap", 536)

  case object GlobalAtomicCmpswapB32 extends Opcode("global_atomic_cmpswap_b32", 537)

  case object GlobalAtomicCmpswapB64 extends Opcode("global_atomic_cmpswap_b64", 538)

  case object GlobalAtomicCmpswapF32 extends Opcode("global_atomic_cmpswap_f32", 539)

  case object GlobalAtomicCmpswapX2 extends Opcode("global_atomic_cmpswap_x2", 540)

  case object GlobalAtomicCondSubU32 extends Opcode("global_atomic_cond_sub_u32", 541)

  case object GlobalAtomicCsub extends Opcode("global_atomic_csub", 542)

  case object GlobalAtomicCsubU32 extends Opcode("global_atomic_csub_u32", 543)

  case object GlobalAtomicDec extends Opcode("global_atomic_dec", 544)

  case object GlobalAtomicDecU32 extends Opcode("global_atomic_dec_u32", 545)

  case object GlobalAtomicDecU64 extends Opcode("global_atomic_dec_u64", 546)

  case object GlobalAtomicDecX2 extends Opcode("global_atomic_dec_x2", 547)

  case object GlobalAtomicFcmpswap extends Opcode("global_atomic_fcmpswap", 548)

  case object GlobalAtomicFcmpswapX2 extends Opcode("global_atomic_fcmpswap_x2", 549)

  case object GlobalAtomicFmax extends Opcode("global_atomic_fmax", 550)

  case object GlobalAtomicFmaxX2 extends Opcode("global_atomic_fmax_x2", 551)

  case object GlobalAtomicFmin extends Opcode("global_atomic_fmin", 552)

  case object GlobalAtomicFminX2 extends Opcode("global_atomic_fmin_x2", 553)

  case object GlobalAtomicInc extends Opcode("global_atomic_inc", 554)

  case object GlobalAtomicIncU32 extends Opcode("global_atomic_inc_u32", 555)

  case object GlobalAtomicIncU64 extends Opcode("global_atomic_inc_u64", 556)

  case object GlobalAtomicIncX2 extends Opcode("global_atomic_inc_x2", 557)

  case object GlobalAtomicMaxF32 extends Opcode("global_atomic_max_f32", 558)

  case object GlobalAtomicMaxF64 extends Opcode("global_atomic_max_f64", 559)

  case object GlobalAtomicMaxI32 extends Opcode("global_atomic_max_i32", 560)

  case object GlobalAtomicMaxI64 extends Opcode("global_atomic_max_i64", 561)

  case object GlobalAtomicMaxNumF32 extends Opcode("global_atomic_max_num_f32", 562)

  case object GlobalAtomicMaxNumF64 extends Opcode("global_atomic_max_num_f64", 563)

  case object GlobalAtomicMaxU32 extends Opcode("global_atomic_max_u32", 564)

  case object GlobalAtomicMaxU64 extends Opcode("global_atomic_max_u64", 565)

  case object GlobalAtomicMinF32 extends Opcode("global_atomic_min_f32", 566)

  case object GlobalAtomicMinF64 extends Opcode("global_atomic_min_f64", 567)

  case object GlobalAtomicMinI32 extends Opcode("global_atomic_min_i32", 568)

  case object GlobalAtomicMinI64 extends Opcode("global_atomic_min_i64", 569)

  case object GlobalAtomicMinNumF32 extends Opcode("global_atomic_min_num_f32", 570)

  case object GlobalAtomicMinNumF64 extends Opcode("global_atomic_min_num_f64", 571)

  case object GlobalAtomicMinU32 extends Opcode("global_atomic_min_u32", 572)

  case object GlobalAtomicMinU64 extends Opcode("global_atomic_min_u64", 573)

  case object GlobalAtomicOr extends Opcode("global_atomic_or", 574)

  case object GlobalAtomicOrB32 extends Opcode("global_atomic_or_b32", 575)

  case object GlobalAtomicOrB64 extends Opcode("global_atomic_or_b64", 576)

  case object GlobalAtomicOrX2 extends Opcode("global_atomic_or_x2", 577)

  case object GlobalAtomicOrderedAddB64 extends Opcode("global_atomic_ordered_add_b64", 578)

  case object GlobalAtomicPkAddBf16 extends Opcode("global_atomic_pk_add_bf16", 579)

  case object GlobalAtomicPkAddF16 extends Opcode("global_atomic_pk_add_f16", 580)

  case object GlobalAtomicSmax extends Opcode("global_atomic_smax", 581)

  case object GlobalAtomicSmaxX2 extends Opcode("global_atomic_smax_x2", 582)

  case object GlobalAtomicSmin extends Opcode("global_atomic_smin", 583)

  case object GlobalAtomicSminX2 extends Opcode("global_atomic_smin_x2", 584)

  case object GlobalAtomicSub extends Opcode("global_atomic_sub", 585)

  case object GlobalAtomicSubClampU32 extends Opcode("global_atomic_sub_clamp_u32", 586)

  case object GlobalAtomicSubU32 extends Opcode("global_atomic_sub_u32", 587)

  case object GlobalAtomicSubU64 extends Opcode("global_atomic_sub_u64", 588)

  case object GlobalAtomicSubX2 extends Opcode("global_atomic_sub_x2", 589)

  case object GlobalAtomicSwap extends Opcode("global_atomic_swap", 590)

  case object GlobalAtomicSwapB32 extends Opcode("global_atomic_swap_b32", 591)

  case object GlobalAtomicSwapB64 extends Opcode("global_atomic_swap_b64", 592)

  case object GlobalAtomicSwapX2 extends Opcode("global_atomic_swap_x2", 593)

  case object GlobalAtomicUmax extends Opcode("global_atomic_umax", 594)

  case object GlobalAtomicUmaxX2 extends Opcode("global_atomic_umax_x2", 595)

  case object GlobalAtomicUmin extends Opcode("global_atomic_umin", 596)

  case object GlobalAtomicUminX2 extends Opcode("global_atomic_umin_x2", 597)

  case object GlobalAtomicXor extends Opcode("global_atomic_xor", 598)

  case object GlobalAtomicXorB32 extends Opcode("global_atomic_xor_b32", 599)

  case object GlobalAtomicXorB64 extends Opcode("global_atomic_xor_b64", 600)

  case object GlobalAtomicXorX2 extends Opcode("global_atomic_xor_x2", 601)

  case object GlobalInv extends Opcode("global_inv", 602)

  case object GlobalLoadAddtidB32 extends Opcode("global_load_addtid_b32", 603)

  case object GlobalLoadAsyncToLdsB128 extends Opcode("global_load_async_to_lds_b128", 604)

  case object GlobalLoadAsyncToLdsB32 extends Opcode("global_load_async_to_lds_b32", 605)

  case object GlobalLoadAsyncToLdsB64 extends Opcode("global_load_async_to_lds_b64", 606)

  case object GlobalLoadAsyncToLdsB8 extends Opcode("global_load_async_to_lds_b8", 607)

  case object GlobalLoadB128 extends Opcode("global_load_b128", 608)

  case object GlobalLoadB32 extends Opcode("global_load_b32", 609)

  case object GlobalLoadB64 extends Opcode("global_load_b64", 610)

  case object GlobalLoadB96 extends Opcode("global_load_b96", 611)

  case object GlobalLoadBlock extends Opcode("global_load_block", 612)

  case object GlobalLoadD16B16 extends Opcode("global_load_d16_b16", 613)

  case object GlobalLoadD16HiB16 extends Opcode("global_load_d16_hi_b16", 614)

  case object GlobalLoadD16HiI8 extends Opcode("global_load_d16_hi_i8", 615)

  case object GlobalLoadD16HiU8 extends Opcode("global_load_d16_hi_u8", 616)

  case object GlobalLoadD16I8 extends Opcode("global_load_d16_i8", 617)

  case object GlobalLoadD16U8 extends Opcode("global_load_d16_u8", 618)

  case object GlobalLoadDword extends Opcode("global_load_dword", 619)

  case object GlobalLoadDwordAddtid extends Opcode("global_load_dword_addtid", 620)

  case object GlobalLoadDwordx2 extends Opcode("global_load_dwordx2", 621)

  case object GlobalLoadDwordx3 extends Opcode("global_load_dwordx3", 622)

  case object GlobalLoadDwordx4 extends Opcode("global_load_dwordx4", 623)

  case object GlobalLoadI16 extends Opcode("global_load_i16", 624)

  case object GlobalLoadI8 extends Opcode("global_load_i8", 625)

  case object GlobalLoadLdsDword extends Opcode("global_load_lds_dword", 626)

  case object GlobalLoadLdsDwordx3 extends Opcode("global_load_lds_dwordx3", 627)

  case object GlobalLoadLdsDwordx4 extends Opcode("global_load_lds_dwordx4", 628)

  case object GlobalLoadLdsSbyte extends Opcode("global_load_lds_sbyte", 629)

  case object GlobalLoadLdsSshort extends Opcode("global_load_lds_sshort", 630)

  case object GlobalLoadLdsUbyte extends Opcode("global_load_lds_ubyte", 631)

  case object GlobalLoadLdsUshort extends Opcode("global_load_lds_ushort", 632)

  case object GlobalLoadMonitorB128 extends Opcode("global_load_monitor_b128", 633)

  case object GlobalLoadMonitorB32 extends Opcode("global_load_monitor_b32", 634)

  case object GlobalLoadMonitorB64 extends Opcode("global_load_monitor_b64", 635)

  case object GlobalLoadSbyte extends Opcode("global_load_sbyte", 636)

  case object GlobalLoadSbyteD16 extends Opcode("global_load_sbyte_d16", 637)

  case object GlobalLoadSbyteD16Hi extends Opcode("global_load_sbyte_d16_hi", 638)

  case object GlobalLoadShortD16 extends Opcode("global_load_short_d16", 639)

  case object GlobalLoadShortD16Hi extends Opcode("global_load_short_d16_hi", 640)

  case object GlobalLoadSshort extends Opcode("global_load_sshort", 641)

  case object GlobalLoadTr16B128 extends Opcode("global_load_tr16_b128", 642)

  case object GlobalLoadTr4B64 extends Opcode("global_load_tr4_b64", 643)

  case object GlobalLoadTr6B96 extends Opcode("global_load_tr6_b96", 644)

  case object GlobalLoadTr8B64 extends Opcode("global_load_tr8_b64", 645)

  case object GlobalLoadTrB128 extends Opcode("global_load_tr_b128", 646)

  case object GlobalLoadTrB64 extends Opcode("global_load_tr_b64", 647)

  case object GlobalLoadU16 extends Opcode("global_load_u16", 648)

  case object GlobalLoadU8 extends Opcode("global_load_u8", 649)

  case object GlobalLoadUbyte extends Opcode("global_load_ubyte", 650)

  case object GlobalLoadUbyteD16 extends Opcode("global_load_ubyte_d16", 651)

  case object GlobalLoadUbyteD16Hi extends Opcode("global_load_ubyte_d16_hi", 652)

  case object GlobalLoadUshort extends Opcode("global_load_ushort", 653)

  case object GlobalPrefetchB8 extends Opcode("global_prefetch_b8", 654)

  case object GlobalStoreAddtidB32 extends Opcode("global_store_addtid_b32", 655)

  case object GlobalStoreAsyncFromLdsB128 extends Opcode("global_store_async_from_lds_b128", 656)

  case object GlobalStoreAsyncFromLdsB32 extends Opcode("global_store_async_from_lds_b32", 657)

  case object GlobalStoreAsyncFromLdsB64 extends Opcode("global_store_async_from_lds_b64", 658)

  case object GlobalStoreAsyncFromLdsB8 extends Opcode("global_store_async_from_lds_b8", 659)

  case object GlobalStoreB128 extends Opcode("global_store_b128", 660)

  case object GlobalStoreB16 extends Opcode("global_store_b16", 661)

  case object GlobalStoreB32 extends Opcode("global_store_b32", 662)

  case object GlobalStoreB64 extends Opcode("global_store_b64", 663)

  case object GlobalStoreB8 extends Opcode("global_store_b8", 664)

  case object GlobalStoreB96 extends Opcode("global_store_b96", 665)

  case object GlobalStoreBlock extends Opcode("global_store_block", 666)

  case object GlobalStoreByte extends Opcode("global_store_byte", 667)

  case object GlobalStoreByteD16Hi extends Opcode("global_store_byte_d16_hi", 668)

  case object GlobalStoreD16HiB16 extends Opcode("global_store_d16_hi_b16", 669)

  case object GlobalStoreD16HiB8 extends Opcode("global_store_d16_hi_b8", 670)

  case object GlobalStoreDword extends Opcode("global_store_dword", 671)

  case object GlobalStoreDwordAddtid extends Opcode("global_store_dword_addtid", 672)

  case object GlobalStoreDwordx2 extends Opcode("global_store_dwordx2", 673)

  case object GlobalStoreDwordx3 extends Opcode("global_store_dwordx3", 674)

  case object GlobalStoreDwordx4 extends Opcode("global_store_dwordx4", 675)

  case object GlobalStoreShort extends Opcode("global_store_short", 676)

  case object GlobalStoreShortD16Hi extends Opcode("global_store_short_d16_hi", 677)

  case object GlobalWb extends Opcode("global_wb", 678)

  case object GlobalWbinv extends Opcode("global_wbinv", 679)

  case object ScratchLoadB128 extends Opcode("scratch_load_b128", 1286)

  case object ScratchLoadB32 extends Opcode("scratch_load_b32", 1287)

  case object ScratchLoadB64 extends Opcode("scratch_load_b64", 1288)

  case object ScratchLoadB96 extends Opcode("scratch_load_b96", 1289)

  case object ScratchLoadBlock extends Opcode("scratch_load_block", 1290)

  case object ScratchLoadD16B16 extends Opcode("scratch_load_d16_b16", 1291)

  case object ScratchLoadD16HiB16 extends Opcode("scratch_load_d16_hi_b16", 1292)

  case object ScratchLoadD16HiI8 extends Opcode("scratch_load_d16_hi_i8", 1293)

  case object ScratchLoadD16HiU8 extends Opcode("scratch_load_d16_hi_u8", 1294)

  case object ScratchLoadD16I8 extends Opcode("scratch_load_d16_i8", 1295)

  case object ScratchLoadD16U8 extends Opcode("scratch_load_d16_u8", 1296)

  case object ScratchLoadDword extends Opcode("scratch_load_dword", 1297)

  case object ScratchLoadDwordx2 extends Opcode("scratch_load_dwordx2", 1298)

  case object ScratchLoadDwordx3 extends Opcode("scratch_load_dwordx3", 1299)

  case object ScratchLoadDwordx4 extends Opcode("scratch_load_dwordx4", 1300)

  case object ScratchLoadI16 extends Opcode("scratch_load_i16", 1301)

  case object ScratchLoadI8 extends Opcode("scratch_load_i8", 1302)

  case object ScratchLoadLdsDword extends Opcode("scratch_load_lds_dword", 1303)

  case object ScratchLoadLdsSbyte extends Opcode("scratch_load_lds_sbyte", 1304)

  case object ScratchLoadLdsSshort extends Opcode("scratch_load_lds_sshort", 1305)

  case object ScratchLoadLdsUbyte extends Opcode("scratch_load_lds_ubyte", 1306)

  case object ScratchLoadLdsUshort extends Opcode("scratch_load_lds_ushort", 1307)

  case object ScratchLoadSbyte extends Opcode("scratch_load_sbyte", 1308)

  case object ScratchLoadSbyteD16 extends Opcode("scratch_load_sbyte_d16", 1309)

  case object ScratchLoadSbyteD16Hi extends Opcode("scratch_load_sbyte_d16_hi", 1310)

  case object ScratchLoadShortD16 extends Opcode("scratch_load_short_d16", 1311)

  case object ScratchLoadShortD16Hi extends Opcode("scratch_load_short_d16_hi", 1312)

  case object ScratchLoadSshort extends Opcode("scratch_load_sshort", 1313)

  case object ScratchLoadU16 extends Opcode("scratch_load_u16", 1314)

  case object ScratchLoadU8 extends Opcode("scratch_load_u8", 1315)

  case object ScratchLoadUbyte extends Opcode("scratch_load_ubyte", 1316)

  case object ScratchLoadUbyteD16 extends Opcode("scratch_load_ubyte_d16", 1317)

  case object ScratchLoadUbyteD16Hi extends Opcode("scratch_load_ubyte_d16_hi", 1318)

  case object ScratchLoadUshort extends Opcode("scratch_load_ushort", 1319)

  case object ScratchStoreB128 extends Opcode("scratch_store_b128", 1320)

  case object ScratchStoreB16 extends Opcode("scratch_store_b16", 1321)

  case object ScratchStoreB32 extends Opcode("scratch_store_b32", 1322)

  case object ScratchStoreB64 extends Opcode("scratch_store_b64", 1323)

  case object ScratchStoreB8 extends Opcode("scratch_store_b8", 1324)

  case object ScratchStoreB96 extends Opcode("scratch_store_b96", 1325)

  case object ScratchStoreBlock extends Opcode("scratch_store_block", 1326)

  case object ScratchStoreByte extends Opcode("scratch_store_byte", 1327)

  case object ScratchStoreByteD16Hi extends Opcode("scratch_store_byte_d16_hi", 1328)

  case object ScratchStoreD16HiB16 extends Opcode("scratch_store_d16_hi_b16", 1329)

  case object ScratchStoreD16HiB8 extends Opcode("scratch_store_d16_hi_b8", 1330)

  case object ScratchStoreDword extends Opcode("scratch_store_dword", 1331)

  case object ScratchStoreDwordx2 extends Opcode("scratch_store_dwordx2", 1332)

  case object ScratchStoreDwordx3 extends Opcode("scratch_store_dwordx3", 1333)

  case object ScratchStoreDwordx4 extends Opcode("scratch_store_dwordx4", 1334)

  case object ScratchStoreShort extends Opcode("scratch_store_short", 1335)

  case object ScratchStoreShortD16Hi extends Opcode("scratch_store_short_d16_hi", 1336)

  val definitions: Vec[Opcode] = Vec(
    ClusterLoadAsyncToLdsB128,
    ClusterLoadAsyncToLdsB32,
    ClusterLoadAsyncToLdsB64,
    ClusterLoadAsyncToLdsB8,
    ClusterLoadB128,
    ClusterLoadB32,
    ClusterLoadB64,
    FlatAtomicAdd,
    FlatAtomicAddF32,
    FlatAtomicAddF64,
    FlatAtomicAddU32,
    FlatAtomicAddU64,
    FlatAtomicAddX2,
    FlatAtomicAnd,
    FlatAtomicAndB32,
    FlatAtomicAndB64,
    FlatAtomicAndX2,
    FlatAtomicCmpswap,
    FlatAtomicCmpswapB32,
    FlatAtomicCmpswapB64,
    FlatAtomicCmpswapF32,
    FlatAtomicCmpswapX2,
    FlatAtomicCondSubU32,
    FlatAtomicDec,
    FlatAtomicDecU32,
    FlatAtomicDecU64,
    FlatAtomicDecX2,
    FlatAtomicFcmpswap,
    FlatAtomicFcmpswapX2,
    FlatAtomicFmax,
    FlatAtomicFmaxX2,
    FlatAtomicFmin,
    FlatAtomicFminX2,
    FlatAtomicInc,
    FlatAtomicIncU32,
    FlatAtomicIncU64,
    FlatAtomicIncX2,
    FlatAtomicMaxF32,
    FlatAtomicMaxF64,
    FlatAtomicMaxI32,
    FlatAtomicMaxI64,
    FlatAtomicMaxNumF32,
    FlatAtomicMaxNumF64,
    FlatAtomicMaxU32,
    FlatAtomicMaxU64,
    FlatAtomicMinF32,
    FlatAtomicMinF64,
    FlatAtomicMinI32,
    FlatAtomicMinI64,
    FlatAtomicMinNumF32,
    FlatAtomicMinNumF64,
    FlatAtomicMinU32,
    FlatAtomicMinU64,
    FlatAtomicOr,
    FlatAtomicOrB32,
    FlatAtomicOrB64,
    FlatAtomicOrX2,
    FlatAtomicPkAddBf16,
    FlatAtomicPkAddF16,
    FlatAtomicSmax,
    FlatAtomicSmaxX2,
    FlatAtomicSmin,
    FlatAtomicSminX2,
    FlatAtomicSub,
    FlatAtomicSubClampU32,
    FlatAtomicSubU32,
    FlatAtomicSubU64,
    FlatAtomicSubX2,
    FlatAtomicSwap,
    FlatAtomicSwapB32,
    FlatAtomicSwapB64,
    FlatAtomicSwapX2,
    FlatAtomicUmax,
    FlatAtomicUmaxX2,
    FlatAtomicUmin,
    FlatAtomicUminX2,
    FlatAtomicXor,
    FlatAtomicXorB32,
    FlatAtomicXorB64,
    FlatAtomicXorX2,
    FlatLoadB128,
    FlatLoadB32,
    FlatLoadB64,
    FlatLoadB96,
    FlatLoadD16B16,
    FlatLoadD16HiB16,
    FlatLoadD16HiI8,
    FlatLoadD16HiU8,
    FlatLoadD16I8,
    FlatLoadD16U8,
    FlatLoadDword,
    FlatLoadDwordx2,
    FlatLoadDwordx3,
    FlatLoadDwordx4,
    FlatLoadI16,
    FlatLoadI8,
    FlatLoadMonitorB128,
    FlatLoadMonitorB32,
    FlatLoadMonitorB64,
    FlatLoadSbyte,
    FlatLoadSbyteD16,
    FlatLoadSbyteD16Hi,
    FlatLoadShortD16,
    FlatLoadShortD16Hi,
    FlatLoadSshort,
    FlatLoadU16,
    FlatLoadU8,
    FlatLoadUbyte,
    FlatLoadUbyteD16,
    FlatLoadUbyteD16Hi,
    FlatLoadUshort,
    FlatPrefetchB8,
    FlatStoreB128,
    FlatStoreB16,
    FlatStoreB32,
    FlatStoreB64,
    FlatStoreB8,
    FlatStoreB96,
    FlatStoreByte,
    FlatStoreByteD16Hi,
    FlatStoreD16HiB16,
    FlatStoreD16HiB8,
    FlatStoreDword,
    FlatStoreDwordx2,
    FlatStoreDwordx3,
    FlatStoreDwordx4,
    FlatStoreShort,
    FlatStoreShortD16Hi,
    GlobalAtomicAdd,
    GlobalAtomicAddF32,
    GlobalAtomicAddF64,
    GlobalAtomicAddU32,
    GlobalAtomicAddU64,
    GlobalAtomicAddX2,
    GlobalAtomicAnd,
    GlobalAtomicAndB32,
    GlobalAtomicAndB64,
    GlobalAtomicAndX2,
    GlobalAtomicCmpswap,
    GlobalAtomicCmpswapB32,
    GlobalAtomicCmpswapB64,
    GlobalAtomicCmpswapF32,
    GlobalAtomicCmpswapX2,
    GlobalAtomicCondSubU32,
    GlobalAtomicCsub,
    GlobalAtomicCsubU32,
    GlobalAtomicDec,
    GlobalAtomicDecU32,
    GlobalAtomicDecU64,
    GlobalAtomicDecX2,
    GlobalAtomicFcmpswap,
    GlobalAtomicFcmpswapX2,
    GlobalAtomicFmax,
    GlobalAtomicFmaxX2,
    GlobalAtomicFmin,
    GlobalAtomicFminX2,
    GlobalAtomicInc,
    GlobalAtomicIncU32,
    GlobalAtomicIncU64,
    GlobalAtomicIncX2,
    GlobalAtomicMaxF32,
    GlobalAtomicMaxF64,
    GlobalAtomicMaxI32,
    GlobalAtomicMaxI64,
    GlobalAtomicMaxNumF32,
    GlobalAtomicMaxNumF64,
    GlobalAtomicMaxU32,
    GlobalAtomicMaxU64,
    GlobalAtomicMinF32,
    GlobalAtomicMinF64,
    GlobalAtomicMinI32,
    GlobalAtomicMinI64,
    GlobalAtomicMinNumF32,
    GlobalAtomicMinNumF64,
    GlobalAtomicMinU32,
    GlobalAtomicMinU64,
    GlobalAtomicOr,
    GlobalAtomicOrB32,
    GlobalAtomicOrB64,
    GlobalAtomicOrX2,
    GlobalAtomicOrderedAddB64,
    GlobalAtomicPkAddBf16,
    GlobalAtomicPkAddF16,
    GlobalAtomicSmax,
    GlobalAtomicSmaxX2,
    GlobalAtomicSmin,
    GlobalAtomicSminX2,
    GlobalAtomicSub,
    GlobalAtomicSubClampU32,
    GlobalAtomicSubU32,
    GlobalAtomicSubU64,
    GlobalAtomicSubX2,
    GlobalAtomicSwap,
    GlobalAtomicSwapB32,
    GlobalAtomicSwapB64,
    GlobalAtomicSwapX2,
    GlobalAtomicUmax,
    GlobalAtomicUmaxX2,
    GlobalAtomicUmin,
    GlobalAtomicUminX2,
    GlobalAtomicXor,
    GlobalAtomicXorB32,
    GlobalAtomicXorB64,
    GlobalAtomicXorX2,
    GlobalInv,
    GlobalLoadAddtidB32,
    GlobalLoadAsyncToLdsB128,
    GlobalLoadAsyncToLdsB32,
    GlobalLoadAsyncToLdsB64,
    GlobalLoadAsyncToLdsB8,
    GlobalLoadB128,
    GlobalLoadB32,
    GlobalLoadB64,
    GlobalLoadB96,
    GlobalLoadBlock,
    GlobalLoadD16B16,
    GlobalLoadD16HiB16,
    GlobalLoadD16HiI8,
    GlobalLoadD16HiU8,
    GlobalLoadD16I8,
    GlobalLoadD16U8,
    GlobalLoadDword,
    GlobalLoadDwordAddtid,
    GlobalLoadDwordx2,
    GlobalLoadDwordx3,
    GlobalLoadDwordx4,
    GlobalLoadI16,
    GlobalLoadI8,
    GlobalLoadLdsDword,
    GlobalLoadLdsDwordx3,
    GlobalLoadLdsDwordx4,
    GlobalLoadLdsSbyte,
    GlobalLoadLdsSshort,
    GlobalLoadLdsUbyte,
    GlobalLoadLdsUshort,
    GlobalLoadMonitorB128,
    GlobalLoadMonitorB32,
    GlobalLoadMonitorB64,
    GlobalLoadSbyte,
    GlobalLoadSbyteD16,
    GlobalLoadSbyteD16Hi,
    GlobalLoadShortD16,
    GlobalLoadShortD16Hi,
    GlobalLoadSshort,
    GlobalLoadTr16B128,
    GlobalLoadTr4B64,
    GlobalLoadTr6B96,
    GlobalLoadTr8B64,
    GlobalLoadTrB128,
    GlobalLoadTrB64,
    GlobalLoadU16,
    GlobalLoadU8,
    GlobalLoadUbyte,
    GlobalLoadUbyteD16,
    GlobalLoadUbyteD16Hi,
    GlobalLoadUshort,
    GlobalPrefetchB8,
    GlobalStoreAddtidB32,
    GlobalStoreAsyncFromLdsB128,
    GlobalStoreAsyncFromLdsB32,
    GlobalStoreAsyncFromLdsB64,
    GlobalStoreAsyncFromLdsB8,
    GlobalStoreB128,
    GlobalStoreB16,
    GlobalStoreB32,
    GlobalStoreB64,
    GlobalStoreB8,
    GlobalStoreB96,
    GlobalStoreBlock,
    GlobalStoreByte,
    GlobalStoreByteD16Hi,
    GlobalStoreD16HiB16,
    GlobalStoreD16HiB8,
    GlobalStoreDword,
    GlobalStoreDwordAddtid,
    GlobalStoreDwordx2,
    GlobalStoreDwordx3,
    GlobalStoreDwordx4,
    GlobalStoreShort,
    GlobalStoreShortD16Hi,
    GlobalWb,
    GlobalWbinv,
    ScratchLoadB128,
    ScratchLoadB32,
    ScratchLoadB64,
    ScratchLoadB96,
    ScratchLoadBlock,
    ScratchLoadD16B16,
    ScratchLoadD16HiB16,
    ScratchLoadD16HiI8,
    ScratchLoadD16HiU8,
    ScratchLoadD16I8,
    ScratchLoadD16U8,
    ScratchLoadDword,
    ScratchLoadDwordx2,
    ScratchLoadDwordx3,
    ScratchLoadDwordx4,
    ScratchLoadI16,
    ScratchLoadI8,
    ScratchLoadLdsDword,
    ScratchLoadLdsSbyte,
    ScratchLoadLdsSshort,
    ScratchLoadLdsUbyte,
    ScratchLoadLdsUshort,
    ScratchLoadSbyte,
    ScratchLoadSbyteD16,
    ScratchLoadSbyteD16Hi,
    ScratchLoadShortD16,
    ScratchLoadShortD16Hi,
    ScratchLoadSshort,
    ScratchLoadU16,
    ScratchLoadU8,
    ScratchLoadUbyte,
    ScratchLoadUbyteD16,
    ScratchLoadUbyteD16Hi,
    ScratchLoadUshort,
    ScratchStoreB128,
    ScratchStoreB16,
    ScratchStoreB32,
    ScratchStoreB64,
    ScratchStoreB8,
    ScratchStoreB96,
    ScratchStoreBlock,
    ScratchStoreByte,
    ScratchStoreByteD16Hi,
    ScratchStoreD16HiB16,
    ScratchStoreD16HiB8,
    ScratchStoreDword,
    ScratchStoreDwordx2,
    ScratchStoreDwordx3,
    ScratchStoreDwordx4,
    ScratchStoreShort,
    ScratchStoreShortD16Hi
  )
}
