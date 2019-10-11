package io.agora.rtcwithbyte.model;

/**
 * Created by QunZhang on 2019-07-21 12:23
 */
public class FilterItem {
    private String title;
    private int icon;
    private String resource;

    public FilterItem(String title, int icon, String resource) {
        this.title = title;
        this.icon = icon;
        this.resource = resource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
