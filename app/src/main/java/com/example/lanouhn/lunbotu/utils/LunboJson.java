package com.example.lanouhn.lunbotu.utils;


import com.example.lanouhn.lunbotu.contans.lunboImage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * 解析Json数据
 */
public class LunboJson {

    /**
     * @param json json字符串
     * @return 轮播图
     */
    public static List<lunboImage> getlunboList(String json) {
        List<lunboImage> list = new ArrayList<>();
        try {


            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject j = jsonArray.getJSONObject(i);

                lunboImage m = new lunboImage();
                m.setCover(j.getString("cover"));

                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
