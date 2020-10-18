// Helpers.cpp : This file contains the 'main' function. Program execution
// begins and ends there.
//

#include <time.h>

#include <fstream>
#include <iostream>
#include <sstream>
#include <string>
#include <vector>
using namespace std;

class Helper {
 public:
  void writeFile(string fileName, int min, int max, int size) {
    ofstream file(fileName);
    srand((unsigned)time(0));
    for (int i = 0; i < size; i++) {
      int randomNumber = (rand() % (max - min)) + min;
      file << randomNumber << " ";
    }

    // Close the file
    file.close();
  }

  vector<int> readIntFile(string fileName) {
    ifstream infile(fileName);
    string line;
    vector<int> numbersInt;
    while (getline(infile, line)) {
      istringstream iss(line);
      int n;
      while (iss >> n) {
        numbersInt.push_back(n);
      }
    }
    return numbersInt;
  }

  vector<float> readFloatFile(string fileName) {
    ifstream infile(fileName);
    string line;
    vector<float> numbersFloat;
    while (getline(infile, line)) {
      istringstream iss(line);
      float n;
      while (iss >> n) {
        numbersFloat.push_back(n);
      }
    }
    return numbersFloat;
  }

  vector<double> readDoubleFile(string fileName) {
    ifstream infile(fileName);
    string line;
    vector<double> numbersFloat;
    while (getline(infile, line)) {
      istringstream iss(line);
      double n;
      while (iss >> n) {
        numbersFloat.push_back(n);
      }
    }
    return numbersFloat;
  }

  bool intFilesEqual(string file1, string file2) {
    vector<int> a = readIntFile(file1);
    vector<int> b = readIntFile(file2);
    if (a.size() != b.size()) return false;
    for (size_t i = 0; i < a.size(); i++) {
      if (a[i] != b[i]) return false;
    }
    return true;
  }

  bool floatFilesEqual(string file1, string file2) {
    vector<float> a = readFloatFile(file1);
    vector<float> b = readFloatFile(file2);
    if (a.size() != b.size()) return false;
    for (size_t i = 0; i < a.size(); i++) {
      if (a[i] != b[i]) return false;
    }
    return true;
  }
};