package parser.oneconfig;

import java.io.File;
import java.util.ArrayList;

/**
 * 获取配置文件名
 */
public class GetFileName {
    //配置、拓扑文件路径
    private String ConfigPath;
    private String TopoPath;
    //配置文件、拓扑文件名记录
    private ArrayList<String> configList= new ArrayList<String>();
    private ArrayList<String> topoList = new ArrayList<String>();

    GetFileName(String ConfigPath, String TopoPath){
        this.ConfigPath = ConfigPath;
        this.TopoPath = TopoPath;
    }

    public void start(){
        File file1 = new File(ConfigPath);
        File file2 = new File(TopoPath);
        File[] files1 = file1.listFiles();
        File[] files2 = file2.listFiles();
        for (int i = 0; i < files1.length; i++){
            configList.add(files1[i].getName());
        }
        for (int i = 0; i < files2.length; i++){
            topoList.add(files2[i].getName());
        }
    }

    public ArrayList<String> getConfigList() {
        return configList;
    }

    public ArrayList<String> getTopoList() {
        return topoList;
    }

    public String getConfigPath() {
        return ConfigPath;
    }

    public String getTopoPath() {
        return TopoPath;
    }
}
