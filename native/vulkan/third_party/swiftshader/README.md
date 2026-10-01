# SwiftShader ETC2 decoder

Source: https://github.com/google/swiftshader/tree/1e80438d2b93ef36a7c05f8d2b81233bac0e3d16/src/Device

`ETC_Decoder.cpp` and `ETC_Decoder.hpp` are copied without changes from that revision.
Copyright and Apache-2.0 license notices are retained; see `LICENSE.txt`.
Gemstone uses the RGB, punch-through alpha, and RGBA decoding entry points.
EAC R11/RG11 decoding is implemented separately in `test/reference/compressed_format.hpp` to retain
11-bit precision and the specified signed base-codeword and bias rules.
The upstream anonymous structs require disabling pedantic diagnostics for this source only.
