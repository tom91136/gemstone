// Generated from the pinned NVIDIA PTX ISA 9.3 specification. Do not edit by hand.
package net.kurobako.gemstone.isa.ptx.v9_3

sealed trait KnownInstruction extends Product with Serializable {
  def arguments: InstructionArguments
  def opcode: Opcode
  def name: String
  final def syntax: Instruction = arguments.toSyntax(opcode)
}

enum InstructionDecodeError {
  case UnknownOpcode(opcode: String)
}

object KnownInstruction {
  private val ChunkCount: Int = 5

  def fromSyntax(value: Instruction): Either[InstructionDecodeError, KnownInstruction] = {
    val name = normalizeOpcode(value.opcode.mnemonic)
    val arguments = InstructionArguments(value.operands, value.modifiers, value.predication)
    decode(name, arguments).toRight(InstructionDecodeError.UnknownOpcode(name))
  }

  private def decode(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    Math.floorMod(name.hashCode, ChunkCount) match {
      case 0 => decode0(name, arguments)
      case 1 => decode1(name, arguments)
      case 2 => decode2(name, arguments)
      case 3 => decode3(name, arguments)
      case 4 => decode4(name, arguments)
    }

  final case class Abs(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("abs")
    override val name: String = "abs"
  }

  final case class Activemask(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("activemask")
    override val name: String = "activemask"
  }

  final case class Add(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("add")
    override val name: String = "add"
  }

  final case class Addc(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("addc")
    override val name: String = "addc"
  }

  final case class Alloca(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("alloca")
    override val name: String = "alloca"
  }

  final case class And(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("and")
    override val name: String = "and"
  }

  final case class Applypriority(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("applypriority")
    override val name: String = "applypriority"
  }

  final case class Atom(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("atom")
    override val name: String = "atom"
  }

  final case class Bar(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("bar")
    override val name: String = "bar"
  }

  final case class Barrier(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("barrier")
    override val name: String = "barrier"
  }

  final case class Bfe(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("bfe")
    override val name: String = "bfe"
  }

  final case class Bfi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("bfi")
    override val name: String = "bfi"
  }

  final case class Bfind(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("bfind")
    override val name: String = "bfind"
  }

  final case class Bmsk(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("bmsk")
    override val name: String = "bmsk"
  }

  final case class Bra(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("bra")
    override val name: String = "bra"
  }

  final case class Brev(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("brev")
    override val name: String = "brev"
  }

  final case class Brkpt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("brkpt")
    override val name: String = "brkpt"
  }

  final case class Brx(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("brx")
    override val name: String = "brx"
  }

  final case class CachePolicy(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("cache_policy")
    override val name: String = "cache_policy"
  }

  final case class Call(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("call")
    override val name: String = "call"
  }

  final case class Clmad(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("clmad")
    override val name: String = "clmad"
  }

  final case class Clusterlaunchcontrol(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("clusterlaunchcontrol")
    override val name: String = "clusterlaunchcontrol"
  }

  final case class Clz(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("clz")
    override val name: String = "clz"
  }

  final case class Cnot(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("cnot")
    override val name: String = "cnot"
  }

  final case class Copysign(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("copysign")
    override val name: String = "copysign"
  }

  final case class Cos(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("cos")
    override val name: String = "cos"
  }

  final case class Cp(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("cp")
    override val name: String = "cp"
  }

  final case class Createpolicy(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("createpolicy")
    override val name: String = "createpolicy"
  }

  final case class Cvt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("cvt")
    override val name: String = "cvt"
  }

  final case class Cvta(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("cvta")
    override val name: String = "cvta"
  }

  final case class Defaults(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("defaults")
    override val name: String = "defaults"
  }

  final case class Discard(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("discard")
    override val name: String = "discard"
  }

  final case class Div(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("div")
    override val name: String = "div"
  }

  final case class Dp2a(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("dp2a")
    override val name: String = "dp2a"
  }

  final case class Dp4a(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("dp4a")
    override val name: String = "dp4a"
  }

  final case class Elect(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("elect")
    override val name: String = "elect"
  }

  final case class Ex2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("ex2")
    override val name: String = "ex2"
  }

  final case class Exit(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("exit")
    override val name: String = "exit"
  }

  final case class Fabric(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("fabric")
    override val name: String = "fabric"
  }

  final case class Fence(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("fence")
    override val name: String = "fence"
  }

  final case class Fma(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("fma")
    override val name: String = "fma"
  }

  final case class Fns(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("fns")
    override val name: String = "fns"
  }

  final case class Getctarank(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("getctarank")
    override val name: String = "getctarank"
  }

  final case class Griddepcontrol(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("griddepcontrol")
    override val name: String = "griddepcontrol"
  }

  final case class Isspacep(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("isspacep")
    override val name: String = "isspacep"
  }

  final case class Istypep(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("istypep")
    override val name: String = "istypep"
  }

  final case class Ld(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("ld")
    override val name: String = "ld"
  }

  final case class Ldu(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("ldu")
    override val name: String = "ldu"
  }

  final case class Lg2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("lg2")
    override val name: String = "lg2"
  }

  final case class Lop3(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("lop3")
    override val name: String = "lop3"
  }

  final case class Mad(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("mad")
    override val name: String = "mad"
  }

  final case class Mad24(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("mad24")
    override val name: String = "mad24"
  }

  final case class Madc(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("madc")
    override val name: String = "madc"
  }

  final case class Mapa(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("mapa")
    override val name: String = "mapa"
  }

  final case class Match(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("match")
    override val name: String = "match"
  }

  final case class Max(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("max")
    override val name: String = "max"
  }

  final case class Mbarrier(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("mbarrier")
    override val name: String = "mbarrier"
  }

  final case class Membar(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("membar")
    override val name: String = "membar"
  }

  final case class Min(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("min")
    override val name: String = "min"
  }

  final case class Mov(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("mov")
    override val name: String = "mov"
  }

  final case class Mul(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("mul")
    override val name: String = "mul"
  }

  final case class Mul24(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("mul24")
    override val name: String = "mul24"
  }

  final case class Multimem(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("multimem")
    override val name: String = "multimem"
  }

  final case class Nanosleep(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("nanosleep")
    override val name: String = "nanosleep"
  }

  final case class Neg(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("neg")
    override val name: String = "neg"
  }

  final case class Not(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("not")
    override val name: String = "not"
  }

  final case class Or(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("or")
    override val name: String = "or"
  }

  final case class Pmevent(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("pmevent")
    override val name: String = "pmevent"
  }

  final case class Popc(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("popc")
    override val name: String = "popc"
  }

  final case class Prefetch(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("prefetch")
    override val name: String = "prefetch"
  }

  final case class Prefetchu(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("prefetchu")
    override val name: String = "prefetchu"
  }

  final case class Prmt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("prmt")
    override val name: String = "prmt"
  }

  final case class Rcp(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("rcp")
    override val name: String = "rcp"
  }

  final case class Red(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("red")
    override val name: String = "red"
  }

  final case class Redux(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("redux")
    override val name: String = "redux"
  }

  final case class Rem(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("rem")
    override val name: String = "rem"
  }

  final case class Ret(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("ret")
    override val name: String = "ret"
  }

  final case class Rsqrt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("rsqrt")
    override val name: String = "rsqrt"
  }

  final case class Sad(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("sad")
    override val name: String = "sad"
  }

  final case class Selp(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("selp")
    override val name: String = "selp"
  }

  final case class Set(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("set")
    override val name: String = "set"
  }

  final case class Setmaxnreg(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("setmaxnreg")
    override val name: String = "setmaxnreg"
  }

  final case class Setp(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("setp")
    override val name: String = "setp"
  }

  final case class Shf(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("shf")
    override val name: String = "shf"
  }

  final case class Shfl(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("shfl")
    override val name: String = "shfl"
  }

  final case class Shl(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("shl")
    override val name: String = "shl"
  }

  final case class Shr(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("shr")
    override val name: String = "shr"
  }

  final case class Sin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("sin")
    override val name: String = "sin"
  }

  final case class Slct(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("slct")
    override val name: String = "slct"
  }

  final case class Sqrt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("sqrt")
    override val name: String = "sqrt"
  }

  final case class St(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("st")
    override val name: String = "st"
  }

  final case class Stackrestore(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("stackrestore")
    override val name: String = "stackrestore"
  }

  final case class Stacksave(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("stacksave")
    override val name: String = "stacksave"
  }

  final case class Sub(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("sub")
    override val name: String = "sub"
  }

  final case class Subc(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("subc")
    override val name: String = "subc"
  }

  final case class Suld(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("suld")
    override val name: String = "suld"
  }

  final case class Suq(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("suq")
    override val name: String = "suq"
  }

  final case class Sured(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("sured")
    override val name: String = "sured"
  }

  final case class Sust(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("sust")
    override val name: String = "sust"
  }

  final case class Szext(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("szext")
    override val name: String = "szext"
  }

  final case class Tanh(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("tanh")
    override val name: String = "tanh"
  }

  final case class Tcgen05(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("tcgen05")
    override val name: String = "tcgen05"
  }

  final case class Tensormap(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("tensormap")
    override val name: String = "tensormap"
  }

  final case class Testp(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("testp")
    override val name: String = "testp"
  }

  final case class Tex(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("tex")
    override val name: String = "tex"
  }

  final case class Tld4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("tld4")
    override val name: String = "tld4"
  }

  final case class Trap(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("trap")
    override val name: String = "trap"
  }

  final case class Txq(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("txq")
    override val name: String = "txq"
  }

  final case class Vabsdiff(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("vabsdiff")
    override val name: String = "vabsdiff"
  }

  final case class Vadd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("vadd")
    override val name: String = "vadd"
  }

  final case class Vadd2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("vadd2")
    override val name: String = "vadd2"
  }

  final case class Vadd4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("vadd4")
    override val name: String = "vadd4"
  }

  final case class Vmad(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("vmad")
    override val name: String = "vmad"
  }

  final case class Vmin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("vmin")
    override val name: String = "vmin"
  }

  final case class Vmin2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("vmin2")
    override val name: String = "vmin2"
  }

  final case class Vmin4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("vmin4")
    override val name: String = "vmin4"
  }

  final case class Vop(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("vop")
    override val name: String = "vop"
  }

  final case class Vop2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("vop2")
    override val name: String = "vop2"
  }

  final case class Vop4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("vop4")
    override val name: String = "vop4"
  }

  final case class Vote(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("vote")
    override val name: String = "vote"
  }

  final case class Vset(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("vset")
    override val name: String = "vset"
  }

  final case class Vset2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("vset2")
    override val name: String = "vset2"
  }

  final case class Vset4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("vset4")
    override val name: String = "vset4"
  }

  final case class Vshl(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("vshl")
    override val name: String = "vshl"
  }

  final case class Vshr(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("vshr")
    override val name: String = "vshr"
  }

  final case class Vsub(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("vsub")
    override val name: String = "vsub"
  }

  final case class Vsub2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("vsub2")
    override val name: String = "vsub2"
  }

  final case class Vsub4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("vsub4")
    override val name: String = "vsub4"
  }

  final case class Xor(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode("xor")
    override val name: String = "xor"
  }

  private def decode0(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "abs" => Some(Abs(arguments))
      case "applypriority" => Some(Applypriority(arguments))
      case "bfi" => Some(Bfi(arguments))
      case "bfind" => Some(Bfind(arguments))
      case "cos" => Some(Cos(arguments))
      case "cvta" => Some(Cvta(arguments))
      case "elect" => Some(Elect(arguments))
      case "istypep" => Some(Istypep(arguments))
      case "ldu" => Some(Ldu(arguments))
      case "madc" => Some(Madc(arguments))
      case "mapa" => Some(Mapa(arguments))
      case "match" => Some(Match(arguments))
      case "or" => Some(Or(arguments))
      case "rcp" => Some(Rcp(arguments))
      case "red" => Some(Red(arguments))
      case "sin" => Some(Sin(arguments))
      case "stackrestore" => Some(Stackrestore(arguments))
      case "stacksave" => Some(Stacksave(arguments))
      case "sub" => Some(Sub(arguments))
      case "suld" => Some(Suld(arguments))
      case "suq" => Some(Suq(arguments))
      case "tcgen05" => Some(Tcgen05(arguments))
      case "testp" => Some(Testp(arguments))
      case "vabsdiff" => Some(Vabsdiff(arguments))
      case "vadd" => Some(Vadd(arguments))
      case "vadd2" => Some(Vadd2(arguments))
      case "vset" => Some(Vset(arguments))
      case "vset2" => Some(Vset2(arguments))
      case "vshl" => Some(Vshl(arguments))
      case "vsub4" => Some(Vsub4(arguments))
      case "xor" => Some(Xor(arguments))
      case _ => None
    }

  private def decode1(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "addc" => Some(Addc(arguments))
      case "bfe" => Some(Bfe(arguments))
      case "brev" => Some(Brev(arguments))
      case "cnot" => Some(Cnot(arguments))
      case "cp" => Some(Cp(arguments))
      case "dp4a" => Some(Dp4a(arguments))
      case "ex2" => Some(Ex2(arguments))
      case "getctarank" => Some(Getctarank(arguments))
      case "lg2" => Some(Lg2(arguments))
      case "mad" => Some(Mad(arguments))
      case "max" => Some(Max(arguments))
      case "mul24" => Some(Mul24(arguments))
      case "multimem" => Some(Multimem(arguments))
      case "nanosleep" => Some(Nanosleep(arguments))
      case "prmt" => Some(Prmt(arguments))
      case "ret" => Some(Ret(arguments))
      case "selp" => Some(Selp(arguments))
      case "shf" => Some(Shf(arguments))
      case "st" => Some(St(arguments))
      case "tld4" => Some(Tld4(arguments))
      case "vop" => Some(Vop(arguments))
      case "vop2" => Some(Vop2(arguments))
      case "vote" => Some(Vote(arguments))
      case "vshr" => Some(Vshr(arguments))
      case _ => None
    }

  private def decode2(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "activemask" => Some(Activemask(arguments))
      case "add" => Some(Add(arguments))
      case "and" => Some(And(arguments))
      case "barrier" => Some(Barrier(arguments))
      case "brkpt" => Some(Brkpt(arguments))
      case "brx" => Some(Brx(arguments))
      case "cache_policy" => Some(CachePolicy(arguments))
      case "call" => Some(Call(arguments))
      case "createpolicy" => Some(Createpolicy(arguments))
      case "exit" => Some(Exit(arguments))
      case "fns" => Some(Fns(arguments))
      case "griddepcontrol" => Some(Griddepcontrol(arguments))
      case "lop3" => Some(Lop3(arguments))
      case "membar" => Some(Membar(arguments))
      case "not" => Some(Not(arguments))
      case "prefetch" => Some(Prefetch(arguments))
      case "redux" => Some(Redux(arguments))
      case "rsqrt" => Some(Rsqrt(arguments))
      case "sad" => Some(Sad(arguments))
      case "set" => Some(Set(arguments))
      case "shl" => Some(Shl(arguments))
      case "sured" => Some(Sured(arguments))
      case "tanh" => Some(Tanh(arguments))
      case "tex" => Some(Tex(arguments))
      case "vadd4" => Some(Vadd4(arguments))
      case "vmin" => Some(Vmin(arguments))
      case "vmin2" => Some(Vmin2(arguments))
      case "vset4" => Some(Vset4(arguments))
      case _ => None
    }

  private def decode3(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "atom" => Some(Atom(arguments))
      case "clmad" => Some(Clmad(arguments))
      case "copysign" => Some(Copysign(arguments))
      case "cvt" => Some(Cvt(arguments))
      case "div" => Some(Div(arguments))
      case "fence" => Some(Fence(arguments))
      case "fma" => Some(Fma(arguments))
      case "ld" => Some(Ld(arguments))
      case "mad24" => Some(Mad24(arguments))
      case "mbarrier" => Some(Mbarrier(arguments))
      case "mov" => Some(Mov(arguments))
      case "pmevent" => Some(Pmevent(arguments))
      case "prefetchu" => Some(Prefetchu(arguments))
      case "setmaxnreg" => Some(Setmaxnreg(arguments))
      case "shr" => Some(Shr(arguments))
      case "slct" => Some(Slct(arguments))
      case "sqrt" => Some(Sqrt(arguments))
      case "sust" => Some(Sust(arguments))
      case "vop4" => Some(Vop4(arguments))
      case "vsub" => Some(Vsub(arguments))
      case "vsub2" => Some(Vsub2(arguments))
      case _ => None
    }

  private def decode4(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "alloca" => Some(Alloca(arguments))
      case "bar" => Some(Bar(arguments))
      case "bmsk" => Some(Bmsk(arguments))
      case "bra" => Some(Bra(arguments))
      case "clusterlaunchcontrol" => Some(Clusterlaunchcontrol(arguments))
      case "clz" => Some(Clz(arguments))
      case "defaults" => Some(Defaults(arguments))
      case "discard" => Some(Discard(arguments))
      case "dp2a" => Some(Dp2a(arguments))
      case "fabric" => Some(Fabric(arguments))
      case "isspacep" => Some(Isspacep(arguments))
      case "min" => Some(Min(arguments))
      case "mul" => Some(Mul(arguments))
      case "neg" => Some(Neg(arguments))
      case "popc" => Some(Popc(arguments))
      case "rem" => Some(Rem(arguments))
      case "setp" => Some(Setp(arguments))
      case "shfl" => Some(Shfl(arguments))
      case "subc" => Some(Subc(arguments))
      case "szext" => Some(Szext(arguments))
      case "tensormap" => Some(Tensormap(arguments))
      case "trap" => Some(Trap(arguments))
      case "txq" => Some(Txq(arguments))
      case "vmad" => Some(Vmad(arguments))
      case "vmin4" => Some(Vmin4(arguments))
      case _ => None
    }
}
