#include <omp.h>
#include <time.h>

#include <algorithm>
#include <chrono>
#include <iostream>
#include <random>
#include <vector>

using namespace std;
using namespace std::chrono;
#define VECTOR_SIZE 100000000

void printVector(vector<int> pr) {
  for (auto &item : pr) {
    cout << item << ' ';
  }
  cout << '\n';
}

int main() {
  vector<int> a(VECTOR_SIZE), b(VECTOR_SIZE), c(VECTOR_SIZE), d(VECTOR_SIZE),
      e(VECTOR_SIZE), f(VECTOR_SIZE);
  srand(time(NULL));
  for (int i = 0; i < VECTOR_SIZE; i++) {
    a[i] = rand() % 10;
    b[i] = rand() % 10;
  }

  auto start = high_resolution_clock::now();

  for (int i = 0; i < VECTOR_SIZE; i++) {
    c[i] = a[i] + b[i];
  }
  auto endtime = high_resolution_clock::now();
  auto duration = duration_cast<microseconds>(endtime - start);
  cout << "secvential=" << duration.count() << '\n';

  int i = 0;
  omp_set_num_threads(8);
  start = high_resolution_clock::now();
#pragma omp parallel for private(i) schedule(static)
  for (i = 0; i < VECTOR_SIZE; i++) {
    d[i] = a[i] + b[i];
  }
  endtime = high_resolution_clock::now();
  duration = duration_cast<microseconds>(endtime - start);
  cout << "static=" << duration.count() << '\n';

  start = high_resolution_clock::now();
#pragma omp parallel for private(i) schedule(dynamic, 1000)
  for (i = 0; i < VECTOR_SIZE; i++) {
    e[i] = a[i] + b[i];
  }
  endtime = high_resolution_clock::now();
  duration = duration_cast<microseconds>(endtime - start);
  cout << "dynamic=" << duration.count() << '\n';

  start = high_resolution_clock::now();
#pragma omp parallel for private(i) schedule(guided, 1000)
  for (i = 0; i < VECTOR_SIZE; i++) {
    f[i] = a[i] + b[i];
  }
  endtime = high_resolution_clock::now();
  duration = duration_cast<microseconds>(endtime - start);
  cout << "guided=" << duration.count() << "\n\n\n\n\n";

  int suma = 0;
  start = high_resolution_clock::now();
  for (i = 0; i < VECTOR_SIZE; i++) {
    suma += a[i];
  }
  endtime = high_resolution_clock::now();
  duration = duration_cast<microseconds>(endtime - start);
  cout << "suma=" << duration.count() << '\n';

  suma = 0;
  start = high_resolution_clock::now();
#pragma omp parallel for private(i), reduction(+ : suma)
  for (i = 0; i < VECTOR_SIZE; i++) {
    suma += a[i];
  }
  endtime = high_resolution_clock::now();
  duration = duration_cast<microseconds>(endtime - start);
  cout << "suma parallel=" << duration.count() << '\n';

  return 0;
}