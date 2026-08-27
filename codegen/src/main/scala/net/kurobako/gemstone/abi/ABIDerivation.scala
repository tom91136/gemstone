package net.kurobako.gemstone.abi

import scala.quoted.Expr
import scala.quoted.Quotes
import scala.quoted.Type
import scala.quoted.Varargs

object ABIDerivation {
  inline def methods[T]: Vector[ABIMethod]              = ${ methodsImpl[T] }
  inline def cFunctions[T]: Vector[CFunction]           = ${ cFunctionsImpl[T] }
  inline def nativeDelegates[T]: Vector[ABIDeclaration] = ${ nativeDelegatesImpl[T] }

  private def cFunctionsImpl[T: Type](using quotes: Quotes): Expr[Vector[CFunction]] = {
    import quotes.reflect.*
    val interface = TypeRepr.of[T].typeSymbol
    if (!interface.flags.is(Flags.Trait)) report.errorAndAbort(s"ABI interface must be a trait: ${interface.fullName}")
    val methods = publicMethods(interface).filterNot(_.name.startsWith("__cudaGemstone"))
    rejectOverloads(methods)
    val expressions = methods.map { method =>
      val signature = deriveCFunctionSignature(method)
      '{ CFunction(${ Expr(method.name) }, ${ Expr(signature) }) }
    }
    '{ Vector(${ Varargs(expressions) }*) }
  }

  private def deriveCFunctionSignature(using quotes: Quotes)(method: quotes.reflect.Symbol): String = {
    import quotes.reflect.*
    val definition = method.tree match {
      case value: DefDef => value
      case _             => abortAt(method, s"ABI method ${method.name} has no retained definition tree")
    }
    val parameters = method.paramSymss.flatten.map { parameter =>
      val parameterType = parameter.tree match {
        case value: ValDef => value.tpt.tpe
        case _             => abortAt(method, s"ABI parameter ${parameter.name} has no retained type tree")
      }
      deriveCType(parameterType, method.name, parameter.name, result = false)
    }
    val resultType = deriveCType(definition.returnTpt.tpe, method.name, "<result>", result = true)
    val separator  = if (resultType.endsWith("*")) "" else " "
    s"$resultType$separator(${if (parameters.isEmpty) "void" else parameters.mkString(", ")})"
  }

  private def deriveCType(using
      quotes: Quotes
  )(
      original: quotes.reflect.TypeRepr,
      methodName: String,
      parameterName: String,
      result: Boolean
  ): String = {
    import quotes.reflect.*

    def literalArgument(tpe: TypeRepr): Option[String] = tpe.dealias match {
      case ConstantType(StringConstant(value)) => Some(value)
      case _                                   => None
    }

    def pointer(base: String, readOnly: Boolean): String =
      if (readOnly) {
        if (base.endsWith("*")) s"const $base*" else s"const $base *"
      } else if (base.endsWith("*")) s"$base*"
      else s"$base *"

    def loop(tpe0: TypeRepr): String = {
      val tpe      = tpe0.dealias.simplified
      val fullName = tpe0.typeSymbol.fullName
      val cudaType = fullName.startsWith("net.kurobako.gemstone.cuda.")
      (cudaType, tpe0.typeSymbol.name) match {
        case (true, "CUDAStatus") =>
          if (methodName.startsWith("cuda")) "cudaError_t" else "CUresult"
        case (true, "UInt8")            => "unsigned char"
        case (true, "UInt32")           => "unsigned int"
        case (true, "SizeT")            => "size_t"
        case (true, "Device")           => "CUdevice"
        case (true, "DevicePtr")        => if (methodName.startsWith("cuda")) "void *" else "CUdeviceptr"
        case (true, "Context")          => "CUcontext"
        case (true, "CUDAModule")       => "CUmodule"
        case (true, "CUDAFunction")     => "CUfunction"
        case (true, "Stream")           => "CUstream"
        case (true, "ModuleImage")      => "const void *"
        case (true, "FunctionName")     => "const char *"
        case (true, "KernelFunction")   => "CUfunction"
        case (true, "KernelParameters") => "void **"
        case _                          =>
          tpe match {
            case AppliedType(constructor, List(name))
                if constructor.typeSymbol.fullName == "net.kurobako.gemstone.abi.CValue" =>
              literalArgument(name).getOrElse(report.errorAndAbort(s"CValue in $methodName has no literal C name"))
            case AppliedType(constructor, List(signature))
                if constructor.typeSymbol.fullName == "net.kurobako.gemstone.abi.Callback" =>
              literalArgument(
                signature
              ).getOrElse(report.errorAndAbort(s"Callback in $methodName has no literal C signature"))
            case AppliedType(constructor, List(element, count))
                if constructor.typeSymbol.fullName == "net.kurobako.gemstone.abi.CArray" =>
              val size = count.dealias match {
                case ConstantType(IntConstant(value)) => value
                case _ => report.errorAndAbort(s"CArray in $methodName has no literal size")
              }
              s"${loop(element)}[$size]"
            case AppliedType(constructor, List(element, mode, extent))
                if constructor.typeSymbol.fullName == "net.kurobako.gemstone.abi.Pointer" =>
              val countedBytes =
                element.dealias =:= TypeRepr.of[Byte] && extent.dealias.typeSymbol.fullName ==
                  "net.kurobako.gemstone.abi.CountedBy"
              pointer(if (countedBytes) "void" else loop(element), mode.dealias =:= TypeRepr.of[Read])
            case AppliedType(constructor, List(value))
                if constructor.typeSymbol.fullName == "scala.Option" => loop(value)
            case _ if tpe =:= TypeRepr.of[Unit]                                    => "void"
            case _ if tpe =:= TypeRepr.of[Boolean]                                 => "bool"
            case _ if tpe =:= TypeRepr.of[Byte]                                    => "char"
            case _ if tpe =:= TypeRepr.of[Short]                                   => "short"
            case _ if tpe =:= TypeRepr.of[Int]                                     => "int"
            case _ if tpe =:= TypeRepr.of[Long]                                    => "long long"
            case _ if tpe =:= TypeRepr.of[Float]                                   => "float"
            case _ if tpe =:= TypeRepr.of[Double]                                  => "double"
            case _ if tpe.typeSymbol.fullName == "net.kurobako.gemstone.abi.CVoid" => "void"
            case _                                                                 =>
              report.errorAndAbort(
                s"cannot derive C type for $methodName/$parameterName${if (result) " result" else ""}: ${tpe0.show} (${tpe.show})"
              )
          }
      }
    }

    loop(original)
  }

  private def nativeDelegatesImpl[T: Type](using quotes: Quotes): Expr[Vector[ABIDeclaration]] = {
    import quotes.reflect.*
    val interface = TypeRepr.of[T].typeSymbol
    if (!interface.flags.is(Flags.Trait)) report.errorAndAbort(s"ABI interface must be a trait: ${interface.fullName}")
    val methods = publicMethods(interface).filter(method => nativeDelegate(method).nonEmpty)
    rejectOverloads(methods)
    val expressions = methods.map { method =>
      val signature      = deriveCFunctionSignature(method)
      val parameterNames = method.paramSymss.flatten.map(parameter => Expr(parameter.name))
      val target         = nativeDelegate(method).get
      '{
        ABIDeclaration(
          ${ Expr(method.name) },
          ${ Expr(signature) },
          Vector(${ Varargs(parameterNames) }*),
          MethodImplementation.NativeDelegate(${ Expr(target) })
        )
      }
    }
    '{ Vector(${ Varargs(expressions) }*) }
  }

  private def methodsImpl[T: Type](using quotes: Quotes): Expr[Vector[ABIMethod]] = {
    import quotes.reflect.*

    val interface = TypeRepr.of[T].typeSymbol
    if (!interface.flags.is(Flags.Trait)) report.errorAndAbort(s"ABI interface must be a trait: ${interface.fullName}")

    val methods = publicMethods(interface)
    rejectOverloads(methods)

    val expressions = methods.map { method =>
      if (!method.name.matches("[A-Za-z_][A-Za-z0-9_]*"))
        abortAt(method, s"ABI method name must be its exact C identifier: ${method.name}")

      val implementation = implementationExpression(method)
      '{ ABIMethod(${ Expr(method.name) }, $implementation) }
    }
    '{ Vector(${ Varargs(expressions) }*) }
  }

  private def implementationExpression(using
      quotes: Quotes
  )(
      method: quotes.reflect.Symbol
  ): Expr[MethodImplementation] =
    if (method.flags.is(quotes.reflect.Flags.Deferred)) '{ MethodImplementation.Remote }
    else
      nativeDelegate(method).fold[Expr[MethodImplementation]]('{ MethodImplementation.Unsupported }) { target =>
        '{ MethodImplementation.NativeDelegate(${ Expr(target) }) }
      }

  private def publicMethods(using quotes: Quotes)(interface: quotes.reflect.Symbol): Vector[quotes.reflect.Symbol] = {
    import quotes.reflect.*
    interface.declaredMethods
      .filterNot(method => method.flags.is(Flags.Synthetic) || method.flags.is(Flags.Private))
      .filterNot(_.name == "<init>")
      .sortBy(_.name)
      .toVector
  }

  private def rejectOverloads(using quotes: Quotes)(methods: Vector[quotes.reflect.Symbol]): Unit = {
    import quotes.reflect.*
    val duplicateNames = methods.groupBy(_.name).collect { case (name, entries) if entries.size > 1 => name }.toVector
    if (duplicateNames.nonEmpty)
      report.errorAndAbort(s"ABI methods may not be overloaded: ${duplicateNames.sorted.mkString(", ")}")
  }

  private def nativeDelegate(using quotes: Quotes)(method: quotes.reflect.Symbol): Option[String] =
    retainedDelegateTarget(method).map { target =>
      if (!target.matches("[A-Za-z_][A-Za-z0-9_]*"))
        abortAt(method, s"native delegate target is not a C identifier: $target")
      target
    }

  private def retainedDelegateTarget(using quotes: Quotes)(method: quotes.reflect.Symbol): Option[String] = {
    import quotes.reflect.*

    def singletonString(tpe: TypeRepr): Option[String] = tpe.dealias match {
      case ConstantType(StringConstant(value)) => Some(value)
      case _                                   => None
    }

    def targetOf(tree: Tree): Option[String] = tree match {
      case TypeApply(function, List(target))
          if function.symbol.fullName == "net.kurobako.gemstone.abi.ABI$package$.delegate" =>
        singletonString(target.tpe)
          .orElse(abortAt(method, s"delegate target for ${method.name} must be a literal string"))
      case _ => None
    }

    def repeatedArguments(arguments: List[Term]): List[Term] = arguments match {
      case List(Typed(Repeated(values, _), _)) => values
      case List(Repeated(values, _))           => values
      case values                              => values
    }

    def argumentSymbol(argument: Term): Symbol = argument match {
      case identifier: Ident         => identifier.symbol
      case Typed(expression, _)      => argumentSymbol(expression)
      case Inlined(_, _, expression) => argumentSymbol(expression)
      case _                         => Symbol.noSymbol
    }

    def validateForwarding(target: String, arguments: List[Term]): String = {
      val expected = method.paramSymss.flatten
      val actual   = repeatedArguments(arguments).map(argumentSymbol)
      if (actual != expected)
        abortAt(
          method,
          s"native delegate ${method.name} must forward every parameter exactly once and in declaration order"
        )
      target
    }

    def loop(tree: Tree): Option[String] = tree match {
      case Apply(function, arguments) =>
        targetOf(function).map(target => validateForwarding(target, arguments)).orElse(loop(function))
      case Inlined(Some(call), _, expansion) => loop(call).orElse(loop(expansion))
      case Inlined(None, _, expansion)       => loop(expansion)
      case Block(_, expression)              => loop(expression)
      case Typed(expression, _)              => loop(expression)
      case _                                 => None
    }

    method.tree match {
      case DefDef(_, _, _, Some(body)) => loop(body)
      case _                           => None
    }
  }

  private def abortAt(using quotes: Quotes)(method: quotes.reflect.Symbol, message: String): Nothing = {
    import quotes.reflect.*
    method.pos match {
      case Some(position) => report.errorAndAbort(message, position)
      case None           => report.errorAndAbort(message)
    }
  }
}
