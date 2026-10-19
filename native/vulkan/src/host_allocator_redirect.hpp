#pragma once

// Apply only to Vulkan implementation translation units, never to applications
// or other runtime libraries. GCC/Clang assembler labels keep the standard C++
// container types while selecting private allocation functions, without replacing
// process-wide operator new or requiring ELF-specific linker interposition.
#ifndef GEMSTONE_VULKAN_HOST_IMPLEMENTATION
  #include <cstddef>
  #include <new>

  // the redeclarations are deliberate; spelling them via a macro keeps them out of readability-redundant-declaration
  #define GEMSTONE_VULKAN_REDIRECT(label, ...) __VA_ARGS__ asm(label)

GEMSTONE_VULKAN_REDIRECT("gemstone_vulkan_host_new", void *operator new(size_t));
GEMSTONE_VULKAN_REDIRECT("gemstone_vulkan_host_new", void *operator new[](size_t));
GEMSTONE_VULKAN_REDIRECT("gemstone_vulkan_host_new_aligned", void *operator new(size_t, std::align_val_t));
GEMSTONE_VULKAN_REDIRECT("gemstone_vulkan_host_new_aligned", void *operator new[](size_t, std::align_val_t));
GEMSTONE_VULKAN_REDIRECT("gemstone_vulkan_host_new_nothrow", void *operator new(size_t, const std::nothrow_t &) noexcept);
GEMSTONE_VULKAN_REDIRECT("gemstone_vulkan_host_new_nothrow", void *operator new[](size_t, const std::nothrow_t &) noexcept);
GEMSTONE_VULKAN_REDIRECT("gemstone_vulkan_host_new_aligned_nothrow",
                         void *operator new(size_t, std::align_val_t, const std::nothrow_t &) noexcept);
GEMSTONE_VULKAN_REDIRECT("gemstone_vulkan_host_new_aligned_nothrow",
                         void *operator new[](size_t, std::align_val_t, const std::nothrow_t &) noexcept);
GEMSTONE_VULKAN_REDIRECT("gemstone_vulkan_host_delete", void operator delete(void *) noexcept);
GEMSTONE_VULKAN_REDIRECT("gemstone_vulkan_host_delete", void operator delete[](void *) noexcept);
GEMSTONE_VULKAN_REDIRECT("gemstone_vulkan_host_delete_sized", void operator delete(void *, size_t) noexcept);
GEMSTONE_VULKAN_REDIRECT("gemstone_vulkan_host_delete_sized", void operator delete[](void *, size_t) noexcept);
GEMSTONE_VULKAN_REDIRECT("gemstone_vulkan_host_delete_aligned", void operator delete(void *, std::align_val_t) noexcept);
GEMSTONE_VULKAN_REDIRECT("gemstone_vulkan_host_delete_aligned", void operator delete[](void *, std::align_val_t) noexcept);
GEMSTONE_VULKAN_REDIRECT("gemstone_vulkan_host_delete_sized_aligned", void operator delete(void *, size_t, std::align_val_t) noexcept);
GEMSTONE_VULKAN_REDIRECT("gemstone_vulkan_host_delete_sized_aligned", void operator delete[](void *, size_t, std::align_val_t) noexcept);
GEMSTONE_VULKAN_REDIRECT("gemstone_vulkan_host_delete_nothrow", void operator delete(void *, const std::nothrow_t &) noexcept);
GEMSTONE_VULKAN_REDIRECT("gemstone_vulkan_host_delete_nothrow", void operator delete[](void *, const std::nothrow_t &) noexcept);
GEMSTONE_VULKAN_REDIRECT("gemstone_vulkan_host_delete_aligned_nothrow",
                         void operator delete(void *, std::align_val_t, const std::nothrow_t &) noexcept);
GEMSTONE_VULKAN_REDIRECT("gemstone_vulkan_host_delete_aligned_nothrow",
                         void operator delete[](void *, std::align_val_t, const std::nothrow_t &) noexcept);
  #undef GEMSTONE_VULKAN_REDIRECT
#endif
