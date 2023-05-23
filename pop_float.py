import gmpy2
from gmpy2 import mpfr
from gmpy2 import xmpz

a = mpfr(0.0,500)
b = mpfr(3.141592264175415,500)
h = mpfr(0.009999999776482582,500)
fa = gmpy2.sin(mpfr(a,500))
fb = gmpy2.sin(mpfr(b,500))
tmp = mpfr(mpfr(a,500)+mpfr(mpfr(h,500)/mpfr(2.0,500),500),500)
fc = gmpy2.sin(mpfr(tmp,500))
intgl = mpfr(mpfr(fa,500)+mpfr(fb,500),500)
intgl = mpfr(mpfr(intgl,500)/mpfr(2.0,500),500)
intgl = mpfr(mpfr(intgl,500)+mpfr(mpfr(2.0,500)*mpfr(fc,500),500),500)
x = mpfr(mpfr(a,500)+mpfr(h,500),500)
while ( x<b):
	x_mid = mpfr(mpfr(x,500)+mpfr(mpfr(h,500)/mpfr(2.0,500),500),500)
	fx = gmpy2.sin(mpfr(x,500))
	fxm = gmpy2.sin(mpfr(x_mid,500))
	intgl = mpfr(mpfr(mpfr(intgl,500)+mpfr(fx,500),500)+mpfr(mpfr(2.0,500)*mpfr(fxm,500),500),500)
	x = mpfr(mpfr(x,500)+mpfr(h,500),500)

intgl = mpfr(mpfr(mpfr(intgl,500)*mpfr(h,500),500)/mpfr(3.0,500),500)
print("intgl = " + str(intgl)  )
# Variable intgl has an accuracy of 20 bits.
