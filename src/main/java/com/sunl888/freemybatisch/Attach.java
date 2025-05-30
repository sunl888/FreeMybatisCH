package com.sunl888.freemybatisch;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * @author sunlong
 * @since 2025/5/30
 */
public class Attach {
    /**
     * 使用 Javassist 库修改 MyBatisCodeHelper 类的字节码，用来绕过注册机制
     * <p>
     * 1. windows系统进入 %AppData%\JetBrains\plugins\MyBatisCodeHelper-Pro 目录
     * 2. 将 instrumented-MyBatisCodeHelper-Pro241-3.4.2+2321.jar 文件复制到 D:\MyBatisCodeHelper-Pro\
     * 3. 用360压缩软件解压 instrumented-MyBatisCodeHelper-Pro241-3.4.2+2321.jar 文件
     * 4. 用Idea打开解压后的 instrumented-MyBatisCodeHelper-Pro 目录
     * 5. 打开Idea的MyBatisCodeHelper离线激活页面，随便输入后点击离线激活，会触发报错，从报错堆栈中看到最后执行的方法是 com.ccnode.codegenerator.ag.f.e.a()
     * 6. 在Idea中看看这个方法的内容是做了哪些操作，可以看到里面是对加密的数据进行解析后通过gson反序列化到com.ccnode.codegenerator.ag.d.f对象
     * 6.1 第6步也可以通过cfr工具来反编译到一个文件中，命令如下：java -jar cfr-0.152.jar "instrumented-MyBatisCodeHelper-Pro241-3.4.2+2321.jar" --renamedupmenbers true --hideutf false >> decode.txt
     * 7. 那么我们可以通过写一段代码来修改该方法，直接对输入的数据进行反序列化，绕过解析流程，这样就可以实现随意修改有效期了。
     * 8. 以下代码执行后会在 D:\MyBatisCodeHelper-Pro 子目录下生成一个class文件。
     * 9. 通过360压缩文件打开 instrumented-MyBatisCodeHelper-Pro241-3.4.2+2321.jar，找到com.ccnode.codegenerator.ag.f 目录，将e.class覆盖原始文件
     * 10. 将修改后的 instrumented-MyBatisCodeHelper-Pro241-3.4.2+2321.jar 放回 %AppData%\JetBrains\plugins\MyBatisCodeHelper-Pro 目录。
     * 11. 打开Idea的离线激活页面，输入 {"paidKey":"你的名字","valid":true,"userMac":"你的唯一码","validTo":32503651199000} 点击激活即可。
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // 获取 Javassist 默认的类池，类池用于管理和查找类文件
        ClassPool classPool = ClassPool.getDefault();

        // 向类池中添加指定 JAR 文件的类路径，以便能找到目标类
        classPool.appendClassPath("D:\\MyBatisCodeHelper-Pro\\instrumented-MyBatisCodeHelper-Pro241-3.4.2+2321.jar");

        // 从类池中获取指定全限定名的类，返回 CtClass 对象
        CtClass ctClass = classPool.get("com.ccnode.codegenerator.ag.f.e");

        // 从获取的类中查找名为 "a" 且参数类型为 java.lang.String 的方法
        CtMethod parseLicenseMethod = ctClass.getDeclaredMethod("a", new CtClass[]{classPool.get("java.lang.String")});

        // 构建新的方法体代码，创建 Gson 对象，将传入的 JSON 字符串解析为指定类型的对象并返回
        String body = "{" +
                "  com.google.gson.Gson gson = new com.google.gson.Gson();" +
                "  com.ccnode.codegenerator.ag.d.f e = (com.ccnode.codegenerator.ag.d.f)gson.fromJson($1,com.ccnode.codegenerator.ag.d.f.class);" +
                "  return e;" +
                "}";

        // 将构建好的新方法体设置到目标方法中
        parseLicenseMethod.setBody(body);

        // 将修改后的类以字节码形式写入指定目录
        ctClass.writeFile("D:\\MyBatisCodeHelper-Pro");

        System.out.println("字节码修改完成！");
    }
}