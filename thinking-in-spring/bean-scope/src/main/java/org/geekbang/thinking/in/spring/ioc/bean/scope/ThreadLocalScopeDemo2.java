package org.geekbang.thinking.in.spring.ioc.bean.scope;

import org.geekbang.thinking.in.spring.ioc.overview.domain.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

public class ThreadLocalScopeDemo2 {

    @Bean(name = "bUser")
    @Scope(value = ThreadLocalScope2.SCOPE_NAME)
    public User create() {
        return createUser();
    }

    private static User createUser() {
        User user = new User();
        user.setId(System.nanoTime());
        return user;
    }

    public static void main(String[] args) {

        // 创建BeanFactory 容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 注册Configuration Class 配置类 -> Spring Bean
        applicationContext.register(ThreadLocalScopeDemo2.class);
        //
        applicationContext.addBeanFactoryPostProcessor(beanFactory -> {
            // 将自定义的ThreadlocalScope注册到spring应用上下文中 , 将创建的对应注册进spring容器中
            beanFactory.registerScope(ThreadLocalScope2.SCOPE_NAME, new ThreadLocalScope2());
            beanFactory.registerSingleton(ThreadLocalScope2.SCOPE_NAME, new ThreadLocalScope2());
        });
        // 启动spring 应用上下文
        applicationContext.refresh();
        Object bean = applicationContext.getBean(ThreadLocalScope2.SCOPE_NAME);
        System.out.println(bean);
        if (bean instanceof ThreadLocalScope2) {
            System.out.println("OK");
            String conversationId = ((ThreadLocalScope2) bean).getConversationId();
            System.out.println(conversationId);
        } else {
            System.out.println("NO");
        }
//        Object user = applicationContext.getBean("user");
        Object bUser = applicationContext.getBean("bUser");
//        System.out.println("user is " + user);
        System.out.println("bUser is " + bUser);
        mutilScopedBeanByLoopup(applicationContext);
        line();
        scopedBeanByLoopup(applicationContext);
        // 关闭spring 用户上下文
        applicationContext.close();
    }

    private static void line() {
        System.out.println();
    }

    private static void mutilScopedBeanByLoopup(AnnotationConfigApplicationContext applicationContext) {
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(() -> {
                Object bUser = applicationContext.getBean("bUser");
                System.out.printf("[Thread is : %d] mutilScopedBeanByLoopup user = %s %n ", Thread.currentThread().getId(), bUser);
                line();
            });
            thread.start();
            // 强制执行完当前线程
            try {
                thread.join(2L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void scopedBeanByLoopup(AnnotationConfigApplicationContext applicationContext) {
        for (int i = 0; i < 3; i++) {
//            User user = applicationContext.getBean("user", User.class);
            Object bUser = applicationContext.getBean("bUser");
            System.out.printf("[Thread is : %d] scopedBeanByLoopup user = %s %n ", Thread.currentThread().getId(), bUser);
            line();
        }
    }

//    private static void scopedBeansByLookup(AnnotationConfigApplicationContext applicationContext) {
//
//        for (int i = 0; i < 3; i++) {
//            Thread thread = new Thread(() -> {
//                // user 是共享 Bean 对象
//                User user = applicationContext.getBean("user", User.class);
//                System.out.printf("[Thread id :%d] user = %s%n", Thread.currentThread().getId(), user);
//            });
//
//            // 启动线程
//            thread.start();
//            // 强制线程执行完成
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
