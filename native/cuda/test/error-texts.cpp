#include <cstddef>
#include <string>

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>
#include <cuda_runtime_api.h>

#include <gemstone_cuda_error_texts.hpp>

namespace {

const CudaErrorText *recorded(int value) {
  for (const auto &row : ErrorTextRows)
    if (row.value == value) return &row;
  return nullptr;
}

} // namespace

TEST_CASE("CUDA names and strings match the driver and runtime for every status", "[cuda][errors]") {
  size_t enumRows = 0;
  for (const auto &status : EnumValues) {
    const auto *row = recorded(status.value);
    REQUIRE(row != nullptr);
    CHECK(std::string(row->enumName) == status.enumName);
  }
  for (const auto &row : ErrorTextRows)
    if (std::string(row.enumName) != "EXTRA") ++enumRows;
  CHECK(enumRows == EnumValues.size());

  for (const auto &row : ErrorTextRows) {
    INFO("status " << std::to_string(row.value));
    const char *driverName = nullptr;
    CHECK(cuGetErrorName(static_cast<CUresult>(row.value), &driverName) == static_cast<CUresult>(row.driverNameStatus));
    if (row.driverName) {
      REQUIRE(driverName != nullptr);
      CHECK(std::string(driverName) == row.driverName);
    }
    const char *driverMessage = nullptr;
    CHECK(cuGetErrorString(static_cast<CUresult>(row.value), &driverMessage) == static_cast<CUresult>(row.driverStringStatus));
    if (row.driverMessage) {
      REQUIRE(driverMessage != nullptr);
      CHECK(std::string(driverMessage) == row.driverMessage);
    }
    CHECK(std::string(cudaGetErrorName(static_cast<cudaError_t>(row.value))) == row.runtimeName);
    CHECK(std::string(cudaGetErrorString(static_cast<cudaError_t>(row.value))) == row.runtimeMessage);
  }
}
