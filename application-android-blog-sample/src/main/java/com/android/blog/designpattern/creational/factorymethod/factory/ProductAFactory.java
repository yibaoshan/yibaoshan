package com.android.blog.designpattern.creational.factorymethod.factory;


import com.android.blog.designpattern.creational.factorymethod.abstractfactory.AbstractFactory;
import com.android.blog.designpattern.creational.factorymethod.abstractproduct.AbstractProduct;
import com.android.blog.designpattern.creational.factorymethod.product.ProductA;

public class ProductAFactory extends AbstractFactory {

    @Override
    public AbstractProduct createProduct() {
        return new ProductA();
    }
}
