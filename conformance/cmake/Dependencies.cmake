# Pinned revisions of every external source the conformance project fetches.
# Data only: the build, the cache and its population all read this manifest.
include_guard(GLOBAL)

# name|kind|url|sha256
# kind is `archive` (extracted) or `file` (a single downloaded file).
set(CONFORMANCE_DEPENDENCY_SOURCES
  "conformance_imgui|archive|https://codeload.github.com/ocornut/imgui/tar.gz/f1cc2ae15e53a861a874c3034aae6798fde194ab|f30be7f3e2136615b1848a9afb72f6dfd8a60820ddf1c1a85997bf3b722e7cfc"
  "conformance_llama|archive|https://codeload.github.com/ggml-org/llama.cpp/tar.gz/e9f824d8c0f011662a742c9d15d4aa18a41e32c0|caf033a18e7edcf9395ad843fc5dccfc85c2310729e4697054ae21eb29b576a5"
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
  "conformance_levelzero_cts|archive|https://codeload.github.com/oneapi-src/level-zero-tests/tar.gz/de9791d7cc678a34d5eeba9f2af1036ad4290c03|04d836acd5518a5c91710f835f44738f8067eaf1887fc9e5765006be852ebb42"
  "conformance_levelzero_gtest|archive|https://codeload.github.com/google/googletest/tar.gz/15460959cbbfa20e66ef0b5ab497367e47fc0a04|258f33ab1a8ee17adf48ec65e821d0ea9eafcbedeff6110f9eaed78472e73dde"
  "conformance_levelzero_headers|archive|https://codeload.github.com/oneapi-src/level-zero/tar.gz/934714b278f850d164627eeaaa043eee02785a9e|ec16e5f6a21443080494fe9ebeae5ea36de003124211d58ae3e3ae5eccfd1940"
  "conformance_babelstream|archive|https://codeload.github.com/UoB-HPC/BabelStream/tar.gz/17ab377b0e919e14fd3df2b67268761fdac8abb3|1600b92c6cefc09e0c61c1571da0f1807a0f5f2c96b47e52fbba0891c9b5da00"
  "conformance_minibude|archive|https://codeload.github.com/UoB-HPC/miniBUDE/tar.gz/570f66c1fd0c29b7f3ab6fa96fba87f4561aa44c|4c1d349785771c1fca818a7abd69dc789d6f5b77990c76ea29a94f1fdec5aa9e"
  "conformance_cloverleaf|archive|https://codeload.github.com/UoB-HPC/CloverLeaf/tar.gz/3e10ff9268d3704a744e5a2a4ea3c62831078082|d914a1ee547dec35818851b4cac8f6aeeb2ba067168e5826266598019445f25f"
  "conformance_tealeaf|archive|https://codeload.github.com/UoB-HPC/TeaLeaf/tar.gz/e70261c0be40537da75b258108ed2898f84f3c58|f454ab1cc89e750e0a6695bf65ce41cd3ebd64ba3e109a5109d02ae534bc6d16"
  "conformance_kokkos|archive|https://codeload.github.com/kokkos/kokkos/tar.gz/62d2b6c879b74b6ae7bd06eb3e5e80139c4708e6|2738406dba3a8edd57c9da845009807d9c5f6ce64d6c098d479470a39bbb3ff9"
  "conformance_vulkan_cts|archive|https://codeload.github.com/KhronosGroup/VK-GL-CTS/tar.gz/f6a29701220f34dd1407513bfe80d74ca7b392ce|d8741d45c2663193cc43b17d8f4e6fa230fa238f943bb4db3fa538ecb07503f3"
  "conformance_vulkan_spirv_tools|archive|https://codeload.github.com/KhronosGroup/SPIRV-Tools/tar.gz/2d14d2e76aa7de72404b17078eda15c20a6a0389|3c3683ec1b88d51de6e2f178cbab3a8e4d0b6546413db9e55335286820e56264"
  "conformance_vulkan_glslang|archive|https://codeload.github.com/KhronosGroup/glslang/tar.gz/715c8500e7cd67f2eba9e60e98852a1ed49d2f15|85c20114a55296a747b837ad500613f958e958ece214f1c05219b5f4a4073432"
  "conformance_vulkan_spirv_headers|archive|https://codeload.github.com/KhronosGroup/SPIRV-Headers/tar.gz/6dd7ba990830f7c15ac1345ff3b43ef6ffdad216|f1168d541510143dba6def246364c453a0a1c6876bbe98d8ede8c69ee478b50f"
  "conformance_vulkan_docs|archive|https://codeload.github.com/KhronosGroup/Vulkan-Docs/tar.gz/4abe0260bbd8e59930786eed645481808a3fe6f1|5f7ddba72b2cf40b55853539d732a74fd11e60452bd06ac40c786259f54f9a42"
  "conformance_vulkan_amber|archive|https://codeload.github.com/google/amber/tar.gz/53a4c8934bf7335d27c694e9fdac9ae1b180c0d2|48138d03652be8b3590e310427a3037f4f7b320e55fe7c3dd37397d146b4b454"
  "conformance_vulkan_jsoncpp|archive|https://codeload.github.com/open-source-parsers/jsoncpp/tar.gz/9059f5cad030ba11d37818847443a53918c327b1|e7a12f50c2125abc9e4b2a38f399393503b6eebd5b4ab75ebb7df1664e768ce7")
