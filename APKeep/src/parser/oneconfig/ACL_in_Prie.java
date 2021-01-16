package parser.oneconfig;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 定义ACLPrie中节点中的元素结构，存储规则的结构（ACLRules）将规则一条条
 * 插入prie中，即插入该结构中
 */
//class ACLRules_id {
//    int aclnumber;
//    int id_in_aclnumber;
//
//    ACLRules_id(int aclnumber, int id_in_aclnumber) {
//        this.aclnumber = aclnumber;
//        this.id_in_aclnumber = id_in_aclnumber;
//    }
//}
class SingleAclRule {
    private DstIP dstIP;
    private SrcIP srcIP;
    private int groupid;
    private int id_in_group;
    private HashSet<String> inf;

    SingleAclRule(SrcIP srcIP, DstIP dstIP, int groupid, int id_in_group, HashSet<String> inf) {
        this.srcIP = srcIP;
        this.dstIP = dstIP;
        this.groupid = groupid;
        this.id_in_group = id_in_group;
        this.inf = inf;
    }

    public void toDisplay() {
        System.out.println(srcIP.toString() + dstIP.toString()+" group: "+groupid+" id in group: "+id_in_group );
        for (String i:inf
             ) {
            System.out.print(i);
        }
        System.out.println();
    }
}

public class ACL_in_Prie {
    //ACLRules_id aclRules_id;
    //liminglong.oneconfig.SingleAclRule singleAclRule;
    private int count = 1;
    private HashMap<Integer, SingleAclRule> aclRuleHashMap = new HashMap<>();

    public void addRule(SingleAclRule singleAclRule) {
        aclRuleHashMap.put(count, singleAclRule);
        count++;
    }

    public void toDisplay() {
        for (Integer i: aclRuleHashMap.keySet()
             ) {
            System.out.print(i);
            aclRuleHashMap.get(i).toDisplay();
        }
    }
}
