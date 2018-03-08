# Huffman Project Tester

This program was created to greatly expedite the testing process for the Huffman project and circumnavigate the issue where test files would be corrupted once opened.

It does this mostly by allowing equality of two files to be programatically tested and by keeping an archive of all given files so they can not be correpted by the os.

To be honest I mostly made this because I saw the opportunity to make a sort of scripting system. Turns out my original code only required 2 tweaks that were pretty easy to spot after running this program on it.

# Setup

Simply download Arbiter.java and the archive folder into your project folder and run the main method on Arbiter.

# Usage

Here are all of the commands 
```
encode <file1> into <file2> using <file3>
decode <file2> into <file2> using <file3>
are <file1> and <file2> equal
are <file1> and <file2> equal <type>
put code for <file1> into <file2>
full pipeline on <file1>
test
end
help
object <file1>
object print code to <file1>
object decode <file1> into <file2>
```

If you do not put the type of the file in any argument with `.txt` it can be assumed from the context of the function. Be careful with the equals function as at least one file must have a type written on it or odd behavior may happen. If something does go wrong read the printed logs to see what files ended up being used by the program.

Putting a `*` before any file name allows that file to be grabbed from the archive folder. Writing to the archive folder using this program should not be possible.

Some extra features are that parentheses can allow for the output of one function to be used as an input to another. However, you may only have one set of parentheses per layer (So you cant `put (foo thing.txt) into (foo thing2.txt)` but `put (foo (foo thing.txt)) into test.txt)` is allowed.

`*output` references the output from the last command executed



# Some example Commands 
```
are *hamlet.txt and hamlet.txt equal
are *hamlet.txt and hamlet equal
full pipeline on *hamlet.txt
object *hamlet.code
are (put code for *hamlet into test) and *hamlet equal
```

You can also do things without parenthesies
```
put code for *hamlet into test
are  *output and *hamlet equal
```


# Do a full test on your code

Test the Contructor(int[]) and print functions (These can not be otherwise tested separately)
```
are (put code for *hamlet into test) and *hamlet.code equal
```

Test the the Constructor(Scanner) method
```
are ((object *hamlet) print code to test) and *hamlet.code equal
```

Test the decode method
```
are ((object *hamlet.code) decode *hamlet into test) and *hamlet.txt equal
```

Test encode in a more detailed manner
```
are (encode *hamlet into test1 using (put code for *hamlet into test1)) and *hamlet.short equal
```

Test your whole system against both hamlet and short
```
test
```
