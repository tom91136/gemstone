s_load_dwordx4 s[4:7], s[0:1], 0
s_waitcnt lgkmcnt(0)
v_cmp_eq_u32 vcc_lo, 0, v0
s_and_saveexec_b32 s8, vcc_lo
s_add_u32 s3, s2, 1
v_mov_b32 v1, 0
v_mov_b32 v2, 0
.Lloop:
global_load_dword v3, v1, s[4:5]
s_waitcnt vmcnt(0)
v_add_nc_u32 v2, v2, v3
v_add_nc_u32 v1, 4, v1
s_sub_u32 s3, s3, 1
s_cmp_lg_u32 s3, 0
s_cbranch_scc1 .Lloop
v_mov_b32 v4, s2
v_lshlrev_b32 v4, 2, v4
global_store_dword v4, v2, s[6:7]
s_endpgm
