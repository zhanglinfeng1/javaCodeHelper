package pers.zlf.plugin.pojo.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanglinfeng
 * @date create in 2023/7/20 11:59
 */
public class FastJumpConfig {
    /** controller所在文件夹名 */
    private String controllerFolderName;
    /** feign所在文件夹名 */
    private String feignFolderName;
    /** 网关模块名 */
    private List<String> moduleNameList = new ArrayList<>();

    public String getControllerFolderName() {
        return controllerFolderName;
    }

    public void setControllerFolderName(String controllerFolderName) {
        this.controllerFolderName = controllerFolderName;
    }

    public String getFeignFolderName() {
        return feignFolderName;
    }

    public void setFeignFolderName(String feignFolderName) {
        this.feignFolderName = feignFolderName;
    }

    public List<String> getModuleNameList() {
        return moduleNameList;
    }

    public void setModuleNameList(List<String> moduleNameList) {
        this.moduleNameList = moduleNameList;
    }
}
