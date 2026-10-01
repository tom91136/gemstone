#include <array>
#include <cmath>
#include <cstdlib>
#include <memory>
#include <vector>

#include <ggml.h>

#include <catch2/catch_test_macros.hpp>

#include <conformance/device.hpp>

#include <ggml-alloc.h>
#include <ggml-backend.h>
#include <ggml-vulkan.h>

namespace {
struct Graph {
  std::unique_ptr<ggml_backend, decltype(&ggml_backend_free)> backend{nullptr, ggml_backend_free};
  std::unique_ptr<ggml_context, decltype(&ggml_free)> context{nullptr, ggml_free};
  std::unique_ptr<ggml_backend_buffer, decltype(&ggml_backend_buffer_free)> buffer{nullptr, ggml_backend_buffer_free};

  Graph() {
    const auto &selection = conformance::device_selection();
    const auto index = std::stoul(selection.environment.at("CONFORMANCE_VULKAN_DEVICE_ID")) - 1;
    // Restrict GGML to the selected device before its independent enumeration.
    REQUIRE(setenv("GGML_VK_VISIBLE_DEVICES", std::to_string(index).c_str(), 1) == 0);
    backend.reset(ggml_backend_vk_init(0));
    REQUIRE(backend);
    REQUIRE(ggml_backend_is_vk(backend.get()));
    context.reset(ggml_init({ggml_graph_overhead_custom(32, false) + 32 * ggml_tensor_overhead(), nullptr, true}));
    REQUIRE(context);
  }

  void allocate() {
    buffer.reset(ggml_backend_alloc_ctx_tensors(context.get(), backend.get()));
    REQUIRE(buffer);
  }

  std::vector<float> evaluate(ggml_tensor *result) {
    REQUIRE(ggml_backend_supports_op(backend.get(), result));
    auto *graph = ggml_new_graph_custom(context.get(), 32, false);
    ggml_build_forward_expand(graph, result);
    REQUIRE(ggml_backend_graph_compute(backend.get(), graph) == GGML_STATUS_SUCCESS);
    ggml_backend_synchronize(backend.get());
    std::vector<float> output(ggml_nelements(result));
    ggml_backend_tensor_get(result, output.data(), 0, output.size() * sizeof(float));
    return output;
  }
};
} // namespace

TEST_CASE("miniapps/ggml/vulkan/add", "[gpu-conformance][miniapps][vulkan][requires-execution]") {
  Graph graph;
  auto *a = ggml_new_tensor_1d(graph.context.get(), GGML_TYPE_F32, 64);
  auto *b = ggml_new_tensor_1d(graph.context.get(), GGML_TYPE_F32, 64);
  auto *sum = ggml_add(graph.context.get(), a, b);
  graph.allocate();
  std::array<float, 64> left{}, right{};
  for (size_t i = 0; i < left.size(); ++i) {
    left[i] = (static_cast<int>(i) - 32) * 0.25f;
    right[i] = static_cast<float>(i % 7) * 0.5f;
  }
  ggml_backend_tensor_set(a, left.data(), 0, sizeof(left));
  ggml_backend_tensor_set(b, right.data(), 0, sizeof(right));
  const auto output = graph.evaluate(sum);
  for (size_t i = 0; i < output.size(); ++i)
    REQUIRE(output[i] == left[i] + right[i]);
}

TEST_CASE("miniapps/ggml/vulkan/half_copy", "[gpu-conformance][miniapps][vulkan][requires-execution]") {
  Graph graph;
  auto *source = ggml_new_tensor_1d(graph.context.get(), GGML_TYPE_F16, 64);
  auto *result = ggml_cast(graph.context.get(), source, GGML_TYPE_F32);
  graph.allocate();
  std::array<ggml_fp16_t, 64> input{};
  for (size_t i = 0; i < input.size(); ++i)
    input[i] = ggml_fp32_to_fp16((static_cast<int>(i) - 32) * 0.125f);
  ggml_backend_tensor_set(source, input.data(), 0, sizeof(input));
  const auto output = graph.evaluate(result);
  for (size_t i = 0; i < output.size(); ++i)
    REQUIRE(output[i] == ggml_fp16_to_fp32(input[i]));
}

TEST_CASE("miniapps/ggml/vulkan/matmul", "[gpu-conformance][miniapps][vulkan][requires-execution]") {
  Graph graph;
  constexpr int k = 32, m = 4, n = 3;
  auto *a = ggml_new_tensor_2d(graph.context.get(), GGML_TYPE_F16, k, m);
  auto *b = ggml_new_tensor_2d(graph.context.get(), GGML_TYPE_F32, k, n);
  auto *product = ggml_mul_mat(graph.context.get(), a, b);
  graph.allocate();
  std::array<ggml_fp16_t, k * m> left{};
  std::array<float, k * n> right{};
  for (int i = 0; i < k * m; ++i)
    left[i] = ggml_fp32_to_fp16((i % 9 - 4) * 0.125f);
  for (int i = 0; i < k * n; ++i)
    right[i] = (i % 7 - 3) * 0.25f;
  ggml_backend_tensor_set(a, left.data(), 0, sizeof(left));
  ggml_backend_tensor_set(b, right.data(), 0, sizeof(right));
  const auto output = graph.evaluate(product);
  for (int j = 0; j < n; ++j)
    for (int i = 0; i < m; ++i) {
      float expected = 0;
      for (int x = 0; x < k; ++x)
        expected += ggml_fp16_to_fp32(left[i * k + x]) * right[j * k + x];
      REQUIRE(std::abs(output[j * m + i] - expected) <= 1e-4f);
    }
}

TEST_CASE("miniapps/ggml/vulkan/softmax", "[gpu-conformance][miniapps][vulkan][requires-execution]") {
  Graph graph;
  constexpr int columns = 32, rows = 2;
  auto *source = ggml_new_tensor_2d(graph.context.get(), GGML_TYPE_F32, columns, rows);
  auto *result = ggml_soft_max(graph.context.get(), source);
  graph.allocate();
  std::array<float, columns * rows> input{};
  for (int i = 0; i < columns * rows; ++i)
    input[i] = (i % 11 - 5) * 0.25f;
  ggml_backend_tensor_set(source, input.data(), 0, sizeof(input));
  const auto output = graph.evaluate(result);
  for (int row = 0; row < rows; ++row) {
    double denominator = 0;
    for (int column = 0; column < columns; ++column)
      denominator += std::exp(input[row * columns + column]);
    double total = 0;
    for (int column = 0; column < columns; ++column) {
      const auto value = output[row * columns + column];
      REQUIRE(std::abs(value - std::exp(input[row * columns + column]) / denominator) <= 1e-5);
      total += value;
    }
    REQUIRE(std::abs(total - 1.0) <= 1e-5);
  }
}
