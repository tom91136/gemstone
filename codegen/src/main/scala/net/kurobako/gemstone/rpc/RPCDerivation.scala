package net.kurobako.gemstone.rpc

import scala.quoted.Expr
import scala.quoted.Quotes
import scala.quoted.Type
import scala.quoted.Varargs

object RPCDerivation {
  inline def schema[T]: RPCSchema           = ${ schemaImpl[T] }
  inline def methodNames[T]: Vector[String] = ${ methodNamesImpl[T] }

  private final case class DerivedParameter(
      name: String,
      wireType: WireType,
      direction: WireDirection,
      extent: WireExtent
  )

  private def schemaImpl[T: Type](using quotes: Quotes): Expr[RPCSchema] = {
    import quotes.reflect.*

    val interfaceType   = TypeRepr.of[T]
    val interfaceSymbol = interfaceType.typeSymbol
    if (!interfaceSymbol.flags.is(Flags.Trait))
      report.errorAndAbort(s"RPC interface must be a trait: ${interfaceSymbol.fullName}")

    val methods        = interfaceSymbol.declaredMethods.filter(_.flags.is(Flags.Deferred)).sortBy(_.name)
    val duplicateNames =
      methods.groupBy(_.name).collect { case (name, overloads) if overloads.size > 1 => name }.toVector
    if (duplicateNames.nonEmpty)
      report.errorAndAbort(s"RPC methods may not be overloaded: ${duplicateNames.sorted.mkString(", ")}")

    val derived = methods.map { method =>
      if (!method.name.matches("(?:cu|cuda|__cuda)[A-Za-z0-9_]*"))
        abortAt(method, s"CUDA RPC method name must be its exact C symbol: ${method.name}")
      val (rawParameters, resultType) = methodShape(interfaceType.memberType(method), method)
      requireCUDAStatus(resultType, method)
      val parameters = rawParameters.map { case (name, parameterType) => deriveParameter(name, parameterType, method) }
      val messageId  = hashName(method.name)
      (method, parameters, messageId)
    }

    val collisions = derived.groupBy(_._3).collect {
      case (id, entries) if entries.size > 1 => s"${hex(id)}: ${entries.map(_._1.name).mkString(", ")}"
    }.toVector
    if (collisions.nonEmpty)
      report.errorAndAbort(s"derived CUDA RPC message ID collision: ${collisions.sorted.mkString("; ")}")

    val canonical = derived.map { case (method, parameters, id) =>
      val parameterText = parameters.map { parameter =>
        s"${parameter.name}:${parameter.wireType.protocolName}:${parameter.direction}:${extentName(parameter.extent)}"
      }.mkString(",")
      s"${method.name}|${hex(id)}|$parameterText|CUDAStatus"
    }
    val fingerprint = sha256(canonical.mkString("\n").getBytes(java.nio.charset.StandardCharsets.UTF_8))
      .map(byte => f"${byte & 0xff}%02x")
      .mkString

    val methodExpressions = derived.map { case (method, parameters, (high, low)) =>
      val parameterExpressions = parameters.map { parameter =>
        '{
          RPCParameter(
            ${ Expr(parameter.name) },
            ${ wireTypeExpression(parameter.wireType) },
            ${ directionExpression(parameter.direction) },
            ${ extentExpression(parameter.extent) }
          )
        }
      }
      '{
        RPCMethod(
          ${ Expr(method.name) },
          MessageId(${ Expr(high) }, ${ Expr(low) }),
          Vector(${ Varargs(parameterExpressions) }*),
          WireType.CUDAStatus
        )
      }
    }

    '{
      RPCSchema(
        ${ Expr(interfaceSymbol.fullName) },
        ${ Expr(fingerprint) },
        Vector(${ Varargs(methodExpressions) }*)
      )
    }
  }

  private def methodNamesImpl[T: Type](using quotes: Quotes): Expr[Vector[String]] = {
    import quotes.reflect.*
    val interfaceSymbol = TypeRepr.of[T].typeSymbol
    if (!interfaceSymbol.flags.is(Flags.Trait))
      report.errorAndAbort(s"API surface must be a trait: ${interfaceSymbol.fullName}")
    val names = interfaceSymbol.declaredMethods
      .filterNot(method => method.flags.is(Flags.Synthetic) || method.flags.is(Flags.Private))
      .map(_.name)
      .filter(_.matches("(?:cu|cuda|__cuda)[A-Za-z0-9_]*"))
      .distinct
      .sorted
      .map(Expr(_))
    '{ Vector(${ Varargs(names) }*) }
  }

  private def methodShape(using
      quotes: Quotes
  )(
      methodType: quotes.reflect.TypeRepr,
      method: quotes.reflect.Symbol
  ): (Vector[(String, quotes.reflect.TypeRepr)], quotes.reflect.TypeRepr) = {
    import quotes.reflect.*
    methodType match {
      case _: PolyType       => abortAt(method, s"RPC methods may not have type parameters: ${method.name}")
      case value: MethodType =>
        val parameters = value.paramNames.zip(value.paramTypes).toVector
        value.resType match {
          case _: MethodType | _: PolyType =>
            abortAt(method, s"RPC methods must have one parameter list: ${method.name}")
          case result => parameters -> result
        }
      case other => Vector.empty -> other
    }
  }

  private def requireCUDAStatus(using
      quotes: Quotes
  )(
      resultType: quotes.reflect.TypeRepr,
      method: quotes.reflect.Symbol
  ): Unit = {
    import net.kurobako.gemstone.cuda.CUDAStatus
    import quotes.reflect.*
    if (!(resultType.dealias =:= TypeRepr.of[CUDAStatus]))
      abortAt(method, s"RPC method must return CUDAStatus: ${method.name}")
  }

  private def deriveParameter(using
      quotes: Quotes
  )(
      name: String,
      rawType: quotes.reflect.TypeRepr,
      method: quotes.reflect.Symbol
  ): DerivedParameter = {
    import net.kurobako.gemstone.abi
    import quotes.reflect.*

    rawType.dealias match {
      case AppliedType(pointer, List(element, mode, extent))
          if pointer.typeSymbol.fullName == "net.kurobako.gemstone.abi.Pointer" =>
        val direction =
          if (mode =:= TypeRepr.of[abi.Read]) WireDirection.In
          else if (mode =:= TypeRepr.of[abi.Write]) WireDirection.Out
          else if (mode =:= TypeRepr.of[abi.ReadWrite]) WireDirection.InOut
          else abortAt(method, s"unsupported pointer access ${mode.show} in ${method.name}.$name")
        val wire = if (element.dealias =:= TypeRepr.of[Byte]) WireType.ByteBuffer else wireType(element, method)
        DerivedParameter(name, wire, direction, deriveExtent(extent, method))
      case value => DerivedParameter(name, wireType(value, method), WireDirection.In, WireExtent.Value)
    }
  }

  private def deriveExtent(using
      quotes: Quotes
  )(
      raw: quotes.reflect.TypeRepr,
      method: quotes.reflect.Symbol
  ): WireExtent = {
    import net.kurobako.gemstone.abi
    import quotes.reflect.*
    val value = raw.dealias
    if (value =:= TypeRepr.of[abi.One]) WireExtent.One
    else if (value =:= TypeRepr.of[abi.Opaque]) WireExtent.Opaque
    else
      value match {
        case AppliedType(constructor, List(ConstantType(StringConstant(parameter))))
            if constructor.typeSymbol.fullName == "net.kurobako.gemstone.abi.CountedBy" =>
          WireExtent.CountedBy(parameter)
        case AppliedType(constructor, List(ConstantType(IntConstant(count))))
            if constructor.typeSymbol.fullName == "net.kurobako.gemstone.abi.Fixed" =>
          WireExtent.Fixed(count)
        case _ => abortAt(method, s"unsupported pointer extent ${value.show} in ${method.name}")
      }
  }

  private def wireType(using
      quotes: Quotes
  )(
      raw: quotes.reflect.TypeRepr,
      method: quotes.reflect.Symbol
  ): WireType = {
    import net.kurobako.gemstone.abi.OpaquePointer
    import net.kurobako.gemstone.cuda
    import quotes.reflect.*
    val value = raw.dealias
    if (value =:= TypeRepr.of[Unit]) WireType.UnitValue
    else if (value =:= TypeRepr.of[Int]) WireType.Int32
    else if (value =:= TypeRepr.of[cuda.UInt8]) WireType.UInt8
    else if (value =:= TypeRepr.of[cuda.UInt32]) WireType.UInt32
    else if (value =:= TypeRepr.of[cuda.SizeT]) WireType.SizeT
    else if (value =:= TypeRepr.of[cuda.Device]) WireType.Device
    else if (value =:= TypeRepr.of[cuda.DevicePtr]) WireType.DevicePtr
    else if (value =:= TypeRepr.of[cuda.Context]) WireType.Context
    else if (value =:= TypeRepr.of[cuda.CUDAModule]) WireType.CUDAModule
    else if (value =:= TypeRepr.of[cuda.CUDAFunction]) WireType.CUDAFunction
    else if (value =:= TypeRepr.of[cuda.ModuleImage]) WireType.ModuleImage
    else if (value =:= TypeRepr.of[cuda.FunctionName]) WireType.FunctionName
    else if (value =:= TypeRepr.of[cuda.KernelFunction]) WireType.KernelFunction
    else if (value =:= TypeRepr.of[cuda.Stream]) WireType.Stream
    else if (value =:= TypeRepr.of[cuda.KernelParameters]) WireType.KernelParameters
    else if (value =:= TypeRepr.of[cuda.CUDAStatus]) WireType.CUDAStatus
    else
      value match {
        case AppliedType(option, List(element))
            if option.typeSymbol.fullName == "scala.Option" && (
              element.dealias =:= TypeRepr.of[OpaquePointer] || (element.dealias match {
                case AppliedType(pointer, List(nested, _, _)) =>
                  pointer.typeSymbol.fullName == "net.kurobako.gemstone.abi.Pointer" &&
                  nested.dealias =:= TypeRepr.of[OpaquePointer]
                case _ => false
              })
            ) =>
          WireType.NullableOpaquePointer
        case _ => abortAt(method, s"unsupported RPC wire type ${value.show} in ${method.name}")
      }
  }

  private def wireTypeExpression(value: WireType)(using Quotes): Expr[WireType] = value match {
    case WireType.UnitValue             => '{ WireType.UnitValue }
    case WireType.Int32                 => '{ WireType.Int32 }
    case WireType.UInt8                 => '{ WireType.UInt8 }
    case WireType.UInt32                => '{ WireType.UInt32 }
    case WireType.SizeT                 => '{ WireType.SizeT }
    case WireType.Device                => '{ WireType.Device }
    case WireType.DevicePtr             => '{ WireType.DevicePtr }
    case WireType.Context               => '{ WireType.Context }
    case WireType.CUDAModule            => '{ WireType.CUDAModule }
    case WireType.CUDAFunction          => '{ WireType.CUDAFunction }
    case WireType.HostInput             => '{ WireType.HostInput }
    case WireType.HostOutput            => '{ WireType.HostOutput }
    case WireType.ModuleImage           => '{ WireType.ModuleImage }
    case WireType.FunctionName          => '{ WireType.FunctionName }
    case WireType.KernelFunction        => '{ WireType.KernelFunction }
    case WireType.KernelLaunch          => '{ WireType.KernelLaunch }
    case WireType.Stream                => '{ WireType.Stream }
    case WireType.ByteBuffer            => '{ WireType.ByteBuffer }
    case WireType.KernelParameters      => '{ WireType.KernelParameters }
    case WireType.NullableOpaquePointer => '{ WireType.NullableOpaquePointer }
    case WireType.CUDAStatus            => '{ WireType.CUDAStatus }
  }

  private def directionExpression(value: WireDirection)(using Quotes): Expr[WireDirection] = value match {
    case WireDirection.In    => '{ WireDirection.In }
    case WireDirection.Out   => '{ WireDirection.Out }
    case WireDirection.InOut => '{ WireDirection.InOut }
  }

  private def extentExpression(value: WireExtent)(using Quotes): Expr[WireExtent] = value match {
    case WireExtent.Value           => '{ WireExtent.Value }
    case WireExtent.One             => '{ WireExtent.One }
    case WireExtent.Opaque          => '{ WireExtent.Opaque }
    case WireExtent.CountedBy(name) => '{ WireExtent.CountedBy(${ Expr(name) }) }
    case WireExtent.Fixed(count)    => '{ WireExtent.Fixed(${ Expr(count) }) }
  }

  private def extentName(value: WireExtent): String = value match {
    case WireExtent.Value           => "Value"
    case WireExtent.One             => "One"
    case WireExtent.Opaque          => "Opaque"
    case WireExtent.CountedBy(name) => s"CountedBy($name)"
    case WireExtent.Fixed(count)    => s"Fixed($count)"
  }

  private def hashName(name: String): (Long, Long) = {
    val bytes                     = sha256(name.getBytes(java.nio.charset.StandardCharsets.UTF_8))
    def longAt(offset: Int): Long = (0 until 8).foldLeft(0L) { (value, index) =>
      (value << 8) | (bytes(offset + index) & 0xffL)
    }
    longAt(0) -> longAt(8)
  }

  private def sha256(bytes: Array[Byte]): Array[Byte] =
    java.security.MessageDigest.getInstance("SHA-256").digest(bytes)

  private def hex(id: (Long, Long)): String = f"${id._1}%016x${id._2}%016x"

  private def abortAt(using quotes: Quotes)(method: quotes.reflect.Symbol, message: String): Nothing = {
    import quotes.reflect.*
    method.pos match {
      case Some(position) => report.errorAndAbort(message, position)
      case None           => report.errorAndAbort(message)
    }
  }
}
