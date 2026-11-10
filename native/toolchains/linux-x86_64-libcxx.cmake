set(CMAKE_SYSTEM_NAME Linux)
set(CMAKE_SYSTEM_PROCESSOR x86_64)

set(CMAKE_C_COMPILER /opt/llvm/bin/clang)
set(CMAKE_CXX_COMPILER /opt/llvm/bin/clang++)
set(CMAKE_AR /opt/llvm/bin/llvm-ar)
set(CMAKE_RANLIB /opt/llvm/bin/llvm-ranlib)

set(CMAKE_C_FLAGS_INIT "--target=x86_64-linux-gnu --sysroot=/opt/sysroot")
# the driver otherwise picks up the libc++ headers beside the LLVM tarball and the host GCC headers
set(CMAKE_CXX_FLAGS_INIT "${CMAKE_C_FLAGS_INIT} -nostdinc++ -isystem /opt/libcxx/include/c++/v1")

set(CMAKE_EXE_LINKER_FLAGS_INIT "-fuse-ld=lld -nostdlib++ -static-libgcc -Wl,-Bsymbolic -Wl,--exclude-libs,ALL")
set(CMAKE_SHARED_LINKER_FLAGS_INIT "${CMAKE_EXE_LINKER_FLAGS_INIT}")
set(CMAKE_CXX_STANDARD_LIBRARIES_INIT
  "/opt/libcxx/lib/libc++.a /opt/libcxx/lib/libc++abi.a /opt/libcxx/lib/libunwind.a")
