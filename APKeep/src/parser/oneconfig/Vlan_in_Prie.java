package parser.oneconfig;

import java.util.HashSet;

/**
 * VLAN在prie中节点的结构
 */
public class Vlan_in_Prie {
    private HashSet<Integer> vlan_id = new HashSet<>();
    private HashSet<String> inf = new HashSet<>();

    public void addId(int id) {
        vlan_id.add(id);
    }

    public void setInf(HashSet<String> infname) {
        inf = infname;
    }

    public HashSet<Integer> getVlan_id() {
        return vlan_id;
    }

    public void toDisplay() {
        System.out.print("vlanid: ");
        for (int i:vlan_id
             ) {
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.print("inf: ");
        for (String i:inf
        ) {
            System.out.print(i + " ");
        }
        System.out.println();
    }
}
