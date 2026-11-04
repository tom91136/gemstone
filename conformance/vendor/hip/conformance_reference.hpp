#pragma once

#include <algorithm>
#include <cmath>
#include <complex>
#include <cstddef>
#include <cstdio>
#include <functional>
#include <limits>
#include <numeric>
#include <type_traits>
#include <utility>
#include <vector>

namespace conformance::reference {

using Extent = std::vector<size_t>;

inline size_t volume(const Extent &extent) { return std::accumulate(extent.begin(), extent.end(), size_t{1}, std::multiplies<>{}); }

inline Extent denseStrides(const Extent &extent) {
  Extent result(extent.size());
  size_t stride = 1;
  for (size_t d = 0; d < extent.size(); ++d) {
    result[d] = stride;
    stride *= extent[d];
  }
  return result;
}

inline Extent unravel(size_t index, const Extent &extent) {
  Extent result(extent.size());
  for (size_t d = 0; d < extent.size(); ++d) {
    result[d] = index % extent[d];
    index /= extent[d];
  }
  return result;
}

inline size_t offset(const Extent &index, const Extent &stride) {
  return std::inner_product(index.begin(), index.end(), stride.begin(), size_t{0});
}

template <typename T> std::complex<double> asComplex(const std::complex<T> &value) {
  return {static_cast<double>(value.real()), static_cast<double>(value.imag())};
}

template <typename T> std::complex<double> asComplex(const T &value) {
  if constexpr (std::is_arithmetic_v<T>) {
    return {static_cast<double>(value), 0.0};
  } else {
    return {static_cast<double>(value.x), static_cast<double>(value.y)};
  }
}

template <typename T> std::complex<double> hermitian(const T *data, const Extent &extent, const Extent &stride, const Extent &index) {
  const bool direct = index[0] <= extent[0] / 2;
  Extent mirrored(index.size());
  for (size_t d = 0; d < index.size(); ++d) {
    mirrored[d] = direct ? index[d] : (extent[d] - index[d]) % extent[d];
  }
  const auto value = asComplex(data[offset(mirrored, stride)]);
  return direct ? value : std::conj(value);
}

template <typename Input> std::complex<double> dft(const Extent &extent, const Extent &frequency, double sign, const Input &input) {
  const double twoPi = 2.0 * std::acos(-1.0);
  std::complex<double> sum = 0.0;
  for (size_t n = 0; n < volume(extent); ++n) {
    const auto index = unravel(n, extent);
    double phase = 0.0;
    for (size_t d = 0; d < extent.size(); ++d) {
      phase += static_cast<double>(frequency[d] * index[d] % extent[d]) / static_cast<double>(extent[d]);
    }
    sum += asComplex(input(index)) * std::polar(1.0, sign * twoPi * phase);
  }
  return sum;
}

namespace detail {

using Complex = std::complex<double>;

inline void transformPowerOfTwo(std::vector<Complex> &values, double sign) {
  const size_t n = values.size();
  for (size_t i = 1, j = 0; i < n; ++i) {
    size_t bit = n >> 1;
    for (; j & bit; bit >>= 1) {
      j ^= bit;
    }
    j ^= bit;
    if (i < j) {
      std::swap(values[i], values[j]);
    }
  }
  const double pi = std::acos(-1.0);
  for (size_t length = 2; length <= n; length <<= 1) {
    for (size_t k = 0; k < length / 2; ++k) {
      const Complex twiddle = std::polar(1.0, (sign * 2.0 * pi * static_cast<double>(k)) / static_cast<double>(length));
      for (size_t start = 0; start < n; start += length) {
        const Complex even = values[start + k];
        const Complex odd = values[start + k + (length / 2)] * twiddle;
        values[start + k] = even + odd;
        values[start + k + (length / 2)] = even - odd;
      }
    }
  }
}

inline void transform(std::vector<Complex> &values, double sign) {
  const size_t n = values.size();
  if ((n & (n - 1)) == 0) {
    transformPowerOfTwo(values, sign);
    return;
  }
  size_t m = 1;
  while (m < (2 * n) - 1) {
    m <<= 1;
  }
  const double pi = std::acos(-1.0);
  std::vector<Complex> chirp(n);
  for (size_t k = 0; k < n; ++k) {
    chirp[k] = std::polar(1.0, (sign * pi * static_cast<double>((k * k) % (2 * n))) / static_cast<double>(n));
  }
  std::vector<Complex> a(m);
  std::vector<Complex> b(m);
  for (size_t k = 0; k < n; ++k) {
    a[k] = values[k] * chirp[k];
    b[k] = std::conj(chirp[k]);
    if (k > 0) {
      b[m - k] = std::conj(chirp[k]);
    }
  }
  transformPowerOfTwo(a, -1.0);
  transformPowerOfTwo(b, -1.0);
  for (size_t i = 0; i < m; ++i) {
    a[i] *= b[i];
  }
  transformPowerOfTwo(a, 1.0);
  for (size_t k = 0; k < n; ++k) {
    values[k] = (chirp[k] * a[k]) / static_cast<double>(m);
  }
}

inline std::vector<Complex> transformExtent(const Extent &extent, std::vector<Complex> values, double sign) {
  size_t stride = 1;
  for (const auto n : extent) {
    std::vector<Complex> line(n);
    for (size_t base = 0; base < values.size(); ++base) {
      if ((base / stride) % n != 0) {
        continue;
      }
      for (size_t j = 0; j < n; ++j) {
        line[j] = values[base + (j * stride)];
      }
      transform(line, sign);
      for (size_t j = 0; j < n; ++j) {
        values[base + (j * stride)] = line[j];
      }
    }
    stride *= n;
  }
  return values;
}

template <typename T, typename = void> struct Component {
  using type = T;
};

template <typename T> struct Component<std::complex<T>> {
  using type = T;
};

template <typename T> struct Component<T, std::void_t<decltype(std::declval<const T &>().x)>> {
  using type = decltype(std::declval<const T &>().x);
};

} // namespace detail

template <typename Output, typename Input>
bool matchesDft(const Extent &extent, const Extent &outputExtent, const Extent &outputStride, const Output *output, double sign,
                const Input &input) {
  constexpr double toleranceFactor = 64.0;
  const size_t count = volume(extent);
  std::vector<detail::Complex> values(count);
  double energy = 0.0;
  for (size_t n = 0; n < count; ++n) {
    values[n] = asComplex(input(unravel(n, extent)));
    energy += std::norm(values[n]);
  }
  values = detail::transformExtent(extent, std::move(values), sign);

  using Real = typename detail::Component<Output>::type; // NOLINT(readability-redundant-typename): the examples build as C++17
  const double log2Count = std::log2(static_cast<double>(std::max<size_t>(count, 2)));
  const double norm = std::sqrt(static_cast<double>(count) * energy);
  const double tolerance = toleranceFactor * static_cast<double>(std::numeric_limits<Real>::epsilon()) * log2Count * norm;

  const auto denseStride = denseStrides(extent);
  const size_t outputCount = volume(outputExtent);
  double worst = 0.0;
  for (size_t k = 0; k < outputCount; ++k) {
    const auto frequency = unravel(k, outputExtent);
    const auto expected = values[offset(frequency, denseStride)];
    const auto actual = asComplex(output[offset(frequency, outputStride)]);
    const double error = std::is_floating_point_v<Output> ? std::abs(expected.real() - actual.real()) : std::abs(expected - actual);
    if (std::isnan(error) || error > worst) worst = error;
  }
  std::fprintf(stderr, "matchesDft: max error %.6e bound %.6e\n", worst, tolerance);
  return worst <= tolerance;
}

template <typename T> bool sameMean(const std::vector<T> &device, const std::vector<T> &host) {
  const auto mean = [](const std::vector<T> &values) {
    double sum = 0.0;
    for (const auto value : values) {
      sum += static_cast<double>(value);
    }
    return sum / static_cast<double>(values.size());
  };
  const auto variance = [&mean](const std::vector<T> &values) {
    const double m = mean(values);
    double sum = 0.0;
    for (const auto value : values) {
      sum += (static_cast<double>(value) - m) * (static_cast<double>(value) - m);
    }
    return sum / static_cast<double>(values.size() - 1);
  };
  const double standardError = std::sqrt(2.0 * variance(host) / static_cast<double>(device.size()));
  return std::abs(mean(device) - mean(host)) < 6.0 * standardError;
}

} // namespace conformance::reference
