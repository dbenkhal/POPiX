#include "/home/dbenkhal/Documents/fixmath-1.4/include/fixmath.h"
#include <stdio.h>
#include <math.h>
#include <time.h>
int main(){ 
 volatile int a;
 volatile int b;
 volatile int h;
 volatile int fa;
 volatile int fb;
 volatile int tmp;
 volatile int fc;
 volatile int intgl;
 volatile int x;
 volatile int x_mid;
 volatile int fx;
 volatile int fxm;
 volatile int POPxxx;
a = fx_ftox(0.0,35);
b = fx_ftox(3.1415923,23);
h = fx_ftox(0.01,27);
fa =  POPxxx = fx_xtox(a,35,30);
fx_subx(POPxxx,fx_divx(fx_mulx(POPxxx,fx_mulx(POPxxx,POPxxx,30),30),fx_dtox(6,30),30)); //fx_sinx(fx_xtox(a,35,30),30);
fb =  POPxxx = fx_xtox(b,23,20);
fx_subx(POPxxx,fx_divx(fx_mulx(POPxxx,fx_mulx(POPxxx,POPxxx,20),20),fx_dtox(6,20),20)); //fx_sinx(fx_xtox(b,23,20),20);
tmp = fx_addx(fx_xtox(a,35,26),fx_xtox(fx_divx(h,fx_ftox(2.0,27),27),27,26));
fc =  POPxxx = fx_xtox(tmp,26,23);
fx_subx(POPxxx,fx_divx(fx_mulx(POPxxx,fx_mulx(POPxxx,POPxxx,23),23),fx_dtox(6,23),23)); //fx_sinx(fx_xtox(tmp,26,23),23);
intgl = fx_addx(fx_xtox(fa,30,7),fx_xtox(fb,20,7));
intgl = fx_divx(intgl,fx_ftox(2.0,7),7);
intgl = fx_addx(fx_xtox(intgl,7,22),fx_xtox(fx_mulx(fx_ftox(2.0,23),fc,23),23,22));
x = fx_addx(fx_xtox(a,35,24),fx_xtox(h,27,24));
while(x<b){x_mid = fx_addx(fx_xtox(x,24,23),fx_xtox(fx_divx(h,fx_ftox(2.0,15),3),15,23));
fx =  POPxxx = fx_xtox(x,24,20);
fx_subx(POPxxx,fx_divx(fx_mulx(POPxxx,fx_mulx(POPxxx,POPxxx,20),20),fx_dtox(6,20),20)); //fx_sinx(fx_xtox(x,24,20),20);
fxm =  POPxxx = fx_xtox(x_mid,23,20);
fx_subx(POPxxx,fx_divx(fx_mulx(POPxxx,fx_mulx(POPxxx,POPxxx,20),20),fx_dtox(6,20),20)); //fx_sinx(fx_xtox(x_mid,23,20),20);
intgl = fx_addx(fx_xtox(fx_addx(fx_xtox(intgl,22,21),fx_xtox(fx,20,21)),21,20),fx_xtox(fx_mulx(fx_ftox(2.0,12),fxm,20),12,20));
x = fx_addx(fx_xtox(x,24,20),fx_xtox(h,27,20));
};
intgl = fx_divx(fx_mulx(intgl,h,27),fx_ftox(3.0,20),20);

printf("Float : intgl = 1.9999786716805323\n");
printf("Fix   : intgl= %f\n",fx_xtof(intgl,20));
return 0;
} 
