s_load_dwordx4 s[4:7], s[0:1], 0
s_load_dwordx2 s[8:9], s[0:1], 16
s_load_dword s10, s[0:1], 24
s_waitcnt lgkmcnt(0)
s_mul_i32 s3, s2, s9
v_add_nc_u32 v1, s3, v0
v_cmp_lt_u32 vcc_lo, v1, s8
s_and_saveexec_b32 s11, vcc_lo
v_add_nc_u32 v3, s10, v1
v_lshlrev_b32 v2, 2, v0
ds_write_b32 v2, v3
v_cmp_eq_u32 vcc_lo, 0, v0
s_and_saveexec_b32 s12, vcc_lo
v_mov_b32 v6, s2
v_lshlrev_b32 v6, 2, v6
global_store_dword v6, v3, s[6:7]
s_mov_b32 exec_lo, s12
s_barrier
ds_read_b32 v4, v2
s_waitcnt_lgkmcnt null, 0
v_lshlrev_b32 v5, 2, v1
global_store_dword v5, v4, s[4:5]
s_endpgm
