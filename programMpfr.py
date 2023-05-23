import gmpy2
from gmpy2 import mpfr
from gmpy2 import xmpz

a = mpfr(0.0,26)
b = mpfr(1.0,0)
h = mpfr(0.05000000074505806,30)
pi = mpfr(3.141592264175415,26)
q = mpfr(0.11999999731779099,0)
x = mpfr(a,26)
fun = gmpy2.sin(mpfr(mpfr(pi,26)*mpfr(x,26),25))
s1 = mpfr(fun,16)
while ( x<b):
	x = mpfr(mpfr(x,26)+mpfr(h,30),26)
	s1 = mpfr(mpfr(s1,15)+mpfr(mpfr(4.0,16)*mpfr(fun,16),15),15)
	x = mpfr(mpfr(x,26)+mpfr(h,30),26)
	s1 = mpfr(mpfr(s1,15)+mpfr(mpfr(2.0,15)*mpfr(fun,16),15),15)

s1 = mpfr(mpfr(s1,15)+mpfr(fun,16),15)
print("s1 = " + str(s1))
# Variable s1 has an accuracy of 15bits.
