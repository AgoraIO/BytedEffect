package io.agora.rtcwithbyte.model;

public class ButtonItem {
    // 一般情况下 id 为正数，当 id 为 -1 时表示这是一个功能关闭按钮
    private int icon;
    private String title;
    private String desc;
    private ComposerNode node;

    public ButtonItem(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public ButtonItem(int icon, String title, String desc) {
        this.icon = icon;
        this.title = title;
        this.desc = desc;
    }

    public ButtonItem(int icon, String title, ComposerNode node) {
        this.icon = icon;
        this.title = title;
        this.node = node;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ComposerNode getNode() {
        return node;
    }

    public void setNode(ComposerNode node) {
        this.node = node;
    }
}
