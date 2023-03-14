package com.graduate.design.activity;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.graduate.design.R;
import com.graduate.design.fragment.DiskFragment;
import com.graduate.design.fragment.MineFragment;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.InitViewUtils;
import com.graduate.design.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class HomeActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private RadioGroup mRadioGroup;
    private RadioButton diskButton, mineButton;  //两个单选按钮
    private List<Fragment> mFragments;   //存放视图
    private int position=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 初始化视图
        InitViewUtils.initView(this);
        // 初始化数据
        initData();
        // 获取组件
        getComponentsById();
        // 设置监听函数
        setListeners();
        // 设置默认情况
        setDefaultFragment();
    }

    private void initData(){
        mFragments = new ArrayList<>();
        mFragments.add(0, new DiskFragment());
        mFragments.add(1, new MineFragment());
    }

    private void getComponentsById(){
        diskButton = findViewById(R.id.rb_disk);
        mineButton = findViewById(R.id.rb_mine);
        mRadioGroup = findViewById(R.id.rg_tab);
    }

    private void setListeners(){
        mRadioGroup.setOnCheckedChangeListener(this);
    }

    private void setDefaultFragment(){
        diskButton.setSelected(true);
        // 设置默认显示第一个
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_layout,mFragments.get(0));
        transaction.commit();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()){
            case R.id.rg_tab:
                changeBottomTab(checkedId);
                break;
            default:
                ToastUtils.showShortToastCenter("错误的元素ID");
        }
    }

    private void changeBottomTab(int checkedId) {
        //获取fragment管理类对象
        FragmentManager fragmentManager = getSupportFragmentManager();
        //拿到fragmentManager的触发器
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (checkedId) {
            case R.id.rb_disk:
                position = 0;
                //调用replace方法，将fragment,替换到fragment_layout这个id所在UI，或者这个控件上面来
                //这是创建replace这个事件，如果想要这个事件执行，需要把这个事件提交给触发器
                //用commit()方法
                fragmentManager.popBackStack(null, 1);
                transaction.replace(R.id.fragment_layout, mFragments.get(0));
                //将所有导航栏设成默认色
                setSelected();
                diskButton.setSelected(true);
                break;
            case R.id.rb_mine:
                position = 1;
                fragmentManager.popBackStack(null, 1);
                transaction.replace(R.id.fragment_layout, mFragments.get(1));
                //将所有导航栏设成默认色
                setSelected();
                mineButton.setSelected(true);
                break;
        }
        //事件的提交
        transaction.commit();
    }

    private void setSelected() {
        diskButton.setSelected(false);
        mineButton.setSelected(false);
    }
}
