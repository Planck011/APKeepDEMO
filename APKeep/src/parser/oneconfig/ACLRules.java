package parser.oneconfig;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 定义ACL规则的结构，从配置中读取规则存到该结构中，之后遍历该结构
 * 中的规则插入prie中
 */
class SrcIP{
    private int id;
    private String address;
    private String mask;
    private int maskbit;
    SrcIP(String address, String mask, int id){
        this.address = address;
        this.mask = IPFormat.MaskToBinaryNumber(mask);
        maskbit = util.count_in_String(IPFormat.MaskToBinaryNumber(mask));
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public int getMaskbit() {
        return maskbit;
    }

    @Override
    public String toString() {
        return "liminglong.oneconfig.SrcIP{" +
                "address='" + address + '\'' +
                ", mask='" + mask + '\'' +
                ", maskbit="+maskbit+
                '}';
    }

    public int getId() {
        return id;
    }
}

class DstIP{
    int id;
    String address;
    String mask;
    int maskbit;
    DstIP(String address, String mask, int id){
        this.address = address;
        this.mask = IPFormat.MaskToBinaryNumber(mask);
        maskbit = util.count_in_String(IPFormat.MaskToBinaryNumber(mask));
        this.id = id;
    }

    @Override
    public String toString() {
        return "liminglong.oneconfig.DstIP{" +
                "address='" + address + '\'' +
                ", mask='" + mask + '\'' +
                ", maskbit="+maskbit+
                '}';
    }
}

class ACLGroup {
    private HashMap<SrcIP, DstIP> aclGroup = new HashMap<>();
    private HashSet<String> infContain = new HashSet<>();

    public void addRule(SrcIP src, DstIP dst){
        aclGroup.put(src, dst);
    }

    public void addinf(String infName) {
        infContain.add(infName);
    }

    public HashSet<String> getInfContain() {
        return infContain;
    }

    public HashMap<SrcIP, DstIP> getAclGroup() {
        return aclGroup;
    }

    public void toDisplay(){
        for (String i : infContain) {
            System.out.print(i + " ");
        }
        System.out.println();
        for (SrcIP i : aclGroup.keySet()){
            System.out.println(i.toString()+ " " + aclGroup.get(i).toString());
        }
    }
}

public class ACLRules {
    private HashMap<Integer, ACLGroup> aclRule = new HashMap<>();

    public void setRuleInf(int id, String infName) {
        aclRule.get(id).addinf(infName);
    }

    public void addRule(int id, ACLGroup aclGroup) {
           aclRule.put(id, aclGroup);
    }

    public void toDisplay() {
        for (Integer i : aclRule.keySet()) {
            System.out.println(i);
            aclRule.get(i).toDisplay();
        }
    }

    public HashMap<Integer, ACLGroup> getAclRule() {
        return aclRule;
    }
}
