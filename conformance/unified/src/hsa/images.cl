__kernel void image_transform(read_only image2d_t source, write_only image2d_t target, __global uint *info) {
  const int2 at = (int2)(get_global_id(0), get_global_id(1));
  const uint4 texel = read_imageui(source, at);
  write_imageui(target, at, (uint4)(texel.y + (uint)at.x, texel.x ^ (uint)at.y, texel.w, texel.z));
  if (at.x == 0 && at.y == 0) {
    info[0] = (uint)get_image_width(source);
    info[1] = (uint)get_image_height(source);
  }
}
