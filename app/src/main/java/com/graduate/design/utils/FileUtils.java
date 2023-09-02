package com.graduate.design.utils;

import android.os.Build;
import android.util.Log;

/*import com.graduate.design.common.Const;
import com.graduate.design.common.SdPathConst;*/
import com.graduate.design.proto.Common;
import com.graduate.design.proto.FileUpload;
import com.graduate.design.service.EncryptionService;
import com.graduate.design.service.impl.EncryptionServiceImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileUtils {
    private static EncryptionService encryptionService = new EncryptionServiceImpl();

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

    public static List<String> wordSegmentation(String content) {
        /*List<String> words = new ArrayList<>();
        StringBuilder word = new StringBuilder();

        for(int i=0;i<content.length();i++){
            char temp = content.charAt(i);
            if(temp=='\'') temp = '’';
            if((temp>='a' && temp<='z') || (temp>='A' && temp<='Z') || (temp>='0' && temp<='9') || temp=='’'){
                word.append(temp);
            }
            else {
                if(word.length()>0 && !words.contains(word.toString().toLowerCase())){
                    words.add(word.toString().toLowerCase());
                }
                word.delete(0, word.length());
            }
        }
        if(word.length()>0 && !words.contains(word.toString().toLowerCase())) words.add(word.toString().toLowerCase());

        return words;*/

        String res = content.replaceAll("'", "’");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Arrays.stream(res.split(",|\\.|;|!|\\?|:|\\s|\\(|\\)|\\\\\"|”|“|---")).filter(word -> word.length()>0)
                    .distinct().collect(Collectors.toList());
        }
        return null;
    }

    // 文件内容分词函数
    public static List<Common.indexToken> indexList(List<String> words, Long fileId) {
        EncryptionService encryptionService = new EncryptionServiceImpl();

        List<Common.indexToken> res = new ArrayList<>();
        // 转变为同步集合
        /*words = Collections.synchronizedList(words);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            words.parallelStream().forEach(word -> res.add(encryptionService.uploadIndex(fileId, word)));
        }*/
        // 对关键字使用主密钥加密
        for(int i=0;i<words.size();i++){
            // 生成索引令牌
            res.add(encryptionService.uploadIndex(fileId, words.get(i)));
        }
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

    public static String bytes2Base64(byte[] origin){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(origin);
        }
        else {
            return android.util.Base64.encodeToString(origin, android.util.Base64.DEFAULT);
        }
    }

    public static byte[] Base64ToBytes(String origin){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getDecoder().decode(origin);
        }
        else {
            return android.util.Base64.decode(origin, android.util.Base64.DEFAULT);
        }
    }
}
