#ifndef UNIFIED_SUITE_RESULT_HPP
#define UNIFIED_SUITE_RESULT_HPP

#include <exception>
#include <functional>
#include <string>
#include <utility>

namespace unified_suite {

enum class Status { pass, skip, fail };

inline constexpr const char *name(Status status) {
  switch (status) {
  case Status::pass:
    return "PASS";
  case Status::skip:
    return "SKIP";
  case Status::fail:
    return "FAIL";
  }
  return "UNKNOWN";
}

struct Result {
  Status status;
  std::string message;

  static Result pass() { return {Status::pass, {}}; }
  static Result skip(std::string message) { return {Status::skip, std::move(message)}; }
  static Result fail(std::string message) { return {Status::fail, std::move(message)}; }
};

struct Case {
  std::string name;
  std::function<Result()> execute;
};

class Unsupported : public std::exception {
public:
  explicit Unsupported(std::string message) : message_(std::move(message)) {}
  const char *what() const noexcept override { return message_.c_str(); }

private:
  std::string message_;
};

inline Result capture(const std::function<void()> &operation) {
  try {
    operation();
    return Result::pass();
  } catch (const Unsupported &error) {
    return Result::skip(error.what());
  } catch (const std::exception &error) {
    return Result::fail(error.what());
  } catch (...) {
    return Result::fail("unknown non-standard exception");
  }
}

} // namespace unified_suite

#endif
