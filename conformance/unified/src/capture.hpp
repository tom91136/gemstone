#pragma once

#include <cstdio>
#include <stdexcept>
#include <string>
#include <vector>

#include <unistd.h>

class StreamCapture {
  std::FILE *stream;
  int saved = -1;
  std::FILE *file = nullptr;

public:
  explicit StreamCapture(std::FILE *stream = stdout) : stream(stream), file(std::tmpfile()) {
    std::fflush(stream);
    if (!file) throw std::runtime_error("tmpfile failed");
    saved = dup(fileno(stream));
    dup2(fileno(file), fileno(stream));
  }
  StreamCapture(const StreamCapture &) = delete;
  StreamCapture &operator=(const StreamCapture &) = delete;
  ~StreamCapture() {
    if (saved >= 0) restore();
    if (file) std::fclose(file);
  }

  void restore() {
    std::fflush(stream);
    dup2(saved, fileno(stream));
    close(saved);
    saved = -1;
  }

  std::string finish() {
    restore();
    std::string text;
    std::fseek(file, 0, SEEK_SET);
    char chunk[4096];
    size_t count = 0;
    while ((count = std::fread(chunk, 1, sizeof(chunk), file)) > 0)
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
