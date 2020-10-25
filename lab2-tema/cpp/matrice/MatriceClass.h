#pragma once


#include<vector>
#include<map>
#include<string>
#include"semafor.h"
using namespace std;

class Matrix {
 public:
  double** matrix;
  int lines, cols;
  Matrix(int lines, int cols);
  Matrix(int lines, int cols, double**);
  void setMatrixFromArray(vector<double>);
  void printMatrix();
  double getCell(int i, int j);
  double applyFilter(Matrix& filter, map<string, double>&, int i, int j);
  double applyLinearFilter(Matrix& filter, int i, int j);
  Matrix filterMatrix(Matrix& filter);
  void filterMatrixParallel(Matrix& filter, int nrThreads);
  bool isEqual(Matrix mat2);
  void run(Matrix& filter,semafor&, int start, int stop);
  void deepCopy(Matrix&);
  string getIdentifier(int i, int j);
  void setCache(Matrix& filter, map<string, double>&, int i, int j);
  void destroy();
};
