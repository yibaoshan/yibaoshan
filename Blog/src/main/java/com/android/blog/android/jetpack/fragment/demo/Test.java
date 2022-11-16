package com.android.blog.android.jetpack.fragment.demo;


/**
 * Created on 2022/11/3
 *
 * @Description
 */
class Test {

    void startActivity(){

    }

    void main(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                startActivity();
            }
        }).start();
    }

    class Run implements Runnable {

        @Override
        public void run() {
            startActivity();
        }
    }

}
