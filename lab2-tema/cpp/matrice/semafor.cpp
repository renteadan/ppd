#include "semafor.h"

    semafor::semafor(int count) : thread_count(count), counter(0), waiting(0) {}

void semafor::wait() {
  std::unique_lock<std::mutex> lk(m);
  ++counter;
  ++waiting;
  cv.wait(lk, [&] { return counter >= thread_count; });
  cv.notify_one();
  --waiting;
  if (waiting == 0) {
    counter = 0;
  }
  lk.unlock();
}