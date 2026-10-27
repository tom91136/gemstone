// Generated from the pinned PTX syntax domains. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.ptx
import net.kurobako.gemstone.constraint.*

object ModifierSlot {
  private lazy val byName: Map[String, ModifierSlot] = Map(
    "BoolOp" -> BoolOp,
    "CmpOp" -> CmpOp,
    "abtype" -> Abtype,
    "acc_prec" -> AccPrec,
    "action" -> Action,
    "alayout" -> Alayout,
    "atype" -> Atype,
    "bitOp" -> BitOp,
    "blayout" -> Blayout,
    "btype" -> Btype,
    "clamp" -> Clamp,
    "cmp" -> Cmp,
    "comp" -> Comp,
    "convertType" -> ConvertType,
    "cop" -> Cop,
    "cta_group" -> CtaGroup,
    "ctype" -> Ctype,
    "dim" -> Dim,
    "dtype" -> Dtype,
    "f6x2type" -> F6x2type,
    "f6x4type" -> F6x4type,
    "f8f6f4type" -> F8f6f4type,
    "f8type" -> F8type,
    "f8x2type" -> F8x2type,
    "f8x4type" -> F8x4type,
    "field1" -> Field1,
    "field2" -> Field2,
    "field3" -> Field3,
    "fp16x2" -> Fp16x2,
    "fp16x2type" -> Fp16x2type,
    "frnd" -> Frnd,
    "frnd2" -> Frnd2,
    "frnd3" -> Frnd3,
    "geom" -> Geom,
    "get_first_ctaid" -> GetFirstCtaid,
    "half_word_type" -> HalfWordType,
    "irnd" -> Irnd,
    "kind" -> Kind,
    "layout" -> Layout,
    "ldsem" -> Ldsem,
    "level" -> Level,
    "level1::eviction_priority" -> Level1EvictionPriority,
    "level2::eviction_priority" -> Level2EvictionPriority,
    "level::eviction_priority" -> LevelEvictionPriority,
    "level::prefetch_size" -> LevelPrefetchSize,
    "level::primary_priority" -> LevelPrimaryPriority,
    "level::secondary_priority" -> LevelSecondaryPriority,
    "load_mode" -> LoadMode,
    "mode" -> Mode,
    "num" -> Num,
    "op" -> Op,
    "op2" -> Op2,
    "packed_type" -> PackedType,
    "phase_type" -> PhaseType,
    "proxykind" -> Proxykind,
    "query" -> Query,
    "redOp" -> RedOp,
    "redOpBit" -> RedOpBit,
    "redOpMinMax" -> RedOpMinMax,
    "redsem" -> Redsem,
    "redtype" -> Redtype,
    "rnd" -> Rnd,
    "scale" -> Scale,
    "scale_vec_size" -> ScaleVecSize,
    "scale_vectorsize" -> ScaleVectorsize,
    "scope" -> Scope,
    "sem" -> Sem,
    "sem_fabric" -> SemFabric,
    "shape" -> Shape,
    "shape1" -> Shape1,
    "shared" -> Shared,
    "size" -> Size,
    "space" -> Space,
    "squery" -> Squery,
    "src_fmt" -> SrcFmt,
    "ss" -> Ss,
    "stsem" -> Stsem,
    "stype" -> Stype,
    "tensormap_space" -> TensormapSpace,
    "tlquery" -> Tlquery,
    "to_proxykind::from_proxykind_fabric" -> ToProxykindFromProxykindFabric,
    "tquery" -> Tquery,
    "type" -> Type,
    "type1" -> Type1,
    "type2" -> Type2,
    "typeAdd" -> TypeAdd,
    "typeAddAcc16" -> TypeAddAcc16,
    "typeAddAcc32" -> TypeAddAcc32,
    "typeArith" -> TypeArith,
    "typeBit" -> TypeBit,
    "typeMinMax" -> TypeMinMax,
    "vec" -> Vec,
    "vec_16_bit" -> Vec16Bit,
    "vec_32_bit" -> Vec32Bit
  )
  def apply(name: String): Option[ModifierSlot] = byName.get(name)
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum ModifierSlot(val name: NonEmpty[String]) derives CanEqual {
  case BoolOp extends ModifierSlot("BoolOp")
  case CmpOp extends ModifierSlot("CmpOp")
  case Abtype extends ModifierSlot("abtype")
  case AccPrec extends ModifierSlot("acc_prec")
  case Action extends ModifierSlot("action")
  case Alayout extends ModifierSlot("alayout")
  case Atype extends ModifierSlot("atype")
  case BitOp extends ModifierSlot("bitOp")
  case Blayout extends ModifierSlot("blayout")
  case Btype extends ModifierSlot("btype")
  case Clamp extends ModifierSlot("clamp")
  case Cmp extends ModifierSlot("cmp")
  case Comp extends ModifierSlot("comp")
  case ConvertType extends ModifierSlot("convertType")
  case Cop extends ModifierSlot("cop")
  case CtaGroup extends ModifierSlot("cta_group")
  case Ctype extends ModifierSlot("ctype")
  case Dim extends ModifierSlot("dim")
  case Dtype extends ModifierSlot("dtype")
  case F6x2type extends ModifierSlot("f6x2type")
  case F6x4type extends ModifierSlot("f6x4type")
  case F8f6f4type extends ModifierSlot("f8f6f4type")
  case F8type extends ModifierSlot("f8type")
  case F8x2type extends ModifierSlot("f8x2type")
  case F8x4type extends ModifierSlot("f8x4type")
  case Field1 extends ModifierSlot("field1")
  case Field2 extends ModifierSlot("field2")
  case Field3 extends ModifierSlot("field3")
  case Fp16x2 extends ModifierSlot("fp16x2")
  case Fp16x2type extends ModifierSlot("fp16x2type")
  case Frnd extends ModifierSlot("frnd")
  case Frnd2 extends ModifierSlot("frnd2")
  case Frnd3 extends ModifierSlot("frnd3")
  case Geom extends ModifierSlot("geom")
  case GetFirstCtaid extends ModifierSlot("get_first_ctaid")
  case HalfWordType extends ModifierSlot("half_word_type")
  case Irnd extends ModifierSlot("irnd")
  case Kind extends ModifierSlot("kind")
  case Layout extends ModifierSlot("layout")
  case Ldsem extends ModifierSlot("ldsem")
  case Level extends ModifierSlot("level")
  case Level1EvictionPriority extends ModifierSlot("level1::eviction_priority")
  case Level2EvictionPriority extends ModifierSlot("level2::eviction_priority")
  case LevelEvictionPriority extends ModifierSlot("level::eviction_priority")
  case LevelPrefetchSize extends ModifierSlot("level::prefetch_size")
  case LevelPrimaryPriority extends ModifierSlot("level::primary_priority")
  case LevelSecondaryPriority extends ModifierSlot("level::secondary_priority")
  case LoadMode extends ModifierSlot("load_mode")
  case Mode extends ModifierSlot("mode")
  case Num extends ModifierSlot("num")
  case Op extends ModifierSlot("op")
  case Op2 extends ModifierSlot("op2")
  case PackedType extends ModifierSlot("packed_type")
  case PhaseType extends ModifierSlot("phase_type")
  case Proxykind extends ModifierSlot("proxykind")
  case Query extends ModifierSlot("query")
  case RedOp extends ModifierSlot("redOp")
  case RedOpBit extends ModifierSlot("redOpBit")
  case RedOpMinMax extends ModifierSlot("redOpMinMax")
  case Redsem extends ModifierSlot("redsem")
  case Redtype extends ModifierSlot("redtype")
  case Rnd extends ModifierSlot("rnd")
  case Scale extends ModifierSlot("scale")
  case ScaleVecSize extends ModifierSlot("scale_vec_size")
  case ScaleVectorsize extends ModifierSlot("scale_vectorsize")
  case Scope extends ModifierSlot("scope")
  case Sem extends ModifierSlot("sem")
  case SemFabric extends ModifierSlot("sem_fabric")
  case Shape extends ModifierSlot("shape")
  case Shape1 extends ModifierSlot("shape1")
  case Shared extends ModifierSlot("shared")
  case Size extends ModifierSlot("size")
  case Space extends ModifierSlot("space")
  case Squery extends ModifierSlot("squery")
  case SrcFmt extends ModifierSlot("src_fmt")
  case Ss extends ModifierSlot("ss")
  case Stsem extends ModifierSlot("stsem")
  case Stype extends ModifierSlot("stype")
  case TensormapSpace extends ModifierSlot("tensormap_space")
  case Tlquery extends ModifierSlot("tlquery")
  case ToProxykindFromProxykindFabric extends ModifierSlot("to_proxykind::from_proxykind_fabric")
  case Tquery extends ModifierSlot("tquery")
  case Type extends ModifierSlot("type")
  case Type1 extends ModifierSlot("type1")
  case Type2 extends ModifierSlot("type2")
  case TypeAdd extends ModifierSlot("typeAdd")
  case TypeAddAcc16 extends ModifierSlot("typeAddAcc16")
  case TypeAddAcc32 extends ModifierSlot("typeAddAcc32")
  case TypeArith extends ModifierSlot("typeArith")
  case TypeBit extends ModifierSlot("typeBit")
  case TypeMinMax extends ModifierSlot("typeMinMax")
  case Vec extends ModifierSlot("vec")
  case Vec16Bit extends ModifierSlot("vec_16_bit")
  case Vec32Bit extends ModifierSlot("vec_32_bit")
}
