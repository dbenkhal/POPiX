#include <math.h>
#include <time.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#define TRUE 1
#define FALSE 0


double ex6(double v, double w, double r) {
	return ((3.0 + (2.0 / (r * r))) - (((0.125 * (3.0 - (2.0 * v))) * (((w * w) * r) * r)) / (1.0 - v))) - 4.5;
}

int main (){
clock_t start, end;
 double cpu_time_used =0;
 start = clock();

    double lowV=1.0, upV= 9.0 ;
    double lowW=1.0, upW= 9.0 ;
   float lowR=1.0, upR= 9.0 ;
   float res=0.0;
    for (int i = 0; i < 100; i++) {
    double v = (double) lowV + rand() / (RAND_MAX / (upV- lowV+ 1) + 1);
    double w = (double) lowW + rand() / (RAND_MAX / (upW- lowW + 1) + 1);
    double r = (double) lowR + rand() / (RAND_MAX / (upR- lowR + 1) + 1);
    res = ex6(v,w,r);
    
    end = clock();
    cpu_time_used = cpu_time_used +  ((double) (end - start)) / CLOCKS_PER_SEC;
    }
    printf("res original= %1.20lf", res);
    
    cpu_time_used = cpu_time_used / 100;
    printf("time spent = %lf",cpu_time_used);


 

return 0;
}
