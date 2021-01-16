package parser.oneconfig;

import java.util.ArrayList;

/**
 * 工具类和调用接口
 */
public class util {
    public static ParseRules parseRules;
    static RulesToPrie rulesToPrie;

    public static int count_in_String(String str) {
        int fromIndex = 0;
        int count = 0;
        while (true) {
            int index = str.indexOf("1", fromIndex);
            if (-1 != index) {
                fromIndex = index + 1;
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    public static void part_1st_Init(String ConfigPath, String TopoPath) {
        //获取配置文件和拓扑文件名
        GetFileName getFileName = new GetFileName(ConfigPath, TopoPath);
        getFileName.start();

        //读取配置并将配置信息记录
        parseRules = new ParseRules();
        parseRules.readFile(getFileName);
        //parseRules.test();

        //将当前配置的规则分别插入到ACL和VLAN的prie树中
        rulesToPrie = new RulesToPrie();
        rulesToPrie.initVlanTrieTree(parseRules.getVlanifRules(), parseRules.getAclRules());
    }

    public static void part_1st_ACLSearch(String ipAddress) {
        ACL_in_Prie a = rulesToPrie.getAclTrieTree().search(IPFormat.toBinaryNumber(ipAddress));
        if (a == null) {
            System.out.println("无相关ACL规则");
        } else {
            a.toDisplay();
        }
    }

    public static void part_1st_VlanSearch(String ipAddress) {
        Vlan_in_Prie v = rulesToPrie.getVlanTrieTree().search(IPFormat.toBinaryNumber(ipAddress));
        if (v == null) {
            System.out.println("无相关VLAN规则");
        } else {
            v.toDisplay();
        }
    }

    //获得设备名称列表
    public static ArrayList<String> getDevName() {
        System.out.println(parseRules.DevNames);//测试
        return parseRules.DevNames;
    }
    //获得设备的接口列表
    public static void getDev_Inf(){
        for (String str1 : parseRules.dev_inf_rules.getDev_Inf_Rules().keySet()
        ) {
            System.out.println(str1+":");//str1为设备名，这里用输出代替操作
            //parseRules.Dev_Inf_Rules.get(str1).
            for (String str2 : parseRules.dev_inf_rules.getDev_Inf_Rules().get(str1).getRealInf().keySet()
            ) {
                System.out.print("\t " + str2 + " ");//str2为接口名
            }
            System.out.println();
        }
    }
    //遍历每个设备的每个接口的规则
    public static void getDev_Inf_Rules() {
        for (String str1 : parseRules.dev_inf_rules.getDev_Inf_Rules().keySet()
        ) {
            System.out.println(str1);//str1为设备名，这里用输出代替操作
            //parseRules.Dev_Inf_Rules.get(str1).
            for (String str2 : parseRules.dev_inf_rules.getDev_Inf_Rules().get(str1).getRealInf().keySet()
            ) {
                System.out.print("\t " + str2 + ":");//str2为接口名
                System.out.print("ACL: ");
                for (int i = 0; i < parseRules.dev_inf_rules.getDev_Inf_Rules().get(str1).getRealInf().get(str2).getAcl_contain().size(); i++) {
                    System.out.print(parseRules.dev_inf_rules.getDev_Inf_Rules().get(str1).getRealInf().get(str2).getAcl_contain().get(i) + " ");
                }
                System.out.print("   Vlan: ");
                for (int i = 0; i < parseRules.dev_inf_rules.getDev_Inf_Rules().get(str1).getRealInf().get(str2).getVlan_contain().size(); i++) {
                    System.out.print(parseRules.dev_inf_rules.getDev_Inf_Rules().get(str1).getRealInf().get(str2).getVlan_contain().get(i) + " ");
                }
                System.out.println();
            }
        }
    }
    //获得指定设备的所有接口规则
    public static void getDev_Inf_Rules(String devName) {
        //parseRules.Dev_Inf_Rules.get(devName);
        System.out.println(devName + ":");
        for (String str2 : parseRules.dev_inf_rules.getDev_Inf_Rules().get(devName).getRealInf().keySet()
        ) {
            System.out.print("\t " + str2 + ":");//str2为接口名
            System.out.print("ACL: ");
            for (int i = 0; i < parseRules.dev_inf_rules.getDev_Inf_Rules().get(devName).getRealInf().get(str2).getAcl_contain().size(); i++) {
                System.out.print(parseRules.dev_inf_rules.getDev_Inf_Rules().get(devName).getRealInf().get(str2).getAcl_contain().get(i) + " ");
            }
            System.out.print("   Vlan: ");
            for (int i = 0; i < parseRules.dev_inf_rules.getDev_Inf_Rules().get(devName).getRealInf().get(str2).getVlan_contain().size(); i++) {
                System.out.print(parseRules.dev_inf_rules.getDev_Inf_Rules().get(devName).getRealInf().get(str2).getVlan_contain().get(i) + " ");
            }
            System.out.println();
        }
    }
    public static void getDev_Inf_Rules(String devName, String infName){
        System.out.println(devName + " "+infName+":");
        parseRules.dev_inf_rules.getDev_Inf_Rules().get(devName).getRealInf().get(infName);
        System.out.print("ACL: ");
        for (int i = 0; i < parseRules.dev_inf_rules.getDev_Inf_Rules().get(devName).getRealInf().get(infName).getAcl_contain().size(); i++) {
            System.out.print(parseRules.dev_inf_rules.getDev_Inf_Rules().get(devName).getRealInf().get(infName).getAcl_contain().get(i) + " ");
        }
        System.out.print("   Vlan: ");
        for (int i = 0; i < parseRules.dev_inf_rules.getDev_Inf_Rules().get(devName).getRealInf().get(infName).getVlan_contain().size(); i++) {
            System.out.print(parseRules.dev_inf_rules.getDev_Inf_Rules().get(devName).getRealInf().get(infName).getVlan_contain().get(i) + " ");
        }
        System.out.println();
    }
    //获得现有VLAN序号
    public static void getVlanNumber(){
        for (Integer i1:parseRules.getVlanifRules().getVlanRule().keySet()
        ) {
            System.out.println("vlan "+i1);
        }
    }
    //获得所有VLAN规则
    public static void getAllVlan(){
        for (Integer i1:parseRules.getVlanifRules().getVlanRule().keySet()
             ) {
            System.out.println("vlan "+i1+":");
            for (int i = 0; i < parseRules.getVlanifRules().getVlanRule().get(i1).getIPGroup().size(); i++) {
                System.out.print("\t");
                System.out.println("src:"+IPFormat.toBinaryNumber(parseRules.getVlanifRules().getVlanRule().get(i1).getIPGroup().get(i).getIPAdd())
                        + " mask:"+parseRules.getVlanifRules().getVlanRule().get(i1).getIPGroup().get(i).getMask());
                IPFormat.toBinaryNumber(parseRules.getVlanifRules().getVlanRule().get(i1).getIPGroup().get(i).getIPAdd());
            }
        }
    }
    //获得指定VLAN号的VLAN规则
    public static void getOneVlan(int o){
        System.out.println("vlan "+o+" ");
        for (int i = 0; i < parseRules.getVlanifRules().getVlanRule().get(o).getIPGroup().size(); i++) {
            System.out.print("\t");
            System.out.println("src:"+IPFormat.toBinaryNumber(parseRules.getVlanifRules().getVlanRule().get(o).getIPGroup().get(i).getIPAdd())
                    + " mask:"+parseRules.getVlanifRules().getVlanRule().get(o).getIPGroup().get(i).getMask());
            IPFormat.toBinaryNumber(parseRules.getVlanifRules().getVlanRule().get(o).getIPGroup().get(i).getIPAdd());
        }
    }

    public static  void test1(){
        parseRules.getDeviceList().toDisplay();
    }
}
