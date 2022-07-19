package com.android.blog.android_view;


class View2 {

    /**
     * 那么，多个应用应该如何显示呢？
     *
     * Android版本迭代可能更改了图形架构的设计，除了在图形领域深耕的开发者，其他人想要了解图形系统的设计的确困难重重
     * 原本想做历年版本更新，这个工程量对比考证
     * */


    class Process {

        class APP {

            class Activity {

                class PhoneWindow {

                    class DecorView {


                    }

                }

                class SurfaceView {

                }

            }

        }

        class SystemServer {

            class WindowManageService {

                /**
                 * java:
                 * Window
                 * */

                /**
                 * native:
                 * SurfaceView
                 * */

            }

        }

        class SurfaceFlinger {

            class Layer{

                /**
                 * BufferQueue
                 * */

            }

        }

    }

    /**
     * 每一块显示的窗口，都有对应的bufferqueue
     */


    class Overview {

        /**
         * 从操作系统的角度来说
         * 1. DRM/KMS或者FB框架给用户空间提供了连接显示器的能力
         * 2.
         * */

    }


    class SurfaceFlinger {

        /**
         * 1. 接收来自hwc的vsync信号
         * 2. 处理vsync信号，DispSync，在Android 12时已经被删除，可能Google觉得高刷是未来的趋势
         * */

    }

}
