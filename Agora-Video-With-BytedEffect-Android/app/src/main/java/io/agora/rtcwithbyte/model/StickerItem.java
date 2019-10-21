package io.agora.rtcwithbyte.model;

/**
 * Created by QunZhang on 2019-07-21 12:25
 */
public class StickerItem extends FilterItem {
    private String tip;

    public StickerItem(String title, int icon, String resource) {
        super(title, icon, resource);
    }

    public StickerItem(String title, int icon, String resource, String tip) {
        super(title, icon, resource);
        this.tip = tip;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public boolean hasTip() {
        return tip != null;
    }
}
