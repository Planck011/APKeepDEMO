package parser.oneconfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 配置解析，将规则从配置文件中读出到结构中（ACLRules,VlanifRules）
 */
public class ParseRules {
    //vlan的配置
    private VlanifRules vlanifRules = new VlanifRules();
//1 ***********************************
    //acl的配置
    private ACLRules aclRules =new ACLRules();
//1 ***************************************
    //物理接口包含的规则
    RealInf_Rules realInf_rules = new RealInf_Rules();
    //设备名称
    ArrayList<String> DevNames = new ArrayList<>();

    DeviceList deviceList = new DeviceList();

    //HashMap<String, RealInf_Rules> Dev_Inf_Rules = new HashMap<>();
    Dev_Inf_Rules dev_inf_rules = new Dev_Inf_Rules();

    public void readFile(GetFileName gfn){
        for (String e : gfn.getConfigList()) {
            File file = new File(gfn.getConfigPath() + "\\" + e);
            device device1 = new device();
            //VlanifRules vlanifRules1 = new VlanifRules();
            String DevName = e.substring(0, e.length()-4);
            DevNames.add(DevName);
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String tempString = null;
                while ((tempString = reader.readLine()) != null) {
                    if (tempString.length() <= 9)
                        continue;
                    if (tempString.startsWith("acl")){
                        int aclId = Integer.valueOf(tempString.substring(11));
//1 **************用ACLRules类结构保存ACL规则,该类是成组包含ACL规则**
                        ACLGroup aclGroup = new ACLGroup();
                        ACLGroup aclGroup1 = new ACLGroup();
                        int line = 1;
                        int line1 = 1;
                        while ((tempString = reader.readLine()).charAt(0) != '#'){
                            String[] str = tempString.split(" ");
                            SrcIP srcIP = new SrcIP(str[6], str[7], line);
                            SrcIP srcIP1 = new SrcIP(IPFormat.toBinaryNumber(str[6]),str[7], line1);
                            DstIP dstIP = new DstIP(str[9], str[10], line);
                            DstIP dstIP1 = new DstIP(IPFormat.toBinaryNumber(str[9]), str[10], line1);
                            aclGroup.addRule(srcIP, dstIP);
                            aclGroup1.addRule(srcIP1, dstIP1);
                            line++;
                            line1++;
                        }
                        aclRules.addRule(aclId, aclGroup);
                        deviceList.addACLRules(aclId, aclGroup1);
                        //device1.addacl_rules(String.valueOf(aclId));
//1 ************************************************************
                    }
                    if (tempString.startsWith("interface Vlanif")){
                        int vlanifId = Integer.valueOf(tempString.substring(16));
                        //device1.addvlan_rules(String.valueOf(vlanifId));
                        while ((tempString = reader.readLine()).charAt(0) != '#'){
                            String[] str = tempString.split(" ");
                            vlanifRules.addRule(vlanifId, new VlanIP(str[3], str[4]));

                            //vlanifRules1.addRule(vlanifId, new VlanIP(IPFormat.toBinaryNumber(str[3]), str[4]));
                            deviceList.addVlanifRules(vlanifId, IPFormat.toBinaryNumber(str[3]), str[4]);
                        }
                    }
                    if (tempString.startsWith("interface GE")){
                        String infName = DevName + tempString.substring(10);
                        device1.addInterfaces(infName.substring(DevName.length()));
                        Rules_Contain rules_contain = new Rules_Contain();
                        //RealInf_Rules realInf_rules1 = new RealInf_Rules();
                        while ((tempString = reader.readLine()).charAt(0) != '#'){
                            if (tempString.startsWith(" port trunk allow-pass vlan")){
                                String tmp = tempString.substring(28);
                                String[] str = tmp.split(" ");
                                for (int i = 0; i < str.length; i++) {
                                    rules_contain.addVlan(Integer.valueOf(str[i]));
                                    vlanifRules.setRuleInf(Integer.valueOf(str[i]), infName);
                                    device1.addvlan_rules(str[i]);
                                    device1.addvlan_list(infName.substring(DevName.length()));
                                    device1.addinterfaceMap_vlan(infName.substring(DevName.length()), str[i]);
                                }
                            }
                            if (tempString.startsWith(" port default vlan")){
                                String tmp = tempString.substring(19);
                                String[] str = tmp.split(" ");
                                for (int i = 0; i < str.length; i++) {
                                    rules_contain.addVlan(Integer.valueOf(str[i]));
                                    vlanifRules.setRuleInf(Integer.valueOf(str[i]), infName);
                                    device1.addvlan_list(infName.substring(DevName.length()));
                                    device1.addinterfaceMap_vlan(infName.substring(DevName.length()), str[i]);
                                    device1.addvlan_rules(str[i]);
                                }
                            }
                            if (tempString.startsWith(" traffic-filter inbound acl")){
                                String tmp = tempString.substring(28);
                                String[] str = tmp.split(" ");
                                for (int i = 0; i < str.length; i++) {
                                    rules_contain.addACL(Integer.valueOf(str[i]));
//1 *********************对应ACLRules设置接口，注释掉上面ACLRules改行也需注释
                                    aclRules.setRuleInf(Integer.valueOf(str[i]), infName);
//1 *********************************************************************
                                    device1.addAcls(infName.substring(DevName.length()));
                                    //device1.addacl_rules(str[i]);
                                    device1.addinterfaceMap_acl(infName.substring(DevName.length()), str[i]);
                                    device1.addacl_rules(str[i]);
                                }
                            }
                        }
                        realInf_rules.addRealInf(infName, rules_contain);
                        //realInf_rules1.addRealInf(infName.substring(DevName.length()), rules_contain);
                        dev_inf_rules.adds(DevName, infName.substring(DevName.length()),rules_contain);
                    }
                    else {
                        continue;
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e2) {
                    }
                }
            }
            deviceList.adddevicList(DevName, device1);
        }

    }

    public VlanifRules getVlanifRules() {
        return vlanifRules;
    }

    public ACLRules getAclRules() {
        return aclRules;
    }

    public void test() {
        aclRules.toDisplay();
        vlanifRules.toDisplay();
        realInf_rules.toDisplay();
    }

    public DeviceList getDeviceList() {
        return deviceList;
    }
}
