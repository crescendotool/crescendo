TextToMorseTape requires five arugments in a specific order

1.  Filename to output data to, this does not include the extensions (.png, .dat)

2.  Length of the blank space on the tape before the start of morse data, in mm

3.  The total length of the tape in mm.  Note the application assumes a morse dot is 1mm long.

4.  The number of pixels per dot on the output png version of the tape.  

5.  The message to output as morse code.  This will be prepended by the standard "attention" sequence and followed by the standard "end of work" sequence.

An example usage would be

java -jar TextToMorseTape.jar myMorseSequence 10 500 5 The quick brown fox jumps over the lazy dog.