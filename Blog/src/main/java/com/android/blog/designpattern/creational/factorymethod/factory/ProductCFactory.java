package com.android.blog.designpattern.creational.factorymethod.factory;

import com.android.designpattern.creational.factorymethod.abstractproduct.AbstractProduct;
import com.android.designpattern.creational.factorymethod.product.ProductC;
import com.android.designpattern.creational.factorymethod.abstractfactory.AbstractFactory;

public class ProductCFactory extends AbstractFactory {

    @Override
    public AbstractProduct createProduct() {
        return new ProductC();
    }
}
