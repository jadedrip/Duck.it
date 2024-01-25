# Duck.It
A simple tools for java to create a interface from object which isn't inherit

Example:

```
class MyClass {
    public String value = "World";
    public String hello(){
        return "Hello";
    }
}

interface IHello{
    String hello();
    String getValue();
}

MyClass myClass = new MyClass();
IHello it = Duck.it(IHello.class, myClass);
String x = it.hello();
assertEquals(x, "Hello");
String v = it.getValue();
assertEquals(v, "World");
```

有句话说得好，当一个东西看起来像鸭子，吃起来像鸭子，那么它就是鸭子。所以我把这个小工具命名为 Duck.it

Java 里，正常对象类必须从某个接口继承，对象才能转换为这个接口，并通过接口操作这个对象。

这个工具类借用接口代理，实现了一个很小的但很有趣的功能，当对象实现了某个接口，可以直接创建这个接口，而如果对象没有继承这个接口，
但实现了那个接口所有的方法，那么我们就可以通过 Duck.it 来创建这个接口。

特别的，如果接口里有 get/set 方法，对象类没有实现它，但有对应的字段（Field)，也可以。

当然，如果接口里有方法在对象里找不到，Duck.it 会抛出 NoSuchMethodException 异常。 

这个小工具有个特别有用的用法，就是限制对外接口的返回字段。比如数据库对象是有密码、手机号等敏感字段的，直接返回给前端显然不合理，常规的做法
是通过 BeanCopy 之类的工具，把对象复制到一个新对象里，再返回给前端。

但用这个工具，就可以通过定义一个接口，通过写 get 方法限制返回字段，然后通过这个接口代理对象创建接口，达到限制返回字段的目的。
特别的，如果你通过 gson 序列化对象，不能使用这个工具，因为 gson 不是通过 get 方法来序列化对象的，而 Spring boot 默认的 Jackson 或者很多人
使用的 Fastjson 是可以正确序列化带 get 方法的接口的。