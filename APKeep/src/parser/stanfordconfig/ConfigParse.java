package parser.stanfordconfig;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * @version 1.0
 * @ClassName ConfigParse
 * @Description
 * @Author liminglong
 * @Date 2021/1/7 21:02
 */
public class ConfigParse {
    private String config_path;
    //路由设备名称集合
    private HashSet<String> routerNameSet;
    //路由设备信息
    private ArrayList<Router> routerList;

    ConfigParse(String config_path){
        this.config_path = config_path;
        routerNameSet = getRouterName(util.getFileName(config_path));
        routerList = new ArrayList<>();
    }

    public void start(){
        for (String e: routerNameSet){
            Router router = new Router(e);
            File file = new File(config_path + "\\" + e + "_rtr_config.txt");
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String tempLine = null;
                while ((tempLine = reader.readLine()) != null) {
                    if (tempLine.startsWith("access-list")) {
                        String[] str = tempLine.split(" +");
                        if (str[2].equals("remark"))
                            router.addAcl(Integer.parseInt(str[1]), new Acl_Rule(tempLine));
                        else if (str.length == 4)
                            router.addAcl(Integer.parseInt(str[1]), new Acl_Rule(str[2], str[3], "no"));
                        else if (str.length == 5) {
                            router.addAcl(Integer.parseInt(str[1]), new Acl_Rule(str[2], str[3], str[4]));
                        }
                        else if (str[3].equals("tcp") || str[3].equals("udp") || str[3].equals("ip"))
                            router.addAcl(Integer.parseInt(str[1]), new Acl_Rule(tempLine));
                    }
                    else if (Pattern.matches("vlan +\\d+", tempLine)) {
                        String[] str1 = tempLine.split(" +");
                        tempLine = reader.readLine();
                        String[] str2 = tempLine.split(" +");
                        if (!Pattern.matches("d+\\.d+\\.d+\\.d+/d+", str2[2]))
                            router.addVlan(Integer.parseInt(str1[1]), new Vlan_Rule(str2[2]));
                        else {
                            String[] str3 = str2[2].split("/");
                            router.addVlan(Integer.parseInt(str1[1]), new Vlan_Rule(str3[0], str3[1]));
                        }
                    }
                    else if (tempLine.startsWith("interface Loopback")) {
                        int loopback_name = Integer.parseInt(tempLine.substring(18));
                        tempLine = reader.readLine();
                        String description = "";
                        String[] str = null;
                        if (tempLine.startsWith(" ip address"))
                            str = tempLine.split(" ");
                        tempLine = reader.readLine();
                        while (!tempLine.equals("!")) {
                            description = description.concat("#").concat(tempLine);
                            tempLine = reader.readLine();
                        }
                        if (description.length() > 2)
                            router.addLoopback(loopback_name, new Loopback_Int(str[3], str[4], description));
                        else router.addLoopback(loopback_name, new Loopback_Int(str[3], str[4], "no"));
                    }
                    else if (tempLine.startsWith("interface ") && tempLine.contains("/")) {
                        if (!tempLine.contains(".")) {
                            String temname = tempLine.substring(10);
                            router.addIntface(temname, run_inf_parse(reader, tempLine));
                        }
                        else {
                            String[] strings = tempLine.substring(10).split("\\.");
                            router.add_sub_int(strings[0], strings[1], run_inf_parse(reader, tempLine));
                        }
                    }
                    else if (tempLine.startsWith("interface Vlan")) {
                        String tename = tempLine.substring(10);
                        router.addvlanif(tename, run_inf_parse(reader, tempLine));
                    }
                }
            } catch (IOException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
            routerList.add(router);
        }
    }

    public HashSet<String> getRouterName(HashSet<String> fileName){
        HashSet<String> routerName = new HashSet<>();
        for (String i: fileName){
            String[] temp = i.split("_");
            routerName.add(temp[0]);
        }
        return routerName;
    }

    public _interface run_inf_parse(BufferedReader reader, String tempLine) throws IOException {
        _interface temp_int = new _interface();
        int temp_flag = -1;
        String temp_description = "";
        tempLine = reader.readLine();
        while (!tempLine.equals("!")) {
            if (tempLine.equals(" switchport") || tempLine.equals(" switchport trunk encapsulation dot1q"))
                ;
            else if (tempLine.startsWith(" switchport trunk allowed vlan ")) {
                if (tempLine.startsWith(" switchport trunk allowed vlan add"))
                    temp_int.addvlan(tempLine.substring(35));
                else
                    temp_int.addvlan(tempLine.substring(31));
            }
            else if (tempLine.startsWith(" switchport mode ")) {
                if (tempLine.substring(17).equals("trunk")) {
                    if (temp_flag == -1)
                        temp_flag = 1;
                    if (temp_flag == 2)
                        temp_flag = 5;
                    temp_int.setFlag(temp_flag);
                } else if (tempLine.substring(17).equals("access")) {
                    temp_flag = 2;
                    temp_int.setFlag(2);
                }
            } else if (tempLine.startsWith(" mtu"))
                temp_int.setMtu(tempLine.substring(5));
            else if (tempLine.equals(" no ip address")) {
                temp_flag = 0;
                temp_int.setFlag(0);
            } else if (tempLine.startsWith(" ip address")) {
                if (!tempLine.endsWith("secondary")) {
                    temp_flag = 4;
                    temp_int.setFlag(4);
                    String[] str = tempLine.substring(12).split(" ");
                    temp_int.setIp(str[0]);
                    temp_int.setIp_mask(str[1]);
                }
                else temp_description = temp_description.concat("#").concat(tempLine);
            } else if (tempLine.startsWith(" encapsulation dot1Q "))
                temp_int.addvlan(tempLine.substring(21));
            else if (tempLine.startsWith(" switchport access vlan")) {
                if (temp_flag == -1)
                    temp_flag = 2;
                if (temp_flag == 1)
                    temp_flag = 5;
                temp_int.setFlag(temp_flag);
                temp_int.addvlanset_access(tempLine.substring(24));
            } else temp_description = temp_description.concat("#").concat(tempLine);
            tempLine = reader.readLine();
        }
        if (temp_flag == -1)
            temp_int.setFlag(3);
        temp_int.setDescription(temp_description);
        return temp_int;
    }
    
    public ArrayList<Router> getRouterlist(){
    	return routerList;
    }
}
