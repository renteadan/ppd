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
  bool newFile = true;
  void createFilter(int i, int j);
  void createMatrix(int i, int j, string file);
  nanoseconds filterLinearDuration();
  nanoseconds filterParallelDuration(int threads);
  long long averageParallel(int threads, int n);
  long long averageLinear(int n);
  void printComparisons(long long linear, long long parallel);
 public:
  Utils();
  void createFile(int size);
  void allTest2();
  void allTest3();
  void allTest4();
  void test1(int threads);
  void test2(int threads);
  void test3(int threads);
  void test4(int threads);
};
