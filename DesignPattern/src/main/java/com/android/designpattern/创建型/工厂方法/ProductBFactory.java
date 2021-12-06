package com.android.designpattern.创建型.工厂方法;

public class ProductBFactory implements CreateProduct{

    @Override
    public AbstractProduct createProduct() {
        return new ProductB();
    }
}
