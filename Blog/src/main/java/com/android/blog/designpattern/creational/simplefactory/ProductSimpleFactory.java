package com.android.blog.designpattern.creational.simplefactory;


import com.android.blog.designpattern.creational.simplefactory.abstractproduct.AbstractProduct;
import com.android.blog.designpattern.creational.simplefactory.product.ProductA;
import com.android.blog.designpattern.creational.simplefactory.product.ProductB;

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
