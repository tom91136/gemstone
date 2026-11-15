cmake_minimum_required(VERSION 3.25)
include("${CMAKE_CURRENT_LIST_DIR}/../cmake/ContainerBuild.cmake")

execute_process(COMMAND mktemp -d /tmp/container-image-tag-test-XXXXXX
  OUTPUT_VARIABLE directory OUTPUT_STRIP_TRAILING_WHITESPACE COMMAND_ERROR_IS_FATAL ANY)
set(path "${directory}/Containerfile")
set(stages base middle leaf consumer unrelated)

set(containerfile [=[
ARG BASE=ubuntu:24.04
FROM ${BASE} AS base
RUN echo base

FROM base AS middle
RUN echo middle

FROM middle AS leaf
RUN echo leaf

FROM ${BASE} AS consumer
COPY --from=base /payload /payload

FROM ${BASE} AS unrelated
RUN echo unrelated
]=])

function(write_containerfile content)
  file(WRITE "${path}" "${content}")
endfunction()

function(tags_of output)
  set(tags)
  foreach(stage IN LISTS stages)
    container_image_tag(tag REPOSITORY localhost/container-image-tag-test FILE "${path}" INPUTS "${path}" STAGE "${stage}")
    list(APPEND tags "${tag}")
  endforeach()
  set(${output} "${tags}" PARENT_SCOPE)
endfunction()

function(check_changed before after expected)
  set(changed)
  list(LENGTH stages count)
  math(EXPR last "${count} - 1")
  foreach(index RANGE ${last})
    list(GET stages ${index} stage)
    list(GET before ${index} old_tag)
    list(GET after ${index} new_tag)
    if(NOT old_tag STREQUAL new_tag)
      list(APPEND changed "${stage}")
    endif()
  endforeach()
  if(NOT "${changed}" STREQUAL "${expected}")
    message(FATAL_ERROR "edit changed [${changed}], expected [${expected}]")
  endif()
endfunction()

write_containerfile("${containerfile}")
tags_of(original)

string(REPLACE "RUN echo leaf" "RUN echo leaf-2" edited "${containerfile}")
write_containerfile("${edited}")
tags_of(edited_tags)
check_changed("${original}" "${edited_tags}" "leaf")

string(REPLACE "RUN echo base" "RUN echo base-2" edited "${containerfile}")
write_containerfile("${edited}")
tags_of(edited_tags)
check_changed("${original}" "${edited_tags}" "base;middle;leaf;consumer")

string(REPLACE "RUN echo unrelated" "RUN echo unrelated-2" edited "${containerfile}")
write_containerfile("${edited}")
tags_of(edited_tags)
check_changed("${original}" "${edited_tags}" "unrelated")

string(REPLACE "ARG BASE=ubuntu:24.04" "ARG BASE=ubuntu:25.04" edited "${containerfile}")
write_containerfile("${edited}")
tags_of(edited_tags)
check_changed("${original}" "${edited_tags}" "base;middle;leaf;consumer;unrelated")

file(REMOVE_RECURSE "${directory}")
