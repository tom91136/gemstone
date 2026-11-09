# AMDGPU scheduling resource

LLVM 23.1.0 scheduling models joined to the opcode catalogue by exact mnemonic.
Cycles are LLVM estimates from ReleaseAtCycles, not measured issue cycles. Latency is recorded and not used.

In scope: catalogue opcodes with an LLVM record available on the processor, plus opcodes with no LLVM record.

| processor | model | in scope | scheduled | absent | no-model | variant |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| bonaire | SIQuarterSpeedModel | 970 | 932 | 38 | 0 | 0 |
| carrizo | SIQuarterSpeedModel | 1020 | 982 | 38 | 0 | 0 |
| fiji | SIQuarterSpeedModel | 1020 | 982 | 38 | 0 | 0 |
| generic | NoSchedModel | 726 | 0 | 38 | 688 | 0 |
| generic-hsa | NoSchedModel | 726 | 0 | 38 | 688 | 0 |
| gfx10-1-generic | GFX10SpeedModel | 1232 | 1194 | 38 | 0 | 0 |
| gfx10-3-generic | GFX10SpeedModel | 1145 | 1107 | 38 | 0 | 0 |
| gfx1010 | GFX10SpeedModel | 1232 | 1194 | 38 | 0 | 0 |
| gfx1011 | GFX10SpeedModel | 1241 | 1203 | 38 | 0 | 0 |
| gfx1012 | GFX10SpeedModel | 1241 | 1203 | 38 | 0 | 0 |
| gfx1013 | GFX10SpeedModel | 1235 | 1197 | 38 | 0 | 0 |
| gfx1030 | GFX10SpeedModel | 1145 | 1107 | 38 | 0 | 0 |
| gfx1031 | GFX10SpeedModel | 1145 | 1107 | 38 | 0 | 0 |
| gfx1032 | GFX10SpeedModel | 1145 | 1107 | 38 | 0 | 0 |
| gfx1033 | GFX10SpeedModel | 1145 | 1107 | 38 | 0 | 0 |
| gfx1034 | GFX10SpeedModel | 1145 | 1107 | 38 | 0 | 0 |
| gfx1035 | GFX10SpeedModel | 1145 | 1107 | 38 | 0 | 0 |
| gfx1036 | GFX10SpeedModel | 1145 | 1107 | 38 | 0 | 0 |
| gfx11-7-generic | GFX11SpeedModel | 1237 | 1199 | 38 | 0 | 0 |
| gfx11-generic | GFX11SpeedModel | 1141 | 1103 | 38 | 0 | 0 |
| gfx1100 | GFX11SpeedModel | 1141 | 1103 | 38 | 0 | 0 |
| gfx1101 | GFX11SpeedModel | 1141 | 1103 | 38 | 0 | 0 |
| gfx1102 | GFX11SpeedModel | 1141 | 1103 | 38 | 0 | 0 |
| gfx1103 | GFX11SpeedModel | 1141 | 1103 | 38 | 0 | 0 |
| gfx1150 | GFX11SpeedModel | 1199 | 1161 | 38 | 0 | 0 |
| gfx1151 | GFX11SpeedModel | 1199 | 1161 | 38 | 0 | 0 |
| gfx1152 | GFX11SpeedModel | 1199 | 1161 | 38 | 0 | 0 |
| gfx1153 | GFX11SpeedModel | 1199 | 1161 | 38 | 0 | 0 |
| gfx1154 | GFX11SpeedModel | 1199 | 1161 | 38 | 0 | 0 |
| gfx1170 | GFX11SpeedModel | 1237 | 1199 | 38 | 0 | 0 |
| gfx1171 | GFX11SpeedModel | 1237 | 1199 | 38 | 0 | 0 |
| gfx1172 | GFX11SpeedModel | 1237 | 1199 | 38 | 0 | 0 |
| gfx12 | GFX12SpeedModel | 1245 | 1207 | 38 | 0 | 0 |
| gfx12-5-generic | GFX125xGenericSpeedModel | 1313 | 1272 | 38 | 0 | 3 |
| gfx12-generic | GFX12SpeedModel | 1245 | 1207 | 38 | 0 | 0 |
| gfx1200 | GFX12SpeedModel | 1245 | 1207 | 38 | 0 | 0 |
| gfx1201 | GFX12SpeedModel | 1245 | 1207 | 38 | 0 | 0 |
| gfx1250 | GFX1250SpeedModel | 1357 | 1316 | 38 | 0 | 3 |
| gfx1251 | GFX1251SpeedModel | 1366 | 1325 | 38 | 0 | 3 |
| gfx13-generic | GFX12SpeedModel | 1277 | 1239 | 38 | 0 | 0 |
| gfx1310 | GFX12SpeedModel | 1277 | 1239 | 38 | 0 | 0 |
| gfx600 | SIFullSpeedModel | 905 | 867 | 38 | 0 | 0 |
| gfx601 | SIQuarterSpeedModel | 905 | 867 | 38 | 0 | 0 |
| gfx602 | SIQuarterSpeedModel | 905 | 867 | 38 | 0 | 0 |
| gfx700 | SIQuarterSpeedModel | 970 | 932 | 38 | 0 | 0 |
| gfx701 | SIFullSpeedModel | 970 | 932 | 38 | 0 | 0 |
| gfx702 | SIQuarterSpeedModel | 970 | 932 | 38 | 0 | 0 |
| gfx703 | SIQuarterSpeedModel | 970 | 932 | 38 | 0 | 0 |
| gfx704 | SIQuarterSpeedModel | 970 | 932 | 38 | 0 | 0 |
| gfx705 | SIQuarterSpeedModel | 970 | 932 | 38 | 0 | 0 |
| gfx801 | SIQuarterSpeedModel | 1020 | 982 | 38 | 0 | 0 |
| gfx802 | SIQuarterSpeedModel | 1020 | 982 | 38 | 0 | 0 |
| gfx803 | SIQuarterSpeedModel | 1020 | 982 | 38 | 0 | 0 |
| gfx805 | SIQuarterSpeedModel | 1020 | 982 | 38 | 0 | 0 |
| gfx810 | SIQuarterSpeedModel | 1028 | 990 | 38 | 0 | 0 |
| gfx9-4-generic | SIDPGFX942FullSpeedModel | 1224 | 1186 | 38 | 0 | 0 |
| gfx9-generic | SIQuarterSpeedModel | 1268 | 1230 | 38 | 0 | 0 |
| gfx900 | SIQuarterSpeedModel | 1271 | 1233 | 38 | 0 | 0 |
| gfx902 | SIQuarterSpeedModel | 1271 | 1233 | 38 | 0 | 0 |
| gfx904 | SIQuarterSpeedModel | 1271 | 1233 | 38 | 0 | 0 |
| gfx906 | SIQuarterSpeedModel | 1280 | 1242 | 38 | 0 | 0 |
| gfx908 | SIQuarterSpeedModel | 1311 | 1273 | 38 | 0 | 0 |
| gfx909 | SIQuarterSpeedModel | 1271 | 1233 | 38 | 0 | 0 |
| gfx90a | SIDPFullSpeedModel | 1234 | 1196 | 38 | 0 | 0 |
| gfx90c | SIQuarterSpeedModel | 1271 | 1233 | 38 | 0 | 0 |
| gfx942 | SIDPGFX942FullSpeedModel | 1250 | 1212 | 38 | 0 | 0 |
| gfx950 | SIDPGFX950FullSpeedModel | 1347 | 1305 | 38 | 0 | 4 |
| hainan | SIQuarterSpeedModel | 905 | 867 | 38 | 0 | 0 |
| hawaii | SIFullSpeedModel | 970 | 932 | 38 | 0 | 0 |
| iceland | SIQuarterSpeedModel | 1020 | 982 | 38 | 0 | 0 |
| kabini | SIQuarterSpeedModel | 970 | 932 | 38 | 0 | 0 |
| kaveri | SIQuarterSpeedModel | 970 | 932 | 38 | 0 | 0 |
| mullins | SIQuarterSpeedModel | 970 | 932 | 38 | 0 | 0 |
| oland | SIQuarterSpeedModel | 905 | 867 | 38 | 0 | 0 |
| pitcairn | SIQuarterSpeedModel | 905 | 867 | 38 | 0 | 0 |
| polaris10 | SIQuarterSpeedModel | 1020 | 982 | 38 | 0 | 0 |
| polaris11 | SIQuarterSpeedModel | 1020 | 982 | 38 | 0 | 0 |
| stoney | SIQuarterSpeedModel | 1028 | 990 | 38 | 0 | 0 |
| tahiti | SIFullSpeedModel | 905 | 867 | 38 | 0 | 0 |
| tonga | SIQuarterSpeedModel | 1020 | 982 | 38 | 0 | 0 |
| tongapro | SIQuarterSpeedModel | 1020 | 982 | 38 | 0 | 0 |
| verde | SIQuarterSpeedModel | 905 | 867 | 38 | 0 | 0 |

## Unmatched

- `absent` for all processors (38): `buffer_atomic_rsub`, `buffer_atomic_rsub_x2`, `ds_condxchg32_rtn_b128`, `image_gather4h_pck`, `image_gather8h_pck`, `image_load_by2`, `image_load_by4`, `image_load_mip_by2`, `image_load_mip_by4`, `image_load_mip_pck2`, `image_load_mip_pck4`, `image_load_pck2`, `image_load_pck4`, `image_store_by2`, `image_store_by4`, `image_store_mip_by2`, `image_store_mip_by4`, `image_store_mip_pck2`, `image_store_mip_pck4`, `image_store_pck2`, `image_store_pck4`, `s_mov_fed_b32`, `s_set_valu_coexec_mode`, `s_singleuse_vdst`, `v_accvgpr_read`, `v_accvgpr_write`, `v_cmpx_t_f64`, `v_dual_and_b32`, `v_dual_bitop2_b32`, `v_dual_max_i32`, `v_dual_min_i32`, `v_interp_p10_f16_f32_inreg`, `v_interp_p10_f32_inreg`, `v_interp_p10_rtz_f16_f32_inreg`, `v_interp_p2_f16_f32_inreg`, `v_interp_p2_f32_inreg`, `v_interp_p2_hi_f16`, `v_interp_p2_rtz_f16_f32_inreg`
- `no-model` for generic, generic-hsa (688): `buffer_atomic_add`, `buffer_atomic_add_u32`, `buffer_atomic_add_u64`, `buffer_atomic_add_x2`, `buffer_atomic_and`, `buffer_atomic_and_b32`, `buffer_atomic_and_b64`, `buffer_atomic_and_x2`, `buffer_atomic_cmpswap`, `buffer_atomic_cmpswap_b32`, `buffer_atomic_cmpswap_b64`, `buffer_atomic_cmpswap_x2`, `buffer_atomic_dec`, `buffer_atomic_dec_u32`, `buffer_atomic_dec_u64`, `buffer_atomic_dec_x2`, `buffer_atomic_fcmpswap`, `buffer_atomic_fcmpswap_x2`, `buffer_atomic_inc`, `buffer_atomic_inc_u32`, `buffer_atomic_inc_u64`, `buffer_atomic_inc_x2`, `buffer_atomic_max_i32`, `buffer_atomic_max_i64`, `buffer_atomic_max_u32`, `buffer_atomic_max_u64`, `buffer_atomic_min_i32`, `buffer_atomic_min_i64`, `buffer_atomic_min_u32`, `buffer_atomic_min_u64`, `buffer_atomic_or`, `buffer_atomic_or_b32`, `buffer_atomic_or_b64`, `buffer_atomic_or_x2`, `buffer_atomic_smax`, `buffer_atomic_smax_x2`, `buffer_atomic_smin`, `buffer_atomic_smin_x2`, `buffer_atomic_sub`, `buffer_atomic_sub_u32`, `buffer_atomic_sub_u64`, `buffer_atomic_sub_x2`, `buffer_atomic_swap`, `buffer_atomic_swap_b32`, `buffer_atomic_swap_b64`, `buffer_atomic_swap_x2`, `buffer_atomic_umax`, `buffer_atomic_umax_x2`, `buffer_atomic_umin`, `buffer_atomic_umin_x2`, `buffer_atomic_xor`, `buffer_atomic_xor_b32`, `buffer_atomic_xor_b64`, `buffer_atomic_xor_x2`, `buffer_load_b128`, `buffer_load_b32`, `buffer_load_b64`, `buffer_load_b96`, `buffer_load_dword`, `buffer_load_dwordx2`, `buffer_load_dwordx3`, `buffer_load_dwordx4`, `buffer_load_i16`, `buffer_load_i8`, `buffer_load_sbyte`, `buffer_load_sshort`, `buffer_load_u16`, `buffer_load_u8`, `buffer_load_ubyte`, `buffer_load_ushort`, `buffer_store_b128`, `buffer_store_b16`, `buffer_store_b32`, `buffer_store_b64`, `buffer_store_b8`, `buffer_store_b96`, `buffer_store_byte`, `buffer_store_dword`, `buffer_store_dwordx2`, `buffer_store_dwordx3`, `buffer_store_dwordx4`, `buffer_store_short`, `buffer_wbinvl1`, `ds_add_rtn_u32`, `ds_add_rtn_u64`, `ds_add_u32`, `ds_add_u64`, `ds_and_b32`, `ds_and_b64`, `ds_and_rtn_b32`, `ds_and_rtn_b64`, `ds_append`, `ds_cmpst_b32`, `ds_cmpst_b64`, `ds_cmpst_f32`, `ds_cmpst_f64`, `ds_cmpst_rtn_b32`, `ds_cmpst_rtn_b64`, `ds_cmpst_rtn_f32`, `ds_cmpst_rtn_f64`, `ds_consume`, `ds_dec_rtn_u32`, `ds_dec_rtn_u64`, `ds_dec_u32`, `ds_dec_u64`, `ds_gws_barrier`, `ds_gws_init`, `ds_gws_sema_br`, `ds_gws_sema_p`, `ds_gws_sema_v`, `ds_inc_rtn_u32`, `ds_inc_rtn_u64`, `ds_inc_u32`, `ds_inc_u64`, `ds_max_f32`, `ds_max_f64`, `ds_max_i32`, `ds_max_i64`, `ds_max_rtn_f32`, `ds_max_rtn_f64`, `ds_max_rtn_i32`, `ds_max_rtn_i64`, `ds_max_rtn_u32`, `ds_max_rtn_u64`, `ds_max_u32`, `ds_max_u64`, `ds_min_f32`, `ds_min_f64`, `ds_min_i32`, `ds_min_i64`, `ds_min_rtn_f32`, `ds_min_rtn_f64`, `ds_min_rtn_i32`, `ds_min_rtn_i64`, `ds_min_rtn_u32`, `ds_min_rtn_u64`, `ds_min_u32`, `ds_min_u64`, `ds_mskor_b32`, `ds_mskor_b64`, `ds_mskor_rtn_b32`, `ds_mskor_rtn_b64`, `ds_or_b32`, `ds_or_b64`, `ds_or_rtn_b32`, `ds_or_rtn_b64`, `ds_ordered_count`, `ds_read2_b32`, `ds_read2_b64`, `ds_read2st64_b32`, `ds_read2st64_b64`, `ds_read_b32`, `ds_read_b64`, `ds_read_i16`, `ds_read_i8`, `ds_read_u16`, `ds_read_u8`, `ds_rsub_rtn_u32`, `ds_rsub_rtn_u64`, `ds_rsub_u32`, `ds_rsub_u64`, `ds_sub_rtn_u32`, `ds_sub_rtn_u64`, `ds_sub_u32`, `ds_sub_u64`, `ds_swizzle_b32`, `ds_write2_b32`, `ds_write2_b64`, `ds_write2st64_b32`, `ds_write2st64_b64`, `ds_write_b16`, `ds_write_b32`, `ds_write_b64`, `ds_write_b8`, `ds_wrxchg2_rtn_b32`, `ds_wrxchg2_rtn_b64`, `ds_wrxchg2st64_rtn_b32`, `ds_wrxchg2st64_rtn_b64`, `ds_wrxchg_rtn_b32`, `ds_wrxchg_rtn_b64`, `ds_xor_b32`, `ds_xor_b64`, `ds_xor_rtn_b32`, `ds_xor_rtn_b64`, `exp`, `s_abs_i32`, `s_absdiff_i32`, `s_add_i32`, `s_add_u32`, `s_addc_u32`, `s_addk_i32`, `s_and_b32`, `s_and_b64`, `s_and_saveexec_b64`, `s_andn2_b32`, `s_andn2_b64`, `s_andn2_saveexec_b64`, `s_ashr_i32`, `s_ashr_i64`, `s_barrier`, `s_bcnt0_i32_b32`, `s_bcnt0_i32_b64`, `s_bcnt1_i32_b32`, `s_bcnt1_i32_b64`, `s_bfe_i32`, `s_bfe_i64`, `s_bfe_u32`, `s_bfe_u64`, `s_bfm_b32`, `s_bfm_b64`, `s_bitcmp0_b32`, `s_bitcmp0_b64`, `s_bitcmp1_b32`, `s_bitcmp1_b64`, `s_bitset0_b32`, `s_bitset0_b64`, `s_bitset1_b32`, `s_bitset1_b64`, `s_branch`, `s_brev_b32`, `s_brev_b64`, `s_buffer_load_dword`, `s_buffer_load_dwordx16`, `s_buffer_load_dwordx2`, `s_buffer_load_dwordx4`, `s_buffer_load_dwordx8`, `s_cbranch_cdbgsys`, `s_cbranch_cdbgsys_and_user`, `s_cbranch_cdbgsys_or_user`, `s_cbranch_cdbguser`, `s_cbranch_execnz`, `s_cbranch_execz`, `s_cbranch_g_fork`, `s_cbranch_i_fork`, `s_cbranch_join`, `s_cbranch_scc0`, `s_cbranch_scc1`, `s_cbranch_vccnz`, `s_cbranch_vccz`, `s_cmov_b32`, `s_cmov_b64`, `s_cmovk_i32`, `s_cmp_eq_i32`, `s_cmp_eq_u32`, `s_cmp_ge_i32`, `s_cmp_ge_u32`, `s_cmp_gt_i32`, `s_cmp_gt_u32`, `s_cmp_le_i32`, `s_cmp_le_u32`, `s_cmp_lg_i32`, `s_cmp_lg_u32`, `s_cmp_lt_i32`, `s_cmp_lt_u32`, `s_cmpk_eq_i32`, `s_cmpk_eq_u32`, `s_cmpk_ge_i32`, `s_cmpk_ge_u32`, `s_cmpk_gt_i32`, `s_cmpk_gt_u32`, `s_cmpk_le_i32`, `s_cmpk_le_u32`, `s_cmpk_lg_i32`, `s_cmpk_lg_u32`, `s_cmpk_lt_i32`, `s_cmpk_lt_u32`, `s_cselect_b32`, `s_cselect_b64`, `s_dcache_inv`, `s_decperflevel`, `s_endpgm`, `s_ff0_i32_b32`, `s_ff0_i32_b64`, `s_ff1_i32_b32`, `s_ff1_i32_b64`, `s_flbit_i32`, `s_flbit_i32_b32`, `s_flbit_i32_b64`, `s_flbit_i32_i64`, `s_getpc_b64`, `s_getreg_b32`, `s_icache_inv`, `s_incperflevel`, `s_load_dword`, `s_load_dwordx16`, `s_load_dwordx2`, `s_load_dwordx4`, `s_load_dwordx8`, `s_lshl_b32`, `s_lshl_b64`, `s_lshr_b32`, `s_lshr_b64`, `s_max_i32`, `s_max_u32`, `s_min_i32`, `s_min_u32`, `s_mov_b32`, `s_mov_b64`, `s_movk_i32`, `s_movreld_b32`, `s_movreld_b64`, `s_movrels_b32`, `s_movrels_b64`, `s_mul_i32`, `s_mulk_i32`, `s_nand_b32`, `s_nand_b64`, `s_nand_saveexec_b64`, `s_nop`, `s_nor_b32`, `s_nor_b64`, `s_nor_saveexec_b64`, `s_not_b32`, `s_not_b64`, `s_or_b32`, `s_or_b64`, `s_or_saveexec_b64`, `s_orn2_b32`, `s_orn2_b64`, `s_orn2_saveexec_b64`, `s_quadmask_b32`, `s_quadmask_b64`, `s_rfe_b64`, `s_sendmsg`, `s_sendmsghalt`, `s_sethalt`, `s_setkill`, `s_setpc_b64`, `s_setprio`, `s_setreg_b32`, `s_setreg_imm32_b32`, `s_setvskip`, `s_sext_i32_i16`, `s_sext_i32_i8`, `s_sleep`, `s_sub_i32`, `s_sub_u32`, `s_subb_u32`, `s_swappc_b64`, `s_trap`, `s_ttracedata`, `s_waitcnt`, `s_wqm_b32`, `s_wqm_b64`, `s_xnor_b32`, `s_xnor_b64`, `s_xnor_saveexec_b64`, `s_xor_b32`, `s_xor_b64`, `s_xor_saveexec_b64`, `v_add_f32`, `v_add_f64`, `v_add_i32`, `v_addc_u32`, `v_alignbit_b32`, `v_alignbyte_b32`, `v_and_b32`, `v_ashr_i32`, `v_ashr_i64`, `v_ashrrev_i32`, `v_bcnt_u32_b32`, `v_bfe_i32`, `v_bfe_u32`, `v_bfi_b32`, `v_bfm_b32`, `v_bfrev_b32`, `v_ceil_f32`, `v_clrexcp`, `v_cmp_class_f32`, `v_cmp_class_f64`, `v_cmp_eq_f32`, `v_cmp_eq_f64`, `v_cmp_eq_i32`, `v_cmp_eq_i64`, `v_cmp_eq_u32`, `v_cmp_eq_u64`, `v_cmp_f_f32`, `v_cmp_f_f64`, `v_cmp_f_i32`, `v_cmp_f_i64`, `v_cmp_f_u32`, `v_cmp_f_u64`, `v_cmp_ge_f32`, `v_cmp_ge_f64`, `v_cmp_ge_i32`, `v_cmp_ge_i64`, `v_cmp_ge_u32`, `v_cmp_ge_u64`, `v_cmp_gt_f32`, `v_cmp_gt_f64`, `v_cmp_gt_i32`, `v_cmp_gt_i64`, `v_cmp_gt_u32`, `v_cmp_gt_u64`, `v_cmp_le_f32`, `v_cmp_le_f64`, `v_cmp_le_i32`, `v_cmp_le_i64`, `v_cmp_le_u32`, `v_cmp_le_u64`, `v_cmp_lg_f32`, `v_cmp_lg_f64`, `v_cmp_lt_f32`, `v_cmp_lt_f64`, `v_cmp_lt_i32`, `v_cmp_lt_i64`, `v_cmp_lt_u32`, `v_cmp_lt_u64`, `v_cmp_ne_i32`, `v_cmp_ne_i64`, `v_cmp_ne_u32`, `v_cmp_ne_u64`, `v_cmp_neq_f32`, `v_cmp_neq_f64`, `v_cmp_nge_f32`, `v_cmp_nge_f64`, `v_cmp_ngt_f32`, `v_cmp_ngt_f64`, `v_cmp_nle_f32`, `v_cmp_nle_f64`, `v_cmp_nlg_f32`, `v_cmp_nlg_f64`, `v_cmp_nlt_f32`, `v_cmp_nlt_f64`, `v_cmp_o_f32`, `v_cmp_o_f64`, `v_cmp_t_i32`, `v_cmp_t_i64`, `v_cmp_t_u32`, `v_cmp_t_u64`, `v_cmp_tru_f32`, `v_cmp_tru_f64`, `v_cmp_u_f32`, `v_cmp_u_f64`, `v_cmps_eq_f32`, `v_cmps_eq_f64`, `v_cmps_f_f32`, `v_cmps_f_f64`, `v_cmps_ge_f32`, `v_cmps_ge_f64`, `v_cmps_gt_f32`, `v_cmps_gt_f64`, `v_cmps_le_f32`, `v_cmps_le_f64`, `v_cmps_lg_f32`, `v_cmps_lg_f64`, `v_cmps_lt_f32`, `v_cmps_lt_f64`, `v_cmps_neq_f32`, `v_cmps_neq_f64`, `v_cmps_nge_f32`, `v_cmps_nge_f64`, `v_cmps_ngt_f32`, `v_cmps_ngt_f64`, `v_cmps_nle_f32`, `v_cmps_nle_f64`, `v_cmps_nlg_f32`, `v_cmps_nlg_f64`, `v_cmps_nlt_f32`, `v_cmps_nlt_f64`, `v_cmps_o_f32`, `v_cmps_o_f64`, `v_cmps_tru_f32`, `v_cmps_tru_f64`, `v_cmps_u_f32`, `v_cmps_u_f64`, `v_cmpsx_eq_f32`, `v_cmpsx_eq_f64`, `v_cmpsx_f_f32`, `v_cmpsx_f_f64`, `v_cmpsx_ge_f32`, `v_cmpsx_ge_f64`, `v_cmpsx_gt_f32`, `v_cmpsx_gt_f64`, `v_cmpsx_le_f32`, `v_cmpsx_le_f64`, `v_cmpsx_lg_f32`, `v_cmpsx_lg_f64`, `v_cmpsx_lt_f32`, `v_cmpsx_lt_f64`, `v_cmpsx_neq_f32`, `v_cmpsx_neq_f64`, `v_cmpsx_nge_f32`, `v_cmpsx_nge_f64`, `v_cmpsx_ngt_f32`, `v_cmpsx_ngt_f64`, `v_cmpsx_nle_f32`, `v_cmpsx_nle_f64`, `v_cmpsx_nlg_f32`, `v_cmpsx_nlg_f64`, `v_cmpsx_nlt_f32`, `v_cmpsx_nlt_f64`, `v_cmpsx_o_f32`, `v_cmpsx_o_f64`, `v_cmpsx_tru_f32`, `v_cmpsx_tru_f64`, `v_cmpsx_u_f32`, `v_cmpsx_u_f64`, `v_cmpx_class_f32`, `v_cmpx_class_f64`, `v_cmpx_eq_f32`, `v_cmpx_eq_f64`, `v_cmpx_eq_i32`, `v_cmpx_eq_i64`, `v_cmpx_eq_u32`, `v_cmpx_eq_u64`, `v_cmpx_f_f32`, `v_cmpx_f_f64`, `v_cmpx_f_i32`, `v_cmpx_f_i64`, `v_cmpx_f_u32`, `v_cmpx_f_u64`, `v_cmpx_ge_f32`, `v_cmpx_ge_f64`, `v_cmpx_ge_i32`, `v_cmpx_ge_i64`, `v_cmpx_ge_u32`, `v_cmpx_ge_u64`, `v_cmpx_gt_f32`, `v_cmpx_gt_f64`, `v_cmpx_gt_i32`, `v_cmpx_gt_i64`, `v_cmpx_gt_u32`, `v_cmpx_gt_u64`, `v_cmpx_le_f32`, `v_cmpx_le_f64`, `v_cmpx_le_i32`, `v_cmpx_le_i64`, `v_cmpx_le_u32`, `v_cmpx_le_u64`, `v_cmpx_lg_f32`, `v_cmpx_lg_f64`, `v_cmpx_lt_f32`, `v_cmpx_lt_f64`, `v_cmpx_lt_i32`, `v_cmpx_lt_i64`, `v_cmpx_lt_u32`, `v_cmpx_lt_u64`, `v_cmpx_ne_i32`, `v_cmpx_ne_i64`, `v_cmpx_ne_u32`, `v_cmpx_ne_u64`, `v_cmpx_neq_f32`, `v_cmpx_neq_f64`, `v_cmpx_nge_f32`, `v_cmpx_nge_f64`, `v_cmpx_ngt_f32`, `v_cmpx_ngt_f64`, `v_cmpx_nle_f32`, `v_cmpx_nle_f64`, `v_cmpx_nlg_f32`, `v_cmpx_nlg_f64`, `v_cmpx_nlt_f32`, `v_cmpx_nlt_f64`, `v_cmpx_o_f32`, `v_cmpx_o_f64`, `v_cmpx_t_i32`, `v_cmpx_t_i64`, `v_cmpx_t_u32`, `v_cmpx_t_u64`, `v_cmpx_tru_f32`, `v_cmpx_tru_f64`, `v_cmpx_u_f32`, `v_cmpx_u_f64`, `v_cndmask_b32`, `v_cos_f32`, `v_cvt_f16_f32`, `v_cvt_f32_f16`, `v_cvt_f32_f64`, `v_cvt_f32_i32`, `v_cvt_f32_u32`, `v_cvt_f32_ubyte0`, `v_cvt_f32_ubyte1`, `v_cvt_f32_ubyte2`, `v_cvt_f32_ubyte3`, `v_cvt_f64_f32`, `v_cvt_f64_i32`, `v_cvt_f64_u32`, `v_cvt_flr_i32_f32`, `v_cvt_i32_f32`, `v_cvt_i32_f64`, `v_cvt_off_f32_i4`, `v_cvt_pk_i16_i32`, `v_cvt_pk_u16_u32`, `v_cvt_pk_u8_f32`, `v_cvt_pkaccum_u8_f32`, `v_cvt_pkrtz_f16_f32`, `v_cvt_rpi_i32_f32`, `v_cvt_u32_f32`, `v_cvt_u32_f64`, `v_div_fixup_f32`, `v_div_fixup_f64`, `v_div_fmas_f32`, `v_div_fmas_f64`, `v_div_scale_f32`, `v_div_scale_f64`, `v_exp_f32`, `v_ffbh_i32`, `v_ffbh_u32`, `v_ffbl_b32`, `v_floor_f32`, `v_fma_f32`, `v_fma_f64`, `v_fract_f32`, `v_fract_f64`, `v_frexp_exp_i32_f32`, `v_frexp_exp_i32_f64`, `v_frexp_mant_f32`, `v_frexp_mant_f64`, `v_interp_mov_f32`, `v_interp_p1_f32`, `v_interp_p2_f32`, `v_ldexp_f32`, `v_ldexp_f64`, `v_log_clamp_f32`, `v_log_f32`, `v_lshl_b32`, `v_lshl_b64`, `v_lshlrev_b32`, `v_lshr_b32`, `v_lshr_b64`, `v_lshrrev_b32`, `v_mad_i32_i24`, `v_mad_u32_u24`, `v_max3_f32`, `v_max3_i32`, `v_max3_u32`, `v_max_f32`, `v_max_f64`, `v_max_i32`, `v_max_legacy_f32`, `v_max_u32`, `v_mbcnt_hi_u32_b32`, `v_mbcnt_lo_u32_b32`, `v_med3_f32`, `v_med3_i32`, `v_med3_u32`, `v_min3_f32`, `v_min3_i32`, `v_min3_u32`, `v_min_f32`, `v_min_f64`, `v_min_i32`, `v_min_legacy_f32`, `v_min_u32`, `v_mov_b32`, `v_mul_f32`, `v_mul_f64`, `v_mul_hi_i32`, `v_mul_hi_i32_i24`, `v_mul_hi_u32`, `v_mul_hi_u32_u24`, `v_mul_i32_i24`, `v_mul_legacy_f32`, `v_mul_lo_i32`, `v_mul_lo_u32`, `v_mul_u32_u24`, `v_mullit_f32`, `v_nop`, `v_not_b32`, `v_or_b32`, `v_rcp_clamp_f32`, `v_rcp_clamp_f64`, `v_rcp_f32`, `v_rcp_f64`, `v_rcp_iflag_f32`, `v_rcp_legacy_f32`, `v_readfirstlane_b32`, `v_readlane_b32`, `v_rndne_f32`, `v_rsq_clamp_f32`, `v_rsq_clamp_f64`, `v_rsq_f32`, `v_rsq_f64`, `v_rsq_legacy_f32`, `v_sin_f32`, `v_sqrt_f32`, `v_sqrt_f64`, `v_sub_f32`, `v_sub_i32`, `v_subb_u32`, `v_subbrev_u32`, `v_subrev_f32`, `v_subrev_i32`, `v_trig_preop_f64`, `v_trunc_f32`, `v_writelane_b32`, `v_xor_b32`
- `variant` for gfx12-5-generic, gfx1250, gfx1251 (3): `v_wmma_f32_16x16x128_f8f6f4`, `v_wmma_scale16_f32_16x16x128_f8f6f4`, `v_wmma_scale_f32_16x16x128_f8f6f4`
- `variant` for gfx950 (4): `v_mfma_f32_16x16x128_f8f6f4`, `v_mfma_f32_32x32x64_f8f6f4`, `v_mfma_scale_f32_16x16x128_f8f6f4`, `v_mfma_scale_f32_32x32x64_f8f6f4`
