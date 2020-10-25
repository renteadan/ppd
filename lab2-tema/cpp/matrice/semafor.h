#pragma once
#include <condition_variable>

class semafor {
 private:
  std::mutex m;
  std::condition_variable cv;
  int counter;
  int waiting;
  int thread_count;

 public:
  semafor(int);
  void wait();
};
