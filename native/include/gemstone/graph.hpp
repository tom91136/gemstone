#pragma once

#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <functional>
#include <map>
#include <memory>
#include <mutex>
#include <optional>
#include <vector>

namespace gemstone::graph {

enum class Kind { Kernel, Memcpy, Memset, Host, Empty, Work };

enum class Outcome { Ok, InvalidValue, InvalidHandle, Unsupported, Isolation, Unmatched, TopologyChanged };

using Work = std::function<int32_t()>;

struct Node {
  Kind kind;
  Work work;
  std::vector<Node *> dependencies;
};

inline bool dependsOn(const Node *node, const Node *target) {
  return std::any_of(node->dependencies.begin(), node->dependencies.end(),
                     [&](const Node *parent) { return parent == target || dependsOn(parent, target); });
}

struct Graph {
  std::vector<std::unique_ptr<Node>> nodes;

  bool owns(const Node *node) const {
    return std::any_of(nodes.begin(), nodes.end(), [&](const auto &owned) { return owned.get() == node; });
  }

  [[nodiscard]] Graph clone() const {
    Graph copy;
    std::map<const Node *, Node *> copies;
    for (const auto &node : nodes) {
      copy.nodes.push_back(std::make_unique<Node>(Node{.kind = node->kind, .work = node->work, .dependencies = {}}));
      copies.emplace(node.get(), copy.nodes.back().get());
    }
    for (const auto &node : nodes)
      for (const auto *parent : node->dependencies)
        copies[node.get()]->dependencies.push_back(copies[parent]);
    return copy;
  }
};

template <class Handle> Outcome add(Graph *graph, Handle *node, Kind kind, const Handle *dependencies, size_t count, Work work) {
  if (!graph || !node || (count && !dependencies)) return Outcome::InvalidValue;
  std::vector<Node *> parents;
  for (size_t index = 0; index < count; ++index) {
    auto *parent = reinterpret_cast<Node *>(dependencies[index]);
    if (!graph->owns(parent)) return Outcome::InvalidHandle;
    parents.push_back(parent);
  }
  auto owned = std::make_unique<Node>(Node{.kind = kind, .work = std::move(work), .dependencies = std::move(parents)});
  *node = reinterpret_cast<Handle>(owned.get());
  graph->nodes.push_back(std::move(owned));
  return Outcome::Ok;
}

template <class Handle> Outcome connect(Graph *graph, const Handle *from, const Handle *to, size_t count) {
  if (!graph || (count && (!from || !to))) return Outcome::InvalidValue;
  for (size_t index = 0; index < count; ++index) {
    auto *parent = reinterpret_cast<Node *>(from[index]);
    auto *child = reinterpret_cast<Node *>(to[index]);
    if (!graph->owns(parent) || !graph->owns(child)) return Outcome::InvalidHandle;
    if (parent == child || dependsOn(parent, child)) return Outcome::InvalidValue;
    if (std::find(child->dependencies.begin(), child->dependencies.end(), parent) == child->dependencies.end())
      child->dependencies.push_back(parent);
  }
  return Outcome::Ok;
}

/** Writes the nodes, or only the roots, with the query convention of the vendor APIs: a null array asks for the count. */
template <class Handle> Outcome list(const Graph &graph, Handle *nodes, size_t *count, bool roots) {
  if (!count) return Outcome::InvalidValue;
  std::vector<Handle> found;
  for (const auto &node : graph.nodes)
    if (!roots || node->dependencies.empty()) found.push_back(reinterpret_cast<Handle>(node.get()));
  if (!nodes) {
    *count = found.size();
    return Outcome::Ok;
  }
  const auto written = std::min(*count, found.size());
  std::copy_n(found.begin(), written, nodes);
  *count = written;
  return Outcome::Ok;
}

struct Exec {
  struct Step {
    const Node *source;
    Kind kind;
    std::vector<size_t> dependencies;
    Work work;
  };
  std::vector<Step> steps;

  static Exec instantiate(const Graph &graph) {
    Exec exec;
    std::map<const Node *, size_t> emitted;
    for (const auto &node : graph.nodes)
      exec.emit(emitted, node.get());
    return exec;
  }

  Outcome replaceKernel(const Node *node, Work work) {
    for (auto &step : steps) {
      if (step.source != node) continue;
      if (step.kind != Kind::Kernel) return Outcome::InvalidHandle;
      step.work = std::move(work);
      return Outcome::Ok;
    }
    return Outcome::InvalidHandle;
  }

  Outcome update(const Graph &graph) {
    auto updated = instantiate(graph);
    const auto compatible = steps.size() == updated.steps.size()
                            && std::equal(steps.begin(), steps.end(), updated.steps.begin(), [](const auto &lhs, const auto &rhs) {
                                 return lhs.kind == rhs.kind && lhs.dependencies == rhs.dependencies;
                               });
    if (!compatible) return Outcome::TopologyChanged;
    for (size_t index = 0; index < steps.size(); ++index) {
      steps[index].source = updated.steps[index].source;
      steps[index].work = std::move(updated.steps[index].work);
    }
    return Outcome::Ok;
  }

private:
  size_t emit(std::map<const Node *, size_t> &emitted, const Node *node) {
    if (const auto found = emitted.find(node); found != emitted.end()) return found->second;
    std::vector<size_t> dependencies;
    dependencies.reserve(node->dependencies.size());
    for (const auto *parent : node->dependencies)
      dependencies.push_back(emit(emitted, parent));
    steps.push_back({.source = node, .kind = node->kind, .dependencies = std::move(dependencies), .work = node->work});
    return emitted[node] = steps.size() - 1;
  }
};

inline thread_local Work *recordSink = nullptr;

struct Recording {
  explicit Recording(Work &target) { recordSink = &target; }
  ~Recording() { recordSink = nullptr; }
  Recording(const Recording &) = delete;
  Recording &operator=(const Recording &) = delete;
};

inline bool divert(Work &work) {
  if (!recordSink) return false;
  *recordSink = std::move(work);
  return true;
}

template <class Stream, class Event> class Captures {
public:
  struct Info {
    uint64_t id{};
    Graph *graph{};
    std::vector<Node *> frontier;
  };

  bool record(Stream stream, Work &work) {
    std::scoped_lock const lock(mutex);
    const auto capture = captures.find(stream);
    if (capture == captures.end()) return false;
    auto &frontier = capture->second->frontier[stream];
    auto node = std::make_unique<Node>(Node{Kind::Work, std::move(work), frontier});
    frontier = {node.get()};
    capture->second->graph->nodes.push_back(std::move(node));
    return true;
  }

  Outcome begin(Stream stream) {
    std::scoped_lock const lock(mutex);
    if (captures.contains(stream)) return Outcome::Unsupported;
    auto session = std::make_shared<Session>();
    session->origin = stream;
    captures.emplace(stream, std::move(session));
    return Outcome::Ok;
  }

  Outcome end(Stream stream, std::unique_ptr<Graph> &result) {
    std::scoped_lock const lock(mutex);
    const auto capture = captures.find(stream);
    if (capture == captures.end()) return Outcome::InvalidValue;
    const auto session = capture->second;
    if (session->origin != stream) return Outcome::Unmatched;
    std::erase_if(captures, [&](const auto &entry) { return entry.second == session; });
    std::erase_if(events, [&](const auto &entry) { return entry.second.session == session; });
    result = std::move(session->graph);
    return Outcome::Ok;
  }

  /** Nothing if the stream is not capturing, in which case the event no longer carries a capture. */
  std::optional<Outcome> recordEvent(Event event, Stream stream) {
    std::scoped_lock const lock(mutex);
    const auto capture = captures.find(stream);
    if (capture == captures.end()) {
      events.erase(event);
      return std::nullopt;
    }
    events.insert_or_assign(event, Recorded{capture->second, stream, capture->second->frontier[stream]});
    return Outcome::Ok;
  }

  /** Nothing if neither the stream nor the event belongs to a capture; otherwise the stream joins the event's session. */
  std::optional<Outcome> waitEvent(Stream stream, Event event) {
    std::scoped_lock const lock(mutex);
    const auto recorded = events.find(event);
    const auto capture = captures.find(stream);
    if (recorded == events.end() && capture == captures.end()) return std::nullopt;
    if (recorded == events.end()) return Outcome::Unsupported;
    if (capture != captures.end() && capture->second != recorded->second.session) return Outcome::Isolation;
    auto &session = recorded->second.session;
    auto &frontier = session->frontier[stream];
    frontier.insert(frontier.end(), recorded->second.frontier.begin(), recorded->second.frontier.end());
    captures.emplace(stream, session);
    return Outcome::Ok;
  }

  bool capturing(Stream stream) {
    std::scoped_lock const lock(mutex);
    return captures.contains(stream);
  }

  std::optional<Info> info(Stream stream) {
    std::scoped_lock const lock(mutex);
    const auto capture = captures.find(stream);
    if (capture == captures.end()) return std::nullopt;
    return Info{static_cast<uint64_t>(reinterpret_cast<uintptr_t>(capture->second.get())), capture->second->graph.get(),
                capture->second->frontier[stream]};
  }

private:
  struct Session {
    std::unique_ptr<Graph> graph = std::make_unique<Graph>();
    Stream origin{};
    std::map<Stream, std::vector<Node *>> frontier;
  };

  struct Recorded {
    std::shared_ptr<Session> session;
    Stream source;
    std::vector<Node *> frontier;
  };

  std::mutex mutex;
  std::map<Stream, std::shared_ptr<Session>> captures;
  std::map<Event, Recorded> events;
};

} // namespace gemstone::graph
