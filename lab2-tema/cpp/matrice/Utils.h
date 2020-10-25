#pragma once

#include"MatriceClass.h"
#include "..\..\..\helpers\cpp\Helpers\Helpers.cpp"
#include <chrono>
#include <ctime>

using namespace std;
using namespace std::chrono;

class Utils {
 private:
  Matrix mat, filter, resLinear, resParallel;
  Helper help;
  void createFilter(int i, int j);
  void createMatrix(int i, int j, string file);
  int64_t filterLinearDuration();
  int64_t filterParallelDuration(int threads);
  signed long long averageParallel(int threads, int n);
  signed long long averageLinear(int n);
  void printComparisons(signed long long linear, signed long long parallel);
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
