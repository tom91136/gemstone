# Pinned revisions of every external source the conformance project fetches.
# Data only: the build, the cache and its population all read this manifest.
include_guard(GLOBAL)

# name|kind|url|sha256
# kind is `archive` (extracted) or `file` (a single downloaded file).
set(CONFORMANCE_DEPENDENCY_SOURCES
  "glaze|archive|https://github.com/stephenberry/glaze/archive/refs/tags/v7.9.1.tar.gz|d6dee391276f5375672c35d06058e4fd8f1f30f62bae163a004b3bd13a4e2ae3"
  "magic_enum|archive|https://github.com/Neargye/magic_enum/archive/refs/tags/v0.9.8.tar.gz|1e54959a3f3cb675938d858603ad69d0f3f7c82439fc2bf86d7232daec2bd10e"
  "fire|file|https://raw.githubusercontent.com/kongaskristjan/fire-hpp/c31b9d6593ee4edfdf9df875d006e26d9a8df879/include/fire-hpp/fire.hpp|f9b82674d0c0a7322043fe837bce895d5798d18585f0e4b1ff105418a8a9e2f8"
  "Catch2|archive|https://github.com/catchorg/Catch2/archive/refs/tags/v3.15.3.tar.gz|b0299ae552918220a7a6e21e7de5b714777f4e8c883fb70c4bb23fe01df8c6e3"
  "fmt|archive|https://github.com/fmtlib/fmt/archive/refs/tags/11.2.0.tar.gz|bc23066d87ab3168f27cef3e97d545fa63314f5c79df5ea444d41d56f962c6af"
  "aspartame|archive|https://github.com/tom91136/Aspartame/archive/350653d33df03386c22b0c0baece22ea48ddfef1.tar.gz|32a68e79f35f1bde31abaf3df9df7428e7cbc98081ce6340adc2c27a16be81bc"
  "opencl_cts|archive|https://codeload.github.com/KhronosGroup/OpenCL-CTS/tar.gz/726a6d94da0876cf69bf6198d20d42197b0392de|b859bda3045735883848c09b4e794c89058cb17ec54d749b5acc0e361afba8e2"
  "opencl_opencl_headers|archive|https://github.com/KhronosGroup/OpenCL-Headers/archive/refs/tags/v2026.05.29.zip|0bdef6f653c577827a75fac3e3f18b0bf000171e39fa65c2eb84ec804c822cab"
  "opencl_spirv_headers|archive|https://github.com/KhronosGroup/SPIRV-Headers/archive/refs/tags/vulkan-sdk-1.4.357.0.zip|7d40c8c1983c1aba6f6f2fcbcf7be345d33a5693aeee0cc5b8c579fb8e3f2fea"
  "conformance_sycl_cts|archive|https://codeload.github.com/KhronosGroup/SYCL-CTS/tar.gz/5ea625931cdbe608bab67046356722270449c883|d0db51864d7f754f14daf6f5638e52fba50b2b92f19db4b03772c98a6ddcba88"
  "conformance_sycl_cts_opencl_headers|archive|https://codeload.github.com/KhronosGroup/OpenCL-Headers/tar.gz/5e2f8c66476bcf60eb8c2921807e34efb30294ee|a21ebd5e11577ce4013c12743d46fc65240eab7edde0229c81a8f4aefee5eef6"
  "conformance_babelstream|archive|https://codeload.github.com/UoB-HPC/BabelStream/tar.gz/17ab377b0e919e14fd3df2b67268761fdac8abb3|1600b92c6cefc09e0c61c1571da0f1807a0f5f2c96b47e52fbba0891c9b5da00"
  "conformance_minibude|archive|https://codeload.github.com/UoB-HPC/miniBUDE/tar.gz/570f66c1fd0c29b7f3ab6fa96fba87f4561aa44c|4c1d349785771c1fca818a7abd69dc789d6f5b77990c76ea29a94f1fdec5aa9e"
  "conformance_cloverleaf|archive|https://codeload.github.com/UoB-HPC/CloverLeaf/tar.gz/3e10ff9268d3704a744e5a2a4ea3c62831078082|d914a1ee547dec35818851b4cac8f6aeeb2ba067168e5826266598019445f25f"
  "conformance_tealeaf|archive|https://codeload.github.com/UoB-HPC/TeaLeaf/tar.gz/e70261c0be40537da75b258108ed2898f84f3c58|f454ab1cc89e750e0a6695bf65ce41cd3ebd64ba3e109a5109d02ae534bc6d16"
  "conformance_kokkos|archive|https://codeload.github.com/kokkos/kokkos/tar.gz/62d2b6c879b74b6ae7bd06eb3e5e80139c4708e6|2738406dba3a8edd57c9da845009807d9c5f6ce64d6c098d479470a39bbb3ff9")
