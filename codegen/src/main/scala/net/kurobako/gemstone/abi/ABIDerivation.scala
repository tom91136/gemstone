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
    val rawParameters = method.paramSymss.flatten.map { parameter =>
      val parameterType = parameter.tree match {
        case value: ValDef => value.tpt.tpe
        case _             => abortAt(method, s"ABI parameter ${parameter.name} has no retained type tree")
      }
      parameter -> parameterType
    }
    validateParameterExtents(rawParameters, method)
    val parameters = rawParameters.map { case (parameter, parameterType) =>
      deriveCType(parameterType, method.name, parameter.name, result = false)
    }
    val resultType = deriveCType(definition.returnTpt.tpe, method.name, "<result>", result = true)
    val separator  = if (resultType.endsWith("*")) "" else " "
    s"$resultType$separator(${if (parameters.isEmpty) "void" else parameters.mkString(", ")})"
  }

  private def validateParameterExtents(using
      quotes: Quotes
  )(
      parameters: List[(quotes.reflect.Symbol, quotes.reflect.TypeRepr)],
      method: quotes.reflect.Symbol
  ): Unit = {
    import net.kurobako.gemstone.cuda
    import quotes.reflect.*

    def isIntegral(tpe: TypeRepr): Boolean = {
      val value = tpe.dealias.simplified
      value =:= TypeRepr.of[Byte] || value =:= TypeRepr.of[Short] || value =:= TypeRepr.of[Int] ||
      value =:= TypeRepr.of[Long] || value =:= TypeRepr.of[cuda.UInt8] || value =:= TypeRepr.of[cuda.UInt32] ||
      value =:= TypeRepr.of[cuda.SizeT]
    }

    parameters.foreach { case (parameter, rawType) =>
      rawType.dealias.simplified match {
        case AppliedType(pointer, List(_, _, extent))
            if pointer.typeSymbol.fullName == "net.kurobako.gemstone.abi.Pointer" =>
          extent.dealias.simplified match {
            case AppliedType(constructor, List(ConstantType(StringConstant(countName))))
                if constructor.typeSymbol.fullName == "net.kurobako.gemstone.abi.CountedBy" =>
              parameters.find(_._1.name == countName) match {
                case None =>
                  abortAt(
                    method,
                    s"${method.name}.${parameter.name} is CountedBy[$countName], but that parameter is absent"
                  )
                case Some((_, countType)) if !isIntegral(countType) =>
                  abortAt(
                    method,
                    s"${method.name} count parameter $countName must have an integral ABI type; found ${countType.show} (${countType.typeSymbol.fullName})"
                  )
                case Some(_) => ()
              }
            case AppliedType(constructor, List(ConstantType(IntConstant(count))))
                if constructor.typeSymbol.fullName == "net.kurobako.gemstone.abi.Fixed" && count <= 0 =>
              abortAt(method, s"Fixed extent in ${method.name}.${parameter.name} must be positive, found $count")
            case _ => ()
          }
        case _ => ()
      }
    }
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

    def requireCTypeSpelling(value: String, description: String): String = {
      val cType = "[A-Za-z_][A-Za-z0-9_ *]*"
      if (!value.matches(cType))
        report.errorAndAbort(s"$description in $methodName has invalid C type spelling: $value")
      value
    }

    def requireCIdentifier(value: String, description: String): String = {
      if (!value.matches("[A-Za-z_][A-Za-z0-9_]*"))
        report.errorAndAbort(s"$description in $methodName is not a C identifier: $value")
      value
    }

    def pointerShaped(tpe: TypeRepr): Boolean = tpe.dealias.simplified match {
      case AppliedType(constructor, _)
          if Set(
            "net.kurobako.gemstone.abi.Pointer",
            "net.kurobako.gemstone.abi.Callback",
            "net.kurobako.gemstone.abi.CallbackTypedef"
          )(constructor.typeSymbol.fullName) =>
        true
      case value if value.typeSymbol.fullName == "net.kurobako.gemstone.abi.CallbackData" => true
      case _                                                                              => false
    }

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
              requireCTypeSpelling(
                literalArgument(name).getOrElse(report.errorAndAbort(s"CValue in $methodName has no literal C name")),
                "CValue"
              )
            case AppliedType(constructor, List(arguments, callbackResult))
                if constructor.typeSymbol.fullName == "net.kurobako.gemstone.abi.Callback" =>
              val parameterTypes     = tupleElements(arguments).map(loop)
              val callbackResultType = loop(callbackResult)
              val separator          = if (callbackResultType.endsWith("*")) "" else " "
              s"$callbackResultType$separator(*)(${
                  if (parameterTypes.isEmpty) "void" else parameterTypes.mkString(", ")
                })"
            case AppliedType(constructor, List(name, _, _))
                if constructor.typeSymbol.fullName == "net.kurobako.gemstone.abi.CallbackTypedef" =>
              requireCIdentifier(
                literalArgument(name)
                  .getOrElse(report.errorAndAbort(s"CallbackTypedef in $methodName has no literal C name")),
                "CallbackTypedef"
              )
            case _ if tpe.typeSymbol.fullName == "net.kurobako.gemstone.abi.CallbackData" => "void *"
            case AppliedType(constructor, List(element, count))
                if constructor.typeSymbol.fullName == "net.kurobako.gemstone.abi.CArray" =>
              val size = count.dealias match {
                case ConstantType(IntConstant(value)) => value
                case _ => report.errorAndAbort(s"CArray in $methodName has no literal size")
              }
              if (size <= 0) report.errorAndAbort(s"CArray in $methodName must have a positive size, found $size")
              s"${loop(element)}[$size]"
            case AppliedType(constructor, List(element, mode, extent))
                if constructor.typeSymbol.fullName == "net.kurobako.gemstone.abi.Pointer" =>
              val countedBytes =
                element.dealias =:= TypeRepr.of[Byte] && extent.dealias.typeSymbol.fullName ==
                  "net.kurobako.gemstone.abi.CountedBy"
              pointer(if (countedBytes) "void" else loop(element), mode.dealias =:= TypeRepr.of[Read])
            case AppliedType(constructor, List(value))
                if constructor.typeSymbol.fullName == "net.kurobako.gemstone.abi.Nullable" =>
              if (!pointerShaped(value))
                report.errorAndAbort(
                  s"Nullable in $methodName/$parameterName requires a pointer-shaped ABI value, found ${value.show}"
                )
              loop(value)
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

    def tupleElements(tpe0: TypeRepr): List[TypeRepr] = {
      val tpe = tpe0.dealias.simplified
      tpe match {
        case value if value =:= TypeRepr.of[EmptyTuple] => Nil
        case AppliedType(constructor, List(head, tail))
            if constructor.typeSymbol.fullName == "scala.*:" =>
          head :: tupleElements(tail)
        case AppliedType(constructor, elements)
            if constructor.typeSymbol.fullName.matches("scala\\.Tuple[1-9][0-9]*") =>
          elements
        case _ =>
          report.errorAndAbort(
            s"Callback arguments in $methodName/$parameterName must be a concrete Tuple, found ${tpe0.show}"
          )
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
    else nativeDelegate(method) match {
      case Some(target)                        => '{ MethodImplementation.NativeDelegate(${ Expr(target) }) }
      case None if retainedUnsupported(method) => '{ MethodImplementation.Unsupported }
      case None                                =>
        abortAt(
          method,
          s"concrete ABI method ${method.name} must consist solely of delegate[\"target\"](...) or unsupported"
        )
    }

  private def publicMethods(using quotes: Quotes)(interface: quotes.reflect.Symbol): Vector[quotes.reflect.Symbol] = {
    import quotes.reflect.*
    val seen = scala.collection.mutable.Set.empty[String]
    interface.typeRef.baseClasses.iterator.filter(_.flags.is(Flags.Trait)).foldLeft(Vector.empty[Symbol]) {
      case (methods, owner) =>
        val declared = owner.declaredMethods
          .filterNot(method => method.flags.is(Flags.Synthetic) || method.flags.is(Flags.Private))
          .filterNot(_.name == "<init>")
        val visible = declared.filterNot(method => seen(method.name))
        seen ++= declared.map(_.name)
        methods ++ visible
    }.sortBy(_.name)
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
      case Block(Nil, expression)            => loop(expression)
      case Typed(expression, _)              => loop(expression)
      case _                                 => None
    }

    method.tree match {
      case DefDef(_, _, _, Some(body)) => loop(body)
      case _                           => None
    }
  }

  private def retainedUnsupported(using quotes: Quotes)(method: quotes.reflect.Symbol): Boolean = {
    import quotes.reflect.*

    def marker(tree: Tree): Boolean =
      tree.symbol.fullName == "net.kurobako.gemstone.abi.ABI$package$.unsupported"

    def loop(tree: Tree): Boolean = tree match {
      case Apply(function, Nil)                   => marker(function) || loop(function)
      case expression: Term if marker(expression) => true
      case Inlined(Some(call), _, expansion)      => loop(call) || loop(expansion)
      case Inlined(None, _, expansion)            => loop(expansion)
      case Block(Nil, expression)                 => loop(expression)
      case Typed(expression, _)                   => loop(expression)
      case _                                      => false
    }

    method.tree match {
      case DefDef(_, _, _, Some(body)) => loop(body)
      case _                           => false
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
