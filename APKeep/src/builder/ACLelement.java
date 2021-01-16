package builder;

import java.util.*;

public class ACLelement implements Element{
	private final String type ="ACL";
	private String name;
	private Set<String> port_list;
	public ACLelement() {
		// TODO Auto-generated constructor stub
		this.port_list = new HashSet<>();
		this.name = "defaultacl";
		port_list.add(name+"permit");
		port_list.add(name+"deny");
	}
	public ACLelement(String n) {
		// TODO Auto-generated constructor stub
		this.port_list = new HashSet<>();
		this.name = n;
		port_list.add(name+"_permit");
		port_list.add(name+"_deny");
	}
	public String getRuleType() {
		return type;
	}
	public String getName() {
		return name;
	}
	public Set<String> getPort_list() {
		return port_list;
	}
}
