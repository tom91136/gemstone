// The same layouts and pose as struct_alignment.hpp, in OpenCL C; _Static_assert fails the build if a compiler ignores an attribute
#define POSE_R00 0.36F
#define POSE_R01 0.48F
#define POSE_R02 -0.8F
#define POSE_R10 -0.8F
#define POSE_R11 0.6F
#define POSE_R12 0.0078125F
#define POSE_R20 0.48F
#define POSE_R21 0.64F
#define POSE_R22 0.6F
#define POSE_T0 1.5F
#define POSE_T1 -2.25F
#define POSE_T2 0.75F

struct __attribute__((packed)) Packed { float x, y, z; int type; };
struct __attribute__((packed, aligned(2))) Packed2 { float x, y, z; int type; };
struct __attribute__((packed, aligned(4))) Packed4 { float x, y, z; int type; };
struct __attribute__((packed, aligned(8))) Packed8 { float x, y, z; int type; };
struct Natural { float x, y, z; int type; };
struct __attribute__((aligned(16))) Aligned16 { float x, y, z; int type; };
struct __attribute__((packed)) MixedPacked { char tag; float x, y, z; int type; };
struct MixedNatural { char tag; float x, y, z; int type; };

_Static_assert(__alignof__(struct Packed) == 1 && sizeof(struct Packed) == 16, "packed");
_Static_assert(__alignof__(struct Packed2) == 2 && sizeof(struct Packed2) == 16, "packed aligned(2)");
_Static_assert(__alignof__(struct Packed4) == 4 && sizeof(struct Packed4) == 16, "packed aligned(4)");
_Static_assert(__alignof__(struct Packed8) == 8 && sizeof(struct Packed8) == 16, "packed aligned(8)");
_Static_assert(__alignof__(struct Natural) == 4 && sizeof(struct Natural) == 16, "natural");
_Static_assert(__alignof__(struct Aligned16) == 16 && sizeof(struct Aligned16) == 16, "aligned(16)");
_Static_assert(__alignof__(struct MixedPacked) == 1 && sizeof(struct MixedPacked) == 17, "mixed packed");
_Static_assert(__alignof__(struct MixedNatural) == 4 && sizeof(struct MixedNatural) == 20, "mixed natural");
_Static_assert(__builtin_offsetof(struct MixedPacked, x) == 1 && __builtin_offsetof(struct MixedPacked, type) == 13,
               "mixed packed offsets");
_Static_assert(__builtin_offsetof(struct MixedNatural, x) == 4 && __builtin_offsetof(struct MixedNatural, type) == 16,
               "mixed natural offsets");

#define POSE(NAME, RECORD, READ_TAG, WRITE_TAG)                                                                        \
  __kernel void NAME(__global uchar *bytes, uint offset, __global uint *out, uint count) {                             \
    uint i = get_global_id(0);                                                                                         \
    if (i >= count) return;                                                                                            \
    __global RECORD *records = (__global RECORD *)(bytes + offset);                                                    \
    float x = records[i].x;                                                                                            \
    float y = records[i].y;                                                                                            \
    float z = records[i].z;                                                                                            \
    int type = records[i].type;                                                                                        \
    uint tag = READ_TAG;                                                                                               \
    float tx = fma(POSE_R00, x, fma(POSE_R01, y, fma(POSE_R02, z, POSE_T0)));                                          \
    float ty = fma(POSE_R10, x, fma(POSE_R11, y, fma(POSE_R12, z, POSE_T1)));                                          \
    float tz = fma(POSE_R20, x, fma(POSE_R21, y, fma(POSE_R22, z, POSE_T2)));                                          \
    __global uint *row = out + i * 8;                                                                                  \
    row[0] = as_uint(tx);                                                                                              \
    row[1] = as_uint(ty);                                                                                              \
    row[2] = as_uint(tz);                                                                                              \
    row[3] = as_uint(x);                                                                                               \
    row[4] = as_uint(y);                                                                                               \
    row[5] = as_uint(z);                                                                                               \
    row[6] = (uint)type;                                                                                               \
    row[7] = tag;                                                                                                      \
    if (i % 2 == 0) {                                                                                                  \
      records[i].x = tx;                                                                                               \
      records[i].y = ty;                                                                                               \
      records[i].z = tz;                                                                                               \
      records[i].type = ~type;                                                                                         \
      WRITE_TAG;                                                                                                       \
    }                                                                                                                  \
  }

#define TAG_READ (uint)(uchar)records[i].tag
#define TAG_WRITE records[i].tag = (char)(tag ^ 0x5au)

POSE(pose_packed, struct Packed, 0u, (void)0)
POSE(pose_packed_aligned_2, struct Packed2, 0u, (void)0)
POSE(pose_packed_aligned_4, struct Packed4, 0u, (void)0)
POSE(pose_packed_aligned_8, struct Packed8, 0u, (void)0)
POSE(pose_natural, struct Natural, 0u, (void)0)
POSE(pose_aligned_16, struct Aligned16, 0u, (void)0)
POSE(pose_mixed_packed, struct MixedPacked, TAG_READ, TAG_WRITE)
POSE(pose_mixed_natural, struct MixedNatural, TAG_READ, TAG_WRITE)
