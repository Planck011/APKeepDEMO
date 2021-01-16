package parser.oneconfig;

import java.util.HashMap;
import java.util.HashSet;

class device {
    private HashSet<String> interfaces = new HashSet<>();
    private HashSet<String> vlan_list = new HashSet<>();//所有vlan端口
    private HashSet<String> acls = new HashSet<>();//acl端口
    //public Set<Rule> rules;//所有规则
//    private VlanifRules vlanifRules;
//    private ACLRules aclRules;
    private HashSet<String> vlan_rules = new HashSet<>();//vlan规则id
    private HashSet<String> acl_rules = new HashSet<>();//ACL规则id
    private HashMap<String, HashSet<String>> interfaceMap_vlan = new HashMap<>();//interface->vlans
    private HashMap<String, HashSet<String>> interfaceMap_acl = new HashMap<>();

    public void addinterfaceMap_vlan(String infname, String vlan){
        if(interfaceMap_vlan.containsKey(infname)){
            interfaceMap_vlan.get(infname).add(vlan);
        }else {
            HashSet<String> hashSet=new HashSet<String>();
            hashSet.add(vlan);
            interfaceMap_vlan.put(infname, hashSet);
        }
    }
    public void addinterfaceMap_acl(String infname, String acl){
        if(interfaceMap_acl.containsKey(infname)){
            interfaceMap_acl.get(infname).add(acl);
        }else {
            HashSet<String> hashSet=new HashSet<String>();
            hashSet.add(acl);
            interfaceMap_acl.put(infname, hashSet);
        }
    }
    public void addvlan_rules(String name){
        this.vlan_rules.add(name);
    }
    public void addacl_rules(String name){
        this.acl_rules.add(name);
    }
//    public void addACLRules(int i, ACLGroup aclGroup){
//        this.aclRules.addRule(i, aclGroup);
//    }
//    public void addVlanifRules(int i, String str1, String str2) {
//        //return vlanifRules;
//        this.vlanifRules.addRule(i, new VlanIP(str1, str2));
//    }

    public void addAcls(String name){
        this.acls.add(name);
    }

    public HashSet<String> getAcls() {
        return acls;
    }

    public void addInterfaces(String interfaces) {
        this.interfaces.add(interfaces);
    }


    public void addvlan_list(String vlanName){
        this.vlan_list.add(vlanName);
    }

    public HashSet<String> getVlan_list() {
        return vlan_list;
    }
    public void toDisplay(){
        System.out.println("inface: "+interfaces);
        System.out.println("vlan端口: "+vlan_list);
        System.out.println("acl端口: "+acls);
        System.out.println("VLANid: "+vlan_rules);
        System.out.println("aclid: "+acl_rules);
        for (String str1:interfaceMap_vlan.keySet()
             ) {
            System.out.println(str1+"---"+interfaceMap_vlan.get(str1));
        }
        System.out.println("-------------");
        for (String str2:interfaceMap_acl.keySet()
             ) {
            System.out.println(str2+"---"+interfaceMap_acl.get(str2));
        }

    }
}

//class fwRule{
//    private String port;//所在端口
//    private String match; //匹配域名
//    private int prior;//优先级
//}
//class aclRule{
//    private String group;//所属分组（插入哪个元素），如3001 3002
//    private String port;//permit or deny
//    private String scrIP;
//    private String mask1;
//    private String dstIP;
//    private String mask2;
//    private int prior;//优先级
//}
public class DeviceList {
    private HashMap<String,device> deviceList = new HashMap<>();
    private VlanifRules vlanifRules=new VlanifRules();
    private ACLRules aclRules=new ACLRules();

    public void addACLRules(int i, ACLGroup aclGroup){
        this.aclRules.addRule(i, aclGroup);
    }
    public void addVlanifRules(int i, String str1, String str2) {
        //return vlanifRules;
        this.vlanifRules.addRule(i, new VlanIP(str1, str2));
    }

    public VlanifRules getVlanifRules() {
        return vlanifRules;
    }

    public ACLRules getAclRules() {
        return aclRules;
    }

    public void adddevicList(String name, device device1){
        this.deviceList.put(name, device1);
    }

    public void toDisplay(){
        for (String s: deviceList.keySet()
             ) {
            System.out.println(s+":");
            deviceList.get(s).toDisplay();
        }
        vlanifRules.toDisplay();
        aclRules.toDisplay();
    }
}


