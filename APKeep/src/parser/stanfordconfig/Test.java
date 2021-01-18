package parser.stanfordconfig;

import java.util.*;
import java.util.Map.Entry;

import parser.topo.Topo;
import verifier.APKeep;
import verifier.device;
import verifier.Rule;
import parser.topo.Link;

/**
 * @version 1.0
 * @ClassName Test
 * @Description
 * @Author liminglong
 * @Date 2021/1/7 21:17
 */
public class Test {
    public static void main(String[] args) {
        String stanford_path = "C:\\Users\\pyh1343122828\\Desktop\\Java\\APKeep\\Resouses\\StanfordDataset";
        String file_path = stanford_path + "\\Stanford_backbone";
//        String port_map_path = stanford_path + "\\tf_stanford_topology";
        long starttime = System.currentTimeMillis();
        ConfigParse configParse = new ConfigParse(file_path);
        configParse.start();
        APKeep ap = initAPKeep(configParse);
        initTopo(ap);
        long parsetime = System.currentTimeMillis();
        for(device d:ap.devices)
        {
        	for(Rule ur:d.unrules)
        	{
        		ap.Identify(ur, d.rules);
        	}
        	Set<Integer> dd = ap.Update(d.ppm.Pred, d.ppm.Port);
        	ap.D.addAll(dd);
        	d.sendTointerfaces(ap.bdd);
			mapPrint(d.Pred_interface);
			mapPrint(d.Port_interface);
			System.out.println(dd);
			ap.ConstructDeltaForwardingGraph(dd, d.Port_interface);
			ap.G.printGraph();
        }
        long insertruletime = System.currentTimeMillis();
        ap.getStartNodes(ap.devices);
        ap.CheckInvariants();
        long endtime = System.currentTimeMillis();
        System.out.println("parse time:"+(parsetime-starttime)+"ns"+" construct time:"+(insertruletime-parsetime)+"ns"+" check time:"+(endtime-insertruletime)+"ns");
    }
    public static  <E,T> void mapPrint(Map<E, Set<T>> map) {
		for(Entry<E, Set<T>> entry:map.entrySet())
		{
			System.out.println("key="+entry.getKey()+"  values="+entry.getValue());
		}
	}
    public static void initTopo(APKeep ap) {
    	Topo ttTopo = new Topo();
    	Map<Integer, String> key = ttTopo.portMap.getPortmap();
    	Map<String, Map<Integer, String>> map = ttTopo.portMap.getPortmap_map();
    	for(Entry<Link<Integer,Integer>, Link<Integer,Integer>> entry:ttTopo.links.entrySet())
    	{
    		String scr_name = ttTopo.portToString(entry.getKey().src, key);
    		String dst_name = ttTopo.portToString(entry.getKey().dst, key);
    		device scr_d = ap.Find(scr_name, ap.devices);
    		device dst_d = ap.Find(dst_name, ap.devices);
    		String scr_port = map.get(scr_name).get(entry.getValue().src);
    		String dst_port = map.get(dst_name).get(entry.getValue().dst);
    		if(scr_port!=null)
    			scr_d.connect.add(dst_port);
    		if(dst_port!=null)
    			dst_d.connect.add(scr_port);
    	}
    }
    public static APKeep  initAPKeep(ConfigParse configParse) {
    	APKeep ap = new APKeep();
        ArrayList<Router> routerlist = configParse.getRouterlist();
        for(Router r:routerlist) {
        	String name = r.name;
        	Map<String, _interface> interfaces = r.getInterfaces();
//        	Set<String> inter = new HashSet<>();
//        	inter.addAll(interfaces.keySet());
        	Map<String, Set<String>> map = new HashMap<>();
        	Map<String, Set<String>> aMap = new HashMap<>();
        	Set<String> inter = new HashSet<>();
        	for(String i:interfaces.keySet()) {
        		inter.add(i.concat("#").concat(name));
        	}
        	for(Entry<String, _interface> entry:interfaces.entrySet())
        	{
        		map.put(entry.getKey().concat("#").concat(name), new HashSet<>());
        		int flag = entry.getValue().getFlag();
        		if(flag==1)
        		{
        			if(entry.getValue().getVlan_set()!=null)
        				map.get(entry.getKey().concat("#").concat(name)).addAll(entry.getValue().getVlan_set());
        		}
        		else if (flag == 2) {
        			if(entry.getValue().getVlan_acess_set()!=null)
        				map.get(entry.getKey().concat("#").concat(name)).addAll(entry.getValue().getVlan_acess_set());
				}
        		else if (flag == 5) {
        			if(entry.getValue().getVlan_set()!=null)
        				map.get(entry.getKey().concat("#").concat(name)).addAll(entry.getValue().getVlan_set());
        			if(entry.getValue().getVlan_acess_set()!=null)
        				map.get(entry.getKey().concat("#").concat(name)).addAll(entry.getValue().getVlan_acess_set());
				}
        	}
        	Map<String, _interface> vlanrule = r.getVlanif_in_rtr();
        	
        	Set<String> vlans = r.getVlansetToString();
        	Set<String> acls = r.getaclsetToString();
        	device d = new device(name, inter, vlans, acls, map, aMap, ap.bdd);
        	for(Entry<String, _interface> v:vlanrule.entrySet()) {
        		String vlan = v.getKey().substring(4, v.getKey().length());
        		String ip = v.getValue().getIP_bin();
        		if(ip == null)
        			continue;
        		int mask = v.getValue().getMasklen();
        		String match = ip.substring(0,mask);
        		Rule rule = new Rule("FW", vlan, match, "false", 1, "FW", ap.bdd);
        		d.unrules.add(rule);
        	}
        	ap.devices.add(d);
        }
        return ap;
	}
}
