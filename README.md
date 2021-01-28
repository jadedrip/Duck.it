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
