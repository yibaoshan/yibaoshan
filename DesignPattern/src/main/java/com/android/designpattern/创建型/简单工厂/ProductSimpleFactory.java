package com.android.designpattern.创建型.简单工厂;

import com.android.designpattern.创建型.简单工厂.abstractproduct.AbstractProduct;
import com.android.designpattern.创建型.简单工厂.product.ProductA;
import com.android.designpattern.创建型.简单工厂.product.ProductB;

public class ProductSimpleFactory {

    public static AbstractProduct createProduct(Class<? extends AbstractProduct> product) {
        if (product.equals(ProductA.class)) {
            return new ProductA();
        } else if (product.equals(ProductB.class)) {
            return new ProductB();
        } else {
            throw new ClassCastException("class " + product.getSimpleName() + " is not extends " + AbstractProduct.class.getSimpleName());
        }
    }

}
