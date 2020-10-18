#pragma once

#include "Utils.h"

Utils::Utils() : mat(Matrix(0, 0)), filter(Matrix(0, 0)) {}

void Utils::createFile(int size) {
  help.writeFile("date.txt", 1, 1000, size);
  newFile = false;
}

void Utils::createFilter(int i, int j) {
  filter = Matrix(i, j);
  vector<double> fillArr(i * j);
  fill(fillArr.begin(), fillArr.end(), 0.5);
  filter.setMatrixFromArray(fillArr);
}

void Utils::createMatrix(int i, int j, string file) {
  mat = Matrix(i, j);
  vector<double> a = help.readDoubleFile("date.txt");
  mat.setMatrixFromArray(a);
}

void Utils::allTest2() {
  newFile = true;
  test2(2);
  test2(4);
  test2(8);
  test2(16);
}

void Utils::allTest3() {
  newFile = true;
  test3(2);
  test3(4);
  test3(8);
  test3(16);
}

void Utils::allTest4() {
  newFile = true;
  test4(2);
  test4(4);
  test4(8);
  test4(16);
}

nanoseconds Utils::filterLinearDuration() {
  auto start_t = high_resolution_clock::now();
  mat.filterMatrix(filter);
  auto end_t = high_resolution_clock::now();
  auto duration = duration_cast<nanoseconds>(end_t - start_t);
  return duration;
}

nanoseconds Utils::filterParallelDuration(int threads) {
  auto start_t = high_resolution_clock::now();
  mat.filterMatrixParallel(filter, threads);
  auto end_t = high_resolution_clock::now();
  auto duration = duration_cast<nanoseconds>(end_t - start_t);
  return duration;
}

long long Utils::averageParallel(int threads, int n) {
  nanoseconds sum;
  for (int i = 0; i < n; i++) {
    nanoseconds duration = filterParallelDuration(threads);
    sum += duration;
  }
  return sum.count() / n;
}

long long Utils::averageLinear(int n) {
  nanoseconds sum;
  for (int i = 0; i < n; i++) {
    nanoseconds duration = filterLinearDuration();
    sum += duration;
  }
  return sum.count() / n;
}

void Utils::test1(int threads) {
  cout << "Test 1; Threads=" << threads << "\n";
  int n = 10, m = 10;
  createFile(n * m);
  createFilter(3, 3);
  createMatrix(n, m, "date.txt");
  long long lin = averageLinear(5);
  long long par = averageParallel(4, 5);
  printComparisons(lin, par);
}

void Utils::test2(int threads) {
  cout << "Test 2; Threads=" << threads << "\n";
  int n = 1000, m = 1000;
  if (newFile) createFile(n * m);
  createFilter(5, 5);
  createMatrix(n, m, "date.txt");
  long lin = averageLinear(5);
  long par = averageParallel(4, 5);
  printComparisons(lin, par);
}

void Utils::test3(int threads) {
  cout << "Test 3; Threads=" << threads << "\n";
  int n = 10, m = 10000;
  if (newFile) createFile(n * m);
  createFilter(5, 5);
  createMatrix(n, m, "date.txt");
  long lin = averageLinear(5);
  long par = averageParallel(4, 5);
  printComparisons(lin, par);
}

void Utils::test4(int threads) {
  cout << "Test 4; Threads=" << threads << "\n";
  int n = 10000, m = 10;
  if (newFile) createFile(n * m);
  createFilter(5, 5);
  createMatrix(n, m, "date.txt");
  long lin = averageLinear(5);
  long par = averageParallel(4, 5);
  printComparisons(lin, par);
}

void Utils::printComparisons(long long linear, long long parallel) {
  cout << "Linear=" << linear << "\n";
  cout << "Parallel=" << parallel << "\n";
  if (linear < parallel)
    cout << "Linear is faster"
         << "\n";
  else
    cout << "Parallel is faster"
         << "\n";
}