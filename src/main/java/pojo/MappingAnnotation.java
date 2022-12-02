package pojo;

import constant.COMMON;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/28 10:42
 */
public class MappingAnnotation {
    private String url;
    private String method;

    public MappingAnnotation() {
    }

    public MappingAnnotation(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public boolean equals(MappingAnnotation mappingAnnotation) {
        String noSlashUrl = this.url.replaceAll(COMMON.SLASH, COMMON.BLANK_STRING);
        String noSlashTargetUrl = mappingAnnotation.getUrl().replaceAll(COMMON.SLASH, COMMON.BLANK_STRING);
        return (this.url.equals(mappingAnnotation.getUrl()) || noSlashUrl.equals(noSlashTargetUrl)) && this.method.equals(mappingAnnotation.getMethod());
    }
}
