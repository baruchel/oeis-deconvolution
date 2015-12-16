# oeis-deconvolution

A tool for performing sophisticated searches in the gzipped version `stripped.gz` of the OEIS database.

### Installation

The program is written in the Clojure language. A compiled java JAR file is also provided. Follow these steps for using the program:

  * download the provided JAR file;
  * download the last version of the `stripped.gz` database (from the OEIS site);
  * type the following command by adapting the path for the gzipped file:

    java -jar app-1.0.0-standalone.jar "1,2,3,4,5,6,7,8,9,10,11,12" ~/docs/maths/stripped.gz

### Location of the database

If the `OEISDATABASE` environment variable exists with the full path of the `stripped.gz` file in it, then the path of this file doesn't need to be added as a last argument in the command line. If the `OEISDATABASE` environment variable doesn't exist but the `stripped.gz` file is in the current directory, the path of this file doesn't need to be added either.

### Usage

The request must be the first argument of the command line; all values must fit in a single string (quotes should be used if values are separated with spaces). Values can be separated with spaces or commas.
