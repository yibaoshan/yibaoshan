package com.android.designpattern.creational.builder;

public class Main {

    /**
     * 一句话概括：将复杂对象的构造与它的表示分开，这样可以在相同的构造过程中创建不同的表示形式。
     * //限制公开属性
     * 1. 将自己不愿意暴露的属性（比如配置参数）放在builder中，一旦对象创建便不可更改，比如线程池对象中最大线程数量
     * 2. 批量设置参数（类似工厂模式），比如create中创建只包含确认按钮的对话框
     * 为了更好的进行业务分层，我们甚至可以设计多个builder类，从而保证product类的纯粹
     * 源码举例：AlertDialog、Notification、Glide、Picasso
     */

    public void main() {
        CommonDialog.Builder builder = new CommonDialog.Builder();
        builder.setConfirmText("yes");
        builder.setCancelText("no");
        builder.setMessageText("will you marry me");
        CommonDialog normalDialog = builder.create();//创建普通对话框
        normalDialog.show();

        builder.setMessageText("Am I handsome?");
        builder.setConfirmText("yes");
        CommonDialog onlyConfirmDialog = builder.createOnlyConfirm();//创建只有确认按钮的对话框
        onlyConfirmDialog.show();
        onlyConfirmDialog.setMessageText("so~ Let's go to the movies?");
        onlyConfirmDialog.show();
    }

}
