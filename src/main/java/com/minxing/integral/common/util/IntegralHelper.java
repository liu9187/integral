package com.minxing.integral.common.util;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.minxing.integral.common.bean.Person;
import com.minxing.integral.handler.IntegralHandler;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class IntegralHelper {
    private static Logger logger = LoggerFactory.getLogger( IntegralHelper.class );
    private static final int BUFFER_SIZE = 1024;
    private static final int THREAD_NUMBERS = 4;

    public static void add(Person person) {
        RingBuffer<Person> ringBuffer = RingBuffer.createSingleProducer( () -> new Person(), BUFFER_SIZE, new YieldingWaitStrategy() );
        //创建线程池
        ExecutorService executors = Executors.newFixedThreadPool( THREAD_NUMBERS );
        //创建SequenceBarrier
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
        //创建消息处理器
        BatchEventProcessor<Person> processor = new BatchEventProcessor<>( ringBuffer, sequenceBarrier, new IntegralHandler() );
        ringBuffer.addGatingSequences( processor.getSequence() );
        //把消息处理器提交到线程池
        executors.submit( processor );
        //如果存大多个消费者 那重复执行上面3行代码 把IntegralHandler换成其它消费者类
        Future<?> future = executors.submit( (Callable<Void>) () -> {
            long seq;
            // for (int i=0;i<10;i++){
            //占个坑 --ringBuffer一个可用区块
            seq = ringBuffer.next();
            ringBuffer.get( seq ).setUrlParameters( person.getUrlParameters() );
            ringBuffer.get( seq ).setDomain( person.getDomain() );
            ringBuffer.get( seq ).setAuth( person.getAuth() );
            ringBuffer.publish( seq );
            // }
            return null;
        } );
        try {
            //等待生产者结束
            future.get();
            //等待消费者结束
            Thread.sleep( 1000 );
            //通知时间管理器
            processor.halt();
            //结束线程
            executors.shutdown();
        } catch (InterruptedException e) {
            logger.error( "<<<<<<增加积分操作错误：", e );
        } catch (ExecutionException e) {
            logger.error( "<<<<<积分增加未结束", e );
        }


    }

    public static void main(String[] args) {
        String user_id = "1";
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add( new BasicNameValuePair( "data_type", "integral" ) );
        urlParameters.add( new BasicNameValuePair( "value", "50" ) );
        urlParameters.add( new BasicNameValuePair( "user_id", user_id ) );
        //调用接口需要的参数
        //  logger.info( "Ruby interface call parameters: data_type=" + data_type + "-----value=" + integer+ "-----user_id=" + user_id );
        //事件加入环形缓冲区
        Person person = new Person();
        person.setAuth( "ji9wKWhGzK5q5xTZ6Ql7ARDvNT7gDcX7LnCnMBpisshbo9dH" );
        person.setDomain( "http://dev8.dehuinet.com:8018" );
        person.setUrlParameters( urlParameters );
        new IntegralHelper().add( person );
    }

}
