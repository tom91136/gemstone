#pragma once

#include "unified_suite/blas.hpp"

namespace unified_suite::regressions {

template <template <ScalarType> class Backend> Case rocblas_f16_case(const Configuration &configuration) {
  const auto test_size =
      std::find(configuration.blas_sizes.begin(), configuration.blas_sizes.end(), std::size_t{8}) !=
              configuration.blas_sizes.end()
          ? std::size_t{8}
          : (configuration.blas_sizes.empty() ? std::size_t{8} : configuration.blas_sizes.front());
  return {"regressions/rocblas/gfx1036/f16/all",
          [test_size] {
            return capture([&] {
              Backend<ScalarType::f16> backend;
              blas::detail::execute_level1<ScalarType::f16>(backend, blas::Operation::axpy, test_size);
            });
          },
          false};
}


} // namespace unified_suite::regressions
