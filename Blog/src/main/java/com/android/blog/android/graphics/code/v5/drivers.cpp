//
// Created by Bob on 2022/9/13.
//


//drivers/input/input.c
//核心层，为驱动层提供设备注册，和操作接口，是 input 子系统的核心类
class input {

    //注册一个 input 设备到内核
    int input_register_device(input_dev *dev){
    }

    //从内核注销掉一个 input 设备
    void input_unregister_device(input_dev *dev){
    }

    int input_attach_handler(input_dev *dev, input_handler *handler){
        handler->evdev_connect();
    }

    //设备上报一个 input 事件，input 子系统对其处理
    void input_event(input_dev *dev, type, code, value){
        input_handle_event(dev, type, code, value);
    }

    void input_handle_event(input_dev *dev, type, code, value){
    }
}

//drivers/input/evdev.c
//事件层，和用户空间进行交互，提供事件监听功能。类似的类还有 mousedev.c 、keyboard.c
class evdev {

    //选择一个事件设备进行连接
    int evdev_connect(input_handler *handler, input_dev *dev, input_device_id *id){
    }

    //取消监听
    void evdev_disconnect(input_handle *handle){
    }
}