# oeis-deconvolution

A tool for performing sophistocated searches in the gzipped version `stripped.gz` of the OEIS database.

### Installation

The program is written in the Clojure language. A compiled java JAR file is also provided. Follow these steps for using the program:

  * download the provided `deconvolution.jar` file;
  * download the last version of the Clojure archive and uncompress it;
  * download the last version of the `stripped.gz` database (from the OEIS site);
  * type the following command by adapting the path for each of the three files:

    java -cp ~/contribs/lisp/clojure-1.8.0-RC3/clojure-1.8.0-RC3.jar:deconvolution.jar baruchel.oeis.deconvolution "1,2,3,4,5,6,7,8,9,10,11,12" ~/docs/maths/stripped.gz

### Location of the database

If the `OEISDATABASE` environment variable exists with the full path of the `stripped.gz` file in it, then the path of this file doesn't need to be added as a last argument in the command line. If the `OEISDATABASE` environment variable doesn't exist but the `stripped.gz` file is in the current directory, the path of this file doesn't need to be added either.

### Usage

The request must be the first argument of the command line; all values must fit in a single string (quotes should be used if values are separated with spaces). Values can be separated with spaces or commas.
