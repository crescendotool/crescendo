This class converts a bitmap into the tabular format required for the 20-sim 2dtable function used in the line following sensors

It should be run using one of the two following commands

java Bitmap2Text <filename> <scale>

or

java Bitmap2Text <filename>

<filename> is the name of the bitmap file you wish to convert but omitting the ".bmp" from the end. 
It also defines the name of the output file, so "java Bitmap2Text foo" would read in foo.bmp and output foo.txt

<scale> defines the size each pixel in the bitmap represents in metres, so the command "java Bitmap2Text foo 0.01" would output a data file where each pixel is 1cm square.
Omittin the scale forces the program to use the default value of 0.01m

The program gives the centre of the bitmap the coordinates 0,0.


It is a very rough tool to allow the pilot project to proceed, any questions please ask (carl.gamble@newcastle.ac.uk)