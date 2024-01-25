/*
BSD 2-Clause License

Copyright (c) 2021, jadedrip
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.caffy.utility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 获取一个对象的代理接口，即使对象没有继承这个接口，只要实现了接口里的方法，就可以创建接口
 * Get the proxy interface of an object, even if the object does not inherit the interface, 
 * as long as the interface implements the methods in the interface, you can create the interface
 * 
 * 就算对象实现了接口，也通过代理进行了隔离
 * Even if an object implements an interface, it is isolated by proxy
 * 
 * @author jadedrip@outlook.com
 * 使用方法：
 * Use:
 * 
 *      Interf inf = Dock.it(Interf.class, obj)
 *
 */
public class Dock {
    private static final Map<String, Dock> classMap = new ConcurrentHashMap<>();

    private final Map<String, Field> fields;
    private final Map<String, Method> methodMap;

    private Dock(Class<?> aClass){
        this.fields = Arrays.stream(aClass.getDeclaredFields()).peek(i -> i.setAccessible(true))
                .collect(Collectors.toMap(Field::getName, Function.identity()));
        this.methodMap = Arrays.stream(aClass.getDeclaredMethods()).peek(i -> i.setAccessible(true)).collect(Collectors.toMap(Method::getName, Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public static <T> T it(Class<T> interf, Object object){
        if(interf.isInstance(object)){
            return (T) Proxy.newProxyInstance(interf.getClassLoader(), new Class[]{interf}, (proxy, method, args) -> method.invoke(object));
        }
        Class<?> aClass = object.getClass();
        Dock v = classMap.computeIfAbsent(aClass.getName(), (val) -> new Dock(aClass));
        return (T)v.getProxy(interf, object);
    }

    private Object getProxy(Class<?> interf, Object object) {
        return Proxy.newProxyInstance(interf.getClassLoader(), new Class[]{interf}, (proxy, method, args) -> {
            String name = method.getName();
            Method m = methodMap.get(name);
            if(m!=null) return m.invoke(object, args);
            if(name.length()>3 && name.startsWith("get") ){
                name = (name.substring(3,4).toLowerCase()) + name.substring(4);
                Field field = fields.get(name);
                if(field!=null) {
                    return field.get(object);
                }
            }
            throw new NoSuchMethodException();
        });
    }
}