package com.example.bdpostapp.Entity.GsonBean.Receiving;

// 基本请求结构，T 定义返回的 “data” 数据内容
public class BaseRequest<T> {

    public int code;
    public String msg;
    public T data;

    @Override
    public String toString() {
        return "BaseRequest{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
