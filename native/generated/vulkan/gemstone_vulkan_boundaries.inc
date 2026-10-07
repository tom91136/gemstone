/* Generated from the official API manifest format 6. Regenerate with: sbt codegen */
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkAllocateMemory(VkDevice p0, const VkMemoryAllocateInfo * p1, const VkAllocationCallbacks * p2, VkDeviceMemory * p3) {
if (p3) *p3 = VK_NULL_HANDLE;
try {
  host::Creation creation(p2, handleArena(p0), VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
  try {
    const auto result = implVkAllocateMemory(p0, p1, p2, p3);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p3 && *p3) { implVkFreeMemory(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p3 && *p3) { implVkFreeMemory(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkBeginCommandBuffer(VkCommandBuffer p0, const VkCommandBufferBeginInfo * p1) {
host::Scope scope(handleArena(p0));
try { return implVkBeginCommandBuffer(p0, p1); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkBindBufferMemory(VkDevice p0, VkBuffer p1, VkDeviceMemory p2, VkDeviceSize p3) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkBindBufferMemory(p0, p1, p2, p3); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkBindBufferMemory2(VkDevice p0, uint32_t p1, const VkBindBufferMemoryInfo * p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkBindBufferMemory2(p0, p1, p2); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkBindBufferMemory2KHR(VkDevice p0, uint32_t p1, const VkBindBufferMemoryInfo * p2) {
return vkBindBufferMemory2(p0, p1, p2);
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkBindImageMemory(VkDevice p0, VkImage p1, VkDeviceMemory p2, VkDeviceSize p3) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkBindImageMemory(p0, p1, p2, p3); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkBindImageMemory2(VkDevice p0, uint32_t p1, const VkBindImageMemoryInfo * p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkBindImageMemory2(p0, p1, p2); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkBindImageMemory2KHR(VkDevice p0, uint32_t p1, const VkBindImageMemoryInfo * p2) {
return vkBindImageMemory2(p0, p1, p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBeginQuery(VkCommandBuffer p0, VkQueryPool p1, uint32_t p2, VkQueryControlFlags p3) {
host::Scope scope(handleArena(p0));
try { implVkCmdBeginQuery(p0, p1, p2, p3); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBeginRenderPass(VkCommandBuffer p0, const VkRenderPassBeginInfo * p1, VkSubpassContents p2) {
host::Scope scope(handleArena(p0));
try { implVkCmdBeginRenderPass(p0, p1, p2); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBeginRenderPass2(VkCommandBuffer p0, const VkRenderPassBeginInfo * p1, const VkSubpassBeginInfo * p2) {
host::Scope scope(handleArena(p0));
try { implVkCmdBeginRenderPass2(p0, p1, p2); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBeginRenderPass2KHR(VkCommandBuffer p0, const VkRenderPassBeginInfo * p1, const VkSubpassBeginInfo * p2) {
return vkCmdBeginRenderPass2(p0, p1, p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindDescriptorSets(VkCommandBuffer p0, VkPipelineBindPoint p1, VkPipelineLayout p2, uint32_t p3, uint32_t p4, const VkDescriptorSet * p5, uint32_t p6, const uint32_t * p7) {
host::Scope scope(handleArena(p0));
try { implVkCmdBindDescriptorSets(p0, p1, p2, p3, p4, p5, p6, p7); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindIndexBuffer(VkCommandBuffer p0, VkBuffer p1, VkDeviceSize p2, VkIndexType p3) {
host::Scope scope(handleArena(p0));
try { implVkCmdBindIndexBuffer(p0, p1, p2, p3); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindPipeline(VkCommandBuffer p0, VkPipelineBindPoint p1, VkPipeline p2) {
host::Scope scope(handleArena(p0));
try { implVkCmdBindPipeline(p0, p1, p2); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindVertexBuffers(VkCommandBuffer p0, uint32_t p1, uint32_t p2, const VkBuffer * p3, const VkDeviceSize * p4) {
host::Scope scope(handleArena(p0));
try { implVkCmdBindVertexBuffers(p0, p1, p2, p3, p4); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBlitImage(VkCommandBuffer p0, VkImage p1, VkImageLayout p2, VkImage p3, VkImageLayout p4, uint32_t p5, const VkImageBlit * p6, VkFilter p7) {
host::Scope scope(handleArena(p0));
try { implVkCmdBlitImage(p0, p1, p2, p3, p4, p5, p6, p7); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdClearAttachments(VkCommandBuffer p0, uint32_t p1, const VkClearAttachment * p2, uint32_t p3, const VkClearRect * p4) {
host::Scope scope(handleArena(p0));
try { implVkCmdClearAttachments(p0, p1, p2, p3, p4); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdClearColorImage(VkCommandBuffer p0, VkImage p1, VkImageLayout p2, const VkClearColorValue * p3, uint32_t p4, const VkImageSubresourceRange * p5) {
host::Scope scope(handleArena(p0));
try { implVkCmdClearColorImage(p0, p1, p2, p3, p4, p5); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdClearDepthStencilImage(VkCommandBuffer p0, VkImage p1, VkImageLayout p2, const VkClearDepthStencilValue * p3, uint32_t p4, const VkImageSubresourceRange * p5) {
host::Scope scope(handleArena(p0));
try { implVkCmdClearDepthStencilImage(p0, p1, p2, p3, p4, p5); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyBuffer(VkCommandBuffer p0, VkBuffer p1, VkBuffer p2, uint32_t p3, const VkBufferCopy * p4) {
host::Scope scope(handleArena(p0));
try { implVkCmdCopyBuffer(p0, p1, p2, p3, p4); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyBufferToImage(VkCommandBuffer p0, VkBuffer p1, VkImage p2, VkImageLayout p3, uint32_t p4, const VkBufferImageCopy * p5) {
host::Scope scope(handleArena(p0));
try { implVkCmdCopyBufferToImage(p0, p1, p2, p3, p4, p5); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyImage(VkCommandBuffer p0, VkImage p1, VkImageLayout p2, VkImage p3, VkImageLayout p4, uint32_t p5, const VkImageCopy * p6) {
host::Scope scope(handleArena(p0));
try { implVkCmdCopyImage(p0, p1, p2, p3, p4, p5, p6); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyImageToBuffer(VkCommandBuffer p0, VkImage p1, VkImageLayout p2, VkBuffer p3, uint32_t p4, const VkBufferImageCopy * p5) {
host::Scope scope(handleArena(p0));
try { implVkCmdCopyImageToBuffer(p0, p1, p2, p3, p4, p5); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyQueryPoolResults(VkCommandBuffer p0, VkQueryPool p1, uint32_t p2, uint32_t p3, VkBuffer p4, VkDeviceSize p5, VkDeviceSize p6, VkQueryResultFlags p7) {
host::Scope scope(handleArena(p0));
try { implVkCmdCopyQueryPoolResults(p0, p1, p2, p3, p4, p5, p6, p7); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDispatch(VkCommandBuffer p0, uint32_t p1, uint32_t p2, uint32_t p3) {
host::Scope scope(handleArena(p0));
try { implVkCmdDispatch(p0, p1, p2, p3); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDispatchBase(VkCommandBuffer p0, uint32_t p1, uint32_t p2, uint32_t p3, uint32_t p4, uint32_t p5, uint32_t p6) {
host::Scope scope(handleArena(p0));
try { implVkCmdDispatchBase(p0, p1, p2, p3, p4, p5, p6); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDispatchBaseKHR(VkCommandBuffer p0, uint32_t p1, uint32_t p2, uint32_t p3, uint32_t p4, uint32_t p5, uint32_t p6) {
return vkCmdDispatchBase(p0, p1, p2, p3, p4, p5, p6);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDispatchIndirect(VkCommandBuffer p0, VkBuffer p1, VkDeviceSize p2) {
host::Scope scope(handleArena(p0));
try { implVkCmdDispatchIndirect(p0, p1, p2); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDraw(VkCommandBuffer p0, uint32_t p1, uint32_t p2, uint32_t p3, uint32_t p4) {
host::Scope scope(handleArena(p0));
try { implVkCmdDraw(p0, p1, p2, p3, p4); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawIndexed(VkCommandBuffer p0, uint32_t p1, uint32_t p2, uint32_t p3, int32_t p4, uint32_t p5) {
host::Scope scope(handleArena(p0));
try { implVkCmdDrawIndexed(p0, p1, p2, p3, p4, p5); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawIndexedIndirect(VkCommandBuffer p0, VkBuffer p1, VkDeviceSize p2, uint32_t p3, uint32_t p4) {
host::Scope scope(handleArena(p0));
try { implVkCmdDrawIndexedIndirect(p0, p1, p2, p3, p4); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDrawIndirect(VkCommandBuffer p0, VkBuffer p1, VkDeviceSize p2, uint32_t p3, uint32_t p4) {
host::Scope scope(handleArena(p0));
try { implVkCmdDrawIndirect(p0, p1, p2, p3, p4); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdEndQuery(VkCommandBuffer p0, VkQueryPool p1, uint32_t p2) {
host::Scope scope(handleArena(p0));
try { implVkCmdEndQuery(p0, p1, p2); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdEndRenderPass(VkCommandBuffer p0) {
host::Scope scope(handleArena(p0));
try { implVkCmdEndRenderPass(p0); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdEndRenderPass2(VkCommandBuffer p0, const VkSubpassEndInfo * p1) {
host::Scope scope(handleArena(p0));
try { implVkCmdEndRenderPass2(p0, p1); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdEndRenderPass2KHR(VkCommandBuffer p0, const VkSubpassEndInfo * p1) {
return vkCmdEndRenderPass2(p0, p1);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdExecuteCommands(VkCommandBuffer p0, uint32_t p1, const VkCommandBuffer * p2) {
host::Scope scope(handleArena(p0));
try { implVkCmdExecuteCommands(p0, p1, p2); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdFillBuffer(VkCommandBuffer p0, VkBuffer p1, VkDeviceSize p2, VkDeviceSize p3, uint32_t p4) {
host::Scope scope(handleArena(p0));
try { implVkCmdFillBuffer(p0, p1, p2, p3, p4); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdNextSubpass(VkCommandBuffer p0, VkSubpassContents p1) {
host::Scope scope(handleArena(p0));
try { implVkCmdNextSubpass(p0, p1); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdNextSubpass2(VkCommandBuffer p0, const VkSubpassBeginInfo * p1, const VkSubpassEndInfo * p2) {
host::Scope scope(handleArena(p0));
try { implVkCmdNextSubpass2(p0, p1, p2); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdNextSubpass2KHR(VkCommandBuffer p0, const VkSubpassBeginInfo * p1, const VkSubpassEndInfo * p2) {
return vkCmdNextSubpass2(p0, p1, p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdPipelineBarrier(VkCommandBuffer p0, VkPipelineStageFlags p1, VkPipelineStageFlags p2, VkDependencyFlags p3, uint32_t p4, const VkMemoryBarrier * p5, uint32_t p6, const VkBufferMemoryBarrier * p7, uint32_t p8, const VkImageMemoryBarrier * p9) {
host::Scope scope(handleArena(p0));
try { implVkCmdPipelineBarrier(p0, p1, p2, p3, p4, p5, p6, p7, p8, p9); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdPushConstants(VkCommandBuffer p0, VkPipelineLayout p1, VkShaderStageFlags p2, uint32_t p3, uint32_t p4, const void * p5) {
host::Scope scope(handleArena(p0));
try { implVkCmdPushConstants(p0, p1, p2, p3, p4, p5); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdResetEvent(VkCommandBuffer p0, VkEvent p1, VkPipelineStageFlags p2) {
host::Scope scope(handleArena(p0));
try { implVkCmdResetEvent(p0, p1, p2); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdResetQueryPool(VkCommandBuffer p0, VkQueryPool p1, uint32_t p2, uint32_t p3) {
host::Scope scope(handleArena(p0));
try { implVkCmdResetQueryPool(p0, p1, p2, p3); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdResolveImage(VkCommandBuffer p0, VkImage p1, VkImageLayout p2, VkImage p3, VkImageLayout p4, uint32_t p5, const VkImageResolve * p6) {
host::Scope scope(handleArena(p0));
try { implVkCmdResolveImage(p0, p1, p2, p3, p4, p5, p6); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetBlendConstants(VkCommandBuffer p0, const float * p1) {
host::Scope scope(handleArena(p0));
try { implVkCmdSetBlendConstants(p0, p1); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDepthBias(VkCommandBuffer p0, float p1, float p2, float p3) {
host::Scope scope(handleArena(p0));
try { implVkCmdSetDepthBias(p0, p1, p2, p3); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetDepthBounds(VkCommandBuffer p0, float p1, float p2) {
host::Scope scope(handleArena(p0));
try { implVkCmdSetDepthBounds(p0, p1, p2); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetEvent(VkCommandBuffer p0, VkEvent p1, VkPipelineStageFlags p2) {
host::Scope scope(handleArena(p0));
try { implVkCmdSetEvent(p0, p1, p2); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetLineWidth(VkCommandBuffer p0, float p1) {
host::Scope scope(handleArena(p0));
try { implVkCmdSetLineWidth(p0, p1); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetScissor(VkCommandBuffer p0, uint32_t p1, uint32_t p2, const VkRect2D * p3) {
host::Scope scope(handleArena(p0));
try { implVkCmdSetScissor(p0, p1, p2, p3); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetStencilCompareMask(VkCommandBuffer p0, VkStencilFaceFlags p1, uint32_t p2) {
host::Scope scope(handleArena(p0));
try { implVkCmdSetStencilCompareMask(p0, p1, p2); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetStencilReference(VkCommandBuffer p0, VkStencilFaceFlags p1, uint32_t p2) {
host::Scope scope(handleArena(p0));
try { implVkCmdSetStencilReference(p0, p1, p2); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetStencilWriteMask(VkCommandBuffer p0, VkStencilFaceFlags p1, uint32_t p2) {
host::Scope scope(handleArena(p0));
try { implVkCmdSetStencilWriteMask(p0, p1, p2); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdSetViewport(VkCommandBuffer p0, uint32_t p1, uint32_t p2, const VkViewport * p3) {
host::Scope scope(handleArena(p0));
try { implVkCmdSetViewport(p0, p1, p2, p3); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdUpdateBuffer(VkCommandBuffer p0, VkBuffer p1, VkDeviceSize p2, VkDeviceSize p3, const void * p4) {
host::Scope scope(handleArena(p0));
try { implVkCmdUpdateBuffer(p0, p1, p2, p3, p4); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdWaitEvents(VkCommandBuffer p0, uint32_t p1, const VkEvent * p2, VkPipelineStageFlags p3, VkPipelineStageFlags p4, uint32_t p5, const VkMemoryBarrier * p6, uint32_t p7, const VkBufferMemoryBarrier * p8, uint32_t p9, const VkImageMemoryBarrier * p10) {
host::Scope scope(handleArena(p0));
try { implVkCmdWaitEvents(p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdWriteTimestamp(VkCommandBuffer p0, VkPipelineStageFlagBits p1, VkQueryPool p2, uint32_t p3) {
host::Scope scope(handleArena(p0));
try { implVkCmdWriteTimestamp(p0, p1, p2, p3); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = commands.find(p0);
if (found != commands.end()) found->second.allocationFailed = true; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateBuffer(VkDevice p0, const VkBufferCreateInfo * p1, const VkAllocationCallbacks * p2, VkBuffer * p3) {
if (p3) *p3 = VK_NULL_HANDLE;
try {
  host::Creation creation(p2, handleArena(p0), VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
  try {
    const auto result = implVkCreateBuffer(p0, p1, p2, p3);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p3 && *p3) { implVkDestroyBuffer(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p3 && *p3) { implVkDestroyBuffer(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateBufferView(VkDevice p0, const VkBufferViewCreateInfo * p1, const VkAllocationCallbacks * p2, VkBufferView * p3) {
if (p3) *p3 = VK_NULL_HANDLE;
try {
  host::Creation creation(p2, handleArena(p0), VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
  try {
    const auto result = implVkCreateBufferView(p0, p1, p2, p3);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p3 && *p3) { implVkDestroyBufferView(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p3 && *p3) { implVkDestroyBufferView(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateCommandPool(VkDevice p0, const VkCommandPoolCreateInfo * p1, const VkAllocationCallbacks * p2, VkCommandPool * p3) {
if (p3) *p3 = VK_NULL_HANDLE;
try {
  host::Creation creation(p2, handleArena(p0), VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
  try {
    const auto result = implVkCreateCommandPool(p0, p1, p2, p3);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p3 && *p3) { implVkDestroyCommandPool(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p3 && *p3) { implVkDestroyCommandPool(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateDescriptorPool(VkDevice p0, const VkDescriptorPoolCreateInfo * p1, const VkAllocationCallbacks * p2, VkDescriptorPool * p3) {
if (p3) *p3 = VK_NULL_HANDLE;
try {
  host::Creation creation(p2, handleArena(p0), VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
  try {
    const auto result = implVkCreateDescriptorPool(p0, p1, p2, p3);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p3 && *p3) { implVkDestroyDescriptorPool(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p3 && *p3) { implVkDestroyDescriptorPool(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateDescriptorSetLayout(VkDevice p0, const VkDescriptorSetLayoutCreateInfo * p1, const VkAllocationCallbacks * p2, VkDescriptorSetLayout * p3) {
if (p3) *p3 = VK_NULL_HANDLE;
try {
  host::Creation creation(p2, handleArena(p0), VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
  try {
    const auto result = implVkCreateDescriptorSetLayout(p0, p1, p2, p3);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p3 && *p3) { implVkDestroyDescriptorSetLayout(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p3 && *p3) { implVkDestroyDescriptorSetLayout(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateDescriptorUpdateTemplate(VkDevice p0, const VkDescriptorUpdateTemplateCreateInfo * p1, const VkAllocationCallbacks * p2, VkDescriptorUpdateTemplate * p3) {
if (p3) *p3 = VK_NULL_HANDLE;
try {
  host::Creation creation(p2, handleArena(p0), VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
  try {
    const auto result = implVkCreateDescriptorUpdateTemplate(p0, p1, p2, p3);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p3 && *p3) { implVkDestroyDescriptorUpdateTemplate(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p3 && *p3) { implVkDestroyDescriptorUpdateTemplate(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateDescriptorUpdateTemplateKHR(VkDevice p0, const VkDescriptorUpdateTemplateCreateInfo * p1, const VkAllocationCallbacks * p2, VkDescriptorUpdateTemplate * p3) {
return vkCreateDescriptorUpdateTemplate(p0, p1, p2, p3);
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateDevice(VkPhysicalDevice p0, const VkDeviceCreateInfo * p1, const VkAllocationCallbacks * p2, VkDevice * p3) {
if (p3) *p3 = VK_NULL_HANDLE;
try {
  host::Creation creation(p2, handleArena(p0), VK_SYSTEM_ALLOCATION_SCOPE_DEVICE);
  try {
    const auto result = implVkCreateDevice(p0, p1, p2, p3);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p3 && *p3) { implVkDestroyDevice(*p3, p2); *p3 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p3 && *p3) { implVkDestroyDevice(*p3, p2); *p3 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateEvent(VkDevice p0, const VkEventCreateInfo * p1, const VkAllocationCallbacks * p2, VkEvent * p3) {
if (p3) *p3 = VK_NULL_HANDLE;
try {
  host::Creation creation(p2, handleArena(p0), VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
  try {
    const auto result = implVkCreateEvent(p0, p1, p2, p3);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p3 && *p3) { implVkDestroyEvent(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p3 && *p3) { implVkDestroyEvent(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateFence(VkDevice p0, const VkFenceCreateInfo * p1, const VkAllocationCallbacks * p2, VkFence * p3) {
if (p3) *p3 = VK_NULL_HANDLE;
try {
  host::Creation creation(p2, handleArena(p0), VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
  try {
    const auto result = implVkCreateFence(p0, p1, p2, p3);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p3 && *p3) { implVkDestroyFence(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p3 && *p3) { implVkDestroyFence(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateFramebuffer(VkDevice p0, const VkFramebufferCreateInfo * p1, const VkAllocationCallbacks * p2, VkFramebuffer * p3) {
if (p3) *p3 = VK_NULL_HANDLE;
try {
  host::Creation creation(p2, handleArena(p0), VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
  try {
    const auto result = implVkCreateFramebuffer(p0, p1, p2, p3);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p3 && *p3) { implVkDestroyFramebuffer(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p3 && *p3) { implVkDestroyFramebuffer(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateImage(VkDevice p0, const VkImageCreateInfo * p1, const VkAllocationCallbacks * p2, VkImage * p3) {
if (p3) *p3 = VK_NULL_HANDLE;
try {
  host::Creation creation(p2, handleArena(p0), VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
  try {
    const auto result = implVkCreateImage(p0, p1, p2, p3);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p3 && *p3) { implVkDestroyImage(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p3 && *p3) { implVkDestroyImage(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateImageView(VkDevice p0, const VkImageViewCreateInfo * p1, const VkAllocationCallbacks * p2, VkImageView * p3) {
if (p3) *p3 = VK_NULL_HANDLE;
try {
  host::Creation creation(p2, handleArena(p0), VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
  try {
    const auto result = implVkCreateImageView(p0, p1, p2, p3);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p3 && *p3) { implVkDestroyImageView(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p3 && *p3) { implVkDestroyImageView(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateInstance(const VkInstanceCreateInfo * p0, const VkAllocationCallbacks * p1, VkInstance * p2) {
if (p2) *p2 = VK_NULL_HANDLE;
try {
  host::Creation creation(p1, nullptr, VK_SYSTEM_ALLOCATION_SCOPE_INSTANCE);
  try {
    const auto result = implVkCreateInstance(p0, p1, p2);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p2 && *p2) { implVkDestroyInstance(*p2, p1); *p2 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p2 && *p2) { implVkDestroyInstance(*p2, p1); *p2 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreatePipelineCache(VkDevice p0, const VkPipelineCacheCreateInfo * p1, const VkAllocationCallbacks * p2, VkPipelineCache * p3) {
if (p3) *p3 = VK_NULL_HANDLE;
try {
  host::Creation creation(p2, handleArena(p0), VK_SYSTEM_ALLOCATION_SCOPE_CACHE);
  try {
    const auto result = implVkCreatePipelineCache(p0, p1, p2, p3);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p3 && *p3) { implVkDestroyPipelineCache(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p3 && *p3) { implVkDestroyPipelineCache(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreatePipelineLayout(VkDevice p0, const VkPipelineLayoutCreateInfo * p1, const VkAllocationCallbacks * p2, VkPipelineLayout * p3) {
if (p3) *p3 = VK_NULL_HANDLE;
try {
  host::Creation creation(p2, handleArena(p0), VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
  try {
    const auto result = implVkCreatePipelineLayout(p0, p1, p2, p3);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p3 && *p3) { implVkDestroyPipelineLayout(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p3 && *p3) { implVkDestroyPipelineLayout(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateQueryPool(VkDevice p0, const VkQueryPoolCreateInfo * p1, const VkAllocationCallbacks * p2, VkQueryPool * p3) {
if (p3) *p3 = VK_NULL_HANDLE;
try {
  host::Creation creation(p2, handleArena(p0), VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
  try {
    const auto result = implVkCreateQueryPool(p0, p1, p2, p3);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p3 && *p3) { implVkDestroyQueryPool(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p3 && *p3) { implVkDestroyQueryPool(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateRenderPass(VkDevice p0, const VkRenderPassCreateInfo * p1, const VkAllocationCallbacks * p2, VkRenderPass * p3) {
if (p3) *p3 = VK_NULL_HANDLE;
try {
  host::Creation creation(p2, handleArena(p0), VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
  try {
    const auto result = implVkCreateRenderPass(p0, p1, p2, p3);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p3 && *p3) { implVkDestroyRenderPass(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p3 && *p3) { implVkDestroyRenderPass(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateRenderPass2(VkDevice p0, const VkRenderPassCreateInfo2 * p1, const VkAllocationCallbacks * p2, VkRenderPass * p3) {
if (p3) *p3 = VK_NULL_HANDLE;
try {
  host::Creation creation(p2, handleArena(p0), VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
  try {
    const auto result = implVkCreateRenderPass2(p0, p1, p2, p3);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p3 && *p3) { implVkDestroyRenderPass(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p3 && *p3) { implVkDestroyRenderPass(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateRenderPass2KHR(VkDevice p0, const VkRenderPassCreateInfo2 * p1, const VkAllocationCallbacks * p2, VkRenderPass * p3) {
return vkCreateRenderPass2(p0, p1, p2, p3);
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateSampler(VkDevice p0, const VkSamplerCreateInfo * p1, const VkAllocationCallbacks * p2, VkSampler * p3) {
if (p3) *p3 = VK_NULL_HANDLE;
try {
  host::Creation creation(p2, handleArena(p0), VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
  try {
    const auto result = implVkCreateSampler(p0, p1, p2, p3);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p3 && *p3) { implVkDestroySampler(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p3 && *p3) { implVkDestroySampler(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateSemaphore(VkDevice p0, const VkSemaphoreCreateInfo * p1, const VkAllocationCallbacks * p2, VkSemaphore * p3) {
if (p3) *p3 = VK_NULL_HANDLE;
try {
  host::Creation creation(p2, handleArena(p0), VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
  try {
    const auto result = implVkCreateSemaphore(p0, p1, p2, p3);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p3 && *p3) { implVkDestroySemaphore(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p3 && *p3) { implVkDestroySemaphore(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateShaderModule(VkDevice p0, const VkShaderModuleCreateInfo * p1, const VkAllocationCallbacks * p2, VkShaderModule * p3) {
if (p3) *p3 = VK_NULL_HANDLE;
try {
  host::Creation creation(p2, handleArena(p0), VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
  try {
    const auto result = implVkCreateShaderModule(p0, p1, p2, p3);
    if (result == VK_SUCCESS) creation.commit();
    else { if (p3 && *p3) { implVkDestroyShaderModule(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } }
    return result;
  } catch (...) { if (p3 && *p3) { implVkDestroyShaderModule(p0, *p3, p2); *p3 = VK_NULL_HANDLE; } throw; }
} catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyBuffer(VkDevice p0, VkBuffer p1, const VkAllocationCallbacks * p2) {
auto *arena = handleArena(p1);
host::Scope scope(arena);
implVkDestroyBuffer(p0, p1, p2);
if (arena) arena->destroy(p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyBufferView(VkDevice p0, VkBufferView p1, const VkAllocationCallbacks * p2) {
auto *arena = handleArena(p1);
host::Scope scope(arena);
implVkDestroyBufferView(p0, p1, p2);
if (arena) arena->destroy(p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyCommandPool(VkDevice p0, VkCommandPool p1, const VkAllocationCallbacks * p2) {
auto *arena = handleArena(p1);
host::Scope scope(arena);
implVkDestroyCommandPool(p0, p1, p2);
if (arena) arena->destroy(p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyDescriptorPool(VkDevice p0, VkDescriptorPool p1, const VkAllocationCallbacks * p2) {
auto *arena = handleArena(p1);
host::Scope scope(arena);
implVkDestroyDescriptorPool(p0, p1, p2);
if (arena) arena->destroy(p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyDescriptorSetLayout(VkDevice p0, VkDescriptorSetLayout p1, const VkAllocationCallbacks * p2) {
auto *arena = handleArena(p1);
host::Scope scope(arena);
implVkDestroyDescriptorSetLayout(p0, p1, p2);
if (arena) arena->destroy(p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyDescriptorUpdateTemplate(VkDevice p0, VkDescriptorUpdateTemplate p1, const VkAllocationCallbacks * p2) {
auto *arena = handleArena(p1);
host::Scope scope(arena);
implVkDestroyDescriptorUpdateTemplate(p0, p1, p2);
if (arena) arena->destroy(p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyDescriptorUpdateTemplateKHR(VkDevice p0, VkDescriptorUpdateTemplate p1, const VkAllocationCallbacks * p2) {
return vkDestroyDescriptorUpdateTemplate(p0, p1, p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyDevice(VkDevice p0, const VkAllocationCallbacks * p1) {
auto *arena = handleArena(p0);
host::Scope scope(arena);
implVkDestroyDevice(p0, p1);
if (arena) arena->destroy(p1);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyEvent(VkDevice p0, VkEvent p1, const VkAllocationCallbacks * p2) {
auto *arena = handleArena(p1);
host::Scope scope(arena);
implVkDestroyEvent(p0, p1, p2);
if (arena) arena->destroy(p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyFence(VkDevice p0, VkFence p1, const VkAllocationCallbacks * p2) {
auto *arena = handleArena(p1);
host::Scope scope(arena);
implVkDestroyFence(p0, p1, p2);
if (arena) arena->destroy(p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyFramebuffer(VkDevice p0, VkFramebuffer p1, const VkAllocationCallbacks * p2) {
auto *arena = handleArena(p1);
host::Scope scope(arena);
implVkDestroyFramebuffer(p0, p1, p2);
if (arena) arena->destroy(p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyImage(VkDevice p0, VkImage p1, const VkAllocationCallbacks * p2) {
auto *arena = handleArena(p1);
host::Scope scope(arena);
implVkDestroyImage(p0, p1, p2);
if (arena) arena->destroy(p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyImageView(VkDevice p0, VkImageView p1, const VkAllocationCallbacks * p2) {
auto *arena = handleArena(p1);
host::Scope scope(arena);
implVkDestroyImageView(p0, p1, p2);
if (arena) arena->destroy(p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyInstance(VkInstance p0, const VkAllocationCallbacks * p1) {
auto *arena = handleArena(p0);
host::Scope scope(arena);
implVkDestroyInstance(p0, p1);
if (arena) arena->destroy(p1);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyPipeline(VkDevice p0, VkPipeline p1, const VkAllocationCallbacks * p2) {
auto *arena = handleArena(p1);
host::Scope scope(arena);
implVkDestroyPipeline(p0, p1, p2);
if (arena) arena->destroy(p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyPipelineCache(VkDevice p0, VkPipelineCache p1, const VkAllocationCallbacks * p2) {
auto *arena = handleArena(p1);
host::Scope scope(arena);
implVkDestroyPipelineCache(p0, p1, p2);
if (arena) arena->destroy(p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyPipelineLayout(VkDevice p0, VkPipelineLayout p1, const VkAllocationCallbacks * p2) {
auto *arena = handleArena(p1);
host::Scope scope(arena);
implVkDestroyPipelineLayout(p0, p1, p2);
if (arena) arena->destroy(p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyQueryPool(VkDevice p0, VkQueryPool p1, const VkAllocationCallbacks * p2) {
auto *arena = handleArena(p1);
host::Scope scope(arena);
implVkDestroyQueryPool(p0, p1, p2);
if (arena) arena->destroy(p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyRenderPass(VkDevice p0, VkRenderPass p1, const VkAllocationCallbacks * p2) {
auto *arena = handleArena(p1);
host::Scope scope(arena);
implVkDestroyRenderPass(p0, p1, p2);
if (arena) arena->destroy(p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroySampler(VkDevice p0, VkSampler p1, const VkAllocationCallbacks * p2) {
auto *arena = handleArena(p1);
host::Scope scope(arena);
implVkDestroySampler(p0, p1, p2);
if (arena) arena->destroy(p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroySemaphore(VkDevice p0, VkSemaphore p1, const VkAllocationCallbacks * p2) {
auto *arena = handleArena(p1);
host::Scope scope(arena);
implVkDestroySemaphore(p0, p1, p2);
if (arena) arena->destroy(p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyShaderModule(VkDevice p0, VkShaderModule p1, const VkAllocationCallbacks * p2) {
auto *arena = handleArena(p1);
host::Scope scope(arena);
implVkDestroyShaderModule(p0, p1, p2);
if (arena) arena->destroy(p2);
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkDeviceWaitIdle(VkDevice p0) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkDeviceWaitIdle(p0); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkEndCommandBuffer(VkCommandBuffer p0) {
host::Scope scope(handleArena(p0));
try { return implVkEndCommandBuffer(p0); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkEnumerateDeviceExtensionProperties(VkPhysicalDevice p0, const char * p1, uint32_t * p2, VkExtensionProperties * p3) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkEnumerateDeviceExtensionProperties(p0, p1, p2, p3); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkEnumerateDeviceLayerProperties(VkPhysicalDevice p0, uint32_t * p1, VkLayerProperties * p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkEnumerateDeviceLayerProperties(p0, p1, p2); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkEnumerateInstanceExtensionProperties(const char * p0, uint32_t * p1, VkExtensionProperties * p2) {
host::Scope scope(deviceArena(nullptr));
try { return implVkEnumerateInstanceExtensionProperties(p0, p1, p2); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkEnumerateInstanceLayerProperties(uint32_t * p0, VkLayerProperties * p1) {
host::Scope scope(deviceArena(nullptr));
try { return implVkEnumerateInstanceLayerProperties(p0, p1); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkEnumerateInstanceVersion(uint32_t * p0) {
host::Scope scope(deviceArena(nullptr));
try { return implVkEnumerateInstanceVersion(p0); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkEnumeratePhysicalDeviceGroups(VkInstance p0, uint32_t * p1, VkPhysicalDeviceGroupProperties * p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkEnumeratePhysicalDeviceGroups(p0, p1, p2); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkEnumeratePhysicalDeviceGroupsKHR(VkInstance p0, uint32_t * p1, VkPhysicalDeviceGroupProperties * p2) {
return vkEnumeratePhysicalDeviceGroups(p0, p1, p2);
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkEnumeratePhysicalDevices(VkInstance p0, uint32_t * p1, VkPhysicalDevice * p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkEnumeratePhysicalDevices(p0, p1, p2); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkFlushMappedMemoryRanges(VkDevice p0, uint32_t p1, const VkMappedMemoryRange * p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkFlushMappedMemoryRanges(p0, p1, p2); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkFreeCommandBuffers(VkDevice p0, VkCommandPool p1, uint32_t p2, const VkCommandBuffer * p3) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkFreeCommandBuffers(p0, p1, p2, p3); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = devices.find(p0);
if (found != devices.end()) found->second->lost = true;
stateChanged.notify_all(); }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkFreeDescriptorSets(VkDevice p0, VkDescriptorPool p1, uint32_t p2, const VkDescriptorSet * p3) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkFreeDescriptorSets(p0, p1, p2, p3); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkFreeMemory(VkDevice p0, VkDeviceMemory p1, const VkAllocationCallbacks * p2) {
auto *arena = handleArena(p1);
host::Scope scope(arena);
implVkFreeMemory(p0, p1, p2);
if (arena) arena->destroy(p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetBufferMemoryRequirements(VkDevice p0, VkBuffer p1, VkMemoryRequirements * p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetBufferMemoryRequirements(p0, p1, p2); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = devices.find(p0);
if (found != devices.end()) found->second->lost = true;
stateChanged.notify_all(); }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetBufferMemoryRequirements2(VkDevice p0, const VkBufferMemoryRequirementsInfo2 * p1, VkMemoryRequirements2 * p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetBufferMemoryRequirements2(p0, p1, p2); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = devices.find(p0);
if (found != devices.end()) found->second->lost = true;
stateChanged.notify_all(); }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetBufferMemoryRequirements2KHR(VkDevice p0, const VkBufferMemoryRequirementsInfo2 * p1, VkMemoryRequirements2 * p2) {
return vkGetBufferMemoryRequirements2(p0, p1, p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDeviceMemoryCommitment(VkDevice p0, VkDeviceMemory p1, VkDeviceSize * p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetDeviceMemoryCommitment(p0, p1, p2); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = devices.find(p0);
if (found != devices.end()) found->second->lost = true;
stateChanged.notify_all(); }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDeviceQueue(VkDevice p0, uint32_t p1, uint32_t p2, VkQueue * p3) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetDeviceQueue(p0, p1, p2, p3); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = devices.find(p0);
if (found != devices.end()) found->second->lost = true;
stateChanged.notify_all(); }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDeviceQueue2(VkDevice p0, const VkDeviceQueueInfo2 * p1, VkQueue * p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetDeviceQueue2(p0, p1, p2); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = devices.find(p0);
if (found != devices.end()) found->second->lost = true;
stateChanged.notify_all(); }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetEventStatus(VkDevice p0, VkEvent p1) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkGetEventStatus(p0, p1); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetFenceStatus(VkDevice p0, VkFence p1) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkGetFenceStatus(p0, p1); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetImageMemoryRequirements(VkDevice p0, VkImage p1, VkMemoryRequirements * p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetImageMemoryRequirements(p0, p1, p2); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = devices.find(p0);
if (found != devices.end()) found->second->lost = true;
stateChanged.notify_all(); }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetImageMemoryRequirements2(VkDevice p0, const VkImageMemoryRequirementsInfo2 * p1, VkMemoryRequirements2 * p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetImageMemoryRequirements2(p0, p1, p2); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = devices.find(p0);
if (found != devices.end()) found->second->lost = true;
stateChanged.notify_all(); }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetImageMemoryRequirements2KHR(VkDevice p0, const VkImageMemoryRequirementsInfo2 * p1, VkMemoryRequirements2 * p2) {
return vkGetImageMemoryRequirements2(p0, p1, p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetImageSparseMemoryRequirements(VkDevice p0, VkImage p1, uint32_t * p2, VkSparseImageMemoryRequirements * p3) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetImageSparseMemoryRequirements(p0, p1, p2, p3); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = devices.find(p0);
if (found != devices.end()) found->second->lost = true;
stateChanged.notify_all(); }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetImageSparseMemoryRequirements2(VkDevice p0, const VkImageSparseMemoryRequirementsInfo2 * p1, uint32_t * p2, VkSparseImageMemoryRequirements2 * p3) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetImageSparseMemoryRequirements2(p0, p1, p2, p3); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = devices.find(p0);
if (found != devices.end()) found->second->lost = true;
stateChanged.notify_all(); }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetImageSparseMemoryRequirements2KHR(VkDevice p0, const VkImageSparseMemoryRequirementsInfo2 * p1, uint32_t * p2, VkSparseImageMemoryRequirements2 * p3) {
return vkGetImageSparseMemoryRequirements2(p0, p1, p2, p3);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetImageSubresourceLayout(VkDevice p0, VkImage p1, const VkImageSubresource * p2, VkSubresourceLayout * p3) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetImageSubresourceLayout(p0, p1, p2, p3); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = devices.find(p0);
if (found != devices.end()) found->second->lost = true;
stateChanged.notify_all(); }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceFeatures(VkPhysicalDevice p0, VkPhysicalDeviceFeatures * p1) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetPhysicalDeviceFeatures(p0, p1); } catch (const std::bad_alloc &) {  }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceFeatures2(VkPhysicalDevice p0, VkPhysicalDeviceFeatures2 * p1) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetPhysicalDeviceFeatures2(p0, p1); } catch (const std::bad_alloc &) {  }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceFeatures2KHR(VkPhysicalDevice p0, VkPhysicalDeviceFeatures2 * p1) {
return vkGetPhysicalDeviceFeatures2(p0, p1);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceFormatProperties(VkPhysicalDevice p0, VkFormat p1, VkFormatProperties * p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetPhysicalDeviceFormatProperties(p0, p1, p2); } catch (const std::bad_alloc &) {  }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceFormatProperties2(VkPhysicalDevice p0, VkFormat p1, VkFormatProperties2 * p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetPhysicalDeviceFormatProperties2(p0, p1, p2); } catch (const std::bad_alloc &) {  }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceFormatProperties2KHR(VkPhysicalDevice p0, VkFormat p1, VkFormatProperties2 * p2) {
return vkGetPhysicalDeviceFormatProperties2(p0, p1, p2);
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceImageFormatProperties(VkPhysicalDevice p0, VkFormat p1, VkImageType p2, VkImageTiling p3, VkImageUsageFlags p4, VkImageCreateFlags p5, VkImageFormatProperties * p6) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkGetPhysicalDeviceImageFormatProperties(p0, p1, p2, p3, p4, p5, p6); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceImageFormatProperties2(VkPhysicalDevice p0, const VkPhysicalDeviceImageFormatInfo2 * p1, VkImageFormatProperties2 * p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkGetPhysicalDeviceImageFormatProperties2(p0, p1, p2); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPhysicalDeviceImageFormatProperties2KHR(VkPhysicalDevice p0, const VkPhysicalDeviceImageFormatInfo2 * p1, VkImageFormatProperties2 * p2) {
return vkGetPhysicalDeviceImageFormatProperties2(p0, p1, p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceMemoryProperties(VkPhysicalDevice p0, VkPhysicalDeviceMemoryProperties * p1) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetPhysicalDeviceMemoryProperties(p0, p1); } catch (const std::bad_alloc &) {  }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceMemoryProperties2(VkPhysicalDevice p0, VkPhysicalDeviceMemoryProperties2 * p1) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetPhysicalDeviceMemoryProperties2(p0, p1); } catch (const std::bad_alloc &) {  }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceMemoryProperties2KHR(VkPhysicalDevice p0, VkPhysicalDeviceMemoryProperties2 * p1) {
return vkGetPhysicalDeviceMemoryProperties2(p0, p1);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceProperties(VkPhysicalDevice p0, VkPhysicalDeviceProperties * p1) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetPhysicalDeviceProperties(p0, p1); } catch (const std::bad_alloc &) {  }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceProperties2(VkPhysicalDevice p0, VkPhysicalDeviceProperties2 * p1) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetPhysicalDeviceProperties2(p0, p1); } catch (const std::bad_alloc &) {  }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceProperties2KHR(VkPhysicalDevice p0, VkPhysicalDeviceProperties2 * p1) {
return vkGetPhysicalDeviceProperties2(p0, p1);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceQueueFamilyProperties(VkPhysicalDevice p0, uint32_t * p1, VkQueueFamilyProperties * p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetPhysicalDeviceQueueFamilyProperties(p0, p1, p2); } catch (const std::bad_alloc &) {  }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceQueueFamilyProperties2(VkPhysicalDevice p0, uint32_t * p1, VkQueueFamilyProperties2 * p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetPhysicalDeviceQueueFamilyProperties2(p0, p1, p2); } catch (const std::bad_alloc &) {  }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceQueueFamilyProperties2KHR(VkPhysicalDevice p0, uint32_t * p1, VkQueueFamilyProperties2 * p2) {
return vkGetPhysicalDeviceQueueFamilyProperties2(p0, p1, p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceSparseImageFormatProperties(VkPhysicalDevice p0, VkFormat p1, VkImageType p2, VkSampleCountFlagBits p3, VkImageUsageFlags p4, VkImageTiling p5, uint32_t * p6, VkSparseImageFormatProperties * p7) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetPhysicalDeviceSparseImageFormatProperties(p0, p1, p2, p3, p4, p5, p6, p7); } catch (const std::bad_alloc &) {  }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceSparseImageFormatProperties2(VkPhysicalDevice p0, const VkPhysicalDeviceSparseImageFormatInfo2 * p1, uint32_t * p2, VkSparseImageFormatProperties2 * p3) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetPhysicalDeviceSparseImageFormatProperties2(p0, p1, p2, p3); } catch (const std::bad_alloc &) {  }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceSparseImageFormatProperties2KHR(VkPhysicalDevice p0, const VkPhysicalDeviceSparseImageFormatInfo2 * p1, uint32_t * p2, VkSparseImageFormatProperties2 * p3) {
return vkGetPhysicalDeviceSparseImageFormatProperties2(p0, p1, p2, p3);
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPipelineCacheData(VkDevice p0, VkPipelineCache p1, size_t * p2, void * p3) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkGetPipelineCacheData(p0, p1, p2, p3); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetQueryPoolResults(VkDevice p0, VkQueryPool p1, uint32_t p2, uint32_t p3, size_t p4, void * p5, VkDeviceSize p6, VkQueryResultFlags p7) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkGetQueryPoolResults(p0, p1, p2, p3, p4, p5, p6, p7); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetRenderAreaGranularity(VkDevice p0, VkRenderPass p1, VkExtent2D * p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkGetRenderAreaGranularity(p0, p1, p2); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = devices.find(p0);
if (found != devices.end()) found->second->lost = true;
stateChanged.notify_all(); }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetSemaphoreCounterValue(VkDevice p0, VkSemaphore p1, uint64_t * p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkGetSemaphoreCounterValue(p0, p1, p2); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetSemaphoreCounterValueKHR(VkDevice p0, VkSemaphore p1, uint64_t * p2) {
return vkGetSemaphoreCounterValue(p0, p1, p2);
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkInvalidateMappedMemoryRanges(VkDevice p0, uint32_t p1, const VkMappedMemoryRange * p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkInvalidateMappedMemoryRanges(p0, p1, p2); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkMapMemory(VkDevice p0, VkDeviceMemory p1, VkDeviceSize p2, VkDeviceSize p3, VkMemoryMapFlags p4, void ** p5) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkMapMemory(p0, p1, p2, p3, p4, p5); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkMergePipelineCaches(VkDevice p0, VkPipelineCache p1, uint32_t p2, const VkPipelineCache * p3) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkMergePipelineCaches(p0, p1, p2, p3); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkQueueSubmit(VkQueue p0, uint32_t p1, const VkSubmitInfo * p2, VkFence p3) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkQueueSubmit(p0, p1, p2, p3); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkQueueWaitIdle(VkQueue p0) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkQueueWaitIdle(p0); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkResetEvent(VkDevice p0, VkEvent p1) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkResetEvent(p0, p1); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkResetFences(VkDevice p0, uint32_t p1, const VkFence * p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkResetFences(p0, p1, p2); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkResetQueryPool(VkDevice p0, VkQueryPool p1, uint32_t p2, uint32_t p3) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkResetQueryPool(p0, p1, p2, p3); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = devices.find(p0);
if (found != devices.end()) found->second->lost = true;
stateChanged.notify_all(); }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkResetQueryPoolEXT(VkDevice p0, VkQueryPool p1, uint32_t p2, uint32_t p3) {
return vkResetQueryPool(p0, p1, p2, p3);
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkSetEvent(VkDevice p0, VkEvent p1) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkSetEvent(p0, p1); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkSignalSemaphore(VkDevice p0, const VkSemaphoreSignalInfo * p1) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkSignalSemaphore(p0, p1); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkSignalSemaphoreKHR(VkDevice p0, const VkSemaphoreSignalInfo * p1) {
return vkSignalSemaphore(p0, p1);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkTrimCommandPoolKHR(VkDevice p0, VkCommandPool p1, VkCommandPoolTrimFlags p2) {
return vkTrimCommandPool(p0, p1, p2);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkUnmapMemory(VkDevice p0, VkDeviceMemory p1) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkUnmapMemory(p0, p1); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = devices.find(p0);
if (found != devices.end()) found->second->lost = true;
stateChanged.notify_all(); }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkUpdateDescriptorSetWithTemplate(VkDevice p0, VkDescriptorSet p1, VkDescriptorUpdateTemplate p2, const void * p3) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkUpdateDescriptorSetWithTemplate(p0, p1, p2, p3); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = devices.find(p0);
if (found != devices.end()) found->second->lost = true;
stateChanged.notify_all(); }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkUpdateDescriptorSetWithTemplateKHR(VkDevice p0, VkDescriptorSet p1, VkDescriptorUpdateTemplate p2, const void * p3) {
return vkUpdateDescriptorSetWithTemplate(p0, p1, p2, p3);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkUpdateDescriptorSets(VkDevice p0, uint32_t p1, const VkWriteDescriptorSet * p2, uint32_t p3, const VkCopyDescriptorSet * p4) {
host::Scope scope(deviceArena(handleArena(p0)));
try { implVkUpdateDescriptorSets(p0, p1, p2, p3, p4); } catch (const std::bad_alloc &) { std::lock_guard lock(stateMutex);
const auto found = devices.find(p0);
if (found != devices.end()) found->second->lost = true;
stateChanged.notify_all(); }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkWaitForFences(VkDevice p0, uint32_t p1, const VkFence * p2, VkBool32 p3, uint64_t p4) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkWaitForFences(p0, p1, p2, p3, p4); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkWaitSemaphores(VkDevice p0, const VkSemaphoreWaitInfo * p1, uint64_t p2) {
host::Scope scope(deviceArena(handleArena(p0)));
try { return implVkWaitSemaphores(p0, p1, p2); } catch (const std::bad_alloc &) { return VK_ERROR_OUT_OF_HOST_MEMORY; }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkWaitSemaphoresKHR(VkDevice p0, const VkSemaphoreWaitInfo * p1, uint64_t p2) {
return vkWaitSemaphores(p0, p1, p2);
}