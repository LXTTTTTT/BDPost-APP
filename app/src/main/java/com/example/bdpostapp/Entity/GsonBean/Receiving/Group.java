package com.example.bdpostapp.Entity.GsonBean.Receiving;

import java.util.ArrayList;
import java.util.List;

public class Group {
    public String id;
    public String name = "";
    public boolean selected;
    public boolean expanded;
    public Object commandCard; // 如果类型已知，可以替换为具体的类
    public List<Terminal> terminals = new ArrayList<>();
}
