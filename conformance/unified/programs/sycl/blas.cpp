#include "../provider.hpp"

#include "unified_suite/adapters/onemkl.hpp"
#include "unified_suite/blas.hpp"

namespace unified_suite::program {

std::vector<Case> blas_cases(const Configuration &configuration) {
  return blas::cases<adapters::OneMkl>(configuration);
}

} // namespace unified_suite::program
