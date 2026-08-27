package net.kurobako.gemstone.rpc

import scala.quoted.Expr
import scala.quoted.Quotes
import scala.quoted.Type
import scala.quoted.Varargs

object RPCDerivation {
  inline def schema[T]: RPCSchema           = ${ schemaImpl[T] }
  inline def methodNames[T]: Vector[String] = ${ methodNamesImpl[T] }

  private def schemaImpl[T: Type](using quotes: Quotes): Expr[RPCSchema] = {
    import quotes.reflect.*

    val interfaceType   = TypeRepr.of[T]
    val interfaceSymbol = interfaceType.typeSymbol
    if (!interfaceSymbol.flags.is(Flags.Trait)) {
      report.errorAndAbort(s"RPC interface must be a trait: ${interfaceSymbol.fullName}")
    }

    val methods = interfaceSymbol.declaredMethods
      .filter(method => method.flags.is(Flags.Deferred))
      .sortBy(_.name)

    val duplicateNames =
      methods.groupBy(_.name).collect { case (name, overloads) if overloads.size > 1 => name }.toVector
    if (duplicateNames.nonEmpty) {
      report.errorAndAbort(s"RPC methods may not be overloaded: ${duplicateNames.sorted.mkString(", ")}")
    }

    val derived = methods.map { method =>
      if (!method.name.matches("(?:cu|cuda|__cuda)[A-Za-z0-9_]*")) {
        abortAt(method, s"CUDA RPC method name must be its exact C symbol: ${method.name}")
      }

      val (rawParameters, resultType) = methodShape(interfaceType.memberType(method), method)
      val parameters                  = rawParameters.map { case (name, parameterType) =>
        name -> wireType(parameterType, method)
      }
      val result    = cudaResultValue(resultType, method)
      val messageId = hashName(method.name)
      (method, parameters, result, messageId)
    }

    val collisions = derived.groupBy(_._4).collect {
      case (id, entries) if entries.size > 1 => s"${hex(id)}: ${entries.map(_._1.name).mkString(", ")}"
    }.toVector
    if (collisions.nonEmpty) {
      report.errorAndAbort(s"derived CUDA RPC message ID collision: ${collisions.sorted.mkString("; ")}")
    }

    val canonical = derived.map { case (method, parameters, result, id) =>
      s"${method.name}|${hex(id)}|${parameters.map { case (name, wire) => s"$name:${wire.protocolName}" }.mkString(",")}|${result.protocolName}"
    }
    val fingerprint = sha256(canonical.mkString("\n").getBytes(java.nio.charset.StandardCharsets.UTF_8))
      .map(byte => f"${byte & 0xff}%02x")
      .mkString

    val methodExpressions = derived.map { case (method, parameters, result, (high, low)) =>
      val parameterExpressions = parameters.map { case (name, wireType) =>
        '{ RPCParameter(${ Expr(name) }, ${ wireTypeExpression(wireType) }) }
      }
      '{
        RPCMethod(
          ${ Expr(method.name) },
          MessageId(${ Expr(high) }, ${ Expr(low) }),
          Vector(${ Varargs(parameterExpressions) }*),
          ${ wireTypeExpression(result) }
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
    if (!interfaceSymbol.flags.is(Flags.Trait)) {
      report.errorAndAbort(s"API surface must be a trait: ${interfaceSymbol.fullName}")
    }
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
      case _: PolyType =>
        abortAt(method, s"RPC methods may not have type parameters: ${method.name}")
      case methodType: MethodType =>
        val parameters = methodType.paramNames.zip(methodType.paramTypes).toVector
        methodType.resType match {
          case _: MethodType | _: PolyType =>
            abortAt(method, s"RPC methods must have one parameter list: ${method.name}")
          case result => parameters -> result
        }
      case other => Vector.empty -> other
    }
  }

  private def cudaResultValue(using
      quotes: Quotes
  )(
      resultType: quotes.reflect.TypeRepr,
      method: quotes.reflect.Symbol
  ): WireType = {
    import quotes.reflect.*
    resultType match {
      case AppliedType(constructor, List(value))
          if constructor.typeSymbol.fullName == "net.kurobako.gemstone.cuda.CUDAResult" => wireType(value, method)
      case _ =>
        abortAt(method, s"RPC method must return CUDAResult[T]: ${method.name}")
    }
  }

  private def wireType(using
      quotes: Quotes
  )(
      value: quotes.reflect.TypeRepr,
      method: quotes.reflect.Symbol
  ): WireType = {
    import net.kurobako.gemstone.cuda
    import quotes.reflect.*

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
    else if (value =:= TypeRepr.of[cuda.HostInput]) WireType.HostInput
    else if (value =:= TypeRepr.of[cuda.HostOutput]) WireType.HostOutput
    else if (value =:= TypeRepr.of[cuda.ModuleImage]) WireType.ModuleImage
    else if (value =:= TypeRepr.of[cuda.FunctionName]) WireType.FunctionName
    else if (value =:= TypeRepr.of[cuda.KernelFunction]) WireType.KernelFunction
    else if (value =:= TypeRepr.of[cuda.KernelLaunch]) WireType.KernelLaunch
    else abortAt(method, s"unsupported RPC wire type ${value.show} in ${method.name}")
  }

  private def wireTypeExpression(wireType: WireType)(using Quotes): Expr[WireType] = wireType match {
    case WireType.UnitValue      => '{ WireType.UnitValue }
    case WireType.Int32          => '{ WireType.Int32 }
    case WireType.UInt8          => '{ WireType.UInt8 }
    case WireType.UInt32         => '{ WireType.UInt32 }
    case WireType.SizeT          => '{ WireType.SizeT }
    case WireType.Device         => '{ WireType.Device }
    case WireType.DevicePtr      => '{ WireType.DevicePtr }
    case WireType.Context        => '{ WireType.Context }
    case WireType.CUDAModule     => '{ WireType.CUDAModule }
    case WireType.CUDAFunction   => '{ WireType.CUDAFunction }
    case WireType.HostInput      => '{ WireType.HostInput }
    case WireType.HostOutput     => '{ WireType.HostOutput }
    case WireType.ModuleImage    => '{ WireType.ModuleImage }
    case WireType.FunctionName   => '{ WireType.FunctionName }
    case WireType.KernelFunction => '{ WireType.KernelFunction }
    case WireType.KernelLaunch   => '{ WireType.KernelLaunch }
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
