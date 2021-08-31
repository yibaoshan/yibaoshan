//
// Created by Bob on 2021/8/26.
//

struct handle_entry
{
    IBinder* binder;
    RefBase::weakref_type* refs;
};

class ProcessState : public virtual RefBase
{
public: static  sp<ProcessState>    self();

    /*启动线程池*/
    void startThreadPool();

    /*BpBinder的向量表*/
    Vector<handle_entry> mHandleToObject;

}
