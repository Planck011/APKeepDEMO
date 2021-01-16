package verifier;

import java.util.*;

import builder.*;
import jdd.bdd.*;
import jdk.internal.jshell.tool.resources.l10n;

public class Port {
//	private Set<String> aSet = new HashSet<>();
	private Port nextPort;
	private String name;
	public Port(String n) {
		// TODO Auto-generated constructor stub
		this.name = n;
	}
	public boolean setNext(Port next) {
		this.nextPort = next;
		return true;
	}
	public boolean nextIsnull() {
		if(nextPort==null)
			return true;
		else return false;
	}
	public String Name() {
		return name;
	}
	public static void main(String[] args) {
		Map<Integer, Integer> a = new HashMap<>();
		a.put(1, null);
		a.put(2, null);
		Set<Integer> bIntegers  = a.keySet();
		bIntegers.remove(2);
	}
}
