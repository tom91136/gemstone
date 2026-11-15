# BabelStream Julia dependency downloads need a device only at run time, never here.
using CUDA
CUDA.precompile_runtime()
