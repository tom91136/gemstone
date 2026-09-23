package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.rpc.ExecutionRPC
import net.kurobako.gemstone.rpc.Wire
import net.kurobako.gemstone.rpc.Wire.{Alternative, Field, OperationShape, Shape}

import cats.syntax.all.*
import com.monovore.decline.{Command, Opts}

import java.nio.file.Path
import java.nio.charset.StandardCharsets
import scala.collection.mutable

/** Derives the native execution client and codecs from the Scala operation enum. */
object GenerateExecutionRPC {
  private final case class Request(check: Boolean, output: Path)
  private final case class Named(name: String, shape: Shape)

  private val command = Command("GenerateExecutionRPC", "Generate the native typed execution RPC client")((
    Opts.flag("check", help = "Verify existing generated RPC sources").orFalse,
    Opts.argument[String]("output-directory").map(Path.of(_))
  ).mapN(Request(_, _)))

  def main(arguments: Array[String]): Unit = CodegenCommand.run(command, arguments) { request =>
    val protocol = ExecutionRPC.Schema
    val files    = Vector(
      request.output.resolve("gemstone_execution_rpc.hpp") -> header(protocol),
      request.output.resolve("gemstone_execution_rpc.cpp") -> source(protocol)
    )
    files.foreach { case (path, content) => GeneratedFiles.sync(path, content, request.check) }
    GeneratedFiles.requireOnly(request.output, files.map(_._1).toSet, _ => true)
    Console.out.println(s"${if (request.check) "verified" else "generated"} ${files.size} execution RPC files")
  }

  private def hex(value: Long): String          = f"0x$value%016xULL"
  private def identifier(value: String): String = {
    val result = value.map(character => if (character.isLetterOrDigit || character == '_') character else '_')
    if (result.headOption.exists(_.isDigit)) s"_$result" else result
  }
  private def constraintName(constraint: Wire.Constraint): String = constraint match {
    case Wire.Constraint.Positive                       => "Positive"
    case Wire.Constraint.Positive0                      => "Positive0"
    case Wire.Constraint.IntInterval(minimum, maximum)  => s"Interval${numberName(minimum)}To${numberName(maximum)}"
    case Wire.Constraint.LongInterval(minimum, maximum) =>
      s"Interval${numberName(minimum)}To${numberName(maximum)}"
    case Wire.Constraint.MinimumLength(minimum) => s"MinimumLength$minimum"
    case Wire.Constraint.NotContains(value)     =>
      val bytes = value.getBytes(StandardCharsets.UTF_8)
      s"NotContains${bytes.view.map(byte => f"${byte & 0xff}%02x").mkString}"
  }
  private def numberName(value: Long): String =
    if (value < 0L) s"Negative${value.toString.drop(1)}" else value.toString
  private def aliasName(name: String, constraints: Vector[Wire.Constraint]): String =
    if (name == "Int" || name == "Long" || name == "String")
      identifier(name + constraints.map(constraintName(_)).mkString)
    else identifier(name)

  private def cppString(value: String): String = {
    val bytes   = value.getBytes(StandardCharsets.UTF_8)
    val escaped = bytes.view.map(byte => f"\\x${byte & 0xff}%02x").mkString
    s"std::string(\"$escaped\", ${bytes.length}U)"
  }

  private def constraintValidation(
      underlying: Shape,
      constraints: Vector[Wire.Constraint],
      expression: String
  ): String = {
    val validations = constraints.map {
      case Wire.Constraint.Positive if underlying == Shape.Int32 || underlying == Shape.Int64 =>
        s"$expression > 0"
      case Wire.Constraint.Positive0 if underlying == Shape.Int32 || underlying == Shape.Int64 =>
        s"$expression >= 0"
      case Wire.Constraint.IntInterval(minimum, maximum) if underlying == Shape.Int32 =>
        s"$expression >= $minimum && $expression <= $maximum"
      case Wire.Constraint.LongInterval(minimum, maximum) if underlying == Shape.Int64 =>
        s"$expression >= INT64_C($minimum) && $expression <= INT64_C($maximum)"
      case Wire.Constraint.MinimumLength(minimum) if underlying == Shape.Text =>
        s"$expression.size() >= $minimum"
      case Wire.Constraint.NotContains(value) if underlying == Shape.Text =>
        s"$expression.find(${cppString(value)}) == std::string::npos"
      case constraint =>
        throw IllegalArgumentException(s"constraint $constraint is incompatible with wire shape $underlying")
    }
    if (validations.isEmpty) "true" else validations.map(value => s"($value)").mkString(" && ")
  }
  private def alternativeName(sum: String, alternative: String): String = s"$sum$alternative"
  private def isNullary(alternative: Alternative): Boolean              = alternative.shape match {
    case Shape.Product(_, fields) => fields.isEmpty
    case _                        => false
  }

  private final class Model(protocol: Wire.ProtocolShape) {
    private val definitions = mutable.LinkedHashMap.empty[String, Shape]

    private def add(name: String, shape: Shape): Unit = {
      definitions.get(name).foreach { existing =>
        if (existing != shape) throw IllegalStateException(s"conflicting wire type $name")
      }
      if (!definitions.contains(name)) definitions.update(name, shape)
    }

    private def register(shape: Shape): Unit = shape match {
      case Shape.UnitValue | Shape.BooleanValue | Shape.Int8 | Shape.Int32 | Shape.Int64 | Shape.Text => ()
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
      case Shape.Int32                       => "int32_t"
      case Shape.Int64                       => "int64_t"
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
      s"struct ${named.name} { ${model.cppType(underlying)} value{}; };"
    case Shape.Product(_, productFields) =>
      s"struct ${named.name} {\n${fields(productFields, model)}};"
    case Shape.Sum(_, alternatives) if alternatives.forall(isNullary(_)) =>
      val values = alternatives.map(value => s"  ${identifier(value.name)} = ${hex(value.id)}").mkString(",\n")
      s"enum class ${named.name} : uint64_t {\n$values\n};"
    case Shape.Sum(_, alternatives) =>
      val cases = alternatives.map { alternative =>
        val name = alternativeName(named.name, identifier(alternative.name))
        alternative.shape match {
          case Shape.Product(_, caseFields) => s"struct $name {\n${fields(caseFields, model)}};"
          case other                        => s"struct $name {\n  ${model.cppType(other)} value{};\n};"
        }
      }
      val variants = alternatives.map(value => alternativeName(named.name, identifier(value.name))).mkString(", ")
      s"${cases.mkString("\n")}\nusing ${named.name} = std::variant<$variants>;"
    case other => throw IllegalArgumentException(s"unnamed RPC definition: $other")
  }

  private def requestDefinition(operation: OperationShape, model: Model): String = operation.request.shape match {
    case Shape.Product(_, requestFields) =>
      s"struct ${identifier(operation.request.name)} {\n${fields(requestFields, model)}};"
    case other => throw IllegalArgumentException(s"operation ${operation.request.name} is not a product: $other")
  }

  private def header(protocol: Wire.ProtocolShape): String = {
    val model       = Model(protocol)
    val definitions = model.named.map(definition(_, model)).mkString("\n\n")
    val requests    = protocol.operations.map(requestDefinition(_, model)).mkString("\n\n")
    val calls       = protocol.operations.map { operation =>
      s"  Result<${model.cppType(operation.reply)}> call(const ${identifier(operation.request.name)} &request);"
    }.mkString("\n")
    val operationIds = protocol.operations.map(operation =>
      s"inline constexpr uint64_t ${identifier(operation.request.name)} = ${hex(operation.request.id)};"
    ).mkString("\n")
    val tags = model.named.flatMap {
      case Named(name, Shape.Sum(_, alternatives)) => alternatives.map(alternative =>
          s"inline constexpr uint64_t ${alternativeName(name, identifier(alternative.name))} = ${hex(alternative.id)};"
        )
      case _ => Vector.empty
    }.mkString("\n")
    GeneratedHeader.cpp("from ExecutionRPC.Operation") +
      s"""|#ifndef GEMSTONE_EXECUTION_RPC_HPP
       |#define GEMSTONE_EXECUTION_RPC_HPP
       |
       |#include <stddef.h>
       |#include <stdint.h>
       |#include <memory>
       |#include <optional>
       |#include <string>
       |#include <utility>
       |#include <variant>
       |#include <vector>
       |
       |namespace gemstone::execution {
       |
       |inline constexpr uint32_t RequestMagic = UINT32_C(0x${ExecutionRPC.RequestMagic.toHexString});
       |inline constexpr uint32_t ResponseMagic = UINT32_C(0x${ExecutionRPC.ResponseMagic.toHexString});
       |inline constexpr uint32_t MaximumPayload = ${ExecutionRPC.MaximumPayload}U;
       |inline constexpr uint32_t MaximumElements = ${ExecutionRPC.MaximumElements}U;
       |inline constexpr uint32_t MaximumTextBytes = ${ExecutionRPC.MaximumTextBytes}U;
       |inline constexpr uint64_t SchemaFingerprint = ${hex(protocol.fingerprint)};
       |inline constexpr size_t RequestFingerprintOffset = ${ExecutionRPC.RequestFingerprintOffset}U;
       |inline constexpr size_t RequestClientOffset = ${ExecutionRPC.RequestClientOffset}U;
       |inline constexpr size_t RequestSessionHighOffset = ${ExecutionRPC.RequestSessionHighOffset}U;
       |inline constexpr size_t RequestSessionLowOffset = ${ExecutionRPC.RequestSessionLowOffset}U;
       |inline constexpr size_t RequestOperationOffset = ${ExecutionRPC.RequestOperationOffset}U;
       |inline constexpr size_t RequestPayloadSizeOffset = ${ExecutionRPC.RequestPayloadSizeOffset}U;
       |inline constexpr size_t RequestHeaderBytes = ${ExecutionRPC.RequestHeaderBytes}U;
       |inline constexpr size_t ResponseFingerprintOffset = ${ExecutionRPC.ResponseFingerprintOffset}U;
       |inline constexpr size_t ResponseStatusOffset = ${ExecutionRPC.ResponseStatusOffset}U;
       |inline constexpr size_t ResponsePayloadSizeOffset = ${ExecutionRPC.ResponsePayloadSizeOffset}U;
       |inline constexpr size_t ResponseHeaderBytes = ${ExecutionRPC.ResponseHeaderBytes}U;
       |namespace operation {
       |$operationIds
       |}
       |namespace tag {
       |$tags
       |}
       |
       |struct Unit {};
       |
       |$definitions
       |
       |$requests
       |
       |struct Error {
       |  std::string detail;
       |  std::optional<ExecutionFailure> remote;
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
       |#endif
       |""".stripMargin
  }

  private def writeFields(expression: String, value: Vector[Field], model: Model): String =
    value.map(field => s"Codec<${model.cppType(field.shape)}>::write(writer, $expression.${identifier(field.name)});")
      .mkString(" ")

  private def readFields(expression: String, value: Vector[Field], model: Model): String =
    value.map(field => s"Codec<${model.cppType(field.shape)}>::read(reader, $expression.${identifier(field.name)})")
      .mkString(" && ") match {
      case ""     => "true"
      case result => result
    }

  private def codec(named: Named, model: Model): String = named.shape match {
    case Shape.Alias(_, underlying, constraints) =>
      val validation = constraintValidation(underlying, constraints, "value.value")
      s"""template <> struct Codec<${named.name}> {
         |  static void write(Writer &writer, const ${named.name} &value) { if (!($validation)) { writer.invalidate(); return; } Codec<${model.cppType(
          underlying
        )}>::write(writer, value.value); }
         |  static bool read(Reader &reader, ${named.name} &value) { return Codec<${model.cppType(
          underlying
        )}>::read(reader, value.value) && ($validation); }
         |};""".stripMargin
    case Shape.Product(_, productFields) =>
      s"""template <> struct Codec<${named.name}> {
         |  static void write(Writer &writer, const ${named.name} &value) { ${writeFields(
          "value",
          productFields,
          model
        )} }
         |  static bool read(Reader &reader, ${named.name} &value) { return ${readFields(
          "value",
          productFields,
          model
        )}; }
         |};""".stripMargin
    case Shape.Sum(_, alternatives) if alternatives.forall(isNullary(_)) =>
      val accepted = alternatives.map(value =>
        s"case ${hex(value.id)}: value = ${named.name}::${identifier(value.name)}; return true;"
      ).mkString(" ")
      s"""template <> struct Codec<${named.name}> {
         |  static void write(Writer &writer, ${named.name} value) { writer.u64(static_cast<uint64_t>(value)); }
         |  static bool read(Reader &reader, ${named.name} &value) { uint64_t id = 0; if (!reader.u64(id)) return false; switch (id) { $accepted default: return false; } }
         |};""".stripMargin
    case Shape.Sum(_, alternatives) =>
      val writeCases = alternatives.map { alternative =>
        val caseName = alternativeName(named.name, identifier(alternative.name))
        val body     = alternative.shape match {
          case Shape.Product(_, caseFields) => writeFields("item", caseFields, model)
          case other                        => s"Codec<${model.cppType(other)}>::write(writer, item.value);"
        }
        s"if constexpr (std::is_same_v<T, $caseName>) { writer.u64(${hex(alternative.id)}); $body }"
      }.mkString(" else ")
      val readCases = alternatives.map { alternative =>
        val caseName = alternativeName(named.name, identifier(alternative.name))
        val body     = alternative.shape match {
          case Shape.Product(_, caseFields) => readFields("item", caseFields, model)
          case other                        => s"Codec<${model.cppType(other)}>::read(reader, item.value)"
        }
        s"case ${hex(alternative.id)}: { $caseName item{}; if (!($body)) return false; value = std::move(item); return true; }"
      }.mkString(" ")
      s"""template <> struct Codec<${named.name}> {
         |  static void write(Writer &writer, const ${named.name} &value) { std::visit([&](const auto &item) { using T = std::decay_t<decltype(item)>; $writeCases }, value); }
         |  static bool read(Reader &reader, ${named.name} &value) { uint64_t id = 0; if (!reader.u64(id)) return false; switch (id) { $readCases default: return false; } }
         |};""".stripMargin
    case other => throw IllegalArgumentException(s"unnamed RPC codec: $other")
  }

  private def call(operation: OperationShape, model: Model): String = operation.request.shape match {
    case Shape.Product(_, requestFields) =>
      val request   = identifier(operation.request.name)
      val reply     = model.cppType(operation.reply)
      val writes    = writeFields("request", requestFields, model)
      val parameter = if (requestFields.isEmpty) s"const $request &" else s"const $request &request"
      val capture   = if (requestFields.isEmpty) "" else "&"
      val writer    = if (requestFields.isEmpty) "Writer &" else "Writer &writer"
      s"""Result<$reply> Client::call($parameter) {
         |  return implementation_->call<$reply>(operation::$request, [$capture]($writer) { $writes });
         |}""".stripMargin
    case other => throw IllegalArgumentException(s"operation ${operation.request.name} is not a product: $other")
  }

  private def source(protocol: Wire.ProtocolShape): String = {
    val model  = Model(protocol)
    val codecs = model.named.map(codec(_, model)).mkString("\n\n")
    val calls  = protocol.operations.map(call(_, model)).mkString("\n\n")
    GeneratedHeader.cpp("from ExecutionRPC.Operation") +
      s"""|#define _POSIX_C_SOURCE 200112L
       |#include "gemstone_execution_rpc.hpp"
       |#include "process_identity.hpp"
       |
       |#include <arpa/inet.h>
       |#include <cerrno>
       |#include <cstdlib>
       |#include <cstring>
       |#include <limits>
       |#include <mutex>
       |#include <netdb.h>
       |#include <netinet/tcp.h>
       |#include <sys/socket.h>
       |#include <sys/time.h>
       |#include <type_traits>
       |#include <unistd.h>
       |
       |namespace gemstone::execution {
       |namespace {
       |
       |int socketTimeoutSeconds() {
       |  constexpr int fallback = 300;
       |  const char *raw = std::getenv("GEMSTONE_RPC_TIMEOUT_SECONDS");
       |  if (!raw || raw[0] == '\\0') return fallback;
       |  char *end = nullptr; errno = 0; const long value = std::strtol(raw, &end, 10);
       |  return errno == 0 && end && *end == '\\0' && value > 0 && value <= std::numeric_limits<int>::max()
       |             ? static_cast<int>(value) : fallback;
       |}
       |
       |class Writer {
       | public:
       |  void u8(uint8_t value) { if (bytes_.size() == MaximumPayload) { valid_ = false; return; } bytes_.push_back(value); }
       |  void u32(uint32_t value) { u8(value >> 24); u8(value >> 16); u8(value >> 8); u8(value); }
       |  void u64(uint64_t value) { u32(value >> 32); u32(value); }
       |  void bytes(const uint8_t *value, size_t size) { if (size > MaximumPayload - bytes_.size()) { valid_ = false; return; } bytes_.insert(bytes_.end(), value, value + size); }
       |  void invalidate() { valid_ = false; }
       |  bool valid() const { return valid_; }
       |  const std::vector<uint8_t> &result() const { return bytes_; }
       | private:
       |  bool valid_ = true;
       |  std::vector<uint8_t> bytes_;
       |};
       |
       |class Reader {
       | public:
       |  explicit Reader(const std::vector<uint8_t> &bytes) : bytes_(bytes) {}
       |  bool u8(uint8_t &value) { if (remaining() < 1) return false; value = bytes_[offset_++]; return true; }
       |  bool u32(uint32_t &value) { if (remaining() < 4) return false; value = (uint32_t(bytes_[offset_]) << 24) | (uint32_t(bytes_[offset_ + 1]) << 16) | (uint32_t(bytes_[offset_ + 2]) << 8) | bytes_[offset_ + 3]; offset_ += 4; return true; }
       |  bool u64(uint64_t &value) { uint32_t high = 0, low = 0; if (!u32(high) || !u32(low)) return false; value = (uint64_t(high) << 32) | low; return true; }
       |  bool bytes(uint8_t *value, size_t size) { if (size > remaining()) return false; std::memcpy(value, bytes_.data() + offset_, size); offset_ += size; return true; }
       |  size_t remaining() const { return bytes_.size() - offset_; }
       | private:
       |  const std::vector<uint8_t> &bytes_;
       |  size_t offset_ = 0;
       |};
       |
       |template <class T> struct Codec;
       |template <> struct Codec<Unit> { static void write(Writer &, Unit) {} static bool read(Reader &, Unit &) { return true; } };
       |template <> struct Codec<bool> { static void write(Writer &w, bool v) { w.u8(v ? 1 : 0); } static bool read(Reader &r, bool &v) { uint8_t n = 0; if (!r.u8(n) || n > 1) return false; v = n != 0; return true; } };
       |template <> struct Codec<uint8_t> { static void write(Writer &w, uint8_t v) { w.u8(v); } static bool read(Reader &r, uint8_t &v) { return r.u8(v); } };
       |template <> struct Codec<int32_t> { static void write(Writer &w, int32_t v) { w.u32(static_cast<uint32_t>(v)); } static bool read(Reader &r, int32_t &v) { uint32_t n = 0; if (!r.u32(n)) return false; v = static_cast<int32_t>(n); return true; } };
       |template <> struct Codec<int64_t> { static void write(Writer &w, int64_t v) { w.u64(static_cast<uint64_t>(v)); } static bool read(Reader &r, int64_t &v) { uint64_t n = 0; if (!r.u64(n)) return false; v = static_cast<int64_t>(n); return true; } };
       |template <> struct Codec<std::string> { static void write(Writer &w, const std::string &v) { if (v.size() > MaximumTextBytes) { w.invalidate(); return; } w.u32(static_cast<uint32_t>(v.size())); w.bytes(reinterpret_cast<const uint8_t *>(v.data()), v.size()); } static bool read(Reader &r, std::string &v) { uint32_t n = 0; if (!r.u32(n) || n > MaximumTextBytes || n > r.remaining()) return false; v.resize(n); return r.bytes(reinterpret_cast<uint8_t *>(v.data()), n); } };
       |template <class T> struct Codec<std::vector<T>> { static void write(Writer &w, const std::vector<T> &v) { const size_t limit = std::is_same_v<T, uint8_t> ? MaximumPayload : MaximumElements; if (v.size() > limit) { w.invalidate(); return; } w.u32(static_cast<uint32_t>(v.size())); if constexpr (std::is_same_v<T, uint8_t>) w.bytes(v.data(), v.size()); else for (const auto &item : v) Codec<T>::write(w, item); } static bool read(Reader &r, std::vector<T> &v) { uint32_t n = 0; const uint32_t limit = std::is_same_v<T, uint8_t> ? MaximumPayload : MaximumElements; if (!r.u32(n) || n > limit) return false; v.resize(n); if constexpr (std::is_same_v<T, uint8_t>) return r.bytes(v.data(), v.size()); else { for (auto &item : v) if (!Codec<T>::read(r, item)) return false; return true; } } };
       |template <class T> struct Codec<std::optional<T>> { static void write(Writer &w, const std::optional<T> &v) { w.u8(v ? 1 : 0); if (v) Codec<T>::write(w, *v); } static bool read(Reader &r, std::optional<T> &v) { uint8_t tag = 0; if (!r.u8(tag) || tag > 1) return false; if (tag == 0) { v.reset(); return true; } T item{}; if (!Codec<T>::read(r, item)) return false; v = std::move(item); return true; } };
       |
       |$codecs
       |
       |std::string failureDetail(const ExecutionFailure &failure) {
       |  return std::visit([](const auto &value) { return value.detail; }, failure);
       |}
       |
       |bool parseClient(const char *text, uint64_t &value) {
       |  if (text == nullptr || text[0] == '\\0' || text[0] == '-') return false;
       |  errno = 0; char *end = nullptr; const auto parsed = std::strtoull(text, &end, 10);
       |  if (errno == ERANGE || end == text || *end != '\\0' || parsed == 0) return false;
       |  value = static_cast<uint64_t>(parsed); return true;
       |}
       |
       |bool sendAll(int fd, const uint8_t *data, size_t size) {
       |  while (size != 0) { const auto sent = ::send(fd, data, size, MSG_NOSIGNAL); if (sent < 0 && errno == EINTR) continue; if (sent <= 0) return false; data += sent; size -= static_cast<size_t>(sent); } return true;
       |}
       |bool receiveAll(int fd, uint8_t *data, size_t size) {
       |  while (size != 0) { const auto received = ::recv(fd, data, size, 0); if (received < 0 && errno == EINTR) continue; if (received <= 0) return false; data += received; size -= static_cast<size_t>(received); } return true;
       |}
       |
       |} // namespace
       |
       |class Client::Impl {
       | public:
       |  Impl(uint64_t client, uint64_t high, uint64_t low) : client_(client), sessionHigh_(high), sessionLow_(low) {}
       |  ~Impl() { if (fd_ >= 0) ::close(fd_); }
       |  std::string error() const { std::lock_guard<std::mutex> guard(mutex_); return error_; }
       |
       |  template <class T, class Encode> Result<T> call(uint64_t operation, Encode encode) {
       |    std::lock_guard<std::mutex> guard(mutex_); error_.clear(); Writer writer; encode(writer);
       |    if (!writer.valid()) return failed<T>("execution request exceeds the encoding limits");
       |    std::vector<uint8_t> response; uint32_t status = 0;
       |    if (!exchange(operation, writer.result(), status, response)) return Result<T>::failure(Error{error_, std::nullopt});
       |    Reader reader(response);
       |    if (status == 1) { ExecutionFailure failure{}; if (!Codec<ExecutionFailure>::read(reader, failure) || reader.remaining() != 0) { poison(); return failed<T>("execution failure response is invalid"); } error_ = failureDetail(failure); return Result<T>::failure(Error{error_, std::move(failure)}); }
       |    T value{}; if (!Codec<T>::read(reader, value) || reader.remaining() != 0) { poison(); return failed<T>("execution response payload is invalid"); }
       |    return Result<T>::success(std::move(value));
       |  }
       |
       | private:
       |  template <class T> Result<T> failed(std::string detail) { error_ = std::move(detail); return Result<T>::failure(Error{error_, std::nullopt}); }
       |  void poison() { if (fd_ >= 0) ::close(fd_); fd_ = -1; if (established_) poisoned_ = true; }
       |  bool connectServer() {
       |    if (fd_ >= 0) return true;
       |    if (poisoned_) { error_ = "execution session was lost; create a fresh client and rebuild its state"; return false; }
       |    const char *port = std::getenv("GEMSTONE_RPC_PORT"); if (port == nullptr || port[0] == '\\0') { error_ = "GEMSTONE_RPC_PORT is not set"; return false; }
       |    addrinfo hints{}; hints.ai_family = AF_INET; hints.ai_socktype = SOCK_STREAM; addrinfo *addresses = nullptr;
       |    if (::getaddrinfo("127.0.0.1", port, &hints, &addresses) != 0) { error_ = "cannot resolve the Gemstone RPC endpoint"; return false; }
       |    int connected = -1; for (auto *address = addresses; address != nullptr; address = address->ai_next) { connected = ::socket(address->ai_family, address->ai_socktype, address->ai_protocol); if (connected < 0) continue; if (::connect(connected, address->ai_addr, address->ai_addrlen) == 0) break; ::close(connected); connected = -1; }
       |    ::freeaddrinfo(addresses); if (connected < 0) { error_ = "cannot connect to the Gemstone RPC endpoint"; return false; }
       |    const timeval timeout{socketTimeoutSeconds(), 0}; const int noDelay = 1;
       |    if (::setsockopt(connected, IPPROTO_TCP, TCP_NODELAY, &noDelay, sizeof(noDelay)) != 0 ||
       |        ::setsockopt(connected, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout)) != 0 ||
       |        ::setsockopt(connected, SOL_SOCKET, SO_SNDTIMEO, &timeout, sizeof(timeout)) != 0) {
       |      ::close(connected); error_ = "cannot configure the Gemstone RPC connection"; return false;
       |    }
       |    fd_ = connected; established_ = true; return true;
       |  }
       |  bool exchange(uint64_t operation, const std::vector<uint8_t> &payload, uint32_t &status, std::vector<uint8_t> &response) {
       |    if (!connectServer()) return false;
       |    Writer header; header.u32(RequestMagic); header.u64(SchemaFingerprint); header.u64(client_); header.u64(sessionHigh_); header.u64(sessionLow_); header.u64(operation); header.u32(static_cast<uint32_t>(payload.size()));
       |    if (!sendAll(fd_, header.result().data(), header.result().size()) || (!payload.empty() && !sendAll(fd_, payload.data(), payload.size()))) { poison(); error_ = "execution request transport failed"; return false; }
       |    uint8_t headerBytes[ResponseHeaderBytes]; if (!receiveAll(fd_, headerBytes, sizeof(headerBytes))) { poison(); error_ = "execution response transport failed"; return false; }
       |    const std::vector<uint8_t> headerVector(headerBytes, headerBytes + sizeof(headerBytes)); Reader headerReader(headerVector); uint32_t magic = 0, size = 0; uint64_t fingerprint = 0;
       |    if (!headerReader.u32(magic) || !headerReader.u64(fingerprint) || !headerReader.u32(status) || !headerReader.u32(size) || magic != ResponseMagic || fingerprint != SchemaFingerprint || status > 1 || size > MaximumPayload) { poison(); error_ = "execution response header is invalid"; return false; }
       |    response.resize(size); if (!response.empty() && !receiveAll(fd_, response.data(), response.size())) { poison(); error_ = "execution response body is truncated"; return false; } return true;
       |  }
       |  mutable std::mutex mutex_; int fd_ = -1; bool established_ = false; bool poisoned_ = false;
       |  uint64_t client_; uint64_t sessionHigh_; uint64_t sessionLow_; std::string error_;
       |};
       |
       |std::unique_ptr<Client> Client::fromEnvironment() {
       |  uint64_t client = 0; if (!parseClient(std::getenv("GEMSTONE_RPC_CLIENT"), client)) return nullptr;
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
