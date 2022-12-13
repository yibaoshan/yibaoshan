
## Object#wait()

一个线程调用一个共享变量的 wait() 方法后，调用线程会被阻塞挂起，直到发生下面几件事情之一才返回：

（1）其他线程调用了该共享对象的notify（）或者notifyAll（）方法；（2）其他线程调用了该线程的interrupt（）方法，该线程抛出InterruptedException异常返回。