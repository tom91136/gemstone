from numba.cuda.cudadrv import nvrtc

major, minor = nvrtc.get_lowest_supported_cc()
print(major * 10 + minor)
