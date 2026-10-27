package net.kurobako.gemstone.explorer

/** The programs the explorer starts from, each chosen to show the machine at work when run unchanged. */
object Examples {

  /** A PTX kernel that renders the Mandelbrot set into a 32 by 8 image, one pixel per lane: a launch of four workgroups
    * of 8 by 8 lanes fills it. The loop and the float arithmetic make the issue timeline and the register liveness
    * worth looking at; the image itself is the buffer the launch writes.
    */
  val mandelbrot: String =
    """.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry mandelbrot(.param .u64 .ptr .global out) {
      |  .reg .b32 %r<8>;
      |  .reg .b64 %rd<3>;
      |  .reg .f32 %f<8>;
      |  .reg .pred %p<3>;
      |
      |  ld.param.u64 %rd0, [out];
      |  mov.u32 %r0, %ctaid.x;
      |  shl.b32 %r0, %r0, 3;
      |  mov.u32 %r1, %tid.x;
      |  add.u32 %r0, %r0, %r1;          // pixel x, 0 to 31
      |  mov.u32 %r2, %tid.y;            // pixel y, 0 to 7
      |  shl.b32 %r3, %r2, 5;
      |  add.u32 %r3, %r3, %r0;          // pixel index
      |  mul.wide.u32 %rd1, %r3, 4;
      |  add.s64 %rd2, %rd0, %rd1;
      |
      |  cvt.rn.f32.u32 %f0, %r0;
      |  mul.f32 %f0, %f0, 0f3D800000;   // x * 1/16
      |  sub.f32 %f0, %f0, 0f3FC00000;   // c real, -1.5 to 0.4375
      |  cvt.rn.f32.u32 %f1, %r2;
      |  mul.f32 %f1, %f1, 0f3E800000;   // y * 1/4
      |  sub.f32 %f1, %f1, 0f3F800000;   // c imaginary, -1.0 to 0.75
      |
      |  mov.f32 %f2, 0f00000000;        // z real
      |  mov.f32 %f3, 0f00000000;        // z imaginary
      |  mov.u32 %r4, 0;                 // iterations
      |LOOP:
      |  mul.f32 %f4, %f2, %f2;
      |  mul.f32 %f5, %f3, %f3;
      |  add.f32 %f6, %f4, %f5;
      |  setp.gt.f32 %p0, %f6, 0f40800000;
      |  setp.ge.u32 %p1, %r4, 24;
      |  or.pred %p2, %p0, %p1;
      |  @%p2 bra DONE;
      |  mul.f32 %f7, %f2, %f3;
      |  add.f32 %f7, %f7, %f7;
      |  add.f32 %f7, %f7, %f1;          // next z imaginary
      |  sub.f32 %f2, %f4, %f5;
      |  add.f32 %f2, %f2, %f0;          // next z real
      |  mov.f32 %f3, %f7;
      |  add.u32 %r4, %r4, 1;
      |  bra LOOP;
      |DONE:
      |  shl.b32 %r5, %r4, 5;
      |  setp.ge.u32 %p1, %r4, 24;
      |  @%p1 mov.u32 %r5, 0;            // points in the set stay black
      |  st.global.u32 [%rd2], %r5;
      |  ret;
      |}
      |""".stripMargin

  /** A PTX kernel that writes one word per lane: a workgroup's lanes land in adjacent words of the output, so the
    * launch covers several workgroups and the timeline has several rows.
    */
  val ptx: String =
    """.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry kernel(.param .u64 .ptr .global out) {
      |  .reg .b32 %r<3>;
      |  .reg .b64 %rd<3>;
      |  ld.param.u64 %rd0, [out];
      |  mov.u32 %r0, %tid.x;
      |  mov.u32 %r1, %ntid.x;
      |  mov.u32 %r2, %ctaid.x;
      |  mad.lo.u32 %r1, %r2, %r1, %r0;
      |  mad.wide.u32 %rd1, %r1, 4, %rd0;
      |  st.global.u32 [%rd1], %r1;
      |  ret;
      |}
      |""".stripMargin
}
