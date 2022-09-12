package util;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/9 9:51
 */
public class StringUtil {

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String toLowerCaseFirst(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] <= 90) {
            ch[0] = (char) (ch[0] + 32);
        }
        return new String(ch);
    }

    public static String toUpperCaseFirst(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] > 90) {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

}
