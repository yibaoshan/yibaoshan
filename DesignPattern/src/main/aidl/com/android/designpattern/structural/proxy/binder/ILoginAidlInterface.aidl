// ILoginAidlInterface.aidl
package com.android.designpattern.structural.proxy.binder;


interface ILoginAidlInterface {

    void connect();

    void disconnect();

    int login(String id,String pwd);

}