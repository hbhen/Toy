package toy.android.com.toy.utils;//package com.tongyuan.android.zhiquleyuan.utils;
//import android.content.Context;
//import android.content.SharedPreferences;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.Map;
//
//public class SPUtils {
//    /**
//     * 保存在手机里面的文件名
//     */
//    public static final String FILE_NAME = "dataaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
//
//    /**
//     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
//     *
//     * @param context
//     * @param key
//     * @param object
//     */
//    public static void put(Context context, String key, Object object) {
//
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//
//        if (object instanceof String) {
//            editor.putString(key, (String) object);
//        } else if (object instanceof Integer) {
//            editor.putInt(key, (Integer) object);
//        } else if (object instanceof Boolean) {
//            editor.putBoolean(key, (Boolean) object);
//        } else if (object instanceof Float) {
//            editor.putFloat(key, (Float) object);
//        } else if (object instanceof Long) {
//            editor.putLong(key, (Long) object);
//        } else {
//            editor.putString(key, object.toString());
//        }
//
//        SharedPreferencesCompat.apply(editor);
//    }
//
//    /**
//     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
//     *
//     * @param context
//     * @param key
//     * @param defaultObject
//     * @return
//     */
//    public static Object get(Context context, String key, Object defaultObject) {
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE);
//
//        if (defaultObject instanceof String)
//        {
//            return sp.getString(key, (String) defaultObject);
//        } else if (defaultObject instanceof Integer)
//        {
//            return sp.getInt(key, (Integer) defaultObject);
//        } else if (defaultObject instanceof Boolean)
//        {
//            return sp.getBoolean(key, (Boolean) defaultObject);
//        } else if (defaultObject instanceof Float)
//        {
//            return sp.getFloat(key, (Float) defaultObject);
//        } else if (defaultObject instanceof Long)
//        {
//            return sp.getLong(key, (Long) defaultObject);
//        }
//
//        return null;
//    }
//
//    /**
//     * 移除某个key值已经对应的值
//     * @param context
//     * @param key
//     */
//    public static void remove(Context context, String key)
//    {
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.remove(key);
//        SharedPreferencesCompat.apply(editor);
//    }
//
//    /**
//     * 清除所有数据
//     * @param context
//     */
//    public static void clear(Context context)
//    {
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.clear();
//        SharedPreferencesCompat.apply(editor);
//    }
//
//    /**
//     * 查询某个key是否已经存在
//     * @param context
//     * @param key
//     * @return
//     */
//    public static boolean contains(Context context, String key)
//    {
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE);
//        return sp.contains(key);
//    }
//
//    /**
//     * 返回所有的键值对
//     *
//     * @param context
//     * @return
//     */
//    public static Map<String, ?> getAll(Context context)
//    {
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE);
//        return sp.getAll();
//    }
//
//    /**
//     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
//     *
//     * @author zhy
//     *
//     */
//    private static class SharedPreferencesCompat
//    {
//        private static final Method sApplyMethod = findApplyMethod();
//
//        /**
//         * 反射查找apply的方法
//         *
//         * @return
//         */
//        @SuppressWarnings({ "unchecked", "rawtypes" })
//        private static Method findApplyMethod()
//        {
//            try
//            {
//                Class clz = SharedPreferences.Editor.class;
//                return clz.getMethod("apply");
//            } catch (NoSuchMethodException e)
//            {
//            }
//
//            return null;
//        }
//
//        /**
//         * 如果找到则使用apply执行，否则使用commit
//         *
//         * @param editor
//         */
//        public static void apply(SharedPreferences.Editor editor)
//        {
//            try
//            {
//                if (sApplyMethod != null)
//                {
//                    sApplyMethod.invoke(editor);
//                    return;
//                }
//            } catch (IllegalArgumentException e)
//            {
//            } catch (IllegalAccessException e)
//            {
//            } catch (InvocationTargetException e)
//            {
//            }
//            editor.commit();
//        }
//    }
//
//}


import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {
    private static SharedPreferences sp;

    /**
     * 写入boolean变量至sp中
     *
     * @param ctx   上下文环境
     * @param key   存储节点名称
     * @param value 存储节点的值 boolean
     */
    public static void putBoolean(Context ctx, String key, boolean value) {
        //(存储节点文件名称,读写方式)
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).commit();
    }

    /**
     * 读取boolean标示从sp中
     *
     * @param ctx      上下文环境
     * @param key      存储节点名称
     * @param defValue 没有此节点默认值
     * @return 默认值或者此节点读取到的结果
     */
    public static boolean getBoolean(Context ctx, String key, boolean defValue) {
        //(存储节点文件名称,读写方式)
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defValue);
    }

    /**
     * 写入boolean变量至sp中
     *
     * @param ctx   上下文环境
     * @param key   存储节点名称
     * @param value 存储节点的值string
     */
    public static void putString(Context ctx, String key, String value) {
        //(存储节点文件名称,读写方式)
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, value).commit();
    }

    /**
     * 读取boolean标示从sp中
     *
     * @param ctx      上下文环境
     * @param key      存储节点名称
     * @param defValue 没有此节点默认值
     * @return 默认值或者此节点读取到的结果
     */
    public static String getString(Context ctx, String key, String defValue) {
        //(存储节点文件名称,读写方式)
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getString(key, defValue);
    }

    public static void putInt(Context ctx, String key, int value) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key, value).commit();
    }

    public static int getInt(Context ctx, String key, int value) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getInt(key, value);
    }

    /**
     * 从sp中移除指定节点
     *
     * @param ctx 上下文环境
     * @param key 需要移除节点的名称
     */
    public static void remove(Context ctx, String key) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().remove(key).commit();
    }

}
