package org.geekbang.thinking.in.spring.ioc.bean.scope;


import org.geekbang.thinking.in.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 自定义Bean scope  实现本线程可见的scope的范围
 * ThreadLocal 级别的 Scope
 *
 * @see Scope
 */
public class ThreadLocalScope2 implements Scope {

    public static void main(String[] args) {
        User user1 = new User();
        user1.setBeanName("user1");
        user1.setName("user1");
        user1.setId(1L);
        User user2 = new User();
        user2.setBeanName("user1");
        user2.setName("user1");
        user2.setId(2L);
        User user3 = new User();
        user3.setBeanName("user3");
        user3.setName("user3");
        user3.setId(3L);

        List<User> users = Arrays.asList(user1, user2, user3);
        //  将一个集合转换成MAP时， 如果作来key的值有重复， 需要指定采用哪一个；
        Map<String, User> collect = users.parallelStream().collect(Collectors.toMap(User::getName, i -> i, (v1, v2) -> v1));
        System.out.println(collect.size());
        System.out.println(collect);

    }

    // 定义一个自定的Scope的名称常量；
    public static final String SCOPE_NAME = "thread-local";

    private final NamedThreadLocal<Map<String, Object>> threadLocal = new NamedThreadLocal<Map<String, Object>>("thread-local-scope") {
        public Map<String, Object> initialValue() {
            return new HashMap<>();
        }
    };

//    private ThreadLocal<T> threadlocal-name=new ThreadLocal<T>


    @Override
    public Object get(String s, ObjectFactory<?> objectFactory) {
        // 获取到当前线程的内存对象
        Map<String, Object> context = getContext();
        Object o = context.get(s);
        if (o == null) {
            o = objectFactory.getObject();
            context.put(s, o);
        }
        return o;
    }

    @NonNull
    private Map<String, Object> getContext() {
        return threadLocal.get();
    }

    @Override
    public Object remove(String s) {
        Map<String, Object> context = getContext();
        return context.remove(s);
    }

    @Override
    public void registerDestructionCallback(String s, Runnable runnable) {
        // TODO
    }

    @Override
    public Object resolveContextualObject(String s) {
        Map<String, Object> context = getContext();
        return context.get(s);
    }

    @Override
    public String getConversationId() {
        Thread thread = Thread.currentThread();
        return String.valueOf(thread.getId());
    }
}
