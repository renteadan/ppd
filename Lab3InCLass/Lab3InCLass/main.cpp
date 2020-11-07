#include <mpi.h>
#include <stdio.h>
#include <time.h>
#include <iostream>
#include <random>

const int n = 10;
int world_size, world_rank;

using namespace std;
void printVector(int* a) {
  for (int i = 0; i < n; i++) {
    cout << a[i] << ' ';
  }
  cout << '\n';
}

void printVectorChuck(int* a, int size) {
  cout << "Rank=" << world_rank << "\n"; 
  for (int i = 0; i < size; i++) {
    cout<<a[i] << ' ';
  }
  cout << '\n';
}

int main2(int argc, char** argv) {
  // Initialize the MPI environment
  MPI_Init(NULL, NULL);

  int a[n], b[n], c[n], limit = 5;

  // Get the number of processes
  int world_size;
  MPI_Comm_size(MPI_COMM_WORLD, &world_size);

  // Get the rank of the process
  int world_rank;
  MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);

  int nrThreads = world_size - 1, start = 0, end;
  int chunk_size = n / nrThreads;
  int reminder = n % nrThreads;
  MPI_Status status;

  srand(time(NULL));

  if (world_rank == 0) {
    for (int i = 0; i < n; i++) {
      a[i] = rand() % limit;
      b[i] = rand() % limit;
    }

    for (int i = 1; i < world_size; i++) {
      end = start + chunk_size;
      if (reminder > 0) {
        end += 1;
        reminder -= 1;
      }
      MPI_Send(&start, sizeof(start), MPI_INT, i, 0, MPI_COMM_WORLD);
      MPI_Send(&end, sizeof(end), MPI_INT, i, 0, MPI_COMM_WORLD);
      MPI_Send(a + start, end - start, MPI_INT, i, 0,
               MPI_COMM_WORLD);
      MPI_Send(b + start, end - start, MPI_INT, i, 0,
               MPI_COMM_WORLD);
      start = end;
    }
    for (int i = 1; i < world_size; i++) {
      MPI_Recv(&start, sizeof(start), MPI_INT, i, 0, MPI_COMM_WORLD, &status);
      MPI_Recv(&end, sizeof(end), MPI_INT, i, 0, MPI_COMM_WORLD, &status);
      MPI_Recv(c + start, end - start, MPI_INT, i, 0,
               MPI_COMM_WORLD, &status);
    }
    printVector(a);
    printVector(b);
    printVector(c);
  } else {
    MPI_Recv(&start, sizeof(start), MPI_INT, 0, 0, MPI_COMM_WORLD, &status);
    MPI_Recv(&end, sizeof(end), MPI_INT, 0, 0, MPI_COMM_WORLD, &status);
    MPI_Recv(a + start, end - start, MPI_INT, 0, 0,
             MPI_COMM_WORLD, &status);
    MPI_Recv(b + start, end - start, MPI_INT, 0, 0,
             MPI_COMM_WORLD, &status);
    for (int i = start; i < end; i++) {
      c[i] = a[i] + b[i];
    }
    MPI_Send(&start, sizeof(start), MPI_INT, 0, 0, MPI_COMM_WORLD);
    MPI_Send(&end, sizeof(end), MPI_INT, 0, 0, MPI_COMM_WORLD);
    MPI_Send(c + start, end - start, MPI_INT, 0, 0,
             MPI_COMM_WORLD);
  }

  MPI_Finalize();

  return 0;
}

int main(int argc, char** argv) {
  // Initialize the MPI environment
  MPI_Init(NULL, NULL);

  int a[n], b[n], c[n], limit = 5;

  // Get the number of processes
  MPI_Comm_size(MPI_COMM_WORLD, &world_size);

  // Get the rank of the process
  MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);

  srand(time(NULL));

  if (world_rank == 0) {
    for (int i = 0; i < n; i++) {
      a[i] = rand() % limit;
      b[i] = rand() % limit;
    }
  }
  int chunk_size = n / world_size;
  if (n % chunk_size) {
    chunk_size++;
  }

  int* localA = new int[chunk_size];
  int* localB = new int[chunk_size];
  int* localC = new int[chunk_size];

  MPI_Scatter(a, chunk_size, MPI_INT, localA, chunk_size, MPI_INT, 0,
              MPI_COMM_WORLD);
  MPI_Scatter(b, chunk_size, MPI_INT, localB, chunk_size, MPI_INT, 0,
              MPI_COMM_WORLD);

  for (int i = 0; i < chunk_size; i++) {
    localC[i] = localA[i] + localB[i];
  }

  MPI_Gather(localC, chunk_size, MPI_INT, c, chunk_size, MPI_INT, 0,
             MPI_COMM_WORLD);

  if (world_rank == 0) {
    printVector(a);
    printVector(b);
    printVector(c);
  }

  MPI_Finalize();

  return 0;
}