#include <mpi.h>
#include <stdio.h>
#include <time.h>
#include <iostream>
#include <random>
#include <algorithm>
#include<fstream>

const int n = 10;

using namespace std;
void printVector(int* a) {
  for (int i = 0; i < n; i++) {
    cout << a[i];
  }
}

void printVectorChunk(int* a, int size) {
  for (int i = 0; i < size; i++) {
    cout << a[i];
  }
}

void putDigits(int* digits, int number) {

  int i = n - 1;
  while (number) {
    int digit = number % 10;
    digits[i] = digit;
    number /= 10;
    i--;
  }
}

int main2(int argc, char** argv) {
  // Initialize the MPI environment
  MPI_Init(NULL, NULL);

  int a[n]{0}, b[n]{0}, c[n]{0}, n1, n2;

  // Get the number of processes
  int world_size;
  MPI_Comm_size(MPI_COMM_WORLD, &world_size);

  // Get the rank of the process
  int world_rank;
  MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);

  int nrThreads = world_size - 1, start = 0, end;
  int chunk_size = n / nrThreads;
  int reminder = n % nrThreads;
  int transport = 0;
  MPI_Status status;

  if (world_rank == 0) {
    cout << "n1=";
    cin >> n1;
    cout << "n2=";
    cin >> n2;
    putDigits(a, n1);
    putDigits(b, n2);

    for (int i = 1; i < world_size; i++) {
      end = start + chunk_size;
      if (reminder > 0) {
        end += 1;
        reminder -= 1;
      }
      MPI_Send(&start, sizeof(start), MPI_INT, i, 0, MPI_COMM_WORLD);
      MPI_Send(&end, sizeof(end), MPI_INT, i, 0, MPI_COMM_WORLD);
      MPI_Send(a + start, end - start, MPI_INT, i, 0, MPI_COMM_WORLD);
      MPI_Send(b + start, end - start, MPI_INT, i, 0, MPI_COMM_WORLD);
      start = end;
    }
    MPI_Recv(&transport, sizeof(transport), MPI_INT, world_rank + 1, 0,
             MPI_COMM_WORLD, &status);
    for (int i = 1; i < world_size; i++) {
      MPI_Recv(&start, sizeof(start), MPI_INT, i, 0, MPI_COMM_WORLD, &status);
      MPI_Recv(&end, sizeof(end), MPI_INT, i, 0, MPI_COMM_WORLD, &status);
      MPI_Recv(c + start, end - start, MPI_INT, i, 0, MPI_COMM_WORLD, &status);
    }
    printVector(a);
    cout << "+\n";
    printVector(b);
    cout << "\n-----------=\n";
    if (transport) {
      cout << transport;
    }
    printVector(c);
  } else {
    MPI_Recv(&start, sizeof(start), MPI_INT, 0, 0, MPI_COMM_WORLD, &status);
    MPI_Recv(&end, sizeof(end), MPI_INT, 0, 0, MPI_COMM_WORLD, &status);
    MPI_Recv(a + start, end - start, MPI_INT, 0, 0, MPI_COMM_WORLD, &status);
    MPI_Recv(b + start, end - start, MPI_INT, 0, 0, MPI_COMM_WORLD, &status);
    for (int i = end - 1; i >= start; i--) {
      int sum = a[i] + b[i] + transport;
      c[i] = sum % 10;
      transport = sum / 10;
    }
    MPI_Status status;

    int newTransport = 0;

    if (world_rank < world_size - 1)
      MPI_Recv(&newTransport, sizeof(newTransport), MPI_INT, world_rank + 1, 0,
               MPI_COMM_WORLD, &status);

    for (int i = end - 1; i >= start && newTransport; i--) {
      int sum = c[i] + newTransport;
      c[i] = sum % 10;
      newTransport = sum / 10;
    }

    int toSend = transport || newTransport;

    if (world_rank > 0)
      MPI_Send(&toSend, sizeof(toSend), MPI_INT, world_rank - 1, 0,
               MPI_COMM_WORLD);
    MPI_Send(&start, sizeof(start), MPI_INT, 0, 0, MPI_COMM_WORLD);
    MPI_Send(&end, sizeof(end), MPI_INT, 0, 0, MPI_COMM_WORLD);
    MPI_Send(c + start, end - start, MPI_INT, 0, 0, MPI_COMM_WORLD);
  }

  MPI_Finalize();

  return 0;
}

int main(int argc, char** argv) {

  int world_size, world_rank;
  // Initialize the MPI environment
  MPI_Init(NULL, NULL);

  // Get the number of processes
  MPI_Comm_size(MPI_COMM_WORLD, &world_size);

  // Get the rank of the process
  MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);

  int size = 0;
  int* a = NULL;
  int* b = NULL;
  int* c = NULL;
  string line;
  if (world_rank == 0) {
    ifstream f("nr1.txt");
    f >> line;
    size = line.size();
    f.close();
    a = new int[size]();
    b = new int[size]();
    c = new int[size]();
    for (int i = 0; i < size; i++) {
      a[i] = (int)line[i] - '0';
    }

    for (int i = 0; i < size; i++) {
      b[i] = (int)line[i] - '0';
    }
  }

  MPI_Bcast(&size, sizeof(size), MPI_INT,0, MPI_COMM_WORLD);
  int chunk_size = size / world_size;
  int reminder = size % world_size;
  int* displs = new int[world_size]();
  int* sendCounts = new int[world_size]();
  int start = 0, end;
  for (int i = 0; i < world_size; i++) {
    end = start + chunk_size;
    if (reminder > 0) {
      end += 1;
      reminder -= 1;
    }
    sendCounts[i] = end - start;
    displs[i] = start;
    start = end;
  }

  int currentSize = sendCounts[world_rank];
  int* localA = new int[currentSize]();
  int* localB = new int[currentSize]();
  int* localC = new int[currentSize]();
  int transport = 0;
  MPI_Scatterv(a, sendCounts, displs, MPI_INT, localA, currentSize, MPI_INT, 0,
              MPI_COMM_WORLD);
  MPI_Scatterv(b, sendCounts, displs, MPI_INT, localB, currentSize,
               MPI_INT, 0,
              MPI_COMM_WORLD);

  for (int i = currentSize - 1; i >= 0; i--) {
    int sum = localA[i] + localB[i] + transport;
    localC[i] = sum % 10;
    transport = sum / 10;
  }
  MPI_Status status;

  int newTransport = 0;

  if (world_rank < world_size - 1)
    MPI_Recv(&newTransport, sizeof(newTransport), MPI_INT, world_rank + 1, 0,
             MPI_COMM_WORLD,
             &status);

  for (int i = currentSize - 1; i >= 0 && newTransport; i--) {
    int sum = localC[i] + newTransport;
    localC[i] = sum % 10;
    newTransport = sum / 10;
  }

  int toSend = transport || newTransport;

  if (world_rank > 0)
    MPI_Send(&toSend, sizeof(toSend), MPI_INT, world_rank - 1, 0,
            MPI_COMM_WORLD);

  MPI_Gatherv(localC, currentSize, MPI_INT, c, sendCounts, displs, MPI_INT, 0,
             MPI_COMM_WORLD);

  if (world_rank == 0) {
    printVectorChunk(a, size);
    cout << "+\n";
    printVectorChunk(b, size);
    cout << "\n-----------=\n";
    if (transport) {
      cout << transport;
    }
    printVectorChunk(c, size);
  }

  MPI_Finalize();

  return 0;
}