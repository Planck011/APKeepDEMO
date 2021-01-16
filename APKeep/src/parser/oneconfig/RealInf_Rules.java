package liminglong.oneconfig;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 存储物理接口包含的规则
 */
public class RealInf_Rules {
    private HashMap<String, Rules_Contain> realInf = new HashMap<>();

    public void addRealInf(String infName, Rules_Contain rules_contain) {
        realInf.put(infName, rules_contain);
    }

    public HashMap<String, Rules_Contain> getRealInf() {
        return realInf;
    }

    public void toDisplay() {
        for (String i : realInf.keySet()) {
            System.out.println(i);
            realInf.get(i).toDisplay();
        }
    }
}

//class Dev_Inf_Rules {}

class Rules_Contain{
    private ArrayList<Integer> acl_contain = new ArrayList<Integer>();
    private ArrayList<Integer> vlan_contain = new ArrayList<Integer>();

    public ArrayList<Integer> getAcl_contain() {
        return acl_contain;
    }

    public ArrayList<Integer> getVlan_contain() {
        return vlan_contain;
    }

    public void addACL(int id) {
        acl_contain.add(id);
    }

    public void addVlan(int id) {
        vlan_contain.add(id);
    }

    public void toDisplay() {
        System.out.print("ACL: ");
        for (int i = 0; i < acl_contain.size(); i++) {
            System.out.print(acl_contain.get(i) + " ");
        }
        System.out.print("Vlan: ");
        for (int i = 0; i < vlan_contain.size(); i++) {
            System.out.print(vlan_contain.get(i) + " ");
        }
        System.out.println();
    }
}

class Dev_Inf_Rules{
    HashMap<String, RealInf_Rules> dev_Inf_Rules = new HashMap<>();

    public void adds(String devName, String infName, Rules_Contain rules_contain){
        if(dev_Inf_Rules.containsKey(devName)){
            dev_Inf_Rules.get(devName).addRealInf(infName, rules_contain);
        }
        else{
            RealInf_Rules realInf_rules = new RealInf_Rules();
            realInf_rules.addRealInf(infName,rules_contain);
            dev_Inf_Rules.put(devName, realInf_rules);
        }
    }

    public HashMap<String, RealInf_Rules> getDev_Inf_Rules() {
        return dev_Inf_Rules;
    }
}
