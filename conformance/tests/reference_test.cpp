#include <complex>
#include <cstddef>
#include <limits>
#include <random>
#include <vector>

#include <catch2/catch_test_macros.hpp>

#include "../vendor/hip/conformance_reference.hpp"

namespace {

using conformance::reference::denseStrides;
using conformance::reference::dft;
using conformance::reference::Extent;
using conformance::reference::matchesDft;
using conformance::reference::offset;
using conformance::reference::sameMean;
using conformance::reference::unravel;
using conformance::reference::volume;

using Complex = std::complex<double>;

struct Vec2 {
  double x;
  double y;
};

std::vector<Complex> randomValues(size_t count, unsigned seed) {
  std::mt19937 generator(seed);
  std::uniform_real_distribution<double> distribution(-1.0, 1.0);
  std::vector<Complex> values(count);
  for (auto &value : values) {
    value = {distribution(generator), distribution(generator)};
  }
  return values;
}

template <typename Input> std::vector<Complex> naiveOutput(const Extent &extent, double sign, const Input &input) {
  const auto strides = denseStrides(extent);
  std::vector<Complex> output(volume(extent));
  for (size_t k = 0; k < volume(extent); ++k) {
    const auto frequency = unravel(k, extent);
    output[offset(frequency, strides)] = dft(extent, frequency, sign, input);
  }
  return output;
}

} // namespace

TEST_CASE("reference DFT accepts a correct transform", "[unit][reference]") {
  for (const Extent &extent : {Extent{1}, Extent{2}, Extent{8}, Extent{12}, Extent{31}, Extent{4, 3}, Extent{2, 3, 5}}) {
    const auto strides = denseStrides(extent);
    const auto values = randomValues(volume(extent), 7);
    const auto input = [&](const Extent &index) { return values[offset(index, strides)]; };
    for (const double sign : {-1.0, 1.0}) {
      const auto expected = naiveOutput(extent, sign, input);
      CHECK(matchesDft(extent, extent, strides, expected.data(), sign, input));

      const std::vector<std::complex<float>> narrowed(expected.begin(), expected.end());
      CHECK(matchesDft(extent, extent, strides, narrowed.data(), sign, input));

      std::vector<double> real(expected.size());
      for (size_t i = 0; i < real.size(); ++i) {
        real[i] = expected[i].real();
      }
      CHECK(matchesDft(extent, extent, strides, real.data(), sign, input));
    }
  }
}

TEST_CASE("reference DFT accepts a half spectrum with a strided output", "[unit][reference]") {
  const Extent extent{8, 6};
  const Extent outputExtent{5, 6};
  const Extent outputStride{1, 8};
  const auto strides = denseStrides(extent);
  const auto values = randomValues(volume(extent), 11);
  const auto input = [&](const Extent &index) { return values[offset(index, strides)]; };
  const auto expected = naiveOutput(extent, -1.0, input);
  CHECK(matchesDft(extent, outputExtent, outputStride, expected.data(), -1.0, input));

  auto corrupted = expected;
  corrupted[offset({4, 5}, outputStride)] += 1.0;
  CHECK_FALSE(matchesDft(extent, outputExtent, outputStride, corrupted.data(), -1.0, input));
}

TEST_CASE("reference DFT rejects a deliberately wrong output", "[unit][reference]") {
  for (const Extent &extent : {Extent{1}, Extent{12}, Extent{31}, Extent{4, 3}, Extent{2, 3, 5}}) {
    const auto strides = denseStrides(extent);
    const auto values = randomValues(volume(extent), 13);
    const auto input = [&](const Extent &index) { return values[offset(index, strides)]; };
    const auto expected = naiveOutput(extent, -1.0, input);

    auto corrupted = expected;
    corrupted.front() += 1.0;
    CHECK_FALSE(matchesDft(extent, extent, strides, corrupted.data(), -1.0, input));

    auto scaled = expected;
    for (auto &value : scaled) {
      value *= 1.5;
    }
    CHECK_FALSE(matchesDft(extent, extent, strides, scaled.data(), -1.0, input));

    auto undefined = expected;
    undefined.back() = {std::numeric_limits<double>::quiet_NaN(), 0.0};
    CHECK_FALSE(matchesDft(extent, extent, strides, undefined.data(), -1.0, input));

    const std::vector<std::complex<float>> narrowed(corrupted.begin(), corrupted.end());
    CHECK_FALSE(matchesDft(extent, extent, strides, narrowed.data(), -1.0, input));
  }
}

TEST_CASE("reference DFT rejects the wrong transform direction", "[unit][reference]") {
  const Extent extent{12, 5};
  const auto strides = denseStrides(extent);
  const auto values = randomValues(volume(extent), 19);
  const auto input = [&](const Extent &index) { return values[offset(index, strides)]; };
  const auto expected = naiveOutput(extent, -1.0, input);
  CHECK_FALSE(matchesDft(extent, extent, strides, expected.data(), 1.0, input));
}

TEST_CASE("reference DFT matches a callback-filtered input with a struct output", "[unit][reference]") {
  const Extent extent{16};
  const auto strides = denseStrides(extent);
  const auto values = randomValues(volume(extent), 17);
  const auto filtered = [&](const Extent &index) {
    const auto &value = values[offset(index, strides)];
    return Vec2{.x = value.real() * 0.5, .y = value.imag() * 0.5};
  };
  const auto expected = naiveOutput(extent, -1.0, filtered);
  std::vector<Vec2> output;
  output.reserve(expected.size());
  for (const auto &value : expected) {
    output.push_back({.x = value.real(), .y = value.imag()});
  }
  CHECK(matchesDft(extent, extent, strides, output.data(), -1.0, filtered));

  output.front().x += 1.0;
  CHECK_FALSE(matchesDft(extent, extent, strides, output.data(), -1.0, filtered));
}

TEST_CASE("reference mean test accepts equal and rejects shifted distributions", "[unit][reference]") {
  std::mt19937 generator(3);
  std::uniform_real_distribution<double> distribution(0.0, 1.0);
  std::vector<double> device(1000);
  std::vector<double> host(1000);
  for (auto &value : device) {
    value = distribution(generator);
  }
  for (auto &value : host) {
    value = distribution(generator);
  }
  CHECK(sameMean(device, host));

  auto shifted = device;
  for (auto &value : shifted) {
    value += 1.0;
  }
  CHECK_FALSE(sameMean(shifted, host));

  auto outlier = device;
  outlier.front() += 1.0e6;
  CHECK_FALSE(sameMean(outlier, host));
}
