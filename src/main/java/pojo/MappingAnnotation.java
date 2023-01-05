package pojo;

import constant.COMMON;
import util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        if (!this.method.equals(mappingAnnotation.getMethod())) {
            return false;
        }
        List<String> urlList = Arrays.stream(this.url.split(COMMON.SLASH)).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
        List<String> targetUrlList = Arrays.stream(mappingAnnotation.getUrl().split(COMMON.SLASH)).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
        int urlSize = urlList.size();
        if (urlSize != targetUrlList.size()) {
            return false;
        }
        for (int i = 0; i < urlSize; i++) {
            if (!urlList.get(i).equals(targetUrlList.get(i))) {
                return false;
            }
        }
        return true;
    }
}
