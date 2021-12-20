package com.android.designpattern.creational.simplefactory;

import com.android.designpattern.creational.simplefactory.product.ProductA;
import com.android.designpattern.creational.simplefactory.product.ProductB;
import com.android.designpattern.creational.simplefactory.product.ProductC;

import org.junit.Test;

public class Main {

    /**
     * 结构：
     * 1. AbstractProduct为抽象工厂：定义公共行为，抽象类或接口
     * 2. ProduceA/B/C为具体实现：继承基类或者实现接口
     * 3. ProductSimpleFactory为创建工厂：负责创建产品对象
     *
     * 值工厂，对应锚点：context.getSystemService(String name)
     */

    @Test
    public void main() {
        System.out.println(ProductSimpleFactory.createProduct(ProductA.class).getName());
        System.out.println(ProductSimpleFactory.createProduct(ProductB.class).getName());
        System.out.println(ProductSimpleFactory.createProduct(ProductC.class).getName());
    }

}
