/*
Copyright 2017 Caleb Kierum
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

import java.io.*;
import java.util.*;

public class Arbiter {
	private static String lastOut = "";
	private static final int CHAR_MAX = 256;
	private static HuffmanTree object;

	public static void main(String[] args) {
		System.out.println("Welcome! This program is a simple command system to speed up debugging");
		printCommands();
		Scanner console = new Scanner(System.in);
		String command = "";
		while (!command.equals("end")) {
			if (!command.equals("")) {
				try {
					lastOut = interpret(command);
				} catch (IOException e) {
					System.out.println("Invalid file");
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("Something went wrong... this can happen if your code is wrong or the code file used does not match the file being read");
				}
			}
			System.out.println("");
			command = console.nextLine();
		}
		System.out.println("We are done! Thanks");
	}

	private static String interpret(String commmand)  throws IOException, ArrayIndexOutOfBoundsException {
		if (commmand.contains(")")) {
			// 0 1 2 3 4 5 6 7 8 9 0
			// i a   m ( c o o ) t o

			int sub1 = commmand.indexOf("(");//4
			int sub2 = commmand.lastIndexOf(")");//8
			String output = interpret(commmand.substring(sub1 + 1, sub2));
			if (output == "") {
				System.out.println("Subcommand " + commmand + " does not have output invalid!");
			} else {
				return interpret(commmand.substring(0, sub1) + output + commmand.substring(sub2 + 1));
			}
			return "";
		} else {
			String[] sections = commmand.split("\\s+");
			if (sections.length > 0) {
				String command = sections[0];
				if (command.equals("help")) {
					printCommands();
					return "";
				} else if (command.equals("test")) {
					String hamlet = "*hamlet";
					String shorter = "*short";
					String hamletf = textFile(hamlet, false);
					String shorterf = textFile(shorter, false);
					boolean p1 = fullPipeline(hamletf, false);
					boolean p2 = fullPipeline(shorterf, false);

					if (!p1 && !p2) {
						System.out.println("Both parts of the test failed");
					} else if (!p1) {
						System.out.println("Hamlet pipeline failed");
					} else if (!p2) {
						System.out.println("Short pipeline failed");
					} else {
						System.out.println("From here it looked like everything went well! No guarantees tho...");
					}
					return "";
				} else if (command.equals("full") && sections.length == 4) {
					fullPipeline(textFile(sections[3], false), true);
					return "";
				} else if (command.equals("encode") && sections.length == 6) {
					String arg1 = textFile(sections[1], false);
					String arg2 = shortFile(sections[3], true);
					String arg3 = codeFile(sections[5], false);
					encode(arg1, arg2, arg3, true);
					return arg2;
				} else if (command.equals("decode") && sections.length == 6) {
					String arg1 = shortFile(sections[1], false);
					String arg2 = textFile(sections[3], true);
					String arg3 = codeFile(sections[5], false);
					decode(arg1, arg2, arg3, true);
					return arg2;
				} else if (command.equals("are") && sections.length >= 4) {
					String arg1 = sections[1];
					String arg2 = sections[3];

					if (!arg1.contains(".") && !arg2.contains(".")) {
						if (sections.length == 6) {
							arg1 += "." + sections[5];
							arg2 += "." + sections[5];
						}
					} else if (arg1.contains(".")) {
						String[] parts = arg1.split("\\.");
						arg2 += "." + parts[1];
					} else if (arg2.contains(".")) {
						String[] parts = arg2.split("\\.");
						arg1 += "." + parts[1];
					}
					arg1 = textFile(arg1, false);
					arg2 = textFile(arg2, false);
					equallsfiles(arg1, arg2, true);
					return "";
				} else if (command.equals("put") && sections.length == 6) {
					String arg1 = textFile(sections[3], false);
					String arg2 = codeFile(sections[5], true);
					code(arg1, arg2, true);
					return arg2;
				} else if (command.equals("object")) {
					if (sections.length == 2) {
						constructObjectFrom(codeFile(sections[1], false));
						return "object";
					} else if (sections.length == 5) {
						if (sections[1].equals("print")) {
							String file = codeFile(sections[4], true);
							objectPrintCode(file);
							return file;
						} else if (sections[1].equals("decode")) {
							String file1 = shortFile(sections[2], false);
							String file2 = textFile(sections[4], true);
							objectDecode(file1, file2);
							return file2;
						} else {
							invalidCommand();
							return "";
						}
					} else {
						invalidCommand();
						return "";
					}
				} else {
					invalidCommand();
					return "";
				}
			} else {
				invalidCommand();
				return "";
			}
		}
	}
	private static void constructObjectFrom(String filef)   throws IOException, ArrayIndexOutOfBoundsException {
		System.out.println("Initializing the object using the code stored in " + filef);
		Scanner codeInput = new Scanner(new File(filef));
		object = new HuffmanTree(codeInput);
		System.out.println("Done!");
	}
	private static void objectPrintCode(String file)    throws IOException, ArrayIndexOutOfBoundsException {
		if (object == null) {
			System.out.println("We dont have an object to do this with make one with \"object <file1> \"");
		} else {
			System.out.println("Using the object to write a code to " + file);
			PrintStream output = new PrintStream(new File(file));
			object.write(output);

			System.out.println("Done!");
		}
	}
	private static void objectDecode(String inputf, String outputf)    throws IOException, ArrayIndexOutOfBoundsException {
		if (object == null) {
			System.out.println("We dont have an object to do this with make one with \"object <file1> \"");
		} else {
			System.out.println("Using object to decode " + inputf + " to " + outputf);
			
			BitInputStream input = new BitInputStream(inputf);
			PrintStream output = new PrintStream(new File(outputf));
			object.decode(input, output, CHAR_MAX);
			output.close();

			System.out.println("Done!");
		}
	}

	private static void invalidCommand() {
		System.out.println("INVALID COMMAND!");
		printCommands();
	}
	private static void printCommands() {
		System.out.println("Available commands commands are: ");
		System.out.println("\tencode <file1> into <file2> using <file3>");
		System.out.println("\tdecode <file2> into <file2> using <file3>");
		System.out.println("\tare <file1> and <file2> equal");
		System.out.println("\tare <file1> and <file2> equal <type>");
		System.out.println("\tput code for <file1> into <file2>");
		System.out.println("\tfull pipeline on <file1>");
		System.out.println("\ttest");
		System.out.println("\tend");
		System.out.println("\thelp");
		System.out.println("\tobject <file1>");
		System.out.println("\tobject print code to <file1>");
		System.out.println("\tobject decode <file1> into <file2>");
		System.out.println("");
		System.out.println("\tPutting parentheses in your command allow you to use a commands output as the argument for another");
		System.out.println("\ta '*' before any file name tries to grab a clean copy of the file from the archive");
		System.out.println("\tIf you do not explicitly put the type after a file name it will be assumed. Watch output to assure its what you want");
		System.out.println("\t*output uses the ouput for the last command as an argument for the new one");
		System.out.println("");
	}

	private static String codeFile(String in, boolean write)  throws IOException {
		if (write && in.contains("*")) {
			System.out.println("Cannot write to master copy!");
			throw new IOException();
		}
		if (!write && in.length() != 0 && in.charAt(0) == '*') {
			return magicFile(in.substring(1), "code");
		} else if (in.contains(".")) {
			return in;
		} else {
			return in + ".code";
		}
	}
	private static String shortFile(String in, boolean write)  throws IOException {
		if (write && in.contains("*")) {
			System.out.println("Cannot write to master copy!");
			throw new IOException();
		}
		if (!write && in.length() != 0 && in.charAt(0) == '*') {
			return magicFile(in.substring(1), "short");
		} else if (in.contains(".")) {
			return in;
		} else {
			return in + ".short";
		}
	}
	private static String textFile(String in, boolean write)  throws IOException {
		if (write && in.contains("*")) {
			System.out.println("Cannot write to master copy!");
			throw new IOException();
		}

		if (!write && in.length() != 0 && in.charAt(0) == '*') {
			return magicFile(in.substring(1), "txt");
		} else if (in.contains(".")) {
			return in;
		} else {
			return in + ".txt";
		}
	}
	private static String magicFile(String in, String bias)  throws IOException {
		if (in.equals("output")) {
			return lastOut;
		} else {
			String[] parts = in.split("\\.");

			String fileWanted = "";
			String fileExtension = "";
			if (parts.length > 1) {
				fileWanted = parts[0];
				fileExtension = parts[1];
			} else {
				fileWanted = in;
				fileExtension = bias;
			}
			return "./archive/"+fileWanted+"."+fileExtension;
		}
	}
	private static void encode(String inputf, String outputf, String codef, boolean verbose)  throws IOException, ArrayIndexOutOfBoundsException {
		if (verbose)
			System.out.println("Encoding " + inputf + " into " + outputf + " using " + codef);

      // open code file and record codes
		String[] codes = new String[CHAR_MAX + 1];
		Scanner codeInput = new Scanner(new File(codef));
		while (codeInput.hasNextLine()) {
			int n = Integer.parseInt(codeInput.nextLine());
			codes[n] = codeInput.nextLine();
		}

      // open source file, open output, encode
		FileInputStream input = new FileInputStream(inputf);
		BitOutputStream output = new BitOutputStream(outputf);
		boolean done = false;
		int n = input.read();
		while (n != -1) {
			if (codes[n] == null) {
				System.out.println("Your code file has no code for " + n +
					" (the character '" + (char)n + "')");
				System.out.println("exiting...");
				System.exit(1);
			}
			writeString(codes[n], output);
			n = input.read();
		}
		writeString(codes[CHAR_MAX], output);
		output.close();

		if (verbose)
			System.out.println("Done!");
	}

	public static void writeString(String s, BitOutputStream output)  throws IOException, ArrayIndexOutOfBoundsException {
		for (int i = 0; i < s.length(); i++)
			output.writeBit(s.charAt(i) - '0');
	}

	private static void decode(String inputf, String outputf, String codef, boolean verbose) throws IOException, ArrayIndexOutOfBoundsException  {
		if (verbose)
			System.out.println("Decoding " + inputf + " into " + outputf + " using " + codef);

		Scanner codeInput = new Scanner(new File(codef));
		HuffmanTree t = new HuffmanTree(codeInput);

      // open encoded file, open output, decode
		BitInputStream input = new BitInputStream(inputf);
		PrintStream output = new PrintStream(new File(outputf));
		t.decode(input, output, CHAR_MAX);
		output.close();

		if (verbose)
			System.out.println("Done!");
	}
	private static boolean equallsfiles(String file1f, String file2f, boolean verbose)  throws IOException, ArrayIndexOutOfBoundsException {
		if (verbose) {
			System.out.println("Checking if " + file1f + " is equal to " + file2f);
		}
		boolean equal = false;
		File file1 = new File(file1f);
		File file2 = new File(file2f);

		equal = binaryDiff(file1, file2, verbose);

		if (verbose) {
			if (equal) {
				System.out.println("Done! They are equal");
			} else {
				System.out.println("Done! They are NOT equal");
			}
		}

		return equal;
	}
	private static boolean binaryDiff(File a, File b, boolean verbose) throws IOException {
		Scanner s1 = new Scanner(a);
		Scanner s2 = new Scanner(b);

		while (s1.hasNext() && s2.hasNext()) {
			String l1 = s1.nextLine();
			String l2 = s2.nextLine();

			if (!l1.equals(l2)) {
				if (verbose)
					System.out.println(l1 + " is not equal to " + l2);

				return false;
			}
		}

		if (s1.hasNext() || s2.hasNext()) {
			if (verbose)
				System.out.println("One of the files has more lines");

			return false;
		}
		return true;
	}
	private static void code(String inputf, String outputf, boolean verbose)  throws IOException, ArrayIndexOutOfBoundsException {
		if (verbose) {
			System.out.println("Coding " + inputf + " into a compressed format at " + outputf);
		}

      // open input file and count character frequencies
		FileInputStream input = new FileInputStream(inputf);
		int[] count = new int[CHAR_MAX];
		int n = input.read();
		while (n != -1) {
			count[n]++;
			n = input.read();
		}

      // build tree, open output file, print codes
		HuffmanTree t = new HuffmanTree(count);
		PrintStream output = new PrintStream(new File(outputf));
		t.write(output);

		if (verbose) {
			System.out.println("Wrote to " + outputf);
		}
	}

	private static boolean fullPipeline(String file, boolean verbose) throws IOException, ArrayIndexOutOfBoundsException  {
      String textFile = file;
      String codeFile = ".code.notoque";
      String shortFile = ".short.notoque";
      String resultTextFile = ".txt.notoque";
      
		if (verbose)
			System.out.println("Running the full pipeline on " + file);

		code(file, codeFile, true);
		encode(file, shortFile, codeFile, true);
      decode(shortFile, resultTextFile, codeFile, true);
		boolean result = equallsfiles(file, resultTextFile, false);

		if (verbose) {
			if (result) {
				System.out.println("Pipeline passed! Well done! Make sure to check other files");
			} else {
				System.out.println("Pipeline test failed... check individual components");
			}
		}

		return result;
	}
}