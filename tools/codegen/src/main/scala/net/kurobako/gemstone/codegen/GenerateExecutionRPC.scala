package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.Constraint.Positive

import net.kurobako.gemstone.rpc.ExecutionRPC
import net.kurobako.gemstone.rpc.Wire
import net.kurobako.gemstone.rpc.Wire.{Alternative, Field, OperationShape, Shape}
import com.monovore.decline.{Command, Opts}

import java.nio.file.Path
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

  private def hex(value: Long): String          = f"0x$value%016xULL"
  private def identifier(value: String): String = {
    val result = value.map(character => if (character.isLetterOrDigit || character == '_') character else '_')
    if (result.headOption.exists(_.isDigit)) s"_$result" else result
  }
  private def qualified(name: String): String = s"$Namespace::$name"
  private def numberName(value: Long): String =
    if (value < 0L) s"Negative${value.toString.drop(1)}" else value.toString
  private def aliasName(name: String, constraints: Vector[Wire.Constraint]): String =
    if (name == "Int" || name == "Long" || name == "String") identifier(name + constraints.map {
      case Wire.Constraint.Positive               => "Positive"
      case Wire.Constraint.Positive0              => "Positive0"
      case Wire.Constraint.IntInterval(min, max)  => s"Interval${numberName(min)}To${numberName(max)}"
      case Wire.Constraint.LongInterval(min, max) => s"Interval${numberName(min)}To${numberName(max)}"
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
      s"""struct ${named.name} {
         |  ${model.cppType(underlying)} value{};
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

  private def metaDefinition(named: Named, model: Model): Vector[String] = named.shape match {
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
    val metas = Vector(
      s"template <> struct glz::meta<${qualified("Unit")}> { static constexpr auto value = glz::object(); };"
    ) ++ model.named.flatMap(metaDefinition(_, model)) ++
      protocol.operations.flatMap(operation => requestMeta(operation))
    GeneratedHeader.cpp("from ExecutionRPC.Operation") +
      s"""|#ifndef GEMSTONE_EXECUTION_RPC_HPP
       |#define GEMSTONE_EXECUTION_RPC_HPP
       |
       |#include <stddef.h>
       |#include <stdint.h>
       |#include <array>
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
       |
       |struct Unit {};
       |
       |$definitions
       |
       |$requests
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
       |#include <string_view>
       |#include <sys/socket.h>
       |#include <sys/time.h>
       |#include <type_traits>
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
       |int socketTimeoutSeconds() {
       |  constexpr int fallback = 300;
       |  const char *raw = std::getenv("GEMSTONE_RPC_TIMEOUT_SECONDS");
       |  if (!raw || raw[0] == '\\0') return fallback;
       |  char *end = nullptr; errno = 0; const long value = std::strtol(raw, &end, 10);
       |  return errno == 0 && end && *end == '\\0' && value > 0 && value <= std::numeric_limits<int>::max()
       |             ? static_cast<int>(value) : fallback;
       |}
       |
       |void appendU32(std::vector<uint8_t> &out, uint32_t value) {
       |  out.push_back(static_cast<uint8_t>(value >> 24));
       |  out.push_back(static_cast<uint8_t>(value >> 16));
       |  out.push_back(static_cast<uint8_t>(value >> 8));
       |  out.push_back(static_cast<uint8_t>(value));
       |}
       |
       |void appendU64(std::vector<uint8_t> &out, uint64_t value) {
       |  appendU32(out, static_cast<uint32_t>(value >> 32));
       |  appendU32(out, static_cast<uint32_t>(value));
       |}
       |
       |uint32_t readU32(const uint8_t *data) {
       |  return (static_cast<uint32_t>(data[0]) << 24) | (static_cast<uint32_t>(data[1]) << 16) | (static_cast<uint32_t>(data[2]) << 8)
       |         | static_cast<uint32_t>(data[3]);
       |}
       |
       |uint64_t readU64(const uint8_t *data) { return (static_cast<uint64_t>(readU32(data)) << 32) | readU32(data + 4); }
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
       |  template <class T, class Request> Result<T> call(uint64_t operation, const Request &request) {
       |    std::lock_guard<std::mutex> guard(mutex_); error_.clear();
       |    std::string payload;
       |    if (glz::write<GlazeOptions{}>(request, payload)) return failed<T>("execution request encoding failed");
       |    if (payload.size() > MaximumPayload) return failed<T>("execution request exceeds the encoding limits");
       |    std::vector<uint8_t> response; uint32_t status = 0;
       |    if (!exchange(operation, payload, status, response)) return Result<T>::failure(Error{error_});
       |    const std::string_view body(reinterpret_cast<const char *>(response.data()), response.size());
       |    if (status == 1) {
       |      ExecutionFailure failure{};
       |      if (glz::read<GlazeOptions{}>(failure, body)) { poison(); return failed<T>("execution failure response is invalid"); }
       |      error_ = failureDetail(failure); return Result<T>::failure(Error{error_});
       |    }
       |    T value{};
       |    if (glz::read<GlazeOptions{}>(value, body)) { poison(); return failed<T>("execution response payload is invalid"); }
       |    return Result<T>::success(std::move(value));
       |  }
       |
       | private:
       |  template <class T> Result<T> failed(std::string detail) { error_ = std::move(detail); return Result<T>::failure(Error{error_}); }
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
       |  bool exchange(uint64_t operation, const std::string &payload, uint32_t &status, std::vector<uint8_t> &response) {
       |    if (!connectServer()) return false;
       |    std::vector<uint8_t> header;
       |    appendU32(header, RequestMagic); appendU64(header, SchemaFingerprint); appendU64(header, client_);
       |    appendU64(header, sessionHigh_); appendU64(header, sessionLow_); appendU64(header, operation);
       |    appendU32(header, static_cast<uint32_t>(payload.size()));
       |    if (!sendAll(fd_, header.data(), header.size())
       |        || (!payload.empty() && !sendAll(fd_, reinterpret_cast<const uint8_t *>(payload.data()), payload.size()))) {
       |      poison(); error_ = "execution request transport failed"; return false;
       |    }
       |    uint8_t headerBytes[ResponseHeaderBytes];
       |    if (!receiveAll(fd_, headerBytes, sizeof(headerBytes))) { poison(); error_ = "execution response transport failed"; return false; }
       |    const uint32_t magic = readU32(headerBytes); const uint64_t fingerprint = readU64(headerBytes + 4);
       |    status = readU32(headerBytes + 12); const uint32_t size = readU32(headerBytes + 16);
       |    if (magic != ResponseMagic || fingerprint != SchemaFingerprint || status > 1 || size > MaximumPayload) {
       |      poison(); error_ = "execution response header is invalid"; return false;
       |    }
       |    response.resize(size); if (!response.empty() && !receiveAll(fd_, response.data(), response.size())) { poison(); error_ = "execution response body is truncated"; return false; }
       |    return true;
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
