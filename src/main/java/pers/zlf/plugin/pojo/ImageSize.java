package pers.zlf.plugin.pojo;

/**
 * @author zhanglinfeng
 * @date create in 2025/6/10 12:49
 */
public class ImageSize {
    private float width;
    private float height;

    public ImageSize() {
    }

    public ImageSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }
}
