package com.zaaach.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zaaach.linematchingview.LineMatchingView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LineMatchingView<ItemInfo> lineMatchingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineMatchingView = findViewById(R.id.line_matching_view);
        Button btnRetry = findViewById(R.id.btn_retry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lineMatchingView.isFinished()){
                    lineMatchingView.restore();
                }else {
                    Toast.makeText(MainActivity.this, "连线未完成", Toast.LENGTH_SHORT).show();
                }
            }
        });

        List<ItemInfo> left = new ArrayList<>();
        left.add(new ItemInfo(ItemInfo.TEXT, "草莓"));
        left.add(new ItemInfo(ItemInfo.TEXT, "苹果"));
        left.add(new ItemInfo(ItemInfo.TEXT, "香蕉"));
        left.add(new ItemInfo(ItemInfo.TEXT, "西瓜"));
        left.add(new ItemInfo(ItemInfo.TEXT, "梨"));

        List<ItemInfo> right = new ArrayList<>();
        right.add(new ItemInfo(ItemInfo.IMAGE, "http://...jpg", "香蕉"));
        right.add(new ItemInfo(ItemInfo.IMAGE, "http://...jpg", "梨"));
        right.add(new ItemInfo(ItemInfo.IMAGE, "http://...jpg", "草莓"));
        right.add(new ItemInfo(ItemInfo.IMAGE, "http://...jpg", "苹果"));
        right.add(new ItemInfo(ItemInfo.IMAGE, "http://...jpg", "西瓜"));

        int[] icons = {
                R.mipmap.xiangjiao,
                R.mipmap.li,
                R.mipmap.caomei,
                R.mipmap.pingguo,
                R.mipmap.xigua
        };

        lineMatchingView.init(new LineMatchingView.LinkableAdapter<ItemInfo>() {
            @Override
            public View getView(ItemInfo item, ViewGroup parent, int itemType, int position) {
                View view = LayoutInflater.from(parent.getContext()).inflate(
                        itemType == ItemInfo.TEXT ? R.layout.item_text : R.layout.item_image, null);
                return view;
            }

            @Override
            public int getItemType(ItemInfo item, int position) {
                return item.type;
            }

            @Override
            public void onBindView(ItemInfo item, View view, int position) {
                if (item.type == ItemInfo.TEXT){
                    TextView tv = view.findViewById(R.id.tv_text);
                    tv.setText(item.content);
                }else {
                    //图片暂时用本地的代替
                    ImageView pic = view.findViewById(R.id.iv_pic);
                    pic.setImageResource(icons[position]);
                }
            }

            @Override
            public void onItemStateChanged(ItemInfo item, View view, int state, int position) {
                switch (state){
                    case LineMatchingView.NORMAL:
                        view.setBackgroundResource(R.drawable.item_bg_normal);
                        break;
                    case LineMatchingView.CHECKED:
                        view.setBackgroundResource(R.drawable.item_bg_checked);
                        break;
                    case LineMatchingView.LINED:
                        view.setBackgroundResource(R.drawable.item_bg_lined);
                        break;
                    case LineMatchingView.CORRECT:
                        view.setBackgroundResource(R.drawable.item_bg_correct);
                        break;
                    case LineMatchingView.ERROR:
                        view.setBackgroundResource(R.drawable.item_bg_error);
                        break;
                }
            }

            @Override
            public boolean isCorrect(ItemInfo left, ItemInfo right, int l, int r) {
                return TextUtils.equals(left.desc, right.desc);
            }
        }).setItems(left, right);
    }
}