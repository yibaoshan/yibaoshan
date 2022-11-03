package com.android.blog.designpattern.creational.factorymethod;


import com.android.blog.designpattern.creational.factorymethod.abstractfactory.AbstractFactory;
import com.android.blog.designpattern.creational.factorymethod.abstractproduct.AbstractProduct;
import com.android.blog.designpattern.creational.factorymethod.factory.ProductAFactory;
import com.android.blog.designpattern.creational.factorymethod.factory.ProductBFactory;
import com.android.blog.designpattern.creational.factorymethod.factory.ProductCFactory;

import org.junit.Test;

public class Main {

    @Test
    public void main() {
        AbstractFactory factoryA = new ProductAFactory();
        AbstractFactory factoryB = new ProductBFactory();
        AbstractFactory factoryC = new ProductCFactory();

        AbstractProduct productA = factoryA.createProduct();
        AbstractProduct productB = factoryB.createProduct();
        AbstractProduct productC = factoryC.createProduct();

        System.out.println("工厂A生产的产品名称：" + productA.getName());
        System.out.println("工厂B生产的产品名称：" + productB.getName());
        System.out.println("工厂C生产的产品名称：" + productC.getName());

    }

}
