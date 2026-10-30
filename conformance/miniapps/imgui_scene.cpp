#include "imgui_scene.hpp"

#include <algorithm>
#include <array>
#include <memory>
#include <stdexcept>
#include <string>

#include <imgui.h>
#include <imgui_impl_vulkan.h>

#include <conformance/device.hpp>
#include <conformance/environment_keys.hpp>

#include <vulkan/vulkan.hpp>

// Include the upstream demo to call its static example functions.
#include <imgui_demo.cpp>

namespace imgui_scene {
namespace {
const uint32_t VertexShader[] =
#include "imgui_vert.hpp"

    ;
const uint32_t FragmentShader[] =
#include "imgui_frag.hpp"

    ;

class Renderer {
  vk::UniqueInstance instance;
  vk::PhysicalDevice physical;
  uint32_t family = 0;
  vk::UniqueDevice device;
  vk::Queue queue;
  vk::UniqueCommandPool pool;
  vk::UniqueRenderPass renderPass;
  std::unique_ptr<ImGuiContext, decltype(&ImGui::DestroyContext)> gui{nullptr, ImGui::DestroyContext};

  vk::UniqueDeviceMemory allocate(vk::MemoryRequirements requirements, vk::MemoryPropertyFlags flags) {
    const auto properties = physical.getMemoryProperties();
    for (uint32_t i = 0; i < properties.memoryTypeCount; ++i)
      if ((requirements.memoryTypeBits & (1u << i)) && (properties.memoryTypes[i].propertyFlags & flags) == flags)
        return device->allocateMemoryUnique({requirements.size, i});
    throw std::runtime_error("ImGui: no compatible Vulkan memory type");
  }

public:
  explicit Renderer(bool reference) {
    vk::ApplicationInfo application("conformance-imgui", 1, "imgui", 1, VK_API_VERSION_1_1);
    instance = vk::createInstanceUnique(vk::InstanceCreateInfo({}, &application));
    auto devices = instance->enumeratePhysicalDevices();
    const auto index =
        reference ? 0 : std::stoul(conformance::deviceSelection().environment.at(conformance::environment::VulkanDeviceId)) - 1;
    physical = devices.at(index);
    const auto families = physical.getQueueFamilyProperties();
    const auto graphics = std::find_if(families.begin(), families.end(),
                                       [](const auto &properties) { return bool(properties.queueFlags & vk::QueueFlagBits::eGraphics); });
    if (graphics == families.end()) throw std::runtime_error("ImGui: selected Vulkan device has no graphics queue");
    family = static_cast<uint32_t>(graphics - families.begin());
    const float priority = 1;
    vk::DeviceQueueCreateInfo queueInfo({}, family, 1, &priority);
    device = physical.createDeviceUnique(vk::DeviceCreateInfo({}, queueInfo));
    queue = device->getQueue(family, 0);
    pool = device->createCommandPoolUnique({vk::CommandPoolCreateFlagBits::eResetCommandBuffer, family});
    vk::AttachmentDescription colour({}, vk::Format::eR8G8B8A8Unorm, vk::SampleCountFlagBits::e1, vk::AttachmentLoadOp::eClear,
                                     vk::AttachmentStoreOp::eStore, vk::AttachmentLoadOp::eDontCare, vk::AttachmentStoreOp::eDontCare,
                                     vk::ImageLayout::eUndefined, vk::ImageLayout::eTransferSrcOptimal);
    vk::AttachmentReference attachment(0, vk::ImageLayout::eColorAttachmentOptimal);
    vk::SubpassDescription subpass({}, vk::PipelineBindPoint::eGraphics, {}, attachment);
    vk::SubpassDependency dependency(0, VK_SUBPASS_EXTERNAL, vk::PipelineStageFlagBits::eColorAttachmentOutput,
                                     vk::PipelineStageFlagBits::eTransfer, vk::AccessFlagBits::eColorAttachmentWrite,
                                     vk::AccessFlagBits::eTransferRead);
    renderPass = device->createRenderPassUnique(vk::RenderPassCreateInfo({}, colour, subpass, dependency));
    gui.reset(ImGui::CreateContext());
    auto &io = ImGui::GetIO();
    io.IniFilename = nullptr;
    io.LogFilename = nullptr;
    io.DisplayFramebufferScale = ImVec2(0.5f, 0.5f);
    ImGui::StyleColorsDark();
    ImGui_ImplVulkan_InitInfo info{};
    info.ApiVersion = VK_API_VERSION_1_1;
    info.Instance = *instance;
    info.PhysicalDevice = physical;
    info.Device = *device;
    info.QueueFamily = family;
    info.Queue = queue;
    info.DescriptorPoolSize = 32;
    info.MinImageCount = info.ImageCount = 2;
    info.PipelineInfoMain.RenderPass = *renderPass;
    info.CheckVkResultFn = [](VkResult result) {
      if (result != VK_SUCCESS) throw std::runtime_error("ImGui Vulkan failure: " + std::to_string(result));
    };
    info.CustomShaderVertCreateInfo = {VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO, nullptr, 0, sizeof(VertexShader), VertexShader};
    info.CustomShaderFragCreateInfo = {VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO, nullptr, 0, sizeof(FragmentShader), FragmentShader};
    if (!ImGui_ImplVulkan_Init(&info)) throw std::runtime_error("ImGui Vulkan initialisation failed");
  }
  ~Renderer() {
    (void)vkDeviceWaitIdle(*device);
    ImGui_ImplVulkan_Shutdown();
  }

  Image frame(Scene scene, uint32_t width, uint32_t height) {
    // Declare memory first so resources are destroyed before it.
    vk::UniqueDeviceMemory pixelsMemory, readbackMemory;
    auto image = device->createImageUnique({{},
                                            vk::ImageType::e2D,
                                            vk::Format::eR8G8B8A8Unorm,
                                            {width, height, 1},
                                            1,
                                            1,
                                            vk::SampleCountFlagBits::e1,
                                            vk::ImageTiling::eOptimal,
                                            vk::ImageUsageFlagBits::eColorAttachment | vk::ImageUsageFlagBits::eTransferSrc});
    pixelsMemory = allocate(device->getImageMemoryRequirements(*image), {});
    device->bindImageMemory(*image, *pixelsMemory, 0);
    auto view = device->createImageViewUnique(
        {{}, *image, vk::ImageViewType::e2D, vk::Format::eR8G8B8A8Unorm, {}, {vk::ImageAspectFlagBits::eColor, 0, 1, 0, 1}});
    auto framebuffer = device->createFramebufferUnique({{}, *renderPass, *view, width, height, 1});
    auto readback = device->createBufferUnique({{}, uint64_t(width) * height * 4, vk::BufferUsageFlagBits::eTransferDst});
    readbackMemory = allocate(device->getBufferMemoryRequirements(*readback),
                              vk::MemoryPropertyFlagBits::eHostVisible | vk::MemoryPropertyFlagBits::eHostCoherent);
    device->bindBufferMemory(*readback, *readbackMemory, 0);
    auto commands = device->allocateCommandBuffersUnique({*pool, vk::CommandBufferLevel::ePrimary, 1});
    auto &command = commands.front();
    auto &io = ImGui::GetIO();
    io.DisplaySize = ImVec2(float(width) * 2, float(height) * 2);
    io.DeltaTime = 1.0f / 60;
    ImGui_ImplVulkan_NewFrame();
    ImGui::NewFrame();
    ImGui::SetNextWindowPos(ImVec2(8, 8), ImGuiCond_Always);
    ImGui::SetNextWindowSize(ImVec2(io.DisplaySize.x - 16, io.DisplaySize.y - 16), ImGuiCond_Always);
    bool open = true;
    if (scene == Scene::Primitives) ShowExampleAppCustomRendering(&open);
    else ShowExampleAppLayout(&open);
    ImGui::Render();
    const auto *draw = ImGui::GetDrawData();
    if (!draw || draw->TotalVtxCount == 0) throw std::runtime_error("ImGui produced an empty scene");
    command->begin({vk::CommandBufferUsageFlagBits::eOneTimeSubmit});
    const vk::ClearValue clear(vk::ClearColorValue(std::array<float, 4>{0.125f, 0.25f, 0.375f, 1}));
    command->beginRenderPass({*renderPass, *framebuffer, {{0, 0}, {width, height}}, clear}, vk::SubpassContents::eInline);
    ImGui_ImplVulkan_RenderDrawData(ImGui::GetDrawData(), *command);
    command->endRenderPass();
    const vk::BufferImageCopy copy(0, 0, 0, {vk::ImageAspectFlagBits::eColor, 0, 0, 1}, {0, 0, 0}, {width, height, 1});
    command->copyImageToBuffer(*image, vk::ImageLayout::eTransferSrcOptimal, *readback, copy);
    const vk::MemoryBarrier barrier(vk::AccessFlagBits::eTransferWrite, vk::AccessFlagBits::eHostRead);
    command->pipelineBarrier(vk::PipelineStageFlagBits::eTransfer, vk::PipelineStageFlagBits::eHost, {}, barrier, {}, {});
    command->end();
    queue.submit(vk::SubmitInfo({}, {}, *command));
    queue.waitIdle();
    Image result{width, height, std::vector<uint8_t>(size_t(width) * height * 3)};
    const auto *mapped = static_cast<const uint8_t *>(device->mapMemory(*readbackMemory, 0, VK_WHOLE_SIZE));
    for (size_t pixel = 0; pixel < size_t(width) * height; ++pixel)
      std::copy_n(mapped + pixel * 4, 3, result.rgb.data() + pixel * 3);
    device->unmapMemory(*readbackMemory);
    return result;
  }
};
} // namespace

Image render(Scene scene, bool reference) {
  Renderer renderer(reference);
  // Warm up layout and fonts before capturing the third frame.
  renderer.frame(scene, 320, 240);
  renderer.frame(scene, 320, 240);
  return renderer.frame(scene, scene == Scene::Resize ? 384 : 320, scene == Scene::Resize ? 256 : 240);
}
} // namespace imgui_scene
