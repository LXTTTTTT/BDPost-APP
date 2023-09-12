package com.example.bdpostapp.Entity.GsonBean.Receiving;

// 标准 GSON 解析类：登录后返回的账户信息
public class Account {

    public String token;//
    public String account;//
    public String name;//
    public String phone;//
    public String email;//
    public String avatar;//
    public String platformLogo;//
    public String platformName;//
    public Role role = new Role();
    public class Role{
        public String name;
        public String value;
    }
    public MapLayer mapLayer;//
    public class MapLayer{
        public String name;
        public String value;
    }

}
