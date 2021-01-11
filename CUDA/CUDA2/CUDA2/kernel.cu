/*
 * Copyright 1993-2015 NVIDIA Corporation.  All rights reserved.
 *
 * Please refer to the NVIDIA end user license agreement (EULA) associated
 * with this source code for terms and conditions that govern your use of
 * this software. Any use, reproduction, disclosure, or distribution of
 * this software and related documentation outside the terms of the EULA
 * is strictly prohibited.
 *
 */

/*
 * This sample demonstrates how use texture fetches in CUDA
 *
 * This sample takes an input PGM image (image_filename) and generates
 * an output PGM image (image_filename_out).  This CUDA kernel performs
 * a simple 2D transform (rotation) on the texture coordinates (u,v).
 */

// Includes, system
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#ifdef _WIN32
#define WINDOWS_LEAN_AND_MEAN
#define NOMINMAX
#include <windows.h>
#endif

// Includes CUDA
#include <cuda_runtime.h>

// Utilities and timing functions
#include "helpers/helper_functions.h"  // includes cuda.h and cuda_runtime_api.h

// CUDA helper functions
#include "helpers/helper_cuda.h"  // helper functions for CUDA error check

// Define the files that are to be save and the reference images for validation
// const char *imageFilename = "lena_bw.pgm";
const char *imageFilename = "test.pgm";
// const char *refFilename = "ref_rotated.pgm";

const char *sampleName = "simpleTexture";

////////////////////////////////////////////////////////////////////////////////
// Constants

float tx = 0.5f, ty = 0.5f;  // image translation
float scale = 1.0f;          // image scale
float cx, cy;                // image centre

// Auto-Verification Code
bool testResult = true;

////////////////////////////////////////////////////////////////////////////////
//! Transform an image using texture lookups
//! @param outputData  output data in global memory
////////////////////////////////////////////////////////////////////////////////
__device__ __host__ float lerp(float a, float b, float t) {
  return a + t * (b - a);
}

// higher-precision 2D bilinear lookup  // texture data type, return type
__device__ float tex2DBilinear(const cudaTextureObject_t tex, float x,
                               float y) {
  x -= 0.5f;
  y -= 0.5f;
  float px = floorf(x);  // integer position
  float py = floorf(y);
  float fx = x - px;  // fractional position
  float fy = y - py;
  px += 0.5f;
  py += 0.5f;

  float ix1 =
      lerp(tex2D<float>(tex, px, py), tex2D<float>(tex, px + 1.0f, py), fx);
  float ix2 = lerp(tex2D<float>(tex, px, py + 1.0f),
                   tex2D<float>(tex, px + 1.0f, py + 1.0f), fx);

  float res = lerp(ix1, ix2, fy);
  return res;
}

__global__ void d_render(unsigned char *d_output, int width, int height,
                         float tx, float ty, float scale, float cx, float cy,
                         cudaTextureObject_t texObj) {
  int x = blockIdx.x * blockDim.x + threadIdx.x;
  int y = blockIdx.y * blockDim.y + threadIdx.y;
  int i = y * width + x;

  float u = (x - cx) * scale + cx + tx;
  float v = (y - cy) * scale + cy + ty;

  if ((x < width) && (y < height)) {
    // write output color
    float c = tex2DBilinear(texObj, u, v);
    d_output[i] = c * 0xff;
  }
}

////////////////////////////////////////////////////////////////////////////////
// Declaration, forward
void runTest(int argc, char **argv);

////////////////////////////////////////////////////////////////////////////////
// Program main
////////////////////////////////////////////////////////////////////////////////
int main(int argc, char **argv) {
  printf("%s starting...\n", sampleName);

  // Process command-line arguments
  if (argc > 1) {
    if (checkCmdLineFlag(argc, (const char **)argv, "input")) {
      getCmdLineArgumentString(argc, (const char **)argv, "input",
                               (char **)&imageFilename);
    }
  }

  runTest(argc, argv);

  printf("%s completed, returned %s\n", sampleName,
         testResult ? "OK" : "ERROR!");
  exit(0);
}

////////////////////////////////////////////////////////////////////////////////
//! Run a simple test for CUDA
////////////////////////////////////////////////////////////////////////////////
void runTest(int argc, char **argv) {
  int devID = findCudaDevice(argc, (const char **)argv);

  // load image from disk
  unsigned char *hData = NULL;
  unsigned int width, height;
  char *imagePath = sdkFindFilePath(imageFilename, argv[0]);

  if (imagePath == NULL) {
    printf("Unable to source image file: %s\n", imageFilename);
    exit(EXIT_FAILURE);
  }

  sdkLoadPGM<unsigned char>(imagePath, &hData, &width, &height);

  unsigned int size = width * height * sizeof(unsigned char);
  printf("Loaded '%s', %d x %d pixels\n", imageFilename, width, height);

  // Allocate device memory for result
  unsigned char *dData = NULL;
  checkCudaErrors(cudaMalloc((void **)&dData, size));

  // Allocate array and copy image data
  cudaChannelFormatDesc channelDesc =
      cudaCreateChannelDesc(8, 0, 0, 0, cudaChannelFormatKindUnsigned);
  cudaArray *cuArray;
  checkCudaErrors(cudaMallocArray(&cuArray, &channelDesc, width, height));
  checkCudaErrors(cudaMemcpy2DToArray(
      cuArray, 0, 0, hData, width * sizeof(unsigned char),
      width * sizeof(unsigned char), height, cudaMemcpyHostToDevice));

  cudaTextureObject_t tex;
  cudaResourceDesc texRes;
  memset(&texRes, 0, sizeof(cudaResourceDesc));

  texRes.resType = cudaResourceTypeArray;
  texRes.res.array.array = cuArray;

  cudaTextureDesc texDescr;
  memset(&texDescr, 0, sizeof(cudaTextureDesc));

  texDescr.normalizedCoords = false;
  texDescr.filterMode = cudaFilterModeLinear;
  texDescr.addressMode[0] = cudaAddressModeWrap;
  texDescr.addressMode[1] = cudaAddressModeWrap;
  texDescr.readMode = cudaReadModeNormalizedFloat;

  checkCudaErrors(cudaCreateTextureObject(&tex, &texRes, &texDescr, NULL));

  dim3 threadsPerBlock(32, 32, 1);
  dim3 numBlocks((width / threadsPerBlock.x) + 1,
                 (height / threadsPerBlock.y) + 1, 1);

  float tempx, tempy, sc;
  sc = atof(argv[1]);
  tempx = atof(argv[2]);
  tempy = atof(argv[3]);
  cx = width * tempx;
  cy = height * tempy;
  scale /= sc;

  checkCudaErrors(cudaDeviceSynchronize());
  StopWatchInterface *timer = NULL;
  sdkCreateTimer(&timer);
  sdkStartTimer(&timer);

  // Execute the kernel
  d_render<<<numBlocks, threadsPerBlock>>>(dData, width, height, tx, ty, scale,cx, cy, tex);

  // Check if kernel execution generated an error
  getLastCudaError("Kernel execution failed");

  checkCudaErrors(cudaDeviceSynchronize());
  sdkStopTimer(&timer);
  printf("Processing time: %f (ms)\n", sdkGetTimerValue(&timer));
  printf("%.2f Mpixels/sec\n",
         (width * height / (sdkGetTimerValue(&timer) / 1000.0f)) / 1e6);
  sdkDeleteTimer(&timer);

  // Allocate mem for the result on host side
  unsigned char *hOutputData = (unsigned char *)malloc(size);
  // copy result from device to host
  checkCudaErrors(cudaMemcpy(hOutputData, dData, size, cudaMemcpyDeviceToHost));

  // Write result to file
  char outputFilename[1024];
  strcpy(outputFilename, imagePath);
  strcpy(outputFilename + strlen(imagePath) - 4, "_out.pgm");
  sdkSavePGM(outputFilename, hOutputData, width, height);
  printf("Wrote '%s'\n", outputFilename);

  checkCudaErrors(cudaDestroyTextureObject(tex));
  checkCudaErrors(cudaFree(dData));
  checkCudaErrors(cudaFreeArray(cuArray));
  free(imagePath);
}