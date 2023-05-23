import gmpy2
from gmpy2 import mpfr
from gmpy2 import xmpz

a = mpfr(0.0,35)
b = mpfr(3.141592264175415,23)
h = mpfr(0.009999999776482582,27)
fa = gmpy2.sin(mpfr(a,33))
fb = gmpy2.sin(mpfr(b,23))
tmp = mpfr(mpfr(a,35)+mpfr(mpfr(h,27)/mpfr(2.0,27),27),26)
fc = gmpy2.sin(mpfr(tmp,26))
intgl = mpfr(mpfr(fa,30)+mpfr(fb,8),7)
intgl = mpfr(mpfr(intgl,7)/mpfr(2.0,7),7)
intgl = mpfr(mpfr(intgl,7)+mpfr(mpfr(2.0,23)*mpfr(fc,23),23),22)
x = mpfr(mpfr(a,32)+mpfr(h,25),24)
while ( x<b):
	x_mid = mpfr(mpfr(x,24)+mpfr(mpfr(h,15)/mpfr(2.0,15),15),23)
	fx = gmpy2.sin(mpfr(x,23))
	fxm = gmpy2.sin(mpfr(x_mid,23))
	intgl = mpfr(mpfr(mpfr(intgl,22)+mpfr(fx,12),21)+mpfr(mpfr(2.0,12)*mpfr(fxm,12),12),20)
	x = mpfr(mpfr(x,21)+mpfr(h,13),20)

intgl = mpfr(mpfr(mpfr(intgl,20)*mpfr(h,20),20)/mpfr(3.0,20),20)
print("intgl = " + str(intgl))
# Variable intgl has an accuracy of 20 bits.
