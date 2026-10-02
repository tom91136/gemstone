// Generated from the pinned PTX syntax domains. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.ptx.v9_3
import net.kurobako.gemstone.constraint.*

object Opcode {
  private lazy val byName: Map[String, Opcode] = Map(
    "abs" -> Abs,
    "activemask" -> Activemask,
    "add" -> Add,
    "addc" -> Addc,
    "alloca" -> Alloca,
    "and" -> And,
    "applypriority" -> ApplyPriority,
    "atom" -> Atom,
    "bar" -> Bar,
    "barrier" -> Barrier,
    "bfe" -> Bfe,
    "bfi" -> Bfi,
    "bfind" -> Bfind,
    "bmsk" -> Bmsk,
    "bra" -> Bra,
    "brev" -> Brev,
    "brkpt" -> Brkpt,
    "brx" -> Brx,
    "call" -> Call,
    "clmad" -> Clmad,
    "clusterlaunchcontrol" -> Clusterlaunchcontrol,
    "clz" -> Clz,
    "cnot" -> Cnot,
    "copysign" -> CopySign,
    "cos" -> Cos,
    "cp" -> Cp,
    "createpolicy" -> CreatePolicy,
    "cvt" -> Cvt,
    "cvta" -> Cvta,
    "discard" -> Discard,
    "div" -> Div,
    "dp2a" -> Dp2a,
    "dp4a" -> Dp4a,
    "elect" -> Elect,
    "ex2" -> Ex2,
    "exit" -> Exit,
    "fabric" -> Fabric,
    "fence" -> Fence,
    "fma" -> Fma,
    "fns" -> Fns,
    "fp8" -> Fp8,
    "getctarank" -> GetCtaRank,
    "griddepcontrol" -> GridDependencyControl,
    "isspacep" -> IsSpacePredicate,
    "istypep" -> IsTypePredicate,
    "ld" -> Ld,
    "ldmatrix" -> Ldmatrix,
    "ldu" -> Ldu,
    "lg2" -> Lg2,
    "lop3" -> Lop3,
    "mad" -> Mad,
    "mad24" -> Mad24,
    "madc" -> Madc,
    "mapa" -> Mapa,
    "match" -> Match,
    "max" -> Max,
    "mbarrier" -> Mbarrier,
    "membar" -> Membar,
    "min" -> Min,
    "mma" -> Mma,
    "mov" -> Mov,
    "movmatrix" -> Movmatrix,
    "mul" -> Mul,
    "mul24" -> Mul24,
    "multimem" -> Multimem,
    "nanosleep" -> NanoSleep,
    "neg" -> Neg,
    "not" -> Not,
    "or" -> Or,
    "pmevent" -> PmEvent,
    "popc" -> Popc,
    "prefetch" -> Prefetch,
    "prefetchu" -> Prefetchu,
    "prmt" -> Prmt,
    "rcp" -> Rcp,
    "red" -> Red,
    "redux" -> Redux,
    "rem" -> Rem,
    "ret" -> Ret,
    "rsqrt" -> Rsqrt,
    "sad" -> Sad,
    "selp" -> Selp,
    "set" -> Set,
    "setmaxnreg" -> SetMaxRegisters,
    "setp" -> Setp,
    "shf" -> Shf,
    "shfl" -> Shfl,
    "shl" -> Shl,
    "shr" -> Shr,
    "sin" -> Sin,
    "slct" -> Slct,
    "sqrt" -> Sqrt,
    "st" -> St,
    "stackrestore" -> StackRestore,
    "stacksave" -> StackSave,
    "stmatrix" -> Stmatrix,
    "sub" -> Sub,
    "subc" -> Subc,
    "suld" -> Suld,
    "suq" -> Suq,
    "sured" -> Sured,
    "sust" -> Sust,
    "szext" -> Szext,
    "tanh" -> Tanh,
    "tcgen05" -> Tcgen05,
    "tensormap" -> Tensormap,
    "testp" -> TestPredicate,
    "tex" -> Tex,
    "tld4" -> Tld4,
    "trap" -> Trap,
    "txq" -> Txq,
    "vabsdiff" -> Vabsdiff,
    "vabsdiff2" -> Vabsdiff2,
    "vabsdiff4" -> Vabsdiff4,
    "vadd" -> Vadd,
    "vadd2" -> Vadd2,
    "vadd4" -> Vadd4,
    "vavrg2" -> Vavrg2,
    "vavrg4" -> Vavrg4,
    "vmad" -> Vmad,
    "vmax" -> Vmax,
    "vmax2" -> Vmax2,
    "vmax4" -> Vmax4,
    "vmin" -> Vmin,
    "vmin2" -> Vmin2,
    "vmin4" -> Vmin4,
    "vote" -> Vote,
    "vset" -> Vset,
    "vset2" -> Vset2,
    "vset4" -> Vset4,
    "vshl" -> Vshl,
    "vshr" -> Vshr,
    "vsub" -> Vsub,
    "vsub2" -> Vsub2,
    "vsub4" -> Vsub4,
    "wgmma" -> Wgmma,
    "wmma" -> Wmma,
    "xor" -> Xor
  )
  def apply(name: NonEmpty[String]): Opcode =
    byName.getOrElse(name.raw, throw IllegalArgumentException(s"unknown PTX opcode $name"))
  def unapply(value: Opcode): Some[NonEmpty[String]] = Some(value.name)
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum Opcode(val name: NonEmpty[String]) {
  case Abs extends Opcode("abs")
  case Activemask extends Opcode("activemask")
  case Add extends Opcode("add")
  case Addc extends Opcode("addc")
  case Alloca extends Opcode("alloca")
  case And extends Opcode("and")
  case ApplyPriority extends Opcode("applypriority")
  case Atom extends Opcode("atom")
  case Bar extends Opcode("bar")
  case Barrier extends Opcode("barrier")
  case Bfe extends Opcode("bfe")
  case Bfi extends Opcode("bfi")
  case Bfind extends Opcode("bfind")
  case Bmsk extends Opcode("bmsk")
  case Bra extends Opcode("bra")
  case Brev extends Opcode("brev")
  case Brkpt extends Opcode("brkpt")
  case Brx extends Opcode("brx")
  case Call extends Opcode("call")
  case Clmad extends Opcode("clmad")
  case Clusterlaunchcontrol extends Opcode("clusterlaunchcontrol")
  case Clz extends Opcode("clz")
  case Cnot extends Opcode("cnot")
  case CopySign extends Opcode("copysign")
  case Cos extends Opcode("cos")
  case Cp extends Opcode("cp")
  case CreatePolicy extends Opcode("createpolicy")
  case Cvt extends Opcode("cvt")
  case Cvta extends Opcode("cvta")
  case Discard extends Opcode("discard")
  case Div extends Opcode("div")
  case Dp2a extends Opcode("dp2a")
  case Dp4a extends Opcode("dp4a")
  case Elect extends Opcode("elect")
  case Ex2 extends Opcode("ex2")
  case Exit extends Opcode("exit")
  case Fabric extends Opcode("fabric")
  case Fence extends Opcode("fence")
  case Fma extends Opcode("fma")
  case Fns extends Opcode("fns")
  case Fp8 extends Opcode("fp8")
  case GetCtaRank extends Opcode("getctarank")
  case GridDependencyControl extends Opcode("griddepcontrol")
  case IsSpacePredicate extends Opcode("isspacep")
  case IsTypePredicate extends Opcode("istypep")
  case Ld extends Opcode("ld")
  case Ldmatrix extends Opcode("ldmatrix")
  case Ldu extends Opcode("ldu")
  case Lg2 extends Opcode("lg2")
  case Lop3 extends Opcode("lop3")
  case Mad extends Opcode("mad")
  case Mad24 extends Opcode("mad24")
  case Madc extends Opcode("madc")
  case Mapa extends Opcode("mapa")
  case Match extends Opcode("match")
  case Max extends Opcode("max")
  case Mbarrier extends Opcode("mbarrier")
  case Membar extends Opcode("membar")
  case Min extends Opcode("min")
  case Mma extends Opcode("mma")
  case Mov extends Opcode("mov")
  case Movmatrix extends Opcode("movmatrix")
  case Mul extends Opcode("mul")
  case Mul24 extends Opcode("mul24")
  case Multimem extends Opcode("multimem")
  case NanoSleep extends Opcode("nanosleep")
  case Neg extends Opcode("neg")
  case Not extends Opcode("not")
  case Or extends Opcode("or")
  case PmEvent extends Opcode("pmevent")
  case Popc extends Opcode("popc")
  case Prefetch extends Opcode("prefetch")
  case Prefetchu extends Opcode("prefetchu")
  case Prmt extends Opcode("prmt")
  case Rcp extends Opcode("rcp")
  case Red extends Opcode("red")
  case Redux extends Opcode("redux")
  case Rem extends Opcode("rem")
  case Ret extends Opcode("ret")
  case Rsqrt extends Opcode("rsqrt")
  case Sad extends Opcode("sad")
  case Selp extends Opcode("selp")
  case Set extends Opcode("set")
  case SetMaxRegisters extends Opcode("setmaxnreg")
  case Setp extends Opcode("setp")
  case Shf extends Opcode("shf")
  case Shfl extends Opcode("shfl")
  case Shl extends Opcode("shl")
  case Shr extends Opcode("shr")
  case Sin extends Opcode("sin")
  case Slct extends Opcode("slct")
  case Sqrt extends Opcode("sqrt")
  case St extends Opcode("st")
  case StackRestore extends Opcode("stackrestore")
  case StackSave extends Opcode("stacksave")
  case Stmatrix extends Opcode("stmatrix")
  case Sub extends Opcode("sub")
  case Subc extends Opcode("subc")
  case Suld extends Opcode("suld")
  case Suq extends Opcode("suq")
  case Sured extends Opcode("sured")
  case Sust extends Opcode("sust")
  case Szext extends Opcode("szext")
  case Tanh extends Opcode("tanh")
  case Tcgen05 extends Opcode("tcgen05")
  case Tensormap extends Opcode("tensormap")
  case TestPredicate extends Opcode("testp")
  case Tex extends Opcode("tex")
  case Tld4 extends Opcode("tld4")
  case Trap extends Opcode("trap")
  case Txq extends Opcode("txq")
  case Vabsdiff extends Opcode("vabsdiff")
  case Vabsdiff2 extends Opcode("vabsdiff2")
  case Vabsdiff4 extends Opcode("vabsdiff4")
  case Vadd extends Opcode("vadd")
  case Vadd2 extends Opcode("vadd2")
  case Vadd4 extends Opcode("vadd4")
  case Vavrg2 extends Opcode("vavrg2")
  case Vavrg4 extends Opcode("vavrg4")
  case Vmad extends Opcode("vmad")
  case Vmax extends Opcode("vmax")
  case Vmax2 extends Opcode("vmax2")
  case Vmax4 extends Opcode("vmax4")
  case Vmin extends Opcode("vmin")
  case Vmin2 extends Opcode("vmin2")
  case Vmin4 extends Opcode("vmin4")
  case Vote extends Opcode("vote")
  case Vset extends Opcode("vset")
  case Vset2 extends Opcode("vset2")
  case Vset4 extends Opcode("vset4")
  case Vshl extends Opcode("vshl")
  case Vshr extends Opcode("vshr")
  case Vsub extends Opcode("vsub")
  case Vsub2 extends Opcode("vsub2")
  case Vsub4 extends Opcode("vsub4")
  case Wgmma extends Opcode("wgmma")
  case Wmma extends Opcode("wmma")
  case Xor extends Opcode("xor")

}
