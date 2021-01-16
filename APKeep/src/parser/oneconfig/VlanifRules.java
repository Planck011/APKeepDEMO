package parser.oneconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * vlan的规则，读取配置的存储结构
 */
class VlanIP{
    private String IPAdd;
    private String mask;
    //private int maskbit = liminglong.oneconfig.util.count_in_String(liminglong.oneconfig.IPFormat.MaskToBinaryNumber(mask));
    VlanIP(String IPAdd, String mask){
        this.IPAdd = IPAdd;
        this.mask = mask;
    }

//    public int getMaskbit() {
//        return maskbit;
//    }


    public String getMask() {
        return mask;
    }

    public String getIPAdd() {
        return IPAdd;
    }

    @Override
    public String toString() {
        return "liminglong.oneconfig.VlanIP{" +
                "IPAdd='" + IPAdd + '\'' +
                ", mask='" + mask + '\'' +
                '}';
    }
}

class VlanIPGroup {
    private ArrayList<VlanIP> IPGroup = new ArrayList<>();

    private HashSet<String> infContain = new HashSet<>();

    public void addIPAdd(VlanIP vlanIP){
        IPGroup.add(vlanIP);
    }

    public void addinfContain(String infName) {
        infContain.add(infName);
    }

    public ArrayList<VlanIP> getIPGroup() {
        return IPGroup;
    }

    public HashSet<String> getInfContain() {
        return infContain;
    }

    public void toDisplay(){
        for (String i : infContain) {
            System.out.print(i + " " +
                    "");
        }
        for (int i = 0; i < IPGroup.size(); i++) {
            System.out.println(IPGroup.get(i).toString());
        }
    }
}

public class VlanifRules {
    private HashMap<Integer, VlanIPGroup> vlanRule = new HashMap<>();

    //设置VlanIPGroup的成员变量infContain，表示一组规则所在的物理接口
    public  void setRuleInf(int id, String infName) {
        vlanRule.get(id).addinfContain(infName);
    }

    public void addRule(int id, VlanIP vlanIP) {
        if (vlanRule.containsKey(id)) {
            vlanRule.get(id).addIPAdd(vlanIP);
        }
        else {
            VlanIPGroup vlanIPGroup = new VlanIPGroup();
            vlanIPGroup.addIPAdd(vlanIP);
            vlanRule.put(id, vlanIPGroup);
        }
    }

    public HashMap<Integer, VlanIPGroup> getVlanRule() {
        return vlanRule;
    }

    public void toDisplay() {
        for (Integer i : vlanRule.keySet()) {
            System.out.println(i);
            vlanRule.get(i).toDisplay();
        }
    }
}
