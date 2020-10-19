// matrice.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

#include <iostream>
#include"Utils.h"

using namespace std;

int main() {
  Utils util;
  util.test1(4);
  util.newFile = true;
  util.allTest2();
  util.newFile = true;
  util.allTest3();
  util.newFile = true;
  util.allTest4();
}
