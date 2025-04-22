package org.khr;

import lombok.Data;
import org.junit.Test;
import org.khr.proxy.ServiceProxyFactory;

/**
 * @author KK
 * @create 2025-04-22-14:42
 */
public class ProxyTest {
    @Test
    public void test1() {
        // 获取代理
        UserService userService = ServiceProxyFactory.getMockProxy(UserService.class);
        User user = new User();
        user.setName("yupi");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
        long number = userService.getNumber();
        System.out.println(number);
    }

    @Test
    public void test2() {
        RpcApplication.init();
        UserService proxy = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("yupi");
        // 调用
        User newUser = proxy.getUser(user);
        System.out.println(newUser.getName());
        long number = proxy.getNumber();
        System.out.println(number);
    }
}

interface UserService {

    /**
     * 获取用户
     *
     * @param user
     * @return
     */
    User getUser(User user);

    /**
     * 新方法 - 获取数字
     */
    default short getNumber() {
        return 110;
    }
}

@Data
class User {
    private String name;
}