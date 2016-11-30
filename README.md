# struts-ready-plugin
基于Struts2 + Struts Convention的Struts插件，无需配置任何action即可享用类似"namespace/action/method.suffix"(后缀可省略)格式的URL访问方式。并集成文件下载、JSON输出等多种常用的Web功能，大幅减少冗余的配置和编码工作。

Struts 2是非常流行的开源 MVC 框架，熟练使用 Struts 也是一个Java EE开发人员的必备技能，因此此处不再赘述 Struts 2 的用法细则。你可以直接参考 Struts 的官方文档。

众所周知，Struts 的请求映射一般都是在 struts.xml 文件中配置的。我们每新增一个业务请求方法，都需要在 struts.xml 中进行相应的配置（例如：package、action、result的配置）。不过，这样的配置方式将会带来以下问题：

* 每次新增业务请求方法，都需要进行手动配置，略显繁琐；无意义的重复劳动，浪费时间（尽管 Struts 也支持通配符来简化配置）。
* 如果URI的命名没有严格的约定，那么用户访问的URL将没有统一的风格。此外，在排查问题时，都需要根据URL在配置文件中定位对应的类和方法。
* 团队合作时，多个开发人员对同一配置文件进行修改，极可能引起文件冲突（尽管Struts配置文件也支持使用include指令将其拆分为多个配置文件，但这会增加上一问题的定位查找负担）。

为了解决上述问题，提高开发效率，我们对 Struts 底层代码进行了深入研究，并自行研发了Struts Ready插件（Struts Ready 插件自身又依赖于Struts 官方的另一个插件：Struts Convention）。使用该插件，我们在每次新增业务请求方法时，将无需在 struts.xml 配置文件中添加任何配置，真正的实现了“零配置”。

以我们项目中的 `me.codeplayer.action.UserAction` 类的`info()`方法为例，我们只需要为该方法添加一个 `@Ready` 注解，即可通过URL "/user/info" 来访问该请求方法：
```java
package me.codeplayer.action;

import me.codeplayer.annotation.Ready;

public class UserAction {

	@Ready
	public String info() {
		// 这里是控制器层的处理代码
		return "user_info";
	}

}
```
>问：为什么一定要给方法加上 `@Ready` 注解？
>答：因为在Action类中一般还有供Struts进行属性注入的 getter/setter 方法。例如，UserAction类具有 user 属性，以及对应的 getUser() 和 setUser() 方法。如果没有@Ready加以区别的话，用户甚至可以通过URL "/user/getUser" 来访问getUser()方法。这将产生一些不必要的问题，并有一定的安全隐患。
>因此，我们定义了`@Ready`注解，只有标注了该注解的方法才允许外部访问，否则显示HTTP 404。

## Struts Ready 插件的Action匹配机制

###一般匹配机制

基于“约定大于配置”的思想，Struts Ready插件重写了 Struts 底层的 Action 映射机制，并能够自动建立请求URL到Action类以及方法之间的映射。
Struts Ready插件认为，一个Action方法的完全限定名称（例如`me.codeplayer.action.admin.UserAction.hello()`）主要由以下几个部分组成：

*action根包名（`me.codeplayer.action`）：用于存放所有Action类的根包
*action子包名（`admin`）：用于按类别存放Action类的包，也可以没有
*类名（`UserAction`）：类名必须以Action作为后缀。
*方法名（`hello`）：处理当前请求的具体方法

Struts Ready将基于以下规则为该方法建立与之对应的URL之间的映射：

| 方法组成 | 映射规则 |	对应的URL部分示例 |
| -------- | -------- | ------------------ |
| action根包名 |	对应项目的根URL	| `me.codeplayer.action` -> localhost:8080/p2p/（视实际情况而定）|
| action子包名	| 对应名称的URL父目录（也可以没有）| `admin` -> admin/ 或 `mobile.admin` -> mobile/admin/ |
| 类名	| 去掉Action后缀，然后全部转小写。如果Action前面由多个单词组成，则多个单词之间以"-"连接 |	`UserAction` -> user/ 或 `HelloWorldAction` -> hello-world/ |
| 方法名	| 保持不变（大小写也保持不变） | `hello()` -> hello 或 `sayHi()` -> sayHi |

Struts Ready是如何知道哪些包下面存放的是Action类呢？此外，由于包名需要区分为根包和子包两部分，Struts Ready是如何知道哪一部分是根包的呢？这是因为我们在 struts.xml 中进行了如下配置：

```xml
<!-- 在哪些包中扫描action类(无需匹配locators) -->
<constant name="struts.convention.action.packages" value="me.codeplayer.action" />
```

Struts Ready将会在该配置所指定的包下面扫描所有的 Action 类和方法，并将该包视作action的根包。

###	特殊匹配机制

在正式服务器运行环境中，如果用户访问首页时，实际上调用的是 `HomeAction.index()` 这个方法。那么，用户是不是必须输入类似如下URL "http://example.com/home/index" 才能访问首页呢？
很明显，这并不是一个好的主意。一般而言，用户更希望直接输入 http://example.com 就能够访问首页。因此，Struts Ready插件也支持缺省的URL路径。

默认情况下：

*如果方法名的映射名称为缺省方法名（默认为index），则URL的方法名部分可以省略。
*如果action类的映射名称为缺省action名（默认为default），并且方法名的映射名称为缺省方法名，则URL的action类名和方法名部分都可以省略。

例如：

`me.codeplayer.action.DefaultAction.index()` 方法，我们可以直接通过 http://example.com 进行访问。
`me.codeplayer.action.admin.DefaultAction.index()` 方法，我们可以直接通过 http://example.com/admin/ 进行访问。
`me.codeplayer.action.HelloAction.index()` 方法，我们可以直接通过 http://example.com/hello/ 进行访问。

>注意：出于性能和区分考虑，除了根URL外，缺省形式的URL必须以"/"号结尾。
