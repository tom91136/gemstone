package net.kurobako.gemstone.isa.nvidia.fixtures

/** PTX for mbarrier transactions, bulk and tensor copies, cluster shared memory and cluster launch control, run by the
  * PTX suites and, through the ptxas listings of each, by the SASS suites.
  */
object AsyncCases {
  val bulkPtx: String =
    """.version 8.6
      |.target sm_90
      |.address_size 64
      |.visible .entry bulk(.param .u64 data) {
      | .shared .align 128 .b8 a[256];
      | .shared .align 128 .b8 b[256];
      | .shared .align 8 .b64 bar;
      | .reg .u64 %data, %dst, %token, %offset;
      | .reg .u32 %r, %thread, %addr, %pending;
      | .reg .pred %p, %leader;
      | ld.param.u64 %data, [data];
      | mov.u32 %thread, %tid.x;
      | setp.eq.u32 %leader, %thread, 0;
      | @%leader mbarrier.init.shared::cta.b64 [bar], 1;
      | fence.mbarrier_init.release.cluster;
      | fence.proxy.async.shared::cta;
      | bar.sync 0;
      | @!%leader bra LOADED;
      | cp.async.bulk.prefetch.L2.global [%data], 256;
      | cp.async.bulk.shared::cluster.global.mbarrier::complete_tx::bytes [a], [%data], 256, [bar];
      | mbarrier.arrive.expect_tx.release.cta.shared::cta.b64 %token, [bar], 256;
      |LOADED:
      | mbarrier.try_wait.parity.acquire.cta.shared::cta.b64 %p, [bar], 0, 1000;
      | @!%p bra LOADED;
      | mov.u32 %addr, a;
      | mad.lo.u32 %addr, %thread, 4, %addr;
      | ld.shared.u32 %r, [%addr];
      | add.u32 %r, %r, 1;
      | st.shared.u32 [%addr], %r;
      | fence.proxy.async.shared::cta;
      | bar.sync 0;
      | @!%leader bra COPIED;
      | add.u64 %dst, %data, 256;
      | cp.async.bulk.global.shared::cta.bulk_group [%dst], [a], 256;
      | add.u64 %dst, %data, 512;
      | cp.reduce.async.bulk.global.shared::cta.bulk_group.add.u32 [%dst], [a], 256;
      | add.u64 %dst, %data, 768;
      | cp.reduce.async.bulk.global.shared::cta.bulk_group.min.s32 [%dst], [a], 256;
      | cp.async.bulk.commit_group;
      | cp.async.bulk.wait_group.read 0;
      | mbarrier.expect_tx.relaxed.cta.shared::cta.b64 [bar], 256;
      | cp.async.bulk.shared::cluster.shared::cta.mbarrier::complete_tx::bytes [b], [a], 256, [bar];
      | mbarrier.arrive.shared::cta.b64 %token, [bar];
      | mbarrier.pending_count.b64 %pending, %token;
      | st.global.u32 [%data+1280], %pending;
      | cp.async.bulk.wait_group 0;
      |COPIED:
      | mbarrier.try_wait.parity.shared::cta.b64 %p, [bar], 1;
      | @!%p bra COPIED;
      | mov.u32 %addr, b;
      | mad.lo.u32 %addr, %thread, 4, %addr;
      | ld.shared.u32 %r, [%addr];
      | mul.wide.u32 %offset, %thread, 4;
      | add.u64 %dst, %data, %offset;
      | st.global.u32 [%dst+1024], %r;
      | ret;
      |}
      |""".stripMargin

  val tilePtx: String =
    """.version 8.6
      |.target sm_90
      |.address_size 64
      |.visible .entry tile(
      | .param .align 64 .b8 load[128],
      | .param .align 64 .b8 store[128],
      | .param .align 64 .b8 reduce[128],
      | .param .u64 out,
      | .param .u32 x,
      | .param .u32 y
      |) {
      | .shared .align 1024 .b8 box[1024];
      | .shared .align 8 .b64 bar;
      | .reg .u64 %map, %out, %offset, %token;
      | .reg .u32 %thread, %x, %y, %addr, %r;
      | .reg .pred %p, %leader;
      | ld.param.u64 %out, [out];
      | ld.param.u32 %x, [x];
      | ld.param.u32 %y, [y];
      | mov.u32 %thread, %tid.x;
      | setp.eq.u32 %leader, %thread, 0;
      | @%leader mbarrier.init.shared::cta.b64 [bar], 1;
      | bar.sync 0;
      | @!%leader bra WAIT;
      | mov.b64 %map, load;
      | cvta.param.u64 %map, %map;
      | prefetch.tensormap [%map];
      | cp.async.bulk.prefetch.tensor.2d.L2.global.tile [%map, {%x, %y}];
      | cp.async.bulk.tensor.2d.shared::cluster.global.tile.mbarrier::complete_tx::bytes [box], [%map, {%x, %y}], [bar];
      | mbarrier.arrive.expect_tx.release.cta.shared::cta.b64 %token, [bar], 1024;
      |WAIT:
      | mbarrier.try_wait.parity.shared::cta.b64 %p, [bar], 0;
      | @!%p bra WAIT;
      | mov.u32 %addr, box;
      | mad.lo.u32 %addr, %thread, 4, %addr;
      | ld.shared.u32 %r, [%addr];
      | mul.wide.u32 %offset, %thread, 4;
      | add.u64 %out, %out, %offset;
      | st.global.u32 [%out], %r;
      | fence.proxy.async.shared::cta;
      | bar.sync 0;
      | @!%leader bra DONE;
      | mov.b64 %map, store;
      | cvta.param.u64 %map, %map;
      | cp.async.bulk.tensor.2d.global.shared::cta.tile.bulk_group [%map, {%x, %y}], [box];
      | mov.b64 %map, reduce;
      | cvta.param.u64 %map, %map;
      | cp.reduce.async.bulk.tensor.2d.global.shared::cta.add.tile.bulk_group [%map, {%x, %y}], [box];
      | cp.async.bulk.commit_group;
      | cp.async.bulk.wait_group.read 0;
      |DONE:
      | ret;
      |}
      |""".stripMargin

  val columnsPtx: String =
    """.version 8.6
      |.target sm_90
      |.address_size 64
      |.visible .entry columns(.param .align 64 .b8 map[128], .param .u64 out, .param .u16 offset) {
      | .shared .align 128 .b8 box[512];
      | .shared .align 8 .b64 bar;
      | .reg .u64 %map, %out, %token, %bytes;
      | .reg .u32 %thread, %addr, %r;
      | .reg .b16 %offset;
      | .reg .pred %p, %leader;
      | ld.param.u64 %out, [out];
      | ld.param.u16 %offset, [offset];
      | mov.u32 %thread, %tid.x;
      | setp.eq.u32 %leader, %thread, 0;
      | @!%leader bra WAIT;
      | mbarrier.init.shared::cta.b64 [bar], 1;
      | mov.b64 %map, map;
      | cvta.param.u64 %map, %map;
      | cp.async.bulk.tensor.3d.shared::cluster.global.im2col.mbarrier::complete_tx::bytes [box], [%map, {0, -1, 0}], [bar], {%offset};
      | mbarrier.arrive.expect_tx.shared::cta.b64 %token, [bar], 512;
      |WAIT:
      | bar.sync 0;
      | mbarrier.try_wait.parity.shared::cta.b64 %p, [bar], 0;
      | @!%p bra WAIT;
      | mov.u32 %addr, box;
      | mad.lo.u32 %addr, %thread, 4, %addr;
      | ld.shared.u32 %r, [%addr];
      | mul.wide.u32 %bytes, %thread, 4;
      | add.u64 %out, %out, %bytes;
      | st.global.u32 [%out], %r;
      | ret;
      |}
      |""".stripMargin

  val replacePtx: String =
    """.version 8.6
      |.target sm_90a
      |.address_size 64
      |.visible .entry replace(.param .align 64 .b8 map[128], .param .u64 global, .param .u64 base, .param .u64 out) {
      | .shared .align 128 .b8 staged[128];
      | .shared .align 128 .b8 box[64];
      | .shared .align 8 .b64 bar;
      | .reg .u64 %global, %base, %out, %token, %word;
      | .reg .u32 %thread, %addr, %r;
      | .reg .pred %p;
      | ld.param.u64 %global, [global];
      | ld.param.u64 %base, [base];
      | ld.param.u64 %out, [out];
      | mov.u32 %thread, %tid.x;
      | mul.lo.u32 %addr, %thread, 8;
      | ld.param.u64 %word, [map];
      | setp.eq.u32 %p, %thread, 0;
      | @!%p bra STAGED;
      | ld.param.v2.u64 {%word, %token}, [map];
      | st.shared.v2.u64 [staged], {%word, %token};
      | ld.param.v2.u64 {%word, %token}, [map+16];
      | st.shared.v2.u64 [staged+16], {%word, %token};
      | ld.param.v2.u64 {%word, %token}, [map+32];
      | st.shared.v2.u64 [staged+32], {%word, %token};
      | ld.param.v2.u64 {%word, %token}, [map+48];
      | st.shared.v2.u64 [staged+48], {%word, %token};
      | ld.param.v2.u64 {%word, %token}, [map+64];
      | st.shared.v2.u64 [staged+64], {%word, %token};
      |STAGED:
      | bar.sync 0;
      | tensormap.replace.tile.global_address.shared::cta.b1024.b64 [staged], %base;
      | tensormap.replace.tile.box_dim.shared::cta.b1024.b32 [staged], 0, 16;
      | tensormap.replace.tile.global_dim.shared::cta.b1024.b32 [staged], 0, 12;
      | tensormap.cp_fenceproxy.global.shared::cta.tensormap::generic.release.gpu.sync.aligned [%global], [staged], 128;
      | fence.proxy.tensormap::generic.acquire.gpu [%global], 128;
      | @!%p bra WAIT;
      | mbarrier.init.shared::cta.b64 [bar], 1;
      | cp.async.bulk.tensor.1d.shared::cta.global.tile.mbarrier::complete_tx::bytes [box], [%global, {0}], [bar];
      | mbarrier.arrive.expect_tx.shared::cta.b64 %token, [bar], 64;
      |WAIT:
      | bar.sync 0;
      | mbarrier.try_wait.parity.shared::cta.b64 %p, [bar], 0;
      | @!%p bra WAIT;
      | setp.lt.u32 %p, %thread, 16;
      | @!%p bra END;
      | mov.u32 %addr, box;
      | mad.lo.u32 %addr, %thread, 4, %addr;
      | ld.shared.u32 %r, [%addr];
      | mul.wide.u32 %word, %thread, 4;
      | add.u64 %out, %out, %word;
      | st.global.u32 [%out], %r;
      |END:
      | ret;
      |}
      |""".stripMargin

  val exchangePtx: String =
    """.version 8.6
      |.target sm_90
      |.address_size 64
      |.visible .entry exchange(.param .u64 out)
      |.reqnctapercluster 2, 1, 1
      |{
      | .shared .align 8 .b32 value[32];
      | .reg .u64 %out, %generic, %remote64, %offset;
      | .reg .u32 %myrank, %peer, %thread, %addr, %remote, %v, %block, %r, %g, %after, %located, %old;
      | .reg .u32 %s<6>;
      | .reg .pred %explicit, %shared;
      | ld.param.u64 %out, [out];
      | mov.u32 %myrank, %cluster_ctarank;
      | mov.u32 %thread, %tid.x;
      | mov.u32 %block, %ctaid.x;
      | mov.u32 %addr, value;
      | mad.lo.u32 %addr, %thread, 4, %addr;
      | mad.lo.u32 %v, %block, 1000, %thread;
      | st.shared.u32 [%addr], %v;
      | barrier.cluster.arrive.release.aligned;
      | barrier.cluster.wait.acquire.aligned;
      | xor.b32 %peer, %myrank, 1;
      | mapa.shared::cluster.u32 %remote, %addr, %peer;
      | ld.shared::cluster.u32 %r, [%remote];
      | cvta.shared.u64 %generic, value;
      | mapa.u64 %remote64, %generic, %peer;
      | isspacep.shared::cluster %shared, %remote64;
      | mul.wide.u32 %offset, %thread, 4;
      | add.u64 %remote64, %remote64, %offset;
      | ld.u32 %g, [%remote64];
      | atom.shared::cluster.add.u32 %old, [%remote], 1;
      | getctarank.shared::cluster.u32 %located, %remote;
      | barrier.cluster.arrive;
      | barrier.cluster.wait;
      | ld.shared.u32 %after, [%addr];
      | mov.u32 %s0, %cluster_ctaid.x;
      | mov.u32 %s1, %cluster_nctaid.x;
      | mov.u32 %s2, %clusterid.x;
      | mov.u32 %s3, %nclusterid.x;
      | mov.u32 %s4, %cluster_nctarank;
      | mov.pred %explicit, %is_explicit_cluster;
      | selp.u32 %s5, 1, 0, %explicit;
      | @!%shared mov.u32 %s5, 7;
      | mad.lo.u32 %v, %block, 32, %thread;
      | mul.wide.u32 %offset, %v, 48;
      | add.u64 %out, %out, %offset;
      | st.global.v4.u32 [%out], {%r, %g, %old, %after};
      | st.global.v4.u32 [%out+16], {%located, %s0, %s1, %s2};
      | st.global.v4.u32 [%out+32], {%s3, %s4, %s5, %myrank};
      | ret;
      |}
      |""".stripMargin

  val asyncPtx: String =
    """.version 8.6
      |.target sm_90a
      |.address_size 64
      |.visible .entry async(.param .u64 out)
      |.reqnctapercluster 2, 1, 1
      |{
      | .shared .align 16 .b32 slot[4];
      | .shared .align 128 .b8 buffer[64];
      | .shared .align 8 .b64 bar;
      | .shared .align 8 .b64 joined;
      | .reg .u64 %out, %token, %offset;
      | .reg .u32 %myrank, %peer, %thread, %remote, %remoteBar, %remoteJoined, %v, %a, %b, %c, %d;
      | .reg .b16 %mask;
      | .reg .pred %p, %leader;
      | ld.param.u64 %out, [out];
      | mov.u32 %myrank, %cluster_ctarank;
      | mov.u32 %thread, %tid.x;
      | setp.eq.u32 %leader, %thread, 0;
      | @!%leader bra INITIALISED;
      | mbarrier.init.shared::cta.b64 [bar], 1;
      | mbarrier.init.shared::cta.b64 [joined], 2;
      |INITIALISED:
      | fence.mbarrier_init.release.cluster;
      | barrier.cluster.arrive.relaxed.aligned;
      | barrier.cluster.wait.aligned;
      | elect.sync _|%p, 0xffffffff;
      | setp.lt.u32 %leader, %thread, 32;
      | and.pred %p, %p, %leader;
      | setp.eq.u32 %leader, %thread, 0;
      | @!%p bra WAIT;
      | xor.b32 %peer, %myrank, 1;
      | mov.u32 %remote, slot;
      | mapa.shared::cluster.u32 %remote, %remote, %peer;
      | mov.u32 %remoteBar, bar;
      | mapa.shared::cluster.u32 %remoteBar, %remoteBar, %peer;
      | mov.u32 %remoteJoined, joined;
      | mapa.shared::cluster.u32 %remoteJoined, %remoteJoined, 0;
      | mbarrier.arrive.expect_tx.shared::cta.b64 %token, [bar], 72;
      | add.u32 %v, %myrank, 10;
      | st.async.shared::cluster.mbarrier::complete_tx::bytes.b32 [%remote], %v, [%remoteBar];
      | red.async.relaxed.cluster.shared::cluster.mbarrier::complete_tx::bytes.add.u32 [%remote+4], 5, [%remoteBar];
      | mbarrier.arrive.release.cluster.shared::cluster.b64 _, [%remoteJoined];
      | setp.ne.u32 %p, %myrank, 0;
      | @%p bra WAIT;
      | mov.u16 %mask, 3;
      | cp.async.bulk.shared::cluster.global.mbarrier::complete_tx::bytes.multicast::cluster [buffer], [%out], 64, [bar], %mask;
      |WAIT:
      | mbarrier.try_wait.parity.shared::cta.b64 %p, [bar], 0;
      | @!%p bra WAIT;
      | setp.ne.u32 %p, %myrank, 0;
      | @%p bra JOINED;
      |JOIN:
      | mbarrier.try_wait.parity.acquire.cluster.shared::cta.b64 %p, [joined], 0;
      | @!%p bra JOIN;
      |JOINED:
      | @!%leader bra DONE;
      | ld.shared.v4.u32 {%a, %b, %c, %d}, [slot];
      | mov.u32 %v, %ctaid.x;
      | mul.wide.u32 %offset, %v, 32;
      | add.u64 %out, %out, %offset;
      | ld.shared.v2.u32 {%c, %d}, [buffer+8];
      | st.global.v4.u32 [%out+64], {%a, %b, %c, %d};
      |DONE:
      | barrier.cluster.arrive;
      | barrier.cluster.wait;
      | ret;
      |}
      |""".stripMargin

  /** mbarrier forms, bulk fills and single-device multimem; the arrival before cp.async.mbarrier.arrive.noinc is
    * `.noComplete` where `noComplete` says. The SASS listing compiles the plain arrival: ptxas lowers `.noComplete`
    * arrivals through RPCMOV scratch moves the VM does not model.
    */
  def formsPtx(noComplete: Boolean): String =
    s""".version 8.8
      |.target sm_100a
      |.address_size 64
      |.visible .entry forms(.param .u64 out) {
      | .shared .align 8 .b64 bar;
      | .shared .align 16 .b8 zeroed[64];
      | .reg .u64 %out, %first, %dropped, %last, %next;
      | .reg .u32 %pending<3>, %value, %done<4>, %word<2>;
      | .reg .pred %complete;
      | ld.param.u64 %out, [out];
      | st.shared.v4.u32 [zeroed+48], {1, 2, 3, 4};
      | mbarrier.init.shared::cta.b64 [bar], 3;
      | mbarrier.arrive.shared::cta.b64 %first, [bar];
      | mbarrier.pending_count.b64 %pending0, %first;
      | mbarrier.arrive_drop.shared::cta.b64 %dropped, [bar];
      | mbarrier.pending_count.b64 %pending1, %dropped;
      | mbarrier.test_wait.shared::cta.b64 %complete, [bar], %first;
      | selp.u32 %done0, 1, 0, %complete;
      | mbarrier.expect_tx.relaxed.cta.shared::cta.b64 [bar], 16;
      | mbarrier.arrive.shared::cta.b64 %last, [bar];
      | mbarrier.test_wait.shared::cta.b64 %complete, [bar], %first;
      | selp.u32 %done1, 1, 0, %complete;
      | mbarrier.complete_tx.relaxed.cta.shared::cta.b64 [bar], 16;
      | mbarrier.test_wait.shared::cta.b64 %complete, [bar], %first;
      | selp.u32 %done2, 1, 0, %complete;
      | mbarrier.arrive${if (noComplete) ".noComplete" else ""}.shared::cta.b64 %next, [bar], 1;
      | mbarrier.pending_count.b64 %pending2, %next;
      | cp.async.mbarrier.arrive.noinc.shared::cta.b64 [bar];
      | mbarrier.try_wait.parity.shared::cta.b64 %complete, [bar], 1;
      | selp.u32 %done3, 1, 0, %complete;
      | mov.u64 %last, 64;
      | mov.u32 %value, zeroed;
      | st.bulk.weak.shared::cta [%value], %last, 0;
      | ld.shared.v2.u32 {%word0, %word1}, [zeroed+56];
      | multimem.ld_reduce.relaxed.gpu.global.add.u32 %value, [%out+64];
      | multimem.st.relaxed.gpu.global.b32 [%out+68], %value;
      | mov.u32 %value, 5;
      | multimem.red.relaxed.gpu.global.add.u32 [%out+72], %value;
      | setmaxnreg.inc.sync.aligned.u32 64;
      | griddepcontrol.wait;
      | griddepcontrol.launch_dependents;
      | st.global.v4.u32 [%out], {%pending0, %pending1, %pending2, %word0};
      | st.global.v4.u32 [%out+16], {%done0, %done1, %done2, %done3};
      | st.global.u32 [%out+32], %word1;
      | ret;
      |}
      |""".stripMargin

  val multicastPtx: String =
    """.version 8.6
      |.target sm_90a
      |.address_size 64
      |.visible .entry multicast(.param .align 64 .b8 map[128], .param .u64 out)
      |.reqnctapercluster 2, 1, 1
      |{
      | .shared .align 128 .b8 box[256];
      | .shared .align 8 .b64 bar;
      | .reg .u64 %map, %out, %token, %offset;
      | .reg .u32 %thread, %rank, %block, %addr, %value;
      | .reg .b16 %mask;
      | .reg .pred %p, %leader;
      | ld.param.u64 %out, [out];
      | mov.u32 %thread, %tid.x;
      | mov.u32 %rank, %cluster_ctarank;
      | mov.u32 %block, %ctaid.x;
      | setp.eq.u32 %leader, %thread, 0;
      | @!%leader bra INITIALISED;
      | mbarrier.init.shared::cta.b64 [bar], 1;
      | mbarrier.arrive.expect_tx.shared::cta.b64 %token, [bar], 256;
      |INITIALISED:
      | fence.mbarrier_init.release.cluster;
      | barrier.cluster.arrive.aligned;
      | barrier.cluster.wait.aligned;
      | setp.eq.u32 %p, %rank, 0;
      | and.pred %p, %p, %leader;
      | @!%p bra WAIT;
      | mov.b64 %map, map;
      | cvta.param.u64 %map, %map;
      | mov.u16 %mask, 3;
      | cp.async.bulk.tensor.2d.shared::cluster.global.tile.mbarrier::complete_tx::bytes.multicast::cluster [box], [%map, {4, 1}], [bar], %mask;
      |WAIT:
      | mbarrier.try_wait.parity.shared::cta.b64 %p, [bar], 0;
      | @!%p bra WAIT;
      | mov.u32 %addr, box;
      | mad.lo.u32 %addr, %thread, 4, %addr;
      | ld.shared.u32 %value, [%addr];
      | mad.lo.u32 %addr, %block, 64, %thread;
      | mul.wide.u32 %offset, %addr, 4;
      | add.u64 %out, %out, %offset;
      | st.global.u32 [%out], %value;
      | barrier.cluster.arrive.aligned;
      | barrier.cluster.wait.aligned;
      | ret;
      |}
      |""".stripMargin

  /** Each CTA of a pair reads the failed response one multicast cluster cancellation leaves in both. */
  val cancelPtx: String =
    """.version 8.7
      |.target sm_100a
      |.address_size 64
      |.visible .entry cancel(.param .u64 out)
      |.reqnctapercluster 2, 1, 1
      |{
      | .shared .align 16 .b8 response[16];
      | .shared .align 8 .b64 bar;
      | .reg .u64 %out, %offset, %token, %low, %high;
      | .reg .u32 %thread, %rank, %result, %tries;
      | .reg .b128 %value;
      | .reg .pred %p, %leader, %first, %done;
      | ld.param.u64 %out, [out];
      | mov.u32 %thread, %tid.x;
      | mov.u32 %rank, %cluster_ctarank;
      | setp.eq.u32 %leader, %thread, 0;
      | @!%leader bra ARMED;
      | st.shared.u64 [response], -1;
      | st.shared.u64 [response+8], -1;
      | mbarrier.init.shared::cta.b64 [bar], 1;
      | mbarrier.arrive.expect_tx.shared::cta.b64 %token, [bar], 16;
      |ARMED:
      | barrier.cluster.arrive.aligned;
      | barrier.cluster.wait.aligned;
      | setp.eq.u32 %first, %rank, 0;
      | and.pred %first, %first, %leader;
      | @%first clusterlaunchcontrol.try_cancel.async.shared::cta.mbarrier::complete_tx::bytes.multicast::cluster::all.b128 [response], [bar];
      | mov.u32 %tries, 0;
      |WAIT:
      | mbarrier.try_wait.parity.shared::cta.b64 %done, [bar], 0;
      | @%done bra READ;
      | add.u32 %tries, %tries, 1;
      | setp.lt.u32 %p, %tries, 1000;
      | @%p bra WAIT;
      |READ:
      | ld.shared.v2.b64 {%low, %high}, [response];
      | mov.b128 %value, {%low, %high};
      | clusterlaunchcontrol.query_cancel.is_canceled.pred.b128 %p, %value;
      | selp.u32 %result, 1, 2, %p;
      | selp.u32 %result, %result, 3, %done;
      | mad.lo.u32 %thread, %rank, 32, %thread;
      | mul.wide.u32 %offset, %thread, 4;
      | add.u64 %out, %out, %offset;
      | st.global.u32 [%out], %result;
      | barrier.cluster.arrive.aligned;
      | barrier.cluster.wait.aligned;
      | ret;
      |}
      |""".stripMargin
}
