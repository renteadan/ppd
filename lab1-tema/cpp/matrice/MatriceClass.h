#pragma once


#include<vector>

using namespace std;

class Matrix {
 public:
  double** matrix;
  int lines, cols;
  Matrix(int lines, int cols);
  void setMatrixFromArray(vector<double>);
  void printMatrix();
  double getCell(int i, int j);
  double applyFilter(Matrix& filter, int i, int j);
  Matrix filterMatrix(Matrix& filter);
  Matrix filterMatrixParallel(Matrix& filter, int nrThreads);
  bool isEqual(Matrix mat2);
  void run(Matrix& res, Matrix& filter, int start, int stop);
};
