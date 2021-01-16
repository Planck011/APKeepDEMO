package parser.oneconfig;

import liminglong.oneconfig.DeviceList;
import liminglong.oneconfig.util;

public class TestMain {
    public static void main(String[] args) {
        //liminglong.oneconfig 使用
        String ConfigPath = "C:\\Users\\liminglong\\IdeaProjects\\APKeepTest\\Resouses\\Config";
        String TopoPath = "C:\\Users\\liminglong\\IdeaProjects\\APKeepTest\\Resouses\\Topo";
        //读取配置初始化，读配置文件，存储规则，将规则插入prie树
        util.part_1st_Init(ConfigPath, TopoPath);
//        //根据IP地址(source)查询现存ACL规则
//        util.part_1st_ACLSearch("10.168.4.0");
//        System.out.println("***************************");
//        //如没有
//        util.part_1st_ACLSearch("10.168.12.9");
//        //根据IP地址查询现存vlan规则
//        System.out.println("***************************");
//        util.part_1st_VlanSearch("10.168.102.3");
//        //如没有
//        System.out.println("***************************");
//        util.part_1st_VlanSearch("10.168.18.0");

//        util.getDevName();
//        util.getDev_Inf();
//        util.getDev_Inf_Rules();
//        util.getDev_Inf_Rules("LSW1");
//        util.getDev_Inf_Rules("LSW1", "GE0/0/1");
//        util.getVlanNumber();
//        util.getAllVlan();
//        util.getOneVlan(3);

        DeviceList dd =  util.parseRules.getDeviceList();
        util.test1();
    }
}
