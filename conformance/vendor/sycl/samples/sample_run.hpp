#pragma once

#include <iostream>
#include <sstream>
#include <streambuf>
#include <string>
#include <type_traits>
#include <vector>

struct SampleResult {
  int rc;
  std::string output;
};

struct TeeBuffer : std::streambuf {
  std::streambuf *echo;
  std::ostringstream buffer;

  explicit TeeBuffer(std::streambuf *echo) : echo(echo) {}

  int_type overflow(int_type c) override {
    if (traits_type::eq_int_type(c, traits_type::eof())) return traits_type::not_eof(c);
    echo->sputc(traits_type::to_char_type(c));
    buffer.put(traits_type::to_char_type(c));
    return c;
  }

  int sync() override { return echo->pubsync(); }
};

struct StdoutCapture {
  TeeBuffer tee{std::cout.rdbuf()};
  std::streambuf *previous;

  StdoutCapture() : previous(std::cout.rdbuf(&tee)) {}
  ~StdoutCapture() { std::cout.rdbuf(previous); }
};

template <typename Entry> SampleResult runSample(std::vector<std::string> args, Entry entry) {
  std::vector<char *> argv;
  argv.reserve(args.size() + 1);
  for (auto &arg : args)
    argv.push_back(arg.data());
  argv.push_back(nullptr);

  StdoutCapture capture;
  const auto rc = [&] {
    if constexpr (std::is_invocable_v<Entry, int, char **>) return entry(static_cast<int>(args.size()), argv.data());
    else return entry();
  }();
  return {rc, capture.tee.buffer.str()};
}
