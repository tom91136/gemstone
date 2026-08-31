#include "../provider.hpp"

#include "unified_suite/adapters/cublas.hpp"
#include "unified_suite/blas.hpp"

namespace unified_suite::program {

std::vector<Case> blas_cases(const Configuration &configuration) {
  return blas::cases<adapters::Cublas>(configuration);
}

} // namespace unified_suite::program
