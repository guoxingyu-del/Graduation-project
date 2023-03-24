package com.graduate.design.utils;

import android.util.Log;

/*import com.graduate.design.common.Const;
import com.graduate.design.common.SdPathConst;*/
import com.graduate.design.proto.Common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    // 把文件夹放到文件前面
    public static List<Common.Node> putDirBeforeFile(List<Common.Node> subNodes){
        List<Common.Node> dirs = new ArrayList<>();
        List<Common.Node> files = new ArrayList<>();
        List<Common.Node> res = new ArrayList<>();

        for(int i=0;i<subNodes.size();i++){
            Common.Node node = subNodes.get(i);
            if(node.getNodeType()== Common.NodeType.File)
                files.add(node);
            else dirs.add(node);
        }

        res.addAll(dirs);
        res.addAll(files);
        return res;
    }

    // 文件内容分词函数
    public static List<String> indexList(String content) {
        List<String> res = new ArrayList<>();
        StringBuilder word = new StringBuilder();
        for(int i=0;i<content.length();i++){
            char temp = content.charAt(i);
            if((temp>='a' && temp<='z') || (temp>='A' && temp<='Z') || (temp>='0' && temp<='9')){
                word.append(temp);
            }
            else {
                if(word.length()>0){
                    res.add(word.toString());
                    word.delete(0, word.length());
                }
            }
        }
        if(word.length()>0) res.add(word.toString());
        return res;
    }

    // 除去文件名中的换行符
    public static String removeLineBreak(String content){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<content.length();i++){
            if(content.charAt(i)!='\n') sb.append(content.charAt(i));
        }
        return sb.toString();
    }
}
