#pragma once

#include"MatriceClass.h"
#include "..\..\..\helpers\cpp\Helpers\Helpers.cpp";
#include <chrono>
#include <ctime>

using namespace std;
using namespace std::chrono;

class Utils {
 private:
  Matrix mat, filter;
  Helper help;
  void createFilter(int i, int j);
  void createMatrix(int i, int j, string file);
  int64_t filterLinearDuration();
  int64_t filterParallelDuration(int threads);
  int64_t averageParallel(int threads, int n);
  int64_t averageLinear(int n);
  void printComparisons(int64_t linear, int64_t parallel);
 public:
  Utils();
  void allTest2();
  void allTest3();
  void allTest4();
  void test1(int threads);
  void test2(int threads);
  void test3(int threads);
  void test4(int threads);
};
