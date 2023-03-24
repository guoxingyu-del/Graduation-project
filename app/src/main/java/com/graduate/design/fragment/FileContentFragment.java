package com.graduate.design.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.graduate.design.R;
import com.graduate.design.activity.HomeActivity;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.ToastUtils;

import java.nio.charset.StandardCharsets;

public class FileContentFragment extends Fragment implements View.OnClickListener {
    private ImageButton backImageButton;
    private Button backButton;
    private TextView fileTitleView;
    private TextView fileContentView;
    private ImageButton shareButton;
    private HomeActivity activity;
    private String fileName;
    private String fileContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化数据
        initData();
        // 拿到页面元素
        getComponentsById(view);
        // 设置监听事件
        setListeners();
        // 设置文件名称和文件内容
        setFileNameAndContent();
    }

    private void initData(){
        activity = (HomeActivity) getActivity();
        fileName = getArguments().getString("fileName");
        fileContent = getArguments().getString("fileContent");
    }

    private void getComponentsById(View view){
        fileTitleView = view.findViewById(R.id.file_title);
        fileContentView = view.findViewById(R.id.file_content);
        backImageButton = view.findViewById(R.id.back_image_btn);
        backButton = view.findViewById(R.id.back_btn);
        shareButton = view.findViewById(R.id.share_btn);
    }

    private void setListeners(){
        backImageButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
    }

    private void setFileNameAndContent(){
        // 从disk页面拿到点击的文件名称和内容
        fileTitleView.setText(fileName);
        fileContentView.setText(fileContent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
            case R.id.back_image_btn:
                goBackDisk();
                break;
            case R.id.share_btn:
                shareFile();
                break;
            default:
                ToastUtils.showShortToastCenter("错误的页面元素ID");
                break;
        }
    }

    private void goBackDisk(){
        activity.getSupportFragmentManager().popBackStack();
    }

    private void shareFile(){
        ToastUtils.showShortToastCenter("点击了分享按钮");
    }
}
