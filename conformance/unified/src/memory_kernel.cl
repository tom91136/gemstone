// Keep the 32-bit fixture usable on devices without 64-bit atomic support.
#ifdef UNIFIED_MEMORY_ATOMIC32
typedef uint WideWord;
typedef atomic_uint atomic_WideWord;
#else
typedef ulong WideWord;
typedef atomic_ulong atomic_WideWord;
#endif
// CMake generates the test images in the build tree using pinned container toolchains.
typedef struct Node { __global struct Node *next; uint value; uint padding[13]; } Node;
inline void system_fence() {
#ifdef __AMDGCN__
  // LLVM's system scope is the empty string.
  __builtin_amdgcn_fence(__ATOMIC_ACQ_REL, "");
#else
  atomic_work_item_fence(CLK_GLOBAL_MEM_FENCE, memory_order_acq_rel, memory_scope_all_svm_devices);
#endif
}
__kernel void probe(__global void *input, __global uint *output, __global uint *indices, uint mode) {
  if (mode >= 15 && mode <= 22) {
    __global uint *p = (__global uint *)input;
    if (mode == 15) {
      output[0] = 1;
      __opencl_atomic_store((volatile __global atomic_uint *)p, 1U, __ATOMIC_RELEASE, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES);
      while (__opencl_atomic_load((volatile __global atomic_uint *)p, __ATOMIC_ACQUIRE, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES) != 3) {}
    } else if (mode == 16) {
      output[0] = 2;
      __opencl_atomic_store((volatile __global atomic_uint *)(p + 1), 2U, __ATOMIC_RELEASE, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES);
    } else if (mode == 17) {
      p[4] = p[2];
      __opencl_atomic_store((volatile __global atomic_uint *)p, 1U, __ATOMIC_RELEASE, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES);
      while (__opencl_atomic_load((volatile __global atomic_uint *)output, __ATOMIC_ACQUIRE, __OPENCL_MEMORY_SCOPE_DEVICE) != 1) {}
      p[3] = p[2];
      __opencl_atomic_store((volatile __global atomic_uint *)(output + 1), 2U, __ATOMIC_RELEASE, __OPENCL_MEMORY_SCOPE_DEVICE);
    } else if (mode == 18) {
      p[2] = 73;
      __opencl_atomic_store((volatile __global atomic_uint *)output, 1U, __ATOMIC_RELEASE, __OPENCL_MEMORY_SCOPE_DEVICE);
      while (__opencl_atomic_load((volatile __global atomic_uint *)(output + 1), __ATOMIC_ACQUIRE, __OPENCL_MEMORY_SCOPE_DEVICE) != 2) {}
      __opencl_atomic_store((volatile __global atomic_uint *)(p + 1), 2U, __ATOMIC_RELEASE, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES);
    } else if (mode == 21 || mode == 22) {
      __global Node *control = (__global Node *)input;
      __global Node *table = (__global Node *)output;
      if (mode == 21) {
        table->next = control->next;
        __opencl_atomic_store((volatile __global atomic_uint *)&control->value, 1U, __ATOMIC_RELEASE, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES);
        while (__opencl_atomic_load((volatile __global atomic_uint *)&table->value, __ATOMIC_ACQUIRE, __OPENCL_MEMORY_SCOPE_DEVICE) != 2) {}
        control->padding[0] = table->next->value;
      } else {
        table->next = control->next->next;
        __opencl_atomic_store((volatile __global atomic_uint *)&table->value, 2U, __ATOMIC_RELEASE, __OPENCL_MEMORY_SCOPE_DEVICE);
      }
    } else if (mode == 19) {
      __global Node *node = (__global Node *)input;
      node->next = (__global Node *)output;
      __opencl_atomic_store((volatile __global atomic_uint *)&node->value, 1U, __ATOMIC_RELEASE, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES);
      while (__opencl_atomic_load((volatile __global atomic_uint *)&node->value, __ATOMIC_ACQUIRE, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES) != 2) {}
      node->padding[0] = node->next->value;
    }
    return;
  }
  if (mode == 13) {
    __global uint *p = (__global uint *)input;
    __global uint *out = (__global uint *)output;
    for (uint round = 0; round < 32; ++round) {
      const uint token = ((uint)1 << 31) + round + 1;
      while (__opencl_atomic_load((volatile __global atomic_uint *)p, __ATOMIC_RELAXED, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES) != token) {}
      uint expected = 0;
      bool exchanged = __opencl_atomic_compare_exchange_strong((volatile __global atomic_uint *)p, &expected, 0U, __ATOMIC_ACQUIRE, __ATOMIC_ACQUIRE, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES);
      out[3 * round] = p[1];
      out[3 * round + 1] = expected;
      out[3 * round + 2] = exchanged;
      __opencl_atomic_store((volatile __global atomic_uint *)(p + 2), token, __ATOMIC_RELEASE, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES);
    }
    return;
  }
  else if (mode == 14) {
    __global WideWord *p = (__global WideWord *)input;
    __global WideWord *out = (__global WideWord *)output;
    for (uint round = 0; round < 32; ++round) {
      const WideWord token = ((WideWord)1 << (sizeof(WideWord) * 8 - 1)) + round + 1;
      while (__opencl_atomic_load((volatile __global atomic_WideWord *)p, __ATOMIC_RELAXED, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES) != token) {}
      WideWord expected = 0;
      bool exchanged = __opencl_atomic_compare_exchange_strong((volatile __global atomic_WideWord *)p, &expected, 0UL, __ATOMIC_ACQUIRE, __ATOMIC_ACQUIRE, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES);
      out[3 * round] = p[1];
      out[3 * round + 1] = expected;
      out[3 * round + 2] = exchanged;
      __opencl_atomic_store((volatile __global atomic_WideWord *)(p + 2), token, __ATOMIC_RELEASE, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES);
    }
    return;
  }
  if (mode == 11) {
    __global uint *p = (__global uint *)input;
    p[1] = 74;
    __opencl_atomic_store((volatile __global atomic_uint *)p, 1U, __ATOMIC_RELEASE, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES);
    output[0] = 74;
    return;
  } else if (mode == 12) {
    __global WideWord *p = (__global WideWord *)input;
    p[1] = 74;
    __opencl_atomic_store((volatile __global atomic_WideWord *)p, 1UL, __ATOMIC_RELEASE, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES);
    ((__global WideWord *)output)[0] = 74;
    return;
  }
  if (mode == 9 || mode == 10) {
#ifdef __AMDGCN__
    uint i = __builtin_amdgcn_workgroup_id_x() * 32 + __builtin_amdgcn_workitem_id_x();
#else
    uint i = get_global_id(0);
#endif
    if (mode == 9)
      output[i] = __opencl_atomic_fetch_add((volatile __global atomic_uint *)input + 2, 1U, __ATOMIC_ACQ_REL, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES);
    else
      ((__global WideWord *)output)[i] = __opencl_atomic_fetch_add((volatile __global atomic_WideWord *)input + 2, 1UL, __ATOMIC_ACQ_REL, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES);
    return;
  }
  const bool fenced = mode == 7 || mode == 8;
  if (mode == 5 || mode == 7) {
    __global uint *p = (__global uint *)input;
    for (uint round = 0; round < 32; ++round) {
      while (__opencl_atomic_load((volatile __global atomic_uint *)p, fenced ? __ATOMIC_RELAXED : __ATOMIC_ACQUIRE, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES) != 1) {}
      if (fenced) system_fence();
      __opencl_atomic_fetch_add((volatile __global atomic_uint *)(p + 2), 1U, __ATOMIC_ACQ_REL, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES);
      uint value = p[1]; output[round] = value; p[1] = 2 * value;
      if (fenced) system_fence();
      __opencl_atomic_store((volatile __global atomic_uint *)p, 2U, fenced ? __ATOMIC_RELAXED : __ATOMIC_RELEASE, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES);
    }
  } else if (mode == 6 || mode == 8) {
    __global WideWord *p = (__global WideWord *)input;
    for (uint round = 0; round < 32; ++round) {
      while (__opencl_atomic_load((volatile __global atomic_WideWord *)p, fenced ? __ATOMIC_RELAXED : __ATOMIC_ACQUIRE, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES) != 1) {}
      if (fenced) system_fence();
      __opencl_atomic_fetch_add((volatile __global atomic_WideWord *)(p + 2), 1UL, __ATOMIC_ACQ_REL, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES);
      WideWord value = p[1]; ((__global WideWord *)output)[round] = value; p[1] = 2 * value;
      if (fenced) system_fence();
      __opencl_atomic_store((volatile __global atomic_WideWord *)p, 2UL, fenced ? __ATOMIC_RELAXED : __ATOMIC_RELEASE, __OPENCL_MEMORY_SCOPE_ALL_SVM_DEVICES);
    }
  } else if (mode == 4) {
    __global uchar *p = (__global uchar *)output;
    uint value = ((__global uint *)input)[0];
    p[0] = value + 1; p[2] = value + 2;
  } else
  if (mode == 3) {
    __global Node *p = (__global Node *)input;
    uint sum = 0;
    while (p) { sum += p->value; p = p->next; }
    output[0] = sum;
  } else {
#ifdef __AMDGCN__
    uint i = __builtin_amdgcn_workgroup_id_x() * 32 + __builtin_amdgcn_workitem_id_x();
#else
    uint i = get_global_id(0);
#endif
    uint target = mode == 0 ? i : mode == 1 ? i * 17 : indices[i];
    output[i] = ((__global uint *)input)[target] + 1;
  }
}
