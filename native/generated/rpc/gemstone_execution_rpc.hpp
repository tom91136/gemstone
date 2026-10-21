/* Generated from ExecutionRPC.Operation. Regenerate with: sbt codegen */
#ifndef GEMSTONE_EXECUTION_RPC_HPP
#define GEMSTONE_EXECUTION_RPC_HPP

#include <stddef.h>
#include <stdint.h>
#include <array>
#include <algorithm>
#include <limits>
#include <type_traits>
#include <memory>
#include <optional>
#include <string>
#include <string_view>
#include <utility>
#include <variant>
#include <vector>

#include <glaze/glaze.hpp>
#include <glaze/msgpack.hpp>

namespace gemstone::execution {

inline constexpr uint32_t RequestMagic = static_cast<uint32_t>(0x474d4533);
inline constexpr uint32_t ResponseMagic = static_cast<uint32_t>(0x474d4633);
inline constexpr uint32_t GraphicsPushConstantBytes = 128U;
inline constexpr uint32_t MaximumPayload = 134217728U;
inline constexpr uint32_t MaximumElements = 16384U;
inline constexpr uint32_t MaximumTextBytes = 8192U;
inline constexpr uint32_t HelloMagic = 1196247091U;
inline constexpr uint32_t HelloResponseMagic = 1196247347U;
inline constexpr uint32_t MaximumSchemaBytes = 1048576U;
inline constexpr uint32_t MaximumTagBytes = 256U;
inline constexpr std::string_view Schema = "protocol(gemstone.execution){Ping:product(Ping){}=>unit,EnableHostMemory:product(EnableHostMemory){}=>unit,PollHostMemory:product(PollHostMemory){completion:alias(CompletionId,i64)[positive]}=>product(HostMemoryBatch){reads:seq[product(HostMemoryRange){address:alias(GuestAddress,i64)[],size:alias(Int,i32)[positive]}],writes:seq[product(HostMemoryBytes){address:alias(GuestAddress,i64)[],bytes:seq[i8],atomicWidth:alias(Int,i32)[one-of[0,1,2,4,8]]}],completed:bool,sync:option[product(HostMemorySync){ticket:i64,atomic:option[product(HostMemoryAtomic){address:alias(GuestAddress,i64)[],size:alias(Int,i32)[one-of[1,2,4,8]],expected:i64,desired:i64,operation:sum(HostMemoryAtomicOperation){CompareExchange:product(CompareExchange){},Load:product(Load){},Store:product(Store){}}}],publish:seq[product(HostMemoryRange){address:alias(GuestAddress,i64)[],size:alias(Int,i32)[positive]}],atomicStores:seq[product(HostMemoryAtomic){address:alias(GuestAddress,i64)[],size:alias(Int,i32)[one-of[1,2,4,8]],expected:i64,desired:i64,operation:sum(HostMemoryAtomicOperation){CompareExchange:product(CompareExchange){},Load:product(Load){},Store:product(Store){}}}],relaxed:bool}]},SupplyHostMemory:product(SupplyHostMemory){completion:alias(CompletionId,i64)[positive],ranges:seq[product(HostMemoryBytes){address:alias(GuestAddress,i64)[],bytes:seq[i8],atomicWidth:alias(Int,i32)[one-of[0,1,2,4,8]]}]}=>unit,RegisterAllocation:product(RegisterAllocation){size:alias(Long,i64)[long-interval[1,2147483647]]}=>alias(AllocationId,i64)[positive],SupplyHostSync:product(SupplyHostSync){completion:alias(CompletionId,i64)[positive],ticket:i64,observed:i64}=>unit,RegisterAllocationAddress:product(RegisterAllocationAddress){allocation:alias(AllocationId,i64)[positive],nativeBase:alias(GuestAddress,i64)[],hostBacked:bool}=>unit,ReleaseAllocation:product(ReleaseAllocation){allocation:alias(AllocationId,i64)[positive]}=>unit,WriteAllocation:product(WriteAllocation){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],bytes:seq[i8]}=>unit,ReadAllocation:product(ReadAllocation){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Int,i32)[positive0]}=>seq[i8],LoadProgram:product(LoadProgram){encoding:sum(ProgramEncoding){PTXText:product(PTXText){},CUDACubin:product(CUDACubin){},HSACodeObject:product(HSACodeObject){},SPIRVOpenCLCompute:product(SPIRVOpenCLCompute){},SPIRVVulkanCompute:product(SPIRVVulkanCompute){},SPIRVVulkanGraphics:product(SPIRVVulkanGraphics){}},image:seq[i8]}=>alias(ProgramId,i64)[positive],DescribeSpecialization:product(DescribeSpecialization){image:seq[i8]}=>seq[product(SpecializationInfo){specId:alias(Binary32,i64)[long-interval[0,4294967295]],byteSize:alias(Int,i32)[positive]}],SpecializeImage:product(SpecializeImage){image:seq[i8],constants:seq[product(SpecializationConstant){specId:alias(Binary32,i64)[long-interval[0,4294967295]],value:seq[i8]}]}=>seq[i8],PrepareShader:product(PrepareShader){image:seq[i8],entry:alias(String,text)[minimum-length[1],not-contains[00]],stage:sum(ShaderStageKind){Vertex:product(Vertex){},Fragment:product(Fragment){},Compute:product(Compute){}},constants:seq[product(SpecializationConstant){specId:alias(Binary32,i64)[long-interval[0,4294967295]],value:seq[i8]}]}=>product(PreparedShader){image:seq[i8],entry:alias(String,text)[minimum-length[1]],stage:sum(ShaderStageKind){Vertex:product(Vertex){},Fragment:product(Fragment){},Compute:product(Compute){}},descriptors:seq[product(ShaderDescriptor){set:alias(Int,i32)[positive0],binding:alias(Int,i32)[positive0],element:alias(Int,i32)[positive0],resource:sum(ShaderResourceKind){Buffer:product(Buffer){},Image:product(Image){},Sampler:product(Sampler){},SampledImage:product(SampledImage){}}}],pushConstants:bool,interfaces:seq[product(ShaderInterface){id:alias(Int,i32)[positive],direction:sum(ShaderDirection){Input:product(Input){},Output:product(Output){}},size:alias(Int,i32)[positive],builtin:option[alias(Int,i32)[positive0]],qualifiers:product(ShaderQualifiers){flat:bool,noPerspective:bool,centroid:bool,component:alias(Int,i32)[int-interval[0,3]]},fields:seq[product(ShaderField){offset:alias(Int,i32)[positive0],size:alias(Int,i32)[positive],location:option[alias(Int,i32)[positive0]],builtin:option[alias(Int,i32)[positive0]],qualifiers:product(ShaderQualifiers){flat:bool,noPerspective:bool,centroid:bool,component:alias(Int,i32)[int-interval[0,3]]}}]}],workgroup:option[product(Dimensions){x:alias(Int,i32)[positive],y:alias(Int,i32)[positive],z:alias(Int,i32)[positive]}],earlyFragmentTests:bool,fragmentQuads:bool},SubmitGraphics:product(SubmitGraphics){submission:product(GraphicsSubmission){commands:seq[sum(GraphicsCommand){Draw:product(Draw){value:product(GraphicsDraw){vertex:product(GraphicsShader){image:seq[i8],entry:alias(String,text)[minimum-length[1],not-contains[00]],interfaces:seq[product(ShaderInterface){id:alias(Int,i32)[positive],direction:sum(ShaderDirection){Input:product(Input){},Output:product(Output){}},size:alias(Int,i32)[positive],builtin:option[alias(Int,i32)[positive0]],qualifiers:product(ShaderQualifiers){flat:bool,noPerspective:bool,centroid:bool,component:alias(Int,i32)[int-interval[0,3]]},fields:seq[product(ShaderField){offset:alias(Int,i32)[positive0],size:alias(Int,i32)[positive],location:option[alias(Int,i32)[positive0]],builtin:option[alias(Int,i32)[positive0]],qualifiers:product(ShaderQualifiers){flat:bool,noPerspective:bool,centroid:bool,component:alias(Int,i32)[int-interval[0,3]]}}]}],arguments:seq[option[product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]}]],relocations:seq[product(PointerRelocation){source:sum(RelocationSource){Memory:product(Memory){region:alias(AllocationId,i64)[positive]},Argument:product(Argument){index:alias(Int,i32)[positive0]}},sourceOffset:alias(Long,i64)[positive0],target:alias(AllocationId,i64)[positive],targetOffset:alias(Long,i64)[positive0]}],pushConstants:seq[i8],earlyFragmentTests:bool,fragmentQuads:bool,descriptorBindings:seq[product(DescriptorBinding){argument:alias(Int,i32)[positive0],descriptor:sum(GraphicsDescriptor){Image:product(Image){source:product(GraphicsImage){memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},levels:alias(NonEmptyGraphicsImageLevels,seq[product(GraphicsImageLevel){extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]},offset:alias(Long,i64)[positive0],rowPitch:alias(Long,i64)[positive],slicePitch:alias(Long,i64)[positive],layerPitch:alias(Long,i64)[positive]}])[minimum-length[1]],layers:alias(Int,i32)[positive],samples:alias(Int,i32)[one-of[1,4]],format:sum(GraphicsFormat){R32f:product(R32f){},Rg32f:product(Rg32f){},Rgba32f:product(Rgba32f){},DecodedRgb32f:product(DecodedRgb32f){},Rgb10a2ui:product(Rgb10a2ui){},R8ui:product(R8ui){},R8i:product(R8i){},Rg8ui:product(Rg8ui){},Rg8i:product(Rg8i){},Rgba8ui:product(Rgba8ui){},Rgba8i:product(Rgba8i){},R16ui:product(R16ui){},R16i:product(R16i){},Rg16ui:product(Rg16ui){},Rg16i:product(Rg16i){},Rgba16ui:product(Rgba16ui){},Rgba16i:product(Rgba16i){},R32ui:product(R32ui){},R32i:product(R32i){},Rg32ui:product(Rg32ui){},Rg32i:product(Rg32i){},Rgba32ui:product(Rgba32ui){},Rgba32i:product(Rgba32i){},R8:product(R8){},R8Snorm:product(R8Snorm){},Rg8:product(Rg8){},Rg8Snorm:product(Rg8Snorm){},Rgba8:product(Rgba8){},Rgba8Snorm:product(Rgba8Snorm){},Rgba8Srgb:product(Rgba8Srgb){},Bgra8Unorm:product(Bgra8Unorm){},Bgra8Srgb:product(Bgra8Srgb){},R16:product(R16){},R16Snorm:product(R16Snorm){},Rg16:product(Rg16){},Rg16Snorm:product(Rg16Snorm){},Rgba16:product(Rgba16){},Rgba16Snorm:product(Rgba16Snorm){},B4G4R4A4Unorm:product(B4G4R4A4Unorm){},R5G6B5Unorm:product(R5G6B5Unorm){},A1R5G5B5Unorm:product(A1R5G5B5Unorm){},Rgb10A2:product(Rgb10A2){},R16f:product(R16f){},Rg16f:product(Rg16f){},Rgba16f:product(Rgba16f){},R11fG11fB10f:product(R11fG11fB10f){},E5B9G9R9Ufloat:product(E5B9G9R9Ufloat){},D16Unorm:product(D16Unorm){},D32Sfloat:product(D32Sfloat){},S8Uint:product(S8Uint){},D32SfloatS8Depth:product(D32SfloatS8Depth){},D32SfloatS8Stencil:product(D32SfloatS8Stencil){},Etc2R8g8b8UnormBlock:product(Etc2R8g8b8UnormBlock){},Etc2R8g8b8SrgbBlock:product(Etc2R8g8b8SrgbBlock){},Etc2R8g8b8a1UnormBlock:product(Etc2R8g8b8a1UnormBlock){},Etc2R8g8b8a1SrgbBlock:product(Etc2R8g8b8a1SrgbBlock){},Etc2R8g8b8a8UnormBlock:product(Etc2R8g8b8a8UnormBlock){},Etc2R8g8b8a8SrgbBlock:product(Etc2R8g8b8a8SrgbBlock){},EacR11UnormBlock:product(EacR11UnormBlock){},EacR11SnormBlock:product(EacR11SnormBlock){},EacR11g11UnormBlock:product(EacR11g11UnormBlock){},EacR11g11SnormBlock:product(EacR11g11SnormBlock){}},volume:bool},components:product(GraphicsComponents){r:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}},g:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}},b:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}},a:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}}}},Sampler:product(Sampler){value:product(GraphicsSampler){minFilter:sum(GraphicsFilter){Nearest:product(Nearest){},Linear:product(Linear){}},magFilter:sum(GraphicsFilter){Nearest:product(Nearest){},Linear:product(Linear){}},addressU:sum(GraphicsAddressMode){Repeat:product(Repeat){},MirroredRepeat:product(MirroredRepeat){},ClampToEdge:product(ClampToEdge){},ClampToBorder:product(ClampToBorder){},MirrorClampToEdge:product(MirrorClampToEdge){}},addressV:sum(GraphicsAddressMode){Repeat:product(Repeat){},MirroredRepeat:product(MirroredRepeat){},ClampToEdge:product(ClampToEdge){},ClampToBorder:product(ClampToBorder){},MirrorClampToEdge:product(MirrorClampToEdge){}},addressW:sum(GraphicsAddressMode){Repeat:product(Repeat){},MirroredRepeat:product(MirroredRepeat){},ClampToEdge:product(ClampToEdge){},ClampToBorder:product(ClampToBorder){},MirrorClampToEdge:product(MirrorClampToEdge){}},unnormalized:bool,compareEnable:bool,compareOp:sum(GraphicsCompare){Never:product(Never){},Less:product(Less){},Equal:product(Equal){},LessOrEqual:product(LessOrEqual){},Greater:product(Greater){},NotEqual:product(NotEqual){},GreaterOrEqual:product(GreaterOrEqual){},Always:product(Always){}},borderColour:sum(GraphicsBorderColour){FloatTransparentBlack:product(FloatTransparentBlack){},IntTransparentBlack:product(IntTransparentBlack){},FloatOpaqueBlack:product(FloatOpaqueBlack){},IntOpaqueBlack:product(IntOpaqueBlack){},FloatOpaqueWhite:product(FloatOpaqueWhite){},IntOpaqueWhite:product(IntOpaqueWhite){}},mipLodBias:f32,minLod:f32,maxLod:f32,mipmapMode:sum(GraphicsMipmapMode){Nearest:product(Nearest){},Linear:product(Linear){}}}},Combined:product(Combined){source:product(GraphicsImage){memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},levels:alias(NonEmptyGraphicsImageLevels,seq[product(GraphicsImageLevel){extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]},offset:alias(Long,i64)[positive0],rowPitch:alias(Long,i64)[positive],slicePitch:alias(Long,i64)[positive],layerPitch:alias(Long,i64)[positive]}])[minimum-length[1]],layers:alias(Int,i32)[positive],samples:alias(Int,i32)[one-of[1,4]],format:sum(GraphicsFormat){R32f:product(R32f){},Rg32f:product(Rg32f){},Rgba32f:product(Rgba32f){},DecodedRgb32f:product(DecodedRgb32f){},Rgb10a2ui:product(Rgb10a2ui){},R8ui:product(R8ui){},R8i:product(R8i){},Rg8ui:product(Rg8ui){},Rg8i:product(Rg8i){},Rgba8ui:product(Rgba8ui){},Rgba8i:product(Rgba8i){},R16ui:product(R16ui){},R16i:product(R16i){},Rg16ui:product(Rg16ui){},Rg16i:product(Rg16i){},Rgba16ui:product(Rgba16ui){},Rgba16i:product(Rgba16i){},R32ui:product(R32ui){},R32i:product(R32i){},Rg32ui:product(Rg32ui){},Rg32i:product(Rg32i){},Rgba32ui:product(Rgba32ui){},Rgba32i:product(Rgba32i){},R8:product(R8){},R8Snorm:product(R8Snorm){},Rg8:product(Rg8){},Rg8Snorm:product(Rg8Snorm){},Rgba8:product(Rgba8){},Rgba8Snorm:product(Rgba8Snorm){},Rgba8Srgb:product(Rgba8Srgb){},Bgra8Unorm:product(Bgra8Unorm){},Bgra8Srgb:product(Bgra8Srgb){},R16:product(R16){},R16Snorm:product(R16Snorm){},Rg16:product(Rg16){},Rg16Snorm:product(Rg16Snorm){},Rgba16:product(Rgba16){},Rgba16Snorm:product(Rgba16Snorm){},B4G4R4A4Unorm:product(B4G4R4A4Unorm){},R5G6B5Unorm:product(R5G6B5Unorm){},A1R5G5B5Unorm:product(A1R5G5B5Unorm){},Rgb10A2:product(Rgb10A2){},R16f:product(R16f){},Rg16f:product(Rg16f){},Rgba16f:product(Rgba16f){},R11fG11fB10f:product(R11fG11fB10f){},E5B9G9R9Ufloat:product(E5B9G9R9Ufloat){},D16Unorm:product(D16Unorm){},D32Sfloat:product(D32Sfloat){},S8Uint:product(S8Uint){},D32SfloatS8Depth:product(D32SfloatS8Depth){},D32SfloatS8Stencil:product(D32SfloatS8Stencil){},Etc2R8g8b8UnormBlock:product(Etc2R8g8b8UnormBlock){},Etc2R8g8b8SrgbBlock:product(Etc2R8g8b8SrgbBlock){},Etc2R8g8b8a1UnormBlock:product(Etc2R8g8b8a1UnormBlock){},Etc2R8g8b8a1SrgbBlock:product(Etc2R8g8b8a1SrgbBlock){},Etc2R8g8b8a8UnormBlock:product(Etc2R8g8b8a8UnormBlock){},Etc2R8g8b8a8SrgbBlock:product(Etc2R8g8b8a8SrgbBlock){},EacR11UnormBlock:product(EacR11UnormBlock){},EacR11SnormBlock:product(EacR11SnormBlock){},EacR11g11UnormBlock:product(EacR11g11UnormBlock){},EacR11g11SnormBlock:product(EacR11g11SnormBlock){}},volume:bool},components:product(GraphicsComponents){r:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}},g:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}},b:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}},a:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}}},sampler:product(GraphicsSampler){minFilter:sum(GraphicsFilter){Nearest:product(Nearest){},Linear:product(Linear){}},magFilter:sum(GraphicsFilter){Nearest:product(Nearest){},Linear:product(Linear){}},addressU:sum(GraphicsAddressMode){Repeat:product(Repeat){},MirroredRepeat:product(MirroredRepeat){},ClampToEdge:product(ClampToEdge){},ClampToBorder:product(ClampToBorder){},MirrorClampToEdge:product(MirrorClampToEdge){}},addressV:sum(GraphicsAddressMode){Repeat:product(Repeat){},MirroredRepeat:product(MirroredRepeat){},ClampToEdge:product(ClampToEdge){},ClampToBorder:product(ClampToBorder){},MirrorClampToEdge:product(MirrorClampToEdge){}},addressW:sum(GraphicsAddressMode){Repeat:product(Repeat){},MirroredRepeat:product(MirroredRepeat){},ClampToEdge:product(ClampToEdge){},ClampToBorder:product(ClampToBorder){},MirrorClampToEdge:product(MirrorClampToEdge){}},unnormalized:bool,compareEnable:bool,compareOp:sum(GraphicsCompare){Never:product(Never){},Less:product(Less){},Equal:product(Equal){},LessOrEqual:product(LessOrEqual){},Greater:product(Greater){},NotEqual:product(NotEqual){},GreaterOrEqual:product(GreaterOrEqual){},Always:product(Always){}},borderColour:sum(GraphicsBorderColour){FloatTransparentBlack:product(FloatTransparentBlack){},IntTransparentBlack:product(IntTransparentBlack){},FloatOpaqueBlack:product(FloatOpaqueBlack){},IntOpaqueBlack:product(IntOpaqueBlack){},FloatOpaqueWhite:product(FloatOpaqueWhite){},IntOpaqueWhite:product(IntOpaqueWhite){}},mipLodBias:f32,minLod:f32,maxLod:f32,mipmapMode:sum(GraphicsMipmapMode){Nearest:product(Nearest){},Linear:product(Linear){}}}}}}]},fragment:option[product(GraphicsShader){image:seq[i8],entry:alias(String,text)[minimum-length[1],not-contains[00]],interfaces:seq[product(ShaderInterface){id:alias(Int,i32)[positive],direction:sum(ShaderDirection){Input:product(Input){},Output:product(Output){}},size:alias(Int,i32)[positive],builtin:option[alias(Int,i32)[positive0]],qualifiers:product(ShaderQualifiers){flat:bool,noPerspective:bool,centroid:bool,component:alias(Int,i32)[int-interval[0,3]]},fields:seq[product(ShaderField){offset:alias(Int,i32)[positive0],size:alias(Int,i32)[positive],location:option[alias(Int,i32)[positive0]],builtin:option[alias(Int,i32)[positive0]],qualifiers:product(ShaderQualifiers){flat:bool,noPerspective:bool,centroid:bool,component:alias(Int,i32)[int-interval[0,3]]}}]}],arguments:seq[option[product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]}]],relocations:seq[product(PointerRelocation){source:sum(RelocationSource){Memory:product(Memory){region:alias(AllocationId,i64)[positive]},Argument:product(Argument){index:alias(Int,i32)[positive0]}},sourceOffset:alias(Long,i64)[positive0],target:alias(AllocationId,i64)[positive],targetOffset:alias(Long,i64)[positive0]}],pushConstants:seq[i8],earlyFragmentTests:bool,fragmentQuads:bool,descriptorBindings:seq[product(DescriptorBinding){argument:alias(Int,i32)[positive0],descriptor:sum(GraphicsDescriptor){Image:product(Image){source:product(GraphicsImage){memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},levels:alias(NonEmptyGraphicsImageLevels,seq[product(GraphicsImageLevel){extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]},offset:alias(Long,i64)[positive0],rowPitch:alias(Long,i64)[positive],slicePitch:alias(Long,i64)[positive],layerPitch:alias(Long,i64)[positive]}])[minimum-length[1]],layers:alias(Int,i32)[positive],samples:alias(Int,i32)[one-of[1,4]],format:sum(GraphicsFormat){R32f:product(R32f){},Rg32f:product(Rg32f){},Rgba32f:product(Rgba32f){},DecodedRgb32f:product(DecodedRgb32f){},Rgb10a2ui:product(Rgb10a2ui){},R8ui:product(R8ui){},R8i:product(R8i){},Rg8ui:product(Rg8ui){},Rg8i:product(Rg8i){},Rgba8ui:product(Rgba8ui){},Rgba8i:product(Rgba8i){},R16ui:product(R16ui){},R16i:product(R16i){},Rg16ui:product(Rg16ui){},Rg16i:product(Rg16i){},Rgba16ui:product(Rgba16ui){},Rgba16i:product(Rgba16i){},R32ui:product(R32ui){},R32i:product(R32i){},Rg32ui:product(Rg32ui){},Rg32i:product(Rg32i){},Rgba32ui:product(Rgba32ui){},Rgba32i:product(Rgba32i){},R8:product(R8){},R8Snorm:product(R8Snorm){},Rg8:product(Rg8){},Rg8Snorm:product(Rg8Snorm){},Rgba8:product(Rgba8){},Rgba8Snorm:product(Rgba8Snorm){},Rgba8Srgb:product(Rgba8Srgb){},Bgra8Unorm:product(Bgra8Unorm){},Bgra8Srgb:product(Bgra8Srgb){},R16:product(R16){},R16Snorm:product(R16Snorm){},Rg16:product(Rg16){},Rg16Snorm:product(Rg16Snorm){},Rgba16:product(Rgba16){},Rgba16Snorm:product(Rgba16Snorm){},B4G4R4A4Unorm:product(B4G4R4A4Unorm){},R5G6B5Unorm:product(R5G6B5Unorm){},A1R5G5B5Unorm:product(A1R5G5B5Unorm){},Rgb10A2:product(Rgb10A2){},R16f:product(R16f){},Rg16f:product(Rg16f){},Rgba16f:product(Rgba16f){},R11fG11fB10f:product(R11fG11fB10f){},E5B9G9R9Ufloat:product(E5B9G9R9Ufloat){},D16Unorm:product(D16Unorm){},D32Sfloat:product(D32Sfloat){},S8Uint:product(S8Uint){},D32SfloatS8Depth:product(D32SfloatS8Depth){},D32SfloatS8Stencil:product(D32SfloatS8Stencil){},Etc2R8g8b8UnormBlock:product(Etc2R8g8b8UnormBlock){},Etc2R8g8b8SrgbBlock:product(Etc2R8g8b8SrgbBlock){},Etc2R8g8b8a1UnormBlock:product(Etc2R8g8b8a1UnormBlock){},Etc2R8g8b8a1SrgbBlock:product(Etc2R8g8b8a1SrgbBlock){},Etc2R8g8b8a8UnormBlock:product(Etc2R8g8b8a8UnormBlock){},Etc2R8g8b8a8SrgbBlock:product(Etc2R8g8b8a8SrgbBlock){},EacR11UnormBlock:product(EacR11UnormBlock){},EacR11SnormBlock:product(EacR11SnormBlock){},EacR11g11UnormBlock:product(EacR11g11UnormBlock){},EacR11g11SnormBlock:product(EacR11g11SnormBlock){}},volume:bool},components:product(GraphicsComponents){r:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}},g:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}},b:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}},a:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}}}},Sampler:product(Sampler){value:product(GraphicsSampler){minFilter:sum(GraphicsFilter){Nearest:product(Nearest){},Linear:product(Linear){}},magFilter:sum(GraphicsFilter){Nearest:product(Nearest){},Linear:product(Linear){}},addressU:sum(GraphicsAddressMode){Repeat:product(Repeat){},MirroredRepeat:product(MirroredRepeat){},ClampToEdge:product(ClampToEdge){},ClampToBorder:product(ClampToBorder){},MirrorClampToEdge:product(MirrorClampToEdge){}},addressV:sum(GraphicsAddressMode){Repeat:product(Repeat){},MirroredRepeat:product(MirroredRepeat){},ClampToEdge:product(ClampToEdge){},ClampToBorder:product(ClampToBorder){},MirrorClampToEdge:product(MirrorClampToEdge){}},addressW:sum(GraphicsAddressMode){Repeat:product(Repeat){},MirroredRepeat:product(MirroredRepeat){},ClampToEdge:product(ClampToEdge){},ClampToBorder:product(ClampToBorder){},MirrorClampToEdge:product(MirrorClampToEdge){}},unnormalized:bool,compareEnable:bool,compareOp:sum(GraphicsCompare){Never:product(Never){},Less:product(Less){},Equal:product(Equal){},LessOrEqual:product(LessOrEqual){},Greater:product(Greater){},NotEqual:product(NotEqual){},GreaterOrEqual:product(GreaterOrEqual){},Always:product(Always){}},borderColour:sum(GraphicsBorderColour){FloatTransparentBlack:product(FloatTransparentBlack){},IntTransparentBlack:product(IntTransparentBlack){},FloatOpaqueBlack:product(FloatOpaqueBlack){},IntOpaqueBlack:product(IntOpaqueBlack){},FloatOpaqueWhite:product(FloatOpaqueWhite){},IntOpaqueWhite:product(IntOpaqueWhite){}},mipLodBias:f32,minLod:f32,maxLod:f32,mipmapMode:sum(GraphicsMipmapMode){Nearest:product(Nearest){},Linear:product(Linear){}}}},Combined:product(Combined){source:product(GraphicsImage){memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},levels:alias(NonEmptyGraphicsImageLevels,seq[product(GraphicsImageLevel){extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]},offset:alias(Long,i64)[positive0],rowPitch:alias(Long,i64)[positive],slicePitch:alias(Long,i64)[positive],layerPitch:alias(Long,i64)[positive]}])[minimum-length[1]],layers:alias(Int,i32)[positive],samples:alias(Int,i32)[one-of[1,4]],format:sum(GraphicsFormat){R32f:product(R32f){},Rg32f:product(Rg32f){},Rgba32f:product(Rgba32f){},DecodedRgb32f:product(DecodedRgb32f){},Rgb10a2ui:product(Rgb10a2ui){},R8ui:product(R8ui){},R8i:product(R8i){},Rg8ui:product(Rg8ui){},Rg8i:product(Rg8i){},Rgba8ui:product(Rgba8ui){},Rgba8i:product(Rgba8i){},R16ui:product(R16ui){},R16i:product(R16i){},Rg16ui:product(Rg16ui){},Rg16i:product(Rg16i){},Rgba16ui:product(Rgba16ui){},Rgba16i:product(Rgba16i){},R32ui:product(R32ui){},R32i:product(R32i){},Rg32ui:product(Rg32ui){},Rg32i:product(Rg32i){},Rgba32ui:product(Rgba32ui){},Rgba32i:product(Rgba32i){},R8:product(R8){},R8Snorm:product(R8Snorm){},Rg8:product(Rg8){},Rg8Snorm:product(Rg8Snorm){},Rgba8:product(Rgba8){},Rgba8Snorm:product(Rgba8Snorm){},Rgba8Srgb:product(Rgba8Srgb){},Bgra8Unorm:product(Bgra8Unorm){},Bgra8Srgb:product(Bgra8Srgb){},R16:product(R16){},R16Snorm:product(R16Snorm){},Rg16:product(Rg16){},Rg16Snorm:product(Rg16Snorm){},Rgba16:product(Rgba16){},Rgba16Snorm:product(Rgba16Snorm){},B4G4R4A4Unorm:product(B4G4R4A4Unorm){},R5G6B5Unorm:product(R5G6B5Unorm){},A1R5G5B5Unorm:product(A1R5G5B5Unorm){},Rgb10A2:product(Rgb10A2){},R16f:product(R16f){},Rg16f:product(Rg16f){},Rgba16f:product(Rgba16f){},R11fG11fB10f:product(R11fG11fB10f){},E5B9G9R9Ufloat:product(E5B9G9R9Ufloat){},D16Unorm:product(D16Unorm){},D32Sfloat:product(D32Sfloat){},S8Uint:product(S8Uint){},D32SfloatS8Depth:product(D32SfloatS8Depth){},D32SfloatS8Stencil:product(D32SfloatS8Stencil){},Etc2R8g8b8UnormBlock:product(Etc2R8g8b8UnormBlock){},Etc2R8g8b8SrgbBlock:product(Etc2R8g8b8SrgbBlock){},Etc2R8g8b8a1UnormBlock:product(Etc2R8g8b8a1UnormBlock){},Etc2R8g8b8a1SrgbBlock:product(Etc2R8g8b8a1SrgbBlock){},Etc2R8g8b8a8UnormBlock:product(Etc2R8g8b8a8UnormBlock){},Etc2R8g8b8a8SrgbBlock:product(Etc2R8g8b8a8SrgbBlock){},EacR11UnormBlock:product(EacR11UnormBlock){},EacR11SnormBlock:product(EacR11SnormBlock){},EacR11g11UnormBlock:product(EacR11g11UnormBlock){},EacR11g11SnormBlock:product(EacR11g11SnormBlock){}},volume:bool},components:product(GraphicsComponents){r:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}},g:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}},b:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}},a:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}}},sampler:product(GraphicsSampler){minFilter:sum(GraphicsFilter){Nearest:product(Nearest){},Linear:product(Linear){}},magFilter:sum(GraphicsFilter){Nearest:product(Nearest){},Linear:product(Linear){}},addressU:sum(GraphicsAddressMode){Repeat:product(Repeat){},MirroredRepeat:product(MirroredRepeat){},ClampToEdge:product(ClampToEdge){},ClampToBorder:product(ClampToBorder){},MirrorClampToEdge:product(MirrorClampToEdge){}},addressV:sum(GraphicsAddressMode){Repeat:product(Repeat){},MirroredRepeat:product(MirroredRepeat){},ClampToEdge:product(ClampToEdge){},ClampToBorder:product(ClampToBorder){},MirrorClampToEdge:product(MirrorClampToEdge){}},addressW:sum(GraphicsAddressMode){Repeat:product(Repeat){},MirroredRepeat:product(MirroredRepeat){},ClampToEdge:product(ClampToEdge){},ClampToBorder:product(ClampToBorder){},MirrorClampToEdge:product(MirrorClampToEdge){}},unnormalized:bool,compareEnable:bool,compareOp:sum(GraphicsCompare){Never:product(Never){},Less:product(Less){},Equal:product(Equal){},LessOrEqual:product(LessOrEqual){},Greater:product(Greater){},NotEqual:product(NotEqual){},GreaterOrEqual:product(GreaterOrEqual){},Always:product(Always){}},borderColour:sum(GraphicsBorderColour){FloatTransparentBlack:product(FloatTransparentBlack){},IntTransparentBlack:product(IntTransparentBlack){},FloatOpaqueBlack:product(FloatOpaqueBlack){},IntOpaqueBlack:product(IntOpaqueBlack){},FloatOpaqueWhite:product(FloatOpaqueWhite){},IntOpaqueWhite:product(IntOpaqueWhite){}},mipLodBias:f32,minLod:f32,maxLod:f32,mipmapMode:sum(GraphicsMipmapMode){Nearest:product(Nearest){},Linear:product(Linear){}}}}}}]}],vertexBuffers:seq[product(GraphicsVertexBuffer){binding:alias(Int,i32)[positive0],memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},stride:alias(Int,i32)[positive0],instanceRate:bool}],attributes:seq[product(GraphicsVertexAttribute){location:alias(Int,i32)[positive0],binding:alias(Int,i32)[positive0],offset:alias(Int,i32)[positive0],format:product(GraphicsVertexFormat){encoding:sum(GraphicsVertexEncoding){Unorm:product(Unorm){},Snorm:product(Snorm){},Uint:product(Uint){},Sint:product(Sint){},Uscaled:product(Uscaled){},Sscaled:product(Sscaled){},Float:product(Float){},Ufloat:product(Ufloat){}},widths:seq[alias(Int,i32)[positive]],bytes:alias(Int,i32)[positive],bgra:bool}}],index:option[product(GraphicsIndexBuffer){memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},width:alias(Int,i32)[one-of[2,4]]}],call:product(GraphicsDrawCall){vertices:alias(Int,i32)[positive0],instances:alias(Int,i32)[positive0],firstVertex:i32,firstInstance:i32,vertexOffset:i32},indirect:option[product(GraphicsIndirect){memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},count:alias(Int,i32)[positive0],stride:alias(Int,i32)[positive0]}],raster:product(GraphicsRasterState){topology:sum(GraphicsTopology){PointList:product(PointList){},LineList:product(LineList){},LineStrip:product(LineStrip){},TriangleList:product(TriangleList){},TriangleStrip:product(TriangleStrip){},TriangleFan:product(TriangleFan){}},restart:bool,discard:bool,cullFront:bool,cullBack:bool,frontCounterClockwise:bool,biasEnabled:bool,biasConstant:f32,biasClamp:f32,biasSlope:f32,samples:alias(Int,i32)[one-of[1,4]],sampleMask:i32,sampleShading:bool,alphaToCoverage:bool},viewport:product(GraphicsViewport){x:f32,y:f32,width:f32,height:f32,minDepth:f32,maxDepth:f32},scissor:product(GraphicsRect){x:i32,y:i32,width:alias(Int,i32)[positive0],height:alias(Int,i32)[positive0]},renderArea:product(GraphicsRect){x:i32,y:i32,width:alias(Int,i32)[positive0],height:alias(Int,i32)[positive0]},width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],viewIndex:alias(Int,i32)[positive0],colours:seq[option[product(GraphicsImage){memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},levels:alias(NonEmptyGraphicsImageLevels,seq[product(GraphicsImageLevel){extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]},offset:alias(Long,i64)[positive0],rowPitch:alias(Long,i64)[positive],slicePitch:alias(Long,i64)[positive],layerPitch:alias(Long,i64)[positive]}])[minimum-length[1]],layers:alias(Int,i32)[positive],samples:alias(Int,i32)[one-of[1,4]],format:sum(GraphicsFormat){R32f:product(R32f){},Rg32f:product(Rg32f){},Rgba32f:product(Rgba32f){},DecodedRgb32f:product(DecodedRgb32f){},Rgb10a2ui:product(Rgb10a2ui){},R8ui:product(R8ui){},R8i:product(R8i){},Rg8ui:product(Rg8ui){},Rg8i:product(Rg8i){},Rgba8ui:product(Rgba8ui){},Rgba8i:product(Rgba8i){},R16ui:product(R16ui){},R16i:product(R16i){},Rg16ui:product(Rg16ui){},Rg16i:product(Rg16i){},Rgba16ui:product(Rgba16ui){},Rgba16i:product(Rgba16i){},R32ui:product(R32ui){},R32i:product(R32i){},Rg32ui:product(Rg32ui){},Rg32i:product(Rg32i){},Rgba32ui:product(Rgba32ui){},Rgba32i:product(Rgba32i){},R8:product(R8){},R8Snorm:product(R8Snorm){},Rg8:product(Rg8){},Rg8Snorm:product(Rg8Snorm){},Rgba8:product(Rgba8){},Rgba8Snorm:product(Rgba8Snorm){},Rgba8Srgb:product(Rgba8Srgb){},Bgra8Unorm:product(Bgra8Unorm){},Bgra8Srgb:product(Bgra8Srgb){},R16:product(R16){},R16Snorm:product(R16Snorm){},Rg16:product(Rg16){},Rg16Snorm:product(Rg16Snorm){},Rgba16:product(Rgba16){},Rgba16Snorm:product(Rgba16Snorm){},B4G4R4A4Unorm:product(B4G4R4A4Unorm){},R5G6B5Unorm:product(R5G6B5Unorm){},A1R5G5B5Unorm:product(A1R5G5B5Unorm){},Rgb10A2:product(Rgb10A2){},R16f:product(R16f){},Rg16f:product(Rg16f){},Rgba16f:product(Rgba16f){},R11fG11fB10f:product(R11fG11fB10f){},E5B9G9R9Ufloat:product(E5B9G9R9Ufloat){},D16Unorm:product(D16Unorm){},D32Sfloat:product(D32Sfloat){},S8Uint:product(S8Uint){},D32SfloatS8Depth:product(D32SfloatS8Depth){},D32SfloatS8Stencil:product(D32SfloatS8Stencil){},Etc2R8g8b8UnormBlock:product(Etc2R8g8b8UnormBlock){},Etc2R8g8b8SrgbBlock:product(Etc2R8g8b8SrgbBlock){},Etc2R8g8b8a1UnormBlock:product(Etc2R8g8b8a1UnormBlock){},Etc2R8g8b8a1SrgbBlock:product(Etc2R8g8b8a1SrgbBlock){},Etc2R8g8b8a8UnormBlock:product(Etc2R8g8b8a8UnormBlock){},Etc2R8g8b8a8SrgbBlock:product(Etc2R8g8b8a8SrgbBlock){},EacR11UnormBlock:product(EacR11UnormBlock){},EacR11SnormBlock:product(EacR11SnormBlock){},EacR11g11UnormBlock:product(EacR11g11UnormBlock){},EacR11g11SnormBlock:product(EacR11g11SnormBlock){}},volume:bool}]],depth:option[product(GraphicsImage){memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},levels:alias(NonEmptyGraphicsImageLevels,seq[product(GraphicsImageLevel){extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]},offset:alias(Long,i64)[positive0],rowPitch:alias(Long,i64)[positive],slicePitch:alias(Long,i64)[positive],layerPitch:alias(Long,i64)[positive]}])[minimum-length[1]],layers:alias(Int,i32)[positive],samples:alias(Int,i32)[one-of[1,4]],format:sum(GraphicsFormat){R32f:product(R32f){},Rg32f:product(Rg32f){},Rgba32f:product(Rgba32f){},DecodedRgb32f:product(DecodedRgb32f){},Rgb10a2ui:product(Rgb10a2ui){},R8ui:product(R8ui){},R8i:product(R8i){},Rg8ui:product(Rg8ui){},Rg8i:product(Rg8i){},Rgba8ui:product(Rgba8ui){},Rgba8i:product(Rgba8i){},R16ui:product(R16ui){},R16i:product(R16i){},Rg16ui:product(Rg16ui){},Rg16i:product(Rg16i){},Rgba16ui:product(Rgba16ui){},Rgba16i:product(Rgba16i){},R32ui:product(R32ui){},R32i:product(R32i){},Rg32ui:product(Rg32ui){},Rg32i:product(Rg32i){},Rgba32ui:product(Rgba32ui){},Rgba32i:product(Rgba32i){},R8:product(R8){},R8Snorm:product(R8Snorm){},Rg8:product(Rg8){},Rg8Snorm:product(Rg8Snorm){},Rgba8:product(Rgba8){},Rgba8Snorm:product(Rgba8Snorm){},Rgba8Srgb:product(Rgba8Srgb){},Bgra8Unorm:product(Bgra8Unorm){},Bgra8Srgb:product(Bgra8Srgb){},R16:product(R16){},R16Snorm:product(R16Snorm){},Rg16:product(Rg16){},Rg16Snorm:product(Rg16Snorm){},Rgba16:product(Rgba16){},Rgba16Snorm:product(Rgba16Snorm){},B4G4R4A4Unorm:product(B4G4R4A4Unorm){},R5G6B5Unorm:product(R5G6B5Unorm){},A1R5G5B5Unorm:product(A1R5G5B5Unorm){},Rgb10A2:product(Rgb10A2){},R16f:product(R16f){},Rg16f:product(Rg16f){},Rgba16f:product(Rgba16f){},R11fG11fB10f:product(R11fG11fB10f){},E5B9G9R9Ufloat:product(E5B9G9R9Ufloat){},D16Unorm:product(D16Unorm){},D32Sfloat:product(D32Sfloat){},S8Uint:product(S8Uint){},D32SfloatS8Depth:product(D32SfloatS8Depth){},D32SfloatS8Stencil:product(D32SfloatS8Stencil){},Etc2R8g8b8UnormBlock:product(Etc2R8g8b8UnormBlock){},Etc2R8g8b8SrgbBlock:product(Etc2R8g8b8SrgbBlock){},Etc2R8g8b8a1UnormBlock:product(Etc2R8g8b8a1UnormBlock){},Etc2R8g8b8a1SrgbBlock:product(Etc2R8g8b8a1SrgbBlock){},Etc2R8g8b8a8UnormBlock:product(Etc2R8g8b8a8UnormBlock){},Etc2R8g8b8a8SrgbBlock:product(Etc2R8g8b8a8SrgbBlock){},EacR11UnormBlock:product(EacR11UnormBlock){},EacR11SnormBlock:product(EacR11SnormBlock){},EacR11g11UnormBlock:product(EacR11g11UnormBlock){},EacR11g11SnormBlock:product(EacR11g11SnormBlock){}},volume:bool}],blends:seq[product(GraphicsBlend){enabled:bool,sourceColour:sum(GraphicsBlendFactor){Zero:product(Zero){},One:product(One){},SrcColor:product(SrcColor){},OneMinusSrcColor:product(OneMinusSrcColor){},DstColor:product(DstColor){},OneMinusDstColor:product(OneMinusDstColor){},SrcAlpha:product(SrcAlpha){},OneMinusSrcAlpha:product(OneMinusSrcAlpha){},DstAlpha:product(DstAlpha){},OneMinusDstAlpha:product(OneMinusDstAlpha){},ConstantColor:product(ConstantColor){},OneMinusConstantColor:product(OneMinusConstantColor){},ConstantAlpha:product(ConstantAlpha){},OneMinusConstantAlpha:product(OneMinusConstantAlpha){},SrcAlphaSaturate:product(SrcAlphaSaturate){}},destinationColour:sum(GraphicsBlendFactor){Zero:product(Zero){},One:product(One){},SrcColor:product(SrcColor){},OneMinusSrcColor:product(OneMinusSrcColor){},DstColor:product(DstColor){},OneMinusDstColor:product(OneMinusDstColor){},SrcAlpha:product(SrcAlpha){},OneMinusSrcAlpha:product(OneMinusSrcAlpha){},DstAlpha:product(DstAlpha){},OneMinusDstAlpha:product(OneMinusDstAlpha){},ConstantColor:product(ConstantColor){},OneMinusConstantColor:product(OneMinusConstantColor){},ConstantAlpha:product(ConstantAlpha){},OneMinusConstantAlpha:product(OneMinusConstantAlpha){},SrcAlphaSaturate:product(SrcAlphaSaturate){}},colourOp:sum(GraphicsBlendOp){Add:product(Add){},Subtract:product(Subtract){},ReverseSubtract:product(ReverseSubtract){},Min:product(Min){},Max:product(Max){}},sourceAlpha:sum(GraphicsBlendFactor){Zero:product(Zero){},One:product(One){},SrcColor:product(SrcColor){},OneMinusSrcColor:product(OneMinusSrcColor){},DstColor:product(DstColor){},OneMinusDstColor:product(OneMinusDstColor){},SrcAlpha:product(SrcAlpha){},OneMinusSrcAlpha:product(OneMinusSrcAlpha){},DstAlpha:product(DstAlpha){},OneMinusDstAlpha:product(OneMinusDstAlpha){},ConstantColor:product(ConstantColor){},OneMinusConstantColor:product(OneMinusConstantColor){},ConstantAlpha:product(ConstantAlpha){},OneMinusConstantAlpha:product(OneMinusConstantAlpha){},SrcAlphaSaturate:product(SrcAlphaSaturate){}},destinationAlpha:sum(GraphicsBlendFactor){Zero:product(Zero){},One:product(One){},SrcColor:product(SrcColor){},OneMinusSrcColor:product(OneMinusSrcColor){},DstColor:product(DstColor){},OneMinusDstColor:product(OneMinusDstColor){},SrcAlpha:product(SrcAlpha){},OneMinusSrcAlpha:product(OneMinusSrcAlpha){},DstAlpha:product(DstAlpha){},OneMinusDstAlpha:product(OneMinusDstAlpha){},ConstantColor:product(ConstantColor){},OneMinusConstantColor:product(OneMinusConstantColor){},ConstantAlpha:product(ConstantAlpha){},OneMinusConstantAlpha:product(OneMinusConstantAlpha){},SrcAlphaSaturate:product(SrcAlphaSaturate){}},alphaOp:sum(GraphicsBlendOp){Add:product(Add){},Subtract:product(Subtract){},ReverseSubtract:product(ReverseSubtract){},Min:product(Min){},Max:product(Max){}},writeMask:i32}],blendConstants:product(GraphicsColour){r:f32,g:f32,b:f32,a:f32},depthStencil:product(GraphicsDepthStencil){depthTest:bool,depthWrite:bool,compare:sum(GraphicsCompare){Never:product(Never){},Less:product(Less){},Equal:product(Equal){},LessOrEqual:product(LessOrEqual){},Greater:product(Greater){},NotEqual:product(NotEqual){},GreaterOrEqual:product(GreaterOrEqual){},Always:product(Always){}},boundsTest:bool,minBounds:f32,maxBounds:f32,stencilTest:bool,front:product(GraphicsStencil){fail:sum(GraphicsStencilOp){Keep:product(Keep){},Zero:product(Zero){},Replace:product(Replace){},IncrementAndClamp:product(IncrementAndClamp){},DecrementAndClamp:product(DecrementAndClamp){},Invert:product(Invert){},IncrementAndWrap:product(IncrementAndWrap){},DecrementAndWrap:product(DecrementAndWrap){}},pass:sum(GraphicsStencilOp){Keep:product(Keep){},Zero:product(Zero){},Replace:product(Replace){},IncrementAndClamp:product(IncrementAndClamp){},DecrementAndClamp:product(DecrementAndClamp){},Invert:product(Invert){},IncrementAndWrap:product(IncrementAndWrap){},DecrementAndWrap:product(DecrementAndWrap){}},depthFail:sum(GraphicsStencilOp){Keep:product(Keep){},Zero:product(Zero){},Replace:product(Replace){},IncrementAndClamp:product(IncrementAndClamp){},DecrementAndClamp:product(DecrementAndClamp){},Invert:product(Invert){},IncrementAndWrap:product(IncrementAndWrap){},DecrementAndWrap:product(DecrementAndWrap){}},compare:sum(GraphicsCompare){Never:product(Never){},Less:product(Less){},Equal:product(Equal){},LessOrEqual:product(LessOrEqual){},Greater:product(Greater){},NotEqual:product(NotEqual){},GreaterOrEqual:product(GreaterOrEqual){},Always:product(Always){}},compareMask:i32,writeMask:i32,reference:i32},back:product(GraphicsStencil){fail:sum(GraphicsStencilOp){Keep:product(Keep){},Zero:product(Zero){},Replace:product(Replace){},IncrementAndClamp:product(IncrementAndClamp){},DecrementAndClamp:product(DecrementAndClamp){},Invert:product(Invert){},IncrementAndWrap:product(IncrementAndWrap){},DecrementAndWrap:product(DecrementAndWrap){}},pass:sum(GraphicsStencilOp){Keep:product(Keep){},Zero:product(Zero){},Replace:product(Replace){},IncrementAndClamp:product(IncrementAndClamp){},DecrementAndClamp:product(DecrementAndClamp){},Invert:product(Invert){},IncrementAndWrap:product(IncrementAndWrap){},DecrementAndWrap:product(DecrementAndWrap){}},depthFail:sum(GraphicsStencilOp){Keep:product(Keep){},Zero:product(Zero){},Replace:product(Replace){},IncrementAndClamp:product(IncrementAndClamp){},DecrementAndClamp:product(DecrementAndClamp){},Invert:product(Invert){},IncrementAndWrap:product(IncrementAndWrap){},DecrementAndWrap:product(DecrementAndWrap){}},compare:sum(GraphicsCompare){Never:product(Never){},Less:product(Less){},Equal:product(Equal){},LessOrEqual:product(LessOrEqual){},Greater:product(Greater){},NotEqual:product(NotEqual){},GreaterOrEqual:product(GreaterOrEqual){},Always:product(Always){}},compareMask:i32,writeMask:i32,reference:i32}}}},ClearColour:product(ClearColour){image:product(GraphicsImage){memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},levels:alias(NonEmptyGraphicsImageLevels,seq[product(GraphicsImageLevel){extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]},offset:alias(Long,i64)[positive0],rowPitch:alias(Long,i64)[positive],slicePitch:alias(Long,i64)[positive],layerPitch:alias(Long,i64)[positive]}])[minimum-length[1]],layers:alias(Int,i32)[positive],samples:alias(Int,i32)[one-of[1,4]],format:sum(GraphicsFormat){R32f:product(R32f){},Rg32f:product(Rg32f){},Rgba32f:product(Rgba32f){},DecodedRgb32f:product(DecodedRgb32f){},Rgb10a2ui:product(Rgb10a2ui){},R8ui:product(R8ui){},R8i:product(R8i){},Rg8ui:product(Rg8ui){},Rg8i:product(Rg8i){},Rgba8ui:product(Rgba8ui){},Rgba8i:product(Rgba8i){},R16ui:product(R16ui){},R16i:product(R16i){},Rg16ui:product(Rg16ui){},Rg16i:product(Rg16i){},Rgba16ui:product(Rgba16ui){},Rgba16i:product(Rgba16i){},R32ui:product(R32ui){},R32i:product(R32i){},Rg32ui:product(Rg32ui){},Rg32i:product(Rg32i){},Rgba32ui:product(Rgba32ui){},Rgba32i:product(Rgba32i){},R8:product(R8){},R8Snorm:product(R8Snorm){},Rg8:product(Rg8){},Rg8Snorm:product(Rg8Snorm){},Rgba8:product(Rgba8){},Rgba8Snorm:product(Rgba8Snorm){},Rgba8Srgb:product(Rgba8Srgb){},Bgra8Unorm:product(Bgra8Unorm){},Bgra8Srgb:product(Bgra8Srgb){},R16:product(R16){},R16Snorm:product(R16Snorm){},Rg16:product(Rg16){},Rg16Snorm:product(Rg16Snorm){},Rgba16:product(Rgba16){},Rgba16Snorm:product(Rgba16Snorm){},B4G4R4A4Unorm:product(B4G4R4A4Unorm){},R5G6B5Unorm:product(R5G6B5Unorm){},A1R5G5B5Unorm:product(A1R5G5B5Unorm){},Rgb10A2:product(Rgb10A2){},R16f:product(R16f){},Rg16f:product(Rg16f){},Rgba16f:product(Rgba16f){},R11fG11fB10f:product(R11fG11fB10f){},E5B9G9R9Ufloat:product(E5B9G9R9Ufloat){},D16Unorm:product(D16Unorm){},D32Sfloat:product(D32Sfloat){},S8Uint:product(S8Uint){},D32SfloatS8Depth:product(D32SfloatS8Depth){},D32SfloatS8Stencil:product(D32SfloatS8Stencil){},Etc2R8g8b8UnormBlock:product(Etc2R8g8b8UnormBlock){},Etc2R8g8b8SrgbBlock:product(Etc2R8g8b8SrgbBlock){},Etc2R8g8b8a1UnormBlock:product(Etc2R8g8b8a1UnormBlock){},Etc2R8g8b8a1SrgbBlock:product(Etc2R8g8b8a1SrgbBlock){},Etc2R8g8b8a8UnormBlock:product(Etc2R8g8b8a8UnormBlock){},Etc2R8g8b8a8SrgbBlock:product(Etc2R8g8b8a8SrgbBlock){},EacR11UnormBlock:product(EacR11UnormBlock){},EacR11SnormBlock:product(EacR11SnormBlock){},EacR11g11UnormBlock:product(EacR11g11UnormBlock){},EacR11g11SnormBlock:product(EacR11g11SnormBlock){}},volume:bool},area:product(GraphicsRect){x:i32,y:i32,width:alias(Int,i32)[positive0],height:alias(Int,i32)[positive0]},bits:seq[i32]},ClearDepthStencil:product(ClearDepthStencil){image:product(GraphicsImage){memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},levels:alias(NonEmptyGraphicsImageLevels,seq[product(GraphicsImageLevel){extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]},offset:alias(Long,i64)[positive0],rowPitch:alias(Long,i64)[positive],slicePitch:alias(Long,i64)[positive],layerPitch:alias(Long,i64)[positive]}])[minimum-length[1]],layers:alias(Int,i32)[positive],samples:alias(Int,i32)[one-of[1,4]],format:sum(GraphicsFormat){R32f:product(R32f){},Rg32f:product(Rg32f){},Rgba32f:product(Rgba32f){},DecodedRgb32f:product(DecodedRgb32f){},Rgb10a2ui:product(Rgb10a2ui){},R8ui:product(R8ui){},R8i:product(R8i){},Rg8ui:product(Rg8ui){},Rg8i:product(Rg8i){},Rgba8ui:product(Rgba8ui){},Rgba8i:product(Rgba8i){},R16ui:product(R16ui){},R16i:product(R16i){},Rg16ui:product(Rg16ui){},Rg16i:product(Rg16i){},Rgba16ui:product(Rgba16ui){},Rgba16i:product(Rgba16i){},R32ui:product(R32ui){},R32i:product(R32i){},Rg32ui:product(Rg32ui){},Rg32i:product(Rg32i){},Rgba32ui:product(Rgba32ui){},Rgba32i:product(Rgba32i){},R8:product(R8){},R8Snorm:product(R8Snorm){},Rg8:product(Rg8){},Rg8Snorm:product(Rg8Snorm){},Rgba8:product(Rgba8){},Rgba8Snorm:product(Rgba8Snorm){},Rgba8Srgb:product(Rgba8Srgb){},Bgra8Unorm:product(Bgra8Unorm){},Bgra8Srgb:product(Bgra8Srgb){},R16:product(R16){},R16Snorm:product(R16Snorm){},Rg16:product(Rg16){},Rg16Snorm:product(Rg16Snorm){},Rgba16:product(Rgba16){},Rgba16Snorm:product(Rgba16Snorm){},B4G4R4A4Unorm:product(B4G4R4A4Unorm){},R5G6B5Unorm:product(R5G6B5Unorm){},A1R5G5B5Unorm:product(A1R5G5B5Unorm){},Rgb10A2:product(Rgb10A2){},R16f:product(R16f){},Rg16f:product(Rg16f){},Rgba16f:product(Rgba16f){},R11fG11fB10f:product(R11fG11fB10f){},E5B9G9R9Ufloat:product(E5B9G9R9Ufloat){},D16Unorm:product(D16Unorm){},D32Sfloat:product(D32Sfloat){},S8Uint:product(S8Uint){},D32SfloatS8Depth:product(D32SfloatS8Depth){},D32SfloatS8Stencil:product(D32SfloatS8Stencil){},Etc2R8g8b8UnormBlock:product(Etc2R8g8b8UnormBlock){},Etc2R8g8b8SrgbBlock:product(Etc2R8g8b8SrgbBlock){},Etc2R8g8b8a1UnormBlock:product(Etc2R8g8b8a1UnormBlock){},Etc2R8g8b8a1SrgbBlock:product(Etc2R8g8b8a1SrgbBlock){},Etc2R8g8b8a8UnormBlock:product(Etc2R8g8b8a8UnormBlock){},Etc2R8g8b8a8SrgbBlock:product(Etc2R8g8b8a8SrgbBlock){},EacR11UnormBlock:product(EacR11UnormBlock){},EacR11SnormBlock:product(EacR11SnormBlock){},EacR11g11UnormBlock:product(EacR11g11UnormBlock){},EacR11g11SnormBlock:product(EacR11g11SnormBlock){}},volume:bool},area:product(GraphicsRect){x:i32,y:i32,width:alias(Int,i32)[positive0],height:alias(Int,i32)[positive0]},depth:f32,stencil:i32,clearDepth:bool,clearStencil:bool},CopyImage:product(CopyImage){source:product(GraphicsImage){memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},levels:alias(NonEmptyGraphicsImageLevels,seq[product(GraphicsImageLevel){extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]},offset:alias(Long,i64)[positive0],rowPitch:alias(Long,i64)[positive],slicePitch:alias(Long,i64)[positive],layerPitch:alias(Long,i64)[positive]}])[minimum-length[1]],layers:alias(Int,i32)[positive],samples:alias(Int,i32)[one-of[1,4]],format:sum(GraphicsFormat){R32f:product(R32f){},Rg32f:product(Rg32f){},Rgba32f:product(Rgba32f){},DecodedRgb32f:product(DecodedRgb32f){},Rgb10a2ui:product(Rgb10a2ui){},R8ui:product(R8ui){},R8i:product(R8i){},Rg8ui:product(Rg8ui){},Rg8i:product(Rg8i){},Rgba8ui:product(Rgba8ui){},Rgba8i:product(Rgba8i){},R16ui:product(R16ui){},R16i:product(R16i){},Rg16ui:product(Rg16ui){},Rg16i:product(Rg16i){},Rgba16ui:product(Rgba16ui){},Rgba16i:product(Rgba16i){},R32ui:product(R32ui){},R32i:product(R32i){},Rg32ui:product(Rg32ui){},Rg32i:product(Rg32i){},Rgba32ui:product(Rgba32ui){},Rgba32i:product(Rgba32i){},R8:product(R8){},R8Snorm:product(R8Snorm){},Rg8:product(Rg8){},Rg8Snorm:product(Rg8Snorm){},Rgba8:product(Rgba8){},Rgba8Snorm:product(Rgba8Snorm){},Rgba8Srgb:product(Rgba8Srgb){},Bgra8Unorm:product(Bgra8Unorm){},Bgra8Srgb:product(Bgra8Srgb){},R16:product(R16){},R16Snorm:product(R16Snorm){},Rg16:product(Rg16){},Rg16Snorm:product(Rg16Snorm){},Rgba16:product(Rgba16){},Rgba16Snorm:product(Rgba16Snorm){},B4G4R4A4Unorm:product(B4G4R4A4Unorm){},R5G6B5Unorm:product(R5G6B5Unorm){},A1R5G5B5Unorm:product(A1R5G5B5Unorm){},Rgb10A2:product(Rgb10A2){},R16f:product(R16f){},Rg16f:product(Rg16f){},Rgba16f:product(Rgba16f){},R11fG11fB10f:product(R11fG11fB10f){},E5B9G9R9Ufloat:product(E5B9G9R9Ufloat){},D16Unorm:product(D16Unorm){},D32Sfloat:product(D32Sfloat){},S8Uint:product(S8Uint){},D32SfloatS8Depth:product(D32SfloatS8Depth){},D32SfloatS8Stencil:product(D32SfloatS8Stencil){},Etc2R8g8b8UnormBlock:product(Etc2R8g8b8UnormBlock){},Etc2R8g8b8SrgbBlock:product(Etc2R8g8b8SrgbBlock){},Etc2R8g8b8a1UnormBlock:product(Etc2R8g8b8a1UnormBlock){},Etc2R8g8b8a1SrgbBlock:product(Etc2R8g8b8a1SrgbBlock){},Etc2R8g8b8a8UnormBlock:product(Etc2R8g8b8a8UnormBlock){},Etc2R8g8b8a8SrgbBlock:product(Etc2R8g8b8a8SrgbBlock){},EacR11UnormBlock:product(EacR11UnormBlock){},EacR11SnormBlock:product(EacR11SnormBlock){},EacR11g11UnormBlock:product(EacR11g11UnormBlock){},EacR11g11SnormBlock:product(EacR11g11SnormBlock){}},volume:bool},destination:product(GraphicsImage){memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},levels:alias(NonEmptyGraphicsImageLevels,seq[product(GraphicsImageLevel){extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]},offset:alias(Long,i64)[positive0],rowPitch:alias(Long,i64)[positive],slicePitch:alias(Long,i64)[positive],layerPitch:alias(Long,i64)[positive]}])[minimum-length[1]],layers:alias(Int,i32)[positive],samples:alias(Int,i32)[one-of[1,4]],format:sum(GraphicsFormat){R32f:product(R32f){},Rg32f:product(Rg32f){},Rgba32f:product(Rgba32f){},DecodedRgb32f:product(DecodedRgb32f){},Rgb10a2ui:product(Rgb10a2ui){},R8ui:product(R8ui){},R8i:product(R8i){},Rg8ui:product(Rg8ui){},Rg8i:product(Rg8i){},Rgba8ui:product(Rgba8ui){},Rgba8i:product(Rgba8i){},R16ui:product(R16ui){},R16i:product(R16i){},Rg16ui:product(Rg16ui){},Rg16i:product(Rg16i){},Rgba16ui:product(Rgba16ui){},Rgba16i:product(Rgba16i){},R32ui:product(R32ui){},R32i:product(R32i){},Rg32ui:product(Rg32ui){},Rg32i:product(Rg32i){},Rgba32ui:product(Rgba32ui){},Rgba32i:product(Rgba32i){},R8:product(R8){},R8Snorm:product(R8Snorm){},Rg8:product(Rg8){},Rg8Snorm:product(Rg8Snorm){},Rgba8:product(Rgba8){},Rgba8Snorm:product(Rgba8Snorm){},Rgba8Srgb:product(Rgba8Srgb){},Bgra8Unorm:product(Bgra8Unorm){},Bgra8Srgb:product(Bgra8Srgb){},R16:product(R16){},R16Snorm:product(R16Snorm){},Rg16:product(Rg16){},Rg16Snorm:product(Rg16Snorm){},Rgba16:product(Rgba16){},Rgba16Snorm:product(Rgba16Snorm){},B4G4R4A4Unorm:product(B4G4R4A4Unorm){},R5G6B5Unorm:product(R5G6B5Unorm){},A1R5G5B5Unorm:product(A1R5G5B5Unorm){},Rgb10A2:product(Rgb10A2){},R16f:product(R16f){},Rg16f:product(Rg16f){},Rgba16f:product(Rgba16f){},R11fG11fB10f:product(R11fG11fB10f){},E5B9G9R9Ufloat:product(E5B9G9R9Ufloat){},D16Unorm:product(D16Unorm){},D32Sfloat:product(D32Sfloat){},S8Uint:product(S8Uint){},D32SfloatS8Depth:product(D32SfloatS8Depth){},D32SfloatS8Stencil:product(D32SfloatS8Stencil){},Etc2R8g8b8UnormBlock:product(Etc2R8g8b8UnormBlock){},Etc2R8g8b8SrgbBlock:product(Etc2R8g8b8SrgbBlock){},Etc2R8g8b8a1UnormBlock:product(Etc2R8g8b8a1UnormBlock){},Etc2R8g8b8a1SrgbBlock:product(Etc2R8g8b8a1SrgbBlock){},Etc2R8g8b8a8UnormBlock:product(Etc2R8g8b8a8UnormBlock){},Etc2R8g8b8a8SrgbBlock:product(Etc2R8g8b8a8SrgbBlock){},EacR11UnormBlock:product(EacR11UnormBlock){},EacR11SnormBlock:product(EacR11SnormBlock){},EacR11g11UnormBlock:product(EacR11g11UnormBlock){},EacR11g11SnormBlock:product(EacR11g11SnormBlock){}},volume:bool},regions:seq[product(GraphicsImageCopy){source:product(GraphicsLayers){level:alias(Int,i32)[positive0],baseLayer:alias(Int,i32)[positive0],layers:alias(Int,i32)[positive],colour:bool,depth:bool,stencil:bool},sourceOffset:product(GraphicsOffset){x:i32,y:i32,z:i32},destination:product(GraphicsLayers){level:alias(Int,i32)[positive0],baseLayer:alias(Int,i32)[positive0],layers:alias(Int,i32)[positive],colour:bool,depth:bool,stencil:bool},destinationOffset:product(GraphicsOffset){x:i32,y:i32,z:i32},extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]}}]},CopyBufferImage:product(CopyBufferImage){buffer:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},image:product(GraphicsImage){memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},levels:alias(NonEmptyGraphicsImageLevels,seq[product(GraphicsImageLevel){extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]},offset:alias(Long,i64)[positive0],rowPitch:alias(Long,i64)[positive],slicePitch:alias(Long,i64)[positive],layerPitch:alias(Long,i64)[positive]}])[minimum-length[1]],layers:alias(Int,i32)[positive],samples:alias(Int,i32)[one-of[1,4]],format:sum(GraphicsFormat){R32f:product(R32f){},Rg32f:product(Rg32f){},Rgba32f:product(Rgba32f){},DecodedRgb32f:product(DecodedRgb32f){},Rgb10a2ui:product(Rgb10a2ui){},R8ui:product(R8ui){},R8i:product(R8i){},Rg8ui:product(Rg8ui){},Rg8i:product(Rg8i){},Rgba8ui:product(Rgba8ui){},Rgba8i:product(Rgba8i){},R16ui:product(R16ui){},R16i:product(R16i){},Rg16ui:product(Rg16ui){},Rg16i:product(Rg16i){},Rgba16ui:product(Rgba16ui){},Rgba16i:product(Rgba16i){},R32ui:product(R32ui){},R32i:product(R32i){},Rg32ui:product(Rg32ui){},Rg32i:product(Rg32i){},Rgba32ui:product(Rgba32ui){},Rgba32i:product(Rgba32i){},R8:product(R8){},R8Snorm:product(R8Snorm){},Rg8:product(Rg8){},Rg8Snorm:product(Rg8Snorm){},Rgba8:product(Rgba8){},Rgba8Snorm:product(Rgba8Snorm){},Rgba8Srgb:product(Rgba8Srgb){},Bgra8Unorm:product(Bgra8Unorm){},Bgra8Srgb:product(Bgra8Srgb){},R16:product(R16){},R16Snorm:product(R16Snorm){},Rg16:product(Rg16){},Rg16Snorm:product(Rg16Snorm){},Rgba16:product(Rgba16){},Rgba16Snorm:product(Rgba16Snorm){},B4G4R4A4Unorm:product(B4G4R4A4Unorm){},R5G6B5Unorm:product(R5G6B5Unorm){},A1R5G5B5Unorm:product(A1R5G5B5Unorm){},Rgb10A2:product(Rgb10A2){},R16f:product(R16f){},Rg16f:product(Rg16f){},Rgba16f:product(Rgba16f){},R11fG11fB10f:product(R11fG11fB10f){},E5B9G9R9Ufloat:product(E5B9G9R9Ufloat){},D16Unorm:product(D16Unorm){},D32Sfloat:product(D32Sfloat){},S8Uint:product(S8Uint){},D32SfloatS8Depth:product(D32SfloatS8Depth){},D32SfloatS8Stencil:product(D32SfloatS8Stencil){},Etc2R8g8b8UnormBlock:product(Etc2R8g8b8UnormBlock){},Etc2R8g8b8SrgbBlock:product(Etc2R8g8b8SrgbBlock){},Etc2R8g8b8a1UnormBlock:product(Etc2R8g8b8a1UnormBlock){},Etc2R8g8b8a1SrgbBlock:product(Etc2R8g8b8a1SrgbBlock){},Etc2R8g8b8a8UnormBlock:product(Etc2R8g8b8a8UnormBlock){},Etc2R8g8b8a8SrgbBlock:product(Etc2R8g8b8a8SrgbBlock){},EacR11UnormBlock:product(EacR11UnormBlock){},EacR11SnormBlock:product(EacR11SnormBlock){},EacR11g11UnormBlock:product(EacR11g11UnormBlock){},EacR11g11SnormBlock:product(EacR11g11SnormBlock){}},volume:bool},regions:seq[product(GraphicsBufferImageCopy){bufferOffset:alias(Long,i64)[positive0],rowLength:alias(Int,i32)[positive0],imageHeight:alias(Int,i32)[positive0],image:product(GraphicsLayers){level:alias(Int,i32)[positive0],baseLayer:alias(Int,i32)[positive0],layers:alias(Int,i32)[positive],colour:bool,depth:bool,stencil:bool},offset:product(GraphicsOffset){x:i32,y:i32,z:i32},extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]}}],toImage:bool},Blit:product(Blit){source:product(GraphicsImage){memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},levels:alias(NonEmptyGraphicsImageLevels,seq[product(GraphicsImageLevel){extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]},offset:alias(Long,i64)[positive0],rowPitch:alias(Long,i64)[positive],slicePitch:alias(Long,i64)[positive],layerPitch:alias(Long,i64)[positive]}])[minimum-length[1]],layers:alias(Int,i32)[positive],samples:alias(Int,i32)[one-of[1,4]],format:sum(GraphicsFormat){R32f:product(R32f){},Rg32f:product(Rg32f){},Rgba32f:product(Rgba32f){},DecodedRgb32f:product(DecodedRgb32f){},Rgb10a2ui:product(Rgb10a2ui){},R8ui:product(R8ui){},R8i:product(R8i){},Rg8ui:product(Rg8ui){},Rg8i:product(Rg8i){},Rgba8ui:product(Rgba8ui){},Rgba8i:product(Rgba8i){},R16ui:product(R16ui){},R16i:product(R16i){},Rg16ui:product(Rg16ui){},Rg16i:product(Rg16i){},Rgba16ui:product(Rgba16ui){},Rgba16i:product(Rgba16i){},R32ui:product(R32ui){},R32i:product(R32i){},Rg32ui:product(Rg32ui){},Rg32i:product(Rg32i){},Rgba32ui:product(Rgba32ui){},Rgba32i:product(Rgba32i){},R8:product(R8){},R8Snorm:product(R8Snorm){},Rg8:product(Rg8){},Rg8Snorm:product(Rg8Snorm){},Rgba8:product(Rgba8){},Rgba8Snorm:product(Rgba8Snorm){},Rgba8Srgb:product(Rgba8Srgb){},Bgra8Unorm:product(Bgra8Unorm){},Bgra8Srgb:product(Bgra8Srgb){},R16:product(R16){},R16Snorm:product(R16Snorm){},Rg16:product(Rg16){},Rg16Snorm:product(Rg16Snorm){},Rgba16:product(Rgba16){},Rgba16Snorm:product(Rgba16Snorm){},B4G4R4A4Unorm:product(B4G4R4A4Unorm){},R5G6B5Unorm:product(R5G6B5Unorm){},A1R5G5B5Unorm:product(A1R5G5B5Unorm){},Rgb10A2:product(Rgb10A2){},R16f:product(R16f){},Rg16f:product(Rg16f){},Rgba16f:product(Rgba16f){},R11fG11fB10f:product(R11fG11fB10f){},E5B9G9R9Ufloat:product(E5B9G9R9Ufloat){},D16Unorm:product(D16Unorm){},D32Sfloat:product(D32Sfloat){},S8Uint:product(S8Uint){},D32SfloatS8Depth:product(D32SfloatS8Depth){},D32SfloatS8Stencil:product(D32SfloatS8Stencil){},Etc2R8g8b8UnormBlock:product(Etc2R8g8b8UnormBlock){},Etc2R8g8b8SrgbBlock:product(Etc2R8g8b8SrgbBlock){},Etc2R8g8b8a1UnormBlock:product(Etc2R8g8b8a1UnormBlock){},Etc2R8g8b8a1SrgbBlock:product(Etc2R8g8b8a1SrgbBlock){},Etc2R8g8b8a8UnormBlock:product(Etc2R8g8b8a8UnormBlock){},Etc2R8g8b8a8SrgbBlock:product(Etc2R8g8b8a8SrgbBlock){},EacR11UnormBlock:product(EacR11UnormBlock){},EacR11SnormBlock:product(EacR11SnormBlock){},EacR11g11UnormBlock:product(EacR11g11UnormBlock){},EacR11g11SnormBlock:product(EacR11g11SnormBlock){}},volume:bool},destination:product(GraphicsImage){memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},levels:alias(NonEmptyGraphicsImageLevels,seq[product(GraphicsImageLevel){extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]},offset:alias(Long,i64)[positive0],rowPitch:alias(Long,i64)[positive],slicePitch:alias(Long,i64)[positive],layerPitch:alias(Long,i64)[positive]}])[minimum-length[1]],layers:alias(Int,i32)[positive],samples:alias(Int,i32)[one-of[1,4]],format:sum(GraphicsFormat){R32f:product(R32f){},Rg32f:product(Rg32f){},Rgba32f:product(Rgba32f){},DecodedRgb32f:product(DecodedRgb32f){},Rgb10a2ui:product(Rgb10a2ui){},R8ui:product(R8ui){},R8i:product(R8i){},Rg8ui:product(Rg8ui){},Rg8i:product(Rg8i){},Rgba8ui:product(Rgba8ui){},Rgba8i:product(Rgba8i){},R16ui:product(R16ui){},R16i:product(R16i){},Rg16ui:product(Rg16ui){},Rg16i:product(Rg16i){},Rgba16ui:product(Rgba16ui){},Rgba16i:product(Rgba16i){},R32ui:product(R32ui){},R32i:product(R32i){},Rg32ui:product(Rg32ui){},Rg32i:product(Rg32i){},Rgba32ui:product(Rgba32ui){},Rgba32i:product(Rgba32i){},R8:product(R8){},R8Snorm:product(R8Snorm){},Rg8:product(Rg8){},Rg8Snorm:product(Rg8Snorm){},Rgba8:product(Rgba8){},Rgba8Snorm:product(Rgba8Snorm){},Rgba8Srgb:product(Rgba8Srgb){},Bgra8Unorm:product(Bgra8Unorm){},Bgra8Srgb:product(Bgra8Srgb){},R16:product(R16){},R16Snorm:product(R16Snorm){},Rg16:product(Rg16){},Rg16Snorm:product(Rg16Snorm){},Rgba16:product(Rgba16){},Rgba16Snorm:product(Rgba16Snorm){},B4G4R4A4Unorm:product(B4G4R4A4Unorm){},R5G6B5Unorm:product(R5G6B5Unorm){},A1R5G5B5Unorm:product(A1R5G5B5Unorm){},Rgb10A2:product(Rgb10A2){},R16f:product(R16f){},Rg16f:product(Rg16f){},Rgba16f:product(Rgba16f){},R11fG11fB10f:product(R11fG11fB10f){},E5B9G9R9Ufloat:product(E5B9G9R9Ufloat){},D16Unorm:product(D16Unorm){},D32Sfloat:product(D32Sfloat){},S8Uint:product(S8Uint){},D32SfloatS8Depth:product(D32SfloatS8Depth){},D32SfloatS8Stencil:product(D32SfloatS8Stencil){},Etc2R8g8b8UnormBlock:product(Etc2R8g8b8UnormBlock){},Etc2R8g8b8SrgbBlock:product(Etc2R8g8b8SrgbBlock){},Etc2R8g8b8a1UnormBlock:product(Etc2R8g8b8a1UnormBlock){},Etc2R8g8b8a1SrgbBlock:product(Etc2R8g8b8a1SrgbBlock){},Etc2R8g8b8a8UnormBlock:product(Etc2R8g8b8a8UnormBlock){},Etc2R8g8b8a8SrgbBlock:product(Etc2R8g8b8a8SrgbBlock){},EacR11UnormBlock:product(EacR11UnormBlock){},EacR11SnormBlock:product(EacR11SnormBlock){},EacR11g11UnormBlock:product(EacR11g11UnormBlock){},EacR11g11SnormBlock:product(EacR11g11SnormBlock){}},volume:bool},regions:seq[product(GraphicsBlit){source:product(GraphicsLayers){level:alias(Int,i32)[positive0],baseLayer:alias(Int,i32)[positive0],layers:alias(Int,i32)[positive],colour:bool,depth:bool,stencil:bool},sourceBegin:product(GraphicsOffset){x:i32,y:i32,z:i32},sourceEnd:product(GraphicsOffset){x:i32,y:i32,z:i32},destination:product(GraphicsLayers){level:alias(Int,i32)[positive0],baseLayer:alias(Int,i32)[positive0],layers:alias(Int,i32)[positive],colour:bool,depth:bool,stencil:bool},destinationBegin:product(GraphicsOffset){x:i32,y:i32,z:i32},destinationEnd:product(GraphicsOffset){x:i32,y:i32,z:i32}}],linear:bool},Resolve:product(Resolve){source:product(GraphicsImage){memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},levels:alias(NonEmptyGraphicsImageLevels,seq[product(GraphicsImageLevel){extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]},offset:alias(Long,i64)[positive0],rowPitch:alias(Long,i64)[positive],slicePitch:alias(Long,i64)[positive],layerPitch:alias(Long,i64)[positive]}])[minimum-length[1]],layers:alias(Int,i32)[positive],samples:alias(Int,i32)[one-of[1,4]],format:sum(GraphicsFormat){R32f:product(R32f){},Rg32f:product(Rg32f){},Rgba32f:product(Rgba32f){},DecodedRgb32f:product(DecodedRgb32f){},Rgb10a2ui:product(Rgb10a2ui){},R8ui:product(R8ui){},R8i:product(R8i){},Rg8ui:product(Rg8ui){},Rg8i:product(Rg8i){},Rgba8ui:product(Rgba8ui){},Rgba8i:product(Rgba8i){},R16ui:product(R16ui){},R16i:product(R16i){},Rg16ui:product(Rg16ui){},Rg16i:product(Rg16i){},Rgba16ui:product(Rgba16ui){},Rgba16i:product(Rgba16i){},R32ui:product(R32ui){},R32i:product(R32i){},Rg32ui:product(Rg32ui){},Rg32i:product(Rg32i){},Rgba32ui:product(Rgba32ui){},Rgba32i:product(Rgba32i){},R8:product(R8){},R8Snorm:product(R8Snorm){},Rg8:product(Rg8){},Rg8Snorm:product(Rg8Snorm){},Rgba8:product(Rgba8){},Rgba8Snorm:product(Rgba8Snorm){},Rgba8Srgb:product(Rgba8Srgb){},Bgra8Unorm:product(Bgra8Unorm){},Bgra8Srgb:product(Bgra8Srgb){},R16:product(R16){},R16Snorm:product(R16Snorm){},Rg16:product(Rg16){},Rg16Snorm:product(Rg16Snorm){},Rgba16:product(Rgba16){},Rgba16Snorm:product(Rgba16Snorm){},B4G4R4A4Unorm:product(B4G4R4A4Unorm){},R5G6B5Unorm:product(R5G6B5Unorm){},A1R5G5B5Unorm:product(A1R5G5B5Unorm){},Rgb10A2:product(Rgb10A2){},R16f:product(R16f){},Rg16f:product(Rg16f){},Rgba16f:product(Rgba16f){},R11fG11fB10f:product(R11fG11fB10f){},E5B9G9R9Ufloat:product(E5B9G9R9Ufloat){},D16Unorm:product(D16Unorm){},D32Sfloat:product(D32Sfloat){},S8Uint:product(S8Uint){},D32SfloatS8Depth:product(D32SfloatS8Depth){},D32SfloatS8Stencil:product(D32SfloatS8Stencil){},Etc2R8g8b8UnormBlock:product(Etc2R8g8b8UnormBlock){},Etc2R8g8b8SrgbBlock:product(Etc2R8g8b8SrgbBlock){},Etc2R8g8b8a1UnormBlock:product(Etc2R8g8b8a1UnormBlock){},Etc2R8g8b8a1SrgbBlock:product(Etc2R8g8b8a1SrgbBlock){},Etc2R8g8b8a8UnormBlock:product(Etc2R8g8b8a8UnormBlock){},Etc2R8g8b8a8SrgbBlock:product(Etc2R8g8b8a8SrgbBlock){},EacR11UnormBlock:product(EacR11UnormBlock){},EacR11SnormBlock:product(EacR11SnormBlock){},EacR11g11UnormBlock:product(EacR11g11UnormBlock){},EacR11g11SnormBlock:product(EacR11g11SnormBlock){}},volume:bool},destination:product(GraphicsImage){memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},levels:alias(NonEmptyGraphicsImageLevels,seq[product(GraphicsImageLevel){extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]},offset:alias(Long,i64)[positive0],rowPitch:alias(Long,i64)[positive],slicePitch:alias(Long,i64)[positive],layerPitch:alias(Long,i64)[positive]}])[minimum-length[1]],layers:alias(Int,i32)[positive],samples:alias(Int,i32)[one-of[1,4]],format:sum(GraphicsFormat){R32f:product(R32f){},Rg32f:product(Rg32f){},Rgba32f:product(Rgba32f){},DecodedRgb32f:product(DecodedRgb32f){},Rgb10a2ui:product(Rgb10a2ui){},R8ui:product(R8ui){},R8i:product(R8i){},Rg8ui:product(Rg8ui){},Rg8i:product(Rg8i){},Rgba8ui:product(Rgba8ui){},Rgba8i:product(Rgba8i){},R16ui:product(R16ui){},R16i:product(R16i){},Rg16ui:product(Rg16ui){},Rg16i:product(Rg16i){},Rgba16ui:product(Rgba16ui){},Rgba16i:product(Rgba16i){},R32ui:product(R32ui){},R32i:product(R32i){},Rg32ui:product(Rg32ui){},Rg32i:product(Rg32i){},Rgba32ui:product(Rgba32ui){},Rgba32i:product(Rgba32i){},R8:product(R8){},R8Snorm:product(R8Snorm){},Rg8:product(Rg8){},Rg8Snorm:product(Rg8Snorm){},Rgba8:product(Rgba8){},Rgba8Snorm:product(Rgba8Snorm){},Rgba8Srgb:product(Rgba8Srgb){},Bgra8Unorm:product(Bgra8Unorm){},Bgra8Srgb:product(Bgra8Srgb){},R16:product(R16){},R16Snorm:product(R16Snorm){},Rg16:product(Rg16){},Rg16Snorm:product(Rg16Snorm){},Rgba16:product(Rgba16){},Rgba16Snorm:product(Rgba16Snorm){},B4G4R4A4Unorm:product(B4G4R4A4Unorm){},R5G6B5Unorm:product(R5G6B5Unorm){},A1R5G5B5Unorm:product(A1R5G5B5Unorm){},Rgb10A2:product(Rgb10A2){},R16f:product(R16f){},Rg16f:product(Rg16f){},Rgba16f:product(Rgba16f){},R11fG11fB10f:product(R11fG11fB10f){},E5B9G9R9Ufloat:product(E5B9G9R9Ufloat){},D16Unorm:product(D16Unorm){},D32Sfloat:product(D32Sfloat){},S8Uint:product(S8Uint){},D32SfloatS8Depth:product(D32SfloatS8Depth){},D32SfloatS8Stencil:product(D32SfloatS8Stencil){},Etc2R8g8b8UnormBlock:product(Etc2R8g8b8UnormBlock){},Etc2R8g8b8SrgbBlock:product(Etc2R8g8b8SrgbBlock){},Etc2R8g8b8a1UnormBlock:product(Etc2R8g8b8a1UnormBlock){},Etc2R8g8b8a1SrgbBlock:product(Etc2R8g8b8a1SrgbBlock){},Etc2R8g8b8a8UnormBlock:product(Etc2R8g8b8a8UnormBlock){},Etc2R8g8b8a8SrgbBlock:product(Etc2R8g8b8a8SrgbBlock){},EacR11UnormBlock:product(EacR11UnormBlock){},EacR11SnormBlock:product(EacR11SnormBlock){},EacR11g11UnormBlock:product(EacR11g11UnormBlock){},EacR11g11SnormBlock:product(EacR11g11SnormBlock){}},volume:bool},regions:seq[product(GraphicsImageCopy){source:product(GraphicsLayers){level:alias(Int,i32)[positive0],baseLayer:alias(Int,i32)[positive0],layers:alias(Int,i32)[positive],colour:bool,depth:bool,stencil:bool},sourceOffset:product(GraphicsOffset){x:i32,y:i32,z:i32},destination:product(GraphicsLayers){level:alias(Int,i32)[positive0],baseLayer:alias(Int,i32)[positive0],layers:alias(Int,i32)[positive],colour:bool,depth:bool,stencil:bool},destinationOffset:product(GraphicsOffset){x:i32,y:i32,z:i32},extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]}}]},ResolveAttachment:product(ResolveAttachment){source:product(GraphicsImage){memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},levels:alias(NonEmptyGraphicsImageLevels,seq[product(GraphicsImageLevel){extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]},offset:alias(Long,i64)[positive0],rowPitch:alias(Long,i64)[positive],slicePitch:alias(Long,i64)[positive],layerPitch:alias(Long,i64)[positive]}])[minimum-length[1]],layers:alias(Int,i32)[positive],samples:alias(Int,i32)[one-of[1,4]],format:sum(GraphicsFormat){R32f:product(R32f){},Rg32f:product(Rg32f){},Rgba32f:product(Rgba32f){},DecodedRgb32f:product(DecodedRgb32f){},Rgb10a2ui:product(Rgb10a2ui){},R8ui:product(R8ui){},R8i:product(R8i){},Rg8ui:product(Rg8ui){},Rg8i:product(Rg8i){},Rgba8ui:product(Rgba8ui){},Rgba8i:product(Rgba8i){},R16ui:product(R16ui){},R16i:product(R16i){},Rg16ui:product(Rg16ui){},Rg16i:product(Rg16i){},Rgba16ui:product(Rgba16ui){},Rgba16i:product(Rgba16i){},R32ui:product(R32ui){},R32i:product(R32i){},Rg32ui:product(Rg32ui){},Rg32i:product(Rg32i){},Rgba32ui:product(Rgba32ui){},Rgba32i:product(Rgba32i){},R8:product(R8){},R8Snorm:product(R8Snorm){},Rg8:product(Rg8){},Rg8Snorm:product(Rg8Snorm){},Rgba8:product(Rgba8){},Rgba8Snorm:product(Rgba8Snorm){},Rgba8Srgb:product(Rgba8Srgb){},Bgra8Unorm:product(Bgra8Unorm){},Bgra8Srgb:product(Bgra8Srgb){},R16:product(R16){},R16Snorm:product(R16Snorm){},Rg16:product(Rg16){},Rg16Snorm:product(Rg16Snorm){},Rgba16:product(Rgba16){},Rgba16Snorm:product(Rgba16Snorm){},B4G4R4A4Unorm:product(B4G4R4A4Unorm){},R5G6B5Unorm:product(R5G6B5Unorm){},A1R5G5B5Unorm:product(A1R5G5B5Unorm){},Rgb10A2:product(Rgb10A2){},R16f:product(R16f){},Rg16f:product(Rg16f){},Rgba16f:product(Rgba16f){},R11fG11fB10f:product(R11fG11fB10f){},E5B9G9R9Ufloat:product(E5B9G9R9Ufloat){},D16Unorm:product(D16Unorm){},D32Sfloat:product(D32Sfloat){},S8Uint:product(S8Uint){},D32SfloatS8Depth:product(D32SfloatS8Depth){},D32SfloatS8Stencil:product(D32SfloatS8Stencil){},Etc2R8g8b8UnormBlock:product(Etc2R8g8b8UnormBlock){},Etc2R8g8b8SrgbBlock:product(Etc2R8g8b8SrgbBlock){},Etc2R8g8b8a1UnormBlock:product(Etc2R8g8b8a1UnormBlock){},Etc2R8g8b8a1SrgbBlock:product(Etc2R8g8b8a1SrgbBlock){},Etc2R8g8b8a8UnormBlock:product(Etc2R8g8b8a8UnormBlock){},Etc2R8g8b8a8SrgbBlock:product(Etc2R8g8b8a8SrgbBlock){},EacR11UnormBlock:product(EacR11UnormBlock){},EacR11SnormBlock:product(EacR11SnormBlock){},EacR11g11UnormBlock:product(EacR11g11UnormBlock){},EacR11g11SnormBlock:product(EacR11g11SnormBlock){}},volume:bool},destination:product(GraphicsImage){memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},levels:alias(NonEmptyGraphicsImageLevels,seq[product(GraphicsImageLevel){extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]},offset:alias(Long,i64)[positive0],rowPitch:alias(Long,i64)[positive],slicePitch:alias(Long,i64)[positive],layerPitch:alias(Long,i64)[positive]}])[minimum-length[1]],layers:alias(Int,i32)[positive],samples:alias(Int,i32)[one-of[1,4]],format:sum(GraphicsFormat){R32f:product(R32f){},Rg32f:product(Rg32f){},Rgba32f:product(Rgba32f){},DecodedRgb32f:product(DecodedRgb32f){},Rgb10a2ui:product(Rgb10a2ui){},R8ui:product(R8ui){},R8i:product(R8i){},Rg8ui:product(Rg8ui){},Rg8i:product(Rg8i){},Rgba8ui:product(Rgba8ui){},Rgba8i:product(Rgba8i){},R16ui:product(R16ui){},R16i:product(R16i){},Rg16ui:product(Rg16ui){},Rg16i:product(Rg16i){},Rgba16ui:product(Rgba16ui){},Rgba16i:product(Rgba16i){},R32ui:product(R32ui){},R32i:product(R32i){},Rg32ui:product(Rg32ui){},Rg32i:product(Rg32i){},Rgba32ui:product(Rgba32ui){},Rgba32i:product(Rgba32i){},R8:product(R8){},R8Snorm:product(R8Snorm){},Rg8:product(Rg8){},Rg8Snorm:product(Rg8Snorm){},Rgba8:product(Rgba8){},Rgba8Snorm:product(Rgba8Snorm){},Rgba8Srgb:product(Rgba8Srgb){},Bgra8Unorm:product(Bgra8Unorm){},Bgra8Srgb:product(Bgra8Srgb){},R16:product(R16){},R16Snorm:product(R16Snorm){},Rg16:product(Rg16){},Rg16Snorm:product(Rg16Snorm){},Rgba16:product(Rgba16){},Rgba16Snorm:product(Rgba16Snorm){},B4G4R4A4Unorm:product(B4G4R4A4Unorm){},R5G6B5Unorm:product(R5G6B5Unorm){},A1R5G5B5Unorm:product(A1R5G5B5Unorm){},Rgb10A2:product(Rgb10A2){},R16f:product(R16f){},Rg16f:product(Rg16f){},Rgba16f:product(Rgba16f){},R11fG11fB10f:product(R11fG11fB10f){},E5B9G9R9Ufloat:product(E5B9G9R9Ufloat){},D16Unorm:product(D16Unorm){},D32Sfloat:product(D32Sfloat){},S8Uint:product(S8Uint){},D32SfloatS8Depth:product(D32SfloatS8Depth){},D32SfloatS8Stencil:product(D32SfloatS8Stencil){},Etc2R8g8b8UnormBlock:product(Etc2R8g8b8UnormBlock){},Etc2R8g8b8SrgbBlock:product(Etc2R8g8b8SrgbBlock){},Etc2R8g8b8a1UnormBlock:product(Etc2R8g8b8a1UnormBlock){},Etc2R8g8b8a1SrgbBlock:product(Etc2R8g8b8a1SrgbBlock){},Etc2R8g8b8a8UnormBlock:product(Etc2R8g8b8a8UnormBlock){},Etc2R8g8b8a8SrgbBlock:product(Etc2R8g8b8a8SrgbBlock){},EacR11UnormBlock:product(EacR11UnormBlock){},EacR11SnormBlock:product(EacR11SnormBlock){},EacR11g11UnormBlock:product(EacR11g11UnormBlock){},EacR11g11SnormBlock:product(EacR11g11SnormBlock){}},volume:bool},area:product(GraphicsRect){x:i32,y:i32,width:alias(Int,i32)[positive0],height:alias(Int,i32)[positive0]},depth:sum(GraphicsResolve){None:product(None){},SampleZero:product(SampleZero){},Average:product(Average){},Min:product(Min){},Max:product(Max){}},stencil:sum(GraphicsResolve){None:product(None){},SampleZero:product(SampleZero){},Average:product(Average){},Min:product(Min){},Max:product(Max){}}}}]}}=>alias(CompletionId,i64)[positive],ReadGraphicsStatistics:product(ReadGraphicsStatistics){completion:alias(CompletionId,i64)[positive]}=>product(GraphicsStatistics){occlusionSamples:alias(Long,i64)[positive0]},UnloadProgram:product(UnloadProgram){program:alias(ProgramId,i64)[positive]}=>unit,DescribeProgram:product(DescribeProgram){program:alias(ProgramId,i64)[positive]}=>product(ProgramSymbols){kernels:seq[product(KernelSymbol){name:alias(String,text)[minimum-length[1],not-contains[00]],linkageName:alias(String,text)[minimum-length[1],not-contains[00]]}],globals:seq[product(GlobalSymbol){name:alias(String,text)[minimum-length[1],not-contains[00]],virtualAddress:option[alias(GuestAddress,i64)[]],size:alias(Long,i64)[positive0],pointerOffsets:seq[alias(Int,i32)[positive0]]}]},DescribeKernel:product(DescribeKernel){program:alias(ProgramId,i64)[positive],name:alias(String,text)[minimum-length[1],not-contains[00]]}=>product(KernelMetadata){argumentBlockSize:alias(Int,i32)[positive0],explicitParameterCount:alias(Int,i32)[positive0],parameters:seq[product(Parameter){width:alias(Int,i32)[positive],offset:alias(Int,i32)[positive0],kind:sum(ParameterKind){Scalar:product(Scalar){},Allocation:product(Allocation){},Local:product(Local){}},containsPointers:bool}],requiredWorkGroupSize:option[product(Dimensions){x:alias(Int,i32)[positive],y:alias(Int,i32)[positive],z:alias(Int,i32)[positive]}],workGroupSizeHint:option[product(Dimensions){x:alias(Int,i32)[positive],y:alias(Int,i32)[positive],z:alias(Int,i32)[positive]}],bindingIndices:seq[alias(Int,i32)[positive0]],entryPointId:option[alias(Int,i32)[positive0]],registerCount:option[alias(Int,i32)[positive0]]},DescribeImage:product(DescribeImage){encoding:sum(ProgramEncoding){PTXText:product(PTXText){},CUDACubin:product(CUDACubin){},HSACodeObject:product(HSACodeObject){},SPIRVOpenCLCompute:product(SPIRVOpenCLCompute){},SPIRVVulkanCompute:product(SPIRVVulkanCompute){},SPIRVVulkanGraphics:product(SPIRVVulkanGraphics){}},image:seq[i8],name:alias(String,text)[minimum-length[1],not-contains[00]]}=>product(KernelMetadata){argumentBlockSize:alias(Int,i32)[positive0],explicitParameterCount:alias(Int,i32)[positive0],parameters:seq[product(Parameter){width:alias(Int,i32)[positive],offset:alias(Int,i32)[positive0],kind:sum(ParameterKind){Scalar:product(Scalar){},Allocation:product(Allocation){},Local:product(Local){}},containsPointers:bool}],requiredWorkGroupSize:option[product(Dimensions){x:alias(Int,i32)[positive],y:alias(Int,i32)[positive],z:alias(Int,i32)[positive]}],workGroupSizeHint:option[product(Dimensions){x:alias(Int,i32)[positive],y:alias(Int,i32)[positive],z:alias(Int,i32)[positive]}],bindingIndices:seq[alias(Int,i32)[positive0]],entryPointId:option[alias(Int,i32)[positive0]],registerCount:option[alias(Int,i32)[positive0]]},DescribeGlobal:product(DescribeGlobal){program:alias(ProgramId,i64)[positive],name:alias(String,text)[minimum-length[1],not-contains[00]]}=>product(ProgramGlobal){size:alias(Long,i64)[positive0],initial:seq[i8],alias:option[product(ProgramGlobalAlias){name:alias(String,text)[minimum-length[1],not-contains[00]],offset:alias(Long,i64)[positive0]}]},BindProgramGlobal:product(BindProgramGlobal){program:alias(ProgramId,i64)[positive],name:alias(String,text)[minimum-length[1],not-contains[00]],allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0]}=>unit,WriteProgramGlobal:product(WriteProgramGlobal){program:alias(ProgramId,i64)[positive],name:alias(String,text)[minimum-length[1],not-contains[00]],offset:alias(Long,i64)[positive0],bytes:seq[i8]}=>unit,BuildKernel:product(BuildKernel){program:alias(ProgramId,i64)[positive],kernelName:alias(String,text)[minimum-length[1],not-contains[00]]}=>unit,Launch:product(Launch){program:alias(ProgramId,i64)[positive],kernelName:alias(String,text)[minimum-length[1],not-contains[00]],groupSize:product(Dimensions){x:alias(Int,i32)[positive],y:alias(Int,i32)[positive],z:alias(Int,i32)[positive]},groupCount:product(Dimensions){x:alias(Int,i32)[positive],y:alias(Int,i32)[positive],z:alias(Int,i32)[positive]},globalSize:product(GlobalDimensions){x:alias(Long,i64)[positive],y:alias(Long,i64)[positive],z:alias(Long,i64)[positive]},workDim:alias(Int,i32)[int-interval[1,3]],globalOffset:product(Coordinates){x:alias(Int,i32)[positive0],y:alias(Int,i32)[positive0],z:alias(Int,i32)[positive0]},privateSegmentBytes:alias(Int,i32)[positive0],groupSegmentBytes:alias(Int,i32)[positive0],arguments:seq[sum(Argument){Scalar:product(Scalar){bytes:alias(NonEmptyBytes,seq[i8])[minimum-length[1]]},Memory:product(Memory){region:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],range:option[alias(Long,i64)[positive]]},Local:product(Local){size:alias(Long,i64)[positive]},Descriptor:product(Descriptor){}}],pointerRelocations:seq[product(PointerRelocation){source:sum(RelocationSource){Memory:product(Memory){region:alias(AllocationId,i64)[positive]},Argument:product(Argument){index:alias(Int,i32)[positive0]}},sourceOffset:alias(Long,i64)[positive0],target:alias(AllocationId,i64)[positive],targetOffset:alias(Long,i64)[positive0]}],discoverArgumentRelocations:bool,descriptorBindings:seq[product(DescriptorBinding){argument:alias(Int,i32)[positive0],descriptor:sum(GraphicsDescriptor){Image:product(Image){source:product(GraphicsImage){memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},levels:alias(NonEmptyGraphicsImageLevels,seq[product(GraphicsImageLevel){extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]},offset:alias(Long,i64)[positive0],rowPitch:alias(Long,i64)[positive],slicePitch:alias(Long,i64)[positive],layerPitch:alias(Long,i64)[positive]}])[minimum-length[1]],layers:alias(Int,i32)[positive],samples:alias(Int,i32)[one-of[1,4]],format:sum(GraphicsFormat){R32f:product(R32f){},Rg32f:product(Rg32f){},Rgba32f:product(Rgba32f){},DecodedRgb32f:product(DecodedRgb32f){},Rgb10a2ui:product(Rgb10a2ui){},R8ui:product(R8ui){},R8i:product(R8i){},Rg8ui:product(Rg8ui){},Rg8i:product(Rg8i){},Rgba8ui:product(Rgba8ui){},Rgba8i:product(Rgba8i){},R16ui:product(R16ui){},R16i:product(R16i){},Rg16ui:product(Rg16ui){},Rg16i:product(Rg16i){},Rgba16ui:product(Rgba16ui){},Rgba16i:product(Rgba16i){},R32ui:product(R32ui){},R32i:product(R32i){},Rg32ui:product(Rg32ui){},Rg32i:product(Rg32i){},Rgba32ui:product(Rgba32ui){},Rgba32i:product(Rgba32i){},R8:product(R8){},R8Snorm:product(R8Snorm){},Rg8:product(Rg8){},Rg8Snorm:product(Rg8Snorm){},Rgba8:product(Rgba8){},Rgba8Snorm:product(Rgba8Snorm){},Rgba8Srgb:product(Rgba8Srgb){},Bgra8Unorm:product(Bgra8Unorm){},Bgra8Srgb:product(Bgra8Srgb){},R16:product(R16){},R16Snorm:product(R16Snorm){},Rg16:product(Rg16){},Rg16Snorm:product(Rg16Snorm){},Rgba16:product(Rgba16){},Rgba16Snorm:product(Rgba16Snorm){},B4G4R4A4Unorm:product(B4G4R4A4Unorm){},R5G6B5Unorm:product(R5G6B5Unorm){},A1R5G5B5Unorm:product(A1R5G5B5Unorm){},Rgb10A2:product(Rgb10A2){},R16f:product(R16f){},Rg16f:product(Rg16f){},Rgba16f:product(Rgba16f){},R11fG11fB10f:product(R11fG11fB10f){},E5B9G9R9Ufloat:product(E5B9G9R9Ufloat){},D16Unorm:product(D16Unorm){},D32Sfloat:product(D32Sfloat){},S8Uint:product(S8Uint){},D32SfloatS8Depth:product(D32SfloatS8Depth){},D32SfloatS8Stencil:product(D32SfloatS8Stencil){},Etc2R8g8b8UnormBlock:product(Etc2R8g8b8UnormBlock){},Etc2R8g8b8SrgbBlock:product(Etc2R8g8b8SrgbBlock){},Etc2R8g8b8a1UnormBlock:product(Etc2R8g8b8a1UnormBlock){},Etc2R8g8b8a1SrgbBlock:product(Etc2R8g8b8a1SrgbBlock){},Etc2R8g8b8a8UnormBlock:product(Etc2R8g8b8a8UnormBlock){},Etc2R8g8b8a8SrgbBlock:product(Etc2R8g8b8a8SrgbBlock){},EacR11UnormBlock:product(EacR11UnormBlock){},EacR11SnormBlock:product(EacR11SnormBlock){},EacR11g11UnormBlock:product(EacR11g11UnormBlock){},EacR11g11SnormBlock:product(EacR11g11SnormBlock){}},volume:bool},components:product(GraphicsComponents){r:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}},g:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}},b:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}},a:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}}}},Sampler:product(Sampler){value:product(GraphicsSampler){minFilter:sum(GraphicsFilter){Nearest:product(Nearest){},Linear:product(Linear){}},magFilter:sum(GraphicsFilter){Nearest:product(Nearest){},Linear:product(Linear){}},addressU:sum(GraphicsAddressMode){Repeat:product(Repeat){},MirroredRepeat:product(MirroredRepeat){},ClampToEdge:product(ClampToEdge){},ClampToBorder:product(ClampToBorder){},MirrorClampToEdge:product(MirrorClampToEdge){}},addressV:sum(GraphicsAddressMode){Repeat:product(Repeat){},MirroredRepeat:product(MirroredRepeat){},ClampToEdge:product(ClampToEdge){},ClampToBorder:product(ClampToBorder){},MirrorClampToEdge:product(MirrorClampToEdge){}},addressW:sum(GraphicsAddressMode){Repeat:product(Repeat){},MirroredRepeat:product(MirroredRepeat){},ClampToEdge:product(ClampToEdge){},ClampToBorder:product(ClampToBorder){},MirrorClampToEdge:product(MirrorClampToEdge){}},unnormalized:bool,compareEnable:bool,compareOp:sum(GraphicsCompare){Never:product(Never){},Less:product(Less){},Equal:product(Equal){},LessOrEqual:product(LessOrEqual){},Greater:product(Greater){},NotEqual:product(NotEqual){},GreaterOrEqual:product(GreaterOrEqual){},Always:product(Always){}},borderColour:sum(GraphicsBorderColour){FloatTransparentBlack:product(FloatTransparentBlack){},IntTransparentBlack:product(IntTransparentBlack){},FloatOpaqueBlack:product(FloatOpaqueBlack){},IntOpaqueBlack:product(IntOpaqueBlack){},FloatOpaqueWhite:product(FloatOpaqueWhite){},IntOpaqueWhite:product(IntOpaqueWhite){}},mipLodBias:f32,minLod:f32,maxLod:f32,mipmapMode:sum(GraphicsMipmapMode){Nearest:product(Nearest){},Linear:product(Linear){}}}},Combined:product(Combined){source:product(GraphicsImage){memory:product(GraphicsRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Long,i64)[positive]},levels:alias(NonEmptyGraphicsImageLevels,seq[product(GraphicsImageLevel){extent:product(GraphicsExtent){width:alias(Int,i32)[positive],height:alias(Int,i32)[positive],depth:alias(Int,i32)[positive]},offset:alias(Long,i64)[positive0],rowPitch:alias(Long,i64)[positive],slicePitch:alias(Long,i64)[positive],layerPitch:alias(Long,i64)[positive]}])[minimum-length[1]],layers:alias(Int,i32)[positive],samples:alias(Int,i32)[one-of[1,4]],format:sum(GraphicsFormat){R32f:product(R32f){},Rg32f:product(Rg32f){},Rgba32f:product(Rgba32f){},DecodedRgb32f:product(DecodedRgb32f){},Rgb10a2ui:product(Rgb10a2ui){},R8ui:product(R8ui){},R8i:product(R8i){},Rg8ui:product(Rg8ui){},Rg8i:product(Rg8i){},Rgba8ui:product(Rgba8ui){},Rgba8i:product(Rgba8i){},R16ui:product(R16ui){},R16i:product(R16i){},Rg16ui:product(Rg16ui){},Rg16i:product(Rg16i){},Rgba16ui:product(Rgba16ui){},Rgba16i:product(Rgba16i){},R32ui:product(R32ui){},R32i:product(R32i){},Rg32ui:product(Rg32ui){},Rg32i:product(Rg32i){},Rgba32ui:product(Rgba32ui){},Rgba32i:product(Rgba32i){},R8:product(R8){},R8Snorm:product(R8Snorm){},Rg8:product(Rg8){},Rg8Snorm:product(Rg8Snorm){},Rgba8:product(Rgba8){},Rgba8Snorm:product(Rgba8Snorm){},Rgba8Srgb:product(Rgba8Srgb){},Bgra8Unorm:product(Bgra8Unorm){},Bgra8Srgb:product(Bgra8Srgb){},R16:product(R16){},R16Snorm:product(R16Snorm){},Rg16:product(Rg16){},Rg16Snorm:product(Rg16Snorm){},Rgba16:product(Rgba16){},Rgba16Snorm:product(Rgba16Snorm){},B4G4R4A4Unorm:product(B4G4R4A4Unorm){},R5G6B5Unorm:product(R5G6B5Unorm){},A1R5G5B5Unorm:product(A1R5G5B5Unorm){},Rgb10A2:product(Rgb10A2){},R16f:product(R16f){},Rg16f:product(Rg16f){},Rgba16f:product(Rgba16f){},R11fG11fB10f:product(R11fG11fB10f){},E5B9G9R9Ufloat:product(E5B9G9R9Ufloat){},D16Unorm:product(D16Unorm){},D32Sfloat:product(D32Sfloat){},S8Uint:product(S8Uint){},D32SfloatS8Depth:product(D32SfloatS8Depth){},D32SfloatS8Stencil:product(D32SfloatS8Stencil){},Etc2R8g8b8UnormBlock:product(Etc2R8g8b8UnormBlock){},Etc2R8g8b8SrgbBlock:product(Etc2R8g8b8SrgbBlock){},Etc2R8g8b8a1UnormBlock:product(Etc2R8g8b8a1UnormBlock){},Etc2R8g8b8a1SrgbBlock:product(Etc2R8g8b8a1SrgbBlock){},Etc2R8g8b8a8UnormBlock:product(Etc2R8g8b8a8UnormBlock){},Etc2R8g8b8a8SrgbBlock:product(Etc2R8g8b8a8SrgbBlock){},EacR11UnormBlock:product(EacR11UnormBlock){},EacR11SnormBlock:product(EacR11SnormBlock){},EacR11g11UnormBlock:product(EacR11g11UnormBlock){},EacR11g11SnormBlock:product(EacR11g11SnormBlock){}},volume:bool},components:product(GraphicsComponents){r:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}},g:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}},b:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}},a:sum(GraphicsSwizzle){Identity:product(Identity){},Zero:product(Zero){},One:product(One){},R:product(R){},G:product(G){},B:product(B){},A:product(A){}}},sampler:product(GraphicsSampler){minFilter:sum(GraphicsFilter){Nearest:product(Nearest){},Linear:product(Linear){}},magFilter:sum(GraphicsFilter){Nearest:product(Nearest){},Linear:product(Linear){}},addressU:sum(GraphicsAddressMode){Repeat:product(Repeat){},MirroredRepeat:product(MirroredRepeat){},ClampToEdge:product(ClampToEdge){},ClampToBorder:product(ClampToBorder){},MirrorClampToEdge:product(MirrorClampToEdge){}},addressV:sum(GraphicsAddressMode){Repeat:product(Repeat){},MirroredRepeat:product(MirroredRepeat){},ClampToEdge:product(ClampToEdge){},ClampToBorder:product(ClampToBorder){},MirrorClampToEdge:product(MirrorClampToEdge){}},addressW:sum(GraphicsAddressMode){Repeat:product(Repeat){},MirroredRepeat:product(MirroredRepeat){},ClampToEdge:product(ClampToEdge){},ClampToBorder:product(ClampToBorder){},MirrorClampToEdge:product(MirrorClampToEdge){}},unnormalized:bool,compareEnable:bool,compareOp:sum(GraphicsCompare){Never:product(Never){},Less:product(Less){},Equal:product(Equal){},LessOrEqual:product(LessOrEqual){},Greater:product(Greater){},NotEqual:product(NotEqual){},GreaterOrEqual:product(GreaterOrEqual){},Always:product(Always){}},borderColour:sum(GraphicsBorderColour){FloatTransparentBlack:product(FloatTransparentBlack){},IntTransparentBlack:product(IntTransparentBlack){},FloatOpaqueBlack:product(FloatOpaqueBlack){},IntOpaqueBlack:product(IntOpaqueBlack){},FloatOpaqueWhite:product(FloatOpaqueWhite){},IntOpaqueWhite:product(IntOpaqueWhite){}},mipLodBias:f32,minLod:f32,maxLod:f32,mipmapMode:sum(GraphicsMipmapMode){Nearest:product(Nearest){},Linear:product(Linear){}}}}}}],argumentBuffer:option[product(ArgumentBuffer){region:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0]}],clusterSize:option[product(Dimensions){x:alias(Int,i32)[positive],y:alias(Int,i32)[positive],z:alias(Int,i32)[positive]}],cooperative:bool}=>alias(CompletionId,i64)[positive],QueryCompletion:product(QueryCompletion){completion:alias(CompletionId,i64)[positive]}=>sum(CompletionStatus){Pending:product(Pending){},Succeeded:product(Succeeded){},Failed:product(Failed){error:sum(ExecutionFailure){InvalidRequest:product(InvalidRequest){detail:alias(String,text)[minimum-length[1],not-contains[00]]},NotFound:product(NotFound){detail:alias(String,text)[minimum-length[1],not-contains[00]]},Unsupported:product(Unsupported){detail:alias(String,text)[minimum-length[1],not-contains[00]]},Failed:product(Failed){detail:alias(String,text)[minimum-length[1],not-contains[00]]},InvalidProgram:product(InvalidProgram){detail:alias(String,text)[minimum-length[1],not-contains[00]]},InvalidConfiguration:product(InvalidConfiguration){detail:alias(String,text)[minimum-length[1],not-contains[00]]},OutOfResources:product(OutOfResources){detail:alias(String,text)[minimum-length[1],not-contains[00]]},IllegalAddress:product(IllegalAddress){detail:alias(String,text)[minimum-length[1],not-contains[00]]},MisalignedAddress:product(MisalignedAddress){detail:alias(String,text)[minimum-length[1],not-contains[00]]},IllegalInstruction:product(IllegalInstruction){detail:alias(String,text)[minimum-length[1],not-contains[00]]},Trapped:product(Trapped){detail:alias(String,text)[minimum-length[1],not-contains[00]]},AssertionFailed:product(AssertionFailed){detail:alias(String,text)[minimum-length[1],not-contains[00]]},TimedOut:product(TimedOut){detail:alias(String,text)[minimum-length[1],not-contains[00]]}}}},CompletionDirtyRanges:product(CompletionDirtyRanges){completion:alias(CompletionId,i64)[positive],offset:alias(Int,i32)[positive0]}=>seq[product(DirtyRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Int,i32)[positive]}],CompletionPointerRelocations:product(CompletionPointerRelocations){completion:alias(CompletionId,i64)[positive],offset:alias(Int,i32)[positive0]}=>seq[product(PointerRelocation){source:sum(RelocationSource){Memory:product(Memory){region:alias(AllocationId,i64)[positive]},Argument:product(Argument){index:alias(Int,i32)[positive0]}},sourceOffset:alias(Long,i64)[positive0],target:alias(AllocationId,i64)[positive],targetOffset:alias(Long,i64)[positive0]}],CompletionPrints:product(CompletionPrints){completion:alias(CompletionId,i64)[positive]}=>text,ReleaseCompletion:product(ReleaseCompletion){completion:alias(CompletionId,i64)[positive]}=>unit};failure=sum(ExecutionFailure){InvalidRequest:product(InvalidRequest){detail:alias(String,text)[minimum-length[1],not-contains[00]]},NotFound:product(NotFound){detail:alias(String,text)[minimum-length[1],not-contains[00]]},Unsupported:product(Unsupported){detail:alias(String,text)[minimum-length[1],not-contains[00]]},Failed:product(Failed){detail:alias(String,text)[minimum-length[1],not-contains[00]]},InvalidProgram:product(InvalidProgram){detail:alias(String,text)[minimum-length[1],not-contains[00]]},InvalidConfiguration:product(InvalidConfiguration){detail:alias(String,text)[minimum-length[1],not-contains[00]]},OutOfResources:product(OutOfResources){detail:alias(String,text)[minimum-length[1],not-contains[00]]},IllegalAddress:product(IllegalAddress){detail:alias(String,text)[minimum-length[1],not-contains[00]]},MisalignedAddress:product(MisalignedAddress){detail:alias(String,text)[minimum-length[1],not-contains[00]]},IllegalInstruction:product(IllegalInstruction){detail:alias(String,text)[minimum-length[1],not-contains[00]]},Trapped:product(Trapped){detail:alias(String,text)[minimum-length[1],not-contains[00]]},AssertionFailed:product(AssertionFailed){detail:alias(String,text)[minimum-length[1],not-contains[00]]},TimedOut:product(TimedOut){detail:alias(String,text)[minimum-length[1],not-contains[00]]}}";
inline constexpr size_t RequestClientOffset = 4U;
inline constexpr size_t RequestSessionHighOffset = 12U;
inline constexpr size_t RequestSessionLowOffset = 20U;
inline constexpr size_t RequestTagSizeOffset = 28U;
inline constexpr size_t RequestPayloadSizeOffset = 32U;
inline constexpr size_t RequestHeaderBytes = 36U;
inline constexpr size_t ResponseStatusOffset = 4U;
inline constexpr size_t ResponsePayloadSizeOffset = 8U;
inline constexpr size_t ResponseHeaderBytes = 12U;
namespace operation {
inline constexpr std::string_view Ping = "Ping";
inline constexpr std::string_view EnableHostMemory = "EnableHostMemory";
inline constexpr std::string_view PollHostMemory = "PollHostMemory";
inline constexpr std::string_view SupplyHostMemory = "SupplyHostMemory";
inline constexpr std::string_view RegisterAllocation = "RegisterAllocation";
inline constexpr std::string_view SupplyHostSync = "SupplyHostSync";
inline constexpr std::string_view RegisterAllocationAddress = "RegisterAllocationAddress";
inline constexpr std::string_view ReleaseAllocation = "ReleaseAllocation";
inline constexpr std::string_view WriteAllocation = "WriteAllocation";
inline constexpr std::string_view ReadAllocation = "ReadAllocation";
inline constexpr std::string_view LoadProgram = "LoadProgram";
inline constexpr std::string_view DescribeSpecialization = "DescribeSpecialization";
inline constexpr std::string_view SpecializeImage = "SpecializeImage";
inline constexpr std::string_view PrepareShader = "PrepareShader";
inline constexpr std::string_view SubmitGraphics = "SubmitGraphics";
inline constexpr std::string_view ReadGraphicsStatistics = "ReadGraphicsStatistics";
inline constexpr std::string_view UnloadProgram = "UnloadProgram";
inline constexpr std::string_view DescribeProgram = "DescribeProgram";
inline constexpr std::string_view DescribeKernel = "DescribeKernel";
inline constexpr std::string_view DescribeImage = "DescribeImage";
inline constexpr std::string_view DescribeGlobal = "DescribeGlobal";
inline constexpr std::string_view BindProgramGlobal = "BindProgramGlobal";
inline constexpr std::string_view WriteProgramGlobal = "WriteProgramGlobal";
inline constexpr std::string_view BuildKernel = "BuildKernel";
inline constexpr std::string_view Launch = "Launch";
inline constexpr std::string_view QueryCompletion = "QueryCompletion";
inline constexpr std::string_view CompletionDirtyRanges = "CompletionDirtyRanges";
inline constexpr std::string_view CompletionPointerRelocations = "CompletionPointerRelocations";
inline constexpr std::string_view CompletionPrints = "CompletionPrints";
inline constexpr std::string_view ReleaseCompletion = "ReleaseCompletion";
}

struct Unit {};

struct CompletionId {
  int64_t value{};
};

struct GuestAddress {
  int64_t value{};
};

struct IntPositive {
  int32_t value{};
};

struct HostMemoryRange {
  GuestAddress address{};
  IntPositive size{};
};

struct IntOneOf0Or1Or2Or4Or8 {
  int32_t value{};
};

struct HostMemoryBytes {
  GuestAddress address{};
  std::vector<uint8_t> bytes{};
  IntOneOf0Or1Or2Or4Or8 atomicWidth{};
};

struct IntOneOf1Or2Or4Or8 {
  int32_t value{};
};

enum class HostMemoryAtomicOperation : uint8_t {
  CompareExchange = 0,
  Load = 1,
  Store = 2
};

struct HostMemoryAtomic {
  GuestAddress address{};
  IntOneOf1Or2Or4Or8 size{};
  int64_t expected{};
  int64_t desired{};
  HostMemoryAtomicOperation operation{};
};

struct HostMemorySync {
  int64_t ticket{};
  std::optional<HostMemoryAtomic> atomic{};
  std::vector<HostMemoryRange> publish{};
  std::vector<HostMemoryAtomic> atomicStores{};
  bool relaxed{};
};

struct HostMemoryBatch {
  std::vector<HostMemoryRange> reads{};
  std::vector<HostMemoryBytes> writes{};
  bool completed{};
  std::optional<HostMemorySync> sync{};
};

struct LongInterval1To2147483647 {
  int64_t value{};
};

struct AllocationId {
  int64_t value{};
};

struct LongPositive0 {
  int64_t value{};
};

struct IntPositive0 {
  int32_t value{};
};

enum class ProgramEncoding : uint8_t {
  PTXText = 0,
  CUDACubin = 1,
  HSACodeObject = 2,
  SPIRVOpenCLCompute = 3,
  SPIRVVulkanCompute = 4,
  SPIRVVulkanGraphics = 5
};

struct ProgramId {
  int64_t value{};
};

struct Binary32 {
  int64_t value{};
};

struct SpecializationInfo {
  Binary32 specId{};
  IntPositive byteSize{};
};

struct SpecializationConstant {
  Binary32 specId{};
  std::vector<uint8_t> value{};
};

struct StringMinimumLength1NotContains00 {
  std::string value{};
};

enum class ShaderStageKind : uint8_t {
  Vertex = 0,
  Fragment = 1,
  Compute = 2
};

struct StringMinimumLength1 {
  std::string value{};
};

enum class ShaderResourceKind : uint8_t {
  Buffer = 0,
  Image = 1,
  Sampler = 2,
  SampledImage = 3
};

struct ShaderDescriptor {
  IntPositive0 set{};
  IntPositive0 binding{};
  IntPositive0 element{};
  ShaderResourceKind resource{};
};

enum class ShaderDirection : uint8_t {
  Input = 0,
  Output = 1
};

struct IntInterval0To3 {
  int32_t value{};
};

struct ShaderQualifiers {
  bool flat{};
  bool noPerspective{};
  bool centroid{};
  IntInterval0To3 component{};
};

struct ShaderField {
  IntPositive0 offset{};
  IntPositive size{};
  std::optional<IntPositive0> location{};
  std::optional<IntPositive0> builtin{};
  ShaderQualifiers qualifiers{};
};

struct ShaderInterface {
  IntPositive id{};
  ShaderDirection direction{};
  IntPositive size{};
  std::optional<IntPositive0> builtin{};
  ShaderQualifiers qualifiers{};
  std::vector<ShaderField> fields{};
};

struct Dimensions {
  IntPositive x{};
  IntPositive y{};
  IntPositive z{};
};

struct PreparedShader {
  std::vector<uint8_t> image{};
  StringMinimumLength1 entry{};
  ShaderStageKind stage{};
  std::vector<ShaderDescriptor> descriptors{};
  bool pushConstants{};
  std::vector<ShaderInterface> interfaces{};
  std::optional<Dimensions> workgroup{};
  bool earlyFragmentTests{};
  bool fragmentQuads{};
};

struct LongPositive {
  int64_t value{};
};

struct GraphicsRange {
  AllocationId allocation{};
  LongPositive0 offset{};
  LongPositive size{};
};

struct RelocationSourceMemory {
  AllocationId region{};
};
struct RelocationSourceArgument {
  IntPositive0 index{};
};

using RelocationSource = std::variant<RelocationSourceMemory, RelocationSourceArgument>;

struct PointerRelocation {
  RelocationSource source{};
  LongPositive0 sourceOffset{};
  AllocationId target{};
  LongPositive0 targetOffset{};
};

struct GraphicsExtent {
  IntPositive width{};
  IntPositive height{};
  IntPositive depth{};
};

struct GraphicsImageLevel {
  GraphicsExtent extent{};
  LongPositive0 offset{};
  LongPositive rowPitch{};
  LongPositive slicePitch{};
  LongPositive layerPitch{};
};

struct NonEmptyGraphicsImageLevels {
  std::vector<GraphicsImageLevel> value{};
};

struct IntOneOf1Or4 {
  int32_t value{};
};

enum class GraphicsFormat : uint8_t {
  R32f = 0,
  Rg32f = 1,
  Rgba32f = 2,
  DecodedRgb32f = 3,
  Rgb10a2ui = 4,
  R8ui = 5,
  R8i = 6,
  Rg8ui = 7,
  Rg8i = 8,
  Rgba8ui = 9,
  Rgba8i = 10,
  R16ui = 11,
  R16i = 12,
  Rg16ui = 13,
  Rg16i = 14,
  Rgba16ui = 15,
  Rgba16i = 16,
  R32ui = 17,
  R32i = 18,
  Rg32ui = 19,
  Rg32i = 20,
  Rgba32ui = 21,
  Rgba32i = 22,
  R8 = 23,
  R8Snorm = 24,
  Rg8 = 25,
  Rg8Snorm = 26,
  Rgba8 = 27,
  Rgba8Snorm = 28,
  Rgba8Srgb = 29,
  Bgra8Unorm = 30,
  Bgra8Srgb = 31,
  R16 = 32,
  R16Snorm = 33,
  Rg16 = 34,
  Rg16Snorm = 35,
  Rgba16 = 36,
  Rgba16Snorm = 37,
  B4G4R4A4Unorm = 38,
  R5G6B5Unorm = 39,
  A1R5G5B5Unorm = 40,
  Rgb10A2 = 41,
  R16f = 42,
  Rg16f = 43,
  Rgba16f = 44,
  R11fG11fB10f = 45,
  E5B9G9R9Ufloat = 46,
  D16Unorm = 47,
  D32Sfloat = 48,
  S8Uint = 49,
  D32SfloatS8Depth = 50,
  D32SfloatS8Stencil = 51,
  Etc2R8g8b8UnormBlock = 52,
  Etc2R8g8b8SrgbBlock = 53,
  Etc2R8g8b8a1UnormBlock = 54,
  Etc2R8g8b8a1SrgbBlock = 55,
  Etc2R8g8b8a8UnormBlock = 56,
  Etc2R8g8b8a8SrgbBlock = 57,
  EacR11UnormBlock = 58,
  EacR11SnormBlock = 59,
  EacR11g11UnormBlock = 60,
  EacR11g11SnormBlock = 61
};

struct GraphicsImage {
  GraphicsRange memory{};
  NonEmptyGraphicsImageLevels levels{};
  IntPositive layers{};
  IntOneOf1Or4 samples{};
  GraphicsFormat format{};
  bool volume{};
};

enum class GraphicsSwizzle : uint8_t {
  Identity = 0,
  Zero = 1,
  One = 2,
  R = 3,
  G = 4,
  B = 5,
  A = 6
};

struct GraphicsComponents {
  GraphicsSwizzle r{};
  GraphicsSwizzle g{};
  GraphicsSwizzle b{};
  GraphicsSwizzle a{};
};

enum class GraphicsFilter : uint8_t {
  Nearest = 0,
  Linear = 1
};

enum class GraphicsAddressMode : uint8_t {
  Repeat = 0,
  MirroredRepeat = 1,
  ClampToEdge = 2,
  ClampToBorder = 3,
  MirrorClampToEdge = 4
};

enum class GraphicsCompare : uint8_t {
  Never = 0,
  Less = 1,
  Equal = 2,
  LessOrEqual = 3,
  Greater = 4,
  NotEqual = 5,
  GreaterOrEqual = 6,
  Always = 7
};

enum class GraphicsBorderColour : uint8_t {
  FloatTransparentBlack = 0,
  IntTransparentBlack = 1,
  FloatOpaqueBlack = 2,
  IntOpaqueBlack = 3,
  FloatOpaqueWhite = 4,
  IntOpaqueWhite = 5
};

enum class GraphicsMipmapMode : uint8_t {
  Nearest = 0,
  Linear = 1
};

struct GraphicsSampler {
  GraphicsFilter minFilter{};
  GraphicsFilter magFilter{};
  GraphicsAddressMode addressU{};
  GraphicsAddressMode addressV{};
  GraphicsAddressMode addressW{};
  bool unnormalized{};
  bool compareEnable{};
  GraphicsCompare compareOp{};
  GraphicsBorderColour borderColour{};
  float mipLodBias{};
  float minLod{};
  float maxLod{};
  GraphicsMipmapMode mipmapMode{};
};

struct GraphicsDescriptorImage {
  GraphicsImage source{};
  GraphicsComponents components{};
};
struct GraphicsDescriptorSampler {
  GraphicsSampler value{};
};
struct GraphicsDescriptorCombined {
  GraphicsImage source{};
  GraphicsComponents components{};
  GraphicsSampler sampler{};
};

using GraphicsDescriptor = std::variant<GraphicsDescriptorImage, GraphicsDescriptorSampler, GraphicsDescriptorCombined>;

struct DescriptorBinding {
  IntPositive0 argument{};
  GraphicsDescriptor descriptor{};
};

struct GraphicsShader {
  std::vector<uint8_t> image{};
  StringMinimumLength1NotContains00 entry{};
  std::vector<ShaderInterface> interfaces{};
  std::vector<std::optional<GraphicsRange>> arguments{};
  std::vector<PointerRelocation> relocations{};
  std::vector<uint8_t> pushConstants{};
  bool earlyFragmentTests{};
  bool fragmentQuads{};
  std::vector<DescriptorBinding> descriptorBindings{};
};

struct GraphicsVertexBuffer {
  IntPositive0 binding{};
  GraphicsRange memory{};
  IntPositive0 stride{};
  bool instanceRate{};
};

enum class GraphicsVertexEncoding : uint8_t {
  Unorm = 0,
  Snorm = 1,
  Uint = 2,
  Sint = 3,
  Uscaled = 4,
  Sscaled = 5,
  Float = 6,
  Ufloat = 7
};

struct GraphicsVertexFormat {
  GraphicsVertexEncoding encoding{};
  std::vector<IntPositive> widths{};
  IntPositive bytes{};
  bool bgra{};
};

struct GraphicsVertexAttribute {
  IntPositive0 location{};
  IntPositive0 binding{};
  IntPositive0 offset{};
  GraphicsVertexFormat format{};
};

struct IntOneOf2Or4 {
  int32_t value{};
};

struct GraphicsIndexBuffer {
  GraphicsRange memory{};
  IntOneOf2Or4 width{};
};

struct GraphicsDrawCall {
  IntPositive0 vertices{};
  IntPositive0 instances{};
  int32_t firstVertex{};
  int32_t firstInstance{};
  int32_t vertexOffset{};
};

struct GraphicsIndirect {
  GraphicsRange memory{};
  IntPositive0 count{};
  IntPositive0 stride{};
};

enum class GraphicsTopology : uint8_t {
  PointList = 0,
  LineList = 1,
  LineStrip = 2,
  TriangleList = 3,
  TriangleStrip = 4,
  TriangleFan = 5
};

struct GraphicsRasterState {
  GraphicsTopology topology{};
  bool restart{};
  bool discard{};
  bool cullFront{};
  bool cullBack{};
  bool frontCounterClockwise{};
  bool biasEnabled{};
  float biasConstant{};
  float biasClamp{};
  float biasSlope{};
  IntOneOf1Or4 samples{};
  int32_t sampleMask{};
  bool sampleShading{};
  bool alphaToCoverage{};
};

struct GraphicsViewport {
  float x{};
  float y{};
  float width{};
  float height{};
  float minDepth{};
  float maxDepth{};
};

struct GraphicsRect {
  int32_t x{};
  int32_t y{};
  IntPositive0 width{};
  IntPositive0 height{};
};

enum class GraphicsBlendFactor : uint8_t {
  Zero = 0,
  One = 1,
  SrcColor = 2,
  OneMinusSrcColor = 3,
  DstColor = 4,
  OneMinusDstColor = 5,
  SrcAlpha = 6,
  OneMinusSrcAlpha = 7,
  DstAlpha = 8,
  OneMinusDstAlpha = 9,
  ConstantColor = 10,
  OneMinusConstantColor = 11,
  ConstantAlpha = 12,
  OneMinusConstantAlpha = 13,
  SrcAlphaSaturate = 14
};

enum class GraphicsBlendOp : uint8_t {
  Add = 0,
  Subtract = 1,
  ReverseSubtract = 2,
  Min = 3,
  Max = 4
};

struct GraphicsBlend {
  bool enabled{};
  GraphicsBlendFactor sourceColour{};
  GraphicsBlendFactor destinationColour{};
  GraphicsBlendOp colourOp{};
  GraphicsBlendFactor sourceAlpha{};
  GraphicsBlendFactor destinationAlpha{};
  GraphicsBlendOp alphaOp{};
  int32_t writeMask{};
};

struct GraphicsColour {
  float r{};
  float g{};
  float b{};
  float a{};
};

enum class GraphicsStencilOp : uint8_t {
  Keep = 0,
  Zero = 1,
  Replace = 2,
  IncrementAndClamp = 3,
  DecrementAndClamp = 4,
  Invert = 5,
  IncrementAndWrap = 6,
  DecrementAndWrap = 7
};

struct GraphicsStencil {
  GraphicsStencilOp fail{};
  GraphicsStencilOp pass{};
  GraphicsStencilOp depthFail{};
  GraphicsCompare compare{};
  int32_t compareMask{};
  int32_t writeMask{};
  int32_t reference{};
};

struct GraphicsDepthStencil {
  bool depthTest{};
  bool depthWrite{};
  GraphicsCompare compare{};
  bool boundsTest{};
  float minBounds{};
  float maxBounds{};
  bool stencilTest{};
  GraphicsStencil front{};
  GraphicsStencil back{};
};

struct GraphicsDraw {
  GraphicsShader vertex{};
  std::optional<GraphicsShader> fragment{};
  std::vector<GraphicsVertexBuffer> vertexBuffers{};
  std::vector<GraphicsVertexAttribute> attributes{};
  std::optional<GraphicsIndexBuffer> index{};
  GraphicsDrawCall call{};
  std::optional<GraphicsIndirect> indirect{};
  GraphicsRasterState raster{};
  GraphicsViewport viewport{};
  GraphicsRect scissor{};
  GraphicsRect renderArea{};
  IntPositive width{};
  IntPositive height{};
  IntPositive0 viewIndex{};
  std::vector<std::optional<GraphicsImage>> colours{};
  std::optional<GraphicsImage> depth{};
  std::vector<GraphicsBlend> blends{};
  GraphicsColour blendConstants{};
  GraphicsDepthStencil depthStencil{};
};

struct GraphicsLayers {
  IntPositive0 level{};
  IntPositive0 baseLayer{};
  IntPositive layers{};
  bool colour{};
  bool depth{};
  bool stencil{};
};

struct GraphicsOffset {
  int32_t x{};
  int32_t y{};
  int32_t z{};
};

struct GraphicsImageCopy {
  GraphicsLayers source{};
  GraphicsOffset sourceOffset{};
  GraphicsLayers destination{};
  GraphicsOffset destinationOffset{};
  GraphicsExtent extent{};
};

struct GraphicsBufferImageCopy {
  LongPositive0 bufferOffset{};
  IntPositive0 rowLength{};
  IntPositive0 imageHeight{};
  GraphicsLayers image{};
  GraphicsOffset offset{};
  GraphicsExtent extent{};
};

struct GraphicsBlit {
  GraphicsLayers source{};
  GraphicsOffset sourceBegin{};
  GraphicsOffset sourceEnd{};
  GraphicsLayers destination{};
  GraphicsOffset destinationBegin{};
  GraphicsOffset destinationEnd{};
};

enum class GraphicsResolve : uint8_t {
  None = 0,
  SampleZero = 1,
  Average = 2,
  Min = 3,
  Max = 4
};

struct GraphicsCommandDraw {
  GraphicsDraw value{};
};
struct GraphicsCommandClearColour {
  GraphicsImage image{};
  GraphicsRect area{};
  std::vector<int32_t> bits{};
};
struct GraphicsCommandClearDepthStencil {
  GraphicsImage image{};
  GraphicsRect area{};
  float depth{};
  int32_t stencil{};
  bool clearDepth{};
  bool clearStencil{};
};
struct GraphicsCommandCopyImage {
  GraphicsImage source{};
  GraphicsImage destination{};
  std::vector<GraphicsImageCopy> regions{};
};
struct GraphicsCommandCopyBufferImage {
  GraphicsRange buffer{};
  GraphicsImage image{};
  std::vector<GraphicsBufferImageCopy> regions{};
  bool toImage{};
};
struct GraphicsCommandBlit {
  GraphicsImage source{};
  GraphicsImage destination{};
  std::vector<GraphicsBlit> regions{};
  bool linear{};
};
struct GraphicsCommandResolve {
  GraphicsImage source{};
  GraphicsImage destination{};
  std::vector<GraphicsImageCopy> regions{};
};
struct GraphicsCommandResolveAttachment {
  GraphicsImage source{};
  GraphicsImage destination{};
  GraphicsRect area{};
  GraphicsResolve depth{};
  GraphicsResolve stencil{};
};

using GraphicsCommand = std::variant<GraphicsCommandDraw, GraphicsCommandClearColour, GraphicsCommandClearDepthStencil, GraphicsCommandCopyImage, GraphicsCommandCopyBufferImage, GraphicsCommandBlit, GraphicsCommandResolve, GraphicsCommandResolveAttachment>;

struct GraphicsSubmission {
  std::vector<GraphicsCommand> commands{};
};

struct GraphicsStatistics {
  LongPositive0 occlusionSamples{};
};

struct KernelSymbol {
  StringMinimumLength1NotContains00 name{};
  StringMinimumLength1NotContains00 linkageName{};
};

struct GlobalSymbol {
  StringMinimumLength1NotContains00 name{};
  std::optional<GuestAddress> virtualAddress{};
  LongPositive0 size{};
  std::vector<IntPositive0> pointerOffsets{};
};

struct ProgramSymbols {
  std::vector<KernelSymbol> kernels{};
  std::vector<GlobalSymbol> globals{};
};

enum class ParameterKind : uint8_t {
  Scalar = 0,
  Allocation = 1,
  Local = 2
};

struct Parameter {
  IntPositive width{};
  IntPositive0 offset{};
  ParameterKind kind{};
  bool containsPointers{};
};

struct KernelMetadata {
  IntPositive0 argumentBlockSize{};
  IntPositive0 explicitParameterCount{};
  std::vector<Parameter> parameters{};
  std::optional<Dimensions> requiredWorkGroupSize{};
  std::optional<Dimensions> workGroupSizeHint{};
  std::vector<IntPositive0> bindingIndices{};
  std::optional<IntPositive0> entryPointId{};
  std::optional<IntPositive0> registerCount{};
};

struct ProgramGlobalAlias {
  StringMinimumLength1NotContains00 name{};
  LongPositive0 offset{};
};

struct ProgramGlobal {
  LongPositive0 size{};
  std::vector<uint8_t> initial{};
  std::optional<ProgramGlobalAlias> alias{};
};

struct GlobalDimensions {
  LongPositive x{};
  LongPositive y{};
  LongPositive z{};
};

struct IntInterval1To3 {
  int32_t value{};
};

struct Coordinates {
  IntPositive0 x{};
  IntPositive0 y{};
  IntPositive0 z{};
};

struct NonEmptyBytes {
  std::vector<uint8_t> value{};
};

struct ArgumentScalar {
  NonEmptyBytes bytes{};
};
struct ArgumentMemory {
  AllocationId region{};
  LongPositive0 offset{};
  std::optional<LongPositive> range{};
};
struct ArgumentLocal {
  LongPositive size{};
};
struct ArgumentDescriptor {};

using Argument = std::variant<ArgumentScalar, ArgumentMemory, ArgumentLocal, ArgumentDescriptor>;

struct ArgumentBuffer {
  AllocationId region{};
  LongPositive0 offset{};
};

struct ExecutionFailureInvalidRequest {
  StringMinimumLength1NotContains00 detail{};
};
struct ExecutionFailureNotFound {
  StringMinimumLength1NotContains00 detail{};
};
struct ExecutionFailureUnsupported {
  StringMinimumLength1NotContains00 detail{};
};
struct ExecutionFailureFailed {
  StringMinimumLength1NotContains00 detail{};
};
struct ExecutionFailureInvalidProgram {
  StringMinimumLength1NotContains00 detail{};
};
struct ExecutionFailureInvalidConfiguration {
  StringMinimumLength1NotContains00 detail{};
};
struct ExecutionFailureOutOfResources {
  StringMinimumLength1NotContains00 detail{};
};
struct ExecutionFailureIllegalAddress {
  StringMinimumLength1NotContains00 detail{};
};
struct ExecutionFailureMisalignedAddress {
  StringMinimumLength1NotContains00 detail{};
};
struct ExecutionFailureIllegalInstruction {
  StringMinimumLength1NotContains00 detail{};
};
struct ExecutionFailureTrapped {
  StringMinimumLength1NotContains00 detail{};
};
struct ExecutionFailureAssertionFailed {
  StringMinimumLength1NotContains00 detail{};
};
struct ExecutionFailureTimedOut {
  StringMinimumLength1NotContains00 detail{};
};

using ExecutionFailure = std::variant<ExecutionFailureInvalidRequest, ExecutionFailureNotFound, ExecutionFailureUnsupported, ExecutionFailureFailed, ExecutionFailureInvalidProgram, ExecutionFailureInvalidConfiguration, ExecutionFailureOutOfResources, ExecutionFailureIllegalAddress, ExecutionFailureMisalignedAddress, ExecutionFailureIllegalInstruction, ExecutionFailureTrapped, ExecutionFailureAssertionFailed, ExecutionFailureTimedOut>;

struct CompletionStatusPending {};
struct CompletionStatusSucceeded {};
struct CompletionStatusFailed {
  ExecutionFailure error{};
};

using CompletionStatus = std::variant<CompletionStatusPending, CompletionStatusSucceeded, CompletionStatusFailed>;

struct DirtyRange {
  AllocationId allocation{};
  LongPositive0 offset{};
  IntPositive size{};
};

struct Ping {
};

struct EnableHostMemory {
};

struct PollHostMemory {
  CompletionId completion{};
};

struct SupplyHostMemory {
  CompletionId completion{};
  std::vector<HostMemoryBytes> ranges{};
};

struct RegisterAllocation {
  LongInterval1To2147483647 size{};
};

struct SupplyHostSync {
  CompletionId completion{};
  int64_t ticket{};
  int64_t observed{};
};

struct RegisterAllocationAddress {
  AllocationId allocation{};
  GuestAddress nativeBase{};
  bool hostBacked{};
};

struct ReleaseAllocation {
  AllocationId allocation{};
};

struct WriteAllocation {
  AllocationId allocation{};
  LongPositive0 offset{};
  std::vector<uint8_t> bytes{};
};

struct ReadAllocation {
  AllocationId allocation{};
  LongPositive0 offset{};
  IntPositive0 size{};
};

struct LoadProgram {
  ProgramEncoding encoding{};
  std::vector<uint8_t> image{};
};

struct DescribeSpecialization {
  std::vector<uint8_t> image{};
};

struct SpecializeImage {
  std::vector<uint8_t> image{};
  std::vector<SpecializationConstant> constants{};
};

struct PrepareShader {
  std::vector<uint8_t> image{};
  StringMinimumLength1NotContains00 entry{};
  ShaderStageKind stage{};
  std::vector<SpecializationConstant> constants{};
};

struct SubmitGraphics {
  GraphicsSubmission submission{};
};

struct ReadGraphicsStatistics {
  CompletionId completion{};
};

struct UnloadProgram {
  ProgramId program{};
};

struct DescribeProgram {
  ProgramId program{};
};

struct DescribeKernel {
  ProgramId program{};
  StringMinimumLength1NotContains00 name{};
};

struct DescribeImage {
  ProgramEncoding encoding{};
  std::vector<uint8_t> image{};
  StringMinimumLength1NotContains00 name{};
};

struct DescribeGlobal {
  ProgramId program{};
  StringMinimumLength1NotContains00 name{};
};

struct BindProgramGlobal {
  ProgramId program{};
  StringMinimumLength1NotContains00 name{};
  AllocationId allocation{};
  LongPositive0 offset{};
};

struct WriteProgramGlobal {
  ProgramId program{};
  StringMinimumLength1NotContains00 name{};
  LongPositive0 offset{};
  std::vector<uint8_t> bytes{};
};

struct BuildKernel {
  ProgramId program{};
  StringMinimumLength1NotContains00 kernelName{};
};

struct Launch {
  ProgramId program{};
  StringMinimumLength1NotContains00 kernelName{};
  Dimensions groupSize{};
  Dimensions groupCount{};
  GlobalDimensions globalSize{};
  IntInterval1To3 workDim{};
  Coordinates globalOffset{};
  IntPositive0 privateSegmentBytes{};
  IntPositive0 groupSegmentBytes{};
  std::vector<Argument> arguments{};
  std::vector<PointerRelocation> pointerRelocations{};
  bool discoverArgumentRelocations{};
  std::vector<DescriptorBinding> descriptorBindings{};
  std::optional<ArgumentBuffer> argumentBuffer{};
  std::optional<Dimensions> clusterSize{};
  bool cooperative{};
};

struct QueryCompletion {
  CompletionId completion{};
};

struct CompletionDirtyRanges {
  CompletionId completion{};
  IntPositive0 offset{};
};

struct CompletionPointerRelocations {
  CompletionId completion{};
  IntPositive0 offset{};
};

struct CompletionPrints {
  CompletionId completion{};
};

struct ReleaseCompletion {
  CompletionId completion{};
};

namespace wire {
inline bool valid(const CompletionId &);
inline bool valid(const GuestAddress &);
inline bool valid(const IntPositive &);
inline bool valid(const HostMemoryRange &);
inline bool valid(const IntOneOf0Or1Or2Or4Or8 &);
inline bool valid(const HostMemoryBytes &);
inline bool valid(const IntOneOf1Or2Or4Or8 &);
inline bool valid(const HostMemoryAtomicOperation &);
inline bool valid(const HostMemoryAtomic &);
inline bool valid(const HostMemorySync &);
inline bool valid(const HostMemoryBatch &);
inline bool valid(const LongInterval1To2147483647 &);
inline bool valid(const AllocationId &);
inline bool valid(const LongPositive0 &);
inline bool valid(const IntPositive0 &);
inline bool valid(const ProgramEncoding &);
inline bool valid(const ProgramId &);
inline bool valid(const Binary32 &);
inline bool valid(const SpecializationInfo &);
inline bool valid(const SpecializationConstant &);
inline bool valid(const StringMinimumLength1NotContains00 &);
inline bool valid(const ShaderStageKind &);
inline bool valid(const StringMinimumLength1 &);
inline bool valid(const ShaderResourceKind &);
inline bool valid(const ShaderDescriptor &);
inline bool valid(const ShaderDirection &);
inline bool valid(const IntInterval0To3 &);
inline bool valid(const ShaderQualifiers &);
inline bool valid(const ShaderField &);
inline bool valid(const ShaderInterface &);
inline bool valid(const Dimensions &);
inline bool valid(const PreparedShader &);
inline bool valid(const LongPositive &);
inline bool valid(const GraphicsRange &);
inline bool valid(const RelocationSourceMemory &);
inline bool valid(const RelocationSourceArgument &);
inline bool valid(const RelocationSource &);
inline bool valid(const PointerRelocation &);
inline bool valid(const GraphicsExtent &);
inline bool valid(const GraphicsImageLevel &);
inline bool valid(const NonEmptyGraphicsImageLevels &);
inline bool valid(const IntOneOf1Or4 &);
inline bool valid(const GraphicsFormat &);
inline bool valid(const GraphicsImage &);
inline bool valid(const GraphicsSwizzle &);
inline bool valid(const GraphicsComponents &);
inline bool valid(const GraphicsFilter &);
inline bool valid(const GraphicsAddressMode &);
inline bool valid(const GraphicsCompare &);
inline bool valid(const GraphicsBorderColour &);
inline bool valid(const GraphicsMipmapMode &);
inline bool valid(const GraphicsSampler &);
inline bool valid(const GraphicsDescriptorImage &);
inline bool valid(const GraphicsDescriptorSampler &);
inline bool valid(const GraphicsDescriptorCombined &);
inline bool valid(const GraphicsDescriptor &);
inline bool valid(const DescriptorBinding &);
inline bool valid(const GraphicsShader &);
inline bool valid(const GraphicsVertexBuffer &);
inline bool valid(const GraphicsVertexEncoding &);
inline bool valid(const GraphicsVertexFormat &);
inline bool valid(const GraphicsVertexAttribute &);
inline bool valid(const IntOneOf2Or4 &);
inline bool valid(const GraphicsIndexBuffer &);
inline bool valid(const GraphicsDrawCall &);
inline bool valid(const GraphicsIndirect &);
inline bool valid(const GraphicsTopology &);
inline bool valid(const GraphicsRasterState &);
inline bool valid(const GraphicsViewport &);
inline bool valid(const GraphicsRect &);
inline bool valid(const GraphicsBlendFactor &);
inline bool valid(const GraphicsBlendOp &);
inline bool valid(const GraphicsBlend &);
inline bool valid(const GraphicsColour &);
inline bool valid(const GraphicsStencilOp &);
inline bool valid(const GraphicsStencil &);
inline bool valid(const GraphicsDepthStencil &);
inline bool valid(const GraphicsDraw &);
inline bool valid(const GraphicsLayers &);
inline bool valid(const GraphicsOffset &);
inline bool valid(const GraphicsImageCopy &);
inline bool valid(const GraphicsBufferImageCopy &);
inline bool valid(const GraphicsBlit &);
inline bool valid(const GraphicsResolve &);
inline bool valid(const GraphicsCommandDraw &);
inline bool valid(const GraphicsCommandClearColour &);
inline bool valid(const GraphicsCommandClearDepthStencil &);
inline bool valid(const GraphicsCommandCopyImage &);
inline bool valid(const GraphicsCommandCopyBufferImage &);
inline bool valid(const GraphicsCommandBlit &);
inline bool valid(const GraphicsCommandResolve &);
inline bool valid(const GraphicsCommandResolveAttachment &);
inline bool valid(const GraphicsCommand &);
inline bool valid(const GraphicsSubmission &);
inline bool valid(const GraphicsStatistics &);
inline bool valid(const KernelSymbol &);
inline bool valid(const GlobalSymbol &);
inline bool valid(const ProgramSymbols &);
inline bool valid(const ParameterKind &);
inline bool valid(const Parameter &);
inline bool valid(const KernelMetadata &);
inline bool valid(const ProgramGlobalAlias &);
inline bool valid(const ProgramGlobal &);
inline bool valid(const GlobalDimensions &);
inline bool valid(const IntInterval1To3 &);
inline bool valid(const Coordinates &);
inline bool valid(const NonEmptyBytes &);
inline bool valid(const ArgumentScalar &);
inline bool valid(const ArgumentMemory &);
inline bool valid(const ArgumentLocal &);
inline bool valid(const ArgumentDescriptor &);
inline bool valid(const Argument &);
inline bool valid(const ArgumentBuffer &);
inline bool valid(const ExecutionFailureInvalidRequest &);
inline bool valid(const ExecutionFailureNotFound &);
inline bool valid(const ExecutionFailureUnsupported &);
inline bool valid(const ExecutionFailureFailed &);
inline bool valid(const ExecutionFailureInvalidProgram &);
inline bool valid(const ExecutionFailureInvalidConfiguration &);
inline bool valid(const ExecutionFailureOutOfResources &);
inline bool valid(const ExecutionFailureIllegalAddress &);
inline bool valid(const ExecutionFailureMisalignedAddress &);
inline bool valid(const ExecutionFailureIllegalInstruction &);
inline bool valid(const ExecutionFailureTrapped &);
inline bool valid(const ExecutionFailureAssertionFailed &);
inline bool valid(const ExecutionFailureTimedOut &);
inline bool valid(const ExecutionFailure &);
inline bool valid(const CompletionStatusPending &);
inline bool valid(const CompletionStatusSucceeded &);
inline bool valid(const CompletionStatusFailed &);
inline bool valid(const CompletionStatus &);
inline bool valid(const DirtyRange &);
inline bool valid(const Ping &);
inline bool valid(const EnableHostMemory &);
inline bool valid(const PollHostMemory &);
inline bool valid(const SupplyHostMemory &);
inline bool valid(const RegisterAllocation &);
inline bool valid(const SupplyHostSync &);
inline bool valid(const RegisterAllocationAddress &);
inline bool valid(const ReleaseAllocation &);
inline bool valid(const WriteAllocation &);
inline bool valid(const ReadAllocation &);
inline bool valid(const LoadProgram &);
inline bool valid(const DescribeSpecialization &);
inline bool valid(const SpecializeImage &);
inline bool valid(const PrepareShader &);
inline bool valid(const SubmitGraphics &);
inline bool valid(const ReadGraphicsStatistics &);
inline bool valid(const UnloadProgram &);
inline bool valid(const DescribeProgram &);
inline bool valid(const DescribeKernel &);
inline bool valid(const DescribeImage &);
inline bool valid(const DescribeGlobal &);
inline bool valid(const BindProgramGlobal &);
inline bool valid(const WriteProgramGlobal &);
inline bool valid(const BuildKernel &);
inline bool valid(const Launch &);
inline bool valid(const QueryCompletion &);
inline bool valid(const CompletionDirtyRanges &);
inline bool valid(const CompletionPointerRelocations &);
inline bool valid(const CompletionPrints &);
inline bool valid(const ReleaseCompletion &);
inline bool valid(const Unit &) { return true; }
template <class T> requires std::is_arithmetic_v<T> inline bool valid(T) { return true; }
inline bool valid(const std::string &value) { return value.size() <= MaximumTextBytes; }
// Scala string refinements count UTF-16 code units, not UTF-8 bytes.
inline size_t textLength(std::string_view value) {
  size_t length = 0;
  for (const unsigned char byte : value) {
    if ((byte & 0xc0) != 0x80) length += byte >= 0xf0 ? 2 : 1;
  }
  return length;
}
template <class T> inline bool valid(const std::vector<T> &);
template <class T> inline bool valid(const std::optional<T> &value) { return !value || valid(*value); }
template <class T> inline bool valid(const std::vector<T> &value) {
  if constexpr (std::is_same_v<T, uint8_t>) return value.size() <= MaximumPayload;
  else return value.size() <= MaximumElements && std::all_of(value.begin(), value.end(), [](const auto &item) { return valid(item); });
}
inline bool valid(const CompletionId &value) { return valid(value.value) && value.value > 0; }
inline bool valid(const GuestAddress &value) { return valid(value.value); }
inline bool valid(const IntPositive &value) { return valid(value.value) && value.value > 0; }
inline bool valid(const HostMemoryRange &value) { return valid(value.address) && valid(value.size); }
inline bool valid(const IntOneOf0Or1Or2Or4Or8 &value) { return valid(value.value) && (value.value == 0LL || value.value == 1LL || value.value == 2LL || value.value == 4LL || value.value == 8LL); }
inline bool valid(const HostMemoryBytes &value) { return valid(value.address) && valid(value.bytes) && valid(value.atomicWidth); }
inline bool valid(const IntOneOf1Or2Or4Or8 &value) { return valid(value.value) && (value.value == 1LL || value.value == 2LL || value.value == 4LL || value.value == 8LL); }
inline bool valid(const HostMemoryAtomicOperation &value) { return static_cast<uint8_t>(value) < 3; }
inline bool valid(const HostMemoryAtomic &value) { return valid(value.address) && valid(value.size) && valid(value.expected) && valid(value.desired) && valid(value.operation); }
inline bool valid(const HostMemorySync &value) { return valid(value.ticket) && valid(value.atomic) && valid(value.publish) && valid(value.atomicStores) && valid(value.relaxed); }
inline bool valid(const HostMemoryBatch &value) { return valid(value.reads) && valid(value.writes) && valid(value.completed) && valid(value.sync); }
inline bool valid(const LongInterval1To2147483647 &value) { return valid(value.value) && value.value >= 1LL && value.value <= 2147483647LL; }
inline bool valid(const AllocationId &value) { return valid(value.value) && value.value > 0; }
inline bool valid(const LongPositive0 &value) { return valid(value.value) && value.value >= 0; }
inline bool valid(const IntPositive0 &value) { return valid(value.value) && value.value >= 0; }
inline bool valid(const ProgramEncoding &value) { return static_cast<uint8_t>(value) < 6; }
inline bool valid(const ProgramId &value) { return valid(value.value) && value.value > 0; }
inline bool valid(const Binary32 &value) { return valid(value.value) && value.value >= 0LL && value.value <= 4294967295LL; }
inline bool valid(const SpecializationInfo &value) { return valid(value.specId) && valid(value.byteSize); }
inline bool valid(const SpecializationConstant &value) { return valid(value.specId) && valid(value.value); }
inline bool valid(const StringMinimumLength1NotContains00 &value) { return valid(value.value) && textLength(value.value) >= 1U && value.value.find(std::string_view("\x00", 1)) == std::string::npos; }
inline bool valid(const ShaderStageKind &value) { return static_cast<uint8_t>(value) < 3; }
inline bool valid(const StringMinimumLength1 &value) { return valid(value.value) && textLength(value.value) >= 1U; }
inline bool valid(const ShaderResourceKind &value) { return static_cast<uint8_t>(value) < 4; }
inline bool valid(const ShaderDescriptor &value) { return valid(value.set) && valid(value.binding) && valid(value.element) && valid(value.resource); }
inline bool valid(const ShaderDirection &value) { return static_cast<uint8_t>(value) < 2; }
inline bool valid(const IntInterval0To3 &value) { return valid(value.value) && value.value >= 0LL && value.value <= 3LL; }
inline bool valid(const ShaderQualifiers &value) { return valid(value.flat) && valid(value.noPerspective) && valid(value.centroid) && valid(value.component); }
inline bool valid(const ShaderField &value) { return valid(value.offset) && valid(value.size) && valid(value.location) && valid(value.builtin) && valid(value.qualifiers); }
inline bool valid(const ShaderInterface &value) { return valid(value.id) && valid(value.direction) && valid(value.size) && valid(value.builtin) && valid(value.qualifiers) && valid(value.fields); }
inline bool valid(const Dimensions &value) { return valid(value.x) && valid(value.y) && valid(value.z); }
inline bool valid(const PreparedShader &value) { return valid(value.image) && valid(value.entry) && valid(value.stage) && valid(value.descriptors) && valid(value.pushConstants) && valid(value.interfaces) && valid(value.workgroup) && valid(value.earlyFragmentTests) && valid(value.fragmentQuads); }
inline bool valid(const LongPositive &value) { return valid(value.value) && value.value > 0; }
inline bool valid(const GraphicsRange &value) { return valid(value.allocation) && valid(value.offset) && valid(value.size); }
inline bool valid(const RelocationSourceMemory &value) { return valid(value.region); }
inline bool valid(const RelocationSourceArgument &value) { return valid(value.index); }
inline bool valid(const RelocationSource &value) { return !value.valueless_by_exception() && std::visit([](const auto &item) { return valid(item); }, value); }
inline bool valid(const PointerRelocation &value) { return valid(value.source) && valid(value.sourceOffset) && valid(value.target) && valid(value.targetOffset); }
inline bool valid(const GraphicsExtent &value) { return valid(value.width) && valid(value.height) && valid(value.depth); }
inline bool valid(const GraphicsImageLevel &value) { return valid(value.extent) && valid(value.offset) && valid(value.rowPitch) && valid(value.slicePitch) && valid(value.layerPitch); }
inline bool valid(const NonEmptyGraphicsImageLevels &value) { return valid(value.value) && value.value.size() >= 1U; }
inline bool valid(const IntOneOf1Or4 &value) { return valid(value.value) && (value.value == 1LL || value.value == 4LL); }
inline bool valid(const GraphicsFormat &value) { return static_cast<uint8_t>(value) < 62; }
inline bool valid(const GraphicsImage &value) { return valid(value.memory) && valid(value.levels) && valid(value.layers) && valid(value.samples) && valid(value.format) && valid(value.volume); }
inline bool valid(const GraphicsSwizzle &value) { return static_cast<uint8_t>(value) < 7; }
inline bool valid(const GraphicsComponents &value) { return valid(value.r) && valid(value.g) && valid(value.b) && valid(value.a); }
inline bool valid(const GraphicsFilter &value) { return static_cast<uint8_t>(value) < 2; }
inline bool valid(const GraphicsAddressMode &value) { return static_cast<uint8_t>(value) < 5; }
inline bool valid(const GraphicsCompare &value) { return static_cast<uint8_t>(value) < 8; }
inline bool valid(const GraphicsBorderColour &value) { return static_cast<uint8_t>(value) < 6; }
inline bool valid(const GraphicsMipmapMode &value) { return static_cast<uint8_t>(value) < 2; }
inline bool valid(const GraphicsSampler &value) { return valid(value.minFilter) && valid(value.magFilter) && valid(value.addressU) && valid(value.addressV) && valid(value.addressW) && valid(value.unnormalized) && valid(value.compareEnable) && valid(value.compareOp) && valid(value.borderColour) && valid(value.mipLodBias) && valid(value.minLod) && valid(value.maxLod) && valid(value.mipmapMode); }
inline bool valid(const GraphicsDescriptorImage &value) { return valid(value.source) && valid(value.components); }
inline bool valid(const GraphicsDescriptorSampler &value) { return valid(value.value); }
inline bool valid(const GraphicsDescriptorCombined &value) { return valid(value.source) && valid(value.components) && valid(value.sampler); }
inline bool valid(const GraphicsDescriptor &value) { return !value.valueless_by_exception() && std::visit([](const auto &item) { return valid(item); }, value); }
inline bool valid(const DescriptorBinding &value) { return valid(value.argument) && valid(value.descriptor); }
inline bool valid(const GraphicsShader &value) { return valid(value.image) && valid(value.entry) && valid(value.interfaces) && valid(value.arguments) && valid(value.relocations) && valid(value.pushConstants) && valid(value.earlyFragmentTests) && valid(value.fragmentQuads) && valid(value.descriptorBindings); }
inline bool valid(const GraphicsVertexBuffer &value) { return valid(value.binding) && valid(value.memory) && valid(value.stride) && valid(value.instanceRate); }
inline bool valid(const GraphicsVertexEncoding &value) { return static_cast<uint8_t>(value) < 8; }
inline bool valid(const GraphicsVertexFormat &value) { return valid(value.encoding) && valid(value.widths) && valid(value.bytes) && valid(value.bgra); }
inline bool valid(const GraphicsVertexAttribute &value) { return valid(value.location) && valid(value.binding) && valid(value.offset) && valid(value.format); }
inline bool valid(const IntOneOf2Or4 &value) { return valid(value.value) && (value.value == 2LL || value.value == 4LL); }
inline bool valid(const GraphicsIndexBuffer &value) { return valid(value.memory) && valid(value.width); }
inline bool valid(const GraphicsDrawCall &value) { return valid(value.vertices) && valid(value.instances) && valid(value.firstVertex) && valid(value.firstInstance) && valid(value.vertexOffset); }
inline bool valid(const GraphicsIndirect &value) { return valid(value.memory) && valid(value.count) && valid(value.stride); }
inline bool valid(const GraphicsTopology &value) { return static_cast<uint8_t>(value) < 6; }
inline bool valid(const GraphicsRasterState &value) { return valid(value.topology) && valid(value.restart) && valid(value.discard) && valid(value.cullFront) && valid(value.cullBack) && valid(value.frontCounterClockwise) && valid(value.biasEnabled) && valid(value.biasConstant) && valid(value.biasClamp) && valid(value.biasSlope) && valid(value.samples) && valid(value.sampleMask) && valid(value.sampleShading) && valid(value.alphaToCoverage); }
inline bool valid(const GraphicsViewport &value) { return valid(value.x) && valid(value.y) && valid(value.width) && valid(value.height) && valid(value.minDepth) && valid(value.maxDepth); }
inline bool valid(const GraphicsRect &value) { return valid(value.x) && valid(value.y) && valid(value.width) && valid(value.height); }
inline bool valid(const GraphicsBlendFactor &value) { return static_cast<uint8_t>(value) < 15; }
inline bool valid(const GraphicsBlendOp &value) { return static_cast<uint8_t>(value) < 5; }
inline bool valid(const GraphicsBlend &value) { return valid(value.enabled) && valid(value.sourceColour) && valid(value.destinationColour) && valid(value.colourOp) && valid(value.sourceAlpha) && valid(value.destinationAlpha) && valid(value.alphaOp) && valid(value.writeMask); }
inline bool valid(const GraphicsColour &value) { return valid(value.r) && valid(value.g) && valid(value.b) && valid(value.a); }
inline bool valid(const GraphicsStencilOp &value) { return static_cast<uint8_t>(value) < 8; }
inline bool valid(const GraphicsStencil &value) { return valid(value.fail) && valid(value.pass) && valid(value.depthFail) && valid(value.compare) && valid(value.compareMask) && valid(value.writeMask) && valid(value.reference); }
inline bool valid(const GraphicsDepthStencil &value) { return valid(value.depthTest) && valid(value.depthWrite) && valid(value.compare) && valid(value.boundsTest) && valid(value.minBounds) && valid(value.maxBounds) && valid(value.stencilTest) && valid(value.front) && valid(value.back); }
inline bool valid(const GraphicsDraw &value) { return valid(value.vertex) && valid(value.fragment) && valid(value.vertexBuffers) && valid(value.attributes) && valid(value.index) && valid(value.call) && valid(value.indirect) && valid(value.raster) && valid(value.viewport) && valid(value.scissor) && valid(value.renderArea) && valid(value.width) && valid(value.height) && valid(value.viewIndex) && valid(value.colours) && valid(value.depth) && valid(value.blends) && valid(value.blendConstants) && valid(value.depthStencil); }
inline bool valid(const GraphicsLayers &value) { return valid(value.level) && valid(value.baseLayer) && valid(value.layers) && valid(value.colour) && valid(value.depth) && valid(value.stencil); }
inline bool valid(const GraphicsOffset &value) { return valid(value.x) && valid(value.y) && valid(value.z); }
inline bool valid(const GraphicsImageCopy &value) { return valid(value.source) && valid(value.sourceOffset) && valid(value.destination) && valid(value.destinationOffset) && valid(value.extent); }
inline bool valid(const GraphicsBufferImageCopy &value) { return valid(value.bufferOffset) && valid(value.rowLength) && valid(value.imageHeight) && valid(value.image) && valid(value.offset) && valid(value.extent); }
inline bool valid(const GraphicsBlit &value) { return valid(value.source) && valid(value.sourceBegin) && valid(value.sourceEnd) && valid(value.destination) && valid(value.destinationBegin) && valid(value.destinationEnd); }
inline bool valid(const GraphicsResolve &value) { return static_cast<uint8_t>(value) < 5; }
inline bool valid(const GraphicsCommandDraw &value) { return valid(value.value); }
inline bool valid(const GraphicsCommandClearColour &value) { return valid(value.image) && valid(value.area) && valid(value.bits); }
inline bool valid(const GraphicsCommandClearDepthStencil &value) { return valid(value.image) && valid(value.area) && valid(value.depth) && valid(value.stencil) && valid(value.clearDepth) && valid(value.clearStencil); }
inline bool valid(const GraphicsCommandCopyImage &value) { return valid(value.source) && valid(value.destination) && valid(value.regions); }
inline bool valid(const GraphicsCommandCopyBufferImage &value) { return valid(value.buffer) && valid(value.image) && valid(value.regions) && valid(value.toImage); }
inline bool valid(const GraphicsCommandBlit &value) { return valid(value.source) && valid(value.destination) && valid(value.regions) && valid(value.linear); }
inline bool valid(const GraphicsCommandResolve &value) { return valid(value.source) && valid(value.destination) && valid(value.regions); }
inline bool valid(const GraphicsCommandResolveAttachment &value) { return valid(value.source) && valid(value.destination) && valid(value.area) && valid(value.depth) && valid(value.stencil); }
inline bool valid(const GraphicsCommand &value) { return !value.valueless_by_exception() && std::visit([](const auto &item) { return valid(item); }, value); }
inline bool valid(const GraphicsSubmission &value) { return valid(value.commands); }
inline bool valid(const GraphicsStatistics &value) { return valid(value.occlusionSamples); }
inline bool valid(const KernelSymbol &value) { return valid(value.name) && valid(value.linkageName); }
inline bool valid(const GlobalSymbol &value) { return valid(value.name) && valid(value.virtualAddress) && valid(value.size) && valid(value.pointerOffsets); }
inline bool valid(const ProgramSymbols &value) { return valid(value.kernels) && valid(value.globals); }
inline bool valid(const ParameterKind &value) { return static_cast<uint8_t>(value) < 3; }
inline bool valid(const Parameter &value) { return valid(value.width) && valid(value.offset) && valid(value.kind) && valid(value.containsPointers); }
inline bool valid(const KernelMetadata &value) { return valid(value.argumentBlockSize) && valid(value.explicitParameterCount) && valid(value.parameters) && valid(value.requiredWorkGroupSize) && valid(value.workGroupSizeHint) && valid(value.bindingIndices) && valid(value.entryPointId) && valid(value.registerCount); }
inline bool valid(const ProgramGlobalAlias &value) { return valid(value.name) && valid(value.offset); }
inline bool valid(const ProgramGlobal &value) { return valid(value.size) && valid(value.initial) && valid(value.alias); }
inline bool valid(const GlobalDimensions &value) { return valid(value.x) && valid(value.y) && valid(value.z); }
inline bool valid(const IntInterval1To3 &value) { return valid(value.value) && value.value >= 1LL && value.value <= 3LL; }
inline bool valid(const Coordinates &value) { return valid(value.x) && valid(value.y) && valid(value.z); }
inline bool valid(const NonEmptyBytes &value) { return valid(value.value) && value.value.size() >= 1U; }
inline bool valid(const ArgumentScalar &value) { return valid(value.bytes); }
inline bool valid(const ArgumentMemory &value) { return valid(value.region) && valid(value.offset) && valid(value.range); }
inline bool valid(const ArgumentLocal &value) { return valid(value.size); }
inline bool valid(const ArgumentDescriptor &) { return true; }
inline bool valid(const Argument &value) { return !value.valueless_by_exception() && std::visit([](const auto &item) { return valid(item); }, value); }
inline bool valid(const ArgumentBuffer &value) { return valid(value.region) && valid(value.offset); }
inline bool valid(const ExecutionFailureInvalidRequest &value) { return valid(value.detail); }
inline bool valid(const ExecutionFailureNotFound &value) { return valid(value.detail); }
inline bool valid(const ExecutionFailureUnsupported &value) { return valid(value.detail); }
inline bool valid(const ExecutionFailureFailed &value) { return valid(value.detail); }
inline bool valid(const ExecutionFailureInvalidProgram &value) { return valid(value.detail); }
inline bool valid(const ExecutionFailureInvalidConfiguration &value) { return valid(value.detail); }
inline bool valid(const ExecutionFailureOutOfResources &value) { return valid(value.detail); }
inline bool valid(const ExecutionFailureIllegalAddress &value) { return valid(value.detail); }
inline bool valid(const ExecutionFailureMisalignedAddress &value) { return valid(value.detail); }
inline bool valid(const ExecutionFailureIllegalInstruction &value) { return valid(value.detail); }
inline bool valid(const ExecutionFailureTrapped &value) { return valid(value.detail); }
inline bool valid(const ExecutionFailureAssertionFailed &value) { return valid(value.detail); }
inline bool valid(const ExecutionFailureTimedOut &value) { return valid(value.detail); }
inline bool valid(const ExecutionFailure &value) { return !value.valueless_by_exception() && std::visit([](const auto &item) { return valid(item); }, value); }
inline bool valid(const CompletionStatusPending &) { return true; }
inline bool valid(const CompletionStatusSucceeded &) { return true; }
inline bool valid(const CompletionStatusFailed &value) { return valid(value.error); }
inline bool valid(const CompletionStatus &value) { return !value.valueless_by_exception() && std::visit([](const auto &item) { return valid(item); }, value); }
inline bool valid(const DirtyRange &value) { return valid(value.allocation) && valid(value.offset) && valid(value.size); }
inline bool valid(const Ping &) { return true; }
inline bool valid(const EnableHostMemory &) { return true; }
inline bool valid(const PollHostMemory &value) { return valid(value.completion); }
inline bool valid(const SupplyHostMemory &value) { return valid(value.completion) && valid(value.ranges); }
inline bool valid(const RegisterAllocation &value) { return valid(value.size); }
inline bool valid(const SupplyHostSync &value) { return valid(value.completion) && valid(value.ticket) && valid(value.observed); }
inline bool valid(const RegisterAllocationAddress &value) { return valid(value.allocation) && valid(value.nativeBase) && valid(value.hostBacked); }
inline bool valid(const ReleaseAllocation &value) { return valid(value.allocation); }
inline bool valid(const WriteAllocation &value) { return valid(value.allocation) && valid(value.offset) && valid(value.bytes); }
inline bool valid(const ReadAllocation &value) { return valid(value.allocation) && valid(value.offset) && valid(value.size); }
inline bool valid(const LoadProgram &value) { return valid(value.encoding) && valid(value.image); }
inline bool valid(const DescribeSpecialization &value) { return valid(value.image); }
inline bool valid(const SpecializeImage &value) { return valid(value.image) && valid(value.constants); }
inline bool valid(const PrepareShader &value) { return valid(value.image) && valid(value.entry) && valid(value.stage) && valid(value.constants); }
inline bool valid(const SubmitGraphics &value) { return valid(value.submission); }
inline bool valid(const ReadGraphicsStatistics &value) { return valid(value.completion); }
inline bool valid(const UnloadProgram &value) { return valid(value.program); }
inline bool valid(const DescribeProgram &value) { return valid(value.program); }
inline bool valid(const DescribeKernel &value) { return valid(value.program) && valid(value.name); }
inline bool valid(const DescribeImage &value) { return valid(value.encoding) && valid(value.image) && valid(value.name); }
inline bool valid(const DescribeGlobal &value) { return valid(value.program) && valid(value.name); }
inline bool valid(const BindProgramGlobal &value) { return valid(value.program) && valid(value.name) && valid(value.allocation) && valid(value.offset); }
inline bool valid(const WriteProgramGlobal &value) { return valid(value.program) && valid(value.name) && valid(value.offset) && valid(value.bytes); }
inline bool valid(const BuildKernel &value) { return valid(value.program) && valid(value.kernelName); }
inline bool valid(const Launch &value) { return valid(value.program) && valid(value.kernelName) && valid(value.groupSize) && valid(value.groupCount) && valid(value.globalSize) && valid(value.workDim) && valid(value.globalOffset) && valid(value.privateSegmentBytes) && valid(value.groupSegmentBytes) && valid(value.arguments) && valid(value.pointerRelocations) && valid(value.discoverArgumentRelocations) && valid(value.descriptorBindings) && valid(value.argumentBuffer) && valid(value.clusterSize) && valid(value.cooperative); }
inline bool valid(const QueryCompletion &value) { return valid(value.completion); }
inline bool valid(const CompletionDirtyRanges &value) { return valid(value.completion) && valid(value.offset); }
inline bool valid(const CompletionPointerRelocations &value) { return valid(value.completion) && valid(value.offset); }
inline bool valid(const CompletionPrints &value) { return valid(value.completion); }
inline bool valid(const ReleaseCompletion &value) { return valid(value.completion); }
} // namespace wire


enum class FailureKind : uint8_t {
  Transport = 0,
  InvalidRequest = 1,
  NotFound = 2,
  Unsupported = 3,
  Failed = 4,
  InvalidProgram = 5,
  InvalidConfiguration = 6,
  OutOfResources = 7,
  IllegalAddress = 8,
  MisalignedAddress = 9,
  IllegalInstruction = 10,
  Trapped = 11,
  AssertionFailed = 12,
  TimedOut = 13
};

struct Error {
  std::string detail;
  FailureKind kind = FailureKind::Transport;
};

template <class T> class Result {
 public:
  static Result success(T value) { return Result(std::move(value)); }
  static Result failure(Error error) { return Result(std::move(error)); }
  explicit operator bool() const { return std::holds_alternative<T>(outcome); }
  T &value() { return std::get<T>(outcome); }
  const T &value() const { return std::get<T>(outcome); }
  const Error &error() const { return std::get<Error>(outcome); }

 private:
  explicit Result(T value) : outcome(std::move(value)) {}
  explicit Result(Error error) : outcome(std::move(error)) {}
  std::variant<T, Error> outcome;
};

class Client {
 public:
  static std::unique_ptr<Client> fromEnvironment();
  ~Client();
  Client(const Client &) = delete;
  Client &operator=(const Client &) = delete;
  std::string error() const;
  Result<Unit> call(const Ping &request);
  Result<Unit> call(const EnableHostMemory &request);
  Result<HostMemoryBatch> call(const PollHostMemory &request);
  Result<Unit> call(const SupplyHostMemory &request);
  Result<AllocationId> call(const RegisterAllocation &request);
  Result<Unit> call(const SupplyHostSync &request);
  Result<Unit> call(const RegisterAllocationAddress &request);
  Result<Unit> call(const ReleaseAllocation &request);
  Result<Unit> call(const WriteAllocation &request);
  Result<std::vector<uint8_t>> call(const ReadAllocation &request);
  Result<ProgramId> call(const LoadProgram &request);
  Result<std::vector<SpecializationInfo>> call(const DescribeSpecialization &request);
  Result<std::vector<uint8_t>> call(const SpecializeImage &request);
  Result<PreparedShader> call(const PrepareShader &request);
  Result<CompletionId> call(const SubmitGraphics &request);
  Result<GraphicsStatistics> call(const ReadGraphicsStatistics &request);
  Result<Unit> call(const UnloadProgram &request);
  Result<ProgramSymbols> call(const DescribeProgram &request);
  Result<KernelMetadata> call(const DescribeKernel &request);
  Result<KernelMetadata> call(const DescribeImage &request);
  Result<ProgramGlobal> call(const DescribeGlobal &request);
  Result<Unit> call(const BindProgramGlobal &request);
  Result<Unit> call(const WriteProgramGlobal &request);
  Result<Unit> call(const BuildKernel &request);
  Result<CompletionId> call(const Launch &request);
  Result<CompletionStatus> call(const QueryCompletion &request);
  Result<std::vector<DirtyRange>> call(const CompletionDirtyRanges &request);
  Result<std::vector<PointerRelocation>> call(const CompletionPointerRelocations &request);
  Result<std::string> call(const CompletionPrints &request);
  Result<Unit> call(const ReleaseCompletion &request);

 private:
  class Impl;
  explicit Client(std::unique_ptr<Impl> implementation);
  std::unique_ptr<Impl> implementation;
};

} // namespace gemstone::execution

template <> struct glz::meta<gemstone::execution::Unit> { static constexpr auto value = glz::object(); };
template <> struct glz::meta<gemstone::execution::CompletionId> { static constexpr auto value = &gemstone::execution::CompletionId::value; };
template <> struct glz::meta<gemstone::execution::GuestAddress> { static constexpr auto value = &gemstone::execution::GuestAddress::value; };
template <> struct glz::meta<gemstone::execution::IntPositive> { static constexpr auto value = &gemstone::execution::IntPositive::value; };
template <> struct glz::meta<gemstone::execution::IntOneOf0Or1Or2Or4Or8> { static constexpr auto value = &gemstone::execution::IntOneOf0Or1Or2Or4Or8::value; };
template <> struct glz::meta<gemstone::execution::IntOneOf1Or2Or4Or8> { static constexpr auto value = &gemstone::execution::IntOneOf1Or2Or4Or8::value; };
template <> struct glz::meta<gemstone::execution::LongInterval1To2147483647> { static constexpr auto value = &gemstone::execution::LongInterval1To2147483647::value; };
template <> struct glz::meta<gemstone::execution::AllocationId> { static constexpr auto value = &gemstone::execution::AllocationId::value; };
template <> struct glz::meta<gemstone::execution::LongPositive0> { static constexpr auto value = &gemstone::execution::LongPositive0::value; };
template <> struct glz::meta<gemstone::execution::IntPositive0> { static constexpr auto value = &gemstone::execution::IntPositive0::value; };
template <> struct glz::meta<gemstone::execution::ProgramId> { static constexpr auto value = &gemstone::execution::ProgramId::value; };
template <> struct glz::meta<gemstone::execution::Binary32> { static constexpr auto value = &gemstone::execution::Binary32::value; };
template <> struct glz::meta<gemstone::execution::StringMinimumLength1NotContains00> { static constexpr auto value = &gemstone::execution::StringMinimumLength1NotContains00::value; };
template <> struct glz::meta<gemstone::execution::StringMinimumLength1> { static constexpr auto value = &gemstone::execution::StringMinimumLength1::value; };
template <> struct glz::meta<gemstone::execution::IntInterval0To3> { static constexpr auto value = &gemstone::execution::IntInterval0To3::value; };
template <> struct glz::meta<gemstone::execution::LongPositive> { static constexpr auto value = &gemstone::execution::LongPositive::value; };
template <> struct glz::meta<gemstone::execution::RelocationSource> {
  static constexpr std::string_view tag = "t";
  static constexpr std::string_view content = "v";
  static constexpr auto ids = std::array<std::string_view, 2>{"Memory", "Argument"};
};
template <> struct glz::meta<gemstone::execution::NonEmptyGraphicsImageLevels> { static constexpr auto value = &gemstone::execution::NonEmptyGraphicsImageLevels::value; };
template <> struct glz::from<glz::MSGPACK, gemstone::execution::NonEmptyGraphicsImageLevels> {
  template <auto Opts, class Value, glz::is_context Ctx, class It, class End>
  static void op(Value&& value, uint8_t tag, Ctx&& ctx, It& it, const End& end) noexcept {
    using Sequence = std::remove_cvref_t<decltype(value.value)>;
    glz::from<glz::MSGPACK, Sequence>::template op<glz::no_header_off<Opts>()>(value.value, tag, ctx, it, end);
  }
};
template <> struct glz::meta<gemstone::execution::IntOneOf1Or4> { static constexpr auto value = &gemstone::execution::IntOneOf1Or4::value; };
template <> struct glz::meta<gemstone::execution::GraphicsDescriptor> {
  static constexpr std::string_view tag = "t";
  static constexpr std::string_view content = "v";
  static constexpr auto ids = std::array<std::string_view, 3>{"Image", "Sampler", "Combined"};
};
template <> struct glz::meta<gemstone::execution::IntOneOf2Or4> { static constexpr auto value = &gemstone::execution::IntOneOf2Or4::value; };
template <> struct glz::meta<gemstone::execution::GraphicsCommand> {
  static constexpr std::string_view tag = "t";
  static constexpr std::string_view content = "v";
  static constexpr auto ids = std::array<std::string_view, 8>{"Draw", "ClearColour", "ClearDepthStencil", "CopyImage", "CopyBufferImage", "Blit", "Resolve", "ResolveAttachment"};
};
template <> struct glz::meta<gemstone::execution::IntInterval1To3> { static constexpr auto value = &gemstone::execution::IntInterval1To3::value; };
template <> struct glz::meta<gemstone::execution::NonEmptyBytes> { static constexpr auto value = &gemstone::execution::NonEmptyBytes::value; };
template <> struct glz::from<glz::MSGPACK, gemstone::execution::NonEmptyBytes> {
  template <auto Opts, class Value, glz::is_context Ctx, class It, class End>
  static void op(Value&& value, uint8_t tag, Ctx&& ctx, It& it, const End& end) noexcept {
    using Sequence = std::remove_cvref_t<decltype(value.value)>;
    glz::from<glz::MSGPACK, Sequence>::template op<glz::no_header_off<Opts>()>(value.value, tag, ctx, it, end);
  }
};
template <> struct glz::meta<gemstone::execution::ArgumentDescriptor> { static constexpr auto value = glz::object(); };
template <> struct glz::meta<gemstone::execution::Argument> {
  static constexpr std::string_view tag = "t";
  static constexpr std::string_view content = "v";
  static constexpr auto ids = std::array<std::string_view, 4>{"Scalar", "Memory", "Local", "Descriptor"};
};
template <> struct glz::meta<gemstone::execution::ExecutionFailure> {
  static constexpr std::string_view tag = "t";
  static constexpr std::string_view content = "v";
  static constexpr auto ids = std::array<std::string_view, 13>{"InvalidRequest", "NotFound", "Unsupported", "Failed", "InvalidProgram", "InvalidConfiguration", "OutOfResources", "IllegalAddress", "MisalignedAddress", "IllegalInstruction", "Trapped", "AssertionFailed", "TimedOut"};
};
template <> struct glz::meta<gemstone::execution::CompletionStatusPending> { static constexpr auto value = glz::object(); };
template <> struct glz::meta<gemstone::execution::CompletionStatusSucceeded> { static constexpr auto value = glz::object(); };
template <> struct glz::meta<gemstone::execution::CompletionStatus> {
  static constexpr std::string_view tag = "t";
  static constexpr std::string_view content = "v";
  static constexpr auto ids = std::array<std::string_view, 3>{"Pending", "Succeeded", "Failed"};
};
template <> struct glz::meta<gemstone::execution::Ping> { static constexpr auto value = glz::object(); };
template <> struct glz::meta<gemstone::execution::EnableHostMemory> { static constexpr auto value = glz::object(); };

#endif
