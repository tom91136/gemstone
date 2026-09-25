#include "completion.hpp"

#include <algorithm>
#include <atomic>
#include <cctype>
#include <charconv>
#include <cmath>
#include <cstdio>
#include <cstring>
#include <limits>
#include <string_view>
#include <type_traits>

#include <fmt/printf.h>

namespace gemstone::execution {

namespace {

template <class T> bool appendFormatted(std::string &output, const std::string &format, T value) {
  try {
    output += fmt::sprintf(format, value);
    return true;
  } catch (const fmt::format_error &) {
    return false;
  }
}

uint64_t widthMask(int32_t width) { return width >= 64 ? std::numeric_limits<uint64_t>::max() : (uint64_t{1} << width) - 1; }

int64_t signedValue(int64_t bits, int32_t width) {
  const uint64_t masked = static_cast<uint64_t>(bits) & widthMask(width);
  if (width >= 64 || (masked & (uint64_t{1} << (width - 1))) == 0) return static_cast<int64_t>(masked);
  return static_cast<int64_t>(masked | ~widthMask(width));
}

double floatingValue(int64_t bits, int32_t width) {
  if (width == 32) {
    const uint32_t encoded = static_cast<uint32_t>(bits);
    float value;
    std::memcpy(&value, &encoded, sizeof(value));
    return value;
  }
  if (width == 64) {
    double value;
    std::memcpy(&value, &bits, sizeof(value));
    return value;
  }
  const uint16_t encoded = static_cast<uint16_t>(bits);
  const uint32_t sign = static_cast<uint32_t>(encoded & 0x8000U) << 16;
  const uint32_t exponent = (encoded >> 10) & 0x1fU;
  const uint32_t fraction = encoded & 0x3ffU;
  uint32_t expanded;
  if (exponent == 0) {
    if (fraction == 0) expanded = sign;
    else {
      uint32_t mantissa = fraction;
      uint32_t shift = 0;
      while ((mantissa & 0x400U) == 0) {
        mantissa <<= 1;
        ++shift;
      }
      expanded = sign | ((127U - 14U - shift) << 23) | ((mantissa & 0x3ffU) << 13);
    }
  } else if (exponent == 0x1fU) expanded = sign | 0x7f800000U | (fraction << 13);
  else expanded = sign | ((exponent - 15U + 127U) << 23) | (fraction << 13);
  float value;
  std::memcpy(&value, &expanded, sizeof(value));
  return value;
}

bool appendArgument(std::string &output, const PrintArgument &argument, std::string_view flagsAndSize, std::string_view lengthSpecifier,
                    char conversion, size_t lane) {
  const std::string spec = "%" + std::string(flagsAndSize);
  return std::visit(
      [&](const auto &value) {
        using T = std::decay_t<decltype(value)>;
        if constexpr (std::is_same_v<T, PrintArgumentInteger>) {
          if (lane >= value.bits.size() || value.width <= 0 || value.width > 64) return false;
          const int32_t width = lengthSpecifier == "hh" ? 8 : lengthSpecifier == "h" ? 16 : lengthSpecifier == "l" ? 64 : value.width;
          if (conversion == 'c') return appendFormatted(output, spec + "c", static_cast<int>(value.bits[lane] & 0xff));
          if (conversion == 'd' || conversion == 'i')
            return appendFormatted(output, spec + "lld", static_cast<long long>(signedValue(value.bits[lane], width)));
          if (conversion == 'o' || conversion == 'u' || conversion == 'x' || conversion == 'X')
            return appendFormatted(output, spec + "ll" + conversion,
                                   static_cast<unsigned long long>(static_cast<uint64_t>(value.bits[lane]) & widthMask(width)));
          return false;
        } else if constexpr (std::is_same_v<T, PrintArgumentFloating>) {
          if (lane >= value.bits.size() || (value.width != 16 && value.width != 32 && value.width != 64)) return false;
          if (std::string_view("aAeEfFgG").find(conversion) == std::string_view::npos) return false;
          return appendFormatted(output, spec + conversion, floatingValue(value.bits[lane], value.width));
        } else if constexpr (std::is_same_v<T, PrintArgumentPointer>) {
          if (lane != 0 || conversion != 'p') return false;
          return appendFormatted(output, spec + "p", reinterpret_cast<void *>(static_cast<uintptr_t>(value.address)));
        } else {
          if (lane != 0 || conversion != 's') return false;
          return appendFormatted(output, spec + "s", value.value.c_str());
        }
      },
      argument);
}

size_t argumentLanes(const PrintArgument &argument) {
  return std::visit(
      [](const auto &value) -> size_t {
        using T = std::decay_t<decltype(value)>;
        if constexpr (std::is_same_v<T, PrintArgumentInteger> || std::is_same_v<T, PrintArgumentFloating>) return value.bits.size();
        else return 1;
      },
      argument);
}

bool render(const PrintEvent &event, std::string &output) {
  size_t argument = 0;
  size_t index = 0;
  while (index < event.format.size()) {
    if (event.format[index] != '%') {
      output.push_back(event.format[index++]);
      continue;
    }
    if (index + 1 < event.format.size() && event.format[index + 1] == '%') {
      output.push_back('%');
      index += 2;
      continue;
    }
    const size_t conversionStart = ++index;
    while (index < event.format.size() && std::string_view("-+ #0").find(event.format[index]) != std::string_view::npos)
      ++index;
    if (index < event.format.size() && event.format[index] == '*') return false;
    while (index < event.format.size() && std::isdigit(static_cast<unsigned char>(event.format[index])))
      ++index;
    if (index < event.format.size() && event.format[index] == '.') {
      ++index;
      if (index < event.format.size() && event.format[index] == '*') return false;
      while (index < event.format.size() && std::isdigit(static_cast<unsigned char>(event.format[index])))
        ++index;
    }
    const size_t flagsAndSizeEnd = index;
    size_t vectorWidth = 1;
    if (index < event.format.size() && event.format[index] == 'v') {
      ++index;
      const size_t widthStart = index;
      while (index < event.format.size() && std::isdigit(static_cast<unsigned char>(event.format[index])))
        ++index;
      if (widthStart == index) return false;
      const auto [widthEnd, widthError] = std::from_chars(event.format.data() + widthStart, event.format.data() + index, vectorWidth);
      if (widthError != std::errc{} || widthEnd != event.format.data() + index) return false;
    }
    const size_t lengthStart = index;
    while (index < event.format.size() && (event.format[index] == 'h' || event.format[index] == 'l'))
      ++index;
    if (index >= event.format.size() || argument >= event.arguments.size()) return false;
    const char conversion = event.format[index++];
    const auto &value = event.arguments[argument++];
    if (argumentLanes(value) != vectorWidth) return false;
    const std::string_view flagsAndSize(event.format.data() + conversionStart, flagsAndSizeEnd - conversionStart);
    const std::string_view lengthSpecifier(event.format.data() + lengthStart, index - lengthStart - 1);
    for (size_t lane = 0; lane < vectorWidth; ++lane) {
      if (lane != 0) output.push_back(',');
      if (!appendArgument(output, value, flagsAndSize, lengthSpecifier, conversion, lane)) return false;
    }
    if (output.size() > 1024U * 1024U) return false;
  }
  return argument == event.arguments.size();
}

} // namespace

bool await_completion(Client &client, CompletionId completion, std::string &error, bool hostMemory) {
  if (!hostMemory) {
    const auto result = client.call(AwaitCompletion{completion});
    if (!result) error = result.error().detail;
    return static_cast<bool>(result);
  }
  for (;;) {
    auto batch = client.call(PollHostMemory{completion});
    if (!batch) {
      error = batch.error().detail;
      return false;
    }
    // Only exact interpreter-requested ranges are touched. Never round to pages.
    std::vector<HostMemoryBytes> replies;
    for (const auto &range : batch.value().reads) {
      const auto address = static_cast<uintptr_t>(range.address.value);
      const auto size = static_cast<size_t>(range.size.value);
      if (!address || size > UINTPTR_MAX - address) {
        error = "invalid host read range";
        return false;
      }
      const auto *begin = reinterpret_cast<const uint8_t *>(address);
      replies.push_back({range.address, std::vector<uint8_t>(begin, begin + size)});
    }
    for (const auto &range : batch.value().writes) {
      const auto address = static_cast<uintptr_t>(range.address.value);
      if (!address || range.bytes.size() > UINTPTR_MAX - address) {
        error = "invalid host write range";
        return false;
      }
      if (range.atomicWidth != 0) {
        const auto width = range.atomicWidth;
        if ((width != 4 && width != 8) || address % width || range.bytes.size() % width) {
          error = "invalid host atomic store range";
          return false;
        }
        for (size_t offset = 0; offset < range.bytes.size(); offset += width) {
          if (width == 4) {
            uint32_t value;
            std::memcpy(&value, range.bytes.data() + offset, sizeof(value));
            std::atomic_ref<uint32_t>(*reinterpret_cast<uint32_t *>(address + offset)).store(value, std::memory_order_seq_cst);
          } else {
            uint64_t value;
            std::memcpy(&value, range.bytes.data() + offset, sizeof(value));
            std::atomic_ref<uint64_t>(*reinterpret_cast<uint64_t *>(address + offset)).store(value, std::memory_order_seq_cst);
          }
        }
      } else std::memcpy(reinterpret_cast<void *>(address), range.bytes.data(), range.bytes.size());
    }
    if (batch.value().sync) {
      const auto &sync = *batch.value().sync;
      uint64_t observed = 0;
      if (sync.atomic) {
        const auto &request = *sync.atomic;
        const auto address = static_cast<uintptr_t>(request.address.value);
        observed = static_cast<uint64_t>(request.expected);
        if (!address || (request.size != 4 && request.size != 8) || address % request.size != 0) {
          error = "invalid host atomic range";
          return false;
        }
        if (request.size == 4) {
          auto expected = static_cast<uint32_t>(observed);
          std::atomic_ref<uint32_t>(*reinterpret_cast<uint32_t *>(address))
              .compare_exchange_strong(expected, static_cast<uint32_t>(request.desired), std::memory_order_seq_cst);
          observed = expected;
        } else {
          std::atomic_ref<uint64_t>(*reinterpret_cast<uint64_t *>(address))
              .compare_exchange_strong(observed, static_cast<uint64_t>(request.desired), std::memory_order_seq_cst);
        }
      } else std::atomic_thread_fence(std::memory_order_seq_cst);
      auto supplied = client.call(SupplyHostSync{completion, sync.ticket, static_cast<int64_t>(observed)});
      if (!supplied) {
        error = supplied.error().detail;
        return false;
      }
    }
    if (batch.value().completed) return true;
    if (replies.empty()) continue;
    auto supplied = client.call(SupplyHostMemory{completion, std::move(replies)});
    if (!supplied) {
      error = supplied.error().detail;
      return false;
    }
  }
}

bool collect_completion(Client &client, CompletionId completion, CompletionResult &result, std::string &error, bool hostMemory) {
  result.dirty_ranges.clear();
  result.pointer_relocations.clear();
  error.clear();

  bool succeeded = await_completion(client, completion, error, hostMemory);
  if (succeeded) {
    auto ranges = client.call(CompletionDirtyRanges{completion});
    succeeded = static_cast<bool>(ranges);
    if (succeeded) result.dirty_ranges = std::move(ranges.value());
    else error = ranges.error().detail;
  }
  if (succeeded) {
    auto relocations = client.call(CompletionPointerRelocations{completion});
    succeeded = static_cast<bool>(relocations);
    if (succeeded) result.pointer_relocations = std::move(relocations.value());
    else error = relocations.error().detail;
  }
  if (succeeded) {
    auto prints = client.call(CompletionPrints{completion});
    succeeded = static_cast<bool>(prints);
    if (succeeded) {
      std::string output;
      for (const auto &event : prints.value())
        if (!render(event, output)) {
          succeeded = false;
          error = "cannot render device printf output";
          break;
        }
      if (succeeded && !output.empty()) std::fwrite(output.data(), 1, output.size(), stdout);
    } else error = prints.error().detail;
  }

  const auto released = client.call(ReleaseCompletion{completion});
  if (!released && error.empty()) error = released.error().detail;
  return succeeded && released;
}

} // namespace gemstone::execution
