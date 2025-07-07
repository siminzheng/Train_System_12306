package com.jiawa.train.generator.gen;


import cn.hutool.core.util.StrUtil;
import com.jiawa.train.business.enums.ConfirmOrderStatusEnum;
import com.jiawa.train.business.enums.SeatColEnum;
import com.jiawa.train.business.enums.SeatTypeEnum;
import com.jiawa.train.business.enums.TrainTypeEnum;
import com.jiawa.train.member.enums.PassengerTypeEnum;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;





public class EnumGenerator {
    //static String path = "web/src/assets/js/enums.js";
    static String path = "admin/src/assets/js/enums.js";


    public static void main(String[] args) {
        StringBuffer bufferObject = new StringBuffer();
        StringBuffer bufferArray = new StringBuffer();
        long begin = System.currentTimeMillis();
        try {
            toJson(PassengerTypeEnum.class, bufferObject, bufferArray);
            toJson(TrainTypeEnum.class, bufferObject, bufferArray);
            toJson(SeatTypeEnum.class, bufferObject, bufferArray);
            toJson(SeatColEnum.class, bufferObject, bufferArray);
            toJson(ConfirmOrderStatusEnum.class, bufferObject, bufferArray);

            StringBuffer buffer = bufferObject.append("\r\n").append(bufferArray);
            writeJs(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("执行耗时:" + (end - begin) + " 毫秒");
    }

    private static void toJson(Class clazz, StringBuffer bufferObject, StringBuffer bufferArray) throws Exception {
        // enumConst：将YesNoEnum变成YES_NO
        String enumConst = StrUtil.toUnderlineCase(clazz.getSimpleName())
                .toUpperCase().replace("_ENUM", "");
        Object[] objects = clazz.getEnumConstants();
        Method name = clazz.getMethod("name");


        // 排除枚举属性和$VALUES，只获取code desc等
        List<Field> targetFields = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isPrivate(field.getModifiers()) || "$VALUES".equals(field.getName())) {
                continue;
            }
            targetFields.add(field);
        }

        // 生成对象
        bufferObject.append(enumConst).append("={");
        for (int i = 0; i < objects.length; i++) {
            Object obj = objects[i];
            bufferObject.append(name.invoke(obj)).append(":");

            // 将一个枚举值转成JSON对象字符串
            formatJsonObj(bufferObject, targetFields, clazz, obj);

            if (i < objects.length - 1) {
                bufferObject.append(",");
            }
        }
        bufferObject.append("};\r\n");

        // 生成数组
        bufferArray.append(enumConst).append("_ARRAY=[");
        for (int i = 0; i < objects.length; i++) {
            Object obj = objects[i];

            // 将一个枚举值转成JSON对象字符串
            formatJsonObj(bufferArray, targetFields, clazz, obj);

            if (i < objects.length - 1) {
                bufferArray.append(",");
            }
        }
        bufferArray.append("];\r\n");
    }

    /**
     * 将一个枚举值转成JSON对象字符串
     * 比如：SeatColEnum.YDZ_A("A", "A", "1")
     * 转成：{code:"A",desc:"A",type:"1"}
     */
    private static void formatJsonObj(StringBuffer bufferObject, List<Field> targetFields, Class clazz, Object obj) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        bufferObject.append("{");
        for (int j = 0; j < targetFields.size(); j++) {
            Field field = targetFields.get(j);
            String fieldName = field.getName();
            String getMethod = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            bufferObject.append(fieldName).append(":\"").append(clazz.getMethod(getMethod).invoke(obj)).append("\"");
            if (j < targetFields.size() - 1) {
                bufferObject.append(",");
            }
        }
        bufferObject.append("}");
    }

    /**
     * 写文件
     * @param stringBuffer
     */
    public static void writeJs(StringBuffer stringBuffer) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
            System.out.println(path);
            osw.write(stringBuffer.toString());
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
