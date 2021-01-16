package parser.oneconfig;

import java.util.HashSet;

/**
 * 将现有规则插入到prie中
 */
public class RulesToPrie {
    private ACLTrieTree aclTrieTree = new ACLTrieTree();
    private VlanTrieTree vlanTrieTree = new VlanTrieTree();

    public void initVlanTrieTree(VlanifRules vlanifRules, ACLRules aclRules) {
        //vlan
        for (Integer i : vlanifRules.getVlanRule().keySet()
             ) {
            for (VlanIP j : vlanifRules.getVlanRule().get(i).getIPGroup()
                 ) {
                HashSet<String> inf = vlanifRules.getVlanRule().get(i).getInfContain();
                String str = IPFormat.toBinaryNumber(j.getIPAdd()).substring(0, Integer.parseInt(j.getMask()));
                vlanTrieTree.insert(str, i , inf);
            }
        }

        //acl
        for (Integer i:aclRules.getAclRule().keySet()
             ) {
            for (SrcIP j:aclRules.getAclRule().get(i).getAclGroup().keySet()){
                SingleAclRule singleAclRule = new SingleAclRule(j, aclRules.getAclRule().get(i).getAclGroup().get(j),
                        i, j.getId(), aclRules.getAclRule().get(i).getInfContain());
                String str = IPFormat.toBinaryNumber(j.getAddress()).substring(0, j.getMaskbit());
                aclTrieTree.insert(str, singleAclRule);
            }



        }
    }

    public VlanTrieTree getVlanTrieTree() {
        return vlanTrieTree;
    }

    public ACLTrieTree getAclTrieTree() {
        return aclTrieTree;
    }
}
