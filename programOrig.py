import gmpy2
from gmpy2 import mpfr
from gmpy2 import xmpz

a = mpfr(0.0,300)
b = mpfr(1.0,300)
h = mpfr(0.05000000074505806,300)
pi = mpfr(3.141592264175415,300)
q = mpfr(0.11999999731779099,300)
x = mpfr(a,300)
fun = gmpy2.sin(mpfr(mpfr(pi,300)*mpfr(x,300),300))
s1 = mpfr(fun,300)
while ( x<b):
	x = mpfr(mpfr(x,300)+mpfr(h,300),300)
	s1 = mpfr(mpfr(s1,300)+mpfr(mpfr(4.0,300)*mpfr(fun,300),300),300)
	x = mpfr(mpfr(x,300)+mpfr(h,300),300)
	s1 = mpfr(mpfr(s1,300)+mpfr(mpfr(2.0,300)*mpfr(fun,300),300),300)

s1 = mpfr(mpfr(s1,300)+mpfr(fun,300),300)
print("s1 = " + str(s1)  )
# Variable s1 has an accuracy of 15 bits.
