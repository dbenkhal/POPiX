package Util;

import Number.*;
import java.util.*;

public class Environment {

	Map<String, Numbers> hm;
	Map<String, Intervalle> range;

	public Environment() {
		hm = new HashMap<String, Numbers>();
		range = new HashMap<String, Intervalle>();
	}

	public Intervalle getRange(String k) {
		return range.get(k);
	}

	public Numbers getValue(String k) {
		return hm.get(k);
	}

	public void set(String k, Numbers num) {
		Intervalle i;
		if (range.containsKey(k))
			i = range.get(k);
		else
			i = new Intervalle(new FloatNumbers(Float.POSITIVE_INFINITY), new FloatNumbers(Float.NEGATIVE_INFINITY));
		i.join(num);
		hm.put(k, num);
		range.put(k, i);
	}

	public String toStringint() {
		return range.toString();

	}

	public int size() {
		return hm.size();

	}

}
