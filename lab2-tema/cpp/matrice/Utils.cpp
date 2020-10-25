#pragma once

#include "Utils.h"

Utils::Utils()
    : mat(Matrix(0, 0)),
      filter(Matrix(0, 0)),
      resLinear(Matrix(0, 0)),
      resParallel(Matrix(0, 0)) {}

void Utils::createFilter(int i, int j) {
  filter = Matrix(i, j);
  vector<double> fillArr(i * j);
  fill(fillArr.begin(), fillArr.end(), 0.5);
  filter.setMatrixFromArray(fillArr);
}

void Utils::createMatrix(int i, int j, string file) {
  mat = Matrix(i, j);
  vector<double> a = help.readDoubleFile(file);
  mat.setMatrixFromArray(a);
}

void Utils::allTest2() {
  test2(2);
  test2(4);
  test2(8);
  test2(16);
}

void Utils::allTest3() {
  test3(2);
  test3(4);
  test3(8);
  test3(16);
}

void Utils::allTest4() {
  test4(2);
  test4(4);
  test4(8);
  test4(16);
}

int64_t Utils::filterLinearDuration() {
  auto start_t = high_resolution_clock::now();
  resLinear = mat.filterMatrix(ref(filter));
  auto end_t = high_resolution_clock::now();
  auto duration = duration_cast<nanoseconds>(end_t - start_t);
  return duration.count();
}

int64_t Utils::filterParallelDuration(int threads) {
  auto start_t = high_resolution_clock::now();
  resParallel.filterMatrixParallel(ref(filter), threads);
  auto end_t = high_resolution_clock::now();
  auto duration = duration_cast<nanoseconds>(end_t - start_t);
  return duration.count();
}

int64_t Utils::averageParallel(int threads, int n) {
  int64_t sum = 0;
  for (int i = 0; i < n; i++) {
    mat.deepCopy(ref(resParallel));
    int64_t duration = filterParallelDuration(threads);
    sum += duration;
  }
  return sum / n;
}

int64_t Utils::averageLinear(int n) {
  int64_t sum = 0;
  for (int i = 0; i < n; i++) {
    int64_t duration = filterLinearDuration();
    sum += duration;
  }
  return sum / n;
}

void Utils::test1(int threads) {
  cout << "Test 1; Threads=" << threads << "\n";
  int n = 10, m = 10;
  createFilter(3, 3);
  createMatrix(n, m, "test1.txt");
  signed long long lin = averageLinear(5);
  signed long long par = averageParallel(threads, 5);
  if (!resLinear.isEqual(resParallel))
    throw runtime_error("Results not equal!");
  printComparisons(lin, par);
}

void Utils::test2(int threads) {
  cout << "Test 2; Threads=" << threads << "\n";
  int n = 1000, m = 1000;
  createFilter(5, 5);
  createMatrix(n, m, "test2.txt");
  signed long long lin = averageLinear(5);
  signed long long par = averageParallel(threads, 5);
  if (!resLinear.isEqual(resParallel))
    throw runtime_error("Results not equal!");
  printComparisons(lin, par);
}

void Utils::test3(int threads) {
  cout << "Test 3; Threads=" << threads << "\n";
  int n = 10, m = 10000;
  createFilter(5, 5);
  createMatrix(n, m, "test3.txt");
  signed long long lin = averageLinear(5);
  signed long long par = averageParallel(threads, 5);
  if (!resLinear.isEqual(resParallel))
    throw runtime_error("Results not equal!");
  printComparisons(lin, par);
}

void Utils::test4(int threads) {
  cout << "Test 4; Threads=" << threads << "\n";
  int n = 10000, m = 10;
  createFilter(5, 5);
  createMatrix(n, m, "test4.txt");
  signed long long lin = averageLinear(5);
  signed long long par = averageParallel(threads, 5);
  if (!resLinear.isEqual(resParallel))
    throw runtime_error("Results not equal!");
  printComparisons(lin, par);
}

void Utils::printComparisons(signed long long linear,
                             signed long long parallel) {
  cout << "Linear=" << linear << "\n";
  cout << "Parallel=" << parallel << "\n";
  if (linear < parallel)
    cout << "Linear is faster"
         << "\n";
  else
    cout << "Parallel is faster"
         << "\n";
}