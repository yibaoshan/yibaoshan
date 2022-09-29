package com.android.blog.designpattern.creational.simplefactory.product;

import com.android.designpattern.creational.simplefactory.abstractproduct.AbstractProduct;

public class ProductA extends AbstractProduct {

    @Override
    public String getName() {
        return "A";
    }

}
