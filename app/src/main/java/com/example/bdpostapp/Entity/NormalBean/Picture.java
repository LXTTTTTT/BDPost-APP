package com.example.bdpostapp.Entity.NormalBean;

// 图片实体类
public class Picture {

    private String path;
    private String name;
    private long createTime;
    private boolean isSelected = false;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "PhotoItem{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", createTime=" + createTime +
                ", isSelected=" + isSelected +
                '}';
    }

}

