#pragma region case: cublas-smoke
#pragma region cublas
#pragma region do: {cuda_cublas_compiler} -x c++ -std=c++17 -O2 {input} -x none {cuda_cublas_include} {cuda_cublas_link} -o {output}
#pragma region do: {output}
#pragma region requires: pass

#include <cublas_v2.h>
#include <cuda_runtime.h>

#include <cmath>
#include <cstdlib>
#include <cstdio>
#include <vector>

#define CUDA_REQUIRE(expression)                                                                                                           \
  do {                                                                                                                                     \
    const cudaError_t status = (expression);                                                                                               \
    if (status != cudaSuccess) {                                                                                                           \
      std::fprintf(stderr, "%s failed: %s at line %d\n", #expression, cudaGetErrorString(status), __LINE__);                           \
      return 1;                                                                                                                            \
    }                                                                                                                                      \
  } while (false)

#define CUBLAS_REQUIRE(expression)                                                                                                         \
  do {                                                                                                                                     \
    const cublasStatus_t status = (expression);                                                                                            \
    if (status != CUBLAS_STATUS_SUCCESS) {                                                                                                 \
      std::fprintf(stderr, "%s failed with cuBLAS status %d at line %d\n", #expression, static_cast<int>(status), __LINE__);           \
      return 1;                                                                                                                            \
    }                                                                                                                                      \
  } while (false)

static bool close_enough(float actual, float expected) {
  return std::fabs(actual - expected) <= 1.0e-4F * (1.0F + std::fabs(expected));
}

int main() {
  constexpr int rows    = 3;
  constexpr int columns = 4;
  constexpr int inner   = 5;

  std::vector<float> left(rows * inner);
  std::vector<float> right(inner * columns);
  std::vector<float> output(rows * columns, 0.0F);
  std::vector<float> expected(rows * columns, 0.0F);
  for (int index = 0; index < static_cast<int>(left.size()); ++index)
    left[index] = static_cast<float>((index % 7) - 3) / 5.0F;
  for (int index = 0; index < static_cast<int>(right.size()); ++index)
    right[index] = static_cast<float>((index % 11) - 5) / 7.0F;
  for (int column = 0; column < columns; ++column)
    for (int row = 0; row < rows; ++row)
      for (int index = 0; index < inner; ++index)
        expected[row + column * rows] += left[row + index * rows] * right[index + column * inner];

  cublasHandle_t handle = nullptr;
  float *device_left = nullptr;
  float *device_right = nullptr;
  float *device_output = nullptr;
  CUBLAS_REQUIRE(cublasCreate(&handle));
  int version = 0;
  CUBLAS_REQUIRE(cublasGetVersion(handle, &version));
  if (version <= 0) {
    std::fprintf(stderr, "cuBLAS returned an invalid version: %d\n", version);
    return 1;
  }

  CUBLAS_REQUIRE(cublasSetPointerMode(handle, CUBLAS_POINTER_MODE_HOST));
  cublasPointerMode_t pointer_mode = CUBLAS_POINTER_MODE_DEVICE;
  CUBLAS_REQUIRE(cublasGetPointerMode(handle, &pointer_mode));
  if (pointer_mode != CUBLAS_POINTER_MODE_HOST) {
    std::fprintf(stderr, "cuBLAS pointer mode was not retained\n");
    return 1;
  }

  CUDA_REQUIRE(cudaMalloc(reinterpret_cast<void **>(&device_left), left.size() * sizeof(float)));
  CUDA_REQUIRE(cudaMalloc(reinterpret_cast<void **>(&device_right), right.size() * sizeof(float)));
  CUDA_REQUIRE(cudaMalloc(reinterpret_cast<void **>(&device_output), output.size() * sizeof(float)));
  CUDA_REQUIRE(cudaMemcpy(device_left, left.data(), left.size() * sizeof(float), cudaMemcpyHostToDevice));
  CUDA_REQUIRE(cudaMemcpy(device_right, right.data(), right.size() * sizeof(float), cudaMemcpyHostToDevice));

  constexpr float alpha = 1.0F;
  constexpr float beta  = 0.0F;
  CUBLAS_REQUIRE(cublasSgemm(
      handle,
      CUBLAS_OP_N,
      CUBLAS_OP_N,
      rows,
      columns,
      inner,
      &alpha,
      device_left,
      rows,
      device_right,
      inner,
      &beta,
      device_output,
      rows));
  CUDA_REQUIRE(cudaDeviceSynchronize());
  CUDA_REQUIRE(cudaMemcpy(output.data(), device_output, output.size() * sizeof(float), cudaMemcpyDeviceToHost));
  for (int index = 0; index < static_cast<int>(output.size()); ++index) {
    if (!close_enough(output[index], expected[index])) {
      std::fprintf(stderr, "Sgemm element %d: expected %.8f, got %.8f\n", index, expected[index], output[index]);
      return 1;
    }
  }

  std::vector<float> vector_input{1.0F, -2.0F, 3.0F, -4.0F};
  constexpr float scale = 2.5F;
  std::vector<float> vector_expected = vector_input;
  for (float &value : vector_expected) value = (scale + 1.0F) * value;
  float *device_vector = nullptr;
  CUDA_REQUIRE(cudaMalloc(reinterpret_cast<void **>(&device_vector), vector_input.size() * sizeof(float)));
  CUDA_REQUIRE(cudaMemcpy(device_vector, vector_input.data(), vector_input.size() * sizeof(float), cudaMemcpyHostToDevice));
  CUBLAS_REQUIRE(cublasSaxpy(handle, static_cast<int>(vector_input.size()), &scale, device_vector, 1, device_vector, 1));
  CUDA_REQUIRE(cudaDeviceSynchronize());
  CUDA_REQUIRE(cudaMemcpy(vector_input.data(), device_vector, vector_input.size() * sizeof(float), cudaMemcpyDeviceToHost));
  for (int index = 0; index < static_cast<int>(vector_input.size()); ++index) {
    if (!close_enough(vector_input[index], vector_expected[index])) {
      std::fprintf(stderr, "Saxpy element %d: expected %.8f, got %.8f\n", index, vector_expected[index], vector_input[index]);
      return 1;
    }
  }

  std::vector<float> matrix_vector(inner);
  std::vector<float> matrix_expected(rows, 0.0F);
  for (int index = 0; index < inner; ++index) matrix_vector[index] = static_cast<float>(index + 1) / 3.0F;
  for (int row = 0; row < rows; ++row)
    for (int index = 0; index < inner; ++index)
      matrix_expected[row] += left[row + index * rows] * matrix_vector[index];
  float *device_matrix_vector = nullptr;
  float *device_matrix_output = nullptr;
  CUDA_REQUIRE(cudaMalloc(reinterpret_cast<void **>(&device_matrix_vector), matrix_vector.size() * sizeof(float)));
  CUDA_REQUIRE(cudaMalloc(reinterpret_cast<void **>(&device_matrix_output), matrix_expected.size() * sizeof(float)));
  CUDA_REQUIRE(cudaMemcpy(
      device_matrix_vector,
      matrix_vector.data(),
      matrix_vector.size() * sizeof(float),
      cudaMemcpyHostToDevice));
  CUBLAS_REQUIRE(cublasSgemv(
      handle,
      CUBLAS_OP_N,
      rows,
      inner,
      &alpha,
      device_left,
      rows,
      device_matrix_vector,
      1,
      &beta,
      device_matrix_output,
      1));
  CUDA_REQUIRE(cudaDeviceSynchronize());
  CUDA_REQUIRE(cudaMemcpy(
      matrix_vector.data(),
      device_matrix_output,
      matrix_expected.size() * sizeof(float),
      cudaMemcpyDeviceToHost));
  for (int index = 0; index < static_cast<int>(matrix_expected.size()); ++index) {
    if (!close_enough(matrix_vector[index], matrix_expected[index])) {
      std::fprintf(stderr, "Sgemv element %d: expected %.8f, got %.8f\n", index, matrix_expected[index], matrix_vector[index]);
      return 1;
    }
  }

  CUDA_REQUIRE(cudaFree(device_matrix_output));
  CUDA_REQUIRE(cudaFree(device_matrix_vector));
  CUDA_REQUIRE(cudaFree(device_vector));
  CUDA_REQUIRE(cudaFree(device_output));
  CUDA_REQUIRE(cudaFree(device_right));
  CUDA_REQUIRE(cudaFree(device_left));
  CUBLAS_REQUIRE(cublasDestroy(handle));
  std::puts("pass");
  return 0;
}
