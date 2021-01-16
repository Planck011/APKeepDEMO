package verifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import builder.PPM;
import jdd.bdd.BDD;


public class device {
	public Set<String> interfaces;//物理接口集
	public Set<String> port_list;
	public Set<String> connect;//拓扑相连的端口
	public Map<String, Set<Integer>> Pred_interface;
	public Map<Integer, Set<String>> Port_interface;
	public Map<String, Set<String>> portMap;//interface->{port}
	public Map<String, Set<String>> aclMap;//interface->{aclgroup}
	public Set<Rule> rules;//exist rules
	public Set<Rule> unrules;//待插入规则
	public PPM ppm;//element topo
	public String name;
	public device(String name,Set<String> inter,Set<String> vlans,Set<String> acls,Map<String, Set<String>> map,Map<String, Set<String>> acmap,BDD bdd) {
		this.Pred_interface = new HashMap<>();
		this.Port_interface = new HashMap<>();
		this.unrules = new HashSet<>();
		this.ppm = new PPM();
		this.interfaces = new HashSet<>();
		interfaces.addAll(inter);
		this.name = name;
		ppm.creatFWelement(this.name,vlans);
		Rule r1 = new Rule("FW", "default", "", "false", 0, "FW", bdd);
		this.rules = new HashSet<>();
		rules.add(r1);
//		for(String acl:acls) {
//			ppm.creatACLelement(acl);
//			String permit = ppm.getElement(acl).getName()+"_permit";
//			Rule r = new Rule("ACL",permit,"","false",0,acl,bdd);
//			rules.add(r);
//		}
		this.portMap = new HashMap<>();
		portMap.putAll(map);
		this.aclMap = new HashMap<>();
		aclMap.putAll(acmap);
	}
	public boolean findInterface(String port) {
		for(String p:interfaces)
		{
			if(p.equals(port))
				return true;
		}
		return false;
	}
	public boolean findConnect(String port) {
		if(connect.contains(port))
			return true;
		return false;
	}
	/**
	 * 将ppm中的端口谓词映射传递到物理端口
	 */
	public void sendTointerfaces(BDD bdd) {
		for(String inter:interfaces)
		{
			for(String port:portMap.get(inter)) {
				if(aclMap.get(port)==null)
				{
					if(Pred_interface.get(inter)==null)
						Pred_interface.put(inter, new HashSet<>());
					if(ppm.Pred.get(port)!=null)
						Pred_interface.get(inter).addAll(ppm.Pred.get(port));
				}
				else {
					if(Pred_interface.get(inter)==null)
						Pred_interface.put(inter, new HashSet<>());
					for(String p:aclMap.get(port))
					{
//						if(ppm.Pred.get(port)!=null&&ppm.Pred.get(p)!=null)
						Set<Integer> s = setAnd(ppm.Pred.get(p), ppm.Pred.get(port),bdd);
						Pred_interface.get(inter).addAll(s);
					}
				}
			}
		}
		updatePort_interfaces();//更新谓词端口映射
	}
	public void updatePort_interfaces() {
		for(Entry<String, Set<Integer>> en:Pred_interface.entrySet()) {
			for(Integer p:en.getValue()) {
				if(Port_interface.get(p)==null)
					Port_interface.put(p, new HashSet<>());
				Port_interface.get(p).add(en.getKey());
			}
		}
	}
	/**
	 * compute A(and)B by using BDD
	 * @param A pset
	 * @param B 边上的谓词集
	 * @return 两集合的交集
	 */
	private  Set<Integer> setAnd(Set<Integer> A,Set<Integer> B,BDD bdd) {
		Set<Integer> set = new HashSet<>();
		for(Integer a:A)
		{
			for(Integer b:B)
			{
				int temp = bdd.ref(bdd.and(a, b));
				if(temp!=0)
				{
					set.add(temp);
					continue;
				}
				bdd.deref(temp);
			}
		}
		return set;
	}
}
