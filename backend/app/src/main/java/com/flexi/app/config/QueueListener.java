package com.flexi.app.config;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class QueueListener {

    @RabbitListener(queues = "kcloud_iot_asset_queue")
    public void handleMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String msgBody = new String(message.getBody());
            System.out.println(msgBody);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
