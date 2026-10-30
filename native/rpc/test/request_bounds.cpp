#include <cstdint>
#include <string>
#include <utility>
#include <vector>

#include <catch2/catch_test_macros.hpp>

#include "gemstone_execution_rpc.hpp"
#include <gemstone/test_support.hpp>

#include "rpc_fixture.hpp"
#include <environment_keys.hpp>

using namespace gemstone::execution;

template <class T> static void checkEncodingLimit(const Result<T> &result) {
  REQUIRE((!result));
  REQUIRE((result.error().detail.contains("encoding limits")));
}

TEST_CASE("execution RPC enforces wire encoding bounds", "[rpc]") {
  const gemstone_test::Environment identity(gemstone::environment::RpcClientId, "42");
  const gemstone_test::Environment endpoint(gemstone::environment::RpcEndpoint, nullptr);
  auto client = Client::fromEnvironment();
  REQUIRE((client));

  std::vector<uint8_t> boundary(MaximumPayload);
  checkEncodingLimit(client->call(WriteAllocation{.allocation = {1}, .offset = {0}, .bytes = std::move(boundary)}));
}

TEST_CASE("execution RPC round trips descriptor alternatives with mip tables and typed samplers", "[rpc]") {
  GraphicsDescriptorCombined combined{};
  combined.source.memory = {.allocation = {7}, .offset = {16}, .size = {128}};
  combined.source.levels.value = {
      {.extent = {.width = {4}, .height = {4}, .depth = {1}}, .offset = {8}, .rowPitch = {8}, .slicePitch = {8}, .layerPitch = {16}},
      {.extent = {.width = {2}, .height = {2}, .depth = {1}}, .offset = {48}, .rowPitch = {8}, .slicePitch = {8}, .layerPitch = {8}}};
  combined.source.layers.value = 2;
  combined.source.samples.value = 1;
  combined.source.format = GraphicsFormat::Etc2R8g8b8UnormBlock;
  combined.components.r = GraphicsSwizzle::B;
  combined.sampler.minFilter = GraphicsFilter::Linear;
  combined.sampler.mipLodBias = -0.75F;
  combined.sampler.minLod = 1.25F;
  combined.sampler.maxLod = 4.5F;
  combined.sampler.compareEnable = true;
  Launch launch{};
  launch.descriptorBindings.push_back({.argument = {2}, .descriptor = combined});
  launch.descriptorBindings.push_back({.argument = {3}, .descriptor = GraphicsDescriptorSampler{combined.sampler}});
  launch.descriptorBindings.push_back(
      {.argument = {4}, .descriptor = GraphicsDescriptorImage{.source = combined.source, .components = combined.components}});
  std::vector<uint8_t> bytes;
  gemstone_test::encodePayload(bytes, launch);
  const auto decoded = gemstone_test::decodePayload<Launch>(bytes);
  REQUIRE(decoded.descriptorBindings.size() == 3);
  REQUIRE(decoded.descriptorBindings.front().argument.value == 2);
  const auto &image = std::get<GraphicsDescriptorCombined>(decoded.descriptorBindings.front().descriptor);
  REQUIRE(image.source.memory.allocation.value == 7);
  REQUIRE(image.source.memory.offset.value == 16);
  REQUIRE(image.source.levels.value.size() == 2);
  REQUIRE(image.source.levels.value[1].offset.value == 48);
  REQUIRE(image.source.levels.value[0].layerPitch.value == 16);
  REQUIRE(image.source.format == GraphicsFormat::Etc2R8g8b8UnormBlock);
  REQUIRE(image.components.r == GraphicsSwizzle::B);
  REQUIRE(image.sampler.minFilter == GraphicsFilter::Linear);
  REQUIRE(image.sampler.compareEnable);
  REQUIRE(image.sampler.mipLodBias == -0.75F);
  REQUIRE(image.sampler.minLod == 1.25F);
  REQUIRE(image.sampler.maxLod == 4.5F);
  REQUIRE(std::get<GraphicsDescriptorSampler>(decoded.descriptorBindings[1].descriptor).value.mipLodBias == -0.75F);
  REQUIRE(std::get<GraphicsDescriptorImage>(decoded.descriptorBindings[2].descriptor).source.memory.offset.value == 16);
  REQUIRE(wire::valid(decoded.descriptorBindings));
  auto invalid = decoded.descriptorBindings;
  std::get<GraphicsDescriptorCombined>(invalid[0].descriptor).source.samples.value = 2;
  REQUIRE_FALSE(wire::valid(invalid));
  invalid = decoded.descriptorBindings;
  std::get<GraphicsDescriptorCombined>(invalid[0].descriptor).source.format = static_cast<GraphicsFormat>(255);
  REQUIRE_FALSE(wire::valid(invalid));
}

TEST_CASE("native wire constraints reject invalid requests before opening a connection", "[rpc]") {
  const gemstone_test::Environment identity(gemstone::environment::RpcClientId, "42");
  const gemstone_test::Environment endpoint(gemstone::environment::RpcEndpoint, nullptr);
  auto client = Client::fromEnvironment();
  REQUIRE(client);
  const auto result = client->call(RegisterAllocation{{-1}});
  REQUIRE_FALSE(result);
  REQUIRE(result.error().detail.contains("wire constraints"));
  REQUIRE(wire::valid(AllocationId{1}));
  REQUIRE_FALSE(wire::valid(AllocationId{0}));
  REQUIRE_FALSE(wire::valid(Binary32{-1}));
  REQUIRE(wire::valid(Binary32{0xffffffffLL}));
  REQUIRE_FALSE(wire::valid(Binary32{0x100000000LL}));
  REQUIRE_FALSE(wire::valid(static_cast<GraphicsFilter>(2)));
  REQUIRE_FALSE(wire::valid(static_cast<GraphicsSwizzle>(7)));
  PreparedShader shader{};
  shader.entry.value = "main";
  shader.interfaces.push_back({});
  REQUIRE_FALSE(wire::valid(shader));
  shader.interfaces.clear();
  REQUIRE(wire::valid(shader));
  REQUIRE_FALSE(wire::valid(DescribeKernel{ProgramId{91}, {std::string("a\0b", 3)}}));
  shader.entry.value.clear();
  REQUIRE_FALSE(wire::valid(shader));
}

TEST_CASE("native wire validation rejects malformed replies and poisons the session", "[rpc]") {
  gemstone_test::RpcFixture fixture;
  fixture.programCatalogue({{.name = "", .linkageName = "add.kd"}}, {});
  const gemstone_test::RpcEnvironment environment(fixture);
  auto client = Client::fromEnvironment();
  REQUIRE(client);
  const auto program = client->call(LoadProgram{.encoding = ProgramEncoding::SPIRVOpenCLCompute, .image = {1}});
  REQUIRE(program);
  const auto reply = client->call(DescribeProgram{program.value()});
  REQUIRE_FALSE(reply);
  REQUIRE(reply.error().detail.contains("payload is invalid"));
  const auto next = client->call(Ping{});
  REQUIRE_FALSE(next);
  REQUIRE(next.error().detail.contains("session was lost"));
}
