#pragma once

#include "MatriceClass.h"

#include <algorithm>
#include <iostream>
#include <thread>
#include <vector>

using namespace std;

void Matrix::run(Matrix& filter, semafor& barrier, int start, int stop) {
  map<string, double> cache;
  for (int a = start; a < stop; a++) {
    int i = a / cols;
    int j = a % cols;
    setCache(ref(filter), ref(cache), i, j);
  }
  barrier.wait();
  for (int a = start; a < stop; a++) {
    int i = a / cols;
    int j = a % cols;
    matrix[i][j] = applyFilter(ref(filter), ref(cache), i, j);
  }
}

void Matrix::destroy() {
  for (int i = 0; i < lines; i++) {
    delete[] matrix[i];
  }
}

void Matrix::deepCopy(Matrix& dest) {
  dest.destroy();
  dest = Matrix(lines, cols, matrix);
}

string Matrix::getIdentifier(int i, int j) {
  string res;
  res += 'l';
  res += to_string(i);
  res += 'c';
  res += to_string(j);
  return res;
}

void Matrix::setCache(Matrix& filter, map<string, double>& cache, int i,
                      int j) {
  int n = filter.lines;
  int m = filter.cols;
  int startY = max(0, i - n / 2);
  int stopY = min(lines - 1, i + n / 2);
  int startX = max(0, j - m / 2);
  int stopX = min(cols - 1, j + m / 2);
  for (int k = startY; k <= stopY; k++) {
    for (int t = startX; t <= stopX; t++) {
      double current = getCell(k, t);
      string id = this->getIdentifier(k, t);
      cache[id] = current;
    }
  }
}

Matrix::Matrix(int lines, int cols) {
  this->lines = lines;
  this->cols = cols;
  matrix = new double*[lines];
  for (int i = 0; i < lines; ++i) {
    matrix[i] = new double[cols];
  }
}

Matrix::Matrix(int lines, int cols, double** storage) {
  this->lines = lines;
  this->cols = cols;
  matrix = new double*[lines];
  for (int i = 0; i < lines; ++i) {
    matrix[i] = new double[cols];
    memcpy(matrix[i], storage[i], cols * sizeof(double));
  }
}

void Matrix::setMatrixFromArray(vector<double> arr) {
  for (int i = 0; i < lines; i++) {
    for (int j = 0; j < cols; j++) {
      matrix[i][j] = arr[i * cols + j];
    }
  }
}

void Matrix::printMatrix() {
  for (int i = 0; i < lines; i++) {
    for (int j = 0; j < cols; j++) {
      cout << matrix[i][j] << " ";
    }
    cout << '\n';
  }
}

double Matrix::getCell(int i, int j) {
  if (i < 0 || i >= lines) return 0;
  if (j < 0 || j >= cols) return 0;
  return matrix[i][j];
}

double Matrix::applyFilter(Matrix& filter, map<string, double>& cache, int i,
                           int j) {
  int n = filter.lines;
  int m = filter.cols;
  int startY = max(0, i - n / 2);
  int stopY = min(lines - 1, i + n / 2);
  int startX = max(0, j - m / 2);
  int stopX = min(cols - 1, j + m / 2);
  double aux = 0;
  int a = 0;
  for (int k = startY; k <= stopY; k++) {
    int b = 0;
    for (int t = startX; t <= stopX; t++) {
      double fil = filter.getCell(a, b);
      string id = getIdentifier(k, t);
      double current = cache[id];
      aux = aux + fil * current;
      b++;
    }
    a++;
  }
  return aux;
}

double Matrix::applyLinearFilter(Matrix& filter, int i, int j) {
  int n = filter.lines;
  int m = filter.cols;
  int startY = max(0, i - n / 2);
  int stopY = min(lines - 1, i + n / 2);
  int startX = max(0, j - m / 2);
  int stopX = min(cols - 1, j + m / 2);
  double aux = 0;
  int a = 0;
  for (int k = startY; k <= stopY; k++) {
    int b = 0;
    for (int t = startX; t <= stopX; t++) {
      double fil = filter.getCell(a, b);
      double current = getCell(k, t);
      aux = aux + fil * current;
      b++;
    }
    a++;
  }
  return aux;
}

Matrix Matrix::filterMatrix(Matrix& filter) {
  Matrix aux = Matrix(lines, cols);
  for (int i = 0; i < lines; i++) {
    for (int j = 0; j < cols; j++) {
      aux.matrix[i][j] = applyLinearFilter(ref(filter), i, j);
    }
  }
  return aux;
}

void Matrix::filterMatrixParallel(Matrix& filter, int nrThreads) {
  vector<thread> threads(nrThreads);
  semafor barrier(nrThreads);
  int m = cols * lines;
  int chunk_size = m / nrThreads;
  int reminder = m % nrThreads;
  int start = 0, end;
  for (int i = 0; i < nrThreads; i++) {
    end = start + chunk_size;
    if (reminder > 0) {
      end += 1;
      reminder -= 1;
    }
    threads[i] =
        thread(&Matrix::run, this, ref(filter), ref(barrier), start, end);
    start = end;
  }
  for (int i = 0; i < nrThreads; i++) {
    threads[i].join();
  }
}

bool Matrix::isEqual(Matrix mat2) {
  if (lines != mat2.lines || cols != mat2.cols) return false;
  for (int i = 0; i < lines; i++) {
    for (int j = 0; j < cols; j++) {
      if (matrix[i][j] != mat2.matrix[i][j]) {
        cout << '\n' << i << ' ' << j << '\n';
        return false;
      }
    }
  }
  return true;
}