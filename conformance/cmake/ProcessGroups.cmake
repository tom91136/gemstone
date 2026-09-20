# Apply small, checked patches to the pinned reproc sources. POSIX children form
# process groups before exec; Windows children enter kill-on-close jobs before
# their first instruction. reproc still owns pipes, polling and bounded waits.
function(conformance_patch_reproc source)
  set(path "${source}/reproc/src/process.posix.c")
  file(READ "${path}" content)
  if(NOT content MATCHES "conformance process group")
    set(before "    if (options.working_directory != NULL) {")
    string(FIND "${content}" "${before}" found)
    if(found EQUAL -1)
      message(FATAL_ERROR "Pinned reproc POSIX process integration changed")
    endif()
    string(REPLACE "${before}" [=[    // conformance process group: include every descendant in timeout cleanup.
    if (setpgid(0, 0) < 0) {
      r = -errno;
      goto child;
    }
    if (options.working_directory != NULL) {]=] content "${content}")
    string(REPLACE "kill(process, SIGTERM)" "kill(-process, SIGTERM)" content "${content}")
    string(REPLACE "kill(process, SIGKILL)" "kill(-process, SIGKILL)" content "${content}")
    file(WRITE "${path}" "${content}")
  endif()
  set(path "${source}/reproc/src/process.windows.c")
  file(READ "${path}" content)
  if(NOT content MATCHES "conformance process jobs")
    set(before "  *process = info.hProcess;")
    string(FIND "${content}" "${before}" found)
    if(found EQUAL -1)
      message(FATAL_ERROR "Pinned reproc Windows process integration changed")
    endif()
    string(REPLACE "true, CREATION_FLAGS, env_wstring" "true, CREATION_FLAGS | CREATE_SUSPENDED, env_wstring" content "${content}")
    string(REPLACE "${before}" [=[  HANDLE job = CreateJobObjectW(NULL, NULL);
  JOBOBJECT_EXTENDED_LIMIT_INFORMATION limits = {0};
  limits.BasicLimitInformation.LimitFlags = JOB_OBJECT_LIMIT_KILL_ON_JOB_CLOSE;
  struct conformance_job *entry = malloc(sizeof(*entry));
  if (!job || !entry || !SetInformationJobObject(job, JobObjectExtendedLimitInformation, &limits, sizeof(limits)) ||
      !AssignProcessToJobObject(job, info.hProcess)) {
    r = -(int) GetLastError();
    if (r == 0) r = -ERROR_NOT_ENOUGH_MEMORY;
    TerminateProcess(info.hProcess, REPROC_SIGKILL);
    if (job) CloseHandle(job);
    free(entry);
    CloseHandle(info.hProcess);
    goto finish;
  }
  entry->process = info.hProcess;
  entry->job = job;
  AcquireSRWLockExclusive(&conformance_jobs_lock);
  entry->next = conformance_jobs;
  conformance_jobs = entry;
  ReleaseSRWLockExclusive(&conformance_jobs_lock);
  ResumeThread(info.hThread);
  *process = info.hProcess;]=] content "${content}")
    set(before "int process_start(")
    string(FIND "${content}" "${before}" found)
    if(found EQUAL -1)
      message(FATAL_ERROR "Pinned reproc Windows process_start changed")
    endif()
    string(REPLACE "${before}" [=[// conformance process jobs, closed together with their process handles.
struct conformance_job { HANDLE process, job; struct conformance_job *next; };
static struct conformance_job *conformance_jobs;
static SRWLOCK conformance_jobs_lock = SRWLOCK_INIT;

int process_start(]=] content "${content}")
    string(REPLACE "  return handle_destroy(process);" [=[  AcquireSRWLockExclusive(&conformance_jobs_lock);
  struct conformance_job **entry = &conformance_jobs;
  while (*entry && (*entry)->process != process) entry = &(*entry)->next;
  if (*entry) {
    struct conformance_job *old = *entry;
    *entry = old->next;
    CloseHandle(old->job);
    free(old);
  }
  ReleaseSRWLockExclusive(&conformance_jobs_lock);
  return handle_destroy(process);]=] content "${content}")
    file(WRITE "${path}" "${content}")
  endif()
endfunction()
