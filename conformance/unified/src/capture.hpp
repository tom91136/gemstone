#pragma once

#include <cstdio>
#include <stdexcept>
#include <string>
#include <vector>

#include <unistd.h>

class StreamCapture {
  std::FILE *stream_;
  int saved_ = -1;
  std::FILE *file_ = nullptr;

public:
  explicit StreamCapture(std::FILE *stream = stdout) : stream_(stream) {
    std::fflush(stream_);
    file_ = std::tmpfile();
    if (!file_) throw std::runtime_error("tmpfile failed");
    saved_ = dup(fileno(stream_));
    dup2(fileno(file_), fileno(stream_));
  }
  StreamCapture(const StreamCapture &) = delete;
  StreamCapture &operator=(const StreamCapture &) = delete;
  ~StreamCapture() {
    if (saved_ >= 0) restore();
    if (file_) std::fclose(file_);
  }

  void restore() {
    std::fflush(stream_);
    dup2(saved_, fileno(stream_));
    close(saved_);
    saved_ = -1;
  }

  std::string finish() {
    restore();
    std::string text;
    std::rewind(file_);
    char chunk[4096];
    size_t count;
    while ((count = std::fread(chunk, 1, sizeof(chunk), file_)) > 0)
      text.append(chunk, count);
    return text;
  }
};

inline std::vector<std::string> lines(const std::string &text) {
  std::vector<std::string> result;
  for (size_t begin = 0; begin < text.size();) {
    const auto end = text.find('\n', begin);
    const auto stop = end == std::string::npos ? text.size() : end;
    result.push_back(text.substr(begin, stop - begin));
    begin = stop + 1;
  }
  return result;
}
