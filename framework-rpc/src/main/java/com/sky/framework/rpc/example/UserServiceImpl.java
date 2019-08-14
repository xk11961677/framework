package com.sky.framework.rpc.example;

/**
 * @author
 */
public class UserServiceImpl implements UserService {

    @Override
    public String hello(String msg) {
        System.out.println("msg:{}" + msg);
        return msg + "[response]";
    }

    @Override
    public void hello() {
        System.out.println("no msg:{}");
    }
}
