package util;

import com.intellij.openapi.util.IconLoader;

import javax.swing.Icon;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/26 19:00
 */
public class IconUtil {
    public static String BO_LUO_SVG_16 = "/icon/boLuo16.svg";
    public static String ADD_PNG = "/icon/add.png";
    public static String ADD2_PNG = "/icon/add2.png";
    public static String DELETE_PNG = "/icon/delete.png";
    public static String DELETE2_PNG = "/icon/delete2.png";

    public static Icon getIcon(String iconPath) {
        return IconLoader.getIcon(iconPath, IconUtil.class);
    }
}
