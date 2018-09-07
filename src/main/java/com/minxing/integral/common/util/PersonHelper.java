package com.minxing.integral.common.util;

import com.lmax.disruptor.*;
import com.minxing.integral.common.bean.Person;
import com.minxing.integral.common.event.PersonEvent;
import com.minxing.integral.handler.PersonEventHandler;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * ringbuffer的初始化
 */
public class PersonHelper {
    private static PersonHelper instance;
    private static boolean inited = false;
    /**
     * ringBuffer的容量，必须是2的N次方
     */
    private static final int BUFFER_SIZE = 256;
    private RingBuffer<PersonEvent> ringBuffer;
    private SequenceBarrier sequenceBarrier;
    private PersonEventHandler handler;
    private BatchEventProcessor<PersonEvent> batchEventProcessor;

    public PersonHelper() {
        //参数1 事件
        //参数2 单线程使用
        //参数3 等待策略
       // ringBuffer = new RingBuffer<PersonEvent>( PersonEvent.EVENT_FACTORY, new SingleThreadedClaimStrategy( BUFFER_SIZE ), new YieldingWaitStrategy() );
        //获取生产者的位置
        ringBuffer.newBarrier();
        //消费者
        handler = new PersonEventHandler();
        //事件处理器，监控指定ringBuffer,有数据时通知指定handler进行处理
        //1.对应的RingBuffer
        //2,依赖关系，通过构造不同的sequenceBarrier用互相的dependentsequence，我们可以构造出先后消费关系
        //3.用户实现的处理消费的event的业务消费者
        batchEventProcessor = new BatchEventProcessor<PersonEvent>( ringBuffer, sequenceBarrier, handler );
        //传入所有消费者的线程的序号
     //   ringBuffer.setGatingSequences( batchEventProcessor.getSequence() );
    }

    /**
     * 启动消费者线程，实际上调用了AudioDataEventHandler中的onEvent方法进行处理
     */
    public static void start() {
        instance = new PersonHelper();
        Thread thread = new Thread( instance.batchEventProcessor );
        thread.start();
        inited = true;
    }

    /**
     * 停止
     */
    public static void shutdown() {
        if (!inited) {
            throw new RuntimeException( "EncodeHelper还没有初始化！" );
        } else {
            instance.doHalt();
        }
    }

    private void doHalt() {
        batchEventProcessor.halt();
    }

    private void doProduce(Person person) {
        //获取下一个序号
        long sequence = ringBuffer.next();
        //写入数据
        ringBuffer.get( sequence ).setPerson( person );
        //通知消费者该资源可以消费
        ringBuffer.publish( sequence );

    }

    /**
     * 生产者压入生产数据
     *
     * @param person
     */
    public static void produce(Person person) {
        if (!inited) {
            throw new RuntimeException( "EncodeHelper还没有初始化！" );
        } else {
            instance.doProduce( person );
        }

    }

    public static void main(String[] args) {
        String user_id = "1";
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add( new BasicNameValuePair( "data_type", "integral" ) );
        urlParameters.add( new BasicNameValuePair( "value", "1" ) );
        urlParameters.add( new BasicNameValuePair( "user_id", user_id ) );
        //调用接口需要的参数
        //  logger.info( "Ruby interface call parameters: data_type=" + data_type + "-----value=" + integer+ "-----user_id=" + user_id );
        //事件加入环形缓冲区
        Person person = new Person();
        person.setAuth( "ji9wKWhGzK5q5xTZ6Ql7ARDvNT7gDcX7LnCnMBpisshbo9dH" );
        person.setDomain( "http://dev8.dehuinet.com:8018" );
        person.setUrlParameters( urlParameters );
        PersonHelper.start();
        PersonHelper.produce( person );

    }

}
