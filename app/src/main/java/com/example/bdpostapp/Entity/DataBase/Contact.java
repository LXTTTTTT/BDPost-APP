package com.example.bdpostapp.Entity.DataBase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

// 联系人实体类
@Entity
public class Contact {

    @Id(autoincrement = true)  // 设置为主键，autoincrement = true - 自增
    private Long id;

    public String name;  // 名字
    public String phone;  // 手机号码
    public String card_number;  // 首字母

// 自动生成，修改后下面的全部删掉再重新编译 ----------------------------------
    @Generated(hash = 1514783639)
    public Contact(Long id, String name, String phone, String card_number) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.card_number = card_number;
    }
    @Generated(hash = 672515148)
    public Contact() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhone() {
        return this.phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getCard_number() {
        return this.card_number;
    }
    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }


}
