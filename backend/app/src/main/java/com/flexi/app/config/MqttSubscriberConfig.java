package com.flexi.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttSubscriberConfig {

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.subscribe-topic}")
    private String subscribeTopic;

    @Autowired
    private MqttPahoClientFactory mqttClientFactory;

    // 定义一个消息通道，用于接收来自 MQTT 的消息
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    // 配置入站适配器，订阅指定主题
    @Bean
    public MessageProducer mqttInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                clientId + "-inbound", mqttClientFactory, subscribeTopic);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1); // 设置服务质量等级
        adapter.setOutputChannel(mqttInputChannel()); // 将消息发送到输入通道
        return adapter;
    }

    // 监听消息通道，处理收到的消息
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttMessageHandler() {
        return message -> {
            String payload = (String) message.getPayload();
            //System.out.println(message.getHeaders().keySet());
            System.out.println("收到 MQTT 消息: " + payload);
            // 在这里添加你的业务逻辑
        };
    }
}
