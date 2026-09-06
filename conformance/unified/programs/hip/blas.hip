#include "../provider.hpp"

#include "unified_suite/adapters/rocblas.hpp"
#include "unified_suite/blas.hpp"
#include "unified_suite/legacy_reds.hpp"

#include <algorithm>

namespace unified_suite::program {

std::vector<Case> blas_cases(const Configuration &configuration) {
  auto result = blas::cases<adapters::Rocblas>(configuration);
  if (configuration.include_legacy_reds &&
      std::find(configuration.blas_types.begin(), configuration.blas_types.end(), ScalarType::f16) !=
          configuration.blas_types.end())
    result.push_back(legacy_reds::rocblas_f16_case<adapters::Rocblas>(configuration));
  return result;
}

} // namespace unified_suite::program
