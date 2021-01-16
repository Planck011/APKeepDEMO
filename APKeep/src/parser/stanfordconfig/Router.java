package parser.stanfordconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Router {
    String name;
    //该路由器中涉及的acl规则
    private HashMap<Integer, ArrayList<Acl_Rule>> Acl_in_rtr;
    //该路由器中涉及的vlan规则
    private HashMap<Integer, Vlan_Rule> Vlan_in_rtr;
    //该路由器中的回环接口
    private HashMap<Integer, Loopback_Int> Loopback_in_rtr;
    //该路由器中的物理端口
    private HashMap<String, _interface> Int_in_rtr;
    //vlan接口
    private HashMap<String, _interface> vlanif_in_rtr;

    Router(String name){
        this.name = name;
        Acl_in_rtr = new HashMap<>();
        Vlan_in_rtr = new HashMap<>();
        Loopback_in_rtr = new HashMap<>();
        Int_in_rtr = new HashMap<>();
        vlanif_in_rtr = new HashMap<>();
    }

    public Map<String, _interface> getInterfaces() {
		return Int_in_rtr;
	}
    
    /**
     * @return vlan端口集合（string）
     */
    public Set<String> getVlansetToString() {
		Set<String> a = new HashSet<>();
		for(Integer i:Vlan_in_rtr.keySet()) {
			a.add(i.toString());
		}
    	return a;
	}
    public Set<String> getaclsetToString() {
		Set<String> a = new HashSet<>();
		for(Integer i:Acl_in_rtr.keySet()) {
			a.add(i.toString());
		}
    	return a;
	}
    public void addAcl(int acl_list, Acl_Rule acl_rule) {
        if (Acl_in_rtr.containsKey(acl_list))
            Acl_in_rtr.get(acl_list).add(acl_rule);
        else {
            ArrayList<Acl_Rule> acl_rules = new ArrayList<>();
            acl_rules.add(acl_rule);
            Acl_in_rtr.put(acl_list, acl_rules);
        }
    }

    public void addVlan(int vlan_list, Vlan_Rule vlan_rule) {
        Vlan_in_rtr.put(vlan_list, vlan_rule);
    }

    public void addLoopback(int name, Loopback_Int loopback_int) {
        Loopback_in_rtr.put(name, loopback_int);
    }

    public void addIntface(String name, _interface inf) {
        Int_in_rtr.put(name, inf);
    }

    public void add_sub_int(String int_name, String sub_int_name, _interface inf) {
        _interface intf = Int_in_rtr.get(int_name);
        intf.setSub_flag(true);
        intf.addsub_int_list(sub_int_name, inf);
    }

    public void addvlanif(String name, _interface inf) {
        vlanif_in_rtr.put(name, inf);
    }
    
    public Map<String, _interface> getVlanif_in_rtr() {
		return vlanif_in_rtr;
	}
}

/**
 * Acl_Rule类只存permit或deny的访问控制规则，对于配置文件后面的类似
 * access-list 135 deny   tcp any any eq 445
 * access-list 135 permit tcp any any gt 139
 * 等等涉及到tcp和udp由于理解不完善只进行简单存储
 */
class Acl_Rule{
    private boolean pack_tcp_udp_flag;  //正常的还是tcp、udp相关，true正常，false代表tcp
    private boolean per_deny_flag;      //permit还是deny，true代表permit，false代表deny
    private String ip;          //ip地址
    private String mask;        //掩码,0.0.0.255形式
    private String ip_bin;      //二进制ip地址
    private String mask_bin;    //二进制掩码
    private int mask_len;       //掩码长度
    private String description; //如果涉及tcp或udp，存储配置命令

    Acl_Rule(String per_deny_flag, String ip, String mask){
        pack_tcp_udp_flag = true;
        if (per_deny_flag.equals("permit"))
            this.per_deny_flag = true;
        else
            this.per_deny_flag = false;
        if (ip.equals("any")) {
            this.ip = ip;
            ip_bin = null;
        } else {
            this.ip = ip;
            ip_bin = IPFormat.toBinaryNumber(ip);
        }
        if (mask.equals("no")) {
            this.mask = null;
            mask_bin = null;
            mask_len = -1;
        } else {
            this.mask = mask;
            mask_bin = IPFormat.MaskToBinaryNumber(mask);
            mask_len = IPFormat.count_in_String(mask_bin);
        }
        description = null;
    }
    Acl_Rule(String description){
        pack_tcp_udp_flag = false;
        this.description = description;
        per_deny_flag = false;
        ip = null;
        mask = null;
        ip_bin = null;
        mask_bin = null;
        mask_len = -1;
    }
}

class Vlan_Rule{
    private boolean name_flag;  //表示vlan中是name还是ip地址形式，false表示ip地址，false表示name
    private String name_ip;
    private String ip_bin;
    private int mask_len;

    Vlan_Rule(String ip, String mask_len){
        name_flag = false;
        this.name_ip = ip;
        ip_bin = IPFormat.toBinaryNumber(ip);
        this.mask_len = Integer.parseInt(mask_len);
    }
    Vlan_Rule(String name){
        this.name_flag = true;
        this.name_ip = name;
        ip_bin = null;
        mask_len = -1;
    }
}

/**
 * 回环接口，只是将配置数据存储，未存ipv6的二进制，description是ipv6 ospf **** area *命令
 */
class Loopback_Int{
    private boolean flag;       //标记该接口是否只有ip，true表示只有ip，否则有ipv6等
    private String ip;
    private String mask_ip;     //255.255.255.0形式
    private String ip_bin;
    private String mask_ip_bin;
    private int mask_ip_len;
    private String description;
    Loopback_Int(String ip, String mask_ip, String description){
        if (description.equals("no")) {
            flag = true;
            this.description = null;
        } else {
            flag = false;
            this.description = description;
        }
        this.ip = ip;
        ip_bin = IPFormat.toBinaryNumber(ip);
        this.mask_ip = mask_ip;
        mask_ip_bin = IPFormat.toBinaryNumber(mask_ip);
        mask_ip_len = IPFormat.count_in_String(mask_ip_bin);
    }
}

class _interface {
    //0:no ip address;1:trunk;2:access
    //3:其他;-1:初始状态;4：有ip地址,可能会有一个默认的vlan
    //5:access,trunk混合模式
    private int flag;
    boolean sub_flag;
    private HashSet<Integer> vlan_set;//trunk和noip默认的vlan
    private HashSet<Integer> vlan_set_access;
    private int mtu;
    private String description;
    private HashMap<Integer, _interface> sub_int_list;
    private String ip;
    private String ip_bin;
    private String ip_mask; //255.255.0.0形式
    private String ip_mask_bin;

    _interface(){
        flag = -1;
        sub_flag = false;
        vlan_set = null;
        vlan_set_access = null;
        mtu = -1;
        description = null;
        sub_int_list = null;
        ip = null;
        ip_bin = null;
        ip_mask = null;
        ip_mask_bin = null;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
    
    public void addvlan(String vlan_seq) {
        if (vlan_set == null)
            vlan_set = new HashSet<>();
        if (!vlan_seq.equals("none")) {
            String[] str = vlan_seq.split(",");
            for (int i = 0; i < str.length; i++) {
                if (str[i].contains("-")) {
                    String[] tem_str = str[i].split("-");
                    for (int j = Integer.parseInt(tem_str[0]); j < Integer.parseInt(tem_str[1]); j++) {
                        vlan_set.add(j);
                    }
                } else vlan_set.add(Integer.parseInt(str[i]));
            }
        }
    }

    public void addvlanset_access(String vlanseq) {
        if (vlan_set_access == null)
            vlan_set_access = new HashSet<>();
        String[] str = vlanseq.split(",");
        for (int i = 0; i < str.length; i++) {
            vlan_set_access.add(Integer.parseInt(str[i]));
        }
    }

    public void setMtu(String mtu) {
        this.mtu = Integer.parseInt(mtu);
    }

    public void setDescription(String description) {
        if (description.length() > 0)
            this.description = description;
    }

    public void addsub_int_list(String id, _interface inf) {
        if (sub_int_list == null)
            sub_int_list = new HashMap<>();
        sub_int_list.put(Integer.parseInt(id), inf);
    }

    public void setIp(String ip) {
        this.ip = ip;
        this.ip_bin = IPFormat.toBinaryNumber(ip);
    }

    public void setIp_mask(String ip_mask) {
        this.ip_mask = ip_mask;
        this.ip_mask_bin = IPFormat.toBinaryNumber(ip_mask);
    }

    public void setSub_flag(boolean sub_flag) {
        this.sub_flag = sub_flag;
    }
    
    public int getFlag() {
    	return flag;
	}
    
    public Set<String> getVlan_set() {
		Set<String> a = new HashSet<>();
		if(vlan_set!=null)
	    	for(Integer v:vlan_set)
			{
				String vv = v.toString();
				a.add(vv);
			}
    	return a;
	}
    
    public Set<String> getVlan_acess_set() {
		Set<String> a = new HashSet<>();
		if(vlan_set_access!=null)
			for(Integer v:vlan_set_access)
			{
				String vv = v.toString();
				a.add(vv);
			}
    	return a;
	}
    
    public String getIP_bin() {
		return ip_bin;
	}
    
    public int getMasklen() {
		return IPFormat.count_in_String(ip_mask_bin);
	}
}

