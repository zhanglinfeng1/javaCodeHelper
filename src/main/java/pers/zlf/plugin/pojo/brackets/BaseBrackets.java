package pers.zlf.plugin.pojo.brackets;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/26 10:13
 */
public abstract class BaseBrackets {
    protected String displayName;
    protected String externalName;
    protected String tagName;
    protected String LBrackets;
    protected String RBrackets;

    public String getRBrackets() {
        return RBrackets;
    }

    public void setRBrackets(String RBrackets) {
        this.RBrackets = RBrackets;
    }

    public String getLBrackets() {
        return LBrackets;
    }

    public void setLBrackets(String LBrackets) {
        this.LBrackets = LBrackets;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getExternalName() {
        return externalName;
    }

    public void setExternalName(String externalName) {
        this.externalName = externalName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
