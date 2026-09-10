.text
.globl kernel
kernel:
 s_mov_b32 s4, 0x3000
 s_mov_b32 s5, 0
 s_mov_b32 s6, 256
 s_mov_b32 s7, 0x00020000
 s_lshl_b32 s8, s2, 4
 v_mov_b32 v0, s8
 buffer_load_dwordx4 v[1:4], v0, s[4:7], 0 offen
 s_waitcnt vmcnt(0)
 s_mov_b32 s10, 0x4000
 s_mov_b32 s11, 0
 global_store_dwordx4 v0, v[1:4], s[10:11]
 s_endpgm
