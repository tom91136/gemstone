// Generated PTX instruction model from the pinned NVIDIA ISA 9.3 specification. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import javax.annotation.processing.Generated
import scala.collection.mutable

@Generated(Array("sbt codegen"))
object Instruction {
  type Decoder = (operands: Vec[Operand], mods: Vec[Modifier]) => Either[DecodeError, Form]

  enum Definition(
      val opcode: NonEmpty[String],
      val mnemonic: Opcode,
      val fixedModifiers: Vec[Modifier],
      val decoder: Decoder,
      metadata: String
  ) {
    case Abs extends Definition(
          "abs",
          Opcode.Abs,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Abs(operands(0), operands(1)))
            else Left(DecodeError("abs", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "1.0 10  RX;s16,s32,s64;;;1\n1.0 13 f64=13 RX;f32;ftz;;1 RX;f64;;;1\n6.5 53 bf16=80,bf16x2=80 RX;f16;ftz;;1 RX;f16x2;ftz;;1 RX;bf16;;;1 RX;bf16x2;;;1"
        )
    case Activemask extends Definition(
          "activemask",
          Opcode.Activemask,
          Vec(),
          (operands, _) => {
            if (operands.size == 1) Right(Form.Activemask(operands(0)))
            else Left(DecodeError("activemask", DecodeProblem.InvalidOperandCount(Vec(1), operands.size)))
          },
          "6.2 30  R;b32;;;1"
        )
    case Add extends Definition(
          "add",
          Opcode.Add,
          Vec(),
          (operands, _) => {
            if (operands.size == 3) Right(Form.Add(dest = operands(0), srcA = operands(1), srcB = operands(2)))
            else Left(DecodeError("add", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "1.0 10 s16x2=90,u16x2=90 RXX;s16,s64,u16,u64;;;1 RXX;s16x2,s32,s8x4,u16x2,u32,u8x4;sat;;1\n1.0 13 f32=20,f32x2=100,f64+rm+rn+rp+rz=13,f64=13 RXX;f32;ftz,rm,rn,rp,rz,sat;;1 RXX;f32x2;ftz,rm,rn,rp,rz;;1 RXX;f64;rm,rn,rp,rz;;1\n4.2 53 bf16=90,bf16x2=90 RXX;f16;ftz,rn,sat;;1 RXX;f16x2;ftz,rn,sat;;1 RXX;bf16;rn;;1 RXX;bf16x2;rn;;1\n1.0 100 bf16+f32=100,f16+f32=100 RXX;f32/bf16,f16;rm,rn,rp,rz,sat;;1"
        )
    case AddCc extends Definition(
          "add.cc",
          Opcode.Add,
          Vec(Modifier.Cc),
          (operands, _) => {
            if (operands.size == 3) Right(Form.AddCc(dest = operands(0), srcA = operands(1), srcB = operands(2)))
            else Left(DecodeError("add.cc", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "1.0 20 cc=20 RXX;s32,s64,u32,u64;;;1"
        )
    case Addc extends Definition(
          "addc",
          Opcode.Addc,
          Vec(),
          (operands, _) => {
            if (operands.size == 3) Right(Form.Addc(dest = operands(0), srcA = operands(1), srcB = operands(2)))
            else Left(DecodeError("addc", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "1.0 20  RXX;s32,s64,u32,u64;cc;;1"
        )
    case Alloca extends Definition(
          "alloca",
          Opcode.Alloca,
          Vec(),
          (operands, _) => {
            if (operands.size >= 2 && operands.size <= 3)
              Right(Form.Alloca(pointer = operands(0), size = operands(1), alignment = operands.lift(2)))
            else Left(DecodeError("alloca", DecodeProblem.InvalidOperandCount(Vec(2, 3), operands.size)))
          },
          "7.3 52  XXX;u32,u64;;;1 XX;u32,u64;;;1"
        )
    case And extends Definition(
          "and",
          Opcode.And,
          Vec(),
          (operands, _) => {
            if (operands.size == 3) Right(Form.And(dest = operands(0), srcA = operands(1), srcB = operands(2)))
            else Left(DecodeError("and", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "1.0 10  RXX;b16,b32,b64,pred;;;1"
        )
    case ApplyPriority extends Definition(
          "applypriority",
          Opcode.ApplyPriority,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.ApplyPriority(operands(0), operands(1)))
            else Left(DecodeError("applypriority", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "7.4 80  AX;L2::evict_normal;global;;1"
        )
    case Atom extends Definition(
          "atom",
          Opcode.Atom,
          Vec(),
          (operands, _) => {
            if (operands.size >= 3 && operands.size <= 4) Right(Form.Atom(
              dest = operands(0),
              address = operands(1),
              value = operands(2),
              compareOrCachePolicy = operands.lift(3)
            ))
            else Left(DecodeError("atom", DecodeProblem.InvalidOperandCount(Vec(3, 4), operands.size)))
          },
          "1.0 11 add+bf16+noftz=90,add+bf16x2+noftz=90,add+f16+noftz=70,add+f16x2+noftz=60,add+f32=20,add+f64=60,add+global=12,add+shared=20,and=32,b128=90,b16+cas=70,cas+global=12,cas+shared=20,cluster=90,exch+global=12,exch+shared=20,global=11,max=32,min=32,or=32,shared=12,xor=32 RAXX;add,and,dec,inc,max,min,or,xor/b32,b64,f32,f64,s32,s64,u32,u64;L2::cache_hint,acq_rel,acquire,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 RAX;add,and,dec,inc,max,min,or,xor/b32,b64,f32,f64,s32,s64,u32,u64;L2::cache_hint,acq_rel,acquire,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 RAXX;cas/b128,b16,b32,b64;acq_rel,acquire,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 RAXX;exch/b128,b32,b64;L2::cache_hint,acq_rel,acquire,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 RAX;exch/b128,b32,b64;L2::cache_hint,acq_rel,acquire,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 RAXX;add/noftz/f16;L2::cache_hint,acq_rel,acquire,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 RAX;add/noftz/f16;L2::cache_hint,acq_rel,acquire,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 RAXX;add/noftz/f16x2;L2::cache_hint,acq_rel,acquire,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 RAX;add/noftz/f16x2;L2::cache_hint,acq_rel,acquire,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 RAXX;add/noftz/bf16;L2::cache_hint,acq_rel,acquire,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 RAX;add/noftz/bf16;L2::cache_hint,acq_rel,acquire,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 RAXX;add/noftz/bf16x2;L2::cache_hint,acq_rel,acquire,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 RAX;add/noftz/bf16x2;L2::cache_hint,acq_rel,acquire,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 RAXX;add/v2,v4/f32;L2::cache_hint,acq_rel,acquire,cluster,cta,global,gpu,relaxed,release,sys;;1 RAX;add/v2,v4/f32;L2::cache_hint,acq_rel,acquire,cluster,cta,global,gpu,relaxed,release,sys;;1 RAXX;add,max,min/noftz/v2,v4,v8/bf16,f16;L2::cache_hint,acq_rel,acquire,cluster,cta,global,gpu,relaxed,release,sys;;1 RAX;add,max,min/noftz/v2,v4,v8/bf16,f16;L2::cache_hint,acq_rel,acquire,cluster,cta,global,gpu,relaxed,release,sys;;1 RAXX;add,max,min/noftz/v2,v4/bf16x2,f16x2;L2::cache_hint,acq_rel,acquire,cluster,cta,global,gpu,relaxed,release,sys;;1 RAX;add,max,min/noftz/v2,v4/bf16x2,f16x2;L2::cache_hint,acq_rel,acquire,cluster,cta,global,gpu,relaxed,release,sys;;1"
        )
    case Bar extends Definition(
          "bar",
          Opcode.Bar,
          Vec(),
          (operands, _) => {
            if (operands.size >= 1 && operands.size <= 4) Right(Form.Bar(
              barrier = operands(0),
              threadCount = operands.lift(1),
              predicate = operands.lift(2),
              value = operands.lift(3)
            ))
            else Left(DecodeError("bar", DecodeProblem.InvalidOperandCount(Vec(1, 2, 3, 4), operands.size)))
          },
          "1.0 20 arrive+cta=20,cta+red=20,cta=30 XX;sync;cta;;1 X;sync;cta;;1 XX;arrive;cta;;1 RXXX;red/popc/u32;cta;;1 RXX;red/popc/u32;cta;;1 RXXX;red/and,or/pred;cta;;1 RXX;red/and,or/pred;cta;;1"
        )
    case BarWarpSync extends Definition(
          "bar.warp.sync",
          Opcode.Bar,
          Vec(Modifier.Warp, Modifier.Sync),
          (operands, _) => {
            if (operands.size == 1) Right(Form.BarWarpSync(operands(0)))
            else Left(DecodeError("bar.warp.sync", DecodeProblem.InvalidOperandCount(Vec(1), operands.size)))
          },
          "6.0 30  X;;;;1"
        )
    case Barrier extends Definition(
          "barrier",
          Opcode.Barrier,
          Vec(),
          (operands, _) => {
            if (operands.size >= 1 && operands.size <= 4) Right(Form.Barrier(
              barrier = operands(0),
              threadCount = operands.lift(1),
              predicate = operands.lift(2),
              value = operands.lift(3)
            ))
            else Left(DecodeError("barrier", DecodeProblem.InvalidOperandCount(Vec(1, 2, 3, 4), operands.size)))
          },
          "1.0 20 arrive+cta=20,cta+red=20,cta=30 XX;sync;aligned,cta;;1 X;sync;aligned,cta;;1 XX;arrive;aligned,cta;;1 RXXX;red/popc/u32;aligned,cta;;1 RXX;red/popc/u32;aligned,cta;;1 RXXX;red/and,or/pred;aligned,cta;;1 RXX;red/and,or/pred;aligned,cta;;1"
        )
    case BarrierCluster extends Definition(
          "barrier.cluster",
          Opcode.Barrier,
          Vec(Modifier.Cluster),
          (operands, _) => {
            if (operands.isEmpty) Right(Form.BarrierCluster())
            else Left(DecodeError("barrier.cluster", DecodeProblem.InvalidOperandCount(Vec(0), operands.size)))
          },
          "7.8 90  ;arrive;aligned,relaxed,release;;1 ;wait;acquire,aligned;;1"
        )
    case Bfe extends Definition(
          "bfe",
          Opcode.Bfe,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Bfe(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("bfe", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "2.0 20  RXXX;s32,s64,u32,u64;;;1"
        )
    case Bfi extends Definition(
          "bfi",
          Opcode.Bfi,
          Vec(),
          (operands, _) => {
            if (operands.size == 5) Right(Form.Bfi(
              dest = operands(0),
              insertValue = operands(1),
              baseValue = operands(2),
              bitPosition = operands(3),
              bitLength = operands(4)
            ))
            else Left(DecodeError("bfi", DecodeProblem.InvalidOperandCount(Vec(5), operands.size)))
          },
          "2.0 20  XXXXX;b32,b64;;;1"
        )
    case Bfind extends Definition(
          "bfind",
          Opcode.Bfind,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Bfind(operands(0), operands(1)))
            else Left(DecodeError("bfind", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "2.0 20  RX;s32,s64,u32,u64;;;1 RX;shiftamt/s32,s64,u32,u64;;;1"
        )
    case Bmsk extends Definition(
          "bmsk",
          Opcode.Bmsk,
          Vec(),
          (operands, _) => {
            if (operands.size == 3) Right(Form.Bmsk(dest = operands(0), srcA = operands(1), srcB = operands(2)))
            else Left(DecodeError("bmsk", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "7.6 70  RXX;clamp,wrap/b32;;;1"
        )
    case Bra extends Definition(
          "bra",
          Opcode.Bra,
          Vec(),
          (operands, _) => {
            if (operands.size == 1) Right(Form.Bra(operands(0)))
            else Left(DecodeError("bra", DecodeProblem.InvalidOperandCount(Vec(1), operands.size)))
          },
          "1.0 10  X;;uni;;1"
        )
    case Brev extends Definition(
          "brev",
          Opcode.Brev,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Brev(operands(0), operands(1)))
            else Left(DecodeError("brev", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "2.0 20  RX;b32,b64;;;1"
        )
    case Brkpt extends Definition(
          "brkpt",
          Opcode.Brkpt,
          Vec(),
          (operands, _) => {
            if (operands.isEmpty) Right(Form.Brkpt())
            else Left(DecodeError("brkpt", DecodeProblem.InvalidOperandCount(Vec(0), operands.size)))
          },
          "1.0 11  ;;;;1"
        )
    case BrxIdx extends Definition(
          "brx.idx",
          Opcode.Brx,
          Vec(Modifier.Idx),
          (operands, _) => {
            if (operands.size == 2) Right(Form.BrxIdx(operands(0), operands(1)))
            else Left(DecodeError("brx.idx", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "6.0 30  XX;;uni;;1"
        )
    case Call extends Definition(
          "call",
          Opcode.Call,
          Vec(),
          (operands, _) => {
            if (operands.size >= 1 && operands.size <= 4) Right(if (
              operands.size >= 2 && Operand.isParenthesized(operands(0))
            ) Form.Call(
              returnParams = Some(operands(0)),
              function = operands(1),
              params = operands.lift(2),
              targets = operands.lift(3)
            )
            else Form.Call(
              returnParams = None,
              function = operands(0),
              params = operands.lift(1),
              targets = operands.lift(2)
            ))
            else Left(DecodeError("call", DecodeProblem.InvalidOperandCount(Vec(1, 2, 3, 4), operands.size)))
          },
          "1.0 20  XSX;;uni;;1 SX;;uni;;1 S;;uni;;1 XXXX;;uni;;1 XXX;;uni;;1 XX;;uni;;1"
        )
    case Clmad extends Definition(
          "clmad",
          Opcode.Clmad,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Clmad(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("clmad", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "9.3 80  RXXX;hi,lo/u64;;;1"
        )
    case ClusterLaunchControlQueryCancel extends Definition(
          "clusterlaunchcontrol.query_cancel",
          Opcode.Clusterlaunchcontrol,
          Vec(Modifier.QueryCancel),
          (operands, _) => {
            if (operands.size == 2) Right(Form.ClusterLaunchControlQueryCancel(operands(0), operands(1)))
            else Left(DecodeError(
              "clusterlaunchcontrol.query_cancel",
              DecodeProblem.InvalidOperandCount(Vec(2), operands.size)
            ))
          },
          "8.6 100  XX;is_canceled/pred/b128;;;1 VX;get_first_ctaid/v4/b32/b128;;;1 XX;get_first_ctaid,get_first_ctaid::x,get_first_ctaid::y,get_first_ctaid::z/b32/b128;;;1"
        )
    case ClusterLaunchControlTryCancel extends Definition(
          "clusterlaunchcontrol.try_cancel",
          Opcode.Clusterlaunchcontrol,
          Vec(Modifier.TryCancel),
          (operands, _) => {
            if (operands.size == 2) Right(Form.ClusterLaunchControlTryCancel(operands(0), operands(1)))
            else Left(DecodeError(
              "clusterlaunchcontrol.try_cancel",
              DecodeProblem.InvalidOperandCount(Vec(2), operands.size)
            ))
          },
          "8.6 100  AA;async/mbarrier::complete_tx::bytes/b128;multicast::cluster::all,shared::cta;;1"
        )
    case Clz extends Definition(
          "clz",
          Opcode.Clz,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Clz(operands(0), operands(1)))
            else Left(DecodeError("clz", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "2.0 20  RX;b32,b64;;;1"
        )
    case Cnot extends Definition(
          "cnot",
          Opcode.Cnot,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Cnot(operands(0), operands(1)))
            else Left(DecodeError("cnot", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "1.0 10  RX;b16,b32,b64;;;1"
        )
    case CopySign extends Definition(
          "copysign",
          Opcode.CopySign,
          Vec(),
          (operands, _) => {
            if (operands.size == 3) Right(Form.CopySign(dest = operands(0), srcA = operands(1), srcB = operands(2)))
            else Left(DecodeError("copysign", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "2.0 20  RXX;f32,f64;;;1"
        )
    case Cos extends Definition(
          "cos",
          Opcode.Cos,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Cos(operands(0), operands(1)))
            else Left(DecodeError("cos", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "1.0 10  RX;approx/f32;ftz;;1"
        )
    case CpAsync extends Definition(
          "cp.async",
          Opcode.Cp,
          Vec(Modifier.Async),
          (operands, _) => {
            if (operands.size >= 3 && operands.size <= 5) Right(Form.CpAsync(
              destAddr = operands(0),
              srcAddr = operands(1),
              copySize = operands(2),
              srcSizeOrIgnore = operands.lift(3),
              cachePolicy = operands.lift(4)
            ))
            else Left(DecodeError("cp.async", DecodeProblem.InvalidOperandCount(Vec(3, 4, 5), operands.size)))
          },
          "7.0 30  AAXXX;ca/shared,shared::cta/global;L2::128B,L2::256B,L2::64B,L2::cache_hint;;1 AAXX;ca/shared,shared::cta/global;L2::128B,L2::256B,L2::64B,L2::cache_hint;;1 AAX;ca/shared,shared::cta/global;L2::128B,L2::256B,L2::64B,L2::cache_hint;;1 AAXXX;cg/shared,shared::cta/global;L2::128B,L2::256B,L2::64B,L2::cache_hint;;1 AAXX;cg/shared,shared::cta/global;L2::128B,L2::256B,L2::64B,L2::cache_hint;;1 AAX;cg/shared,shared::cta/global;L2::128B,L2::256B,L2::64B,L2::cache_hint;;1"
        )
    case CpAsyncBulkMBarrier extends Definition(
          "cp.async.bulk",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.Bulk),
          (operands, mods) => {
            val modifierNames = mods.map(_.name)
            if (
              !modifierNames.contains("bulk_group") &&
              (if (modifierNames.contains("ignore_oob")) operands.size == 6 || operands.size == 7
               else if (modifierNames.contains("multicast::cluster")) operands.size >= 4 && operands.size <= 6
               else operands.size == 4 || operands.size == 5)
            ) Right(if (modifierNames.contains("ignore_oob")) Form.CpAsyncBulkMBarrier(
              destAddr = operands(0),
              srcAddr = operands(1),
              size = operands(2),
              barrierAddr = operands(5),
              ignoredBytesLeft = Some(operands(3)),
              ignoredBytesRight = Some(operands(4)),
              ctaMask = None,
              cachePolicy = operands.lift(6)
            )
            else if (modifierNames.contains("multicast::cluster")) Form.CpAsyncBulkMBarrier(
              destAddr = operands(0),
              srcAddr = operands(1),
              size = operands(2),
              barrierAddr = operands(3),
              ignoredBytesLeft = None,
              ignoredBytesRight = None,
              ctaMask = operands.lift(4),
              cachePolicy = operands.lift(5)
            )
            else Form.CpAsyncBulkMBarrier(
              destAddr = operands(0),
              srcAddr = operands(1),
              size = operands(2),
              barrierAddr = operands(3),
              ignoredBytesLeft = None,
              ignoredBytesRight = None,
              ctaMask = None,
              cachePolicy = operands.lift(4)
            ))
            else if (!modifierNames.contains("bulk_group"))
              Left(DecodeError("cp.async.bulk", DecodeProblem.InvalidOperandCount(Vec(4, 5, 6, 7), operands.size)))
            else Left(DecodeError((("cp.async.bulk") +: modifierNames).mkString("."), DecodeProblem.UnknownOpcode))
          },
          "8.0 90 cp_mask=100 AAXXXAX;shared::cta/global/mbarrier::complete_tx::bytes;L2::cache_hint,ignore_oob,weak;;1 AAXXAX;shared::cta/global/mbarrier::complete_tx::bytes;L2::cache_hint,ignore_oob,weak;;1 AAXAX;shared::cta/global/mbarrier::complete_tx::bytes;L2::cache_hint,ignore_oob,weak;;1 AAXXXA;shared::cta/global/mbarrier::complete_tx::bytes;L2::cache_hint,ignore_oob,weak;;1 AAXXA;shared::cta/global/mbarrier::complete_tx::bytes;L2::cache_hint,ignore_oob,weak;;1 AAXA;shared::cta/global/mbarrier::complete_tx::bytes;L2::cache_hint,ignore_oob,weak;;1 AAXXXAX;relaxed/cluster,cta,gpu,sys/shared::cta/global/mbarrier::complete_tx::bytes/b128;L2::cache_hint,ignore_oob;;1 AAXXAX;relaxed/cluster,cta,gpu,sys/shared::cta/global/mbarrier::complete_tx::bytes/b128;L2::cache_hint,ignore_oob;;1 AAXAX;relaxed/cluster,cta,gpu,sys/shared::cta/global/mbarrier::complete_tx::bytes/b128;L2::cache_hint,ignore_oob;;1 AAXXXA;relaxed/cluster,cta,gpu,sys/shared::cta/global/mbarrier::complete_tx::bytes/b128;L2::cache_hint,ignore_oob;;1 AAXXA;relaxed/cluster,cta,gpu,sys/shared::cta/global/mbarrier::complete_tx::bytes/b128;L2::cache_hint,ignore_oob;;1 AAXA;relaxed/cluster,cta,gpu,sys/shared::cta/global/mbarrier::complete_tx::bytes/b128;L2::cache_hint,ignore_oob;;1 AAXAXX;shared::cluster/global/mbarrier::complete_tx::bytes;L2::cache_hint,multicast::cluster,weak;;1 AAXAX;shared::cluster/global/mbarrier::complete_tx::bytes;L2::cache_hint,multicast::cluster,weak;;1 AAXA;shared::cluster/global/mbarrier::complete_tx::bytes;L2::cache_hint,multicast::cluster,weak;;1 AAXAXX;relaxed/cluster,cta,gpu,sys/shared::cluster/global/mbarrier::complete_tx::bytes/b128;L2::cache_hint,multicast::cluster;;1 AAXAX;relaxed/cluster,cta,gpu,sys/shared::cluster/global/mbarrier::complete_tx::bytes/b128;L2::cache_hint,multicast::cluster;;1 AAXA;relaxed/cluster,cta,gpu,sys/shared::cluster/global/mbarrier::complete_tx::bytes/b128;L2::cache_hint,multicast::cluster;;1 AAXA;shared::cluster/shared::cta/mbarrier::complete_tx::bytes;weak;;1 AAXA;relaxed/cluster,cta/shared::cluster/shared::cta/mbarrier::complete_tx::bytes/b128;;;1 AAXXX;global/shared::cta/bulk_group;L2::cache_hint,cp_mask,weak;;1 AAXX;global/shared::cta/bulk_group;L2::cache_hint,cp_mask,weak;;1 AAX;global/shared::cta/bulk_group;L2::cache_hint,cp_mask,weak;;1 AAXXX;relaxed/cluster,cta,gpu,sys/global/shared::cta/bulk_group/b128;L2::cache_hint,cp_mask;;1 AAXX;relaxed/cluster,cta,gpu,sys/global/shared::cta/bulk_group/b128;L2::cache_hint,cp_mask;;1 AAX;relaxed/cluster,cta,gpu,sys/global/shared::cta/bulk_group/b128;L2::cache_hint,cp_mask;;1"
        )
    case CpAsyncBulkGroup extends Definition(
          "cp.async.bulk",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.Bulk),
          (operands, mods) => {
            val modifierNames = mods.map(_.name)
            if (modifierNames.contains("bulk_group") && (operands.size >= 3 && operands.size <= 5)) Right(if (
              operands.size == 3
            ) Form.CpAsyncBulkGroup(
              destAddr = operands(0),
              srcAddr = operands(1),
              size = operands(2),
              cachePolicy = None,
              byteMask = None
            )
            else if (operands.size == 4 && modifierNames.contains("cp_mask")) Form.CpAsyncBulkGroup(
              destAddr = operands(0),
              srcAddr = operands(1),
              size = operands(2),
              cachePolicy = None,
              byteMask = Some(operands(3))
            )
            else Form.CpAsyncBulkGroup(
              destAddr = operands(0),
              srcAddr = operands(1),
              size = operands(2),
              cachePolicy = Some(operands(3)),
              byteMask = operands.lift(4)
            ))
            else if (modifierNames.contains("bulk_group"))
              Left(DecodeError("cp.async.bulk", DecodeProblem.InvalidOperandCount(Vec(3, 4, 5), operands.size)))
            else Left(DecodeError((("cp.async.bulk") +: modifierNames).mkString("."), DecodeProblem.UnknownOpcode))
          },
          "8.0 90 cp_mask=100 AAXXXAX;shared::cta/global/mbarrier::complete_tx::bytes;L2::cache_hint,ignore_oob,weak;;1 AAXXAX;shared::cta/global/mbarrier::complete_tx::bytes;L2::cache_hint,ignore_oob,weak;;1 AAXAX;shared::cta/global/mbarrier::complete_tx::bytes;L2::cache_hint,ignore_oob,weak;;1 AAXXXA;shared::cta/global/mbarrier::complete_tx::bytes;L2::cache_hint,ignore_oob,weak;;1 AAXXA;shared::cta/global/mbarrier::complete_tx::bytes;L2::cache_hint,ignore_oob,weak;;1 AAXA;shared::cta/global/mbarrier::complete_tx::bytes;L2::cache_hint,ignore_oob,weak;;1 AAXXXAX;relaxed/cluster,cta,gpu,sys/shared::cta/global/mbarrier::complete_tx::bytes/b128;L2::cache_hint,ignore_oob;;1 AAXXAX;relaxed/cluster,cta,gpu,sys/shared::cta/global/mbarrier::complete_tx::bytes/b128;L2::cache_hint,ignore_oob;;1 AAXAX;relaxed/cluster,cta,gpu,sys/shared::cta/global/mbarrier::complete_tx::bytes/b128;L2::cache_hint,ignore_oob;;1 AAXXXA;relaxed/cluster,cta,gpu,sys/shared::cta/global/mbarrier::complete_tx::bytes/b128;L2::cache_hint,ignore_oob;;1 AAXXA;relaxed/cluster,cta,gpu,sys/shared::cta/global/mbarrier::complete_tx::bytes/b128;L2::cache_hint,ignore_oob;;1 AAXA;relaxed/cluster,cta,gpu,sys/shared::cta/global/mbarrier::complete_tx::bytes/b128;L2::cache_hint,ignore_oob;;1 AAXAXX;shared::cluster/global/mbarrier::complete_tx::bytes;L2::cache_hint,multicast::cluster,weak;;1 AAXAX;shared::cluster/global/mbarrier::complete_tx::bytes;L2::cache_hint,multicast::cluster,weak;;1 AAXA;shared::cluster/global/mbarrier::complete_tx::bytes;L2::cache_hint,multicast::cluster,weak;;1 AAXAXX;relaxed/cluster,cta,gpu,sys/shared::cluster/global/mbarrier::complete_tx::bytes/b128;L2::cache_hint,multicast::cluster;;1 AAXAX;relaxed/cluster,cta,gpu,sys/shared::cluster/global/mbarrier::complete_tx::bytes/b128;L2::cache_hint,multicast::cluster;;1 AAXA;relaxed/cluster,cta,gpu,sys/shared::cluster/global/mbarrier::complete_tx::bytes/b128;L2::cache_hint,multicast::cluster;;1 AAXA;shared::cluster/shared::cta/mbarrier::complete_tx::bytes;weak;;1 AAXA;relaxed/cluster,cta/shared::cluster/shared::cta/mbarrier::complete_tx::bytes/b128;;;1 AAXXX;global/shared::cta/bulk_group;L2::cache_hint,cp_mask,weak;;1 AAXX;global/shared::cta/bulk_group;L2::cache_hint,cp_mask,weak;;1 AAX;global/shared::cta/bulk_group;L2::cache_hint,cp_mask,weak;;1 AAXXX;relaxed/cluster,cta,gpu,sys/global/shared::cta/bulk_group/b128;L2::cache_hint,cp_mask;;1 AAXX;relaxed/cluster,cta,gpu,sys/global/shared::cta/bulk_group/b128;L2::cache_hint,cp_mask;;1 AAX;relaxed/cluster,cta,gpu,sys/global/shared::cta/bulk_group/b128;L2::cache_hint,cp_mask;;1"
        )
    case CpAsyncBulkCommitGroup extends Definition(
          "cp.async.bulk.commit_group",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.Bulk, Modifier.CommitGroup),
          (operands, _) => {
            if (operands.isEmpty) Right(Form.CpAsyncBulkCommitGroup())
            else
              Left(DecodeError("cp.async.bulk.commit_group", DecodeProblem.InvalidOperandCount(Vec(0), operands.size)))
          },
          "8.0 90  ;;;;1"
        )
    case CpAsyncBulkPrefetch extends Definition(
          "cp.async.bulk.prefetch",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.Bulk, Modifier.Prefetch),
          (operands, _) => {
            if (operands.size >= 2 && operands.size <= 3)
              Right(Form.CpAsyncBulkPrefetch(srcAddr = operands(0), size = operands(1), cachePolicy = operands.lift(2)))
            else
              Left(DecodeError("cp.async.bulk.prefetch", DecodeProblem.InvalidOperandCount(Vec(2, 3), operands.size)))
          },
          "8.0 90  AXX;L2/global;L2::cache_hint;;1 AX;L2/global;L2::cache_hint;;1"
        )
    case CpAsyncBulkPrefetchTensor extends Definition(
          "cp.async.bulk.prefetch.tensor",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.Bulk, Modifier.Prefetch, Modifier.Tensor),
          (operands, _) => {
            if (operands.size >= 1 && operands.size <= 3) Right(Form.CpAsyncBulkPrefetchTensor(
              tensorMapAndCoordinates = operands(0),
              im2colInfo = operands.lift(1),
              cachePolicy = operands.lift(2)
            ))
            else Left(DecodeError(
              "cp.async.bulk.prefetch.tensor",
              DecodeProblem.InvalidOperandCount(Vec(1, 2, 3), operands.size)
            ))
          },
          "8.0 90  AXX;1d,2d,3d,4d,5d/L2/global;L2::cache_hint,im2col,im2col::w,im2col::w::128,tile,tile::gather4;;1 AX;1d,2d,3d,4d,5d/L2/global;L2::cache_hint,im2col,im2col::w,im2col::w::128,tile,tile::gather4;;1 A;1d,2d,3d,4d,5d/L2/global;L2::cache_hint,im2col,im2col::w,im2col::w::128,tile,tile::gather4;;1"
        )
    case CpAsyncBulkTensorMBarrier extends Definition(
          "cp.async.bulk.tensor",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.Bulk, Modifier.Tensor),
          (operands, mods) => {
            val modifierNames = mods.map(_.name)
            if (
              !modifierNames.contains("bulk_group") &&
              (if (modifierNames.exists(_.startsWith("im2col")) && modifierNames.contains("multicast::cluster"))
                 operands.size >= 5 && operands.size <= 6
               else if (modifierNames.exists(_.startsWith("im2col")) || modifierNames.contains("multicast::cluster"))
                 operands.size >= 4 && operands.size <= 5
               else operands.size == 3 || operands.size == 4)
            ) Right(if (modifierNames.exists(_.startsWith("im2col")) && modifierNames.contains("multicast::cluster"))
              Form.CpAsyncBulkTensorMBarrier(
                destAddr = operands(0),
                tensorMapAndCoordinates = operands(1),
                barrierAddr = operands(2),
                im2colInfo = Some(operands(3)),
                ctaMask = operands.lift(4),
                cachePolicy = operands.lift(5)
              )
            else if (modifierNames.exists(_.startsWith("im2col"))) Form.CpAsyncBulkTensorMBarrier(
              destAddr = operands(0),
              tensorMapAndCoordinates = operands(1),
              barrierAddr = operands(2),
              im2colInfo = Some(operands(3)),
              ctaMask = None,
              cachePolicy = operands.lift(4)
            )
            else if (modifierNames.contains("multicast::cluster")) Form.CpAsyncBulkTensorMBarrier(
              destAddr = operands(0),
              tensorMapAndCoordinates = operands(1),
              barrierAddr = operands(2),
              im2colInfo = None,
              ctaMask = operands.lift(3),
              cachePolicy = operands.lift(4)
            )
            else Form.CpAsyncBulkTensorMBarrier(
              destAddr = operands(0),
              tensorMapAndCoordinates = operands(1),
              barrierAddr = operands(2),
              im2colInfo = None,
              ctaMask = None,
              cachePolicy = operands.lift(3)
            ))
            else if (!modifierNames.contains("bulk_group")) Left(DecodeError(
              "cp.async.bulk.tensor",
              DecodeProblem.InvalidOperandCount(Vec(3, 4, 5, 6), operands.size)
            ))
            else
              Left(DecodeError((("cp.async.bulk.tensor") +: modifierNames).mkString("."), DecodeProblem.UnknownOpcode))
          },
          "8.0 90  AAAXX;1d,2d,3d,4d,5d/shared::cta/global/mbarrier::complete_tx::bytes;L2::cache_hint,cta_group::1,cta_group::2,im2col,im2col::w,im2col::w::128,tile,tile::gather4;;1 AAAX;1d,2d,3d,4d,5d/shared::cta/global/mbarrier::complete_tx::bytes;L2::cache_hint,cta_group::1,cta_group::2,im2col,im2col::w,im2col::w::128,tile,tile::gather4;;1 AAA;1d,2d,3d,4d,5d/shared::cta/global/mbarrier::complete_tx::bytes;L2::cache_hint,cta_group::1,cta_group::2,im2col,im2col::w,im2col::w::128,tile,tile::gather4;;1 AAAXXX;1d,2d,3d,4d,5d/shared::cluster/global/mbarrier::complete_tx::bytes;L2::cache_hint,cta_group::1,cta_group::2,im2col,im2col::w,im2col::w::128,multicast::cluster,tile,tile::gather4;;1 AAAXX;1d,2d,3d,4d,5d/shared::cluster/global/mbarrier::complete_tx::bytes;L2::cache_hint,cta_group::1,cta_group::2,im2col,im2col::w,im2col::w::128,multicast::cluster,tile,tile::gather4;;1 AAAX;1d,2d,3d,4d,5d/shared::cluster/global/mbarrier::complete_tx::bytes;L2::cache_hint,cta_group::1,cta_group::2,im2col,im2col::w,im2col::w::128,multicast::cluster,tile,tile::gather4;;1 AAA;1d,2d,3d,4d,5d/shared::cluster/global/mbarrier::complete_tx::bytes;L2::cache_hint,cta_group::1,cta_group::2,im2col,im2col::w,im2col::w::128,multicast::cluster,tile,tile::gather4;;1 AAX;1d,2d,3d,4d,5d/global/shared::cta/bulk_group;L2::cache_hint,im2col_no_offs,tile,tile::scatter4;;1 AA;1d,2d,3d,4d,5d/global/shared::cta/bulk_group;L2::cache_hint,im2col_no_offs,tile,tile::scatter4;;1"
        )
    case CpAsyncBulkTensorGroup extends Definition(
          "cp.async.bulk.tensor",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.Bulk, Modifier.Tensor),
          (operands, mods) => {
            val modifierNames = mods.map(_.name)
            if (modifierNames.contains("bulk_group") && (operands.size >= 2 && operands.size <= 3))
              Right(Form.CpAsyncBulkTensorGroup(
                tensorMapAndCoordinates = operands(0),
                srcAddr = operands(1),
                cachePolicy = operands.lift(2)
              ))
            else if (modifierNames.contains("bulk_group"))
              Left(DecodeError("cp.async.bulk.tensor", DecodeProblem.InvalidOperandCount(Vec(2, 3), operands.size)))
            else
              Left(DecodeError((("cp.async.bulk.tensor") +: modifierNames).mkString("."), DecodeProblem.UnknownOpcode))
          },
          "8.0 90  AAAXX;1d,2d,3d,4d,5d/shared::cta/global/mbarrier::complete_tx::bytes;L2::cache_hint,cta_group::1,cta_group::2,im2col,im2col::w,im2col::w::128,tile,tile::gather4;;1 AAAX;1d,2d,3d,4d,5d/shared::cta/global/mbarrier::complete_tx::bytes;L2::cache_hint,cta_group::1,cta_group::2,im2col,im2col::w,im2col::w::128,tile,tile::gather4;;1 AAA;1d,2d,3d,4d,5d/shared::cta/global/mbarrier::complete_tx::bytes;L2::cache_hint,cta_group::1,cta_group::2,im2col,im2col::w,im2col::w::128,tile,tile::gather4;;1 AAAXXX;1d,2d,3d,4d,5d/shared::cluster/global/mbarrier::complete_tx::bytes;L2::cache_hint,cta_group::1,cta_group::2,im2col,im2col::w,im2col::w::128,multicast::cluster,tile,tile::gather4;;1 AAAXX;1d,2d,3d,4d,5d/shared::cluster/global/mbarrier::complete_tx::bytes;L2::cache_hint,cta_group::1,cta_group::2,im2col,im2col::w,im2col::w::128,multicast::cluster,tile,tile::gather4;;1 AAAX;1d,2d,3d,4d,5d/shared::cluster/global/mbarrier::complete_tx::bytes;L2::cache_hint,cta_group::1,cta_group::2,im2col,im2col::w,im2col::w::128,multicast::cluster,tile,tile::gather4;;1 AAA;1d,2d,3d,4d,5d/shared::cluster/global/mbarrier::complete_tx::bytes;L2::cache_hint,cta_group::1,cta_group::2,im2col,im2col::w,im2col::w::128,multicast::cluster,tile,tile::gather4;;1 AAX;1d,2d,3d,4d,5d/global/shared::cta/bulk_group;L2::cache_hint,im2col_no_offs,tile,tile::scatter4;;1 AA;1d,2d,3d,4d,5d/global/shared::cta/bulk_group;L2::cache_hint,im2col_no_offs,tile,tile::scatter4;;1"
        )
    case CpAsyncBulkWaitGroup extends Definition(
          "cp.async.bulk.wait_group",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.Bulk, Modifier.WaitGroup),
          (operands, _) => {
            if (operands.size == 1) Right(Form.CpAsyncBulkWaitGroup(operands(0)))
            else Left(DecodeError("cp.async.bulk.wait_group", DecodeProblem.InvalidOperandCount(Vec(1), operands.size)))
          },
          "8.0 90  X;;read;;1"
        )
    case CpAsyncCommitGroup extends Definition(
          "cp.async.commit_group",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.CommitGroup),
          (operands, _) => {
            if (operands.isEmpty) Right(Form.CpAsyncCommitGroup())
            else Left(DecodeError("cp.async.commit_group", DecodeProblem.InvalidOperandCount(Vec(0), operands.size)))
          },
          "7.0 80  ;;;;1"
        )
    case CpAsyncMbarrierArrive extends Definition(
          "cp.async.mbarrier.arrive",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.Mbarrier, Modifier.Arrive),
          (operands, _) => {
            if (operands.size == 1) Right(Form.CpAsyncMbarrierArrive(operands(0)))
            else Left(DecodeError("cp.async.mbarrier.arrive", DecodeProblem.InvalidOperandCount(Vec(1), operands.size)))
          },
          "7.0 80  A;b64;noinc,shared,shared::cta;;1"
        )
    case CpAsyncWaitAll extends Definition(
          "cp.async.wait_all",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.WaitAll),
          (operands, _) => {
            if (operands.isEmpty) Right(Form.CpAsyncWaitAll())
            else Left(DecodeError("cp.async.wait_all", DecodeProblem.InvalidOperandCount(Vec(0), operands.size)))
          },
          "7.0 80  ;;;;1"
        )
    case CpAsyncWaitGroup extends Definition(
          "cp.async.wait_group",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.WaitGroup),
          (operands, _) => {
            if (operands.size == 1) Right(Form.CpAsyncWaitGroup(operands(0)))
            else Left(DecodeError("cp.async.wait_group", DecodeProblem.InvalidOperandCount(Vec(1), operands.size)))
          },
          "7.0 80  X;;;;1"
        )
    case CpReduceAsyncBulk extends Definition(
          "cp.reduce.async.bulk",
          Opcode.Cp,
          Vec(Modifier.Reduce, Modifier.Async, Modifier.Bulk),
          (operands, _) => {
            if (operands.size >= 3 && operands.size <= 4) Right(Form.CpReduceAsyncBulk(
              destAddr = operands(0),
              srcAddr = operands(1),
              size = operands(2),
              cachePolicy = operands.lift(3)
            ))
            else Left(DecodeError("cp.reduce.async.bulk", DecodeProblem.InvalidOperandCount(Vec(3, 4), operands.size)))
          },
          "8.0 90  AAXA;shared::cluster/shared::cta/mbarrier::complete_tx::bytes/add,and,dec,inc,max,min,or,xor/b32,b64,s32,u32,u64;cluster,cta,relaxed;;1 AAXX;global/shared::cta/bulk_group/add,and,dec,inc,max,min,or,xor/b32,b64,bf16,f16,f32,f64,s32,s64,u32,u64;L2::cache_hint,cluster,cta,gpu,relaxed,sys;;1 AAX;global/shared::cta/bulk_group/add,and,dec,inc,max,min,or,xor/b32,b64,bf16,f16,f32,f64,s32,s64,u32,u64;L2::cache_hint,cluster,cta,gpu,relaxed,sys;;1 AAXX;global/shared::cta/bulk_group/add/noftz/bf16,f16;L2::cache_hint,cluster,cta,gpu,relaxed,sys;;1 AAX;global/shared::cta/bulk_group/add/noftz/bf16,f16;L2::cache_hint,cluster,cta,gpu,relaxed,sys;;1"
        )
    case CpReduceAsyncBulkTensor extends Definition(
          "cp.reduce.async.bulk.tensor",
          Opcode.Cp,
          Vec(Modifier.Reduce, Modifier.Async, Modifier.Bulk, Modifier.Tensor),
          (operands, _) => {
            if (operands.size >= 2 && operands.size <= 3) Right(Form.CpReduceAsyncBulkTensor(
              address = operands(0),
              srcAddr = operands(1),
              cachePolicy = operands.lift(2)
            ))
            else Left(DecodeError(
              "cp.reduce.async.bulk.tensor",
              DecodeProblem.InvalidOperandCount(Vec(2, 3), operands.size)
            ))
          },
          "8.0 90  AAX;1d,2d,3d,4d,5d/global/shared::cta/add,and,dec,inc,max,min,or,xor/bulk_group;im2col_no_offs,level::cache_hint,tile;;1 AA;1d,2d,3d,4d,5d/global/shared::cta/add,and,dec,inc,max,min,or,xor/bulk_group;im2col_no_offs,level::cache_hint,tile;;1"
        )
    case CreatePolicy extends Definition(
          "createpolicy",
          Opcode.CreatePolicy,
          Vec(),
          (operands, _) => {
            if (operands.size >= 1 && operands.size <= 2 || operands.size == 4) Right(Form.CreatePolicy(
              cachePolicy = operands(0),
              accessProperty = operands.lift(1),
              primarySize = operands.lift(2),
              totalSize = operands.lift(3)
            ))
            else Left(DecodeError("createpolicy", DecodeProblem.InvalidOperandCount(Vec(1, 2, 4), operands.size)))
          },
          "7.4 80  XAXX;range/L2::evict_first,L2::evict_last,L2::evict_normal,L2::evict_unchanged/b64;L2::evict_first,L2::evict_unchanged,global;;1 XX;fractional/L2::evict_first,L2::evict_last,L2::evict_normal,L2::evict_unchanged/b64;L2::evict_first,L2::evict_unchanged;;1 X;fractional/L2::evict_first,L2::evict_last,L2::evict_normal,L2::evict_unchanged/b64;L2::evict_first,L2::evict_unchanged;;1 XX;cvt/L2/b64;;;1"
        )
    case Cvt extends Definition(
          "cvt",
          Opcode.Cvt,
          Vec(),
          (operands, _) => {
            if (operands.size >= 2 && operands.size <= 4) Right(Form.Cvt(
              dest = operands(0),
              src = operands(1),
              secondarySrc = operands.lift(2),
              roundingBits = operands.lift(3)
            ))
            else Left(DecodeError("cvt", DecodeProblem.InvalidOperandCount(Vec(2, 3, 4), operands.size)))
          },
          "1.0 13 bf16+f16=90,bf16+f32+ftz=90,bf16+f32=80,bf16+f64=90,bf16+s16=90,bf16+s32=90,bf16+s64=90,bf16+s8=90,bf16+u16=90,bf16+u32=90,bf16+u64=90,bf16+u8=90,bf16=80,bf16x2=80,e4m3x2+f16x2+satfinite=89,e4m3x2+f32+satfinite=89,e5m2x2+f16x2+satfinite=89,e5m2x2+f32+satfinite=89,f16x2=80,f32+relu+rn+tf32=90,f32+relu+rz+tf32=90,f32+rn+satfinite+tf32=100,f32+rz+satfinite+tf32=100,f64=13,relu=80,tf32=80 RX;bf16,f16,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8/bf16,f16,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;ftz,rmi,rni,rpi,rzi,sat;;1 RX;bf16,f16,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8/bf16,f16,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;ftz,rm,rn,rp,rz,sat;;1 RX;rn,rz/f16/f32;relu,satfinite;;1 RXX;rn,rz/f16x2/f32;relu,satfinite;;1 RXXX;rs/f16x2/f32;relu,satfinite;;1 RX;rn,rz/bf16/f32;relu,satfinite;;1 RXX;rn,rz/bf16x2/f32;relu,satfinite;;1 RXXX;rs/bf16x2/f32;relu,satfinite;;1 RX;rna/tf32/f32;satfinite;;1 RX;rn,rz/tf32/f32;relu,satfinite;;1 RXX;rn/satfinite/e4m3x2,e5m2x2/f32;relu;;1 RX;rn/satfinite/e4m3x2,e5m2x2/bf16x2,f16x2;relu;;1 RX;rn/f16x2/e4m3x2,e5m2x2;relu;;1 RXX;rn/bf16x2/e4m3x2,e5m2x2;relu,satfinite,scaled::n2::ue8m0;;1 RX;rn/bf16x2/e4m3x2,e5m2x2;relu,satfinite,scaled::n2::ue8m0;;1 RVX;rs/satfinite/e4m3x4,e5m2x4/f32;relu;;1 RXX;rn/satfinite/e2m1x2/f32;relu;;1 RX;rn/satfinite/e2m1x2/bf16x2,f16x2;relu;;1 RX;rn/f16x2/e2m1x2;relu;;1 RXX;rn/bf16x2/e2m1x2;relu,satfinite,scaled::n2::ue8m0;;1 RX;rn/bf16x2/e2m1x2;relu,satfinite,scaled::n2::ue8m0;;1 RVX;rs/satfinite/e2m1x4/f32;relu;;1 RXX;rn/satfinite/e2m3x2,e3m2x2/f32;relu;;1 RX;rn/satfinite/e2m3x2,e3m2x2/bf16x2,f16x2;relu;;1 RX;rn/f16x2/e2m3x2,e3m2x2;relu;;1 RXX;rn/bf16x2/e2m3x2,e3m2x2;relu,satfinite,scaled::n2::ue8m0;;1 RX;rn/bf16x2/e2m3x2,e3m2x2;relu,satfinite,scaled::n2::ue8m0;;1 RVX;rs/satfinite/e2m3x4,e3m2x4/f32;relu;;1 RXX;rp,rz/ue8m0x2/f32;satfinite;;1 RX;rp,rz/ue8m0x2/bf16x2;satfinite;;1 RX;rn/bf16x2/ue8m0x2;;;1 RXXX;rn/satfinite/s2f6x2/f32;relu,scaled::n2::ue8m0;;1 RXX;rn/satfinite/s2f6x2/f32;relu,scaled::n2::ue8m0;;1 RXX;rn/satfinite/s2f6x2/bf16x2;relu,scaled::n2::ue8m0;;1 RX;rn/satfinite/s2f6x2/bf16x2;relu,scaled::n2::ue8m0;;1 RXX;rn/bf16x2/s2f6x2;relu,satfinite,scaled::n2::ue8m0;;1 RX;rn/bf16x2/s2f6x2;relu,satfinite,scaled::n2::ue8m0;;1"
        )
    case CvtPack extends Definition(
          "cvt.pack",
          Opcode.Cvt,
          Vec(Modifier.Pack),
          (operands, _) => {
            if (operands.size >= 3 && operands.size <= 4) Right(Form.CvtPack(
              dest = operands(0),
              src = operands(1),
              secondarySrc = operands(2),
              roundingBits = operands.lift(3)
            ))
            else Left(DecodeError("cvt.pack", DecodeProblem.InvalidOperandCount(Vec(3, 4), operands.size)))
          },
          "6.5 72 s2=75,s4=75,u2=75,u4=75 RXX;sat/s16,u16/s32;;;1 RXXX;sat/s2,s4,s8,u2,u4,u8/s32/b32;;;1"
        )
    case Cvta extends Definition(
          "cvta",
          Opcode.Cvta,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Cvta(operands(0), operands(1)))
            else Left(DecodeError("cvta", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "2.0 20 param+to=70,param=70 RX;const,global,local,param,param::entry,shared,shared::cluster,shared::cta/u32,u64;;;1 RX;to/const,global,local,param,param::entry,shared,shared::cluster,shared::cta/u32,u64;;;1"
        )
    case Discard extends Definition(
          "discard",
          Opcode.Discard,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Discard(operands(0), operands(1)))
            else Left(DecodeError("discard", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "7.4 80  AX;L2;global;;1"
        )
    case Div extends Definition(
          "div",
          Opcode.Div,
          Vec(),
          (operands, _) => {
            if (operands.size == 3) Right(Form.Div(dest = operands(0), srcA = operands(1), srcB = operands(2)))
            else Left(DecodeError("div", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "1.0 10  RXX;s16,s32,s64,u16,u32,u64;;;1\n1.0 13 f32=20,f64+rm=20,f64+rn=13,f64+rp=20,f64+rz=20 RXX;approx/f32;ftz;;1 RXX;full/f32;ftz;;1 RXX;rm,rn,rp,rz/f32;ftz;;1 RXX;rm,rn,rp,rz/f64;;;1"
        )
    case Dp2a extends Definition(
          "dp2a",
          Opcode.Dp2a,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Dp2a(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("dp2a", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "5.0 61  RXXX;hi,lo/s32,u32/s32,u32;;;1"
        )
    case Dp4a extends Definition(
          "dp4a",
          Opcode.Dp4a,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Dp4a(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("dp4a", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "5.0 61  RXXX;s32,u32/s32,u32;;;1"
        )
    case ElectSync extends Definition(
          "elect.sync",
          Opcode.Elect,
          Vec(Modifier.Sync),
          (operands, _) => {
            if (operands.size == 2) Right(Form.ElectSync(operands(0), operands(1)))
            else Left(DecodeError("elect.sync", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "8.0 90  RX;;;;1"
        )
    case Ex2 extends Definition(
          "ex2",
          Opcode.Ex2,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Ex2(operands(0), operands(1)))
            else Left(DecodeError("ex2", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "1.0 10  RX;approx/f32;ftz;;1\n7.0 75 approx+bf16+ftz=90,approx+bf16x2+ftz=90 RX;approx/f16,f16x2;;;1 RX;approx/ftz/bf16,bf16x2;;;1"
        )
    case Exit extends Definition(
          "exit",
          Opcode.Exit,
          Vec(),
          (operands, _) => {
            if (operands.isEmpty) Right(Form.Exit())
            else Left(DecodeError("exit", DecodeProblem.InvalidOperandCount(Vec(0), operands.size)))
          },
          "1.0 10  ;;;;1"
        )
    case FabricSubmit extends Definition(
          "fabric.submit",
          Opcode.Fabric,
          Vec(Modifier.Submit),
          (operands, _) => {
            if (operands.isEmpty) Right(Form.FabricSubmit())
            else Left(DecodeError("fabric.submit", DecodeProblem.InvalidOperandCount(Vec(0), operands.size)))
          },
          "9.3 100  ;;op_restrict::fetching;;1"
        )
    case FabricTryGet extends Definition(
          "fabric.try_get",
          Opcode.Fabric,
          Vec(Modifier.TryGet),
          (operands, _) => {
            if (operands.size == 4) Right(Form.FabricTryGet(
              destAddr = operands(0),
              srcAddr = operands(1),
              size = operands(2),
              barrierAddr = operands(3)
            ))
            else Left(DecodeError("fabric.try_get", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "9.3 100  AAXA;async/shared::cta/relaxed/sys/b128;;;1"
        )
    case FabricTryPullred extends Definition(
          "fabric.try_pullred",
          Opcode.Fabric,
          Vec(Modifier.TryPullred),
          (operands, _) => {
            if (operands.size == 5) Right(Form.FabricTryPullred(
              destAddr = operands(0),
              srcAddr = operands(1),
              size = operands(2),
              barrierAddr = operands(3),
              memberMask = operands(4)
            ))
            else Left(DecodeError("fabric.try_pullred", DecodeProblem.InvalidOperandCount(Vec(5), operands.size)))
          },
          "9.3 100  AAXAX;async/multimem/shared::cta/relaxed/sys/and,or,xor/b32,b64/sync;;;1 AAXAX;async/multimem/shared::cta/relaxed/sys/max,min/bf16,e4m3,e5m2,f16,s32,s64,u32,u64/sync;;;1 AAXAX;async/multimem/shared::cta/relaxed/sys/add/bf16,f16,f32,u32,u64/sync;;;1 AAXAX;async/multimem/shared::cta/relaxed/sys/e4m3,e5m2/sync;;;1 AAXAX;async/multimem/shared::cta/relaxed/sys/bf16,f16/sync;;;1"
        )
    case FabricTryPut extends Definition(
          "fabric.try_put",
          Opcode.Fabric,
          Vec(Modifier.TryPut),
          (operands, _) => {
            if (operands.size >= 4 && operands.size <= 5) Right(Form.FabricTryPut(
              destAddr = operands(0),
              srcAddr = operands(1),
              size = operands(2),
              barrierAddr = operands(3),
              bytemask = operands.lift(4)
            ))
            else Left(DecodeError("fabric.try_put", DecodeProblem.InvalidOperandCount(Vec(4, 5), operands.size)))
          },
          "9.3 100  AAXA;async/shared::cta/relaxed/sys/b128;multimem;;1 AAXAX;async/shared::cta/cp_mask/relaxed/sys/b128;multimem;;1"
        )
    case FabricTryRed extends Definition(
          "fabric.try_red",
          Opcode.Fabric,
          Vec(Modifier.TryRed),
          (operands, _) => {
            if (operands.size == 4) Right(Form.FabricTryRed(
              destAddr = operands(0),
              srcAddr = operands(1),
              size = operands(2),
              barrierAddr = operands(3)
            ))
            else Left(DecodeError("fabric.try_red", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "9.3 100  AAXA;async/shared::cta/relaxed/sys/and,or,xor/b32,b64;multimem;;1 AAXA;async/shared::cta/relaxed/sys/max,min/bf16,f16,s32,s64,u32,u64;multimem;;1 AAXA;async/shared::cta/relaxed/sys/add/bf16,f16,f32,f64,u32,u64;multimem;;1"
        )
    case FabricWait extends Definition(
          "fabric.wait",
          Opcode.Fabric,
          Vec(Modifier.Wait),
          (operands, _) => {
            if (operands.isEmpty) Right(Form.FabricWait())
            else Left(DecodeError("fabric.wait", DecodeProblem.InvalidOperandCount(Vec(0), operands.size)))
          },
          "9.3 100  ;sync_restrict::reads;;;1"
        )
    case Fence extends Definition(
          "fence",
          Opcode.Fence,
          Vec(),
          (operands, _) => {
            if (operands.isEmpty || operands.size == 2) Right(Form.Fence(operands.headOption, operands.lift(1)))
            else Left(DecodeError("fence", DecodeProblem.InvalidOperandCount(Vec(0, 2), operands.size)))
          },
          "1.0 20 acquire=90,async+proxy=90,cluster=90,proxy=60,release=90,sys=20 ;cluster,cta,gpu,sys;acq_rel,acquire,release,sc;;1 ;acquire/sync_restrict::shared::cluster/cluster;;;1 ;release/sync_restrict::shared::cta/cluster;;;1 ;mbarrier_init/release/cluster;;;1 ;proxy/alias,async;;;1 ;proxy/tensormap::generic/release/cluster,cta,gpu,sys;;;1 AX;proxy/tensormap::generic/acquire/cluster,cta,gpu,sys;;;1 ;proxy/async::generic/acquire/sync_restrict::shared::cluster/cluster;;;1 ;proxy/async::generic/release/sync_restrict::shared::cta/cluster;;;1 ;proxy/fabric::fabric,fabric::generic,generic::fabric/alias/acquire,release/sys;;;1"
        )
    case Fma extends Definition(
          "fma",
          Opcode.Fma,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Fma(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("fma", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "1.0 13 f32=20,f32x2=100,f64=13 RXXX;rm,rn,rp,rz/f32;ftz,sat;;1 RXXX;rm,rn,rp,rz/f32x2;ftz;;1 RXXX;rm,rn,rp,rz/f64;;;1\n4.2 53 bf16+oob=90,bf16+relu=80,bf16x2+oob=90,bf16x2+relu=80,f16+oob=90,f16+relu=80,f16x2+oob=90,f16x2+relu=80 RXXX;rn/f16;ftz,sat;;1 RXXX;rn/f16x2;ftz,sat;;1 RXXX;rn/relu/f16;ftz;;1 RXXX;rn/relu/f16x2;ftz;;1 RXXX;rn/bf16;relu;;1 RXXX;rn/bf16x2;relu;;1 RXXX;rn/oob/type;;;0\n1.0 100 bf16+f32=100,f16+f32=100 RXXX;rm,rn,rp,rz/f32/bf16,f16;sat;;1"
        )
    case Fns extends Definition(
          "fns",
          Opcode.Fns,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Fns(dest = operands(0), mask = operands(1), base = operands(2), offset = operands(3)))
            else Left(DecodeError("fns", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "6.0 30  RXXX;b32;;;1"
        )
    case GetCtaRank extends Definition(
          "getctarank",
          Opcode.GetCtaRank,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.GetCtaRank(operands(0), operands(1)))
            else Left(DecodeError("getctarank", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "7.8 90  RX;u32,u64;shared::cluster;;1 RX;shared::cluster/u32,u64;;;1 RX;u32,u64;;;1"
        )
    case GridDependencyControl extends Definition(
          "griddepcontrol",
          Opcode.GridDependencyControl,
          Vec(),
          (operands, _) => {
            if (operands.isEmpty) Right(Form.GridDependencyControl())
            else Left(DecodeError("griddepcontrol", DecodeProblem.InvalidOperandCount(Vec(0), operands.size)))
          },
          "7.8 90  ;launch_dependents,wait;;;1"
        )
    case IsSpacePredicate extends Definition(
          "isspacep",
          Opcode.IsSpacePredicate,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.IsSpacePredicate(operands(0), operands(1)))
            else Left(DecodeError("isspacep", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "2.0 20 param=70 RX;const,global,local,param,param::entry,shared,shared::cluster,shared::cta;;;1"
        )
    case IsTypePredicate extends Definition(
          "istypep",
          Opcode.IsTypePredicate,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.IsTypePredicate(operands(0), operands(1)))
            else Left(DecodeError("istypep", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "4.0 30  RX;samplerref,surfref,texref;;;1"
        )
    case Ld extends Definition(
          "ld",
          Opcode.Ld,
          Vec(),
          (operands, _) => {
            if (operands.size >= 2 && operands.size <= 3)
              Right(Form.Ld(dest = operands(0), address = operands(1), cachePolicy = operands.lift(2)))
            else Left(DecodeError("ld", DecodeProblem.InvalidOperandCount(Vec(2, 3), operands.size)))
          },
          "1.0 13 L2::256B=80,L2::cache_hint=80,acquire=70,b128=70,b32+v8=100,b64+v4=100,cluster=90,f64=13,mmio=70,relaxed=70,weak=70 RAX;b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L2::128B,L2::256B,L2::64B,L2::cache_hint,ca,cg,const,cs,cv,global,local,lu,param,param::entry,param::func,shared,shared::cluster,shared::cta,v2,v4,v8,weak;;1 RA;b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L2::128B,L2::256B,L2::64B,L2::cache_hint,ca,cg,const,cs,cv,global,local,lu,param,param::entry,param::func,shared,shared::cluster,shared::cta,v2,v4,v8,weak;;1 RAX;b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L1::evict_first,L1::evict_last,L1::evict_normal,L1::evict_unchanged,L1::no_allocate,L2::128B,L2::256B,L2::64B,L2::cache_hint,L2::evict_first,L2::evict_last,L2::evict_normal,const,global,local,param,param::entry,param::func,shared,shared::cluster,shared::cta,v2,v4,v8,weak;;1 RA;b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L1::evict_first,L1::evict_last,L1::evict_normal,L1::evict_unchanged,L1::no_allocate,L2::128B,L2::256B,L2::64B,L2::cache_hint,L2::evict_first,L2::evict_last,L2::evict_normal,const,global,local,param,param::entry,param::func,shared,shared::cluster,shared::cta,v2,v4,v8,weak;;1 RA;volatile/b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L2::128B,L2::256B,L2::64B,const,global,local,param,param::entry,param::func,shared,shared::cluster,shared::cta,v2,v4,v8;;1 RAX;relaxed/cluster,cta,gpu,sys/b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L1::evict_first,L1::evict_last,L1::evict_normal,L1::evict_unchanged,L1::no_allocate,L2::128B,L2::256B,L2::64B,L2::cache_hint,L2::evict_first,L2::evict_last,L2::evict_normal,const,global,local,param,param::entry,param::func,shared,shared::cluster,shared::cta,v2,v4,v8;;1 RA;relaxed/cluster,cta,gpu,sys/b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L1::evict_first,L1::evict_last,L1::evict_normal,L1::evict_unchanged,L1::no_allocate,L2::128B,L2::256B,L2::64B,L2::cache_hint,L2::evict_first,L2::evict_last,L2::evict_normal,const,global,local,param,param::entry,param::func,shared,shared::cluster,shared::cta,v2,v4,v8;;1 RAX;acquire/cluster,cta,gpu,sys/b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L1::evict_first,L1::evict_last,L1::evict_normal,L1::evict_unchanged,L1::no_allocate,L2::128B,L2::256B,L2::64B,L2::cache_hint,L2::evict_first,L2::evict_last,L2::evict_normal,const,global,local,param,param::entry,param::func,shared,shared::cluster,shared::cta,v2,v4,v8;;1 RA;acquire/cluster,cta,gpu,sys/b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L1::evict_first,L1::evict_last,L1::evict_normal,L1::evict_unchanged,L1::no_allocate,L2::128B,L2::256B,L2::64B,L2::cache_hint,L2::evict_first,L2::evict_last,L2::evict_normal,const,global,local,param,param::entry,param::func,shared,shared::cluster,shared::cta,v2,v4,v8;;1 RA;mmio/acquire,relaxed/sys/b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;global;;1"
        )
    case LdGlobalNc extends Definition(
          "ld.global.nc",
          Opcode.Ld,
          Vec(Modifier.Global, Modifier.Nc),
          (operands, _) => {
            if (operands.size >= 2 && operands.size <= 3)
              Right(Form.LdGlobalNc(dest = operands(0), address = operands(1), cachePolicy = operands.lift(2)))
            else Left(DecodeError("ld.global.nc", DecodeProblem.InvalidOperandCount(Vec(2, 3), operands.size)))
          },
          "3.1 32 b128=70,b32+v8=100,b64+v4=100 RAX;b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L2::128B,L2::256B,L2::64B,L2::cache_hint,ca,cg,cs;;1 RA;b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L2::128B,L2::256B,L2::64B,L2::cache_hint,ca,cg,cs;;1 RAX;v2,v4,v8/b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L2::128B,L2::256B,L2::64B,L2::cache_hint,ca,cg,cs;;1 RA;v2,v4,v8/b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L2::128B,L2::256B,L2::64B,L2::cache_hint,ca,cg,cs;;1 RAX;b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L1::evict_first,L1::evict_last,L1::evict_normal,L1::evict_unchanged,L1::no_allocate,L2::128B,L2::256B,L2::64B,L2::cache_hint,L2::evict_first,L2::evict_last,L2::evict_normal;;1 RA;b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L1::evict_first,L1::evict_last,L1::evict_normal,L1::evict_unchanged,L1::no_allocate,L2::128B,L2::256B,L2::64B,L2::cache_hint,L2::evict_first,L2::evict_last,L2::evict_normal;;1 RAX;v2,v4,v8/b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L1::evict_first,L1::evict_last,L1::evict_normal,L1::evict_unchanged,L1::no_allocate,L2::128B,L2::256B,L2::64B,L2::cache_hint,L2::evict_first,L2::evict_last,L2::evict_normal;;1 RA;v2,v4,v8/b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L1::evict_first,L1::evict_last,L1::evict_normal,L1::evict_unchanged,L1::no_allocate,L2::128B,L2::256B,L2::64B,L2::cache_hint,L2::evict_first,L2::evict_last,L2::evict_normal;;1"
        )
    case Ldmatrix extends Definition(
          "ldmatrix",
          Opcode.Ldmatrix,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Ldmatrix(operands(0), operands(1)))
            else Left(DecodeError("ldmatrix", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "6.5 75  RA;sync/aligned/m16n16,m8n8/x1,x2,x4/b16,b8;shared,shared::cta,trans;m16n16,m8n8,trans,x1,x2,x4;1 RA;sync/aligned/m8n16/x1,x2,x4/b8x16/b4x16_p64,b6x16_p32;shared,shared::cta;x1,x2,x4;1 RA;sync/aligned/m16n16/x1,x2,x4/trans/b8x16/b4x16_p64,b6x16_p32;shared,shared::cta;m16n16,trans,x1,x2,x4;1"
        )
    case Ldu extends Definition(
          "ldu",
          Opcode.Ldu,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Ldu(operands(0), operands(1)))
            else Left(DecodeError("ldu", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "2.0 13 b128=70,f64=13 RA;b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;global;;1 RA;v2,v4/b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;global;;1"
        )
    case Lg2 extends Definition(
          "lg2",
          Opcode.Lg2,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Lg2(operands(0), operands(1)))
            else Left(DecodeError("lg2", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "1.0 10  RX;approx/f32;ftz;;1"
        )
    case Lop3 extends Definition(
          "lop3",
          Opcode.Lop3,
          Vec(),
          (operands, _) => {
            if (operands.size >= 5 && operands.size <= 6) Right(Form.Lop3(
              dest = operands(0),
              srcA = operands(1),
              srcB = operands(2),
              srcC = operands(3),
              immLut = operands(4),
              predicateQ = operands.lift(5)
            ))
            else Left(DecodeError("lop3", DecodeProblem.InvalidOperandCount(Vec(5, 6), operands.size)))
          },
          "4.3 50  RXXXX;b32;;;1 RXXXXX;and,or/b32;;;1"
        )
    case Mad extends Definition(
          "mad",
          Opcode.Mad,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Mad(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("mad", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "1.0 10  RXXX;hi,lo,wide/s16,s32,s64,u16,u32,u64;;;1 RXXX;hi/sat/s32;;;1\n1.0 20  RXXX;cc/s32,s64,u32,u64;hi,lo;;1\n1.0 13 f32+rm+rn+rp+rz=20,f64+rm+rn+rp+rz=13,f64=13 RXXX;f32;ftz,sat;;1 RXXX;rm,rn,rp,rz/f32;ftz,sat;;1 RXXX;rm,rn,rp,rz/f64;;;1"
        )
    case MadCc extends Definition(
          "mad.cc",
          Opcode.Mad,
          Vec(Modifier.Cc),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.MadCc(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("mad.cc", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "1.0 20  RXXX;s32,s64,u32,u64;hi,lo;;1"
        )
    case Mad24 extends Definition(
          "mad24",
          Opcode.Mad24,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Mad24(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("mad24", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "1.0 10  RXXX;hi,lo/s32,u32;;;1 RXXX;hi/sat/s32;;;1"
        )
    case Madc extends Definition(
          "madc",
          Opcode.Madc,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Madc(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("madc", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "1.0 20  RXXX;s32,s64,u32,u64;cc,hi,lo;;1"
        )
    case Mapa extends Definition(
          "mapa",
          Opcode.Mapa,
          Vec(),
          (operands, _) => {
            if (operands.size == 3) Right(Form.Mapa(dest = operands(0), srcA = operands(1), srcB = operands(2)))
            else Left(DecodeError("mapa", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "7.8 90  RXX;u32,u64;shared::cluster;;1 RXX;shared::cluster/u32,u64;;;1 RXX;u32,u64;;;1"
        )
    case MatchAllSync extends Definition(
          "match.all.sync",
          Opcode.Match,
          Vec(Modifier.All, Modifier.Sync),
          (operands, _) => {
            if (operands.size == 3)
              Right(Form.MatchAllSync(dest = operands(0), srcA = operands(1), memberMask = operands(2)))
            else Left(DecodeError("match.all.sync", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "6.0 70  RXX;b32,b64;;;1"
        )
    case MatchAnySync extends Definition(
          "match.any.sync",
          Opcode.Match,
          Vec(Modifier.Any, Modifier.Sync),
          (operands, _) => {
            if (operands.size == 3)
              Right(Form.MatchAnySync(dest = operands(0), srcA = operands(1), memberMask = operands(2)))
            else Left(DecodeError("match.any.sync", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "6.0 70  RXX;b32,b64;;;1"
        )
    case Max extends Definition(
          "max",
          Opcode.Max,
          Vec(),
          (operands, _) => {
            if (operands.size >= 3 && operands.size <= 4)
              Right(Form.Max(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands.lift(3)))
            else Left(DecodeError("max", DecodeProblem.InvalidOperandCount(Vec(3, 4), operands.size)))
          },
          "1.0 10 relu+s16x2=90,relu+s32=90,u16x2=90 RXX;s16,s64,u16,u16x2,u32,u64,u8x4;;;1 RXX;s16x2,s32,s8x4;relu;;1\n1.0 13 NaN=80,abs+xorsign=86,f64=13 RXX;f32;NaN,abs,ftz,xorsign;;1 RXXX;f32;NaN,abs,ftz;;1 RXX;f64;;;1\n7.0 80 abs+xorsign=86 RXX;f16;NaN,abs,ftz,xorsign;;1 RXX;f16x2;NaN,abs,ftz,xorsign;;1 RXX;bf16;NaN,abs,xorsign;;1 RXX;bf16x2;NaN,abs,xorsign;;1"
        )
    case MbarrierArrive extends Definition(
          "mbarrier.arrive",
          Opcode.Mbarrier,
          Vec(Modifier.Arrive),
          (operands, _) => {
            if (operands.size >= 2 && operands.size <= 3) Right(Form.MbarrierArrive(
              state = operands(0),
              barrierAddr = operands(1),
              countOrTransactionCount = operands.lift(2)
            ))
            else Left(DecodeError("mbarrier.arrive", DecodeProblem.InvalidOperandCount(Vec(2, 3), operands.size)))
          },
          "7.0 80 cluster=90,expect_tx=90,noComplete=90,relaxed=90 XAX;b64;cluster,cta,relaxed,release,shared,shared::cta;;1 XA;b64;cluster,cta,relaxed,release,shared,shared::cta;;1 XAX;b64;cluster,cta,relaxed,release,shared::cluster;;1 XA;b64;cluster,cta,relaxed,release,shared::cluster;;1 XAX;expect_tx/b64;cluster,cta,relaxed,release,shared,shared::cta;;1 XAX;expect_tx/b64;cluster,cta,relaxed,release,shared::cluster;;1 XAX;noComplete/b64;cta,release,shared,shared::cta;;1"
        )
    case MbarrierArriveDrop extends Definition(
          "mbarrier.arrive_drop",
          Opcode.Mbarrier,
          Vec(Modifier.ArriveDrop),
          (operands, _) => {
            if (operands.size >= 2 && operands.size <= 3) Right(Form.MbarrierArriveDrop(
              state = operands(0),
              barrierAddr = operands(1),
              countOrTransactionCount = operands.lift(2)
            ))
            else Left(DecodeError("mbarrier.arrive_drop", DecodeProblem.InvalidOperandCount(Vec(2, 3), operands.size)))
          },
          "7.0 80 cluster=90,expect_tx=90,noComplete=90,relaxed=90 XAX;b64;cluster,cta,relaxed,release,shared,shared::cta;;1 XA;b64;cluster,cta,relaxed,release,shared,shared::cta;;1 XAX;b64;cluster,cta,relaxed,release,shared::cluster;;1 XA;b64;cluster,cta,relaxed,release,shared::cluster;;1 XAX;expect_tx/b64;cluster,cta,relaxed,release,shared,shared::cta;;1 XAX;expect_tx/b64;cluster,cta,relaxed,release,shared::cluster;;1 XAX;noComplete/b64;cta,release,shared,shared::cta;;1"
        )
    case MbarrierCheckLayout extends Definition(
          "mbarrier.check_layout",
          Opcode.Mbarrier,
          Vec(Modifier.CheckLayout),
          (operands, _) => {
            if (operands.size == 2) Right(Form.MbarrierCheckLayout(operands(0), operands(1)))
            else Left(DecodeError("mbarrier.check_layout", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "9.3 90  RA;layout::v0,layout::v1/b64;shared::cta;;1"
        )
    case MbarrierCompleteTx extends Definition(
          "mbarrier.complete_tx",
          Opcode.Mbarrier,
          Vec(Modifier.CompleteTx),
          (operands, _) => {
            if (operands.size == 2) Right(Form.MbarrierCompleteTx(operands(0), operands(1)))
            else Left(DecodeError("mbarrier.complete_tx", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "8.0 90  AX;b64;cluster,cta,relaxed,shared,shared::cluster,shared::cta;;1"
        )
    case MbarrierExpectTx extends Definition(
          "mbarrier.expect_tx",
          Opcode.Mbarrier,
          Vec(Modifier.ExpectTx),
          (operands, _) => {
            if (operands.size == 2) Right(Form.MbarrierExpectTx(operands(0), operands(1)))
            else Left(DecodeError("mbarrier.expect_tx", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "8.0 90  AX;b64;cluster,cta,relaxed,shared,shared::cluster,shared::cta;;1"
        )
    case MbarrierInit extends Definition(
          "mbarrier.init",
          Opcode.Mbarrier,
          Vec(Modifier.Init),
          (operands, _) => {
            if (operands.size == 2) Right(Form.MbarrierInit(operands(0), operands(1)))
            else Left(DecodeError("mbarrier.init", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "7.0 80  AX;b64;layout::v0,layout::v1,shared,shared::cta;;1"
        )
    case MbarrierInval extends Definition(
          "mbarrier.inval",
          Opcode.Mbarrier,
          Vec(Modifier.Inval),
          (operands, _) => {
            if (operands.size == 1) Right(Form.MbarrierInval(operands(0)))
            else Left(DecodeError("mbarrier.inval", DecodeProblem.InvalidOperandCount(Vec(1), operands.size)))
          },
          "7.0 80  A;b64;shared,shared::cta;;1"
        )
    case MbarrierPendingCount extends Definition(
          "mbarrier.pending_count",
          Opcode.Mbarrier,
          Vec(Modifier.PendingCount),
          (operands, _) => {
            if (operands.size == 2) Right(Form.MbarrierPendingCount(operands(0), operands(1)))
            else Left(DecodeError("mbarrier.pending_count", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "7.0 80  XX;b64;layout::v0;;1"
        )
    case MbarrierTestWait extends Definition(
          "mbarrier.test_wait",
          Opcode.Mbarrier,
          Vec(Modifier.TestWait),
          (operands, _) => {
            if (operands.size >= 3 && operands.size <= 4) Right(if (operands.size == 3) Form.MbarrierTestWait(
              completion = operands(0),
              reportValue = None,
              barrierAddr = operands(1),
              stateOrPhase = operands(2)
            )
            else Form.MbarrierTestWait(
              completion = operands(0),
              reportValue = Some(operands(1)),
              barrierAddr = operands(2),
              stateOrPhase = operands(3)
            ))
            else Left(DecodeError("mbarrier.test_wait", DecodeProblem.InvalidOperandCount(Vec(3, 4), operands.size)))
          },
          "1.0 80 cluster=90,relaxed=90,test_wait=80,try_wait=90 XAX;b64;acquire,cluster,cta,phase_type::primary,relaxed,shared,shared::cta;;1 XXAX;phase_type::primary/b64;acquire,cluster,cta,relaxed,shared,shared::cta;;1 XAX;phase_type::primary/b64;acquire,cluster,cta,relaxed,shared,shared::cta;;1 XAX;parity/b64;acquire,cluster,cta,phase_type::conditional,phase_type::primary,relaxed,shared,shared::cta;;1 XXAX;parity/phase_type::primary/b64;acquire,cluster,cta,relaxed,shared,shared::cta;;1 XAX;parity/phase_type::primary/b64;acquire,cluster,cta,relaxed,shared,shared::cta;;1"
        )
    case MbarrierTryWait extends Definition(
          "mbarrier.try_wait",
          Opcode.Mbarrier,
          Vec(Modifier.TryWait),
          (operands, _) => {
            if (operands.size >= 3 && operands.size <= 5) Right(if (operands.size == 3) Form.MbarrierTryWait(
              completion = operands(0),
              reportValue = None,
              barrierAddr = operands(1),
              stateOrPhase = operands(2),
              timeHint = None
            )
            else if (operands.size == 4 && !Operand.isWithPredicate(operands(0))) Form.MbarrierTryWait(
              completion = operands(0),
              reportValue = None,
              barrierAddr = operands(1),
              stateOrPhase = operands(2),
              timeHint = Some(operands(3))
            )
            else Form.MbarrierTryWait(
              completion = operands(0),
              reportValue = Some(operands(1)),
              barrierAddr = operands(2),
              stateOrPhase = operands(3),
              timeHint = operands.lift(4)
            ))
            else Left(DecodeError("mbarrier.try_wait", DecodeProblem.InvalidOperandCount(Vec(3, 4, 5), operands.size)))
          },
          "1.0 80 cluster=90,relaxed=90,test_wait=80,try_wait=90 XAXX;b64;acquire,cluster,cta,phase_type::primary,relaxed,shared,shared::cta;;1 XAX;b64;acquire,cluster,cta,phase_type::primary,relaxed,shared,shared::cta;;1 XXAXX;phase_type::primary/b64;acquire,cluster,cta,relaxed,shared,shared::cta;;1 XAXX;phase_type::primary/b64;acquire,cluster,cta,relaxed,shared,shared::cta;;1 XXAX;phase_type::primary/b64;acquire,cluster,cta,relaxed,shared,shared::cta;;1 XAX;phase_type::primary/b64;acquire,cluster,cta,relaxed,shared,shared::cta;;1 XAXX;parity/b64;acquire,cluster,cta,phase_type::conditional,phase_type::primary,relaxed,shared,shared::cta;;1 XAX;parity/b64;acquire,cluster,cta,phase_type::conditional,phase_type::primary,relaxed,shared,shared::cta;;1 XXAXX;parity/phase_type::primary/b64;acquire,cluster,cta,relaxed,shared,shared::cta;;1 XAXX;parity/phase_type::primary/b64;acquire,cluster,cta,relaxed,shared,shared::cta;;1 XXAX;parity/phase_type::primary/b64;acquire,cluster,cta,relaxed,shared,shared::cta;;1 XAX;parity/phase_type::primary/b64;acquire,cluster,cta,relaxed,shared,shared::cta;;1"
        )
    case Membar extends Definition(
          "membar",
          Opcode.Membar,
          Vec(),
          (operands, _) => {
            if (operands.isEmpty) Right(Form.Membar())
            else Left(DecodeError("membar", DecodeProblem.InvalidOperandCount(Vec(0), operands.size)))
          },
          "1.0 20 acquire=90,async+proxy=90,cluster=90,proxy=60,release=90,sys=20 ;cta,gl,sys;;;1 ;proxy/alias,async;;;1"
        )
    case Min extends Definition(
          "min",
          Opcode.Min,
          Vec(),
          (operands, _) => {
            if (operands.size >= 3 && operands.size <= 4)
              Right(Form.Min(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands.lift(3)))
            else Left(DecodeError("min", DecodeProblem.InvalidOperandCount(Vec(3, 4), operands.size)))
          },
          "1.0 10 relu+s16x2=90,relu+s32=90,u16x2=90 RXX;s16,s64,u16,u16x2,u32,u64,u8x4;;;1 RXX;s16x2,s32,s8x4;relu;;1\n1.0 13 NaN=80,abs+xorsign=86,f64=13 RXX;f32;NaN,abs,ftz,xorsign;;1 RXXX;f32;NaN,abs,ftz;;1 RXX;f64;;;1\n7.0 80 abs+xorsign=86 RXX;f16;NaN,abs,ftz,xorsign;;1 RXX;f16x2;NaN,abs,ftz,xorsign;;1 RXX;bf16;NaN,abs,xorsign;;1 RXX;bf16x2;NaN,abs,xorsign;;1"
        )
    case Mma extends Definition(
          "mma",
          Opcode.Mma,
          Vec(),
          (operands, _) => {
            if (operands.size == 4 || operands.size == 8) Right(Form.Mma(
              dest = operands(0),
              srcA = operands(1),
              srcB = operands(2),
              srcC = operands(3),
              scaleAData = operands.lift(4),
              scaleASelector = operands.lift(5),
              scaleBData = operands.lift(6),
              scaleBSelector = operands.lift(7)
            ))
            else Left(DecodeError("mma", DecodeProblem.InvalidOperandCount(Vec(4, 8), operands.size)))
          },
          "6.4 70 and=80,b1=75,bf16=80,block_scale=120,e4m3=89,e5m2=89,f16=70,f64=80,m16n8k128=80,m16n8k16=80,m16n8k256=80,m16n8k32=80,m16n8k4=80,m16n8k64=80,m16n8k8=75,m8n8k128=75,m8n8k16=75,m8n8k32=75,m8n8k4=70,s4=75,s8=75,tf32=80,u4=75,u8=75 RXXX;sync/aligned/m8n8k4/col,row/col,row/f16,f32/f16/f16/f16,f32;;;1 RXXX;sync/aligned/m16n8k8/row/col/f16,f32/f16/f16/f16,f32;;;1 RXXX;sync/aligned/m16n8k16/row/col/f16,f32/f16/f16/f16,f32;;;1 RXXX;sync/aligned/m16n8k4/row/col/f32/tf32/tf32/f32;;;1 RXXX;sync/aligned/m16n8k8/row/col/f32/bf16,tf32/bf16,tf32/f32;;;1 RXXX;sync/aligned/m16n8k16/row/col/f32/bf16/bf16/f32;;;1 RXXX;sync/aligned/m16n8k16,m16n8k32/row/col/f16,f32/e4m3,e5m2/e4m3,e5m2/f16,f32;;;1 RXXX;sync/aligned/m16n8k32/row/col/kind::f8f6f4/f16,f32/e2m1,e2m3,e3m2,e4m3,e5m2/e2m1,e2m3,e3m2,e4m3,e5m2/f16,f32;;;1 RXXXXVXV;sync/aligned/m16n8k64/row/col/kind::mxf4/block_scale/f32/e2m1/e2m1/f32/ue8m0;scale_vec::2X;;1 RXXXXVXV;sync/aligned/m16n8k64/row/col/kind::mxf4nvf4/block_scale/scale_vec::2X,scale_vec::4X/f32/e2m1/e2m1/f32/ue4m3,ue8m0;;;1 RXXXXVXV;sync/aligned/m16n8k32/row/col/kind::mxf8f6f4/block_scale/f32/e2m1,e2m3,e3m2,e4m3,e5m2/e2m1,e2m3,e3m2,e4m3,e5m2/f32/ue8m0;scale_vec::1X;;1 RXXX;sync/aligned/m16n8k16,m16n8k4,m16n8k8,m8n8k4/row/col/f64/f64/f64/f64;;;1 RXXX;sync/aligned/m16n8k16,m16n8k32,m8n8k16/row/col/s32/s8,u8/s8,u8/s32;satfinite;;1 RXXX;sync/aligned/m16n8k32,m16n8k64,m8n8k32/row/col/s32/s4,u4/s4,u4/s32;satfinite;;1 RXXX;sync/aligned/m16n8k128,m16n8k256,m8n8k128/row/col/s32/b1/b1/s32/and,xor/popc;;;1"
        )
    case MmaSp extends Definition(
          "mma.sp",
          Opcode.Mma,
          Vec(Modifier.Sp),
          (operands, _) => {
            if (operands.size == 6) Right(Form.MmaSp(
              dest = operands(0),
              srcA = operands(1),
              srcB = operands(2),
              srcC = operands(3),
              e = operands(4),
              f = operands(5)
            ))
            else Left(DecodeError("mma.sp", DecodeProblem.InvalidOperandCount(Vec(6), operands.size)))
          },
          "7.1 80 e2m1=120,e2m3=120,e3m2=120,e4m3=89,e5m2=89,sp::ordered_metadata=80 RXXXXX;sync/aligned/m16n8k16/row/col/f16,f32/f16/f16/f16,f32;;;1 RXXXXX;sync/aligned/m16n8k32/row/col/f16,f32/f16/f16/f16,f32;;;1 RXXXXX;sync/aligned/m16n8k16/row/col/f32/bf16/bf16/f32;;;1 RXXXXX;sync/aligned/m16n8k32/row/col/f32/bf16/bf16/f32;;;1 RXXXXX;sync/aligned/m16n8k8/row/col/f32/tf32/tf32/f32;;;1 RXXXXX;sync/aligned/m16n8k16/row/col/f32/tf32/tf32/f32;;;1 RXXXXX;sync/aligned/m16n8k64/row/col/f32/e4m3,e5m2/e4m3,e5m2/f32;;;1 RXXXXX;sync/aligned/m16n8k32,m16n8k64/row/col/s32/s8,u8/s8,u8/s32;satfinite;;1 RXXXXX;sync/aligned/m16n8k128,m16n8k64/row/col/s32/s4,u4/s4,u4/s32;satfinite;;1"
        )
    case MmaSpOrderedMetadata extends Definition(
          "mma.sp::ordered_metadata",
          Opcode.Mma,
          Vec(Modifier.SpOrderedMetadata),
          (operands, _) => {
            if (operands.size == 6 || operands.size == 10) Right(Form.MmaSpOrderedMetadata(
              dest = operands(0),
              srcA = operands(1),
              srcB = operands(2),
              srcC = operands(3),
              e = operands(4),
              f = operands(5),
              scaleAData = operands.lift(6),
              scaleASelector = operands.lift(7),
              scaleBData = operands.lift(8),
              scaleBSelector = operands.lift(9)
            ))
            else Left(DecodeError(
              "mma.sp::ordered_metadata",
              DecodeProblem.InvalidOperandCount(Vec(6, 10), operands.size)
            ))
          },
          "7.1 80 e2m1=120,e2m3=120,e3m2=120,e4m3=89,e5m2=89,sp::ordered_metadata=80 RXXXXX;sync/aligned/m16n8k16/row/col/f16,f32/f16/f16/f16,f32;;;1 RXXXXX;sync/aligned/m16n8k32/row/col/f16,f32/f16/f16/f16,f32;;;1 RXXXXX;sync/aligned/m16n8k16/row/col/f32/bf16/bf16/f32;;;1 RXXXXX;sync/aligned/m16n8k32/row/col/f32/bf16/bf16/f32;;;1 RXXXXX;sync/aligned/m16n8k8/row/col/f32/tf32/tf32/f32;;;1 RXXXXX;sync/aligned/m16n8k16/row/col/f32/tf32/tf32/f32;;;1 RXXXXX;sync/aligned/m16n8k64/row/col/f32/e4m3,e5m2/e4m3,e5m2/f32;;;1 RXXXXX;sync/aligned/m16n8k64/row/col/kind::f8f6f4/f16,f32/e2m1,e2m3,e3m2,e4m3,e5m2/e2m1,e2m3,e3m2,e4m3,e5m2/f16,f32;;;1 RXXXXXXVXV;sync/aligned/m16n8k128/row/col/kind::mxf4/block_scale/f32/e2m1/e2m1/f32/ue8m0;scale_vec::2X;;1 RXXXXXXVXV;sync/aligned/m16n8k128/row/col/kind::mxf4nvf4/block_scale/scale_vec::2X,scale_vec::4X/f32/e2m1/e2m1/f32/ue4m3,ue8m0;;;1 RXXXXXXVXV;sync/aligned/m16n8k64/row/col/kind::mxf8f6f4/block_scale/f32/e2m1,e2m3,e3m2,e4m3,e5m2/e2m1,e2m3,e3m2,e4m3,e5m2/f32/ue8m0;scale_vec::1X;;1 RXXXXX;sync/aligned/m16n8k32,m16n8k64/row/col/s32/s8,u8/s8,u8/s32;satfinite;;1 RXXXXX;sync/aligned/m16n8k128,m16n8k64/row/col/s32/s4,u4/s4,u4/s32;satfinite;;1"
        )
    case Mov extends Definition(
          "mov",
          Opcode.Mov,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Mov(operands(0), operands(1)))
            else Left(DecodeError("mov", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "1.0 13 f64=13 RX;b16,b32,b64,f32,f64,pred,s16,s32,s64,u16,u32,u64;;;1 RX;u32;;;1 RX;u64;;;1\n1.0 10 b128=70 RX;b128,b16,b32,b64;;;1"
        )
    case Movmatrix extends Definition(
          "movmatrix",
          Opcode.Movmatrix,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Movmatrix(operands(0), operands(1)))
            else Left(DecodeError("movmatrix", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "7.8 75  RX;sync/aligned/m8n8/trans/b16;;;1"
        )
    case Mul extends Definition(
          "mul",
          Opcode.Mul,
          Vec(),
          (operands, _) => {
            if (operands.size == 3) Right(Form.Mul(dest = operands(0), srcA = operands(1), srcB = operands(2)))
            else Left(DecodeError("mul", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "1.0 10  RXX;hi,lo,wide/s16,s32,s64,u16,u32,u64;;;1\n1.0 13 f32=20,f32x2=100,f64+rm+rn+rp+rz=13,f64=13 RXX;f32;ftz,rm,rn,rp,rz,sat;;1 RXX;f32x2;ftz,rm,rn,rp,rz;;1 RXX;f64;rm,rn,rp,rz;;1\n4.2 53 bf16=90,bf16x2=90 RXX;f16;ftz,rn,sat;;1 RXX;f16x2;ftz,rn,sat;;1 RXX;bf16;rn;;1 RXX;bf16x2;rn;;1"
        )
    case Mul24 extends Definition(
          "mul24",
          Opcode.Mul24,
          Vec(),
          (operands, _) => {
            if (operands.size == 3) Right(Form.Mul24(dest = operands(0), srcA = operands(1), srcB = operands(2)))
            else Left(DecodeError("mul24", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "1.0 10  RXX;hi,lo/s32,u32;;;1"
        )
    case MultimemCpAsyncBulk extends Definition(
          "multimem.cp.async.bulk",
          Opcode.Multimem,
          Vec(Modifier.Cp, Modifier.Async, Modifier.Bulk),
          (operands, _) => {
            if (operands.size >= 3 && operands.size <= 4) Right(Form.MultimemCpAsyncBulk(
              destAddr = operands(0),
              srcAddr = operands(1),
              size = operands(2),
              byteMask = operands.lift(3)
            ))
            else
              Left(DecodeError("multimem.cp.async.bulk", DecodeProblem.InvalidOperandCount(Vec(3, 4), operands.size)))
          },
          "9.1 90 cp_mask=100 AAXX;global/shared::cta/bulk_group;cp_mask,weak;;1 AAX;global/shared::cta/bulk_group;cp_mask,weak;;1 AAXX;relaxed/cluster,cta,gpu,sys/global/shared::cta/bulk_group/b128;cp_mask;;1 AAX;relaxed/cluster,cta,gpu,sys/global/shared::cta/bulk_group/b128;cp_mask;;1"
        )
    case MultimemCpReduceAsyncBulk extends Definition(
          "multimem.cp.reduce.async.bulk",
          Opcode.Multimem,
          Vec(Modifier.Cp, Modifier.Reduce, Modifier.Async, Modifier.Bulk),
          (operands, _) => {
            if (operands.size == 3)
              Right(Form.MultimemCpReduceAsyncBulk(destAddr = operands(0), srcAddr = operands(1), size = operands(2)))
            else Left(DecodeError(
              "multimem.cp.reduce.async.bulk",
              DecodeProblem.InvalidOperandCount(Vec(3), operands.size)
            ))
          },
          "9.1 90  AAX;global/shared::cta/bulk_group/add,and,dec,inc,max,min,or,xor/b32,b64,bf16,f16,f32,f64,s32,s64,u32,u64;cluster,cta,gpu,relaxed,sys;;1 AAX;global/shared::cta/bulk_group/add/noftz/bf16,f16;cluster,cta,gpu,relaxed,sys;;1"
        )
    case MultimemLdReduce extends Definition(
          "multimem.ld_reduce",
          Opcode.Multimem,
          Vec(Modifier.LdReduce),
          (operands, _) => {
            if (operands.size == 2) Right(Form.MultimemLdReduce(operands(0), operands(1)))
            else Left(DecodeError("multimem.ld_reduce", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "8.1 90  RA;add,and,max,min,or,xor/b32,b64,s32,s64,u32,u64;acquire,cluster,cta,global,gpu,relaxed,sys;;1 RA;weak/add,and,max,min,or,xor/b32,b64,s32,s64,u32,u64;global;;1 RA;add,max,min/bf16,bf16x2,e4m3,e4m3x2,e4m3x4,e5m2,e5m2x2,e5m2x4,f16,f16x2,f32,f64;acc::f16,acc::f32,acquire,cluster,cta,global,gpu,relaxed,sys,v2,v4,v8;;1 RA;weak/add,max,min/bf16,bf16x2,e4m3,e4m3x2,e4m3x4,e5m2,e5m2x2,e5m2x4,f16,f16x2,f32,f64;acc::f16,acc::f32,global,v2,v4,v8;;1"
        )
    case MultimemRed extends Definition(
          "multimem.red",
          Opcode.Multimem,
          Vec(Modifier.Red),
          (operands, _) => {
            if (operands.size == 2) Right(Form.MultimemRed(operands(0), operands(1)))
            else Left(DecodeError("multimem.red", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "8.1 90  AX;add,and,max,min,or,xor/b32,b64,s32,s64,u32,u64;cluster,cta,global,gpu,relaxed,release,sys;;1 AX;add/bf16,bf16x2,f16,f16x2,f32,f64;cluster,cta,global,gpu,relaxed,release,sys,v2,v4,v8;;1"
        )
    case MultimemRedAsync extends Definition(
          "multimem.red.async",
          Opcode.Multimem,
          Vec(Modifier.Red, Modifier.Async),
          (operands, _) => {
            if (operands.size == 2) Right(Form.MultimemRedAsync(operands(0), operands(1)))
            else Left(DecodeError("multimem.red.async", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "9.3 100  AX;release/gpu,sys/add/s32,u32,u64;global;;1"
        )
    case MultimemSt extends Definition(
          "multimem.st",
          Opcode.Multimem,
          Vec(Modifier.St),
          (operands, _) => {
            if (operands.size == 2) Right(Form.MultimemSt(operands(0), operands(1)))
            else Left(DecodeError("multimem.st", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "8.1 90  AX;b32,b64,s32,s64,u32,u64;cluster,cta,global,gpu,relaxed,release,sys;;1 AX;weak/b32,b64,s32,s64,u32,u64;global;;1 AX;bf16,bf16x2,e4m3,e4m3x2,e4m3x4,e5m2,e5m2x2,e5m2x4,f16,f16x2,f32,f64;cluster,cta,global,gpu,relaxed,release,sys,v2,v4,v8;;1 AX;weak/bf16,bf16x2,e4m3,e4m3x2,e4m3x4,e5m2,e5m2x2,e5m2x4,f16,f16x2,f32,f64;global,v2,v4,v8;;1"
        )
    case MultimemStAsync extends Definition(
          "multimem.st.async",
          Opcode.Multimem,
          Vec(Modifier.St, Modifier.Async),
          (operands, _) => {
            if (operands.size == 2) Right(Form.MultimemStAsync(operands(0), operands(1)))
            else Left(DecodeError("multimem.st.async", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "9.3 100  AX;release/gpu,sys/b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;global;;1"
        )
    case NanoSleep extends Definition(
          "nanosleep",
          Opcode.NanoSleep,
          Vec(),
          (operands, _) => {
            if (operands.size == 1) Right(Form.NanoSleep(operands(0)))
            else Left(DecodeError("nanosleep", DecodeProblem.InvalidOperandCount(Vec(1), operands.size)))
          },
          "1.0 70  X;u32;;;1"
        )
    case Neg extends Definition(
          "neg",
          Opcode.Neg,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Neg(operands(0), operands(1)))
            else Left(DecodeError("neg", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "1.0 10  RX;s16,s32,s64,s8x4;;;1\n1.0 13 f64=13 RX;f32;ftz;;1 RX;f64;;;1\n6.0 53 bf16=80,bf16x2=80 RX;f16;ftz;;1 RX;f16x2;ftz;;1 RX;bf16;;;1 RX;bf16x2;;;1"
        )
    case Not extends Definition(
          "not",
          Opcode.Not,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Not(operands(0), operands(1)))
            else Left(DecodeError("not", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "1.0 10  RX;b16,b32,b64,pred;;;1"
        )
    case Or extends Definition(
          "or",
          Opcode.Or,
          Vec(),
          (operands, _) => {
            if (operands.size == 3) Right(Form.Or(dest = operands(0), srcA = operands(1), srcB = operands(2)))
            else Left(DecodeError("or", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "1.0 10  RXX;b16,b32,b64,pred;;;1"
        )
    case PmEvent extends Definition(
          "pmevent",
          Opcode.PmEvent,
          Vec(),
          (operands, _) => {
            if (operands.size == 1) Right(Form.PmEvent(operands(0)))
            else Left(DecodeError("pmevent", DecodeProblem.InvalidOperandCount(Vec(1), operands.size)))
          },
          "1.0 20 mask=20 X;;;;1 X;mask;;;1"
        )
    case Popc extends Definition(
          "popc",
          Opcode.Popc,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Popc(operands(0), operands(1)))
            else Left(DecodeError("popc", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "2.0 20  RX;b32,b64;;;1"
        )
    case Prefetch extends Definition(
          "prefetch",
          Opcode.Prefetch,
          Vec(),
          (operands, _) => {
            if (operands.size == 1) Right(Form.Prefetch(operands(0)))
            else Left(DecodeError("prefetch", DecodeProblem.InvalidOperandCount(Vec(1), operands.size)))
          },
          "2.0 20 tensormap=90 A;L1,L2;global,local;;1 A;global/L2::evict_last,L2::evict_normal;;;1 A;tensormap;const,param;;1"
        )
    case Prefetchu extends Definition(
          "prefetchu",
          Opcode.Prefetchu,
          Vec(),
          (operands, _) => {
            if (operands.size == 1) Right(Form.Prefetchu(operands(0)))
            else Left(DecodeError("prefetchu", DecodeProblem.InvalidOperandCount(Vec(1), operands.size)))
          },
          "2.0 20 tensormap=90 A;L1;;;1"
        )
    case Prmt extends Definition(
          "prmt",
          Opcode.Prmt,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Prmt(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("prmt", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "2.0 20  RXXX;b32;b4e,ecl,ecr,f4e,rc16,rc8;;1"
        )
    case Rcp extends Definition(
          "rcp",
          Opcode.Rcp,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Rcp(operands(0), operands(1)))
            else Left(DecodeError("rcp", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "1.0 13 f32=20,f64+rm=20,f64+rn=13,f64+rp=20,f64+rz=20 RX;approx/f32;ftz;;1 RX;rm,rn,rp,rz/f32;ftz;;1 RX;rm,rn,rp,rz/f64;;;1"
        )
    case RcpApproxFtzF64 extends Definition(
          "rcp.approx.ftz.f64",
          Opcode.Rcp,
          Vec(Modifier.Approx, Modifier.Ftz, Modifier.F64),
          (operands, _) => {
            if (operands.size == 2) Right(Form.RcpApproxFtzF64(operands(0), operands(1)))
            else Left(DecodeError("rcp.approx.ftz.f64", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "1.0 20 approx+f64+ftz=20 RX;;;;1"
        )
    case Red extends Definition(
          "red",
          Opcode.Red,
          Vec(),
          (operands, _) => {
            if (operands.size >= 2 && operands.size <= 3)
              Right(Form.Red(address = operands(0), value = operands(1), cachePolicy = operands.lift(2)))
            else Left(DecodeError("red", DecodeProblem.InvalidOperandCount(Vec(2, 3), operands.size)))
          },
          "1.2 11 add+bf16+noftz=90,add+bf16x2+noftz=90,add+f16+noftz=70,add+f16x2+noftz=60,add+f32=20,add+f64=60,add+global+u64=12,add+shared+u64=20,and=32,cluster=90,global=11,max=32,min=32,or=32,shared=12,xor=32 AXX;add,and,dec,inc,max,min,or,xor/b32,b64,f32,f64,s32,s64,u32,u64;L2::cache_hint,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 AX;add,and,dec,inc,max,min,or,xor/b32,b64,f32,f64,s32,s64,u32,u64;L2::cache_hint,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 AXX;add/noftz/f16;L2::cache_hint,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 AX;add/noftz/f16;L2::cache_hint,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 AXX;add/noftz/f16x2;L2::cache_hint,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 AX;add/noftz/f16x2;L2::cache_hint,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 AXX;add/noftz/bf16;L2::cache_hint,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 AX;add/noftz/bf16;L2::cache_hint,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 AXX;add/noftz/bf16x2;L2::cache_hint,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 AX;add/noftz/bf16x2;L2::cache_hint,cluster,cta,global,gpu,relaxed,release,shared,shared::cluster,shared::cta,sys;;1 AXX;add/v2,v4/f32;L2::cache_hint,cluster,cta,global,gpu,relaxed,release,sys;;1 AX;add/v2,v4/f32;L2::cache_hint,cluster,cta,global,gpu,relaxed,release,sys;;1 AXX;add,max,min/noftz/v2,v4,v8/bf16,f16;L2::cache_hint,cluster,cta,global,gpu,relaxed,release,sys;;1 AX;add,max,min/noftz/v2,v4,v8/bf16,f16;L2::cache_hint,cluster,cta,global,gpu,relaxed,release,sys;;1 AXX;add,max,min/noftz/v2,v4/bf16x2,f16x2;L2::cache_hint,cluster,cta,global,gpu,relaxed,release,sys;;1 AX;add,max,min/noftz/v2,v4/bf16x2,f16x2;L2::cache_hint,cluster,cta,global,gpu,relaxed,release,sys;;1"
        )
    case RedAsync extends Definition(
          "red.async",
          Opcode.Red,
          Vec(Modifier.Async),
          (operands, _) => {
            if (operands.size >= 2 && operands.size <= 3)
              Right(Form.RedAsync(address = operands(0), value = operands(1), cachePolicy = operands.lift(2)))
            else Left(DecodeError("red.async", DecodeProblem.InvalidOperandCount(Vec(2, 3), operands.size)))
          },
          "8.1 90 global=100,gpu=100,mmio=100,release=100,sys=100 AXA;relaxed/cluster/mbarrier::complete_tx::bytes/dec,inc/u32;shared::cluster;;1 AXA;relaxed/cluster/mbarrier::complete_tx::bytes/max,min/s32,u32;shared::cluster;;1 AXA;relaxed/cluster/mbarrier::complete_tx::bytes/and,or,xor/b32;shared::cluster;;1 AXA;relaxed/cluster/mbarrier::complete_tx::bytes/add/s32,u32,u64;shared::cluster;;1 AX;release/gpu,sys/add/s32,s64,u32,u64;global,mmio;;1"
        )
    case ReduxSync extends Definition(
          "redux.sync",
          Opcode.Redux,
          Vec(Modifier.Sync),
          (operands, _) => {
            if (operands.size == 3)
              Right(Form.ReduxSync(dest = operands(0), src = operands(1), memberMask = operands(2)))
            else Left(DecodeError("redux.sync", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "7.0 80 NaN=100,abs=100 RXX;add,max,min/s32,u32;;;1 RXX;and,or,xor/b32;;;1 RXX;max,min/f32;NaN,abs;;1"
        )
    case Rem extends Definition(
          "rem",
          Opcode.Rem,
          Vec(),
          (operands, _) => {
            if (operands.size == 3) Right(Form.Rem(dest = operands(0), srcA = operands(1), srcB = operands(2)))
            else Left(DecodeError("rem", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "1.0 10  RXX;s16,s32,s64,u16,u32,u64;;;1"
        )
    case Ret extends Definition(
          "ret",
          Opcode.Ret,
          Vec(),
          (operands, _) => {
            if (operands.isEmpty) Right(Form.Ret())
            else Left(DecodeError("ret", DecodeProblem.InvalidOperandCount(Vec(0), operands.size)))
          },
          "1.0 10  ;;uni;;1"
        )
    case Rsqrt extends Definition(
          "rsqrt",
          Opcode.Rsqrt,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Rsqrt(operands(0), operands(1)))
            else Left(DecodeError("rsqrt", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "1.0 13 f64=13 RX;approx/f32;ftz;;1 RX;approx/f64;;;1"
        )
    case RsqrtApproxFtzF64 extends Definition(
          "rsqrt.approx.ftz.f64",
          Opcode.Rsqrt,
          Vec(Modifier.Approx, Modifier.Ftz, Modifier.F64),
          (operands, _) => {
            if (operands.size == 2) Right(Form.RsqrtApproxFtzF64(operands(0), operands(1)))
            else Left(DecodeError("rsqrt.approx.ftz.f64", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "1.0 20 approx+f64+ftz=20 RX;;;;1"
        )
    case Sad extends Definition(
          "sad",
          Opcode.Sad,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Sad(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("sad", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "1.0 10  RXXX;s16,s32,s64,u16,u32,u64;;;1"
        )
    case Selp extends Definition(
          "selp",
          Opcode.Selp,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Selp(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("selp", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "1.0 13 f64=13 RXXX;b16,b32,b64,f32,f64,s16,s32,s64,u16,u32,u64;;;1"
        )
    case Set extends Definition(
          "set",
          Opcode.Set,
          Vec(),
          (operands, _) => {
            if (operands.size >= 3 && operands.size <= 4) Right(Form.Set(
              dest = operands(0),
              left = operands(1),
              right = operands(2),
              booleanSrc = operands.lift(3)
            ))
            else Left(DecodeError("set", DecodeProblem.InvalidOperandCount(Vec(3, 4), operands.size)))
          },
          "1.0 13 f64=13 RXX;eq,equ,ge,geu,gt,gtu,hi,hs,le,leu,lo,ls,lt,ltu,nan,ne,neu,num/f32,s32,u32/b16,b32,b64,f32,f64,s16,s32,s64,u16,u32,u64;ftz;;1 RXXX;eq,equ,ge,geu,gt,gtu,hi,hs,le,leu,lo,ls,lt,ltu,nan,ne,neu,num/and,or,xor/f32,s32,u32/b16,b32,b64,f32,f64,s16,s32,s64,u16,u32,u64;ftz;;1\n4.2 53 b16+bf16=90,b32+bf16=90,b64+bf16=90,bf16+f16=90,bf16+f32=90,bf16+f64=90,bf16+s16=90,bf16+s32=90,bf16+s64=90,bf16+u16=90,bf16+u32=90,bf16+u64=90,bf16x2+s32=90,bf16x2+u32=90,bf16x2=90 RXX;eq,equ,ge,geu,gt,gtu,le,leu,lt,ltu,nan,ne,neu,num/f16/b16,b32,b64,f16,f32,f64,s16,s32,s64,u16,u32,u64;ftz;;1 RXXX;eq,equ,ge,geu,gt,gtu,le,leu,lt,ltu,nan,ne,neu,num/and,or,xor/f16/b16,b32,b64,f16,f32,f64,s16,s32,s64,u16,u32,u64;ftz;;1 RXX;eq,equ,ge,geu,gt,gtu,le,leu,lt,ltu,nan,ne,neu,num/bf16/b16,b32,b64,f16,f32,f64,s16,s32,s64,u16,u32,u64;;;1 RXXX;eq,equ,ge,geu,gt,gtu,le,leu,lt,ltu,nan,ne,neu,num/and,or,xor/bf16/b16,b32,b64,f16,f32,f64,s16,s32,s64,u16,u32,u64;;;1 RXX;eq,equ,ge,geu,gt,gtu,le,leu,lt,ltu,nan,ne,neu,num/s16,s32,u16,u32/f16;ftz;;1 RXXX;eq,equ,ge,geu,gt,gtu,le,leu,lt,ltu,nan,ne,neu,num/and,or,xor/s16,s32,u16,u32/f16;ftz;;1 RXX;eq,equ,ge,geu,gt,gtu,le,leu,lt,ltu,nan,ne,neu,num/s16,s32,u16,u32/bf16;;;1 RXXX;eq,equ,ge,geu,gt,gtu,le,leu,lt,ltu,nan,ne,neu,num/and,or,xor/s16,s32,u16,u32/bf16;;;1 RXX;eq,equ,ge,geu,gt,gtu,le,leu,lt,ltu,nan,ne,neu,num/f16x2,s32,u32/f16x2;ftz;;1 RXXX;eq,equ,ge,geu,gt,gtu,le,leu,lt,ltu,nan,ne,neu,num/and,or,xor/f16x2,s32,u32/f16x2;ftz;;1 RXX;eq,equ,ge,geu,gt,gtu,le,leu,lt,ltu,nan,ne,neu,num/bf16x2,s32,u32/bf16x2;;;1 RXXX;eq,equ,ge,geu,gt,gtu,le,leu,lt,ltu,nan,ne,neu,num/and,or,xor/bf16x2,s32,u32/bf16x2;;;1"
        )
    case SetMaxRegisters extends Definition(
          "setmaxnreg",
          Opcode.SetMaxRegisters,
          Vec(),
          (operands, _) => {
            if (operands.size == 1) Right(Form.SetMaxRegisters(operands(0)))
            else Left(DecodeError("setmaxnreg", DecodeProblem.InvalidOperandCount(Vec(1), operands.size)))
          },
          "8.0 100  X;dec,inc/sync/aligned/u32;;;1"
        )
    case Setp extends Definition(
          "setp",
          Opcode.Setp,
          Vec(),
          (operands, _) => {
            if (operands.size >= 3 && operands.size <= 4) Right(Form.Setp(
              dest = operands(0),
              left = operands(1),
              right = operands(2),
              booleanSrc = operands.lift(3)
            ))
            else Left(DecodeError("setp", DecodeProblem.InvalidOperandCount(Vec(3, 4), operands.size)))
          },
          "1.0 13 f64=13 RXX;eq,equ,ge,geu,gt,gtu,hi,hs,le,leu,lo,ls,lt,ltu,nan,ne,neu,num/b16,b32,b64,f32,f64,s16,s32,s64,u16,u32,u64;ftz;;1 RXXX;eq,equ,ge,geu,gt,gtu,hi,hs,le,leu,lo,ls,lt,ltu,nan,ne,neu,num/and,or,xor/b16,b32,b64,f32,f64,s16,s32,s64,u16,u32,u64;ftz;;1\n4.2 53 bf16=90,bf16x2=90 RXX;eq,equ,ge,geu,gt,gtu,le,leu,lt,ltu,nan,ne,neu,num/f16;ftz;;1 RXXX;eq,equ,ge,geu,gt,gtu,le,leu,lt,ltu,nan,ne,neu,num/and,or,xor/f16;ftz;;1 RXX;eq,equ,ge,geu,gt,gtu,le,leu,lt,ltu,nan,ne,neu,num/f16x2;ftz;;1 RXXX;eq,equ,ge,geu,gt,gtu,le,leu,lt,ltu,nan,ne,neu,num/and,or,xor/f16x2;ftz;;1 RXX;eq,equ,ge,geu,gt,gtu,le,leu,lt,ltu,nan,ne,neu,num/bf16;;;1 RXXX;eq,equ,ge,geu,gt,gtu,le,leu,lt,ltu,nan,ne,neu,num/and,or,xor/bf16;;;1 RXX;eq,equ,ge,geu,gt,gtu,le,leu,lt,ltu,nan,ne,neu,num/bf16x2;;;1 RXXX;eq,equ,ge,geu,gt,gtu,le,leu,lt,ltu,nan,ne,neu,num/and,or,xor/bf16x2;;;1"
        )
    case Shf extends Definition(
          "shf",
          Opcode.Shf,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Shf(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("shf", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "3.1 32  RXXX;l/clamp,wrap/b32;;;1 RXXX;r/clamp,wrap/b32;;;1"
        )
    case Shfl extends Definition(
          "shfl",
          Opcode.Shfl,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Shfl(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("shfl", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "3.0 30  RXXX;bfly,down,idx,up/b32;;;1"
        )
    case ShflSync extends Definition(
          "shfl.sync",
          Opcode.Shfl,
          Vec(Modifier.Sync),
          (operands, _) => {
            if (operands.size == 5) Right(Form.ShflSync(
              dest = operands(0),
              srcA = operands(1),
              srcB = operands(2),
              srcC = operands(3),
              memberMask = operands(4)
            ))
            else Left(DecodeError("shfl.sync", DecodeProblem.InvalidOperandCount(Vec(5), operands.size)))
          },
          "6.0 30  RXXXX;bfly,down,idx,up/b32;;;1"
        )
    case Shl extends Definition(
          "shl",
          Opcode.Shl,
          Vec(),
          (operands, _) => {
            if (operands.size == 3) Right(Form.Shl(dest = operands(0), srcA = operands(1), srcB = operands(2)))
            else Left(DecodeError("shl", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "1.0 10  RXX;b16,b32,b64;;;1"
        )
    case Shr extends Definition(
          "shr",
          Opcode.Shr,
          Vec(),
          (operands, _) => {
            if (operands.size == 3) Right(Form.Shr(dest = operands(0), srcA = operands(1), srcB = operands(2)))
            else Left(DecodeError("shr", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "1.0 10  RXX;b16,b32,b64,s16,s32,s64,u16,u32,u64;;;1"
        )
    case Sin extends Definition(
          "sin",
          Opcode.Sin,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Sin(operands(0), operands(1)))
            else Left(DecodeError("sin", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "1.0 10  RX;approx/f32;ftz;;1"
        )
    case Slct extends Definition(
          "slct",
          Opcode.Slct,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Slct(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("slct", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "1.0 13 f64=13 RXXX;b16,b32,b64,f32,f64,s16,s32,s64,u16,u32,u64/s32;;;1 RXXX;b16,b32,b64,f32,f64,s16,s32,s64,u16,u32,u64/f32;ftz;;1"
        )
    case Sqrt extends Definition(
          "sqrt",
          Opcode.Sqrt,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Sqrt(operands(0), operands(1)))
            else Left(DecodeError("sqrt", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "1.0 13 f32=20,f64+rm=20,f64+rn=13,f64+rp=20,f64+rz=20 RX;approx/f32;ftz;;1 RX;rm,rn,rp,rz/f32;ftz;;1 RX;rm,rn,rp,rz/f64;;;1"
        )
    case St extends Definition(
          "st",
          Opcode.St,
          Vec(),
          (operands, _) => {
            if (operands.size >= 2 && operands.size <= 3)
              Right(Form.St(address = operands(0), value = operands(1), cachePolicy = operands.lift(2)))
            else Left(DecodeError("st", DecodeProblem.InvalidOperandCount(Vec(2, 3), operands.size)))
          },
          "1.0 13 b128=70,b32+v8=100,b64+v4=100,cluster=90,f64=13,mmio=70,relaxed=70,release=70,weak=70 AXX;b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L2::cache_hint,cg,cs,global,local,param,param::func,shared,shared::cluster,shared::cta,v2,v4,v8,wb,weak,wt;;1 AX;b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L2::cache_hint,cg,cs,global,local,param,param::func,shared,shared::cluster,shared::cta,v2,v4,v8,wb,weak,wt;;1 AXX;b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L1::evict_first,L1::evict_last,L1::evict_normal,L1::evict_unchanged,L1::no_allocate,L2::cache_hint,L2::evict_first,L2::evict_last,L2::evict_normal,global,local,param,param::func,shared,shared::cluster,shared::cta,v2,v4,v8,weak;;1 AX;b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L1::evict_first,L1::evict_last,L1::evict_normal,L1::evict_unchanged,L1::no_allocate,L2::cache_hint,L2::evict_first,L2::evict_last,L2::evict_normal,global,local,param,param::func,shared,shared::cluster,shared::cta,v2,v4,v8,weak;;1 AX;volatile/b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;global,local,param,param::func,shared,shared::cluster,shared::cta,v2,v4,v8;;1 AXX;relaxed/cluster,cta,gpu,sys/b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L1::evict_first,L1::evict_last,L1::evict_normal,L1::evict_unchanged,L1::no_allocate,L2::cache_hint,L2::evict_first,L2::evict_last,L2::evict_normal,global,local,param,param::func,shared,shared::cluster,shared::cta,v2,v4,v8;;1 AX;relaxed/cluster,cta,gpu,sys/b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L1::evict_first,L1::evict_last,L1::evict_normal,L1::evict_unchanged,L1::no_allocate,L2::cache_hint,L2::evict_first,L2::evict_last,L2::evict_normal,global,local,param,param::func,shared,shared::cluster,shared::cta,v2,v4,v8;;1 AXX;release/cluster,cta,gpu,sys/b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L1::evict_first,L1::evict_last,L1::evict_normal,L1::evict_unchanged,L1::no_allocate,L2::cache_hint,L2::evict_first,L2::evict_last,L2::evict_normal,global,local,param,param::func,shared,shared::cluster,shared::cta,v2,v4,v8;;1 AX;release/cluster,cta,gpu,sys/b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;L1::evict_first,L1::evict_last,L1::evict_normal,L1::evict_unchanged,L1::no_allocate,L2::cache_hint,L2::evict_first,L2::evict_last,L2::evict_normal,global,local,param,param::func,shared,shared::cluster,shared::cta,v2,v4,v8;;1 AX;mmio/relaxed,release/sys/b128,b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;global;;1"
        )
    case StAsync extends Definition(
          "st.async",
          Opcode.St,
          Vec(Modifier.Async),
          (operands, _) => {
            if (operands.size >= 2 && operands.size <= 3)
              Right(Form.StAsync(address = operands(0), value = operands(1), cachePolicy = operands.lift(2)))
            else Left(DecodeError("st.async", DecodeProblem.InvalidOperandCount(Vec(2, 3), operands.size)))
          },
          "8.1 90 global=100,mmio=100,release=100 AXA;mbarrier::complete_tx::bytes/b128,b32,b64,f32,f64,s32,s64,u32,u64;shared::cluster,v2,v4,weak;;1 AX;release/gpu,sys/b16,b32,b64,b8,f32,f64,s16,s32,s64,s8,u16,u32,u64,u8;global,mmio;;1"
        )
    case StBulk extends Definition(
          "st.bulk",
          Opcode.St,
          Vec(Modifier.Bulk),
          (operands, _) => {
            if (operands.size == 3)
              Right(Form.StBulk(address = operands(0), value = operands(1), cachePolicy = operands(2)))
            else Left(DecodeError("st.bulk", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "8.6 100  AXX;;shared::cta,weak;;1"
        )
    case StackRestore extends Definition(
          "stackrestore",
          Opcode.StackRestore,
          Vec(),
          (operands, _) => {
            if (operands.size == 1) Right(Form.StackRestore(operands(0)))
            else Left(DecodeError("stackrestore", DecodeProblem.InvalidOperandCount(Vec(1), operands.size)))
          },
          "7.3 52  X;u32,u64;;;1"
        )
    case StackSave extends Definition(
          "stacksave",
          Opcode.StackSave,
          Vec(),
          (operands, _) => {
            if (operands.size == 1) Right(Form.StackSave(operands(0)))
            else Left(DecodeError("stacksave", DecodeProblem.InvalidOperandCount(Vec(1), operands.size)))
          },
          "7.3 52  R;u32,u64;;;1"
        )
    case Stmatrix extends Definition(
          "stmatrix",
          Opcode.Stmatrix,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Stmatrix(operands(0), operands(1)))
            else Left(DecodeError("stmatrix", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "7.8 90  AX;sync/aligned/m16n8,m8n8/x1,x2,x4/b16,b8;shared,shared::cta,trans;;1"
        )
    case Sub extends Definition(
          "sub",
          Opcode.Sub,
          Vec(),
          (operands, _) => {
            if (operands.size == 3) Right(Form.Sub(dest = operands(0), srcA = operands(1), srcB = operands(2)))
            else Left(DecodeError("sub", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "1.0 10  RXX;s16,s64,u16,u32,u64;;;1 RXX;s32,s8x4,u8x4;sat;;1\n1.0 13 f32=20,f32x2=100,f64+rm+rn+rp+rz=13,f64=13 RXX;f32;ftz,rm,rn,rp,rz,sat;;1 RXX;f32x2;ftz,rm,rn,rp,rz;;1 RXX;f64;rm,rn,rp,rz;;1\n4.2 53 bf16=90,bf16x2=90 RXX;f16;ftz,rn,sat;;1 RXX;f16x2;ftz,rn,sat;;1 RXX;bf16;rn;;1 RXX;bf16x2;rn;;1\n1.0 100 bf16+f32=100,f16+f32=100 RXX;f32/bf16,f16;rm,rn,rp,rz,sat;;1"
        )
    case SubCc extends Definition(
          "sub.cc",
          Opcode.Sub,
          Vec(Modifier.Cc),
          (operands, _) => {
            if (operands.size == 3) Right(Form.SubCc(dest = operands(0), srcA = operands(1), srcB = operands(2)))
            else Left(DecodeError("sub.cc", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "1.0 20 cc=20 RXX;s32,s64,u32,u64;;;1"
        )
    case Subc extends Definition(
          "subc",
          Opcode.Subc,
          Vec(),
          (operands, _) => {
            if (operands.size == 3) Right(Form.Subc(dest = operands(0), srcA = operands(1), srcB = operands(2)))
            else Left(DecodeError("subc", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "1.0 20  RXX;s32,s64,u32,u64;cc;;1"
        )
    case Suld extends Definition(
          "suld",
          Opcode.Suld,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Suld(operands(0), operands(1)))
            else Left(DecodeError("suld", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "1.0 20 a1d=20,a2d=20 RA;b/1d,2d,3d,a1d,a2d/none,v2,v4/b16,b32,b64,b8/clamp,trap,zero;ca,cg,cs,cv;;1"
        )
    case Suq extends Definition(
          "suq",
          Opcode.Suq,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Suq(operands(0), operands(1)))
            else Left(DecodeError("suq", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "1.5 10  RA;array_size,channel_data_type,channel_order,depth,height,memory_layout,width/b32;;;1"
        )
    case Sured extends Definition(
          "sured",
          Opcode.Sured,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Sured(operands(0), operands(1)))
            else Left(DecodeError("sured", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "2.0 20 b64=50,max=50,min=50 AX;b/add,and,max,min,or/1d,2d,3d/b32,b64/clamp,trap,zero;;;1 AX;p/add,and,max,min,or/1d,2d,3d/b32,b64/clamp,trap,zero;;;1"
        )
    case Sust extends Definition(
          "sust",
          Opcode.Sust,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Sust(operands(0), operands(1)))
            else Left(DecodeError("sust", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "1.0 20 p=20 AX;b/none,v2,v4/b16,b32,b64,b8/clamp,trap,zero;cg,cs,wb,wt;;0 AX;p/none,v2,v4/b32/clamp,trap,zero;;;0"
        )
    case Szext extends Definition(
          "szext",
          Opcode.Szext,
          Vec(),
          (operands, _) => {
            if (operands.size == 3) Right(Form.Szext(dest = operands(0), srcA = operands(1), srcB = operands(2)))
            else Left(DecodeError("szext", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "7.6 70  RXX;clamp,wrap/s32,u32;;;1"
        )
    case Tanh extends Definition(
          "tanh",
          Opcode.Tanh,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Tanh(operands(0), operands(1)))
            else Left(DecodeError("tanh", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "7.0 75  RX;approx/f32;;;1\n7.0 75 approx+bf16=90,approx+bf16x2=90 RX;approx/bf16,bf16x2,f16,f16x2;;;1"
        )
    case Tcgen05Alloc extends Definition(
          "tcgen05.alloc",
          Opcode.Tcgen05,
          Vec(Modifier.Alloc),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Tcgen05Alloc(operands(0), operands(1)))
            else Left(DecodeError("tcgen05.alloc", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "8.6 100  AX;cta_group::1,cta_group::2/sync/aligned/b32;shared::cta;;1"
        )
    case Tcgen05Commit extends Definition(
          "tcgen05.commit",
          Opcode.Tcgen05,
          Vec(Modifier.Commit),
          (operands, _) => {
            if (operands.size >= 1 && operands.size <= 2) Right(Form.Tcgen05Commit(operands(0), operands.lift(1)))
            else Left(DecodeError("tcgen05.commit", DecodeProblem.InvalidOperandCount(Vec(1, 2), operands.size)))
          },
          "8.6 100  AX;cta_group::1,cta_group::2/mbarrier::arrive::one/b64;multicast::cluster,shared::cluster;;1 A;cta_group::1,cta_group::2/mbarrier::arrive::one/b64;multicast::cluster,shared::cluster;;1"
        )
    case Tcgen05Cp extends Definition(
          "tcgen05.cp",
          Opcode.Tcgen05,
          Vec(Modifier.Cp),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Tcgen05Cp(operands(0), operands(1)))
            else Left(DecodeError("tcgen05.cp", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "8.6 100  AX;cta_group::1,cta_group::2/128x128b,128x256b,4x256b;b4x16_p64,b6x16_p32,b8x16;;0"
        )
    case Tcgen05Dealloc extends Definition(
          "tcgen05.dealloc",
          Opcode.Tcgen05,
          Vec(Modifier.Dealloc),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Tcgen05Dealloc(operands(0), operands(1)))
            else Left(DecodeError("tcgen05.dealloc", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "8.6 100  XX;cta_group::1,cta_group::2/sync/aligned/b32;;;1"
        )
    case Tcgen05FenceAfterThreadSync extends Definition(
          "tcgen05.fence::after_thread_sync",
          Opcode.Tcgen05,
          Vec(Modifier.FenceAfterThreadSync),
          (operands, _) => {
            if (operands.isEmpty) Right(Form.Tcgen05FenceAfterThreadSync())
            else Left(DecodeError(
              "tcgen05.fence::after_thread_sync",
              DecodeProblem.InvalidOperandCount(Vec(0), operands.size)
            ))
          },
          "8.6 100  ;;;;1"
        )
    case Tcgen05FenceBeforeThreadSync extends Definition(
          "tcgen05.fence::before_thread_sync",
          Opcode.Tcgen05,
          Vec(Modifier.FenceBeforeThreadSync),
          (operands, _) => {
            if (operands.isEmpty) Right(Form.Tcgen05FenceBeforeThreadSync())
            else Left(DecodeError(
              "tcgen05.fence::before_thread_sync",
              DecodeProblem.InvalidOperandCount(Vec(0), operands.size)
            ))
          },
          "8.6 100  ;;;;1"
        )
    case Tcgen05Ld extends Definition(
          "tcgen05.ld",
          Opcode.Tcgen05,
          Vec(Modifier.Ld),
          (operands, _) => {
            if (operands.size >= 2 && operands.size <= 4) Right(Form.Tcgen05Ld(
              destRegs = operands(0),
              reductionValue = operands(1),
              address = operands.lift(2),
              halfSplitOffset = operands.lift(3)
            ))
            else Left(DecodeError("tcgen05.ld", DecodeProblem.InvalidOperandCount(Vec(2, 3, 4), operands.size)))
          },
          "8.6 100  RA;sync/aligned/16x128b,16x256b,16x64b,32x32b/x1,x128,x16,x2,x32,x4,x64,x8/b32;pack::16b;;1 RAX;sync/aligned/16x32bx2/x1,x128,x16,x2,x32,x4,x64,x8/b32;pack::16b;;1 RXA;red/sync/aligned/32x32b/x1,x128,x16,x2,x32,x4,x64,x8/max,min/f32;NaN,abs;;1 RXAX;red/sync/aligned/16x32bx2/x1,x128,x16,x2,x32,x4,x64,x8/max,min/f32;NaN,abs;;1 RXA;red/sync/aligned/32x32b/x1,x128,x16,x2,x32,x4,x64,x8/max,min/s32,u32;;;1 RXAX;red/sync/aligned/16x32bx2/x1,x128,x16,x2,x32,x4,x64,x8/max,min/s32,u32;;;1"
        )
    case Tcgen05Mma extends Definition(
          "tcgen05.mma",
          Opcode.Tcgen05,
          Vec(Modifier.Mma),
          (operands, _) => {
            if (operands.size >= 5 && operands.size <= 7) Right(if (operands.size == 5) Form.Tcgen05Mma(
              destAddr = operands(0),
              srcA = operands(1),
              srcBDesc = operands(2),
              instDesc = operands(3),
              scaleAAddr = None,
              scaleBAddr = None,
              disabledOutputLanes = None,
              enableInputD = operands(4),
              scaleInputD = None
            )
            else if (Operand.isAddress(operands(4))) Form.Tcgen05Mma(
              destAddr = operands(0),
              srcA = operands(1),
              srcBDesc = operands(2),
              instDesc = operands(3),
              scaleAAddr = Some(operands(4)),
              scaleBAddr = Some(operands(5)),
              disabledOutputLanes = None,
              enableInputD = operands(6),
              scaleInputD = None
            )
            else Form.Tcgen05Mma(
              destAddr = operands(0),
              srcA = operands(1),
              srcBDesc = operands(2),
              instDesc = operands(3),
              scaleAAddr = None,
              scaleBAddr = None,
              disabledOutputLanes = Some(operands(4)),
              enableInputD = operands(5),
              scaleInputD = operands.lift(6)
            ))
            else Left(DecodeError("tcgen05.mma", DecodeProblem.InvalidOperandCount(Vec(5, 6, 7), operands.size)))
          },
          "8.6 100 kind::i8=100 AXXXVXX;cta_group::1,cta_group::2/kind::f16,kind::f8f6f4,kind::tf32;;;1 AXXXVX;cta_group::1,cta_group::2/kind::f16,kind::f8f6f4,kind::tf32;;;1 AAXXVXX;cta_group::1,cta_group::2/kind::f16,kind::f8f6f4,kind::tf32;;;1 AAXXVX;cta_group::1,cta_group::2/kind::f16,kind::f8f6f4,kind::tf32;;;1 AXXXAAX;cta_group::1,cta_group::2/kind::mxf4,kind::mxf4nvf4,kind::mxf8f6f4/block_scale;block16,block32,scale_vec::1X,scale_vec::2X,scale_vec::4X;;1 AAXXAAX;cta_group::1,cta_group::2/kind::mxf4,kind::mxf4nvf4,kind::mxf8f6f4/block_scale;block16,block32,scale_vec::1X,scale_vec::2X,scale_vec::4X;;1 AXXXVXX;cta_group::1,cta_group::2/kind::f16,kind::f8f6f4,kind::tf32/collector::buffer::op;;;1 AXXXVX;cta_group::1,cta_group::2/kind::f16,kind::f8f6f4,kind::tf32/collector::buffer::op;;;1 AAXXVXX;cta_group::1,cta_group::2/kind::f16,kind::f8f6f4,kind::tf32/collector::buffer::op;ashift;;1 AAXXVX;cta_group::1,cta_group::2/kind::f16,kind::f8f6f4,kind::tf32/collector::buffer::op;ashift;;1 AAXXVXX;cta_group::1,cta_group::2/kind::f16,kind::f8f6f4,kind::tf32/ashift;collector::buffer::op;;1 AAXXVX;cta_group::1,cta_group::2/kind::f16,kind::f8f6f4,kind::tf32/ashift;collector::buffer::op;;1 AXXXAAX;cta_group::1,cta_group::2/kind::mxf4,kind::mxf4nvf4,kind::mxf8f6f4/block_scale/collector::buffer::op;block16,block32,scale_vec::1X,scale_vec::2X,scale_vec::4X;;1 AAXXAAX;cta_group::1,cta_group::2/kind::mxf4,kind::mxf4nvf4,kind::mxf8f6f4/block_scale/collector::buffer::op;block16,block32,scale_vec::1X,scale_vec::2X,scale_vec::4X;;1 AXXXVX;cta_group::1,cta_group::2/kind::i8;;;1 AAXXVX;cta_group::1,cta_group::2/kind::i8;;;1 AXXXVX;cta_group::1,cta_group::2/kind::i8/collector::buffer::op;;;1 AAXXVX;cta_group::1,cta_group::2/kind::i8/ashift;collector::buffer::op;;1 AAXXVX;cta_group::1,cta_group::2/kind::i8/collector::buffer::op;ashift;;1"
        )
    case Tcgen05MmaSp extends Definition(
          "tcgen05.mma.sp",
          Opcode.Tcgen05,
          Vec(Modifier.Mma, Modifier.Sp),
          (operands, _) => {
            if (operands.size >= 6 && operands.size <= 8) Right(if (operands.size == 6) Form.Tcgen05MmaSp(
              destAddr = operands(0),
              srcA = operands(1),
              srcBDesc = operands(2),
              sparseMetadataAddr = operands(3),
              instDesc = operands(4),
              scaleAAddr = None,
              scaleBAddr = None,
              disabledOutputLanes = None,
              enableInputD = operands(5),
              scaleInputD = None
            )
            else if (Operand.isAddress(operands(5))) Form.Tcgen05MmaSp(
              destAddr = operands(0),
              srcA = operands(1),
              srcBDesc = operands(2),
              sparseMetadataAddr = operands(3),
              instDesc = operands(4),
              scaleAAddr = Some(operands(5)),
              scaleBAddr = Some(operands(6)),
              disabledOutputLanes = None,
              enableInputD = operands(7),
              scaleInputD = None
            )
            else Form.Tcgen05MmaSp(
              destAddr = operands(0),
              srcA = operands(1),
              srcBDesc = operands(2),
              sparseMetadataAddr = operands(3),
              instDesc = operands(4),
              scaleAAddr = None,
              scaleBAddr = None,
              disabledOutputLanes = Some(operands(5)),
              enableInputD = operands(6),
              scaleInputD = operands.lift(7)
            ))
            else Left(DecodeError("tcgen05.mma.sp", DecodeProblem.InvalidOperandCount(Vec(6, 7, 8), operands.size)))
          },
          "8.6 100 kind::i8=100,kind::mxf4=100,kind::mxf4nvf4=100 AXXAXVXX;cta_group::1,cta_group::2/kind::f16,kind::f8f6f4,kind::tf32;;;1 AXXAXVX;cta_group::1,cta_group::2/kind::f16,kind::f8f6f4,kind::tf32;;;1 AAXAXVXX;cta_group::1,cta_group::2/kind::f16,kind::f8f6f4,kind::tf32;;;1 AAXAXVX;cta_group::1,cta_group::2/kind::f16,kind::f8f6f4,kind::tf32;;;1 AXXAXAAX;cta_group::1,cta_group::2/kind::mxf4,kind::mxf4nvf4,kind::mxf8f6f4/block_scale;block16,block32,scale_vec::1X,scale_vec::2X,scale_vec::4X;;1 AAXAXAAX;cta_group::1,cta_group::2/kind::mxf4,kind::mxf4nvf4,kind::mxf8f6f4/block_scale;block16,block32,scale_vec::1X,scale_vec::2X,scale_vec::4X;;1 AXXAXVXX;cta_group::1,cta_group::2/kind::f16,kind::f8f6f4,kind::tf32/collector::buffer::op;;;1 AXXAXVX;cta_group::1,cta_group::2/kind::f16,kind::f8f6f4,kind::tf32/collector::buffer::op;;;1 AAXAXVXX;cta_group::1,cta_group::2/kind::f16,kind::f8f6f4,kind::tf32/ashift;collector::buffer::op;;1 AAXAXVX;cta_group::1,cta_group::2/kind::f16,kind::f8f6f4,kind::tf32/ashift;collector::buffer::op;;1 AAXAXVXX;cta_group::1,cta_group::2/kind::f16,kind::f8f6f4,kind::tf32/collector::buffer::op;ashift;;1 AAXAXVX;cta_group::1,cta_group::2/kind::f16,kind::f8f6f4,kind::tf32/collector::buffer::op;ashift;;1 AXXAXAAX;cta_group::1,cta_group::2/kind::mxf4,kind::mxf4nvf4,kind::mxf8f6f4/block_scale/collector::buffer::op;block16,block32,scale_vec::1X,scale_vec::2X,scale_vec::4X;;1 AAXAXAAX;cta_group::1,cta_group::2/kind::mxf4,kind::mxf4nvf4,kind::mxf8f6f4/block_scale/collector::buffer::op;block16,block32,scale_vec::1X,scale_vec::2X,scale_vec::4X;;1 AXXAXVX;cta_group::1,cta_group::2/kind::i8;;;1 AAXAXVX;cta_group::1,cta_group::2/kind::i8;;;1 AXXAXVX;cta_group::1,cta_group::2/kind::i8/collector::buffer::op;;;1 AAXAXVX;cta_group::1,cta_group::2/kind::i8/ashift;collector::buffer::op;;1 AAXAXVX;cta_group::1,cta_group::2/kind::i8/collector::buffer::op;ashift;;1"
        )
    case Tcgen05MmaWs extends Definition(
          "tcgen05.mma.ws",
          Opcode.Tcgen05,
          Vec(Modifier.Mma, Modifier.Ws),
          (operands, _) => {
            if (operands.size >= 5 && operands.size <= 6) Right(Form.Tcgen05MmaWs(
              destAddr = operands(0),
              srcA = operands(1),
              srcBDesc = operands(2),
              instDesc = operands(3),
              enableInputD = operands(4),
              zeroColumnMaskDesc = operands.lift(5)
            ))
            else Left(DecodeError("tcgen05.mma.ws", DecodeProblem.InvalidOperandCount(Vec(5, 6), operands.size)))
          },
          "8.6 100 kind::i8=100 AXXXXX;cta_group::1/kind::f16,kind::f8f6f4,kind::tf32;collector::buffer::op;;1 AXXXX;cta_group::1/kind::f16,kind::f8f6f4,kind::tf32;collector::buffer::op;;1 AAXXXX;cta_group::1/kind::f16,kind::f8f6f4,kind::tf32;collector::buffer::op;;1 AAXXX;cta_group::1/kind::f16,kind::f8f6f4,kind::tf32;collector::buffer::op;;1 AXXXXX;cta_group::1/kind::i8;collector::buffer::op;;1 AXXXX;cta_group::1/kind::i8;collector::buffer::op;;1 AAXXXX;cta_group::1/kind::i8;collector::buffer::op;;1 AAXXX;cta_group::1/kind::i8;collector::buffer::op;;1"
        )
    case Tcgen05MmaWsSp extends Definition(
          "tcgen05.mma.ws.sp",
          Opcode.Tcgen05,
          Vec(Modifier.Mma, Modifier.Ws, Modifier.Sp),
          (operands, _) => {
            if (operands.size >= 6 && operands.size <= 7) Right(Form.Tcgen05MmaWsSp(
              destAddr = operands(0),
              srcA = operands(1),
              srcBDesc = operands(2),
              sparseMetadataAddr = operands(3),
              instDesc = operands(4),
              enableInputD = operands(5),
              zeroColumnMaskDesc = operands.lift(6)
            ))
            else Left(DecodeError("tcgen05.mma.ws.sp", DecodeProblem.InvalidOperandCount(Vec(6, 7), operands.size)))
          },
          "8.6 100 kind::i8=100 AXXAXXX;cta_group::1/kind::f16,kind::f8f6f4,kind::tf32;collector::buffer::op;;1 AXXAXX;cta_group::1/kind::f16,kind::f8f6f4,kind::tf32;collector::buffer::op;;1 AAXAXXX;cta_group::1/kind::f16,kind::f8f6f4,kind::tf32;collector::buffer::op;;1 AAXAXX;cta_group::1/kind::f16,kind::f8f6f4,kind::tf32;collector::buffer::op;;1 AXXAXXX;cta_group::1/kind::i8;collector::buffer::op;;1 AXXAXX;cta_group::1/kind::i8;collector::buffer::op;;1 AAXAXXX;cta_group::1/kind::i8;collector::buffer::op;;1 AAXAXX;cta_group::1/kind::i8;collector::buffer::op;;1"
        )
    case Tcgen05RelinquishAllocPermit extends Definition(
          "tcgen05.relinquish_alloc_permit",
          Opcode.Tcgen05,
          Vec(Modifier.RelinquishAllocPermit),
          (operands, _) => {
            if (operands.isEmpty) Right(Form.Tcgen05RelinquishAllocPermit())
            else Left(DecodeError(
              "tcgen05.relinquish_alloc_permit",
              DecodeProblem.InvalidOperandCount(Vec(0), operands.size)
            ))
          },
          "8.6 100  ;cta_group::1,cta_group::2/sync/aligned;;;1"
        )
    case Tcgen05Shift extends Definition(
          "tcgen05.shift",
          Opcode.Tcgen05,
          Vec(Modifier.Shift),
          (operands, _) => {
            if (operands.size == 1) Right(Form.Tcgen05Shift(operands(0)))
            else Left(DecodeError("tcgen05.shift", DecodeProblem.InvalidOperandCount(Vec(1), operands.size)))
          },
          "8.6 10  A;cta_group::1,cta_group::2/down;;;1"
        )
    case Tcgen05St extends Definition(
          "tcgen05.st",
          Opcode.Tcgen05,
          Vec(Modifier.St),
          (operands, _) => {
            if (operands.size >= 2 && operands.size <= 3)
              Right(Form.Tcgen05St(address = operands(0), halfSplitOffset = operands(1), srcRegs = operands.lift(2)))
            else Left(DecodeError("tcgen05.st", DecodeProblem.InvalidOperandCount(Vec(2, 3), operands.size)))
          },
          "8.6 100  AX;sync/aligned/16x128b,16x256b,16x64b,32x32b/x1,x128,x16,x2,x32,x4,x64,x8/b32;unpack::16b;;1 AXX;sync/aligned/16x32bx2/x1,x128,x16,x2,x32,x4,x64,x8/b32;unpack::16b;;1"
        )
    case Tcgen05WaitLd extends Definition(
          "tcgen05.wait::ld",
          Opcode.Tcgen05,
          Vec(Modifier.WaitLd),
          (operands, _) => {
            if (operands.isEmpty) Right(Form.Tcgen05WaitLd())
            else Left(DecodeError("tcgen05.wait::ld", DecodeProblem.InvalidOperandCount(Vec(0), operands.size)))
          },
          "8.6 100  ;sync/aligned;;;1"
        )
    case Tcgen05WaitSt extends Definition(
          "tcgen05.wait::st",
          Opcode.Tcgen05,
          Vec(Modifier.WaitSt),
          (operands, _) => {
            if (operands.isEmpty) Right(Form.Tcgen05WaitSt())
            else Left(DecodeError("tcgen05.wait::st", DecodeProblem.InvalidOperandCount(Vec(0), operands.size)))
          },
          "8.6 100  ;sync/aligned;;;1"
        )
    case TensorMapCopyFenceProxy extends Definition(
          "tensormap.cp_fenceproxy",
          Opcode.Tensormap,
          Vec(Modifier.CpFenceproxy),
          (operands, _) => {
            if (operands.size == 3) Right(Form.TensorMapCopyFenceProxy(
              dest = operands(0),
              srcOrOrdinal = operands(1),
              valueOrSize = operands(2)
            ))
            else Left(DecodeError("tensormap.cp_fenceproxy", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "8.3 90  AAX;sync/aligned;;;1"
        )
    case TensorMapReplace extends Definition(
          "tensormap.replace",
          Opcode.Tensormap,
          Vec(Modifier.Replace),
          (operands, _) => {
            if (operands.size >= 2 && operands.size <= 3) Right(Form.TensorMapReplace(
              dest = operands(0),
              srcOrOrdinal = operands(1),
              valueOrSize = operands.lift(2)
            ))
            else Left(DecodeError("tensormap.replace", DecodeProblem.InvalidOperandCount(Vec(2, 3), operands.size)))
          },
          "8.3 100  AX;tile/global_address,rank/b1024/b32,b64;global,shared::cta;;1 AXX;tile/box_dim,element_stride,global_dim,global_stride/b1024/b32,b64;global,shared::cta;;1 AX;tile/elemtype,fill_mode,interleave_layout,swizzle_atomicity,swizzle_mode/b1024/b32,b64;global,shared::cta;;1"
        )
    case TestPredicate extends Definition(
          "testp",
          Opcode.TestPredicate,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.TestPredicate(operands(0), operands(1)))
            else Left(DecodeError("testp", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "2.0 20  RX;finite,infinite,normal,notanumber,number,subnormal/f32,f64;;;1"
        )
    case Tex extends Definition(
          "tex",
          Opcode.Tex,
          Vec(),
          (operands, _) => {
            if (operands.size >= 2 && operands.size <= 6) Right(Form.Tex(
              dest = operands(0),
              textureAndCoordinates = operands(1),
              lodOffsetOrDerivativeX = operands.lift(2),
              depthCompareOrDerivativeY = operands.lift(3),
              offset = operands.lift(4),
              depthCompare = operands.lift(5)
            ))
            else Left(DecodeError("tex", DecodeProblem.InvalidOperandCount(Vec(2, 3, 4, 5, 6), operands.size)))
          },
          "1.0 10 acube+grad=20,acube=20,cube+grad=20,f16=53,f16x2=53 RAXX;1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v4/f16,f32,s32,u32/f32,s32;;;1 RAX;1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v4/f16,f32,s32,u32/f32,s32;;;1 RA;1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v4/f16,f32,s32,u32/f32,s32;;;1 RAXX;1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v2/f16x2/f32,s32;;;1 RAX;1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v2/f16x2/f32,s32;;;1 RA;1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v2/f16x2/f32,s32;;;1 RAXX;base/1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v4/f16,f32,s32,u32/f32,s32;;;1 RAX;base/1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v4/f16,f32,s32,u32/f32,s32;;;1 RA;base/1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v4/f16,f32,s32,u32/f32,s32;;;1 RAXXX;level/1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v4/f16,f32,s32,u32/f32,s32;;;1 RAXX;level/1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v4/f16,f32,s32,u32/f32,s32;;;1 RAX;level/1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v4/f16,f32,s32,u32/f32,s32;;;1 RAXXXX;grad/1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v4/f16,f32,s32,u32/f32,s32;;;1 RAXXX;grad/1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v4/f16,f32,s32,u32/f32,s32;;;1 RAXX;grad/1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v4/f16,f32,s32,u32/f32,s32;;;1 RAXX;base/1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v2/f16x2/f32,s32;;;1 RAX;base/1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v2/f16x2/f32,s32;;;1 RA;base/1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v2/f16x2/f32,s32;;;1 RAXXX;level/1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v2/f16x2/f32,s32;;;1 RAXX;level/1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v2/f16x2/f32,s32;;;1 RAX;level/1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v2/f16x2/f32,s32;;;1 RAXXXX;grad/1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v2/f16x2/f32,s32;;;1 RAXXX;grad/1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v2/f16x2/f32,s32;;;1 RAXX;grad/1d,2d,2dms,3d,a1d,a2d,a2dms,acube,cube/v2/f16x2/f32,s32;;;1"
        )
    case Tld4 extends Definition(
          "tld4",
          Opcode.Tld4,
          Vec(),
          (operands, _) => {
            if (operands.size >= 2 && operands.size <= 4) Right(Form.Tld4(
              dest = operands(0),
              textureAndCoordinates = operands(1),
              offset = operands.lift(2),
              depthCompare = operands.lift(3)
            ))
            else Left(DecodeError("tld4", DecodeProblem.InvalidOperandCount(Vec(2, 3, 4), operands.size)))
          },
          "2.2 20 a2d=30,acube=30,cube=30 RAXX;a,b,g,r/v4/f32,s32,u32/f32;;;0 RAX;a,b,g,r/v4/f32,s32,u32/f32;;;0 RA;a,b,g,r/v4/f32,s32,u32/f32;;;0 RAXX;a,b,g,r/2d,a2d,acube,cube/v4/f32,s32,u32/f32;;;1 RAX;a,b,g,r/2d,a2d,acube,cube/v4/f32,s32,u32/f32;;;1 RA;a,b,g,r/2d,a2d,acube,cube/v4/f32,s32,u32/f32;;;1"
        )
    case Trap extends Definition(
          "trap",
          Opcode.Trap,
          Vec(),
          (operands, _) => {
            if (operands.isEmpty) Right(Form.Trap())
            else Left(DecodeError("trap", DecodeProblem.InvalidOperandCount(Vec(0), operands.size)))
          },
          "1.0 10  ;;;;1"
        )
    case Txq extends Definition(
          "txq",
          Opcode.Txq,
          Vec(),
          (operands, _) => {
            if (operands.size >= 2 && operands.size <= 3)
              Right(Form.Txq(dest = operands(0), texture = operands(1), lod = operands.lift(2)))
            else Left(DecodeError("txq", DecodeProblem.InvalidOperandCount(Vec(2, 3), operands.size)))
          },
          "1.5 10 level=30 RA;array_size,channel_data_type,channel_order,depth,height,normalized_coords,num_mipmap_levels,num_samples,width/b32;;;1 RAX;level/depth,height,width/b32;;;1 RA;addr_mode_0,addr_mode_1,addr_mode_2,filter_mode,force_unnormalized_coords/b32;;;1"
        )
    case Vabsdiff extends Definition(
          "vabsdiff",
          Opcode.Vabsdiff,
          Vec(),
          (operands, _) => {
            if (operands.size >= 3 && operands.size <= 4)
              Right(Form.Vabsdiff(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands.lift(3)))
            else Left(DecodeError("vabsdiff", DecodeProblem.InvalidOperandCount(Vec(3, 4), operands.size)))
          },
          "2.0 20  RXX;s32,u32/s32,u32/s32,u32;sat;;1 RXXX;s32,u32/s32,u32/s32,u32/add,max,min;sat;;1 RXXX;s32,u32/s32,u32/s32,u32;sat;;1"
        )
    case Vabsdiff2 extends Definition(
          "vabsdiff2",
          Opcode.Vabsdiff2,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Vabsdiff2(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("vabsdiff2", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "3.0 30  RXXX;s32,u32/s32,u32/s32,u32;sat;;1 RXXX;s32,u32/s32,u32/s32,u32/add;;;1"
        )
    case Vabsdiff4 extends Definition(
          "vabsdiff4",
          Opcode.Vabsdiff4,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Vabsdiff4(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("vabsdiff4", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "3.0 30  RXXX;s32,u32/s32,u32/s32,u32;sat;;1 RXXX;s32,u32/s32,u32/s32,u32/add;;;1"
        )
    case Vadd extends Definition(
          "vadd",
          Opcode.Vadd,
          Vec(),
          (operands, _) => {
            if (operands.size >= 3 && operands.size <= 4)
              Right(Form.Vadd(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands.lift(3)))
            else Left(DecodeError("vadd", DecodeProblem.InvalidOperandCount(Vec(3, 4), operands.size)))
          },
          "2.0 20  RXX;s32,u32/s32,u32/s32,u32;sat;;1 RXXX;s32,u32/s32,u32/s32,u32/add,max,min;sat;;1 RXXX;s32,u32/s32,u32/s32,u32;sat;;1"
        )
    case Vadd2 extends Definition(
          "vadd2",
          Opcode.Vadd2,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Vadd2(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("vadd2", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "3.0 30  RXXX;s32,u32/s32,u32/s32,u32;sat;;1 RXXX;s32,u32/s32,u32/s32,u32/add;;;1"
        )
    case Vadd4 extends Definition(
          "vadd4",
          Opcode.Vadd4,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Vadd4(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("vadd4", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "3.0 30  RXXX;s32,u32/s32,u32/s32,u32;sat;;1 RXXX;s32,u32/s32,u32/s32,u32/add;;;1"
        )
    case Vavrg2 extends Definition(
          "vavrg2",
          Opcode.Vavrg2,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Vavrg2(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("vavrg2", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "3.0 30  RXXX;s32,u32/s32,u32/s32,u32;sat;;1 RXXX;s32,u32/s32,u32/s32,u32/add;;;1"
        )
    case Vavrg4 extends Definition(
          "vavrg4",
          Opcode.Vavrg4,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Vavrg4(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("vavrg4", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "3.0 30  RXXX;s32,u32/s32,u32/s32,u32;sat;;1 RXXX;s32,u32/s32,u32/s32,u32/add;;;1"
        )
    case Vmad extends Definition(
          "vmad",
          Opcode.Vmad,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Vmad(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("vmad", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "2.0 20  RXXX;s32,u32/s32,u32/s32,u32;sat,shr15,shr7;;1 RXXX;s32,u32/s32,u32/s32,u32/po;sat,shr15,shr7;;1"
        )
    case Vmax extends Definition(
          "vmax",
          Opcode.Vmax,
          Vec(),
          (operands, _) => {
            if (operands.size >= 3 && operands.size <= 4)
              Right(Form.Vmax(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands.lift(3)))
            else Left(DecodeError("vmax", DecodeProblem.InvalidOperandCount(Vec(3, 4), operands.size)))
          },
          "2.0 20  RXX;s32,u32/s32,u32/s32,u32;sat;;1 RXXX;s32,u32/s32,u32/s32,u32/add,max,min;sat;;1 RXXX;s32,u32/s32,u32/s32,u32;sat;;1"
        )
    case Vmax2 extends Definition(
          "vmax2",
          Opcode.Vmax2,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Vmax2(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("vmax2", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "3.0 30  RXXX;s32,u32/s32,u32/s32,u32;sat;;1 RXXX;s32,u32/s32,u32/s32,u32/add;;;1"
        )
    case Vmax4 extends Definition(
          "vmax4",
          Opcode.Vmax4,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Vmax4(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("vmax4", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "3.0 30  RXXX;s32,u32/s32,u32/s32,u32;sat;;1 RXXX;s32,u32/s32,u32/s32,u32/add;;;1"
        )
    case Vmin extends Definition(
          "vmin",
          Opcode.Vmin,
          Vec(),
          (operands, _) => {
            if (operands.size >= 3 && operands.size <= 4)
              Right(Form.Vmin(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands.lift(3)))
            else Left(DecodeError("vmin", DecodeProblem.InvalidOperandCount(Vec(3, 4), operands.size)))
          },
          "2.0 20  RXX;s32,u32/s32,u32/s32,u32;sat;;1 RXXX;s32,u32/s32,u32/s32,u32/add,max,min;sat;;1 RXXX;s32,u32/s32,u32/s32,u32;sat;;1"
        )
    case Vmin2 extends Definition(
          "vmin2",
          Opcode.Vmin2,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Vmin2(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("vmin2", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "3.0 30  RXXX;s32,u32/s32,u32/s32,u32;sat;;1 RXXX;s32,u32/s32,u32/s32,u32/add;;;1"
        )
    case Vmin4 extends Definition(
          "vmin4",
          Opcode.Vmin4,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Vmin4(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("vmin4", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "3.0 30  RXXX;s32,u32/s32,u32/s32,u32;sat;;1 RXXX;s32,u32/s32,u32/s32,u32/add;;;1"
        )
    case Vote extends Definition(
          "vote",
          Opcode.Vote,
          Vec(),
          (operands, _) => {
            if (operands.size == 2) Right(Form.Vote(operands(0), operands(1)))
            else Left(DecodeError("vote", DecodeProblem.InvalidOperandCount(Vec(2), operands.size)))
          },
          "1.2 12 b32+ballot=20 RX;all,any,uni/pred;;;1 RX;ballot/b32;;;1"
        )
    case VoteSync extends Definition(
          "vote.sync",
          Opcode.Vote,
          Vec(Modifier.Sync),
          (operands, _) => {
            if (operands.size == 3)
              Right(Form.VoteSync(dest = operands(0), srcA = operands(1), memberMask = operands(2)))
            else Left(DecodeError("vote.sync", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "6.0 30  RXX;all,any,uni/pred;;;1 RXX;ballot/b32;;;1"
        )
    case Vset extends Definition(
          "vset",
          Opcode.Vset,
          Vec(),
          (operands, _) => {
            if (operands.size >= 3 && operands.size <= 4)
              Right(Form.Vset(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands.lift(3)))
            else Left(DecodeError("vset", DecodeProblem.InvalidOperandCount(Vec(3, 4), operands.size)))
          },
          "2.0 20  RXX;s32,u32/s32,u32/eq,ge,gt,le,lt,ne;;;1 RXXX;s32,u32/s32,u32/eq,ge,gt,le,lt,ne/add,max,min;;;1 RXXX;s32,u32/s32,u32/eq,ge,gt,le,lt,ne;;;1"
        )
    case Vset2 extends Definition(
          "vset2",
          Opcode.Vset2,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Vset2(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("vset2", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "3.0 30  RXXX;s32,u32/s32,u32/eq,ge,gt,le,lt,ne;;;1 RXXX;s32,u32/s32,u32/eq,ge,gt,le,lt,ne/add;;;1"
        )
    case Vset4 extends Definition(
          "vset4",
          Opcode.Vset4,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Vset4(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("vset4", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "3.0 30  RXXX;s32,u32/s32,u32/eq,ge,gt,le,lt,ne;;;1 RXXX;s32,u32/s32,u32/eq,ge,gt,le,lt,ne/add;;;1"
        )
    case Vshl extends Definition(
          "vshl",
          Opcode.Vshl,
          Vec(),
          (operands, _) => {
            if (operands.size >= 3 && operands.size <= 4)
              Right(Form.Vshl(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands.lift(3)))
            else Left(DecodeError("vshl", DecodeProblem.InvalidOperandCount(Vec(3, 4), operands.size)))
          },
          "2.0 20  RXX;s32,u32/s32,u32/u32/clamp,wrap;sat;;1 RXXX;s32,u32/s32,u32/u32/clamp,wrap/add,max,min;sat;;1 RXXX;s32,u32/s32,u32/u32/clamp,wrap;sat;;1"
        )
    case Vshr extends Definition(
          "vshr",
          Opcode.Vshr,
          Vec(),
          (operands, _) => {
            if (operands.size >= 3 && operands.size <= 4)
              Right(Form.Vshr(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands.lift(3)))
            else Left(DecodeError("vshr", DecodeProblem.InvalidOperandCount(Vec(3, 4), operands.size)))
          },
          "2.0 20  RXX;s32,u32/s32,u32/u32/clamp,wrap;sat;;1 RXXX;s32,u32/s32,u32/u32/clamp,wrap/add,max,min;sat;;1 RXXX;s32,u32/s32,u32/u32/clamp,wrap;sat;;1"
        )
    case Vsub extends Definition(
          "vsub",
          Opcode.Vsub,
          Vec(),
          (operands, _) => {
            if (operands.size >= 3 && operands.size <= 4)
              Right(Form.Vsub(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands.lift(3)))
            else Left(DecodeError("vsub", DecodeProblem.InvalidOperandCount(Vec(3, 4), operands.size)))
          },
          "2.0 20  RXX;s32,u32/s32,u32/s32,u32;sat;;1 RXXX;s32,u32/s32,u32/s32,u32/add,max,min;sat;;1 RXXX;s32,u32/s32,u32/s32,u32;sat;;1"
        )
    case Vsub2 extends Definition(
          "vsub2",
          Opcode.Vsub2,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Vsub2(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("vsub2", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "3.0 30  RXXX;s32,u32/s32,u32/s32,u32;sat;;1 RXXX;s32,u32/s32,u32/s32,u32/add;;;1"
        )
    case Vsub4 extends Definition(
          "vsub4",
          Opcode.Vsub4,
          Vec(),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.Vsub4(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("vsub4", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "3.0 30  RXXX;s32,u32/s32,u32/s32,u32;sat;;1 RXXX;s32,u32/s32,u32/s32,u32/add;;;1"
        )
    case WgmmaCommitGroup extends Definition(
          "wgmma.commit_group",
          Opcode.Wgmma,
          Vec(Modifier.CommitGroup),
          (operands, _) => {
            if (operands.isEmpty) Right(Form.WgmmaCommitGroup())
            else Left(DecodeError("wgmma.commit_group", DecodeProblem.InvalidOperandCount(Vec(0), operands.size)))
          },
          "8.0 10  ;sync/aligned;;;1"
        )
    case WgmmaFence extends Definition(
          "wgmma.fence",
          Opcode.Wgmma,
          Vec(Modifier.Fence),
          (operands, _) => {
            if (operands.isEmpty) Right(Form.WgmmaFence())
            else Left(DecodeError("wgmma.fence", DecodeProblem.InvalidOperandCount(Vec(0), operands.size)))
          },
          "8.0 10  ;sync/aligned;;;1"
        )
    case WgmmaMmaAsync extends Definition(
          "wgmma.mma_async",
          Opcode.Wgmma,
          Vec(Modifier.MmaAsync),
          (operands, _) => {
            if (operands.size == 4 || operands.size >= 6 && operands.size <= 8) Right(Form.WgmmaMmaAsync(
              dest = operands(0),
              aDesc = operands(1),
              bDesc = operands(2),
              scaleD = operands(3),
              immScaleA = operands.lift(4),
              immScaleB = operands.lift(5),
              immTransA = operands.lift(6),
              immTransB = operands.lift(7)
            ))
            else Left(DecodeError("wgmma.mma_async", DecodeProblem.InvalidOperandCount(Vec(4, 6, 7, 8), operands.size)))
          },
          "8.0 10  RXXXXXXX;sync/aligned/m64n104k16,m64n112k16,m64n120k16,m64n128k16,m64n136k16,m64n144k16,m64n152k16,m64n160k16,m64n168k16,m64n16k16,m64n176k16,m64n184k16,m64n192k16,m64n200k16,m64n208k16,m64n216k16,m64n224k16,m64n232k16,m64n240k16,m64n248k16,m64n24k16,m64n256k16,m64n32k16,m64n40k16,m64n48k16,m64n56k16,m64n64k16,m64n72k16,m64n80k16,m64n88k16,m64n8k16,m64n96k16/f16,f32/f16/f16;;;1 RXXXXXX;sync/aligned/m64n104k16,m64n112k16,m64n120k16,m64n128k16,m64n136k16,m64n144k16,m64n152k16,m64n160k16,m64n168k16,m64n16k16,m64n176k16,m64n184k16,m64n192k16,m64n200k16,m64n208k16,m64n216k16,m64n224k16,m64n232k16,m64n240k16,m64n248k16,m64n24k16,m64n256k16,m64n32k16,m64n40k16,m64n48k16,m64n56k16,m64n64k16,m64n72k16,m64n80k16,m64n88k16,m64n8k16,m64n96k16/f16,f32/f16/f16;;;1 RXXXXXXX;sync/aligned/m64n104k16,m64n112k16,m64n120k16,m64n128k16,m64n136k16,m64n144k16,m64n152k16,m64n160k16,m64n168k16,m64n16k16,m64n176k16,m64n184k16,m64n192k16,m64n200k16,m64n208k16,m64n216k16,m64n224k16,m64n232k16,m64n240k16,m64n248k16,m64n24k16,m64n256k16,m64n32k16,m64n40k16,m64n48k16,m64n56k16,m64n64k16,m64n72k16,m64n80k16,m64n88k16,m64n8k16,m64n96k16/f32/bf16/bf16;;;1 RXXXXXX;sync/aligned/m64n104k16,m64n112k16,m64n120k16,m64n128k16,m64n136k16,m64n144k16,m64n152k16,m64n160k16,m64n168k16,m64n16k16,m64n176k16,m64n184k16,m64n192k16,m64n200k16,m64n208k16,m64n216k16,m64n224k16,m64n232k16,m64n240k16,m64n248k16,m64n24k16,m64n256k16,m64n32k16,m64n40k16,m64n48k16,m64n56k16,m64n64k16,m64n72k16,m64n80k16,m64n88k16,m64n8k16,m64n96k16/f32/bf16/bf16;;;1 RXXXXX;sync/aligned/m64n104k8,m64n112k8,m64n120k8,m64n128k8,m64n136k8,m64n144k8,m64n152k8,m64n160k8,m64n168k8,m64n16k8,m64n176k8,m64n184k8,m64n192k8,m64n200k8,m64n208k8,m64n216k8,m64n224k8,m64n232k8,m64n240k8,m64n248k8,m64n24k8,m64n256k8,m64n32k8,m64n40k8,m64n48k8,m64n56k8,m64n64k8,m64n72k8,m64n80k8,m64n88k8,m64n8k8,m64n96k8/f32/tf32/tf32;;;1 RXXXXX;sync/aligned/m64n104k32,m64n112k32,m64n120k32,m64n128k32,m64n136k32,m64n144k32,m64n152k32,m64n160k32,m64n168k32,m64n16k32,m64n176k32,m64n184k32,m64n192k32,m64n200k32,m64n208k32,m64n216k32,m64n224k32,m64n232k32,m64n240k32,m64n248k32,m64n24k32,m64n256k32,m64n32k32,m64n40k32,m64n48k32,m64n56k32,m64n64k32,m64n72k32,m64n80k32,m64n88k32,m64n8k32,m64n96k32/f16,f32/e4m3,e5m2/e4m3,e5m2;;;1 RXXX;sync/aligned/m64n112k32,m64n128k32,m64n144k32,m64n160k32,m64n16k32,m64n176k32,m64n192k32,m64n208k32,m64n224k32,m64n24k32,m64n32k32,m64n48k32,m64n64k32,m64n80k32,m64n8k32,m64n96k32/s32/s8,u8/s8,u8;satfinite;;1 RXXX;sync/aligned/m64n112k256,m64n128k256,m64n144k256,m64n160k256,m64n16k256,m64n176k256,m64n192k256,m64n208k256,m64n224k256,m64n240k256,m64n24k256,m64n256k256,m64n32k256,m64n48k256,m64n64k256,m64n80k256,m64n8k256,m64n96k256/s32/b1/b1/and/popc;;;1"
        )
    case WgmmaMmaAsyncSp extends Definition(
          "wgmma.mma_async.sp",
          Opcode.Wgmma,
          Vec(Modifier.MmaAsync, Modifier.Sp),
          (operands, _) => {
            if (operands.size == 6 || operands.size >= 8 && operands.size <= 10) Right(Form.WgmmaMmaAsyncSp(
              dest = operands(0),
              aDesc = operands(1),
              bDesc = operands(2),
              spMeta = operands(3),
              spSel = operands(4),
              scaleD = operands(5),
              immScaleA = operands.lift(6),
              immScaleB = operands.lift(7),
              immTransA = operands.lift(8),
              immTransB = operands.lift(9)
            ))
            else Left(DecodeError(
              "wgmma.mma_async.sp",
              DecodeProblem.InvalidOperandCount(Vec(6, 8, 9, 10), operands.size)
            ))
          },
          "8.2 10  RXXXXXXXXX;sync/aligned/m64n104k32,m64n112k32,m64n120k32,m64n128k32,m64n136k32,m64n144k32,m64n152k32,m64n160k32,m64n168k32,m64n16k32,m64n176k32,m64n184k32,m64n192k32,m64n200k32,m64n208k32,m64n216k32,m64n224k32,m64n232k32,m64n240k32,m64n248k32,m64n24k32,m64n256k32,m64n32k32,m64n40k32,m64n48k32,m64n56k32,m64n64k32,m64n72k32,m64n80k32,m64n88k32,m64n8k32,m64n96k32/f16,f32/f16/f16;;;1 RXXXXXXXX;sync/aligned/m64n104k32,m64n112k32,m64n120k32,m64n128k32,m64n136k32,m64n144k32,m64n152k32,m64n160k32,m64n168k32,m64n16k32,m64n176k32,m64n184k32,m64n192k32,m64n200k32,m64n208k32,m64n216k32,m64n224k32,m64n232k32,m64n240k32,m64n248k32,m64n24k32,m64n256k32,m64n32k32,m64n40k32,m64n48k32,m64n56k32,m64n64k32,m64n72k32,m64n80k32,m64n88k32,m64n8k32,m64n96k32/f16,f32/f16/f16;;;1 RXXXXXXXXX;sync/aligned/m64n104k32,m64n112k32,m64n120k32,m64n128k32,m64n136k32,m64n144k32,m64n152k32,m64n160k32,m64n168k32,m64n16k32,m64n176k32,m64n184k32,m64n192k32,m64n200k32,m64n208k32,m64n216k32,m64n224k32,m64n232k32,m64n240k32,m64n248k32,m64n24k32,m64n256k32,m64n32k32,m64n40k32,m64n48k32,m64n56k32,m64n64k32,m64n72k32,m64n80k32,m64n88k32,m64n8k32,m64n96k32/f32/bf16/bf16;;;1 RXXXXXXXX;sync/aligned/m64n104k32,m64n112k32,m64n120k32,m64n128k32,m64n136k32,m64n144k32,m64n152k32,m64n160k32,m64n168k32,m64n16k32,m64n176k32,m64n184k32,m64n192k32,m64n200k32,m64n208k32,m64n216k32,m64n224k32,m64n232k32,m64n240k32,m64n248k32,m64n24k32,m64n256k32,m64n32k32,m64n40k32,m64n48k32,m64n56k32,m64n64k32,m64n72k32,m64n80k32,m64n88k32,m64n8k32,m64n96k32/f32/bf16/bf16;;;1 RXXXXXXX;sync/aligned/m64n104k16,m64n112k16,m64n120k16,m64n128k16,m64n136k16,m64n144k16,m64n152k16,m64n160k16,m64n168k16,m64n16k16,m64n176k16,m64n184k16,m64n192k16,m64n200k16,m64n208k16,m64n216k16,m64n224k16,m64n232k16,m64n240k16,m64n248k16,m64n24k16,m64n256k16,m64n32k16,m64n40k16,m64n48k16,m64n56k16,m64n64k16,m64n72k16,m64n80k16,m64n88k16,m64n8k16,m64n96k16/f32/tf32/tf32;;;1 RXXXXXXX;sync/aligned/m64n104k64,m64n112k64,m64n120k64,m64n128k64,m64n136k64,m64n144k64,m64n152k64,m64n160k64,m64n168k64,m64n16k64,m64n176k64,m64n184k64,m64n192k64,m64n200k64,m64n208k64,m64n216k64,m64n224k64,m64n232k64,m64n240k64,m64n248k64,m64n24k64,m64n256k64,m64n32k64,m64n40k64,m64n48k64,m64n56k64,m64n64k64,m64n72k64,m64n80k64,m64n88k64,m64n8k64,m64n96k64/f16,f32/e4m3,e5m2/e4m3,e5m2;;;1 RXXXXX;sync/aligned/m64n112k64,m64n128k64,m64n144k64,m64n160k64,m64n16k64,m64n176k64,m64n192k64,m64n208k64,m64n224k64,m64n240k64,m64n24k64,m64n256k64,m64n32k64,m64n48k64,m64n64k64,m64n80k64,m64n8k64,m64n96k64/s32/s8,u8/s8,u8;satfinite;;1"
        )
    case WgmmaWaitGroup extends Definition(
          "wgmma.wait_group",
          Opcode.Wgmma,
          Vec(Modifier.WaitGroup),
          (operands, _) => {
            if (operands.size == 1) Right(Form.WgmmaWaitGroup(operands(0)))
            else Left(DecodeError("wgmma.wait_group", DecodeProblem.InvalidOperandCount(Vec(1), operands.size)))
          },
          "8.0 10  X;sync/aligned;;;1"
        )
    case WmmaLoad extends Definition(
          "wmma.load",
          Opcode.Wmma,
          Vec(Modifier.Load),
          (operands, _) => {
            if (operands.size >= 2 && operands.size <= 3)
              Right(Form.WmmaLoad(r = operands(0), address = operands(1), stride = operands.lift(2)))
            else Left(DecodeError("wmma.load", DecodeProblem.InvalidOperandCount(Vec(2, 3), operands.size)))
          },
          "6.0 70  RAX;a/sync/aligned/col,row/m16n16k16,m32n8k16,m8n32k16/f16,s8,u8;global,shared,shared::cta;;1 RA;a/sync/aligned/col,row/m16n16k16,m32n8k16,m8n32k16/f16,s8,u8;global,shared,shared::cta;;1 RAX;b/sync/aligned/col,row/m16n16k16,m32n8k16,m8n32k16/f16,s8,u8;global,shared,shared::cta;;1 RA;b/sync/aligned/col,row/m16n16k16,m32n8k16,m8n32k16/f16,s8,u8;global,shared,shared::cta;;1 RAX;c/sync/aligned/col,row/m16n16k16,m32n8k16,m8n32k16/f16,f32,s32;global,shared,shared::cta;;1 RA;c/sync/aligned/col,row/m16n16k16,m32n8k16,m8n32k16/f16,f32,s32;global,shared,shared::cta;;1 RAX;a/sync/aligned/col,row/m16n16k16,m32n8k16,m8n32k16/bf16;global,shared,shared::cta;;1 RA;a/sync/aligned/col,row/m16n16k16,m32n8k16,m8n32k16/bf16;global,shared,shared::cta;;1 RAX;b/sync/aligned/col,row/m16n16k16,m32n8k16,m8n32k16/bf16;global,shared,shared::cta;;1 RA;b/sync/aligned/col,row/m16n16k16,m32n8k16,m8n32k16/bf16;global,shared,shared::cta;;1 RAX;c/sync/aligned/col,row/m16n16k16,m32n8k16,m8n32k16/f32;global,shared,shared::cta;;1 RA;c/sync/aligned/col,row/m16n16k16,m32n8k16,m8n32k16/f32;global,shared,shared::cta;;1 RAX;a/sync/aligned/col,row/m16n16k8/tf32;global,shared,shared::cta;;1 RA;a/sync/aligned/col,row/m16n16k8/tf32;global,shared,shared::cta;;1 RAX;b/sync/aligned/col,row/m16n16k8/tf32;global,shared,shared::cta;;1 RA;b/sync/aligned/col,row/m16n16k8/tf32;global,shared,shared::cta;;1 RAX;c/sync/aligned/col,row/m16n16k8/f32;global,shared,shared::cta;;1 RA;c/sync/aligned/col,row/m16n16k8/f32;global,shared,shared::cta;;1 RAX;a/sync/aligned/col,row/m8n8k4/f64;global,shared,shared::cta;;1 RA;a/sync/aligned/col,row/m8n8k4/f64;global,shared,shared::cta;;1 RAX;b/sync/aligned/col,row/m8n8k4/f64;global,shared,shared::cta;;1 RA;b/sync/aligned/col,row/m8n8k4/f64;global,shared,shared::cta;;1 RAX;c/sync/aligned/col,row/m8n8k4/f64;global,shared,shared::cta;;1 RA;c/sync/aligned/col,row/m8n8k4/f64;global,shared,shared::cta;;1 RAX;a/sync/aligned/row/m8n8k32/s4,u4;global,shared,shared::cta;;1 RA;a/sync/aligned/row/m8n8k32/s4,u4;global,shared,shared::cta;;1 RAX;b/sync/aligned/col/m8n8k32/s4,u4;global,shared,shared::cta;;1 RA;b/sync/aligned/col/m8n8k32/s4,u4;global,shared,shared::cta;;1 RAX;c/sync/aligned/col,row/m8n8k32/s32;global,shared,shared::cta;;1 RA;c/sync/aligned/col,row/m8n8k32/s32;global,shared,shared::cta;;1 RAX;a/sync/aligned/row/m8n8k128/b1;global,shared,shared::cta;;1 RA;a/sync/aligned/row/m8n8k128/b1;global,shared,shared::cta;;1 RAX;b/sync/aligned/col/m8n8k128/b1;global,shared,shared::cta;;1 RA;b/sync/aligned/col/m8n8k128/b1;global,shared,shared::cta;;1 RAX;c/sync/aligned/col,row/m8n8k128/s32;global,shared,shared::cta;;1 RA;c/sync/aligned/col,row/m8n8k128/s32;global,shared,shared::cta;;1"
        )
    case WmmaMma extends Definition(
          "wmma.mma",
          Opcode.Wmma,
          Vec(Modifier.Mma),
          (operands, _) => {
            if (operands.size == 4)
              Right(Form.WmmaMma(dest = operands(0), srcA = operands(1), srcB = operands(2), srcC = operands(3)))
            else Left(DecodeError("wmma.mma", DecodeProblem.InvalidOperandCount(Vec(4), operands.size)))
          },
          "6.0 70 and=80 RXXX;sync/aligned/col,row/col,row/m16n16k16,m32n8k16,m8n32k16/f16,f32/f16,f32;;;1 RXXX;sync/aligned/col,row/col,row/m16n16k16,m32n8k16,m8n32k16/s32/s8,u8/s8,u8/s32;satfinite;;1 RXXX;sync/aligned/col,row/col,row/m16n16k16,m32n8k16,m8n32k16/f32/bf16/bf16/f32;;;1 RXXX;sync/aligned/col,row/col,row/m16n16k8/f32/tf32/tf32/f32;;;1 RXXX;sync/aligned/col,row/col,row/m8n8k4/f64/f64/f64/f64;rm,rn,rp,rz;;1 RXXX;sync/aligned/row/col/m8n8k32/s32/s4,u4/s4,u4/s32;satfinite;;1 RXXX;and,xor/popc/sync/aligned/row/col/m8n8k128/s32/b1/b1/s32;;;1"
        )
    case WmmaStore extends Definition(
          "wmma.store",
          Opcode.Wmma,
          Vec(Modifier.Store),
          (operands, _) => {
            if (operands.size >= 2 && operands.size <= 3)
              Right(Form.WmmaStore(address = operands(0), r = operands(1), stride = operands.lift(2)))
            else Left(DecodeError("wmma.store", DecodeProblem.InvalidOperandCount(Vec(2, 3), operands.size)))
          },
          "6.0 70 m16n16k8=80 AXX;d/sync/aligned/col,row/m16n16k16,m32n8k16,m8n32k16/f16,f32,s32;global,shared,shared::cta;;1 AX;d/sync/aligned/col,row/m16n16k16,m32n8k16,m8n32k16/f16,f32,s32;global,shared,shared::cta;;1 AXX;d/sync/aligned/col,row/m8n8k128,m8n8k32/s32;global,shared,shared::cta;;1 AX;d/sync/aligned/col,row/m8n8k128,m8n8k32/s32;global,shared,shared::cta;;1 AXX;d/sync/aligned/col,row/m16n16k8/f32;global,shared,shared::cta;;1 AX;d/sync/aligned/col,row/m16n16k8/f32;global,shared,shared::cta;;1 AXX;d/sync/aligned/col,row/m8n8k4/f64;global,shared,shared::cta;;1 AX;d/sync/aligned/col,row/m8n8k4/f64;global,shared,shared::cta;;1"
        )
    case Xor extends Definition(
          "xor",
          Opcode.Xor,
          Vec(),
          (operands, _) => {
            if (operands.size == 3) Right(Form.Xor(dest = operands(0), srcA = operands(1), srcB = operands(2)))
            else Left(DecodeError("xor", DecodeProblem.InvalidOperandCount(Vec(3), operands.size)))
          },
          "1.0 10  RXX;b16,b32,b64,pred;;;1"
        )

    lazy val meta: Metadata = Metadata.parse(metadata)

  }

  val definitions: Vec[Definition] = Definition.values.toVec

  private lazy val DefinitionsByMnemonic: Map[String, Vec[Definition]] =
    definitions.groupMap(_.opcode.takeWhile(_ != '.'))(value => value)

  def decode(
      opcode: String,
      operands: Vec[Operand],
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Predication] = None
  ): Either[DecodeError, Instruction] = {
    val opcodeParts = Vec.from(opcode.toLowerCaseRoot.split('.')).flatMap(_.refine[NonEmpty[String]].toOption)
    val name = opcodeParts.headOption.getOrElse("")
    val allModifiers = opcodeParts.drop(1).map(value => Modifier(value)) ++ mods
    decodeParsed(name, operands, allModifiers, pred)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def decodeParsed(
      mnemonic: String,
      operands: Vec[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ): Either[DecodeError, Instruction] = {
    val name = mnemonic.toLowerCaseRoot
    val allModifiers = mods
    val definitions = DefinitionsByMnemonic.getOrElse(name, Vec.empty[Definition])
    var specificity = -1
    var index = 0
    while (index < definitions.size) {
      val definition = definitions(index)
      if (definition.fixedModifiers.size > specificity && fixedModifiersMatch(allModifiers, definition.fixedModifiers))
        specificity = definition.fixedModifiers.size
      index += 1
    }

    val remaining = allModifiers.drop(specificity)
    var successful: Option[(Definition, Form)] = None
    val ambiguous = mutable.ArrayBuffer.empty[String]
    val expected = mutable.ArrayBuffer.empty[Int]
    index = 0
    while (index < definitions.size) {
      val definition = definitions(index)
      if (definition.fixedModifiers.size == specificity && fixedModifiersMatch(allModifiers, definition.fixedModifiers))
        definition.decoder(operands, remaining) match {
          case Right(form) => successful match {
              case None => successful = Some(definition -> form)
              case Some((first, _)) =>
                if (ambiguous.isEmpty) ambiguous.append(first.toString)
                ambiguous.append(definition.toString)
            }
          case Left(DecodeError(_, DecodeProblem.InvalidOperandCount(counts, _))) => expected.appendAll(counts)
          case Left(_) => ()
        }
      index += 1
    }

    if (ambiguous.nonEmpty)
      Left(DecodeError(qualifiedOpcode(name, allModifiers), DecodeProblem.AmbiguousDefinitions(ambiguous.toVec)))
    else successful match {
      case Some((_, form)) => Right(Instruction(form, remaining, pred))
      case None if expected.nonEmpty =>
        Left(DecodeError(
          qualifiedOpcode(name, allModifiers),
          DecodeProblem.InvalidOperandCount(expected.distinct.sorted.toVec, operands.size)
        ))
      case None => Left(DecodeError(qualifiedOpcode(name, allModifiers), DecodeProblem.UnknownOpcode))
    }
  }

  private def fixedModifiersMatch(modifiers: Vec[Modifier], fixed: Vec[Modifier]): Boolean =
    fixed.size <= modifiers.size && fixed.indices.forall(index => modifiers(index) == fixed(index))

  private def qualifiedOpcode(name: String, modifiers: Vec[Modifier]): String =
    (name +: modifiers.map(_.name)).mkString(".")
}

@Generated(Array("sbt codegen"))
case class Instruction(form: Form, mods: Vec[Modifier], pred: Option[Predication]) {
  def definition: Instruction.Definition = Instruction.Definition.fromOrdinal(form.ordinal)

  def operands: Vec[Operand] = form.operands

  def opcode: NonEmpty[String] = definition.opcode

  def fixedModifiers: Vec[Modifier] = definition.fixedModifiers

  def decoder: Instruction.Decoder = definition.decoder

  def meta: Metadata = definition.meta

  def qualifiedOpcode: NonEmpty[String] =
    if (mods.isEmpty) opcode else s"$opcode.${mods.map(_.name).mkString(".")}"

  lazy val signature: InstructionSignature =
    InstructionSignature(definition.mnemonic, definition.fixedModifiers ++ mods)
}
