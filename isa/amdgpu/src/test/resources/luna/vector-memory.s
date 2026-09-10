.text
.globl kernel
kernel:
 s_load_dwordx4 s[4:7], s[0:1], 0
 s_waitcnt lgkmcnt(0)
 v_lshlrev_b32 v0, 4, v0
 global_load_dwordx4 v[1:4], v0, s[4:5]
 s_waitcnt vmcnt(0)
 global_store_dwordx4 v0, v[1:4], s[6:7]
 s_endpgm
