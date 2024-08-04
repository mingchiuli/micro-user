package org.chiu.micro.user.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mingchiuli
 * @create 2022-12-25 4:13 pm
 */
@Configuration
public class UserAuthMenuChangeRabbitConfig {

    public static final String QUEUE = "user.auth.menu.change.queue.auth";

    public static final String TOPIC_EXCHANGE = "user.auth.menu.change.topic.exchange";

    public static final String BINDING_KEY = "user.auth.menu.change.binding.auth";

    @Bean("authQueue")
    Queue authQueue() {
        return new Queue(QUEUE, true, false, false);
    }

    //ES交换机
    @Bean("topicExchange")
    TopicExchange exchange() {
        return new TopicExchange(TOPIC_EXCHANGE, true, false);
    }

    //绑定ES队列和ES交换机
    @Bean("authBinding")
    Binding esBinding(@Qualifier("authQueue") Queue authQueue,
                      @Qualifier("topicExchange") TopicExchange authExchange) {
        return BindingBuilder
                .bind(authQueue)
                .to(authExchange)
                .with(BINDING_KEY);
    }
}
