// Assembled with LLVM 22.1.8; binary hex is retained in AtomicContractCode.scala.
// llvm-mc -triple=amdgcn-amd-amdhsa -mcpu=gfx1030 -filetype=obj
// --defsym=WIDE={0,1} --defsym=RETURN={0,1} --defsym=OP={0:add,1:cas,2:inc,3:flatcas}
// llvm-objcopy --only-section=.text -O binary kernel.o {op}-{width}-{return}.bin
.text
s_load_dwordx2 s[8:9], s[0:1], 0
s_load_dwordx4 s[12:15], s[0:1], 8
s_waitcnt lgkmcnt(0)
v_mov_b32 v0, 0x12345678
v_mov_b32 v1, 0x76543210
v_mov_b32 v2, 0
v_mov_b32 v4, s12
.if WIDE
v_mov_b32 v5, s13
.else
v_mov_b32 v5, s14
.endif
v_mov_b32 v6, s14
v_mov_b32 v7, s15
.if OP == 3
v_mov_b32 v2, s8
v_mov_b32 v3, s9
.if WIDE
.if RETURN
flat_atomic_cmpswap_x2 v[0:1], v[2:3], v[4:7] glc
.else
flat_atomic_cmpswap_x2 v[2:3], v[4:7]
.endif
.else
.if RETURN
flat_atomic_cmpswap v0, v[2:3], v[4:5] glc
.else
flat_atomic_cmpswap v[2:3], v[4:5]
.endif
.endif
.else
.if OP == 0
.if WIDE
.if RETURN
global_atomic_add_x2 v[0:1], v2, v[4:5], s[8:9] glc
.else
global_atomic_add_x2 v2, v[4:5], s[8:9]
.endif
.else
.if RETURN
global_atomic_add v0, v2, v4, s[8:9] glc
.else
global_atomic_add v2, v4, s[8:9]
.endif
.endif
.elseif OP == 1
.if WIDE
.if RETURN
global_atomic_cmpswap_x2 v[0:1], v2, v[4:7], s[8:9] glc
.else
global_atomic_cmpswap_x2 v2, v[4:7], s[8:9]
.endif
.else
.if RETURN
global_atomic_cmpswap v0, v2, v[4:5], s[8:9] glc
.else
global_atomic_cmpswap v2, v[4:5], s[8:9]
.endif
.endif
.else
.if WIDE
.if RETURN
global_atomic_inc_x2 v[0:1], v2, v[4:5], s[8:9] glc
.else
global_atomic_inc_x2 v2, v[4:5], s[8:9]
.endif
.else
.if RETURN
global_atomic_inc v0, v2, v4, s[8:9] glc
.else
global_atomic_inc v2, v4, s[8:9]
.endif
.endif
.endif
.endif
s_waitcnt vmcnt(0)
v_mov_b32 v2, s2
v_lshlrev_b32 v2, 3, v2
v_add_nc_u32 v2, 16, v2
global_store_dwordx2 v2, v[0:1], s[8:9]
s_endpgm
