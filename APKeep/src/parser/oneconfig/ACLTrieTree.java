package parser.oneconfig;

/*
 * Trie树，用于存储、检索ip地址
 * 叶子节点标记为ip地址对应的规则
 */
public class ACLTrieTree {
    private Node root = null;   //根节点

    /*二叉树的节点*/
    private static class Node {
        //String element;  //非叶子节点为空 叶子节点标记为ISP
        //liminglong.oneconfig.SingleAclRule singleAclRule;
        ACL_in_Prie acl_in_prie;
        Node[] children; //左孩子节点为0 右孩子节点为1

        public Node() {
            //element = "";
            //singleAclRule = null;
            acl_in_prie = null;
            children = new Node[2];
            for (int i = 0; i < children.length; i++) {
                children[i] = null;
            }
        }
    }

    public ACLTrieTree() {
        root = new Node();
    }

    /*插入ip地址*/
    public void insert(Node root, String ipAddress, SingleAclRule isp) {
        if(ipAddress.length() > 32) {
            System.out.println("ip地址处理错误");
        } else {
            Node crawl = root;
            for(int i=0; i<ipAddress.length(); i++) {
                int index = (int) ipAddress.charAt(i) - '0';
                if(crawl.children[index] == null) {
                    crawl.children[index] = new Node();
                }
                crawl = crawl.children[index];
            }
            if (crawl.acl_in_prie==null)
                crawl.acl_in_prie = new ACL_in_Prie();

            crawl.acl_in_prie.addRule(isp);
        }
    }

    public void insert(String ipAddress, SingleAclRule isp) {
        insert(root, ipAddress, isp);
    }

    /*
     * 检索ip地址，返回其所对应的ISP
     * 若不在Trie树中，则返回null
     * */
    public ACL_in_Prie search(String binaryIP) {
        Node crawl = root;
        for(int i = 0; crawl.acl_in_prie == null; i++) {
            int index = (int) binaryIP.charAt(i) - '0';
            if(crawl.children[index] == null) {
                return null;
            }
            crawl = crawl.children[index];
        }
        return crawl.acl_in_prie;
    }
}