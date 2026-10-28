// Generated SASS typed logical instruction forms from pinned NVIDIA descriptions. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import javax.annotation.processing.Generated

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
@Generated(Array("sbt codegen"))
object Form {
  type Acqbulk = AcqbulkNoOperands
  type Acqshminit = AcqshminitNoOperands
  type Al2p = Al2pPredRegReg | Al2pPredRegRegImm | Al2pRegReg | Al2pRegRegImm
  type Ald = AldRegAddressReg
  type Arrives = ArrivesAddress
  type Ast = AstAddressRegReg
  type Atom =
    AtomPredRegAddress | AtomPredRegAddressReg | AtomPredRegAddressRegReg | AtomRegAddressReg | AtomRegAddressRegReg
  type Atomg = AtomgPredRegAddressReg | AtomgPredRegAddressRegReg
  type Atoms = AtomsPredAddressRegReg | AtomsRegAddress | AtomsRegAddressReg | AtomsRegAddressRegReg
  type B2r = B2rReg | B2rRegImm | B2rRegPred
  type Bar =
    BarNoOperands | BarImm | BarImmImm | BarImmImmPred | BarImmPred | BarImmReg | BarImmRegPred | BarReg | BarRegImm | BarRegImmPred | BarRegPred | BarRegReg | BarRegRegPred
  type Bfe = BfeRegRegConst | BfeRegRegImm | BfeRegRegReg
  type Bfi = BfiRegRegConstReg | BfiRegRegImmReg | BfiRegRegRegConst | BfiRegRegRegReg
  type Bgmma =
    BgmmaRegAddressReg | BgmmaRegAddressRegPred | BgmmaRegAddressRegReg | BgmmaRegAddressRegRegPred | BgmmaRegRegAddressReg | BgmmaRegRegAddressRegPred
  type Bmma = BmmaRegRegRegReg | BmmaRegRegRegRegPred
  type Bmov =
    BmovAddressImm | BmovConst | BmovImm | BmovReg | BmovRegAddressImm | BmovRegConst | BmovRegImm | BmovRegReg | BmovRegSymbol | BmovSymbolAddressImm | BmovSymbolConst | BmovSymbolImm | BmovSymbolReg
  type Bmsk = BmskRegRegAddressImm | BmskRegRegConst | BmskRegRegImm | BmskRegRegReg
  type Bpt = BptNoOperands | BptImm
  type Bra =
    BraConst | BraImm | BraPredImm | BraPredPredImm | BraPredRegImm | BraRegImm | BraSymbolConst | BraSymbolImm | BraSymbolImmImm | BraSymbolSymbolImmImm
  type Break = BreakPredReg | BreakReg
  type Brev = BrevRegAddressImm | BrevRegConst | BrevRegImm | BrevRegReg
  type Brk = BrkNoOperands
  type Brx =
    BrxConst | BrxPredReg | BrxPredRegImm | BrxReg | BrxRegImm | BrxSymbolConst | BrxSymbolImmAddress | BrxSymbolReg | BrxSymbolRegImm | BrxSymbolSymbolImmAddress
  type Brxu = BrxuPredReg | BrxuPredRegImm | BrxuReg | BrxuRegImm
  type Bssy = BssyPredRegImm | BssyRegImm
  type Bsync = BsyncPredReg | BsyncReg
  type Cal = CalConst | CalImm
  type Call = CallConst | CallImm | CallPredConst | CallPredImm | CallPredReg | CallPredRegImm | CallReg | CallRegImm
  type Cctl =
    CctlNoOperands | CctlAddress | CctlAddressAddress | CctlAddressImm | CctlConst | CctlPredAddress | CctlRegAddress
  type Cctll = CctllNoOperands | CctllAddress | CctllRegAddress
  type Cctls = CctlsNoOperands
  type Cctlt = CctltNoOperands | CctltImm | CctltImmImm | CctltReg
  type Cgaerrbar = CgaerrbarNoOperands
  type Clmad =
    ClmadRegRegAddressImmReg | ClmadRegRegConstReg | ClmadRegRegRegAddressImm | ClmadRegRegRegConst | ClmadRegRegRegReg
  type Cont = ContNoOperands
  type Credux = CreduxRegReg
  type Cs2r = Cs2rRegSpecial
  type Cs2ur = Cs2urRegSpecial
  type Cset = CsetRegSymbol | CsetRegSymbolPred
  type Csetp = CsetpPredPredSymbolPred | CsetpPredSymbol
  type Csmtest = CsmtestImm | CsmtestPredImm | CsmtestPredPredImmPred
  type Dadd = DaddRegRegAddressImm | DaddRegRegConst | DaddRegRegImm | DaddRegRegReg
  type Dchk = DchkPredRegConst | DchkPredRegImm | DchkPredRegReg
  type Depbar =
    DepbarNoOperands | DepbarGroup | DepbarImm | DepbarRegImm | DepbarRegImmGroup | DepbarRegImmImm | DepbarRegReg | DepbarRegRegGroup | DepbarRegRegImm
  type Dfma =
    DfmaRegRegAddressImmReg | DfmaRegRegConstReg | DfmaRegRegImmReg | DfmaRegRegRegAddressImm | DfmaRegRegRegConst | DfmaRegRegRegImm | DfmaRegRegRegReg
  type Dmma = DmmaRegRegRegReg | DmmaRegRegRegRegPred
  type Dmnmx = DmnmxRegRegConstPred | DmnmxRegRegImmPred | DmnmxRegRegRegPred
  type Dmul = DmulRegRegAddressImm | DmulRegRegConst | DmulRegRegImm | DmulRegRegReg
  type Dset =
    DsetRegRegConst | DsetRegRegConstPred | DsetRegRegImm | DsetRegRegImmPred | DsetRegRegReg | DsetRegRegRegPred
  type Dsetp =
    DsetpPredPredRegAddressImmPred | DsetpPredPredRegConstPred | DsetpPredPredRegImmPred | DsetpPredPredRegRegPred | DsetpPredRegAddressImm | DsetpPredRegConst | DsetpPredRegImm | DsetpPredRegReg
  type Elect = ElectPredReg | ElectPredRegPred | ElectPredRegReg
  type Endcollective = EndcollectiveNoOperands | EndcollectivePred
  type Errbar = ErrbarNoOperands
  type Exit = ExitNoOperands | ExitPred
  type F2f = F2fRegAddressImm | F2fRegConst | F2fRegImm | F2fRegReg
  type F2fp =
    F2fpRegAddressImm | F2fpRegAddressImmReg | F2fpRegConst | F2fpRegConstReg | F2fpRegImm | F2fpRegImmReg | F2fpRegReg | F2fpRegRegAddressImm | F2fpRegRegAddressImmReg | F2fpRegRegConst | F2fpRegRegConstReg | F2fpRegRegImm | F2fpRegRegImmReg | F2fpRegRegReg | F2fpRegRegRegAddressImm | F2fpRegRegRegConst | F2fpRegRegRegImm | F2fpRegRegRegReg
  type F2i = F2iRegAddressImm | F2iRegConst | F2iRegImm | F2iRegReg
  type F2ip =
    F2ipRegRegAddressImmReg | F2ipRegRegConstReg | F2ipRegRegImmReg | F2ipRegRegRegAddressImm | F2ipRegRegRegConst | F2ipRegRegRegImm | F2ipRegRegRegReg
  type Fadd = FaddRegRegAddressImm | FaddRegRegConst | FaddRegRegImm | FaddRegRegReg
  type Fadd2 = Fadd2RegRegImm | Fadd2RegRegReg
  type Fadd32i = Fadd32iRegRegImm
  type Fcco =
    FccoPredPredRegConstReg | FccoPredPredRegImmReg | FccoPredPredRegRegConst | FccoPredPredRegRegReg | FccoPredRegConstReg | FccoPredRegImmReg | FccoPredRegRegConst | FccoPredRegRegReg
  type Fchk = FchkPredRegAddressImm | FchkPredRegConst | FchkPredRegImm | FchkPredRegReg
  type Fcmp = FcmpRegRegConstReg | FcmpRegRegImmReg | FcmpRegRegRegConst | FcmpRegRegRegReg
  type Fence = FenceNoOperands
  type Ffma =
    FfmaRegRegAddressImmReg | FfmaRegRegConstReg | FfmaRegRegImmReg | FfmaRegRegRegAddressImm | FfmaRegRegRegConst | FfmaRegRegRegImm | FfmaRegRegRegReg
  type Ffma2 = Ffma2RegRegImmReg | Ffma2RegRegRegImm | Ffma2RegRegRegReg
  type Ffma32i = Ffma32iRegRegImm | Ffma32iRegRegImmReg
  type Fhadd = FhaddRegRegReg
  type Fhfma = FhfmaRegRegRegReg
  type Flo =
    FloRegAddressImm | FloRegConst | FloRegImm | FloRegPredAddressImm | FloRegPredConst | FloRegPredImm | FloRegPredReg | FloRegReg
  type Fmnmx =
    FmnmxRegPredRegAddressImmPred | FmnmxRegPredRegConstPred | FmnmxRegPredRegImmPred | FmnmxRegPredRegRegPred | FmnmxRegRegAddressImmPred | FmnmxRegRegConstPred | FmnmxRegRegImmPred | FmnmxRegRegRegPred
  type Fmnmx3 = Fmnmx3RegRegImmRegPred | Fmnmx3RegRegRegRegPred
  type Fmul = FmulRegRegAddressImm | FmulRegRegConst | FmulRegRegImm | FmulRegRegReg
  type Fmul2 = Fmul2RegRegImm | Fmul2RegRegReg
  type Fmul32i = Fmul32iRegRegImm
  type Footprint =
    FootprintPredRegRegRegReg | FootprintPredRegRegRegRegImmImm | FootprintPredRegRegRegRegImmImmImm | FootprintPredRegRegRegRegReg | FootprintPredRegRegRegRegRegImm | FootprintRegRegRegReg | FootprintRegRegRegRegImmImm | FootprintRegRegRegRegImmImmImm | FootprintRegRegRegRegReg | FootprintRegRegRegRegRegImm
  type Frnd = FrndRegAddressImm | FrndRegConst | FrndRegImm | FrndRegReg
  type Fsel = FselRegRegAddressImmPred | FselRegRegConstPred | FselRegRegImmPred | FselRegRegRegPred
  type Fset =
    FsetRegRegAddressImm | FsetRegRegAddressImmPred | FsetRegRegConst | FsetRegRegConstPred | FsetRegRegImm | FsetRegRegImmPred | FsetRegRegReg | FsetRegRegRegPred
  type Fsetp =
    FsetpPredPredRegAddressImmPred | FsetpPredPredRegConstPred | FsetpPredPredRegImmPred | FsetpPredPredRegRegPred | FsetpPredRegAddressImm | FsetpPredRegConst | FsetpPredRegImm | FsetpPredRegReg
  type Fswz = FswzRegRegReg
  type Fswzadd = FswzaddRegRegReg
  type Gather = GatherRegRegRegRegImm | GatherRegRegRegRegImmImm | GatherRegRegRegRegImmImmImm
  type Getcrsptr = GetcrsptrReg
  type Getlmembase = GetlmembaseReg
  type Hadd2 =
    Hadd2NoOperands | Hadd2Const | Hadd2ImmImm | Hadd2RegRegAddressImm | Hadd2RegRegConst | Hadd2RegRegImm | Hadd2RegRegImmImm | Hadd2RegRegReg
  type Hadd232i = Hadd232iImmImm | Hadd232iRegRegImmImm
  type Hfma2 =
    Hfma2NoOperands | Hfma2Const | Hfma2ImmImm | Hfma2RegRegAddressImmReg | Hfma2RegRegAddressImmRegPred | Hfma2RegRegConstReg | Hfma2RegRegConstRegPred | Hfma2RegRegImmImmReg | Hfma2RegRegImmImmRegPred | Hfma2RegRegRegAddressImm | Hfma2RegRegRegAddressImmPred | Hfma2RegRegRegConst | Hfma2RegRegRegConstPred | Hfma2RegRegRegImmImm | Hfma2RegRegRegImmImmPred | Hfma2RegRegRegReg | Hfma2RegRegRegRegPred
  type Hfma232i = Hfma232iRegRegImmImm | Hfma232iRegRegImmImmReg
  type Hgmma =
    HgmmaRegAddressReg | HgmmaRegAddressRegPred | HgmmaRegAddressRegPredRegImm | HgmmaRegAddressRegReg | HgmmaRegAddressRegRegImm | HgmmaRegAddressRegRegPred | HgmmaRegAddressRegRegPredRegImm | HgmmaRegAddressRegRegRegImm | HgmmaRegRegAddressReg | HgmmaRegRegAddressRegPred | HgmmaRegRegAddressRegPredRegImm | HgmmaRegRegAddressRegRegImm
  type Hir0x002 = Hir0x002NoOperands
  type Hir0x003 = Hir0x003NoOperands | Hir0x003Pred
  type Hir0x004 = Hir0x004NoOperands
  type Hir0x005 = Hir0x005PredRegRegImm | Hir0x005RegRegImm
  type Hir0x006 = Hir0x006Imm
  type Hir0x007 = Hir0x007NoOperands
  type Hir0x008 = Hir0x008ImmRegReg
  type Hir0x020 = Hir0x020NoOperands | Hir0x020Imm
  type Hir0x120 =
    Hir0x120AddressImm | Hir0x120Const | Hir0x120Imm | Hir0x120PredAddressImm | Hir0x120PredConst | Hir0x120PredImm | Hir0x120PredReg | Hir0x120Reg
  type Hir0x130 = Hir0x130AddressAddressAddressAddressAddressAddressPred
  type Hir0x150 = Hir0x150RegRegRegRegPredRegRegReg | Hir0x150RegRegRegRegRegRegReg
  type Hir0x160 = Hir0x160Reg
  type Hir0x170 = Hir0x170Reg
  type Hir0x1a0 = Hir0x1a0RegRegRegRegImmImm
  type Hir0x1b0 = Hir0x1b0RegRegRegReg
  type Hir0x1e0 = Hir0x1e0NoOperands
  type Hir0x1f0 = Hir0x1f0ImmReg
  type Hir0x1f2 = Hir0x1f2Reg
  type Hir0x1f3 = Hir0x1f3Reg
  type Hmma =
    HmmaAddressRegRegAddress | HmmaAddressRegRegAddressPred | HmmaAddressRegRegAddressPredRegImm | HmmaAddressRegRegAddressRegImm | HmmaRegRegRegReg | HmmaRegRegRegRegPred | HmmaRegRegRegRegPredRegImm | HmmaRegRegRegRegRegImm
  type Hmnmx2 =
    Hmnmx2RegPredPredRegAddressImmPred | Hmnmx2RegPredPredRegConstPred | Hmnmx2RegPredPredRegImmImmPred | Hmnmx2RegPredPredRegRegPred | Hmnmx2RegRegAddressImmPred | Hmnmx2RegRegConstPred | Hmnmx2RegRegImmImmPred | Hmnmx2RegRegRegPred
  type Hmul2 =
    Hmul2NoOperands | Hmul2Const | Hmul2ImmImm | Hmul2RegRegAddressImm | Hmul2RegRegConst | Hmul2RegRegImmImm | Hmul2RegRegReg
  type Hmul232i = Hmul232iImmImm | Hmul232iRegRegImmImm
  type Hset2 =
    Hset2NoOperands | Hset2Const | Hset2ConstPred | Hset2ImmImm | Hset2ImmImmPred | Hset2Pred | Hset2RegRegAddressImm | Hset2RegRegAddressImmPred | Hset2RegRegConst | Hset2RegRegConstPred | Hset2RegRegImmImm | Hset2RegRegImmImmPred | Hset2RegRegReg | Hset2RegRegRegPred
  type Hsetp2 =
    Hsetp2PredPredRegAddressImm | Hsetp2PredPredRegAddressImmPred | Hsetp2PredPredRegConst | Hsetp2PredPredRegConstPred | Hsetp2PredPredRegImmImm | Hsetp2PredPredRegImmImmPred | Hsetp2PredPredRegReg | Hsetp2PredPredRegRegPred
  type I2f = I2fRegAddressImm | I2fRegConst | I2fRegImm | I2fRegReg
  type I2fp = I2fpRegAddressImm | I2fpRegConst | I2fpRegImm | I2fpRegReg
  type I2i = I2iRegAddressImm | I2iRegConst | I2iRegImm | I2iRegReg
  type I2ip = I2ipRegRegAddressImmReg | I2ipRegRegConstReg | I2ipRegRegImmReg | I2ipRegRegRegReg
  type Iabs = IabsRegAddressImm | IabsRegConst | IabsRegImm | IabsRegReg
  type Iadd =
    IaddRegPredRegAddressImm | IaddRegPredRegAddressImmPred | IaddRegPredRegConst | IaddRegPredRegConstPred | IaddRegPredRegImm | IaddRegPredRegImmPred | IaddRegPredRegImmReg | IaddRegPredRegImmRegPred | IaddRegPredRegReg | IaddRegPredRegRegPred | IaddRegPredRegRegReg | IaddRegPredRegRegRegPred | IaddRegRegAddressImm | IaddRegRegAddressImmPred | IaddRegRegConst | IaddRegRegConstPred | IaddRegRegImm | IaddRegRegImmPred | IaddRegRegImmReg | IaddRegRegImmRegPred | IaddRegRegReg | IaddRegRegRegPred | IaddRegRegRegReg | IaddRegRegRegRegPred
  type Iadd3 =
    Iadd3RegPredPredRegAddressImmReg | Iadd3RegPredPredRegAddressImmRegPredPred | Iadd3RegPredPredRegConstReg | Iadd3RegPredPredRegConstRegPredPred | Iadd3RegPredPredRegImmReg | Iadd3RegPredPredRegImmRegPredPred | Iadd3RegPredPredRegRegReg | Iadd3RegPredPredRegRegRegPredPred | Iadd3RegPredRegAddressImmReg | Iadd3RegPredRegAddressImmRegPredPred | Iadd3RegPredRegConstReg | Iadd3RegPredRegConstRegPredPred | Iadd3RegPredRegImmReg | Iadd3RegPredRegImmRegPredPred | Iadd3RegPredRegRegReg | Iadd3RegPredRegRegRegPredPred | Iadd3RegRegAddressImmReg | Iadd3RegRegAddressImmRegPredPred | Iadd3RegRegConstReg | Iadd3RegRegConstRegPredPred | Iadd3RegRegImmReg | Iadd3RegRegImmRegPredPred | Iadd3RegRegRegReg | Iadd3RegRegRegRegPredPred
  type Iadd32i = Iadd32iRegPredRegImm | Iadd32iRegPredRegImmPred | Iadd32iRegRegImm | Iadd32iRegRegImmPred
  type Icmp = IcmpRegRegConstReg | IcmpRegRegImmReg | IcmpRegRegRegConst | IcmpRegRegRegReg
  type Ide = IdeNoOperands | IdeImm
  type Idp = IdpRegRegAddressImmReg | IdpRegRegConstReg | IdpRegRegRegReg
  type Idp4a = Idp4aRegRegAddressImmReg | Idp4aRegRegConstReg | Idp4aRegRegRegReg
  type Igmma =
    IgmmaRegAddressReg | IgmmaRegAddressRegPred | IgmmaRegAddressRegPredReg | IgmmaRegAddressRegPredRegImm | IgmmaRegAddressRegReg | IgmmaRegAddressRegRegImm | IgmmaRegAddressRegRegPred | IgmmaRegAddressRegRegPredReg | IgmmaRegAddressRegRegPredRegImm | IgmmaRegAddressRegRegReg | IgmmaRegAddressRegRegRegImm | IgmmaRegRegAddressReg | IgmmaRegRegAddressRegPred | IgmmaRegRegAddressRegPredReg | IgmmaRegRegAddressRegPredRegImm | IgmmaRegRegAddressRegReg | IgmmaRegRegAddressRegRegImm
  type Imad =
    ImadRegPredRegAddressImmReg | ImadRegPredRegAddressImmRegPred | ImadRegPredRegConstReg | ImadRegPredRegConstRegPred | ImadRegPredRegImmReg | ImadRegPredRegImmRegPred | ImadRegPredRegRegAddressImm | ImadRegPredRegRegAddressImmPred | ImadRegPredRegRegConst | ImadRegPredRegRegConstPred | ImadRegPredRegRegReg | ImadRegPredRegRegRegPred | ImadRegRegAddressImmReg | ImadRegRegAddressImmRegPred | ImadRegRegConstReg | ImadRegRegConstRegPred | ImadRegRegImmReg | ImadRegRegImmRegPred | ImadRegRegRegAddressImm | ImadRegRegRegAddressImmPred | ImadRegRegRegConst | ImadRegRegRegConstPred | ImadRegRegRegImm | ImadRegRegRegImmPred | ImadRegRegRegReg | ImadRegRegRegRegPred
  type Imad32i = Imad32iRegRegImmReg
  type Imadsp = ImadspRegRegConstReg | ImadspRegRegImmReg | ImadspRegRegRegConst | ImadspRegRegRegReg
  type Imma = ImmaRegRegRegReg | ImmaRegRegRegRegPred | ImmaRegRegRegRegPredRegImm | ImmaRegRegRegRegRegImm
  type Imnmx =
    ImnmxPredPredRegRegImmPred | ImnmxPredPredRegRegImmPredPred | ImnmxPredPredRegRegRegPred | ImnmxPredPredRegRegRegPredPred | ImnmxPredRegRegImmPred | ImnmxPredRegRegImmPredPred | ImnmxPredRegRegRegPred | ImnmxPredRegRegRegPredPred | ImnmxRegRegAddressImmPred | ImnmxRegRegConstPred | ImnmxRegRegImmPred | ImnmxRegRegImmPredPred | ImnmxRegRegRegPred | ImnmxRegRegRegPredPred
  type Imul =
    ImulRegPredRegAddressImm | ImulRegPredRegConst | ImulRegPredRegImm | ImulRegPredRegReg | ImulRegRegAddressImm | ImulRegRegConst | ImulRegRegImm | ImulRegRegImmReg | ImulRegRegReg | ImulRegRegRegImm | ImulRegRegRegReg
  type Imul32i = Imul32iRegPredRegImm | Imul32iRegRegImm
  type Ipa =
    IpaRegAddress | IpaRegAddressImm | IpaRegAddressPred | IpaRegAddressReg | IpaRegAddressRegReg | IpaRegAddressRegRegPred | IpaRegImm | IpaRegImmConst | IpaRegImmImm | IpaRegImmPred | IpaRegImmReg | IpaRegImmRegReg | IpaRegImmRegRegPred | IpaRegPredAddress | IpaRegPredAddressImm | IpaRegPredAddressReg | IpaRegPredImm | IpaRegPredImmConst | IpaRegPredImmImm | IpaRegPredImmReg
  type Isad = IsadRegRegConstReg | IsadRegRegImmReg | IsadRegRegRegConst | IsadRegRegRegReg
  type Isberd = IsberdRegAddress
  type Isbewr = IsbewrAddressReg
  type Iscadd =
    IscaddRegPredRegAddressImmImm | IscaddRegPredRegConstImm | IscaddRegPredRegImmImm | IscaddRegPredRegRegImm | IscaddRegRegAddressImmImm | IscaddRegRegConstImm | IscaddRegRegImmImm | IscaddRegRegRegImm
  type Iscadd32i = Iscadd32iRegPredRegImmImm | Iscadd32iRegRegImmImm
  type Iset =
    IsetRegRegConst | IsetRegRegConstPred | IsetRegRegImm | IsetRegRegImmPred | IsetRegRegReg | IsetRegRegRegPred
  type Isetp =
    IsetpPredPredRegAddressImmPred | IsetpPredPredRegAddressImmPredPred | IsetpPredPredRegConstPred | IsetpPredPredRegConstPredPred | IsetpPredPredRegImmPred | IsetpPredPredRegImmPredPred | IsetpPredPredRegRegPred | IsetpPredPredRegRegPredPred | IsetpPredRegAddressImm | IsetpPredRegAddressImmPred | IsetpPredRegConst | IsetpPredRegConstPred | IsetpPredRegImm | IsetpPredRegImmPred | IsetpPredRegReg | IsetpPredRegRegPred
  type Isub = IsubRegConstReg | IsubRegImmReg | IsubRegRegConst | IsubRegRegImm | IsubRegRegReg
  type Jcal = JcalConst | JcalImm
  type Jmp =
    JmpConst | JmpImm | JmpPredConst | JmpPredImm | JmpPredPredConst | JmpPredPredImm | JmpPredRegConst | JmpPredRegImm | JmpRegConst | JmpRegImm | JmpSymbolConst | JmpSymbolImm | JmpSymbolImmImm | JmpSymbolSymbolImmImm
  type Jmx =
    JmxConst | JmxPredReg | JmxPredRegImm | JmxReg | JmxRegImm | JmxSymbolConst | JmxSymbolImmAddress | JmxSymbolReg | JmxSymbolRegImm | JmxSymbolSymbolImmAddress
  type Jmxu = JmxuPredReg | JmxuPredRegImm | JmxuReg | JmxuRegImm
  type Kil = KilNoOperands
  type Kill = KillNoOperands | KillPred
  type Ld = LdPredRegAddress | LdPredRegAddressPred | LdRegAddress | LdRegAddressPred
  type Ldc = LdcRegAddressAddress | LdcRegConst
  type Ldcu =
    LdcuRegAddress | LdcuRegAddressAddress | LdcuRegAddressPred | LdcuRegConst | LdcuRegRegAddress | LdcuRegRegAddressAddress | LdcuRegRegAddressAddressImm | LdcuRegRegAddressImm | LdcuRegRegAddressImmPred | LdcuRegRegAddressPred | LdcuRegRegConst | LdcuRegRegConstImm
  type Ldg =
    LdgPredRegAddress | LdgPredRegAddressPred | LdgRegAddress | LdgRegAddressPred | LdgRegRegAddress | LdgRegRegAddressImm | LdgRegRegAddressImmPred | LdgRegRegAddressPred
  type Ldgdepbar = LdgdepbarNoOperands
  type Ldgmc = LdgmcRegAddress
  type Ldgsts = LdgstsAddressAddress | LdgstsAddressAddressPred
  type Ldl = LdlRegAddress
  type Lds = LdsPredRegAddress | LdsRegAddress
  type Ldslk = LdslkPredRegAddress
  type Ldsm = LdsmRegAddress
  type Ldt = LdtRegAddress
  type Ldtm = LdtmRegAddress | LdtmRegRegAddress
  type Ldtram = LdtramRegAddress
  type Lea =
    LeaPredRegRegConst | LeaPredRegRegConstImm | LeaPredRegRegConstReg | LeaPredRegRegConstRegImm | LeaPredRegRegImm | LeaPredRegRegImmImm | LeaPredRegRegReg | LeaPredRegRegRegImm | LeaPredRegRegRegReg | LeaPredRegRegRegRegImm | LeaRegPredRegAddressImmImm | LeaRegPredRegAddressImmImmPred | LeaRegPredRegAddressImmRegImm | LeaRegPredRegAddressImmRegImmPred | LeaRegPredRegConstImm | LeaRegPredRegConstImmPred | LeaRegPredRegConstRegImm | LeaRegPredRegConstRegImmPred | LeaRegPredRegImmImm | LeaRegPredRegImmImmPred | LeaRegPredRegImmRegImm | LeaRegPredRegImmRegImmPred | LeaRegPredRegRegImm | LeaRegPredRegRegImmImm | LeaRegPredRegRegImmImmPred | LeaRegPredRegRegImmPred | LeaRegPredRegRegRegImm | LeaRegPredRegRegRegImmPred | LeaRegRegAddressImmImm | LeaRegRegAddressImmImmPred | LeaRegRegAddressImmRegImm | LeaRegRegAddressImmRegImmPred | LeaRegRegConst | LeaRegRegConstImm | LeaRegRegConstImmPred | LeaRegRegConstReg | LeaRegRegConstRegImm | LeaRegRegConstRegImmPred | LeaRegRegImm | LeaRegRegImmImm | LeaRegRegImmImmPred | LeaRegRegImmRegImm | LeaRegRegImmRegImmPred | LeaRegRegReg | LeaRegRegRegImm | LeaRegRegRegImmImm | LeaRegRegRegImmImmPred | LeaRegRegRegImmPred | LeaRegRegRegReg | LeaRegRegRegRegImm | LeaRegRegRegRegImmPred
  type Lepc = LepcReg | LepcRegImm
  type Longjmp = LongjmpNoOperands
  type Lop =
    LopPredRegRegAddressImm | LopPredRegRegAddressImmPred | LopPredRegRegConst | LopPredRegRegConstPred | LopPredRegRegImm | LopPredRegRegImmPred | LopPredRegRegReg | LopPredRegRegRegPred | LopRegRegAddressImm | LopRegRegAddressImmPred | LopRegRegConst | LopRegRegConstPred | LopRegRegImm | LopRegRegImmPred | LopRegRegReg | LopRegRegRegPred
  type Lop3 =
    Lop3PredRegRegAddressImmReg | Lop3PredRegRegAddressImmRegImm | Lop3PredRegRegAddressImmRegImmPred | Lop3PredRegRegAddressImmRegPred | Lop3PredRegRegConstReg | Lop3PredRegRegConstRegImm | Lop3PredRegRegConstRegImmPred | Lop3PredRegRegConstRegPred | Lop3PredRegRegImmReg | Lop3PredRegRegImmRegImm | Lop3PredRegRegImmRegImmPred | Lop3PredRegRegImmRegPred | Lop3PredRegRegRegReg | Lop3PredRegRegRegRegImm | Lop3PredRegRegRegRegImmPred | Lop3PredRegRegRegRegPred | Lop3RegRegAddressImmReg | Lop3RegRegAddressImmRegImm | Lop3RegRegAddressImmRegImmPred | Lop3RegRegAddressImmRegPred | Lop3RegRegConstReg | Lop3RegRegConstRegImm | Lop3RegRegConstRegImmPred | Lop3RegRegConstRegPred | Lop3RegRegImmReg | Lop3RegRegImmRegImm | Lop3RegRegImmRegImmPred | Lop3RegRegImmRegPred | Lop3RegRegRegReg | Lop3RegRegRegRegImm | Lop3RegRegRegRegImmPred | Lop3RegRegRegRegPred
  type Lop32i = Lop32iPredRegRegImm | Lop32iPredRegRegImmPred | Lop32iRegRegImm | Lop32iRegRegImmPred
  type Match = MatchPredRegReg | MatchRegReg
  type Membar = MembarNoOperands
  type Mov =
    MovAddressConst | MovAddressConstImm | MovAddressImm | MovAddressImmImm | MovAddressReg | MovAddressRegImm | MovRegAddress | MovRegAddressImm | MovRegAddressImmImm | MovRegConst | MovRegConstImm | MovRegImm | MovRegImmImm | MovRegReg | MovRegRegImm
  type Mov32i = Mov32iRegImm | Mov32iRegImmImm
  type Mov64iur = Mov64iurRegImm | Mov64iurRegReg
  type Movm = MovmRegReg
  type Mufu = MufuRegAddressImm | MufuRegConst | MufuRegImm | MufuRegImmImm | MufuRegReg
  type Nanosleep =
    NanosleepNoOperands | NanosleepAddressImm | NanosleepConst | NanosleepImm | NanosleepPred | NanosleepPredAddressImm | NanosleepPredConst | NanosleepPredImm | NanosleepPredReg | NanosleepReg
  type Nanotrap = NanotrapConst | NanotrapImm | NanotrapPredConst | NanotrapPredImm | NanotrapPredReg | NanotrapReg
  type Nop = NopNoOperands | NopImm | NopSymbol | NopSymbolImm
  type Omma =
    OmmaRegRegRegRegPredRegRegReg | OmmaRegRegRegRegPredRegRegRegImm | OmmaRegRegRegRegRegRegReg | OmmaRegRegRegRegRegRegRegImm
  type Out = OutReg | OutRegRegConst | OutRegRegImm | OutRegRegReg
  type P2r =
    P2rReg | P2rRegRegConst | P2rRegRegImm | P2rRegRegReg | P2rRegSymbol | P2rRegSymbolRegAddressImm | P2rRegSymbolRegConst | P2rRegSymbolRegImm | P2rRegSymbolRegReg
  type Pbk = PbkConst | PbkImm
  type Pcnt = PcntConst | PcntImm
  type Pexit = PexitImm
  type Pixld = PixldReg | PixldRegAddress | PixldRegPred | PixldRegPredAddress
  type Plongjmp = PlongjmpConst | PlongjmpImm
  type Plop3 =
    Plop3PredPredAddressImmPredImm | Plop3PredPredAddressImmRegImm | Plop3PredPredConstPredImm | Plop3PredPredConstRegImm | Plop3PredPredPredAddressImmPredImmImm | Plop3PredPredPredAddressImmRegImmImm | Plop3PredPredPredConstPredImmImm | Plop3PredPredPredConstRegImmImm | Plop3PredPredPredPred | Plop3PredPredPredPredImm | Plop3PredPredPredPredPredImmImm | Plop3PredPredPredRegPredImmImm | Plop3PredPredPredRegRegImmImm | Plop3PredPredRegAddressImmRegImmImm | Plop3PredPredRegConstRegImmImm | Plop3PredPredRegPredImm | Plop3PredPredRegRegImm | Plop3PredPredRegRegRegImmImm | Plop3PredRegAddressImmRegImm | Plop3PredRegConstRegImm | Plop3PredRegRegRegImm
  type Pmtrig = PmtrigImm | PmtrigPredImm
  type Popc =
    PopcRegAddressImm | PopcRegConst | PopcRegImm | PopcRegReg | PopcRegRegConst | PopcRegRegImm | PopcRegRegReg
  type Preexit = PreexitNoOperands
  type Pret = PretConst | PretImm
  type Prmt =
    PrmtRegRegAddressImmReg | PrmtRegRegConstReg | PrmtRegRegImmReg | PrmtRegRegRegAddressImm | PrmtRegRegRegConst | PrmtRegRegRegImm | PrmtRegRegRegReg
  type Pset = PsetRegPredPred | PsetRegPredPredPred
  type Psetp = PsetpPredPredPred | PsetpPredPredPredPredPred
  type Qadd4 = Qadd4RegRegImm | Qadd4RegRegReg
  type Qfma4 = Qfma4RegRegImmReg | Qfma4RegRegRegImm | Qfma4RegRegRegReg
  type Qgmma =
    QgmmaRegAddressReg | QgmmaRegAddressRegPred | QgmmaRegAddressRegPredReg | QgmmaRegAddressRegPredRegImm | QgmmaRegAddressRegReg | QgmmaRegAddressRegRegImm | QgmmaRegAddressRegRegPred | QgmmaRegAddressRegRegPredReg | QgmmaRegAddressRegRegPredRegImm | QgmmaRegAddressRegRegReg | QgmmaRegAddressRegRegRegImm | QgmmaRegRegAddressReg | QgmmaRegRegAddressRegPred | QgmmaRegRegAddressRegPredReg | QgmmaRegRegAddressRegPredRegImm | QgmmaRegRegAddressRegReg | QgmmaRegRegAddressRegRegImm
  type Qmma =
    QmmaRegRegRegReg | QmmaRegRegRegRegPred | QmmaRegRegRegRegPredRegImm | QmmaRegRegRegRegPredRegRegReg | QmmaRegRegRegRegPredRegRegRegImm | QmmaRegRegRegRegRegImm | QmmaRegRegRegRegRegRegReg | QmmaRegRegRegRegRegRegRegImm
  type Qmul4 = Qmul4RegRegImm | Qmul4RegRegReg
  type Qspc = QspcPredAddress | QspcPredRegAddress | QspcRegAddress
  type R2b = R2bImmReg
  type R2p =
    R2pReg | R2pRegConst | R2pRegImm | R2pRegReg | R2pSymbolReg | R2pSymbolRegAddressImm | R2pSymbolRegConst | R2pSymbolRegImm | R2pSymbolRegReg
  type R2ur = R2urPredRegReg | R2urRegReg
  type Ram = RamNoOperands
  type Red = RedAddressReg
  type Redas = RedasAddressReg
  type Redg = RedgAddressReg
  type Reds = RedsAddressReg
  type Redux = ReduxRegReg
  type Ret = RetNoOperands | RetPredReg | RetPredRegImm | RetReg | RetRegImm
  type Rpcmov = RpcmovAddressImm | RpcmovConst | RpcmovImm | RpcmovReg
  type Rro = RroRegConst | RroRegImm | RroRegReg
  type Rtt = RttNoOperands
  type S2r = S2rRegSpecial
  type S2ur = S2urRegSpecial
  type Sam = SamNoOperands
  type Scatter = ScatterRegRegRegRegImmImm
  type Sel = SelRegRegAddressImmPred | SelRegRegConstPred | SelRegRegImmPred | SelRegRegRegPred
  type Setcrsptr = SetcrsptrReg
  type Setctaid = SetctaidReg
  type Setlmembase = SetlmembaseReg
  type Sgxt = SgxtRegRegAddressImm | SgxtRegRegConst | SgxtRegRegImm | SgxtRegRegReg
  type Shf =
    ShfRegRegAddressImmReg | ShfRegRegConstReg | ShfRegRegImmReg | ShfRegRegRegAddressImm | ShfRegRegRegConst | ShfRegRegRegImm | ShfRegRegRegReg
  type Shfl = ShflPredRegRegImmImm | ShflPredRegRegImmReg | ShflPredRegRegRegImm | ShflPredRegRegRegReg
  type Shint = ShintImmImm | ShintImmImmImmImmImmImmImmImmImmImmImmImmImmImm
  type Shl = ShlRegImmReg | ShlRegRegAddressImm | ShlRegRegConst | ShlRegRegImm | ShlRegRegReg
  type Shr = ShrRegRegAddressImm | ShrRegRegConst | ShrRegRegImm | ShrRegRegReg
  type Spmetadata = SpmetadataRegRegRegImmImmImm
  type Ssy = SsyConst | SsyImm
  type St = StAddressReg | StAddressRegPred
  type Stas = StasAddressReg
  type Stg = StgAddressReg | StgAddressRegReg | StgAddressRegRegImm
  type Stl = StlAddressReg
  type Stp = StpImm
  type Sts = StsAddressReg
  type Stscul = StsculPredAddressReg
  type Stsm = StsmAddressReg
  type Stt = SttAddressReg
  type Sttm = SttmAddressReg
  type Suatom =
    SuatomPredRegAddressRegImm | SuatomPredRegAddressRegImmImm | SuatomPredRegAddressRegImmImmImm | SuatomPredRegAddressRegReg | SuatomPredRegAddressRegRegImm | SuatomPredRegAddressRegRegReg | SuatomPredRegAddressRegRegRegImm | SuatomPredRegAddressRegRegRegReg | SuatomRegAddressRegImm | SuatomRegAddressRegImmImm | SuatomRegAddressRegImmImmImm | SuatomRegAddressRegReg | SuatomRegAddressRegRegImm | SuatomRegAddressRegRegReg | SuatomRegAddressRegRegRegImm | SuatomRegAddressRegRegRegReg
  type Subfm = SubfmPredRegRegConstReg | SubfmPredRegRegImmReg | SubfmPredRegRegRegConst | SubfmPredRegRegRegReg
  type Suclamp = SuclampPredRegRegConstImm | SuclampPredRegRegImmImm | SuclampPredRegRegRegImm
  type Sueau = SueauRegRegConstReg | SueauRegRegImmReg | SueauRegRegRegConst | SueauRegRegRegReg
  type Suld =
    SuldPredRegAddressImm | SuldPredRegAddressImmImm | SuldPredRegAddressImmImmImm | SuldPredRegAddressReg | SuldPredRegAddressRegImm | SuldPredRegAddressRegReg | SuldPredRegAddressRegRegImm | SuldPredRegAddressRegRegReg | SuldRegAddressImm | SuldRegAddressImmImm | SuldRegAddressImmImmImm | SuldRegAddressReg | SuldRegAddressRegImm | SuldRegAddressRegReg | SuldRegAddressRegRegImm | SuldRegAddressRegRegReg
  type Suldga = SuldgaRegAddressConstPred | SuldgaRegAddressRegPred
  type Suquery =
    SuqueryPredRegAddressImmImm | SuqueryPredRegAddressImmImmImm | SuqueryPredRegAddressReg | SuqueryPredRegAddressRegImm | SuqueryPredRegAddressRegReg | SuqueryPredRegAddressRegRegImm | SuqueryPredRegAddressRegRegReg | SuqueryRegAddressImmImm | SuqueryRegAddressImmImmImm | SuqueryRegAddressReg | SuqueryRegAddressRegImm | SuqueryRegAddressRegReg | SuqueryRegAddressRegRegImm | SuqueryRegAddressRegRegReg
  type Sured =
    SuredAddressRegImm | SuredAddressRegImmImm | SuredAddressRegImmImmImm | SuredAddressRegReg | SuredAddressRegRegImm | SuredAddressRegRegReg | SuredAddressRegRegRegImm | SuredAddressRegRegRegReg
  type Sust =
    SustAddressRegImm | SustAddressRegImmImm | SustAddressRegImmImmImm | SustAddressRegReg | SustAddressRegRegImm | SustAddressRegRegReg | SustAddressRegRegRegImm | SustAddressRegRegRegReg
  type Sustga = SustgaAddressConstRegPred | SustgaAddressRegRegPred
  type Sync = SyncNoOperands
  type Syncs =
    SyncsNoOperands | SyncsAddress | SyncsAddressReg | SyncsPredAddressReg | SyncsRegAddress | SyncsRegAddressReg | SyncsRegAddressRegReg
  type Tex =
    TexPredRegRegRegImm | TexPredRegRegRegImmImm | TexPredRegRegRegImmImmImm | TexPredRegRegRegReg | TexPredRegRegRegRegImm | TexPredRegRegRegRegImmImm | TexPredRegRegRegRegImmImmImm | TexPredRegRegRegRegImmImmImmImm | TexPredRegRegRegRegReg | TexPredRegRegRegRegRegImm | TexPredRegRegRegRegRegImmImm | TexPredRegRegRegRegRegReg | TexPredRegRegRegRegRegRegImm | TexPredRegRegRegRegRegRegImmImm | TexRegRegRegImm | TexRegRegRegImmImm | TexRegRegRegImmImmImm | TexRegRegRegReg | TexRegRegRegRegImm | TexRegRegRegRegImmImm | TexRegRegRegRegImmImmImm | TexRegRegRegRegImmImmImmImm | TexRegRegRegRegReg | TexRegRegRegRegRegImm | TexRegRegRegRegRegImmImm | TexRegRegRegRegRegReg | TexRegRegRegRegRegRegImm | TexRegRegRegRegRegRegImmImm
  type Texdepbar = TexdepbarImm
  type Texs = TexsRegRegRegRegImm
  type Tld =
    TldPredRegRegRegImm | TldPredRegRegRegImmImm | TldPredRegRegRegReg | TldPredRegRegRegRegImm | TldPredRegRegRegRegImmImm | TldPredRegRegRegRegImmImmImm | TldPredRegRegRegRegImmImmImmImm | TldPredRegRegRegRegReg | TldPredRegRegRegRegRegImm | TldPredRegRegRegRegRegImmImm | TldPredRegRegRegRegRegReg | TldPredRegRegRegRegRegRegImm | TldPredRegRegRegRegRegRegImmImm | TldRegRegRegImm | TldRegRegRegImmImm | TldRegRegRegReg | TldRegRegRegRegImm | TldRegRegRegRegImmImm | TldRegRegRegRegImmImmImm | TldRegRegRegRegImmImmImmImm | TldRegRegRegRegReg | TldRegRegRegRegRegImm | TldRegRegRegRegRegImmImm | TldRegRegRegRegRegReg | TldRegRegRegRegRegRegImm | TldRegRegRegRegRegRegImmImm
  type Tld4 =
    Tld4PredRegRegRegImm | Tld4PredRegRegRegImmImm | Tld4PredRegRegRegImmImmImm | Tld4PredRegRegRegReg | Tld4PredRegRegRegRegImm | Tld4PredRegRegRegRegImmImm | Tld4PredRegRegRegRegImmImmImm | Tld4PredRegRegRegRegImmImmImmImm | Tld4PredRegRegRegRegReg | Tld4PredRegRegRegRegRegImm | Tld4PredRegRegRegRegRegImmImm | Tld4PredRegRegRegRegRegReg | Tld4PredRegRegRegRegRegRegImm | Tld4PredRegRegRegRegRegRegImmImm | Tld4RegRegRegImm | Tld4RegRegRegImmImm | Tld4RegRegRegImmImmImm | Tld4RegRegRegReg | Tld4RegRegRegRegImm | Tld4RegRegRegRegImmImm | Tld4RegRegRegRegImmImmImm | Tld4RegRegRegRegImmImmImmImm | Tld4RegRegRegRegReg | Tld4RegRegRegRegRegImm | Tld4RegRegRegRegRegImmImm | Tld4RegRegRegRegRegReg | Tld4RegRegRegRegRegRegImm | Tld4RegRegRegRegRegRegImmImm
  type Tld4s = Tld4sRegRegRegRegImm
  type Tlds = TldsRegRegRegRegImm
  type Tmml =
    TmmlRegRegImm | TmmlRegRegImmImm | TmmlRegRegImmImmImm | TmmlRegRegRegImm | TmmlRegRegRegImmImm | TmmlRegRegRegImmImmImm | TmmlRegRegRegReg | TmmlRegRegRegRegImm | TmmlRegRegRegRegImmImm | TmmlRegRegRegRegImmImmImm | TmmlRegRegRegRegImmImmImmImm | TmmlRegRegRegRegReg | TmmlRegRegRegRegRegImm | TmmlRegRegRegRegRegImmImm
  type Txa = TxaRegRegImm | TxaRegRegImmImm | TxaRegRegImmImmImm
  type Txd =
    TxdPredRegRegRegImm | TxdPredRegRegRegImmImm | TxdPredRegRegRegImmImmImm | TxdPredRegRegRegReg | TxdPredRegRegRegRegImm | TxdPredRegRegRegRegImmImm | TxdPredRegRegRegRegImmImmImm | TxdPredRegRegRegRegImmImmImmImm | TxdPredRegRegRegRegReg | TxdPredRegRegRegRegRegImm | TxdPredRegRegRegRegRegImmImm | TxdPredRegRegRegRegRegReg | TxdPredRegRegRegRegRegRegImm | TxdPredRegRegRegRegRegRegImmImm | TxdRegRegRegImm | TxdRegRegRegImmImm | TxdRegRegRegImmImmImm | TxdRegRegRegReg | TxdRegRegRegRegImm | TxdRegRegRegRegImmImm | TxdRegRegRegRegImmImmImm | TxdRegRegRegRegImmImmImmImm | TxdRegRegRegRegReg | TxdRegRegRegRegRegImm | TxdRegRegRegRegRegImmImm | TxdRegRegRegRegRegReg | TxdRegRegRegRegRegRegImm | TxdRegRegRegRegRegRegImmImm
  type Txq =
    TxqRegRegImm | TxqRegRegImmImm | TxqRegRegImmImmImm | TxqRegRegImmImmImmImm | TxqRegRegRegImm | TxqRegRegRegImmImmImm | TxqRegRegRegImmImmImmImm | TxqRegRegRegRegImm | TxqRegRegRegRegImmImm
  type Ublkcp = UblkcpAddressAddressReg | UblkcpAddressAddressRegAddress
  type Ublkpf = UblkpfAddressReg | UblkpfAddressRegAddress
  type Ublkred = UblkredAddressAddressReg | UblkredAddressAddressRegAddress
  type Ubmsk = UbmskRegRegImm | UbmskRegRegReg
  type Ubrev = UbrevRegImm | UbrevRegReg
  type Ucgabar_Arv = Ucgabar_ArvNoOperands
  type Ucgabar_Wait = Ucgabar_WaitNoOperands
  type Ucgabararv = UcgabararvNoOperands
  type Ucgabarget = UcgabargetReg
  type Ucgabarset = UcgabarsetReg
  type Ucgabarwait = UcgabarwaitNoOperands
  type Uclea = UcleaRegPredRegImmImm | UcleaRegPredRegRegImm
  type Uf2f = Uf2fRegImm | Uf2fRegReg
  type Uf2fp =
    Uf2fpRegImm | Uf2fpRegImmReg | Uf2fpRegReg | Uf2fpRegRegImm | Uf2fpRegRegImmReg | Uf2fpRegRegReg | Uf2fpRegRegRegImm | Uf2fpRegRegRegReg
  type Uf2i = Uf2iRegImm | Uf2iRegReg
  type Uf2ip = Uf2ipRegRegImmReg | Uf2ipRegRegRegImm | Uf2ipRegRegRegReg
  type Ufadd = UfaddRegRegImm | UfaddRegRegReg
  type Uffma = UffmaRegRegImmReg | UffmaRegRegRegImm | UffmaRegRegRegReg
  type Ufhadd = UfhaddRegRegReg
  type Ufhfma = UfhfmaRegRegRegReg
  type Uflo = UfloRegImm | UfloRegPredImm | UfloRegPredReg | UfloRegReg
  type Ufmnmx = UfmnmxRegPredRegImmPred | UfmnmxRegPredRegRegPred | UfmnmxRegRegImmPred | UfmnmxRegRegRegPred
  type Ufmul = UfmulRegRegImm | UfmulRegRegReg
  type Ufrnd = UfrndRegImm | UfrndRegReg
  type Ufsel = UfselRegRegImmPred | UfselRegRegRegPred
  type Ufset = UfsetRegRegImm | UfsetRegRegImmPred | UfsetRegRegReg | UfsetRegRegRegPred
  type Ufsetp = UfsetpPredPredRegImmPred | UfsetpPredPredRegRegPred | UfsetpPredRegImm | UfsetpPredRegReg
  type Ugetnextworkid = UgetnextworkidAddressAddress
  type Ui2f = Ui2fRegImm | Ui2fRegReg
  type Ui2fp = Ui2fpRegImm | Ui2fpRegReg
  type Ui2i = Ui2iRegImm | Ui2iRegReg
  type Ui2ip = Ui2ipRegRegImmReg | Ui2ipRegRegRegReg
  type Uiabs = UiabsRegImm | UiabsRegReg
  type Uiadd3 =
    Uiadd3RegPredPredRegImmReg | Uiadd3RegPredPredRegImmRegPredPred | Uiadd3RegPredPredRegRegReg | Uiadd3RegPredPredRegRegRegPredPred | Uiadd3RegPredRegImmReg | Uiadd3RegPredRegImmRegPredPred | Uiadd3RegPredRegRegReg | Uiadd3RegPredRegRegRegPredPred | Uiadd3RegRegImmReg | Uiadd3RegRegImmRegPredPred | Uiadd3RegRegRegReg | Uiadd3RegRegRegRegPredPred
  type Uimad =
    UimadRegPredRegImmReg | UimadRegPredRegImmRegPred | UimadRegPredRegRegReg | UimadRegPredRegRegRegPred | UimadRegRegImmReg | UimadRegRegImmRegPred | UimadRegRegRegImm | UimadRegRegRegImmPred | UimadRegRegRegReg | UimadRegRegRegRegPred
  type Uimnmx =
    UimnmxPredPredRegRegImmPred | UimnmxPredPredRegRegImmPredPred | UimnmxPredPredRegRegRegPred | UimnmxPredPredRegRegRegPredPred | UimnmxPredRegRegImmPred | UimnmxPredRegRegImmPredPred | UimnmxPredRegRegRegPred | UimnmxPredRegRegRegPredPred | UimnmxRegRegImmPred | UimnmxRegRegImmPredPred | UimnmxRegRegRegPred | UimnmxRegRegRegPredPred
  type Uisetp =
    UisetpPredPredRegImmPred | UisetpPredPredRegImmPredPred | UisetpPredPredRegRegPred | UisetpPredPredRegRegPredPred | UisetpPredRegImm | UisetpPredRegImmPred | UisetpPredRegReg | UisetpPredRegRegPred
  type Uldc = UldcRegAddress | UldcRegAddressImm | UldcRegAddressPred | UldcRegConst
  type Ulea =
    UleaRegPredRegImmImm | UleaRegPredRegImmImmPred | UleaRegPredRegImmRegImm | UleaRegPredRegImmRegImmPred | UleaRegPredRegRegImm | UleaRegPredRegRegImmImm | UleaRegPredRegRegImmImmPred | UleaRegPredRegRegImmPred | UleaRegPredRegRegRegImm | UleaRegPredRegRegRegImmPred | UleaRegRegImmImm | UleaRegRegImmImmPred | UleaRegRegImmRegImm | UleaRegRegImmRegImmPred | UleaRegRegRegImm | UleaRegRegRegImmImm | UleaRegRegRegImmImmPred | UleaRegRegRegImmPred | UleaRegRegRegRegImm | UleaRegRegRegRegImmPred
  type Ulepc = UlepcReg | UlepcRegImm
  type Ulop =
    UlopPredRegRegImm | UlopPredRegRegImmPred | UlopPredRegRegReg | UlopPredRegRegRegPred | UlopRegRegImm | UlopRegRegImmPred | UlopRegRegReg | UlopRegRegRegPred
  type Ulop3 =
    Ulop3PredRegRegImmReg | Ulop3PredRegRegImmRegImm | Ulop3PredRegRegImmRegImmPred | Ulop3PredRegRegImmRegPred | Ulop3PredRegRegRegReg | Ulop3PredRegRegRegRegImm | Ulop3PredRegRegRegRegImmPred | Ulop3PredRegRegRegRegPred | Ulop3RegRegImmReg | Ulop3RegRegImmRegImm | Ulop3RegRegImmRegImmPred | Ulop3RegRegImmRegPred | Ulop3RegRegRegReg | Ulop3RegRegRegRegImm | Ulop3RegRegRegRegImmPred | Ulop3RegRegRegRegPred
  type Ulop32i = Ulop32iPredRegRegImm | Ulop32iPredRegRegImmPred | Ulop32iRegRegImm | Ulop32iRegRegImmPred
  type Umemsets = UmemsetsAddressRegReg
  type Umov = UmovRegImm | UmovRegReg
  type Up2ur = Up2urReg | Up2urRegRegImm | Up2urRegRegReg
  type Uplop3 =
    Uplop3PredPredPredPred | Uplop3PredPredPredPredImm | Uplop3PredPredPredPredPredImmImm | Uplop3PredPredPredRegPredImmImm | Uplop3PredPredPredRegRegImmImm | Uplop3PredPredRegPredImm | Uplop3PredPredRegRegImm | Uplop3PredPredRegRegRegImmImm | Uplop3PredRegRegRegImm
  type Upopc = UpopcRegImm | UpopcRegReg
  type Uprmt = UprmtRegRegImmReg | UprmtRegRegRegReg
  type Upsetp = UpsetpPredPredPred | UpsetpPredPredPredPredPred
  type Ur2up = Ur2upReg | Ur2upRegImm | Ur2upRegReg
  type Uredgr = UredgrAddressReg
  type Usel = UselRegRegImmPred | UselRegRegRegPred
  type Usetmaxreg = UsetmaxregImm | UsetmaxregPredImm | UsetmaxregPredReg | UsetmaxregReg
  type Usetshmsz = UsetshmszNoOperands | UsetshmszImm | UsetshmszReg
  type Usgxt = UsgxtRegRegImm | UsgxtRegRegReg
  type Ushf = UshfRegRegImmReg | UshfRegRegRegImm | UshfRegRegRegReg
  type Ushl = UshlRegImmReg | UshlRegRegImm | UshlRegRegReg
  type Ushr = UshrRegRegImm | UshrRegRegReg
  type Ustgr = UstgrAddressReg
  type Utcatomsws = UtcatomswsPredRegReg | UtcatomswsPredRegRegReg | UtcatomswsRegReg
  type Utcbar = UtcbarNoOperands | UtcbarAddressRegReg
  type Utccp = UtccpAddressAddress
  type Utchmma =
    UtchmmaAddressAddressAddressAddressAddressRegPred | UtchmmaAddressAddressAddressAddressAddressRegPredImm
  type Utcimma = UtcimmaAddressAddressAddressAddressAddressRegPred
  type Utcomma = UtcommaAddressAddressAddressAddressAddressAddressPred
  type Utcqmma =
    UtcqmmaAddressAddressAddressAddressAddressAddressPred | UtcqmmaAddressAddressAddressAddressAddressRegPred
  type Utcshift = UtcshiftAddress
  type Utmacctl = UtmacctlNoOperands | UtmacctlAddress
  type Utmacmdflush = UtmacmdflushNoOperands
  type Utmaldg =
    UtmaldgAddressAddress | UtmaldgAddressAddressAddress | UtmaldgAddressAddressReg | UtmaldgAddressAddressRegAddress
  type Utmapf =
    UtmapfAddressAddress | UtmapfAddressAddressAddress | UtmapfAddressAddressReg | UtmapfAddressAddressRegAddress
  type Utmaredg = UtmaredgAddressAddress | UtmaredgAddressAddressAddress
  type Utmastg = UtmastgAddressAddress | UtmastgAddressAddressAddress
  type Uviadd = UviaddRegRegImm | UviaddRegRegReg
  type Uvimnmx = UvimnmxRegRegImmPred | UvimnmxRegRegRegPred
  type Uvirtcount = UvirtcountImm | UvirtcountReg
  type Vabsdiff =
    VabsdiffRegPredRegAddressImmReg | VabsdiffRegPredRegConstReg | VabsdiffRegPredRegImmReg | VabsdiffRegPredRegRegAddressImm | VabsdiffRegPredRegRegConst | VabsdiffRegPredRegRegImm | VabsdiffRegPredRegRegReg | VabsdiffRegRegAddressImmReg | VabsdiffRegRegConstReg | VabsdiffRegRegImmReg | VabsdiffRegRegRegAddressImm | VabsdiffRegRegRegConst | VabsdiffRegRegRegImm | VabsdiffRegRegRegReg
  type Vabsdiff2 = Vabsdiff2RegRegImmReg | Vabsdiff2RegRegRegReg
  type Vabsdiff4 =
    Vabsdiff4RegPredRegAddressImmReg | Vabsdiff4RegPredRegConstReg | Vabsdiff4RegPredRegImmReg | Vabsdiff4RegPredRegRegAddressImm | Vabsdiff4RegPredRegRegConst | Vabsdiff4RegPredRegRegImm | Vabsdiff4RegPredRegRegReg | Vabsdiff4RegRegAddressImmReg | Vabsdiff4RegRegConstReg | Vabsdiff4RegRegImmReg | Vabsdiff4RegRegRegAddressImm | Vabsdiff4RegRegRegConst | Vabsdiff4RegRegRegImm | Vabsdiff4RegRegRegReg
  type Vadd = VaddRegRegImmReg | VaddRegRegRegReg
  type Vadd2 = Vadd2RegRegImmReg | Vadd2RegRegRegReg
  type Vadd4 = Vadd4RegRegImmReg | Vadd4RegRegRegReg
  type Vhmnmx =
    VhmnmxRegRegAddressImmRegPred | VhmnmxRegRegConstRegPred | VhmnmxRegRegImmImmRegPred | VhmnmxRegRegRegRegPred
  type Viadd = ViaddRegRegAddressImm | ViaddRegRegConst | ViaddRegRegImm | ViaddRegRegReg
  type Viaddmnmx =
    ViaddmnmxRegRegAddressImmRegPred | ViaddmnmxRegRegConstRegPred | ViaddmnmxRegRegImmRegPred | ViaddmnmxRegRegRegAddressImmPred | ViaddmnmxRegRegRegConstPred | ViaddmnmxRegRegRegImmPred | ViaddmnmxRegRegRegRegPred
  type Vild = VildRegAddress
  type Vimnmx =
    VimnmxRegPredPredRegAddressImmPred | VimnmxRegPredPredRegConstPred | VimnmxRegPredPredRegImmPred | VimnmxRegPredPredRegRegPred | VimnmxRegRegAddressImmPred | VimnmxRegRegConstPred | VimnmxRegRegImmPred | VimnmxRegRegRegPred
  type Vimnmx3 =
    Vimnmx3RegRegAddressImmRegPred | Vimnmx3RegRegConstRegPred | Vimnmx3RegRegImmRegPred | Vimnmx3RegRegRegRegPred
  type Vmad = VmadRegRegImmReg | VmadRegRegRegReg
  type Vmnmx = VmnmxRegRegImmReg | VmnmxRegRegRegReg
  type Vmnmx2 = Vmnmx2RegRegImmReg | Vmnmx2RegRegRegReg
  type Vmnmx4 = Vmnmx4RegRegImmReg | Vmnmx4RegRegRegReg
  type Vote = VoteImm | VotePredPred | VoteRegPredPred
  type VoteVtg = VoteVtgImm | VoteVtgPredImm | VoteVtgPredPredImmPred
  type Voteu = VoteuRegPredPred
  type Vsel2 = Vsel2RegRegImmPred | Vsel2RegRegRegPred
  type Vsel4 = Vsel4RegRegImmPred | Vsel4RegRegRegPred
  type Vset = VsetRegRegImmReg | VsetRegRegRegReg
  type Vset2 = Vset2RegRegImmReg | Vset2RegRegRegReg
  type Vset4 = Vset4RegRegImmReg | Vset4RegRegRegReg
  type Vsetp = VsetpPredPredRegImmPred | VsetpPredPredRegRegPred | VsetpPredRegImm | VsetpPredRegReg
  type Vshl = VshlRegRegImmReg | VshlRegRegRegReg
  type Vshr = VshrRegRegImmReg | VshrRegRegRegReg
  type Warpgroup = WarpgroupNoOperands | WarpgroupImm
  type Warpgroupset = WarpgroupsetReg
  type Warpsync =
    WarpsyncNoOperands | WarpsyncAddressImm | WarpsyncConst | WarpsyncImm | WarpsyncPred | WarpsyncPredAddressImm | WarpsyncPredConst | WarpsyncPredImm | WarpsyncPredReg | WarpsyncPredRegImm | WarpsyncReg | WarpsyncRegImm
  type Xmad = XmadRegRegConstReg | XmadRegRegImmReg | XmadRegRegRegConst | XmadRegRegRegReg
  type Yield = YieldNoOperands | YieldPred
}
@Generated(Array("sbt codegen"))
enum Form(operandValues: Operand*) derives CanEqual {
  val operands: Vec[Operand] = operandValues.toVec
  case AcqbulkNoOperands() extends Form()
  case AcqshminitNoOperands() extends Form()
  case Al2pPredRegReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register
  ) extends Form(dstPredicate, dst, srcA)
  case Al2pPredRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, sImm)
  case Al2pRegReg(dst: InstructionOperand.Register, srcA: InstructionOperand.Register) extends Form(dst, srcA)
  case Al2pRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      ra_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, ra_offset)
  case AldRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case ArrivesAddress(srcA: InstructionOperand.Address) extends Form(srcA)
  case AstAddressRegReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(srcA, srcB, srcC)
  case AtomPredRegAddress(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address
  ) extends Form(pu, dst, srcA)
  case AtomPredRegAddressReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register
  ) extends Form(dstPredicate, dst, srcA, srcB)
  case AtomPredRegAddressRegReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dstPredicate, dst, srcA, srcB, srcC)
  case AtomRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case AtomRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case AtomgPredRegAddressReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register
  ) extends Form(pu, dst, srcA, srcB)
  case AtomgPredRegAddressRegReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(pu, dst, srcA, srcB, srcC)
  case AtomsPredAddressRegReg(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(pu, srcA, srcB, srcC)
  case AtomsRegAddress(dst: InstructionOperand.Register, srcA: InstructionOperand.Address) extends Form(dst, srcA)
  case AtomsRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case AtomsRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case B2rReg(dst: InstructionOperand.Register) extends Form(dst)
  case B2rRegImm(dst: InstructionOperand.Register, barNum: InstructionOperand.Immediate) extends Form(dst, barNum)
  case B2rRegPred(dst: InstructionOperand.Register, dstPredicate: InstructionOperand.Predicate)
      extends Form(dst, dstPredicate)
  case BarNoOperands() extends Form()
  case BarImm(sb: InstructionOperand.Immediate) extends Form(sb)
  case BarImmImm(sb: InstructionOperand.Immediate, sc: InstructionOperand.Immediate) extends Form(sb, sc)
  case BarImmImmPred(
      sb: InstructionOperand.Immediate,
      sc: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(sb, sc, pp)
  case BarImmPred(sb: InstructionOperand.Immediate, pp: InstructionOperand.Predicate) extends Form(sb, pp)
  case BarImmReg(sb: InstructionOperand.Immediate, srcC: InstructionOperand.Register) extends Form(sb, srcC)
  case BarImmRegPred(
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(sb, srcC, pp)
  case BarReg(srcA: InstructionOperand.Register) extends Form(srcA)
  case BarRegImm(srcA: InstructionOperand.Register, uImm: InstructionOperand.Immediate) extends Form(srcA, uImm)
  case BarRegImmPred(
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate
  ) extends Form(srcA, uImm, pa)
  case BarRegPred(srcA: InstructionOperand.Register, pa: InstructionOperand.Predicate) extends Form(srcA, pa)
  case BarRegReg(srcA: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(srcA, srcB)
  case BarRegRegPred(
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate
  ) extends Form(srcA, srcB, pa)
  case BfeRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcConst)
  case BfeRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate
  ) extends Form(dst, srcA, uImm)
  case BfeRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case BfiRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcConst, srcC)
  case BfiRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, uImm, srcC)
  case BfiRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcB, srcConst)
  case BfiRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case BgmmaRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcC)
  case BgmmaRegAddressRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcC, uPp)
  case BgmmaRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case BgmmaRegAddressRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, uPp)
  case BgmmaRegRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case BgmmaRegRegAddressRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, uPp)
  case BmmaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case BmmaRegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, uPp)
  case BmovAddressImm(srcB: InstructionOperand.Address, sb_offset: InstructionOperand.Immediate)
      extends Form(srcB, sb_offset)
  case BmovConst(sb: InstructionOperand.ConstantBank) extends Form(sb)
  case BmovImm(sb: InstructionOperand.Immediate) extends Form(sb)
  case BmovReg(srcB: InstructionOperand.Register) extends Form(srcB)
  case BmovRegAddressImm(
      cbu_state: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(cbu_state, srcB, sb_offset)
  case BmovRegConst(cbu_state: InstructionOperand.Register, sb: InstructionOperand.ConstantBank)
      extends Form(cbu_state, sb)
  case BmovRegImm(cbu_state: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(cbu_state, sb)
  case BmovRegReg(dst: InstructionOperand.Register, cbu_state: InstructionOperand.Register) extends Form(dst, cbu_state)
  case BmovRegSymbol(dst: InstructionOperand.Register, cbu_state: InstructionOperand.Symbol)
      extends Form(dst, cbu_state)
  case BmovSymbolAddressImm(
      cbu_state: InstructionOperand.Symbol,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(cbu_state, srcB, sb_offset)
  case BmovSymbolConst(cbu_state: InstructionOperand.Symbol, sb: InstructionOperand.ConstantBank)
      extends Form(cbu_state, sb)
  case BmovSymbolImm(cbu_state: InstructionOperand.Symbol, sb: InstructionOperand.Immediate) extends Form(cbu_state, sb)
  case BmovSymbolReg(cbu_state: InstructionOperand.Symbol, srcB: InstructionOperand.Register)
      extends Form(cbu_state, srcB)
  case BmskRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sb_offset)
  case BmskRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, sb)
  case BmskRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case BmskRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case BptNoOperands() extends Form()
  case BptImm(sb: InstructionOperand.Immediate) extends Form(sb)
  case BraConst(srcConst: InstructionOperand.ConstantBank) extends Form(srcConst)
  case BraImm(sImm: InstructionOperand.Immediate) extends Form(sImm)
  case BraPredImm(pp: InstructionOperand.Predicate, sImm: InstructionOperand.Immediate) extends Form(pp, sImm)
  case BraPredPredImm(
      pp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      sImm: InstructionOperand.Immediate
  ) extends Form(pp, uPq, sImm)
  case BraPredRegImm(
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate
  ) extends Form(pp, srcB, sImm)
  case BraRegImm(srcB: InstructionOperand.Register, sImm: InstructionOperand.Immediate) extends Form(srcB, sImm)
  case BraSymbolConst(testCC: InstructionOperand.Symbol, srcConst: InstructionOperand.ConstantBank)
      extends Form(testCC, srcConst)
  case BraSymbolImm(testCC: InstructionOperand.Symbol, sImm: InstructionOperand.Immediate) extends Form(testCC, sImm)
  case BraSymbolImmImm(
      srcConst: InstructionOperand.Symbol,
      constBank: InstructionOperand.Immediate,
      immConstOffset: InstructionOperand.Immediate
  ) extends Form(srcConst, constBank, immConstOffset)
  case BraSymbolSymbolImmImm(
      testCC: InstructionOperand.Symbol,
      srcConst: InstructionOperand.Symbol,
      constBank: InstructionOperand.Immediate,
      immConstOffset: InstructionOperand.Immediate
  ) extends Form(testCC, srcConst, constBank, immConstOffset)
  case BreakPredReg(pp: InstructionOperand.Predicate, barReg: InstructionOperand.Register) extends Form(pp, barReg)
  case BreakReg(barReg: InstructionOperand.Register) extends Form(barReg)
  case BrevRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcB, sb_offset)
  case BrevRegConst(dst: InstructionOperand.Register, sb: InstructionOperand.ConstantBank) extends Form(dst, sb)
  case BrevRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case BrevRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case BrkNoOperands() extends Form()
  case BrxConst(srcConst: InstructionOperand.ConstantBank) extends Form(srcConst)
  case BrxPredReg(pp: InstructionOperand.Predicate, srcA: InstructionOperand.Register) extends Form(pp, srcA)
  case BrxPredRegImm(
      pp: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      ra_offset: InstructionOperand.Immediate
  ) extends Form(pp, srcA, ra_offset)
  case BrxReg(srcA: InstructionOperand.Register) extends Form(srcA)
  case BrxRegImm(srcA: InstructionOperand.Register, ra_offset: InstructionOperand.Immediate)
      extends Form(srcA, ra_offset)
  case BrxSymbolConst(testCC: InstructionOperand.Symbol, srcConst: InstructionOperand.ConstantBank)
      extends Form(testCC, srcConst)
  case BrxSymbolImmAddress(
      srcConst: InstructionOperand.Symbol,
      constBank: InstructionOperand.Immediate,
      srcA: InstructionOperand.Address
  ) extends Form(srcConst, constBank, srcA)
  case BrxSymbolReg(testCC: InstructionOperand.Symbol, srcA: InstructionOperand.Register) extends Form(testCC, srcA)
  case BrxSymbolRegImm(
      testCC: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate
  ) extends Form(testCC, srcA, sImm)
  case BrxSymbolSymbolImmAddress(
      testCC: InstructionOperand.Symbol,
      srcConst: InstructionOperand.Symbol,
      constBank: InstructionOperand.Immediate,
      srcA: InstructionOperand.Address
  ) extends Form(testCC, srcConst, constBank, srcA)
  case BrxuPredReg(pp: InstructionOperand.Predicate, srcA: InstructionOperand.Register) extends Form(pp, srcA)
  case BrxuPredRegImm(
      pp: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uR_offset: InstructionOperand.Immediate
  ) extends Form(pp, srcA, uR_offset)
  case BrxuReg(srcA: InstructionOperand.Register) extends Form(srcA)
  case BrxuRegImm(srcA: InstructionOperand.Register, uR_offset: InstructionOperand.Immediate)
      extends Form(srcA, uR_offset)
  case BssyPredRegImm(
      pp: InstructionOperand.Predicate,
      barReg: InstructionOperand.Register,
      sa: InstructionOperand.Immediate
  ) extends Form(pp, barReg, sa)
  case BssyRegImm(barReg: InstructionOperand.Register, sa: InstructionOperand.Immediate) extends Form(barReg, sa)
  case BsyncPredReg(pp: InstructionOperand.Predicate, barReg: InstructionOperand.Register) extends Form(pp, barReg)
  case BsyncReg(barReg: InstructionOperand.Register) extends Form(barReg)
  case CalConst(srcConst: InstructionOperand.ConstantBank) extends Form(srcConst)
  case CalImm(sImm: InstructionOperand.Immediate) extends Form(sImm)
  case CallConst(sa: InstructionOperand.ConstantBank) extends Form(sa)
  case CallImm(sa: InstructionOperand.Immediate) extends Form(sa)
  case CallPredConst(pp: InstructionOperand.Predicate, sa: InstructionOperand.ConstantBank) extends Form(pp, sa)
  case CallPredImm(pp: InstructionOperand.Predicate, sa: InstructionOperand.Immediate) extends Form(pp, sa)
  case CallPredReg(pp: InstructionOperand.Predicate, srcA: InstructionOperand.Register) extends Form(pp, srcA)
  case CallPredRegImm(
      pp: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      ra_offset: InstructionOperand.Immediate
  ) extends Form(pp, srcA, ra_offset)
  case CallReg(srcA: InstructionOperand.Register) extends Form(srcA)
  case CallRegImm(srcA: InstructionOperand.Register, ra_offset: InstructionOperand.Immediate)
      extends Form(srcA, ra_offset)
  case CctlNoOperands() extends Form()
  case CctlAddress(srcA: InstructionOperand.Address) extends Form(srcA)
  case CctlAddressAddress(srcA: InstructionOperand.Address, srcB: InstructionOperand.Address) extends Form(srcA, srcB)
  case CctlAddressImm(srcA: InstructionOperand.Address, sector_count: InstructionOperand.Immediate)
      extends Form(srcA, sector_count)
  case CctlConst(sa: InstructionOperand.ConstantBank) extends Form(sa)
  case CctlPredAddress(pu: InstructionOperand.Predicate, srcA: InstructionOperand.Address) extends Form(pu, srcA)
  case CctlRegAddress(dst: InstructionOperand.Register, srcA: InstructionOperand.Address) extends Form(dst, srcA)
  case CctllNoOperands() extends Form()
  case CctllAddress(srcA: InstructionOperand.Address) extends Form(srcA)
  case CctllRegAddress(dst: InstructionOperand.Register, srcA: InstructionOperand.Address) extends Form(dst, srcA)
  case CctlsNoOperands() extends Form()
  case CctltNoOperands() extends Form()
  case CctltImm(uImm: InstructionOperand.Immediate) extends Form(uImm)
  case CctltImmImm(sb_bank: InstructionOperand.Immediate, sb_offset: InstructionOperand.Immediate)
      extends Form(sb_bank, sb_offset)
  case CctltReg(srcB: InstructionOperand.Register) extends Form(srcB)
  case CgaerrbarNoOperands() extends Form()
  case ClmadRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, sb_offset, srcC)
  case ClmadRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case ClmadRegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, sc_offset)
  case ClmadRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcB, sc)
  case ClmadRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case ContNoOperands() extends Form()
  case CreduxRegReg(dst: InstructionOperand.Register, srcA: InstructionOperand.Register) extends Form(dst, srcA)
  case Cs2rRegSpecial(dst: InstructionOperand.Register, sR: InstructionOperand.SpecialRegister) extends Form(dst, sR)
  case Cs2urRegSpecial(dst: InstructionOperand.Register, sRa: InstructionOperand.SpecialRegister) extends Form(dst, sRa)
  case CsetRegSymbol(dst: InstructionOperand.Register, dummyCC: InstructionOperand.Symbol) extends Form(dst, dummyCC)
  case CsetRegSymbolPred(
      dst: InstructionOperand.Register,
      dummyCC: InstructionOperand.Symbol,
      pa: InstructionOperand.Predicate
  ) extends Form(dst, dummyCC, pa)
  case CsetpPredPredSymbolPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      inputCC: InstructionOperand.Symbol,
      pa: InstructionOperand.Predicate
  ) extends Form(dstPredicate, nPd, inputCC, pa)
  case CsetpPredSymbol(dstPredicate: InstructionOperand.Predicate, inputCC: InstructionOperand.Symbol)
      extends Form(dstPredicate, inputCC)
  case CsmtestImm(sa: InstructionOperand.Immediate) extends Form(sa)
  case CsmtestPredImm(pu: InstructionOperand.Predicate, sa: InstructionOperand.Immediate) extends Form(pu, sa)
  case CsmtestPredPredImmPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      sa: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, pv, sa, pp)
  case DaddRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcC, sc_offset)
  case DaddRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, sc)
  case DaddRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sc)
  case DaddRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case DchkPredRegConst(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dstPredicate, srcA, srcConst)
  case DchkPredRegImm(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate
  ) extends Form(dstPredicate, srcA, uImm)
  case DchkPredRegReg(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dstPredicate, srcA, srcB)
  case DepbarNoOperands() extends Form()
  case DepbarGroup(dep_scbd: InstructionOperand.Group) extends Form(dep_scbd)
  case DepbarImm(dep_scbd: InstructionOperand.Immediate) extends Form(dep_scbd)
  case DepbarRegImm(sbidx: InstructionOperand.Register, cnt: InstructionOperand.Immediate) extends Form(sbidx, cnt)
  case DepbarRegImmGroup(
      sbidx: InstructionOperand.Register,
      cnt: InstructionOperand.Immediate,
      dep_scbd: InstructionOperand.Group
  ) extends Form(sbidx, cnt, dep_scbd)
  case DepbarRegImmImm(
      sbidx: InstructionOperand.Register,
      cnt: InstructionOperand.Immediate,
      dep_scbd: InstructionOperand.Immediate
  ) extends Form(sbidx, cnt, dep_scbd)
  case DepbarRegReg(sbidx: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(sbidx, srcB)
  case DepbarRegRegGroup(
      sbidx: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scoreboard_list: InstructionOperand.Group
  ) extends Form(sbidx, srcB, scoreboard_list)
  case DepbarRegRegImm(
      sbidx: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scoreboard_list: InstructionOperand.Immediate
  ) extends Form(sbidx, srcB, scoreboard_list)
  case DfmaRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, sb_offset, srcC)
  case DfmaRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case DfmaRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case DfmaRegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, sc_offset)
  case DfmaRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcB, sc)
  case DfmaRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sc)
  case DfmaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case DmmaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case DmmaRegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, uPp)
  case DmnmxRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      pa: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcConst, pa)
  case DmnmxRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate
  ) extends Form(dst, srcA, uImm, pa)
  case DmnmxRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, pa)
  case DmulRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sb_offset)
  case DmulRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, sb)
  case DmulRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case DmulRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case DsetRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcConst)
  case DsetRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      pa: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcConst, pa)
  case DsetRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate
  ) extends Form(dst, srcA, uImm)
  case DsetRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate
  ) extends Form(dst, srcA, uImm, pa)
  case DsetRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case DsetRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, pa)
  case DsetpPredPredRegAddressImmPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, pv, srcA, srcC, sc_offset, pp)
  case DsetpPredPredRegConstPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      pa: InstructionOperand.Predicate
  ) extends Form(dstPredicate, nPd, srcA, srcConst, pa)
  case DsetpPredPredRegImmPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate
  ) extends Form(dstPredicate, nPd, srcA, uImm, pa)
  case DsetpPredPredRegRegPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate
  ) extends Form(dstPredicate, nPd, srcA, srcB, pa)
  case DsetpPredRegAddressImm(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate
  ) extends Form(pu, srcA, srcC, sc_offset)
  case DsetpPredRegConst(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dstPredicate, srcA, srcConst)
  case DsetpPredRegImm(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate
  ) extends Form(dstPredicate, srcA, uImm)
  case DsetpPredRegReg(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dstPredicate, srcA, srcB)
  case ElectPredReg(pu: InstructionOperand.Predicate, dst: InstructionOperand.Register) extends Form(pu, dst)
  case ElectPredRegPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, dst, pp)
  case ElectPredRegReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register
  ) extends Form(pu, dst, srcA)
  case EndcollectiveNoOperands() extends Form()
  case EndcollectivePred(pp: InstructionOperand.Predicate) extends Form(pp)
  case ErrbarNoOperands() extends Form()
  case ExitNoOperands() extends Form()
  case ExitPred(pp: InstructionOperand.Predicate) extends Form(pp)
  case F2fRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcB, sb_offset)
  case F2fRegConst(dst: InstructionOperand.Register, sb: InstructionOperand.ConstantBank) extends Form(dst, sb)
  case F2fRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case F2fRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case F2fpRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcB, sb_offset)
  case F2fpRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcB, sb_offset, srcC)
  case F2fpRegConst(dst: InstructionOperand.Register, sb: InstructionOperand.ConstantBank) extends Form(dst, sb)
  case F2fpRegConstReg(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, sb, srcC)
  case F2fpRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case F2fpRegImmReg(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, sb, srcC)
  case F2fpRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case F2fpRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sb_offset)
  case F2fpRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, sb_offset, srcC)
  case F2fpRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, sb)
  case F2fpRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case F2fpRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case F2fpRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case F2fpRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case F2fpRegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, sc_offset)
  case F2fpRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcB, sc)
  case F2fpRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sc)
  case F2fpRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case F2iRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcB, sb_offset)
  case F2iRegConst(dst: InstructionOperand.Register, sb: InstructionOperand.ConstantBank) extends Form(dst, sb)
  case F2iRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case F2iRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case F2ipRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, sb_offset, srcC)
  case F2ipRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case F2ipRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case F2ipRegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, sc_offset)
  case F2ipRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcB, sc)
  case F2ipRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sc)
  case F2ipRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case FaddRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcC, sc_offset)
  case FaddRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, sc)
  case FaddRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sc)
  case FaddRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case Fadd2RegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sc)
  case Fadd2RegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcC)
  case Fadd32iRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sc)
  case FccoPredPredRegConstReg(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dstPredicate, nPd, srcA, srcConst, srcC)
  case FccoPredPredRegImmReg(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dstPredicate, nPd, srcA, uImm, srcC)
  case FccoPredPredRegRegConst(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dstPredicate, nPd, srcA, srcB, srcConst)
  case FccoPredPredRegRegReg(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dstPredicate, nPd, srcA, srcB, srcC)
  case FccoPredRegConstReg(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dstPredicate, srcA, srcConst, srcC)
  case FccoPredRegImmReg(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dstPredicate, srcA, uImm, srcC)
  case FccoPredRegRegConst(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dstPredicate, srcA, srcB, srcConst)
  case FccoPredRegRegReg(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dstPredicate, srcA, srcB, srcC)
  case FchkPredRegAddressImm(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(pu, srcA, srcB, sb_offset)
  case FchkPredRegConst(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dstPredicate, srcA, srcConst)
  case FchkPredRegImm(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate
  ) extends Form(dstPredicate, srcA, uImm)
  case FchkPredRegReg(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dstPredicate, srcA, srcB)
  case FcmpRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcConst, srcC)
  case FcmpRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, uImm, srcC)
  case FcmpRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcB, srcConst)
  case FcmpRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case FenceNoOperands() extends Form()
  case FfmaRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, sb_offset, srcC)
  case FfmaRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case FfmaRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case FfmaRegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, sc_offset)
  case FfmaRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcB, sc)
  case FfmaRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sc)
  case FfmaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case Ffma2RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case Ffma2RegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sc)
  case Ffma2RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case Ffma32iRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      fImm: InstructionOperand.Immediate
  ) extends Form(dst, srcA, fImm)
  case Ffma32iRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case FhaddRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcC)
  case FhfmaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case FloRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcB, sb_offset)
  case FloRegConst(dst: InstructionOperand.Register, sb: InstructionOperand.ConstantBank) extends Form(dst, sb)
  case FloRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case FloRegPredAddressImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcB, sb_offset)
  case FloRegPredConst(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      sb: InstructionOperand.ConstantBank
  ) extends Form(dst, pu, sb)
  case FloRegPredImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, pu, sb)
  case FloRegPredReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register
  ) extends Form(dst, pu, srcB)
  case FloRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case FmnmxRegPredRegAddressImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, srcB, sb_offset, pp)
  case FmnmxRegPredRegConstPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, sb, pp)
  case FmnmxRegPredRegImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, sb, pp)
  case FmnmxRegPredRegRegPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, srcB, pp)
  case FmnmxRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sb_offset, pp)
  case FmnmxRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, pp)
  case FmnmxRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, pp)
  case FmnmxRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, pa)
  case Fmnmx3RegRegImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, pp)
  case Fmnmx3RegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, pp)
  case FmulRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sb_offset)
  case FmulRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, sb)
  case FmulRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case FmulRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case Fmul2RegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case Fmul2RegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case Fmul32iRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case FootprintPredRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(pu, rd2, dst, srcA, srcB)
  case FootprintPredRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, bank5, tsPtrIdx)
  case FootprintPredRegRegRegRegImmImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8)
  case FootprintPredRegRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC)
  case FootprintPredRegRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC, tid8)
  case FootprintRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(rd2, dst, srcA, srcB)
  case FootprintRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, bank5, tsPtrIdx)
  case FootprintRegRegRegRegImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8)
  case FootprintRegRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(rd2, dst, srcA, srcB, srcC)
  case FootprintRegRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, srcC, tid8)
  case FrndRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcB, sb_offset)
  case FrndRegConst(dst: InstructionOperand.Register, sb: InstructionOperand.ConstantBank) extends Form(dst, sb)
  case FrndRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case FrndRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case FselRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sb_offset, pp)
  case FselRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, pp)
  case FselRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, pp)
  case FselRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, pp)
  case FsetRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sb_offset)
  case FsetRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sb_offset, pp)
  case FsetRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, sb)
  case FsetRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, pp)
  case FsetRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case FsetRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, pp)
  case FsetRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case FsetRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, pa)
  case FsetpPredPredRegAddressImmPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, pv, srcA, srcB, sb_offset, pp)
  case FsetpPredPredRegConstPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      pa: InstructionOperand.Predicate
  ) extends Form(dstPredicate, nPd, srcA, srcConst, pa)
  case FsetpPredPredRegImmPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate
  ) extends Form(dstPredicate, nPd, srcA, uImm, pa)
  case FsetpPredPredRegRegPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate
  ) extends Form(dstPredicate, nPd, srcA, srcB, pa)
  case FsetpPredRegAddressImm(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(pu, srcA, srcB, sb_offset)
  case FsetpPredRegConst(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dstPredicate, srcA, srcConst)
  case FsetpPredRegImm(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate
  ) extends Form(dstPredicate, srcA, uImm)
  case FsetpPredRegReg(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dstPredicate, srcA, srcB)
  case FswzRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case FswzaddRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case GatherRegRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mdidx: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, mdidx)
  case GatherRegRegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mdidx: InstructionOperand.Immediate,
      dstbyte: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, mdidx, dstbyte)
  case GatherRegRegRegRegImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      mdidx: InstructionOperand.Immediate,
      dstbyte: InstructionOperand.Immediate,
      srchalf: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, mdidx, dstbyte, srchalf)
  case GetcrsptrReg(dst: InstructionOperand.Register) extends Form(dst)
  case GetlmembaseReg(dst: InstructionOperand.Register) extends Form(dst)
  case Hadd2NoOperands() extends Form()
  case Hadd2Const(sb: InstructionOperand.ConstantBank) extends Form(sb)
  case Hadd2ImmImm(sb: InstructionOperand.Immediate, sb2: InstructionOperand.Immediate) extends Form(sb, sb2)
  case Hadd2RegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcC, sc_offset)
  case Hadd2RegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, sc)
  case Hadd2RegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sc)
  case Hadd2RegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb, sc)
  case Hadd2RegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcC)
  case Hadd232iImmImm(imm: InstructionOperand.Immediate, imm2: InstructionOperand.Immediate) extends Form(imm, imm2)
  case Hadd232iRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      sc1: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sc, sc1)
  case Hfma2NoOperands() extends Form()
  case Hfma2Const(sb: InstructionOperand.ConstantBank) extends Form(sb)
  case Hfma2ImmImm(sb: InstructionOperand.Immediate, sb2: InstructionOperand.Immediate) extends Form(sb, sb2)
  case Hfma2RegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, sb_offset, srcC)
  case Hfma2RegRegAddressImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sb_offset, srcC, pp)
  case Hfma2RegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case Hfma2RegRegConstRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, pp)
  case Hfma2RegRegImmImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      sb1: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, sb1, srcC)
  case Hfma2RegRegImmImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      sb1: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, sb1, srcC, pp)
  case Hfma2RegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, sc_offset)
  case Hfma2RegRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, sc_offset, pp)
  case Hfma2RegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcB, sc)
  case Hfma2RegRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sc, pp)
  case Hfma2RegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      sc1: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sc, sc1)
  case Hfma2RegRegRegImmImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      sc1: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sc, sc1, pp)
  case Hfma2RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case Hfma2RegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, pp)
  case Hfma232iRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      fImm: InstructionOperand.Immediate,
      fImm1: InstructionOperand.Immediate
  ) extends Form(dst, srcA, fImm, fImm1)
  case Hfma232iRegRegImmImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      sb1: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, sb1, srcC)
  case HgmmaRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcC)
  case HgmmaRegAddressRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcC, uPp)
  case HgmmaRegAddressRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcC, uPp, re, id)
  case HgmmaRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case HgmmaRegAddressRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcC, re, id)
  case HgmmaRegAddressRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, uPp)
  case HgmmaRegAddressRegRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, uPp, re, id)
  case HgmmaRegAddressRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, re, id)
  case HgmmaRegRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case HgmmaRegRegAddressRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, uPp)
  case HgmmaRegRegAddressRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, uPp, re, id)
  case HgmmaRegRegAddressRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, re, id)
  case HmmaAddressRegRegAddress(
      dst: InstructionOperand.Address,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address
  ) extends Form(dst, srcA, srcB, srcC)
  case HmmaAddressRegRegAddressPred(
      dst: InstructionOperand.Address,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, uPp)
  case HmmaAddressRegRegAddressPredRegImm(
      dst: InstructionOperand.Address,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, uPp, re, id)
  case HmmaAddressRegRegAddressRegImm(
      dst: InstructionOperand.Address,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, re, id)
  case HmmaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case HmmaRegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, uPp)
  case HmmaRegRegRegRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, uPp, re, id)
  case HmmaRegRegRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, re, id)
  case Hmnmx2RegPredPredRegAddressImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, pv, srcA, srcB, sb_offset, pp)
  case Hmnmx2RegPredPredRegConstPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, pv, srcA, sb, pp)
  case Hmnmx2RegPredPredRegImmImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      sb1: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, pv, srcA, sb, sb1, pp)
  case Hmnmx2RegPredPredRegRegPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, pv, srcA, srcB, pp)
  case Hmnmx2RegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sb_offset, pp)
  case Hmnmx2RegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, pp)
  case Hmnmx2RegRegImmImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      sb1: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, sb1, pp)
  case Hmnmx2RegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, pp)
  case Hmul2NoOperands() extends Form()
  case Hmul2Const(sb: InstructionOperand.ConstantBank) extends Form(sb)
  case Hmul2ImmImm(sb: InstructionOperand.Immediate, sb2: InstructionOperand.Immediate) extends Form(sb, sb2)
  case Hmul2RegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sb_offset)
  case Hmul2RegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, sb)
  case Hmul2RegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      sb1: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb, sb1)
  case Hmul2RegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case Hmul232iImmImm(imm: InstructionOperand.Immediate, imm2: InstructionOperand.Immediate) extends Form(imm, imm2)
  case Hmul232iRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      sb1: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb, sb1)
  case Hset2NoOperands() extends Form()
  case Hset2Const(sb: InstructionOperand.ConstantBank) extends Form(sb)
  case Hset2ConstPred(sb: InstructionOperand.ConstantBank, ps: InstructionOperand.Predicate) extends Form(sb, ps)
  case Hset2ImmImm(sb: InstructionOperand.Immediate, sb2: InstructionOperand.Immediate) extends Form(sb, sb2)
  case Hset2ImmImmPred(
      sb: InstructionOperand.Immediate,
      sb2: InstructionOperand.Immediate,
      ps: InstructionOperand.Predicate
  ) extends Form(sb, sb2, ps)
  case Hset2Pred(ps: InstructionOperand.Predicate) extends Form(ps)
  case Hset2RegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcC, sc_offset)
  case Hset2RegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcC, sc_offset, pp)
  case Hset2RegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, sc)
  case Hset2RegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sc, pp)
  case Hset2RegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      sc1: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sc, sc1)
  case Hset2RegRegImmImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      sc1: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sc, sc1, pp)
  case Hset2RegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcC)
  case Hset2RegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcC, pp)
  case Hsetp2PredPredRegAddressImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate
  ) extends Form(pu, pv, srcA, srcC, sc_offset)
  case Hsetp2PredPredRegAddressImmPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, pv, srcA, srcC, sc_offset, pp)
  case Hsetp2PredPredRegConst(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dstPredicate, nPd, srcA, srcConst)
  case Hsetp2PredPredRegConstPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      pa: InstructionOperand.Predicate
  ) extends Form(dstPredicate, nPd, srcA, srcConst, pa)
  case Hsetp2PredPredRegImmImm(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      uImmH0: InstructionOperand.Immediate
  ) extends Form(dstPredicate, nPd, srcA, uImm, uImmH0)
  case Hsetp2PredPredRegImmImmPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      uImmH0: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate
  ) extends Form(dstPredicate, nPd, srcA, uImm, uImmH0, pa)
  case Hsetp2PredPredRegReg(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dstPredicate, nPd, srcA, srcB)
  case Hsetp2PredPredRegRegPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate
  ) extends Form(dstPredicate, nPd, srcA, srcB, pa)
  case I2fRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcB, sb_offset)
  case I2fRegConst(dst: InstructionOperand.Register, sb: InstructionOperand.ConstantBank) extends Form(dst, sb)
  case I2fRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case I2fRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case I2fpRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcB, sb_offset)
  case I2fpRegConst(dst: InstructionOperand.Register, sb: InstructionOperand.ConstantBank) extends Form(dst, sb)
  case I2fpRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case I2fpRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case I2iRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcB, sb_offset)
  case I2iRegConst(dst: InstructionOperand.Register, sb: InstructionOperand.ConstantBank) extends Form(dst, sb)
  case I2iRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case I2iRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case I2ipRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, sb_offset, srcC)
  case I2ipRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case I2ipRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case I2ipRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case IabsRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcB, sb_offset)
  case IabsRegConst(dst: InstructionOperand.Register, sb: InstructionOperand.ConstantBank) extends Form(dst, sb)
  case IabsRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case IabsRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case IaddRegPredRegAddressImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, srcB, sb_offset)
  case IaddRegPredRegAddressImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, srcB, sb_offset, pp)
  case IaddRegPredRegConst(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank
  ) extends Form(dst, pu, srcA, sb)
  case IaddRegPredRegConstPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, sb, pp)
  case IaddRegPredRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, sb)
  case IaddRegPredRegImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, sb, pp)
  case IaddRegPredRegImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, srcA, sb, srcC)
  case IaddRegPredRegImmRegPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, sb, srcC, pp)
  case IaddRegPredRegReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, pu, srcA, srcB)
  case IaddRegPredRegRegPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, srcB, pp)
  case IaddRegPredRegRegReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, srcA, srcB, srcC)
  case IaddRegPredRegRegRegPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, srcB, srcC, pp)
  case IaddRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sb_offset)
  case IaddRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sb_offset, pp)
  case IaddRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, sb)
  case IaddRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, pp)
  case IaddRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case IaddRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, pp)
  case IaddRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case IaddRegRegImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, pp)
  case IaddRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case IaddRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, pp)
  case IaddRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case IaddRegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, pp)
  case Iadd3RegPredPredRegAddressImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, pv, srcA, srcB, sb_offset, srcC)
  case Iadd3RegPredPredRegAddressImmRegPredPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate
  ) extends Form(dst, pu, pv, srcA, srcB, sb_offset, srcC, pp, pq)
  case Iadd3RegPredPredRegConstReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, pv, srcA, sb, srcC)
  case Iadd3RegPredPredRegConstRegPredPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate
  ) extends Form(dst, pu, pv, srcA, sb, srcC, pp, pq)
  case Iadd3RegPredPredRegImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, pv, srcA, sb, srcC)
  case Iadd3RegPredPredRegImmRegPredPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate
  ) extends Form(dst, pu, pv, srcA, sb, srcC, pp, pq)
  case Iadd3RegPredPredRegRegReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, pv, srcA, srcB, srcC)
  case Iadd3RegPredPredRegRegRegPredPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate
  ) extends Form(dst, pu, pv, srcA, srcB, srcC, pp, pq)
  case Iadd3RegPredRegAddressImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, srcA, srcB, sb_offset, srcC)
  case Iadd3RegPredRegAddressImmRegPredPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, srcB, sb_offset, srcC, pp, pq)
  case Iadd3RegPredRegConstReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, srcA, sb, srcC)
  case Iadd3RegPredRegConstRegPredPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, sb, srcC, pp, pq)
  case Iadd3RegPredRegImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, srcA, sb, srcC)
  case Iadd3RegPredRegImmRegPredPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, sb, srcC, pp, pq)
  case Iadd3RegPredRegRegReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, srcA, srcB, srcC)
  case Iadd3RegPredRegRegRegPredPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, srcB, srcC, pp, pq)
  case Iadd3RegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, sb_offset, srcC)
  case Iadd3RegRegAddressImmRegPredPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sb_offset, srcC, pp, pq)
  case Iadd3RegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case Iadd3RegRegConstRegPredPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, pp, pq)
  case Iadd3RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case Iadd3RegRegImmRegPredPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, pp, pq)
  case Iadd3RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case Iadd3RegRegRegRegPredPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, pp, pq)
  case Iadd32iRegPredRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, sb)
  case Iadd32iRegPredRegImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, sb, pp)
  case Iadd32iRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case Iadd32iRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, pp)
  case IcmpRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcConst, srcC)
  case IcmpRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, uImm, srcC)
  case IcmpRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcB, srcConst)
  case IcmpRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case IdeNoOperands() extends Form()
  case IdeImm(sb: InstructionOperand.Immediate) extends Form(sb)
  case IdpRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, sb_offset, srcC)
  case IdpRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case IdpRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case Idp4aRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, sb_offset, srcC)
  case Idp4aRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case Idp4aRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case IgmmaRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcC)
  case IgmmaRegAddressRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcC, uPp)
  case IgmmaRegAddressRegPredReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register
  ) extends Form(dst, srcA, srcC, uPp, re)
  case IgmmaRegAddressRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcC, uPp, re, id)
  case IgmmaRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case IgmmaRegAddressRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcC, re, id)
  case IgmmaRegAddressRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, uPp)
  case IgmmaRegAddressRegRegPredReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC, uPp, re)
  case IgmmaRegAddressRegRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, uPp, re, id)
  case IgmmaRegAddressRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC, re)
  case IgmmaRegAddressRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, re, id)
  case IgmmaRegRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case IgmmaRegRegAddressRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, uPp)
  case IgmmaRegRegAddressRegPredReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC, uPp, re)
  case IgmmaRegRegAddressRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, uPp, re, id)
  case IgmmaRegRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC, re)
  case IgmmaRegRegAddressRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, re, id)
  case ImadRegPredRegAddressImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, srcA, srcB, sb_offset, srcC)
  case ImadRegPredRegAddressImmRegPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, srcB, sb_offset, srcC, pp)
  case ImadRegPredRegConstReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, srcA, sb, srcC)
  case ImadRegPredRegConstRegPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, sb, srcC, pp)
  case ImadRegPredRegImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, srcA, sb, srcC)
  case ImadRegPredRegImmRegPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, sb, srcC, pp)
  case ImadRegPredRegRegAddressImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, srcB, srcC, sc_offset)
  case ImadRegPredRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, srcB, srcC, sc_offset, pp)
  case ImadRegPredRegRegConst(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank
  ) extends Form(dst, pu, srcA, srcB, sc)
  case ImadRegPredRegRegConstPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, srcB, sc, pp)
  case ImadRegPredRegRegReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, srcA, srcB, srcC)
  case ImadRegPredRegRegRegPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, srcB, srcC, pp)
  case ImadRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, sb_offset, srcC)
  case ImadRegRegAddressImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sb_offset, srcC, pp)
  case ImadRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case ImadRegRegConstRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, pp)
  case ImadRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case ImadRegRegImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, pp)
  case ImadRegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, sc_offset)
  case ImadRegRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, sc_offset, pp)
  case ImadRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcB, sc)
  case ImadRegRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sc, pp)
  case ImadRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sc)
  case ImadRegRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sc, pp)
  case ImadRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case ImadRegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, pp)
  case Imad32iRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sImm, srcC)
  case ImadspRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcConst, srcC)
  case ImadspRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, uImm, srcC)
  case ImadspRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcB, srcConst)
  case ImadspRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case ImmaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case ImmaRegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, uPp)
  case ImmaRegRegRegRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, uPp, re, id)
  case ImmaRegRegRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, re, id)
  case ImnmxPredPredRegRegImmPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, pv, dst, srcA, sb, pp)
  case ImnmxPredPredRegRegImmPredPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate
  ) extends Form(pu, pv, dst, srcA, sb, pp, pq)
  case ImnmxPredPredRegRegRegPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, pv, dst, srcA, srcB, pp)
  case ImnmxPredPredRegRegRegPredPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate
  ) extends Form(pu, pv, dst, srcA, srcB, pp, pq)
  case ImnmxPredRegRegImmPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, dst, srcA, sb, pp)
  case ImnmxPredRegRegImmPredPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate
  ) extends Form(pu, dst, srcA, sb, pp, pq)
  case ImnmxPredRegRegRegPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, dst, srcA, srcB, pp)
  case ImnmxPredRegRegRegPredPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate
  ) extends Form(pu, dst, srcA, srcB, pp, pq)
  case ImnmxRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sb_offset, pp)
  case ImnmxRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, pp)
  case ImnmxRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, pp)
  case ImnmxRegRegImmPredPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, pp, pq)
  case ImnmxRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, pa)
  case ImnmxRegRegRegPredPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, pp, pq)
  case ImulRegPredRegAddressImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, srcB, sb_offset)
  case ImulRegPredRegConst(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank
  ) extends Form(dst, pu, srcA, sb)
  case ImulRegPredRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, sb)
  case ImulRegPredRegReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, pu, srcA, srcB)
  case ImulRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sb_offset)
  case ImulRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, sb)
  case ImulRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case ImulRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case ImulRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case ImulRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sc)
  case ImulRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case Imul32iRegPredRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, sb)
  case Imul32iRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case IpaRegAddress(dst: InstructionOperand.Register, srcA: InstructionOperand.Address) extends Form(dst, srcA)
  case IpaRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case IpaRegAddressPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      ps: InstructionOperand.Predicate
  ) extends Form(dst, srcA, ps)
  case IpaRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case IpaRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case IpaRegAddressRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      ps: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, ps)
  case IpaRegImm(dst: InstructionOperand.Register, attr: InstructionOperand.Immediate) extends Form(dst, attr)
  case IpaRegImmConst(
      dst: InstructionOperand.Register,
      attr: InstructionOperand.Immediate,
      sb: InstructionOperand.ConstantBank
  ) extends Form(dst, attr, sb)
  case IpaRegImmImm(
      dst: InstructionOperand.Register,
      attr: InstructionOperand.Immediate,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, attr, sb)
  case IpaRegImmPred(
      dst: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      ps: InstructionOperand.Predicate
  ) extends Form(dst, uImm, ps)
  case IpaRegImmReg(
      dst: InstructionOperand.Register,
      attr: InstructionOperand.Immediate,
      srcB: InstructionOperand.Register
  ) extends Form(dst, attr, srcB)
  case IpaRegImmRegReg(
      dst: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, uImm, srcB, srcC)
  case IpaRegImmRegRegPred(
      dst: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      ps: InstructionOperand.Predicate
  ) extends Form(dst, uImm, srcB, srcC, ps)
  case IpaRegPredAddress(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Address
  ) extends Form(dst, pu, srcA)
  case IpaRegPredAddressImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Address,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, sb)
  case IpaRegPredAddressReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register
  ) extends Form(dst, pu, srcA, srcB)
  case IpaRegPredImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      attr: InstructionOperand.Immediate
  ) extends Form(dst, pu, attr)
  case IpaRegPredImmConst(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      attr: InstructionOperand.Immediate,
      sb: InstructionOperand.ConstantBank
  ) extends Form(dst, pu, attr, sb)
  case IpaRegPredImmImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      attr: InstructionOperand.Immediate,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, pu, attr, sb)
  case IpaRegPredImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      attr: InstructionOperand.Immediate,
      srcB: InstructionOperand.Register
  ) extends Form(dst, pu, attr, srcB)
  case IsadRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcConst, srcC)
  case IsadRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, uImm, srcC)
  case IsadRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcB, srcConst)
  case IsadRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case IsberdRegAddress(dst: InstructionOperand.Register, srcA: InstructionOperand.Address) extends Form(dst, srcA)
  case IsbewrAddressReg(srcA: InstructionOperand.Address, srcB: InstructionOperand.Register) extends Form(srcA, srcB)
  case IscaddRegPredRegAddressImmImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, srcB, sb_offset, scaleU5)
  case IscaddRegPredRegConstImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, sb, scaleU5)
  case IscaddRegPredRegImmImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, sb, scaleU5)
  case IscaddRegPredRegRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, srcB, scaleU5)
  case IscaddRegRegAddressImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sb_offset, scaleU5)
  case IscaddRegRegConstImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb, scaleU5)
  case IscaddRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb, scaleU5)
  case IscaddRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, scaleU5)
  case Iscadd32iRegPredRegImmImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, sb, scaleU5)
  case Iscadd32iRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb, scaleU5)
  case IsetRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcConst)
  case IsetRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      pa: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcConst, pa)
  case IsetRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate
  ) extends Form(dst, srcA, uImm)
  case IsetRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate
  ) extends Form(dst, srcA, uImm, pa)
  case IsetRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case IsetRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, pa)
  case IsetpPredPredRegAddressImmPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, pv, srcA, srcB, sb_offset, pp)
  case IsetpPredPredRegAddressImmPredPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      pr: InstructionOperand.Predicate
  ) extends Form(pu, pv, srcA, srcB, sb_offset, pp, pr)
  case IsetpPredPredRegConstPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      pa: InstructionOperand.Predicate
  ) extends Form(dstPredicate, nPd, srcA, srcConst, pa)
  case IsetpPredPredRegConstPredPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate,
      pr: InstructionOperand.Predicate
  ) extends Form(pu, pv, srcA, sb, pp, pr)
  case IsetpPredPredRegImmPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate
  ) extends Form(dstPredicate, nPd, srcA, uImm, pa)
  case IsetpPredPredRegImmPredPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate,
      pr: InstructionOperand.Predicate
  ) extends Form(pu, pv, srcA, sb, pp, pr)
  case IsetpPredPredRegRegPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate
  ) extends Form(dstPredicate, nPd, srcA, srcB, pa)
  case IsetpPredPredRegRegPredPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate,
      pr: InstructionOperand.Predicate
  ) extends Form(pu, pv, srcA, srcB, pp, pr)
  case IsetpPredRegAddressImm(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(pu, srcA, srcB, sb_offset)
  case IsetpPredRegAddressImmPred(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pr: InstructionOperand.Predicate
  ) extends Form(pu, srcA, srcB, sb_offset, pr)
  case IsetpPredRegConst(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dstPredicate, srcA, srcConst)
  case IsetpPredRegConstPred(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pr: InstructionOperand.Predicate
  ) extends Form(pu, srcA, sb, pr)
  case IsetpPredRegImm(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate
  ) extends Form(dstPredicate, srcA, uImm)
  case IsetpPredRegImmPred(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pr: InstructionOperand.Predicate
  ) extends Form(pu, srcA, sb, pr)
  case IsetpPredRegReg(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dstPredicate, srcA, srcB)
  case IsetpPredRegRegPred(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pr: InstructionOperand.Predicate
  ) extends Form(pu, srcA, srcB, pr)
  case IsubRegConstReg(
      dst: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcA: InstructionOperand.Register
  ) extends Form(dst, srcConst, srcA)
  case IsubRegImmReg(
      dst: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcA: InstructionOperand.Register
  ) extends Form(dst, uImm, srcA)
  case IsubRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcConst)
  case IsubRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate
  ) extends Form(dst, srcA, uImm)
  case IsubRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case JcalConst(srcConst: InstructionOperand.ConstantBank) extends Form(srcConst)
  case JcalImm(sImm: InstructionOperand.Immediate) extends Form(sImm)
  case JmpConst(sa: InstructionOperand.ConstantBank) extends Form(sa)
  case JmpImm(sa: InstructionOperand.Immediate) extends Form(sa)
  case JmpPredConst(pp: InstructionOperand.Predicate, sa: InstructionOperand.ConstantBank) extends Form(pp, sa)
  case JmpPredImm(pp: InstructionOperand.Predicate, sa: InstructionOperand.Immediate) extends Form(pp, sa)
  case JmpPredPredConst(
      pp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      sa: InstructionOperand.ConstantBank
  ) extends Form(pp, uPq, sa)
  case JmpPredPredImm(
      pp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      sa: InstructionOperand.Immediate
  ) extends Form(pp, uPq, sa)
  case JmpPredRegConst(
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      sa: InstructionOperand.ConstantBank
  ) extends Form(pp, srcB, sa)
  case JmpPredRegImm(
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      sa: InstructionOperand.Immediate
  ) extends Form(pp, srcB, sa)
  case JmpRegConst(srcB: InstructionOperand.Register, sa: InstructionOperand.ConstantBank) extends Form(srcB, sa)
  case JmpRegImm(srcB: InstructionOperand.Register, sa: InstructionOperand.Immediate) extends Form(srcB, sa)
  case JmpSymbolConst(testCC: InstructionOperand.Symbol, srcConst: InstructionOperand.ConstantBank)
      extends Form(testCC, srcConst)
  case JmpSymbolImm(testCC: InstructionOperand.Symbol, sImm: InstructionOperand.Immediate) extends Form(testCC, sImm)
  case JmpSymbolImmImm(
      srcConst: InstructionOperand.Symbol,
      constBank: InstructionOperand.Immediate,
      immConstOffset: InstructionOperand.Immediate
  ) extends Form(srcConst, constBank, immConstOffset)
  case JmpSymbolSymbolImmImm(
      testCC: InstructionOperand.Symbol,
      srcConst: InstructionOperand.Symbol,
      constBank: InstructionOperand.Immediate,
      immConstOffset: InstructionOperand.Immediate
  ) extends Form(testCC, srcConst, constBank, immConstOffset)
  case JmxConst(srcConst: InstructionOperand.ConstantBank) extends Form(srcConst)
  case JmxPredReg(pp: InstructionOperand.Predicate, srcA: InstructionOperand.Register) extends Form(pp, srcA)
  case JmxPredRegImm(
      pp: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      ra_offset: InstructionOperand.Immediate
  ) extends Form(pp, srcA, ra_offset)
  case JmxReg(srcA: InstructionOperand.Register) extends Form(srcA)
  case JmxRegImm(srcA: InstructionOperand.Register, ra_offset: InstructionOperand.Immediate)
      extends Form(srcA, ra_offset)
  case JmxSymbolConst(testCC: InstructionOperand.Symbol, srcConst: InstructionOperand.ConstantBank)
      extends Form(testCC, srcConst)
  case JmxSymbolImmAddress(
      srcConst: InstructionOperand.Symbol,
      constBank: InstructionOperand.Immediate,
      srcA: InstructionOperand.Address
  ) extends Form(srcConst, constBank, srcA)
  case JmxSymbolReg(testCC: InstructionOperand.Symbol, srcA: InstructionOperand.Register) extends Form(testCC, srcA)
  case JmxSymbolRegImm(
      testCC: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate
  ) extends Form(testCC, srcA, sImm)
  case JmxSymbolSymbolImmAddress(
      testCC: InstructionOperand.Symbol,
      srcConst: InstructionOperand.Symbol,
      constBank: InstructionOperand.Immediate,
      srcA: InstructionOperand.Address
  ) extends Form(testCC, srcConst, constBank, srcA)
  case JmxuPredReg(pp: InstructionOperand.Predicate, srcA: InstructionOperand.Register) extends Form(pp, srcA)
  case JmxuPredRegImm(
      pp: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      uR_offset: InstructionOperand.Immediate
  ) extends Form(pp, srcA, uR_offset)
  case JmxuReg(srcA: InstructionOperand.Register) extends Form(srcA)
  case JmxuRegImm(srcA: InstructionOperand.Register, uR_offset: InstructionOperand.Immediate)
      extends Form(srcA, uR_offset)
  case KilNoOperands() extends Form()
  case KillNoOperands() extends Form()
  case KillPred(pp: InstructionOperand.Predicate) extends Form(pp)
  case LdPredRegAddress(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address
  ) extends Form(pu, dst, srcA)
  case LdPredRegAddressPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      pnz: InstructionOperand.Predicate
  ) extends Form(pu, dst, srcA, pnz)
  case LdRegAddress(dst: InstructionOperand.Register, srcA: InstructionOperand.Address) extends Form(dst, srcA)
  case LdRegAddressPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      pnz: InstructionOperand.Predicate
  ) extends Form(dst, srcA, pnz)
  case LdcRegAddressAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address
  ) extends Form(dst, srcA, srcB)
  case LdcRegConst(dst: InstructionOperand.Register, sa: InstructionOperand.ConstantBank) extends Form(dst, sa)
  case LdcuRegAddress(dst: InstructionOperand.Register, srcA: InstructionOperand.Address) extends Form(dst, srcA)
  case LdcuRegAddressAddress(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address
  ) extends Form(dst, srcA, srcB)
  case LdcuRegAddressPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, uPp)
  case LdcuRegConst(dst: InstructionOperand.Register, sa: InstructionOperand.ConstantBank) extends Form(dst, sa)
  case LdcuRegRegAddress(
      uRd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address
  ) extends Form(uRd2, dst, srcA)
  case LdcuRegRegAddressAddress(
      uRd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address
  ) extends Form(uRd2, dst, srcA, srcB)
  case LdcuRegRegAddressAddressImm(
      uRd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      word_mask: InstructionOperand.Immediate
  ) extends Form(uRd2, dst, srcA, srcB, word_mask)
  case LdcuRegRegAddressImm(
      uRd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      word_mask: InstructionOperand.Immediate
  ) extends Form(uRd2, dst, srcA, word_mask)
  case LdcuRegRegAddressImmPred(
      uRd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      word_mask: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(uRd2, dst, srcA, word_mask, uPp)
  case LdcuRegRegAddressPred(
      uRd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      uPp: InstructionOperand.Predicate
  ) extends Form(uRd2, dst, srcA, uPp)
  case LdcuRegRegConst(
      uRd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      sa: InstructionOperand.ConstantBank
  ) extends Form(uRd2, dst, sa)
  case LdcuRegRegConstImm(
      uRd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      sa: InstructionOperand.ConstantBank,
      word_mask: InstructionOperand.Immediate
  ) extends Form(uRd2, dst, sa, word_mask)
  case LdgPredRegAddress(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address
  ) extends Form(dstPredicate, dst, srcA)
  case LdgPredRegAddressPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      pnz: InstructionOperand.Predicate
  ) extends Form(pu, dst, srcA, pnz)
  case LdgRegAddress(dst: InstructionOperand.Register, srcA: InstructionOperand.Address) extends Form(dst, srcA)
  case LdgRegAddressPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      pnz: InstructionOperand.Predicate
  ) extends Form(dst, srcA, pnz)
  case LdgRegRegAddress(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address
  ) extends Form(rd2, dst, srcA)
  case LdgRegRegAddressImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      word_mask: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, word_mask)
  case LdgRegRegAddressImmPred(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      word_mask: InstructionOperand.Immediate,
      pnz: InstructionOperand.Predicate
  ) extends Form(rd2, dst, srcA, word_mask, pnz)
  case LdgRegRegAddressPred(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      pnz: InstructionOperand.Predicate
  ) extends Form(rd2, dst, srcA, pnz)
  case LdgdepbarNoOperands() extends Form()
  case LdgmcRegAddress(dst: InstructionOperand.Register, srcA: InstructionOperand.Address) extends Form(dst, srcA)
  case LdgstsAddressAddress(srcB: InstructionOperand.Address, srcA: InstructionOperand.Address) extends Form(srcB, srcA)
  case LdgstsAddressAddressPred(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      pnz: InstructionOperand.Predicate
  ) extends Form(srcB, srcA, pnz)
  case LdlRegAddress(dst: InstructionOperand.Register, srcA: InstructionOperand.Address) extends Form(dst, srcA)
  case LdsPredRegAddress(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address
  ) extends Form(pu, dst, srcA)
  case LdsRegAddress(dst: InstructionOperand.Register, srcA: InstructionOperand.Address) extends Form(dst, srcA)
  case LdslkPredRegAddress(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address
  ) extends Form(dstPredicate, dst, srcA)
  case LdsmRegAddress(dst: InstructionOperand.Register, srcA: InstructionOperand.Address) extends Form(dst, srcA)
  case LdtRegAddress(dst: InstructionOperand.Register, srcB: InstructionOperand.Address) extends Form(dst, srcB)
  case LdtmRegAddress(dst: InstructionOperand.Register, srcB: InstructionOperand.Address) extends Form(dst, srcB)
  case LdtmRegRegAddress(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address
  ) extends Form(rd2, dst, srcB)
  case LdtramRegAddress(dst: InstructionOperand.Register, srcA: InstructionOperand.Address) extends Form(dst, srcA)
  case LeaPredRegRegConst(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dstPredicate, dst, srcA, srcConst)
  case LeaPredRegRegConstImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      uImm5: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, srcConst, uImm5)
  case LeaPredRegRegConstReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dstPredicate, dst, srcA, srcConst, srcC)
  case LeaPredRegRegConstRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      uImm5: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, srcConst, srcC, uImm5)
  case LeaPredRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm20: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, uImm20)
  case LeaPredRegRegImmImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm20: InstructionOperand.Immediate,
      uImm5: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, uImm20, uImm5)
  case LeaPredRegRegReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dstPredicate, dst, srcA, srcB)
  case LeaPredRegRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uImm5: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, srcB, uImm5)
  case LeaPredRegRegRegReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dstPredicate, dst, srcA, srcB, srcC)
  case LeaPredRegRegRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uImm5: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, srcB, srcC, uImm5)
  case LeaRegPredRegAddressImmImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, srcB, sb_offset, scaleU5)
  case LeaRegPredRegAddressImmImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, srcB, sb_offset, scaleU5, pp)
  case LeaRegPredRegAddressImmRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, srcB, sb_offset, srcC, scaleU5)
  case LeaRegPredRegAddressImmRegImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, srcB, sb_offset, srcC, scaleU5, pp)
  case LeaRegPredRegConstImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, sb, scaleU5)
  case LeaRegPredRegConstImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, sb, scaleU5, pp)
  case LeaRegPredRegConstRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, sb, srcC, scaleU5)
  case LeaRegPredRegConstRegImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, sb, srcC, scaleU5, pp)
  case LeaRegPredRegImmImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, sb, scaleU5)
  case LeaRegPredRegImmImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, sb, scaleU5, pp)
  case LeaRegPredRegImmRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, sb, srcC, scaleU5)
  case LeaRegPredRegImmRegImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, sb, srcC, scaleU5, pp)
  case LeaRegPredRegRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, srcB, scaleU5)
  case LeaRegPredRegRegImmImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, srcB, sc, scaleU5)
  case LeaRegPredRegRegImmImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, srcB, sc, scaleU5, pp)
  case LeaRegPredRegRegImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, srcB, scaleU5, pp)
  case LeaRegPredRegRegRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, srcB, srcC, scaleU5)
  case LeaRegPredRegRegRegImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, srcA, srcB, srcC, scaleU5, pp)
  case LeaRegRegAddressImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sb_offset, scaleU5)
  case LeaRegRegAddressImmImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sb_offset, scaleU5, pp)
  case LeaRegRegAddressImmRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sb_offset, srcC, scaleU5)
  case LeaRegRegAddressImmRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sb_offset, srcC, scaleU5, pp)
  case LeaRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcConst)
  case LeaRegRegConstImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb, scaleU5)
  case LeaRegRegConstImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, scaleU5, pp)
  case LeaRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcConst, srcC)
  case LeaRegRegConstRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb, srcC, scaleU5)
  case LeaRegRegConstRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, scaleU5, pp)
  case LeaRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm20: InstructionOperand.Immediate
  ) extends Form(dst, srcA, uImm20)
  case LeaRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb, scaleU5)
  case LeaRegRegImmImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, scaleU5, pp)
  case LeaRegRegImmRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb, srcC, scaleU5)
  case LeaRegRegImmRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, scaleU5, pp)
  case LeaRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case LeaRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, scaleU5)
  case LeaRegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sc, scaleU5)
  case LeaRegRegRegImmImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sc, scaleU5, pp)
  case LeaRegRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, scaleU5, pp)
  case LeaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case LeaRegRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, scaleU5)
  case LeaRegRegRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, scaleU5, pp)
  case LepcReg(dst: InstructionOperand.Register) extends Form(dst)
  case LepcRegImm(dst: InstructionOperand.Register, sImm58: InstructionOperand.Immediate) extends Form(dst, sImm58)
  case LongjmpNoOperands() extends Form()
  case LopPredRegRegAddressImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(pu, dst, srcA, srcB, sb_offset)
  case LopPredRegRegAddressImmPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, dst, srcA, srcB, sb_offset, pp)
  case LopPredRegRegConst(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dstPredicate, dst, srcA, srcConst)
  case LopPredRegRegConstPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, dst, srcA, sb, pp)
  case LopPredRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, uImm)
  case LopPredRegRegImmPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, dst, srcA, sb, pp)
  case LopPredRegRegReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dstPredicate, dst, srcA, srcB)
  case LopPredRegRegRegPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, dst, srcA, srcB, pp)
  case LopRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sb_offset)
  case LopRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sb_offset, pp)
  case LopRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, sb)
  case LopRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, pp)
  case LopRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case LopRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, pp)
  case LopRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case LopRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, pp)
  case Lop3PredRegRegAddressImmReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(pu, dst, srcA, srcB, sb_offset, srcC)
  case Lop3PredRegRegAddressImmRegImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate
  ) extends Form(pu, dst, srcA, srcB, sb_offset, srcC, imm8)
  case Lop3PredRegRegAddressImmRegImmPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, dst, srcA, srcB, sb_offset, srcC, imm8, pp)
  case Lop3PredRegRegAddressImmRegPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, dst, srcA, srcB, sb_offset, srcC, pp)
  case Lop3PredRegRegConstReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(pu, dst, srcA, sb, srcC)
  case Lop3PredRegRegConstRegImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate
  ) extends Form(pu, dst, srcA, sb, srcC, imm8)
  case Lop3PredRegRegConstRegImmPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, dst, srcA, sb, srcC, imm8, pp)
  case Lop3PredRegRegConstRegPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, dst, srcA, sb, srcC, pp)
  case Lop3PredRegRegImmReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(pu, dst, srcA, sb, srcC)
  case Lop3PredRegRegImmRegImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate
  ) extends Form(pu, dst, srcA, sb, srcC, imm8)
  case Lop3PredRegRegImmRegImmPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, dst, srcA, sb, srcC, imm8, pp)
  case Lop3PredRegRegImmRegPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, dst, srcA, sb, srcC, pp)
  case Lop3PredRegRegRegReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dstPredicate, dst, srcA, srcB, srcC)
  case Lop3PredRegRegRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, srcB, srcC, imm8)
  case Lop3PredRegRegRegRegImmPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, dst, srcA, srcB, srcC, imm8, pp)
  case Lop3PredRegRegRegRegPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, dst, srcA, srcB, srcC, pp)
  case Lop3RegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, sb_offset, srcC)
  case Lop3RegRegAddressImmRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sb_offset, srcC, imm8)
  case Lop3RegRegAddressImmRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sb_offset, srcC, imm8, pp)
  case Lop3RegRegAddressImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sb_offset, srcC, pp)
  case Lop3RegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case Lop3RegRegConstRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb, srcC, imm8)
  case Lop3RegRegConstRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, imm8, pp)
  case Lop3RegRegConstRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, pp)
  case Lop3RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case Lop3RegRegImmRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb, srcC, imm8)
  case Lop3RegRegImmRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, imm8, pp)
  case Lop3RegRegImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, pp)
  case Lop3RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case Lop3RegRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, imm8)
  case Lop3RegRegRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, imm8, pp)
  case Lop3RegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, pp)
  case Lop32iPredRegRegImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(pu, dst, srcA, sb)
  case Lop32iPredRegRegImmPred(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, dst, srcA, sb, pp)
  case Lop32iRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case Lop32iRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, pp)
  case MatchPredRegReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register
  ) extends Form(pu, dst, srcA)
  case MatchRegReg(dst: InstructionOperand.Register, srcA: InstructionOperand.Register) extends Form(dst, srcA)
  case MembarNoOperands() extends Form()
  case MovAddressConst(dst: InstructionOperand.Address, sb: InstructionOperand.ConstantBank) extends Form(dst, sb)
  case MovAddressConstImm(
      dst: InstructionOperand.Address,
      sb: InstructionOperand.ConstantBank,
      pixMaskU04: InstructionOperand.Immediate
  ) extends Form(dst, sb, pixMaskU04)
  case MovAddressImm(dst: InstructionOperand.Address, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case MovAddressImmImm(
      dst: InstructionOperand.Address,
      sb: InstructionOperand.Immediate,
      pixMaskU04: InstructionOperand.Immediate
  ) extends Form(dst, sb, pixMaskU04)
  case MovAddressReg(dst: InstructionOperand.Address, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case MovAddressRegImm(
      dst: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      pixMaskU04: InstructionOperand.Immediate
  ) extends Form(dst, srcB, pixMaskU04)
  case MovRegAddress(dst: InstructionOperand.Register, srcB: InstructionOperand.Address) extends Form(dst, srcB)
  case MovRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      pixMaskU04: InstructionOperand.Immediate
  ) extends Form(dst, srcB, pixMaskU04)
  case MovRegAddressImmImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pixMaskU04: InstructionOperand.Immediate
  ) extends Form(dst, srcB, sb_offset, pixMaskU04)
  case MovRegConst(dst: InstructionOperand.Register, sb: InstructionOperand.ConstantBank) extends Form(dst, sb)
  case MovRegConstImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pixMaskU04: InstructionOperand.Immediate
  ) extends Form(dst, sb, pixMaskU04)
  case MovRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case MovRegImmImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pixMaskU04: InstructionOperand.Immediate
  ) extends Form(dst, sb, pixMaskU04)
  case MovRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case MovRegRegImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pixMaskU04: InstructionOperand.Immediate
  ) extends Form(dst, srcB, pixMaskU04)
  case Mov32iRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case Mov32iRegImmImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pixMaskU04: InstructionOperand.Immediate
  ) extends Form(dst, sb, pixMaskU04)
  case Mov64iurRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case Mov64iurRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case MovmRegReg(dst: InstructionOperand.Register, srcA: InstructionOperand.Register) extends Form(dst, srcA)
  case MufuRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcB, sb_offset)
  case MufuRegConst(dst: InstructionOperand.Register, sb: InstructionOperand.ConstantBank) extends Form(dst, sb)
  case MufuRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case MufuRegImmImm(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      sb1: InstructionOperand.Immediate
  ) extends Form(dst, sb, sb1)
  case MufuRegReg(dst: InstructionOperand.Register, srcA: InstructionOperand.Register) extends Form(dst, srcA)
  case NanosleepNoOperands() extends Form()
  case NanosleepAddressImm(srcB: InstructionOperand.Address, sb_offset: InstructionOperand.Immediate)
      extends Form(srcB, sb_offset)
  case NanosleepConst(sb: InstructionOperand.ConstantBank) extends Form(sb)
  case NanosleepImm(sb: InstructionOperand.Immediate) extends Form(sb)
  case NanosleepPred(pp: InstructionOperand.Predicate) extends Form(pp)
  case NanosleepPredAddressImm(
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(pp, srcB, sb_offset)
  case NanosleepPredConst(pp: InstructionOperand.Predicate, sb: InstructionOperand.ConstantBank) extends Form(pp, sb)
  case NanosleepPredImm(pp: InstructionOperand.Predicate, sb: InstructionOperand.Immediate) extends Form(pp, sb)
  case NanosleepPredReg(pp: InstructionOperand.Predicate, srcB: InstructionOperand.Register) extends Form(pp, srcB)
  case NanosleepReg(srcB: InstructionOperand.Register) extends Form(srcB)
  case NanotrapConst(sb: InstructionOperand.ConstantBank) extends Form(sb)
  case NanotrapImm(sb: InstructionOperand.Immediate) extends Form(sb)
  case NanotrapPredConst(pp: InstructionOperand.Predicate, sb: InstructionOperand.ConstantBank) extends Form(pp, sb)
  case NanotrapPredImm(pp: InstructionOperand.Predicate, sb: InstructionOperand.Immediate) extends Form(pp, sb)
  case NanotrapPredReg(pp: InstructionOperand.Predicate, srcB: InstructionOperand.Register) extends Form(pp, srcB)
  case NanotrapReg(srcB: InstructionOperand.Register) extends Form(srcB)
  case NopNoOperands() extends Form()
  case NopImm(uImm: InstructionOperand.Immediate) extends Form(uImm)
  case NopSymbol(testCC: InstructionOperand.Symbol) extends Form(testCC)
  case NopSymbolImm(testCC: InstructionOperand.Symbol, uImm: InstructionOperand.Immediate) extends Form(testCC, uImm)
  case OmmaRegRegRegRegPredRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      rh: InstructionOperand.Register,
      uRi: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC, uPp, re, rh, uRi)
  case OmmaRegRegRegRegPredRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      rh: InstructionOperand.Register,
      uRi: InstructionOperand.Register,
      sparseId: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, uPp, re, rh, uRi, sparseId)
  case OmmaRegRegRegRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      rh: InstructionOperand.Register,
      uRi: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC, re, rh, uRi)
  case OmmaRegRegRegRegRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      rh: InstructionOperand.Register,
      uRi: InstructionOperand.Register,
      sparseId: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, re, rh, uRi, sparseId)
  case OutReg(srcA: InstructionOperand.Register) extends Form(srcA)
  case OutRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcConst)
  case OutRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case OutRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case P2rReg(dst: InstructionOperand.Register) extends Form(dst)
  case P2rRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcConst)
  case P2rRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate
  ) extends Form(dst, srcA, uImm)
  case P2rRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case P2rRegSymbol(dst: InstructionOperand.Register, pr: InstructionOperand.Symbol) extends Form(dst, pr)
  case P2rRegSymbolRegAddressImm(
      dst: InstructionOperand.Register,
      pr: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, pr, srcA, srcB, sb_offset)
  case P2rRegSymbolRegConst(
      dst: InstructionOperand.Register,
      pr: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank
  ) extends Form(dst, pr, srcA, sb)
  case P2rRegSymbolRegImm(
      dst: InstructionOperand.Register,
      pr: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, pr, srcA, sb)
  case P2rRegSymbolRegReg(
      dst: InstructionOperand.Register,
      pr: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, pr, srcA, srcB)
  case PbkConst(srcConst: InstructionOperand.ConstantBank) extends Form(srcConst)
  case PbkImm(sImm: InstructionOperand.Immediate) extends Form(sImm)
  case PcntConst(srcConst: InstructionOperand.ConstantBank) extends Form(srcConst)
  case PcntImm(sImm: InstructionOperand.Immediate) extends Form(sImm)
  case PexitImm(sImm: InstructionOperand.Immediate) extends Form(sImm)
  case PixldReg(dst: InstructionOperand.Register) extends Form(dst)
  case PixldRegAddress(dst: InstructionOperand.Register, srcA: InstructionOperand.Address) extends Form(dst, srcA)
  case PixldRegPred(dst: InstructionOperand.Register, dstPredicate: InstructionOperand.Predicate)
      extends Form(dst, dstPredicate)
  case PixldRegPredAddress(
      dst: InstructionOperand.Register,
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Address
  ) extends Form(dst, dstPredicate, srcA)
  case PlongjmpConst(srcConst: InstructionOperand.ConstantBank) extends Form(srcConst)
  case PlongjmpImm(sImm: InstructionOperand.Immediate) extends Form(sImm)
  case Plop3PredPredAddressImmPredImm(
      pu: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate
  ) extends Form(pu, pp, srcB, sb_offset, pr, uimm8)
  case Plop3PredPredAddressImmRegImm(
      pu: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate
  ) extends Form(pu, pp, srcB, sb_offset, srcC, uimm8)
  case Plop3PredPredConstPredImm(
      pu: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      sb: InstructionOperand.ConstantBank,
      pr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate
  ) extends Form(pu, pp, sb, pr, uimm8)
  case Plop3PredPredConstRegImm(
      pu: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate
  ) extends Form(pu, pp, sb, srcC, uimm8)
  case Plop3PredPredPredAddressImmPredImmImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate
  ) extends Form(pu, pv, pp, srcB, sb_offset, pr, uimm8, vimm8)
  case Plop3PredPredPredAddressImmRegImmImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate
  ) extends Form(pu, pv, pp, srcB, sb_offset, srcC, uimm8, vimm8)
  case Plop3PredPredPredConstPredImmImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      sb: InstructionOperand.ConstantBank,
      pr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate
  ) extends Form(pu, pv, pp, sb, pr, uimm8, vimm8)
  case Plop3PredPredPredConstRegImmImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate
  ) extends Form(pu, pv, pp, sb, srcC, uimm8, vimm8)
  case Plop3PredPredPredPred(
      pu: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      pr: InstructionOperand.Predicate
  ) extends Form(pu, pp, pq, pr)
  case Plop3PredPredPredPredImm(
      pu: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      pr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate
  ) extends Form(pu, pp, pq, pr, uimm8)
  case Plop3PredPredPredPredPredImmImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      pq: InstructionOperand.Predicate,
      pr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate
  ) extends Form(pu, pv, pp, pq, pr, uimm8, vimm8)
  case Plop3PredPredPredRegPredImmImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      pr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate
  ) extends Form(pu, pv, pp, srcB, pr, uimm8, vimm8)
  case Plop3PredPredPredRegRegImmImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate
  ) extends Form(pu, pv, pp, srcB, srcC, uimm8, vimm8)
  case Plop3PredPredRegAddressImmRegImmImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate
  ) extends Form(pu, pv, srcA, srcB, sb_offset, srcC, uimm8, vimm8)
  case Plop3PredPredRegConstRegImmImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate
  ) extends Form(pu, pv, srcA, sb, srcC, uimm8, vimm8)
  case Plop3PredPredRegPredImm(
      pu: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      pr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate
  ) extends Form(pu, pp, srcB, pr, uimm8)
  case Plop3PredPredRegRegImm(
      pu: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate
  ) extends Form(pu, pp, srcB, srcC, uimm8)
  case Plop3PredPredRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate
  ) extends Form(pu, pv, srcA, srcB, srcC, uimm8, vimm8)
  case Plop3PredRegAddressImmRegImm(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate
  ) extends Form(pu, srcA, srcB, sb_offset, srcC, uimm8)
  case Plop3PredRegConstRegImm(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate
  ) extends Form(pu, srcA, sb, srcC, uimm8)
  case Plop3PredRegRegRegImm(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate
  ) extends Form(pu, srcA, srcB, srcC, uimm8)
  case PmtrigImm(imm: InstructionOperand.Immediate) extends Form(imm)
  case PmtrigPredImm(pp: InstructionOperand.Predicate, imm: InstructionOperand.Immediate) extends Form(pp, imm)
  case PopcRegAddressImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcB, sb_offset)
  case PopcRegConst(dst: InstructionOperand.Register, sb: InstructionOperand.ConstantBank) extends Form(dst, sb)
  case PopcRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case PopcRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case PopcRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcConst)
  case PopcRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate
  ) extends Form(dst, srcA, uImm)
  case PopcRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case PreexitNoOperands() extends Form()
  case PretConst(srcConst: InstructionOperand.ConstantBank) extends Form(srcConst)
  case PretImm(sImm: InstructionOperand.Immediate) extends Form(sImm)
  case PrmtRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, sb_offset, srcC)
  case PrmtRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case PrmtRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case PrmtRegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, sc_offset)
  case PrmtRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcB, sc)
  case PrmtRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sc)
  case PrmtRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case PsetRegPredPred(
      dst: InstructionOperand.Register,
      pc: InstructionOperand.Predicate,
      pb: InstructionOperand.Predicate
  ) extends Form(dst, pc, pb)
  case PsetRegPredPredPred(
      dst: InstructionOperand.Register,
      pc: InstructionOperand.Predicate,
      pb: InstructionOperand.Predicate,
      pa: InstructionOperand.Predicate
  ) extends Form(dst, pc, pb, pa)
  case PsetpPredPredPred(
      dstPredicate: InstructionOperand.Predicate,
      pc: InstructionOperand.Predicate,
      pb: InstructionOperand.Predicate
  ) extends Form(dstPredicate, pc, pb)
  case PsetpPredPredPredPredPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      pc: InstructionOperand.Predicate,
      pb: InstructionOperand.Predicate,
      pa: InstructionOperand.Predicate
  ) extends Form(dstPredicate, nPd, pc, pb, pa)
  case Qadd4RegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sc)
  case Qadd4RegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcC)
  case Qfma4RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case Qfma4RegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sc)
  case Qfma4RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case QgmmaRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcC)
  case QgmmaRegAddressRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcC, uPp)
  case QgmmaRegAddressRegPredReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register
  ) extends Form(dst, srcA, srcC, uPp, re)
  case QgmmaRegAddressRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcC, uPp, re, id)
  case QgmmaRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case QgmmaRegAddressRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcC, re, id)
  case QgmmaRegAddressRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, uPp)
  case QgmmaRegAddressRegRegPredReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC, uPp, re)
  case QgmmaRegAddressRegRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, uPp, re, id)
  case QgmmaRegAddressRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC, re)
  case QgmmaRegAddressRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, re, id)
  case QgmmaRegRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case QgmmaRegRegAddressRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, uPp)
  case QgmmaRegRegAddressRegPredReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC, uPp, re)
  case QgmmaRegRegAddressRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, uPp, re, id)
  case QgmmaRegRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC, re)
  case QgmmaRegRegAddressRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, re, id)
  case QmmaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case QmmaRegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, uPp)
  case QmmaRegRegRegRegPredRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, uPp, re, id)
  case QmmaRegRegRegRegPredRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      rh: InstructionOperand.Register,
      uRi: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC, uPp, re, rh, uRi)
  case QmmaRegRegRegRegPredRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      rh: InstructionOperand.Register,
      uRi: InstructionOperand.Register,
      sparseId: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, uPp, re, rh, uRi, sparseId)
  case QmmaRegRegRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      id: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, re, id)
  case QmmaRegRegRegRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      rh: InstructionOperand.Register,
      uRi: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC, re, rh, uRi)
  case QmmaRegRegRegRegRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      rh: InstructionOperand.Register,
      uRi: InstructionOperand.Register,
      sparseId: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, re, rh, uRi, sparseId)
  case Qmul4RegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case Qmul4RegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case QspcPredAddress(pu: InstructionOperand.Predicate, srcA: InstructionOperand.Address) extends Form(pu, srcA)
  case QspcPredRegAddress(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address
  ) extends Form(pu, dst, srcA)
  case QspcRegAddress(dst: InstructionOperand.Register, srcA: InstructionOperand.Address) extends Form(dst, srcA)
  case R2bImmReg(barname: InstructionOperand.Immediate, srcB: InstructionOperand.Register) extends Form(barname, srcB)
  case R2pReg(srcA: InstructionOperand.Register) extends Form(srcA)
  case R2pRegConst(srcA: InstructionOperand.Register, srcConst: InstructionOperand.ConstantBank)
      extends Form(srcA, srcConst)
  case R2pRegImm(srcA: InstructionOperand.Register, uImm: InstructionOperand.Immediate) extends Form(srcA, uImm)
  case R2pRegReg(srcA: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(srcA, srcB)
  case R2pSymbolReg(pR: InstructionOperand.Symbol, srcA: InstructionOperand.Register) extends Form(pR, srcA)
  case R2pSymbolRegAddressImm(
      pR: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(pR, srcA, srcB, sb_offset)
  case R2pSymbolRegConst(
      pR: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank
  ) extends Form(pR, srcA, sb)
  case R2pSymbolRegImm(
      pR: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(pR, srcA, sb)
  case R2pSymbolRegReg(
      pR: InstructionOperand.Symbol,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(pR, srcA, srcB)
  case R2urPredRegReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register
  ) extends Form(pu, dst, srcA)
  case R2urRegReg(dst: InstructionOperand.Register, srcA: InstructionOperand.Register) extends Form(dst, srcA)
  case RamNoOperands() extends Form()
  case RedAddressReg(srcA: InstructionOperand.Address, srcB: InstructionOperand.Register) extends Form(srcA, srcB)
  case RedasAddressReg(srcA: InstructionOperand.Address, srcB: InstructionOperand.Register) extends Form(srcA, srcB)
  case RedgAddressReg(srcA: InstructionOperand.Address, srcB: InstructionOperand.Register) extends Form(srcA, srcB)
  case RedsAddressReg(srcA: InstructionOperand.Address, srcB: InstructionOperand.Register) extends Form(srcA, srcB)
  case ReduxRegReg(dst: InstructionOperand.Register, srcA: InstructionOperand.Register) extends Form(dst, srcA)
  case RetNoOperands() extends Form()
  case RetPredReg(pp: InstructionOperand.Predicate, srcA: InstructionOperand.Register) extends Form(pp, srcA)
  case RetPredRegImm(
      pp: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      ra_offset: InstructionOperand.Immediate
  ) extends Form(pp, srcA, ra_offset)
  case RetReg(srcA: InstructionOperand.Register) extends Form(srcA)
  case RetRegImm(srcA: InstructionOperand.Register, ra_offset: InstructionOperand.Immediate)
      extends Form(srcA, ra_offset)
  case RpcmovAddressImm(srcB: InstructionOperand.Address, sb_offset: InstructionOperand.Immediate)
      extends Form(srcB, sb_offset)
  case RpcmovConst(sb: InstructionOperand.ConstantBank) extends Form(sb)
  case RpcmovImm(sb: InstructionOperand.Immediate) extends Form(sb)
  case RpcmovReg(srcB: InstructionOperand.Register) extends Form(srcB)
  case RroRegConst(dst: InstructionOperand.Register, srcConst: InstructionOperand.ConstantBank)
      extends Form(dst, srcConst)
  case RroRegImm(dst: InstructionOperand.Register, uImm: InstructionOperand.Immediate) extends Form(dst, uImm)
  case RroRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case RttNoOperands() extends Form()
  case S2rRegSpecial(dst: InstructionOperand.Register, sR: InstructionOperand.SpecialRegister) extends Form(dst, sR)
  case S2urRegSpecial(dst: InstructionOperand.Register, sRa: InstructionOperand.SpecialRegister) extends Form(dst, sRa)
  case SamNoOperands() extends Form()
  case ScatterRegRegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      vecidx: InstructionOperand.Immediate,
      mask: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, vecidx, mask)
  case SelRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sb_offset, pp)
  case SelRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, pp)
  case SelRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, pp)
  case SelRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, pa)
  case SetcrsptrReg(srcA: InstructionOperand.Register) extends Form(srcA)
  case SetctaidReg(srcA: InstructionOperand.Register) extends Form(srcA)
  case SetlmembaseReg(srcA: InstructionOperand.Register) extends Form(srcA)
  case SgxtRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sb_offset)
  case SgxtRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, sb)
  case SgxtRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case SgxtRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case ShfRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, sb_offset, srcC)
  case ShfRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case ShfRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case ShfRegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, sc_offset)
  case ShfRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcB, sc)
  case ShfRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sc)
  case ShfRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case ShflPredRegRegImmImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm5: InstructionOperand.Immediate,
      uImm13: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, uImm5, uImm13)
  case ShflPredRegRegImmReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm5: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dstPredicate, dst, srcA, uImm5, srcC)
  case ShflPredRegRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uImm13: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, srcB, uImm13)
  case ShflPredRegRegRegReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dstPredicate, dst, srcA, srcB, srcC)
  case ShintImmImm(uImmMsb: InstructionOperand.Immediate, uImmLsb: InstructionOperand.Immediate)
      extends Form(uImmMsb, uImmLsb)
  case ShintImmImmImmImmImmImmImmImmImmImmImmImmImmImm(
      uImmA: InstructionOperand.Immediate,
      uImmB: InstructionOperand.Immediate,
      uImmC: InstructionOperand.Immediate,
      uImmD: InstructionOperand.Immediate,
      uImmE: InstructionOperand.Immediate,
      uImmF: InstructionOperand.Immediate,
      uImmG: InstructionOperand.Immediate,
      uImmH: InstructionOperand.Immediate,
      uImmI: InstructionOperand.Immediate,
      uImmJ: InstructionOperand.Immediate,
      uImmK: InstructionOperand.Immediate,
      uImmL: InstructionOperand.Immediate,
      uImmM: InstructionOperand.Immediate,
      uImmN: InstructionOperand.Immediate
  ) extends Form(uImmA, uImmB, uImmC, uImmD, uImmE, uImmF, uImmG, uImmH, uImmI, uImmJ, uImmK, uImmL, uImmM, uImmN)
  case ShlRegImmReg(
      dst: InstructionOperand.Register,
      sa: InstructionOperand.Immediate,
      srcB: InstructionOperand.Register
  ) extends Form(dst, sa, srcB)
  case ShlRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sb_offset)
  case ShlRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, sb)
  case ShlRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case ShlRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case ShrRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sb_offset)
  case ShrRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, sb)
  case ShrRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case ShrRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case SpmetadataRegRegRegImmImmImm(
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      vecidx: InstructionOperand.Immediate,
      mask: InstructionOperand.Immediate,
      destidx: InstructionOperand.Immediate
  ) extends Form(dst, srcB, srcC, vecidx, mask, destidx)
  case SsyConst(srcConst: InstructionOperand.ConstantBank) extends Form(srcConst)
  case SsyImm(sImm: InstructionOperand.Immediate) extends Form(sImm)
  case StAddressReg(srcA: InstructionOperand.Address, srcB: InstructionOperand.Register) extends Form(srcA, srcB)
  case StAddressRegPred(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      plg: InstructionOperand.Predicate
  ) extends Form(srcA, srcB, plg)
  case StasAddressReg(srcA: InstructionOperand.Address, srcB: InstructionOperand.Register) extends Form(srcA, srcB)
  case StgAddressReg(srcA: InstructionOperand.Address, srcB: InstructionOperand.Register) extends Form(srcA, srcB)
  case StgAddressRegReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      rb2: InstructionOperand.Register
  ) extends Form(srcA, srcB, rb2)
  case StgAddressRegRegImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      rb2: InstructionOperand.Register,
      word_mask: InstructionOperand.Immediate
  ) extends Form(srcA, srcB, rb2, word_mask)
  case StlAddressReg(srcA: InstructionOperand.Address, srcB: InstructionOperand.Register) extends Form(srcA, srcB)
  case StpImm(uImm: InstructionOperand.Immediate) extends Form(uImm)
  case StsAddressReg(srcA: InstructionOperand.Address, srcB: InstructionOperand.Register) extends Form(srcA, srcB)
  case StsculPredAddressReg(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register
  ) extends Form(dstPredicate, srcA, srcB)
  case StsmAddressReg(srcA: InstructionOperand.Address, srcB: InstructionOperand.Register) extends Form(srcA, srcB)
  case SttAddressReg(srcC: InstructionOperand.Address, srcB: InstructionOperand.Register) extends Form(srcC, srcB)
  case SttmAddressReg(srcC: InstructionOperand.Address, srcB: InstructionOperand.Register) extends Form(srcC, srcB)
  case SuatomPredRegAddressRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      suid: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, srcB, suid)
  case SuatomPredRegAddressRegImmImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate
  ) extends Form(pu, dst, srcA, srcB, bank5, tsPtrIdx)
  case SuatomPredRegAddressRegImmImmImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, dst, srcA, srcB, bank5, hdrtblbase6, tid8)
  case SuatomPredRegAddressRegReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dstPredicate, dst, srcA, srcB, srcC)
  case SuatomPredRegAddressRegRegImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, dst, srcA, srcB, srcC, tid8)
  case SuatomPredRegAddressRegRegReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(pu, dst, srcA, srcB, srcC, uRe)
  case SuatomPredRegAddressRegRegRegImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, dst, srcA, srcB, srcC, uRe, tid8)
  case SuatomPredRegAddressRegRegRegReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC1: InstructionOperand.Register,
      srcC2: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(pu, dst, srcA, srcB, srcC1, srcC2, uRe)
  case SuatomRegAddressRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      suid: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, suid)
  case SuatomRegAddressRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, bank5, tsPtrIdx)
  case SuatomRegAddressRegImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, bank5, hdrtblbase6, tid8)
  case SuatomRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case SuatomRegAddressRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, tid8)
  case SuatomRegAddressRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC, uRe)
  case SuatomRegAddressRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, uRe, tid8)
  case SuatomRegAddressRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC1: InstructionOperand.Register,
      srcC2: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC1, srcC2, uRe)
  case SubfmPredRegRegConstReg(
      pcl: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(pcl, dst, srcA, srcConst, srcC)
  case SubfmPredRegRegImmReg(
      pcl: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(pcl, dst, srcA, uImm, srcC)
  case SubfmPredRegRegRegConst(
      pcl: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(pcl, dst, srcA, srcB, srcConst)
  case SubfmPredRegRegRegReg(
      pcl: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(pcl, dst, srcA, srcB, srcC)
  case SuclampPredRegRegConstImm(
      pcl: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      sImm: InstructionOperand.Immediate
  ) extends Form(pcl, dst, srcA, srcConst, sImm)
  case SuclampPredRegRegImmImm(
      pcl: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      sImm: InstructionOperand.Immediate
  ) extends Form(pcl, dst, srcA, uImm, sImm)
  case SuclampPredRegRegRegImm(
      pcl: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate
  ) extends Form(pcl, dst, srcA, srcB, sImm)
  case SueauRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcConst, srcC)
  case SueauRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, uImm, srcC)
  case SueauRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcB, srcConst)
  case SueauRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case SuldPredRegAddressImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      suid: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, suid)
  case SuldPredRegAddressImmImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate
  ) extends Form(pu, dst, srcA, bank5, tsPtrIdx)
  case SuldPredRegAddressImmImmImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, dst, srcA, bank5, hdrtblbase6, tid8)
  case SuldPredRegAddressReg(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register
  ) extends Form(dstPredicate, dst, srcA, srcC)
  case SuldPredRegAddressRegImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, dst, srcA, srcC, tid8)
  case SuldPredRegAddressRegReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(pu, dst, srcA, srcC, uRe)
  case SuldPredRegAddressRegRegImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, dst, srcA, srcC, uRe, tid8)
  case SuldPredRegAddressRegRegReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC1: InstructionOperand.Register,
      srcC2: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(pu, dst, srcA, srcC1, srcC2, uRe)
  case SuldRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      suid: InstructionOperand.Immediate
  ) extends Form(dst, srcA, suid)
  case SuldRegAddressImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate
  ) extends Form(dst, srcA, bank5, tsPtrIdx)
  case SuldRegAddressImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate
  ) extends Form(dst, srcA, bank5, hdrtblbase6, tid8)
  case SuldRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcC)
  case SuldRegAddressRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcC, tid8)
  case SuldRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(dst, srcA, srcC, uRe)
  case SuldRegAddressRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcC, uRe, tid8)
  case SuldRegAddressRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC1: InstructionOperand.Register,
      srcC2: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(dst, srcA, srcC1, srcC2, uRe)
  case SuldgaRegAddressConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcConst: InstructionOperand.ConstantBank,
      pcl: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcConst, pcl)
  case SuldgaRegAddressRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      pcl: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, pcl)
  case SuqueryPredRegAddressImmImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate
  ) extends Form(pu, dst, srcA, bank5, tsPtrIdx)
  case SuqueryPredRegAddressImmImmImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, dst, srcA, bank5, hdrtblbase6, tid8)
  case SuqueryPredRegAddressReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register
  ) extends Form(pu, dst, srcA, srcC)
  case SuqueryPredRegAddressRegImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, dst, srcA, srcC, tid8)
  case SuqueryPredRegAddressRegReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(pu, dst, srcA, srcC, uRe)
  case SuqueryPredRegAddressRegRegImm(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, dst, srcA, srcC, uRe, tid8)
  case SuqueryPredRegAddressRegRegReg(
      pu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC1: InstructionOperand.Register,
      srcC2: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(pu, dst, srcA, srcC1, srcC2, uRe)
  case SuqueryRegAddressImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate
  ) extends Form(dst, srcA, bank5, tsPtrIdx)
  case SuqueryRegAddressImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate
  ) extends Form(dst, srcA, bank5, hdrtblbase6, tid8)
  case SuqueryRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcC)
  case SuqueryRegAddressRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcC, tid8)
  case SuqueryRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(dst, srcA, srcC, uRe)
  case SuqueryRegAddressRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcC, uRe, tid8)
  case SuqueryRegAddressRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcC1: InstructionOperand.Register,
      srcC2: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(dst, srcA, srcC1, srcC2, uRe)
  case SuredAddressRegImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      suid: InstructionOperand.Immediate
  ) extends Form(srcA, srcB, suid)
  case SuredAddressRegImmImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate
  ) extends Form(srcA, srcB, bank5, tsPtrIdx)
  case SuredAddressRegImmImmImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate
  ) extends Form(srcA, srcB, bank5, hdrtblbase6, tid8)
  case SuredAddressRegReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(srcA, srcB, srcC)
  case SuredAddressRegRegImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(srcA, srcB, srcC, tid8)
  case SuredAddressRegRegReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(srcA, srcB, srcC, uRe)
  case SuredAddressRegRegRegImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(srcA, srcB, srcC, uRe, tid8)
  case SuredAddressRegRegRegReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC1: InstructionOperand.Register,
      srcC2: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(srcA, srcB, srcC1, srcC2, uRe)
  case SustAddressRegImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      suid: InstructionOperand.Immediate
  ) extends Form(srcA, srcB, suid)
  case SustAddressRegImmImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate
  ) extends Form(srcA, srcB, bank5, tsPtrIdx)
  case SustAddressRegImmImmImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate
  ) extends Form(srcA, srcB, bank5, hdrtblbase6, tid8)
  case SustAddressRegReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(srcA, srcB, srcC)
  case SustAddressRegRegImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(srcA, srcB, srcC, tid8)
  case SustAddressRegRegReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(srcA, srcB, srcC, uRe)
  case SustAddressRegRegRegImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(srcA, srcB, srcC, uRe, tid8)
  case SustAddressRegRegRegReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC1: InstructionOperand.Register,
      srcC2: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(srcA, srcB, srcC1, srcC2, uRe)
  case SustgaAddressConstRegPred(
      srcA: InstructionOperand.Address,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pcl: InstructionOperand.Predicate
  ) extends Form(srcA, srcConst, srcC, pcl)
  case SustgaAddressRegRegPred(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pcl: InstructionOperand.Predicate
  ) extends Form(srcA, srcB, srcC, pcl)
  case SyncNoOperands() extends Form()
  case SyncsNoOperands() extends Form()
  case SyncsAddress(srcA: InstructionOperand.Address) extends Form(srcA)
  case SyncsAddressReg(srcA: InstructionOperand.Address, srcB: InstructionOperand.Register) extends Form(srcA, srcB)
  case SyncsPredAddressReg(
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register
  ) extends Form(pu, srcA, srcB)
  case SyncsRegAddress(dst: InstructionOperand.Register, srcA: InstructionOperand.Address) extends Form(dst, srcA)
  case SyncsRegAddressReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case SyncsRegAddressRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case TexPredRegRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, srcB, tid)
  case TexPredRegRegRegImmImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, srcB, tid, smp)
  case TexPredRegRegRegImmImmImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, srcB, tid, smp, wmsk)
  case TexPredRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(pu, rd2, dst, srcA, srcB)
  case TexPredRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, wmsk)
  case TexPredRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, bank5, tsPtrIdx)
  case TexPredRegRegRegRegImmImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8)
  case TexPredRegRegRegRegImmImmImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8, wmsk)
  case TexPredRegRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC)
  case TexPredRegRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC, tid8)
  case TexPredRegRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC, tid8, wmsk)
  case TexPredRegRegRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC, uRe)
  case TexPredRegRegRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC, uRe, tid8)
  case TexPredRegRegRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC, uRe, tid8, wmsk)
  case TexRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, tid)
  case TexRegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, tid, smp)
  case TexRegRegRegImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, tid, smp, wmsk)
  case TexRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(rd2, dst, srcA, srcB)
  case TexRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, wmsk)
  case TexRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, bank5, tsPtrIdx)
  case TexRegRegRegRegImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8)
  case TexRegRegRegRegImmImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8, wmsk)
  case TexRegRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(rd2, dst, srcA, srcB, srcC)
  case TexRegRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, srcC, tid8)
  case TexRegRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, srcC, tid8, wmsk)
  case TexRegRegRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(rd2, dst, srcA, srcB, srcC, uRe)
  case TexRegRegRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, srcC, uRe, tid8)
  case TexRegRegRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, srcC, uRe, tid8, wmsk)
  case TexdepbarImm(uImm: InstructionOperand.Immediate) extends Form(uImm)
  case TexsRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, tid)
  case TldPredRegRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, srcB, tid)
  case TldPredRegRegRegImmImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, srcB, tid, wmsk)
  case TldPredRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(pu, rd2, dst, srcA, srcB)
  case TldPredRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, wmsk)
  case TldPredRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, bank5, tsPtrIdx)
  case TldPredRegRegRegRegImmImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8)
  case TldPredRegRegRegRegImmImmImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8, wmsk)
  case TldPredRegRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC)
  case TldPredRegRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC, tid8)
  case TldPredRegRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC, tid8, wmsk)
  case TldPredRegRegRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC, uRe)
  case TldPredRegRegRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC, uRe, tid8)
  case TldPredRegRegRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC, uRe, tid8, wmsk)
  case TldRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, tid)
  case TldRegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, tid, wmsk)
  case TldRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(rd2, dst, srcA, srcB)
  case TldRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, wmsk)
  case TldRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, bank5, tsPtrIdx)
  case TldRegRegRegRegImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8)
  case TldRegRegRegRegImmImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8, wmsk)
  case TldRegRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(rd2, dst, srcA, srcB, srcC)
  case TldRegRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, srcC, tid8)
  case TldRegRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, srcC, tid8, wmsk)
  case TldRegRegRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(rd2, dst, srcA, srcB, srcC, uRe)
  case TldRegRegRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, srcC, uRe, tid8)
  case TldRegRegRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, srcC, uRe, tid8, wmsk)
  case Tld4PredRegRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, srcB, tid)
  case Tld4PredRegRegRegImmImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, srcB, tid, smp)
  case Tld4PredRegRegRegImmImmImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, srcB, tid, smp, wmsk)
  case Tld4PredRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(pu, rd2, dst, srcA, srcB)
  case Tld4PredRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, wmsk)
  case Tld4PredRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, bank5, tsPtrIdx)
  case Tld4PredRegRegRegRegImmImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8)
  case Tld4PredRegRegRegRegImmImmImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8, wmsk)
  case Tld4PredRegRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC)
  case Tld4PredRegRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC, tid8)
  case Tld4PredRegRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC, tid8, wmsk)
  case Tld4PredRegRegRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC, uRe)
  case Tld4PredRegRegRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC, uRe, tid8)
  case Tld4PredRegRegRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC, uRe, tid8, wmsk)
  case Tld4RegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, tid)
  case Tld4RegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, tid, smp)
  case Tld4RegRegRegImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, tid, smp, wmsk)
  case Tld4RegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(rd2, dst, srcA, srcB)
  case Tld4RegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, wmsk)
  case Tld4RegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, bank5, tsPtrIdx)
  case Tld4RegRegRegRegImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8)
  case Tld4RegRegRegRegImmImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8, wmsk)
  case Tld4RegRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(rd2, dst, srcA, srcB, srcC)
  case Tld4RegRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, srcC, tid8)
  case Tld4RegRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, srcC, tid8, wmsk)
  case Tld4RegRegRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(rd2, dst, srcA, srcB, srcC, uRe)
  case Tld4RegRegRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, srcC, uRe, tid8)
  case Tld4RegRegRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, srcC, uRe, tid8, wmsk)
  case Tld4sRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, tid)
  case TldsRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, tid)
  case TmmlRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      tid: InstructionOperand.Immediate
  ) extends Form(dst, srcA, tid)
  case TmmlRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate
  ) extends Form(dst, srcA, tid, smp)
  case TmmlRegRegImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(dst, srcA, tid, smp, wmsk)
  case TmmlRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, tid)
  case TmmlRegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, tid, smp)
  case TmmlRegRegRegImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, tid, smp, wmsk)
  case TmmlRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(rd2, dst, srcA, srcB)
  case TmmlRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, wmsk)
  case TmmlRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, bank5, tsPtrIdx)
  case TmmlRegRegRegRegImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8)
  case TmmlRegRegRegRegImmImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8, wmsk)
  case TmmlRegRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(rd2, dst, srcA, srcB, srcC)
  case TmmlRegRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, srcC, tid8)
  case TmmlRegRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, srcC, tid8, wmsk)
  case TxaRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      tid: InstructionOperand.Immediate
  ) extends Form(dst, srcA, tid)
  case TxaRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(dst, srcA, tid, wmsk)
  case TxaRegRegImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(dst, srcA, tid, smp, wmsk)
  case TxdPredRegRegRegImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, srcB, tid)
  case TxdPredRegRegRegImmImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, srcB, tid, smp)
  case TxdPredRegRegRegImmImmImm(
      dstPredicate: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(dstPredicate, dst, srcA, srcB, tid, smp, wmsk)
  case TxdPredRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(pu, rd2, dst, srcA, srcB)
  case TxdPredRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, wmsk)
  case TxdPredRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, bank5, tsPtrIdx)
  case TxdPredRegRegRegRegImmImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8)
  case TxdPredRegRegRegRegImmImmImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8, wmsk)
  case TxdPredRegRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC)
  case TxdPredRegRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC, tid8)
  case TxdPredRegRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC, tid8, wmsk)
  case TxdPredRegRegRegRegRegReg(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC, uRe)
  case TxdPredRegRegRegRegRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC, uRe, tid8)
  case TxdPredRegRegRegRegRegRegImmImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, srcA, srcB, srcC, uRe, tid8, wmsk)
  case TxdRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, tid)
  case TxdRegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, tid, smp)
  case TxdRegRegRegImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, tid, smp, wmsk)
  case TxdRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(rd2, dst, srcA, srcB)
  case TxdRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, wmsk)
  case TxdRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, bank5, tsPtrIdx)
  case TxdRegRegRegRegImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8)
  case TxdRegRegRegRegImmImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8, wmsk)
  case TxdRegRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(rd2, dst, srcA, srcB, srcC)
  case TxdRegRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, srcC, tid8)
  case TxdRegRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, srcC, tid8, wmsk)
  case TxdRegRegRegRegRegReg(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register
  ) extends Form(rd2, dst, srcA, srcB, srcC, uRe)
  case TxdRegRegRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, srcC, uRe, tid8)
  case TxdRegRegRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcB, srcC, uRe, tid8, wmsk)
  case TxqRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      tid: InstructionOperand.Immediate
  ) extends Form(dst, srcA, tid)
  case TxqRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      query: InstructionOperand.Immediate,
      tid: InstructionOperand.Immediate
  ) extends Form(dst, srcA, query, tid)
  case TxqRegRegImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      query: InstructionOperand.Immediate,
      tid: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(dst, srcA, query, tid, wmsk)
  case TxqRegRegImmImmImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      query: InstructionOperand.Immediate,
      tid: InstructionOperand.Immediate,
      smp: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(dst, srcA, query, tid, smp, wmsk)
  case TxqRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, wmsk)
  case TxqRegRegRegImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      tsPtrIdx: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, bank5, tsPtrIdx, wmsk)
  case TxqRegRegRegImmImmImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      bank5: InstructionOperand.Immediate,
      hdrtblbase6: InstructionOperand.Immediate,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, bank5, hdrtblbase6, tid8, wmsk)
  case TxqRegRegRegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcC, wmsk)
  case TxqRegRegRegRegImmImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      tid8: InstructionOperand.Immediate,
      wmsk: InstructionOperand.Immediate
  ) extends Form(rd2, dst, srcA, srcC, tid8, wmsk)
  case UblkcpAddressAddressReg(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register
  ) extends Form(srcB, srcA, srcC)
  case UblkcpAddressAddressRegAddress(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Address
  ) extends Form(srcB, srcA, srcC, uRe)
  case UblkpfAddressReg(srcA: InstructionOperand.Address, srcC: InstructionOperand.Register) extends Form(srcA, srcC)
  case UblkpfAddressRegAddress(
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Address
  ) extends Form(srcA, srcC, uRe)
  case UblkredAddressAddressReg(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register
  ) extends Form(srcB, srcA, srcC)
  case UblkredAddressAddressRegAddress(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Address
  ) extends Form(srcB, srcA, srcC, uRe)
  case UbmskRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case UbmskRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case UbrevRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case UbrevRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case UcgabararvNoOperands() extends Form()
  case UcgabargetReg(dst: InstructionOperand.Register) extends Form(dst)
  case UcgabarsetReg(srcB: InstructionOperand.Register) extends Form(srcB)
  case UcgabarwaitNoOperands() extends Form()
  case Ucgabar_ArvNoOperands() extends Form()
  case Ucgabar_WaitNoOperands() extends Form()
  case UcleaRegPredRegImmImm(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      constSizeU04: InstructionOperand.Immediate
  ) extends Form(dst, uPu, srcA, sb, constSizeU04)
  case UcleaRegPredRegRegImm(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      constSizeU04: InstructionOperand.Immediate
  ) extends Form(dst, uPu, srcA, srcB, constSizeU04)
  case Uf2fRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case Uf2fRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case Uf2fpRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case Uf2fpRegImmReg(
      dst: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, sb, srcC)
  case Uf2fpRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case Uf2fpRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case Uf2fpRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case Uf2fpRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case Uf2fpRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sc)
  case Uf2fpRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case Uf2iRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case Uf2iRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case Uf2ipRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case Uf2ipRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sc)
  case Uf2ipRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case UfaddRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sc)
  case UfaddRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcC)
  case UffmaRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case UffmaRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sc)
  case UffmaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case UfhaddRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcC)
  case UfhfmaRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case UfloRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case UfloRegPredImm(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, uPu, sb)
  case UfloRegPredReg(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register
  ) extends Form(dst, uPu, srcB)
  case UfloRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case UfmnmxRegPredRegImmPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, uPu, srcA, sb, uPp)
  case UfmnmxRegPredRegRegPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, uPu, srcA, srcB, uPp)
  case UfmnmxRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, uPp)
  case UfmnmxRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, uPp)
  case UfmulRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case UfmulRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case UfrndRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case UfrndRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case UfselRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, uPp)
  case UfselRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, uPp)
  case UfsetRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case UfsetRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, uPp)
  case UfsetRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case UfsetRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, uPp)
  case UfsetpPredPredRegImmPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(uPu, uPv, srcA, sb, uPp)
  case UfsetpPredPredRegRegPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(uPu, uPv, srcA, srcB, uPp)
  case UfsetpPredRegImm(
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(uPu, srcA, sb)
  case UfsetpPredRegReg(
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(uPu, srcA, srcB)
  case UgetnextworkidAddressAddress(srcA: InstructionOperand.Address, srcB: InstructionOperand.Address)
      extends Form(srcA, srcB)
  case Ui2fRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case Ui2fRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case Ui2fpRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case Ui2fpRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case Ui2iRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case Ui2iRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case Ui2ipRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case Ui2ipRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case UiabsRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case UiabsRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case Uiadd3RegPredPredRegImmReg(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, uPu, uPv, srcA, sb, srcC)
  case Uiadd3RegPredPredRegImmRegPredPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate
  ) extends Form(dst, uPu, uPv, srcA, sb, srcC, uPp, uPq)
  case Uiadd3RegPredPredRegRegReg(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, uPu, uPv, srcA, srcB, srcC)
  case Uiadd3RegPredPredRegRegRegPredPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate
  ) extends Form(dst, uPu, uPv, srcA, srcB, srcC, uPp, uPq)
  case Uiadd3RegPredRegImmReg(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, uPu, srcA, sb, srcC)
  case Uiadd3RegPredRegImmRegPredPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate
  ) extends Form(dst, uPu, srcA, sb, srcC, uPp, uPq)
  case Uiadd3RegPredRegRegReg(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, uPu, srcA, srcB, srcC)
  case Uiadd3RegPredRegRegRegPredPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate
  ) extends Form(dst, uPu, srcA, srcB, srcC, uPp, uPq)
  case Uiadd3RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case Uiadd3RegRegImmRegPredPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, uPp, uPq)
  case Uiadd3RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case Uiadd3RegRegRegRegPredPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, uPp, uPq)
  case UimadRegPredRegImmReg(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, uPu, srcA, sb, srcC)
  case UimadRegPredRegImmRegPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, uPu, srcA, sb, srcC, uPp)
  case UimadRegPredRegRegReg(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, uPu, srcA, srcB, srcC)
  case UimadRegPredRegRegRegPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, uPu, srcA, srcB, srcC, uPp)
  case UimadRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case UimadRegRegImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, uPp)
  case UimadRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sc)
  case UimadRegRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sc, uPp)
  case UimadRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case UimadRegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, uPp)
  case UimnmxPredPredRegRegImmPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(uPu, uPv, dst, srcA, sb, uPp)
  case UimnmxPredPredRegRegImmPredPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate
  ) extends Form(uPu, uPv, dst, srcA, sb, uPp, uPq)
  case UimnmxPredPredRegRegRegPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(uPu, uPv, dst, srcA, srcB, uPp)
  case UimnmxPredPredRegRegRegPredPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate
  ) extends Form(uPu, uPv, dst, srcA, srcB, uPp, uPq)
  case UimnmxPredRegRegImmPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(uPu, dst, srcA, sb, uPp)
  case UimnmxPredRegRegImmPredPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate
  ) extends Form(uPu, dst, srcA, sb, uPp, uPq)
  case UimnmxPredRegRegRegPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(uPu, dst, srcA, srcB, uPp)
  case UimnmxPredRegRegRegPredPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate
  ) extends Form(uPu, dst, srcA, srcB, uPp, uPq)
  case UimnmxRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, uPp)
  case UimnmxRegRegImmPredPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, uPp, uPq)
  case UimnmxRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, uPp)
  case UimnmxRegRegRegPredPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, uPp, uPq)
  case UisetpPredPredRegImmPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(uPu, uPv, srcA, sb, uPp)
  case UisetpPredPredRegImmPredPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate,
      uPr: InstructionOperand.Predicate
  ) extends Form(uPu, uPv, srcA, sb, uPp, uPr)
  case UisetpPredPredRegRegPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(uPu, uPv, srcA, srcB, uPp)
  case UisetpPredPredRegRegPredPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      uPr: InstructionOperand.Predicate
  ) extends Form(uPu, uPv, srcA, srcB, uPp, uPr)
  case UisetpPredRegImm(
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(uPu, srcA, sb)
  case UisetpPredRegImmPred(
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPr: InstructionOperand.Predicate
  ) extends Form(uPu, srcA, sb, uPr)
  case UisetpPredRegReg(
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(uPu, srcA, srcB)
  case UisetpPredRegRegPred(
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPr: InstructionOperand.Predicate
  ) extends Form(uPu, srcA, srcB, uPr)
  case UldcRegAddress(dst: InstructionOperand.Register, srcA: InstructionOperand.Address) extends Form(dst, srcA)
  case UldcRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      sa_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sa_offset)
  case UldcRegAddressPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Address,
      uPx: InstructionOperand.Predicate
  ) extends Form(dst, srcA, uPx)
  case UldcRegConst(dst: InstructionOperand.Register, sa: InstructionOperand.ConstantBank) extends Form(dst, sa)
  case UleaRegPredRegImmImm(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, uPu, srcA, sb, scaleU5)
  case UleaRegPredRegImmImmPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, uPu, srcA, sb, scaleU5, uPp)
  case UleaRegPredRegImmRegImm(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, uPu, srcA, sb, srcC, scaleU5)
  case UleaRegPredRegImmRegImmPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, uPu, srcA, sb, srcC, scaleU5, uPp)
  case UleaRegPredRegRegImm(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, uPu, srcA, srcB, scaleU5)
  case UleaRegPredRegRegImmImm(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, uPu, srcA, srcB, sc, scaleU5)
  case UleaRegPredRegRegImmImmPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, uPu, srcA, srcB, sc, scaleU5, uPp)
  case UleaRegPredRegRegImmPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, uPu, srcA, srcB, scaleU5, uPp)
  case UleaRegPredRegRegRegImm(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, uPu, srcA, srcB, srcC, scaleU5)
  case UleaRegPredRegRegRegImmPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, uPu, srcA, srcB, srcC, scaleU5, uPp)
  case UleaRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb, scaleU5)
  case UleaRegRegImmImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, scaleU5, uPp)
  case UleaRegRegImmRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb, srcC, scaleU5)
  case UleaRegRegImmRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, scaleU5, uPp)
  case UleaRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, scaleU5)
  case UleaRegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sc, scaleU5)
  case UleaRegRegRegImmImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      scaleU5: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sc, scaleU5, uPp)
  case UleaRegRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, scaleU5, uPp)
  case UleaRegRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, scaleU5)
  case UleaRegRegRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      scaleU5: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, scaleU5, uPp)
  case UlepcReg(dst: InstructionOperand.Register) extends Form(dst)
  case UlepcRegImm(dst: InstructionOperand.Register, sImm58: InstructionOperand.Immediate) extends Form(dst, sImm58)
  case UlopPredRegRegImm(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(uPu, dst, srcA, sb)
  case UlopPredRegRegImmPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(uPu, dst, srcA, sb, uPp)
  case UlopPredRegRegReg(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(uPu, dst, srcA, srcB)
  case UlopPredRegRegRegPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(uPu, dst, srcA, srcB, uPp)
  case UlopRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case UlopRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, uPp)
  case UlopRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case UlopRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, uPp)
  case Ulop3PredRegRegImmReg(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(uPu, dst, srcA, sb, srcC)
  case Ulop3PredRegRegImmRegImm(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate
  ) extends Form(uPu, dst, srcA, sb, srcC, imm8)
  case Ulop3PredRegRegImmRegImmPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(uPu, dst, srcA, sb, srcC, imm8, uPp)
  case Ulop3PredRegRegImmRegPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(uPu, dst, srcA, sb, srcC, uPp)
  case Ulop3PredRegRegRegReg(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(uPu, dst, srcA, srcB, srcC)
  case Ulop3PredRegRegRegRegImm(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate
  ) extends Form(uPu, dst, srcA, srcB, srcC, imm8)
  case Ulop3PredRegRegRegRegImmPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(uPu, dst, srcA, srcB, srcC, imm8, uPp)
  case Ulop3PredRegRegRegRegPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(uPu, dst, srcA, srcB, srcC, uPp)
  case Ulop3RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case Ulop3RegRegImmRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb, srcC, imm8)
  case Ulop3RegRegImmRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, imm8, uPp)
  case Ulop3RegRegImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, uPp)
  case Ulop3RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case Ulop3RegRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, imm8)
  case Ulop3RegRegRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      imm8: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, imm8, uPp)
  case Ulop3RegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, uPp)
  case Ulop32iPredRegRegImm(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(uPu, dst, srcA, sb)
  case Ulop32iPredRegRegImmPred(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(uPu, dst, srcA, sb, uPp)
  case Ulop32iRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case Ulop32iRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, uPp)
  case UmemsetsAddressRegReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(srcA, srcB, srcC)
  case UmovRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case UmovRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case Up2urReg(dst: InstructionOperand.Register) extends Form(dst)
  case Up2urRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case Up2urRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case Uplop3PredPredPredPred(
      uPu: InstructionOperand.Predicate,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      uPr: InstructionOperand.Predicate
  ) extends Form(uPu, uPp, uPq, uPr)
  case Uplop3PredPredPredPredImm(
      uPu: InstructionOperand.Predicate,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      uPr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate
  ) extends Form(uPu, uPp, uPq, uPr, uimm8)
  case Uplop3PredPredPredPredPredImmImm(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      uPr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate
  ) extends Form(uPu, uPv, uPp, uPq, uPr, uimm8, vimm8)
  case Uplop3PredPredPredRegPredImmImm(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      uPp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      uPr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate
  ) extends Form(uPu, uPv, uPp, srcB, uPr, uimm8, vimm8)
  case Uplop3PredPredPredRegRegImmImm(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      uPp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate
  ) extends Form(uPu, uPv, uPp, srcB, srcC, uimm8, vimm8)
  case Uplop3PredPredRegPredImm(
      uPu: InstructionOperand.Predicate,
      uPp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      uPr: InstructionOperand.Predicate,
      uimm8: InstructionOperand.Immediate
  ) extends Form(uPu, uPp, srcB, uPr, uimm8)
  case Uplop3PredPredRegRegImm(
      uPu: InstructionOperand.Predicate,
      uPp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate
  ) extends Form(uPu, uPp, srcB, srcC, uimm8)
  case Uplop3PredPredRegRegRegImmImm(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate,
      vimm8: InstructionOperand.Immediate
  ) extends Form(uPu, uPv, srcA, srcB, srcC, uimm8, vimm8)
  case Uplop3PredRegRegRegImm(
      uPu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uimm8: InstructionOperand.Immediate
  ) extends Form(uPu, srcA, srcB, srcC, uimm8)
  case UpopcRegImm(dst: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(dst, sb)
  case UpopcRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case UprmtRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case UprmtRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case UpsetpPredPredPred(
      uPu: InstructionOperand.Predicate,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate
  ) extends Form(uPu, uPp, uPq)
  case UpsetpPredPredPredPredPred(
      uPu: InstructionOperand.Predicate,
      uPv: InstructionOperand.Predicate,
      uPp: InstructionOperand.Predicate,
      uPq: InstructionOperand.Predicate,
      uPr: InstructionOperand.Predicate
  ) extends Form(uPu, uPv, uPp, uPq, uPr)
  case Ur2upReg(srcA: InstructionOperand.Register) extends Form(srcA)
  case Ur2upRegImm(srcA: InstructionOperand.Register, sb: InstructionOperand.Immediate) extends Form(srcA, sb)
  case Ur2upRegReg(srcA: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(srcA, srcB)
  case UredgrAddressReg(srcA: InstructionOperand.Address, srcB: InstructionOperand.Register) extends Form(srcA, srcB)
  case UselRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, uPp)
  case UselRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, uPp)
  case UsetmaxregImm(sb: InstructionOperand.Immediate) extends Form(sb)
  case UsetmaxregPredImm(uPu: InstructionOperand.Predicate, sb: InstructionOperand.Immediate) extends Form(uPu, sb)
  case UsetmaxregPredReg(uPu: InstructionOperand.Predicate, srcB: InstructionOperand.Register) extends Form(uPu, srcB)
  case UsetmaxregReg(srcB: InstructionOperand.Register) extends Form(srcB)
  case UsetshmszNoOperands() extends Form()
  case UsetshmszImm(sb: InstructionOperand.Immediate) extends Form(sb)
  case UsetshmszReg(srcB: InstructionOperand.Register) extends Form(srcB)
  case UsgxtRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case UsgxtRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case UshfRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case UshfRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sc)
  case UshfRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case UshlRegImmReg(
      dst: InstructionOperand.Register,
      sa: InstructionOperand.Immediate,
      srcB: InstructionOperand.Register
  ) extends Form(dst, sa, srcB)
  case UshlRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case UshlRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case UshrRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case UshrRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case UstgrAddressReg(srcA: InstructionOperand.Address, srcB: InstructionOperand.Register) extends Form(srcA, srcB)
  case UtcatomswsPredRegReg(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(uPu, dst, srcB)
  case UtcatomswsPredRegRegReg(
      uPu: InstructionOperand.Predicate,
      dst: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(uPu, dst, srcB, srcC)
  case UtcatomswsRegReg(dst: InstructionOperand.Register, srcB: InstructionOperand.Register) extends Form(dst, srcB)
  case UtcbarNoOperands() extends Form()
  case UtcbarAddressRegReg(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(srcA, srcB, srcC)
  case UtccpAddressAddress(srcA: InstructionOperand.Address, srcB: InstructionOperand.Address) extends Form(srcA, srcB)
  case UtchmmaAddressAddressAddressAddressAddressRegPred(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Address,
      uRe: InstructionOperand.Address,
      uRh: InstructionOperand.Address,
      uRi: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(srcA, srcB, srcC, uRe, uRh, uRi, uPp)
  case UtchmmaAddressAddressAddressAddressAddressRegPredImm(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Address,
      uRe: InstructionOperand.Address,
      uRh: InstructionOperand.Address,
      uRi: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      scaleU4: InstructionOperand.Immediate
  ) extends Form(srcA, srcB, srcC, uRe, uRh, uRi, uPp, scaleU4)
  case UtcimmaAddressAddressAddressAddressAddressRegPred(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Address,
      uRe: InstructionOperand.Address,
      uRh: InstructionOperand.Address,
      uRi: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(srcA, srcB, srcC, uRe, uRh, uRi, uPp)
  case UtcommaAddressAddressAddressAddressAddressAddressPred(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Address,
      uRe: InstructionOperand.Address,
      uRh: InstructionOperand.Address,
      uRi: InstructionOperand.Address,
      uPp: InstructionOperand.Predicate
  ) extends Form(srcA, srcB, srcC, uRe, uRh, uRi, uPp)
  case UtcqmmaAddressAddressAddressAddressAddressAddressPred(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Address,
      uRe: InstructionOperand.Address,
      uRh: InstructionOperand.Address,
      uRi: InstructionOperand.Address,
      uPp: InstructionOperand.Predicate
  ) extends Form(srcA, srcB, srcC, uRe, uRh, uRi, uPp)
  case UtcqmmaAddressAddressAddressAddressAddressRegPred(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Address,
      uRe: InstructionOperand.Address,
      uRh: InstructionOperand.Address,
      uRi: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(srcA, srcB, srcC, uRe, uRh, uRi, uPp)
  case UtcshiftAddress(srcA: InstructionOperand.Address) extends Form(srcA)
  case UtmacctlNoOperands() extends Form()
  case UtmacctlAddress(srcA: InstructionOperand.Address) extends Form(srcA)
  case UtmacmdflushNoOperands() extends Form()
  case UtmaldgAddressAddress(srcB: InstructionOperand.Address, srcA: InstructionOperand.Address)
      extends Form(srcB, srcA)
  case UtmaldgAddressAddressAddress(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      uRe: InstructionOperand.Address
  ) extends Form(srcB, srcA, uRe)
  case UtmaldgAddressAddressReg(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register
  ) extends Form(srcB, srcA, srcC)
  case UtmaldgAddressAddressRegAddress(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Address
  ) extends Form(srcB, srcA, srcC, uRe)
  case UtmapfAddressAddress(srcB: InstructionOperand.Address, srcA: InstructionOperand.Address) extends Form(srcB, srcA)
  case UtmapfAddressAddressAddress(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      uRe: InstructionOperand.Address
  ) extends Form(srcB, srcA, uRe)
  case UtmapfAddressAddressReg(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register
  ) extends Form(srcB, srcA, srcC)
  case UtmapfAddressAddressRegAddress(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      srcC: InstructionOperand.Register,
      uRe: InstructionOperand.Address
  ) extends Form(srcB, srcA, srcC, uRe)
  case UtmaredgAddressAddress(srcB: InstructionOperand.Address, srcA: InstructionOperand.Address)
      extends Form(srcB, srcA)
  case UtmaredgAddressAddressAddress(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      uRe: InstructionOperand.Address
  ) extends Form(srcB, srcA, uRe)
  case UtmastgAddressAddress(srcB: InstructionOperand.Address, srcA: InstructionOperand.Address)
      extends Form(srcB, srcA)
  case UtmastgAddressAddressAddress(
      srcB: InstructionOperand.Address,
      srcA: InstructionOperand.Address,
      uRe: InstructionOperand.Address
  ) extends Form(srcB, srcA, uRe)
  case UviaddRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case UviaddRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case UvimnmxRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, uPp)
  case UvimnmxRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, uPp)
  case UvirtcountImm(sa: InstructionOperand.Immediate) extends Form(sa)
  case UvirtcountReg(srcA: InstructionOperand.Register) extends Form(srcA)
  case VabsdiffRegPredRegAddressImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, srcA, srcB, sb_offset, srcC)
  case VabsdiffRegPredRegConstReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, srcA, sb, srcC)
  case VabsdiffRegPredRegImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, srcA, sb, srcC)
  case VabsdiffRegPredRegRegAddressImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, srcB, srcC, sc_offset)
  case VabsdiffRegPredRegRegConst(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank
  ) extends Form(dst, pu, srcA, srcB, sc)
  case VabsdiffRegPredRegRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, srcB, sc)
  case VabsdiffRegPredRegRegReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, srcA, srcB, srcC)
  case VabsdiffRegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, sb_offset, srcC)
  case VabsdiffRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case VabsdiffRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case VabsdiffRegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, sc_offset)
  case VabsdiffRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcB, sc)
  case VabsdiffRegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sc)
  case VabsdiffRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case Vabsdiff2RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, uImm, srcC)
  case Vabsdiff2RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case Vabsdiff4RegPredRegAddressImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, srcA, srcB, sb_offset, srcC)
  case Vabsdiff4RegPredRegConstReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, srcA, sb, srcC)
  case Vabsdiff4RegPredRegImmReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, srcA, sb, srcC)
  case Vabsdiff4RegPredRegRegAddressImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, srcB, srcC, sc_offset)
  case Vabsdiff4RegPredRegRegConst(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank
  ) extends Form(dst, pu, srcA, srcB, sc)
  case Vabsdiff4RegPredRegRegImm(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, pu, srcA, srcB, sc)
  case Vabsdiff4RegPredRegRegReg(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, pu, srcA, srcB, srcC)
  case Vabsdiff4RegRegAddressImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, sb_offset, srcC)
  case Vabsdiff4RegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case Vabsdiff4RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sb, srcC)
  case Vabsdiff4RegRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, sc_offset)
  case Vabsdiff4RegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcB, sc)
  case Vabsdiff4RegRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sc)
  case Vabsdiff4RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case VaddRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sImm, srcC)
  case VaddRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case Vadd2RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, uImm, srcC)
  case Vadd2RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case Vadd4RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, uImm, srcC)
  case Vadd4RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case VhmnmxRegRegAddressImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sb_offset, srcC, pp)
  case VhmnmxRegRegConstRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, pp)
  case VhmnmxRegRegImmImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      sb1: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, sb1, srcC, pp)
  case VhmnmxRegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, pp)
  case ViaddRegRegAddressImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, sb_offset)
  case ViaddRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, sb)
  case ViaddRegRegImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate
  ) extends Form(dst, srcA, sb)
  case ViaddRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB)
  case ViaddmnmxRegRegAddressImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sb_offset, srcC, pp)
  case ViaddmnmxRegRegConstRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, pp)
  case ViaddmnmxRegRegImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, pp)
  case ViaddmnmxRegRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Address,
      sc_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, sc_offset, pp)
  case ViaddmnmxRegRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sc, pp)
  case ViaddmnmxRegRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      sc: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sc, pp)
  case ViaddmnmxRegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, pp)
  case VildRegAddress(dst: InstructionOperand.Register, srcA: InstructionOperand.Address) extends Form(dst, srcA)
  case VimnmxRegPredPredRegAddressImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, pv, srcA, srcB, sb_offset, pp)
  case VimnmxRegPredPredRegConstPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, pv, srcA, sb, pp)
  case VimnmxRegPredPredRegImmPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, pv, srcA, sb, pp)
  case VimnmxRegPredPredRegRegPred(
      dst: InstructionOperand.Register,
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, pu, pv, srcA, srcB, pp)
  case VimnmxRegRegAddressImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sb_offset, pp)
  case VimnmxRegRegConstPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, pp)
  case VimnmxRegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, pp)
  case VimnmxRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, pp)
  case Vimnmx3RegRegAddressImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, sb_offset, srcC, pp)
  case Vimnmx3RegRegConstRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, pp)
  case Vimnmx3RegRegImmRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sb: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, sb, srcC, pp)
  case Vimnmx3RegRegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, srcC, pp)
  case VmadRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sImm, srcC)
  case VmadRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case VmnmxRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sImm, srcC)
  case VmnmxRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case Vmnmx2RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, uImm, srcC)
  case Vmnmx2RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case Vmnmx4RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, uImm, srcC)
  case Vmnmx4RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case VoteImm(uImm: InstructionOperand.Immediate) extends Form(uImm)
  case VotePredPred(dstPredicate: InstructionOperand.Predicate, pa: InstructionOperand.Predicate)
      extends Form(dstPredicate, pa)
  case VoteRegPredPred(
      dst: InstructionOperand.Register,
      dstPredicate: InstructionOperand.Predicate,
      pa: InstructionOperand.Predicate
  ) extends Form(dst, dstPredicate, pa)
  case VoteuRegPredPred(
      dst: InstructionOperand.Register,
      uPu: InstructionOperand.Predicate,
      pp: InstructionOperand.Predicate
  ) extends Form(dst, uPu, pp)
  case VoteVtgImm(sa: InstructionOperand.Immediate) extends Form(sa)
  case VoteVtgPredImm(pu: InstructionOperand.Predicate, sa: InstructionOperand.Immediate) extends Form(pu, sa)
  case VoteVtgPredPredImmPred(
      pu: InstructionOperand.Predicate,
      pv: InstructionOperand.Predicate,
      sa: InstructionOperand.Immediate,
      pp: InstructionOperand.Predicate
  ) extends Form(pu, pv, sa, pp)
  case Vsel2RegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate
  ) extends Form(dst, srcA, uImm, pa)
  case Vsel2RegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, pa)
  case Vsel4RegRegImmPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate
  ) extends Form(dst, srcA, uImm, pa)
  case Vsel4RegRegRegPred(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate
  ) extends Form(dst, srcA, srcB, pa)
  case VsetRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sImm, srcC)
  case VsetRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case Vset2RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, uImm, srcC)
  case Vset2RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case Vset4RegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, uImm, srcC)
  case Vset4RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case VsetpPredPredRegImmPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      pa: InstructionOperand.Predicate
  ) extends Form(dstPredicate, nPd, srcA, sImm, pa)
  case VsetpPredPredRegRegPred(
      dstPredicate: InstructionOperand.Predicate,
      nPd: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      pa: InstructionOperand.Predicate
  ) extends Form(dstPredicate, nPd, srcA, srcB, pa)
  case VsetpPredRegImm(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate
  ) extends Form(dstPredicate, srcA, sImm)
  case VsetpPredRegReg(
      dstPredicate: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register
  ) extends Form(dstPredicate, srcA, srcB)
  case VshlRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sImm, srcC)
  case VshlRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case VshrRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, sImm, srcC)
  case VshrRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case WarpgroupNoOperands() extends Form()
  case WarpgroupImm(gsbcnt: InstructionOperand.Immediate) extends Form(gsbcnt)
  case WarpgroupsetReg(srcB: InstructionOperand.Register) extends Form(srcB)
  case WarpsyncNoOperands() extends Form()
  case WarpsyncAddressImm(srcB: InstructionOperand.Address, sb_offset: InstructionOperand.Immediate)
      extends Form(srcB, sb_offset)
  case WarpsyncConst(sb: InstructionOperand.ConstantBank) extends Form(sb)
  case WarpsyncImm(sb: InstructionOperand.Immediate) extends Form(sb)
  case WarpsyncPred(pp: InstructionOperand.Predicate) extends Form(pp)
  case WarpsyncPredAddressImm(
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(pp, srcB, sb_offset)
  case WarpsyncPredConst(pp: InstructionOperand.Predicate, sb: InstructionOperand.ConstantBank) extends Form(pp, sb)
  case WarpsyncPredImm(pp: InstructionOperand.Predicate, sb: InstructionOperand.Immediate) extends Form(pp, sb)
  case WarpsyncPredReg(pp: InstructionOperand.Predicate, srcA: InstructionOperand.Register) extends Form(pp, srcA)
  case WarpsyncPredRegImm(
      pp: InstructionOperand.Predicate,
      srcA: InstructionOperand.Register,
      sImm: InstructionOperand.Immediate
  ) extends Form(pp, srcA, sImm)
  case WarpsyncReg(srcA: InstructionOperand.Register) extends Form(srcA)
  case WarpsyncRegImm(srcA: InstructionOperand.Register, sImm: InstructionOperand.Immediate) extends Form(srcA, sImm)
  case XmadRegRegConstReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcConst, srcC)
  case XmadRegRegImmReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      uImm: InstructionOperand.Immediate,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, uImm, srcC)
  case XmadRegRegRegConst(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcConst: InstructionOperand.ConstantBank
  ) extends Form(dst, srcA, srcB, srcConst)
  case XmadRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case YieldNoOperands() extends Form()
  case YieldPred(pp: InstructionOperand.Predicate) extends Form(pp)
  case Hir0x002NoOperands() extends Form()
  case Hir0x003NoOperands() extends Form()
  case Hir0x003Pred(pu: InstructionOperand.Predicate) extends Form(pu)
  case Hir0x004NoOperands() extends Form()
  case Hir0x005PredRegRegImm(
      pu: InstructionOperand.Predicate,
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      immU16: InstructionOperand.Immediate
  ) extends Form(pu, rd2, dst, immU16)
  case Hir0x005RegRegImm(
      rd2: InstructionOperand.Register,
      dst: InstructionOperand.Register,
      immU16: InstructionOperand.Immediate
  ) extends Form(rd2, dst, immU16)
  case Hir0x006Imm(sb: InstructionOperand.Immediate) extends Form(sb)
  case Hir0x007NoOperands() extends Form()
  case Hir0x008ImmRegReg(
      immU16: InstructionOperand.Immediate,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(immU16, srcB, srcC)
  case Hir0x020NoOperands() extends Form()
  case Hir0x020Imm(sb: InstructionOperand.Immediate) extends Form(sb)
  case Hir0x120AddressImm(srcB: InstructionOperand.Address, sb_offset: InstructionOperand.Immediate)
      extends Form(srcB, sb_offset)
  case Hir0x120Const(sb: InstructionOperand.ConstantBank) extends Form(sb)
  case Hir0x120Imm(sb: InstructionOperand.Immediate) extends Form(sb)
  case Hir0x120PredAddressImm(
      pp: InstructionOperand.Predicate,
      srcB: InstructionOperand.Address,
      sb_offset: InstructionOperand.Immediate
  ) extends Form(pp, srcB, sb_offset)
  case Hir0x120PredConst(pp: InstructionOperand.Predicate, sb: InstructionOperand.ConstantBank) extends Form(pp, sb)
  case Hir0x120PredImm(pp: InstructionOperand.Predicate, sb: InstructionOperand.Immediate) extends Form(pp, sb)
  case Hir0x120PredReg(pp: InstructionOperand.Predicate, srcB: InstructionOperand.Register) extends Form(pp, srcB)
  case Hir0x120Reg(srcB: InstructionOperand.Register) extends Form(srcB)
  case Hir0x130AddressAddressAddressAddressAddressAddressPred(
      srcA: InstructionOperand.Address,
      srcB: InstructionOperand.Address,
      srcC: InstructionOperand.Address,
      uRe: InstructionOperand.Address,
      uRh: InstructionOperand.Address,
      uRi: InstructionOperand.Address,
      uPp: InstructionOperand.Predicate
  ) extends Form(srcA, srcB, srcC, uRe, uRh, uRi, uPp)
  case Hir0x150RegRegRegRegPredRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      uPp: InstructionOperand.Predicate,
      re: InstructionOperand.Register,
      rh: InstructionOperand.Register,
      uRi: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC, uPp, re, rh, uRi)
  case Hir0x150RegRegRegRegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      re: InstructionOperand.Register,
      rh: InstructionOperand.Register,
      uRi: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC, re, rh, uRi)
  case Hir0x160Reg(srcB: InstructionOperand.Register) extends Form(srcB)
  case Hir0x170Reg(dst: InstructionOperand.Register) extends Form(dst)
  case Hir0x1a0RegRegRegRegImmImm(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register,
      vecidx2: InstructionOperand.Immediate,
      mdidx: InstructionOperand.Immediate
  ) extends Form(dst, srcA, srcB, srcC, vecidx2, mdidx)
  case Hir0x1b0RegRegRegReg(
      dst: InstructionOperand.Register,
      srcA: InstructionOperand.Register,
      srcB: InstructionOperand.Register,
      srcC: InstructionOperand.Register
  ) extends Form(dst, srcA, srcB, srcC)
  case Hir0x1e0NoOperands() extends Form()
  case Hir0x1f0ImmReg(barname: InstructionOperand.Immediate, srcB: InstructionOperand.Register)
      extends Form(barname, srcB)
  case Hir0x1f2Reg(dst: InstructionOperand.Register) extends Form(dst)
  case Hir0x1f3Reg(srcB: InstructionOperand.Register) extends Form(srcB)
}
