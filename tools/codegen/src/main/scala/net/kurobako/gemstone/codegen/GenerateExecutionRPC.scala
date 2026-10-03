package net.kurobako.gemstone.codegen

import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.nio.file.Path
import net.kurobako.gemstone.graphics.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.Wire.*
import scala.collection.mutable

/** Derives the native execution client and its Glaze MessagePack codecs from the Scala operation enum. */
object GenerateExecutionRPC {
  private case class Request(output: Path)
  private case class Named(name: String, shape: Shape)

  private val Namespace = "gemstone::execution"

  private val command = Command("GenerateExecutionRPC", "Generate the native typed execution RPC client")(
    Opts.argument[String]("output-directory").map(Path.of(_)).map(Request(_))
  )

  def main(arguments: Array[String]): Unit = CodegenCommand.run(command, arguments) { request =>
    val protocol = ExecutionRPC.Schema
    val files    = Vector(
      request.output.resolve("gemstone_execution_rpc.hpp") -> header(protocol),
      request.output.resolve("gemstone_execution_rpc.cpp") -> source(protocol)
    )
    files.foreach { case (path, content) => val _ = GeneratedFiles.write(path, content) }
    GeneratedFiles.removeUnexpected(request.output, files.map(_._1).toSet, _ => true)
    Console.out.println(s"generated ${files.size} execution RPC files")
  }

  private def identifier(value: String): String = {
    val result = value.map(character => if (character.isLetterOrDigit || character == '_') character else '_')
    if (result.headOption.exists(_.isDigit)) s"_$result" else result
  }
  private def qualified(name: String): String = s"$Namespace::$name"
  private def numberName(value: Long): String =
    if (value < 0L) s"Negative${value.toString.drop(1)}" else value.toString
  private def aliasName(name: String, constraints: Vector[Wire.Constraint]): String =
    if (name == "Byte" || name == "Short" || name == "Int" || name == "Long" || name == "String")
      identifier(name + constraints.map {
        case Wire.Constraint.Positive               => "Positive"
        case Wire.Constraint.Positive0              => "Positive0"
        case Wire.Constraint.IntInterval(min, max)  => s"Interval${numberName(min)}To${numberName(max)}"
        case Wire.Constraint.LongInterval(min, max) => s"Interval${numberName(min)}To${numberName(max)}"
        case Wire.Constraint.OneOf(values)          => s"OneOf${values.map(numberName(_)).mkString("Or")}"
        case Wire.Constraint.MultipleOf(divisor)    => s"MultipleOf${numberName(divisor)}"
        case Wire.Constraint.MinimumLength(min)     => s"MinimumLength$min"
        case Wire.Constraint.NotContains(value)     =>
          s"NotContains${value.getBytes(java.nio.charset.StandardCharsets.UTF_8).view.map(byte => f"${byte & 0xff}%02x").mkString}"
      }.mkString)
    else identifier(name)
  private def alternativeName(sum: String, alternative: String): String = s"$sum$alternative"
  private def isNullary(alternative: Alternative): Boolean              = alternative.shape match {
    case Shape.Product(_, fields) => fields.isEmpty
    case _                        => false
  }

  private class Model(protocol: Wire.ProtocolShape) {
    private val definitions = mutable.LinkedHashMap.empty[String, Shape]

    private def add(name: String, shape: Shape): Unit = {
      definitions.get(name).foreach { existing =>
        if (existing != shape) throw IllegalStateException(s"conflicting wire type $name")
      }
      if (!definitions.contains(name)) definitions.update(name, shape)
    }

    private def register(shape: Shape): Unit = shape match {
      case Shape.UnitValue | Shape.BooleanValue | Shape.Int8 | Shape.Int16 | Shape.Int32 | Shape.Int64 | Shape.Float32 | Shape.Float64 | Shape.Text =>
        ()
      case Shape.Sequence(element)                            => register(element)
      case Shape.Optional(element)                            => register(element)
      case value @ Shape.Alias(name, underlying, constraints) =>
        register(underlying); add(aliasName(name, constraints), value)
      case value @ Shape.Product(name, fields) =>
        fields.foreach(field => register(field.shape)); add(identifier(name), value)
      case value @ Shape.Sum(name, alternatives) =>
        alternatives.foreach { alternative =>
          alternative.shape match {
            case Shape.Product(_, fields) => fields.foreach(field => register(field.shape))
            case other                    => register(other)
          }
        }
        add(identifier(name), value)
    }

    protocol.operations.foreach { operation =>
      val _ = operation.request.shape match {
        case Shape.Product(_, fields) => fields.foreach(field => register(field.shape))
        case other                    => register(other)
      }
      register(operation.reply)
    }
    val _ = register(protocol.failure)

    val named: Vector[Named] = definitions.view.map(Named(_, _)).toVector

    def cppType(shape: Shape): String = shape match {
      case Shape.UnitValue                   => "Unit"
      case Shape.BooleanValue                => "bool"
      case Shape.Int8                        => "uint8_t"
      case Shape.Int16                       => "int16_t"
      case Shape.Int32                       => "int32_t"
      case Shape.Int64                       => "int64_t"
      case Shape.Float32                     => "float"
      case Shape.Float64                     => "double"
      case Shape.Text                        => "std::string"
      case Shape.Sequence(element)           => s"std::vector<${cppType(element)}>"
      case Shape.Optional(element)           => s"std::optional<${cppType(element)}>"
      case Shape.Alias(name, _, constraints) => aliasName(name, constraints)
      case Shape.Product(name, _)            => identifier(name)
      case Shape.Sum(name, _)                => identifier(name)
    }
  }

  private def fields(value: Vector[Field], model: Model): String =
    if (value.isEmpty) ""
    else value.map(field => s"  ${model.cppType(field.shape)} ${identifier(field.name)}{};").mkString("\n") + "\n"

  private def definition(named: Named, model: Model): String = named.shape match {
    case Shape.Alias(_, underlying, _) =>
      val carrier = if (underlying == Shape.Int8) "int8_t" else model.cppType(underlying)
      s"""struct ${named.name} {
         |  $carrier value{};
         |};""".stripMargin
    case Shape.Product(_, productFields) =>
      s"struct ${named.name} {\n${fields(productFields, model)}};"
    case Shape.Sum(_, alternatives) if alternatives.forall(isNullary(_)) =>
      val values = alternatives.zipWithIndex.map { case (alternative, index) =>
        s"  ${identifier(alternative.name)} = $index"
      }.mkString(",\n")
      s"enum class ${named.name} : uint8_t {\n$values\n};"
    case Shape.Sum(_, alternatives) =>
      val cases = alternatives.map { alternative =>
        val name = alternativeName(named.name, identifier(alternative.name))
        alternative.shape match {
          case Shape.Product(_, caseFields) if caseFields.isEmpty => s"struct $name {};"
          case Shape.Product(_, caseFields)                       => s"struct $name {\n${fields(caseFields, model)}};"
          case other => s"struct $name {\n  ${model.cppType(other)} value{};\n};"
        }
      }.mkString("\n")
      val variants = alternatives.map(value => alternativeName(named.name, identifier(value.name))).mkString(", ")
      s"""$cases
         |
         |using ${named.name} = std::variant<$variants>;""".stripMargin
    case other => throw IllegalArgumentException(s"unnamed RPC definition: $other")
  }

  private def metaDefinition(named: Named): Vector[String] = named.shape match {
    case Shape.Alias(_, Shape.Sequence(_), _) =>
      val name = qualified(named.name)
      // Glaze 7.9.1 propagates a wrapper's no_header flag into sequence elements.
      // The sequence tag is already supplied, but each element still has its own header.
      Vector(
        s"template <> struct glz::meta<$name> { static constexpr auto value = &$name::value; };",
        s"""template <> struct glz::from<glz::MSGPACK, $name> {
           |  template <auto Opts, class Value, glz::is_context Ctx, class It, class End>
           |  static void op(Value&& value, uint8_t tag, Ctx&& ctx, It& it, const End& end) noexcept {
           |    using Sequence = std::remove_cvref_t<decltype(value.value)>;
           |    glz::from<glz::MSGPACK, Sequence>::template op<glz::no_header_off<Opts>()>(value.value, tag, ctx, it, end);
           |  }
           |};""".stripMargin
      )
    case Shape.Alias(_, _, _) =>
      Vector(s"template <> struct glz::meta<${qualified(named.name)}> { static constexpr auto value = &${qualified(
          named.name
        )}::value; };")
    case Shape.Sum(_, alternatives) if alternatives.forall(isNullary(_)) => Vector.empty
    case Shape.Sum(_, alternatives)                                      =>
      val emptyArms = alternatives.collect {
        case alternative if isNullary(alternative) =>
          val name = alternativeName(named.name, identifier(alternative.name))
          s"template <> struct glz::meta<${qualified(name)}> { static constexpr auto value = glz::object(); };"
      }
      val ids = alternatives.map(value => s"\"${identifier(value.name)}\"").mkString(", ")
      emptyArms :+ s"""template <> struct glz::meta<${qualified(named.name)}> {
                      |  static constexpr std::string_view tag = "t";
                      |  static constexpr std::string_view content = "v";
                      |  static constexpr auto ids = std::array<std::string_view, ${alternatives.length}>{$ids};
                      |};""".stripMargin
    case _ => Vector.empty
  }

  private def requestDefinition(operation: OperationShape, model: Model): String = operation.request.shape match {
    case Shape.Product(_, requestFields) =>
      s"struct ${identifier(operation.request.name)} {\n${fields(requestFields, model)}};"
    case other => throw IllegalArgumentException(s"operation ${operation.request.name} is not a product: $other")
  }

  private def requestMeta(operation: OperationShape): Option[String] = operation.request.shape match {
    case Shape.Product(_, requestFields) if requestFields.isEmpty =>
      Some(
        s"template <> struct glz::meta<${qualified(identifier(operation.request.name))}> { static constexpr auto value = glz::object(); };"
      )
    case _ => None
  }

  private def call(operation: OperationShape, model: Model): String = operation.request.shape match {
    case Shape.Product(_, _) =>
      val request = identifier(operation.request.name)
      val reply   = model.cppType(operation.reply)
      s"""Result<$reply> Client::call(const $request &request) {
         |  return implementation_->call<$reply>(operation::$request, request);
         |}""".stripMargin
    case other => throw IllegalArgumentException(s"operation ${operation.request.name} is not a product: $other")
  }

  private def validation(protocol: Wire.ProtocolShape, model: Model): String = {
    def integer(value: Long): String =
      if (value == Long.MinValue) "std::numeric_limits<int64_t>::min()" else s"${value}LL"
    def constraint(value: Wire.Constraint, underlying: Shape): String = value match {
      case Wire.Constraint.Positive               => "value.value > 0"
      case Wire.Constraint.Positive0              => "value.value >= 0"
      case Wire.Constraint.IntInterval(min, max)  => s"value.value >= ${integer(min)} && value.value <= ${integer(max)}"
      case Wire.Constraint.LongInterval(min, max) => s"value.value >= ${integer(min)} && value.value <= ${integer(max)}"
      case Wire.Constraint.OneOf(values)          =>
        values.map(value => s"value.value == ${integer(value)}").mkString("(", " || ", ")")
      case Wire.Constraint.MultipleOf(divisor) => s"value.value % ${integer(divisor)} == 0"
      case Wire.Constraint.MinimumLength(min)  =>
        val size = if (underlying == Shape.Text) "textLength(value.value)" else "value.value.size()"
        s"$size >= ${min}U"
      case Wire.Constraint.NotContains(value) =>
        val bytes   = value.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        val literal = bytes.view.map(byte => f"\\x${byte & 0xff}%02x").mkString
        s"value.value.find(std::string_view(\"$literal\", ${bytes.length})) == std::string::npos"
    }
    val products = model.named.flatMap { named =>
      named.shape match {
        case Shape.Sum(_, alternatives) if !alternatives.forall(isNullary(_)) =>
          alternatives.map(alternative =>
            Named(alternativeName(named.name, identifier(alternative.name)), alternative.shape)
          ) :+ named
        case _ => Vector(named)
      }
    } ++ protocol.operations.map(operation => Named(identifier(operation.request.name), operation.request.shape))
    val declarations = products.map(named => s"inline bool valid(const ${named.name} &);").mkString("\n")
    val definitions  = products.map { named =>
      val check = named.shape match {
        case Shape.Alias(_, underlying, constraints) =>
          (Vector("valid(value.value)") ++ constraints.map(constraint(_, underlying))).mkString(" && ")
        case Shape.Product(_, fields) =>
          if (fields.isEmpty) "true"
          else fields.map(field => s"valid(value.${identifier(field.name)})").mkString(" && ")
        case Shape.Sum(_, alternatives) if alternatives.forall(isNullary(_)) =>
          s"static_cast<uint8_t>(value) < ${alternatives.size}"
        case Shape.Sum(_, _) =>
          "!value.valueless_by_exception() && std::visit([](const auto &item) { return valid(item); }, value)"
        case _ => "valid(value.value)"
      }
      val parameter = if (check == "true") "" else "value"
      s"inline bool valid(const ${named.name} &$parameter) { return $check; }"
    }.mkString("\n")
    s"""namespace wire {
       |$declarations
       |inline bool valid(const Unit &) { return true; }
       |template <class T> requires std::is_arithmetic_v<T> inline bool valid(T) { return true; }
       |inline bool valid(const std::string &value) { return value.size() <= MaximumTextBytes; }
       |// Scala string refinements count UTF-16 code units, not UTF-8 bytes.
       |inline size_t textLength(std::string_view value) {
       |  size_t length = 0;
       |  for (const unsigned char byte : value) {
       |    if ((byte & 0xc0) != 0x80) length += byte >= 0xf0 ? 2 : 1;
       |  }
       |  return length;
       |}
       |template <class T> inline bool valid(const std::vector<T> &);
       |template <class T> inline bool valid(const std::optional<T> &value) { return !value || valid(*value); }
       |template <class T> inline bool valid(const std::vector<T> &value) {
       |  if constexpr (std::is_same_v<T, uint8_t>) return value.size() <= MaximumPayload;
       |  else return value.size() <= MaximumElements && std::all_of(value.begin(), value.end(), [](const auto &item) { return valid(item); });
       |}
       |$definitions
       |} // namespace wire
       |""".stripMargin
  }

  private def header(protocol: Wire.ProtocolShape): String = {
    val model       = Model(protocol)
    val definitions = model.named.map(definition(_, model)).mkString("\n\n")
    val requests    = protocol.operations.map(requestDefinition(_, model)).mkString("\n\n")
    val calls       = protocol.operations.map { operation =>
      s"  Result<${model.cppType(operation.reply)}> call(const ${identifier(operation.request.name)} &request);"
    }.mkString("\n")
    val operationTags = protocol.operations.map(operation =>
      s"inline constexpr std::string_view ${identifier(operation.request.name)} = ${Emission.literal(operation.request.name)};"
    ).mkString("\n")
    val metas = Vector(
      s"template <> struct glz::meta<${qualified("Unit")}> { static constexpr auto value = glz::object(); };"
    ) ++ model.named.flatMap(metaDefinition(_)) ++
      protocol.operations.flatMap(operation => requestMeta(operation))
    GeneratedHeader.cpp("from ExecutionRPC.Operation") +
      s"""|#ifndef GEMSTONE_EXECUTION_RPC_HPP
       |#define GEMSTONE_EXECUTION_RPC_HPP
       |
       |#include <stddef.h>
       |#include <stdint.h>
       |#include <array>
       |#include <algorithm>
       |#include <limits>
       |#include <type_traits>
       |#include <memory>
       |#include <optional>
       |#include <string>
       |#include <string_view>
       |#include <utility>
       |#include <variant>
       |#include <vector>
       |
       |#include <glaze/glaze.hpp>
       |#include <glaze/msgpack.hpp>
       |
       |namespace gemstone::execution {
       |
       |inline constexpr uint32_t RequestMagic = static_cast<uint32_t>(0x${ExecutionRPC.RequestMagic.toHexString});
       |inline constexpr uint32_t ResponseMagic = static_cast<uint32_t>(0x${ExecutionRPC.ResponseMagic.toHexString});
       |inline constexpr uint32_t GraphicsPushConstantBytes = ${GraphicsProtocol.PushConstantBytes}U;
       |inline constexpr uint32_t MaximumPayload = ${ExecutionRPC.MaximumPayload}U;
       |inline constexpr uint32_t MaximumElements = ${ExecutionRPC.MaximumElements}U;
       |inline constexpr uint32_t MaximumTextBytes = ${ExecutionRPC.MaximumTextBytes}U;
       |inline constexpr uint32_t HelloMagic = ${ExecutionRPC.HelloMagic}U;
       |inline constexpr uint32_t HelloResponseMagic = ${ExecutionRPC.HelloResponseMagic}U;
       |inline constexpr uint32_t MaximumSchemaBytes = ${ExecutionRPC.MaximumSchemaBytes}U;
       |inline constexpr uint32_t MaximumTagBytes = ${ExecutionRPC.MaximumTagBytes}U;
       |inline constexpr std::string_view Schema = ${Emission.literal(protocol.canonical)};
       |inline constexpr size_t RequestClientOffset = ${ExecutionRPC.RequestClientOffset}U;
       |inline constexpr size_t RequestSessionHighOffset = ${ExecutionRPC.RequestSessionHighOffset}U;
       |inline constexpr size_t RequestSessionLowOffset = ${ExecutionRPC.RequestSessionLowOffset}U;
       |inline constexpr size_t RequestTagSizeOffset = ${ExecutionRPC.RequestTagSizeOffset}U;
       |inline constexpr size_t RequestPayloadSizeOffset = ${ExecutionRPC.RequestPayloadSizeOffset}U;
       |inline constexpr size_t RequestHeaderBytes = ${ExecutionRPC.RequestHeaderBytes}U;
       |inline constexpr size_t ResponseStatusOffset = ${ExecutionRPC.ResponseStatusOffset}U;
       |inline constexpr size_t ResponsePayloadSizeOffset = ${ExecutionRPC.ResponsePayloadSizeOffset}U;
       |inline constexpr size_t ResponseHeaderBytes = ${ExecutionRPC.ResponseHeaderBytes}U;
       |namespace operation {
       |$operationTags
       |}
       |
       |struct Unit {};
       |
       |$definitions
       |
       |$requests
       |
       |${validation(protocol, model)}
       |
       |struct Error {
       |  std::string detail;
       |};
       |
       |template <class T> class Result {
       | public:
       |  static Result success(T value) { return Result(std::move(value)); }
       |  static Result failure(Error error) { return Result(std::move(error)); }
       |  explicit operator bool() const { return std::holds_alternative<T>(value_); }
       |  T &value() { return std::get<T>(value_); }
       |  const T &value() const { return std::get<T>(value_); }
       |  const Error &error() const { return std::get<Error>(value_); }
       |
       | private:
       |  explicit Result(T value) : value_(std::move(value)) {}
       |  explicit Result(Error error) : value_(std::move(error)) {}
       |  std::variant<T, Error> value_;
       |};
       |
       |class Client {
       | public:
       |  static std::unique_ptr<Client> fromEnvironment();
       |  ~Client();
       |  Client(const Client &) = delete;
       |  Client &operator=(const Client &) = delete;
       |  std::string error() const;
       |$calls
       |
       | private:
       |  class Impl;
       |  explicit Client(std::unique_ptr<Impl> implementation);
       |  std::unique_ptr<Impl> implementation_;
       |};
       |
       |} // namespace gemstone::execution
       |
       |${metas.mkString("\n")}
       |
       |#endif
       |""".stripMargin
  }

  private def source(protocol: Wire.ProtocolShape): String = {
    val model = Model(protocol)
    val calls = protocol.operations.map(call(_, model)).mkString("\n\n")
    GeneratedHeader.cpp("from ExecutionRPC.Operation") +
      s"""|#define _POSIX_C_SOURCE 200112L
       |#include "gemstone_execution_rpc.hpp"
       |#include <gemstone/environment.hpp>
       |#include "process_identity.hpp"
       |
       |#include "connection.hpp"
       |#include <mutex>
       |#include <string_view>
       |#include <unistd.h>
       |
       |namespace gemstone::execution {
       |namespace {
       |
       |struct GlazeOptions : glz::opts {
       |  static constexpr auto format = glz::MSGPACK;
       |  bool structs_as_arrays = true;
       |};
       |
       |std::string failureDetail(const ExecutionFailure &failure) {
       |  return std::visit([](const auto &value) { return value.detail; }, failure);
       |}
       |
       |} // namespace
       |
       |class Client::Impl {
       | public:
       |  Impl(uint64_t client, uint64_t high, uint64_t low) : connection_(client, high, low) {}
       |  std::string error() const { std::lock_guard<std::mutex> guard(mutex_); return error_; }
       |
       |  template <class T, class Request> Result<T> call(std::string_view operation, const Request &request) {
       |    std::lock_guard<std::mutex> guard(mutex_); error_.clear();
       |    if (!wire::valid(request)) return failed<T>("execution request violates wire constraints or encoding limits");
       |    std::string payload;
       |    if (glz::write<GlazeOptions{}>(request, payload)) return failed<T>("execution request encoding failed");
       |    if (payload.size() > MaximumPayload) return failed<T>("execution request exceeds the encoding limits");
       |    std::vector<uint8_t> response; uint32_t status = 0;
       |    if (!connection_.exchange(operation, payload, status, response)) return failed<T>(connection_.error());
       |    const std::string_view body(reinterpret_cast<const char *>(response.data()), response.size());
       |    if (status == 1) {
       |      ExecutionFailure failure{};
       |      if (glz::read<GlazeOptions{}>(failure, body) || !wire::valid(failure)) { connection_.poison(); return failed<T>("execution failure response is invalid"); }
       |      error_ = failureDetail(failure); return Result<T>::failure(Error{error_});
       |    }
       |    T value{};
       |    if (glz::read<GlazeOptions{}>(value, body) || !wire::valid(value)) { connection_.poison(); return failed<T>("execution response payload is invalid"); }
       |    return Result<T>::success(std::move(value));
       |  }
       |
       | private:
       |  template <class T> Result<T> failed(std::string detail) { error_ = std::move(detail); return Result<T>::failure(Error{error_}); }
       |  mutable std::mutex mutex_;
       |  Connection connection_;
       |  std::string error_;
       |};
       |
       |std::unique_ptr<Client> Client::fromEnvironment() {
       |  const auto client = environment::unsignedValue(environment::RpcClient, 0, std::numeric_limits<int64_t>::max());
       |  if (client == 0) return nullptr;
       |  const auto incarnation = processIncarnation(); if (incarnation == 0) return nullptr;
       |  return std::unique_ptr<Client>(new Client(std::make_unique<Impl>(client, static_cast<uint32_t>(::getpid()), incarnation)));
       |}
       |Client::Client(std::unique_ptr<Impl> implementation) : implementation_(std::move(implementation)) {}
       |Client::~Client() = default;
       |std::string Client::error() const { return implementation_->error(); }
       |
       |$calls
       |
       |} // namespace gemstone::execution
       |""".stripMargin
  }
}
