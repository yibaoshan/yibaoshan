package com.android.designpattern.creational.factorymethod.factory;

import com.android.designpattern.creational.factorymethod.abstractproduct.AbstractProduct;
import com.android.designpattern.creational.factorymethod.product.ProductB;
import com.android.designpattern.creational.factorymethod.abstractfactory.AbstractFactory;

public class ProductBFactory extends AbstractFactory {

    @Override
    public AbstractProduct createProduct() {
        return new ProductB();
    }
}
