# Map Explorer

Map Explorer shows Line-of-Sight (LOS) on D&D Miniatures maps.

This is free software, use as you wish for any purpose
whatsoever. For details, see the file License.txt.


## Installation

Unpack `MapExplorer.zip`. This will create a subdirectory MapExplorer
containing the program.

You need to have a Java 7 Runtime Environment installed to run
Map Explorer.


## Running

To start Map Explorer from the command line, change to the
MapExplorer directory and run

    java -jar MapExplorer.jar <map file>

If called without a map file argument, Map Explorer will try to
open Fane_of_Lolth.map.

For Windows, a shell script mapexplorer.bat can be used to start
Map Explorer. (And if you associate mapexplorer.bat with the .map
extension in Windows Explorer, you should be able to open
Map Explorer by double-clicking on a map file).

To show LOS from a map square, just click on the square.


## Limitations

Map Explorer might not always find all LOS squares. If you find a case
where Map Explorer misses a LOS on one of the official maps, please
report that as an issue.
