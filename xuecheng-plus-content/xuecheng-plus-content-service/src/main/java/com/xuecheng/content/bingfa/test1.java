package com.xuecheng.content.bingfa;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class test1 {
    public static void main(String[] args) {
        Data data = new Data();
        new Thread(()->{
            for (int i = 0; i < 30; i++){
                try {
                    data.jishu();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }, "A").start();
        new Thread(()->{
            for (int i = 0; i < 30; i++){
                try {
                    data.oushu();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }, "B").start();
        new Thread(()->{
            for (int i = 0; i < 30; i++){
                try {
                    data.jishu();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }, "C").start();
        new Thread(()->{
            for (int i = 0; i < 30; i++){
                try {
                    data.oushu();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }, "D").start();
    }
}
//class Data{
//    int num = 0;
//    //等待业务唤醒
//    public synchronized void jishu() throws InterruptedException {
//        while(num % 2 == 0){
//            this.wait(1);
//        }
//        System.out.println(Thread.currentThread().getName() + num++);
//        this.notify();
//    }
//    public synchronized void oushu() throws InterruptedException {
//        while(num % 2 != 0){
//            this.wait(1);
//        }
//        System.out.println(Thread.currentThread().getName() + num++);
//        this.notify();
//    }
//}
class Data{
    int num = 0;
    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();
    //等待业务唤醒
    public void jishu() throws InterruptedException {
        lock.lock();
        try {
            while(num % 2 == 0){
                condition.await();
            }
            System.out.println(Thread.currentThread().getName() + num++);
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
    public synchronized void oushu() throws InterruptedException {
        lock.lock();
        try {
            while(num % 2 != 0){
                condition.await();
            }
            System.out.println(Thread.currentThread().getName() + num++);
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
