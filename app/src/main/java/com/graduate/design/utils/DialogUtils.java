package com.graduate.design.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;


import com.graduate.design.R;
import com.graduate.design.service.UserService;


public class DialogUtils {
    private static Dialog dialog;
    private static View inflate;


    //中间显示的dialog
    public static void showDialog(Context context, UserService userService, Long nodeId, String token) {
        //自定义dialog显示布局
        inflate = LayoutInflater.from(context).inflate(R.layout.activity_dialog, null);
        //自定义dialog显示风格
        dialog = new Dialog(context, R.style.DialogStyle);
        dialog.setContentView(inflate);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);
        dialog.show();

        EditText createDirName = inflate.findViewById(R.id.create_dir_name);
        Button dialogCancelButton = inflate.findViewById(R.id.dialog_cancel);
        Button dialogOKButton = inflate.findViewById(R.id.dialog_ok);

        // 点击了ok按钮
        dialogOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDialog();
                String dirName = createDirName.getText().toString();
                int res = userService.createDir(dirName, nodeId, token);
                if(res==0) ToastUtils.showShortToastCenter("添加文件夹成功");
                else ToastUtils.showShortToastCenter("添加文件夹失败");
                // 更新页面, 未实现 TODO
            }
        });
        // 点击了取消按钮
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDialog();
            }
        });
    }

    //关闭dialog时调用
    public static void closeDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}

