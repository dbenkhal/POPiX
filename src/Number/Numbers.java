package Number;

import Expression.*;

public abstract class Numbers extends AbstractClass {
	Numbers val;
	Number x;
	Intervalle IntervalleValue;

	public Numbers(float y) {
		x = new Float(y);
	};

	public Numbers(int y) {
		x = new Integer(y);
	};

	public Numbers(double y) {
		x = new Double(y);
	};

	public Numbers() {
	};

	public abstract float getFloatValue();

	public abstract int getIntValue();

	public abstract double getDoubleValue();

	public abstract int ufp();

	public abstract int getufp();

	public abstract int ufpErr();

	public abstract int ulp();

	// public abstract int ulpErrAdd();

	// public abstract int ulpErrMult();

	public abstract int ulpErrCst();

	// public abstract int ulpErrId();

	// public abstract int ulpErrDiv();

	// public abstract int ulpErrSub();

	public abstract Numbers add(Numbers b);

	public abstract Numbers subtract(Numbers b);

	public abstract Numbers multiply(Numbers b);

	public abstract Numbers divide(Numbers b);

	public abstract Numbers Rand(Numbers xmin, Numbers xmax);

	public abstract Boolean LESS(Numbers b);

	public abstract Boolean LESSEQUAL(Numbers b);

	public abstract Boolean GREATER(Numbers b);

	public abstract Boolean GREATEREQUAL(Numbers b);

	public abstract Boolean EQUAL(Numbers b);

	public abstract Boolean NOTEQUAL(Numbers b);

	public String toString() {
		return x + "";

	}

	public String toStringInt() {
		return x.intValue() + "";

	}

	public String toStringLab() {
		return x + "" + "|" + lab + "|";

	}

	public abstract Numbers getElmt(int i);

	public abstract void setElmt(int n, Numbers i);
}
