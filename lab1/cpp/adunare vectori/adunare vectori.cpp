// adunare vectori.cpp : This file contains the 'main' function. Program
// execution begins and ends there.
//

#include <time.h>
#include <chrono>
#include <iostream>
#include <thread>
#include <vector>
#include <ctime>

using namespace std;
using namespace std::chrono;
void run(int start, int end, vector<int>& a, vector<int>& b, vector<int> &c) {
  for (int i = start; i < end; i++) {
    c[i] = a[i] + b[i];
  }
}

void print(vector<int> a) {
  for (auto x : a) {
    cout << x << " ";
  }
  cout << '\n';
}

int main() {
  int n = 1000000000, max = 1000;
  vector<int> a(n), b(n), c(n), d(n);
  srand(time(NULL));
  for (int i = 0; i < n; i++) {
    a[i] = rand() % max + 1;
    b[i] = rand() % max + 1;
  }
  auto startTime = high_resolution_clock::now();
  for (int i = 0; i < n; i++) {
    c[i] = a[i] + b[i];
  }
  auto endTime = high_resolution_clock::now();
  auto duration = duration_cast<milliseconds>(endTime - startTime);
  cout << duration.count()<< " secvential\n";
  int p = 13, reminder = n % p, start = 0, chunkSize = n / p, end;
  vector<thread> threads(p);
  startTime = high_resolution_clock::now();
  for (int i = 0; i < p; i++) {
    end = start + chunkSize;
    if (reminder > 0) {
      end += 1;
      reminder -= 1;
    }
    threads[i] = thread(run, start, end, ref(a), ref(b), ref(d));
    start = end;
  }
  for (int i = 0; i < p; i++) {
    threads[i].join();
  }
  endTime = high_resolution_clock::now();
  duration = duration_cast<milliseconds>(endTime - startTime);
  cout << duration.count()<< " paralel\n";
  //print(a);
  //print(b);
  //print(c);
  //print(d);
}