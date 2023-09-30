package com.graduate.design.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.graduate.design.R;
import com.graduate.design.activity.HomeActivity;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.ToastUtils;

import java.nio.charset.StandardCharsets;

public class FileContentFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {
    private ImageButton backImageButton;
    private Button backButton;
    private TextView fileTitleView;
    private TextView fileContentView;
    private ImageView picContentView;
  //  private ImageButton shareButton;
    private HomeActivity activity;
    private String fileName;
    private String fileContent;
    private String fileType;

    // 縮放控制
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    // 不同状态的表示：
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    // 定义第一个按下的点，两只接触点的重点，以及出事的两指按下的距离：
    private PointF startPoint = new PointF();
    private PointF midPoint = new PointF();
    private float oriDis = 1f;

    private static long lastClickTime; //点击时间 hwb001

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
        fileType = getArguments().getString("fileType");
    }

    private void getComponentsById(View view){
        fileTitleView = view.findViewById(R.id.file_title);
        fileContentView = view.findViewById(R.id.file_content);
        picContentView = view.findViewById(R.id.pic_content);
        backImageButton = view.findViewById(R.id.back_image_btn);
        backButton = view.findViewById(R.id.back_btn);
       // shareButton = view.findViewById(R.id.share_btn);
    }

    private void setListeners(){
        backImageButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        picContentView.setOnTouchListener(this);
       // shareButton.setOnClickListener(this);
    }

    private void setFileNameAndContent(){
        // 从disk页面拿到点击的文件名称和内容
        if("txt".equals(fileType)) {
            fileTitleView.setText(fileName);
            fileContentView.setText(fileContent);
        }
        else if("jpg".equals(fileType) || "png".equals(fileType)){
            fileTitleView.setVisibility(View.GONE);
            byte[] content = fileContent.getBytes(StandardCharsets.ISO_8859_1);
            Bitmap bitmap = BitmapFactory.decodeByteArray(content, 0, content.length);
            picContentView.setImageBitmap(bitmap);
            picContentView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
            case R.id.back_image_btn:
                goBackDisk();
                break;
            default:
                ToastUtils.showShortToastCenter("错误的页面元素ID");
                break;
        }
    }

    private void goBackDisk(){
        activity.getSupportFragmentManager().popBackStack();
    }

    // 计算两个触摸点之间的距离
    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return Float.valueOf(String.valueOf(Math.sqrt(x * x + y * y))) ;
    }

    // 计算两个触摸点的中点
    private PointF middle(MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        return new PointF(x / 2, y / 2);
    }

    // 处理图片缩放事件
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        ImageView imgView = (ImageView) view;
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            //双击
            case MotionEvent.ACTION_UP:
                //点击整个页面都会让内容框获得焦点，且弹出软键盘
                if(isFastDoubleThreeClick()) {
                    matrix.reset();
                }else{
                    mode = NONE;
                }
                break;
            // 单指
            case MotionEvent.ACTION_DOWN:
                matrix.set(imgView.getImageMatrix());
                savedMatrix.set(matrix);
                startPoint.set(motionEvent.getX(), motionEvent.getY());
                mode = DRAG;
                break;
            // 双指
            case MotionEvent.ACTION_POINTER_DOWN:
                oriDis = distance(motionEvent);
                if (oriDis > 10f) {
                    savedMatrix.set(matrix);
                    midPoint = middle(motionEvent);
                    mode = ZOOM;
                }
                break;
            // 手指放开
            // case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            // 单指滑动事件
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    // 是一个手指拖动
                    matrix.set(savedMatrix);
                    matrix.postTranslate(motionEvent.getX() - startPoint.x, motionEvent.getY() - startPoint.y);
                } else if (mode == ZOOM) {
                    // 两个手指滑动
                    float newDist = distance(motionEvent);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oriDis;
                        matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                    }
                }
                break;
        }
        // 设置ImageView的Matrix
        imgView.setImageMatrix(matrix);
        return true;
    }

    /**
     * 防止过快点击(200毫秒)
     * @return
     */
    private static boolean isFastDoubleThreeClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( 0 < timeD && timeD < 200) {    //这样所有按钮在200毫秒内不能同时起效。
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
