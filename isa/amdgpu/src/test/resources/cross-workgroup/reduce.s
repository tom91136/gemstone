s_load_dwordx4 s[4:7], s[0:1], 0
s_load_dwordx4 s[8:11], s[0:1], 16
s_load_dwordx2 s[12:13], s[0:1], 32
s_waitcnt lgkmcnt(0)
v_cmp_eq_u32 vcc_lo, 0, v0
s_and_saveexec_b32 s14, vcc_lo
s_mul_i32 s3, s2, s13
s_sub_u32 s12, s12, s3
s_min_u32 s12, s12, s13
v_mov_b32 v1, s3
v_lshlrev_b32 v1, 2, v1
v_mov_b32 v2, 0
.Lloop:
global_load_dword v3, v1, s[4:5]
s_waitcnt vmcnt(0)
v_add_nc_u32 v2, v2, v3
v_add_nc_u32 v1, 4, v1
s_sub_u32 s12, s12, 1
s_cmp_lg_u32 s12, 0
s_cbranch_scc1 .Lloop
v_mov_b32 v4, s2
v_lshlrev_b32 v4, 2, v4
global_store_dword v4, v2, s[6:7]
v_mov_b32 v5, 0
v_mov_b32 v6, 1
global_atomic_add v7, v5, v6, s[8:9] glc
global_store_dword v4, v7, s[10:11]
s_endpgm
