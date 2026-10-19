package net.kurobako.gemstone.isa.nvidia.fixtures

/** Probes of the matrix instructions ptxas 13.3 emits: each lane loads its fragment words, runs one instruction and
  * stores the result, for the inputs [[inputs]] derives.
  */
object MatrixProbes {

  /** Each lane's fragment words in turn from a 64-bit LCG's high words, with float exponents kept small by the masks of
    * A's, B's and C's words.
    */
  def inputs(probe: Probe): Vector[Long] = {
    val (a, b, c) = (probe.a, probe.b, probe.c)
    val masks     = probe.masks
    val count     = a + b + c
    Iterator.iterate(1L)(state => state * 6364136223846793005L + 1442695040888963407L).drop(1).take(32 * count)
      .zipWithIndex.map { (state, index) =>
        val slot = index % count
        val mask = if (slot < a) masks._1 else if (slot < a + b) masks._2 else masks._3
        (state >>> 32) & mask
      }.toVector
  }

  /** A probe's PTX for `sm`, the fragment words of A, B and C (with any further operands) each lane loads, the words it
    * stores, the masks of A's, B's and C's words, and the words an RTX 4060 Ti stores where it runs the probe.
    */
  case class Probe(
      name: String,
      sm: Int,
      ptx: String,
      a: Int,
      b: Int,
      c: Int,
      d: Int,
      masks: (Long, Long, Long),
      measured: Option[String]
  )

  /** The measured words of a probe. */
  def words(text: String): Vector[Long] =
    text.split("\\s+").toVector.filter(_.nonEmpty).map(java.lang.Long.parseLong(_, 16))

  /** Each probe's PTX, fragment register counts of A, B and C and of the result, input masks and measured words. */
  val probes: Vector[Probe] = Vector(
    Probe(
      "immaS8SatSm89",
      89,
      """.version 8.7
      |.target sm_89
      |.address_size 64
      |.visible .entry immaS8SatSm89(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<48>;
      |.reg .b64 %rd<8>;
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %tid.x;
      |mul.wide.u32 %rd2, %r0, 40;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |ld.global.b32 %r5, [%rd3+16];
      |ld.global.b32 %r6, [%rd3+20];
      |ld.global.b32 %r7, [%rd3+24];
      |ld.global.b32 %r8, [%rd3+28];
      |ld.global.b32 %r9, [%rd3+32];
      |ld.global.b32 %r10, [%rd3+36];
      |mma.sync.aligned.m16n8k32.row.col.satfinite.s32.s8.s8.s32 {%r20,%r21,%r22,%r23}, {%r1,%r2,%r3,%r4}, {%r5,%r6}, {%r7,%r8,%r9,%r10};
      |mul.wide.u32 %rd4, %r0, 16;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |st.global.b32 [%rd5+8], %r22;
      |st.global.b32 [%rd5+12], %r23;
      |ret;
      |}
      |""".stripMargin,
      4,
      2,
      4,
      4,
      (0xffffffffL, 0xffffffffL, 0xffffffffL),
      Some(
        """8dceb017 10bf1c17 d6f7e519 32cd3f54 6cae5a64 927808be 9f646f1e 3406b4d5
        |96f56f07 0e0f245e f6fabbea e34dbc0c ccd96aea 2f0620ee 0474f8cc 4e403fb7
        |2fa2a2ae 8f41efca 036260cd 2eaa4eac 5b1d659a eff817f3 d610b094 12bb36ce
        |881784f1 3953958b e9e4fca1 1fc902dd 0981093d 5d898cab 9cb78724 924680e0
        |aa9c6593 04c85625 16394842 8b3a7804 5d1d917a dc10d394 b40e0fb2 74c09686
        |684615c5 4b8d09c7 5121f867 4a4b6320 5ec64309 ea9b4138 dfa8ae24 9e1351d0
        |3b0fa3e3 e9767e8c 289a0925 5b0689ac 43cb35e9 ec36cd7d c479e237 69be76e1
        |919f5612 1887657a b3fa62c6 783e59e7 34f2a8c5 65f0561f a9188491 3f9904e4
        |e5d5adb5 4dbf0b5c 423771bc ca10a633 cbbe5512 768ff6b9 a8d08c23 72a2c003
        |04391796 2016ffa1 e5814754 42f0957d f5721c51 d6cc34a7 65dde744 d4a389db
        |0a178460 f993a2f7 2da0df61 8b1e17b6 1a6c2189 4773881e e62291ce 17cc1c67
        |c27b84d0 bae896ac 07676be4 1c457bbe 3b6f475a 15371e2b 489d35f5 d60799db
        |2f14a7e2 daa2d70f a76e6320 4aa82b55 23bfd0cd c6794c3b 70d4a6fd 53473aba
        |6a26c0a4 eaccda09 474063fb eb627857 2427a71f a2df9197 32cf6151 8f3da4d3
        |c3252c53 b138bd3f 381cc4f4 8f73130e 5bf17a3b 116a1d17 45782fe8 7bbc8b2d
        |1e874fc9 e0d63a85 43984e98 9cd8160e 6bb94d28 94c47fc6 db77601b c9248def
        |""".stripMargin
      )
    ),
    Probe(
      "immaU4Sm89",
      89,
      """.version 8.7
      |.target sm_89
      |.address_size 64
      |.visible .entry immaU4Sm89(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<48>;
      |.reg .b64 %rd<8>;
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %tid.x;
      |mul.wide.u32 %rd2, %r0, 16;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |mma.sync.aligned.m8n8k32.row.col.s32.u4.u4.s32 {%r20,%r21}, {%r1}, {%r2}, {%r3,%r4};
      |mul.wide.u32 %rd4, %r0, 8;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |ret;
      |}
      |""".stripMargin,
      1,
      1,
      2,
      2,
      (0xffffffffL, 0xffffffffL, 0xffffffffL),
      Some(
        """a5fae821 62035fb3 8dcebd7e 10bf59bd beea8dea 89277201 3e5b6161 bffae8f3
        |9f64df8a 3406ccf5 8cbc9f17 9f21bdf3 96f62710 0e0f447c 4880d59d a3e9536f
        |8a16bf05 3f8e3582 0474c99a 4e40558f fc9f9e6a 5bd74afa 2fa1bc88 8f416743
        |7568cd2d 49abd0f6 98d1aeba f94aa7da d6109aca 12bb5f0a 3c8b1266 d75b4306
        |8817bf82 395379a0 80401aa7 35d89d36 c97759da f6620fa3 9cb6dc46 9245e4f3
        |70690fdf 1340f310 aa9cee3f 04c7d8c6 adc1f6d3 0ae62c7f b18f402e 8527f0d9
        |b40e0fd8 74c1099b b803099e fe2c02e5 684691bc 4b8d0e94 3373af9f db6552c4
        |47d7ffe1 6dad54e8 dfa86004 9e1286fd b2b12777 6843ddb9 3b0fde51 e9762bb1
        |""".stripMargin
      )
    ),
    Probe(
      "bmmaAndSm89",
      89,
      """.version 8.7
      |.target sm_89
      |.address_size 64
      |.visible .entry bmmaAndSm89(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<48>;
      |.reg .b64 %rd<8>;
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %tid.x;
      |mul.wide.u32 %rd2, %r0, 40;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |ld.global.b32 %r5, [%rd3+16];
      |ld.global.b32 %r6, [%rd3+20];
      |ld.global.b32 %r7, [%rd3+24];
      |ld.global.b32 %r8, [%rd3+28];
      |ld.global.b32 %r9, [%rd3+32];
      |ld.global.b32 %r10, [%rd3+36];
      |mma.sync.aligned.m16n8k256.row.col.s32.b1.b1.s32.and.popc {%r20,%r21,%r22,%r23}, {%r1,%r2,%r3,%r4}, {%r5,%r6}, {%r7,%r8,%r9,%r10};
      |mul.wide.u32 %rd4, %r0, 16;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |st.global.b32 [%rd5+8], %r22;
      |st.global.b32 [%rd5+12], %r23;
      |ret;
      |}
      |""".stripMargin,
      4,
      2,
      4,
      4,
      (0xffffffffL, 0xffffffffL, 0xffffffffL),
      Some(
        """8dceb57d 10bf5235 d6f84a88 32ccf7ae 6cae6a72 92780c89 9f64d9e4 3406c4aa
        |96f61e8a 0e0f3d90 f6fa4ae0 e34e2f7e ccd985db 2f066b23 0474c1f5 4e404e73
        |2fa1b622 8f415e41 0362e29b 2eaa4589 5b1ddbda eff801a4 d6109333 12bb5854
        |8817b999 39537135 e9e56d86 1fc8a499 09816a06 5d8a52ce 9cb6d5a2 9245dc59
        |aa9ce8b3 04c7d228 163950a9 8b3a793e 5d1d5729 dc1114cf b40e0a70 74c10287
        |68468a63 4b8d0830 51227a15 4a4bc316 5ec5cafe ea9b1c30 dfa8580e 9e127fb2
        |3b0fd749 e97622f2 2899a4c3 5b06c5a4 43cb485b ec366b97 c479842d 69be5900
        |919ef48b 18878147 b3fa25bf 783e7c83 34f30d19 65f11753 a9191842 3f993c92
        |e5d68475 4dbfacaf 4237a24c ca0fcdb4 cbbe6fbe 7690f3c5 a8d0d65d 72a3524e
        |0439441b 2015e46c e581d870 42f13ae8 f572035e d6cb8f6e 65de38f7 d4a433fd
        |0a167fe2 f993dbd6 2da0c296 8b1d78b0 1a6b8454 47743d45 e6232d24 17cbf62d
        |c27c106c bae77b6b 07672af8 1c46a479 3b6ef535 153746cf 489c68d4 d6072190
        |2f149acc daa2ec02 a76df05b 4aa86941 23bf7922 c6791e5e 70d50bb1 5347a8df
        |6a27754f eacd64c4 47401c80 eb62af5e 24282567 a2df562b 32cfc529 8f3e3be6
        |c325b114 b138929c 381ccb68 8f72fdc0 5bf15ac9 1169808b 4578acb0 7bbc82ce
        |1e879d6d e0d5b3c1 43980fb9 9cd84daf 6bb901d4 94c413ab db77bd99 c9245bbb
        |""".stripMargin
      )
    ),
    Probe(
      "bmmaXorSm89",
      89,
      """.version 8.7
      |.target sm_89
      |.address_size 64
      |.visible .entry bmmaXorSm89(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<48>;
      |.reg .b64 %rd<8>;
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %tid.x;
      |mul.wide.u32 %rd2, %r0, 16;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |mma.sync.aligned.m8n8k128.row.col.s32.b1.b1.s32.xor.popc {%r20,%r21}, {%r1}, {%r2}, {%r3,%r4};
      |mul.wide.u32 %rd4, %r0, 8;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |ret;
      |}
      |""".stripMargin,
      1,
      1,
      2,
      2,
      (0xffffffffL, 0xffffffffL, 0xffffffffL),
      Some(
        """a5fae1e3 62035612 8dceb56c 10bf522b beea8434 89276a21 3e5b59ae bffae057
        |9f64d9f0 3406c4aa 8cbc9801 9f21b700 96f61e80 0e0f3d7d 4880cf88 a3e94a59
        |8a16b95a 3f8e2cf3 0474c1fc 4e404e7f fc9f9551 5bd743b7 2fa1b627 8f415e43
        |7568c78d 49abca15 98d1a87e f94aa298 d610932f 12bb5845 3c8b0c1d d75b3cb0
        |8817b9a5 39537130 80401383 35d89681 c9775158 f6620952 9cb6d59d 9245dc52
        |70690bb8 1340ecd4 aa9ce8b4 04c7d21f adc1ef6c 0ae6266a b18f3ac4 8527ea06
        |b40e0a6f 74c10283 b8030380 fe2bfce4 68468a64 4b8d0829 3373a930 db654c4b
        |47d7fa5e 6dad4d0c dfa85811 9e127fb0 b2b11ecd 6843d65b 3b0fd74e e97622f3
        |""".stripMargin
      )
    ),
    Probe(
      "qmmaE4m3E5m2Sm89",
      89,
      """.version 8.7
      |.target sm_89
      |.address_size 64
      |.visible .entry qmmaE4m3E5m2Sm89(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<48>;
      |.reg .b64 %rd<8>;
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %tid.x;
      |mul.wide.u32 %rd2, %r0, 40;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |ld.global.b32 %r5, [%rd3+16];
      |ld.global.b32 %r6, [%rd3+20];
      |ld.global.b32 %r7, [%rd3+24];
      |ld.global.b32 %r8, [%rd3+28];
      |ld.global.b32 %r9, [%rd3+32];
      |ld.global.b32 %r10, [%rd3+36];
      |mma.sync.aligned.m16n8k32.row.col.f32.e4m3.e5m2.f32 {%r20,%r21,%r22,%r23}, {%r1,%r2,%r3,%r4}, {%r5,%r6}, {%r7,%r8,%r9,%r10};
      |mul.wide.u32 %rd4, %r0, 16;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |st.global.b32 [%rd5+8], %r22;
      |st.global.b32 [%rd5+12], %r23;
      |ret;
      |}
      |""".stripMargin,
      4,
      2,
      4,
      4,
      (0xbfbfbfbfL, 0xbfbfbfbfL, 0xbfffffffL),
      Some(
        """c033c000 bf8f6000 bdc30800 bfc75400 bf986800 bf41a000 bf146000 3f647400
        |3e7e9000 3f361800 3ef77000 bf7b3c00 beefb000 bf660000 3c048000 be8c1c00
        |400e8400 3d6d1000 bf9cf000 4009a800 be785000 bec49c00 3e75e400 be8fe000
        |3f85b400 402a3400 bfb2e800 3f813000 c0163400 bf3fe000 3f85b800 bf8ec800
        |c03bb400 4084b400 3ebad000 3fb1f000 3f850c00 3f54e800 3ef06000 3f9e6c00
        |bf3e7400 401f5800 bfa6bc00 be6ac000 3fabe800 bf68a000 40471c00 3cd50000
        |3fcfb400 c0478400 3f4b2400 bf320c00 be156000 bf2a8800 bd1cb000 3fbed800
        |3f34d000 3fae2000 3ef7e000 be793000 bf066400 be91b000 bf3f6800 406fcc00
        |c02a2400 bfb04000 bf3a2800 3f1f1000 bd07b000 bf393400 beaf6800 3fb3d000
        |40717c00 c06ce400 be564800 4051bc00 c09b6c00 be099800 3ea8c400 bf2c1000
        |40537800 3fa06400 3e444000 bf964800 3fabd400 3fe18c00 c0233000 bf19f400
        |c0431800 bdbdc000 bf1c2000 3f3b4000 3f714400 c02dd800 3e687c00 3eeb9000
        |3f8a2c00 bfb70c00 bcc9e000 bff9d400 bf6fe000 bf39a000 bff01400 bf9d6000
        |bf63dc00 bfae6000 3f78ac00 40373c00 3fea7c00 bf52c000 be94f400 be160000
        |bf1b3400 bed96000 be42c000 3e83c800 bd93e000 40253c00 3e234400 c039d800
        |3e85fc00 3f777800 3f06d400 bf8fb000 3f91c400 3f2db400 bf84a800 bf1bf400
        |""".stripMargin
      )
    ),
    Probe(
      "qmmaE5m2F16Sm89",
      89,
      """.version 8.7
      |.target sm_89
      |.address_size 64
      |.visible .entry qmmaE5m2F16Sm89(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<48>;
      |.reg .b64 %rd<8>;
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %tid.x;
      |mul.wide.u32 %rd2, %r0, 20;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |ld.global.b32 %r5, [%rd3+16];
      |mma.sync.aligned.m16n8k16.row.col.f16.e5m2.e5m2.f16 {%r20,%r21}, {%r1,%r2}, {%r3}, {%r4,%r5};
      |mul.wide.u32 %rd4, %r0, 8;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |ret;
      |}
      |""".stripMargin,
      2,
      1,
      2,
      2,
      (0xbfbfbfbfL, 0xbfbfbfbfL, 0xbfffbfffL),
      Some(
        """b4efb0dc b3193a74 2b6239ae 308cb6dc 41353916 3d6c9e42 32b89530 385dabf7
        |3bccc011 26223957 b78abc48 ba1e3ada a630c14a 3669ba01 bd1bbbcc a3c8a1f4
        |3ebe2ffd b64c3a9f 38f2b89b 2fee1f74 c1299fb2 1c34a6a2 4071af08 21a5a04a
        |a9c24332 b806bd4e b4452f82 ae14b630 bb2c3907 b757bc81 a27dba0a b8bdb628
        |2d3cb30c 2632b16a 3905b915 aab7385a b1a8a8bb b1903903 b514b082 3284ad2e
        |be8ebc78 386abaf0 afa23b26 3282b26b b6d536f2 acbbb9f9 32022837 a0a33f4b
        |2f393c0d 3ab83474 2826a29d 3887a956 acb7beef b807b101 2e38b1fb 39068d40
        |21b8bd8d b650398d b6243785 38003c9c 1c17b8f8 b1fc2623 21c5aacc 3f983bce
        |""".stripMargin
      )
    ),
    Probe(
      "hmmaBf16Sm89",
      89,
      """.version 8.7
      |.target sm_89
      |.address_size 64
      |.visible .entry hmmaBf16Sm89(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<48>;
      |.reg .b64 %rd<8>;
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %tid.x;
      |mul.wide.u32 %rd2, %r0, 40;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |ld.global.b32 %r5, [%rd3+16];
      |ld.global.b32 %r6, [%rd3+20];
      |ld.global.b32 %r7, [%rd3+24];
      |ld.global.b32 %r8, [%rd3+28];
      |ld.global.b32 %r9, [%rd3+32];
      |ld.global.b32 %r10, [%rd3+36];
      |mma.sync.aligned.m16n8k16.row.col.f32.bf16.bf16.f32 {%r20,%r21,%r22,%r23}, {%r1,%r2,%r3,%r4}, {%r5,%r6}, {%r7,%r8,%r9,%r10};
      |mul.wide.u32 %rd4, %r0, 16;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |st.global.b32 [%rd5+8], %r22;
      |st.global.b32 [%rd5+12], %r23;
      |ret;
      |}
      |""".stripMargin,
      4,
      2,
      4,
      4,
      (0xbfffbfffL, 0xbfffbfffL, 0xbfffffffL),
      Some(
        """bdc82e00 ae268a80 26ad293e b677da11 31030d34 2e29a600 b3b5400b 367d0e49
        |3375e90c a99520a9 b6fa4a0c ac132699 97d4a312 ba4759b7 a0144628 a5af1a38
        |3446c8da 1c855c13 bb0b3973 37f0ae03 a781fb53 aff80173 a9299f00 b5657e02
        |3586e841 3d1e9570 b48f7003 39d01ea0 baedd200 b957a5a0 9cb6d15b b553b992
        |bf3db400 3616f600 32968020 3780bc00 ad69e224 ae16f811 b40e09e0 35c0c890
        |2f826150 3e0e4700 b725a800 b5454800 36602200 b973a347 3c924900 ab4e13bc
        |3b10940a ad0ff2f4 2895b20c b7bb7e00 38441c00 ac366b54 ab02ec20 37b7da00
        |27f70801 33295399 b1c6a2a0 383e7c42 34f30cdb 33d28c02 a91918e9 3fb04210
        |bf6a6000 b8bd6000 b2150701 a310aa6c a1d43b93 3690f336 b290820d 32a35216
        |3ee88004 ba034000 ad893681 23544771 b9707a03 a3e4a704 25de38c2 aea62896
        |40444a00 3789e760 39d099c0 333b0205 36342000 36855a00 b93dc370 2b947c00
        |bfc2b800 b8ca7038 b964f7f5 34819c02 3b6ef4f6 b5ca5d00 21412dff 35b95d85
        |2f149a8e 1b7d2c19 a75e046a 262977f7 ab3f3761 a850ea05 b86d6e56 2df32a00
        |2a27dced aacd648b aeb13800 392ceeac 2ac31141 a2a41e87 32cfc4e9 2df66ba7
        |a45a4350 b784cbc4 381cbef2 34daf7d8 aac91800 37823200 245b5415 3bbb0b83
        |ab725800 ad1d5c64 b3ad9680 b2a7d638 2bb90189 a14296fc 39244000 2ca68025
        |""".stripMargin
      )
    ),
    Probe(
      "hmmaTf32Sm89",
      89,
      """.version 8.7
      |.target sm_89
      |.address_size 64
      |.visible .entry hmmaTf32Sm89(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<48>;
      |.reg .b64 %rd<8>;
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %tid.x;
      |mul.wide.u32 %rd2, %r0, 40;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |ld.global.b32 %r5, [%rd3+16];
      |ld.global.b32 %r6, [%rd3+20];
      |ld.global.b32 %r7, [%rd3+24];
      |ld.global.b32 %r8, [%rd3+28];
      |ld.global.b32 %r9, [%rd3+32];
      |ld.global.b32 %r10, [%rd3+36];
      |mma.sync.aligned.m16n8k8.row.col.f32.tf32.tf32.f32 {%r20,%r21,%r22,%r23}, {%r1,%r2,%r3,%r4}, {%r5,%r6}, {%r7,%r8,%r9,%r10};
      |mul.wide.u32 %rd4, %r0, 16;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |st.global.b32 [%rd5+8], %r22;
      |st.global.b32 [%rd5+12], %r23;
      |ret;
      |}
      |""".stripMargin,
      4,
      2,
      4,
      4,
      (0xbfffffffL, 0xbfffffffL, 0xbfffffffL),
      Some(
        """bdc8d620 1840068e 9e791b48 32ccf775 31037205 9897920f b3b64654 3406c46a
        |2fc54970 267986fa b6fa4a0b a387ba82 8cc551f3 2f03c8f1 1d8a94b0 0e4146e0
        |3447a74a 1c8317f7 bb0b6b24 31e05b55 a78289e7 aff80173 a929f368 aa1d92c0
        |3586e876 3d1f7820 a9e56d45 39d1cd5c baee5d00 b95b0600 9cb6d26e 2fa9953c
        |bf3e6ce0 36176700 a6e64256 1da3df2f ad6820c0 ae176912 b40e09df 34c10240
        |2f8b8fd0 3e0f4790 b7268100 25ad4054 3660a500 b9751d6c 3c935e90 27090bd8
        |3b0fd726 a9b09da8 2899a49b 1b06ac9f 2024323a ac366b54 0d18b903 29be58be
        |1c3ee4cd 3329b704 b3fa257f 383e7c45 34f30cdb a64f2411 a9191804 3f993c4e
        |bf6b70b8 b8be28a8 a6ea82f3 a3112436 a1d51d40 3690f335 32a5c96b 32a35216
        |3ee9181c ba039f10 ad8a0639 1fb13006 b97173c3 a3e86aaf 25de38c0 a7afb528
        |4045193c 37b55620 39e8fd88 333c2e18 1aa131e8 a1be4945 b2aee238 2b9497a0
        |bfc3226c b8b1b938 b966ab27 34824070 3b6ef4f6 b55397a0 20ccb5a0 2b32a418
        |2f149a8e 1b7f0349 a76df00d 8f322960 ab402959 a85105a0 30d50b76 1347a8a2
        |2a27ddbf aacd648b 1ee4e5a5 35d47a3d 2ac3d591 a2a3019f 32cfc4e9 9dc5e01c
        |1b1f63f4 b138925e 381cbe8e aba27526 1bf15a8a 11698245 245c9d2d 3bbc8283
        |1e879e94 a946ed6a b3adead0 acf7500f 2bb9018e 94c2d1c2 3924f250 2394dc64
        |""".stripMargin
      )
    ),
    Probe(
      "hmmaSparseF16Sm89",
      89,
      """.version 8.7
      |.target sm_89
      |.address_size 64
      |.visible .entry hmmaSparseF16Sm89(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<48>;
      |.reg .b64 %rd<8>;
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %tid.x;
      |mul.wide.u32 %rd2, %r0, 40;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |ld.global.b32 %r5, [%rd3+16];
      |ld.global.b32 %r6, [%rd3+20];
      |ld.global.b32 %r7, [%rd3+24];
      |ld.global.b32 %r8, [%rd3+28];
      |ld.global.b32 %r9, [%rd3+32];
      |ld.global.b32 %r10, [%rd3+36];
      |mov.b32 %r40, 0xe9c84d8e;
      |mma.sp::ordered_metadata.sync.aligned.m16n8k32.row.col.f16.f16.f16.f16 {%r20,%r21}, {%r1,%r2,%r3,%r4}, {%r5,%r6,%r7,%r8}, {%r9,%r10}, %r40, 0x1;
      |mul.wide.u32 %rd4, %r0, 8;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |ret;
      |}
      |""".stripMargin,
      4,
      4,
      2,
      2,
      (0xbfffbfffL, 0xbfffbfffL, 0xbfffbfffL),
      Some(
        """b9613848 318ab7d0 b3bb3da3 34cb2822 be169f6b bdf12c41 39eb33b1 3e2a2467
        |309f3a0a 3a653c28 290db640 a8d42c75 aa77374b bff2a51c afadb0f3 b3d229ce
        |bc223cba 3b013c19 b4bca9d2 34c5a095 3e433877 bf6f35c6 2827b3c0 b1263f6d
        |30fe2975 3ca8a913 3a47afb3 380b23ba b693a02f bec83c1f b02d9fdf 3fc43864
        |a66da5dd 33a9343d ae3aa649 32862527 b8a73aaa bd79362c 9953b8e7 be8833f3
        |bc2b3818 3722b09e 391aac57 b298b6b8 bd45afb6 bbffbcb3 29002e76 b0961efa
        |2f7333e1 3c99259d 303a917d bc9a2e1f 30cbb2fd 2fc02911 327ab742 2d4c3a58
        |381699fa 3879bc82 9d00b053 3b589d7d c00caaba c11e302f 2621bd50 b61fa434
        |""".stripMargin
      )
    ),
    Probe(
      "immaSparseS8Sm89",
      89,
      """.version 8.7
      |.target sm_89
      |.address_size 64
      |.visible .entry immaSparseS8Sm89(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<48>;
      |.reg .b64 %rd<8>;
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %tid.x;
      |mul.wide.u32 %rd2, %r0, 32;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |ld.global.b32 %r5, [%rd3+16];
      |ld.global.b32 %r6, [%rd3+20];
      |ld.global.b32 %r7, [%rd3+24];
      |ld.global.b32 %r8, [%rd3+28];
      |mov.b32 %r40, 0xe9c84d8e;
      |mma.sp::ordered_metadata.sync.aligned.m16n8k32.row.col.satfinite.s32.s8.s8.s32 {%r20,%r21,%r22,%r23}, {%r1,%r2}, {%r3,%r4}, {%r5,%r6,%r7,%r8}, %r40, 0x0;
      |mul.wide.u32 %rd4, %r0, 16;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |st.global.b32 [%rd5+8], %r22;
      |st.global.b32 [%rd5+12], %r23;
      |ret;
      |}
      |""".stripMargin,
      2,
      2,
      4,
      4,
      (0xffffffffL, 0xffffffffL, 0xffffffffL),
      Some(
        """cba1cdee 8020e3c3 8dceccab 10bf91b0 c89fb480 512aa0d5 3e5b3b2e bffaf92b
        |ddaae165 fac84479 8cbc12ff 9f21f552 f6faae4c e34e0b09 48810bac a3e90a9d
        |ccd971e5 2f06a293 04749f65 4e40a748 94ec670b 49ae3064 2fa1ee9b 8f417d55
        |a2a64a7b 00ea611e 98d17f96 f94a4689 48175efa a2d0b713 3c8b0a0c d75ba00b
        |e9e564c5 1fc86484 803fde95 35d863b6 098122a1 5d8a7027 9cb73a09 9245ba26
        |23a4c763 4223a165 aa9cb88b 04c7a847 e6f0af56 db18875b b18f91e7 8527fd17
        |909e9f78 32c08b2a b803242a fe2baae8 51225d71 4a4b7ca2 33737e51 db64f9cc
        |5ec5a29e ea9b4de4 dfa8e62b 9e129324 abb3ee85 92f7ed07 3b0fb4e9 e975c58f
        |93339478 c009d575 8ffea206 db573aeb 9f1ca832 452c7d8c 5f924463 0ca0ccd7
        |b3fa6e14 783e8407 c5337290 4bd046f1 34f34c24 65f0a84c a9194693 3f9976e7
        |36ec3688 a9086925 e5d69646 4dbf9ea9 7ef05514 07b51d33 b0578b2c 3f78cbd1
        |0627f935 39b67499 7b0cf61e 818c4b06 e581ae3a 42f0ff6d 7ac7018c 205a3cf7
        |f571cfa4 d6cbc460 65de8c69 d4a4469a afa86caf bfff8a0f 0a16e998 f993c34a
        |bfc98004 b96ddcec 5a8c07fb fa8c6431 56e6832e f9c0978a c182953a e26fc30e
        |07676e7c 1c46ab51 8096e822 f82c9d2f 3b6eecc5 1537170f 489c6a65 d60701fa
        |cf7d58a6 00e8f742 2f14549b daa2b38d c28e4417 00e07936 04c0231c 431aae98
        |""".stripMargin
      )
    ),
    Probe(
      "qmmaSparseE4m3Sm89",
      89,
      """.version 8.7
      |.target sm_89
      |.address_size 64
      |.visible .entry qmmaSparseE4m3Sm89(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<48>;
      |.reg .b64 %rd<8>;
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %tid.x;
      |mul.wide.u32 %rd2, %r0, 48;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |ld.global.b32 %r5, [%rd3+16];
      |ld.global.b32 %r6, [%rd3+20];
      |ld.global.b32 %r7, [%rd3+24];
      |ld.global.b32 %r8, [%rd3+28];
      |ld.global.b32 %r9, [%rd3+32];
      |ld.global.b32 %r10, [%rd3+36];
      |ld.global.b32 %r11, [%rd3+40];
      |ld.global.b32 %r12, [%rd3+44];
      |mov.b32 %r40, 0xe9c84d8e;
      |mma.sp::ordered_metadata.sync.aligned.m16n8k64.row.col.f32.e4m3.e4m3.f32 {%r20,%r21,%r22,%r23}, {%r1,%r2,%r3,%r4}, {%r5,%r6,%r7,%r8}, {%r9,%r10,%r11,%r12}, %r40, 0x0;
      |mul.wide.u32 %rd4, %r0, 16;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |st.global.b32 [%rd5+8], %r22;
      |st.global.b32 [%rd5+12], %r23;
      |ret;
      |}
      |""".stripMargin,
      4,
      4,
      4,
      4,
      (0xbfbfbfbfL, 0xbfbfbfbfL, 0xbfffffffL),
      Some(
        """bfb31000 bf899400 c0838800 3f362c00 c0019800 3fdcd800 3f36d000 3f4c9800
        |bfc96c00 c000b400 bfbb2400 bf14c800 bfcc2400 4079d000 bfd25000 c014ec00
        |3ea47800 bfa14000 c052fc00 c07a7400 be487000 3ed3a800 3fb96c00 3f071800
        |3fa39800 3d71c000 3e0d7c00 bd690000 40364400 3f88f800 bea85400 3f284000
        |c01bd000 3f67b800 bf96b800 bf956800 402cdc00 3f83d800 3f83c000 4007b400
        |bf30d800 bf0b4c00 bf763c00 be83c400 3fb86800 c0822000 3d63a000 3f02a400
        |40778000 3ff1d800 3e538000 c030c000 3ff78c00 3e1d4800 3f016000 bf67e400
        |c078d800 bf782000 40674c00 403f9000 bfaa4400 bfd1b400 401b1000 3fa54800
        |4004ac00 3dfae000 40ce9000 3f28c400 40015000 bfe1e800 3fff6400 41011000
        |3f11b000 3f55a400 4042c400 bde19000 be2dc800 3f842800 bf009000 40e89000
        |bfbfd800 3e925800 bf462c00 bfb24400 3fbfd400 400ec400 bfb4c000 3f6ca800
        |bee53800 3cc20000 bea16000 bf35cc00 c0679c00 c011fc00 3f84c400 410a0400
        |3e31e000 3d583000 3fbbb400 3f02cc00 3d534000 3f9ebc00 3e863000 bef38000
        |3cf30000 bfc44400 3facb800 401ce800 be61d000 3e274800 bdb7c000 bf47c800
        |40398c00 3e86f000 bf880800 bf1c0400 409fa400 3e651000 3f53c800 3f82f400
        |3fa35800 3fd28400 bf84f400 bea51c00 3f089c00 c00a4c00 bf6f9800 3f83ac00
        |""".stripMargin
      )
    ),
    Probe(
      "hmmaSparseTf32Sm89",
      89,
      """.version 8.7
      |.target sm_89
      |.address_size 64
      |.visible .entry hmmaSparseTf32Sm89(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<48>;
      |.reg .b64 %rd<8>;
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %tid.x;
      |mul.wide.u32 %rd2, %r0, 48;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |ld.global.b32 %r5, [%rd3+16];
      |ld.global.b32 %r6, [%rd3+20];
      |ld.global.b32 %r7, [%rd3+24];
      |ld.global.b32 %r8, [%rd3+28];
      |ld.global.b32 %r9, [%rd3+32];
      |ld.global.b32 %r10, [%rd3+36];
      |ld.global.b32 %r11, [%rd3+40];
      |ld.global.b32 %r12, [%rd3+44];
      |mov.b32 %r40, 0xe4e44e4e;
      |mma.sp.sync.aligned.m16n8k16.row.col.f32.tf32.tf32.f32 {%r20,%r21,%r22,%r23}, {%r1,%r2,%r3,%r4}, {%r5,%r6,%r7,%r8}, {%r9,%r10,%r11,%r12}, %r40, 0x1;
      |mul.wide.u32 %rd4, %r0, 16;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |st.global.b32 [%rd5+8], %r22;
      |st.global.b32 [%rd5+12], %r23;
      |ret;
      |}
      |""".stripMargin,
      4,
      4,
      4,
      4,
      (0xbfffffffL, 0xbfffffffL, 0xbfffffffL),
      Some(
        """198ed2da 32ccf775 bed6fa24 2e3dfe64 39f63a60 bac87b1c 9be10a28 28022cd7
        |1a873dd6 33cf5de8 96075c44 3f8e2cb5 9a54314f 2205c46f 2fa1b5e8 ae794854
        |1b1dceae aff80167 9ce40ffb 3032e140 381c1a80 ad6ea3de 3517b8cb 35ccea3b
        |399ccb03 32037761 30690b6e 1352e5bc a6f09bfe b17834c0 b18f9f2e 9eb6fc36
        |3868ee1c ac922b74 28468a28 a21aefd8 2f08cd18 aa9b224f ad458e36 9d9d4772
        |2899a495 28e4931c 1ad798d1 245fe900 9f1c419b 30176e00 1f91c506 1a566f78
        |31f28f09 1ee0bb98 bcbeceed 2ddc45ae 36ebdcec 3b081628 2227c600 32b08e00
        |9e02ccb0 3690f38b a8d0d627 32a35224 a5818074 16b34962 3ac692a8 2e18272c
        |3587535a ab646c93 ba09cf44 27dfca10 bfc935f2 b96dc9cc a27fd479 ba8c597f
        |928499d2 0ec12b4a 370fcd3c bae77b21 3b6ef4f6 96c6cd39 2a08fe78 372bdf74
        |a76defc2 240a32e4 9d069e0f 24a295a1 b2535378 32c98fe8 b123e2d2 b6a33b50
        |9e7c8502 118ac8db 9bb95600 a4f7d6a0 bf485f1b 27ed2e97 b02ffc60 b138925e
        |25558aae a179aad0 37efb7a0 3bbc8283 b7d20540 2dbcf308 32e81e16 3820d000
        |ace87277 2072c9ba 2b21c38c 3a522fee 24361a58 1dc79c7d 21442b66 2b3564f1
        |b145d457 212bd6d8 b1a9f8c5 b8e2f664 3fc09d0f 393dd436 31936339 37117a1c
        |2cc49170 a9d50214 345def68 9eacced7 26123f60 af5f11eb 309cd416 ae2bdf73
        |""".stripMargin
      )
    ),
    Probe(
      "movmSm89",
      89,
      """.version 8.7
      |.target sm_89
      |.address_size 64
      |.visible .entry movmSm89(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<48>;
      |.reg .b64 %rd<8>;
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %tid.x;
      |mul.wide.u32 %rd2, %r0, 4;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |movmatrix.sync.aligned.m8n8.trans.b16 %r20, %r1;
      |mul.wide.u32 %rd4, %r0, 4;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |ret;
      |}
      |""".stripMargin,
      1,
      0,
      0,
      1,
      (0xffffffffL, 0xffffffffL, 0xffffffffL),
      Some(
        """76b46fac 7af94a52 72d76a34 4aa3247a cba26c57 c89fd6f8 ddaa6cae f6fa631f
        |81e686b3 2640f775 7b1b0c3f 2f41af61 80218268 512a32cc fac89278 e34ec9cc
        |b534e199 597483f6 97bcd9a9 cf3f1e49 8dcea5fa 3e5bbeea 8cbc9f64 488096f6
        |51ed55cd e01969e4 b6c5c46a 4a243d41 10bf6203 bffa8927 9f213406 a3e90e0f
        |""".stripMargin
      )
    ),
    Probe(
      "dmma16816Sm90",
      90,
      """.version 9.0
      |.target sm_90
      |.address_size 64
      |.visible .entry dmma16816Sm90(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<64>;
      |.reg .b64 %rd<16>;
      |.reg .f64 %fd<32>;
      |.shared .align 16 .b8 tile[2048];
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %laneid;
      |mul.wide.u32 %rd2, %r0, 128;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |ld.global.b32 %r5, [%rd3+16];
      |ld.global.b32 %r6, [%rd3+20];
      |ld.global.b32 %r7, [%rd3+24];
      |ld.global.b32 %r8, [%rd3+28];
      |ld.global.b32 %r9, [%rd3+32];
      |ld.global.b32 %r10, [%rd3+36];
      |ld.global.b32 %r11, [%rd3+40];
      |ld.global.b32 %r12, [%rd3+44];
      |ld.global.b32 %r13, [%rd3+48];
      |ld.global.b32 %r14, [%rd3+52];
      |ld.global.b32 %r15, [%rd3+56];
      |ld.global.b32 %r16, [%rd3+60];
      |ld.global.b32 %r17, [%rd3+64];
      |ld.global.b32 %r18, [%rd3+68];
      |ld.global.b32 %r19, [%rd3+72];
      |ld.global.b32 %r20, [%rd3+76];
      |ld.global.b32 %r21, [%rd3+80];
      |ld.global.b32 %r22, [%rd3+84];
      |ld.global.b32 %r23, [%rd3+88];
      |ld.global.b32 %r24, [%rd3+92];
      |ld.global.b32 %r25, [%rd3+96];
      |ld.global.b32 %r26, [%rd3+100];
      |ld.global.b32 %r27, [%rd3+104];
      |ld.global.b32 %r28, [%rd3+108];
      |ld.global.b32 %r29, [%rd3+112];
      |ld.global.b32 %r30, [%rd3+116];
      |ld.global.b32 %r31, [%rd3+120];
      |ld.global.b32 %r32, [%rd3+124];
      |mov.b64 %fd0, {%r1, %r2};
      |mov.b64 %fd1, {%r3, %r4};
      |mov.b64 %fd2, {%r5, %r6};
      |mov.b64 %fd3, {%r7, %r8};
      |mov.b64 %fd4, {%r9, %r10};
      |mov.b64 %fd5, {%r11, %r12};
      |mov.b64 %fd6, {%r13, %r14};
      |mov.b64 %fd7, {%r15, %r16};
      |mov.b64 %fd8, {%r17, %r18};
      |mov.b64 %fd9, {%r19, %r20};
      |mov.b64 %fd10, {%r21, %r22};
      |mov.b64 %fd11, {%r23, %r24};
      |mov.b64 %fd12, {%r25, %r26};
      |mov.b64 %fd13, {%r27, %r28};
      |mov.b64 %fd14, {%r29, %r30};
      |mov.b64 %fd15, {%r31, %r32};
      |mma.sync.aligned.m16n8k16.row.col.f64.f64.f64.f64 {%fd16,%fd17,%fd18,%fd19}, {%fd0,%fd1,%fd2,%fd3,%fd4,%fd5,%fd6,%fd7}, {%fd8,%fd9,%fd10,%fd11}, {%fd12,%fd13,%fd14,%fd15};
      |mov.b64 {%r20, %r21}, %fd16;
      |mov.b64 {%r22, %r23}, %fd17;
      |mov.b64 {%r24, %r25}, %fd18;
      |mov.b64 {%r26, %r27}, %fd19;
      |mul.wide.u32 %rd4, %r0, 32;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |st.global.b32 [%rd5+8], %r22;
      |st.global.b32 [%rd5+12], %r23;
      |st.global.b32 [%rd5+16], %r24;
      |st.global.b32 [%rd5+20], %r25;
      |st.global.b32 [%rd5+24], %r26;
      |st.global.b32 [%rd5+28], %r27;
      |ret;
      |}
      |""".stripMargin,
      16,
      8,
      8,
      8,
      (0xffffffffL, 0xffffffffL, 0xffffffffL),
      None
    ),
    Probe(
      "hmmaE4m3Sm90",
      90,
      """.version 9.0
      |.target sm_90
      |.address_size 64
      |.visible .entry hmmaE4m3Sm90(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<64>;
      |.reg .b64 %rd<16>;
      |.reg .f64 %fd<32>;
      |.shared .align 16 .b8 tile[2048];
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %laneid;
      |mul.wide.u32 %rd2, %r0, 40;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |ld.global.b32 %r5, [%rd3+16];
      |ld.global.b32 %r6, [%rd3+20];
      |ld.global.b32 %r7, [%rd3+24];
      |ld.global.b32 %r8, [%rd3+28];
      |ld.global.b32 %r9, [%rd3+32];
      |ld.global.b32 %r10, [%rd3+36];
      |mma.sync.aligned.m16n8k32.row.col.f32.e4m3.e4m3.f32 {%r20,%r21,%r22,%r23}, {%r1,%r2,%r3,%r4}, {%r5,%r6}, {%r7,%r8,%r9,%r10};
      |mul.wide.u32 %rd4, %r0, 16;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |st.global.b32 [%rd5+8], %r22;
      |st.global.b32 [%rd5+12], %r23;
      |ret;
      |}
      |""".stripMargin,
      4,
      2,
      4,
      4,
      (0xbfbfbfbfL, 0xbfbfbfbfL, 0xbfffffffL),
      None
    ),
    Probe(
      "immaS4SatSm90",
      90,
      """.version 9.0
      |.target sm_90
      |.address_size 64
      |.visible .entry immaS4SatSm90(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<64>;
      |.reg .b64 %rd<16>;
      |.reg .f64 %fd<32>;
      |.shared .align 16 .b8 tile[2048];
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %laneid;
      |mul.wide.u32 %rd2, %r0, 16;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |mma.sync.aligned.m8n8k32.row.col.satfinite.s32.s4.s4.s32 {%r20,%r21}, {%r1}, {%r2}, {%r3,%r4};
      |mul.wide.u32 %rd4, %r0, 8;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |ret;
      |}
      |""".stripMargin,
      1,
      1,
      2,
      2,
      (0xffffffffL, 0xffffffffL, 0xffffffffL),
      None
    ),
    Probe(
      "bmmaXorSm100",
      100,
      """.version 9.0
      |.target sm_100
      |.address_size 64
      |.visible .entry bmmaXorSm100(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<64>;
      |.reg .b64 %rd<16>;
      |.reg .f64 %fd<32>;
      |.shared .align 16 .b8 tile[2048];
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %laneid;
      |mul.wide.u32 %rd2, %r0, 16;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |mma.sync.aligned.m8n8k128.row.col.s32.b1.b1.s32.xor.popc {%r20,%r21}, {%r1}, {%r2}, {%r3,%r4};
      |mul.wide.u32 %rd4, %r0, 8;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |ret;
      |}
      |""".stripMargin,
      1,
      1,
      2,
      2,
      (0xffffffffL, 0xffffffffL, 0xffffffffL),
      None
    ),
    Probe(
      "bmmaAndSm100",
      100,
      """.version 9.0
      |.target sm_100
      |.address_size 64
      |.visible .entry bmmaAndSm100(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<64>;
      |.reg .b64 %rd<16>;
      |.reg .f64 %fd<32>;
      |.shared .align 16 .b8 tile[2048];
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %laneid;
      |mul.wide.u32 %rd2, %r0, 40;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |ld.global.b32 %r5, [%rd3+16];
      |ld.global.b32 %r6, [%rd3+20];
      |ld.global.b32 %r7, [%rd3+24];
      |ld.global.b32 %r8, [%rd3+28];
      |ld.global.b32 %r9, [%rd3+32];
      |ld.global.b32 %r10, [%rd3+36];
      |mma.sync.aligned.m16n8k256.row.col.s32.b1.b1.s32.and.popc {%r20,%r21,%r22,%r23}, {%r1,%r2,%r3,%r4}, {%r5,%r6}, {%r7,%r8,%r9,%r10};
      |mul.wide.u32 %rd4, %r0, 16;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |st.global.b32 [%rd5+8], %r22;
      |st.global.b32 [%rd5+12], %r23;
      |ret;
      |}
      |""".stripMargin,
      4,
      2,
      4,
      4,
      (0xffffffffL, 0xffffffffL, 0xffffffffL),
      None
    ),
    Probe(
      "immaU4SatSm120",
      120,
      """.version 9.0
      |.target sm_120
      |.address_size 64
      |.visible .entry immaU4SatSm120(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<64>;
      |.reg .b64 %rd<16>;
      |.reg .f64 %fd<32>;
      |.shared .align 16 .b8 tile[2048];
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %laneid;
      |mul.wide.u32 %rd2, %r0, 16;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |mma.sync.aligned.m8n8k32.row.col.satfinite.s32.u4.u4.s32 {%r20,%r21}, {%r1}, {%r2}, {%r3,%r4};
      |mul.wide.u32 %rd4, %r0, 8;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |ret;
      |}
      |""".stripMargin,
      1,
      1,
      2,
      2,
      (0xffffffffL, 0xffffffffL, 0xffffffffL),
      None
    ),
    Probe(
      "qmmaE4m3E5m2Sm120",
      120,
      """.version 9.0
      |.target sm_120
      |.address_size 64
      |.visible .entry qmmaE4m3E5m2Sm120(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<64>;
      |.reg .b64 %rd<16>;
      |.reg .f64 %fd<32>;
      |.shared .align 16 .b8 tile[2048];
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %laneid;
      |mul.wide.u32 %rd2, %r0, 40;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |ld.global.b32 %r5, [%rd3+16];
      |ld.global.b32 %r6, [%rd3+20];
      |ld.global.b32 %r7, [%rd3+24];
      |ld.global.b32 %r8, [%rd3+28];
      |ld.global.b32 %r9, [%rd3+32];
      |ld.global.b32 %r10, [%rd3+36];
      |mma.sync.aligned.m16n8k32.row.col.f32.e4m3.e5m2.f32 {%r20,%r21,%r22,%r23}, {%r1,%r2,%r3,%r4}, {%r5,%r6}, {%r7,%r8,%r9,%r10};
      |mul.wide.u32 %rd4, %r0, 16;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |st.global.b32 [%rd5+8], %r22;
      |st.global.b32 [%rd5+12], %r23;
      |ret;
      |}
      |""".stripMargin,
      4,
      2,
      4,
      4,
      (0xbfbfbfbfL, 0xbfbfbfbfL, 0xbfffffffL),
      None
    ),
    Probe(
      "qmmaKindE2m1E3m2Sm120a",
      120,
      """.version 9.0
      |.target sm_120a
      |.address_size 64
      |.visible .entry qmmaKindE2m1E3m2Sm120a(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<64>;
      |.reg .b64 %rd<16>;
      |.reg .f64 %fd<32>;
      |.shared .align 16 .b8 tile[2048];
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %laneid;
      |mul.wide.u32 %rd2, %r0, 32;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |ld.global.b32 %r5, [%rd3+16];
      |ld.global.b32 %r6, [%rd3+20];
      |ld.global.b32 %r7, [%rd3+24];
      |ld.global.b32 %r8, [%rd3+28];
      |mma.sync.aligned.m16n8k32.row.col.kind::f8f6f4.f16.e2m1.e3m2.f16 {%r20,%r21}, {%r1,%r2,%r3,%r4}, {%r5,%r6}, {%r7,%r8};
      |mul.wide.u32 %rd4, %r0, 8;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |ret;
      |}
      |""".stripMargin,
      4,
      2,
      2,
      2,
      (0xffffffffL, 0xffffffffL, 0xbfffbfffL),
      None
    ),
    Probe(
      "qmmaScaleSm120a",
      120,
      """.version 9.0
      |.target sm_120a
      |.address_size 64
      |.visible .entry qmmaScaleSm120a(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<64>;
      |.reg .b64 %rd<16>;
      |.reg .f64 %fd<32>;
      |.shared .align 16 .b8 tile[2048];
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %laneid;
      |mul.wide.u32 %rd2, %r0, 48;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |ld.global.b32 %r5, [%rd3+16];
      |ld.global.b32 %r6, [%rd3+20];
      |ld.global.b32 %r7, [%rd3+24];
      |ld.global.b32 %r8, [%rd3+28];
      |ld.global.b32 %r9, [%rd3+32];
      |ld.global.b32 %r10, [%rd3+36];
      |ld.global.b32 %r11, [%rd3+40];
      |ld.global.b32 %r12, [%rd3+44];
      |mma.sync.aligned.m16n8k32.row.col.kind::mxf8f6f4.block_scale.scale_vec::1X.f32.e4m3.e2m3.f32.ue8m0 {%r20,%r21,%r22,%r23}, {%r1,%r2,%r3,%r4}, {%r5,%r6}, {%r7,%r8,%r9,%r10}, %r11, {1, 1}, %r12, {3, 2};
      |mul.wide.u32 %rd4, %r0, 16;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |st.global.b32 [%rd5+8], %r22;
      |st.global.b32 [%rd5+12], %r23;
      |ret;
      |}
      |""".stripMargin,
      4,
      2,
      6,
      4,
      (0xffffffffL, 0xffffffffL, 0xbfffffffL),
      None
    ),
    Probe(
      "ommaNvf4Sm120a",
      120,
      """.version 9.0
      |.target sm_120a
      |.address_size 64
      |.visible .entry ommaNvf4Sm120a(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<64>;
      |.reg .b64 %rd<16>;
      |.reg .f64 %fd<32>;
      |.shared .align 16 .b8 tile[2048];
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %laneid;
      |mul.wide.u32 %rd2, %r0, 48;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |ld.global.b32 %r5, [%rd3+16];
      |ld.global.b32 %r6, [%rd3+20];
      |ld.global.b32 %r7, [%rd3+24];
      |ld.global.b32 %r8, [%rd3+28];
      |ld.global.b32 %r9, [%rd3+32];
      |ld.global.b32 %r10, [%rd3+36];
      |ld.global.b32 %r11, [%rd3+40];
      |ld.global.b32 %r12, [%rd3+44];
      |mma.sync.aligned.m16n8k64.row.col.kind::mxf4nvf4.block_scale.scale_vec::4X.f32.e2m1.e2m1.f32.ue4m3 {%r20,%r21,%r22,%r23}, {%r1,%r2,%r3,%r4}, {%r5,%r6}, {%r7,%r8,%r9,%r10}, %r11, {0, 1}, %r12, {0, 3};
      |mul.wide.u32 %rd4, %r0, 16;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |st.global.b32 [%rd5+8], %r22;
      |st.global.b32 [%rd5+12], %r23;
      |ret;
      |}
      |""".stripMargin,
      4,
      2,
      6,
      4,
      (0xffffffffL, 0xffffffffL, 0xbfffffffL),
      None
    ),
    Probe(
      "ommaMxf4Sm120a",
      120,
      """.version 9.0
      |.target sm_120a
      |.address_size 64
      |.visible .entry ommaMxf4Sm120a(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<64>;
      |.reg .b64 %rd<16>;
      |.reg .f64 %fd<32>;
      |.shared .align 16 .b8 tile[2048];
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %laneid;
      |mul.wide.u32 %rd2, %r0, 48;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |ld.global.b32 %r5, [%rd3+16];
      |ld.global.b32 %r6, [%rd3+20];
      |ld.global.b32 %r7, [%rd3+24];
      |ld.global.b32 %r8, [%rd3+28];
      |ld.global.b32 %r9, [%rd3+32];
      |ld.global.b32 %r10, [%rd3+36];
      |ld.global.b32 %r11, [%rd3+40];
      |ld.global.b32 %r12, [%rd3+44];
      |mma.sync.aligned.m16n8k64.row.col.kind::mxf4.block_scale.scale_vec::2X.f32.e2m1.e2m1.f32.ue8m0 {%r20,%r21,%r22,%r23}, {%r1,%r2,%r3,%r4}, {%r5,%r6}, {%r7,%r8,%r9,%r10}, %r11, {2, 0}, %r12, {0, 1};
      |mul.wide.u32 %rd4, %r0, 16;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |st.global.b32 [%rd5+8], %r22;
      |st.global.b32 [%rd5+12], %r23;
      |ret;
      |}
      |""".stripMargin,
      4,
      2,
      6,
      4,
      (0xffffffffL, 0xffffffffL, 0xbfffffffL),
      None
    ),
    Probe(
      "qmmaSparseScaleSm120a",
      120,
      """.version 9.0
      |.target sm_120a
      |.address_size 64
      |.visible .entry qmmaSparseScaleSm120a(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<64>;
      |.reg .b64 %rd<16>;
      |.reg .f64 %fd<32>;
      |.shared .align 16 .b8 tile[2048];
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %laneid;
      |mul.wide.u32 %rd2, %r0, 56;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |ld.global.b32 %r5, [%rd3+16];
      |ld.global.b32 %r6, [%rd3+20];
      |ld.global.b32 %r7, [%rd3+24];
      |ld.global.b32 %r8, [%rd3+28];
      |ld.global.b32 %r9, [%rd3+32];
      |ld.global.b32 %r10, [%rd3+36];
      |ld.global.b32 %r11, [%rd3+40];
      |ld.global.b32 %r12, [%rd3+44];
      |ld.global.b32 %r13, [%rd3+48];
      |ld.global.b32 %r14, [%rd3+52];
      |mov.b32 %r40, 0xe9c84d8e;
      |mma.sp::ordered_metadata.sync.aligned.m16n8k64.row.col.kind::mxf8f6f4.block_scale.scale_vec::1X.f32.e3m2.e2m1.f32.ue8m0 {%r20,%r21,%r22,%r23}, {%r1,%r2,%r3,%r4}, {%r5,%r6,%r7,%r8}, {%r9,%r10,%r11,%r12}, %r40, 0x0, %r13, {0, 1}, %r14, {2, 1};
      |mul.wide.u32 %rd4, %r0, 16;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |st.global.b32 [%rd5+8], %r22;
      |st.global.b32 [%rd5+12], %r23;
      |ret;
      |}
      |""".stripMargin,
      4,
      4,
      6,
      4,
      (0xffffffffL, 0xffffffffL, 0xbfffffffL),
      None
    ),
    Probe(
      "ldsmU4Sm100a",
      100,
      """.version 9.0
      |.target sm_100a
      |.address_size 64
      |.visible .entry ldsmU4Sm100a(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<64>;
      |.reg .b64 %rd<16>;
      |.reg .f64 %fd<32>;
      |.shared .align 16 .b8 tile[2048];
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %laneid;
      |mul.wide.u32 %rd2, %r0, 16;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |mul.wide.u32 %rd6, %r0, 8;
      |mov.u64 %rd7, tile;
      |add.u64 %rd8, %rd7, %rd6;
      |st.shared.v2.b32 [%rd8], {%r1, %r2};
      |st.shared.v2.b32 [%rd8+256], {%r3, %r4};
      |bar.warp.sync 0xffffffff;
      |mul.wide.u32 %rd9, %r0, 16;
      |add.u64 %rd10, %rd7, %rd9;
      |ldmatrix.sync.aligned.m16n16.x2.trans.shared.b8x16.b4x16_p64 {%r20,%r21,%r22,%r23}, [%rd10];
      |mul.wide.u32 %rd4, %r0, 16;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |st.global.b32 [%rd5+8], %r22;
      |st.global.b32 [%rd5+12], %r23;
      |ret;
      |}
      |""".stripMargin,
      4,
      0,
      0,
      4,
      (0xffffffffL, 0xffffffffL, 0xffffffffL),
      None
    ),
    Probe(
      "ldsmU6Sm120a",
      120,
      """.version 9.0
      |.target sm_120a
      |.address_size 64
      |.visible .entry ldsmU6Sm120a(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<64>;
      |.reg .b64 %rd<16>;
      |.reg .f64 %fd<32>;
      |.shared .align 16 .b8 tile[2048];
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %laneid;
      |mul.wide.u32 %rd2, %r0, 16;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |mul.wide.u32 %rd6, %r0, 8;
      |mov.u64 %rd7, tile;
      |add.u64 %rd8, %rd7, %rd6;
      |st.shared.v2.b32 [%rd8], {%r1, %r2};
      |st.shared.v2.b32 [%rd8+256], {%r3, %r4};
      |bar.warp.sync 0xffffffff;
      |mul.wide.u32 %rd9, %r0, 16;
      |add.u64 %rd10, %rd7, %rd9;
      |ldmatrix.sync.aligned.m8n16.x4.shared.b8x16.b6x16_p32 {%r20,%r21,%r22,%r23}, [%rd10];
      |mul.wide.u32 %rd4, %r0, 16;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |st.global.b32 [%rd5+8], %r22;
      |st.global.b32 [%rd5+12], %r23;
      |ret;
      |}
      |""".stripMargin,
      4,
      0,
      0,
      4,
      (0xffffffffL, 0xffffffffL, 0xffffffffL),
      None
    ),
    Probe(
      "stsmB8Sm120a",
      120,
      """.version 9.0
      |.target sm_120a
      |.address_size 64
      |.visible .entry stsmB8Sm120a(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<64>;
      |.reg .b64 %rd<16>;
      |.reg .f64 %fd<32>;
      |.shared .align 16 .b8 tile[2048];
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %laneid;
      |mul.wide.u32 %rd2, %r0, 8;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |mov.u64 %rd7, tile;
      |mul.wide.u32 %rd9, %r0, 16;
      |add.u64 %rd10, %rd7, %rd9;
      |stmatrix.sync.aligned.m16n8.x2.trans.shared.b8 [%rd10], {%r1, %r2};
      |bar.warp.sync 0xffffffff;
      |mul.wide.u32 %rd6, %r0, 8;
      |add.u64 %rd8, %rd7, %rd6;
      |ld.shared.v2.b32 {%r20, %r21}, [%rd8];
      |mul.wide.u32 %rd4, %r0, 8;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |ret;
      |}
      |""".stripMargin,
      2,
      0,
      0,
      2,
      (0xffffffffL, 0xffffffffL, 0xffffffffL),
      None
    ),
    Probe(
      "stsmB16Sm90",
      90,
      """.version 9.0
      |.target sm_90
      |.address_size 64
      |.visible .entry stsmB16Sm90(.param .u64 out, .param .u64 in) {
      |.reg .b32 %r<64>;
      |.reg .b64 %rd<16>;
      |.reg .f64 %fd<32>;
      |.shared .align 16 .b8 tile[2048];
      |ld.param.u64 %rd0, [out];
      |ld.param.u64 %rd1, [in];
      |mov.u32 %r0, %laneid;
      |mul.wide.u32 %rd2, %r0, 16;
      |add.u64 %rd3, %rd1, %rd2;
      |ld.global.b32 %r1, [%rd3+0];
      |ld.global.b32 %r2, [%rd3+4];
      |ld.global.b32 %r3, [%rd3+8];
      |ld.global.b32 %r4, [%rd3+12];
      |mov.u64 %rd7, tile;
      |mul.wide.u32 %rd9, %r0, 16;
      |add.u64 %rd10, %rd7, %rd9;
      |stmatrix.sync.aligned.m8n8.x4.trans.shared.b16 [%rd10], {%r1, %r2, %r3, %r4};
      |bar.warp.sync 0xffffffff;
      |mul.wide.u32 %rd6, %r0, 16;
      |add.u64 %rd8, %rd7, %rd6;
      |ld.shared.v4.b32 {%r20, %r21, %r22, %r23}, [%rd8];
      |mul.wide.u32 %rd4, %r0, 16;
      |add.u64 %rd5, %rd0, %rd4;
      |st.global.b32 [%rd5+0], %r20;
      |st.global.b32 [%rd5+4], %r21;
      |st.global.b32 [%rd5+8], %r22;
      |st.global.b32 [%rd5+12], %r23;
      |ret;
      |}
      |""".stripMargin,
      4,
      0,
      0,
      4,
      (0xffffffffL, 0xffffffffL, 0xffffffffL),
      None
    )
  )
}
