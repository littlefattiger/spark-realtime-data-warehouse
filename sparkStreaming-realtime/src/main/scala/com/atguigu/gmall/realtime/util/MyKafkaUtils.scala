package com.atguigu.gmall.realtime.util
import java.util
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.internals.Topic
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

import scala.collection.mutable

object MyKafkaUtils {
  private val consumerConfigs: mutable.Map[String, Object] = mutable.Map[String, Object](
//    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "hadoop102:9092,hadoop103:9092,hadoop104:9092",
//    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> MyPropsUtils("kafka.bootstrap-servers"),
    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> MyPropsUtils(MyConfig.KAFKA_BOOTSTRAP_SERVERS),
    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
    ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> "true",
    //    ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG -> "5s"
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "latest"
  )

  def getKafkaDStream(ssc: StreamingContext, topic: String, groupId: String) = {
    consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    val kafkaDStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream(ssc, LocationStrategies.PreferConsistent, ConsumerStrategies.Subscribe[String, String](Array(topic), consumerConfigs))
    kafkaDStream
  }
val producer : KafkaProducer[String, String] = createProducer()
  def createProducer():KafkaProducer[String,String] = {
    val producerConfigs: util.HashMap[String, AnyRef] = new util.HashMap[String,AnyRef]
    //生产者配置类 ProducerConfig
    //kafka集群位置
    //producerConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"hadoop102:9092,hadoop103:9092,hadoop104:9092")
    //producerConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,MyPropsUtils("kafka.bootstrap-servers"))
    producerConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,MyPropsUtils(MyConfig.KAFKA_BOOTSTRAP_SERVERS))
    //kv序列化器
    producerConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG , "org.apache.kafka.common.serialization.StringSerializer")
    producerConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG , "org.apache.kafka.common.serialization.StringSerializer")
    //acks
    producerConfigs.put(ProducerConfig.ACKS_CONFIG , "all")
    //batch.size  16kb
    //linger.ms   0
    //retries
    //幂等配置
    producerConfigs.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG , "true")

    val producer: KafkaProducer[String, String] = new KafkaProducer[String,String](producerConfigs)
    producer
  }

  /**
   * 生产（按照默认的黏性分区策略）
   */
  def send(topic : String  , msg : String ):Unit = {
    producer.send(new ProducerRecord[String,String](topic , msg ))
  }

  /**
   * 生产（按照key进行分区）
   */
  def send(topic : String  , key : String ,  msg : String ):Unit = {
    producer.send(new ProducerRecord[String,String](topic , key ,  msg ))
  }

  /**
   * 关闭生产者对象
   */
  def close():Unit = {
    if(producer != null ) producer.close()
  }

  /**
   * 刷写 ，将缓冲区的数据刷写到磁盘
   *
   */
  def flush(): Unit ={
    producer.flush()
  }
}
