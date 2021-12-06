package com.android.designpattern.创建型.工厂方法;

public class ProductCFactory implements CreateProduct{

    @Override
    public AbstractProduct createProduct() {
        return new ProductC();
    }
}
