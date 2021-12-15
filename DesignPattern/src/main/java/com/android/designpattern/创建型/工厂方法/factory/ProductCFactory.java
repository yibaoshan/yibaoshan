package com.android.designpattern.创建型.工厂方法.factory;

import com.android.designpattern.创建型.工厂方法.abstractproduct.AbstractProduct;
import com.android.designpattern.创建型.工厂方法.product.ProductC;
import com.android.designpattern.创建型.工厂方法.abstractfactory.AbstractFactory;

public class ProductCFactory extends AbstractFactory {

    @Override
    public AbstractProduct createProduct() {
        return new ProductC();
    }
}
