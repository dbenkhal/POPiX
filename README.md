# POPiX: Floating to Fixed-Point Program tuner
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![Python](https://img.shields.io/badge/python-3670A0?style=for-the-badge&logo=python&logoColor=ffdd54) ![C](https://img.shields.io/badge/c-%2300599C.svg?style=for-the-badge&logo=c&logoColor=white)


POPIX a precision tuning framework  with the new functionality of generating fixed- point formats. It returns solutions suitable – at bit-level – for the IEEE-754 floating-point arithmetic, the fixed-point arithmetic, and the MPFR library for non-standard precision.


## :bulb: Features
 - Analyzes a program written in an IMP language and annotated with the precision desired by the user on the output
 - Produces an under-approximation of the ranges of the variables by dynamic analysis
 - Generates and solves an ILP problem based on a semantic modelling of the numerical errors propagation  
 - Finds the minimal number of bits needed at bit-level and in floating-point arithmetic with an accuracy guarantee on the result
 - Internally calls a fixed-point library to convert the associated indications into ones that exploit fixed-point computations

## :wrench: Prerequisites

To use POPiX, you must install the following dependencies:

- JAVA SE version 19 or latest 
- The following jar Files :
  - [ANTLR](https://www.antlr.org/download.html) generator parser version 4.7.2 or latest
  - [GLPK](https://glpk-java.sourceforge.net/gettingStarted.html) solver version 4.65 or latest 
  - [FixMath](http://download.savannah.nongnu.org/releases/fixmath/)  Library version 1.4 (User manual 🔗 https://www.nongnu.org/fixmath/doc/index.html)
 


## :hammer: Running the Example

   - Go inside examples folder, choose an example and copy its code to the file ```popix_src```.
   - Next, open ```main.java``` add your the POPiX pPath on your machine at line 15, and the path of the fixed-point library at Line 208 
   
   - Compile POPiX with the following command
    
 ```   
javac -cp  /path/to/.jars *.java  src/Boolean/*.java src/Constraint/*.java src/Expression/*.java src/Number/*.java src/Statement/*.java src/Main.java src/Util/*.java
```
-  Now you can run POPiX to tune precision of the program in  ```popix_src``` using the following command.
 ```  
 cd src 
 java -cp /path/to/.jars:. Main
```
   

## Generated Files
Once the execution process is done successfully, this will generate the follwowing files:
-  ```popix_lab```:   program source annotated with  control points at each node of the AST
-  ```constraints```: prints the semantic equations generated for the program  source
-  ```popix_output```: Optimized program annotated with the couple *|ufp, nsb|* where *ufp* is the weight of the most ignificant bit of the variable and *nsb* is the optimized precision returned by POPiX
- ```popix_float.py```: MPFR  code to create a program that gives an exact result   (i.e. 500 bits)
- ```popix_mpfr.py```: MPFR code generated with the optimized precisions returned by our POPiX
- ```popix_output.c```:  fixed-point program written in C with the fixed-point library instructions

 ## :bookmark_tabs: Cite This Work
 
POPiX version 1.0 is already available at [![GitHub release](https://img.shields.io/github/release/Naereen/StrapDown.js.svg)](https://github.com/sbessai/popix)
Please cite our paper in the the  15th Workshop on Design and Architectures for Signal and Image Processing (DASIP'22).
```
@inproceedings{BessaiKBM22,
  author       = {Sofiane Bessa{\"{\i}} and
                  Dorra {Ben Khalifa} and
                  Hanane Benmaghnia and
                  Matthieu Martel},
  title        = {Fixed-Point Code Synthesis Based on Constraint Generation},
  booktitle    = {Design and Architecture for Signal and Image Processing - 15th International
                  Workshop, {DASIP} 2022, Budapest, Hungary, June 20-22, 2022, Proceedings},
  series       = {Lecture Notes in Computer Science},
  volume       = {13425},
  pages        = {108--120},
  publisher    = {Springer},
  year         = {2022}
}

```

## :clipboard: License

POPiX is licensed under the GNU GPL 3 license only (GPL-3.0-only).

Copyright (c) 2023.All rights reserved. This program and the accompanying materials are made available under the terms of the GNU General Public License v3.0 only (GPL-3.0-only) which accompanies this
distribution, and is available at: https://www.gnu.org/licenses/gpl-3.0.en.html

Author : Dorra Ben Khalifa :email: dorra.ben-khalifa@univ-perp.fr
