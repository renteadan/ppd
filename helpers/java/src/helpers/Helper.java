package helpers;

import java.io.*;
import java.util.Random;
import java.util.Vector;

public class Helper {
		public void writeFile(String fileName, int min, int max, int size) {
				try {
						File newFile = new File(fileName);
						if (newFile.createNewFile()) {
								System.out.println("File created: " + newFile.getName());
						} else {
								System.out.println("File already exists.");
						}
						FileWriter writer = new FileWriter(fileName);
						Random r = new Random();
						int[] randomNumbers = r.ints(size, min, max+1).toArray();
						for(int x: randomNumbers) {
								writer.write(x + " ");
						}
						writer.close();
				} catch (IOException e) {
						System.out.println("An error occurred.");
						e.printStackTrace();
				}
		}

		private int[] stringToInt(String s) {
				String[] arr = s.split(" ");
				Vector<Integer> numbers = new Vector<Integer>();
				for(String str: arr) {
						int aux = Integer.parseInt(str);
						numbers.add(aux);
				}
				return numbers.stream().mapToInt(i->i).toArray();
		}

		public int[] readFileInt(String fileName) {
				int[] arr = new int[0];
				try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
						while (br.ready()) {
								String a = br.readLine();
								a = a.strip();
								arr = stringToInt(a);
						}
				} catch (IOException e) {
						e.printStackTrace();
				}
				return arr;
		}

		private Float[] stringToFloat(String s) {
				String[] arr = s.split(" ");
				Vector<Float> numbers = new Vector<>();
				for(String str: arr) {
						float aux = Float.parseFloat(str);
						numbers.add(aux);
				}
				return numbers.toArray(new Float[0]);
		}

		public Float[] readFileFloat(String fileName) {
				Float[] arr = new Float[0];
				try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
						while (br.ready()) {
								String a = br.readLine();
								a = a.strip();
								arr = stringToFloat(a);
						}
				} catch (IOException e) {
						e.printStackTrace();
				}
				return arr;
		}

		boolean intFilesEqual(String file1, String file2) {
				int[] a = readFileInt(file1);
				int[] b = readFileInt(file2);
				if(a.length != b.length)
						return false;
				for(int i=0;i<a.length; i++) {
						if(a[i] != b[i])
								return false;
				}
				return true;
		}

		boolean floatFilesEqual(String file1, String file2) {
				Float[] a = readFileFloat(file1);
				Float[] b = readFileFloat(file2);
				if(a.length != b.length)
						return false;
				for(int i=0;i<a.length; i++) {
						if(Float.compare(a[i], b[i]) != 0)
								return false;
				}
				return true;
		}
}
