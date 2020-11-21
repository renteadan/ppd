#include <mpi.h>
#include <stdio.h>
#include <time.h>

#include <algorithm>
#include <fstream>
#include <iostream>
#include <random>

using namespace std;

void printVectorChunk(int* a, int size) {
  for (int i = 0; i < size; i++) {
    cout << a[i];
  }
}

int main1(int argc, char** argv) {
  // Initialize the MPI environment
  MPI_Init(NULL, NULL);

  int *a, *b, *c, start, end, transport = 0, size = 0;

  // Get the number of processes
  int world_size;
  MPI_Comm_size(MPI_COMM_WORLD, &world_size);

  // Get the rank of the process
  int world_rank;
  MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);

  MPI_Status status;

  if (world_rank == 0) {
    string line, line2;
    ifstream f("nr1.txt");
    ifstream f2("nr2.txt");
    f >> line;
    f2 >> line2;
    if (line.size() > line2.size())
      size = line.size();
    else
      size = line2.size();
    f.close();
    f2.close();
    a = new int[size]();
    b = new int[size]();
    c = new int[size]();
    for (int i = line.size() - 1; i >= 0; i--) {
      a[size - 1 - i] = (int)line[i] - '0';
    }

    for (int i = line2.size() - 1; i >= 0; i--) {
      b[size - 1 - i] = (int)line2[i] - '0';
    }

    int nrThreads = world_size - 1, start = 0, end;
    int chunk_size = size / nrThreads;
    int reminder = size % nrThreads;

    MPI_Bcast(&size, sizeof(size), MPI_INT, 0, MPI_COMM_WORLD);

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
    printVectorChunk(a, size);
    cout << "+\n";
    printVectorChunk(b, size);
    cout << "\n-----------=\n";
    if (transport) {
      cout << transport;
    }
    printVectorChunk(c, size);
  } else {
    a = new int[size]();
    b = new int[size]();
    c = new int[size]();
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

int main2(int argc, char** argv) {
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
  if (world_rank == 0) {
    string line, line2;
    ifstream f("nr1.txt");
    ifstream f2("nr2.txt");
    f >> line;
    f2 >> line2;
    if (line.size() > line2.size())
      size = line.size();
    else
      size = line2.size();
    f.close();
    f2.close();
    a = new int[size]();
    b = new int[size]();
    c = new int[size]();
    for (int i = line.size() - 1; i >= 0; i--) {
      a[size - 1 - i] = (int)line[i] - '0';
    }

    for (int i = line2.size() - 1; i >= 0; i--) {
      b[size - 1 - i] = (int)line2[i] - '0';
    }
  }

  MPI_Bcast(&size, sizeof(size), MPI_INT, 0, MPI_COMM_WORLD);
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
  MPI_Scatterv(b, sendCounts, displs, MPI_INT, localB, currentSize, MPI_INT, 0,
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
             MPI_COMM_WORLD, &status);

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

int main(int argc, char** argv) {
  // Initialize the MPI environment
  MPI_Init(NULL, NULL);

  int *a = NULL, *b = NULL, *c = NULL, start, end, transport = 0, size = 0;

  // Get the number of processes
  int world_size;
  MPI_Comm_size(MPI_COMM_WORLD, &world_size);

  // Get the rank of the process
  int world_rank;
  MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);

  MPI_Status status;

  if (world_rank == 0) {
    string line, line2;
    ifstream f("nr1.txt");
    ifstream f2("nr2.txt");
    f >> line;
    f2 >> line2;
    if (line.size() > line2.size())
      size = line.size();
    else
      size = line2.size();
    f.close();
    f2.close();
    a = new int[size]();
    b = new int[size]();
    c = new int[size]();
    for (int i = line.size() - 1; i >= 0; i--) {
      a[size - 1 - i] = (int)line[i] - '0';
    }

    for (int i = line2.size() - 1; i >= 0; i--) {
      b[size - 1 - i] = (int)line2[i] - '0';
    }
  }


  MPI_Bcast(&size, sizeof(size), MPI_INT, 0, MPI_COMM_WORLD);

  if (world_rank == 0) {

    int nrThreads = world_size - 1, start = 0, end;
    int chunk_size = size / nrThreads;
    int reminder = size % nrThreads;

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
    printVectorChunk(a, size);
    cout << "+\n";
    printVectorChunk(b, size);
    cout << "\n-----------=\n";
    if (transport) {
      cout << transport;
    }
    printVectorChunk(c, size);
  } else {
    a = new int[size]();
    b = new int[size]();
    c = new int[size]();
    MPI_Recv(&start, sizeof(start), MPI_INT, 0, 0, MPI_COMM_WORLD, &status);
    MPI_Recv(&end, sizeof(end), MPI_INT, 0, 0, MPI_COMM_WORLD, &status);
    MPI_Recv(a + start, end - start, MPI_INT, 0, 0, MPI_COMM_WORLD, &status);
    MPI_Recv(b + start, end - start, MPI_INT, 0, 0, MPI_COMM_WORLD, &status);
    for (int i = end - 1; i >= start; i--) {
      int sum = a[i] + b[i] + transport;
      c[i] = sum % 10;
      transport = sum / 10;
    }
    MPI_Request sendRequest = MPI_REQUEST_NULL, recRequest = MPI_REQUEST_NULL;
    int newTransport = 0;

    if (world_rank < world_size - 1) {
      MPI_Irecv(&newTransport, sizeof(newTransport), MPI_INT, world_rank + 1, 0,
                MPI_COMM_WORLD, &recRequest);

      MPI_Wait(&recRequest, &status);
    }

    for (int i = end - 1; i >= start && newTransport; i--) {
      int sum = c[i] + newTransport;
      c[i] = sum % 10;
      newTransport = sum / 10;
    }

    int toSend = transport || newTransport;

    if (world_rank > 0) {
      MPI_Isend(&toSend, sizeof(toSend), MPI_INT, world_rank - 1, 0,
                MPI_COMM_WORLD, &sendRequest);
      MPI_Wait(&sendRequest, &status);
    }
    MPI_Send(&start, sizeof(start), MPI_INT, 0, 0, MPI_COMM_WORLD);
    MPI_Send(&end, sizeof(end), MPI_INT, 0, 0, MPI_COMM_WORLD);
    MPI_Send(c + start, end - start, MPI_INT, 0, 0, MPI_COMM_WORLD);
  }

  MPI_Finalize();

  return 0;
}