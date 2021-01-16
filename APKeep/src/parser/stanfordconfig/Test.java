package parser.stanfordconfig;

import java.util.*;
import java.util.Map.Entry;


import verifier.APKeep;
import verifier.device;
import verifier.Rule;;

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

        ConfigParse configParse = new ConfigParse(file_path);
        configParse.start();
        APKeep ap = initAPKeep(configParse);
        for(device d:ap.devices)
        {
        	for(Rule ur:d.unrules)
        	{
        		ap.Identify(ur, d.rules);
        	}
        	Set<Integer> dd = ap.Update(d.ppm.Pred, d.ppm.Port);
        	d.sendTointerfaces(ap.bdd);
			mapPrint(d.Pred_interface);
			mapPrint(d.Port_interface);
			System.out.println(dd);
        }
        System.out.println();
    }
    public static  <E,T> void mapPrint(Map<E, Set<T>> map) {
		for(Entry<E, Set<T>> entry:map.entrySet())
		{
			System.out.println("key="+entry.getKey()+"  values="+entry.getValue());
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
        	for(Entry<String, _interface> entry:interfaces.entrySet())
        	{
        		map.put(entry.getKey(), new HashSet<>());
        		int flag = entry.getValue().getFlag();
        		if(flag==1)
        		{
        			if(entry.getValue().getVlan_set()!=null)
        				map.get(entry.getKey()).addAll(entry.getValue().getVlan_set());
        		}
        		else if (flag == 2) {
        			if(entry.getValue().getVlan_acess_set()!=null)
        				map.get(entry.getKey()).addAll(entry.getValue().getVlan_acess_set());
				}
        		else if (flag == 5) {
        			if(entry.getValue().getVlan_set()!=null)
        				map.get(entry.getKey()).addAll(entry.getValue().getVlan_set());
        			if(entry.getValue().getVlan_acess_set()!=null)
        				map.get(entry.getKey()).addAll(entry.getValue().getVlan_acess_set());
				}
        	}
        	Map<String, _interface> vlanrule = r.getVlanif_in_rtr();
        	Set<String> inter = interfaces.keySet();
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
