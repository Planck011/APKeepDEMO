package parser.oneconfig;

/**
 * vlanprie
 */

import java.util.HashSet;

public class VlanTrieTree {
    private Node root = null;   //根节点

    /*二叉树的节点*/
    private class Node {
        Vlan_in_Prie vlan_in_prie;// = new liminglong.oneconfig.Vlan_in_Prie();
        //boolean flag;
        Node[] children; //左孩子节点为0 右孩子节点为1

        public Node() {
            vlan_in_prie = null;
            //flag = false;
            children = new Node[2];
            for (int i = 0; i < children.length; i++) {
                children[i] = null;
            }
        }
    }

    public VlanTrieTree() {
        root = new Node();
    }

    public void insert(Node root, String ipAddress, int isp, HashSet<String> inf) {
        if (ipAddress.length() > 32) {
            System.out.println("ip地址处理错误");
        } else {
            Node crawl = root;
            for (int i = 0; i < ipAddress.length(); i++) {
                int index = (int) ipAddress.charAt(i) - '0';
                if (crawl.children[index] == null) {
                    crawl.children[index] = new Node();
                }
                crawl = crawl.children[index];
            }
            crawl.vlan_in_prie = new Vlan_in_Prie();
            //crawl.flag = true;
            crawl.vlan_in_prie.addId(isp);
            crawl.vlan_in_prie.setInf(inf);
        }
    }

    public void insert(String ipAddress, int isp, HashSet<String> inf) {
        insert(root, ipAddress, isp, inf);
    }

    /*
     * 检索ip地址，返回其所对应的ISP
     * 若不在Trie树中，则返回null
     * */
    public Vlan_in_Prie search(String binaryIP) {
        Node crawl = root;
        for (int i = 0; crawl.vlan_in_prie == null; i++) {
            int index = (int) binaryIP.charAt(i) - '0';
            if (crawl.children[index] == null) {
                return null;
            }
            crawl = crawl.children[index];
        }
        return crawl.vlan_in_prie;
    }
}